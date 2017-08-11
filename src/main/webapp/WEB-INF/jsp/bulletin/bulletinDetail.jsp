<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
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
        
            topbarInnerText("拍摄管理&&系统公告");
            
            //表单
            $("#register").jqxExpander({theme:theme, toggleMode: 'none', width: '500px', showArrow: false });
            $('#saveButton').jqxButton({theme:theme, width: 60, height: 25 });
            $('#publishButton').jqxButton({theme:theme, width: 60, height: 25 });
            $(':button').jqxButton({theme:theme, width: 60, height: 25 });
            
            //公告名称
            $('#bulletinName').jqxInput({
               theme: theme, 
               width: 300,
               height: 25
            });
            
            //时间控件
            var startDate = "${bulletinInfo.startDate }";
            var endDate = "${bulletinInfo.endDate }";
            //有效开始时间
            $('#startDate').jqxInput({
                width: '143px', 
                height: '25px',
                theme: theme
            });
            //有效结束时间
            $('#endDate').jqxInput({
                width: '143px', 
                height: '25px',
                theme: theme
            });
            if (startDate != "" && startDate != null && startDate != 'undefined') {
                $('#startDate').val(new Date(startDate).Format("yyyy-MM-dd"));
            } else {
                $('#startDate').val(new Date().Format("yyyy-MM-dd"));
            }
            
            if (endDate != "" && endDate != null && endDate != 'undefined') {
                $('#endDate').val(new Date(endDate).Format("yyyy-MM-dd"));
            } else {
                $('#endDate').val(new Date().Format("yyyy-MM-dd"));
            }
            
            //表单提交
            //表单校验器
            $('#form').jqxValidator({
                hintType: 'label',
                animationDuration: 1,
                rules: [
                    { input: '#bulletinName', message: '公告名称不可为空', action: 'blur', rule: 'required' },
                    { input: '#shootLogInfo', message: '公告内容不可为空', action: 'blur', rule: 'required' },
                ]
            });
            
            $('#saveButton').on("click",function(){
                $('#operateFlag').val("1");
                if($('#form').jqxValidator('validate')){
                    $('#form').submit();
                }
            });
            
            $('#publishButton').on("click",function(){
                $('#operateFlag').val("2");
                if($('#form').jqxValidator('validate')){
                    $('#form').submit();
                }
            });
            
            $('#viewPublish').on("click",function(){
	            $.ajax({
	              type: 'post',
	              url: '/bulletinInfoManager/publishBulletin',
	              data: "bulletinId=${bulletinInfo.bulletinId}",
	              dataType: 'json',
	              success: function (param) {
	                  if (param.success) {
	                      showSuccessMessage(param.message);
	                      window.location.href = "/bulletinInfoManager/bulletinManager";
	                  } else {
	                      showErrorMessage(param.message);
	                  }
	              }
	            });
            });
        });
    </script>
  </head>
  
  <body>
    <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
    <div>
        <div id="register" style=" margin-left: 200px;margin-top:50px">
            <div><h3>系统公告</h3></div>
            <div>
                <form id="form" action="/bulletinInfoManager/addBulletinInfo" enctype="multipart/form-data" method="post">
                    <input type="hidden" name="bulletinId" value="${bulletinInfo.bulletinId}"/>
                    <input type="hidden" id="operateFlag" name="operateFlag" value="">
                    <table class="register-table">
                        <tr>
                            <td>公告名称:</td>
                            <td>
                                <input type="text" name="bulletinName" id="bulletinName"  value="${bulletinInfo.bulletinName}"/>
                             </td>
                        </tr>
                        <tr>
                            <td>有效时间:</td>
                            <td>
                                <input type="text" id='startDate' name="startDate" style='float: left; margin-top: 3px;' onFocus="WdatePicker({isShowClear:false,readOnly:true})">
                                <div style='float: left; margin-top: 10px;'>至</div>
                                <input type="text" name="endDate" id='endDate' style='float: left; margin-top: 3px;' onFocus="WdatePicker({isShowClear:false,readOnly:true})">
                            </td>
                        </tr>
                        <tr>
                            <td>附件:</td>
                            <td><input type="file" name="bulletinfile"></td>
                        </tr>
                        
                        <tr>
                            <td>内容:</td>
                            <td><textarea id="shootLogInfo" name="content" rows="" cols="" style=" width:300px; height:200px; border:2px solid #ff8432;background:#D8FFD5;">${bulletinInfo.content}</textarea></td>
                        </tr>
                        <tr>
                            <td colspan="2" style="text-align: center;">
	                            <input type="button" value="保存" id="saveButton" />
	                            <input type="button" value="发布" id="publishButton" />
	                            <input type="button" value="返回" onclick="window.location.href='/bulletinInfoManager/bulletinManager'" />
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
