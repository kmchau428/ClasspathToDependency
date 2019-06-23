package reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EclipseClasspathFileReader implements IClasspathFileReader {
    @Override
    public List<String> read(String filePath) throws ParserConfigurationException, IOException, SAXException {
        File libXmlFile = new File(filePath + File.separator + ".classpath");
        System.out.println("Processing file: " +libXmlFile.getName());

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(libXmlFile);

        doc.getDocumentElement().normalize();

        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList nodeList = doc.getElementsByTagName("classpathentry");

        List<String> jarClasspaths = new ArrayList<>();
        for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {

            Node node = nodeList.item(nodeIdx);

//            System.out.println("\nCurrent Element :" + node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
//                System.out.println("Kind: " + element.getAttribute("kind"));
//                System.out.println("Path: " + element.getAttribute("path"));

                String kind = element.getAttribute("kind");
                String path = element.getAttribute("path");

                if (kind.equals("lib")) {
                    jarClasspaths.add(path);
                }
            }
        }

        return jarClasspaths;
    }
}
