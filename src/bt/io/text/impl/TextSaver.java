package bt.io.text.impl;

import bt.io.json.JSONBuilder;
import bt.io.text.obj.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Lukas Hartwig
 * @since 10.07.2022
 */
public class TextSaver
{
    public void save(File file, String language, List<Text> texts) throws IOException
    {
        JSONBuilder builder = new JSONBuilder();

        builder.put("texts", texts.toArray());
        builder.put("language", language);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        writer.write(builder.toJSON().toString(2));

        writer.close();
    }
}