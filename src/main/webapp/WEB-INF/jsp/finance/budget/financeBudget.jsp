<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";


Object isBudgetReadonly = false;
Object hasImportFinanceBudgetAuth = false;
Object hasExportFinanceBudgetAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_FINANCE_BUDGET) == 1){
        isBudgetReadonly = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_FINANCE_BUDGET) != null){
    	hasImportFinanceBudgetAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_FINANCE_BUDGET) != null){
    	hasExportFinanceBudgetAuth = true;
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
	
	  <link rel="stylesheet" type="text/css" href="<%=basePath%>css/finance/financeBudgeList.css">
    <link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css">
    
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    
    <link rel="stylesheet" href="<%=request.getContextPath()%>/js/jquery-easyui-1.4.5/themes/default/easyui.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/finance/accountManager.css" type="text/css">
    
    <!-- 实现表格的拖动 -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
	  <script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.easyui.min.js"></script>
	  <script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.draggable.js"></script>
	  <script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/treegrid-dnd.js"></script>
    
    <script type="text/javascript">
        var isBudgetReadonly = <%=isBudgetReadonly %>;
        var hasImportFinanceBudgetAuth = <%=hasImportFinanceBudgetAuth %>;
        var hasExportFinanceBudgetAuth = <%=hasExportFinanceBudgetAuth %>;
        var basePath = "<%=basePath %>";
    </script>
    <script type="text/javascript" src="<%=basePath%>/js/finance/financeBudgetList.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
  </head>
  
  <body>
    <div class="my-container" >
        
            <div id="finaceBudgetList"></div>
        
        
        <!--新建科目窗口  -->
        <div class="jqx-window" id="subjectwin" onclick="hideUnitTypeSelect()">
            <div>新建科目</div>
            <div class="subjectwin-container">
                <ul>
                    <li>
                        <!-- hidden框 -->
                        <input type="hidden" id="financeSubjId">
                        <input type="hidden" id="financeSubjParentId">
                        <input type="hidden" id="level">
                        <p>财务科目:</p>
                        <input type="text" id="financeSubjName">
                        <span class="finance-subject-tips">科目名称不能为空</span>
                    </li>
                    <li class="li-amount-unit">
                        <p>数量:</p>
                        <input class='subject-amount' type="text" id="amount" placeholder="0" maxlength="5">
                        <p>单位:</p>
                         <input class='subject-unitType-input' type="text" id="unitType" placeholder="请填写单位" maxlength="10" onclick="showSelect(event)">
                      		<div class='unitType-main-div' id='unitTypeMainDiv' style="display: none;">
                      		
                      		</div>
                        <p>币种:</p>
                        <select class='currency-type' id="currentId">
                            
                        </select>
                    </li>
                    <li class="li-subject-price">
                        <p>单价:</p>
                        <input class='subject-price' type="text" id="perPrice" placeholder="0" maxlength="15">
                    </li>
                    <li class="li-finance-money">
                        <p>预算金额:</p>
                        <input class='finance-money' type="text" id="money" onkeyup="onlyNum(this)">
                    </li>
                    <li>
                        <p>备注:</p>
                        <textarea rows="3" Maxlength="490" cols="65" name="remark" id="remark"></textarea>
                    </li> 
                </ul>
                 <div class="win-btn-list-div">
                            <input type="button" id="subject-sure" value="确定" onclick="saveBudgetInfo()">
                            <input type="button" value="取消" onclick="cancelBudgetInfo()">
                 </div>
                
            </div>
        </div>
        
       <!--  调整顺序弹出窗  -->
        <div class="jqx-window" id="sortWindow">
            <div class="sort-win-title">调整预算表顺序</div>
            <div class="sort-win-container">
                <div class="sort-win-div-tips"><span class="sort-win-span-point">·</span>&nbsp;&nbsp;<span class="sort-win-span-content">拖动只能在同级之间进行。</span></div>
                <div id="sortGrid"></div>
                <div class="close-sort-win"><input class="close-sort-btn" type="button" value="关闭" id="closeSortWinBtn" onclick="closeSortListWin()"></div>
            </div>
        </div>
        
        
       
        <!-- 导出 -->
        <div id="loadingDiv" class="show-loading-container">
            <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
        </div>
        
        
        <!--导入窗口  -->
		<div id="importBudgetWin" class="jqx-window">
			<div>导入</div>
			<div>
			   <iframe id="importIframe" width="100%" height="100%"></iframe>
			</div>
		</div>
        
    </div>
		
	
  </body>
</html>
