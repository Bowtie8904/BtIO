package bt.io.text.obj;

import java.util.List;

/**
 * @author &#8904
 *
 */
public interface TextSource
{
    public List<Text> loadTexts(String group, String language);
}