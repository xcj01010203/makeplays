var valCrewName='';

$(document).ready(function () {
	
	 //初始化剧组详细信息弹出窗
    $("#addCrewDiv").jqxWindow({
         theme:theme,  
         width: 810,
         height: 770, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         isModal: true,
         showCloseButton: true,
         resizable: false,
         modalZIndex: 1000,
         initContent: function() {
              
         }
    });
    //初始化button
    $(':button').jqxButton({theme:theme, width: 60, height: 25 });
    $('.close-btn').jqxButton({theme:theme, width: 75, height: 25});
    //选择排序字段弹窗
    $("#sortdiv").jqxWindow({
        theme:theme,  
        width: 500,
        height: 330, 
        autoOpen: false,
        maxWidth: 600,
        maxHeight: 600,
        isModal: true,
        showCloseButton: true,
        resizable: false,
        modalZIndex: 1000,
        initContent: function() {
             
        }
    });
    //加载查询、排序条件
    loadCondition();
    //初始化排序表格
    initSortGrid();
    //加载剧组列表
  	loadCrewList();
  	//隐藏下拉菜单
	$(document).click(function(){
		$(".dropdown_box").prev().removeClass('btn-click-div').addClass('btn-div');
		$(".dropdown_box").hide();
	});
	$(".dropdown_box").on("click",function(ev){
		ev.stopPropagation();
	});
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
  	//$("#crewtypeSelect").selectpicker("refresh");
	//为下拉插件增加其他下拉框关闭事件
	$(".selectpicker").next().find('button').on('click',function(ev){
		var yuanClick=$(this).onclick;
		if(typeof(yuanClick)=='function'){
			yuanClick();
		}
		hideDropdown($(this).attr('data-id'));
	});
  	
  	//显示清空按钮
	$('table.crew-table th').on('mouseover', function(event) {
        if ($(this).find("select").val()) {
            $(this).find(".clearSelection").show();
        }
        var obj=$(this);
        $(this).find("input[type='text']").each(function(i){
        	if($(this).val()){
        		$(obj).find(".clearSelection").show();
        	}
        });
    });
    
    $('table.crew-table th').on('mouseout', function(event) {
        $(this).find(".clearSelection").hide();
    });
    //初始化选择事件
    initSelectChange();
    
    //获取系统题材数据
    $.ajax({
        url: "/crewManager/queryAllSubject",
        type: "post",
        dataType: "json",
        async: false,
        success: function(response) {
            if (response.success) {
                var subjectList = response.subjectList;
                for (var i = 0; i < subjectList.length; i++) {
                    var subject = subjectList[i];
                    $("#subject").append("<option value='" + subject.subjectName + "'>" + subject.subjectName + "</option>");
                }
            } else {
                $("#errorMessage").text(response.message);
            }
        }
    });
 });
//加载剧组列表
function loadCrewList() {
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight,
	    _PageWidth = document.documentElement.clientWidth;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	    _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDiv");
	
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	//获取选中的条件
	loadSearchResult();
	$.ajax({
		   url:'/crewManager/queryAllCrews',
		   dataType:'json',
		   type:'post',
		   data:crewFilter,
		   success:function(data){
			   _LoadingHtml.hide();
	            $(".opacityAll").hide();
			   if(data.success) {
				   var crewList=data.crewList;
				   var content=[];
				   for(var i=0;i<crewList.length;i++){
					   var obj=crewList[i];
					   content.push("<tr class='");
					   if(i%2==1){
						   content.push(" color-view-tr ");
					   }
					   if(obj.endDate < today){
						   content.push(" outofdate-tr ");
					   }
					   content.push("' name='tableName'>");
					   //剧组类型
					   var crewtype="";
					   switch(obj.crewType){
						   case 0:
							   crewtype="电影";
							   break;
						   case 1:
							   crewtype="电视剧";
							   break;
						   case 2:
							   crewtype="网剧";
							   break;
						   case 3:
							   crewtype="网大";
							   break;
						   /*default:
							   crewtype="其他";
							   break;*/
					   }
					   content.push("<td>"+crewtype+"</td>");
					   //剧组名称
					   content.push("<td><a title='查看剧组信息' class='link-a' onclick='gotoCrewSetting(\""+obj.crewId+"\")'>"+obj.crewName+"</a><span title='删除' name='delfloatspan' class='delfloatspan' crewId='"+obj.crewId+"' onclick='delCrewInfo(this)'></span></td>");
					   //有效期
					   content.push("<td>"+isNull(obj.startDate).replace(/-/g,'/') +'-'+isNull(obj.endDate).replace(/-/g,'/') +"</td>");
					   //拍摄期
					   var shootStartDate=isNull(obj.shootStartDate);
					   if(shootStartDate){
						   shootStartDate=shootStartDate.replace(/-/g,'/');
					   }else{
						   shootStartDate="";
					   }
					   var shootEndDate=isNull(obj.shootEndDate);
					   if(shootEndDate){
						   shootEndDate=shootEndDate.replace(/-/g,'/');
					   }else{
						   shootEndDate="";
					   }
					   content.push("<td>"+shootStartDate+'-'+shootEndDate +"</td>");
					   //制片公司
					   content.push("<td>"+isNull(obj.company)+"</td>");
					   //导演
					   content.push("<td>"+isNull(obj.director)+"</td>");
					   //项目类型
					   var projectType="";
					   switch(obj.projectType){
						   case 0:
							   projectType="普通项目";
							   break;
						   case 1:
							   projectType="试用项目";
							   break;
						   case 2:
							   projectType="内部项目";
							   break;
					   }
					   content.push("<td>"+projectType+"</td>");
					   //拍摄进度
					   content.push("<td>"+obj.finishedViewCount+"/"+obj.totalViewCount+"</td>");
					   //费用进度
					   var totalPayedMoney=isNull(obj.totalPayedMoney);//支出
					   var totalBudgetMoney=isNull(obj.totalBudgetMoney);//预算
					   if(!totalPayedMoney && !totalBudgetMoney){
						   content.push("<td></td>");
					   }else{
						   if(!totalPayedMoney) {
							   totalPayedMoney="--";
						   }else{
							   totalPayedMoney=fmoney(totalPayedMoney,2);
						   }
						   if(!totalBudgetMoney){
							   totalBudgetMoney="--";
						   }else{
							   totalBudgetMoney=fmoney(totalBudgetMoney,2);
						   }
						   content.push("<td>"+totalPayedMoney+"/"+totalBudgetMoney+"</td>");
					   }
					   if(obj.isStop) {
						   content.push("<td><img src='../images/stop.png'></td>");
					   } else if(obj.endDate < today){
						   content.push("<td><img src='../images/outofdate.png'></td>");
					   }else{
						   content.push("<td></td>");
					   }
					   content.push("</tr>");
				   }
				   //添加之前先清空数据
					var trArr = $("tr[name='tableName']");
					for (var a = 0; a < trArr.length; a++) {
						trArr[a].remove();
					}
					//添加数据
				   $("#firstTr").after(content.join(""));
				   addDelFloatHover();
			   }else{
				   showErrorMessage(data.message);
			   }
		   },
		   error:function(){}
	   });
}
//设置删除浮动
function addDelFloatHover()
{
	$("span[name='delfloatspan']").parents("td").parents("tr").hover(function()
	{
		$(this).find("span").show();
	},
	function()
	{
		$(this).find("span").hide();
	});	
}
//判空
function isNull(exp){
	if (!exp || typeof(exp)=="undefined"){
		return "";
	}else{
		return exp;
	}
}
//显示筛选框
function showDropdown(obj,ev){
	var nextobj=$(obj).next();
	var state=nextobj.css('display');
	hideDropdown();
	if(state=='none'){
		$(obj).removeClass('btn-div').addClass('btn-click-div');
		$(nextobj).show();
		//$(nextobj).find('input').eq(0).focus();
	}else{
		$(obj).removeClass('btn-click-div').addClass('btn-div');
		$(nextobj).hide();
	}
	ev.stopPropagation();
}
//隐藏筛选框
function hideDropdown(id){
	$(".dropdown_box").hide();
	if(i){
		if(id!='crewtypeSelect'){
			$("#crewtypeSelect").next().removeClass('open');
		}
		if(id!='projecttypeSelect'){
			$("#projecttypeSelect").next().removeClass('open');
		}
	}
	$(".dropdown_box").prev().removeClass('btn-click-div').addClass('btn-div');
}
//清空选中的下拉选项
function clearSelection(own){
	own = $(own);
	own.siblings(".selectpicker").selectpicker('deselectAll');
	//重新加载数据
	loadCrewList();
}
//初始化下拉菜单事件
function initSelectChange(){
	//剧组类型筛选
	$('#crewtypeSelect').on('change',function(){
		//重新加载数据
		loadCrewList();
	});
	//项目类型筛选
	$("#projecttypeSelect").on('change',function(){
		//重新加载数据
		loadCrewList();
	});
}
//条件筛选
function searchContent(){
	//重新加载数据
	loadCrewList();
	//隐藏筛选框
	hideDropdown();
}
//清空筛选条件
function clearContent(id){
	if(id=="crewname"){
		$("#crewnameSelect").val("");
	}else if(id=="date"){
		$("#startDateSelect").val("");
		$("#endDateSelect").val("");
	}else if(id=="shootdate"){
		$("#shootStartDateSelect").val("");
		$("#shootEndDateSelect").val("");
	}else if(id=="company"){
		$("#companySelect").val("");
	}else if(id=="director"){
		$("#directorSelect").val("");
	}
	searchContent();
}

//加载查询、排序等条件
function loadCondition(){
	if(crewFilter) {
		var crewType=crewFilter.crewType;
		if(crewType) {
			$("#crewtypeSelect").val(crewType.split(','));
		}
		var crewName=crewFilter.crewName;
		if(crewName) {
			$("#crewnameSelect").val(crewName);
		}
		var startDate=crewFilter.startDate;
		if(startDate){
			$("#startDateSelect").val(startDate);
		}
		var endDate=crewFilter.endDate;
		if(endDate){
			$("#endDateSelect").val(endDate);
		}
		var shootStartDate=crewFilter.shootStartDate;
		if(shootStartDate) {
			$("#shootStartDateSelect").val(shootStartDate);
		}
		var shootEndDate=crewFilter.shootEndDate;
		if(shootEndDate) {
			$("#shootEndDateSelect").val(shootEndDate);
		}
		var company=crewFilter.company;
		if(company) {
			$("#companySelect").val(company);
		}
		var director=crewFilter.director;
		if(director) {
			$("#directorSelect").val(director);
		}
		var outofdate=crewFilter.outofdate;
		if(outofdate) {
			$("#outofdate").val(outofdate);
		}
	}
}
//加载高级查询结果
function loadSearchResult(){
	//剧组类型
	var crewType =$("#crewtypeSelect").val();
	//剧组名称
	var crewName =$("#crewnameSelect").val();
	//有效期
	var startDate =$("#startDateSelect").val();
	var endDate =$("#endDateSelect").val();
	//拍摄期
	var shootStartDate =$("#shootStartDateSelect").val();
	var shootEndDate =$("#shootEndDateSelect").val();
	//制片公司
	var company = $("#companySelect").val(); 
	//导演
	var director = $("#directorSelect").val(); 
	//项目分类
	var projectType = $("#projecttypeSelect").val(); 
	//是否有效
	var outofdate = $("#outofdate").val();
	
	//剧组类型
	if(crewType){
		var crewTypeStr = "";
		
		for(var i=0;i<crewType.length;i++){
			crewTypeStr+=crewType[i]+",";
		}
		crewTypeStr=crewTypeStr.substring(0,crewTypeStr.length-1);
		crewFilter.crewType=crewTypeStr;
	}else{
		crewFilter.crewType="";
	}
	//项目类型
	if(projectType) {
		var projectTypeStr = "";
		
		for(var i=0;i<projectType.length;i++){
			projectTypeStr+=projectType[i]+",";
		}
		projectTypeStr=projectTypeStr.substring(0,projectTypeStr.length-1);
		crewFilter.projectType=projectTypeStr;
	}else{
		crewFilter.projectType="";
	}
	//剧组名称
	if(crewName){
		crewFilter.crewName=crewName;
	}else{
		crewFilter.crewName="";
    }
	//有效期
	if(startDate && endDate){
		if(startDate>=endDate){
			showErrorMessage("有效期开始日期需大于结束日期");
			return;
		}
	}
	if(startDate){
		crewFilter.startDate=startDate;
	}else{
		crewFilter.startDate="";
    }
	if(endDate){
		crewFilter.endDate=endDate;
	}else{
		crewFilter.endDate="";
    }
	//拍摄期
	if(shootStartDate && shootEndDate){
		if(shootStartDate>=shootEndDate){
			showErrorMessage("拍摄期开始日期需大于结束日期");
			return;
		}
	}	
	if(shootStartDate){
		crewFilter.shootStartDate=shootStartDate;
	}else{
		crewFilter.shootStartDate="";
    }
	if(shootEndDate){
		crewFilter.shootEndDate=shootEndDate;
	}else{
		crewFilter.shootEndDate="";
    }
	//制片公司
	if(company){
		crewFilter.company=company;
	}else{
		crewFilter.company="";
    }	
	//导演
	if(director){
		crewFilter.director=director;
	}else{
		crewFilter.director="";
    }
	crewFilter.outofdate=outofdate;	
	//排序
	var sortStr=[];
	var sortOrder=[];
	$("input[name='sortfieldchk']").each(function(index){
		if($(this).attr('checked')) {
			var str = $(this).attr('value');
			var obj=$("input[name='sortchk']").eq(index);
			if($(obj).attr('checked')) {
				str += ' desc';
			}
			sortStr.push(str);
		}
		sortOrder.push($(this).attr('value'));
	});
	crewFilter.crewSortCon=sortStr.join(",");
	crewFilter.crewSortOrder=sortOrder.join(",");
}
//跳转到剧组设置页面
function gotoCrewSetting(crewId){
//	window.location.href="/crewManager/toCrewManagePage?crewId="+crewId+"&crewFilter="+JSON.stringify(crewFilter);
	$("#rightPopUpWin").show().animate({"right":"0px"}, 500);
	//$("#crewContentIframe").attr("src", "/crewManager/toCrewSetPage?crewId="+crewId + "&&flag=crew");
	$("#crewContentDiv").load("/crewManager/toCrewSetPage?crewId="+crewId + "&&flag=crew");
}
//创建剧组
function showAddCrewWin(){
    $("#addCrewDiv").find("iframe").attr("src", "/crewManager/toCrewDetailPageForAdmin?flag=add");
    $("#addCrewDiv").jqxWindow("open");
}
//关闭创建剧组窗口
function closeCrewDetailWindow() {
    $("#addCrewDiv").jqxWindow("close");
    loadCrewList();
}
//删除剧组
function delCrewInfo(obj){
	var crewId=$(obj).attr('crewId');
	//查询剧组是否有未删除的数据
    $.ajax({
        url: "/crewManager/queryCrewInfoNum",
        type: "post",
        dataType: "json",
        data:{crewId:crewId},
        success: function(response) {
            if (response.success) {
                var crewInfoNum = response.crewInfoNum;
                var total=0;
                for(var key in crewInfoNum) {
                	total+=crewInfoNum[key];
                }
                if(total!=0 || crewInfoNum.financePassword=='1'){
                	showErrorMessage("存在未清除的数据，请先清除");
                	return;
                }
                popupPromptBox("提示","确定删除吗?",function(){
					$.ajax({
						url:"/crewManager/deleteCrew",
						type:"post",
						dataType:"json",
						data:{
							crewId:crewId
						},
						async:true,
						success:function(response){
							if(response.success){
								showSuccessMessage(response.message);
								loadCrewList();
							}else{
								showErrorMessage(response.message);
							}
						}
					});
				});
            } else {
            	showErrorMessage(response.message);
            }
        }
    });
}
//加载排序可拖动表格
function initSortGrid(){
	var idArr=['createTime','crewType','crewName','startDate','endDate','shootStartDate','company','director'];
	var textArr=['最后更新时间','剧组类型','剧组名称','有效开始时间','有效结束时间','开机时间','制片公司','导演'];
	var data=[];
	if(crewFilter.crewSortCon) {
		var crewSortOrder=crewFilter.crewSortOrder.split(',');
		var crewSortConArr=crewFilter.crewSortCon.split(',');
		for(var m=0;m<crewSortOrder.length;m++){
			for(var i=0;i<idArr.length;i++){
				if(idArr[i]==crewSortOrder[m]) {
					var flag=false;
					for(var j=0;j<crewSortConArr.length;j++){
						if(crewSortConArr[j].indexOf(idArr[i])>=0){
							flag=true;
							if(crewSortConArr[j].indexOf('desc')>=0){
								data.push({id:idArr[i],text:textArr[i],value:'1',ascstate:'1'});
							} else {
								data.push({id:idArr[i],text:textArr[i],value:'1',ascstate:'0'});
							}
							break;
						}
					}
					if(!flag){
						data.push({id:idArr[i],text:textArr[i],value:'0',ascstate:'0'});
					}
					break;
				}
			}
		}
	} else {
		data=[{id:'createTime',text:'最后更新时间',value:'1',ascstate:'1'},
	          {id:'crewType',text:'剧组类型',value:'0',ascstate:'0'},
	          {id:'crewName',text:'剧组名称',value:'0',ascstate:'0'},
	          {id:'startDate',text:'有效开始时间',value:'0',ascstate:'0'},
	          {id:'endDate',text:'有效结束时间',value:'0',ascstate:'0'},
	          {id:'shootStartDate',text:'开机时间',value:'0',ascstate:'0'},
	          {id:'company',text:'制片公司',value:'0',ascstate:'0'},
	          {id:'director',text:'导演',value:'0',ascstate:'0'}];
	}
	$('#sortGrid').datagrid({
	    width:'100%',
	    nowrap:true,
	    striped:true,
	    singleSelect:true,
	    collapsible:true,
	    fitColumns:true,
	    columns:[[
	        {field:'id',title:'选择',width:'7%',align:'center',formatter:function(value,row,index){
	        	if(row["value"]=='1'){
		        	return '<input name="sortfieldchk" type="checkbox" id="sortfield_'+row["id"]+'" value="'+row["id"]+'" checked onclick="sortfieldClick(this)">';
	        	}else{
		        	return '<input name="sortfieldchk" type="checkbox" id="sortfield_'+row["id"]+'" value="'+row["id"]+'" onclick="sortfieldClick(this)">';
	        	}
	        }},  
	        {field:'text',title:'排序字段',width: '60%',align:'center'},   
	        {field:'ascstate',title:'顺序',width: '32%',align:'center',formatter:function(value,row,index){
	        	if(value && value=='1'){
		        	return '<label style="font-weight:normal;"><input name="sortchk" type="checkbox" id="sortchk_'+row["id"]+'" value="1" checked onclick="sortchkClick(this)">DESC</label>';
	        	}else{
		        	return '<label style="font-weight:normal;"><input name="sortchk" type="checkbox" id="sortchk_'+row["id"]+'" value="1" onclick="sortchkClick(this)">DESC</label>';
	        	}
	        }}
	    ]],
	    
	    onLoadSuccess:function(){
	    	$(this).datagrid('enableDnd');
		},
		
		onStopDrag: function(row){
			
        },
        
        onDrop:function(targetRow,sourceRow,point){
        }
	});
	  
	$('#sortGrid').datagrid('loadData', data);
}
//打开排序窗口
function showSelectSortWin(){
	$("#sortdiv").jqxWindow("open");
}
//关闭排序窗口
function closeSelectSortWin(){
	$("#sortdiv").jqxWindow("close");
}
//排序确定
function sortSure(){
	closeSelectSortWin();
	loadCrewList();
}
//排序选择
function sortfieldClick(obj){
	if(!$(obj).attr('checked')){
		$("input[name='sortchk'][id='sortchk_"+$(obj).attr('value')+"']").attr('checked',false);
	}
}
//asc、desc选择
function sortchkClick(obj) {
	if($(obj).attr('checked')) {
		var id=$(obj).attr('id');
		var index=id.substring(id.indexOf('_')+1);
		$("input[name='sortfieldchk'][id='sortfield_"+index+"']").attr('checked',true);
	}
}




//关闭滑动窗口
function closeRightPopupWin(){
	var width = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-width}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
	loadCrewList();	
}