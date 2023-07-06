package bt.io.text.impl;

import bt.io.text.intf.TextLoader;
import bt.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Hartwig
 * @since 06.07.2023
 */
public abstract class FileTextLoader extends BaseTextLoader
{
    protected String fileEnding;

    public FileTextLoader(String fileEnding)
    {
        this.fileEnding = fileEnding;
    }

    protected boolean filenameMatchesEagerLoading(File dir, String group, String name)
    {
        return name.toLowerCase().matches(group.toLowerCase() + "_.{1,3}\\." + this.fileEnding.toLowerCase());
    }

    protected boolean filenameMatchesLazyLoading(File dir, String group, String name)
    {
        return name.toLowerCase().matches(group.toLowerCase() + "_" + this.language.toLowerCase() + "\\." + this.fileEnding.toLowerCase());
    }

    protected boolean filenameMatchesLanguageFileEagerLoading(File dir, String name)
    {
        return name.toLowerCase().matches(".*_.{1,3}\\." + this.fileEnding.toLowerCase());
    }

    protected boolean filenameMatchesLanguageFileLazyLoading(File dir, String name)
    {
        return name.toLowerCase().matches(".*_" + this.language.toLowerCase()+ "\\." + this.fileEnding.toLowerCase());
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
}