package org.orienteer.architect.component.widget;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.architect.OArchitectModule;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;
import org.orienteer.architect.util.OClassJsonDeserializer;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Editor widget for OrientDB Schema
 */
@Widget(id="architect-editor", domain = "document", tab="schemeEditor", selector = OArchitectModule.OARCHITECTOR_CLASS, autoEnable = true)
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.CREATE)
public class OArchitectEditorWidget extends AbstractWidget<ODocument> {

    private static final Logger LOG = LoggerFactory.getLogger(OArchitectEditorWidget.class);

    private static final JavaScriptResourceReference MXGRAPH_JS = new WebjarsJavaScriptResourceReference("mxgraph/current/javascript/mxClient.min.js");
    private static final CssResourceReference MXGRAPH_CSS    = new WebjarsCssResourceReference("mxgraph/current/javascript/src/css/common.css");
    private static final CssResourceReference OARCHITECT_CSS = new CssResourceReference(OArchitectEditorWidget.class, "css/architect.css");

    private WebMarkupContainer editor;
    private WebMarkupContainer toolbar;
    private WebMarkupContainer sidebar;


    public OArchitectEditorWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(editor = new WebMarkupContainer("editor"));
        add(toolbar = new WebMarkupContainer("toolbar"));
        add(sidebar = new WebMarkupContainer("sidebar"));
        add(createConfigBehavior());
        add(createApplyEditorChangesBehavior());
    }


    private Behavior createConfigBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private final String var = "config";

            @Override
            protected void respond(AjaxRequestTarget target) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                LOG.debug("Save editor config: {}", params.getParameterValue(var));
                IModel<ODocument> model = OArchitectEditorWidget.this.getModel();
                ODocument document = model.getObject();
                document.field(OArchitectModule.CONFIG, params.getParameterValue(var));
                document.save();
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                IModel<ODocument> model = OArchitectEditorWidget.this.getModel();
                ODocument document = model.getObject();
                String xml = document.field(OArchitectModule.CONFIG);
                if (Strings.isNullOrEmpty(xml)) xml = "";
                response.render(OnLoadHeaderItem.forScript(String.format("; app.setSaveEditorConfig(%s, '%s');",
                        getCallbackFunction(CallbackParameter.explicit(var)), xml)));
            }
        };
    }

    private Behavior createApplyEditorChangesBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private final String var = "json";

            @Override
            protected void respond(AjaxRequestTarget target) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                String json = params.getParameterValue(var).toString("");
                LOG.debug("Apply editor changes: {}", json);
                List<OArchitectOClass> classes;
                try {
                    Type type = new TypeToken<List<OArchitectOClass>>(){}.getType();
                    Gson gson = new GsonBuilder().registerTypeAdapter(type, new OClassJsonDeserializer()).create();
                    classes = gson.fromJson(json, type);
                } catch (Exception ex) {
                    throw new WicketRuntimeException("Can't parse input json!", ex);
                }
                writeClassesToSchema(classes);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnLoadHeaderItem.forScript(String.format("; app.setApplyEditorChanges(%s);",
                        getCallbackFunction(CallbackParameter.explicit(var)))));
            }
        };
    }

    private void writeClassesToSchema(final List<OArchitectOClass> classes) {
        new DBClosure<Void>() {
            @Override
            protected Void execute(ODatabaseDocument db) {
                db.commit();
                OSchema schema = db.getMetadata().getSchema();
                for (OArchitectOClass oArchitectOClass : classes) {
                    String name = oArchitectOClass.getName();
                    OClass oClass = schema.getOrCreateClass(name);
                    addSuperClassesToOClass(schema, oClass, oArchitectOClass.getSuperClasses());
                    addPropertiesToOClass(schema, oClass, oArchitectOClass.getProperties());
                    LOG.debug("Create class: {}", oClass);
                }
                return null;
            }
        }.execute();
    }

    private void addSuperClassesToOClass(OSchema schema, OClass oClass, List<String> superClassNames) {
        if (superClassNames != null && !superClassNames.isEmpty()) {
            List<OClass> superClasses = Lists.newArrayList();
            for (String name : superClassNames) {
                OClass superClass = schema.getOrCreateClass(name);
                superClasses.add(superClass);
            }
            oClass.setSuperClasses(superClasses);
        }
    }

    private void addPropertiesToOClass(OSchema schema, OClass oClass, List<OArchitectOProperty> properties) {

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(OARCHITECT_CSS));
        response.render(CssReferenceHeaderItem.forReference(MXGRAPH_CSS));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/architect.js")));
        response.render(JavaScriptHeaderItem.forScript(
                String.format("; initMxGraph('%s');", "en"), null));
        response.render(JavaScriptHeaderItem.forReference(MXGRAPH_JS));
        addOArchitectDependencies(response);
    }

    private void addOArchitectDependencies(IHeaderResponse response) {
        response.render(CssReferenceHeaderItem.forReference(OARCHITECT_CSS));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor-bar.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/metadata.js")));
        response.render(OnLoadHeaderItem.forScript(String.format("; init('#%s', '#%s', '#%s');",
                editor.getMarkupId(), sidebar.getMarkupId(), toolbar.getMarkupId())));
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.edit);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.architect.editor.title");
    }

}