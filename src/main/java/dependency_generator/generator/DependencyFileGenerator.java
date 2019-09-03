package dependency_generator.generator;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import dependency_generator.DependencyConverter;
import dependency_generator.Main;
import dependency_generator.dto.DependencyEntry;
import dependency_generator.searcher.IDependencySearcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyFileGenerator {

    public static final String DEPENDENCY_TXT_FILEPATH = "_dependency.txt";

    public static void generateDepFile(String fileDest, List<String> dependencyList, List<IDependencySearcher> searchers, String buildTool)
            throws TransformerException, ParserConfigurationException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("dependencies");
        doc.appendChild(rootElement);

        int successCount = 0;
        Set<String> dependencySet = new HashSet<>();
        for (String dependencyItem : dependencyList) {
            Pattern pattern = Pattern.compile("([^\\/]+\\.jar)$");
            Matcher matcher = pattern.matcher(dependencyItem);
            if (matcher.find())
            {
                String jar = matcher.group(0);
                System.out.println("\nResolving jar: " + jar);

                String[] jarName = jar.split("\\\\");
                String artifactId = jarName[jarName.length-1].split("-\\d+[.]")[0].replaceAll(".jar","");
                String version =
                        jar.lastIndexOf("-") < jar.lastIndexOf(artifactId) + artifactId.length() ?
                        "" :
                        jar.substring(jar.lastIndexOf("-")+1, jar.lastIndexOf(".jar"));

                String checksum;
                try {
                    checksum = genChecksum(dependencyItem);
                }
                catch (FileNotFoundException e) {
                    checksum = artifactId + (version.equals("") ? "" : "-" + version);
                }

                //duplicate found
                if (!dependencySet.add(checksum)) {
                    System.out.println("duplicate found, will be ignored.");
                    continue;
                }

                DependencyEntry dependencyEntry = null;
                for (IDependencySearcher searcher : searchers) {
                    dependencyEntry = searcher.resolveDependency(artifactId, version, checksum);
                    if (dependencyEntry.getGroupId() != null) {
                        successCount++;
                        break;
                    }
                }

                if (dependencyEntry.getGroupId() != null) {
                    System.out.println("resolved to: " + dependencyEntry.getGroupId() + ":" + dependencyEntry.getArtifactId() + ":" + dependencyEntry.getVerison());
                    Element dependencyElement = doc.createElement("dependency");
                    rootElement.appendChild(dependencyElement);

                    Element groupIdElement = doc.createElement("groupId");
                    groupIdElement.appendChild(doc.createTextNode(dependencyEntry.getGroupId()));
                    dependencyElement.appendChild(groupIdElement);

                    Element artifactIdElement = doc.createElement("artifactId");
                    artifactIdElement.appendChild(doc.createTextNode(dependencyEntry.getArtifactId()));
                    dependencyElement.appendChild(artifactIdElement);

                    Element versionElement = doc.createElement("version");
                    versionElement.appendChild(doc.createTextNode(dependencyEntry.getVerison()));
                    dependencyElement.appendChild(versionElement);
                }
            }
        }

        if (buildTool.equals(Main.BUILD_TOOL_MAVEN)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileDest + File.separator + DEPENDENCY_TXT_FILEPATH));
            transformer.transform(source, result);
        }
        else {
            DependencyConverter.convert(fileDest + File.separator + DEPENDENCY_TXT_FILEPATH, doc, buildTool);
        }


        System.out.println("\nConversation rate: " + String.format("%.2f", (double)successCount/dependencySet.size() * 100) + "% ("
                + successCount + " out of " + dependencySet.size() + ")");

    }

    private static String genChecksum(String jarFilePath) throws IOException {
        File currentJavaJarFile = new File(jarFilePath);
        HashCode hc = Files.asByteSource(currentJavaJarFile).hash(Hashing.sha1());

        return hc.toString();
    }
}
