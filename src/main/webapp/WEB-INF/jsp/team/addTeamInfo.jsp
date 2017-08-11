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
	
    <%-- <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/bootstrap/css/bootstrap.min.css"> --%>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/team/addTeamInfo.css" />
	
    <script type="text/javascript" src="<%=basePath%>/js/team/addTeamInfo.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
    <%-- <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-select.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap.min.js"></script> --%>
	

  </head>
  
  <body>
    <div class="my-container">
        <div class="new-btn-div"><input class="new-team-btn" type="button" value="新建组讯" onclick="createNewTeamInfo()"></div>
        <div class="team-detail-info">
	        <form id="teamInfoForm">
	            <ul>
	                <li>
		                <p>剧组名称：</p>
		                <input type="text" name="crewName" id="crewName">
	                </li>
	                <li>
	                    <p>剧组类型：</p>
		                <select name="crewType">
		                    <option value="0">电影</option>
		                    <option value="1">电视剧</option>
		                    <option value="2">网剧</option>
		                    <option value="3">网大</option>
		                </select>
	                </li>
	                <li>
	                    <p>题材：</p>
	                    <select id="subject" name="subject"></select>
	                </li>
	                <li>
	                    <p>制片公司：</p><input type="text" name="company">
	                </li>
	                <li>
	                    <p>开机时间：</p><input type="text" id="shootStartDate" name="shootStartDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'shootStartDate\')}', isShowClear: false})">
	                </li>
	                <li>
	                    <p>杀青时间：</p><input type="text" id="shootEndDate" name="shootEndDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'shootStartDate\')}', isShowClear: false})">
	                </li>
	                <li>
	                    <p>拍摄地点：</p><input type="text" name="shootLocation">
	                </li>
	                <li>
	                    <p>导演：</p><input type="text" name="director">
	                </li>
	                <li>
	                    <p>编剧：</p><input type="text" name="scriptWriter">
	                </li>
	                <li>
	                    <p>联系人：</p><input type="text" name="contactName">
	                </li>
	                <li>
	                    <p>联系电话：</p><input type="text" name="phoneNum">
	                </li>
	                <li>
	                    <p>联系邮箱：</p><input type="text" name="email">
	                </li>
	                <li>
	                    <p>联系地址：</p><input type="text" name="contactAddress">
	                </li>
	                <!-- <li>
	                    <p>剧照：</p><input type="file" name="file">
	                </li> -->
	                <li>
	                    <p>剧组简介：</p><textarea rows="10" name="crewComment"></textarea>
	                </li>
	                <li class="btn-li">
	                    <input type="button" value="保存" onclick="saveTeamInfo()">
	                </li>
	            </ul>
	        </form>
        </div>
        
        <div class="position-info-remark" id="positionInfoRemark"></div>
        <div class="position-detail-info">
            <form id="positionInfoForm">
	            <input type="hidden" name="teamId" id="teamId">
	            <ul>
	                <li>
	                    <p>职位：</p>
	                    <select id="needPosition" name="needPositionId" onchange="selectPosition(this)"></select>
	                    <input type="hidden" name="positonName" id="positonName" value="制片人">
	                </li>
	                <li>
	                    <p>招聘人数：</p>
	                    <input type="text" name="needPeopleNum" onkeyup="checkNum(this)">
	                </li>
	                <li>
	                    <p>职位要求：</p>
	                    <textarea rows="10" cols="" name="positionRequirement"></textarea>
	                </li>
	                <li class="btn-li">
	                    <input type="button" value="保存" onclick="savePositionInfo()">
	                </li>
	            </ul>
            </form>
        </div>
    </div>
  </body>
</html>
