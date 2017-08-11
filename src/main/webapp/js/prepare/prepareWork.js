/**
 * 办公筹备
 */
$(function(){
	queryOfficePrepare();
	if(isReadonly){
		$(".add-role-btn").hide();
	}
});
//查询办公筹备数据
function queryOfficePrepare(){
	$.ajax({
		url: '/prepareController/queryPrepareWorkInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.result == ""){
					var data = [];
					buildOfficePrepare(data);
					return;
				}
				buildOfficePrepare(JSON.parse(response.result));
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//拼接办公筹备表格
function buildOfficePrepare(data){
	var htmlArray = [];
	if(data.length != 0){
		for(var i= 0; i< data.length; i++){
			var html = [];
			html.push('<dl>');
			html.push('<dt id="'+ data[i].id +'" pid="'+ data[i].parentId +'" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
			html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" value="'+ data[i].type +'" onblur="reductionDt(this)"></span></span>');
			html.push('<span class="opera-btn-list" style="display: block; float: left;">');
			if(!isReadonly){
				html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
				html.push('<a style="display:none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
			}
			html.push('</span>');
			html.push('</dt>');
			var secondChildren = data[i].children;
			if(secondChildren.length != 0){
				for(var k= 0; k< secondChildren.length; k++){
					html.push('<dd id="'+ secondChildren[k].id +'" pid="'+ secondChildren[k].parentId + '" onmouseOver= "showBtnList(this)" onmouseOut = "hideBtnList(this)">');
					html.push('<ul class="tree-content-ul">');
					html.push('<li style="width: 25%; min-width: 25%; max-width: 25%;"></li>');
					html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;">');
					html.push('<span class="dd-content"><input type="text" value="'+ secondChildren[k].purpose +'" onblur="saveRowInfo(this)"></span>');
					html.push('<span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span>');
					html.push('</li>');
					html.push('<li class="edit-li"  style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: left; box-sizing:border-box; padding-left:3px;" value="'+ secondChildren[k].schedule +'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li"  style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: center;" value="'+ secondChildren[k].personLiable +'" onblur="saveRowInfo(this)"></li>');
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
	$("#workerGridContent").empty();
	$("#workerGridContent").append(htmlArray.join(""));
	if(isReadonly){
		$("#workerGridContent input[type=text]").each(function(){
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
	if(id != "" && id != null && id != undefined && id != "blank"){
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
	if(id != undefined && id != "" && id != "blank" && id != null){
		$this.find("span.opera-btn-list").css("display", "inline-block");
	}
	
}
function hideBtnList(own){
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "none");
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
		$("#workerGridContent").empty();
	}
	$("#workerGridContent").append(html.join(""));
	$("#workerGridContent").find("dl:last-child").find("input[type=text]").focus();
}
//还原根节点
function reductionDt(own){
	var $this = $(own);
	var content = $this.val();
	var dt = $this.parents("dt");
	var pid =dt.attr('pid');
	var id =dt.attr('id');
	if(content == ""){
		return;
	}
	var subData = {};
	subData.id = id;
	subData.parentId =pid;
	subData.type =content;
	
	
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareWorkInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				dt.attr('pid',response.parentId);
				dt.attr('id',response.id);
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
	html.push('<li style="width: 25%; min-width: 25%; max-width: 25%;"></li>');
	html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;">');
	html.push('<span class="dd-content"><input type="text" onblur="saveRowInfo(this)"></span>');
	html.push('<span class="opera-btn-list">');
	html.push('<a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: left; box-sizing:border-box; padding-left:3px;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('</ul>');
	html.push('</dd>');
	$this.parents("dl").append(html.join(""));
	$this.parents("dl").find("dd:last-child").find("span.dd-content").find("input[type=text]").focus();
}

//还原其他单元格
function saveRowInfo(own){
	var $this = $(own);
	var li = $this.parents("li");
	var id = li.parent("ul").parent("dd").attr("id");
	var pid = li.parent("ul").parent("dd").attr("pid");
	
	var purpose = li.parent("ul").find('li.edit-li').eq(0).find('input[type=text]').val();
	var schedule = li.parent("ul").find('li.edit-li').eq(1).find('input[type=text]').val();
	var personLiable = li.parent("ul").find('li.edit-li').eq(2).find('input[type=text]').val();
	if(purpose == "" && personLiable == "" && schedule == ""){
		return;
	}
	var subData = {};
	subData.id = id;
	subData.parentId =pid;
	subData.purpose =purpose;
	subData.personLiable =personLiable;
	subData.schedule =schedule;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareWorkInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				li.parent("ul").parent("dd").attr("id",response.id);
				li.parent("ul").parent("dd").attr("pid",response.pid);
			}
		}
	});
	
	
}
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
//删除根节点
function deleteNode(own){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var id = $(own).parents('dt').attr('id');
		if(id == "" || id == undefined || id == "blank"){
			parent.showSuccessMessage("删除成功");
			queryOfficePrepare();
		}
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
		delcrewPeopleInfo(ids.join(","));
	});
	
}
//删除行
function deleteRow(own){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var id = $(own).parents('dd').attr('id');
		if(id){
			delcrewPeopleInfo(id);
		}else{
			parent.showSuccessMessage("删除成功");
			queryOfficePrepare();
		}
		
	});
}

function delcrewPeopleInfo(id){
	$.ajax({
		url: '/prepareController/delPrepareWorkInfo',
		type: 'post',
		datatype: 'json',
		data:{"id":id},
		async:false,
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("删除成功");
				queryOfficePrepare();
			}
			
		}
	});
}