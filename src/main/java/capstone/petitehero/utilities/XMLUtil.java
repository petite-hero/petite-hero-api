package capstone.petitehero.utilities;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class XMLUtil implements Serializable {

    public static Document parseXMLFileToXMLDOM(String source) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            doc.getOwnerDocument().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static XPath createXPath() {
        XPath xPath = null;
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            xPath = xpf.newXPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xPath;
    }
}
