package bt.io.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author &#8904
 *
 */
public class XML
{
    /**
     * Creates an XML document from the given XML String.
     *
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document parse(String xml) throws DocumentException
    {
        return DocumentHelper.parseText(xml);
    }

    /**
     * Creates an XML document from the given XML file.
     *
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document parse(File xml) throws DocumentException
    {
        if (xml == null)
        {
            return null;
        }

        SAXReader reader = new SAXReader();
        return reader.read(xml);
    }

    /**
     * Creates an XML document from the given stream.
     *
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document parse(InputStream xml) throws DocumentException
    {
        if (xml == null)
        {
            return null;
        }

        SAXReader reader = new SAXReader();
        return reader.read(xml);
    }

    /**
     * Saves the XML document structure to the given file.
     *
     * <p>
     * The file is created if it does not exist.
     * </p>
     *
     * @param doc
     * @param file
     * @throws IOException
     */
    public static void save(Document doc, File file) throws IOException
    {
        file.getParentFile().mkdirs();
        file.createNewFile();

        OutputFormat format = OutputFormat.createPrettyPrint();
        try (FileWriter fileWriter = new FileWriter(file))
        {
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            xmlWriter.write(doc);
        }
    }

    /**
     * Saves the XML document structure to the file at the given path.
     *
     * <p>
     * The file is created if it does not exist.
     * </p>
     *
     *
     * @param doc
     * @param filePath
     * @throws IOException
     */
    public static void save(Document doc, String filePath) throws IOException
    {
        save(doc, new File(filePath));
    }

    /**
     * Creates a new extensible ElemtnBuilder from the given Element.
     *
     * @param e
     * @return
     */
    public static ElementBuilder element(Element e)
    {
        return new ElementBuilder(e);
    }

    /**
     * Creates a new extensible ElemtnBuilder for an element with the given name.
     *
     * @param name
     * @return
     */
    public static ElementBuilder element(String name)
    {
        return new ElementBuilder(name);
    }

    /**
     * Creates a new extensible ElemtnBuilder for an element with the given name and the given text.
     *
     * @param name
     * @param text
     * @return
     */
    public static ElementBuilder element(String name, String text)
    {
        return new ElementBuilder(name, text);
    }

    /**
     * Creates a new extensible XMLBuilder.
     *
     * @return
     */
    public static XMLBuilder builder()
    {
        return new XMLBuilder();
    }

    /**
     * Creates a new extensible XMLBuilder from the given Document.
     *
     * @return
     */
    public static XMLBuilder builder(Document doc)
    {
        return new XMLBuilder(doc);
    }

    /**
     * XPath attribute conversion to be case-insensitive.
     *
     * @param attribute
     * @return
     */
    public static String lowerAttribute(String attribute)
    {
        return "@*[lower-case(local-name()) = '" + attribute.toLowerCase() + "']";
    }

    /**
     * XPath node conversion to be case-insensitive.
     *
     * @param node
     * @return
     */
    public static String lowerNode(String node)
    {
        return "*[lower-case(local-name()) = '" + node.toLowerCase() + "']";
    }

    /**
     * Creates a case insensitive xPath String consisting of the given node names joined by '/'.
     *
     * @param nodes
     * @return
     */
    public static String createXPath(String... nodes)
    {
        String xPath = "";

        for (String node : nodes)
        {
            if (node.equals("*"))
            {
                xPath += "/*";
            }
            else
            {
                xPath += "/" + lowerNode(node);
            }
        }

        return xPath;
    }
}