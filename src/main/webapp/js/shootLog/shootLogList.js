$(document).ready(function (){
	loadShootLogData(1, true);	
	//初始化上传窗口
	initUploadWin();
	//初始化上传插件
	initUploader();
	$("#uploadWindow").on("close", function(){
		if (uploader.getFiles('inited').length != 0){
			var files = uploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				uploader.removeFile(files[i].id, true);
			}
		}
		$("#uploadFileList").empty();
		$("#uploadFilevedio").empty();
	});
	
	//音频播放结束
	var video = document.getElementById("myVideo");
	video.onended = function(){
		$("a.play").removeClass("play").addClass("stop").attr("title", "播放");
	};
	
	//只读
	if(isShootLogReadonly) {
		$(".shootlog-basic-info").find("input[type='text']").attr('disabled',true);
		$(".add-transition-site").remove();
		$(".add-actor").remove();
		$(".btn-list").find("input[type='button']").remove();
		$(".add-special-prop").remove();
		$(".add-remark-btn").remove();
		$("#uploadFileBtn").remove();
	}
});

//初始化上传窗口
function initUploadWin(){
	$("#uploadWindow").jqxWindow({
		theme: theme,
		width: 700,
		height: 620,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		modalZIndex: 700,
		isModal: true,
		autoOpen: false
	});
}

//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/\D/g,'', ""));
}

//跳转到页面后加载数据
function loadShootLogData(pageNo, isLoad){
	if (isLoad) {
		$.ajax({
			url: '/clipManager/queryShootLogList',
			type: 'post',
			datatype: 'json',
			data:{pageNo: pageNo, pagesize: 10},
			success: function(response){
				if(response.success){
					//拍摄日志列表
					var shootList = response.shootList;
					//总页数
					var totalPageCount = response.totalPageCount;
					//没有数据时
					if (totalPageCount == 0) {
						initClipListData(shootList);
						hoverTobody();
					}
					
					//当当前页数小于总页数时
					if (pageNo < totalPageCount) {
						initClipListData(shootList);
						hoverTobody();
						
						loadShootLogData(pageNo+1, true);
					}else if (pageNo == totalPageCount) { //查询到最后一页
						initClipListData(shootList);
						hoverTobody();
						isLoad = false;
					}
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}
}


function hoverTobody(){
    $("#shootLogTable tbody tr").hover(function(){
  	   if($(this).find("td").length == 7){
  		   $(this).find("td").css("background-color","#e3e3e4").css("cursor","pointer");
  	   }else{
  		   $(this).find("td:gt(0)").css("background-color","#e3e3e4").css("cursor","pointer");
  	   }
     },function(){
       if(!($(this).hasClass("select"))){
    	   $(this).find("td").css("background-color","#f8f8f8");
       }
     });
	if($.trim($("#shootLogGrid tbody").text()) == ''){
	 	   $("#shootLogGrid tbody").html("<tr><td colspan='8'>暂无数据</td></tr>");
	}
}

//tr的选中事件
function selectTr(own){
	var trObj = document.getElementsByClassName("select");
	if(trObj.length != 0){
		$("tr.select").removeClass("select");
	}
	$(own).addClass("select");
	$(".back-ground-td").removeClass("back-ground-td").css("background-color","#f8f8f8");
	   if($(own).find("td").length == 7){
		   $(own).find("td").addClass("back-ground-td");
	   }else{
		   $(own).find("td:gt(0)").addClass("back-ground-td");
	   }
}


//拼接拍摄日志列表数据
function initClipListData(shootList){
	var _clipListTr = $("#clipListTbody");
	var clipTdArr = [];
	for(key in shootList){
		var clipList = shootList[key];
		for(var i = 0; i < clipList.length; i++){
			var map = clipList[i];
			clipTdArr.push("	<tr noticeid= '"+ map.noticeId +"' onclick='selectTr(this)'>");
			if (i == 0) {
				clipTdArr.push("	<td style='width: 8%; min-width: 8%;' rowspan='" +clipList.length+"'>"+ key +"</td>");
			}
				
			if (map.groupName == null || map.groupName == '') {
				clipTdArr.push("	<td style='width: 6%; min-width: 6%;' onclick='logDetail(this)'> </td>");
			}else {
				clipTdArr.push("	<td style='width: 6%; min-width: 6%;' onclick='logDetail(this)'>"+ map.groupName +"</td>");
			}
			
			if (map.shootLocation == null || map.shootLocation == '') {
				clipTdArr.push("	<td style='width: 20%; min-width: 20%; max-width: 20%;' onclick='logDetail(this)'><div class='table-column'></div></td>");
			}else {
				clipTdArr.push("	<td style='width: 20%; min-width: 20%; max-width: 20%;' onclick='logDetail(this)'><div class='table-column' title='"+ map.shootLocation +"'>"+ map.shootLocation +"</div></td>");
			}
			
			if (map.sumPage == null || map.sumPage == '') {
				map.sumPage = 0;
			}
			var pageNum = parseFloat(map.sumPage).toFixed(1);
			clipTdArr.push("	<td style='width: 8%; min-width: 8%;' onclick='logDetail(this)'>"+ pageNum +"</td>");
			
			if (map.viewCount == null || map.viewCount =='') {
				map.viewCount = 0;
			}
//			var viewCount = parseFloat(map.viewCount).toFixed(2);
			clipTdArr.push("	<td style='width: 8%; min-width: 8%;' onclick='logDetail(this)'>" + map.viewCount + "</td>");
			
			if (map.shootScene == null || map.shootScene == '') {
				clipTdArr.push("	<td style='width: 30%; min-width: 30%; max-width: 30%;' onclick='logDetail(this)'> </td>");
			}else {
				clipTdArr.push("	<td style='width: 30%; min-width: 30%; max-width: 30%;' onclick='logDetail(this)'><div class='table-column' title='"+ map.shootScene +"'>"+ map.shootScene +"</div></td>");
			}
			
			if (map.bootTime == null || map.bootTime == '') {
				clipTdArr.push("	<td style='width: 10%; min-width: 10%;' onclick='logDetail(this)'> </td>");
			}else {
				var fromatTime = fromatDate(map.bootTime);
				clipTdArr.push("	<td style='width: 10%; min-width: 10%;' onclick='logDetail(this)'>"+ fromatTime +"</td>");
			}
			
			if (map.packupTime == null || map.packupTime == '') {
				clipTdArr.push("	<td style='width: 10%; min-width: 10%;' onclick='logDetail(this)'> </td>");
			}else {
				var fromatTime = fromatDate(map.packupTime);
				clipTdArr.push("	<td style='width: 10%; min-width: 10%;' onclick='logDetail(this)'>"+ fromatTime +"</td>");
			}
			clipTdArr.push("	</tr>");
		}
	}
	
	_clipListTr.append(clipTdArr.join(""));
	
}

//格式化时间
function fromatDate(timeStr){
    var date = new Date(timeStr).Format("yyyy-MM-dd HH:mm:ss");
    return date;
}






//初始化tab键单击事件
  //现场信息
function showSiteInformation(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".shootlog-public").hide();
	$(".site-information-div").show();
	var noticeId = $("#noticeId").val();
	//拼接现场信息
	loadSceneData(noticeId);
}
  //演员出勤
function actorAttendance(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".shootlog-public").hide();
	$(".actor-attendance-div").show();
	loadMajorRoleAttenceData();
}
//部门表现
function departPerformance(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".shootlog-public").hide();
	$(".depart-performance-div").show();
	loadDepartmentPerformance();
}
  //特殊道具
function specialItemInfo(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".shootlog-public").hide();
	$(".special-item-div").show();
	loadSpecialProp();
}
  //重要备注
function importantRemark(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".shootlog-public").hide();
	$(".important-remark-div").show();
	loadImportantRemark();
}


//显示日志详细信息
function logDetail(obj){
	   var noticeId = $(obj).parent("tr").attr("noticeid");
	   var time = $(obj).parent("tr").find("td").eq(0).text();
//	   window.location.href = "<%=basePath%>/shootLogManager/toShootLogList?noticeId=" + noticeId;
	   $("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	   $(".shootlog-public").hide();
	   $(".site-information-div").show();
	   $(".tab_li_current").removeClass("tab_li_current");
	   $("#site_information").addClass("tab_li_current");
	   $("#noticeId").val(noticeId);
	   $("#nowShootTime").val(time);
	   getShootLogDetailInfo();
}

//关闭日志详情
function closePopUpWin(){
	clearInterval(timer);
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-width}, 500);
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
}


//获取日志详细信息
function getShootLogDetailInfo(){
	//取出通告单的id
	var noticeId = $("#noticeId").val();
	//拼接现场信息
	loadSceneData(noticeId);
	
//	$.ajax({
//		url: '/shootLogManager/queryClipInfoList',
//		type: 'post',
//		async: true,
//		data:{noticeId:noticeId},
//		datatype: 'json',
//		success: function(response){
//			if(response.success){
//				//场记单列表
//				var clipInfoList = response.clipInfo;
//				//机位名称列表
//				var nameList = response.nameList;
//				//主要演员出勤信息列表
//				var majorRoleAttenInfo = response.majorRoleAttenInfo;
//				//群众特约演员出勤列表
//				var notMajRoleAttenInfo =response.notMajRoleAttenInfo;
//				//特殊道具列表
//				var specialPropList = response.specialPropInfo;
//				//重要备注列表
//				var importCommentList = response.importCommentInfo;
//				//现场信息
//				var liveList = response.liveInfo;
//				loadClipData(clipInfoList, nameList);
//				
//				//拼接主演员出勤信息
//				loadMajorRoleAttenceData(majorRoleAttenInfo);
//				//拼接群众演员/特约演员出勤信息
//				loadMassRoleAttenData(notMajRoleAttenInfo);
//				loadSpecialPropData(specialPropList);
//				loadImportCommentData(importCommentList);
//			}else{
//					showErrorMessage(response.message);
//			}
//		}
//	});
}



//拼接现场信息
function loadSceneData(noticeId){
	$.ajax({
		url: '/clipManager/queryLiveInfo',
		type: 'post',
		data: {"noticeId": noticeId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var liveList = response.liveInfo;
				//转场信息
				var convertInfoList = liveList.convertInfoList;
				//拼接转场信息
				loadConvertData(convertInfoList);
				//拍摄带号
				if (liveList.tapNo == null || liveList.tapNo =='') {
					$("#tapNoDD").val('');
				}else {
					$("#tapNoDD").val(liveList.tapNo);
				}
				//拍摄地点
				if (liveList.shootLocation == null || liveList.shootLocation=='') {
					$("#shootLocationDD").val('');
				}else {
					$("#shootLocationDD").val(liveList.shootLocation);
					$("#shootLocationDD").prop("title", liveList.shootLocation);
				}
				//拍摄场景
				if (liveList.shootScene == null || liveList.shootScene=='') {
					$("#shootSeneDD").val('');
				}else {
					$("#shootSeneDD").val(liveList.shootScene);
					$("#shootSeneDD").prop("title", liveList.shootScene);
				}
				//出发时间
				if (liveList.startTime == null || liveList.startTime== '') {
					$("#startTimeDD").val('');
				}else {
					var time = liveList.startTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					$("#startTimeDD").val(timeString);
				}
				//到场时间
				if (liveList.arriveTime == null || liveList.arriveTime == '') {
					$("#arriveTimeDD").val('');
				}else {
					var time = liveList.arriveTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					$("#arriveTimeDD").val(timeString);
				}
				//开机时间
				if (liveList.bootTime == null || liveList.bootTime == '') {
					$("#bootTimeDD").val('');
				}else {
					var time = liveList.bootTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					$("#bootTimeDD").val(timeString);
				}
				//收工时间
				if (liveList.packupTime == null || liveList.packupTime == '') {
					$("#packupTimeDD").val('');
				}else {
					var time = liveList.packupTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					$("#packupTimeDD").val(timeString);
				}
			}
		}
	});
	
	
}


//拼接转场信息
function loadConvertData(liveList){
	//拼接转场信息
	var _convertInfoTr = $("#transitionTable");
	var convertArr = [];
	if (liveList == null || liveList.length == 0) {
		convertArr.push("	<tr class='blank-tr'><td colspan='6' style='text-align: center; vertical-align: middle;'>暂无数据</td></tr>");
	}else {
		for(var i=0; i< liveList.length; i++){
			var item = liveList[i];
			convertArr.push('<tr id="'+ item.convertId +'">');
			convertArr.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
			convertArr.push('<span class="td-content">');
			if(item.cshootLocation == null || item.cshootLocation == ''){
				
				convertArr.push('<input type="text">');
				
			}else{
				convertArr.push('<input type="text" value="'+ item.cshootLocation +'">');
			}
			convertArr.push('<input type="button" class="delete-site" onclick="deleteTransitionSite(this)">');
			convertArr.push('<span>');
			convertArr.push('</td>');
			convertArr.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
			if(item.cshootScene == null || item.cshootScene == ""){
				convertArr.push('<input type="text">');
			}else{
				convertArr.push('<input type="text" value="'+ item.cshootScene +'">');
			}
			convertArr.push('</td>');
			convertArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
			if (item.convertTime == null || item.convertTime == '') {
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"  style="text-align: center;">');
			}else {
				var time = item.convertTime.split(" ")[1];
				var timeString = time.split(":")[0] + ":" + time.split(":")[1];
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"  style="text-align: center;" value="'+ timeString +'">');
			}
			convertArr.push('</td>');
			convertArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
			if (item.carriveTime == null || item.carriveTime == '') {
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"  style="text-align: center;">');
			}else {
				var time = item.carriveTime.split(" ")[1];
				var timeString = time.split(":")[0] + ":" + time.split(":")[1];
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})" style="text-align: center;" value="'+ timeString +'">');
			}
			convertArr.push('</td>');
			convertArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
			if (item.cbootTime == null || item.cbootTime == '') {
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"  style="text-align: center;">');
			}else {
				var time = item.cbootTime.split(" ")[1];
				var timeString = time.split(":")[0] + ":" + time.split(":")[1];
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})" style="text-align: center;" value="'+ timeString +'">');
			}
			convertArr.push('</td>');
			convertArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
			if(item.cpackupTime == null || item.cpackupTime == ""){
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"  style="text-align: center;">');
			}else{
				var time = item.cpackupTime.split(" ")[1];
				var timeString = time.split(":")[0] + ":" + time.split(":")[1];
				convertArr.push('<input type="text" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})" style="text-align: center;" value="'+ timeString +'"');
			}
			convertArr.push('</td>');
			convertArr.push('</tr>');
		}
	}
	_convertInfoTr.empty();
	_convertInfoTr.append(convertArr.join(""));
	
	if(isShootLogReadonly) {
		$("#transitionTable").find("input[type='text']").attr('disabled', true);
		$("#transitionTable").find("input[type='button']").remove();		
	}
}



//拼接演员出勤信息
function loadMajorRoleAttenceData(){
	var noticeId= $("#noticeId").val();
	$.ajax({
		url: '/clipManager/queryRoleAttendanceInfo',
		type: 'post',
		data: {"noticeId": noticeId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var attendanceList = response.attendanceList;
				
				if($("input[name=gateCard]:checked").val() == "2"){
					$("input[name=gateCard]").eq(1).prop("checked", true);
					$(".actor-information-body").hide();
					$(".mass-info-body").show();
				}else{
					$("input[name=gateCard]").eq(0).prop("checked", true);
					$(".actor-information-body").hide();
					$(".main-info-body").show();
				}
				
				//演员出勤信息拼接
				produceActorAttenceInfo(attendanceList);
			}
		}
	});
	
}

//演员出勤信息拼接
function produceActorAttenceInfo(attendanceList){
	var attenceArr = [];
	var majorAttenceArr = [];
	var massAttenceArr = [];
	if (attendanceList == null || attendanceList.length < 1) {
		attenceArr.push(" <tr class='blank-tr'><td colspan='6' style='text-align: center; vertical-align: middle;'>暂无数据</td></tr>");
		$("#mainActorGrid").empty();
		$("#mainActorGrid").append(attenceArr.join(""));
//		$("#specialActorGrid").empty();
//		$("#sepcialActorGrid").append(attenceArr.join(""));
		$("#massActorGrid").empty();
		$("#massActorGrid").append(attenceArr.join(""));
	}else {
		for(var i= 0; i< attendanceList.length; i++){
			var item = attendanceList[i];
			if(item.roleType == 1 || item.roleType == 2){
				majorAttenceArr.push('<tr id="'+ item.attendanceId +'">');
				majorAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				if(item.actorName == "" || item.actorName == null){
					majorAttenceArr.push('<input type="text">');
				}else{
					majorAttenceArr.push('<input type="text" value="'+ item.actorName +'">');
				}
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				majorAttenceArr.push('<span class="td-content">');
				majorAttenceArr.push('<input type="text" value="'+ item.viewRoleName +'">');
				majorAttenceArr.push('<input type="button" class="delete-actor" onclick="deleteActor(this);">');
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				if(item.roleType == 1){
					majorAttenceArr.push('<select class="acotr-type"><option value=1>主要演员</option><option value=2>特约演员</option></select>');
				}
				if(item.roleType == 2){
					majorAttenceArr.push('<select class="acotr-type"><option value=2>特约演员</option><option value=1>主要演员</option></select>');
				}
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				if(item.rarriveTime == null || item.rarriveTime == ""){
					majorAttenceArr.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}else{
					var time = item.rarriveTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					majorAttenceArr.push('<input type="text" style="text-align: center;" value="'+ timeString +'" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				if(item.rpackupTime == "" || item.rpackupTime == null){
					majorAttenceArr.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}else{
					var time = item.rpackupTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					majorAttenceArr.push('<input type="text" style="text-align: center;" value="'+ timeString +'" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: center;">');
				if(item.isLateArrive){
					majorAttenceArr.push('<label><input type="checkbox" name="isLateArrive" checked>迟到</label>');
				}else{
					majorAttenceArr.push('<label><input type="checkbox" name="isLateArrive">迟到</label>');
				}
				if(item.isLatePackup){
					majorAttenceArr.push('<label><input type="checkbox" name="isLatePackup" checked>迟放</label>');
				}else{
					majorAttenceArr.push('<label><input type="checkbox" name="isLatePackup">迟放</label>');
				}
				majorAttenceArr.push('</td>');
				majorAttenceArr.push('</tr>');
			}else{
				massAttenceArr.push('<tr id="'+ item.attendanceId +'">');
				massAttenceArr.push('<td style="width: 35%; min-width: 35%; max-width: 35%;">');
				massAttenceArr.push('<span class="td-content">');
				if(item.viewRoleName == "" || item.viewRoleName == null){
					massAttenceArr.push('<input type= "text">');
				}else{
					massAttenceArr.push('<input type="text" value="'+ item.viewRoleName +'">');
				}
				massAttenceArr.push('<input type="button" class="delete-actor" onclick="deleteActor(this);">');
				massAttenceArr.push('</span>');
				massAttenceArr.push('</td>');
				massAttenceArr.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
				if(item.roleNum == "" || item.roleNum == null){
					massAttenceArr.push('<input type= "text">');
				}else{
					massAttenceArr.push('<input type="text" value="'+ item.roleNum +'">');
				}
				massAttenceArr.push('</td>');
				massAttenceArr.push('<td style="width: 25%; min-width: 25%; max-width: 25%;">');
				if(item.rarriveTime == null || item.rarriveTime == ""){
					massAttenceArr.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}else{
					var time = item.rarriveTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					massAttenceArr.push('<input type="text" style="text-align: center;" value="'+ timeString +'" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}
				massAttenceArr.push('</td>');
				massAttenceArr.push('<td style="width: 25%; min-width: 25%; max-width: 25%;">');
				if(item.rpackupTime == "" || item.rpackupTime == null){
					massAttenceArr.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}else{
					var time = item.rpackupTime.split(" ")[1];
					var timeString = time.split(":")[0] + ":" + time.split(":")[1];
					massAttenceArr.push('<input type="text" style="text-align: center;" value="'+ timeString +'" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
				}
				massAttenceArr.push('</td>');
				massAttenceArr.push('</tr>');
			}
				
		}
		if(majorAttenceArr.length != 0){
			$("#mainActorGrid").empty();
			$("#mainActorGrid").append(majorAttenceArr.join(""));

			//只读
			if(isShootLogReadonly) {
				$("#mainActorGrid").find("input[type='button']").remove();
				$("#mainActorGrid").find("input[type='text']").attr('disabled',true);
				$("#mainActorGrid").find("select").attr('disabled',true);
				$("#mainActorGrid").find("input[type='checkbox']").attr('disabled',true);
			}
		}else{
			$("#mainActorGrid").empty();
			$("#mainActorGrid").append("<tr class='blank-tr'><td colspan='6' style='text-align: center; vertical-align: middle;'>暂无数据</td></tr>");
		}
		if(massAttenceArr.length != 0){
			$("#massActorGrid").empty();
			$("#massActorGrid").append(massAttenceArr.join(""));

			//只读
			if(isShootLogReadonly) {
				$("#massActorGrid").find("input[type='text']").attr('disabled',true);
				$("#massActorGrid").find("input[type='button']").remove();
			}
		}else{
			$("#massActorGrid").empty();
			$("#massActorGrid").append("<tr class='blank-tr'><td colspan='6' style='text-align: center; vertical-align: middle;'>暂无数据</td></tr>");
		}
		
	}
}







//添加转场信息
function addTransitionSite(){
	var html = '';
	html += '<tr id="">';
	html += '<td style="width: 20%; min-width: 20%; max-width: 20%;">';
	html += '<span class="td-content">';
	html += '<input type="text">';
	html += '<input type="button" class="delete-site" onclick="deleteTransitionSite(this)">';
	html += '<span>';
	html += '</td>';
	html += '<td style="width: 20%; min-width: 20%; max-width: 20%;"><input type="text" style="text-align:left;"></td>';
	html += '<td style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align:center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"></td>';
	html += '<td style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align:center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"></td>';
	html += '<td style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align:center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"></td>';
	html += '<td style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align:center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})"></td>';
	html += '</tr>';
	
	var addRow = $("#transitionTable").find("tr:last-child");
	var shootLocation = addRow.find("input[type=text]").eq(0).val();
	var shootView = addRow.find("input[type=text]").eq(1).val();
	var startTime = addRow.find("input[type=text]").eq(2).val();
	var endTime = addRow.find("input[type=text]").eq(3).val();
	var startShootingTime = addRow.find("input[type=text]").eq(4).val();
	var endShootingTime = addRow.find("input[type=text]").eq(5).val();
	if(shootLocation == "" && shootView == "" && startTime == "" && endTime == "" && startShootingTime == "" && endShootingTime == ""){
		showInfoMessage("请完善信息后再添加");
		return;
	} 
	
	var blankTr = $("#transitionTable").find("tr.blank-tr");
	if(blankTr.length != 0){
		$("#transitionTable").empty();
	}
	$("#transitionTable").append(html);
	$("#transitionTable").find("tr:last-child").find("input[type=text]").eq(0).focus();
}

//删除转场信息
function deleteTransitionSite(own){
	var id = $(own).parents("tr").attr("id");
	var noticeId = $("#noticeId").val();
	if((id != "") && (id != undefined)){
		popupPromptBox("提示","是否要删除该条信息？", function (){
			$.ajax({
				url: '/clipManager/deleteConvertInfo',
				type: 'post',
				data: {"noticeId": noticeId, "convertIds": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						loadSceneData(noticeId);
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});
		
	}else{
		$(own).parents("tr").remove();
		showSuccessMessage("删除成功");
	}
}

//保存现场信息和转场信息
function saveLiveInfo(){
	var subData = {};
	var nowDate = $("#nowShootTime").val();
	var noticeId = $("#noticeId").val();
	subData.noticeId = $("#noticeId").val();
	subData.tapNo = $("#tapNoDD").val();
	subData.shootLocation = $("#shootLocationDD").val();
	subData.shootScene = $("#shootSeneDD").val();
	if($("#startTimeDD").val() != ""){
		subData.startTime = nowDate + " " + $("#startTimeDD").val() + ":00";
	}else{
		subData.startTime = "";
	}
	if($("#arriveTimeDD").val() != ""){
		subData.arriveTime = nowDate + " " + $("#arriveTimeDD").val() + ":00";
	}else{
		subData.arriveTime = "";
	}
	if($("#bootTimeDD").val() != ""){
		subData.bootTime = nowDate + " " + $("#bootTimeDD").val() + ":00";
	}else{
		subData.bootTime = "";
	}
	if($("#packupTimeDD").val() != ""){
		subData.packupTime = nowDate + " " + $("#packupTimeDD").val() + ":00";
	}else{
		subData.packupTime = "";
	}
	
	//转场信息
	var blankFlag = false;
	var infoMessage = "";
	
	var convertInfoStr = [];
	var blankTr = $("#transitionTable").find("tr.blank-tr");
	if(blankTr.length != 0 && blankTr != undefined){
		subData.convertInfoStr = "";
	}else{
		var trObj = $("#transitionTable").find("tr");
		$.each(trObj, function(i){
			var convertInfoString = "";
			i=i+1;
			var id = $(this).attr("id");
			if(id == undefined){
				id="";
			}
			convertInfoString += id + ',';
			var shootLocation = $(this).find("input[type=text]").eq(0).val();
			if(shootLocation == ""){
				infoMessage += "第"+ i + "行拍摄地点为空，请完善信息";
				blankFlag = true;
				return;
			}else{
				convertInfoString += shootLocation + ',';
			}
			convertInfoString += $(this).find("input[type=text]").eq(1).val() + ',';
			var startTime = $(this).find("input[type=text]").eq(2).val();
			if(startTime != ""){
				startTime = nowDate + " " + startTime + ":00";
			}
			convertInfoString += startTime + ",";
			var endTime = $(this).find("input[type=text]").eq(3).val();
			if(endTime != ""){
				endTime = nowDate + " " + endTime + ":00";
			}
			convertInfoString += endTime + ",";
			var bootTime = $(this).find("input[type=text]").eq(4).val();
			if(bootTime != ""){
				bootTime = nowDate + " " + bootTime + ":00";
			}
			convertInfoString += bootTime + ",";
			var packupTime = $(this).find("input[type=text]").eq(5).val();
			if(packupTime != ""){
				packupTime = nowDate + " " + packupTime + ":00";
			}
			convertInfoString += packupTime;
			convertInfoStr.push(convertInfoString);
		});
		if(blankFlag){
			showInfoMessage(infoMessage);
			return;
		}else{
			subData.convertInfoStr = convertInfoStr.join("##");
		}
	}
	$.ajax({
		url: '/clipManager/saveShootLiveInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				loadSceneData(noticeId);
				//刷新日志列表单行数据
				updateShootRow(subData);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	
}

function updateShootRow(rowData){
	var trObj = $("#shootLogTable").find("tr.select");
	if(trObj.find("td").length == 8){
		trObj.find("td").eq(2).find("div.table-column").attr("title", rowData.shootLocation);
		trObj.find("td").eq(2).find("div.table-column").text(rowData.shootLocation);
		trObj.find("td").eq(5).find("div.table-column").attr("title", rowData.shootScene);
		trObj.find("td").eq(5).find("div.table-column").text(rowData.shootScene);
		trObj.find("td").eq(6).html(rowData.bootTime);
		trObj.find("td").eq(7).html(rowData.packupTime);
		return;
	}
	if(trObj.find("td").length == 7){
		trObj.find("td").eq(1).find("div.table-column").eq(0).attr("title", rowData.shootLocation).html(rowData.shootLocation);
		trObj.find("td").eq(4).find("div.table-column").eq(0).attr("title", rowData.shootScene).html(rowData.shootScene);
		trObj.find("td").eq(5).html(rowData.bootTime);
		trObj.find("td").eq(6).html(rowData.packupTime);
		return;
	}
}


//显示主要演员出勤表
function showActorGrid(flag){
	if(flag == 1){
		$(".actor-information-body").hide();
		$(".main-info-body").show();
	
	}else {
		$(".actor-information-body").hide();
		$(".mass-info-body").show();
	}
}

//添加主演、特约演员
function addMainSepcialActor(){
	var html = [];
	var blankTr = $("#mainActorGrid").find("tr.blank-tr");
	html.push('<tr id="">');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<input type="text">');
	html.push('</td>');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<span class="td-content">');
	html.push('<input type="text">');
	html.push('<input type="button" class="delete-actor" onclick="deleteActor(this);">');
	html.push('</span>');
	html.push('</td>');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<select class="acotr-type"><option value=1>主要演员</option><option value=2>特约演员</option></select>');
	html.push('</td>');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
	html.push('</td>');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
	html.push('</td>');
	html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; text-align: center;">');
	html.push('<label><input type="checkbox" name="isLateArrive">迟到</label>');
	html.push('<label><input type="checkbox" name="isLatePackup">迟放</label>');
	html.push('</td>');
	html.push('</tr>');
	if(blankTr != undefined && blankTr.length != 0){
		$("#mainActorGrid").empty();
	}
	var trObj = $("#mainActorGrid").find("tr");
	var blankFlag = false;
	var infoMessage = "";
	$.each(trObj, function(i){
		i=i+1;
		if(!$(this).hasClass("blank-tr")){
			var roleName = $(this).find("input[type=text]").eq(1).val();
			if(roleName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行角色名称为空";
				return;
			}
		}
	});
	if(blankFlag){
		showInfoMessage(infoMessage+",请完善后再继续添加");
		return;
	}
	$("#mainActorGrid").append(html.join(""));
	$("#mainActorGrid").find("tr:last-child").find("input[type=text]").eq(0).focus();
}




//删除演员出勤信息
function deleteActor(own){
	var id = $(own).parents("tr").attr("id");
	var noticeId = $("#noticeId").val();
	if(id == "" || id == undefined){
		$(own).parents("tr").remove();
		showSuccessMessage("删除成功");
		loadMajorRoleAttenceData();
	}else{
		popupPromptBox("提示","是否要删除该条信息？", function (){
			$.ajax({
				url: '/clipManager/deleteRoleAttendanceInfo',
				type: 'post',
				data: {"noticeId": noticeId, "attendanceIds": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						loadMajorRoleAttenceData();
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});
		
	}
}

//添加群众演员出勤信息
function addMassActor(){
	var html = [];
	var blankTr = $("#massActorGrid").find("tr.blank-tr");
	html.push('<tr id="">');
	html.push('<td style="width: 35%; min-width: 35%; max-width: 35%;">');
	html.push('<span class="td-content">');
	html.push('<input type="text">');
	html.push('<input type="button" class="delete-actor" onclick="deleteActor(this)">');
	html.push('</span>');
	html.push('</td>');
	html.push('<td style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<input type="text">');
	html.push('</td>');
	html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; ">');
	html.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
	html.push('</td>');
	html.push('<td style="width: 25%; min-width: 25%; max-width: 25%; ">');
	html.push('<input type="text" style="text-align: center;" onclick="WdatePicker({dateFmt:&quot;HH:mm&quot;})">');
	html.push('</td>');
	html.push('</tr>');
	if(blankTr != undefined && blankTr.length != 0){
		$("#massActorGrid").empty();
	}
	var trObj = $("#massActorGrid").find("tr");
	var blankFlag = false;
	var infoMessage = "";
	$.each(trObj, function(i){
		i=i+1;
		if(!$(this).hasClass("blank-tr")){
			var roleName = $(this).find("input[type=text]").eq(0).val();
			if(roleName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行角色名称为空";
				return;
			}
		}
	});
	if(blankFlag){
		showInfoMessage(infoMessage+",请完善后再继续添加");
		return;
	}
	$("#massActorGrid").append(html.join(""));
	$("#massActorGrid").find("tr:last-child").find("input[type=text]").eq(0).focus();
}


//保存演员出勤信息
function saveActorAttenInfo(){
	var noticeId = $("#noticeId").val();
	var nowDate = $("#nowShootTime").val();
	var majorguestActorArr = [];
	var majorguestActorStr;
	
	var majorguestFlag = false;
	var majorguestInfo = "";
	var massFlag = false;
	var massesActorArr = [];
	var massesActorStr;
	
	var massesInfo = "";
	//主演特约信息
	var blankTr = $("#mainActorGrid").find("tr.blank-tr");
	if(blankTr.length != 0){
		majorguestActorStr = "";
	}else{
		
		var trObj = $("#mainActorGrid").find("tr");
		$.each(trObj, function(i){
			var majorguestString = "";
			i= i+1;
			var id = $(this).attr("id");
			if(id == undefined){
				id = "";
			}
			majorguestString += id +",";
			var roleType = $(this).find("select").eq(0).val();
			majorguestString += roleType + ",";
			majorguestString += $(this).find("input[type=text]").eq(0).val()+",";
			var roleName = $(this).find("input[type=text]").eq(1).val();
			if(roleName == ""){
				majorguestFlag = true;
				majorguestInfo += "第"+i+"行角色名称为空";
				return;
			}else{
				majorguestString += roleName +",";
			}
			var startTime = $(this).find("input[type=text]").eq(2).val();
			if(startTime == ""){
				majorguestString += "" +",";
			}else{
				startTime = nowDate + " " + startTime + ":00";
				majorguestString += startTime +",";
			}
			var endTime = $(this).find("input[type=text]").eq(3).val();
			if(endTime == ""){
				majorguestString += "" +",";
			}else{
				endTime = nowDate + " " + endTime + ":00";
				majorguestString += endTime +",";
			}
			if($(this).find("input[name=isLateArrive]").is(":checked")){
				majorguestString += "true" +",";
			}else{
				majorguestString += "false" +",";
			}
			if($(this).find("input[name=isLatePackup]").is(":checked")){
				majorguestString += "true";
			}else{
				majorguestString += "false";
			}
			majorguestActorArr.push(majorguestString);
		});
		majorguestActorStr = majorguestActorArr.join("##");
	}
	//群演信息
	var massBlank = $("#massActorGrid").find("tr.blank-tr");
	if(massBlank.length != 0){
		massesActorStr = "";
	}else{
		var trObj = $("#massActorGrid").find("tr");
		$.each(trObj, function(i){
			var massesString = "";
			i=i+1;
			massesInfo += "第" + i + "行";
			var id = $(this).attr("id");
			if(id == undefined){
				id="";
			}
			massesString += id +",";
			var massRoleName = $(this).find("input[type=text]").eq(0).val();
			if(massRoleName == ""){
				massFlag = true;
				massesInfo += ",角色名称为空";
				return;
			}else{
				massesString += massRoleName+",";
			}
			var num = $(this).find("input[type=text]").eq(1).val();
			massesString += num +",";
			var overTime = $(this).find("input[type=text]").eq(2).val();
			if(overTime == ""){
				massesString += "" +",";
			}else{
				overTime = nowDate + " " + overTime + ":00";
				massesString += overTime +",";
			}
			var outTime = $(this).find("input[type=text]").eq(3).val();
			if(outTime == ""){
				massesString += "";
			}else{
				outTime = nowDate + " " + outTime + ":00";
				massesString += outTime;
			}
			massesActorArr.push(massesString);
		});
		massesActorStr = massesActorArr.join("##");
	}
	
	
	if(majorguestFlag){
		showInfoMessage("主演、特约出勤表中"+ majorguestInfo +",请完善信息后再保存");
		return;
	}
	if(massFlag){
		showInfoMessage("群演出勤表中" + massesInfo + ",请完善信息后再保存");
		return;
	}
	$.ajax({
		url: '/clipManager/saveRoleAttendanceInfo',
		type: 'post',
		data: {"noticeId": noticeId, "majorguestActorStr": majorguestActorStr, "massesActorStr": massesActorStr},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				loadMajorRoleAttenceData();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}



//加载部门表现信息
function loadDepartmentPerformance(){
	var noticeId = $("#noticeId").val();
	$.ajax({
		url: '/clipManager/queryDepartmentEvaluateInfo',
		type: 'post',
		data: {"noticeId": noticeId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				//拼接部门表现信息
				var departmentList = response.departmentList;
				produceDepartmentInfo(departmentList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//拼接部门表现信息
function produceDepartmentInfo(departmentList){
	var html = [];
	if(departmentList.length != 0){
		for(var i= 0; i< departmentList.length; i++){
			html.push('<div class="department-div">');
			html.push('<p class="department" id="'+ departmentList[i].departmentId +'">' + departmentList[i].departmentName + '&nbsp;:&nbsp;&nbsp;</p>');
			html.push('<ul class="grade-star">');
			if(departmentList[i].score != null){
				
				var score = departmentList[i].score;
				if(score == 0){
					html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
					html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
					html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
					html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
					html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
					html.push('<li class="grade-tips"><span class="star-info"></span><span class="grade-df"></span></li>');
				}else{
					var no = divide(score, 20);
					
					if(no <= 1){
						if(no < 1){
							html.push('<li class="half-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}else{
							html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}
						for(var j= 0; j< 4; j++){
							html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
						}
						html.push('<li class="grade-tips"><span class="star-info">非常差</span><span class="grade-df">'+ departmentList[i].score +'</span></li>');	
					}
					else if(no > 1 && no <= 2){
						if(no < 2){
							html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							html.push('<li class="half-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}else{
							html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}
						for(var j= 0; j< 3; j++){
							html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
						}
						html.push('<li class="grade-tips"><span class="star-info">很差</span><span class="grade-df">'+ departmentList[i].score +'</span></li>');
					}
					else if(no > 2 && no <= 3){
						if(no < 3){
							for(var j= 1; j< no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
							html.push('<li class="half-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}else{
							for(var j= 1; j<= no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
						}
						for(var j= 0; j< 2; j++){
							html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
						}
						html.push('<li class="grade-tips"><span class="star-info">一般</span><span class="grade-df">'+ departmentList[i].score +'</span></li>');
					}
					else if(no > 3 && no <= 4){
						if(no < 4){
							for(var j= 1; j< no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
							html.push('<li class="half-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}else{
							for(var j= 1; j<= no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
						}
						for(var j= 0; j< 1; j++){
							html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
						}
						html.push('<li class="grade-tips"><span class="star-info">好</span><span class="grade-df">'+ departmentList[i].score +'</span></li>');
					}
					else if(no > 4 && no <= 5){
						if(no < 5){
							for(var j= 1; j< no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
							html.push('<li class="half-star"><span class="star-left"></span><span class="star-right"></span></li>');
						}else{
							for(var j= 1; j<= no; j++){
								html.push('<li class="full-star"><span class="star-left"></span><span class="star-right"></span></li>');
							}
						}
						html.push('<li class="grade-tips"><span class="star-info">非常好</span><span class="grade-df">'+ departmentList[i].score +'</span></li>');
					}
				}
				
				
					
					
				
			}else{
				html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
				html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
				html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
				html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
				html.push('<li><span class="star-left"></span><span class="star-right"></span></li>');
				html.push('<li class="grade-tips"><span class="star-info"></span><span class="grade-df"></span></li>');
			}
			
			html.push('</ul>');
			html.push('</div>');
		}
		$("#departmentGradeCon").empty();
		$("#departmentGradeCon").append(html.join(""));
		
		
		loadGradeScore();
	}
}

//初始化星级时间
function loadGradeScore(){
	var $ul = $('.grade-star');
//    var $df = $('.grade-df');
    $ul.off().on('click', '.star-left', function(e){
        var $el = $(e.currentTarget).parent();

        setFullStar($ul, $el,  true);

    }).on('click', '.star-right', function(e){

        var $el = $(e.currentTarget).parent();

        setFullStar($ul, $el,  false);

    });
}

/*
 * 填充星星
 * @params index 填充到序号为止 0开始
 */
 function setFullStar($ul, $el,  isHalf, score){
     var index = $el && $el.length ? $el.index() : undefined;
     var df = score || (index === undefined ? 0 : (index + 1) * 20);

     $el.parent("ul").find('>li').removeClass('full-star').removeClass('half-star').find('.star-info').hide();
//     $ul.find('>li:lt('+ (isHalf || index === undefined ? index : index + 1) +')').addClass('full-star');
     $el.parent("ul").find('>li:lt('+ (isHalf || index === undefined ? index : index + 1) +')').addClass('full-star');

     if($el && $el.length){
         $el.find('.star-info').css({
             'display': index !== undefined ? 'block' : 'none',
             'right': isHalf ? '50%' : 'auto',
             'left': isHalf ? 'auto' : '50%'
         });
     }
     if(isHalf){
         $el && $el.addClass('half-star');
         !score && (df -= 10);
     }
     $el.parent("ul").find("span.grade-df").text(df);
     switch(true)
     {
     case df <= 20 :
       $el.parent("ul").find("span.star-info").show().text("非常差");
       break;
     case df > 20 && df <= 40 :
    	 $el.parent("ul").find("span.star-info").show().text("很差");
       break;
     case df > 40 && df <= 60 :
    	 $el.parent("ul").find("span.star-info").show().text("一般");
         break;
     case df > 60 && df <= 80 :
    	 $el.parent("ul").find("span.star-info").show().text("好");
         break;
     default:
    	 $el.parent("ul").find("span.star-info").show().text("非常好");
     }
 }
 
 /*
  *   根据分数获取星的位置
  *   @params  $ul
  *           score 分数
  *
  *   @return  $el
  *           isHalf
  */
  function getCurElem($ul, score){
      if(!score){
          return {
              '$el': undefined,
              'isHalf': false
          };
      }
      if(score > 100){
          score = 100;
      }
      var isHalf = !!(score % 20);
      var index = parseInt(score / 20);

      return {
          '$el': $ul.find('>li:eq('+ (isHalf ? index : index - 1) +')'),
          'isHalf': isHalf
      };

  }

  
//保存部门评价信息
  function saveDepartmentInfo(){
	  var noticeId = $("#noticeId").val();
	  
	  var departmentScoreArr = [];
	  var departmentObj = $(".department-div");
	  $.each(departmentObj, function(){
		  var departmentScoreStr = "";
		    var id = $(this).find("p.department").attr("id");
			if(id == undefined){
				id = "";
			}
		  departmentScoreStr += id + ",";
		  departmentScoreStr += $(this).find("span.grade-df").text();
		  departmentScoreArr.push(departmentScoreStr);
	  });
	  $.ajax({
		  url: '/clipManager/saveDepartmentScore',
		  type: 'post',
		  data: {"noticeId": noticeId, "departmentScoreStr": departmentScoreArr.join("##")},
		  datatype: 'json',
		  success: function(response){
			  if(response.success){
				  showSuccessMessage("保存成功");
				  loadDepartmentPerformance();
			  }else{
				  showErrorMessage(response.message);
			  }
		  }
	  });
  }
  

//查询特殊道具信息
  function loadSpecialProp(){
	  var noticeId = $("#noticeId").val();
	  $.ajax({
		  url: '/clipManager/queryClipPropInfo',
		  type: 'post',
		  data: {"noticeId": noticeId},
		  datatype: 'json',
		  success: function(response){
			  if(response.success){
				  var propList = response.propList;
				  //拼接特殊道具表
				  produceSpecialPropInfo(propList);
			  }else{
				  showErrorMessage(response.message);
			  }
		  }
	  });
  }
  
//拼接特殊道具表
function  produceSpecialPropInfo(propList){
	var html = [];
	if(propList != null && propList.length != 0){
		for(var i= 0; i< propList.length; i++){
			html.push('<tr id="'+ propList[i].propId +'">');
			html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
			html.push('<span class="td-content">');
			html.push('<input type="text" value="'+ propList[i].name +'">');
			html.push('<input type="button" class="delete-prop" onclick="deleteSepcialProp(this)">');
			html.push('</span>');
			html.push('</td>');
			html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
			html.push('<input type="text" style="box-sizing: border-box; text-align: right; padding-right: 5px;" value="'+ propList[i].num +'" onkeyup="onlyNumber(this)">');
			html.push('</td>');
			html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
			if(propList[i].comment == null){
				propList[i].comment = "";
			}
			html.push('<input type="text" value="'+ propList[i].comment +'">');
			html.push('</td>');
			html.push('<td style="width: 20%; min-width: 20%; max-width: 20%; text-align: center;">');
			if(propList[i].attpackId != ""){
				html.push('<input type="button" class="appack-tag" id="'+ propList[i].attpackId +'" onclick="attpackUpload(this)">');
			}
			html.push('</td>');
			html.push('</tr>');
		}
	}else{
		html.push('<tr class="blank-tr" id="">');
		html.push('<td colspan="4" style="text-align: center; vertical-align: middle;">暂无数据</td>');
		html.push('</tr>');
	}
	$("#specialPropGrid").empty();
	$("#specialPropGrid").append(html.join(""));
	//只读
	if(isShootLogReadonly) {
		$("#specialPropGrid").find("input[type='text']").attr('disabled',true);
		$("#specialPropGrid").find(".delete-prop").remove();
	}
}

//显示上传窗口
function attpackUpload(own){
	var attpackId = $(own).attr("id");
	if($("#special_item").hasClass("tab_li_current")){
		$(".video-list-p").hide();
		$(".vedio-file-list").hide();
	}else{
		$(".video-list-p").show();
		$(".vedio-file-list").show();
	}
	$("#uploadWindow").jqxWindow("open", function(){
		//初始化上传插件
		initUploader();
		$("#attpackId").val(attpackId);
		//查询已有的附件信息
		loadReadyAttpackInfo();
	});
	
}

var uploader;
//初始化上传插件
function initUploader(){
	uploader = WebUploader.create({  
	       // 选完文件后，是否自动上传。  
	       auto: false,  
	       // 文件接收服务端。  
	       server: '/attachmentManager/uploadAttachment',  
	       timeout: 30*60*1000,//超时
	       // 选择文件的按钮。可选。  
	       // 内部根据当前运行是创建，可能是input元素，也可能是flash.  
	       pick: '#uploadFileBtn',  
	  
	       // 只允许选择图片文件。  
//	       accept: {
//		        title: 'Images',
//		        extensions: 'jpg,jpeg,png',
//		        mimeTypes: 'image/jpg,image/jpeg,image/png'
//		    },
	       thumb: {
	    	   width: 110,
	    	   height: 110,
	    	   crop: false
	       },
	       method:'POST',  
	   });  
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
		var fileUl = $("#uploadFileList");
		
		if($("#uploadFileList").hasClass("blank")){
			$("#uploadFileList").empty();
			$("#uploadFileList").removeClass("blank");
		}
		
		if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		
		var $li = $("<li class='upload-file-list-li'></li>");
		
		uploader.makeThumb( file, function( error, ret ) {
	        if ( error ) {
	            $li.html("预览错误");
	            $("#uploadFileList").append($li);
	        } else {
	        	$li.append("<img alt='' src='" + ret + "' /><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#uploadFileList").append($li);
	        }
	    });
	});
	
	//当文件开始上传时
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	//当文件上传结束时
	uploader.on('uploadFinished', function(file) {
		    $(".loader").text("上传成功");
		    $('#myLoader').dimmer("hide");
			showSuccessMessage("上传成功");
			closeUploadWin();
		
	});
	
	uploader.on('uploadComplete', function(file) {
		
	});
}

//删除未上传的文件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	uploader.removeFile(fileId, true);
	own.parent("li").remove();
}
//关闭上传窗口
function closeUploadWin(){
	$("#uploadWindow").jqxWindow("close");
}

//查询已有的附件信息
function loadReadyAttpackInfo(){
	$.ajax({
		url: '/clipManager/queryAttachmentById',
		type: 'post',
		data: {"attpackId": $("#attpackId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var attachmentList = response.attachmentList;
				if(attachmentList != null || attachmentList.length != 0){
					var html = [];
					var vedioHtml = [];
					for(var i= 0; i< attachmentList.length; i++){
						var attachment = attachmentList[i];
						if(attachment.type == 3){
							/*vedioHtml.push("<li class='upload-file-list-li'><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p><audio src='/fileManager/previewAttachment?address="+ attachment.hdPreviewUrl +"' controls='controls'>您的浏览器不支持该播放器</audio><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId +
									"\")'></a></li>");*/
							vedioHtml.push("<li class='upload-file-list-li'><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p><a class='music stop' title='播放' data-src='/fileManager/previewAttachment?address="+ attachment.hdPreviewUrl +"' onclick='playVideo(this)'></a><a class='deleteTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId +
							"\")'></a></li>");
						}
						else{
							html.push("<li class='upload-file-list-li' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type 
									+"\")'><img src='/fileManager/previewAttachment?address="+ attachment.hdPreviewUrl +"' title='"+ attachment.name 
									+"'><a class='closeTag' title='删除' onclick='deleteUploadedFile(event,this,\""+ attachment.attachmentId +"\")'></a><p class='file-list-tips' title='"
									+ attachment.name +"'>" + attachment.name + "</p></li>");
//							html.push("<li class='upload-file-list-li'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"' title='"+ attachment.name +"'><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>")
						}
					
					}
					if(html.length != 0){
						$("#uploadFileList").removeClass("blank");
						$("#uploadFileList").empty();
						$("#uploadFileList").append(html.join(""));
						//只读
						if(isShootLogReadonly) {
							$("#uploadFileList").find(".closeTag").remove();
						}
					}else{
						$("#uploadFileList").addClass("blank");
						html.push('<p class="blank-p">暂无图片</p>');
						$("#uploadFileList").append(html);
					}
					
					if(vedioHtml.length != 0){
						$("#uploadFilevedio").removeClass("blank");
						$("#uploadFilevedio").empty();
						$("#uploadFilevedio").append(vedioHtml.join(""));
						//只读
						if(isShootLogReadonly) {
							$("#uploadFilevedio").find(".deleteTag").remove();
						}
					}else{
						$("#uploadFilevedio").addClass("blank");
						vedioHtml.push('<p class="blank-p">暂无音频文件</p>');
						$("#uploadFilevedio").append(vedioHtml);
					}
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//预览附件
function previewAtts(attpackId, type, hdPreviewUrl) {
	if (type == 2) {
		window.open("/attachmentManager/toPreviewPage?attpackId='" + attpackId + "'&type=" + type);
	} else {
		window.open("/fileManager/previewAttachment?address=" + hdPreviewUrl);
		return;
	}
}
//播放音频
function playVideo(own){
	if($(own).hasClass("stop")){
		var src = $(own).attr("data-src");
		$("a.play").removeClass("play").addClass("stop").attr("title","播放");
		$("#myVideo").attr("src", src);
		$(own).removeClass("stop").addClass("play");
		document.getElementById("myVideo").play();
		$(own).attr("title", "暂停");
		
		return;
	}
	if($(own).hasClass("play")){
		$(own).removeClass("play").addClass("stop");
		document.getElementById("myVideo").pause();
		$(own).attr("title", "播放");
		return;
	}
}

//删除上传成功的文件附件
function deleteUploadedFile(ev,own,attId){
	
	own= $(own);
	popupPromptBox("提示","是否删除该附件？", function () {
		$.ajax({
	    	url:'/attachmentManager/deleteAttachment',
	    	type:'post',
	    	dataType:'json',
	    	data:{attachmentId: attId},
	    	success:function(data){
	    		if(data.success){
	    			own.parent("li").remove();
	    			showSuccessMessage("删除成功！");
	    		}else{
	    			showErrorMessage('删除附件失败');
	    		}
	    		
	    	}   	
		});
    });
	ev.stopPropagation();
}

//上传图片
function uploadPropImg(){
	var attpackId = $("#attpackId").val();
	
	if (uploader.getFiles().length == 0) {
		showSuccessMessage("上传成功");
		closeUploadWin();
	} else {
		uploader.option('formData', {
			attpackId: attpackId
		});
		uploader.upload();
	}
}

//删除道具信息
function deleteSepcialProp(own){
	var id = $(own).parents("tr").attr("id");
	if(id == "" || id == undefined){
		$(own).parents("tr").remove();
		showSuccessMessage("删除成功");
		loadSpecialProp();
	}else{
		popupPromptBox("提示","是否要删除该条信息？", function (){
			$.ajax({
				url: '/clipManager/deleteClipPropInfo',
				type: 'post',
				data: {"noticeId": $("#noticeId").val(), "propIds": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						loadSpecialProp();
					}
				}
			});
		});
		
	}
}

//添加特殊道具
function addSpecialProp(){
	var blankTr = $("#specialPropGrid").find("tr.blank-tr");
	var blankFlag = false;
	var infoMessage = "";
	var html = [];
	html.push('<tr id="">');
	html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
	html.push('<span class="td-content">');
	html.push('<input type="text">');
	html.push('<input type="button" class="delete-prop" onclick="deleteSepcialProp(this)">');
	html.push('</span>');
	html.push('</td>');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
	html.push('<input type="text" style="box-sizing: border-box; text-align: right; padding-right: 5px;" onkeyup="onlyNumber(this)">');
	html.push('</td>');
	html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
	html.push('<input type="text">');
	html.push('</td>');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%; text-align: center;"></td>');
	html.push('</tr>');
	if(blankTr.length > 0){
		$("#specialPropGrid").empty();
	}else{
		var trObj = $("#specialPropGrid").find("tr");
		$.each(trObj, function(i){
			i=i+1;
			var propName = $(this).find("input[type=text]").eq(0).val();
			if(propName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行" +",";
			}
		});
	}
	if(blankFlag){
		showInfoMessage(infoMessage + "道具名称为空，请完善信息");
		return;
	}
	$("#specialPropGrid").append(html.join(""));
	$("#specialPropGrid").find("tr:last-child").find("input[type=text]").eq(0).focus();
}

//保存特殊道具
function saveSpecialPropInfo(){
	var noticeId = $("#noticeId").val();
	var clipPropInfoStr = [];
	var blankFlag = false;
	var infoMessage = "";
	var blankTr = $("#specialPropGrid").find("tr.blank-tr");
	if(blankTr.length != 0 && blankTr != undefined){
		
	}else{
		var trObj = $("#specialPropGrid").find("tr");
		$.each(trObj, function(i){
			i= i+1;
			var clipPropInfoString = "";
			var id = $(this).attr("id");
			if(id == undefined){
				id = "";
			}
			clipPropInfoString += id +",";
			var propName = $(this).find("input[type=text]").eq(0).val();
			if(propName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行道具名称为空";
				return;
			}else{
				clipPropInfoString += propName +",";
			}
			clipPropInfoString += $(this).find("input[type=text]").eq(1).val()+",";
			clipPropInfoString += $(this).find("input[type=text]").eq(2).val();
			clipPropInfoStr.push(clipPropInfoString);
		});
	}
	if(blankFlag){
		showInfoMessage(infoMessage +",请先完善信息");
		return;
	}
	//保存
	$.ajax({
		url: '/clipManager/saveClipPropInfo',
		type: 'post',
		data: {"noticeId": noticeId, "clipPropInfoStr": clipPropInfoStr.join("##")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				loadSpecialProp();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//拼接重要备注
function loadImportantRemark(){
	var noticeId = $("#noticeId").val();
	$.ajax({
		url: '/clipManager/queryImportantCommentList',
		type: 'post',
		data: {"noticeId": noticeId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var commentInfoList = response.commentInfoList;
				var html = [];
				if(commentInfoList != null && commentInfoList.length != 0){
					for(var i= 0; i< commentInfoList.length; i++){
						html.push('<tr id="'+ commentInfoList[i].commentId +'">');
						html.push('<td style="width: 70%; min-width: 70%; max-width: 70%;">');
						html.push('<span class="td-content">');
						html.push('<input type="text" value="'+ commentInfoList[i].content +'">');
						html.push('<input type="button" class="delete-remark" onclick="deleteRemark(this)">');
						html.push('</span>');
						html.push('</td>');
						html.push('<td style="width: 30%; min-width: 30%; max-width: 30%; text-align: center;">');
						if(commentInfoList[i].attpackId != ""){
							html.push('<input type="button" class="appack-tag" id="'+ commentInfoList[i].attpackId +'" onclick="attpackUpload(this)">');
						}
						html.push('</td>');
						html.push('</tr>');
					}
				}else{
					html.push('<tr class="blank-tr" id="">');
					html.push('<td colspan="2" style="text-align: center; vertical-align: middle;">暂无数据</td>');
					html.push('</tr>');
				}
				$("#remarkBodyTable").empty();
				$("#remarkBodyTable").append(html.join(""));

				//只读
				if(isShootLogReadonly) {
					$("#remarkBodyTable").find("input[type='text']").attr('disabled',true);
					$("#remarkBodyTable").find(".delete-remark").remove();
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//添加备注
function addRemark(){
	var blankTr = $("#remarkBodyTable").find("tr.blank-tr");
	var blankFlag = false;
	var infoMessage = "";
	var html = [];
	html.push('<tr id="">');
	html.push('<td style="width: 70%; min-width: 70%; max-width: 70%;">');
	html.push('<span class="td-content">');
	html.push('<input type="text">');
	html.push('<input type="button" class="delete-remark" onclick="deleteRemark(this)">');
	html.push('</span>');
	html.push('</td>');
	html.push('<td style="width: 30%; min-width: 30%; max-width: 30%; text-align: center;">');
	html.push('</td>');
	html.push('</tr>');
	if(blankTr.length > 0){
		$("#remarkBodyTable").empty();
	}else{
		var trObj = $("#remarkBodyTable").find("tr");
		$.each(trObj, function(i){
			i=i+1;
			var remarkName = $(this).find("input[type=text]").eq(0).val();
			if(remarkName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行" +",";
			}
		});
	}
	if(blankFlag){
		showInfoMessage(infoMessage + "备注内容为空，请完善信息");
		return;
	}
	$("#remarkBodyTable").append(html.join(""));
	$("#remarkBodyTable").find("tr:last-child").find("input[type=text]").eq(0).focus();
}

//删除备注信息
function deleteRemark(own){
	var id = $(own).parents("tr").attr("id");
	var noticeId = $("#noticeId").val();
	if(id != "" && id != undefined){
		popupPromptBox("提示","是否要删除该条信息？", function (){
			$.ajax({
				url: '/clipManager/deleteImportCommentInfo',
				type: 'post',
				data: {"noticeId": noticeId, "commentIds": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						loadImportantRemark();
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});
		
	}else{
		$(own).parents("tr").remove();
		showSuccessMessage("删除成功");
		loadImportantRemark();
	}
}

//保存备注信息
function saveImportantRemark(){
	var noticeId = $("#noticeId").val();
	var commentInfoStr = [];
	var blankFlag = false;
	var infoMessage = "";
	var blankTr = $("#remarkBodyTable").find("tr.blank-tr");
	if(blankTr.length != 0 && blankTr != undefined){
		
	}else{
		var trObj = $("#remarkBodyTable").find("tr");
		$.each(trObj, function(i){
			i= i+1;
			var commentInfoString = "";
			var id = $(this).attr("id");
			if(id == undefined){
				id = "";
			}
			commentInfoString += id +",";
			var remarkName = $(this).find("input[type=text]").eq(0).val();
			if(remarkName == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行备注内容为空";
				return;
			}else{
				commentInfoString += remarkName;
			}
			commentInfoStr.push(commentInfoString);
		});
	}
	if(blankFlag){
		showInfoMessage(infoMessage +",请先完善信息");
		return;
	}
	$.ajax({
		url: '/clipManager/saveClipCommentInfo',
		type: 'post',
		data: {"noticeId": noticeId, "commentInfoStr": commentInfoStr.join("##")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				loadImportantRemark();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}