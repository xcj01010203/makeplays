$(function(){
	//加载历史反馈列表
	loadFeedBackList();
	initLoad();
});
//保存反馈信息
function saveFeedBackInfo() {
	var message = $("#message").val();
	var contact = $("#contact").val();
	
	if (message == null || message == "") {
		return;
	}
	
	$.ajax({
		url: "/feedbackManager/saveFeedBackInfo",
		type: "post",
		dataType: "json",
		data: {message: message, contact: contact},
		success: function(response) {
			if (response.success) {
				parent.showSuccessMessage("谢谢您的建议");
				$("#message").val(null);
				$("#contact").val(null);
				//重新加载反馈列表
				subData.pageSize=10;
				subData.pageNo=1;
				continueflag=true;
				$(".list-div").scrollTop(0);
				loadFeedBackList();
			}
		}
	});
}
var subData = {};
subData.pageSize = 10;
subData.pageNo = 1;
var continueflag = true;
//加载历史反馈列表
function loadFeedBackList(){
	$.ajax({
		url: "/feedbackManager/queryUserFeedBackList",
		type: "post",
		data: subData,
		dataType: "json",
		success: function(response) {
			if (response.success) {
				var feedBackList = response.result;
				var html = [];
				if(feedBackList && feedBackList.length>0) {
					for(var i=0;i<feedBackList.length;i++){
						var obj = feedBackList[i];
						var contact = obj.contact || "";
						
						html.push('<ul class="feedreply-ul" id="ul_'+obj.feedbackId+'">');
						html.push('	<li class="userinfo-li">');
						html.push('		<div class="userinfo_name">'+obj.userName+'</div>');
						html.push('		<div class="userinfo_contact">'+ contact +'</div>');
						html.push('		<div class="userinfo_time">'+obj.createTime+'</div>');
						html.push('	</li>');
						html.push('	<li class="message-li">');
						html.push(obj.message);
						html.push('	</li>');
						for(var j=0;j<obj.replyList.length;j++){
							var reply=obj.replyList[j];
							html.push('	<li class="reply-li">');
							if(reply.replyUserId==currentUserId) {
								html.push('		<div class="reply_content user"><span class="reply_name">'+reply.replyUserName+'</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
							}else{
								html.push('		<div class="reply_content customer"><span class="reply_name">客服('+reply.replyUserName+')</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
							}
							html.push('		<div class="reply_time">'+reply.replyTime+'</div>');
							html.push('	</li>');
						}
						html.push('	<li class="oper-li">');
						html.push('		<textarea class="reply-textarea" id="reply_'+obj.feedbackId+'" rows="1" cols="10" placeholder="请输入回复内容"></textarea>');
						html.push('		<input type="button" class="reply-button" feedBackId="'+obj.feedbackId+'" value="回复" onclick="reply(this)">');
						html.push('	</li>');
						html.push('</ul>');
						
					}
					if(subData.pageNo==1){
						$(".list-div").html(html.join(''));
					}else{
						$(".list-div").append(html.join(''));
					}
				}else{
					continueflag= false;
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


function initLoad(){
	 var nScrollHight = 0; //滚动距离总长(注意不是滚动条的长度)
     var nScrollTop = 0;   //滚动到的当前位置
     var nDivHight = $(".list-div").height();
	 $(".list-div").scroll(function(){
		if(!continueflag){
			return;
		}
        nScrollHight = $(this)[0].scrollHeight;
        nScrollTop = $(this)[0].scrollTop;
        var paddingBottom = parseInt( $(this).css('padding-bottom') ),paddingTop = parseInt( $(this).css('padding-top') );
        if(nScrollTop + paddingBottom + paddingTop + nDivHight >= nScrollHight){
        	subData.pageSize = 10;
        	subData.pageNo = parseInt(subData.pageNo) + 1;
        	loadFeedBackList();
        }
          
     });
}

//用户回复
function reply(obj){
	var feedBackId = $(obj).attr('feedBackId');
	var replyContent = $("#reply_"+feedBackId).val();
	if(!replyContent) {
		return;
	}
	$.ajax({
		url: '/feedbackManager/saveReplyInfo',
		type: 'post',
		datatype: 'json',
		data:{feedBackId:feedBackId,reply:replyContent},
		success: function(response){
			if(response.success){
				$("#ul_"+feedBackId).find('.reply-li').remove();
				var html=[];
				for(var i=0;i<response.replyList.length;i++){
					var reply=response.replyList[i];
					html.push('	<li class="reply-li">');
					if(reply.replyUserId==currentUserId) {
						html.push('		<div class="reply_content user"><span class="reply_name">'+reply.replyUserName+'</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
					}else{
						html.push('		<div class="reply_content customer"><span class="reply_name">客服('+reply.replyUserName+')</span>：'+reply.reply.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
					}
					html.push('		<div class="reply_time">'+reply.replyTime+'</div>');
					html.push('	</li>');
				}
				$("#ul_"+feedBackId).find('.message-li').after(html.join(''));
				$("#reply_"+feedBackId).val('');
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
	$(obj).prev().attr('rows','1');
	$(obj).hide();
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

