var dataMap;

var pictureCount;
var noticePictureList;
var noticePublished;

var uploader;
var version;
var crewType = null;
$(document).ready(function(){
	//获取当前剧组类型
	getCrewType();
	
	if($("#noticeId").val() != ""){
		//加载表格数据
		loadNoticeTableData();
		//设置td中textarea的高度，不要显示滚动条
		$(".textarea-height").each(function(){
			var height =this.scrollHeight+'px';
			$(this).css({"height": height});
			var tdHeight = $(this).parents("td").height();
			//设置提示信息的高度
			$(this).prev("span.td-tips").css({"height": tdHeight+1});
			$(this).next("div.make-notice-time").css({"height": tdHeight+1});
			//设置提示信息居中
			var content = $(this).prev("span.td-tips");
			var contentHeight = content.find("span").eq(0).height();
			var top = subtract(divide(tdHeight, 2), divide(contentHeight,2));
			content.find("span").css({"top": top+"px"});
		});
		//重置按钮组位置
		resizeButtonPosition();
		//初始化大小插件
//		initSmartspinner();
		//初始化发布弹窗
		initPublicWin();
		//初始化消息窗口和图片上传
		initMessageAndUpLoader();
		
		
	}
});

//获取年月日（格式：yyyy.MM.dd）
function getCurrDateFormat(date){
	var curronDate = date.getFullYear()+"."+(date.getMonth()+1)+"."+date.getDate();
	return curronDate;
}

//获取当前剧组类型
function getCrewType(){
	$.ajax({
		url: '/scenarioManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
			}else{
					parent.showErrorMessage(response.message);
			}
		}
	});
}

//获取小时分钟秒
function getCurrTime(date){
	var hours;
	var minutes;
	var seconds;
	if(date.getHours() < 10){
		hours = "0" + date.getHours();
	}else{
		hours = date.getHours();
	}
	if(date.getMinutes() < 10){
		minutes = "0" + date.getMinutes();
	}else{
		minutes = date.getMinutes();
	}
	if(date.getSeconds() < 10){
		seconds = "0" + date.getSeconds();
	}else{
		seconds = date.getSeconds();
	}
	var curronTime = hours+ "：" + minutes + "："+ seconds;
	return curronTime;
}

//

//获取表格数据
function loadNoticeTableData(){
	$.ajax({
		url: '/notice/getNoticePrintData',
		type: 'post',
		async: false,
		data: {noticeId: $("#noticeId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				pictureCount = response.pictureCount;
				noticePictureList = response.noticePictureList;
				dataMap = response.data;
				noticePublished = response.noticePublished;
				produceNoticeTable(dataMap);
			}
		}
	});
}

//生成表格
function produceNoticeTable(dataMap){
	//生成表格的标题信息
	if(dataMap.noticeName == null){
		dataMap.noticeName = "";
	}
	if(dataMap.lastGroupNoticeTime == null || dataMap.lastGroupNoticeTime == ""){
		dataMap.lastGroupNoticeTime.version = "";//版本号
		dataMap.lastGroupNoticeTime.groupDirector = "";//导演
		dataMap.lastGroupNoticeTime.breakfastTime = "";//早餐时间
		dataMap.lastGroupNoticeTime.departureTime = "";//出发时间
		dataMap.lastGroupNoticeTime.weatherInfo = "";//天气
	}else{
		if(dataMap.lastGroupNoticeTime.version == ""){
			dataMap.lastGroupNoticeTime.version == "";
		}
		if(dataMap.lastGroupNoticeTime.groupDirector = ""){
			dataMap.lastGroupNoticeTime.groupDirector = "";
		}
		if(dataMap.lastGroupNoticeTime.breakfastTime = ""){
			dataMap.lastGroupNoticeTime.breakfastTime = "";
		}
		if(dataMap.lastGroupNoticeTime.departureTime = ""){
			dataMap.lastGroupNoticeTime.departureTime = "";
		}
		if(dataMap.lastGroupNoticeTime.weatherInfo = ""){
			dataMap.lastGroupNoticeTime.weatherInfo = "";
		}
	}
	if(dataMap.noticeName == null){
		dataMap.noticeName = "";
	}
	$("#noticeTableTitle").html(dataMap.noticeName);//通告单标题
	if(dataMap.noticeDate == null){
		dataMap.noticeDate = "";
	}
	if(dataMap.day == null){
		dataMap.day = "";
	}
	if(dataMap.shootDays == null){
		dataMap.shootDays = "";
	}
	$("#date").html(dataMap.noticeDate + "&nbsp;&nbsp;周" + dataMap.day + "&nbsp;&nbsp;第" + dataMap.shootDays + "天");//通告单日期
	$("#shootLocationInfos").html("拍摄地点:&nbsp;&nbsp;"+ dataMap['myNoticeTime'].shootLocationInfos);
	$("#groupDirector").html( dataMap['myNoticeTime'].groupDirector + "&nbsp;&nbsp;" + dataMap.viewCount + "场/" + parseFloat(dataMap.pageCount).toFixed(1) +"页");
	
	//生成表格主体信息
	var html = [];
	var basicTr = [];//定义基准行
     
   
	
	
	
	/*排查是否有 -特别-信息
	 * 排查场景和内容提要的字符长度
	 * 排查是否有备注字段
	 * 排查特约，群演，服化道最大字符长度
	 * 做相应操作**/
	
	var maxSpecial = dataMap.maxSpecial;//最大特别字符串
	var maxLocation = dataMap.maxLocation;//最大主场景
	var maxContent  = dataMap.maxContent;//最大内容提要
	var maxGuest = dataMap.maxGuest;//最大特约字符串
	var maxMass = dataMap.maxMass;//最大群演字符串
	var maxClothPro = dataMap.maxClothPro;//最大服化道字符串
	var maxRemark = dataMap.maxRemark;//最大备注字符串
	
	var roleLength = dataMap.mainRoleList.length;
	var cellsCount = add(4,add(1,add(6,roleLength))); 
	
	var specialStyle="";
	var remarkStyle = "";
	var viewStyle = "";
	var contentStyle = "";
	var guestStyle = "";//特约style
	var massStyle = "";//群演style
	var clothProStyle= "";//服化道style
	
	var accountWidth = 1320;
    var mainRoleWidth = multiply(roleLength, 26);
	var residualWidth = subtract(accountWidth, add(132, mainRoleWidth));//减去固定宽剩余宽度（132：集场：50，气氛：50，页数：32）；
	
	var maxSpecialLength = maxSpecial.length;
	var maxLocationLength = maxLocation.length;
	var maxContentLength = maxContent.length;
	var maxGuestLength = maxGuest.length;
	var maxMassLength = maxMass.length;
	var maxClothProLength = maxClothPro.length;
	var maxRemarkLength = maxRemark.length;
	
	
	
	if(maxSpecialLength == 0 || maxSpecialLength == 1){//特别
		maxSpecialLength = 2;
	}
	if(maxLocationLength == 0 || maxLocationLength == 1){//场景
		maxLocationLength = 2;
	}
	if(maxContentLength == 0 || maxContentLength == 1){//内容提要
		maxContentLength = 4;
	}
	if(maxGuestLength == 0 || maxGuestLength == 1){//特约
		maxGuestLength = 2;
	}
	if(maxMassLength == 0 || maxMassLength == 1){//群众
		maxMassLength = 2;
	}
	if(maxClothProLength == 0 || maxClothProLength == 1){//服化道
		maxClothProLength = 3;
	}
	if(maxRemarkLength == 0 || maxRemarkLength == 1){//备注
		maxRemarkLength = 2;
	}
	
	var otherWidth = add(maxSpecialLength, add(maxLocationLength, add(maxContentLength, add(maxGuestLength, add(maxMassLength, add(maxClothProLength, maxRemarkLength))))));//剩余列总列数
    basicTr.push('<tr style="height: 0px;">');
	

	
	//按比例计算每列列宽
	var specialWidth = multiply(divide(maxSpecialLength, otherWidth), residualWidth);//特约列所占宽度
	basicTr.push('<td style="width: '+ specialWidth  +'px; max-width: '+ specialWidth +'px;"></td>');
	specialStyle = 'style="width: '+ specialWidth +'px; max-width: ' + specialWidth + 'px;"';
	
	basicTr.push('<td  style="width: 50px; min-width: 50px; max-width: 50px;"></td>');//集场列-固定
	basicTr.push('<td style="width: 50px; min-width: 50px; max-width: 50px;"></td>');//气氛列-固定
	basicTr.push('<td style="width: 32px; min-width: 32px; max-width: 32px;"></td>');//页数-固定
	
	
	var locationWidth = multiply(divide(maxLocationLength, otherWidth), residualWidth);//场景列所占宽度
	basicTr.push('<td style="width: '+ locationWidth  +'px; min-width: '+ locationWidth +'px; max-width: '+ locationWidth +'px;"></td>');
	viewStyle = 'style="width: '+ locationWidth +'px; max-width: ' + locationWidth + 'px;"';
	
	var contentWidth = multiply(divide(maxContentLength, otherWidth), residualWidth);//内容提要列所占宽度
	basicTr.push('<td style="width: '+ contentWidth  +'px; max-width: '+ contentWidth +'px;"></td>');
	contentStyle = 'style="width: '+ contentWidth +'px; max-width: ' + contentWidth + 'px;"';
	
	
	
	
	//第一行
	html.push('<tr>');
	html.push('  <td class="font-size-style" style="text-align: left;" colspan="' + cellsCount + '">');
	html.push("早餐时间:&nbsp;" +dataMap['myNoticeTime'].breakfastTime + "&nbsp;|&nbsp;出发:&nbsp;" + dataMap['myNoticeTime'].departureTime +"&nbsp;|&nbsp;天气:  " + dataMap['myNoticeTime'].weatherInfo);
	html.push('  </td>');
	html.push('</tr>');
	//第二行
	if(dataMap.myNoticeTime != null && dataMap.myNoticeTime !=""){
		if(dataMap.myNoticeTime.noticeContact == null){//联系人
			dataMap.myNoticeTime.noticeContact = "";
		}
		if(dataMap['myNoticeTime'].insideAdvert == null){//商植
			dataMap['myNoticeTime'].insideAdvert = "";
		}
		if(dataMap.myNoticeTime.roleInfo == null){//特约，群众
			dataMap.myNoticeTime.roleInfo = "";
		}
		if(dataMap.myNoticeTime.note == null){
			dataMap.myNoticeTime.note = "";
		}
		if(dataMap.myNoticeTime.remark == null){
			dataMap.myNoticeTime.remark = "";
		}
		if(dataMap.myNoticeTime.roleConvertRemark == null){
			dataMap.myNoticeTime.roleConvertRemark = "";//其他提示
		}
		if(dataMap.myNoticeTime.version == null){
			version = "";
		}else{
			version = dataMap.myNoticeTime.version;
		}
	}else{
		dataMap.myNoticeTime.noticeContact = "";
		dataMap.myNoticeTime.roleInfo == "";
		dataMap.myNoticeTime.note = "";
		dataMap.myNoticeTime.remark = "";
	}
	
	html.push('<tr>');
	html.push(' <td class="font-size-style" style="text-align: left; vertical-align: top;" colspan="6" rowspan= "2">');
	
	html.push('<textarea class="textarea-height" style="height: 99%;" readonly rows="" cols="">'+dataMap.myNoticeTime.noticeContact+'</textarea>');
	html.push(' </td>');
	
	
	
	//生成角色列表
	basicTr.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"></td>');//角色列
	html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">角色</p></td>');
	for(var i= 0; i< dataMap.mainRoleList.length; i++){
		basicTr.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"></td>');//演员
		if(dataMap.mainRoleList[i].viewRoleName == null){
			dataMap.mainRoleList[i].viewRoleName = "";
		}
		html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">' + dataMap.mainRoleList[i].viewRoleName + '</p></td>');
	}
	
	
	var guestWidth = multiply(divide(maxGuestLength, otherWidth), residualWidth);//特约列所占宽度；
	basicTr.push('<td  style="width: '+ guestWidth  +'px; max-width: '+ guestWidth +'px;"></td>');
	guestStyle = 'style="width: '+ guestWidth +'px; max-width: ' + guestWidth + 'px;"';
	
	var massWidth = multiply(divide(maxMassLength, otherWidth), residualWidth); //群演列所占宽度；
	basicTr.push('<td style="width: '+ massWidth  +'px; max-width: '+ massWidth +'px;"></td>');
	massStyle = 'style="width: '+ massWidth +'px; max-width: ' + massWidth + 'px;"';
	
	var clothProLength = multiply(divide(maxClothProLength, otherWidth), residualWidth); //服化道所占宽度
	basicTr.push('<td style="width: '+ clothProLength  +'px; max-width: '+ clothProLength +'px;"></td>');
	clothProStyle = 'style="width: '+ clothProLength +'px; max-width: ' + clothProLength + 'px;"';
	
	var remarkLength = multiply(divide(maxRemarkLength, otherWidth), residualWidth); //备注所占宽度
	basicTr.push('<td style="width: '+ remarkLength  +'px; max-width: '+ remarkLength +'px;"></td>');
	clothProStyle = 'style="width: '+ remarkLength +'px; max-width: ' + clothProLength + 'px;"';
	
	
	html.push('<td class="font-size-style" style="text-align: left; vertical-align: top;" colspan="4">');
	//html.push(' <span class="td-tips" style="width: 15px; border: 0px;"><span style="border-right: 1px solid #000; border-bottom:1px solid #000; height: 30px;display: inline-block;">商植</span></span>');
	html.push('<textarea class="textarea-height" readonly style="width: calc(100% - 20px); rows="" cols="">' + dataMap['myNoticeTime'].insideAdvert+'</textarea>');
	html.push('</td>');
	html.push('</tr>');
	basicTr.push('</tr>');
	$("#noticeTable").append(basicTr.join(""));
	
	//第三行
	html.push('<tr>');
	html.push(' <td style="width: 25px; min-width: 25px; max-width:25px;"><p style="font-size: 1em; border: 0px;">演员</p></td>');
	for(var i= 0; i< dataMap.mainRoleList.length; i++){
		if(dataMap.mainRoleList[i].actorName == null){
				dataMap.mainRoleList[i].actorName = "";
		}
		html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">' + dataMap.mainRoleList[i].actorName + '</p></td>');
	}
	html.push('<td class="font-size-style" style="text-align: left; vertical-align: top;" colspan="4" rowspan="3">');
	html.push('<textarea class="textarea-height" readonly>'+ dataMap['myNoticeTime'].roleInfo + '</textarea>');
	html.push("</td>");
	html.push('</tr>');
	
	//第四行
	html.push('<tr>');
	html.push(' <td class="font-size-style" style="text-align: left;" colspan="6">');
	html.push('<span class="td-tips" style="width:15px; "><span>提示</span></span>');
	html.push('<textarea class="textarea-height" style="width: calc(100% - 20px);" readonly rows="" cols="">'+ dataMap['myNoticeTime'].note + '</textarea>');
	html.push(' </td>');
	html.push('<td  style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">化妆地</p></td>');
	for(var i= 0; i< dataMap.mainRoleList.length; i++){
		if(dataMap.mainRoleList[i].makeup == null){
			dataMap.mainRoleList[i].makeup = "";
		}
		html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">' + dataMap.mainRoleList[i].makeup + '</p></td>');
	}
	
	
	html.push('</tr>');
	
	
	//第五行
	html.push('<tr>');
	html.push(' <td class="font-size-style" style="text-align: left; vertical-align: top;" colspan="6">');
	html.push('  <span class="td-tips" style="width:15px;"><span>备注</span></span>');
	html.push('  <textarea class="textarea-height" readonly rows="" cols="" style="width: calc(100% - 20px);">' + dataMap['myNoticeTime'].remark + '</textarea>');
	html.push(' </td>');
	html.push(' <td style="width: 25px; min-width: 25px; max-width:25px; text-combine-horizontal;"><p style="font-size: 1em; border: 0px;">化妆</p></td>');
	for(var i= 0; i< dataMap.mainRoleList.length; i++){
		if(dataMap.mainRoleList[i].arriveTime == null){
			dataMap.mainRoleList[i].arriveTime = "";
		}
		html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><div class="role-time">' + dataMap.mainRoleList[i].arriveTime + '</div></td>');
	}
	html.push('</tr>');
	//第六行
	html.push('<tr>');
	html.push('<td '+ specialStyle +' class="font-size-style">特别</td>');
	
	if (crewType == 0 || crewType == 3) { //电影/网大 剧本
		html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">场次</td>');
	}else {
		html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">集-场</td>');
	}
	html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">气氛</td>');
	html.push('<td class="font-size-style" style="width: 32px; min-width: 32px; max-width: 32px;">页数</td>');
	
	html.push('<td ' + viewStyle + ' class="font-size-style">场景</td>');
	html.push('<td ' + contentStyle + ' class="font-size-style">内容提要</td>');
	
	html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">出发</p></td>');
	for(var i= 0; i< dataMap.mainRoleList.length; i++){
		if(dataMap.mainRoleList[i].giveMakeupTime == null){
			dataMap.mainRoleList[i].giveMakeupTime = "";
		}
		html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><div class="role-time">' + dataMap.mainRoleList[i].giveMakeupTime + '</div></td>');
	}
	html.push('<td '+ guestStyle +' class="font-size-style">特约</td>');
	html.push('<td '+ massStyle +' class="font-size-style">群演</td>');
	html.push('<td '+ clothProStyle +' class="font-size-style">服化道</td>');
	html.push('<td '+ remarkStyle +' class="font-size-style">备注</td>');
	html.push('</tr>');
	
	
	
	
	
	
	
	//第六行---最后
	var locationGroupList = dataMap['locationGroupList'];
	if(locationGroupList.length != 0){
		for(var i = 0; i < dataMap.locationGroupList.length; i++){
			var locationViewMap = locationGroupList[i];
			//检验值是否为null
			if(locationViewMap['location'] == null){
				locationViewMap['location'] = "";
			}
			
			
			if(locationViewMap['location'] == ""){
				html.push('<tr class="change-tr">');
				html.push('<td style="text-align: left;" colspan="5">'+ locationViewMap['location'] +'</td>');
				var colWidth = subtract(cellsCount, 5);
				if(locationViewMap['convertAddressInfo'] == undefined){
					html.push('<td style="text-align:left;" colspan="'+ colWidth +'"></td>');
				}else{
					html.push('<td style="text-align:left;" colspan="'+ colWidth +'">'+ locationViewMap['convertAddressInfo'] +'</td>');
				}
				
				html.push('</tr>');
			}else{
				html.push('<tr class="change-tr">');
				html.push('<td class="font-size-style" style="text-align: left;" colspan="5">'+ locationViewMap['location'] +'</td>');
				var colWidth = subtract(cellsCount, 5);
				if(locationViewMap['convertAddressInfo'] == undefined){
					html.push('<td class="font-size-style" style="text-align:left;" colspan="'+ colWidth +'"></td>');
				}else{
					html.push('<td class="font-size-style" style="text-align:left;" colspan="'+ colWidth +'">'+ locationViewMap['convertAddressInfo'] +'</td>');
				}
				html.push('</tr>');
			}
			
			
			
			var viewList = dataMap.locationGroupList[i].viewList;
			if(viewList.length != 0){
				for(var j = 0; j < viewList.length; j++){
					
					//检验值是否为null
					if(viewList[j].advertId == null){
						viewList[j].advertId = "";
					} 
					if(viewList[j].advertName == null){
						viewList[j].advertName = "";
					}
					if(viewList[j].atmosphere == null){
						viewList[j].atmosphere = "";
					}
					if(viewList[j].atmosphereId == null){
						viewList[j].atmosphereId = "";
					}
					if(viewList[j].atmosphereName == null){
						viewList[j].atmosphereName = "";
					}
					if(viewList[j].clothesId == null){
						viewList[j].clothesId = "";
					}
					if(viewList[j].clothesName == null){
						viewList[j].clothesName = "";
					}
					if(viewList[j].guestRoleList == null){
						viewList[j].guestRoleList = "";
					}
					if(viewList[j].mainContent == null){
						viewList[j].mainContent = "";
					}
					if(viewList[j].majorView == null){
						viewList[j].majorView = "";
					}
					if(viewList[j].makeupId == null){
						viewList[j].makeupId = "";
					}
					if(viewList[j].makeupName == null){
						viewList[j].makeupName = "";
					}
					if(viewList[j].massRoleList == null){
						viewList[j].massRoleList = "";
					}
					if(viewList[j].minorView == null){
						viewList[j].minorView = "";
					}
					if(viewList[j].pageCount == null){
						viewList[j].pageCount = "";
					}
					if(viewList[j].propsList == null){
						viewList[j].propsList = "";
					}
					if(viewList[j].remark == null){
						viewList[j].remark = "";
					}
					if(viewList[j].roleShortNames == null){
						viewList[j].roleShortNames = "";
					}
					if(viewList[j].specialRemind == null){
						viewList[j].specialRemind = "";
					}
					if(viewList[j].seriesNo == null){
						viewList[j].seriesNo = "";
					}
					if(viewList[j].shootDate == null){
						viewList[j].shootDate = "";
					}
					if(viewList[j].shootLocation == null){
						viewList[j].shootLocation = "";
					}
					if(viewList[j].shootLocationId == null){
						viewList[j].shootLocationId = "";
					}
					if(viewList[j].shootStatus == null){
						viewList[j].shootStatus = "";
					}
					if(viewList[j].site == null){
						viewList[j].site = "";
					}
					if(viewList[j].specialPropsList == null){
						viewList[j].specialPropsList = "";
					}
					if(viewList[j].tapNo == null){
						viewList[j].tapNo = "";
					}
					if(viewList[j].thirdLevelView == null){
						viewList[j].thirdLevelView = "";
					}
					if(viewList[j].viewAddress == null){
						viewList[j].viewAddress = "";
					}
					if(viewList[j].viewAddressId == null){
						viewList[j].viewAddressId = "";
					}
					if(viewList[j].viewCount == null){
						viewList[j].viewCount = "";
					}
					if(viewList[j].viewId == null){
						viewList[j].viewId = "";
					}
					if(viewList[j].viewNo == null){
						viewList[j].viewNo = "";
					}
					if(viewList[j].viewRemark == null){
						viewList[j].viewRemark = "";
					}
					var prepareStatusStr = '';
					 if (viewList[j].prepareStatus == 1) {
						 prepareStatusStr = '备';
					}
					html.push('<tr class="change-tr">');
					if(viewList[j].specialPropsList == ""){
						if(viewList[j].specialRemind == ""){
							if (prepareStatusStr == '') {
								html.push(' <td '+ specialStyle +' class="font-size-style"></td>');//特别值
							}else {
								html.push(' <td '+ specialStyle +' class="font-size-style">' + prepareStatusStr + '</td>');
							}
						}else{
							if (prepareStatusStr == '') {
								html.push(' <td '+ specialStyle +' class="font-size-style">' + viewList[j].specialRemind + '</td>');;//特别值
							}else {
								html.push(' <td '+ specialStyle +' class="font-size-style">' + prepareStatusStr + " |"+ viewList[j].specialRemind +'</td>');
							}
						}
					}else{
						if(viewList[j].specialRemind == ""){
							if (prepareStatusStr == '') {
								html.push(' <td '+ specialStyle +' class="font-size-style">'+ viewList[j].specialPropsList +'</td>');//特别值
							}else {
								html.push(' <td '+ specialStyle +' class="font-size-style">' + prepareStatusStr + " |" + viewList[j].specialPropsList +'</td>');
							}
						}else{
							if (prepareStatusStr == '') {
								html.push(' <td '+ specialStyle +' class="font-size-style">'+ viewList[j].specialPropsList +'|' + viewList[j].specialRemind + '</td>');
							}else {
								html.push(' <td '+ specialStyle +' class="font-size-style">' + prepareStatusStr + " |" + viewList[j].specialPropsList + '|' + viewList[j].specialRemind +'</td>');
							}
						}
					}
					
					//集场号
					if (crewType == 0 || crewType == 3) { //电影
						html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">'+ viewList[j].viewNo +'</td>');
					}else { //电视剧
						html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">'+ viewList[j].seriesNo + '-' + viewList[j].viewNo +'</td>');
					}
					/*if(viewList[j].seriesNo == ""){
						if(viewList[j].viewNo == ""){
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;"></td>');
						}else{
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">'+ viewList[j].viewNo +'</td>');
						}
					}else{
						if(viewList[j].viewNo == ""){
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">'+ viewList[j].seriesNo +'</td>');
						}else{
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width: 50px;">'+ viewList[j].seriesNo + '-' + viewList[j].viewNo +'</td>');
						}
					}*/
					
					
					if(viewList[j].site == ""){//气氛
						if(viewList[j].atmosphere == ""){
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width:50px;"></td>');
						}else{
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width:50px;">'+ viewList[j].atmosphere+ '</td>');
						}
					}else{
						if(viewList[j].atmosphere == ""){
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width:50px;">'+ viewList[j].site +'</td>');
						}else{
							html.push('<td class="font-size-style" style="width: 50px; min-width: 50px; max-width:50px;">'+ viewList[j].site + '/' + viewList[j].atmosphere+ '</td>');
						}
						
					}
					
					html.push('<td class="font-size-style" style="width: 32px; min-width: 32px; max-width: 32px;">' + viewList[j].pageCount + '</td>');//页数
					
					if (viewList[j].majorView == "") {//场景
						if (viewList[j].minorView == "") {
							if (viewList[j].thirdLevelView == "") {
								html.push('<td '+ viewStyle +' class="font-size-style"></td>');
							}else {
								html.push('<td ' + thirdLevelView +' class="font-size-style"></td>');
							}
						}else {
							if (viewList[j].thirdLevelView == "") {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].minorView +'</td>');
							}else {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].minorView +'-'+ viewList[j].thirdLevelView +'</td>');
							}
						}
					}else {
						if (viewList[j].minorView == "") {
							if (viewList[j].thirdLevelView == "") {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].majorView +'</td>');
							}else {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].minorView + '-' + viewList[j].thirdLevelView +'</td>');
							}
						}else {
							if (viewList[j].thirdLevelView == '') {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].majorView + '-' + viewList[j].minorView +'</td>');
							}else {
								html.push('<td '+ viewStyle +' class="font-size-style">'+ viewList[j].majorView +'-' + viewList[j].minorView +'-' + viewList[j].thirdLevelView +'</td>');
							}
						}
					}
					
					html.push('<td '+ contentStyle +' class="font-size-style">'+ viewList[j].mainContent +'</td>');//内容提要
					html.push('<td style="width: 25px; min-width: 25px; max-width:25px;"></td>');
					
					var roleList = viewList[j].roleList;
					for(var k = 0; k < roleList.length; k++){
						if(roleList[k].shortName == null){
							roleList[k].shortName = "";
						}
						html.push('<td style="width: 25px; min-width: 25px; max-width: 25px;"><p style="font-size: 1em; border: 0px;">'+ roleList[k].shortName +'</p></td>');//演员简称
					}
					
					html.push('<td '+ guestStyle +' class="font-size-style">'+ viewList[j].guestRoleList +'</td>');
					html.push('<td '+ massStyle +' class="font-size-style">'+ viewList[j].massRoleList +'</td>');
					if (viewList[j].clothesName == "") {
						if (viewList[j].makeupName == "") {
							if (viewList[j].propsList == "") {
								html.push('<td '+ clothProStyle +' class="font-size-style"></td>');
							}else {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].propsList +'</td>');
							}
						}else {
							if (viewList[j].propsList == '') {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].makeupName +'</td>');
							}else {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+viewList[j].makeupName +'|'+ viewList[j].propsList +'</td>');
							}
						}
					}else {
						if (viewList[j].makeupName == '') {
							if (viewList[j].propsList =='') {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].clothesName +'</td>');
							}else {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].clothesName + '|' + viewList[j].propsList +'</td>');
							}
						}else {
							if (viewList[j].propsList =='') {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].clothesName + '|' + viewList[j].makeupName +'</td>');
							}else {
								html.push('<td '+ clothProStyle +' class="font-size-style">'+ viewList[j].clothesName + '|' + viewList[j].makeupName + '|' + viewList[j].propsList +'</td>');
							}
						}
					}
					
					html.push('<td '+ remarkStyle +' class="font-size-style">'+ viewList[j].viewRemark +'</td>');
					html.push('</tr>');
				}
			}
		} 
	}
	
	//最后一行提示
	html.push('<tr>');
	html.push('<td class="font-size-style" colspan="'+ cellsCount +'">');
	html.push(' <span class="td-tips" style="width: 30px;"><span>其他提示</span></span>');
	html.push(' <textarea class="textarea-height" style="width: calc(100% - 135px);" readonly>' + dataMap['myNoticeTime'].roleConvertRemark + '</textarea>');
	if(dataMap['myNoticeTime'].updateTime == null){
		var date = new Date();
		html.push(' <div class="make-notice-time"><p>' + getCurrDateFormat(date) + '</p><p>' + getCurrTime(date) + '</p></div>');
		
	}else{
		var makeTableTime = new Date(dataMap['myNoticeTime'].updateTime);
		html.push(' <div class="make-notice-time"><p>' + getCurrDateFormat(makeTableTime) + '</p><p>' + getCurrTime(makeTableTime) + '</p></div>');
	}
	
	html.push('</td>');
	html.push('</tr>');
	
	
	//最后要执行的
	$("#noticeTable").append(html.join(""));
	
	var tableWidth = $("#noticeTable").width();
	$("#tableHeaderDiv").css({"width": tableWidth});
//	$("#suspensionFrame").hide();
//	window.print();
}



function addRowHeight(){
	var rowHeightValue = $("#rowHeightValue").val()-0;
	if(rowHeightValue == 30){
		return;
	}else{
		rowHeightValue = add(rowHeightValue, 1);
		$(".change-tr").each(function(){
			$(this).css({"height": rowHeightValue});
		});
		$("#rowHeightValue").val(rowHeightValue);
	}
	
}


function subRowHeight(){
	var rowHeightValue = $("#rowHeightValue").val()-0;
	if(rowHeightValue == 17){
		return;
	}else{
		rowHeightValue = subtract(rowHeightValue, 1);
		$(".change-tr").each(function(){
			$(this).css({"height": rowHeightValue});
		});
		$("#rowHeightValue").val(rowHeightValue);
	}
}

//字体大小
function addFontSize(){
	var tdFontSize = $("#fontSizeValueTD").val()-0;
	var textFontSize = $("#fontSizeValueText").val()-0;
	tdFontSize = add(tdFontSize, 0.01);
	$(".font-size-style").each(function(){
		
		
		$(this).css({"font-size": tdFontSize+'em'});
	});
	$("#fontSizeValueTD").val(tdFontSize);
	/*textFontSize = add(textFontSize, 0.01);*/
	$(".textarea-height").each(function(){
		$(this).css({"font-size": '1em'});
		var height =this.scrollHeight+'px';
		$(this).css({"height": height});
		var tdHeight = $(this).parents("td").height();
		$(this).prev("span.td-tips").css({"height": tdHeight+1});
		$(this).next("div.make-notice-time").css({"height": tdHeight+1});
		//设置提示信息居中
		var content = $(this).prev("span.td-tips");
		var contentHeight = content.find("span").eq(0).height();
		var top = subtract(divide(tdHeight, 2), divide(contentHeight,2));
		content.find("span").css({"top": top+"px"});
	});
	$("#fontSizeValueText").val(1);
}
function subFontSize(){
	var tdFontSize = $("#fontSizeValueTD").val()-0;
	var textFontSize = $("#fontSizeValueText").val()-0;
	tdFontSize = subtract(tdFontSize, 0.01);
    $(".font-size-style").each(function(){
		$(this).css({"font-size": tdFontSize+'em'});
		
	});
    $("#fontSizeValueTD").val(tdFontSize);
    /*textFontSize = subtract(textFontSize, 0.01);*/
	$(".textarea-height").each(function(){
		
		$(this).css({"font-size": '1em'});
		var height =this.scrollHeight+'px';
		$(this).css({"height": height});
		var tdHeight = $(this).parents("td").height();
		$(this).prev("span.td-tips").css({"height": tdHeight+1});
		$(this).next("div.make-notice-time").css({"height": tdHeight+1});
		//设置提示信息居中
		var content = $(this).prev("span.td-tips");
		var contentHeight = content.find("span").eq(0).height();
		var top = subtract(divide(tdHeight, 2), divide(contentHeight,2));
		content.find("span").css({"top": top+"px"});
	});
	$("#fontSizeValueText").val(1);
}




//重置按钮组位置
function resizeButtonPosition(){
	//取出table的宽度
	var tableWidth = $("#noticeTable").width();
	//取出table的宽度
	var tableHeight = $("#noticeTable").height();
	//取出页面宽度
	var windowWidth = document.body.clientWidth;
	// 取出按钮组对象
	var $suspensionFrame = $("#suspensionFrame");
	//计算出按钮组的位置
	var leftDis = (windowWidth - tableWidth)/2;
	$suspensionFrame.css("left", leftDis + tableWidth + 10);
	$suspensionFrame.css("top", tableHeight - 130);
	
	$("#borderSign").css("left", divide(leftDis, 2));
}

//初始化大小插件
//function initSmartspinner(){
//	$("#spinner").spinner('changing', function(e, newVal, oldVal) {
//	    $('#spinner-value').html(newVal);
//	    console.log($('#spinner-value').text());
//	  });
//}
//function addRoweHeight(){
//}
//function subRowHeight(){
//	var value = $("#rowHeight").val();
//	console.log(value);
//}
//
//function adjustment(own){
//	var $this = $(own);
//	if($this.hasClass("already-adjust")){
//		$("#adjustRowHeight").hide();
//		$this.removeClass("already-adjust");
//		return;
//	}
//	var frameLeft = $("#suspensionFrame").position().left;
//	var frameTop = $("#suspensionFrame").position().top;
//	$("#adjustRowHeight").css({"left": frameLeft+80, "top": frameTop}).show();
//	$this.addClass("already-adjust");
//}
//
//function hideAdjustDiv(own){
//	
//}



var theme = "ui-lightness";
var noticeId = $("#noticeId").val();
var slide;
var count;


//初始化发布弹窗
function initPublicWin(){
	if(dataMap['myNoticeTime'].version == null){
		$("#noticeTitle").val(dataMap.noticeName);
		$("#noticeContent").html("《" + dataMap.crewName + "》" + dataMap.noticeDataDay + dataMap.groupName + "通告  已发布，请查收。");
	}else{
		$("#noticeTitle").val(dataMap.noticeName+"("+dataMap['myNoticeTime'].version+")");
		$("#noticeContent").html("《" + dataMap.crewName +"》" + dataMap.noticeDataDay + dataMap.groupName+ "通告（" + dataMap['myNoticeTime'].version +"）已发布，请查收。");
	}
	
	$("#selectRoleWindow").jqxWindow({
        theme: theme,
        width: 990,
        height: 680,
        maxHeight: 2000,
        maxWidth: 2000,
        modalZIndex: 1000,
        resizable: false,
        autoOpen: false,
        title: "消息推送",
        cancelButton: $("#cancel"),
        isModal: true,
        initContent: function() {
            //slide = initThumbnail([]);
        
            var imgArray = new Array();
            //加载通告单图片
            var noticePicCount = pictureCount;
            
            for(var i= 0; i< noticePictureList.length; i++){
            	var noticePicture = noticePictureList[i];
                imgArray[i]= {
                	src: noticePicture.bigPicurl,
                	title: noticePicture.name,
                	id: noticePicture.id
                };
            }
            
	        slide = initThumbnail(imgArray);
	        
	        if (noticePicCount == 0) {
	        	//将滚动条滚动到最顶端
	       	 	document.getElementsByTagName("body")[0].scrollTop = 0;
	       	 
	          generateNoticeImg(slide);
	        }
	        
	        //加载待发送人列表
	        var userGrid = new UserListGrid("toSelectUserListGrid", "selectedUserTagListUl");
	        userGrid.loadTable();
	        
            $("#publish").on("click", function() {
                var $this = $(this);
                var noticeTitle = $("#noticeTitle").val();
                var noticeContent = $("#noticeContent").val();
                var noticeId = $("#noticeId").val();
                var userIds = userGrid.getSelectedIds();
                var isAll = userGrid.isCheckAll();
                var needFeedback = false;
                
                if (noticeTitle == "") {
                    showErrorMessage("错误提示：请填写标题");
                    return false;
                }
                if (noticeContent == "") {
                    showErrorMessage("错误提示：请填写内容");
                    return false;
                }
                if ($("#needFedback").prop("checked")) {
                    needFeedback = true;
                }
                
                //获取推送状态
                var pushChecked = $("#pushTips").prop("checked");
                
                //推送期间发布按钮禁用
                $this.attr("disabled", true);
			    //目前先发送通告单给剧组下的所有人
			    $.ajax({
			        url:"/notice/publishNotice",
			        type:"post",
			        data:{noticeTitle: noticeTitle, noticeContent:noticeContent, noticeId: noticeId, 
			        	userIds:userIds, isAll:isAll, needFeedback:needFeedback, publishNotice: pushChecked},
			        async:true,
			        success: function(param) {
			            if (param.success) {
			                showSuccessMessage("发布成功");
			                $("#selectRoleWindow").jqxWindow("close");
			                window.opener.location.reload();
			                if (isAll == 1) {
			                 published = "true";
			                }
			            } else {
			                showErrorMessage(param.message);
			            }
			            $this.attr("disabled", false);
			        }
			    });
            });
            
		    //已选列表中删除符号的行为
		    $("#selectedUserTagListUl").on("click", ".closeTag", function(ev) {
		        $(this).parent("li").remove();
		        userGrid.unCheckItem($(this).parent("li").attr("id"));
		        
		        if ($(this).parent("li").attr("id")=="all") {
		          userGrid.unCheckAll();
		        }
		    });
        }
    });
}


//初始化消息窗口和图片上传
function initMessageAndUpLoader(){
	//加载消息窗口
    $("#jqxNotification").jqxNotification({
        width: "auto", 
        position: "custom", 
        opacity: 0.9,
        browserBoundsOffset: 250,
        notificationOffset: 500,
        autoOpen: false, 
        animationOpenDelay: 800, 
        autoClose: true, 
        closeOnClick: true
    });
    
    //=======================通告单图片上传start===================================
    uploader = WebUploader.create({
        //文件选择器
        pick: "#uploadImg",
        //拖拽的容器
        dnd: '#thumbnail',
        disableGlobalDnd: true,
        //截图粘贴的容器
        paste: '#thumbnail',
        //配置生成缩略图的选项。
        thumb: {
            width: 110,
            height: 110,
        
            // 图片质量，只有type为`image/jpeg`的时候才有效。
            quality: 70,
        
            // 是否允许放大，如果想要生成小图的时候不失真，此选项应该设置为false.
            allowMagnify: true,
        
            // 是否允许裁剪。
            crop: false,
        
            // 为空的话则保留原有图片格式。
            // 否则强制转换成指定的类型。
            type: 'image/jpeg'
        },
        //配置压缩的图片的选项
        compress: false,
        // 不压缩image
        resize: false,
        //是否选择文件后自动上传文件
        auto: true,
        //上传并发数
        threads: 1,

        // swf文件路径
        //swf: BASE_URL + '/js/Uploader.swf',

        // 文件接收服务端。
        server: "/notice/uploadNoticeImg",

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        //pick: '#selectFileBtn',
        formData: {noticeId: $("#noticeId").val()},
        threads: 1,
        fileNumLimit: 10,
        
        // 只允许选择图片文件，可选。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/jpg,image/jpeg,image/png'
        }
    });
    // 文件上传成功
    uploader.on('uploadSuccess', function(file, response) {
        // 创建缩略图
        if (response.success) {
	        showNoticeImg(slide, response.imgId, response.storePath, response.smallImgPath, response.pictureCount-1, response.imgName);
        }
        uploader.removeFile(file, true);
    });
    //=======================通告单图片上传end===================================
}


//缩略图初始化
function initThumbnail(imgArray) {
    var $thumbnail = $("#thumbnail"),
	    $list = $thumbnail.find('.thumbnail-list'),
	    space = 520;
	    
    count = imgArray.length;
    function initPage(){
        var len = imgArray.length,
            html = [];
        for(var i = 0; i < len; i++){
            var img = imgArray[i];
            html.push('<li class="thumbnail-item" id="'+ img.id +'"><button class="thumbnail-btn btn-close"></button><img src="'+ img.src +'" alt="'+ img.title +'"/></li>');
        }
        $list.append(html.join(""));

        if(len <= 2){
            $thumbnail.find('.btn-next').addClass('disable');
        }
    }
    var slide = new Slide({
        imgArray: imgArray,
        duration: 0.5,
        deleteCallback: function(deletedIndex, deletedImg){

            var imgId = deletedImg.id;
            deleteNoticeImg(imgId);
            
            var img = $list.find("#"+ deletedImg.id);
            if(img != undefined && img.length > 0) {
        
                var $li = img;
		        //var index = deletedIndex;
		        // 在小slide里删除效果
		        $li.remove();
		        count--;
		        $thumbnail.find('.btn-prev').addClass('disable');
                $list.css("transform", "translate3d(0, 0, 0)");
		        if(count <= 2){
		          $thumbnail.find('.btn-next').addClass('disable');
		        }else{
		          $thumbnail.find('.btn-next').removeClass('disable');
		        }
            }
        }
    });

    $(window).resize(function(){
        if(slide.$container.css("display") == "block")
            slide.resize();
    });

    $thumbnail.on('click', '.thumbnail-item > img', function(evt){
        slide.open($(this).parent().index());
    }).on('click', '.btn-close', function(){
        var $li = $(this).parent();
        var index = $li.index();
        // 在小slide里删除效果
        $li.remove();
        count--;
        if((count % 2) == 0){
            if(count == index) {
                $thumbnail.find('.btn-next').addClass('disable');
                $list.css("transform", "translate3d(" + -(space * (index / 2 - 1)) +"px, 0, 0)");
            }else if(Math.ceil((index + 1) / 2) == parseInt(count / 2)){
                //$thumbnail.find('.btn-prev').click();
                $thumbnail.find('.btn-next').addClass('disable');
            }

            if(count == 2) {
                $thumbnail.find('.btn-prev').addClass('disable');
            }
        }
        
        slide.deleteImg(index);
    }).on('click', '.btn-prev:not(.disable)', function(){
        var index = -1, transform = parseInt($list.css("transform").split(",")[4]) || 0;

        if(transform){
            index = Math.ceil(parseFloat(transform / space)) + 1;
        }

        $list.css("transform", "translate3d(" + (space * index) +"px, 0, 0)");
        $thumbnail.find('.btn-next').removeClass('disable');
        if(index >= 0){
            this.className += " disable";
        }
    }).on('click', '.btn-next:not(.disable)', function(){
        var index = -1, transform = parseInt($list.css("transform").split(",")[4]) || 0;

        if(transform){
            index = Math.ceil(parseFloat(transform / space)) - 1;
        }
        $list.css("transform", "translate3d(" + (space * index) +"px, 0, 0)");
        $thumbnail.find('.btn-prev').removeClass('disable');
        if(-(index - 1) * 2 >= count){
            this.className += " disable";
        }
    });

    // 初始化页面
    initPage(imgArray);
    
    return slide;
} 


//显示图片
function showNoticeImg(slide, imgId, bigUrl, smallUrl, index, name) {
    var isFirst = false; //是否是向第一个位置添加图片
    var $thumbnail = $("#thumbnail");
    var $list = $thumbnail.find('.thumbnail-list');
    var img = {
        src: bigUrl,
        title: name,
        id: imgId
    };
    
    slide && slide.addImage(img, isFirst);

    if (isFirst) {
        $list.prepend('<li class="thumbnail-item" id="'+ imgId +'"><button class="thumbnail-btn btn-close"></button><img src="'+ img.src +'" alt="'+ img.title +'"/></li>');
	    $list.css("transform", "translate3d(0, 0, 0)");
	    
	    count += 1;
	    $thumbnail.find('.btn-prev').addClass('disable');
	    if(count > 2){
	        $thumbnail.find('.btn-next').removeClass('disable');
	    }
    } else {
        $list.append('<li class="thumbnail-item" id="'+ imgId +'"><button class="thumbnail-btn btn-close"></button><img src="'+ img.src +'" alt="'+ img.title +'"/></li>');
        
        var imgArray = slide.getImgArray();
        var myIndex = -1;
        var space = 520;
        
        myIndex = -Math.ceil(parseFloat(imgArray.length / 2)) + 1;
        
        $list.css("transform", "translate3d(" + (space * myIndex) +"px, 0, 0)");
        
        count += 1;
        $thumbnail.find('.btn-next').addClass('disable');
        if(count > 2){
            $thumbnail.find('.btn-prev').removeClass('disable');
        }
        
    }
}



//删除通告单图片
function deleteNoticeImg(imgId) {

    $.ajax({
        url: "/notice/deleteNoticeImg",
        data: {imgId: imgId},
        type: "post",
        async: true,
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
            }
        }
    });
}



function printNotice(){
    $("#suspensionFrame").hide();
    $("#borderSign").hide();
    window.print();
    $("#suspensionFrame").show();
    $("#borderSign").show();
    $(".noticeInfo").css("margin-top", "20px");
}


function selectRole() {
    $("#selectRoleWindow").jqxWindow("open");
}

function backToEdit() {
	//var noticeId = $("#noticeId").val();
    window.close();
    window.location.reload();
}


/**
 * 自动根据当前网页生成图片，并保存
 **/
function generateNoticeImg(slide){
	//设置图片大小
	 var width = $("#noticeTable").width();
	 
	 $("#tableContainer").css("width", width+10);
    	html2canvas($("#tableContainer") ,{
        onrendered: function(canvas){
            //生成base64图片数据
            var dataUrl = canvas.toDataURL();
            var base64Str = dataUrl.split("base64,")[1];
            $.ajax({
		        url:"/notice/uploatNoticeImgBase64",
		        async: false,
		        dataType:"json",
		        data:{base64Str: base64Str,noticeId: $("#noticeId").val()},
		        type:"post",
		        success: function(data) {
		          if (!data.success) {
		              showErrorMessage("自动生成图片失败");
		          } else {
                      showNoticeImg(slide, data.imgId, data.storePath, data.smallImgPath, 0, data.imgName);
                      $("#tableContainer").css("width", "");
		          }
		        }
		    });
        },
    });
}


/**
 *  导出通告单
 */
function downloadNotice(){
	var noticeId = $("#noticeId").val();
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
   		 url:"/notice/exportNotice",
            data: {noticeId: $("#noticeId").val() },
            dataType:"json",
            type:"post",
            success:function(response){
           	 if (response.success) {
           		 _LoadingHtml.hide();
           		 $(".opacityAll").hide();
           		fileAddress = response.downloadPath;
   			}else{
   	        	   showErrorMessage(response.message);
   	               return;
   	           }
           	 
           	 var form = $("<form></form>");
                form.attr("action","/fileManager/downloadFileByAddr");
                form.attr("method","post");
                form.append("<input type='hidden' name='address'>");
                form.append("<input type='hidden' name='fileName'>");
                form.find("input[name='address']").val(fileAddress);
                $("body").append(form);
                form.submit();
                form.remove();
            }
   	});
}