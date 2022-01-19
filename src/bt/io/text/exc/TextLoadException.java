package bt.io.text.exc;

/**
 * @author Lukas Hartwig
 * @since 19.01.2022
 */
public class TextLoadException extends RuntimeException
{
    public TextLoadException(String message)
    {
        super(message);
    }

    public TextLoadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}