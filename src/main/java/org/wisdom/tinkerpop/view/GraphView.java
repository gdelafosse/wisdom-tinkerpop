package org.wisdom.tinkerpop.view;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.wisdom.tinkerpop.TinkerPopGraphManagedServiceFactory;

/**
 * A view of the Graph
 */
public class GraphView
{
    private Graph graph;

    public GraphView(Graph graph)
    {
        this.graph = graph;
    }

    public String getName()
    {
        return this.graph.configuration().getString(TinkerPopGraphManagedServiceFactory.GRAPH);
    }

    public long getEdgeCount()
    {
        return IteratorUtils.count(graph.edges());
    }

    public long getVertexCount()
    {
        return IteratorUtils.count(graph.vertices());
    }

}
