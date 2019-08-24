package dependency_generator.reader;

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

                    if (url.contains("-javadoc.jar") || url.contains("-sources.jar")) {
                        continue;
                    }

                    if (url.startsWith("jar")) {
                        jarClasspaths = resolveJars(url,filePath,jarClasspaths);
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

    private String resolveJarDirPath(String url, String filePath ) {
        String parentDir = "";
        if (url.contains("$PROJECT_DIR$")) {
            parentDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
        }
        else if (url.contains("USER_HOME")) {
            parentDir = System.getProperty("user.home");
        }

        String jarDirPath = url.substring(url.lastIndexOf("$") + "$".length() + 1);
        Path path = Paths.get(jarDirPath);
        String normalizedJarDirPath = path.normalize().toString().replace(".." + File.separator,"");

        return parentDir + File.separator + normalizedJarDirPath;
    }

    private List<String> resolveJars(String url, String filePath, List<String> jarClasspaths) {
        String fullPath = resolveJarDirPath(url, filePath);
        jarClasspaths.add(fullPath.replaceAll("!","")); //the entry ends with !/

        return jarClasspaths;
    }

    private List<String> resolveFiles(String url, String filePath, List<String> jarClasspaths) {
        String fullPath = resolveJarDirPath(url, filePath);
        File jarDir = new File(fullPath);

        for (File jar : jarDir.listFiles()) {
            jarClasspaths.add(fullPath + File.separator + jar.getName());
        }

        return jarClasspaths;
    }
}
