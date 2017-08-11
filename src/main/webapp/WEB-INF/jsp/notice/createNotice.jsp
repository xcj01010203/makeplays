<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isNoticeReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO) == 1){
        	isNoticeReadonly = true;
        }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <base href="<%=basePath%>">
		<meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/notice/createNotice.css">
    <script type="text/javascript" src="<%=basePath%>/js/notice/createNotice.js"></script>
    <script type="text/javascript">
		var isNoticeReadonly=<%=isNoticeReadonly%>;
	</script>
</head>
<body>
	<input type="hidden" id="noticeId" value="${noticeId }">
	<input type="hidden" id="stepPage" value="${stepPage }">
	<input type="hidden" id="showWindow" value="${window }">
    <div class="my-create-container">
       <div class="notice-navigator">
           <!--  <div class="menu-glide"></div> -->
            <ul class="nav-ul">
                <li class="return-btn" id="returnBtn" onclick="goNoticePage(this)">返&nbsp;&nbsp;回</li>
                <li class="notice-name click" id="notieName" onclick="noticeName(this)"><i>1</i><i class="li-content">通告命名</i></li>
                <li class="scene-config" id="sceneConfig" onclick="showNoticeViwq(this)"><i>2</i><i class="li-content">场景配置</i></li>
                <li class="add-information" id="addInformation" onclick="showGenerateNotice(this)"><i>3</i><i class="li-content">附加信息</i></li>
            </ul>
        </div> 
        <div class="notice-create-content">
            <div class="create-notice-div" id="createNotice">
                    <ul>
                        <li>
                            <p>拍摄日期:</p>
                            <input type="text" id="noticeDateDiv" name="noticeDateStr" onFocus="WdatePicker({isShowClear:false, readOnly:true, onpicked:autoGetNoticeName})">
                        </li>
                        <li>
                            <p>拍摄组别:</p>
                            <select class="group-list-select" id="groupSelect"></select>
                        </li>
                        <li>
                            <p>通告名称:</p>
                            <input type="text"  name="noticeName" id="noticeNameInput" onkeyup="changeStatus()">
                            <input type="text" style="display:none;">
                        </li>
                    </ul>
                    <!-- <div class="btn-list">
                        <input type="button" value="配置场景" onclick="saveNewNotice()">
                    </div> -->
                </div> 
                
                <div class="btn-list">
                     <input type="button" value="配置场景>" onclick="saveNewNotice()">
                </div>
                
                
                
            
            <div class="load-notice-content" id="loadNoticeContent"></div>
            <iframe name="noticeContentIframe" style="display: none;" id="noticeContentIframe" width="100%" height="100%"></iframe>
            
            <div class="win-btn-list">
                <input type="button" value="上一步" onclick="goBack()">
                <input type="button" value="下一步" onclick="goNext()">
            </div> 
        </div> 
     </div> 
</body>
</html>