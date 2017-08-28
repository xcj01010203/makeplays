var caterTypeList;
var caterTypeTimeList;
var caterAddrList;
//餐饮管理
$(function(){
	loadCaterListGrid();
	initTipWindow();
	if(isReadonly){
		$("#right-save-btn").remove();
		$("#right-delete-btn").remove();
		$("#right-add-icon").remove();
		$("#dateinfo").attr("disabled", true);
		$("#right-day-budget-input").attr("disabled", true);
	}
});
//生成餐饮管理主列表
function loadCaterListGrid(){
	var source = {
		url: '/caterInfo/queryCaterInfoList',
		type: "post",
		dataType : "json",
		datafields : [
			 {name: "caterId", type: "string"},
		     {name: "days", type: "int"},
		     {name: "caterDate", type: "string"},
		     {name: "dayTotalMoney", type: "double"},
		     {name: "budget", type: "double"},
		     {name: "dayLeftMoney", type: "double"},
		     {name: "peopleCount", type: "int"},
		     {name: "dayPerCapita", type: "double"},
		     {name: "accumulateMoney", type: "double"},
		     {name: "accumulateLeftMoney", type: "double"},
		],
		root: 'caterInfoList',
	};
	
	//日期
	var toCaterDate = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
		var html= [];
		html.push("<div class='jqx-column'>");
		html.push(" <a href='javascript:modifyCarInfo(\""+rowdata.caterId+"\",\""+ rowdata.caterDate +"\")' class='cater-date'>" + rowdata.caterDate + "</a>");
		html.push("</div>");
		return html.join("");
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#caterInfoList").jqxGrid({
		width: "calc(100% - 2px)",
		height: "calc(100% - 2px)",
		columnsheight: 35,
		rowsheight: 30,
		toolbarheight: 40,
		altrows: true,
		source: dataAdapter,
		showtoolbar: true,
		columns: [
			        { text: '天数', datafield: 'days', cellsAlign: "center", align: "center", width: '8%' },
			        { text: '日期', datafield: 'caterDate', cellsrenderer: toCaterDate,  cellsAlign: "left", align: "center", width: '13%' },
			        { text: '当日费用', datafield: 'dayTotalMoney', cellsAlign: "center", align: "center", width: '11%' },
			        { text: '当日预算', datafield: 'budget', cellsAlign: "center", align: "center", width: '11%' },
			        { text: '当日节约/超支', datafield: 'dayLeftMoney', cellsAlign: "center", align: "center", width: '9%' },
			        { text: '人数 ', datafield: 'peopleCount', cellsAlign: "center", align: "center", width: '12%' },
			        { text: '人均', datafield: 'dayPerCapita', cellsAlign: "center", align: "center", width: '12%' },
			        { text: '累计费用', datafield: 'accumulateMoney',cellsAlign: "center", align: "center", width: '12%' },
			        { text: '累计节约/超支', datafield: 'accumulateLeftMoney', cellsAlign: "center", align: "center", width: '12%' }
			    ],
		rendertoolbar: function(toolbar) {
			var container = [];
			container.push("<div class='toolbar'>");
			container.push("<input type='button' class='add-cater-btn' id='addCaterInfoBtn' onclick='addCaterInfo()' value='添加'>");
            container.push("</div>");
			toolbar.append($(container.join("")));
			$("#addCaterInfoBtn").jqxTooltip({content: "添加餐饮信息", position: "bottom"});
		},
	});
}
//添加餐饮信息弹窗
function addCaterInfo(){
	//清空入住信息
	$("#inhotelInfo").html('');
	if(isReadonly){
		return;
	}
	//将餐饮信息置空
	caterData = undefined;
	resetPopupWindow();
	gatCaterType();
}
//还原右侧页面设置
function resetPopupWindow(){
	$("#rightPopUpWin").show().animate({"right": "0px"}, 600);
	$('#right-day-budget-input').val('');
	$('#hideInputForID').val('');
	$('.right-popup-table-tr').remove();
	$('#right-delete-btn').hide();
	gatCaterType();
	addBlankRow();
	getNowFormatDate();
	countPeopleNum();
	countTotalPrice();
	countSaveOver();
	//获取住宿信息
	loadHotelTip( $('#dateinfo').val());
}
//点击日期，渲染对应数据
var caterData;
function modifyCarInfo(editrow, date){
	$("#inhotelInfo").text('');
	if(isReadonly){
//		return;
	}
	$("#dateinfo").attr("sval", editrow);
    $("#right-popup-table tr[class='right-popup-table-tr']").remove();
	$('#hideInputForID').attr('value',editrow);//将ID传到弹框里的隐藏的input中
	$("#rightPopUpWin").show().animate({"right": "0px"}, 600);
	$('#right-delete-btn').show();
	gatCaterType();
	//根据餐饮id查询餐饮餐饮及餐饮金额的详细信息
	$.ajax({ 
	    url: '/caterInfo/queryCaterAndMoneyInfo',
		type: 'post',
		data: {caterId:editrow, date:date},
		dataType: 'json',
		async: false,
	    success:function(data){//这个data就是后台给你的json格式的数据
	    	if(data.success){
	    		caterData = data.data;
				var rightDate = data.data.caterInfo.caterDate;//时间
				var rightCaterId = data.data.caterInfo.caterId;//ID
				var rightBudget = data.data.caterInfo.budget.toFixed(2);//预算
				var leftMoney = data.data.leftMoney;//节约超支
				var totalMoney = data.data.totalMoney;//合计金额
				var totalPeopleMoney = data.data.totalPeopleMoney;//总人数
				var inHotelInfoStr = data.inHotelInfoStr; //入住信息
				var balance=0;
				
				
				if(!totalPeopleMoney || totalPeopleMoney == 'null'){
					totalPeopleMoney = 0;
				}
				if(!totalMoney || totalMoney == 'null'){
					totalMoney = 0;
				}
				if(!leftMoney || leftMoney == 'null'){
					leftMoney = 0;
				}
				
				$("#inhotelInfo").text(inHotelInfoStr);
				$("#inhotelInfo").attr("title", inHotelInfoStr);
				$('.total-people').text(totalPeopleMoney);
				$('.total-price').text(fmoney(totalMoney));
				$('.people-avg').prop('disabled',true);
				/*if(leftMoney < 0){
				    leftMoney = leftMoney + "";
				    var money = leftMoney.split("-");
				    $('.save-over').html("-" + fmoney(money));
				}else{
					$('.save-over').html(fmoney(leftMoney));
				}*/
				balance=totalMoney/totalPeopleMoney;
				if(totalMoney==0 || totalPeopleMoney==0){
					$('.save-over').html(fmoney(0.00));
				}else{
					$('.save-over').html(fmoney(balance));
				}
				
				var len = data.data.caterMoneyList.length;
				for(var i = 0 ; i < len; i ++){
					var rowid = $('#right-add-icon').attr('rowid');
					//餐饮金额信息
					var rightCaterType = data.data.caterMoneyList[i].caterType;//餐饮类别
					var rightCaterTime = data.data.caterMoneyList[i].caterTimeType;//用餐时间
					var rightCaterAddr = data.data.caterMoneyList[i].caterAddr;//用餐地点
					var rightPeopleCount = data.data.caterMoneyList[i].peopleCount;//人数
					var rightCaterCount = data.data.caterMoneyList[i].caterCount;//份数
					var rightMoney = data.data.caterMoneyList[i].caterMoney;//金额
					var rightPerCapita = data.data.caterMoneyList[i].perCapita;//人均
					var rightRemark = data.data.caterMoneyList[i].remark;//备注
					var caterMoneyId = data.data.caterMoneyList[i].caterMoneyId;//单行的ID
					
					//判定数据是否存在
					if(!rightBudget){
						rightBudget='';
					}
					if(!rightCaterType){
						rightCaterType='';
					}
					if(!rightCaterTime){
						rightCaterTime='';
					}
					if(!rightCaterAddr){
						rightCaterAddr='';
					}
					if(!rightPeopleCount){
						rightPeopleCount='';
					}
					if(!rightCaterCount){
						rightCaterCount='';
					}
					if(!rightMoney){
						rightMoney='';
					}
					if(!rightPerCapita){
						rightPerCapita='';
					}
					if(!rightRemark){
						rightRemark='';
					}
					var avgTd;
					var remarkTd;
					//创建对应的行
					var tr = $('<tr class="right-popup-table-tr"></tr>');
					var caterTd = $('<td class="cater-type" ><input type="text" name="right-select-cater" id="right-select-cater'+rowid+'" opotion-id="" value="'+ rightCaterType +'" class="right-select-cater insert-input" onclick="showSelectWin(this)"/><div class="delete-add-row" onclick="deleteCaterMoneyInfo(this)"></div></td>');
					var caterTt = $('<td class="cater-time" ><input type="text" name="right-select-cater1" id="right-select-caterTime'+rowid+'" opotion-id="" value="'+ rightCaterTime +'" class="right-select-cater insert-time" onclick="showSelectWinTime(this)"/></td>');
					var caterTa = $('<td class="cater-addr" ><input type="text" name="right-select-cater2" id="right-select-caterAddr'+rowid+'" opotion-id="" value="'+ rightCaterAddr +'" class="right-select-cater insert-addr" onclick="showSelectWinAddr(this)"/></td>');
					var peopleTd = $('<td class="people-num"><input type="text" id="right-people-input'+rowid+'" class="right-select-cater total-people-num" onblur="getCaterPeopleNum(this)" onkeyup="onlyNumber(this)"  value="'+ rightPeopleCount +'"/></td>');
					var copiesTd = $('<td class="copies-num"><input type="text" id="right-copies-input'+rowid+'" class="right-select-cater cater-copies" onblur="getCaterCopies(this)"  onkeyup="onlyNumber(this)"  value="'+ rightCaterCount +'"/></td>');
					var priceTd = $('<td class="prices-num"><input type="text" id="right-price-input'+rowid+'" class="right-select-cater total-price" value="'+ fmoney(rightMoney) +'" onkeyup="caterPriceVerify(this)"  onblur="caterPeoplePrice(this)" /></td>');
					if(rightPerCapita == ""){
						avgTd = $('<td class="people-avg"><input type="text" id="right-people-avg'+rowid+'" class="right-select-cater people-avg" value="'+ rightPerCapita +'" readonly/></td>');
					}else{
						avgTd = $('<td class="people-avg"><input type="text" id="right-people-avg'+rowid+'" class="right-select-cater people-avg" value="'+ fmoney(rightPerCapita) +'" readonly/></td>');
					}
					remarkTd = $('<td class="remark-info"><input type="text" id="right-remark-input'+rowid+'"  class="right-select-cater" value="'+ rightRemark +'"/><input type="text" class="hide-input-val" value="'+ caterMoneyId +'" style="display:none;"/></td>');
		
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
				if(isReadonly){
					$(".delete-add-row").hide();
					$("#right-popup-table input[type=text]").each(function(){
					$(this).attr("disabled", true);
				    });
				}
				
				
				$('#dateinfo').val(rightDate);//餐饮日期
				$('#right-day-budget-input').val(fmoney(rightBudget));//本日预算   fmoney(rightBudget)
				$('#hideInputForID').val(rightCaterId);//ID
			}else{
				showErrorMessage(data.message);
			}
	    }
	});
}
//选择餐饮类型
function selectCaterType(own){
	var $this = $(own);
	$this.parent().prev().val($this.text());
	$this.parent().prev().attr("value", $this.text());
	$this.parent().prev().attr('opotion-id',$this.attr('id'));
}
//选择餐饮时间
function selectCaterTypeTime(own){
	var $this = $(own);
	$this.parent().prev().val($this.text());
	$this.parent().prev().attr("value", $this.text());
	$this.parent().prev().attr('opotion-id',$this.attr('id'));
}
//选择餐饮地点
function selectCaterAddr(own){
	var $this = $(own);
	$this.parent().prev().val($this.text());
	$this.parent().prev().attr("value", $this.text());
	$this.parent().prev().attr('opotion-id',$this.attr('id'));
}
//填写人数失去焦点，计算总人数
function getCaterPeopleNum(own){
	countPeopleNum();
	var $this = $(own);
	var peopleNum = $this.val();
	var peopleAvg = 0;
	var Price = 0;
	var reg = new RegExp("^(0|[1-9][0-9]*)$");
	if(!peopleNum){
		$this.parent().parent().find('.people-avg').val('');
		return;
	}
	if(!reg.test(peopleNum)){
		$this.val('');
		$('.total-people').text(0);
		showInfoMessage("请正确输入就餐人数！");
		return;
	}
	if(!$this.parent().parent().find('.total-price').val()){
		Price = 0;
	}else{
		Price = $this.parent().parent().find('.total-price').val().replace(/,/g, "");
		
	}
	if(Number(peopleNum) === 0){
		$this.parent().parent().find('.people-avg').val(0);
		return;
	}
	if(!peopleNum){
		$this.parent().parent().find('.people-avg').val('');
		return;
	}else{
		peopleAvg = Price / peopleNum;
		$this.parent().parent().find('.people-avg').val(fmoney(peopleAvg));
	}
	countSaveOver();
}
//caterPeoplePrice 填写完金额，失去焦点，计算总金额，计算人均
function caterPeoplePrice(own){
   countTotalPrice();
   countSaveOver();
   var _this = $(own);
   var price = _this.val();
   var peopleNum = _this.parent().parent().find('.total-people-num').val();
   var reg = new RegExp("^[0-9]+\.{0,1}[0-9]{0,2}$");
   if(price > 999999999999999){
   		_this.val('');
   		showInfoMessage("输入金额超过了餐饮金额范围");
   		countTotalPrice();
   		countSaveOver();
   		return;
   }
   if(!price){
		price = 0;
	}else if(!reg.test(price.replace(/,/g, ""))){
		var oldVal = _this.val();
		$('.total-price').text(0);
		showInfoMessage("请正确输入餐饮金额！");
		var caterOldValur = (oldVal.replace(/,/g, "")*1).toFixed(3);
		_this.val(fmoney(caterOldValur.substring(0,caterOldValur.lastIndexOf('.')+3)));
		countTotalPrice();
  		countSaveOver();
		return;
	}else{
		_this.val(fmoney(price));
		price = price.replace(/,/g, "");
	}
	if(Number(peopleNum) === 0){
		_this.parent().parent().find('.people-avg').val(0);
		return;
	}
   if(!peopleNum){
   		_this.parent().parent().find('.people-avg').val('');
		return;
	}else{
		peopleAvg = divide(Number(price),Number(peopleNum));
		_this.parent().parent().find('.people-avg').val(fmoney(peopleAvg));
	}
}
//填写完本日预算失去焦点，计算节约超支
function countSaveOverFunt(own){
	var _this = $(own);
	var price = _this.val();
	var reg = new RegExp("^[0-9]+\.{0,1}[0-9]{0,2}$");
	if(!price){
		return;
	}
	if(!reg.test(price.replace(/,/g, ""))){
		_this.val('');
		showInfoMessage("请正确输入本日预算！");
		return;
	}
	_this.val(fmoney(price));
    countTotalPrice();
    countSaveOver();
}
//显示下下拉选项框
function showSelectWin(own){
	var _this = $(own);
	_this.parent().find(".right-select-wrap").toggle();
	_this.parent().find('.right-select-wrap').parents('tr.right-popup-table-tr').siblings().find('td.cater-type').find('.right-select-wrap').hide();
}
//显示下下拉选项框 用餐时间
function showSelectWinTime(own){
	var _this = $(own);
	_this.parent().find(".right-select-wrap-time").toggle();
	_this.parent().find('.right-select-wrap-time').parents('tr.right-popup-table-tr').siblings().find('td.cater-time').find('.right-select-wrap-time').hide();
}
//显示下下拉选项框 用餐地点
function showSelectWinAddr(own){
	var _this = $(own);
	_this.parent().find(".right-select-wrap-addr").toggle();
	_this.parent().find('.right-select-wrap-addr').parents('tr.right-popup-table-tr').siblings().find('td.cater-addr').find('.right-select-wrap-addr').hide();
}
//获取餐饮类别
function gatCaterType(){
	$.ajax({ 
	    url:'/caterInfo/queryCaterTypeList',//后台提供的接口
	    dataType:'json', //返回json格式
	    async:false,
	    success:function(data){//这个data就是后台给你的json格式的数据
	    	if(data.success){
				caterTypeList = data.caterTypeList;
				caterTypeTimeList = data.caterTimeTypeList;
				caterAddrList = data.caterAddrList;
			}else{
				showErrorMessage(data.message);
			}
	        
	    }
	});
}
//只允许输入数字和小数
function caterPriceVerify(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
}
//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/\D/g,'', ""));
}

//记载住宿提示信息
function loadHotelTip(date, own) {
	$("#inhotelInfo").text('');
	var caterId = $(own).attr("sval");
	$.ajax({ 
	    url: '/caterInfo/queryCaterAndMoneyInfo',
		type: 'post',
		data: {caterId:caterId, date:date},
		dataType: 'json',
		async: false,
	    success:function(data){//这个data就是后台给你的json格式的数据
	    	if(data.success){
				var inHotelInfoStr = data.inHotelInfoStr; //入住信息
				$("#inhotelInfo").text(inHotelInfoStr);
	    	}
	    }
	});
}