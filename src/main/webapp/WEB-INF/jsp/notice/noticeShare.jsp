<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!doctype html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width,initial-scale=1">
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/notice/noticeShare/noticeShare.css">
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/notice/noticeShare/commes.css" />
   <link rel="stylesheet" href="<%=basePath%>/css/notice/noticeShare/photoswipe.css"/> 
    <link rel="stylesheet" href="<%=basePath%>/css/notice/noticeShare/default-skin/default-skin.css"/>
    
  <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/notice/noticeShare.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/swiper/photoswipe.min.js"></script> 
  <script type="text/javascript" src="js/swiper/photoswipe-ui-default.min.js"></script>
  
</head>

<body>
      <div class="my-container">
      <input type="hidden" id="userId" value="${userId}">
      <input type="hidden" id="crewId" value="${crewId}">
      <input type="hidden" id="noticeId" value="${noticeId}">
      <div class="tab-list">
          <ul class="tab-list-ul">
              <li class="click" onclick="showBasicInfo(this);">基本信息</li>
                <li onclick="showScenceInfo(this);">场次信息</li>
                <li onclick="showNoticeAllContent(this);">通告全文</li>
            </ul>
        </div>
        <div class="tab-main-content" id="mainContent">
        <!--基本信息-->
          <div class="tab-content" id="basicInfo">
                <div class="banben-info">
                    <h3 class="banben-num" id="version"></h3>
                    <span class="banben-date" id="noticeTimeUpdateTime"></span>
                </div>
            
                <!--演员通告单信息-->
                <div class="notice-info-list" id="actorPersonNotice">
                    <h5 class="notice-title style-color">
                        <label></label>                 
                        <span>个人通告</span>
                    </h5>
                    <div class="notice-content back-color">
                        <ul class="notice-content-ul">
                            <li>
                                <span>个人提示:</span>
                                <span id="roleNames"></span>
                            </li>
                            <li>
                                <span>拍摄任务:</span>
                                <span id="viewNos"></span>
                            </li>
                            <li>
                                <span>拍摄地点:</span>
                                <span id="converLocationInfo"></span>
                            </li>
                            <li>
                                <span>搭戏角色:</span>
                                <span id="cooperators"></span>
                            </li>
                            <li>
                                <span>化妆地:</span>
                                <span id="makeup"></span>
                            </li>
                            <li>
                                <span>化妆:</span>
                                <span id="arriveTime"></span>
                            </li>
                            <li>
                                <span>出发:</span>
                                <span id="getOutTime"></span>
                            </li>
                        </ul>
                    </div>
                </div>
            
                <!--通告概况-->
                <div class="notice-info-list">
                    <h5 class="notice-title">
                        <label></label>                 
                        <span>通告概况</span>
                    </h5>
                    <div class="notice-content">
                        <ul class="notice-content-ul">
                            <li>
                                <span>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演:</span>
                                <span id="groupDirector"></span>
                            </li>
                            <li>
                                <span>今日戏量:</span>
                                <span id="statistics"></span>
                            </li>
                            <li>
                                <span>拍摄地点:</span>
                                <span class="shoot-location" id=""></span>
                            </li>
                            <li>
                                <span>早餐时间:</span>
                                <span id="breakfastTime"></span>
                            </li>
                            <li>
                                <span>出发时间:</span>
                                <span id="departureTime"></span>
                            </li>
                            <li>
                                <span>联&nbsp;系&nbsp;&nbsp;人:</span>
                                <span id="contactList"></span>
                            </li>
                        </ul>
                    </div>
                </div>
            
                <!--化妆信息-->
                <div class="notice-info-list">
                    <h5 class="notice-title">
                        <label></label>                 
                        <span>化妆信息</span>
                    </h5>
                    <div class="notice-content dress-up-content">
                        <table class="dress-up-info" cellspacing=0 cellpadding=0 id="noticeRoleTimeList">
                            <thead>
                                <tr>
                                    <td>角色名</td>
                                    <td>演员</td>
                                    <td>化妆时间</td>
                                    <td>地点</td>
                                    <td>出发时间</td>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- <tr>
                                    <td>沈西林</td>
                                    <td>黄渤</td>
                                    <td>06:00</td>
                                    <td>化妆间</td>
                                    <td>06:30</td>
                                </tr>
                                <tr>
                                    <td>武田弘一</td>
                                    <td>尹涛</td>
                                    <td>06:00</td>
                                    <td>化妆间</td>
                                    <td>06:30</td>
                                </tr>
                                <tr>
                                    <td>王建中</td>
                                    <td>李柱</td>
                                    <td>06:00</td>
                                    <td>化妆间</td>
                                    <td>06:30</td>
                                </tr> -->
                            </tbody>
                        </table>
                    </div>
                </div>
            
            <!--提示信息-->
               <div class="notice-info-list">
              <h5 class="notice-title">
                    <label></label>                 
                    <span>提示</span>
                </h5>
                <div class="notice-content tips-info">
                  <ul class="notice-content-ul">
                      <li>
                          <span>提示消息:</span>
                            <span id="note"></span>
                        </li>
                        <li>
                          <span>群特消息:</span>
                            <span id="roleInfo"></span>
                        </li>
                        <li>
                          <span>其他提示:</span>
                            <span class="otherTips" id="otherTips"></span>
                        </li>
                        <li>
                          <span>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:</span>
                            <span id="remark"></span>
                        </li>
                        <li>
                          <span>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植:</span>
                            <span id="insideAdvert"></span>
                        </li>
                    </ul>
                </div>
            </div>
            
            
        </div>
 <!--场次信息-->
    <div class="scence-info" id="scenceInfoContent">
          <!-- <div class="scence-info-list">
              <h6 class="scence-info-title" onclick="showScenceInfoContent(this)">
                  <i>1-88</i>茶楼二包间&nbsp;&nbsp;夜<i class="sign"></i>
                </h6>
                <ul class="scence-info-content">
                  <li>
                      <span>场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景:</span>
                        <span class="scence-name">茶楼二楼,茶楼三楼</span>
                    </li>
                    <li>
                      <span>气氛内外:</span>
                        <span class="atmo-sphere-ame">夜</span>
                    </li>
                    <li>
                      <span>页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;量:</span>
                        <span class="page-count">1.08</span>
                    </li>
                    <li>
                      <span>主要演员:</span>
                        <span class="main-actor">老谭</span>
                    </li>
                    <li>
                      <span>特&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;约:</span>
                        <span class="special-actor">老谭</span>
                    </li>
                    <li>
                      <span>群&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;众:</span>
                        <span class="public-actor">老谭</span>
                    </li>
                    <li>
                      <span>服&nbsp;化&nbsp;&nbsp;道:</span>
                        <span class="">//</span>
                    </li>
                    <li>
                      <span>主要内容:</span>
                        <span class="main-content"></span>
                    </li>
                    <li>
                      <span>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:</span>
                        <span class="scence-remark"></span>
                    </li>
                    <li>
                      <span>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植:</span>
                        <span class="insert-adverts">老谭</span>
                    </li>
                </ul>
            </div>
            
            <div class="scence-info-list">
              <h6 class="scence-info-title" onclick="showScenceInfoContent(this)">
                  <i>1-88</i>茶楼二包间&nbsp;&nbsp;夜<i class="sign"></i>
                </h6>
                <ul class="scence-info-content">
                  <li>
                      <span>场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景:</span>
                        <span class="scence-name">茶楼二楼,茶楼三楼</span>
                    </li>
                    <li>
                      <span>气氛内外:</span>
                        <span class="atmo-sphere-ame">夜</span>
                    </li>
                    <li>
                      <span>页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;量:</span>
                        <span class="page-count">1.08</span>
                    </li>
                    <li>
                      <span>主要演员:</span>
                        <span class="main-actor">老谭</span>
                    </li>
                    <li>
                      <span>特&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;约:</span>
                        <span class="special-actor">老谭</span>
                    </li>
                    <li>
                      <span>群&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;众:</span>
                        <span class="public-actor">老谭</span>
                    </li>
                    <li>
                      <span>服&nbsp;化&nbsp;&nbsp;道:</span>
                        <span class="">//</span>
                    </li>
                    <li>
                      <span>主要内容:</span>
                        <span class="main-content"></span>
                    </li>
                    <li>
                      <span>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:</span>
                        <span class="scence-remark"></span>
                    </li>
                    <li>
                      <span>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植:</span>
                        <span class="insert-adverts">老谭</span>
                    </li>
                </ul>
            </div> -->
            
        </div>
        
        <!--通告全文-->
        <div class="notice-all-content" id="noticeAllContent">
        <!-- 小图片 -->
            <!--<div class="notice-pic-list" id="noticePicList">
                
            </div>-->
         <!-- 大图片 --> 
            <!--<div class="notice-picbig-list" id="noticePicBigList" onclick="hideBigPicSwiper(this)">
                <div class="swiper-container swiper-container-horizontal" id="swiper-container1" style="height:300px;">
          <div class="swiper-wrapper">
            <div class="swiper-slide blue-slide" onclick="alert('你点了Swiper')" style="width: 800px;">slider1</div>
            <div class="swiper-slide red-slide" style="width: 800px;">slider2</div>
            <div class="swiper-slide orange-slide" style="width: 800px;">slider3</div>
          </div>
        </div>
            </div>-->
     
            <div class="container">
        <div class="right_con">               
                    <div class="my-gallery">
                      <!--如果需要动态 大图地址在<a href="url"></a>,data-size 大图放大的大小,小图在<img src="url />"--> 
                        <!-- <figure>
                            <a href="images/s5.jpg" data-size="800x1142">
                                <img src="images/s5_m.jpg" />
                            </a>
                        </figure>
                        <figure>
                            <a href="images/s5.jpg" data-size="800x1142">
                                <img src="images/s5_m.jpg" />
                            </a>
                        </figure>
                        <figure>
                            <a href="images/s5.jpg" data-size="800x1142">
                                <img src="images/s5_m.jpg" />
                            </a>
                        </figure>
                        <figure>
                            <a href="images/s5.jpg" data-size="800x1142">
                                <img src="images/s5_m.jpg" />
                            </a>
                        </figure>           -->      
                    </div>             
          </div> 
          </div>
    <!--以下内容不要管-->
    <div class="pswp" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="pswp__bg"></div>
      <div class="pswp__scroll-wrap">
        <div class="pswp__container">
          <div class="pswp__item"></div>
          <div class="pswp__item"></div>
          <div class="pswp__item"></div>
        </div>
        <div class="pswp__ui pswp__ui--hidden">
          <div class="pswp__top-bar">
            <div class="pswp__counter"></div>
            <button class="pswp__button pswp__button--close" title="Close (Esc)"></button>
            <div class="pswp__preloader">
              <div class="pswp__preloader__icn">
                <div class="pswp__preloader__cut">
                    <div class="pswp__preloader__donut"></div>
                            </div>
                        </div>
                    </div>
                 </div>
                 <div class="pswp__share-modal pswp__share-modal--hidden pswp__single-tap">
                  <div class="pswp__share-tooltip"></div> 
                 </div>
                 <!--<button class="pswp__button pswp__button--arrow--left" title="Previous (arrow left)"></button>
                 <button class="pswp__button pswp__button--arrow--right" title="Next (arrow right)"></button>-->
                 <div class="pswp__caption">
                  <div class="pswp__caption__center"></div>
           </div>
        </div>
      </div>
    </div>
         
        </div>
        
       </div> 
       
       <!-- 底部下载 -->
       <footer>
          <div class="bottom-download">
             <button class="close-download" onclick="closeDownUpload()"></button>
             <div class="logo-pic-con">
                <img src="images/new_logo.png">
             </div>
             <div class="text-content">
                <p>剧易拍</p>
                <p>高效生产, 轻松制片</p>
             </div>
             <div class="btn-list">
                <input type="button" value="下载" onclick="downloadApp()">
             </div>
          </div>
       </footer>
       
       
        
    </div>
    <script type="text/javascript" src="<%=basePath%>/js/notice/pictureSlide.js"></script>
</body>
</html>

