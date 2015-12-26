package org.wisdom.tinkerpop.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.wisdom.tinkerpop.WisdomTinkerPopConstants;

/**
 * A view of the Graph
 */
class GraphView
{
    private Graph graph;

    public GraphView(Graph graph)
    {
        this.graph = graph;
    }

    public String getName()
    {
        return this.graph.configuration().getString(WisdomTinkerPopConstants.GRAPH_ID);
    }

    public long getEdgeCount()
    {
        return IteratorUtils.count(graph.edges());
    }

    public long getVertexCount()
    {
        return IteratorUtils.count(graph.vertices());
    }

    public Map<Object, Object> getConfiguration()
    {
        return ConfigurationConverter.getMap(graph.configuration());
    }

    public Map<String, Boolean> getGraphFeatures()
    {
        Graph.Features.GraphFeatures features = graph.features().graph();
        Map<String, Boolean> graphFeatures = new HashMap<>();
        graphFeatures.put(Graph.Features.GraphFeatures.FEATURE_COMPUTER, features.supportsComputer());
        graphFeatures.put(Graph.Features.GraphFeatures.FEATURE_CONCURRENT_ACCESS, features.supportsConcurrentAccess());
        graphFeatures.put(Graph.Features.GraphFeatures.FEATURE_PERSISTENCE, features.supportsPersistence());
        graphFeatures.put(Graph.Features.GraphFeatures.FEATURE_THREADED_TRANSACTIONS, features.supportsThreadedTransactions());
        graphFeatures.put(Graph.Features.GraphFeatures.FEATURE_TRANSACTIONS, features.supportsTransactions());

        return graphFeatures;
    }
}
