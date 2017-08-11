<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object hasExportSettlementAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_SETTLEMENT) != null){
    	hasExportSettlementAuth = true;
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
		<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/finance/financeSettlement.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/financeSettlement.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/exportLoading.css">
    <script type="text/javascript">
    var hasExportSettlementAuth = <%=hasExportSettlementAuth%>;
    </script>
  </head>
  
  <body>
     <div class="my-container">
     <!-- 财务科目结算表 -->
        <div class="finance-subj-settle-list-div" id="financeSubjSettleListDiv">
            <div id="financeSubjSettleList"></div>
        </div>
     <!-- 结算汇总表 -->
        <div class="settle-account-list">
            <div id="settleAccountList">
            
            </div>
        </div>
        <div>备注：可机动费用=总预算-合同款-借款未还款</div>
        
        <!-- 结算时间段窗口 -->
        <div class="jqx-window" id="settleTimeWin">
            <div>结算时间段</div>
            <div>
                <ul>
                    <li>
                        <span>从:</span>
                        <input class="start-date" id="startDate" type="text" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
                    </li>
                    <li>
                        <span>到:</span>
                        <input class="end-date" id="endDate" type="text" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
                    </li>
                </ul>
                <div class="btn-list">
                    <input type="button" onclick="querySettleTime()" value="确定">
                </div>
            </div>
        </div>
        
        
        
        
        
        <!-- 滑动窗口 -->
        <div class="right-popup-win" id="rightPopUpWin">
            <input type="hidden" id="financeSubjectId">
            <div class="win-header-div">
                <input type="button" value="关闭" onclick="closePopUpWin()">
            </div>
            <div class="finance-settlement-info">
                <!-- tab页 -->
		            <div class="tab-body-wrap">
		                <div class="btn_tab_wrap">
		                    <!-- tab键空白处 -->
		                    <div class="btn_wrap"><span class='finance-SubjName-span' id='financeSubjNameSpan'></span></div>
		                    <!-- tab键 -->
		                    <div class="tab_wrap">
		                        <ul>
		                            <li id="budget_expenditure" class="tab_li_current" onclick="showBudgetExpenditure(this)">预算总支出</li>
		                            <li id="related_contract" onclick="relatedContract(this)">关联合同列表</li>
		                            <li id="contract_payment" onclick="paymentContract(this)">合同付款单列表</li>
		                            <li id="related_loan" onclick="relatedLoanList(this)">关联借款单列表</li>
		                            <li id="repayment_list" onclick="repaymentList(this)">还款列表</li>
		                        </ul>
		                    </div>
		                </div>
		                <!-- 财务科目的预算总支出列表 -->
		                <div class="public-content-style budget-expenditure">
		                   <div class="grid-style" id="totalpaymentListGrid"></div> 
		                   <div class="payment-count">
		                      <table cellspacing = "0" cellpadding="0" id="paymentCountTable">
		                          <tr>
		                              <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="paymentCountMoney"></div></td>
		                          </tr>
		                      </table>
		                   </div>
		                </div>
		                <!-- 财务科目的关联合同列表 -->
		                <div class="public-content-style related-contract">
                       <div class="grid-style" id="contractListGrid"></div> 
                       <div class="payment-count">
                          <table cellspacing = "0" cellpadding="0" id="contractCountTable">
                              <tr>
                                  <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="contractCountMoney"></div></td>
                              </tr>
                          </table>
                       </div>
                    </div>
                    <!-- 合同付款单列表 -->
                    <div class="public-content-style contract-payment">
                       <div class="grid-style" id="contractPaymentListGrid"></div> 
                       <div class="payment-count">
                          <table cellspacing = "0" cellpadding="0" id="conPayCountTable">
                              <tr>
                                  <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="conPayCountMoney"></div></td>
                              </tr>
                          </table>
                       </div>
                    </div>
                    <!-- 关联借款单列表 -->
                    <div class="public-content-style related-loan">
                       <div class="grid-style" id="loanListGrid"></div> 
                       <div class="payment-count">
                          <table cellspacing = "0" cellpadding="0" id="loanCountTable">
                              <tr>
                                  <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="loanCountMoney"></div></td>
                              </tr>
                          </table>
                       </div>
                    </div>
                    <!-- 还款列表 -->
                    <div class="public-content-style repayment-list">
                       <div class="grid-style" id="loanPaymentListGrid"></div>
                       <div class="payment-count">
                          <table cellspacing = "0" cellpadding="0" id="loanPayCountTable">
                              <tr>
                                  <td style="text-align: left; padding-left: 5px;"><div class="payment-count-div" id="loanPayCountMoney"></div></td>
                              </tr>
                          </table>
                       </div>
                    </div>
		            </div>
            </div>
            
        </div>
        
        
        <!-- 导出 -->
        <div id="loadingDiv" class="show-loading-container">
            <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
        </div>
        
     </div>
  </body>
</html>
