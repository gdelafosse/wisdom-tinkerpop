package org.wisdom.tinkerpop;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.tinkerpop.util.DictionnaryToMap;

/**
 * A ManagedServiceFactory that registers TinkerPop Graph as a service.
 */
class TinkerPopGraphManagedServiceFactory implements ManagedServiceFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerPopGraphManagedServiceFactory.class);

    private final BundleContext bundleContext;

    private final Class<?> factoryClass;

    private final Map<String, ServiceRegistration<Graph>> registeredServices = new ConcurrentHashMap<>();

    public TinkerPopGraphManagedServiceFactory(BundleContext bundleContext, Class<?> factoryClass)
    {
        this.bundleContext = bundleContext;
        this.factoryClass = factoryClass;
    }

    @Override
    public String getName()
    {
        return "Wisdom TinkerPop Graph Managed Service Factory";
    }

    @Override
    public void updated(final String pid, final Dictionary dictionary) throws ConfigurationException
    {
        LOGGER.info("Registering new graph {} [{}]", pid, factoryClass.getName());

        // if the service is already registered, unregister it before updating it
        if (registeredServices.containsKey(pid))
        {
            unregisterGraph(pid, registeredServices.get(pid));
        }

        ServiceRegistration<Graph> sr = registerGraph(pid, dictionary);
        registeredServices.put(pid, sr);
    }

    @Override
    public void deleted(final String pid)
    {
        LOGGER.info("Unregistering graph {}", pid);

        ServiceRegistration<Graph> sr = registeredServices.remove(pid);
        unregisterGraph(pid, sr);
    }

    private ServiceRegistration<Graph> registerGraph(final String pid, final Dictionary dictionary) throws ConfigurationException
    {
        try
        {
            // if there's no graphId specify, use the pid
            if (dictionary.get(WisdomTinkerPopConstants.GRAPH_ID) == null)
            {
                dictionary.put(WisdomTinkerPopConstants.GRAPH_ID, pid);
            }

            Graph graph = open(new MapConfiguration(new DictionnaryToMap<String, Object>(dictionary)), factoryClass);
            return bundleContext.registerService(Graph.class, graph, dictionary);
        }
        catch (TinkerPopGraphException e)
        {
            LOGGER.error("Unable to create graph {} [{}]", pid, factoryClass.getName());
            LOGGER.error(e.getMessage(), e);
            throw new ConfigurationException(Graph.GRAPH, e.getMessage(), e);
        }
    }

    private void unregisterGraph(final String pid, final ServiceRegistration<Graph> registeredService)
    {
        if (registeredService != null)
        {
            ServiceReference<Graph> service = registeredService.getReference();
            if (service != null)
            {
                Graph graph = bundleContext.getService(service);
                if (graph != null)
                {
                    try
                    {
                        graph.close();
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Fail to close graph {}", pid);
                    }
                }
            }
            registeredService.unregister();
        }
    }

    /*
     * Copied from org.apache.tinkerpop.gremlin.structure.util.GraphFactory
     */
    private static Graph open(final Configuration configuration, final Class<?> graphFactoryClass) throws TinkerPopGraphException
    {
        final Graph g;
        try
        {
            // will use open(Configuration c) to instantiate
            g = (Graph) graphFactoryClass.getMethod("open", Configuration.class).invoke(null, configuration);
        }
        catch (final NoSuchMethodException e1)
        {
            throw new TinkerPopGraphException(String.format("GraphFactory can only instantiate Graph implementations from classes that have a static open() method that takes a single Apache Commons Configuration argument - [%s] does not seem to have one", graphFactoryClass));
        }
        catch (final Exception e2)
        {
            throw new TinkerPopGraphException(String.format("GraphFactory could not instantiate this Graph implementation [%s]", graphFactoryClass), e2);
        }
        return g;
    }

    public void close()
    {
        registeredServices.keySet().parallelStream().forEach(k -> unregisterGraph(k, registeredServices.get(k)));
        registeredServices.clear();
    }
}

