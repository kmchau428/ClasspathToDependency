package dependency_generator.searcher;

import dependency_generator.dto.DependencyEntry;

public interface IDependencySearcher {

    DependencyEntry resolveDependency(String artifactId, String version, String checksum);
}
