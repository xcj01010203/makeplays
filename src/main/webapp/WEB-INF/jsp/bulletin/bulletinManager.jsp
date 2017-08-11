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
               showErrorMessage(message);
           }
           
           //加载导航栏数据
           topbarInnerText("生产管理&&系统公告");
           
           var bulletinStatusMap = new HashMap();
		   bulletinStatusMap.put("0","草稿");
		   bulletinStatusMap.put("1","已发布");
           bulletinStatusMap.put("2","已废弃");
           //表格
           var source = {
                datatype: "json",
                root:'resultList',
                url:'<%=basePath%>/bulletinInfoManager/bulletinListJson?operateType=2',
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
                    { name: 'playId',type: 'string' }
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
           
           //公告名称列渲染
           var namecellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                var html = "<a style='line-height:25px;' class='linkText' href='javascript:void(0)' id='buttetinName' onclick='viewBulletionInfo(\""+rowdata.bulletinId+"\");'>" + rowdata.bulletinName + "</a>";
                
                return html;
           };
           //操作列渲染
           var operatecellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                var html = "<div style='line-height:25px;margin-left:5px;'>";
                if (rowdata.status == 0) {
                    html +="<a class='linkText' style='margin-right:10px;' onclick='updateBulletinInfo(\""+rowdata.bulletinId+"\");'>修改</a>";
                    html += "<a class='linkText' style='margin-right:10px;' onclick='publishBulletionInfo(\""+rowdata.bulletinId+"\");'>发布</a>";
                    html += "<a class='linkText' style='margin-right:10px;' onclick='deleteBulletionInfo(\""+rowdata.bulletinId+"\");'>废弃</a>";
                }
                if (rowdata.status == 1) {
                    html += "<a class='linkText' onclick='rebackBulletionInfo(\""+rowdata.bulletinId+"\");'>撤回</a>";
                }
                html += "</div>";
                return html;
           };
           
           //附件列渲染
           var attachcellrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                if (rowdata.attachUrl != '' && rowdata.attachUrl != null && rowdata.attachUrl != 'undefined') {
                    var html = "<a class='linkText' type='button' style='line-height:25px;' id='downloadFile' onclick='downloadFile(\""+rowdata.attachUrl+"\",\""+ rowdata.attachName +"\");' >下载附件</a>";
                    return html;
                }
           };
           var rendergridrows = function (params) {
                //调用json返回的列表数据
                return params.data;
           };
           var statusColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
                //状态列
                var html = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>";
                html += bulletinStatusMap.get(rowdata.status);
                html += "</div>";
                return html;
           };
           var dataAdapter = new $.jqx.dataAdapter(source);
           $('#messageList').jqxGrid({
                theme:theme,
                width: '100%',
                source: dataAdapter,
                altrows: true,
                pageable: true,
                virtualmode :true,
                height: '98%',
                columnsresize: true,
                showtoolbar: true,
                pagesize: pageSize,
                pagerbuttonscount: 5,
                rendergridrows:rendergridrows,
                localization:localizationobj,//表格文字设置
                rendertoolbar: function (toolbar) {
                    var container = $("<div style='margin: 5px;'></div>");
                    var html = "<input type='button' class='add' style='margin-left: 25px;' id='addBulletin'>";
                    toolbar.append(container);
                    container.append(html);
                    
                    
                    $("#addBulletin").jqxTooltip({ content: '发布公告', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#addBulletin").on("click", function() {
                        window.location.href="/bulletinInfoManager/bulletinDetail";
                    });
                    
                },
                columns: [
                  { text: '公告名称', datafield: 'bulletinName', width: 200, cellsrenderer: namecellsrenderer},
                  { text: '公告内容', datafield: 'content', width: 300},
                  { text: '有效开始时间', datafield: 'startDate', width: 100, cellsformat: 'yyyy-MM-dd'},
                  { text: '有效结束时间', datafield: 'endDate', width: 100, cellsformat: 'yyyy-MM-dd'},
                  { text: '发布人', datafield: 'pubUserName', width: 100},
                  { text: '发布时间', datafield: 'createTime', width: 200, cellsformat: 'yyyy-MM-dd HH:mm:ss'} ,
                  { text: '状态', width: 80, cellsrenderer: statusColumn},
                  { text: '附件', width: 100, cellsrenderer: attachcellrenderer},
                  { text: '操作'  ,cellsrenderer: operatecellsrenderer }
                ]
           });
           $("#messageList").bind("pagechanged", function (event) {
               //翻页时的事件绑定
           });
       });
       
       function publishBulletionInfo(bulletinId) {
            $.ajax({
            type: 'post',
            url: '/bulletinInfoManager/publishBulletin',
            data: "bulletinId="+ bulletinId,
            dataType: 'json',
            success: function (param) {
                if (param.success) {
                    showSuccessMessage(param.message);
                    $('#messageList').jqxGrid("updatebounddata", "cells");
                } else {
                    showErrorMessage(param.message);
                }
            }
          });
       }
       
       //修改公告
       function updateBulletinInfo(bulletinId) {
            window.location.href = "/bulletinInfoManager/bulletinDetail?bulletinId=" + bulletinId;
       }
       
       //查看公告
       function viewBulletionInfo(bulletinId) {
            window.location.href = "/bulletinInfoManager/bulletinViewPage?bulletinId=" + bulletinId;
       }
       
       //废弃公告
       function deleteBulletionInfo(bulletinId) {
           popupPromptBox("提示", "是否废弃该公告？", function() {
				$.ajax({
				    type: 'post',
				    data: "bulletinId="+bulletinId,
				    url: '/bulletinInfoManager/deleteBulletinInfo',
				    dataType: 'json',
				    success: function (param) {
				       if (param.success) {
				           showSuccessMessage(param.message);
				           $('#messageList').jqxGrid("updatebounddata", "cells");
				       } else {
				           showErrorMessage(param.message);
				       }
				    }
				});
           });
       }
       
       //撤回公告
       function rebackBulletionInfo(bulletinId) {
            popupPromptBox("提示", "撤回将使公告变回草稿状态，是否继续？", function() {
                $.ajax({
                   type: 'post',
                   data: "bulletinId="+bulletinId,
                   url: '/bulletinInfoManager/rebackBulletionInfo',
                   dataType: 'json',
                   success: function (param) {
                      if (param.success) {
                          showSuccessMessage(param.message);
                          $('#messageList').jqxGrid("updatebounddata", "cells");
                      } else {
                          showErrorMessage(param.message);
                      }
                   }
               });
            });
       }
       
       //下载附件
       function downloadFile(storePath, attachName) {
            window.location.href = "/bulletinInfoManager/downloadFile?storePath=" + storePath + "&fileName=" + attachName;
       }
    </script>
    
    <style>
        .add{width:24px; height:24px; background: url("images/add.png") no-repeat; background-size: 24px 24px;}
        .add:hover{background: url("images/add_hover.png") no-repeat; background-size: 24px 24px;}
        .linkText{text-decoration:none;}
        .linkText:hover{text-decoration:underline; cursor:pointer}
    </style>
  </head>
  
  <body>
        <div id="messageList"></div>
  </body>
</html>
