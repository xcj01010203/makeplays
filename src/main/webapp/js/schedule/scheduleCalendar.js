var filter = {};
var colorArray = ["#50a2e6", "#2db3a5", "#e6aa50", "#8abf4c", "#5967b3", "#b473bf", "#3dbbcc", "#8d6e63", "#7c98a6", "#e67373"];
var activeThing = [];
var activeId = [];
$(function(){
	if(isScheduleReadonly){
		$("#editedPlanTab").hide();
	}
	//初始化添加关注窗口
	initAddActiveWin();
	//查询保存的关注条件
	querySaveCondition();
	
	$("#addActiveWin").on("open", function(){
		queryAttenCondition();
	});
});



//查询保存的关注条件
function querySaveCondition(){
	$.ajax({
		url: '/cacheManager/queryCacheInfo',
		type: 'post',
		data: {"type": 2},
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.result != null){
					filter.attention = response.result.content;
				}else{
					createPlanViewHead();//显示表头
				}
				//查询关注内容信息
				queryActiveContent();
			}else{
				parent.showErrorMesage(response.message);
			}
		}
	});
}


//初始化添加关注窗口
function initAddActiveWin(){
	$("#addActiveWin").jqxWindow({
		width: 700,
		height: 650, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
		initContent: function(){
		}
	});
}

//查询已关注的信息
function queryAttenCondition(){
	var html = [];
	activeId = [];
	if(activeThing.length != 0){
		for(var i=0; i< activeThing.length; i++){
			var color = colorArray[i];
			html.push('<li class="active-option" data-color="'+ color +'" data-type="'+ activeThing[i].type +'" itemid="'+ activeThing[i].id +'" style="background:'+ color +';" onclick="selectActiveOption(this)">');
			html.push('<span class="active-option-name">'+ activeThing[i].name +'</span>');		                         
			html.push('<input class="delete-btn" type="button" value="x" onclick="deleteActiveOption(this)">');		                         
			html.push('</li>');
			activeId.push(activeThing[i].id);
		}
		
	}
//	html.push('<li>');
//	html.push('<input class="add-active-option" id="addActiveOptionBtn" type="button" title="添加" onclick="addActiveOption(this)">');
//	html.push('</li>');
    $("#alreadyActiveList").empty();
    $("#alreadyActiveList").append(html.join(""));
  //查询演员、道具、场景列表
    queryNeedData();
}


//查询演员、道具、场景列表
function queryNeedData(){
	$.ajax({
		url: '/scheduleManager/queryAttentionInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var viewRoleList = response.viewRoleList;
				var specialPropList = response.specialPropList;
				var locationList = response.locationList;
				createAttentionInfo(viewRoleList, specialPropList, locationList);
			}
		}
	});
}

//加载关注项
function createAttentionInfo(viewRoleList, specialPropList, locationList){
	if(viewRoleList.length != 0 && viewRoleList != null){
		var _html = [];
		var html = ['<input type="button" class="coll-expand hide" onclick="collOrExpandItem(this)">'];
		var htmlp = [];
		htmlp.push('<p>');
		var htmlUl = ['<ul class="drop-list">'];
		for(var i= 0; i< viewRoleList.length; i++){
			if(i < 15){
				if(jQuery.inArray(viewRoleList[i].viewRoleId, activeId) == -1){
					htmlp.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="1" itemid="'+ viewRoleList[i].viewRoleId +'" title="'+ viewRoleList[i].viewRoleName +'">'+ viewRoleList[i].viewRoleName +'</span>');
				}else{
					htmlp.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="1" itemid="'+ viewRoleList[i].viewRoleId +'" title="'+ viewRoleList[i].viewRoleName +'">'+ viewRoleList[i].viewRoleName +'</span>');
				}
				
				if(i % 5 == 4){
					htmlp.push('</p>');
					html = html.concat(htmlp);
					htmlp = ['<p>'];
				}
				if(i == (viewRoleList.length -1)){
					htmlp.push('</p>');
					html = html.concat(htmlp);
				}
			}else{
				if(jQuery.inArray(viewRoleList[i].viewRoleId, activeId) == -1){
					htmlUl.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="1" itemid="'+ viewRoleList[i].viewRoleId+'" title="'+ viewRoleList[i].viewRoleName +'">'+ viewRoleList[i].viewRoleName +'</span>');
				}else{
					htmlUl.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="1" itemid="'+ viewRoleList[i].viewRoleId+'" title="'+ viewRoleList[i].viewRoleName +'">'+ viewRoleList[i].viewRoleName +'</span>');
				}
				
			}
			
		}
		htmlUl.push('</ul>');
		_html = html.concat(htmlUl);
		$("#shootLocationList").empty();
		$("#shootLocationList").append(_html.join(""));
	}else{
		$("#shootLocationList").empty();
	}
	if(specialPropList.length != 0 && specialPropList != null){
		var _html = [];
		var html = ['<input type="button" class="coll-expand hide" onclick="collOrExpandItem(this)">'];
		var htmlp = [];
		htmlp.push('<p>');
		var htmlUl = ['<ul class="drop-list">'];
		for(var i= 0; i< specialPropList.length; i++){
			if(i < 10){
				if(jQuery.inArray(specialPropList[i].id, activeId) == -1){
					htmlp.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="2" itemid="'+ specialPropList[i].id+'" title="'+ specialPropList[i].goodsName +'">'+ specialPropList[i].goodsName +'</span>');
				}else{
					htmlp.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="2" itemid="'+ specialPropList[i].id+'" title="'+ specialPropList[i].goodsName +'">'+ specialPropList[i].goodsName +'</span>');
				}
				
				if(i % 5 == 4){
					htmlp.push('</p>');
					html = html.concat(htmlp);
					htmlp = ['<p>'];
				}
				if(i == (specialPropList.length -1)){
					htmlp.push('</p>');
					html = html.concat(htmlp);
				}
			}else{
				if(jQuery.inArray(specialPropList[i].id, activeId) == -1) {
					htmlUl.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="2" itemid="'+ specialPropList[i].id+'" title="'+ specialPropList[i].goodsName +'">'+ specialPropList[i].goodsName +'</span>');
				}else{
					htmlUl.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="2" itemid="'+ specialPropList[i].id+'" title="'+ specialPropList[i].goodsName +'">'+ specialPropList[i].goodsName +'</span>');
				}
				
			}
			
		}
		htmlUl.push('</ul>');
		_html = html.concat(htmlUl);
		$("#specialPropList").empty();
		$("#specialPropList").append(_html.join(""));
	}else{
		$("#specialPropList").empty();
	}
	if(locationList.length != 0 && locationList != null){
		var _html = [];
		var html = ['<input type="button" class="coll-expand hide" onclick="collOrExpandItem(this)">'];
		var htmlp = [];
		htmlp.push('<p>');
		var htmlUl = ['<ul class="drop-list">'];
		for(var i= 0; i< locationList.length; i++){
			if(i < 25){
				if(jQuery.inArray(locationList[i].locationId, activeId) == -1){
					htmlp.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="3" itemid="'+ locationList[i].locationId+'" title="'+ locationList[i].location +'">'+ locationList[i].location +'</span>');
				}else{
					htmlp.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="3" itemid="'+ locationList[i].locationId+'" title="'+ locationList[i].location +'">'+ locationList[i].location +'</span>');
				}
				
				if(i % 5 == 4){
					htmlp.push('</p>');
					html = html.concat(htmlp);
					htmlp = ['<p>'];
				}
				if(i == (locationList.length -1)){
					htmlp.push('</p>');
					html = html.concat(htmlp);
				}
			}else{
				if(jQuery.inArray(locationList[i].locationId, activeId) == -1){
					htmlUl.push('<span class="select-item" onclick="selectActiveItem(this)" data-type="3" itemid="'+ locationList[i].locationId+'" title="'+ locationList[i].location +'">'+ locationList[i].location +'</span>');
				}else{
					htmlUl.push('<span class="select-item select" onclick="selectActiveItem(this)" data-type="3" itemid="'+ locationList[i].locationId+'" title="'+ locationList[i].location +'">'+ locationList[i].location +'</span>');
				}
				
			}
		}
		htmlUl.push('</ul>');
		_html = html.concat(htmlUl);
		$("#mainViewList").empty();
		$("#mainViewList").append(_html.join(""));
	}else{
		$("#mainViewList").empty();
	}
}


//显示或者收起选项列表
function collOrExpandItem(own){
	if($(own).hasClass("hide")){
		$(own).removeClass("hide");
		$(own).addClass("show");
		$(own).parent("div").find("ul.drop-list").toggle();
	}else{
		$(own).removeClass("show");
		$(own).addClass("hide");
		$(own).parent("div").find("ul.drop-list").toggle();
	}
}


//显示添加关注窗口
function showActiveWin(){
	$("#addActiveWin").jqxWindow("open");
}
//关闭添加关注窗口
function closeActiveWin(){
	$("#addActiveWin").jqxWindow("close");
}

//查询关注项信息
function queryActiveContent(){
	$.ajax({
		url: '/scheduleManager/queryAttentionTotalInfo',
		type: 'post',
		data: filter,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var attentionTotal = response.attentionTotal;
				//生成关注内容列表
				initActiveList(attentionTotal);
			}else{
				createPlanViewHead();
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//生成关注内容列表
function initActiveList(activeList){
	var html = [];
	activeThing = [];
	if(activeList != null && activeList.length != 0){
		for(var i= 0; i< activeList.length; i++){
			var color = colorArray[i];
			var data = {};
			data.name = activeList[i].name;
			data.id = activeList[i].id;
			data.type = activeList[i].type;
			html.push('<div class="tab-list" data-color="'+ color +'" style="border: 2px solid '+ color +';" aid="'+ activeList[i].id +'" data-type="'+ activeList[i].type +'">');
			html.push(' <div class="tab-title" style="background:'+ color +';">');
			html.push('  <span>'+ activeList[i].name +'</span>');
			html.push('  <input class="delete-btn" type="button" value="X" onclick="deleteActiveItem(this)">');
			html.push(' </div>');
			if(activeList[i].type == 1){
				html.push(' <p>主要演员</p>');
			}else if(activeList[i].type == 2){
				data.type = activeList[i].type;
				html.push(' <p>特殊道具</p>');
			}else{
				data.type = activeList[i].type;
				html.push(' <p>主场景</p>');
			}
			var date = "";
			if(activeList[i].startDate != null && activeList[i].endDate != null){
				date = activeList[i].startDate+"&nbsp;&nbsp;至&nbsp;&nbsp;" + activeList[i].endDate;
			}else if(activeList[i].startDate != null && activeList[i].endDate == null){
				date = activeList[i].startDate;
			}else if(activeList[i].startDate == null && activeList[i].endDate != null){
				date = activeList[i].endDate;
			}else{
				date = "";
			}
			html.push(' <p>'+ date +'</p>');
			if(activeList[i].dayNum == null){
				activeList[i].dayNum = 0;
			}
			if(activeList[i].viewNum == null){
				activeList[i].viewNum = 0;
			}
			if(activeList[i].pageCount == null){
				activeList[i].pageCount = "";
			}
			html.push(' <p>共:&nbsp;'+ activeList[i].dayNum +'天&nbsp;&nbsp;'+ activeList[i].viewNum +'场&nbsp;&nbsp;'+ activeList[i].pageCount +'页</p>');
//			html.push(' <div class="shoot-group">');
//			html.push('  <span>拍摄组: </span>');
//			var groupName = [];
//			if(activeList[i].groupName == null){
//				activeList[i].groupName = "";
//				groupName = [];
//			}
//			if(activeList[i].groupName.search($.trim("单组")) != -1){
//				var groupNames = activeList[i].groupName.split(",");
//				for(var n=0; n<groupNames.length; n++){
//					if(groupNames[n] != "" && groupNames[n] != "单组"){
//						groupName.push(groupNames[n]);
//					}
//					
//				}
//			}
//			html.push('  <span class="shoot-group-list" id="shootGroupName" title="'+ groupName.join(",") +'">'+ groupName.join(",") +'</span>');
//			html.push(' </div>');
			html.push('</div>');
			
			activeThing.push(data);//关注内容
		}
		
	}else{
		html.push('<p style="box-sizing: border-box; padding-left: 10px;">暂无关注列表</p>');
	}
	$("#concernsList").empty();
	$("#concernsList").append(html.join(""));
	
	//查询计划视图数据
	queryPlanView();
}


//查询计划视图数据
function queryPlanView(){
	$.ajax({
		url: '/scheduleManager/queryScheduleCalendarInfo',
		type: 'post',
		data: filter,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var calendarInfo = response.calendarInfo;
				var attentionTotal = response.attentionTotal;
				//生成计划视图
				createPlanViewHead();
				createPlanViewBody(calendarInfo);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//生成计划视图表头
function createPlanViewHead(){
	var html = [];
	html.push('<tr>');
	html.push('<td style="width: 10%;">月</td>');
	html.push('<td style="width: 5%;">日</td>');
	html.push('<td style="width: 5%;">拍摄组别</td>');
	html.push('<td style="width: 5%;">场数</td>');
	var countWidth = 75;
	var columnWidth = divide(countWidth, 2);//平分拍摄地和关注项的总宽度
	var shootWidth = columnWidth;
	var activesWidth = divide(columnWidth, activeThing.length);//每一个关注项的宽度
	
	if(activesWidth < 5){//如果得到的每一个关注项的宽度小于50px,就设为50px；
		activesWidth = 5;
		shootWidth = subtract(countWidth - multiply(activeThing.length, 5));
	}
	if(activeThing.length <= 5){
		activesWidth = 6;
		shootWidth = subtract(countWidth - multiply(activeThing.length, 6));
	}
	if(activeThing.length == 10){//关注项为10个时，平分一半的宽度；
		activesWidth = divide(columnWidth, 10);
		shootWidth = columnWidth;
	}
	if(activeThing.length != 0){
		for(var i= 0; i< activeThing.length; i++){
			html.push('<td class="active-column" style="width: '+ activesWidth +'%;"><p class="header-active-col" title="'+ activeThing[i].name +'">'+ activeThing[i].name +'</p></td>');
		}
	}
	html.push('<td class="shoot-column" style="width:'+ shootWidth +'%;">拍摄地</td>');
	html.push('<td class="scroll-td" style="width: 11px; border: 0px; background: #fff;"></td>');
	html.push('</tr>');
	$("#headerTable").empty().append(html.join(""));
	var height = $("#gridHeader").height();
	$("#gridBody").css({"height": "calc(100% - "+ height+"px)"});
}

//生成计划视图表体
function createPlanViewBody(calendarInfo){
	var countWidth = 75;
	var columnWidth = divide(countWidth, 2);//平分拍摄地和关注项的总宽度
	var shootWidth = columnWidth;
	var activesWidth = divide(columnWidth, activeThing.length);//每一个关注项的宽度
	
	if(activesWidth < 5){//如果得到的每一个关注项的宽度小于50px,就设为50px；
		activesWidth = 5;
		shootWidth = subtract(countWidth - multiply(activeThing.length, 5));
	}
	if(activeThing.length <= 5){
		activesWidth = 6;
		shootWidth = subtract(countWidth - multiply(activeThing.length, 6));
	}
	if(activeThing.length == 10){//关注项为10个时，平分一半的宽度；
		activesWidth = divide(columnWidth, 10);
		shootWidth = columnWidth;
	}
	
	var html = [];
	var flag = true;
	var length = 0;
	if(calendarInfo != null){
		for(var key in calendarInfo){
			length = 0;
			flag = true;
			var item = calendarInfo[key];
			for(var i= 0; i< item.length; i++){
				length += item[i].dayList.length;//合并总长度
			}
			for(var i= 0; i< item.length; i++){
				var dayList = item[i].dayList;
				for(var k= 0; k< dayList.length; k++){
					if(flag){
						html.push('<tr>');
						if(key != ""){
//							key = parseInt(key);
							html.push('<td rowspan="'+ length +'" style="width: 10%; text-align: center;">' + key + "月</td>");//月
						}else{
							html.push('<td rowspan="'+ length +'" style="width: 10%; text-align: center;">' + key + "</td>");//月
						}
						
						flag = false;
					}else{
						html.push('<tr>');
					}
					if(dayList[k].day == null){
						dayList[k].day = "";
					}
					if(dayList[k].day != ""){
						dayList[k].day = parseInt(dayList[k].day);
					}
					html.push('<td style="width:5%; text-align: center">' + dayList[k].day + '</td>');//日
					if(dayList[k].shootGroup == null || dayList[k].shootGroup == ""){//分组
						html.push('<td style="width: 5%;"></td>');
					}else if(dayList[k].shootGroup != "" && dayList[k].shootGroup != "单组"){
						html.push('<td style="width: 5%;"><p class="group-flag"></p></td>');
					}else {
						html.push('<td style="width: 5%;"></td>');
					}
					
//					var groupName = [];
//					if(activeList[i].groupName == null){
//						activeList[i].groupName = "";
//						groupName = [];
//					}
//					if(activeList[i].groupName.search($.trim("单组")) != -1){
//						var groupNames = activeList[i].groupName.split(",");
//						for(var n=0; n<groupNames.length; n++){
//							if(groupNames[n] != "" && groupNames[n] != "单组"){
//								groupName.push(groupNames[n]);
//							}
//							
//						}
//					}
					
					
					
					
					if(dayList[k].viewNum == null){
						dayList[k].viewNum = "";						
					}
					html.push('<td style="width: 5%; text-align: center;">'+  dayList[k].viewNum  +'</td>');
					for(var j= 0; j< activeThing.length; j++){
						if(dayList[k][activeThing[j].id]){
							html.push('<td class="active-column" style="width:'+ activesWidth +'%;"><p style="background:'+ colorArray[j] +'"></p></td>');
						}else{
							html.push('<td class="active-column" style="width:'+ activesWidth +'%;"></td>');
						}
					}
					if(item[i].shootLocation == null){
						item[i].shootLocation = "";
					}
					if(k == 0){
						html.push('<td class="active-column" rowspan="'+ dayList.length +'" style="width:'+ shootWidth +'%; box-sizing: border-box; padding-left:3px;"><p>' + item[i].shootLocation + "</p></td>");//拍摄地
					}
					
					html.push('</tr>');
				}
				
			}
		}
		$("#bodyTable").empty();
		$("#bodyTable").append(html.join(""));
	}
}


//删除关注的项目
function deleteActiveItem(own){
	parent.popupPromptBox("提示", "确定要删除该项吗？", function(){
		$("#addActiveOptionBtn").attr("disabled", true);
		var tabObj = $(own).parents("div.tab-list");
		var id = tabObj.attr("aid");
		tabObj.remove();
		var content = [];
		var tabArray = $("#concernsList").find("div.tab-list");
		$.each(tabArray, function(){
			var aid = $(this).attr("aid");
			if(aid != id){
				var str = $(this).attr("data-type") + ":" +aid;
				content.push(str);
			}
		});
		var info = content.join(",");
		saveAttentionMethod(info);
		$("#addActiveOptionBtn").attr("disabled", false);
	});
}
//选择关注条件
function selectActiveOption(own){
	$(own).siblings("li").removeClass("select");
	$(own).addClass("select");
}
//添加关注的项目
function addActiveOption(name, type, id){
	var index = 0;
	var lastLi = $("#alreadyActiveList").find("li.active-option:last");
	if(lastLi.length == 0){
		index = 0;
	}else{
		var colorItem = lastLi.attr("data-color");
		index = jQuery.inArray(colorItem, colorArray);//当前最新的使用的颜色；
		if(index == -1){
			return;
		}
		index++;
	}
	
	var color = colorArray[index];
	var html = [];
	html.push('<li class="active-option" data-color="'+ color +'" data-type="'+ type +'" itemid="'+ id +'" style="background:'+ color +';" onclick="selectActiveOption(this)">');
	html.push('<span class="active-option-name">'+ name +'</span>');		                         
	html.push('<input class="delete-btn" type="button" value="x" onclick="deleteActiveOption(this)">');		                         
	html.push('</li>');
	$("#alreadyActiveList").append(html.join(""));
}
//选择要关注的项目
function selectActiveItem(own){
	var type = $(own).attr('data-type');
	var itemId = $(own).attr("itemid");
	var itemName = $(own).text();
	if($(own).hasClass("select")){
		$(own).removeClass("select");
		var li = $("#alreadyActiveList").find("li[itemid="+ itemId +"]");
		var colorItem = li.attr("data-color");
		colorArray.splice(jQuery.inArray(colorItem,colorArray),1); //移除数组中的指定元素
		colorArray.push(colorItem);//将移除的元素放到数组末尾 
		activeThing.splice(jQuery.inArray(colorItem,colorArray), 1);//移除数据
		li.remove();
	}else{
		var liCount = $("#alreadyActiveList").find("li.active-option");
		if(liCount.length >= 10){
			parent.showInfoMessage("最多只能选择10个关注内容");
			return;
		}
		$(own).addClass("select");
		addActiveOption(itemName, type, itemId);
	}
}
//删除关注的项目
function deleteActiveOption(own){
	var li = $(own).parent("li");
	var id = li.attr("itemid");
	var colorItem = li.attr("data-color");
	colorArray.splice(jQuery.inArray(colorItem,colorArray),1); //移除数组中的指定元素
	colorArray.push(colorItem);//将移除的元素放到数组末尾 
	activeThing.splice(jQuery.inArray(colorItem,colorArray), 1);//移除数据
	li.remove();
	$("#activeItemSelect").find("span[itemid="+ id +"]").removeClass("select");
}
//保存关注内容
function saveActiveContent(){
	var options = $("#alreadyActiveList").find("li.active-option");
	var content = [];
	$.each(options, function(){
		var data = {};
		var type = $(this).attr("data-type");
		var id = $(this).attr("itemid");
		data.type = type;
		data.id = id;
		data.name = $(this).find("span.active-option-name").val();
		if(id != undefined || id != ""){//没有id说明没有选择关注的内容,不保存
			var str = type + ":" + id;
			content.push(str);
		}
	});
	var info = content.join(",");
	saveAttentionMethod(info);
	
}

//保存关注内容方法
function saveAttentionMethod(content){
	$.ajax({
		url: '/cacheManager/saveCacheInfo',
		type: 'post',
		data: {"type": 2, "content": content},
		datatype: 'json',
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("操作成功");
				$("#addActiveWin").jqxWindow("close");

				//保存关注内容成功后，查询数据
				querySaveCondition();
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}


//回车搜索
function keySearch(event){
	if(event.keyCode == 13){
		searchKeyContent();
	}
}

//关键字搜索
function searchKeyContent(){
	var value= $("#keySearch").val();
	if(value == ""){
		$("#activeItemSelect").find("span.select-item").show();
	}
	else{
		var items = $("#activeItemSelect").find("span.select-item");
		$("#activeItemSelect").find("ul.drop-list").show();
		$.each(items, function(){
			var content = $(this).text();
			if(content.search($.trim(value)) != -1){
				$(this).show();
			}else{
				$(this).hide();
			}
		});
	}
}




//编辑计划
function editorialPlan(){
	parent.editedPlan();
}
//查看计划
function viewPlanInfo(){
	parent.viewPlan();
}


//计划详情
function planDetail(){
	parent.planDetail();
}