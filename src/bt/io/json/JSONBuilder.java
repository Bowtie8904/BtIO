package bt.io.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A calss to simplyfy the process of building a JSON from multiple objects.
 *
 * @author &#8904
 *
 */
public class JSONBuilder
{
    private JSONObject jsonObject;

    public JSONBuilder()
    {
        this.jsonObject = new JSONObject();
    }

    /**
     * returns the built JSON as a JSONObject.
     *
     * @return
     */
    public JSONObject toJSON()
    {
        return this.jsonObject;
    }

    /**
     * returns the built JSON as a JSON String.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.jsonObject.toString();
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, Object value)
    {
        try
        {
            if (value instanceof Jsonable)
            {
                this.jsonObject.put(name,
                                    ((Jsonable)value).toJSON());
            }
            else
            {
                this.jsonObject.put(name,
                                    value);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, int value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, boolean value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, long value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, double value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, float value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, char value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts the value with the name into the json.
     *
     * @param name
     * @param value
     * @return
     */
    public JSONBuilder put(String name, short value)
    {
        try
        {
            this.jsonObject.put(name,
                                value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * <p>
     * If the value classes implement Jsonable the toJSON() method will be called to format them correctly.
     * </p>
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, Object... values)
    {
        JSONArray array = new JSONArray();

        for (Object o : values)
        {
            if (o instanceof Jsonable)
            {
                array.put(((Jsonable)o).toJSON());
            }
            else
            {
                array.put(o);
            }
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, int... values)
    {
        JSONArray array = new JSONArray();

        for (int o : values)
        {
            array.put(o);
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, boolean... values)
    {
        JSONArray array = new JSONArray();

        for (boolean o : values)
        {
            array.put(o);
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, long... values)
    {
        JSONArray array = new JSONArray();

        for (long o : values)
        {
            array.put(o);
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, short... values)
    {
        JSONArray array = new JSONArray();

        for (short o : values)
        {
            array.put(o);
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, double... values)
    {
        JSONArray array = new JSONArray();

        for (double o : values)
        {
            try
            {
                array.put(o);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, float... values)
    {
        JSONArray array = new JSONArray();

        for (float o : values)
        {
            try
            {
                array.put(o);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts an array of the values with the given name into the JSON.
     *
     * @param name
     * @param values
     * @return
     */
    public JSONBuilder put(String name, char... values)
    {
        JSONArray array = new JSONArray();

        for (char o : values)
        {
            array.put(o);
        }

        try
        {
            this.jsonObject.put(name,
                                array);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }
}