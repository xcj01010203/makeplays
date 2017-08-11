$(function(){
	queryCrewerGrid();
	if(isReadonly){
		$(".add-role-btn").hide();
	}
});
var subData = {};
//查询剧组人员表格
function queryCrewerGrid(){
	$.ajax({
		url: '/prepareController/queryPrepareCrewPeopleInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				//生成表格
				if(response.result == ""){
					var data = [];
					buildCrewerGrid(data);
					return;
				}
				buildCrewerGrid(JSON.parse(response.result));
			}else{
				parent.showErrorMessage(response.message);
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

//生成表格
function buildCrewerGrid(data){
	var htmlArray = [];
	if(data.length != 0){
		for(var i= 0; i< data.length; i++){
			var html = [];
			html.push('<dl>');
			html.push('<dt id="'+ data[i].id +'" pid="'+ data[i].parentId +'" onmouseover="showHideDelete(this)" onmouseout="hideDeleteBtn(this)">');
			html.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span"><input type="text" value = "'+ data[i].groupName +'" onblur="reductionDt(this)"></span></span>');
			html.push('<span class="opera-btn-list" style="display: block; width: 45px;">');
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
					html.push('<li style="width: 17%; min-width:17%; max-width: 17%;"></li>');
					html.push('<li class="edit-li" style="width: 17%; min-width:17%; max-width: 17%;">');
					html.push('<span class="dd-content"><input type="text" value="'+ secondChildren[k].duties +'" onblur="saveRowInfo(this)"></span>');
					html.push('<span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span>');
					html.push('</li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+secondChildren[k].name+'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+secondChildren[k].phone+'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+secondChildren[k].reviewer+'" onblur="saveRowInfo(this)"></li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+secondChildren[k].confirmDate+'" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: center;" value="'+secondChildren[k].arrivalTime+'" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
					html.push('<li class="edit-li" style="width: 11%; min-width:11%; max-width: 11%;"><input type="text" style="text-align: right; box-sizing: border-box; padding-right: 3px;" value="'+secondChildren[k].payment+'" onkeyup="checkOutNum(this)" onblur="saveRowInfo(this)"></li>');
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
	$("#crewerGridContent").empty();
	$("#crewerGridContent").append(htmlArray.join(""));
	if(isReadonly){
		$("#crewerGridContent input[type=text]").each(function(){
			$(this).attr("disabled", true);
		});
	}
}


function showHideDelete(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	if ($this.attr("id") != null && $this.attr("id") != '' && $this.attr("id") != "blank" && $this.attr("id") != undefined) {
		$this.find(".delete-row-btn").css("display", "inline-block");
	}
}
function hideDeleteBtn(own){
	var $this = $(own);
	$this.find(".delete-row-btn").css("display", "none");
}

function showBtnList(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	if ($this.attr("id") != null && $this.attr("id") != '' && $this.attr("id") != "blank" && $this.attr("id") != undefined) {
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
	html.push('<span class="opera-btn-list" style="display: block; width: 45px;">');
	html.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addSecondNode(this)" title="添加"></a>');
	html.push('<a style="display:none;" class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</dt>');
	html.push('</dl>');
	var length = 0;
	$("dl.blank-dl").each(function(){
		length ++;
	});
	if(length > 0){
		$("#crewerGridContent").empty();
	}
	$("#crewerGridContent").append(html.join(""));
	$("#crewerGridContent").find("dl:last-child").find("span.dt-span").find("input[type=text]").focus();
}
//还原根节点
function reductionDt(own){
	var $this = $(own);
	var content = $this.val();
	var dt = $(own).parents('dt');
	var id = dt.attr('id');
	var pid = dt.attr('pid');
	
	if(content == ""){
		return;
	}
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.groupName = content;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareCrewPeopleInfo',
		type: 'post',
		datatype: 'json',
		data:subData,
		success: function(response){
			dt.attr('id',response.id);
			dt.attr('pid',response.parentId);
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
	html.push('<li class="edit-li" style="width: 17%; min-width: 17%; max-width: 17%;">');
	html.push('<span class="dd-content"><input type="text" onblur="saveRowInfo(this)"></span>');
	html.push('<span class="opera-btn-list">');
	html.push('<a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a>');
	html.push('</span>');
	html.push('</li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: center;" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
	html.push('<li class="edit-li" style="width: 11%; min-width: 11%; max-width: 11%;"><input type="text" style="text-align: right; box-sizing: border-box; padding-right: 3px;" onkeyup="checkOutNum(this)" onblur="saveRowInfo(this)"></li>');
	html.push('</ul>');
	html.push('</dd>');
	$this.parents("dl").append(html.join(""));
	$this.parents("dl").find("dd:last-child").find("span.dd-content").find("input[type=text]").focus();
	
}
//保存
function saveRowInfo(own){
	var $this = $(own);
	var li = $this.parents("li");
	
	var id = li.parents('dd').attr('id');
	var pid = li.parents('dd').attr('pid');
	var duties = li.parent('ul').find('li.edit-li').eq(0).find('span.dd-content').find("input[type=text]").val();
	var name = li.parent('ul').find('li.edit-li').eq(1).find("input[type=text]").val();
	var phone = li.parent('ul').find('li.edit-li').eq(2).find("input[type=text]").val();
	var reviewer = li.parent('ul').find('li.edit-li').eq(3).find("input[type=text]").val();
	var confirmDate = li.parent('ul').find('li.edit-li').eq(4).find("input[type=text]").val();
	var arrivalTime = li.parent('ul').find('li.edit-li').eq(5).find("input[type=text]").val();
	var payment = li.parent('ul').find('li.edit-li').eq(6).find("input[type=text]").val();
	if(duties == "" && name == "" && phone == "" && reviewer == "" && confirmDate == "" && arrivalTime == "" && payment == ""){
		return;
	} 
	subData = {};
	subData.id = id;
	subData.parentId = pid;
	subData.duties = duties;
	subData.name = name;
	subData.phone = phone;
	subData.reviewer = reviewer;
	subData.confirmDate = confirmDate;
	subData.arrivalTime = arrivalTime;
	subData.payment = payment;
	$.ajax({
		url: '/prepareController/saveOrUpdatePrepareCrewPeopleInfo',
		type: 'post',
		datatype: 'json',
		data:subData,
		success: function(response){
			li.parents('dd').attr('id',response.id);
			li.parents('dd').attr('pid',response.parentId);
		}
	});
}





function deleteRow(own){
	var id = $(own).parents('dd').attr('id');
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		if(id){
			delCrewPeopleInfo(id);
			$(own).parents('dd').remove();
		}else{
			parent.showSuccessMessage("删除成功");
			queryCrewerGrid();
		}
	});
	
}

function deleteNode(own){
	var id = $(own).parents('dt').attr('id');
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		if(id == undefined || id == "" || id == "blank"){
			parent.showSuccessMessage("删除成功");
			queryCrewerGrid();
			return;
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
		if(ids.length != 0){
			delCrewPeopleInfo(ids.join(","));
		}
	});
	
}
function delCrewPeopleInfo(id){
	$.ajax({
		url: '/prepareController/delPrepareCrewPeopleInfo',
		type: 'post',
		datatype: 'json',
		data:{"id":id},
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("删除成功");
				queryCrewerGrid();
			}else{
				parent.showErrorMessage(response.message);
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
