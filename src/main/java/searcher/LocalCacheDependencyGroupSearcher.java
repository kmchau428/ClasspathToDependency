package searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LocalCacheDependencyGroupSearcher implements IDependencyGroupSearcher {
    public static final String GROUP_ID_CACHE_FILE = "groupId_cache.txt";

    Map<String,String> cache = new HashMap<>();

    public LocalCacheDependencyGroupSearcher()
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
    public String findGroupId(String artifactId, String version) {
        String key = artifactId + "-" + version;
        if (cache.containsKey(key)) {
            System.out.println("resolved with cache");
            return cache.get(key);
        }

        return IDependencyGroupSearcher.UNCLASSIFIED;
    }

}
