package bt.io.text.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import bt.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import bt.io.json.JSON;
import bt.io.text.exc.TextGroupNotFoundException;
import bt.io.text.intf.TextLoader;
import bt.io.text.obj.Text;

/**
 * @author &#8904
 *
 */
public class JsonTextLoader extends BaseTextLoader
{
    private String resourceDir;

    public JsonTextLoader(File resourceDir)
    {
        this(resourceDir.getAbsolutePath());
    }

    public JsonTextLoader(String resourcePath)
    {
        this.resourceDir = resourcePath;
    }

    /**
     * Attempts to find a file with the given name inside the defined directory (see the constructor). The first file
     * with the correct (case insensitive) name will be used. This method will try to parse the file content as json and
     * return the created {@link JSONObject}.
     *
     * <p>
     * The resource filer needs to have the file extension .lang.
     * </p>
     *
     * @param group
     *            The context name = the name of the file (without file ending) to load from.
     * @return The parsed json from the file or null if parsing failed for any reason.
     */
    private JSONObject getJsonForName(String group)
    {
        String jsonString = null;
        String path = this.resourceDir + "/" + group + ".lang";

        try (var stream = getClass().getClassLoader().getResourceAsStream(path))
        {
            jsonString = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
        }
        catch (Exception e)
        {
            Log.error("Failed to read file", e);
        }

        return JSON.parse(jsonString);
    }

    /**
     * Looks for a file with the name 'group'.lang.
     *
     * The file will be parsed expecting the following format.
     *
     * <pre>
     {
        "texts":
        [
            {
                "key":"Buttons.Play",
                "languages":
                [
                    {
                        "language":"en",
                        "text":"Play"
                    },
                    {
                        "language":"de",
                        "text":"Spielen"
                    },
                    ...
                ]
            },
            {
                "key":"Buttons.Exit",
                "languages":
                [
                    {
                        "language":"en",
                        "text":"Exit"
                    },
                    {
                        "language":"de",
                        "text":"Beenden"
                    }
                ]
            },
            ...
        ]
     }
     * </pre>
     */
    @Override
    public void load(String group)
    {
        Log.entry(group);

        super.load(group);
        JSONObject jsonFile = getJsonForName(group);

        if (jsonFile == null)
        {
            throw new TextGroupNotFoundException("Could not find a language file with the name " + group + ".lang");
        }

        JSONArray jsonTextArray = jsonFile.getJSONArray("texts");

        JSONObject jsonTextObj = null;
        JSONArray jsonLanguageArray = null;
        JSONObject jsonLanguageObj = null;
        String text = null;
        String language = null;
        Text textObj = null;
        int count = 0;

        for (int i = 0; i < jsonTextArray.length(); i ++ )
        {
            text = null;
            jsonLanguageArray = null;
            jsonLanguageObj = null;
            jsonTextObj = jsonTextArray.getJSONObject(i);

            if (jsonTextObj.has("key"))
            {
                String key = jsonTextObj.getString("key");

                if (this.loadMode == TextLoader.LAZY_LOADING)
                {
                    if (jsonTextObj.has("languages"))
                    {
                        jsonLanguageArray = jsonTextObj.getJSONArray("languages");

                        for (int k = 0; k < jsonLanguageArray.length(); k ++ )
                        {
                            jsonLanguageObj = jsonLanguageArray.getJSONObject(k);

                            if (this.language.equalsIgnoreCase(jsonLanguageObj.getString("language")))
                            {
                                if (jsonLanguageObj.has("text"))
                                {
                                    text = jsonLanguageObj.getString("text");
                                    language = jsonLanguageObj.getString("language");
                                    break;
                                }
                            }
                        }
                    }

                    if (text == null)
                    {
                        text = "* " + key + " *";
                    }

                    textObj = new Text(key, text);
                    textObj.setLanguage(language == null ? "EN" : language);
                    add(textObj);
                    count ++ ;
                }
                else if (this.loadMode == TextLoader.EAGER_LOADING)
                {
                    if (jsonTextObj.has("languages"))
                    {
                        jsonLanguageArray = jsonTextObj.getJSONArray("languages");

                        for (int k = 0; k < jsonLanguageArray.length(); k ++ )
                        {
                            jsonLanguageObj = jsonLanguageArray.getJSONObject(k);

                            if (jsonLanguageObj.has("text"))
                            {
                                text = jsonLanguageObj.getString("text");
                                language = jsonLanguageObj.getString("language");
                            }
                            else
                            {
                                text = null;
                            }

                            if (text == null)
                            {
                                text = "* " + key + " *";
                            }

                            textObj = new Text(key, text);
                            textObj.setLanguage(language == null ? "EN" : language);
                            add(textObj);
                            count ++ ;
                        }
                    }
                }
            }
        }

        Log.info("[{}] Loaded {} texts from language file.",
                          group,
                          count);

        Log.exit();
    }
}