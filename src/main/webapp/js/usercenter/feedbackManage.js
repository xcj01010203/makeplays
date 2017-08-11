var filter={};

String.prototype.trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ''); 
};
$(function(){
	//加载反馈用户列表
	loadFeedBackUserList();	
	//初始化滚动条事件
	initScrollEvent();
    //查询弹窗
    $('#queryWindow').jqxWindow({
		width: 480,
        height: 300,
		resizable: false, 
        autoOpen: false,
        isModal: true,
        cancelButton: $('#closeSearchSubmit'),
        initContent: function () {
        }
	});
});

filter.pageSize = 20;
filter.pageNo = 1;
var continueFlag = true;
//加载反馈用户列表
function loadFeedBackUserList(){
	$.ajax({
		url: '/feedbackManager/queryFeedBackUserList',
		type: 'post',
		datatype: 'json',
		data:filter,
		success: function(response){
			if(response.success){
				var feedBackUserList = response.result;
				var html = [];
				if(feedBackUserList && feedBackUserList.length>0) {
					for(var i=0;i<feedBackUserList.length;i++){
						var obj = feedBackUserList[i];
						html.push('<ul class="user-ul" id="ul_'+obj.userId+'" onclick="showUserFeedbackList(\''+obj.userId+'\')">');
						html.push('	<li class="user-li">');
						if(obj.hasNewStatus) {
							html.push('		<div class="tips active"></div>');
						} else {
							html.push('		<div class="tips"></div>');
						}
						if(obj.userName && obj.userName.trim()){
							html.push('		<div class="user_name" title="'+obj.userName+'">'+obj.userName+'</div>');
						}else{
							html.push('		<div class="user_name">&nbsp;</div>');
						}
						if(obj.phone && obj.phone.trim()){
							html.push('		<div class="user_contact">'+obj.phone+'</div>');
						}else{
							html.push('		<div class="user_contact">&nbsp;</div>');
						}
						html.push('		<div class="user_num">'+obj.feedbackNum+'条</div>');
						html.push('		<div class="user_time">'+obj.statusUpdateTime+'</div>');
						html.push('	</li>');
						html.push('</ul>');
					}
				} else {
					continueFlag = false;
					if(filter.pageNo == 1) {
						html.push('<div class="empty-div" id="emptyDiv">暂无反馈用户</div>');
					}
				}
				if(filter.pageNo == 1) {
					$("#userList").html(html.join(''));
				}else{
					$("#userList").append(html.join(''));
				}				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}
//显示用户反馈列表
function showUserFeedbackList(userId) {
	//初始化查询条件
	subData.userId = userId;
	subData.pageSize = 10;
	subData.pageNo = 1;
	continueFlagForFeedBack=true;
	$("#user-div").hide();
	$("#feedback-div").show();
	$("#feedbackList").scrollTop(0);
	//加载用户反馈信息列表，同时，更新用户反馈和回复状态为已读
	loadUserFeedBackList();	
	//初始化滚动条事件
	initScrollEventForFeedBack();
}
//清空查询条件
function clearSearchCon(){
	$("input[name='searchStatus']").prop('checked',false);
	$(".search-class").val('');
}
//查询
function query() {
	var userName = $("#searchName").val();
	var status=$("input[name='searchStatus']:checked").val();
	var startTime =$("#searchStartTime").val();
	var endTime = $("#searchEndTime").val();
	
	if(userName!= null && userName!=""){
		filter.userName=userName;
	}else{
		filter.userName="";
	}
	if(status!= null && status!=""){
		filter.status=status;
	} else {
		filter.status="";
	}
	if(startTime!= null && startTime!=""){
		filter.startTime=startTime;
	}else{
		filter.startTime="";
	}	
	if(endTime!= null && endTime!=""){
		filter.endTime=endTime;
	}else{
		filter.endTime="";
	}

	loadFeedBackUserList();
	
	$('#queryWindow').jqxWindow('close');
}


//初始化滚动条事件
function initScrollEvent(){
	var nScrollHight = 0; //滚动距离总长(注意不是滚动条的长度)
    var nScrollTop = 0;   //滚动到的当前位置
    var nDivHight = $("#userList").height();
	$("#userList").scroll(function(){
		if(!continueFlag) {
			return;
		}
		nScrollHight = $(this)[0].scrollHeight;
        nScrollTop = $(this)[0].scrollTop;
        var paddingBottom = parseInt( $(this).css('padding-bottom') ),paddingTop = parseInt( $(this).css('padding-top') );
        if(nScrollTop + paddingBottom + paddingTop + nDivHight >= nScrollHight){
        	filter.pageSize = 20;
        	filter.pageNo = parseInt(filter.pageNo) + 1;
        	loadFeedBackUserList();
        }
	});
}

//显示搜索框
function showSearchWin(){
	$('#queryWindow').jqxWindow('open');
}
//清空查询条件
function clearSearchCon(){
	$(".search-text").val('');
	$("input[name='searchStatus']").prop('checked',false);
}
//查询
function query(){
	var content=$("#content").val();
	if(content){
		filter.content=content;
	}else{
		filter.content='';
	}
	var userName=$("#userName").val();
	if(userName){
		filter.userName=userName;
	}else{
		filter.userName='';
	}
	var searchStatus=$("input[name='searchStatus']:checked").val();
	if(searchStatus){
		filter.status=searchStatus;
	}else{
		filter.status='';
	}
	var searchStartTime=$("#searchStartTime").val();
	if(searchStartTime){
		filter.startTime=searchStartTime;
	}else{
		filter.startTime='';
	}
	var searchEndTime=$("#searchEndTime").val();
	if(searchEndTime){
		filter.endTime=searchEndTime;
	}else{
		filter.endTime='';
	}
	filter.pageSize = 20;
	filter.pageNo = 1;
	continueFlag = true;
	$("#userList").scrollTop(0);
	loadFeedBackUserList();
	$('#queryWindow').jqxWindow('close');
}

/***************以下JS是反馈列表****************/
var subData = {};
var continueFlagForFeedBack = true;
//加载用户反馈信息列表
function loadUserFeedBackList(){
	$.ajax({
		url: "/feedbackManager/queryUserFeedBackList",
		type: "post",
		dataType: "json",
		data: subData,
		success: function(response) {
			if (response.success) {

				//去掉新消息提醒
				if($("#ul_"+subData.userId).find(".tips").hasClass('active')){
					$("#ul_"+subData.userId).find(".tips").removeClass('active');
				}
				
				var feedBackList = response.result;
				var html = [];
				if(feedBackList && feedBackList.length>0) {
					for(var i=0;i<feedBackList.length;i++){
						var obj = feedBackList[i];
						html.push('<ul class="feedreply-ul" id="ul_'+obj.feedbackId+'">');
						html.push('	<li class="userinfo-li">');
						if(obj.userName) {
							var userName=obj.userName;
							if(obj.phone && obj.phone.trim()){
								userName+='('+obj.phone+')';
							}
							html.push('		<div class="userinfo_name" title="'+userName+'">'+userName+'</div>');
						}else if(obj.phone){
							html.push('		<div class="userinfo_name" title="'+obj.phone+'">'+obj.phone+'</div>');
						}else{
							html.push('		<div class="userinfo_name">&nbsp;</div>');
						}
						if(obj.contact) {
							html.push('		<div class="userinfo_contact">'+obj.contact+'</div>');
						}else{
							html.push('		<div class="userinfo_contact">&nbsp;</div>');
						}
						var terminal='';
						if(obj.clientType==0){
							terminal='pc';
						}else if(obj.clientType==1){
							terminal='ios';
						}else if(obj.clientType==2){
							terminal='android';
						}
						html.push('		<div class="userinfo_time">'+obj.createTime+'<span class="teminal_'+terminal+' big"></span></div>');
						html.push('	</li>');
						html.push('	<li class="message-li">');
						html.push(obj.message);
						html.push('	</li>');
						for(var j=0;j<obj.replyList.length;j++){
							var reply=obj.replyList[j];
							html.push('	<li class="reply-li">');
							if(reply.replyUserId==obj.userId) {
								html.push('		<div class="reply_content"><span class="reply_name">'+reply.replyUserName+'</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
							}else{
								html.push('		<div class="reply_content customer"><span class="reply_name">客服('+reply.replyUserName+')</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
							}
							var replyTerminal='';
							if(reply.replyClientType==0){
								replyTerminal='pc';
							}else if(reply.replyClientType==1){
								replyTerminal='ios';
							}else if(reply.replyClientType==2){
								replyTerminal='android';
							}
							html.push('		<div class="reply_time">'+reply.replyTime+'<span class="teminal_'+replyTerminal+'"></span></div>');
							html.push('	</li>');
						}
						html.push('	<li class="oper-li">');
						html.push('		<textarea class="reply-textarea" id="reply_'+obj.feedbackId+'" rows="1" cols="10" placeholder="请输入回复内容"></textarea>');
						html.push('		<input type="button" class="reply-button" feedBackId="'+obj.feedbackId+'" aimUserId="'+obj.userId+'" value="回复" onclick="reply(this)">');
						html.push('	</li>');
						html.push('</ul>');
					}
				}else{
					continueFlagForFeedBack = false;
				}
				if(subData.pageNo==1){
					$("#feedbackList").html(html.join(''));
				}else{
					$("#feedbackList").append(html.join(''));
				}
				$(".reply-textarea").click(function(){
					$(".reply-textarea").attr('rows','1');
					$(".reply-textarea").next().hide();
					$(this).attr('rows','3');
					$(this).next().show();
					stopPropagation();
				});
				$(document).click(function(){
					$(".reply-textarea").attr('rows','1');
					$(".reply-textarea").next().hide();
				});
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}
//用户回复
function reply(obj){
	var feedBackId=$(obj).attr('feedBackId');
	var aimUserId=$(obj).attr('aimUserId');
	var replyContent = $("#reply_"+feedBackId).val();
	if(!replyContent) {
		return;
	}
	
	$.ajax({
		url: '/feedbackManager/saveReplyInfo',
		type: 'post',
		datatype: 'json',
		data:{feedBackId:feedBackId,reply:replyContent,aimUserId:aimUserId},
		success: function(response){
			if(response.success){
				$("#ul_"+feedBackId).find('.reply-li').remove();
				var html=[];
				for(var i=0;i<response.replyList.length;i++){
					var reply=response.replyList[i];
					html.push('	<li class="reply-li">');
					if(reply.replyUserId==aimUserId) {
						html.push('		<div class="reply_content"><span class="reply_name">'+reply.replyUserName+'</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
					}else{
						html.push('		<div class="reply_content customer"><span class="reply_name">客服('+reply.replyUserName+')</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
					}
					html.push('		<div class="reply_time">'+reply.replyTime+'</div>');
					html.push('	</li>');
				}
				$("#ul_"+feedBackId).find('.message-li').after(html.join(''));
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});

	$("#reply_"+feedBackId).val('');
	$(obj).prev().attr('rows','1');
	$(obj).hide();
	stopPropagation();
}

//返回
function goback(){
	$("#feedback-div").hide();
	$("#user-div").show();
}

//初始化滚动条事件
function initScrollEventForFeedBack(){
	var nScrollHight = 0; //滚动距离总长(注意不是滚动条的长度)
    var nScrollTop = 0;   //滚动到的当前位置
    var nDivHight = $("#feedbackList").height();
	$("#feedbackList").scroll(function(){
		if(!continueFlagForFeedBack) {
			return;
		}
		nScrollHight = $(this)[0].scrollHeight;
        nScrollTop = $(this)[0].scrollTop;
        var paddingBottom = parseInt( $(this).css('padding-bottom') ),paddingTop = parseInt( $(this).css('padding-top') );
        if(nScrollTop + paddingBottom + paddingTop + nDivHight >= nScrollHight){
        	subData.pageSize = 10;
        	subData.pageNo = parseInt(subData.pageNo) + 1;
        	loadUserFeedBackList();
        }
	});
}

//阻止冒泡方法
function stopPropagation(e) {
  e = e || window.event;
  if(e.stopPropagation) { //W3C阻止冒泡方法
      e.stopPropagation();
  } else {
      e.cancelBubble = true; //IE阻止冒泡方法
  }
}