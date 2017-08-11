$(function(){
	//请求相册列表
	queryAlbumList();
	//初始化新建相册弹窗
	initCreateAlbum();
	//初始化上传剧照窗口
	initUploadPicture();
	//初始化上传图片按钮及上传过程事件
	initUploadEvent();
	//初始化下拉列表事件
	initDropDownButton();
	//初始化设置封面窗口
	initSetCoverWin();
	//初始化移动图片窗口
    initMoveToWin();
    //密码弹窗初始化
    initValidatePasswordWin();
    //初始化修改密码弹窗
    initUpdatePasswordWin();
	$(document).on("click", function(){
		$("#dropListDiv").hide();
	});
	$('#createAlbumWin').on('close',  function(){
		$("#albumNameInput").val("");
	});
	
});



//请求相册列表
function queryAlbumList(){
	$.ajax({
		url: '/crewPicture/queryCrewPictureInfoList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var crewPictureList = response.crewPictureList;
				if(crewPictureList.length != 0){
				     showAlbumList(crewPictureList);
				}else{
					//如果当前没有分组，则新建一个分的分组
					$.ajax({
						url: '/crewPicture/savePictureGroup',
						type: 'post',
						data: {"pictureGroupName": "新的分组"},
						datatype: 'json',
						success: function(response){
							if(response.success){
								if(response.success){
									queryAlbumList();
								}
							}else{
								showErrorMessage(response.message);
							}
						}
					});
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//生成相册列表
function showAlbumList(crewPictureList){
	var html = [];
	for(var i= 0; i< crewPictureList.length; i++){
		html.push('<li id="'+ crewPictureList[i].id +'" attpackid="'+ crewPictureList[i].attpackId +'" crewid="'+ crewPictureList[i].crewId +'" title="'+ crewPictureList[i].attpackName +'">');
		if(crewPictureList[i].sdStorePath == null || crewPictureList[i].attachmentId == ""){
			html.push('<img id="" src="../images/no_cover.png" onclick="queryAlbumDetail(this)">');
		}else{
			html.push('<img id="'+ crewPictureList[i].attachmentId +'" src="'+ crewPictureList[i].sdStorePath +'" onclick="queryAlbumDetail(this)">');
		} 
		html.push('<span class="operat-btn">');
		if(crewPictureList[i].pictureCount != 0){
			html.push('<span class="download-icon" title="下载" onclick="dowloadAlbum(this)" sval="'+ crewPictureList[i].id +'"></span>');
		}
		html.push('<span class="delete-icon" title="删除" onclick="deleteAlbum(this)" sval="'+ crewPictureList[i].id +'"></span>');
		html.push('</span>');
		html.push('<span class="picture-amount">'+ crewPictureList[i].pictureCount +'</span>');
		html.push("<p class='album-name'><input type='text' id='groupName"+ crewPictureList[i].id +"' name='pictureNameInput' value='"+ crewPictureList[i].attpackName +"' onblur='reName(this)' sv='"+ crewPictureList[i].attpackName +"'></p>");
		html.push('</li>');
	}
	$("#fileList").empty();
	$("#fileList").append(html.join(""));
	$("#fileList li").hover(function(){
		$(this).find("span.operat-btn").show();
	},function(){
		$(this).find("span.operat-btn").hide();
	});
	
	//判断是否拥有权限
	if (isCewPictureReadonly) {
		//移除上传按钮组
		$("div[class='btn-list']").hide();
		//禁用重命名
		$("input[name='pictureNameInput']").attr("readonly", true);
		//影藏下载删除按钮
		$(".download-icon").hide();
		$(".delete-icon").hide();
	}
}




//初始化新建相册弹窗
function initCreateAlbum(){
	$("#createAlbumWin").jqxWindow({
		theme: theme,
		width: 450,
		height: 310,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: true,
		isModal: true,
		autoOpen: false
	});
}

//打开创建相册弹窗
function createAlbum(){
	$("#albumPasswordInput").val('');
	$("#repeatAlbumPasswordInput").val('');
	$("#createAlbumWin").jqxWindow("open");
	
}

//创建相册
function confirmCreateAlbum(){
	var name = $("#albumNameInput").val();
	if(name == ""){
		showInfoMessage("请填写分组名称");
		return;
	}
	var picturePassword = $("#albumPasswordInput").val();
	//取出确认密码
	var repeatPassword = $("#repeatAlbumPasswordInput").val();
	//判断两次输入的密码是否相同
	if (picturePassword != repeatPassword) {
		showErrorMessage("两次密码输入不相同请修改！");
		return
	}
	
	$.ajax({
		url: '/crewPicture/savePictureGroup',
		type: 'post',
		data: {"pictureGroupName": name,"picturePassword":picturePassword},
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.success){
					$("#createAlbumWin").jqxWindow("close");
					queryAlbumList();
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//取消创建相册
function cancelCreateAlbum(){
	$("#createAlbumWin").jqxWindow("close");
}

function saveNewName(own, ev){
	if(ev.keyCode == 13){
		reName(own);
	}
}

//重命名
function reName(own){
	$("#sourceFrom").val('3');
	var name = $(own).val();
	var oldName = $(own).attr("sv");
	$("#packetName").val(oldName);
	var pictureId = $(own).parents("li").attr("id");
	if(name == ""){
		showInfoMessage("分组名称不能为空");
		$(own).val(oldName);
		return;
	}
	
	$("#crewPictureId").val(pictureId);
	//判断是否有密码
	if (isKeFu) {
		updateGroupName(own,pictureId, name,oldName);
	}else {
		var picturePassword = "";
		//根据id查询出当前分组的详细信息
		$.ajax({
			url: '/crewPicture/queryPictureInfoById',
			type: 'post',
			data: {"crewPictureId" : pictureId},
			datatype: 'json',
			async: false,
			success: function(response){
				if(response.success){
					var model = response.crewPictureInfo;
					picturePassword = model.picturePassword;
					$("#picturePassword").val(picturePassword);
				}
			}
		});
		
		if (picturePassword != "" &&  picturePassword != null) {
			$("#validatePasswordInput").val('');
			//设置过分组密码，提示输入密码
			$("#validatePasswordWin").jqxWindow("open");
		}else {
			updateGroupName(own,pictureId, name,oldName);
		}
	}
	
}

//更新分组名称
function updateGroupName(own,pictureId, name,oldName) {
	$.ajax({
		url: '/crewPicture/savePictureGroup',
		type: 'post',
		data: {"pictureId": pictureId, "pictureGroupName": name},
		datatype: 'json',
		success: function(response){
			if(response.success){
//				showSuccessMessage("重命名分组成功");
				$("#packetName").val(name);
				return;
			}else{
				$(own).val(oldName);
				showErrorMessage(response.message);
			}
		}
	});
}


//初始化上传剧照窗口
function initUploadPicture(){
	$("#uploadPictureWin").jqxWindow({
		theme: theme,
		width: 1160,
		height: 670,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		showCloseButton: false,
		isModal: true,
		autoOpen: false,
		initContent: function() {
			/*initDropDownButton();*/
//			$("#dropDownInput").val("");
//			$("#attpackId").val("");
//			$("#groupId").val("");
//			$("#crewPictureList").empty();
		}
	});
}

//初始化分组下拉列表
function initDropDownButton(){
	 $("#dropDownInput").on("click", function(event){
	    	var left = $(this).position().left;
	    	var top = $(this).position().top;
	    	var height = $(this).height();
	    	$.ajax({
	    		url: '/crewPicture/queryPictureGroupNameList',
	    		type: 'post',
	    		datatype: 'json',
	    		success: function(response){
	    			if(response.success){
	    				var groupNameList = response.groupNameList;
	    				var html = [];
	    				if(groupNameList.length != 0){
	    					for(var i= 0; i< groupNameList.length; i++){
	    						html.push('<li id="'+ groupNameList[i].id +'" attpack="'+ groupNameList[i].attpackId +'" onclick="setUploadGroup(this, event)">' + groupNameList[i].attpackName + '</li>');
	    					}
	    				}else{
	    					html.push('<li>暂无分组</li>');
	    				}
	    			    $("#dropListUl").empty();
	    			    $("#dropListUl").append(html.join(""));
	    			    $("#dropListDiv").css({"left": left, "top": top+height}).show();
	    			}else{
	    				showErrorMessage(response.message);
	    			}
	    		}
	    	});
	    	
	    });
	 event.stopPropagation();
}

//打开上传剧照窗口
function showUploadPicture(){
	
	$("#uploadPictureWin").jqxWindow("open", function(){ 
		$("#dropDownInput").css({"background": "url(../../images/icon-down-black.png) no-repeat 180px", "background-size": "16px"});
		$("#dropDownInput").attr("disabled", false);
		$("#crewPictureList").empty();
		$("#dropDownInput").val("");
		$("#dropDownInput").attr("attpackid", "");
		$("#dropDownInput").attr("albumid", "");
	});
}

//相册详细信息里面的上传事件
function showUploadWin(){
	var albumName = $(".album-detail-name").text();
	var attpackId = $("#nowAppackId").val();
	$("#uploadPictureWin").jqxWindow("open", function(){
		$("#dropDownInput").css({"background": "url(../../images/icon-down.png) no-repeat 180px", "background-size": "16px"});
		$("#crewPictureList").empty();
		$("#dropDownInput").val(albumName);
		$("#dropDownInput").attr("attpackid", attpackId);
		$("#dropDownInput").attr("albumid", "");
		$("#dropDownInput").attr("disabled", true);
	});
}


//设置下拉按钮列表的选中事件
function setUploadGroup(own, event){
	var id= $(own).attr("id");//相册id
	var attpackId = $(own).attr("attpack");//附件包id
	var attpackName = $(own).text();
	$("#dropDownInput").val(attpackName);
//	$("#attpackId").val(attpackId);
//	$("#groupId").val(id);
	$("#dropDownInput").attr("attpackid", attpackId);
	$("#dropDownInput").attr("albumid", id);
	$("#dropListDiv").hide();
	event.stopPropagation();
}

//查询相册名称是否已经存在
function queryAlbumName(own){
	var $this = $(own);
	var groupName = $this.val();
	if($this.val() != ""){
		$.ajax({
			url: '/crewPicture/queryCrewPictureInfo',
			type: 'post',
			data: {"groupName": groupName},
			datatype: 'json',
			success: function(response){
				if(response.success){
					if(response.crewPictureId == "" || response.crewPictureId == null) {
						popupPromptBox("提示","是否用当前名称"+ groupName +"创建分组？", function (){
							$this.attr("attpackid", response.packetId);
							$this.attr("albumid", "");
						});
					}else{
						$this.attr("attpackid", response.packetId);
						$this.attr("albumid", response.crewPictureId);
					}
					$("#dropListDiv").hide();
				}
			}
		});
	}
}



var uploader;
//初始化上传图片按钮及上传过程事件
function initUploadEvent(){
	uploader = WebUploader.create({  
	       // 选完文件后，是否自动上传。  
	       auto: false,  
	       // 文件接收服务端。  
	       server: '/crewPicture/uploadCrewPicture',  
	       timeout: 30*60*1000,//超时
	       // 选择文件的按钮。可选。  
	       // 内部根据当前运行是创建，可能是input元素，也可能是flash.  
	       pick: '#uploadFileBtn',  
	  
	       // 只允许选择图片文件。  
	       /*accept: {  
	           title: 'Images',  
	           extensions: 'gif,jpg,jpeg,bmp,png',  
	           mimeTypes: 'image/*'  
	       },*/
	       thumb: {
	    	   width: 200,
	    	   height: 200,
	    	   crop: false
	       },
	       method:'POST',  
	   });  
	   // 当有文件添加进来的时候  
	   uploader.on('fileQueued', function(file) {
		   if(file.size > 104857600){
	    		showInfoMessage("文件大小超出了100M");
	    		uploader.removeFile( file, true );
	    		return;
	    	}
			var fileUl = $("#showFileRealNameAndSaveId");
			var $li = $("<li class='upload-file-list-li' id='"+ file.id +"'></li>");
			uploader.makeThumb( file, function( error, ret ) {
				var suffix = file.ext.toLowerCase();
				if(suffix == "jpg" || suffix == "gif" || suffix == "jpeg" || suffix == "bmp" || suffix == "png"){
					$li.append("<img alt='' title='"+ file.name +"' src='" + ret + "' alt='找不到图片'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
		        	$li.append('<p class="progress"><span></p>');
		        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
		            $("#crewPictureList").append($li);
				}else{
					$li.append("<img alt='' title='"+ file.name +"'  src='../images/img_format.png' alt='找不到图片'><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
					$li.append('<p class="img-format">' + suffix + '</p>');
					$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
					$("#crewPictureList").append($li);
				}
		    });
		});
	   
	   uploader.on('startUpload', function(){
		   //一旦开始上传不允许切换相册，不允许再次选择图片
		   $("#uploadFileBtn").find("input[type=file]").attr("disabled", true);
		   $("#dropDownInput").attr("disabled", true);
	   });
	   
	   
	   
	   // 文件上传过程中创建进度条实时显示。  
	   uploader.on( 'uploadProgress', function( file, percentage ) {  
	       var $li = $( '#'+file.id ),  
	           $percent = $li.find('.progress span');  
	  
	       // 避免重复创建  
	       if ( !$percent.length ) {  
	           $percent = $('<p class="progress"><span></span></p>').appendTo( $li ).find('span');  
	       }  
	  
	       $percent.css( {'width': percentage * 100 + '%', "display": "block" });  
	   });  
	  
	   // 文件上传成功，给item添加成功class, 用样式标记上传成功。  
	   uploader.on( 'uploadSuccess', function(file, response) {  
	       /*$( '#'+file.id ).addClass('upload-state-done');*/
		   var albumName = $("#dropDownInput").val();
		   if(response.success){
			   var $li = $( '#'+file.id );
			   $( '#'+file.id ).find('.progress').remove();
			   $( '#'+file.id ).find('.upload-error-capition').remove();
			   var $capition = $('<p class="upload-success-capition" title="'+ albumName +'-上传成功">'+ albumName +'-上传成功</p>');
			   $capition.appendTo($li);
			   uploader.removeFile(file.id, true);
		   }else{
			   var $li = $( '#'+file.id );
			   $( '#'+file.id ).find('.progress').remove();
			   $( '#'+file.id ).find('.upload-success-capition').remove();
			   var $capition = $('<p class="upload-error-capition" title="'+ albumName +'-上传失败,不支持该类型的文件">'+ albumName +'-上传失败,不支持该类型的文件</p>');
			   $capition.appendTo($li);
			   uploader.removeFile(file.id, true);
		   }
		   
	   });  
	  
	   // 文件上传失败，显示上传出错。  
	   uploader.on( 'uploadError', function( file ) {  
		   var albumName = $("#dropDownInput").val();
		   var $li = $( '#'+file.id );
		   $( '#'+file.id ).find('.progress').remove();
		   $( '#'+file.id ).find('.upload-success-capition').remove();
		   var $capition = $('<p class="upload-error-capition" title="'+ albumName +'-上传失败">'+ albumName +'-上传失败</p>');
		   $capition.appendTo($li);
		   uploader.removeFile(file.id, true);
	   });  
	  
	   // 完成上传完了，成功或者失败，先删除进度条。  
	   uploader.on( 'uploadComplete', function( file ) {
	       $( '#'+file.id ).find('.progress').remove();
	       $( '#'+file.id ).find('.closeTag').remove();
	       
	   });  
	   //所有文件上传完成后触发
       uploader.on('uploadFinished', function(){
    	   //上传结束后才能再次选择文件上传
    	   $("#uploadFileBtn").find("input[type=file]").attr("disabled", false);
       });
}



//开始上传文件
function uploadPicture(){
	var attpackId = $("#dropDownInput").attr("attpackid");
	var groupName = $("#dropDownInput").val();
	if(groupName == ""){
		showInfoMessage("请选择要上传到的分组");
		return;
	}
	if (uploader.getFiles('inited').length == 0) {
		showInfoMessage("请选择要上传的图片");
	} else {
		if(groupName != ""){//查询是该相册是否存在，不存在就新建，存在就直接上传
			$.ajax({
				url: '/crewPicture/queryCrewPictureInfo',
				type: 'post',
				data: {"groupName": groupName},
				datatype: 'json',
				success: function(response){
					if(response.success){
						if(response.crewPictureId == "" || response.crewPictureId == null) {
							popupPromptBox("提示","是否用当前名称"+ groupName +"创建分组？", function (){
								$.ajax({
									url: '/crewPicture/savePictureGroup',
									type: 'post',
									data: {"pictureGroupName": groupName},
									datatype: 'json',
									success: function(response){
										if(response.success){
											var idsMap = response.idsMap;
											var packetId = idsMap.packetId;
											attpackId = packetId;
											$("#dropDownInput").attr("attpackid", packetId);
											uploader.option('formData', {
												attpackId: attpackId
											});
											uploader.upload();
											/*$("#dropDownInput").attr("attpackid", "");
											$("#dropDownInput").attr("albumid", "");
											$("#dropDownInput").val("");*/
										}else{
											showErrorMessage(response.message);
											$("#dropDownInput").val("");
										}
									}
								});
							});
						}else{
							$("#dropDownInput").attr("attpackid", response.packetId);
							$("#dropDownInput").attr("albumid", response.crewPictureId);
							uploader.option('formData', {
								attpackId: response.packetId
							});
							uploader.upload();
						}
					}
				}
			});
		}
		
		
	}
}
//删除待上传图片
function deleteReadyUploadFile(own, fileId){
	var $this= $(own);
	uploader.removeFile(fileId, true);
	$this.parent("li").remove();
}

//关闭上传弹窗
function closeUploadWin(){
	$("#uploadPictureWin").jqxWindow("close", function(){
		//查询相册列表
		$("#crewPictureList").empty();
		$("#dropDownInput").val("");
		$("#dropDownInput").attr("attpackid", "");
		$("#dropDownInput").attr("albumid", "");
		queryAlbumList();
		var crewPictureId = $("#nowCrewPictureId").val();
		var attpackId = $("#nowAppackId").val();
		//刷新相册详细列表
		$.ajax({
			url: '/crewPicture/queryAttachmentList',
			type: 'post',
			data: {"crewPictureId" : crewPictureId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					var attachmentList = response.attachmentList;
					var crewPictureInfo = response.crewPictureInfo;
//					$("#showAlbumList").hide();
//					$("#hiddenAlbumDetail").show();
					$("#nowCrewPictureId").val(crewPictureId);
					$("#nowAppackId").val(attpackId);
					//获取该相册的所有信息
					albumDetail(attachmentList, crewPictureInfo);
				}
			}
		});
		//从待上传队列中移除所有待上传文件
		if (uploader.getFiles('inited').length != 0){
			var files = uploader.getFiles('inited');
			for(var i= 0; i< files.length; i++){
				uploader.removeFile(files[i].id, true);
			}
		}
	});
}

//返回主列表
function returnMainList(){
	$("#hiddenAlbumDetail").hide();
	$("#showAlbumList").show();
	queryAlbumList();
}


//查询相册的具体信息
function queryAlbumDetail(own){
	$("#sourceFrom").val('0');
	var $this = $(own);
	var crewPictureId = $this.parent("li").attr("id");//当前相册的id
	$("#crewPictureId").val(crewPictureId);
	$("#validatePasswordInput").val("");
	
	if (isKeFu) {
		queryCrewPictureInfo(crewPictureId);
	}else {
		var picturePassword = "";
		//根据id查询出当前分组的详细信息
		$.ajax({
			url: '/crewPicture/queryPictureInfoById',
			type: 'post',
			data: {"crewPictureId" : crewPictureId},
			datatype: 'json',
			async: false,
			success: function(response){
				if(response.success){
					var model = response.crewPictureInfo;
					picturePassword = model.picturePassword;
					$("#picturePassword").val(picturePassword);
				}
			}
		});
		
		if (picturePassword != "" &&  picturePassword != null) {
			//设置过分组密码，提示输入密码
			$("#validatePasswordWin").jqxWindow("open");
		}else {
			queryCrewPictureInfo(crewPictureId);
		}
	}
	/*var src = $this.attr("src");
	var amount = $this.siblings("span.picture-amount").eq(0).text();
	var albumName = $this.parent("li").find("input[type=text]").eq(0).val();*/
}

//查询分组详情方法
function queryCrewPictureInfo(crewPictureId) {
	$.ajax({
		url: '/crewPicture/queryAttachmentList',
		type: 'post',
		data: {"crewPictureId" : crewPictureId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var attachmentList = response.attachmentList;
				var crewPictureInfo = response.crewPictureInfo;
				var isCreateUser = response.isCreateUser;
				$("#showAlbumList").hide();
				$("#hiddenAlbumDetail").show();
				$("#nowCrewPictureId").val(crewPictureId);
				$("#nowAppackId").val(crewPictureInfo.attpackId);
				//获取该相册的所有信息
				albumDetail(attachmentList, crewPictureInfo);
			}
		}
	});
}

var viewer;

//查询该相册的所有信息
function albumDetail(attachmentList, crewPictureInfo){
	if(viewer != undefined){
		viewer.destroy();
	}
	var html = [];
	if(crewPictureInfo.sdStorePath != null && crewPictureInfo.sdStorePath != ""){
		$("#coverImg").attr("src", crewPictureInfo.sdStorePath);
	}else{
		$("#coverImg").attr("src", "../images/no_cover.png");
	}
	$("#albumRealName").html('<span class="album-detail-name">'+crewPictureInfo.attpackName + '</span>&nbsp;&nbsp;共' + crewPictureInfo.pictureCount + '张');
	if(attachmentList != undefined && attachmentList != null && attachmentList.length >0){
		
		for(var i= 0; i< attachmentList.length; i++){
			html.push('<li attpackid="'+ attachmentList[i].attpackId +'" crewid="'+ attachmentList[i].crewId +'" title="'+attachmentList[i].name +'">');
			if(attachmentList[i].sdStorePath != null && attachmentList[i].sdStorePath != ""){
				var suffix = attachmentList[i].suffix.toLowerCase( );
			    
				if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){//火狐浏览器
			        if(suffix == '.png' || suffix == '.gif' || suffix == '.jpg' || suffix == '.bmp' || suffix == '.jpeg' || suffix == '.apng'){
			        	html.push('<img id="'+ attachmentList[i].id +'" src="'+ attachmentList[i].hdStorePath +'">');
			        }else{
			        	html.push('<img id="'+ attachmentList[i].id +'" src="../images/img_format.png">');
			        	html.push('<p class="img-format">' + suffix + '</p>');
			        }
			    } else{
			    	if(suffix == '.png' || suffix == '.gif' || suffix == '.jpg' || suffix == '.bmp' || suffix == '.jpeg'){
			        	html.push('<img id="'+ attachmentList[i].id +'" src="'+ attachmentList[i].hdStorePath +'">');
			        }else{
			        	html.push('<img id="'+ attachmentList[i].id +'" src="../images/img_format.png">');
			        	html.push('<p class="img-format">' + suffix + '</p>');
			        }
			    }
				
			}
			html.push('<input class="image-checkbox" name="image" type="checkbox" onclick="isCheckAll()">');
			var size = attachmentList[i].size.toFixed(2);
			html.push('<p class="album-name"><input type="text" name="pictureNameInput" value="'+ attachmentList[i].name +'" onkeyup="savePictureName(this,event)" onblur="rePictureName(this)" sv="'+ attachmentList[i].name +'"><span>'+ size +'M</span></p>');
			html.push('</li>');
		}
	}else {
		html.push("");
	}
	
	
	$("#detailImage").empty();
	$("#detailImage").append(html.join(""));
	viewer = new Viewer(document.getElementById('detailImage'), {
        url: 'data-original'
    });
	viewer.reset();
	//取消全选
	$("#selectAll").prop("checked",false);

	//判断是否拥有权限
	if (isCewPictureReadonly) {
		//影藏详情中的操作按钮
		$(".album-operation-btn").hide();
		$("input[name='pictureNameInput']").attr("readonly", true);
	}
}

//全选
function selectAll(own,ev){
	var checkboxLength = 0;
	$("#detailImage :checkbox").each(function(){
		checkboxLength ++;
	});
	if(checkboxLength == 0){
		//如果可勾选的已配置场景为零，全选按钮不可选；
		ev.preventDefault();
	}else{
		$(own).attr("disabled", false);
		if($(own).is(":checked")){
			$("input[type=checkbox][name=image]").each(function(){
				$(this).prop("checked", true);
			});
		}else{
			$("input[type=checkbox][name=image]").each(function(){
				$(this).prop("checked", false);
			});
		}
	}
}

//判断是否是全选
function isCheckAll(){
	var obj = $("#detailImage");
	var checkboxs = obj.find(":checkbox");
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#selectAll").prop("checked",false);
	}else{
		$("#selectAll").prop("checked",true);
	}
}


//初始化设置封面窗口
function initSetCoverWin(){
	$("#selectCoverWin").jqxWindow({
		theme: theme,
		width: 1160,
		height: 670,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}

/*//设置封面
function setCoverImg(){
	var pictureLength = 0;
	$("#detailImage li").each(function(){
		pictureLength ++;
	});
	if(pictureLength > 0){
		$("#selectCoverWin").jqxWindow("open");
		$.ajax({
			url: '/crewPicture/queryAttachmentList',
			type: 'post',
			data: {"crewPictureId" : $("#nowCrewPictureId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					var attachmentList = response.attachmentList;
					$("#showAlbumList").hide();
					$("#hiddenAlbumDetail").show();
					//获取该相册的所有信息
					var html = [];
					if(null != attachmentList && attachmentList.length != 0){
						for(var i= 0; i< attachmentList.length; i++){
							html.push('<li><img id="'+ attachmentList[i].id +'" src="'+ attachmentList[i].sdStorePath +'" onclick="setImgToCover(this)"></li>');
						}
						$("#coverImgUl").empty();
						$("#coverImgUl").append(html.join(""));
					}else{
						$("#coverImgUl").empty();
					}
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}else{
		showInfoMessage("请上传图片后再设置封面");
		return;
	} 
	
}*/

//将图片设置为封面
/*function setImgToCover(own){
	var crewPictureId = $("#nowCrewPictureId").val();
	var attachmentId = $(own).attr("id");
	var src = $(own).attr("src");
	$.ajax({
		url: '/crewPicture/saveIndexPictureInfo',
		type: 'post',
		data: {"crewPictureId": crewPictureId, "attachmentId": attachmentId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("设置成功");
				$("#coverImg").attr("src", src);
				$("#selectCoverWin").jqxWindow("close");
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}*/

//初始化移动图片窗口
function initMoveToWin(){
	$("#moveToWin").jqxWindow({
		theme: theme,
		width: 1160,
		height: 670,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}


//移动到其他相册
function moveTo(){
	var checkedSize = 0;
	var crewPictureId = $("#nowCrewPictureId").val();
	$("input[name=image]:checked").each(function(){
		checkedSize ++;
	});
	if(checkedSize == 0){
		showInfoMessage("请选择图片");
		return;
	}else{
		$("#moveToWin").jqxWindow("open");
		$.ajax({
			url: '/crewPicture/queryCrewPictureInfoList',
			type: 'post',
			data:{crewPictureId: crewPictureId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					var crewPictureList = response.crewPictureList;
					if(crewPictureList.length != 0){
						var html = [];
						for(var i= 0; i< crewPictureList.length; i++){
							html.push('<li id="'+ crewPictureList[i].id +'" attpackid="'+ crewPictureList[i].attpackId +'" crewid="'+ crewPictureList[i].crewId +'" title="'+ crewPictureList[i].attpackName +'"  onclick="selectAlbum(this)">');
							if(crewPictureList[i].sdStorePath == null && crewPictureList[i].indexPictureId == ""){
								html.push('<img id="" src="../images/no_cover.png">');
							}else{
								html.push('<img id="'+ crewPictureList[i].attachmentId +'" src="'+ crewPictureList[i].sdStorePath +'">');
							} 
							html.push('<span class="picture-amount">'+ crewPictureList[i].pictureCount +'</span>');
							html.push('<p class="album-name">'+ crewPictureList[i].attpackName +'</p>');
							html.push('</li>');
						}
						$("#moveToUl").empty();
						$("#moveToUl").append(html.join(""));
					}else{
						$("#moveToUl").empty();
					}
				}else{
					showErrorMessage(response.message);
				}
			}
		});

	}		
}

function selectAlbum(own){
	$(own).siblings("li").removeClass("select");
	$(own).addClass("select");
}

function giveUpAlbum(own){
	$(this).removeClass("select");
}
//移动图片事件
function moveToImg(){
	var ids = [];
	var crewPictureId = $("#nowCrewPictureId").val();
	var attpackId = $("#nowAppackId").val();
	$("input[name=image]:checked").each(function(){
		var id = $(this).parents("li").find("img").attr("id");
		ids.push(id);
	});
	var packetId = $("#moveToUl").find("li.select").eq(0).attr("attpackid");
	$.ajax({
		url: '/crewPicture/moveAttachmentInfo',
		type: 'post',
		data: {"attachmentIds": ids.join(","), "packetId": packetId},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				$("#moveToWin").jqxWindow("close");
				//重新刷新详细信息列表
				$.ajax({
					url: '/crewPicture/queryAttachmentList',
					type: 'post',
					data: {"crewPictureId" : crewPictureId},
					datatype: 'json',
					success: function(response){
						if(response.success){
							var attachmentList = response.attachmentList;
							var crewPictureInfo = response.crewPictureInfo;
//							$("#showAlbumList").hide();
//							$("#hiddenAlbumDetail").show();
							$("#nowCrewPictureId").val(crewPictureId);
							$("#nowAppackId").val(attpackId);
							//获取该相册的所有信息
							albumDetail(attachmentList, crewPictureInfo);
							 $("#selectAll").prop("checked", false);
						}
					}
				});
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//关闭移动窗口
function closeMoveWin(){
	$("#moveToWin").jqxWindow("close");
}


//删除照片
function deletePicture(){
	var ids = [];
	var checkLength = 0;
	$("input[name=image]:checked").each(function(){
		checkLength ++;
		var id = $(this).siblings("img").eq(0).attr("id");
		ids.push(id);
	});
	if(checkLength > 0){
		var crewPictureId = $("#nowCrewPictureId").val();
		var attpackId = $("#nowAppackId").val();
		$.ajax({
			url: '/crewPicture/deleteCrewPictureInfo',
			type: 'post',
			data: {"attachmentIds": ids.join(","), "crewPictureId": crewPictureId, "isDeleteCrewPicture": false},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("删除成功");
					//重新刷新详细信息列表
					$.ajax({
						url: '/crewPicture/queryAttachmentList',
						type: 'post',
						data: {"crewPictureId" : crewPictureId},
						datatype: 'json',
						success: function(response){
							if(response.success){
								var attachmentList = response.attachmentList;
								var crewPictureInfo = response.crewPictureInfo;
//								$("#showAlbumList").hide();
//								$("#hiddenAlbumDetail").show();
								$("#nowCrewPictureId").val(crewPictureId);
								$("#nowAppackId").val(attpackId);
								//获取该相册的所有信息
								albumDetail(attachmentList, crewPictureInfo);
								$("#selectAll").prop("checked", false);
							}
						}
					});
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}else{
		showInfoMessage("请选择要删除的图片");
		return;
	}
	
}


//删除相册
function deleteAlbum(own){
	$("#sourceFrom").val('2');
	var crewPictureId = $(own).attr("sval");
	$("#validatePasswordInput").val("");
	$("#crewPictureId").val(crewPictureId);
	
	if (isKeFu) {
		publicDeleteCrewPicture(crewPictureId);
	}else {
		var picturePassword = "";
		//根据id查询出当前分组的详细信息
		$.ajax({
			url: '/crewPicture/queryPictureInfoById',
			type: 'post',
			data: {"crewPictureId" : crewPictureId},
			datatype: 'json',
			async: false,
			success: function(response){
				if(response.success){
					var model = response.crewPictureInfo;
					picturePassword = model.picturePassword;
					$("#picturePassword").val(picturePassword);
				}
			}
		});
		
		if (picturePassword != "" &&  picturePassword != null) {
			//设置过分组密码，提示输入密码
			$("#validatePasswordWin").jqxWindow("open");
		}else {
			publicDeleteCrewPicture(crewPictureId);
		}
	}
}

//删除分组方法
function publicDeleteCrewPicture(crewPictureId) {
	popupPromptBox("提示", "是否要删除该分组？", function(){
		$.ajax({
			url: '/crewPicture/deleteCrewPictureInfo',
				type: 'post',
				data: {"crewPictureId": crewPictureId, "isDeleteCrewPicture": true},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						queryAlbumList();
					}else{
						showErrorMessage(response.message);
					}
				}
		});
	}); 
}

//下载相册里面的图片
function downloadImg(){
	var ids = [];
	var checkLength = 0;
	var packetName = $(".album-detail-name").text();
	$("input[name=image]:checked").each(function(){
		checkLength ++;
		var id = $(this).siblings("img").eq(0).attr("id");
		ids.push(id);
	});
	if(checkLength > 0){	
		/*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
	    var _PageHeight = document.documentElement.clientHeight;
	    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	    //在页面未加载完毕之前显示的loading Html自定义内容
	    var _LoadingHtml = $("#loadingDiv");
	    
	    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	    //呈现loading效果
	    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	    _LoadingHtml.show();
		$.ajax({
			url: '/crewPicture/downLoadCrewPicture',
			type: 'post',
			data: {"attachmentIds": ids.join(","), "packetName": packetName},
			datatype: 'json',
			success: function(response){
				if(response.success){
					 $("#selectAll").prop("checked", false);
					_LoadingHtml.hide();
		            $(".opacityAll").hide();
					var form = $("<form></form>");
		            form.attr("action","/fileManager/downloadFileByAddr");
		            form.attr("method","post");
		            form.append("<input type='hidden' name='address'>");
		            form.find("input[name='address']").val(response.downloadPath);
		            $("body").append(form);
		            form.submit();
		            
		            form.remove();
		           
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}else{
		showInfoMessage("请选择要下载的图片");
	}
}

//下载整个相册
function dowloadAlbum(own){
	$("#sourceFrom").val('1');
	var packetId = $(own).parents("li").attr("attpackid");
	var packetName = $(own).parents("li").find("input[type=text]").val();
	$("#packetId").val(packetId);
	$("#packetName").val(packetName);
	var $this = $(own);
	var crewPictureId = $this.attr("sval");//当前相册的id
	$("#validatePasswordInput").val("");
	$("#crewPictureId").val(crewPictureId);
	
	if (isKeFu) {
		publicDownLoad(packetId, packetName);
	}else {
		var picturePassword = "";
		//根据id查询出当前分组的详细信息
		$.ajax({
			url: '/crewPicture/queryPictureInfoById',
			type: 'post',
			data: {"crewPictureId" : crewPictureId},
			datatype: 'json',
			async: false,
			success: function(response){
				if(response.success){
					var model = response.crewPictureInfo;
					picturePassword = model.picturePassword;
					$("#picturePassword").val(picturePassword);
				}
			}
		});
		
		if (picturePassword != "" &&  picturePassword != null) {
			//设置过分组密码，提示输入密码
			$("#validatePasswordWin").jqxWindow("open");
		}else {
			publicDownLoad(packetId, packetName);
		}
	}
	
	
}

//下载相册方法
function publicDownLoad(packetId, packetName) {
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
	$.ajax({
		url: '/crewPicture/downLoadCrewPicture',
		type: 'post',
		data: {"packetId": packetId, "packetName": packetName},
		datatype: 'json',
		success: function(response){
			if(response.success){
				_LoadingHtml.hide();
	            $(".opacityAll").hide();
				var form = $("<form></form>");
	            form.attr("action","/fileManager/downloadFileByAddr");
	            form.attr("method","post");
	            form.append("<input type='hidden' name='address'>");
	            form.find("input[name='address']").val(response.downloadPath);
	            $("body").append(form);
	            form.submit();
	            
	            form.remove();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

function savePictureName(own,ev){
	if(ev.keyCode == 13){
		rePictureName(own);
	}
}

//照片重命名
function rePictureName(own){
	var $this = $(own);
	var oldName = $this.attr("sv");
	var attpackId = $this.parents("li").attr("attpackid");
	var attachmentId = $this.parents("li").find("img").attr("id");
	var attachmentName = $this.val();
	if(attachmentName == ""){
		showInfoMessage("名称不能为空");
		$this.val(oldName);
		return;
	}
	$.ajax({
		url: '/crewPicture/saveAttachmentPictureName',
		type: 'post',
		data: {"attachmentId": attachmentId, "attachmentName": attachmentName, "attpackId":attpackId},
		datatype: 'json',
		success: function(response){
			if(response.success){
//				showSuccessMessage("操作成功");
				return;
			}else{
				
				$this.val(oldName);
				showErrorMessage(response.message);
			}
		}
	});
}

//密码输入框初始化
function initValidatePasswordWin() {
	$("#validatePasswordWin").jqxWindow({
		theme: theme,
		width: 360,
		height: 160,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}

//修改密码输入框初始化
function initUpdatePasswordWin() {
	$("#updatePasswordWin").jqxWindow({
		theme: theme,
		width: 360,
		height: 320,
		maxWidth: 2000,
		maxHeight: 2000,
		resizable: false,
		isModal: true,
		autoOpen: false
	});
}

//验证密码是否正确
function confirmValidatePassword() {
	//取出输入的密码
	var validatePassword = $("#validatePasswordInput").val();
	//取出原密码
	var picturePassword = $("#picturePassword").val();
	if (validatePassword == picturePassword) {
		//密码输入正确，取出分组id，查询分组详细信息
		var crewPictureId = $("#crewPictureId").val();
		var sourceFrom = $("#sourceFrom").val();
		if (sourceFrom == '0') {
			queryCrewPictureInfo(crewPictureId);
		}
		
		if (sourceFrom == '1') {
			var packetId = $("#packetId").val();
			var packetName = $("#packetName").val();
			publicDownLoad(packetId, packetName);
		}
		
		if (sourceFrom == '2') {
			publicDeleteCrewPicture(crewPictureId);
		}
		
		if (sourceFrom == '3') {
			//取出输入的分组名称
			var name = $("#groupName"+crewPictureId).val();
			var oldName = $("#groupName"+crewPictureId).attr("sv");
			updateGroupName($("#groupName"+crewPictureId),crewPictureId, name,oldName);
		}
		
		$("#validatePasswordWin").jqxWindow("close");
	}else {
		showErrorMessage("密码输入错误，请重新输入!");
	}
}

//取消验证密码
function cancelValidatePassword() {
	var sourceFrom = $("#sourceFrom").val();
	var crewPictureId = $("#crewPictureId").val();
	if (sourceFrom == '3') {
		var packetName = $("#packetName").val();
		if (packetName != '' && packetName != undefined) {
			$("#groupName"+crewPictureId).val(packetName);
		}
	}
	
	$("#validatePasswordWin").jqxWindow("close");
}

//显示更新密码窗口
function updatePicturePasswordBtn() { 
	//确认原分组是否设置过密码
	var password = $("#picturePassword").val();
	if (password == '' || password == null) {
		$("#oldUpdatePasswordInput").hide();
	}else {
		$("#oldUpdatePasswordInput").show();
	}
	$("#updatePasswordInput").val("");
	$("#oldUpdatePasswordInput").val("");
	$("#repeatUpdatePasswordInput").val("");
	$("#updatePasswordWin").jqxWindow("open");
}

//确认更新密码
function confirmUpdatePassword(){
	//确认原分组是否设置过密码
	var password = $("#picturePassword").val();
	//取出当前分组的id
	var crewPictureId = $("#nowCrewPictureId").val();
	//取出输入的密码
	var picturePassword = $("#updatePasswordInput").val();
	//取出确认密码
	var repeatPassword = $("#repeatUpdatePasswordInput").val();
	if (password == '' || password == null) {
		$("#oldUpdatePasswordInput").hide();
	}
	//取出原密码
	var oldUpdatePassword = $("#oldUpdatePasswordInput").val();
	if (oldUpdatePassword != password) {
		showErrorMessage("原密码输入错误，请重新输入");
		$("#oldUpdatePasswordInput").val('');
		return;
	}
	if ( picturePassword != repeatPassword) {
		showErrorMessage("两次密码输入不相同，请修改！");
		return;
	}
	
	if (picturePassword == '') {
		//取消原密码
		parent.popupPromptBox("提示", "是否要取消当前分组密码？", function(){
			$.ajax({
				url: '/crewPicture/updatePicturePassword',
				type: 'post',
				data: {"crewPictureId": crewPictureId, "picturePassword": picturePassword},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("操作成功");
						$("#updatePasswordWin").jqxWindow("close");
						//更改原密码
						$("#picturePassword").val('');
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});
	}else {
		$.ajax({
			url: '/crewPicture/updatePicturePassword',
			type: 'post',
			data: {"crewPictureId": crewPictureId, "picturePassword": picturePassword},
			datatype: 'json',
			success: function(response){
				if(response.success){
					showSuccessMessage("操作成功");
					$("#updatePasswordWin").jqxWindow("close");
					$("#picturePassword").val(picturePassword);
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}
}

//取消更新密码
function cancelUpdatePassword() {
	$("#updatePasswordWin").jqxWindow("close");
}
