$(function(){
	//初始化角色表
	initRoleGrid();
	if(isReadonly){
		$(".add-row-btn").hide();
	}
});

function showRoleGrid(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".public-arteffect").hide();
	$(".role-grid-div").show();
}
function showViewGrid(own){
	var $this = $(own);
	$this.siblings("li").removeClass("tab_li_current");
	$this.addClass("tab_li_current");
	$(".public-arteffect").hide();
	$(".view-grid-div").show();
	initViewGrid();
}


//初始化角色表
function initRoleGrid(){
	$.ajax({
		url: '/prepareController/queryPrepareArteffectRoleInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			var data = response.result;
			var html = [];
			html.push('<table class="role-content-table" id="roleContentTable" cellpadding="0" cellspacing="0">');
			if(data&&data.length>0){
				console.log(data);
				for(var i= 0,le = data.length; i< le; i++){
					html.push('<tr  onmouseover="showDeleteBtn(this)" onmouseout="hideDeleteBtn(this)">');
					html.push('<td style="width: 16%; min-width: 16%; max-width: 16%;" >');
					html.push('<input class="role-name" type="text" value="'+data[i].role+'" onblur="saveInfo(this);">');
					html.push('<span class="delete-row-span"><input class="delete-row-btn" id='+data[i].id+' onclick="delInfo(this);" type="button"></span>');
					html.push('</td>');
				    html.push('<td style="width: 16%; min-width: 16%; max-width: 16%;" >');
				    html.push('<input type="text" value="'+data[i].modelling+'" onblur="saveInfo(this);">');
				    html.push('</td>');
				    if(data[i].confirmDate == null){
				    	data[i].confirmDate = "";
				    }
				    html.push('<td style="width: 14%; min-width: 14%; max-width: 14%;" >');
				    html.push('<input type="text" value="'+data[i].confirmDate+'" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveInfo(dp.el)} })" >');
				    html.push('</td>');
				    html.push('<td style="width: 14%; min-width: 14%; max-width: 14%;" >');
				    html.push('<input type="text" value="'+data[i].status+'" onblur="saveInfo(this);">');
				    html.push('</td>');
				    html.push('<td style="width: 20%; min-width:20%; max-width: 20%;" >');
				    html.push('<input type="text" style="text-align: left; padding-left:3px; box-sizing: border-box;" value="'+data[i].mark+'" onblur="saveInfo(this);">');
				    html.push('</td>');
				    html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;" >');
				    html.push('<input type="text" value="'+data[i].reviewer+'" onblur="saveInfo(this);">');
				    html.push('</td>');
				    html.push('</tr>');
				}
				
			    
			}else{
				html.push('<tr class="blank-tr">');
				html.push('<td colspan= "6" style="text-align: center; vertical-align: center;">暂无数据</td>');
			}
			html.push('</table>');
		    $("#roleGridContent").empty();
		    $("#roleGridContent").append(html.join(""));
		    
		    if(isReadonly){
		    	$("#roleContentTable tr").find("input[type=text]").attr("disabled", true);
		    }
		}});
	
}
function delInfo(obj){
	
	var id = $(obj).attr('id');
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		$.ajax({
			url: '/prepareController/delPrepareArteffectRoleInfo',
			type: 'post',
			datatype: 'json',
			data:{"id":id},
			async:false,
			success: function(response){
				parent.showSuccessMessage("删除成功");
				initRoleGrid();
			}
		});
	});
	
}


function showDeleteBtn(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id = $this.find(".delete-row-btn").attr("id");
	if(id != undefined && id != "" && id != null && id != "blank"){
		$this.find("span.delete-row-span").show();
	}
	
}
function hideDeleteBtn(own){
	var $this = $(own);
	$this.find("span.delete-row-span").hide();
}
//添加角色
function addRow(){
	var html ='<tr onmouseover="showDeleteBtn(this)" onmouseout="hideDeleteBtn(this)">';
	html += '<td style="width: 16%; min-width: 16%; max-width: 16%;"  >';
	html += '<input class="role-name" type="text" onblur="saveInfo(this)"><span class="delete-row-span"><input id="blank" class="delete-row-btn" type="button" onclick="delInfo(this);"></span>';
	html += '</td>';
	html += '<td style="width: 16%; min-width: 16%; max-width: 16%;"><input type="text" onblur="saveInfo(this)"></td>';
	html += '<td style="width: 14%; min-width: 14%; max-width: 14%;"><input type="text" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveInfo(dp.el)} })"></td>';
	html += '<td style="width: 14%; min-width: 14%; max-width: 14%;"><input type="text" onblur="saveInfo(this)"></td>';
	html += '<td style="width: 20%; min-width:20%; max-width: 20%;"><input style="text-align: left; padding-left:3px; box-sizing: border-box;" type="text" onblur="saveInfo(this)"></td>';
	html += '<td style="width: 20%; min-width: 20%; max-width: 20%;"><input type="text" onblur="saveInfo(this)"></td>';
	html += '</tr>';
	var blankTrLength = 0;
	$("#roleContentTable .blank-tr").each(function(){
		blankTrLength ++;
	});
	if(blankTrLength > 0){
		$("#roleContentTable").empty();
	}
	$("#roleContentTable").append(html);
	$("#roleContentTable").find("tr:last-child").find("input[type=text]").eq(0).focus();
}


function saveInfo(obj){
	var role = $(obj).parent("td").parent("tr").find("input[type=text]").eq(0).val();
	var modelling = $(obj).parent("td").parent("tr").find("input[type=text]").eq(1).val();
	var confirmDate = $(obj).parent("td").parent("tr").find("input[type=text]").eq(2).val();
	var status = $(obj).parent("td").parent("tr").find("input[type=text]").eq(3).val();
	var mark = $(obj).parent("td").parent("tr").find("input[type=text]").eq(4).val();
	var reviewer = $(obj).parent("td").parent("tr").find("input[type=text]").eq(5).val();
	var id = $(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id');
	if(role == "" && modelling == "" && confirmDate == "" && status == "" && mark == "" && reviewer == ""){
		return;
	}
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareArteffectRoleInfo',
		type: 'post',
		datatype: 'json',
		data:{"role":role,"modelling":modelling,"confirmDate":confirmDate,"status":status,"mark":mark,"reviewer":reviewer,"id":id},
		success: function(response){
			$(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id',response.id);
		}
	});
}



//初始化场景表
function initViewGrid(){
	
	$.ajax({
		url: '/prepareController/queryPrepareArteffectLocationInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			var data = response.result;
			console.log(data);
			var html = [];
			html.push('<table class="view-content-table" id="viewContentTable" cellpadding="0" cellspacing="0" border="0">');
			if(data.length != 0){
				for(var i= 0,le=data.length; i< le; i++){
					html.push('<tr onmouseover="showDeleteBtn(this)" onmouseout="hideDeleteBtn(this)">');
					html.push('<td style="width: 16%; min-width: 16%; max-width: 16%;">');
					html.push('<input class="view-name" type="text" value="' + data[i].location + '" onblur="saveLocationInfo(this);">');
					html.push('<span class="delete-row-span"><input class="delete-row-btn" id="' + data[i].id + '" onclick="delLocationInfo(this);" type="button"></span>');
					html.push('</td>');
				    html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;">');
				    html.push('<input type="text" value="' + data[i].designSketch + '" onblur="saveLocationInfo(this);">');
				    html.push('</td>');
				    if(data[i].designSketchDate == null){
				    	data[i].designSketchDate = "";
				    }
				    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;">');
				    html.push('<input type="text" value="' + data[i].designSketchDate + '" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })">');
				    html.push('</td>');
				    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;">');
				    html.push('<input type="text" value="' + data[i].workDraw + '" onblur="saveLocationInfo(this);">');
				    html.push('</td>');
				    if(data[i].workDrawDate == null){
				    	data[i].workDrawDate = "";
				    }
				    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;">');
				    html.push('<input type="text" value="' + data[i].workDrawDate + '" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })">');
				    html.push('</td>');
				    html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;">');
				    html.push('<input type="text" value="' + data[i].scenery + '" onblur="saveLocationInfo(this);">');
				    html.push('</td>');
				    if(data[i].sceneryDate == null){
				    	data[i].sceneryDate = "";
				    }
				    html.push('<td style="width: 10%; min-width: 10%; max-width: 10%;">');
				    html.push('<input type="text" value="' + data[i].sceneryDate + '" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })">');
				    html.push('</td>');
				    html.push('<td style="width: 8%; min-width: 8%; max-width: 8%;">');
				    html.push('<input type="text" value="' + data[i].reviewer + '" onblur="saveLocationInfo(this);">');
				    html.push('</td>');
				    html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
				    html.push('<input type="text" style="text-align: left; box-sizing: border-box; padding-left: 3px;" value="' + data[i].opinion + '" onblur="saveLocationInfo(this);">');
				    html.push('</td>');
				    html.push('</tr>');
				}
			}else{
				html.push('<tr class="blank-tr">');
				html.push('<td colspan="9" style="text-align: cetner; vertical-align: cetner;">暂无数据</td>');
				html.push('</tr>');
			}
			
			
		    html.push('</table>');
		    $("#viewGridContent").empty();
		    $("#viewGridContent").append(html.join(""));
		    
		    if(isReadonly){
		    	$("#viewContentTable tr").find("input[type=text]").attr("disabled", true);
		    }
		}
	});
	
	
	
}

function delLocationInfo(obj){
	var id = $(obj).attr('id');
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		$.ajax({
			url: '/prepareController/delPrepareArteffectLocationInfo',
			type: 'post',
			datatype: 'json',
			data:{"id":id},
			async:false,
			success: function(response){
				parent.showSuccessMessage("删除成功");
				initViewGrid();
			}
		});
	});
	
	
}
function saveLocationInfo(obj){
	
	var location = $(obj).parent("td").parent("tr").find("input[type=text]").eq(0).val();
	var designSketch = $(obj).parent("td").parent("tr").find("input[type=text]").eq(1).val();
	var designSketchDate = $(obj).parent("td").parent("tr").find("input[type=text]").eq(2).val();
	var workDraw = $(obj).parent("td").parent("tr").find("input[type=text]").eq(3).val();
	var workDrawDate = $(obj).parent("td").parent("tr").find("input[type=text]").eq(4).val();
	var scenery = $(obj).parent("td").parent("tr").find("input[type=text]").eq(5).val();
	var sceneryDate = $(obj).parent("td").parent("tr").find("input[type=text]").eq(6).val();
	var reviewer = $(obj).parent("td").parent("tr").find("input[type=text]").eq(7).val();
	var opinion = $(obj).parent("td").parent("tr").find("input[type=text]").eq(8).val();
	var id = $(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id');
	if(location == "" && designSketch == "" && designSketchDate == "" && workDraw == "" && workDrawDate == "" && scenery == "" && sceneryDate == "" && reviewer == "" && opinion == ""){
		return;
	}
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareArteffectLocationInfo',
		type: 'post',
		datatype: 'json',
		data:{
			"location":location,
			"designSketch":designSketch,
			"designSketchDate":designSketchDate,
			"workDraw":workDraw,
			"workDrawDate":workDrawDate,
			"scenery":scenery,
			"sceneryDate":sceneryDate,
			"reviewer":reviewer,
			"opinion":opinion,
			"id":id
			},
		success: function(response){
			console.log(response);
			$(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id',response.id);
		}
	});
}

function addViewRow(){
	var html ='<tr onmouseover="showDeleteBtn(this)" onmouseout="hideDeleteBtn(this)">';
	html += '<td style="width: 16%; min-width: 16%; max-width: 16%;">';
	html += '<input class="view-name" type="text" onblur="saveLocationInfo(this);"><span class="delete-row-span"><input id="blank" class="delete-row-btn" type="button" onclick="delLocationInfo(this)"></span>';
	html += '</td>';
	html += '<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" onblur="saveLocationInfo(this);"></td>';
	html += '<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })"></td>';
	html += '<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" onblur="saveLocationInfo(this);"></td>';
	html += '<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })"></td>';
	html += '<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" onblur="saveLocationInfo(this);"></td>';
	html += '<td style="width: 10%; min-width: 10%; max-width: 10%;"><input type="text" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveLocationInfo(dp.el)} })"></td>';
	html += '<td style="width: 8%; min-width: 8%; max-width: 8%;"><input type="text" onblur="saveLocationInfo(this);"></td>';
	html += '<td style="width: 20%; min-width: 20%; max-width: 20%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left: 3px;" onblur="saveLocationInfo(this);"></td>';
	html += '</tr>';
	var blankTrLength = 0;
	$("#viewContentTable .blank-tr").each(function(){
		blankTrLength ++;
	});
	if(blankTrLength > 0){
		$("#viewContentTable").empty();
	}
	$("#viewContentTable").append(html);
	$("#viewContentTable").find("tr:last-child").find("input[type=text]").eq(0).focus();
}