$(document).ready(function(){
	
	//请求角色列表
	loadViewRoleList();
	//初始化设置角色类型下拉框
    initSetRoleTypeTree();
	//初始化高级查询窗口
	initAdvanceSearch ();
	//初始化创建角色窗口--修改角色窗口
	initAddViewRoleInfo();
	//初始化合并角色名称窗口
	initMakeRoleToOne();
	//初始化演员请假列表
	/*initLeaveRecord();*/
	//初始化演员评价
	initRoleEvaluation();
	//初始化角色场景统计tab、radio click事件
	initRoleViewPlayStatClick();
	
	//权限
	if(!hasDeleteViewRoleBatchAuth){
		
		$("#deleteViewRoleBatch").remove();
	}
	if(isRoleReadonly){
		$("#createRoleBtn").remove();
		$("#makeRoleToOneBtn").remove();
		$("#setRoleTypeDiv").remove();
		$("#deleteViewRoleBatch").remove();
		$("#modifyViewRoleName").attr("disabled", true);
		$("#modifyShortName").attr("disabled", true);
		$("#modifyViewRoleType").attr("disabled", true);
		$("#modifyActorName").attr("disabled", true);
		$("#modifyEnterDate").attr("disabled", true);
		$("#modifyLeaveDate").attr("disabled", true);
		$("#saveModifyRole").hide();
		$("#leaveStartDate").attr("disabled", true);
		$("#leaveEndDate").attr("disabled", true);
		$("#setLeaveButton").hide();
	}
	if(!hasExportRoleAuth) {
		$("#exportBtn").remove();
		$("#exportRoleBtn").remove();
	}
	
	
});
var subData = {};//定义要查询的变量
//请求角色列表
function loadViewRoleList(top){
	//显示loading效果
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDataDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	$.ajax({
		url: '/viewRole/queryViewRoleList',
		type: 'post',
		data: subData,
		datatype: 'json',
		success:  function(response){
			if(response.success){
				data = response.viewRoleList;
				loadViewRoleListWithNull(data,top);
				//取消loading效果
				_LoadingHtml.hide();
				$(".opacityAll").hide();
				//取消全选
				$("#checkAll").prop("checked", false);
				//$('#viewRoleListGrid').datagrid('loadData', data);
				
				//取出演员数量
				var mainCount = response.mainCount;
				var guestCount = response.guestCount;
				var massCount = response.massCount;
				var otherCount = response.otherCount;
				var totalCount = response.totalCount;
				//拼接统计数据
				var text = "共有角色："+ totalCount +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 其中主演："+ mainCount +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 特约："+ 
							guestCount +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 群演："+ massCount +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 待定：" + otherCount;
				$("#viewRoleCountNum").html(text);
			}
		}
	});
}

//手工拼接数据列表
function loadViewRoleListWithNull(data,top){
	var _table = $("#viewRoleListGrid");
	var tableDataArr = [];
	if (data != null && data.length>0) {
		
		for(var i =0; i<data.length; i++){
			var dataMap = data[i];
			var roleInfo = {};
			//封装数据
			roleInfo.viewRoleName = dataMap['viewRoleName'];
			roleInfo.shortName = dataMap['shortName'];
			roleInfo.viewRoleType = dataMap['viewRoleType'];
			roleInfo.actorName = dataMap['actorName'];
			roleInfo.enterDate = dataMap['enterDate'];
			roleInfo.leaveDate = dataMap['leaveDate'];
			roleInfo.viewRoleId = dataMap['viewRoleId'];
			roleInfo.actorId = dataMap['actorId'];
			roleInfo.shootDays = dataMap['shootDays'];
			roleInfo.workHours = dataMap['workHours'];
			roleInfo.restHours = dataMap['restHours'];
			tableDataArr.push(" <tr onmouseover='showBtnList(this)' onmouseout='hideBtnList(this)'>");
			tableDataArr.push(" <td style='width:3%; min-width: 3%; max-width: 3%;'><input type='checkbox' onclick='isCheckAll()' value='"+ dataMap['viewRoleId'] +"' svalTotalCount='"+ dataMap['totalViewCount'] +"'  svalRoleType='"+ dataMap['viewRoleType'] +"'></td>");
			//角色名称
			var viewRoleName = dataMap['viewRoleName'];
			if (viewRoleName == null ) {
				tableDataArr.push(" <td style='width:10%; min-width: 10%; max-width: 10%;'><div class='viewrole-name-column'>");
			}else {
				tableDataArr.push(" <td style='width:10%; min-width: 10%; max-width: 10%;'><div class='viewrole-name-column'>");
//				tableDataArr.push(" <a class='float-left viewrole-name' viewroleid = '"+ dataMap['viewRoleId'] +"' href='javascript:void(0);' onclick='modifyViewRoleInfo("+ JSON.stringify(roleInfo) +")'>" + dataMap['viewRoleName'] + "</a>");
				tableDataArr.push(" <a class='float-left viewrole-name' viewroleid = '"+ dataMap['viewRoleId'] +"' href='javascript:void(0);' onclick='showRoleViewPlayStat("+ JSON.stringify(roleInfo) +")'>" + dataMap['viewRoleName'] + "</a>");
			}
			tableDataArr.push("	<span class='operate-attention-a'>");
			
			//是否是关注角色
			if(dataMap['isAttentionRole']== 1){
				tableDataArr.push("<a class='float-right attention-role' href='javascript:void(0);' sval='"+ dataMap['viewRoleId'] +"' onclick='setAttentionRole(this)' title='取消关注角色'></a>");
			}
			
			tableDataArr.push("	</span>");
			tableDataArr.push("	<span class='operate-btn-list'>");
//			tableDataArr.push("	<a class='float-right role-view-stat' title='戏量统计' href='javascript:void(0);' onclick='showRoleViewPlayStat("+ JSON.stringify(roleInfo) +")'></a>");
//			tableDataArr.push("	<a class='float-right leave-record' title='请假设置' href='javascript:void(0);' onclick='showLeaveRecord(\""+dataMap['actorId']+"\")'></a>");
			if(dataMap['isAttentionRole']!= 1){
				tableDataArr.push("<a class='float-right empty-attention-role' href='javascript:void(0);' sval='"+ dataMap['viewRoleId'] +"' onclick='setAttentionRole(this)' title='设置关注角色'></a>");
			}
			if(!isRoleReadonly && (dataMap['totalViewCount']==0 || dataMap['viewRoleType'] == 4)){
				tableDataArr.push("<a class='float-right delete-view-list' title='删除角色' href='javascript:void(0);' onclick='deleteViewRoleInfo(\""+dataMap['viewRoleId']+"\",this)'></a>");
			}
			tableDataArr.push("	</span>");
			tableDataArr.push("</div></td>");
			
			//简称
			var shortName = dataMap['shortName'];
			if (shortName == null) {
				tableDataArr.push(" <td style='width:4%; min-width: 4%; max-width: 4%;'><span></span></td>");
			}else {
				tableDataArr.push(" <td style='width:4%; min-width: 4%; max-width: 4%;'><span>"+ dataMap['shortName'] +"</span></td>");
			}
			
			//角色类型
			var viewRoleType = dataMap['viewRoleType'];
			if (viewRoleType == 1) {
				tableDataArr.push(" <td style='width:8%; min-width: 8%; max-width: 8%;'><span>主要演员</span></td>");
			}else if (viewRoleType == 2) {
				tableDataArr.push(" <td style='width:8%; min-width: 8%; max-width: 8%;'><span>特约演员</span></td>");
			}else if (viewRoleType == 3) {
				tableDataArr.push(" <td style='width:8%; min-width: 8%; max-width: 8%;'><span>群众演员</span></td>");
			}else if (viewRoleType == 4) {
				tableDataArr.push(" <td style='width:8%; min-width: 8%; max-width: 8%;'><span>待定</span></td>");
			}
			
			//演员姓名
			tableDataArr.push(" <td style='width:6%; min-width: 6%; max-width: 6%;'>");
			if(dataMap['actorId'] !== null && dataMap['actorId'] != ''){
				tableDataArr.push("<div class='viewrole-name-column'>");
				tableDataArr.push(" <div class='float-left actor-name-padd'>"+ dataMap['actorName'] + "</div>");
				if(!isRoleReadonly) {
					tableDataArr.push(" <span class='operate-btn-list'>");
					tableDataArr.push("		<a class='float-right viewRole-evaluation' title='' href='javascript:void(0);' onclick='showRoleEvaluation(\""+ dataMap['actorId'] + "\")'>评</a>");
					tableDataArr.push(" </span>");
				}
				tableDataArr.push("</div>");
			}
			tableDataArr.push(" </td>");
			//第一次出现集次号
			var seriesViewNo = dataMap['seriesViewNo'];
			if (seriesViewNo == null) {
				tableDataArr.push(" <td style='width:6%; min-width: 6%; max-width: 6%;'><span></span></td>");
			}else {
				tableDataArr.push(" <td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ dataMap['seriesViewNo'] +"</span></td>");
			}
			
			//总场数
			var totalViewCount = dataMap['totalViewCount'];
			if (totalViewCount == null) {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>0</span></td>");
			}else {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ dataMap['totalViewCount'] +"</span></td>");
			}
			
			//总页数
			var totalPageCount = dataMap['totalPageCount'];
			if (totalPageCount == null) {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>0.0</span></td>");
			}else {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ dataMap['totalPageCount'] +"</span></td>");
			}
			
//			//已完成场数
//			var finishedViewCount = dataMap['finishedViewCount'];
//			if (finishedViewCount == null) {
//				tableDataArr.push("	<td style='width:8%; min-width: 8%; max-width: 8%;'><span>0</span></td>");
//			}else {
//				tableDataArr.push("	<td style='width:8%; min-width: 8%; max-width: 8%;'><span>"+ dataMap['finishedViewCount'] +"</span></td>");
//			}
//			
//			//未完成场数
//			var unfinishedViewCount = dataMap['unfinishedViewCount'];
//			if (unfinishedViewCount == null) {
//				tableDataArr.push("	<td style='width:8%; min-width: 8%; max-width: 8%;'><span>0</span></td>");
//			}else {
//				tableDataArr.push("	<td style='width:8%; min-width: 8%; max-width: 8%;'><span>"+ dataMap['unfinishedViewCount'] +"</span></td>");
//			}
			var finishedViewCount = dataMap['finishedViewCount'];
			if(totalViewCount == 0){
				tableDataArr.push("<td class='position-td' style='width:10%; min-width: 10%; max-width: 10%;'><div class='view-td-div'></div><p class='td-view-title'>"+ finishedViewCount +"/" + totalViewCount + "</p></td>");
				
			}else{
				if(finishedViewCount == null){
					finishedViewCount = 0;
				}
//				var width = multiply(divide(finishedViewCount, totalViewCount),100);
				var width = divide(multiply(finishedViewCount, 100), totalViewCount);
				if(width == 0){
					tableDataArr.push("<td class='position-td' style='width:10%; min-width: 10%; max-width: 10%;'><div class='view-td-div'></div><p class='td-view-title'>"+ finishedViewCount +"/" + totalViewCount + "</p></td>");
				}else{
					if(width < 50){
						tableDataArr.push("<td class='position-td' style='width:10%; min-width: 10%; max-width: 10%;'><div class='view-td-div left-half' style='width:"+ width +"%;'></div><p class='td-view-title'>"+ finishedViewCount +"/" + totalViewCount + "</p></td>");
					}else if(width == 100){
						tableDataArr.push("<td class='position-td' style='width:10%; min-width: 10%; max-width: 10%;'><div class='view-td-div over-half' style='width:"+ width +"%;'></div><p class='td-view-title'>"+ finishedViewCount +"/" + totalViewCount + "</p></td>");
						
					}else{
						tableDataArr.push("<td class='position-td' style='width:10%; min-width: 10%; max-width: 10%;'><div class='view-td-div equal-half' style='width:"+ width +"%;'></div><p class='td-view-title'>"+ finishedViewCount +"/" + totalViewCount + "</p></td>");
					}
					
				}
				
			} 
			
			//入组时间
			var enterDate = dataMap['enterDate'];
			if (enterDate == null) {
				tableDataArr.push("	<td style='width:7%; min-width: 7%; max-width: 7%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:7%; min-width: 7%; max-width: 7%;'><span>"+ dataMap['enterDate'] +"</span></td>");
			}
			
			//离组时间
			var leaveDate = dataMap['leaveDate'];
			if (leaveDate == null) {
				tableDataArr.push("	<td style='width:7%; min-width: 7%; max-width: 7%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:7%; min-width: 7%; max-width: 7%;'><span>"+ dataMap['leaveDate'] +"</span></td>");
			}
			
			//在组天数
			var shootDays = dataMap['shootDays'];
			if (shootDays == null) {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ dataMap['shootDays'] +"</span></td>");
			}
			
			//工作时长
			var workHours = dataMap['workHours'];
			if (workHours == null) {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ workHours +"</span></td>");
			}
			
			//休息时长
			var restHours = dataMap['restHours'];
			if (restHours == null) {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:6%; min-width: 6%; max-width: 6%;'><span>"+ restHours +"</span></td>");
			}
			
			//请假记录
			var leaveCount = dataMap['leaveCount'];
			if (leaveCount == null || leaveCount == 0) {
				tableDataArr.push("	<td style='width:9%; min-width: 9%; max-width: 9%;'><span></span></td>");
			}else {
				tableDataArr.push("	<td style='width:9%; min-width: 9%; max-width: 9%;'><div class='jqx-column align-center leave-statistic'>请假：" + leaveCount + "次/共：" + dataMap['totalLeaveDays'] + "天</div></td>");
			}
		}
		_table.append(tableDataArr.join(""));
	}
	
	//拖动排序插件
	var fixHelper = function(e, ui) {  
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    if(!isRoleReadonly) {
        $("#viewRoleListGrid").sortable({                //这里是talbe tbody，绑定 了sortable   
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
        $("#viewRoleListGrid").unbind("sortstop");
    	$("#viewRoleListGrid").on( "sortstop", function( event, ui ) {
    		updateRoleSort();
    	});
    }
	
	//回到原位置
	$("#viewRoleDiv").scrollTop(top);
}

//显示按钮组
function showBtnList(own){
	$(own).find(".operate-btn-list").show();
}

//隐藏按钮组
function hideBtnList(own){
	$(own).find(".operate-btn-list").hide();
}

//拖动排序
function updateRoleSort(){
	if(isRoleReadonly) {
		return false;
	}
	var ids = [];
	 $("#viewRoleListGrid input[type=checkbox]").each(function(i){
		 ids.push($(this).val());
	 }); 
	 ids.splice(ids.length-1,1);
	 $.ajax({
		 url: "/viewRole/updateViewRoleSequence", 
		 type:'post',
		 data:{viewRoleIds: ids.toString()},
		 dataType:'json',
		 success:function(response){
			 
			 if(!response.success) {
				 showErrorMessage(response.message); 
			 }
		 },
		 error:function(){
			 showErrorMessage("请求失败！");
		 }
	 });
}
//初始化设置角色类型下拉框
function initSetRoleTypeTree(){
	if(!isRoleReadonly){
		var screenWidth = window.screen.width;//屏幕分辨率(使其自适应)
		if(screenWidth >= 1366 && screenWidth <= 1399){
			$("#setRoleTypeDiv").jqxDropDownButton({theme:theme, height: 20, width: 20});
			//改动插件
			$("#dropDownButtonArrowsetRoleTypeDiv").jqxDropDownButton({ width: '20px'});
		}else{
			$("#setRoleTypeDiv").jqxDropDownButton({theme:theme, height: 24, width: 24});
			//改动插件
			$("#dropDownButtonArrowsetRoleTypeDiv").jqxDropDownButton({ width: '24px'});
		}
		
		$("#jqxTreeDrop").jqxTree({theme:theme, width: 100});
		$('#jqxTreeDrop').click('select', setRoleType);
	}
}

//设置关注角色
function setAttentionRole(own) {
	var _this = $(own);
    var item = 0;
    if(_this.hasClass('empty-attention-role')) {
    	item = 1;
    }
   var setData = {};
	//获取待合并角色的Id
	setData.viewRoleIds = _this.attr("sval");
	setData.isAttentionRole = item;
	
	$.ajax({
		url: '/viewRole/updateViewRoleAttention',
		type: 'post',
		data : setData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				
				//改变图标
				if (item == 1) {//设置关注角色
					var attention_role = _this.parent().siblings(".operate-attention-a");
					var html = "<a class='float-right attention-role' href='javascript:void(0);' sval='"+ _this.attr("sval") +"' onclick='setAttentionRole(this)' title='取消关注角色'></a>";
					$(attention_role).append(html);
					
					//取消当前图标
					_this.remove();
				}else {
					//取消关注图标
					var empty_role = _this.parent().siblings(".operate-btn-list");
					var html = "<a class='float-right empty-attention-role' href='javascript:void(0);' sval='"+ _this.attr("sval") +"' onclick='setAttentionRole(this)' title='设置关注角色'></a>";
					$(empty_role).prepend(html);
					
					//取消当前图标
					_this.remove();
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//设置演员类型
function setRoleType(event){
		//取出滚动条距离上边距的距离
		var top=$("#viewRoleDiv").scrollTop();
        var item = event.target.innerHTML;
        if(item == "待定") {
        	item = 4;
        } else if (item=='主要演员'){
        	item=1;
        }else if(item=='特约演员'){
        	item=2;
        }else if(item=='群众演员'){
        	item=3;
        }
       var setData = {};
       //var checkedRow = $("#viewRoleListGrid").datagrid("getChecked");
       var checkedRow = $("#viewRoleListGrid input[type='checkbox']:checked");
    	//获取待合并角色的Id
    	var viewRoleIds = "";
    	for(var i = 0; i < checkedRow.length; i++) {
    		var viewRoleId = $(checkedRow[i]).val();
    		viewRoleIds += viewRoleId+",";
    	}
    	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
    	setData.viewRoleIds = viewRoleIds;
    	setData.viewRoleType = item;
    	$.ajax({
    		url: '/viewRole/updateViewRoleTypeBatch',
    		type: 'post',
    		data : setData,
    		datatype: 'json',
    		success: function(response){
    			if(response.success){
    				showSuccessMessage("操作成功");
    				//清空表格
    				$("#viewRoleListGrid").empty();
    				//刷新表格
    				loadViewRoleList(top);
    				//$('#viewRoleListGrid').datagrid('clearChecked');
    				$("#setRoleTypeDiv").jqxDropDownButton('close');
    			}else{
    				showInfoMessage(response.message);
    			}
    		}
    	});
}
//判断场数是否为数字
function isRoleNumber(){
	var minView=$("#queryMinViewCount").val;
	if(isNaN(minView)){
		$("#queryMinViewCount").val("");
	}else{
		$("#queryMinViewCount").val(minView);
	}
}
//创建角色--判断角色名称是否为空
function isAddRoleNameEmpty(){
	if($("#viewRoleName").val()===""){		
		$("li span.tips1").css("display","block");
		$("#viewRoleName").addClass("roleNameEmpty");
	}	
}
//创建角色--判断角色类型是否为空
function isAddRoleTypeEmpty(){
	if($("#viewRoleType").val()===""){
		$("li span.tips2").css("display","block");
	}
}
//创建角色--清楚提示信息
function clearAddNameTips(){
	$("li span.tips1").css("display","none");
	$("#viewRoleName").removeClass("roleNameEmpty");
}
function clearAddTypeTips(){
	$("li span.tips2").css("display","none");
}
//修改角色--判断角色名称是否为空
function isRoleNameEmpty(own){
	var $this = $(own);
	if($this.val() == ""){
		$this.next("span.tips1").css("display", "block");
		$this.addClass("roleNameEmpty");
	}
}
//修改角色--判断角色类型是否为空
function isRoleTypeEmpty(own){
	var $this = $(own);
	if($this.val() == ""){
		$this.next("span.tips2").css("display", "block");
		$this.addClass("roleNameEmpty");
	}
}
//修改角色--清除提示信息
function clearRoleNameTips(own){
	var $this = $(own);
	$this.next("span.tips1").css("display", "none");
	$this.removeClass("roleNameEmpty");
}
function clearRoleTypeTips(own){
	var $this = $(own);
	$this.next("span.tips2").css("display", "none");
}

//统一角色--判断角色名称是否为空
function isMakeRoleNameEmpty(){
	if($("#makeViewRoleName").val()===""){		
		$("span.tips1").css("display","block");
		$("#makeViewRoleName").addClass("roleNameEmpty");
	}	
}
//统一角色--判断角色类型是否为空
function isMakeRoleTypeEmpty(){
	if($("#makeViewRoleType").val()===""){
		$("span.tips2").css("display","block");
	}
}
//统一角色--清除提示信息
function clearMakeNameTips(){
	$("span.tips1").css("display","none");
	$("#makeViewRoleName").removeClass("roleNameEmpty");
}
function clearMakeTypeTips(){
	$("span.tips2").css("display","none");
}
//显示高级查询面板
function openAdvanceSearch() {
	$("#advanceSearchWin").jqxWindow("open");
}
//初始化高级查询窗口
function initAdvanceSearch () {
	$("#advanceSearchWin").jqxWindow({
		theme: theme,
		height: 300,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//高级查询功能--确定--advanceQuery--
function advanceQuery(){
	var viewRoleName = $("#queryViewRoleName").val();
	var viewRoleType = $("#queryViewRoleType").val();
	var minViewCount = $("#queryMinViewCount").val();
	var maxViewCount = $("#queryMaxViewCount").val();
	var minFinished = $("#queryMinFinished").val();
	var maxFinished = $("#queryMaxFinished").val();
	subData.viewRoleName = viewRoleName;
	subData.viewRoleType = viewRoleType;
	subData.minViewCount = minViewCount;
	subData.maxViewCount = maxViewCount;
	subData.minFinished=minFinished;
	subData.maxFinished=maxFinished;
	//清空表格
	$("#viewRoleListGrid").empty();
    //加载高级查询显示的数据
	loadViewRoleList();
	$("#advanceSearchWin").jqxWindow("close");
}
//高级查询功能--取消--cancelQuery--
function cancelQuery(){
	$("#advanceSearchWin").jqxWindow("close");
}
//高级查询功能--清空--clearQuery--、
function clearQuery(){
	$("#queryViewRoleName").val("");
	$("#queryViewRoleType").val("");
	$("#queryMinViewCount").val("");
	$("#queryMaxViewCount").val("");
	$("#queryMinFinished").val("");
	$("#queryMaxFinished").val("");
}
//显示创建角色面
function addViewRoleInfo() {
	$("#roleViewPlayStat").animate({"right": '0px'}, 300).show();
	//默认跳到第一个tab页
	$("#tab_0").addClass("tab_li_current");
	$("#tab_0").siblings().removeClass("tab_li_current");
	$("#playGeneralDetail").hide();
	$(".danju").hide();
	$(".danju0").show();
	//清除所有提示信息
	$("span.tips1").css("display", "none");
	$("span.tips2").css("display", "none");
	
	$("#viewRoleGridId").val("");
	$("#viewRoleGridName").val("");
	$("#modifyActorId").val("");
	
	//加载演员信息
	$("#modifyViewRoleName").val("");
	$("#modifyShortName").val("");
	$("#modifyViewRoleType").val("");
	$("#modifyActorName").val("");
	$("#modifyEnterDate").val("");
	$("#modifyLeaveDate").val("");
	$("#setLeaveButton").addClass("enabled-button");
	$("#leaveRecordDiv").hide();
	$("#leaveRecordTips").show();
	
	getACtorData("");
//	//修改标题
//	$('#viewRoleDetail').jqxWindow('setTitle', '创建角色');
//	//清空内容
//	$("#viewRoleId").val("");
//	$("#viewRoleName").val("");
//	$("#shortName").val("");
//	$("#viewRoleType").val("");
//	$("#actorName").val("");
//	$("#enterDate").val("");
//	$("#leaveDate").val("");	
//	//清空样式
//	$("span.tips1").css("display","none");
//	$("#viewRoleName").removeClass("roleNameEmpty");
//	$("span.tips2").css("display","none");
//	//打开窗口
//	$("#viewRoleDetail").jqxWindow("open");
}
//初始化创建角色窗口--修改角色窗口
function initAddViewRoleInfo(){
	//权限--不能修改
	if(isRoleReadonly){
		return false;
	}
	$("#viewRoleDetail").jqxWindow({
		theme: theme,
		height: 440,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//显示修改角色窗口--共用创建角色窗口
function modifyViewRoleInfo(editrow){
	//清空样式
	$("span.tips1").css("display","none");
	$("#viewRoleName").removeClass("roleNameEmpty");
	$("span.tips2").css("display","none");
	//修改标题
	$('#viewRoleDetail').jqxWindow('setTitle', '修改角色');
	
	//先获取信息
	$("#viewRoleName").val(editrow.viewRoleName);
	$("#shortName").val(editrow.shortName);
	$("#viewRoleType").val(editrow.viewRoleType);
	$("#actorName").val(editrow.actorName);
	$("#enterDate").val(editrow.enterDate);
	$("#leaveDate").val(editrow.leaveDate);
	$("#viewRoleId").val(editrow.viewRoleId);
	$("#actorId").val(editrow.actorId);
	//打开窗口
	$("#viewRoleDetail").jqxWindow("open");
}
//创建角色功能
function createRole(own){
	//取出滚动条距离上边距的距离
	var top=$("#viewRoleDiv").scrollTop();
	//获取值
	var viewRoleId = $("#viewRoleId").val();
	var viewRoleName = $("#viewRoleName").val();
	var shortName = $("#shortName").val();
	var actorId = $("#actorId").val();
	var viewRoleType = $("#viewRoleType").val();
	var actorName = $("#actorName").val();
	var enterDate = $("#enterDate").val();
	var leaveDate = $("#leaveDate").val();
	var wrokHours = $("#modifyWorkHours").val();
	var restHours = $("#modifyRestHours").val();
	//定义存放数据的对象
	var roleData={};
	
	roleData.viewRoleId = viewRoleId;
	roleData.viewRoleName = viewRoleName;
	roleData.shortName = shortName;
	roleData.actorId = actorId;
	roleData.viewRoleType = viewRoleType;
	roleData.actorName = actorName;
	roleData.enterDate = enterDate;
	roleData.leaveDate = leaveDate;
	roleData.workHours = wrokHours;
	roleData.restHours = restHours;
	
	//判断角色名称是否为空
	if(viewRoleName===""){
		$("span.tips1").css("display","block");
		$("#viewRoleName").addClass("roleNameEmpty");
		return false;
	}
	//判断角色类型是否为空
	if(viewRoleType===""){
		$("span.tips2").css("display","block");
		$("#viewRoleType").addClass("roleTypeEmpty");
		return false;
	}
	
	//当选择入组离组时间时，提示必须得填写演员姓名
	if (enterDate != "" || leaveDate != "") {
		//判断是否填写演员姓名
		if (actorName == "") {
			showInfoMessage("请先填写演员姓名，再选择入组离组时间！");
			return false;
		}
	}
	
	//当填写工作时长、休息时长而没有填写演员姓名时，给出错误提示信息
	if (wrokHours != "" || restHours != "") {
		if (actorName == "") {
			showInfoMessage("请先填写演员姓名，再填写工作、休息时长！");
			return false;
		}
	}
	
	$(own).attr("disabled", "disabled");
	$.ajax({
		url: '/viewRole/saveViewRoleInfo',
		type: 'post',
		data: roleData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#viewRoleDetail").jqxWindow("close");
				//清空表格
				$("#viewRoleListGrid").empty();
				//刷新表格
				loadViewRoleList(top);
			}else{
				showErrorMessage(response.message);
			}
			setTimeout(function() {
				$(own).removeAttr("disabled");
			}, 2000);
		}
	});
}
//取消创建角色
function cancelCreateRole(){
	$("#viewRoleDetail").jqxWindow("close");
}

//保存修改后的角色信息
function saveModifyRole(){
	var viewRoleId = $("#viewRoleGridId").val();
	var viewRoleName = $("#modifyViewRoleName").val();
	var shortName = $("#modifyShortName").val();
	var actorId = $("#modifyActorId").val();
	var viewRoleType = $("#modifyViewRoleType").val();
	var actorName = $("#modifyActorName").val();
	var enterDate = $("#modifyEnterDate").val();
	var leaveDate = $("#modifyLeaveDate").val();
	var shootDays = $("#modifyShootDays").val();
	var wrokHours = $("#modifyWorkHours").val();
	var restHours = $("#modifyRestHours").val();
	//定义存放数据的对象
	var roleData={};
	
	roleData.viewRoleId = viewRoleId;
	roleData.viewRoleName = viewRoleName;
	roleData.shortName = shortName;
	roleData.actorId = actorId;
	roleData.viewRoleType = viewRoleType;
	roleData.actorName = actorName;
	roleData.enterDate = enterDate;
	roleData.leaveDate = leaveDate;
	roleData.shootDays = shootDays;
	roleData.workHours = wrokHours;
	roleData.restHours = restHours;
	
	//判断角色名称是否为空
	if(viewRoleName===""){
		$("span.tips1").css("display","block");
		$("#viewRoleName").addClass("roleNameEmpty");
		return false;
	}
	//判断角色类型是否为空
	if(viewRoleType===""){
		$("span.tips2").css("display","block");
		$("#viewRoleType").addClass("roleTypeEmpty");
		return false;
	}
	
	//当选择入组离组时间时，提示必须得填写演员姓名
	if (enterDate != "" || leaveDate != "") {
		//判断是否填写演员姓名
		if (actorName == "") {
			showInfoMessage("请先填写演员姓名，再选择入组离组时间！");
			return false;
		}
	}
	//当填写在组天数时，提示必须得填写演员姓名
	if(shootDays != '') {
		//判断是否填写演员姓名
		if (actorName == "") {
			showInfoMessage("请先填写演员姓名，再填写在组天数！");
			return false;
		}
	}
	
	//当填写工作时长、休息时长而没有填写演员姓名时，给出错误提示信息
	if (wrokHours != "" || restHours != "") {
		if (actorName == "") {
			showInfoMessage("请先填写演员姓名，再填写工作、休息时长！");
			return false;
		}
	}
	
	$.ajax({
		url: '/viewRole/saveViewRoleInfo',
		type: 'post',
		data: roleData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#viewRoleGridName").val($("#modifyViewRoleName").val());
				if(response.actorId == undefined){
					response.actorId = "";
				}
				$("#viewRoleGridId").val(response.viewRoleId);
				$("#modifyActorId").val(response.actorId);
				//刷新请假列表
				if($("#modifyActorId").val() != ""){
					$("#setLeaveButton").removeClass("enabled-button");
					$("#leaveRecordTips").hide();
					$("#leaveRecordDiv").show();
					askForLeave(response.actorId);
				}else{
					$("#setLeaveButton").addClass("enabled-button");
					$("#leaveRecordDiv").hide();
					$("#leaveRecordTips").show();
					$("#modifyEnterDate").val("");
					$("#modifyLeaveDate").val("");
				}
				
				//清空表格
				$("#viewRoleListGrid").empty();
				//刷新表格
				loadViewRoleList();
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}




//显示合并角色名称窗口
function showMakeRoleToOne() {
	//权限--不能修改
	if(isRoleReadonly){
		return false;
	}
	//判断是否选中角色
	//var  checkedRow = $("#viewRoleListGrid").datagrid("getChecked");
	var  checkedRow = $("#viewRoleListGrid input[type='checkbox']:checked");
	if(checkedRow.length<=1){
		showInfoMessage("请选择要合并的角色名称");
	}else{
		//清空内容
		$("#makeViewRoleName").val("");
		$("#makeShortName").val("");
		$("#makeViewRoleType").val("");
		$("#makeActorName").val("");
		$("#makeEnterDate").val("");
		$("#makeLeaveDate").val("");
		//清空样式
		$("span.tips1").css("display","none");
		$("#makeViewRoleName").removeClass("roleNameEmpty");
		$("span.tips2").css("display","none");
		//打开窗口
		$("#MakeRoleToOne").jqxWindow("open");
	}
}
//初始化合并角色名称窗口
function initMakeRoleToOne() {
	$("#MakeRoleToOne").jqxWindow({
		theme: theme,
		height: 455,
		width: 550,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//合并角色名称
function unifiedRoleName(){
	//取出滚动条距离上边距的距离
	var top=$("#viewRoleDiv").scrollTop();
	//判断角色名称是否为空
	if($("#makeViewRoleName").val()===""){
		$("span.tips1").css("display","block");
		$("#makeViewRoleName").addClass("roleNameEmpty");
		return false;
	}
	//判断角色类型是否为空
	if($("#makeViewRoleType").val()===""){
		$("span.tips2").css("display","block");
		return false;
	}
	var unifiedData = {};
	var checkedRow = $("#viewRoleListGrid input[type='checkbox']:checked");
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < checkedRow.length; i++) {
		var viewRoleId = $(checkedRow[i]).val();
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	//获取值
	var viewRoleName = $("#makeViewRoleName").val();
	var shortName = $("#makeShortName").val();
	var viewRoleType = $("#makeViewRoleType").val();
	var actorName = $("#makeActorName").val();
	var enterDate = $("#makeEnterDate").val();
	var leaveDate = $("#makeLeaveDate").val();
	var shootDays = $("#makeShootDays").val();
	
	//如果没有填写演员姓名，但是填写了入组离组时间，和再组天数，则提示错误
	if (actorName == "" || actorName == undefined || actorName == null) {
		if (leaveDate != "" || enterDate != "" || shootDays != "") {
			showInfoMessage("请先填写演员姓名，在填写入组离组时间！");
			return false;
		}
	}
	
	unifiedData.viewRoleIds = viewRoleIds;
	unifiedData.viewRoleName = viewRoleName;
	unifiedData.shortName = shortName;
	unifiedData.viewRoleType = viewRoleType;
	unifiedData.actorName = actorName;
	unifiedData.enterDate = enterDate;
	unifiedData.leaveDate = leaveDate;
	unifiedData.shootDays = shootDays;
	$.ajax({
		url: '/viewRole/makeRolesToOne',
		type: 'post',
		data: unifiedData,
		datatype: "json",
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#MakeRoleToOne").jqxWindow("close");
				//清空列表
				$("#viewRoleListGrid").empty();
				//刷新表格
				loadViewRoleList(top);
				//清空选中
				//$('#viewRoleListGrid').datagrid('clearChecked');
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//取消合并角色名称
function cancelUnifiedRole(){
	$("#MakeRoleToOne").jqxWindow("close");
}
//显示演员请假列表
function showLeaveRecord(editrow) {
	//获取当前日期
	var date = new Date();
	var str = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	$("#leaveStartDate").val(str);
	$("#leaveEndDate").val(str);
	//$("#actorId").val(editrow);
	
	if(editrow == "null"){
		showInfoMessage("请为角色名称选择演员");
		return;
	}else{
		$("#setLeaveRecord").jqxWindow("open");
		askForLeave(editrow);
	}
}
//初始化演员请假列表
function initLeaveRecord(){
	$("#setLeaveRecord").jqxWindow({
		theme: theme,
		height: 420,
		width: 640,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}
//演员请假记录
function askForLeave(actorId){
	//获取当前日期
	    var date = new Date();
	    var str = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	    $("#leaveStartDate").val(str);
	    $("#leaveEndDate").val(str);
	    var subData={};
	    subData.actorId=actorId;
        var source1 =
        {
            url: '/viewRole/queryActorOffRecordList',
            type:'post',
            data: subData,
            dataType : "json",
            datafields: [
				{ name: 'id',type: 'string' },
				{ name: 'actorId',type: 'string'},
				{ name: 'leaveStartDate',type: 'string' },
				{ name: 'leaveEndDate',type: 'string' },
				{ name: 'leaveDays',type: 'string' },
				{ name: 'leaveReason',type: 'string'}
            ],
            beforeprocessing:function(data){
            },
            root:'leaveRecordList',
            processdata: function (data) {
                //查询之前可执行的代码
            }
        };
        var operateRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        	var html = "";
        	if(!isRoleReadonly){
        		html += "<div class='del-leave-div'><a href='javascript:void(0);'";
        		html += " aria-disabled='false' class='' ";
        		html += " onclick='deleteLeaveRecord(\""+rowdata.id+"\")' >删除</a></div>";
        	}
        	return html;
        };
        var dataAdapter1 = new $.jqx.dataAdapter(source1);
        $("#jqxGridLeaveDate").jqxGrid(
        {
        	width: 600,
    		height: 301,
    		source: dataAdapter1,
    		showtoolbar: false,
    		localization: localizationobj,
            columns: [
              { text: '开始时间',  datafield: 'leaveStartDate', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '结束时间', datafield: 'leaveEndDate', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '请假天数', datafield: 'leaveDays', width: '25%',cellsAlign: 'center',align:'center' },
              { text: '操作',width: '25%',align:'center', cellsrenderer: operateRenderer}
            ]
        });
       }

//添加请假记录
function addLeaveDate(own){
	var $this = $(own);
	//取出滚动条距离上边距的距离
	var top=$("#viewRoleDiv").scrollTop();
	//权限设置不可以添加请假记录
	if(isRoleReadonly){
		return false;
	}
	if($("#modifyActorId").val() == ""){
		showInfoMessage("请为角色名称选择演员");
		return false;
	}
	var subData={};
	subData.actorId=$("#modifyActorId").val();
	subData.leaveStartDate=$("#leaveStartDate").val();
	subData.leaveEndDate=$("#leaveEndDate").val();
	
	$.ajax({
		url: '/viewRole/saveActorLeaveRecord',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("添加请假记录成功");
				$("#jqxGridLeaveDate").jqxGrid("updatebounddata");
				//清空表格
				$("#viewRoleListGrid").empty();
				//刷新表格
				loadViewRoleList();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
	//请假设置关闭事件
	$("#setLeaveRecord").on("close", function (event) {
		$("#viewRoleListGrid").empty();
		//刷新表格
		loadViewRoleList(top);
	 });
}
//删除请假记录
function deleteLeaveRecord(id){
	//取出滚动条距离上边距的距离
	var top=$("#viewRoleDiv").scrollTop();
	//权限设置不可以删除请假记录
	if(isRoleReadonly){
		return false;
	}
	popupPromptBox("提示","确定要删除此条请假记录么？",function(){
	var subData = {};
	subData.id = id;
	
		$.ajax({
			url: '/viewRole/deleteActorLeaveRecord',
			type: 'post',
			data: subData,
			datatype: 'json',
			success: function(){
				showSuccessMessage("删除请假记录成功");
				$("#jqxGridLeaveDate").jqxGrid("updatebounddata");
				//清空表格
				$("#viewRoleListGrid").empty();
				//刷新表格
				loadViewRoleList();
			}
		});
	});
	//请假设置关闭事件
	$("#setLeaveRecord").on("close", function (event) {
		$("#viewRoleListGrid").empty();
		//刷新表格
		loadViewRoleList(top);
	 });
}
//显示演员评价
function showRoleEvaluation(editrow){
	//初始化
	$("#actorIdEvaluate").val(editrow);
	$(".grade-star>li").removeClass('full-star');
	$(".grade-star>li").removeClass('half-star');
	$(".star-info").css("display","none");
	$(".grade-df").text("0");
	$("input[type=checkbox]").attr("checked",false);
	$(".grade-py").val("");
	$("#actroEvaluateWindow").jqxWindow("open");	
}
//初始化演员评价
function initRoleEvaluation(){
	$("#actroEvaluateWindow").jqxWindow({
		theme: theme,
		height: 430,
		width: 780,
		resizable: false,
		isModal: true,
		autoOpen: false,
		cancelButton: $("#actorEvaluateCencle"),
		initContent: function(){
			//获取演员评价标签
			$.ajax({
			    	url:'/evaluateManager/queryEvaluateTagList',
			    	type: 'post',
			    	datatype: 'json',
			    	success: function(response){
			    		if(response.success){
			    			var htmlRed="<li></li>";
			    			var htmlBlack="<li></li>";
		                    for(var i=0;i<response.redTagList.length;i++){
		                    	htmlRed+="<li><input type='checkbox' name='best' id='"+response.redTagList[i].tagId+"'><span>"+response.redTagList[i].tagName+"</span></li>";
		                    }
		                    $(".best").html(htmlRed);
		                    for(var j=0;j<response.blackTagList.length;j++){
		                    	htmlBlack+="<li><input type='checkbox' name='bad' id='"+response.blackTagList[j].tagId+"'><span>"+response.blackTagList[j].tagName+"</span></li>";
		                    }
		                    $(".bad").html(htmlBlack);
			    		}
			    	}
			    });
		}
	});
}
//演员评价
function actorEvaluateButton(){
	var actorId = $("#actorIdEvaluate").val();
	var spCodesTemp = "";
    $('.content input[type=checkbox]:checked').each(function(i){
    	  if(0==i){
	        spCodesTemp = $(this).attr("Id");
	      }else{
	        spCodesTemp += (","+$(this).attr("Id"));
	      }
    }); 
    var score=$(".grade-df").text();//得分
    var subData={};
    subData.actorId=actorId;
    subData.score=score;
    subData.evatagIds=spCodesTemp;
    subData.comment=$(".grade-py").val();
    
    $.ajax({
    	url: '/evaluateManager/evaluateActor',
    	type: 'post',
    	data: subData,
    	datatype: 'json',
    	success: function(response){
    		if(response.success){
    			showSuccessMessage("操作成功");
    			$("#actroEvaluateWindow").jqxWindow("close");
    		}else{
    			showErrorMessage("评价失败，请联系管理员！");
    		}
    	}
    });
}
//显示角色场景统计窗口--戏量统计
function showRoleViewPlayStat(editrow) {
	$("#roleViewPlayStat").animate({"right": '0px'}, 300).show();
//	if($("#tab_3_viewGrid").hasClass("tab_li_current")){//条件成立加载角色场景表
//		//加载角色场景表
//		$('#fViewList').attr('src','/viewRole/toRoleViewListPage?roles=' + editrow.viewRoleId +'&viewRoleName=' + editrow.viewRoleName);
//	}
	//默认跳到第一个tab页
	$("#tab_0").addClass("tab_li_current");
	$("#tab_0").siblings().removeClass("tab_li_current");
	$("#playGeneralDetail").hide();
	$(".danju").hide();
	$(".danju0").show();
	//清除所有提示信息
	$("span.tips1").css("display", "none");
	$("span.tips2").css("display", "none");
	
	$("#viewRoleGridId").val(editrow.viewRoleId);
	$("#viewRoleGridName").val(editrow.viewRoleName);
	if(editrow.actorId == null){
		editrow.actorId = "";
	}
	$("#modifyActorId").val(editrow.actorId);
	
	//加载演员信息
	$("#modifyViewRoleName").val(editrow.viewRoleName);
	$("#modifyShortName").val(editrow.shortName);
	$("#modifyViewRoleType").val(editrow.viewRoleType);
	$("#modifyActorName").val(editrow.actorName);
	if(editrow.enterDate == null){
		editrow.enterDate = "";
	}
	$("#modifyEnterDate").val(editrow.enterDate);
	if(editrow.leaveDate == null){
		editrow.leaveDate = "";
	}
	$("#modifyLeaveDate").val(editrow.leaveDate);
	$("#modifyShootDays").val(editrow.shootDays);
	
	//工作时长
	$("#modifyWorkHours").val(editrow.workHours);
	//休息时长
	$("#modifyRestHours").val(editrow.restHours);
	
	//加载请假信息
	if($("#modifyActorId").val() != ""){
		$("#setLeaveButton").removeClass("enabled-button");
		$("#leaveRecordTips").hide();
		$("#leaveRecordDiv").show();
		askForLeave(editrow.actorId);
	}else{
		$("#setLeaveButton").addClass("enabled-button");
		$("#leaveRecordDiv").hide();
		$("#leaveRecordTips").show();
		
	}
	
	getACtorData(editrow.viewRoleId);
}

//请求数据
function getACtorData(viewRoleId){
	//表格加载数据
	$.ajax({
		url: '/viewRole/queryViewCountStatistic',
		type: 'post',
		data: {"viewRoleId":viewRoleId},
		datatype: 'json',
		success: function(response){
			
		  if(response.success){
			//演员参演集数
			$(".actorNumber").html(response.serieStatInfo.roleSeriesNum+"/"+response.serieStatInfo.totalSeriesNum);
			var data = "<table class='table-play-count' width='100%' border='1px'>";
			
			for(var i=0; i<response.viewStatGrpByShootLocationList.length; i++){
				data +="	<tr>";
				data+="		<td rowspan='"+(response.viewStatGrpByShootLocationList[i].viewLocationStatList.length)+"' cellspan='0' align='left' class='table-left-td'>"+response.viewStatGrpByShootLocationList[i].shootLocation;
				data+="		<br><br>场数："+response.viewStatGrpByShootLocationList[i].finishedViewCount+"/"+response.viewStatGrpByShootLocationList[i].viewCount+"&nbsp;&nbsp;&nbsp;&nbsp;页数："+response.viewStatGrpByShootLocationList[i].finishedPageCount+"/"+response.viewStatGrpByShootLocationList[i].totalPageCount;
				data+="		<br>气氛："+response.viewStatGrpByShootLocationList[i].atmosphere+"</td>";
				for(var j=0;j<(response.viewStatGrpByShootLocationList[i].viewLocationStatList.length);j++){
					if(j>0){
					    data+="<tr>";
					}
					data+="<td align='left'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].location+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].finishedViewCount+"/"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].viewCount+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].finishedPageCount+"/"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].totalPageCount+"</td>";
					data+="<td align='center' class='table-right-td'>"+response.viewStatGrpByShootLocationList[i].viewLocationStatList[j].atmosphere+"</td>";
					data+="</tr>";
				}
			}
			data+="</table>";
			$("#playCountDetail").html(data);
			
			var roleViewDetail = "";
			var rolePageDetail = "";
//			roleViewDetail+="&nbsp;"+response.generalStatistic.viewRoleName+"&nbsp;|"+"&nbsp;演员:&nbsp;&nbsp;"+response.generalStatistic.actorName+"&nbsp;|";
			roleViewDetail+="&nbsp;场数:&nbsp;&nbsp;"+response.generalStatistic.finishedTotalViewCount+"/"+response.generalStatistic.totalViewCount+"&nbsp;|";
			roleViewDetail+="&nbsp;主场景数:&nbsp;&nbsp;"+response.generalStatistic.viewLocationCount+"&nbsp;&nbsp;<br>";			
//			rolePageDetail+="&nbsp;"+response.generalStatistic.viewRoleName+"&nbsp;|"+"&nbsp;演员:&nbsp;&nbsp;"+response.generalStatistic.actorName+"&nbsp;|";
			rolePageDetail+="&nbsp;页数:&nbsp;&nbsp;"+response.generalStatistic.finishedTotalPageCount+"/"+response.generalStatistic.totalPageCount+"&nbsp;|";
			rolePageDetail+="&nbsp;主场景数:&nbsp;&nbsp;"+response.generalStatistic.viewLocationCount+"&nbsp;&nbsp;<br>";			
			$("#viewspan").html(roleViewDetail);
			$("#pagespan").html(rolePageDetail);
			
			Number.prototype.toFixed2=function (){
				return parseFloat(this.toString().replace(/(\.\d{2})\d+$/,"$1"));
			};
			/*进度条显示*/
			  /*按场景统计*/
			//场数进度条
			$("#viewCount").html("场数:&nbsp;" + response.generalStatistic.finishedTotalViewCount+"/"+response.generalStatistic.totalViewCount);
			//设置进度条进度的方法
			viewProgressBar($("#viewCountProgress"), $("#viewCountFlag"), response.generalStatistic.totalViewCount, response.generalStatistic.finishedTotalViewCount);
			 //内外进度条
			 $("#staticView").html("内戏:&nbsp;" + response.generalStatistic.finishedInsideViewCount + "/" + response.generalStatistic.insideViewCount);
			 $("#outsideView").html("外戏:&nbsp;" + response.generalStatistic.finishedOutsideViewCount+"/"+response.generalStatistic.outsideViewCount);
			 twoContrastProgressBar($("#neiProgress"), $("#waiProgress"), $("#neiCountFlag"), $("#waiCountFlag"), response.generalStatistic.insideViewCount, response.generalStatistic.outsideViewCount, response.generalStatistic.finishedInsideViewCount, response.generalStatistic.finishedOutsideViewCount);
			 //日夜进度条
			 $("#dayView").html("日戏:&nbsp;" + response.generalStatistic.finishedDayViewCount + "/" + response.generalStatistic.dayViewCount);
			 $("#nightView").html("夜戏:&nbsp;" + response.generalStatistic.finishedNightViewCount+"/"+response.generalStatistic.nightViewCount);
			 twoContrastProgressBar($("#dayProgress"), $("#nightProgress"), $("#dayCountFlag"), $("#nightCountFlag"), response.generalStatistic.dayViewCount, response.generalStatistic.nightViewCount, response.generalStatistic.finishedDayViewCount, response.generalStatistic.finishedNightViewCount);
			 /*//文武戏进度条
			 $("#literateView").html("文戏:&nbsp;" + response.generalStatistic.finishedLiterateViewCount + "/" + response.generalStatistic.literateViewCount);
			 $("#kongfuView").html("武戏:&nbsp;" + response.generalStatistic.finishedKungFuViewCount +"/"+response.generalStatistic.kungFuViewCount);
			 twoContrastProgressBar($("#wenProgress"), $("#wuProgress"), $("#wenCountFlag"), $("#wuCountFlag"), response.generalStatistic.literateViewCount, response.generalStatistic.kungFuViewCount, response.generalStatistic.finishedLiterateViewCount, response.generalStatistic.finishedKungFuViewCount);*/
			 /*进度条显示*/
			    /*按页数统计*/
			 $("#viewPage").html("页数:&nbsp;" + response.generalStatistic.finishedTotalPageCount+"/" + response.generalStatistic.totalPageCount);
			 //页数进度条
			 viewProgressBar($("#viewPageProgress"), $("#viewPageFlag"), response.generalStatistic.totalPageCount, response.generalStatistic.finishedTotalPageCount);
			 /*内外景*/
			 $("#staticPage").html("内戏:&nbsp;" + response.generalStatistic.finishedInsidePageCount + "/" + response.generalStatistic.insidePageCount);
			 $("#outsidePage").html("外戏:&nbsp;" + response.generalStatistic.finishedOutsidePageCount+"/"+response.generalStatistic.outsidePageCount);
			 twoContrastProgressBar ($("#neiPageProgress"), $("#waiPageProgress"), $("#neiPageFlag"), $("#waiPageFlag"), response.generalStatistic.insidePageCount, response.generalStatistic.outsidePageCount, response.generalStatistic.finishedInsidePageCount, response.generalStatistic.finishedOutsidePageCount);
			//日夜进度条
			 $("#dayPage").html("日戏:&nbsp;" + response.generalStatistic.finishedDayPageCount + "/" + response.generalStatistic.dayPageCount);
			 $("#nightPage").html("夜戏:&nbsp;" + response.generalStatistic.finishedNightPageCount+"/"+response.generalStatistic.nightPageCount); 
			 twoContrastProgressBar ($("#dayPageProgress"), $("#nightPageProgress"), $("#dayPageFlag"), $("#nightPageFlag"), response.generalStatistic.dayPageCount, response.generalStatistic.nightPageCount, response.generalStatistic.finishedDayPageCount, response.generalStatistic.finishedNightPageCount);
			/* 文武进度条
			 $("#literatePage").html("文戏:&nbsp;" + response.generalStatistic.finishedLiteratePageCount + "/" + response.generalStatistic.literatePageCount);
			 $("#kongfuPage").html("武戏:&nbsp;" + response.generalStatistic.finishedKungFuPageCount+"/"+response.generalStatistic.kungFuPageCount);
			 twoContrastProgressBar ($("#wenPageProgress"), $("#wuPageProgress"), $("#wenPageFlag"), $("#wuPageFlag"), response.generalStatistic.literatePageCount, response.generalStatistic.kungFuPageCount, response.generalStatistic.finishedLiteratePageCount, response.generalStatistic.finishedKungFuPageCount);*/
			//按场景统计
			var seriesViewCountMap=response.serieStatInfo.seriesViewCountMap;
			var map=seriesViewCountMap;
			//集数
			var collection1=[];
			//场数
			var fields=[];
			for(var key in map){
				collection1.push(key+"集");
				fields.push(map[key]);
			}
			//echar显示数据
			  var echarWidth = $(".danju").width();
			  $("#echarPlayView").width(echarWidth);
			  var myChart1=echarts.init(document.getElementById("echarPlayView"));
			  var option={
					    color: ['#ff7f50'],
					    tooltip : {
					        trigger: 'axis'
					    },
					    xAxis : [
					        {
					        	data : collection1
					        }
					    ],
					    yAxis : [
					        {
					            type : 'value'
					        }
					    ],
					    series : [
					        {
					            name:'场',
					            type:'bar',	
					            data : fields
					        }			        
					    ]
			  };
			  myChart1.setOption(option);
			  
			  //按页数统计
			  var seriesPageCountMap=response.serieStatInfo.seriesPageCountMap;
				var map=seriesPageCountMap;
				//集数
				var collection2=[];
				//页数
				var pages=[];
				for(var key in map){
					collection2.push(key+"集");
					pages.push(map[key]);
				}
				//echar显示数据
				  $("#echarPlayPage").width(echarWidth);
				  var myChart2=echarts.init(document.getElementById("echarPlayPage"));
				  var option={
						    color: ['#ff7f50'],
						    tooltip : {
						        trigger: 'axis'
						    },
						    xAxis : [
						        {
						        	data : collection2
						        }
						    ],
						    yAxis : [
						        {
						            type : 'value'
						        }
						    ],
						    series : [
						        {
						            name:'页',
						            type:'bar',	
						            data : pages
						        }			        
						    ]
				  };
				  myChart2.setOption(option);
			}else{
				showErrorMessage(response.message);
			}
		}
		
	});	 
}

//初始化角色场景统计窗口
function initRoleViewPlayStat(){
	$("#roleViewPlayStat").jqxWindow({
		theme: theme,
		height: 800,
		width: 1000,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			initRoleViewPlayStatClick();
		}
	});
}

//初始化角色场景统计tab、radio click事件
function initRoleViewPlayStatClick(){
	//tab键菜单--角色信息
	$("#tab_0").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").hide();
		$(".danju").hide();
		$(".danju0").show();
	});
	//tab键菜单--戏量统计-分场景
	$("#tab_1").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").show();
		$(".danju").hide();
		$(".danju1").show();
	});
	//tab键菜单--戏量统计-分集
	$("#tab_2").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").show();
		$(".danju").hide();
		$(".danju2").show();
	});
	//tab键菜单--角色场景表
	$("#tab_3_viewGrid").click(function(){
		$(this).addClass("tab_li_current");
		$(this).siblings().removeClass("tab_li_current");
		$("#playGeneralDetail").hide();
		$(".danju").hide();
		$("#viewGridPage").show();
		var viewRoleId = $("#viewRoleGridId").val();
		var viewRoleName = $("#viewRoleGridName").val();
		//初始化加载角色场景表
		$('#fViewList').attr('src','/viewRole/toRoleViewListPage?roles=' + viewRoleId +'&viewRoleName=' + viewRoleName);
	});
	//按场或页统计单选按钮
	$("#radio_1").click(function(){
		$("#echarPlayView").show();
		$("#echarPlayPage").hide();
	});
	$("#radio_2").click(function(){
		$("#echarPlayPage").show();
		$("#echarPlayView").hide();
	});
}

function showRoleTotal(id){
	if(id==1){
		$("#viewspan").show();
		$("#accountTheView").show();
		$("#pagespan").hide();
		$("#accountThePage").hide();
		$("#echarPlayView").show();
		$("#echarPlayPage").hide();
	}else{
		$("#viewspan").hide();
		$("#accountTheView").hide();
		$("#pagespan").show();
		$("#accountThePage").show();
		$("#echarPlayView").hide();
		$("#echarPlayPage").show();
	}
}
//批量删除角色
function deleteViewRoleBatch() {
	//取出滚动条距离上边距的距离
	var top=$("#viewRoleDiv").scrollTop();
	//var checkedRow = $("#viewRoleListGrid").datagrid("getChecked");
	var checkedRow = $("#viewRoleListGrid input[type='checkbox']:checked");
	if(checkedRow.length < 1){
		showInfoMessage("请选择要删除的角色");
		return false;
	}
	var data = {};	
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < checkedRow.length; i++) {
		//获取每个选中的checkbox的sval
		var totalViewCount = $(checkedRow[i]).attr("svalTotalCount");
		var viewRoleType = $(checkedRow[i]).attr("svalRoleType");
		var viewRoleId = $(checkedRow[i]).val();
		
		if (viewRoleType != 4 && totalViewCount != 0) {
			showErrorMessage("所选角色中存在戏量不为0的角色，请将其改为待定角色后，方可删除");
			return false;
		}
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	data.viewRoleIds = viewRoleIds;
	
	$.ajax({
		url: "/viewRole/deleteViewRoleInfoBatch",
		type: "post",
		dataType: "json",
		data: data,
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
			}else{
				showSuccessMessage("删除成功");
				//清空表格
				$('#viewRoleListGrid').empty();
				//刷新表格
				loadViewRoleList(top);
				//清空选中
				//取消全选
				$("#checkAll").prop("checked", false);
				//$('#viewRoleListGrid').datagrid('clearChecked');
			}
		}
	});
}
//显示删除角色场景弹窗
function deleteViewRoleInfo(editrow,own){
	popupPromptBox("提示","确定删除吗？",function(){
		$.ajax({
			url: '/viewRole/deleteViewRoleInfo',
			type: 'post',
			data: {viewRoleId:editrow},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					//刷新表格
					//loadViewRoleList();
					//删除节点
					$(own).parent().parent().parent().parent().remove();
					
					//清空选中
					//$('#viewRoleListGrid').datagrid('clearChecked');
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	});
}
//关闭右侧弹出层
function closeRightPopup(){
	var hiddenWidth = $("#roleViewPlayStat").width();//要隐藏的元素的宽度
	clearInterval(timer);
	$("#roleViewPlayStat").animate({"right": 0 - hiddenWidth}, 300);
	var timer = setTimeout(function(){
		$("#roleViewPlayStat").hide();
	}, 300);
}
//导出角色场景表
function downLoadSence(){
	//var checkedRow = $("#viewRoleListGrid").datagrid("getChecked");
	var checkedRow = $("#viewRoleListGrid input[type='checkbox']:checked");
	if(checkedRow.length < 1){
		showInfoMessage("请选择要导出的角色");
		return false;
	}
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    
    /*实现功能*/
	var fileAddress;
	var data={};	
	//获取待合并角色的Id
	var viewRoleIds = "";
	for(var i = 0; i < checkedRow.length; i++) {
		var viewRoleId = $(checkedRow[i]).val();
		viewRoleIds += viewRoleId+",";
	}
	viewRoleIds = viewRoleIds.substring(0,viewRoleIds.length-1);
	data.viewRoleIds=viewRoleIds;
	
	$.ajax({
		url: '/viewRole/exportRoleViewList',
		type: 'post',
		data: data,
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
//导出角色表
function downLoadRoleTab(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    /*实现功能*/
	var form = $("<form></form>");
	if (subData.viewRoleName != null && subData.viewRoleName != '' &&subData.viewRoleName != undefined) {
		form.append("<input type='hidden' name='viewRoleName' value='"+ subData.viewRoleName +"' >");
	}
	if (subData.viewRoleType != null && subData.viewRoleType != '' &&subData.viewRoleType != undefined) {
		form.append("<input type='hidden' name='viewRoleType' value='"+ subData.viewRoleType +"' >");
	}
	if (subData.minViewCount != null && subData.minViewCount != '' &&subData.minViewCount != undefined) {
		form.append("<input type='hidden' name='minViewCount' value='"+ subData.minViewCount +"' >");
	}
	if (subData.maxViewCount != null && subData.maxViewCount != '' &&subData.maxViewCount != undefined) {
		form.append("<input type='hidden' name='maxViewCount' value='"+ subData.maxViewCount +"' >");
	}
	if (subData.minFinished != null && subData.minFinished != '' &&subData.minFinished != undefined) {
		form.append("<input type='hidden' name='minFinished' value='"+ subData.minFinished +"' >");
	}
	if (subData.maxFinished != null && subData.maxFinished != '' &&subData.maxFinished != undefined) {
		form.append("<input type='hidden' name='maxFinished' value='"+ subData.maxFinished +"' >");
	}
    form.attr("action","/viewRole/queryViewRoleListForExport");
    form.attr("method","post");
    $("body").append(form);
    form.submit();
    form.remove();
    _LoadingHtml.hide();
    $(".opacityAll").hide();
}

//判断是否全选
function isCheckAll(){
	var _tableObj = $("#viewRoleListGrid");
	var checkboxs = _tableObj.find(":checkbox");
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#checkAll").prop("checked",false);
	}else{
		$("#checkAll").prop("checked",true);
	}
}

//全选
function checkAllRoles() {
	//取出当前按钮的状态
	var checked = $("#checkAll").prop("checked");
	if (checked) { //选中
		//设置每一行都选中
		$("#viewRoleListGrid input[type='checkbox']").each(function(i){
			$(this).prop("checked", true);
		 }); 
	}else{
		//全部选中
		$("#viewRoleListGrid input[type='checkbox']").each(function(i){
			$(this).prop("checked", false);
		 }); 
	}
}
//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^0-9.]/g,'', ""));
}
//根据在组时间变化预设拍摄天数
function showShootDays(id){
	var enterDate=$("#"+id+"EnterDate").val();
	var leaveDate=$("#"+id+"LeaveDate").val();
	if(enterDate && leaveDate) {
		$("#"+id+"ShootDays").val(GetDateDiff(enterDate,leaveDate)+1);
	}
}
//获取两个日期间隔天数
function GetDateDiff(startDate,endDate)  
{  
    var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();     
    var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();     
    var dates = Math.abs((startTime - endTime))/(1000*60*60*24);     
    return  dates;    
}