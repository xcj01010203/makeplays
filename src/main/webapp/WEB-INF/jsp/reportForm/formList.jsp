<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%-- <link href="<%=request.getContextPath()%>/js/report/base.css" rel="stylesheet" /> --%>
 	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/echarts2.0/echarts.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/radialIndicator/radialIndicator.min.js"></script>
  <style type="text/css">
  .jqx-widget-content-ui-lightness{background-color: #f2f4f6; }
  #table,#charTable{font-family: "微软雅黑";font-size: 16px;color: black;}
  #charTable{margin-top: 20px;}
  #charTable table{width:100%;border-collapse:collapse;}
  #charTable thead{background-color: #EBEBEB;}
  #charTable tr{min-height: 30px;}
  #charTable tbody tr:hover{background-color: #e8e8e8;}
  #charTable td{text-align: center;height: 35px;}
  #charTable tbody td{border: 1px solid #aaa;}
  #charTable tbody td div{margin: 0 auto;padding-top: 5px;}
  </style>  
<script type="text/javascript">
//配置echar路径
require.config({
    paths: {
        echarts: ctx+'/js/echarts'
    }
});
var CrewAmount_colors=['#0067a4','#00acd6','#009550','#da8c10','#771143','#dc5e4b'];
        	   $(document).ready(function () {
        		  /*  Highcharts.setOptions({  
        		       lang: {  
        		              resetZoom : '还原'  
        		       }  
        			}); */
        		   var clientWidth=window.screen.availWidth*0.78;
        		   var clientHeight=window.innerHeight - 190;
        		   $("#ddddddddd").css("height",clientHeight);
        		   $("#bydaystatistic").css("height",clientHeight);
        		   $("#byrolestatistic").css("height",clientHeight);
        		   $(".byShootAddress").css("width",clientWidth);
        		  // $(".byShootAddress").css("height",clientHeight);bydaystatistic
        		   $("#container").css("width",clientWidth);
        		   $(".clientWidth").css("width",clientWidth);
        		   
        		   topbarInnerText("进度表&&生产进度");
        		  	var tabValue = "${tabValue }";
        			if(tabValue==1){
        				$("#tab_2").addClass("tab_li_current");
        				$("#tab_2").siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_2").show();
        				//$(".f_r").hide();
        				$("#tabValue").val(1);
        			}else if(tabValue==2){
        				$("#tab_3").addClass("tab_li_current");
        				$("#tab_3").siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_3").show();
        				$("#tabValue").val(2);
        				//$(".f_r").show();
        			}else if(tabValue==3){
        				$("#tab_4").addClass("tab_li_current");
        				$("#tab_4").siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_4").show();
        				$("#tabValue").val(3);
        				//$(".f_r").show();
        			}else{
        				$("#tab_1").addClass("tab_li_current");
        				$("#tab_1").siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_1").show();
        				$("#tabValue").val(0);
        				//$(".f_r").show();
        			}
        			
        			$("#tab_1").click(function(){
        				$(this).addClass("tab_li_current");
        				$(this).siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_1").show();
        				$("#tabValue").val(0);
        				//$(".f_r").show();
        			});
        				
        			$("#tab_2").click(function(){
        				$(this).addClass("tab_li_current");
        				$(this).siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_2").show();
        				$("#tabValue").val(1);
        				//$("#typeline").val(1);
        				//$(".f_r").hide();
        			});
        			$("#tab_3").click(function(){
        				$(this).addClass("tab_li_current");
        				$(this).siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_3").show();
        				$("#tabValue").val(2);
        				$("#typemian").val(2);
        				//$(".f_r").show();
        				
        			});
        			$("#tab_4").click(function(){
        				$(this).addClass("tab_li_current");
        				$(this).siblings().removeClass("tab_li_current");
        				$(".danju").hide();
        				$(".danju_4").show();
        				$("#tabValue").val(3);
        				//$(".f_r").show();
        			});
                   // prepare the data
                   var data = new Array();
                   
                   var overView_titles  = [];//tite
                   <c:if test="${overView_titles !=null}" >
                       overView_titles  = ${overView_titles};
                   </c:if>
                   
                   var overView_total=[];//合计
                   <c:if test="${overView_total !=null}" >
                   		overView_total  = ${overView_total};
	               </c:if>
	               
                   var overView_finishedCrewAmount=[];//已完成
                   <c:if test="${overView_finishedCrewAmount !=null}" >
	                   overView_finishedCrewAmount  = ${overView_finishedCrewAmount};
		           </c:if>
                   
                   var overView_unFinishCrewAmount=[];//未完成
                   <c:if test="${overView_unFinishCrewAmount !=null}" >
                       overView_unFinishCrewAmount  = ${overView_unFinishCrewAmount};
		           </c:if>
                   
                   var overView_finishedPercent=[];//已完成比例
                   <c:if test="${overView_finishedPercent !=null}" >
                   overView_finishedPercent  = ${overView_finishedPercent};
		           </c:if>
                   
                   var overView_shootDays=[];//计划拍摄天数
                   <c:if test="${overView_shootDays !=null}" >
                   overView_shootDays  = ${overView_shootDays};
                   </c:if>
                   
                   var overView_shotedDays=[];//已拍摄天数
                   <c:if test="${overView_shotedDays !=null}" >
                   overView_shotedDays  = ${overView_shotedDays};
                   </c:if>
                  
                   var overView_dialyfinishedCrewAmount=[];//日均完成
                   <c:if test="${overView_dialyFinishedCrewAmount !=null}" >
                   overView_dialyfinishedCrewAmount  = ${overView_dialyFinishedCrewAmount};
                   </c:if>
                   var needDays=[];//预计至完成还需要的天数
                   <c:if test="${needDays !=null}" >
                   needDays  = ${needDays};
                   </c:if>
                   var earlyOrLateDays=[];//预计
                   <c:if test="${earlyOrLateDays !=null}" >
                   earlyOrLateDays  = ${earlyOrLateDays};
                   </c:if>
                   var zon_bing_ok = overView_finishedCrewAmount[0];
                  
                   var zon_bing_no = overView_unFinishCrewAmount[0];
                  
                   //bingChar(zon_bing_ok,zon_bing_no,"总进度","container");//初始化饼图
                   for (var i = 0; i < '${countSize}'; i++) {
                       var row = {};
                       row["overView_titles"] = overView_titles[i];
                       row["overView_total"] = overView_total[i];
                       row["overView_finishedCrewAmount"] = overView_finishedCrewAmount[i];
                       row["overView_unFinishCrewAmount"] = overView_unFinishCrewAmount[i];
                       row["overView_finishedPercent"] = overView_finishedPercent[i];
                       row["overView_shootDays"] = overView_shootDays[i];
                       row["overView_shotedDays"] = overView_shotedDays[i];
                       row["overView_dialyfinishedCrewAmount"] = overView_dialyfinishedCrewAmount[i];
                       row["needDays"] = needDays[i];
                       row["earlyOrLateDays"] = earlyOrLateDays[i];
                       data[i] = row;
                   }
                   /* var source =
                   {
                       localData: data,
                       dataType: "array",
                       dataFields:
                       [
                           { name: 'overView_titles', type: 'string' },
                           { name: 'overView_total', type: 'number' },
                           { name: 'overView_finishedCrewAmount', type: 'number' },
                           { name: 'overView_unFinishCrewAmount', type: 'number' },
                           { name: 'overView_finishedPercent', type: 'number' },
                           { name: 'overView_shootDays', type: 'number' },
                           { name: 'overView_shotedDays', type: 'number' },
                           { name: 'overView_dialyfinishedCrewAmount', type: 'number' },
                           { name: 'needDays', type: 'number' },
                           { name: 'earlyOrLateDays', type: 'number' }
                       ]
                   }; */
                   //var dataAdapter = new $.jqx.dataAdapter(source);
              
                   /* var cellsrenderer_overView_finishedPercent = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
                   	return "<div class='rowStatusColor' >"+value+"%</div>";
                   }  
                  	 以下是 columns: 属性;
                   cellsrenderer: cellsrenderer_overView_finishedPercent */

                   /* $('#table').on('rowClick', 
	                   function (event){
	                       // event args.
	                       var args = event.args;
	                       // row data.
	                       var row = args.row;
	                       var date=[{name: '已完成',value: row.overView_finishedCrewAmount},{name:'未完成',value:row.overView_unFinishCrewAmount}];
	                       var titles=row.overView_titles;
	                       if(titles.indexOf('总')<0 && titles.indexOf('组')<0){
	                    	   titles=titles+'景';
	                       }
	                       bingChar(date,'',titles,"container");
	                   }
                   ); */
                   
                   var legend_title='';
                   var top_statistics = "场";
       			   if('${statisticsType}'==1){
       	    		 legend_title='场数'
       			   }else if('${statisticsType}'==2){
       	    		 legend_title='页数';
       	    		 top_statistics = "页";
       			   }
       			   
       			   var topheader = [];
       			   topheader.push("<tr>");
       			   topheader.push("<td style='width:18%;'>标题</td>");
		    	   topheader.push("<td style='width:28%;'>已完成比例</td>");
		    	   topheader.push("<td style='width:18%;'>合计("+top_statistics+")</td>");
		    	   topheader.push("<td style='width:18%;'>已完成("+top_statistics+")</td>");
		    	   topheader.push("<td style='width:18%;'>未完成("+top_statistics+")</td>");
		    	   //topheader.push("<td style='width:10%;'>已完成比例</td>");
		    	   topheader.push("</tr>");
		    	   $("#charTable thead").html(topheader.join(''));
                   
                   if(data.length >0){
                	   var html = [];
                	   
                	   var topText = "";
        	    	   
        	    	   $.each(data,function(i,v){
        	    		   if(i == 0){
        	    			   //topText += "计划拍摄天数";
        	    			   topText += "&nbsp;&nbsp;&nbsp;&nbsp;计划拍摄"+v.overView_shootDays+"天，";
        	    			   topText += "已拍摄"+v.overView_shotedDays+"天，";
        	    			   topText += "日均完成"+v.overView_dialyfinishedCrewAmount+top_statistics+"，";
        	    			   //topText += "预计至完成还需要"+v.needDays+"天，";
        	    			   if(v.earlyOrLateDays>=0)
        	    			       topText += "预计要提前"+v.earlyOrLateDays+"天。" ;
        	    			   else
        	    				   topText += "预计要逾期"+Math.abs(v.earlyOrLateDays)+"天。" ;
        	    		   }
        	    		   var fg = "<tr>";
        	    		   fg += "<td>"+v.overView_titles+"</td>";
        	    		   fg += "<td><div id='"+(Math.random()+"").substring(2)+"' model-percent="+v.overView_finishedPercent+"></div></td>";
        	    		   fg += "<td>"+v.overView_total+"</td>";
        	    		   fg += "<td>"+v.overView_finishedCrewAmount+"</td>";
        	    		   fg += "<td>"+v.overView_unFinishCrewAmount+"</td>";
        	    		   //fg += "<td>"+v.overView_finishedPercent+"%</td>";
        	    		   fg += "</tr>";
        	    		   html.push(fg);
        	    	   });
        	    	   $("#table").html(topText);
        			   $("#charTable tbody").html(html);
        			   
        			   var time = 0;
        			   $("#charTable tbody tr").each(function(i,_tr0){
        				   var _tr = $(_tr0);
        				   //var titles = _tr.find("td:eq(0)").text();
        				   //var finish = _tr.find("td:eq(2)").text();
        				   //var unfinish = _tr.find("td:eq(3)").text();
        				   var percent = _tr.find("td:eq(1) div").attr("model-percent");
        				   var _id = _tr.find("td:eq(1) div").attr("id");
        				   $("#"+_id).hide().css({height:'105px',width:'140px'});
        				   //var datas=[{name: '已完成',value: finish},{name:'未完成',value:unfinish}];
        				   setTimeout(function(){
        					   radialIndicator(_id,percent.replace(/%/g,""));
        				   },time);
        				   time = time + 200;
        			   });
                   }
                   
                   /* $("#table").jqxDataTable(
                   {
                       width: '100%',
                       //pageable: true,
                      // pagerButtonsCount: 10,
                       source: dataAdapter,
                       columnsResize: true, 
                       theme:theme,
                       columns: [
                         { text: '标题', dataField: 'overView_titles', width: '10%',align: 'center',cellsAlign: 'center' },
                         { text: '合计', dataField: 'overView_total', width: '10%',align: 'center',cellsAlign: 'center'},
                         { text: '已完成', dataField: 'overView_finishedCrewAmount', width: '10%' ,cellsAlign: 'center',align: 'center'},
                         { text: '未完成', editable: false, dataField: 'overView_unFinishCrewAmount', width: '10%',cellsAlign: 'center',align: 'center' },
                         { text: '已完成比例',cellsformat: 'p', dataField: 'overView_finishedPercent', width: '10%', cellsAlign: 'center', align: 'center', width: '10%' },
                         { text: '计划拍摄天数', dataField: 'overView_shootDays', width: '10%', cellsAlign: 'center', align: 'center', width: '10%' },
                         { text: '已拍摄天数', dataField: 'overView_shotedDays', cellsAlign: 'center', align: 'center' , width: '10%' },
                         { text: '日均完成', dataField: 'overView_dialyfinishedCrewAmount', cellsAlign: 'center', align: 'center', width: '10%' },
                         { text: '预计至完成还需要天数', dataField: 'needDays', cellsAlign: 'center', align: 'center' , width: '10%' },
                         { text: '预计   逾期/提前', dataField: 'earlyOrLateDays', cellsAlign: 'center', align: 'center', width:'10%' } 
                       ]
                     
                   }); */
		///////////////--报表统计---/////////////////////
			//按页场查询
			$(".f_r").find(":radio").click(function(){
				$(".f_r").find(".form_1").submit();
			});
			
			//角色按主要，特约演员查询
			$("#form_viewRoleType").find(":radio").click(function(){
				$("#form_viewRoleType").submit();
			});
			//日拍摄，日累计
			$("#form_byDay").find(":radio").click(function(){
				$("#form_byDay").submit();
			});
			
			//*********************图表**********************/ '#3b6ba1','#71a450',
			
			
    		/**按天统计*/
		 	    xAxisDatas=[];//横坐标值（通告单日期）
		 	    var nameDate = [];
		 	  // var day_colors=['#9dc7f1','#727276','#acf19d','#f9ba85'];
		 	   <c:forEach items="${noticeDates}" var='noticeDate' varStatus="notone">
		 	 		 xAxisDatas['${notone.index}']='${noticeDate}';
		 	   </c:forEach>
				var seriesDataArray=new Array();
				//var maxCrewAmount=0;
		    	   <c:forEach items="${byDay}" var='groupName_CrewAmounts' varStatus="byone">
		    	   		var series={};
		    	   		var marker={};
		    	   		var itemStyle={};
		    	   		var normal={};
			 	    	series.name='${groupName_CrewAmounts.title}';
			 	    	nameDate.push('${groupName_CrewAmounts.title}');
			 	    	series.type='bar';
			 	    	if('${groupName_CrewAmounts.title}' == '单组'){
			 	    		normal.color="#33b7b7";
			 	    		itemStyle.normal = normal;
			 	    		series.itemStyle = itemStyle;
			 	    	}else{
			 	    		normal.color=CrewAmount_colors['${byone.index}'];
			 	    		itemStyle.normal = normal;
			 	    		series.itemStyle = itemStyle;
			 	    	}
			 	    	
			 	    		var crewAmounts=[];
			 	    		<c:forEach items="${groupName_CrewAmounts.crewAmountList}" var='CrewAmount' varStatus="Crewone">
			 	    			var crewAmount_temp=0;
			 	    			crewAmount_temp=${CrewAmount};
			 	    			crewAmounts['${Crewone.index}']=crewAmount_temp;
			 	    			/* if(crewAmount_temp>maxCrewAmount){
			 	    				maxCrewAmount=crewAmount_temp;
			 	    			}	 */		 	    			
			 	    		</c:forEach>
			 	    		series.data=crewAmounts;
			 	    		series.stack = "全部";
			 	    		marker.radius=3;
			 	    		//marker.lineWidth=0.1;
			 	    		series.marker=marker;
			 	    	seriesDataArray['${byone.index}']=series;
			 	    </c:forEach>
			   
		 	   var totalWidth=(45+15)*xAxisDatas.length;//所有柱子及柱子的间隔(15px)的总宽度
			   var byRoleWidth=$('.byDay').width();//容器的宽度
			   var scrollbar = false;
			   var xmin = 100;
			   
			   if(totalWidth>byRoleWidth){
				  byRoleWidth=byRoleWidth*(totalWidth/byRoleWidth);
				 if(xAxisDatas.length>30){
					 //$('.byDay').css('width',byRoleWidth);
					 scrollbar = true;
					 xmin = parseInt(30/xAxisDatas.length*100);
				 }
			   }
			   if(seriesDataArray.length == 0){
				   var series={};
				   series.name = "单组";
				   seriesDataArray[0]=series;
			   }
			   dayShootingChar(xAxisDatas,seriesDataArray,xmin,scrollbar,nameDate);
		 	   /* if("${typeline}"==2){
		 		 $("#dayTypelines").prop("checked",true);
		 		dayCumulativeChar(xAxisDatas,seriesDataArray);
		 	   }else{
		 		 $("#dayTypeline").prop("checked",true);
		 		 dayShootingChar(xAxisDatas,seriesDataArray,xmin,scrollbar);
		 	   } */
		 	   
		 	   var dayTotalData = [];
		 	   var itemStyle = {normal: {areaStyle: {type: 'default'}}};
		 	  <c:forEach items="${dayTotalList}" var='groupName_CrewAmounts' varStatus="byone">
	   	   		var series={};
	   	   		var marker={};
		 	    	series.name='${groupName_CrewAmounts.title}';
		 	    	series.color=CrewAmount_colors['${byone.index}'];
		 	    		var crewAmounts=[];
		 	    		<c:forEach items="${groupName_CrewAmounts.crewAmountList}" var='CrewAmount' varStatus="Crewone">
		 	    			var crewAmount_temp=0;
		 	    			crewAmount_temp=${CrewAmount};
		 	    			crewAmounts['${Crewone.index}']=crewAmount_temp;
		 	    			/* if(crewAmount_temp>maxCrewAmount){
		 	    				maxCrewAmount=crewAmount_temp;
		 	    			}	 */		 	    			
		 	    		</c:forEach>
		 	    		series.data=crewAmounts;
		 	    		series.type="line";
		 	    		series.smooth = true;
		 	    		series.itemStyle = itemStyle;
		 	    		marker.radius=3;
		 	    		//marker.lineWidth=0.1;
		 	    		series.marker=marker;
		 	    		dayTotalData['${byone.index}']=series;
		 	    </c:forEach>
		 	   dayCumulativeChar(xAxisDatas,dayTotalData);
			 	
			   
			   
			   
			   
			   var columnWidth=25;//每个柱子自身的宽度
			   /**按角色统计*/
		 	    xAxisDatas=[];//横坐标值（角色）
		 	   nameData = [];
				totalCrewAmounts=[];//各角色的总戏量
				finishcrewAmounts=[];//各角色的已完成戏量
				//maxCrewAmount=0;
		    	   <c:forEach items="${byRole}" var='shootSchedule' varStatus="shindex">
		    	   		xAxisDatas['${shindex.index}']='${shootSchedule.title}';
		    	   		var totalCrewAmount=0;
		    	   		if("${shootSchedule.totalCrewAmount}"!=""&&"${shootSchedule.totalCrewAmount}"!=null){
		    	   			totalCrewAmount=${shootSchedule.totalCrewAmount};
		    	   		}		    	   		
		    	   		/* if(totalCrewAmount>maxCrewAmount){
		    	   			maxCrewAmount=totalCrewAmount;
		    	   		} */
		    	   		totalCrewAmounts['${shindex.index}']=totalCrewAmount;
		    	   		finishcrewAmounts['${shindex.index}']=0;
		    	   		if('${shootSchedule.finishedCrewAmount}'!=""&&"${shootSchedule.finishedCrewAmount}"!=null){
		    	   			finishcrewAmounts['${shindex.index}']=${shootSchedule.finishedCrewAmount};
		    	   		}
		    	   </c:forEach>
		 	    var seriesDataArray=new Array();
		 	    var series={};
		 	    series.name='总'+legend_title;
		 	    nameData.push('总'+legend_title);
		 	    series.color=CrewAmount_colors[2];
		 	    series.type="bar";
		 	    series.barGap='-50%';
		 	    if(totalCrewAmounts.length==0){
		 	    	series.data=[0];
		 	    }else{
		 	    	series.data=totalCrewAmounts;
		 	    }		 	    
		 	    seriesDataArray[0]=series;
		 	    series={};
		 	    series.name='已完成'+legend_title;
		 	    nameData.push('已完成'+legend_title);
		 	    series.color=CrewAmount_colors[0];
		 	    series.type="bar";
		 	    series.barGap=0;
		 	    if(finishcrewAmounts.length==0){
		 	    	series.data=[0];
		 	    }else{
		 	    	series.data=finishcrewAmounts;
		 	    }	
		 	    seriesDataArray[1]=series;
		 	   //图表
		 	   var totalWidth=(columnWidth+15)*xAxisDatas.length;//所有柱子及柱子的间隔(15px)的总宽度
		 	   var byRoleWidth=$('.byRole').width();//容器的宽度
		 	   var rolemin = 100;
		 	   var roleScrollbar = false;
		 	   
		 	   if(xAxisDatas.length>30){
		 			 //$('.byRole').css('width',$("#form_viewRoleType").width());
		 			rolemin = parseInt(30/xAxisDatas.length*100);
		 			roleScrollbar = true;
		 		 }
		 	  byrolestatistic(nameData,roleScrollbar,rolemin,seriesDataArray,xAxisDatas);
		 	  
			    
			   
			   /**按拍摄地*/
		 	    var xAxisDatas=[];//横坐标值（拍摄地点）
				var totalCrewAmounts=[];//各拍摄地点的总戏量
				var finishcrewAmounts=[];//各拍摄地点的已完成戏量
				var maxCrewAmount=0;
				var addrNameDate = [];
		    	   <c:forEach items="${byShootAddress}" var='shootSchedule' varStatus="byindex">
		    	   		xAxisDatas['${byindex.index}']='${shootSchedule.title}';
		    	   		var totalCrewAmount=0;
		    	   		if('${shootSchedule.totalCrewAmount}'!="" && '${shootSchedule.totalCrewAmount}'!=null){
		    	   			totalCrewAmount=${shootSchedule.totalCrewAmount};
		    	   		}
		    	   		if(totalCrewAmount>maxCrewAmount){
		    	   			maxCrewAmount=totalCrewAmount;
		    	   		}
		    	   		
		    	   		totalCrewAmounts['${byindex.index}']=totalCrewAmount;
		    	   		finishcrewAmounts['${byindex.index}']=0;
		    	   		if('${shootSchedule.finishedCrewAmount}'!="" && '${shootSchedule.finishedCrewAmount}'!=null){
		    	   			finishcrewAmounts['${byindex.index}']=${shootSchedule.finishedCrewAmount}
		    	   		}
		    	   		</c:forEach>
		 	    var seriesDataArray=new Array();
		 	    var series={};
		 	    series.name='总'+legend_title;
		 	    addrNameDate.push('总'+legend_title);
		 	    series.color=CrewAmount_colors[2];
		 	    series.data=totalCrewAmounts;
		 	    series.type="bar";
		 	    series.barGap='-50%';
		 	    seriesDataArray[0]=series;
		 	    series={};
		 	    series.name='已完成'+legend_title;
		 	    series.type="bar";
		 	    series.barGap=0;
		 	    addrNameDate.push('已完成'+legend_title);
		 	    series.color=CrewAmount_colors[0];
		 	    series.data=finishcrewAmounts;
		 	    seriesDataArray[1]=series;
		    	   	//	debugger
		 	   //图表
		 	   totalWidth=(25+15)*xAxisDatas.length;//所有柱子及柱子的间隔(15px)的总宽度
		 	   var byShootAddressHeight=100;//$('.byShootAddress').height();//容器的高度
		 	   //console.log(byShootAddressHeight)
		 	   var addressMin = 100;
		 	   var addressScro = false;
		 	   if(xAxisDatas.length>12){
		 		  //byShootAddressHeight=byShootAddressHeight*(totalWidth/byShootAddressHeight);
		 		 //$('.byShootAddress').css('height',400);
		 		  addressMin = parseInt(12/xAxisDatas.length*100);
		 		 addressScro = true;
		 	   }
		 	  require(
		 				[
		 	             'echarts',
		 	             'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
		 	         ],
		 	            function(ec){
		 					
		 	           	 var myChart = ec.init(document.getElementById('byShootAddress')); 
		 	           	 
		 	           	 var option = {
		 	                        tooltip: {
		 	                            show: true
		 	                        },
		 	                        legend: {
		 	                            data: addrNameDate,
		 	                            //y:'bottom' 
		 	                            x: 'right',
		 	                            y: 15,
		 	                            
		 	                        },
		 	                        title: {
		 	                       	 text: '',
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
			                        	 x:50,
			                        	 y2:65,
			                        	 y:35
		 	                        },
		 	                        tooltip: {
		 	                       	 trigger: 'axis',
		 	                       	 formatter: function(param){
		 	                       		 var res = param[0].name+"</br>";
		 	                       		 for(var i=0;i<param.length;i++){
		 	                       			 if(param[i].data!=0){
		 	                       			 	res+=param[i].seriesName+":"+param[i].data+"</br>";
		 	                       			 }
		 	                       		 }
		 	                       		 return res;
		 	                       	 },
		 	                       	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		 	                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		 	                            }
		 	                        },
		 	                       color: ["#3b6ba1","#71a450"],
		 	                       backgroundColor:"#f8f8f8",
		 	                        dataZoom: {
		 	                       	 show: addressScro,
		 	                       	 start: 0,
		 	                       	 end: addressMin,
		 	                       	 showDetail:false,
		 	                       	 //backgroundColor: 'rgba(197,197,197,0.1)',
		 	                       	 //orient: 'vertical',
		 	                       	 //height: 485,
		 	                       	 //x: clientWidth-45
		 	                        },
		 	                        xAxis : [
										
										{
		 	                                type : 'category',
		 	                                splitLine: {show:false},
		 	                                data : xAxisDatas,
		 	                                /* axisLabel:{
		 	                                	formatter:function(value){
				 	                            	   if(value.length>7){
				 	                            		   return substr(value,7);
				 	                            	   }
				 	                            	   return value;
				 	                             }
		 	                                }, */
		 	                               
		 	                            }
		 	                        ],
		 	                        yAxis : [
										{
										    type : 'value'
										}
		 	                        ],
		 	                        series : seriesDataArray
		 	                    };
		 	           	 
		 	           	 myChart.setOption(option); 
		 				}
		 	         );
			   /* $('.byShootAddress').highcharts({
				   chart: {
		                type: 'bar'
		            },
		            title: {
						text: ''
					},
		            xAxis: {
		                categories: xAxisDatas,
		                min:addressMin
		            },
		            yAxis: {
		                min: 0,
		                max:maxCrewAmount,
		                title: {
		                    text: ''
		                }
		            },
		            scrollbar: {
			             enabled: addressScro
			        },
		            credits: {
		                enabled:false
		     		 },
		            tooltip: {
						headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
						pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
							'<td style="padding:0"><b>{point.y} </b></td></tr>',
						footerFormat: '</table>',
						shared: true,
						useHTML: true
					},
					plotOptions: {
						column: {
							pointPadding: 0.2,
							pointWidth :columnWidth,
							borderWidth: 0
						},
						series: {
							 turboThreshold:0,
							 pointPadding: -0.2, //数据点之间的距离值
				             borderWidth: 0,
		                }
						 ,bar: {                                                         
				                dataLabels: {                                              
				                    enabled: true,
				                }                                                          
				            }    
					},legend: {                                                          
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
				    
        })
        //饼图 lma///////////////////////////
function bingChar(date,date_load_no,titles,id) {
	//var chart;
    $(document).ready(function () {
    	if(date_load_no!='' && date_load_no!=undefined){
    		date=[{name: '已完成',value:date},{name:'未完成',value:date_load_no}];
    	}
    	$("#"+id).show();
    	require(
	             [
	                 'echarts',
	                 'echarts/chart/pie' // 使用柱状图就加载pie模块，按需加载
	             ],
	             function(ec){
	            	 //var ecConfig = require('echarts/config'); //点击事件
	             
	            	 var myChart = ec.init(document.getElementById(id)); 
	            	 
	            	 option = {
	            			    title : {
	            			    	show:false,
	            			        text: titles,
	            			        //subtext: '纯属虚构',
	            			        
	            			    },
	            			    
	            			    tooltip : {
	            			        trigger: 'item',
	            			        formatter: "{a} <br/>{b} : {c} ({d}%)"
	            			    },
	            			    
	            			    legend: {
	            			    	y:'30%',
	            			    	x:'right',
	            			        data:['已完成','未完成']
	            			    },
	            			    color: [ '#90EE7E', '#7CB5EC', '#da70d6', '#32cd32', '#6495ed', 
	            			             '#ff7f50', '#87cefa', '#da70d6', '#32cd32', '#6495ed', ],
	            			    series : [
	            			        {
	            			            name:titles,
	            			            type:'pie',
	            			            radius : '55%',
	            			            center: ['50%', '50%'],
	            			            data:date
	            			        }
	            			    ]
	            			};
	            	 
	            	 myChart.setOption(option); 
	            	 /* myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
	            	        console.log(param)
	            	    }) */
	             }
	             );
    	
    });
    
};

//日累计
function dayCumulativeChar(xAxisDatas,seriesDataArray,step){
	var step = xAxisDatas.length > 30 ? parseInt(xAxisDatas.length/30) : 0;
	require(
            [
                'echarts',
                'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
            ],
            function(ec){
           	 var myChart = ec.init(document.getElementById('byDayTotal')); 
           	 
           	 var option = {
                        tooltip: {
                            show: true
                        },
                        legend: {
                            data:["合计"],
                            //y:'bottom' 
                            x: 'right',
                            y: 15,
                            
                        },
                        title: {
                       	 text: '拍摄进度累计',
                       	 x: 'center',
                       	 itemGap: 0,
                       	 padding: 0,
                       	 textStyle: {
                       		 fontSize: 16,
                       		 fontWeight: 'normal',
                       		 color: '#333'
                       	 }
                        },
                        color: ["#71a450","#3b6ba1"],
                        backgroundColor:"#f8f8f8",
                        grid:{
                       	 x2:20,
                       	 x:50,
                       	 y:35
                        },
                        tooltip: {
                       	 trigger: 'axis',
                       	 formatter: function(param){
                       		 var res = param[0].name+"</br>";
                       		 for(var i=0;i<param.length;i++){
                       			 if(param[i].data!=0){
                       			 	res+=param[i].seriesName+":"+param[i].data+"</br>";
                       			 }
                       		 }
                       		 return res;
                       	 },
                       	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'none'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        xAxis : [
                            {
                                type : 'category',
                                splitLine: {show:false},
                                data : xAxisDatas,
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
                        series : seriesDataArray,
                        
                    };
           	 
           	 myChart.setOption(option); 
           	 /* myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
           	        console.log(param)
           	    }) */
            }
            );
	
}

//日拍摄量
function dayShootingChar(xAxisDatas,seriesDataArray,xmin,scrollbar,nameData){
	//图表spline,line,areaspline
	//console.log(xmin)
				require(
			             [
			                 'echarts',
			                 'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
			             ],
			             function(ec){
			            	 var myChart = ec.init(document.getElementById('byDay')); 
			            	 
			            	 var option = {
			                         tooltip: {
			                             show: true
			                         },
			                         legend: {
			                             data:nameData,
			                             //y:'bottom' 
			                             x: 'right',
			                             y: 15,
			                             
			                         },
			                         title: {
			                        	 text: '日拍摄量',
			                        	 x: 'center',
			                        	 itemGap: 0,
			                        	 padding: 0,
			                        	 textStyle: {
			                        		 fontSize: 16,
			                        		 fontWeight: 'normal',
			                        		 color: '#333'
			                        	 }
			                         },
			                         //color: CrewAmount_colors,
			                         backgroundColor:"#f8f8f8",
			                         grid:{
			                        	 x2:20,
			                        	 x:50,
			                        	 y2:105,
			                        	 y:35
			                         },
			                         tooltip: {
			                        	 trigger: 'axis',
			                        	 formatter: function(param){
			                        		 var res = param[0].name+"</br>";
			                        		 for(var i=0;i<param.length;i++){
			                        			 if(param[i].data!=0){
			                        			 	res+=param[i].seriesName+":"+param[i].data+"</br>";
			                        			 }
			                        		 }
			                        		 return res;
			                        	 },
			                        	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			                                 type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			                             }
			                         },
			                         dataZoom: {
			                        	 show: scrollbar,
			                        	 start: 0,
			                        	 end: xmin,
			                        	 showDetail:false
			                         },
			                         xAxis : [
			                             {
			                                 type : 'category',
			                                 splitLine: {show:false},
			                                 data : xAxisDatas,
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
			                         series : seriesDataArray
			                     };
			            	 
			            	 myChart.setOption(option); 
			            	 
			             }
			             );
	   
}

function viewRoleDayStatistic(name){  //演员角色按天统计
	var type = ${statisticsType};
	var param = {
		viewRoleName:name,
		type:type
	};
	$.ajax({
		type:'post',
		dataType:'json',
		data:param,
		url:ctx+"/lmaroleCrewReportManager/getViewRoleDayStatistic",
		success:function(data){
			diversityStatistics(name,type);
			var rolemin = 100;
			var roleScrollbar = false;
			if(data.xaxis.length>30){
				rolemin = parseInt(30/data.xaxis.length*100);
				roleScrollbar = true;
			}
			var yxias = [];
			var ydata = {};
			var roleNameDate = [];
			var title = "";
			if(type == 1){
				ydata.name = "场";
				title = "场次";
			}
			else if(type == 2){
				ydata.name = "页";
				title = "页数";
			}
			roleNameDate.push(ydata.name);
			ydata.type="bar";
			ydata.data = data.yaxis;
			yxias.push(ydata);
			$("#roleByDay").show();
			require(
		             [
		                 'echarts',
		                 'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
		             ],
		             function(ec){
		            	 var myChart = ec.init(document.getElementById('roleByDay')); 
		            	 
		            	 var option = {
		                         tooltip: {
		                             show: true
		                         },
		                         legend: {
		                             data:roleNameDate,
		                             //y:'bottom' 
		                             x: 'right',
		                             y: 15,
		                             
		                         },
		                         title: {
		                        	 text: name+'-日拍摄量',
		                        	 x: 'center',
		                        	 itemGap: 0,
		                        	 padding: 0,
		                        	 textStyle: {
		                        		 fontSize: 16,
		                        		 fontWeight: 'normal',
		                        		 color: '#333'
		                        	 }
		                         },
		                         color: ["#71a450"],
		                         backgroundColor:"#f8f8f8",
		                         grid:{
		                        	 x2:20,
		                        	 x:30,
		                        	 y2:105,
		                        	 y:35
		                         },
		                         tooltip: {
		                        	 trigger: 'axis',
		                        	 formatter: function(param){
		                        		 var res = param[0].name+"</br>";
		                        		 for(var i=0;i<param.length;i++){
		                        			 if(param[i].data!=0){
		                        			 	res+=param[i].seriesName+":"+param[i].data+"</br>";
		                        			 }
		                        		 }
		                        		 return res;
		                        	 },
		                        	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		                                 type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		                             }
		                         },
		                         dataZoom: {
		                        	 show: roleScrollbar,
		                        	 start: 0,
		                        	 end: rolemin,
		                        	 showDetail:false
		                         },
		                         xAxis : [
		                             {
		                                 type : 'category',
		                                 splitLine: {show:false},
		                                 data : data.xaxis,
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
		                         series : yxias
		                     };
		            	 
		            	 myChart.setOption(option); 
		            	 
		             }
		             );
			
			
		}
	});
}

function diversityStatistics(viewRoleName,statisType){
	$.ajax({
        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getByContSet",  
        type: 'POST',
        data: {
        	roleName:viewRoleName,
        	statisType:statisType
         },
        async: false,
        dataType: 'JSON',
        success:function(data){
			if(data!="" && data!=null)
			{
				
			  	   var seriesDataArray = [];
			  	 seriesDataArray[0]=data.series;
			  	var columnWidth=15;//每个柱子自身的宽度
			  	
		 	   	var xmin = 100;
			  	var scrollbar = false;
			  	if(data.categories.length>30){
			  		xmin = parseInt(30/data.categories.length*100);
			  		scrollbar = true;
			  	}
			  	
			  	var tips = "场";
			  	if(statisType == 2){
			  		tips = "页";
			  	}
			  	$("#roleBySet").show();
			  	require(
			             [
			                 'echarts',
			                 'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
			             ],
			             function(ec){
			            	 //var ecConfig = require('echarts/config'); //点击事件
			             
			            	 var myChart = ec.init(document.getElementById('roleBySet')); 
			            	 
			            	 var option = {
			                         tooltip: {
			                             show: true
			                         },
			                         legend: {
			                             data:[tips],
			                             //y:'bottom' 
			                             x: 'right',
			                             y: 15,
			                         },
			                         title: {
			                        	 text: viewRoleName+"-戏量按集分布",
			                        	 x: 'center',
			                        	 itemGap: 0,
			                        	 padding: 0,
			                        	 textStyle: {
			                        		 fontSize: 16,
			                        		 fontWeight: 'normal',
			                        		 color: '#333'
			                        	 }
			                         },
			                         color: ["#3b6ba1"],
			                         backgroundColor:"#f8f8f8",
			                         tooltip: {
			                        	 formatter: function(params,ticket,callback){
			                        		 //console.log(params)
			                        		 var res = params.name+"集</br>"+params.value+params.seriesName;
			                        		 /* for(var i = 0, l = params.length; i < l; i++) {
			                        			 
			                        		 } */
			                        		 //callback(ticket, res);
			                        		 return res;
			                        	 },
			                         },
			                         grid:{
			                        	 x2:20,
			                        	 x:30,
			                        	 y2:65,
			                        	 y:35
			                         },
			                         dataZoom: {
			                        	 show: scrollbar,
			                        	 start: 0,
			                        	 end: xmin,
			                         },
			                         xAxis : [
			                             {
			                                 type : 'category',
			                                 splitLine: {show:false},
			                                 data : data.categories
			                             }
			                         ],
			                         yAxis : [
			                             {
			                                 type : 'value'
			                             }
			                         ],
			                         series : seriesDataArray
			                     };
			            	 
			            	 myChart.setOption(option); 
			            	 /* myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
			            	        console.log(param)
			            	    }) */
			             }
			             );
			  	   
			  	  
			}
        }
  	});
}

function byrolestatistic(nameData,roleScrollbar,rolemin,seriesDataArray,xAxisDatas){
	require(
			[
             'echarts',
             'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
         ],
            function(ec){
			 var ecConfig = require('echarts/config'); //点击事件	
				
           	 var myChart = ec.init(document.getElementById('byRole')); 
           	 
           	 var option = {
                        tooltip: {
                            show: true
                        },
                        legend: {
                            data:nameData,
                            //y:'bottom' 
                            x: 'right',
                            y: 15,
                            
                        },
                        title: {
                       	 text: '',
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
                       	 x:40,
                       	 y2:105,
                       	 y:35
                        },
                        color: ["#3b6ba1","#71a450"],
                        backgroundColor:"#f8f8f8",
                        tooltip: {
                       	 trigger: 'axis',
                       	 formatter: function(param){
                       		 var res = param[0].name+"</br>";
                       		 for(var i=0;i<param.length;i++){
                       			 if(param[i].data!=0){
                       			 	res+=param[i].seriesName+":"+param[i].data+"</br>";
                       			 }
                       		 }
                       		 return res;
                       	 },
                       	 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        dataZoom: {
                       	 show: roleScrollbar,
                       	 start: 0,
                       	 end: rolemin,
                       	 showDetail:false
                        },
                        xAxis : [
                            {
                                type : 'category',
                                splitLine: {show:false},
                                data : xAxisDatas,
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
                        series : seriesDataArray
                    };
           	 
           	 myChart.setOption(option); 
           	 myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
           	        //console.log(param);
           	     viewRoleDayStatistic(param.name);
           	    });
            }
            );
}

//环形图
function radialIndicator(id,percent){
	$("#"+id).show();
	$('#'+id).radialIndicator({
        barColor: '#3c8dbc',
        radius: 40,
        barWidth: 10,
        initValue: 0,
        roundCorner : true,
        percentage: true
    });
	var radialObj = $('#'+id).data('radialIndicator');
	//now you can use instance to call different method on the radial progress.
	//like
	radialObj.animate(percent);
}

/**
 * 截取字符串换行
 */
 function substr(str,len){
	var a = Math.ceil(str.length/len);
	var b = "";
	for(var i=0;i<a;i++){
		if((i+1)*len<=str.length)
			b = b + str.substring(i*len,(i+1)*len);
		else
		    b = b + str.substring(i*len,str.length);
		if(i!=(a-1))
			b = b + "\n";
	}
	return b;
}

</script>
</head>
<body ><!-- id="statis_page" class="no_scroll" -->
	<div class="bd_wrap">
	<div class="btn_tab_wrap">
    	<div class="btn_wrap">  
    		<div  class="f_r" style="float: left;margin-top: 6px">   
    		<form class="form_1"  method="post" action="<%=request.getContextPath()%>/shootReportManager/formList">
	                <label class="mr8">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                	<input type="radio" style="cursor:pointer;" name="statisticsType"  <c:if test="${statisticsType eq 2}">checked</c:if> value="2">
	                    <span style="color: #1c94c4;cursor:pointer;" >页</span>
	                     <input type="hidden" name="tabValue" id="tabValue" value="${tabValue }">
	                </label>
	                <label>
	                		<input type="radio" style="cursor:pointer;" name="statisticsType" <c:if test="${statisticsType eq 1}">checked</c:if> value="1"/>
	                    <span style="color: #1c94c4;cursor:pointer;" >场</span>
	                </label>
                </form></div>
    	</div>
        <div class="tab_wrap">
        
        	<ul>
            	<li id="tab_1" style="width: 24%" class="tab_li_current">总体进度</li>
                <li id="tab_2" style="width: 24%">日进度</li>
                <li id="tab_3"style="width: 24%">戏份</li>
                <li id="tab_4" style="width: 24%">拍摄地</li>
            </ul>
        </div>
    </div>
    
    <div class="danju danju_1">
    	<br/>
    	<div style="overflow-y: auto;overflow-x: hidden;" id="ddddddddd">
	        <div id="table">
	   		</div>
	   			
	   		<!-- <div id="container" align="center" style="width:400px;height: 400px;"></div> -->
	   		
	   		<div id="charTable" align="center" style="width:100%;">
	   			<table border="0" cellpadding="0" cellspacing="0">
	   			    <thead>
	   			    	
	   			    </thead>
	   			    <tbody></tbody>
	   			</table>
	   		</div>
   		</div>
    </div>
    
     <div class="danju danju_2">
     	<%-- <div style="margin-top: 14px;">  
        	  <form id='form_byDay' method="post" action="<%=request.getContextPath()%>/shootReportManager/formList">
        		 <label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        			 <input type="hidden"  name="statisticsType"   value="${statisticsType}">
	                	<input type="radio" style="cursor:pointer;" name="typeline" id='dayTypeline'  <c:if test="${typeline eq 1}">checked</c:if> value="1">
	                    <span style="color: #1c94c4;cursor:pointer;">日拍摄量</span>
	                     <input type="hidden" name="tabValue" id='typeline' value="${tabValue }">
	                </label>
	                <label>&nbsp;&nbsp;
	                		<input type="radio" style="cursor:pointer;" name="typeline" id='dayTypelines' <c:if test="${typeline eq 2}">checked</c:if> value="2"/>
	                    <span style="color: #1c94c4;cursor:pointer;">日累计</span>
	                </label>
	            </form>    
        </div> --%><br/>
        <div style="overflow:auto;" id="bydaystatistic">
        	<div style="width:95%;height: 400px; " class="byDay clientWidth" id="byDay" ></div>
        	<div style="width:95%;margin-top: 20px; height: 400px;" id="byDayTotal" class="clientWidth" ></div>
        </div>    
    </div>
     <div class="danju danju_3">
       <div style="margin-top: 14px;">  
        	  <form id='form_viewRoleType' method="post" action="<%=request.getContextPath()%>/shootReportManager/formList">
        		 <label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        			 <input type="hidden"  name="statisticsType"   value="${statisticsType}">
	                	<input type="radio" style="cursor:pointer;" name="viewRoleType" <c:if test="${viewRoleType eq 1}">checked</c:if> value="1">
	                    <span style="color: #1c94c4;cursor:pointer;">主要演员</span>
	                     <input type="hidden" name="tabValue" id='typemian' value="${tabValue }">
	                </label>
	                <label>&nbsp;&nbsp;
	                		<input type="radio" style="cursor:pointer;" name="viewRoleType" <c:if test="${viewRoleType eq 2}">checked</c:if> value="2"/>
	                    <span style="color: #1c94c4;cursor:pointer;">特约演员</span>
	                </label>
	            </form>    
        </div><br/>
       	<div id="byrolestatistic" style="overflow:auto;" >
       		<div id="byRole" class="byRole clientWidth" style="width:95%;height: 400px;padding-top: 10px;padding-bottom: 10px;" ></div>
       		<div id="roleByDay" class="clientWidth" style="width:95%;margin-top: 20px;height: 400px;display: none;padding-top: 10px;padding-bottom: 10px;" ></div>
       		<div id="roleBySet" class="clientWidth" style="width:95%;margin-top: 20px;height: 400px;display: none;padding-top: 10px;padding-bottom: 10px;" ></div>
       	</div>
    </div>
    <div class="danju danju_4" ><br/>
    	 <div style="overflow:auto;height:540px; ">	
        	<div class="byShootAddress" id="byShootAddress" style="width:95%;height: 540px;" ></div> 
        </div>
    </div>
</div> 
</body>
</html>