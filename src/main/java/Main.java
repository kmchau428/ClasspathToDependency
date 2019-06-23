import reader.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            String ide = "", inputPath = "", outputPath = "";
            for (String arg : args) {
                if (arg.startsWith("-i")) {
                    ide = arg.substring(2);
                }
                else if (arg.startsWith("-s")) {
                    inputPath = arg.substring(2);
                }
                else if (arg.startsWith("-o")) {
                    outputPath = arg.substring(2);
                }
            }

            IClasspathFileReader classpathFileReader = null;
            switch (ide) {
                case "IntelliJ":
                    classpathFileReader = new IntelliJClasspathFileReader();
                    break;
                case "Eclipse":
                    classpathFileReader = new EclipseClasspathFileReader();
                    break;
                default:
                    break;
            }
            List<String> dependencyList = classpathFileReader.read(inputPath);

            //System.out.println(dependencyList);

            DependencyXmlGenerator.generatePomFile(outputPath, dependencyList);

            System.out.println("COMPLETED");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
