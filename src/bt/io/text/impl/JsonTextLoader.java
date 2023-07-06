package bt.io.text.impl;

import bt.io.json.JSON;
import bt.io.text.exc.TextLoadException;
import bt.io.text.obj.Text;
import bt.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author &#8904
 */
public class JsonTextLoader extends FileTextLoader
{
    protected File baseFolder;

    public JsonTextLoader(File baseDir)
    {
        super("json");
        this.baseFolder = baseDir;
    }

    public JsonTextLoader(String basePath)
    {
        this(new File(basePath));
    }

    /**
     * Looks for files with the name 'group_<language>'.lang inside the base directory of this loader.
     * <p>
     * Note that these language files can not be inside the jar file.
     * <p>
     * The file will be parsed expecting the following format.
     *
     * <pre>
     * {
     * "language": "en",
     * "texts":
     * [
     * {
     * "key":"Buttons.Play",
     * "value":"Play"
     * }
     * ...
     * ]
     * }
     * </pre>
     */
    @Override
    public void load(String group)
    {
        Log.entry(group);

        super.load(group);

        var files = getGroupFiles(this.baseFolder, group);

        for (File file : files)
        {
            JSONObject jsonFile = null;

            try
            {
                jsonFile = JSON.parse(file);
            }
            catch (IOException e)
            {
                throw new TextLoadException("Failed to read text file.", e);
            }

            String language = jsonFile.getString("language");

            JSONArray jsonTextArray = jsonFile.getJSONArray("texts");

            JSONObject jsonTextObj = null;
            String text = null;
            Text textObj = null;
            int textCount = 0;

            for (int i = 0; i < jsonTextArray.length(); i++)
            {
                text = null;
                jsonTextObj = jsonTextArray.getJSONObject(i);

                if (jsonTextObj.has("key"))
                {
                    String key = jsonTextObj.getString("key").toLowerCase();

                    if (jsonTextObj.has("value"))
                    {
                        text = jsonTextObj.getString("value");
                    }

                    if (text == null)
                    {
                        text = "* " + key + " *";
                    }

                    textObj = new Text(key, text);
                    textObj.setLanguage(language == null ? "EN" : language);
                    add(textObj);
                    textCount++;
                }
            }

            Log.debug("[{}] Loaded {} texts from language file {}.",
                      group,
                      textCount,
                      file.getAbsolutePath());
        }

        Log.exit();
    }
}