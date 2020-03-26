package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * {@link IMethodHandler} to GET field value from {@link ODocumentWrapper}
 */
public class ODocumentGetHandler implements IMethodHandler<ODocumentWrapper> {

  private static final Logger LOG = LoggerFactory.getLogger(ODocumentGetHandler.class);

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (args.length == 0) {
      String fieldName = null;
      String methodName = method.getName();
      if (methodName.startsWith("get")) {
      	fieldName = CommonUtils.decapitalize(methodName.substring(3));
			}
      if (methodName.startsWith("is")) {
      	fieldName = CommonUtils.decapitalize(methodName.substring(2));
			}
      if (fieldName != null) {
        Object value = getValue(target.getDocument(), fieldName, method.getReturnType());
        return new ResultHolder(value);
      }
    }
    return null;
  }

  private Object getValue(ODocument document, String field, Class<?> returnType) {
    Object value = document.field(field);

    if (value == null) {
      return null;
    }

    OType typeByValue = OType.getTypeByValue(value);

    switch (typeByValue) {
      case EMBEDDED:
      case LINK:
        return convertToLink(value, returnType);
      case EMBEDDEDLIST:
      case LINKLIST:
        break;
      case EMBEDDEDSET:
      case LINKSET:
        break;
      case EMBEDDEDMAP:
      case LINKMAP:
        break;
    }

    return OType.convert(value, returnType);
  }

  @SuppressWarnings("unchecked")
  private Object convertToLink(Object value, Class<?> type) {
    if (value instanceof OIdentifiable && IODocumentWrapper.class.isAssignableFrom(type)) {
      OIdentifiable identifiable = (OIdentifiable) value;
      ODocument document = identifiable.getRecord();
      IODocumentWrapper wrapper = IODocumentWrapper.get((Class<? extends IODocumentWrapper>) type);
      wrapper.fromStream(document);
      return wrapper;
    }
    throw new IllegalStateException("Can't convert to link. Value: " + value + " target type: " + type);
  }

  private Object convertToLinkList(Object value, Class<?> type) {
    if (value instanceof Collection) {
      Collection<Object> result = null;

      if (List.class.isAssignableFrom(type)) {
        result = new LinkedList<>();
      } else if (Set.class.isAssignableFrom(type)) {
        result = new LinkedHashSet<>();
      }

      if (result != null) {
        Collection<?> collection = (Collection<?>) value;
        if (collection.isEmpty()) {
          return result;
        }

        Iterator<?> iterator = collection.iterator();
        Object element = iterator.next();
        if (element instanceof IODocumentWrapper) {



//          result.add(convertToLink(element, ));
//          while (iterator.hasNext()) {
//
//          }
        }
        return collection;
      }


    }
    throw new IllegalStateException("Can't convert value to type:" + type + " value: " + value);
  }


}
