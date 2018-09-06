package org.orienteer.core.boot.loader.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactReference;
import org.orienteer.core.util.CommonUtils;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class OArtifactDbUtils {

    public static final String CLASS_NAME = "OArtifact";

    public static final String PROP_GROUP_ID           = "groupId";
    public static final String PROP_ARTIFACT_ID        = "artifactId";
    public static final String PROP_VERSION            = "version";
    public static final String PROP_DESCRIPTION        = "description";
    public static final String PROP_REPOSITORY         = "repository";
    public static final String PROP_AVAILABLE_VERSIONS = "availableVersions";
    public static final String PROP_LOAD               = "load";
    public static final String PROP_TRUSTED            = "trusted";
    public static final String PROP_FILE               = "file";
    public static final String PROP_ID                 = "id";


    private OArtifactDbUtils() {}


    public static OQueryDataProvider<ODocument> getArtifactsProvider() {
        return new OQueryDataProvider<>("select from " + CLASS_NAME);
    }

    public static List<OArtifact> getNotPresentArtifactsIn(List<OArtifact> artifacts) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where not(%s in ?) or not(%s in ?) or (%s in ?)",
                    CLASS_NAME, PROP_GROUP_ID, PROP_ARTIFACT_ID, PROP_VERSION);
            ArtifactsInfo info = toArtifactsInfo(artifacts);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql), info.groupIds, info.artifactIds, info.versions);
            return CommonUtils.mapIdentifiables(identifiables, OArtifactDbUtils::fromDocument)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        });
    }

    public static ODocument toDocument(OArtifact artifact) {
        OArtifactReference reference = artifact.getArtifactReference();
        ODocument document = new ODocument(CLASS_NAME);

        document.field(PROP_GROUP_ID, reference.getGroupId());
        document.field(PROP_ARTIFACT_ID, reference.getArtifactId());
        document.field(PROP_VERSION, reference.getVersion());
        document.field(PROP_DESCRIPTION, reference.getDescription());
        document.field(PROP_REPOSITORY, reference.getRepository());
        document.field(PROP_AVAILABLE_VERSIONS, reference.getAvailableVersions());
        document.field(PROP_FILE, readOArtifactFile(reference));
        document.field(PROP_LOAD, artifact.isLoad());
        document.field(PROP_TRUSTED, artifact.isTrusted());
        document.field(PROP_ID, artifact.getId());

        return document;
    }

    public static OArtifact fromDocument(ODocument document) {
        OArtifactReference reference = new OArtifactReference(
                document.field(PROP_GROUP_ID),
                document.field(PROP_ARTIFACT_ID),
                document.field(PROP_VERSION),
                document.field(PROP_REPOSITORY),
                document.field(PROP_DESCRIPTION)
        );
        boolean load = document.field(PROP_LOAD);
        boolean trusted = document.field(PROP_TRUSTED);
        String id = document.field(PROP_ID);
        String fileName = reference.getArtifactId() + "-" + reference.getVersion() + ".jar";
        Optional<File> jarTempFile = OrienteerClassLoaderUtil.createJarTempFile(reference.getArtifactId(), fileName, document.field(PROP_FILE));

        return jarTempFile
                .map(reference::setFile)
                .map(ref -> new OArtifact(reference, load, trusted, false, id))
                .orElse(null);
    }

    public static void save(List<OArtifact> artifacts) {
        ODocument[] docs = artifacts.stream()
                .map(OArtifactDbUtils::toDocument)
                .toArray(ODocument[]::new);
        DBClosure.sudoSave(docs);
    }

    private static byte[] readOArtifactFile(OArtifactReference reference) {
        try {
            return Files.readAllBytes(reference.getFile().toPath());
        } catch (IOException ex) {
            throw new IllegalStateException("Can't read artifact jar file: " + reference, ex);
        }
    }

    private static ArtifactsInfo toArtifactsInfo(List<OArtifact> references) {
        ArtifactsInfo info = new ArtifactsInfo();
        references.forEach(artifact -> {
            info.groupIds.add(artifact.getArtifactReference().getGroupId());
            info.artifactIds.add(artifact.getArtifactReference().getArtifactId());
            info.versions.add(artifact.getArtifactReference().getVersion());
        });
        return info;
    }

    private static class ArtifactsInfo {
        public final List<String> groupIds;
        public final List<String> artifactIds;
        public final List<String> versions;

        public ArtifactsInfo() {
            this(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        }

        public ArtifactsInfo(List<String> groupIds, List<String> artifactIds, List<String> versions) {
            this.groupIds = groupIds;
            this.artifactIds = artifactIds;
            this.versions = versions;
        }
    }
}
