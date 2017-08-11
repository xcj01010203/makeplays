$(function(){
	//查询剧本类型
	queryScriptType();
	//查询评审权重
	queryReviewWeight();
//	initHeaderTable();
	
	//查询表头,生成表
	queryGridHead();
	 //权限管理
	
	
});

//评审人数
var tableHeadPeople;

//添加评审人
function addReview(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var html = '';
	html += '<li class="reivew-people"><input type="text" placeholder="评审人">:<input type="text" placeholder="权重" onkeyup="checkOutNum(this)">%<a class="close-tag" onclick="deleteReview(this)"></a></li>';
	$this.parent("li").before(html);
}
/*//删除评审人
function deleteReview(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	$this.parent("li").remove();
}*/
var rowKey = null;
//生成剧本进度表

//查询剧本类型
function queryScriptType(){
	$.ajax({
		url: '/prepareScriptController/queryScriptTypeChecked',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var scriptTypeList = response.scriptTypeCheckedList;
				if(scriptTypeList.length != 0){
					var html = [];
					for(var i= 0; i< scriptTypeList.length; i++){
						if(scriptTypeList[i].scripttypeid != null){
							html.push('<li><label><input type="checkbox" checked name="scriptType" value="'+ scriptTypeList[i].id +'">'+ scriptTypeList[i].name +'</label></li>');
						}else{
							html.push('<li><label><input type="checkbox" name="scriptType" value="'+ scriptTypeList[i].id +'">'+ scriptTypeList[i].name +'</label></li>');
						}
						
					}
					$("#reivewPeopleUl").empty();
					$("#reivewPeopleUl").append(html.join(""));
					if(isReadonly){
						$("input[name=scriptType]").attr("disabled", true);
					}
				}
			}
		}
	});
}
//查询评审权重
function queryReviewWeight(){
	$.ajax({
		url: '/prepareScriptController/queryWeightInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var weightList = response.weightList;
				var html = [];
				if(weightList.length != 0){
					for(var i= 0; i< weightList.length; i++){
						html.push('<li class="reivew-people"><input type="text" value="'+ weightList[i].name +'">:<input type="text" value="'+ weightList[i].weight +'">%<a class="close-tag" cid="'+ weightList[i].id +'" onclick="deleteReview(this)"></a></li>');
					}
					
				}
				html.push('<li><input class="add-review-btn" type="button" onclick="addReview(this)"></li>');
				$("#reivewPeopleList").empty();
				$("#reivewPeopleList").append(html.join(""));
				if(isReadonly){
					$(".add-review-btn").hide();
					//文本框不可输入
					$("#reivewPeopleList li.reivew-people").each(function(){
						$(this).find("input[type=text]").attr("disabled", true);
					});
				}
			}
		}
	});
}

//删除权重信息
function deleteReview(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id= $this.attr("cid");
	if(id == undefined){
		$this.parents("li").remove();
	}else{
		parent.popupPromptBox("提示","确定要删除吗？", function (){
			$.ajax({
				url: '/prepareScriptController/delWeightInfo',
				type: 'post',
				data: {"id": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						$this.parents("li").remove();
						parent.showSuccessMessage("删除成功");
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});
		
	}
}

function checkOutNum(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
//	$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}

//生成进度表
function buildProgressGrid(){
	if(isReadonly){
		return;
	}
	var list = $("#reivewPeopleList").find("li.reivew-people");
	var listArray = [];
	var notComplete = false;
	var countReivew = 0;
	var scriptTypeLength = 0;
	$("input[name=scriptType]:checked").each(function(){
		scriptTypeLength ++;
	});
	if(scriptTypeLength != 0){
		if(list.length != 0){
			$.each(list, function(){
				var name = $(this).find("input[type=text]:first-child").val();
				var reivew = $(this).find("input[type=text]:nth-child(2)").val();
				var id = $(this).find("a").attr("cid");
				if(name != "" && reivew != ""){
					if(id == undefined){
						id="blank";
					}
					var data = "";
					data += id +"##";
					data += name +"##";
					data += reivew;
					listArray.push(data);
					countReivew =add(countReivew, reivew);
				}else{
					notComplete = true;
				} 
			});
			if(countReivew > 100){
				parent.showInfoMessage("权重比例不能超过百分之百");
				return;
			}
			if(notComplete){
				parent.showInfoMessage("评审权重信息填写不完整");
				return;
			}
			
			//获取剧本类型
			var ids = [];
			$("input[name=scriptType]:checked").each(function(){
				var id = $(this).val();
				ids.push(id);
			});
			if(ids.length == 0){
				parent.showInfoMessage("请选择剧本类型");
				return;
			}
			$.ajax({
				url: '/prepareScriptController/generateSchedule',
				type: 'post',
				data:{"weightInfo": listArray.join(","), "scriptTypeId": ids.join(",")},
			    datatype: 'json',
			    success: function(response){
			    	if(response.success){
			    		parent.showSuccessMessage("生成进度表成功");
			    		//重新渲染评审权重信息
			    		queryReviewWeight();
			    		//生成进度表
			    		queryGridHead();
			    	}else{
			    		parent.showErrorMessage(response.message);
			    	}
			    }
			});
			
		}else{
			parent.showInfoMessage("请填写评审权重信息");
		}
	}else{
		parent.showInfoMessage("请选择剧本类型");
	}
	
}






function showBtnList(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "inline-block");
}
function hideBtnList(own){
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "none");
}



function showDeleteBtn(own){
	if(isReadonly){
		return;
	}
	var $this = $(own);
	var id = $this.parents("dd").attr("id");
	if(id != undefined && id != "" && id != "blank" && id != null){
		$this.find("span.opera-btn-list").css("display", "inline-block");
	}
	
}
function hideDeleteBtn(own){
	var $this = $(own);
	$this.find("span.opera-btn-list").css("display", "none");
}




//保存
function saveRowInfo(own){
	var $this = $(own);
	var li = $this.parents("li");
	//拿到当前行数据，走保存接口
	var subData= {};
	var id = li.parent("ul").parent("dd").attr("id");
	var parentId = li.parent("ul").parent("dd").attr("pid");
	var edition = li.parent("ul").find("li.edit-li").eq(0).find("span.dd-content").find("input[type=text]").val();
	var finishDate = li.parent("ul").find("li.edit-li").eq(1).find("input[type=text]").val();
	var personLiable = li.parent("ul").find("li.edit-li").eq(2).find("input[type=text]").val();
	var content = li.parent("ul").find("li.edit-li").eq(3).find("input[type=text]").val();
	subData.id = id;
	subData.parentId = parentId;
	subData.edition = edition;
	subData.finishDate = finishDate;
	subData.personLiable = personLiable;
	subData.content = content;
	var i = 3;//为取得人员后面的数值
	var peopleIds = [];
	var scoreArray = [];
	for(var j= 0; j< tableHeadPeople.length; j++){
		var score= li.parent("ul").find("li.people-list").eq(j).find("input[type=text]").val();
		var peopleId = li.parent("ul").find("li.people-list").eq(j).attr("pid");//peopleId;
		if(score == ""){
			score = "blank";
		}
		peopleIds.push(peopleId);
		scoreArray.push(score);
		i++;
	}
	//总分不提交
	i+=2;
	var status = li.parent("ul").find("li.edit-li").eq(i).find("input[type=text]").val();
	subData.status = status;
	i++;
	var mark = li.parent("ul").find("li.edit-li").eq(i).find("input[type=text]").val();
	subData.mark = mark;
	subData.scriptTypeId = li.parent("ul").parent("dd").attr("tid");
	var weightInfoString = peopleIds.join(",");
	subData.weightInfoId = weightInfoString;
	var scoreString = scoreArray.join(",");
	subData.score = scoreString;
	if(edition == "" && finishDate == "" && personLiable == "" && content == "" && status == "" && mark == ""){
		return;
	}
	$.ajax({
		url: '/prepareScriptController/saveOrUpdateScriptInfo',
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
}

//计算总分
function caluScoreCoount(own){
	var $this = $(own);
	var peopleList= $this.parents("ul").find("li.people-list");
	var scoreCount = 0;
	if($this.val() == ""){
		return;
	}
	$.each(peopleList, function(i){
		var tag = i;
		var value = $(this).find("input[type=text]").val();
		$("#reivewPeopleList li.reivew-people").each(function(j){
			if(tag == j){
				var reivew = $(this).find("input[type=text]:nth-child(2)").val();
				scoreCount = add(scoreCount, multiply(value, divide(reivew, 100))); 
			}
		});
	});
	$this.parents("ul").find("li.score-input").find("input[type=text]").val(scoreCount);
	//调用保存
	saveRowInfo(own);
}


//添加一行
function addRow(own){
	var $this = $(own);
	var parentId = $this.parents("dt").attr("id");
	var scriptTypeId = $this.parents("dt").attr("tid");
	//前提得到几列数据
	var contentHtml = [];
	contentHtml.push('<dd id="" pid="'+ parentId +'" tid="'+ scriptTypeId +'">');
	contentHtml.push('<ul class="tree-content-ul"  onmouseOver="showDeleteBtn(this)" onmouseOut="hideDeleteBtn(this)">');
	contentHtml.push('<li></li>');
//	contentHtml.push('<li class="edit-li"  ondblclick="editFirstCell(this)"><span class="dd-content"></span><span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a><a class="add-row-btn" href="javascript:void(0);" onclick="addthirdRow(this)"></a></span></li>');
	contentHtml.push('<li class="edit-li"><span class="dd-content"><input type="text" onblur="saveRowInfo(this)"></span><span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span></li>');
	contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
	contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: left;" onblur="saveRowInfo(this)"></li>');
	for(var i= 0; i< tableHeadPeople.length; i++){
		contentHtml.push('<li class="edit-li people-list" pid="'+ tableHeadPeople[i].id +'"><input type="text" style="text-align: center;" onkeyup="checkOutNum(this)" onblur="caluScoreCoount(this)"></li>');
	}
	contentHtml.push('<li class="edit-li score-input"><input type="text" style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" onblur="saveRowInfo(this)"></li>');
	contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: left;" onblur="saveRowInfo(this)"></li>');
	contentHtml.push('</ul>');
	contentHtml.push('</dd>');
	
	var dl = $this.parents("dt").parent("dl");
	dl.append(contentHtml.join(""));
	dl.find("dd:last-child").find(".dd-content").find("input[type=text]").eq(0).focus();
//	var ddcopy = $this.parents("dt").next("dd").clone(true);
//	$this.parents("dt").parent("dl").after(ddcopy);
}




//删除一行
function deleteRow(own){
	var $this = $(own);
	
	var id = $this.parents("li").parent("ul").parent("dd").attr('id');
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		$this.parents("li").parent("ul").parent("dd").remove();
		$.ajax({
			url: '/prepareScriptController/delScriptInfo',
			type: 'post',
			datatype: 'json',
			data:{"id":id},
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("删除成功");
					queryGridHead();
				}
			}
		});
	});
	
}


//表格滚动
function tableScroll() {
	var left = document.getElementById("treeGridContent").scrollLeft;
	document.getElementById("treeGridHeader").scrollLeft = left;
}



//查询表头-加载表
function queryGridHead(){
	var peopleId = [];
	$.ajax({
		url: '/prepareScriptController/queryWeightInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(!response.success){
				parent.showErrorMessage(response.message);
			}
			tableHeadPeople = response.weightList;
			var weightList = response.weightList;
			var headHtml = [];
//			headHtml.push('<div class="tree-grid-header" id="treeGridHeader">');
			headHtml.push('<dl>');
			headHtml.push('<dt></dt>');
			headHtml.push('<dd>');
			headHtml.push('<ul class="tree-header-ul" id="treeHeaderUl">');
			headHtml.push('<li>类型</li>');
			headHtml.push('<li style="text-align: left; box-sizing: border-box;">版本</li>');
			headHtml.push('<li>交稿日期</li>');
			headHtml.push('<li>负责人</li>');
			headHtml.push('<li>负责内容</li>');
			
			
			if(weightList.length != 0){
				for(var i= 0; i< weightList.length; i++){
					headHtml.push('<li>'+ weightList[i].name+'</li>');
					peopleId.push(weightList[i].id);
				}
				
			}
			headHtml.push('<li>总分</li>');
			headHtml.push('<li>状态</li>');
			headHtml.push('<li>备注</li>');
			headHtml.push('</ul>');
			headHtml.push('</dd>');
			headHtml.push('</dl>');
//			headHtml.push('</div>');
//			$("#treeGrid").append(headHtml.join(""));
			$("#treeGridHeader").empty();
			$("#treeGridHeader").append(headHtml.join(""));
			var length = add(multiply(8,150), multiply(150, weightList.length));
			$("#treeHeaderUl").css("width", length);
			//20px是滚动条的宽度
			$("#treeGrid").css("width", length+20);
			
			//加载表格内容
			loadGridContent(peopleId);
		}
	});
}


//加载表格内容
function loadGridContent(tableHead){
	$.ajax({
		url: '/prepareScriptController/queryScriptScheduleInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.result == ""){
				return;
			}
			var data = JSON.parse(response.result);
			var contentHtml = [];
			/*
			 * pid:parentId
			 * id:id
			 * scriptTypeId :类型id
			 * */
			if(data.length != 0){
				console.log(data);
				for(var i=0; i< data.length; i++){
					contentHtml.push('<dl>');
					contentHtml.push('<dt pid="'+ data[i].parentId +'" id="'+ data[i].id +'" tid="'+ data[i].scriptTypeId +'">');
					contentHtml.push('<span class="dt-content"><input class="list-tag zhaikai" type="button" onclick="zhaikaishouqi(this)"><span class="dt-span" title="'+ data[i].name +'">'+ data[i].name +'</span></span>');
					contentHtml.push('<span class="opera-btn-list" style="display: block">');
					if(!isReadonly){
						contentHtml.push('<a class="add-row-btn" href="javascript:void(0);" onclick="addRow(this)" title="添加"></a>');
					}
					
//					contentHtml.push('<a class="delete-row-btn" href="javascript:void(0);" onclick="deleteNode(this)" title="删除"></a>');
					contentHtml.push('</span>');
					contentHtml.push('</dt>');
			        var children = data[i].children;
			        if(children.length != 0){
			        	for(var j= 0; j< children.length; j++){
			        		contentHtml.push('<dd id="'+ children[j].id +'" pid="'+ children[j].parentId +'" tid="'+ data[i].scriptTypeId +'">');
			        		contentHtml.push('<ul class="tree-content-ul"    onmouseOver="showDeleteBtn(this)" onmouseOut="hideDeleteBtn(this)">');
			        		contentHtml.push('<li></li>');
			        		if(children[j].edition == undefined || children[j].edition == null){
			        			children[j].edition = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><span class="dd-content"><input type="text"  value="'+ children[j].edition +'" onblur="saveRowInfo(this)"></span><span class="opera-btn-list"><a class="delete-row-btn" href="javascript:void(0);" onclick="deleteRow(this)" title="删除"></a></span></li>');
			        		if(children[j].finishDate == undefined || children[j].finishDate == null){
			        			children[j].finishDate = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" value="'+ children[j].finishDate +'" onfocus="WdatePicker({isShowClear:true, onpicked:function(dp){saveRowInfo(dp.el)} })"></li>');
			        		if(children[j].personLiable == undefined || children[j].personLiable == null){
			        			children[j].personLiable = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" value="'+ children[j].personLiable +'" onblur="saveRowInfo(this)"></li>');
			        		if(children[j].content == undefined || children[j].content == null){
			        			children[j].content = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: left;" value="'+ children[j].content +'" onblur="saveRowInfo(this)"></li>');
			        		for(var key=0; key< tableHead.length; key++){//pid:人员id
			        			var reviewweightId = children[j].reviewweightId;
			        			var scoreString = children[j].score;
			        			if(reviewweightId != ""){
			        				var reviewweightIdArray = reviewweightId.split(",");
			        				var scoreArray = scoreString.split(",");
			        				var flag = true;
				        			for(var k= 0; k< reviewweightIdArray.length; k++){
				        				if(tableHead[key] == reviewweightIdArray[k]){
				        					flag = false;
				        					var sc = scoreArray[k];
				        					if('blank' == sc){
				        						sc = '';
				        					}else{
				        						sc = Number(sc).toFixed(2);
				        					}
				        					contentHtml.push('<li class="edit-li people-list" pid="'+ tableHead[key] +'"><input type="text" style="text-align: center;" onkeyup="checkOutNum(this)" value="'+ sc +'" onblur="caluScoreCoount(this)"></li>');
				        				}
				        			}
				        			if(flag){
				        				contentHtml.push('<li class="edit-li people-list" pid="'+ tableHead[key] +'"><input type="text" style="text-align: center;" onkeyup="checkOutNum(this)" onblur="caluScoreCoount(this)"></li>');
				        			}
			        			}else{
			        				contentHtml.push('<li class="edit-li people-list" pid="'+ tableHead[key] +'"><input type="text" style="text-align: center;" onkeyup="checkOutNum(this)" onblur="caluScoreCoount(this)"></li>');
			        			}
			        			
			        			
			        		}
			        		
			        		var totalScore = 0.0;
			        		if(children[j].totleScore == undefined || children[j].totleScore == null || children[j].totleScore == ""){
			        			children[j].totleScore = "";
			        			totalScore = "";
			        		}else{
			        			children[j].totleScore =Number(children[j].totleScore).toFixed(2);
			        			totalScore = fmoney(Number(children[j].totleScore).toFixed(2));
			        		}
			        		contentHtml.push('<li class="edit-li score-input"><input type="text" style="text-align: center;" onkeyup="checkOutNum(this)" value="'+ totalScore +'" onblur="saveRowInfo(this)"></li>');
			        		if(children[j].status == undefined || children[j].status == null){
			        			children[j].status = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: center;" value="'+ children[j].status +'" onblur="saveRowInfo(this)"></li>');
			        		if(children[j].mark == undefined || children[j].mark == null){
			        			children[j].mark = "";
			        		}
			        		contentHtml.push('<li class="edit-li"><input type="text"  style="text-align: left;" value="'+ children[j].mark +'" onblur="saveRowInfo(this)"></li>');
			        		contentHtml.push('</ul>');
			        		contentHtml.push('</dd>');
			        	}
			        }
					
					contentHtml.push('</dl>');
				}
			}else{
				contentHtml.push('<dl>');
				contentHtml.push('<dt style="text-align: center;">暂无数据</dt>');
				contentHtml.push('</dt>');
			}
			
			
//			contentHtml.push('</div>');
//			$("#treeGrid").append(contentHtml.join(""));
			$("#treeGridContent").empty();
			$("#treeGridContent").append(contentHtml.join(""));
			var length = add(multiply(8,150), multiply(150, tableHead.length));
			$("#treeGridContent dt").css("width", length);
			$("#treeGridContent dl").css("width", length);
			if(isReadonly){
				$("#treeGridContent input[type=text]").each(function(){
					$(this).attr("disabled", true);
				});
			}
		}
	});
	
}

//删除根节点
function deleteNode(own){
	var $this = $(own);
	var ids = [];
	var id = $this.parents("dt").attr("id");
	ids.push(id);
	var child = $this.parents("dt").parent("dl").find("dd");
	$.each(child, function(){
		var parentId = $(this).attr("pid");
		if(parentId == id){
			var childId = $(this).attr("id");
			ids.push(childId);
		}
	});
	parent.popupPromptBox("提示","确定要删除吗？", function (){
		$.ajax({
			url: '/prepareScriptController/delScriptInfo',
			type: 'post',
			datatype: 'json',
			data:{"id":ids.join(",")},
			success: function(response){
				if(response.success){
					queryGridHead();
					parent.showSuccessMessage("删除成功");
					
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