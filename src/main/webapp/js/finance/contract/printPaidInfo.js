$(document).ready(function(){
	console.log($("#paidId").val());
	getPaidOrderInfo();
});

//获得当前用户名
function getUserName(){
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var userInfo = response.userInfo;
				if(userInfo != null){
					$(".userAgent").each(function(){
						$(this).text(userInfo.realName);
					});
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//获得代付单的信息
function getPaidOrderInfo(){
	$.ajax({
		url: '/contractToPaidController/queryContractToPaidListById',
		type: 'post',
		data: {"id": $("#paidId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				console.log(response);
				var contractToPaidMap = response.contractToPaidMap;
				if(contractToPaidMap.length != 0){
					var html = [];
					for(var i= 0; i<contractToPaidMap.length; i++){
						
						html.push('<div class="my-reimbursement-content">');
						html.push('  <div class="right-div-modular">');
						html.push('    <dl class="make-content-dl">');
						html.push('      <dt>');
						html.push('        <h2>PRODUCTION EXPENDITURE REPORT FORM<br>制作实支报销单</h2>');
						html.push('          <div class="description-info">');
						html.push('            <table class="basic-info-table">');
						html.push('              <tr><td></td><td><p>No.(凭单号)&nbsp;:</p><div class="voucher-no"></div></td></tr>');
						html.push('              <tr> <td><p>项目/剧组:</p><div class="pro-crew-name">《'+ contractToPaidMap[i].crewName +'》</div></td>');
						html.push('                   <td><p>Date(日期)&nbsp;:</p></td></tr>');
						html.push('            </table>');
						html.push('           </div>');
						html.push('         </dt>');
						html.push('         <dd>');
						html.push('           <table class="detail-info-table-one" cellspacing=0 cellpadding=0>');
						/*html.push('             <thead>');*/
						html.push('               <tr class="thead-tr"><td>日期(date)</td><td>编号(No.)</td><td style="width: 227px;">Particulars<br>用途说明</td><td>A/C code<br>科目</td><td style="width: 256px;">Analysis code<br>明细科目</td><td>Amount(RMB)<br>金额(人民币)</td></tr>');
	                  /*  html.push('             </thead>');*/            
	                   /* html.push('             <tbody>');*/
	                    html.push('               <tr class="detail-info-tr">');
	                    html.push('                 <td><div></div></td>');
	                    html.push('                 <td><div></div></td>');
	                    html.push('                 <td><div>'+ contractToPaidMap[i].summary +'</div></td>');
	                    html.push('                 <td><div>'+ contractToPaidMap[i].subjectNameMain +'</div></td>');
	                    html.push('                 <td><div>'+ contractToPaidMap[i].subjectNameDetail +'</div></td>');
	                    html.push('                 <td><div style="padding-right: 5px; text-align: right;">'+ fmoney(contractToPaidMap[i].money) +'</div></td>');
	                    html.push('               </tr>');
	                    for(var j= 0; j< 15; j++){
	                    	html.push('<tr class="detail-info-tr"><td><div></div></td><td><div></div></td><td><div></div></td><td><div></div></td><td><div></div></td><td><div></div></td></tr>');
	                    }
	                    html.push('<tr class="money-tr">');
	                    html.push('  <td colspan="4"><p>大写金额:</p><div>'+ numberToCapital(contractToPaidMap[i].money) +'</div></td>');
	                   /* html.push('  <td colspan="2"><p>总额(Total):</p><div>' + fmoney(contractToPaidMap[i].money) + '</div><input type="hidden"></td>');*/
	                    html.push('  <td><p>Total(总额):</p>');
	                    html.push('<td><div style="padding-right: 5px; text-align: right; width: calc(100% - 10px);">'+ fmoney(contractToPaidMap[i].money) + '</div></td>');
	                    html.push('</tr>');
	                  /*  html.push('</tbody>');*/
	                    html.push('</table>');
	                    html.push('</dd>');
	                    html.push('<dd>');
	                    html.push('  <table class="readonly-explain" cellspacing=0 cellpadding=0>');
	                    html.push('    <tr><td style="width: 400px;">Applicant/Date<br>报销者/日期</td><td>Position/Dept<br>职务/部门</td><td rowspan="3" style="width: 180px;">Received by<br>签收</td></tr>');
	                    html.push('    <tr><td>line Producer/Date<br>监制/日期</td><td>Prod Manager/Date<br>制片主任</td></tr>');
	                    html.push('    <tr><td>Producer/Date<br>制片人/日期</td><td><p>Producer Manager/Date<br>会计出纳/日期 &nbsp;&nbsp;<span class="userAgent">当前用户名</span></p></td></tr>');
	                    html.push('  </table>');
	                    html.push('</dd>');
	                    html.push('<dd class="last-dd-pack"><div class="last-tips">Accounts Department</div></dd>');
	                    html.push('<p class="pack-count">附件:&nbsp; <input type="text">张</p>');
	                    html.push('</dl>');
	                    html.push('</div>');
	                    html.push('</div>');
					}
					$("body").append(html.join(""));
					getUserName();
					
					if(contractToPaidMap.length > 1){
						$(".my-reimbursement-content:not(:last)").addClass("pageNext");
					}
					
					window.print();
					window.close();
				}
			}else{
				alert(response.message);
			}
		}
	});
}