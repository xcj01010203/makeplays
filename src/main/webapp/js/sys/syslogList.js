var pageSize=100;
var currentPage=1;
var filter={pageSize:100,pageNo:1};
var xhr;
var currentIp;
var interval;
$(function(){
	$(":button").jqxButton({theme:theme, width: 65, height: 25});
	$('.selectpicker').selectpicker({
	    size: 10,width:'455px'
	});
	//初始化记录数
	$("#recordNum").html(currentPage*pageSize-pageSize+1+'-'+currentPage*pageSize);
	//初始化高级查询窗口
	initSearchWindow();
	//初始化查询条件事件
	initSearchFunc();
	//加载日志列表
	loadSyslogList();
	$(document).click(function(){
    	$(".dropdown-box").hide();
    });
    $(".dropdown-box").click(function(e){
    	e.stopPropagation();
    });
    
    //初始化多选框事件
    $("input[name='searchType']").click(function(){
    	if($(this).val()==''){//全选
   			$("input[name='searchType']").prop('checked',$(this).prop('checked'));
    	} else {
    		var allchecked = true;
    		$("input[name='searchType']").each(function(i,item){
    			if($(item).val()!='' && !$(item).prop('checked')) {
    				allchecked=false;
    			}
    		});
			$("input[name='searchType']").eq(0).prop('checked',allchecked);
    	}
    });
    $(".searchOperType").click(function(){
    	if($(this).val()==''){//全选
   			$(".searchOperType").prop('checked',$(this).prop('checked'));
    	} else {
    		var allchecked = true;
    		$(".searchOperType").each(function(i,item){
    			if($(item).val()!='' && !$(item).prop('checked')) {
    				allchecked=false;
    			}
    		});
			$(".searchOperType").eq(0).prop('checked',allchecked);
    	}
    });
    //消息明细弹窗
    $('#logDescWindow').jqxWindow({
    	theme:theme,
    	width: 530,
	    height: 360, 
	    autoOpen: false,
	    isModal: true,
		resizable: false, 
	    cancelButton: $('#cancelButton'),
	    initContent: function () {
	    	
	    }
	});
});
//初始化高级查询窗口
function initSearchWindow(){
	$('#queryWindow').jqxWindow({
		theme:theme,
		width: 600,
        height: 550,
		resizable: false, 
        autoOpen: false,
        isModal: true,
        cancelButton: $('#closeSearchSubmit'),
        initContent: function () {
        }
	});
}
//初始化查询条件事件
function initSearchFunc() {
	//下拉控件中当选择空的时候自动取消勾选其他选项，当选择其他选项时，自动取消勾选空选项
	$(".searchUl").find("li").find(".clearSelection").hide();
    $('.selectpicker').on('change', function(event) {
        var value = event.target.value;
        var eventId = event.target.id;  //获取当前select控件id
        
        //select控件之前选中的值
        var prevSelectedValue = $("#"+eventId).parent().find(".preValue").val();  
        if (prevSelectedValue == "blank") {
            $("#"+ eventId).find('option').eq(0).prop('selected', false).removeAttr('selected');
            $("#"+ eventId).selectpicker('render');
            
            $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
            return false;
        }
        
        if (value == "blank") {
            $("#"+ eventId).selectpicker('deselectAll');    //首先取消所有选中
            
            //为[空]值执行选中事件 setSelected
            $("#"+ eventId).find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
            $("#"+ eventId).selectpicker('render');
        }
        $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
    });
    
    //对选择框设置鼠标移动时的显示样式
    $('.searchUl').on('mouseover', function(event) {
        if ($(this).find("li").find("select").val() != null && $(this).find("li").find("select").val() != undefined) {
            $(this).find("li").find(".clearSelection").show();
        }
    });
    
    $('.searchUl').on('mouseout', function(event) {
        $(this).find("li").find(".clearSelection").hide();
    });
    
    $(".clearSelection").on('click', function() {
        $(this).siblings(".selectpicker").selectpicker('deselectAll');
    });
    
	//加载查询条件--剧组名称
	$.ajax({
		url:'/crewManager/queryAllCrewIdAndName',
		dataType:'json',
		type:'post',
		success:function(data){
			if(data.success) {
				var crewList = data.crewList;
				for(var i=0;i<crewList.length;i++){
					$("#searchCrewName").append("<option value="+ crewList[i].crewId + ">" + crewList[i].crewName + "</option>");
				}
                $("#searchCrewName").selectpicker('refresh');
			}else {
				showErrorMessage(data.message);
			}
		},
		error:function(){}
	});
    //用户姓名/手机
	$("#searchUserName").keyup(function(){
		var _this = $(this);
		var _ul = _this.next("ul");
		
		var valueObj=$.trim(_this.val());
		var realName="";
		var phone="";
		if(valueObj == ''){
			$(".user-dropdown").hide();
			$("#searchUserId").val("");
			return;
		}
		if(valueObj.length<2 ){
			realName=valueObj;
		}else{
			phone=valueObj;
			realName=valueObj;
		}
		var params = {
				realName:realName,
				phone:phone
		};
		$.ajax({
			url:'/userManager/queryUserInfo',
			data:params,
			dataType:'json',
			type:'post',
			success:function(data){
				var html = [];
				if(data.result.length > 0){
					html.push("<table><thead><tr><td style='width:250px;'>姓名</td><td style='width:200px;'>手机</td></tr></thead><tbody>");
					$.each(data.result,function(i,v){
						var htm = "<tr>";
						htm += "<td sid="+v.userId+">"+v.realName+"</td>";
//						if(v.userName == null)
//							htm += "<td></td>";
//						else
//						    htm += "<td>"+v.userName+"</td>";
						if(v.phone == null)
							htm += "<td></td>";
						else
							htm += "<td>"+v.phone+"</td>";
						htm += "</tr>";
						html.push(htm);
					});
					html.push("</tbody></table>");
				}else{
					html.push("<center>暂无结果</center>");
					$("#searchUserId").val("");
				}
				
				_ul.css({left:92}).show().html(html.join(''));
				_ul.find("tbody tr").on("click",function(e){
					$("#searchUserName").val($(this).find("td:eq(0)").text());
					$("#searchUserId").val($(this).find("td:eq(0)").attr("sid"));
					$(".user-dropdown").hide();
				});
			},
			error:function(){}
		});
	});
}
//加载日志列表
function loadSyslogList() {
	var source =
	{
        datatype: "json",
        datafields: [
			{ name: 'logId',type: 'string' },
	        { name: 'userId',type: 'string' },
	        { name: 'userIp',type: 'string' },
//	        { name: 'operType',type: 'string' }, 
	        { name: 'objectId',type: 'string' },
//	        { name: 'tableName',type: 'string' },
//	        { name: 'authUrl',type: 'string' },
//	        { name: 'params',type: 'string' },
	        { name: 'logTime',type: 'date' }, 
	        { name: 'logDesc',type: 'string' }  ,
//	        { name: 'logResult',type: 'string' },
	        { name: 'terminal',type: 'string' },
	        { name: 'crewId',type: 'string' }, 
	        { name: 'realName',type: 'string' },
	        { name: 'userName',type: 'string' },
	        { name: 'roles',type: 'string' },
	        { name: 'address',type: 'string' },
	        { name: 'phone',type: 'string' },
	        { name: 'crewName',type: 'string' },
            { name: 'storePath',type: 'string' },
            { name: 'logFileName',type: 'string' }
        ],
        data:filter,
        type:'post',
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	//source.totalrecords=data.result.total;
        },
        root:'resultList',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        url:"/syslogManager/querySyslogList"
    };
	var columns = [
	    { text: '日志时间', datafield: 'logTime',sortable: false,  width: '10%',cellsAlign: "center",align: 'center',cellsformat: 'yyyy-MM-dd HH:mm:ss'
		},
        { text: '剧组名称', datafield: 'crewName', width: '15%',cellsAlign: "center"  ,align: 'center',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
				if($.trim(value) == '') return '';
				var html="";
				html+="<div ";
				if(loginUserType!='2'){//客服
					html+=" class='font_v_ms link_div' row='"+row+"' onclick='gotoCrewInfo(this);'";
				}else{
					html+=" class='font_v_ms' ";
				}
				html+=">" + value + "</div>";
				return html;
	        }	
        },
        { text: '用户姓名', datafield: 'realName', width: '8%',cellsAlign: "center",align: 'center' ,enabletooltips:false,
       	    cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
				if($.trim(value) == '') return '';
				var html="";
				html+="<div ";
				if(loginUserType!='2'){//客服
					html+=" class='font_v_ms link_div' row='"+row+"' onclick='gotoUserInfo(this);'";
				}else{
					html+=" class='font_v_ms' ";
				}
				html+=">" + value + "</div>";
				return html;
            }
        },
        { text: '用户职务', datafield: 'roles', width: '10%',cellsAlign: "center",align: 'center'},
	    { text: '终端类型',  datafield: 'terminal',sortable: false,  width: '7%', align: "center",cellsAlign: "center",enabletooltips:false,
	    	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
				var html ="<div class='font_v_ms ";
				if(value == 0){
					html+="pc";
				}else if(value == 1){
					html+="ios";
				}else if(value == 2){
					html+="android";
				}
				html+="'></div>";
				return html;
           }	   
	    },
	    { text: '操作ip', datafield: 'userIp',sortable: false,  width: '10%',align: 'center',cellsAlign: "center" ,enabletooltips:false,
    	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
				if($.trim(value) == '') return '';
			   var html ="<div class='font_v_ms operateIp' onmouseover='showIpArea(this)' onmouseout='hideIpArea(this)'>";
					html+=value;
					html+="</div>";
					 return html;
    	   }	 
	    },
	    { text: '操作地点',datafield: 'address',sortable: false,  width: '10%',align: 'center',cellsAlign: "center"},
	    { text: '日志摘要', datafield: 'logDesc',sortable: false,  width: '15%',align: 'center',enabletooltips:false,
	    	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			if($.trim(value) == '') return '';
			   var html ="<div class='font_v_ms operateIp link_div' onmouseover='showLogDesc(this)' onmouseout='hideLogDesc(this)' onclick='showLogDescWin(this)'>";
					html+=value;
					html+="</div>";
					 return html;
 	   }},        
	    { text: '操作对象', datafield: 'objectId',sortable: false,  width: '15%',align: 'center'},        
//	    { text: '日志文件',sortable: false,  width: '10%',align: 'center', 
//	       cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
//	           var storePath = rowdata.storePath;
//	           var fileName = rowdata.logFileName;
//	           if (storePath != null && fileName != null) {
//	               var html ="<a href='javascript:void(0)' style='width:100%;height:100%;text-align:center;line-height:30px;padding-left:10px;' onclick='downloadLog(\"" + storePath + "\",\"" + fileName + "\")'>"+ fileName +"</a>";
//                   return html;
//	           }
//               return null; 
//           }  
//	    },
	];
	var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) {
        	if(!data.success) {
        		showErrorMessage(data.message);
        	}
        }
	});
    $("#syslog").remove();
    $("#syslogdiv").append('<div id="syslog" style="width: 100%;height: 100%;"></div>');
	$("#syslog").jqxGrid(
    {
    	theme:theme,        
        width: "100%",
		height: "100%",
		source: dataAdapter,        
        enabletooltips: true,
        selectionmode: 'none',
		showToolbar: true,
		columnsResize: true,
		toolbarHeight: 35,
		rowsHeight:30,
		localization: localizationobj,
        //rendergridrows:rendergridrows,
        rendertoolbar: function (toolbar) {            
            var container = $("<div style='margin: 5px;'></div>");
            var button = $("<input type='button' style='margin-left: 10px;'  id='superSearchButton' class='button_search'/>");
            container.append(button);            
            toolbar.append(container);
            button.jqxTooltip({ content: '高级查询', position: 'bottom', autoHide: true, name: 'movieTooltip'});            
            
            $("#superSearchButton").on('click',function(){
            	$('#queryWindow').jqxWindow('open');
            });  
            
        },
        columns: columns,
        scrollmode: 'logical',
        ready:function(){
        	//加载ip
        	
        }
    });
}
//查询日志
function querySysLog() {
	var crewNameSel = $("#searchCrewName").val();
	var crewId = "";
	if(crewNameSel){
		for(var i=0;i<crewNameSel.length;i++){
			crewId+=crewNameSel[i]+",";
		}
		crewId=crewId.substring(0,crewId.length-1);
	}
	var company = $("#searchCompany").val();
	var ip =$("#searchIp").val();
	var searchAddress = $("#searchAddress").val();
	var realName = $("#searchUserName").val();
	var userId = $("#searchUserId").val();
	var startTime =$("#searchstartTime").val();
	var endTime = $("#searchendTime").val();
	var type ='';
	$("input[name=searchType]:checked").each(function(i,n){
		if($(this).val()!='') {
			if(type != ''){
				type += ",";
			}
			type += $(this).val();
		}		
	});
	var operType = '';
	$(".searchOperType:checked").each(function(i,n){
		if($(this).val()!='') {
			if(operType != ''){
				operType += ",";
			}
			operType += $(this).val();
		}
		
	});
	var desc = $("#searchlogDesc").val();
	var searchObject = $("#searchObject").val();
	var isIncludeInternalProject = $("#isIncludeInternalProject").prop('checked');
	if(crewId!= null && crewId!=""){
		filter.crewId=crewId;
	} else {
		filter.crewId="";
	}
	filter.crewName="";
	if(company) {
		filter.company=company;
	}else{
		filter.company="";
	}
	if(ip!= null && ip!=""){
		filter.userIp=ip;
	}else{
		filter.userIp="";
	}
	var isIp = 0;//包含
	if($("#searchIsIp").is(":checked")){
		isIp = 1; //不包含
	}
	filter.isIp = isIp;
	
	if(searchAddress!= null && searchAddress!=""){
		filter.address=searchAddress;
	}else{
		filter.address="";
	}
	
	if(userId!= null && userId!=""){
		filter.userId=userId;
		filter.phone="";
		filter.realName="";
	}else{
		filter.userId="";
		if(realName!= null && realName!=""){
    		filter.realName=realName;
    		var myreg = /^(1(([34578][0-9])|(76)))\d{8}$/;
    		if(myreg.test(realName)){ 
    			filter.realName="";
        		filter.phone=realName;
            }else{
            	filter.realName=realName;
        		filter.phone="";
            }
    	}else{
    		filter.realName="";
    		filter.phone="";
    	}
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
	
	if(type!= null && type!=""){
		filter.terminal=type;
	}else{
		filter.terminal="";
	}
	
	if(operType!= null && operType!=""){
		filter.operType=operType;
	}else{
		filter.operType="";
	}
	
	if(desc!= null && desc!=""){
		filter.logDesc=desc;
	}else{
		filter.logDesc="";
	}
	if(searchObject!= null && searchObject!=""){
		filter.object=searchObject;
	}else{
		filter.object="";
	}
	filter.isIncludeInternalProject=isIncludeInternalProject;
	
	currentPage = 1;
	filter.pageNo = 1;
	//初始化记录数
	$("#recordNum").html(currentPage*pageSize-pageSize+1+'-'+currentPage*pageSize);
//	$("#syslog").jqxGrid("updatebounddata",'cells');
	loadSyslogList();
	
	$('#queryWindow').jqxWindow('close');
}
//清空查询条件
function clearSearchCon(){
	$(".search-class").val('');
	$("input[type='checkbox']").prop('checked',false);
	if($("#searchCrewName").val()) {
		$(".clearSelection").trigger('click');
	}
}
//下载日志文件
function downloadLog(storePath, fileName) {
    window.location.href="/syslog/downLoadLogFile?storePath=" + storePath + "&fileName=" + fileName;
}
//打开剧组信息
function gotoCrewInfo(obj){
	var row = $(obj).attr('row');
	var rowid = $('#syslog').jqxGrid('getrowid', row);
	var data = $('#syslog').jqxGrid('getrowdatabyid', rowid);
	var crewId = data.crewId==null?'':data.crewId;
	window.location.href = "/crewManager/toCrewManagePage?crewId=" + crewId+'&flag=log';
}
//打开用户信息
function gotoUserInfo(obj){
	var row = $(obj).attr('row');
	var rowid = $('#syslog').jqxGrid('getrowid', row);
	var data = $('#syslog').jqxGrid('getrowdatabyid', rowid);
	var userInfo = data.phone==null||$.trim(data.phone)=='' ? data.realName : data.phone;
	window.location.href = "/userManager/toUserListPage?type=1&userId="+data.userId+"&userInfo=" + userInfo;
}
//根据IP地址显示所属地区
function showIpArea(obj){
	var _this = $(obj);
	$("#propTips").css({left:_this.offset().left+40,top:_this.offset().top-54}).show();
	if(currentIp == _this.text() && $("#propTips").text()!="加载中..."){
		return;
	}else{
		clearInterval(interval);
		interval = setInterval(function(){
			currentIp = _this.text();
			xhr = $.ajax({
				url:"/syslogManager/getAddrByIp",
				data:{ip:_this.text()},
				dataType:'json',
				type:'post',
				success:function(data){
					$("#propTips").text(data.data);
					clearInterval(interval);
				}
			});
		},1000);
		$("#propTips").text("加载中...");
	}	
}
//隐藏ip地址所属地区
function hideIpArea(obj){
	$("#propTips").hide();
	clearInterval(interval);
	if(typeof(xhr)!="undefined")
		xhr.abort();
}
//显示日志摘要
function showLogDesc(obj){
	var _this = $(obj);
	$("#logDesc").text(_this.text());
	$("#logDesc").css({left:_this.offset().left,top:_this.offset().top-54}).show();
}

//隐藏日志摘要
function hideLogDesc(obj){
	$("#logDesc").hide();
}
//分页
//首页
function firstPage(){
	currentPage=1;
	filter.pageNo=currentPage;
//	$("#syslog").jqxGrid("updatebounddata",'cells');
	loadSyslogList();
	$("#recordNum").html(currentPage*pageSize-pageSize+1+'-'+currentPage*pageSize);
}
//上一页
function previousPage(){
	if(currentPage>1) {
		currentPage--;
		filter.pageNo=currentPage;
//		$("#syslog").jqxGrid("updatebounddata",'cells');
		loadSyslogList();
		$("#recordNum").html(currentPage*pageSize-pageSize+1+'-'+currentPage*pageSize);
	}
}
//下一页
function nextPage(){
	currentPage++;
	filter.pageNo=currentPage;
//	$("#syslog").jqxGrid("updatebounddata",'cells');
	loadSyslogList();
	$("#recordNum").html(currentPage*pageSize-pageSize+1+'-'+currentPage*pageSize);
}
//显示日志摘要窗口
function showLogDescWin(obj) {
	$(".topdiv").text($(obj).text());
	$('#logDescWindow').jqxWindow('open');
}