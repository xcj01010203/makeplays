<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();

    Object isReadonly = false;
    Object obj = session.getAttribute("userAuthMap");
    
    if(obj!=null){
        java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
        if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CATER)) {
            if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CATER) == 1){
                isReadonly = true;
            }
        }
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=path%>/css/caterInfo/caterInfo.css"/>
		<script type="text/javascript" src="<%=path%>/js/caterinfo/caterinfo.js"></script>
		<script type="text/javascript" src="<%=path%>/js/caterinfo/caterPopupWindow.js"></script>
		<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
		<script>
			var isReadonly = <%=isReadonly %>;
		</script>
	</head>
	<body>
		<div class="my-container">
			<!-- 餐饮管理主列表 -->
			<div id="caterInfoList" class="caterInfoList"></div>
			<!-- 添加/修改窗口 -->
			<div class="right-popup-win" id="rightPopUpWin">
				<div class="right-popup-body">
					<div class="right-popup-head">
						<input type="button" name="save" id="right-save-btn" class="right-save-btn" value="保 存" onclick="saveOneCaterInfo()"/>
						<input type="button" name="save" id="right-delete-btn" class="right-delete-btn" value="删 除" onclick="delCaterInfo()"/>
						<input type="button" name="save" id="right-close-btn" class="right-close-btn" value="关 闭" onclick="closeRightWindow()" />
					</div>
					<div class="right-popup-cont">
						<div class="date-month-budget">
							<span class="right-popup-cont-date">日&nbsp;&nbsp;&nbsp;&nbsp;期<span class="right-must-write">*</span>：</span>
							<span>
								<input class="right-popup-date-input" id="dateinfo" type="text" sval='' placeholder="请选择" onfocus="WdatePicker({isShowClear:true,readOnly:true,onpicked:function(dp){loadHotelTip(dp.cal.getNewDateStr(), dp.el)}})">
							</span>
							<span class="right-popup-cont-budget">预&nbsp;&nbsp;&nbsp;&nbsp;算<span class="right-must-write">*</span>：</span>
							<span>
								<input type="text" placeholder="填写本日预算" id="right-day-budget-input" class="right-popup-date-input" onkeyup="caterPriceVerify(this)" onblur="countSaveOverFunt(this)"/>
								<input type="text" id="hideInputForID" value="" style="display: none;" />
								<input type="text" style="display: none;"/>
							</span>
							
							<!-- 入住信息 -->
							<div class='inhotel-info-div' id='inhotelInfo'>
								
							</div>
						</div>
						
						<table id="right-popup-table" border="0" cellspacing="0" cellpadding="0" class="right-popup-table">
							<tr>
								<th class="width269">
									<span class="font14">餐别<span class="right-must-write">*</span></span>
									<input type="button" id="right-add-icon" rowid="0" class="right-add-icon" onclick="addNewRecord()"/>
								</th>
								<th class="width223">人数</th>
								<th class="width223">份数</th>
								<th class="width238">金额<span class="right-must-write">*</span></th>
								<th class="width223">人均</th>
								<th class="width223">备注</th>
							</tr>
							<tr id="totalSumTr">
								<td colspan="6" class="total-sum-wrap">
									<span >总人数：</span>
									<span class="total-people">0</span>
									<span>合计金额：</span>
									<span class="total-price">0</span>
									<span>节约/超支：</span>
									<span class="save-over">0</span>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 提示信息 -->
		<div id="eventAll" style="display: none;">
		      <div>
		                提示
		      </div>
		      <div>
		         <div style="margin-top: 25px;font-size: 16px;margin-left: 10px;" id="eventContent">
		                       是否确定此操作？
		         </div>
		         <div>
		           <div style=" margin: 30px 0px 0px 60px;">
		              <input type="button" id="sure" value="确定" style="margin-right: 10px;" />
		              <input type="button" id="closeBtn" value="取消" />
		           </div>
		         </div>
		       </div>
		</div>
	</body>
</html>