package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;

import java.util.List;

@ProvidedBy(ODocumentWrapperProvider.class)
public interface ILinkTestModel extends IODocumentWrapper {


  IPureTypeTestModel getTestModel();
  ILinkTestModel setTestModel(IPureTypeTestModel model);

  List<IPureTypeTestModel> getTestModels();
  ILinkTestModel setTestModels(List<IPureTypeTestModel> models);
}
