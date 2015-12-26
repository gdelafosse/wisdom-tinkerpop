package org.wisdom.tinkerpop;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.tinkerpop.util.BundleScanner;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@Component
@Instantiate
class TinkerPopBundleTracker implements BundleTrackerCustomizer<Set<Class<?>>>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerPopBundleTracker.class);

    private final BundleContext bundleContext;

    private BundleTracker<Set<Class<?>>> bundleTracker;

    private Lock lock = new ReentrantLock();

    private Multimap<Bundle, ServiceRegistration<ManagedServiceFactory>> registeredFactories = LinkedListMultimap.create();

    private final TinkerPopGraphFactoryPredicate predicate = new TinkerPopGraphFactoryPredicate();

    public TinkerPopBundleTracker(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Validate
    public void validate()
    {
        LOGGER.debug("Validating TinkerPopBundleTracker");
        bundleTracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, this);
        bundleTracker.open();
        LOGGER.debug("TinkerPopBundleTracker invalidated");

    }

    @Invalidate
    public void invalidate()
    {
        try
        {
            lock.lock();

            LOGGER.debug("Invalidating TinkerPopBundleTracker");
            unregisterAllFactories();
            stopTracker();
            LOGGER.debug("TinkerPopBundleTracker invalidate");
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Set<Class<?>> addingBundle(final Bundle bundle, final BundleEvent event)
    {
        try
        {
            lock.lock();

            LOGGER.debug("Adding bundle {}", bundle.getSymbolicName());

            Set<Class<?>> classes = Collections.emptySet();
            if (isBundleWiredToTinkerPop(bundle))
            {
                classes = findAndRegisterGraphFactories(bundle);
            }
            return classes;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void modifiedBundle(final Bundle bundle, final BundleEvent event, final Set<Class<?>> object)
    {

    }

    @Override
    public void removedBundle(final Bundle bundle, final BundleEvent event, final Set<Class<?>> object)
    {
        try
        {
            lock.lock();
            LOGGER.debug("Removing bundle {}", bundle.getSymbolicName());
            unregisterBundleFactories(bundle);
        }
        finally
        {
            lock.unlock();
        }
    }

    private Set<Class<?>> findAndRegisterGraphFactories(Bundle bundle)
    {
        Set<Class<?>> classes = BundleScanner.bundle(bundle).scan(predicate);
        LOGGER.debug("{} GraphFactory found in bundle {}", classes.size(), bundle.getSymbolicName());
        if (!classes.isEmpty())
        {
            classes.stream().forEach(c -> {
                LOGGER.info("{} from {} is a GraphFactory.", c.getName(), bundle.getSymbolicName());
                registerFactory(bundle, c);
            });

        }
        else
        {
            LOGGER.debug("No GraphFactory found in bundle {}", bundle.getSymbolicName());
        }
        return Collections.emptySet();
    }

    private boolean isBundleWiredToTinkerPop(final Bundle bundle)
    {
        try
        {
            bundle.loadClass(Graph.class.getName());
            return true;
        }
        catch (ClassNotFoundException | IllegalStateException | NoClassDefFoundError e)
        {
            return false;
        }
    }

    private void registerFactory(final Bundle bundle, final Class<?> c)
    {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("service.pid", c.getName());
        ServiceRegistration<ManagedServiceFactory> registeredFactory = bundleContext.registerService(ManagedServiceFactory.class, new TinkerPopGraphManagedServiceFactory(bundleContext, c), properties);
        registeredFactories.put(bundle, registeredFactory);
    }

    private void unregisterBundleFactories(final Bundle bundle)
    {
        Collection<ServiceRegistration<ManagedServiceFactory>> factories = registeredFactories.removeAll(bundle);
        unregisterFactories(factories);
    }

    private void unregisterFactories(Collection<ServiceRegistration<ManagedServiceFactory>> factories)
    {
        factories.stream().forEach(f -> { ((TinkerPopGraphManagedServiceFactory)bundleContext.getService(f.getReference())).close();f.unregister();});
    }

    private void unregisterAllFactories()
    {
        registeredFactories.keys().stream()
                .map(b -> registeredFactories.get(b))
                .forEach(fs -> unregisterFactories(fs));
        registeredFactories.clear();
    }

    private void stopTracker()
    {
        if (bundleTracker != null)
        {
            bundleTracker.close();
        }
    }
}
