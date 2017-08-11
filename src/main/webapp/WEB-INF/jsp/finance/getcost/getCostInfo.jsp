<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.xiaotu.makeplays.user.model.UserInfoModel"%>
<%@page import="com.xiaotu.makeplays.utils.Constants"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object obj = session.getAttribute("userAuthMap");
UserInfoModel userInfo = (UserInfoModel)session.getAttribute(Constants.SESSION_USER_INFO);
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
		
		<link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/getCostInfo.css">
		
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    
		<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/interactiveFrame.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/finance/getcost/getCostInfo.js"></script>
		
	    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
	    <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
	    <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
  </head>
  
  <body>
    <div class="my-container">
        <div class="ui dimmer body" id="myLoader">
           <div class="ui large text loader">正在上传附件，请稍后...</div>
        </div>
        <!-- 判断跳转页面 -->
        <input type="hidden" id="receiptType" value="${receiptType}">
        
        <!-- 付款单id -->
        <!-- <input type="hidden" id="paymentId">
        <input type="hidden" id="contractId">
        <input type="hidden" id="contractType"> -->
        
        <!-- 收款单id -->
       <!--  <input type="hidden" id="collectionId"> -->
        <!-- 借款单id -->
        <!-- <input type="hidden" id="loanId"> -->
        <div class="ui dimmer body" id="myLoader">
           <div class="ui large text loader">正在上传附件，请稍后...</div>
        </div>
        <div class="tab-body-wrap">
            <!-- tab键容器 -->
            <div class="btn_tab_wrap">
                <!-- tab键空白处 -->
                <div class="btn_wrap"></div>
                <!-- tab键 -->
                <div class="tab_wrap">
                    <ul>
                        <li id="tab_1_paymentOrder" data-flag="pay" class="tab_li_current" onclick="paymentOrderTab()">付款单</li>
                        <li id="tab_2_receiptOrder" data-flag="coll" onclick="collectionOrderTab()">收款单</li>
                        <li id="tab_3_loanbillOrder" data-flag="loan" onclick="loanOrderTab()">借款单</li>
                    </ul>
                </div>
            </div>
            <div class="budget-public payment-order-div" id="paymentDiv">
                <div class="payment-iframe-div" id="payment">
                   <!--  <iframe id="paymentOrderIframe" width="100%" height="100%"  src="/paymentManager/toPaymentDetailPage"></iframe> -->
                   <!-- <div class="payment-load-div" id="paymentOrderDiv"></div> -->
                   <iframe class="full-receipt-div" id="fullReceiptDiv" scrolling="auto" width="100%"></iframe>
                   <!--付款单附件上传区域-->
                   <div class="payment-upload-wrap">
                        <div class="payment-upload-title">
                            <div class="upload-file-btn" id="uploadPaymentBtn">添加附件</div> 
                        </div>
                        <div class="show-upload-file">
                            <ul id="showAttachmentFilePay"></ul>
                        </div>
                   </div>

		               <div class="btn-list">
		                    <input class="payment-btn settle-btn" type="button" id="settlement"  value="结算" onclick="savePaymentMethod(1, false)">
		                    <input class="payment-btn double-function-button" type="button" id="settlementPrint"  value="结算并打印" onclick="savePaymentMethod(1, true)">
		                    <input class="payment-btn save-btn" type="button" id="savePayment" value="保存"  onclick="savePaymentMethod(0, false)">
		                    <input class="payment-btn double-function-button" type="button" id="savePrintPayment"  value="保存并打印" onclick="savePaymentMethod(0, true)">
                   </div>
                </div>
            </div>
            
            <div class="budget-public collection-order-div" id="collectionDiv">
                
                <div class="collection-iframe-div" id="collection">
                    <iframe class="collection-iframe" id="collectionOrderIframe" width="100%" height="100%"></iframe>
                    
                    <!--收款单附件上传区域-->
                   <!-- <div class="collection-upload-wrap">
                        <div class="collection-upload-title">
                            <div class="upload-coll-btn" id="uploadCollectionBtn">添加附件</div> 
                        </div>
                        <div class="show-upload-file">
                            <ul id="showAttachmentFileColl"></ul>
                        </div>
                   </div> -->
                   
                    <div class="btn-list">
					              <input class="collection-btn save-btn" type="button" id="saveCollection"  value="保存" onclick="saveCollectionMthod(false);">
					              <input class="collection-btn double-function-button" type="button" id="savePrintCollection"  value="保存并打印" onclick="saveCollectionMthod(true);">
                    </div>   
                </div>
                
            </div>
            
            <div class="budget-public loan-order-div" id="loanDiv">
                <div class="loan-iframe-div" id="loan">
                   <iframe class="loan-iframe" id="loanOrderIframe" width="100%" height="100%"></iframe>
                   <!-- 借款单附件上传区域 -->
                   <div class="loan-upload-wrap">
                        <div class="loan-upload-title">
                            <div class="upload-loan-btn" id="uploadloanBtn">添加附件</div> 
                        </div>
                        <div class="show-upload-file">
                            <ul id="showAttachmentFileLoan"></ul>
                        </div>
                   </div>
                   
                   <div class="btn-list">
		                   <input class="loan-btn save-btn" type="button" id="saveLoan"  value="保存" onclick="saveLoanMethod(false)">
		                   <input class="loan-btn double-function-button" type="button"  data-type="1" value="保存并打印" onclick="saveLoanMethod(true)">
                   </div>  
                </div>
            </div>
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
        
        <!-- 验证财务密码、用户手机号 -->
	    <div id="validUserWin" class="jqx-window validate-user-win">
	        <div>安全验证</div>
	        <div>
	            <ul>
	                <li class="financepassli">
	                    <input class="finance-password" type="password" id="financePwd"  onkeyup="finanPwdWinKeyup(event)" placeHolder="请输入财务密码">
	                </li>
	                <li class="useripli">当前用户手机号：<%=userInfo.getPhone() %></li>
	                <li class="useripli">
	                    <input class="verify-code" type="text" id="verifyCode" placeHolder="验证码，有效期为1分钟">
	                    <input type="button" class="get-verifycode-btn" id="getVerifyCodeBtn" value="获取验证码" onclick="obtainVerifyCode(this)">
	                </li>
	            </ul>
	            <div class="win-btn-list-div">
	                <input type="hidden" id="validType">
	                <input type="button" value="确定" onclick="checkPassword()">
	                <input type="button" value="取消" onclick="closePwdWindow()">
	            </div>
	         </div>
	    </div>    
        
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
        
    </div>
  </body>
</html>
