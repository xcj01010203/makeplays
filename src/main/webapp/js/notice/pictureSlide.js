$(document).ready(function(){
	
});
var initPhotoSwipeFromDOM = function(gallerySelector) {
                //获得相应的数据
                
                var parseThumbnailElements = function(el) {
                    var thumbElements = el.childNodes,
                            numNodes = thumbElements.length,
                            items = [],
                            figureEl,
                            linkEl,
                            size,
                            item;

                    for (var i = 0; i < numNodes; i++) {

                        figureEl = thumbElements[i]; // <figure> 元素

                        // 只包括元素节点
                        if (figureEl.nodeType !== 1) {
                            continue;
                        }

                        linkEl = figureEl.children[0]; // 获得A标签

                        size = linkEl.getAttribute('data-size').split('x');

                        // 创建对象幻灯片
                        item = {
                            src: linkEl.getAttribute('href'),
                            w: parseInt(size[0], 10),
                            h: parseInt(size[1], 10)
                        };



                        if (figureEl.children.length > 1) {
                            // <figcaption> 内容
                            item.title = figureEl.children[1].innerHTML;
                        }

                        if (linkEl.children.length > 0) {
                            // <img> 缩略图, 检索缩略图的URL
                            item.msrc = linkEl.children[0].getAttribute('src');
                        }

                        item.el = figureEl; // 保存元素
                        items.push(item);
                    }

                    return items;
                };

                // 获取父元素
                var closest = function closest(el, fn) {
                    return el && (fn(el) ? el : closest(el.parentNode, fn));
                };

                // 点击缩略图时触发
                var onThumbnailsClick = function(e) {
                    e = e || window.event;
                    e.preventDefault ? e.preventDefault() : e.returnValue = false;

                    var eTarget = e.target || e.srcElement;

                    // 找到根元素
                    var clickedListItem = closest(eTarget, function(el) {
                        return (el.tagName && el.tagName.toUpperCase() === 'FIGURE');
                    });

                    if (!clickedListItem) {
                        return;
                    }

                    // 找点击项 遍历元素
                    // 获得DATA-
                    var clickedGallery = clickedListItem.parentNode,
                            childNodes = clickedListItem.parentNode.childNodes,
                            numChildNodes = childNodes.length,
                            nodeIndex = 0,
                            index;

                    for (var i = 0; i < numChildNodes; i++) {
                        if (childNodes[i].nodeType !== 1) {
                            continue;
                        }

                        if (childNodes[i] === clickedListItem) {
                            index = nodeIndex;
                            break;
                        }
                        nodeIndex++;
                    }



                    if (index >= 0) {
                        // 索引值
                        openPhotoSwipe(index, clickedGallery);
                    }
                    return false;
                };

                // 解析图片索引
                var photoswipeParseHash = function() {
                    var hash = window.location.hash.substring(1),
                            params = {};

                    if (hash.length < 5) {
                        return params;
                    }

                    var vars = hash.split('&');
                    for (var i = 0; i < vars.length; i++) {
                        if (!vars[i]) {
                            continue;
                        }
                        var pair = vars[i].split('=');
                        if (pair.length < 2) {
                            continue;
                        }
                        params[pair[0]] = pair[1];
                    }

                    if (params.gid) {
                        params.gid = parseInt(params.gid, 10);
                    }

                    return params;
                };

                var openPhotoSwipe = function(index, galleryElement, disableAnimation, fromURL) {
                    var pswpElement = document.querySelectorAll('.pswp')[0],
                            gallery,
                            options,
                            items;

                    items = parseThumbnailElements(galleryElement);

                    // 定义的选项
                    options = {
                        // 定义画廊索引值
                        galleryUID: galleryElement.getAttribute('data-pswp-uid'),
                        getThumbBoundsFn: function(index) {
                            // 查看更多信息
                            var thumbnail = items[index].el.getElementsByTagName('img')[0], // find thumbnail
                                    pageYScroll = window.pageYOffset || document.documentElement.scrollTop,
                                    rect = thumbnail.getBoundingClientRect();

                            return {x: rect.left, y: rect.top + pageYScroll, w: rect.width};
                        }

                    };

                    // 打开url
                    if (fromURL) {
                        if (options.galleryPIDs) {
                            // 解析实际 
                            
                            for (var j = 0; j < items.length; j++) {
                                if (items[j].pid == index) {
                                    options.index = j;
                                    break;
                                }
                            }
                        } else {
                            // 索引从1开始
                            options.index = parseInt(index, 10) - 1;
                        }
                    } else {
                        options.index = parseInt(index, 10);
                    }

                    // 没有找的处理
                    if (isNaN(options.index)) {
                        return;
                    }

                    if (disableAnimation) {
                        options.showAnimationDuration = 0;
                    }

                    // 传递数据并初始化
                    gallery = new PhotoSwipe(pswpElement, PhotoSwipeUI_Default, items, options);
                    gallery.init();
                };

                // 遍历的所有事件
                var galleryElements = document.querySelectorAll(gallerySelector);

                for (var i = 0, l = galleryElements.length; i < l; i++) {
                    galleryElements[i].setAttribute('data-pswp-uid', i + 1);
                    galleryElements[i].onclick = onThumbnailsClick;
                }

                // 解析
                var hashData = photoswipeParseHash();
                if (hashData.pid && hashData.gid) {
                    openPhotoSwipe(hashData.pid, galleryElements[ hashData.gid - 1 ], true, true);
                }
            };

        // 执行上面的函数
            initPhotoSwipeFromDOM('.my-gallery');