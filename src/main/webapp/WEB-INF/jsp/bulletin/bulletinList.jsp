<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object hasCrewSetAuth = false; //是否有剧组设置权限
Object obj = session.getAttribute("userAuthMap");
if(obj!=null){
	java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CREW_SETTING) != null){
		hasCrewSetAuth = true;
	}
}
request.setAttribute("hasCrewSetAuth", hasCrewSetAuth);
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
	       topbarInnerText("拍摄管理&&消息中心");
	       
	       //系统公告
	       var bulletinsource = {
	            datatype: "json",
                root:'resultList',
                url:'<%=basePath%>/bulletinInfoManager/bulletinListJson?operateType=1',
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
                    bulletinsource.totalrecords=data.result.total;
                }
	       };
	       
	       bulletingrid(bulletinsource);
	       
	       //消息
	       var messagesource = {
	            datatype: "json",
                root:'resultList',
                url:'<%=basePath%>/bulletinInfoManager/messageListJson',
                datafields: [
                    { name: 'messageId',type: 'string' },
                    { name: 'senderName',type: 'string'},
                    { name: 'content',type: 'string' },
                    { name: 'status',type: 'string' },
                    { name: 'crewId',type: 'string' },
                    { name: 'createTime',type: 'string' },
                    { name: 'dealerName',type: 'string' },
                    { name: 'senderId',type: 'string' }
                ],
                type:'post',
                processdata: function (data) {
                    //查询之前可执行的代码
                },
                beforeprocessing:function(data){
                    //查询之后可执行的代码
                    messagesource.totalrecords=data.result.total;
                }
	       };
	       
	       $("#tab_1").click(function(){
	   		$(this).addClass("tab_li_current");
	   		$(this).siblings().removeClass("tab_li_current");
	   		$(".danju").hide();
	   		$(".danju_1").show();
	   	});
	   		
	   	$("#tab_2").click(function(){
	   		$(this).addClass("tab_li_current");
	   		$(this).siblings().removeClass("tab_li_current");
	   		$(".danju").hide();
	   		$(".danju_2").show();
	   		messagegrid(messagesource);
	   	});
	   });
	   //下载附件
       function downloadFile(storePath, attachName) {
            window.location.href = "/bulletinInfoManager/downloadFile?storePath=" + storePath + "&fileName=" + attachName;
       }
	   //加载公告表格
	   function bulletingrid(source){
		   var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
               if (rowdata.attachUrl != '' && rowdata.attachUrl != null && rowdata.attachUrl != 'undefined') {
                   var html = "<a class='linkText' type='button' style='line-height:25px;' id='downloadFile' onclick='downloadFile(\""+rowdata.attachUrl+"\",\""+ rowdata.attachName +"\");' >下载附件</a>";
                   return html;
               }
          };
	       var rendergridrows = function (params) {
               //调用json返回的列表数据
               return params.data;
          };
	       var bulletindataAdapter = new $.jqx.dataAdapter(source);
	       $('#bulletingeList').jqxGrid({
               theme:theme,
               width: '100%',
               source: bulletindataAdapter,
               selectionmode: 'multiplerowsextended',
               altrows: true,
               pageable: true,
               virtualmode :true,
               autoheight: true,
               columnsresize: true,
               showtoolbar: false,
               pagesize: pageSize,
               pagerbuttonscount: 5,
               rendergridrows:rendergridrows,
               localization:localizationobj,//表格文字设置
               columns: [
                 { text: '公告名称', datafield: 'bulletinName', width: 230},
                 { text: '公告内容', datafield: 'content', width: 90},
                 { text: '有效开始时间', datafield: 'startDate', width: 350, cellsformat: 'yyyy-MM-dd'},
                 { text: '有效结束时间', datafield: 'endDate', width: 250, cellsformat: 'yyyy-MM-dd'},
                 { text: '发布人', datafield: 'pubUserName', width: 130},
                 { text: '发布时间', datafield: 'createTime', width: 250, cellsformat: 'yyyy-MM-dd'},
                 { text: '附件'  ,cellsrenderer:cellsrenderer }
               ]
	       });
	       $("#messageList").bind("pagechanged", function (event) {
              //翻页时的事件绑定
          });
	   }
	   
	   
	   //加载消息表格
	   function messagegrid(source){
		   /* var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			   		var html = "<div class='font_v_m ft_m'>";
			   		if(rowdata.status == 0){
			   			html += "<a href='javascript:void(0);' userId='"+rowdata.senderId+"' messageId='"+rowdata.messageId+"' onclick='messagesure(this)'>同意</a><a href='javascript:void(0);' messageId='"+rowdata.messageId+"' userId='"+rowdata.senderId+"' style='margin-left:15px;' onclick='messagerefush(this)'>拒绝</a>";
			   		}else if(rowdata.status == 1){
			   			html += "<a href='javascript:void(0);' class='setauth' onclick='setauth(\""+rowdata.senderId+"\")'>去设置权限</a>";
			   		}else{
			   			
			   		}
			   		html += "</div>";
                   	return html;
             } */
             
          var statuscellsrenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
        	  var html = "<div class='font_v_m ft_m'>";
        	  if(value == 0){
        		  html += "未读";
        	  }else if(value == 1){
        		  html += "已读";
        	  }else{
        		  html += "已处理";
        	  }
        	  html += "</div>";
        	  return html;
          }
	       var rendergridrows = function (params) {
               //调用json返回的列表数据
               return params.data;
          };
	       var messagedataAdapter = new $.jqx.dataAdapter(source);
	       $('#messageList').jqxGrid({
               theme:theme,
               width: '100%',
               source: messagedataAdapter,
               /* selectionmode: 'checkbox', */
               altrows: true,
               pageable: true,
               enabletooltips: true,
               virtualmode :true,
               autoheight: true,
               columnsresize: true,
               showtoolbar: false,
               pagesize: pageSize,
               pagerbuttonscount: 5,
               rendergridrows:rendergridrows,
               localization:localizationobj,//表格文字设置
               /* rendertoolbar: function (toolbar) {
		            
		            var container = $("<div style='overflow: hidden; position: relative; height: 100%; width: 100%;margin: 5px;'></div>");
		            var deletemessage = $("<input type='button' style='margin-left:15px;height: 18px; width: 18px;'/>");
		            container.append(deletemessage);
		            toolbar.append(container);
		            deletemessage.attr("class","");
		            deletemessage.addClass("button_delete");
		            deletemessage.jqxTooltip({ content: '删除选中消息', position: 'bottom', autoHide: true, name: 'movieTooltip'});
		            deletemessage.click(function(){
		            	var rowindexes = $('#messageList').jqxGrid('getselectedrowindexes');
		            	if(rowindexes.length>0){
		            		popupPromptBox("提示","是否要删除选中消息？",function(){
		            			var arr = [];
				            	rowindexes.forEach(function(e){
				            		var data = $('#messageList').jqxGrid('getrowdatabyid', $('#messageList').jqxGrid('getrowid', e));
				            		//console.log(data)
				            		if(data!=undefined)
				            		arr.push(data.messageId);
				            	});
				            	var param = {messageIds:arr.join(',')};
				            	$.ajax({
				            		data:param,
				            		url:ctx+'/bulletinInfoManager/deleteMessageBatch',
				            		type:'post',
				            		dataType:'json',
				            		success:function(){
				            			var ids = [];
				            			rowindexes.forEach(function(e){
				            				var id = $('#messageList').jqxGrid('getrowid', e);
						            		ids.push(id);
						            	});
				            			
				            			$("#messageList").jqxGrid('deleterow', ids);
				            			$("div[id^='row']").mouseout(function(){
				                 		   $(this).children().removeClass("jqx-fill-state-hover");
				                 	    });
				            			$('#messageList').jqxGrid('clearselection');
				            		},
				            		error:function(){
				            			showErrorMessage("删除失败！");
				            		}
				            	});
		            		});
		            	}
		            	
		            });
               }, */
               columns: [
                 { text: '发送人', datafield: 'senderName', align: 'center', cellsalign: 'center', width: '10%'},
                 { text: '消息内容', datafield: 'content', align: 'center', cellsalign: 'center', width: '60%'},
                 { text: '发送时间', datafield: 'createTime', align: 'center', cellsalign: 'center', width: '10%'},
                 { text: '消息状态', datafield: 'status', align: 'center', cellsalign: 'center', width: '10%',cellsrenderer:statuscellsrenderer},
                 { text: '处理人', datafield: 'dealerName', align: 'center', cellsalign: 'center', width: '10%'}
                 /* { text: '操作'  ,cellsrenderer:cellsrenderer } */
               ],
               cellhover:function(e){
            	   var _this = $(e);
            	   _this.siblings().addClass("jqx-fill-state-hover").parent()
            	   .siblings().children().removeClass("jqx-fill-state-hover");
               },
               ready:function(){
            	   $("div[id^='row']").mouseout(function(){
            		   $(this).children().removeClass("jqx-fill-state-hover");
            	    });
               }
	       });
	       $("#messageList").bind("pagechanged", function (event) {
	    	   $("div[id^='row']").mouseout(function(){
        		   $(this).children().removeClass("jqx-fill-state-hover");
        	    });
          });
	   }
	 function setauth(userId){
		 window.location.href = ctx+"/crewManager/crewList?type=1&ifMessage=1&userId="+userId;
	 }
	 function messagesure(obj){
		 var _this = $(obj);
		   var params = {
					messageId:_this.attr("messageId"),
					userId:_this.attr("userId"),
					type:1
			};
			$.ajax({
				url:ctx+'/userManager/messageHandle',
				data:params,
				dataType:'json',
				type:'post',
				success:function(data){
					
				},
				error:function(data){}
			});
			_this.parent().html("<a href='javascript:void(0);' onclick='setauth(\""+_this.attr("userId")+"\")'>去设置权限</a>");
	 }
	 function messagerefush(obj){
		 var _this = $(obj);
			var params = {
					messageId:_this.attr("messageId"),
					userId:_this.attr("userId"),
					type:0
			};
			$.ajax({
				url:ctx+'/userManager/messageHandle',
				data:params,
				dataType:'json',
				type:'post',
				success:function(data){
					
				},
				error:function(data){}
			});
			_this.parent().html("");
	 }
	</script>
    <style>
        .add{width:20px; height:20px; background: url("images/add.png") no-repeat;}
        .add:hover{background: url("images/add_hover.png") no-repeat;}
        .linkText{text-decoration:none;}
        .linkText:hover{text-decoration:underline; cursor:pointer}
        .btn_wrap{width: 74%;}
        .tab_wrap{width: 26%;}
        .tab_wrap li{width: 50%;}
        .button_delete{background: url("images/delete_ioc.png") no-repeat;}
		.button_delete:hover{background: url("images/delete1_ioc.png") no-repeat;}
		.ft_m{text-align: center;}
		.mar_m{margin: 0 auto;}
		.font_v_m{height:26px;line-height:26px;overflow:hidden;}
		
    </style>
  </head>
  
  <body>
  <div class="bd_wrap">
  <c:if test="${hasCrewSetAuth }">
	<div class="btn_tab_wrap">
    	<div class="btn_wrap"></div>
        <div class="tab_wrap">
        	<ul>
            	<li id="tab_1" class="tab_li_current">公告</li>
                <li id="tab_2">消息</li>
                
            </ul>
        </div>
    </div>
  </c:if>  
    <div class="danju danju_1">
        <div id="bulletingeList"></div>
    </div>
  <c:if test="${hasCrewSetAuth }">  
     <div class="danju danju_2">
        <div id="messageList"></div>   
    </div>
   </c:if>  
</div> 
        
  </body>
</html>
