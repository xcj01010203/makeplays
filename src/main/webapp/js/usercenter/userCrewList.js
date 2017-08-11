$(document).ready(function(){
	//获取用户剧组列表
	getUserCrewList();
	
	$(".tab_wrap li").click(function(){
		if(!$(this).hasClass('tab_li_current')) {
			$(this).siblings().removeClass('tab_li_current');
			$(this).addClass('tab_li_current');
			$("#crewdiv").hide();
			$("#expiredCrewDiv").hide();
			if($(this).index()==0){
				$("#crewdiv").show();
			} else {
				$("#expiredCrewDiv").show();
			}
		}
	});
});

//获取用户剧组列表
function getUserCrewList(){
	$.ajax({
		url: '/crewManager/queryUserCrewList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var crewList = response.crewList;
				var expiredCrewList = response.expiredCrewList;
				if(crewList.length != 0 || expiredCrewList.length!=0){
					$("#emptyDiv").hide();
					$("#contentDiv").show();
				} else {
					$("#emptyDiv").show();
					$("#contentDiv").hide();
					return;
				}
				var currentId = '';
				var divId='';
				if(crewList.length != 0){
					var html=[];
					for(var i=0;i<crewList.length; i++){
						html.push("<div class='cast-list' id='"+crewList[i].crewId+"'>");
						html.push("  <img src='" + crewList[i].picPath + "'></img>");
						html.push("  <ul>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>剧组名称:</span>");
						html.push("      <span class='cast-list-content'>《" + crewList[i].crewName  + "》</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>制片公司:</span>");
						html.push("      <span class='cast-list-content produce-company'>" + crewList[i].company  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>影片题材:</span>");
						html.push("      <span class='cast-list-content'>" + crewList[i].subjectName  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>拍摄导演:</span>");
						html.push("      <span class='cast-list-content'>" + crewList[i].director  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>拍摄编剧:</span>");
						html.push("      <span class='cast-list-content'>" + crewList[i].scriptWriter  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>主演:</span>");
						html.push("      <span class='cast-list-content produce-company'>" + crewList[i].mainActorNames  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>进组密码:</span>");
						html.push("      <span class='cast-list-content'>" + crewList[i].enterPassword  + "</span>");
						html.push("    </li>");
						html.push("  </ul>");
						if(crewList[i].isStop==1) {
							html.push("  <span class='now-crew-settle frozen-status'>停用</span>");
						} else {
							if(crewList[i].crewUserStatus == 1 || crewList[i].crewUserStatus == 3){
								if(crewList[i].crewId!=currentCrewId) {
									html.push("<input type='button' class='switch-crew-btn' onclick='switchCrewList(\""+ crewList[i].crewId +"\")' value='设置为当前剧组' />");
								} else {
									html.push("  <span class='now-crew-settle current-status'>当前剧组</span>");
								}
							}
							if(crewList[i].crewUserStatus == 2){
								html.push("  <span class='now-crew-settle auduting-status'>审核中...</span>");
							}
							if(crewList[i].crewUserStatus == 99){
								html.push("  <span class='now-crew-settle frozen-status'>冻结</span>");
							}
						}
						if(crewList[i].crewId==currentCrewId){
							currentId = crewList[i].crewId;
							divId='crewdiv';
						}
						
						html.push("</div>");
					}
					$("#crewdiv").append(html.join(""));
				}
				//过期剧组 
				if(expiredCrewList.length!=0) {
					var html=[];
					for(var i=0; i<expiredCrewList.length; i++){
						var obj=expiredCrewList[i];
						html.push("<div class='cast-list' id='"+obj.crewId+"'>");
						html.push("  <img src='" + obj.picPath + "'></img>");
						html.push("  <ul>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>剧组名称:</span>");
						html.push("      <span class='cast-list-content'>《" + obj.crewName  + "》</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>制片公司:</span>");
						html.push("      <span class='cast-list-content produce-company'>" + obj.company  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>影片题材:</span>");
						html.push("      <span class='cast-list-content'>" + obj.subjectName  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>拍摄导演:</span>");
						html.push("      <span class='cast-list-content'>" + obj.director  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>拍摄编剧:</span>");
						html.push("      <span class='cast-list-content'>" + obj.scriptWriter  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>主演:</span>");
						html.push("      <span class='cast-list-content produce-company'>" + obj.mainActorNames  + "</span>");
						html.push("    </li>");
						html.push("    <li>");
						html.push("      <span class='cast-list-title'>进组密码:</span>");
						html.push("      <span class='cast-list-content'>" + obj.enterPassword  + "</span>");
						html.push("    </li>");
						html.push("  </ul>");
						if(obj.isStop==1) {
							html.push("  <span class='now-crew-settle frozen-status'>停用</span>");
						}else{
							if(obj.crewUserStatus == 1 || obj.crewUserStatus == 3){
								if(obj.crewId!=currentCrewId) {
									html.push("<input type='button' class='switch-crew-btn' onclick='switchCrewList(\""+ obj.crewId +"\")' value='设置为当前剧组' />");
								} else {
									html.push("  <span class='now-crew-settle current-status'>当前剧组</span>");
								}
							}
							if(obj.crewUserStatus == 2){
								html.push("  <span class='now-crew-settle auduting-status'>审核中...</span>");
							}
							if(obj.crewUserStatus == 99){
								html.push("  <span class='now-crew-settle frozen-status'>冻结</span>");
							}
						}
						if(obj.crewId==currentCrewId){
							currentId = obj.crewId;
							divId='expiredCrewDiv';
							//激活tab页
							$("#expiredCrewTab").trigger('click');
						}
						
						html.push("</div>");
					}
					$("#expiredCrewDiv").append(html.join(""));
				}
				//页面滚动到当前剧组处
				if(currentId) {
					$("#"+divId).scrollTop($("#"+divId).find("#"+currentId).offset().top-250);
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//切换剧组
function switchCrewList(crewId){
	if(loginUserType == 3) {//普通用户切换剧组
		$.ajax({
			url: '/userManager/switchCrew',
			type: 'post',
			data: {crewId: crewId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.window.location.href="/userManager/toUserCenterPage?activeTagType=3";
					showSuccessMessage("操作成功");
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	} else {//客服切换剧组
		$.ajax({
			url: '/userManager/switchCrewForCustomerService',
			type: 'post',
			data: {crewId: crewId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.window.location.href="/userManager/toUserCenterPage?activeTagType=3";
					showSuccessMessage("操作成功");
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}
}
