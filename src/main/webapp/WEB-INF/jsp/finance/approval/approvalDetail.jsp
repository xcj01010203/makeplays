<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

	Object isApprovalReadonly = false; //是否只读
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_APPROVAL)) {
	        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_APPROVAL) == 1){
	        	isApprovalReadonly = true;
	        }
	    }
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title></title>
	  <link rel="stylesheet" type="text/css" href="<%=path%>/css/webuploader.css">
  	<link rel="stylesheet" type="text/css" href="<%=path%>/css/finance/approvalDetail.css">
  	<link rel="stylesheet" type="text/css" href="<%=path%>/css/finance/approvalProgress.css">
  	
  	<script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
  	<script type="text/javascript" src="<%=path%>/js/webuploader/webuploader.min.js"></script>
  	
  	<link rel="stylesheet" href="<%=path%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=path%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
    <script type="text/javascript" src="<%=path%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
  	
  	<script type="text/javascript" src="<%=path%>/js/finance/approvalDetail.js"></script>
  	<script type="text/javascript">
        var currentUserId='${user.userId}';
        //是否只读
        var isApprovalReadonly = <%=isApprovalReadonly%>;
    </script>
</head>
<body>
   <input type="hidden" id="listType" value="${listType}"><!-- 单剧列表类型 -->
   <input type="hidden" id="receiptType" value="${receiptType}"><!-- 单据类型 -->
   <input type="hidden" id="receiptId" value="${receiptId}"><!-- 单据id -->
   <input type="hidden" id="receiptStatus" value="${receiptStatus}"><!-- 单据状态 -->
   
   <div class="ui dimmer body" id="myLoader">
       <div class="ui large text loader">正在上传附件，请稍后...</div>
   </div>
      
	 <div class="detail-container">
	     <input type="hidden" id="type"><!-- 单据类型 -->
	     <input type="hidden" id="receiptNo"><!-- 单据编号 -->
	     <div class="approval-detail-one" id="approValDetailOne">
	         <div class="header-btn-list">
	             <input type="button" id="submitBtn" value="提&nbsp;&nbsp;交" onclick="saveApprovalInfo(this,2)">
	             <input type="button" id="saveBtn" value="保&nbsp;&nbsp;存" onclick="saveApprovalInfo(this,1)">
	             <input type="button" id="withdrawBtn" value="撤&nbsp;&nbsp;回" onclick="widthdrawReceipt()">
	             <input type="button" id="deleteBtn" value="删&nbsp;&nbsp;除" onclick="deleteReceipt()">
	             
	             <input type="button" id="agreenBtn" value="同&nbsp;&nbsp;意" onclick="approvalReceipt(1)">
	             <input type="button" id="refuseBtn" value="拒&nbsp;&nbsp;绝" onclick="approvalReceipt(2)">
	             <input type="button" id="return" value="退&nbsp;&nbsp;回" onclick="approvalReceipt(3)">
	             <input type="button" id="endBtn" value="完&nbsp;&nbsp;结" onclick="approvalReceipt(4)">
	             <input type="button" id="activation" value="激&nbsp;&nbsp;活" onclick="activationReceipt()">
	             
	             <input type="button" id="arrowUp" style="display: inline-block;" class="close-btn" value="收起" onclick="closeApprovalDetail()">
	         </div>
	         <div class="content-list">
	             <!-- 审批进度 -->
		           <div class="approval-progress" id="approvalProgress">
		               <p class="content-tips">审批进度</p>
		               <!-- 审批时间轴 -->
		               <div class="timer-view-container">
		                  <!-- <div class="timer-viwer-div">
		                       <div class="timer-content-div">
		                          <div class="timer-flag green"></div>
		                          <div class="timer-content">
		                            <div class="timer-title">
		                                <span class="name-tips">我(发起)</span>
		                                <span class="date-tips">2017-05-12</span>
		                            </div>
		                            <p class="timer-tips-content">这是第一个</p>
		                          </div>
		                        </div>
		                   </div> -->
		               </div>
		           </div>
		               
		               <p class="content-tips">信息<span></span></p>
		               <ul class="approval-info">
		                  <li>
		                      <table cellspacing="0" cellpadding="0">
		                          <tr>
		                              <td style="width: 50%; text-align: left;">
		                                  <p id="moneyCapition">报销金额:</p>
                                      <input type="text" id="financeMoney" onkeyup="checkNum(this)">
                                      <span style="color: #e48945; font-size: 14px;" id="moneyCurrentCode"></span>
		                              </td>
		                              <td style="width: 50%;">
		                                  <select style="float:right; display: none;" id="moneyCurrentSelect">
                                            
                                      </select>
                                      <p style="float: right; display: none;"><label>币种:<span></span></label></p>
                                  </td>
		                          </tr>
		                      </table>
		                      
		                  </li>
		                  <li>
		                     <p><label>说明:<span></span></label></p> 
		                     <textarea id="capition" placeholder="限200字" maxLength="200"></textarea>
		                  </li>
		                  <li>
		                      <p><label>审批人:<span></span></label></p>
		                      <div class="approval-person" id="approvalPerson" type="text" onclick="showPersonList(event, this)"></div>
		                      <input type="hidden" id="approvalPersonId"><!-- 选择审批人的id -->
                          <div class="drop-down-list" id="dropDownList" onclick="stopPagation(event)">
                              <div class="search-div"><input type="text" class="search-input" onkeyup="checkOutPerson(this,event)"></div>
                              <ul class="drop-person-list" id="dropPersonList"></ul>
                          </div>
		                  </li>
		                  <li>
		                      <p><label>附件:<span></span></label></p>
		                      <div class="select-image-btn" id="uploadImageBtn">选择附件</div>
		                      <ul class="upload-file-list" id="uploadFileList"></ul>
		                  </li>
		                  
		               </ul>
		               
		           
	         </div>
	         
	     </div>
	     
	     <!-- 审批单据页面 -->
	     <div class="approval-detail-two" id="approValDetailTwo">
	         <div class="header-btn-list">
               <input type="button" id="submitBtn" value="提&nbsp;&nbsp;交" onclick="saveApprovalInfo(2)">
               <input type="button" id="saveBtn" value="保&nbsp;&nbsp;存" onclick="saveApprovalInfo(1)">
               <input type="button" id="withdrawBtn" value="撤&nbsp;&nbsp;回" onclick="widthdrawReceipt()">
               <input type="button" id="deleteBtn" value="删&nbsp;&nbsp;除" onclick="deleteReceipt()">
               
               <input type="button" id="addApprover" style="letter-spacing: 0px;" value="确定" onclick="addApproverBtn()">
               
               <input type="button" id="agreenBtn" value="同&nbsp;&nbsp;意" onclick="approvalReceipt(1)">
               <input type="button" id="refuseBtn" value="拒&nbsp;&nbsp;绝" onclick="approvalReceipt(2)">
               <input type="button" id="return" value="退&nbsp;&nbsp;回" onclick="approvalReceipt(3)">
               <input type="button" id="endBtn" value="完&nbsp;&nbsp;结" onclick="approvalReceipt(4)">
               <input type="button" id="activation" value="激&nbsp;&nbsp;活" onclick="activationReceipt()">
               
               <input type="button" id="arrowUp"  style="display: inline-block;" class="close-btn" value="收起" onclick="closeApprovalDetail()">
           </div>
           <div class="content-list">
               <!-- 审批进度 -->
               <div class="approval-progress" id="approvalProgress">
                   <p class="content-tips">审批进度</p>
                   <!-- 审批时间轴 -->
                   <div class="timer-view-container">
                      <div class="timer-viwer-div">
                           <div class="timer-content-div">
                              <div class="timer-flag green"></div>
                              <div class="timer-content">
                                <div class="timer-title">
                                    <span class="name-tips">我(发起)</span>
                                    <span class="date-tips">2017-05-12</span>
                                </div>
                                <p class="timer-tips-content">这是第一个</p>
                              </div>
                            </div>
                       </div>
                   </div>
                   
                   <p class="content-tips">信息</p>
                   <ul class="approval-info">
                      <li>
                          <p><label>单号:<span></span></label></p>
                          <span class="order-num" id="orderNum"></span>
                          <span id="moneyCapition" class="expense-money">报销金额:</span>
                          <span id="expenseMoney"></span>
                      </li>
                      <li>
                         <p id="expenseTips">报销说明:</p>
                         <div class="expense-capition" id="expenseCapition"></div>
                      </li>
                      <li>
                          <p><label>审批人:<span></span></label></p>
                          <div class="approval-person" id="approvalPerson" type="text" onclick="showPersonList(event, this)"></div>
                          <input type="hidden" id="approvalPersonId"><!-- 选择审批人的id -->
                          <input type="hidden" id="newAddApproverName"><!-- 新选择的审批人名称 -->
                          <input type="hidden" id="newAddApproverId"><!-- 新选择的审批人id -->
                          <div class="drop-down-list" id="dropDownList" onclick="stopPagation(event)">
                              <div class="search-div"><input type="text" class="search-input" onkeyup="checkOutPerson(this,event)"></div>
                              <ul class="drop-person-list" id="dropPersonList">
                                  
                              </ul>
                          </div>
                      </li>
                      <li>
                          <p><label>附件:<span></span></label></p>
                          <div class="upload-file-container">
                              <ul class="upload-file-list" id="uploadFileList"></ul>
                          </div>
                      </li>
                      <li>
                          <p>审批意见:</p>
                          <textarea id="approvalOpionion" placeholder="限200字" maxLength="200"></textarea>
                      </li>
                      
                   </ul>
                   
               </div>
           </div>
           
	     </div>
	     
	 </div>
</body>
</html>