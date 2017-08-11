<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" type="text/css" href="<%=path%>/css/statistic/cutViewStatistuc.css">
<script type="text/javascript" src="<%=path%>/js/echarts3.6.2/echarts.min.js"></script>
<script type="text/javascript" src="<%=path %>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=path %>/js/makeplays/statistic/cutViewStatistuc.js"></script>
</head>
<body>
    <div class="my-container">
        <!-- tab页 -->
        <div class="tab-body-wrap">
            <!-- tab键容器 -->
                <div class="btn_tab_wrap">
                    <!-- tab键空白处 -->
                    <div class="btn_wrap">
                        <input type="button" class="set-btn" onclick="showSetParamWin()">
                    </div>
                    <!-- tab键 -->
                    <div class="tab_wrap">
                        <ul>
                            <li id="hotel_information" class="tab_li_current" onclick="showReportForm(this)">报表</li>
                            <li id="lodging_costs" onclick="showDataGrid(this)">数据</li>
                        </ul>
                    </div>
                    
                    
                </div>
               <p class="count-info">预计粗剪
                  <span id="series">0集</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日均剪辑
                  <span id="page">0页</span>&nbsp;&nbsp;&nbsp;&nbsp;<span id="minute">0分钟</span>
                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;预计精剪 <span id="cutCount">0集</span>
               </p>
        </div>
         <!-- 报表 -->
        <div class="public-style pic-div">
           <span class="border-left"></span>
           <span>剪辑进度</span>
           <div class="progress-cutView" id="progressCutView">
              <div class="public-progress">
                  <p>已拍页数:</p>
                  <div class="progress-div">
                      <div class="value-div">50%</div>
                      <div class="length-div"></div>
                  </div>
                  <p>16/116</p>
              </div>
           </div>
           
           <!-- echarts图表 -->
           <div id="fenJiDiv">
	          <span class="border-left"></span>
	          <span>分集进度</span>
	          <div class="echarts-series" id="seriesCount"></div>
           </div>
           
           <span class="border-left"></span>
           <span>每日剪辑量</span>
           <div class="echarts-daycount" id="dayCount"></div>
           
           
           
        </div>
        <!-- 数据 -->
        <div class="public-style data-div">
            <div class="cut-progress" id="cutProgressGrid"></div>
        </div> 
        
        <!-- 参数设置 -->
        <div class="jqx-window" id="paramSetWin">
            <div>参数设置</div>
            <div class="jqx-content">
                <ul>
                    <li>
                        <p>每集时长(分钟):</p>
                        <input type="text" id="seriesLength" onkeyup="onlyNumberPointer(this)" value='43'>
                    </li>
                    <li>
                        <p>预计精剪比(%):</p>
                        <input type="text" id="cutRatio" onkeyup="this.value=this.value.replace(/\D/g,'')" value='90' onafterpaste="this.value=this.value.replace(/\D/g,'')">
                        <span>（精剪时长/粗剪时长）</span>
                    </li>
                </ul>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick="confirmSetParam()">
                    <input type="button" value="取消" onclick="cancelSetParam()">
                </div>
            </div>
        </div>
        
    </div>
</body>
</html>