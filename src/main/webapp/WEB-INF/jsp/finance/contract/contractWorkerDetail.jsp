<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	    <base href="<%=basePath%>">
	    
	    <title></title>
	    
			<meta http-equiv="pragma" content="no-cache">
			<meta http-equiv="cache-control" content="no-cache">
			<meta http-equiv="expires" content="0">    
			<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
			<meta http-equiv="description" content="This is my page">
			
			<%-- <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/styles.css"> --%>
			<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
		  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
      <%-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css"> --%>
      
			<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/contract/workerDetail.css">
      <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
      <link rel="stylesheet" href="<%=path%>/css/common/public.css" type="text/css" />
      <link rel="stylesheet" href="<%=path%>/css/exportLoading.css" type="text/css" />
	    
	    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
	   
	    
	    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxcore.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdata.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxbuttons.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxscrollbar.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdatatable.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcheckbox.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxlistbox.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxdropdownlist.js"></script>
	    
	    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxtreegrid.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.selection.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.edit.js"></script> 
		<script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxgrid.aggregates.js"></script> 
	  
	    
	    <%-- <script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script> --%>
	    
      
      
      <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
      <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script>
      
	    <script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
        <script type="text/javascript" src="<%=basePath%>/js/dateUtils.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
	    <script type="text/javascript" src="<%=basePath%>/js/finance/contract/contractWorkerDetail.js"></script>
	    
	    
        <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
        <link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
        <script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
        
	    <script>
         var isContractReadonly = <%=isContractReadonly%>;
      </script>
  </head>
  
  <body>
      <div class="ui dimmer body" id="myLoader">
           <div class="ui large text loader">正在上传附件，请稍后...</div>
      </div>
      <div class="my-container">
          <input type="hidden" id="sonAttpackId">
          <div class="right-popup-header">
                  <table cellspacing=0 cellpadding=0>
                    <tr>
                      <td class="popup-title">
                        <span class="right-popup-title">新增职员合同</span>
                      <td>
                      <td class="popup-btn-con">
                        <div class="popup-btn-list">
                            <input class="right-popup-save"  type="button" value='保存' onclick="saveWorkerContractInfo();">
		                        <input class="right-popup-delete" type="button" value="删除" onclick="deleteWorkerContractInfo();">
		                        <input class="right-popup-close" type="button" value='关闭' onclick="closeRightPopup();">
                        </div>
                        
                      <td>
                    </tr>
                  </table>
          </div>
          <div class="worker-detail-main" id="workerDetailMain">
              <div class="header-height">
                  <input type="hidden" id="contractId" value="${contractId }">
                  <input type="hidden" id="readonly" value="${readonly }">
                  <input type="hidden" id="contractNo">
                
                  <table class="worker-basic-info">
                      <tbody>
                          <tr class="info-title tr-height">
                              <td colspan="3" class="td-height">基本内容:</td>
                          </tr>
                          <tr class="tr-height">
                              <th><span class="must-info">*</span>姓名 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonWorkerName" onclick= "selectWorkerName(this)" onblur="getContactDuty(this)">
                                  <ul class="dropdown_box">
                                      <span class="arrows_up"></span>
                                  </ul>
                                  <br>
                                  <span class="name-error-tips">姓名不能为空</span>
                                  
                              </td>
                              <th class="second-th">部门职务 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonDepartment">
                              </td>
                          </tr>
                          
                          <tr class="finan-sub-tr">
                              <th>联系电话 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonPhone"><br>
                              </td>
                              <th  class="second-th">财务科目: </th>
                              <td class="td-height finan-td">
                                  <input type="text" id="sonFinanceSubjName">
                                  <input type="hidden" id="sonFinanceSubjId">
                                  <a class="clear-finance" href="javascript:void(0);" id="clearFinanceSubj" onclick="clearFinanceSubj()">清空</a>
                                  <!-- 选择财务科目 -->
                                  <div id="levelPopup" class="fin_subj">
                                      <div class="filter-con">
                                        <input class="filter-input" type = "text" id = "filter">
                                        <div class="filter-btn" id="filterBtn"><img src="<%=request.getContextPath()%>/css/finance/image/search.png"/></div>
                                      </div>
                                      <div id="subjectTree"></div>    
                                  </div>
                                  
                              </td>
                              
                          </tr>
                          
                          <tr class="tr-height">
                              <th><span class="must-info">*</span>币种:</th>
                              <td class="td-height">
                                  <select class="currency-kind" id="sonCurrencyId">
                                      
                                  </select><br>
                                  <span class="currency-error-tips">币种不能为空</span>
                              </td>
                              <th class="second-th"><span class="must-info">*</span>总金额 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonTotalMoney" onchange="changePaymentMoney(this)" maxlength="10">
                                  <input type="hidden">
                                  <br>
                                  <span class="total-error-tips">总金额不能为空</span>
                              </td>
                          </tr>
                          <tr class="tr-height">
                              <!-- <th><span class="must-info">*</span>联系电话 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonPhone"><br>
                                  <span class="phone-error-tips">联系电话不能为空</span>
                              </td> -->
                              <th>证件类型 : </th>
                              <td class="td-height">
                                  <select class="card-type" id="identityCardType">
                                      <option value = 1 checked>身份证</option>
                                      <option value = 2>护照</option>
                                      <option value = 3>台胞证</option>
                                      <option value = 4>军官证</option>
                                      <option value = 5>其他</option>
                                  </select>
                              </td>
                              <th class="second-th">证件号码:</th>
                              <td class="td-height">
                                  <input type="text" id="identityCardNumber" maxlength="18">
                              </td>
                          </tr>
                          <tr class="tr-height">
                              <th>在组日期 : </th>
                              <td class="td-height">
                                  <input class="enter-date" type="text" id="sonEnterDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})" readonly>到
                                  <input class="leave-date" type="text" id="sonLeaveDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})" readonly><br>
                                  <span class="el-date-error-tips">日期不能为空</span>
                              </td>
                              <th class="second-th"><span class="must-info">*</span>合同签署日期 : </th>
                              <td class="td-height">
                                  <input type="text" id="sonContractDate" onfocus="WdatePicker({isShowClear:false,readOnly:true})" readonly><br>
                                  <span class="contract-date-error-tips">合同日期不能为空</span>
                              </td>
                          </tr>
                      </tbody>
                  </table>
                  <!-- 账户信息 -->
                  <table class="worker-account-info">
                      <tbody>
                          <tr class="info-title">
                              <td colspan="3">乙方银行账户 : </td>
                          </tr>
                          <tr>
                              <th>银行名称: </th>
                              <td>
                                  <input type="text" id="sonBankName">
                              </td>
                              <th class="second-th">账户名称 : </th>
                              <td>
                                  <input type="text" id="sonBankAccountName">
                              </td>
                             
                          </tr>
                          <tr>
                              <th>账号: </th>
                              <td>
                                  <input type="text" id="sonBankAccountNumber">
                              </td>
                          </tr>
                      </tbody>
                  </table>
                  <!-- 支付方式 -->
                  <table class="worker-pay-method">
                      <tbody>
                          <tr>
                              <td class="pay-method-title">支付方式 : </td>
                              <td class="pay-method-radio">
                                  <label class="radio-label"><input type="radio" name="payMethod" value=1>按阶段支付</label>
                                  <label class="radio-label"><input type="radio" name="payMethod"  value=2>按月支付</label>
                                  <label class="radio-label"><input type="radio" name="payMethod"  value=3>按日支付（每月结算）</label>
                                  <label class="radio-label"><input type="radio" name="payMethod"  value=4>按日支付（定期结算）</label>
                                  <p>&nbsp;&nbsp;注：“月薪拆分到日计算规则”和“合同支付提前提醒天数”请到财务设置-其他设置中进行调整</p>
                              </td>
                          </tr>
                          <!-- <tr>
                              <td class="pay-method-title"></td>
                              <td class="pay-method-title">注：按月支付和按日支付请确认薪酬明细后再保存合同</td>
                          </tr> -->
                          <tr>
                              <!-- <td class="pay-method-title">
                                  <span title="提醒时间" class="remind-time"></span>
                              </td> -->
                              <td colspan="2">
                                  <table class="stage-or-month-table" id="byMonthOrByStage">
                                      <tbody>
                                          <tr class="byStageTr">
                                              <td class="symbol-td">
                                                  <div class="increase-reduce-div">
                                                  <!-- 增加、减少日期 -->
                                                      <div class="addition">
                                                          
                                                      </div>
                                                      <div class="subtraction">
                                                      
                                                      </div>
                                                      
                                                  </div>
                                              </td>
                                              <!-- <td>
                                              
                                                  <img alt="" src="/images/timer_1.png" title="提醒时间" class="date-select time-pic" onClick="WdatePicker({el:'dataContainer-1',onpicked:pickedFunc})">
                                                  <input type="hidden" id="dataContainer-1"> 
                                                  
                                              </td> -->
                                              
                                              <td  class="payment-td">
                                                <span>第</span><span class="spanClass" id="1">1</span><span>期: <input class="stage-info" type="text" maxlength="180"  title="">,甲方向乙方支付酬金总额的<input class="stage-money-part" type="text" maxlength="10" onblur="percentCalculation(this)">%,即<input type="text" maxlength="10" onkeyup="" onblur="" class="stage-money">元整。</span>     
                                                <span class="remind-tips">预计付款日期:</span>
                                                <input class="date-select remind-input" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">
                                              </td>
                                              <!-- <td class="remind-time-td">
                                                  <span class="remind-tips">预计付款日期:</span>
                                                  <input class="date-select remind-input" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">
                                              </td> -->
                                          </tr>
                                      </tbody>
                                  </table>
                              </td>
                              <!-- <td></td> -->
                          </tr>
                          <tr class="month-pay-detail-tr">
                            <td></td>
                            <td>
                                <p class="paydetail-remark" id="payDetailRemark">&nbsp;&nbsp;注：“按月支付”和“按日支付”请先计算金额明细并确认无误后再保存合同</p>
                                <input class="input-orange" type="button" value="计算薪酬明细" onclick="calculateMonthPayDetail()" />
                            </td>
                          </tr>
                          <tr class="month-pay-detail-tr">
                            <td></td>
                            <td>
                                <div id="monthPayDetailGrid"></div>
                            </td>
                          </tr>
                      </tbody>
                  </table>
                  <table class="remark-table">
                      <tbody>
                          <tr class="info-title">
                              <td>备注 : </td> 
                          </tr>
                          <tr>
                               
                              <td class="remark-td">
                                  <textarea id="sonRemark" rows="1"></textarea>
                              </td>
                          </tr>
                      </tbody>
                  </table>
                  <table class="sao-miao-table">
                      <tbody>
                          <tr>
                              <td class="saomiao">合同电子版/扫描件 : </td>
                              <td>
                                  <div class="upload-file-btn" id="uploadFileBtn">上传</div><!-- &nbsp;&nbsp;仅支持图片、word、pdf文件 -->
                                  <p class="upload-file-capilation">仅支持图片、word、pdf文件</p>
                              </td>
                          </tr>
                          <tr>
                              <td></td>
                              <td class="upload-file-show-name">
                                  <ul id='showFileRealNameAndSaveId'>
                                      
                                  </ul>
                              </td>
                          </tr>
                      </tbody>
                  </table>
                  
                  
                  
                  
                   <!-- 支付明细 -->
                  
                  <table class="payment-detail-table" id="paymentDetailTable">
                      <tbody>
                          <tr class="info-title">
                              <td>支付明细 : </td> 
                          </tr>
                          <tr>
                              <!-- <td></td>  -->
                              <td class="payment-detail-td" id="paymentDetailListTd">
                                  
                              </td>
                          </tr>
                      </tbody>
                  </table>
                  
                  
                  
              </div>
          </div>
          
      </div>
  </body>
</html>
