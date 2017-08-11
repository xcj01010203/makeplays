var global_colors=['#0067a4','#00acd6','#009550','#da8c10','#771143','#dc5e4b'];
var progressBarColor='#f0a132';//进度条颜色
var progressLeftFinal=7;//进度条left
var totalMap=null;//总体进度
var siteMap=null;//分项进度
var dateMap=null;//日拍摄量
var dateList=null;//日累计拍摄量
var majorViewRoleList=null;//主要演员
var guestViewRoleList=null;//特约演员
var viewRoleDayList=null;//演员-日拍摄量
var viewRoleSeriesList=null;//演员-戏量按集分布
var shootLocationList=null;//拍摄地
var viewRoleName=null;//当前查看的角色名称
var yAxisName='页数';
var viewPageType=2;
$(document).ready(function() {
	//$(".contentdiv").css('height',document.body.clientHeight-145);
	//tab页切换事件
	$(".tab_wrap>ul>li").click(function(){
		if(!$(this).hasClass('tab_li_current')) {
			$(this).siblings().removeClass('tab_li_current');
			$(this).addClass('tab_li_current');
			$(".contentdiv").hide();
			$("#div_"+$(this).index()).show();
			if($(this).html()=='总体进度'){
				loadTotalProgress($("#statisticsType").is(":checked")?2:1);
			}
		}
	});
	
	//加载总体进度
	loadTotalProduction();
	//加载日进度
	loadDayProduction();
	//加载场景角色进度
	loadViewRoleProduction();
	//加载拍摄地
	loadShootLocationProduction();
});

//场、页切换事件
function viewPageChange(own){
	var $this = $(own);
	var viewRoleType=$("input[name='viewRoleType']:checked").val();
	if($this.is(":checked")){ //页
		$("#byPage").removeClass("font-color");
		$("#byView").addClass("font-color");
		viewPageType=2;
		yAxisName='页数';
	}else{//场
		$("#byPage").addClass("font-color");
		$("#byView").removeClass("font-color");
		viewPageType=1;
		yAxisName='场数';
	}
	loadTotalProgress();
	initDayChart();
	initViewRoleChart(viewRoleType);
	initShootLocationChart();
	if(viewRoleName) {
		initRoleDayChart();
		initRoleSeriesChart();
	}
}
//演员类型切换
function viewRoleTypeChange(obj){
	//var viewPageType=$("#statisticsType").is(":checked")?2:1;
	initViewRoleChart($(obj).val());
	//将当前正在查看的角色置空
	viewRoleName=null;
	//隐藏角色-日拍摄量，角色-戏量按集分布
	$("#viewrole_shootdays").hide();
	$("#viewrole_day_chart").hide();
	$("#viewrole_series_chart").hide();
}
//总体进度
function loadTotalProduction(){
	$.ajax({
		url: "/shootStatistic/queryTotalProduction",
		type: "post",
		dataType: "json",
		data: {},
		success: function(response) {
			if (response.success) {
				totalMap = response.total;
				var pageDateCha = totalMap.pageDateCha;
				/*var fore_word='';
				var fore_figure='';
				if(pageDateCha>=0){
					fore_word='逾期';
					fore_figure=pageDateCha;
				}else{
					fore_word='提前';
					fore_figure=-pageDateCha;
				}*/
				var foreHtml='';
				if(pageDateCha > 0) {
					foreHtml='当前<span class="figure result_word">已提前<span class="result_figure">'+pageDateCha+'</span>天</span>';
				}else if(pageDateCha == 0) {
					foreHtml='当前<span class="figure result_word">如期进行</span>';
				}else{
					foreHtml='当前<span class="figure result_word">已超期<span class="result_figure">'+(-pageDateCha)+'</span>天</span>';
				}
				var html=[];
				html.push('<ul class="total_ul">');
				html.push('	<li>计划拍摄天数：<span class="figure plan">'+totalMap.planShootDate+'</span>天，实际拍摄天数：<span class="figure real">'+totalMap.shootDate+'</span>天</li>');
				if (totalMap.everyDayPageCount == null || totalMap.everyDayPageCount == '' || totalMap.everyDayPageCount == undefined) {
					totalMap.everyDayPageCount = 0.00;
				}
				if (totalMap.planEveryDayPageCount == null || totalMap.planEveryDayPageCount == '' || totalMap.planEveryDayPageCount == undefined) {
					totalMap.planEveryDayPageCount = 0.00;
				}
				html.push('	<li>计划日均完成：<span class="figure plan">'+parseFloat(totalMap.planEveryDayPageCount).toFixed(1)+'</span>页，实际日均完成：<span class="figure real">'+parseFloat(totalMap.everyDayPageCount).toFixed(1)+'</span>页</li>');
				html.push('</ul>');
				html.push('<img src="../images/right.png" class="total_rightimg">');
				html.push('<ul class="total_ul">');
				html.push('	<li>'+foreHtml+'</li>');
				if (totalMap.needPageCount == null || totalMap.needPageCount == '' || totalMap.needPageCount == undefined) {
					totalMap.needPageCount = 0.00;
				}
				html.push('	<li>按原计划完成需日均拍摄<span class="figure result_figure">'+parseFloat(totalMap.needPageCount).toFixed(1)+'</span>页</li>');
				html.push('</ul>');
				$("#total_title").html(html.join(''));
				//分项进度
				siteMap=response.siteMap;
				loadTotalProgress();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
}
//加载总进度、分项进度
function loadTotalProgress(){
	var keyValue1='';
	var keyValue2='';
	if(viewPageType==1){
		keyValue1='totalViewCount';
		keyValue2='finishedViewCount';
	}else if(viewPageType==2){
		keyValue1='totalPageCount';
		keyValue2='finishedPageCount';
	}
	//总进度
	if(totalMap){
		if (totalMap[keyValue2] == null || totalMap[keyValue2] =='' || totalMap[keyValue2] == undefined) {
			totalMap[keyValue2] = 0.00;
		}
		if (totalMap[keyValue1] == null || totalMap[keyValue1] == '' || totalMap[keyValue1] == undefined) {
			totalMap[keyValue1] = 0.00;
		}
		var html='总进度:&nbsp;'+parseFloat(totalMap[keyValue2]).toFixed(1)+'/'+parseFloat(totalMap[keyValue1]).toFixed(1);
		if(viewPageType==2){
			html+='<li>(集数:&nbsp;'+totalMap.finishedSeriesNo+'/'+totalMap.totalSeriesNo+')</li>';
		}
		$("#total").html(html);
		$(".jindu_tiao").css("width",divide(totalMap[keyValue2], totalMap[keyValue1])*100+'%');
	}
	//分项进度
	if(siteMap){
		/*进度条显示*/		
		//内外进度条
		var inside=siteMap['内戏'];
		var outside=siteMap['外戏'];
		if (inside[keyValue2] == null || inside[keyValue2] == '' || inside[keyValue2] == undefined ) {
			inside[keyValue2] = 0.0;
		}
		if (inside[keyValue1] == null || inside[keyValue1] == '' || inside[keyValue1] == undefined) {
			inside[keyValue1] = 0.0;
		}
		if (outside[keyValue2] == null || outside[keyValue2] == '' || outside[keyValue2] == undefined) {
			outside[keyValue2] = 0.0;
		}
		if (outside[keyValue1] == null || outside[keyValue1] == '' || outside[keyValue1] == undefined) {
			outside[keyValue1] = 0.0;
		}
		$("#inside").html("内戏:&nbsp;" + parseFloat(inside[keyValue2]).toFixed(1) + "/" + parseFloat(inside[keyValue1]).toFixed(1));
		$("#outside").html("外戏:&nbsp;" + parseFloat(outside[keyValue2]).toFixed(1) + "/" + parseFloat(outside[keyValue1]).toFixed(1));		 
		twoContrastProgressBar($("#neiProgress"), $("#waiProgress"), $("#neiCountFlag"), $("#waiCountFlag"), inside[keyValue1], outside[keyValue1], inside[keyValue2], outside[keyValue2]);		 
		 
		//日夜进度条
		var day=siteMap['日戏'];
		var night=siteMap['夜戏'];
		if (day[keyValue2] == null || day[keyValue2] == '' || day[keyValue2] == undefined ) {
			day[keyValue2] = 0.0;
		}
		if (day[keyValue1] == null || day[keyValue1] == '' || day[keyValue1] == undefined) {
			day[keyValue1] = 0.0;
		}
		if (night[keyValue2] == null || night[keyValue2] == '' || night[keyValue2] == undefined) {
			night[keyValue2] = 0.0;
		}
		if (night[keyValue1] == null || night[keyValue1] == '' || night[keyValue1] == undefined) {
			night[keyValue1] = 0.0;
		}
		$("#day").html("日戏:&nbsp;" + parseFloat(day[keyValue2]).toFixed(1) + "/" + parseFloat(day[keyValue1]).toFixed(1));
		$("#night").html("夜戏:&nbsp;" +parseFloat(night[keyValue2]).toFixed(1) + "/" +parseFloat(night[keyValue1]).toFixed(1));
		twoContrastProgressBar($("#dayProgress"), $("#nightProgress"), $("#dayCountFlag"), $("#nightCountFlag"), day[keyValue1], night[keyValue1], day[keyValue2], night[keyValue2]);		 
		 
		/*//文武戏进度条
		var literate=siteMap['文戏'];
		var kongfu=siteMap['武戏'];
		$("#literate").html("文戏:&nbsp;" + literate[keyValue2] + "/" + literate[keyValue1]);
		$("#kongfu").html("武戏:&nbsp;" + kongfu[keyValue2] + "/" + kongfu[keyValue1]);
		twoContrastProgressBar($("#wenProgress"), $("#wuProgress"), $("#wenCountFlag"), $("#wuCountFlag"), literate[keyValue1], kongfu[keyValue1], literate[keyValue2], kongfu[keyValue2]);*/
	}
}
//日进度
function loadDayProduction(){
	$.ajax({
		url: "/shootStatistic/queryDayProduction",
		type: "post",
		dataType: "json",
		data: {},
		success: function(response) {
			if (response.success) {
				dateMap = response.dateMap;
				dateList = response.dateList;
				initDayChart();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
}
//加载日进度图形
function initDayChart() {
	var valueKey = "";
	if(viewPageType==1) {
		valueKey="viewCount";
	} else {
		valueKey="pageCount";
	}
	//日拍摄量
	if(dateMap) {
		var xAxisData=[];
		var seriesMap = new HashMap();
		var n=0;
		for(var i in dateMap) {
			xAxisData.push(i);
			var groupList=dateMap[i];
			for(var j=0;j<groupList.length;j++){
				var groupName=groupList[j].groupName;
				var data=groupList[j][valueKey];
				if(!seriesMap.containsKey(groupName)) {
					seriesMap.put(groupName, {name:groupName,type:'bar',itemStyle:{normal:{color:global_colors[n]}},stack:'分组',data:[data]});
					n++;
				}else{
					seriesMap.get(groupName).data.push(data);
				}
			}
		}
		initChart("day_chart", "日拍摄量", seriesMap.keys(), xAxisData, seriesMap.values(),null,null,true);
	}else{
		$("#day_chart").html('<center>暂无数据。</center>');
	}
	//拍摄进度累计
	if(dateList) {
		var xAxisData=[];
		var legendData=['合计'];
		var seriesData=[];
		for(var i=0;i<dateList.length;i++){
			xAxisData.push(dateList[i].noticeDate);
			seriesData.push(dateList[i][valueKey]);			
		}
		initChart("daytotal_chart", "拍摄进度累计", legendData, xAxisData, [{name:'合计',type:'line',itemStyle:{normal:{color:'#71A450'}},smooth:true,data:seriesData}],'line',null,true);
	}
}
//场景角色进度
function loadViewRoleProduction(){
	$.ajax({
		url: "/shootStatistic/queryViewRoleProduction",
		type: "post",
		dataType: "json",
		data: {},
		success: function(response) {
			if (response.success) {
				majorViewRoleList = response.majorList;
				guestViewRoleList = response.guestList;
				initViewRoleChart(1);
			} else {
				showErrorMessage(response.message);
			}
		}
	});
}
/**
 * 加载场景角色进度
 * @param id 1:场、2:页
 * @param viewRoleType 角色类型
 */
function initViewRoleChart(viewRoleType){
	var valueKey1 = "";
	var valueKey2 = "";
	var legendData=null;
	if(viewPageType==1) {
		valueKey1="viewCount";
		valueKey2="finishedViewCount";
		legendData=['总场数','已完成场数'];
	} else {
		valueKey1="pageCount";
		valueKey2="finishedPageCount";
		legendData=['总页数','已完成页数'];
	}
	var objList=null;
	if(viewRoleType==1){
		objList=majorViewRoleList;
	}else{
		objList=guestViewRoleList;
	}
	if(objList) {
		//排序
		objList.sort(function(a,b){
			if(a[valueKey1] > b[valueKey1]) {
				return -1;
			}else if(a[valueKey1] > b[valueKey1]) {
				return 0;
			}else{
				return 1;
			}
		});
		var xAxisData=[];
		var seriesData1=[];
		var seriesData2=[];
		for(var i=0;i<objList.length;i++){
			xAxisData.push(objList[i].viewRoleName);
			seriesData1.push({value:objList[i][valueKey1],viewRoleId:objList[i].viewRoleId});
			seriesData2.push({value:objList[i][valueKey2],viewRoleId:objList[i].viewRoleId});		
		}
		initChart("viewrole_chart", null, legendData, xAxisData, 
				[{name:legendData[0],type:'bar',itemStyle:{normal:{color:'#0067a4'}},barGap:'-50%',data:seriesData1},
				 {name:legendData[1],type:'bar',itemStyle:{normal:{color:'#71A450'}},data:seriesData2}],null,0,false,loadRoleDetail);
	}
}
//角色日拍摄量、分集戏量
function loadRoleDetail(params){
	viewRoleName=params.name;
	var viewRoleId=params.data.viewRoleId;
	//已拍摄天数、预计拍摄天数
	$.ajax({
		url: "/shootStatistic/queryViewRoleShootDays",
		type: "post",
		dataType: "json",
		data: {viewRoleId:viewRoleId},
		success: function(response) {
			if (response.success) {
				var html=[];
				html.push(viewRoleName+'：已拍摄'+response.finishedDays+'天');
				if(response.shootDays) {
					var chaDays = response.shootDays-response.finishedDays;
					if(chaDays>=0) {
						html.push('，尚需拍摄' + chaDays + '天');
					} else {
						html.push('，超期拍摄' + chaDays + '天');
					}
				}
				$("#viewrole_shootdays").html(html.join(''));
				$("#viewrole_shootdays").show();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
	//日拍摄量
	$.ajax({
		url: "/shootStatistic/queryViewRoleDayProduction",
		type: "post",
		dataType: "json",
		data: {viewRoleId:viewRoleId},
		success: function(response) {
			if (response.success) {
				viewRoleDayList = response.result;
				initRoleDayChart();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
	//戏量按集分布
	$.ajax({
		url: "/shootStatistic/queryViewRoleSeries",
		type: "post",
		dataType: "json",
		data: {viewRoleId:viewRoleId},
		success: function(response) {
			if (response.success) {
				viewRoleSeriesList = response.result;
				initRoleSeriesChart();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
}
//加载角色-日拍摄量
function initRoleDayChart() {
	var valueKey="";
	var legendData=null;
	if(viewPageType==1) {
		valueKey="viewCount";
		legendData=['场'];
	} else {
		valueKey="pageCount";
		legendData=['页'];
	}
	var xAxisData=[];
	var seriesData=[];
	for(var i=0;i<viewRoleDayList.length;i++){
		xAxisData.push(viewRoleDayList[i].noticeDate);
		seriesData.push(viewRoleDayList[i][valueKey]);
	}
	initChart("viewrole_day_chart", viewRoleName+"-日拍摄量", legendData, xAxisData, [{name:legendData[0],type:'bar',itemStyle:{normal:{color:'#71A450'}},data:seriesData}],null,null,true);
}
//加载角色-戏量按集分布
function initRoleSeriesChart(){
	var valueKey="";
	var legendData=null;
	if(viewPageType==1) {
		valueKey="viewCount";
		legendData=['场'];
	} else {
		valueKey="pageCount";
		legendData=['页'];
	}
	var xAxisData=[];
	var seriesData=[];
	for(var i=0;i<viewRoleSeriesList.length;i++){
		xAxisData.push(viewRoleSeriesList[i].seriesNo);
		seriesData.push(viewRoleSeriesList[i][valueKey]);
	}
	initChart("viewrole_series_chart", viewRoleName+"-戏量按集分布", legendData, xAxisData, [{name:legendData[0],type:'bar',itemStyle:{normal:{color:'#0067a4'}},data:seriesData}],null,0,true);
}
//拍摄地进度
function loadShootLocationProduction(){
	$.ajax({
		url: "/shootStatistic/queryLocationProduction",
		type: "post",
		dataType: "json",
		data: {},
		success: function(response) {
			if (response.success) {
				shootLocationList = response.result;
				initShootLocationChart();
			} else {
				showErrorMessage(response.message);
			}
		}
	});
}
//加载拍摄地进度
function initShootLocationChart(){
	var valueKey1 = "";
	var valueKey2 = "";
	var legendData=null;
	if(viewPageType==1) {
		valueKey1="viewCount";
		valueKey2="finishedViewCount";
		legendData=['总场数','已完成场数'];
	} else {
		valueKey1="pageCount";
		valueKey2="finishedPageCount";
		legendData=['总页数','已完成页数'];
	}
	if(shootLocationList) {
		//排序
		shootLocationList.sort(function(a,b){
			if(a[valueKey1] > b[valueKey1]) {
				return -1;
			}else if(a[valueKey1] > b[valueKey1]) {
				return 0;
			}else{
				return 1;
			}
		});
		var xAxisData=[];
		var seriesData1=[];
		var seriesData2=[];
		for(var i=0;i<shootLocationList.length;i++) {
			xAxisData.push(shootLocationList[i].shootLocation);
			seriesData1.push(shootLocationList[i][valueKey1]);
			seriesData2.push(shootLocationList[i][valueKey2]);
		}
		initChart("shootlocation_chart", null, legendData, xAxisData, 
				[{name:legendData[0],type:'bar',itemStyle:{normal:{color:'#0067a4'}},barGap:'-50%',data:seriesData1},
				 {name:legendData[1],type:'bar',itemStyle:{normal:{color:'#71A450'}},data:seriesData2}],null,0,false);
	}
}
//获取图表option
function getOption(title,legendData,xAxisData,seriesData,axisPointerType,rotate,dataZoom,click) {
	if(!axisPointerType) {
		axisPointerType='shadow';
	}
	var endNum=100; //范围是：0 ~ 100。表示 0% ~ 100%
	var dataZoomShow=false;
	if(xAxisData.length>12){
		if(!dataZoom) {
			endNum = parseInt(12/xAxisData.length*100);
		}
		dataZoomShow=true;
	}
	var option = {
			title:{
				show:true,
				text:title,
				left:'center',
				textStyle: {
					color:'#777777',
					fontSize: 16
				}
			},
			tooltip : {
		        trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : axisPointerType        // 默认为直线，可选为：'line' | 'shadow'
		        },
		        formatter: function(params) {
		        	var returnStr=params[0].name;
		        	for(var i=0;i<params.length;i++){
		        		var val=params[i].value;
		        		if (val && val != 0) {
		        			returnStr+= '<br><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:'+params[i].color+'"></span>' + params[i].seriesName+'：'+val;
		        		}
		        	}
		        	if(typeof(click)=='function') {
		        		returnStr+='<br><font color="#f98a66">点击查看演员日拍摄量和戏量按集分布</font>';
		        	}
		        	return returnStr;
		        }
		    },
		    grid: {
		        left: '2%',
		        right: '80',
		        bottom: '80',
		        containLabel: true
		    },
		    legend: {
		        data:legendData,
		        right:'4%'
		    },
		    xAxis : [
		        {
		            type : 'category',
		            data : xAxisData,
		            axisLabel:{
		            	rotate:rotate==null?30:rotate
		            }
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value',
		            name:yAxisName
		        }
		    ],
		    dataZoom: [
		       {
		    	   show:dataZoomShow,
                   start: 0,
                   end:endNum
               }
            ],
		    series : seriesData
		};
	return option;
}
/**
 * 加载图形
 * @param divId 
 * @param title 图标题
 * @param legendData 图例数据
 * @param xAxisData x轴数据
 * @param seriesData 显示数据
 * @param axisPointerType tooltip鼠标类型，默认为shadow,可为line
 * @param rotate x轴标签旋转角度
 * @param dataZoom true:显示全部 false:显示12个
 * @param click 点击事件
 */
function initChart(divId,title,legendData,xAxisData,seriesData,axisPointerType,rotate,dataZoom,click) {
	$("#"+divId).css('width',document.body.clientWidth*0.9);
	$("#"+divId).show();
	var option = getOption(title, legendData, xAxisData, seriesData,axisPointerType,rotate,dataZoom,click);
	var myChart = echarts.init(document.getElementById(divId));
	if(typeof(click)=='function') {
		myChart.on('click', click);
	}
	myChart.setOption(option);
}