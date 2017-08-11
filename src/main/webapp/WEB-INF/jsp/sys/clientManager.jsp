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
	       
		    //页面tab初始化
			$("#client-spliter").jqxSplitter({
				theme:theme,
		        width: '100%',
		        height: '100%',
		        showSplitBar: false,
		        resizable: false,
		        panels: [
		           { size: "100%", min: "70%", collapsible: false },
		           {min: 380 ,collapsed :true}]
		    });
		    
			$('#clientRight').jqxSplitter({
		        orientation: 'horizontal', 
		        width: '100%', 
		        height: "100%",
		        resizable: false,
		        showSplitBar: false,
		        panels: [
		            {size: 40, collapsible: false}, 
		            {size:'100%', collapsible: false}
		        ]
		    });
			
			$(':button').jqxButton({theme:theme, width: 60, height: 25 });
			$('.text-input').jqxInput({theme:theme,  });
	       
	       //表格
	       var source = {
	            datatype: "json",
                root:'result',
                url:'<%=basePath%>/clientManager/getClientLsit',
                datafields: [
                    { name: 'versionId',type: 'string' },
                    { name: 'userId',type: 'string'},
                    { name: 'versionNo',type: 'string' },
                    { name: 'updateTime',type: 'date' },
                    { name: 'url',type: 'string' },
                    { name: 'updateLog',type: 'string' },
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
           
           var dataAdapter = new $.jqx.dataAdapter(source);
           
           loadgrid(dataAdapter);
           
           $("#btncancle").on("click",function(e){//关闭
       		$("#client-spliter").jqxSplitter('collapse');
       	   });
           
           $("#btnsure").on("click",function(){
        	   if($('#clientForm').jqxValidator('validate')){
        		   $('#clientForm').attr("action",ctx+"/clientManager/addClientVersion").submit();
        	   }
           });
           
           $('#clientForm').jqxValidator({
       	     hintType: 'label',
       	     animationDuration: 1,
	       	 rules: [
		       	        { 
		       	        	input: '#versionNo', message: '版本号不可为空!', action: 'keyup, blur',  rule: function(input, commit){
		                   	if($.trim(input.val()).length==0){
		                    		return false;
		                    }
		                       return true;
		                   } 
		       	        },
		       	        { input: '#clientFile', message: '文件不可为空!', action: 'keyup, blur', rule: function(input, commit){
		                   	if($.trim(input.val()).length==0){
	                    		return false;
	                         }
	                       return true;
	                       } 
		       	        }
	       	        ]
	       });
           
           $("#versionNo").on("blur",function(){
        	   var version = $(this).val();
        	   if($.trim(version) == ''){
        		  return; 
        	   }
        	   $.ajax({
        		   url: ctx+'/clientManager/validVersionNo',
        		   data:{versionNo:version},
        		   dataType:'json',
        		   type:"post",
        		   success:function(data){
        			   if(data.isSuccess == '1'){
        				   $("#versionNo").val("");
        				   $("#versionId").val('');
   		            	   $("#updateTime").val('');
        				   var client = data.clientModel;
        				   popupPromptBox("提示","当前版本号已经存在，是否要覆盖此版本！",function(){
        					   $("#versionNo").val(client.versionNo);
        					   $("#updateLog").val(client.updateLog);
        					   $("#versionId").val(client.versionId);
        					   $("#updateTime").val(client.updateTime);
        				   });
        				       
        			   }else{
        				   //alert(false)
        			   }
        		   }
        	   });
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
       function loadgrid(dataAdapter){
    	   var rendergridrows = function (params) {
               //调用json返回的列表数据
               return params.data;
          };
	       
	       $('#client-list').jqxGrid({
               theme:theme,
               width: '100%',
               source: dataAdapter,
               selectionmode: 'multiplerowsextended',
               enabletooltips: true,
               altrows: true,
               pageable: true,
               virtualmode :true,
               autoheight: true,
               columnsresize: true,
               showtoolbar: true,
               pagesize: pageSize,
               pagerbuttonscount: 5,
               rowsheight: 30,
               rendergridrows:rendergridrows,
               localization:localizationobj,//表格文字设置
               rendertoolbar: function (toolbar) {
		            
		            var container = $("<div style='margin: 5px;'></div>");
		            var button = $("<input type='button' style='margin-left: 10px;'  id='addBtn'/>");
		            container.append(button);
		            button.attr("class","");
		            button.addClass("addBtn");
		            /* container.append(print_btn); */
		            
		            toolbar.append(container);
		            button.jqxTooltip({ content: '新增版本', position: 'bottom', autoHide: true, name: 'movieTooltip'});
		            
		            
		            $("#addBtn").on('click',function(){
		            	$('#clientForm').jqxValidator('hide');
		            	$("#versionId").val('');
		            	$("#updateTime").val('');
		            	$(".right-top-name").html("添&nbsp;&nbsp;加");
		            	$("#client-spliter").jqxSplitter('expand');
		            });
		            
		        },
               columns: [
                 { text: '版本号', datafield: 'versionNo', width: '20%',align: 'center',cellsAlign: "center" },
                 { text: '更新时间', datafield: 'updateTime', width: '20%',align: 'center',cellsAlign: "center",cellsformat: 'yyyy-MM-dd'},
                 { text: '更新日志', datafield: 'updateLog', width: '60%',align: 'center',cellsAlign: "center"},
                 
               ]
	       });
	       $("#client-list").bind("pagechanged", function (event) {
              //翻页时的事件绑定
          });
       }
       function updateClient(){
    	   
       }
	</script>
	<style type="text/css">
	  .addBtn{width:24px; height:24px;background: url("../images/add.png") no-repeat; background-size: 24px 24px;/* float: right; margin-right: 15px; background-size: 16px 16px; */}
      .addBtn:hover{background:url(../images/add_hover.png) no-repeat; background-size: 24px 24px;}
	  .con_right_title{border-bottom: 1px solid #e4e7f2;background-color:#e7effa;}
      .con_right_title span{display: inline-block; line-height: 30px;border-bottom: 4px solid #ec8e0c;margin-left:20px;font-family:'黑体';font-size:16px;
            *zoom: 1;font-weight:normal;}
      .con_right_title #operateBtn{display: inline-block; line-height: 30px;margin-left:76px;*zoom: 1;padding-top:5px;}
      #clientRight{border-left: 1px solid #ccc;}
      .right-body-style table{width: 100%;margin-left: 20px;margin-top: 20px;}
      .right-body-style table th{text-align: right;}
      .right-body-style table tr{height: 55px;}
      .text-input{}
      .right-body-style table textarea{width: 242px;height: 80px;border: 1px solid rgb(169, 169, 169);}
	</style>
  </head>
  
  <body>
    <!--切割页面  -->
    <div id="client-spliter">
      <!--页面左侧  -->
      <div id="clientLeft">
      	<div id="client-list"></div>
      </div>
      <!--页面右侧（默认隐藏）  -->
      <div>
      	<div id="clientRight">
      	  <div id="right-top" class="con_right_title">
      	    <span style="" class="right-top-name">修&nbsp;&nbsp;改</span>
            <div id="operateBtn" style="margin-left: 76px;">
                <input type="button" id="btnsure" value="确&nbsp;定" /> 
                  <!--  <input type="button" id="btndelete" value="删&nbsp;除"/>&nbsp;&nbsp;&nbsp; -->
                <input type="button" id="btncancle" style="margin-left: 50px;" value="关&nbsp;闭" />
            </div>
      	  </div>
      	  <div>
      	  	<div id="right-body" class="right-body-style">
      	  	  <form action="" method="post" enctype="multipart/form-data" id="clientForm">
      	  	    <input type="hidden" name="versionId" id="versionId"/>
      	  	    <input type="hidden" name="updateTime" id="updateTime"/>
      	  	    <table>
      	  	      <tr>
      	  	        <th><label for="versionNo">版本号：</label></th>
      	  	        <td><input type="text" name="versionNo" id="versionNo" class="text-input"/></td>
      	  	      </tr>
      	  	      <tr>
      	  	        <th><label for="clientFile">文件：</label></th>
      	  	        <td><input type="file" name="clientFile" id="clientFile"/></td>
      	  	      </tr>
      	  	      <tr>
      	  	        <th><label for="updateLog">更新日志：</label></th>
      	  	        <td><textarea id="updateLog" name="updateLog"></textarea></td>
      	  	      </tr>
      	  	    </table>
      	  	  </form>
      	  	</div>
      	  </div>
      	</div>
      </div>
    </div>
  </body>
</html>
