package org.wisdom.tinkerpop;

import java.lang.reflect.Method;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Graph;

import com.google.common.base.Predicate;

public class TinkerPopGraphFactoryPredicate implements Predicate<Class<?>>
{

    @Override
    public boolean apply(final Class<?> input)
    {
        try
        {
            Method method = input.getMethod("open", Configuration.class);
            return method != null && method.getReturnType().isAssignableFrom(Graph.class);
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
    }
}
