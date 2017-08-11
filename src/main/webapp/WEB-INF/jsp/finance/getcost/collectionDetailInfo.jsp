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
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/collectionDetailInfo.css">
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/collectionDetailInfo.js"></script>
    <script>
        var isRunningAccountReadonly = <%=isRunningAccountReadonly%>;
        var basePath = "<%=basePath%>";
    </script>
  </head>
  
  <body>
      <div class="collection-order-box" id="loadCollectionBox">
      <!-- 盛放收款单id -->
      <input type="hidden" id="collectionId" value="${collectionId}">
      <!-- 由于修改的时候，票据编号也需要跟着变动，这里记录一下付款单最原始的票据编号 -->
      <input type="hidden" id="originalReceipNo">
      <input type="hidden" id="originalCollectionDate">
          <dl class="collection-box-dl">
              <dt>
                <div class="collection-header">
                    <div class="bills-num">
                                                                                     票据编号 : 
                        <input class="header-input" type="text" id="billsNum" readonly>                                                      
                    </div>
                    <div class="header-title">收款凭证</div>
                    <div class="bills-date">
                                                                                     日期 : 
                        <input class="header-input" type="text" id="billsDateTime" onfocus="WdatePicker({isShowClear:false,readOnly:true, onpicked: getNewCollectionNum});">
                    </div>
                    <div class="div-clear"></div>
                </div>
            </dt>
            <dd>
                <div class="collection-header">
                    <table width="100%" class="collection-table" cellspacing=0 cellpadding=0>
                        <tbody>
                            <tr>
                                <td class="fukuan-td">付款方(单位)&nbsp;&nbsp;: </td>
                                <td>
                                    <input class="fukuan-person" type="text" id="payMoneyParty" onclick="getPayNameList(this)">
                                    <ul class="dropdown_box">
                                        <!-- <span class="arrows_up"></span> -->
                                    </ul>
                                </td>
                            </tr>
                            <tr>
                                <td class="zhaiyao">摘&nbsp;&nbsp;要: </td>
                                <td>
                                    <input class="fukuan-person" type="text" id="summary" maxlength="200">
                                </td>
                            </tr>
                            <tr>
                                <td class="moneyTd">金&nbsp;&nbsp;额: </td>
                                <td>
                                   <select class="select-currency" name="currency" id="currency">
                                                        
                                   </select>
                                   <input class="money-count" type="text" id="money" maxlength="10">
                                   <input type="hidden">
                                </td>
                            </tr>
                            <tr class="last-tr">
                                <td class="payMethod">付款方式&nbsp;&nbsp;: </td>
                                <td class="last-td">
                                    <input class="arrows-low" type="text" id="paymentWay">
                                        <ul class="paymethod-dropdown-box">
                                           <!--  <span class="arrows_up"></span> -->
                                        </ul>
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
         
      </div>
  </body>
</html>
