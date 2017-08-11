/**
 * 选角进度
 */
$(function(){
	
	//mosaicGrid();
	buildRoleProgress();
	if(isReadonly){
		$(".add-role-btn").hide();
	}
});
var subData ={};
//生成选角进度表
function buildRoleProgress(){
	$.ajax({
		url: '/prepareController/queryPrepareRoleInfo',
		type: 'post',
		data: '',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.result == ""){
					var result = [];
					mosaicGrid(result);
				}else{
					mosaicGrid(JSON.parse(response.result));
				}
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//拼接表格数据
function mosaicGrid(data){
	console.log(data);
	var html = [];
	if(data.length != 0){
		for(var k= 0,le = data.length; k< le; k++){
			html.push('<dl>');
				html.push('<dt id="'+data[k].id+'" pid="'+data[k].parentId+'" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
				html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" value="'+ data[k].role +'" onblur="reductionDt(this)"></span></span>');
				html.push('<span class="opera-btn-list" style="display: block; float:left;">');
				if(!isReadonly){
					html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
					html.push('<a style="display: none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
				}
				html.push('</span>');
				html.push('</dt>');
				var children = data[k].children;
				for(var j= 0,lec = children.length; j< lec; j++){
					html.push('<dd id="'+children[j].id+'" pid="'+children[j].parentId+'" onmouseOver= "showBtnList(this)" onmouseOut = "hideBtnList(this)">');
					html.push('<ul class="tree-content-ul">');
					html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;"></li>');
					html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;">');
					html.push('<span class="dd-content"><input type="text" value="'+children[j].actor+'" onblur="saveRowInfo(this)"></span>');
					html.push('<span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span>');
					html.push('</li>');
					html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" value="'+children[j].schedule+'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li" style="width: 30%; min-width: 30%; max-width: 30%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" value="'+children[j].content+'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" value="'+children[j].mark+'" onblur="saveRowInfo(this)"></li>');
					html.push('</ul>');
					html.push('</dd>');
//					var childrenSon = children[j].children;
//					for(var m= 0,leSon = childrenSon.length; m< leSon; m++){
//						html.push('<dd id="'+ childrenSon[m].id +'" pid="'+ children[j].id +'">');
//						html.push('<ul class="tree-content-ul">');
//						html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;"></li>');
//						html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;">');
//						html.push('</li>');
//						html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align: center;" value="'+childrenSon[m].schedule+'" onblur="saveRowInfo(this)"></li>');
//						html.push('<li class="edit-li" style="width: 30%; min-width: 30%; max-width: 30%;"><input type="text" style="text-align: center;" value="'+childrenSon[m].content+'" onblur="saveRowInfo(this)"></li>');
//						html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: center;" value="'+childrenSon[m].mark+'" onblur="saveRowInfo(this)"></li>');
//						html.push('</ul>');
//						html.push('</dd>');
//					}
				}
			html.push('</dl>');
		}
	}else{
		html.push('<dl class="blank-dl">');
		html.push('<dt style="text-align: center;">暂无数据</dt>');
		html.push('</dl>');
	}
	
	$("#roleGridContent").empty();
	$("#roleGridContent").append(html.join(""));
	if(isReadonly){
		$("#roleGridContent input[type=text]").each(function(){
			$(this).attr("disabled", true);
		});
	}
}

function showHideDelete(own){
	if(isReadonly){
		return;
	}
	var id = $(own).attr("id");
	if(id != undefined && id != null && id != "" && id != "blank"){
		$(own).find(".delete-row-btn").show();
	}
}
function hideDeleteBtn(own){
	$(own).find(".delete-row-btn").hide();
}



function showBtnList(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id = $this.attr("id");
	if(id != undefined && id != null && id != "" && id != "blank"){
		$this.find("span.opera-btn-list").css("display", "inline-block");
	}
	
}
function hideBtnList(own){
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "none");
}

//添加根节点
function addFirstNode(){
	var blankLength =0;
	$("#roleGridContent dl.blank-dl").each(function(){
		blankLength ++;
	});
	var html = [];
	html.push('<dl>');
	html.push('<dt id="blank" pid="0" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
	html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" onblur="reductionDt(this)"></span></span>');
	html.push('<span class="opera-btn-list" style="display: block; float: left;">');
	html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
	html.push('<a style="display: none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</dt>');
	html.push('</dl>');
	if(blankLength > 0){
		$("#roleGridContent").empty();
	}
	$("#roleGridContent").append(html.join(""));
	$("#roleGridContent").find("dl:last-child").find("span.dt-span").find("input[type=text]").focus();
}
//保存根节点
function reductionDt(own){
	var $this = $(own);
	var content = $this.val();
	var dt = $this.parents("dt");
	
	if(content == ""){
		return;
	}
	var id = dt.attr('id');
	var pid = dt.attr('pid');
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.role = content;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareRoleInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				dt.attr("id", response.id);
				dt.attr("pid", response.parentId);
			}
		}
	});
}

//添加二级节点
function addSecondNode(own){
	var $this = $(own);
	var pid = $this.parents('dt').attr('id');
	var html = [];
	html.push('<dd id="blank" pid='+pid+' onmouseOver= "showBtnList(this)" onmouseOut = "hideBtnList(this)">');
	html.push('<ul class="tree-content-ul">');
	html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;"></li>');
	html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<span class="dd-content"><input type="text" onblur="saveRowInfo(this)"></span>');
	html.push('<span class="opera-btn-list">');
	html.push('<a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 30%; min-width: 30%; max-width: 30%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: left; box-sizing: border-box; padding-left:3px;" onblur="saveRowInfo(this)"></li>');
	html.push('</ul>');
	html.push('</dd>');
	$this.parents("dl").append(html.join(""));
	$this.parents("dl").find("dd:last-child").find("span.dd-content").find("input[type=text]").focus();
}
//还原二级节点
/*function reductionDD(own){
	var $this = $(own);
	var content = $this.val();
	var li = $this.parents("li");
	var id = li.parent('ul').parent('dd').attr('id');
	var pid = li.parent('ul').parent('dd').attr('pid');
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.actor = content;
	if(content == ""){
		return;
	}
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareRoleInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				li.parents("dd").attr("id", response.id);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}*/

/*//添加三级节点
function addthirdRow(own){
	var $this = $(own);
	var parentId = $this.parents('dd').attr('id');
	
	var html = [];
	html.push('<dd id="blank" pid='+ parentId +'>');
	html.push('<ul class="tree-content-ul">');
	html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;"></li>');
	html.push('<li style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 15%; min-width: 15%; max-width: 15%;">');
	html.push('<input type="text" style="text-align: center;" onblur="saveRowInfo(this);">');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 30%; min-width: 30%; max-width: 30%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this);"></li>');
	html.push('<li class="edit-li" style="width: 25%; min-width: 25%; max-width: 25%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this);"></li>');
	html.push('</ul>');
	html.push('</dd>');
	$this.parents("dd").after(html.join(""));
	$this.parents("dd").next("dd").find("input[type=text]").eq(0).focus();
}*/





//还原其他单元格
function saveRowInfo(own){
	var $this = $(own);
	var li = $this.parents("li");
	var id = li.parent("ul").parent("dd").attr("id");
	var pid = li.parent("ul").parent("dd").attr("pid");
	var actor = li.parent("ul").find('li.edit-li').eq(0).find("input[type=text]").val();
	var schedule = li.parent("ul").find('li.edit-li').eq(1).find("input[type=text]").val();
	var content = li.parent("ul").find('li.edit-li').eq(2).find("input[type=text]").val();
	var mark = li.parent("ul").find('li.edit-li').eq(3).find("input[type=text]").val();
	if(actor== "" && schedule == "" && content == "" && mark == ""){
		return;
	}
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.actor = actor;
	subData.schedule = schedule;
	subData.content = content;
	subData.mark = mark;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareRoleInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				li.parents("dd").attr("id", response.id);
			}
		}
	});
}
//删除一行
function deleteRow(obj){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var id  = $(obj).parents('dd').attr('id');
		/*var child = $(obj).parents("dd").parent("dl").find("dd");
		$.each(child, function(){
			var parentId = $(this).attr("pid");
			if(parentId == id){
				var childId = $(this).attr("id");
				ids.push(childId);
			}
		});*/
		if(id == undefined || id == "" || id == "blank"){
			parent.showSuccessMessage("删除成功");
			buildRoleProgress();
		}
		$.ajax({
			url: '/prepareController/delPrepareRoleInfo',
			type: 'post',
			data: {"id":id},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除成功");
					buildRoleProgress();
				}
			}
		});
	});
	
	
}

function deleteNode(own){
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		var id  = $(own).parents('dt').attr('id');
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
		$.ajax({
			url: '/prepareController/delPrepareRoleInfo',
			type: 'post',
			data: {"id":ids.join(",")},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除成功");
					buildRoleProgress();
				}
			}
		});
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