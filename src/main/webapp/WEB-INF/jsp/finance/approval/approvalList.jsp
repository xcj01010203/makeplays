<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object isApprovalReadonly = false; //是否只读
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_APPROVAL)) {
	        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_APPROVAL) == 1){
	        	isApprovalReadonly = true;
	        }
	    }
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title></title>
	<%--   <link rel="stylesheet" href="<%=path%>/js/jqwidgets/styles/jqx.base.css" type="text/css" /> --%>
  	<link rel="stylesheet" type="text/css" href="<%=path%>/css/finance/approvalList.css">
  	
  	<link rel="stylesheet" type="text/css" href="<%=path %>/js/BeAlert-master/BeAlert.css">
    <script type="text/javascript" src="<%=path %>/js/jqwidgets/jqxbuttons.js"></script>
    
    <!-- 弹窗 -->
    <script type="text/javascript" src="<%=path%>/js/BeAlert-master/BeAlert.js"></script>
    
    
    
  	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
  	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
  	<script type="text/javascript" src="<%=path%>/js/finance/approvalList.js"></script>
  	<script type="text/javascript">
        //是否只读
        var isApprovalReadonly = <%=isApprovalReadonly%>;
    </script>
</head>
<body>
	 <div class="my-container">
	     <input type="hidden" id="listTypeFlag" value=3><!-- 默认为待我审批 -->
	     <div class="tab-body-wrap">
	         <div class="btn_tab_wrap">
                 <!-- tab键空白处 -->
                 <div class="btn_wrap"></div>
                 
                 <!-- tab键 -->
                 <div class="tab_wrap">
                     <ul>
                         <li id="waitApproval_li" class="tab_li_current" onclick="waitingApproval(this)">待我审批</li>
                         <li id="alreadyApproval_li" onclick="alreadyApproval(this)">我已审批</li>
                         <li id="myApproval_li" onclick="myApproval(this)">我的申请</li>
                     </ul>
                 </div>
                    
                    
            </div>
	     </div>
	     
	     <!-- 内容 -->
	     <div class="main-container" id="mainContainer">
	         <!-- 主列表 （待我审批）-->
	         <div id="approvalList"></div>
	     </div>
	     <!-- 我的审请列表 -->
	     <div class="hide-container" id="hideContainer">
	         <div id="myApprovalList"></div>
	     </div>
	     
	     <!-- 高级搜索窗口 -->
	     <div class="jqx-window" id="advanceSearch">
	         <div>高级查询</div>
	         <div class="jqx-content">
	             <ul class="search-condition">
	                 <li>
	                     <p>单据类型&nbsp;:&nbsp;</p>
	                     <label><input type="radio" name="receiptType" value=1>借款</label>
	                     <label><input type="radio" name="receiptType" value=2>报销</label>
	                     <label><input type="radio" name="receiptType" value=3>预算</label>
	                 </li>
	                 <li>
	                     <p>单据编号&nbsp;:&nbsp;</p>
	                     <input type="text" id="receiptNoInput">
	                 </li>
	                 <li>
	                     <p>申&nbsp;请&nbsp;&nbsp;人&nbsp;:&nbsp;</p>
	                     <input type="text" id="applyerName">
	                 </li>
	                 <li>
	                     <p>申请金额&nbsp;:&nbsp;</p>
	                     <input class="money-section" type="text" id="minMoney" onkeyup="checkNum(this)">-<input class="money-section" type="text" id="maxMoney" onkeyup="checkNum(this)">
	                 </li>
	                 <li>
	                     <p>申请日期&nbsp;:&nbsp;</p>
	                     <input class="date-section" type="text" id="minDate" onclick="WdatePicker({isShowClear:true,readOnly:true})">-<input class="date-section" type="text" id="maxDate" onclick="WdatePicker({isShowClear:true,readOnly:true})">
	                 </li>
	                 <li>
	                     <p>说&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;明&nbsp;:&nbsp;</p>
	                     <textarea id="description"></textarea>
	                 </li>
	             </ul>
	             <div class="win-btn-list">
	                 <input type="button" value="确定" onclick="confirmSearch()">
	                 <input type="button" value="清空" onclick="clearQueryContent()">
	                 <input type="button" value="取消" onclick="cancelSearch()">
	             </div>
	         </div>
	     </div>
	     
	     <!-- 审请详细信息 -->
	     <div class="right-popup-win" id="rightPopupWin">
	         <iframe id="approvalIframe" width="100%" height="100%"></iframe>
	     </div>
	     
	 </div>
</body>
</html>