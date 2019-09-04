package dependency_generator.searcher;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import dependency_generator.dto.DependencyEntry;

import java.io.File;
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

public class MavenLocalRepoSearcher implements IDependencySearcher {
    public static List<String> mavenCache = new ArrayList<>();
    Map<String,String> mavenCacheMap = new HashMap<>();
    private static final String MAVEN_CACHE_FILEPATH = System.getProperty("user.home") + "\\.m2\\repository";

    public MavenLocalRepoSearcher() {
        try (Stream<Path> walk = Files.walk(
                Paths.get(MAVEN_CACHE_FILEPATH))) {

            mavenCache = walk.filter(f -> !Files.isDirectory(f))
                    .map(c -> c.toString())
                    .filter(c -> c.endsWith(".jar"))
                    .collect(Collectors.toList());

            for (String cachePath : mavenCache) {
                File cachedJarFile = new File(cachePath);
                HashCode hc = com.google.common.io.Files.asByteSource(cachedJarFile).hash(Hashing.sha1());
                String[] array = cachePath.split("\\\\");
                String v = array[array.length-2];
                String a = array[array.length-3];
                String g = "";
                for (int i = array.length-4; i >=0; i--) {
                    if (array[i].equals("repository")) {
                        break;
                    }
                    g = array[i] + "." + g;
                }
                mavenCacheMap.put(hc.toString(), g + ":" + a + ":" + v );
//                System.out.println(g + ":" + a + ":" + v);
            }
        }
        catch (NoSuchFileException e) {
            System.out.println("The Maven local repository cannot be found!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DependencyEntry resolveDependency(String artifactId, String version, String checksum) {

        if (mavenCacheMap.containsKey(checksum)) {
            System.out.println("Resolved with Maven cache");

            String[] array = mavenCacheMap.get(checksum).split(":");
            String g = array[0];
            String a = array[1];
            String v = array[2];

            return new DependencyEntry(g,a,v);
        }

        return new DependencyEntry();

    }
}
