package bt.io.text.intf;

import bt.io.text.obj.Text;
import bt.io.text.obj.TextSource;
import bt.types.Killable;

/**
 * @author &#8904
 */
public interface TextLoader extends Killable
{
    /** Indicates that all language variants of the texts should be loaded. */
    public static final int EAGER_LOADING = 1;

    /** Indicates that only the required language variant of the texts should be loaded. */
    public static final int LAZY_LOADING = 2;

    /**
     * Gets the currently set language.
     *
     * @return
     */
    public String getLanguage();

    /**
     * Sets the language.
     *
     * @param language
     *            Format can be chosen freely but has to be used with case sensitivity in mind.
     */
    public void setLanguage(String language);

    /**
     * Sets the mode that will be used during loading.
     *
     * @param mode
     *            <ul>
     *            <li>{@link #LAZY_LOADING} to only load the texts for the currently set language</li>
     *            <li>{@link #EAGER_LOADING} to load all language variants for the texts</li>
     *            </ul>
     */
    public void setLoadMode(int mode);

    /**
     * Gets the mode that texts will be loaded with.
     *
     * @return
     *         <ul>
     *         <li>{@link #LAZY_LOADING} to only load the texts for the currently set language</li>
     *         <li>{@link #EAGER_LOADING} to load all language variants for the texts</li>
     *         </ul>
     */
    public int getLoadMode();

    /**
     * Gets the version of the text with the given key for the currently set language.
     *
     * <p>
     * If no text could be found a new text will be created consisting of '* key *'.
     * </p>
     *
     * @param key
     * @return
     */
    public Text get(String key);

    /**
     * Gets the version of the text with the given key for the currently set language.
     *
     * The parameters will be inserted in order into {} placeholders.
     *
     * <p>
     * If no text could be found a new text will be created consisting of '* key *'.
     * </p>
     *
     * @param key
     * @return
     */
    public Text get(String key, Object... parameters);

    /**
     * Adds the given text to the loader. The text must be fully configured with id and language.
     *
     * @param text
     */
    public void add(Text text);

    /**
     * Registers a TextSource that will be loaded during {@link #load(String)}.
     *
     * @param textSource
     */
    public void register(TextSource textSource);

    /**
     * Loads the language texts for the given group.
     *
     * @param group
     */
    public void load(String group);

    public void clear();
}