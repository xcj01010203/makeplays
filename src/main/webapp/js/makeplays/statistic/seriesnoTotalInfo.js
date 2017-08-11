$(document).ready(function() {
	loadSeriesnoTotalInfo();
});
//加载剧本分集汇总信息
function loadSeriesnoTotalInfo() {
	//加载source
	var source = {
		url: '/viewStatisticManager/querySeriesnoTotalInfo',
		type: "post",
		dataType : "json",
		data: {},
		datafields : [
			{ name: 'seriesNo',type: 'string' },//集数
			{ name: 'viewNum',type: 'int' },//场数
			{ name: 'finishedViewNum',type: 'int' },//完成场数
			{ name: 'pageNum',type: 'double' },//完成页数
			{ name: 'finishedPageNum',type: 'double' },//页数
	        { name: 'nightoutView',type: 'int' },//夜外场数
	        { name: 'noViewPer',type: 'double' },//夜外场数比重
	        { name: 'nightoutPage',type: 'double' },//夜外页数
	        { name: 'noPagePer',type: 'double' },//夜外页数比重
	        { name: 'nightView',type: 'int' },//夜景场数
	        { name: 'nViewPer',type: 'double' },//夜景场数比重
	        { name: 'nightPage',type: 'double' },//夜景页数
	        { name: 'nPagePer',type: 'double' },//夜景页数比重
	        { name: 'dayoutView',type: 'int' },//日内场数
	        { name: 'doViewPer',type: 'double' },//日内场数比重
	        { name: 'dayoutPage',type: 'double' },//日内页数
	        { name: 'doPagePer',type: 'double' },//日内页数比重
	        { name: 'dayView',type: 'int' },//日景场数
	        { name: 'dViewPer',type: 'double' },//日景场数比重
	        { name: 'dayPage',type: 'double' },//日景页数
	        { name: 'dPagePer',type: 'double' }//日景页数比重
		],
		root: 'seriesnoList'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) {
        	if(data.success) {
        		//合计、平均
        		var html=[];
        		var totalaverage=data.totalaverage;
        		if(totalaverage){
        			var width=100/19+'%';
        			html.push('<tr>');
        			html.push('<td style="width:'+ width +';">合计</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.finishedViewNum+'/'+totalaverage.viewNum+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.finishedPageNum+'/'+totalaverage.pageNum+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightoutView+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.noViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightoutPage+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.noPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightView+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightPage+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.nPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayoutView+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.doViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayoutPage+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.doPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayView+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayPage+'</td>');
        			html.push('<td>'+totalaverage.dPagePer+'</td>');
        			html.push('</tr>');
        			html.push('<tr>');
        			html.push('<td style="width:'+ width +';">平均</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.viewAvg+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.pageAvg+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightoutViewAvg+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.noViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightoutPageAvg+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.noPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightViewAvg+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.nightPageAvg+'</td>');
        			html.push('<td class="divide_line" style="width:'+ width +';">'+totalaverage.nPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayoutViewAvg+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.doViewPer+'</td>');
        			html.push('<td>'+totalaverage.dayoutPageAvg+'</td>');
        			html.push('<td class="divide_line">'+totalaverage.doPagePer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayViewAvg+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dViewPer+'</td>');
        			html.push('<td style="width:'+ width +';">'+totalaverage.dayPageAvg+'</td>');
        			html.push('<td>'+totalaverage.dPagePer+'</td>');
        			html.push('</tr>');
        			$("#totalTable").empty();
        			$("#totalTable").append(html.join(''));
        		}
        	} else {
        		showErrorMessage(data.message);
        	}
        },
        loadError: function (xhr, status, error) { }
    });
	var width=100/19+'%';
	var gridcolumns=[
				    { text: '集',datafield:'seriesNo', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center'},
				    { text: '完成/场数',datafield:'viewNum', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							return '<div class="colstyle">'+rowdata.finishedViewNum+'/'+rowdata.viewNum+'</div>';
				        }	
			        },
				    { text: '完成/页数',datafield:'pageNum', width: width, cellclassname: cellclass, classname:'divide_line',cellsAlign: 'center', align: 'center',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
							return '<div class="colstyle">'+rowdata.finishedPageNum+'/'+rowdata.pageNum+'</div>';
				        }	
			        },
				    { text: '场数',columngroup: 'nightoutCol',datafield:'nightoutView', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'nightoutCol',datafield:'noViewPer', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '页数',columngroup: 'nightoutCol',datafield:'nightoutPage', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'nightoutCol',datafield:'noPagePer', width: width, cellclassname: cellclass, classname:'divide_line',cellsAlign: 'center', align: 'center' },
				    { text: '场数',columngroup: 'nightCol',datafield:'nightView', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'nightCol',datafield:'nViewPer', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '页数',columngroup: 'nightCol',datafield:'nightPage', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'nightCol',datafield:'nPagePer', width: width, cellclassname: cellclass, classname:'divide_line',cellsAlign: 'center', align: 'center' },
				    { text: '场数',columngroup: 'dayoutCol',datafield:'dayoutView', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'dayoutCol',datafield:'doViewPer', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '页数',columngroup: 'dayoutCol',datafield:'dayoutPage', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'dayoutCol',datafield:'doPagePer', width: width, cellclassname: cellclass, classname:'divide_line',cellsAlign: 'center', align: 'center' },
				    { text: '场数',columngroup: 'dayCol',datafield:'dayView', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'dayCol',datafield:'dViewPer', width: width,cellclassname: cellclass, cellsAlign: 'center', align: 'center' },
				    { text: '页数',columngroup: 'dayCol',datafield:'dayPage', width: width, cellclassname: cellclass,cellsAlign: 'center', align: 'center' },
				    { text: '比重(%)',columngroup: 'dayCol',datafield:'dPagePer', width: width,cellclassname: cellclass, cellsAlign: 'center', align: 'center' }
				];
	var columngroups=[
	                  { text: '夜外', align: 'center', name: 'nightoutCol',datafield:'nightoutCol' },
	                  { text: '夜内', align: 'center', name: 'nightCol',datafield:'nightCol' },
	                  { text: '日外', align: 'center', name: 'dayoutCol',datafield:'dayoutCol' },
	                  { text: '日内', align: 'center', name: 'dayCol',datafield:'dayCol' }
	                  ];
	$("#seriesnoDiv").jqxGrid({
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
			//判断权限
			if(hasExportSeriesTotalAuth) {
				container.push("<input type='button' class='export-btn' id='exportBtn'>");
			}
			container.push("</div>");
			toolbar.append($(container.join("")));
			if(hasExportSeriesTotalAuth) {
				$("#exportBtn").jqxTooltip({content: "导出", position: "bottom"});
			}
			
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
			    	url: '/viewStatisticManager/exportSeriesnoTotalInfo',
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
var cellclass = function (row, columnfield, value,defaultvalue) {
	var returnstr=[];
	if(defaultvalue.seriesNo=='合计' || defaultvalue.seriesNo=='平均'){
		returnstr.push("praybackground"); 
	}
	if(columnfield=="pageNum" || columnfield=="noPagePer" || columnfield=="nPagePer" || columnfield=="doPagePer") {
		returnstr.push("divide_line");
	}
	return returnstr.join(" ");
}