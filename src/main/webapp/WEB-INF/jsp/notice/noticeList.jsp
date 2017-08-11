<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.xiaotu.makeplays.utils.Constants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>

<!-- bootstrap CSS -->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/notice/noticelist.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/exportLoading.css" type="text/css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/HashMap.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/noticeList.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/noticeListWindow.js"></script>
<!-- bootstrap JS -->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript">
var isNoticeReadonly=<%=isNoticeReadonly%>;
</script>
</head>
<body>
	
<input type="hidden" id="showWindow" value="${window }">
<div class='body-div' id="bodyDiv">

	<!-- 选项按钮组 -->
	 <div class="btn_tab_wrap-worker" id="tabWrapWorker">
         <!-- tab键空白处 -->
           <div class="tab_key_container">
              <div class="btn_wrap"></div>
           <!-- tab键 -->
		           <div class="tab_wrap">
		               <ul>
		                   <li id="noticeTableTab" class="tab_li_current left-li" onclick="showTableWindow()">表单视图</li>
		                   <li id="noticeListTab" onclick="showListWindow()">列表视图</li>
		               </ul>
		           </div>
           </div>
           
       </div>
                
<!-- 列表视图 -->
<div class="main-window-div  hidden-window" id="noticeListWindow">

	<!-- <div id="jqxgrid" class='grid-data-div hidden-window'>
	
	</div> -->
	<!-- 通告单列表页面 -->
	<div class='custom-notice-list '>
		<input class="add-list-notice-button" type='button' value="新建" onclick="addNewNotice('list')">	
		<input type='button' class='export-car' id='exportNoticeListBtn' onclick='exportNoticeList()' title='导出列表'>
				
		<div class='btn-group' style='margin-left: 1015px; margin-top: -7px;'>
          	<button type='button' class='btn btn-primary fen-view-button' id='addViewColor' onclick='addColor()'>色彩模式</button>
          	<button type='button' class='btn btn-default shun-view-button' id='dropViewColor' onclick='dropColor()'>黑白模式</button>
        </div>
		<!-- <input class="color-list-button" type='button' value="颜色切换" onclick="changeColor()"> -->
		
		<!-- 搜索框 -->
		<div class='searchTextDiv'>
			<input class='search-content-input form-control' type="text" id='search' placeholder='请输入查询的集场号' onkeyup="searchJspValue(event)" onfocus="stopLoadData()"><input type="text" style='display: none;'>
			<div class='icon_cha1' onclick="confirmSearchView()">
				<img src="../images/find.png">
			</div>
			<span class='search-font-span' onclick="showSearchWindow()">高级查询</span>
		</div>
		<div class='search-result-div hidden-window' id="searchResultDiv">
			<ul id="searchResultUl">
			
			</ul>
		</div>
		<!-- 标题头table，防止滚动式标题头消失 -->
		<table class='custom-title-table' cellpadding="0" cellspacing="0">
			<tr style="background-color: #E5E5E5;">
				<td><p style="width: 170px;"></p></td>
				<td><p style="text-align: center;width: 60px;">拍摄状态</p></td>
				<td><p style='text-align: center;width: 59px;'>集场号</p></td>
				<td><p style='text-align: center;width: 75px;'>气氛/内外</p></td>
				<td><p style='text-align: center;width: 60px;'>页数</p></td>
				<td><p style='text-align: center;width: 160px;'>拍摄地点</p></td>
				<td><p style='text-align: center; width: 190px;'>场景</p></td>
				<td><p style='text-align: center;width:171px;'>主要内容</p></td>
				<td><p style='text-align: center;width: 189px;'>主要演员</p></td>
				<td><p style='text-align: center;width: 150px;'>特约群众演员</p></td>
				<td><p style='text-align: center;width:160px;'>服化道</p></td>
				<td><p style='text-align: center;width:140px;'>带号</p></td>
				<td><p style='text-align: center;width:191px;'>备注</p></td>
			</tr>
		</table>
		
		<!-- 内容table -->
		<div style="overflow: auto;height: calc(100% - 80px);">
			<table class="custom-content-table" cellpadding="0" cellspacing="0" id="noticeListTable">
			
			</table>
		</div>
	</div>
	
</div>

<!-- 表单视图 -->
<div class="main-window-div" id="noticeTableWindow">
	<input type="hidden" id='useCurrPageNo' value=''>
	<!-- 未销场窗口 -->
	<div class="top-div" id="useViewWindow" style="border-bottom: 2px solid #a7a7a7;width: 100%;">
		<div class='use-view-span'>
		  <div class="border-left"></div>
		  <label class="label-tips">未销场</label><span class='summary-data-span' id='summaryDataSpan'></span>&nbsp;&nbsp;&nbsp;&nbsp;
			<input class="add-notice-button" type='button' value="新建" onclick="addNewNotice('table')">		
		</div>
		<!-- 上一页通告单 -->
		<div class='pre-used-notice click-page-div' id='preUsedNotice' onclick="preUseNoticePage()">
			<img alt="" src="../images/notice/left.png">
		</div>
		<div class='used-notice-main-div'>
			<div class='used-notice-container' id='usedMainDiv'></div>
		</div>
		<!-- 下一页通告单 -->
		<div class='next-used-notice click-page-div' id='nextUsedNotice' onclick="nextUseNoticePage()">
			<img alt="" src="../images/notice/right.png">
		</div>
		
		
	</div>
	
	<!-- 已销场窗口 -->
	<div id="cancleViewWindow" class='bottom-div'>
		<input type="hidden" id='lastNoticeMonth' value=''>
		<input type="hidden" id="cancledPageNo" value=''>
		<div class='use-view-span'>
		  <div class="border-left"></div>
		  <label class="label-tips">已销场</label><span class='summary-data-span' id='summaryCancledSpan'></span>
		  
		  <!-- 月份 -->
		  <div class="cancled-month-div">
		  		<input type="hidden" id="noticeMonthInput" value=''>
			  	<!-- 上一页通告单 -->
				<div class='pre-notice-month notice-page-div pre-month-useable' id='preCancledMonth' onclick="preNoticeMonth()">
					<!-- <img alt="" src="../images/notice/month-left.png"> -->
				</div>
				<!-- 下一页通告单 -->
				<div class='next-notice-month notice-page-div next-month-useable' id='nextCancledMonth' onclick="nextNoticeMonth()">
					<!-- <img alt="" src="../images/notice/month-right.png"> -->
				</div>
				
				<div id='cancledMonthDiv' class='cancle-notice-month-mian-div'>
					
				</div>
		  </div>
		</div>
		
		<!-- 上一页通告单 -->
		<div class='pre-used-notice click-page-div' id='preCancledNotice' onclick="preCancledNoticePage()">
			<img alt="" src="../images/notice/left.png">
		</div>
		<!-- 下一页通告单 -->
		<div class='next-used-notice click-page-div' id='nextCancledNotice' onclick="nextCancledNoticePage()">
			<img alt="" src="../images/notice/right.png">
		</div>
		
		<!-- 通告单列表数据主题div -->
		<div class='cancle-notice-main-div'>
			<div id='cancleNoticeMainDiv' class='cancle-notice-container'>
			</div>
		
		</div>
		
	</div>

</div>


<!-- 日历视图 -->
<div class="main-window-div  hidden-window" id="noticeCalendarWindow">
	<div id="calendar">
		
	</div>
</div>


</div>
	<div id="noticeFedbackWindow" class="fedbackList" style="display:none;">
        <div>回复列表</div>
        <div>
            <div id="fedbackGridDiv"></div>
            <div class="btnlistDiv"><input type="button" id="fedbackWinClose" value="关闭"></div>
        </div>
    </div>
    
    
    <!-- 新建通告单窗口 -->
     <div id="noticeFormWindow" style="display:none">
        <div>
            <form id="noticeDivForm">
                <input id="noticeIdInput" name="noticeId" type="hidden">
                <table>
                    <tr>
                        <td class="nameLabel">名称：</td>
                        <td><input type="text" style="width:300px;height:30px;" name="noticeName" id="noticeNameInput" /></td>
                    </tr>
                    <tr>
                        <td class="nameLabel">时间：</td>
                        <td><input type="text" id="noticeDateDiv" name="noticeDateStr" onFocus="WdatePicker({isShowClear:false, readOnly:true, onpicked:autoGetNoticeName})"></td>
                    </tr>
                    <tr>
                        <td class="nameLabel">分组：</td>
                        <td><div id="noticeGroupDiv"></div><input type="hidden" name="groupId" id="groupIdValue"></td>
                    </tr>
                    <tr>
                        <td colspan="2"><div id="addNoticeError" style="text-align:left;padding-left:36px;width:370px;color:red; line-height:17px;"></div></td>
                    </tr>
                </table>
                <div style="text-align:center; margin-top: 23px;">
                     <input type="button" value="确定" id="saveNoticeButton" />
                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                     <input type="button" value="关闭" id="noticeCancelButton" />
                </div>
            </form>
        </div>
    </div>

<!-- 高级搜索框 -->
<div class="hidden-window" id='searchWindow'>
	<div>搜索场景 </div>
	<div class='second-div'>
		<span class='search-shoot-sapn'>拍摄地点:</span>
		 <select class="selectpicker" id="shootLocationSelect"  multiple data-live-search="true"></select>
		<br>
		<!-- <span class='search-cloth-sapn'>服&nbsp;化&nbsp; 道:</span>
		<input type="text" class='search-input' id='clothPropInput'/><input type="text" style="display: none;">
		<br> -->
		<span class='search-cloth-sapn'>带&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 号:</span>
		<input type="text" class='search-input' id='viewTapeNum'/><input type="text" style="display: none;">
		<br>
		<span class='search-sapn'>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 注:</span>
		<input type="text" class='search-input' id='remarkInfoInput'/><input type="text" style="display: none;">
		<br>
		<input type="button" class='search-notice-button' id='SearchNoticeButton' value="搜索" onclick="confirmSearchView()">
		<input type="button" class='search-close-button' id='SearchCloseButton' value="关闭" onclick="closeSearchWindow()">
		<input type="button" class='search-close-button' id='SearchEmptyButton' value="清空" onclick="clearSearchContent()">
	</div>
</div>


<!-- 正在加载 -->
<div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
<div id="loadingDataDiv" class="show-loading-container" style="display: none;">
	<div class="show-loading-div"> 正在加载数据，请稍候... </div>
</div>

</body>
</html>