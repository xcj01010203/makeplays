<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  String path = request.getContextPath();
  String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
  
  Object isNoticeReadonly = false;
  Object obj = session.getAttribute("userAuthMap");

  if(obj!=null){
      java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
      if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO)) {
          if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO) == 1){
          	  isNoticeReadonly = true;
          }
      }
  }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<!-- 下拉 -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/paging/paging.css">

<link rel="stylesheet"  href="<%=basePath%>/css/bootstrap/css/bootstrap.css" type="text/css">
<link rel="stylesheet"  href="<%=basePath%>/css/notice/noticeViewList.css" type="text/css">
<!-- sweetaltert -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />


 <script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/view/selectPanel.js"></script>


<script src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script> 
 <script type="text/javascript" src="<%=basePath%>/js/dist/makeplays.js"></script>
 <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
 <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxinput.js"></script>
 <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script>
 
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>

<!-- 下拉 -->
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/paging/paging.js"></script>

<script type="text/javascript"  src="<%=basePath%>/js/notice/loadNoticeViewList.js"></script>

<script type="text/javascript"  src="<%=basePath%>/js/notice/noticeViewList.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-paginator.js"></script>
<script type="text/javascript">
	var isNoticeReadonly=<%=isNoticeReadonly%>;
</script>
</head>
<body>
  <input type="hidden" value="${noticeId}" id="viewNoticeId">
  <input type="hidden" value="" id="totalView">
  <input type="hidden" value="" id="totalPage">
  <%-- <input type="hidden" value="${source}" id="source"> --%>
  <div class="my-container" >
  
    <!-- 通告单场景列表 -->
    <div id="noticeViewDiv" class="notice-view-list-div">
    
    <div class="notice-header" >
      <div class="left-header public-header">
        <div class="left-header-top">
          <div class="border-left"></div>
          <span class="header-title">通告场景</span>
        </div>

        <!-- <div class="notice-count">
          <button class="set-shoot-adress-button" onclick='setShootLocationAddress()'>设置拍摄地</button>
          <button class="set-shoot-adress-button" onclick='cancleView()'>销场</button>
        </div> -->
      </div>
      
      <div class="right-header public-header" id="rightHeaderTop">
        <div class="right-header-top" onclick="showBackupView()" >
          <div class="border-left"></div>
          <span class="header-title" title='添加场景至通告单'>备选场景</span>
        </div>

        <!-- <div class="notice-count" id="noticeCount">
          <ul>
            <li><a id="breakScenceBtn" href="javascript:void(0);">分场表</a>
            </li>
            <li>|</li>
            <li><a id="planOneBtn" href="javascript:void(0);">计划一</a></li>
            <li>|</li>
            <li><a id="planTwoBtn" href="javascript:void(0);">计划二</a></li>
          </ul>
        </div> -->

      </div>
    </div>

    <!--  -->

    <div class="container-body">
      <!-- 通告场景表 -->
      <div class="notice-scene-left" id="noticeSceneList">
          <!-- 数据列表 -->
          <div class="data-grid" id="grid"></div>
      </div>
      <!-- 分场表 -->
      <div class="break-secent-right" id="breakSecentRigth"  onclick="showBackupView()">
        <!-- <div id="breakSecentList"></div> -->
        <table class="break-secent-table-right" cellspacing=0 cellpadding=0>
          <tr id='noticeViewNoTr'>
            <!-- <th>集场</th> -->
          </tr>
          <tr style="line-height: 32px;">
              <td>
                  <span class="add-view">点击添加场景至通告单</span>
              </td>
          </tr>
        </table>
      </div>
      
    </div>
    
    <!-- 按钮 -->
    <div class="btn-list">
       <input type="button" value="<通告命名" onclick='setNoticeName()'>
      <input type="button" value="附加信息>" onclick='backToNoticeList()'>
    </div>
    
    <!-- 场景修改开始 -->
	<div class='break-view-detail-right' id="right_main">
		<div style="height: 100%;">
           	<div class="con_right_title">
               <span id="right_title_span" class='right-info-span'>场景信息</span>
               <div id="operateBtn" class='operate-button'>
                   <input type="button" class="sure-button" id='saveViewInfo' value="确&nbsp;定" onclick="saveView()"> &nbsp;&nbsp;&nbsp;
                   <input type="button" class="cancle-button"  value="关&nbsp;闭" onclick="cancleViewIframe()">
               </div>
           	</div>
           	<div style="height: 97%;">
	            <div id="con_right_bottom" style="height: 100%;">
	                <div  class="rigth-bottom-div">
	                    <iframe class="view-info-frame"  src="" name="f_scene_create" id="viewInfoFrame"></iframe>
		            </div>
	                <div class="bottom_button">
		            </div>
	            </div>
            </div>
       	</div>
	</div>
	<!-- 场景修改结束 -->
    
    </div>
    
    <!-- 备选场景列表 -->
    <div class="hidden-div" id="backupViewDiv">
    
    <div class="notice-header">
      <div class="left-header public-header">
        <div class="left-header-top" style='width: 11%;' onclick="showNoticeView()">
          <div class="border-left"></div>
          <span class="header-title">通告场景</span>
        </div>

        <div class="notice-count" id="viewPageCountDiv">
            <!--  共 <span>XX</span>场/<span>XX</span>页 -->
        </div>

      </div>
      <div class="option-right-header public-header">
        <div class="right-header-top">
          <div class="border-left"></div>
          <span class="header-title">备选场景</span>
        </div>

        <!-- <div class="notice-count-btn" id="noticeCount">
          <ul>
            <li><a id="breakScenceBtn" href="javascript:void(0);">分场表</a>
            </li>
            <li>|</li>
            <li><a id="planOneBtn" href="javascript:void(0);">计划一</a></li>
            <li>|</li>
            <li><a id="planTwoBtn" href="javascript:void(0);">计划二</a></li>
            <li>
            	<input class="add-to-notice-scence" type="button" id="addToNoticeScence" value="添加到通告场景" onclick="addViewToNotice()">
            	<div class='btn-group' style='margin-left: 30px;'>
	            	<button type='button' class='btn shun-view-button' id='smoothView' onclick='queryShunView()'>顺场</button>
	            	<button type='button' class='btn fen-view-button' id='groupView' onclick='queryFenView()'>分场</button>
            	</div>
            </li>
          </ul>
          添加到通告场景
          
        </div> -->


      </div>
    </div>

    <div class="container-body">
      <!-- 通告场景表 -->
      <div class="break-secent-left" onclick="showNoticeView()">
        <!-- <div id="noticeSceneList"></div> -->
        <div class="break-secent-table-header">
            <table class="break-secent-table" cellspacing=0 cellpadding=0>
		          <tr id="viewNoTr">
		            <!-- <th>集场</th> -->
		          </tr>
		        </table>
        </div>
        <div class="break-secent-table-body">
            <table class="break-secent-table-data" id="breakSecentTableData" cellspacing=0 cellpadding=0>
                
            </table>
        </div>
      </div>
      <!-- 分场表 -->
      <div class="notice-scene-right">
        <!-- <div id="breakSecentList"></div> -->
        <div class="notice-scence-div">
        
        <!-- 按钮 -->
        <div class='right-scene-button'>
          <ul>
            <li>
            	<div class='view-page-count' id='viewPageCount'></div>
            	<!-- <input type="text" class='view-page-count' id='viewPageCount' value='0场/0.00页' readonly="readonly"> -->
            	<input class="add-to-notice-scence" type="button" id="addToNoticeScence" value="添加到通告" onclick="addViewToNotice()">
            	<div class='btn-group' style='margin-left: 30px;'>
	            	<button type='button' class='btn btn-default shun-view-button' id='smoothView' onclick='queryShunView()'>顺场</button>
	            	<button type='button' class='btn fen-view-button' id='groupView' onclick='queryFenView()'>分场</button>
            	</div>
            </li>
          </ul>
        </div>
        
        
      <div class="table-main-con">
         <div class="table-main-header" id="tableMainHeader">
         
          <table class="notice-view-table" id="ViewListTable" cellspacing=0 cellpadding=0>
            <tr id="firstTr">
              <th class="select-all"><input class="input-select-all" type="checkbox" id="selectAll" value='' onchange="selectAll(this);"></th>
              <th class="collection-scence" id="secriesNoTh" onclick="selectCollScence(this, event)">
                <ul class="dropdown_box" id="collScenceDrop">
                  <li class="coll-first-li">
                  <!-- 集场下拉框 -->
                  </li>
                  <li class="coll-last-li">
                    <div class="select-btn-list">
                      <input type="button" value="确定" onclick="searchViewNo()">
                      <input type="button" value="清空" onclick="clearViewNo()">
                    </div>
                  </li>

                </ul>
              </th>
              <th class="air-atmo">气氛
                  <select class="selectpicker air-atmo-select" id="airAtmoSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <!-- <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'sans-serif';" class="clearSelection" onclick="clearAtmoSelection(this)">[清空]</a>  -->
              </th>
              <th class="air-atmo">内外
                  <select class="selectpicker site-select" id="siteSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <!-- <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSiteSelection(this)">[清空]</a>  -->
              </th>
              
               <th class="shootlocation-th">拍摄地点
                   <select class="selectpicker shoot-location-select" id="shootLocationSelect"  multiple data-live-search="true" style="display: none;"></select>
                   <a style="display:none; float: right; lline-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearShootLocationSelection(this)">[清空]</a> 
              </th>
              
              <th class="scence-major-th">场景
                  <select class="selectpicker first-scence-select" id="firstScenceSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearFirstSceneSelection(this)">[清空]</a> 
              </th>
              
              <th class="scence-th">页数
              
              </th>
             
              <th class="content-th" onclick="selectContent(this, event)">内容<div class='select-picture-div content-div'></div>
              
              	<ul class="content-dropdown-box" id="collContentDrop">
                  <li class="coll-second-li">
                  	<input type="text" id="mainContent" class='contnent-info-input' placeholder='请输入需要筛选的内容'><input type="text" style="display: none;" />
                  </li>
                  <li class="coll-last-li">
                    <div class='content-reamrk-list'>
                      <input type="button" class="search-button" value="确定" onclick="searchContent()">
                      <input type="button" class="search-button" value="清空" onclick="clearContent()">
                    </div>
                  </li>

                </ul>
                
              </th>
              <th class="main-actor">主要演员
                  <select class="selectpicker main-actor-select" id="mainActorSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 2px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearMajorSelection(this)">[清空]</a> 
              </th>
              <th class="special-actor">特约
                  <select class="selectpicker special-actor-select" id="specialActorSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 5px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSpecialSelection(this)">[清空]</a> 
              </th>
              <th class="public-actor">群演
                  <select class="selectpicker public-actor-select" id="publicActorSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 5px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearPublicSelection(this)">[清空]</a>
              </th>
               <th class="cloth-dress-prop">化妆
                  <select class="selectpicker dress-select" id="makeUpSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearMakeUpSelection(this)">[清空]</a>
              </th>
              <th class="cloth-dress-prop">服装
                  <select class="selectpicker cloth-select" id="clothSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearClosthSelection(this)">[清空]</a>
              </th>
               <th class="cloth-dress-prop">道具
                  <select class="selectpicker prop-select" id="propSelect"  multiple data-live-search="true" style="display: none;"></select>
                  <a style="display:none; float: right; line-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearPropSelection(this)">[清空]</a> 
              </th>
              <th class="remark-th" onclick="selectRemark(this, event)">备注<div class='select-picture-div reamrk-div'></div>
              
              <ul class="content-dropdown-box" id="collRemarkDrop">
                  <li class="coll-third-li">
                  	<input type="text" id="ReamrkContent" class='contnent-info-input' placeholder='请输入需要筛选的内容'><input type="text" style="display: none;" />
                  </li>
                  <li class="coll-last-li">
                    <div class='content-reamrk-list'>
                      <input type="button" class="search-button" value="确定" onclick="searchRemark()">
                      <input type="button" class="search-button" value="清空" onclick="clearRemark()">
                    </div>
                  </li>

                </ul>
              
              </th>

            </tr>
           <!--  <tr id="firstTr">
              <td colspan="10" align="left"><input class="input-select-all" type="checkbox" id="selectAll" onchange="selectAll(this);"></td>
            </tr> -->
            
            <!-- <tr>
              <td><input type="checkbox" id="selectAll"
                onchange="selectAll(this);"></td>
              <td>
                集场td <span class="select-coll-scence">全部</span>

              </td>
              <td>
                气氛td <span class="select-air-atmo"
                id="selectedAtmosphere">全部</span>


              </td>
              <td><span class="select-scence" id="selectScence">全部</span>

              </td>
              <td>---</td>
              <td><span class="select-main-actor" id="selectMainActor">全部</span>

              </td>
              <td><span class="select-special-actor"
                id="selectSpecialActor">全部</span></td>
              <td><span class="select-public-actor" id="selectPublicActor">全部</span>

              </td>
              <td><span class="select-cloth-dress-prop"
                id="selectClothProp">全部</span></td>
              <td>---</td>
            </tr> -->
            
            <!-- <tr>
              <td><input type="checkbox"></td>
              <td>11-11</td>
              <td>日/内</td>
              <td>古长城</td>
              <td class="content-column"><div class="jqx-column">
                  1111111111111111111111111111111</div></td>
              <td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
              <td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
              <td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
            </tr>
            <tr>
              <td><input type="checkbox"></td>
              <td>11-11</td>
              <td>日/内</td>
              <td>古长城</td>
              <td class="content-column"><div class="jqx-column">
                  1111111111111111111111111111111</div></td>
              <td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
              <td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
              <td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
            </tr>
            <tr>
              <td><input type="checkbox"></td>
              <td>11-11</td>
              <td>日/内</td>
              <td>古长城</td>
              <td class="content-column"><div class="jqx-column">
                  1111111111111111111111111111111</div></td>
              <td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
              <td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
              <td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
            </tr>
            <tr>
              <td><input type="checkbox"></td>
              <td>11-11</td>
              <td>日/内</td>
              <td>古长城</td>
              <td class="content-column"><div class="jqx-column">
                  1111111111111111111111111111111</div></td>
              <td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
              <td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
              <td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
              <td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
            </tr> -->
          </table>
          
          
         </div>
         
         <div class="table-main-body-div" id="tableMainBodyDiv" onscroll="changeScroll()">
            <div>
                <table id="tableMainBody" cellspacing=0 cellpadding=0>
                
                </table>
            </div>
            
         </div>
        </div> 
         
         
          
        </div>


       

      </div>
    </div>
     <!-- 表格分页 -->
   <div class="table-page-div">
      <div class="span9">
         <div id="tablePage" style="float: right;margin-right: 60px;"></div>
      </div>
    </div>
    <div class="btn-list">
      <input type="button" value="<通告命名" onclick='setNoticeName()'>
      <input type="button" value="附加信息>" onclick='backToNoticeList()'>
    </div>
  </div>
  </div>
  
  <div id='setAddressWindow' style='display:none;'>
        <div id="windowHeader">
             <span id=""> 设置拍摄地</span>
         </div>
         <div style="overflow:auto; line-height:22px;letter-spacing:1px; font-size:12px;" id="viewContentDIV">
             <div style="height:60px">
                <input type="text" id="addressInput" placeholder="拍摄地"><input type="text" style="display: none;" />
                <!-- <input class="set-shoot-regin" type="button" id="setShootRegin" value="设置地域" title="设置拍摄地域" onclick="showShootReginInfo()"> -->
                <span style="margin-left: 23px;">地域:</span>
                <select id="setShootReginInfo" class="selectpicker shoot-region" data-live-search="true" multiple style="display: none;"></select>
                <a style="display:none; float: right; lline-height: 18px; margin-right: 0px; cursor:pointer; font-size:12px; font-family:'微软雅黑';" class="clearSelection" onclick="clearShootRegionSelection(this)">[清空]</a> 
                <input type="hidden" class="preValue">
                <!-- <p id="shootReginInfo"></p> -->
             </div>
             <div class="shoot-list" id="addressList">
             
             </div>
             <!-- <div class="shoot-list" id="shootReginList" style="display: none;">
                
             </div> -->
             <div style="text-align:center;">
               <input type='button' style='margin-top: 10px;' id='setAddressButton' value='确定' onclick="saveLocationRegion()"/>
               &nbsp;&nbsp;&nbsp;&nbsp;    
               <input type='button' style='margin-top: 10px;' id='setAddressClose' value='取消'/> 
             </div>
         </div>
    </div>
    
<!-- 场景状态设置窗口 -->
<div id="setStatusWindow" style="display:none;">
  <div id="customWindowHeader">
         <span id="captureContainer" style="float: left">销场</span>
    </div>
    <div id="customWindowContent" style="overflow: hidden">
            <table>
              <tr>
                  <td class="nameLabel">拍摄状态：</td>
                  <td>
                  <!-- <div style="display: block;" id="shootStatus"></div> -->
                      <select class="shoot-status-select" id="shootStatus">
                        <option value="">请选择</option>
                        <option value="0">甩戏</option>
                        <option value="2">完成</option>
                        <option value="1">部分完成</option>
                        <option value="3">删戏</option>
                        <option value="4">加戏部分完成</option>
                        <option value="5">加戏已完成</option>
                      </select>
                  </td>
              </tr>
                <tr id="finishDateDl" style="display:none;">
                    <td class="nameLabel" id='finishDateTd'></td>
                    <td><input type="text" style="display: block;" id="finishDate" readonly="readonly"></td>
                </tr>
                <tr>
                    <td class="nameLabel">带&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</td>
                    <td><input type="text" id="tapNo" style="width:300px;  box-sizing: border-box; height:30px;background: none !important; border: 1px solid rgba(204,204,204,1);"><input style="display: none;" /></td>
                </tr>
                <tr>
                    <td class="nameLabel">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</td>
                    <td>
                        <textarea rows="5" cols="10" id="remark" style="width:300px; height:80px;"></textarea>
                    </td>
                </tr>
            </table>
            <div style="text-align:center; margin-top: 46px;">
                 <input type="button" id="saveButton" value="保存">
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <input type="button" id="closeWindow" value="关闭"/>
            </div>
   </div>
</div>

<!-- 正在加载 -->
<div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
<div id="loadingDataDiv" class="show-loading-container" style="display: none;">
	<div class="show-loading-div"> 正在加载数据，请稍候... </div>
</div>

<!--统一提示  -->
<div id="eventWindow" style="display: none;">
      <div>
                提示
      </div>
      <div>
         <div style="margin-top: 25px;font-size: 16px;margin-left: 10px;" id="eventWindowContent">
                       是否确定此操作？
         </div>
         <div>
           <div style=" margin: 30px 0px 0px 60px;">
              <input type="button" id="ok" value="确定" style="margin-right: 10px;" />
              <input type="button" id="mainCloseBtn" value="取消" />
           </div>
         </div>
       </div>
</div>
</body>
</html>