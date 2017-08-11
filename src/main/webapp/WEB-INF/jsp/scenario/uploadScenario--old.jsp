<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>上传剧本</title>
    
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
  <script type="text/javascript" src="<%=basePath%>/js/zeroClipboard/ZeroClipboard.min.js"></script>
  <script type="text/javascript" src="<%=path%>/js/Constants.js"></script>
  <script type="text/javascript" src="<%=path%>/js/sceneAnalysis/uploadScenario.js"></script>
  
  <body>
    <div class="mask">
        <div class="wrap clearfix J-wrap expanded"><!-- 此处为默认展开状态，expanded -->
	        <div class="pull-left upload-container">
	            <span class="stepTitle step-left-span">
	            	<span class="step">第四步</span>
	            	上传剧本
	            	<span onclick="showGuide()" class='help'>?</span>
	            </span>
                <div class="table-uploaddiv">
		            <table id="filelist" class="table-upload">
		                <thead>
		                    <tr>
		                        <th><p class="file-p">文件</p></th>
		                        <th><p class="process-p">上传进度</p></th>
	                            <th><p class="operate-p">操作</p></th>
		                    </tr>
		                </thead>
		                <tbody>
		                </tbody>
		            </table>
	            </div>
	            <p class="mark"></p>
	            <div id="seriesNoDiv">
	               <div>
		               <span>自定义格式中没有集次--</span>
		               <label>
		               	<input class="vartical-middle" name="seriesNoFlag" value="1" type="radio" checked>
		               	我的剧本中包含“第XX集”标识
		               </label>
	               </div>
	               <div class="fill-series-div">
	                   <label>
	                   	<input class="vartical-middle" name="seriesNoFlag" value="2" type="radio">
	                   	我要手动填写集次
	                   </label>
	                   <input id="seriesNo"/>
				   </div>
	            </div>
	            <div class="text-right">
	                <button id="selectFileBtn" class="btn" type="button">选择文件</button>
	                <button id="uploadBtn" class="btn btn-letter" type="button">上传</button>
	            </div>
	            <p>解析日志：</p>
	            <div class="copy-container">
	               <div id="analysisLog" class="text-info analysis-log">
	               </div>
	               <!-- <span id="copyInfo" class="info-copy" data-clipboard-target="analysisLog">单击复制</span> -->
	            </div>
	            <p>缺省页数定义：
                   <input class="mini-field" type="text" id="wordCount" name="wordCount" value="35"> 字/行，
                   <input class="mini-field" type="text" id="lineCount" name="lineCount" value="40"> 行/页
                </p>
                <dd class="text-right">
                    <button id="closeUploadBtn" class="btn btn-letter" type="button" onclick="closeUpload()">关闭</button>
                </dd>
	        </div>
	        <div class="line-middle"></div>
	        <dl class="pull-right edit-container">
                <span class="stepTitle"><span class="step">第一步</span>将剧本中任一场景信息复制粘贴到下面的输入框中</span>
                <textarea id="scenarioSample" class="text-info" rows="3"></textarea>
	            <span class="stepTitle sec-top-27"><span class="step">第二步</span>根据场景信息配置场景元素和分隔符</span>
	            <dt class="labelTitle">元&nbsp;&nbsp;素：</dt>
	            <dd id="scripleteListContainter" class="p15 J-dragged">
	            </dd>
	            <dt class="labelTitle">分隔符:</dt>
	            <dd id="separatorListContainter" class="J-dragged">
	            </dd>
	            <dt class="labelTitle">格&nbsp;&nbsp;式：</dt>
	            <dd id="formatTag" class="J-container">
	            </dd>
	            <dt class="labelTitle otherRule">其&nbsp;&nbsp;它：</dt>
                <dd class="otherRule other-dd">
                    <label>
                    	<input class="vartical-middle" type="checkbox" id="supportCNViewNo"/>
                    	场次为“三十二”形式的中文数字
                    </label>
                </dd>
                <dd>
                </dd>
                <span class="stepTitle sec-top-180"><span class="step">第三步</span>根据配置解析场景<button class="refresh btn btn-letter" onclick="refreshSample()">解析</button></span>
                <dd class="ana-result-dd">
                    <table id="analyResult" class="anaresult">
                        <tbody>
                            <tr>
                                <td>集</td>
                                <td></td>
                            </tr><tr>
                                <td>场</td>
                                <td></td>
                            </tr><tr>
                                <td>主场景/次场景/三级场景</td>
                                <td></td>
                            </tr><tr>
                                <td>气氛</td>
                                <td></td>
                            </tr><tr>
                                <td>内外景</td>
                                <td></td>
                            </tr><tr>
                                <td>人物（/隔开）</td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </dd>
	        </dl>
	    </div>
	</div>
  </body>
</html>
