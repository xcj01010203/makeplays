<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.xiaotu.makeplays.utils.Constants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isViewInfoReadonly = false;
Object hasImportViewInfoAuth = false;
Object hasExportViewInfoAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.VIEW_INFO)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.VIEW_INFO) == 1){
        	isViewInfoReadonly = true;
        }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_VIEWINFO) != null){
    	hasImportViewInfoAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_VIEWINFO) != null){
    	hasExportViewInfoAuth = true;
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/viewList.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/uploadScenarioGuide.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />

<!-- bootstrap CSS -->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">

<script type="text/javascript" src="<%=request.getContextPath()%>/js/view/viewlist.js"></script>
<!-- bootstrap JS -->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/view/selectPanel.js"></script>
<script>
<% 
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String currDate = sdf.format(new Date());
%>
</script>
<script type="text/javascript">
var currDate = <%=currDate%>;
var basePath = "<%=basePath %>";

var isViewInfoReadonly = <%=isViewInfoReadonly%>;
var hasImportViewInfoAuth = <%=hasImportViewInfoAuth%>;
var hasExportViewInfoAuth = <%=hasExportViewInfoAuth%>;
    
    //为了解决初次进入页面时
   /*  $("#right_main").show();
    
    var message = "${message }";
    if (message != null && message != '' && message != undefined) {
        showErrorMessage(message);
    } else {
        var source = "${source }";
        if (source == "importView") {
            $.ajax({
                url: '/scenarioManager/hasSkipOrReplaceData',
                type: 'post',
                data: {},
                async: false,
                success: function(param) {
                    if (param.success) {
                        if (param.hasSkipOrReplaceData) {
                            //parent.window.location.href = "/scenarioManager/toUploadResultPage";
                            showResultWindow();
                        }
                    } else {
                        showErrorMessage(param.message);
                    }
                },
                failure: function() {
                    
                }
            });        
        }
    } */

/* function loadGrid(dataAdapter){
	
	gridColumns=[
      { text: '集-场', cellsrenderer: viewColumn, width: 55 ,pinned: true},
      { text: '季节', cellsrenderer: seasonColumn, width: 40 },
      { text: '气氛',cellsrenderer: atmosphere, width: 40 },
      { text: '内外景',cellsrenderer:siteColumn,width: 50 },
      { text: '文武戏', cellsrenderer: typeColumn , width: 60 },
      { text: '拍摄地点', cellsrenderer: shootLocationColumn, width: 120 },
      { text: '主场景',cellsrenderer:majorViewColumn, width: 120 },
      { text: '次场景',cellsrenderer:minorViewColumn , width: 120 },
      { text: '三级场景',cellsrenderer: thirdLevelViewColumn, width: 120 },
      { text: '主要内容',cellsrenderer: mainContentColumn, width: 120 },
      { text: '页数',cellsrenderer: pageCountColumn, width: 40}
      ];
} */
/* function getIndex(row){
	var datainformation = $("#jqxgrid").jqxGrid('getdatainformation');
	var paginginformation = datainformation.paginginformation;
	var pagenum = paginginformation.pagenum;
	var pagesize = paginginformation.pagesize;
	var pagescount = paginginformation.pagescount;
	
	if ((row + 1) >= (pagenum*pagesize)) {
       row=row-pagenum*pagesize;
    }
	return row;
} */

</script>
</head>
<body>
		<div id="viewSpliter" onclick="hiddenPopup()">
			<!-- 表格加载div -->
			<div class="load-view-div">
			    <div class="title back_1" id="rendertoolbar"> 
			    </div>
			    <!-- 表格 -->
				<div id="jqxgrid" class="t_i">
				</div>
                <div class="statistics-data-div" id="statistics"></div>
			</div>
			<!-- 表格加载div结束 -->
			<!-- 场景修改开始 -->
			<div>
				<div class="hidden-tag" id="right_main">
	            	<div class="con_right_title">
	                <span id="right_title_span">场景信息</span>
	                <div id="operateBtn">
	                    <input type="button" id="btnsure" value="确&nbsp;定" onclick="saveView()"> &nbsp;&nbsp;&nbsp;
                        <input type="button" id="btndelete" value="删&nbsp;除" onclick="confirmDelView()">&nbsp;&nbsp;&nbsp;
	                    <input type="button" id="btncancle"  value="关&nbsp;闭" onclick="cancleUpdateView()">
	                </div>
	            	</div>
	            	<div>
			            <div id="con_right_bottom">
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
		<div class="hidden-tag" id="customWindow">
            <div>
	            <div class="over-flow-hidden" id="noticeDivForm">
	                <div>新建通告单</div>
	                <div>
		            	<form id="noticeForm" action="/notice/noticeSave">
		            	     <table>
		            	         <tr>
		            	             <td class="nameLabel">名称：</td>
                                     <td><input class="notice-name-input" type="text" name="noticeName" id="noticeNameInput" ></td>
		            	         </tr>
                                 <tr>
                                     <td class="nameLabel">时间：</td>
                                     <td><input class="notice-time-input" type="text" id="noticeTime" onFocus="WdatePicker({isShowClear:false,readOnly:true, onpicked:autoGetNoticeName})"></td>
                                 </tr>
                                 <tr>
                                     <td class="nameLabel">分组：</td>
                                     <td><div id="group" onchange="changeGroupInfo(event)"></div></td>
                                 </tr>
                                 <tr>
			                         <td colspan="2"><div class="add-notice-error-div" id="addNoticeError"></div></td>
			                     </tr>
		            	     </table>
                             <div class="save-notice-div">
                                  <input class="save-notice-button" type="button" value="确定" id="saveNoticeButton" onclick="saveNoticeInfo()">
                                  <input type="button" value="关闭" class="noticeCancelButton">
                             </div>
		           		 </form>
	           		 </div>
	            </div>
	            <div class="notice-list-div" id="noticeDivList">
	               <div>选择已有通告单</div>
	               <div>
		               <div id="existNoticeGrid"></div>
	                   <div class="exist-notice-div">
	                       <input type="button" value="确定" style='margin-top: 10px;' id="addToNoticeButton" onclick="confirmAddToNotice()"> 
	                       <input type="button" value="关闭" class="noticeCancelButton">
	                   </div>
                   </div>
	            </div>
            </div>
        </div>
	<div class="hidden-tag" id="planWindow">
		<div class="over-flow-hidden">
			<div class="plan-form-div">
				<div>新建计划</div>
				<div>
					<form id="planForm" action="/shootPlanManager/saveShootPlan">
						<input type="hidden" id="viewIdsInput" name="viewIds">
						<table class="register-table">
							<tr>
								<td class="nameLabel">计划名称：</td>
								<td>
									<input class="text-input plan-name-input" type="text" name="planName" id="planNameInput">
								</td>
							</tr>
							<tr>
								<td class="nameLabel">计划开始时间：</td>
								<td>
									<input class="plan-start-time" type="text" id='planStartTime' name="planStartTime" value="" onFocus="WdatePicker({isShowClear:false,readOnly:true})">
								</td>
							</tr>
							<tr>
								<td class="nameLabel">计划结束时间：</td>
								<td>
									<input class="plan-end-time" type="text" id='planEndTime' name="planEndTime" value="" onFocus="WdatePicker({isShowClear:false,readOnly:true})">
								</td>
							</tr>
							<tr>
								<td class="nameLabel">分组：</td>
								<td>
									<div id="planGroup" /></div> 
									<input type="hidden" name="groupId" id="groupIdValue">
								</td>
							</tr>
						</table>
					</form>
					<div class="plan-button-div">
						<input type="button" value="确定" id="savePlanButton"> 
						<input type="button" value="关闭" class="planCancelButton">
					</div>
				</div>
			</div>
			<div class="exist-plan-div" id="planDivList">
				<div>选择已有计划</div>
				<div>
					<div id="existPlanGrid"></div>
					<div class="exist-plan-button">
						<input type="button" value="确定" id="addToPlanButton"> 
						<input type="button" value="关闭" class="planCancelButton">
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="searchWindow" class="hidden-tag">
		<div id="customWindowHeader">
	         <span class="capture-search-span" id="captureContainer">高级查询 </span>
	    </div>
		<div id="dropDownDIV" class='Popups_box container'>
	      <div class="classify">基本信息：</div>
	         <ul class="searchUl">
	          <li id="moviceSectionNo">
	           <label>场次区间：</label>
	           <input class="hidden-tag" type='text' name='startSeriesNo' id='startSeriesNo'>
	           <input class="view-sction-input" type='text' name='startViewNo' id='startViewNo'>到
	           <input class="hidden-tag" type='text' name='endSeriesNo' id='endSeriesNo'>
	           <input class="view-sction-input" type='text' name='endViewNo' id='endViewNo'>
	          </li>
	          
            <li id="tvbSctionNo">
             <label>集场区间：</label>
             <input class="series-view-section" type='text' name='startSeriesNo' id='startSeriesNo'>
             -
             <input class="series-view-section" type='text' name='startViewNo' id='startViewNo'>
                              到
             <input class="series-view-section" type='text' name='endSeriesNo' id='endSeriesNo'>
             -
             <input class="series-view-section" type='text' name='endViewNo' id='endViewNo'>
            </li>
             
	         </ul>
	            
	            <ul class='searchUl'>
	                <li id="tvbSeriesNoLi">
	                   <label>集场编号：</label>
	                   <input type="text" id="seriesViewNos">
	                </li>
	                
	                <li id="moviceViewNos">
                       <label>场次编号：</label>
                       <input type="text" id="viewNos">
                  </li>
	            </ul>
	         
			        <ul class='searchUl'>
	                <li>
	                   <label>特殊提醒：</label>
	                   <select id="specialRemindSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	                 </li>
	            </ul>
	
	          <ul class='searchUl'>
						    <li>
						        <label>气&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;氛：</label>
						        <select id="atmosphereSelect" class="selectpicker show-tick" multiple>
						     		<option value="blank">[空]</option>
						        </select>
						 		<input type="hidden" class="preValue">
						    </li>
						</ul>
	
	         <ul class='searchUl'>
		          <li>
		             <label>内&nbsp;外&nbsp;&nbsp;景：</label>
		                <select id="siteSelect" class="selectpicker show-tick" multiple>
		                       <option value="blank">[空]</option>
		                </select>
		                   <input type="hidden" class="preValue">
		          </li>
	         </ul>
	         
	         <ul class='searchUl'>
	            <li>
	                   <label>拍摄状态：</label>
	                   <select id="shootStatusSelect" class="selectpicker show-tick" multiple>
	                   
	                   </select>
	                   <input type="hidden" class="preValue">
	                </li>
	         </ul>
	            
	            <ul class='searchUl'>
	                <li>
	                   <label>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植：</label>
	                   <select id="advertInfoSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                  <label>主要内容：</label>
	                  <input type="text" id="mainContent">
	               </li>
	           </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                  <label>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</label>
	                   <input type="text" id="viewRemark" autocomplete="off" />
	                   <input style="display:none"><!-- for disable autocomplete on chrome -->
	               </li>
	           </ul>
	           
	           <div class="classify" style="width: 235px;">场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景：</div>
	           <ul class='searchUl'>
	               <li>
	                   <label>拍摄地点：</label>
	                   <select id="shootLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	           
	           <ul class='searchUl'></ul>
	           
	           <ul class='searchUl'>
	               <li>
	                   <label>主&nbsp;&nbsp;场&nbsp;景：</label>
	                   <select id="firstLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                   <label>次&nbsp;&nbsp;场&nbsp;景：</label>
	                   <select id="secondLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	           
	            <ul class='searchUl'>
	               <li>
	                   <label>三级场景：</label>
	                   <select id="thirdLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                       <option value="blank">[空]</option>
	                   </select>
	                   <input type="hidden" class="preValue">
	                   <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	        
	        
	           <div class="classify" style="width: 10px;">演&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;员：</div>
	        <ul class='searchUl'>
		         <li>
		            <label>主要演员：</label>
		               <select id="majorRoleSelect" class="selectpicker show-tick" multiple data-live-search="true" onchange="changeMajorRole(event)">
		                      <option value="blank">[空]</option>
		               </select>
		                  <input type="hidden" class="preValue">
		                  <a class="clearSelection clear-a-selection">清空</a>
		         </li>
	        </ul>
	        
	        <ul class='searchUl'>
		         <li id="searchModeLI">
				         	<dd class="anyone-appear-dd" id="anyOneAppear">
				         	<input name="searchMode" value="1" type="radio" checked="checked">
				         	出现即可
				         	</dd>
		         	
			            <dd class="other-appear-dd" id="noOneAppear" >
			            <input name="searchMode" value="3" type="radio">
			                                不出现
			            </dd>
		            
				         	<dd class="other-appear-dd" id="everyOneAppear">
				         	<input name="searchMode" value="0" type="radio">
				         	同时出现
				         	</dd>
		         	
				         	<dd class="other-appear-dd" id="notEvenyOneAppear">
				         	<input name="searchMode"  value="2" type="radio">
				         	不同时出现
				         	</dd>
		         	
		         </li>
	        </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                  <label>特约演员：</label>
	                  <select id="guestRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                      <option value="blank">[空]</option>
	                  </select>
	                  <input type="hidden" class="preValue">
	                  <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	        
	        <ul class='searchUl'>
		         <li>
		            <label>群众演员：</label>
		               <select id="massRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
		                      <option value="blank">[空]</option>
		               </select>
		                  <input type="hidden" class="preValue">
		                  <a class="clearSelection clear-a-selection">清空</a>
		         </li>
	        </ul>
	           
	           
	           <div class="classify">服&nbsp;&nbsp;化&nbsp;道：</div>
	           <ul class='searchUl'>
	               <li>
	                  <label>服&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;装：</label>
	                  <select id="clothSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                      <option value="blank">[空]</option>
	                  </select>
	                  <input type="hidden" class="preValue">
	                  <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                  <label>化&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;妆：</label>
	                  <select id="makeupSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                      <option value="blank">[空]</option>
	                  </select>
	                  <input type="hidden" class="preValue">
	                  <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	        
	        <ul class='searchUl'>
	         <li>
	            <label>道&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;具：</label>
	               <select id="propSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                      <option value="blank">[空]</option>
	               </select>
	                  <input type="hidden" class="preValue">
	                  <a class="clearSelection clear-a-selection">清空</a>
	         </li>
	        </ul>
	           
	           <ul class='searchUl'>
	               <li>
	                  <label>特殊道具：</label>
	                  <select id="specialPropSelect" class="selectpicker show-tick" multiple data-live-search="true">
	                      <option value="blank">[空]</option>
	                  </select>
	                  <input type="hidden" class="preValue">
	                  <a class="clearSelection clear-a-selection">清空</a>
	               </li>
	           </ul>
	        <!-- <ul class='searchUl'>
	         <li>
	          <label>排序方式：</label>
	          <input type='radio' name='sortFlag' id='sortFlag' value='1' checked/>正序&nbsp;
	          <input type='radio' name='sortFlag' id='sortFlag' value='2'>倒序
	         </li>
	        </ul> -->
	        <ul style="width:100%;text-align:center;padding:0px 0px;">
	         <li class="search-button-li">
	          <input type='button'  value='查询' id='searchSubmit' style="margin-top:10px;" onclick="confirmSearchViewInfo()">
	          &nbsp;&nbsp;&nbsp;&nbsp;
	          <input type='button'  value='关闭' id='closeSearchSubmit'>
	          &nbsp;&nbsp;&nbsp;&nbsp;
	          <input type='button'  value='清空' id='clearSearchButton' onclick="clearSaerchContent()">
	            </li>
	        </ul>
		</div>
	</div>
	
	<div class="hidden-tag" id="viewContentWindow">
         <div id="windowHeader">
             <span id="viewSpanTitle">
             </span>
         </div>
         <div id="viewContentDIV" onmouseup="isOpenRigthMenu(event)">
	         <div id="con_middle">
	             <div id="viewContent">
	                <div id="viewTitle">
		               <div class="no-getnames-div">
		                    <label class="no-getnames-title" id="title"></label>
	                        <!-- <div class="noget-info-div">
	                            <label class="noget-info-label" id="noGetRoleNames" style='display: block;'></label>
	                            <label class="noget-info-label" id="noGetProps" style='display: block;'></label>
	                        </div> -->
		                </div>
		            </div>
	                <span></span>
	             </div>
	             
	             <div id="view_btn">
	                 <div class="pre-next-scene"></div>
	                 <div id="btn_list">
	                     <input title="上一场" id="preScene" onclick="searchPreScene()" readonly>
	                     <input class="next-scene-button" title="下一场" id="nextScene" onclick="searchNextScene()" readonly>
	                 </div>
	             </div>
	        </div>
         </div>
    </div>
    
     <div class="hidden-tag" id='importViewWindow'>
        <div id="importHeader">
             <span>
                导入顺场表
             </span>
         </div>
         <div id="viewSampleImg">
         <center>
		     <form id="importViewFileForm" action="/importManager/importViewInfo" enctype="multipart/form-data" method="post">
		         <input class="scene_upload" type="file" name="uploadFile">
		         <input class="scene-import" type="button" id="importFileBtn" value="导入"> 
		         <input type="button" id="downloadTamplate" value="模板下载">
		         <input class="scene-cancle" type="button" id="cancelImportBtn" value="取消">
		     </form>
         </center>
	     </div>
    </div>
    <div class="hidden-tag" id='uploadScenarioWindow'>
        <div id="uploadScenarioHeader">
             <span id="">
                上传剧本
             </span>
         </div>
         <div id="uploadScenarioDiv">
             <iframe class="upload-scene-iframe" ></iframe>
         </div>
    </div>
    
    <div class="hidden-tag" id='setAddressWindow'>
        <div>
            <span>
                设置拍摄地
             </span>
         </div>
         <div class="set-shootaddress-div">
             <div class="address-input">
             <input type="text" id="addressInput"><input type="text" style="display: none;">
             </div>
             <div id="addressList">
             
             </div>
             <div class="set-address-button">
                 <input class="confirm-setaddress" type='button' id='setAddressButton' value='确定' onclick="saveViewShootAddress()">
                 &nbsp;&nbsp;&nbsp;&nbsp;    
                 <input type='button' id='setAddressClose' value='取消'> 
             </div>
         </div>
    </div>
    
    <div class="hidden-tag" id='uploadResultWindow'>
        <div>
             <span>结果处理(以下场次在顺场表中已被手动修改过，请选择“跳过/替换”操作)</span>
         </div>
         <div>
             <div class="upload-result">                
                <div id="viewNoResult" class="viewno-result">
                </div>
                <iframe id="viewCompare" class="view-compare" src=""></iframe>
             </div>
             <div class="mybtn-group">
                <input type="button" id="replaceAllBtn" class="mybtn" value="替换场景" onclick="confirmReplaceAll()">
                <!-- <input type="button" id="replaceSecBtn" class="mybtn" value="只替换剧本" onclick="confirmReplaceSec()"> -->
                <input type="button" id="skipBtn" class="mybtn" value="跳过" onclick="confirmSkip()">
                <input type="button" id="cancelDealResultBtn" class="mybtn" value="取消" onclick="confirmCancelDealResult()">
             </div>
         </div>
    </div>
    
    <!-- 分析操作菜单 -->
	<div id="addColorBox">
	    <ul>
            <li id="c_majorscene" onclick="analysisRightClick(this)">主场景</li>
			<li id="c_secscene" onclick="analysisRightClick(this)">次场景</li>
			<li id="c_thirdscene" onclick="analysisRightClick(this)">三级场景</li>
			<li id="c_content" onclick="analysisRightClick(this)">主要内容</li>
			<li id="c_majoractor" onclick="analysisRightClick(this)">主要演员</li>
			<li id="c_guestactor" onclick="analysisRightClick(this)">特约演员</li>
			<li id="c_messactor" onclick="analysisRightClick(this)">群众演员</li>
			<li id="c_clothes" onclick="analysisRightClick(this)">服装</li>
			<li id="c_makeup" onclick="analysisRightClick(this)">化妆</li>
			<li id="c_commonprop" onclick="analysisRightClick(this)">道具</li>
			<li id="c_specialprop" onclick="analysisRightClick(this)">特殊道具</li>
	    </ul>
	</div>
	
	<div id="step-container" class="tipwizard-container">
    <div class="tipwizard-bg">
        <div class="step step1" data-step="1">
            <button type="button" class="btn-close">close</button>
            <button type="button" class="btn-next">下一步</button>
            <div class="tipwizard-btn">
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
                <button type="button" class="btn-step">close</button>
            </div>
        </div>
    </div>
    
    <!--导入窗口  -->
    <div id="importViewWin" class="jqx-window">
          <div>导入</div>
          <div>
            <!--  <form class="import-form" method="post" action="/viewManager/importViewInfo" enctype="multipart/form-data">
		        <input type="file" name="file">
		        <input type="submit" name="submit" value='导入'>
		        <input type="button" value="模板下载" onclick="downloadImportTemplate()">
                <input type="button" value="取消" onclick="closeImportWin()">
             </form> -->
              <iframe id="importIframe" width="100%" height="100%"></iframe>
           </div>
    </div>
  </div>
  
  <!-- 批量修改场景窗口 -->
  <div id='autoSetViewWindown' class="jqx-window">
  	<div>合并主场景</div>
  	<div>
  		<div class="main-content">
  		    <div class="main-scene-div">
  		        <input class='search-content-input form-control' type="text" id='searchMainLocation' placeholder='过滤主场景' onkeyup="clicksearchMainLocation(event)"><input type="text" style='display: none;'>
				<div class='icon_cha1' onclick="searchSameMainLocation()">
					<img src="../images/find.png">
				</div>
  		    </div>
  		    
  		    <div class="main-scene-content">
  		        <!-- 表头 -->
  		        <div class="main-scene-table-header">
  		            <table class="more-scene-table-header" cellpadding="0" cellspacing="0">
  		            	<tr>
			                <td>主场景</td>
			                <td>次场景</td>
			                <td>三级场景</td>
  		                </tr>
  		            </table>
  		        </div>
  		        <!-- 表体 -->
  		        <div class="main-scene-table-body">
  		            <table class="more-scene-table-body" cellpadding="0" cellspacing="0" id='locationSceneTable'>
  		                
                     
  		            </table>
  		        </div>
  		    </div>
  		    
  		    
  		</div>
  		
  		<div class="win-btn-list">
  		    <input type="button" value="保存" onclick="saveBtchViewLocation()">
  		    <input type="button" value="还原" onclick="backToInitData()">
  		    <input type="button" value="取消" onclick="cancelMergeView()">
  		</div>
  		
  	</div>
  </div>
  
  <!-- 导出文本框 -->
  <div style="display: none;" id='exportScenarioWindow'>
		<div id="exportScenarioHeader">
			<span> 导出场景 </span>
		</div>
		<div id="exportScenarioDiv">
			<div class="exportTvOption">
				<input class="sxport-radio-input" type="checkbox" id="exportViewInfo" checked>导出场景
				<input class="export-content-checkbox" id="exportViewContent" type="checkbox">导出剧本
			</div>
			<div style="text-align: center;">
				<input class="mybtn export" type="button" id="exportBtn" value="导出" onclick="confirmExport()">
				<input class="mybtn" type="button" id="cancelExpBtn" value="取消">
			</div>
		</div>
	</div>
	
	<!-- 隐藏列面板 -->
	<div class="columns-panel" id="columnsPanel" onclick="objEvent(event)">
	   <ul id="columnsPanelItem">
	   </ul>
	</div>
	
	
	
	
</body>
</html>