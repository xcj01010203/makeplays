var crewType = "";
var regPerformerCommonNum = /[\(（](\d*)[\)）]/g;
$(document).ready(function () {
	//获取剧组类型
	getcrewType();
	
	//初始化场景列表信息
	initViewDetailInfo();
	
	//关闭弹层
	$(document).click(function(){
		parent.Popup.hide();
		$('.dropdown_box').hide();
    });
});

//获取剧组类型
function getcrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				crewType = response.crewType;
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//初始化场景信息页面时需要加载的数据
function initViewDetailInfo(){
	var viewId = $("#viewDetailId").val();
	$.ajax({
		url: '/viewManager/queryViewDetailInfo',
		type: 'post',
		data:{viewId:viewId},
		datatype: 'json',
		success: function(response){
			if(response.success){
			var	viewInfoDto = response.viewInfoDto;
			var advertInfoList = response.advertInfoList;
			var	saveType = response.saveType;
			var	filterDto = response.filterDto;
			
			//添加body的样式及属性
			addBodyHtml(viewInfoDto);
			
			//加载右侧下拉框及文本框中的内容
			loadViewMainInfo(viewInfoDto, advertInfoList, saveType, filterDto);
			
			//初始化场景详细信息文本框的点击事件
			initRightContentClick();
			
			//自动将手动输入的内容转变为标签
			autoConetentToTag();
			
			//初始化剧本内容框中角色的名字的颜色
			initContentColor();
			
			//初始化下拉列表框的操作
			initSelectMany();
			
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//渲染右侧文本框的数据
function loadContentData(){
	//进入页面，根据后台传过来的数据渲染页面上演员场景等信息
    $('.tagInput input').each(function(){
         var _this=$(this);
         if(_this.attr('sv')!=undefined){
        	//后台返回的值
             var arr=_this.attr('sv').split(','),
             	_list=_this.parents('.tagWrap').next('.tagWrap_popup').find('li a');
             for(var i=0; i<arr.length; i++){
                 var _val = $.trim(arr[i]).replace(regPerformerCommonNum, '');
                 var _num = '';
                 
               //主演后台传递的值格式为“演员名称_（OS）”，该格式需要特殊处理
                 if ($(this).parents('.tagWrap').hasClass('performer_first')) {
                	 var majorArr = _val.split('(');
                	 var majorName = majorArr[0];
                	 if (majorArr.length > 1) {
                		 var majorNum = "(" + majorArr[1];
                		 _num = majorNum;
                	 }
                	 
                	 _val = majorName;
                	 
                	 //模拟点击
                	 _list.each(function(){
                		 if($.trim($(this).text()) == _val){
                			 if($(this)[0].tagName.toLowerCase()=='a'){
                				 var tagInput=$(this).parents('.tagWrap_popup').prev('div').find('.tagInput');
                				 if ($(this).parents('.tagWrap_popup').prev('div').hasClass('performer_first') && _num != undefined && _num != '') {
                					 tagInput.before('<li sid="'+$(this).attr('sid')+'">'+ $.trim($(this).text() +  _num )+'<a href="javascript:void(0)" class="closeTag""></a></li>');
                					 $(this).addClass('current');
                				 } else {
                					 tagInput.before('<li sid="'+$(this).attr('sid')+'">'+ $.trim($(this).text())+'<a href="javascript:void(0)" class="closeTag""></a></li>');
                					 $(this).addClass('current');
                				 }
                			 }
                		 }
                	 });
                 }else {
                	 //群众演员后台传递的值格式为“演员名称_演员人数”，该格式需要特殊处理
                	 if ($(this).parents('.tagWrap').hasClass('performer_common')) {
                		 var massArr = _val.split('_');
                		 var massName = massArr[0];
                		 var massNum = massArr[1];
                		 
                		 _val = massName;
                		 _num = massNum;
                	 }
                	 //模拟点击
                	 _list.each(function(){
                		 if($.trim($(this).text()) == _val){
                			 if($(this)[0].tagName.toLowerCase()=='a'){
                				 var tagInput=$(this).parents('.tagWrap_popup').prev('div').find('.tagInput');
                				 if ($(this).parents('.tagWrap_popup').prev('div').hasClass('performer_common') && _num > 1) {
                					 
                					 tagInput.before('<li sid="'+$(this).attr('sid')+'">'+ $.trim($(this).text() + '(' + _num + ')')+'<a href="javascript:void(0)" class="closeTag""></a></li>');
                					 $(this).addClass('current');
                				 } else {
                					 tagInput.before('<li sid="'+$(this).attr('sid')+'">'+ $.trim($(this).text())+'<a href="javascript:void(0)" class="closeTag""></a></li>');
                					 $(this).addClass('current');
                				 }
                			 }
                		 }
                	 });
					
				}
                 
             }
         }
     });
}


//自动将手动输入的内容转换为标签
function autoConetentToTag(){
	//在群众演员等多选下拉框中手动输入内容时，按回车键，自动把输入内容转为标签
	$('.tagInput input').keyup(function(ev){
        var _this=$(this);
        if(_this.val().length>10){
            _this.val(_this.val().substr(0,10));
        }
        if(ev.keyCode==13){//回车添加新记录
            if($.trim(_this.val()).length>=1){
                var tagList=_this.parent('.tagInput').siblings('li');
                var _isExists=false; //是否存在的标识，默认为false;
                for(var i=0;i<tagList.length;i++){//判断是否已存在
                    if( tagList.eq(i).text()==$.trim(_this.val()) ){
                        _isExists=true;
                        _this.val('').next('span').text('');
                        break;
                    }
                }
                if(!_isExists){//新创建
                    var _tagList=_this.parents('.tagWrap').next('.tagWrap_popup').find('ul li a');
                    var _sid='';
                    for(var i=0;i<_tagList.length;i++){
                        if(_tagList.eq(i).text()==$.trim(_this.val())){
                            _sid=_tagList.eq(i).attr('sid');
                            _tagList.eq(i).addClass('current');
                            break;
                        }
                    }
                    
                    //判断输入的内容是否包含特殊字符，如果包含则提示是否分割
                    //定义正则
            		var testReg = new RegExp('\\,|，|、|/|；| |\\t|　| ');
                    var text = _this.val();
                    if (testReg.test(text)) {
						//包含特殊字符
                    	parent.swal({
            				title: "是否拆分道具",
            				text: '检测到您输入的道具中含有特殊字符，是否将当前道具拆分为多个道具？',
            				type: "warning",
            				showCancelButton: true,  
            				confirmButtonColor: "rgba(255,103,2,1)",
            				confirmButtonText: "是",   
            				cancelButtonText: "否",   
            				closeOnConfirm: true,   
            				closeOnCancel: true
            			}, function (isConfirm) {
            				if (isConfirm) {
            					var nameArr = text.split(testReg);
            					for(var i = 0; i<nameArr.length; i++){
            						 if(_sid!=''){
            							 if ($.trim(nameArr[i]) != '') {
            								 _this.parent().before('<li sid="'+_sid+'">'+$.trim(nameArr[i])+'<a href="javascript:void(0)" class="closeTag"></a></li>');
            							 }
            		                    }else{
            		                    	if ($.trim(nameArr[i]) != '') {
            		                    		_this.parent().before('<li>'+$.trim(nameArr[i])+'<a href="javascript:void(0)" class="closeTag"></a></li>');
											}
            		                    }
            		                    _this.val('').next('span').html('w');
            					}
            				}else {
            					 if(_sid!=''){
            	                        _this.parent().before('<li sid="'+_sid+'">'+$.trim(_this.val())+'<a href="javascript:void(0)" class="closeTag"></a></li>');
            	                    }else{
            	                        _this.parent().before('<li>'+$.trim(_this.val())+'<a href="javascript:void(0)" class="closeTag"></a></li>');
            	                    }
            	                    _this.val('').next('span').html('w');
            				}
            				
            			});
					}else {
						if(_sid!=''){
	                        _this.parent().before('<li sid="'+_sid+'">'+$.trim(_this.val())+'<a href="javascript:void(0)" class="closeTag"></a></li>');
	                    }else{
	                        _this.parent().before('<li>'+$.trim(_this.val())+'<a href="javascript:void(0)" class="closeTag"></a></li>');
	                    }
	                    _this.val('').next('span').html('w');
					}
            		
                }
            }
        }else{
            _this.next('span').text($.trim(_this.val()));
            _this.width(_this.next('span').width()+50);
            
            
            //搜索
            var tagList = _this.parent("li").parent("ul").parent(".tagWrap").next(".tagWrap_popup").find("ul").find("li").find("a");
            tagList.each(function(){
                var tagValue = $(this).text();
                if(tagValue.search($.trim(_this.val())) != -1){
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
    });
}

//初始化多选下拉框
function initSelectMany(){
	//多选下拉框中右上角关闭图标
    $('.closePopup').unbind("click");
	$('.closePopup').click(function(){
        $(this).parent().hide();
        return false;
    });
	
	//点击多选下拉框中元素时触发的操作
    $('.tagWrap').unbind("click");
	$('.tagWrap').click(function(ev){
    	$('#performer_common_inp').remove();
    	$('#performer_first_inp').remove();
        var _target=$(ev.target);
        $this = $(this);
        //主演OS
        $osinp = $( "<input type='button' class='major-os-button' id='performer_first_inp' value='OS' onclick='changeClass(this)'/>" );
        // 群众演员 修改人数input
        $inp = $( '<input type="text" id="performer_common_inp" placeholder="人数" style="z-index:999; position:absolute; width: 50px;height:20px; line-height:30px \0; text-align: center;border: 1px solid #ccc;border-color:#409DFE;" />' );
        // x 关闭click
        if(_target.is('.closeTag')) {
        	
            var _tagList=_target.parents('.tagWrap').next('.tagWrap_popup').find('ul li a'),
            	_val = $.trim(_target.parent().text()).replace(regPerformerCommonNum, '');
            for(var i=0;i < _tagList.length;i++){
                if( _tagList.eq(i).text() == _val ){
                    _tagList.eq(i).removeClass('current');
                    break;
                }
            }
            $(ev.target).parent().remove();
            $("#isChanged").val(1);//删除就改变值
            parent.Popup.unSelectItem(_target.parent().text());	//左侧面板中对应数据取消选中
          // 点击的li 修改人数
        } else if ( $this.hasClass('performer_common') && _target[0].tagName.toLowerCase() == 'li' ) {
        	$inp.keyup(function(ev){
				if(ev.keyCode==13){
					commonPepleCount( $(this), _target);			
				}
        	});
        	$inp.blur(function(){
        		 commonPepleCount( $(this), _target );
        	});
        	
        	if( regPerformerCommonNum.exec( _target.text() ) == null){
        		$inp.css({left: _target.offset().left, top: _target.offset().top });	
        	}else{
        		regPerformerCommonNum.lastIndex = 0;
        		$inp.val( regPerformerCommonNum.exec( _target.text() )[1] ).css({left: _target.offset().left, top: _target.offset().top });
        	}
        	
        	$('body').append($inp);
        	
        }else if ($this.hasClass('performer_first') && _target[0].tagName.toLowerCase() == 'li' ) {
        	$osinp.keyup(function(ev){
				if(ev.keyCode==13){
					firstMajorRoleInfo( $(this), _target);			
				}
        	});
        	$osinp.blur(function(){
        		firstMajorRoleInfo( $(this), _target );
        	});
        	
        	if( regPerformerCommonNum.exec( _target.text() ) == null){
        		$osinp.css({left: _target.offset().left, top: _target.offset().top });	
        	}else{
        		regPerformerCommonNum.lastIndex = 0;
        		$osinp.val( regPerformerCommonNum.exec( _target.text() )[1] ).css({left: _target.offset().left, top: _target.offset().top });
        	}
        	
        	//判断是否包含OS
        	 var tagInput=_target.text();
        	 if (tagInput.indexOf("OS") != -1) {
        		 $osinp.removeClass("major-os-button");
        		 $osinp.addClass("major-os-button-hover");
			}
        	
        	$('body').append($osinp);
		} else {
        	if ($this.hasClass('readOnly')) {
        		return false;
        	}
        	var selectedLi = $this.find("ul li[sid=19]");
        	var dataJson = new Array();
            var dropdownLiList = $this.next('.tagWrap_popup').find("li a");
            $.each(dropdownLiList, function(index, item) {
            	var text = item.text;
            	var selected = false;
            	$.each(selectedLi, function(seletedIndex, seletedItem) {
            		if (seletedItem.textContent == text) {
            			selected = true;
            			return false;
            		}
            	});
            	dataJson[index] = {name: item.text, selected: selected};
            });
            var title = $this.parent("td").prev("td").text();
            
            var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
            parent.showSelectPanel({dataJson: dataJson, right: window.innerWidth, arrowTop: $this.offset().top - scrollTop + 40, title: title, multiselect: true, currentTarget: $this.get(0)});
            parent.Popup.setCallback(this, function(option){
            	if (option.selected) {
            		//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
                    //定义正则
            		var testReg = new RegExp('\\,|，|、|/|；| |\\t|　| ');
                    var text = option.value;
                    if (testReg.test(text)) {
            			//包含特殊字符
            			parent.swal({
            				title: "是否拆分道具",
            				text: '检测到您选择的道具中含有特殊字符，是否将当前道具拆分为多个道具？',
            				type: "warning",
            				showCancelButton: true,  
            				confirmButtonColor: "rgba(255,103,2,1)",
            				confirmButtonText: "是",   
            				cancelButtonText: "否",   
            				closeOnConfirm: true,   
            				closeOnCancel: true
            			}, function (isConfirm) {
            				if (isConfirm) {
            					var nameArr = text.split(testReg);
            					for(var i = 0; i<nameArr.length; i++){
            						if ($.trim(nameArr[i]) != '') {
            							$this.find(".tagInput").before('<li sid="19">'+ nameArr[i] +'<a href="javascript:void(0)" class="closeTag"></a></li>');
									}
            					}
            				}else {
            					$this.find(".tagInput").before('<li sid="19">'+ text +'<a href="javascript:void(0)" class="closeTag"></a></li>');
            				}
            			});
            		}else {
            			$this.find(".tagInput").before('<li sid="19">'+ text +'<a href="javascript:void(0)" class="closeTag"></a></li>');
					}
                    
            	} else {
            		var selectedLi = $this.find("ul li[sid=19]");
            		$.each(selectedLi, function(seletedIndex, seletedItem) {
                		if (seletedItem.textContent == option.value) {
                			$(this).remove();
                		}
                	});
            	}
            	setViewIsChanged();
          	});
        }
        
        // 修改群众演员人数
        function commonPepleCount(_$this, _target){
        	if(Math.ceil( _$this.val() ) ){
    			var _close = _target.children('.closeTag').clone('true');
    			_target.text( _target.text().replace(regPerformerCommonNum, '') + '('+ Math.ceil( _$this.val() ) +')' ).append(_close);
    			$inp.remove();
    		}else{
    			_$this.val('');
    		}
        }
        
        //修改主演os
        function firstMajorRoleInfo(_$this, _target){
        	if(_$this.hasClass("major-os-button-hover")){
        		//添加os
    			var _close = _target.children('.closeTag').clone('true');
    			var text  = _target.text();
    			if (text.indexOf('OS')== -1) {
    				_target.text( _target.text().replace(regPerformerCommonNum, '') + '(OS)' ).append(_close);
    				$osinp.remove();
				}else {
					$osinp.remove();
				}
    		}else{
    			//去掉OS
    			var _close = _target.children('.closeTag').clone('true');
    			var text  = _target.text();
    			if (text.indexOf('OS')!= -1) {
    				_target.text( _target.text().substring(0,text.indexOf("(")).replace(regPerformerCommonNum, '')).append(_close);
    				$osinp.remove();
				}else {
					$osinp.remove();
				}
    		}
        }


//        setViewIsChanged();
        ev.stopPropagation();
    });
}

//将点击内容添加到文本框中
function addDataToContet(){
	//把点击内容放入文本框
    $('.dropdown_box li').unbind("click");
    $('.dropdown_box li').click(function(ev){
    	$(this).parent('ul').prev('.drop_down').val($(this).text()).attr('sid',$(this).attr('sid'));
    	setViewIsChanged();
    });
}

//初始化快速收索功能
function initSearch(){
	//快速搜索功能
    $('.drop_down').on("keyup.textchange", function(ev) {
    	var _this=$(this);
    	var value = _this.val();
    	var dropdownList = _this.next('.dropdown_box').find("li");
    	
    	dropdownList.each(function(){
            var dropdownValue = $(this).text();
            if(dropdownValue.search($.trim(value)) != -1){
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });
}

//初始化drop_down的点击事件
function initRightContentClick(){
	$('.drop_down').unbind("click");
    $('.drop_down').click(function(ev){
        var _this=$(this);
        $('.dropdown_box').hide();
    	if (_this.context.readOnly) {
    		return false;
    	}
        _this.next('.dropdown_box').css({left:_this.position().left,top:_this.position().top+_this.outerHeight()}).show();
        ev.stopPropagation();
    });
    
    //页数只能输入数字
	$("input.scene_page").on("keyup", function() {
		var value = $(this).val();
		if (isNaN(value)) {
	        $(this).val(value.substring(0, value.length-1));
	    }
	});
	
    //初始化 drop_click点击事件
    initDropClick();
    //初始化收索框
    initSearch();
    //将点击内容添加到文本框中
    addDataToContet();
}

//初始刷drop_click点击事件
function initDropClick(){
	$('.drop_click').unbind("click");
	$('.drop_click').click(function(ev){
	        var _this=$(this);
//	        _this.siblings("p.special-modole").eq(0).hide();
	        
	        $('.dropdown_box').hide();
	        if(_this.attr('url')){
	            $.ajax({
	                type:'post',
	                url:_this.attr('url'),
	                dataType:'json',
	                success:function(param){
	                    var message=param.message;
	                    if(message==undefined || message=="0"){
	                        var groupList=param.groupList;
	                        if(groupList!=undefined && groupList.length>0){
	                            $('.dropdown_box').find('.plan_add_group').siblings('li').remove();
	                            for(var i=0;i<groupList.length;i++){
	                                var _liCopy=_this.next('.dropdown_box').find('.plan_add_group').clone(true);                               
	                                _liCopy.removeClass('plan_add_group');
	                                $(_liCopy).attr('sid',groupList[i].id);
	                                $(_liCopy).find('a').text(groupList[i].name);
	                                $('.dropdown_box').find('.plan_add_group').before(_liCopy);
	                            }
	                            _this.next('.dropdown_box').css({left:_this.position().left,top:_this.position().top+_this.outerHeight()}).show();
	                        }
	                    }else{
	                    	parent.showErrorMessage(message);
	                    }
	                }
	            });
	        }else{
	        	if (_this.context.readOnly) {
	        		return false;
	        	}
	        	
	        	var hadValue = _this.val();
	        	var dataJson = new Array();
	            var dropdownLiList = _this.next('.dropdown_box').find("li a");
	            $.each(dropdownLiList, function(index, item) {
	            	if (item.text == hadValue) {
	            		dataJson[index] = {name: item.text, selected: true};
	            	} else {
	            		dataJson[index] = {name: item.text};
	            	}
	            });
	            var title = _this.parent("td").prev("td").text();
	            var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
	            parent.showSelectPanel({dataJson: dataJson, right: window.innerWidth, arrowTop: _this.offset().top - scrollTop + 40, title: title, multiselect: false, currentTarget: _this.get(0)});
	            parent.Popup.setCallback(this, function(option){
	            	if (option.selected) {
	            		_this.val(option.value);
	            	} else {
	            		_this.val("");
	            	}
	            	setViewIsChanged();
	          	});
	        }
	        ev.stopPropagation();
	    });
	//设置拍摄地
	$(".shoot_location").unbind("click");
	$(".shoot_location").click(function(ev){
		var _this = $(this);
		var hadValue = $("#noFinishViewShootLocation").val();
		var dataJson = new Array();
		var dropdownLiList = _this.next('.dropdown_box').find("li a");
		$.each(dropdownLiList, function(index, item) {
	    	if (item.text == hadValue) {
	    		dataJson[index] = {name: item.text, selected: true, attr: $(this).attr("shoot_regin")};
	    	} else {
	    		dataJson[index] = {name: item.text, attr: $(this).attr("shoot_regin")};
	    	}
	    });
		var title = _this.parent("td").prev("td").text();
	    var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
	    parent.showSelectPanel({dataJson: dataJson, right: window.innerWidth, arrowTop: _this.offset().top - scrollTop + 40, title: title, multiselect: false, currentTarget: _this.get(0)});
	    parent.Popup.setCallback(this, function(option){
	    	if (option.selected) {
	    		_this.val(option.value);
	    		if(option.attrRegin) {
		    		$("#shootReginValue").text("(" + option.attrRegin + ")");
	    		}else{
	    			$("#shootReginValue").text("");
	    		}
	    	} else {
	    		_this.val("");
	    		$("#shootReginValue").text("");
	    	}
	    	setViewIsChanged();
	  	});
	    ev.stopPropagation();
	});
	//设置拍摄地域
	$(".set-shootRegin").unbind("click");
	$(".set-shootRegin").click(function(ev){
		if($("#noFinishViewShootLocation").val() == ""){
			parent.showInfoMessage("拍摄地不能为空");
			return;
		}
		var _this = $(this);
		var hadValue = $("#shootReginValue").text();
		hadValue=hadValue.replace(/\(/g,'').replace(/\)/g,'');
		var dataJson = new Array();
		var dropdownLiList = $('#shootReginBox').find("li a");
		$.each(dropdownLiList, function(index, item) {
	    	if (item.text == hadValue) {
	    		dataJson[index] = {name: item.text, selected: true};
	    	} else {
	    		dataJson[index] = {name: item.text};
	    	}
	    });
		var title = "拍摄地域";
	    var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
	    parent.showSelectPanel({dataJson: dataJson, right: window.innerWidth, arrowTop: _this.offset().top - scrollTop + 40, title: title, multiselect: false, currentTarget: _this.get(0)});
	    parent.Popup.setCallback(this, function(option){
	    	if (option.selected) {
	    		$("#shootReginValue").text("("+option.value+")");
	    	} else {
	    		$("#shootReginValue").text("");
	    	}
	    	setViewIsChanged();
	  	});
	    ev.stopPropagation();
	});
}






//剧本内容上颜色标签的加载
function initContentColor(){
	 //剧本内容
    var contentDiv = $(window.parent.document).find('#viewContent span');
    if (contentDiv != undefined && contentDiv != null && contentDiv.html() != undefined && contentDiv.html() != null && contentDiv.html() != "") {
    	//主要演员
        var majorActorStr= $('.performer_first .tagInput input').attr('sv');
        if (majorActorStr != undefined) {
        	var majorActorArr = majorActorStr.split(',');
        	for (var i = 0; i < majorActorArr.length; i++) {
        		var majorActor = eval("/" + majorActorArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(majorActor, "<span style='background:#EECFA1;'>" + majorActorArr[i]  + "</span>"));
        	}
        }
        //特约演员
        var guestActorStr= $('.performer_special .tagInput input').attr('sv');
        if (guestActorStr != undefined) {
        	var guestActorArr = guestActorStr.split(',');
        	for (var i = 0; i < guestActorArr.length; i++) {
        		var guestActor = eval("/" + guestActorArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(guestActor, "<span style='background:#BFEFFF';>" + guestActorArr[i]  + "</span>"));
        	}
        }
        //群众演员
        var massActorStr= $('.performer_common .tagInput input').attr('sv');
        if (massActorStr != undefined) {
        	var massActorArr = massActorStr.split(',');
        	for (var i = 0; i < massActorArr.length; i++) {
        		var massActorName = massActorArr[i].split('_')[0];
        		var massActor = eval("/" + massActorName + "/g");
        		contentDiv.html(contentDiv.html().replace(massActor, "<span style='background:#BCD2EE;'>" + massActorName  + "</span>"));
        	}
        }
        //服装
        var clothesStr= $('.clothes_info .tagInput input').attr('sv');
        if (clothesStr != undefined) {
        	var clothesArr = clothesStr.split(',');
        	for (var i = 0; i < clothesArr.length; i++) {
        		var clothes = eval("/" + clothesArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(clothes, "<span style='background:#9AFF9A;'>" + clothesArr[i]  + "</span>"));
        	}
        }
        //化妆
        var makeupStr= $('.makeup_info .tagInput input').attr('sv');
        if (makeupStr != undefined) {
        	var makeupArr = makeupStr.split(',');
        	for (var i = 0; i < makeupArr.length; i++) {
        		var makeup = eval("/" + makeupArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(makeup, "<span style='background:#CDC9A5;'>" + makeupArr[i]  + "</span>"));
        	}
        }
        //道具
        var propStr= $('.tool_main .tagInput input').attr('sv');
        if (propStr != undefined) {
        	var propArr = propStr.split(',');
        	for (var i = 0; i < propArr.length; i++) {
        		var prop = eval("/" + propArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(prop, "<span style='background:#FFE1FF;'>" + propArr[i]  + "</span>"));
        	}
        }
        
        //特殊道具
        var propStr= $('.tool_special .tagInput input').attr('sv');
        if (propStr != undefined) {
        	var propArr = propStr.split(',');
        	for (var i = 0; i < propArr.length; i++) {
        		var prop = eval("/" + propArr[i] + "/g");
        		contentDiv.html(contentDiv.html().replace(prop, "<span style='background:#FFE1FF;'>" + propArr[i]  + "</span>"));
        	}
        }
    }
    
    $("#viewDetailInfoPage input").on('change', function() {
    	setViewIsChanged();
    });
    
    $("#viewDetailInfoPage select").on('change', function() {
    	setViewIsChanged();
    });
    
    $("#otherShootTables input").on('change', function() {
    	setViewIsChanged();
	});
}

//初始化页面时,加载场景表字段信息
function loadViewMainInfo(viewInfoDto, advertInfoList, saveType, filterDto){
	//修改场次面板和新增场次面板不同，新增面板中没有数据，而修改面板中有数据
	if (saveType == 'update') {
		
	$("#saveType").attr("value", saveType);
	$("#shootStatus").attr("value", viewInfoDto.shootStatus);
	$("#isManualSave").attr("value", viewInfoDto.isManualSave);
	

	$(".page-btn").show();
	
	//拍摄已完成/删戏
	if ((parentFromFlag=='scene' && isScenarioReadonly) || (parentFromFlag=='view' && isViewInfoReadonly) ) {
		//如果为电影类型剧组，则不展示集号
		if (crewType == 0 || crewType == 3) { //当前剧本为电影
			$("#tvbTypeTr").empty();
			
			$("#filmViewid").attr("value", viewInfoDto.viewId);
			$("#filmSeriesNo").attr("value", viewInfoDto.seriesNo);
			$("#filmViewNo").attr("value", viewInfoDto.viewNo);
			$("#filmPageCount").attr("value", viewInfoDto.pageCount);
		}else {
			$("#flimTypeTr").empty();
			
			$("#tvbViewid").attr("value", viewInfoDto.viewId);
			$("#tvbSeriesNo").attr("value", viewInfoDto.seriesNo);
			$("#tvbViewNo").attr("value", viewInfoDto.viewNo);
			$("#tvbPageCount").attr("value", viewInfoDto.pageCount);
		}
		
		//气氛
		$("#viewAtmosphereName").attr("value", viewInfoDto.atmosphereName);
		//内外
		$("#viewSite").attr("value", viewInfoDto.site);
		//特殊提醒
		$("#viewSpecialRemind").attr("value", viewInfoDto.specialRemind);
		//主要内容
		$("#viewContent").attr("value", viewInfoDto.mainContent);
		//主场景
		$("#viewFirstLocation").attr("value", viewInfoDto.firstLocation);
		//次级场景
		$("#viewSecondLocation").attr("value", viewInfoDto.secondLocation);
		//三级场景
		$("#viewThirdLocation").attr("value", viewInfoDto.thirdLocation);
		//拍摄地点
		$("#viewShootLocation").attr("value", viewInfoDto.shootLocation);
		//拍摄地域
		if(viewInfoDto.shootRegion) {
			$("#shootReginValue").text("(" + viewInfoDto.shootRegion + ")");
		} else {
			$("#shootReginValue").text("");
		}
		
		//主要演员
		$("#viewMajorActor").attr("sv", viewInfoDto.majorActor);
		var $majorActorUl = $("#majorFilterUl");
		var majorRoleList = filterDto.majorRoleList;
		//当后台传递的参数不为空时取出主要演员的信息
		addUlData(filterDto, majorRoleList, $majorActorUl);
		
		//特约演员
		$("#viewGuestActor").attr("sv", viewInfoDto.guestActor);
		var $guestActorUl = $("#guestFilterUl");
		var guestRoleList = filterDto.guestRoleList;
		//当后台传递的参数不为空时取出特约演员的信息
		addUlData(filterDto, guestRoleList, $guestActorUl);
		
		//群众演员
		$("#viewMassActor").attr("sv", viewInfoDto.massesActor);
		var $massActorUl = $("#massFilterUl");
		var massesRoleList = filterDto.massesRoleList;
		//当后台传递的参数不为空时取出群众演员的信息
		addUlData(filterDto, massesRoleList, $massActorUl);
		
		//服装
		$("#viewClothes").attr("sv", viewInfoDto.clothes);
		var $clothesActorUl = $("#clothesFilterUl");
		var clotheList = filterDto.clotheList;
		//当后台传递的参数不为空时取出服装的信息
		addUlData(filterDto, clotheList, $clothesActorUl);
		
		//化妆
		$("#viewMakeups").attr("sv", viewInfoDto.makeups);
		var $makeupsUl = $("#makeupsFilterUl");
		var makeupList = filterDto.makeupList;
		//当后台传递的参数不为空时取出化妆的信息
		addUlData(filterDto, makeupList, $makeupsUl);
		
		//道具
		$("#viewCommonProps").attr("sv", viewInfoDto.commonProps);
		var $commonPropsUl = $("#commonPropsFilterUl");
		var commonPropList = filterDto.commonPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, commonPropList, $commonPropsUl);
		
		//特殊道具
		$("#viewSpecialProps").attr("sv", viewInfoDto.specialProps);
		var $speialPropsUl = $("#specialPropsFliterUl");
		var specialPropList = filterDto.specialPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, specialPropList, $speialPropsUl);
		
		//拍摄状态
		$("#viewShootStatus").attr("value", viewInfoDto.shootStatusValue);
		//备注
		$("#viewRemark").attr("value", viewInfoDto.remark);
		
		//清除另外一种情况的html代码
		$("#otherShootTables").empty();
	}else {
		//拍摄未完成/加戏
		//清空已完成拍摄模块的html代码
		$("#completeShootTable").empty();
		//如果是电影类型就不展示集次信息
		if (crewType == 0 || crewType == 3) {
			$("#NoFinishFilmViewid").attr("value", viewInfoDto.viewId);
			$("#noFinishFilmSeriesNo").attr("value", 1);
			$("#noFinishFilmViewNo").attr("value", viewInfoDto.viewNo);
//			if (saveType == 'update') {
//				$("#noFinishFilmViewNo").css("color", "grey");
//				$("#noFinishFilmViewNo").attr("readonly", "true");
//			}
			$("#noFinishFilmPageCount").attr("value", viewInfoDto.pageCount);
			$("#noFinishTvbTr").empty();
		}else {
			$("#noFinishFilmTr").empty();
			$("#noFinishTvbViewId").attr("value", viewInfoDto.viewId);
			
			$("#noFinishTvbSeriesNo").attr("value", viewInfoDto.seriesNo);
//			if (saveType == 'update') {
//				$("#noFinishTvbSeriesNo").css("color", "grey");
//				$("#noFinishTvbSeriesNo").attr("readonly", "true");
//			}
			
			$("#noFinishTvbViewNo").attr("value", viewInfoDto.viewNo);
//			if (saveType == 'update') {
//				$("#noFinishTvbViewNo").css("color", "grey");
//				$("#noFinishTvbViewNo").attr("readonly", "true");
//			}
			$("#noFinishTvbPageCount").attr("value", viewInfoDto.pageCount);
		}
		
		//气氛
		$("#noFinishViewAtmosphere").attr("value", viewInfoDto.atmosphereName);
		var $noFinishAtmosphereUl = $("#noFinishViewAtmosphere");
		var atmosphereList = filterDto.atmosphereList;
		addNoFinishUlData(filterDto, atmosphereList, $noFinishAtmosphereUl);
		
		//场景内外
		$("#noFinishViewSite").attr("value", viewInfoDto.site);
		if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
			var noFinishSiteUl = $("#noFinishViewSite");
			var siteList = filterDto.siteList;
			//取出ul对象
			var noFinishSiteUlArr = [];
			noFinishSiteUlArr.push("	<ul class='dropdown_box'>");
			if (siteList != null) {
				for(var i = 0; i < siteList.length; i++){
					noFinishSiteUlArr.push("	<li>");
					var  noFinishSite = siteList[i];
					noFinishSiteUlArr.push("	<a href='javascript:void(0)'>"+noFinishSite+"</a>");
					noFinishSiteUlArr.push("	</li>");
				}
			}
			
			noFinishSiteUlArr.push("	<span class='arrows_up'></span>");
			noFinishSiteUlArr.push("	</ul>");
			noFinishSiteUl.after(noFinishSiteUlArr.join(""));
		}
		//特殊提醒
		$("#noFinishSpecialRemind").val(viewInfoDto.specialRemind);
		var $noFinishSpecialRemindUl = $("#noFinishSpecialRemind");
		var specialRemindList = filterDto.specialRemindList;
		addNoFinishUlData(filterDto, specialRemindList, $noFinishSpecialRemindUl);
		
		//主要内容
		$("#noFinishMainContent").attr("value", viewInfoDto.mainContent);
		
		//主场景
		$("#noFinishFirstLocation").attr("value", viewInfoDto.firstLocation);
		var $noFinishFirstLocationUl = $("#noFinishFirstLocation");
		var FirstLocationList = filterDto.firstLocationList;
		addNoFinishUlData(filterDto, FirstLocationList, $noFinishFirstLocationUl);
		
		//次级场景
		$("#noFinishSecondLocation").attr("value", viewInfoDto.secondLocation);
		var $noFinishSecondLocationUl = $("#noFinishSecondLocation");
		var secondLocationList = filterDto.secondLocationList;
		addNoFinishUlData(filterDto, secondLocationList, $noFinishSecondLocationUl);
		
		//三级场景
		$("#noFinishThirdLocation").attr("value", viewInfoDto.thirdLocation);
		var $noFinishThirdLocationUl = $("#noFinishThirdLocation");
		var thirdLocationList = filterDto.thirdLocationList;
		addNoFinishUlData(filterDto, thirdLocationList, $noFinishThirdLocationUl);
		
		//拍摄地点
		$("#noFinishViewShootLocation").attr("value", viewInfoDto.shootLocation);
		var $noFinishShootLocationUl = $("#noFinishViewShootLocation");
		var shootLocationRegionList = filterDto.shootLocationRegionList;
		addShootLocationData(filterDto, shootLocationRegionList, $noFinishShootLocationUl);
		//拍摄地域
		if(viewInfoDto.shootRegion) {
			$("#shootReginValue").text("(" + viewInfoDto.shootRegion + ")");
		} else {
			$("#shootReginValue").text("");
		}
		var $noFinishShootReginUl = $("#shootReginValue");
		var shootReginList = filterDto.shootRegionList;
		addShootReginData(filterDto, shootReginList, $noFinishShootReginUl);
		
		//主要演员
		$("#noFinishViewMajorActor").attr("sv", viewInfoDto.majorActor);
		var $noFinishMajorActorUl = $("#noFinishMajorActorUl");
		var majorRoleList = filterDto.majorRoleList;
		//当后台传递的参数不为空时取出主要演员的信息
		addUlData(filterDto, majorRoleList, $noFinishMajorActorUl);
		
		//特约演员
		$("#noFinishViewGuestActor").attr("sv", viewInfoDto.guestActor);
		var $guestActorUl = $("#noFinishGuestActorUl");
		var guestRoleList = filterDto.guestRoleList;
		//当后台传递的参数不为空时取出特约演员的信息
		addUlData(filterDto, guestRoleList, $guestActorUl);
		
		//群众演员
		$("#noFinishViewMassActor").attr("sv", viewInfoDto.massesActor);
		var $massActorUl = $("#noFinishMassActorUl");
		var massesRoleList = filterDto.massesRoleList;
		//当后台传递的参数不为空时取出群众演员的信息
		addUlData(filterDto, massesRoleList, $massActorUl);
		
		//服装
		$("#noFinishViewClothes").attr("sv", viewInfoDto.clothes);
		var $clothesActorUl = $("#noFinishClothesUl");
		var clotheList = filterDto.clotheList;
		//当后台传递的参数不为空时取出服装的信息
		addUlData(filterDto, clotheList, $clothesActorUl);
		
		//化妆
		$("#noFinishViewMakeups").attr("sv", viewInfoDto.makeups);
		var $makeupsUl = $("#noFinishMakeupsUl");
		var makeupList = filterDto.makeupList;
		//当后台传递的参数不为空时取出化妆的信息
		addUlData(filterDto, makeupList, $makeupsUl);
		
		//道具
		$("#noFinishViewCommonProps").attr("sv", viewInfoDto.commonProps);
		var $commonPropsUl = $("#noFinishCommonPropsUl");
		var commonPropList = filterDto.commonPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, commonPropList, $commonPropsUl);
		
		//特殊道具
		$("#noFinishViewSpecialProps").attr("sv", viewInfoDto.specialProps);
		var $speialPropsUl = $("#noFinishSpecialPropsUl");
		var specialPropList = filterDto.specialPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, specialPropList, $speialPropsUl);
		
		//拍摄状态
		$("#noFinishViewShootStatus").attr("value", viewInfoDto.shootStatusValue);
		//备注
		$("#noFinishViewRemark").attr("value", viewInfoDto.remark);
	}
	//初始化商植信息
	if (viewInfoDto.viewId == null || viewInfoDto.viewId == undefined || viewInfoDto.viewId == '') {
		$("#insertAdvert").css("display", "none");
	}
	
	//获取以前的商值数据
	var $advertInfoTable = $("#advertInfoTable");
	var advertInfoTableArr = [];
	advertInfoTableArr.push("	<tr><th>广告名称</th><th>类型</th><th>操作</th></tr>");
	
	for(var j=0; j<advertInfoList.length; j++){
		var advert = advertInfoList[j];
		advertInfoTableArr.push("	<tr>");
		advertInfoTableArr.push("	<td><input class='advert-name-input' type='text' value='"+advert.advertName+"' readonly onblur='updateAdvertName(this, \""+advert.advertId+"\")' onfocus='inputAdvertName(this)'/></td>");
		advertInfoTableArr.push("	<td>");
		if (advert.advertType == 1 ) {
			advertInfoTableArr.push("	道具");
		}else if (advert.advertType == 2) {
			advertInfoTableArr.push("	台词");
		}else if (advert.advertType == 99) {
			advertInfoTableArr.push("	其他");
		}else if (advert.advertType == 3) {
			advertInfoTableArr.push("	场景");
		}
		
		//判断是否显示删除按钮
		if ((parentFromFlag=='scene' && isScenarioReadonly) || (parentFromFlag=='view' && isViewInfoReadonly)) { //表示当前拍摄状态为: 完成/删戏
			advertInfoTableArr.push("	</td><td></td>");
		}else {
			advertInfoTableArr.push("	</td><td><div class='icon icon_batdel' title='删除' onclick='deleteAdvertInfo(this, \""+advert.advertId+"\")'></div></td>");
		}
		advertInfoTableArr.push("	</tr>");
	}
	$advertInfoTable.append(advertInfoTableArr.join(""));
	
	//判端是否显示添加广告字段
	if ((parentFromFlag=='scene' && isScenarioReadonly) || (parentFromFlag=='view' && isViewInfoReadonly) ) { //表示当前拍摄状态为: 完成/删戏
		$("#addOneAdvert").css("display", "none");
	}
	
	//判端是否显示商值信息
	var $advertUl = $("#advertName");
	var advertInfoList = filterDto.advertInfoList;
	addNoFinishUlData(filterDto, advertInfoList, $advertUl);
	
	//渲染右侧文本框的数据
	loadContentData();
	
	}else if (saveType == 'new') { //表示是新增场景信息 
		//清空已完成拍摄模块的html代码
		$("#completeShootTable").empty();
		//如果是电影类型就不展示集次信息
		if (crewType == 0 || crewType == 3) {
//			$("#NoFinishFilmViewid").val("");
//			
//			$("#noFinishFilmViewNo").val("");
//			$("#noFinishFilmPageCount").val("");
			$("#noFinishTvbTr").empty();
		}else {
			$("#noFinishFilmTr").empty();
//			$("#noFinishTvbViewId").val("");
//			
//			$("#noFinishTvbSeriesNo").val("");
//			$("#noFinishTvbPageCount").val("");
		}
		
		//气氛
		$("#noFinishViewAtmosphere").val("");
		var $noFinishAtmosphereUl = $("#noFinishViewAtmosphere");
		var atmosphereList = filterDto.atmosphereList;
		addNoFinishUlData(filterDto, atmosphereList, $noFinishAtmosphereUl);
		
		//场景内外
		$("#noFinishViewSite").val("");
		if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
			var noFinishSiteUl = $("#noFinishViewSite");
			var siteList = filterDto.siteList;
			//取出ul对象
			var noFinishSiteUlArr = [];
			noFinishSiteUlArr.push("	<ul class='dropdown_box'>");
			for(var i = 0; i < siteList.length; i++){
				noFinishSiteUlArr.push("	<li>");
				var  noFinishSite = siteList[i];
				noFinishSiteUlArr.push("	<a href='javascript:void(0)'>"+noFinishSite+"</a>");
				noFinishSiteUlArr.push("	</li>");
			}
			noFinishSiteUlArr.push("	<span class='arrows_up'></span>");
			noFinishSiteUlArr.push("	</ul>");
			noFinishSiteUl.after(noFinishSiteUlArr.join(""));
		}
		
		//特殊提醒
		$("#noFinishSpecialRemind").val('');
		var $noFinishSpecialRemindUl = $("#noFinishSpecialRemind");
		var specialRemindList = filterDto.specialRemindList;
		addNoFinishUlData(filterDto, specialRemindList, $noFinishSpecialRemindUl);
		//主要内容
		$("#noFinishMainContent").attr("value", "");
		
		//主场景
		$("#noFinishFirstLocation").val("");
		var $noFinishFirstLocationUl = $("#noFinishFirstLocation");
		var FirstLocationList = filterDto.firstLocationList;
		addNoFinishUlData(filterDto, FirstLocationList, $noFinishFirstLocationUl);
		
		//次级场景
		$("#noFinishSecondLocation").val("");
		var $noFinishSecondLocationUl = $("#noFinishSecondLocation");
		var secondLocationList = filterDto.secondLocationList;
		addNoFinishUlData(filterDto, secondLocationList, $noFinishSecondLocationUl);
		
		//三级场景
		$("#noFinishThirdLocation").val("");
		var $noFinishThirdLocationUl = $("#noFinishThirdLocation");
		var thirdLocationList = filterDto.thirdLocationList;
		addNoFinishUlData(filterDto, thirdLocationList, $noFinishThirdLocationUl);
		
		//拍摄地点
		$("#noFinishViewShootLocation").val("");
		var $noFinishShootLocationUl = $("#noFinishViewShootLocation");
		var shootLocationRegionList = filterDto.shootLocationRegionList;
		addShootLocationData(filterDto, shootLocationRegionList, $noFinishShootLocationUl);
		
		//拍摄地域
		$("#shootReginValue").text("");
		var $noFinishShootReginUl = $("#shootReginValue");
		var shootReginList = filterDto.shootRegionList;
		addShootReginData(filterDto, shootReginList, $noFinishShootReginUl);
		
		//主要演员
		$("#noFinishViewMajorActor").val("");
		var $noFinishMajorActorUl = $("#noFinishMajorActorUl");
		var majorRoleList = filterDto.majorRoleList;
		//当后台传递的参数不为空时取出主要演员的信息
		addUlData(filterDto, majorRoleList, $noFinishMajorActorUl);
		
		//特约演员
		$("#noFinishViewGuestActor").val("");
		var $guestActorUl = $("#noFinishGuestActorUl");
		var guestRoleList = filterDto.guestRoleList;
		//当后台传递的参数不为空时取出特约演员的信息
		addUlData(filterDto, guestRoleList, $guestActorUl);
		
		//群众演员
		$("#noFinishViewMassActor").val("");
		var $massActorUl = $("#noFinishMassActorUl");
		var massesRoleList = filterDto.massesRoleList;
		//当后台传递的参数不为空时取出群众演员的信息
		addUlData(filterDto, massesRoleList, $massActorUl);
		
		//服装
		$("#noFinishViewClothes").val("");
		var $clothesActorUl = $("#noFinishClothesUl");
		var clotheList = filterDto.clotheList;
		//当后台传递的参数不为空时取出服装的信息
		addUlData(filterDto, clotheList, $clothesActorUl);
		
		//化妆
		$("#noFinishViewMakeups").val("");
		var $makeupsUl = $("#noFinishMakeupsUl");
		var makeupList = filterDto.makeupList;
		//当后台传递的参数不为空时取出化妆的信息
		addUlData(filterDto, makeupList, $makeupsUl);
		
		//道具
		$("#noFinishViewCommonProps").val("");
		var $commonPropsUl = $("#noFinishCommonPropsUl");
		var commonPropList = filterDto.commonPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, commonPropList, $commonPropsUl);
		
		//特殊道具
		$("#noFinishViewSpecialProps").val("");
		var $speialPropsUl = $("#noFinishSpecialPropsUl");
		var specialPropList = filterDto.specialPropList;
		//当后台传递的参数不为空时取出道具的信息
		addUlData(filterDto, specialPropList, $speialPropsUl);
		
		//拍摄状态
		$("#noFinishViewShootStatus").val("");
		//备注
		$("#noFinishViewRemark").val("");
		//隐藏商植信息
		$("#insertAdvert").css("display","none");
	}
}

//遍历集合拼接ul数据
function addUlData(filterDto, objList, $obj){
	if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
		//取出ul对象
		var objUlArr = [];
		for(var key in objList){
			objUlArr.push("	<li>");
			objUlArr.push("	<a href='javascript:void(0)' sid='19' title='"+objList[key]+"'>"+objList[key]+"</a>");
			objUlArr.push("	</li>");
		}
		$obj.append(objUlArr.join(""));
	}
}

//遍历未拍摄完成的集合拼接ul数据
function addNoFinishUlData(filterDto, objList, $obj){
	if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
		//取出ul对象
		var objUlArr = [];
		objUlArr.push("	<ul class='dropdown_box'>");
		for(var key in objList){
			objUlArr.push("	<li>");
			objUlArr.push("	<a href='javascript:void(0)'>"+objList[key]+"</a>");
			objUlArr.push("	</li>");
		}
		objUlArr.push("	<span class='arrows_up'></span>");
		objUlArr.push("	</ul>");
		$obj.after(objUlArr.join(""));
	}
}

//便利拍摄地集合拼接ul数据
function addShootLocationData(filterDto, objList, $obj){
	if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
		var objUlArr = [];
		objUlArr.push("	<ul class='dropdown_box'>");
		for(var i= 0; i< objList.length; i++){
			objUlArr.push("	<li>");
			if(objList[i].shootRegion == null || objList[i].shootRegion == undefined){
				objList[i].shootRegion = "";
			}
			objUlArr.push("	<a href='javascript:void(0)' shoot_regin='"+ objList[i].shootRegion +"'>"+ objList[i].shootLocationName +"</a>");
			objUlArr.push("	</li>");
		}
		objUlArr.push("	<span class='arrows_up'></span>");
		objUlArr.push("	</ul>");
		$obj.after(objUlArr.join(""));
	}
}
//遍历拍摄地域集合拼接ul数据
function addShootReginData(filterDto, objList, $obj){
	if (filterDto != '' && filterDto != 'undefined' && filterDto != null) {
		var objUlArr = [];
		objUlArr.push("	<ul class='shootregin_box' id='shootReginBox' style='display: none;'>");
		for(var key in objList){
			objUlArr.push("	<li>");
			objUlArr.push("	<a href='javascript:void(0)'>"+objList[key]+"</a>");
			objUlArr.push("	</li>");
		}
		objUlArr.push("	<span class='arrows_up'></span>");
		objUlArr.push("	</ul>");
		$obj.after(objUlArr.join(""));
	}
} 






//点击添加 按钮
function confirmAddAdvert(ev){
	//添加广告按钮功能
	var advertName = $("#advertName").val();
	var advertType = $("input[name='advertTypeRedio']:checked").eq(0).val();
	var viewId = $("input[name='viewId']").val();
	var status = $('input#shootStatus').val();
	if (advertName == '' || advertName == undefined || advertName == null) {
		parent.showErrorMessage('请输入广告名称');
		//alert("请输入广告名称");
		return;
	}
	if (viewId == null || viewId == '' || viewId == undefined) {
		parent.showErrorMessage('请先保存场景信息');
		return;
	}
	
	//判断当前场景状态，如果是完成或删戏给出提示信息
	if (status == 2 || status == 5) {
		parent.popupPromptBox("提示", "当前场景已经完成，是否要继续添加广告信息？", function() {
			$.ajax({
		        type:'post',
		        url:'/insiteAdvertManager/saveInsiteAdvert',
		        dataType:'json',
		        data: {advertName: advertName, viewId: viewId, advertType: advertType},
		        success:function(param){
		        	if (param.success) {
		        		var advertId = param.advertId;
		        		
		        		parent.showSuccessMessage(param.message);
		        		$("#advertName").val("");
		        		$("input[name='advertTypeRedio']").eq(0).click();
//		        		location.reload();
		        		
		        		//获取以前的商值数据
		        		var $advertInfoTable = $("#advertInfoTable");
		        		var advertInfoTableArr = [];
		        		
		    			advertInfoTableArr.push("	<tr>");
		    			advertInfoTableArr.push("	<td><input class='advert-name-input' type='text' value='"+advertName+"' readonly onblur='updateAdvertName(this, \""+advertId+"\")' onfocus='inputAdvertName(this)'/></td>");
		    			advertInfoTableArr.push("	<td>");
		    			if (advertType == 1 ) {
		    				advertInfoTableArr.push("	道具");
		    			}else if (advertType == 2) {
		    				advertInfoTableArr.push("	台词");
		    			}else if (advertType == 99) {
		    				advertInfoTableArr.push("	其他");
		    			}else if (advertType == 3) {
		    				advertInfoTableArr.push("	场景");
		    			}
		    			
		    			advertInfoTableArr.push("	</td><td><div class='icon icon_batdel' title='删除' onclick='deleteAdvertInfo(this, \"" + advertId + "\")'></div></td>");
		    			advertInfoTableArr.push("	</tr>");
		    		
		    			$advertInfoTable.append(advertInfoTableArr.join(""));
		    			
		    			//刷新场景表
						if (viewId != null && viewId != undefined && viewId != "") {
							//refreshViewRowWithNoRequest(shootLocation,atmosphereName,site,viewIds);
							parent.refreshViewGridRow({viewIds:viewId});
						}
		        	} else {
		        		parent.showErrorMessage(param.message);
		        	}
		        }
			});
			ev.stopPropagation();
		});
	}else if (status == 3) {
		parent.popupPromptBox("提示", "当前场景已删戏，是否要继续添加广告信息？", function() {
			$.ajax({
		        type:'post',
		        url:'/insiteAdvertManager/saveInsiteAdvert',
		        dataType:'json',
		        data: {advertName: advertName, viewId: viewId, advertType: advertType},
		        success:function(param){
		        	if (param.success) {
		        		var advertId = param.advertId;
		        		
		        		parent.showSuccessMessage(param.message);
		        		$("#advertName").val("");
		        		$("input[name='advertTypeRedio']").eq(0).click();
//		        		location.reload();
		        		
		        		//获取以前的商值数据
		        		var $advertInfoTable = $("#advertInfoTable");
		        		var advertInfoTableArr = [];
		        		
		    			advertInfoTableArr.push("	<tr>");
		    			advertInfoTableArr.push("	<td><input class='advert-name-input' type='text' value='"+advertName+"' readonly onblur='updateAdvertName(this, \""+advertId+"\")' onfocus='inputAdvertName(this)'/></td>");
		    			advertInfoTableArr.push("	<td>");
		    			if (advertType == 1 ) {
		    				advertInfoTableArr.push("	道具");
		    			}else if (advertType == 2) {
		    				advertInfoTableArr.push("	台词");
		    			}else if (advertType == 99) {
		    				advertInfoTableArr.push("	其他");
		    			}else if (advertType == 3) {
		    				advertInfoTableArr.push("	场景");
		    			}
		    			
		    			advertInfoTableArr.push("	</td><td><div class='icon icon_batdel' title='删除' onclick='deleteAdvertInfo(this, \"" + advertId + "\")'></div></td>");
		    			advertInfoTableArr.push("	</tr>");
		    		
		    			$advertInfoTable.append(advertInfoTableArr.join(""));
		    			
		    			//刷新场景表
						if (viewId != null && viewId != undefined && viewId != "") {
							//refreshViewRowWithNoRequest(shootLocation,atmosphereName,site,viewIds);
							parent.refreshViewGridRow({viewIds:viewId});
						}
		        	} else {
		        		parent.showErrorMessage(param.message);
		        	}
		        }
			});
			ev.stopPropagation();
		});
	}else {
		$.ajax({
	        type:'post',
	        url:'/insiteAdvertManager/saveInsiteAdvert',
	        dataType:'json',
	        data: {advertName: advertName, viewId: viewId, advertType: advertType},
	        success:function(param){
	        	if (param.success) {
	        		var advertId = param.advertId;
	        		
	        		parent.showSuccessMessage(param.message);
	        		$("#advertName").val("");
	        		$("input[name='advertTypeRedio']").eq(0).click();
//	        		location.reload();
	        		
	        		//获取以前的商值数据
	        		var $advertInfoTable = $("#advertInfoTable");
	        		var advertInfoTableArr = [];
	        		
	    			advertInfoTableArr.push("	<tr>");
	    			advertInfoTableArr.push("	<td><input class='advert-name-input' type='text' value='"+advertName+"' readonly onblur='updateAdvertName(this, \""+advertId+"\")' onfocus='inputAdvertName(this)'/></td>");
	    			advertInfoTableArr.push("	<td>");
	    			if (advertType == 1 ) {
	    				advertInfoTableArr.push("	道具");
	    			}else if (advertType == 2) {
	    				advertInfoTableArr.push("	台词");
	    			}else if (advertType == 99) {
	    				advertInfoTableArr.push("	其他");
	    			}else if (advertType == 3) {
	    				advertInfoTableArr.push("	场景");
	    			}
	    			
	    			advertInfoTableArr.push("	</td><td><div class='icon icon_batdel' title='删除' onclick='deleteAdvertInfo(this, \"" + advertId + "\")'></div></td>");
	    			advertInfoTableArr.push("	</tr>");
	    		
	    			$advertInfoTable.append(advertInfoTableArr.join(""));
	        		
	    			
	    			//刷新场景表
					if (viewId != null && viewId != undefined && viewId != "") {
						//refreshViewRowWithNoRequest(shootLocation,atmosphereName,site,viewIds);
						parent.refreshViewGridRow({viewIds:viewId});
					}
	        	} else {
	        		parent.showErrorMessage(param.message);
	        	}
	        }
		});
		ev.stopPropagation();
	}
	
}

//删除广告
function deleteAdvertInfo(own, advertId){
	var seriesNo = $("input[name='seriesNo']").val();
	var viewNo = $("input[name='viewNo']").val();
	var newGrid = parent.grid;
	//获取选中的场景
	var viewIds = "";
	if (newGrid != undefined) {
		viewIds = parent.grid.getSelectIds();
	}
	parent.popupPromptBox("提示", "确定要删除广告？", function() {
		$.ajax({
			type:'post',
			url:'/insiteAdvertManager/deleteAdvert',
			dataType:'json',
			data: {advertId: advertId},
			success:function(param){
				if (param.success) {
					parent.showSuccessMessage(param.message);
					$("#advertName").val("");
					$("input[name='advertTypeRedio']").eq(0).click();
//        		location.reload();
					$(own).parent("td").parent("tr").remove();
					//在场景表中引入场景详细信息面板时，添加广告后刷新场景表的表格
					if (typeof(parent.reloadPage)=='function') {
						parent.reloadPage(seriesNo, viewNo);
					}
					if (newGrid != undefined) {
						//刷新列表数据
						parent.refreshViewGridRow({viewIds: viewIds});
					}
				} else {
					parent.showErrorMessage(param.message);
				}
			}
		});
	});
}

//取消点击事件
function cancleAddAdvert(ev){
	//取消添加点击时间
	$('#advertForm').hide();
	$('#advertAddBtn').hide();
	$('#cancelAddBtn').hide();
	parent.Popup.hide();
	ev.stopPropagation();
}

//点击添加广告按钮
function addAdvertTitle(){
	//添加广告标题点击事件
	$('#advertForm').show();
	$('#advertAddBtn').css("display", "inline-block");
	$('#cancelAddBtn').css("display", "inline-block");
}

//添加body的样式及属性
function addBodyHtml(viewInfoDto){
	//取出head对象
	var $body = $("#viewDetailBody");
	if (viewInfoDto != null &&((parentFromFlag=='scene' && isScenarioReadonly) || (parentFromFlag=='view' && isViewInfoReadonly) 
			|| viewInfoDto.shootStatus == 2 || viewInfoDto.shootStatus == 5)) {
		$body.css({"min-width":"1px", background: "#FFFFFF"});
		$body.addClass("scene_completed");
	}else {
		$body.css({"min-width":"1px", background: "#FFFFFF"});
		$body.attr("id", "viewDetailInfoPage");
		$body.attr("onscroll", "targetScroll()");
	}
}

//当场景信息被修改时，设置场景详细信息页面隐藏项为被修改
function setViewIsChanged() {
	var isChanged = $("#isChanged").val();
	if (((parentFromFlag=='scene' && !isScenarioReadonly) || (parentFromFlag=='view' && !isViewInfoReadonly)) && isChanged == 0) {
		$("#isChanged").val(1);
	}
	
}

//生成滚动条
function targetScroll() {
   /*var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
   parent.Popup.scrollArrowTop(scrollTop);*/
}

//能够修改广告名称
function inputAdvertName(own){
	$(own).attr("readonly", false);
	$(own).removeClass("advert-name-input");
}

//更新广告名称
function updateAdvertName(own, advertId){
	//取出广告名称
	var advertName = $(own).val();
	//取出选中的场景id字符串
	var gridNew = parent.grid;
	var viewIds = "";
	if (gridNew != undefined) {
		viewIds = parent.grid.getSelectIds();
	}
	
	//使用ajax保存填写的信息
	$.ajax({
		type:'post',
		url:'/insiteAdvertManager/updateAdvertName',
		dataType:'json',
		data: {advertId: advertId, advertName: advertName},
		success:function(param){
			if (param.success) {
				//parent.showSuccessMessage(param.message);
				$(own).attr("readonly", true);
				$(own).addClass("advert-name-input");
				if (gridNew != undefined) {
					parent.refreshViewGridRow({viewIds: viewIds});
				}
			} else {
				parent.showErrorMessage(param.message);
			}
		}
	});
	
}

//添加os
function changeClass(own){
	var _this = $(own);
	if (_this.hasClass("major-os-button")) {
		_this.removeClass("major-os-button");
		_this.addClass("major-os-button-hover");
	}else {
		_this.removeClass("major-os-button-hover");
		_this.addClass("major-os-button");
	}
}

//计算页数
function calculateViewPage() {
	var viewId = $("#viewDetailId").val();
	$.ajax({
		url: "/scenarioManager/calculateViewPage",
		type: "post",
		data: {viewId: viewId},
		success: function(response) {
			if (!response.success) {
				parent.showErrorMessage(response.message);
				return;
			}
			var pageCount = response.pageCount;
			$("input[name=pageCount]").val(pageCount);
			parent.showSuccessMessage("操作成功");
		}
	});
}