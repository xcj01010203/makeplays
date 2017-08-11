<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
java.util.Date currentTime = new java.util.Date();//得到当前系统时间
String today = formatter.format(currentTime); //将日期时间格式化
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is create a crew page">

    <link rel="stylesheet" href="<%=path%>/css/crewDetailForAdmin.css" type="text/css" />
    <link rel="stylesheet" href="<%=path%>/css/timePicker.css">    
  	<link rel="stylesheet" href="<%=path%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
    
    <script type="text/javascript" src="<%=path%>/js/scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<%=path%>/js/UI-Checkbox-master/checkbox.min.js"></script>
    <script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=path%>/js/crew/crewDetailForAdmin.js"></script>
    <script type="text/javascript">
    var crewId = "${crewId}";
    </script>
  </head>
  
  <body>
      <div style="width: 100%; height: 100%; overflow: hidden;">
        <div class="create-crew">
            <ul>
                <li class="error-message-li">
                    <label id="errorMessage"></label> 
                </li>
                <li>
                    <p>剧组名称：</p>
                    <input type="text" id="crewName" autofocus>
                    <label class="necessory">*</label>
                    <label class="error-message" id="crewNameErrorMsg"></label>
                </li>
                <li>
                    <p>剧组类型：</p>
                    <select id="crewType" class="half">
                        <option value="1" selected>电视剧</option>
                        <option value="0">电影</option>
                        <option value="2">网剧</option>
                        <option value="3">网大</option>
                    </select>
                    <label class="necessory">*</label>
                    <p>项目类型：</p>
                    <select id="projectType" class="half"><!--  onchange="showAllowExport(this)" -->
                        <option value="0" selected>普通项目</option>
                        <option value="1">试用项目</option>
                        <option value="2">内部项目</option>
                    </select>
                    <label class="necessory">*</label>
                    <div class="ui toggle checkbox" id="isallowexportdiv" style="display: none;">
                      	<input type="checkbox" checked name="exportchk" id="exportchk">
                      	<label id="byPage">可导出</label>
                    </div>
                </li>
                <li>
                    <p>题材：</p>
                    <select id="subject">
                        <option value=""></option>
                    </select>
                </li>
                <li>
                    <p>备案号：</p>
                    <input type="text" id="recordNumber">
                </li>
                <li>
                    <p>账号开始时间：</p>
                    <input type="text" id="startDate" value='<%=today %>' onFocus="WdatePicker({readOnly:true, maxDate: '#F{$dp.$D(\'endDate\')}'})">
                    <label class="necessory">*</label>
                </li>
                <li>
                    <p>账号结束时间：</p>
                    <input type="text" id="endDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'startDate\')}'})">
                    <label class="necessory">*</label>
                </li>
                <li>
                    <p>开机时间：</p>
                    <input type="text" id="shootStartDate" onFocus="WdatePicker({readOnly:true, maxDate: '#F{$dp.$D(\'shootEndDate\')}'})">
                    <label class="error-message"></label>
                </li>
                <li>
                    <p>杀青时间：</p>
                    <input type="text" id="shootEndDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'shootStartDate\')}'})">
                    <label class="error-message"></label>
                </li>
                <li>
                    <p>制片公司：</p>
                    <input type="text" id="company">
                </li>
                <li>
                    <p>导演：</p>
                    <input type="text" id="director">
                </li>
                <li>
                    <p>编剧：</p>
                    <input type="text" id="scriptWriter">
                </li>
                <li>
                    <p>主演：</p>
                    <input type="text" id="mainactor">
                </li>
                <li>
                    <p>入组密码：</p>
                    <input type="text" id="enterPassword" placeHolder="仅支持六位数字">
                    <label class="necessory">*</label>
                    <label class="error-message" id="enterPassMsg"></label>
                    <label class="descrip">入组密码为其他人进入剧组的重要凭证</label>
                </li>
                <li class="create-btn-li">
                    <input class="create-btn" id="modifyBtn" type="button" value="保存并关闭" onclick="modifyCrew()">
                </li>
                <li class="success-msg-li">
                    <label class="success-message" id="successMessage"></label>
                </li>
            </ul>
        </div>
      </div>
  </body>
</html>
