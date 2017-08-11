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
        $(document).ready(function () {
        
            //页面tab初始化
            topbarInnerText("拍摄管理&&拍摄日志");
			//表单
			$("#register").jqxExpander({theme:theme, toggleMode: 'none', width: '500px', showArrow: false });
			$('#sendButton').jqxButton({theme:theme, width: 60, height: 25 });
			$("#backButton").jqxButton({theme: theme, width:60, height:25});
			$('.text-input').jqxInput({theme:theme, width: 300 });
			
			//分组下拉框
			var source =
            {
                datatype: "json",
                //root:'resultList',
                url:'<%=basePath%>/shootGroupManager/groupListJson',
                datafields: [
                    { name: 'groupId',type: 'string' },
                    { name: 'groupName',type: 'string' },
                ]
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
			$("#groupId").jqxDropDownList({
			     source: dataAdapter, 
                 displayMember: "groupName",
                 valueMember: "groupId",
                 selectedIndex: 0,
                 width:300,
                 height:25
            });
            
            var groupId = "${shootLogModel.groupId }";
            $("#groupId").on('bindingComplete', function (event) {
                $("#groupId").jqxDropDownList('selectItem',valueMember=groupId);
            });
            
            //日期控件
            $("#jqxDateTimeInput").jqxInput({
                width: '300px', 
                height: '25px',
                theme: theme
            });
            var shootLogTime = "${shootLogModel.shootLogTime }";
            if (shootLogTime != '' && shootLogTime != null && shootLogTime != 'undefined') {
                $("#jqxDateTimeInput").val(new Date(shootLogTime).Format("yyyy-MM-dd"));
            } else {
                $("#jqxDateTimeInput").val(new Date().Format("yyyy-MM-dd"));
            }
            
            //表单校验器
            $('#form').jqxValidator({
		        hintType: 'label',
		        animationDuration: 1,
		        rules: [
		            { input: '#shootLocation', message: '拍摄地点不可为空', action: 'blur', rule: 'required' },
		            { input: '#shootRole', message: '出场角色不可为空', action: 'blur', rule: 'required' },
		            { input: '#shootLogInfo', message: '内容不可为空', action: 'blur', rule: 'required' },
		        ]
		    });
            
            
            $('#sendButton').on("click",function(){
                $('#groupIdValue').val($("#groupId").val());
		        if($('#form').jqxValidator('validate')){
		            $.ajax({
		              type: 'post',
		              url: '/shootLogManager/saveShootLog',
		              data: $('#form').serialize(),
		              dataType: 'json',
		              success: function (param) {
		                  if (param.success) {
		                      showSuccessMessage(param.message);
		                      window.location.href = "/shootLogManager/shootLogList";
		                  } else {
		                      showErrorMessage(param.message);
		                  }
		              }
		            });
		        }
		    });
        });
    </script>
    <style type="text/css">
        
    </style>
  </head>
  
  <body>
    <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
    <div>
        <div id="register" style=" margin-left: 200px;margin-top:50px">
            <div><h3>拍摄日志</h3></div>
            <div>
                <form id="form" action="/shootLogManager/saveShootLog" method="post">
                    <input type="hidden" name="shootLogId" value="${shootLogModel.shootLogId}"/>
                    <table class="register-table" style="margin-left: 40px;font-family:微软雅黑;font-weight: bold;">
                        <tr>
                            <td>时间:</td>
                            <td>
                                <input style='float: left; margin-top: 3px;' type="text" id='jqxDateTimeInput' name="shootLogTime" onFocus="WdatePicker({isShowClear:false,readOnly:true})">
                             </td>
                        </tr>
                        <tr>
                            <td>分组:</td>
                            <td>
                                <div id="groupId" /></div>
                                <input type="hidden" name="groupId" id="groupIdValue">
                            </td>
                        </tr>
                        <tr height="59px;">
                            <td>拍摄地点:</td>
                            <td><input type="text" name="shootLocation" id="shootLocation" class="text-input"  value="${shootLogModel.shootLocation}"/></td>
                        </tr>
                        <tr height="59px;">
                            <td>出场角色:</td>
                            <td><input type="text" name="shootRole" id="shootRole" class="text-input" value="${shootLogModel.shootRole}"/></td>
                        </tr>
                        <tr height="231px;">
                            <td>内容:</td>
                            <td><textarea id="shootLogInfo" name="shootLogInfo" rows="" cols="" style=" width:300px; height:200px; border:2px solid #ff8432;background:#D8FFD5;">${shootLogModel.shootLogInfo}</textarea></td>
                        </tr>
                        <tr>
                            <td colspan="2" style="text-align:center">
	                            <input type="button" value="保存" id="sendButton" />
	                            <input type="button" value="返回" style="margin-left: 70px;" id="backButton" onclick="window.location.href='/shootLogManager/shootLogList'" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </div>
    </div>
  </body>
</html>
