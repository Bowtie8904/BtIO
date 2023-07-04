package bt.io.text.impl;

import bt.io.json.JSON;
import bt.io.text.exc.TextLoadException;
import bt.io.text.intf.TextLoader;
import bt.io.text.obj.Text;
import bt.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author &#8904
 */
public class JsonTextLoader extends BaseTextLoader
{
    protected File baseFolder;

    public JsonTextLoader(File baseDir)
    {
        this.baseFolder = baseDir;
    }

    public JsonTextLoader(String basePath)
    {
        this(new File(basePath));
    }

    protected boolean filenameMatchesEagerLoading(File dir, String group, String name)
    {
        return name.toLowerCase().matches(group.toLowerCase() + "_.{1,3}\\.lang");
    }

    protected boolean filenameMatchesLazyLoading(File dir, String group, String name)
    {
        return name.toLowerCase().matches(group.toLowerCase() + "_" + this.language.toLowerCase() + "\\.lang");
    }

    protected boolean filenameMatchesLanguageFileEagerLoading(File dir, String name)
    {
        return name.toLowerCase().matches(".*_.{1,3}\\.lang");
    }

    protected boolean filenameMatchesLanguageFileLazyLoading(File dir, String name)
    {
        return name.toLowerCase().matches(".*_" + this.language.toLowerCase()+ "\\.lang");
    }

    protected List<File> getGroupFiles(File directory, String group)
    {
        Log.entry(directory, group);

        List<File> files = new ArrayList<>();

        for (File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                files.addAll(getGroupFiles(file, group));
            }
            else if ((group.equals("*") && this.loadMode == TextLoader.EAGER_LOADING && filenameMatchesLanguageFileEagerLoading(directory, file.getName()))
                    || (group.equals("*") && this.loadMode == TextLoader.LAZY_LOADING && filenameMatchesLanguageFileLazyLoading(directory, file.getName()))
                    || (this.loadMode == TextLoader.EAGER_LOADING && filenameMatchesEagerLoading(directory, group, file.getName()))
                    || (this.loadMode == TextLoader.LAZY_LOADING && filenameMatchesLazyLoading(directory, group, file.getName())))
            {
                files.add(file);
            }
        }

        Log.exit(files);

        return files;
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