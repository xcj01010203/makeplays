// JavaScript Document
$(document).ready(function(){
	
	//加载通告单数据
  loadNoticeData();

  //初始化swiper
   /* var mySwiper = new Swiper('.swiper-container',{
	  	preventLinksPropagation : false,
	});*/
  
});



//显示基本信息
function showBasicInfo(own){
	var $this = $(own);
	$this.siblings("li").removeClass("click");
	$this.addClass("click");
	//$("#mainContent").empty();
	//隐藏其他两个
	$("#noticeAllContent").hide().animate({"left":"1000px"}, 100);
	$("#scenceInfoContent").hide().animate({"left":"1000px"},100);
	$("#basicInfo").show().animate({"left":"0px"}, 100);	
}

//显示场次信息
function showScenceInfo(own){
	var $this = $(own);
	$this.siblings("li").removeClass("click");
	$this.addClass("click");
	$("#noticeAllContent").hide().animate({"left":"1000px"}, 100);
	$("#basicInfo").hide().animate({"left":"1000px"},100);
	$("#scenceInfoContent").show().animate({"left":"0px"}, 100);
}

//显示隐藏的场次信息
function showScenceInfoContent(own){
	
	var $this = $(own);
	$this.parents("div.scence-info-list").find("ul.scence-info-content").toggle(function(){
		var className = $this.find("i.sign");
		
		if(className.length != 0){
			$this.find("i.sign").removeClass("sign").addClass("sign1");
		}else{
			$this.find("i.sign1").removeClass("sign1").addClass("sign");
		}
	});
	$this.parents("div.scence-info-list").siblings("div.scence-info-list").find("ul.scence-info-content").hide();
	$this.parents("div.scence-info-list").siblings("div.scence-info-list").find("i.sign1").removeClass("sign1").addClass("sign");
}

//显示通告全文
function showNoticeAllContent(own){
	var $this = $(own);
	$this.siblings("li").removeClass("click");
	$this.addClass("click");
	$("#basicInfo").hide().animate({"left":"1000px"},100);
	$("#scenceInfoContent").hide().animate({"left":"1000px"}, 100);
	$("#noticeAllContent").show().animate({"left":"0px"}, 100);
}


//加载通告单数据
function loadNoticeData(){
	
	var userId = $("#userId").val();
	var crewId = $("#crewId").val();
	var noticeId = $("#noticeId").val();
	/*var userId = "430b5e09d23d4776b9ee6440423a1699";
	var crewId = "fb3f6c2be1c04a76ac7ac84346e840de";
	var noticeId = "88b880fee3e9410b9dbfda5c459e693a";*/
 $.ajax({
		url: '/notice/appIndex/queryNoticeShareData?userId='+userId+'&crewId='+ crewId +'&noticeId='+noticeId,
        type: "post",
        async: false,
        datatype: 'json',
		success: function(response){
			if (!response.success) {
				alert(response.message);
				return;
			}
			
			if(response.success){
				/*基本信息*/
				//演员通告单信息
				var actorNoticeInfo = response.actorNoticeInfo;
				if(actorNoticeInfo != null){
					$("#actorPersonNotice").show();
					if(actorNoticeInfo.roleNames != null && actorNoticeInfo.roleNames != ""){
						$("#roleNames").html(actorNoticeInfo.roleNames);
					}else{
						$("#roleNames").html("");
					}
					if(actorNoticeInfo.viewNos != null && actorNoticeInfo.viewNos != ""){
						$("#viewNos").text(actorNoticeInfo.viewNos);
					}else{
						$("#viewNos").text("");
					}
					if(actorNoticeInfo.converLocationInfo != null && actorNoticeInfo.converLocationInfo != ""){
						$("#converLocationInfo").text(actorNoticeInfo.converLocationInfo);
					}else{
						$("#converLocationInfo").text("");
					}
					if(actorNoticeInfo.cooperators != null && actorNoticeInfo.cooperators != ""){
						$("#cooperators").text(actorNoticeInfo.cooperators);
					}else{
						$("#cooperators").text(actorNoticeInfo.cooperators);
					}
					if(actorNoticeInfo.makeup != null && actorNoticeInfo.makeup != ""){
						$("#makeup").text(actorNoticeInfo.makeup);
					}else{
						$("#makeup").text("");
					}
					if(actorNoticeInfo.arriveTime != null && actorNoticeInfo.arriveTime != ""){
						$("#arriveTime").text(actorNoticeInfo.arriveTime);
					}else{
						$("#arriveTime").text("");
					}
					if(actorNoticeInfo.giveMakeupTime != null && actorNoticeInfo.giveMakeupTime != ""){
						$("#giveMakeupTime").text(actorNoticeInfo.giveMakeupTime);
					}else{
						$("#giveMakeupTime").text("");
					}
					
					
				}else{
					$("#actorPersonNotice").hide();
				}
				//通告概况
				var noticeTime = response.noticeTime;
				var pictureInfo = response.noticeTime.pictureInfo;
				if(noticeTime != null){
					if(noticeTime.version != null && noticeTime.version != ""){
						$("#version").text(noticeTime.version);
					}else{
						$("#version").text("");
					}
					if(noticeTime.noticeTimeUpdateTime != null && noticeTime.noticeTimeUpdateTime != ""){
						$("#noticeTimeUpdateTime").text(noticeTime.noticeTimeUpdateTime);
					}else{
						$("#noticeTimeUpdateTime").text("");
					}
					if(noticeTime.groupDirector != null && noticeTime.groupDirector != ""){
						$("#groupDirector").text(noticeTime.groupDirector);
					}else{
						$("#groupDirector").text("");
					}
					if(noticeTime.statistics != null && noticeTime.statistics != ""){
						$("#statistics").append(noticeTime.totalViewnum + "场/" + noticeTime.totalPagenum + "页<br>");
						$("#statistics").append(noticeTime.statistics.replace(/\n/g,"<br>"));
					}else{
						$("#statistics").html("");
					}
					if(noticeTime.shootLocationInfos != null && noticeTime.shootLocationInfos != ""){
						$(".shoot-location").text(noticeTime.shootLocationInfos);
					}else{
						$(".shoot-location").text("");
					}
					if(noticeTime.breakfastTime != null && noticeTime.breakfastTime != ""){
						$("#breakfastTime").text(noticeTime.breakfastTime);
					}else{
						$("#breakfastTime").text("");
					}
					if(noticeTime.departureTime != null && noticeTime.departureTime != ""){
						$("#departureTime").text(noticeTime.departureTime);
					}else{
						$("#departureTime").text("");
					}
					if(noticeTime.noticeContact != null && noticeTime.noticeContact != ""){
						$("#contactList").html(noticeTime.noticeContact.replace(/\n/g,"<br>"));
					}else{
						$("#contactList").html("");
					}
					
					
					//提示
					if(noticeTime.note != null && noticeTime.note != ""){
						$("#note").html(noticeTime.note);
					}else{
						$("#note").html("");
					}
					if(noticeTime.roleInfo != null && noticeTime.roleInfo != ""){
						$("#roleInfo").html(noticeTime.roleInfo);
					}else{
						$("#roleInfo").html("");
					}
					if(noticeTime.otherTips != null && noticeTime.otherTips != ""){
						$("#otherTips").html(noticeTime.otherTips);
					}else{
						$("#otherTips").html("");
					}
					if(noticeTime.remark != null && noticeTime.remark != ""){
						$("#remark").html(noticeTime.remark);
					}else{
						$("#remark").html("");
					}
					if(noticeTime.insideAdvert != null && noticeTime.insideAdvert != ""){
						$("#insideAdvert").html(noticeTime.insideAdvert);
					}else{
						$("#insideAdvert").html("");
					}
					
				}
				
				//化妆信息
				var noticeRoleTimeList = response.noticeRoleTimeList;
				if(noticeRoleTimeList.length != 0){
					$("#noticeRoleTimeList tbody").empty();
					var html =[];
					for(var i= 0; i< noticeRoleTimeList.length; i++){
						html.push("<tr>");
						html.push("<td>" + noticeRoleTimeList[i].viewRoleName + "</td>");
						if(noticeRoleTimeList[i].actorName == null){
							html.push("<td></td>");
						}else{
							html.push("<td>" + noticeRoleTimeList[i].actorName + "</td>");
						}
						if(noticeRoleTimeList[i].arriveTime == null){
							html.push("<td></td>");
						}else{
							html.push("<td>" + noticeRoleTimeList[i].arriveTime + "</td>");
						}
						if(noticeRoleTimeList[i].makeup == null){
							html.push("<td></td>");
						}else{
							html.push("<td>" + noticeRoleTimeList[i].makeup + "</td>");
						}
						if(noticeRoleTimeList[i].giveMakeupTime == null){
							html.push("<td></td>");
						}else{
							html.push("<td>" + noticeRoleTimeList[i].giveMakeupTime + "</td>");
						}
						
						html.push("</tr>");
					}
					$("#noticeRoleTimeList tbody").append(html.join(""));
				}
				
				/*场次信息*/
				
				var locationViewList = response.locationViewList;
				if(locationViewList.length != 0){
					$("#scenceInfoContent").empty();
					
					for(var j= 0; j< locationViewList.length; j++){
						var viewInfoList = locationViewList[j].viewInfoList;
						if(viewInfoList.length != 0){
							for(var i= 0; i< viewInfoList.length; i++){
								if(viewInfoList[i].seriesNo == "" || viewInfoList[i].seriesNo== null){
									viewInfoList[i].seriesNo = "";
								}
								if(viewInfoList[i].viewNo == "" || viewInfoList[i].viewNo== null){
									viewInfoList[i].viewNo = "";
								}
								if(viewInfoList[i].viewLocation == "" || viewInfoList[i].viewLocation == null){
									viewInfoList[i].viewLocation = "";
								}
								if(viewInfoList[i].atmosphereName == "" || viewInfoList[i].atmosphereName == null){
									viewInfoList[i].atmosphereName = "";
								}
								if(viewInfoList[i].site == "" || viewInfoList[i].site == null){
									viewInfoList[i].site = "";
								}
								if(viewInfoList[i].pageCount == "" || viewInfoList[i].pageCount == null){
									viewInfoList[i].pageCount = "";
								}
								if(viewInfoList[i].mainRoleNames == "" || viewInfoList[i].mainRoleNames == null){
									viewInfoList[i].mainRoleNames = "";
								}
								if(viewInfoList[i].guestRoleNames == "" || viewInfoList[i].guestRoleNames == null){
									viewInfoList[i].guestRoleNames="";
								}
								if(viewInfoList[i].massRoleNames == "" || viewInfoList[i].massRoleNames == null){
									viewInfoList[i].massRoleNames = "";
								}
								var html = [];
								html.push('<div class="scence-info-list">');
								html.push(' <h6 class="scence-info-title" onclick="showScenceInfoContent(this)">');
								html.push('  <p><i>'+ viewInfoList[i].seriesNo +'-'+ viewInfoList[i].viewNo +'</i>'+ viewInfoList[i].viewLocation +'&nbsp;&nbsp;'+ viewInfoList[i].atmosphereName +'&nbsp;&nbsp;'+ viewInfoList[i].site +'</p><i class="sign"></i>');
								html.push(' </h6>');
								html.push('  <ul class="scence-info-content">');
								html.push('   <li>');
								html.push('    <span>场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景:</span>');
								html.push('    <span class="scence-name">'+ viewInfoList[i].viewLocation +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>气氛内外:</span>');
								html.push('    <span class="atmo-sphere-ame">'+ viewInfoList[i].atmosphereName +'&nbsp;&nbsp;'+ viewInfoList[i].site +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;量:</span>');
								html.push('    <span class="page-count">'+ viewInfoList[i].pageCount +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>主要演员:</span>');
								html.push('    <span class="main-actor">'+ viewInfoList[i].mainRoleNames +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>特&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;约:</span>');
								html.push('    <span class="special-actor">'+ viewInfoList[i].guestRoleNames +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>群&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;众:</span>');
								html.push('    <span class="public-actor">'+ viewInfoList[i].massRoleNames +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>服&nbsp;化&nbsp;&nbsp;道:</span>');
								var clothesNames = "";
								if(viewInfoList[i].clothesNames == null || viewInfoList[i].clothesNames == ""){
									clothesNames = "";
								}else{
									clothesNames = viewInfoList[i].clothesNames;
								}
								var makeupNames = "";
								if(viewInfoList[i].makeupNames == null || viewInfoList[i].makeupNames == ""){
									makeupNames = "";
								}else{
									makeupNames = '/' + viewInfoList[i].makeupNames;
								}
								var propNames = "";
								if(viewInfoList[i].propNames == null || viewInfoList[i].propNames == ""){
									propNames = "";
								}else{
									propNames = '/' + viewInfoList[i].propNames;
								}
								/*html.push('    <span class="">'+ viewInfoList[i].clothesNames+ '/' + viewInfoList[i].makeupNames + '/' + viewInfoList[i].propNames +'</span>');*/
								html.push('    <span class="">'+ clothesNames + makeupNames + propNames +'</span>');
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>主要内容:</span>');
								if(viewInfoList[i].mainContent == null){
									html.push('    <span class="main-content"></span>');
								}else{
									html.push('    <span class="main-content">'+ viewInfoList[i].mainContent +'</span>');
								}
								
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:</span>');
								if(viewInfoList[i].remark == null){
									html.push('    <span class="scence-remark"></span>');
								}else{
									html.push('    <span class="scence-remark">'+ viewInfoList[i].remark +'</span>');
								}
								
								html.push('   </li>');
								
								html.push('   <li>');
								html.push('    <span>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植:</span>');
								if(viewInfoList[i].insertAdverts == null){
									html.push('    <span class="insert-adverts"></span>');
								}else{
									html.push('    <span class="insert-adverts">'+ viewInfoList[i].insertAdverts +'</span>');
								}
								
								html.push('   </li>');
								
								html.push(' </ul>');
								html.push('</div>');
								
								$("#scenceInfoContent").append(html.join(""));
							}
						}
						
					}
				}
			
				//通告单图片信息
				if(pictureInfo.length != 0){
					
					//$("#noticePicList").empty();
					
					$(".my-gallery").empty();
					/*for(var i= 0; i< 3; i++){
						var bigHtml = [];
						if(i==0){
							bigHtml.push('<div class="swiper-slide swiper-slide-active" style="width: 800px">');
							bigHtml.push(' <img src="http://192.168.10.250:8080//fileManager/previewAttachment?address=/u6/xiaotu-app/makeplays_attachment/notice//20161110//small/88edd654d762486592504a2f30c22176.jpg_small.png">');
							bigHtml.push('</div');
						}else if(i>0 && i< 3 - 1){
							bigHtml.push('<div class="swiper-slide swiper-slide-next" style="width: 800px">');
							bigHtml.push(' <img src="http://192.168.10.250:8080//fileManager/previewAttachment?address=/u6/xiaotu-app/makeplays_attachment/notice//20161110//small/88edd654d762486592504a2f30c22176.jpg_small.png">');
							bigHtml.push('</div');
						}else{
							bigHtml.push('<div class="swiper-slide" style="width: 800px">');
							bigHtml.push(' <img src="http://192.168.10.250:8080//fileManager/previewAttachment?address=/u6/xiaotu-app/makeplays_attachment/notice//20161110//small/88edd654d762486592504a2f30c22176.jpg_small.png">');
							bigHtml.push('</div');
						}
						
						
					}
					//小图片
					for(var i= 0; i< pictureInfo.length; i++){
						var html = [];
						
						html.push('<div class="notice-pic-con">');
						html.push('<img src="http://192.168.10.250:8080//fileManager/previewAttachment?address=/u6/xiaotu-app/makeplays_attachment/notice//20161110//small/88edd654d762486592504a2f30c22176.jpg_small.png"');
						html.push('</div>');
						
					}*/
					
					for(var i= 0; i< pictureInfo.length; i++){
						var html = [];
						html.push('<figure>');
						html.push('<a href="'+ pictureInfo[i].bigPicurl +'" data-size="800x1142">');
						html.push('<img src="'+ pictureInfo[i].smallPicurl +'" />');
						html.push('</a>');
						html.push('</figure>');
						$(".my-gallery").append(html.join(""));
					}
					
					
					
				}
				
				
			}
		},
		error: function(message){
		   console.log(message);
		}
		
	});
	
	 
}


//关闭下载
function closeDownUpload(){
	$(".bottom-download").hide();
	$("#mainContent").css("margin-bottom","0px");
}



//显示大图片
function showBigPicture(own){
	$("#noticePicList").hide();
	$("#noticePicBigList").show();
}


//隐藏大图片
function hideBigPicSwiper(own){
	var $this = $(own);
	$("#noticePicBigList").hide();
	$("#noticePicList").show();
}

//下载app
function downloadApp() {
	window.location.href="/downLoadAppMananger/appIndex/toDownAppPage";
}