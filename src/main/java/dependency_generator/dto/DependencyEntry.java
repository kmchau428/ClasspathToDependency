package dependency_generator.dto;

public class DependencyEntry {
    String groupId;
    String artifactId;
    String verison;

    public DependencyEntry(String groupId, String artifactId, String verison) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.verison = verison;
    }

    public DependencyEntry() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVerison() {
        return verison;
    }

    public void setVerison(String verison) {
        this.verison = verison;
    }
}
