package org.wisdom.tinkerpop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.tinkerpop.util.BundleScanner;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@Component
@Provides(specifications = TinkerPopGraphFinder.class)
@Instantiate
class TinkerPopBundleTracker implements TinkerPopGraphFinder, BundleTrackerCustomizer<Set<Class<?>>>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerPopBundleTracker.class);

    private final BundleContext bundleContext;

    private BundleTracker<Set<Class<?>>> bundleTracker;

    private Lock lock = new ReentrantLock();

    private Map<Class<?>, Bundle> bundlesPerClasses = new HashMap<>();

    private Multimap<Bundle, Class<?>> graphClassesPerBundle = LinkedListMultimap.create();

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
            bundlesPerClasses.clear();
            graphClassesPerBundle.clear();

            if (bundleTracker != null)
            {
                bundleTracker.close();
            }
            LOGGER.debug("TinkerPopBundleTracker invalidate");
        }
        finally
        {
            lock.unlock();
        }
    }

    public Class<?> findGraphFactory(final String graphClassName)
    {
        try
        {
            lock.lock();
            return bundlesPerClasses.keySet().stream().filter(c -> StringUtils.equals(graphClassName, c.getName())).findFirst().get();
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
            Set<Class<?>> classes = BundleScanner.bundle(bundle).scan(predicate);
            if (!classes.isEmpty())
            {
                classes.stream().forEach(c -> {bundlesPerClasses.put(c, bundle); LOGGER.info("{} from {} is a GraphFactory.", c.getName(), bundle.getSymbolicName());});
                graphClassesPerBundle.putAll(bundle, classes);
                LOGGER.debug("{} GraphFactory found in bundle {}", classes.size(), bundle.getSymbolicName());

                return classes;
            }
            else
            {
                LOGGER.debug("No GraphFactory found in bundle {}", bundle.getSymbolicName());
                return Collections.emptySet();
            }
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
            object.stream().forEach(c -> bundlesPerClasses.remove(c));
            graphClassesPerBundle.removeAll(bundle);
        }
        finally
        {
            lock.unlock();
        }
    }
}
