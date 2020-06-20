package bt.io.xml;

import org.dom4j.Document;

/**
 * Defines an Object that can be saved to an XML structure.
 *
 * @author &#8904
 */
public interface Xmlable
{
    /**
     * Creates an XML Document that represents this instance.
     *
     * @return
     */
    public Document toXML();
}