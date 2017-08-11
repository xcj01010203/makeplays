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
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/prepare/prepareScript.css">
<%-- <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxinput.js"></script> --%>
<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/prepare/prepareScript.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/common/common.js"></script>
<script> 
var isReadonly = <%=isReadonly %>;
</script>
<title></title>
</head>
<body>
   <div class="script-container">
        <div class="script-type-div">
           <span class="script-type">剧本类型:</span>
           <div class="script-type-list">
              <ul class="reivew-people-ul" id="reivewPeopleUl">
                  <li><label><input type="checkbox" name="scriptType" value>剧情梗概</label></li>
                  <li><label><input type="checkbox" name="scriptType" value>分场剧本</label></li>
                  <li><label><input type="checkbox" name="scriptType" value>台词剧本</label></li>
                  <li><label><input type="checkbox" name="scriptType" value>分集/导演台本</label></li>
              </ul>
           </div>
           
        </div>
        
        <div class="review-weight-div">
           <span class="review-weight">评审权重:</span>
           <div class="review-people-div">
               <ul class="reivew-people-list" id="reivewPeopleList">
                   <li class="reivew-people"><input type="text" value="张三">:<input type="text" value="40">%<a class="close-tag" onclick="deleteReview(this)"></a></li>
                   <li class="reivew-people"><input type="text" value="李思思">:<input type="text" value="30">%+<a class="close-tag" onclick="deleteReview(this)"></a></li>
                   <li><input class="add-review-btn" type="button" onclick="addReview(this)"></li>
               </ul>
           </div>
           <div class="btn-list-div">
               <input type="button" value="生成进度表" onclick="buildProgressGrid()">
               <span style="color: gray; font-size: 13px;">*设置剧本类型和评审权重之后，请点击生成进度表按钮，重新生成进度表</span>
           </div>
        </div>
        
        <!--分隔线-->
        <div class="border-line"></div>
        
        <!-- 进度树表 -->
       <!--  <div class="script-progress-grid" id="scriptProgressGrid"></div> -->
        <div class="tree-container-div">
            <div class="tree-grid-container" id="treeGrid">
                <div class="tree-grid-header" id="treeGridHeader"></div>
                <div class="tree-grid-content" id="treeGridContent" onscroll="tableScroll()"></div>
            </div>
        </div>
        
     </div>
</body>
</html>