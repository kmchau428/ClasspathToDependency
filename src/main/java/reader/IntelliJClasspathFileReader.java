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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IntelliJClasspathFileReader implements IClasspathFileReader {
    @Override
    public List<String> read(String filePath) throws ParserConfigurationException, IOException, SAXException {
        File libDir = new File(filePath + File.separator + ".idea" + File.separator + "libraries");
        List<String> jarClasspaths = new ArrayList<>();

        File[] libXmlFiles = libDir.listFiles();
        for(File libXmlFile : libXmlFiles) {
            System.out.println("File name :" +libXmlFile.getName());

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(libXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            jarClasspaths = resolveJars(doc,jarClasspaths);
            jarClasspaths = resolveFiles(doc,filePath,jarClasspaths);


        }


        return jarClasspaths;
    }

    private List<String> resolveJars(Document doc, List<String> jarClasspaths) {
        NodeList nodeList = doc.getElementsByTagName("root");

        for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {

            Node node = nodeList.item(nodeIdx);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                String url = element.getAttribute("url");

                if (url.startsWith("jar")) {
                    System.out.println(url);
                    jarClasspaths.add(url.substring(0, url.lastIndexOf("!/"))); //the entry ends with !/
                }
            }
        }

        return jarClasspaths;
    }

    private List<String> resolveFiles(Document doc, String filePath, List<String> jarClasspaths) throws IOException {
        NodeList nodeList = doc.getElementsByTagName("jarDirectory");

        for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {

            Node node = nodeList.item(nodeIdx);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                String url = element.getAttribute("url");
                String jarDirPath = url.substring(url.lastIndexOf("$") + "$".length() + 1);
                System.out.println("**" + jarDirPath);
                System.out.println("**" + filePath);
                System.out.println("**" + System.getProperty("user.home"));
                Path p1 = Paths.get(jarDirPath);
                String normalizedJarDirPath = p1.normalize().toString().replace(".." + File.separator,"");
                System.out.println("**" + normalizedJarDirPath);

                File jarDir = new File("C:\\projects\\TestProjIntelliJ\\..\\lib_cmp\\test");
                for (File jar : jarDir.listFiles()) {
                    //jarClasspaths.add(url.substring(0, url.lastIndexOf("!/"))); //the entry ends with !/
                    System.out.println("++" + jar.getName());
                }
            }
        }

        return jarClasspaths;
    }
}
