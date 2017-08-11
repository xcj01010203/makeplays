  var crewType = null;   //剧组类型
  var selecationText = "";    //页面上选中的文字
  var bmSeriesViewNo = "";  //书签中的集场信息
  var seriesNoDtoList = null; //集场编号集合列表
  var needRefresh = false; //是否需要刷新;当进行批量删除后进行判断
  var picTimer;//定时器
  var viewObj;
  
$(document).ready(function () {
	topbarInnerText("拍摄管理&&剧本分析");
	//获取剧组类型
	getCrewType();
	
	//加载集场号面板
	loadSeriesNoAndViewNo(); 
	 
	//页面禁用右键
    $(document).on('contextmenu', function (e) {
        return false;
    });
    
	//初始化剧本内容框
	initMainContent(); 
	
	//初始化场景内容框
	initViewContent();
	
	//初始化右侧场景信息窗口的格式及大小
	initAdvertContent();
	
	//判断当前点击是否是左击事件
	isLeftClick(window.event || arguments.callee.caller.arguments[0]);//兼容火狐
	
	//初始化右键菜单文本框
	initRightContent();
	
	//隐藏集场号文本框
	$(".select_box").css("display", "none");
	
	//面板右侧场景框中确定/取消/删除 按钮样式初始化
    initViewButton();
    
    //初始化剧本上传页面
    loadScenarioUpload(); 

    //初始化导出窗口
    initExportWindow(); 
    
    //初始化重新分析页数窗口
    initRefreshPageWindow(); 
    
    //初始化重新分析角色窗口
    initRefreshFigureWindow(); 
    //初始化新角色窗口
    initNewRoleWindow();
    
    //初始化引导页面窗口
    initUploadGuideWindow(); 
    
    //加载上传结果窗口
    loadUploadResultWin();
    
    //判断是否有权限进行操作
    hasAuthJudge();
    
  //校验是否有发布内容
    queryHasReleaseContent();
  //初始化发布剧本弹窗
    initReleaseWin();
    if(isScenarioReadonly){
    	$(".icon_edit").remove();
    	$(".icon_prop_refresh").remove();
    	$(".icon_fabu_juben").remove();
    }
});

//获取剧组类型
function getCrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            
	            //如果剧组类型为电影，则隐藏集次面板
	            if (crewType == Constants.CrewType.movie || crewType == 3) {
	                $("#seriesNoTr").hide();
	               
	                //当前场次，电影剧本只能看到“场次”，而电视剧剧本能看到“集场”
	                $(".movieNo").show();
	                $(".tvplayNo").hide();
	                
	                $(".exportMovieOption").show();
	                $(".exportTvOption").hide();
	            } else {
	                $(".tvplayNo").show();
	                $(".movieNo").hide();
	                
	                $(".exportMovieOption").hide();
	                $(".exportTvOption").show();
	            }
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//获取当前剧组的行页数信息
function getCrewPageInfo(){
	$.ajax({
		url: "/scenarioManager/queryScenarioFormatInfo",
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#wordCount").val(response.wordCount);
				$("#lineCount").val(response.lineCount);
				if (response.pageIncludeTitle) {
					$("#pageIncludeTitle")[0].checked = true;
				} else {
					$("#pageIncludeTitle")[0].checked = false;
				}
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//页面初始时,加载集场号信息
function loadSeriesNoAndViewNo(){
	$.ajax({
		url: '/viewManager/querySeriesNoAndViewNo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				$(".icon_compare").attr("disabled", false);
				seriesNoDtoList = response.seriesNoDtoList; //获取集场号列表
	            //selecationText = "";    //页面上选中的文字
	            bmSeriesViewNo = response.bmSeriesViewNo; 
	            addSeriesNoAndViewNo(seriesNoDtoList); //加载集场号面板中的集场编号信息
	            initSeriesAndViewNo(bmSeriesViewNo); //书签中的集场信息
	            loadBatchDelViewWin(crewType); //加载批量删除窗口
	            loadViewMessage();
			}else{
				$(".icon_compare").attr("disabled", true);
				var noViewMessage = response.noViewMessage;
				if (noViewMessage != null && noViewMessage != '') {
					loadViewMessage(noViewMessage); //如果没有上传剧本,初始化提示信息
				}else {
					showErrorMessage(response.message);
				}
			}
		}
	});
}

//获取集场号,并将集场号添加进面板中
function addSeriesNoAndViewNo(seriesNoDtoList){
	//添加集次信息
	var seriesNoTr = $("#seriesNoTr");
	var seriesNoArr = [];
	seriesNoArr.push("<th>集：</th>");
	seriesNoArr.push( "	<td>");
	seriesNoArr.push("	<ul>");
	for(var i =0; i<seriesNoDtoList.length; i++){
		seriesNoArr.push("<li id = '"+seriesNoDtoList[i].seriesNo+"' onclick='clickseriesNo(this)'>"+seriesNoDtoList[i].seriesNo);
		seriesNoArr.push("<a>("+seriesNoDtoList[i].viewNoDtoList.length+")</a></li>");
	}
	seriesNoArr.push("	</ul>");
	seriesNoArr.push("	</td>");
	seriesNoTr.append(seriesNoArr.join(""));
	
	//添加场次信息
	var viewNoTr = $("#viewNoTr");
	var viewNoArr = [];
	viewNoArr.push("<th>");
	//当前为电影剧本时不显示场次信息
	//if (crewType == 0 || crewType == 3) {
		viewNoArr.push("<span>场：</span>");
	//}
	viewNoArr.push("	</th>");
	viewNoArr.push("	<td>");
	
	for(var i=0; i<seriesNoDtoList.length; i++){
		viewNoArr.push("	<ul seriesNo='"+seriesNoDtoList[i].seriesNo+"' style='display:none;'> ");
		var viewNoDtoList = seriesNoDtoList[i].viewNoDtoList;
		for(var j=0; j<viewNoDtoList.length; j++){
			var viewNoDto = viewNoDtoList[j];
			var titleContent = "";
			if (viewNoDto.isManualSave) {
				titleContent = "场景信息已手动保存";
				viewNoArr.push("<li sid='"+seriesNoDtoList[i].seriesNo+"-"+viewNoDto.viewNo+"' class='saved");
				
				if (!viewNoDto.isReaded) {
					titleContent += "&#10;剧本有更新";
					viewNoArr.push(" unread");
				}
				
				if (viewNoDto.hasNoGetRole) {
					titleContent += "&#10;有可能存在未提取的角色或道具";
					viewNoArr.push(" hasNoGetRole");
				}
				
				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNo(this)' sval='"+ viewNoDto.viewId +"'>"+viewNoDto.viewNo);
				viewNoArr.push("</li>");
			}else {
 				viewNoArr.push("<li sid='"+seriesNoDtoList[i].seriesNo+"-"+viewNoDto.viewNo+"' class='nosave");
 				if (!viewNoDto.isReaded) {
 					titleContent += "剧本有更新";
					viewNoArr.push(" unread");
				}
 				
 				if (viewNoDto.hasNoGetRole) {
 					titleContent += "&#10;有可能存在未提取的角色或道具";
 					viewNoArr.push(" hasNoGetRole");
				}
				
 				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNo(this)' sval='"+ viewNoDto.viewId +"'>"+viewNoDto.viewNo);
 				viewNoArr.push("</li>");
			}
		}
		viewNoArr.push("	</ul>");
	}
	viewNoArr.push("	</td>");
	viewNoTr.append(viewNoArr.join(""));
	
}

//点击集次触发的方法
function clickseriesNo(own){
    $(own).addClass("ji_current");
    $(own).siblings().removeClass("ji_current");
    
    var seriesNo = $(own).attr("id");
    $(".chang_tab_box").find("ul").hide();
    $(".chang_tab_box").find("ul[seriesNo="+ seriesNo +"]").show();
}

//点击场次触发的方法
function clickViewNo(own){
	viewObj = own;
	var _frame=$('#f_scene_create').contents();
    
    //判断是否修改
    var isChanged = _frame.find('input#isChanged').val();
    if (isChanged == 1) {
        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
        	_frame.find('input#isChanged').val(0);
        	clickViewNo(viewObj);
        });
        return false;
    }
	//用jquery获取场次数量
	//获取场次列表的长度
	var viewNoLength = seriesNoDtoList[0].viewNoDtoList.length;
    //查询场景信息 
    var viewId = $(own).attr("sval");
    var seriesViewNo = $(own).attr("sid");
//    searchSceneDetail(seriesViewNo);
    searchViewDetail(viewId);
    
    $("#seriesViewNoDiv").find("span").text(seriesViewNo);
    $("#movieViewNoDiv").find("span").text($(own).text() + "/" + viewNoLength);
    
    //添加点击效果
    $("#seriesNoTr").find("li[id=" + seriesViewNo.split("-")[0] + "]").click();
    $(own).parent("ul").parent("td").find("li").removeClass("chang_current");
    $(own).addClass("chang_current");
    $(own).removeClass("unread");
    var newTitle = $(own).attr("title").replace("剧本有更新", '');
    $(own).attr("title", newTitle);
    
    //收起集场面板
    $(".select_box").slideUp("200"); 
    $(".ji_chang_box").removeClass("ji_chang_box_down").addClass("ji_chang_box_up");
}

//打开或收起集场面板
function openOrClosePancle(own){
    var $self=$(own);
    if($self.hasClass("ji_chang_box_up")){
        $(".select_box").slideDown("200"); 
        $self.removeClass("ji_chang_box_up").addClass("ji_chang_box_down");
    }else if($self.hasClass("ji_chang_box_down")){
        $(".select_box").slideUp("200"); 
        $self.removeClass("ji_chang_box_down").addClass("ji_chang_box_up");
    }
}

//初始化剧本文本框
function initMainContent(){
	$('#main_content').jqxSplitter({
	    theme: theme,
	    orientation: 'vertical', 
	    width: '100%', 
	    resizable: false,
	    height: 'calc(100% - 50px)', 
	    panels: [
	        {size: '100%', collapsible: false}, 
	        {size: 377, min: 377, collapsible: false}
	    ]
	});
}

//初始化场景信息框
function initViewContent(){
	$('#right_main').jqxSplitter({
        orientation: 'horizontal', 
        width: '100%', 
        height: "100%", 
        resizable: false,
        showSplitBar: false,
        panels: [
            {size: 40, collapsible: false}, 
            {size:'100%', collapsible: false}
        ]
    });
}

//初始化右侧场景信息窗口的格式及大小
function initAdvertContent(){
	$('#con_right_bottom').jqxSplitter({
        orientation: 'horizontal', 
        width: '100%', 
        height: "100%", 
        resizable: false,
        showSplitBar: false,
        panels: [
            {size:'100%', collapsible: false},  
            {collapsible: false}
        ]
    });
}

//判断是否是左击事件
function isLeftClick(event) {
    var leftclick;
    if (!event) {
    	var event = window.event;
    	}
    if (event.which) {
    	leftclick = (event.which == 1);
    	}
    else if (event.button) {
    	leftclick = (event.button == 1);
    	}
    return leftclick;
}

/*function isLeftClick(e){
	debugger;
	var leftclick;
	var theEvent = window.event || e;
	if(!e){
		var theEvent = window.event || e;
	}
	if(theEvent.which){
		leftclick = (theEvent.which == 1);
	}
	else if(theEvent.button){
		leftclick = (theEvent.button == 1);
	}
	return leftclick;
}*/

//初始化右键菜单文本框
function initRightContent(){
	$("#addColorBox").jqxMenu({ 
		theme:theme, 
		width: '120px', 
		height: '280px',
		autoOpenPopup: false, 
		mode: 'popup'
	});
    
}

//当鼠标弹起时,是否弹出右键菜单选项
function isOpenRigthMenu(event){
	var contextMenu = $("#addColorBox");
    var selecation = '';
    if(document.selection){
        selecation = document.selection.createRange().text.toString();//ie
        if(selecation!=""){
            selecationText=selecation;
        } else {
            return ;
        }
    } else {
        selecation =document.getSelection().toString();
        if(selecation!=""){
            selecationText=selecation;
        }else{
            return ;
        }
    }
    
    var leftClick = isLeftClick(event) || $.jqx.mobile.isTouchDevice();
    if (leftClick) {
        var scrollTop = $(window).scrollTop();
        var scrollLeft = $(window).scrollLeft();
        var windowHeight = $(this).height();
        var boxY = parseInt(event.clientY) + 5 + scrollTop;
        
        if (boxY + 290 > windowHeight) {
            boxY = windowHeight - 290;
        }
        
        contextMenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, boxY);
        return false;
    }
}

//面板右侧场景框中确定/取消/删除 按钮样式初始化
function initViewButton(){
	$("#btnsure, #btncancle, #btndelete").jqxButton({
        theme: theme,
        width: '50px',
        height: '28px'
    });
}

//分析右键菜单的单击事件
function analysisRightClick(own){
	 //分析菜单的单击行为
    var frame= $('#f_scene_create').contents();
    frame.find("input#isChanged").val(1);
    //主要内容
    if($(own).is('#c_content')){
        if(selecationText.length>0){
            frame.find('input.scene_content').val(selecationText);
        }
        return false;
    }
    //主要演员
    if($(own).is('#c_majoractor')){
        if(selecationText.length>0){
            frame.find('div.performer_first ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //特约演员
    if($(own).is('#c_guestactor')){
        if(selecationText.length>0){
            frame.find('div.performer_special ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //群众演员
    if($(own).is('#c_messactor')){
        if(selecationText.length>0){
            frame.find('div.performer_common ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //主场景
    if($(own).is('#c_majorscene')){
        if(selecationText.length>0){
            frame.find('input.scene_first').val(selecationText);
        }
        return false;
    }
    //次场景
    if($(own).is('#c_secscene')){
        if(selecationText.length>0){
            frame.find('input.scene_second').val(selecationText);
        }
        return false;
    }
    //三级场景
    if($(own).is('#c_thirdscene')){
        if(selecationText.length>0){
            frame.find('input.scene_third').val(selecationText);
        }
        return false;
    }
    
    //服装
    if ($(own).is('#c_clothes')) {
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分服装",
    				text: '检测到您选择的服装中含有特殊字符，是否将当前服装拆分为多个服装？',
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
    							frame.find('div.clothes_info ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.clothes_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.clothes_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    
    //化妆
    if ($(own).is('#c_makeup')) {
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分化妆",
    				text: '检测到您选择的化妆中含有特殊字符，是否将当前化妆拆分为多个化妆？',
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
    							frame.find('div.makeup_info ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.makeup_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.makeup_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    //道具
    if($(own).is('#c_commonprop')){
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
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
    							frame.find('div.tool_main ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.tool_main ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.tool_main ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    //特殊道具
    if($(own).is('#c_specialprop')){
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
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
    							frame.find('div.tool_special ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.tool_special ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.tool_special ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
}

//保存方法
function saveView(){
	var _frame=$('#f_scene_create').contents();
	var shootLocation = "";
	var shootRegin = "";
    
	shootLocation = _frame.find("#noFinishViewShootLocation").val();
	shootRegin = _frame.find("#shootReginValue").text();
	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
    if(shootLocation=="") {
    	if(shootRegin) {
    		_frame.find("#shootReginValue").text("");
    		shootRegin="";
    	}
    }	
	if(shootLocation == "" && shootRegin == ""){
		saveViewInfo();
		return;
	}
	$.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegin},
		datatype: 'json',
		success: function(response){
			if(response.success){
				saveViewInfo();
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){
					saveViewInfo();
				});
			}
		}
	});
}


//保存或修改场景信息
function saveViewInfo(){
    var arr = [];
    var _frame=$('#f_scene_create').contents();
    
    var viewId = _frame.find("input[name=viewId]").val();
    
    //是否只读权限校验
    if(isScenarioReadonly) {
    	showErrorMessage("对不起，您没有权限进行修改");
    	$('#f_scene_create')[0].contentWindow.location.reload(true);
    	return;
    }
    
    //主要内容,校验
    var mainContent = _frame.find("input.scene_content").val();
    if (mainContent.length >= 250) {
        showErrorMessage("亲，主要内容太长，不能超过250个字哦");
        return;
    }
    //获取集场号
    var seriesNo = _frame.find('input.scene_set').val();
    var viewNo = _frame.find('input.scene_field').val();
    
    //主要演员
    _frame.find('div.performer_first ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_first').val(arr);
    arr.length=0;
    
    //特约演员        
    _frame.find('div.performer_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_special').val(arr);
    arr.length=0;
    
    //群众演员
    var _cList=[];// 群众演员的名称
    var figurantName='';        
    _frame.find('div.performer_common ul .tagInput').siblings('li').each(function(){
        if( /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) ) != null){
            figurantName= $.trim( $(this).text() ).replace(/[\(（](\d*)[\)）]/g, '');
            figurantName=figurantName.replace(/\s*/g,'');
            arr.push( figurantName+ '_' + /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) )[1] );
        }else{
            figurantName=$.trim( $(this).text());
            figurantName=figurantName.replace(/\s*/g,'');
            arr.push(figurantName);
        }
        _cList.push(figurantName);
    });
    _frame.find('input.performer_common').val(arr);
    arr.length=0;
        
    // 主要演员
    var _fList = _frame.find('input.performer_first').val().replace(/\s*/g,'').split(',');
        // 特约演员
    var _sList = _frame.find('input.performer_special').val().replace(/\s*/g,'').split(',');
    
    // 对演员是否有重复进行校验
    var isRepeat = false;
	for(var i = 0; i < _fList.length; i++){
	    // 主要演员
	    var f = _fList[i];
	    // 主要 与 特约 是否有重复
	    if($.inArray(f, _sList) != -1 && f != ''){
	        isRepeat = true;
	        showErrorMessage('保存失败！主要演员 与 特约演员 存在重复：' + f);    
	        break;
	    }
	    
	    // 主要 与 群众 是否有重复            
	    if($.inArray(f, _cList) != -1 && f != ''){
	        isRepeat = true;
	        showErrorMessage('保存失败！主要演员 与 群众演员 存在重复：' + f);    
	        break;
	    }
	}
    // 有重复 直接跳出
    if(isRepeat){
        return false;
    }
    
  //拍摄地域:
	var shootRegin = _frame.find("#shootReginValue").text();
	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
	_frame.find('input.shoot_regin_info').val(shootRegin);
    
    //普通道具
    arr.length=0;
    _frame.find('div.tool_main ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_main').val(arr);
    
    //特殊道具
    arr.length=0;
    _frame.find('div.tool_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_special').val(arr);
    
    //服装
    arr.length=0;
    _frame.find('div.clothes_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.clothes_info').val(arr);
    
    //化妆
    arr.length=0;
    _frame.find('div.makeup_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.makeups_info').val(arr);
    
  //拍摄状态校验
    var shootStatus = _frame.find('input#shootStatus').val();
    if (shootStatus ==  2 || shootStatus == 5) {
    	parent.popupPromptBox("提示", "当前场景已经完成，是否要继续保存？", function() {
    		 //向后台发送请求
    	    $.ajax({
    	        url:'/viewManager/saveViewInfo',
    	        data:_frame.contents().find('form').serialize(),
    	        type:"post",
    	        success:function(respone){
    	            if (respone.success) {
    	                _frame.find('input#isChanged').val(0);
    	                showSuccessMessage(respone.message);
    	                //window.location.reload(true);
    	                searchSceneDetail(viewId);
                		reloadPage(seriesNo, viewNo);
    	             } else {
    	                showErrorMessage(respone.message);
    	             }
    	        },
    	        error: function() {
    	            showErrorMessage("发送请求失败");
    	        }
    	    });
		});
    }else if (shootStatus == 3) {
    	parent.popupPromptBox("提示", "当前场景已删戏，是否要继续保存？", function() {
	   		 //向后台发送请求
	   	    $.ajax({
	   	        url:'/viewManager/saveViewInfo',
	   	        data:_frame.contents().find('form').serialize(),
	   	        type:"post",
	   	        success:function(respone){
	   	            if (respone.success) {
	   	                _frame.find('input#isChanged').val(0);
	   	                showSuccessMessage(respone.message);
	   	                //window.location.reload(true);
	   	                searchSceneDetail(viewId);
                		reloadPage(seriesNo, viewNo);
	   	             } else {
	   	                showErrorMessage(respone.message);
	   	             }
	   	        },
	   	        error: function() {
	   	            showErrorMessage("发送请求失败");
	   	        }
	   	    });
		});
	}else {
    	 //向后台发送请求
        $.ajax({
            url:'/viewManager/saveViewInfo',
            data:_frame.contents().find('form').serialize(),
            type:"post",
            success:function(respone){
                if (respone.success) {
                    _frame.find('input#isChanged').val(0);
                    showSuccessMessage(respone.message);
                    //window.location.reload(true);
                    searchSceneDetail(viewId);
                	reloadPage(seriesNo, viewNo);
                 } else {
                    showErrorMessage(respone.message);
                 }
            },
            error: function() {
                showErrorMessage("发送请求失败");
            }
        });
	}
}

//场景信息框中取消按钮
function cancleViewInfo(){
    var _frame=$('#f_scene_create').contents();
    var isChanged = _frame.find('input#isChanged').val();
    if (isChanged != 1) {
        return false;
    }

    popupPromptBox("提示", "确定要取消所做修改吗", function() {
        $('#f_scene_create')[0].contentWindow.location.reload(true);
    });
}

//删除场景信息
function deleteViewInfo(){
	if(isScenarioReadonly) {
		showErrorMessage("对不起，您没有权限进行删除");
		return false;
	}
	popupPromptBox("提示", "删除后将不可恢复，请确认是否删除?", function() {
        var _frame=$('#f_scene_create').contents();
        var viewId = _frame.find("input[name=viewId]").val();
        var seriesNo = _frame.find("input[name=seriesNo]").val();
        var viewNo = _frame.find("input[name=viewNo]").val();
        $.ajax({
            url:'/viewManager/deleteViewInfo',
            data: {viewIds: viewId},
            type:"post",
            async:true,
            success:function(respone){
                if (respone.success) {
                   showSuccessMessage(respone.message);
                   
                   var seriesViewNo = seriesNo+"-"+viewNo;
                   var noNext = false;
                   var noPre = false;
                   
                   var currScene = $('#viewNoTr li[sid="'+ seriesViewNo +'"]'); //当前场控件
		           
				   var nextScene = currScene.next('li');   //下一场控件
				   if (nextScene.length == 0) {
				       var nextSeriesLi = $("#seriesNoTr").find("li[class=ji_current]").next('li');
					   if (nextSeriesLi.length == 0) {
					      noNext = true;
					   }
				   }
		           var preScene = currScene.prev('li');   //下一场控件
		           if (preScene.length == 0) {
		               var preSeriesLi = $("#seriesNoTr").find("li[class=ji_current]").prev('li');
		               if (preSeriesLi.length == 0) {
		                   noPre = true;
		               }
	                }
                   
                   var currentJi = $("#seriesNoTr").find(".ji_current");
                   var totalViewCountStr = currentJi.find("a").text();
                   var totalViewCount = totalViewCountStr.substring(1, totalViewCountStr.length-1);
                   currentJi.find("a").text("(" + (totalViewCount-1) + ")");
                   
                   if (!noNext) {
                    searchNextScene(); //查询下一场
                   }
                   if (noNext && !noPre) {
                    searchPreScene(); //查询上一场
                   }
                   //按照指定的集场号重新请求页面
                   if (noNext && noPre) {
                    window.location.href="/viewManager/toScenarioManagePage";
                   }
                   currScene.remove();
                   if (totalViewCount == 1) {
                    currentJi.remove();
                   }
                   
                } else {
                   showErrorMessage(respone.message);
                }
            }
        });
    });
}

//判端打开剧本分析页面时,需要加载哪一场场景信息
function initSeriesAndViewNo(bmSeriesViewNo){
	var startSeriesNo = $("#startSeriesNoInput").val();
	//跳转到该页面时，加载第一场的内容
    if (startSeriesNo != null && startSeriesNo != "") {
    	clickseriesNo($("#seriesNoTr").find("li[id="+ startSeriesNo +"]"));
        clickViewNo($("#viewNoTr").find("ul[seriesno="+ startSeriesNo +"]").find("li").eq(0));
        
    } else if (bmSeriesViewNo != null && bmSeriesViewNo != "") {
        var seriesViewArr = bmSeriesViewNo.split("-");
        var seriesNo = seriesViewArr[0];
        clickseriesNo($("#seriesNoTr").find("li[id="+ seriesNo +"]"));
        clickViewNo($("#viewNoTr").find("li[sid="+ bmSeriesViewNo +"]"));
        
    } else {
        var firstSeriesLi = $("#seriesNoTr").find("li").eq(0);
        var firstViewLi = $("#viewNoTr").find("li").eq(0);
        
        clickseriesNo(firstSeriesLi);
        clickViewNo(firstViewLi);
    }
}

//点击剧本主窗口收起集场面板
function closeDownPancle(){
	//点击下方面板时，上方的面板收起
    $jcbox = $(".ji_chang_box");
    if($jcbox.hasClass("ji_chang_box_down")){
        $(".select_box").slideUp("200"); 
        $jcbox.removeClass("ji_chang_box_down").addClass("ji_chang_box_up");
    }
    Popup.hide();
}

/*如果后台返回该值，说明当前剧组没有上传剧本
 *把提示放在最后弹出是考虑到如果放前面，会在页面元素渲染完成之前提示，页面比较丑 
 */
function loadViewMessage(noViewMessage){
    if (hasImportScenarioAuth && !isScenarioReadonly && noViewMessage != "" && noViewMessage != 'undefined' && noViewMessage != null) {
        popupPromptBox("提示", noViewMessage, function() {
            $("#uploadScenarioWindow").jqxWindow("open");
        });
    }
}

//初始化剧本导出窗口
function initExportWindow(){
	$("#exportScenarioWindow").jqxWindow({
		theme:theme,  
		width: 250,
		height: 130, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: true,
		isModal: true,
		showCloseButton: false,
		resizable: true,
		cancelButton: $("#cancelExpBtn"),
   });
}

//点击剧本导出按钮
function confirmExport(){
   var exportType = $("input[name=exportOption]:checked").val();
   $("#exportScenarioWindow").jqxWindow("close");
   //显示正在下载中的样式
   /*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    
    var fileAddress ="";
   $.ajax({
		url: '/scenarioManager/exportScenario',
		type: 'post',
		data: {type:exportType},
		datatype: 'json',
		success: function(response){
			_LoadingHtml.hide();
            $(".opacityAll").hide();
			if (response.success) {
				fileAddress = response.downloadFilePath; 
           }else{
        	   showErrorMessage(response.message);
               return;
           }
			
			var form = $("<form></form>");
            form.attr("action","/fileManager/downloadFileByAddr");
            form.attr("method","post");
            form.append("<input type='hidden' name='address'>");
            form.find("input[name='address']").val(fileAddress);
            $("body").append(form);
            form.submit();
            
            form.remove();
		}
	});
}

//初始化反刷页数窗口
function initRefreshPageWindow(){
	$("#refreshPageWindow").jqxWindow({
		theme:theme,  
		width: 270,
		height: 200, 
		autoOpen: false,
		resizable: true,
		isModal: true,
		showCloseButton: true,
		resizable: false,
		cancelButton: $("#cancelReAnalyseBtn"),
		initContent: function() {
			//获取剧组的行页数信息
		    getCrewPageInfo();
		}
    });
}

//点击反刷页数确定按钮
function confirmRefreshPage(){
    $("#refreshPageWindow").jqxWindow("close");
    LayerShow("正在分析..");
    
    var wordCount = $("#wordCount").val();
    var lineCount = $("#lineCount").val();
    var pageIncludeTitle = false;
    
    if (wordCount == "" || lineCount == "") {
        showErrorMessage("请输入完整数值");
        return false;
    }
    
    if (isNaN(wordCount) || isNaN(lineCount)) {
        showErrorMessage("请输入数字");
        return false;
    }
    
    if ($("#pageIncludeTitle")[0].checked) {
    	pageIncludeTitle = true;
    }
    
    $.ajax({
        url: "/scenarioManager/refreshPage",
        type: "post",
        async: true,
        data: {wordCount: wordCount, lineCount: lineCount, pageIncludeTitle: pageIncludeTitle},
        success: function(respone) {
            if (respone.success) {
                showSuccessMessage(respone.message);
                //延迟一秒从新加载剧本分析页面
                setTimeout(function (){
                	window.location.href="/viewManager/toScenarioManagePage";
                }, 1000);
            } else {
                showErrorMessage(respone.message);
            }
            LayerHide();
        },
        failure: function() {
            
        }
    });
}

//初始化角色反刷窗口
function initRefreshFigureWindow(){
	$("#refreshFigureWindow").jqxWindow({
		theme:theme,  
		width: 270,
		height: 150, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: true,
		isModal: true,
		resizable: false
   });
}

//初始化角色反刷窗口
function initNewRoleWindow(){
	$("#newRoleListWindow").jqxWindow({
		theme:theme,  
		width: 800,
		height: 500, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: true,
		isModal: true,
		resizable: false
   });
}

//点击反刷角色的确定按钮
function refreshFigureFromExistRole(){
	$("#refreshFigureWindow").jqxWindow("close");
   
	LayerShow("正在分析..");
	$.ajax({
		url: "/scenarioManager/refreshFigure",
		type: "post",
		async: true,
		success: function(respone) {
			if (respone.success) {
				showSuccessMessage(respone.message);
				//延迟一秒重新加载剧本分析页面
				setTimeout(function (){
					window.location.href="/viewManager/toScenarioManagePage";
				}, 1000);
			} else {
				showErrorMessage(respone.message);
			}
			LayerHide();
		}
	});
}

//智能提取新角色
function analyseScenarioFigure() {
	$("#refreshFigureWindow").jqxWindow("close");
	LayerShow("正在提取..");
	$.ajax({
		url: "/scenarioManager/analyseScenarioFigure",
		type: "post",
		async: true,
		success: function(response) {
			LayerHide();
			if (response.success) {
				var roleInfoList = response.roleInfoList;
				if (roleInfoList.length == 0) {
					showInfoMessage("剧本中不存在未提取的有效角色");
					return;
				}
				
				var newRoleLabelArray = [];
				$.each(roleInfoList, function(index, item) {
					var singleRoleLabel = "<span class='role-name-tag selected' roleName='"+ item.roleName + "' sval='" + item.roleCount + "' onclick='checkSingleNewRole(this)'>"+ item.roleName + "<span>(" + item.roleCount + "场)" +"</span></span>";
					newRoleLabelArray.push(singleRoleLabel);
				});
				
				$("#newRoleList").empty();
				$("#newRoleList").append(newRoleLabelArray.join(""));
				$("#newRoleDesc").text("本次操作从剧本中共提取"+ roleInfoList.length +"个角色，请在下表中选择，选中的角色将加入角色表。");
				$("#newRoleListWindow").jqxWindow("open");
			} else {
				showErrorMessage(respone.message);
			}
		}
	});
}
//新角色全选复选框点击事件
function checkAllNewRole (own) {
	if (own.checked) {
		$("#newRoleList .role-name-tag").addClass("selected");
	} else {
		$("#newRoleList .role-name-tag").removeClass("selected");
	}
}
//单个新角色复选框点击事件
function checkSingleNewRole(own) {
	$(own).toggleClass("selected");
	
	var allChecked = true;
	var allRoleCheckbox = $("#newRoleList").find(".role-name-tag");
	$.each(allRoleCheckbox, function() {
		if (!$(this).hasClass("selected")) {
			allChecked = false;
			return false;
		}
	});
	if (allChecked) {
		$("#allNewRoleCheckbox").prop("checked", true);
	} else {
		$("#allNewRoleCheckbox").prop("checked", false);
	}
}
//关闭新角色窗口
function closeNewRoleWin() {
	$("#newRoleListWindow").jqxWindow("close");
}
//保存可能存在的角色
function saveProbablyRoles() {
	var checkedRole = $("#newRoleList").find(".role-name-tag.selected");
	if (checkedRole.length == 0) {
		showErrorMessage("请选择角色");
		return;
	}
	var roleNameArray = [];
	$.each(checkedRole, function() {
		var roleName = $(this).attr("roleName");
		var viewCount = $(this).attr("sval");
		roleNameArray.push(roleName + "-" + viewCount);
	});
	
	closeNewRoleWin();
	LayerShow("正在保存..");
	$.ajax({
		url: "/scenarioManager/saveProbablyRoles",
		type: "post",
		async: true,
		data: {roleNames: roleNameArray.toString()},
		success: function(response) {
			LayerHide();
			if (response.success) {
				showSuccessMessage("操作成功");
				setTimeout(function (){
					window.location.href="/viewManager/toScenarioManagePage";
				}, 1000);
			} else {
				showErrorMessage(response.message);
			}
		}
	});
	
}

//初始化剧本上传引导页面
function initUploadGuideWindow(){
	var $container = $("#step-container");
    var maxStep = 8;
    $container.on("click", ".btn-close", function(){
        closeGuide($container);
        
    }).on("click", ".btn-next", function (e) {
        var $parent = $(this).parent(),
            step = $parent.data("step");

        if(step < maxStep){
            var next = step + 1;
            gotoNext($parent, step, next);
        }else{
            closeGuide($container);
        }
        
    }).on("click", ".btn-step", function(){
        var $el = $(this),
            index = $el.index(),
            $step = $el.parents(".step:eq(0)");

        gotoNext($step, $step.data("step"), index + 1);
    });
}


//点击场次超链接查询场次信息
function searchSceneDetail(viewId) {
    searchViewDetail(viewId);
}

//根据集场编号查询出剧本及场景的详细信息
function searchViewDetail(viewId) {
    if (viewId != null && viewId != undefined && viewId != '' && viewId != 'undefined') {
    $.ajax({
    type:'post',
    url:'/viewManager/queryViewContent',
    data:{viewId: viewId},
    dataType:'json',
    async: false,
    success:function(response){
        if (response.success) {
        	var title = "";
        	var content = "";
        	if (response.title) {
        		title = response.title.replace(/\n/g, "<br>");
        	}
    		$('#con_middle #viewContent #viewTitle #title').html(title);
            if (response.noGetRoleNames != null && response.noGetRoleNames != "") {
                $('#noGetRoleNames').show();
                $('#noGetRoleNames').html("可能存在的角色：" + response.noGetRoleNames);
            } else {
                $('#noGetRoleNames').hide();
                $('#noGetRoleNames').html("");
            }
            if (response.noGetProps != null && response.noGetProps != "") {
                $('#noGetProps').show();
                $('#noGetProps').html("可能存在的道具：" + response.noGetProps);
            } else {
                $('#noGetProps').hide();
                $('#noGetProps').html("");
            }
            if (response.viewContent) {
            	content = response.viewContent.replace(/\n/g, "<br>");
            }
            $('#con_middle #viewContent span').html(content);
            $('#con_middle #viewContent').scrollTop(0);
        } else {
            showErrorMessage(response.message);
        }
    }
    });
    
    //跳转到场景详情页面
    $('#f_scene_create').attr('src','/viewManager/toViewDetailInfo?viewId=' + viewId + '&flag=scene');
    
    //设置超时信息,需要等待场景信息列表数据加载完成,根据数据添加属性
    setTimeout(function() {
        var _frame=$('#f_scene_create').contents();
        var isManualSave = _frame.find("input#isManualSave").val();
        if (isManualSave == "true") {
           $("#right_title_span").removeClass("nosave");
           $("#right_title_span").addClass("saved");
        } else {
           $("#right_title_span").removeClass("saved");
            $("#right_title_span").addClass("nosave");
        }
    }, 1000);
    
        //保存书签信息
//    $.ajax({
//           url: "/scenarioManager/saveSceBookMark",
//           type: "post",
//           data: {seriesViewNo: seriesViewNo},
//           success: function(respone) {
//               if (!respone.success) {
//            	   showErrorMessage(respone.message);
//               }
//           },
//           failure: function() {
//           }
//        });
    }
}
        
//跳转到上传文件页面
function uploadPlay() {
    $("#uploadScenarioWindow").jqxWindow("open");
}
     
//页数反刷
function reAnalysePage() {
    $("#refreshPageWindow").jqxWindow("open");
}
      
//人物反刷
function refreshFigure() {
    $("#refreshFigureWindow").jqxWindow("show");
}

//查询上一场信息
function searchPreScene() {
    var currSeriesNo = $("#seriesNoTr").find("li.ji_current").attr("id");    //当前集号
    var currViewNo = $("#viewNoTr").find("li.chang_current").text(); //当前场号
    var seriesViewNo = currSeriesNo + "-" + currViewNo;
    
    var currScene = $('#viewNoTr li[sid="'+ seriesViewNo +'"]'); //当前场控件
    if (currScene == null || currScene == '' || currScene == 'undefined' || currScene.attr('sid') == null) {
        return;
    }
    var preScene = currScene.prev('li');   //上一场控件
    if (preScene.length == 0) {
        var preSeriesLi = $("#seriesNoTr").find("li[class=ji_current]").prev('li');
        if (preSeriesLi.length == 0) {
            showInfoMessage("已经是第一场了");
            return false;
        } else {
//            preSeriesLi.click();
            var preSeriesNo = preSeriesLi.attr("id");
            var length = $("#viewNoTr").find("ul[seriesNo="+ preSeriesNo +"]").find("li").length;
            preScene = $("#viewNoTr").find("ul[seriesNo="+ preSeriesNo +"]").find("li").eq(length - 1);
        }
    }
    preScene.click();
    
}

//查询下一场信息
function searchNextScene() {
    var currSeriesNo = $("#seriesNoTr").find("li.ji_current").attr("id");    //当前集号
    var currViewNo = $("#viewNoTr").find("li.chang_current").text(); //当前场号
    var seriesViewNo = currSeriesNo + "-" + currViewNo;
    
    var currScene = $('#viewNoTr li[sid="'+ seriesViewNo +'"]'); //当前场控件
    if (currScene == null || currScene == '' || currScene == 'undefined' || currScene.attr('sid') == null) {
        return;
    }
    
    var nextScene = currScene.next('li');   //下一场控件
    if (nextScene.length == 0) {
        var nextSeriesLi = $("#seriesNoTr").find("li[class=ji_current]").next('li');
        if (nextSeriesLi.length == 0) {
            showInfoMessage("已经是最后一场了");
            return false;
        } else {
//            nextSeriesLi.click();
           var nextSeriesNo = nextSeriesLi.attr("id");
            nextScene = $("#viewNoTr").find("ul[seriesNo="+ nextSeriesNo +"]").find("li").eq(0);
        }
    }
    nextScene.click();
}
        
//保存或修改场景信息后刷新左侧集场次列表
function reloadPage(seriesNo, viewNo) {
    var currScene = $('#viewNoTr li[sid='+ seriesNo + '-' + viewNo +']');
    if (currScene.hasClass('nosave')) {
        currScene.removeClass('nosave');
        currScene.addClass('saved');
    }
}

//初始化剧本上传页面窗口,并跳转到剧本上传页面
function loadScenarioUpload() {
	$("#uploadScenarioWindow").jqxWindow({
         theme:theme,  
         width: 730,
         height: 635, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: false,
         modalZIndex: 1000,
         initContent: function() {
        	 $("#uploadScenarioDiv").find("iframe").attr("src", "/scenarioManager/toUploadScePage");
         }
    });
	
	$("#uploadScenarioWindow").on("close", function() {
		$.ajax({
			url: "/scenarioManager/classifyViewRole",
			type: "post",
			dataType: "json",
			success: function(response) {
				if (!response.success) {
					showErrorMessage(response.message);
					return;
				}
			}
		});
	});
}


//关闭演示示例后，重置窗口
function closeResizeUploadWindow(){
	$("#uploadScenarioWindow").jqxWindow('resize', 1065, 730);
}

//显现剧本上传参照格式图片
function showSampImg() {
    $("#showSampCheck").click();
}

//显示剧本上传页面
function showUploadSampl() {
    $("#uploadScenarioWindow").jqxWindow('resize', '800', 550);
}

//隐藏剧本上传页面
function hideUploadSampl() {
    $("#uploadScenarioWindow").jqxWindow('resize', 410, 120);
}

//重置剧本上传页面的大小
function resizeWindow() {
  var uploadWinWidth = $("#uploadScenarioWindow").width();
  if (uploadWinWidth == "985") {
      $("#uploadScenarioWindow").jqxWindow('resize', 440, 520);
  } else {
      $("#uploadScenarioWindow").jqxWindow('resize', 985, 520);
  }
}

//根据起始集场号,刷新剧本分析页面
function refreshPage(startSeriesNo) {
  window.location.href="/viewManager/toScenarioManagePage?startSeriesNo="+startSeriesNo;
}

//关闭剧本上传页面
function closeUploadWin() {
  $("#uploadScenarioWindow").jqxWindow("close");
}

//打开导出剧本窗口
function exportScenario() {
    $("#exportScenarioWindow").jqxWindow("open");
}

/***************************屏幕遮幕*****************************/
function loadDiv(message) {
    var sub = "<div style='z-index: 9999999; margin-left: -66px; margin-top: -24px; position: relative; width: 100px; height: 30px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;'>"
            +"<div style='float: left; overflow: hidden; width: 32px; height: 32px;' class='jqx-grid-load'/>" 
            + "<span style='margin-top: 10px; float: left; display: block; margin-left: 5px;' >" + message + "</span>" 
            + "</div></div>";
    var div = "<div id='_layer_'> " 
            + "<div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 99999; position: absolute;"
            + "left: 0; top: 0; overflow: hidden; display: none'>" 
            + "</div>" 
            + "<div id='_wait_' style='z-index: 9999999; position: absolute; width:430px;height:218px; display: none'  >" 
            + "<center>" 
            + sub
            + "</center>" 
            + "</div>" 
            + "</div>";
    return div;
}

//特殊显示信息时,需要的特殊样式处理
function LayerShow(message) {
    
	var addDiv = loadDiv(message);
    var element = $(addDiv).appendTo(document.body);
    $(window).resize(Position);
    var deHeight = $(document).height();
    var deWidth = $(document).width();
    Position();
    $("#_MaskLayer_").show();
    $("#_wait_").show();
}

//初始化定位信息
function Position() {
    $("#_MaskLayer_").width($(document).width());
    var deHeight = $(window).height();
    var deWidth = $(window).width();
    $("#_wait_").css({
        left : (deWidth - $("#_wait_").width()) / 2 + "px",
        top : (deHeight - $("#_wait_").height()) / 2 + "px"
    });
}

//隐藏并移除特殊显示
function LayerHide() {
    $("#_MaskLayer_").hide();
    $("#_wait_").hide();
    del();
}

//移除特殊显示
function del() {
    var delDiv = document.getElementById("_layer_");
    delDiv.parentNode.removeChild(delDiv);
};

//显示左侧列表框
function showSelectPanel(data) {
  Popup.show({
      dataList: data.dataJson,
      right: data.right,
      arrowTop: data.arrowTop,
      title: data.title,
      multiselect: data.multiselect,
      currentTarget: data.currentTarget,
      top: 123
  });
}

function showGuidePage() {
    var $container = $("#step-container");
    $container.fadeIn();
    
    $container.find(".step").removeClass().addClass("step step1").data("step", 1);
}

//关闭引导页面
function closeGuide($container){
    $container.fadeOut();
}

//下一步
function gotoNext($el, curStep, newStep){
    $el.removeClass("step" + curStep).addClass("step" + newStep).data("step", newStep);
}

//加载批量删除场景窗口
function loadBatchDelViewWin(crewType) {
  $("#delViewWindow").jqxWindow({
         theme:theme,  
         width: 1000,
         height: 600, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: false,
         isModal: true,
         showCloseButton: true,
         modalZIndex: 10,
         cancelButton: $("#cancelDelBtn"),
         initContent: function() {
        	 
            var $panelObj = $("#viewNoDiv");
            //加载批量删除页面的集场号信息
            loadBatchDeleteNO(seriesNoDtoList, $panelObj);
            
            $panelObj.on("click", ".seriesNoTr :checkbox", function() {
                if (this.checked) {
                    $(this).parents(".seriesNoTr").next(".viewNoTr").find(":checkbox").prop("checked", true);
                } else {
                    $(this).parents(".seriesNoTr").next(".viewNoTr").find(":checkbox").prop("checked", false);
                }
            }).on("click", ".viewNoTr :checkbox", function() {
                var viewNosLength = $(this).parents(".viewNoTr").find(":checkbox").length;
                var checkedLength = $(this).parents(".viewNoTr").find(":checked").length;
                if (viewNosLength == checkedLength) {
                    $(this).parents(".viewNoTr").prev(".seriesNoTr").find(":checkbox").prop("checked", true);
                } else {
                    $(this).parents(".viewNoTr").prev(".seriesNoTr").find(":checkbox").prop("checked", false);
                }
            });
         }
    });
    
  	$("#delViewWindow").on("close", function() {
	      if (needRefresh) {
	          window.location.href="/viewManager/toScenarioManagePage";
	      }
  	});
}

//点击批量删除页面的删除按钮
function confirmBatchDelView(){
    var checkedBox = $("#viewNoDiv").find(".viewNoTr :checked");
    
    var viewIds = "";
    $.each(checkedBox, function(index) {
        if(index == 0){
            viewIds = $(this).val();
        } else {
            viewIds += ","+$(this).val();
        }
    });
    
    if (viewIds == "") {
        showErrorMessage("请选择需要删除的场景!");
        return;
    }
    
    popupPromptBox("提示", "删除后将不可恢复，请确认是否删除?", function() {
        LayerShow("正在删除..");
        $.ajax({
            url: "/viewManager/deleteViewInfo",
            type: "post",
            data: {viewIds: viewIds},
            dataType: "json",
            async: true,
            success: function(response) {
                LayerHide();
                if (response.success) {
                    $("#viewNoDiv").find(":checked").parent("label").remove();
                    needRefresh = true;
                    
                    if ($("#viewNoDiv").find("input[type=checkbox]").length == 0) {
                       window.location.href="/viewManager/toScenarioManagePage";
                    }
                    
                    showSuccessMessage(response.message);
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
    });
}

//加载批量删除界面的集场号信息及其操作
function loadBatchDeleteNO(seriesNoDtoList){
	 var $panelObj = $("#viewNoDiv");
     var table_array = ["<table>"];
     //拼接集场次信息
     for(var i=0; i<seriesNoDtoList.length; i++){
    	 var seriesNoDto = seriesNoDtoList[i];
    	 var seriesNo = seriesNoDto.seriesNo;
    	 
    	 var seriesNoTrStr = "<tr class='seriesNoTr'><td><label><input type='checkbox' class='del-checkbox'/><span>第"+ seriesNo +"集</span></label></td></tr>";
    	//电影剧本不需要显示集次
         if (crewType == Constants.CrewType.movie || crewType == 3) { //此代表电影的常量来自 /js/constants.js中
          seriesNoTrStr = "<tr class='seriesNoTr'><td><label><input type='checkbox' class='del-checkbox'/><span>全选</span></label></td></tr>";
         } else {
          seriesNoTrStr = "<tr class='seriesNoTr'><td><label><input type='checkbox' class='del-checkbox'/><span>第"+ seriesNo +"集</span></label></td></tr>";
         }
         
         var viewno_array = ["<tr class='viewNoTr' seriesNo="+ seriesNo +"><td>"];
         table_array.push(seriesNoTrStr);	
         
         //取出场次信息的列表
         var viewNoDtoList = seriesNoDto.viewNoDtoList;
         //取出场次信息
         for(var j = 0; j<viewNoDtoList.length; j++){
        	 var viewNoDto = viewNoDtoList[j];
        	 var viewNo = viewNoDto.viewNo;
             var viewId = viewNoDto.viewId;
             //电影剧本不需要显示集次
             if (crewType == Constants.CrewType.movie || crewType == 3) {
                 viewno_array.push("<label><input type='checkbox' value="+ viewId +"  class='del-checkbox'/><span title="+ viewNo +">"+ viewNo +"</span></label>");
             } else {
                 viewno_array.push("<label><input type='checkbox' value="+ viewId +"  class='del-checkbox'/><span title="+ seriesNo + "-" + viewNo +">"+ seriesNo + "-" + viewNo +"</span></label>");
             }
         }
         viewno_array.push("</td></tr>");
         table_array.push(viewno_array.join(''));
     }
     table_array.push("</table>");
     
     $panelObj.append(table_array.join(''));
     
     //初始化批量删除页面的点击方法及是否选中/全选 方法
     $panelObj.on("click", ".seriesNoTr :checkbox", function() {
         if (this.checked) {
             $(this).parents(".seriesNoTr").next(".viewNoTr").find(":checkbox").prop("checked", true);
         } else {
             $(this).parents(".seriesNoTr").next(".viewNoTr").find(":checkbox").prop("checked", false);
         }
     }).on("click", ".viewNoTr :checkbox", function() {
         var viewNosLength = $(this).parents(".viewNoTr").find(":checkbox").length;
         var checkedLength = $(this).parents(".viewNoTr").find(":checked").length;
         if (viewNosLength == checkedLength) {
             $(this).parents(".viewNoTr").prev(".seriesNoTr").find(":checkbox").prop("checked", true);
         } else {
             $(this).parents(".viewNoTr").prev(".seriesNoTr").find(":checkbox").prop("checked", false);
         }
     });
}

//批量删除
function batchDelete() {
  $("#delViewWindow").jqxWindow("open");
}

//加载上传结果页面
function loadUploadResultWin() {
  $("#uploadResultWindow").jqxWindow({
      theme:theme,  
      width: 1020,
      height: 740, 
      autoOpen: false,
      maxWidth: 2000,
      maxHeight: 1500,
      resizable: false,
      modalZIndex: 100, 
      isModal: true,
      showCloseButton: false,
      initContent: function() {
         //集-场号面板
        $.ajax({
            url: "/scenarioManager/queryUploadResultData",
            dataType: "json",
            type: "post",
            async: true,
            success: function(response) {
                if (response.success) {
                    var skipOrReplaceSceMap = response.skipOrReplaceSceMap;
                    var $containter = $("#viewNoResult");
                    
                    $.each(skipOrReplaceSceMap, function(key,values){ 
						var seriesnoArray = ["<div class='result-series-viewno'>"];
						
						//电影类型的剧组不现实集次
						if (crewType == Constants.CrewType.movie || crewType == 3) {
						    seriesnoArray.push("<div class='result-seriesno'><input type='checkbox'>全选</div>");
						} else {
						    seriesnoArray.push("<div class='result-seriesno'><input type='checkbox'>第" + key + "集</div>");
						}
						
						$.each(values ,function(index, item) {
						    if (crewType == Constants.CrewType.movie || crewType == 3) {
						        seriesnoArray.push("<div class='result-viewno'><input type='checkbox' value=" + key + "-" + item + "><span>" + item + "</span></div>");
						    } else {
						        seriesnoArray.push("<div class='result-viewno'><input type='checkbox' value=" + key + "-" + item + "><span>" + key + "-" + item + "</span></div>");
						    }
                        });
                        
                        seriesnoArray.push("</div>");
                        
                        $containter.append(seriesnoArray.join(""));
					});
                    
                    //初始化点击对比页面的集场号是触发的方法
                    $containter.on("click", ".result-viewno span", function() {
                      var seriesViewno = $(this).prev("input:checkbox").val();
                      $("#viewCompare").attr("src", "/viewManager/toScenarioComparePage?seriesViewNo=" + seriesViewno);
                      
                      $(this).prev(":checkbox").prop("checked", true);
                      checkIsCheckedAll($(this).prev(":checkbox"));
                      
                    }).on("click", ".result-seriesno :checkbox", function(ev) {
                      if (this.checked) {
                          $(this).parent("div").siblings("div .result-viewno").find(":checkbox").prop("checked", true);
                      } else {
                          $(this).parent("div").siblings("div .result-viewno").find(":checkbox").prop("checked", false);
                      }
                      
                      ev.stopPropagation();
                    }).on("click", ".result-viewno :checkbox", function() {
                      checkIsCheckedAll($(this));
                      
                    }).on("click", ".result-seriesno", function() {
                      if ($(this).hasClass("up")) {
                          $(this).removeClass("up");
                          $(this).siblings(".result-viewno").slideDown();
                          
                      } else {
                          $(this).addClass("up");
                          $(this).siblings(".result-viewno").slideUp();
                      }
                    });
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
      }
  });
  
  //关闭上传结果页面
  $("#uploadResultWindow").on("close", function() {
  });
}

//点击取消按钮
function confirmCancelDealResult(){
	var $containter = $("#viewNoResult");
    var existViewNo = $containter.find(".result-viewno");
    if (existViewNo.length > 0) {
    	popupPromptBox("提示", "关闭后未处理的数据会默认执行\"跳过\"操作，是否继续？", function() {
        	//拼接场景号字符串
        	var seriesViewNoStr = "";
        	for(var i=0; i<existViewNo.length; i++){
        		if (i == 0) {
        			seriesViewNoStr = $(existViewNo[i]).children().text();
				}else {
					seriesViewNoStr = seriesViewNoStr + "," + $(existViewNo[i]).children().text();
				}
        	}
            cancelOperate(seriesViewNoStr);
        });
    } else {
        cancelOperate("");
    }
}

//点击剧本对比页面的跳过按钮
function confirmSkip(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    if (checkedBoxs.length == 0) {
        showErrorMessage("请选择需要跳过的场次");
        return false;
    }
    //从数据库中移除选中的场景
    var checkSeriesViewNoStr = "";
    for(var i =0; i<checkedBoxs.length; i++){
    	if (i == 0) {
    		checkSeriesViewNoStr = $(checkedBoxs[i]).val();
		}else {
			checkSeriesViewNoStr = checkSeriesViewNoStr + "," + $(checkedBoxs[i]).val();
		}
    }
    
    $.ajax({
        url: "/viewManager/cancelOperate",
        dataType: "json",
        async: false,
        data:{seriesViewNoStr: checkSeriesViewNoStr},
        success: function(response) {
        	if (!response.success) {
        		showErrorMessage(response.message);
			}
        }, 
        failure: function() {
            showErrorMessage("网络故障");
        }
    });
    
    $containter.find(":checked").parent("div").remove();
    showSuccessMessage("操作成功");
    
    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
    
    if ($containter.find(".result-series-viewno").find("div").length == 0) {
    	window.location.href="/viewManager/toScenarioManagePage";
    }
}

//点击对比界面的只替换剧本按钮
function confirmReplaceSec(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    
    var seriesViewNos = "";
    $.each(checkedBoxs, function(index) {
        if(index == 0){
            seriesViewNos = $(this).val();
        } else {
            seriesViewNos += ","+$(this).val();
        }
    });
    
    if (seriesViewNos == "") {
        showErrorMessage("请勾选需要替换的场次");
        return false;
    }
    
    popupPromptBox("提示", "是否确定只将所选场次的剧本替换为新的内容？", function() {
        $.ajax({
            url: "/viewManager/replaceSecBatch",
            type: "post",
            data: {seriesViewNoStr: seriesViewNos},
            dataType: "json",
            async: false,
            success: function(response) {
                if (response.success) {
                    $containter.find(":checked").parent("div").remove();
                    showSuccessMessage(response.message);
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                    if ($containter.find(".result-series-viewno").find("div").length == 0) {
                    	window.location.href="/viewManager/toScenarioManagePage";
                    }
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
    });
}

//点击对比界面的全替换按钮
function confirmReplaceAll(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    
    var seriesViewNos = "";
    $.each(checkedBoxs, function(index) {
	    if(index == 0){
	        seriesViewNos = $(this).val();
	    } else {
	        seriesViewNos += ","+$(this).val();
	    }
	});
	
	if (seriesViewNos == "") {
	    showErrorMessage("请勾选需要替换的场次");
	    return false;
	}
	
	popupPromptBox("提示", "是否确定将所选场次的剧本和场景全部替换为新的内容？", function() {
	    $.ajax({
            url: "/viewManager/replaceViewBatch",
            type: "post",
            data: {seriesViewNoStr: seriesViewNos},
            dataType: "json",
            async: false,
            success: function(response) {
                if (response.success) {
                    $containter.find(":checked").parent("div").remove();
                    showSuccessMessage(response.message);
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                    if ($containter.find(".result-series-viewno").find("div").length == 0) {
                    	window.location.href="/viewManager/toScenarioManagePage";
                    }
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
	});
}

//处理上传结果时取消操作
function cancelOperate(seriesViewNoStr) {
    $.ajax({
        url: "/viewManager/cancelOperate",
        dataType: "json",
        async: false,
        data:{seriesViewNoStr: seriesViewNoStr},
        success: function(response) {
        	if (response.success) {
        		 showSuccessMessage(response.message);
			}else{
				showErrorMessage(response.message);
			}
        }, 
        failure: function() {
            showErrorMessage("网络故障");
        }
    });
    
    window.location.href="/viewManager/toScenarioManagePage";
}

//判端是否全选中
function checkIsCheckedAll($this) {
    var totalLength = $this.parents("div .result-series-viewno").find(".result-viewno").length;
    var checkedLength = $this.parents("div .result-series-viewno").find(".result-viewno").find(":checked").length;
    if (totalLength == checkedLength) {
        $this.parents("div .result-series-viewno").find(".result-seriesno").find(":checkbox").prop("checked", true);
    } else {
        $this.parents("div .result-series-viewno").find(".result-seriesno").find(":checkbox").prop("checked", false);
    }
}

//显示上传结果页面
function showResultWindow() {
  $("#uploadResultWindow").jqxWindow("open");
}

//判断是否有权限进行编辑
function hasAuthJudge(){
	if(isScenarioReadonly) {
		$(".icon_man_refresh").remove();
		$(".icon_page_refresh").remove();
	}
	if (!hasBatchDeleteViewAuth || isScenarioReadonly) {
		$(".icon_batdel").remove();
	}
	if(!hasImportScenarioAuth || isScenarioReadonly) {
		$(".icon_uploading").remove();
	}
	if(!hasExportScenarioAuth) {
		$(".icon_export").remove();
	}
}

//点击集场面板时隐藏右侧弹出的选择窗口
function closeRgithSelectWin(){
	$(".right-pup-container").css("display", "none");
}

//道具提取
function refreshProp() {
	popupPromptBox("提示", "该操作将会根据服化道表提取场景的服化道信息，是否继续？", function() {
		LayerShow("正在分析..");
		$.ajax({
			url: "/scenarioManager/refreshProp",
			dataType: "json",
			success: function(response) {
				LayerHide();
				if (!response.success) {
					showErrorMessage(response.message);
					return;
				}
				showSuccessMessage("操作成功");
				//延迟一秒重新加载剧本分析页面
				setTimeout(function (){
					window.location.href="/viewManager/toScenarioManagePage";
				}, 1000);
			}
		});
	});
}


//校验是否有发布内容
function queryHasReleaseContent(){
	$.ajax({
		url: '/scenarioManager/checkHasNewEdit',
		type: 'get',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var hasNewEdit = response.hasNewEdit;
				if(hasNewEdit){
					$("#fabuJubenBtn").show();
					
					//自动弹窗提示发布
					if (response.needShow) {
						$("#releaseWin").jqxWindow("open");
						$("#autoShowPublishWin")[0].checked = false;
					} else {
						$("#autoShowPublishWin")[0].checked = true;
					}
				}else{
					$("#fabuJubenBtn").hide();
				}
				//发布按钮闪动效果
				releaseBtnMethod();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//发布按钮闪动效果
function releaseBtnMethod(){
	if($("#fabuJubenBtn").is(":hidden")){
    	if(picTimer != undefined){
    		clearInterval(picTimer);
    	}
    }else{
    	picTimer = setInterval(function(){
    		if($("#fabuJubenBtn").hasClass("high-light")){
    			$("#fabuJubenBtn").removeClass("high-light");
    			$("#fabuJubenBtn").css({"background": "url(../../images/fabu.png)","background-repeat": "no-repeat", "background-size":"24px"});
    		}else{
    			$("#fabuJubenBtn").addClass("high-light");
    			$("#fabuJubenBtn").css({"background": "url(../../images/fabu-hover.png)", "background-repeat": "no-repeat", "background-size":"24px"});
    		}
    	}, 500);
    }
}

//初始化发布剧本弹窗
function initReleaseWin(){
	$("#releaseWin").jqxWindow({
		theme: theme,
		width: 480,
		height: 400,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false, 
		initContent: function(){
			getReleaseContent();
		}
	});
}

//显示发布剧本弹窗
function releaseScenarioCon(){
	$("#releaseWin").jqxWindow("open");
}
//获取发布剧本内容
function getReleaseContent(){
	$.ajax({
		url: '/scenarioManager/queryPublishContent',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#releaseContent").val(response.publishContent);
			}
		}
	});
}
//发布剧本
function releaseContentEvt(){
	var title= $("#releaseTitle").val();
	var content = $("#releaseContent").val();
	
	var autoShowPublishWin = true;
	if ($("#autoShowPublishWin")[0].checked) {
		autoShowPublishWin = false;
	}
	
	showSuccessMessage("发布成功");
	$("#releaseWin").jqxWindow("close");
	$.ajax({
		url: '/scenarioManager/publishScenario',
		type: 'post',
		data: {"title": title, "content": content, "autoShowPublishWin": autoShowPublishWin},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#fabuJubenBtn").hide();
				releaseBtnMethod();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//取消发布
function cancelReleae(){
	$("#releaseWin").jqxWindow("close");
}


//跳转到剧本分析页面
function scenarioAnalysis(){
	window.location.href="/viewManager/toScenarioManagePage?pageType=1";
}
//跳转到剧本编辑页面
function scenarioEdit(){
	var _frame=$('#f_scene_create').contents();
    
    //是否修改
    var isChanged = _frame.find('input#isChanged').val();
    if (isChanged == 1) {
        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
        	window.location.href="viewManager/toScenarioManagePage?pageType=2";
        });
        return false;
    }else{
    	window.location.href="viewManager/toScenarioManagePage?pageType=2";
    }
	
}
//跳转到剧本对比页面
function scenarioCompare(){
	if($(".icon_compare").attr("disabled") == "disabled"){
		return;
	}else{
		var _frame=$('#f_scene_create').contents();
	    
	    //是否修改
	    var isChanged = _frame.find('input#isChanged').val();
	    if (isChanged == 1) {
	        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
	        	window.location.href="viewManager/toScenarioManagePage?pageType=3";
	        });
	        return false;
	    }else{
	    	window.location.href="viewManager/toScenarioManagePage?pageType=3";
	    }
		
	}
	
}