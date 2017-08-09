/**
 * Bar for OArchitectEditor
 * @param editor editor which need sidebar
 * @param container container (html element) for bar
 * @constructor
 */
var OArchitectBar = function(editor, container) {
    this.editor = editor;
    this.container = container;
    this.barElements = [];
};

/**
 * Add item to bar
 * @param action - action which will execute when user do something with item
 * @param element - element which contains bar item
 */
OArchitectBar.prototype.addItem = function (action, element) {
    this.barElements.push({
        action: action,
        element: element
    });
};

/**
 * Add action to bar and add action to editor. Create new element for action
 * @param label label for action
 * @param actionName action name for editor and creating element
 * @param actionFunction function which will be execute on action
 */
OArchitectBar.prototype.addAction = function (label, actionName, actionFunction) {
    this.editor.addAction(actionName, actionFunction);
    this.addElementToContainer(label, actionName);
};

/**
 * Function for create element for {@link OArchitectBar}. Must be implemented by sub classes.
 */
OArchitectBar.prototype.createElement = function () {
    throw new Error('No implementation for \'createElement\' function!');
};

/**
 * Function for add element to container. Must be implemented by sub classes.
 */
OArchitectBar.prototype.addElementToContainer = function () {
    throw new Error('No implementation for \'addElementToContainer\' function!');
};

/**
 * Toolbar for {@link OArchitectEditor}. Extends from {@link OArchitectBar}
 * @param editor editor which need sidebar
 * @param container container (html element) for sidebar
 * @constructor
 */
var OArchitectToolbar = function (editor, container) {
    OArchitectBar.apply(this, arguments);
};

OArchitectToolbar.prototype = Object.create(OArchitectBar.prototype);
OArchitectToolbar.prototype.constructor = OArchitectToolbar;

OArchitectToolbar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var element = this.createElement(label, actionName);
    var editor = this.editor;
    this.container.appendChild(element);

    mxEvent.addListener(element, 'click', function (evt) {
        editor.execute(actionName);
    });
};

OArchitectToolbar.prototype.createElement = function (label, action) {
    var button = document.createElement('a');
    button.innerHTML = label;
    button.setAttribute('class', this.getCssClassByAction(action));
    button.style.margin = '5px';
    return button;
};

OArchitectToolbar.prototype.getCssClassByAction = function (action) {
    return 'btn btn-primary';
};

/**
 * Sidebar for {@link OArchitectEditor}. Extends from {@link OArchitectBar}
 * @param editor editor which need sidebar
 * @param container container (html element) for sidebar
 * @constructor
 */
var OArchitectSidebar = function (editor, container) {
    OArchitectBar.apply(this, arguments);
};

OArchitectSidebar.prototype = Object.create(OArchitectBar.prototype);
OArchitectSidebar.prototype.constructor = OArchitectSidebar;

OArchitectSidebar.prototype.addElementToContainer = function (label, actionName) {
    this.addItem(this, label, actionName);
    var element = this.createElement(label, actionName);
    this.container.appendChild(element);
    this.makeDraggable(element, actionName);
};

OArchitectSidebar.prototype.makeDraggable = function (element, actionName) {
    var editor = this.editor;
    var graph = editor.graph;
    var draggable = mxUtils.makeDraggable(element, graph, function (graph, evt, cell) {
        editor.execute(actionName, cell, evt);
    });
    draggable.getDropTarget = this.getDropTarget(actionName);
};

OArchitectSidebar.prototype.getDropTarget = function (actionName) {
    return function (graph, x, y) {
        if (actionName === OArchitectActionNames.ADD_OCLASS_ACTION) {
            return null;
        } else if (actionName === OArchitectActionNames.ADD_OPROPERTY_ACTION) {
            var cell = graph.getCellAt(x, y);
            if (graph.isSwimlane(cell))
                return cell;
            var parent = graph.getModel().getParent(cell);
            return graph.isSwimlane(parent) ? parent : null;
        } else if (actionName === OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION) {
            var cell = graph.getCellAt(x, y);
            return cell === null;
        }
    };
};

OArchitectSidebar.prototype.createElement = function (label, action) {
    var a = document.createElement('a');
    a.classList.add(OArchitectConstants.SIDEBAR_ITEM_CLASS);
    a.setAttribute('title', label);
    a.appendChild(this.getIconElementForAction(action));
    return a;
};

OArchitectSidebar.prototype.getIconElementForAction = function (action) {
    var icon = document.createElement('i');
    if (action === OArchitectActionNames.ADD_OCLASS_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_FILE_O_CLASS);
    } else if (action === OArchitectActionNames.ADD_OPROPERTY_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_ALIGN_JUSTIFY_CLASS);
    } else if (action === OArchitectActionNames.ADD_EXISTS_OCLASSES_ACTION) {
        icon.setAttribute('class', OArchitectConstants.FA_DATABASE_CLASS);
    }
    icon.classList.add(OArchitectConstants.FA_2X_CLASS);
    icon.style.margin = '5px';
    icon.style.display = 'block';
    return icon;
};