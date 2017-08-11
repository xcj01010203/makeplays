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
	   $(document).ready(function (){
	       var success = "${success }";
	       var message = "${message }";
	       if (success != '' && success != null && success != 'undefined' && !success) {
	           alert(message);
	       }
	       
	       $("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%' });
	       
	       var bulletinStatusMap = new HashMap();
           bulletinStatusMap.put("0","草稿");
           bulletinStatusMap.put("1","已发布");
           bulletinStatusMap.put("2","已废弃");
	       
	       //表格
	       var source = {
	            datatype: "json",
                root:'resultList',
                url:'<%=basePath%>/bulletinInfoManager/bulletinListJsonForAdmin',
                datafields: [
                    { name: 'bulletinId',type: 'string' },
                    { name: 'bulletinName',type: 'date'},
                    { name: 'content',type: 'string' },
                    { name: 'attachUrl',type: 'string' },
                    { name: 'attachName',type: 'string' },
                    { name: 'pubUserName',type: 'int' },
                    { name: 'pubUserId',type: 'string' },
                    { name: 'createTime',type: 'date' },
                    { name: 'startDate',type: 'date' },
                    { name: 'endDate',type: 'date' },
                    { name: 'status',type: 'int' },
                    { name: 'playId',type: 'string' },
                    { name: 'crewName', type: 'string'}
                ],
                type:'post',
                processdata: function (data) {
                    //查询之前可执行的代码
                },
                beforeprocessing:function(data){
                    //查询之后可执行的代码
                    source.totalrecords=data.result.total;
                }
	       };
	       var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                if (rowdata.attachUrl != '' && rowdata.attachUrl != null && rowdata.attachUrl != 'undefined') {
                    /* var html = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"
                        + "<a href='javascript:void(0)' onclick='downloadFile(\""+rowdata.attachUrl+"\",\""+ rowdata.attachName +"\");' >下载附件</a>" 
                        +"</div>"; */
                        
                    var html = "<input type='button' style='width: 55px; height: 20px;' "+
                  " aria-disabled='false' class='jqx-rc-all jqx-rc-all-ui-lightness jqx-button jqx-button-ui-lightness jqx-widget jqx-widget-ui-lightness jqx-fill-state-normal jqx-fill-state-normal-ui-lightness' value='下载附件' onclick='downloadFile(\""+rowdata.attachUrl+"\",\""+ rowdata.attachName +"\");' >";
                    return html;
                }
           };
           var statusColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                //状态列
                var html = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>";
                html += bulletinStatusMap.get(rowdata.status);
                html += "</div>";
                return html;
           };
           //公告名称列渲染
           var namecellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                //var html="<a href='javascript:void(0)' onclick='viewBulletionInfo(\""+rowdata.bulletinId+"\");' style='float: " + columnproperties.cellsalign + ";color: blue;' >" + rowdata.bulletinName + "</a>";
                var html = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"
                + "<a href='javascript:void(0)' onclick='viewBulletionInfo(\""+rowdata.bulletinId+"\");'>" + rowdata.bulletinName + "</a>" 
                +"</div>";
                
                return html;
           };
	       var rendergridrows = function (params) {
                //调用json返回的列表数据
                return params.data;
           };
	       var dataAdapter = new $.jqx.dataAdapter(source);
	       $('#messageList').jqxGrid({
                theme:theme,
                width: '100%',
                source: dataAdapter,
                selectionmode: 'multiplerowsextended',
                altrows: true,
                pageable: true,
                virtualmode :true,
                autoheight: true,
                columnsresize: true,
                showtoolbar: true,
                pagesize: pageSize,
                pagerbuttonscount: 5,
                rendergridrows:rendergridrows,
                localization:localizationobj,//表格文字设置
                columns: [
                  { text: '剧组', datafield: 'crewName', width: 230 },
                  { text: '公告名称', datafield: 'bulletinName', width: 90, cellsrenderer: namecellsrenderer},
                  { text: '公告内容', datafield: 'content'},
                  { text: '有效开始时间', datafield: 'startDate', width: 100, cellsformat: 'yyyy-MM-dd'},
                  { text: '有效结束时间', datafield: 'endDate', width: 100, cellsformat: 'yyyy-MM-dd'},
                  { text: '发布人', datafield: 'pubUserName', width: 130},
                  { text: '发布时间', datafield: 'createTime', width: 100, cellsformat: 'yyyy-MM-dd'},
                  { text: '状态', width: 50, cellsrenderer: statusColumn},
                  { text: '附件名称', datafield: 'attachName', width: 250},
                  { text: '附件'  ,cellsrenderer:cellsrenderer, width: 100 }
                ]
	       });
	       $("#messageList").bind("pagechanged", function (event) {
               //翻页时的事件绑定
           });
	   });
	   //下载附件
       function downloadFile(storePath, attachName) {
            window.location.href = "/bulletinInfoManager/downloadFile?storePath=" + storePath + "&fileName=" + attachName;
       }
       //查看公告
       function viewBulletionInfo(bulletinId) {
            window.location.href = "/bulletinInfoManager/bulletinViewPage?bulletinId=" + bulletinId;
       }
	</script>

  </head>
  
  <body>
    <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
        <ul>
            <li style="margin-left: 30px;">消息列表</li>
        </ul>
        <div>
            <div id="messageList"></div>
        </div>
    </div>
  </body>
</html>
