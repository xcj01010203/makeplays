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
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/prepare/prepareArteffect.css">
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/prepare/prepareArteffect.js"></script>
<script>
var isReadonly = <%=isReadonly %>;
</script>
<title></title> 
</head>
<body>
    <div class="affect-container">
      <div class="tab-body-wrap">
            <div class="btn_tab_wrap">
                <!-- tab键空白处 -->
                <div class="btn_wrap"></div>
                <!-- tab键 -->
                <div class="tab_wrap">
                    <ul>
                        <li id="contact_information" class="tab_li_current" onclick="showRoleGrid(this)">角色</li>
                        <li id="hotel_information" onclick="showViewGrid(this)">场景</li>
                    </ul>
                </div>
             </div>
             
             <!-- 角色表 -->
             <div class="public-arteffect role-grid-div">
                <div class="role-grid" id="roleGrid">
                    <div class="role-grid-header">
                        <table class="role-header-table" id="roleHeadTable" cellpadding="0" cellspacing="0" border="0">
                            <tr>
                                <td style="width: 16%; min-width: 16%; max-width: 16%;">
                                    <span>角色</span>
                                    <input class="add-row-btn" type="button" onclick="addRow()">
                                </td>
                                <td style="width: 16%; min-width: 16%; max-width: 16%;">造型</td>
                                <td style="width: 14%; min-width: 14%; max-width: 14%;">确定日期</td>
                                <td style="width: 14%; min-width: 14%; max-width: 14%;">状态</td>
                                <td style="width: 20%; min-width:20%; max-width: 20%;">备注</td>
                                <td style="width: 20%; min-width: 20%; max-width: 20%;">审核人</td>
                            </tr>
                        </table>
                    </div>
                    <div class="role-grid-content" id="roleGridContent">
                        
                    </div>
                </div>
             </div>
             <!-- 场景表 -->
             <div class="public-arteffect view-grid-div">
                <div class="view-grid" id="viewGrid">
                    <div class="view-grid-header">
                        <table class="view-header-table" id="roleHeadTable" cellpadding="0" cellspacing="0" border="0">
                            <tr>
                                <td style="width: 16%; min-width: 16%; max-width: 16%;">
                                    <span>场景</span>
                                    <input class="add-row-btn" type="button" onclick="addViewRow()">
                                </td>
                                <td style="width: 8%; min-width: 8%; max-width: 8%;">效果图</td>
                                <td style="width: 10%; min-width: 10%; max-width: 10%;">日期</td>
                                <td style="width: 10%; min-width: 10%; max-width: 10%;">施工图</td>
                                <td style="width: 10%; min-width: 10%; max-width: 10%;">日期</td>
                                <td style="width: 8%; min-width: 8%; max-width: 8%;">置景</td>
                                <td style="width: 10%; min-width: 10%; max-width: 10%;">日期</td>
                                <td style="width: 8%; min-width: 8%; max-width: 8%;">审核人</td>
                                <td style="width: 20%; min-width: 20%; max-width: 20%;">意见</td>
                            </tr>
                        </table>
                    </div>
                    <div class="view-grid-content" id="viewGridContent">
                    </div>
                </div>
             </div>
             
        </div>
      
    </div>
</body>
</html>