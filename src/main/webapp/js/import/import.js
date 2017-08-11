$(document).ready(function(){
	//获取上传参数
	getUploadOptions();
	//初始化上传
	initUploader();
	
});

//文件上传成功标志
var uploadFileFlag;
var isDelete;
var uploader;
//获取上传参数
function getUploadOptions(){
	var needIsCover = $("#needIsCover").val();
	if(needIsCover == "true"){
		$("#optional").show();
	}else{
		$("#optional").hide();
	}
}
//初始化上传
function initUploader(){
	var serverUrl = $("#uploadUrl").val();
	uploader = WebUploader.create({
		server: serverUrl,
		pick: '#selectFileBtn',
		accept: {
			extensions: 'xls,xlsx',
			mimeTypes: 'application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
		},
		threads: 1,
		fileNumLimit: 1,
		multiple: false,
		resize: false,
		timeout: 30 * 60 * 1000
	});
	
	//显示用户选择
	uploader.on('fileQueued', function(file){
		
		var html = [];
		html.push("<tr class='file-item-tr' id='"+ file.id +"'>");
		html.push("  <td><p>"+ file.name + "</p></td>");
		html.push("  <td class='state'>等待上传...</td>");
		html.push("  <td><span class='delete-btn'>删除</span></td>");
		html.push("</tr>");
		var newTr = $(html.join(""));
		newTr.find("span.delete-btn").on("click",function(){
			uploader.removeFile(file, true);
			newTr.remove();
		});
		$("table#fileListTable tbody").append(newTr);
		if($("#queryDelete").val() == "true"){
			swal({
				title: "提示",
				text: '是否要删除原有数据？',
				type: "warning",
				showCancelButton: true,
				confirmButtonColor: "rgba(255,103,2,1)",
		        confirmButtonText: "是",   
		        cancelButtonText: "否",   
		        closeOnConfirm: true,   
		        closeOnCancel: false
			}, function(isConfirm){
				if(isConfirm){
					isDelete = true;
					uploader.option('formData', {
						isDelete: isDelete
					});
					uploader.upload();
				}else{
					isDelete = false;
					//是否覆盖数据
					isRetainLastData();
				}
			});
		}else{
			isDelete = false;
			//是否覆盖数据
			isRetainLastData();
		}
		
		
	});
	
	
	// 当有文件添加进来的时候
	uploader.on('beforeFileQueued', function(file) {
		var ext = file.ext;
		
		if (ext != "xls" && ext != "xlsx") {
			return false;
		}
		
		
		
		return true;
	});
	
	//开始上传
	uploader.on('uploadStart', function(file){
		$("#analyticalInfo").append("<span>--《" + file.name + "》开始上传</span>");
	});
	
	//文件上传过程中创建进度条实时显示
	uploader.on('uploadProgress', function(file, percentage){
		var $state = $("#"+ file.id).find(".state"), $percent = $state.find('.progress-bar');
		//避免重复创建
		if(!$percent.length){
			$state.text("");
			$state.append("<div class='progress-bar spinner'><div class='rect1'></div><div class='rect2'></div><div class='rect3'></div><div class='rect4'></div><div class='rect5'></div></div>");
			$("table#fileListTable tbody").find("tr[id=" + file.id + "]").find(".delete-btn").hide();
		}
	});
	
	
	//文件上传成功时
	uploader.on('uploadSuccess', function(file, response){
		if(response.success){
			$("#"+ file.id).find(".state").text("已上传");
			$("#analyticalInfo").append("<span>--《" + file.name + "》上传成功</span><br>");
			uploadFileFlag = true;
		}else{
			$('#'+ file.id).find('.state').text('解析失败');
			//中断上传当前正在上传的文件。
            uploader.stop(true);
          
           
            $("#analyticalInfo").append("<br><span>"+ response.message +"</span><br>");
            $("#analyticalInfo").append("<span>--《" + file.name + "》上传失败</span><br>");
		}
		//删除列表中的文件
		 setTimeout(function(){
             $("table#fileListTable tbody").find("tr[id=" + file.id + "]").find(".delete-btn").click();
         }, 1000);
	});
	
	
	//文件上传失败
	uploader.on('uploadError', function(file, reason){
		$("#"+ file.id).find(".state").text("网络故障");
		//中断上传
		uploader.stop(true);
		setTimeout(function(){
            $("#filelist tbody").find("tr[id=" + file.id + "]").find(".delete-btn").click();
        }, 1000);
		$("#analyticalInfo").append("<span>--网络故障，错误码：" + reason + "</span><br>");
	});
	
	
	/*//文件上传
	$("#importFileBtn").on("click", function() {
		var isCover= $("input[type='radio']:checked").val();
		uploader.option('formData', {
			isCover: isCover
		});
		uploader.upload();
	});*/
	
	
} 

//是否覆盖数据
function isRetainLastData(){
	//文件加入队列后直接上传
	if($("#needIsCover").val() == "true"){
		/*var isCover= $("input[type='radio']:checked").val();
		if(isCover == "true"){*/
			swal({
			    title: "提示",
		        text: '如遇重复数据替换还是跳过',
		        type: "warning",
		        showCancelButton: true,  
		        confirmButtonColor: "rgba(255,103,2,1)",
		        confirmButtonText: "替换",   
		        cancelButtonText: "跳过",   
		        closeOnConfirm: true,   
		        closeOnCancel: true
		},function (isConfirm){
			
			if (isConfirm){
				var isCover = true;
				uploader.option('formData', {
					isCover: isCover,
					isDelete: false
				});
				uploader.upload();
			}else{
				var isCover = false;
				uploader.option('formData', {
					isCover: isCover,
					isDelete:false
				});
				uploader.upload();
			}
		});
	}else{
		uploader.upload();
	}
}




//下载模板
function downLoadTemplate(){
	window.location.href= $("#templateUrl").val();
}
//关闭按钮
function closeImportWin(){
	parent.closeImportWin();
	var isCompareData = $('#isCompareData').val();
	if("true"==isCompareData){
		parent.compareData();
	}else if(uploadFileFlag){
		parent.window.location.href= $("#refreshUrl").val();
	}
}
