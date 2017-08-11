$(function(){
	//初始化版本列表
	initVersionGrid();
});
//显示app版本管理
function showAppVersion() {
	location.href="/androidVersionInfoManager/toAppVersionListPage?type=1";
}
//显示web版本管理
function showWebVersion() {
	location.href="/androidVersionInfoManager/toAppVersionListPage?type=2";
}
function initVersionGrid(){
	var source =
    {
		url: '/webVersionInfoManager/queryWebVersionList',	
        datatype: "json",
        datafields: [
            { name: 'id', type: 'string'},
            { name: 'versionName',  type: 'string' },
            { name: 'insideUpdateLog', type: 'string'},
            { name: 'userUpdateLog', type: 'string'},
            { name: 'createTime', type: 'date' }
        ],
        pagenum: 1,
        pagesize: 20,
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        }
    };
    var dataAdapter = new $.jqx.dataAdapter(source); 
    var versionNameRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	return '<div class="jqx-column-cell align-left"><a href="javascript:void(0);" onclick="ModifyWebVersion(\''+row+'\')">'+ cellvalue +'</a></div>';
    };
    $("#webVersionGrid").jqxGrid(
        {
        	theme: theme,
            width: 'calc(100% - 2px)',
            height: '95%',
            source: dataAdapter,
            pageable: true,
            pagesize: 20,
            pagerbuttonscount: 5,
            columnsheight: 35,
    		rowsheight: 30,
    		showtoolbar: true,
    		localization:localizationobj,//表格文字设置
    		enabletooltips: true,
            columns: [
              { text: '版本名称', datafield: 'versionName', cellsrenderer: versionNameRenderer, width: '20%', align: 'center', cellsalign: 'left'},
              { text: '内部更新日志', datafield: 'insideUpdateLog', width: '35%', align: 'center', cellsalign: 'left' },
              { text: '用户更新日志', datafield: 'userUpdateLog', width: '35%', align: 'center', cellsalign: 'left' },
              { text: '创建时间', datafield: 'createTime', width: '10%', align: 'center', cellsalign: 'left', cellsformat: 'yyyy-MM-dd HH:mm:ss' },
            ],
            rendertoolbar: function(toolbar){
            	var container = [];
    			container.push("<div class='toolbar'>");
    			container.push('<input type="button" class="add-btn" id="addVersionBtn" onclick="addWebVersion()">');
    			container.push('</div>');
    			toolbar.append($(container.join("")));
           }
    });
}

//添加版本信息
function addWebVersion(){
	$("#headerTitle").html("新增版本信息");
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	$("#versionId").val("");
	$("#versionName").val("");
	$("#insideUpdateLog").val("");
	$("#userUpdateLog").val("");
}
//修改版本信息
function ModifyWebVersion(editrow) {
	$("#headerTitle").html("修改版本信息");
	
	var dataRecord = $("#webVersionGrid").jqxGrid('getrowdata', editrow);
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);	
	$("#versionId").val(dataRecord.id);
	$("#versionName").val(dataRecord.versionName);
	$("#insideUpdateLog").val(dataRecord.insideUpdateLog);
	$("#userUpdateLog").val(dataRecord.userUpdateLog);
}
//关闭滑动窗口
function closePopUpWin(){
	clearInterval(timer);
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-width}, 500);
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
}

//保存版本信息
function saveVersion(){
	var subData = {};
	var  id = $("#versionId").val();
	subData.id = id;
	subData.versionName = $("#versionName").val();
	if(!versionName){
		showInfoMessage('请输入版本名称');
	}
	subData.insideUpdateLog = $("#insideUpdateLog").val();
	if(!insideUpdateLog){
		showInfoMessage('请输入内部更新日志');
	}
	subData.userUpdateLog = $("#userUpdateLog").val();
	if(!userUpdateLog){
		showInfoMessage('请输入用户更新日志');
	}
	$.ajax({
		url: '/webVersionInfoManager/saveWebVersionInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				closePopUpWin();
				$('#webVersionGrid').jqxGrid('updatebounddata');
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}