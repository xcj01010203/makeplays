<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isSceneViewReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.LOCATION_SEARCH)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.LOCATION_SEARCH) == 1){
        	isSceneViewReadonly = true;
        }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
		
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/locationsearch/locationSearch.css">
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
	  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
	  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxlistbox.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/locationsearch/loadLocationSearch.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/locationsearch/locationSearch.js"></script>
    <script type="text/javascript">
    	var isSceneViewReadonly = <%=isSceneViewReadonly%>;
    </script>
</head>
<body>
    <div class="my-container">
        
        <!-- 主列表 -->
        <div class="location-search-table" id="locationSearchTable">
           <!-- 工具条 -->
           <div id='toolbar' class='toolbar'>
              <div class='toolbar'>
                  <input type='button' class='add-scence-btn' id='addScenceBtn' onclick='addScenceBtn()' value='添加' title='添加'>
              </div>
           </div>
           <!-- 表格 -->
           <div class='t_i' id="scenceGrid">
              <!-- 表头 -->
              <div class='t_i_h' id='hh'>
                  <div class='ee'>
                      <table class='notice-scence-table' id='scenceTableTitle' cellspacing=0 cellpadding=0></table>
                  </div>
              </div>
              <!-- 表体 -->
              <div class='auto-height cc' id='ca'></div>
           </div>
        </div>
      <!-- 添加修改弹窗 -->
        <div class="right-popup-win" id="rightPopUpWin">
            <!-- <div class="win-btn-list">
                <input class="save-btn" type="button" value="保存">
                <input class="delete-btn" type="button" value="删除">
                <input class="close-btn" type="button" value="关闭" onclick="closeRightPopupWin()">
            </div> -->
            <div class="scence-content" id="scenceContentDiv">
                 <iframe id="scenceContentIframe" width="100%" height="100%"></iframe> 
                
            </div>
        </div>  
        
        
        <!-- 地图弹窗 -->
        <div class="jqx-window" id="setDetailAddressWin">
            <div>设置详细信息</div>
            <div>
                <!-- 地图 -->
              <div  style="width: 100%; height: 100%;">
                <div id="r-result" style='position: absolute; top: 5px; left: 5px; z-index: 999; width: 50%;'>
                  <!-- <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="您要去哪儿" style="width:100%;" /> -->
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%;height: 100%;"></div>
              </div>
            </div>
        </div>
        
        <!-- 加载表格 -->
           <div id="loadingTable" class="show-loading-container">
              <div class="show-loading-div"> Loading... </div>
           </div>
        
    </div>
</body>
</html>