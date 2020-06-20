package bt.io.xml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author &#8904
 *
 */
public class XMLBuilder
{
    private Document doc;

    public XMLBuilder()
    {
        this.doc = DocumentHelper.createDocument();
    }

    public XMLBuilder(Document doc)
    {
        this.doc = doc;
    }

    public XMLBuilder addElement(Element element)
    {
        this.doc.add(element);

        return this;
    }

    public XMLBuilder addElements(Element... elements)
    {
        for (Element e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public XMLBuilder addElement(Document document)
    {
        this.doc.add(document.getRootElement().createCopy());

        return this;
    }

    public XMLBuilder addElements(Document... documents)
    {
        for (Document doc : documents)
        {
            addElement(doc);
        }

        return this;
    }

    public XMLBuilder addElement(Xmlable xmlable)
    {
        this.doc.add(xmlable.toXML().getRootElement().createCopy());

        return this;
    }

    public XMLBuilder addElements(Xmlable... xmlables)
    {
        for (Xmlable xmlable : xmlables)
        {
            addElement(xmlable);
        }

        return this;
    }

    public XMLBuilder addElement(ElementBuilder element)
    {
        this.doc.add(element.toXML());

        return this;
    }

    public XMLBuilder addElements(ElementBuilder... elements)
    {
        for (ElementBuilder e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public XMLBuilder addComment(String comment)
    {
        this.doc.addComment(comment);
        return this;
    }

    public ElementBuilder getRoot()
    {
        return XML.element(this.doc.getRootElement());
    }

    public Document toXML()
    {
        return this.doc;
    }

    @Override
    public String toString()
    {
        return this.doc.asXML();
    }
}