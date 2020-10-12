package bt.io.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import bt.utils.FileUtils;

/**
 * A utility class to perform JSON operations.
 *
 * @author &#8904
 */
public final class JSON
{
    /**
     * Parses the given JSON String to a valid JSONObject.
     *
     * @param json
     *            The json String to parse.
     * @return The parsed JSONObject or null if the String was null or incorrectly formatted.
     */
    public static JSONObject parse(String json)
    {
        if (json == null)
        {
            return null;
        }

        JSONTokener tokener = new JSONTokener(json);
        JSONObject object = null;

        try
        {
            object = new JSONObject(tokener);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Parses the given JSON InpuitStream to a valid JSONObject.
     *
     * @param json
     *            The json InputStream to parse.
     * @return The parsed JSONObject or null if the InputStream was null or incorrectly formatted.
     */
    public static JSONObject parse(InputStream json)
    {
        if (json == null)
        {
            return null;
        }

        JSONTokener tokener = new JSONTokener(json);
        JSONObject object = null;

        try
        {
            json.close();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        try
        {
            object = new JSONObject(tokener);
        }
        catch (JSONException e)
        {
            return null;
        }
        return object;
    }

    /**
     * Parses the given JSON file to a valid JSONObject.
     *
     * @param json
     *            The json file to parse.
     * @return The parsed JSONObject or null if the file was null, does not exist or is incorrectly formatted.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static JSONObject parse(File json) throws FileNotFoundException, IOException
    {
        return parse(FileUtils.readFile(json));
    }
}