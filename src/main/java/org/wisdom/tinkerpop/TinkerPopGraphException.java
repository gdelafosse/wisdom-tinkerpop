package org.wisdom.tinkerpop;

/**
 * Exception thrown by wisdom-tinkerpop
 */
class TinkerPopGraphException extends Exception
{
    public TinkerPopGraphException(final String message)
    {
        super(message);
    }

    public TinkerPopGraphException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
