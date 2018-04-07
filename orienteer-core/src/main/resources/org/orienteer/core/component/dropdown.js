(function ($) {
    function ODropdown(container, currentItemText, prepareItemStrategy, generateCurrentItemStrategy) {
        this.container = container;
        this.prepareItem = prepareItemStrategy || defaultPrepareItemStrategy;
        this.dropdown = container.find('.last').first();
        this.currentItemText = currentItemText;
        this.items = null;
        this.menu = this.createDropdownMenu();
        this.currentItem = generateCurrentItemStrategy ? generateCurrentItemStrategy(currentItemText) :
            defaultCreateCurrentItemStrategy(currentItemText);
    }

    ODropdown.prototype.createDropdownMenu = function () {
        var div = $( document.createElement('div') );
        div.addClass('dropdown-menu');
        return div;
    };

    ODropdown.prototype.render = function () {
        var self = this;
        if (self.items && self.items.length > 0) {
            var i, element;
            self.setVisible(true);
            self.setDisplayText(self.currentItemText);
            for (i = 0; i < self.items.length; i++) {
                element = self.prepareItem(self.items[i]);
                if (element.hasClass('active')) {
                    self.setDisplayText(element.find('span').first().html(), true);
                }
                self.menu.append(element);
            }
            self.dropdown.append(self.currentItem);
            self.dropdown.append(self.menu);
            self.container.append(self.dropdown);
        } else self.setVisible(false);
    };

    ODropdown.prototype.setDisplayText = function (text, active) {
        this.currentItem.find('span').first().replaceWith(text ? '<span>' + text + '</span>' : '<span></span>');
        if (active) this.currentItem.addClass('active');
        else this.currentItem.removeClass('active');
        return this;
    };

    ODropdown.prototype.setItems = function (items) {
        this.items = items;
        return this;
    };

    ODropdown.prototype.setVisible = function (visible) {
        if (this.currentItem) {
            if (visible) {
                this.currentItem.show();
            } else this.currentItem.hide();
        }
        return this;
    };

    function defaultPrepareItemStrategy(item) {
        return item.addClass('dropdown-item');
    }

    function defaultCreateCurrentItemStrategy() {
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
    }

    function ODropdownSupportedComponent(container, controller) {
        this.container = container;
        this.screenWidth = null;
        this.controller = controller;
        this.items = this.controller.getItems(container);
    }

    ODropdownSupportedComponent.prototype.render = function () {
        var width = $(window).width();
        if (width !== this.screenWidth) {
            var self = this;
            self.screenWidth = width;
            var dropdownTabs = self.computeODropdownItems(self.screenWidth, self.tabs);
            var panelTabs = self.tabs.slice(0, self.tabs.length - dropdownTabs.length);
            self.controller.getItemContainers().each(function (i) {
                if (i < panelTabs.length) {
                    var el = panelTabs[i].element;
                    el.removeClass('dropdown-item');
                    self.controller.prepareComponentItem(el);
                    $(this).append(el);
                }
            });

            self.dropdown.setItems(self.prepareDropdownTabs(dropdownTabs));
            self.dropdown.render();
        }
    };

    ODropdownSupportedComponent.prototype.computeODropdownItems = function (screenWidth, items) {
        var max = this.searchMaxItemWidth(items);
        var availableWidth = screenWidth - max - items[0].width;
        var i, computedWidth;
        for (i = 1; i < items.length; i++) {
            computedWidth = availableWidth -= items[i].width;
            if (computedWidth <= 0) {
                break;
            }
            availableWidth = computedWidth;
        }
        return i < items.length ? items.slice(i - 1) : [];
    };

    ODropdownSupportedComponent.prototype.searchMaxItemWidth = function (items) {
        var max = items[0].width;
        items.forEach(function (item) {
            if (item.width > max) {
                max = item.width;
            }
        });
        return max;
    };

    // ODropdownSupportedComponent.prototype.prepareComponentItem = function (item) {
    //     return item;
    // };
    //
    // ODropdownSupportedComponent.prototype.prepareDropdownItems = function (items) {
    //     return [];
    // };
    //
    // ODropdownSupportedComponent.prototype.getItems = function (container) {
    //     return [];
    // };
    //
    // ODropdownSupportedComponent.prototype.getItemContainers = function () {
    //
    // };

    $.fn.orienteerDropdown = function (options) {
        return new ODropdown(this, options.currentItemText, options.prepareItemStrategy, options.generateCurrentItemStrategy);
    };
})(jQuery);