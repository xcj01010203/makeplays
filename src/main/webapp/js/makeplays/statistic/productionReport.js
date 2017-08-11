$(document).ready(function() {
	loadShootingProductionReport();
});
//加载摄制生产报表
function loadShootingProductionReport() {
	var source = {
		url: '/viewStatisticManager/queryShootingProductionReport',
		type: "post",
		dataType : "json",
		data: {},
		datafields : [
			{ name: 'dayNum',type: 'int' },//天数
			{ name: 'noticeDate',type: 'string' },//日期
			{ name: 'planViewCount',type: 'int' },//计划完成场次
	        { name: 'realViewCount',type: 'int' },//实际完成场次
	        { name: 'totalViewCount',type: 'double' },//累计完成场次
	        { name: 'planPageCount',type: 'double' },//计划完成页数
	        { name: 'realPageCount',type: 'double' },//实际完成页数
	        { name: 'totalPageCount',type: 'double' },//累计完成页数
	        { name: 'avgViewCount',type: 'double' },//平均完成场次
	        { name: 'avgPageCount',type: 'double' },//平均完成场数
	        { name: 'everyDayCha',type: 'double' },//每天差额
	        { name: 'totalDayCha',type: 'double' },//累计差额
	        { name: 'everyDayPageCount',type: 'double' },//每日需完成
	        { name: 'finishSeriesno',type: 'double' }//完成集数
		],
		root: 'resultList'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) {
        	if(data.success) {
        		if(data.resultList && data.resultList.length>0 && !data.flag) {
        			showInfoMessage("拍摄日期未设置，请在“剧组设置”里设置");
        		}

        		var html=[];
        		html.push("拍摄周期为：");
        		if(data.shootStartDate) {
        			html.push(data.shootStartDate);
        		}
        		html.push("至");
        		if(data.shootEndDate){
        			html.push(data.shootEndDate);
        		}
        		html.push(" 共");
        		if(data.shootDate){
        			html.push(data.shootDate);
        		}
        		html.push("天，需累计拍摄");
        		if(data.viewTotalViewCount){
        			html.push(data.viewTotalViewCount);
        		}
        		html.push("场，");
        		if(data.viewTotalPageCount){
        			html.push(parseFloat(data.viewTotalPageCount).toFixed(1));
        		}
        		html.push("页，平均日拍摄");
        		if(data.everyDayPageCount){
        			html.push(parseFloat(data.everyDayPageCount).toFixed(1));
        		}
        		html.push("页");
        		html.push("（<font color='red'>注：将删戏计入已完成</font>）");
        		$("#totalDiv").html(html.join(""));
        	} else {
        		showErrorMessage(data.message);
        	}
        },
        loadError: function (xhr, status, error) { }
    });
	var width=100/14+'%';
	var gridcolumns=[
				    { text: '天数',datafield:'dayNum', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '日期',datafield:'noticeDate', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '计划完成',columngroup: 'viewCol',datafield:'planViewCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '实际完成',columngroup: 'viewCol',datafield:'realViewCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '累计完成',columngroup: 'viewCol',datafield:'totalViewCount', cellclassname:'divide_line',classname:'divide_line', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '计划完成',columngroup: 'pageCol',datafield:'planPageCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '实际完成',columngroup: 'pageCol',datafield:'realPageCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '累计完成',columngroup: 'pageCol',datafield:'totalPageCount', cellclassname:'divide_line',classname:'divide_line',  width: width, cellsAlign: 'center', align: 'center' },
				    { text: '平均完成场次',columngroup: 'avgCol',datafield:'avgViewCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '平均完成页数',columngroup: 'avgCol',datafield:'avgPageCount', cellclassname:'divide_line',classname:'divide_line',  width: width, cellsAlign: 'center', align: 'center' },
				    { text: '每天差额',columngroup: 'chaCol',datafield:'everyDayCha', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '累计差额',columngroup: 'chaCol',datafield:'totalDayCha', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '每日需完成',datafield:'everyDayPageCount', width: width, cellsAlign: 'center', align: 'center' },
				    { text: '完成集数',datafield:'finishSeriesno', width: width, cellsAlign: 'center', align: 'center' }
				];
	var columngroups=[
	                  { text: '场次', align: 'center', name: 'viewCol' },
	                  { text: '页数', align: 'center', name: 'pageCol' },
	                  { text: '平均数', align: 'center', name: 'avgCol' },
	                  { text: '差天数', align: 'center', name: 'chaCol' }
	                  ];
	$("#productionReportDiv").jqxGrid({
		theme:'bootstrap',
		width: "100%",
		height: "100%",
		source: dataAdapter,
		columns:gridcolumns,
		columngroups:columngroups,
		showToolbar: true,
		columnsResize: true,
		toolbarHeight: 35,
		rowsHeight:35,
		localization: localizationobj,
		rendertoolbar: function(toolbar) {							
			var container = [];
			container.push("<div class='toolbar'>");
			//container.push("<input type='button' class='return-btn' id='returnBtn'>");
			if(hasExportShootProduceAuth) {
				container.push("<input type='button' class='export-btn' id='exportBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			
			//$("#returnBtn").jqxTooltip({content: "返回", position: "bottom"});
			if(hasExportShootProduceAuth) {
				$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
			}
			
			//返回
			/*$("#returnBtn").on("click", function(){
				window.location.href="/notice/toNoticeList";
			});*/
			//导出分集汇总信息
			$("#exportBtn").on("click", function(){
				
				/*显示加载中*/
				var clientWidth=window.screen.availWidth;
				//获取浏览器页面可见高度和宽度
			    var _PageHeight = document.documentElement.clientHeight,
			        _PageWidth = document.documentElement.clientWidth;
			    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
			    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
			        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
			    //在页面未加载完毕之前显示的loading Html自定义内容
			    var _LoadingHtml = $("#loadingDiv");
			    
			    //呈现loading效果
			    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
			    _LoadingHtml.show();
			    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
				
			    $.ajax({
			    	url: '/viewStatisticManager/exportShootingProductionReport',
			    	type: 'post',
			    	data: {},
			    	datatype: 'json',
			    	success: function(response){
			    		
			    		_LoadingHtml.hide();
			            $(".opacityAll").hide();
			            
			    		if(response.success){
			    			fileAddress = response.downloadPath;
			        		fileName = response.fileName;
			    		}else{
			    			showErrorMessage(response.message);
			    			return;
			    		}
			    		var form = $("<form></form>");
			        	form.attr("action","/fileManager/downloadFileByAddr");
			            form.attr("method","post");
			            form.append("<input type='hidden' name='address'>");
			            form.append("<input type='hidden' name='fileName'>");
			            form.find("input[name='address']").val(fileAddress);
			            form.find("input[name='fileName']").val(fileName);
			            $("body").append(form);
						form.submit();
						form.remove();
			    	}
			    });  
			});
		},
	});
}