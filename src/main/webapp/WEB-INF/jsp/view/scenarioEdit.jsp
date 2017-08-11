<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
		<link rel="stylesheet" type="text/css" href="styles.css">
		-->
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/sceneAnalysis/scenarioEdit.css">
		
		
		<script type="text/javascript"  src="<%=basePath%>/js/view/selectPanel.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/sceneAnalysis/scenearioEdit.js"></script>
    
  </head>
  
  <body>
      <%-- <input type="hidden" id="startSeriesNoInput" value="${startSeriesNo}"> --%>
      <input type="hidden" id="seriesNoInput">
      <input type="hidden" id="scenarioChange" value="0">
      <div class="top_wrap">
			    <div class="button_box">
				      <div class="icon icon_add" id='addViewContent' title="添加剧本" onclick="addScenario()"></div>
				      <div class='icon icon_save' id="icon_save_btn" title='保存剧本' onclick="saveScenarMethod()"></div>
				      
				      <!-- 模式切换按钮 -->
				      <div class='icon icon_compare' title='剧本对比模式' onclick="scenarioCompare()"></div>
				      <div class='icon icon_edit default-status' title='剧本编辑模式' onclick="scenarioEdit()"></div>
				      <div class='icon icon_analysis' title='剧本分析模式' onclick="scenarioAnalysis()"></div>
				      
				      <div class="tvplayNo" id="scenarioViewDiv">集场：</div>
				      <div class="ji_chang_box ji_chang_box_up tvplayNo" id="seriesViewNoDiv" onclick="openOrClosePancle(this)">
				        <span>1-1</span>
				      </div>
				
				      <div class="movieNo" id="moviceViewDiv">当前场：</div>
				      <div class="ji_chang_box ji_chang_box_up movieNo" id="movieViewNoDiv"  onclick="openOrClosePancle(this)">
				        <span>1</span>
				      </div>
				      <!-- 保存说明 -->
				      <div class="save-tips" id="saveTips"></div>
				      
			    </div>
			    <div class="select_box" onclick="closeRgithSelectWin()">
				      <table class="select_table">
				        <tr class="ji_tab_box" id="seriesNoTr">
				        </tr>
				        <tr class="chang_tab_box" id="viewNoTr">
				        </tr>
				      </table>
			    </div>
			</div>
			
			<div id="main_content" onclick="closeDownPancle()">
			    <!-- 剧本内容 -->
			    <div class="con-middle" id="con_middle">
			      <div id="viewContent" onmouseup="isOpenRigthMenu(event)">
			        <div id="viewTitle">
			          <div class="view-title-div" >
			            <!-- <label id="title"></label> -->
			            <textarea id="title" onkeyup="changeScenarioValue()"></textarea>
			            <div class="jiexi-script-title" id="analysisTitleBtn" onclick="analysisScenarioTitle()"></div>
			            <!-- <div class="noget-info-div">
			                <label class="noget-info-label" id="noGetRoleNames" style='display: block;'></label>
			                <label class="noget-info-label" id="noGetProps" style='display: block;'></label>
			            </div> -->
			          </div>
			        </div>
			        <!-- <span></span> -->
			        <textarea class="script-content" id= "scriptContent" onkeyup="changeScenarioValue()"></textarea>
			      </div>
			      <div id="view_btn">
			        <div class="view-btn-div"></div>
			        <div id="btn_list">
			          <input type="button" title="上一场" id="preScene"  onclick="searchPreScene()"> 
			          <input class="chang_tab_box" type="button" title="下一场" id="nextScene" onclick="searchNextScene()">
			        </div>
			      </div>
			    </div>
			    <div id="con_right">
			      <div id="right_main">
			        <div class="con_right_title">
			          <span id="right_title_span">场景信息</span>
			          <div id="operateBtn">
			            <!-- <input type="button" id="btnsure" value="确&nbsp;&nbsp;定" onclick="saveViewInfo()"> &nbsp;&nbsp;&nbsp; 
			            <input type="button" id="btndelete" value="删&nbsp;除" onclick="deleteViewInfo()">&nbsp;&nbsp;&nbsp; 
			            <input type="button" id="btncancle" value="取&nbsp;&nbsp;消"  onclick="cancleViewInfo()"> -->
			          </div>
			        </div>
			        <div>
			          <div id="con_right_bottom">
			            <div class="con_right_main">
			              <iframe src="" name="f_scene_create" id="f_scene_create" frameborder="0" scrolling="yes">
			              </iframe>
			            </div>
			            <div class="bottom_button"></div>
			          </div>
			        </div>
			      </div>
			    </div>
			  </div>
			  
			  <!-- 分析操作菜单 -->
			  <div class="add-color-box" id="addColorBox">
			    <ul style="background-color: #fff;">
			      <li onclick="sceneDisassemb(1)">剧本复制拆解</li>
			      <li onclick="sceneDisassemb(2)">剧本剪切拆解</li>
			    </ul>
			  </div>
			  
			  <!-- 解析剧本标题弹窗 -->
			  <div class="jqx-window" id="analysisScenarioWin">
			     <div>解析</div>
			     <div class="jqx-content">
			         <p class="jqx-win-tips">解析结果如下:</p>
			         <div class="view-info-con">
			             <table class="view-info-table" id="viewInfoTable" cellspacing="0" cellpadding = "0">
			                <!--  <tr>
			                     <td style="width: 30%;">集</td>
			                     <td style="width: 70%;">
			                         <div id="e1_ji"></div>
			                     </td>
			                 </tr>
			                 <tr>
                           <td style="width: 30%;">场</td>
                           <td style="width: 70%;">
                              <div id="e2_view"></div>
                           </td>
                       </tr>
			                 <tr>
                           <td style="width: 30%;">主场景/次场景/三级场景</td>
                           <td style="width: 70%;">
                              <div id="e3_location"></div>
                           </td>
                       </tr>
                       <tr>
                           <td style="width: 30%;">气氛</td>
                           <td style="width: 70%;">
                              <div id="e4_atmo"></div>
                           </td>
                       </tr>
                       <tr>
                           <td style="width: 30%;">内外景</td>
                           <td style="width: 70%;">
                              <div id="e5_neiwai"></div>
                           </td>
                       </tr>
                       <tr>
                           <td style="width: 30%;">季节</td>
                           <td style="width: 70%;">
                              <div id="e6_season"></div>
                           </td>
                       </tr>
                       <tr>
                           <td style="width: 30%;">人物(/隔开)</td>
                           <td style="width: 70%;">
                              <div id="e7_person"></div>
                           </td>
                       </tr> -->
			             </table>
			         </div>
			         <div class="jqx-win-btn">
			             <input type="button" value="替换" onclick="replaceViewFormat()">
			             <input type="button" value="取消" onclick="cancelAnalysis()">
			         </div>
			     </div>
			  </div>
			  
			  
			  <div class="jqx-window analysis-fail-win" id="analysisFailWin">
			     <div>提示</div>
			     <div>
			         <div class="message">
			            <div class="error-message">格式不正确，自己反省去</div>
                        <div>请按照下面的格式正确编写剧本标题</div>
			         </div>
                     <div class="scenario-format">
                        <div></div>
                        <p class="title">例</p>
                        <p class="sample">1-2 大门外 夜/外<br>人物：张三，李四，王五</p>
                     </div>
			         <div class="jqx-win-btn">
                         <input type="button" value="确定" id="cancelFailWinBtn">
                     </div>
			     </div>
			  </div>
  </body>
</html>
