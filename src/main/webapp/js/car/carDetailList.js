$(document).ready(function() {
	//加载部门列表
	initDepartmentSelect();
	if($("#carId").val()!= ""){
		loadOneCarInfo();
	}else{
		//自动获取编号
		getCarNo();
		//隐藏删除按钮
		$("#deleteBtn").hide();
	}
	//判断权限
	if(isCarInfoReadonly) {
		$("#saveBtn").remove();
		$("#deleteBtn").remove();
		
		$("input[type='text']").attr('disabled',true);
		$("input[type='checkbox']").attr('disabled',true);
		$(".add-car-btn").attr('disabled',true).css('display','none');
	}
	
	//点击页面时，关闭下拉框
	$(document).on('click', function() {
		$("#department").hide();
	});
	
});

//定义全局变量保存原值
var carData;
//获取当前日期
function getNowFormatDate() {
	
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    $("#detailTableData tr:last-child").find("input[type=text]").eq(0).val(currentdate);
}

//获取编号
function getCarNo(){
	$.ajax({
		url: '/carManager/queryMaxCarNo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#carNo").val(response.maxCarNo+1);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}


//加载单个车辆信息
function loadOneCarInfo(){
	$.ajax({
		url: '/carManager/queryOneCarInfo',
		type: 'post',
		data: {carId: $("#carId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				carData = response;
				var carInfo = response.carInfo;
				$("#carNo").val(carInfo.carNo);
				$("#driver").val(carInfo.driver);
				$("#phone").val(carInfo.phone);
				$("#carModel").val(carInfo.carModel);
				$("#carNumber").val(carInfo.carNumber);
				$("#identityNum").val(carInfo.identityNum);
				$("#carId").val(carInfo.carId);
				$("#crewId").val(carInfo.crewId);
				$("#enterDate").val(carInfo.enterDate);
				$("#useFor").val(carInfo.useFor);
				
				var departments = carInfo.departments;
				if (departments != null && departments != '' && departments != undefined) {
					$("#departmentText").val(departments);
				}
				
				if(carInfo.totalMiles == null){
					$("#kilometersCount").text("");
				}else{
					$("#kilometersCount").text(carInfo.totalMiles);
				}
				if(carInfo.totalOil == null){
					$("#oilLitresCount").text("");
				}else{
					$("#oilLitresCount").text(fmoney(carInfo.totalOil));
				}
				if(carInfo.totalMoney == null){
					$("#oilTotalMoney").text("");
				}else{
					$("#oilTotalMoney").text(fmoney(carInfo.totalMoney));
				}
				
				if(carInfo.status == 0){
					$("#isGroup").prop("checked", false);
					$("#noHasGroup").removeClass("font-color");
					
					//离组不能添加加油登记信息
//					$(".add-car-btn").attr('disabled',true).css('display','none');
				}else{
					$("#isGroup").prop("checked", true);
					$("#noHasGroup").addClass("font-color");
				}
				var carWorks = response.carWorks;
				loadAddOilData(carWorks);
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//校验格式
function checkkilometer(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\-\d.]/g,""));  //清除“数字”和“.”“-”以外的字符
	$this.val($this.val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
	$this.val($this.val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
	$this.val($this.val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	$this.val($this.val().replace(/\-{2,}/g,"-"));
	$this.val($this.val().replace("-","$#$").replace(/\-/g,"").replace("$#$","-"));
}
//只允许输入数字和小数
function onlyNumberPointer(own){
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

//格式化数字
function formatNumber(own){
	var $this = $(own);
	if($this.val() != ""){
		var value = $this.val().replace(/,/g, "");
		$this.val(fmoney(value));
	}else{
		return;
	}
}

//汇总公里数
function summaryKilometers(){
	var flag =true;
	var kilometersCount = 0;
	$("#detailTableData tr").each(function(){
		var text = $(this).find("input[type=text]").eq(3).val();
		if (text.indexOf('-') != -1 && text.length == 1) {
			parent.showInfoMessage("公里数输入错误！");
			flag = false;
			return false;
		}
		var kilometer = $(this).find("input[type=text]").eq(3).val().replace(/,/g, "");
		if(kilometer != ""){
			kilometersCount = add(kilometersCount, kilometer);
		}
	});
	if (flag) {
//		formatNumber($this);
		$("#kilometersCount").text(kilometersCount);
	}
	
}

//只允许输入数字
function onlyNumber(own){
	$(own).val($(own).val().replace(/\D/g,""));
}


//加载加油表格数据
function loadAddOilData(carWorks){
	if(carWorks.length != 0){
		var html = [];
		for(var i= 0; i< carWorks.length; i++){
			html.push('<tr workid="'+ carWorks[i].workId +'">');
			html.push(' <td style="width: 8%; max-width: 8%; min-width:8%; padding: 5px;">'+ (i + 1) +'</td>');
			html.push(' <td style="width: 12%; max-width: 12%; min-width:12%;"><input class="date-input" type="text" sval="'+ carWorks[i].workId +'" value="'+ carWorks[i].workDate +'" readonly onclick="WdatePicker({isShowClear:false,readOnly:true})"><span class="operate-btn"><input class="delete-car-row" type="button" title="删除" onclick="deleteCarInfoRow(this)"></span></td>');
			
			if(carWorks[i].startMileage == null){
				carWorks[i].startMileage = "";
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input type="text" style="text-align:right; padding-right: 5px;" onkeyup="onlyNumber(this)"  onblur="calcuteKilometer(this,1)" value="'+ carWorks[i].startMileage +'"></td>');
			}else{
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input type="text" style="text-align:right; padding-right: 5px;" onkeyup="onlyNumber(this)"  onblur="calcuteKilometer(this,1)" value="'+ carWorks[i].startMileage +'"></td>');
			}
			
			if(carWorks[i].mileage == null){
				carWorks[i].mileage = "";
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input type="text" style="text-align:right; padding-right: 5px;" onkeyup="onlyNumber(this)"  onblur="calcuteKilometer(this,2)" value="'+ carWorks[i].mileage +'"></td>');
			}else{
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input type="text" style="text-align:right; padding-right: 5px;" onkeyup="onlyNumber(this)"  onblur="calcuteKilometer(this,2)" value="'+ carWorks[i].mileage +'"></td>');
			}
			
			if(carWorks[i].kilometers == null){
				carWorks[i].kilometers = "";
//				html.push(' <td style="width: 20%; max-width: 20%; min-width:20%;"><div class="kilometer-div">'+ carWorks[i].kilometers +'</div></td>');
				html.push(' <td style="width: 10%; max-width: 10%; min-width:10%;"><input style="text-align:right; padding-right: 5px;" type="text" onkeyup="onlyNumber(this)" onblur="summaryKilometers()" value="'+ carWorks[i].kilometers +'"></td>');
			}else{
//				html.push(' <td style="width: 20%; max-width: 20%; min-width:20%;"><div class="kilometer-div">'+ fmoney(carWorks[i].kilometers) +'</div></td>');
				html.push(' <td style="width: 10%; max-width: 10%; min-width:10%;"><input style="text-align:right; padding-right: 5px;" type="text" onkeyup="onlyNumber(this)" onblur="summaryKilometers()" value="' + carWorks[i].kilometers + '"></td>');
			}
			
			if(carWorks[i].oilLitres == null){
				carWorks[i].oilLitres = "";
				html.push(' <td style="width: 10%; max-width: 10%; min-width:10%;"><input style="text-align:right; padding-right: 5px;" type="text" onblur="oilLitresTotal(this)" onkeyup="onlyNumberPointer(this)" value="'+ carWorks[i].oilLitres +'"></td>');
			}else{
				html.push(' <td style="width: 10%; max-width: 10%; min-width:10%;"><input style="text-align:right; padding-right: 5px;" type="text" onblur="oilLitresTotal(this)" onkeyup="onlyNumberPointer(this)" value="'+ fmoney(carWorks[i].oilLitres) +'"></td>');
			}
			
			if(carWorks[i].oilMoney == null){
				carWorks[i].oilMoney = "";
				html.push(' <td><input style="text-align:right; padding-right: 5px;" type="text"  onkeyup="onlyNumberPointer(this)" value="'+ carWorks[i].oilMoney +'" onblur="oilMoneyTotal(this)"></td>');
			}else{
				html.push(' <td><input style="text-align:right; padding-right: 5px;" type="text"  onkeyup="onlyNumberPointer(this)" value="'+ fmoney(carWorks[i].oilMoney) +'" onblur="oilMoneyTotal(this)"></td>');
			}
			
			if(carWorks[i].remark == null){
				carWorks[i].remark = "";
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input style="text-align:left; padding-right: 5px;" type="text" ></td>');
			}else{
				html.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input style="text-align:left; padding-right: 5px;" type="text" value="'+ carWorks[i].remark +'"></td>');
			}
			
			html.push('</tr>');
		}
		$("#detailTableData").empty();
		$("#detailTableData").append(html.join(""));
		
		//判断权限
		if(isCarInfoReadonly) {
			$("input[type='text']").attr('disabled',true);
			$("input[type='checkbox']").attr('disabled',true);
			$(".delete-car-row").remove();
		}
	}else{
		return;
	}
}


var blankFlag = false;//空行标志
//添加一行加油信息
function addNewRecord(){
	//取出加油列表的最后一行
	var lastTr = $("#detailTableData tr:last-child");
	//取出最后一行的结束里程数
	var lastMileage = $(lastTr).find("input[type=text]").eq(2).val();
	
	var errorMessage = "";
	var htmlArray = [];
	htmlArray.push("<tr>");
	htmlArray.push('<td style="width: 8%; max-width: 8%; min-width:8%; padding:5px;"></td>');
	htmlArray.push('<td style="width: 12%; max-width: 12%; min-width:12%;"><input class="date-input" type="text" sval=""  readonly onclick="WdatePicker({isShowClear:false,readOnly:true})"><span class="operate-btn"><input class="delete-car-row" type="button" title="删除" onclick="deleteCarInfoRow(this)"></span></td>');
	htmlArray.push('<td style="width: 15%; max-width: 15%; min-width:15%;"><input style="text-align:right; padding-right: 5px;" type="text"  onkeyup="onlyNumber(this)" onblur="calcuteKilometer(this,1)"></td>');
	htmlArray.push('<td style="width: 15%; max-width: 15%; min-width:15%;"><input style="text-align:right; padding-right: 5px;" type="text"  onkeyup="onlyNumber(this)" onblur="calcuteKilometer(this,2)"></td>');
	htmlArray.push('<td style="width: 10%; max-width: 10%; min-width:10%;"><input input style="text-align:right; padding-right: 5px;" type="text" onkeyup="onlyNumber(this)" onblur="summaryKilometers()"></td>');
	htmlArray.push('<td style="width: 10%; max-width: 10%; min-width:10%;"><input style="text-align:right; padding-right: 5px;" type="text" onblur="oilLitresTotal(this)" onkeyup="onlyNumberPointer(this)"></td>');
	htmlArray.push('<td><input style="text-align:right; padding-right: 5px;" type="text" onkeyup="onlyNumberPointer(this)" onblur="oilMoneyTotal(this)"></td>');
	htmlArray.push(' <td style="width: 15%; max-width: 15%; min-width:15%;"><input style="text-align:left; padding-right: 5px;" type="text" ></td>');
	htmlArray.push('</tr>');
    var blankTr = $("#detailTableData tr.blank-tr");
    if(blankTr.length != 0){
    	$("#detailTableData").empty();
    	$("#detailTableData").append(htmlArray.join(""));
        setCarNo();
        getNowFormatDate();
    }else{
    	
    	$("#detailTableData tr:last-child").each(function(){
 		   var date = $(this).find("input[type=text]").eq(0).val();
 		   var startMileage = $(this).find("input[type=text]").eq(1).val();
 		   var mileage = $(this).find("input[type=text]").eq(2).val();
 		   var kilometers = $(this).find("input[type=text]").eq(3).val();
 		   var oilLitres = $(this).find("input[type=text]").eq(4).val();
 		   var oilMoney = $(this).find("input[type=text]").eq(5).val();
 		   if(date == "" && startMileage == '' && mileage == "" && kilometers == "" && oilLitres == "" && oilMoney == ""){
 			   blankFlag = true;
 			   return;
 		   }else if(date != "" && startMileage == '' && mileage == "" && kilometers == "" && oilLitres == "" && oilMoney == ""){
 			  blankFlag = true;
			   return;
 		   }
 		   else if(date == ""){
 			   errorMessage += "日期不能为空,";
 			   blankFlag = true;
 			   return;
 		   }else if(oilLitres == "" && oilMoney != "" || oilLitres != "" && oilMoney == ""){
 			   errorMessage += "加油升数和加油金额必须同时为空或同时不为空,";
 			   blankFlag = true;
 			   return;
 		   }
 		   else{
 			   blankFlag = false;
 		   }
 	   });
	 	if(blankFlag){
	 		parent.showInfoMessage(errorMessage + "请完善信息后再添加");
	 		return;
	 	}else{
	 		$("#detailTableData").append(htmlArray.join(""));
	 		//对开始里程数赋值
	 		$("#detailTableData tr:last-child").find("input[type=text]").eq(1).val(lastMileage);
	        setCarNo();
	        getNowFormatDate();
	 	}
    }
    
    
   
   
}
function setCarNo(){
	
	$("#detailTableData tr").each(function(i){
    	$(this).find("td").eq(0).text(i + 1);
    });
}

//删除一行加油信息
function deleteCarInfoRow(own){
	parent.popupPromptBox("提示", "是否要删除该条加油信息？", function(){
		var id = $(own).parents("tr").attr("workid");
		if(id != undefined && id != "") {
			$.ajax({
				url: '/carManager/deleteCarWorkInfo',
				type: 'post',
				data: {"workId": id},
				datatype: 'json',
				success: function(response){
					if(response.success){
						$(own).parents("tr").remove();
						parent.showSuccessMessage("删除成功");
					}else{
						parent.showErrorMessage(response.message);
					}
				}
			});
		}else{
			$(own).parents("tr").remove();
			parent.showSuccessMessage("删除成功");
		}
	});
	
}






var valugflag= false;//值是否改变标识
//关闭弹窗
function closeRightWin(){
	if(carData == undefined){
		parent.closeRightWin();
		return;
	}
	var carInfo = carData.carInfo;
	var carWorks = carData.carWorks;
	var trLength = $("#detailTableData tr").length;
	if (trLength == 1) {
		var textLength = $("#detailTableData tr").find("input[type=text]").length;
		if (textLength == 0) {
			trLength = 0;
		}
	}
	
	if($("#carNo").val() != carInfo.carNo){
		valugflag = true;
		if(($("#carNo").val() == "" && carInfo.carNo == null) || ($("#carNo").val() == "" && carInfo.carNo == "")){
			valugflag = false;
		}
	}
	
	if(!valugflag){
		if($("#driver").val() != carInfo.driver){
			valugflag = true;
			if(($("#driver").val() == "" && carInfo.driver == null) || ($("#driver").val() == "" && carInfo.driver == "")){
				valugflag = false;
			}
		}
	}
	
	if(!valugflag){
		if($("#phone").val() != carInfo.phone){
			valugflag = true;
			if($("#phone").val() == "" && carInfo.phone == null || $("#phone").val() == "" && carInfo.phone == ""){
				valugFlag = false;
			}
		}
		
	}
	
	if(!valugflag){
		if($("#carModel").val() != carInfo.carModel){
			valugflag = true;
			if($("#carModel").val() == "" && carInfo.carModel == null || $("#carModel").val() == "" && carInfo.carModel == ""){
				valugflag = false;
			}
		}
	}
	
	if(!valugflag){
		if($("#carNumber").val() != carInfo.carNumber){
			valugflag = true;
			if($("#carNumber").val() == "" && carInfo.carNumber == null || $("#carNumber").val() == "" && carInfo.carNumber == ""){
				valugflag = false;
			}
		}
	}
	
	if(!valugflag){
		
		if($("#identityNum").val() != carInfo.identityNum){
			valugflag = true;
			if($("#identityNum").val() == "" && carInfo.identityNum == null || $("#identityNum").val() == "" && carInfo.identityNum == ""){
				valugflag = false;
			}
		}
	}
	
	if(!valugflag){
		
		if($("#enterDate").val() != carInfo.enterDate){
			valugflag = true;
			if($("#enterDate").val() == "" && carInfo.enterDate == null || $("#enterDate").val() == "" && carInfo.enterDate == ""){
				valugflag = false;
			}
		}
	}
	
	if(!valugflag){
		
		if($("#useFor").val() != carInfo.useFor){
			valugflag = true;
			if($("#useFor").val() == "" && carInfo.useFor == null || $("#useFor").val() == "" && carInfo.useFor == ""){
				valugflag = false;
			}
		}
	}
	
	if (!valugflag) {
		if ($("#departmentText").val() != carInfo.departments) {
			valugflag = true;
			if($("#departmentText").val() == "" && carInfo.departments == null || $("#departmentText").val() == "" && carInfo.departments == ""){
				valugflag = false;
			}
		}
	}
	
	var status = true;
	if($("#isGroup").is(":checked")){
	}else{
		status = false;
	}
	if(status != carInfo.status){
		valugflag = true;
	}
	if(trLength != carWorks.length){//长度不相等相当于改变加油登记表，不在做其他处理
		valugflag = true;
	}else{//精确比较每行的值
		var trObj = $("#detailTableData tr");
		for(var i= 0; i< carWorks.length; i++){
			for(var j=0; j< trObj.length; j++){
				if(i == j){
					if($(trObj[j]).find("input[type=text]").eq(0).val() != carWorks[i].workDate){//日期
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(1).val().replace(/,/g, "") != carWorks[i].startMileage){//开始里程数
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(2).val().replace(/,/g, "") != carWorks[i].mileage){//结束工作里程
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(3).val().replace(/,/g, "") != carWorks[i].kilometers){//公里数
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(4).val().replace(/,/g, "") != carWorks[i].oilLitres){//加油升数
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(5).val().replace(/,/g, "") != carWorks[i].oilMoney){//加油金额
						valugflag = true;
					}
					if($(trObj[j]).find("input[type=text]").eq(6).val().replace(/,/g, "") != carWorks[i].remark){//加油金额
						valugflag = true;
					}
				}
			}
		}
	}
	
	
	
	if(valugflag){
		parent.tipInfoBox("提示","您修改了信息，是否要保存？", function (){
			saveOneCarInfo();//保存
	    });
	}else{
		parent.closeRightWin();
	}
}

//删除单个车辆信息
function deleteCarInfo(){
	 parent.popupPromptBox("提示", "是否要删除车辆信息？", function() {
		 $.ajax({
			 url: '/carManager/deleteCarInfo',
			 type: 'post',
			 data: {carId: $("#carId").val()},
			 datatype: 'json',
			 success: function(response){
				 if(response.success){
					 parent.showSuccessMessage("删除成功");
					 parent.closeRightWin();
				 }else{
					 parent.showErrorMessage(response.message);
				 }
			 }
		 });
	 });
}

//是否在组
function isHasGroup(own){
	var $this = $(own);
	if($this.is(":checked")){
		$("#noHasGroup").addClass("font-color");
		$("#hasGroup").removeClass("font-color");
	}else{
		$("#hasGroup").addClass("font-color");
		$("#noHasGroup").removeClass("font-color");
	}
}




//计算公里数
function calcuteKilometer(own, source){
	var $this = $(own);
	var trObj = $this.parents("tr");
	
	var startMileage = 0.0;
	var mileage = 0.0;
	//判断点击的是开工还是结束
	if (source == 1) {
		//开工里程数
		if ($this.val() != '' && $this.val() != undefined) {
			startMileage = $this.val();
		}
		//判断收工里程数是否有值
		var mileageStr = trObj.find("input[type=text]").eq(2).val().replace(/,/g, "");
		if (mileageStr != '' && mileageStr != undefined) {
			mileage = mileageStr;
		}
		
	}else if (source == 2) {
		//结束里程数
		if ($this.val() != '' && $this.val() != undefined) {
			mileage = $this.val();
		}
		
		//判断开工里程数
		var startMileageStr = trObj.find("input[type=text]").eq(1).val().replace(/,/g, "");
		if (startMileageStr != '' && startMileageStr != undefined) {
			startMileage = startMileageStr;
		}
	}
	
	var kilometers = 0.0;
	//如果开工里程书为空时，需要取出上一行的收工里程数，与这一行的收工里程数相减得出公里数
	if (startMileage == 0.0) {
		//取出上一行的收工里程数
		var preTr = $(trObj).prev();
		if (preTr != null && preTr != undefined && preTr.length > 0) {
			var preMileage = $(preTr).find("input[type=text]").eq(2).val().replace(/,/g, "");
			if (preMileage == '' || preMileage == undefined) {
				preMileage = 0.0;
			}
			
			if (preMileage != 0.0) {
				kilometers = subtract(mileage, preMileage);
			}
		}
	}else {
		//计算公里数
		kilometers = subtract(mileage, startMileage);
	}
	if (kilometers < 0) {
		kilometers = 0;
	}
	trObj.find("input[type=text]").eq(3).val(kilometers);
	
	//计算公里数总和
	summaryKilometers();
	
}

//计算加油升数
function oilLitresTotal(own){
	var $this = $(own);
	var tableObj = $("#detailTableData tr");
	var oilTotal=0;
	$.each(tableObj, function(){
		if($(this).find("input[type=text]").eq(4).val() == ""){
			oilTotal = add(oilTotal, 0);
		}
		oilTotal = add(oilTotal, $(this).find("input[type=text]").eq(4).val().replace(/,/g, ""));
	});
	$("#countTable tr:last-child").find("#oilLitresCount").eq(0).text(fmoney(oilTotal));
	formatNumber($this);
}
//计算加油金额
function oilMoneyTotal(own){
	var $this = $(own);
	if($this.val() != ""){
		$this.val(fmoney($this.val()));
	}
	
	var tableObj = $("#detailTableData tr");
	var oilMoneyTotal=0;
	
	$.each(tableObj, function(){
		if($(this).find("input[type=text]").eq(5).val() == ""){
			oilMoneyTotal = add(oilMoneyTotal, 0);
		}
		oilMoneyTotal = add(oilMoneyTotal, $(this).find("input[type=text]").eq(5).val().replace(/,/g, ""));
	});
	$("#countTable tr:last-child").find("#oilTotalMoney").eq(0).text(fmoney(oilMoneyTotal));
}



//保存单个车辆信息
function saveOneCarInfo(){
	var flag = false;
	var carWorkStr = "";//加油登记信息
	var carWorkStrArray = [];
	var tableObj = $("#detailTableData");
	var blankTr = $("#detailTableData tr.blank-tr");
	var isExist = false;
	
	if($("#carNo").val() != ""){
		//检测编号是否被占用
		$.ajax({
			url: '/carManager/isExistCarNo',
			type: 'post',
			async: false,
			data: {carNo: $("#carNo").val(), carId: $("#carId").val()},
			datatype: 'json',
			success: function(response){
				if(response.success){
					if(response.isExist){
						isExist = true;
						return;
					}
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	}
	if(blankTr.length != 0){//没有数据
		carWorkStr = "";
		var subData = {};
		subData.carNo = $("#carNo").val();
		if($("#carNo").val() == ""){
			   parent.showInfoMessage("汽车编号不能为空");
			   return;
		}
		
		if ($("#carNumber").val() == "" || $("#carNumber").val() == undefined) {
			 parent.showInfoMessage("车牌号码不能为空");
			  return;
		}
		if(isExist){ 
			parent.showInfoMessage("该汽车编号已经存在，请重新填写");
			return;
		}
		subData.carId = $("#carId").val();
	    subData.crewId = $("#crewId").val();
	    subData.driver = $("#driver").val();
	    subData.phone = $("#phone").val();
	    subData.carModel = $("#carModel").val();
	    subData.carNumber = $("#carNumber").val();
	    subData.useFor = $("#useFor").val();
	    subData.identityNum = $("#identityNum").val();
	    subData.enterDate = $("#enterDate").val();
	    subData.departments = $("#departmentText").val();;
	    if($("#isGroup").is(":checked")){
	    	subData.status = 1;
	    }else{
	    	subData.status = 0;
	    }
	    subData.carWorkStr = carWorkStr;
	    saveMethod(subData);
	}else{//有数据
		var trObj = tableObj.find("tr");
		var errorMessage = "";
		if(!flag){
			$.each(trObj, function(i){
				var carWorkStr = "";
			       i++;
			       //日期
		 		   var date = $(this).find("input[type=text]").eq(0).val();
		 		   //加友信息id
		 		   var workId = $(this).find("input[type=text]").eq(0).attr("sval");
		 		   if (workId == undefined) {
					workId = '';
				}
		 		   //开工里程表数
		 		   var startMileage =  $(this).find("input[type=text]").eq(1).val().replace(/,/g, "");
		 		   var mileage = $(this).find("input[type=text]").eq(2).val().replace(/,/g, "");
//		 		   var kilometers = $(this).find("div.kilometer-div").text().replace(/,/g, "");
		 		  var kilometers = $(this).find("input[type=text]").eq(3).val().replace(/,/g, "");
		 		   var oilLitres = $(this).find("input[type=text]").eq(4).val().replace(/,/g, "");
		 		   var oilMoney = $(this).find("input[type=text]").eq(5).val().replace(/,/g, "");
		 		  var remark = $(this).find("input[type=text]").eq(6).val().replace(/,/g, "");
		 		  
		 		   if(date == "" && startMileage == '' && mileage == "" && kilometers == "" && oilLitres == "" && oilMoney == ""){
		 			   carWorkStr += "";
		 		   }else if(date != "" && startMileage == '' && mileage == "" && kilometers == "" && oilLitres == "" && oilMoney == ""){
		 			  carWorkStr += "";
		 		   }else if(date == ""){
		 			   flag = true;
		 			   errorMessage += "日期不能为空,";
		 		   }else if(oilLitres == "" && oilMoney != ""){
		 			   flag = true;
		 			   errorMessage += "加油升数和加油金额必须同时为空或同时不为空,";
		 		   }else if(oilLitres != "" && oilMoney == ""){
		 			   flag = true;
		 			   errorMessage += "加油升数和加油金额必须同时为空或同时不为空,";
		 		   }else if (kilometers.indexOf('-') != -1 && kilometers.length == 1) {
		 			  errorMessage += "公里数输入错误！";
		 			  flag = true;
		 		   }else{
//		 			   flag = false;
		 			   carWorkStr += date +",";
		 			   carWorkStr += startMileage +",";
		 			   carWorkStr += mileage +",";
		 			   carWorkStr += kilometers + ",";
		 			   carWorkStr += oilLitres +",";
		 			   carWorkStr += oilMoney +",";
		 			   carWorkStr += remark + ",";
		 			   carWorkStr += workId;
		 			   carWorkStrArray.push(carWorkStr);
		 		   }
			});
			if(errorMessage != "" && flag){
				parent.showInfoMessage(errorMessage);
				return;
			}else{
				
				var subData = {};
				if(isExist){
					parent.showInfoMessage("该汽车编号已经存在，请重新填写");
					return;
				}
				subData.carNo = $("#carNo").val();
				if($("#carNo").val() == ""){
					   parent.showInfoMessage("汽车编号不能为空");
					   return;
				 }
				if ($("#carNumber").val() == "" || $("#carNumber").val() == undefined) {
				 	parent.showInfoMessage("车牌号码不能为空");
				   return;
				}
				subData.carId = $("#carId").val();
			    subData.crewId = $("#crewId").val();
			    subData.driver = $("#driver").val();
			    subData.phone = $("#phone").val();
			    subData.carModel = $("#carModel").val();
			    subData.carNumber = $("#carNumber").val();
			    subData.useFor = $("#useFor").val();
			    subData.identityNum = $("#identityNum").val();
			    subData.enterDate = $("#enterDate").val();
			    subData.departments =  $("#departmentText").val();
			    if($("#isGroup").is(":checked")){
			    	subData.status = 1;
			    }else{
			    	subData.status = 0;
			    }
			    subData.carWorkStr = carWorkStrArray.join("##");
			    saveMethod(subData);
			}
			
			
		}else{
			return;
		}
		
	};
}

//保存方法
function saveMethod(subData){
	$.ajax({
		url: '/carManager/saveOrUpdateCarInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				parent.showSuccessMessage("保存成功");
				parent.closeRightWin();
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//初始化当前剧组的部门列表
function initDepartmentSelect() {
	$.ajax({
		url: '/carManager/queryCrewDepartmentList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var departmentList = response.departmentList;
				var departmentArr = [];
				//添加部门下拉框
				for(var i =0; i < departmentList.length; i++){
					departmentArr.push("<li value="+ departmentList[i].roleName + " onclick='selectDepartment(this,event)'>" + departmentList[i].roleName + "</li>");
				}
				$("#department").append(departmentArr.join(''));
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//显示部门列表
function showDepartment(ev) {
	$("#department").show();
	ev.stopPropagation();
}

//选择部门
function selectDepartment(own, ev) {
	//取出选中的值
	var department = $(own).text();
	//对文本框赋值
	$("#departmentText").val(department);
	//关闭下拉框
	$("#department").hide();
	ev.stopPropagation();
}

var inputIndex;
//检索住宿人员
function searchDepartmentName(own){
	var $this = $(own);
	inputIndex = $(own);
	if($this.val() != ""){
		var _subList = $("#department").children("li");
		_subList.hide();
		var searchFlag = false;
		$.each(_subList, function(){
			if($(this).text().search($.trim($this.val()))!=-1){
      	   searchFlag = true;
             $(this).show();
             $("#department").show();
         }else{
      	
             $(this).hide();
//             $("#searchPersonList").hide();
         }
		});
		
		if (!searchFlag) {
			$("#department").hide();
	    }
	}else{
		$("#department").show();
		$.each($("#department").children("li"), function(){
             $(this).show();
		});
	}
}