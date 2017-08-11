$(function(){
	//默认加载剧本进度页面
	scriptProgress();
});

//加载剧本进度页面
function scriptProgress(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
//	$("#mainContent").empty();
//	$("#mainContent").load("/prepareController/toPreparePageScript");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageScript");
	return false;
}
//选角进度
function selectRole(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
//	$("#mainContent").empty();
//	$("#mainContent").load("/prepareController/toPreparePageRole");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageRole");
	return false;
}
//剧组人员
function crewPeople(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageCrewPeople");
	return false;
}
//加载美术视觉
function artVertion(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
//	$("#mainContent").empty();
//	$("#mainContent").load("/prepareController/toPreparePageArteffect");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageArteffect");
	return false;
}
//堪景情况
function sceneView(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
	$("#scenceDiv").empty();
	$("#scenceDiv").show();
	$("#mainIframe").hide();
//	$("#mainContent").empty();
	$("#scenceDiv").load("/prepareController/toPreparePageSceneView");
	return false;
//	$("#mainIframe").attr("src", "/prepareController/toPreparePageSceneView");
}
//宣传进度
function extension(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
//	$("#mainContent").empty();
//	$("#mainContent").load("/prepareController/toPreparePageExtension");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageExtension");
	return false;
}
//办公筹备
function officePrepare(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageWork");
	return false;
}
//商业运营
function commerOperation(own){
	var $this = $(own);
	$this.parent("li").siblings("li").removeClass("click");
	$this.parent("li").addClass("click");
	$("#scenceDiv").empty();
	$("#scenceDiv").hide();
	$("#mainIframe").show();
	$("#mainIframe").attr("src", "/prepareController/toPreparePageOperate");
	return false;
}