
function OrienteerTabbedPanel(id) {

    function ODropdown(container, tabs) {
        this.container = container;
        this.tabs = tabs;
        this.dropdown = $( document.createElement('li') );
        this.currentTab = this.createCurrentTabElement();
        this.menu = this.createDropdownMenu();
    }

    ODropdown.prototype.createCurrentTabElement = function () {
        var a = $( document.createElement('a') );
        a.attr({
            'class': 'nav-link dropdown-toggle',
            'data-toggle': 'dropdown',
            'href': '#',
            'role': 'button',
            'aria-haspopup': 'true',
            'aria-expanded': 'false'
        });
        a.append('<span></span>');
        return a;
    };

    ODropdown.prototype.createDropdownMenu = function () {
        var div = $( document.createElement('div') );
        div.addClass('dropdown-menu');
        return div;
    };

    ODropdown.prototype.createMenuItem = function (content) {
        return content.find('a').first()
            .removeClass('nav-link')
            .addClass('dropdown-item');
    };

    ODropdown.prototype.render = function () {
        var self = this;
        var i, element;
        var onClick = self.createOnTabClick();
        for (i = 0; i < self.tabs.length; i++) {
            element = self.createMenuItem(self.tabs[i]);
            if (element.hasClass('active')) {
                self.setDisplayText(element.find('span').first().html());
            }
            self.menu.append(element);
        }
        self.dropdown.append(self.currentTab);
        self.dropdown.append(self.menu);
        self.container.append(self.dropdown);
    };

    ODropdown.prototype.createOnTabClick = function () {
        var self = this;
        return function () {
            var tab = $(this);
            var text = tab.find('span').first().html();
            self.setDisplayText(text);
        };
    };

    ODropdown.prototype.setDisplayText = function (text) {
        this.currentTab.find('span').first().replaceWith('<span>' + text + '</span>');
        this.currentTab.addClass('active');
        return this;
    };



    function TabbedHandler() {
        this.previousWidth = -1;
        this.dropdown = null;
    }

    TabbedHandler.prototype.constructor = TabbedHandler;

    TabbedHandler.prototype.handleTabsForCurrentScreen = function() {
        var width = $(window).width();
        if (width !== this.previousWidth) {
            this.previousWidth = width;
            var container = $('#' + id + '>.card>.card-header>ul');
            var tabs = this.searchTabs(container);
            var dropdownTabs = this.computeDropdownTabs(tabs, width);
            this.dropdown = new ODropdown(container, dropdownTabs);
            this.dropdown.render();
        }
    };

    TabbedHandler.prototype.searchTabs = function (container) {
        var tabs = [];
        container.find('li').each(function () {
            tabs.push( $(this) );
        });
        return tabs;
    };

    TabbedHandler.prototype.computeDropdownTabs = function (tabs, containerWidth) {
        var max = this.searchMaxTabWidth(tabs);
        var availableWidth = containerWidth - max;
        var firstWidth = tabs[0].width();
        var i;
        for (i = 1; i < tabs.length; i++) {
            if (availableWidth <= firstWidth) {
                break;
            }
            availableWidth -= tabs[i].width();
        }

        return tabs.slice(i - 1);
    };

    TabbedHandler.prototype.searchMaxTabWidth = function(tabs) {
        var max = tabs[0].width();
        var width;
        tabs.forEach(function (t) {
            width = t.width();
            if (width > max) {
                max = width;
            }
        });
        return max;
    };

    var handler = new TabbedHandler();
    handler.handleTabsForCurrentScreen();
    $(window).resize(handler.handleTabsForCurrentScreen);
}