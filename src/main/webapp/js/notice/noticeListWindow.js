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
			url: "/notice/loadNotice",
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
function loadNoticeViewTable(noticeStr, gridDiv, data, index, noticeId){
	//showSuccessMessage("正在加载...");
	
	//var noticeId = noticeInfo.noticeId;
	this.isRowClick=true;
	
	//gridDiv.addClass("box_wrap");
	//gridDiv.append("<div id='toolbar"+index+"' class='title'></div>");
	
	//loadSearchViewGrid(gridDiv.find("#toolbar"+index), index,noticeInfo);
	
	var gridMainDiv = $("<div class='t_i'></div>");
	
	
	var gridHead_array = ["<div class='t_i' id='hh"+ index +"'><div class='ee'><table cellpadding='0' cellspacing='0' border='0'><thead>"];
	gridHead_array.push("<tr>");
	
	gridHead_array.push("<td style='display:none;'><input style='width:30px;margin-left:auto;margin-right:auto;' type='checkbox' id='all"+index+"' class='line-height'/></td>");
	gridHead_array.push("<td><p style='width:60px;'></p></td>");
	if (crewType == Constants.CrewType.movie || crewType == 3) {
		gridHead_array.push("<td><p style='width:50px;'></p></td>");
	} else {
		gridHead_array.push("<td><p style='width:50px;'></p></td>");
	}
	gridHead_array.push("<td><p style='width:50px;'></p></td>");
	gridHead_array.push("<td><p style='width:65px;'></p></td>");
	/*gridHead_array.push("<td><p style='width:50px;'>内外</p></td>");*/
	gridHead_array.push("<td><p style='width:50px;'></p></td>");
	
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	gridHead_array.push("<td><p style='width:100px;'></p></td>");
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	gridHead_array.push("<td><p style='width:130px;'></p></td>");
	/*gridHead_array.push("<td><p style='width:120px;'>群众演员</p></td>");*/
	
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	/*gridHead_array.push("<td><p style='width:150px;'>服装</p></td>");
	
	gridHead_array.push("<td><p style='width:150px;'>化妆</p></td>");*/
	gridHead_array.push("<td><p style='width:150px;'></p></td>");
	gridHead_array.push("<td style='border-right:none !important;'><p style='width:150px;'></p></td>");
	
	//gridHead_array.push("<td><p style='width:100px;'></p></td>");
	gridHead_array.push("<td><p style='width:7px;'></p></td>");
	
	gridHead_array.push("</tr>");
	gridHead_array.push("</thead></table></div></div>");
	
	var gridHeadDiv = $(gridHead_array.join(""));
	
	var gridContentDiv = $("<div class='cc' id='cc"+ index +"' onscroll='viewGridScroll("+ index +")'></div>");
	
	var table=$("<table cellpadding='0' cellspacing='0' border='0' noticeId='"+noticeId+"' id='noticeViewGrid"+index+"'></table>");
	
	table.append("<tbody id='noticeViewTbody"+index+"'><tbody>");
	
	$.each(data,function(noticeViewIndex,item){
		var _row = createRow(noticeStr ,data.length,noticeId,index, noticeViewIndex, item, crewType);
		table.find("#noticeViewTbody"+index).append(_row);
	});
	
	gridContentDiv.append(table);
	gridMainDiv.append(gridHeadDiv);
	gridMainDiv.append(gridContentDiv);
	gridDiv.append(gridMainDiv);
	
	//全选复选框点击事件
	$("#all"+index).on("click",function(event){
		if($("#all"+index).prop("checked")){
			$(this).trigger("checked");
		} else {
			$(this).trigger("unChecked");
		}
	});
	
	//checkbox指定全部不选
	$("#all"+index).bind("unChecked",function(){
		$("#all"+index).prop("checked",false);
		gridDiv.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all"+index).bind("checked",function(){
		$("#all"+index).prop("checked",true);
		gridDiv.find("tbody :checkbox").trigger("checked");
	});
	
	/*//行checkbox事件只判断是否全选(行checkbox值发生变化时调用该方法)
	function isCheckAll() {
		var unChecked = gridDiv.find("tbody :checked").not(":checkbox[id^='all']").length;
		if(unChecked>=data.length){
			$("#all"+index).prop("checked",true);
		}else{
			$("#all"+index).prop("checked",false);
		}
	}
	
	gridDiv.find("tbody :checkbox").bind("checked",function(){
		$(this).prop("checked",true);
		$(this).parents("tr").addClass("mouse_click");
		
		isCheckAll();
	});
	
	gridDiv.find("tbody :checkbox").bind("unChecked",function(){
		$(this).prop("checked",false);
		$(this).parents("tr").removeClass("mouse_click");

		isCheckAll();
	});*/
	
	
	/*var fixHelper = function(e, ui) {  
            //console.log(ui)   
            ui.children().each(function() {  
                $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
            });  
            return ui;
        };
	$("#noticeViewTbody"+index).sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#noticeViewTbody"+index).on( "sortstop", function( event, ui ) {
    	updateViewSort(index);
	});*/
}

/**
 * 重新加载表格
 * @param data
 * @param index
 * @param noticeId
 */
function reloadNoticeView(data,index,noticeId){
	//showSuccessMessage("正在加载...");
	var table=$('#noticeViewGrid'+index);
	
	var _this = this;
	table.find("tr").remove();
	table.append("<tbody id='noticeViewTbody"+index+"'><tbody>");
	
	//全选复选框反选
	$("#all"+index).prop("checked",false);
	
	$.each(data,function(noticeViewIndex,item){
		var _row = createRow(index, noticeViewIndex, item);
		table.find("#noticeViewTbody"+index).append(_row);
	});
	
	//全选复选框点击事件
	$("#all"+index).on("click",function(event){
		if($("#all"+index).prop("checked")){
			$(this).trigger("checked");
		} else {
			$(this).trigger("unChecked");
		}
	});
	
	//checkbox指定全部不选
	$("#all"+index).bind("unChecked",function(){
		$("#all"+index).prop("checked",false);
		table.find("tbody :checkbox").trigger("unChecked");
	});
	
	//checkbox指定全选
	$("#all"+index).bind("checked",function(){
		$("#all"+index).prop("checked",true);
		table.find("tbody :checkbox").trigger("checked");
	});
	
	/*//行checkbox事件只判断是否全选(行checkbox值发生变化时调用该方法)
	function isCheckAll() {
		var unChecked = table.find("tbody :checked").not(":checkbox[id^='all']").length;
		if(unChecked>=data.length){
			$("#all"+index).prop("checked",true);
		}else{
			$("#all"+index).prop("checked",false);
		}
	}
	
	table.find("tbody :checkbox").bind("checked",function(){
		$(this).prop("checked",true);
		$(this).parents("tr").addClass("mouse_click");
		
		isCheckAll();
	});
	
	table.find("tbody :checkbox").bind("unChecked",function(){
		$(this).prop("checked",false);
		$(this).parents("tr").removeClass("mouse_click");

		isCheckAll();
	});*/
	
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#noticeViewTbody"+index).sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#noticeViewTbody"+index).on( "sortstop", function( event, ui ) {
		updateViewSort(index);
	});
}

//刷新表格的单行数据
function updaterowdata(tableIndex, rowIndex, rowData) {
	var _tBody = $("#noticeViewTbody" + tableIndex);
	//生成一行数据
	var _row = createRow(tableIndex, rowIndex, rowData);
	//替换掉指定行
	_tBody.find("#notice" + tableIndex + "row" + rowIndex).replaceWith(_row);
	
	_row.find(":checkbox").trigger("unChecked");
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
function createRow (noticeStr, totalRowCount,noticeId,tableIndex, rowIndex, rowData) {
	var _this = this;
	var table=$('#noticeViewGrid'+tableIndex);
	
	var statusClass=" style='background-color:"+getColor(rowData.shootStatus)+"' ";
	var _row = $("<tr id='notice"+tableIndex+"row"+rowIndex+"' "+ statusClass +" name='"+ noticeId +"'></tr>");
	if (rowIndex == 0) {
		var dateArr = noticeStr.split("-");
		var disTop = 3*totalRowCount;
		if (totalRowCount>2) {
			//通告单统计信息
			_row.append("<td rowspan='"+ totalRowCount +"' style='background-color:white !important;'><p class='notice-shootdays-p'>"+ dateArr[0] + "</p><p style='width:160px;text-align:center;color:#aaa;margin-top:"+ disTop +"px;'>"+ dateArr[1] +"</p></td>");
		}else {
			_row.append("<td rowspan='"+ totalRowCount +"' style='background-color:white !important;'><p class='notice-shootdays-p'>"+ dateArr[0] + "</p><p style='width:160px;text-align:center;color:#aaa;'>"+ dateArr[1] +"</p></td>");
		}
	}
	
	var shootStatusText = "";
	if(rowData.shootStatus){
		shootStatusText=rowData.shootStatus;
	}
	_row.append("<td><p style='width:50px;'>"+shootStatusText+"</p></td>");//拍摄状态
	
	//电影类型的剧组不显示集次
	if (crewType == Constants.CrewType.movie || crewType == 3) {
		if (rowData.viewNo != undefined) {
			_row.append("<td><p style='width:50px;'>"+ rowData.viewNo+"</p></td>");//集场
		}else{
			_row.append("<td><p style='width:50px;'></p></td>");//集场
		}
	} else {
		if (rowData.seriesNo != undefined && rowData.viewNo != undefined) {
			_row.append("<td><p style='width:50px;'>"+rowData.seriesNo+"-"+rowData.viewNo+"</p></td>");//集场
		}else{
			_row.append("<td><p style='width:50px;'></p></td>");//集场
		}
	}
	
	//添加集场数组数据
//	
//	var seasonText= seasonMap.get(rowData.season);
//	if(seasonText==null){
//		seasonText="";
//	}
//	_row.append("<td><p style='width:50px;'>"+seasonText+"</p></td>");//季节
	
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
	
	/*_row.append("<td><p style='width:50px;'>"+atmosphere+"</p></td>");*/
//    _row.append("<td><p style='width:50px;'>"+siteText+"</p></td>");//内外景
    
    
    var pageCountText="";
   	if(rowData.pageCount){
   		pageCountText=rowData.pageCount;
   	}
   	_row.append("<td><p style='width:50px;'>"+pageCountText+"</p></td>");//页数
   	
   	//拍摄地点
   	var shootLocationText="";
	if(rowData.shootLocation){
		shootLocationText=rowData.shootLocation;
	}
	_row.append("<td><p style='width:150px;' title='"+ shootLocationText +"'>"+shootLocationText+"</p></td>");
    
    //场景
    var majorViewText=""; //主场景
    var minorViewText=""; //次场景
    var thirdLevelViewText=""; //三级场景
   	if(rowData.majorView){
   		majorViewText = rowData.majorView;
   		if(rowData.minorView){
   			minorViewText = rowData.minorView;
   			if(rowData.thirdLevelView){
   				thirdLevelViewText=rowData.thirdLevelView;
   				_row.append("<td><p style='width:180px;' title='"+ majorViewText + " | " + minorViewText + " | " + thirdLevelViewText +"'>" + majorViewText + " | " + minorViewText + " | " + thirdLevelViewText+"</p></td>");
   			}else {
   				_row.append("<td><p style='width:180px;' title='"+ majorViewText + " | " + minorViewText +"'>" + majorViewText + " | " + minorViewText +"</p></td>");
			}
   		}else {
   			if(rowData.thirdLevelView){
   				thirdLevelViewText=rowData.thirdLevelView;
   				_row.append("<td><p style='width:180px;' title='"+ majorViewText + " | " + thirdLevelViewText +"'>" + majorViewText + " | " + thirdLevelViewText +"</p></td>");
   			}else {
   				_row.append("<td><p style='width:180px;' title='"+ majorViewText +"'>" + majorViewText + "</p></td>");
			}
		}
   	}else {
   		if(rowData.minorView){
   			minorViewText = rowData.minorView;
   			if(rowData.thirdLevelView){
   				thirdLevelViewText=rowData.thirdLevelView;
   				_row.append("<td><p style='width:180px;' title='"+ minorViewText + " | " + thirdLevelViewText +"'>" + minorViewText + " | " + thirdLevelViewText +"</p></td>");
   			}else {
   				_row.append("<td><p style='width:180px;' title='"+ minorViewText +"'>" + minorViewText +"</p></td>");
			}
   		}else {
   			if(rowData.thirdLevelView){
   				thirdLevelViewText=rowData.thirdLevelView;
   				_row.append("<td><p style='width:180px;' title='"+ thirdLevelViewText +"'>" + thirdLevelViewText +"</p></td>");
   			}else {
   				_row.append("<td><p style='width:180px;' title=''></p></td>");
			}
		}
	}
    
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
   	_row.append("<td><p style='width:160px;' title='"+ mainContentText +"'>"+mainContentText+"</p></td>");//主要内容
	/*var pageCountText="";
   	if(rowData.pageCount){
   		pageCountText=rowData.pageCount;
   	}
   	_row.append("<td><p style='width:50px;'>"+pageCountText+"</p></td>");*/ //页数
	var roleListText="";
   	if(rowData.roleList){
   		roleListText=rowData.roleList;
   	}
   	_row.append("<td><p style='width:179px;' title='"+ roleListText +"'>"+roleListText+"</p></td>");//主要演员
   	
   	//特约演员、群众演员
	var guestRoleListText="";
	var massRoleListText="";
   	if(rowData.guestRoleList){
   		guestRoleListText=rowData.guestRoleList;
   		if(rowData.massRoleList){
   	   		massRoleListText=rowData.massRoleList;
   	   		_row.append("<td><p style='width:140px;' title='"+ guestRoleListText+ " | " + massRoleListText +"'>"+guestRoleListText+ " | " + massRoleListText +"</p></td>");
   	   	}else {
   	   		_row.append("<td><p style='width:140px;' title='"+ guestRoleListText +"'>"+guestRoleListText+"</p></td>");//特约演员
		}
   	}else {
   		if(rowData.massRoleList){
   	   		massRoleListText=rowData.massRoleList;
   	   		_row.append("<td><p style='width:140px;' title='"+ massRoleListText +"'>"+massRoleListText+"</p></td>");//特约演员
   		}else {
   			_row.append("<td><p style='width:140px;' title=''></p></td>");//特约演员
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
	//特殊道具
	var specialPropsListText="";
	if(rowData.specialPropsList){
   		specialPropsListText=rowData.specialPropsList;
   	}
	if(rowData.makeupName){ //化妆
		makeupNameText=rowData.makeupName;
		if(rowData.clothesName){ //服装
			clothesNameText=rowData.clothesName;
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText + " | " + propsListText +"'>" + makeupNameText + " | " + clothesNameText + " | " + propsListText+ " | "+ specialPropsListText +"</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText + " | " + propsListText +"'>" + makeupNameText + " | " + clothesNameText + " | " + propsListText+"</p></td>");
				}
			}else {
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText + " | " + propsListText +"'>" + makeupNameText + " | " + clothesNameText + " | " + specialPropsListText+"</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + clothesNameText +"'>" + makeupNameText + " | " + clothesNameText + "</p></td>");
				}
			}
		}else {
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + propsListText + "|" + specialPropsListText + "'>" + makeupNameText + " | " + propsListText + "|" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + " | " + propsListText +"'>" + makeupNameText + " | " + propsListText + "</p></td>");
				}
			}else {
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText + "|" + specialPropsListText + "'>" + makeupNameText + "|" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ makeupNameText +"'>" + makeupNameText + "</p></td>");
				}
			}
		}
		
	}else {
		if(rowData.clothesName){ //服装
			clothesNameText=rowData.clothesName;
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ clothesNameText + " | " + propsListText +"'>" + clothesNameText + " | " + propsListText + "|" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ clothesNameText + " | " + propsListText +"'>" + clothesNameText + " | " + propsListText + "</p></td>");
				}
			}else {
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ clothesNameText + "|" + specialPropsListText +"'>" + clothesNameText+ "|" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ clothesNameText +"'>" + clothesNameText + "</p></td>");
				}
			}
		}else {
			if(rowData.propsList){ // 普通道具
				propsListText=rowData.propsList;
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ propsListText + "|" + specialPropsListText +"'>" + propsListText+ "|" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title='"+ propsListText +"'>" + propsListText + "</p></td>");
				}
			}else {
				if(rowData.specialPropsList){
					_row.append("<td><p style='width:150px;' title='"+ specialPropsListText +"'>" + specialPropsListText + "</p></td>");
				}else {
					_row.append("<td><p style='width:150px;' title=''></p></td>");
				}
			}
		}
	}
   /*	_row.append("<td><p style='width:150px;' title='"+ clothesNameText +"'>"+clothesNameText+"</p></td>");//服装
   	_row.append("<td><p style='width:150px;' title='"+ makeupNameText +"'>"+makeupNameText+"</p></td>");//化妆
*/   	
   	//特殊道具
	/*var specialPropsListText="";
   	if(rowData.specialPropsList){
   		specialPropsListText=rowData.specialPropsList;
   	}*/
   	//_row.append("<td><p style='width:150px;' title='"+ specialPropsListText +"'>"+specialPropsListText+"</p></td>");//特殊道具
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
*/	/*var advertNameText="";
   	if(rowData.advertName){
   		advertNameText=rowData.advertName;
   	}
   	_row.append("<td><p style='width:150px;' title='"+ advertNameText +"'>"+advertNameText+"</p></td>");//商植
*/	/*var shootDateText="";
   	if(rowData.shootDate){
   		shootDateText=rowData.shootDate;
   	}
   	_row.append("<td><p style='width:100px;'>"+shootDateText+"</p></td>");//拍摄时间
	var tapNoText="";
   	if(rowData.tapNo){
   		tapNoText=rowData.tapNo;
   	}
   	_row.append("<td><p style='width:80px;'>"+tapNoText+"</p></td>");*/ //带号
   	//带号
   	var tapNoText="";
   	if(rowData.tapNo){
   		tapNoText=rowData.tapNo;
   	}
   	_row.append("<td><p style='width:130px;'>"+tapNoText+"</p></td>");
   	
   	//备注
	var remarkText="";
   	if(rowData.remark){
   		remarkText=rowData.remark;
   	}
   	_row.append("<td><p style='width:180px;' title='"+ remarkText +"'>"+remarkText+"</p></td>");
   	
   	//行checkbox事件只判断是否全选(行checkbox值发生变化时调用该方法)
	function isCheckAll() {
		var UnChecked = $("#noticeViewTbody" + tableIndex).find(":checkbox").not(function(index){
			if($(this).prop("checked")){
				return this;
			}
		}).length;
		if(UnChecked==0){
			$("#all"+tableIndex).prop("checked",true);
		}else{
			$("#all"+tableIndex).prop("checked",false);
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
function updateViewSort(index){
	var viewIds = "";
	$("#noticeViewTbody"+index).find(":checkbox").each(function(){
    		viewIds+=$(this).val()+",";
	});
	viewIds=viewIds.substring(0,viewIds.length-1);
	var noticeId=$("#noticeViewGrid"+index).attr("noticeId");
	$.ajax({
		url:ctx+"/notice/sortNoticeView",
		type:"post",
		dataType:"json",
		data:{noticeId:noticeId,viewIds:viewIds},
		success:function(data){
		}
	});
	
}

/**
 * 生成表格的工具栏
 * @param toolbar 工具栏jquery对象
 * @param index	当前表格的下标
 * @param noticeId notice数据
 */
function loadSearchViewGrid(toolbar,index,noticeInfo){
	
	var noticeId = noticeInfo.noticeId;
	var container = toolbar;
    //生成头部查询条件
    var html= "<div style='margin:5px;'>"
    	+ "<div class='noticeViewToolbar' id='addNoticeView"+index+"'>"
		+"<div id='viewList"+index+"'></div>"		
        + "</div>"
    	+ "<input type='button' class='noticeViewToolbar setAddressBtn' id='setAddress"+index+"' onclick='loadSetAddressWindow(\""+ noticeId +"\",\""+index+"\")'/>"
        + "<input type='button' class='noticeViewToolbar removeView' id='deleteNoticeView"+index+"'/>"
        + "<input type='button' class='noticeViewToolbar cancelView' id='setShootStatusButton"+index+"'/>"
        + "<input type='button' class='noticeViewToolbar export' id='exportNoticeBtn"+ index +"' onclick='downloadNotice(\""+noticeId+"\")' >"
	    + "<input type='button' class='noticeViewToolbar printView' id='printViewBtn"+ index +"' onclick='printView(\""+ noticeId +"\",\""+index+"\")' > "
	    + "<input type='button' class='noticeViewToolbar modify' id='modifyNoticeBtn"+ index +"' onclick='updateNotice(\""+noticeId+"\")' >"
	    + "<input type='button' class='noticeViewToolbar delete' id='deleteNoticeBtn"+ index +"' onclick='deleteNotice(\""+noticeId+"\")' >"
    	+ "<input type='button' class='noticeViewToolbar fedback' id='noticeFedBack"+ index +"' onclick='loadFedbackWindow(\""+noticeId+"\")' >"
		+ "<input type='button' class='noticeViewToolbar compareCancel' id='compareCancel"+ index +"' onclick='loadCompareCancelWindow(\""+ index +"\",\""+noticeId+"\")' >";
    
    html += "<input type='button' style='float:right'  value='' id='viewColorExample' class='colorExample4'>";
    html += "<div>";
    toolbar.append(container);
    container.append(html);

    $("#setAddress" + index).jqxTooltip({ content: '设置拍摄地', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#deleteNoticeView" + index).jqxTooltip({ content: '移出场景', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#setShootStatusButton" + index).jqxTooltip({ content: '销场', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#exportNoticeBtn" + index).jqxTooltip({ content: '导出', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#printViewBtn" + index).jqxTooltip({ content: '打印发布', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#modifyNoticeBtn" + index).jqxTooltip({ content: '修改通告单信息', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#deleteNoticeBtn" + index).jqxTooltip({ content: '删除通告单', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#noticeFedBack" + index).jqxTooltip({ content: '查看回复意见', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    $("#compareCancel" + index).jqxTooltip({ content: '确认销场', position: 'bottom', autoHide: true, name: 'movieTooltip'});
    
    //弹出设置场景状态窗口
	$("#setShootStatusButton"+index).on("click",function(){
		var rowindexes = $('#noticeViewTbody'+index+' :checked');
    	if(rowindexes.length==0){
    		showErrorMessage("请选择场次！");
    		return;
    	}
    	$("#refreshNoticeIndex").val(index);
		$("#setStatusWindow").jqxWindow('open');
    	
    	var viewIds = "";
    	$.each(rowindexes,function(){
    		viewIds+=$(this).val()+",";
    	})
    	
    	viewIds=viewIds.substring(0,viewIds.length-1);
		
    	$("#selectedViewId").val(viewIds);
    	
    });
    
  //删除场
    $("#deleteNoticeView"+index).on("click",function(){
    	
    	var rowindexes =$('#noticeViewTbody'+index+' :checked');
    	if(rowindexes.length==0){
    		showErrorMessage("请选择要移除的场次！");
    		return;
    	}
    	
    	var finishedView = "";
    	$.each(rowindexes,function(){
    		var shootStatus = $(this).attr("shootStatus");
    		//拍摄状态为已完成
    		if (shootStatus == "完成") {
    			finishedView = "所选场景中有已完成的场次，";
    			return false;
    		}
    	});
    	
    	popupPromptBox("提示", finishedView + "确定移出所选场次？", function() {
        	var viewIds = "";
        	$.each(rowindexes,function(){
        		viewIds+=$(this).val()+",";
        	});
        	viewIds=viewIds.substring(0,viewIds.length-1);
        	$.ajax({
        		url:"/notice/deleteNoticeView",
        		data:{viewIds:viewIds,noticeId:noticeId},
        		dataType:"json",
        		type:'post',
       			async:false,
       			success:function(data){
       				if(data.status==1){
       					showErrorMessage(data.message);
       				}else{
       					var noticeId=$("#noticeViewGrid"+index).attr("noticeId");
        				$.ajax({
        	        		url:ctx+"/notice/loadNoticeView",
        	        		type:"post",
        	        		dataType:"json",
        	        		data:{noticeId:noticeId},
        	        		success:function(data){
        	        			reloadNoticeView(data.viewList,index,noticeId);
        	        		}
        	        	});
           				//$("#grid"+index).jqxGrid('updatebounddata', 'cells');
           				$("#viewList"+index).jqxGrid('updatebounddata', 'cells');
        				refreshSingleNoticeRowData(noticeId, $("#jqxgrid"));
       				}
       			}
        	});
		});
    });
  
  	//添加场
    $("#addNoticeView"+index).jqxDropDownButton({theme:theme,height: 20, width: 90,popupZIndex:10000});
    $("#addNoticeView"+index).jqxDropDownButton('setContent', '添加场景');
    
    $("#addNoticeView"+index).on("click",function(){
    	
    	$("#refreshNoticeIndex").val(index);
    });
    
    $("#addNoticeView"+index).on("open",function(){
        
    	//设置锁死弹出层
        $("#addNoticeView"+index).jqxDropDownButton("setCloseLock",true);
        
        LayerShow();
    	
        if (addViewList[index] == null || addViewList[index] == undefined || addViewList[index] == "") {
    	   gridObj = searchGridArray[index];
           gridSource = addViewSourceArray[index];
           
           popupLayer.jqxWindow('open');
    	}
    });
    
    $("#addNoticeView"+index).on("close",function(){
    	addNoticeViewIds="";
    	$("#viewList"+index).jqxGrid('clearselection');
    });
    
    var addViewSource= {
    	datafields: [
			{ name: 'viewId,',type: 'string' },
			{ name: 'seriesNo,',type: 'int' },
			{ name: 'viewNo,',type: 'string' },
	        { name: 'season',type: 'int' },//季节 seasonMap
	        { name: 'atmosphereId',type: 'int' },//气氛
	        { name: 'site',type: 'string' },//内外景
	        { name: 'type',type: 'int' },//文戏武戏
	        { name: 'shootLocation',type: 'string' },//拍摄地点
	        { name: 'majorView',type: 'string' },//主场景
	        { name: 'minorView',type: 'string' },//次场景
	        { name: 'thirdLevelView',type: 'string' },//三级场景
	        { name: 'mainContent',type: 'string' },//主要内容
	        { name: 'pageCount',type: 'string' },//页数
	        { name: 'roleList',type: 'string' },//
	        { name: 'guestRoleList',type: 'string' },//特约演员
	        { name: 'massRoleList',type: 'string' },//群众演员
	        { name: 'propsList',type: 'string' },//普通道具
	        { name: 'clothesName',type: 'string' },//服装
	        { name: 'makeupName',type: 'string' },//化妆
	        { name: 'specialPropsList',type: 'string' },//个人道具
            { name: 'advertName',type: 'string' },//广告
	        { name: 'shootDate',type: 'string' },//拍摄时间
	        { name: 'remark',type: 'string' },//备注
	        { name: 'shootStatus',type: 'string' }//拍摄状态
        ],
        datatype: "json",
        type:'post',
        beforeprocessing:function(data){
        	
        	//查询之后可执行的代码\
        	var datainformation = $("#viewList"+index).jqxGrid('getdatainformation');
        	
        	var paginginformation = datainformation.paginginformation;
        	var pagenum = paginginformation.pagenum;
        	var pagesize = paginginformation.pagesize;
        	var pagescount = paginginformation.pagescount;
        	
        	addViewSource.totalrecords=data.total;
        	addViewList[index]=data.viewList;
        },
        root:'viewList',
        data:{noticeId:noticeId,pageFlag:true},
        url:ctx+'notice/loadNoticeView'
    };
    
    addViewSourceArray[index]=addViewSource;
    var viewColumn_Button = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+addViewList[index][getIndex(row,index)].seriesNo+"-"+addViewList[index][getIndex(row,index)].viewNo+"</div>";
    };
    var viewNoColumn_Button = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+addViewList[index][getIndex(row,index)].viewNo+"</div>";
    };
    var seasonColumn_Button=function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var seasonText= seasonMap.get(addViewList[index][getIndex(row,index)].season);
    	if(seasonText==null) {
    		seasonText="";
    	}
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+seasonText+"</div>";
    };
    var typeColumn_Button=function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var typeText = typeMap.get(addViewList[index][getIndex(row,index)].viewType);
    	if(typeText==null){
    		typeText="";
    	}
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+typeText+"</div>";
    };
    var atmosphere_Button=function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var atmosphere= addViewList[index][getIndex(row,index)].atmosphereName;
    	if(atmosphere==null){
    		atmosphere="";
    	}
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+atmosphere+"</div>";
    };
    var shootStatusColumn_Button=function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var shootStatusText = addViewList[index][getIndex(row,index)].shootStatus;
    	if(shootStatusText==null){
    		shootStatusText="";
    	}
    	return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+shootStatusText+"</div>";
    };
    
    var rendergridrows = function (params) {
    	
    	//调用json返回的列表数据
        return params.data;
    };
    var viewColumns = [];
    if (crewType == Constants.CrewType.movie) {
    	viewColumns.push({ text: '场次', cellsrenderer: viewNoColumn_Button, width: 65 ,pinned: true });
    } else {
    	viewColumns.push({ text: '集-场', cellsrenderer: viewColumn_Button, width: 65 ,pinned: true });
    }
    viewColumns.push({ text: '季节', cellsrenderer: seasonColumn_Button, width: 40 },
       { text: '气氛',cellsrenderer: atmosphere_Button, width: 40 },
       { text: '内外景', datafield: "site" ,width: 50 },
       { text: '文武戏', cellsrenderer: typeColumn_Button , width: 60 },
       { text: '拍摄地点', datafield: 'shootLocation', width: 120 },
       { text: '主场景', datafield: 'majorView', width: 120 },
       { text: '次场景', datafield: 'minorView', width: 120 },
       { text: '三级场景', datafield: 'thirdLevelView', width: 120 },
       { text: '主要内容', datafield: 'mainContent', width: 120 },
       { text: '页数', datafield: 'pageCount', width: 80, cellsformat:"d2" },
       { text: '主要演员',datafield: 'roleList', width: 150 },
       { text: '特约演员', datafield: 'guestRoleList', width: 90 },
       { text: '群众演员', datafield: 'massRoleList', width: 90 },
       { text: '道具', datafield: 'propsList', width: 90},
       { text: '特殊道具', datafield: 'specialPropsList', width: 90},
       { text: '服装', datafield: 'clothesName', width: 90 },
       { text: '化妆', datafield: 'makeupName', width: 90 },
       { text: '商植', datafield: 'advertName', width: 90 },
       { text: '拍摄时间', datafield: 'shootDate', width: 90 },
       { text: '备注', datafield: 'remark', width: 90 },
       { text: '拍摄状态', cellsrenderer:  shootStatusColumn_Button, width: 90 });
    
    
    var addViewAdapter = new $.jqx.dataAdapter(addViewSource);
    
    $("#viewList"+index).jqxGrid({
    	theme:theme,
        width: 800,
        source: addViewAdapter,
        selectionmode: 'checkbox',
        pageable: true,
        autoheight: false,
        height: 500,
        columnsresize: true,
        showtoolbar: true,
        pagesize: 50,
        pagesizeoptions: ['50', '100'],
        virtualmode: true,
        //pagermode: 'simple',
        altrows: true,
        rendergridrows:rendergridrows,
        localization:localizationobj,//表格文字设置
        rendertoolbar: function (toolbar) {
            
        	var me = this;
            var container = $("<div style='margin: 5px;'></div>");
            var selected="<span class='badge' id='_already_selected_"+index+"' style='vertical-align: bottom'>0</span>";
            
            var button = $("<input type='button' class='advanceSearch' id='searchButton"+index+"'/>");
            var html="<input type='button' class='addToNotice' id='addNoticeButton"+index+"'/>";
            html+="<input type='button' class='close' id='closeGridButton"+index+"'/>";
            
            toolbar.append(container);
            container.append(selected);
            container.append(button);
            container.append(html);
            
            $("#searchButton" + index).jqxTooltip({ content: '高级查询', position: 'bottom', autoHide: true, name: 'movieTooltip'});
            $("#addNoticeButton" + index).jqxTooltip({ content: '加入通告单', position: 'bottom', autoHide: true, name: 'movieTooltip'});
            
            $("#closeGridButton" + index).jqxTooltip({ content: '关闭', position: 'bottom', autoHide: true, name: 'movieTooltip'});
            
            $("#closeGridButton"+index).on("click",function(){
            	$("#addNoticeView"+index).jqxDropDownButton("setCloseLock",false);
            	$("#addNoticeView"+index).jqxDropDownButton('close');
            	LayerHide();
            	$('#searchWindow').jqxWindow('close');
            });
            
            //$("#addNoticeView"+index).jqxDropDownButton('setContent', 'my content');
            $("#searchButton"+index).on("click",function(){
            	
            	gridObj= searchGridArray[index];
            	gridSource=addViewSourceArray[index];
            	//设置锁死弹出层
            	$("#addNoticeView"+index).jqxDropDownButton("setCloseLock",true);
            	
            	popupLayer.jqxWindow('open');
            });
            
            $("#viewList"+index).on('rowselect', function (event){
                
            	//显示选中的数量
            	$("#_already_selected_"+index).html($("#viewList"+index).jqxGrid('getselectedrowindexes').length);
            	
            	var rowBoundIndex = JSON.stringify(args.rowindex);
                
            	if (rowBoundIndex.indexOf("[") != -1) {
                    rowBoundIndex = rowBoundIndex.substring(1, rowBoundIndex.length-1);
                }
                if (rowBoundIndex.indexOf(",") != -1) {
                    var rowIndexArr = rowBoundIndex.split(",");
                    for (var i = 0; i < rowIndexArr.length; i++) {
                    	if (rowIndexArr[i] && getIndex(rowIndexArr[i],index) != -1) {
                    		addNoticeViewIds += addViewList[index][getIndex(rowIndexArr[i],index)].viewId+",";
                    	}
                    }
                } else {
                	//当反选表格时，该表格不会执行rowunselect事件，而是执行rowselect事件，并且rowBoundIndex值为""
                	if (rowBoundIndex == "") {
                		rowBoundIndex = -1;
                	}
                	if (getIndex(rowBoundIndex,index) != -1) {
                		addNoticeViewIds += addViewList[index][getIndex(rowBoundIndex,index)].viewId+",";
                	} else {
                		addNoticeViewIds = "";
                	}
                }
            });
            
            $("#viewList"+index).on('rowunselect', function (event){
            	
            	//显示选中的数量
            	$("#_already_selected_"+index).html($("#viewList"+index).jqxGrid('getselectedrowindexes').length);
            	
            	if(addNoticeViewIds==""){
            		return;
            	}
    		    var rowBoundIndex = args.rowindex;
    		    addNoticeViewIds=addNoticeViewIds.replace(addViewList[index][getIndex(rowBoundIndex,index)].viewId+",", "");
    		});

            $("#addNoticeButton"+index).on("click",function(){
            	
            	var rowindexes = $("#viewList"+index).jqxGrid('getselectedrowindexes');
            	var grid = $("#viewList"+index);
            	if(addNoticeViewIds==""){
            		showErrorMessage("请选择要添加的场次！");
            		return;
            	}
            	
            	$.ajax({
            		url:"/notice/addNoticeView",
            		data:{viewIds:addNoticeViewIds,noticeId:noticeId},
            		dataType:"json",
            		type:'post',
           			async:false,
           			success:function(data){
           				if(data.status=="1"){
           					showErrorMessage(data.message);
           				}else{
           					addNoticeViewIds="";
           					//$('#addNoticeView'+index).jqxDropDownButton('close');
           					//FIXME 更新通告单场景表
           					var noticeId=$("#noticeViewGrid"+index).attr("noticeId");
            				$.ajax({
            	        		url:ctx+"/notice/loadNoticeView",
            	        		type:"post",
            	        		dataType:"json",
            	        		data:{noticeId:noticeId},
            	        		success:function(data){
            	        			reloadNoticeView(data.viewList,index,noticeId);
            	        		}
            	        	});
               				//$("#grid"+index).jqxGrid('updatebounddata', 'cells');
               				//$("#jqxgrid").jqxGrid('updatebounddata', 'cells');
               				refreshSingleNoticeRowData(noticeId, $("#jqxgrid"));
               				$("#viewList"+index).jqxGrid('updatebounddata', 'cells');
           				}
           			}
            	});
            	addNoticeViewIds="";
            	$("#viewList"+index).jqxGrid('clearselection');
            });
        },
        enabletooltips:true,
        columns: viewColumns
    });

    /*$("#viewList" + index).on("rowclick", function(event) {
    	
    	jqxGridRowClick($("#viewList" + index), event);
    });*/
    
	searchGridArray[index]=$("#viewList"+index);
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
	delDiv.parentNode.removeChild(delDiv)
};

function viewGridScroll(index) {
	/*var b = document.getElementById("cc"+index).scrollLeft;
	document.getElementById("hh"+index).scrollLeft = b;*/
}

function tableScroll() {
	var b = document.getElementById("theadDiv"+index).scrollLeft;
	document.getElementById("tbodyDiv"+index).scrollLeft = b;
}
