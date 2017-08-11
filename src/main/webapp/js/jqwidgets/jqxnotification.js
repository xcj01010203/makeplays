/*
jQWidgets v3.8.1 (2015-June)
Copyright (c) 2011-2015 jQWidgets.
License: http://jqwidgets.com/license/
*/

(function(a) {
    a.jqx.jqxWidget("jqxNotification", "", {});
    a.extend(a.jqx._jqxNotification.prototype, {
        defineInstance: function() {
            var b = {
                width: "auto",
                height: "auto",
                left:650,//当position选择custom时生效，左边框距离
                top:450,//当position选择custom时生效，上边框距离
                appendContainer: null,
                position: "top-right",
                zIndex: 99999,
                browserBoundsOffset: 5,
                notificationOffset: 5,
                opacity: 0.9,
                hoverOpacity: 1,
                autoOpen: false,
                animationOpenDelay: 400,
                animationCloseDelay: 800,
                closeOnClick: true,
                autoClose: true,
                autoCloseDelay: 3000,
                showCloseButton: true,
                template: "info",
                icon: null,
                blink: false,
                disabled: false,
                rtl: false,
                events: ["open", "close", "click"]
            };
            a.extend(true, this, b);
            return b
        },
        createInstance: function(b) {
            var c = this;
            c.render();
            if (c.autoOpen == true) {
                c.open()
            }
        },
        render: function() {
            var c = this;
            if (c.host.hasClass("jqx-notification") == false) {
                c.host.addClass(c.toThemeProperty("jqx-rc-all"));
                c.host.addClass(c.toThemeProperty("jqx-notification"));
                c.host.css({
                    zIndex: c.zIndex,
                    display: "none",
                    opacity: c.opacity
                });
                c._container = c._notificationContainer();
                c._appendContent();
                c._setHoverFunctions();
                c._instances = new Array();
                c._instanceKey = 0;
                var b = a.data(document.body, "jqxNotifications");
                if (b == undefined) {
                    b = 0
                }
                a.data(document.body, "jqxNotifications", (b + 1))
            } else {
                c.refresh()
            }
        },
        refresh: function(b) {
            if (!b) {
                this._appendContent(true)
            }
        },
        open: function() {
            var c = this;
            if (c.disabled == false) {
                var b = c.host.clone();
                b.removeAttr("id");
                b.width(c.width);
                b.height(c.height);
                if (this.width != null && this.width.toString().indexOf("%") != -1) {
                    b.css("width", this.width)
                }
                if (this.height != null && this.height.toString().indexOf("%") != -1) {
                    b.css("height", this.height)
                }
                if (!c.appendContainer && (c.position == "bottom-left" || c.position == "bottom-right")) {
                    b.css("margin-top", c.notificationOffset);
                    c._container.prepend(b)
                } else {
                    b.css("margin-bottom", c.notificationOffset);
                    c._container.append(b)
                }
                c._addHandlers(b);
                b.fadeIn(c.animationOpenDelay,
                function() {
                    c._raiseEvent("0")
                });
                if (c.blink == true) {
                    b._blinkInterval = setInterval(function() {
                        b.fadeTo(400, c.opacity / 1.5,
                        function() {
                            b.fadeTo(400, c.opacity)
                        })
                    },
                    850)
                }
                if (c.autoClose == true) {
                    b._autoCloseTimeout = setTimeout(function() {
                        c._close(b)
                    },
                    c.autoCloseDelay)
                }
                b._key = c._instanceKey;
                c._instances[c._instanceKey] = b;
                c._instanceKey++
            }
        },
        closeAll: function() {
            var c = this;
            for (var b = 0; b < c._instances.length; b++) {
                if (c._instances[b]) {
                    c._close(c._instances[b])
                }
            }
        },
        closeLast: function() {
            var c = this;
            for (var b = c._instances.length; b >= 0; b--) {
                if (c._instances[b]) {
                    c._close(c._instances[b]);
                    break
                }
            }
        },
        destroy: function() {
            var c = this;
            c.closeAll();
            c.host.remove();
            var b = a.data(document.body, "jqxNotifications");
            a.data(document.body, "jqxNotifications", (b - 1));
            c._destroyContainers(b - 1)
        },
        propertyChangedHandler: function(c, d, h, f) {
            var e = this;
            if (f != h) {
                switch (d) {
                case "width":
                case "height":
                    var b = h.indexOf && h.indexOf("%") != -1;
                    b == undefined ? b = false: b = b;
                    var g = f.indexOf && f.indexOf("%") != -1;
                    g == undefined ? g = false: g = g;
                    if (g != b) {
                        e[d] = h
                    }
                    break;
                case "appendContainer":
                case "position":
                    e._container = e._notificationContainer();
                    break;
                case "browserBoundsOffset":
                    if (!e.appendContainer) {
                        e._position(e._container)
                    }
                    break;
                case "opacity":
                    e.host.css("opacity", f);
                    break;
                case "showCloseButton":
                case "template":
                case "icon":
                case "rtl":
                    e._appendContent(true);
                    break
                }
            }
        },
        _raiseEvent: function(h, e) {
            var g = this;
            var c = g.events[h];
            var f = new a.Event(c);
            f.owner = g;
            f.args = e;
            try {
                var b = g.host.trigger(f)
            } catch(d) {}
            return b
        },
        _close: function(b) {
            var c = this;
            if (c._instances[b._key]) {
                c._instances[b._key] = false;
                clearInterval(b._blinkInterval);
                clearTimeout(b._autoCloseTimeout);
                b.fadeOut(c.animationCloseDelay,
                function() {
                    c._removeHandlers(b);
                    b.remove();
                    c._raiseEvent("1")
                })
            }
        },
        _addHandlers: function(b) {
            var d = this;
            d.addHandler(b, "click.notification" + d.element.id,
            function(e) {
                d._raiseEvent("2");
                if (d.closeOnClick == true) {
                    d._close(b)
                }
            });
            d.addHandler(b, "mouseenter.notification" + d.element.id,
            function(e) {
                if (!b._blinkInterval) {
                    d.mouseenterFunction(b)
                }
            });
            d.addHandler(b, "mouseleave.notification" + d.element.id,
            function(e) {
                if (!b._blinkInterval && b.css("display") != "none") {
                    d.mouseleaveFunction(b)
                }
            });
            if (d.showCloseButton == true) {
                var c = b.find(".jqx-notification-close-button");
                d.addHandler(c, "click.notification" + d.element.id,
                function(e) {
                    e.stopPropagation();
                    d._close(b)
                })
            }
        },
        _removeHandlers: function(b) {
            var d = this;
            d.removeHandler(b, "click.notification" + d.element.id);
            d.removeHandler(b, "click.mouseenter" + d.element.id);
            d.removeHandler(b, "click.mouseleave" + d.element.id);
            var c = b.find(".jqx-notification-close-button");
            if (c.length > 0) {
                d.removeHandler(c, "click.notification" + d.element.id)
            }
        },
        _appendContent: function(g) {
            var j = this;
            var e;
            var b = j.host.html();
            if (!g) {
                e = j.host.children().detach()
            }
            if (g) {
                j.host.removeAttr("class");
                j.host.addClass(j.toThemeProperty("jqx-rc-all"));
                j.host.addClass(j.toThemeProperty("jqx-notification"))
            }
            var m = a("<table class='" + j.toThemeProperty("jqx-notification-table") + "'></table>");
            var d = a("<tr></tr>");
            m.append(d);
            var h = j.rtl ? "jqx-notification-content-rtl": "";
            if (!g) {
                var i = a("<td class='" + j.toThemeProperty("jqx-notification-content") + " " + h + "'></td>")
            } else {
                var i = j.host.find(".jqx-notification-content");
                b = i.html();
                e = i.detach();
                if (j.rtl) {
                    i.addClass("jqx-notification-content-rtl")
                } else {
                    i.removeClass("jqx-notification-content-rtl")
                }
            }
            d.html(i);
            var f = function() {
                if (j.rtl == false) {
                    k.addClass("jqx-notification-icon-ltr");
                    i.before(k)
                } else {
                    k.addClass("jqx-notification-icon-rtl");
                    i.after(k)
                }
            };
            var k;
            if (j.template) {
                j.host.addClass("jqx-widget");
                j.host.addClass("jqx-popup");
                j.host.addClass("jqx-notification-" + j.template);
                k = a("<td class='" + j.toThemeProperty("jqx-notification-icon") + " jqx-notification-icon-" + j.template + "'></td>");
                f()
            } else {
                j.host.addClass(j.toThemeProperty("jqx-widget"));
                j.host.addClass(j.toThemeProperty("jqx-popup"));
                j.host.addClass(j.toThemeProperty("jqx-fill-state-normal"));
                if (j.icon) {
                    k = a("<td class='" + j.toThemeProperty("jqx-notification-icon") + "'></td>");
                    j.icon.padding = j.icon.padding ? parseInt(j.icon.padding) : 5;
                    k.css({
                        width: (parseInt(j.icon.width) + j.icon.padding),
                        height: j.icon.height,
                        "background-image": "url('" + j.icon.url + "')"
                    });
                    f()
                }
            }
            if (j.showCloseButton == true) {
                var l;
                if (j.template) {
                    l = "jqx-notification-close-button jqx-notification-close-button-" + j.template
                } else {
                    l = j.toThemeProperty("jqx-icon-close") + " " + j.toThemeProperty("jqx-notification-close-button")
                }
                var c = a("<td class='" + j.toThemeProperty("jqx-notification-close-button-container") + "'><div class='" + l + " " + j.element.id + "CloseButton' title='Close'></div></td>");
                if (j.rtl == false) {
                    c.find("div").addClass("jqx-notification-close-button-ltr");
                    i.after(c)
                } else {
                    c.find("div").addClass("jqx-notification-close-button-rtl");
                    i.before(c)
                }
            }
            j.host[0].innerHTML = "";
            j.host.append(m);
            if (e.length > 0) {
                j.host.find(".jqx-notification-content").append(e)
            } else {
                j.host.find(".jqx-notification-content").html(b)
            }
        },
        _position: function(b) {
            var e = this;
            var g;
            var d;
            var f;
            var c;
            switch (e.position) {
            case "top-right":
                g = "";
                d = e.browserBoundsOffset;
                f = e.browserBoundsOffset;
                c = "";
                break;
            case "top-left":
                g = e.browserBoundsOffset;
                d = "";
                f = e.browserBoundsOffset;
                c = "";
                break;
            case "bottom-left":
                g = e.browserBoundsOffset;
                d = "";
                f = "";
                c = e.browserBoundsOffset;
                break;
            case "bottom-right":
                g = "";
                d = e.browserBoundsOffset;
                f = "";
                c = e.browserBoundsOffset;
                break;
            case "custom":
            	g = e.left;
                d = "";//e.browserBoundsOffset;
                f = e.top;//e.browserBoundsOffset;
                c = "";
                break;
            }
            b.css({
                left: g,
                right: d,
                top: f,
                bottom: c
            })
        },
        _notificationContainer: function() {
            var c = this;
            var b;
            if (!c.appendContainer) {
                b = a("#jqxNotificationDefaultContainer-" + c.position);
                if (b.length == 0) {
                    a("body").append("<div id='jqxNotificationDefaultContainer-" + c.position + "' class='jqx-notification-container' style='z-index:99999;'></div>");
                    b = a("#jqxNotificationDefaultContainer-" + c.position);
                    if (c.width.indexOf && c.width.indexOf("%") != -1) {
                        b.addClass(c.toThemeProperty("jqx-notification-container-full-width"))
                    }
                    if (c.height.indexOf && c.height.indexOf("%") != -1) {
                        b.addClass(c.toThemeProperty("jqx-notification-container-full-height"))
                    }
                    c._position(b)
                }
            } else {
                b = a(c.appendContainer);
                if (b.length == 0) {
                    throw new Error("jqxNotification: Invalid appendContainer jQuery Selector - " + c.appendContainer + "! Please, check whether the used ID or CSS Class name is correct.")
                }
            }
            return b
        },
        _destroyContainers: function(b) {
            if (b == 0) {
                a(".jqx-notification-container").remove()
            }
        },
        _setHoverFunctions: function() {
            var b = this;
            var c = false;
            if (a.jqx.browser.browser == "msie" && a.jqx.browser.version == "9.0") {
                c = true
            }
            if (c == false) {
                b.mouseenterFunction = function(d) {
                    d.fadeTo("fast", b.hoverOpacity)
                };
                b.mouseleaveFunction = function(d) {
                    d.fadeTo("fast", b.opacity)
                }
            } else {
                b.mouseenterFunction = function(d) {
                    d.css("filter", "alpha(opacity = " + (b.hoverOpacity * 100) + ")")
                };
                b.mouseleaveFunction = function(d) {
                    d.css("filter", "alpha(opacity = " + (b.opacity * 100) + ")")
                }
            }
        }
    })
})(jqxBaseFramework);