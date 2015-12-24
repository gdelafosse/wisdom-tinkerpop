package org.wisdom.tinkerpop;


import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Defines in an interface the TinkerPop GraphFactory sevices.
 */
public interface TinkerPopGraphFactory
{
    public static final String GRAPH_NAME = "wisdom.tinkerpop.name";

    /**
     * Open a graph.  See each {@link Graph} instance for its configuration options.
     *
     * @param configuration A configuration object that specifies the minimally required properties for a                        I
     *                      {@link Graph} instance. This minimum is determined by the
     *                      {@link Graph} instance itself.
     * @return A {@link org.apache.tinkerpop.gremlin.structure.Graph} instance.
     * @throws TinkerPopGraphException if {@code configuration} is not correct
     */
    Graph open(Map<String, String> configuration) throws TinkerPopGraphException;
}
