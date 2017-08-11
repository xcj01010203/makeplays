<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object hasBatchDeleteViewAuth = false;
Object isScenarioReadonly = false;
Object hasImportScenarioAuth = false;
Object hasExportScenarioAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.DELETE_VIEW_BATCH) != null){
        hasBatchDeleteViewAuth = true;
    }
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.SCENARIO_ANALYSE)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.SCENARIO_ANALYSE) == 1){
        	isScenarioReadonly = true;
        }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.UPLOAD_SCENARIO) != null){
    	hasImportScenarioAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_SCENARIO) != null){
    	hasExportScenarioAuth = true;
    }
}
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


<link rel="stylesheet" href="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/uploadScenarioGuide.css">
<link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sceneAnalysis/sceneAnalysis.js"></script>
<script src="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.js"></script>

<script>
    var hasBatchDeleteViewAuth = <%=hasBatchDeleteViewAuth%>;
    var isScenarioReadonly = <%=isScenarioReadonly%>;
    var hasImportScenarioAuth = <%=hasImportScenarioAuth%>;
    var hasExportScenarioAuth = <%=hasExportScenarioAuth%>;
</script>
<script type="text/javascript"	src="<%=basePath%>/js/view/selectPanel.js"></script>
</head>
<body>
	<input type="hidden" id="startSeriesNoInput" value="${startSeriesNo}">
	<div class="top_wrap">
		<div class="button_box">
			<div class="icon icon_uploading" title="剧本上传" onclick="uploadPlay()"></div>
			<div class="icon icon_man_refresh" title="角色提取" onclick="refreshFigure()"></div>
			<div class="icon icon_page_refresh" title="页数重新分析" onclick="reAnalysePage()"></div>
      <div class="icon icon_prop_refresh" title="服化道提取" onclick="refreshProp()"></div> 
			<div class="icon icon_export" title="剧本导出" onclick="exportScenario()"></div>
			<div class='icon icon_batdel' title='批量删除' onclick="batchDelete()"></div>
			
			<!-- 模式切换按钮 -->
		  <div class='icon icon_compare' title='剧本对比模式' onclick="scenarioCompare()"></div>
      <div class='icon icon_edit' title='剧本编辑模式' onclick="scenarioEdit()"></div>
      <div class='icon icon_analysis default-status' title='剧本分析模式' onclick="scenarioAnalysis()"></div>
      <div class="icon icon_fabu_juben" id="fabuJubenBtn" title="发布" onclick="releaseScenarioCon()"></div>     
			
			<div class="tvplayNo" id="scenarioViewDiv">集场：</div>
			<div class="ji_chang_box ji_chang_box_up tvplayNo" id="seriesViewNoDiv" onclick="openOrClosePancle(this)">
				<span>1-1</span>
			</div>

			<div class="movieNo" id="moviceViewDiv">当前场：
			</div>
			<div class="ji_chang_box ji_chang_box_up movieNo" id="movieViewNoDiv"  onclick="openOrClosePancle(this)">
				<span>1</span>
			</div>
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
		<div id="con_middle">
			<div id="viewContent" onmouseup="isOpenRigthMenu(event)">
				<div id="viewTitle">
					<div class="view-title-div" >
						<label id="title"></label>
						<div class="noget-info-div">
						    <label class="noget-info-label" id="noGetRoleNames" style='display: block;'></label>
                            <label class="noget-info-label" id="noGetProps" style='display: block;'></label>
						</div>
					</div>
				</div>
				<span></span>
			</div>
			<div id="view_btn">
				<div class="view-btn-div"></div>
				<div id="btn_list">
					<input type="button" title="上一场" id="preScene"	onclick="searchPreScene()"> 
					<input class="chang_tab_box" type="button" title="下一场" id="nextScene" onclick="searchNextScene()">
				</div>
			</div>
		</div>
		<div id="con_right">
			<div id="right_main">
				<div class="con_right_title">
					<span id="right_title_span">场景信息</span>
					<div id="operateBtn">
						<input type="button" id="btnsure" value="确&nbsp;&nbsp;定" onclick="saveView()"> &nbsp;&nbsp;&nbsp; 
						<input type="button" id="btndelete" value="删&nbsp;除" onclick="deleteViewInfo()">&nbsp;&nbsp;&nbsp; 
						<input type="button" id="btncancle" value="取&nbsp;&nbsp;消"	onclick="cancleViewInfo()">
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

	<div id='uploadScenarioWindow' class="hiddenWindow">
		<div>上传剧本 </div>
		<div id="uploadScenarioDiv">
			<iframe id="uploadIframe" frameborder="0" scrolling="yes" src="">
			</iframe>
		</div>
	</div>

	<div class="hiddenWindow" id='exportScenarioWindow'>
		<div id="exportScenarioHeader">
			<span> 导出剧本 </span>
		</div>
		<div id="exportScenarioDiv">
			<div class="exportTvOption">
				<input class="sxport-radio-input" type="radio" name="exportOption" value="1" checked>不分集 
				<input class="export-radio-fen sxport-radio-input" type="radio"	name="exportOption" value="2" >分集
			</div>
			<div class="exportMovieOption">确定导出？
			</div>
			<div style="text-align: center;">
				<input class="mybtn export" type="button" id="exportBtn" value="导出" onclick="confirmExport()">
				<input class="mybtn" type="button" id="cancelExpBtn" value="取消">
			</div>
		</div>
	</div>

	<div class="hiddenWindow" id='refreshFigureWindow'>
		<div id="refreshFitureHeader">
			<span id=""> 角色提取 </span>
		</div>
		<div>
			<div class="rersh-confirm-div">
				<input class="mybtn confirm-refresh-figure" type="button" value="根据已有角色分析" onclick="refreshFigureFromExistRole()"> 
				<input class="mybtn confirm-refresh-figure" type="button" value="智能提取新角色" onclick="analyseScenarioFigure()">
			</div>
		</div>
	</div>
	
	<div id="refreshPageWindow" class="hiddenWindow">
		<div>重新分析页数</div>
		<div>
			<div class="refresh-page-div">
				<div>
					<input id="wordCount" type="text" value="">字/行
				</div>
				<div>
					<input id="lineCount" type="text" value="">行/页
				</div>
                <div>
                    <label><input id="pageIncludeTitle" type="checkbox" checked>计算页数时包含标题</label>
                </div>
			</div>
			<div class="confirm-refresh-div">
				<input class="mybtn refresh-page-btn" type="button" id="reAnalysePageBtn"  value="确定" onclick="confirmRefreshPage()">
				<input class="mybtn refresh-page-btn" type="button" id="cancelReAnalyseBtn" value="取消">
			</div>
		</div>
	</div>
	<div class="hiddenWindow new-role-list-win" id="newRoleListWindow">
	   <div>角色列表</div>
	   <div>
	       <div class="new-role-desc">
	           <span id="newRoleDesc"></span>
	           <div class="ui toggle checkbox select-all-new-role">
                   <input id="allNewRoleCheckbox" type="checkbox" onclick="checkAllNewRole(this)" checked>
                   <label>全选</label>
               </div>
	       </div>
	       <div class="new-role-list-div">
               <div id="newRoleList">
               </div>
	       </div>
	       <div class="mybtn-group">
	           <input class="mybtn" type="button" value="确定" onclick="saveProbablyRoles()">
	           <input class="mybtn" type="button" value="取消" onclick="closeNewRoleWin()">
	       </div>
	   </div>
	</div>

	<div id='delViewWindow' class="hiddenWindow">
		<div>
			<span id="">删除场景</span>
		</div>
		<div>
			<div id="viewNoDiv" class="viewno-div"></div>
			<div class="mybtn-group">
				<input class="mybtn" type="button" id="delViewBtn" style=""	value="删除"   onclick="confirmBatchDelView()"> 
				<input class="mybtn" type="button" id="cancelDelBtn"  value="取消">
			</div>
		</div>
	</div>

	<div id='uploadResultWindow' class="hiddenWindow">
		<div>
			<span id="">结果处理(以下场次在顺场表中已被手动修改过，请选择“跳过/替换”操作)</span>
		</div>
		<div>
			<div class="upload-result">
				<div class="viewno-result" id="viewNoResult" ></div>
				<iframe class="view-compare" id="viewCompare"  src=""></iframe>
			</div>
			<div class="mybtn-group"> 
				<input class="mybtn" type="button" id="replaceAllBtn" style="" value="场景/剧本全替换" onclick="confirmReplaceAll()"> 
				<input class="mybtn" type="button" id="replaceSecBtn" style="" value="只替换剧本" onclick="confirmReplaceSec()"> 
				<input class="mybtn" type="button" id="skipBtn" value="跳过" onclick="confirmSkip()"> 
				<input class="mybtn" type="button" id="cancelDealResultBtn" value="取消" onclick="confirmCancelDealResult()">
			</div>
		</div>
	</div>

	<!-- 分析操作菜单 -->
	<div class="add-color-box" id="addColorBox">
		<ul style="background-color: #fff;">
			<li id="c_majorscene" onclick="analysisRightClick(this)">主场景</li>
			<li id="c_secscene" onclick="analysisRightClick(this)">次场景</li>
			<li id="c_thirdscene" onclick="analysisRightClick(this)">三级场景</li>
			<li id="c_content" onclick="analysisRightClick(this)">主要内容</li>
			<li id="c_majoractor" onclick="analysisRightClick(this)">主要演员</li>
			<li id="c_guestactor" onclick="analysisRightClick(this)">特约演员</li>
			<li id="c_messactor" onclick="analysisRightClick(this)">群众演员</li>
			<li id="c_clothes" onclick="analysisRightClick(this)">服装</li>
			<li id="c_makeup" onclick="analysisRightClick(this)">化妆</li>
			<li id="c_commonprop" onclick="analysisRightClick(this)">道具</li>
			<li id="c_specialprop" onclick="analysisRightClick(this)">特殊道具</li>
		</ul>
	</div>

	<div id="step-container" class="tipwizard-container">
		<div class="tipwizard-bg">
			<div class="step step1" data-step="1">
				<button class="btn-close" type="button">close</button>
				<button class="btn-next" type="button">下一步</button>
				<div class="tipwizard-btn">
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
					<button type="button" class="btn-step">close</button>
				</div>
			</div>
		</div>
	</div>
	
	
	<!-- 发布弹窗 -->
	<div class="jqx-window" id="releaseWin">
	   <div>发布</div>
	   <div class="jqx-body-content">
	       <fieldset class="fabu-tips-fieldset">
	           <legend>提醒</legend>
	           <p class="fabu-tips">有以下部分剧本为新上传或做了修改。<br>点击发布后，剧组其他成员方可在手机端查阅新内容。</p>
	       </fieldset>
	       <ul>
	           <li>
	             <p>标题&nbsp;:</p>
	             <input type="text" id="releaseTitle" value="剧本变动提醒">
	           </li>
	           <li>
	             <p>内容&nbsp;:</p>
	             <textarea id="releaseContent"></textarea>
	           </li>
	           <li>
	               <p></p>
	               <div><label><input type="checkbox" id="autoShowPublishWin">不再自动弹窗（可点击右上方闪烁按钮发布）</label></div>
	           </li>
	       </ul>
	       <div class="jqx-win-btn">
	           <input type="button" value="发布" onclick="releaseContentEvt()">
	           <input type="button" value="取消" onclick="cancelReleae()">
	       </div>
	   </div>
	</div>

</body>
</html>
