package org.orienteer.core.dao;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class GenericTypeTest {

  private static final Logger LOG = LoggerFactory.getLogger(GenericTypeTest.class);

  @Test
  public void test() {
    List<String> list = new ArrayList<>();
    Class<?> listClass = list.getClass();



    for (Method method : listClass.getMethods()) {
      if (method.getName().equals("add")) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        LOG.info("parameter types: {}", parameterTypes);
      }

    }


    TypeVariable<? extends Class<?>>[] typeParameters = listClass.getTypeParameters();

    LOG.info("list class: {}", listClass);
    LOG.info("Component type: {}", listClass.getComponentType());

//    LOG.info("Type parameters: {}", );
  }


}
