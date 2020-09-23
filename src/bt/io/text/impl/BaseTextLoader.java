package bt.io.text.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bt.io.text.intf.TextLoader;
import bt.io.text.obj.Text;
import bt.io.text.obj.TextSource;
import bt.log.Logger;
import bt.utils.nulls.Null;

/**
 * @author &#8904
 *
 */
public class BaseTextLoader implements TextLoader
{
    protected int loadMode;
    protected String language = "EN";
    protected Map<String, Map<Integer, Text>> texts;
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
    public Text get(int id)
    {
        var textsForLanguage = this.texts.get(this.language);

        Text text = null;

        if (textsForLanguage != null)
        {
            text = textsForLanguage.get(id);
        }

        if (text == null)
        {
            text = new Text(id, "* " + id + " *", this.language);
            add(text);
        }

        return text;
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#add(int, bt.game.resource.text.Text)
     */
    @Override
    public void add(Text text)
    {
        var textsForLanguage = this.texts.get(text.getLanguage());

        if (textsForLanguage == null)
        {
            textsForLanguage = new HashMap<>();
            this.texts.put(text.getLanguage(), textsForLanguage);
        }

        this.texts.get(text.getLanguage()).put(text.getID(), text);
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#register(bt.game.resource.load.intf.Loadable)
     */
    @Override
    public void register(TextSource textSource)
    {
        this.textSources.add(textSource);
    }

    /**
     * @see bt.game.resource.load.intf.TextLoader#load(java.lang.String)
     */
    @Override
    public void load(String group)
    {
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

            Logger.global().printf("[%s] Loaded %d texts for %s.",
                                   group,
                                   count,
                                   textSource.getClass().getName());
        }
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Clearing texts.");
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