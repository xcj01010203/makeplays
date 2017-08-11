<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isRunningAccountReadonly = false;
Object hasExportLoanAuth = false; 
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    //账务详情可编辑权限
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.RUNNGING_ACCOUNT)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.RUNNGING_ACCOUNT) == 1){
        	isRunningAccountReadonly = true;
        }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_LOAN_DETAIL) != null){
    	hasExportLoanAuth = true;
    }
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
		<link rel="stylesheet" type="text/css" href="styles.css">
		-->
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/loanList.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/exportLoading.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
    <script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/loanList.js"></script>
    
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
    
    <script>
    	var isRunningAccountReadonly = <%=isRunningAccountReadonly%>;
        var hasExportLoanAuth = <%=hasExportLoanAuth%>;
    </script>
  </head>
  
  <body>
      <div class="my-container">
          <!-- jqx表格 -->
          <div class="loan-information-list-div" id="loanInformationListDiv">
              <div id="loanInformationList"></div>
          </div>
          <!-- 借款金额汇总表格 -->
          <div class="curr-information-sum" id="currInformationSum">
          </div>
          
          <!-- 单个借款单信息弹窗 -->
          <div class="jqx-window" id="loanDetailWin">
              <div>修改单据</div>
              <div>
                  <!-- <div class="full-receipt-div" id="fullReceiptDiv">
                      
                  </div> -->
                  <iframe class="loan-receipt-div" id="loanReceiptDiv" scrolling="auto" width="100%" height="49%"></iframe>
                  
                  <!-- 盛放上传附件 -->
                    <div class="payment-upload-wrap">
                        <div class="payment-upload-title">
                            <div class="upload-file-btn" id="uploadFileBtn">添加附件</div> 
                        </div>
                        <div class="show-upload-file">
                            <ul id="showAttachmentFile"></ul>
                        </div>
                   </div>
                  
                  
                  <div class="btn-list">
		                  <input type="button" id="saveModifyAfterLoan" value="保存" onclick="saveModifyAfterLoan()">
		                  <input type="button" id="deleteModifyAfterLoan" value="删除" onclick="deleteModifyAfterLoan()">
		                  <input type="button" value="取消" onclick="cancelModifyAfterLoan()">
                  </div>
                  
              </div>
              
          </div>
          
          <!-- 付款详情弹窗 -->
          <div class="jqx-window" id="paymentDetailWin">
              <div>付款详情</div>
              <div>
                  <div id="paymentDetailGrid"></div>
              </div>
          </div>
          
          
          <!-- 导出 -->
            <div id="loadingDiv" class="show-loading-container">
                <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
            </div>
          
          
      </div>
  </body>
</html>
