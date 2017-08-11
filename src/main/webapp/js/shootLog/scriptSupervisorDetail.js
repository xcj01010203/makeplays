$(document).ready(function () {
        	
	var clientWidth=window.document.body.scrollWidth;
	var clientHeight=window.document.body.scrollHeight;
	var wid = (clientWidth-228)+"px";
	var hei = (clientHeight-120)+"px";
	
	//加载页面数据
	loadListData();
	
	$(".setting-right").css({width:wid,height:hei});
	$(window).resize(function(){
 	   var nowwidth=window.document.body.offsetWidth;
 	   var nowHeight=window.document.body.scrollHeight;
 	   hei = (nowHeight-120)+"px";
 	   wid = (nowwidth-228)+"px";
 	   $(".setting-right").css({width:wid,height:hei});
 	});
	
	$(".setting-left li").on("click",function(){
    	var _this = $(this);
    	if(!_this.hasClass("setting-li-current")){
    		_this.addClass("setting-li-current").siblings().removeClass("setting-li-current");
    		var index = _this.index();
    		$(".setting-right").children("div:eq("+index+")").show().siblings("div").hide();
    		$(".current-menu-name").text(_this.text());
    	}
    });
	
	$(".del-camera").on("click",function(e){
		var _this = $(this);
		$("#tipprop").css({left:_this.offset().left+20,top:_this.offset().top-105}).show();
		e.stopPropagation();
	});
	
	$(document).click(function(){
		$("#tipprop").hide();
    });
	
	$("#tipprop").on("click",function(e){
		e.stopPropagation();
	});
    
	var _right = $(".setting-right")[0];
	if(_right.scrollWidth>_right.clientWidth||_right.offsetWidth>_right.clientWidth){ 
        $("#clipInfo").css("display","inline-block");
    } else{
    	//$("#clipInfo").css("display","block");
    }
});


//返回日志列表
function returnShootLogList(){
	window.location.href="/shootLogManager/toShootLogList";
}
 
//跳转到页面后需要加载的数据
function loadListData(){
	//取出通告单的id
	var noticeId = $("#noticeId").val();
	$.ajax({
		url: '/shootLogManager/queryClipInfoList',
		type: 'post',
		async: true,
		data:{noticeId:noticeId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				//场记单列表
				var clipInfoList = response.clipInfo;
				//机位名称列表
				var nameList = response.nameList;
				//主要演员出勤信息列表
				var majorRoleAttenInfo = response.majorRoleAttenInfo;
				//群众特约演员出勤列表
				var notMajRoleAttenInfo =response.notMajRoleAttenInfo;
				//特殊道具列表
				var specialPropList = response.specialPropInfo;
				//重要备注列表
				var importCommentList = response.importCommentInfo;
				//现场信息
				var liveList = response.liveInfo;
				loadClipData(clipInfoList, nameList);
				loadSceneData(liveList);
				loadConvertData(liveList);
				loadMajorRoleAttenceData(majorRoleAttenInfo);
				loadMassRoleAttenData(notMajRoleAttenInfo);
				loadSpecialPropData(specialPropList);
				loadImportCommentData(importCommentList);
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//拼接重要备注信息
function loadImportCommentData(importCommentList){
	var _importCommentTr = $("#importCommentTbody");
	var importCommentArr = [];
	if (importCommentList == null || importCommentList.length == 0) {
		importCommentArr.push("	<tr><td colspan='2'>暂无数据</td></tr>");
	}else {
		for(var i=0; i<importCommentList.length; i++){
			var item = importCommentList[i];
			importCommentArr.push("	<tr>");
//			if (item.serverTime == null || item.serverTime == '') {
//				importCommentArr.push("	<td> </td>");
//			}else {
//				var fromatTime = new Date(item.serverTime).Format('yyyy-MM-dd hh:mm:ss');
//				importCommentArr.push("	<td>" + fromatTime + "</td>");
//			}
			
			if (item.content == null || item.content == '') {
				importCommentArr.push("	<td> </td>");
			}else {
				importCommentArr.push("	<td>" + item.content + "</td>");
			}
			importCommentArr.push("	</tr>");
		}
	}
	_importCommentTr.append(importCommentArr.join(""));
}

//拼接特殊道具数据
function loadSpecialPropData(specialPropList){
	var _specialPropTr = $("#specialPropTbody");
	var specialPropArr = [];
	if (specialPropList == null || specialPropList.length == 0) {
		specialPropArr.push("	<tr><td colspan='3'>暂无数据</td></tr>");
	}else {
		for(var i =0; i<specialPropList.length; i++){
			var item = specialPropList[i];
			specialPropArr.push("	<tr>");
			if (item.name == null || item.name=='') {
				specialPropArr.push("<td> </td>");
			}else {
				specialPropArr.push("<td>"+ item.name +"</td>");
			}
			
			if (item.num == null || item.num == '') {
				specialPropArr.push("<td> </td>");
			}else {
				specialPropArr.push("<td>"+ item.num +"</td>");
			}
			
			if (item.comment == null || item.comment=='') {
				specialPropArr.push("<td> </td>");
			}else {
				specialPropArr.push("<td>"+ item.comment +"</td>");
			}
			specialPropArr.push("	</tr>");
		}
	}
	_specialPropTr.append(specialPropArr.join(""));
}

//拼接群众演员/特约演员出勤信息
function loadMassRoleAttenData(notMajRoleAttenInfo){
	var _massRoleAttenTr = $("#massRoleAttenTbody");
	var massRoleAttenArr = [];
	if (notMajRoleAttenInfo == null || notMajRoleAttenInfo.length<1) {
		massRoleAttenArr.push(" <tr><td colspan='5'>暂无数据</td></tr>");
	}else {
		for(var i=0; i<notMajRoleAttenInfo.length; i++){
			var item = notMajRoleAttenInfo[i];
			massRoleAttenArr.push("	<tr>");
			massRoleAttenArr.push("	<td>");
			if (item.roleType == 1) {
				massRoleAttenArr.push("主要演员");
			}else if (item.roleType == 2) {
				massRoleAttenArr.push("特约演员");
			}else if (item.roleType == 3) {
				massRoleAttenArr.push("群众演员");
			}
			massRoleAttenArr.push("	</td>");
			massRoleAttenArr.push("	<td>"+ item.viewRoleName +"</td>");
			
			if (item.roleNum == null || item.roleNum=='') {
				massRoleAttenArr.push("	<td> </td>");
			}else {
				massRoleAttenArr.push("	<td>"+ item.roleNum +"</td>");
			}
			
			if (item.rarriveTime == null || item.rarriveTime == '') {
				massRoleAttenArr.push("	<td> </td>");
			}else {
				massRoleAttenArr.push("	<td>"+ item.rarriveTime +"</td>");
			}
			
			if (item.rpackupTime == null || item.rpackupTime == '') {
				massRoleAttenArr.push("	<td> </td>");
			}else {
				massRoleAttenArr.push("	<td>"+ item.rpackupTime +"</td>");
			}
			
			massRoleAttenArr.push("	</tr>");
		}
	}
	_massRoleAttenTr.append(massRoleAttenArr.join(""));
}

//拼接主演员出勤信息
function loadMajorRoleAttenceData(majorRoleAttenInfo){
	var _majorRoleAttenInfoTr = $("#majorRoleAttenInfoTbody");
	var majorAttenceArr = [];
	if (majorRoleAttenInfo == null || majorRoleAttenInfo.length<1) {
		majorAttenceArr.push(" <tr><td colspan='3'>暂无数据</td></tr>");
	}else {
		for(var i=0; i<majorRoleAttenInfo.length; i++){
			var item = majorRoleAttenInfo[i];
			majorAttenceArr.push("	<tr>");
			if (item.actorName == null || item.actorName== '') {
				majorAttenceArr.push(" <td >");
			}else {
				majorAttenceArr.push(" <td >"+item.actorName);
			}
			majorAttenceArr.push("("+item.viewRoleName+")"+"</td>");
			
			if (item.rarriveTime == null || item.rarriveTime=='') {
				majorAttenceArr.push(" <td > ");
			}else {
				majorAttenceArr.push(" <td >"+item.rarriveTime);
			}
			
			if (item.isLateArrive) {
				majorAttenceArr.push("（<span style='color: #FF7803;'>迟到</span>）");
			}
			majorAttenceArr.push(" </td >");
			
			if (item.rpackupTime == null || item.rpackupTime == '') {
				majorAttenceArr.push(" <td >");
			}else {
				majorAttenceArr.push(" <td >"+item.rpackupTime);
			}
			
			if (item.isLatePackup) {
				majorAttenceArr.push("（<span style='color: #768519;'>迟放</span>）");
			}
			majorAttenceArr.push(" </td>");
			majorAttenceArr.push(" </tr>");
		}
	}
	_majorRoleAttenInfoTr.append(majorAttenceArr.join(""));
}

//拼接转场信息
function loadConvertData(liveList){
	//拼接转场信息
	var _convertInfoTr = $("#convertInfoTbody");
	var convertArr = [];
	if (liveList == null ||  liveList.convertInfoList == null || liveList.convertInfoList.length == 0) {
		convertArr.push("	<tr><td colspan='4'>暂无数据</td></tr>");
	}else {
		var convertList = liveList.convertInfoList;
		for(var i=0; i<convertList.length; i++){
			var item = convertList[i];
			convertArr.push("	<tr>");
			convertArr.push("	<td>"+i+"</td>");
			if (item.convertTime == null || item.convertTime == '') {
				convertArr.push("	<td> </td>");
			}else {
				convertArr.push("	<td>"+item.convertTime+"</td>");
			}
			
			if (item.carriveTime == null || item.carriveTime == '') {
				convertArr.push("	<td> </td>");
			}else {
				convertArr.push("	<td>"+item.carriveTime+"</td>");
			}
			
			if (item.cbootTime == null || item.cbootTime == '') {
				convertArr.push("	<td> </td>");
			}else {
				convertArr.push("	<td>"+item.cbootTime+"</td>");
			}
			convertArr.push("	</tr>");
		}
	}
	_convertInfoTr.append(convertArr.join(""));
}

//拼接场记单数据
function loadClipData(clipInfoList, nameList){
	var _clipInfoTr = $("#clipInfoTr");
	var _clipNameTr = $("#clipNameTr");
	//添加数据表头和机位名称字段信息
	var nameArr = [];
	var appendArr = [];
	for(var i = 0; i < nameList.length; i++){
		var name = nameList[i];
		nameArr.push("	<td colspan='5'>"+name+"</td>");
		appendArr.push("	<td style='min-width:60px;'>景别</td>");
		appendArr.push("	<td style='min-width:100px;'>内容</td>");
		appendArr.push("	<td style='min-width:60px;'>tc</td>");
		appendArr.push("	<td style='min-width:60px;'>成绩</td>");
		appendArr.push("	<td style='min-width:100px;'>备注</td>");
	}
	_clipInfoTr.append(nameArr.join(""));
	_clipNameTr.append(appendArr.join(""));
	
	//拼接场记单的主体列表数据
	var _clipInfoTbody = $("#clipInfoTbody");
	var clipInfoArr = [];
	if (clipInfoList == null ||clipInfoList.length < 1) {
		_clipInfoTbody.append("<tr><td colspan='11'>暂无数据</td></tr>");
	}else {
		for(key in clipInfoList){
			var status1 = 0;
			var viewitem = clipInfoList[key];
			for(key1 in viewitem.map){
				var status2 = 0;
				var lensNoitem = viewitem.map[key1];
				for(key2 in lensNoitem.map){
					var auditionNo = lensNoitem.map[key2];
					clipInfoArr.push("	<tr>");
					if (status1 ==0 && status2 == 0) {
						clipInfoArr.push("	<td rowspan='"+viewitem.num+"'>"+key+"</td>");
						if (auditionNo.list.atmosphereName == null || auditionNo.list.atmosphereName == '') {
							clipInfoArr.push("	<td rowspan= '"+ viewitem.num +"'> </td>");
						}else {
							clipInfoArr.push("	<td rowspan= '"+ viewitem.num +"'>"+ auditionNo.list.atmosphereName +"</td>");
						}
						clipInfoArr.push("	<td rowspan= '"+ viewitem.num +"'>"+auditionNo.list.statusDetail +"</td>");
						clipInfoArr.push("	<td rowspan= '"+ lensNoitem.num +"'>"+auditionNo.list.lensNo +"</td>");
						clipInfoArr.push("	<td rowspan= '1'>"+auditionNo.list.auditionNo +"</td>");
						for(key3 in auditionNo.cameraMap){
							var cameraMap = auditionNo.cameraMap[key3];
							if (cameraMap.sceneType == null || cameraMap.sceneType == 'null') {
								clipInfoArr.push("<td></td>");
							}else {
								clipInfoArr.push("<td>"+ cameraMap.sceneType +"</td>");
							}
							clipInfoArr.push("<td>"+ cameraMap.content +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.tcValue +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.grade +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.comment +"</td>");
						}
					}else {
						if (status1 !=0 && status2 == 0) {
							clipInfoArr.push("	<td rowspan= '"+ lensNoitem.num +"'>"+auditionNo.list.lensNo +"</td>");
						}
						clipInfoArr.push("	<td rowspan= '1'>"+auditionNo.list.auditionNo +"</td>");
						for(key3 in auditionNo.cameraMap){
							var cameraMap = auditionNo.cameraMap[key3];
							if (cameraMap.sceneType == null || cameraMap.sceneType == 'null') {
								clipInfoArr.push("<td class='camera-info'></td>");
							}else {
								clipInfoArr.push("<td class='camera-info'>"+ cameraMap.sceneType +"</td>");
							}
							clipInfoArr.push("<td>"+ cameraMap.content +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.tcValue +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.grade +"</td>");
							clipInfoArr.push("<td>"+ cameraMap.comment +"</td>");
						}
					}
					clipInfoArr.push("	</tr>");
					status2++;
				}
				status1++;
			}
		}
		_clipInfoTbody.append(clipInfoArr.join(""));
	}
	
}

//拼接现场信息
function loadSceneData(liveList){
	//拍摄带号
	if (liveList.tapNo == null || liveList.tapNo =='') {
		$("#tapNoDD").text('');
	}else {
		$("#tapNoDD").text(liveList.tapNo);
	}
	//拍摄地点
	if (liveList.shootLocation == null || liveList.shootLocation=='') {
		$("#shootLocationDD").text('');
	}else {
		$("#shootLocationDD").text(liveList.shootLocation);
		$("#shootLocationDD").prop("title", liveList.shootLocation);
	}
	//拍摄场景
	if (liveList.shootScene == null || liveList.shootScene=='') {
		$("#shootSeneDD").text('');
	}else {
		$("#shootSeneDD").text(liveList.shootScene);
		$("#shootSeneDD").prop("title", liveList.shootScene);
	}
	//出发时间
	if (liveList.startTime == null || liveList.startTime== '') {
		$("#startTimeDD").text('');
	}else {
		$("#startTimeDD").text(liveList.startTime);
	}
	//到场时间
	if (liveList.arriveTime == null || liveList.arriveTime == '') {
		$("#arriveTimeDD").text('');
	}else {
		$("#arriveTimeDD").text(liveList.arriveTime);
	}
	//开机时间
	if (liveList.bootTime == null || liveList.bootTime == '') {
		$("#bootTimeDD").text('');
	}else {
		$("#bootTimeDD").text(liveList.bootTime);
	}
	//收工时间
	if (liveList.packupTime == null || liveList.packupTime == '') {
		$("#packupTimeDD").text('');
	}else {
		$("#packupTimeDD").text(liveList.packupTime);
	}
	
}

