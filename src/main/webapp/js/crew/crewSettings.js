var interval;
$(document).ready(function() {
	//获取系统题材数据
    $.ajax({
        url: "/crewManager/queryAllSubject",
        type: "post",
        dataType: "json",
        async: false,
        success: function(response) {
            if (response.success) {
                var subjectList = response.subjectList;
                for (var i = 0; i < subjectList.length; i++) {
                    var subject = subjectList[i];
                    $("#subject").append("<option value='" + subject.subjectName + "'>" + subject.subjectName + "</option>");
                }
            } else {
                showErrorMessage(response.message);
            }
        }
    });
	
    loadCrewInfo(); //加载剧组信息
    loadCrewUserInfo(); //加载剧组成员信息
    if(loginUserType=='1') {
        loadCrewAuthInfo(); //加载剧组权限信息
    }
    
    //初始化剧组人员详细信息弹出窗
    $("#crewUserDetailDiv").jqxWindow({
         theme:theme,  
         width: 950,
         height: 720, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: true,
         resizable: false,
         modalZIndex: 1000,
         initContent: function() {
              
         }
    });
    //初始化清空记录弹窗
    $("#deletCrewMemberWin").jqxWindow({
    	    theme: theme,
    	    width: 580,
    	    height: 540,
    	    maxWidth: 2000,
    	    maxHeight: 2000,
    	    resizable: false,
    	    isModal: true,
    	    autoOpen: false,
    
    });
    
    //初始化剧组详细信息弹出窗
    $("#crewDetailDiv").jqxWindow({
         theme:theme,  
         width: 810,
         height: 710, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: true,
         resizable: false,
         modalZIndex: 1000,
         initContent: function() {
              
         }
    });
    
    //初始化拉人入组弹出窗
    $("#addUserDiv").jqxWindow({
         theme:theme,  
         width: 810,
         height: 710, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: true,
         resizable: false,
         modalZIndex: 1000,
         initContent: function() {
              
         }
    });
    $("#addUserDiv").on("close", function() {
        loadCrewUserInfo();
    });
    //tab页切换事件
    $(".tab_div li").click(function() {
    	if(!$(this).hasClass('tab_li_current')) {
    		$(this).siblings().removeClass('tab_li_current');
    		$(this).addClass('tab_li_current');
    		$("#crewInfoSet").hide();
    		$("#crewAuthSet").hide();
    		$("#crewUserManage").hide();
    		$("#authWatch").hide();
    		if($(this).index()==0) {
    			$("#crewInfoSet").show();
    		}else if($(this).index()==1) {
    			$("#crewAuthSet").show();
    		}else if($(this).index()==2) {
    			$("#crewUserManage").show();
    		}else{
    			loadAllAuthTree();
    			$("#authWatch").show();
    		}
    	}
    });
    if(loginUserType=='1'){
		if(parentFlag && parentFlag!='crew') {
        	$("#content").before('<div id="history" onclick="goback()" class="return-img" title="返回"></div>');
		}
    	$("#crewNameText").after('<input id="deletecrewbtn" class="delete-crew-img" type="button" onclick="deleteCrewMember()" title="清空记录">'
    			+'<div class="check-div"><span class="font-color" id="stop">停用</span><div class="ui toggle checkbox"><input type="checkbox" id="isStop" checked onclick="stopChange(this)"><label id="canuse">可用</label></div></div>');
    	$(".tab_div li").eq(1).show();
    }
    initFormValid();
    initWebUploader();
});
//是否停用
function stopChange(own){
	var $this = $(own);
	if($this.is(":checked")){
		$("#stop").addClass("font-color");
		$("#canuse").removeClass("font-color");
	}else{
		$("#canuse").addClass("font-color");
		$("#stop").removeClass("font-color");
	}
	$.ajax({
        url: "/crewManager/updateCrewIsStop",
        type: "post",
        async: true,
        dataType: "json",
        data: {isStop:!$this.is(":checked")},
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
        }
    });
}

function initFormValid(){
    //剧组名称blur事件，判断非空
    $("#crewName").on("blur", function() {
        if(!validateCrewName($(this).val())){
        	return false;
        }
    });
    $("#crewName").on("focus", function() {
        $("#crewNameErrorMsg").text("");
    });
    
    //入组密码blur事件，判断格式
    $("#enterPassword").on("blur", function() {
        if(!validateEnterPassword($(this).val())){
        	return false;
        }
    });
    $("#enterPassword").on("focus", function() {
        $("#enterPassMsg").text("");
    });
    //立项集数
    $("#seriesNo").on("blur", function() {
        if(!validateInteger($(this).val())){
        	return false;
        }
    });
    $("#seriesNo").on("focus", function() {
        $("#seriesNoErrorMsg").text("");
    });
    //剧组执行预算blur事件，判断格式
    $("#budget").on("blur", function() {
        if(!validateFigure($(this).val(),"budget")){
        	return false;
        }
    	var value = $(this).val();
        if(value!=""){
			$(this).val(fmoney(value));
        }
    });
    $("#budget").on("focus", function() {
    	var value = $(this).val().replace(/,/g,"");
		$(this).val(value);
        $("#budgetErrorMsg").text("");
    });
    //合拍协议金额
    $("#coProMoney").on("blur", function() {
        if(!validateFigure($(this).val(),"coProMoney")){
        	return false;
        }
        var value = $(this).val();
        if(value!=""){
			$(this).val(fmoney(value));
        }
    });
    $("#coProMoney").on("focus", function() {
    	var value = $(this).val().replace(/,/g,"");
		$(this).val(value);
        $("#budgetErrorMsg").text("");
    });
    //我方投资比例
    $("#investmentRatio").on("blur", function() {
        if(!validateRatio($(this).val())){
        	return false;
        }
        var value = $(this).val();
        if(value!=""){
			$(this).val(fmoney(value)+'%');
        }
    });
    $("#investmentRatio").on("focus", function() {
    	var value = $(this).val().replace(/%/g,"");
		$(this).val(value);
        $("#budgetErrorMsg").text("");
    });
    
    $(".figure-input").keyup(function(){
		 $(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		 $(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		 $(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		 $(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));       
   }).bind("paste",function(){  //CTR+V事件处理    
  	 $(this).val($(this).val().replace(/[^0-9.]/g,''));     
   }).css("ime-mode", "disabled"); //CSS设置输入法不可用
}

//显示清除记录弹窗
function deleteCrewMember(){
	  $("#deletCrewMemberWin").jqxWindow("open");
	  $("input[type=checkbox][name=crewInfo]:checked").each(function(){
		    $(this).prop("checked", false);
	  });
	  getCrewRecordNum();
}
//获取剧组相关记录数
function getCrewRecordNum(){
	  $.ajax({
		    url: '/crewManager/queryCrewInfoNum',
		    type: 'post',
		    datatype: 'json',
		    success: function(response){
		    	 if(response.success){
		    		 //console.log(response);
		    		 var crewInfoNum = response.crewInfoNum;
		    		 if(crewInfoNum != null){
		    			   $("#ShootProData").html("剧本(<i>"+ crewInfoNum.scenarioNum +"</i>),场景(<i>"+crewInfoNum.viewNum+"</i>),角色(<i>"+ crewInfoNum.viewRoleNum +
		    					   "</i>),通告单(<i>"+ crewInfoNum.noticeNum +"</i>),日志(<i>"+ crewInfoNum.shootLogNum +"</i>),计划(<i>"+ crewInfoNum.shootPlanNum +"</i>,剧照(<i>"+ crewInfoNum.crewPictureNum +"</i>),剪辑(<i>"+crewInfoNum.cutViewNum+"</i>))");
		    			   $("#crewTelData").html("<i>" + crewInfoNum.crewContactNum + "</i>");
		    			   $("#inHotelData").html("<i>" + crewInfoNum.inHotelNum + "</i>");
		    			   $("#carData").html("<i>" + crewInfoNum.carNum + "</i>");
		    			   $("#sceneViewData").html("<i>" + crewInfoNum.sceneViewNum + "</i>");
		    			   $("#financeData").html("收款(<i>" + crewInfoNum.collectionNum + "</i>),付款(<i>"+ crewInfoNum.paymentNum +"</i>),借款(<i>"+ crewInfoNum.loanNum +"</i>)");
		    			   $("#contractData").html("演员合同(<i>"+ crewInfoNum.contractActorNum +"</i>),职员合同(<i>"+ crewInfoNum.contractWorkerNum +"</i>),制作合同(<i>"+ crewInfoNum.contractProduceNum+"</i>),合同待付(<i>"+ crewInfoNum.contractToPaidNum+"</i>)");
		    			   $("#budgetData").html("<i>" + crewInfoNum.financeNum + "</i>");
		    			   $("#caterData").html("<i>" + crewInfoNum.caterNum + "</i>");
		    			   $("#receiptData").html("<i>" + crewInfoNum.receiptNum + "</i>");
		    			   $("#prepareData").html("剧本进度(<i>"+ crewInfoNum.prepareScriptNum +"</i>),选角进度(<i>"+ crewInfoNum.prepareRoleNum +"</i>),剧组人员(<i>"+ crewInfoNum.prepareCrewPeopleNum +"</i>),美术视觉(<i>"+ (crewInfoNum.prepareArteffectRoleNum+crewInfoNum.prepareArteffectLocationNum) +"</i>),宣传进度(<i>"+ crewInfoNum.prepareExtensionNum +"</i>),办公筹备(<i>"+ crewInfoNum.prepareWorkNum +"</i>),商务运营(<i>"+ crewInfoNum.prepareOperateNum +"</i>)");
		    			   if(crewInfoNum.financePassword == 1){
		    				   $("#financePassWord").html("<i>已启用</i>");
		    			   }else{
		    				   $("#financePassWord").html("<i>未启用</i>");
		    			   }
		    			   $("#rootData").html("<i>" + crewInfoNum.crewUserNum + "</i>");
		    		 }
		    	 }else{
		    		 showErrorMessage(response.message);
		    	 }
		    }
	  });
}

//清空剧组相关记录数
function clearCrewBtn(){
	  var infoIds = [];
	  $("input[type=checkbox][name=crewInfo]:checked").each(function(){
		    infoIds.push($(this).val());
	  });
	  if(infoIds.join(",") == ""){
		    showErrorMessage("请选择要清除的记录");
		    return;
	  }else{
		  /*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
		var _PageHeight = document.documentElement.clientHeight,
		    _PageWidth = document.documentElement.clientWidth;
		//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
		var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
		    _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
		//在页面未加载完毕之前显示的loading Html自定义内容
		var _LoadingHtml = $("#loadingDiv");
		
		//呈现loading效果
		_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
		_LoadingHtml.show();
		$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
		    
		  $.ajax({
			    url: '/crewManager/clearCrewInfo',
			    type: 'post',
			    data: {"infoIds": infoIds.join(",")},
			    datatype: 'json',
			    success: function(response){
		    		_LoadingHtml.hide();
		            $(".opacityAll").hide();
			    	if(response.success){
			    		showSuccessMessage("操作成功");
			    		loadCrewUserInfo();
			    		$("#deletCrewMemberWin").jqxWindow("close");
			    	}else{
			    		showErrorMessage(response.message);
			    	}
			    }
		  });
	  }
	  //console.log(infoIds.join(","));
}


//关闭剧组详细信息窗口
/*function closeCrewDetailWindow() {
      if(parentFlag && parentFlag!='null') {
          if(parentFlag=='crew') {
        	    $("#crewDetailDiv").jqxWindow("close");
              loadCrewInfo();
        	    parent.loadCrewList();
        	    return;
        	}
       }
    $("#crewDetailDiv").jqxWindow("close");
    loadCrewInfo();
}*/

//显示剧组人员详细信息窗口
function showDetail(own) {

    var aimUserId = $(own).attr("userId");

    $("#crewUserDetailDiv").find("iframe").attr("src", "/userManager/toCrewUserDetailPage?aimUserId=" + aimUserId);
    $("#crewUserDetailDiv").jqxWindow("open");
}

//显示剧组人员详细信息窗口
function showDetailWithUserId(aimUserId) {
    $("#crewUserDetailDiv").find("iframe").attr("src", "/userManager/toCrewUserDetailPage?aimUserId=" + aimUserId);
    $("#crewUserDetailDiv").jqxWindow("open");
}

//显示剧组详细信息窗口
function showCrewDetail() {
    var crewId = $("#crewId").val();
    var src="/crewManager/toCrewDetailPage";
    if(loginUserType=='1') {
    	src="/crewManager/toCrewDetailPageForAdmin";
    	$("#crewDetailDiv").jqxWindow({
            height: 775
    	});
    }else{
    	$("#crewDetailDiv").jqxWindow({
            height: 710
    	});
    }
    $("#crewDetailDiv").find("iframe").attr("src", src+"?crewId=" + crewId);
    $("#crewDetailDiv").jqxWindow("open");
}


//显示拉人入组窗口
function showAddUserWin() {
    $("#addUserDiv").jqxWindow("open");
    $("#addUserDiv").find("iframe").attr("src", "/userManager/toSelectUserPage");
}

function hideAddUserWin() {
    $("#addUserDiv").jqxWindow("close");
}

//剧组详细信息展开/收起
function crewUpDown(own) {
    var $moreCrewInfo = $(".more-info-tr");
    if ($moreCrewInfo.is(":hidden")) {
        $moreCrewInfo.show();
        
        $(own).attr("src", "images/up.png");
    } else {
        $moreCrewInfo.hide();
        $(own).attr("src", "images/down.png");
    }
    
}

//关闭人物详情窗口
function closeCrewUserDetailWindow() {
    $("#crewUserDetailDiv").jqxWindow("close");
    loadCrewUserInfo();
}

//加载剧组信息
function loadCrewInfo () {
    $.ajax({
        url: "/crewManager/queryCurrentCrewInfo",
        type: "post",
        async: true,
        dataType: "json",
        data: {},
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
            
            var crewInfo = response.crewInfo;
            
            var crewId = crewInfo.crewId;
            var crewName = crewInfo.crewName;
            var crewType = crewInfo.crewType;
            var projectType = crewInfo.projectType;
            var company = crewInfo.company;
            var startDate = crewInfo.startDate;
            var endDate = crewInfo.endDate;
            var shootStartDate = crewInfo.shootStartDate;
            var shootEndDate = crewInfo.shootEndDate;
            var subject = crewInfo.subject;
            var recordNumber = crewInfo.recordNumber;
            var director = crewInfo.director;
            var scriptWriter = crewInfo.scriptWriter;
            var mainactor = crewInfo.mainactor;
            var enterPassword = crewInfo.enterPassword;
            var status = crewInfo.status;
            var seriesNo=crewInfo.seriesNo;
            var coProduction=crewInfo.coProduction;
            var coProMoney=crewInfo.coProMoney;
            var budget=crewInfo.budget;
            var investmentRatio=crewInfo.investmentRatio;
            var remark=crewInfo.remark;
            var isStop=crewInfo.isStop;
            var picPath = crewInfo.picPath;
            if (picPath != '' && picPath != undefined && picPath != null) {
            	var picHtml = [];
        		picHtml.push('<li class="upload-file-list-li">');
        		picHtml.push("<img alt='' src='"+picPath+"' /><a class='closeTag' title='删除' onclick='deleteUploadFile(this)'></a>");
        		picHtml.push('</li>');
        		$("#uploadFileList").empty();
        		$("#uploadFileList").append(picHtml.join(""));
        		
			}
            $("#crewId").val(crewId);
            $("#crewNameText").text("《" + crewName + "》");
            $("#crewName").val(crewName);
            $("#company").val(company);
            $("#shootStartDate").val(shootStartDate);
            $("#shootEndDate").val(shootEndDate);
            $("#crewType").val(crewType);                      
            $("#subject").val(subject);
            $("#recordNumber").val(recordNumber);
            $("#director").val(director);
            $("#scriptWriter").val(scriptWriter);
            $("#mainactor").val(mainactor);
            $("#enterPassword").val(enterPassword);
            $("#status").val(status);
            $("#seriesNo").val(seriesNo);
            $("#coProduction").val(coProduction);
            $("#coProduction").trigger('change');
            if(coProMoney){
                $("#coProMoney").val(fmoney(coProMoney));
            }else{
                $("#coProMoney").val(coProMoney);
            }
            if(budget){
                $("#budget").val(fmoney(budget));
            }else{
                $("#budget").val(budget);
            }
            if(investmentRatio) {
                $("#investmentRatio").val(fmoney(investmentRatio)+'%');
            } else {
            	$("#investmentRatio").val(investmentRatio);
            }
            $("#remark").val(remark);
            $("#startDate").val(startDate);
            $("#endDate").val(endDate);
            $("#projectType").val(projectType);
            if(loginUserType=='1') {
                //默认是试用项目，则可以修改项目类型，默认是普通项目，不能修改项目类型
//                if(projectType != 1) {
//                	$("#projectType").attr('disabled',true);
//                }
                $("#projectType").parent().show();
                $("#startDate").parent().show();
                $("#endDate").parent().show();
            }else{
                $("#projectType").parent().hide();
                $("#startDate").parent().hide();
                $("#endDate").parent().hide();
            }
            if(!isStop) {
            	$("#isStop").prop("checked", true);
            	$("#stop").addClass("font-color");
        		$("#canuse").removeClass("font-color");
            }else{
            	$("#isStop").prop("checked", false);
            	$("#canuse").addClass("font-color");
        		$("#stop").removeClass("font-color");
            }
        }
    });
}
//加载剧组成员信息
function loadCrewUserInfo() {
    $.ajax({
        url: "/userManager/queryCrewUserList",
        type: "post",
        async: true,
        dataType: "json",
        data: {},
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
            
            //清除定时刷新
            if(interval) {
                window.clearInterval(interval);
            }
            
            var toAuditUserList = response.toAuditUserList;
            var crewUserList = response.crewUserList;
            
            if (toAuditUserList.length == 0) {
                $(".enter-apply-div").hide();
            }
            
            $("#enterApplyerTable").html("");
            var enterApplyTableArray = [];
            $.each(toAuditUserList, function(index, item) {
                var userId = item.userId;
                var userName = item.userName;
                var phone = item.phone;
                var roleNames = item.roleNames;
                var remark = item.remark;
                var createTime = item.createTime;
                
                enterApplyTableArray.push("<tr userId='"+userId+"'>");
                enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ userName +"</p></td>");
                enterApplyTableArray.push(" <td style='width: 25%;'><p>"+ roleNames +"</p></td>");
                enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ phone +"</p></td>");
                enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ createTime +"</p></td>");
                enterApplyTableArray.push(" <td style='width: 25%;'><p>"+ remark +"</p></td>");
                enterApplyTableArray.push(" <td style='width: 20%;'>");
                enterApplyTableArray.push("     <input class='agree' userId='"+ userId +"' type='button' value='通过并分配权限' onclick='auditApply(this, true)'>");
                enterApplyTableArray.push("     <input class='reject' userId='"+ userId +"' type='button' value='拒绝' onclick='auditApply(this, false)'>");
                enterApplyTableArray.push(" </td>");
                enterApplyTableArray.push("</tr>");
            });
            $("#enterApplyerTable").html(enterApplyTableArray.join(""));
            
            //启动定时刷新
            interval = setInterval("refreshJoinCrewApply()",5000);
            
            //剧组中已有的成员信息
            $("#crewUserListDiv").html("");
            
            var groupArray = [];
            $.each(crewUserList, function(gindex, gitem) {
                var groupName = gitem.groupName;
                var groupRoleList = gitem.groupRoleList;
                
                var singleGroupArray = [];
                singleGroupArray.push("<div class='group-div'>");
                singleGroupArray.push("<div class='group-name-div'>"+ groupName +"</div>");
                singleGroupArray.push("<div class='group-user-div'>");
                singleGroupArray.push("<table class='group-user-table'>");
                
                $.each(groupRoleList, function(rindex, ritem) {
                    var roleName = ritem.roleName;
                    var roleUserList = ritem.roleUserList;
                    
                    singleGroupArray.push("<tr class='role-name-tr'>");
                    singleGroupArray.push("<td colspan='3'>"+ roleName +"</td>");
                    singleGroupArray.push("</tr>");
                    
                    $.each(roleUserList, function(uindex, uitem) {
                        var userId = uitem.userId;
                        var userName = uitem.realName;
                        var phone = uitem.phone;
                        var createTime = uitem.createTime;
                        
                        singleGroupArray.push("<tr title='单击查看详情' userId='"+ userId +"' onclick='showDetail(this)'>");
                        singleGroupArray.push("<td style='width: 20%'><p>"+ userName +"</p>");
                        if(loginUserType=='1') {
                        	singleGroupArray.push("<span title='删除' name='delfloatspan' userId='"+userId+"' onclick='delUser(this)'></span>");
                        }
                        singleGroupArray.push("</td>");
                        singleGroupArray.push("<td style='width: 20%'>"+ phone +"</td>");
                        singleGroupArray.push("<td style='width: 60%'>"+ createTime +" 入组</td>");
                        singleGroupArray.push("</tr>");
                        
                    });
                });
                
                
                singleGroupArray.push("</table>");
                singleGroupArray.push("</div>");
                singleGroupArray.push("</div>");
                
                groupArray.push(singleGroupArray.join(""));
            });
            
            $("#crewUserListDiv").html(groupArray.join(""));
            
            addDelFloatHover();
        }
    });
}
//加载剧组权限信息
function loadCrewAuthInfo() {
    $.ajax({
        url: "/crewManager/queryCrewAuthList",
        type: "post",
        dataType: "json",
        success: function(response) {
            if (response.success) {
                var appAuthList = response.appAuthList;
                var pcAuthList = response.pcAuthList;
                
                /*
                 * 用户权限列表
                 */
                 //PC端权限
                $("#pcAuthList").html("");
                var pcAuthListHtml = [];    //所有的一级权限集合
                var isAllCheckedMap = {};
                $.each(pcAuthList, function(findex, fitem) {
                   var firstlvlAuthHtml = [];   //单个第一级权限
                   
                   var fauthId = fitem.authId;
                   var fauthName = fitem.authName;
                   var fhasAuth = fitem.hasAuth;
                   var fdifferInRAndW = fitem.differInRAndW;
                   var freadonly = fitem.readonly;
                   var secondAuthList = fitem.subAuthList;
                   
                   firstlvlAuthHtml.push("<div class='first-level-auth'>");
                   firstlvlAuthHtml.push("<label><input type='checkbox' class='checkbox-auth' id='"+fauthId+"' onclick='checkRootAuth(this)'>");
                   firstlvlAuthHtml.push("<em id='"+ fauthId +"' class='single-auth group-auth'>"+ fauthName +"</em></label>");
                   
                   var isAllChecked=0;//0:不选中,1：全部选中，2：部分选中
                   if (fhasAuth) {  //是否拥有此权限
                	   isAllChecked=1;
                   }
                   
                   //遍历二级权限
                   $.each(secondAuthList, function(sindex, sitem) {
                       var secondlvlAuthHtml = [];  //单个第二级权限
                   
                       var sauthId = sitem.authId;
                       var sauthName = sitem.authName;
                       var shasAuth = sitem.hasAuth;
                       var sdifferInRAndW = sitem.differInRAndW;
                       var sreadonly = sitem.readonly;
                       var thirdAuthList = sitem.subAuthList;
                       
                       secondlvlAuthHtml.push("<div class='second-level-auth'>");
                       if (sdifferInRAndW) {//是否区分读写操作
                       
                          if (shasAuth) {  //是否拥有此权限
                        	  if(isAllChecked==0){
                        		  isAllChecked=1;
                        	  }
                              secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ sauthName +"</label>");
                              
                              if (sreadonly) {  //是否只读
                                secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                              } else {
                                secondlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                              }
                          } else {
                        	  if(isAllChecked==1){
                        		  isAllChecked=2;
                        	  }
                              secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ sauthName +"</label>");
                              secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                          }
                       } else {
                           if (shasAuth) {  //是否拥有此权限
                        	   if(isAllChecked==0){
                         		  isAllChecked=1;
                         	  }
                               secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ sauthName +"</label>");
                           } else {
                        	  if(isAllChecked==1){
                        		  isAllChecked=2;
                        	  }
                               secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ sauthName +"</label>");
                           }
                       }
                       
                       
                       //遍历三级权限
                       var thirdlvlAuthHtml = [];   //单个三级权限
                       if (shasAuth) {
                           thirdlvlAuthHtml.push("<div class='third-level-auth'>");
                       } else {
                           thirdlvlAuthHtml.push("<div class='third-level-auth' style='display:none;'>");
                       }
                       
                       $.each(thirdAuthList, function(tindex, titem) {
                           
                           var tauthId = titem.authId;
                           var tauthName = titem.authName;
                           var thasAuth = titem.hasAuth;
                           var tdifferInRAndW = titem.differInRAndW;
                           var treadonly = titem.readonly;
                           
                           
                           if (tdifferInRAndW) {
                               if (thasAuth) {
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ tauthName +"</label>");
                                   
                                   if (treadonly) {
                                       thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                                   } else {
                                       thirdlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                                   }
                                   
                               } else {
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ tauthName +"</label>");
                                   thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                               }
                           } else {
                               if (thasAuth) {
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ tauthName +"</label>");
                               } else {
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ tauthName +"</label>");
                               }
                           }
                           
                       });
                       thirdlvlAuthHtml.push("</div>");
                       
                       secondlvlAuthHtml.push(thirdlvlAuthHtml.join(""));
                       
                       secondlvlAuthHtml.push("</div>");
                       
                       firstlvlAuthHtml.push(secondlvlAuthHtml.join(""));
                   });
                   
                   firstlvlAuthHtml.push("</div>");
                   
                   pcAuthListHtml.push(firstlvlAuthHtml.join(""));                           

                   isAllCheckedMap[fauthId]=isAllChecked;
                });
                
                $("#pcAuthList").append(pcAuthListHtml.join(""));
                for(var id in isAllCheckedMap){
                	var value=isAllCheckedMap[id];
                	if(value==0){
                		$("#"+id).prop("checked", false);
                	}else if(value==1){
                		$("#"+id).prop("checked", true);
                	}else if(value==2){
                		$("#"+id).prop("indeterminate", true);
                	}
                }
                
                //app端权限,app端只有一级权限
                $("#appAuthList").html("");
                var appAuthListHtml = [];    //所有的一级权限集合
                $.each(appAuthList, function(findex, fitem) {
                   var firstlvlAuthHtml = [];   //单个第一级权限
                   
                   var fauthId = fitem.authId;
                   var fauthName = fitem.authName;
                   var fhasAuth = fitem.hasAuth;
                   var fdifferInRAndW = fitem.differInRAndW;
                   var freadonly = fitem.readonly;
                   
                   firstlvlAuthHtml.push("<div class='first-level-auth'>");
                   
                   if (fdifferInRAndW) {//是否区分读写操作
                       
                      if (fhasAuth) {  //是否拥有此权限
                          firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ fauthName +"</label>");
                          
                          if (freadonly) {  //是否只读
                            firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                          } else {
                            firstlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                          }
                      } else {
                          firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ fauthName +"</label>");
                          firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeCrewAuth(this, true)'>可编辑</label>");
                      }
                   } else {
                       if (fhasAuth) {  //是否拥有此权限
                           firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeCrewAuth(this, false)'>"+ fauthName +"</label>");
                       } else {
                           firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeCrewAuth(this, false)'>"+ fauthName +"</label>");
                       }
                   }
                   
                   firstlvlAuthHtml.push("</div>");
                   
                   appAuthListHtml.push(firstlvlAuthHtml.join(""));
                });
                
                $("#appAuthList").append(appAuthListHtml.join(""));
                
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}
//own:当前元素
function checkRootAuth(own) {
	var $this = $(own);
	var operateType=1;
	var authId = $this.attr("id");
	if(!$this.prop('checked')){
		operateType=3;
	}
	$.ajax({
        url: "/crewManager/saveCrewAuthInfo",
        type: "post",
        async: true,
        dataType: "json",
        data: {operateType: operateType, authId: authId},
        success: function(response) {
            if (!response.success) {
                parent.showErrorMessage(response.message);
                return false;
            }
            
            if (!$this.prop('checked')) {
            	//所有子节点设为未选中
            	var childs=$this.parent().siblings(".second-level-auth").find(".single-auth");
            	childs.removeClass("selected");
            	//可编辑设为未选中
            	childs.next(".allow-modify-tag").removeClass("selected");
                
                //三级权限隐藏
                childs.siblings(".third-level-auth").hide();
                //childs.siblings(".third-level-auth").find(".single-auth").removeClass("selected");
            } else {
            	var childs=$this.parent().siblings(".second-level-auth").find(".single-auth");
            	childs.addClass("selected");
            	//可编辑设为选中
            	childs.next(".allow-modify-tag").addClass("selected");
                
                //显示三级权限
                childs.siblings(".third-level-auth").show();
                childs.siblings(".third-level-auth").find(".single-auth").removeClass("selected");
            }
        }
    });
}
//own:当前元素    isModify：是否是修改操作
function changeCrewAuth(own, isModify) {
    var $this = $(own);
    
    var operateType = 1;    //operateType 操作类型 1：新增  2：修改  3：删除
    var authId = '';
    var readonly = false;
    
    //isModify为true表示是点击后面的“可编辑”文本
    if (isModify) {
        operateType = 2;
        authId = $this.prev(".single-auth").attr("id");
        
        if (!$this.prev(".single-auth").hasClass("selected")) {
            return false;
        }
        
        if ($this.hasClass("selected")) {
            readonly = true;
        } else {
            readonly = false;
        }
        
    } else {
        if ($this.hasClass("selected")) {
            operateType = 3;
        } else {
            operateType = 1;
        }
        authId = $this.attr("id");
    }
    
    $.ajax({
        url: "/crewManager/saveCrewAuthInfo",
        type: "post",
        async: true,
        dataType: "json",
        data: {operateType: operateType, authId: authId, readonly: readonly},
        success: function(response) {
            if (!response.success) {
                parent.showErrorMessage(response.message);
                return false;
            }
            
            if ($this.hasClass("selected")) {
                $this.removeClass("selected");
                if (!isModify) {
                    $this.next(".allow-modify-tag").removeClass("selected");
                    
                    //三级权限隐藏
                    $this.siblings(".third-level-auth").hide();
                    $this.siblings(".third-level-auth").find(".single-auth").removeClass("selected");
                    
                    //设置根权限checkbox
                    if($this.parent().hasClass("second-level-auth")) {
                    	var childs = $this.parent().siblings(".second-level-auth").find(".single-auth");
                    	var isAllChecked=0;
                    	for(var i=0;i<childs.length;i++){
                    		if($(childs[i]).parent().hasClass("second-level-auth")) {
                    			if($(childs[i]).hasClass("selected")) {
                    				isAllChecked=2;
                    				break;
                    			}
                    		}
                    	}
                		$this.parent().siblings("label").find("input").prop("indeterminate", false);
                    	if(isAllChecked==0){
                    		$this.parent().siblings("label").find("input").prop("checked", false);
                    	}else if(isAllChecked==2){
                    		$this.parent().siblings("label").find("input").prop("indeterminate", true);
                    	}
                    }
                }
            } else {
                $this.addClass("selected");
                if (!isModify) {
                    $this.next(".allow-modify-tag").addClass("selected");
                    
                    //显示三级权限
                    $this.siblings(".third-level-auth").show();
                    
                  	//设置根权限checkbox
                    if($this.parent().hasClass("second-level-auth")) {
                    	var childs = $this.parent().siblings(".second-level-auth").find(".single-auth");
                    	var isAllChecked=1;
                    	for(var i=0;i<childs.length;i++){
                    		if($(childs[i]).parent().hasClass("second-level-auth")) {
                    			if($(childs[i]).hasClass("selected")) {
                        			isAllChecked=1;
                    			} else {
                    				isAllChecked=2;
                    				break;
                    			}
                    		}
                    	}
                    	$this.parent().siblings("label").find("input").prop("indeterminate", false);
                    	if(isAllChecked==1){
                    		$this.parent().siblings("label").find("input").prop("checked", true);
                    	}else if(isAllChecked==2){
                    		$this.parent().siblings("label").find("input").prop("indeterminate", true);
                    	}
                    }
                }
            }
        }
    });
}
function addDelFloatHover()
{
	$("span[name='delfloatspan']").parents("td").parents("tr").hover(function()
	{
		$(this).find("span").show();
	},
	function()
	{
		$(this).find("span").hide();
	});	
}

//审核入组申请信息
function auditApply(own, agree) {
    var aimUserId = $(own).attr("userId");
    $(own).attr("disabled", "disabled");
    $.ajax({
        url: "/userManager/auditEnterApply",
        type: "post",
        dataType: "json",
        data: {aimUserId: aimUserId, agree: agree},
        async: true,
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
            
            if (agree) {
                $("#crewUserDetailDiv").find("iframe").attr("src", "/userManager/toCrewUserDetailPage?aimUserId=" + aimUserId);
                $("#crewUserDetailDiv").jqxWindow("open");
            } else {
                loadCrewUserInfo();
            }
        }
    });
}
//刷新入组申请
function refreshJoinCrewApply(){
	$.ajax({
	    url: "/userManager/queryJoinCrewApply",
	    type: "post",
	    dataType: "json",
	    success: function(response) {
	        if (!response.success) {
	            showErrorMessage(response.message);
	            return false;
	        }
	            
	        var toAuditUserList = response.resultList;
			
			if (toAuditUserList.length == 0) {
			    $(".enter-apply-div").hide();
			    return false;
			}
			
			var enterApplyTableArray = [];
			$.each(toAuditUserList, function(index, item) {
			    var userId = item.userId;
			    var userName = item.userName;
			    var phone = item.phone;
			    var roleNames = item.roleNames;
			    var remark = item.remark;
			    var createTime = item.createTime;
			    if($("#enterApplyerTable").find('tr[userId="'+userId+'"]').length==0) {
				    enterApplyTableArray.push("<tr userId='"+userId+"'>");
				    enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ userName +"</p></td>");
				    enterApplyTableArray.push(" <td style='width: 25%;'><p>"+ roleNames +"</p></td>");
				    enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ phone +"</p></td>");
				    enterApplyTableArray.push(" <td style='width: 10%;'><p>"+ createTime +"</p></td>");
				    enterApplyTableArray.push(" <td style='width: 25%;'><p>"+ remark +"</p></td>");
				    enterApplyTableArray.push(" <td style='width: 20%;'>");
				    enterApplyTableArray.push("     <input class='agree' userId='"+ userId +"' type='button' value='通过并分配权限' onclick='auditApply(this, true)'>");
				    enterApplyTableArray.push("     <input class='reject' userId='"+ userId +"' type='button' value='拒绝' onclick='auditApply(this, false)'>");
				    enterApplyTableArray.push(" </td>");
				    enterApplyTableArray.push("</tr>");
			    }
			});
			$("#enterApplyerTable").append(enterApplyTableArray.join(""));
			if($(".enter-apply-div").css('display')=='none') {
				$(".enter-apply-div").show();
			}
	     }
	});
}
//从剧组中删除用户
function delUser(obj){
	var userId=$(obj).attr("userId");
	popupPromptBox("提示","确定删除吗?",function(){
        $.ajax({
            url:"/crewManager/deleteCrewUser",
            type:"post",
            dataType:"json",
            data:{
                userId:userId,
                crewId:$("#crewId").val()
            },
            async:true,
            success:function(data){
                if(data.success){
                    showSuccessMessage(data.message);
                    loadCrewUserInfo();
                }else{
                    showErrorMessage(data.message);
                }
            }
        });
    });
	stopPropagation();
}
function stopPropagation(e) {
    e = e || window.event;
    if(e.stopPropagation) { //W3C阻止冒泡方法
        e.stopPropagation();
    } else {
        e.cancelBubble = true; //IE阻止冒泡方法
    }
}
//返回
function goback(){
	if(parentFlag && parentFlag!='null') {
		if(parentFlag=='user') {
    		location.href='/userManager/toUserListPage?type=1&userId='+userId;
		}else if(parentFlag=='log') {
			history.go(-1);
		}
	}
}
//加载权限树
var isAuthIdIsExist=false;
function loadAllAuthTree() {
	//判断是否已设置剧组权限
	$.ajax({
        url:"/crewManager/isCrewHasAuth",
        type:"post",
        dataType:"json",
        success:function(data){
            if(data.success){
            	loadAuthTree('pcTree',2);
            	loadAuthTree('mobileTree',3);
            	var authId = $("#authTitle").attr('authId');
            	if(authId && !isAuthIdIsExist) {
            		authId='';
            		$("#pcTree").jqxTree('selectItem',$("#pcTree").find('li:first')[0]);
            	}
            }else{
                showErrorMessage(data.message);
            }
        }
    });
}
function loadAuthTree(divId,type) {
 	$.ajax({
 		async:false,
		url: '/authorityManager/queryAuthAndUserNumWithoutAdmin',
		type: 'post',
		datatype: 'json',
		data:{type:type},
		success: function(response){
			if(response.success){
				var authList= response.result;
				
				var authId = $("#authTitle").attr('authId');
				var data=[];
				var flag = false;
				for(var i= 0; i< authList.length; i++){
					var icon="";
					if(authList[i].iconCls=='icon-parent') {
						icon="../../images/menu_1.png";
					}else if(authList[i].iconCls=='icon-child') {
						icon="../../images/menu_2.png";
					}else if(authList[i].iconCls=='icon-final') {
						icon="../../images/menu_3.png";       							
					}
					if(authId && authId==authList[i].authId){
						flag=true;
						isAuthIdIsExist=true;
					}
					data.push({'id': authList[i].authId, 'parentId': authList[i].parentId, 'text': authList[i].authName+'('+authList[i].userNum+')','icon':icon});
				}
				 
				//初始化财务科目
				var source = {
					datatype: "json",
					datafields: [
					    { name: 'id' },
					    { name: 'parentId' },
					    { name: 'text' },
					    { name: 'icon'}
					],
					      
					localdata: data,
					id: 'id'
					      
				};
				var dataAdapter = new $.jqx.dataAdapter(source);
				dataAdapter.dataBind();
				var records = dataAdapter.getRecordsHierarchy('id','parentId','items', [{name: 'text', map:'label'}]);
				$("#"+divId).jqxTree({
					theme: theme,
					width: '99%',
					height: '99%',
					source: records,    							 
				});
				$('#'+divId).jqxTree('expandAll');
				//选择权限时的操作
				$('#'+divId).on('select', function (event){
					if(divId=='pcTree'){
						$("#mobileTree").jqxTree('selectItem', null);
					} else {
						$("#pcTree").jqxTree('selectItem', null);
					}
					var args = event.args;
	                var item = $('#'+divId).jqxTree('getItem', args.element);
	                $("#authTitle").attr('authId',item.id);
	                $("#authTitle").html(item.label);
	                loadAuthUser(item.id);
				});
				if(flag){
					$('#'+divId).jqxTree('selectItem',null);
					$('#'+divId).jqxTree('selectItem',$("#"+authId)[0]);
				}else if(!authId && divId=='pcTree'){
					$("#pcTree").jqxTree('selectItem',$("#pcTree").find('li:first')[0]);
				}
			 }else{
				 showErrorMessage(response.message);
			 }
		}
 	});
}
//pc、mobile切换
function tabChange(id) {
	if(id==0) {//pc
		var _this=$("#pcImg");
		if(_this.hasClass('pcImg1')){
			_this.removeClass('pcImg1').addClass('pcImg');
			$("#mobileImg").removeClass().addClass('mobileImg1');
			$("#mobiletreediv").hide();
			$("#pcTree").jqxTree('refresh');
			$("#pctreediv").show();
		}
	}else if(id==1) {//mobile
		var _this=$("#mobileImg");
		if(_this.hasClass('mobileImg1')){
			_this.removeClass('mobileImg1').addClass('mobileImg');
			$("#pcImg").removeClass().addClass('pcImg1');
			$("#mobileTree").jqxTree('refresh');
			$("#mobiletreediv").show();
			$("#pctreediv").hide();
		}
	}
}
//加载某权限用户
function loadAuthUser(authId) {
	$.ajax({
        url: "/userManager/queryAuthUserList",
        type: "post",
        async: true,
        dataType: "json",
        data: {authId:authId},
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
            
            //拥有某权限的成员信息
            $("#rightDiv").html("");

            var crewUserList = response.crewUserList;
            var groupArray = [];
            $.each(crewUserList, function(gindex, gitem) {
                var groupName = gitem.groupName;
                var groupRoleList = gitem.groupRoleList;
                
                var singleGroupArray = [];
                singleGroupArray.push("<div class='group-div'>");
                singleGroupArray.push("<div class='group-name-div'>"+ groupName +"</div>");
                singleGroupArray.push("<div class='group-user-div'>");
                singleGroupArray.push("<table class='group-user-table'>");
                
                $.each(groupRoleList, function(rindex, ritem) {
                    var roleName = ritem.roleName;
                    var roleUserList = ritem.roleUserList;
                    
                    singleGroupArray.push("<tr class='role-name-tr'>");
                    singleGroupArray.push("<td colspan='4'>"+ roleName +"</td>");
                    singleGroupArray.push("</tr>");
                    
                    $.each(roleUserList, function(uindex, uitem) {
                        var userName = uitem.realName;
                        var phone = uitem.phone;
                        var createTime = uitem.createTime;
                        var readonly = uitem.readonly;
                        var differInRAndW = uitem.differInRAndW;
                   	 	singleGroupArray.push("<tr>");
                        if(differInRAndW==1) {
 	                        singleGroupArray.push("<td style='width: 40%'><p>"+ userName+"</p><span title='移除权限' name='delfloatspan' userId='"+uitem.userId+"' authId='"+authId+"' onclick='removeUserAuth(this)'></span></td>");
	//                        if(differInRAndW==1 && readonly==1) {
	//                        	singleGroupArray.push("<font color='orange'>(不可编辑)<font>");
	//                        }
	                        singleGroupArray.push("<td style='width: 20%'>"+ phone +"</td>");
	                        singleGroupArray.push("<td style='width: 20%'>"+ createTime +" 入组</td>");
	                        if(readonly==1) {
		                        singleGroupArray.push("<td style='width: 20%'><div class='ui toggle checkbox user-status'><input id='crewUserStatus' userId='"+uitem.userId+"' authId='"+authId+"' type='checkbox' onclick='modifyUserAuthReadonly(this)'><label>可编辑</label></div></td>");
	                        }else{
		                        singleGroupArray.push("<td style='width: 20%'><div class='ui toggle checkbox user-status'><input id='crewUserStatus' userId='"+uitem.userId+"' authId='"+authId+"' type='checkbox' onclick='modifyUserAuthReadonly(this)' checked><label>可编辑</label></div></td>");
	                        }
                        }else{
 	                        singleGroupArray.push("<td style='width: 40%'><p>"+ userName+"</p><span title='移除权限' name='delfloatspan' userId='"+uitem.userId+"' authId='"+authId+"' onclick='removeUserAuth(this)'></span></td>");
 	                        singleGroupArray.push("<td style='width: 30%'>"+ phone +"</td>");
 	                        singleGroupArray.push("<td style='width: 30%'>"+ createTime +" 入组</td>");
                        }    
	                    singleGroupArray.push("</tr>");                    
                    });
                });
                
                
                singleGroupArray.push("</table>");
                singleGroupArray.push("</div>");
                singleGroupArray.push("</div>");
                
                groupArray.push(singleGroupArray.join(""));
            });
            
            $("#rightDiv").html(groupArray.join(""));
            
            addDelFloatHover();
        }
    });
}
//移除权限
function removeUserAuth(own){
	var _this = $(own);
	popupPromptBox("提示","确定要移除吗?",function(){
		$.ajax({
            url: "/userManager/saveUserAuthInfo",
            type: "post",
            async: true,
            dataType: "json",
            data: {aimUserId: _this.attr('userId'), operateType: 3, authId: _this.attr('authId')},
            success: function(response) {
                if (!response.success) {
                    showErrorMessage(response.message);
                    return false;
                }
                showSuccessMessage(response.message);
                loadAllAuthTree();
            }
        });
    });
}
//修改用户权限只读属性
function modifyUserAuthReadonly(own) {
	var _this = $(own);
	var readonly;
	if(_this.is(":checked")) {
		readonly = false;
	}else{
		readonly = true;
	}
	$.ajax({
        url: "/userManager/saveUserAuthInfo",
        type: "post",
        async: true,
        dataType: "json",
        data: {aimUserId: _this.attr('userId'), operateType: 2, authId: _this.attr('authId'), readonly: readonly},
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
                return false;
            }
//            showSuccessMessage(response.message);
            loadAuthUser(_this.attr('authId'));
        }
    });
}
//切换权限的pc/app视图
function siwtchPlatform(own) {
    var $this = $(own);
    if (!$this.hasClass("selected")) {
        $this.addClass("selected");
        $this.siblings("p").removeClass("selected");
        
        if ($this.index() == 0) {
            $("#pcAuthList").show();
            $("#appAuthList").hide();
        } else {
            $("#appAuthList").show();
            $("#pcAuthList").hide();
        }
    }
}
//合拍协议选择事件
function coProductionChange(obj){
	if($(obj).val()==""){
		$("#coProMoney").parent().hide();
		$("#investmentRatio").parent().hide();
		$("#investmentRatio").val('');
	}else if($(obj).val()==0){
		$("#coProMoney").parent().hide();
		$("#investmentRatio").parent().hide();
		$("#investmentRatio").val('100%');
	}else if($(obj).val()==1){
		$("#coProMoney").parent().show();
		$("#investmentRatio").parent().show();
		$("#investmentRatio").val('');
	}
}
//验证剧组名称
function validateCrewName(value){
	if (value == "") {
        $("#crewNameErrorMsg").text("请填写剧组名称");
        return false;
    }
	return true;
}
//验证入组密码
function validateEnterPassword(value){
	if (value == "") {
        $("#enterPassMsg").text("请输入入组密码");
        return false;
    }
    
    if (!/^[0-9]{6}$/.test(value)) {
        $("#enterPassMsg").text("仅支持6位数字");
        return false;
    }
    return true;
}
//验证整数
function validateInteger(value){
	if(value==""){
		return true;
	}
	if(!/^[1-9]\d*$/.test(value)) {
		$("#seriesNoErrorMsg").text("请输入整数");
		return false;
	}
	return true;
}
//验证输入数字
function validateFigure(value,id){
	if(value==""){
		return true;
	}
	if(!/^[0-9]+(\.[0-9]+)?$/.test(value)) {
		$("#"+id+"ErrorMsg").text("请输入数字");
		return false;
	}
	return true;
}
function validateRatio(value){
	if(value==""){
		return true;
	}
	if(!/^[0-9]+(\.[0-9]+)?$/.test(value)) {
		$("#investmentRatioErrorMsg").text("请输入数字");
		return false;
	}
	if(value>100){
		$("#investmentRatioErrorMsg").text("最大值为100%");
		return false;
	}
	return true;
}

var uploader;

//修改剧组信息
function modifyCrew() {
    if (!validateCrewName($("#crewName").val()) 
    		|| !validateEnterPassword($("#enterPassword").val())
    		|| !validateInteger($("#seriesNo").val())
    		|| !validateFigure($("#budget").val().replace(/,/g,""),'budget')
    		|| !validateFigure($("#coProMoney").val().replace(/,/g,""),'coProMoney')
    		|| !validateRatio($("#investmentRatio").val().replace(/%/g,""))) {
        return false;
    }
    
    var crewName = $("#crewName").val();
    var crewType = $("#crewType").val();
    var projectType = $("#projectType").val();
    var subject = $("#subject").val();
    var seriesNo = $("#seriesNo").val();
    var company = $("#company").val();
    var status = $("#status").val();
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    var shootStartDate = $("#shootStartDate").val();
    var shootEndDate = $("#shootEndDate").val();
    var recordNumber = $("#recordNumber").val();
    var director = $("#director").val();
    var scriptWriter = $("#scriptWriter").val();
    var mainactor = $("#mainactor").val();
    var coProduction = $("#coProduction").val();
    var budget = $("#budget").val();
    var coProMoney = $("#coProMoney").val();
    var investmentRatio = $("#investmentRatio").val();
    var enterPassword = $("#enterPassword").val();
    var remark = $("#remark").val();
    
    var param = {};
    param.crewId = $("#crewId").val();
    param.crewName = crewName;
    param.crewType = crewType;
    param.projectType = projectType;
    param.subject = subject;
    param.seriesNo = seriesNo;
    param.company = company;
    param.status=status;
    param.startDate = startDate;
    param.endDate = endDate;
    param.shootStartDate = shootStartDate;
    param.shootEndDate = shootEndDate;
    param.recordNumber = recordNumber;
    param.director = director;
    param.scriptWriter = scriptWriter;
    param.mainactor = mainactor;
    param.coProduction=coProduction;
    param.budget = budget.replace(/,/g, "");
    if(coProduction==""){
    	param.coProMoney = '';
        param.investmentRatio='';
    }else if(coProduction==1) {
        param.coProMoney = coProMoney.replace(/,/g, "");
        param.investmentRatio=investmentRatio.replace(/%/g, "");
    }else if(coProduction==0){
        param.coProMoney = '';
        param.investmentRatio='100';
    }
    param.enterPassword = enterPassword;
    param.remark=remark;
    
    //是否将当前剧组图片设置为默认图片
    var picFlag = false;
	var li = $("#uploadFileList").find("li.upload-file-list-li");
	if(li.length != 0){
		 picFlag = true;
	}
	param.picFlag = picFlag;
	
    var url = '/crewManager/saveCrewByNormalUser';
    if(loginUserType==1){
    	url='/crewManager/saveCrewByAdmin';
    }
    $.ajax({
        url: url,
        type: "post",
        data: param,
        dataType: "json",
        success: function(response) {
            if (response.success) {
            	if (uploader.getFiles().length == 0) {
 	    			showSuccessMessage("保存成功");
 	    		} else {
 	    			uploader.option('formData', {
 	    				crewId: param.crewId
 		    		});
 		    		uploader.upload();
 	    		}
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}

//初始化上传插件
function initWebUploader(){
	uploader = WebUploader.create({  
      // 选完文件后，是否自动上传。  
      auto: false,  
      // 文件接收服务端。  
      server: '/crewManager/uploadCrewPicture',  
      timeout: 30*60*1000,//超时
      // 选择文件的按钮。可选。  
      // 内部根据当前运行是创建，可能是input元素，也可能是flash.  
      pick: {
      	id:'#uploadFileBtn',
      	multiple:true  //是否开起同时选择多个文件能力。
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