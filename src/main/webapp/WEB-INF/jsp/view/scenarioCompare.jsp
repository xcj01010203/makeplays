<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title></title>
    
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" href="<%=basePath%>/js/mergely/codemirror.css" type="text/css">
    <link rel="stylesheet" href="<%=basePath%>/js/mergely/mergely.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/sceneAnalysis/scenarioCompare.css">
		
    <script type="text/javascript" src="<%=basePath%>/js/mergely/codemirror.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/mergely/mergely.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/sceneAnalysis/scenarioCompare.js"></script>
  
  </head>
  
  <body>
      <div class="my-container">
          <div class="top_wrap">
              <div class="button_box">
                  <!-- 模式切换按钮 -->
		              <div class='icon icon_compare default-status' title='剧本对比模式' onclick="scenarioCompare()"></div>
		              <div class='icon icon_edit' title='剧本编辑模式' onclick="scenarioEdit()"></div>
		              <div class='icon icon_analysis' title='剧本分析模式' onclick="scenarioAnalysis()"></div>
                  
                  <div class="tvplayNo" id="scenarioViewDiv">集场：</div>
		                <div class="ji_chang_box ji_chang_box_up tvplayNo" id="seriesViewNoDiv" onclick="openOrClosePancle(this)">
		                  <span>1-1</span>
		                </div>
		                <div class="movieNo" id="moviceViewDiv">当前场：</div>
		                <div class="ji_chang_box ji_chang_box_up movieNo" id="movieViewNoDiv"  onclick="openOrClosePancle(this)">
		                  <span>1</span>
		              </div>
                  
		              <div class="page-btn-list">
		                  <a class="button back" href="javascript:void(0)" id="preScene" onclick="searchPreScene()">上一场</a>
		                  <span>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;</span>
		                  <a class="button next" href="javascript:void(0)" id="nextScene" onclick="searchNextScene()">下一场</a>
		              </div>
		              
		              
		          </div>
		          <div class="select_box">
		              <table class="select_table">
		                <tr class="ji_tab_box" id="seriesNoTr">
		                </tr>
		                <tr class="chang_tab_box" id="viewNoTr">
		                </tr>
		              </table>
		          </div>
              
          </div>
          
          <!-- 标题 -->
          <div class="scenario-title-div">
              <div class="scenario-left-title" id="oldScenarioTitle" >
                
                <select class="version-select" id="versionSelect" onchange="queryViewContent(this)">
                    <option>上一次发布的版本</option>
                </select>
                <span>（目前最多支持三个历史版本内容查看）</span>
              </div>
              <div class="scenario-right-title" id="newScenarioTitle" >当前剧本</div>
          </div>
          <!-- 内容 -->
          <div class="main-content-div" id="main_content" onclick="closeDownPancle()">
              <div class="scenario-div-container">
              <!-- 文本比较器 -->
                  <div id="mergely-resizer" style="height:100%;">
								      <div id="compare" style="height: 100%;">
								      </div>
							    </div>
              </div>
              
              <!-- <div id="view_btn">
	              <div class="view-btn-div"></div>
	              <div id="btn_list">
	                <input type="button" title="上一场" id="preScene"  onclick="searchPreScene()"> 
	                <input class="chang_tab_box" type="button" title="下一场" id="nextScene" onclick="searchNextScene()">
	              </div>
	            </div> -->
              
          </div>
          
          
      </div>
  
  </body>
</html>
