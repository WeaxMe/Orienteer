(function () {
    function ODropdown(container, prepareItemStrategy, generateCurrentItemStrategy) {
        this.container = container;
        this.prepareItem = prepareItemStrategy || defaultPrepareItemStrategy;
        this.dropdown = container.find('.last').first();
        this.items = null;
        this.menu = this.createDropdownMenu();
        this.currentItem = generateCurrentItemStrategy ? generateCurrentItemStrategy() : defaultCreateCurrentItemStrategy();
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
            self.setDisplayText(null);
            for (i = 0; i < self.items.length; i++) {
                element = self.prepareItem(self.items[i]);
                if (element.hasClass('active')) {
                    self.setDisplayText(element.find('span').first().html());
                }
                self.menu.append(element);
            }
            self.dropdown.append(self.currentItem);
            self.dropdown.append(self.menu);
            self.container.append(self.dropdown);
        } else self.setVisible(false);
    };

    ODropdown.prototype.setDisplayText = function (text) {
        if (text) {
            this.currentItem.find('span').first().replaceWith('<span>' + text + '</span>');
            this.currentItem.addClass('active');
        } else {
            this.currentItem.find('span').first().replaceWith('<span></span>');
            this.currentItem.removeClass('active');
        }
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

    $.fn.orienteerDropdown = function (prepareItemStrategy, generateCurrentItemStrategy) {
        return new ODropdown(this, prepareItemStrategy, generateCurrentItemStrategy);
    };
})();