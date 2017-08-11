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
            
			$("#noticeNameInput").jqxInput({
		        theme: theme,
		        width: '180px',
		        height: 25,
		        placeHolder: "请输入通告单名称"
		    })
		    
		    $("#noticeDateDiv").jqxDateTimeInput({
		        width: '180px', 
		        height: '25px',
		        culture: 'ch-CN',
		        value:new Date(),
		        //min: new Date(), 
		        formatString: "yyyy/MM/dd" 
		    });
		    
		    $("#noticeCancelButton").jqxButton({
		        theme:theme, 
		        width: 80, 
		        height: 25 
		    });
		    
		    $("#saveNoticeButton").jqxButton({
		        theme:theme, 
		        width: 80, 
		        height: 25
		    });
		    
		    $("#noticeGroupDiv").jqxDropDownList({
		        theme:theme,
		        selectedIndex:0,
		        source: groupSource, 
		        displayMember: "text", 
		        valueMember: "value", 
		        width: '180px', 
		        height: 25,
		        autoDropDownHeight: true
		    });
		    $("#noticeGroupDiv").on('change',function(event){
		        var args = event.args;
		        if (args) {
		            var index = args.index;
		            var item = args.item;
		            
		            if(item.value=="99"){
		                
		                if(index>25){
		                    alert("目前最多选择到Z组");
		                    $("#noticeGroupDiv").jqxDropDownList("selectIndex",index-1);
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
		                            $("#noticeGroupDiv").jqxDropDownList("selectIndex",0);
		                        }
		                        $("#noticeGroupDiv").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
		                        $("#noticeGroupDiv").jqxDropDownList("selectIndex",index);
		                    }
		                });
		            }
		        }
		    });
		    
		    $('#noticeForm').jqxValidator({
		        animationDuration: 1,
		        rules: [{input: '#noticeNameInput', message: '通告单名称不可为空!', action: 'keyup,blur', rule: 'required' }]
		    });
		    
		    $("#saveNoticeButton").on("click", function () {
		        $("#noticeDateInput").val($("#noticeDateDiv").val());
		        $("#groupIdValue").val($("#noticeGroupDiv").val());
		        
		        if ($('#noticeForm').jqxValidator('validate')) {
		            $.ajax({
		                url: "/notice/noticeSaveWinoutView",
		                type: 'post',
		                data: $('#noticeForm').serialize(),
		                dataType: 'json',
		                async: false,
		                success: function (param) {
		                    if (param.success) {
		                        alert("操作成功");
		                        window.location.href = "/notice/noticeList";
		                    } else {
		                        alert(param.message);
		                    }
		                }
		            });
		        }
		    });
		    
		    $("#noticeCancelButton").on('click', function() {
		      window.location.href = "/notice/noticeList";
		    });
        });
    </script>
    <style type="text/css">
        
    </style>
  </head>
  
  <body>
    <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
    <ul>
        <li style="margin-left: 50px;">通告单</li>
    </ul>
    <div>
        <div id="register" style=" margin-left: 200px;margin-top:50px">
            <div><h3>通告单</h3></div>
            <div>
                <form id="noticeForm" action="/notice/noticeSave">
	                <table class="register-table">
	                    <tr>
	                        <td>名称:</td>
	                        <td><input type="text" name="noticeName" id="noticeNameInput" class="text-input" /></td>
	                    </tr>
	                    <tr>
	                        <td>时间：</td>
	                        <td>
	                            <div style='float: left; margin-top: 3px;' id='noticeDateDiv'></div>
	                            <input type="hidden" id="noticeDateInput" name="noticeDateStr">
	                        </td>
	                    </tr>
	                    <tr>
	                        <td>分组:</td>
	                        <td>
	                            <div id="noticeGroupDiv" /></div> 
	                            <input type="hidden" name="groupId" id="groupIdValue">
	                        </td>
	                    </tr>
	                </table>
	            </form>
	            <div style="bottom:0;left:120px;margin-bottom:10px;">
	                <input type="button" value="确定" id="saveNoticeButton" /> 
	                <input type="button" value="返回" id="noticeCancelButton" />
	            </div>
            </div>
        </div>
    </div>
    </div>
  </body>
</html>
