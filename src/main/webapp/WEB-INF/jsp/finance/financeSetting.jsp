<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isFinanceSetReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.FINANCE_SET)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.FINANCE_SET) == 1){
        	isFinanceSetReadonly = true;
        }
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
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/financeSetting.css">
    	<link rel="stylesheet" href="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
    	<script type="text/javascript" src="<%=basePath%>/js/finance/financeSetting.js"></script>
    	<script src="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.js"></script>
    	
        <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
        <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
        
    	<script>
		    var isFinanceSetReadonly = <%=isFinanceSetReadonly%>;
		</script>
  </head>
  
  <body>
  <input type="hidden" id="activeTagType" value="${activeTagType }">
    <div class="my-container">
        <!-- 左边菜单 -->
        <div style="" class="setting-left">
	          <div class="setting-left-header">
	            <span>财务设置</span>
	          </div>
			      <ul>
			        <li class="setting-li-current currency-set-tab">币种设置</li>
			        <li class="receipts-set-tab">单据设置</li>
			        <li class="pass-set-tab">安全设置</li>
			        <li class="tax-set-tab">付款单缴税设置</li>
                    <li class="other-set-tab">其他设置</li>
			      </ul>           
        </div> 
        
        <!-- 右边内容 -->
        <div class="setting-right">
           
            <span class="current-menu-name">币种设置</span>
            <!-- 币种设置 -->
            <div class="finance-show-con" id="financeShowCon">
                <div id="financeShow"></div>
            </div>
            
            <!-- 单据设置 -->
            <div class="finance-set-content" id="financeSettingContent">
                <ul>
                    <li>
                        <p>付款单有票无票是否单独编号:</p>
                        <input class="has-receipt" type="radio" name="hasReceiptStatus" value="true">是
                        <input type="radio" name="hasReceiptStatus" value="false">否
                    </li>
                    <li>
                        <p>单据是否按月编号:</p>
                        <input type="radio" name="payStatus" value="true">是
                        <input type="radio" name="payStatus" value="false">否               
                    </li>
                </ul>
                <div class="win-btn-list-div">
                        <input type="button" id="billSetBtn" value="确定"  onclick="saveBillSettingInfo()">
                </div>
            </div>
            
            <!-- 财务密码 -->
            <div  id="financePassWord">
	            <div class="finance-pass">
	            	<h2>财务密码</h2>
	                 <ul>
	                    <li>
	                    	<div class="ui toggle checkbox user-status">
								<input id="setFinancePass" type="checkbox">
								<label>是否启用财务密码功能</label>
		                    </div>
	                    </li>
	                 </ul>
	                 
	                 <div class="set-new-pass-info">
	                    <ul>
	                        <li>
	                            <p><span>*</span>请输入密码:</p>
	                            <input class="new-password" type="password"  placeholder="请输入密码">
	                        </li>
	                        <span class="finance-new-pass-tips">密码不能为空</span>
	                        <li>
	                            <p><span>*</span>请再次输入密码:</p>
	                            <input class="repeat-password" type="password"  placeholder="请再次输入密码">
	                        </li>
	                        <span class="finance-repeat-pass-tips">确认密码不能为空</span>
	                    </ul>
	                    <div class="win-btn-list-div">
	                        <input type="button" value="确定" onclick="saveNewPassInfo()">
	                    </div>
	                 </div>
	                 
	                 <div class="set-old-pass-info">
	                    <!-- <ul>
		                    <li>
		                        <p>是否启用财务密码功能:</p>
		                        <input class="set-finance-pass" type="checkbox" value="true">
		                    </li>
	                    </ul> -->
	                    
	                    <ul>
	                        <li>
	                            <p><span>*</span>请输入旧密码:</p>
	                            <input class="modify-old-pass" type="password" placeholder="请输入旧密码">
	                        </li>
	                        <span class="finance-old-pass-tips">旧密码不能为空</span>
	                    </ul>
	                    
	                    <ul class="set-old-pass-info-chec">
	                        <li>
	                            <p><span>*</span>请输入新密码:</p>
	                            <input class="modify-new-pass" type="password"  placeholder="请输入新密码">
	                        </li>
	                        <span class="finance-modify-pass-tips">新密码不能为空</span>
	                        <li>
	                            <p><span>*</span>请再次输入密码:</p>
	                            <input class="modify-repeat-pass" type="password"  placeholder="请再次输入密码">
	                        </li>
	                        <span class="modify-repeat-pass-tips">确认密码不能为空</span>
	                    </ul>
	                    <div class="win-btn-list-div">
	                        <input type="button" value="确定" onclick="saveNewPassInfoToo()">
	                    </div>
	                    
	                 </div>
	      		</div>
	        	<div class="finance-pass">
	            	<h2>手机验证</h2>
					<ul>
	                    <li>
	                        <div class="ui toggle checkbox user-status">
								<input id="setValidUserIp" type="checkbox" onclick="setValidUserIpFunc(this)">
								<label>是否根据用户IP地址变化验证用户手机号</label>
		                    </div>
	                    </li>
					</ul>                 
	            </div>
	        </div>
	        
	        <!-- 付款单缴税设置 -->
            <div class="tax-set-content" id="taxSettingContent">
                <ul>
                    <li>
                        <p>税务科目</p>
                        <input id ="taxFinanSubjName" type="text" onclick="showSelectFinanSubjDiv(this, event)">
                        <input id = "taxFinanSubjId" type="hidden">
                        <a class="clear-finance" href="javascript:void(0);" id="clearFinanceSubj" onclick="clearFinanceSubj()">清空</a>
                    </li>
                    <li>
                        <p>税率</p>
                        <input id="taxRate" type="text" onkeyup="checkIsNumber(this)">
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
                <div class="win-btn-list-div">
                    <input type="button" value="确定"  onclick="setTaxInfo()">
                </div>
            </div>
	        
	        <div class="other-setting-div" id="otherSetting">
	           <div>
		           <label>月薪拆分到日计算规则</label>
		           <label><input type="radio" name="monthDayType" id="natureDayInput" value=1>自然月实际天数拆分</label>
	               <label><input type="radio" name="monthDayType" id="thirtyDayInput" value=2>统一按每月30天拆分</label>
               </div>
               <div>
                   <label>合同支付提前提醒天数</label>
                   <input type="text" id="contractAdvanceRemindDays" onkeyup="checkIsNumber(this)"/> 
               </div>
               <div class="win-btn-list-div">
                   <input type="button" value="保存" onclick="saveOtherSetting()"/> 
               </div>
	        </div>    
        </div>
        
        
       <!-- 添加窗口 -->
       <div class="jqx-window" id="addCurrencyWin">
          <div>添加币种</div>
          <div class="add-currency-con">
               <ul>
                    <li>
                        <!-- hidden框 -->
                        <input type="hidden" id="id">
                        <p>名称:</p>
                        <input type="text" id="name" title="请填写此字段">
                        <ul class="dropdown_box">
                               <span class="arrows_up"></span>
                        </ul>
                        <span class="currency-name-error-tips">币种名称不能为空</span>
                    </li>
                    <li>
                        <p>编码:</p>
                        <input class='currency-code' type="text" id="code" title="请填写此字段">
                        <span class="currency-code-error-tips">编码不能为空</span>
                    </li>
                    <li>
                        <p>本位币:</p>
                        <select class='standard-type' id="ifStandard">
                            
                            <option value="true">是</option>
                            <option value="false">否</option>
                        </select>
                     </li>
                     <li>
                        <p>汇率:</p>
                        <input class='exchange-rate' type="text" id="exchangeRate" title="请填写此字段">
                        <span class="currency-rate-error-tips">必须是整数或小数</span>
                    </li>
                    <li>
                        <p>状态:</p>
                        <select class='enable' id="ifEnable">
                            
                            <option value="true">启用</option>
                            <option value="false">禁用</option>
                        </select>
                    </li>
                     
                </ul>
                 <div class="win-btn-list-div">
                            <input type="button" value="确定" onclick="savecurrencyInfo()">
                 </div>
          </div>
       </div>
        
    </div>
  </body>
</html>
