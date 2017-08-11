<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/getcost/collectionPrint.css">
		<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/getcost/collectionPrint.js"></script>
  </head>
  
  <body>
      <input type="hidden" id="collectionIds" value="${collectionIds}">
      <input type="hidden" id="needClosePage" value="${needClosePage}">
      <input type="hidden" id="needBacktoPage" value="${needBacktoPage}">
      <input type="hidden" id="collectionId">
      <div class="collection-order-box" id="loadCollectionBox">
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
                        <input class="header-input" type="text" id="billsDateTime">
                    </div>
                    <div class="div-clear"></div>
                </div>
            </dt>
            <dd>
                <div class="collection-header">
                    <table width="100%" class="collection-table" cellspacing=0 cellpadding=0>
                        <tbody>
                            <tr>
                                <td class="fukuan-td">付款人(单位)&nbsp;&nbsp;: </td>
                                <td colspan=2>
                                    <input class="fukuan-person" type="text" id="payMoneyParty">
                                    
                                </td>
                            </tr>
                            <tr>
                                <td class="zhaiyao">摘&nbsp;&nbsp;要: </td>
                                <td colspan=2>
                                    <input class="fukuan-person" type="text" id="summary">
                                </td>
                            </tr>
                            <tr>
                                <td class="moneyTd">金&nbsp;&nbsp;额: </td>
                                <td class="money-detail">
                                   <input class="select-currency" type="text" name="currency" id="currency">
                                </td> 
                                <td>  
                                   <input class="money-count" type="text" id="money">
                                </td>
                            </tr>
                            <tr class="last-tr">
                                <td class="payMethod">付款方式&nbsp;&nbsp;: </td>
                                <td class="last-td" colspan =2>
                                    <input class="arrows-low" type="text" id="paymentWay">
                                        
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
