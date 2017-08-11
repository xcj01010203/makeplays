<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="<%=request.getContextPath()%>/js/report/base.css" rel="stylesheet" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/report.js"></script>
    <style>
    	.tb_head{
    		border-top:1px solid #D7DAE4;
    	}
    	.search_roleName{
    		margin-left:5%;
    		position:absolute;
    		z-index:100;
    		padding-top:7px;
    	}
    	.search_roleName ul{
	    	display:none;
	    	background-color: #ebebeb;
	    	border: 1px solid #ebebeb;
	    	max-height: 300px;
			overflow-y: scroll;
			overflow-x: hidden;
			width:65%;
			margin-left:21%
		}
		.search_roleName ul li{
			text-align:center;
			line-height:25px;
			height:25px;
			font-weight:bold;
			}
		.search_roleName ul li:hover{
			background-color:#1e90ff;
			color:#fff;
			cursor:pointer;
			}
		.role_check_search{
			padding:0px 3px;
			line-height:22px;
			height:22px;
			width:100%;
		}
		.tr_cur {
   	 background-color: rgba(198,198,201,0.5);
	}
    </style>
    <script>
		$(function(){
			 topbarInnerText("进度表&&拍摄地点统计");
			$(".f_r").find(":radio").click(function(){
				$(".f_r").find(".form_1").submit();
			});
			//点击 TR 变色
		    $('tbody tr').click(function(ev){
		        $(this).addClass('tr_cur').siblings('.tr_cur').removeClass('tr_cur');
		        ev.stopPropagation();
		    });
		    restTableScroll();
			$(window).resize(function(){
				restTableScroll();
			});
			
			//鼠标移动到td上，显示详情
			tdHoverLayer($('.statis_fold:eq(1), .statis_fold:eq(2)').find('tbody'));
			
			var timer;
		})
		/**
		* 重置当前页面table
		*/
		function restTableScroll(){
			setTimeout(function(){
		    	$('.tb_body').height($(window).height()- 250);
		    }, 300);
		}
</script>
</head>
<body id="statis_page" >
    <div id="content" style="margin-left:0px">
           <div id="con_top">
          <!--   <div id="con_top_crumb" >
            	<span class="f_l">进度表<em>></em>拍摄地点进度</span>
            </div> -->
            <c:if test="${locationList ne null}">
	            <div class="search_roleName" style="left: 10px;" tabindex="-1">
	            	<label>
						<em>查询：</em>
					 	<input type="text"  style="width: 250px;" class="role_check_search">
					 </label>
	            	<ul style="width: 320px;margin-left: 39px;margin-top: 5px;" >
		            	<c:forEach  items="${locationList }" var="location">
			            	<li >
								<span>${location}</span>
							</li>
		            	</c:forEach>
					</ul>
	            </div>
            </c:if>
        </div>

        <!-- 内容区 begin -->
        <div id="con_box" style="width: 100%;">
        
        <dl class="statis_fold">
                <dt class="open fold_head" style="display: none;background-color: #ebebeb;" >按拍摄地点主场景统计</dt>
                <dd class="fold_body">
                	<div class="tb_box">
                		<div class="tb_head">
                			<table>
                			   <thead>
	                			   <tr style="background-color: #ebebeb;"> 
	                					<td class="w120"><div class="td_div" >拍摄地点/戏量</div></td>
	                					<td class="" style="width:180px;border-right: 5px solid #ccc;"> 
	                						<div class="td_div">               						
			    								主场景/戏量
		    								</div>
	                					</td>
	                					<td> 
	                						<div class="td_div" style="background-color:#d6d6d6;">               						
			    								二级场景/戏量
		    								</div>
	                					</td>
	                				</tr>
                				</thead>
                			</table>
                		</div>
                		<div class="tb_body">
                			<table>
	                			 <thead>
	                			   <tr>
	                					<td class="w120"><div class="td_div">拍摄地点</div></td>
	                					<td class="" style="width:180px;border-right: 5px solid #ccc;"> 
	                						<div class="td_div">               						
			    								主场景/戏量
		    								</div>
	                					</td>
	                					<td > 
	                						<div class="td_div">               						
			    								二级场景/戏量
		    								</div>
	                					</td>
	                				</tr>
                				</thead>
			                   <tbody>
                			    <c:forEach items="${location }"  var="locationView" >
	                			   <tr>
	                			   		<c:choose>
	                			   			<c:when test="${locationView.homeViewList ne null }">
	                			   				<td class="w80" align="center" rowspan="${fn:length(locationView.homeViewList)}">
	                			   			</c:when>
	                			   			<c:otherwise>
	                			   				<td class="w80" ></c:otherwise>
	                			   		</c:choose>     
	                						<div style="" title="${locationView.shootLocation }" class="td_div">${locationView.shootLocation}</div>
	                						<div><fmt:formatNumber type="number" value="${locationView.locationCountTotal}" maxFractionDigits="0"/>场/${locationView.locationPageTotal}页</div>
	                					</td>
              			    				<c:forEach items="${locationView.homeViewList }" var="locations" varStatus="loindex">
              			    				<c:if test="${loindex.index gt 0 }">
												<tr >
													<c:if test="${locationView.homeViewList eq null }">
			                			   				<td></td>	
			                			   			</c:if>		                			   			
              			    				</c:if>
			                					<td align="center" style="border-right: 5px solid #ccc;"> 
			                						<div class="td_div" >    
				    									<%-- <div>${locations.homeView.firstViewLocation}</div> --%>
				    									<div title="${locations.homeView.firstViewLocation }">${fn:substring(locations.homeView.firstViewLocation, 0, 12)}<br/>
				    										${fn:substring(locations.homeView.firstViewLocation, 12, fn:length(locations.homeView.firstViewLocation))}
				    									</div>
				    									<div ><fmt:formatNumber type="number" value="${locations.crewByHomeView}" maxFractionDigits="0"/>场/${locations.crewByHomePage}页</div>
				    								</div>
			                					</td>
			                					<td >
			                						<div class="shoot_addr_tit">
			                								<c:forEach items="${locations.locationBaseList}" var="viewBase">
						                						<div  class="shoot_addr_wrap" >               						
							    									<div align="center" title="${viewBase.secondViewLocation}" class="w120 shoot_addr_top">${viewBase.secondViewLocation}</div>
							    									<div align="center" class="w120 shoot_addr_dow mt10 "><fmt:formatNumber type="number" value="${viewBase.viewNo}" maxFractionDigits="0"/>场/${viewBase.pageCount}页</div>
							    								</div>
					    									</c:forEach>
					    							</div>
					    							
			                					</td>
			                					</c:forEach>
			                				</tr>
			                				
		                			</c:forEach>
			                   </tbody>
                   			</table>
                		</div>
                	</div>
                </dd>
            </dl>
        	<dl></dl>
            
        </div>
    </div>
</div>

</body>
</html>