/**
 * 组讯管理js
 */
var filter={pageNo: 1, pagesize: 50};
var roleOptionsStr=[];
$(function(){
	//初始化分页
	initPagation();
	//初始化上传插件
	initWebUploader();
	//获取系统题材数据
    $.ajax({
        url: "/crewManager/queryAllSubject",
        type: "post",
        dataType: "json",
        success: function(response) {
            if (response.success) {
                var subjectList = response.subjectList;
                for (var i = 0; i < subjectList.length; i++) {
                    var subject = subjectList[i];
                    $("#subject").append("<option value='" + subject.subjectName + "'>" + subject.subjectName + "</option>");
                    
                    $("#search-subject-li").append('<label><input name="searchSubject" value="'+subject.subjectName+'" type="radio" />'+subject.subjectName+'</label> &nbsp;&nbsp;');
                }
            } else {
                showErrorMessage(response.message);
            }
        }
    });
    //初始化职务
    loadCrewDepartmentAndDuties();
    //初始化查询窗口
    $('#queryWindow').jqxWindow({
		theme:theme,
		width: 560,
        height: 460,
		resizable: false, 
        autoOpen: false,
        isModal: true,
        cancelButton: $('#closeSearchSubmit'),
        initContent: function () {
        }
	});
    //button
    $("#searchSubmit").jqxButton({theme:theme, width: 65, height: 25});
    $("#closeSearchSubmit").jqxButton({theme:theme, width: 65, height: 25});
    $("#clearSearchButton").jqxButton({theme:theme, width: 65, height: 25});
});
//初始化职务
//获取系统部门职务信息
function loadCrewDepartmentAndDuties() {
    $.ajax({
        url: "/sysrole/queryCrewDepartmentAndDuties",
        type: "post",
        success: function(response) {
            if (response.success) {
                var roleOptions = [];
            
                var roleList = response.roleList;
                $.each(roleList, function(index, item) {
                    var roleName = item.roleName;
                    var child = item.child;
                    
                    roleOptions.push("<optgroup label='"+ roleName +"'>");
                    
                    $.each(child, function(index, cItem) {
                        var cRoleId = cItem.roleId;
                        var cRoleName = cItem.roleName;
                        
                        roleOptions.push("<option value='" + cRoleId + "'>"+ cRoleName +"</option>");
                    });
                    roleOptions.push("</optgroup>");
                });
                
                roleOptionsStr = roleOptions.join("");
            }
        }
    });
}
//显示组讯
function showTeamInfo() {
	location.href="/teamInfoManager/toCommunityPage?teamType=1";
}
//显示寻组
function showSearchTeam() {
	location.href="/teamInfoManager/toCommunityPage?teamType=2";
}
//初始化分页事件
function initPagation(){
	$.ajax({
		url: '/teamInfoManager/queryTeamInfoList',
		type: 'post',
		data: filter,
		datatype: 'json',
		success:  function(response){
			if(response.success){
				var total = response.total;
				if (total == 0 || total == null) {
					total = 1;
				}
				var pageCount = response.pageCount;
				var teamInfoList = response.teamInfoList;
				$('#tablePage').html("");
				loadTeamInfoList(teamInfoList);
				
				kkpager.generPageHtml({
					pno : filter.pageNo,
					//总页码
					total : pageCount,
					//总数据条数
					totalRecords : total,
					mode : 'click',//默认值是link，可选link或者click
					click : function(n){
						this.selectPage(n);
						$("#checkAll").prop('checked',false);
						filter.pageNo=n;
						$.ajax({
							url: '/teamInfoManager/queryTeamInfoList',
							type: 'post',
							data: filter,
							datatype: 'json',
							success: function(response){
								var teamInfoList = response.teamInfoList;
								//加载表格数据
								loadTeamInfoList(teamInfoList);
							}
						});
					}
				}, true);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//加载组讯列表
function loadTeamInfoList(dataList){
	var tableHtml=[];
	if(dataList){
		for(var i=0;i<dataList.length;i++){
			var obj=dataList[i];
			tableHtml.push('<tr>');
			if(obj.status==1) {
				tableHtml.push("<td style='width:50px;'><input type='checkbox' name='teamInfoChk' id='"+obj.teamId+"' onclick='checkOne(this)'></td>");
			}else{
				tableHtml.push("<td style='width:50px;'></td>");
			}
			tableHtml.push("<td style='width:calc((100% - 50px) / 11); text-align: left;' title='"+obj.crewName+"'><a class='link' id='"+obj.teamId+"' onclick='showTeamInfoDetail(this)'>"+obj.crewName+"</a></td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+(obj.status==1 ? '可用' : '不可用')+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11); text-align: left;' title='"+nullToEmptyStr(obj.positionName)+"'>"+nullToEmptyStr(obj.positionName)+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+nullToEmptyStr(obj.shootStartDate)+"</td> ");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+obj.agoDays+"</td> ");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11); text-align: left;'>"+obj.phoneNum+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11); text-align: left;' title='"+obj.contactAddress+"'>"+obj.contactAddress+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+obj.createTime+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+obj.resumeCount+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+obj.storeCount+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 11);'>"+obj.realName+"</td>");
			tableHtml.push('</tr>');
		}
	}
	$("#teamInfoList").html(tableHtml.join(''));
}
//全选
function checkAll(own){
	$("input[name='teamInfoChk']").prop('checked',$(own).prop('checked'));
}
//选择框选中事件
function checkOne(own){
	if(!$(own).prop('checked')) {
		if($("#checkAll").prop('checked')){
			$("#checkAll").prop('checked',false);
		}
	}else{
		var isAllCheck=true;
		$("input[name='teamInfoChk']").each(function(){
			if(!$(this).prop('checked')){
				isAllCheck=false;
				$("#checkAll").prop('checked',false);
				return;
			}
		});
		if(isAllCheck){
			$("#checkAll").prop('checked',true);
		}
	}
}
//将空字符串处理成''
function nullToEmptyStr(obj){
	if(!obj){
		return '';
	}
	return obj;
}
//显示组讯详情
function showTeamInfoDetail(obj){
	$("#deleteBtn").show();
	$("#storeListDiv").show();
	$("li[id='basicinfo']").trigger('click');
	//加载数据
	loadTeamInfo($(obj).attr('id'));
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	//初始化上传插件
	initWebUploader();
}
//添加
function addTeamInfo(){
	$("#deleteBtn").hide();
	$("#storeListDiv").hide();
	//清空表格
	$("li[id='basicinfo']").trigger('click');
	$(':input','#teamInfoForm')  
	 .not(':button, :submit, :reset')  
	 .val('') 
	 .removeAttr('selected');
	//清空职位
	$("#positionBodyTable").html('<tr class="blank-tr"><td colspan="4" style="text-align: center; vertical-align: middle;">暂无数据</td></tr>');
	
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);

	//初始化上传插件
	initWebUploader();
	//清空图片
	var li = $("#uploadFileList").find("li.upload-file-list-li");
	if(li.length != 0){
		var fileId=$(li).attr('id');
		if(fileId) {
			uploader.removeFile(fileId, true);
		}
		$(li).remove();
	}
}
//关闭右侧滑出框
function closeRightWin(){
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
	//移除未上传的图片
	if(uploader.getFiles('inited').length != 0){
		var files = uploader.getFiles('inited');
		for(var i= 0; i< files.length; i++){
			uploader.removeFile(files[i].id, true);
			$("#"+files[i].id).remove();
		}
	}
	//initPagation();
}

var uploader;
//初始化上传插件
function initWebUploader(){
	uploader = WebUploader.create({  
        // 选完文件后，是否自动上传。  
        auto: false,  
        // 文件接收服务端。  
        server: '/teamInfoManager/saveTeamInfoPic',  
        timeout: 30*60*1000,//超时
        // 选择文件的按钮。可选。  
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.  
        pick: {
        	id:'#uploadFileBtn',
        	multiple:false  //是否开起同时选择多个文件能力。
        },
  
	    // 只允许选择图片文件。  
		accept: {
			title: 'Images',
			extensions: 'jpg,jpeg,png',
			mimeTypes: 'image/jpg,image/jpeg,image/png'
		},
        thumb: {
    	    width: 110,
    	    height: 110,
    	    crop: false
        },
        method:'POST' 
	});  
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
    	if(file.size > 104857600){
    		showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var li = $("#uploadFileList").find("li.upload-file-list-li");
		if(li.length != 0){
			//showInfoMessage("由于存在图片，不能上传其他版本，请将当前版本删除后再上传");
			var fileId=$(li).attr('id');
			if(fileId) {
				uploader.removeFile(fileId, true);
			}
			$(li).remove();
		}
		var $li = $("<li class='upload-file-list-li' id='"+file.id+"'></li>");
    	uploader.makeThumb( file, function( error, ret ) {
	        if ( error ) {
	            $li.html("预览错误");
	            $("#uploadFileList").html($li);
	        } else {
	        	$li.append("<img alt='' src='" + ret + "' /><a class='closeTag' title='删除' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
//	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#uploadFileList").append($li);
	        }
	    });		
	});
	
	//当文件开始上传时
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	// 发送请求成功后触发
	uploader.on('uploadSuccess', function(file, response) {
		if (response.success) {
		    $('#myLoader').dimmer("hide");
			showSuccessMessage("保存成功");
            loadTeamInfo(response.teamId);
            initPagation();
		} else {
			showErrorMessage(response.message);
		}

	});
	//当文件上传结束时
	uploader.on('uploadFinished', function(file) {
		
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
//保存组讯基本信息
function saveTeamInfo(){
	var crewName = $("#crewName").val();
	if(!crewName) {
		showErrorMessage('剧组名称不能为空!');
		return;
	}
	var crewType=$("#crewType").val();
	if(!crewType) {
		showErrorMessage('剧组类型不能为空!');
		return;
	}
	var subject=$("#subject").val();
	if(!subject) {
		showErrorMessage('拍摄题材不能为空!');
		return;
	}
	var picFlag = false;
	var li = $("#uploadFileList").find("li.upload-file-list-li");
	if(li.length != 0){
		picFlag = true;
	}
	$.ajax({
        url: "/teamInfoManager/saveTeamInfo",
        type: "post",
        dataType: "json",
        data:{
    		teamId:$("#teamId").val(),
    		crewName:crewName,
    		crewType:crewType,
    		subject:subject,
    		company:$("#company").val(),
    		director:$("#director").val(),
    		scriptWriter:$("#scriptWriter").val(),
    		shootStartDate:$("#shootStartDate").val(),
    		shootEndDate:$("#shootEndDate").val(),
    		shootLocation:$("#shootLocation").val(),
    		contactName:$("#contactName").val(),
    		phoneNum:$("#phoneNum").val(),
    		email:$("#email").val(),
    		contactAddress:$("#contactAddress").val(),
    		crewComment:$("#crewComment").val(),
    		picFlag:picFlag
        },
        success: function(response) {
            if (response.success) {
                $("#teamId").val(response.teamId);
                if (uploader.getFiles().length == 0) {
	    			showSuccessMessage("保存成功");
	                loadTeamInfo(response.teamId);
	                initPagation();
	    		} else {
	    			uploader.option('formData', {
	    				teamId: response.teamId
		    		});
		    		uploader.upload();
	    		}
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}
//删除组讯
function deleteTeamInfo() {
	$.ajax({
		url: '/teamInfoManager/deleteTeamInfo',
		type: 'post',
		data: {teamId: $("#teamId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("删除成功");
				loadTeamInfo($("#teamId").val());
				initPagation();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//批量删除组讯
function deleteMulTeamInfo() {
	var teamIdArr = [];
	$("input[name='teamInfoChk']:checked").each(function(){
		teamIdArr.push($(this).attr('id'));
	});
	if(teamIdArr.length==0){
		showInfoMessage('请选择要删除的信息!');
		return;
	}
	$.ajax({
		url: '/teamInfoManager/deleteTeamInfo',
		type: 'post',
		data: {teamId:teamIdArr.join(',')},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("删除成功");
				initPagation();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//加载组讯信息
function loadTeamInfo(id) {
	$.ajax({
        url: "/teamInfoManager/queryTeamInfo",
        type: "post",
        dataType: "json",
        data:{ teamId:id },
        success: function(response) {
            if (response.success) {
            	//基本信息
            	var teamInfoModel = response.teamInfoModel;
            	
            	//状态
            	if(teamInfoModel.status==2) {
            		$("#deleteBtn").hide();
            	}
            	
            	$("#teamId").val(teamInfoModel.teamId);
            	$("#crewName").val(teamInfoModel.crewName);
            	$("#crewType").val(teamInfoModel.crewType);
            	$("#subject").val(teamInfoModel.subject);
            	$("#company").val(teamInfoModel.company);
            	$("#director").val(teamInfoModel.director);
            	$("#scriptWriter").val(teamInfoModel.scriptWriter);
            	var shootStartDate=new Date(teamInfoModel.shootStartDate);
            	$("#shootStartDate").val(shootStartDate.Format('yyyy-MM-dd'));
            	var shootEndDate=new Date(teamInfoModel.shootEndDate);
            	$("#shootEndDate").val(shootEndDate.Format('yyyy-MM-dd'));
            	$("#shootLocation").val(teamInfoModel.shootLocation);
            	$("#contactName").val(teamInfoModel.contactName);
            	$("#phoneNum").val(teamInfoModel.phoneNum);
            	$("#email").val(teamInfoModel.email);
            	$("#contactAddress").val(teamInfoModel.contactAddress);
            	$("#crewComment").val(teamInfoModel.crewComment);
            	//显示宣传图片
            	if(teamInfoModel.picPath != ""){
            		var picHtml = [];
            		picHtml.push('<li class="upload-file-list-li">');
            		picHtml.push("<img alt='' src='"+teamInfoModel.picPath+"' /><a class='closeTag' title='删除' onclick='deleteUploadFile(this)'></a>");
            		picHtml.push('</li>');
            		$("#uploadFileList").empty();
            		$("#uploadFileList").append(picHtml.join(""));
            	}
            	
            	//收藏信息
            	var teamStoreList = response.teamStoreList;
            	$("#storeTable tr:not(:first)").remove();//清空表格
            	if(teamStoreList && teamStoreList.length>0){
            		var html=[];
            		for(var i=0;i<teamStoreList.length;i++){
            			var obj=teamStoreList[i];
            			html.push('<tr>');
            			html.push('<td>'+obj.realName+'</td>');
            			html.push('<td>'+nullToEmptyStr(obj.phone)+'</td>');
            			html.push('<td>'+obj.createTime+'</td>');
            			html.push('</tr>');
            		}
            		$("#storeTable").append(html.join(''));
            	}else{
            		$("#storeTable").append('<tr><td colspan="3" style="text-align: center; vertical-align: middle;">暂无数据</td></tr>>');
            	}
            	//职位信息
            	var positionInfoList = response.positionInfoList;
            	if(positionInfoList && positionInfoList.length>0) {
            		var html=[];
            		for(var i=0;i<positionInfoList.length;i++) {
            			var obj=positionInfoList[i];
            			html.push('<tr id="'+obj.positionId+'">');
            			html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
            			html.push('<span class="td-content">');
            			html.push('<select class="needPosition" onchange="selectPosition(this)"></select>');
            			html.push('<input type="hidden">');
            			if(obj.status==1) {
                			html.push('<input type="button" class="delete-position" onclick="deletePosition(this)">');
            			}
            			html.push('</span>');
            			html.push('</td>');
            			html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
            			html.push('<input type="text" onkeyup="onlyNumber(this)" value="'+obj.needPeopleNum+'">');
            			html.push('</td>');
            			html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
            			html.push('<textarea rows="3" cols="10">'+obj.positionRequirement+'</textarea>');
            			html.push('</td>');
            			html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;vertical-align:top;">');
            			var userList=obj.userList;
            			if(userList && userList.length>0){
            				html.push('<div class="td-username-div">');
            				for(var j=0;j<userList.length;j++){
            					var userObj=userList[j];
            					html.push('<a class="link" id="'+userObj.userId+'" onclick="gotoUserInfo(this)">'+userObj.realName+'</a>');
            					if(j!=userList.length-1){
            						html.push('，');
            					}
            				}
            				html.push('</div>');
            			}
            			html.push('</td>');
            			html.push('</tr>');
            		}
            		$("#positionBodyTable").html(html.join(''));

            		if(!roleOptionsStr){
            			//初始化职务
            		    loadCrewDepartmentAndDuties();
            		}else{
            			$(".needPosition").each(function(i){
            				$(this).append(roleOptionsStr);
            				$(this).val(positionInfoList[i].needPositionId);
            				$(this).next().val(positionInfoList[i].positionName);
            			});
            		}
            	}else{
            		$("#positionBodyTable").html('<tr class="blank-tr"><td colspan="4" style="text-align: center; vertical-align: middle;">暂无数据</td></tr>');
            	}
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}
//跳到用户页面
function gotoUserInfo(own){
	window.location.href = "/userManager/toUserListPage?type=1&userId="+$(own).attr('id');
}
//tab切换
function changeTeamInfoTab(obj,id){
	if(!$(obj).hasClass('tab_li_current')) {
		$(obj).siblings().removeClass('tab_li_current');
		$(obj).addClass('tab_li_current');
		if(id==1){
			$("#positionInfoDiv").hide();
			$("#basicInfoDiv").show();
		}else{
			$("#basicInfoDiv").hide();
			$("#positionInfoDiv").show();
		}
	}
}
//select选择事件
function selectPosition(obj) {
	var parentText=$(obj).find('option:selected').parent().attr('label');
	var text=$(obj).find("option:selected").text();
	$(obj).next().val(parentText+'-'+text);
}
//添加一行职位
function addPosition(){
	if(!$("#teamId").val()) {
		showInfoMessage('请先保存基本信息!');
		return;
	}
	var blankTr = $("#positionBodyTable").find("tr.blank-tr");
	var blankFlag = false; //是否有空行
	var infoMessage = "";
	var html = [];
	html.push('<tr id="">');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
	html.push('<span class="td-content">');
	html.push('<select class="needPosition" onchange="selectPosition(this)"></select>');
	html.push('<input type="hidden" value="制片人">');
	html.push('<input type="button" class="delete-position" onclick="deletePosition(this)">');
	html.push('</span>');
	html.push('</td>');
	html.push('<td style="width: 20%; min-width: 20%; max-width: 20%;">');
	html.push('<input type="text" onkeyup="onlyNumber(this)">');
	html.push('</td>');
	html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
	html.push('<textarea rows="3" cols="10"></textarea>');
	html.push('</td>');
	html.push('<td style="width: 30%; min-width: 30%; max-width: 30%;">');
	html.push('</td>');
	html.push('</tr>');
	if(blankTr.length > 0){
		$("#positionBodyTable").empty();
	}else{
		var trObj = $("#positionBodyTable").find("tr");
		$.each(trObj, function(i){
			i=i+1;
			var positionId = $(this).find("select").eq(0).val();
			var needPeopleNum = $(this).find("input[type='text']").eq(0).val();
			var positionRequirement = $(this).find("textarea").eq(0).val();
			if(positionId == "" || needPeopleNum == "" || positionRequirement == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行";
				if(positionId==""){
					infoMessage += "招募职位为空,";
				}
				if(needPeopleNum==""){
					infoMessage += "招募人数为空,";
				}
				if(needPeopleNum==""){
					infoMessage += "职务要求为空,";
				}
			}
		});
	}
	if(blankFlag){
		showInfoMessage(infoMessage + "请完善信息");
		return;
	}
	$("#positionBodyTable").append(html.join(""));
	if(!roleOptionsStr){
		//初始化职务
	    loadCrewDepartmentAndDuties();
	}else{
		$(".needPosition").append(roleOptionsStr);
	}
	$("#positionBodyTable").find("tr:last-child").find("select").eq(0).focus();
}
//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/\D/g,'', ""));
}
//保存职位信息
function savePosition() {
	var positionArray = [];
	var trArray = $("#positionBodyTable").find("tr[class!='blank-tr']");
	var blankFlag = false;//是否为空标志
	var infoMessage = "";
	if(trArray.length != 0){
		$.each(trArray, function(i){
			i++;
			if(blankFlag){
				return;
			}
			var positionStr = "";
			
			infoMessage = "第" + i + "行";
			
			var positionId = $(this).find("select").eq(0).val();
			var needPeopleNum = $(this).find("input[type='text']").eq(0).val();
			var positionRequirement = $(this).find("textarea").eq(0).val();
			if(positionId == "" || needPeopleNum == "" || positionRequirement == ""){
				blankFlag = true;
				infoMessage += "第" + i + "行";
				if(positionId==""){
					infoMessage += "招募职位为空,";
				}
				if(needPeopleNum==""){
					infoMessage += "招募人数为空,";
				}
				if(positionRequirement==""){
					infoMessage += "职务要求为空,";
				}
			}else if(positionRequirement.length>1000){
				blankFlag = true;
				infoMessage += "职务要求不能超过1000字,";
			}else{
				if($(this).attr('id')) {
					positionStr+=$(this).attr('id')+"$$";
				}else{
					positionStr+="$$";
				}			
				positionStr+=positionId + "$$";
				positionStr+=$(this).find("input[type='hidden']").eq(0).val() + "$$";
				positionStr+=needPeopleNum + "$$";
				positionStr+=positionRequirement;
				
				positionArray.push(positionStr);
			}
		});
	}
	
	if(blankFlag){
		showInfoMessage(infoMessage.substring(0,infoMessage.length - 1));
		return;
	}
	
	$.ajax({
        url: "/teamInfoManager/saveTeamPosition",
        type: "post",
        dataType: "json",
        data:{teamId:$("#teamId").val(),positionStr:positionArray.join("##")},
        success: function(response) {
            if (response.success) {
            	showSuccessMessage("保存成功");
            } else {
                showErrorMessage(response.message);
            }
        }
    });	
}
//删除职位
function deletePosition(own) {
	var id = $(own).parents("tr").attr("id");
	if(id){
		popupPromptBox("提示","是否要删除该条信息？", function (){
			$.ajax({
				url: '/teamInfoManager/deleteTeamPosition',
				type: 'post',
				data: {teamId: $("#teamId").val(), positionId:id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						showSuccessMessage("删除成功");
						loadTeamInfo($("#teamId").val());
					}else{
						showErrorMessage(response.message);
					}
				}
			});
		});		
	}else{
		$(own).parents("tr").remove();
		showSuccessMessage("删除成功");
	}
}
//显示查询窗口
function openAdvanceSearch(){
	$('#queryWindow').jqxWindow('open');
}
//清空查询条件
function clearSearchCon(){
	$("#queryWindow").find('input[type="radio"]').prop('checked',false);
}
//查询组讯
function queryTeamInfo() {
	var crewType = $("input[name='searchCrewType']:checked").val();
	if(crewType) {
		filter.crewType=crewType;
	}else{
		filter.crewType="";
	}
	var status = $("input[name='searchStatus']:checked").val();
	if(status) {
		filter.status=status;
	}else{
		filter.status="";
	}
	var shootStartType = $("input[name='shootStartType']:checked").val();
	if(shootStartType) {
		filter.shootStartType=shootStartType;
	}else{
		filter.shootStartType="";
	}
	var createTimeType = $("input[name='createTimeType']:checked").val();
	if(createTimeType) {
		filter.createTimeType=createTimeType;
	}else{
		filter.createTimeType="";
	}
	var subject = $("input[name='searchSubject']:checked").val();
	if(subject) {
		filter.subject=subject;
	}else{
		filter.subject="";
	}
	initPagation();
	$('#queryWindow').jqxWindow('close');
}