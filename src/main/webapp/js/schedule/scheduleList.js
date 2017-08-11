var crewType = null;
var filter = {};
filter.pagenum = 0;
filter.pagesize = 100;
//var roleArray;//角色
//var gridColumns = [];
var grid;
var popupLayer;//高级查询窗口
var cutRowId = [];
var loadComplete = false;//用于判断是否可以粘贴数据
var hideTdIndex = [];//隐藏列的id
var hideTdText = [];//隐藏列的标题
var shiftFlag = false;//是否按下shift键
var ctrlFlag = false;//是否按下ctrl
var pasteFlag = false;
var scrollHeight = 0;
var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];
$(function(){
	
	//可编辑权限
	if(isScheduleReadonly){
		$("#editPlanTab").hide();
		$("#myContainer").hide();
		$("#viewPlanDetail").attr("src", "/scheduleManager/toScheduleDetailPage");
		$("#viewPlanInfo").show();
		return;
	}
	
	$(document).on("contextmenu", function(event){
		return false;//取消浏览器默认的鼠标选中事件
	});
	$("#bodyTable").on("contextmenu", function(event){
		return false;
	});
	//加载隐藏列的信息(必须保证在渲染列表之前返回数据)
	loadHideColumnInfo();
	//默认加载分组列表
	loadGroupList();
	//获取剧组类型
	getCrewType();
	//初始化移动到分组窗口
	initMoveToWin();
	//初始化筛选窗口
	initSearchWin();
	//初始化导入窗口
	initImportWin();
	$(document).on("click", function(){
		$("#columnsPanel").slideUp(200);
	});
	$(document).on("keydown", function(event){
		if(event.shiftKey){
			shiftFlag = true;
		}
		if(event.ctrlKey){
			ctrlFlag = true;
		}
		
	}).on("keyup", function(){
		shiftFlag = false;
		ctrlFlag = false;
	});
	
});

//只允许输入正整数和负整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\-\d]/g,""));  //清除“数字”和“-”以外的字符
//	$this.val($this.val().replace(".",""));
	$this.val($this.val().replace(/\-{2,}/g,"-"));
	$this.val($this.val().replace("-","$#$").replace(/\-/g,"").replace("$#$","-"));
}

//减少天数
function substractDays(own){
	$(own).attr("disabled", true);
	if($("#adjustDate").val() == ""){
		var value = 0;
		$("#adjustDate").val(value);
	}else{
		var value = parseInt($("#adjustDate").val());
		value-=1;
		$("#adjustDate").val(value);
	}
	$(own).attr("disabled", false);
}
//增加天数
function addDays(own){
	$(own).attr("disabled", true);
	if($("#adjustDate").val() == ""){
		var value = 0;
		$("#adjustDate").val(value);
	}else{
		var value = parseInt($("#adjustDate").val());
		value+=1;
		$("#adjustDate").val(value);
	}
	$(own).attr("disabled", false);
}


//加载隐藏列的信息
function loadHideColumnInfo(){
	$.ajax({
		url: '/cacheManager/queryCacheInfo',
		type: 'post',
		data: {"type": 1},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var result = response.result;
				if(result != null){
					hideTdText = result.content.split(",");
					if(jQuery.inArray("", hideTdText) != -1){
						hideTdText.splice(jQuery.inArray("",hideTdText),1); 
					}
				}
				hideTdIndex = [];
				//加载演员信息
				loadRoleList();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}



//默认加载分组列表
function loadGroupList(){
	$("#groupIframe").attr("src", "/scheduleManager/toScheduleGroupListPage?flag=0");
}

//展开或收起
function extendOrCollapse(own){
	var _frame = $('#groupIframe').contents();
	if($(own).hasClass("extend")){//收起
		$("#leftPanel").removeClass("expend-left-panel");
		$("#leftPanel").addClass("collapse-left-panel");
		$("#rightPanel").removeClass("expend-right-panel");
		$("#rightPanel").addClass("collapse-right-panel");
		_frame.find("#detailGroupInfo").hide();
		_frame.find("#roughGroupInfo").show();
		$(own).removeClass("extend");
		$(own).addClass("collapse");
		groupIframe.window.queryPlanGroupList(1);//调用子窗口的方法
	}else{
		$("#leftPanel").removeClass("collapse-left-panel");
		$("#leftPanel").addClass("expend-left-panel");
		$("#rightPanel").removeClass("collapse-right-panel");
		$("#rightPanel").addClass("expend-right-panel");
		_frame.find("#roughGroupInfo").hide();
		_frame.find("#detailGroupInfo").show();
		$(own).removeClass("collapse");
		$(own).addClass("extend");
		
	}
}

//初始化智能整理窗口
function initIntelligentWin(majorRoleList){
	$("#intelligentWin").jqxWindow({
		theme:theme,  
		width: 550,
		height: 360, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
		initContent: function(){
			if(majorRoleList != null && majorRoleList.length != 0){
				var majorSource = [];
			    for(var i= 0; i< majorRoleList.length; i++){
			    	majorSource.push({text:majorRoleList[i].viewRoleName, value: majorRoleList[i].viewRoleId});
			    }
				//初始化分组下拉框的数据
		        $("#mainRoleListOne").jqxDropDownList({
		            theme:theme,source: majorSource, disabled: true, displayMember: "text", valueMember: "value", width: 'calc(95% - 2px)', height: 30,placeHolder: "演员一"
		                ,dropDownHeight: '150px'
		        });
		        $("#mainRoleListTwo").jqxDropDownList({
		            theme:theme,source: majorSource, disabled: true, displayMember: "text", valueMember: "value", width: 'calc(95% - 2px)', height: 30,placeHolder: "演员二"
		                ,dropDownHeight: '150px'
		        });
			}
			$("#intelligentWin").on("close", function(){
				$("#mainRoleListOne").jqxDropDownList('clearSelection');
				$("#mainRoleListTwo").jqxDropDownList('clearSelection');
				$("#mainRoleListOne").jqxDropDownList("disabled", true);
				$("#mainRoleListTwo").jqxDropDownList("disabled", true);
				$(".finish-condition").find("select").val("");
			});
			
			
		}
   });
}

//显示智能整理窗口
function showIntelligentWin(){
	$("#intelligentWin").jqxWindow("open");
}
//取消
function cancelBtn(){
	$(".finish-condition").find("select").val("");
	$("#intelligentWin").jqxWindow("close");
}

//清空智能排期窗口内容
function clearIntellWin(){
	$(".finish-condition").find("select").val("");
	$("#mainRoleListOne").jqxDropDownList('clearSelection');
	$("#mainRoleListTwo").jqxDropDownList('clearSelection');
}


//智能整理
function confirmIntelligent(){
	var subData = {};
	var isHasRole = false;
	var firstValue = $("#conditionFirst").val();
	var secondValue = $("#conditionSecond").val();
	subData.conditionOne = firstValue;
	subData.conditionTwo = secondValue;
	if(firstValue == "viewRole" || secondValue == "viewRole"){
		isHasRole = true;
	}
	if(isHasRole){
		var viewRole = "";
		var roleOne = $("#mainRoleListOne").val();
		var roleTwo = $("#mainRoleListTwo").val();
		if(roleOne == "" && roleTwo == ""){
			showInfoMessage("至少选择一个演员");
			return;
		}
		if(roleOne != ""){
			viewRole += roleOne;
		}
		if(roleTwo != ""){
			if(viewRole != ""){
				viewRole += "," + roleTwo;
			}else{
				viewRole += roleTwo;
			}
		}
		subData.viewRole = viewRole;
	}
	popupPromptBox("提示", "该操作会删除未锁定的分组，确定要执行吗", function(){
		grid.loading();
		$.ajax({
			url: '/scheduleManager/autoSchedule',
			type: 'post',
			data: subData,
			datatype: 'json',
			success: function(response){
				$("#loadingDataDiv").hide();
			    $(".opacityAll").hide();
				if(response.success){
					showSuccessMessage("智能整理完成");
					groupIframe.window.queryPlanGroupList();//调用子窗口的方法--刷新分组列表
					filter = {};
					filter.pagesize = 100;
					grid.setFilter(filter);
					grid.goToPage(0);
					$("#intelligentWin").jqxWindow("close");
				}else{
					showErrorMessage(response.message);
				}
			}
		});
		
	});
	
}

//选择实景地
function setConditionFirst(own){
	if($(own).val() == "" && $("#conditionSecond").val() == "") {
		$("#mainRoleListOne").jqxDropDownList({ disabled: true });
		$("#mainRoleListTwo").jqxDropDownList({ disabled: true });
		$("#mainRoleListOne").jqxDropDownList('clearSelection');
		$("#mainRoleListTwo").jqxDropDownList('clearSelection');
		return;
	}
	if($(own).val() == $("#conditionSecond").val()){
		showInfoMessage("条件不能相同");
		$(own).val("");
		return;
	}
	if($(own).val() == "viewRole"){
		$("#mainRoleListOne").jqxDropDownList({ disabled: false });
		$("#mainRoleListTwo").jqxDropDownList({ disabled: false });
	}else{
		if($("#conditionSecond").val() != "viewRole"){
			$("#mainRoleListOne").jqxDropDownList({ disabled: true });
			$("#mainRoleListTwo").jqxDropDownList({ disabled: true });
		}
	}
}
//选择实景地域
function setConditionSecond(own){
	if($(own).val() == "" && $("#conditionFirst").val() == "") {
		$("#mainRoleListOne").jqxDropDownList({ disabled: true });
		$("#mainRoleListTwo").jqxDropDownList({ disabled: true });
		$("#mainRoleListOne").jqxDropDownList('clearSelection');
		$("#mainRoleListTwo").jqxDropDownList('clearSelection');
		return;
	}
	if($(own).val() == $("#conditionFirst").val()){
		showInfoMessage("条件不能相同");
		$(own).val("");
		return;
	}
	if($(own).val() == "viewRole"){
		$("#mainRoleListOne").jqxDropDownList({ disabled: false });
		$("#mainRoleListTwo").jqxDropDownList({ disabled: false });
	}else{
		if($("#conditionFirst").val() != "viewRole"){
			$("#mainRoleListOne").jqxDropDownList({ disabled: true });
			$("#mainRoleListTwo").jqxDropDownList({ disabled: true });
		}
	}
}

//选择演员一
function mainRoleOne(event){
	var args = event.args;
	var item = args.item;
	if(item.value != ""){
		if(item.value == $("#mainRoleListTwo").val()){
			showInfoMessage("演员姓名不能重复");
			$("#mainRoleListOne").jqxDropDownList('clearSelection');
			return;
		}
	}
}
//选择演员二
function mainRoleTwo(event){
	var args = event.args;
	var item = args.item;
	if(item.value != ""){
		if(item.value == $("#mainRoleListOne").val()){
			showInfoMessage("演员姓名不能重复");
			$("#mainRoleListTwo").jqxDropDownList('clearSelection');
			return;
		}
	}
}






//初始化批量修改窗口
function initRepeatWin(groupList){
	var groupSource = [];
	var flag = true;
    for(var key in groupList){
    	if(flag){
    		groupSource.push({text: "不分组", value: key});
    		flag = false;
    	}else{
    		groupSource.push({text: groupList[key], value: key});
    	}
    }
    groupSource.push({text:'新增组',value:'99'});
	$("#repeatModify").jqxWindow({
		theme:theme,  
		width: 480,
		height: 360, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
		initContent: function(){
			
			//初始化分组下拉框的数据
	        $("#setShootGroup").jqxDropDownList({
	            theme:theme,selectedIndex:0,source: groupSource, disabled: true, displayMember: "text", valueMember: "value", width: '100%', height: 30,placeHolder: ""
	                ,dropDownHeight: getHeight(groupSource)
	        });
	        $("#setShootGroup").jqxDropDownList('selectIndex', 0);
	        $("#repeatModify").on("close", function(){
	        	$("#planDateEnable").prop("checked", false);
	        	$("#planGroupEnable").prop("checked", false);
	        	$("input[name=setPlanDate]").attr("disabled", true);
	        	$("input[name=setPlanDate]:checked").prop("checked", false);
	        	$("#shootDate").val("");
	        	$("#adjustDate").val("");
	        	$("#setShootGroup").jqxDropDownList('selectIndex', 0);
	        	$("button[class^='date-icon']").attr("disabled", true);
	        });
	        $("#repeatModify").on("open", function(){//计算选中日期范围和选中的组别
	        	var selectGroupArr = [];
	    		var group = $("#setShootGroup").jqxDropDownList('getSelectedItem').label;
	    		$("#selectplanGroup").text(group);
	    		$("#setShootGroup").jqxDropDownList({ disabled: true }); 
	    		var selectIds = grid.getSelectIds().split(",");
	    		var minDate;
	    		var maxDate;
	    		if(selectIds.length == 1){
	    			minDate = $("tr[id="+selectIds[0]+ "]").find("td[cellid=1]").text();
	    			if(minDate == ""){
	    				$("#dateRange").text("");
	    				return;
	    			}
	    			var rDate = new Date(minDate);  
	    		    var year = rDate.getFullYear();  
	    		    var month = rDate.getMonth() + 1;  
	    		    if (month < 10) month = "0" + month;  
	    		    var date = rDate.getDate();  
	    		    if (date < 10) date = "0" + date;
	    		    var startDate = year +"-"+ month +"-"+ date;
	    		    $("#dateRange").text(startDate);
	    		    var selectGroup = $("tr[id="+selectIds[0]+ "]").find("td[cellid=2]").text();
	    		    $("#selectplanGroup").text(selectGroup);
	    		    return;
	    		}else{
	    			for(var i=0; i< selectIds.length; i++){
	    				var selectGroup = $("tr[id="+selectIds[i]+ "]").find("td[cellid=2]").text();
	    				if(selectGroup != ""){
	    					if(jQuery.inArray(selectGroup, selectGroupArr) == -1){
		    					selectGroupArr.push(selectGroup);
		    				}
	    				}
	    				var lastDate = $("tr[id="+selectIds[i]+ "]").find("td[cellid=1]").text();
	    				if(lastDate != ""){
	    					 var arys1=lastDate.split('-');      
	    				     var sdate=new Date(arys1[0],parseInt(arys1[1]-1),arys1[2]);      
	    				     if(minDate == undefined){
	    						minDate = sdate;
	    						maxDate = sdate;
	    					 }
	    				     if(minDate < sdate){
	    				    	 if(maxDate < sdate){
	    				    		 maxDate = sdate;
	    				    	 }
	    				     }
	    				     if(minDate > sdate){
	    				    	 if(minDate > sdate){
	    				    		 minDate = sdate;
	    				    	 }
	    				     }
	    				     if(minDate > maxDate){
	    				    	 var dd = maxDate;
	    				    	 maxDate = minDate;
	    				    	 minDate = dd;
	    				     }
	    				     
	    				     
	    				}
	    			}
	    			if(selectGroupArr.length == 0){
	    				$("#selectplanGroup").text("");
	    			}else{
	    				$("#selectplanGroup").text(selectGroupArr.join("、"));
	    			}
	    			if(minDate == undefined){
	    				$("#dateRange").text("");
	    				return;
	    			}
	    			var rDate = new Date(minDate);  
	    		    var year = rDate.getFullYear();  
	    		    var month = rDate.getMonth() + 1;  
	    		    if (month < 10) month = "0" + month;  
	    		    var date = rDate.getDate();  
	    		    if (date < 10) date = "0" + date;
	    		    var startDate = year +"-"+ month +"-" + date;
	    		    var rDate1 = new Date(maxDate);
	    		    var year1 = rDate1.getFullYear();  
	    		    var month1 = rDate1.getMonth() + 1; 
	    		    if (month1 < 10) month1 = "0" + month1;  
	    		    var date1 = rDate1.getDate();  
	    		    if (date1 < 10) date1 = "0" + date1;
	    		    var endDate = year1 +"-"+ month1 +"-"+ date1;
	    		    if(minDate == maxDate){
	    		    	 $("#dateRange").text(startDate);
	    		    	 return;
	    		    }
	    		    $("#dateRange").text(startDate +"至" +endDate);
	    		}
	        });
		}
   });
}
//改变计划组别
function changeNoticeGroup(event){
	var args = event.args;
    if (args) {
        var index = args.index;
        var item = args.item;
        var group;
        //选择新增分组
        if(item.value=="99"){
            if(index>25){
                showErrorMessage("目前最多选择到Z组");
                $("#setShootGroup").jqxDropDownList("selectIndex",index-1);
                group = $("#setShootGroup").jqxDropDownList('getSelectedItem').label;
                return;
            }
            $.ajax({
                url:"/shootGroupManager/saveGroup",
                type:"post",
                dataType:"json",
                data:{groupName:groupArray[index-1].text},
                success:function(data){
                    if(!data.success){
                        showErrorMessage(data.message);
                        $("#setShootGroup").jqxDropDownList("selectIndex",0);
                    }
                    $("#setShootGroup").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                    $("#setShootGroup").jqxDropDownList("selectIndex",index);
                }
            });
        }else{
        	 group= $("#setShootGroup").jqxDropDownList('getSelectedItem').label;
        }
        $("#selectplanGroup").text(group);
    }
}

//显示批量修改窗口
function showRepeatWin(){
	if(grid.getSelectIds()==0){
        showErrorMessage("请选择场次");
        return;
    }
	$("#repeatModify").jqxWindow("open");
}

//设置计划日期可编辑
function isplanDateEnable(own){
	if($(own).is(":checked")){
		$("#shootDate").attr("disabled", true);
		$("input[name=setPlanDate]").attr("disabled", false);
	}else{
		$("#shootDate").attr("disabled", true);
		$("input[name=setPlanDate]:checked").prop("checked", false);
		$("input[name=setPlanDate]").attr("disabled", true);
	}
}
//设置计划分组可编辑
function isPlanGroupEnable(own){
	if($(own).is(":checked")){
		$("#setShootGroup").jqxDropDownList({ disabled: false });
	}else{
		$("#setShootGroup").jqxDropDownList({ disabled: true });
	}
}
//设置日期调整是否可用
function isShootDate(own){
	$("#shootDate").attr("disabled", false);
	$("#adjustDate").attr("disabled", true);
	$("button[class^='date-icon']").attr("disabled", true);
}
function isAdjustDate(own){
	$("#shootDate").attr("disabled", true);
	$("#adjustDate").attr("disabled", false);
	$("button[class^='date-icon']").attr("disabled", false);
}





//批量设置
function confirmRepeatSet(){
	var subData = {};
	if($("#planDateEnable").is(":checked")){
		var value = $("input[name=setPlanDate]:checked").val();
		if(value == 0){
			subData.planDate = $("#shootDate").val();
		}
		if(value == 1){
			subData.dayNum = parseInt($("#adjustDate").val());
		}
		
	}
	if($("#planGroupEnable").is(":checked")){
		subData.planGroupId = $("#setShootGroup").val();
	}
	var selectId = grid.getSelectIds();
	subData.viewIds = selectId;
//	if(!$("#planDateEnable").is(":checked") && !$("#planGroupEnable").is(":checked")){
//		showInfoMessage("请选择日期或者组别");
//		return;
//	}
	if(!subData.planDate && !subData.dayNum && !subData.planGroupId){
		if(subData.planDate == "" || subData.dayNum == "" || subData.planGroupId == ""){
			
		}else{
			showInfoMessage("请选择日期或者组别");
			return;
		}
	}
	$.ajax({
		url: '/scheduleManager/setScheduleDateAndGroup',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				selectIds = selectId.split(",");
				for(var i= 0; i< selectIds.length; i++){
					if(subData.planDate != undefined && subData.planDate != null){
						$("tr[id="+selectIds[i]+"]").find("td[cellid=1]").find("p").text(subData.planDate);
					}
					if(subData.dayNum != undefined && subData.dayNum != null){
						var date = $("tr[id="+selectIds[i]+"]").find("td[cellid=1]").text();
						if(date != ""){
							var day = parseInt($("#adjustDate").val());
							var newDate = calculateDay(date, day);
							$("tr[id="+selectIds[i]+"]").find("td[cellid=1]").find("p").text(newDate);
						}
					}
					if(subData.planGroupId != undefined && subData.planGroupId != null){
						var planGroup = $("#setShootGroup").val();
						if(planGroup == "1"){
							$("tr[id="+selectIds[i]+"]").find("td[cellid=2]").find("p").text("");
						}else{
							var groupName =  $("#setShootGroup").jqxDropDownList('getItem', planGroup ).label;
							$("tr[id="+selectIds[i]+"]").find("td[cellid=2]").find("p").text(groupName);
						}
						
					}
				}
				//刷新分组列表数据
				refrushGroupData();
				$("#repeatModify").jqxWindow("close");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//计算提前或者延后天数的日期
function calculateDay(dateTemp, days){//计算日期
	var dateTemp = dateTemp.split("-");  
    var nDate = new Date(dateTemp[1] + '-' + dateTemp[2] + '-' + dateTemp[0]); //转换为MM-DD-YYYY格式 
    var millSeconds;
    if(days < 0){
    	days = Math.abs(days);
    	millSeconds = Math.abs(nDate) - (days * 24 * 60 * 60 * 1000);  
    }else{
    	millSeconds = Math.abs(nDate) + (days * 24 * 60 * 60 * 1000);  
    }
    var rDate = new Date(millSeconds);  
    var year = rDate.getFullYear();  
    var month = rDate.getMonth() + 1;  
    if (month < 10) month = "0" + month;  
    var date = rDate.getDate();  
    if (date < 10) date = "0" + date;  
    return (year + "-" + month + "-" + date);
}
//刷新分组列表数据
function refrushGroupData(){
	$.ajax({
		url: '/scheduleManager/queryScheduleGroupList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var scheduleGroupList = response.scheduleGroupList;
				var _frame = $('#groupIframe').contents();
				var grouopDetailCon = _frame.find("#groupDetailCon");
				if(scheduleGroupList != null && scheduleGroupList.length != 0){
					var dateStr;
					for(var i= 0; i< scheduleGroupList.length; i++){
						var id = scheduleGroupList[i].groupId;
						if(scheduleGroupList[i].startDate == null || scheduleGroupList[i].endDate == null){
							dateStr = "";
						}else{
							dateStr = scheduleGroupList[i].startDate +'~'+ scheduleGroupList[i].endDate;
						}
						grouopDetailCon.find("li[gid="+ id +"]").find("span.date-period").text(dateStr);
					}
				}
			}
		}
	});
}





//关闭批量修改窗口
function cancelRepeatSet(){
	$("#repeatModify").jqxWindow("close");
}

//初始化移动到分组窗口
function initMoveToWin(){
	$("#moveToWin").jqxWindow({
		theme:theme,  
		width: 400,
		height: 360, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: false,
		isModal: true,
		initContent: function(){
			$("#moveToWin").on("open", function(){
				//获取分组名称列表
				getGroupName();
			});
		}
   });
}
//获取分组名称列表
function getGroupName(){
	$.ajax({
		url: '/scheduleManager/queryScheduleGroupList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var scheduleGroupList = response.scheduleGroupList;
				if(scheduleGroupList != null && scheduleGroupList.length != 0){
					var groupSource = [];
					for(var i= 0; i< scheduleGroupList.length; i++){
						groupSource.push({text:scheduleGroupList[i].groupName, value: scheduleGroupList[i].groupId});
					}
					//初始化分组下拉框的数据
					$("#planGroupList").jqxDropDownList({
			            theme:theme,source: groupSource, displayMember: "text", valueMember: "value", width: 'calc(100% - 85px)', height: 30,placeHolder: ""
			                ,dropDownHeight: '150px'
			        });
			        $("#planGroupList").jqxDropDownList("selectIndex",0);
				}
			}
		}
	});
}


//显示移动到分组窗口
function showMoveToWin(){
	if(grid.getSelectIds()==0){
        showErrorMessage("请选择场次");
        return;
    }
	$("#moveToWin").jqxWindow("open");
}
//取消移动到其他分组
function closeMoveToWin(){
	$("#moveToWin").jqxWindow("close");
}


//移动到分组
function moveToGroup(){
	var groupId = $("#planGroupList").val();
	var selectId = grid.getSelectIds();
	$.ajax({
		url: '/scheduleManager/setViewScheduleGroup',
		type: 'post',
		data: {"viewIds": selectId, "groupId": groupId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$(".count-flag").text("0");
				groupIframe.window.queryPlanGroupList();//重新刷新分组列表
//				var selectIds = selectId.split(",");
//				var moveTrs = [];
//				for(var i= 0; i< selectIds.length; i++){
//					var tr = $("tr[id="+ selectIds[i] +"]").clone(true);
//					moveTrs.push(tr);
//				}
//				var selectTr = $("#bodyTable").find("tr.select");
//				selectTr.remove();
//				var groupLastItem = $("#bodyTable").find("tr[groupid=" + groupId + "]:last-child");
//				if(groupLastItem != undefined){//只有显示全部场景的时候去手动移动并赋值组id
//					groupLastItem.after(moveTrs);
//					$.each(moveTrs, function(){
//						$(this).attr("groupid", groupId);
//					});
//				}
//				grid.upadateIndex();
//				moveTrs.removeClass("select");
				grid.setFilter(filter);
				grid.goToPage(0);
				$("#moveToWin").jqxWindow("close");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}



//加载高级查询窗口
function superSearchViewInfo(){
	//如果是第一次打开高级查询窗口则需要初始化查询窗口,若不是,只需要清空拍摄地点并重新加载拍摄地点的查询条件即可
	if(typeof(popupLayer)=="undefined"){
    	
    	loadSearchDIV();
    	popupLayer.jqxWindow('open');
    }else{
    	loadSearchCondition();
    	popupLayer.jqxWindow('open');
    }
}
//初始化筛选窗口
function initSearchWin(){
	var screenWidth = window.screen.width;
	var winHeight;//弹窗高度
	if(screenWidth >= 1366 && screenWidth <= 1399){
		winHeight = 550;
	}else{
		winHeight = 710;
	}
	 popupLayer = $('#searchWindow').jqxWindow({
		theme:theme,  
	    width: 840,
	    height: winHeight, 
	    maxHeight: 800,
	    autoOpen: false,
	    cancelButton: $('#closeSearchSubmit'),
	    isModal: true,
	    resizable: false,
	    initContent: function () {
	        //加载查询条件
	        loadSearchCondition(false);
	        
	        showDifferentSearchData();
	        
		    $("#searchSubmit").jqxButton({
	           width:80,
	           height: 25
	        });
		    
		    $("#closeSearchSubmit").jqxButton({
	           width:80,
	           height: 25
	        });
		    
		    $("#clearSearchButton").jqxButton({
		       width:80,
	           height: 25
		    });
		    
	        $("#seriesViewNos").jqxInput({
	            placeHolder: '输入范例：1-1,1-2,1-3'
	        });
	        
	        $("#viewNos").jqxInput({
	            placeHolder: '输入范例：1,2,3'
	        });
	        
	        $("#viewRemark, #mainContent").jqxInput({
	            theme: theme
	        });
	
		    $('.selectpicker').selectpicker({
	            size: 10
	        });
	        
		    //jqxFormattedInput({ width: 250, height: 25, radix: "decimal", decimalNotation: "exponential", value: "330000" });
		    $("#startSeriesNo").jqxInput({theme:theme, placeHolder: "集", value: '' });
		    $("#startSeriesNo, #endSeriesNo").on("keyup", function() {
	            if (isNaN($(this).val())) {
			        $(this).val("");
			    }
		    });
		    
	        $("#startViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
	        $("#endSeriesNo").jqxInput({theme:theme,placeHolder: "集", value: '' });
	        $("#endViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
	        
	        //下拉控件中当选择空的时候自动取消勾选其他选项，当选择其他选项时，自动取消勾选空选项
	        $('.selectpicker').on('change', function(event) {
	            var value = event.target.value;
	            var eventId = event.target.id;  //获取当前select控件id
	            
	            //var prevSelectedValue = $("#selectpickerPreValue").val();   //select控件之前选中的值
	            var prevSelectedValue = $("#"+eventId).parent().find(".preValue").val();  
	            if (prevSelectedValue == "blank") {
	                $("#"+ eventId).find('option').eq(0).prop('selected', false).removeAttr('selected');
	                $("#"+ eventId).selectpicker('render');
	                
	                //$("#selectpickerPreValue").val($("#"+ eventId).val());
	                $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
	                return false;
	            }
	            
	            if (value == "blank") {
	                $("#"+ eventId).selectpicker('deselectAll');    //首先取消所有选中
	                
	                //为[空]值执行选中事件 setSelected
	                $("#"+ eventId).find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
	                $("#"+ eventId).selectpicker('render');
	            }
	            $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
	        });
	        
	        //对选择框设置鼠标移动时的显示样式
	        $('.searchUl').on('mouseover', function(event) {
	            if ($(this).find("li").find("select").val() != null && $(this).find("li").find("select").val() != undefined) {
	                $(this).find("li").find(".clearSelection").show();
	            }
	        });
	        
	        $('.searchUl').on('mouseout', function(event) {
	            $(this).find("li").find(".clearSelection").hide();
	        });
	        
	        $(".clearSelection").on('click', function() {
	            var id = $(this).siblings(".selectpicker").attr("id");
	            if (id == "majorRoleSelect") {
	                //隐藏单选按钮
	                $("#anyOneAppear").hide();
	                $("#noOneAppear").hide();
	                $("#everyOneAppear").hide();
	                $("#notEvenyOneAppear").hide();
	            }
	            $(this).siblings(".selectpicker").selectpicker('deselectAll');
	        });
		}
	});
}

function loadSearchCondition(flag, selectedLocation){

	//查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
    $.ajax({
        url:"/viewManager/loadAdvanceSerachData",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
            if(data.success) {
            	//只动态更新拍摄地
        		var viewFilterDto = data.viewFilterDto;
                var atmosphereList = viewFilterDto.atmosphereList;
                var shootStatusList = viewFilterDto.shootStatusList;
                var siteList = viewFilterDto.siteList;
                var firstLocationList = viewFilterDto.firstLocationList;
                var secondLocationList = viewFilterDto.secondLocationList;
                var thirdLocationList = viewFilterDto.thirdLocationList;
                var majorRoleList = viewFilterDto.majorRoleList;
                var guestRoleList = viewFilterDto.guestRoleList;
                var massesRoleList = viewFilterDto.massesRoleList;
                var commonPropList = viewFilterDto.commonPropList;
                var specialPropList  = viewFilterDto.specialPropList;
                var clotheList = viewFilterDto.clotheList;
                var makeupList = viewFilterDto.makeupList;
//                var shootLocationList = viewFilterDto.shootLocationList;
                var shootLocationList = viewFilterDto.shootLocationRegionList;
                var advertInfoList = viewFilterDto.advertInfoList;
                var specialRemindList = viewFilterDto.specialRemindList;
                

                $("#atmosphereSelect").empty();
                $("#atmosphereSelect").append("<option value='blank'>[空]</option>");
                for (var atm in atmosphereList) {
                    $("#atmosphereSelect").append("<option value="+ atm + ">" + atmosphereList[atm] + "</option>");
                }
                if (filter.atmosphere == "blank") {
                	$("#atmosphereSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#atmosphereSelect").selectpicker('render');
                } else if (filter.atmosphere) {
                	$("#atmosphereSelect").selectpicker("val", filter.atmosphere.split(","));
                }
                $("#atmosphereSelect").selectpicker('refresh');
                
                
                $("#siteSelect").empty();
                $("#siteSelect").append("<option value='blank'>[空]</option>");
                for (var site in siteList) {
                    $("#siteSelect").append("<option value="+ siteList[site] + ">" + siteList[site] + "</option>");
                }
                if (filter.site == "blank") {
                	$("#siteSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#siteSelect").selectpicker('render');
                } else if (filter.site) {
                	 $("#siteSelect").selectpicker('val', filter.site.split(','));
                }
                $("#siteSelect").selectpicker('refresh');
                
                
                $("#specialRemindSelect").empty();
                $("#specialRemindSelect").append("<option value='blank'>[空]</option>");
                for (var specialRemind in specialRemindList) {
                    $("#specialRemindSelect").append("<option value="+ specialRemindList[specialRemind] + ">" + specialRemindList[specialRemind] + "</option>");
                }
                if (filter.specialRemind == "blank") {
                	$("#specialRemindSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#specialRemindSelect").selectpicker('render');
                } else if (filter.specialRemind) {
                	$("#specialRemindSelect").selectpicker('val', filter.specialRemind.split(","));
                }
                $("#specialRemindSelect").selectpicker('refresh');
                

                $("#shootStatusSelect").empty();
                for (var shotstatus in shootStatusList) {
                    $("#shootStatusSelect").append("<option value="+ shotstatus + ">" + shootStatusList[shotstatus] + "</option>");
                }
                if (filter.shootStatus) {
                	$("#shootStatusSelect").selectpicker('val', filter.shootStatus.split(","));
                }
                $("#shootStatusSelect").selectpicker('refresh');
                

                $("#advertInfoSelect").empty();
                $("#advertInfoSelect").append("<option value='blank'>[空]</option>");
                for (var advert in advertInfoList) {
                    $("#advertInfoSelect").append("<option value="+ advert + ">" + advertInfoList[advert] + "</option>");
                }
                if (filter.advert == "blank") {
                	$("#advertInfoSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#advertInfoSelect").selectpicker('render');
                } else if (filter.advert) {
                	$("#advertInfoSelect").selectpicker('val', filter.advert.split(","))
                }
                $("#advertInfoSelect").selectpicker('refresh');
                

                $("#shootLocationSelect").empty();
                $("#shootLocationSelect").append("<option value='blank'>[空]</option>");
//                for (var shotLocation in shootLocationList) {
//                    $("#shootLocationSelect").append("<option value="+ shotLocation + ">" + shootLocationList[shotLocation] + "</option>");
//                }
//                if (filter.shootLocation == "blank") {
//                	$("#shootLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
//                    $("#shootLocationSelect").selectpicker('render');
//                } else if (filter.shootLocation) {
//                	$("#shootLocationSelect").selectpicker('val', filter.shootLocation.split(","))
//                }
//                $("#shootLocationSelect").selectpicker('refresh');
                
                for (var i= 0; i<shootLocationList.length; i++) {
                    $("#shootLocationSelect").append("<option value="+ shootLocationList[i].shootLocationId + ">" + shootLocationList[i].shootLocationRegion + "</option>");
                }
                if (filter.shootLocation == "blank") {
                	$("#shootLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#shootLocationSelect").selectpicker('render');
                } else if (filter.shootLocation) {
                	$("#shootLocationSelect").selectpicker('val', filter.shootLocation.split(","))
                }
                $("#shootLocationSelect").selectpicker('refresh');

                $("#firstLocationSelect").empty();
                $("#firstLocationSelect").append("<option value='blank'>[空]</option>");
                for (var fLocation in firstLocationList) {
                    $("#firstLocationSelect").append("<option value="+ firstLocationList[fLocation] + ">" + firstLocationList[fLocation] + "</option>");
                }
                if (filter.major == "blank") {
                	$("#firstLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#firstLocationSelect").selectpicker('render');
                } else if (filter.major) {
                	$("#firstLocationSelect").selectpicker('val', filter.major.split(','));
                }
                $("#firstLocationSelect").selectpicker('refresh');
                

                $("#secondLocationSelect").empty();
                $("#secondLocationSelect").append("<option value='blank'>[空]</option>");
                for (var sLocation in secondLocationList) {
                    $("#secondLocationSelect").append("<option value="+ secondLocationList[sLocation] + ">" + secondLocationList[sLocation] + "</option>");
                }
                if (filter.minor == "blank") {
                	$("#secondLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#secondLocationSelect").selectpicker('render');
                } else if (filter.minor) {
                	$("#secondLocationSelect").selectpicker('val', filter.minor.split(","));
                }
                $("#secondLocationSelect").selectpicker('refresh');
                
                $("#thirdLocationSelect").empty();
                $("#thirdLocationSelect").append("<option value='blank'>[空]</option>");
                for (var tLocation in thirdLocationList) {
                    $("#thirdLocationSelect").append("<option value="+ thirdLocationList[tLocation] + ">" + thirdLocationList[tLocation] + "</option>");
                }
                if (filter.thirdLevel == "blank") {
                	$("#thirdLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#thirdLocationSelect").selectpicker('render');
                } else if (filter.thirdLevel) {
                	$("#thirdLocationSelect").selectpicker('val', filter.thirdLevel.split(","));
                }
                $("#thirdLocationSelect").selectpicker('refresh');
                

                $("#majorRoleSelect").empty();
                $("#majorRoleSelect").append("<option value='blank'>[空]</option>");
                for (var mrole in majorRoleList) {
                    $("#majorRoleSelect").append("<option value="+ mrole + ">" + majorRoleList[mrole] + "</option>");
                }
                if (filter.roles == "blank") {
                	$("#majorRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#majorRoleSelect").selectpicker('render');
                } else if (filter.roles) {
                	$("#majorRoleSelect").selectpicker('val', filter.roles.split(","));
                }
                $("#majorRoleSelect").selectpicker('refresh');
                

                $("#guestRoleSelect").empty();
                $("#guestRoleSelect").append("<option value='blank'>[空]</option>");
                for (var grole in guestRoleList) {
                    $("#guestRoleSelect").append("<option value="+ grole + ">" + guestRoleList[grole] + "</option>");
                }
                if (filter.guest == "blank") {
                	$("#guestRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#guestRoleSelect").selectpicker('render');
                } else if (filter.guest) {
                	$("#guestRoleSelect").selectpicker('val', filter.guest.split(","));
                }
                $("#guestRoleSelect").selectpicker('refresh');


                $("#massRoleSelect").empty();
                $("#massRoleSelect").append("<option value='blank'>[空]</option>");
                for (var mrole in massesRoleList) {
                    $("#massRoleSelect").append("<option value="+ mrole + ">" + massesRoleList[mrole] + "</option>");
                }
                if (filter.mass == "blank") {
                	$("#massRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#massRoleSelect").selectpicker('render');
                } else if (filter.mass) {
                	$("#massRoleSelect").selectpicker('val', filter.mass.split(","));
                }
                $("#massRoleSelect").selectpicker('refresh');
                

                $("#clothSelect").empty();
                $("#clothSelect").append("<option value='blank'>[空]</option>");
                for (var cloth in clotheList) {
                    $("#clothSelect").append("<option value="+ cloth + ">" + clotheList[cloth] + "</option>");
                }
                if (filter.clothes == "blank") {
                	$("#clothSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#clothSelect").selectpicker('render');
                } else if (filter.clothes) {
                	$("#clothSelect").selectpicker('val', filter.clothes.split(","));
                }
                $("#clothSelect").selectpicker('refresh');

                
                $("#makeupSelect").empty();
                $("#makeupSelect").append("<option value='blank'>[空]</option>");
                for (var makeup in makeupList) {
                    $("#makeupSelect").append("<option value="+ makeup + ">" + makeupList[makeup] + "</option>");
                }
                if (filter.makeup == "blank") {
                	$("#makeupSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#makeupSelect").selectpicker('render');
                } else if (filter.makeup) {
                	$("#makeupSelect").selectpicker('val', filter.makeup.split(","));
                }
                $("#makeupSelect").selectpicker('refresh');

                
                $("#propSelect").empty();
                $("#propSelect").append("<option value='blank'>[空]</option>");
                for (var cprop in commonPropList) {
                    $("#propSelect").append("<option value="+ cprop + ">" + commonPropList[cprop] + "</option>");
                }
                if (filter.props == "blank") {
                	$("#propSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#propSelect").selectpicker('render');
                } else if (filter.props) {
                	$("#propSelect").selectpicker('val', filter.props.split(","));
                }
                $("#propSelect").selectpicker('refresh');

                
                $("#specialPropSelect").empty();
                $("#specialPropSelect").append("<option value='blank'>[空]</option>");
                for (var sprop in specialPropList) {
                    $("#specialPropSelect").append("<option value="+ sprop + ">" + specialPropList[sprop] + "</option>");
                }
                if (filter.specialProps == "blank") {
                	$("#specialPropSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#specialPropSelect").selectpicker('render');
                } else if (filter.specialProps) {
                	$("#specialPropSelect").selectpicker('val', filter.specialProps.split(","));
                }
                $("#specialPropSelect").selectpicker('refresh');
        	}
        }
    });
}
//高级查询窗口初始化时根据当前剧组的不同类型显示不同的数据
function showDifferentSearchData(){
	//根据剧组类型显示不同的集场区间信息
	if (crewType == 0 || crewType == 3) { //电影
		$("#tvbSctionNo").empty();
	}else {
		$("#moviceSectionNo").empty();
	}
	
	//根据剧组类型显示不同的编号信息
	if (crewType == 0 || crewType == 3) {
		$("#tvbSeriesNoLi").css("display", "none");
	}else {
		$("#moviceViewNos").css("display", "none");
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
	            crewName = response.crewName; //剧组名称
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}
//清空高级查询文本框中的内容
function clearSaerchContent(){
    //集场重置
    if (crewType != Constants.CrewType.movie && crewType != 3) {
        $("#startSeriesNo").val("");
        $("#endSeriesNo").val("");
    }
    
    $("#startViewNo").val("");
    $("#endViewNo").val("");
    $("#seriesViewNos").val("");
    $("#viewNos").val("");
    
    //隐藏单选按钮
    $("#anyOneAppear").hide();
    $("#noOneAppear").hide();
    $("#everyOneAppear").hide();
    $("#notEvenyOneAppear").hide();
    
    //设置单选按钮默认选中状态
    $("input[name='searchMode'][value='1']").prop("checked",true);
    //$("input[name='sortType'][value='1']").prop("checked",true);
   /* $("input[name='sortFlag'][value='1']").prop("checked",true);*/
    
    //所有下拉值全部反选
    $('.selectpicker').selectpicker('deselectAll');
    
    $(".preValue").val("");
    
    $("#mainContent").val("");
    $("#viewRemark").val("");
}

//更改高级查询中的主演下拉框时触发的方法
function changeMajorRole(event){
	 //当选择主要演员显现"出现即可"和"不出现"单选按钮，只有当选择两个及两个以上才显现"同时出现"和"不同时出现"单选按钮
    var majorRoleVal = $("#majorRoleSelect").val();
    
    if (majorRoleVal != null) {
        var selectedLength = majorRoleVal.length;
        if (selectedLength > 1) {
            /* 选择多条数据的情况 */
            
            // 展现"同时出现"、"不同时出现"
            $("#everyOneAppear").show();
            $("#notEvenyOneAppear").show();
        } else {
            /* 由选择多条数据变为选择一条数据情况 */
            
            //如果此时同时出现或不同时出现被选中，则设置单选按钮“出现即可”选中
            var searchMode=$("input[name='searchMode']:checked").val();
            if (searchMode == 0 || searchMode == 2) {
                $("input[name='searchMode'][value='1']").prop("checked",true);
            }
            
            //展现"出现即可"、"不出现"，隐藏"同时出现"、"不同时出现"
            $("#anyOneAppear").show();
            $("#noOneAppear").show();
            $("#everyOneAppear").hide();
            $("#notEvenyOneAppear").hide();
        }
    } else {
        //一条数据都没选择的情况
        //隐藏"出现即可"、"不出现"、"同时出现"、"不同时出现"
        $("#anyOneAppear").hide();
        $("#noOneAppear").hide();
        $("#everyOneAppear").hide();
        $("#notEvenyOneAppear").hide();
    }

}
//点击高级查询页面的查询按钮,根据查询条件获取数据
function confirmSearchViewInfo(){
	var atmosphere =$("#atmosphereSelect").val();
	var site =$("#siteSelect").val();
	var major =$("#firstLocationSelect").val();
	var minor =$("#secondLocationSelect").val();
	var thirdLevelView = $("#thirdLocationSelect").val();
	var clothes =$("#clothSelect").val();
	var makeup =$("#makeupSelect").val();
	var roles = $("#majorRoleSelect").val(); 
	var props = $("#propSelect").val(); 
    var specialProps = $("#specialPropSelect").val();
	var guestRole = $("#guestRoleSelect").val(); 
	var massRole = $("#massRoleSelect").val(); 
	var shootStatus = $("#shootStatusSelect").val();
	var advert = $("#advertInfoSelect").val();
	//拍摄地点
	var shootLocation =$("#shootLocationSelect").val();
	var mainContent = $("#mainContent").val();
	var remark = $("#viewRemark").val();
    var seriesViewNos = $("#seriesViewNos").val();
    var viewNos = $("#viewNos").val();
    //特殊提醒
    var specialRemind = $("#specialRemindSelect").val();
    //对数据进行校验
	//气氛
	if(atmosphere!= null && atmosphere!=""){
		var atmosphereStr = "";
		
		for(var i=0;i<atmosphere.length;i++){
			atmosphereStr+=atmosphere[i]+",";
		}
		atmosphereStr=atmosphereStr.substring(0,atmosphereStr.length-1);
		filter.atmosphere=atmosphereStr;
	}else{
		filter.atmosphere="";
	}
	
	//特殊提醒
	if(specialRemind!= null && specialRemind!=""){
		var specialRemindStr = "";
		
		for(var i=0;i<specialRemind.length;i++){
			specialRemindStr+=specialRemind[i]+",";
		}
		specialRemindStr=specialRemindStr.substring(0,specialRemindStr.length-1);
		filter.specialRemind=specialRemindStr;
	}else{
		filter.specialRemind="";
	}
	
	//内外景
	if(site!= null && site!=""){
		var siteStr = "";
		
		for(var i=0;i<site.length;i++){
			siteStr+=site[i]+",";
		}
		siteStr=siteStr.substring(0,siteStr.length-1);
		filter.site=siteStr;
	}else{
		filter.site="";
	}
	//主场景信息
	if(major!= null && major!=""){
		var majorStr = "";
		
		for(var i=0;i<major.length;i++){
			majorStr+=major[i]+",";
		}
		majorStr=majorStr.substring(0,majorStr.length-1);
		filter.major=majorStr;
	}else{
		filter.major="";
	}
	//次场景信息
	if(minor!= null && minor!=""){
		var minorStr = "";
		
		for(var i=0;i<minor.length;i++){
			minorStr+=minor[i]+",";
		}
		minorStr=minorStr.substring(0,minorStr.length-1);
		filter.minor=minorStr;
	}else{
		filter.minor="";
	}
	//三级场景信息
	if(thirdLevelView!= null && thirdLevelView!=""){
		var thirdLevelViewStr = "";
		
		for(var i=0;i<thirdLevelView.length;i++){
			thirdLevelViewStr+=thirdLevelView[i]+",";
		}
		thirdLevelViewStr=thirdLevelViewStr.substring(0,thirdLevelViewStr.length-1);
		filter.thirdLevel=thirdLevelViewStr;
	}else{
		filter.thirdLevel="";
	}
	//道具
	if(clothes!= null && clothes!=""){
		var clothe = "";
		
		for(var i=0;i<clothes.length;i++){
			clothe+=clothes[i]+",";
		}
		clothe=clothe.substring(0,clothe.length-1);
		filter.clothes=clothe;
	}else{
		filter.clothes="";
	}
	//化妆
	if(makeup!= null && makeup!=""){
		var makeupStr = "";
		
		for(var i=0;i<makeup.length;i++){
			makeupStr+=makeup[i]+",";
		}
		makeupStr=makeupStr.substring(0,makeupStr.length-1);
		filter.makeup=makeupStr;
	}else{
		filter.makeup="";
	}
	//拍摄状态
	if(shootStatus!= null && shootStatus!=""){
		var shootStatusStr = "";
		
		for(var i=0;i<shootStatus.length;i++){
			shootStatusStr+=shootStatus[i]+",";
		}
		shootStatusStr=shootStatusStr.substring(0,shootStatusStr.length-1);
		filter.shootStatus=shootStatusStr;
	}else{
		filter.shootStatus="";
	}
	//角色信息
	if(roles!= null && roles!=""){
		var role = "";
		
		for(var i=0;i<roles.length;i++){
			role+=roles[i]+",";
		}
		role=role.substring(0,role.length-1);
		filter.roles=role;
	}else{
		filter.roles="";
	}
	//道具
	if(props!= null && props!=""){
		var prop = "";
		
		for(var i=0;i<props.length;i++){
			prop+=props[i]+",";
		}
		prop=prop.substring(0,prop.length-1);
		filter.props=prop;
	}else{
		filter.props="";
	}
	//特殊道具
    if (specialProps != null && specialProps != "") {
       var specialProp = "";
       for (var i = 0; i < specialProps.length; i++) {
           specialProp += specialProps[i] + ",";
       }
       specialProp = specialProp.substring(0, specialProp.length-1);
       filter.specialProps = specialProp;
    } else {
       filter.specialProps = ""; 
    }
    //特约演员
	if(guestRole!= null && guestRole!=""){
		var guest = "";
		
		for(var i=0;i<guestRole.length;i++){
			guest+=guestRole[i]+",";
		}
		guest=guest.substring(0,guest.length-1);
		filter.guest=guest;
	}else{
		filter.guest="";
	}
	//群众演员
	if(massRole!= null && massRole!=""){
		var mass = "";
		
		for(var i=0;i<massRole.length;i++){
			mass+=massRole[i]+",";
		}
		mass=mass.substring(0,mass.length-1);
		filter.mass=mass;
	}else{
		filter.mass="";
	}
	//拍摄地点
	if(shootLocation!= null && shootLocation!=""){
		var shootLocationStr = "";
		
		for(var i=0;i<shootLocation.length;i++){
			shootLocationStr+=shootLocation[i]+",";
		}
		shootLocationStr=shootLocationStr.substring(0,shootLocationStr.length-1);
		filter.shootLocation=shootLocationStr;
	}else{
		filter.shootLocation="";
	}
	//商植
	if(advert!= null && advert!=""){
		var advertStr = "";
		
		for(var i=0;i<advert.length;i++){
			advertStr+=advert[i]+",";
		}
		advertStr=advertStr.substring(0,advertStr.length-1);
		filter.advert=advertStr;
	}else{
		filter.advert="";
	}
	//开始集次号
	if (crewType == 0 || crewType == 3) {
		if($("#startSeriesNo").val()!=""){
			filter.startSeriesNo=$("#startSeriesNo").val();
		}else{
	        filter.startSeriesNo="1";
	    }
	}else {
		if($("#startSeriesNo").val()!=""){
			filter.startSeriesNo=$("#startSeriesNo").val();
		}else{
			filter.startSeriesNo="";
		}
	}
	//开始场次号
	if($("#startViewNo").val()!=""){
		filter.startViewNo=$("#startViewNo").val();
	}else{
		filter.startViewNo="";
	}
	
	//结束集次号
	if (crewType == 3 || crewType == 0) {
		if($("#endSeriesNo").val()!=""){
			filter.endSeriesNo=$("#endSeriesNo").val();
		}else{
			filter.endSeriesNo="1";
		}
	}else {
		if($("#endSeriesNo").val()!=""){
			filter.endSeriesNo=$("#endSeriesNo").val();
		}else{
			filter.endSeriesNo="";
		}
		
	}
	//结束场次号
	if($("#endViewNo").val()!=""){
		filter.endViewNo=$("#endViewNo").val();
	}else{
		filter.endViewNo="";
	}
	
	if (crewType != 0 && crewType != 3) { //电影或网大
		if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
			showErrorMessage("请填写起始集数");
			return;
		}
	}
	
	if (crewType != 0 && crewType != 3) {
		if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
			showErrorMessage("请填写集数");
			return;
		}
	}
	filter.seriesNo="";
	filter.viewNo="";
    filter.seriesViewNos = "";
    //集场编号（非电影剧本时使用）
    if (seriesViewNos != null && seriesViewNos != "") {
        var seriesViewNoArr = seriesViewNos.split(/，|；|,|;/);
        for (var i = 0; i < seriesViewNoArr.length; i++) {
            if (seriesViewNoArr[i] != null && seriesViewNoArr[i] != "" && !/^\d+( )*(-|－|——)( )*.+/.test(seriesViewNoArr[i])) {
                showErrorMessage("《" + seriesViewNoArr[i] + "》场集场编号不符合规范，请重新输入");
                return;
            }
            
            var singleSeriesViewNoArr = seriesViewNoArr[i].split(/-|－|——/);
            var seriesNo = singleSeriesViewNoArr[0];
            if (isNaN(seriesNo)) {
                showErrorMessage("《" + seriesViewNoArr[i] + "》场集号只能输入数字，请重新输入");
                return;
            }
        }
    
        filter.seriesViewNos = seriesViewNos;
    }
    
    //场次编号（电影剧本时使用）
    if (viewNos != null && viewNos != "") {
        var viewNoArr = viewNos.split(/，|；|,|;/);
        
        var dealedViewNos = "";
        for (var i = 0; i < viewNoArr.length; i++) {
            var seriesViewNo = "1-" + viewNoArr[i];
            if (i == 0) {
                dealedViewNos = seriesViewNo;
            } else {
                dealedViewNos += "," + seriesViewNo;
            }
        }
    
        filter.seriesViewNos = dealedViewNos;
    }
	
	//主要内容
	if (mainContent != "" && mainContent != null) {
	 filter.mainContent = mainContent;
	} else {
	 filter.mainContent = "";
	}
	
	//备注
	if (remark != "" && remark != null) {
     filter.remark = remark;
    } else {
     filter.remark = "";
    }
	//排序方式
	/*var sortFlag = $("input[name='sortFlag']:checked").val();
	filter.sortFlag=sortFlag;*/
	//查询条件的出现频率
	filter.searchMode=$("input[name='searchMode']:checked").val();
	filter.fromAdvance = true;
	grid.setFilter(filter);
	grid.goToPage(0);
//	console.log(filter);
	
	$('#searchWindow').jqxWindow('close');
	
	$("select.selectpicker").each(function(){
        var _this = $(this);
        var sid = _this.attr("id");
        var sval = $("#"+sid).val();
        if(sval != null){
            for(var i=0;i<sval.length;i++){
                _this.find("option[value="+sval[i]+"]").insertBefore(_this.find('option:eq(1)'));
            }
        }
        $("#"+sid).selectpicker('refresh');
    });
}

//初始化添加到通告单窗口
function initAddViewToNoticeWin(groupList){
	var groupSource = [];
    for(var key in groupList){
    	groupSource.push({text: groupList[key], value: key});
    }
    groupSource.push({text:'新增组',value:'99'});
   $('#customWindow').jqxWindow({
    theme: theme,
    width: 640,
    maxHeight: 1600,
    height: 420,
    resizable: false,
    autoOpen: false,
    isModal: true,
    title: '添加到通告单',
    cancelButton: $('.noticeCancelButton'),
    initContent: function () {
    	//初始化通告单中各种组件的样式
    	initNoticeCss();
    	
        /* 新建通告单 */
        $("#noticeTime").jqxInput({theme: theme});
        $("#noticeNameInput").jqxInput({theme: theme});
        //初始化分组下拉框的数据
        $("#group").jqxDropDownList({
            theme:theme,selectedIndex:0,source: groupSource, displayMember: "text", valueMember: "value", width: '300px', height: 30,placeHolder: ""
                ,dropDownHeight: getHeight(groupSource)
        });
        
        //验证通告单名称不能为空
        $('#noticeForm').jqxValidator({
            animationDuration: 1,
            rules: [
              { input: '#noticeNameInput', message: '通告单名称不可为空!', action: 'keyup,blur', rule: 'required' }
            ]
        });
        
        
        /* 已有通告单 */
        //加载已有通告单的数据列表
        loadHasNoticeGrid();
        
       //解决第一次加载窗口，因为控件都没有初始化完成导致赋值不成功问题
       $("#group").jqxDropDownList('selectIndex', 0);
       $("#noticeTime").val(new Date().Format('yyyy-MM-dd'));
       autoGetNoticeName();
      }
   });
}
//初始化通告单中的按钮/表单/div的样式
function initNoticeCss(){
	$("#saveNoticeButton").jqxButton({
        theme:theme, 
        width: 80, 
        height: 25
    });
    
    $(".noticeCancelButton").jqxButton({
           theme:theme, 
           width: 80, 
           height: 25
    });
       
    $("#addToNoticeButton").jqxButton({
        theme:theme, 
        width: 80, 
        height: 25
    });
    
    $("#noticeDivForm").jqxExpander({
        theme: theme,
        width: '100%',
        expanded: true,
    });
    
    $("#noticeDivList").jqxExpander({
        theme: theme,
        width: '100%',
        expanded: false
    });
    
    $("#noticeDivForm").on('expanding', function () {
        $("#noticeDivList").jqxExpander('collapse');
    });
    $("#noticeDivList").on('expanding', function () {
        $("#noticeDivForm").jqxExpander('collapse');
    });
}


//加载已有通告单的数据列表
function loadHasNoticeGrid(){
     var noticeSource =
     {
        datatype: "json",
        root:'resultList',
        url:'/notice/loadNoticeList',
        data: {forSimple: true},
        datafields: [
             { name: 'noticeId',type: 'string' },
             { name: 'noticeName',type: 'string' },
             { name: 'noticeDate',type: 'date' },
             { name: 'shootLocation',type: 'string' },
             { name: 'groupName',type: 'string' },
             { name: 'mainrole',type: 'string' },
             { name: 'canceledStatus',type: 'string' }
        ],
        type:'post',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        beforeprocessing:function(data){
            //查询之后可执行的代码
            noticeSource.totalrecords=data.result.total;
            noticeResultData = data.result;
        }
     };
     //渲染通告单数据
     var canceledStatusRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
         if(rowdata.canceledStatus==0){
             return "<div class='rowStatusColor' style='padding-top:5px;'>未销场</div>";
         }else if(rowdata.canceledStatus==1){
             return "<div class='rowStatusColor' style='padding-top:5px;'>已销场</div>";
         }
     };
     
     var noticeDataAdapter = new $.jqx.dataAdapter(noticeSource);
     
     $("#existNoticeGrid").jqxGrid({
         theme:theme,
         width: '99%',
         source: noticeDataAdapter,
         pageable: false,
         autoheight: false,
         height: 262,
         columnsresize: true,
         showtoolbar: false,
         rendergridrows:rendergridrows,
         localization:localizationobj,//表格文字设置
         columns: [
           { text: '通告单名称', datafield: 'noticeName', width: 245},
           { text: '时间', datafield: 'noticeDate', cellsformat: 'yyyy-MM-dd', width: 190},
           { text: '分组', datafield: 'groupName', width: 70},
           { text: '状态', cellsrenderer:canceledStatusRenderer, width: 96}
         ]
     });
}

//自动获取通告单名称
function autoGetNoticeName() {
    var selectGroupItem = $("#group").jqxDropDownList('getSelectedItem');
    if (selectGroupItem == undefined) {
        return false;
    }
    var groupName = selectGroupItem.label;
    var noticeDateStr = $("#noticeTime").val();
    
    var noticeDateArr = noticeDateStr.split("-");
    var noticeDate = noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日";
    
    $("#noticeNameInput").val("《" + crewName + "》" + noticeDate + groupName+"通告");
    
    $("#addNoticeError").html("");
}

//将场景信息添加到通告单中
function addViewToNotice(){
	if(grid.getSelectIds()==0){
        showErrorMessage("请选择场次");
        return;
    }
	$('#customWindow').jqxWindow('open');
	
	$("#group").jqxDropDownList('selectIndex', 0);
    $("#noticeTime").val(new Date().Format('yyyy-MM-dd'));
    autoGetNoticeName();
}

//改变分组信息
function changeGroupInfo(event){
    var args = event.args;
    if (args) {
        var index = args.index;
        var item = args.item;
        
        //选择新增分组
        if(item.value=="99"){
            if(index>25){
                showErrorMessage("目前最多选择到Z组");
                $("#group").jqxDropDownList("selectIndex",index-1);
                return;
            }
            $.ajax({
                url:"/shootGroupManager/saveGroup",
                type:"post",
                dataType:"json",
                data:{groupName:groupArray[index-1].text},
                success:function(data){
                    if(!data.success){
                        showErrorMessage(data.message);
                        $("#group").jqxDropDownList("selectIndex",0);
                    }
                    $("#group").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                    $("#group").jqxDropDownList("selectIndex",index);
                    autoGetNoticeName();
                }
            });
        }
        autoGetNoticeName();
    }
}
//保存通告单
function saveNoticeInfo(){
    if($('#noticeForm').jqxValidator("validate")){
        var noticeName=$("#noticeNameInput").val();
        var group=$("#group").val();
        var noticeTime=$("#noticeTime").val();
        
        if(grid.getSelectIds()==0){
            showErrorMessage("请选择场次");
            return;
        }
        
        //判断当前场景中是否有演员请假
    	$.ajax({
    		url:"/notice/checkIsLeave",
    		data:{viewIds:grid.getSelectIds(),noticeDateStr:noticeTime},
    		dataType:"json",
    		type:'post',
			async:false,
			success:function(data){
    				if(!data.success){
    					showErrorMessage(data.message);
    				}else{
    					var leaveInfo = data.leaveInfo;
    					if (leaveInfo != null && leaveInfo != '' && leaveInfo != 'undefined') {
    						
    						doubleCallBackFun("是否重新选择", leaveInfo,"重新选择", "添加到通告单", null, function () {
    							$.ajax({
    					            url:"/notice/noticeSave",
    					            type:"post",
    					            dataType:"json",
    					            data:{noticeName:noticeName,groupId:group,noticeDateStr:noticeTime,viewIds:grid.getSelectIds()},
    					            async:false,
    					            success:function(data){
    					                if(data.success){
    					                    showSuccessMessage("添加通告单成功！");
    					                    $('#customWindow').jqxWindow('close');
    					                    grid.unSelectedAll();
    					                    $('#existNoticeGrid').jqxGrid("updatebounddata");
    					                }else{
    					                	showErrorMessage(data.message);
    					                }
    					            }
    					        });
    							
    						});
    						  
    					}else {
    						$.ajax({
    				            url:"/notice/noticeSave",
    				            type:"post",
    				            dataType:"json",
    				            data:{noticeName:noticeName,groupId:group,noticeDateStr:noticeTime,viewIds:grid.getSelectIds()},
    				            async:false,
    				            success:function(data){
    				                if(data.success){
    				                    showSuccessMessage("添加通告单成功！");
    				                    
    				                    $('#customWindow').jqxWindow('close');
    				                    grid.unSelectedAll();
    				                    $('#existNoticeGrid').jqxGrid("updatebounddata");
    				                }else{
    				                	showErrorMessage(data.message);
    				                }
    				            }
    				        });
    					}
    				}
    			}
    	});
        
    }
}

//将场景信息添加到已有通告单中
function confirmAddToNotice(){
	 //获取选中的拍摄计划ID
    var noticeGridRowIndexes = $('#existNoticeGrid').jqxGrid('getselectedrowindexes');
    if(noticeGridRowIndexes.length == 0){
        showErrorMessage("请选择通告单");
        return;
    }
    if (noticeGridRowIndexes.length > 1) {
		showErrorMessage("只能选择一条通告单");
		return;
    }
    
    var noticeRows="";
    for(var i = 0; i < noticeGridRowIndexes.length; i++){
        var resultrow = noticeResultData.resultList[noticeGridRowIndexes[i]];
        noticeRows += resultrow.noticeId +",";
    }
    noticeRows = noticeRows.substring(0, noticeRows.length - 1);
    
    //获取选中的场景IDs
    if(grid.getSelectIds()==""){
        showErrorMessage("请选择场次");
        return;
    }
    var viewRows=grid.getSelectIds();
    
    
    //取出请假信息
    //判断当前场景中是否有演员请假
	$.ajax({
		url:"/notice/checkIsLeave",
		data:{viewIds:grid.getSelectIds(),noticeId:noticeRows},
		dataType:"json",
		type:'post',
		async:false,
		success:function(data){
				if(!data.success){
					showErrorMessage(data.message);
				}else{
					var leaveInfo = data.leaveInfo;
					if (leaveInfo != null && leaveInfo != '' && leaveInfo != 'undefined') {
						
						doubleCallBackFun("是否重新选择", leaveInfo,"重新选择", "添加到通告单", null, function () {
						    $.ajax({
						        url: "/notice/addNoticeView",
						        type: 'post',
						        data: {'noticeId': noticeRows, 'viewIds': viewRows},
						        dataType: 'json',
						        async: false,
						        success: function (response) {
						            if (response.success) {
						                showSuccessMessage(response.message);
						                $('#customWindow').jqxWindow('close');
						                grid.unSelectedAll();
						                $('#existNoticeGrid').jqxGrid('clearselection');
						            } else {
						                   showErrorMessage(response.message);
						            }
						        },
						        error: function () {
						            showErrorMessage("发送请求失败");
						        }
						    });
						});
						  
					}else {
						  
					    $.ajax({
					        url: "/notice/addNoticeView",
					        type: 'post',
					        data: {'noticeId': noticeRows, 'viewIds': viewRows},
					        dataType: 'json',
					        async: false,
					        success: function (response) {
					            if (response.success) {
					                showSuccessMessage(response.message);
					                $('#customWindow').jqxWindow('close');
					                grid.unSelectedAll();
					                $('#existNoticeGrid').jqxGrid('clearselection');
					            } else {
					                   showErrorMessage(response.message);
					            }
					        },
					        error: function () {
					            showErrorMessage("发送请求失败");
					        }
					    });
					}
				}
			}
	}); 
    
}

//锁定场景
function lockSelectRow(){
	var selectId = grid.getSelectIds();
	var isLock = false;
	var lockTr = [];
	if(selectId.length == 0){
		showInfoMessage("请选择要锁定的场景");
		return;
	}else{
		var selectIds = selectId.split(",");
		for(var i= 0; i< selectIds.length; i++){
			var span = $("tr[id="+selectIds[i]+"]").find("td:first-child").find("span");
			if(span.hasClass("lock-icon")){
			}else{
				isLock = true;
			}
			lockTr.push(span);
		}
			
	}
	$.ajax({
		url: '/scheduleManager/updateViewGroupMapIsLock',
		type: 'post',
		data: {"viewIds": selectId, "isLock": isLock},
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(isLock){
					$.each(lockTr, function(){
						$(this).addClass("lock-icon");
					});
				}else{
					$.each(lockTr, function(){
						$(this).removeClass("lock-icon");
					});
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//查询拍摄地
function searchshootLocation(own, event){
	if(event.keyCode == 13){
		if($(own).val() == ""){
			filter.shootLocationLike = "";
		}else{
			filter.shootLocationLike = $(own).val();
		}
		grid.setFilter(filter);
		grid.goToPage(0);
	}
}
//实时赋值拍摄地
function assignmentLocation(own){
	filter.shootLocationLike = $(own).val();
}
//查询主场景
function searchMainView(own, event){
	if(event.keyCode == 13){
		if($(own).val() == ""){
			filter.majorLike = "";
		}else{
			filter.majorLike = $(own).val();
		}
		grid.setFilter(filter);
		grid.goToPage(0);
	}
}
//实时赋值主场景
function assignmentView(own){
	filter.majorLike = $(own).val();
}

//查询次场景
function searchminorView(own, event){
	if(event.keyCode == 13){
		if($(own).val() == ""){
			filter.minorLike = "";
		}else{
			filter.minorLike = $(own).val();
		}
		grid.setFilter(filter);
		grid.goToPage(0);
	}
}
function assignmentMinView(own){
	filter.minorLike = $(own).val();
}
//查询三级场景
function searchThirdView(own, event){
	if(event.keyCode == 13){
		if($(own).val() == ""){
			filter.thirdLevelLike = "";
		}else{
			filter.thirdLevelLike = $(own).val();
		}
		grid.setFilter(filter);
		grid.goToPage(0);
	}
}
function assignmentThirdView(own){
	filter.thirdLevelLike = $(own).val();
}
//查询主要内容
function searchMainContent(own, event){
	if(event.keyCode == 13){
		if($(own).val() == ""){
			filter.mainContent = "";
		}else{
			filter.mainContent = $(own).val();
		}
		grid.setFilter(filter);
		grid.goToPage(0);
	}
}
function assignmentContent(own){
	filter.mainContent = $(own).val();
}



//加载演员信息
function loadRoleList(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var majorRoleList = response.majorRoleList;
				var groupList = response.groupList;
				//初始化计划列表
				initPlanList(majorRoleList);
				//初始化添加到通告单窗口
				initAddViewToNoticeWin(groupList);
				//初始化批量修改窗口
				initRepeatWin(groupList);
				//初始化智能整理窗口
				initIntelligentWin(majorRoleList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//初始化计划列表
function initPlanList(majorRoleList){
	var roleArray = [];
	for(var i= 0; i < majorRoleList.length; i++){
		var role = majorRoleList[i];
		roleArray.push({text: role.viewRoleName, value: role.viewRoleId});
	}
	//加载角色列信息
    roleColumn = function (columnfield, value, columnproperties, rowdata) {
    	//操作列html
    	var thisRowData = rowdata;
    	
    	var roleArray = thisRowData.roleList;
    	for(var i=0;i<roleArray.length;i++){
    		if(columnproperties.text==roleArray[i].viewRoleName){
    			var roleNum = roleArray[i].roleNum;
    			if (roleNum == 0) {
    				return "OS";
				}else {
					if(roleArray[i].shortName==null || roleArray[i].shortName.trim() == ""){
						return "√";
					}else{
						return roleArray[i].shortName;
					}
				}
    		}
    	}
    	return "";
    };
    
    //加载锁定列
    lockColumn = function(columnfield, value, columnproperties, rowdata){
    	if(rowdata.isLock){
    		return '<span class="lock-icon"></span>';
    	}else{
    		return '<span></span>';
    	}
    };
    
    //加载计划日期
    dateColumn = function(columnfield, value, columnproperties, rowdata){
    	if(rowdata.planShootDate == null){
    		rowdata.planShootDate = "";
    	}
    	return rowdata.planShootDate;
    }; 
    //加载计划组别
    groupColumn = function(columnfield, value, columnproperties, rowdata){
    	if(rowdata.planGroupName == null || rowdata.planGroupName == "单组"){
    		rowdata.planGroupName = "";
    	}
    	return rowdata.planGroupName;
    };
    
//    //拍摄组别
//    shootGroupsColumn = function(columnfield, value, columnproperties, rowdata){
//    	if(rowdata.shootGroups == null){
//    		rowdata.shootGroups = "";
//    	}
//    	return rowdata.shootGroups;
//    };
    
    //加载集次编号信息
    viewColumn = function(columnfield, value, columnproperties, rowdata){
		var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
    	return "<span style=''  class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' >" + seriesNoAndViewNo + "</span>";
    };
    
    //加载场次编号
    viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
        var viewNo =rowdata.viewNo;
        return "<span style='' class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' >" + viewNo + "</span>";
    };
    
    rendergridrows = function (params) {
    	//调用json返回的列表数据
        return params.data;
    };
    
    //加载气氛信息
    atmosphere=function(columnfield, value, columnproperties, rowdata){
    	var atmosphere= rowdata.atmosphereName;
    	if(atmosphere==null){
    		atmosphere="";
    	}
    	return atmosphere;
    };
    
    //加载特殊提醒
    seasonColumn=function(columnfield, value, columnproperties, rowdata){
    	var seasonText= '';
    	if(rowdata.specialRemind !=null && rowdata.specialRemind != undefined){
    		seasonText=rowdata.specialRemind;
    	}
    	return "<span title='"+ seasonText +"'>" + seasonText + "</span>";
    };
    
    //加载内外景信息
    siteColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.site){
    		text=rowdata.site;
    	}
    	return text;
    };
    
    //加载主场景信息
    majorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.majorView){
    		text=rowdata.majorView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载次场景信息
    minorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.minorView){
    		text=rowdata.minorView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载三级场景信息
    thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.thirdLevelView){
    		text=rowdata.thirdLevelView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载主要内容信息
    mainContentColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.mainContent){
    		text=rowdata.mainContent;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载页数信息
    pageCountColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.pageCount){
    		text=rowdata.pageCount;
    	}
    	return text;
    };
    
    //加载特约演员信息
    guestRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.guestRoleList){
    		text=rowdata.guestRoleList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载主要演员信息
    massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.massRoleList){
    		text=rowdata.massRoleList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载道具信息
    propsListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.propsList){
    		text=rowdata.propsList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载服装信息
    clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.clothesName){
    		text=rowdata.clothesName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载化妆信息
    makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.makeupName){
    		text=rowdata.makeupName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载拍摄日期信息
    shootDateColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootDate){
    		text=rowdata.shootDate;
    	}
    	return text;
    };
    
    //加载备注信息
    remarkColumn= function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.remark){
    		text=rowdata.remark;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载拍摄地点信息
    shootLocationColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootLocation){
    		text+=rowdata.shootLocation;
    	}
    	if(rowdata.shootRegion) {
    		text+="("+rowdata.shootRegion+")";
    	}
    	return text;
    };
    
    //加载拍摄状态信息
    shootStatusColumn=function(columnfield, value, columnproperties, rowdata){
    	var shootStatusText = rowdata.shootStatus;
    	if(shootStatusText==null){
    		shootStatusText="";
    	}else {
    		if(shootStatusText == 0){
    			shootStatusText = "未完成";
    		}else if(shootStatusText == 1){
    			shootStatusText = "部分完成";
    		}else if(shootStatusText == 2){
    			shootStatusText = "完成";
    		}else if(shootStatusText == 3){
    			shootStatusText = "删戏";
    		}else if(shootStatusText == 4){
    			shootStatusText = "加戏";
    		}else if(shootStatusText == 5){
    			shootStatusText == "加戏完成";
    		}else {
    			shootStatusText == "完成";
    		}
    	}
    	return shootStatusText;
    };
    
    //加载广告信息
    advertNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.advertName){
    		text=rowdata.advertName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    var gridColumns = [];
    gridColumns.push({text: "", cellsrenderer: lockColumn, width: "35px"});
	gridColumns.push({text: "计划拍摄日期", cellsrenderer: dateColumn, width: "110px"});
	gridColumns.push({text: "计划拍摄组别", cellsrenderer: groupColumn, width: "90px"});
//	gridColumns.push({text: "拍摄组别", cellsrenderer: shootGroupsColumn, width: '65px'});
	//电影类型的剧组不显示集次
    if (crewType == 0 || crewType == 3) {
      gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn, width: '65px'});
    } else {
      gridColumns.push({ text: '集-场', cellsrenderer: viewColumn, width: '65px'});
    }
  //加载数据列,拼接场景列表的表头信息
    gridColumns.push(
			{ text: '特殊提醒', cellsrenderer: seasonColumn, width: '70px'},
            { text: '气氛',cellsrenderer: atmosphere, width: '40px'},
            { text: '内外景',cellsrenderer:siteColumn, width: '50px'},
            { text: '<p style="width:150px"><input type="text" onkeyup="searchshootLocation(this, event)" onblur="assignmentLocation(this)" placeholder="拍摄地点"></p>', cellsrenderer: shootLocationColumn, width: '150px', isSearch: true, title: "拍摄地点"},
            { text: '<p style="width:120px"><input type="text" onkeyup="searchMainView(this, event)" onblur="assignmentView(this)" placeholder="主场景"></p>',cellsrenderer:majorViewColumn, width: '120px', isSearch: true, title: "主场景"},
            { text: '<p style="width:120px"><input type="text" onkeyup="searchminorView(this, event)" onblur="assignmentMinView(this)" placeholder="次场景"></p>',cellsrenderer:minorViewColumn, width: '120px', isSearch: true, title: "次场景"},
            { text: '<p style="width:120px"><input type="text" onkeyup="searchThirdView(this, event)" onblur="assignmentThirdView(this)" placeholder="三级场景"></p>',cellsrenderer: thirdLevelViewColumn, width: '120px', isSearch: true, title: "三级场景"},
            { text: '<p style="width:150px"><input type="text" onkeyup="searchMainContent(this, event)" onblur="assignmentContent(this)" placeholder="主要内容"></p>',cellsrenderer: mainContentColumn, width: '150px', isSearch: true, title: "主要内容"},
            { text: '页数',cellsrenderer: pageCountColumn, width: '40px'}
     );
    if(roleArray.length != 0){
    	for(var i = 0; i< roleArray.length; i++){
            gridColumns.push({ text: roleArray[i].text, cellsrenderer: roleColumn, width: '25px',isRoleColumn:true });
        }
    }
    gridColumns.push(
    	 	   { text: '特约演员',cellsrenderer: guestRoleListColumn, width: '90px' },
    	       { text: '群众演员',cellsrenderer: massRoleListColumn, width: '90px' },
    	       { text: '服装',cellsrenderer: clothesNameColumn,width: '90px' },
    	       { text: '化妆',cellsrenderer: makeupNameColumn, width: '90px' },
    	       { text: '道具',cellsrenderer: propsListColumn, width: '90px' },
    	       { text: '特殊道具',filedName: "specialPropsList", width: '90px' },
    	       { text: '备注',cellsrenderer: remarkColumn, width: '90px' },
    	       { text: '商植', cellsrenderer: advertNameColumn, width: '120px' },
    	       { text: '拍摄时间',cellsrenderer: shootDateColumn, width: '90px' },
    	       { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: '90px' }
    	   );
    
    var items = [];
	//生成隐藏列面板的所有项目名
	for(var k= 0; k< gridColumns.length; k++){
		if((k > 4)){
			if(k>6 && k<12){
				items.push('<li title="'+ gridColumns[k].title +'"><label>');
				if(jQuery.inArray(gridColumns[k].title, hideTdText) == -1){
					items.push('<input type="checkbox" checked onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].title);
				}else{
					items.push('<input type="checkbox" onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].title);
				}
				
				items.push('</label></li>');
			}else{
				items.push('<li title="'+ gridColumns[k].text +'"><label>');
				if(jQuery.inArray(gridColumns[k].text, hideTdText) == -1){
					items.push('<input type="checkbox" checked onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].text);
				}else{
					items.push('<input type="checkbox" onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].text);
				}
				items.push('</label></li>');
			}
			
		}
	}
	$("#columnsPanelItem").append(items.join(""));
    
    
    
    grid = new structureTable(gridColumns, '/scheduleManager/queryViewList', filter, 100, 0);
    grid.loadTable();
}

//水平滚动条事件
function showHiddenData(){
	var b = document.getElementById("bodyDiv").scrollLeft;
	document.getElementById("headerDiv").scrollLeft = b;
}

//构建表格
function structureTable(columnList, url, filter, pagesize, pagenum){
	isContinueQuery = true;
	var $this = this;
	var rowIndex = 0;
	this.filter = filter;
	this.source = [];
	this.columns = columnList;
	this.url = url;
	this.page = {pagesize:pagesize,pagenum: pagenum};
	this.continueAjaxRecords = false;
	
	//异步加载反馈 //需要一次握手之后才能继续
	this.handshake = true;
	
	
	
	//初始化
	this.loadTable = function(){
		this.createTableFrame();
		this.queryStatisticData();
	};
	//构建表格表头
	this.createTableFrame = function(){
		var tableObj = $("#viewList");
		tableObj.children().remove();
//		$("#headerDiv").children().remove();
//		$("#_table_doc").children().remove();
		tableObj.append('<div class="header-div" id="headerDiv"></div>');
		var _head = tableObj.find("#headerDiv");
		var html = [];
		html.push('<table class="header-table" cellspacing="0" cellpadding="0">');
		html.push('<tr>');
		var columnItem = this.columns;
		for(var i= 0; i< columnItem.length; i++){
			if(columnItem[i].isSearch){
				if(jQuery.inArray(columnItem[i].title, hideTdText) == -1){
					html.push('<td style="width:' + columnItem[i].width + '" cellid="'+ i +'">'+ columnItem[i].text +'</td>');
				}else{
					html.push('<td style="display: none; width:' + columnItem[i].width + '" cellid="'+ i +'">'+ columnItem[i].text +'</td>');
					hideTdIndex.push(i);
				}
			}else{
				if(columnItem[i].width){
					if(jQuery.inArray(columnItem[i].text, hideTdText) == -1){
						html.push('<td style="width: '+ columnItem[i].width +'" cellid="'+ i +'"><p style="width: '+ columnItem[i].width +'">' + columnItem[i].text + '</p></td>');
					}else{
						html.push('<td style="display: none; width: '+ columnItem[i].width +'" cellid="'+ i +'"><p style="width: '+ columnItem[i].width +'">' + columnItem[i].text + '</p></td>');
						hideTdIndex.push(i);
					}
				}else{
					if(jQuery.inArray(columnItem[i].text, hideTdText) == -1){
						html.push('<td style="width: 50px;"><p style="width: 50px;">'+ columnItem[i].text + '</p></td>');
					}else{
						html.push('<td style="display: none; width: 50px;"><p style="width: 50px;">'+ columnItem[i].text + '</p></td>');
						hideTdIndex.push(i);
					}
				}
			}
			
		}
		html.push('<td style="width: 18px; border: 0px;"><p style="width: 18px;"></p></td>');
		html.push('</tr>');
		_head.append(html.join(""));
		
		tableObj.append('<div class="body-div" id="bodyDiv"  onscroll="showHiddenData()"><div id="_table_doc"></div></div>');
		var height = $("#headerDiv").height();
		$("#bodyDiv").css({"height": "calc(100% - " + height + "px)"});
		
		//查询内容赋值(避免滚动条滚动问题)
		if($this.filter.shootLocationLike != null && $this.filter.shootLocationLike != undefined){//拍摄地
			$(".header-table").find("td[cellid=7]").find("input[type=text]").val($this.filter.shootLocationLike);
		}
		if($this.filter.majorLike != null && $this.filter.majorLike != undefined){//主场景
			$(".header-table").find("td[cellid=8]").find("input[type=text]").val($this.filter.majorLike);
		}
		if($this.filter.minorLike != null && $this.filter.minorLike != undefined){//次场景
			$(".header-table").find("td[cellid=9]").find("input[type=text]").val($this.filter.minorLike);
		}
		if($this.filter.thirdLevelLike != null && $this.filter.thirdLevelLike != undefined){//三级场景
			$(".header-table").find("td[cellid=10]").find("input[type=text]").val($this.filter.thirdLevelLike);
		}
		if($this.filter.mainContent != null && $this.filter.mainContent != undefined){//主要内容
			$(".header-table").find("td[cellid=11]").find("input[type=text]").val($this.filter.mainContent);
		}
		if(pasteFlag){
			$this.loading();
		}
		$this.getRecords();
	};
	//显示加载中
	this.loading = function(){
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
	};
	
	//继续获取数据
	this.getAjaxRecords = function(callback, filter){
		$this.page.pagenum = $this.page.pagenum +1;
		$this.filter.pagenum = $this.page.pagenum;
		$.ajax({
			url: $this.url,
			type: 'post',
			data: $this.filter,
			datatype: 'json',
			success: function(response){
				if(response.success){
					var resultList = response.result.resultList;
					$this.source = $this.source.concat(resultList);
					var result = response.result;
					$this.createTable(resultList);
					//显示第200条
					if(result.pageNo == 2){
						$("#bodyTable tbody:last").removeClass("hidden-tbody");
					}
					//判断是否还存在异步数据, 没有的话就开启全选按钮
					if(($this.page.pagenum < $this.page.pageCount) && $this.continueAjaxRecords){
						callback(callback, filter);
					}else{
						loadComplete = true;
						$this.handshake = true;
//						$this.upadateIndex();
						$("#selectAll").attr("disabled",false);//开启全选按钮
						$("#reverseSelect").attr("disabled", false);//开启反选按钮
						$("#loadingFlag").css({"display":"none"});
						//启用图例按钮
						$("#queryFinishView").attr("disabled", false);
						$("#queryHalfView").attr("disabled", false);
						$("#querydeleteView").attr("disabled", false);
						$("#loadingDataDiv").hide();
						$(".opacityAll").hide();
						if(pasteFlag) {
							document.getElementById("bodyDiv").scrollTop = scrollHeight;
							scrollHeight = 0;
							pasteFlag = false;
							cutRowId = [];
							//计算选中行数
							var trObj = $("#bodyTable").find("tr.select");
							var count = 0;
							$.each(trObj, function(){
								count++;
							});
							$(".count-flag").text(count);
						}
					}
				}
			}
		});
	};
	
	
	//获取记录
	this.getRecords = function(){
		$.ajax({
			url: $this.url,
			type: 'post',
			data: $this.filter,
			datatype: 'json',
			success: function(response){
				if(response.success){
					rowIndex = 0;
					//表格主体
					$("#_table_doc").append('<table class="body-table" cellspacing="0" cellpadding="0" id="bodyTable"></table>');
					var resultPageCount = response.result.pageCount;
					var resultList = response.result.resultList;
					var total = response.result.total;
					//生成表格
					$this.createTable(resultList);
					$this.source = $this.source.concat(resultList);
					
					$this.page.total = total;
					$this.page.pageCount = resultPageCount;
					//绑定事件
					$this.initTableEvent();
					
					//显示第一个100条数据
					$("#bodyTable tbody").removeClass("hidden-tbody");
					
					if($this.page.pageCount > 1){
						$this.continueAjaxRecords = true;
						$this.handshake = false;
						
//						//在所有的数据没有加载完之前, 全选是禁用的
						$("#selectAll").attr("disabled",true);
						$("#reverseSelect").attr("disabled", true);
						loadComplete = false;
						//禁用图例按钮
						$("#queryFinishView").attr("disabled", true);
						$("#queryHalfView").attr("disabled", true);
						$("#querydeleteView").attr("disabled", true);
						$("#loadingFlag").css({"display": "inline-block"});
						$this.getAjaxRecords($this.getAjaxRecords, $this.filter);
					}else{
						loadComplete = true;
						$this.handshake = true;
						if(pasteFlag) {
							document.getElementById("bodyDiv").scrollTop = scrollHeight;
							scrollHeight = 0;
							pasteFlag = false;
							cutRowId = [];
							//计算选中行数
							var trObj = $("#bodyTable").find("tr.select");
							var count = 0;
							$.each(trObj, function(){
								count++;
							});
							$(".count-flag").text(count);
						}
						$("#selectAll").attr("disabled",false);//开启全选按钮
						$("#reverseSelect").attr("disabled", false);//开启反选按钮
						$("#loadingFlag").css({"display":"none"});
						//启用图例按钮
						$("#queryFinishView").attr("disabled", false);
						$("#queryHalfView").attr("disabled", false);
						$("#querydeleteView").attr("disabled", false);
						$("#loadingDataDiv").hide();
						$(".opacityAll").hide();
					}
					
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	};
	//生成表格
	this.createTable= function(resultList){
		var tableObj = $("#bodyTable");
		var tbody = $('<tbody class="hidden-tbody"></tbody>');
		var _rowArray = [];
		for(var i=0; i< resultList.length; i++){
			if($this.createRow(resultList[i], rowIndex) != ''){
				_rowArray.push($this.createRow(resultList[i], rowIndex));
			}
			rowIndex++;
		}
		tbody.append(_rowArray.join(""));
		tableObj.append(tbody);
		
		
		
	};
	//创建表格的行
	this.createRow = function(rowData, rowIndex){
		var _row = [];
		var style = "";
		
		if(rowData.shootStatus != ""){
			
			style = " style='background-color:"+getColor(rowData.shootStatus)+";' ";
		}
//		if(jQuery.inArray(rowData.viewId,cutRowId) == -1){//判断是否是剪切但未粘贴的数据
		if(pasteFlag){
			if(jQuery.inArray(rowData.viewId, cutRowId) != -1){
				_row.push('<tr '+ style +' class="select" id="'+ rowData.viewId +'" shootstatus="'+ rowData.shootStatus +'" tr-index="'+ rowIndex +'" groupid="'+ rowData.groupId +'" onmousedown="checkFirstTr(this,event)" onmouseup="calculateSelectTr(this,event)">');
			}else{
				_row.push('<tr '+ style +' id="'+ rowData.viewId +'" shootstatus="'+ rowData.shootStatus +'" tr-index="'+ rowIndex +'" groupid="'+ rowData.groupId +'" onmousedown="checkFirstTr(this,event)" onmouseup="calculateSelectTr(this,event)">');
			}
		}else{
			_row.push('<tr '+ style +' id="'+ rowData.viewId +'" shootstatus="'+ rowData.shootStatus +'" tr-index="'+ rowIndex +'" groupid="'+ rowData.groupId +'" onmousedown="checkFirstTr(this,event)" onmouseup="calculateSelectTr(this,event)">');
		}	
		
			
			var columnList =  $this.columns;
			for(var i=0; i< columnList.length; i++){
				if(columnList[i].cellsrenderer){
					if(jQuery.inArray(i,hideTdIndex) == -1){
						_row.push('<td cellid="'+ i +'" style="width:' + columnList[i].width + '"><p style="width:' + columnList[i].width + ';">'+columnList[i].cellsrenderer(columnList[i].filedName,rowData[columnList[i].filedName],columnList[i],rowData)+'</p></td>');
					}else{
						_row.push('<td cellid="'+ i +'" style="display: none; width:' + columnList[i].width + '"><p style="width:' + columnList[i].width + ';">'+columnList[i].cellsrenderer(columnList[i].filedName,rowData[columnList[i].filedName],columnList[i],rowData)+'</p></td>');
					}
					
				}else{
					if(jQuery.inArray(i,hideTdIndex) == -1){
						_row.push('<td cellid="'+ i +'" style="width:' + columnList[i].width + '"><p title="'+rowData[columnList[i].filedName]+'" style="width:' + columnList[i].width + ';">'+rowData[columnList[i].filedName]+'</p></td>');
					}else{
						_row.push('<td cellid="'+ i +'" style="display: none; width:' + columnList[i].width + '"><p title="'+rowData[columnList[i].filedName]+'" style="width:' + columnList[i].width + ';">'+rowData[columnList[i].filedName]+'</p></td>');
					}
					
				}
			}
			_row.push('</tr>');
			return _row.join("");
//		}else{
//			return '';
//		}
		
	};
	this.getRowData = function(rowIndex){
		return $this.source[rowIndex];
	};
	this.initTableEvent = function(){
		/*document.getElementById("bodyDiv").scrollTop=0;*/
		var _tableObj = $("#bodyTable");
		var pageCount = $this.page.pageCount;
		var total = $this.page.total;
		//一个body的高度
		var bodyheight = _tableObj.find("tbody:eq(0)").height();
		
		//设置文档的高度
		$("#_table_doc").css("height", 30 * total);
		
		//拉动滚动条产生的重复事件
		var timeoutnum = 0;
		
		//记录那个body在窗口显示
		var preId = 0;
		$("#bodyDiv").bind("scroll", function(evt){
			//清除重复的事件
			window.clearTimeout(timeoutnum);
			
			timeoutnum = setTimeout(function(){
                var target = $(evt.currentTarget);
				
				var scollTop = target.scrollTop();
				
				var id = (scollTop - scollTop % bodyheight)/bodyheight;
				//如果相同就不更新数据
				if(preId != id){
					
					preId = id;
					
					//解决滚动条长距离拉动的问题
					_tableObj.find("tbody").addClass("hidden-tbody");
					
					//如果在异步加载之前滚动, 判断异步加载是否完成
					if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
						
						(function pollingBody(){
							timer1 = setTimeout(function(){
								
								if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
									
									pollingBody();
								}else{
									
									_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
									
									$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
								}
								
							},1000);
						}).call(this);
						
					}else{
						
						_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
						
						$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
					}
				}
				
			},0);
		});
	};
	//查询统计数据
	this.queryStatisticData = function(){
		$.ajax({
			url: '/scheduleManager/queryViewTotal',
			type: 'post',
			data: $this.filter,
			datatype: 'json',
			success: function(response){
				if(response.success){
					var viewStatistics = response.viewStatistics;
					//总场数
					var statisticsViewCount = viewStatistics.statisticsViewCount;
					//总页数
					var statisticsPageCount = viewStatistics.statisticsPageCount;
					//状态分类统计
					var statisticsShootStatus = viewStatistics.statisticsShootStatus;
					//内外景统计
					var statisticsSite = viewStatistics.statisticsSite;
					//场景总数
					var statisticsHTML = "场景统计：共"+statisticsViewCount[0].funResult+"场";
					
					statisticsHTML+="&nbsp;&nbsp;&nbsp;&nbsp;"+statisticsPageCount[0].funResult.toFixed(1)+"页";
					
					//状态统计
					var shootStatusKeys = shootStatusMap.keys();
					for(var i=0;i<shootStatusKeys.length;i++){
						var shootStatusKey = shootStatusKeys[i];
						for(var j = 0;j<statisticsShootStatus.length;j++){
							if(shootStatusKey==statisticsShootStatus[j].shootStatus){
								statisticsHTML+="&nbsp;&nbsp;&nbsp;&nbsp;"+shootStatusMap.get(shootStatusKey)+statisticsShootStatus[j].funResult+"场 ";
								break;
							}else if(j==statisticsShootStatus.length-1){
								statisticsHTML+="&nbsp;&nbsp;&nbsp;&nbsp;"+shootStatusMap.get(shootStatusKey)+"0场 ";
							}
						}
					}
					
					//内外景统计
					var siteKeys = siteMap.keys();// |  &nbsp;气氛2场 &nbsp;
					for(var i=0;i<siteKeys.length;i++){
						var siteKey = siteKeys[i];
						for(var j = 0;j<statisticsSite.length;j++){
							if(siteKey==statisticsSite[j].site){
								statisticsHTML+="&nbsp;&nbsp;&nbsp;&nbsp;"+siteMap.get(siteKey)+statisticsSite[j].funResult+"场 ";
								break;
							}else if(j==statisticsSite.length-1){
								statisticsHTML+="&nbsp;&nbsp;&nbsp;&nbsp; "+siteMap.get(siteKey)+"0场 ";
							}
						}
					}
					
					$("#viewSummary").html(statisticsHTML);
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	};
	this.setFilter = function(filter){
		$this.filter = filter;
	};
	this.goToPage = function(pageNo){
		$this.filter.pagenum = pageNo;
		this.continueAjaxRecords = false;
		
		var inter = null;
		
		//判断上一次的操作的异步加载是否结束, 如果没有结束, 在下一次告诉他结束异步加载, 并且反馈回来.
		inter = setInterval(function(){
			
			if($this.handshake){
				
				clearInterval(inter);
				var pageSize = 100;
				
				if(pageNo==null || pageNo == undefined){
					
					pageNo= 0;
				}
				
				$this.page.pagenum=pageNo;
				
				if(pageSize){
					$this.page.pagesize=pageSize;
				}
				
				$this.loadTable();
			}
			
		},50);
	};
	
	
	this.upadateIndex = function(){//更新所有行号
		var allTrs = $("#bodyTable").find("tr");
		$.each(allTrs, function(index){
			$(this).attr("tr-index", index);
		});
	};
	this.updateNextIndex = function(index, id){//更新下面所有行号
		var nextAll = $("tr[id="+id +"]").nextAll("tr");
		$.each(nextAll, function(){
			index ++;
			$(this).attr("tr-index", index);
		});
	};
	
	this.refresh = function(){
		
		this.loadTable();
	};
	this.getRowIndex = function(viewId){
		
		if(!viewId){
			return null;
		}
		return parseInt($("#"+viewId).attr("tr-index"));
	};
	//获取选中行的Id
	this.getSelectIds = function(){
		
		var result = "";
		
		var _tableObj = $("#bodyTable");
		
		_tableObj.find("tbody tr.select").each(function(index){
			
			if(index == 0){
				
				result = $(this).attr("id");
			}else{
				result += ","+$(this).attr("id");
			}
		});
		
		return result;
	};
	//全不选
	this.unSelectedAll=function(){
		
		var _tr = $("#bodyTable").find("tbody tr.select");
		$.each(_tr, function(){
			$(this).removeClass("select");
		});
		$(".count-flag").html($("#bodyTable tr.select").length);
	};
	this.updateRow= function(index, rowData){
		var _row = $this.createRow(rowData, index);
		var $row = $(_row);
		
		//然后替换表格中指定的行
		$("#_table_doc").find("tr[tr-index="+ index +"]").replaceWith($row);
	};
	this.updateCellData = function(index, cellid, cellData){
		var td = $("tr[tr-index="+index+"]").find("td[cellid="+cellid+"]");
		td.find("p").text(cellData);
	};
}

//获取拍摄状态的颜色
function getColor(shootStatus){
	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(shootStatus);
	return divColor;
}

//行选中事件所需参数
var cutData = [];//grid表格所剪切的数据
var cutTrDom = [];//grid表格所克隆的行
var $rightDom = null;//鼠标右键当前行
var $rightDomIndex;//鼠标右键当前行的index
//var checkFlag = false;
var nowTr;//当前鼠标左键选择的行
//快速选择行
function checkFirstTr(own,event){
	var index = $(own).attr("tr-index");
	if(event.button == 0){//鼠标左键按下
		$("#shortCutMenu").hide();
		if(shiftFlag){//是否按住shift键
			shiftFlag = false;
//			checkFlag = false;
			var selectTrs = $("#bodyTable").find("tr.select");
			$.each(selectTrs, function(){
				$(this).removeClass("select");
			});
			nowTr.addClass("select");
			if(nowTr.hasClass("select")){
				var lastIndex = parseInt(nowTr.attr("tr-index"));
				var nowIndex = parseInt($(own).attr("tr-index"));
				if(lastIndex < nowIndex){//向下选择
					for(var i= lastIndex; i<= nowIndex; i++){
						$("tr[tr-index="+ i +"]").addClass("select");
					}
				}else if(lastIndex > nowIndex){//向上选择
					for(var i= lastIndex; i>= nowIndex; i--){
						$("tr[tr-index="+ i +"]").addClass("select");
					}
				}
				else{
					$(own).addClass("select");
				}
			}else{
				$(own).addClass("select");
			}
		}else if(ctrlFlag){
			ctrlFlag = false;
			if($(own).hasClass("select")){
				$(own).removeClass("select");
			}else{
				$(own).addClass("select");
			}
			nowTr = $(own);
		}else{
			shiftFlag = false;
			ctrlFlag = false;
			var selectTrs = $("#bodyTable").find("tr.select");
			$.each(selectTrs, function(){
				$(this).removeClass("select");
			});
//			$("#bodyTable").find("tr.select").removeClass();
			$(own).addClass("select");
			nowTr = $(own);
		}
	}else if(event.button == 2){//鼠标右键按下
		if($(own).hasClass("select")){
			var left = event.pageX;
			var top = event.pageY-80;
			$("#shortCutMenu").css({"left": left, "top": top}).show();
			$rightDom = $(own);//只有选中状态 才可改变当前右击的对象
			$rightDomIndex = index;
		}
	}
//	event.preventDefault();//不能阻止冒泡和默认事件
//	event.stopPropagation();
}





function calculateSelectTr(own,event){
	var trObj = $("#bodyTable").find("tr.select");
	var count = 0;
	$.each(trObj, function(){
		count++;
	});
	$(".count-flag").text(count);
}



//鼠标按下状态滑动时继续选中划过的行
function continueTr(own, e){
//	if(checkFlag){
//		if($(own).hasClass("select")){
//			$(own).removeClass("select");
//			$(own).removeClass("shift");
//		}else{
//			$(own).addClass("select");
//		}
//	}
}
//停止继续选中
function stopCheckTr(own){
//	checkFlag = false;
//	var trObj = $("#bodyTable").find("tr.select");
//	var count = 0;
//	$.each(trObj, function(){
//		count++;
//	});
//	$(".count-flag").text(count);
}
//剪切
function cutTrData(){
	if(cutTrDom.length != 0){//清除之前剪切的数据
		var cutRow = $("#bodyTable").find("tr.cut-row");
		$.each(cutRow, function(){
			$(this).removeClass("cut-row");
		});
		cutTrDom = [];
		cutData = [];
		cutRowId = [];
	}
	var cutTr = $("#bodyTable").find("tr.select");
	$.each(cutTr, function(i){
//		var groupId = $(this).attr("groupid");
//		if(groupId == undefined || groupId == "" || groupId == null || groupId == "null"){
//			//没有groupid说明是未分组，不允许剪切
//			showInfoMessage("所选数据包含未分组数据，请重新选择");
//			return;
//		}
		var data = {};
		var cell = [];
		data.index = $(this).attr("tr-index");
		data.id = $(this).attr("id");
		var tds = $(this).find("td");
		$.each(tds, function(i){
			cell.push($(this).text());
		});
		data.cellsValue = cell;
		cutData.push(data);
		var _tr = $(this).clone(true);
		cutTrDom.push(_tr);
		cutRowId.push($(this).attr("id"));//当前剪切行的id
		$(this).addClass("cut-row");
	});
	$("#shortCutMenu").hide();
	console.log(cutData);
	console.log(cutRowId);
	//计算选中行数
	var trObj = $("#bodyTable").find("tr.select");
	var count = 0;
	$.each(trObj, function(){
		count++;
	});
	$(".count-flag").text(count);
}
			
//粘贴
function pasteTrData(){
	if(cutData.length != 0 && cutTrDom.length != 0){
		if(!loadComplete){
			showInfoMessage("请耐心等候数据加载完成后再进行操作");
			return;
		}
		var groupId = $rightDom.attr("groupid");
		var viewId = $rightDom.attr("id");
		if(groupId == undefined || groupId == null || groupId == "" || groupId == "null"){
			showInfoMessage("不能将已分组的数据移动到未分组中");
			return;
		}
		$.ajax({
			url: '/scheduleManager/setViewScheduleGroup',
			type: 'post',
			data: {"viewIds": cutRowId.join(","),  "targetViewId": viewId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					pasteFlag = true;
					$rightDom.before(cutTrDom);
					$("#bodyTable").find("tr.cut-row").remove();
					//改变行号
					grid.upadateIndex();
					$.each(cutTrDom, function(){//手动改变粘贴数据的组id
						$(this).attr("groupid", groupId);
					});
					cutTrDom = [];
					cutData = [];
//					cutRowId = [];
					groupIframe.window.queryPlanGroupList();//重新刷新分组列表
//					$("#bodyDiv").unbind("scroll");
//					document.getElementById("bodyDiv").scrollTop = 0;
//					grid.initTableEvent();
					scrollHeight = document.getElementById("bodyDiv").scrollTop;
					grid.setFilter(filter);
					grid.goToPage(0);
					
				}else{
					showErrorMessage(response.message);
				}
			}
		});
		$("#shortCutMenu").hide();
	}
	
}




//全选
function selectAll(own){
//	$(own).siblings("button").removeClass("checked");
//	if($(own).is(":checked")){
//		var trs = $("#bodyTable").find("tr");
//		var count = 0;
//		$.each(trs, function(){
//			count++;
//			$(this).addClass("select");
//		});
//		$(".count-flag").text(count);
//	}else{
//		var trs = $("#bodyTable").find("tr.select");
//		$.each(trs, function(){
//			$(this).removeClass("select");
//		});
//		$(".count-flag").text("0");
//	}
	var trs = $("#bodyTable").find("tr");
	var count = 0;
	$.each(trs, function(){
		count++;
		$(this).addClass("select");
	});
	$(".count-flag").text(count);
}

//反选
function reverseSelect(own){
//	$("#selectAll").prop("checked", false);
	var trs = $("#bodyDiv tr").not(".select");
	var tr = $("#bodyDiv").find("tr.select");
	var count = 0;
	$.each(tr, function(){
		$(this).removeClass("select");
	});
	$.each(trs, function(){
		count++;
		$(this).addClass("select");
	});
	$(".count-flag").text(count);
}

//查询全部的场景
function queryAllView(own){
	$(".count-flag").html(0);
	groupIframe.window.checkIndex = "";//改变子窗口变量的值；
	var _frame = $('#groupIframe').contents();
	_frame.find("li.checked").removeClass("checked");
	$(own).addClass("click-all");
	$(own).addClass("checked");
	$("#rightPanelTitle").text("全部分组");
	resetLegendBtn();
	filter = {};
	filter.pagesize = 100;
	grid.setFilter(filter);
	grid.goToPage(0);
}
//查询完成的场景
function queryView(own, value){
	if($(own).hasClass("checked")){
		$(own).removeClass("checked");
		$(own).css({"background":"#ccc"});
		$("tr[shootstatus="+value+"]").each(function(){
			$(this).hide();
		});
	}else{
		$(own).addClass("checked");
		var style = "";
		style = getColor(value);
		$(own).css({"background": style});
		$("tr[shootstatus="+value+"]").each(function(){
			$(this).show();
		});
	}
	
}
//分组点击事件
function groupClick(id, groupName){
	$("#rightPanelTitle").text(groupName);
	resetLegendBtn();
	filter.scheduleGroupId = id;
	$(".count-flag").html(0);
//	$("#selectAll").attr('checked',false);
	grid.setFilter(filter);
	grid.goToPage(0);
}

//显示隐藏列面板
function showColumnsPanel(own, event){
	var left = $(own).position().left;
	var top = $(own).position().top;
	var height = $(own).outerHeight();
	$("#columnsPanel").css({"left": left, "top": top+height+5}).toggle();
	event.stopPropagation();
}
//显示或隐藏列
function showOrHideColumn(own){
	var index = $(own).attr("td-index");
	var text= $(own).parent("label").text();
	index = parseInt(index);
	if($(own).is(":checked")){
		$("td[cellid="+index+"]").show();
		hideTdIndex.splice(jQuery.inArray(index,hideTdIndex),1); //移除数组中的指定元素
		hideTdText.splice(jQuery.inArray(text, hideTdText),1);
	}else{
		$("td[cellid="+index+"]").hide();
		hideTdIndex.push(index);
		hideTdText.push(text);
	}
	$.ajax({
		url: '/cacheManager/saveCacheInfo',
		type: 'post',
		data: {"type": 1, content: hideTdText.join(",")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				
			}else{
				showErrorMessage(response.message);
			}
		}
	});
//	console.log(index);
//	console.log(hideTdIndex);
//	console.log(hideTdText);
}
//阻止冒泡事件
function objEvent(event){
	event.stopPropagation();
}

//计划分组列表改变顺序后，显示全部数据时需要重新刷新场景列表
function refrushGrid(){
	grid.setFilter(filter);
	grid.goToPage(0);
}

//重新查询数据后，将图例按钮恢复正常状态
function resetLegendBtn(){
	$(".status-btn-list").find("input[type=button]").addClass("checked");
	$("#queryFinishView").css({"background": "#ffbaba"});
	$("#queryHalfView").css({"background": "#fee9fa"});
	$("#querydeleteView").css({"background": "#d3f0ff"});
}


//计划编辑
function editedPlan(){
	$("#viewPlanInfo").hide();
	$("#myContainer").show();
	$("#viewPlanDetail").attr("src", "");
	$("#viewPlanDetail").empty();
}

//计划查看
function viewPlan(){
	$("#myContainer").hide();
	$("#viewPlanInfo").show();
	$("#viewPlanDetail").attr("src", "/scheduleManager/toScheduleCalendarPage");
}
//计划详情
function planDetail(){
	$("#myContainer").hide();
	$("#viewPlanInfo").show();
	$("#viewPlanDetail").empty();
	$("#viewPlanDetail").attr("src", "/scheduleManager/toScheduleDetailPage");
}

//初始化导入窗口
function initImportWin(){
	$("#importPlanWin").jqxWindow({
		theme: theme,
		height: 540,
		width: 482,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			
		}
	});
}

//关闭导入窗口
function closeImportWin(){
	$("#importPlanWin").jqxWindow("close");
}

//导入数据
function importPlanList(){
	$("#importPlanWin").jqxWindow("open");
	var templateUrl;
	if (crewType == 0 || crewType == 3) {
		templateUrl= "/template/import/计划（电影）导入模板.xls";
	} else {
		templateUrl= "/template/import/计划（电视剧）导入模板.xls";
	}
	$("#importIframe").attr("src", "/importManager/toImportPage?uploadUrl=/scheduleManager/importSchedule&&needIsCover=true&&refreshUrl=/scheduleManager/toScheduleListPage&&templateUrl="+ templateUrl +"&&queryDelete=true");
}
//导出数据
function exportPlanList(url){
	//显示loading效果
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
	
	window.location.href= url;
	_LoadingHtml.hide();
	$(".opacityAll").hide();
}