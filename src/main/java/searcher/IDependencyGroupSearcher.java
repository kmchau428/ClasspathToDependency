package searcher;

public interface IDependencyGroupSearcher {
    String UNCLASSIFIED = "unclassified";

    String findGroupId(String artifactId, String version);
}
