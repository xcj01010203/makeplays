$(function(){
	//初始化对比工具
	initCompareEvent();
	//页面初始时,加载集场号信息
	loadSeriesNoAndViewNo();
	//获取剧组类型
    getCrewType();
    
});


//页面初始时,加载集场号信息
function loadSeriesNoAndViewNo(){
	$.ajax({
		url: '/viewManager/querySeriesNoAndViewNo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				seriesNoDtoList = response.seriesNoDtoList; //获取集场号列表
	            //selecationText = "";    //页面上选中的文字
	            bmSeriesViewNo = response.bmSeriesViewNo; 
	            addSeriesNoAndViewNo(seriesNoDtoList); //加载集场号面板中的集场编号信息
	            initSeriesAndViewNo(bmSeriesViewNo); //书签中的集场信息
	            
	          
			}else{
				/*var noViewMessage = response.noViewMessage;
				if (noViewMessage != null && noViewMessage != '') {
					loadViewMessage(noViewMessage); //如果没有上传剧本,初始化提示信息
				}else {
					showErrorMessage(response.message);
				}*/
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
			var titleContent = "";
			var viewNoDto = viewNoDtoList[j];
			if (viewNoDto.isManualSave) {
				titleContent = "场景信息已手动保存";
				viewNoArr.push("<li id='"+ viewNoDto.viewId +"' sid='"+seriesNoDtoList[i].seriesNo+"-"+viewNoDto.viewNo+"' class='saved");
				
				if (!viewNoDto.isReaded) {
					titleContent += "&#10;剧本有更新";
					viewNoArr.push(" unread");
				}
				
				if (viewNoDto.hasNoGetRole) {
					titleContent += "&#10;有可能存在未提取的角色或道具";
					viewNoArr.push(" hasNoGetRole");
				}
				
				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNo(this)'>"+viewNoDto.viewNo);
				viewNoArr.push("</li>");
			}else {
 				viewNoArr.push("<li id='"+ viewNoDto.viewId + "' sid='"+seriesNoDtoList[i].seriesNo+"-"+viewNoDto.viewNo+"' class='nosave");
 				
 				if (!viewNoDto.isReaded) {
					titleContent += "&#10;剧本有更新";
					viewNoArr.push(" unread");
				}
 				
 				if (viewNoDto.hasNoGetRole) {
 					titleContent = "有可能存在未提取的角色或道具";
 					viewNoArr.push(" hasNoGetRole");
				}
 				
 				viewNoArr.push("' title='"+titleContent+"' onclick='clickViewNo(this)'>"+viewNoDto.viewNo);
 				viewNoArr.push("</li>");
			}
		}
		viewNoArr.push("	</ul>");
	}
	viewNoArr.push("	</td>");
	viewNoTr.append(viewNoArr.join(""));
	
}
//判端打开剧本对比页面时,需要加载哪一场场景信息
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
	//用jquery获取场次数量
	//获取场次列表的长度
	var seriesViewNoList = seriesNoDtoList[0].viewNoDtoList.length;
    //查询场景信息 
	//查询场景信息 
    var seriesViewNo = $(own).attr("sid");
    var viewId = $(own).attr("id");
    searchSceneDetail(viewId);
    
    $("#seriesViewNoDiv").find("span").text(seriesViewNo);
    $("#movieViewNoDiv").find("span").text($(own).text() + "/" + seriesViewNoList);
    
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
//点击场次超链接查询场次信息
function searchSceneDetail(viewId) {
    
    searchViewDetail(viewId);
}
//根据集场编号查询出最新版本的剧本内容和上一版剧本内容
function searchViewDetail(viewId) {
    if (viewId != null && viewId != undefined && viewId != '' && viewId != 'undefined') {
	    $.ajax({
		    type:'post',
		    url:'/scenarioManager/queryViewContentCompareInfo',
		    data: {"viewId": viewId},
		    dataType:'json',
		    async: false,
		    success:function(respone){
		        if (respone.success) {
	//	        	debugger;
	//	        	var lfHeight, rtHeight;
	//	        	$(".scenario-left-title").each(function(){
	//	        		lfHeight =this.scrollHeight;
	//	    		});
	//	        	$(".scenario-right-title").each(function(){
	//	        		rtHeight =this.scrollHeight;
	//	    		});
	//	        	if((lfHeight - rtHeight) > 0){
	//	        		$(".textarea-height").css({"height": lfHeight});
	//	        		lfHeight += 55;
	//	        		$("#main_content").css({"height": "calc(100% - " + lfHeight + "px)"});
	//	        	}else{
	//	        		$(".textarea-height").css({"height": rtHeight});
	//	        		rtHeight += 55;
	//	        		$("#main_content").css({"height": "calc(100% - " + rtHeight + "px)"});
	//	        	}
		        	var leftContent = "";
		        	if((respone.preTitle != "") && (respone.preTitle != null) && (respone.preContent != "") && (respone.preContent != null)){
		        		leftContent = respone.preTitle + "\n" + respone.preContent;
		        	}else if((respone.preTitle != "") && (respone.preTitle != null)){
		        		leftContent = respone.preTitle;
		        	}else if((respone.preContent != "") && (respone.preContent != null)){
		        		leftContent = respone.preContent;
		        	}else {
		        		leftContent = "";
		        	}
		        	
		        	
		        	var rightContent = "";
		        	if((respone.currTitle != "") && (respone.currTitle != null) && (respone.currContent != "") && (respone.currContent != null)){
		        		rightContent = respone.currTitle + "\n" + respone.currContent;
		        	}else if((respone.currTitle != "") && (respone.currTitle != null)){
		        		rightContent = respone.currTitle;
		        	}else if((respone.currContent != "") && (respone.currContent != null)){
		        		rightContent = respone.currContent;
		        	}else {
		        		rightContent = "";
		        	}
		        	$("#compare").mergely('lhs', leftContent);
		        	$("#compare").mergely('rhs', rightContent);
		        	
		        	
		        } else {
		            showErrorMessage(respone.message);
		        }
		    }
	    });
	    
	    //查询场次的历史版本信息
	    $.ajax({
		    type:'post',
		    url:'/scenarioManager/queryVersionList',
		    data: {"viewId": viewId},
		    dataType:'json',
		    success:function(response){
		        if (response.success) {
		        	$("#versionSelect").empty();
		        	if (response.versionList.length > 0) {
		        		$.each(response.versionList, function(index, value) {
		        			$("#versionSelect").append("<option value='"+ value.version +"'>"+ value.createTime +"版</option>");
		        			$("#versionSelect").attr("viewId", viewId);
		        		});
		        	} else {
		        		$("#versionSelect").append("<option>上一次发布的版本</option>");
		        	}
		        }
		    }
	    });
    
    }
}

/**
 * 查询场景指定版本的剧本内容
 * @param viewId
 * @param version
 */
function queryViewContent(own) {
	var viewId = $(own).attr("viewId");
	var version = $(own).val();
	$.ajax({
		url: "/scenarioManager/queryVersionViewContent",
		data: {viewId: viewId, version: version},
		dataType: "json",
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return;
			}
			$("#compare").mergely('lhs', response.content);
		}
	});
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

//点击剧本主窗口收起集场面板
function closeDownPancle(){
	//点击下方面板时，上方的面板收起
    $jcbox = $(".ji_chang_box");
    if($jcbox.hasClass("ji_chang_box_down")){
        $(".select_box").slideUp("200"); 
        $jcbox.removeClass("ji_chang_box_down").addClass("ji_chang_box_up");
    }
}

//初始化对比控件
function initCompareEvent(){
//	var leftContent = '高度紧张的私立医院内科科长林医生（男，三十多岁，长了一副卑劣相）和浩泽对坐在办公室一角的沙发上。\n a 林医生：贺总，你放心吧，你老婆给令尊捐肝脏的事情绝对不会让别人知道。\n浩泽：（焦急地搓着手） a 不怕一万就怕万一……（还是觉得不妥）等一下，等一下，我要再考虑一下……\n林医生：（让浩泽安心）您不必担心。\n我们医院虽然是私人医院，但是我们VIP病房在保护隐私方面是出了名的。';
//	var rightContent = '高度紧张的私立医院内科科长林医生（男，三十多岁，长了一副卑劣相）和浩泽对坐在办公室一角的沙发上。\n林医生：贺总，你放心吧，你老婆给令尊捐肝脏的事情绝对不会让别人知道。\n浩泽：（焦急地搓着手）不怕一万就怕万一……（还是觉得不妥）等一下，等一下，我要再考虑一下……\n林医生：（让浩泽安心）您不必担心。\n我们医院虽然是私人医院，但是我们VIP病房在保护隐私方面是出了名的。';
	$('#compare').mergely({
		cmsettings: { readOnly: true,lineNumbers: true },//true,false是否可编辑
		fgcolor:{a:'#4ba3fa',c:'#cccccc',d:'#ff7f7f'},  //（连接线的样式）a:右边内容为最新（添加）d:删除）
		editor_width: 'calc(50% - 25px)',
		bgcolor: '#0ff',
		editor_height: '99%',
		lhs: function(setValue) {
            setValue('');
            
		},
		rhs: function(setValue) {
			setValue('');
		}
	});
}

//跳转到剧本分析页面
function scenarioAnalysis(){
	window.location.href="/viewManager/toScenarioManagePage?pageType=1";
}
//跳转到剧本编辑页面
function scenarioEdit(){
	window.location.href="viewManager/toScenarioManagePage?pageType=2";
}
//跳转到剧本对比页面
function scenarioCompare(){
	window.location.href="viewManager/toScenarioManagePage?pageType=3";
}