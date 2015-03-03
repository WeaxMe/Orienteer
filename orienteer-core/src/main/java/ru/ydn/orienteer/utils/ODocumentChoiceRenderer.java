package ru.ydn.orienteer.utils;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.services.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentChoiceRenderer implements IChoiceRenderer<ODocument>
{
	private IOClassIntrospector oClassIntrospector;
	
	public ODocumentChoiceRenderer()
	{
		this(OrienteerWebApplication.get().getOClassIntrospector());
	}
	
	@Inject
	public ODocumentChoiceRenderer(IOClassIntrospector oClassIntrospector)
	{
		this.oClassIntrospector = oClassIntrospector;
	}
	
	@Override
	public Object getDisplayValue(ODocument object) {
		return oClassIntrospector.getDocumentName(object);
	}

	@Override
	public String getIdValue(ODocument object, int index) {
		return Integer.toString(index);
	}

}
