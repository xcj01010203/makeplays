/**
 * 勘景进度
 */
$(function(){
	reloadMainScenceGrid();
});

function reloadMainScenceGrid() {
	var url = "/sceneViewInfoController/querySceneViewBaseInfo";
	var source = {
		type : 'post',
		datatype : "json",
		datafields : [ {
			name : 'id',
			type : 'string'
		}, {
			name : 'vname',
			type : 'string'
		}, {
			name : 'vaddress',
			type : 'string'
		}, {
			name : 'remark',
			type : 'string'
		} ],
		url : url
	};
	var dataAdapter = new $.jqx.dataAdapter(source);

	var vnamerenderer = function(row, columnfield, value, defaulthtml,columnproperties, rowdata) {
		var html = [];
		html.push("<div class='worker-name-column'>");
		html.push("<div class='financial-subjects-column'>");
		html.push("<a style='color: #f88400;' class='over-apostrophe' href='javascript:updateScence(\""+ row + "\")'>" + rowdata.vname + "</a>");
		html.push("<div class='operate-btn-list'>");
		if(!isReadonly){
			html.push("<a class='float-right delete-finance-subject delete-icon' title='删除' href='javascript:deleteSceneView(\""+ row + "\")'></a>");
		}
		
		html.push("</div>");
		html.push("</div>");
		html.push("</div>");
		return html.join("");
	};
	$("#jqxgrid").jqxGrid({
		width : '100%',
		height : '100%',
		columnsheight : 35,
		rowsheight : 30,
		source : dataAdapter,
		columns : [
				{
					text : '<span class="scene-vname">名称</span><input type="button" class="addIcon" id="addScence" onclick="addScence();">',
					datafield : 'vname',
					align:'center',
					cellsrenderer : vnamerenderer,
					width : "30%"
				}, {
					text : '位置',
					align:'center',
					cellsAlign: "left",
					datafield : 'vaddress',
					width : "40%"
				}, {
					text : '备注',
					align:'center',
					cellsAlign: "center",
					datafield : 'remark',
					width : "30%"
				} ],

		rendered : function() {
			$('div[role="row"]').bind(
				"mouseover",
				function(event){
					$(this).find(".operate-btn-list").show();
				}).bind("mouseout", function() {
				$(this).find(".operate-btn-list").hide();
			});
		}
	});
	
	
}

// 添加场景
function addScence() {
	if(isReadonly){
		return;
	}
//	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', row);
	$("#rightPopUpWin").show().animate({"right" : "0px"}, 500);
	$("#scenceContentIframe").attr("src","/sceneViewInfoController/toSceneViewDetailPage?where=prepare");
	$(".delete-btn").hide();
}
// 修改场景
function updateScence(row) {
	if(isReadonly){
		return;
	}
	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', row);
	var id = dataRecord.id;
	$("#rightPopUpWin").show().animate({"right" : "0px"}, 500);
	$("#scenceContentIframe").attr("src","/sceneViewInfoController/toSceneViewDetailPage?where=prepare&&sceneViewId=" + id);
	$(".delete-btn").hide();
}
// 删除场景
function deleteSceneView(row) {
	var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', row);
	popupPromptBox("提示", "是否要删除?", function() {
		$.ajax({
			url : '/sceneViewInfoController/delSceneViewInfo',
			type : 'post',
			data : {
				"id" : dataRecord.id
			},
			datatype : 'json',
			success : function(response) {
				if (response.success) {
					showSuccessMessage("操作成功");
					reloadMainScenceGrid();
					//$("#mainIframe").attr("src", "/prepareController/toPreparePageCrewPeople");
				} else {
					showErrorMessage(response.message);
				}
			}
		});
	});
}

// 关闭弹窗
function closeRightPopupWin() {
	parent.closeRightPopupWin();
}

function closeRightPopupWin() {
	clearInterval(timer);
	var rightPopWidth = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({
		"right" : 0 - rightPopWidth
	}, 300);

	var timer = setTimeout(function() {
		$("#rightPopUpWin").hide();
	}, 300);
}