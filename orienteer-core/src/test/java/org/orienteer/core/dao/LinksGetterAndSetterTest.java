package org.orienteer.core.dao;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(OrienteerTestRunner.class)
public class LinksGetterAndSetterTest {

  @Before
  @Sudo
  public void init() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    OSchemaHelper helper = OSchemaHelper.bind(db);

    helper.oClass("TestModel");

    helper.oClass("LinkTestModel")
            .oProperty("testModel", OType.LINK)
              .linkedClass("TestModel");
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    db.getMetadata().getSchema().dropClass("LinkTestModel");
    db.getMetadata().getSchema().dropClass("TestModel");
  }

  @Test
  @Sudo
  public void testGetterAndSetterForLink() {
    ILinkTestModel linkTestModel = IODocumentWrapper.get(ILinkTestModel.class);
    linkTestModel.fromStream(new ODocument("LinkTestModel"));

    assertNull(linkTestModel.getTestModel());

    IPureTypeTestModel testModel = IODocumentWrapper.get(IPureTypeTestModel.class);
    testModel.fromStream(new ODocument("TestModel"));

    linkTestModel.setTestModel(testModel);

    assertNotNull(linkTestModel.getTestModel());
  }

  @Test
  @Sudo
  public void testGetterAndSetterForLinkCollections() {
    ILinkTestModel linkTestModel = IODocumentWrapper.get(ILinkTestModel.class);
    linkTestModel.fromStream(new ODocument("LinkTestModel"));

    assertNull(linkTestModel.getTestModels());

    IPureTypeTestModel testModel = IODocumentWrapper.get(IPureTypeTestModel.class);
    testModel.fromStream(new ODocument("TestModel"));

    linkTestModel.setTestModels(Collections.singletonList(testModel));

    List<IPureTypeTestModel> models = linkTestModel.getTestModels();
    assertNotNull(models);
    assertFalse(models.isEmpty());

    testModel = models.get(0);
    assertNotNull(testModel);
  }
}
