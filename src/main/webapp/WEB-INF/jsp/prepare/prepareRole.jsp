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
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/style.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/prepare/prepareRole.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareRole.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
</head>
<body>
    <div class="role-container">
        <div class="role-grid-div">
          <div class="role-grid-head">
              <dl>
                  <dt></dt>
                  <dd>
                      <ul class="role-head-ul">
                          <li style="width: 15%; min-width: 15%; max-width: 15%; padding-left: 15px; text-align: left; box-sizing: border-box;">
                              <span>角色</span>
                              <input class="add-role-btn" type="button" onclick="addFirstNode()">
                          </li>
                          <li style="width: 15%; min-width: 15%; max-width: 15%;  text-align: left;">备选演员</li>
                          <li style="width: 15%; min-width: 15%; max-width: 15%;">沟通进度</li>
                          <li style="width: 30%; min-width: 30%; max-width: 30%;">沟通内容</li>
                          <li style="width: 25%; min-width: 25%; max-width: 25%;">备注</li>
                      </ul>
                  </dd>
              </dl>
          </div>
          <!-- 内容 -->
          <div class="role-grid-content" id="roleGridContent">
              
          </div>
          
        </div>
    </div>
</body>
</html>