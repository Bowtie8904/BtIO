package bt.io.text.impl;

import bt.io.json.JSONBuilder;
import bt.io.text.obj.Text;
import bt.log.Log;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Hartwig
 * @since 06.07.2023
 */
public class YamlTextSaver implements TextSaver
{
    @Override
    public void save(File file, String language, List<Text> texts) throws IOException
    {
        Map<String, Object> yamlMap = new HashMap<>();
        yamlMap.put("language", language);

        Map<String, Object> textMap = new HashMap<>();

        for (Text text : texts)
        {
            String key = text.getKey().toLowerCase();
            String[] keyParts = key.split("\\.");

            Map<String, Object> map = findMap(textMap, keyParts);
            map.put(keyParts[keyParts.length - 1], text.getValue());
        }

        yamlMap.put("texts", textMap);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);
        yaml.dump(yamlMap, new FileWriter(file.getAbsolutePath()));
    }

    private Map<String, Object> findMap(Map<String, Object> map, String[] keyParts)
    {
        int index = 0;

        while (index < keyParts.length - 1)
        {
            String keyPart = keyParts[index];

            if (!map.containsKey(keyPart))
            {
                map.put(keyPart, new HashMap<String, Object>());
            }

            map = (Map<String, Object>)map.get(keyPart);
            index++;
        }

        return map;
    }
}
