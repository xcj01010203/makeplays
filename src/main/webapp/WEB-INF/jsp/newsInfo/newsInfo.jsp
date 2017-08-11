<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

 <!-- 网站开启对web app程序的支持 -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <!-- 在web app应用下状态条（屏幕顶部条）的颜色 -->
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
	<script type="text/javascript" src="${basePath}/js/scripts/jquery-1.10.2.min.js"></script>
    <link rel="stylesheet" href="${basePath}/css/newsInfo/demo.css"/>
    <link rel="stylesheet" href="${basePath}/css/newsInfo/newSlide.css"/>
    <link rel="stylesheet" href="${basePath}/css/newsInfo/swiper-3.2.5.min.css"/>
    <script type="text/javascript" src="${basePath}/js/newsInfo/news.js"></script>
	<script type="text/javascript" src="${basePath}/js/newsInfo/newsSlide.js"></script>
	<script type="text/javascript" src="${basePath}/js/newsInfo/hammer.js"></script>
	<script type="text/javascript" src="${basePath}/js/newsInfo/swiper-3.2.5.jquery.min.js"></script>
   <script type="text/javascript">
    var curPage = 1;
    $(function(){
    	var newsId=$("#newsInfoId").val();
    	
    	loadNews(newsId);
    });
    </script>
</head>
<body>
<input type="hidden" id="newsInfoId" value="${newsInfoId}">
<div class="commend-epwrap">
    <div class="mainarea clearfix">
        <div class="commend-epbody">
            <div class="commend-epbody-inner">
                <div class="ep-content-bg clearfix">
                    <div class="ep-content-main">
                        <h1 id="title" class="ep-h1"></h1>
                        <h3 id="subTitle" class="ep-h3"></h3>
                        <div class="ep-info cDGray" id="srcUrlAndName"></div>
                    </div>
                    <div class="endText" id="content">
                    </div>
                    <div class="iconfont">
                        <!-- <div class="iconfont-cai" id="caiCount"></div>
                        <div class="iconfont-zan" id="newsLikeCount"></div>
                        <div class="iconfont-ck" id="browing"></div> -->
                    </div>
                </div>
                
            </div>
        </div>
    </div>
</div>
<div id="slide">
    <div class="swiper-container">
        <div class="swiper-wrapper"></div>
        <div id="num-pagination">
            <span id="active-num"></span><span>/</span><span id="all-num"></span>
        </div>
    </div>
</div>


</body>
</html>