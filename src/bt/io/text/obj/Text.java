package bt.io.text.obj;

import bt.io.json.JSONBuilder;
import bt.io.json.Jsonable;
import org.json.JSONObject;

/**
 * @author &#8904
 */
public class Text implements Jsonable
{
    private String value;
    private String language;
    private String key;

    public Text(String key, String value)
    {
        this.key = key.toUpperCase();
        this.value = value;
        this.language = "EN";
    }

    public Text(String key, String value, String language)
    {
        this(key, value);
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

    public String getValue()
    {
        return this.value;
    }

    /**
     * @param value the text to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "{key=" + this.key + ", value=" + this.value + "}";
    }

    @Override
    public JSONObject toJSON()
    {
        return new JSONBuilder().put("key", this.key)
                                .put("value", this.value)
                                .toJSON();
    }
}