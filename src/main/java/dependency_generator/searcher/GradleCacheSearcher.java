package dependency_generator.searcher;

import dependency_generator.dto.DependencyEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradleCacheSearcher implements IDependencySearcher {
    public static List<String> gradleCache = new ArrayList<>();
    Map<String,String> gradleCacheMap = new HashMap<>();
    private static final String GRADLE_CACHE_FILEPATH = System.getProperty("user.home") + "\\.gradle\\caches\\modules-2\\files-2.1";

    public GradleCacheSearcher() {
        try (Stream<Path> walk = Files.walk(
                Paths.get(GRADLE_CACHE_FILEPATH))) {

            gradleCache = walk.filter(Files::isDirectory)
                    .map(c -> c.toString())
                    .filter(c -> c.matches(".+[a-f0-9]{40}"))
                    .collect(Collectors.toList());



        }
        catch (NoSuchFileException e) {
            System.out.println("Gradle not available on this machine!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DependencyEntry resolveDependency(String artifactId, String version, String checksum) {
        String cacheMatched = gradleCache.stream().filter( c -> c.contains(checksum))
                .findAny()
                .orElse(null);

        if (cacheMatched != null) {
            System.out.println("Resolved with Gradle cache");

            String fileSeparator = System.getProperty("os.name").contains("Windows")? "\\\\" : "/";
            String[] array = cacheMatched.split(fileSeparator);
            String g = array[array.length-4];
            String a = array[array.length-3];
            String v = array[array.length-2];

            return new DependencyEntry(g,a,v);
        }

        return new DependencyEntry();

    }
}
