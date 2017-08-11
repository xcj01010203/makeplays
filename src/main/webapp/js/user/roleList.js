var rowExpandRow=0;
var rowClickRow=0;
$(function() {
	$(':button').jqxButton({theme:theme, width: 60, height: 25 });
	$('.text-input').jqxInput({theme:theme});
	//表单验证
	$('#updateRoleForm').jqxValidator({
	    hintType: 'label',
	    animationDuration: 1,
	    rules: [
	        { input: '#roleName', message: '名称不可为空!', action: 'keyup, blur',  rule: function(input, commit){
            	if($.trim(input.val()).length==0){
            		$("#roleBlooen").hide();
             		return false;
             	}
                return true;
            } }
	    ]
	});
	//初始化排序窗口
    initSortWindow();
    //初始化修改窗口
    initUpdateWindow();
    //初始化角色权限设置窗口
    initRoleAuthWindow();
    //加载角色列表
    loadRoleList();
});
//加载角色列表
function loadRoleList(){
	var source =
    {
        datatype: "json",
        datafields: [
			{ name: 'roleId',type: 'string' },
	        { name: 'roleName',type: 'string' },
            { name: 'crewName',type: 'string' },
	        { name: 'roleDesc',type: 'string' },
	        { name: 'crewId',type: 'string' },
	        { name: 'parentId',type: 'string' },
	        { name: 'level',type: 'string' },
	        { name: 'orderNo',type: 'int' }
        ],
        hierarchy:
        {
            keyDataField: { name: 'roleId' },
            parentDataField: { name: 'parentId' }
        },
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	source.totalrecords=data.rows.total;
        },
        root:'rows',
        id: 'roleId',
        type:'post',
        url:'/roleManager/queryRoleList'
    };
    
    var rendergridrows = function (params) {
    	//调用json返回的列表数据
        return params.data;
    }
    var dataAdapter = new $.jqx.dataAdapter(source);
    $("#roleListgrid").jqxTreeGrid(
    {
    	theme:theme,
        width: '100%',
        height:'100%',
        source: dataAdapter,
        showToolbar: true,
        rendertoolbar: function (toolbar) {
            $("#toolbarroleListgrid").css("background-color","#ffffff");
            var container = $("<div style='margin: 5px;'></div>");
            var button = $("<button id='addBtn' class='addBtn'></button>");
            var btnParent = $("<button id='btnParent' class='btnParent'></button>");
            var orderButton = $("<button id='orderButton' class='orderButton'></button>");
            container.append(button);
            container.append(btnParent);
            container.append(orderButton);
            toolbar.append(container);
            $("#addBtn").jqxTooltip({ content: '创建角色', position: 'bottom', });
            $("#btnParent").jqxTooltip({ content: '添加分组', position: 'bottom', });
            $("#orderButton").jqxTooltip({ content: '排序', position: 'bottom', });
            $("#addBtn").click(function(){
            	$("#uprole").html("创建角色");
            	$("#roleGroup").html("角色名称:");
         		$("#roleDesc").val('');
          		$("#roleName").val('');
          		$("#roleId").val('');
          		$("#parentId").val('01');
          		$("#level").val(1);
          		$("#orderNo").val(0);
        		$("#roleBlooen").hide();
            	$("#updateWindow").jqxWindow('open');
            });
            $("#btnParent").click(function(){
            	$("#uprole").html("创建分组");
            	$("#roleGroup").html("组别名称:");
         		$("#roleDesc").val('');
          		$("#roleName").val('');
          		$("#roleId").val('');
          		$("#parentId").val('00');
          		$("#level").val(1);
          		$("#orderNo").val(0);
        		$("#roleBlooen").hide();
            	$("#updateWindow").jqxWindow('open');
            });
            $("#orderButton").click(function(){//排序
            	showSortListWin();
            });                    
        },
        columns: [
          { text: '角色名', datafield: 'roleName', width: '50%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		  var html=[];
        		  if(defaulthtml.parentId=='00'){
        			  html.push("<a href='javascript:void(0);'  style='color:#000;' title='修改' id='"+defaulthtml.roleId+"'  onclick='updateRole(\""+defaulthtml.roleId+"\");'>"+defaulthtml.roleName+"</a>"+
        					    "<a class='addBtns' style='display:none;'  href='javascript:void(0);' title='添加角色' onclick='addChild(\""+defaulthtml.roleId+"\");'></a>"+
        			 		    "<a class='deltelumns' style='display:none;' href='javascript:void(0);' title='删除' onclick='delRole(\""+defaulthtml.roleId+"\",\""+defaulthtml.crewId+"\",\""+defaulthtml.records+"\");' ></a>");
        		  }else{
					    html.push("<a href='javascript:void(0);'  style='margin-left: -10px;' title='修改' id='"+defaulthtml.roleId+"' onclick='roleAuthority(\""+defaulthtml.roleId+"\");'>"+defaulthtml.roleName+"</a>"+
					   			  "<a class='deltelumns' style='display:none;' href='javascript:void(0);' title='删除' onclick='delRole(\""+defaulthtml.roleId+"\",\""+defaulthtml.crewId+"\",\""+defaulthtml.records+"\");' ></a>");
        		  }
	                    return html;
               }                	    
          },
          { text: '角色描述', datafield: 'roleDesc', width: '50%' },
        ]
    });
    $('#roleListgrid').on('rowClick', 
	function (event){
	    var args = event.args;
	    var key = args.key;
	    rowClickRow=key;
   	});
    $('#roleListgrid').on('rowExpand', 
	function (event){
	    var args = event.args;
	    var key = args.key;
	    rowExpandRow=key;
   	});
    $('#roleListgrid').on('rowCollapse', 
	function (event){
	    var args = event.args;
	    var key = args.key;
	    rowExpandRow=0;
   	});
}
//初始化排序窗口
function initSortWindow(){
	$("#sortWindow").jqxWindow({
		theme:theme,  
        width: 450,
        height: 600, 
        autoOpen: false,
        maxWidth: 2000,
        maxHeight: 1500,
        resizable: true,
        isModal: true,
        showCloseButton: true,
        resizable: false,
        modalZIndex: 1000,
        initContent: function() {
        }
	});
	//窗口的关闭事件
	$("#sortWindow").on("close", function(){
	    $('#roleListgrid').jqxTreeGrid("updateBoundData");
	});
}
//加载角色排序grid
function showRoleSortGrid() {
	$("#sortGrid").treegrid({
		width: "440",
        height: "480",
        loadMsg: "正在加载角色..",
        animate: true,
        url: "/roleManager/queryRoleList",
        method: 'post',
        idField: 'roleId',
        treeField: 'roleName',
        autoRowHeight: true,
        dnd:true,
        columns: [[
            {title:'角色名', field:'roleName', width:420}
        ]],
        onLoadSuccess: function(row){
        	 $(this).treegrid('enableDnd', row?row.roleId:null); 
        } ,
        onDragOver:function(targetRow, sourceRow){
     	   if(targetRow._parentId!=sourceRow._parentId){
           		return false;
           	}
        },
        onBeforeDrag:function(Row){},
        onBeforeDrop: function(targetRow, sourceRow, point){
        	if(targetRow._parentId!=sourceRow._parentId){
           		return false;
           	}
           	if(point == 'append'){
           		return false;
           	}
        	
        },
        onStopDrag: function(row){
        	
        },
        onClickRow:function(row){
        	
        },
        onDrop:function(targetRow, sourceRow, point){
        	if(targetRow._parentId!=sourceRow._parentId){
          		return false;
          	}
          	if(point == 'append'){
          		showInfoMessage("拖动只能在同级之间进行！");
          		return false;
          	}
    	    var parent = $('tr[node-id='+sourceRow.roleId+']').parent();
          	var child = [];
          	parent.children('tr').each(function(i,n){
          		var nodeid = $(this).attr('node-id');
          		if(nodeid != undefined){
          			child.push(nodeid);
          		}
          	});
           	
           	$.ajax({
           		url: '/roleManager/updateRoleSequence',
           		data: {ids:child.join(',')},
           		dataType: 'json',
           		type: 'post',
           		success: function(response){
           			if(response.success){
           				showSuccessMessage("更新角色顺序成功！");
           			}else{
           				showErrorMessage(response.message);
           			}
           		}
           	});
        }
	});
}
//显示角色排序窗口
function showSortListWin(){
	showRoleSortGrid();
	$("#sortWindow").jqxWindow("open");
}
//关闭按钮的事件
function closeSortListWin(){
	$("#sortWindow").jqxWindow("close");
}
//初始化修改窗口
function initUpdateWindow(){
	 $("#updateWindow").jqxWindow({
		theme : theme,
		height : 240,
		width : 400,
		resizable : false,
		isModal : true,
		autoOpen : false
	});
}
//关闭修改窗口
function sendCloses(){
	 $("#updateWindow").jqxWindow('close');
}
//创建子级角色
function addChild(roleId){   
	$("#uprole").html("创建子级");
	$("#roleGroup").html("角色名称:");
	$("#roleDesc").val('');
	$("#roleName").val('');
	$("#roleId").val('');
	$("#parentId").val(roleId);
	$("#level").val(2);
	$("#orderNo").val(0);
	$("#roleBlooen").hide();
 	$("#updateWindow").jqxWindow('open');
}
//修改分组
function updateRole(roleId,parentId){
   $.ajax({
     	url:'/roleManager/queryRoleInfo',
     	type:'post',
     	data:{
     		roleId:roleId
     	},
     	async: false,
     	dataType: 'json',
     	 success:function(data){
     		 if(data.success) {
     			 var obj = data.roleInfo;
     			 $("#uprole").html("修改分组");
     			 $("#roleGroup").html("组别名称:");
     			 $("#crewId").val(obj.crewId);
     			 $("#roleDesc").val(obj.roleDesc);
     			 $("#roleName").val(obj.roleName);
     			 $("#roleId").val(obj.roleId);
     			 $("#parentId").val('00');
     			 $("#level").val(obj.level);
     			 $("#orderNo").val(obj.orderNo);
     			 $("#roleBlooen").hide();
     			 $("#updateWindow").jqxWindow('open');
     		 }
        }
 	});
}
//保存角色信息
function saveRole(){
	$("#roleBlooen").hide();
	if($('#updateRoleForm').jqxValidator('validate')){
		$.ajax({
         	url:'/roleManager/isExistRoleName',
         	type:'post',
         	data:{
         		roleName:$("#roleName").val(),
         		roleId:$("#roleId").val(),
         	},
         	dataType: 'json',
         	success:function(data){
         		if(data.success){         			
         			if(data.result) {
             			$("#roleBlooen").show();
         			}else{
             			addrole();
         			}
         		}
	        }
     	});
	}
}
//添加角色
function addrole(){
	 $.ajax({
      	url:'/roleManager/saveRole',
      	type:'post',
      	data:{
      		roleId:$("#roleId").val(),
      		roleName:$("#roleName").val(),
      		roleDesc:$("#roleDesc").val(),
      		crewId:$("#crewId").val(),
      		parentId:$("#parentId").val(),
      		level:$("#level").val(),
      		orderNo:$("#orderNo").val()
      	},
      	async: false,
      	dataType: 'json',
      	success:function(data){
      		if(data.success){
      			sendCloses();
                $("#roleListgrid").jqxTreeGrid("updateBoundData", "cells");
                 
                setTimeout("selectExpandRow();",200);
      			showSuccessMessage("操作成功！");
      		}else{
      			showErrorMessage(data.message);
      		}
	     }
  	});
}
function selectExpandRow(){
	$('#roleListgrid').jqxTreeGrid('expandRow', rowExpandRow);
	$('#roleListgrid').jqxTreeGrid('selectRow', rowClickRow);
}
//删除角色
function delRole(roleId,crewId,records){
  if(records=='undefined' || $.trim(records).length<1){
	 $.ajax({
	       	url:'/roleManager/isExistUserRoleMap',//查询角色是否已被引用
	       	type:'post',
	       	data:{
	       		roleId:roleId,
	       	},
	       	async: false,
	       	dataType: 'json',
	       	 success:function(data){
	       		if(data){
	       			popupPromptBox("提示","确定删除吗?",function(){
	       				$.ajax({
	       				  	url:'/roleManager/deleteRole',
	       				  	type:'post',
	       				  	data:{
	       				  		roleId:roleId,
	       				  		crewId:crewId
	       				  	},
	       				  	async: false,
	       				  	dataType: 'json',
	       				  	 success:function(data){
	       				  		if(data.success){
	       				            $("#roleListgrid").jqxTreeGrid('deleteRow', roleId);
	       				          
	       				  			showSuccessMessage("操作成功！");
	       				  		}else{
	       				  			showErrorMessage(data.message);
	       				  		}
	       				  	 }
	       					});
	       			});
	       		}else{
	       			showErrorMessage("角色已被引用，不能删除！");
	       		}
	       	 }
	   	});
	 }else{
		 showErrorMessage("分组含有角色，不能删除！");
	 }
}
//角色权限设置
function roleAuthority(roleId){
    $("#roleDetailDiv").jqxWindow("open");
    $("#roleDetailDiv").find("iframe").attr("src", "/roleManager/toRoleAuthDetailPage?roleId=" + roleId);
}
//初始化角色权限弹出窗
function initRoleAuthWindow() {
    $("#roleDetailDiv").jqxWindow({
         theme:theme,  
         width: 850,
         height: 720, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: true,
         resizable: false,
         modalZIndex: 1000,
         initContent: function() {
              
         }
    });
}
function closeRoleDetailWindow() {
    $("#roleDetailDiv").jqxWindow("close");
}
function closeRoleDetailWindowAndRefresh() {
    $("#roleDetailDiv").jqxWindow("close");
    $('#roleListgrid').jqxTreeGrid("updateBoundData");
    
    setTimeout("selectExpandRow();",200);
}