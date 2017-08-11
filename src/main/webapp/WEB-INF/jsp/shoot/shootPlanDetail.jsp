<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <script type="text/javascript">
    var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];
    
    
        $(document).ready(function () {
        
            //页面tab初始化
            $("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%' });
            //表单
            $("#register").jqxExpander({theme:theme, toggleMode: 'none', width: '500px', showArrow: false });
            
            var groupSource =
            [
                <c:forEach items="${groupList}" var="group">
                {text:"${group.groupName}",value:"${group.groupId}"},
                </c:forEach>
                {text:"新增组",value:"99"}
            ];
            
            
            $("#planNameInput").jqxInput({
                theme: theme,
                width: '180px',
                height: 25,
                placeHolder: "请输入拍摄计划名称"
            })
            
            $("#planStartTime").jqxDateTimeInput({
                value: "${shootPlanInfo.startDate }", 
                width: '180px', 
                height: '25px',
                culture: 'ch-CN',
                formatString: "yyyy-MM-dd" 
            });
            
            $("#planEndTime").jqxDateTimeInput({
                value: "${shootPlanInfo.endDate }", 
                width: '180px', 
                height: '25px',
                culture: 'ch-CN',
                formatString: "yyyy-MM-dd"
            });
            
            $("#planCancelButton").jqxButton({
                theme:theme, 
                width: 80, 
                height: 25 
            });
            
            $("#savePlanButton").jqxButton({
                theme:theme, 
                width: 80, 
                height: 25
            });
            
            $("#planGroup").jqxDropDownList({
                theme:theme,
                source: groupSource, 
                displayMember: "text", 
                valueMember: "value", 
                width: '180px', 
                height: 25,
                autoDropDownHeight: true
            });
            var groupId = "${shootPlanInfo.groupId }";
            $("#planGroup").jqxDropDownList('selectItem',valueMember=groupId);
            
            $("#planGroup").on('change',function(event){
                var args = event.args;
                if (args) {
                    var index = args.index;
                    var item = args.item;
                    
                    if(item.value=="99"){
                        
                        if(index>25){
                            alert("目前最多选择到Z组");
                            $("#planGroup").jqxDropDownList("selectIndex",index-1);
                            return;
                        }
                        $.ajax({
                            url:"/shootGroupManager/saveGroup",
                            type:"post",
                            dataType:"json",
                            data:{groupName:groupArray[index-1].text},
                            success:function(data){
                                if(data.status!="0"){
                                    alert(data.message);
                                    $("#planGroup").jqxDropDownList("selectIndex",0);
                                }
                                $("#planGroup").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                                $("#planGroup").jqxDropDownList("selectIndex",index);
                            }
                        });
                    }
                }
            });
            
            $('#planForm').jqxValidator({
                animationDuration: 1,
                rules: [{input: '#planNameInput', message: '计划名称不可为空!', action: 'keyup,blur', rule: 'required' }]
            });
            
            $("#savePlanButton").on("click", function () {
                $("#planStartTimeInput").val($("#planStartTime").val());
                $("#planEndTimeInput").val($("#planEndTime").val());
                $("#groupIdValue").val($("#planGroup").val());
                
                if ($('#planForm').jqxValidator('validate')) {
                    $.ajax({
                        url: "/shootPlanManager/saveShootPlan",
                        type: 'post',
                        data: $('#planForm').serialize(),
                        dataType: 'json',
                        async: false,
                        success: function (param) {
                            if (param.success) {
                                alert(param.message);
                                window.location.href = "/shootPlanManager/shootPlanList";
                            } else {
                                alert(param.message);
                            }
                        }
                    });
                }
            });
            
            $("#planCancelButton").on('click', function() {
            
              window.location.href = "/shootPlanManager/shootPlanList";
            });
        });
    </script>
    <style type="text/css">
        
    </style>
  </head>
  
  <body>
    <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
    <ul>
        <li style="margin-left: 50px;">拍摄计划</li>
    </ul>
    <div>
        <div id="register" style=" margin-left: 200px;margin-top:50px">
            <div><h3>拍摄计划</h3></div>
            <div>
                <form id="planForm" action="/shootPlanManager/saveShootPlan">
                    <input type = "hidden" name = "planId" value = "${shootPlanInfo.planId }">
                    <table class="register-table">
                        <tr>
                            <td>计划名称:</td>
                            <td><input type="text" name="planName" id="planNameInput" value="${shootPlanInfo.planName }" /></td>
                        </tr>
                        <tr>
                            <td>计划开始时间：</td>
                            <td>
                                <div style='float: left; margin-top: 3px;' id='planStartTime'></div>
                                <input type="hidden" id="planStartTimeInput" name="planStartTime">
                            </td>
                        </tr>
                        <tr>
                            <td>计划结束时间：</td>
                            <td>
                                <div style='float: left; margin-top: 3px;' id='planEndTime'></div>
                                <input type="hidden" id="planEndTimeInput" name="planEndTime">
                            </td>
                        </tr>
                        <tr>
                            <td>分组:</td>
                            <td>
                                <div id="planGroup" /></div> 
                                <input type="hidden" name="groupId" id="groupIdValue">
                            </td>
                        </tr>
                    </table>
                </form>
                <div style="bottom:0;left:120px;margin-bottom:10px;">
                    <input type="button" value="确定" id="savePlanButton" /> 
                    <input type="button" value="关闭" id="planCancelButton" />
                </div>
            </div>
        </div>
    </div>
    </div>
  </body>
</html>
