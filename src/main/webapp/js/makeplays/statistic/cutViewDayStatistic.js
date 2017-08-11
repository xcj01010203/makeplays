$(document).ready(function() {
	initProgressDate();
});
var optionData;
function initProgressDate(){
	var crewId = $("#crewId").val();
	var userId = $("#userId").val();
	$.ajax({
		url: "/cutViewStatisticManager/appIndex/queryCutViewStatistic",
		type: "post",
		dataType: "json",
		data: {crewId: crewId, userId: userId},
		success: function(response) {
			if(!response.success) {
				return;
			}
			var preDayCutLength = response.preDayCutLength;
			//初始化按场
			genCutViewDayBarChart(preDayCutLength);
		}
	});
}

//生成日剪辑量柱状图
function genCutViewDayBarChart (preDayCutLength) {
	var xAxisData = [];
	var seriesData = [];
	for(var i=0; i<preDayCutLength.length; i++){
		var item = preDayCutLength[i];
		if (item.noticeDate == null) {
			  item.noticeDate = '';
		}
		xAxisData.push(item.noticeDate);
		seriesData.push(((item.cutMinutes)/60).toFixed(2));
	}
	
	var myChart = echarts.init(document.getElementById('echartDiv'));
	var option = {
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
	        show:false
	    },
	    xAxis : [
	        {
	            type : 'category',
	            //name:'日期',
	            data : xAxisData,
	            axisLabel: {
                 	rotate:30
                }
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
	            name: 'min'
	        }
	    ],
	    series : [{
	        type: 'bar',
	        name: '剪辑量',
	        itemStyle:{normal:{color:'#039be5'}},
	        data: seriesData
	    }]
	};
	
	myChart.setOption(option);
}