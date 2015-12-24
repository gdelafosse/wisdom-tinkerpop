package org.wisdom.tinkerpop;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceController;
import org.apache.felix.ipojo.annotations.Updated;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.tinkerpop.util.DictionnaryToMap;

import com.google.common.base.Preconditions;

/**
 * Publishes a TinkerPop Graph as a IPojo Component that others can require.
 * Configured exactly with the same configuration that TinkerPop GraphFactory receives.
 */
@Component(name = "org.wisdom.tinkerpop")
@Provides(specifications = Graph.class)
@Instantiate
class IPojoGraphProvider implements Graph
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IPojoGraphProvider.class);

    private Graph graph;

    @Requires
    private TinkerPopGraphFactory factory;

    @ServiceController
    private boolean valid = false;

    private String graphId;

    @Validate
    public void validate()
    {
        LOGGER.debug("IPojoGraphProvider {} validated", graphId);
    }

    @Invalidate
    public void invalidate() throws Exception
    {
        LOGGER.debug("Invalidating IPojoGraphProvider {}", graphId);
        closeGraph();
        LOGGER.debug("IPojoGraphProvider {} invalidated", graphId);
    }

    @Updated
    public void updated(Dictionary<String, String> configuration) throws Exception
    {
        graphId = configuration.get(TinkerPopGraphFactory.GRAPH_NAME);
        LOGGER.debug("Udpating IPojoGraphProvider {}", graphId);
        closeGraph();
        createGraph(new DictionnaryToMap(configuration));
        LOGGER.debug("IPojoGraphProvider {} updated", graphId);
    }

    public Vertex addVertex(final Object... keyValues)
    {
        checkGraphNotNull();
        return graph.addVertex(keyValues);
    }

    public <C extends GraphComputer> C compute(final Class<C> graphComputerClass) throws IllegalArgumentException
    {
        checkGraphNotNull();
        return graph.compute(graphComputerClass);
    }

    public GraphComputer compute() throws IllegalArgumentException
    {
        checkGraphNotNull();
        return graph.compute();
    }

    public Iterator<Vertex> vertices(final Object... vertexIds)
    {
        checkGraphNotNull();
        return graph.vertices(vertexIds);
    }

    public Iterator<Edge> edges(final Object... edgeIds)
    {
        checkGraphNotNull();
        return graph.edges(edgeIds);
    }

    public Transaction tx()
    {
        checkGraphNotNull();
        return graph.tx();
    }

    public void close() throws Exception
    {
        checkGraphNotNull();
        graph.close();
    }

    public Variables variables()
    {
        checkGraphNotNull();
        return graph.variables();
    }

    public Configuration configuration()
    {
        checkGraphNotNull();
        return graph.configuration();
    }

    private void createGraph(Map<String, String> configuration) throws TinkerPopGraphException
    {
        LOGGER.debug("Creating graph {}", graphId);
        graph = factory.open(configuration);
        LOGGER.debug("Graph {} created", graphId);
        valid = true;
    }

    private void closeGraph() throws Exception
    {
        valid = false;
        if (graph != null)
        {
            try
            {
                LOGGER.debug("Closing graph {}", graphId);
                graph.close();
            }
            catch (Exception e)
            {
                LOGGER.warn("Unable to close graph {}.", graph);
                LOGGER.debug(e.getMessage(), e);
                throw e;
            }
        }
    }

    private void checkGraphNotNull()
    {
        Preconditions.checkNotNull(graph, "The TinkerPop graph {} is not instanciated", graphId);
    }
}
