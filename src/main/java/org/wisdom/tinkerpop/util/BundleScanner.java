package org.wisdom.tinkerpop.util;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

public class BundleScanner
{
    private final static Logger LOGGER = LoggerFactory.getLogger(BundleScanner.class);

    private final Bundle bundle;

    private BundleScanner(Bundle bundle)
    {
        this.bundle = bundle;
    }

    public static BundleScanner bundle(Bundle bundle)
    {
        return new BundleScanner(bundle);
    }

    public Set<Class<?>> scan(Predicate<Class<?>> predicate)
    {
        /*return Collections.list(bundle.findEntries("/", "*.class", true)).stream()
                .map(new UrlToClassFunction(bundle))
                .filter(new AnnotationPredicate(annotation))
                .collect(Collectors.toSet());*/

        Enumeration<URL> entries = bundle.findEntries("/", "*.class", true);
        if (entries == null)
        {
            return Collections.emptySet();
        }
        return FluentIterable.from(Collections.list(entries))
                .transform(new UrlToClassFunction((bundle)))
                .filter(predicate)
                .toSet();
    }

    public Set<Class<?>> scan()
    {
        return scan(Predicates.notNull());
    }
}
