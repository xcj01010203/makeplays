var selecationText = "";    //页面上选中的文字
var timer;//保存定时器
var crewType;	//剧组类型
var shootStatus; //场景状态
$(function(){
	$("#title").focus();
	//页面初始时,加载集场号信息
	loadSeriesNoAndViewNo();
	//获取剧组类型
    getCrewType();
  //初始化剧本文本框
    initMainContent();
  //判断当前点击是否是左击事件
	isLeftClick(window.event || arguments.callee.caller.arguments[0]);//兼容火狐
	//页面禁用右键
    $(document).on('contextmenu', function (e) {
        return false;
    });
  //初始化场景信息框
    initViewContent();
  //初始化右侧场景信息窗口的格式及大小
    initAdvertContent();
  //初始化右键菜单文本框
    initRightContent();
  //初始化解析剧本弹窗
    initAnalysisScenarioWin();
    //初始化解析剧本失败窗口
    initAnalysisFailWin();
    
    //ctrl+s保存
    $(document).keydown(function(e) {
    	if (e.keyCode == 83 && e.ctrlKey) {
    		e.preventDefault();
    		//保存剧本内容和场景信息
    		saveScenarioAndView(true, undefined, false, false);
    	}
    });
    timer = setInterval(function(){
    	if (shootStatus != 2 && shootStatus != 3 ) {
    		//保存剧本内容和场景信息
    		saveScenarioAndView(false, undefined, true, true);
		}
    },30000);
    
    
    $("#analysisScenarioWin").on("close", function(){
    	$("#viewInfoTable").empty();
	});
});
//页面初始时,加载集场号信息
function loadSeriesNoAndViewNo(){
	$.ajax({
		url: '/viewManager/querySeriesNoAndViewNo',
		type: 'post',
		datatype: 'json',
		async: false,
		success: function(response){
			if(response.success){
				$(".icon_compare").attr("disabled", false);
				seriesNoDtoList = response.seriesNoDtoList; //获取集场号列表
	            //selecationText = "";    //页面上选中的文字
	            bmSeriesViewNo = response.bmSeriesViewNo; 
	            addSeriesNoAndViewNo(seriesNoDtoList); //加载集场号面板中的集场编号信息
	            initSeriesAndViewNo(bmSeriesViewNo); //书签中的集场信息
			}else{
				$("#title").val("");
				$("#scriptContent").val("");
				$(".icon_compare").attr("disabled", true);
				$("#f_scene_create").attr("src", '/viewManager/toViewDetailInfo?flag=scene');
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
	seriesNoTr.empty();
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
			var titleContent = "";
			var viewNoDto = viewNoDtoList[j];
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
				
				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNum(this)' sval= '"+ viewNoDto.viewId +"'>"+viewNoDto.viewNo);
				viewNoArr.push("</li>");
			}else {
 				viewNoArr.push("<li sid='"+seriesNoDtoList[i].seriesNo+"-"+viewNoDto.viewNo+"' class='nosave");
 				
 				if (!viewNoDto.isReaded) {
					titleContent += "&#10;剧本有更新";
					viewNoArr.push(" unread");
				}
 				
 				if (viewNoDto.hasNoGetRole) {
 					titleContent = "有可能存在未提取的角色或道具";
 					viewNoArr.push(" hasNoGetRole");
				}
 				
 				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNum(this)' sval= '"+ viewNoDto.viewId +"'>"+viewNoDto.viewNo);
 				viewNoArr.push("</li>");
			}
		}
		viewNoArr.push("	</ul>");
	}
	viewNoArr.push("	</td>");
	viewNoTr.empty();
	viewNoTr.append(viewNoArr.join(""));
	
}
//判端打开剧本编辑页面时,需要加载哪一场场景信息
function initSeriesAndViewNo(bmSeriesViewNo){
	var startSeriesNo = $("#startSeriesNoInput").val();
	//跳转到该页面时，加载第一场的内容
    if (startSeriesNo != null && startSeriesNo != "") {
    	clickseriesNo($("#seriesNoTr").find("li[id="+ startSeriesNo +"]"));
        clickViewNum($("#viewNoTr").find("ul[seriesno="+ startSeriesNo +"]").find("li").eq(0));
        
    } else if (bmSeriesViewNo != null && bmSeriesViewNo != "") {
        var seriesViewArr = bmSeriesViewNo.split("-");
        var seriesNo = seriesViewArr[0];
        clickseriesNo($("#seriesNoTr").find("li[id="+ seriesNo +"]"));
        clickViewNum($("#viewNoTr").find("li[sid="+ bmSeriesViewNo +"]"));
        
    } else {
        var firstSeriesLi = $("#seriesNoTr").find("li").eq(0);
        var firstViewLi = $("#viewNoTr").find("li").eq(0);
        
        clickseriesNo(firstSeriesLi);
        clickViewNum(firstViewLi);
    }
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
function clickViewNum(own){//isSave:初始化加载时模仿点击事件，不需要保存
//	if($("#scenarioChange").val() == "1" || _frame.find("input#isChanged").val() == "1"){
	if (shootStatus !=2 && shootStatus != 3) {
		saveScenarioAndView(true, undefined, true, false);
	}
//	}
	
	//用jquery获取场次数量
	//获取场次列表的长度
	var seriesViewNoList = seriesNoDtoList[0].viewNoDtoList.length;
    //查询场景信息 
    var seriesViewNo = $(own).attr("sid");
    var viewId = $(own).attr("sval");
    searchViewDetail(viewId);
    
    $("#seriesViewNoDiv").find("span").text(seriesViewNo);
    $("#movieViewNoDiv").find("span").text($(own).text() + "/" + seriesViewNoList);
    $("#seriesNoInput").val(seriesViewNo);
    //添加点击效果
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

//根据集场编号查询出剧本及场景的详细信息
function searchViewDetail(viewId) {
    if (viewId != null && viewId != undefined && viewId != '' && viewId != 'undefined') {
    $.ajax({
    type:'post',
    url:'/viewManager/queryViewContent?viewId=' + viewId,
    dataType:'json',
    async: false,
    success:function(respone){
        if (respone.success) {
        	$("#title").focus();
            $('#con_middle #viewContent #viewTitle #title').val(respone.title);
            
            $('#con_middle #viewContent #scriptContent').val(respone.viewContent);
            $('#con_middle #viewContent #scriptContent').scrollTop(0);
            //判断状态，如果是完成或者是删戏则不能修改剧本内容
            shootStatus = respone.shootStatus;
           /* if (shootStatus == 2 || shootStatus == 3) { //完成或者是删戏
				//将剧本内容框变为只读状态、场景详情页变为只读
            	$("#title").attr("readonly", true);
            	$("#scriptContent").attr("readonly", true);
            	//隐藏弹出框
            	$("#addColorBox").hide();
            	$("#addViewContent").hide();
            	$("#analysisTitleBtn").hide();
			}else {
				$("#title").attr("readonly", false);
            	$("#scriptContent").attr("readonly", false);
            	$("#addViewContent").show();
            	$("#analysisTitleBtn").show();
			}*/
        } else {
            showErrorMessage(respone.message);
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
    }, 3000);
    
        //保存书签信息
    /*$.ajax({
           url: "/scenarioManager/saveSceBookMark",
           type: "post",
           data: {seriesViewNo: seriesViewNo},
           success: function(respone) {
               if (!respone.success) {
            	   showErrorMessage(respone.message);
               }
           },
           failure: function() {
           }
        });*/
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

//查询上一场信息
function searchPreScene() {
	if (shootStatus != 2 && shootStatus != 3) {
		saveScenarioAndView(true, undefined, true, false);
	}
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
            preSeriesLi.click();
            var preSeriesNo = preSeriesLi.attr("id");
            var length = $("#viewNoTr").find("ul[seriesNo="+ preSeriesNo +"]").find("li").length;
            preScene = $("#viewNoTr").find("ul[seriesNo="+ preSeriesNo +"]").find("li").eq(length - 1);
        }
    }
    preScene.click();
    
}

//查询下一场信息
function searchNextScene() {
	if (shootStatus != 2 && shootStatus !=3 ) {
		saveScenarioAndView(true, undefined, true, false);
	}
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
            nextSeriesLi.click();
           var nextSeriesNo = nextSeriesLi.attr("id");
            nextScene = $("#viewNoTr").find("ul[seriesNo="+ nextSeriesNo +"]").find("li").eq(0);
        }
    }
    nextScene.click();
}




//当鼠标弹起时,是否弹出右键菜单选项
function isOpenRigthMenu(event){
	var _frame=$('#f_scene_create').contents();
	var viewId = _frame.find("input[name=viewId]").val();//场景ID 
	if(viewId == "" || viewId == undefined){//添加时不显示右键菜单
		return;
	}
	
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
        
        if (boxY + 180 > windowHeight) {
            boxY = windowHeight - 180;
        }
        
        contextMenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, boxY);
        return false;
    }
}

//初始化右键菜单文本框
function initRightContent(){
	$("#addColorBox").jqxMenu({ 
		theme:theme, 
		width: '120px', 
		height: '61px',
		autoOpenPopup: false, 
		mode: 'popup'
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
//点击集场面板时隐藏右侧弹出的选择窗口
function closeRgithSelectWin(){
	$(".right-pup-container").css("display", "none");
}


//初始化解析剧本弹窗
function initAnalysisFailWin(){
	$("#analysisFailWin").jqxWindow({
		theme: theme,
		width: 540,
		height: 370,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		cancelButton: "#cancelFailWinBtn"
	});
}

//初始化解析剧本弹窗
function initAnalysisScenarioWin(){
	$("#analysisScenarioWin").jqxWindow({
		theme: theme,
		width: 540,
		height: 370,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false, 
	});
}




//解析剧本标题
function analysisScenarioTitle(){
	var titleString = $("#title").val();
	if(titleString != ""){
			$.ajax({
				url: '/scenarioManager/analysisScenarioTitle',
				type: 'post',
				data: {"title": titleString},
				datatype: 'json',
				success: function(response){
					if(response.success){
						
			              var html =[];
			              if(response.e1 != "" && response.e1 != undefined && response.e1 != null){
			            	  html.push('<tr><td style="width: 30%;">集</td><td style="width: 70%;"><div id="e1_ji">'+ response.e1 +'</div></td></tr>');
			              }
			              if(response.e2 != "" && response.e2 != undefined && response.e2 != null){
			            	  html.push('<tr><td style="width: 30%;">场</td><td style="width: 70%;"><div id="e2_view">'+ response.e2 +'</div></td></tr>');
			              }
			              if(response.e3 != "" && response.e3 != undefined && response.e3 != null){
			            	  html.push('<tr><td style="width: 30%;">主场景/次场景/三级场景</td><td style="width: 70%;"><div id="e3_location">'+ response.e3 +'</div></td></tr>');
			              }
			              if(response.e4 != "" && response.e4 != undefined && response.e4 != null){
			            	  html.push('<tr><td style="width: 30%;">气氛</td><td style="width: 70%;"><div id="e4_atmo">'+ response.e4 +'</div></td></tr>');
			              }
			              if(response.e5 != "" && response.e5 != undefined && response.e5 != null){
			            	  html.push('<tr><td style="width: 30%;">内外景</td><td style="width: 70%;"><div id="e5_neiwai">'+ response.e5 +'</div></td></tr>');
			              }
			              if(response.e6 != "" && response.e6 != undefined && response.e6 != null){
			            	  html.push('<tr><td style="width: 30%;">季节</td><td style="width: 70%;"><div id="e6_season">'+ response.e6 +'</div></td></tr>');
			              }
			              if(response.e7 != "" && response.e7 != undefined && response.e7 != null){
			            	  html.push('<tr><td style="width: 30%;">人物(/隔开)</td><td style="width: 70%;"><div id="e7_person">'+ response.e7 +'</div></td></tr>');
			              }
			              $("#viewInfoTable").empty();
			              $("#viewInfoTable").append(html.join(""));
			              
			              $("#analysisScenarioWin").jqxWindow("open");
					}else{
						if (response.formatSampleList) {
							var formatDom = [];
							var sampleDom = [];
							$.each(response.formatSampleList, function(index, item) {
								var singleFormat = "";
								var singleSample = "";
								if (item.type == 1) {	//元素
									singleFormat = "<span class='scriptele'>"+ item.name +"</span>";
									singleSample = "<span>"+ item.sample +"</span>";
								} else {	//分隔符
									if (item.name == "/r/n") {
										singleFormat = "<span class='separator key-enter' title='换行'></span>";
										singleSample = "<br>";
									} else {
										singleFormat = "<span class='separator'>"+ item.name +"</span>";
										if (item.sample == "[*]") {
											singleSample = "<span>,</span>";
										} else {
											singleSample = "<span>"+ item.sample +"</span>";
										}
									}
								}
								formatDom.push(singleFormat);
								sampleDom.push(singleSample);
							});
							//标题格式
							$("#analysisFailWin .scenario-format>div").empty();
							$("#analysisFailWin .scenario-format>div").html($(formatDom.join("")));
							
							//标题示例
							$("#analysisFailWin .scenario-format .sample").empty();
							$("#analysisFailWin .scenario-format .sample").html($(sampleDom.join("")));
							
							$("#analysisFailWin").jqxWindow("open");
							$("#analysisFailWin .error-message").text(response.message);
						} else {
							showErrorMessage(response.message);
						}
					}
				}
			});
	}else{
		showInfoMessage("请输入剧本标题");
	}
}
//取消解析剧本
function cancelAnalysis(){
	$("#analysisScenarioWin").jqxWindow("close");
}

//替换场景格式
function replaceViewFormat(){
	var _frame= $('#f_scene_create').contents();
	_frame.find("input#isChanged").val("1");
	if($("#icon_save_btn").hasClass("hasNoSave")){
		
		if($("#e1_ji").text() != ""){
			_frame.find("input#noFinishTvbSeriesNo").val($("#e1_ji").text());
		}
		if($("#e2_view").text() != ""){
			
			_frame.find("input#noFinishTvbViewNo").val($("#e2_view").text());
		}
	}
	if($("#e4_atmo").text() != ""){
		_frame.find("input#noFinishViewAtmosphere").val($("#e4_atmo").text());
	}
	if($("#e5_neiwai").text() != ""){
		_frame.find("input#noFinishViewSite").val($("#e5_neiwai").text());
	}
	if($("#e6_season").text() != ""){
		_frame.find("input#noFinishViewSeason").val($("#e6_season").text());
	}
	if($("#e7_person").text() != ""){
		var persons = $("#e7_person").text().split("/");
		_frame.find('div.performer_first ul li.tagInput').siblings("li").remove();
		for(var i= 0; i< persons.length; i++){
			if(persons[i] != ""){
				_frame.find('div.performer_first ul li.tagInput').before('<li>'+persons[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
			}
		}
	}
	if($("#e3_location").text() != ""){
		var location = $("#e3_location").text().split("/");
		for(var i= 0; i< location.length; i++){
			if(i == 0){
				_frame.find("input#noFinishFirstLocation").val(location[i]);
			}else if(i == 1){
				_frame.find("input#noFinishSecondLocation").val(location[i]);
			}else {
				_frame.find("input#noFinishThirdLocation").val(location[i]);
			}
		}
	}
	
	$("#analysisScenarioWin").jqxWindow("close");
}


//添加剧本内容
function addScenario(){
	$("#icon_save_btn").addClass("hasNoSave");//给未保存状态一个标识
	$("#title").val('').focus();
	$("#scriptContent").val('');//清空剧本内容
	$("#scenarioViewDiv").hide();
	$("#seriesViewNoDiv").hide();//隐藏集场按钮
	$("#moviceViewDiv").hide();
	$("#movieViewNoDiv").hide();
	$("#btn_list").hide();//隐藏上下场按钮
	$("#f_scene_create").attr('src','/viewManager/toViewDetailInfo?flag=scene');
	document.getElementById('f_scene_create').onload= function(){
		var _frame=$('#f_scene_create').contents();
//		_frame.find("input[name=seriesNo]").val("");
//		_frame.find("input[name=viewNo]").val("");
		_frame.find("input#isChanged").val(0);
		
	};
	clearInterval(timer);
}


//保存剧本内容和场景信息
function saveScenarMethod(){
	//保存剧本内容和场景信息方法
	saveScenarioAndView(true, undefined, false, false);
}

//场景拆解需要保存的方法
function chaijieSaveView(flag, content, noReload, autoSaveFlag) {
	//flag:是否弹窗，content:场景拆解后的内容, noReload:是否不需要重新加载
	var _frame=$('#f_scene_create').contents();
	if($("#scenarioChange").val() != "1" && _frame.find("input#isChanged").val() != "1"){
		return;
	}
	var subData = {};
	var arr = [];
	//获取集场号
    var seriesNo = _frame.find('input.scene_set').val();
    var viewNo = _frame.find('input.scene_field').val();
    
    if(seriesNo == "" || viewNo == ""){
    	if (flag) {
    		showInfoMessage("集场号不能为空");
        	return;
    	}
    }
    seriesViewNo = seriesNo + "-" + viewNo;
    
    subData.seriesNo = seriesNo;
    subData.viewNo = viewNo;
	subData.site = _frame.find("input#noFinishViewSite").val();//内外景
	subData.specialRemind = _frame.find("#noFinishSpecialRemind").val();//特殊提醒
	
	//主要演员
    _frame.find('div.performer_first ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_first').val(arr);
    subData.majorActor = arr.join(",");
    arr.length=0;
    
    //特约演员        
    _frame.find('div.performer_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_special').val(arr);
    subData.guestActor = arr.join(",");
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
    subData.massesActor = arr.join(",");
    arr.length=0;
    var _fList = '';
    if (_frame.find('input.performer_first').val() != '' && _frame.find('input.performer_first').val() != undefined) {
    	// 主要演员
    	_fList = _frame.find('input.performer_first').val().replace(/\s*/g,'').split(',');
	}
    var _sList = '';
    if (_frame.find('input.performer_special').val() != '' && _frame.find('input.performer_special').val() != undefined) {
    	// 特约演员
    	_sList = _frame.find('input.performer_special').val().replace(/\s*/g,'').split(',');
		
	}
    
    // 对演员是否有重复进行校验
    var isRepeat = false;
	for(var i = 0; i < _fList.length; i++){
	    // 主要演员
	    var f = _fList[i];
	    // 主要 与 特约 是否有重复
	    if(flag){
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
	    
	}
    // 有重复 直接跳出
    if(isRepeat){
    	throw new Error("演员存在重复");
    }
    
    //普通道具
    arr.length=0;
    _frame.find('div.tool_main ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_main').val(arr);
    subData.commonProps = arr.join(",");
    
    //特殊道具
    arr.length=0;
    _frame.find('div.tool_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_special').val(arr);
    subData.specialProps = arr.join(",");
    
    //服装
    arr.length=0;
    _frame.find('div.clothes_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.clothes_info').val(arr);
    subData.clothes = arr.join(",");
    
    //化妆
    arr.length=0;
    _frame.find('div.makeup_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.makeups_info').val(arr);
    subData.makeups = arr.join(",");
  //主要内容,校验
    var mainContent = _frame.find("input.scene_content").val();
    if(flag){
    	if (mainContent != undefined && mainContent.length >= 250) {
            showErrorMessage("主要内容太长，不能超过250个字");
            throw new Error("主要内容太长，不能超过250个字");
        }
    }
    subData.mainContent = mainContent;
    subData.thirdLocation = _frame.find('input#noFinishThirdLocation').val();//三级场景
    subData.secondLocation = _frame.find('input#noFinishSecondLocation').val();//次场景
    subData.firstLocation = _frame.find('input#noFinishFirstLocation').val();//主场景
    subData.view = _frame.find('input#noFinishViewAtmosphere').val();//气氛
    subData.pageCount = _frame.find('input#noFinishTvbPageCount').val();//页数
    var shootLocation = _frame.find('input#noFinishViewShootLocation').val();//拍摄地点
    var shootRegion = _frame.find('#shootReginValue').text().replace(/\(/g,'').replace(/\)/g,'');//拍摄地域
    subData.shootLocation = shootLocation;
    subData.shootRegion = shootRegion;
    
    subData.title = $("#title").val();//剧本标题
    if(content == undefined){
    	subData.content = $("#scriptContent").val();//剧本内容
    }else{
    	subData.content = content;//剧本内容
    }
    
    subData.viewId = _frame.find("input[name=viewId]").val();//场景ID 
    
    if($("#icon_save_btn").hasClass("hasNoSave")){
    	$("#icon_save_btn").removeClass("hasNoSave");
    }
    //备注
    subData.remark = _frame.find("input[name=remark]").val();
    $.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegion},
		datatype: 'json',
		success: function(response){
			if(response.success){
				publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData);
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){
					publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData);
				});
			}
		}
	});
    
    
}



//保存剧本内容和场景信息方法
function saveScenarioAndView(flag, content, noReload, autoSaveFlag){//flag:是否弹窗，content:场景拆解后的内容, noReload:是否不需要重新加载
	var _frame=$('#f_scene_create').contents();
	if($("#scenarioChange").val() != "1" && _frame.find("input#isChanged").val() != "1"){
		return;
	}
	var shootRegion = _frame.find('#shootReginValue').text().replace(/\(/g,'').replace(/\)/g,'');//拍摄地域
	var shootLocation = _frame.find('input#noFinishViewShootLocation').val();//拍摄地点
	$.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegion},
		datatype: 'json',
		success: function(response){
			if(response.success){
				//保存剧本内容和场景信息方法的执行
				performSaveScenarioView(flag, content, noReload, autoSaveFlag);
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){
					//保存剧本内容和场景信息方法的执行
					performSaveScenarioView(flag, content, noReload, autoSaveFlag);
				});
			}
		}
	});
	
}

//保存剧本内容和场景信息方法的执行
function performSaveScenarioView(flag, content, noReload, autoSaveFlag){
	var _frame=$('#f_scene_create').contents();
	var subData = {};
	var arr = [];
	//获取集场号
    var seriesNo = _frame.find('input.scene_set').val();
    var viewNo = _frame.find('input.scene_field').val();
    
    if(seriesNo == "" || viewNo == ""){
    	if (flag) {
    		showInfoMessage("集场号不能为空");
        	return;
    	}
    }
    seriesViewNo = seriesNo + "-" + viewNo;
    
    subData.seriesNo = seriesNo;
    subData.viewNo = viewNo;
	subData.site = _frame.find("input#noFinishViewSite").val();//内外景
	subData.specialRemind = _frame.find("#noFinishSpecialRemind").val();//特殊提醒
	
	//主要演员
    _frame.find('div.performer_first ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_first').val(arr);
    subData.majorActor = arr.join(",");
    arr.length=0;
    
    //特约演员        
    _frame.find('div.performer_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.performer_special').val(arr);
    subData.guestActor = arr.join(",");
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
    subData.massesActor = arr.join(",");
    arr.length=0;
    var _fList = '';
    if (_frame.find('input.performer_first').val() != '' && _frame.find('input.performer_first').val() != undefined) {
    	// 主要演员
    	_fList = _frame.find('input.performer_first').val().replace(/\s*/g,'').split(',');
	}
    var _sList = '';
    if (_frame.find('input.performer_special').val() != '' && _frame.find('input.performer_special').val() != undefined) {
    	// 特约演员
    	_sList = _frame.find('input.performer_special').val().replace(/\s*/g,'').split(',');
		
	}
    
    // 对演员是否有重复进行校验
    var isRepeat = false;
	for(var i = 0; i < _fList.length; i++){
	    // 主要演员
	    var f = _fList[i];
	    // 主要 与 特约 是否有重复
	    if(flag){
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
	    
	}
    // 有重复 直接跳出
    if(isRepeat){
    	throw new Error("演员存在重复");
    }
    
    //普通道具
    arr.length=0;
    _frame.find('div.tool_main ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_main').val(arr);
    subData.commonProps = arr.join(",");
    
    //特殊道具
    arr.length=0;
    _frame.find('div.tool_special ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.tool_special').val(arr);
    subData.specialProps = arr.join(",");
    
    //服装
    arr.length=0;
    _frame.find('div.clothes_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.clothes_info').val(arr);
    subData.clothes = arr.join(",");
    
    //化妆
    arr.length=0;
    _frame.find('div.makeup_info ul .tagInput').siblings('li').each(function(){
        arr.push($(this).text());
    });
    _frame.find('input.makeups_info').val(arr);
    subData.makeups = arr.join(",");
  //主要内容,校验
    var mainContent = _frame.find("input.scene_content").val();
    if(flag){
    	if (mainContent != undefined && mainContent.length >= 250) {
            showErrorMessage("主要内容太长，不能超过250个字");
            throw new Error("主要内容太长，不能超过250个字");
        }
    }
    subData.mainContent = mainContent;
    subData.thirdLocation = _frame.find('input#noFinishThirdLocation').val();//三级场景
    subData.secondLocation = _frame.find('input#noFinishSecondLocation').val();//次场景
    subData.firstLocation = _frame.find('input#noFinishFirstLocation').val();//主场景
    subData.view = _frame.find('input#noFinishViewAtmosphere').val();//气氛
    subData.pageCount = _frame.find('input#noFinishTvbPageCount').val();//页数
    subData.shootLocation = _frame.find('input#noFinishViewShootLocation').val();//拍摄地点
    subData.shootRegion = _frame.find('#shootReginValue').text().replace(/\(/g,'').replace(/\)/g,'');
    subData.title = $("#title").val();//剧本标题
    if(content == undefined){
    	subData.content = $("#scriptContent").val();//剧本内容
    }else{
    	subData.content = content;//剧本内容
    }
    
    subData.viewId = _frame.find("input[name=viewId]").val();//场景ID 
    
    if($("#icon_save_btn").hasClass("hasNoSave")){
    	$("#icon_save_btn").removeClass("hasNoSave");
    }
    //备注
    subData.remark = _frame.find("input[name=remark]").val();
    
    if (shootStatus == 2 || shootStatus == 5) {
    	parent.popupPromptBox("提示", "当前场景已经完成，是否要继续保存？", function() {
    		publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData);
		});
	}else if (shootStatus == 3) {
		parent.popupPromptBox("提示", "当前场景已删戏，是否要继续保存？", function() {
    		publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData);
		});
	}else {
		publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData);
	}
}

//保存场景公用方法
function publicSaveViewDetail(flag, content, noReload, autoSaveFlag, subData) {
	var _frame=$('#f_scene_create').contents();
	$.ajax({
    	url: '/viewManager/saveViewInfo',
    	type: 'post',
    	data: subData,
    	datatype: 'json',
    	async: false,
    	success: function(response){
    		if(response.success){
    			$("#scenarioChange").val("0");
    			_frame.find("input#isChanged").val("0");//恢复默认值（监控值是否有改变）
    			if(flag){
//    				showSuccessMessage("保存成功");
    				$("#saveTips").html("<label class='true-flag'></label>保存成功");
    				setTimeout(function(){
    					$("#saveTips").html("");
    				}, 1000);
    			}
    			//自动保存
    			if(autoSaveFlag){
    				$("#saveTips").html("<label class='true-flag'></label>自动保存成功");
    				setTimeout(function(){
    					$("#saveTips").html("");
    				}, 5000);
    			}
    			
    			if(!noReload){//上下场切换只保存不需要重定位
    				setTimeout(function(){
    					window.location.href="viewManager/toScenarioManagePage?pageType=2";
    				}, 1000);
				}
    			
    		}else{
    			if(flag){
    				showErrorMessage(response.message);
    				throw new Error(response.message);
    			}
    			
    		}
    	}
    });
}

//场景拆解
function sceneDisassemb(source){
	if(selecationText.length > 0){
		var content = $("#scriptContent").val();//剧本内容
		
		var _frame=$('#f_scene_create').contents();
		//获取集场号
	    var seriesNo = _frame.find('input.scene_set').val();
	    var viewNo = _frame.find('input.scene_field').val();
	    
	    if (shootStatus == 2 || shootStatus == 5) {
	    	parent.popupPromptBox("提示", "当前场景已经完成，是否要继续拆解场景？", function() {
	    		publicSceneDisassemb(seriesNo,viewNo,selecationText);
	    		//保存剧本内容
	    	    if (source == '2') { //剪切拆解时需要将选中的剧本内容置为空
	    			$("#scenarioChange").val("1");
	    			var newContent = content.replace(selecationText, "");
	    			chaijieSaveView(false,newContent, true, false);
	    		}
			});
		}else if (shootStatus == 3) {
			parent.popupPromptBox("提示", "当前场景已删戏，是否要继续拆解场景？", function() {
	    		publicSceneDisassemb(seriesNo,viewNo,selecationText);
	    		//保存剧本内容
	    	    if (source == '2') { //剪切拆解时需要将选中的剧本内容置为空
	    			$("#scenarioChange").val("1");
	    			var newContent = content.replace(selecationText, "");
	    			chaijieSaveView(false,newContent, true, false);
	    		}
			});
		}else {
			publicSceneDisassemb(seriesNo,viewNo,selecationText);
			//保存剧本内容
		    if (source == '2') { //剪切拆解时需要将选中的剧本内容置为空
				$("#scenarioChange").val("1");
				var newContent = content.replace(selecationText, "");
				chaijieSaveView(false,newContent, true, false);
			}
		}
	}
}

//场景拆解的公用方法
function publicSceneDisassemb(seriesNo,viewNo,selecationText) {

    $.ajax({
    	url: "/viewManager/divideViewInfo",
    	type: "post",
    	data: {seriesNo: seriesNo, viewNo: viewNo, content: selecationText},
    	dataType: "json",
    	success: function(response) {
    		if (response.success) {
    			//加载集场号
    			loadSeriesNoAndViewNo();
    		}
    	}
    });
}

//跳转到剧本分析页面
function scenarioAnalysis(){
	var _frame = $('#f_scene_create').contents();
	if($("#scenarioChange").val() == "1" || _frame.find("input#isChanged").val() == "1"){
		showInfoMessage("请保存当前剧本内容后再操作");
	}else{
		saveScenarioAndView(true, undefined, true, false);
		window.location.href="/viewManager/toScenarioManagePage?pageType=1";
	}
	
	
}
//跳转到剧本编辑页面
function scenarioEdit(){
	window.location.href="viewManager/toScenarioManagePage?pageType=2";
}
//跳转到剧本对比页面
function scenarioCompare(){
	var _frame = $('#f_scene_create').contents();
	if($(".icon_compare").attr("disabled") == "disabled"){
		return;
	}
	if($("#scenarioChange").val() == "1" || _frame.find("input#isChanged").val() == "1"){
		showInfoMessage("请保存当前剧本内容后再操作");
	}else{
//		saveScenarioAndView(true, undefined, true, false);
		window.location.href="viewManager/toScenarioManagePage?pageType=3";
	}
	
}

//一旦进行按键事件默认为有改动
function changeScenarioValue(){
	$("#scenarioChange").val("1");
}