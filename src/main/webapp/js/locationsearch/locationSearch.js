$(document).ready(function(){
	//加载列表
	loadLocationSearchTable();
	//只读权限，去掉新增按钮
	if(isSceneViewReadonly) {
		$("#addScenceBtn").remove();
	}
});

var subData={};

var noneConfigActor;//未配置列表主要演员

//显示loading
function showLoading(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
     _LoadingHtml = $("#loadingTable");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
}


//加载主列表
function loadLocationSearchTable(){
	//显示loading
	showLoading();
	
	$.ajax({
		url: '/sceneViewInfoController/querySceneViewInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			
			if(response.success){
				
				var data={};
				data.titleMap = response.titleMap;
				data.result = response.result;
				
				var grid = $("#locationSearchTable");
			    
				loadScenceList(grid, data);
				_LoadingHtml.hide();
			    $(".opacityAll").hide();
			    
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
    
}

//添加场景
function addScenceBtn(){
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	$("#scenceContentIframe").attr("src", "/sceneViewInfoController/toSceneViewDetailPage?sceneViewId=");
	$(".delete-btn").hide();
}



//关闭添加或修改场景窗口
function closeRightPopupWin(){
	/*$("#rightPopUpWin").animate({"right":"-2000px"}, 500);*/
	
	clearInterval(timer);
	var rightPopWidth = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-rightPopWidth},300);
	
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 300);
}

//修改场景
function modifyScence(id){
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	$("#scenceContentIframe").attr("src", "/sceneViewInfoController/toSceneViewDetailPage?sceneViewId="+id);
	$(".delete-btn").show();
}

//初始化配置场景弹窗
function initConfigScenceWin(){
	var screenWidth = window.screen.width;
	var setScenceWinHeight;
	if(screenWidth >= 1366 && screenWidth <= 1399) {
		setScenceWinHeight = 650;
	}else{
		setScenceWinHeight = 750;
	}
	$("#setScenceWin").jqxWindow({
		theme: theme,
		width: 1300,
		height: setScenceWinHeight,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			//获取未配置场景列表
			getNoneConfiguredScenceList();
			//获取已配置场景列表
			initAlreadyScence();
		}
	});
}
var alreadySceneViewId ;
var sceneViewId = {};


//重新加载主场景列表
function reloadMainScenceGrid(){
	//显示loading
	showLoading();
	$.ajax({
		url: '/sceneViewInfoController/querySceneViewInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			
			if(response.success){
				var data={};
				data.titleMap = response.titleMap;
				data.result = response.result;
				var grid = $("#locationSearchTable");
				reloadMainScenceView(grid, data);
				_LoadingHtml.hide();
			    $(".opacityAll").hide();
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}