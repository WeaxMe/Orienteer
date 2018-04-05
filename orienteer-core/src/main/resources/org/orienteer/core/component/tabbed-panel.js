
(function ($) {

    function ODropdown(container) {
        this.container = container;
        this.dropdown = container.find('.last').first();
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
            .addClass('dropdown-item');
    };

    ODropdown.prototype.render = function (items) {
        var self = this;
        var i, element;
        for (i = 0; i < items.length; i++) {
            element = self.createMenuItem(items[i]);
            if (element.hasClass('active')) {
                self.setDisplayText(element.find('span').first().html());
            }
            self.menu.append(element);
        }
        self.dropdown.append(self.currentTab);
        self.dropdown.append(self.menu);
        self.container.append(self.dropdown);
    };

    ODropdown.prototype.setDisplayText = function (text) {
        this.currentTab.find('span').first().replaceWith('<span>' + text + '</span>');
        this.currentTab.addClass('active');
        return this;
    };

    function OTabbedPanel(container) {
        this.tabsContainer = container;
        this.screenWidth = null;
        this.tabs = this.searchTabs(this.tabsContainer);
        this.dropdown = new ODropdown(this.tabsContainer);
    }

    OTabbedPanel.prototype.render = function () {
        var width = $(window).width();
        if (width !== this.screenWidth) {
            var self = this;
            self.screenWidth = width;
            var dropdownTabs = self.computeDropdownTabs(self.tabs);
            var tabs = self.tabs.slice(0, self.tabs.length - dropdownTabs.length);
            if (dropdownTabs.length <= 1) {
                return;
            }
            // tabs.forEach(function (tab) {
            //     if (!tab.hasClass('nav-item')) {
            //         var li = $( document.createElement('li') );
            //         li.addClass('nav-item');
            //         tab.removeClass('dropdown-item').addClass('nav-link');
            //         li.addClass(tab);
            //         self.tabsContainer.append(li);
            //     }
            // });

            self.prepareDropdownTabs(dropdownTabs);
            self.dropdown.render(dropdownTabs);
        }
    };

    OTabbedPanel.prototype.computeDropdownTabs = function (allTabs) {
        var max = this.searchMaxTabWidth(allTabs);
        var availableWidth = this.screenWidth - max;
        var firstWidth = allTabs[0].width();
        var i;
        for (i = 1; i < allTabs.length; i++) {
            if (availableWidth <= firstWidth) {
                break;
            }
            availableWidth -= allTabs[i].width();
        }
        return allTabs.slice(i - 1);
    };

    OTabbedPanel.prototype.prepareDropdownTabs = function (tabs) {
        tabs.forEach(function(tab) {
            tab.find('a').first().removeClass('nav-link');
        });
        return tabs;
    };

    OTabbedPanel.prototype.searchTabs = function (container) {
        var tabs = [];
        container.find('li').each(function () {
            tabs.push( $(this) );
        });
        return tabs;
    };

    OTabbedPanel.prototype.searchMaxTabWidth = function(tabs) {
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


    $.fn.tabbedPanel = function (options) {
        return this.each(function () {
            var panel = new OTabbedPanel( $(this) );
            panel.render();
            var callback = $.proxy(panel.render, panel);
            $(window).resize(callback);
        });
    };
})(jQuery);