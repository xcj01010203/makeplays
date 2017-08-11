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
	<meta http-equiv="description" content="This is join a crew page">
	
	<link rel="stylesheet" href="<%=basePath%>/css/joinCrew.css" type="text/css" />
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    
    <script>
        var ubCreateCrewNum = 0;
        $(document).ready(function() {
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
                    
                    ubCreateCrewNum = response.ubCreateCrewNum;
                    $("#createCrewBtn").val("现在新建一个剧组（可建"+ ubCreateCrewNum +"个）");
                }
            });
        });
    
        function toSelectCrewPage() {
            window.location.href = "/crewManager/toSelectCrewPage";
        }
        function toCreateCrewPage() {
            if (ubCreateCrewNum == 0) {
                parent.showErrorMessage("您当前无可用建组次数");
                return false;
            }
        
            window.location.href = "/crewManager/toCreateCrewPage";
        }
    </script>
  </head>
  
  <body>
	  <div style="width: 100%; height: 100%; overflow: auto;">
	    <div class="join-crew">
	        <div class="join-title">请选择一种操作方式</div>
		    <div class="join-content">
		        <div class="join-sub-content">
		            <div class="join-btn"><input type="button" value="现在加入一个剧组" onclick="toSelectCrewPage()"></div>
		            <!-- <div class="join-img" onclick="toSelectCrewPage()"><img alt="select" src="images/selectCrew.png"></div> -->
		        </div>
		        <div class="join-sub-content">
		            <div class="join-btn"><input id="createCrewBtn" type="button" value="现在新建一个剧组（可建0个）" onclick="toCreateCrewPage()"></div>
		            <!-- <div class="join-img" onclick="toCreateCrewPage()"><img alt="select" src="images/createCrew.png"></div> -->
		        </div>
		    </div>
	    </div>
	  </div>
  </body>
</html>
