function loadNews(newsId)
{
	 $.ajax({
         url: "/appIndex/getNewsContent",
         type: "post",
         data: {newsInfoId:newsId},
         dataType: "json",
         success: function(data) {
             if (data.success) {

     			var rows = data.data;
     			if(rows==null){
     				return;
     			}
     			if(rows.title!=null)
     			{
     				$('#title').append(rows.title);
     				$("title").text(rows.title);    				
     			}
     			
     			if(rows.subTitle)
     				$('#subTitle').append(rows.subTitle);
     			else
     				$('#subTitle').hide();
     			if(rows.srcUrl!=null)
     				$('#srcUrlAndName').append('<a id="srcUrl" href="'+rows.srcUrl+'" target="_blank" rel="nofollow"></a>');
     			
     			if(rows.srcName!=null && rows.srcUrl!=null){
     				$('#srcUrl').append(rows.srcName);
     				$('#downSrc').append('<a href="'+rows.srcUrl+'">本文来源：'+rows.srcName+'  </a>');
     			}
     			
     			if(rows.newsTime!=null){
     				
     				$('#srcUrlAndName').append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+rows.newsTime);
     			}
     			//新闻内容
     			if(rows.content!=null){
     				$('#content').append(rows.content);
     				newsSlide($('#content'));
     			}
     			
     			
             } else {
                 showErrorMessage(data.message);
             }
         }
     });
}

/**
 * 获得评论时间提示
 */
function getCommenttime(commTime){
	 var t = new Date(commTime),
     s = new Date,
     a = (s - t) / 1e3;
	 var timeText = 10 > a ? "刚刚": 60 > a ? Math.round(a) + "秒前": 3600 > a ? Math.round(a / 60) + "分钟前": 86400 > a ? Math.round(a / 3600) + "小时前": (s.getFullYear() == t.getFullYear() ? "": t.getFullYear() + "年") + (t.getMonth() + 1) + "月" + t.getDate() + "日"
	 return timeText;
}

/**
 * 加载评论框
 * return 评论的html
 */
/*function loadCommentHtml(){
	var comment = $('<div class="comment"></div>');
	var comment_title = $('<div class="comment-title">' +
			'<div class="comment-title-text">茶客点评</div>' +
			'<form action="" method="get" class="check">' +
			'<label><input id="anony" name="anony" type="checkbox" value="" class="checkbox" > 匿名</label>' +
			'</form></div>');
	var comment_main = $('<div class="comment-main"><div class="comment-main-left"></div><textarea id="cmtContent" placeholder="  客官请点评" style="border:0;height:100%;width:100%;"></textarea><div class="comment-main-right"></div></div>');
	
	var comment_sub = $('<div class="comment-sub"><form action="" method="post"><input type="button" value="发表" class="comment-sub-button"></form></div>');
	
	$(comment).append(comment_title);
	$(comment).append(comment_main);
	$(comment).append(comment_sub);
	$('.ep-content-bg').append(comment);
	
	var opinion = $('<div class="opinion"></div>');
	var opinion_title = $('<div class="opinion-title"><div class="opinion-title-text">茶友高见</div></div>');
	var opinion_main = $('<div class="opinion-main"></div>');
	var opinion_main_left = $('<div class="opinion-main-left"></div>');
	var opinion_main_right = $('<div class="opinion-main-right"></div>');
	var opinion_main_in = $('<div id="opinion-main-in" class="opinion-main-in"></div>');
	$(opinion_main).append(opinion_main_left);
	$(opinion_main).append(opinion_main_right);
	$(opinion_main).append(opinion_main_in);
	$(opinion_main).append('<div id="buttomText" style="width: 100%;margin: 10px auto;float: left;color:#888;text-align:center;"></div>');
	$(opinion).append(opinion_title);
	$(opinion).append(opinion_main);
	$('.ep-content-bg').append(opinion);
}
*/

//注册单击事件
function initClick(rows){
	//newsLike(rows);  //注册  喜欢和取消喜欢事件
	//freshComment(rows); //注册 最新 最早 最热 点击事件
	//publishComment(rows); //注册发布评论事件
	newsZan(rows);
	newsCai(rows);
	//举报框的单选框点击事件
	//reportRadioClick();
	//底部提示点击事件
	//buttomTextClick(rows);
	//表情包加载事件
	//$('#face').SinaEmotion($('#cmtContent'));
}

/**
 * 评论内容点击事件
 */
function commentClick(rows,jq){
	
	//replyClick(rows,jq);
	push_upClick(rows,jq);
	push_downClick(rows,jq);
	//reportClick(rows,jq);
}

/**
 * 最新 最早 最热 刷新评论
 */
/*function freshComment(rows){
	var cmtList = rows.newsCmtList;
	var imgPath = rows.imgPath;
	$('#orderParam').find('a').click(function(){
		var _this = $(this);
		if(!_this.hasClass('active')){
			var id = _this.attr('id');
			_this.parent().children().removeClass('active');
			_this.addClass('active');
			curPage = 1;//初始化当前页
			doPost(
				path+'/news/ios/getCommentList',
				{newsId:rows.id,userId:rows.appUser.id,orderParam:id,currentPage:curPage},
				function(data){
					var newCmtList = data.data.cmtList;
					
					$('#commentContent').html('');
					getCommentContent(newCmtList,imgPath,rows);
					
				}
			);
		}
	});
}*/


/**
 * 发布评论
 */
/*function publishComment(rows){
	
	var newsId = rows.id;
	var userId = rows.appUser.id;
	var user = {
			headerUrl:rows.imgPath+rows.appUser.headerUrl,
			userName:rows.appUser.name,
		};
	var cmtList = rows.newsCmtList;
	var imgPath = rows.imgPath;
	$('.comment').find('.comment-sub-button').click(function(){
		var cmtContent = $('#cmtContent').val().trim();
		if($('#cmtContent').val()==null || $('#cmtContent').val().trim()=='' || $('#cmtContent').val().trim()=='客官请点评'){
			alert('您还没有输入评论');
			return;
		}
		doPost(
			path+"/news/ios/publishComment",
			{newsId:newsId,userId:userId,comments:cmtContent,anony:$('#anony').is(":checked")},
			function(data){
				
				//如果不是最新  则跳到最新
				if($('#orderParam').find('a[class=active]').attr('id')!='newTime'){ 
					$('#newTime').parent().children().removeClass('active');
					$('#newTime').addClass('active');
				}
				
				cmtList = data.data.cmtList;
				
				rows.newsCmtList = data.data.cmtList;
				
				$('#opinion-main-in').html('');
				getCommentContent(cmtList,imgPath,rows);
				
				$('#commentCount').html(parseInt($('#commentCount').text())+1);//设置评论数
				//清空输入框
				$('#cmtContent').val('');
			}
		);
		
	});
		
}*/


/**
 * 插入一条评论
 */
/*function addComment(cmt,rows){
	var imgPath = rows.imgPath;
	var topComment = $('<div class="top_comment" id="'+cmt.id+'"></div>');
	var picLeft = $('<div class="pic_left"></div>');
	
	var picLeft_img="";
	if(cmt.anony){
		//headUrl = path+'/iosStatic/images/anony.png';
		picLeft_img =$('<img id="head_url" />'); 
	}else{
		var headUrl = imgPath+cmt.user.headerUrl;
		picLeft_img = $('<img src="'+headUrl+'" width="40" />');
	}
	picLeft.append(picLeft_img);
	
	var textRight=$('<div class="text_right"></div>');
	var line = $('<div class="line"></div>');
	var textRight_p = $('<p><a href="javascript:void(0);" class="active">'+(cmt.anony?'匿名网友':cmt.user.name)+'</a><span>'+cmt.floor+'#</span><br />'+(cmt.cmt!=null?('<a class="active" href="javascript:void(0);">回复 &nbsp;&nbsp;'+(cmt.cmt.anony?'匿名网友':cmt.cmt.user.name)+'&nbsp;:&nbsp;</a>'+AnalyticEmotion(cmt.comments)):AnalyticEmotion(cmt.comments))+'</p>');
	var textRight_ul = $('<ul></ul>');
	var textRight_ul_time = $('<li class="time"></li>');
	textRight_ul_time = getTextRight_ul_time(textRight_ul_time,cmt.commTime);
	
	var textRight_ul_reply = $('<li class="reply"><span><a href="javascript:void(0);">回复</a></span></li>');
	var textRight_ul_push_up = $('<li class="push_up"></li>');
	var textRight_ul_push_up_span = $('<span></span>');
	
	var textRight_ul_push_up_span_a = $('<a href="javascript:void(0);" ><span class="upText" style="padding-left:0px;">顶</span>(<span class="upCount" style="padding-left:0px;">'+cmt.upCount+'</span>)</a>');
	if(cmt.liked)
		textRight_ul_push_up_span_a = $('<a href="javascript:void(0);" class="active"><span class="upText" style="padding-left:0px;">已顶</span>(<span class="upCount" style="padding-left:0px;">'+cmt.upCount+'</span>)</a>');
	textRight_ul_push_up_span.append(textRight_ul_push_up_span_a);
	textRight_ul_push_up.append(textRight_ul_push_up_span);
	
	
	//var textRight_ul_forward = $('<li class="forward"><span><a href="javascript:void(0);">转发</a></span></li>');
	var textRight_ul_report = $('<li class="report"><span><a href="javascript:void(0);">举报</a></span></li>');
	//var textRight_ul_delete = $('<li class="delete"><span><a href="javascript:void(0);">删除</a></span></li>');
	textRight_ul.append(textRight_ul_time);
	textRight_ul.append(textRight_ul_reply);
	textRight_ul.append(textRight_ul_push_up);
	//textRight_ul.append(textRight_ul_forward);
	textRight_ul.append(textRight_ul_report);
	//textRight_ul.append(textRight_ul_delete);
	
	textRight.append(textRight_p);
	textRight.append(textRight_ul);
	
	topComment.append(picLeft);
	topComment.append(textRight);
	topComment.append(line);
	
	$('#commentContent').prepend(topComment);
	
	commentClick(rows,topComment);
	
}
*/


/**
 * 加载评论内容
 */
/*function getCommentContent(cmtList,imgPath,rows){
	
	$('#buttomText').html('');
	for(var key in cmtList){
		
		var opinion_content = $('<div class="opinion-content" id="'+cmtList[key].id+'"></div>');
		var opinion_content_left = $('<ul class="opinion-content-left"></ul>');
		var opinion_content_left_li = $('<li class="wealth-wrap-position"></li>');
		var opinion_content_left_li_img = $('<img src="'+path+'/iosStatic/images/anony.jpg'+'">');
		var opinion_content_left_li_div = $('<div class="wealth-list-v wealth-wrap-v-top"><span></span></div>');
		
		//判断是否匿名
		if(!cmtList[key].anony){
			var headUrl = imgPath+cmtList[key].user.headerUrl;
			opinion_content_left_li_img = $('<img src="'+headUrl+'">');
		}
		$(opinion_content_left_li).append(opinion_content_left_li_img);
		
		//判断是否认证
		if(cmtList[key].user.authen){
			if(cmtList[key].user.authen.status){
				if(cmtList[key].user.authen.status=='1')
					$(opinion_content_left_li).append(opinion_content_left_li_div);
			}
		}
		$(opinion_content_left).append(opinion_content_left_li);
		
		
		var opinion_content_right = $('<ul class="opinion-content-right"></ul>');
		var opinion_content_right_li1 = $('<li class="opinion-content-title"><h1>'+(cmtList[key].anony?'匿名网友':cmtList[key].user.name)+'</h1><span class="push_down '+(cmtList[key].disLiked?'opinion-yicai':'opinion-cai')+'">'+cmtList[key].downCount+'</span><span class="push_up '+(cmtList[key].liked?'opinion-yizan':'opinion-zan')+'">'+cmtList[key].upCount+'</span></li>');
		var opinion_content_right_li2 = $('<li><h2>'+cmtList[key].user.login.loginArea+'</h2><h2>'+getCommenttime(cmtList[key].commTime)+'</h2></li>');
		var opinion_content_right_li3 = $('<li><p>'+cmtList[key].comments+'</p></li>');
		$(opinion_content_right).append(opinion_content_right_li1);
		$(opinion_content_right).append(opinion_content_right_li2);
		$(opinion_content_right).append(opinion_content_right_li3);
		
		
		$(opinion_content).append(opinion_content_left);
		$(opinion_content).append(opinion_content_right);
		
		
		
		$('#opinion-main-in').append(opinion_content);
		
		//注册事件
		commentClick(rows,opinion_content);
	}
	
	if(cmtList.length<10){
		$('#buttomText').append('没有更多评论');
	}else{
		$('#buttomText').append('点击加载更多评论');
	}
	
	
}*/


/**
 * 回复点击事件
 */
function replyClick(rows,jq){
	var headUrl = rows.imgPath+rows.appUser.headerUrl;
	var $jquery = $('#commentContent');
	if(jq)
		$jquery = jq;
	$jquery.find('.reply').click(function(){
		var _this = $(this);
		
		var thisId = _this.parent().parent().parent().attr('id');
		if(thisId!=$('#commentContent').find('.bottom_comment_sh').parent().parent().attr('id')){
			$('#commentContent').find('.bottom_comment_sh').remove();//移除别的回复框
			
			//创建新的回复框
			var buttom_comment_sh = $('<div class="bottom_comment_sh"></div>');
			var pic_left_sh = $('<div class="pic_left_sh"></div>');
			var img = $('<img src="'+headUrl+'" width="40" />');
			pic_left_sh.append(img);
			var text_right_sh = $('<div class="text_right_sh"></div>');
			var text = $('<textarea id="replyContent" onfocus="if(this.value==\'说点什么吧\') {this.value=\'\';}" onblur="if(this.value==\'\') {this.value=\'说点什么吧\';}" value="说点什么吧">说点什么吧</textarea>');
			var ol = $('<ol><li  class="face"></li><li  class="publish">发布</li></ol>');
			var share = $('<li class="share"><input id="replyAnony" type="checkbox"  /><span style="line-height:32px; ">匿名</span></li>');
			ol.append(share);
			text_right_sh.append(text);
			text_right_sh.append(ol);
			buttom_comment_sh.append(pic_left_sh);
			buttom_comment_sh.append(text_right_sh);
			_this.parent().after(buttom_comment_sh);
			//回复评论发布事件
			replyComment(rows);
			//回复框表情事件
			$(ol).find('li[class=face]').SinaEmotion($('#replyContent'));
		}else{
			$('#commentContent').find('.bottom_comment_sh').remove();//移除回复框
		}
		
		
		
	});
}


/**
 * 评论 顶  点击事件
 */
function push_upClick(rows,jq){
	var $jquery = $('#opinion-content');
	if(jq)
		$jquery = jq;
	
	$jquery.find('.push_up').click(function(){
		var _this = $(this);
		
		var thisId = _this.parent().parent().parent().attr('id');
		
		doPost(
			path+"/news/ios/likeComment",
			{commentId:thisId,userId:rows.appUser.id,param:(_this.hasClass('opinion-zan')?'顶':'已顶')},
			function(data){
				if(_this.hasClass('opinion-zan')){
					_this.removeClass('opinion-zan').addClass('opinion-yizan');    //添加已赞样式
					
					_this.html(parseInt(_this.text())+1);  //顶的数量+1
				}else{
					_this.removeClass('opinion-yizan').addClass('opinion-zan');  //移除已赞样式
					_this.html(parseInt(_this.text())-1);  //顶的数量-1
				}
			}
		);
		
		
	});
}

/**
 * 评论 踩  点击事件
 */
function push_downClick(rows,jq){
	var $jquery = $('.opinion-content');
	if(jq)
		$jquery = jq;
	
	$jquery.find('.push_down').click(function(){
		var _this = $(this);
		
		var thisId = _this.parent().parent().parent().attr('id');
		
		doPost(
			path+"/news/ios/likeComment",
			{commentId:thisId,userId:rows.appUser.id,param:(_this.hasClass('opinion-cai')?'踩':'已踩')},
			function(data){
				if(_this.hasClass('opinion-cai')){
					_this.removeClass('opinion-cai').addClass('opinion-yicai');    //添加已踩样式
					
					_this.html(parseInt(_this.text())+1);  //踩的数量+1
				}else{
					_this.removeClass('opinion-yicai').addClass('opinion-cai');  //移除已踩样式
					_this.html(parseInt(_this.text())-1);  //踩的数量-1
				}
			}
		);
		
		
	});
}




/**
 * 举报点击事件
 */
function reportClick(rows,jq){
	var $jquery = $('#commentContent');
	if(jq)
		$jquery = jq;
	
	$jquery.find('.report').click(function(){
		var _this = $(this);
		
		var thisId = _this.parent().parent().parent().attr('id');
		
		$('#my-prompt').modal({
		    relatedTarget: this,
		    onConfirm: function(e) {
		        var reportText = '';
		        var type = $('#my-prompt').find('input[name=radio1]:checked').val();
		        if(type==3){
		        	reportText = e.data;
		        }else if(type==0){
		        	reportText = '淫秽色情';
		        }else if(type==1){
		        	reportText = '营销方式';
		        }else if(type==2){
		        	reportText = '恶意攻击谩骂';
		        }
		        doPost(
		        	path+'/news/ios/reportComment',
		        	{commentId:thisId,userId:rows.appUser.id,reportText:reportText},
		        	function(data){
		        		alert('感谢您的监督，举报已发往议事大厅');
		        		_this.hide();
		        	}
		        );
		    }
	    });

		
		
	});
}


/**
 * 举报框的单选框点击事件
 */
function reportRadioClick(){
	$('#my-prompt').find('input[type=radio]').click(function(){
		if($(this).val()==3){
			$('#reportText').show();
		}else{
			$('#reportText').hide();
		}
		
	});
}


/**
 * 点击加载更多
 */
function buttomTextClick(rows){
	var imgPath = rows.imgPath;
	
	$('#buttomText').click(function(){
		if($('#buttomText').text()!='没有更多评论'){
			$('#buttomText').html('<i class="am-icon-spinner am-i con-spin"></i>加载中');
			doPost(
				path+'/news/ios/getCommentList',
				{
					newsId:rows.id,
					userId:rows.appUser.id,
					orderParam:$('#orderParam').find('a[class=active]').attr('id'),
					currentPage:++curPage
				},
				function(data){
					var newCmtList = data.data.cmtList;
					getCommentContent(newCmtList,imgPath,rows);
				}
			);
		}
	});
	
}


var upDiv;
function initUpTop()
{

    window.onload = function(){
        upDiv = document.createElement('div');
        $(upDiv).hide();
        upDiv.id="scrollDiv";
        $(upDiv).addClass("upBtn");

//    var scrollTop = document.documentElement.scrollTop;
//    if(scrollTop == 0){
//        div.style.display = 'none';
//    }

        upDiv.onmousedown = function(){
            scroll(document.body.scrollTop);
            return false;
        };

        document.body.appendChild(upDiv);
    };



    window.requestAnimFrame = (function(){
        return  window.requestAnimationFrame       ||
                window.webkitRequestAnimationFrame ||
                function( callback ){
                    window.setTimeout(callback, 1000 / 60);
                };
    })();

    
    $(window).scroll(function()
    {
    	if($(this).scrollTop()>0)
    		$(upDiv).show();
    	else
    		$(upDiv).hide();
    });
}	
function scroll(scrollTop){
    if(scrollTop == 0)
        return false;
    var el = document.body,
        during = parseInt(scrollTop);
    var s = Math.round(during/40);
    var _run = function() {
        during -= s;
        el.scrollTop = during;
        if (during > 0)
            requestAnimationFrame(_run);
    };

    _run();
}
