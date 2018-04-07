
(function ($) {

    function OTabbedPanel(container, options) {
        this.tabsContainer = container;
        this.screenWidth = null;
        this.tabs = this.searchTabs(this.tabsContainer);
        this.dropdown = $(this.tabsContainer).orienteerDropdown(options ? options.text : null);
    }

    OTabbedPanel.prototype.render = function () {
        var width = $(window).width();
        if (width !== this.screenWidth) {
            var self = this;
            self.screenWidth = width;
            var dropdownTabs = $().computeODropdownItems(self.screenWidth, self.tabs);
            var panelTabs = self.tabs.slice(0, self.tabs.length - dropdownTabs.length);
            this.tabsContainer.find('li').each(function (i) {
                if (i < panelTabs.length) {
                    var el = panelTabs[i].element;
                    el.removeClass('dropdown-item');
                    el.addClass('nav-link');
                    $(this).append(el);
                }
            });

            self.dropdown.setItems(self.prepareDropdownTabs(dropdownTabs));
            self.dropdown.render();
        }
    };

    OTabbedPanel.prototype.prepareDropdownTabs = function (tabs) {
        var elements = [];
        tabs.forEach(function(tab) {
            var el = tab.element;
            elements.push(
                el.removeClass('nav-link')
            );
        });
        return elements;
    };

    OTabbedPanel.prototype.searchTabs = function (container) {
        var tabs = [];
        container.find('li').each(function () {
            var tab = $(this);
            tabs.push({
                element: tab.find('a').first(),
                width: tab.width()
            });
        });
        return tabs;
    };

    $.fn.tabbedPanel = function (options) {
        return this.each(function () {
            var panel = new OTabbedPanel($(this), options);
            panel.render();
            var callback = $.proxy(panel.render, panel);
            $(window).resize(callback);
        });
    };
})(jQuery);