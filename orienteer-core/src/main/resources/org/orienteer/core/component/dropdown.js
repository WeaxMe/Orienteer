(function () {
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

    $.fn.orienteerDropdown = function (currentItemText, prepareItemStrategy, generateCurrentItemStrategy) {
        return new ODropdown(this, currentItemText, prepareItemStrategy, generateCurrentItemStrategy);
    };
})();