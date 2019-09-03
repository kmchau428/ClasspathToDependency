package dependency_generator;

import dependency_generator.generator.DependencyFileGenerator;
import dependency_generator.reader.EclipseClasspathFileReader;
import dependency_generator.reader.IClasspathFileReader;
import dependency_generator.reader.IntelliJClasspathFileReader;
import dependency_generator.searcher.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String IDE_INTELLI_J = "IntelliJ";
    public static final String IDE_ECLIPSE = "Eclipse";

    public static final String BUILD_TOOL_MAVEN = "Maven";
    public static final String BUILD_TOOL_GRADLE = "Gradle";

    public static List<String> unresolvedJars = new ArrayList<>();
    public static List<String> resolvedByNameJars = new ArrayList<>();

    public static void main(String[] args) {
        try {

            String ide = "", inputPath = "", outputPath = "", buildTool = "";
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
                else if (arg.startsWith("-b")) {
                    buildTool = arg.substring(2);
                }
            }

            IClasspathFileReader classpathFileReader = null;
            switch (ide) {
                case IDE_INTELLI_J:
                    classpathFileReader = new IntelliJClasspathFileReader();
                    break;
                case IDE_ECLIPSE:
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
            searchers.add(new MavenLocalRepoSearcher());
            searchers.add(new GradleCacheSearcher());
            searchers.add(new PublicDependencySearcher());

            DependencyFileGenerator.generateDepFile(outputPath, dependencyList, searchers, buildTool);

            System.out.println("COMPLETED");

            System.out.println("The following JARs cannot be automatically resolved:");
            for (String jar : unresolvedJars) {
                System.out.println(jar);
            }

            System.out.println("\nThe following JARs are NOT resolved by checksum (risk of delta in JAR content)");
            for (String jar : resolvedByNameJars) {
                System.out.println(jar);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
