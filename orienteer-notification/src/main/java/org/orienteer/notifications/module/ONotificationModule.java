package org.orienteer.notifications.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.mail.OMailModule;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.notifications.hook.ONotificationHook;
import org.orienteer.notifications.model.*;

import java.util.List;

public class ONotificationModule extends AbstractOrienteerModule {

  public static final String NAME = "orienteer-notification";
  public static final int VERSION = 1;

  protected ONotificationModule() {
    super(NAME, VERSION, OMailModule.NAME);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);
    installNotificationStatus(helper);
    installNotificationTransport(helper);
    installNotification(helper);
    installMailNotification(helper);

    return null;
  }

  private void installNotification(OSchemaHelper helper) {

    helper.oClass(ONotificationStatusHistory.CLASS_NAME)
            .oProperty(ONotificationStatusHistory.PROP_TIMESTAMP, OType.DATETIME, 0)
              .markDisplayable()
              .markAsDocumentName()
            .oProperty(ONotificationStatusHistory.PROP_STATUS, OType.LINK, 10)
              .linkedClass(ONotificationStatus.CLASS_NAME)
              .markDisplayable()
            .oProperty(ONotificationStatusHistory.PROP_NOTIFICATION, OType.LINK, 20)
              .markAsLinkToParent()
              .markDisplayable();

    helper.oAbstractClass(ONotification.CLASS_NAME)
            .oProperty(ONotification.PROP_ID, OType.STRING, 0)
              .notNull()
            .oProperty(ONotification.PROP_STATUS, OType.LINK, 10)
              .notNull()
              .markDisplayable()
              .markAsDocumentName()
              .linkedClass(ONotificationStatus.CLASS_NAME)
            .oProperty(ONotification.PROP_TRANSPORT, OType.LINK, 20)
              .linkedClass(ONotificationTransport.CLASS_NAME)
              .markAsLinkToParent()
              .markDisplayable()
            .oProperty(ONotification.PROP_STATUS_HISTORIES, OType.LINKLIST, 30)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_TABLE);

    helper.setupRelationship(ONotification.CLASS_NAME, ONotification.PROP_STATUS_HISTORIES,
            ONotificationStatusHistory.CLASS_NAME, ONotificationStatusHistory.PROP_NOTIFICATION);
  }

  private void installMailNotification(OSchemaHelper helper) {
    helper.oClass(OMailNotification.CLASS_NAME, ONotification.CLASS_NAME)
            .oProperty(OMailNotification.PROP_PREPARED_MAIL, OType.LINK)
              .linkedClass(OPreparedMail.CLASS_NAME);
  }

  private void installNotificationStatus(OSchemaHelper helper) {
    helper.oClass(ONotificationStatus.CLASS_NAME)
            .oProperty(ONotificationStatus.PROP_NAME, OType.EMBEDDEDMAP, 0)
              .linkedType(OType.STRING)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
              .markAsDocumentName()
            .oProperty(ONotificationStatus.PROP_ALIAS, OType.STRING, 10)
              .notNull()
              .oIndex(OClass.INDEX_TYPE.UNIQUE);

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_PENDING)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.pending").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_SENDING)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.sending").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_SENT)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.sent").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_FAILED)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.failed").getObject()))
            .saveDocument();
  }

  private void installNotificationTransport(OSchemaHelper helper) {
    helper.oAbstractClass(ONotificationTransport.CLASS_NAME)
            .oProperty(ONotificationTransport.PROP_NAME, OType.EMBEDDEDMAP)
              .linkedType(OType.STRING)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
              .notNull()
              .markAsDocumentName()
              .markDisplayable()
            .oProperty(ONotificationTransport.PROP_ALIAS, OType.STRING)
              .notNull()
              .oIndex(OClass.INDEX_TYPE.UNIQUE)
              .markAsDocumentName();

    helper.oClass(OMailNotificationTransport.CLASS_NAME)
            .oProperty(OMailNotificationTransport.PROP_MAIL_SETTINGS, OType.LINK)
              .linkedClass(OMailSettings.CLASS_NAME)
              .notNull()
            .oProperty(OMailNotificationTransport.PROP_CONNECTIONS_LIMIT, OType.INTEGER)
              .notNull()
              .defaultValue("1")
              .min("1");
  }

  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }

  @Override
  public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
    super.onInitialize(app, db, moduleDoc);

    List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
    hooks.add(ONotificationHook.class);
  }

  @Override
  public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
    super.onDestroy(app, db, moduleDoc);

    List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
    hooks.remove(ONotificationHook.class);
  }
}
