import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            IClasspathFileReader classpathFileReader = new EclipseClasspathFileReader();
            List<String> dependencyList = classpathFileReader.read("C:/projects/Test");

            System.out.println(dependencyList);

            DependencyXmlGenerator.generatePomFile("C:\\projects\\lib_cmp", dependencyList);

            DependencyGroupSearcher.findGroup("", "");
            System.out.println("COMPLETED");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
