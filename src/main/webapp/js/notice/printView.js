function UserListGrid(tableId, containerId) {
	this.tableId = tableId;
	this.containerId = containerId;
	this.records = null;
	var me = this;
	//获取记录数据
	this.getRecords=function(){
		var records = null;
		$.ajax({
			url: "/notice/queryCrewMobileUserWithNoticeAuth",
			type: "post",
			dataType: "json",
			async: false,
			success:function(data) {
				records=data.userList;
			}
		});
		this.records = records;
		return this.records;
	};
	
	//加载表格
	this.loadTable=function(){
		this.getRecords();
		this.createTable();
	};
	
	//创建表格html
	this.createTable = function (){
		var $container = $("#"+me.containerId);
		
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tableObj.append('<div class="theadDiv" id="theadDiv"><table cellpadding="0" cellspacing="0" border="0" class="user_table">'+
				'<thead><tr id="tableHead"></tr></thead></table></div>');
		
		//表格头对象
		var _head=_tableObj.find("#tableHead");
		
		_head.append('<td class="checkboxTd"><input type="checkbox" id="checkedAll" class="line-height" style="display:none;"/></td>');
		_head.append('<td><p class="userName">名称</p></td>');
		_head.append('<td><p class="roleName">职务</p></td>');
		
		_tableObj.append('<div class="tbodyDiv" id="tbodyDiv"><table cellpadding="0" cellspacing="0" border="0"><tbody id="tableBody"></tbody></table></div>');
		//表格主体
		var _tBody=_tableObj.find("#tableBody");
		
		//所有数据
		var tableData = this.records;
		if (tableData != null) {
			for (var i=0;i<tableData.length;i++) {
				var rowData=tableData[i];
				var _row = this.createRow(_tBody, rowData, i);
				_tBody.append(_row);
			}
		}
		
		//checkbox全选
		$("#checkedAll").click(function(){
			if(this.checked){
				_tableObj.find("tbody :checkbox").prop("checked",true);
				
				$container.children().remove();
				$container.append("<li id='all'>所有成员<a href='javascript:void(0)' class='closeTag'></a></li>");
			}else{
				_tableObj.find("tbody :checkbox").prop("checked",false);
				$container.children().remove();
			}
		});
		
		_tBody.on("click", ":checkbox", function(ev) {
			var $this = $(this);
			var userName = $this.attr("userName");
			var userId = this.id;

			if (isCheckAll()) {
				$container.children().remove();
				$container.append("<li id='all'>所有成员<a href='javascript:void(0)' class='closeTag'></a></li>");
			} else {
				if (this.checked) {
					$container.append("<li id="+userId+">"+userName+"<a href='javascript:void(0)' class='closeTag'></a></li>");
				} else {
					$container.find("li").each(function(index){
						var liId = this.id;
						if (liId == userId) {
							$(this).remove();
							return false;
						}
						
						//如果此时是全选状态，则把“全部人员”标签去掉，重新遍历一次选中的人员
						if (liId == "all") {
							$(this).remove();
							_tableObj.find("tbody :checkbox:checked").each(function(index) {
								var userName = $(this).attr("userName");
								var userId = this.id;
								$container.append("<li id="+userId+">"+userName+"<a href='javascript:void(0)' class='closeTag'></a></li>");
							});
						}
					});
				}
			}
			ev.stopPropagation();
		}).on("click", "tr", function() {
			$(this).find(":checkbox").trigger("click");
		});
		
		//行checkbox事件只判断是否全选
		function isCheckAll(){
			var checkboxs = _tableObj.find("tbody :checkbox");
			
			for(var i=0, len=checkboxs.length; i<len;i++){
				//console.log(checkboxs[i].checked);
				if(!checkboxs[i].checked)
					break;
			}
			
			if(i != len){
				$("#checkedAll").prop("checked",false);
				return false;
			}else{
				$("#checkedAll").prop("checked",true);
				return true;
			}
		}
	};
	
	//选中一行数据
	this.checkedItem = function(userId) {
		var tableId = this.tableId;
		$("#" + tableId).find(":checkbox[id="+ userId +"]").trigger("checked");
	};
	
	//取消选中一行
	this.unCheckItem = function(userId) {
		$("#"+this.tableId).find("tbody :checkbox[id="+userId+"]").trigger("click");
	};
	
	this.unCheckAll = function() {
		$("#checkedAll").trigger("click");
	};
	
	//判断是否全选
	this.isCheckAll = function() {
		return $("#checkedAll").prop("checked");
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		var _row = $("<tr rowId='"+rowid+"'></tr>");
		var roleNames= rowData.roleNames == null ? "" : rowData.roleNames;
		
		_row.append('<td class="checkboxTd"><input type="checkbox" id="'+rowData.userId+'" index="'+rowid+'" userName="'+rowData.realName+'" class="line-height userCheckbox"/></td>');
		_row.append('<td title="'+rowData.realName+'"><p class="userName">'+rowData.realName+'</p></td>');
		_row.append('<td title="'+ roleNames +'"><p class="roleName">'+ roleNames +'</p></td>');
		return _row;
	};
	
	
	this.getSelectedIds = function() {
		var result = "";
		var _tableObj = $("#"+this.tableId);
		_tableObj.find("tbody :checkbox:checked").each(function(index) {
			if(index == 0){
				result = $(this).attr("id");
			} else {
				result += ","+$(this).attr("id");
			}
		});
		return result;
	};
}

//点击发送给所有人按钮
function selectAllUser(){
	$("#checkedAll").trigger("click");
}


//拖动悬浮框
$(document).ready(function(){
	$("#suspensionFrame").mousedown(function(e){//e鼠标事件
		$(this).css("cursor","move");
		var offset = $(this).offset();//div在页面的位置
		var x = e.pageX - offset.left;//获得鼠标指针离div元素左边界的距离
		var y = e.pageY - offset.top;//获得鼠标指针离div元素上边界的距离
		$(document).bind("mousemove", function(ev){
			$("#suspensionFrame").stop();
			
			var _x = ev.pageX - x;//获得X轴方向移动的值 
			var _y = ev.pageY - y;//获得Y轴方向移动的值 
			
			$("#suspensionFrame").animate({left: _x+"px", top: _y+"px"},10);
		});
	});
	$(document).mouseup(function(){
		$("#suspensionFrame").css("cursor", "default");
		$(this).unbind("mousemove");
	});
});

