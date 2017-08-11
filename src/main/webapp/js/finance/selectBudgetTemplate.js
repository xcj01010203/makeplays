var clientHeight;
$(document).ready(function(){
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest("#from-history-select").length == 0){ 
			$(".history-temp-wrap").hide(); 
		} 
	});
	getTempFromHistory();
//判断是否生成财务科目
$.ajax({
	url: '/financeSubject/nopassword/hasFinanceSubject',
	type: 'post',
	datatype: 'json',
	async: false,
	success: function(response){
		//没有生成财务科目
		if(response.hasSubject){
			window.location.href = "/financeSubject/toFinanceBudgetPage";
		}
	}
});
	showCoverWindow();
	makeProductCycle();
	makeDepartment();	
});
//不使用模板
function makeNoTemplate() {
	$.ajax({
		url: '/financeSubject/selectFinanceSubjectTemplate',
		type: 'post',
		data: {"type" : 2},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				window.location="/financeSubject/toFinanceBudgetPage";
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//按制作周期
function makeProductCycle(){
	var source = {
		url: '/financeSubject/queryFinanceSubjectByTempType',
		type: 'post',
		data: {"type" : 0},
		datatype: 'json',
		dataFields: [
		    {name: 'id',type: 'string'},
		    {name: 'name',type: 'string'},
		    {name: 'level',type: 'int'},
		    {name: 'parentId',type: 'string'},
		    {name: 'type',type: 'int'}
		],
		hierarchy:{
			keyDataField: {name:'id'},
			parentDataField: {name:'parentId'}
		},
		id: 'id'	
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#production").jqxTreeGrid({
		theme: theme,
		width: 240,
		height:480,
		maxHeight:clientHeight,
        source: dataAdapter,
        selectionMode: "singleRow",
        ready: function(){
        	$("#production").on("rowSelect",function(event){
        		var args = event.args;
        		var key = args.key;
        		var row = $("#production").jqxTreeGrid('getRow', key);
        		if(row.expanded == true){
			    	 $("#production").jqxTreeGrid('collapseRow', key);
			     }else{
			    	 $("#production").jqxTreeGrid('expandRow', key);
			     }
			     $("#production").jqxTreeGrid('clearSelection');
        	});
        },
        columns: [
                  { text: '财务科目', dataField: 'name', align: "center" }
                 
                ]
	});
}

//按部门
function makeDepartment(){
	var source = {
		url: '/financeSubject/queryFinanceSubjectByTempType',
		type: 'post',
		data: {"type" : 1},
		datatype: 'json',
		dataFields: [
		    {name: 'id',type: 'string'},
		    {name: 'name',type: 'string'},
		    {name: 'level',type: 'int'},
		    {name: 'parentId',type: 'string'},
		    {name: 'type',type: 'int'}
		],
		hierarchy:{
			keyDataField: {name:'id'},
			parentDataField: {name:'parentId'}
		},
		id: 'id'	
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
    $("#department").jqxTreeGrid({
    	theme: theme,
    	width: 240,
    	height:480,
    	maxHeight: clientHeight,
    	source: dataAdapter,
    	selectionMode: "singleRow",
        ready: function(){
        	$("#department").on('rowSelect',function(event){
        		var args = event.args;
        		var key = args.key;
        		var row = $("#department").jqxTreeGrid('getRow',key);
        		if(row.expanded == true){
			    	 $("#department").jqxTreeGrid('collapseRow', key);
			     }else{
			    	 $("#department").jqxTreeGrid('expandRow', key);
			     }
			     $("#department").jqxTreeGrid('clearSelection');
        	});
        },
        columns: [
            {text: '财务科目', dataField: 'name', align: "center"}
        ]
    });
}
//选取部门radio按钮
function selectDepartTemp(){
	$("input[name='selectTeam']:eq(0)").prop("checked",true); 
	$('#hideProductionDiv').css('display','block');
	$('#hideHistoryDiv').css('display','block');
	$('#hideDeparmentDiv').css('display','none');
}
//选取周期radio按钮
function selectPeriodTemp(){
	$("input[name='selectTeam']:eq(1)").prop("checked",true); 
	$('#hideDeparmentDiv').css('display','block');
	$('#hideProductionDiv').css('display','none');
	$('#hideHistoryDiv').css('display','block');
}
//选取历史项目radio按钮
function selectHistoryTemp(){
	$("input[name='selectTeam']:eq(2)").prop("checked",true); 
	$('#hideDeparmentDiv').css('display','block');
	$('#hideProductionDiv').css('display','block');
	$('#hideHistoryDiv').css('display','none');
}
//确定按钮
function productCycleButton(){
	$('#finance-popup-confirm').prop('disabled','disabled');
	var firstLevelRows = $("#financeHistory").jqxTreeGrid('getRows');
	var financeValue =$("input[name=selectTeam]:checked").val();
	var subData = {};
	subData.type = financeValue;
	if(financeValue == 9){
		var oldCrewid = $('#hideInput').val();
		if(oldCrewid){
			subData.oldCrewId=oldCrewid;
			if(!firstLevelRows || firstLevelRows.length == 0){
				makeNoTemplate();
				return;
			}
		}else{
			showErrorMessage('请选择模板');
			return;
		}
	}
	
	$.ajax({
		url: '/financeSubject/selectFinanceSubjectTemplate',
		type: 'post',
		data: subData,
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("操作成功");
				hideCoverWindow();
				window.location="/financeSubject/toFinanceBudgetPage";
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//显示隐藏层和弹出层
function showCoverWindow(){
   $('#financeGrayBg').css('display','block');
   $('#financeGrayBg').css('height',$(window).height());
   $('#financeWrap').css('display','block');
}
//去除隐藏层和弹出层
function hideCoverWindow(){
    $('#financeGrayBg').css('display','none');
    $('#financeWrap').css('display','none');
}
//function getHistoryList(){
//	getTempFromHistory();
//}
//获取历史项目下拉列表
function getTempFromHistory(){
	$.ajax({
		url: '/crewManager/queryAllCrewsByUserId',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				var len = response.crewList.length;
				var html = '';
				for(var i = 0; i < len;i++){
					html += '<div class="history-option" id="'+ response.crewList[i].crewid +'" onclick="getSelectCrew(this)">'+ response.crewList[i].crewname +'</div>';
				}
				$('.history-temp-wrap').append(html);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//获取历史剧组ID
function getSelectCrew(own){
	var crewId = $(own).attr('id');
	var crewName = $(own).text();
	$('#hideInput').val(crewId);
	$('#from-history-select').val(crewName);
	$('.history-temp-wrap').hide();
	loadTreeTable(crewId);
}
//显示下拉框
function historySelectShow(){
	$('.history-temp-wrap').toggle();
}
//导入历史剧组树表
function loadTreeTable(crewId){
    $.ajax({
		url: '/financeSubject/queryFinanceSubjectByTempType',
		type: 'post',
		data: {'type':9,'crewId' : crewId},
		dataType: 'json',
		success: function(data){
			if(data.success){
				if(!data || !data.financeSubjectList){
					return;
				}
				var dataH = data;
				console.log(dataH);
				var source = {
//					url: '/financeSubject/queryFinanceSubjectByTempType',
//					type: 'post',
//					data: {'type':9,'crewId' : crewId},
					datatype: 'json',
					dataFields: [
					    {name: 'id',type: 'string'},
					    {name: 'name',type: 'string'},
					    {name: 'parentId',type: 'string'}
					],
					hierarchy:{
						keyDataField: {name:'id'},
						parentDataField: {name:'parentId'}
					},
					id: 'id',
					localData: dataH
				};
				var dataAdapter = new $.jqx.dataAdapter(source);
			    $("#financeHistory").jqxTreeGrid({
			    	theme: theme,
			    	width: 230,
			    	height:480,
			    	maxHeight: clientHeight,
			    	source: dataAdapter,
			    	selectionMode: "singleRow",
			    	ready: function(){
			        	$("#financeHistory").on('rowSelect',function(event){
			        		var args = event.args;
			        		var key = args.key;
			        		var row = $("#financeHistory").jqxTreeGrid('getRow',key);
			        		if(row.expanded == true){
						    	 $("#financeHistory").jqxTreeGrid('collapseRow', key);
						     }else{
						    	 $("#financeHistory").jqxTreeGrid('expandRow', key);
						     }
						     $("#financeHistory").jqxTreeGrid('clearSelection');
			        	});
			        },
			        columns: [
			            {text: '财务科目', dataField: 'name',align: "center"}
			        ]
			    });
			 }else{
				 showErrorMessage(data.message);
			 }
		}
	});
}


