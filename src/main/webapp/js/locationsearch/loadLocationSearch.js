/**
 * gridDiv:要生成表格的div
 * data: 表格需要的数据**/

//加载主场景列表
function loadScenceList(gridDiv,data){
    var gridHead_array = [];
    gridHead_array.push("<tr>");
	var tableHeadMap =  data.titleMap;
	var length = 0;
	var actorCount = 0;
	for(var key in tableHeadMap){
		length++;
	}
	actorCount = length - 7;
	var actorColumnWidth = divide(25,7);
	//遍历表头
	for(var key in tableHeadMap){
		
		if(key == "id"){
			
		}
		else if(key == "地域"){
			gridHead_array.push("<td style='width: 10%; min-width:10%; max-width:10%;'><div class='jqx-column'>" + key + "</div></td>");
		}
		else if(key == "名称"){
			gridHead_array.push("<td style='width: 15%; min-width:15%; max-width:15%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
		else if(key=="主场景"){
			gridHead_array.push("<td style='width: 20%; min-width:20%; max-width:20%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
		else if(key=="页数"){
			gridHead_array.push("<td style='width: 10%; min-width:10%; max-width:10%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
		else if(key == "场数"){
			gridHead_array.push("<td style='width: 10%; min-width:10%; max-width:10%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
		else if(key == "日夜比例"){
			gridHead_array.push("<td style='width: 10%; min-width:10%; max-width:10%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
        
		else{
			gridHead_array.push("<td style='width:"+ actorColumnWidth +"%; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'><div class='jqx-column'>"+ key +"</div></td>");
		}
		
	}
	gridHead_array.push("</tr>");
	
	var gridHeadDiv = gridHead_array.join("");
	$("#scenceTableTitle").append(gridHeadDiv);
	
    var gridContentDiv = $("#ca");
	
	var table=$("<table cellpadding='0' cellspacing='0' border='0' id='scenceListGrid'></table>");
	
	table.append("<tbody id='scenceListTbody'><tbody>");
	viewNoArr = [];
	var _rowArray= [];
	
	$.each(data.result,function(locationScenceIndex,item){
		var _row = createRow(locationScenceIndex,item,data,actorCount);
		_rowArray.push(_row);
	});
	
	table.find("#scenceListTbody").append(_rowArray.join(""));
	
	
	gridContentDiv.append(table);
	
//	gridMainDiv.append(gridContentDiv);
//	$("#ca").append(gridContentDiv);
	
    
	
	//排序
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    if(!isSceneViewReadonly) {
        $("#scenceListTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
    	$("#scenceListTbody").on( "sortstop", function( event, ui ) {
    		updateViewSort();
    	});
    }
	
}


/**
 * 更新主表格排序顺序
 */
function updateViewSort(){
	if(isSceneViewReadonly) {
		return false;
	}
	
	var scenceIds = [];
	$("#scenceListGrid td.scenceId").each(function(){
		scenceIds.push($(this).find("a").attr("sid"));
	});
	scenceIds= scenceIds.join(",");
	$.ajax({
		url: '/sceneViewInfoController/updateOrder',
		type: 'post',
		data: {ids : scenceIds},
		datatype: 'json',
		success: function(response){
			if(response.success){
				/*showSuccessMessage("操作成功");*/
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//更新已配置的表格排序
function updateAlreadySceneViewSort(){
	var sceneViewId = $("#sceneViewId").val();
	var locationIds = [];
	$(".input-check").each(function(i){
		if(i==0){//排除第一个
			
		}else{
			locationIds.push($(this).attr("lid"));
		}
		
	});
	var locationId = locationIds.join(",");
	$.ajax({
		url: '/sceneViewInfoController/updateSceneViewMapOrder',
		type: 'post',
		data: {"sceneViewInfoId": sceneViewId, "locationId": locationId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				/*showSuccessMessage("操作成功");*/
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

/**
 * 重新加载表格
 * gridDiv: 要生成表格的div
 * div: 表格需要的数据
 */
function reloadMainScenceView(gridDiv,data){
	
	var table=$('#scenceListGrid');
	
	var _this = this;
	table.html('');
	table.append("<tbody id='scenceListTbody'><tbody>");
	var _rowArray = [];
	
	$.each(data.result,function(locationScenceIndex,item){
		var _row = createRow( locationScenceIndex, item,data);
		_rowArray.push(_row);
	});
	table.find("#scenceListTbody").append(_rowArray.join(""));
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#scenceListTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#scenceListTbody").on( "sortstop", function( event, ui ) {
		updateViewSort();
	});
}

/**
 * 创建堪景主表格的一行
 * @param tableIndex	表格编号
 * @param rowIndex	行编号
 * @param rowData	行数据
 * data: 表格需要的数据
 * @returns	行
 */
function createRow (tableIndex,rowData,data) {
	/*var _this = this;
	var table=$('#scenceListGrid'+tableIndex);*/
	var header_row = [];
	header_row.push("<tr style='height: 0px;'>");
	
//	var _row = $("<tr id='scenceList"+tableIndex+"row'></tr>");
	var _row = ["<tr id='scenceList"+tableIndex+"row' onclick='changeBackground(this)'>"];
	var tableHeadMap =  data.titleMap;
	var length = 0;
	var actorCount = 0;
	for(var key in tableHeadMap){
		length++;
	}
	actorCount = length - 7;
	var actorColumnWidth = divide(25,7);
	//遍历表头
	var i = 0;
	for(var key in tableHeadMap){
		i++;
		if(rowData[tableHeadMap[key]] == undefined){
			rowData[tableHeadMap[key]] = "";
		}
		
		if(key == "id"){
			i--;
		}
		else if(key == "地域"){
			
			_row.push("<td style='width: 10%; min-width:10%; max-width:10%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] +"</div></td>");//实景位置
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 10%; min-width:10%; max-width:10%;'></td>");
			}
			
		}
		else if(key == "名称"){
			_row.push("<td style='width: 15%; min-width:15%; max-width:15%;' class='scenceId'><a  sid='"+ rowData[tableHeadMap["id"]] +"' href='javascript:modifyScence(\""+ rowData[tableHeadMap["id"]] +"\");'>"+ rowData[tableHeadMap[key]] +"</a></td>");//实景名称
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 15%; min-width:15%; max-width:15%;'></td>");
			}
		}
		else if(key=="主场景"){
			_row.push("<td style='width: 20%; min-width:20%; max-width:20%;'><div class='jqx-column'><div class='text-content' title='"+ rowData[tableHeadMap[key]] +"'>"+ rowData[tableHeadMap[key]] + "</div></div></td>");//主场景名称
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 20%; min-width:20%; max-width:20%;'></td>");
			}
		}
		else if(key=="页数"){
			if(rowData[tableHeadMap[key]] != ""){
				_row.push("<td style='width: 11%; min-width:11%; max-width:11%;'><div class='jqx-column'>"+ parseFloat(rowData[tableHeadMap[key]]).toFixed(1) + "</div></td>");//页数
			}else{
				_row.push("<td style='width: 11%; min-width:11%; max-width:11%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");
			}
			
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 11%; min-width:11%; max-width:11%;'></td>");
			}
		}
		else if(key == "场数"){
			_row.push("<td style='width: 11%; min-width:11%; max-width:11%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//场数
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 11%; min-width:11%; max-width:11%;'></td>");
			}
		}
		else if(key == "日夜比例"){
			_row.push("<td style='width: 11%; min-width:11%; max-width:11%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//日夜比例
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 11%; min-width:11%; max-width:11%;'></td>");
			}
		}
		
		else{
			_row.push("<td style='width:"+ actorColumnWidth+"%; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//主演
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width:"+ actorColumnWidth+"%; ; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'></td>");
			}
		}
		
	}
	
//	if(tableIndex == 0){
//		header_row.push("</tr>");
//		
//		$("#scenceTableTitle").append(header_row.join(""));
//	}
	_row.push("</tr>");
	return _row;
}


/**
 * 生成堪景表格的工具栏
 * @param toolbar 工具栏jquery对象
 *
 *
 */
function loadSearchViewGrid(toolbar){
	
	var container = toolbar;
    //生成头部查询条件
    var html= [];
    html.push("<div class='toolbar'>");
    html.push("<input type='button' class='add-scence-btn' id='addScenceBtn' onclick='addScenceBtn()' value='添加' title='添加'>");
    html.push("</div>");
	
    toolbar.append(container);
    container.append(html.join(""));
}





/**
 * girdDiv:要生成表格的div
 * */


//加载已配置场景列表
function loadAlreadySetScenceTable(gridDiv, data){
	
	gridDiv.append("<div id='alreadySetToolbar' class='toolbar'></div>");
	loadToolbarView(gridDiv.find("#alreadySetToolbar"));
	
	var gridMainDiv = $("<div class='t_i alread-table'></div>");
	var gridHead_array = ["<div class='t_i_h' id='alreadyTableHeader'><div class='ee'><table class='notice-scence-table' id='alreadyScenceTableTitle' cellspacing=0 cellpadding=0>"];
	gridHead_array.push("<tr>");
	var tableHeadMap =  data.titleMap;
	var length = 0;
	var actorCount = 0;
	for(var key in tableHeadMap){
		length++;
	}
	actorCount = length - 5;
	var actorColumnWidth;
	if(actorCount <= 3){
		actorColumnWidth= divide(41,actorCount);
	}else{
		actorColumnWidth= divide(55,actorCount);
	}
	
	for(var key in tableHeadMap){
		if(key == "id"){
			gridHead_array.push("<td style='width: 3%; min-width:3%; max-width:3%;'><input class='input-check' type='checkbox' id='selectAll'></td>");
		}
		else if(key == "主场景"){
			gridHead_array.push("<td style='width: 26%; min-width:26%; max-width:26%;'><div class='jqx-column'>"+ key +"</td>");
		}
		else if(key == "时间"){
			/*gridHead_array.push("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ key +"</td>");*/
		}

		else if(key == "场/页数"){
			if(actorCount <= 3){
				gridHead_array.push("<td style='width: 15%; min-width:15%; max-width:15%;'><div class='jqx-column'>"+ key +"</td>");
			}else{
				gridHead_array.push("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ key +"</td>");
			}
			
		}
		else if(key == "日夜比例"){
			if(actorCount <= 3){
				gridHead_array.push("<td style='width: 15%; min-width:15%; max-width:15%;'><div class='jqx-column'>"+ key +"</td>");
			}else{
				gridHead_array.push("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ key +"</td>");
			}
			
		}
		
		else{
			gridHead_array.push("<td style='width:"+ actorColumnWidth +"%; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'><div class='jqx-column'>"+ key +"</div></td>");
		}

	}
	gridHead_array.push("</tr>");
	gridHead_array.push("</table></div></div>");
	
	var gridHeadDiv = $(gridHead_array.join(""));
	gridMainDiv.append(gridHeadDiv);
	gridDiv.append(gridMainDiv);
	
	
    var gridContentDiv = $("<div class='auto-height cc already-table-con' id='alreadyScenceDiv'></div>");
	
	var table=$("<table cellpadding='0' cellspacing='0' border='0' id='alreadyScenceListGrid'></table>");
	
	table.append("<tbody id='alreadyScenceListTbody'><tbody>");
	viewNoArr = [];
	$.each(data.result,function(locationScenceIndex,item){
		var _row = createAlreadyGridRow(locationScenceIndex,item,data,actorCount);
		table.find("#alreadyScenceListTbody").append(_row);
	});
	gridContentDiv.append(table);
	
	gridMainDiv.append(gridContentDiv);
	
	
	//排序
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#alreadyScenceListTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#alreadyScenceListTbody").on( "sortstop", function( event, ui ) {
		updateAlreadySceneViewSort();
	});
	
	//全选
	$("#selectAll").on("click", function(ev){
		var checkboxLength = 0;
		$("#alreadyScenceListGrid :checkbox").each(function(){
			checkboxLength ++;
		});
		if(checkboxLength == 0){
			//如果可勾选的已配置场景为零，全选按钮不可选；
			ev.preventDefault();
		}else{
			$(this).attr("disabled", false);
			if($(this).is(":checked")){
				$(".input-check").each(function(){
					$(this).prop("checked", true);
				});
			}else{
				$(".input-check").each(function(){
					$(this).prop("checked", false);
				});
			}
		}
		
	});
	
}


/**
 * 重新加载表格
 * gridDiv: 要生成表格的div
 * data: 表格需要的数据
 */
function reloadAlreadyView(gridDiv,data){
	//showSuccessMessage("正在加载...");
	
	gridDiv.find(".toolbar").html('');
	loadToolbarView(gridDiv.find(".toolbar"));
	
	var table=$('#alreadyScenceListGrid');
	
	var _this = this;
	table.html('');
	table.append("<tbody id='alreadyScenceListTbody'><tbody>");
	
	
	$.each(data.result,function(locationScenceIndex,item){
		var _row = createAlreadyGridRow( locationScenceIndex, item,data,actorCount);
		table.find("#alreadyScenceListTbody").append(_row);
	});
	var fixHelper = function(e, ui) {  
        //console.log(ui)   
        ui.children().each(function() {  
            $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
        });  
        return ui;
    };
    $("#alreadyScenceListTbody").sortable({                //这里是talbe tbody，绑定 了sortable   
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
	$("#alreadyScenceListTbody").on( "sortstop", function( event, ui ) {
		updateAlreadySceneViewSort();
	});
}

/**
 * 创建已配置表格的一行
 * @param tableIndex	表格编号
 * @param rowIndex	行编号
 * @param rowData	行数据
 * data: 表格需要的数据
 * actorCount: 主演的个数(可有可无)
 * @returns	行
 */
function createAlreadyGridRow (tableIndex,rowData,data,actorCount) {
	/*var _this = this;
	var table=$('#scenceListGrid'+tableIndex);*/
	
	var header_row = [];
	header_row.push("<tr style='height: 0px;'>");
	
	var _row = $("<tr id='alreadyScenceList"+tableIndex+"row'></tr>");
	
	var tableHeadMap =  data.titleMap;
	var length = 0;
	var actorCount = 0;
	for(var key in tableHeadMap){
		length++;
	}
	actorCount = length - 5;
	var actorColumnWidth;
	if(actorCount <= 3){
		actorColumnWidth= divide(41,actorCount);
	}else{
		actorColumnWidth= divide(55,actorCount);
	}
	
	//遍历表头
	var i = 0;
	for(var key in tableHeadMap){
		i++;
		if(rowData[tableHeadMap[key]] == undefined){
			rowData[tableHeadMap[key]] = "";
		}
		
		if(key == "id"){
			/*i--;*/
			_row.append("<td style='width: 3%; min-width:3%; max-width:3%;'><input class='input-check' type='checkbox' lid='"+ rowData.locationid +"' onclick='isCheckAll()'></td>");
			header_row.push("<td style='height: 0px; width: 3%; min-width:3%; max-width:3%;'></td>");
		}
		else if(key=="主场景"){
			_row.append("<td style='width: 26%; min-width:26%; max-width:26%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//主场景名称
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 26%; min-width:26%; max-width:26%;'></td>");
			}
		}
		else if(key == "时间"){
			
			/*_row.append("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] +"</div></td>");//实景位置
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 8%; min-width:8%; max-width:8%;'></td>");
			}*/
			
		}
		
		
		else if(key=="场/页数"){
			if(actorCount <= 3){
				_row.append("<td style='width: 15%; min-width:15%; max-width:15%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//页数
			}else{
				_row.append("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//页数
			}
			
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 8%; min-width:8%; max-width:8%;'></td>");
			}
		}
		else if(key == "日夜比例"){
			if(actorCount <= 3){
				_row.append("<td style='width: 15%; min-width:15%; max-width:15%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//日夜比例
			}else{
				_row.append("<td style='width: 8%; min-width:8%; max-width:8%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//日夜比例
			}
			
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width: 8%; min-width:8%; max-width:8%;'></td>");
			}
		}
		
		else{
			_row.append("<td style='width:"+ actorColumnWidth+"%; ; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'><div class='jqx-column'>"+ rowData[tableHeadMap[key]] + "</div></td>");//主演
			if(tableIndex == 0){
				header_row.push("<td style='height: 0px; width:"+ actorColumnWidth+"%; ; min-width:"+ actorColumnWidth +"%; max-width:"+ actorColumnWidth +"%;'></td>");
			}
		}
		
	}
	
	if(tableIndex == 0){
		header_row.push("</tr>");
		$("#alreadyScenceTableTitle").append(header_row.join(""));
	}
	
	return _row.join("");
}





//生成已配置表格工具栏
function loadToolbarView(toolbar){
	var container = toolbar;
	var html= [];
    html.push("<div class='toolbar'>");
    html.push("<div class='left-border-top'>");
    html.push("  <div class='border-left'></div>");
    html.push("  <span class='header-title'>已配置</span>");
    html.push("</div>");
    html.push("<input type='button' class='delete-scence-btn' id='deleteScenceBtn' onclick='deleteScenceBtn()' value='↓移除选中主场景'>");
    html.push("</div>");
	
    toolbar.append(container);
    container.append(html.join(""));
}



//行checkbox事件只判断是否全选
function isCheckAll(){
	
	var _tableObj = $("#alreadyScenceListGrid");
	var checkboxs = _tableObj.find(":checkbox");
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#selectAll").prop("checked",false);
	}else{
		$("#selectAll").prop("checked",true);
	}
}


function changeBackground(own){
	var $this = $(own);
	$this.siblings("tr").removeClass("background-color-tr");
	$this.addClass("background-color-tr");
}