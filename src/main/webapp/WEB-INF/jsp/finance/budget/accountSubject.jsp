<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object isBudgetReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_FINANCE_BUDGET) == 1){
        isBudgetReadonly = true;
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
	  
	  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css" type="text/css" />
	
	  <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-easyui-1.4.5/themes/default/easyui.css" type="text/css" />
	  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/finance/accountManager.css" type="text/css"></link>
	    
	  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui/jquery-ui.js"></script>
	  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-easyui-1.4.5/jquery.easyui.min.js"></script>
	  <script type="text/javascript" src="<%=basePath%>/js/finance/accountSubject.js"></script>
	  <script type="text/javascript">
        var isBudgetReadonly = <%=isBudgetReadonly %>;
	</script>
  </head>
  
  <body>
      <div class="my-containter">
      <input type="hidden" id="selectedAccId">
      <input type="hidden" id="selectedAccName">
      <input type="hidden" id="selectedAccCode">
          <div class="account-list">
              <div class="tool-bar">
                  <span title="返回" class="btn-back" onclick="back()"></span>
                  <span title="添加" class="btn-add" onclick="addAcc()"></span>
              </div>
              <div class="account-table-div">
              <table class="account-table-head" id="accountTableHead">
                  <tr>
                        <td class="acc-num-td"><p>会计科目号码</p></td>
                      <td class="acc-name-td"><p>会计科目名称</p></td>
                      <td class="budget-names-td"><p>财务科目</p></td>
                  </tr>
              </table>
              <div class="account-table-body">
                  <table class="account-table-body" id="accountTableBody">
                  <!-- <tr title="单击管理财务科目">
                      <td class="acc-name-td" onmouseover="showOperBtn(this)" onmouseout="hideOperBtn(this)">剧本
                          <span class="col-operate-btn delete" onclick="deleteAcc(this)"></span>
                          <span class="col-operate-btn modify" onclick="modifyAcc(this)"></span>
                      </td>
                      <td class="acc-num-td">500101</td>
                      <td class="budget-names-td"><p>机票，住宿</p></td>
                  </tr> -->
                  </table>
                  <div class="empty-div">暂无数据</div>
              </div>
              </div>
          </div>
          <div class="direction"></div>
          <div class="budget-list">
              <div id="budgetSubject"></div>
          </div>
        
          <div class="account-detail" id="accountDetailWin" onkeyup="detailWinClick()">
              <div>会计科目</div>
              <div>
                  <ul>
                      <li class="error-li">
                          <p></p>
                          <input type="hidden" id="accountId">
                          <label class="error-message"></label>
                      </li>
                      <li>
                          <p>代码：</p>
                          <input type="text" id="code">
                          <label class="necessory">*必填</label>
                      </li>
                      <li>
                          <p>名称：</p>
		                      <input type="text" id="name">
		                      <label class="necessory">*必填</label>
		                  </li>
		                </ul>
		                <div class="operate-btn">
			                    <input type="button" value="保存" onclick="saveAccountInfo()">
			                    <input type="button" value="取消" onclick="closeDetailWin()">
		                </div>
                </div>
            </div>
        </div>
  </body>
</html>
