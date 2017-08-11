Array.prototype.S = String.fromCharCode(2);
Array.prototype.in_array = function(e) {
	var r = new RegExp(this.S + e + this.S);
	return (r.test(this.S + this.join(this.S) + this.S));
};
var colorsArr=['#039be5','#2ec7c9','#a7a0df','#e9b58a','#d87a80'];
$(document).ready(function() {
	/*var crewId = $("#crewId").val();
	$.ajax({
		url: "/viewStatisticManager/appIndex/queryDayShootInfo",
		type: "post",
		dataType: "json",
		data: {crewId: crewId},
		success: function(response) {
			var shootedViewStatistic = response.shootedViewStatistic;
			
			var optionData = genDayShootBarOption(shootedViewStatistic);
			genDayShootBarChart(optionData.groupNameArray, optionData.noticeDateArray, optionData.seriesArray);
		}
	});*/
	initProgressDate();
});
var optionData;
var countType = "1";
var periodType = "1";
function initProgressDate(){
	var crewId = $("#crewId").val();
	$.ajax({
		url: "/viewStatisticManager/appIndex/queryDayShootInfo",
		type: "post",
		dataType: "json",
		data: {crewId: crewId},
		success: function(response) {
			var shootedViewStatistic = response.shootedViewStatistic;
			var weekShootedViewStatistic = response.weekShootedViewStatistic;
			optionData = genDayShootBarOption(shootedViewStatistic, weekShootedViewStatistic);
			//初始化按场
			genDayShootBarChart(optionData.groupNameArray_week, optionData.noticeDateArray_week, optionData.seriesArray_week);
		}
	});
}

function showTotalEchart(own, id){
	if($(own).hasClass('blue')) {
		return;
	}
	$(own).siblings().removeClass('blue').addClass('gray');
	$(own).removeClass('gray').addClass('blue');
	var flag=$(own).attr('value');
	if(id==1) {
		countType=flag;
	}else if(id == 2){
		periodType=flag;
	}
	if(countType == 1){
		if(periodType==1){
			genDayShootBarChart(optionData.groupNameArray_week, optionData.noticeDateArray_week, optionData.seriesArray_week);
		}else{
			genDayShootBarChart(optionData.groupNameArray, optionData.noticeDateArray, optionData.seriesArray);
		}
	}else if(countType == 2){
		if(periodType==1){
			genDayShootBarChart(optionData.groupNameArray_week, optionData.noticeDateArray_week, optionData.pageArray_week);
		}else{
			genDayShootBarChart(optionData.groupNameArray, optionData.noticeDateArray, optionData.pageArray);
		}
	}
}


//生成日拍摄量柱状图所需的数据
function genDayShootBarOption(shootedViewStatistic, weekShootedViewStatistic) {
	var optionData = {};
	
	var groupNameArray = [];
	var noticeDateArray = [];
	for (var i = 0; i < shootedViewStatistic.length; i++) {
		var groupName = shootedViewStatistic[i].groupName;
		var noticeDate = shootedViewStatistic[i].noticeDate;
		
		if (!groupNameArray.in_array(groupName)) {
			groupNameArray.push(groupName);
		}
		if (!noticeDateArray.in_array(noticeDate)) {
			noticeDateArray.push(noticeDate);
		}
	}
	groupNameArray.sort();
	
	
	var seriesArray = [];//场
	var pageArray = [];//页
	for (var g = 0; g < groupNameArray.length; g++) {	//第一层遍历组别
		var currGroupName = groupNameArray[g];
		
		var data = [];
		var pageData = [];
		for (var d = 0; d < noticeDateArray.length; d++) {	//第二层遍历日期
			var currDate = noticeDateArray[d];
			
			var currViewCount = 0;
			var currPageCount = 0;
			for (var s = 0; s < shootedViewStatistic.length; s++) {	//第三层遍历查询出的所有数据
				var groupName = shootedViewStatistic[s].groupName;
				var viewCount = shootedViewStatistic[s].viewCount;
				var noticeDate = shootedViewStatistic[s].noticeDate;
				var pageCount;
				if(shootedViewStatistic[s].pageCount != "" && shootedViewStatistic[s].pageCount != null && shootedViewStatistic[s].pageCount != undefined){
					pageCount = shootedViewStatistic[s].pageCount.toFixed(1);
				}else{
					pageCount = shootedViewStatistic[s].pageCount;
				}
				
				
				if (currGroupName == groupName && noticeDate == currDate) {
					currViewCount = viewCount;
					currPageCount = pageCount;
					break;
				}
			}
			data.push(currViewCount);
		    pageData.push(currPageCount);
		}
		
		var singleGroupInfo = {};//场次数据
		singleGroupInfo.name = currGroupName;
		singleGroupInfo.type = "bar";
		singleGroupInfo.stack = "groupData";
		singleGroupInfo.data = data;
		singleGroupInfo.itemStyle={normal:{color:colorsArr[g%5]}};
		seriesArray.push(singleGroupInfo);
		
		var singlePageInfo = {};//页数据
		singlePageInfo.name = currGroupName;
		singlePageInfo.type = "bar";
		singlePageInfo.stack = "groupData";
		singlePageInfo.data = pageData;
		singlePageInfo.itemStyle={normal:{color:colorsArr[g%5]}};
		pageArray.push(singlePageInfo);
		
	}
	
	var groupNameArray_week=[];
	var noticeDateArray_week=[];
	for (var i = 0; i < weekShootedViewStatistic.length; i++) {
		var groupName = weekShootedViewStatistic[i].groupName;
		var noticeDate = '第'+parseInt(weekShootedViewStatistic[i].week)+'周 \n'+weekShootedViewStatistic[i].startDate;
		
		if (!groupNameArray_week.in_array(groupName)) {
			groupNameArray_week.push(groupName);
		}
		if (!noticeDateArray_week.in_array(noticeDate)) {
			noticeDateArray_week.push(noticeDate);
		}
	}
	groupNameArray_week.sort();
	
	var seriesArray_week = [];//场
	var pageArray_week = [];//页
	for (var g = 0; g < groupNameArray_week.length; g++) {	//第一层遍历组别
		var currGroupName = groupNameArray_week[g];
		
		var data = [];
		var pageData = [];
		for (var d = 0; d < noticeDateArray_week.length; d++) {	//第二层遍历日期
			var currDate = noticeDateArray_week[d];
			
			var currViewCount = 0;
			var currPageCount = 0;
			for (var s = 0; s < weekShootedViewStatistic.length; s++) {	//第三层遍历查询出的所有数据
				var groupName = weekShootedViewStatistic[s].groupName;
				var viewCount = weekShootedViewStatistic[s].viewCount;
				var noticeDate = '第'+parseInt(weekShootedViewStatistic[s].week)+'周 \n'+weekShootedViewStatistic[s].startDate;
				var pageCount;
				if(weekShootedViewStatistic[s].pageCount != "" && weekShootedViewStatistic[s].pageCount != null && weekShootedViewStatistic[s].pageCount != undefined){
					pageCount = weekShootedViewStatistic[s].pageCount.toFixed(1);
				}else{
					pageCount = weekShootedViewStatistic[s].pageCount;
				}
				
				
				if (currGroupName == groupName && noticeDate == currDate) {
					currViewCount = viewCount;
					currPageCount = pageCount;
					break;
				}
			}
			data.push(currViewCount);
		    pageData.push(currPageCount);
		}
		
		var singleGroupInfo = {};//场次数据
		singleGroupInfo.name = currGroupName;
		singleGroupInfo.type = "bar";
		singleGroupInfo.stack = "groupData";
		singleGroupInfo.data = data;
		singleGroupInfo.itemStyle={normal:{color:colorsArr[g%5]}};
		seriesArray_week.push(singleGroupInfo);
		
		var singlePageInfo = {};//页数据
		singlePageInfo.name = currGroupName;
		singlePageInfo.type = "bar";
		singlePageInfo.stack = "groupData";
		singlePageInfo.data = pageData;
		singlePageInfo.itemStyle={normal:{color:colorsArr[g%5]}};
		pageArray_week.push(singlePageInfo);
		
	}

	optionData.groupNameArray = groupNameArray;
	optionData.noticeDateArray = noticeDateArray;
	optionData.seriesArray = seriesArray;
	optionData.pageArray = pageArray;
	optionData.groupNameArray_week = groupNameArray_week;
	optionData.noticeDateArray_week = noticeDateArray_week;
	optionData.seriesArray_week = seriesArray_week;
	optionData.pageArray_week = pageArray_week;
	return optionData;
}

//生成日拍摄量柱状图
function genDayShootBarChart (legendData, xAxisData, seriesData) {
	var myChart = echarts.init(document.getElementById('echartDiv'));
	var nameFlag='';
	if(countType == 1){
		nameFlag = "场";
	}
	if(countType == 2){
		nameFlag = "页";
	}
	option = {
	    tooltip : {
	        trigger: 'axis',
	        axisPointer : {
	            type : 'shadow'
	        },
	        formatter: function(params) {
	        	var returnStr=params[0].name;
	        	for(var i=0;i<params.length;i++){
	        		var val=params[i].value;
	        		if (val != 0) {
	        			returnStr+= "<br>" + params[i].seriesName+'：'+val;
	        		}
	        	}
	        	return returnStr;
	        }
	    },
	    dataZoom: {
			show: true,
			showDetail: false,
			handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
	        handleSize: '100%'
        },
        grid: {
        	bottom:65,
	        containLabel: true
        },
	    legend: {
	    	textStyle:{
	    		fontSize:14
	    	},
	        data: legendData
	    },
	    xAxis : [
	        {
	            type : 'category',
	            data : xAxisData,
	            axisLabel: {
                 	rotate:30
                }
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
	            name: nameFlag
	        }
	    ],
	    series : seriesData
	};
	
	myChart.setOption(option);
}