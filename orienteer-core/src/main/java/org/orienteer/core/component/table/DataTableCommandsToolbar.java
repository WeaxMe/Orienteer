package org.orienteer.core.component.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.TabbedPanel;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.MethodsView;
import org.orienteer.core.method.MethodPlace;

/**
 * {@link AbstractToolbar} to collect {@link Command}s
 *
 * @param <T> the type of a data table objects
 */
public class DataTableCommandsToolbar<T> extends AbstractToolbar implements ICommandsSupportComponent<T> {
	private static final long serialVersionUID = 1L;

	public static final JavaScriptResourceReference ORIENTEER_COMMANDS_JS = new JavaScriptResourceReference(DataTableCommandsToolbar.class, "commands.js");

	private RepeatingView commands;
    public DataTableCommandsToolbar(DataTable<T, ?> table)
    {
        super(table);
        WebMarkupContainer span = new WebMarkupContainer("span");
        span.add(new AttributeModifier("colspan", new Model<String>(String.valueOf(table.getColumns().size()))));
        commands = new RepeatingView("commands");
        span.add(commands);
        add(span);
    }
    
    @Override
	public DataTableCommandsToolbar<T> addCommand(Command<T> command) {
		commands.add(command);
		return this;
	}

	@Override
	public DataTableCommandsToolbar<T> removeCommand(Command<T> command) {
		commands.remove(command);
		return this;
	}

	@Override
	public String newCommandId() {
		return commands.newChildId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataTable<T, ?> getTable() {
		return (DataTable<T, ?>)super.getTable();
	}

	@Override
    protected void onInitialize() {
    	super.onInitialize();
		MethodsView methods = new MethodsView(commands, getDefaultModel(),MethodPlace.DATA_TABLE,getTable());
		methods.loadMethods();
    }

	@Override
	protected void onConfigure() {
		super.onConfigure();
		IVisitor<Component, Boolean> visitor = new IVisitor<Component, Boolean>()
        {
            public void component(Component component, IVisit<Boolean> visit)
            {
            	component.configure();
                if(component.determineVisibility())
                {
                    visit.stop(true);
                }
                else
                {
                	visit.dontGoDeeper();
                }
            }
        };
		Boolean ret = commands.visitChildren(visitor);
		setVisible(ret!=null?ret:false);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(TabbedPanel.ORIENTEER_DROPDOWN_JS));
		response.render(JavaScriptHeaderItem.forReference(ORIENTEER_COMMANDS_JS));
	}
}