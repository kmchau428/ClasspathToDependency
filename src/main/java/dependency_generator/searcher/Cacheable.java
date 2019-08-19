package dependency_generator.searcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public interface Cacheable {
    default void writeToCache(String groupId, String artifactId, String version) {
        try (FileWriter writer = new FileWriter(LocalCacheDependencySearcher.GROUP_ID_CACHE_FILE, true);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write(groupId + "," + artifactId + "," + version +"\n");

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
