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
        
            //页面tab初始化
            topbarInnerText("生产管理&&查看系统公告");
            //表单
            $("#register").jqxExpander({theme:theme, toggleMode: 'none', width: '500px', showArrow: false });
            $(':button').jqxButton({theme:theme, width: 60, height: 25 });
            $('#bulletinfile').jqxInput({
               theme: theme, 
               width: 300,
               height: 25,
               disabled: true
            });
            
            //公告名称
            $('#bulletinName').jqxInput({
               theme: theme, 
               width: 300,
               height: 25,
               disabled: true
            });
            
            //时间控件
            var startDate = "${bulletinInfo.startDate }";
            var endDate = "${bulletinInfo.endDate }";
            if (startDate != "" && startDate != null && startDate != 'undefined') {
                //有效开始时间
                $('#startDate').jqxDateTimeInput({
                    value: startDate,
                    formatString: "yyyy-MM-dd",
                    width: '143px', 
                    height: '25px',
                    culture: 'ch-CN',
                    disabled: true
                });
            } else {
                $('#startDate').jqxDateTimeInput({
                    formatString: "yyyy-MM-dd",
                    width: '143px', 
                    height: '25px',
                    culture: 'ch-CN'
                });
            }
            
            if (endDate != "" && endDate != null && endDate != 'undefined') {
                //有效结束时间
                $('#endDate').jqxDateTimeInput({
                    value: endDate,
                    formatString: "yyyy-MM-dd",
                    width: '143px', 
                    height: '25px',
                    culture: 'ch-CN',
                    disabled: true
                });
            } else {
                $('#endDate').jqxDateTimeInput({
                    formatString: "yyyy-MM-dd",
                    width: '143px', 
                    height: '25px',
                    culture: 'ch-CN'
                });
            }
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
                                <div style='float: left; margin-top: 3px;' id='startDate'></div>
                                <input type="hidden" name="startDate" id="startDateValue">
                                <div style='float: left; margin-top: 10px;'>至</div>
                                <div style='float: left; margin-top: 3px;' id='endDate'></div>
                                <input type="hidden" name="endDate" id="endDateValue">
                            </td>
                        </tr>
                        <tr>
                            <td>附件:</td>
                            <td><input type="text" id="bulletinfile" name="bulletinfile" value="${bulletinInfo.attachName}"></td>
                        </tr>
                        <tr>
                            <td>内容:</td>
                            <td><textarea id="shootLogInfo" name="content" rows="" cols="" style=" width:300px; height:200px; border:2px solid #ff8432;background:#D8FFD5;" readonly disabled>${bulletinInfo.content}</textarea></td>
                        </tr>
                        <tr>
                            <td colspan="2" style="text-align: center;">
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
