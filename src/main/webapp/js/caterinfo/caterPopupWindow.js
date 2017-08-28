//餐饮管理右侧
$(function(){ 
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest(".cater-type").length == 0){ 
			$(".right-select-wrap").hide(); 
		} 
		if(target.closest(".cater-time").length == 0){ 
			$(".right-select-wrap-time").hide(); 
		} 
		if(target.closest(".cater-addr").length == 0){ 
			$(".right-select-wrap-addr").hide(); 
		} 
	});
	if(isReadonly){
		$("#right-save-btn").hide();
		$("#right-delete-btn").hide();
		$("#right-add-icon").hide();
	}

}); 
//获取当前日期
function getNowFormatDate() {
    var date = new Date();
    var character = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + character + month + character + strDate;
    $('#dateinfo').val(currentdate);
}
var blankFlag = false;;//空行标志
//添加一行加油信息
function addNewRecord(){
	$("#right-popup-table .right-popup-table-tr").each(function(){
	   var caterTypeCont = $(this).find("input[type=text]").eq(0).val();
	   var caterTypeTime = $(this).find("input[type=text]").eq(1).val();
	   var caterTypeAddr = $(this).find("input[type=text]").eq(2).val();
	   var caterPeople = $(this).find("input[type=text]").eq(3).val();
	   var caterCoipes = $(this).find("input[type=text]").eq(4).val();
	   var caterPrices = $(this).find("input[type=text]").eq(5).val();
	   var caterPeopleAvg = $(this).find("input[type=text]").eq(6).val();
	   var caterRemark = $(this).find("input[type=text]").eq(7).val();
	   if(caterTypeCont == "" || caterPrices == ""){
		   blankFlag = true;
		   return;
	   }else if(caterTypeCont == ""){
		   showInfoMessage("餐饮类别不能为空");
		   blankFlag = true;
		   return;
	   }else if(caterPrices == ""){
		   showInfoMessage("餐饮金额不能为空");
		   blankFlag = true;
		   return;
	   }else{
		   blankFlag = false;
	   }
   });
   
    var rowLength = $('.right-popup-table-tr').length;
    if(rowLength === 0){
        addBlankRow();
    }else if(blankFlag){
// 		showInfoMessage("请完善信息后再添加");
 		var trLen = $(".right-popup-table-tr").length;
 		var caterTypeValue = $(".right-popup-table-tr").eq(trLen-1).find('.cater-type').find('.insert-input');
 		var caterpriceValue = $(".right-popup-table-tr").eq(trLen-1).find('.prices-num').find('.total-price');
 		if(!caterTypeValue.val()){
 			caterTypeValue.focus();
 			caterTypeValue.prop('placeholder','请填写餐饮类别');
 		}else if(!caterpriceValue.val()){
 			caterpriceValue.focus();
 			caterpriceValue.prop('placeholder','请填写餐饮金额');
 		}
 	}else{
 		addBlankRow();
 	}
}
//添加一行空行
function addBlankRow(){
	var rowid = $('#right-add-icon').attr('rowid');
	$('#right-add-icon').attr('rowid',parseInt($('#right-add-icon').attr('rowid'))+1);
	var tr = $('<tr class="right-popup-table-tr"></tr>');
	var caterTd = $('<td class="cater-type"><input type="text" name="right-select-cater" id="right-select-cater'+rowid+'" class="right-select-cater insert-input" opotion-id="blank" onclick="showSelectWin(this)"/><div class="delete-add-row" onclick="deleteCaterMoneyInfo(this)"></div></td>');
	var caterTt = $('<td class="cater-time" ><input type="text" name="right-select-cater" id="right-select-caterTime'+rowid+'" opotion-id="blank"  class="right-select-cater insert-time" onclick="showSelectWinTime(this)"/></td>');
	var caterTa = $('<td class="cater-addr" ><input type="text" name="right-select-cater" id="right-select-caterAddr'+rowid+'" opotion-id="blank"  class="right-select-cater insert-addr" onclick="showSelectWinAddr(this)"/></td>');
	var peopleTd = $('<td class="people-num"><input type="text" id="right-people-input'+rowid+'" onkeyup="onlyNumber(this)"  class="right-select-cater total-people-num" onblur="getCaterPeopleNum(this)"/></td>');
	var copiesTd = $('<td class="copies-num"><input type="text" id="right-copies-input'+rowid+'" onkeyup="onlyNumber(this)"  class="right-select-cater cater-copies" onblur="getCaterCopies(this)"/></td>');
	var priceTd = $('<td class="prices-num"><input type="text" id="right-price-input'+rowid+'" onkeyup="caterPriceVerify(this)" class="right-select-cater total-price" onblur="caterPeoplePrice(this)"/></td>');
	var avgTd = $('<td class="people-avg"><input type="text" id="right-people-avg'+rowid+'" class="right-select-cater people-avg" readonly/></td>');
	var remarkTd = $('<td class="remark-info"><input type="text" id="right-remark-input'+rowid+'"  class="right-select-cater" /><input type="text" class="hide-input-val" value="" style="display:none;"/></td>');
	tr.append(caterTd);
	tr.append(caterTt);
	tr.append(caterTa);
	tr.append(peopleTd);
	tr.append(copiesTd);
	tr.append(priceTd);
	tr.append(avgTd);
	tr.append(remarkTd);
	$('#totalSumTr').before(tr);
	getCaterType(tr);
	getCaterTypeTime(tr);
	getCaterAddr(tr);
	
}
//删除餐饮信息
function delCaterInfo(){
	var id =  $('#hideInputForID').val();
	popupPromptBox("提示","是否要删除该条餐饮信息？", function (){
		$.ajax({
			url: '/caterInfo/deleteCaterInfoById',
			type: 'post',
			data: {caterId:id},
			dataType: 'json',
			success: function(response){
				if(response.success){
					 showSuccessMessage("删除成功");
					 closeRightWin();
				 }else{
					 showErrorMessage(response.message);
				 }
			}
		});
	});
}
//获取餐饮类别信息
function getCaterType(tr){
	var html = '';
    for(var i = 0;i<caterTypeList.length;i++){
    	html +='<div class="select-option" id="'+ caterTypeList[i].caterType +'" onclick="selectCaterType(this)">'+ caterTypeList[i].caterType +'</div>';
    }
	html = '<div class="right-select-wrap">'+ html +'</div>';
	tr.find('.insert-input').after(html);
}
//获取餐饮时间
function getCaterTypeTime(tr){
	var html = '';
	for(var i = 0;i<caterTypeTimeList.length;i++){
		html +='<div class="select-option" id="'+ caterTypeTimeList[i].caterTimeType +'" onclick="selectCaterTypeTime(this)">'+ caterTypeTimeList[i].caterTimeType +'</div>';
	}
	html = '<div class="right-select-wrap-time">'+ html +'</div>';
	tr.find('.insert-time').after(html);
}
//获取餐饮地点
function getCaterAddr(tr){
	var html = '';
	for(var i = 0;i<caterAddrList.length;i++){
		html +='<div class="select-option" id="'+ caterAddrList[i].caterAddr +'" onclick="selectCaterAddr(this)">'+ caterAddrList[i].caterAddr +'</div>';
	}
	html = '<div class="right-select-wrap-addr">'+ html +'</div>';
	tr.find('.insert-addr').after(html);
}
//
//计算总人数
function countPeopleNum(){
	var countPeopleSum = 0;
	$('#right-popup-table').find('tr').each(function(){
		var _this = $(this);
		var num = _this.find('.total-people-num').val();
		if(!num){
			num = 0;
		}
		countPeopleSum += parseInt(num);
	});
	$("span.total-people").text(countPeopleSum);
}
//计算合计金额
var tempTotalPrice=0;
function countTotalPrice(){
	var totalPrice = 0;
	$('#right-popup-table').find('tr').each(function(){
		var num = 0;
		var _this = $(this);
		
		if(!_this.find('.total-price').val()){
			num = 0;
		}else{
			num = _this.find('.total-price').val().replace(/,/g, "");
		}
		totalPrice += parseFloat(num);
		tempTotalPrice=totalPrice;
	});
	$("span.total-price").text(fmoney(totalPrice));
}
//计算合计金额、总人数、节约超支
function showTableFooter(){
	var html = [];
	$('.total-sum-wrap').html('<span>总人数：'+ countPeopleNum() +'</span><span>合计金额：<span id="totalPriceNum">'+ countTotalPrice() +'</span></span><span>人均：'+ countSaveOver() +'</span>');
}
//计算节约超支
function countSaveOver(){
	/*var saveOver = 0;
	var budgetNum = 0;
	var totalPriceNum = 0;
	//获取本日预算值
	if(!$('#right-day-budget-input').val()){
		budgetNum = 0;
	}else{
		budgetNum =$('#right-day-budget-input').val().replace(/,/g, "");
		
	}
	//获取合计金额值
	if(!tempTotalPrice){
		tempTotalPrice = 0;
	}else{
		totalPriceNum = tempTotalPrice;
	}
	saveOver = budgetNum -totalPriceNum;
	if(saveOver < 0){//负号和逗号的问题处理
	    leftMoney = saveOver + "";
	    var money = leftMoney.split("-");
	    $('.save-over').html("-" + fmoney(money));
	}else{
		$('.save-over').html(fmoney(saveOver));
	}*/
	//总人数
	var countPeopleSum = 0;
	$('#right-popup-table').find('tr').each(function(){
		var _this = $(this);
		var num = _this.find('.total-people-num').val();
		if(!num){
			num = 0;
		}
		countPeopleSum += parseInt(num);
	});
	$("span.total-people").text(countPeopleSum);
	//总金额
	var totalPrice = 0;
	$('#right-popup-table').find('tr').each(function(){
		var num = 0;
		var _this = $(this);
		
		if(!_this.find('.total-price').val()){
			num = 0;
		}else{
			num = _this.find('.total-price').val().replace(/,/g, "");
		}
		totalPrice += parseFloat(num);
		tempTotalPrice=totalPrice;
	});
	$("span.total-price").text(fmoney(totalPrice));
	var balance = 0;
	balance=totalPrice/countPeopleSum;
	if(totalPrice==0 || countPeopleSum==0){
		$('.save-over').html(fmoney(0.00));
	}else{
		$('.save-over').html(fmoney(balance));
	}
}
//保存餐饮信息
function saveOneCaterInfo(){
	//必填项处理
	if($("#dateinfo").val() == ""){
	   showInfoMessage("就餐日期不能为空");
	   return;
	}
	if($("#right-day-budget-input").val() == ""){
	   showInfoMessage("本日预算不能为空");
	   return;
	}
/*	if($(".insert-input").val() == "" || $(".insert-input").val() == undefined){
		showInfoMessage("就餐类别不能为空");
	    return;
	}
	if($(".total-price").val() == "" || $(".total-price").val() == undefined){
		showInfoMessage("就餐金额不能为空");
	    return;
	}*/
	
	var caterMOneyStr = "";
	var caterId = $("#hideInputForID").val();
	var caterDate = $("#dateinfo").val();
	var budget = parseFloat($('#right-day-budget-input').val().replace(/,/g, ""));
	var errorMessage = "";
	$("#right-popup-table .right-popup-table-tr").each(function(){
	   var caterTypeCont = $(this).find("input[type=text]").eq(0).val();
	   var caterPrices = $(this).find("input[type=text]").eq(5).val();
	   if(caterTypeCont == "" && caterPrices == ""){
		   showInfoMessage("餐饮类别和就餐金额不能为空");
		   errorMessage = "餐饮类别和就餐金额不能为空";
	   }else if(caterTypeCont == ""){
		  showInfoMessage("餐饮类别不能为空，请填写餐饮类别");
		  errorMessage = "餐饮类别不能为空，请填写餐饮类别";
	   }else if(caterPrices == ""){
		   showErrorMessage("就餐金额不能为空，请填写就餐金额");
		   errorMessage = "就餐金额不能为空，请填写就餐金额";
	   }else{
	   		caterMOneyStr='';
			$('#right-popup-table tr[class=right-popup-table-tr]').each(function(){//循环每一行的内容
				var _this = $(this);
				var caterTypeId = _this.find('td[class=cater-type] input[class*=right-select-cater]').attr('opotion-id');
				if(!caterTypeId){
					caterTypeId = 'blank';
				}
		//		caterMOneyStr += caterTypeId+',';//ID
				caterMOneyStr += _this.find('td[class=cater-type] input[class*=right-select-cater]').val()+',';//类别
				caterMOneyStr += _this.find('td[class=people-num] input[class*=right-select-cater]').val()+',';//人数
				caterMOneyStr += _this.find('td[class=copies-num] input[class*=right-select-cater]').val()+',';//份数
				caterMOneyStr += _this.find('td[class=prices-num] input[class*=right-select-cater]').val().replace(/,/g, "")+',';//金额
				caterMOneyStr += _this.find('td[class=people-avg] input[class*=right-select-cater]').val().replace(/,/g, "")+',';//人均
				caterMOneyStr += _this.find('td[class=remark-info] input[class*=right-select-cater]').val()+',';//备注
				caterMOneyStr += _this.find('td[class=remark-info] input[class*=hide-input-val]').val()+','; //就餐信息id
				caterMOneyStr += _this.find('td[class=cater-time] input[class*=right-select-cater]').val()+',';//用餐时间
				caterMOneyStr += _this.find('td[class=cater-addr] input[class*=right-select-cater]').val()+',##';//用餐地点
			});
	   }
	   caterMOneyStr = caterMOneyStr.substring(0,caterMOneyStr.length-2);
   });
   
	if (errorMessage == '') {
		$.ajax({
			url:"/caterInfo/saveOrUpdateCaterInfo",
			type:"post",
			dataType:"json",
			data:{caterId:caterId,caterDate:caterDate,budget:budget,caterMOneyStr:caterMOneyStr},
			async: false,
			success:function(response){
				if(response.success){
					showSuccessMessage("保存成功");
					closeRightWin();
					$("#caterInfoList").jqxGrid("updatebounddata");
					$("#caterInfoList").on("bindingcomplete", function (event) {
						$('#caterInfoList').jqxGrid('clearselection');
						var rows = $('#caterInfoList').jqxGrid('getrows');
						var hideCaterId = $('#hideInputForID').val();
						for(var i= 0; i< rows.length; i++){
							if(rows[i].caterId == hideCaterId){
								$('#caterInfoList').jqxGrid('selectrow', i);
								return;
							}
						}
					});
				}else{
					showErrorMessage(response.message);
				}
			}
		});	
		
	}
}
//关闭餐饮信息弹窗
function closeRightWin(){
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 600);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 600);
	$("#caterInfoList").jqxGrid("updatebounddata");
}
//删除某一行的方法
function deleteCaterMoneyInfo(own){
	var rowId =  $(own).parent('.cater-type').find('.insert-input').attr('id');
	var hideInputVal =  $(own).parent().parent().find('.remark-info').find('.hide-input-val').attr('value');
	if(!hideInputVal){
		rowId = rowId;
	}else{
		rowId = hideInputVal;
	}
	popupPromptBox("提示","是否要删除该条餐饮信息？", function(){
		$.ajax({
			url: '/caterInfo/deleteCaterMoneyInfo',
			type: 'post',
			data: {caterMoneyId:rowId},
			dataType: 'json',
			success: function(response){
				if(response.success){
					if(!hideInputVal){
						hideInputVal = rowId;
						$(own).parent('.cater-type').parent('.right-popup-table-tr').remove();
					}else{
						hideInputVal = hideInputVal;
						$(own).parent('.cater-type').parent('.right-popup-table-tr').remove();
					}
					showSuccessMessage("删除成功");
					
					countPeopleNum();
					countTotalPrice();
					countSaveOver();
				 }else{
					 showErrorMessage(response.message);
				 }
			}
		});
	});
}
//餐饮份数失去焦点，验证输入内容
function getCaterCopies(own){
	var _this = $(own);
	var copies = _this.val();
	var reg = new RegExp("^(0|[1-9][0-9]*)$");
	if(!copies){
		_this.parent().parent().find('.cater-copies').val('');
		return;
	}
	if(!reg.test(copies)){
		_this.val('');
		showInfoMessage("请正确输入就餐份数！");
		return;
	}
}
//关闭弹窗
var valugflag= false;//值是否改变标识,同一个页面，值不变
function closeRightWindow(){
	if(caterData == undefined){
		closeRightWin();
		return;
	}
	var caterInfo = caterData.caterInfo;
	var caterMoneyList = caterData.caterMoneyList;
	var trLength = $(".right-popup-table-tr").length;
//	if (trLength == 1) {
//		var textLength = $("right-popup-table-tr td").find("input").length;
//		if (textLength == 0) {
//			trLength = 0;
//		}
//	}
	if(!valugflag){
		if($("#dateinfo").val() != caterData.caterInfo.caterDate){
			valugflag = true;
			if($("#dateinfo").val() == "" && caterData.caterDate == null || $("#dateinfo").val() == "" && caterData.caterDate == ""){
				valugflag = false;
			}
		}
	}
		
	if(!valugflag){
		if($("#right-day-budget-input").val().replace(/,/g, "") != caterData.caterInfo.budget){
			valugflag = true;
			if($("#right-day-budget-input").val() == "" && caterData.budget == null || $("#right-day-budget-input").val() == "" && caterData.budget == ""){
				valugflag = false;
			}
		}
	}

	if(trLength != caterMoneyList.length){//长度不相等相当于改变餐饮，不在做其他处理
		valugflag = true;
	}else{//精确比较每行的值
		var trObj = $(".right-popup-table-tr");
		for(var i= 0; i< caterMoneyList.length; i++){
			for(var j=0; j< trObj.length; j++){
				if(i == j){
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(0).val() != caterMoneyList[i].caterType){//餐别
							valugflag = true;
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(1).val() != caterMoneyList[i].caterTimeType){//用餐地点
							valugflag = true;
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(2).val() != caterMoneyList[i].caterAddr){//用餐时间
							valugflag = true;
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(3).val() != caterMoneyList[i].peopleCount){//人数
							valugflag = true;
							if($(trObj[j]).find("input[type=text]").eq(3).val() == "" && caterMoneyList[i].peopleCount == null || $(trObj[j]).find("input[type=text]").eq(3).val() == "" && caterMoneyList[i].peopleCount == ""){
								valugflag = false;
							}
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(4).val() != caterMoneyList[i].caterCount){//份数
							valugflag = true;
							if($(trObj[j]).find("input[type=text]").eq(4).val() == "" && caterMoneyList[i].caterCount == null || $(trObj[j]).find("input[type=text]").eq(4).val() == "" && caterMoneyList[i].caterCount == ""){
								valugflag = false;
							}
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(5).val().replace(/,/g, "") != caterMoneyList[i].caterMoney){//金额
							valugflag = true;
							if($(trObj[j]).find("input[type=text]").eq(5).val() == "" && caterMoneyList[i].caterMoney == null || $(trObj[j]).find("input[type=text]").eq(5).val() == "" && caterMoneyList[i].caterMoney == ""){
								valugflag = false;
							}
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(6).val().replace(/,/g, "") != caterMoneyList[i].perCapita){//人均
							valugflag = true;
							if($(trObj[j]).find("input[type=text]").eq(6).val() == "" && caterMoneyList[i].perCapita == null || $(trObj[j]).find("input[type=text]").eq(6).val() == "" && caterMoneyList[i].perCapita == ""){
								valugflag = false;
							}
						}
					}
					if(!valugflag){
						if($(trObj[j]).find("input[type=text]").eq(7).val() != caterMoneyList[i].remark){//备注
							valugflag = true;
							if($(trObj[j]).find("input[type=text]").eq(7).val() == "" && caterMoneyList[i].remark == null || $(trObj[j]).find("input[type=text]").eq(7).val() == "" && caterMoneyList[i].remark == ""){
								valugflag = false;
							}
						}
					}
				}
			}
		}
	}
	if(valugflag){
		tipInfoBox("提示","您修改了信息，是否要保存？", function (){
			saveOneCaterInfo();//保存
			/*var flag;
			caterData = flag;*/
	    });
		valugflag= false;
	}else{
		valugflag= false;
		/*var flag;
		caterData = flag;*/
		closeRightWin();
	}
}
//初始化消息弹出窗口
function initTipWindow(){
	$('#eventAll').jqxWindow({
         maxHeight: 150, 
         maxWidth: 280, 
         minHeight: 30, 
         minWidth: 250, 
         height: 145, 
         width: 270,
         modalZIndex: 20010,
         resizable: false, 
         isModal: true, 
         modalOpacity: 0.3,
         theme:theme,
         okButton: $('#sure'), 
         cancelButton: $('#closeBtn'),
         autoOpen: false,
         initContent: function () {
             $('#sure').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').jqxButton({theme:theme, width: '65px',height:'25px' });
             $('#closeBtn').on("click", function(){
            	 closeRightWin();//关闭
            	 $('#eventAll').jqxWindow('close');
             });
         }
     });
}
/**
 * 弹出提示框
 * @param title 标题
 * @param content 内容
 */
function tipInfoBox(title,content,obj){
	$('#eventAll').jqxWindow('open');
	if(title!=undefined || title!=null)
		$('#eventAll').jqxWindow('setTitle', title);
	if(content!=undefined || content!=null)
		$('#eventContent').html(content);
	if(content.length > 15)
		$('#eventContent').css("margin-top","13px");
	$('#eventAll').unbind("close");
	$('#eventAll').on('close', function (event) {
		if (event.args.dialogResult.OK) {
			$(obj);
        }
    });
}