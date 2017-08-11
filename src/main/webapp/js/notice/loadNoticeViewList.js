//要查询的gridObj
var gridObj;
//要查询的grid的source
var gridSource;

var filter={};

/**
 * 加载通告单主表
 * @param tableId
 * @param url
 * @param rendertoolbar
 */
function loadNoticeList(tableId) {
	this.tableId = tableId;
	this.soure = {};
	
	this.getRecords = function() {
		$.ajax({
			url: "/notice/loadNoticeView",
			type: "post",
			async: true,
			dataType: "json",
			success: function(params) {
				var noticeList = params.result.resultList;
				this.soure.data = noticeList;
			},
			failure: function() {
				
			}
		});
	};
	
	//加载表格
	this.loadNoticeTable = function() {
		
	};
	
	//刷新整个表格
	this.refreshGrid = function() {
		
	};
	
	//刷新单行
	this.refreshSingleRow = function() {
		
	};
	
	//刷新单列
	this.refreshSingleCell = function() {
		
	};
	
	//生成工具类
	this.rendertoolbar = function() {
		
	};
	
	//生成单行
	this.createRow = function() {
		
	};
}


/**
 * 生成通告单子表
 * @param gridDiv
 * @param data
 * @param index
 * @param noticeId
 */
function loadNoticeViewTable(gridDiv,data,noticeId,crewType,totalPage){
	//showSuccessMessage("正在加载...");
	
	this.isRowClick=true;
	
	//gridDiv.addClass("box_wrap");
	gridDiv.append("<div id='toolbar' class='title' style='display:block; margin-bottom:10px;'></div>");
	
	loadSearchViewGrid(gridDiv.find("#toolbar"), noticeId, data,totalPage);
	
	var gridMainDiv = $("<div class='t_i'></div>");
	
	
	var gridHead_array = ["<div class='t_i_h' id='hh'><div class='ee'><table class='notice-scence-table' cellspacing=0 cellpadding=0><thead>"];
	gridHead_array.push("<tr>");
	
	gridHead_array.push("<td><input style='width:30px;margin-left:auto;margin-right:auto;' type='checkbox' id='all' class='line-height' value=''/></td>");
	gridHead_array.push("<td><p style='width:80px;'>备戏状态</p></td>");
	gridHead_array.push("<td><p style='width:80px;'>拍摄状态</p></td>");
	if (crewType == 0 || crewType == 3) {
		gridHead_array.push("<td><p style='width:50px;'>场次</p></td>");
	} else {
		gridHead_array.push("<td><p style='width:50px;'>集-场</p></td>");
	}
	gridHead_array.push("<td><p style='width:80px;'>特殊提醒</p></td>");
	gridHead_array.push("<td><p style='width:65px;'>气氛/内外</p></td>");
	/*gridHead_array.push("<td><p style='width:50px;'>内外</p></td>");*/
	gridHead_array.push("<td><p style='width:50px;'>页数</p></td>");
	gridHead_array.push("<td><p style='width:65px;'>拍摄页数</p></td>");
	
	gridHead_array.push("<td><p style='width:100px;'>拍摄地点</p></td>");
	gridHead_array.push("<td><p style='width:150px;'>场景</p></td>");
	gridHead_array.push("<td><p style='width:150px;'>主要内容</p></td>");
	
	gridHead_array.push("<td><p style='width:150px;'>主要演员</p></td>");
	gridHead_array.push("<td><p style='width:130px;'>特约/群演</p></td>");
	/*gridHead_array.push("<td><p style='width:120px;'>群众演员</p></td>");*/
	
	gridHead_array.push("<td><p style='width:150px;'>服化道</p></td>");
	gridHead_array.push("<td><p style='width:150px;'>特殊道具</p></td>");
	/*gridHead_array.push("<td><p style='width:150px;'>服装</p></td>");
	
	gridHead_array.push("<td><p style='width:150px;'>化妆</p></td>");*/
	gridHead_array.push("<td><p style='width:150px;'>商植</p></td>");
	gridHead_array.push("<td style='border-right:none !important;'><p style='width:150px;'>备注</p></td>");
	
	//gridHead_array.push("<td><p style='width:100px;'></p></td>");
	gridHead_array.push("<td><p style='width:7px;'></p></td>");
	
	gridHead_array.push("</tr>");
	gridHead_array.push("</thead></table></div></div>");
	
	var gridHeadDiv = $(gridHead_array.join(""));
	
	var gridContentDiv = $("<div class='auto-height cc' id='ca' onscroll='viewGridScroll()'></div>");
	
	var table=$("<table cellpadding='0' cellspacing='0' border='0' noticeId='"+noticeId+"' id='noticeViewGrid'></table>");
	
	table.append("<tbody id='noticeViewTbody'><tbody>");
	viewNoArr = [];
	
	$.each(data,function(noticeViewIndex,item){
		if (item.viewId != null && item.viewId != undefined && item.viewId != '') {
			var _row = createRow(noticeViewIndex,item);
			table.find("#noticeViewTbody").append(_row);
		}
	});
	gridContentDiv.append(table);
	gridMainDiv.append(gridHeadDiv);
	gridMainDiv.append(gridContentDiv);
	gridDiv.append(gridMainDiv);
	
	//全选复选框点击事件
	$("#all").on("click",function(event){
		if($("#all").prop("checked")){
			$(this).trigger("checked");
		} else {
			$(this).trigger("unChecked");
		}
	});
	
	//checkbox指定全部不选
	$("#all").bind("unChecked",function(){
		$("#all").prop("checked",false);
		gridDiv.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all").bind("checked",function(){
		$("#all").prop("checked",true);
		gridDiv.find("tbody :checkbox").trigger("checked");
	});
	
	var fixHelper = function(e, ui) {  
            //console.log(ui)   
            ui.children().each(function() {  
                $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
            });  
            return ui;
        };
        
    //只读权限，去掉排序功能
    if(!isNoticeReadonly) {
    	$("#noticeViewTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
    	$("#noticeViewTbody").on( "sortstop", function( event, ui ) {
        	updateViewSort();
    	});
    } else {//只读权限，去掉设置拍摄地、销场、移除场景按钮
		$("#setAddress").remove();
		$("#setShootStatusButton").remove();
		$("#removeViewButton").remove();
    }
}

/**
 * 重新加载表格
 * @param data
 * @param index
 * @param noticeId
 */
function reloadNoticeView(gridDiv,data,noticeId,totalPage){
	//showSuccessMessage("正在加载...");
	
	gridDiv.find("#toolbar").html('');
	loadSearchViewGrid(gridDiv.find("#toolbar"), noticeId, data,totalPage);
	
	var table=$('#noticeViewGrid');
	
	var _this = this;
	table.html('');
	table.append("<tbody id='noticeViewTbody'><tbody>");
	
	//全选复选框反选
	$("#all").prop("checked",false);
	viewNoArr = [];
	$.each(data,function(noticeViewIndex,item){
		if (item.viewId != null && item.viewId != undefined && item.viewId != '') {
			var _row = createRow(noticeViewIndex,item);
			table.find("#noticeViewTbody").append(_row);
		}
	});
	
	//全选复选框点击事件
	$("#all").on("click",function(event){
		if($("#all").prop("checked")){
			$(this).trigger("checked");
		} else {
			$(this).trigger("unChecked");
		}
	});
	
	//checkbox指定全部不选
	$("#all").bind("unChecked",function(){
		$("#all").prop("checked",false);
		table.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all").bind("checked",function(){
		$("#all").prop("checked",true);
		table.find("tbody :checkbox").trigger("checked");
	});
	
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#noticeViewTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#noticeViewTbody").on( "sortstop", function( event, ui ) {
		updateViewSort();
	});
}

//获取通告单下场景表中指定场景的序号
function getRowIndex(tableIndex, viewId) {
	if(!viewId){
		return null;
	}
	var _tBody = $("#noticeViewTbody" + tableIndex);
	
	return parseInt(_tBody.find("#"+viewId).attr("index"));
}

/**
 * 创建通告单中表格的一行
 * @param tableIndex	表格编号
 * @param rowIndex	行编号
 * @param rowData	行数据
 * @returns	行
 */
function createRow (tableIndex,rowData) {
	var _this = this;
	var table=$('#noticeViewGrid'+tableIndex);
	
	var statusClass=" style='background-color:"+getColor(rowData.shootStatus)+"' ";
	
	var _row = $("<tr id='notice"+tableIndex+"row' "+ statusClass +"></tr>");
	
	//行点击事件
	_row.click(function(){
		if(_this.isRowClick){
			if($(this).attr("class")&&$(this).attr("class").indexOf("mouse_click")>-1){
				$(this).find(":checkbox").trigger("unChecked");
			}else{
				$(this).find(":checkbox").trigger("checked");
			}
			
		}else{
			_this.isRowClick=true;
		}
	});
	
	if (crewType == 0 || crewType == 3) {
		_row.append("<td><input id='"+ rowData.viewId +"' style='width:30px;margin-left:auto;margin-right:auto;' type='checkbox' sval='"+ rowData.viewNo +"' shootStatus='"+ rowData.shootStatus +"' value='"+rowData.viewId+"' class='line-height'/></td>");//拍摄状态
		
	}else {
		_row.append("<td><input id='"+ rowData.viewId +"' style='width:30px;margin-left:auto;margin-right:auto;' type='checkbox' sval='"+ rowData.seriesNo+"-"+rowData.viewNo +"' shootStatus='"+ rowData.shootStatus +"' value='"+rowData.viewId+"' class='line-height'/></td>");//拍摄状态
	}
	
	var shootStatusText = "";
	if(rowData.shootStatus){
		shootStatusText=rowData.shootStatus;
	}
	
	if (rowData.prepareStatus == 1) {
		_row.append("<td><p style='width:80px;text-align:center;'>备</p></td>");//备戏状态
	}else {
		_row.append("<td><p style='width:80px;text-align:center;'></p></td>");//备戏状态
	}
	_row.append("<td><p style='width:80px;'>"+shootStatusText+"</p></td>");//拍摄状态
	
	//电影类型的剧组不显示集次
	if (crewType == 0 || crewType == 3) {
		_row.append("<td><p style='width:50px;cursor:pointer;color:#52b0cc;' onclick='showViewContent(this)' sval='"+ rowData.viewId +"'>"+ rowData.viewNo+"</p></td>");//集场
		viewNoArr.push(rowData.viewNo);
	} else {
		_row.append("<td><p style='width:50px;cursor:pointer;color:#52b0cc;' onclick='showViewContent(this)' sval='"+ rowData.viewId +"'>"+rowData.seriesNo+"-"+rowData.viewNo+"</p></td>");//集场
		viewNoArr.push(rowData.seriesNo+"-"+rowData.viewNo);
	}
	
	//添加集场数组数据
	var specialRemindText= rowData.specialRemind;
	if(specialRemindText==null){
		specialRemindText="";
	}
	_row.append("<td><p style='width:80px;' title='"+ specialRemindText +"'>"+specialRemindText+"</p></td>");//特别提醒
	
	//气氛.内外景
//	var atmosphere= rowData.atmosphereName;
	var siteText="";
	var atmosphere = "";
	if(rowData.atmosphereName){
		atmosphere=rowData.atmosphereName;
		if(rowData.site){
			siteText=rowData.site;
			_row.append("<td><p style='width:65px;'>"+atmosphere + "/"+ siteText +"</p></td>");
		}else {
			_row.append("<td><p style='width:65px;'>"+atmosphere+"</p></td>");
		}
	}else {
		if(rowData.site){
			siteText=rowData.site;
			_row.append("<td><p style='width:65px;'>"+siteText+"</p></td>");
		}else {
			_row.append("<td><p style='width:65px;'></p></td>");
		}
	}
	
	//原页数
	_row.append("<td><p style='width:50px;'>"+ rowData.pageCount +"</p></td>");//页数
	
	//拍摄页数
   	_row.append("<td><p style='width:65px;'><input type='text' value='"+rowData.shootPage+"' class='page-count-text' sval='"+ rowData.viewId +"' onblur='saveInputPage(this)'/></p></td>");//页数
   	
   	//拍摄地点
   	var shootLocationText="";
	if(rowData.shootLocation){
		shootLocationText=rowData.shootLocation;
	}
//	if(rowData.shootRegion){
//		shootLocationText += "(" + rowData.shootRegion + ")";
//	}
	_row.append("<td><p style='width:100px;' title='"+ shootLocationText +"'>"+shootLocationText+"</p></td>");
    
	var dataArr = [];
    //场景
    var majorViewText=""; 
    var minorViewText=""; //次场景
    var thirdLevelViewText=""; //三级场景
    //主场景
    if(rowData.majorView){
    	dataArr.push(rowData.majorView);
    }
    //次场景
    if(rowData.minorView){
    	dataArr.push(rowData.minorView);
    }
    //三级场景
    if(rowData.thirdLevelView){
    	dataArr.push(rowData.thirdLevelView);
    }
    
    var locationStr = '';
    for(var i=0; i<dataArr.length; i++){
    	if (i == 0) {
    		locationStr = dataArr[i];
    		continue;
		}
    	
    	locationStr = locationStr +" | " + dataArr[i];
    }
    
    _row.append("<td><p style='width:150px;' title='"+ locationStr +"'>" + locationStr +"</p></td>");
    
    
    //文武戏
	/*var typeText = typeMap.get(rowData.viewType);
	if(typeText==null){
		typeText="";
	}
	_row.append("<td><p style='width:50px;'>"+typeText+"</p></td>");*/
	
	
	/*var majorViewText="";
   	if(rowData.majorView){
   		majorViewText=rowData.majorView;
   	}
   	_row.append("<td><p style='width:120px;' title='"+ majorViewText +"'>"+majorViewText+"</p></td>");//主场景
	var minorViewText="";
   	if(rowData.minorView){
   		minorViewText=rowData.minorView;
   	}
   	_row.append("<td><p style='width:120px;' title='"+ minorViewText +"'>"+minorViewText+"</p></td>");//次场景
	var thirdLevelViewText="";
   	if(rowData.thirdLevelView){
   		thirdLevelViewText=rowData.thirdLevelView;
   	}
   	_row.append("<td><p style='width:120px;' title='"+ thirdLevelViewText +"'>"+thirdLevelViewText+"</p></td>");*/ //三级场景
	var mainContentText="";
   	if(rowData.mainContent){
   		mainContentText=rowData.mainContent;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ mainContentText +"'>"+mainContentText+"</p></td>");//主要内容
	/*var pageCountText="";
   	if(rowData.pageCount){
   		pageCountText=rowData.pageCount;
   	}
   	_row.append("<td><p style='width:50px;'>"+pageCountText+"</p></td>");*/ //页数
	var roleListText="";
   	if(rowData.roleList){
   		roleListText=rowData.roleList;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ roleListText +"'>"+roleListText+"</p></td>");//主要演员
   	
   	//特约演员、群众演员
	var guestRoleListText="";
	var massRoleListText="";
   	if(rowData.guestRoleList){
   		guestRoleListText=rowData.guestRoleList;
   		if(rowData.massRoleList){
   	   		massRoleListText=rowData.massRoleList;
   	   		_row.append("<td><p style='width:130px;' title='"+ guestRoleListText+ " | " + massRoleListText +"'>"+guestRoleListText+ " | " + massRoleListText +"</p></td>");
   	   	}else {
   	   		_row.append("<td><p style='width:130px;' title='"+ guestRoleListText +"'>"+guestRoleListText+"</p></td>");//特约演员
		}
   	}else {
   		if(rowData.massRoleList){
   	   		massRoleListText=rowData.massRoleList;
   	   		_row.append("<td><p style='width:130px;' title='"+ massRoleListText +"'>"+massRoleListText+"</p></td>");//特约演员
   		}else {
   			_row.append("<td><p style='width:130px;' title=''></p></td>");//特约演员
		}
	}
   //	_row.append("<td><p style='width:130px;' title='"+ guestRoleListText +"'>"+guestRoleListText+"</p></td>");//特约演员
   	
//   	if(rowData.massRoleList){
//   		massRoleListText=rowData.massRoleList;
//   	}
//   	_row.append("<td><p style='width:120px;' title='"+ massRoleListText +"'>"+massRoleListText+"</p></td>");//群众演员
   	
   	//服化道
	var propsListText="";
	var clothesNameText="";
	var makeupNameText="";
	if(rowData.makeupName){ //化妆
		makeupNameText=rowData.makeupName;
		if(rowData.clothesName){ //服装
			clothesNameText=rowData.clothesName;
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText + " | " + propsListText +"'>" + makeupNameText + " | " + clothesNameText + " | " + propsListText+"</p></td>");
			}else {
				_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText +"'>" + makeupNameText + " | " + clothesNameText + "</p></td>");
			}
		}else {
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + propsListText +"'>" + makeupNameText + " | " + propsListText + "</p></td>");
			}else {
				_row.append("<td><p style='width:150px;' title='"+ makeupNameText +"'>" + makeupNameText + "</p></td>");
			}
		}
		
	}else {
		if(rowData.clothesName){ //服装
			clothesNameText=rowData.clothesName;
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				_row.append("<td><p style='width:150px;' title='"+ clothesNameText + " | " + propsListText +"'>" + clothesNameText + " | " + propsListText + "</p></td>");
			}else {
				_row.append("<td><p style='width:150px;' title='"+ clothesNameText +"'>" + clothesNameText + "</p></td>");
			}
		}else {
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				_row.append("<td><p style='width:150px;' title='"+ propsListText +"'>" + propsListText + "</p></td>");
			}else {
				_row.append("<td><p style='width:150px;' title=''></p></td>");
			}
		}
	}
   /*	_row.append("<td><p style='width:150px;' title='"+ clothesNameText +"'>"+clothesNameText+"</p></td>");//服装
   	_row.append("<td><p style='width:150px;' title='"+ makeupNameText +"'>"+makeupNameText+"</p></td>");//化妆
*/   	
   	//特殊道具
	var specialPropsListText="";
   	if(rowData.specialPropsList){
   		specialPropsListText=rowData.specialPropsList;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ specialPropsListText +"'>"+specialPropsListText+"</p></td>");//特殊道具
	/*var clothesNameText="";
   	if(rowData.clothesName){
   		clothesNameText=rowData.clothesName;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ clothesNameText +"'>"+clothesNameText+"</p></td>");//服装
	var makeupNameText="";
   	if(rowData.makeupName){
   		makeupNameText=rowData.makeupName;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ makeupNameText +"'>"+makeupNameText+"</p></td>");//化妆
*/	var advertNameText="";
   	if(rowData.advertName){
   		advertNameText=rowData.advertName;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ advertNameText +"'>"+advertNameText+"</p></td>");//商植
	/*var shootDateText="";
   	if(rowData.shootDate){
   		shootDateText=rowData.shootDate;
   	}
   	_row.append("<td><p style='width:100px;'>"+shootDateText+"</p></td>");//拍摄时间
	var tapNoText="";
   	if(rowData.tapNo){
   		tapNoText=rowData.tapNo;
   	}
   	_row.append("<td><p style='width:80px;'>"+tapNoText+"</p></td>");*/ //带号
	var remarkText="";
   	if(rowData.remark){
   		remarkText=rowData.remark;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ remarkText +"'>"+remarkText+"</p></td>");//备注
   	/*_row.append("<td><p style='width:6px;'></p></td>");*/
   	
	//_row.append("<td><span class='delete-row' title='移除场景' onclick='removeView(\"" + rowData.viewId + "\",\"" + rowData.shootStatus + "\")'></span></td>");//备注
   	
   	
   	//行checkbox事件只判断是否全选(行checkbox值发生变化时调用该方法)
	function isCheckAll() {
		var UnChecked = $("#noticeViewTbody").find(":checkbox").not(function(index){
			if($(this).prop("checked")){
				return this;
			}
		}).length;
		if(UnChecked==0){
			$("#all").prop("checked",true);
		}else{
			$("#all").prop("checked",false);
		}
	}
	
	_row.find(":checkbox").bind("checked",function(){
		$(this).prop("checked",true);
		$(this).parents("tr").addClass("mouse_click");
		
		isCheckAll();
	});
	
	_row.find(":checkbox").bind("unChecked",function(){
		$(this).prop("checked",false);
		$(this).parents("tr").removeClass("mouse_click");

		isCheckAll();
	});
	
	return _row;
}

/**
 * 更新表格排序顺序
 */
function updateViewSort(){
	var viewIds = "";
	$("#noticeViewTbody").find(":checkbox").each(function(){
    		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	var noticeId=$("#noticeViewGrid").attr("noticeId");
	$.ajax({
		url:"/notice/sortNoticeView",
		type:"post",
		dataType:"json",
		data:{noticeId:noticeId,viewIds:viewIds},
		success:function(data){
			if (data.success) {
				/*showSuccessMessage(data.message);*/
			}else {
				showErrorMessage(data.message);
			}
		}
	});
	
}

/**
 * 生成表格的工具栏
 * @param toolbar 工具栏jquery对象
 * @param index	当前表格的下标
 * @param noticeId notice数据
 */
function loadSearchViewGrid(toolbar,noticeId,data,totalPage){
	
	//取出隐藏域的值
	var totalViewCount = $("#totalView").val();
	var totalPageCount =  parseFloat($("#totalPage").val()).toFixed(1);
	var container = toolbar;
    //生成头部查询条件
    var html= "<div style='height: 100%;'>"
    	+ " <div class='noticeViewToolbar' id='addNoticeView'>"
    	+ "<div class='set-shoot-adress-div' title='设置拍摄地' id='setAddress' onclick='setShootLocationAddress()'></div>"
    	+ "<div class='set-remark-view-div' title='设置备场' id='setRemarkView' onclick='setRemarkView()'></div>"
        + "<div class='remove-view-div' title='移除场景' id='removeViewButton' onclick='removeView()'></div>"
        + "<div class='set-shoot-status-div' title='销&nbsp;&nbsp;&nbsp;&nbsp;场' id='setShootStatusButton' onclick='cancleView()'></div>"
        + "<div class='view-count-div' id='viewCountDiv'>共" + totalViewCount + "场/" + totalPageCount + "页</div>";
    	+ "</div></div>";
    //加载图例
    html+=" <input type='button' style='float:right;width:375px;margin-top: -25px;margin-left:140px;'  value='' id='viewColorExample' class='colorExample4'>";
    toolbar.append(container);
    container.append(html);
}

//截取页数只显示小数点后两位数
function subStringPage(PageCount){
	var pageCountStr = PageCount+"";
	var subStr = pageCountStr.substring(0,pageCountStr.indexOf(".")+3);
	return subStr;
}

//高级查询弹出层
var popupLayer;

//异步加载高级查询条件
function loadSearchCondition() {
    //查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
    $.ajax({
        url:"/viewManager/loadAdvanceSerachData",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
            if(data.success) {
                var viewFilterDto = data.viewFilterDto;
                var atmosphereList = viewFilterDto.atmosphereList;
                var seasonList = viewFilterDto.seasonList;
                var shootStatusList = viewFilterDto.shootStatusList;
                var siteList = viewFilterDto.siteList;
                var viewLocationList = viewFilterDto.viewLocationList;
                var firstLocationList = viewFilterDto.firstLocationList;
                var secondLocationList = viewFilterDto.secondLocationList;
                var thirdLocationList = viewFilterDto.thirdLocationList;
                var majorRoleList = viewFilterDto.majorRoleList;
                var guestRoleList = viewFilterDto.guestRoleList;
                var massesRoleList = viewFilterDto.massesRoleList;
                var commonPropList = viewFilterDto.commonPropList;
                var specialPropList  = viewFilterDto.specialPropList;
                var cultureTypeList = viewFilterDto.cultureTypeList;
                var clotheList = viewFilterDto.clotheList;
                var makeupList = viewFilterDto.makeupList;
                var shootLocationList = viewFilterDto.shootLocationList;
                var advertInfoList = viewFilterDto.advertInfoList;
                
                for (var season in seasonList) {
                    $("#seasonSelect").append("<option value="+ season + ">" + seasonList[season] + "</option>");
                }
                $("#seasonSelect").selectpicker('refresh');
                
                for (var atm in atmosphereList) {
                    $("#atmosphereSelect").append("<option value="+ atm + ">" + atmosphereList[atm] + "</option>");
                }
                $("#atmosphereSelect").selectpicker('refresh');
                
                for (var site in siteList) {
                    $("#siteSelect").append("<option value="+ siteList[site] + ">" + siteList[site] + "</option>");
                }
                $("#siteSelect").selectpicker('refresh');
                
                for (var shotstatus in shootStatusList) {
                    $("#shootStatusSelect").append("<option value="+ shotstatus + ">" + shootStatusList[shotstatus] + "</option>");
                }
                $("#shootStatusSelect").selectpicker('refresh');
                
                for (var cultureType in cultureTypeList) {
                    $("#cultureTypeSelect").append("<option value="+ cultureType + ">" + cultureTypeList[cultureType] + "</option>");
                }
                $("#cultureTypeSelect").selectpicker('refresh');
                
                for (var advert in advertInfoList) {
                    $("#advertInfoSelect").append("<option value="+ advert + ">" + advertInfoList[advert] + "</option>");
                }
                $("#advertInfoSelect").selectpicker('refresh');
                
                
                
                for (var shotLocation in shootLocationList) {
                    $("#shootLocationSelect").append("<option value="+ shootLocationList[shotLocation] + ">" + shootLocationList[shotLocation] + "</option>");
                }
                $("#shootLocationSelect").selectpicker('refresh');
                
                for (var fLocation in firstLocationList) {
                    $("#firstLocationSelect").append("<option value="+ fLocation + ">" + firstLocationList[fLocation] + "</option>");
                }
                $("#firstLocationSelect").selectpicker('refresh');
                
                for (var sLocation in secondLocationList) {
                    $("#secondLocationSelect").append("<option value="+ sLocation + ">" + secondLocationList[sLocation] + "</option>");
                }
                $("#secondLocationSelect").selectpicker('refresh');
                
                
                
                for (var mrole in majorRoleList) {
                    $("#majorRoleSelect").append("<option value="+ mrole + ">" + majorRoleList[mrole] + "</option>");
                }
                $("#majorRoleSelect").selectpicker('refresh');
                
                for (var grole in guestRoleList) {
                    $("#guestRoleSelect").append("<option value="+ grole + ">" + guestRoleList[grole] + "</option>");
                }
                $("#guestRoleSelect").selectpicker('refresh');
                
                for (var mrole in massesRoleList) {
                    $("#massRoleSelect").append("<option value="+ mrole + ">" + massesRoleList[mrole] + "</option>");
                }
                $("#massRoleSelect").selectpicker('refresh');
                
                
                
                for (var cloth in clotheList) {
                    $("#clothSelect").append("<option value="+ cloth + ">" + clotheList[cloth] + "</option>");
                }
                $("#clothSelect").selectpicker('refresh');
                
                for (var makeup in makeupList) {
                    $("#makeupSelect").append("<option value="+ makeup + ">" + makeupList[makeup] + "</option>");
                }
                $("#makeupSelect").selectpicker('refresh');
                
                for (var cprop in commonPropList) {
                    $("#propSelect").append("<option value="+ cprop + ">" + commonPropList[cprop] + "</option>");
                }
                $("#propSelect").selectpicker('refresh');
                
                for (var sprop in specialPropList) {
                    $("#specialPropSelect").append("<option value="+ sprop + ">" + specialPropList[sprop] + "</option>");
                }
                $("#specialPropSelect").selectpicker('refresh');
            }
        }
    });
}

//加载高级检索窗口
function loadSearchDIV(){
	
	//popupLayer = new PopupLayer({trigger:"#superSearchButton",popupBlk:"#dropDownDIV"});
	
	popupLayer = $('#searchWindow').jqxWindow({
	    theme:theme,  
	    width: 840,
        height: 710, 
        maxHeight: 800,
        autoOpen: false,
        cancelButton: $('#closeSearchSubmit'),
        isModal: true,
        resizable: false,
        initContent: function () {
        
        	//加载查询条件
            //loadSearchCondition();
        	
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
		    
		
			$("#searchSubmit").on("click",function(){
		    	var season =$("#seasonSelect").val();
		    	var atmosphere =$("#atmosphereSelect").val();
		    	var site =$("#siteSelect").val();
		    	var major =$("#firstLocationSelect").val();
		    	var minor =$("#secondLocationSelect").val();
		    	//var thirdLevel =$("#thirdLevelViewListDIV").jqxDropDownList("getSelectedItem");
		    	var clothes =$("#clothSelect").val();
		    	var makeup =$("#makeupSelect").val();
		    	var roles = $("#majorRoleSelect").val(); 
		    	var props = $("#propSelect").val(); 
		    	var specialProps = $("#specialPropSelect").val();
		    	var guestRole = $("#guestRoleSelect").val(); 
		    	var massRole = $("#massRoleSelect").val(); 
		    	var shootStatus = $("#shootStatusSelect").val();
		    	var advert = $("#advertInfoSelect").val();
		    	var viewType=$("#cultureTypeSelect").val();
		    	var shootLocation =$("#shootLocationSelect").val();
		    	var mainContent = $("#mainContent").val();
		    	var remark = $("#viewRemark").val();
		    	var seriesViewNos = $("#seriesViewNos").val();
                var viewNos = $("#viewNos").val();
		    	
		    	if(season!= null && season!=""){
		    		var seasonStr = "";
		    		
		    		for(var i=0;i<season.length;i++){
		    			seasonStr+=season[i]+",";
		    		}
		    		seasonStr=seasonStr.substring(0,seasonStr.length-1);
		    		filter.season=seasonStr;
		    	}else{
		    		filter.season="";
		    	}
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
		    	if(viewType!= null && viewType!=""){
		    		var viewTypeStr = "";
		    		
		    		for(var i=0;i<viewType.length;i++){
		    			viewTypeStr+=viewType[i]+",";
		    		}
		    		viewTypeStr=viewTypeStr.substring(0,viewTypeStr.length-1);
		    		filter.viewType=viewTypeStr;
		    	}else{
		    		filter.viewType="";
		    	}
		    	
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
		    	if(specialProps!= null && specialProps!=""){
		    		var prop = "";
		    		
		    		for(var i=0;i<specialProps.length;i++){
		    			prop+=specialProps[i]+",";
		    		}
		    		prop=prop.substring(0,prop.length-1);
		    		filter.specialProps=prop;
		    	}else{
		    		filter.specialProps="";
		    	}
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
		    	
		    	if($("#startSeriesNo").val()!=""){
		    		filter.startSeriesNo=$("#startSeriesNo").val();
		    	}else{
                    filter.startSeriesNo="";
                }
		    	if($("#startViewNo").val()!=""){
		    		filter.startViewNo=$("#startViewNo").val();
		    	}else{
		    		filter.startViewNo="";
		    	}
		    	if($("#endSeriesNo").val()!=""){
		    		filter.endSeriesNo=$("#endSeriesNo").val();
		    	}else{
		    		filter.endSeriesNo="";
		    	}
		    	if($("#endViewNo").val()!=""){
		    		filter.endViewNo=$("#endViewNo").val();
		    	}else{
		    		filter.endViewNo="";
		    	}
		    	
				if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
					showErrorMessage("请填写集数");
		    		return;
		    	}
				if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
					showErrorMessage("请填写集数");
		    		return;
		    	}
		    	
		    	filter.seriesNo="";
		    	filter.viewNo="";
		    	
		    	/*if(/.*[\u4e00-\u9fa5]+.*$/.test(seriesViewNos)){
		    		showErrorMessage("集场编号中不能含有汉字");
		    		return;
	    		}*/
		    	
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
		    	
		    	//$("#seriesNo").val("");
		    	//$("#viewNo").val("");
		    	
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
		    	
		    	/*var sortType = $("input[name='sortType']:checked").val();
		    	var sortFlag = $("input[name='sortFlag']:checked").val();
		    	
		    	filter.sortType=sortType;
				filter.sortFlag=sortFlag;*/
				filter.searchMode=$("input[name='searchMode']:checked").val();
				filter.pageFlag=true;
				filter.fromAdvance = true;
				if($("#isAll").prop("checked")){
					filter.isAll="1";
				}else{
					filter.isAll="0";
				}
				filter.noticeId=$("#noticeViewGrid"+gridObj.attr("id").replace("viewList","")).attr("noticeId");;
				gridSource.data=filter;
				$('#searchWindow').jqxWindow('close');
				gridObj.jqxGrid('gotopage', 0);
				gridObj.jqxGrid('updatebounddata');
				

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
		    });
		    
		    
		    $('.selectpicker').selectpicker({
                size: 10
            });
		    
		    //jqxFormattedInput({ width: 250, height: 25, radix: "decimal", decimalNotation: "exponential", value: "330000" });
		    $("#startSeriesNo").jqxFormattedInput({theme:theme, placeHolder: "集", radix: "decimal", min: 1, value: '' });
            $("#startViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
            $("#endSeriesNo").jqxFormattedInput({theme:theme,placeHolder: "集", radix: "decimal", min: 1, value: '' });
            $("#endViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
            $("#startSeriesNo, #endSeriesNo").on("keyup", function() {
                if (isNaN($(this).val())) {
			        $(this).val("");
			    }
		    });
            
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
            
            
            //当选择主要演员显现"出现即可"和"不出现"单选按钮，只有当选择两个及两个以上才显现"同时出现"和"不同时出现"单选按钮
            $("#majorRoleSelect").on('change', function (event){
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
            });
            
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
            
            
		    $("#clearSearchButton").on("click",function(){
		        //集场重置
		    	if (crewType != 0) {
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
                /*$("input[name='sortType'][value='1']").prop("checked",true);
                $("input[name='sortFlag'][value='1']").prop("checked",true);*/
                
                //所有下拉值全部反选
		        $('.selectpicker').selectpicker('deselectAll');
		        
		        $("#isAll").prop("checked", false);
		        
		        //$("#selectpickerPreValue").val("");
		        $(".preValue").val("");
		        
		        $("#mainContent").val("");
		        $("#viewRemark").val("");
		    });
    	}
    });
}

//获取表格的实际行数，如果取不到，则返回-1
function getIndex(row,index){
	var pageIndex=0;
	if(index != null){
		var datainformation = $("#viewList"+index).jqxGrid('getdatainformation');
		var paginginformation = datainformation.paginginformation;
		var pagenum = paginginformation.pagenum;
		var pagesize = paginginformation.pagesize;
		var pagescount = paginginformation.pagescount;
		pageIndex=row-pagenum*pagesize;
	}else{
		var datainformation = $("#jqxgrid").jqxGrid('getdatainformation');
		var paginginformation = datainformation.paginginformation;
		var pagenum = paginginformation.pagenum;
		var pagesize = paginginformation.pagesize;
		var pagescount = paginginformation.pagescount;
		pageIndex=row-pagenum*pagesize;
	}
	
	return pageIndex;
}


function getColor(shootStatus){
	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(noticeShootStatusMap.getKey(shootStatus));
	return divColor;
}

//设置UI框架中表格点击行中任意列选中和取消选中的方法
function jqxGridRowClick(grid, event) {
    var args = event.args;
    var boundIndex = args.rowindex;
    
    var rowindexs = grid.jqxGrid('getselectedrowindexes');
    var containsFlag = false;
    
    for (var i = 0; i < rowindexs.length; i ++) {
        if (rowindexs[i] == boundIndex) {
           containsFlag = true;
        }
    }
    
    if(containsFlag) {
        grid.jqxGrid('unselectrow', boundIndex);
    } else {
        grid.jqxGrid('selectrow', boundIndex);
    }
}


function loadDiv() {
	var sub = "<div style='z-index: 99999; margin-left: -66px; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;'>"
	        +"<div style='float: left; overflow: hidden; width: 32px; height: 32px;' class='jqx-grid-load'/>" 
	        + "<span style='margin-top: 10px; float: left; display: block; margin-left: 5px;' >" +"上传中..." + "</span>" 
	        + "</div>";
	var div = "<div id='_layer_'> " 
	        + "<div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 9999; position: absolute;"
			+ "left: 0; top: 0; overflow: hidden; display: none'>" 
			+ "</div>" 
			+ "<div id='_wait_' style='z-index: 1005; position: absolute; width:430px;height:218px; display: none'  >" 
			+ "<center>" 
			+ "</center>" 
			+ "</div>" 
			+ "</div>";
	return div;
}
function LayerShow() {
	var addDiv = loadDiv();
	
	var element = $(addDiv).appendTo(document.body);
	$(window).resize(Position);
	var deHeight = $(document).height();
	var deWidth = $(document).width();
	Position();
	$("#_MaskLayer_").show();
	$("#_wait_").show();
}
function Position() {
	$("#_MaskLayer_").width($(document).width());
	var deHeight = $(window).height();
	var deWidth = $(window).width();
	$("#_wait_").css({
		left : (deWidth - $("#_wait_").width()) / 2 + "px",
		top : (deHeight - $("#_wait_").height()) / 2 + "px"
	});
}
function LayerHide() {
	$("#_MaskLayer_").hide();
	$("#_wait_").hide();
	del();
}
function del() {
	var delDiv = document.getElementById("_layer_");
	delDiv.parentNode.removeChild(delDiv);
};

//重置文本框的高度

function viewGridScroll() {
	var b = document.getElementById("ca").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}

function tableScroll() {
	var b = document.getElementById("theadDiv").scrollLeft;
	document.getElementById("tbodyDiv").scrollLeft = b;
}

//加载反馈列表表格
/*function loadFedbackTable(tableId, noticeId) {
	this.tableId = tableId;
	this.records = null;
	this.totalCount = null, hasFinishLookCount = null;
	//获取记录数据
	this.getRecords=function(){
		var records = null;
		var totalCount = null;
		var hasFinishLookCount = null;
		$.ajax({
			url: "/notice/queryNoticeFedBackInfo",
			type: "post",
			data: {noticeId: noticeId},
			dataType: "json",
			async: false,
			success:function(data) {
				records=data.fedBackList;
				totalCount = data.totalCount;
				hasFinishLookCount = data.hasFinishLookCount;
			}
		});
		this.records = records;
		this.totalCount = totalCount;
		this.hasFinishLookCount = hasFinishLookCount;
		
		return this.records;
	};
	
	//加载表格
	this.loadTable=function(){
		this.getRecords();
		this.createTable();
	};
	
	//创建表格html
	this.createTable = function (){
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tableObj.append('<div class="theadDiv" id="theadDiv"><table cellpadding="0" cellspacing="0" border="0" class="user_table">'+
				'<thead><tr id="tableHead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#tableHead");
		
		_head.append('<td><p class="userName">姓名</p></td>');
		_head.append('<td><p class="roleName">职务</p></td>');
		_head.append('<td><p class="fedbackStatus">反馈状态</p></td>');
		_head.append('<td><p class="remark">意见</p></td>');
		_head.append('<td><p class="statusUpdateTime">更新时间</p></td>');
		
		_tableObj.append('<div class="tbodyDiv" id="tbodyDiv"><table cellpadding="0" cellspacing="0" border="0"><tbody id="tableBody"></tbody></table></div>');
		//表格主体
		var _tBody=_tableObj.find("#tableBody");
		
		//所有数据
		var tableData = this.records;
		if (tableData != null) {
			for (var i=0;i<tableData.length;i++) {
				var rowData=tableData[i];
				var _row = this.createRow(_tBody, rowData, i);
				_tBody.append(_row);
			}
		}
	};
	
	//选中一行数据
	this.checkedItem = function(userId) {
		var tableId = this.tableId;
		$("#" + tableId).find(":checkbox[id="+ userId +"]").trigger("checked");
	};
	
	this.unCheckAll = function() {
		$("#" + tableId).find(":checkbox").trigger("unChecked");
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		var statusColor = fedBackStatusColorMap.get(rowData.backStatus);
		
		var _row = $("<tr rowId='"+rowid+"' style='color:"+ statusColor +";'></tr>");
		
		var roleNames= rowData.roleNames == null ? "" : rowData.roleNames;
		var fedbackStatus = fedBackStatusMap.get(rowData.backStatus);
		var isSatisfied = febBackIsSatisfied.get(rowData.isSatisfied);
		var remark = rowData.remark == null ? "" : "("+rowData.remark+")";
		
		_row.append('<td title="'+rowData.realName+'"><p class="userName">'+rowData.realName+'</p></td>');
		_row.append('<td title="'+ roleNames +'"><p class="roleName">'+ roleNames +'</p></td>');
		_row.append('<td title="'+ fedbackStatus +'"><p class="fedbackStatus">'+ fedbackStatus +'</p></td>');
		_row.append('<td title="'+ isSatisfied + remark +'"><p class="remark">'+ isSatisfied + remark +'</p></td>');
		_row.append('<td title="'+ rowData.statusUpdateTime +'"><p class="statusUpdateTime">'+ rowData.statusUpdateTime +'</p></td>');
		return _row;
	};
	
	this.refreshGrid = function() {
		this.loadTable();
	};
	
	this.getTotalCount = function() {
		return this.totalCount;
	};
	
	this.getFinishLookCount = function() {
		return this.hasFinishLookCount;
	};
}*/

//加载待销场信息表格
/*function loadToCancelViewTable(tableId, noticeId) {
	this.tableId = tableId;
	this.tmpCancelRecords = null;
	this.noticeViewRecords = null;
	this.newAddViewInfoList = null;
	//获取临时销场记录数据
	this.getRecords=function(){
		var tmpCancelRecords = null;
		var noticeViewList = null;
		var newAddViewInfoList = null;
		$.ajax({
			url: "/notice/queryToCancelViewList",
			type: "post",
			dataType: "json",
			data: {noticeId: noticeId},
			async: false,
			success:function(data) {
				tmpCancelRecords=data.tmpCancelViewList;
				noticeViewList = data.noticeViewList;
				newAddViewInfoList = data.newAddViewInfoList;
			}
		});
		this.tmpCancelRecords = tmpCancelRecords;
		this.noticeViewRecords = noticeViewList;
		this.newAddViewInfoList = newAddViewInfoList;
		
		return tmpCancelRecords;
	};
	
	//加载表格
	this.loadTable=function(){
		this.getRecords();;
		
		this.createComparePanel();
		this.createTmpCancelGrid();
		this.createNoticeViewGrid();
	};
	
	//创建对比面板
	this.createComparePanel = function() {
		//表格对象
		var _comparePanelObj = $("#"+this.tableId);
		_comparePanelObj.children().remove();
		
		_comparePanelObj.append("<div class='compare-subpanel' id='tmpCancelPanel'></div><div class='line-middle'></div><div class='compare-subpanel' id='noticeViewPanel'></div>");
	};
	//创建场记单销场表格
	this.createTmpCancelGrid = function() {
		var _comparePanelObj = $("#"+this.tableId);
		
		var _tableObj = _comparePanelObj.find("#tmpCancelPanel");
		
		_tableObj.children().remove();
		
		_tableObj.append("<h4 class='cancelview-title'>场记单销场信息</h4>");
		_tableObj.append('<div class="theadDiv"><table cellpadding="0" cellspacing="0" border="0"><thead><tr id="tmpCancelThead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#tmpCancelThead");
		
		//电影剧本不显示集次信息
		if (crewType == Constants.CrewType.movie) {
			_head.append('<td><p class="seriesViewNo">场次</p></td>');
		} else {
			_head.append('<td><p class="seriesViewNo">集-场</p></td>');
		}
		
		_head.append('<td><p class="shootStatus">拍摄状态</p></td>');
		_head.append('<td><p class="finishDate">完成时间</p></td>');
		_head.append('<td><p class="tapNo">带号</p></td>');
		_head.append('<td><p class="remark">备注</p></td>');
		
		_tableObj.append('<div class="tbodyDiv"><table cellpadding="0" cellspacing="0" border="0"><tbody id="tmpCancelTbody"></tbody></table></div>');
		//表格主体
		var _tBody=_tableObj.find("#tmpCancelTbody");
		
		//所有数据
		var tableData = this.tmpCancelRecords;
		if (tableData != null) {
			for (var i=0;i<tableData.length;i++) {
				var rowData=tableData[i];
				var _row = this.createRow(_tBody, rowData, i, false);
				_tBody.append(_row);
			}
		}
	};
	//创建通告单场景表格
	this.createNoticeViewGrid = function() {
		var _comparePanelObj = $("#"+this.tableId);
		var _tableObj = _comparePanelObj.find("#noticeViewPanel");
		
		_tableObj.children().remove();
		_tableObj.append("<h4 class='cancelview-title'>通告单销场信息</h4>");
		_tableObj.append('<div class="theadDiv"><table cellpadding="0" cellspacing="0" border="0"><thead><tr id="noticeViewThead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#noticeViewThead");

		_head.append('<td class="checkboxTd"><input type="checkbox" id="noticeViewCheckAll" class="line-height"/></td>');
		//电影剧本不显示集次信息
		if (crewType == Constants.CrewType.movie) {
			_head.append('<td><p class="seriesViewNo">场次</p></td>');
		} else {
			_head.append('<td><p class="seriesViewNo">集-场</p></td>');
		}
		_head.append('<td><p class="shootStatus">拍摄状态</p></td>');
		_head.append('<td><p class="finishDate">完成时间</p></td>');
		_head.append('<td><p class="tapNo">带号</p></td>');
		_head.append('<td><p class="remark">备注</p></td>');
		
		_tableObj.append('<div class="tbodyDiv"><table cellpadding="0" cellspacing="0" border="0"><tbody id="noticeViewTbody"></tbody></table></div>');
		//表格主体
		var _tBody=_tableObj.find("#noticeViewTbody");
		
		//所有数据
		var tableData = this.noticeViewRecords;
		if (tableData != null) {
			for (var i=0;i<tableData.length;i++) {
				var rowData=tableData[i];
				var _row = this.createRow(_tBody, rowData, i, true);
				_tBody.append(_row);
			}
		}
		
		//新添加的场
		var newArrData = this.newAddViewInfoList;
		if (newArrData != null && newArrData.length > 0) {
			var _row = $("<tr></tr>");
			_row.append("<td class='newViewTitleTd' colspan=6><p>新添场景</p></td>");
			_tBody.append(_row);
			
			for (var i=0;i<newArrData.length;i++) {
				var rowData=newArrData[i];
				var _row = this.createRow(_tBody, rowData, i, true);
				_tBody.append(_row);
			}
		}
		
		//checkbox全选
		$("#noticeViewCheckAll").click(function(){
			if(this.checked){
				_tableObj.find("tbody :checkbox").prop("checked",true);
			}else{
				_tableObj.find("tbody :checkbox").prop("checked",false);
			}
		});
		
		_tBody.on("click", ":checkbox", function(ev) {
			isCheckAll();
			ev.stopPropagation();
		}).on("click", "tr", function() {
			$(this).find(":checkbox").trigger("click");
		});
		
		//行checkbox事件只判断是否全选
		function isCheckAll(){
			var checkboxs = _tableObj.find("tbody :checkbox");
			
			for(var i=0, len=checkboxs.length; i<len;i++){
				//console.log(checkboxs[i].checked);
				if(!checkboxs[i].checked)
					break;
			}
			
			if(i != len){
				$("#noticeViewCheckAll").prop("checked",false);
			}else{
				$("#noticeViewCheckAll").prop("checked",true);
			}
		}
	};
	
	//选中一行数据
	this.checkedItem = function(userId) {
		var tableId = this.tableId;
		$("#" + tableId).find(":checkbox[id="+ userId +"]").trigger("checked");
	};
	
	this.unCheckAll = function() {
		$("#" + tableId).find(":checkbox").trigger("unChecked");
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid, needCheckbox) {
		
		var trBgcolor = "#fff";
		var shootStatus = "";
		var finishDate = "";
		if (typeof(rowData.shootStatus) == "string") {
			trBgcolor = viewStatusColor.get(noticeShootStatusMap.getKey(rowData.shootStatus));
			shootStatus = rowData.shootStatus;
			finishDate = rowData.shootDate == null ? "" : new Date(rowData.shootDate).Format("yyyy-MM-dd");
		}
		if (typeof(rowData.shootStatus) == "number") {
			trBgcolor = viewStatusColor.get(rowData.shootStatus);
			shootStatus = noticeShootStatusMap.get(rowData.shootStatus);
			finishDate = rowData.finishDate == null ? "" : new Date(rowData.finishDate).Format("yyyy-MM-dd");
		}
		
		var _row = $("<tr style='background-color:"+ trBgcolor +"' rowId='"+rowid+"'></tr>");
		
		var tapNo = rowData.tapNo == null ? "" : rowData.tapNo;
		var remark = rowData.remark == null ? "" : rowData.remark;
		
		if (needCheckbox) {
			_row.append('<td class="checkboxTd"><input type="checkbox" id="checkedAll" viewId='+ rowData.viewId +' scenarioViewNo='+ rowData.seriesNo + "-" + rowData.viewNo +' class="line-height"/></td>');
		}
		
		//电影剧本不显示集次信息
		if (crewType == Constants.CrewType.movie) {
			_row.append('<td title="'+ rowData.viewNo +'"><p class="seriesViewNo">'+ rowData.viewNo +'</p></td>');
		} else {
			_row.append('<td title="'+ rowData.seriesNo + "-" + rowData.viewNo +'"><p class="seriesViewNo">'+ rowData.seriesNo + "-" + rowData.viewNo +'</p></td>');
		}
		
		_row.append('<td title="'+ shootStatus +'"><p class="shootStatus">'+ shootStatus +'</p></td>');
		_row.append('<td title="'+ finishDate +'"><p class="finishDate">'+ finishDate +'</p></td>');
		_row.append('<td title="'+ tapNo +'"><p class="tapNo">'+ tapNo +'</p></td>');
		_row.append('<td title="'+ remark +'"><p class="remark">'+ remark +'</p></td>');
		return _row;
	};
	
	//获取选中行的集-场号
	this.getSelectedSeriesViewNo = function() {
		var result = "";
		var _tableObj = $("#"+this.tableId);
		_tableObj.find("tbody :checkbox:checked").each(function(index) {
			if(index == 0){
				result = $(this).attr("scenarioViewNo");
			} else {
				result += ","+$(this).attr("scenarioViewNo");
			}
		});
		return result;
	};
	
	//获取选中行的场景ID
	this.getSelectedViewIds = function() {
		var result = "";
		var _tableObj = $("#"+this.tableId);
		_tableObj.find("tbody :checkbox:checked").each(function(index) {
			if(index == 0){
				result = $(this).attr("viewId");
			} else {
				result += ","+$(this).attr("viewId");
			}
		});
		return result;
	};
	
	this.refreshGrid = function() {
		this.loadTable();
	};
}*/

