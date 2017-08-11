$(document).ready(function(){
	getPrintCollectionInfo();
	
});

//获取打印信息
function getPrintCollectionInfo(){
	var collectionIds = $("#collectionIds").val();
	console.log(collectionIds);
	$.ajax({
		url: '/collectionManager/queryManyCollectionDetailInfo',
		type: 'post',
		data: {collectionIds: collectionIds},
		datatype: 'json',
		success: function(response){
			if(response.success){
				
				console.log(response);
				var collectionList= response.collectionList[0];
				$("#collectionId").val(collectionList.collectionId);
				$("#billsNum").val(collectionList.receiptNo);
				$("#billsDateTime").val(collectionList.collectionDate);
				$("#payMoneyParty").val(collectionList.otherUnit);
				$("#summary").val(collectionList.summary);
				$("#currency").val(collectionList.currencyName);
				$("#money").val(fmoney(collectionList.money));
				$("#paymentWay").val(collectionList.paymentWay);
				$("#agent").val(collectionList.agent);
				var capitalMoney= numberToCapital($("#money").val().replace(/,/g,"")-0);
				$("#capitalAccountMoney").text("(大写)"+capitalMoney);
				window.print();
				if($("#needBacktoPage").val() == "true"){
					window.location.href='getCostManager/toGetCostPage?receiptType=2';
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