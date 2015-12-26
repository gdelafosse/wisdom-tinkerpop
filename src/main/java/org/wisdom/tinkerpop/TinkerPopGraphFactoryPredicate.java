package org.wisdom.tinkerpop;

import java.lang.reflect.Method;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import com.google.common.base.Predicate;

/**
 * Predicate that returns true if a given class can be used as a TinkerPop GraphFactory.
 */
class TinkerPopGraphFactoryPredicate implements Predicate<Class<?>>
{

    @Override
    public boolean apply(final Class<?> input)
    {
        try
        {
            if (input == null)
                return false;

            // we are not interested by the gremlin-core GraphFactory
            if (input.equals(GraphFactory.class))
                return false;

            Method method = input.getMethod("open", Configuration.class);
            return method != null && Graph.class.isAssignableFrom(method.getReturnType());
        }
        catch (NoSuchMethodException | NoClassDefFoundError e)
        {
            return false;
        }
    }
}
