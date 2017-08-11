<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isRunningAccountReadonly = false; 
Object modifySettledPayment = false; 
Object hasImportFinanceDetailAuth = false;
Object hasExportFinanceDetailAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.RUNNGING_ACCOUNT) == 1){
        isRunningAccountReadonly = true;
    }    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.MODIFY_SETTLED_PAYMENT)){
        modifySettledPayment = true;
    }    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_FINANCE_DETAIL)){
    	hasImportFinanceDetailAuth = true;
    }    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_FINANCE_DETAIL)){
    	hasExportFinanceDetailAuth = true;
    }
}

%>

<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
    
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/financeRunningAccount.css">
    
    <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <%--  <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/loadFinanceRunningAccount.js"></script> --%>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
    <script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
   
    <script type="text/javascript" src="<%=basePath%>/js/interactiveFrame.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/financeRunningAccount.js"></script>
    
    
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
    
    <script>
        var isRunningAccountReadonly = <%=isRunningAccountReadonly%>;
        var basePath = "<%=basePath%>";
        var modifySettledPayment = <%=modifySettledPayment%>;
        var hasImportFinanceDetailAuth = <%=hasImportFinanceDetailAuth%>;
        var hasExportFinanceDetailAuth = <%=hasExportFinanceDetailAuth%>;
    </script>
  </head>
  
  <body>
        <div class="my-container" id="myContainerWin">
		        <div class="ui dimmer body" id="myLoader">
		           <div class="ui large text loader">正在上传附件，请稍后...</div>
		        </div>
		        
            <input type="hidden" id="paymentIds">
            <input type="hidden" id="financeIds">
            <input type="hidden" id="sortOfType" value=0><!-- 排序的依据 :0,按日期；1,按票据编号-->
            <!-- 财务流水账 -->
            <div class="running-account-list-div" id="runningAccountListDiv">
                <div id="rendertoolbar"></div>
                <div class="running-account-list" id="runningAccountList"></div>  
            </div>
            <!-- 货币总信息 -->
            <div class="currency-list-div" id="currencyListDiv">
              
            </div>
            
            <!-- 高级查询窗口 -->
            <div class="my-jqx-window" id="advanceQueryWin">
                <div>高级查询</div>
                <div>
                   <div class="query-condition-div">
                       <table class='query-condition-table' width="100%" cellspacing=0 cellpadding=0>
                       <tbody>
                           <tr>
                              <td>
                                  <label class="finance-subj-label" for="financeSubjIds">财务科目:</label>
                                  <!-- <input class="finance-subject" type="text" id="financeSubjIds"> -->
                                  <div id="financeSubjIds">
                                      <!-- 财务科目列表 -->
											                 <div id="financeSubjectTree"></div>
											                    
                                  </div>
                              </td>
                              <td>
                                  <label>收/付款人:</label>
                                  <!-- <input class="aim-people-name" type="text" id="aimPeopleNames"> -->
                                  <select class="selectpicker aim-people-name" id="aimPeopleNames"  multiple data-live-search="true" style="display: none;">
                             
                                  </select>
                          
                                  <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                              </td>
                           </tr>
                           <tr>
                              <td>
                                  <label for="aimDates">票据日期:</label>
                                  <!-- <input class="finance-subject" type="text" id="aimDates"> -->
                                  <select class="selectpicker" id="aimDates"  multiple data-live-search="true" style="display: none;">
                                  
                                  </select>
                                  <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                              </td>
                              <td>
                                  <label for="agents">记账人:</label>
                                  <!-- <input class="aim-people-name" type="text" id="agents"> -->
                                  <select class="selectpicker" id="agents" multiple data-live-search="true" style="display: none;">
                                  
                                  </select>
                                  <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                              </td>
                           </tr>
                           <tr>
                              <td>
                                  <label>收付款:</label>
                                  <label><input type="radio" name="formType" value="" checked>全部</label>
                                  <label><input type="radio" name="formType" value=1>付款</label>
                                  <label><input type="radio" name="formType" value=2>收款</label>
                                  <label><input type="radio" name="formType" value=3>借款</label>
                              </td>
                              <td>
                                  <label>有无发票:</label>
                                  <label><input type="radio" name="hasReceipt" value="" checked>全部</label>
                                  <label><input type="radio" name="hasReceipt" value=true>有发票</label>
                                  <label><input type="radio" name="hasReceipt" value=false>无发票</label>
                              </td>
                           </tr>
                           <tr>
                              <td>
                                  <label>结算状态:</label>
                                  <label><input type="radio" name="status" value="" checked>全部</label>
                                  <label><input type="radio" name="status" value=1>已结算</label>
                                  <label><input type="radio" name="status" value=0>未结算</label>
                              </td>
                              <td>
                                  <label>票据种类:</label>
                                  <label><input type="radio" name="billType" value="" checked>全部</label>
                                  <label><input type="radio" name="billType" value=1>普通发票</label>
                                  <label><input type="radio" name="billType" value=2>增值税发票</label>
                              </td>
                           </tr>
                           <tr>
                              <td>
                                  <label for="queryPaymentWay">付款方式:</label>
                                  <select class="selectpicker" id="queryPaymentWay" style="display: none;"></select>
                              </td>
                              <td>
                                  <label for="minMoney">金额区间:</label>
                                  <input class="min-money" type="text" id="minMoney" onkeyup="indexOfMoney(this)">-
                                  <input class="max-money" type="text" id="maxMoney" onkeyup="indexOfMoney(this)">
                              </td>
                           </tr>
                           <tr>
                              <td>
                                  <label for="querySummary">摘要:</label>
                                  <input type="text" id="querySummary">
                              </td>
                              <td>
                                  <label for="department">部门:</label>
                                   <select class="selectpicker" id="department" multiple data-live-search="true" style="display: none;">
                                  </select>
                                  <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                              </td>
                           </tr>
                       </tbody>	
                       
                   </table> 
                   </div>
                   
                   <!-- 按钮 -->
                   <div class="btn-list">
                      <input class="determine-query" type="button" value="查询" onclick="determineQuery()">
                      <input class="close-query" type="button" value="关闭" onclick="closeQuery()">
                      <input class="clear-query" type="button" value="清空" onclick="clearQuery()">
                   </div>
                   
                    
                   
                </div>
                
                
            </div>
            
            <!-- 修改单据窗口 -->
            <div class="jqx-window" id="modifyReceiptWin">
                <div>修改单据</div>
                <div id="receiptContentDiv">
                    <!-- 盛放单据页面 -->
                    <!-- <div class="full-receipt-div" id="fullReceiptDiv"></div> -->
                    <iframe class="full-receipt-div" id="fullReceiptDiv" scrolling="auto" width="100%"></iframe>
                    <!-- 盛放上传附件 -->
                    <div class="payment-upload-wrap" id="paymentUploadWrap">
                        <div class="payment-upload-title">
                            <div class="upload-file-btn" id="uploadFileBtn">添加附件</div> 
                        </div>
                        <div class="show-upload-file">
                            <ul id="showAttachmentFile"></ul>
                        </div>
                   </div>
                    
                    <!-- 付款单按钮 -->
                    <div class="modify-payment-btn-list">
                        <input type="button" value="打印" onclick="printModifyAfterPayment()">
                        <input class="settle-btn" type="button" id="paymentOrderSettleBtn" value="结算" onclick="saveModifyAfterPayment(1)">
                        <input type="button" id="saveModifyAfterPayment" value="保存" onclick="saveModifyAfterPayment(0)">
                        <input type="button" id="deleteModifyPayment" value="删除" onclick="deleteModifyPayment()">
                        <input type="button" value="关闭" onclick="closeModifyReceiptWin()">
                    </div>
                    <!-- 收款单按钮 -->
                    <div class="modify-collection-btn-list">
                        <input type="button" value="打印" onclick="printModifyAfterColletcion()">
                        <input type="button" id="saveModifyAfterColletion" value="保存" onclick="saveModifyAfterColletion()">
                        <input type="button" id="deleteModifyColletion" value="删除" onclick="deleteModifyColletion()">
                        <input type="button" value="关闭" onclick="closeModifyReceiptWin()">
                    </div>
                </div>
            </div>
            
        </div>
        <!-- 带有会计科目信息的付款单列表 -->
        <div class="account-subject-page" id="accountSubjectPage">
        <!-- 财务科目id -->
        <input type="hidden" id="accountFinanceIds">
            <div id="accountSubjectTable"></div>
            
            <!-- 查询窗口 -->
            <div class="my-jqx-window" id="hasAccountQueryWin">
                <div>高级查询</div>
                <div>
                    <div class="query-condition-div">
                        <table class='query-condition-table' width="100%" cellspacing=0 cellpadding=0>
                            <tr>
                                <td>
                                    <label>日期:</label>
                                    <select class="selectpicker" id="accountPaymentDates"  multiple data-live-search="true" style="display: none;">
                                  
                                    </select>
                                    <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                                </td>
                                <td>
                                    <label>会计科目:</label>
                                    <select class="selectpicker" id="accountSubjectCodes"  multiple data-live-search="true" style="display: none;">
                                  
                                    </select>
                                    <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label class="finance-subj-label" for="accountFinanceSubjIds">财务科目:</label>
	                                  <!-- <input class="finance-subject" type="text" id="financeSubjIds"> -->
	                                  <div id="accountFinanceSubjIds">
	                                      <!-- 财务科目列表 -->
	                                       <div id="accountFinanceSubjectTree"></div>
	                                          
	                                  </div>
                                </td>
                                <td>
                                    <label>收款人:</label>
                                    <select class="selectpicker" id="accountAimPeopleName"  multiple data-live-search="true" style="display: none;">
                                  
                                    </select>
                                    <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label for="accountSummary">摘要:</label>
                                    <input type="text" id="accountSummary">
                                </td>
                                <td>
                                    <label for="accountMinMoney">金额:</label>
                                    <input class="min-money" type="text" id="accountMinMoney" onkeyup="indexOfMoney(this)">-
                                  <input class="max-money" type="text" id="accountMaxMoney" onkeyup="indexOfMoney(this)">
                                </td>
                            </tr>
                        </table>
                    </div>
                    <!-- 按钮 -->
                    <div class="btn-list">
                      <input class="determine-query" type="button" value="查询" onclick="queryHasAccoutPayment()">
                      <input class="clear-query" type="button" value="清空" onclick="clearHasAccountPayment()">
                      <input class="close-query" type="button" value="关闭" onclick="closeHasAccountPayment()">
                   </div>
                   
                </div>
            </div>
            
            
           
            
        </div>
        
         <!-- 导出 -->
            <div id="loadingDiv" class="show-loading-container">
                <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
            </div>
            
            <!--导入窗口  -->
					  <div id="importRunningAccountWin" class="jqx-window">
							<div>导入</div>
								<div>
								    <iframe id="importIframe" width="100%" height="100%"></iframe>
								</div>
					</div>
					
					<!-- 加载表格 -->
					 <div id="loadingTable" class="show-loading-container">
              <div class="show-loading-div"> Loading... </div>
           </div>
           
           <!-- 选择借款单弹窗 -->
        <div class="jqx-window" id="queryNotPayedLoanList">
            <div>请选择借款单</div>
            
            <div class="query-loan-list">
                <div class="query-loan-list-header">
                        <span class="loan-person-name" id="name"></span>
                        <span class="account-money-tips" id="loanAccountMoney"></span>                  
                </div>
                <div class="query-loan-list-body">
                    <div id="queryLoanList"></div>
                </div>
                <div class="balance-tips-div">
                    <span class="balance-tips-span">报销/还<span id="loanOrderId">借款。</span>共计借款&nbsp;<span id="payedMoneyCount"></span>。&nbsp;&nbsp;[结算后，补领<span id="balanceTips"></span>]</span>
                </div>
                <div class="win-btn-list-div">
                    <input type="button" value="确定" onclick="reimbursementLoanOrder()">
                    <!-- <input type="button" value="取消" onclick="cancelSelectLoanOrder()"> -->
                </div>
            </div>
            
        </div>
        
        
        <!-- 付合同款弹窗 -->
        <div class="jqx-window" id="payedContractMoneyWin">
            <div>12345</div>
            
            <div class="contract-money-info">
                <div class="contract-window-header">
                    <span class="contract-pay-way" id="payWay"></span>
                    <span class="by-month-tips"></span>
                </div>
                <div class="contract-window-bankInfo">
                    <span id="bankName"></span>
                    <span id="bankAccountName"></span>
                    <span id="bankAccountNumber"></span>
                </div>
                <div class="contract-window-table" id="contractWindowTable">
                    
                </div>
                <div class="contract-window-footer">
                    <span class="pay-info-span" id="payInfoSpan"></span>
                </div>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick="settlementContract()">
                </div>
                
            </div>
        </div>
        
        
        <div class="jqx-window" id="contractDetailWin">
            <div>合同详情</div>
            <div>
                <iframe id="contractDetailIframe" src="" width="100%" height="100%"></iframe>
            </div>
        </div>
        
        <!-- 缴税信息窗口 -->
        <div id="setTaxWin" class="jqx-window set-tax-window">
            <div>缴税信息</div>
            <div>
                <fieldset class="set-tax-tips-fieldset">
                    <legend>提醒</legend>
                    <p class="set-tax-tips">您尚未设置缴税信息，请填写以下信息</p>
                </fieldset>
                <div class="form-info">
                    <ul>
                        <li>
                            <p>税务科目</p>
                            <input id ="taxFinanSubjName" type="text" onclick="showSelectFinanSubjDiv(this, event)">
                            <input id = "taxFinanSubjId" type="hidden">
                            <a class="clear-finance" href="javascript:void(0);" id="clearFinanceSubj" onclick="clearFinanceSubj()">清空</a>
                        </li>
                        <li>
                            <p>税率</p>
                            <input id="taxRate" type="text" onkeyup="isNum(this)">
                        </li>
                    </ul>
                    <!-- 选择财务科目 -->
                    <div id="levelPopup" class="fin_subj">
                        <div class="filter-con">
                          <input class="filter-input" type = "text" id = "filter">
                          <div class="filter-btn" id="filterBtn"><img src="<%=request.getContextPath()%>/css/finance/image/search.png"/></div>
                        </div>
                        <div id="subjectTree"></div>    
                    </div>
                </div>
                <div class="win-btn-list-div">
                    <input type="button" value="确定" onclick="setTaxInfo()">
                    <input type="button" id="cancelSetTaxBtn" value="取消">
                </div>
            </div>
        </div>
           
  </body>
</html>
