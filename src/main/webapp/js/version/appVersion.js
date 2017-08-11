$(function(){
	//初始化版本列表
	initVersionGrid();
//	//初始化上传插件
	initWebUploader();
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
		url: '/androidVersionInfoManager/queryAppVersionList',	
        datatype: "json",
        datafields: [
            { name: 'id', type: 'string'},
            { name: 'versionNo',  type: 'string' },
            { name: 'versionName',  type: 'string' },
            { name: 'updateLog', type: 'string'},
            { name: 'size', type: 'long' },
            { name: 'storePath', type: 'string' },
            { name: 'createTime', type: 'Date' }
        ],
        pagenum: 1,
        pagesize: 20,
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        }
    };
    var dataAdapter = new $.jqx.dataAdapter(source); 
    var versionNoRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	return '<div class="jqx-column-cell align-left"><a href="javascript:void(0);" onclick="ModifyAppVersion(\''+row+'\')">'+ cellvalue +'</a></div>';
    };
    var sizeRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata){
    	var size = rowdata.size.toFixed(2);
    	return '<div class="jqx-column-cell align-right">'+ rowdata.size +'kB</div>';
    };
    $("#appVersionGrid").jqxGrid(
            {
            	theme: theme,
                width: 'calc(100% - 2px)',
                height: '95%',
                source: dataAdapter,
                selectionmode: 'multiplerowsextended',
                pageable: true,
                pagesize: 20,
                pagerbuttonscount: 5,
                columnsheight: 35,
        		rowsheight: 30,
        		showtoolbar: true,
        		localization:localizationobj,//表格文字设置
//                virtualmode: true,
                columns: [
                  { text: '版本号', datafield: 'versionNo', cellsrenderer: versionNoRenderer, width: '20%', align: 'center' },
                  { text: '版本名称', datafield: 'versionName', width: '20%', align: 'center', cellsalign: 'left'},
                  { text: '版本更新日志', datafield: 'updateLog', width: '40%', align: 'center', cellsalign: 'left' },
                  { text: '文件大小', datafield: 'size', cellsrenderer: sizeRenderer, width: '20%', align: 'center', cellsalign: 'right' },
                ],
                rendertoolbar: function(toolbar){
                	var container = [];
        			container.push("<div class='toolbar'>");
        			container.push('<input type="button" class="add-btn" id="addVersionBtn" onclick="addAppVersion()">');
        			container.push('</div>');
        			toolbar.append($(container.join("")));
                }
    });
}

//添加版本信息
function addAppVersion(){
	$("#headerTitle").html("新增版本信息");
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	$("#versionId").val("");
	$("#versionId").val("");
	$("#versionNo").val("");
	$("#versionName").val("");
	$("#updateLog").val("");
	$("#uploadFileList").empty();
	if(uploader.getFiles('inited').length != 0){//从修改状态直接点击添加（删除待上传文件）
		var files = uploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			uploader.removeFile(files[i].id, true);
		}
	}
	initWebUploader();
}
//修改版本信息
function ModifyAppVersion(editrow) {
	$("#headerTitle").html("修改版本信息");
	
	var dataRecord = $("#appVersionGrid").jqxGrid('getrowdata', editrow);
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	if(uploader.getFiles('inited').length != 0){//从添加直接点击修改（删除待上传文件）
		var files = uploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			uploader.removeFile(files[i].id, true);
		}
	}
	initWebUploader();
	$("#versionId").val(dataRecord.id);
	$("#versionNo").val(dataRecord.versionNo);
	$("#versionName").val(dataRecord.versionName);
	$("#updateLog").val(dataRecord.updateLog);
	if(editrow.storePath != ""){
		var html = [];
		html.push('<li class="upload-file-list-li">');
		html.push("<img alt='' src='../images/110-f.png' /><a class='closeTag' title='删除' onclick='deleteUploadFile(this)'></a>");
		html.push('</li>');
		$("#uploadFileList").empty();
		$("#uploadFileList").append(html.join(""));
	}
}
//关闭滑动窗口
function closePopUpWin(){
	clearInterval(timer);
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-width}, 500);
	var timer = setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
	if(uploader.getFiles('inited').length != 0){
		var files = uploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			uploader.removeFile(files[i].id, true);
		}
	}
}

var uploader;
var upFile;
//初始化上传插件
function initWebUploader(){
	uploader = WebUploader.create({  
	       // 选完文件后，是否自动上传。  
	       auto: false,  
	       // 文件接收服务端。  
	       server: '/androidVersionInfoManager/uploadAppFile',  
	       timeout: 30*60*1000,//超时
	       // 选择文件的按钮。可选。  
	       // 内部根据当前运行是创建，可能是input元素，也可能是flash.  
	       pick: '#uploadFileBtn',  
	  
	       // 只允许选择apk文件。  
//	       accept: {
//		        title: 'apk文件',
//		        extensions: 'apk',
////		        mimeTypes: 'image/jpg,image/jpeg,image/png'
//		    },
	       thumb: {
	    	   width: 110,
	    	   height: 110,
	    	   crop: false
	       },
	       method:'POST' 
	   });  
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
		var suffix = file.ext.toLowerCase();
		if(suffix != "apk"){
			showInfoMessage("请选择扩展名为.apk的文件");
			uploader.removeFile( file, true );
			return;
		}
		var li = $("#uploadFileList").find("li.upload-file-list-li");
		if(li.length != 0){
			showInfoMessage("由于存在当前版本，不能上传其他版本，请将当前版本删除后再上传");
			uploader.removeFile( file, true );
			return;
		}/*else{
			if(uploader.getFiles('inited').length != 0){
				var files = uploader.getFiles('inited');
				for(var i= 0; i< files.length; i++){
					uploader.removeFile(files[i].id, true);
				}
			}
		}*/
		var $li = $("<li class='upload-file-list-li'></li>");
    	if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}else{
    		$li.append("<img alt='' src='../images/110-f.png' /><a class='closeTag' title='删除' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
//	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
            $("#uploadFileList").append($li);
	    }
		
	});
	
	//当文件开始上传时
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	//当文件上传结束时
	uploader.on('uploadFinished', function(file) {
		    $(".loader").text("上传成功");
		    $('#myLoader').dimmer("hide");
			showSuccessMessage("保存成功");
			closePopUpWin();
			$('#appVersionGrid').jqxGrid('updatebounddata');
		
	});
	
	uploader.on('uploadComplete', function(file) {
		
	});
}
//删除未上传的文件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	uploader.removeFile(fileId, true);
	own.parent("li").remove();
}
//删除已经上传的文件
function deleteUploadFile(own){
	$(own).parent("li").remove();
}
//保存版本信息
function saveVersion(){
	var subData = {};
	var  id = $("#versionId").val();
	subData.id = id;
	subData.versionNo = $("#versionNo").val();
	subData.versionName = $("#versionName").val();
	subData.updateLog = $("#updateLog").val();
	if (id == ""){//新建
		var li = $("#uploadFileList").find("li.upload-file-list-li");
		if(li.length == 0){
			showInfoMessage("请上传版本文件");
			return;
		}
	}
	$.ajax({
		url: '/androidVersionInfoManager/saveAppVersionInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			var fileId = response.id;
			if(response.success){
				if (uploader.getFiles('inited').length == 0) {
					showSuccessMessage("保存成功");
					closePopUpWin();
					$('#appVersionGrid').jqxGrid('updatebounddata');
	    		} else {
	    			uploader.option('formData', {
	    				appVersionId: fileId
//		    			attpackId: attpackId
		    		});
		    		uploader.upload();
	    		}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/\D/g,'', ""));
}