package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.util.CommonUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * {@link IMethodHandler} to SET field value to {@link ODocumentWrapper}
 */
public class ODocumentSetHandler extends AbstractMethodHandler<ODocumentWrapper> {

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().startsWith("set") && args.length == 1) {
      String fieldName = CommonUtils.decapitalize(method.getName().substring(3));
      Object value = args[0];
      setField(target.getDocument(), fieldName, value);
      return returnChained(proxy, method);
    }
    return null;
  }

  private void setField(ODocument document, String fieldName, Object value) {
    OType type = OType.getTypeByValue(value);
    Object finalValue = value;

    if (type != null) {
      switch (type) {
        case CUSTOM:
          finalValue = convertToLink(value);
          break;
        case LINKLIST:
        case EMBEDDEDLIST:
        case LINKSET:
        case EMBEDDEDSET:
          finalValue = convertToLinkCollection(value);
          break;
      }
    }

    document.field(fieldName, finalValue);
  }

  private Object convertToLink(Object value) {
    if (value instanceof IODocumentWrapper) {
      IODocumentWrapper wrapper = (IODocumentWrapper) value;
      return wrapper.getDocument();
    }
    throw new IllegalStateException("Can't convert value to link: " + value);
  }

  private Object convertToLinkCollection(Object value) {
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.isEmpty()) {
        return collection;
      }
      Iterator<?> iterator = collection.iterator();
      Object element = iterator.next();
      if (element instanceof IODocumentWrapper) {
        Collection<Object> resultCollection = null;

        if (collection instanceof List) {
          resultCollection = new LinkedList<>();
        } else if (collection instanceof Set) {
          resultCollection = new LinkedHashSet<>();
        }

        if (resultCollection != null) {
          resultCollection.add(convertToLink(element));
          while (iterator.hasNext()) {
            resultCollection.add(convertToLink(iterator.next()));
          }
          return resultCollection;
        }
      } else {
        return collection;
      }
    }
    throw new IllegalStateException("Can't convert value to link collection: " + value);
  }
}