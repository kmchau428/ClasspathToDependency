import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyXmlGenerator {
    public static void generatePomFile(String fileDest, List<String> dependencyList) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("dependencies");
        doc.appendChild(rootElement);

        int successCount = 0;
        for (String dependencyItem : dependencyList) {
            Pattern pattern = Pattern.compile("([^\\/]+\\.jar)$");
            Matcher matcher = pattern.matcher(dependencyItem);
            if (matcher.find())
            {
                successCount++;

                String jar = matcher.group(0);
                System.out.println("Resolving jar: " + jar);

                String artifactId = jar.split("-\\d[.]")[0];
//                System.out.println(artifactId);

                String version = jar.substring(jar.lastIndexOf("-")+1, jar.lastIndexOf(".jar"));
//                System.out.println(version);

                String groupId = DependencyGroupSearcher.findGroupId(artifactId, version);
//                System.out.println(groupId);

                System.out.println();

                Element dependencyElement = doc.createElement("dependency");
                rootElement.appendChild(dependencyElement);

                Element groupIdElement = doc.createElement("groupId");
                groupIdElement.appendChild(doc.createTextNode(groupId));
                dependencyElement.appendChild(groupIdElement);

                Element artifactIdElement = doc.createElement("artifactId");
                artifactIdElement.appendChild(doc.createTextNode(artifactId));
                dependencyElement.appendChild(artifactIdElement);

                Element versionElement = doc.createElement("version");
                versionElement.appendChild(doc.createTextNode(version));
                dependencyElement.appendChild(versionElement);

            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileDest + File.separator + "pom_dependency.xml"));

        transformer.transform(source, result);

        System.out.println("Conversation rate: " + successCount / dependencyList.size() * 100 + "%");

    }
}
