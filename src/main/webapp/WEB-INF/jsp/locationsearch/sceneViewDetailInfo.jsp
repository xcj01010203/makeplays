<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isSceneViewReadonly = false;//拍摄管理勘景是否是只读
Object isReadonly = false;     //筹备管理勘景是否只读
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.LOCATION_SEARCH)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.LOCATION_SEARCH) == 1){
        	isSceneViewReadonly = true;
        }
    }
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
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
   <%--  <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/jqwidgets/styles/jqx.base.css" type="text/css" /> --%>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/locationsearch/sceneViewDetailInfo.css">
    
     <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
    
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/webuploader/webuploader.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
    
    <%-- <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script> --%>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/locationsearch/sceneViewDetailInfo.js"></script>
     <script type="text/javascript" src="<%=basePath%>/js/locationsearch/loadLocationSearch.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=kkz9NgUHHA0yVwnYfgBbqpiB"></script>
    
     <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
       <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
	  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
	  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxlistbox.js"></script>
    
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
    
     <script type="text/javascript">
    	var isSceneViewReadonly = <%=isSceneViewReadonly%>;
    	var isReadonly = <%=isReadonly%>
    </script>
</head>
<body>
   
   <input type="hidden" id="sceneViewId" value="${sceneViewId}">
   <input type="hidden" id="vLongitude">
   <input type="hidden" id="vLatitude">
   
   <div class="container">
   
   <input type="hidden" id="where" value="${where}">
   <!-- 进度显示 -->
   <div class="ui dimmer body" id="myLoader">
        <div class="ui large text loader">正在上传附件，请稍后...</div>
   </div> 
   <div class="win-btn-list top-btn-list">
         <input class="close-btn" type="button" value="关闭" onclick="closeRightPopupWin()">
   </div>
   
     <div class="tab-body-wrap">
	   <!-- tab键容器 -->
		  <div class="btn_tab_wrap">
		      <!-- tab键空白处 -->
		      <div class="btn_wrap"></div>
		      <!-- tab键 -->
		      <div class="tab_wrap">
		          <ul>
		              <li id="tab_0" class="tab_li_current" onclick="showBaseInfo(this)">场景地点基本信息</li>
		              <li id="tab_1" onclick="showSteSceneView(this)">配置场景信息</li>
		          </ul>
		      </div>
		  </div>
	  </div>
   
   <div class="form-list-container" id ="baseInfoDiv">
       
       <h5>基本信息</h5>
       <ul class="basic-info">
          <li>
              <p>
                  <span><i class="need-flag">*</i>名&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称&nbsp;:</span>
                  <input type="text" placeholder="例如：横店影视城" id="vName">
              </p>
              
              <p>
                  <span>距住宿地&nbsp;:</span>
                  <input type="text" placeholder="例如：5公里" id="distanceToHotel">
              </p>
          </li>
          <li>
              <p>
                  <span>地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;域&nbsp;:</span>
                  <input type="text" id='cityName' disabled="disabled">
                  
              </p>
              <p>
                  <span>容纳人数&nbsp;:</span>
                  <input type="text" placeholder="例如：1000人" id="holePeoples" maxlength="8" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
              </p>
          </li>
          <li>
              <p class="map-p">
                  <span><i class="need-flag">*</i>详细地址&nbsp;:</span>
                  <input type="text" placeholder="例如：某大街XX号(精确设置→)" id='address'>
                  <span class="show-baidu-map" id="showMap" onclick="showMapWindow()"></span>
              </p>
              <p>
                  <span>设备空间&nbsp;:</span>
                  <input type="text" placeholder="例如：无限制" id="equipmentSpace">
              </p>
          </li>
         <!--  <li>
              <span style="float:left;">标&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;记&nbsp;:</span>
              地图
              <div style="position: relative; float: left; width: 60%; margin-left: 15px;">
              	<div id="r-result" style='position: absolute; top: 5px; left: 5px; z-index: 999; width: 50%;'>
              		<input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="您要去哪儿" style="width:100%;" />
              	</div>
			  	      <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
              	<div id="allmap" style="width:100%;height: 500px;"></div>
              </div>
          </li> -->
       </ul>
       
       <!-- 详细信息 -->
       <h5>详细信息</h5>
       <ul class="detail-info">
          <li>
              <p>
                  <span>联&nbsp;系&nbsp;&nbsp;人&nbsp;:</span>
                  <input type="text" placeholder="例如：张明" id="contactName">
              </p>
              <p>
                  <span>联系人职务&nbsp;:</span>
                  <input type="text" placeholder="例如：业主" id="contactRole">
              </p>
          </li>
          <li>
              <p>
                  <span>联系方式&nbsp;:</span>
                  <input type="text" placeholder="例如：1236547789" id="contactNo">
              </p>
              <p class="idle-p">
                  <span>空闲档期&nbsp;:</span>
                  <input type="text" placeholder="开始时间" id="freeStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">-
                  <input type="text" placeholder="结束时间" id="freeEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
              </p>
          </li>
          <li class="space-li"></li>
          <li>
              <p>
                  <span>改&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景&nbsp;:</span>
                  <select id="isModifyView" onchange="changeViewStatus(this);">
                      <option value=0>是</option>
                      <option value=1 checked>否</option>
                  </select>
              </p>
              <p>
                  <span>道具陈设&nbsp;:</span>
                  <select id="hasProp" onchange="changePropStatus(this);">
                      <option value=0>需要</option>
                      <option value=1 checked>不需要</option>
                  </select>
               </p>
          </li>
          <li>
              <p>
                  <span>改景时间&nbsp;:</span>
                  <input type="text" placeholder="例如：4个月" id="modifyViewTime">
              </p>
              <p>
                  <span>陈设时间&nbsp;:</span>
                  <input type="text" placeholder="例如：2天" id="propTime">
              </p>
          </li>
          <li>
              <p>
                  <span>改景费用&nbsp;:</span>
                  <input type="text" placeholder="例如：5000.00" id="modifyViewCost" maxlength="10">
                  <input type="hidden">
              </p>
              <p>
                  <span>陈设费用&nbsp;:</span>
                  <input type="text" placeholder="例如：4000.00" id="propCost" maxlength="10">
                  <input type="hidden">
              </p>
              
          </li>
          
          <li class="space-li"></li>
          
          
          <li>
              <!-- <p>
                  <span>进景时间&nbsp;:</span>
                  <input type="text" placeholder="例如：2016-11-20" id="enterViewDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
              </p>
              <p>
                  <span>离景时间&nbsp;:</span>
                  <input type="text" placeholder="例如：2017-11-30" id="leaveViewDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
              </p> -->
              <p class="idle-p">
                  <span>进景时段</span>
                  <input type="text" placeholder="进景时间" id="enterViewDate" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked: getDays})">-
                  <input type="text" placeholder="离景时间" id="leaveViewDate" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked: getDays})">
              </p>
               <p>
                  <span>使用时间&nbsp;:</span>
                  <input type="text" placeholder="10天" id="viewUseTime" readonly>
              </p>
          </li>
          
          <li>
             
              <p>
                  <span>价&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;格&nbsp;:</span>
                  <input type="text" placeholder="例如：5000.00" id="viewPrice" maxlength="10">
                  <input type="hidden">
              </p>
          </li>
          <!-- <li>
              
              <p>
                  <span>自&nbsp;&nbsp;定&nbsp;义&nbsp;:</span>
                  <input type="text" id="other">
              </p>
          </li> -->
          
          
          <li>
              <p style="float: none; width: 100%">
                  <span style="vertical-align: top; width: 6.8%;">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注&nbsp;:</span>
                  <textarea id="remark"></textarea>
              </p>
              
          </li>
          <li>
              <p>
                  <span>添加图片&nbsp;:</span>
                  <button class="upload-file-btn" type="button" id="uploadFileBtn">选择文件</button>
              </p>
          </li>
          
       </ul>
       
       <ul class='upload-file-show-pic' id="showSmallUploadFile"></ul>
   	</div>
   	
   	<!-- 按钮组 -->
    <div class="win-btn-list" id="btnListDiv">
         <input class="save-btn" type="button" value="保存" onclick="saveScenceInfo()">
         <input class="delete-btn" type="button" value="删除" onclick="delScenceInfo()">
   </div>
	  
       <!-- 配置场景弹窗 -->
       <div class="set-scence-win hidden-tag" id="setScenceWin">
           <!-- <div style="display: block;margin-left: 21px;">配置场景</div> -->
           <div class="configured-container">
               <!-- 已配置 -->
               <div class="already-configured-con" id="alreadyConfiguredCon">
                   <div class="already-configured" id="alreadyConfigured"></div>
               </div>
              <!--  <div class="line-div-a"></div> -->
               <!-- 未配置 -->
               <div class="none-configured-con" id="noneConfiguredCon">
                   <!-- <h5 id="relationMoreView" onclick="showNoneConfiguredScence(this)">关联更多主场景</h5> -->
                   <div class="none-configured" id="noneConfigured"></div>
               </div>
           </div>
       </div>
        
        <!-- 地图遮罩 -->
        <div class="map-modal-zindex" id="mapModalZIndex"></div>
   <!-- 地图弹窗 -->
        <div class="map-container-div" id="setDetailAddressWin">
                <!-- 地图 -->
              <div class="map-con-div">
                <div class="search-address-div">
                    
                    <p><span style="color: #f00;">*</span>&nbsp;请用搜索框搜索地址并拖动红色标志进行精确定位</p>
                    <input type="button" value="保存并关闭" onclick="saveLocation()">
                </div>
                <div id="r-result" style='position: absolute; top: 43px; left: 30px; z-index: 999; width: 30%;'>
                  <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="搜索地点" style="width:100%;" />
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%;height: calc(100% - 30px);"></div>
              </div>
        </div>
        
       
   </div>
</body>
</html>