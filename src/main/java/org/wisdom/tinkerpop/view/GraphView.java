package org.wisdom.tinkerpop.view;

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
}
