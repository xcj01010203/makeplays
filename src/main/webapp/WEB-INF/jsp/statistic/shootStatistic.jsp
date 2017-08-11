<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  	<link rel="stylesheet" href="<%=path%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<%=path%>/css/statistic/shootStatistic.css">
  
	<script type="text/javascript" src="<%=path%>/js/UI-Checkbox-master/checkbox.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/echarts/echarts.js"></script>
	<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=path%>/js/viewrole/produceProgressBar.js"></script>
	<script type="text/javascript" src="<%=path%>/js/makeplays/statistic/shootStatistic.js"></script>
</head>

<body>
	<div class="bd_wrap">
		<div class="btn_tab_wrap">
	    	<div class="btn_wrap">
	    		<div class="radio_wrap">
		    		<div class="ui toggle checkbox">
                      	<span class="font-color" id="byView">按场统计</span>
                      	<input type="checkbox" checked name="statisticsType" id="statisticsType" onclick="viewPageChange(this)">
                      	<label id="byPage">按页统计</label>
                    </div>
                </div>
	    	</div>
	        <div class="tab_wrap">	        
	        	<ul>
	            	<li class="tab_li_current">总体进度</li>
	                <li>日进度</li>
	                <li>戏份</li>
	                <li>拍摄地</li>
	            </ul>
	        </div>
	    </div>
	    <div id="div_0" class="contentdiv total">
	    	<div class="totalouter">
    		<div id="total_title" class="total_title">
    			<!-- <ul class="total_ul">
    				<li>计划拍摄天数：<span class="figure plan">100</span>天，实际拍摄天数：<span class="figure real">100</span>天</li>
    				<li>计划日均完成：<span class="figure plan">6.5</span>页，实际日均完成：<span class="figure real">8.9</span>页</li>
    			</ul>
    			<img src="../../../images/right.png" class="total_rightimg">
    			<ul class="total_ul">
    				<li>按当前日均进度拍摄预计<span class="figure result_word">提前<span class="result_figure">7</span>天</span>完成</li>
    				<li>按原计划完成需日均拍摄<span class="figure result_figure">5.7</span>页</li>
    			</ul> -->
    		</div>
    		<div class="total_progress">
    			<div class="total_group">
	    			<div class="border-left"></div>
	    			<label class="label-tips">总进度</label>
    			</div>
    			<div class="progress-con-div">
    				<ul id="total">总进度</ul>
    				<div class="jindu_kuang">
                   		<div class="jindu_tiao"></div>
                   	</div>
    			</div>
    		</div>
    		<div class="total_progress">
    			<div class="total_group">
	    			<div class="border-left"></div>
	    			<label class="label-tips">分项进度</label>
    			</div>
    			<div class="progress-con-div">
                     <p id="inside">内戏</p>
                     <div class="progress-div">
                         <div id="neiProgress"><span id="neiCountFlag"><img src="../images/icon_flag.png"></span></div>
                         <div id="waiProgress"><span id="waiCountFlag"><img src="../images/icon_flag.png"></span></div>
                     </div>
                     <p id="outside">外戏</p>
                 </div>
                 <div class="progress-con-div">
                     <p id="day">日戏</p>
                     <div class="progress-div">
                         <div id="dayProgress"><span id="dayCountFlag"><img src="../images/icon_flag.png"></span></div>
                         <div id="nightProgress"><span id="nightCountFlag"><img src="../images/icon_flag.png"></span></div>
                     </div>
                     <p id="night">夜戏</p>
                 </div>
                 <!-- <div class="progress-con-div">
                     <p id="literate">文戏</p>
                     <div class="progress-div">
                         <div id="wenProgress"><span id="wenCountFlag"><img src="../images/icon_flag.png"></span></div>
                         <div id="wuProgress"><span id="wuCountFlag"><img src="../images/icon_flag.png"></span></div>
                     </div>
                     <p id="kongfu">武戏</p>
                 </div> -->
    		</div>
    		</div>
	    </div>
	    <div id="div_1" class="contentdiv chart" style="display: none;">
	    	<div id="day_chart" class="chart_div"></div>
	    	<div id="daytotal_chart" class="chart_div"></div>
	    </div>
	    <div id="div_2" class="contentdiv chart" style="display: none;">
	    	<div class="div_sel">
	    		<label>
                	<input type="radio" name="viewRoleType" value="1" checked="checked" onclick="viewRoleTypeChange(this)">主要演员
                </label>
                <label>
                	<input type="radio" name="viewRoleType" value="2" onclick="viewRoleTypeChange(this)"/>特约演员
                </label>
	    	</div>
	    	<div class="chartdiv">
		    	<div id="viewrole_chart" class="chart_div"></div>
	    		<div id="viewrole_shootdays" class="show_text" style="display: none;"></div>
		    	<div id="viewrole_day_chart" class="chart_div" style="display: none;"></div>
		    	<div id="viewrole_series_chart" class="chart_div" style="display: none;"></div>
	    	</div>
	    </div>
	    <div id="div_3" class="contentdiv chart" style="display: none;">
	    	<div id="shootlocation_chart" class="chart_div" style="height: 550px;"></div>
	    </div>
    </div>
</body>
</html>
