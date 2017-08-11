<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object isContractReadonly = false; 
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    
    if((Integer)authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_FINANCE_CONTRACT) == 1){
        isContractReadonly = true;
    }
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<base href="<%=basePath%>">
	<meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/contract/contractToPaid.css">
		
		<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.selection.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/finance/contract/contractToPaid.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
	
	<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
    
    <script type="text/javascript" src="<%=basePath%>/js/interactiveFrame.js"></script>
		<script>
	        var isContractReadonly = <%=isContractReadonly%>;
	        var isRunningAccountReadonly = <%=isContractReadonly%>;
	    </script>
</head>
<body>
     <div class="my-container">
        <div class="tab-body-wrap-actor" id="tabWrapActor">
                <div class="btn_tab_wrap-paid">
                  <!-- tab键空白处 -->
                    <div class="btn_wrap"></div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="contractWorkerTab">职员合同</li>
                            <li id="contractActorTab">演员合同</li>
                            <li id="contractProduceTab">制作合同</li>
                            <li class="tab_li_current" id="contractToPaidTab">合同批量支付</li>
                        </ul>
                    </div>
                </div>
                
                
                
        </div>
        <div class="contract-public paid-order-page">
            <div class="paid-order-div" id="paidOrderList"></div>
            <!-- 状态栏 -->
		        <div class="state-div" id="stateDiv">
		            <span class="state-explain-color">*</span><span>勾选未付单生成待付单，勾选待付单生成已付单</span>
		        </div>
        </div>
        
        
        
        
        <!-- 筛选窗口 -->
        <div class="my-jqx-window" id="ScreenWin">
            <div>筛选</div>
            <div class="my-screen-content">
                <div class="screen-list">
                    <ul>
                        <li>
                            <p>合同类型:</p>
                            <label style='margin-top:3px;'><input type="checkbox" name="contractType" value="1">职员合同</label>
                            <label><input type="checkbox" name="contractType" value="2">演员合同</label>
                            <label><input type="checkbox" name="contractType" value="3">制作合同</label>
                        </li>
		                    <li>
		                        <p>待付日期:</p>
		                        <input class="start-date" id="startDate" type="text" onfocus="WdatePicker({isShowClear:true,readOnly:true,maxDate: '#F{$dp.$D(\'endDate\')}'})"> 
		                        <div class="line-div"></div> 
		                        <input class="end-date" id="endDate" type="text" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
		                    </li>
		                    <li>
		                        <p class="p-letter-spacing">合&nbsp;同&nbsp;方:</p>
		                        <!-- <input type="text" id='contractName' /> -->
		                        <select class="selectpicker contract-name-select" id="contractName"  multiple data-live-search="true" style="display: none;">
                             
                            </select>
                            <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a> 
		                    </li>
		                    <li>
		                        <p>科&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目:</p>
		                        <input class="subj-name-select" type="text" id='subjectName' />
		                        <!-- <select class="selectpicker subj-name-select" id="paidSubjectName"  multiple data-live-search="true" style="display: none;">
                             
                            </select>
                            <a style="display:none; float: right; line-height: 30px; margin-right: 0px; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection" onclick="clearSelection(this)">清空</a> -->
                            <div id="levelPopupSearch" class="fin_subj">
                                      <div class="filter-con">
                                        <input class="filter-input" type = "text" id = "filterSearch"> 
                                        <div class="filter-btn" id="filterSearchBtn"><img src="<%=request.getContextPath()%>/css/finance/image/search.png"/></div>
                                      </div>
                                      <div id="subjectTreeSearch"></div>    
                             </div>
		                    </li>
		                    <li>
		                        <p>状&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;态:</p>
		                        <label style='margin-top:3px;'><input type="checkbox" name="contractStatus" value="0">未付</label>
		                        <label><input type="checkbox" name="contractStatus" value="1">已生成待付单</label>
		                        <!-- <label><input type="checkbox" name="contractStatus" value="2">已生成付款单</label>
		                        <label><input type="checkbox" name="contractStatus" value="3">已结算</label> -->
		                    </li>
                    </ul>
                </div>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick="searchContractToPaidInfoList()">
                    <input type="button" value="取消" onclick="cancelScrren()">
                    <input type="button" value="清空" onclick="clearScreen()">
                </div>
            </div>
        </div>
        
        
        <!-- 制作报销单窗口 -->
        <div class="jqx-window" id="makeReimbursementWin">
            <div>制作实支报销单</div>
            <div class="my-reimbursement-content">
                <div class="left-div-modular" id="leftDivModular">
                    <ul class="personnel-list" id="personnelList">
                    </ul>
                </div>
                <div class="right-div-modular">
                    <dl class="make-content-dl">
                        <dt>
                            <h2>制作实支报销单</h2>
                            <div class="description-info">
                                <table class="basic-info-table">
                                    <tr>
                                        <td></td>
                                        <td>
                                            <p>No.(凭单号)&nbsp;:</p>
                                            <div class="voucher-no"  id=""></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>项目/剧组:</p>
                                            <div class="pro-crew-name" id="currentCrewName"></div>
                                        </td>
                                        <td>
                                            <p>Date(日期)&nbsp;:</p>
                                            <!-- <input class="bill-date" type="text" id="" onclick="WdatePicker({isShowClear:false,readOnly:true})" readonly> -->
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </dt>
                        <dd>
                            <table class="detail-info-table-one" cellspacing=0 cellpadding=0 id="detailInfoTableOne">
                                <thead>
                                    <tr>
                                        <td>日期(date)</td>
                                        <td>编号(No.)</td>
                                        <td style="width: 227px;">Particulars<br>用途说明</td>
                                        <td>A/C code<br>科目</td>
                                        <td style="width: 256px;">Analysis code<br>明细科目</td>
                                        <td>Amount(RMB)<br>金额(人民币)</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><input class="amount-input" type="text" onblur="changeMoney(this);" onfocus="modifyMoney(this);" id="payTotalMoney"></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="money-tr">
                                        <td colspan="4">
                                            <p>大写金额:</p>
                                            <div id="capitalPayMoney"></div>
                                        </td>
                                        <td colspan="2">
                                            <p>总额(Total):</p>
                                            <div class="read-only-money" id="readOnlyMoney"></div>
                                            <input type="hidden">
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </dd>
                        <dd>
                            <table class="readonly-explain" cellspacing=0 cellpadding=0>
                                <tr>
                                    <td style="width: 400px;">Applicant/Date<br>报销者/日期</td>
                                    <td>Position/Dept<br>职务/部门</td>
                                    <td rowspan="3" style="width: 180px;">Received by<br>签收</td>
                                </tr>
                                <tr>
                                    <td>line Producer/Date<br>监制/日期</td>
                                    <td>Prod Manager/Date<br>制片主任</td>
                                </tr>
                                <tr>
                                    <td>Producer/Date<br>制片人/日期</td>
                                    <td>
                                      <p>Producer Manager/Date<br>会计出纳/日期 &nbsp;&nbsp;<span id="userAgent">当前用户名</span></p>
                                    </td>
                                </tr>
                            </table>
                        </dd>
                        <dd class="last-dd-pack">
                            <div class="last-tips">Accounts Department
                                
                            </div>
                        </dd>
                        <p class="pack-count">附件:&nbsp; <input type="text">张</p>
                    </dl>
                </div>
                <div class="win-btn-list">
                    <input class="all-print-btn" type="button" value="全部打印" onclick="printAllPaidOrder()">
                    <input class="select-print-btn" type="button" value="打印当前单据" onclick="printPaidOrder()">
                    <input class="prev-btn" type="button" value="上一张" onclick="prevOrder()">
                    <input class="next-btn" type="button" value="下一张" onclick="nextOrder()">
                </div>                
            </div>
        </div>
        
        <div class="jqx-window" id="realPaymentWin">
            <div>实付清单</div>
            <div class='my-realPaywin-content'>
                <div class="left-div-modular-payment" id="leftModularPayment">
                    <ul class="personnel-list" id="paymentPersonnelList">
                    </ul>
                </div>
                <div class="right-div-modular-payment" id="rightModularPayment">
                    <iframe class="full-receipt-div" name="fullReceiptDiv" id="fullReceiptDiv" scrolling="auto" width="100%" height="100%"></iframe>
                </div>
                
                <div class="win-btn-list input-margin">
                    <input type="button" value="上一张" onclick="prevPaymentOrder()">
                    <input type="button" value="保存" id="saveModifyPaymentBtn" onclick="saveModifyPaymentOrder()">
                    <input type="button" value="下一张" onclick="nextPaymentOrder()">
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