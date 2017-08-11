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
<title></title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/style.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/prepare/prepareExtension.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareExtension.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
</head>
<body>
    <div class="extension-container">
        <div class= "extension-grid-div">
            <div class="extension-grid-header">
                <table class="extension-head-table" id="extensionHeadTable" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td style="width: 25%; min-width: 25%; max-width: 25%;">
                            <span>类型</span>
                            <input class="add-row-btn" type="button" onclick="addRow()">
                        </td>
                        <td style="width: 35%; min-width: 35%; max-width: 35%;">素材列表</td>
                        <td style="width: 20%; min-width: 20%; max-width: 20%;">负责人</td>
                        <td style="width: 20%; min-width: 20%; max-width: 20%;">审核人</td>
                    </tr>
                </table>
            </div>
            <div class="extension-content-div" id="extensionConDiv"> 
            </div>
        </div>
    </div>
</body>
</html>