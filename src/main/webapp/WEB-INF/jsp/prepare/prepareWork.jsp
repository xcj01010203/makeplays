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
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/prepare/prepareWork.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareWork.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
<title></title>
</head>
<body>
    <div class="worker-container">
        <div class="worker-grid-div">
            <div class="worker-grid-head">
                <dl>
                    <dt></dt>
                    <dd>
                        <ul class="worker-head-ul">
                            <li style="width: 25%; min-width:25%; max-width: 25%; text-align: left; box-sizing: border-box; padding-left: 15px;">
                                 <span>类型</span>
                                 <input class="add-role-btn" type="button" onclick="addFirstNode()">
                            </li>
                            <li style="width: 25%; min-width:25%; max-width: 25%; text-align: left;">工作</li>
                            <li style="width: 25%; min-width:25%; max-width: 25%;">进度</li>
                            <li style="width: 25%; min-width:25%; max-width: 25%;">负责人</li>
                        </ul>
                    </dd>
                </dl>
            </div>
            <div class="worker-grid-content" id="workerGridContent">
                
            </div>
        </div>
    </div>
</body>
</html>