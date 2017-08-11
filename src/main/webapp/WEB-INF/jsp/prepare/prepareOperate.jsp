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
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/prepare/prepareOperate.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareOperate.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
<title></title>
</head>
<body>
    <div class="operate-container">
        <div class="operate-grid-div">
            <div class="operate-grid-head">
                <dl>
                    <dt></dt>
                    <dd>
                        <ul class="operate-head-ul">
                            <li style="width: 17%; min-width:17%; max-width: 17%; text-align: left; box-sizing:border-box; padding-left: 15px;">
                                 <span>合作种类</span>
                                 <input class="add-role-btn" type="button" onclick="addFirstNode()">
                            </li>
                            <li style="width: 17%; min-width:17%; max-width: 17%; text-align: left;">品牌</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">方式</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">费用</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">联系人</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">电话</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">备注</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">负责人</li>
                        </ul>
                    </dd>
                </dl>
            </div>
            <div class="operate-grid-content" id="operateGridContent">
                
            </div>
        </div>
    </div>
</body>
</html>