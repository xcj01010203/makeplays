<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%  Object hasCrewSetting = false;     //拥有剧组设置权限
	Object hasIndexFinanceSchedule = false;    //拥有首页财务进度权限
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
		java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
		if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CREW_SETTING) != null){
			hasCrewSetting = true;
		}
		
		if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.INDEX_FINANCE_SCHEDULE) != null){
			hasIndexFinanceSchedule = true;
		}
	}
	request.setAttribute("hasCrewSetting", hasCrewSetting);
	request.setAttribute("hasIndexFinanceSchedule", hasIndexFinanceSchedule);
	
	String path=request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<c:choose><c:when test="${userType == 0 }">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/home_style.css" type="text/css"></link>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/radialIndicator/radialIndicator.min.js"></script>
<title></title>

<script type="text/javascript">
var hasCrewSetting = "<%=hasCrewSetting %>";
$(document).ready(function () {
	var clientHeight=(window.screen.availHeight-65)*0.8;
	$("#container").css({height:clientHeight});
	//bingChar(${shootSchedule.finishedCrewAmount},${shootSchedule.totalCrewAmount - shootSchedule.finishedCrewAmount},"<fmt:formatNumber type="number" value="${shootSchedule.finishedCrewAmount}" pattern="#,##0"/>");
	
	//获取文武戏统计
	/* $.ajax({
		type:'post',
		dataType:'json',
		url:ctx+'index/getTotalSchedule',
		success:function(data){   //console.log(data)
			var militaryHtml = "";
			$.each(data.military,function(i,v){
				var val = v.split("/");
				if(i == "singingPlay"){
					militaryHtml += "文戏&nbsp;<em>"+val[0]+"</em>/"+val[1]+"&nbsp;场，";
				}else if(i == "actionPlay"){
					militaryHtml += "武戏&nbsp;<em>"+val[0]+"</em>/"+val[1]+"&nbsp;场，";
				}else if(i == "militaryPlay"){
					militaryHtml += "文武戏&nbsp;<em>"+val[0]+"</em>/"+val[1]+"&nbsp;场，";
				}
			});
			$(".zong_info").find("ul li:eq(1)").html(militaryHtml.substring(0,militaryHtml.length-1));
			//拍摄实景地&nbsp;共有27个，尚有11场戏没有落实景地
			var address = data.address;
			$(".zong_info").find("ul li:eq(2)").html("拍摄实景地&nbsp;共有"+address.number+"个，尚有"+(address.all - address.part)+"场戏没有落实景地");
			var actor = data.actor;
			$(".zong_info").find("ul li:eq(3)").html("主要角色"+actor.total+"名，尚有"+(actor.total-actor.part)+"个角色没有落实演员");
		}
	}); */
	
	//获取费用进度统计数据
	<c:if test="${hasIndexFinanceSchedule }">
		$.ajax({
			type:'post',
			dataType:'json',
			url:ctx+'/index/getFinanceSchedule',
			success:function(data){   //console.log(data)
				var totalBudget = "";
				$.each(data.countList,function(i,v){
					totalBudget += fmoney(v.budgetMoney,2) + "("+v.currencyCode+"),"
				});
				$(".feiyong_info").find("td:eq(1)>a").html(fmoney(data.totalMoney,2)+"("+data.stardard+")");  //总预算
				$(".other_info").find("ul li:eq(0)>a").html(fmoney(data.episodebudget,2)+"("+data.stardard+")"); //单集投资
				$(".feiyong_info").find("td:eq(0)>a").html(fmoney(data.balanceMoney,2)+"("+data.stardard+")");   //已支出
				/* var remainMoney = data.remainMoney;
				if(remainMoney>=0){
					$(".feiyong_wrap").find("ul li:eq(2)").append("剩余："+fmoney(remainMoney,2)+"元");  //剩余
				}else{
					$(".feiyong_wrap").find("ul li:eq(2)").append("剩余：<span style='color:red;'>("+fmoney(remainMoney,2).substring(1)+")</span>元"); //剩余
				} */
				$(".other_info").find("ul li:eq(1)").html("已拍摄"+data.days+"天，单日拍摄摊分成本：<a>"+fmoney(data.dayMoney,2)+"</a>元");  //已拍摄  单日拍摄摊分成本
				$(".other_info").find("ul li:eq(2)").html("昨日支出费用：<a>");
				var yesterdayPay = data.yesterdayPay;
				var payhtml = "";
				if(yesterdayPay.length>0){
					$.each(data.yesterdayPay,function(i,v){
						payhtml += v.num+"("+v.currencyCode+"),";
					});
				}else{
					payhtml += "0,";
				}
				
				$(".other_info").find("ul li:eq(2)").append(payhtml.substring(0,payhtml.length-1)+"</a>元");
				
				$('.bingtu_2').radialIndicator({
			        barColor: '#3c8dbc',
			        radius: 50,
			        barWidth: 10,
			        initValue: 0,
			        roundCorner : true,
			        percentage: true
			    });
				var radialObj = $('.bingtu_2').data('radialIndicator');
				//now you can use instance to call different method on the radial progress.
				//like
				if(data.totalMoney == 0)
					radialObj.animate(0);
				else
					radialObj.animate(accDiv(data.balanceMoney,data.totalMoney)*100);
			}
		});
	</c:if>
	//获取通告单信息
	$.ajax({
		type:'post',
		dateType:'json',
		url:ctx+'/index/getNoticeStatistics',
		success:function(data){
			var preNotice = data.preNotice;
			var length = 0;
			var yesterday = [];
			$.each(preNotice,function(i,v){
				var status = v.shootStatus;
				switch (status) {
				case 0:
					yesterday.push(",甩戏<a>"+v.num+"</a>场");
					length += v.num;
					break;
					
				case 1:
					yesterday.push(",部分完成<a>"+v.num+"</a>场");
					length += v.num;
					break;
					
				case 2:
					yesterday.push(",完成<a>"+v.num+"</a>场");
					length += v.num;
					break;
					
				case 3:
					yesterday.push(",删戏<a>"+v.num+"</a>场");
					length += v.num;
					break;
					
				case 4:
					yesterday.push(",加戏部分完成<a>"+v.num+"</a>场");
					length += v.num;
					break;
					
				case 5:
					yesterday.push(",加戏已完成<a>"+v.num+"</a>场");
					length += v.num;
					break;

				}
				
			});
			//html.push(",部分完成<a>"+v.num+"</a>场");
			$(".zuori_date").html("共<a>"+length+"</a>场"+yesterday.join(''));
			
			var newNotice = [];
			$.each(data.todayNotice,function(i,v){
				var date = new Date(v.noticeDate);
				var date1 = (date.getMonth()+1)+"月"+date.getDate()+"号";
				newNotice.push("<div class='zuixin'><a href='<%=request.getContextPath()%>/notice/toNoticeList'>"+v.groupName+"&nbsp;"+date1+"</a></div>");
			});
			$(".zuixin_info").append(newNotice.join(''));
			
		}
	});
	
	$('.bingtu').radialIndicator({
        barColor: '#3c8dbc',
        radius: 50,
        barWidth: 10,
        initValue: 0,
        roundCorner : true,
        percentage: true
    });
	var radialObj = $('.bingtu').data('radialIndicator');
	//now you can use instance to call different method on the radial progress.
	//like
	if('${shootSchedule.totalCrewAmount}' == '0')
		radialObj.animate(0);
	else
		radialObj.animate(accDiv(${shootSchedule.finishedCrewAmount},${shootSchedule.totalCrewAmount})*100);
	
	//获取通联表
	$.ajax({
		type: 'post',
		dataType: 'json',
		url: '/contact/queryCrewContactList',
		data: {ifOpen: true},
		success:function(data){
			var html = "<tr class='table_head'><td>职务</td><td>姓名</td><td>电话</td></tr>";
			$.each(data,function(i,v){
				if(v.sysRoleNames == null){
					html += "<tr class='td_border'><td></td><td>"+v.contactName+"</td><td>"+v.phone+"</td></tr>";
				} else {
				   html += "<tr class='td_border'><td><p style='white-space: nowrap;text-overflow: ellipsis;overflow: hidden;width: 200px;'>"+v.sysRoleNames+"</p></td><td>"+v.contactName+"</td><td>"+v.phone+"</td></tr>";
				}
			});
			$(".tonglian_wrap table").html(html);
		}
	});
	
	//加载进度条
	$(".jindu_kuang").each(function(){
		var _this = $(this);
		var count = _this.find(".jindu_date").html().split("/");
		var per = accDiv(count[0],count[1])*100 + "%";
		_this.find(".jindu_tiao").css("width",per);
	});
});


/**
 * 格式化金额
 * @param s
 * @param n
 * @returns {String}
 */
function fmoney(s, n)   
{   
   n = n > 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(),   
   r = s.split(".")[1];   
   t = "";   
   for(var i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }   
   return t.split("").reverse().join("") + "." + r;   
}
 
//除法函数  
 function accDiv(arg1, arg2) {  
     var t1 = 0, t2 = 0, r1, r2;  
     try {  
         t1 = arg1.toString().split(".")[1].length;  
     }  
     catch (e) {  
     }  
     try {  
         t2 = arg2.toString().split(".")[1].length;  
     }  
     catch (e) {  
     }  
     with (Math) {  
         r1 = Number(arg1.toString().replace(".", ""));  
         r2 = Number(arg2.toString().replace(".", ""));  
         return (r1 / r2) * pow(10, t2 - t1);  
     }  
 }

</script>    

    
</head>
<body>
<div id="container" style="overflow: auto;"> 
<div class="wrap" style="height: 93%; min-width: 1300px;">
<header>
	<div class="juzu_name">《${crewInfo.crewName}》</div>
    <div class="juzu_index">剧组
    <c:if test="${projectType == 1 }">
    	<font color="red" size="3">（注：试用剧组，该剧组数据每天上午8:00清空）</font>
    </c:if>
    </div>
    <c:if test="${projectType != 1 }">
    <div class="biaoyu"><img src="<%=path%>/images/kaiji.png"></div>
    </c:if>
    <div class="time_show"><!-- 已开机&nbsp;27&nbsp;天，预计剩余&nbsp;13&nbsp;天 -->
     <c:choose>
    <c:when test="${status == 1 }">
    	尚未设定拍摄周期
    	<c:if test="${hasCrewSetting }">
    	，<a href='<%=request.getContextPath()%>/crewManager/toCrewSettingsPage'>去设置</a>
    	</c:if>
    </c:when>
    <c:when test="${status == 2 }">
    	距离开机还有&nbsp;${forwordStartDate }&nbsp;天
    </c:when>
    <c:when test="${status == 3 }">
    	已开机&nbsp;${endStartDate }&nbsp;天，预计剩余&nbsp;${forwordEndDate }&nbsp;天 
    </c:when>
    <c:when test="${status == 4 }">
    	已杀青
    </c:when>
    </c:choose>
    </div>
    <div style="color: red;margin-left: 10px;line-height: 25px;font-size: 14px;">${crewStatusMessage } </div>
</header>


	<div class="zong_wrap">
    	<div class="title">拍摄进度</div>
        <div class="zong_bingtu">
        	<div class="bingtu">
            	<!-- <img src="images/bingtu.png"> -->
            </div>
            <div class="tuli">
            	<div>已完成拍摄&nbsp;<em><fmt:formatNumber type="number" value="${shootSchedule.finishedCrewAmount}" pattern="#,##0"/>
            	</em>/<fmt:formatNumber type="number" value="${shootSchedule.totalCrewAmount}" pattern="#,##0"/>&nbsp;场</div>
            </div>
        </div>
        <div class="juese_wrap">
    		
            <table rules=all>
                <tr class="table_head">
                    <th>角色</td>
                    <td>进度</td>
                    
                </tr>
                <!-- tr class="td_border">
                    <th>王大锤</td>
                    <td>
                    	<div class="jindu_kuang">
                        	<div class="jindu_tiao"></div>
                            <div class="jindu_date">123/321</div>
                        </div>
                    </td>
                    
                </tr> -->
                <c:forEach items="${roleCount}" var="role">
            	<tr class="td_border">
            		<td>${role.viewRoleName }</td>
                	<td>
                		<div class="jindu_kuang">
                        	<div class="jindu_tiao"></div>
                            <div class="jindu_date">${role.endcount }/${role.crewAmountByview }</div>
                        </div>
                	</td>
            	</tr>
            </c:forEach>
            </table>
    	</div>	
        <div class="tonggao_wrap">
            
            <div class="zuori_info">
            	<div class="zuori_title">昨日通告：</div>
                <div class="zuori_date"><!-- 共<a>35</a>场，完成<a>35</a>场，甩戏<a>3</a>场，加戏<a>1</a>场 --></div>    
            </div>
            <div class="zuixin_info">
            	<div class="zuixin">最新通告：</div>
                <!-- <div class="zuixin"><a>A组&nbsp;8月7号</a></div>
                <div class="zuixin"><a>B组&nbsp;8月7号</a></div>
                <div class="zuixin"><a>C组&nbsp;8月7号</a></div> -->
        	</div>
        </div>
        
    </div>
   

	
        <div class="tonglian_wrap" style="height: 76%;">
            <div class="title">联系表</div>
            <table rules=all>
                <tr class="table_head">
                	<td>职务</td>
                    <td>组别</td>
                    <td>姓名</td>
                    <td>电话</td>
                </tr>
                <!-- <tr class="td_border">
                    <td>导演</td>
                    <td>A</td>
                    <td>王大锤</td>
                    <td>15201020304</td>
                </tr> -->
                
            </table>
        </div>
        
   <c:if test="false">
	    <div class="feiyong_wrap">
	    	<div class="title">费用进度</div>
	        <div class="feiyong_info_wrap">
	        	<div class="bingtu_2">
	            	<!-- <img src="images/bingtu.png"> -->
	            </div>
	            <div class="feiyong_info">
	            <table>
	            	<tr>
	            		<th>已支出：</th>
	            		<td><a style="color:#3c8dbc;">1312313.00</a>元</td>
	            	</tr>
	            	<tr>
	            		<th>总预算：</th>
	            		<td><a>1531313.00</a>元</td>
	            	</tr>
	            </table>
	                <!-- <div>已支出：<a style="color:#3c8dbc;">1312313.00</a>元</div>
	                <div>总预算：<a>1531313.00</a>元</div> -->
	            </div>
	        </div>
	        <div class="other_info">
	        	<ul>
	            	<li>单集投资：<a>23134</a>万</li>
	                <li>已拍摄天，单日拍摄摊分成本：<a>213123</a>元</li>
	                <li>昨日支出费用：<a>21313</a>元</li>
	            </ul>
	        </div>
	        
	       <!-- <div class="feiyong_more"><a>查看详情</a></div>-->        
	    </div>
    </c:if>
    
</div>
</div>
<!-- <DIV id=img1 style="Z-INDEX: 100; right: 15px; WIDTH: 130px; POSITION: absolute; TOP: 260px; HEIGHT: 61px;

 visibility: visible;"><img src="../images/qr.png" width="130" height="130" border="0">
 <div>扫码下载android客户端</div>
 </DIV> -->
</body>
</c:when>

<c:when test="${userType == 1 }">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

</body>
</c:when>
</c:choose>
</html>
