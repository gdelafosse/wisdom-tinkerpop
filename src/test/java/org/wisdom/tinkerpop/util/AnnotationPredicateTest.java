package org.wisdom.tinkerpop.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AnnotationPredicateTest
{
    private AnnotationPredicate cut = new AnnotationPredicate(Deprecated.class);

    @Test
    public void testApplyOnAnnotated() throws Exception
    {
        assertTrue(cut.apply(Annotated.class));
    }

    @Test
    public void testApplyOnNonAnnotated() throws Exception
    {
        assertFalse(cut.apply(String.class));
    }

    @Test
    public void testApplyOnOtherAnnotation() throws Exception
    {
        assertFalse(cut.apply(OtherAnnotated.class));
    }
}