package org.wisdom.tinkerpop;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TinkerPopGraphFactoryPredicateTest
{
    private TinkerPopGraphFactoryPredicate cut = new TinkerPopGraphFactoryPredicate();

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {ExactSignature.class, true},
                {WithGraphImplementation.class, false},
                {BadParameter.class, false},
                {BadResult.class, false}});
    }

    @Parameter
    public Class<?> input;

    @Parameter(1)
    public boolean expected;


    @Test
    public void testApply() throws Exception
    {
        assertEquals(expected, cut.apply(input));
    }

    public static class ExactSignature
    {
        public static Graph open(Configuration conf)
        {
            return null;
        }
    }

    public static class WithGraphImplementation
    {
        public static GraphImpl open(Configuration conf)
        {
            return null;
        }
    }

    public static class BadParameter
    {
        public static Graph open(Map<String, String> conf)
        {
            return null;
        }
    }

    public static class BadResult
    {
        public static String open(Configuration conf)
        {
            return null;
        }
    }

    public static class GraphImpl implements Graph
    {

        @Override
        public Vertex addVertex(final Object... keyValues)
        {
            return null;
        }

        @Override
        public <C extends GraphComputer> C compute(final Class<C> graphComputerClass) throws IllegalArgumentException
        {
            return null;
        }

        @Override
        public GraphComputer compute() throws IllegalArgumentException
        {
            return null;
        }

        @Override
        public Iterator<Vertex> vertices(final Object... vertexIds)
        {
            return null;
        }

        @Override
        public Iterator<Edge> edges(final Object... edgeIds)
        {
            return null;
        }

        @Override
        public Transaction tx()
        {
            return null;
        }

        @Override
        public void close() throws Exception
        {

        }

        @Override
        public Variables variables()
        {
            return null;
        }

        @Override
        public org.apache.commons.configuration.Configuration configuration()
        {
            return null;
        }
    }
}