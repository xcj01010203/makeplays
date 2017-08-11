<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isReadonly = false;     //角色表是否只读

Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PREPARE)) {
      if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PREPARE) == 1){
          isReadonly = true;
      }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/style.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/prepare/prepareSceneView.css">
	<script type="text/javascript" src="<%=basePath %>/js/prepare/prepareSceneView.js"></script>
	<script>
	var isReadonly = <%=isReadonly %>;
	</script>
	<title>勘景情况</title>
</head>

<body>
	<div class="my-container">
	  <div class="jqx-grid-div">
	     <div id="jqxgrid" class="secenViewWrap"></div>
	  </div>
		
	</div>
	<div class="right-popup-win" id="rightPopUpWin">
		<div class="scence-content" id="scenceContentDiv">
			<iframe id="scenceContentIframe" width="100%" height="100%"></iframe>
		</div>
	</div>
</body>

</html>