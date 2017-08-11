<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <link href="<%=request.getContextPath()%>/js/report/base.css" rel="stylesheet" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/report.js"></script>
    <style>
    	.tb_head{
    		border-top:1px solid #D7DAE4;
    	}
    	.search_roleName{
    		margin-left:450px;
    		position:absolute;
    		z-index:100;
    		padding-top:8px;
    	}
    	.search_roleName ul{
	    	display:none;
	    	background-color: #dadff3;
	    	border: 1px solid #777f96;
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
			background-color:#464e6b;
			color:#fff;
			cursor:pointer;
			}
		.role_check_search{
			padding:0px 3px;
			line-height:22px;
			height:22px;
			width:61%;
		}
    </style>
    <script>
		$(function(){
			var tabValue ="${tabValue}"; //$("input[name=tabValue]").val();
			$("#tabswidget").on("selected",function(event){
		    	var item = event.args.item;
		    	if(item == 0){ //切换到
		    		$("#tabValue").val(0);
		    		$("#tabTable").val(0);
		    		$("#formSub").find(".form_1").submit();
		    	}else if(item == 1){  //切换到   	
		    		$("#tabValue").val(1);
		    		$("#tabTable").val(1);
		    		$("#formSub").find(".form_1").submit();
		    	}else if(item == 2){  //切换到
		    		$("#tabValue").val(2);
		    		$("#tabTable").val(2);
		    		$("#formSub").find(".form_1").submit();
		    	}else{
		    		$("#tabValue").val(3);
		    		$("#tabTable").val(3);
		    		$("#formSub").find(".form_1").submit();
		    	}
		    });
			//页面tab初始化
			if(tabValue == 0 || tabValue==""){
				$("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%',selectedItem: 0 });
				$("#lmaFour").removeClass("open");$("#lmaThree").removeClass("open");$("#lmaTwo").removeClass("open");
	    		$("#lmaOne").addClass("open");
			}else if(tabValue == 1){
				$("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%',selectedItem: 1 });
				$("#lmaOne").removeClass("open");$("#lmaThree").removeClass("open");$("#lmaFour").removeClass("open");
	    		$("#lmaTwo").addClass("open");
			}else if(tabValue == 2){
				$("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%',selectedItem: 2 });
				$("#lmaTwo").removeClass("open");$("#lmaOne").removeClass("open");$("#lmaFour").removeClass("open");
	    		$("#lmaThree").addClass("open");
			}else{
				$("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%',selectedItem: 3 });
				$("#lmaThree").removeClass("open");$("#lmaOne").removeClass("open");$("#lmaTwo").removeClass("open");
	    		$("#lmaFour").addClass("open");
			}
			var tabTable='${tabTable}';
			if(tabTable==0 || tabTable==""){
				//按拍摄地点主场景统计
				$.ajax({
			        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getByAddress",  
			        type: 'POST',
			        data: {
			        	roleType:"${roleType}"
			         },
			        async: false,
			        dataType: 'JSON',
			        success:function(data){
						if(data!="" && data!=null)
						{
							$("#fistMianAddress").append(data.sbStr);
							
						}
			        }
			  	});
			}
			if(tabTable==1){
				//按集统计
				$.ajax({
			        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getByContSet",  
			        type: 'POST',
			        data: {
			        	roleType:"${roleType}"
			         },
			        async: false,
			        dataType: 'JSON',
			        success:function(data){
						if(data!="" && data!=null)
						{
							$("#setNo_one").append(data.setNo);
							$("#setNo_two").append(data.setNo);
							$("#marketForm").append(data.marketForm);
						}
			        }
			  	});
			}
			if(tabTable==2){
				//按拍摄地点统计 
				$.ajax({
			        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getbyShootLocation",  
			        type: 'POST',
			        data: {
			        	roleType:"${roleType}"
			         },
			        async: false,
			        dataType: 'JSON',
			        success:function(data){
						if(data!="" && data!=null)
						{
							$("#addressTite_one").append(data.headStr);
							$("#addressTite_two").append(data.headStr);
							$("#addressThree").append(data.strTo);
							
						}
			        }
			  	});
			}
			if(tabTable==3){
				//按场景统计 
				$.ajax({
			        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getbyViewAddress",  
			        type: 'POST',
			        data: {
			        	roleType:"${roleType}"
			         },
			        async: false,
			        dataType: 'JSON',
			        success:function(data){
						if(data!="" && data!=null)
						{
							$("#viewForm").append(data.viewForm);
							$("#shootLocation_one").append(data.shootLocation);
							$("#shootLocation_two").append(data.shootLocation);
							
						}
			        }
			  	});
			}
			$("#formSub").find(":radio").click(function(){
				$("#formSub").find(".form_1").submit();
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
			tdHoverLayer();
			
		})
		/**
		* 重置当前页面table
		*/
		function restTableScroll(){
			setTimeout(function(){
		    	$('.tb_body').height($(window).height()- 220);
		    }, 300);
		}
	</script>
</head>
<body id="statis_page" >
<div class="wrap clearfix">
    <div id="content" style="margin-left: 0px">
        <div id="con_top">
            <div id="con_top_crumb" >
            	<span class="f_l">进度表<em>></em>角色戏量</span>
            </div>
            <c:if test='${roleNameList != null}'>
	            <div class=" search_roleName ">
	            	<label>
						<em>查询：</em>
					 	<input type="text" class="role_check_search">
					 </label>
	            	<ul id="titeSo" style="background-color: #e7effa">
		            	<c:forEach items="${roleNameList }" var="roleName">
			            	<li >
								<span>${roleName}</span>
							</li>
		            	</c:forEach>
					</ul>
	            </div>
            </c:if>
            <div  id="formSub" style="line-height: 38px;margin-right:30px;">
            	<form class="form_1" method="post" action="<%=request.getContextPath() %>/lmaroleCrewReportManager/roleCrewAmount">
	                <label class="mr8">
	                		<input type="radio" name="roleType"<c:if test="${roleType eq 1 }">checked="checked"</c:if>value="1"/>
	                		<input type="hidden" name="tabValue" id="tabValue" value="${tabValue }">
	                		<input type="hidden" name="tabTable" id="tabTable" value="${tabTable }">
	                    <span>主要演员</span>
	                </label>
	                <label>
	                		<input type="radio"  name="roleType" <c:if test="${roleType eq 2 }">checked="checked"</c:if> value="2"/>
	                    <span>特约演员</span>
	                </label>
                </form>
            </div>
        </div>
        <!-- 内容区 begin -->
        <div id="con_box" style="width: 100%; padding: 0px;">
        <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
         <ul>
             <li style="margin-left: 30px;">分拍摄地主场景</li> 
             <li style="margin-left: 30px;" >分集</li>
             <li style="margin-left: 30px;">分拍摄地点</li>                          
             <li style="margin-left: 30px;">分场景</li>
         </ul>
         <!-- 按拍摄地点主场景统计 -->
        	<div>
        	<dl class="statis_fold" >
                <dt class="open fold_head" id="lmaOne" style="display: none;"></dt>
                <dd class="fold_body" id="oneDd">
                	<div class="tb_box">
                		<div class="tb_head">
                			<table>
                			   <thead>
	                			   <tr>
	                					<td class="" style="width:150px;"><div class="td_div">角色/戏量</div></td>
	                					<!-- <td class="w100"><div class="td_div">主场景个数</div></td> -->
	                					<td class="" style="border-right: 5px solid #ccc;width:180px;" ><div class="td_div" >拍摄地点/戏量</div></td>
	                					<td > 
	                						<div class="td_div" style="background-color:#E7EFE2">               						
			    								主场景/戏量
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
	                					<td class="" style="width:150px;"><div class="td_div">角色/戏量</div></td>
	                					<!-- <td class="w100"><div class="td_div">主场景个数</div></td> -->
	                					<td class="" style="border-right: 5px solid #ccc;width:180px;"><div class="td_div" >拍摄地点/戏量</div></td>
	                					<td > 
	                						<div class="td_div">               						
			    								主场景/戏量
		    								</div>
	                					</td>
	                				</tr>
                				</thead>
			                       <tbody id="fistMianAddress">
									      <!--  -->
			                       </tbody>
                   			</table>
                		</div>
                	</div> 
                </dd>
            </dl>
            </div>
            <!-- 按集统计 -->
            <div>
            <dl class="statis_fold">
            	<dt class="open fold_head" id="lmaTwo" style="display: none;"></dt>
                <dd class="fold_body " id="twoDd ">
                <div class="tb_box">
                		<div class="tb_head" style="background: #e7effa;font: 12px '黑体';position: relative;z-index: 12;overflow: hidden;" >
                			<table>
			                   <thead >
				                   <tr id="setNo_one" align="center">
				                   	   <td class="" style="width: 110px;"><div class="td_div">角色名称</div></td>
				                   	   <td class="" style="width: 110px;"><div class="td_div">演员姓名</div></td>
				                   	   <td class="" style="border-right: 5px solid #ccc; width: 110px;"><div class="td_div" ">戏量</div></td>
			                       </tr>
			                   </thead>
                			</table>
                		</div>
                		<div class="tb_body">
                			<table>
                		     <thead >
			                   <tr id="setNo_two" align='center'>
			                   	   <td class="" style="width: 110px;"><div class="td_div">角色名称</div></td>
			                   	   <td class="" style="width: 110px;"><div class="td_div">演员姓名</div></td>
			                   	   <td class="" style="border-right: 5px solid #ccc;width: 110px;"><div class="td_div" style="border-right: 5px solid #ccc">戏量</div></td>
		                       </tr>
		                   </thead>
	                       <tbody id="marketForm" >
	                       </tbody>
                   	</table>
                		</div>
                	</div>
                <!--  -->
                </dd>
            </dl>
            
            </div>
            <div>
            <!--  分拍摄地点 -->
              <dl class="statis_fold byShootAddress">
            	<dt class="open fold_head" id="lmaThree" style="display: none;"></dt>
                <dd class="fold_body " id="threeDd">
               		<div class="tb_box">
               			<div class="tb_head">
               				 <table>
			                       <thead>
			                            <tr id="addressTite_one">
			                                <td class="w100"><div class="td_div">角色名称</div></td>
			                                <td class="w100"><div class="td_div">演员姓名</div></td>
			                                <td class="w100"><div class="td_div">戏量</div></td>
			                                <td class="w100" style="border-right: 5px solid #ccc;"><div class="td_div">拍摄地点个数</div></td>
			                            </tr>
			                       </thead>
               				 </table>
               			</div>
               			<div class="tb_body">
               				<table>
                       <thead>
                            <tr id="addressTite_two">
                                <td class="w100"><div class="td_div">角色名称</div></td>
                                <td class="w100"><div class="td_div">演员姓名</div></td>
                                <td class="w100"><div class="td_div">戏量</div></td>
                                <td class="w100" style="border-right: 5px solid #ccc;"><div class="td_div">拍摄地点个数</div></td>
                            </tr>
                       </thead>
                       <tbody id="addressThree">
                      
                       </tbody>
                   </table>
               			</div>
               		</div>
                </dd>
            </dl> 
            </div>
          <div>
            <dl class="statis_fold byShootAddress">
            	<dt class="open fold_head" id="lmaFour" style="display: none;"></dt>
                <dd class="fold_body " id="fourDd">
               		<div class="tb_box" >
               			<div class="tb_head">
               				 <table>
               				
			                       <thead>
			                            <tr id="shootLocation_one">
			                                <td class="w100"><div class="td_div">角色名称</div></td>
			                                <td class="w100"><div class="td_div">演员姓名</div></td>
			                                <td class="w100"><div class="td_div">戏量</div></td>
			                                <td class="w100" style="border-right: 5px solid #ccc;"><div class="td_div">场景个数</div></td>
			                            </tr>
			                       </thead>
			                      
               				 </table>
               			</div>
               			<div class="tb_body">
               				<table>
                       <thead>
                            <tr id="shootLocation_two">
                                <td class="w100"><div class="td_div">角色名称</div></td>
                                <td class="w100"><div class="td_div">演员姓名</div></td>
                                <td class="w100"><div class="td_div">戏量</div></td>
                                <td class="w100" style="border-right: 5px solid #ccc;"><div class="td_div">场景个数</div></td>
                            </tr>
                       </thead>
                       <tbody id="viewForm">
	                           
                       </tbody>
                     <%--  </c:otherwise>
                       </c:choose> --%>
                   </table>
               			</div>
               		</div>
                </dd>
            </dl> 
               		</div>
            </div> 
        </div>
       </div>
    </div>

</body>
</html>