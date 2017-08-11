var filter = {};
var viewFilter = {};
filter.pagenum = 0,viewFilter.pagenum = 0;
filter.pagesize = 3, viewFilter.pagesize = 100;
viewFilter.sortField = "planShootDate";
var pageCount, viewPageCount;
var crewType = null;
var resultData;
var gridColumns = [];
var hideTdIndex = [];//隐藏列的id
var hideTdText = [];//隐藏列的标题
$(function(){
	if(isScheduleReadonly){
		$("#editedPlanTab").hide();
	}
	if(!hasExportScheduleAuth){//导出权限
		$(".export-btn").hide();
	}
	
	//加载隐藏列的信息
	loadHideColumnInfo();
	//获取剧组类型
	getCrewType();
	draw("backImg");
});

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
				parent.showErrorMessage(response.message);
			}
		}
	});
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
					parent.showErrorMessage(response.message);
			}
		}
	});
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
//				var groupList = response.groupList;
				//初始化计划详细信息表格
				initPlanDetialGrid(majorRoleList);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//初始化表格
function initPlanDetialGrid(majorRoleList){
	//加载角色列信息
    roleColumn = function (columnfield, value, columnproperties, rowdata) {
    	//操作列html
    	var thisRowData = rowdata;
    	
    	var roleArray = thisRowData["roleList"];
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
	//加载集次编号信息
    viewColumn = function(columnfield, value, columnproperties, rowdata){
		var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
//    	return "<span style=''  class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' >" + seriesNoAndViewNo + "</span>";
		return seriesNoAndViewNo;
    };
    
    //加载场次编号
    viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
        var viewNo =rowdata.viewNo;
//        return "<span style='' class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' >" + viewNo + "</span>";
        return viewNo;
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
//    	return "<span title='"+ seasonText +"'>" + seasonText + "</span>";
    	return seasonText;
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
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载次场景信息
    minorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.minorView){
    		text=rowdata.minorView;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载三级场景信息
    thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.thirdLevelView){
    		text=rowdata.thirdLevelView;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载主要内容信息
    mainContentColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.mainContent){
    		text=rowdata.mainContent;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
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
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载主要演员信息
    massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.massRoleList){
    		text=rowdata.massRoleList;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载道具信息
    propsListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.propsList){
    		text=rowdata.propsList;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载服装信息
    clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.clothesName){
    		text=rowdata.clothesName;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载化妆信息
    makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.makeupName){
    		text=rowdata.makeupName;
    	}
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
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
//    	return "<span title='"+ text +"'>" + text + "</span>";
    	return text;
    };
    
    //加载拍摄地点信息
    shootLocationColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootLocation){
    		text=rowdata.shootLocation;
    	}
    	if(rowdata.shootRegion){
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
	var roleArray = [];
	gridColumns = [];
	for(var i= 0; i < majorRoleList.length; i++){
		var role = majorRoleList[i];
		roleArray.push({text: role.viewRoleName, value: role.viewRoleId});
	}
	gridColumns.push({text: "",   width: "200px"});
	//电影类型的剧组不显示集次
    if (crewType == 0 || crewType == 3) {
      gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn,  width: '65px'});
    } else {
      gridColumns.push({ text: '集-场', cellsrenderer: viewColumn,  width: '65px'});
    }
  //加载数据列,拼接场景列表的表头信息
    gridColumns.push(
			{ text: '特殊提醒', cellsrenderer: seasonColumn,  width: '70px'},
            { text: '气氛', cellsrenderer: atmosphere, width: '40px'},
            { text: '内外景', cellsrenderer:siteColumn, width: '50px'},
            { text: '拍摄地', cellsrenderer: shootLocationColumn, width: '150px'},
            { text: '主场景', cellsrenderer:majorViewColumn, width: '120px'},
            { text: '次场景', cellsrenderer:minorViewColumn, width: '120px'},
            { text: '三级场景', cellsrenderer: thirdLevelViewColumn, width: '120px'},
            { text: '主要内容', cellsrenderer: mainContentColumn, width: '150px'},
            { text: '页数', cellsrenderer: pageCountColumn, width: '40px'}
     );
    if(roleArray.length != 0){
    	for(var i = 0; i< roleArray.length; i++){
            gridColumns.push({ text: roleArray[i].text, cellsrenderer: roleColumn, width: '25px'});
        }
    }
    gridColumns.push(
    	 	   { text: '特约演员', cellsrenderer: guestRoleListColumn, width: '90px' },
    	       { text: '群众演员', cellsrenderer: massRoleListColumn, width: '90px' },
    	       { text: '服装', cellsrenderer: clothesNameColumn, width: '90px' },
    	       { text: '化妆', cellsrenderer: makeupNameColumn, width: '90px' },
    	       { text: '道具', cellsrenderer: propsListColumn, width: '90px' },
    	       { text: '特殊道具', filedName: "specialPropsList",  width: '90px' },
    	       { text: '备注', cellsrenderer: remarkColumn, width: '90px' },
    	       { text: '商植', cellsrenderer: advertNameColumn, width: '120px' },
    	       { text: '拍摄时间', cellsrenderer: shootDateColumn, width: '90px' },
    	       { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: '90px' }
    	   );
    //创建表头
    createHeader(gridColumns);
}

//创建表头
function createHeader(gridColumns){
	var tableObj = $('<div class="grid-header" id="gridHeader"><table class="header-table" id="headerTable" cellspacing="0" cellpadding="0"></table></div>');
	var header = [];
	hideTdIndex = [];
	header.push('<tr>');
	var width= 0;
	var body = [];
	body.push('<tr style="height:0px;">');
	for(var i= 0; i< gridColumns.length; i++){
		if(jQuery.inArray(gridColumns[i].text, hideTdText) == -1){
			if(i == 1){
				var columnWidth = parseInt(gridColumns[i].width) +1;
				header.push('<td style="width:'+ columnWidth +'px;" cellid="'+ i +'"><p style="width:'+ gridColumns[i].width +';">' + gridColumns[i].text + "</p></td>");
			}else{
				header.push('<td style="width:'+ gridColumns[i].width +';" cellid="'+ i +'"><p style="width:'+ gridColumns[i].width +';">' + gridColumns[i].text + "</p></td>");
			}
			width = add(width, parseInt(gridColumns[i].width));
			body.push('<td style="width:'+ gridColumns[i].width +'; border:0px; height: 0px;" cellid="'+ i +'"><p style="width:'+ gridColumns[i].width +'; height:0px;"></p></td>');
		}else{
			if(i == 1){
				var columnWidth = parseInt(gridColumns[i].width) +1;
				header.push('<td style="display: none; width:'+ columnWidth +'px;"><p style="width:'+ gridColumns[i].width +';">' + gridColumns[i].text + "</p></td>");
			}else{
				header.push('<td style="display: none; width:'+ gridColumns[i].width +';"><p style="width:'+ gridColumns[i].width +';">' + gridColumns[i].text + "</p></td>");
			}
			
			hideTdIndex.push(i);
			body.push('<td style="display: none; width:'+ gridColumns[i].width +'; height: 0px; border:0px;" cellid="'+ i +'"><p style="width:'+ gridColumns[i].width +'; height:0px;"></p></td>');
		}
		
	}
	header.push('<td style="width:18px; border: 0px; background: #fff;"><p style="width:18px;"></p></td>');
	header.push('</tr>');
	body.push('</tr>');
	tableObj.find("#headerTable").append(header.join(""));
	$("#planDetailGrid").append(tableObj);
	$("#planDetailGrid").append('<div class="grid-body" id="gridBody" onscroll="showHiddenData()"><table class="body-table" id="bodyTable" cellspacing="0" cellpadding="0"></table></div>');
	var height = $("#gridHeader").height();
	$("#gridBody").css({"height": "calc(100% - "+ height+"px)"});
		
	$("#headerTable").width(width);
	$("#bodyTable").width(width);	
	$("#bodyTable").append(body.join(""));
	//查询表格数据
	queryListData();
	
}

//水平滚动条事件
function showHiddenData(){
	var b = document.getElementById("gridBody").scrollLeft;
	document.getElementById("gridHeader").scrollLeft = b;
	var top = document.getElementById("gridBody").scrollTop;
	if(top >= 200){
		$("#gotoTop").show();
	}else{
		$("#gotoTop").hide();
	}
}


//查询表格数据
function queryListData(){
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
//	$(".opacityAll").css({opacity:0.3,width:'100%',height:'100%'}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
//	_LoadingHtml.show();
	requestMethod();
}

//请求方法
function requestMethod(){
	$.ajax({
		url: '/scheduleManager/queryScheduleDetailInfo',
		type: 'post',
		data: filter,
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var result = response.result;
				resultData = result.resultList;
				pageCount = result.pageCount;
				initTableBody();
//				if(pageCount > 1){
//					queryOtherPageData();
//				}else{
//					//取消loading效果
//					$("#loadingDataDiv").hide();
//					$(".opacityAll").hide();
//				}
			}else{
				//取消loading效果
//				_LoadingHtml.hide();
//				$(".opacityAll").hide();
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//请求其他分页数据
function queryOtherPageData(){
	filter.pagenum +=1;
	if(filter.pagenum >= pageCount){
		//取消loading效果
//		$("#loadingDataDiv").hide();
//		$(".opacityAll").hide();
		return;
	}
	$.ajax({
		url: '/scheduleManager/queryScheduleDetailInfo',
		data: filter,
//		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var result = response.result;
				resultData = result.resultList;
				pageCount = result.pageCount;
				initTableBody();
			}
		}
	});
}




//创建表体
function initTableBody(){
	for(var i= 0; i< resultData.length; i++){
		var html = [];
		html.push('<tr></tr>');
		var result = resultData[i];
		var scheduleId = result.scheduleId;
		var columns = gridColumns.length-1;
	    html.push('<tr>');
	    html.push('<td style="width:200px; background: #fff;" rowspan="2">');
	    html.push(' <div class="plan-calendar">');
	    if(result.dayNum == null){
			result.dayNum = "";
		}
	    if(result.dayNum != ""){
			html.push('  <p class="plan-calendar-title">第' + result.dayNum + '天</p>');
		}else{
			html.push('  <p class="plan-calendar-title">待定</p>');
		}
		if(result.planShootDate == null){
			result.planShootDate = "";
		}
		if(result.planShootGroup == null){
			result.planShootGroup = "";
		}
		if(result.planShootGroup == "单组"){
			result.planShootGroup = "";
	    }
		var str = result.planShootDate + result.planShootGroup;
		html.push('  <p class="plan-date-group">'+ str +'</p>');
		html.push(' </div>');
	    html.push('</td>');
	    html.push('<td colspan="'+ columns +'">');
	    html.push('<div class="grid-body-content" ><table class="body-table"  cellspacing="0" cellpadding="0" id="data_table_'+ scheduleId +'"></table></div>');
	    html.push('</td></tr>');
		//最后一行(不拼第一列)
		html.push('<tr>');
		var countLength = gridColumns.length - 1;
		html.push('<td colspan="'+ countLength +'">');
		if(result.finishedViewNum == null){
			result.finishedViewNum = 0;
		}
		if(result.finishedPageCount == null){
			result.finishedPageCount = 0;
		}
		if(result.shootLocation == null){
			result.shootLocation = "";
		}
		var str = "";
		if(result.majorRole != null && result.guestRole != null){
			str = result.majorRole + "&nbsp;&nbsp;|&nbsp;&nbsp;" + result.guestRole;
		}else if(result.majorRole != null && result.guestRole == null){
			str = result.majorRole;
		}
		else if(result.majorRole == null && result.guestRole != null){
			str = result.guestRole;
		}
		var title= '共' + result.viewNum + '场&nbsp;&nbsp;完成' + result.finishedViewNum + '场&nbsp;&nbsp;共' + result.pageCount + '页&nbsp;&nbsp;完成' + result.finishedPageCount + '页&nbsp;&nbsp;&nbsp;&nbsp;拍摄地点:&nbsp;&nbsp;' + result.shootLocation + '&nbsp;&nbsp;&nbsp;&nbsp;主演/特约:&nbsp;&nbsp;'+str;
		html.push(' <p class="summary-info" title="'+ title +'">');
		
		html.push('共' + result.viewNum + '场&nbsp;&nbsp;完成' + result.finishedViewNum + '场&nbsp;&nbsp;共' + result.pageCount + '页&nbsp;&nbsp;完成' + result.finishedPageCount + '页&nbsp;&nbsp;&nbsp;&nbsp;拍摄地点:&nbsp;&nbsp;' + result.shootLocation);
		
		html.push('&nbsp;&nbsp;&nbsp;&nbsp;主演/特约:&nbsp;&nbsp;' + str);
		html.push(' </p>');
		html.push('</td>');
		html.push('</tr>');
		$("#bodyTable").append(html.join(""));
		var width = $("#bodyTable").width();
		$(".grid-body-content").find("table").css({"width":width-200});
		
		//查询表体数据
	    viewFilter.pagenum = 0;
		queryViewInfo(scheduleId,result);
	}
	if(filter.pagenum < pageCount){
		queryOtherPageData();
	}
}

//查询场景表数据
function queryViewInfo(scheduleId, result){
	viewFilter.scheduleIds = scheduleId;
	$.ajax({
		url: '/scheduleManager/queryViewList',
		type: 'post',
		async: false,
		data: viewFilter,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var viewInfo = response.result.resultList;
				viewPageCount = response.result.pageCount;
				createTableContent(viewInfo, result);
				return;
//				if(viewPageCount > 1){
//					viewFilter.pagenum +=1;
//					queryOtherPageView(result);
//				}else{
//					if(filter.pagenum < pageCount){
//						queryOtherPageData();
//					}
//				}
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//查询其他页的场景表数据
function queryOtherPageView(result){
	viewFilter.scheduleIds = result.scheduleId;
	if(viewFilter.pagenum % 2 == 0 && viewFilter.pagenum != 0){
		$("table[id^='data_table_"+ result.scheduleId +"']").find("tbody.hidden-tbody").removeClass("hidden-tbody");
	}
	/*if(viewFilter.pagenum >= viewPageCount){
		$("table[id^='data_table_"+ result.scheduleId +"']").find("tbody.hidden-tbody").removeClass("hidden-tbody");
		return;
	}*/
	$.ajax({
		url: '/scheduleManager/queryViewList',
		type: 'post',
		data: viewFilter,
//		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var viewInfo = response.result.resultList;
				createTableContent(viewInfo, result);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}



//创建表格内数据
function createTableContent(viewInfo, result){
	
	var html = [];
	if(viewFilter.pagenum >= 1){
		html.push('<tbody class="hidden-tbody">');
	}else if(viewFilter.pagenum == viewPageCount){
		html.push('<tbody>');
	}
	else{
		html.push('<tbody>');
	}
	html.push('<tbody>');
    for(var j= 0; j< viewInfo.length; j++){
		var color = getColor(viewInfo[j].shootStatus);
		html.push('<tr style="background:'+ color +';">');
		var rowData = viewInfo[j];
		for(var k=1; k< gridColumns.length; k++){
			if(gridColumns[k].cellsrenderer){
				if(jQuery.inArray(k,hideTdIndex) == -1){
//					html.push('<td cellid="'+ k +'" style="width:' + gridColumns[k].width + '"><p style="width:' + gridColumns[k].width + ';">'+gridColumns[k].cellsrenderer(gridColumns[k].filedName,rowData[gridColumns[k].filedName],gridColumns[k],rowData)+'</p></td>');
					html.push('<td cellid="'+ k +'" style="width:' + gridColumns[k].width + '">'+gridColumns[k].cellsrenderer(gridColumns[k].filedName,rowData[gridColumns[k].filedName],gridColumns[k],rowData)+'</td>');
				}else{
//					html.push('<td cellid="'+ k +'" style="display: none; width:' + gridColumns[k].width + '"><p style="width:' + gridColumns[k].width + ';">'+gridColumns[k].cellsrenderer(gridColumns[k].filedName,rowData[gridColumns[k].filedName],gridColumns[k],rowData)+'</p></td>');
					html.push('<td cellid="'+ k +'" style="display: none; width:' + gridColumns[k].width + '">'+gridColumns[k].cellsrenderer(gridColumns[k].filedName,rowData[gridColumns[k].filedName],gridColumns[k],rowData)+'</td>');
				}
				
			}else{
				if(jQuery.inArray(k,hideTdIndex) == -1){
//					html.push('<td cellid="'+ k +'" style="width:' + gridColumns[k].width + '"><p title="'+rowData[gridColumns[k].filedName]+'" style="width:' + gridColumns[k].width + ';">'+rowData[gridColumns[k].filedName]+'</p></td>');
					html.push('<td cellid="'+ k +'" style="width:' + gridColumns[k].width + '">'+rowData[gridColumns[k].filedName]+'</td>');
				}else{
//					html.push('<td cellid="'+ k +'" style="display: none; width:' + gridColumns[k].width + '"><p title="'+rowData[gridColumns[k].filedName]+'" style="width:' + gridColumns[k].width + ';">'+rowData[gridColumns[k].filedName]+'</p></td>');
					html.push('<td cellid="'+ k +'" style="display: none; width:' + gridColumns[k].width + '">'+rowData[gridColumns[k].filedName]+'</td>');
				}
				
			}
		}
		html.push('</tr>');
    }
    html.push('</tbody>');
    $("#data_table_" + viewFilter.scheduleIds).append(html.join(""));
    if(viewFilter.pagenum < viewPageCount && (viewFilter.pagenum != (viewPageCount-1))){
		viewFilter.pagenum += 1;
		queryOtherPageView(result);
	}else{
		$("table[id^='data_table_"+ result.scheduleId +"']").find("tbody.hidden-tbody").removeClass("hidden-tbody");
	}
}
	
//导出计划详情列表
function exportPlanDetail(){
	var url = "/scheduleManager/exportScheduleDetail";
	parent.exportPlanList(url);
}






//获取拍摄状态的颜色
function getColor(shootStatus){
	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(shootStatus);
	return divColor;
}

//查看计划
function viewPlan(){
	parent.viewPlan();
}
//编辑计划
function editorialPlan(){
	parent.editedPlan();
}
//计划详情
function queryPlanDetail(){
	parent.planDetail();
}




//画图
function draw(id){
	var canvas = document.getElementById(id);
	if(canvas == null || canvas == undefined){
		return;
	}
	var context = canvas.getContext("2d");
//	context.beginPath();
	context.strokeStyle= '#fff';
	context.lineWidth = 3;
	context.moveTo(15,37); //从A（29,39）开始
	context.lineTo(29,20);//从A(29,39)开始，画到B (41,29)结束
	context.moveTo(29,20); //A(29,39)-C(17,29)
	context.lineTo(43,37);
	context.stroke(); //闭合形状并且以填充方式绘制出来
}

//回到顶部
function gotoTop(){
	document.getElementById("gridBody").scrollTop = 0;
}