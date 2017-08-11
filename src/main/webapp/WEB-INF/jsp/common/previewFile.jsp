<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/notice/slide.css">

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/js-viewer/viewer.css">

<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/slide.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>

 <script type="text/javascript" src="<%=request.getContextPath()%>/js/js-viewer/viewer.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
	   var attpackId = ${attpackId};   //附件包ID
	   var type = ${type}; //附件类型
	   
	   $.ajax({
	       url: "/attachmentManager/queryByAttpackIdAndType",
	       type: "post",
	       data: {attpackId: attpackId, type: type},
	       dataType: "json",
	       success: function(response) {
	           if (!response.success) {
	               window.close();
	           }
	           
	           var attachmentList = response.attachmentList;
	           
	           var fileArray = [];
	           for (var i = 0; i < attachmentList.length; i++) {
                    var attachment = attachmentList[i];
					var file = new Object();
					file.src = "/fileManager/previewAttachment?address=" + attachment.hdStorePath;
					file.title = attachment.name;
					fileArray.push(file);
	           }
	           
	           //如果是图片附件，则加载相框插件
	           if (type == 2) {
	        	       $("#pictureContainer").show();
	               /* var slide = initBigPicture(fileArray);
                   slide.open(0); */
                    var html = [];
                    for(var j = 0; j<fileArray.length; j++){
                    	  html.push('<li><img src="'+ fileArray[j].src+'"></li>');
                    }
                    $("#pictureList").append(html.join(""));
                    
                    var viewer = new Viewer(document.getElementById('pictureList'), {
                        url: 'data-original',
                        button: false
                    });
                    viewer.show(); 
	           } else {
                    var othserFileContainer = $('#otherFile');
                    
                    var fileIframe = $("<iframe width='100%' height='100%' id='fileIframe' style='position:absolute;z-index:4;' frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>");
					fileIframe.prependTo(othserFileContainer);
					$("#fileIframe").attr("src", fileArray[0].src);
					 
					var ch=document.documentElement.clientHeight;//屏幕的高度
					var cw=document.documentElement.clientWidth;//屏幕的宽度
					$("#fileIframe").css({width:cw, height:ch});
					 
					othserFileContainer.show();
	           }
	       }
	   });
	});

	/**
	 * 初始化相框插件
	 */
	function initBigPicture(imgArray){
		var slide = new Slide({
	        imgArray: imgArray,
	        duration: 0.5
	    });
		$('.btn-close').on('click',function(){
			window.close();
		});
		$('.btn-delete').hide();
		return slide;
	}
</script>
</head>
<body>
<div id='otherFile'></div>
<div id="pictureContainer" style="display: none;">
    <ul id="pictureList"></ul>
</div>
</body>

</html>