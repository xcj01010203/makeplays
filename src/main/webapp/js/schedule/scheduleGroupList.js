var subData = {};
var checkIndex="";
$(function(){
	if(!hasImportScheduleAuth){//导入权限
		$(".import-btn").hide();
	}
	if(!hasExportScheduleAuth){//导出权限
		$(".export-btn").hide();
	}
	//查询计划分组列表
	queryPlanGroupList();
});


//查询当前选中的分组
function queryCheckedGroup(){
	var id = $("#groupDetailCon li.checked").attr("gid");
	if(id== undefined || id == ""){
		checkIndex = "";
	}else{
		checkIndex= id;
	}
}

//查询计划分组列表
function queryPlanGroupList(flag){
	queryCheckedGroup();
	$.ajax({
		url: '/scheduleManager/queryScheduleGroupList',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var scheduleGroupList = response.scheduleGroupList;
				//初始化计划分组列表
				if(flag){
					//计划分组粗略信息
					loadRoughtGroup(scheduleGroupList);
				}else{
					initGroupList(scheduleGroupList);
				}
			    
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//初始化计划分组列表
function initGroupList(scheduleGroupList){
	//计划分组粗略信息
	loadRoughtGroup(scheduleGroupList);
	var html = [];
	var headerHtml = [];
	if(scheduleGroupList.length != 0 && scheduleGroupList != null){
		for(var i= 0; i< scheduleGroupList.length; i++){
			
			if(i==0){//未分组
				if(scheduleGroupList[i].groupId == checkIndex){
					headerHtml.push('<li class="checked" onclick="checkGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">');
				}else{
					headerHtml.push('<li onclick="checkGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">');
				}
				headerHtml.push('<p>');
				headerHtml.push('<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>');
				if(scheduleGroupList[i].groupId == "0"){
					headerHtml.push('<span class="none-group-name">' + scheduleGroupList[i].groupName + "</span>");
				}else{
					headerHtml.push('<input type="text" value="'+ scheduleGroupList[i].groupName +'">');
				}
				
				if(scheduleGroupList[i].viewCount == null){
					scheduleGroupList[i].viewCount = "";
				}
				headerHtml.push('<span>'+ scheduleGroupList[i].viewCount +'</span>场&nbsp;&nbsp;&nbsp;&nbsp;');
				if(scheduleGroupList[i].pageCount == null){
					scheduleGroupList[i].pageCount = "";
				}
				headerHtml.push('<span>'+ scheduleGroupList[i].pageCount +'</span>页');
				headerHtml.push('</p>');
				headerHtml.push('<p>');
				if(scheduleGroupList[i].startDate == null || scheduleGroupList[i].endDate == null){
					headerHtml.push('<span class="date-period"></span>');
				}else{
					headerHtml.push('<span class="date-period">'+ scheduleGroupList[i].startDate +'~'+ scheduleGroupList[i].endDate +'</span>');
				}
				
				if(scheduleGroupList[i].dayCount == null){
					headerHtml.push('<span></span>');
				}else{
					headerHtml.push('<span>'+ scheduleGroupList[i].dayCount +'</span>天&nbsp;&nbsp;&nbsp;&nbsp;');
				}
				if(scheduleGroupList[i].everyDayPage == null){
					headerHtml.push('<span></span>');
				}else{
					headerHtml.push('<span>'+ scheduleGroupList[i].everyDayPage +'</span>页/天');
				}
				
				headerHtml.push('</p>');
				headerHtml.push('</li>');
			}else{
				if(scheduleGroupList[i].groupId == checkIndex){
					html.push('<li class="checked" onclick="checkGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">');
				}else{
					html.push('<li onclick="checkGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">');
				}
				html.push('<p>');
				html.push('<input type="checkbox" onclick="isCheckAll(event)">');
				if(scheduleGroupList[i].groupId == "0"){
					html.push('<span class="none-group-name">' + scheduleGroupList[i].groupName + "</span>");
				}else{
					html.push('<input type="text" value="'+ scheduleGroupList[i].groupName +'">');
				}
				
				if(scheduleGroupList[i].viewCount == null){
					scheduleGroupList[i].viewCount = "";
				}
				html.push('<span>'+ scheduleGroupList[i].viewCount +'</span>场&nbsp;&nbsp;&nbsp;&nbsp;');
				if(scheduleGroupList[i].pageCount == null){
					scheduleGroupList[i].pageCount = "";
				}
				html.push('<span>'+ scheduleGroupList[i].pageCount +'</span>页');
				html.push('</p>');
				html.push('<p>');
				if(scheduleGroupList[i].startDate == null || scheduleGroupList[i].endDate == null){
					html.push('<span class="date-period"></span>');
				}else{
					html.push('<span class="date-period">'+ scheduleGroupList[i].startDate +'~'+ scheduleGroupList[i].endDate +'</span>');
				}
				
				if(scheduleGroupList[i].dayCount == null){
					html.push('<span></span>');
				}else{
					html.push('<span>'+ scheduleGroupList[i].dayCount +'</span>天&nbsp;&nbsp;&nbsp;&nbsp;');
				}
				if(scheduleGroupList[i].everyDayPage == null){
					html.push('<span></span>');
				}else{
					html.push('<span>'+ scheduleGroupList[i].everyDayPage +'</span>页/天');
				}
				
				html.push('</p>');
				html.push('</li>');
			}
			
		}
	}else{
			
		}
		$("#noneGroupItem").empty();
		$("#noneGroupItem").append(headerHtml.join(""));
		$("#groupDetailList").empty();
		$("#groupDetailList").append(html.join(""));
		//初始化分组的拖拽排序事件
		initSortGroup();
		//重命名分组
		reWriteName();
}

//分组粗略信息
function loadRoughtGroup(scheduleGroupList){
	var roughTml = [];
	if(scheduleGroupList.length != 0 && scheduleGroupList != null){
		for(var i= 0; i< scheduleGroupList.length; i++){
			if(scheduleGroupList[i].groupId == checkIndex){
				roughTml.push('<li class="checked" onclick="checkRoughGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">' + scheduleGroupList[i].groupName + '</li>');
			}else{
				roughTml.push('<li onclick="checkRoughGroup(this, event)" gid="'+ scheduleGroupList[i].groupId +'">' + scheduleGroupList[i].groupName + '</li>');
			}
		}
	}else{
		roughTml.push('<li>暂无分组</li>');
	}
	$("#groupRoughList").empty();
	$("#groupRoughList").append(roughTml.join(""));
}



//初始化分组的拖拽排序事件
function initSortGroup(){
	var fixHelper = function(e, ui) {  
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#groupDetailList").sortable({                //这里是talbe tbody，绑定 了sortable   
        helper: fixHelper,                  //调用fixHelper   
        axis:"y",  
        start:function(e, ui){  
            //ui.helper.css({"background":"#fff"})     //拖动时的行，要用ui.helper   
            return ui;  
        },  
        stop:function(e, ui){  
            //ui.item.removeClass("ui-state-highlight"); //释放鼠标时，要用ui.item才是释放的行   
            return ui;  
        }
    }).disableSelection();
	$("#groupDetailList").on( "sortstop", function( event, ui ) {
		//更新排序后的分组信息
		updateGroupInfo();
	});
}
//分组的点击事件
function checkGroup(own, event){
	checkIndex = $(own).attr("gid");
	if($(own).hasClass("checked")){
		
	}else{
		var id= $(own).attr("gid");
		if(id != undefined){
			$("li.checked").removeClass("checked");
			$("li[gid="+id+"]").addClass("checked");
			var groupName = $(own).find("input[type=text]").val();
			if(groupName == undefined){
				groupName = $(own).find("span.none-group-name").text();
			}
			parent.groupClick(id, groupName);
		}
	}
}

//粗略分组的点击事件
function checkRoughGroup(own, event){
	checkIndex = $(own).attr("gid");
	if($(own).hasClass("checked")){
		
	}else{
		var id= $(own).attr("gid");
		if(id != undefined){
			$("li.checked").removeClass("checked");
			$("li[gid="+id+"]").addClass("checked");
			var groupName = $(own).text();
			parent.groupClick(id, groupName);
		}
		return false;
	}
}


//通过关键字查询分组
function searchKeyGroup(own,ev){
	if(ev.keyCode == 13){
		searchGroup();
	}
}
//查询分组
function searchGroup(){
	var value = $("#groupKey").val();
	if(value == ""){
		$("#groupDetailCon").find("li").show();
	}else{
		var _li = $("#groupDetailCon").find("li");
		$.each(_li, function(){
			var content = $(this).find("input[type=text]").eq(0).val();
			if(content == undefined){
				content = $(this).find("span").eq(0).text();
			}
			if(content.search($.trim(value))!=-1){
				$(this).show();
			}else{
				$(this).hide();
			}
		});
	}
}

//显示智能整理窗口
function showIntelligent(){
	parent.showIntelligentWin();
}

//全选
function selectAllGroup(own){
	var _li = $("#groupDetailCon").find("li");
	if($(own).is(":checked")){
		$.each(_li, function(){
			$(this).find("input[type=checkbox]").prop("checked", true);
		});
	}else{
		$.each(_li, function(){
			$(this).find("input[type=checkbox]").prop("checked", false);
		});
	}
}
//checkbox事件判断是否全选
function isCheckAll(event){
	
	var _obj = $("#groupDetailCon");
	var checkboxs = _obj.find("li").find(":checkbox");
	
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#selectAll").prop("checked",false);
	}else{
		$("#selectAll").prop("checked",true);
	}
	event.stopPropagation();
}

//反选
function selectNoCheck(){
	var checkboxs = $("#groupDetailCon").find(":checkbox");
	$.each(checkboxs, function(){
		if($(this).is(":checked")){
			$(this).prop("checked", false);
		}else{
			$(this).prop("checked", true);
		}
	});
	isCheckAll();
}
//添加分组
function addGroup(){
	$.ajax({
		url: '/scheduleManager/saveScheduleGroupInfo',
		type: 'post',
		data: {"groupName": "新建分组"},
		datatype: 'json',
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("新建分组成功");
				var id= response.groupId;
				var html = '';
				html += '<li gid="'+ id +'" onclick="checkGroup(this, event)">';
				html += '<p>';
				html += '<input type="checkbox" onclick="isCheckAll(event)">';
				html += '<input type="text" value="新建分组">';
				html += '<span></span>&nbsp;&nbsp;&nbsp;&nbsp;';
				html += '<span></span>';
				html += '</p>';
				html += '<p>';
				html += '<span class="date-period"></span>';
				html += '<span></span>&nbsp;&nbsp;&nbsp;&nbsp;';
				html += '<span></span>';
				html += '</p>';
				html += '</li>';
				$("#groupDetailList").prepend(html);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
	
}

//重命名分组
function reWriteName(){
	$("#groupDetailList").on("change", "input[type=text]", function(event){
		var name = $(this).val();
		var id = $(this).parents("li").attr("gid");
		$.ajax({
			url: '/scheduleManager/saveScheduleGroupInfo',
			type: 'post',
			data: {"groupId": id, "groupName": name},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("重命名成功");
					$("#groupRoughList li[gid="+id+"]").text(name);
				}else{
					parent.showErrorMessage(response.message);
					$("#groupDetailList").find("li[gid="+id+"]").find("input[type=text]").val(response.groupName);
					$("#groupDetailList").find("li[gid="+id+"]").find("input[type=text]").focus();
				}
			}
		});
	});
}

//删除分组
function deleteGroup(){
	var _li = $("#groupDetailList").find("li");
	var _liArray = [];
	var ids = [];
	$.each(_li, function(){
		var checkbox = $(this).find("input[type=checkbox]").eq(0);
		if(checkbox.is(":checked")){
			ids.push($(this).attr("gid"));
			_liArray.push($(this));
		}
	});
	if(ids.length == 0) {
		parent.showInfoMessage('请选择要删除的分组');
		return;
	}
	parent.popupPromptBox('提示','是否要删除分组信息？',function(){
		$.ajax({
			url: '/scheduleManager/deleteScheduleGroupInfo',
			type: 'post',
			data: {"groupIds": ids.join(",")},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除计划分组成功");
					$.each(_liArray, function(){
						$(this).remove();
					});
					if(checkIndex == ""){//值为空说明当前没有选择分组，展示的是全部数据，更新顺序后需要刷新场景列表；
						parent.refrushGrid();
					}
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	});
	
}

//更新排序后的分组信息
function updateGroupInfo(){
	var _li = $("#groupDetailList").find("li");
	var groupId = [];
	$.each(_li, function(){
		groupId.push($(this).attr("gid"));
	});
	$.ajax({
		url: '/scheduleManager/updateScheduleGroupSequence',
		type: 'post',
		data: {"groupIds": groupId.join(",")},
		datatype: 'json',
		success: function(response){
			if(response.success){
//				parent.showSuccessMessage("更新顺序成功");
				if(checkIndex == ""){//值为空说明当前没有选择分组，展示的是全部数据，更新顺序后需要刷新场景列表；
					parent.refrushGrid();
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}


//阻止冒泡事件
function cancelBuble(event){
	return false;
	event.stopPropagation();
}



//导入计划列表
function importPlan(){
	parent.importPlanList();
}
//导出计划列表
function exportPlan(){
	var url = "/scheduleManager/exportSchedule";
	parent.exportPlanList(url);
}