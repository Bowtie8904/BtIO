package bt.io.text.obj;

/**
 * @author &#8904
 *
 */
public class Text
{
    private String text;
    private String language;
    private String key;

    /**
     * @return the key
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * @param key
     *            the id to set
     */
    public void setKey(String key)
    {
        this.key = key.toUpperCase();
    }

    public Text(String key, String text)
    {
        this.key = key.toUpperCase();
        this.text = text;
        this.language = "EN";
    }

    public Text(String key, String text, String language)
    {
        this(key, text);
        setLanguage(language);
    }

    public void setLanguage(String language)
    {
        this.language = language.toUpperCase();
    }

    public String getLanguage()
    {
        return this.language;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public String toString()
    {
        return getText();
    }
}