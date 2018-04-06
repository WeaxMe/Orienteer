package org.orienteer.core.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * Bootstrap and AJAX enabled {@link org.apache.wicket.extensions.markup.html.tabs.TabbedPanel}
 *
 * @param <T> The type of panel to be used for this component's tabs. Just use {@link ITab} if you
 *            have no special needs here.
 */
public class TabbedPanel<T extends ITab> extends org.apache.wicket.extensions.markup.html.tabs.TabbedPanel<T> {

	public static final CssResourceReference TABBED_PANEL_CSS          = new CssResourceReference(TabbedPanel.class, "tabbed-panel.js");
	public static final JavaScriptResourceReference TABBED_PANEL_JS    = new JavaScriptResourceReference(TabbedPanel.class, "tabbed-panel.js");
    public static final JavaScriptResourceReference ORIENTEER_DROPDOWN_JS = new JavaScriptResourceReference(TabbedPanel.class, "dropdown.js");

	private boolean hideIfSingle=true;

	public TabbedPanel(String id, List<T> tabs, IModel<Integer> model) {
		super(id, tabs, model);
	}

	public TabbedPanel(String id, List<T> tabs) {
		super(id, tabs);
	}
	
	public boolean isHideIfSingle() {
		return hideIfSingle;
	}

	public TabbedPanel<T> setHideIfSingle(boolean hideIfSingle) {
		this.hideIfSingle = hideIfSingle;
		return this;
	}

	@Override
	protected String getSelectedTabCssClass() {
		return "active";
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		get("tabs-container").setVisibilityAllowed(!(hideIfSingle && getTabs().size()<2));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(TABBED_PANEL_CSS));
		response.render(JavaScriptHeaderItem.forReference(TABBED_PANEL_JS));
		response.render(JavaScriptHeaderItem.forReference(ORIENTEER_DROPDOWN_JS));
		response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s>.card>.card-header>ul')" +
				".tabbedPanel({'text':'%s'})", getMarkupId(), new ResourceModel("panel.tab.other").getObject())));
	}
	
	@Override
	protected WebMarkupContainer newLink(String linkId, final int index) {
		return new AjaxLink<Void>(linkId) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setSelectedTab(index);
				target.add(TabbedPanel.this);
				onLinkClick(target);
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String cssClass = tag.getAttribute("class");
				if (getSelectedTab() == index) {
					cssClass += " active";
				} else cssClass = "nav-link";
				tag.put("class", cssClass.trim());
			}
		};

	}
	
	protected void onLinkClick(AjaxRequestTarget target){
		
	}
	
	@Override
	protected WebMarkupContainer newTabsContainer(String id) {
		return new WebMarkupContainer(id);
	}
}
