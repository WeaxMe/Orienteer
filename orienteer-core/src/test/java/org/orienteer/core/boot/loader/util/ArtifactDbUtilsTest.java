package org.orienteer.core.boot.loader.util;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArtifactDbUtilsTest {

    private static List<OArtifact> artifacts;

    @BeforeClass
    public static void init() {

    }

    @AfterClass
    public static void destroy() {

    }

    @Test
    public void testArtifactsProvider() {
        OQueryDataProvider<ODocument> provider = OArtifactDbUtils.getArtifactsProvider();
        assertTrue(provider.size() > 0);
    }

    @Test
    public void testToDocument() {

    }

    @Test
    public void testFromDocument() {

    }
}
