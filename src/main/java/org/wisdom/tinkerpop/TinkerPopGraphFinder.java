package org.wisdom.tinkerpop;

/**
 * Defines a service that finds the Tinkerpop providers graph.
 */
public interface TinkerPopGraphFinder
{
    /**
     * Finds and loads the class from the bundle that contains it.
     * @param graphClassName
     * @return the loaded class
     */
    Class<?> findGraphFactory(final String graphClassName);
}
