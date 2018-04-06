
(function ($) {

    function ODropdown(container) {
        this.container = container;
        this.dropdown = container.find('.last').first();
        this.items = null;
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

    ODropdown.prototype.prepareItem = function (item) {
        return item.addClass('dropdown-item');
    };

    ODropdown.prototype.render = function () {
        var self = this;
        var i, element;
        self.setVisible(true);
        self.setDisplayText(null);
        for (i = 0; i < self.items.length; i++) {
            element = self.prepareItem(self.items[i]);
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
        if (text) {
            this.currentTab.find('span').first().replaceWith('<span>' + text + '</span>');
            this.currentTab.addClass('active');
        } else {
            this.currentTab.find('span').first().replaceWith('<span></span>');
            this.currentTab.removeClass('active');
        }
        return this;
    };

    ODropdown.prototype.setItems = function (items) {
        this.items = items;
        return this;
    };

    ODropdown.prototype.setVisible = function (visible) {
        if (this.currentTab) {
            if (visible) {
                this.currentTab.show();
            } else this.currentTab.hide();
        }
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
            var panelTabs = self.tabs.slice(0, self.tabs.length - dropdownTabs.length);
            this.tabsContainer.find('li').each(function (i) {
                if (i < panelTabs.length) {
                    var el = panelTabs[i].element;
                    el.removeClass('dropdown-item');
                    el.addClass('nav-link');
                    $(this).append(el);
                }
            });
            if (dropdownTabs.length > 0) {
                self.dropdown.setItems(self.prepareDropdownTabs(dropdownTabs));
                self.dropdown.render();
            } else self.dropdown.setVisible(false);
        }
    };

    OTabbedPanel.prototype.computeDropdownTabs = function (allTabs) {
        var max = this.searchMaxTabWidth(allTabs);
        var availableWidth = this.screenWidth - max - allTabs[0].width;
        var i, computedWidth;
        for (i = 1; i < allTabs.length; i++) {
            computedWidth = availableWidth -= allTabs[i].width;
            if (computedWidth <= 0) {
                break;
            }
            availableWidth = computedWidth;
        }
        return i < allTabs.length ? allTabs.slice(i - 1) : [];
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

    OTabbedPanel.prototype.searchMaxTabWidth = function(tabs) {
        var max = tabs[0].width;
        tabs.forEach(function (t) {
            if (t.width > max) {
                max = t.width;
            }
        });
        return max;
    };

    $.fn.tabbedPanel = function (options) {
        return this.each(function () {
            var panel = new OTabbedPanel( $(this) );
            panel.render();
            var callback = $.proxy(panel.render, panel);
            var lock = false;
            $(window).resize(function() {
                if (!lock) {
                    lock = true;
                    setTimeout(function() {
                        callback();
                        lock = false;
                    }, 10);
                }
            });
        });
    };
})(jQuery);