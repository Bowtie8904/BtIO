package bt.io.json;

import bt.log.Log;
import bt.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

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
     * Should the given json start with an array instead of a normal json object, the array will be
     * wrapped in a json object with the key "array".
     * </p>
     *
     * @param json The json String to parse.
     *
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
     * Should the given json start with an array instead of a normal json object, the array will be
     * wrapped in a json object with the key "array".
     * </p>
     *
     * @param json The json InputStream to parse.
     *
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
     * @param json The json file to parse.
     *
     * @return The parsed JSONObject or null if the file was null, does not exist or is incorrectly formatted.
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static JSONObject parse(File json) throws FileNotFoundException, IOException
    {
        return parse(FileUtils.readFile(json));
    }

    /**
     * Saves the JSON structure to the file at the given path.
     *
     * <p>
     * The file is created if it does not exist.
     * </p>
     */
    public static void save(JSONObject json, File file) throws IOException
    {
        file.getParentFile().mkdirs();
        file.createNewFile();

        try (FileWriter fileWriter = new FileWriter(file))
        {
            fileWriter.write(json.toString(4));
        }
    }

    /**
     * Saves the JSON structure to the file at the given path.
     *
     * <p>
     * The file is created if it does not exist.
     * </p>
     */
    public static void save(JSONObject json, String filePath) throws IOException
    {
        save(json, new File(filePath));
    }
}