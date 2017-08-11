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

    <link rel="stylesheet" href="<%=basePath%>/css/joinCrew.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/My97DatePicker/WdatePicker.js"></script>
    
    <script>
        var crewNameValid = false;
        var enterPasswordValid = false;
    
        $(document).ready(function() {
            //当前可用建组次数
            $.ajax({
                url: "/userManager/queryCurrUserUbCreateCrewNum",
                type: "post",
                dataType: "json",
                data: {},
                async: true,
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return false;
                    }
                    
                    var ubCreateCrewNum = response.ubCreateCrewNum;
                    if (ubCreateCrewNum == 0) {
                        alert("您当前无建组机会，请联系客服");
                        toJoinCrewPage();
                    }
                }
            });
        
            
            //获取系统题材数据
            $.ajax({
                url: "/crewManager/queryAllSubject",
                type: "post",
                dataType: "json",
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
                    return false;
                }
                crewNameValid = true;
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
                    return false;
                }
                
                if (!/^[0-9]{6}$/.test(value)) {
                    $("#enterPassMsg").text("仅支持6位数字");
                    return false;
                }
                enterPasswordValid = true;
            });
            $("#enterPassword").on("focus", function() {
                $(this).siblings(".descrip").show();
                $("#enterPassMsg").text("");
                $("#errorMessage").text("");
            });
            
            //创建剧组
            
        });
        
        function toJoinCrewPage () {
            window.location.href = "/crewManager/toJoinCrewPage";
        }
        function toIndexPage() {
            parent.window.location.href = "/userManager/toUserCenterPage";
        }
        function createCrew (own) {
            own = $(own);
        
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
            
            own.attr("disabled", "disabled");
            $.ajax({
                url: "/crewManager/saveCrewByNormalUser",
                type: "post",
                data: param,
                dataType: "json",
                success: function(response) {
                    if (response.success) {
                        $("#createBtn").hide();
                        $("#successMessage").text("新建成功，3秒后自动跳转到登录页");
                        
                        var totalSecond = 3;
                        var int = setInterval(function(event) {
                            totalSecond --;
                            $("#successMessage").text("新建成功，"+ totalSecond +"秒后自动返回");
                            
                            if (totalSecond == 0) {
                                window.clearInterval(int);
                                toIndexPage();
                            }
                        }, 1000);
                    } else {
                        $("#errorMessage").text(response.message);
                    }
                }
            });
        }
    </script>
  </head>
  
  <body>
	  <div style="width: 100%; height: 100%; overflow: auto;">
	    <div class="create-crew">
	        <ul>
	            <li class="create-title-li">
	                <label class="create-crew-title">创建一个属于自己的剧组</label>
	                <a href="javascript:(0)" onclick="toJoinCrewPage()">&lt;&lt;返回</a>
	            </li>
	            <li class="create-title-li">
	                <label class="create-crew-tips">注：其它信息请在剧组设置里设置</label>
	            </li>
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
	                <input class="create-btn" id="createBtn" type="button" value="立即创建" onclick="createCrew(this)">
	            </li>
	            <li class="success-msg-li">
	                <label class="success-message" id="successMessage"></label>
	            </li>
	        </ul>
	    </div>
	  </div>
  </body>
</html>
