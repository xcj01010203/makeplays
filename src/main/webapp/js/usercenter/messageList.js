var filter={};
$(document).ready(function() {
	loadMessageGrid();
	loadBulletinGrid();
	
	//更新消息查看状态
	updateMessageReadStatus();	
	
	$("#bulletinInfoTable").hide();
	
	//消息明细弹窗
    $('#messageDetailWindow').jqxWindow({
    	width: 530,
	    height: 360, 
	    autoOpen: false,
	    isModal: true,
		resizable: false, 
	    cancelButton: $('#cancelButton'),
	    initContent: function () {
	    	
	    }
	});
    //查询弹窗
    $('#queryWindow').jqxWindow({
		width: 480,
        height: 300,
		resizable: false, 
        autoOpen: false,
        isModal: true,
        cancelButton: $('#closeSearchSubmit'),
        initContent: function () {
        }
	});
});


//加载消息列表
function loadMessageGrid() {
	
	var source = {
		url: '/messageInfoManager/queryMessageList',
		type: "post",
		dataType : "json",
        root:'messageList',
        data:filter,
		datafields : [
		     {name: "id", type: "string"},
		     {name: "title", type: "string"},
		     {name: "content", type: "int"},
		     {name: "type", type: "string"},
		     {name: "status", type: "string"},
		     {name: "remindTime", type: "string"},
		     {name: "createTime", type: "string"}
		],
        beforeprocessing: function(data){
        	source.totalrecords = data.totalCount;
        }
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	var statusClass = function(row, column, value, data) {
		if (data.status == 1) {
			return "grey";
		}
	};
	
	var contentRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "";
		if (rowdata.status == 1) {
			html = "<div class='content-cell pointer-hover grey' title='"+ value +"' onclick='openMessage(1,\""+rowdata.id+"\")'>"+ value +"</div>";
		} else {
			html = "<div class='content-cell pointer-hover' title='"+ value +"' onclick='openMessage(0,\""+ rowdata.id +"\",\""+row+"\","+JSON.stringify(rowdata)+")'>"+ value +"</div>";
		}
		return html;
	};
	
	var rendergridrows = function (params) {
        return params.data;
	};
	$("#messageInfoTable").remove();
	$("#bulletinInfoTable").before('<div id="messageInfoTable" class="content-div"></div>');
	$("#messageInfoTable").jqxGrid({
		width: "cal(100% - 5px)",
		height: "calc(100% - 40px)",
		source: dataAdapter,
        selectionmode: 'checkbox',
		pagesize: 20,
        pageable: true,
        pagesizeoptions: ['20', '50', '100', '全部'],
		localization: localizationobj,
        rendergridrows: rendergridrows,
        //virtualmode: true,
		rowsheight: 35,
		columnsheight: 35,
		showToolbar: true,
		toolbarHeight: 35,
		rendertoolbar: function (toolbar) {
            var container = $("<div style='margin: 5px;'></div>");
            var searchButton = $("<input type='button' style='margin-left: 10px;' id='superSearchButton' class='button_search'/>");
            var readButton = $("<input type='button' style='margin-left: 10px;' id='readButton' class='button_read'/>");
            container.append(searchButton);
            container.append(readButton);
            toolbar.append(container);
            searchButton.jqxTooltip({ content: '高级查询', position: 'bottom'}); 
            readButton.jqxTooltip({ content: '标记为已读', position: 'bottom'}); 
            
            $("#superSearchButton").on('click',function(){
            	$('#queryWindow').jqxWindow('open');
            });  
            $("#readButton").on('click',function(){
            	setMultiRead();
            });
        },
		columns: [
			{text: "标题", cellclassname: statusClass, datafield: 'title', width: '20%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "内容", cellclassname: statusClass, cellsrenderer: contentRenderer, datafield: 'content', width: '50%', cellsAlign: 'left', align: 'center', sortable: false},
			{text: "时间", cellclassname: statusClass, datafield: 'remindTime', width: '30%', cellsAlign: 'left', align: 'center', sortable: false}
		]
	});
		
}

//加载系统公告
function loadBulletinGrid() {
	
	var source = {
            url:'/bulletinInfoManager/queryBulletinList',
            datatype: "json",
            data: {operateType: 1},
            root:'bulletinList',
            datafields: [
                { name: 'bulletinId', type: 'string' },
                { name: 'bulletinName', type: 'date'},
                { name: 'content', type: 'string' },
                { name: 'attachUrl', type: 'string' },
                { name: 'attachName', type: 'string' },
                { name: 'pubUserName', type: 'int' },
                { name: 'pubUserId', type: 'string' },
                { name: 'createTime', type: 'string' },
                { name: 'startDate', type: 'string' },
                { name: 'endDate', type: 'string' },
                { name: 'status', type: 'int' }
            ],
            beforeprocessing: function(data){
            	source.totalrecords = data.totalCount;
            }
       };
	
	var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		if (rowdata.attachUrl != '' && rowdata.attachUrl != null) {
			var html = "<a class='download-link' type='button' id='downloadFile' onclick='downloadFile(\""+rowdata.attachUrl+"\");' >下载附件</a>";
			return html;
		}
	};
	
	var contentRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='content-cell' title='"+ rowdata.content +"'>"+ rowdata.content +"</div>";
		return html;
	};
	
	var timeRenderer = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var html = "<div class='content-cell'>"+ rowdata.startDate + " ~ " + rowdata.endDate +"</div>";
		return html;
	};
	
	var rendergridrows = function (params) {
        return params.data;
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	$('#bulletinInfoTable').jqxGrid({
		width: "cal(100% - 5px)",
		height: "calc(100% - 40px)",
		source: dataAdapter,
		pagesize: 20,
        pageable: true,
        pagesizeoptions: ['20', '50', '100', '全部'],
		localization: localizationobj,
        rendergridrows: rendergridrows,
        virtualmode: true,
		rowsheight: 33,
		columns: [
		  { text: '公告名称', datafield: 'bulletinName', width: "10%"},
		  { text: '公告内容', cellsrenderer: contentRenderer, width: "40%"},
		  { text: '有效时间', cellsrenderer: timeRenderer, width: "25%"},
		  { text: '发布时间', datafield: 'createTime', width: "15%"},
          { text: '附件', cellsrenderer: cellsrenderer, width: "10%" }
		]
	});
}
//打开消息
function openMessage(flag,id,rowid,rowdata){
	if(flag==0){
		readMessage(id,rowid,rowdata);
	}
	$.ajax({
		url: "/messageInfoManager/queryMessageById",
		type: "post",
		dataType: "json",
		data: {messageId: id},
		success: function(response) {
			if (response.success) {
				var result=response.result;
				var html=[];
				html.push('	<div class="message_title" title="'+ result.title +'">'+result.title+'</div>');
				html.push('	<div class="createTime">'+result.remindTime+'</div>');
				html.push('	<div class="content">'+result.content.replace(/\r\n/g,'<br>').replace(/\n/g,"<br>")+'</div>');
				$("#manageButton").remove();
				if(result.type==4 || result.type==8) {//申请入组、意见反馈
					$("#closeButton").after('<input type="button" class="button" id="manageButton" value="去处理" style="margin-left:20px;" onclick="gotoManagePage('+result.type+')">');
				}
				$(".topdiv").html(html.join(''));
				//打开窗口
				$('#messageDetailWindow').jqxWindow('open');
			}
		}
	});
}
//关闭消息明细
function closeMessageDetailWindow(){
	$('#messageDetailWindow').jqxWindow('close');
}
//跳转到入组消息处理页面
function gotoManagePage(type){
	if(type==4){//申请入组
		parent.location.href="/crewManager/toCrewSettingsPage";
	}else if(type==8){//意见反馈
		parent.showRightDiv(5);
	}
}

//阅读消息
function readMessage(id,rowid,rowdata) {
	$.ajax({
		url: "/messageInfoManager/readMessage",
		type: "post",
		dataType: "json",
		data: {messageId: id},
		success: function(response) {
			if (response.success) {
				rowdata.status=1;
				$('#messageInfoTable').jqxGrid('updaterow', rowid, rowdata);
				if(typeof(parent.loadUnReadMessageNum)=='function') {
					parent.loadUnReadMessageNum();
				}
				if(typeof(parent.parent.getMessageList)=='function') {
					parent.parent.getMessageList();
				}
			}
		}
	});
};

//切换标签
function selectTag(own, type) {
	$(own).addClass("active");
	$(own).siblings("a").removeClass("active");
	
	if (type == 0) {	//消息
		$("#bulletinInfoTable").hide();
		$("#messageInfoTable").show();
	} else {	//公告
		$("#bulletinInfoTable").show();
		$("#messageInfoTable").hide();
	}
}

//下载附件
function downloadFile(storePath) {
	window.location.href = storePath;
}

//更新消息查看状态
function updateMessageReadStatus(){
	$.ajax({
		url: "/messageInfoManager/updateMessageReadStatus",
		type: "post",
		dataType: "json",
		success: function(response) {
			
		}
	});
}
//清空查询条件
function clearSearchCon(){
	$("input[name='searchStatus']").prop('checked',false);
	$(".search-class").val('');
}
//查询消息
function queryMessage(){
	var content = $("#searchMessageContent").val();
	var status=$("input[name='searchStatus']:checked").val();
	var startTime =$("#searchstartTime").val();
	var endTime = $("#searchendTime").val();
	
	if(content!= null && content!=""){
		filter.content=content;
	}else{
		filter.content="";
	}
	if(status!= null && status!=""){
		filter.status=status;
	} else {
		filter.status="";
	}
	if(startTime!= null && startTime!=""){
		filter.startTime=startTime;
	}else{
		filter.startTime="";
	}	
	if(endTime!= null && endTime!=""){
		filter.endTime=endTime;
	}else{
		filter.endTime="";
	}

	loadMessageGrid();
	
	$('#queryWindow').jqxWindow('close');
}
//全部设为已读
function setMultiRead(){
	var indexs = $('#messageInfoTable').jqxGrid('getselectedrowindexes');
	//没有选中
	if(!indexs || indexs.length==0) {
		parent.showInfoMessage('请选择消息');
		return false;
	}
	var messageIds=[];
	for(var i=0;i<indexs.length;i++){
		var rowData=$('#messageInfoTable').jqxGrid('getrowdata',indexs[i]);
		messageIds.push(rowData.id);
	}
	$.ajax({
		url: "/messageInfoManager/readMultiMessage",
		type: "post",
		dataType: "json",
		data:{messageIds:messageIds.join(',')},
		success: function(response) {
			if(response.success) {
				parent.showSuccessMessage('操作成功!');
				loadMessageGrid();
				if(typeof(parent.loadUnReadMessageNum)=='function') {
					parent.loadUnReadMessageNum();
				}
				if(typeof(parent.parent.getMessageList)=='function') {
					parent.parent.getMessageList();
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}