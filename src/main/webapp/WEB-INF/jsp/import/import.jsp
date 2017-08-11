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
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">
       
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/import/import.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/import/import.js"></script>
  </head>
  
  <body>
      <div class="upload-container">
          <input type="hidden" id="uploadUrl" value="${uploadUrl}"><!-- 上传地址 -->
          <input type="hidden" id="needIsCover" value="${needIsCover}"><!-- 是否需要询问覆盖数据与否 -->
          <input type="hidden" id="refreshUrl" value="${refreshUrl}"><!-- 刷新地址 -->
          <input type="hidden" id="templateUrl" value="${templateUrl}"><!-- 下载模板地址 -->
          <input type="hidden" id="isCompareData" value="${isCompareData}"><!-- 是否要比较数据 -->
          <input type="hidden" id="queryDelete" value="${queryDelete}"><!-- 是否需要询问你删除原有数据 -->
          <span class="upload-title">导入文件</span>
          <div class="table-upload-div">
              <table class="upload-table" id="fileListTable" cellpadding="0" cellspacing="0">
                  <thead>
                      <tr>
		                      <th>
		                          <p class="file-title">文件</p>
		                      </th>
		                      <th>
		                          <p class="file-upload-proc">上传进度</p>
		                      </th>
		                      <th>
		                          <p class="operation-p">操作</p>
		                      </th>
                      </tr>
                  </thead>
                  <tbody>
                  </tbody>
              </table>
          </div>
          <p class="null-label"></p>
          <p class="null-label"></p>
          <div class="upload-win-btn">
              <button class="btn" onclick="downLoadTemplate()">下载模板</button>
              <button class="btn select-file-btn" id="selectFileBtn">选择文件</button>
              <!-- <button class="btn import-file-btn" id="importFileBtn">导入</button> -->
          </div>
          <p class="null-label"></p>
          <!-- 选项 -->
          
          
          <p class="upload-title">解析日志</p>
          <div class="analytical-information" id="analyticalInfo"></div>
          <p class="null-label"></p>
          <div class="close-upload-win-btn">
              <button class="btn close-import-win" onclick="closeImportWin()">关闭</button>
          </div>
      </div>
  </body>
</html>
