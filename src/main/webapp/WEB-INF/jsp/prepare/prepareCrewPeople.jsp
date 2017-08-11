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
<link rel="stylesheet" type="text/css" href="<%=basePath %>/css/prepare/prepareCrewPeople.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareCrewPeople.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
<title></title>
</head>
<body>
    <div class="crewer-container">
        <div class="crewer-grid-div">
            <div class="crewer-grid-head">
                <dl>
                    <dt></dt>
                    <dd>
                        <ul class="crewer-head-ul">
                            <li style="width: 17%; min-width:17%; max-width: 17%; text-align: left; box-sizing:border-box; padding-left:10px">
                                 <span>组别</span>
                                 <input class="add-role-btn" type="button" onclick="addFirstNode()">
                            </li>
                            <li style="width: 17%; min-width:17%; max-width: 17%; text-align: left;">职务</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">姓名</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">电话</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">审核人</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">确认时间</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">到岗时间</li>
                            <li style="width: 11%; min-width:11%; max-width: 11%;">薪酬</li>
                        </ul>
                    </dd>
                </dl>
            </div>
            <div class="crewer-grid-content" id="crewerGridContent">
                
            </div>
        </div>
    </div>
</body>
</html>