$(document).ready(function(){
	//获取打印信息
	getPrintInfo();
});
function getPrintInfo(){
	$.ajax({
		url: '/paymentManager/queryManyPaymentDetail',
		type: 'post',
		data: {paymentIds: $("#paymentIds").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#paymentIds").val("");
				console.log(response);
				var paymentInfoList= response.paymentInfoList;
				var container = [];
				if(paymentInfoList != null || paymentInfoList.length != 0){
					for(var i= 0; i< paymentInfoList.length; i++){
						container.push('<div class="payment-order-box">');
						container.push('  <dl class="payment-box-dl">');
						container.push('    <dt><div class="payment-header">');
						container.push('      <div class="bills-num">票据编号 : ');
						container.push('        <input class="header-input" type="text"  readonly value="'+ paymentInfoList[i].receiptNo +'">');
						container.push('      </div>');
						container.push('      <div class="header-title">付款单</div>');
						container.push('      <div class="bills-date">日期 : ');
						container.push('        <input class="header-input" type="text" value="'+ paymentInfoList[i].paymentDate +'">');
						container.push('      </div>');
						container.push('      <div class="div-clear"></div>');
						container.push('     </div></dt>');
						container.push('     <dd><div class="payment-header">');
						container.push('       <table width="100%" class="payment-table" cellspacing=0 cellpadding=0>');
						container.push('         <tbody>');
						container.push('         <tr>');
						container.push('			 <td> <span class="department-td">部门: </span>');
						container.push('			<input type="text" id="departmentText"  class="department-text" value = "'+  paymentInfoList[i].department +'">');
						container.push('           <span class="shoukuan-td">收款方(单位):</span>');
						container.push('			 <input class="shoukuan-person" type="text" value="'+ paymentInfoList[i].payeeName+'"></td>');
						container.push('         </tr>');
						container.push('         <tr>');
						container.push('           <td colspan ="2">');
						container.push('             <table class="payment-detail-table" cellspacing="0">');
						container.push('              <tr>');
						container.push('                <td>摘要</td><td>财务科目</td><td width="120px">币种</td><td>金额</td>');
						container.push('              </tr>');
						container.push('              <tbody>');
						var paymentFinanSubjMapList= paymentInfoList[i].paymentFinanSubjMapList;
						if(paymentFinanSubjMapList != null || paymentFinanSubjMapList.length != 0){
							
							for(var j= 0; j< paymentFinanSubjMapList.length; j++){
								container.push('                <tr class="add-sub-tr">');
								container.push('                  <td align="left">'+ paymentFinanSubjMapList[j].summary + '</td>' );
								container.push('                  <td align="left">'+ paymentFinanSubjMapList[j].financeSubjName + '</td>');
								if(j == 0){
									container.push('                  <td rowspan="500">'+ paymentInfoList[i].currencyName +"</td>");
								}
								
								container.push('                  <td align="right">'+ fmoney(paymentFinanSubjMapList[j].money) + '</td>');
								container.push('                 </tr>');
							}
						}
						container.push('               </tbody>');
						container.push('               <tfoot><tr>');
						container.push('                 <td colspan="3" align="left">');
						container.push('                   <div class="td-div">金额合计:'+ numberToCapital(paymentInfoList[i].totalMoney) + "</div>");
						container.push('                 </td>');
						container.push('                 <td align="right">');
						container.push('                   <div class="td-div">' + fmoney(paymentInfoList[i].totalMoney) +'</div>');
						container.push('                 </td>');
						container.push('               </tr></tfoot>');
						container.push('             </table>');
						container.push('           </td>');
						container.push('         </tr>');
						container.push('         <tr>');
						container.push('           <td colspan="2">');
						container.push('             <div class="bottom-option">有无发票:');
						if(paymentInfoList[i].hasReceipt == 0){
							container.push('            <input class="is-has-bill" type="text" value="无">');
						}else{
							container.push('            <input class="is-has-bill" type="text" value="有">');
						}
						container.push('              </div>');
						container.push('             <div class="bottom-option">票据种类:');
						if (paymentInfoList[i].billType == 1) {
							container.push('            <input class="is-has-bill" type="text" value="普通发票">');
						}else if (paymentInfoList[i].billType == 2) {
							container.push('            <input class="is-has-bill" type="text" value="增值税发票">');
						}else {
							container.push('            <input class="is-has-bill" type="text" value="">');
						}
						container.push('              </div>');
						container.push('              <div class="bottom-option">附单据:');
						container.push('                <input type="text" id="billCount" value="'+ paymentInfoList[i].billCount +'">张');
						container.push('              </div>');
						container.push('              <div class="bottom-option">付款方式:');
						container.push('                <input class="arrows-low" type="text" value="'+ paymentInfoList[i].paymentWay +'">');
						container.push('              </div>');
						container.push('              <div class="bottom-option">记账:');
						container.push('                <input class="agent-people" type="text" value="'+ paymentInfoList[i].agent +'">');
						container.push('              </div>');
						container.push('            </td>');
						container.push('          </tr>');
						container.push('        </tbody>');
						container.push('      </table>');
						container.push('    </div></dd>');
						container.push('  </dl>');
						container.push('</div>');
					}
				}
				
				$("#myContainer").append(container.join(""));
				if(paymentInfoList.length > 1){
					$(".payment-order-box:not(:last)").addClass("pageNext");
				}
				
				window.print();
				
			    if($("#needBacktoPage").val() == "true"){
			    	window.location.href='getCostManager/toGetCostPage?receiptType=1';
			    }
			    if($("#needClosePage").val() == "true"){
			    	window.close();
			    }
				
			}else{
				alert(response.message);
			}
		}
	});
}