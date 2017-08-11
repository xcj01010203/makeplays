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
    <meta http-equiv="description" content="This is create a crew page">

    <link rel="stylesheet" href="<%=basePath%>/css/crewDetail.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/timePicker.css">
    
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
    <script>
        var crewNameValid = true;
        var enterPasswordValid = true;
        var crewId = "${crewId}";
        
        $(document).ready(function() {
        
            //获取系统题材数据
            $.ajax({
                url: "/crewManager/queryAllSubject",
                type: "post",
                dataType: "json",
                async: false,
                success: function(response) {
                    if (response.success) {
                        var subjectList = response.subjectList;
                        for (var i = 0; i < subjectList.length; i++) {
                            var subject = subjectList[i];
                            $("#subject").append("<option value='" + subject.subjectName + "'>" + subject.subjectName + "</option>");
                        }
                    } else {
                        $("#errorMessage").text(response.message);
                    }
                }
            });
            
            //剧组名称blur事件，判断非空
            $("#crewName").on("blur", function() {
                if ($(this).val() == "") {
                    $("#crewNameErrorMsg").text("请填写剧组名称");
                    crewNameValid = false;
                    return false;
                }
            });
            $("#crewName").on("focus", function() {
                $("#crewNameErrorMsg").text("");
                $("#errorMessage").text("");
            });
            
            //入组密码blur事件，判断格式
            $("#enterPassword").on("blur", function() {
                $(this).siblings(".descrip").hide();
                var value = $(this).val();
                if (value == "") {
                    $("#enterPassMsg").text("请输入入组密码");
                    enterPasswordValid = false;
                    return false;
                }
                
                if (!/^[0-9]{6}$/.test(value)) {
                    $("#enterPassMsg").text("仅支持6位数字");
                    enterPasswordValid = false;
                    return false;
                }
            });
            $("#enterPassword").on("focus", function() {
                $(this).siblings(".descrip").show();
                $("#enterPassMsg").text("");
                $("#errorMessage").text("");
            });
            
            
            loadCrewInfo();
        });
        
        function toJoinCrewPage () {
            window.location.href = "/crewManager/toJoinCrewPage";
        }
        function closeWindow() {
            parent.closeCrewDetailWindow();
        }
        function modifyCrew() {
            if (!crewNameValid || !enterPasswordValid) {
                return false;
            }
            
            var crewName = $("#crewName").val();
            var crewType = $("#crewType").val();
            var subject = $("#subject").val();
            var recordNumber = $("#recordNumber").val();
            var shootStartDate = $("#shootStartDate").val();
            var shootEndDate = $("#shootEndDate").val();
            var company = $("#company").val();
            var director = $("#director").val();
            var scriptWriter = $("#scriptWriter").val();
            var mainactor = $("#mainactor").val();
            var enterPassword = $("#enterPassword").val();
            
            var param = {};
            param.crewId = crewId;
            param.crewName = crewName;
            param.crewType = crewType;
            param.subject = subject;
            param.recordNumber = recordNumber;
            param.shootStartDate = shootStartDate;
            param.shootEndDate = shootEndDate;
            param.company = company;
            param.director = director;
            param.scriptWriter = scriptWriter;
            param.mainactor = mainactor;
            param.enterPassword = enterPassword;
            
            $.ajax({
                url: "/crewManager/saveCrewByNormalUser",
                type: "post",
                data: param,
                dataType: "json",
                success: function(response) {
                    if (response.success) {
                        $("#modifyBtn").hide();
                        closeWindow();
                        
                    } else {
                        $("#errorMessage").text(response.message);
                    }
                }
            });
        }
        
        //加载剧组信息
        function loadCrewInfo () {
            $.ajax({
                url: "/crewManager/queryCurrentCrewInfo",
                type: "post",
                async: true,
                dataType: "json",
                data: {},
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return false;
                    }
                    
                    var crewInfo = response.crewInfo;
                    
                    var crewName = crewInfo.crewName;
                    var company = crewInfo.company;
                    var shootStartDate = crewInfo.shootStartDate;
                    var shootEndDate = crewInfo.shootEndDate;
                    var crewType = crewInfo.crewType;
                    var subject = crewInfo.subject;
                    var recordNumber = crewInfo.recordNumber;
                    var director = crewInfo.director;
                    var scriptWriter = crewInfo.scriptWriter;
                    var mainactor = crewInfo.mainactor;
                    var enterPassword = crewInfo.enterPassword;
                    
                    $("#crewName").val(crewName);
                    $("#company").val(company);
                    $("#shootStartDate").val(shootStartDate);
                    $("#shootEndDate").val(shootEndDate);
                    $("#crewType").val(crewType);
                    
                    $("#subject").val(subject);
                    $("#recordNumber").val(recordNumber);
                    $("#director").val(director);
                    $("#scriptWriter").val(scriptWriter);
                    $("#mainactor").val(mainactor);
                    $("#enterPassword").val(enterPassword);
                    
                }
            });
        }
    </script>
  </head>
  
  <body>
      <div style="width: 100%; height: 100%; overflow: auto;">
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
                    <select id="crewType">
                        <option value="1" selected>电视剧</option>
                        <option value="0">电影</option>
                        <option value="2">网剧</option>
                        <option value="3">网大</option>
                    </select>
                    <label class="necessory">*</label>
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
                    <p>开机时间：</p>
                    <input type="text" id="shootStartDate" onFocus="WdatePicker({readOnly:true, maxDate: '#F{$dp.$D(\'shootEndDate\')}', isShowClear: false})">
                    <label class="error-message"></label>
                </li>
                <li>
                    <p>杀青时间：</p>
                    <input type="text" id="shootEndDate" onFocus="WdatePicker({readOnly:true, minDate: '#F{$dp.$D(\'shootStartDate\')}', isShowClear: false})">
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
