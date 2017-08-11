/**
 * 商业运营
 */
$(function(){
	queryOperateData();
	if(isReadonly){
		$(".add-role-btn").hide();
	}
});

//查询商业运营信息
function queryOperateData(){
	$.ajax({
		url: '/prepareController/queryPrepareOperateInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.result == ""){
					var data = [];
					buildOperateGrid(data);
					return;
				}
				buildOperateGrid(JSON.parse(response.result));
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
function checkOutNum(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
//	$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}

//生成商业运营表
function buildOperateGrid(data){
	var htmlArray = [];
	console.log(data);
	if(data.length != 0){
		for(var i= 0; i< data.length; i++){
			var html = [];
			html.push('<dl>');
			html.push('<dt id="'+ data[i].id +'" pid="'+ data[i].parentId +'" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
			html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" value="'+ data[i].operateType +'" onblur="reductionDt(this)"></span></span>');
			html.push('<span class="opera-btn-list" style="display: block; float: left;">');
			if(!isReadonly){
				html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
				html.push('<a style="display: none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
			}
			
			html.push('</span>');
			html.push('</dt>');
			var children = data[i].children;
			if(children.length != 0){
				for(var j= 0; j< children.length; j++){
					html.push('<dd id="'+ children[j].id +'" pid="'+ children[j].parentId +'" onmouseOver= "showBtnList(this)" onmouseOut = "hideBtnList(this)">');
					html.push('<ul class="tree-content-ul">');
					html.push('<li style="width: 17%; min-width:17%; max-width: 17%;"></li>');
					html.push('<li class="edit-li" style="width: 17%; min-width:17%; max-width: 17%;">');
					html.push('<span class="dd-content"><input type="text" value="'+ children[j].operateBrand +'" onblur="saveRowInfo(this)"></span>');
					html.push('<span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span>');
					html.push('</li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+ children[j].operateMode +'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: right; padding-right:3px; box-sizing: broder-box;" value="'+ children[j].operateCost +'" onkeyup="checkOutNum(this)" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+ children[j].contactName +'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+ children[j].phoneNumber +'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: left; padding-left:3px; box-sizing: broder-box;" value="'+ children[j].mark +'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+ children[j].personLiable +'" onblur="saveRowInfo(this)"></li>');
					html.push('</ul>');
					html.push('</dd>');
				}
			}
			
			html.push('</dl>');
			htmlArray.push(html.join(""));
		}
	}else{
		var html = [];
		//暂无数据
		html.push('<dl class="blank-dl">');
		html.push('<dt style="height: 30px; line-height: 30px; text-align: center;">暂无数据</dt>');
		html.push('</dl>');
		htmlArray.push(html.join(""));
	}

	$("#operateGridContent").empty();
	$("#operateGridContent").append(htmlArray.join(""));
	if(isReadonly){
		$("#operateGridContent input[type=text]").each(function(){
			$(this).attr("disabled", true);
		});
	}
}


function showHideDelete(own){
	if(isReadonly){
		return;
	}
	
	var $this = $(own);
	var id = $this.attr("id");
	if(id != "" && id != undefined && id != "blank" && id != null){
		$this.find(".delete-row-btn").show();
	}
}
function hideDeleteBtn(own){
	var $this = $(own);
	$this.find(".delete-row-btn").hide();
}

function showBtnList(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id = $this.attr("id");
	if(id != "" && id != undefined && id != null && id != "blank"){
		$this.find("span.opera-btn-list").css("display", "inline-block");
	}
	
}
function hideBtnList(own){
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "none");
}
//展开收起
function zhaikaishouqi(own){
	var $this = $(own);
	if($this.hasClass("zhaikai")){
		$this.removeClass("zhaikai");
		$this.addClass("shouqi");
		$this.parents("dt").parent("dl").find("dd").hide();
	
	}else{
		$this.removeClass("shouqi");
		$this.addClass("zhaikai");
		$this.parents("dt").parent("dl").find("dd").show();
	}
}

//添加根节点

function addFirstNode(){
	var html = [];
	html.push('<dl>');
	html.push('<dt id="" pid="0" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
	html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" onblur="reductionDt(this)"></span></span>');

	html.push('<span class="opera-btn-list" style="display: block; float: left;">');
	html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
	html.push('<a style="display: none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</dt>');
	html.push('</dl>');
	var length = 0;
	$("dl.blank-dl").each(function(){
		length ++;
	});
	if(length > 0){
		$("#operateGridContent").empty();
	}
	$("#operateGridContent").append(html.join(""));
	$("#operateGridContent").find("dl:last-child").find("input[type=text]").focus();
}
//保存根节点
function reductionDt(own){
	var $this = $(own);
	var content = $this.val();
    var dt = $this.parents("dt");
	var id = $(own).parents('dt').attr('id');
	var pid = $(own).parents('dt').attr('pid');

	if(content == ""){
		return;
	}
	var data = {};
	data.id = id;
	data.parentId = pid;
	data.operateType = content;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareOperateInfo',
		type: 'post',
		datatype: 'json',
		data:data,
		success: function(response){
			if(response.success){
				dt.attr("id", response.id);
				dt.attr("pid", response.parentId);
			}else{
				parent.showErrorMessage(response.message);
			}
			
		}
	});
	
}

//添加二级节点
function addSecondNode(own){
	var $this = $(own);
	var parentid= $this.parents("dt").attr("id");
	var html = [];
	html.push('<dd id="" pid="' + parentid+ '" onmouseOver= "showBtnList(this)" onmouseOut = "hideBtnList(this)">');
	html.push('<ul class="tree-content-ul">');
	html.push('<li style="width: 17%; min-width: 17%; max-width: 17%;"></li>');
	html.push('<li class="edit-li" ondblclick="editFirstCell(this)" style="width: 17%; min-width: 17%; max-width: 17%;">');
	html.push('<span class="dd-content"><input type="text" onblur="saveRowInfo(this)"></span>');
	html.push('<span class="opera-btn-list">');
	html.push('<a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: right; box-sizing: border-box; padding-right:3px;" onkeyup="checkOutNum(this)" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('</ul>');
	html.push('</dd>');
	$this.parents("dl").append(html.join(""));
	$this.parents("dl").find("dd:last-child").find("span.dd-content").find("input[type=text]").focus();
	
}
//还原二级节点
function saveRowInfo(own){
	var $this = $(own);
	var li = $this.parents("li");
	var dd = li.parents('dd');
	var id = li.parents('dd').attr('id');
	var pid = li.parents('dd').attr('pid');
	var operateBrand = li.parent('ul').find('li.edit-li').eq(0).find('input[type=text]').val();
	var operateMode = li.parent('ul').find('li.edit-li').eq(1).find('input[type=text]').val();
	var operateCost = li.parent('ul').find('li.edit-li').eq(2).find('input[type=text]').val();
	var contactName = li.parent('ul').find('li.edit-li').eq(3).find('input[type=text]').val();
	var phoneNumber = li.parent('ul').find('li.edit-li').eq(4).find('input[type=text]').val();
	var mark = li.parent('ul').find('li.edit-li').eq(5).find('input[type=text]').val();
	var personLiable = li.parent('ul').find('li.edit-li').eq(6).find('input[type=text]').val();
	if(operateBrand == "" && operateMode == "" && operateCost == "" && contactName == "" && phoneNumber == "" && mark == "" && personLiable == ""){
		return;
	}
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.operateBrand = operateBrand;
	subData.operateMode = operateMode;
	subData.operateCost = operateCost;
	subData.contactName = contactName;
	subData.phoneNumber = phoneNumber;
	subData.mark = mark;
	subData.personLiable = personLiable;
	saveOperateInfo(dd, subData);
}




function saveOperateInfo(obj, data){
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareOperateInfo',
		type: 'post',
		datatype: 'json',
		data:data,
		success: function(response){
			if(response.success){
				obj.attr("id", response.id);
			}else{
				parent.showErrorMessage(response.message);
			}
			
		}
	});
}
//删除子节点
function deleteRow(own){
	var id = $(own).parents('dd').attr('id');
	if(id){
		parent.popupPromptBox("提示","确定要删除吗？", function (){
			delOperateInfo(id);
		});
		
	}else{
		parent.popupPromptBox("提示","确定要删除吗？", function (){
			parent.showSuccessMessage("删除成功");
			queryOperateData();
		});
		
	}
}
//删除根节点
function deleteNode(own){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var id = $(own).parents('dt').attr('id');
		var ids = [];
		ids.push(id);
		var child = $(own).parents("dt").parent("dl").find("dd");
		$.each(child, function(){
			var parentId = $(this).attr("pid");
			if(parentId == id){
				var childId = $(this).attr("id");
				ids.push(childId);
			}
		});
		if(ids.length != 0){
			delOperateInfo(ids.join(","));
		}else{
			parent.showSuccessMessage("删除成功");
			queryOperateData();
		}
	});
	
}
//删除方法
function delOperateInfo(id){
	$.ajax({
		url: '/prepareController/delPrepareOperateInfo',
		type: 'post',
		datatype: 'json',
		data:{"id":id},
		success: function(response){
			parent.showSuccessMessage("删除成功");
			queryOperateData();
		}
	});
}

