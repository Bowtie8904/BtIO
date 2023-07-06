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
public interface TextSaver
{
    void save(File file, String language, List<Text> texts) throws IOException;
}