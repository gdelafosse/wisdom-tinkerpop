package org.wisdom.tinkerpop.util;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class UrlToClassFunction implements Function<URL, Class<?>>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlToClassFunction.class);

    private final Bundle bundle;

    public UrlToClassFunction(final Bundle bundle)
    {
        this.bundle = bundle;
    }

    @Override
    public Class<?> apply(final URL url)
    {
        return classFromUrl(url);
    }

    private Class<?> classFromUrl(URL url)
    {
        try
        {
            return bundle.loadClass(classNameFromUrl(url));
        }
        catch (ClassNotFoundException | IllegalStateException | NoClassDefFoundError e)
        {
            LOGGER.warn("Unable to load class {} from bundle {}", url.getFile(), bundle.getSymbolicName());
            LOGGER.debug(e.getMessage(), e);
            return null;
        }
    }

    private String classNameFromUrl(URL url)
    {
        String className = StringUtils.replace(url.getFile(), "/", ".");
        className = StringUtils.removeStart(className, ".");
        return StringUtils.removeEnd(className, ".class");
    }
}
