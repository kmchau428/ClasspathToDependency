package dependency_generator;

import dependency_generator.reader.EclipseClasspathFileReader;
import dependency_generator.reader.IClasspathFileReader;
import dependency_generator.reader.IntelliJClasspathFileReader;
import dependency_generator.searcher.IDependencySearcher;
import dependency_generator.searcher.LocalCacheDependencySearcher;
import dependency_generator.searcher.PublicDependencySearcher;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<String> unresolvedJars = new ArrayList<>();

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

            System.out.println("Dependency List:");
            System.out.println(dependencyList);

            //New groupId dependency_generator.searcher goes here
            List<IDependencySearcher> searchers = new ArrayList<>();
            searchers.add(new LocalCacheDependencySearcher());
            searchers.add(new PublicDependencySearcher());

            DependencyXmlGenerator.generatePomFile(outputPath, dependencyList, searchers);

            System.out.println("COMPLETED");

            System.out.println("The following JARs cannot be automatically resolved:");
            for (String jar : unresolvedJars) {
                System.out.println(jar);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
