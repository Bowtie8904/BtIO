package bt.io.xml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author &#8904
 *
 */
public class ElementBuilder
{
    private Element element;

    public ElementBuilder(Element e)
    {
        this.element = e;
    }

    public ElementBuilder(String name)
    {
        this.element = DocumentHelper.createElement(name);
    }

    public ElementBuilder(String name, String text)
    {
        this.element = DocumentHelper.createElement(name);
        this.element.addText(text);
    }

    public ElementBuilder addText(String text)
    {
        this.element.addText(text);

        return this;
    }

    public ElementBuilder addElement(Element element)
    {
        this.element.add(element);

        return this;
    }

    public ElementBuilder addElements(Element... elements)
    {
        for (Element e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public ElementBuilder addElement(Document element)
    {
        addElement(element.getRootElement().createCopy());

        return this;
    }

    public ElementBuilder addElements(Document... elements)
    {
        for (Document e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public ElementBuilder addElement(Xmlable element)
    {
        addElement(element.toXML().getRootElement().createCopy());

        return this;
    }

    public ElementBuilder addElements(Xmlable... elements)
    {
        for (Xmlable e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public ElementBuilder addElement(ElementBuilder element)
    {
        return addElement(element.toXML());
    }

    public ElementBuilder addElements(ElementBuilder... elements)
    {
        for (ElementBuilder e : elements)
        {
            addElement(e);
        }

        return this;
    }

    public ElementBuilder addAttribute(String name, String value)
    {
        this.element.addAttribute(name, value);

        return this;
    }

    public ElementBuilder addComment(String comment)
    {
        this.element.addComment(comment);
        return this;
    }

    public Element toXML()
    {
        return this.element;
    }

    @Override
    public String toString()
    {
        return this.element.asXML();
    }
}