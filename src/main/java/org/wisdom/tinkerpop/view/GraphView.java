package org.wisdom.tinkerpop.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONWriter;
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

    public String getGraphSON()
    {
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GraphSONWriter.build().wrapAdjacencyList(true).create().writeGraph(os, graph);
            return new String(os.toByteArray(), "UTF8");
        }
        catch (IOException e)
        {
            return e.getMessage();
        }
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

    public Map<String, Boolean> getEdgeFeatures()
    {
        Graph.Features.EdgeFeatures features = graph.features().edge();
        Map<String, Boolean> edgeFeatures = new HashMap<>();
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_ADD_EDGES, features.supportsAddEdges());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_ADD_PROPERTY, features.supportsAddProperty());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_ANY_IDS, features.supportsAnyIds());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_CUSTOM_IDS, features.supportsCustomIds());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_NUMERIC_IDS, features.supportsNumericIds());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_REMOVE_EDGES, features.supportsRemoveEdges());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_REMOVE_PROPERTY, features.supportsRemoveProperty());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_STRING_IDS, features.supportsStringIds());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_USER_SUPPLIED_IDS, features.supportsUserSuppliedIds());
        edgeFeatures.put(Graph.Features.EdgeFeatures.FEATURE_UUID_IDS, features.supportsUuidIds());

        return edgeFeatures;
    }

    public Map<String, Boolean> getVertexFeatures()
    {
        Graph.Features.VertexFeatures features = graph.features().vertex();
        Map<String, Boolean> vertexFeatures = new HashMap<>();
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_ADD_PROPERTY, features.supportsAddProperty());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_ADD_VERTICES, features.supportsAddVertices());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_ANY_IDS, features.supportsAnyIds());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_CUSTOM_IDS, features.supportsCustomIds());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_META_PROPERTIES, features.supportsMetaProperties());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_MULTI_PROPERTIES, features.supportsMultiProperties());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_NUMERIC_IDS, features.supportsNumericIds());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_REMOVE_PROPERTY, features.supportsRemoveProperty());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_REMOVE_VERTICES, features.supportsRemoveVertices());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_STRING_IDS, features.supportsStringIds());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_USER_SUPPLIED_IDS, features.supportsUserSuppliedIds());
        vertexFeatures.put(Graph.Features.VertexFeatures.FEATURE_UUID_IDS, features.supportsUuidIds());
        return vertexFeatures;
    }
}
