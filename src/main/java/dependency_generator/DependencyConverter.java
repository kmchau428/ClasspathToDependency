package dependency_generator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DependencyConverter {
    public static void convert(String outputPath, Document doc, String buildTool) {
        if (buildTool.equals(Main.BUILD_TOOL_GRADLE)) {
            try (FileWriter writer = new FileWriter(outputPath);
                 BufferedWriter bw = new BufferedWriter(writer)) {

                NodeList nodeList = doc.getElementsByTagName("dependency");
                bw.write("dependencies {\n");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    NodeList dependency = nodeList.item(i).getChildNodes();
                    String g = dependency.item(0).getFirstChild().getNodeValue();
                    String a = dependency.item(1).getFirstChild().getNodeValue();
                    String v = dependency.item(2).getFirstChild().getNodeValue();

                    bw.write("compile '" + g + ":" + a + ":" + v + "'\n");
                }
                bw.write("}");

            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
            }
        }
    }
}
