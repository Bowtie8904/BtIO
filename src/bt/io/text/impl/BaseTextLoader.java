package bt.io.text.impl;

import bt.io.text.exc.TextLoadException;
import bt.io.text.intf.TextLoader;
import bt.io.text.obj.Text;
import bt.io.text.obj.TextSource;
import bt.log.Log;
import bt.utils.Null;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author &#8904
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

    @Override
    public List<Text> getTexts()
    {
        List<Text> textList = new ArrayList<>();
        Map<String, Text> textsForLanguage = this.texts.get(getLanguage());

        if (textsForLanguage != null)
        {
            textList.addAll(textsForLanguage.values());
        }

        return textList;
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
     * @see bt.game.resource.load.intf.TextLoader#get(String)
     */
    @Override
    public Text getText(String key)
    {
        return getTextForLanguage(key, this.language);
    }

    @Override
    public Text getText(String key, Object... parameters)
    {
        return getTextForLanguage(key, this.language, parameters);
    }

    @Override
    public Text getTextForLanguage(String key, String language)
    {
        Log.entry(key);

        var textsForLanguage = this.texts.get(language);

        Text text = null;

        if (textsForLanguage != null)
        {
            text = textsForLanguage.get(key.toLowerCase());
        }

        if (text == null)
        {
            text = new Text(key, "* " + key + " *", this.language);
        }

        Log.exit(text);

        return text;
    }

    @Override
    public Text getTextForLanguage(String key, String language, Object... parameters)
    {
        Log.entry(key, new Object[] { parameters });

        Text text = getTextForLanguage(key, language);
        String currentText = text.getValue();

        if (parameters != null)
        {
            for (int i = 0; i < parameters.length; i++)
            {
                currentText = currentText.replaceFirst("\\{}", parameters[i] != null ? parameters[i].toString() : "null");
            }
        }

        text = new Text(key, currentText, this.language);

        return text;
    }

    @Override
    public String translate(String key)
    {
        return getText(key).getValue();
    }

    @Override
    public String translate(String key, Object... parameters)
    {
        return getText(key, parameters).getValue();
    }

    @Override
    public String translateToLanguage(String key, String language)
    {
        return getTextForLanguage(key, language).getValue();
    }

    @Override
    public String translateToLanguage(String key, String language, Object... parameters)
    {
        return getTextForLanguage(key, language, parameters).getValue();
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

        String key = text.getKey().toLowerCase();

        if (textsForLanguage.containsKey(key))
        {
            throw new TextLoadException("Text with key '" + key + "' already exists.");
        }

        textsForLanguage.put(key, text);

        Log.exit();
    }

    @Override
    public void update(String key, String language, String value)
    {
        Log.entry(key, language, value);

        var textsForLanguage = this.texts.get(language.toUpperCase());

        if (textsForLanguage == null)
        {
            textsForLanguage = new HashMap<>();
            this.texts.put(language.toUpperCase(), textsForLanguage);
        }

        if (!textsForLanguage.containsKey(key.toLowerCase()))
        {
            Text text = new Text(key, value, language);
            textsForLanguage.put(key, text);
        }
        else
        {
            Text text = textsForLanguage.get(key.toLowerCase());
            text.setValue(value);
        }

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
                    count++;
                }
            }

            Log.debug("[{}] Loaded {} texts for {}.",
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
     * @see bt.game.resource.load.intf.TextLoader#getLoadMode()
     */
    @Override
    public int getLoadMode()
    {
        return this.loadMode;
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
     * @see bt.io.text.intf.TextLoader#clear()
     */
    @Override
    public void clear()
    {
        Null.checkRun(this.texts, () -> this.texts.clear());
    }
}