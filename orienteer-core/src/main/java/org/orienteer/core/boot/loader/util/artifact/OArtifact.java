package org.orienteer.core.boot.loader.util.artifact;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class which contains all information about Orienteer outside module.
 */
public class OArtifact implements Comparable<OArtifact>, Serializable {
    private OArtifactReference artifact;
    private OArtifactReference previousArtifact;
    private List<OArtifactReference> dependencies;
    private final String id;
    private boolean load;
    private boolean trusted;
    private boolean downloaded; // optional need only for Orienteer default modules

    public OArtifact(String id) {
        this(null, id);
    }

    public OArtifact(OArtifactReference artifact, String id) {
        this(artifact, false, false, false, id);
    }

    public OArtifact(OArtifactReference artifact, boolean load, boolean trusted, boolean downloaded, String id) {
        this.artifact = artifact;
        this.load = load;
        this.trusted = trusted;
        this.downloaded = downloaded;
        this.id = id;
        if (artifact != null) {
            this.previousArtifact = new OArtifactReference(this.artifact.getGroupId(), this.artifact.getArtifactId(),
                    this.getArtifactReference().getVersion(), this.artifact.getRepository(), this.artifact.getDescription(), this.artifact.getFile());
        }
    }

    public static OArtifact getEmptyOArtifact() {
        OArtifactReference artifact = new OArtifactReference("", "", "");
        return new OArtifact(artifact, UUID.randomUUID().toString());
    }

    public OArtifact setArtifactReference(OArtifactReference artifact) {
        this.artifact = artifact;
        this.previousArtifact = new OArtifactReference(this.artifact.getGroupId(), this.artifact.getArtifactId(),
                this.getArtifactReference().getVersion(), this.artifact.getRepository(), this.artifact.getDescription(), this.artifact.getFile());
        return this;
    }

    public OArtifact setDependencies(List<OArtifactReference> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public OArtifact setLoad(boolean load) {
        this.load = load;
        return this;
    }

    public OArtifact setTrusted(boolean trusted) {
        this.trusted = trusted;
        return this;
    }

    public OArtifact setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public OArtifactReference getArtifactReference() {
        return artifact;
    }

    public OArtifactReference getPreviousArtifactRefence() {
        return previousArtifact;
    }

    public List<OArtifactReference> getDependencies() {
        return dependencies;
    }

    public boolean isLoad() {
        return load;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OArtifact oArtifact = (OArtifact) o;
        return load == oArtifact.load &&
                trusted == oArtifact.trusted &&
                downloaded == oArtifact.downloaded &&
                Objects.equals(artifact, oArtifact.artifact) &&
                Objects.equals(previousArtifact, oArtifact.previousArtifact) &&
                Objects.equals(dependencies, oArtifact.dependencies) &&
                Objects.equals(id, oArtifact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifact, previousArtifact, dependencies, id, load, trusted, downloaded);
    }

    @Override
    public String toString() {
        return "OArtifact{" +
                "artifact=" + artifact +
                ", previousArtifact=" + previousArtifact +
                ", dependencies=" + dependencies +
                ", id='" + id + '\'' +
                ", load=" + load +
                ", trusted=" + trusted +
                ", downloaded=" + downloaded +
                '}';
    }

    @Override
    public int compareTo(OArtifact moduleMetadata) {
        String groupId = moduleMetadata.getArtifactReference().getGroupId();
        int result = artifact.getGroupId().compareTo(groupId);
        if (result == 0) {
            String artifactId = moduleMetadata.getArtifactReference().getArtifactId();
            result = artifact.getArtifactId().compareTo(artifactId);
        }
        if (result == 0) {
            String version = moduleMetadata.getArtifactReference().getVersion();
            result = artifact.getVersion().compareTo(version);
        }
        if (result == 0) {
            String id = moduleMetadata.getId();
            result = this.id.compareTo(id);
        }
        return result;
    }
}
