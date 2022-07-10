package bt.io.text.obj;

import bt.io.json.JSONBuilder;
import bt.io.json.Jsonable;
import org.json.JSONObject;

/**
 * @author &#8904
 */
public class Text implements Jsonable
{
    private String text;
    private String language;
    private String key;

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

    /**
     * @return the key
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * @param key the id to set
     */
    public void setKey(String key)
    {
        this.key = key.toUpperCase();
    }

    public String getLanguage()
    {
        return this.language;
    }

    public void setLanguage(String language)
    {
        this.language = language.toUpperCase();
    }

    public String getText()
    {
        return this.text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return getText();
    }

    @Override
    public JSONObject toJSON()
    {
        return new JSONBuilder().put("key", this.key)
                                .put("text", this.text)
                                .toJSON();
    }
}