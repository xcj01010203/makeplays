<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/loanDetailInfo.css">
    <link rel="stylesheet" href="<%=basePath%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtree.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/loanDetailInfo.js"></script>
     <script>
        var isRunningAccountReadonly = <%=isRunningAccountReadonly%>;
      </script>
  </head>
  
  <body>
      <div class="loan-order-box" id="loadLoanBox">
      <input type="hidden" id="loanId" value="${loanId}">
      <!-- 由于修改的时候，票据编号也需要跟着变动，这里记录一下付款单最原始的票据编号 -->
      <input type="hidden" id="originalReceipNo">
      <input type="hidden" id="originalLoanDate">
       <input type="hidden" id="attachmentPacketId">
          <dl class="loan-box-dl">
              <dt>
                <div class="loan-header">
                    <div class="bills-num">
                                                                                     票据编号 : 
                        <input class="header-input" type="text" id="billsNum" readonly>                                                      
                    </div>
                    <div class="header-title">借款单</div>
                    <div class="bills-date">
                                                                                     日期 : 
                        <input class="header-input" type="text" id="billsDateTime" onfocus="WdatePicker({isShowClear:false,readOnly:true, onpicked: getLoanOrderNum});">
                    </div>
                    <div class="div-clear"></div>
                </div>
            </dt>
            <dd>
                <div class="loan-header">
                    <table width="100%" class="loan-table" cellspacing=0 cellpadding=0>
                        <tbody>
                            <tr>
                                <td class="jiekuan-td">借款方(单位)&nbsp;&nbsp;: </td>
                                <td>
                                    <input class="jiekuan-person" type="text" id="loanMoneyParty" onclick="getBorrowedName(this)">
                                    <ul class="dropdown_box">
                                        <!-- <span class="arrows_up"></span> -->
                                    </ul>
                                </td>
                            </tr>
                            <tr>
                                <td class="zhaiyao">摘&nbsp;&nbsp;要: </td>
                                <td>
                                    <input class="jiekuan-person" type="text" id="summary" maxlength="200">
                                </td>
                            </tr>
                            <tr>
                                <td class="finance-subject">财务科目: </td>
                                <td>
                                    <input class="finance-subject-name" type="text" id="financeSubjName">
                                    <input type="hidden" id="financeSubjId">
                                    <a class="clear-finance" href="javascript:void(0);" id="clearFinanceSubj" onclick="clearFinanceSubj()">清空</a>
                                    <div class="money-div">金&nbsp;&nbsp;额: 
                                        <select class="select-currency" name="currency" id="currency">
                                                        
                                        </select>
                                        <input class="money-count" type="text" id="money" maxlength="10">
                                        <input type="hidden">
                                    </div>
                                     
                                </td>
                            </tr>
                            <tr class="last-tr">
                                <td class="payMethod">付款方式&nbsp;&nbsp;: </td>
                                <td class="last-td">
                                    
                                    <select class="arrows-low" id="paymentWay">
                                        <option value='1' checked="true">现金</option>
                                        <option value='2'>现金(网转)</option>
                                        <option value='3'>银行</option>
                                    </select>
                                    <div class="capital-money-div">
                                        <span class="capital-account-money" id="capitalAccountMoney"></span>
                                    </div>
                                    
                                    <div class="agent-div">记账&nbsp;&nbsp;:
                                        <input class="agent-person" type="text" id="agent">
                                                                                                                                             
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </dd>
          </dl>
          
         
          <!-- 选择财务科目 -->
	        <div id="levelPopup" class="fin_subj">
	          <div class="filter-con">
	            <input class="filter-input" type="text" id="filter">
	            <input class="filter-input" style="display: none;">
	            <div class="filter-btn" id="filterBtn">
	              <img
	                src="<%=request.getContextPath()%>/css/finance/image/search.png" />
	            </div>
	          </div>
	          <div id="subjectTree" style="height: 140px;"></div>
	        </div>
        
        
      </div>
  </body>
</html>
