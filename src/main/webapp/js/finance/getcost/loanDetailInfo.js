$(document).ready(function(){
	//校验是否需要财务密码
	//checkNeedFinancePwd();
	//获取当前日期
	getNowFormatDate();
	//获取新的票据编号
	if($("#loanId").val() == ""){
		getLoanOrderNum();
	}
	
	//加载财务科目列表
	loadFinanceSubjList();
	//获取币种列表
	getcurrencyId();

	//输入金额限制，自动转换大写金额
	formatAmount();
	
	$(document).on("click", function(){
		$('.fin_subj').css("display", "none");
		$('.dropdown_box').hide();	
    	$('.paymethod-dropdown-box').hide();
	});
	
	//判断新增还是修改借款单
	if($("#loanId").val() != ""){
		getLoanDetailInfo();
	} else {
		//获取记账人信息
		getAccounting();
	}
	
	//只读权限，不能进行修改
	if(isRunningAccountReadonly){
		$("input[type='text']").attr('disabled',true);
		$("select").attr('disabled',true);
		$("#clearFinanceSubj").hide();
	}
});

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
    $("#billsDateTime").val(currentdate);
}

//获取借款单票据编号
function getLoanOrderNum(){
	var originalReceipNo = $("#originalReceipNo").val();
	if($("#loanId").val() != ""){
		var originalLoanDate = $("#originalLoanDate").val().substring(0, 7);
		var currLoanDate = $("#billsDateTime").val().substring(0, 7);
		if (originalLoanDate == currLoanDate) {
			$("#billsNum").val(originalReceipNo);
			return;
		}
	}
	
	$.ajax({
		url: '/loanInfoManager/queryNewReceiptNo',
		type: 'post',
		data: {loanDate: $("#billsDateTime").val(), originalReceipNo: originalReceipNo},
		datatype: 'json',
		success: function(response){
			if(response.success){
				$("#billsNum").val(response.newReceiptNo);
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});
}


//选择借款人
function getBorrowedName(own){
	own= $(own);
	$.ajax({
		url: '/getCostManager/queryDropDownData',
		type: 'post',
		data: {includePayment: false,includeCollection: false,includeLoan: true},
		datatype: 'json',
		success: function(response){
			
			if(response.success){
				aimPeople = response.aimPeople;
				if(aimPeople.length > 0){
					own.next("ul.dropdown_box").find("li").remove();
					$.each(aimPeople, function(i,value){
						own.next("ul.dropdown_box").append("<li class='drop-down-li' title='" + value + "'><a href='javascript:void(0)'>" + value + "</a></li>");
					});
					own.next("ul.dropdown_box").css({left: own.position().left, top: own.position().top+own.outerHeight()}).show();
				}
				
				$("#loanMoneyParty").keyup(function(){
				       var _this=$(this), _subList=_this.siblings('ul').children('li');
				       var a = false;
				       _subList.each(function(){
				    	   
				           if($(this).text().search($.trim(_this.val()))!=-1){
				        	   a = true;
				        	   _this.siblings("ul.dropdown_box").show();
				               $(this).show();
				           }else{
				               $(this).hide();
				           }
				       });
				       
				       if (!a) {
				    	   _this.siblings("ul.dropdown_box").hide();
				       }
				   });
					 
				$('.dropdown_box li').off('click');
				$('.dropdown_box').on('click','li',function(ev){
					       var _this=$(this);
					       $('#loanMoneyParty').val($(this).text());        
					       _this.parent().hide();
				});
				
			}else{
				showErrorMessage(response);
			}
		}
	});
}




//加载财务科目列表
function loadFinanceSubjList(){
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
		 width: 260,
		 height:250,
		 source: dataAdapter,
	     /*filterable: true,*/
	     //filterMode:'advanced',
		 showHeader: false,
		 ready: function(){},
		    columns: [
		          { text: '财务科目', dataField: 'name', width: '100%', align: "center" }
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
	         
		         if(records == undefined){

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
				          $("#financeSubjName").val(name);
				          $("#financeSubjId").val(row.id);
				          
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
		
		//阻止冒泡事件
		$("#levelPopup").on("click", function(ev){
			ev.stopPropagation();
		});
		
		
		$('#financeSubjName').click(function(ev){
			var obj = $(this);
			//鼠标点击的位置
			var objHeight = ev.pageY;
			//dl相对于浏览器的位置
			var dlHeight = obj.parents("dl").offset().top;
			if($("#loanId").val() != ""){
				$('.fin_subj').css({left:obj.position().left+40,top: obj.offset().top, "display": "block"});
			}else{
				$('.fin_subj').css({left:obj.offset().left-4,top:obj.offset().top, "display": "block"});
			}
	        ev.stopPropagation();
	    });
}


//清空财务科目
function clearFinanceSubj(){
	 $("#financeSubjName").val("");
     $("#financeSubjId").val("");
     $("#clearFinanceSubj").hide();
}


//获取币种列表
function getcurrencyId(){
	$.ajax({
		url: '/currencyManager/queryCurrencyList',
		type: 'post',
		data: {ifEnable: true, ifStandard: ''},
		datatype: 'json',
		async: false,
		success: function(response){
			if(response.success){
				var currencyInfoList = response.currencyInfoList;
				var html = [];
				for(var i = 0; i < currencyInfoList.length; i++){
					html.push("<option value='" + currencyInfoList[i].id + "'>" + currencyInfoList[i].name+"-"+ currencyInfoList[i].code +"</option>");
				}
				$("#currency").append($(html.join("")));
				
			}
		}
	});
}






//输入金额限制,自动转换大写金额
function formatAmount(){

	$("#money").on("keyup", function(){
		$(this).val($(this).val().replace(/[^\-\d.]/g,""));  //清除“数字”和“.”“-”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
		//只保留第一个-
		$(this).val($(this).val().replace(/\-{2,}/g,"-"));
		$(this).val($(this).val().replace("-","$#$").replace(/\-/g,"").replace("$#$","-"));
	}).on("blur",function(){
		
		var money = $(this).val().replace(/,/g,"")-0;
		$(this).next("input[type=hidden]").val(money);
		if(money != ""){
			$(this).val(fmoney(money));
			$("#capitalAccountMoney").text("(大写)"+numberToCapital(money));
		}else{
			$("#capitalAccountMoney").text("");
		}
	});
}


//获取记账人信息
function getAccounting(){
	$.ajax({
		url: '/userManager/queryLoginUserInfo',
		type: 'get',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var realName = response.userInfo.realName;
				$("#agent").val(realName);
			}else{
				//parent.showErrorMessage(response.message);
			}
		}
	});
}




//获取借款单信息
function getLoanDetailInfo(){
	$.ajax({
		url: '/loanInfoManager/queryLoanDetailInfo',
		type: 'post',
		data: {loanId: $("#loanId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				console.log(response);
				var loanInfo = response.loanInfo;
				if(loanInfo != null){
					$("#billsNum").val(loanInfo.receiptNo);
					$("#billsDateTime").val(loanInfo.loanDate);
					$("#loanMoneyParty").val(loanInfo.peyeeName);
					$("#summary").val(loanInfo.summary);
					if((loanInfo.financeSubjId != "" && loanInfo.financeSubjName != "") && (loanInfo.financeSubjId != null && loanInfo.financeSubjName != null)){
						$("#clearFinanceSubj").show();
					}
					$("#financeSubjName").val(loanInfo.financeSubjName);
					$("#financeSubjId").val(loanInfo.financeSubjId);
					$("#currency").val(loanInfo.currencyId);
					$("#money").val(fmoney(loanInfo.money));
					$("#money").next("input[type=hidden]").val(loanInfo.money);
					$("#paymentWay").val(loanInfo.paymentWay);
					$("#capitalAccountMoney").text(numberToCapital(loanInfo.money));
					$("#agent").val(loanInfo.agent);
					$("#attachmentPacketId").val(response.attachmentPacketId);
					
					$("#originalReceipNo").val(loanInfo.receiptNo);
					$("#originalLoanDate").val(loanInfo.loanDate);
				}
				
			}else{
				//showErrorMessage(response.message);
			}
		}
	});
}
