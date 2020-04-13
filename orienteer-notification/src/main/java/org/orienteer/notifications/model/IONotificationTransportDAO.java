package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

/**
 * Provides access for class {@link IONotificationTransport#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface IONotificationTransportDAO {

  @Query("select from ONotificationTransport where status = :status")
  ODocument getTransportByAlias(String alias);

}
