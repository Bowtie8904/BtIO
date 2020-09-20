package bt.io.text.obj;

/**
 * @author &#8904
 *
 */
public class Text
{
    private String text;
    private String language;
    private int id;

    /**
     * @return the id
     */
    public int getID()
    {
        return this.id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setID(int id)
    {
        this.id = id;
    }

    public Text(int id, String text)
    {
        this.id = id;
        this.text = text;
        this.language = "EN";
    }

    public Text(int id, String text, String language)
    {
        this(id, text);
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