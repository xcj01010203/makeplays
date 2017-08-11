var lineChart;
var barChart;
var colors = ['#3b6ba1', '#71a450', '#ecb137', '#c8463b', '#9265a1', '#527caf', '#83b165', '#efbb59', '#d05c55', 
			'#a37ab0', '#698ebc', '#93bd7a', '#f1c577', '#d7736b', '#815290', '#afb1b0', '#82a1c9', '#a6c911'];
//------------------------------------------------------Line Chart Begin-----------------------------------------------------
/**
 * 类型为line的chart
 * @param {Object} xAxisCategories
 * 		横坐标
 * @param {Object} dataList
 * 		所要展示的数据集（“二维数组”）包含选择剧以及对比剧
 * 		{{},{},{},{}}
 * @param {Object} nameList
 * 		项目名称列表
 * @param {Object} yTitleText
 * 		纵坐标标题
 * @param {Object} viewChartDivId
 * 		展示报表DIV的ID
 */
function getLineChart(xAxisCategories, dataList, nameList, yTitleText, viewChartDivId,title) {
	
	 /*var totalWidth=(25+15)*xAxisCategories.length;//所有柱子及柱子的间隔(15px)的总宽度
	   var byRoleWidth=$('#'+viewChartDivId).width();//容器的宽度
	   if(totalWidth>byRoleWidth){
		  byRoleWidth=byRoleWidth*(totalWidth/byRoleWidth);
		 if(xAxisCategories.length>30){
			 $('#'+viewChartDivId).css('width',byRoleWidth);
		 }
	   }*/
	var step = xAxisCategories.length > 30 ? parseInt(xAxisCategories.length/30) : 0;
	
	var yAxis = [];
    var ydata = {};
    ydata.name="支出";
    ydata.type="line";
    ydata.data=dataList;
    ydata.smooth = true;
    ydata.itemStyle = {normal: {areaStyle: {type: 'default'}}};
    yAxis.push(ydata);
	require(
            [
                'echarts',
                'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
            ],
            function(ec){
           	 var myChart = ec.init(document.getElementById(viewChartDivId)); 
           	 
           	 var option = {
                        tooltip: {
                            show: true
                        },
                        legend: {
                            data:["支出"],
                            //y:'bottom' 
                            x: 'right',
                            y: 15,
                            
                        },
                        color: ["#71a450","#3b6ba1"],
                        backgroundColor:"#f8f8f8",
                        title: {
                       	 text: title,
                       	 x: 'center',
                       	 itemGap: 0,
                       	 padding: 0,
                       	 textStyle: {
                       		 fontSize: 16,
                       		 fontWeight: 'normal',
                       		 color: '#333'
                       	 }
                        },
                        grid:{
                       	 x2:20,
                       	 x:80,
                       	 y2:105,
                       	 y:35
                        },
                        animation : false,
                        dataZoom: {
	                       	 show: true,
	                       	 start: 0,
	                       	 end: 100,
	                       	 dataBackgroundColor: "#B4CEA4",
	                       	 fillerColor: "rgba(180,206,124,0.5)",
	                       	backgroundColor: "rgba(238,238,238,0.5)",
	                        },
                        tooltip: {
                       	 trigger: 'axis',
                       	 formatter: function(param){
                       		 var res = param[0].name+"</br>";
                       		 for(var i=0;i<param.length;i++){
                       			 if(param[i].data!=0){
                       			 	res+=param[i].seriesName+":"+fmoney(param[i].data,2)+"</br>";
                       			 }
                       		 }
                       		 return res;
                       	 },
                       	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'line'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        xAxis : [
                            {
                                type : 'category',
                                splitLine: {show:false},
                                data : xAxisCategories,
                                axisLabel:{
                                	rotate:45
                                }
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value'
                            }
                        ],
                        series : yAxis,
                        
                    };
           	 
           	 myChart.setOption(option); 
           	 /* myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
           	        console.log(param)
           	    }) */
            }
            );
	
	/*var options = {
		//设置时区
		global : {
			useUTC : false
		},
		chart : {
			type : 'areaspline', //spline,line,areaspline			
			zoomType : 'x', //x轴方向可以缩放			
			spacingRight : 20,
			renderTo : viewChartDivId,
			backgroundColor:"#F5FFFA",
			plotBorderColor:'#FF0000',
		},
		xAxis:{
			//tickPosition:xAxisCategories,						
			categories:xAxisCategories,
			type: 'datetime',
		    labels: {  
            	rotation: -45,  
            	style: {  
                	color: '#000000',  
                	fontSize: '8px',  
                	fontWeight: 'normal'  
  				},
  				step:step
            }  
		},
		
		yAxis:{
			//tickInterval: 300000,
			title: {
	          
				text:'单位（元）'
			}
		},
		colors: ['#71a450'],
		title : {
			text : title //图表标题		
		},
		tooltip : {
			headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			pointFormat: '<tr><br/><td style="color:{series.color};padding:0">{series.name}: </td>' +
			'<td align="right" style="padding:0;"><b>{point.y:##,###,##.2f}</b></td></tr>',
			footerFormat: '</table>',
			crosshairs : true,
			shared : true,
		},
		credits: {
            enabled: false
        },
		plotOptions : {  			
			areaspline : {
				dataLabels : {
					enabled : false
			},
				marker : {
					enabled : false
				},
				enableMouseTracking : true
			}
		},
		series: [
			{
				name: '支出',
				data: dataList,
				lineWidth:1,//线条宽度
				marker: {  
                    enabled: true,  
                    fillColor: '#FF0000',  
                    lineWidth: 1,  
                    lineColor: '#FF0000',  
                    radius: 2  
                }  
			  ,turboThreshold: 0
			}
		]
	};
	lineChart = new Highcharts.Chart(options);*/
}
//------------------------------------------------------Line Chart End-----------------------------------------------------

//------------------------------------------------------Column Chart Begin-------------------------------------------------
/**
 * 类型为column的chart
 * @param {Object} setNoList
 * 		横坐标
 * @param {Object} dataList
 * 		所要展示的数据集（“二维数组”）包含选择剧以及对比剧
 * 		{{},{},{},{}}
 * @param {Object} nameList
 * 		项目名称列表
 * @param {Object} yTitleText
 * 		纵坐标标题
 * @param {Object} viewChartDivId
 * 		展示报表DIV的ID
 */
function getBarChart(xAxisCategories, dataList, nameList, yTitleText, viewChartDivId,accountList,childCount,title) {
	var totalWidth=(25+15)*xAxisCategories.length;//所有柱子及柱子的间隔(15px)的总宽度
	   /*var byRoleWidth=$('#'+viewChartDivId).width();//容器的宽度
	   if(totalWidth>byRoleWidth){
		  byRoleWidth=byRoleWidth*(totalWidth/byRoleWidth);
		 if(xAxisCategories.length>30){
			 $('#'+viewChartDivId).css('width',byRoleWidth);
		 }
	   }*/
	   var yAxis = [];
	   var ydata = {};
	   ydata.name="预算";
	   ydata.type="bar";
	   ydata.barGap = "-40%";
	   ydata.data=dataList[0];
	   yAxis.push(ydata);
	   ydata = {};
	   ydata.name="支出";
	   ydata.type="bar";
	   ydata.barGap = "0";
	   ydata.data=dataList[1];
	   yAxis.push(ydata);
	   setTimeout(function(){
	   require(
				[
	             'echarts',
	             'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
	         ],
	            function(ec){
				 var ecConfig = require('echarts/config'); //点击事件	
					
	           	 var myChart = ec.init(document.getElementById(viewChartDivId)); 
	           	 var option = {
	                        tooltip: {
	                            show: true
	                        },
	                        legend: {
	                            data:nameList,
	                            //y:'bottom' 
	                            x: 'right',
	                            y: 15,
	                            
	                        },
	                        title: {
	                       	 text: title,
	                       	 x: 'center',
	                       	 itemGap: 0,
	                       	 padding: 0,
	                       	 textStyle: {
	                       		 fontSize: 16,
	                       		 fontWeight: 'normal',
	                       		 color: '#333'
	                       	 }
	                        },
	                        color: ["#3b6ba1","#71a450"],
	                        backgroundColor:"#f8f8f8",
	                        grid:{
	                       	 x2:20,
	                       	 x:80,
	                       	 y2:105,
	                       	 y:35
	                        },
	                        tooltip: {
	                       	 trigger: 'axis',
	                       	 formatter: function(param){
	                       		 var res = param[0].name+"</br>";
	                       		 for(var i=0;i<param.length;i++){
	                       			 if(param[i].data!=0){
	                       			 	res+=param[i].seriesName+":"+fmoney(param[i].data,2)+"</br>";
	                       			 }
	                       		 }
	                       		 return res;
	                       	 },
	                       	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
	                                type : 'none'        // 默认为直线，可选为：'line' | 'shadow'
	                            }
	                        },
	                        /*dataZoom: {
	                       	 show: false,
	                       	 start: 0,
	                       	 end: 100,
	                       	 showDetail:false
	                        },*/
	                        xAxis : [
	                            {
	                                type : 'category',
	                                splitLine: {show:false},
	                                data : xAxisCategories,
	                                axisLabel:{
	                                	rotate:45
	                                }
	                            }
	                        ],
	                        yAxis : [
	                            {
	                                type : 'value'
	                            }
	                        ],
	                        series : yAxis
	                    };
	           	  
	           	 myChart.setOption(option); 
	             
	           	 myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
	           	        //console.log(param);
	           		  var parentId= accountList[param.dataIndex];
					  var name=xAxisCategories[param.dataIndex];
							$.ajax({
							    url:'/financeAccountManager/statsList'
							    ,type:'post'
							    ,dataType:'json'
							    ,data:{parentId: parentId,statsType:199,zidiyiChild:zidiyiChild}
							    ,async:false
							    ,success:function(param){
							    	var xAxisTitleList = ["预算","支出"];//"支出",,"超支"
							    	var xAxisList = [];
							    	var dataList = [];
							    	var accountList=[];
							    	if(param != null) {
							    		if(param.nameList!=null){xAxisList = param.nameList;}
							    		if(param.dataList!=null){dataList = param.dataList;}
							    		if(param.accountList!=null){accountList = param.accountList;}
							    	}
							    	if(param.accountList!=0){//判断是否有子级
							    		$(".ml40").append("<span> > </span><span name='"+parentId+"' class='spanTitle' style='cursor:pointer;' onclick='titeSpan(199,\""+parentId+"\",\""+name+"\");'>"+name+"</span>");
							    		myChart.clear();
							    		getBarChart(xAxisList,dataList, xAxisTitleList, '元', 'statis_charts_fs',accountList,param.childCount,name+"-预算支出概况");
							    	}else{
							    		 showInfoMessage("已经是最后一级科目！");
							    	}
							    	if(zidiyiChild == 1){
								    	dailyExpenses(1,parentId,'finance-daily-expenses',name+'-日支出');
								    	dailyExpenses(2,parentId,'finance-daily-accumulate',name+'-累计日支出');
							    	}
							    }
							}); 
	           	     //viewRoleDayStatistic(param.name);
	           	    });
	            }
	            );
	   },50);
	
}
//------------------------------------------------------Column Chart End---------------------------------------------------
//------------------------------------------------------bar Chart Begin---------------------------------------------------
function getbarchar(yu,zhi){
	 var xAxisDatas=['总费用'];//横坐标值（拍摄地点）
	    var seriesDataArray=new Array();
	    var series={};
	    series.name='预算';
	    series.color=["#3b6ba1"];
	    series.data=[yu];
	    seriesDataArray[0]=series;
	    series={};
	    series.name='支出';
	    series.color=["#71a450"];
	    series.data=[zhi];
	    seriesDataArray[1]=series;
	    
	    var maxcount=zhi;
		if(maxcount<yu){
			maxcount=yu;
		}
	   //图表
	   /*$('#container').highcharts({
		   chart: {
          type: 'bar'
      },
      title: {                                                           
	            text: ''                    
	        },  
      xAxis: {
          categories: xAxisDatas,
      },
      yAxis: {
          min: 0,
          max:maxcount,
          gridLineWidth:0,
          labels:{enabled:false} ,
          title: {
              text: ''
          },
      },
      credits: {
          enabled:false
		 },
      tooltip: {
				headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
				pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
					'<td style="padding:0"><b>{point.y}</b></td></tr>',
				footerFormat: '</table>',
				valueDecimals: 2,
				shared: true,
				useHTML: true 
			},
			plotOptions: {
				series: {
					 turboThreshold:0,
					 pointPadding: -0.2, //数据点之间的距离值
		             borderWidth: 0,
				}
				  ,bar: {                                                         
		                dataLabels: {                                              
		                    enabled: true,
		                	 formatter: function () {
		                         return '<b style="color:#ff6600">' + this.y + '</b>';
		                     },
		                  padding:-25
		                   
		                }                                                          
		            }     
			},
			  legend: {                                                          
		            layout: 'vertical',//'horizontal',                                            
		            align: 'right',
		            reversed:true,
		            verticalAlign: 'top',                                          
		            x: -0,                                                        
		            y: 0,                                                        
		           // floating: true,                                                
		            borderWidth: 1,                                                
		            backgroundColor: '#FFFFFF',                                    
		            shadow: true                                                   
		        },
			series: seriesDataArray
		}); */
}
//------------------------------------------------------bar Chart End---------------------------------------------------
