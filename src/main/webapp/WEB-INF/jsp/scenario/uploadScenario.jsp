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

  </head>
  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/uploadScenario.css"/>
  <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
  
  <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/zeroClipboard/ZeroClipboard.js"></script>
  <script type="text/javascript" src="<%=path%>/js/Constants.js"></script>
  <script type="text/javascript" src="<%=path%>/js/sceneAnalysis/uploadScenario.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
  <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script> 
  <link rel="stylesheet" href="<%=basePath%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
  
  <body>
	<!-- 第一步窗口 -->  
	<div class='stepWindow' id='firstWindow'>
		<div class="stepTitle">
            <span class="step">第一步</span>将剧本中任一场景标题信息复制粘贴到下面的输入框中
            <span class="help-span">什么是场景标题<span onmouseover="showGuide()" onmouseout="closeGuide()" class='help'>?</span></span>
		</div>
		<div class="main-content-div">
            <textarea class="view-title-area" id="scenarioSample" rows="8" autofocus></textarea>
            <div class="title-div">请在下列场景标题样式中选择与您的剧本相近的格式，如果没有，直接点击下一步</div>
            <div class="text-div-info" name='exampleTextarea' id="example1" onclick="selectFormat(this)">
	            <input type="hidden" id="firstInput" value=''>
	        </div>
	        <div class="text-div-info" name='exampleTextarea' id="example2" onclick="selectFormat(this)">
	            <input type="hidden" id="secondInput" value=''>
	        </div>
	        <div class="text-div-info" name='exampleTextarea' id="example3" onclick="selectFormat(this)">
	            <input type="hidden" id="thirdInput" value=''>
	        </div>
	        <div class="show-example-picture" id="showGuidePicture">
	            <img alt="" src="../../images/upload/guide/example.png">
	        </div>
		</div>
		<div class="step-btn-list-div">
            <input class='step-btn btn-letter' type="button" value="关闭" onclick="closeUpload()">
            <input class='step-btn' type="button" value="下一步" onclick="nextThirdStep()">
		</div>
	</div>
	
	<!--第三步窗口 -->
	<div class='stepWindow hidden-window' id='thirdWindow' onselectstart="return false">
		<div class="stepTitle"><span class="step">第二步</span>配置场景格式</div>
		
		<div class="main-content-div">
		   <div class="pull-left-container">
          <div class="pull-left view-info-div" id="viewInfoDiv">
								<!-- 场景标题 -->
								<div>
								    <div class="title-div">场景标题：</div>
								    <textarea id="thirdScenarioSample" class="view-title-area" rows="4"></textarea>
								</div>
		            <!-- 配置场景格式 -->
		            <!--  <div style="margin-top: 16px;" class="format-div"> -->
		            <div class='format-title-div'>
		                <div class="title-div">
		                   <span>配置场景场景格式（从右侧备选元素中拖拽元素）：</span>
		                   <span class="help-span"><span onclick="showExample()" class='help'>?</span></span>
		                </div>
		                <div class='span-div-format'>
			                <dl class="edit-container">
				                <dd id="formatTag" class="J-container"></dd>
			        		    </dl>
			        		    <div class='other-rule-div' id="otherRule">其它：<label><input class="vartical-middle" type="checkbox" id="supportCNViewNo"/>场次为“三十二”形式的中文数字</label></div>
			        	    </div>
		       	    </div>
		       	    
				        <!-- 解析场景 -->
				        <div class="validate-title-div">
					        <div class="title-div">
		                        <span>根据校验的格式解析场景</span>
		                        <input type="button" class="btn refresh btn-letter" onclick="refreshSample()" value="解析">
					        </div>
		             		<table class="anaresult" id="analyResult">
		                 		<tbody>
			                     	  <!-- <tr>
			                             <td>集</td>
			                             <td></td>
			                         </tr>
			                         <tr>
			                             <td>场</td>
			                             <td></td>
			                        </tr> -->
			                        <tr id="viewInfoTr">
			                            <td>主场景/次场景/三级场景</td>
			                            <td></td>
			                         </tr>
			                         <tr>
			                            <td>气氛</td>
			                            <td></td>
			                        </tr>
			                        <tr>
			                            <td>内外景</td>
			                            <td></td>
			                         </tr>
			                         <tr>
			                             <td>人物（/隔开）</td>
			                            <td></td>
			                        </tr>
		                        </tbody>
		                    </table>
		                </div>
            </div>
       
	       	<div class="pull-left hidden-window" id="excempleWindow">
							<div class="title-div"><a class='back-format-a' href="javascript:void(0)" onclick="closeExample()">&lt&lt返回格式编辑</a></div>
							<!-- <div class="example-info-p">通过点击和拖拽，将元素和分隔符按照剧本场景标题的格式顺序排列好，如下图所示：</div> -->
							<div class="title-div">场景标题：</div>
							<div class="example-content-div">
									<span class="example-span" id='seriesNOTitle' title="集" onmouseover="seriesChange()" onmouseout="resizeSeries()">1</span>
									-
									<span class="example-span" title="场" id='viewNoTitle' onmouseover="viewNoChange()" onmouseout="resizeViewNo()">3</span>
									<span class="example-span" id="atmosphereTitle" title='气氛' onmouseover="atmosChange()" onmouseout="resizeAtmos()">日</span>
									<span class="example-span" id='siteTitle' title='内外景' onmouseover="siteChange()" onmouseout="resizeSite()">外</span><br>
									<span class="example-span" id='viewTitle' onmouseover="viewChange()" onmouseout="resizeView()">场景：</span>
									<span class="example-span" id="viewContentTitle" onmouseover="viewContentChange()" onmouseout="resizeViewContent()">繁华的街道</span><br>
									<span class="example-span" id="roleTitle" onmouseover="roleChange()" onmouseout="resizeRole()">人物：</span>
									<span class="example-span" id="roleContentTitle" onmouseover="roleContentChange()" onmouseout="resizeRoleContent()">华路，张宇，洪炎</span>
							</div>
				
		          <div class="title-div">格式：</div>
							<div class="example-content-div format-content">
							    <div class="example-format-div">
									<span class="example-tag bg-78a6cd" id="seriesNoSpan">集</span>
									<span class="example-tag bg-f0f0f0"  title="通配（逗号，顿号，中划线，右斜杠，分号，空格，冒号，点）">[*]</span>
									<span class="example-tag bg-78a6cd" id="viewNoSpan">场</span>
									<span class="example-tag bg-f0f0f0"  title="通配（逗号，顿号，中划线，右斜杠，分号，空格，冒号，点）">[*]</span>
									<span class="example-tag bg-78a6cd" id="atmosphereSpan">气氛</span>
									<span class="example-tag bg-f0f0f0"  title="通配（逗号，顿号，中划线，右斜杠，分号，空格，冒号，点）">[*]</span>
									<span class="example-tag bg-78a6cd" id="siteSpan">内外景</span>
									<span class="example-tag bg-f0f0f0 newline" title="换行"></span>
									<span class="example-tag bg-f0f0f0" id="viewSpan">场景：</span>
									<span class="example-tag bg-78a6cd" id="viewContentSpan">场景*</span>
									<span class="example-tag bg-f0f0f0" id='roleSpan'>人物：</span>
									<span class="example-tag bg-78a6cd" id="roleContent">人物</span>
								</div>
							</div>
			    </div>
       </div> 
            <!-- 备选标签 -->
	       	<div class="pull-left scriplete-separator-div">
	        	<div class="title-div">备选标签：</div>
	      		<div class="pull-right edit-choose-container">
		        	<dt class="title">备选场景元素：</dt>
		            <dd id="scripleteListContainter" class="p15 J-dragged scriplete">
		            </dd>
		            <dt class="title">备选分隔符：</dt>
		            <dd id="separatorListContainter" class="J-dragged separator">
		            </dd>
		            <div class="add-separartor-info"><span>*注：<br>1、分隔符指剧本中相对固定的文字或标点符号<br>2、[*]替代了大多数常见的分隔符<br>3、如果备选分隔符列表中没有需要的，可以点击加号添加。</span></div>
	           </div>
	       	</div>
        </div>
        
       	<div class='step-btn-list-div'>
	       	<input class='step-btn' type="button" value="上一步" onclick="goToFirstWindow()">
            <input class='step-btn btn-letter' type="button" value="关闭" onclick="closeUpload()">
	       	<input class='step-btn' type="button" value="下一步" onclick="nextFiveStep()">
       	</div>
	</div>
	
	<!-- 第五步窗口 -->
	<div class='stepWindow hidden-window upload-container' id='fivethWindow'>
		<div class="stepTitle"><span class="step">第三步</span>上传剧本
		
		<!-- 分析页数输入框 -->
	    <div class='word-line-count'>
	    	<input class='word-line-input' id="wordCount" type="text" value="35">字/行&nbsp;&nbsp;
	    	<input class='word-line-input' id="lineCount" type="text" value="40">行/页&nbsp;&nbsp;
	    	<label><input type="checkbox" id="pageIncludeTitle" checked>计算页数时包含标题</label>
	    </div>
		</div>
		<div class="main-content-div">
			<div class="table-uploaddiv">
				<table id="filelist" class="table-upload">
					<thead>
						<tr>
							<th><p class="file-p">文件</p></th>
							<th><p class="process-p">上传进度</p></th>
							<th><p class="operate-p">操作</p></th>
						</tr>
					</thead>
				    <tbody></tbody>
				</table>
			</div>
			<div class="manual-series-div" id="seriesNoDiv">
				<span>自定义格式中没有集次--</span>
				<label><input class="vartical-middle" name="seriesNoFlag" value="1" type="radio" checked>我的剧本中包含“第XX集”标识</label>
				<label class="manual-series-label"><input class="vartical-middle" name="seriesNoFlag" value="2" type="radio">我要手动填写集次</label>
				<input class="series-no" id="seriesNo"/>
            </div>
            <div>
                <!-- <button type="button" id="selectFileBtn" class='file-btn' type="button">选择文件</button> -->
                <div id="selectFileBtn" class="file-btn select-file-btn">选择文件</div>
                <input type="button" id="uploadBtn" class="file-btn btn-letter" type="button" value="上传">
            </div>
            <div class="title-div">
                <span>解析日志：</span>
                <a id="copyInfo" class="copy-log-a" href="javascript:void(0)" data-clipboard-target="logContent">复制</a>
            </div>
            <div class="copy-container">
                <div id="analysisLog" class="text-info analysis-log"></div>
                <input type="hidden" id="logContent" value='' >
            </div>
        </div>
        
        <div class='step-btn-list-div'>
            <input class='step-btn' type="button" value="上一步" onclick="goTothirdWindow()">
            <input class='step-btn btn-letter' type="button" value="关闭" onclick="closeUpload()">
        </div>
		  
          <!-- <p class="mark"></p> -->
          
    </div>
  </body>
</html>
