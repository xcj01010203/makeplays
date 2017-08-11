<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<style type="text/css">
		.my-container {
      width: 100%;
      height: 100%;
      margin: 0px auto;
    }
    html {
      font-size: 125%;/*20px*/
    }
    p.download-title {
      width: 100%;
      text-align: center;
      font-size: 0.9rem;
      margin-top: 1rem;
      font-family: "微软雅黑";
      color: #000;
      letter-spacing: 0.05rem;
    }
    .img-logo {
      width: 100%;
      height: 10%;
      display:flex;
            justify-content:center;
            align-items: center;
      margin-top: 0.5rem;
    }
    .img-logo img {
      width: 2.5rem;
      height: 2.5rem;
    }
    .img-container {
      display: flex;
      width: 100%;
      /*margin-top: 0.5rem;*/
    }
    .img-container img {
      flex: 1;
      width: 100%;
      height:100%;
    }
    .down-load-btn {
      width: 100%;
      margin-top: 1rem;
    }
    .apple-down-btn,.android-down-btn {
      width:60%;
      height: 5%;
      line-height: 2rem;
      margin:0 auto;
      text-align: center;
      border-radius: 0.3rem;
      padding: 0.2rem 0;
      margin-top: 1rem;
    }
    .apple-down-btn img {
      width: 1.5rem;
      height: 1.6rem;
      vertical-align: middle;
    }
    .android-down-btn {
      background: #98c13d;
    }
    .apple-down-btn{
      background: #54a0e4;
    }
    .android-down-btn img {
      width: 1.5rem;
      height: 1.6rem;
      vertical-align: middle;
      
      
    }
    .android-down-btn span, .apple-down-btn span {
      display: inline-block;
      height: 1.6rem;
      line-height:1.6rem;
      font-size: 1rem;
      font-family: "微软雅黑";
      color: #fff;
    }
    .remark {
	    font-size: 0.5rem;
	    text-align: center;
	    color: grey;
	    margin-top: 1rem;
	    letter-spacing: 0.05rem;
    }
		
	</style>
    <script type="text/javascript">
        window.onload=function(){
        	var width = document.body.clientWidth;
            window.onresize = function(){
        	document.documentElement.style.fontSize = document.body.clientWidth/width*100+"%";
            };
        };
        
        function downloadIOSApp() {
            window.location.href="https://itunes.apple.com/cn/app/jue-pai/id1059918032?mt=8";
        }
        function downloadAndroidApp() {
            window.location.href= "/fileManager/downloadFileByAddr?address=/data1/tomcat-data/makeplays/apk/jep_2.0.0.apk";
        }
        
    </script>
    
</head>
<body>
	<div class="my-container">
    
    <div class="img-logo">
      <img src="/images/downLoadImage/icon_makeplay.png">
    </div>
    <div class="img-container">
      <img src="/images/downLoadImage/phone.png">
    </div>
      <p class="download-title">开启轻松便捷有效的制片</p>
    <div class='down-load-btn'>
      <div class="apple-down-btn">
        <img src="/images/downLoadImage/apple.png" alt="" />
        <span onclick="downloadIOSApp()">苹果版下载</span>
      </div>
      <div class="android-down-btn">
        <img src="/images/downLoadImage/android.png" alt="" />
        <span onclick="downloadAndroidApp()">安卓版下载</span>
      </div>
      
    </div>
    <div class="remark">如点击按钮无反应，请用浏览器打开该页面</div>
  </div>
</body>
</html>