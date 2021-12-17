package bt.io.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import bt.log.Log;
import org.json.JSONArray;
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
    private static JSONObject buildObject(JSONTokener tokener)
    {
        JSONObject object = null;

        try
        {
            if (tokener.nextClean() != '{')
            {
                // json might start with an array, so we have to wrap it in a new jsonobject
                tokener.back();
                var array = new JSONArray(tokener);
                object = new JSONObject();
                object.put("array", array);
            }
            else
            {
                tokener.back();
                object = new JSONObject(tokener);
            }
        }
        catch (JSONException e)
        {
            Log.error("Failed to create JSOn object", e);
        }

        return object;
    }

    /**
     * Parses the given JSON String to a valid JSONObject.
     *
     * <p>
     *     Should the given json start with an array instead of a normal json object, the array will be
     *     wrapped in a json object with the key "array".
     * </p>
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
        JSONObject object = buildObject(tokener);

        return object;
    }

    /**
     * Parses the given JSON InpuitStream to a valid JSONObject.
     *
     * <p>
     *     Should the given json start with an array instead of a normal json object, the array will be
     *     wrapped in a json object with the key "array".
     * </p>
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
            Log.error("Failed to close input stream", e1);
        }

        object = buildObject(tokener);

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