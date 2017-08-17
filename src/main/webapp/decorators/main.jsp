<%@page import="com.xiaotu.makeplays.user.model.UserInfoModel"%>
<%@page import="com.xiaotu.makeplays.utils.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String currentUrl = request.getRequestURI();

Object hasCrewSetAuth = false; //是否有剧组设置权限
Object obj = session.getAttribute("userAuthMap");
Object pwdobj = session.getAttribute(Constants.SESSION_FINFNCE_PWD);

Object menuTree = session.getAttribute("menuTree");

if(obj!=null){
	java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CREW_SETTING) != null){
		hasCrewSetAuth = true;
	}
}
request.setAttribute("hasCrewSetAuth", hasCrewSetAuth);
UserInfoModel userInfo = (UserInfoModel)session.getAttribute(Constants.SESSION_USER_INFO);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="<%=path%>/images/favicon.ico" type="image/x-icon">
<title><decorator:title default="剧易拍" /></title>

<link rel="stylesheet" href="<%=path%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=path%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" href="<%=path%>/js/jqwidgets/styles/jqx.bootstrap.css" type="text/css" />
<link rel="stylesheet" href="<%=path%>/css/style.css" type="text/css" />
<link rel="stylesheet" href="<%=path%>/css/common/public.css" type="text/css" />

<script type="text/javascript" src="<%=path%>/js/dist/makeplays.min.js"></script>
<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=path%>/js/Constants.js"></script>
<script type="text/javascript" src="<%=path%>/js/HashMap.js"></script>

<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">
<script type="text/javascript" src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/common/common.js"></script>


<script type="text/javascript">
var ctx="<%=basePath%>";
$(document).ready(function () {
	
	theme='ui-lightness';
	
	var clientHeight= window.innerHeight - 2;//window.screen.availHeight-65;
	//布局
    $('#splitContainer').jqxSplitter({theme:theme, height: clientHeight, width: '99.8%', orientation: 'horizontal',showSplitBar: false, panels: [{ size: '84px' }, { size: '82.5%' }] });
    /* $('#splitter').jqxSplitter({theme:theme,width: '100%',  panels: [{ size: '0%' }, { size: '100%'}] }); */
    var treeHeight=800*0.8;
    //消息提示窗口
    
    $(window).resize(function(){
    	//$('#splitContainer').css('height', window.innerHeight - 2);
    	$('#splitContainer').jqxSplitter({height: window.innerHeight - 2});
    	$("#cc").css("height", window.innerHeight - 140 - $("#tableHead").height());
    });
               //菜单
               var menuJson = <%=menuTree%>;
               if (menuJson == null) {
                   menuJson = {};
               }
               $("#topMenu").css('visibility', 'visible'); 
                
                var currentUrl = '<%=currentUrl%>';
                currentUrl = currentUrl.replace(/\/+/g,"/");
                if(currentUrl == "/index"){
                	currentUrl = 'notmenu';
                }
                var firstMenu = [];
                var secendMenu = [];
                var firstMenuIndex;
                var secendMenuIndex;
                firstMenu.push("<ul class='main_nav_ul' style='margin-bottom: 0px;'>");
                $.each(menuJson, function(i,v){
                	
                	if(v.childList != undefined && v.childList.length > 0){
                		firstMenu.push("<li model-value='"+v.value+"'>"+v.text+"</li>");
                		secendMenu = [];
                		secendMenu.push("<ul style='display: none;'>");
                		$.each(v.childList,function(ci,cv){
                			
                			if(cv.value.indexOf(currentUrl) != -1) {
                				firstMenuIndex = i;
                				secendMenuIndex = ci;
                				secendMenu.push("<li model-value='"+cv.value+"'><a class='menu-a-hover' href='javascript:void(0)'>"+cv.text+"</a></li>");
                			} else {
                				secendMenu.push("<li model-value='"+cv.value+"'><a class='menu-a' href='javascript:void(0)'>"+cv.text+"</a></li>");
                			}
                		});
                		secendMenu.push("</ul>");
                		$("#menu-topbar").append(secendMenu.join(''));
                	}else{
                		firstMenu.push("<li model-value='"+v.value+"'>"+v.text+"</li>");
                		$("#menu-topbar").append("<ul style='display: none;'></ul>");
                		if (currentUrl == v.value) {
            				firstMenuIndex = i;
            			}
                	}
                });
                firstMenu.push("</ul>");
                $("#tabMenuList").html(firstMenu.join(''));
                
                if(firstMenuIndex!=undefined){
                	$("#tabMenuList ul").find("li:eq("+firstMenuIndex+")").addClass("main_li_current");
                	$("#menu-topbar").find("ul:eq("+firstMenuIndex+")").show().addClass("menu-current-ul");
                }
                
                $("#menu-topbar ul li").on("click",function(){
                	var _this = $(this);
                	var href='<%=request.getContextPath()%>';
            		var url = _this.attr("model-value");
            		
            		var opacityMenuMap = new HashMap(); //过滤的菜单
            		<c:forEach items="${financePwd}" var="cur">
            		opacityMenuMap.put("${cur.key}",${cur.value});
            		</c:forEach>
            		
            		window.location.href=href + url;
                });
                
                $("#tabMenuList li").click(function(e){
                	var _this = $(this);
                	if(!_this.hasClass("main_li_current")){
                		var index = _this.index();
                		_this.addClass("main_li_current").siblings().removeClass("main_li_current");
                		$("#menu-topbar").find("ul:eq("+index+")").show().siblings().hide();
                	}
                	
                	var href='<%=request.getContextPath()%>';
                    var url = _this.attr("model-value");
                	if (url != undefined && url != "undefined" && url) {
                	   window.location.href=href + url;
                	}
                	
                	e.stopPropagation();
                });
                
                $("#menu-topbar ul").hover(function(e){
                	if(!$(this).hasClass(".menu-current-ul")){
                		$(this).show();
                	}
                	e.stopPropagation();
                },function(e){
                	if(!$(this).hasClass(".menu-current-ul")){
                		//$(this).hide();
                	}
                	e.stopPropagation();
                });
                
				$("#menu-topbar").click(function(e){
					e.stopPropagation();
                });
                                
			    //右下角新消息提示
			    $("#messageRemind").jqxNotification({
			        width: 250, position: "bottom-right", opacity: 0.9,
			        autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000, template: "info"
			    });
			    $('#messageRemind').on('click', function () {
			    	window.location.href='/userManager/toUserCenterPage?activeTagType=2';
			    });
                
                $(".nav_box").on("mouseover",function(){
                	var _this = $(this);
                	var wd = $(".user_name").css("width");
                	//if(wd<80){wd=80;}
                	$(".usermenulist").css({left:_this.offset().left,top:_this.offset().top+_this.outerHeight()-3,width:wd})
                	.slideDown().stop();
                });
                $(".nav_box").on("mouseout",function(){
                	$(".usermenulist").hide();
                });
                $(".usermenulist").on("mouseover",function(){
                	$(this).show();
                }).on("mouseout",function(){
                	$(".usermenulist").hide();
                });
                
                $(".juzu").on("mouseover",function(){
                	var _this = $(this);
                	var wd = $(this).css("width");
                	//if(wd<80){wd=80;}
                	$(".crewmenulist").css({left:_this.offset().left,top:_this.offset().top+_this.outerHeight()}).css('min-width',wd)
                	.slideDown().stop();
                });
                $(".juzu").on("mouseout",function(){
                	$(".crewmenulist").hide();
                });
                $(".crewmenulist").on("mouseover",function(){
                	$(this).show();
                }).on("mouseout",function(){
                	$(".crewmenulist").hide();
                });
                
                $("#smallQrCodeDiv").on("mouseover", function() {
                    var _this = $(this);
                    var wd = _this.css("width");
                    
                    $("#bigQrCodeDiv").show();
                    /* $("#bigQrCodeDiv").css({left:_this.offset().left-45,top:_this.offset().top+_this.outerHeight()-3}).slideDown(); */
                    $("#bigQrCodeDiv").css({left:_this.offset().left-58,top:_this.offset().top+_this.outerHeight()}).slideDown();
                }).on("mouseout", function() {
                    $("#bigQrCodeDiv").hide();
                });
                $("#bigQrCodeDiv").on("mouseover",function(){
                    $(this).show();
                }).on("mouseout",function(){
                    $("#bigQrCodeDiv").hide();
                });
                
                $('#eventWindowAll').jqxWindow({
                    maxHeight: 170, maxWidth: 280, minHeight: 30, minWidth: 250, height: 165, width: 270,modalZIndex: 20010,
                    resizable: false, isModal: true, modalOpacity: 0.3,theme:theme,
                    okButton: $('#ok'), cancelButton: $('#mainCloseBtn'),autoOpen: false,
                    initContent: function () {
                        $('#ok').jqxButton({theme:theme, width: '65px',height:'25px' });
                        $('#mainCloseBtn').jqxButton({theme:theme, width: '65px',height:'25px' });
                    }
                });
                
                getMessageList(true);//第一次执行,不查询新消息
                <c:if test="${hasCrewSetAuth }">
                	//setTimeout("getMessageList()", 20000);
                </c:if>
                window.setInterval("getMessageList()",30000);//每隔30秒执行一次
                $(document).click(function(){
                	$('.proupmessage').slideUp("normal");
                	<c:if test="${loginUserType == 2 }">
                	$(".crewsearchlist").hide();
                	</c:if>
                	
                	if($(".menu-current-ul").is(":hidden")){
						$(".menu-current-ul").show().siblings().hide();
						if(firstMenuIndex!=undefined){
		                	$("#tabMenuList ul").find("li:eq("+firstMenuIndex+")").addClass("main_li_current").siblings().removeClass("main_li_current");
		                }
					}
					if($(".menu-current-ul").length<1){
						$("#tabMenuList li").removeClass("main_li_current");
						$("#menu-topbar ul").removeClass("menu-current-ul").hide();
					}
                });
                $(".proupmessage").click(function(e){
                	e.stopPropagation();
                });
                <c:if test="${loginUserType == 2 }">
                $(".crewsearchlist").click(function(e){
                	e.stopPropagation();
                });
                </c:if>
                /* $("#messageproupheaderclose").on("click",function(){
    				$(".proupmessage").slideUp("normal");
    			}); */
    			
    			//初始化财务密码窗口
                //initFinancePwdWin();
    			//初始化验证用户窗口
                initValidUserWin();
    			
              	//隐藏剧组选择框
            	$(document).click(function(){
            		if($(".crewsearchlist")) {
                		$(".crewsearchlist").hide();
            		}
            	});
});

function contactScroll() {
	var b = $(".contactTableList").scrollLeft;
	$(".contactTableListHead").scrollLeft = b;
}

//读取消息  每30秒读取一次
function getMessageList(flag){
	$.ajax({
		url: '/messageInfoManager/queryUnReadMessageNum',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				if(response.num && response.num != 0) {
					/* if(!$("#userMessageDot").hasClass("dot")) {
						$("#userMessageDot").addClass("dot");
					} */
					$("#unReadMessageNum").html('(<font color="#f57c00">'+response.num+'</font>)');
					if(!flag) {
						if(response.tipNum && response.tipNum != 0) {
							$("#messageRemindNum").html(response.tipNum);
							$("#messageRemind").jqxNotification("open");
						}
					}
				}else{
					//$("#userMessageDot").removeClass("dot");
					$("#unReadMessageNum").html('');
				}
			}else{
				showErrorMessage(response.message);
			}   
		}
	});
	
	/* $.ajax({
		url:ctx+'/userManager/getMessageInfo',
		type:'post',
		dataType:'json',
		success:function(data){ //console.log(data)
			var flag = $("#userMessageDot").hasClass("dot");
			if(data.number > 0){
				if(!flag){
					$("#userMessageDot").addClass("dot");
					$("#messageMenu").text("消息("+ data.number +")");
					//$("#messagecenter").addClass("messagecenterimg");
				}
			}else{
				if(flag){
					$("#userMessageDot").removeClass("dot");
                    $("#messageMenu").text("消息");
					//$("#messagecenter").removeClass("messagecenterimg");
				}
			}
		}
	}); */
}
//弹出消息窗口并进行操作
function proupmessagewindow(obj){
	var _this = $(obj);
	$("#userMessageDot").removeClass("dot");
	$("#messagecenter").removeClass("messagecenterimg");
	$.ajax({
		url:ctx+'/userManager/getMessageList',
		dataType:'json',
		type:'post',
		success:function(data){
			var lihtml = [];
			$.each(data.message,function(i,v){
				lihtml.push("<li><div class='dropDownButtonMessage' userId='"+v.senderId+"' messageId='"+v.messageId+"' content='"+v.content+"'>"+v.realName+"申请加入剧组</div></li>");
			});
			$(".proupmessage>ul").html(lihtml.join(''));
		
			$(".proupmessage").css({left:_this.offset().left-192,top:_this.offset().top+_this.outerHeight()- 43,width:'230px',height:'450px'})
			.slideDown()//.stop();
			//$(".proupmessage").css({height:'500px'});
			$(".dropDownButtonMessage").on("click",function(e){
				var next = $(this).next("div");
				$(this).parent().siblings().find(".dropDownButtonContent").css("display","none");;
				if(next.length < 1){
					var html = "<div class='dropDownButtonContent'><div class='dropDownBtnCtno'>"+$(this).attr("content")+"</div><div style='padding-left: 24px;background-color: #F3F3F4;padding-bottom: 20px;'>";
					html += "<input type='button' id='messageBtnSure' style='width: 60px;' class='messageBtnstyle' value='同意'/>";
					html += "<input type='button' class='messageBtnstyle' id='messageBtncancle' value='拒绝' style='margin-left: 30px;width: 60px;'/></div>";
					html += "</div>";
					$(this).after(html);
					$("#messageBtnSure").on("click",function(ev){
						var parent = $(this).parent().parent();
						var _user = parent.prev().attr("userId");
						//parent.html("<div style='padding-left: 52px;'><input type='button' userId='"+_user+"' id='messagesetauth' class='messageBtnstyle1' style='width: 90px;' value='去设置权限'/></div><div style='padding-left: 52px;'><input type='button' class='messageBtnstyle1' style='width: 90px;margin-top: 8px;' id='messagesetnoauth' value='稍后设置权限'/></div>");
						var params = {
								messageId:parent.prev().attr("messageId"),
								userId:_user,
								type:1
						};
						$.ajax({
							url:ctx+'/userManager/messageHandle',
							data:params,
							dataType:'text',
							type:'post',
							async:false,
							success:function(data){
								window.location.href = ctx+"/crewManager/crewList?type=1&ifMessage=1&userId="+_user;
							},
							error:function(data){}
						});
						/* $("#messagesetauth").on("click",function(evn){
							window.location.href = ctx+"/crewManager/crewList?type=1&ifMessage=1&userId="+$(this).attr("userId");
							evn.stopPropagation();
						});
						$("#messagesetnoauth").on("click",function(evn){
							$(this).parent().parent().parent().remove();
							evn.stopPropagation();
						}); */
						ev.stopPropagation();
					});
					$("#messageBtncancle").on("click",function(ev){
						var parent = $(this).parent().parent();
						var params = {
								messageId:parent.prev().attr("messageId"),
								userId:parent.prev().attr("userId"),
								type:0
						};
						$.ajax({
							url:ctx+'/userManager/messageHandle',
							data:params,
							dataType:'json',
							type:'post',
							success:function(data){
								
							},
							error:function(data){}
						});
						parent.parent().remove();
						ev.stopPropagation();
					});
				}else{
					next.css("display","block");
				}
				
				e.stopPropagation();
			});
			
		},
		error:function(){}
	});
	
	
	
}
var searchindex = 1;//当前li
var searchlenght = 0;
function searchMenuCrew(){
	var _this = $("#crewManuSearchInput");
	var crewName=_this.val();
	
	//if($.trim(crewName) == ''){
		//$(".crewsearchlist").hide();
		//return;
	//}
	
	var key = event.keyCode;
	var search_show = $(".crewsearchlist ul");
	
	if (key == 13) {
		search_show.find("li:eq(" + (searchindex-2) + ")").click();
		return;
	}
	
	if (key == 38) { /*向上按钮*/  
		searchindex--;  
        if (searchindex == 0) searchindex = searchlenght; //到顶了，
        
        var li = search_show.find("li:eq(" + (searchindex-2) + ")");  
        li.css("background", "#E8F4FC").siblings().css("background", "");
        _this.val(li.text());
        return;
    } else if (key == 40) {/*向下按钮*/  
    	searchindex++;  
        if (searchindex == searchlenght+2) searchindex = 2; //到底了   
        
        var li = search_show.find("li:eq(" + (searchindex-2) + ")");  
        li.css("background", "#E8F4FC").siblings().css("background", "");
        _this.val(li.text());
        return;
    }  
      
	searchlenght = 0;
	var params = {
			crewName:crewName
	};
	$.ajax({
		url:ctx+'/crewManager/queryCrewIdAndCrewName',
		data:params,
		dataType:'json',
		type:'post',
		success:function(data){
			//console.log(data);
			var html = [];
			if(data.result.length > 0){
				$.each(data.result,function(i,v){
					html.push("<li sid='"+v.crewId+"' onclick='swithCrewForService(\""+ v.crewId +"\")'>"+v.crewName+"</li>");
				});
				searchlenght = data.result.length;
			}else{
				html.push("<div style='width:207px;font-size:12px;line-height:28px;text-align:center;'>暂无结果</div>");
			}
			
			searchindex = 1;
			
			$(".crewsearchlist").css({left:_this.offset().left,top:_this.offset().top+_this.outerHeight()}).show().find("ul").html(html);
		}
	});
	 
}
function swithCrewForService(crewId) {
    //window.location.href = "/userManager/switchCrewForSerivce?crewId=" + crewId;
	$.ajax({
        url: "/userManager/switchCrewForCustomerService",
        type: "post",
        dataType: "json",
        data: {crewId: crewId},
        success: function(response) {
            if (response.success) {
                window.location.reload();
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}
function switchCrew (crewId) {
    $.ajax({
         url: "/userManager/switchCrew",
         type: "post",
         dataType: "json",
         data: {crewId: crewId},
         success: function(response) {
             if (response.success) {
                 window.location.href = "/toIndexPage";
             } else {
                 showErrorMessage(response.message);
             }
         }
     });
}

//初始化财务密码窗口
function initFinancePwdWin() {
    $("#financePwdWindow").jqxWindow({
        theme: theme,
        height: 250,
        width: 400,
        resizable: false,
        isModal: true,
        autoOpen: false,
        initContent: function(){
            
        }
    });
    
    $("#financePwdWindow").on("close", function() {
        window.location.href = "/toIndexPage";
    });
}
//初始化安全验证窗口
function initValidUserWin(){
	$("#validUserWin").jqxWindow({
        theme: theme,
        height: 250,
        width: 400,
        resizable: false,
        isModal: true,
        autoOpen: false,
        initContent: function(){
            
        }
    });
    
    $("#validUserWin").on("close", function() {
        window.location.href = "/toIndexPage";
    });
}
//校验是否需要财务密码、用户手机号(ip)
function checkNeedFinancePwd() {
    var need = false;
    $.ajax({
        url: "/financeSettingManager/nopassword/checkPasswordHasSetted",
        type: "post",
        data: {},
        dataType: "json",
        async: false,
        success: function(response) {
    		$(".financepassli").hide();
    		$(".useripli").hide();
        	var flag = false;
        	var validType=-1;
        	if(response.needPwd) {
        		flag = true;
        		$(".financepassli").show();
        		if(response.needUserIp){
        			validType=0;//全部验证
        		} else {
        			validType=1;//仅验证财务密码
        		}
        		$("#financePwd").focus();
        	}else{
        		if(response.needUserIp) {
        			validType=2;//仅验证用户手机号
        			$("#verifyCode").focus();
        		}
        	}
        	if(response.needUserIp){
        		flag = true;
        		$(".useripli").show();
        	}
        	$("#validType").val(validType);
            if (flag) {
                $("#validUserWin").jqxWindow("open");
                need = true;
            }
        }
    });
    
    return need;
}
function finanPwdWinKeyup(event) {
    var key = event.keyCode;
    if (key == 13) {
        checkPassword();
    }
}
//校验财务密码、验证码
function checkPassword(own) {
	var params={};
	var validType=$("#validType").val();
	params.validType=validType;
	if(validType==0 || validType==1){//财务密码
		var financePwd=$("#financePwd").val();
		if(!financePwd) {
			showErrorMessage("请填写财务密码");
	        return false;
		}
		params.password=financePwd;
	}
	if(validType==0 || validType==2){//验证码
		var verifyCode=$("#verifyCode").val();
		if(!verifyCode) {
			showErrorMessage("请填写验证码");
	        return false;
		}
		params.verifyCode=verifyCode;
	}
	var phone = '<%=userInfo.getPhone()%>';
	params.phone=phone;
    $.ajax({
        url: "/financeSettingManager/nopassword/checkPasswordCorrect",
        type: "post",
        data: params,
        dataType: "json",
        async: false,
        success: function(response) {
            if (!response.success) {
                showErrorMessage(response.message);
            } else {
                $("#validUserWin").jqxWindow("close");
                window.location.reload();
            }
        }
    });
}
//关闭财务密码窗口
function closePwdWindow() {
    $("#validUserWin").jqxWindow("close");
}
//跳转到个人中心
function toUserCenter(id) {
	if(id) {
		window.location.href="/userManager/toUserCenterPage?activeTagType="+id;
	}else{
	    window.location.href = "/userManager/toUserCenterPage";
	}
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
//获取验证码点击事件
function obtainVerifyCode(obj){
	if ($(obj).hasClass("disabled")) {
        return false;
    }
    
    var phone = '<%=userInfo.getPhone()%>';
    $.ajax({
        url: "/interface/verifyCodeManager/sendVerifyCode",
        type: "post",
        data: {phone: phone, type: 4},
        success: function(response) {
            if (response.success) {
                $("#getVerifyCodeBtn").addClass("disabled");
                $("#getVerifyCodeBtn").val("重新发送（60s）");
                
                var totalSecond = 60;
                var int = setInterval(function(event) {
                    totalSecond --;
                    $("#getVerifyCodeBtn").val("重新发送（"+ totalSecond +"s）");
                    
                    if (totalSecond == 0) {
                        window.clearInterval(int);
                        $("#getVerifyCodeBtn").removeClass("disabled");
                        $("#getVerifyCodeBtn").val("获取验证码");
                    }
                }, 1000);
                
                $("#verifyCode").focus();
            } else {
                showErrorMessage(response.message);
            }
        }
    });
}
//打开二维码大图页面
function openDownLoadImagePage(obj){
	window.open($(obj).attr('src'));
}
</script>
<decorator:head />
</head>
<body>
<div class="proupmessage" style="position: absolute;z-index: 17001;display: none;top:-324px;">
        				<ul style="height: 100%;overflow-y: auto;">
                			
            			</ul>
</div>
<div class="usermenulist" style="position: absolute;z-index: 17000;display: none;top:-324px;float:right;">
			<ul>
				<!-- <span id="messagecenter" onclick="proupmessagewindow(this);" title='展开消息' class=""></span> -->
	            <!-- <li><a href="/crewManager/toJoinCrewPage" title="新建/加入剧组">新建/加入剧组</a></li> -->
   			</ul>
</div>
<%-- <div class='crewmenulist' style="position: absolute;z-index: 17000;display: none;top:-324px;float:right;">
        				<ul>
                			<c:forEach items="${allCrew }" var='allCrew'>
                				<li><a href='javascript:void(0)' onclick="switchCrew('${allCrew.crewId}')">${allCrew.crewName}</a></li>
                			</c:forEach>
            			</ul>
</div> --%>
<div id="bigQrCodeDiv" style="position: absolute;z-index: 17000;top:55px;float:right;left: -100px;display: none;">
    <div style="float:left; background-color: rgba(4,84,133,0.9); color: white; text-align: center;padding: 20px;padding-bottom:0px;">
        <img src="/images/downLoadImage/download_pr.png" width="100px" height="100px" style="cursor: pointer;" onclick="openDownLoadImagePage(this)"/>
        <p style="line-height: 30px;font-size: 16px; margin: 0;">APP下载</p>
    </div>
    <!-- <div style="float:left; background-color: rgba(4,84,133,0.9); color: white; text-align: center;padding: 20px;padding-bottom:0px;">
        <img src="/images/ios-qr.jpg" width="100px" height="100px"/>
        <p style="line-height: 30px;">IOS</p>
    </div> -->
</div>
<c:if test="${loginUserType == 4}">
<div class='crewsearchlist' style="position: absolute;z-index: 17000;display: none;top:-324px;float:right;max-height: calc(100% - 50px);overflow-y:auto;">
        				<ul>
            				
                			<!-- <li>花千骨</li>
                			<li>虎妈猫爸</li> -->
                			
            			</ul>
</div>
</c:if>
<div id="splitContainer" class="splitContainer" style="min-width: 1300px;">
<div>
        <div class="tab_manu_top" style="height: 52px;">
        	<div id="topMenu" class="menuTopRight" style="float: right; visibility: hidden;">
        		<%-- <div class="nav_box" onclick="toUserCenter()">
    				<div class="user">
        				<div class="" id="userMessageDot" title="有新消息"></div>
        			</div>
        			<div class="user_name" style="min-width: 80px;text-align: center;">${user.realName}<c:if test="${user.realName eq ''}">${user.userName}</c:if><c:if test="${loginUserType == 2}">(客服)</c:if></div>
    			</div> --%>
    			
        		<div class="user_box" onclick="toUserCenter()">
    				<div class="userimg"></div>
        			<div class="user_name">${user.realName}<c:if test="${user.realName eq ''}">${user.userName}</c:if><c:if test="${loginUserType == 2}">(客服)</c:if><span class="unReadMessage" id="unReadMessageNum" title="未读消息" onclick="toUserCenter('2')"></span></div>
    			</div>
                <!-- <div id="smallQrCodeDiv" style="float:right;margin-top:18px;margin-right:15px;cursor:pointer;color:white;font-size:15px;">APP下载</div> -->
    			<c:choose>
    			<c:when test="${loginUserType == 4 }">
    				
    					<div class="main_search_box">
			                <input type="text" id='crewManuSearchInput' value="${crewInfo.crewName}" onkeyup="searchMenuCrew();" onClick="searchMenuCrew()" class="search_kuang21">
    						<input type="text" style="display: none;">
			                <div class="icon_cha1" onclick="searchMenuCrew();"><img src="<%=request.getContextPath()%>/images/search.png"></div>
	            		</div>
    				
    			</c:when>
    			<c:otherwise>
    			    <c:if test="${loginUserType == 3 || loginUserType == 2}">
	    				<div class="juzu">
		    				<div class="group_name" style="min-width: 80px;text-align: center;"><c:if test="${!empty crewInfo.crewName }">《${crewInfo.crewName}》</c:if></div>
	    				</div>
    				</c:if>
    			</c:otherwise>
        		</c:choose>
        	</div>
         	<div class="tab_manu_logo"><img src="<%=request.getContextPath()%>/images/logo.png" style="height: 50px"></div> 
         	<div class="tab_menu_code"><img src="<%=request.getContextPath()%>/images/code_xiao.png" style="height: 24px" id="smallQrCodeDiv"></div>
         	<div id="nav-menu" class="nav-menu" style="height: 52px;">
	         	<div id="tabMenuList" class="menuList" >
					
			    </div>
         	
         	</div>  
        </div>
        <div style="height: 32px;background: #0277bd;">
        	<div id="menu-topbar" class="secondary_nav_ul" style="">  <!-- id="topbar" -->
        		
        	</div>
        </div>
 </div>       
        <div>
            <!-- <div id="splitter">
                <div id="menu">
                </div>
                <div> -->
                <decorator:body />
                <!-- </div>
            </div> -->
        </div>
    </div>
<div id="jqxNotification">
</div>
<!--统一提示  -->
<div id="eventWindowAll" style="display: none;">
      <div>
                提示
      </div>
      <div>
         <div style="margin-top: 25px;font-size: 16px;margin-left: 10px;" id="eventWindowContent">
                       是否确定此操作？
         </div>
         <div>
           <div style=" margin: 30px 0px 0px 60px;">
              <input type="button" id="ok" value="确定" style="margin-right: 10px;" />
              <input type="button" id="mainCloseBtn" value="取消" />
           </div>
         </div>
       </div>
</div>
<!--财务密码弹出窗口  -->
<!-- <div id="financePwdWindow" class="jqx-window finance-password-win">
      <div>财务密码</div>
      <div>
          <ul>
              <li>
                  <p>请输入财务密码</p>
                  
              </li>
              <li>
                  <input class="finance-password" type="password" id="financePwd"  onkeyup="finanPwdWinKeyup(event)">
              </li>
          </ul>
          <div class="win-btn-list-div">
              <input type="button" value="确定" onclick="checkPassword()">
              <input type="button" value="取消" onclick="closePwdWindow()">
          </div>
       </div>
</div> -->
<!-- 验证财务密码、用户手机号 -->
<div id="validUserWin" class="jqx-window validate-user-win">
    <div>安全验证</div>
    <div>
        <ul>
            <li class="financepassli">
                <input class="finance-password" type="password" id="financePwd"  onkeyup="finanPwdWinKeyup(event)" placeHolder="请输入财务密码">
            </li>
            <li class="useripli">
                	当前用户手机号：<%=userInfo.getPhone() %>
            </li>
            <li class="useripli">
                <input class="verify-code" type="text" id="verifyCode" placeHolder="验证码，有效期为1分钟">
                <input type="button" class="get-verifycode-btn" id="getVerifyCodeBtn" value="获取验证码" onclick="obtainVerifyCode(this)">
            </li>
        </ul>
        <div class="win-btn-list-div">
        	<input type="hidden" id="validType">
            <input type="button" value="确定" onclick="checkPassword()">
            <input type="button" value="取消" onclick="closePwdWindow()">
        </div>
     </div>
</div>
<div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
<!-- 显示正在加载中 -->
<div id="loadingDiv" class="show-loading-container" style="display: none;">
	<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
</div>
<div id="messageRemind">
    <div style="cursor: pointer;">您有（&nbsp;<span id="messageRemindNum" style="font-weight: bold;color: #f2bbaa;"></span>&nbsp;）条新消息.</div>
</div>
</body>
</html>