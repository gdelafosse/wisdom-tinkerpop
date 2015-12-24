package org.wisdom.tinkerpop.util;

import java.lang.annotation.Annotation;

import com.google.common.base.Predicate;

/**
 * A Predicate that tells if a class is annotated.
 * @param <A>
 */
public class AnnotationPredicate<A extends Annotation> implements Predicate<Class<?>>
{
    private final Class<A> annotation;

    /**
     * Constructor
     * @param annotation the searched annotation
     */
    public AnnotationPredicate(Class<A> annotation)
    {
        this.annotation = annotation;
    }

    @Override
    public boolean apply(final Class<?> c)
    {
        return c!=null && c.getDeclaredAnnotation(annotation) != null;
    }
}
