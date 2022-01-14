package bt.io.text.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bt.io.text.intf.TextLoader;
import bt.io.text.obj.Text;
import bt.io.text.obj.TextSource;
import bt.log.Log;
import bt.utils.Null;

/**
 * @author &#8904
 *
 */
public class BaseTextLoader implements TextLoader
{
    protected int loadMode;
    protected String language = "EN";
    protected Map<String, Map<String, Text>> texts;
    protected List<TextSource> textSources;

    public BaseTextLoader()
    {
        this.texts = new HashMap<>();
        this.textSources = new ArrayList<>();
        this.loadMode = TextLoader.LAZY_LOADING;
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#getLanguage()
     */
    @Override
    public String getLanguage()
    {
        return this.language;
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#setLanguage(java.lang.String)
     */
    @Override
    public void setLanguage(String language)
    {
        this.language = language.toUpperCase();
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#get(int)
     */
    @Override
    public Text get(String key)
    {
        Log.entry(key);

        var textsForLanguage = this.texts.get(this.language);

        Text text = null;

        if (textsForLanguage != null)
        {
            text = textsForLanguage.get(key.toLowerCase());
        }

        if (text == null)
        {
            text = new Text(key, "* " + key + " *", this.language);
            add(text);
        }

        Log.exit(text);

        return text;
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#add(int, bt.game.resource.text.Text)
     */
    @Override
    public void add(Text text)
    {
        Log.entry(text);

        var textsForLanguage = this.texts.get(text.getLanguage());

        if (textsForLanguage == null)
        {
            textsForLanguage = new HashMap<>();
            this.texts.put(text.getLanguage(), textsForLanguage);
        }

        this.texts.get(text.getLanguage()).put(text.getKey().toLowerCase(), text);

        Log.exit();
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#register(bt.game.resource.load.intf.Loadable)
     */
    @Override
    public void register(TextSource textSource)
    {
        Log.entry(textSource);

        this.textSources.add(textSource);

        Log.exit();
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#load(java.lang.String)
     */
    @Override
    public void load(String group)
    {
        Log.entry(group);

        List<String> loadedClasses = new ArrayList<>();

        for (TextSource textSource : this.textSources)
        {
            if (loadedClasses.contains(textSource.getClass().getName()))
            {
                continue;
            }
            else
            {
                loadedClasses.add(textSource.getClass().getName());
            }

            textSource.loadTexts(group, "");

            int count = 0;
            List<Text> loadedTexts = null;

            if (this.loadMode == TextLoader.LAZY_LOADING)
            {
                loadedTexts = textSource.loadTexts(group, this.language);
            }
            else if (this.loadMode == TextLoader.EAGER_LOADING)
            {
                loadedTexts = textSource.loadTexts(group, null);
            }

            if (loadedTexts != null)
            {
                for (var text : loadedTexts)
                {
                    add(text);
                    count ++ ;
                }
            }

            Log.info("[{}] Loaded {} texts for {}.",
                     group,
                     count,
                     textSource.getClass().getName());
        }

        Log.exit();
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Log.info("Clearing texts.");
        this.textSources.clear();
        this.texts.clear();
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#setLoadMode(int)
     */
    @Override
    public void setLoadMode(int mode)
    {
        this.loadMode = mode;
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#getLoadMode()
     */
    @Override
    public int getLoadMode()
    {
        return this.loadMode;
    }

    /**
     * @see bt.io.text.intf.TextLoader#clear()
     */
    @Override
    public void clear()
    {
        Null.checkRun(this.texts, () -> this.texts.clear());
    }
}