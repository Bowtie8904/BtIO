package bt.io.text.impl;

import bt.io.text.exc.TextLoadException;
import bt.io.text.obj.Text;
import bt.log.Log;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author Lukas Hartwig
 * @since 06.07.2023
 */
public class YamlTextLoader extends FileTextLoader
{
    protected File baseFolder;

    public YamlTextLoader(File baseDir)
    {
        super("yaml");
        this.baseFolder = baseDir;
    }

    public YamlTextLoader(String basePath)
    {
        this(new File(basePath));
    }

    @Override
    public void load(String group)
    {
        Log.entry(group);

        super.load(group);

        var files = getGroupFiles(this.baseFolder, group);

        for (File file : files)
        {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = null;

            try
            {
                yamlData = yaml.load(Files.readString(file.toPath()));
            }
            catch (IOException e)
            {
                throw new TextLoadException("Failed to read text file.", e);
            }

            String language = (String)yamlData.get("language");
            Map<String, Object> textMap = (Map<String, Object>)yamlData.get("texts");

            parseTexts(textMap, "", language);

            Log.debug("[{}] Loaded {} texts from language file {}.",
                      group,
                      getTexts().size(),
                      file.getAbsolutePath());
        }

        Log.exit();
    }

    private void parseTexts(Map<String, Object> textMap, String textKey, String language)
    {
        Text textObj = null;

        for (var key : textMap.keySet())
        {
            var value = textMap.get(key);

            if (value instanceof Map)
            {
                String finalKey = textKey;

                if (!finalKey.isEmpty())
                {
                    finalKey += ".";
                }

                finalKey += key;

                parseTexts((Map<String, Object>)value, finalKey, language);
            }
            else
            {
                String finalKey = textKey;

                if (!finalKey.isEmpty())
                {
                    finalKey += ".";
                }

                finalKey += key;

                textObj = new Text(finalKey, value == null ? "" : value.toString());
                textObj.setLanguage(language == null ? "EN" : language);
                add(textObj);
            }
        }
    }
}
