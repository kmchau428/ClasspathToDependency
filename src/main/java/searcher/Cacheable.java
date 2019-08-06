package searcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public interface Cacheable {
    default void writeToCache(String artifactId, String version, String groupId) {
        try (FileWriter writer = new FileWriter(LocalCacheDependencyGroupSearcher.GROUP_ID_CACHE_FILE, true);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write(groupId + "," + artifactId + "," + version +"\n");

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
