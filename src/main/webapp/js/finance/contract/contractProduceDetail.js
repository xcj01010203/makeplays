$(document).ready(function() {
	//获取货币列表
	getcurrencyId();
	//基本信息校验
	checkOutIsEmpty();
	//加载财务科目
	loadFinanceSubject();
	
	$(document).click(function(){
		//隐藏财务科目
    	$('.fin_subj').css("display", "none");
    	//隐藏下拉菜单
    	$('.dropdown_box').hide();	
    	
    });	
	$("#filter").on("click", function(ev){
		ev.stopPropagation();
	});
	//支付方式显示
	paymentMethod();
	//添加、删除一行
	addPayMethodLine();
	//初始化上传
	showUpLoadFileList();
	//初始化修改合同页面
	initProducePage();
	
	$("#myLoader").dimmer({
		closable: false
	});
	
});


//定义附件包的ID
var attpackId;


function dropBox(data, id, left, top){
	//清空下拉框里的所有子元素
	$(id).empty();
	if(data != null && data.length != 0){
		var _li = [];
		for(var i = 0; i < data.length; i++){
			_li.push('<li class="drop-down-li"><a href="javascript:void(0)">'+ data[i] +'</a></li>');
		}
		
		$(id).append($(_li.join("")));
			
	}
		
	$(id).css({"left":left,"top":top});
	
	
}


//选择公司名称
function selectCompanyName(own){
	own= $(own);
	$.ajax({
		url: '/contractProduce/queryDropDownList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var companyList= response.companyList;
				if(companyList.length != 0){
					own.next("ul.dropdown_box").find("li").remove();
					$.each(companyList, function(i,value){
						own.next("ul.dropdown_box").append("<li class='drop-down-li' title='"+ value +"'><a href='javascript:void(0)'>" + value + "</a></li>");
					});
					own.next("ul.dropdown_box").css({left: own.position().left, top: own.position().top+own.outerHeight()}).show();
				}
				
				$("#sonCompany").keyup(function(){
				       var _this=$(this), _subList=_this.siblings('ul').children('li');
				       var searchFlag = false;
				       _subList.each(function(){
				    	   
				           if($(this).text().search($.trim(_this.val()))!=-1){
				        	   searchFlag = true;
				        	   _this.siblings("ul.dropdown_box").show();
				               $(this).show();
				           }else{
				               $(this).hide();
				           }
				       });
				       
				       if (!searchFlag) {
				    	   _this.siblings("ul.dropdown_box").hide();
				       }
				   });
				
					 
				 $('.dropdown_box li').off('click');
				 $('.dropdown_box').on('click','li',function(ev){
					  var _this=$(this);
					  $('#sonCompany').val($(this).text());
					  //清楚错误提示
					  $(".name-error-tips").hide();
					  $("#sonCompany").removeClass("produce-name-tips");
					  _this.parent().hide();
				 });
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}


//获取币种列表
function getcurrencyId(){
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var currencyInfoList = response.currencyInfoList;
				var html = [];
				for(var i = 0; i < currencyInfoList.length; i++){
					if(currencyInfoList[i].ifEnable) {
						html.push("<option value='" + currencyInfoList[i].id + "'>" + currencyInfoList[i].name +"</option>");
					}
				}
				$("#sonCurrencyId").append($(html.join("")));
				
			}
		}
	});
}



//基本信息校验事件
function checkOutIsEmpty(){
	//名称
	$("#sonCompany").on("blur", function(){
		if($(this).val() == ""){
			$(".name-error-tips").show();
			$(this).addClass("produce-name-tips");
		}
	}).on("focus", function(){
		$(".name-error-tips").hide();
		$(this).removeClass("produce-name-tips");
	});
	//总金额
	$("#sonTotalMoney").on("blur", function(){
		if($(this).val() == ""){
			$(".total-error-tips").show();
			$(this).addClass("produce-name-tips");
			$(this).next("input[type=hidden]").val("");
		}else{
			checkIsMoney(this);
//			var totalMoney = $("#sonTotalMoney").val();
//			$(this).val(fmoney(totalMoney));
			
			$(this).next("input[type=hidden]").val($.trim($("#sonTotalMoney").val().replace(/,/g,""))-0);
		}
	}).on("focus", function(){
		$(".total-error-tips").hide();
		$(this).removeClass("produce-name-tips");
//		if($(this).val()!= ""){
//			var totalMoney = $(this).val().replace(/,/g, "")-0;
//			$(this).val(totalMoney);
//		}
	}).keyup(function(){    
//		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
//		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
//		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
//		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	
	//合同日期
	$("#sonContractDate").on("blur", function(){
		if($(this).val() == ""){
			$(".contract-date-error-tips").show();
			$(this).addClass("produce-name-tips");
		}
	}).on("focus", function(){
		$(".contract-date-error-tips").hide();
		$(this).removeClass("produce-name-tips");
		
	});
}



//加载财务科目
//加载财务科目
function loadFinanceSubject(){
	var source = {
			url: '/financeSubject/querySubjectList',
			datatype: 'json',
			dataFields: [
			    {name: 'id',type: 'string'},
			    {name: 'name',type: 'string'},
			    {name: 'level',type: 'int'},
			    {name: 'parentId',type: 'string'},
			],
			hierarchy:{
				keyDataField: {name:'id'},
				parentDataField: {name:'parentId'}
			},
			id: 'id'	
		};
	 var dataAdapter = new $.jqx.dataAdapter(source);

	 $("#subjectTree").jqxTreeGrid({
		 width: 220,
		 height:200,
		 source: dataAdapter,
	     /*filterable: true,*/
	     //filterMode:'advanced',
		 showHeader: false,
		 ready: function(){},
		 columns: [
		          { text: '财务科目', dataField: 'name', width: 200, align: "center" }
		        ]
    });
	 
	 $('#subjectTree').on('rowSelect', function (event){
       
       var args = event.args;
       var key = args.key;
       var row = $("#subjectTree").jqxTreeGrid('getRow', key);
       
       if(row.expanded == true){
             $("#subjectTree").jqxTreeGrid('collapseRow', key);
       }else{
             $("#subjectTree").jqxTreeGrid('expandRow', key);
       }
       $("#subjectTree").jqxTreeGrid('clearSelection');
       var records = row.records;
//       console.log(row);
       if(records == undefined){
//      	 	var text = $("input[name=subval]").val();
              var subjectName = row.name;
              var b = true;
              var par = row;
              while(b){
	                if(par.parent != undefined){
	                	subjectName += "-" + par.parent.name;
	                 	par = par.parent;
	                }else{
	                 	b = false;
	                }
              }
		          var names = subjectName.split("-");
		          var name = "";
		          for(var i = names.length-1;i>=0;i--){
		              name += names[i];
		              if(i != 0){
		                  name += "-";
		              }
		          }
		          $("#sonFinanceSubjName").val(name);
		          $("#sonFinanceSubjId").val(row.id);
		          $('.fin_subj').css("display", "none");
		          $("#clearFinanceSubj").show();
           }
      });
	 
	 $("#filterBtn").click(function(ev){
            var name = $("#filter").val();
		      var filtertype = 'stringfilter';
		      // create a new group of filters.
		      var filtergroup = new $.jqx.filter();
		      var filter_or_operator = 1;
		      var filtervalue = name;
		      var filtercondition = 'CONTAINS';
		      var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
		      filtergroup.addfilter(filter_or_operator, filter);
		      // add the filters.
		      $("#subjectTree").jqxTreeGrid('addFilter', 'name', filtergroup);
		      // apply the filters.
		      $("#subjectTree").jqxTreeGrid('applyFilters');
		      $("#subjectTree").jqxTreeGrid('expandAll');
		      ev.stopPropagation();
		});
		
		$("#filter").keydown(function(ev){
			if(ev.keyCode == 13) {
				var name = $("#filter").val();
				var filtertype = 'stringfilter';
			      // create a new group of filters.
			      var filtergroup = new $.jqx.filter();
			      var filter_or_operator = 1;
			      var filtervalue = name;
			      var filtercondition = 'CONTAINS';
			      var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
			      filtergroup.addfilter(filter_or_operator, filter);
			      // add the filters.
			      $("#subjectTree").jqxTreeGrid('addFilter', 'name', filtergroup);
			      // apply the filters.
			      $("#subjectTree").jqxTreeGrid('applyFilters');
			      $("#subjectTree").jqxTreeGrid('expandAll');
			}
		});
		
	    $('#sonFinanceSubjName').on("click", function(ev){
	    	var obj = $(this);
	    	$('.fin_subj').css({left:obj.offset().left-3,top:obj.offset().top+32, "display": "block"});
	    	ev.stopPropagation();
		});
		
}


//清空财务科目
function clearFinanceSubj(){
	 $("#sonFinanceSubjName").val("");
     $("#sonFinanceSubjId").val("");
     $("#clearFinanceSubj").hide();
}


//当改变总金额的值时
function changePaymentMoney(own){
	var $this = $(own);
	var selValue = $("input[name=payMethod]:checked").val();
	if(selValue == 1){
		if($this.val() != ""){
			
			var totalMoney = $.trim($this.val().replace(/,/g,""));
			$("#byMonthOrByStage tr.byStageTr").each(function(){
				var payedRange = $.trim($(this).find("input[type=text]").eq(1).val().replace(/,/g,""));
				payedRange = payedRange-0;
				$(this).find("input[type=text]").eq(2).val(fmoney(payedRange/100*totalMoney,2));
			});
		}else{
			$("#byMonthOrByStage tr.byStageTr").each(function(){
				$(this).find("input[type=text]").eq(2).val("");
			});
		}
	}
	
	
}



//支付方式 百分比  光标失去时根据总金额计算
function percentCalculation(obj){
	var totalMoney = $.trim($("#sonTotalMoney").val().replace(/,/g,""));
	var val = $.trim($(obj).val()).replace(/,/g,"");
	if(totalMoney !="" && val!=undefined && val != "" && !isNaN(val)){
		$(obj).next("input").val(fmoney(multiply(divide(val, 100), totalMoney),2));
	}
	var payRangePercent = 0;
	$(".byStageTr").each(function(){
		var pagePercent = $(this).find("input[type=text]").eq(1).val();
		if(pagePercent != ""){
			payRangePercent = add(payRangePercent, pagePercent);
		}
	});
	if(payRangePercent > 100){
		parent.showInfoMessage("分阶段总期数已经超过100%!");
	}
}


//设置货币格式
function moneyFormat(obj){
	var str_m = ($.trim($(obj).val())).replace(new RegExp(',','gm'),'');
	if(!$.isNumeric(str_m)){
			$(obj).val('');
			return false;
		}
	$(obj).val(fmoney(str_m,2));
}

//设置title
function setTitle(obj) {
	$(obj).attr("title",$(obj).val());
}

function checkNum(obj) {  
    //检查是否是非数字值  
    if (isNaN(obj.value)) {  
        obj.value = "";  
    }  
    if (obj != null) {  
        //检查小数点后是否对于两位http://blog.csdn.net/shanzhizi  
        if (obj.value.toString().split(".").length > 1 && obj.value.toString().split(".")[1].length > 2) {  
        	showErrorMessage("小数点后多于两位！");  
            obj.value = "";  
        }
    }  
}
//校验是否是金额
function checkIsMoney(obj) {
	var value = $.trim(obj.value).replace(/,/g,"");
    //检查是否是非数字值  
    if (isNaN(value)) {  
        obj.value = "";
        return;
    }  
    if (obj != null) {  
        //检查小数点后是否对于两位http://blog.csdn.net/shanzhizi  
        if (value.toString().split(".").length > 1 && value.toString().split(".")[1].length > 2) {  
        	parent.showErrorMessage("小数点后多于两位！");  
            obj.value = "";
            return;
        }
    }
    if (value != "") {
    	obj.value = fmoney(obj.value, 2);
    }
}
//校验是否是数字，不带小数点
function checkMonthDayInput(obj) {  
    //检查是否是非数字值  
    if (isNaN(obj.value)) {  
        obj.value = "";  
    }  
    if (obj != null) {  
        //检查小数点后是否对于两位http://blog.csdn.net/shanzhizi  
        if (obj.value.toString().split(".").length > 1) {  
            obj.value = "";  
        }
    }
    if (obj.value > 31 || obj.value < 1) {
    	obj.value = "";
    }
}

function fmoney(s, n)   
{   
   n = n > 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(),   
   r = s.split(".")[1];   
   t = "";   
   for(var i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }   
   return t.split("").reverse().join("") + "." + r;   
}


//支付方式显示
function paymentMethod(){
	$(".spanClass").each(function(i){
		var value = this.innerHTML;
		var back =changeNumToChinese(value);
		this.innerHTML=back;
	});
	//初始化
	$("input[name=payMethod]:eq(0)").attr("checked",true);
	
	$("input[name='payMethod']").on("click", function(){
		
		var radioValue = $("input[name='payMethod']:checked").val();
		$("table.produce-pay-method").find("#byMonthOrByStage tr:not(:first)").remove();//删除 除了第一行的所有数据
		
	    var selValue = radioValue;
  		/*$('#paymenttd').html('');*/
	    $(".payment-td").html("");
	    var paymentContent = "";
		if(selValue == 1){
			paymentContent += '<input type="hidden" value="" />';
			paymentContent += '<span>第</span>';
			paymentContent += '<span class="spanClass" id="1">一</span>';
			paymentContent += '<span>期: <input class="stage-info" type="text" maxlength="180"  title="">,甲方向乙方支付酬金总额的';
			paymentContent += '    <input class="stage-money-part" type="text" maxlength="10" onblur="percentCalculation(this)">%,即';
			paymentContent += '    <input type="text" maxlength="10" onkeyup="" onblur="checkIsMoney(this)" class="stage-money">元整。';
			paymentContent += '</span>';
			paymentContent += '<span class="remind-tips">预计付款日期:</span>';
			paymentContent += '<input class="date-select remind-input" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">';  

  			$(".month-pay-detail-tr").hide();
  			$("#payDetailRemark").hide();
  		}
		if(selValue == 2){
			paymentContent += '<input type="hidden" value="" />';
  			paymentContent += '<span>';
  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按月支付酬金，每月金额为';
  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
  			paymentContent += '</span>';
  			
  			$(".month-pay-detail-tr").show();
  			loadMonthPayDetailGrid();
  			$("#payDetailRemark").show();
  		}
		if(selValue == 3){
			paymentContent += '<input type="hidden" value="" />';
  			paymentContent += '<span>';
  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
  			paymentContent += '</span>';
  			
  			$(".month-pay-detail-tr").show();
  			loadMonthPayDetailGrid();
  			$("#payDetailRemark").show();
  		}
		if(selValue == 4){
			paymentContent += '<input type="hidden" value="" />';
  			paymentContent += '<span>';
  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">每';
  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">天结算一次';
  			paymentContent += '</span>';
  			
  			$(".month-pay-detail-tr").show();
  			loadMonthPayDetailGrid();
  			$("#payDetailRemark").show();
  		}
		$(".payment-td").append(paymentContent);
		//给定默认日期
		$("table.produce-pay-method").find("#byMonthOrByStage tr:first").find("td.payment-td").find("input.date-select").attr("onClick","WdatePicker({readOnly:true, isShowClear:false})");
		$("table.produce-pay-method").find("#byMonthOrByStage tr:first").find("td.payment-td").find("input.date-select").val("");
	});
}

/*将中文的 数字转换成阿拉伯数字*/
function zhToNum(num) {  
	  var strOutput = "";  
	  var strUnit = '千百十个';  
	  num = num+"";
	  strUnit = strUnit.substr(strUnit.length - num.length);  
	  for (var i=0; i < num.length; i++)  
	    strOutput += '零一二三四五六七八九'.substr(num.substr(i,1),1) + strUnit.substr(i,1);
	  if(strOutput.substr(0,2) == "一十")
		  strOutput = strOutput.substring(1); 
	    return strOutput.replace(/零[仟佰拾]/g, '零').replace(/零{2,}/g, '零').replace(/零+个/, '个').replace(/^元/, "零元").replace(/个/g, '');  
	};

/*数字转中文*/
	function changeNumToChinese(num){
	    var cnNums = new Array("零","一","二","三","四","五","六","七","八","九"); //汉字的数字
	    var cnIntRadice = new Array("","十","百","千"); //基本单位
	    var cnIntUnits = new Array("","万","亿","兆"); //对应整数部分扩展单位
	    var ChineseStr=""; //输出的中文金额字符串
	    num = num.toString(); //转换为字符串;
        zeroCount = 0;
        var IntLen = num.length;
        for(var i=0;i<IntLen;i++ ){
            n = num.substr(i,1);
            p = IntLen - i - 1;
            q = p / 4;
            m = p % 4;
            if( n == "0" ){
                zeroCount++;
            }else{
                if( zeroCount > 0 ){
                    ChineseStr += cnNums[0];
                }
                zeroCount = 0; //归零
                ChineseStr += cnNums[parseInt(n)]+cnIntRadice[m];
            }
            if( m==0 && zeroCount<4 ){
                ChineseStr += cnIntUnits[q];
            }
        }
	    //整型部分处理完毕
	    return ChineseStr;
	}



//添加、删除一行
function addPayMethodLine(){
	
	
	/*var clockid =1;*/
	//添加一行
	$('#addition').on("click",function(){
		
		//判断是否选择支付日期
		var remindTime= $(this).parent("div").parent("td").siblings("td.payment-td").eq(0).find("input.date-select").eq(0).val();
		if(remindTime == ""){
			parent.showErrorMessage("请选择支付日期");
			return;
		}
		
		var setValue = $("input[name=payMethod]:checked").val();
		if(setValue == 1){
			
			var _tr =$(this).parent('div').parent('td').parent('tr');
			var _trCopy = _tr.clone(true);
			_trCopy.find(".date-select").attr("onClick","WdatePicker({readOnly:true,isShowClear:false})");
			_trCopy.find("input").val("");
			_tr.after(_trCopy); 
			
			$('#byMonthOrByStage .spanClass').each(function(i){
				$(this).text(changeNumToChinese(i+1));
				$(this).attr('id',i+1);
			});
		}
	    if(setValue == 2 || setValue == 3 || setValue == 4){
	    	var payInfoTd = $(this).parent("div").parent("td").siblings("td.payment-td").eq(0);
	    	var monthMoney= payInfoTd.find("input.month-money").val();
	    	var startDate= payInfoTd.find("input.start-date").val();
	    	var endDate= payInfoTd.find("input.end-date").val();
	    	var monthPayDay= payInfoTd.find("input.month-pay-day").val();
	    	
			if(monthMoney == "" || startDate == "" || endDate == "" || monthPayDay == "") {
				parent.showErrorMessage("请完善支付信息");
				return;
			}
	    	
			var _trf=$(this).parent('div').parent('td').parent('tr');
			var _copyTr = _trf.clone(true);
			_copyTr.find("input").val("");
			
			var newStartDate = getNewDay(new Date(endDate), 1).Format("yyyy-MM-dd");
			var newEndDate = getNewDay(new Date(endDate), 30).Format("yyyy-MM-dd");
			_copyTr.find("input.start-date").val(newStartDate);
			_copyTr.find("input.end-date").val(newEndDate);
//			_copyTr.find("input.start-date").attr("onClick","WdatePicker({minDate:'"+ newStartdate +"'})");
//			_copyTr.find("input.end-date").attr("onClick","WdatePicker({minDate:'"+ newStartdate +"'})");
			_copyTr.find("input.month-pay-day").val(monthPayDay);
			
			_trf.after(_copyTr);
		}
	});	
	//删除一行
	$('.subtraction').click(function(){
		var selValue = $("input[name=payMethod]:checked").val();
		var allTr = $("#byMonthOrByStage tr");
		if(allTr.length == 1){
			parent.showErrorMessage('最后一条记录不能删除');
	        return false;
		}
		var partId = $(this).parent('div').parent('td').parent('tr').find('input[type=hidden]').val();
		var flag = false;
		$.ajax({
			url: '/contractToPaidController/queryContractToPaidListById',
			type: 'post',
			data: {id: partId},
			datatype: 'json',
			async:false,
			success: function(response){
				if(response.success){
					var list = response.contractToPaidMap;
					if(list&&list.length>0){
						flag = true;
					}
				}
			}
		});
		if(flag){
			var obj = $(this).parent('div').parent('td').parent('tr');
			parent.popupPromptBox("提示","该条信息存在合同待付信息，是否删除该条信息？", function (){
				obj.remove();
				if(selValue == 1){
					$('#byMonthOrByStage .spanClass').each(function(i){
						$(this).text(changeNumToChinese(i+1));
						$(this).attr('id',i+1);
					});
				}
			});
		}else{
			$(this).parent('div').parent('td').parent('tr').remove();
			if(selValue == 1){
				$('#byMonthOrByStage .spanClass').each(function(i){
					$(this).text(changeNumToChinese(i+1));
					$(this).attr('id',i+1);
				});
			}
		}
	});
}

function showUpLoadFileList(){
	uploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/contractManager/uploadAttachment',
		timeout: 30*60*1000,//超时
		pick : '#uploadFileBtn',
		threads: 5
	});
	
	
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		parent.showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var fileUl = $("#showFileRealNameAndSaveId");
		var $li = $("<li class='upload-file-list-li'></li>");
		uploader.makeThumb( file, function( error, ret ) {
			var suffix = file.ext.toLowerCase();
			
			if(suffix == "doc" || suffix == "docx"){
				$li.append("<img alt='' src= '../images/word.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showFileRealNameAndSaveId").append($li);
			}
			else if(suffix == "pdf"){
				$li.append("<img alt='' src= '../images/pdf.jpg' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
				$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
				$("#showFileRealNameAndSaveId").append($li);
			}
			else if(error){
	            $li.html("预览错误");
	            $("#showFileRealNameAndSaveId").append($li);
	        }else{
	        	$li.append("<img alt='' src='" + ret + "' title='"+ file.name +"'/><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#showFileRealNameAndSaveId").append($li);
	        }
	    });
	});
	
	// 当有文件添加进来的时候
	uploader.on('beforeFileQueued', function(file) {
		var ext = file.ext.toLowerCase();
		var type = file.type;
		if (ext != "doc" && ext != "docx" && ext != "pdf" && type.indexOf("image") == -1) {
			return false;
		}
		return true;
	});
	
	uploader.on('uploadFinished', function(file) {
		closeRightPopup();
	});
	
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	
	uploader.on('uploadFinished', function(file) {
		$('#myLoader').dimmer("hide");
		closeRightPopup();
		parent.showSuccessMessage("操作成功");
	});
	
}


//删除未上传的文件附件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	uploader.removeFile(fileId, true);
	own.parent("li").remove();
}

//删除上传成功的文件附件
function deleteUploadedFile(ev,own,attId){
	//只读权限，不能进行删除
	if(isContractReadonly) {
		ev.stopPropagation();
		return false;
	}
	
	own= $(own);
	parent.popupPromptBox("提示","是否删除该附件？", function () {
		$.ajax({
	    	url:'/attachmentManager/deleteAttachment',
	    	type:'post',
	    	dataType:'json',
	    	data:{attachmentId: attId},
	    	success:function(data){
	    		if(data.success){
	    			own.parent("li").remove();
	    			parent.showSuccessMessage("删除成功！");
	    		}else{
	    			parent.showErrorMessage('删除附件失败');
	    		}
	    		
	    	}   	
		});
    });
	ev.stopPropagation();
}







////获取支付信息的方法
function getPayMoneyInfo(){

	 var paymentTerm = []; //支付条件
 	//支付方式
 /*  var payWay = 1;*/
   var selValue = $("input[name=payMethod]:checked").val();
   var payWayTotalMoney = 0;
   var dataValid = true;
/*   var flag = false;*/
	if(selValue == 2 || selValue == 3 || selValue == 4){
		$("#byMonthOrByStage tr").each(function(i){
			var payMoneyInfo = "";
			var remark = $.trim($(this).find("input[type=text]:eq(0)").val());
			payMoneyInfo += remark;
			var monthMoney = $.trim($(this).find("input[type=text]:eq(1)").val()).replace(/,/g,"");
			payMoneyInfo += "&&"+monthMoney;
			var startDate = $.trim($(this).find("input[type=text]:eq(2)").val());
			payMoneyInfo += "&&"+startDate;
			var endDate = $(this).find("input[type=text]:eq(3)").val();
			payMoneyInfo+= "&&"+endDate;
			var monthPayDay = $(this).find("input[type=text]:eq(4)").val();
			payMoneyInfo+= "&&"+monthPayDay;
			var contractPaywayId = $(this).find("input[type=hidden]:eq(0)").val();
			if(contractPaywayId==''){
				contractPaywayId = "blank";
			}
			payMoneyInfo += "&&" + contractPaywayId;
//			payWayTotalMoney = add(payWayTotalMoney, multiply(monthMoney, startDate));
			paymentTerm.push(payMoneyInfo);
			if (monthMoney == "" || startDate == "" || endDate == "" || monthPayDay == "") {
				parent.showErrorMessage("请完善支付信息");
				dataValid = false;
			}
		});
	}else{
		$("#byMonthOrByStage tr").each(function(i){
			var payMoneyInfo = "";
			var payStage = i+1;
			payMoneyInfo += payStage;
			var time = $(this).find("input[type=text]:eq(3)").val();
			if(time != undefined && time != ''){
				payMoneyInfo += "&&"+time;
			}else{
				payMoneyInfo += "&&"+"";
			}
			var term = $.trim($(this).find("input[type=text]:eq(0)").val());
			payMoneyInfo += "&&"+term;
			var per = $.trim($(this).find("input[type=text]:eq(1)").val());
			/*if(per == undefined || per == ''){
				flag = true;
			}*/
			payMoneyInfo += "&&"+per;
			var money = $.trim($(this).find("input[type=text]:eq(2)").val()).replace(/,/g,"");
			/*if(money == undefined || money == ''){
				flag = true;
			}*/
			payMoneyInfo += "&&"+money;
			var contractPartId = $(this).find("input[type=hidden]:eq(0)").val();
			if(contractPartId==''){
				contractPartId = "blank";
			}
			payMoneyInfo+= "&&"+contractPartId;
			payWayTotalMoney = add(payWayTotalMoney, money);
			paymentTerm.push(payMoneyInfo);
			if (time == "" || per == "" || money == "") {
				parent.showErrorMessage("请完善支付信息");
				dataValid = false;
				return;
			}
		});
	}
	
	if($("input[type=radio][name=payMethod]").val() != "1" && $("input[type=radio][name=payMethod]").val() != "2"){
		parent.showErrorMessage('请填写支付方式!');
		return;
	}
	/*if(flag){
		parent.showErrorMessage('请填写支付方式!');
		return;
	}*/
	
	
	return {paymentTerm: paymentTerm, payWayTotalMoney: payWayTotalMoney, dataValid: dataValid};
}


//保存职员信息
function saveProduceContractInfo(){
	var subData = {};
	var objPayMentInfo = getPayMoneyInfo();
	subData.contractId = $("#contractId").val();
	subData.contractNo = $("#contractNo").val();
	subData.contractDate = $("#sonContractDate").val();
	subData.company = $("#sonCompany").val();
	subData.contactPerson = $("#sonContactPerson").val();
	subData.phone = $("#sonPhone").val();
	subData.identityCardType = $("#identityCardType").val();
	if ($("#identityCardNumber").val().length > 18) {
		parent.showInfoMessage("身份证号输入长度过长，请重新输入！");
		return false;
	}else {
		subData.identityCardNumber = $("#identityCardNumber").val();
	}
	subData.startDate = $("#sonStartDate").val();
	subData.endDate = $("#sonEndDate").val();
	subData.currencyId = $("#sonCurrencyId").val();
	var totalMoney = $("#sonTotalMoney").next("input[type=hidden]").val();
	subData.totalMoney = totalMoney;
    
	subData.paymentTerm = objPayMentInfo.paymentTerm.join("##");
	subData.bankName = $("#sonBankName").val();
	subData.bankAccountName = $("#sonBankAccountName").val();
	subData.bankAccountNumber = $("#sonBankAccountNumber").val();
	subData.payWay = $("input[name=payMethod]:checked").val();
	subData.financeSubjId = $("#sonFinanceSubjId").val();
	subData.financeSubjName = $("#sonFinanceSubjName").val();
	subData.remark = $("#sonRemark").val();
	subData.attpackId = $("#sonAttpackId").val();

	
	if($("#sonCompany").val() == ""){
		$(".name-error-tips").show();
		$("#sonWorkerName").addClass("produce-name-tips");
		return;
	}
	if($("#sonCurrencyId").val() == ""){
		$(".currency-error-tips").show();
		$("#sonCurrencyId").addClass("produce-name-tips");
		return;
	}
	if($("#sonTotalMoney").val() == ""){
		$(".total-error-tips").show();
		$("#sonTotalMoney").addClass("produce-name-tips");
		return;
	}
	if($("#sonContractDate").val() == ""){
		$(".contract-error-tips").show();
		$("#sonContractDate").addClass("produce-name-tips");
		parent.showErrorMessage("请选择合同的签署日期");
		return;
	}
	if (!objPayMentInfo.dataValid) {
		return;
	}
	if(subData.paymentTerm == undefined){
		return;
	}	
	var payTotalMoney = 0;
	var monthPayDetailArray = [];
	if (subData.payWay == 1) {
		payTotalMoney = objPayMentInfo.payWayTotalMoney;
	} else {
		var rows = $("#monthPayDetailGrid").jqxGrid('getrows');
//		if (rows.length == 0) {
//			calculateMonthPayDetail();
//			rows = $("#monthPayDetailGrid").jqxGrid('getrows');
//		}
//		if (rows.length == 0) {
//			return ;
//		}
		for (var i = 0; i < rows.length; i++) {
			var singleRow = rows[i];
			var month = singleRow.month;
			var startDate = singleRow.startDate;
			var endDate = singleRow.endDate;
			var money = singleRow.money;
			var payDate = singleRow.payDate;
			var id = singleRow.id;
			if(id==''){
				id = 'blank';
			}
			payTotalMoney = add(payTotalMoney, money);
			
			var singlePayWay = month + "&&" + startDate + "&&" + endDate + "&&" + money + "&&" + payDate+"&&"+id;
			monthPayDetailArray.push(singlePayWay);
		}
	}
	subData.monthPayDetail = monthPayDetailArray.join("##");
	
	var InfoMessgae = "";
	//根据填写的姓名和手机号判断是否有重复数据
	$.ajax({
		url: '/contractProduce/queryRepeatData',
	    type: 'post',
	    data: {company:subData.company, phone: subData.phone, contractId: subData.contractId},
	    datatype: 'json',
	    async: false,
	    success: function(response){
	    	if(response.success){
	    		var isRepeat = response.isRepeat;
	    		if (isRepeat) { //有重复数据
	    			InfoMessgae = "已经保存过 " + subData.company + " 的合同信息，是否继续保存？ ";
				}
	    	}else{
	    		parent.showErrorMessage(response.message);
	    	}
	    }
	});
	
	//判断阶段总金额是否和总金额相等
	if(payTotalMoney != totalMoney-0){
		InfoMessgae += "支付方式金额合计与总金额不相等，是否保存？";
	}	
	
	if (InfoMessgae != "" && InfoMessgae.length>0) {
		parent.popupPromptBox("提示",InfoMessgae, function (){
			saveActorInfos(subData);
		});
	}else{
		saveActorInfos(subData);
	}
}

//保存演员信息
function saveActorInfos(subData){
	$.ajax({
		url: '/contractProduce/saveContractInfo',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				
	    		var attpackId = response.attpackId;
	    		
	    		if (uploader.getFiles().length == 0) {
	    			closeRightPopup();
	    			parent.showSuccessMessage("操作成功");
	    		} else {
	    			uploader.option('formData', {
		    			attpackId: attpackId
		    		});
		    		uploader.upload();
	    		}
	    	}else{
	    		parent.showErrorMessage(response.message);
	    	}
		}
	});
}






//关闭添加页面
function closeRightPopup(){
	parent.closeAddPage();
	
}



//初始化修改制作合同页面
function initProducePage(){
//	var contractId = $("#contractId").val();
	//设置权限
	if(!isContractReadonly){
		$("input.right-popup-save").css("display", "block");
	}else{
		$("input.right-popup-save").css("display", "none");
		$("input.right-popup-delete").css("display", "none");
	}
	//是否渲染删除按钮
	if($("#contractId").val() != ""){
		$(".right-popup-title").text("修改制作合同");
		if(!isContractReadonly){
			$(".right-popup-delete").css("display", "block");
		}
		$.ajax({
			url: '/contractProduce/queryContractById',
			type: 'post',
			data: {contractId: $("#contractId").val()},
			datatype: 'json',
		
			success: function(response){
				if(response.success){
					var contractInfo = response.contractInfo;
					var attachmentList = response.attachmentList;
					var paymentList = response.paymentList;
					var contractStagePayWayList = response.contractStagePayWayList;
					var contractMonthPayWayList = response.contractMonthPayWayList;
					var contractMonthPayDetailList = response.contractMonthPayDetailList;
					
					//制作合同基本信息
					$("#contractId").val(contractInfo.contractId);
					$("#contractNo").val(contractInfo.contractNo);
					$("#sonContractDate").val(contractInfo.contractDate);
					$("#sonCompany").val(contractInfo.company);
					$("#sonContactPerson").val(contractInfo.contactPerson);
					$("#sonPhone").val(contractInfo.phone);
					/*$("#sonIdNumber").val(contractInfo.idNumber);*/
					$("#identityCardType").val(contractInfo.identityCardType);
					$("#identityCardNumber").val(contractInfo.identityCardNumber);
					$("#sonStartDate").val(contractInfo.startDate);
					$("#sonEndDate").val(contractInfo.endDate);
					$("#sonCurrencyId").val(contractInfo.currencyId);
					$("#sonTotalMoney").val(fmoney(contractInfo.totalMoney));
					$("#sonTotalMoney").next("input[type=hidden]").val(contractInfo.totalMoney);
					$("#sonBankName").val(contractInfo.bankName);
					$("#sonBankAccountName").val(contractInfo.bankAccountName);
					$("#sonBankAccountNumber").val(contractInfo.bankAccountNumber);
					$("#sonPayWay").val(contractInfo.payWay);
					if(contractInfo.financeSubjId != "" && contractInfo.financeSubjName != ""){
						$("#clearFinanceSubj").show();
					}
					$("#sonFinanceSubjId").val(contractInfo.financeSubjId);
					$("#sonFinanceSubjName").val(contractInfo.financeSubjName);
					$("#sonAttpackId").val(contractInfo.attpackId);
					$("#sonRemark").val(contractInfo.remark);
					
					//合同支付信息
					if(contractInfo.payWay == 1 || contractInfo.payWay == null) {
						$(".payment-td").html("");
						$("input[type=radio][value=1]").prop("checked", true);
						if(contractStagePayWayList.length == 0){
							var paymentContent = "";
							paymentContent += '<input type="hidden" value="" />';
							paymentContent += '<span>第</span>';
							paymentContent += '<span class="spanClass" id="1">一</span>';
							paymentContent += '<span>期: <input class="stage-info" type="text" maxlength="180"  title="">,甲方向乙方支付酬金总额的';
							paymentContent += '    <input class="stage-money-part" type="text" maxlength="10" onblur="percentCalculation(this)">%,即';
							paymentContent += '    <input type="text" maxlength="10" onkeyup="" onblur="checkIsMoney(this)" class="stage-money">元整。';
							paymentContent += '</span>';
							paymentContent += '<span class="remind-tips">预计付款日期:</span>';
							paymentContent += '<input class="date-select remind-input" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">';  
							
							$(".payment-td").append(paymentContent);
						}else{
							for (var i= 0; i < contractStagePayWayList.length; i++) {
								var id = contractStagePayWayList[i].id;
								var money = contractStagePayWayList[i].money;
								var rate = contractStagePayWayList[i].rate;
								var remindTime = contractStagePayWayList[i].remindTime;
								var stage = contractStagePayWayList[i].stage;
								var remark = contractStagePayWayList[i].remark;
								
								if(i == 0){
									var paymentContent = "";
									paymentContent += '<input type="hidden" value="" />';
									paymentContent += '<span>第</span>';
									paymentContent += '<span class="spanClass" id="1">一</span>';
									paymentContent += '<span>期: <input class="stage-info" type="text" maxlength="180"  title="">,甲方向乙方支付酬金总额的';
									paymentContent += '    <input class="stage-money-part" type="text" maxlength="10" onblur="percentCalculation(this)">%,即';
									paymentContent += '    <input type="text" maxlength="10" onkeyup="" onblur="checkIsMoney(this)" class="stage-money">元整。';
									paymentContent += '</span>';
									paymentContent += '<span class="remind-tips">预计付款日期:</span>';
									paymentContent += '<input class="date-select remind-input" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">';  
									
									$(".payment-td").append(paymentContent);
									
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(rate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(fmoney(money));
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(remindTime);
								}else{
//									var allSpan = $('#byMonthOrByStage tr').find('.spanClass');
//									var spanRows = allSpan.length;
//									var nowSpan = allSpan[spanRows-1];
//									var oldId = nowSpan.id;
//									var newId = parseInt(oldId)+1;
									var _tr =$('table#byMonthOrByStage').find("tr.byStageTr").eq(i-1);
									var _trCopy = _tr.clone(true);
									
//									clockid=clockid+newId;
									
									_trCopy.find(".date-select").attr("onClick","WdatePicker({readOnly:true,isShowClear:false})");
									_trCopy.find("input").val("");
									_tr.after(_trCopy); 
									
									$('#byMonthOrByStage .spanClass').each(function(i){
										$(this).text(changeNumToChinese(i+1));
										$(this).attr('id',i+1);
									});
									
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(rate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(fmoney(money));
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(remindTime);
									
								}
							}
						}
						
					} else if (contractInfo.payWay == 2) {
						$(".payment-td").html("");
						loadMonthPayDetailGrid(contractMonthPayDetailList);
						$("input[type=radio][value=2]").prop("checked", true);
						$(".month-pay-detail-tr").show();
						if(contractMonthPayWayList.length == 0){
							var paymentContent = "";
							paymentContent += '<input type="hidden" value="" />';
				  			paymentContent += '<span>';
				  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按月支付酬金，每月金额为';
				  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
				  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
				  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
				  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
				  			paymentContent += '</span>';
							
							$(".payment-td").append(paymentContent);
						}else{
							for (var i= 0; i < contractMonthPayWayList.length; i++) {
								var id = contractMonthPayWayList[i].id;
								var monthMoney = contractMonthPayWayList[i].monthMoney;
								var startDate = contractMonthPayWayList[i].startDate;
								var endDate = contractMonthPayWayList[i].endDate;
								var monthPayDay = contractMonthPayWayList[i].monthPayDay;
								var remark = contractMonthPayWayList[i].remark;
								
								if(i == 0) {
									var paymentContent = "";
									paymentContent += '<input type="hidden" value="" />';
						  			paymentContent += '<span>';
						  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按月支付酬金，每月金额为';
						  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
						  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
						  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
						  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
						  			paymentContent += '</span>';
									
									$(".payment-td").append(paymentContent);
									
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								} else {
									var _trf=$('table#byMonthOrByStage').find("tr.byStageTr").eq(i-1);
									var _copyTr = _trf.clone(true);
//									var newId = $('#byMonthOrByStage tr').length + 1;
//									clockid=clockid+newId;
									
//									_copyTr.find(".date-select").attr("onClick","WdatePicker({readOnly:true,isShowClear:false})");
									_copyTr.find("input").val("");
									
									_trf.after(_copyTr);
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								}
							}
						}
						
					} else if (contractInfo.payWay == 3) {
						$(".payment-td").html("");
						loadMonthPayDetailGrid(contractMonthPayDetailList);
						$("input[type=radio][value=3]").prop("checked", true);
						$(".month-pay-detail-tr").show();
						if(contractMonthPayWayList.length == 0){
							var paymentContent = "";
							paymentContent += '<input type="hidden" value="" />';
				  			paymentContent += '<span>';
				  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
				  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
				  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
				  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
				  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
				  			paymentContent += '</span>';
							
							$(".payment-td").append(paymentContent);
						}else{
							for (var i= 0; i < contractMonthPayWayList.length; i++) {
								var id = contractMonthPayWayList[i].id;
								var monthMoney = contractMonthPayWayList[i].monthMoney;
								var startDate = contractMonthPayWayList[i].startDate;
								var endDate = contractMonthPayWayList[i].endDate;
								var monthPayDay = contractMonthPayWayList[i].monthPayDay;
								var remark = contractMonthPayWayList[i].remark;
								
								if(i == 0) {
									var paymentContent = "";
									paymentContent += '<input type="hidden" value="" />';
						  			paymentContent += '<span>';
						  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
						  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
						  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
						  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">结算日为每月';
						  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">日';
						  			paymentContent += '</span>';
									
									$(".payment-td").append(paymentContent);
									
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								} else {
									var _trf=$('table#byMonthOrByStage').find("tr.byStageTr").eq(i-1);
									var _copyTr = _trf.clone(true);
//									var newId = $('#byMonthOrByStage tr').length + 1;
//									clockid=clockid+newId;
									
//									_copyTr.find(".date-select").attr("onClick","WdatePicker({readOnly:true,isShowClear:false})");
									_copyTr.find("input").val("");
									
									_trf.after(_copyTr);
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								}
							}
						}
						
					} else if (contractInfo.payWay == 4) {
						$(".payment-td").html("");
						
						loadMonthPayDetailGrid(contractMonthPayDetailList);
						$("input[type=radio][value=4]").prop("checked", true);
						$(".month-pay-detail-tr").show();
						if(contractMonthPayWayList.length == 0){
							var paymentContent = "";
							paymentContent += '<input type="hidden" value="" />';
				  			paymentContent += '<span>';
				  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
				  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
				  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
				  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">每';
				  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">天结算一次';
				  			paymentContent += '</span>';
							
							$(".payment-td").append(paymentContent);
						}else{
							for (var i= 0; i < contractMonthPayWayList.length; i++) {
								var id = contractMonthPayWayList[i].id;
								var monthMoney = contractMonthPayWayList[i].monthMoney;
								var startDate = contractMonthPayWayList[i].startDate;
								var endDate = contractMonthPayWayList[i].endDate;
								var monthPayDay = contractMonthPayWayList[i].monthPayDay;
								var remark = contractMonthPayWayList[i].remark;
								
								if(i == 0) {
									var paymentContent = "";
									paymentContent += '<input type="hidden" value="" />';
						  			paymentContent += '<span>';
						  			paymentContent += '    <input class="remark input-long" type="text" maxlength="180" onkeyup="setTitle(this);" >，甲方向乙方按日支付酬金，每日金额为';
						  			paymentContent += '    <input class="month-money input-middle" type="text" maxlength="10" onblur="checkIsMoney(this)">元，付款周期为';
						  			paymentContent += '    <input class="start-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">至';
						  			paymentContent += '    <input class="end-date input-middle" type="text" onClick="WdatePicker({readOnly:true,isShowClear:false})">每';
						  			paymentContent += '    <input class="month-pay-day input-small" type="text" maxlength="2" onblur="checkMonthDayInput(this)">天结算一次';
						  			paymentContent += '</span>';
									
									$(".payment-td").append(paymentContent);
									
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								} else {
									var _trf=$('table#byMonthOrByStage').find("tr.byStageTr").eq(i-1);
									var _copyTr = _trf.clone(true);
									_copyTr.find("input").val("");
									_trf.after(_copyTr);
									$(".byStageTr").eq(i).find("input[type=hidden]").eq(0).val(id);
									$(".byStageTr").eq(i).find("input[type=text]").eq(0).val(remark);
									$(".byStageTr").eq(i).find("input[type=text]").eq(1).val(fmoney(monthMoney));
									$(".byStageTr").eq(i).find("input[type=text]").eq(2).val(startDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(3).val(endDate);
									$(".byStageTr").eq(i).find("input[type=text]").eq(4).val(monthPayDay);
								}
							}
						}
						
					}
					
					//上传合同附件信息
					if(attachmentList != null || attachmentList.length != 0){
						var html = [];
						for(var i= 0; i< attachmentList.length; i++){
							var attachment = attachmentList[i];
							var suffix =attachment.suffix.toLowerCase();
							if(suffix == ".doc" || suffix == ".docx"){
								html.push("<li class='upload-file-list-li'  onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type 
										+"\", \""+ attachment.hdStorePath +"\")'><img src='../images/word.jpg' title='"+ attachment.name 
										+"'><a class='download-tag' onclick='downLoadAttachment(event,this,\""+ attachment.id 
										+"\")' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id 
										+"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
							}
							else if(suffix == ".pdf"){
								html.push("<li class='upload-file-list-li'  onclick='previewAtts(\""+ attachment.attpackId +"\", \""
										+ attachment.type +"\", \""+ attachment.hdStorePath +"\")'><img src='../images/pdf.jpg' title='"
										+ attachment.name +"'><a class='download-tag' onclick='downLoadAttachment(event,this,\""+ attachment.id 
										+"\")' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id 
										+"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
							}else{
//								html.push("<li class='upload-file-list-li'><a href='javascript:void(0)' class='showFile' style='text-decoration: none;' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\", \""+ attachment.hdStorePath +"\")'>" + attachment.name +"</a><a class='closeTag' onclick='deleteUploadedFile(this,\""+ attachment.id +"\")'></a></li>");
//								html.join("");
								html.push("<li class='upload-file-list-li'  onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type 
										+"\")'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"' title='"+ attachment.name 
										+"'><a class='download-tag' onclick='downLoadAttachment(event,this,\""+ attachment.id 
										+"\")' title='下载'></a><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id 
										+"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
								
							}
						
						}
						html.join("");
						$("#showFileRealNameAndSaveId").append(html);
					}
					
					//支付明细信息
					if(paymentList.length != 0){
						
						var html = [];
						html.push("<table class='payment-detail-list' id='paymentDetailList'>");
						html.push("<thead><tr><td>支付时间</td><td>票据编号</td><td>摘要</td><td>实付金额</td><td>状态</td></tr></thead>");
						html.push("<tbody>");
						for(var i= 0; i< paymentList.length; i++){
							html.push("<tr>");
							html.push("<td>" + paymentList[i].paymentDate + "</td>");
							html.push("<td>" + paymentList[i].receiptNo + "</td>");
							html.push("<td>" + paymentList[i].summary + "</td>");
							html.push("<td>" + paymentList[i].totalMoney + "</td>");
							if (paymentList[i].status == 0) {
								html.push("<td>未结算</td>");
							} else {
								html.push("<td>已结算</td>");
							}
							html.push("</tr>");
						}
						$("#paymentDetailListTd").append(html.join(""));
					}
					

					//只读模式
					if ($("#readonly").val() != "" && $("#readonly").val()) {
						$(".right-popup-header").remove();
						$("input").attr("disabled", "disabled");
						$("select").attr("disabled", "disabled");
						$(".closeTag").remove();
					}
					
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
	}
}

//预览附件
function previewAtts(attpackId, type, hdStorePath) {
	if (type == 2) {
		window.open("/attachmentManager/toPreviewPage?attpackId='" + attpackId + "'&type=" + type);
	} else {
		window.open("/fileManager/previewAttachment?address=" + hdStorePath);
	}
}

//删除制作合同
function deleteProduceContractInfo(){
	parent.popupPromptBox("提示","是否删除该制作合同？", function (){
		var contractId = $("#contractId").val();
		$.ajax({
			url: '/contractProduce/deleteContract',
			type: 'post',
			data: {contractId: contractId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("操作成功");
					closeRightPopup();
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
    });
}

//计算按月支付详情
function calculateMonthPayDetail() {
	var paymentInfo = getPayMoneyInfo();
	var dataValid= paymentInfo.dataValid;
	var payWay = $("input[name=payMethod]:checked").val();
	var paymentTerm = "";
	if (dataValid) {
		paymentTerm = paymentInfo.paymentTerm.join("##");
	}
	$.ajax({
		url: "/contractManager/calculateMonthPayDetail",
		type: "post",
		data: {paymentTerm: paymentTerm, payWay: payWay},
		dataType: "json",
		success: function(response) {
			if (!response.success) {
				parent.showErrorMessage(response.message);
				loadMonthPayDetailGrid();
				return;
			}
			var monthPayDetailList = response.monthPayDetailList;
			loadMonthPayDetailGrid(monthPayDetailList);
		}
	});
}

//初始化按月支付详情表格
function loadMonthPayDetailGrid(data) {
	var source = {
		localdata: data,
		dataType : "json",
        datafields: [
            {name: 'month', type: 'string' },
            {name: 'startDate', type: 'string' },
            {name: 'endDate', type: 'string' },
            {name: 'money', type: 'string' },
            {name: 'payDate', type: 'string' }
        ]
    };
    var dataAdapter = new $.jqx.dataAdapter(source);
    var dateRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
    	return "<div class='align-center jqx-column'>" + rowdata.startDate + "-" + rowdata.endDate + "</div>";
    };
//    var screenWidth = window.screen.width;
//    var gridWidth;
//    if(screenWidth >= 1366 && screenWidth <= 1399){
//    	gridWidth = 600;
//    }else if(screenWidth >= 1400 && screenWidth <= 1439){
//    	gridWidth = 650;
//    }else{
//    	gridWidth = 800;
//    }
	$("#monthPayDetailGrid").jqxGrid({
		width: 800,
        autoheight: true,
        source: dataAdapter,
        rowsheight: 30,
        editable: true,
        autoshowloadelement: false,
        showaggregates: true,
        showstatusbar: true,
        columns: [
          { text: '月份', datafield: 'month', width: '20%', editable: false, cellsalign: 'center', align: 'center' },
          { text: '周期', cellsrenderer: dateRenderer, width: '30%', editable: false, cellsalign: 'center', align: 'center' },
          { text: '费用(单击修改金额)', datafield: 'money', width: '30%', cellsformat: 'f', cellsalign: 'right', align: 'center', aggregates: [{'总金额':
              function (aggregatedValue, currentValue, column, record) {
	              return add(aggregatedValue, currentValue);
		      }
		  }]},
          { text: '付款日期', datafield: 'payDate', width: '20%', editable: false, cellsalign: 'center', align: 'center'}
        ]
	});
}

//下载文件附件
function downLoadAttachment(ev,own,attId){
	//只读权限，不能进行删除
	if(isContractReadonly) {
		ev.stopPropagation();
		return false;
	}
	var address = "";
	var fileName = "";
	own= $(own);
	$.ajax({
    	url:'/attachmentManager/queryAttachmentById',
    	type:'post',
    	async: false,
    	dataType:'json',
    	data:{attachmentId: attId},
    	success:function(data){
    		if(data.success){
    			address = data.downLoadAddress;
    			fileName = data.fileName;
    		}else{
    			parent.showErrorMessage('下载附件失败');
    		}
    		
    	}   	
	});
	ev.stopPropagation();
	//根据地址下载附件
	if (address != "") {
		window.location.href="/fileManager/downloadFileByAddr?address="+address + "&fileName=" + fileName;
	}
}
