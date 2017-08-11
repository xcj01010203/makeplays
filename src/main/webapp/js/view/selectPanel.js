(function($){
	var $container = $([
            '<div class="right-pup-container">',
            '<span class="right-pup-arrow"></span>',
            '<h3 class="right-pup-title"></h3>',
            '<div class="right-pup-search">',
                '<input  class="right-pup-search-text" type="text"><button class="right-pup-search-btn"></button>',
            '</div>',
            '<ul class="right-pup-content"></ul>',
            '</div>'
           ].join(''));

        var curElem = null;
        var multiselect = null;
        var dataList = null;
        var timer = null;
        var title = null;
        var arrowTop = null;
        
        window.Popup = {
            show : function(options){
            	curElem = options.currentTarget;
            	if(!curElem || typeof curElem != 'object'){
            		throw Error('没有指定当前目标对象');
            	}
            	dataList = options.dataList || [];
                var right = options.right || 0;
                arrowTop = options.arrowTop || 0;
                var containterTop = options.top || 80;
                multiselect = options.multiselect || false;
                title = options.title || "";
                var html = [];
                $.each(dataList, function(i, obj){
                	if(obj.attr){
                		html.push('<li title='+ obj.name +' shoot_regin="'+ obj.attr +'" class="right-pup-item ' + (obj.selected ? 'selected' : '') + '">' + obj.name +'(' + obj.attr +')' + '</li>');
                	}else{
                		html.push('<li title='+ obj.name +' class="right-pup-item ' + (obj.selected ? 'selected' : '') + '">' + obj.name + '</li>');
                	}
                    
                });
                $container.show().css('right', right).find('.right-pup-arrow').fadeIn().css('top', arrowTop).end().find('.right-pup-content').html(html.length ? html.join('') : '<p style="padding-left:52px; font-size: 13px;">暂无数据</p>');
                $container.find('.right-pup-title').text(title);	//标题
                $container.css("top", containterTop);
                $container.css("height", "calc(100% - "+ containterTop +"px)");
                $container.find(".right-pup-search-text").val("");
            },
            hide: function(){
                $container.hide();
            },
            setParams : function(options){
                if(options.top || options.top === 0)
                    $container.css('top', options.top);
                if(options.title)
                	$container.find('.right-pup-title').text(title);
                if(options.arrowTop || options.arrowTop === 0)
                    $container.find('.right-pup-arrow').css('top', options.arrowTop);
                
            },
            setTitle: function(title){
            	if(title)
            		$container.find('.right-pup-title').text(title);
            },
            setCallback : function(dom, callback){
            	dom.callback = callback;
            },
            unSelectItem: function(text) {
            	$(".right-pup-item[title="+ text +"]").removeClass('selected');;
            },
            scrollArrowTop: function(scrollTop) {
            	if(curElem){
            		var top = $(curElem).offset().top - scrollTop + 40;
            		if(top < 0){
            			$container.find('.right-pup-arrow').fadeOut();
            		}else{
            			$container.find('.right-pup-arrow').fadeIn().css('top', top);
            		}
            	}
            }
        };
        
        $(function(){
        	$(document.body).append($container);
        	
        	$container.off().on('click', '.right-pup-item', function(){
        		var $self = $(this);
        		
        		if(!multiselect && !$self.hasClass('selected')){
        			$container.find('.selected').removeClass('selected');
        			$self.addClass('selected');
        		}else{
        			$self.toggleClass('selected');
        		}
        		
        		var callback = curElem.callback;
        		if(callback){
        			if($self.attr("shoot_regin")){
        				callback.call(curElem, {value: $self.attr("title"), attrRegin:$self.attr("shoot_regin"), selected: $self.hasClass('selected'), multiselect: multiselect});
        			}else{
        				callback.call(curElem, {value: $self.text(), selected: $self.hasClass('selected'), multiselect: multiselect});
        			}
        			
        		}
        		
        	}).on('click', '.right-pup-search-btn', function(){
        		var text = $container.find('.right-pup-search-text').val();
        		search(text);
        	}).on('keyup', '.right-pup-search-text', function(e){
        		clearTimeout(timer);
        		var text = this.value;
        		timer = setTimeout(function(){
        			mySearch(text);
        		}, 300);
        	}).on("click", ".right-pup-arrow", function() {
        		$container.hide();
        	});
        });
        
        
        function mySearch(text){
        	var dropdownList = $('.right-pup-item');
        	
        	dropdownList.each(function(){
                var dropdownValue = $(this).text();
                if(dropdownValue.search($.trim(text)) != -1){
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        	
        	/*var html = [];
        	
        	if(!text){
        		$.each(dataList, function(i, obj){
                    html.push('<li class="right-pup-item ' + (obj.selected ? 'selected' : '') + '">' + obj.name + '</li>');
                });
                $container.find('.right-pup-content').html(html.join(''));
                return;
        	}
        	
    		var res = $.grep(dataList, function(string, i){
    			return string.name.indexOf(text) != -1;
    		});
    		
    		
    		if(res){
    			$.each(res, function(i, obj){
                    html.push('<li class="right-pup-item ' + (obj.selected ? 'selected' : '') + '">' + obj.name + '</li>');
                });
                $container.find('.right-pup-content').html(html.join(''));
    		}*/
    		
        }
        
    }).call(this, jQuery);

