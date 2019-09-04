package dependency_generator.searcher;

import dependency_generator.dto.DependencyEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LocalCacheDependencySearcher implements IDependencySearcher {
    public static final String GROUP_ID_CACHE_FILE = "groupId_cache.txt";

    static Map<String,String> cache = new HashMap<>();

    public LocalCacheDependencySearcher()
    {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(GROUP_ID_CACHE_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0)
                    continue;

                String[] jarStr = line.split(",");
                cache.put(jarStr[1] + "-" + jarStr[2], jarStr[0]);
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    @Override
    public DependencyEntry resolveDependency(String artifactId, String version, String checksum) {
        String key = artifactId + "-" + version;

        if (cache.containsKey(key)) {
            System.out.println("resolved with cache");
            return new DependencyEntry(cache.get(key), artifactId, version);
        }

        return new DependencyEntry();
    }

}
