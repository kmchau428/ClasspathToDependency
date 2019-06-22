import reader.IClasspathFileReader;
import reader.IntelliJClasspathFileReader;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
//            reader.IClasspathFileReader classpathFileReader = new reader.EclipseClasspathFileReader();
//            List<String> dependencyList = classpathFileReader.read("C:/projects/Test");

            IClasspathFileReader classpathFileReader = new IntelliJClasspathFileReader();
            List<String> dependencyList = classpathFileReader.read("C:/projects/TestProjIntelliJ");

            System.out.println(dependencyList);

            DependencyXmlGenerator.generatePomFile("C:\\projects\\lib_cmp", dependencyList);

            System.out.println("COMPLETED");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
