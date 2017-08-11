$(document).ready(function(){
	//获取借款单信息
	getLoanOrderInfo();
	
});

function getLoanOrderInfo(){
	$.ajax({
		url: '/loanInfoManager/queryManyLoanDetailInfo',
		type: 'post',
		data: {loanIds: $("#loanIds").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				console.log(response);
				var loanInfoList= response.loanInfoList[0];
				$("#loanId").val(loanInfoList.loanId);
				$("#billsNum").val(loanInfoList.receiptNo);
				$("#billsDateTime").val(loanInfoList.loanDate);
				$("#loanMoneyParty").val(loanInfoList.payeeName);
				$("#summary").val(loanInfoList.summary);
				$("#financeSubjId").val(loanInfoList.financeSubjId);
				$("#financeSubjName").val(loanInfoList.financeSubjName);
				$("#currency").val(loanInfoList.currencyName);
				$("#money").val(fmoney(loanInfoList.money));
				$("#paymentWay").val(loanInfoList.paymentWay);
				$("#agent").val(loanInfoList.agent);
				var capitalMoney= numberToCapital($("#money").val().replace(/,/g,"")-0);
				$("#capitalAccountMoney").text("(大写)"+capitalMoney);
				window.print();
				if($("#needBacktoPage").val() == "true"){
					window.location.href="getCostManager/toGetCostPage?receiptType=3";
				}
				if($("#needClosePage").val() == "true"){
					window.close();
				}
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}