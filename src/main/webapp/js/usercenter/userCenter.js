$(document).ready(function() {
//	$("#rightContentDiv").load("/userManager/toUserDetailInfoPage");
//	$("#rightContentIframe").hide();
//	$("#rightContentDiv").show();

	setUserImg();
	
	var activeTagType = $("#activeTagType").val();
	showRightDiv(activeTagType);
	//显示未读消息数量
	loadUnReadMessageNum();
	//客服，不显示新建/加入剧组
	if(loginUserType==2 || loginUserType==4) {
		$("#joinCrew").parent().remove();
	}
});

//设置用户头像
function setUserImg() {
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var userInfo = response.userInfo;
				if(userInfo != null){
					$("#userImg").attr("src", userInfo.bigImgUrl);
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

/**
 * 显示右侧面板
 * @param type 1-我的信息  2-我的消息  3-我的剧组  4-用户协议  5-意见反馈  6-关于我们
 */
function showRightDiv(type) {
	$("a").removeClass("active");
	
	var url = "";
	if (type == 1) {
		url = "/userManager/toUserDetailInfoPage";
		
		$("#rightContentDiv").load(url);
		$("#rightContentIframe").hide();
		$("#rightContentDiv").show();

		$("#userDetail").addClass("active");
	}
	if (type == 2) {
		url = "/messageInfoManager/toMessagePage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#myMessage").addClass("active");
	}
	if (type == 3) {
		url = "/crewManager/toUserCrewListPage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#myCrewList").addClass("active");
	}
	if (type == 4) {
		url = "/userManager/toAgreementPage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#userAgreement").addClass("active");
	}
	if (type == 5) {
		url = "/feedbackManager/toFeedbackPage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#feedback").addClass("active");
	}
	if (type == 6) {
		url = "/userManager/toAboutUsPage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#aboutUs").addClass("active");
	}
	//新建、加入剧组
	if (type == 7) {
		url = "/crewManager/toJoinCrewPage";
		$("#rightContentIframe").attr("src", url);
		$("#rightContentDiv").hide();
		$("#rightContentIframe").show();

		$("#joinCrew").addClass("active");
	}
	
}
//加载未读消息数量
function loadUnReadMessageNum() {
	$.ajax({
		url: '/messageInfoManager/queryUnReadMessageNum',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.num && response.num != 0) {
					$(".unReadNum").html('('+response.num+')');
				}else{
					$(".unReadNum").html('');
				}
			}else{
				showErrorMessage(response.message);
			}   
		}
	});
}
