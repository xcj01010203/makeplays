<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isContractReadonly = false; 
Object hasImportContractAuth = false;
Object hasExportContractAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_FINANCE_CONTRACT) == 1){
        isContractReadonly = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_CONTRACT) != null){
    	hasImportContractAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_CONTRACT) != null){
    	hasExportContractAuth = true;
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
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
        <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/contract/contract.css">
		<link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css" />
		
		<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/finance/contract/contractActor.js"></script>
		<script>
        var isContractReadonly = <%=isContractReadonly%>;
        var hasImportContractAuth = <%=hasImportContractAuth%>;
        var hasExportContractAuth = <%=hasExportContractAuth%>;
    	</script>
  </head>
  
  <body>
    <div class="my-container">
            <div class="tab-body-wrap-actor" id="tabWrapActor">
                <div class="btn_tab_wrap-actor">
                  <!-- tab键空白处 -->
                    <div class="btn_wrap"></div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="contractWorkerTab">职员合同</li>
                            <li id="contractActorTab"  class="tab_li_current">演员合同</li>
                            <li id="contractProduceTab">制作合同</li>
                            <li id="contractToPaidTab">合同批量支付</li>
                        </ul>
                    </div>
                </div>
                
                <div class="contract-public actor-contract-page">
                    <div class="contract-public" id="actorContractList"></div>
                    
                    <div class="actor-contract-total">
                    </div>
                </div>
                
                
            </div>
            
             <!-- 高级搜索 -->
            <div class="my-jqx-window advance-search-win" id="advanceSearchWin">
                <div>高级查询</div>
                <div class="my-window-content" id="dropDownDIV">
                    <ul class="info-list">
                        <li class="select-picker-li">
                          <p>演员姓名:</p>
                          <select id="actorName" class="selectpicker show-tick" multiple data-live-search="true" style="display: none;">
                             
                          </select>
                          <!-- <input type="hidden" class="preValue" /> -->
                          <a style="display:none; float: right; line-height: 30px; margin-right: 5px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                            
                        </li>
                        <li class="select-picker-li">
                            <p>角色名称:</p>
                            <select id="roleName" class="selectpicker show-tick" multiple data-live-search="true" style="display: none;">
                             
                            </select>
                            <a style="display:none; float: right; line-height: 30px; margin-right: 5px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                        </li>
                        <li class="select-picker-li">
                            <p>财务科目:</p>
                            <select id="financeSubjId" class="selectpicker show-tick" multiple data-live-search="true" style="display: none;">
                             
                            </select>
                            <a style="display:none; float: right; line-height: 30px; margin-right: 5px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                        </li>
                        <li class="pay-way-li">
                            <p>支付方式:</p>
                            <label><input type ="radio" name="payWay" value=1 checked>按阶段支付</label>
                            <label><input type="radio" name="payWay" value=2>按月支付</label>
                            <label><input type="radio" name="payWay" value=3>按日支付（每月结算）</label>
                            <label><input type="radio" name="payWay" value=4>按日支付（定期结算）</label>
                        </li>
                        <li class="general-li-texta">
                            <p>支付条件:</p>
                            <textarea id="paymentTerm"></textarea>
                        </li>
                        <li class="general-li-texta">
                            <p>备注:</p>
                            <textarea id="remark"></textarea>
                        </li>
                    </ul>
                    <div class="win-btn-list-div">
                        <input type="button" id="advanceQueryBtn" value="查询"  onclick="advanceQueryActor()">
                        <input type="button" id="closeQueryBtn" value="关闭" onclick="closeQuery()"> 
                        <input type="button" id="clearQueryBtn" value="清空" onclick="clearQuery()">
                    </div>
                </div>
    
            </div>
            
            
            <!-- iframe页面 -->
             <div class="right-popup-win">
                
                <div class="right-popup-body">
    
                  <iframe id="contractDetailIframe" width="100%" height="100%"></iframe>
                </div>
            </div>
     </div>
     
      <!-- 导出演员合同方式窗口 -->
     <div class="hiddenWindow" id='exportContractACtorWindow'>
		<div id="exportContractActorHeader">
			<span> 导出演员合同 </span>
		</div>
		<div id="exportContractActorDiv">
			<div class="exportTvOption">
				<input class="sxport-radio-input" type="radio" name="exportOption" value="1" checked>统计列表
				<input class="export-radio-fen sxport-radio-input" type="radio"	name="exportOption" value="2" >合同详情
			</div>
			<div style="text-align: center;">
				<input class="mybtn export" type="button" id="exportBtn" value="导出" onclick="confirmExportContractActor()">
				<input class="mybtn" type="button" id="cancelExpBtn" value="取消">
			</div>
		</div>
	</div>
     
      <!--导入窗口  -->
	  <div id="importExportActorWin" class="jqx-window">
			<div>导入</div>
				<div>
				    <iframe id="importIframe" width="100%" height="100%"></iframe>
				</div>
	 </div>
     
<!-- 显示正在加载中 -->
<div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
<div id="loadingDiv" class="show-loading-container" style="display: none;">
<div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
</div>
  </body>
</html>
