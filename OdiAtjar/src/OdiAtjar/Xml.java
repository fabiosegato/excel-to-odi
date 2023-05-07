package OdiAtjar;

import org.w3c.dom.Node;
import org.w3c.dom.CharacterData;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Xml
{
    private static String xml;
    
    public Xml(final String pXml) {
        Xml.xml = pXml;
    }
    
    public String getAtributte(final String pAtribute) throws Exception {
        final File file = new File(Xml.xml);
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = builder.parse(file);
        final NodeList nodes = doc.getElementsByTagName("Field");
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Element element = (Element)nodes.item(i);
            if (element.getAttribute("name").trim().equals(pAtribute)) {
                return getCharacterDataFromElement(element).trim();
            }
        }
        return "";
    }
    
    public static String getCharacterDataFromElement(final Element e) {
        final Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            final CharacterData cd = (CharacterData)child;
            return cd.getData();
        }
        return "";
    }
}

