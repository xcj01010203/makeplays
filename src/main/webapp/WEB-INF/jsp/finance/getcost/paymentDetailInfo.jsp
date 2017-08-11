 <%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
 <%@page import="com.xiaotu.makeplays.user.model.UserInfoModel"%>
 <%@page import="com.xiaotu.makeplays.utils.Constants"%>
<% 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isRunningAccountReadonly = false; 
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.RUNNGING_ACCOUNT) == 1){
        isRunningAccountReadonly = true;
    }
}

Object modifySettledPayment = false; 

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.MODIFY_SETTLED_PAYMENT)){
        modifySettledPayment = true;
    }
}

UserInfoModel userInfo = (UserInfoModel)session.getAttribute(Constants.SESSION_USER_INFO);
%> 

<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
      
			<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/paymentDetailInfo.css">
			<link rel="stylesheet" href="<%=basePath%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
      <link rel="stylesheet" href="<%=basePath%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
			
			<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
			<script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
			<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdropdownbutton.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxbuttons.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtree.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
            <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
		        <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script>
            
	        <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
			<script type="text/javascript" src="<%=basePath%>/js/finance/getcost/paymentDetailInfo.js"></script>
			
      <script>
        <%-- var isRunningAccountReadonly = <%=isRunningAccountReadonly%>; --%>
        var modifySettledPayment = <%=modifySettledPayment%>;
        var isRunningAccountReadonly = <%=isRunningAccountReadonly%>;
      </script>
  </head>
  
  <body>
    <div class="payment-order-box" id="loadPayMentBox">
      <!-- 付款单id -->
      <input type="hidden" id="paymentId" value="${paymentId}">
      <!-- 由于修改付款单的时候，票据编号也需要跟着变动，这里记录一下付款单最原始的票据编号 -->
      <input type="hidden" id="originalReceipNo">
      <input type="hidden" id="originalHasReceipt">
      <input type="hidden" id="originalPaymentDate">
      <!-- 关联的合同Id -->
       <input type="hidden" id="contractId"> 
      <!-- 关联合同ID -->
      <input type="hidden" id="contractNo">
      <!-- 关联的合同的类型 -->
       <input type="hidden" id="contractType">
       <!-- 关联合同期数id  -->
        <input type="hidden" id="contractPartId" >
	      <!-- 还借款id -->
	      <input type="hidden" id="loanIds">
	      <!-- 判断是否是 待付合同清单-->
	      <input type="hidden" id="isContractToPaid" value="${isContractToPaid}">
	      <!-- 盛放付款单的结算状态 -->
	      <input type="hidden" id="paymentStatus">
	      <!-- 合同支付信息的id -->
	      <input type="hidden" id="contractPartIds">
	      <!-- 附件包id -->
	      <input type="hidden" id="attachmentPcketId">
        <dl class="payment-box-dl">
            <dt>
                <div class="payment-header">
                    <div class="bills-num">
                                                                                     票据编号 : 
                        <input class="header-input" type="text" id="billsNum" readonly>                                                      
                    </div>
                    <div class="header-title">付款单</div>
                    <div class="bills-date">
                                                                                     日期 : 
                        <input class="header-input" type="text" id="billsDateTime" onfocus="WdatePicker({isShowClear:false,readOnly:true, onpicked: receipDateChange});">
                        
                    </div>
                    <div class="div-clear"></div>
                </div>
            </dt>
            <dd>
                <div class="payment-header payment-content">
                    <table width="100%" class="payment-table" cellspacing=0 cellpadding=0>
                        <tbody>
                        	
                            <tr>
                                <td>
                                	<span class="department-td">部门: </span>
                                	<input type='text' id='departmentText'  class='department-text' onclick="showDepartment(event)" onkeyup='searchDepartmentName(this)'>
                                	<span class="shoukuan-td">收款方(单位):</span>
                                    <input class="shoukuan-person" type="text" id="receivingParty" onclick="getReceiveingParty(this)">
                                    <ul class="dropdown_box">
                                        <!-- <span class="arrows_up"></span> -->
                                    </ul>
                                </td>
                                <td>
                                    <div class="check-box-div">
                                        <label class="check-box-label">
		                                        <input type="checkbox" name="payContractMoney" id="payContractMoney" onchange="showContractPayList();">
		                                        <span>付合同款</span>
                                        </label>
                                        <!-- 合同收款人下拉列表 -->
                                        
                                        <div class="contract-receive-person" id="receivePersonDropdown">
					                               <div class="jqx-drop-down-btn" id="dropDownButton">
							                                <div class="contact-pay-list" id="contactPayTree">
								                                    <input class="contact-pay-list-search" type="text" id="contactInputSearch" name="contact-pay-list-search" onKeyDown="searchName(event)">
								                                    <dl class="contract-dl-con" id="contractDlCon">
								                                        <dt class="worker-dl-dt" onclick="showDropList(this)">职员合同</dt>
								                                        <dd class="worker-dl-dd">
								                                            <table class="worker-contract-table"></table>
								                                        </dd>
								                                        <dt class="actor-dl-dt" onclick="showDropList(this)">演员合同</dt>
								                                        <dd class="actor-dl-dd">
								                                            <table class="actor-contract-table"></table>
								                                        </dd>
								                                        <dt class="produce-dl-dt" onclick="showDropList(this)">制作合同</dt>
								                                        <dd class="produce-dl-dd">
								                                            <table class="produce-contract-table"></table>
								                                        </dd> 
								                                    </dl>
								                                    
							                                  </div>
							                                </div>
					                              </div>
					                              
                                        
                                    
                                    
                                    </div>
                                </td>
                                
                                    
                               
                            </tr>
                            <tr>
                                <td colspan ="2">
                                    <table class="payment-detail-table" cellspacing="0">
                                        <thead>
                                            <tr class="tr-back-color">
                                                <td width="20"></td>
                                                <td>摘要</td>
                                                <td>财务科目</td>
                                                <td width="120">币种</td>
                                                <td width="185">金额</td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr class="add-sub-tr">
                                                <td width="20">
                                                    <div class="td-div">
                                                        <div class="addition" onclick="additionTr(this);"></div>
                                                        <div class="subtraction" onclick="subtractionTr(this);"></div>
                                                    </div>
                                                </td>
                                                <td>
                                                   <div class="td-div">
                                                      <input class="summary" type="text" name="zhaiyao" maxlength="200">
                                                   </div> 
                                                </td>
                                                <td>
                                                    <div class="td-div">
                                                        <span class="select-flag"></span>
                                                        <input type="text"  class="text-finance-subject finance-subject1" name="textFinanceSubj" sid="financeSubjName1" id="1" readonly>
                                                        <input type="hidden" id="financeSubjId">
                                                       
											                                  
                                                    </div>
                                                </td>
                                                <td rowspan="500">
                                                    <select class="select-currency" name="currency" id="currency" onchange="currencyChange(this)">
                                                        
                                                    </select>
                                                    <input type="hidden" id="selectedCurrencyValue" >
                                                </td>
                                                <td class="money-td" align="right">
                                                    <input class="money-td-input" type="text"  name="totalMoney"  maxlength="10">
                                                    <input class='no-format-money' type="hidden">
                                                </td>
                                            </tr>
                                        </tbody>
                                        <tfoot>
                                            <tr>
                                                <td colspan="4" align="left">
                                                    <div class="td-div">金额合计: 
                                                    <!-- 盛放大写金额合计 -->
                                                        <span class="total-account-money" id="totalAccountMoney">
                                                        </span>
                                                    </div>
                                                </td>
                                                <td align="right">
                                                    <div class="td-div">
                                                        <span id="formatTotalMoney"></span>
                                                        <input type="hidden" id="readTotalMoney">
                                                    </div>
                                                </td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <div class="bottom-option">有无发票: 
                                        <select class="is-has-bill" id="hasReceipt" onchange="chageHasReceipt(this);">
                                            <option value="true">有</option>
                                            <option value="false">无</option>
                                        </select>
                                    </div>
                                    <div class="bottom-option">附单据: 
                                        <input type="text" id="billCount" value="0">张
                                    </div>
                                    <div class="bottom-option">付款方式: 
                                        <input class="arrows-low" type="text" id="paymentWay">
                                        <ul class="paymethod-dropdown-box">
                                            <!-- <span class="arrows_up"></span> -->
                                        </ul>
                                    </div>
                                    <div class="bottom-option">记账: 
                                        <input class="agent-people" type="text" id="agent">
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                     <!-- 部门下拉框 -->
			         <ul id='department' class='department-ul' >
			         </ul>
                    <div class="bottom-option-con">
                        <div class="bottom-option-div" id="bottomOptionDiv">
                            <div class="bottom-option">票据种类: 
		                            <select id="billType" onchange="billTypeChange(this)">
		                                <option value="1" checked>普通发票</option>
		                                <option value="2">增值税发票</option>
		                            </select>
                            </div>
		                        <div class="bottom-option">是否收到票: 
		                            <select class="if-receive-bill" id="ifReceiveBill">
		                                <option value="true" checked>是</option>
		                                <option value="false">否</option>
		                            </select>
		                        </div>
                        </div>
                        
                        <div  class="remind-time" id="remindTime">(&nbsp;&nbsp;开启提醒: &nbsp;&nbsp;
                            <input type="text" class="time-input" id="remindTimeValue" onfocus="WdatePicker({isShowClear:false,readOnly:true})" readonly>&nbsp;&nbsp;
                            <span class="remind-image" onClick="WdatePicker({el:'remindTimeValue'})"></span>&nbsp;&nbsp;)
                        </div>
                        
                        <div class="loan-info-div">
                            <div><span class="loan-money-info" id="payLeftMoney"></span></div>
                            <div><span class="loan-money-info" id="forLoanMoney"></span></div>
                            <div>
                                <span class="loan-money-info" id="loanLeftMoney"></span>
                                <input type="button" id="repayLoans" value="还借款" onclick="showSelectLoanOrder()">
                            </div>
                        </div>
                    </div>
                    
                </div>  
            </dd>
        </dl>
        
        
        
         <!-- 选择财务科目 -->
				<div id="levelPopup" class="fin_subj">
					<div class="filter-con">
						<input class="filter-input" type="text" id="filter" autocomplete="off">
            <input type="text" style="display: none;">
						<div class="filter-btn" id="filterBtn">
							<img
								src="<%=request.getContextPath()%>/css/finance/image/search.png" />
						</div>
					</div>
					<div id="subjectTree"></div>
				</div>
				
				
	    </div>
  </body>
</html>
