package org.wisdom.tinkerpop;

import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a TinkerPop GraphFactory thanks to IPojo
 */
@Component
@Provides
@Instantiate
class IPojoGraphFactory implements TinkerPopGraphFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IPojoGraphFactory.class);

    @Requires
    private TinkerPopGraphFinder scanner;

    public Graph open(final Map<String, String> configuration) throws TinkerPopGraphException
    {
        String graphClassName = configuration.get(Graph.GRAPH);
        if (StringUtils.isBlank(graphClassName))
        {
            String message = String.format("Configuration must contain a valid %s setting.", Graph.GRAPH);
            LOGGER.error(message);
            throw new TinkerPopGraphException(message);
        }

        Class<?> graphClass = scanner.findGraphFactory(graphClassName);
        if (graphClass == null)
        {
            String message = String.format("GraphFactory could not findGraphFactory [{}] - Ensure that the provider bundle is installed.", graphClassName);
            LOGGER.error(message);
            throw new TinkerPopGraphException(message);
        }

        return open(new MapConfiguration(configuration), graphClass);
    }

    /*
     * Copied from org.apache.tinkerpop.gremlin.structure.util.GraphFactory
     */
    private static Graph open(final Configuration configuration, final Class<?> graphFactoryClass) throws TinkerPopGraphException
    {
        final Graph g;
        try {
            // will use open(Configuration c) to instantiate
            g = (Graph) graphFactoryClass.getMethod("open", Configuration.class).invoke(null, configuration);
        } catch (final NoSuchMethodException e1) {
            throw new TinkerPopGraphException(String.format("GraphFactory can only instantiate Graph implementations from classes that have a static open() method that takes a single Apache Commons Configuration argument - [%s] does not seem to have one", graphFactoryClass));
        } catch (final Exception e2) {
            throw new TinkerPopGraphException(String.format("GraphFactory could not instantiate this Graph implementation [%s]", graphFactoryClass), e2);
        }
        return g;
    }
}
