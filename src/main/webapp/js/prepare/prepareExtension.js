/**
 * 宣传进度
 */
$(function(){
	initExtensionGrid();
	if(isReadonly){
		$(".add-row-btn").hide();
	}
});

//初始化宣传进度表
function initExtensionGrid(){
	$.ajax({
		url: '/prepareController/queryPrepareExtensionInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			var data = response.result;
			var html = [];
			html.push('<table class="extension-content-table" id="extensionConTable" cellpadding="0" cellspacing="0">');
            if(data.length != 0){
            	for(var i = 0,le = data.length; i< le; i++){
    				html.push('<tr onmouseover="showBtnList(this)" onmouseOut = "hideBtnList(this)">');
    				html.push('<td style="width: 25%; min-width: 25%; max-width: 25%;">');
    				html.push('<input class="type-name" onblur="saveInfo(this)" type="text" value="'+ data[i].type+'">');
    				html.push('<span class="delete-row-span"><input class="delete-row-btn" type="button" id='+data[i].id+' onclick="deleteRow(this)"></span>');
    				html.push('</td>');
    				html.push('<td style="width: 35%; min-width: 35%; max-width: 35%;">');
    				html.push('<input type="text" style="text-align: left; padding-left: 3px; box-sizing: border-box;"  onblur="saveInfo(this)" value="'+data[i].material+'">');
    				html.push('</td>');
    				html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
    				html.push('<input type="text" onblur="saveInfo(this)" value="'+data[i].personLiable+'">');
    				html.push('</td>');
    				html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
    				html.push('<input type="text"onblur="saveInfo(this)"  value="'+data[i].reviewer+'">');
    				html.push('</tr>');
    			}
			}else{
				html.push('<tr class="blank-tr">');
				html.push('<td colspan="4" style="text-align: center;">暂无数据</td>');
				html.push('</tr>');
			}
			html.push('</table>');
			$("#extensionConDiv").empty();
			$("#extensionConDiv").append(html.join(""));
			if(isReadonly){
				$("#extensionConTable tr").find("input[type=text]").attr("disabled", true);
			}
		}
	});
	
	
	
	
}

//显示删除按钮
function showBtnList(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id = $this.find(".delete-row-btn").attr("id");
	if(id != undefined && id != "" && id != null && id != "blank"){
		$this.find("span.delete-row-span").show();
	}
	
}
//隐藏删除按钮
function hideBtnList(own){
	var $this = $(own);
	$this.find("span.delete-row-span").hide();
}


//添加一行
function addRow() {
	var blankLength = 0;
	$("#extensionConTable tr.blank-tr").each(function(){
		blankLength ++;
	});
	var html = [];
	html.push('<tr onmouseover="showBtnList(this)" onmouseOut = "hideBtnList(this)">');
	html.push('<td style="width: 25%; min-width: 25%; max-width: 25%;">');
	html.push('<input class="type-name" type="text" onblur="saveInfo(this)" value="">');
	html.push('<span class="delete-row-span"><input class="delete-row-btn" type="button" id="blank" onclick="deleteRow(this)"></span>');
	html.push('</td>');
	html.push('<td style="width: 35%; min-width: 35%; max-width: 35%;">');
	html.push('<input type="text" style="text-align: left; padding-left: 3px; box-sizing: border-box;" onblur="saveInfo(this)" value="">');
	html.push('</td>');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;"><input type="text" onblur="saveInfo(this)" value=""></td>');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;"><input type="text" onblur="saveInfo(this)" value=""></td>');
	html.push('</tr>');
	if(blankLength > 0){
		$("#extensionConTable").empty();
	}
	$("#extensionConTable").append(html.join(""));
	$("#extensionConTable").find("tr:last-child").find("input[type=text]").eq(0).focus();
}

function saveInfo(obj){
	var type = $(obj).parent("td").parent("tr").find("input[type=text]").eq(0).val();
	var material = $(obj).parent("td").parent("tr").find("input[type=text]").eq(1).val();
	var personLiable = $(obj).parent("td").parent("tr").find("input[type=text]").eq(2).val();
	var reviewer = $(obj).parent("td").parent("tr").find("input[type=text]").eq(3).val();
	var id = $(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id');
	if(type == "" && material == "" && personLiable == "" && reviewer == ""){
		return;
	}
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareExtensionInfo',
		type: 'post',
		datatype: 'json',
		data:{"type":type,"material":material,"personLiable":personLiable,"reviewer":reviewer,"id":id},
		success: function(response){
			$(obj).parent("td").parent("tr").find("input[type=button]").eq(0).attr('id',response.id);
		}
	});
}
//删除一行
function deleteRow(own){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var $this = $(own);
		$this.parents("td").parent("tr").remove();
		var id = $this.attr('id');
		$.ajax({
			url: '/prepareController/delPrepareExtensionInfo',
			type: 'post',
			datatype: 'json',
			async:false,
			data:{'id':id},
			success: function(response){
				parent.showSuccessMessage("删除成功");
				$this.parents('tr').remove();
				initExtensionGrid();
			}
		});
	});
	
}