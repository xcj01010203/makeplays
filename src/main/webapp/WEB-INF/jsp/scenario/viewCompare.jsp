<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/sceneAnalysis/viewCompare.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/viewCompare.css">

  </head>
  
  <body>
	<input id="seriesViewNoInput" type="hidden" value="${seriesViewNo}">
	<input id="isViewList" type="hidden" value="${isViewList}">
	<div id="scenario_box">
	    <div class="scenario_l" id="newOldCompareDiv">
	        <h1>新剧本</h1>
	        <div class="sce_new">
	        </div>
	        <h1>老剧本</h1>
	        <div class="sce_old">
	        </div>
	    </div>
	    <div class="scenario_r" sid="">
	        <table>
	            <thead>
	                <tr>
	                    <td class="view-info-td" ></td>
	                    <td><div class="td_div">新场景信息</div></td>
	                    <td><div class="td_div">原场景信息</div></td>
	                </tr>
	            </thead>
	            <tbody>
	                <tr>
	                    <td id="crewTypeTd">
	                    </td>
	                    <td id="newViewNoTd" class="td_set_scene">
	                    </td>
	                    <td id = "oldViewNoTd">
	                    </td>
	                </tr>
	                <tr id="newViewAtmosphereTr">
	                    <td><div class="td_div">气氛/内外景</div></td>
	                    <td id="newViewAtmosphereTd">
	                    </td>
	                    <td id="oldViewAtmosphereTd">
	                    </td>
	                </tr>
                    <!-- <tr id="comprViewSeaonTr">
                        <td><div class="td_div">季节</div></td>
                        <td>
	                        <div id="newViewSeason" class="td_div">
	                        </div>
                        </td>
                        <td>
	                        <div id= "oldViewSeason" class="td_div">
	                        </div>
                        </td>
                    </tr> -->
                    <tr id="comprViewShootLocation">
                        <td><div class="td_div">拍摄地</div></td>
                        <td>
	                        <div id = "newViewShootLocation" class="td_div">
	                        </div>
                        </td>
                        <td>
                        	<div id = "oldViewShootLocation" class="td_div">
                        	</div>
                        </td>
                    </tr>
	                <tr id="comprViewFirstLocation" >
	                    <td><div class="td_div">主场景</div></td>
	                    <td><div id ="newViewFirstLocation" class="td_div"></div></td>
	                    <td><div id= "oldViewFirstLocation" class="td_div"></div></td>
	                </tr>
	                <tr id="comprViewSecondLocation">
	                    <td><div class="td_div">次场景</div></td>
	                    <td><div id="newViewSecondLocation" class="td_div"></div></td>
	                    <td><div id ="oldViewSecondLocation" class="td_div"></div></td>
	                </tr>
	                <tr id ="comprViewThirdLocation">
	                    <td><div class="td_div">三级场景</div></td>
	                    <td><div id="newViewThirdLocation" class="td_div"></div></td>
	                    <td><div id="oldViewThirdLocation" class="td_div"></div></td>
	                </tr>
	                <tr id="comprViewMainContent">
                        <td><div class="td_div">主要内容</div></td>
                        <td><div id="newViewMainContent" class="td_div"></div></td>
                        <td><div id="oldViewMainContent" class="td_div"></div></td>
                    </tr>
	                <tr id="comprViewRoleNames">
	                    <td><div class="td_div">主要演员</div></td>
	                    <td><div id="newViewRoleNames" class="td_div"></div></td>
	                    <td><div id="oldViewRoleNames" class="td_div"></div></td>
	                </tr>
	                <tr id="comprViewguestNames">
	                    <td><div class="td_div">特约演员</div></td>
	                    <td><div id="newViewGuestNames" class="td_div"></div></td>
	                    <td><div id="oldViewGuestNames" class="td_div"></div></td>
	                </tr>
	                <tr id="comprViewMassNames" >
	                    <td><div class="td_div">群众演员</div></td>
	                    <td><div id="newViewMassNames" class="td_div"></div></td>
	                    <td><div id="oldViewMassNames" class="td_div"></div></td>
	                </tr>
                    <tr id="comprViewClothes">
                        <td><div class="td_div">服装</div></td>
                        <td><div id="newViewClothes" class="td_div"></div></td>
                        <td><div id="oldViewClothes" class="td_div"></div></td>
                    </tr>
                    <tr id="comprViewMakeups" >
                        <td><div class="td_div">化妆</div></td>
                        <td><div id="newViewMakeups" class="td_div"></div></td>
                        <td><div id="oldViewMakeups" class="td_div"></div></td>
                    </tr>
	                <tr id="comprViewCommonProps">
	                    <td><div class="td_div">道具</div></td>
	                    <td><div id="newViewCommonProps" class="td_div"></div></td>
	                    <td><div id="oldViewCommonProps" class="td_div"></div></td>
	                </tr>
	                <tr id="comprViewRemark">
	                    <td><div class="td_div">备注</div></td>
	                    <td><div id="newViewRemark" class="td_div"></div></td>
	                    <td><div id="oldViewRemark" class="td_div"></div></td>
	                </tr>
	               <!--  <tr id="comprViewViewType">
                        <td><div class="td_div">文武戏</div></td>
                        <td>
	                        <div id="newViewType" class="td_div">
	                        </div>
	                    </td>
                        <td><div id="oldViewType" class="td_div"></div></td>
                    </tr> -->
	            </tbody>
	        </table>
	    </div>
	</div>
  </body>
</html>
