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
            System.out.println("Processing file: " +libXmlFile.getName());

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(libXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("root");
            for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {

                Node node = nodeList.item(nodeIdx);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String url = element.getAttribute("url");

                    if (url.startsWith("jar")) {
                        jarClasspaths = resolveJars(url,jarClasspaths);
                    }
                    else if (url.startsWith("file")) {
                        jarClasspaths = resolveFiles(url,filePath,jarClasspaths);
                    }
                }
            }

            System.out.println();
        }


        return jarClasspaths;
    }

    private List<String> resolveJars(String url, List<String> jarClasspaths) {
        jarClasspaths.add(url.substring(url.lastIndexOf("/", url.lastIndexOf("!/"))+1, url.lastIndexOf("!/"))); //the entry ends with !/


        return jarClasspaths;
    }

    private List<String> resolveFiles(String url, String filePath, List<String> jarClasspaths) {
        if (url.contains("$PROJECT_DIR$")) {
            url = url.replaceAll("\\$PROJECT_DIR\\$", filePath);
        }
        else if (url.contains("$USER_HOME$")) {
            url = url.replaceAll("\\$USER_HOME\\$",
                System.getProperty("user.home").replaceAll("\\\\", "/"));
        }

        String jarDirPath = url.substring(url.indexOf("//") + 2);
        Path p1 = Paths.get(jarDirPath);
        String normalizedJarDirPath = p1.normalize().toString();

        File jarDir = new File(normalizedJarDirPath);
        for (File jar : jarDir.listFiles()) {
            jarClasspaths.add(jar.getName());
        }

        return jarClasspaths;
    }
}
