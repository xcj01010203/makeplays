//新表格对象
var grid;
//是否刷新统计
var summaryFlag=true;

var source;
var gridColumns=new Array();
var viewContentRowNo;   //在场景内容面板上显示的场景在场景表格中的行号

//所有角色列表
var roleArray;

var rendergridrows = null;
//高级查询弹出层
var popupLayer;

var filter={};
var crewName = null;
var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];
var crewType = null;
//已有通告单数据列表
var noticeResultData = null;

//选中的剧本内容
var selecationText ="";
//隐藏的td的index值
var hideTdIndex = [];
var hideTdText = [];

$(document).ready(function() {
	 topbarInnerText("拍摄管理&&场景表");
	//获取剧组类型
	getCrewType();
	
	//初始化场景内容框
	initViewContent();
	
	//初始化右侧场景信息窗口的格式及大小
	initAdvertContent();
	
	//初始化剧本信息框,并加载剧本框绑定的事件
	loadViewContentWindow();
	
	
	//加载隐藏列的信息
	loadHideColumnInfo();
	//初始化场景列表的布局
	initViewListSpilter();
	
	//初始化右键菜单文本框
	initRightContent();
	
	//页面禁用右键菜单
    $("#viewContentDIV").on('contextmenu', function (e) {
        return false;
    });
    
	//面板右侧场景框中确定/取消/删除 按钮样式初始化
    initViewButton();
    
    //初始化剧本上传页面窗口,并跳转到剧本上传页面
    loadScenarioUpload();
    
    //初始化上传结果界面
    loadUploadResultWin();
    
    //对计划文本框中的时间进行赋值
    initPlanDate();
    
    //初始化智能合并主场景窗口
    initAutoViewWindown();
    
    /***********************上下场操作按钮start*************************/
  /*  $("#openClose").on('click', function (event) {
        var _this = $(event.target);
        if (_this.hasClass('open')) {
        	$("#btn_list").animate({right:30},"normal");
        	
            _this.removeClass('open');
            _this.addClass('close');
            _this.attr('title', '展开');
        } else if (_this.hasClass('close')) {
        	$("#btn_list").animate({right:100},"normal");
        	
            _this.removeClass('close');
            _this.addClass('open');
            _this.attr('title', '隐藏');
        }
    });*/
    /***********************上下场操作按钮end*************************/
    

	//导入窗口
	initImportWin();
	
	//初始化导出窗口
	initExportWindow();
    
	$(document).on("click", function(){
		$("#columnsPanel").slideUp(200);
	});
});


//加载隐藏列的信息
function loadHideColumnInfo(){
	$.ajax({
		url: '/cacheManager/queryCacheInfo',
		type: 'post',
		data: {"type": 1},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var result = response.result;
				if(result != null){
					hideTdText = result.content.split(",");
				}
				hideTdIndex = [];
				//获取跳转到场景列表之后需要加载的列表数据
				loadListDta();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}



//生成场景表列表
function loadViewListTable(majorRoleList){
	//初始化roleArray数据
	roleArray= [];
	for(var i= 0; i < majorRoleList.length; i++){
		var role = majorRoleList[i];
		roleArray.push({text: role.viewRoleName, value: role.viewRoleId});
	}
	
	//加载source
    source =
    {
        datatype: "json",
        datafields: [
			{ name: 'viewId,',type: 'string' },//场景id
			{ name: 'seriesNo,',type: 'int' },//集次编号
			{ name: 'viewNo,',type: 'string' },//场次编号
	        { name: 'specialRemind',type: 'string' },//特殊提醒
	        { name: 'atmosphereId',type: 'int' },//气氛
	        { name: 'site',type: 'string' },//内外景
	        { name: 'shootLocation',type: 'string' },//拍摄地点
	        { name: 'majorView',type: 'string' },//主场景
	        { name: 'minorView',type: 'string' },//次场景
	        { name: 'thirdLevelView',type: 'string' },//三级场景
	        { name: 'mainContent',type: 'string' },//主要内容
	        { name: 'pageCount',type: 'string' },//页数
	        { name: 'guestRoleList',type: 'string' },//特约演员
	        { name: 'massRoleList',type: 'string' },//群众演员
	        { name: 'propsList',type: 'string' },//普通道具
	        { name: 'specialPropsList',type: 'string' },//特殊道具
	        { name: 'clothesName',type: 'string' },//服装
	        { name: 'makeupName',type: 'string' },//化妆
	        { name: 'shootDate',type: 'string' },//拍摄时间
	        { name: 'remark',type: 'string' },//备注
	        { name: 'shootStatus',type: 'string' },//拍摄状态
	        { name: 'advertName',type: 'string' }//商植
                ],
        type:'post',
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	//全局变量赋值
        	source.totalrecords=data.result.total;
        },
        root:'resultList',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        url:'/viewManager/loadViewList'
    };
	
    //加载角色列信息
    roleColumn = function (columnfield, value, columnproperties, rowdata) {
    	//操作列html
    	var thisRowData = rowdata;
    	
    	var roleArray = thisRowData.roleList;
    	for(var i=0;i<roleArray.length;i++){
    		if(columnproperties.text==roleArray[i].viewRoleName){
    			var roleNum = roleArray[i].roleNum;
    			if (roleNum == 0) {
    				return "OS";
				}else {
					if(roleArray[i].shortName==null || roleArray[i].shortName.trim() == ""){
						return "√";
					}else{
						return roleArray[i].shortName;
					}
				}
    		}
    	}
    	return "";
    };
    
    //加载集次编号信息
    viewColumn = function(columnfield, value, columnproperties, rowdata){
		var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
    	return "<span style='cursor:pointer;color:#52b0cc;'  class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' onclick='showViewContent(\""+rowdata.viewId+"\",\""+rowdata.viewId+"\")'>" + seriesNoAndViewNo + "</span>";
    };
    
    //加载场次编号
    viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
        var viewNo =rowdata.viewNo;
        return "<span style='cursor:pointer;color:#52b0cc;' class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' onclick='showViewContent(\""+rowdata.viewId+"\",\""+rowdata.viewId+"\")'>" + viewNo + "</span>";
    };
    
    rendergridrows = function (params) {
    	//调用json返回的列表数据
        return params.data;
    };
    
    //加载气氛信息
    atmosphere=function(columnfield, value, columnproperties, rowdata){
    	var atmosphere= rowdata.atmosphereName;
    	if(atmosphere==null){
    		atmosphere="";
    	}
    	return atmosphere;
    };
    
    //加载特殊提醒
    seasonColumn=function(columnfield, value, columnproperties, rowdata){
    	var seasonText= '';
    	if(rowdata.specialRemind !=null && rowdata.specialRemind != undefined){
    		seasonText=rowdata.specialRemind;
    	}
    	return "<span title='"+ seasonText +"'>" + seasonText + "</span>";
    };
    
    //加载内外景信息
    siteColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.site){
    		text=rowdata.site;
    	}
    	return text;
    };
    
    //加载主场景信息
    majorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.majorView){
    		text=rowdata.majorView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载次场景信息
    minorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.minorView){
    		text=rowdata.minorView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载三级场景信息
    thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.thirdLevelView){
    		text=rowdata.thirdLevelView;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载主要内容信息
    mainContentColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.mainContent){
    		text=rowdata.mainContent;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载页数信息
    pageCountColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.pageCount){
    		text=rowdata.pageCount;
    	}
    	return text;
    };
    
    //加载特约演员信息
    guestRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.guestRoleList){
    		text=rowdata.guestRoleList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载主要演员信息
    massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.massRoleList){
    		text=rowdata.massRoleList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载道具信息
    propsListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.propsList){
    		text=rowdata.propsList;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载服装信息
    clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.clothesName){
    		text=rowdata.clothesName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载化妆信息
    makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.makeupName){
    		text=rowdata.makeupName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载拍摄日期信息
    shootDateColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootDate){
    		text=rowdata.shootDate;
    	}
    	return text;
    };
    
    //加载备注信息
    remarkColumn= function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.remark){
    		text=rowdata.remark;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //加载拍摄地点信息
    shootLocationColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootLocation){
    		text=rowdata.shootLocation;
    	}
    	if(rowdata.shootRegion){
    		text+="("+rowdata.shootRegion +")";
    	}
    	return "<span title='" +text + "'>" + text + "</span>";
    };
    
    //加载拍摄状态信息
    shootStatusColumn=function(columnfield, value, columnproperties, rowdata){
    	var shootStatusText = shootStatusMap.get(rowdata.shootStatus);
    	if(shootStatusText==null){
    		shootStatusText="";
    	}
    	return shootStatusText;
    };
    
    //加载广告信息
    advertNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.advertName){
    		text=rowdata.advertName;
    	}
    	return "<span title='"+ text +"'>" + text + "</span>";
    };
    
    //设置每行数据的格式
    gridColumns=[{ width: '40px',isCheckbox:true }];
    
    //电影类型的剧组不显示集次
    if (crewType == Constants.CrewType.movie || crewType == 3) {
      gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn, width: '65px' ,pinned: true});
    } else {
      gridColumns.push({ text: '集-场', cellsrenderer: viewColumn, width: '65px' ,pinned: true});
    }
    //加载数据列,拼接场景列表的表头信息
    gridColumns.push(
			{ text: '特殊提醒', cellsrenderer: seasonColumn, width: '60px'},
            { text: '气氛',cellsrenderer: atmosphere, width: '40px', style:'padding-left: 0px;text-align:center;'},
            { text: '内外景',cellsrenderer:siteColumn, width: '40px', style:'padding-left: 0px;text-align:center;'},
            { text: '拍摄地点', cellsrenderer: shootLocationColumn, width: '120px' },
            { text: '主场景',cellsrenderer:majorViewColumn, width: '120px' },
            { text: '次场景',cellsrenderer:minorViewColumn , width: '120px' },
            { text: '三级场景',cellsrenderer: thirdLevelViewColumn, width: '120px' },
            { text: '主要内容',cellsrenderer: mainContentColumn, width: '120px' },
            { text: '页数',cellsrenderer: pageCountColumn, width: '40px'}
          );
    //暂时显示所有的主演信息列表
   	if(false){
   		//此处的木得是只显示查询的主演角色列表信息,但是目前要求是显示所有的主演数据
   		//此处做处理动态列
       var showRoleArray = filter.roles.split(",");
       var showRoleMap=new HashMap();
       showRoleMap.clear();
       //要显示的角色
       for(var i=0;i<showRoleArray.length;i++){
          showRoleMap.put(showRoleArray[i],showRoleArray[i]);
       }
       //循环所有角色，值保留要显示的角色
       for(var i =0;i<roleArray.length;i++){
       	
           if(showRoleMap.get(roleArray[i].value)!=null&&(typeof(filter.searchMode)=="undefined"||filter.searchMode!="2")){
           	//查询方式为必须同时出现或者出现即可
           	gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '50px',isRoleColumn:true });
           }else if(showRoleMap.get(roleArray[i].value)==null&&typeof(filter.searchMode)!="undefined"&&filter.searchMode=="2"){
           	//查询方式为必须不可同时出现
           	gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '50px',isRoleColumn:true });
           }
       }
   }else{
   	//没有过滤条件时，显示所有列
   	for(var i = 0; i<roleArray.length; i++){
           gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '20px',isRoleColumn:true });
       }
   }
	gridColumns.push(
 	   { text: '特约演员',cellsrenderer: guestRoleListColumn, width: '90px' },
       { text: '群众演员',cellsrenderer: massRoleListColumn, width: '90px' },
       { text: '服装',cellsrenderer: clothesNameColumn,width: '90px' },
       { text: '化妆',cellsrenderer: makeupNameColumn, width: '90px' },
       { text: '道具',cellsrenderer: propsListColumn, width: '90px' },
       { text: '特殊道具',filedName: "specialPropsList", width: '90px' },
       { text: '备注',cellsrenderer: remarkColumn, width: '90px' },
       { text: '商植', cellsrenderer: advertNameColumn, width: '120px' },
       { text: '拍摄时间',cellsrenderer: shootDateColumn, width: '90px' },
       { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: '90px' }
   );
	
	var items = [];
	//生成隐藏列面板的所有项目名
	for(var k= 0; k< gridColumns.length; k++){
		if((k != 0) && (k != 1)){
			items.push('<li title="'+ gridColumns[k].text +'"><label>');
			if(jQuery.inArray(gridColumns[k].text, hideTdText) == -1){
				items.push('<input type="checkbox" checked onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].text);
			}else{
				items.push('<input type="checkbox" onclick="showOrHideColumn(this)" td-index="'+ k +'">' + gridColumns[k].text);
			}
			
			items.push('</label></li>');
		}
	}
	$("#columnsPanelItem").append(items.join(""));
	
	
	//生成表格头部查询条件
	rendertoolbar = function (toolbar) {
    	if($("#searchDIV")[0]){
    		return;
    	}
        var container = $("<div class='toolbar' id='searchDIV'></div>");
        //生成头部查询条件
        var html="<span class='badge' id='_already_selected' style=' margin-top: 8px;'>0</span>";
        html+="<input type='button' value='' id='addViewButton' onclick='addNewView()'>";
      //批量修改场景
        html+="<input type='button' value='' id='batchUpdateView' onclick='batchUpdateViewInfo()' >";
        html+="<input type='button' id='exportButton'  value='' onclick='downloadExcel();'>";
        html+="<input type='button' class='daoru_button' id='importButton'  value='' onclick='importExcel();'>";
        html+="<input type='button' value='' id='noticeWindowButton' onclick='addViewToNotice()'>";
        
        html+= "<div class='split-line'></div>";
        
        html+="<input type='button' value='' id='superSearchButton' onclick='superSearchViewInfo()'>";
      //隐藏列按钮
        html+="<input type='button' value='' id='hideGridColumns' onclick='showColumnsPanel(this, event)'>";
        //打印按钮
        html+="<input type='button' value='' id='printViewList' onclick='printViewList()'>";
        html+="<div class='btn-group btn-group-xs' role='group' style='margin-top: 7px;'>";
        html+="<button type='button' id='smoothView' class='btn btn-primary' role='group' style='height:24px;' onclick='queryShunView(this)'>顺场</button>";
        html+="<button type='button' id='groupView' class='btn btn-default' role='group' style='height:24px;' onclick='queryFenView(this)'>分场</button>";
        html+="</div>";
        //显示/隐藏剧本
        html+="<input class='hide-scenario scenario-button' ishide='0' type='button' id='showOrHideScenarioBtn' onclick='showOrHideScenario(this)' value='看剧本'>";
        
        
        html+="<input type='button' value='' id='planWindowButton'  style='display:none;' onclick='addViewToPlan()'>";
        html+="<input type='button' value='' id='uploadScenarioButton' onclick='uploadScenario()' style='display:none;' >";
        
//        html+="<input type='button' id='setAddress' onclick='setViewLoocation()'>";
//        //统一时空(合并场景)
//        html+="<input type='button' value='' id='coalitionViewLocation' onclick='showAutoSetWindow()' >";
        
        html+="<input type='button' style='float:right'  value='' id='viewColorExample' class='colorExample3'>";
        
        toolbar.append(container);
        container.append(html);
        
        //初始化表头按钮
        $("#superSearchButton").jqxTooltip({ content: '高级搜索', position: 'bottom', autoHide: true, name: 'movieTooltip'});
//        $("#setAddress").jqxTooltip({ content: '设置拍摄地', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#importButton").jqxTooltip({ content: '导入顺场表', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#exportButton").jqxTooltip({ content: '导出', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#noticeWindowButton").jqxTooltip({ content: '添加到通告单', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#planWindowButton").jqxTooltip({ content: '添加到拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#addViewButton").jqxTooltip({ content: '添加场景', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#uploadScenarioButton").jqxTooltip({ content: '上传剧本', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#importButton").jqxTooltip({ content: '导入', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#batchUpdateView").jqxTooltip({ content: '批量修改', position: 'bottom', autoHide: true, name: 'movieTooltip'});
//        $("#coalitionViewLocation").jqxTooltip({ content: '统一场景', position: 'bottom', autoHide: true, name: 'movieTooltip'});
        $("#hideGridColumns").jqxTooltip({ content: '隐藏列', position: 'bottom', autoHide: true, name: 'movieToolTip'});
        $("#printViewList").jqxTooltip({ content: '打印预览', position: 'bottom', autoHide: true, name: 'movieToolTip'});
        
        //初始化场景列表表头的按钮样式
        initTopButtonCss();
        
        //
        if(isViewInfoReadonly) {
        	$("#setAddress").remove();
	       	$("#noticeWindowButton").remove();
	        $("#planWindowButton").remove();
	        $("#addViewButton").remove();
	        $("#uploadScenarioButton").remove();
	        $("#importButton").remove();
	        $("#batchUpdateView").remove();
	        $("#coalitionViewLocation").remove();
	        $("#hideGridColumns").remove();
        }
        if(!hasImportViewInfoAuth) {
        	$("#importButton").remove();
        }
        if(!hasExportViewInfoAuth) {
        	$("#exportButton").remove();
        }
        
        //初始化设置拍摄地点窗口
        initSetAddressWindow();
	};
	
	//开始加载表格数据
	grid = new SimpleGrid("jqxgrid","/viewManager/loadViewList",100,0,gridColumns,filter,"resultList",rendertoolbar);
	grid.loadTable();
}

function importExcel() {
	$("#importViewWin").jqxWindow("open");
	if (crewType == Constants.CrewType.movie || crewType == 3) {
		templateUrl= "/template/import/场景表（电影）.xlsx";
	} else {
		templateUrl= "/template/import/场景表（电视剧）.xlsx";
	}
	$("#importIframe").attr("src", "/importManager/toImportPage?isCompareData=true&&uploadUrl=/viewManager/importViewInfo&&needIsCover=true&&refreshUrl=/viewManager/toviewListPage&&templateUrl="+ templateUrl);
}
function initImportWin() {
	$("#importViewWin").jqxWindow({
		theme: theme,
		height: 540,
		width: 482,
		resizable: false,
		isModal: true,
		autoOpen: false,
		initContent: function(){
			
		}
	});
}

//关闭导入窗口
function closeImportWin(){
	$("#importViewWin").jqxWindow("close");
}

function compareData(){
	$.ajax({
		url : '/scenarioManager/hasSkipOrReplaceData',
		type : 'post',
		data : {},
		async : false,
		success : function(respone) {
			if (respone.success) {
				if (respone.hasSkipOrReplaceData) {
					showResultWindow();
				}else{
					window.location.reload(true);
				}
			} else {
				showErrorMessage(respone.message);
			}
		},
		failure : function() {

		}
	});
}


/*//下载导入模板
function downloadImportTemplate() {
	alert(1);
	if (crewType == Constants.CrewType.movie) {
		window.location.href = basePath + "/template/import/场景表（电影）.xlsx";
	} else {
		window.location.href = basePath + "/template/import/场景表（电视剧）.xlsx";
	}
}*/

//初始化剧本上传引导页面
function initUploadGuideWindow(){
	var $container = $("#step-container");
    var maxStep = 8;
    $container.on("click", ".btn-close", function(){
        closeGuide($container);
        
    }).on("click", ".btn-next", function (e) {
        var $parent = $(this).parent(),
            step = $parent.data("step");

        if(step < maxStep){
            var next = step + 1;
            gotoNext($parent, step, next);
        }else{
            closeGuide($container);
        }
        
    }).on("click", ".btn-step", function(){
        var $el = $(this),
            index = $el.index(),
            $step = $el.parents(".step:eq(0)");

        gotoNext($step, $step.data("step"), index + 1);
    });
}

//点击下一步
function gotoNext($el, curStep, newStep){
    $el.removeClass("step" + curStep).addClass("step" + newStep).data("step", newStep);
}

//展示引页面
function showGuidePage() {
    var $container = $("#step-container");
    $container.fadeIn();
    
    $container.find(".step").removeClass().addClass("step step1").data("step", 1);
}

//关闭引导页面
function closeGuide($container){
    $container.fadeOut();
}

//加载上传结果页面
function loadUploadResultWin() {
  $("#uploadResultWindow").jqxWindow({
      theme:theme,  
      /*width: 1020,*/
      width: 620,
      height: 740, 
      autoOpen: false,
      maxWidth: 2000,
      maxHeight: 1500,
      resizable: false,
      isModal: true,
      showCloseButton: false,
      initContent: function() {
         //集-场号面板
        $.ajax({
            url: "/scenarioManager/queryUploadResultData",
            dataType: "json",
            type: "post",
            async: true,
            success: function(response) {
                if (response.success) {
                    var skipOrReplaceSceMap = response.skipOrReplaceSceMap;
                    var $containter = $("#viewNoResult");
                    
                    $.each(skipOrReplaceSceMap, function(key,values){ 
						var seriesnoArray = ["<div class='result-series-viewno'>"];
						
						//电影类型的剧组不现实集次
						if (crewType == Constants.CrewType.movie || crewType == 3) {
						    seriesnoArray.push("<div class='result-seriesno'><input type='checkbox'>全选</div>");
						} else {
						    seriesnoArray.push("<div class='result-seriesno'><input type='checkbox'>第" + key + "集</div>");
						}
						
						$.each(values ,function(index, item) {
						    if (crewType == Constants.CrewType.movie || crewType == 3) {
						        seriesnoArray.push("<div class='result-viewno'><input type='checkbox' value=" + key + "-" + item + "><span>" + item + "</span></div>");
						    } else {
						        seriesnoArray.push("<div class='result-viewno'><input type='checkbox' value=" + key + "-" + item + "><span>" + key + "-" + item + "</span></div>");
						    }
                        });
                        
                        seriesnoArray.push("</div>");
                        
                        $containter.append(seriesnoArray.join(""));
					});
                    
                    //初始化点击对比页面的集场号是触发的方法
                    $containter.on("click", ".result-viewno span", function() {
                      var seriesViewno = $(this).prev("input:checkbox").val();
                      $("#viewCompare").attr("src", "/viewManager/toScenarioComparePage?seriesViewNo=" + seriesViewno+"&&isViewList=true");
                      
                      $(this).prev(":checkbox").prop("checked", true);
                      checkIsCheckedAll($(this).prev(":checkbox"));
                      
                    }).on("click", ".result-seriesno :checkbox", function(ev) {
                      if (this.checked) {
                          $(this).parent("div").siblings("div .result-viewno").find(":checkbox").prop("checked", true);
                      } else {
                          $(this).parent("div").siblings("div .result-viewno").find(":checkbox").prop("checked", false);
                      }
                      
                      ev.stopPropagation();
                    }).on("click", ".result-viewno :checkbox", function() {
                      checkIsCheckedAll($(this));
                      
                    }).on("click", ".result-seriesno", function() {
                      if ($(this).hasClass("up")) {
                          $(this).removeClass("up");
                          $(this).siblings(".result-viewno").slideDown();
                          
                      } else {
                          $(this).addClass("up");
                          $(this).siblings(".result-viewno").slideUp();
                      }
                    });
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
      }
  });
  
  //关闭上传结果页面
  $("#uploadResultWindow").on("close", function() {
	  window.location.reload(true);
  });
}

//点击取消按钮
function confirmCancelDealResult(){
	var $containter = $("#viewNoResult");
    var existViewNo = $containter.find(".result-viewno");
    if (existViewNo.length > 0) {
        popupPromptBox("提示", "关闭后未处理的数据会默认执行\"跳过\"操作，是否继续？", function() {
        	//拼接场景号字符串
        	var seriesViewNoStr = "";
        	for(var i=0; i<existViewNo.length; i++){
        		if (i == 0) {
        			seriesViewNoStr = $(existViewNo[i]).children().text();
				}else {
					seriesViewNoStr = seriesViewNoStr + "," + $(existViewNo[i]).children().text();
				}
        	}
            cancelOperate(seriesViewNoStr);
        });
    } else {
        cancelOperate("");
    }
}

//点击剧本对比页面的跳过按钮
function confirmSkip(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    if (checkedBoxs.length == 0) {
        showErrorMessage("请选择需要跳过的场次");
        return false;
    }
    //从数据库中移除选中的场景
    var checkSeriesViewNoStr = "";
    for(var i =0; i<checkedBoxs.length; i++){
    	if (i == 0) {
    		checkSeriesViewNoStr = $(checkedBoxs[i]).val();
		}else {
			checkSeriesViewNoStr = checkSeriesViewNoStr + "," + $(checkedBoxs[i]).val();
		}
    }
    
    $.ajax({
        url: "/viewManager/cancelOperate",
        dataType: "json",
        async: false,
        data:{seriesViewNoStr: checkSeriesViewNoStr},
        success: function(response) {
        	if (!response.success) {
        		showErrorMessage(response.message);
			}
        }, 
        failure: function() {
            showErrorMessage("网络故障");
        }
    });
    
    $containter.find(":checked").parent("div").remove();
    showSuccessMessage("操作成功");
    
    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
    
    if ($containter.find(".result-series-viewno").find("div").length == 0) {
    	 window.location.href="/viewManager/toviewListPage";
    }
}

//点击对比界面的只替换剧本按钮
function confirmReplaceSec(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    
    var seriesViewNos = "";
    $.each(checkedBoxs, function(index) {
        if(index == 0){
            seriesViewNos = $(this).val();
        } else {
            seriesViewNos += ","+$(this).val();
        }
    });
    
    if (seriesViewNos == "") {
        showErrorMessage("请勾选需要替换的场次");
        return false;
    }
    
    popupPromptBox("提示", "是否确定只将所选场次的剧本替换为新的内容？", function() {
        $.ajax({
            url: "/viewManager/replaceSecBatch",
            type: "post",
            data: {seriesViewNoStr: seriesViewNos},
            dataType: "json",
            async: false,
            success: function(response) {
                if (response.success) {
                    $containter.find(":checked").parent("div").remove();
                    showSuccessMessage(response.message);
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                    if ($containter.find(".result-series-viewno").find("div").length == 0) {
                    	 window.location.href="/viewManager/toviewListPage";
                    }
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
    });
}

//点击对比界面的全替换按钮
function confirmReplaceAll(){
	var $containter = $("#viewNoResult");
    var checkedBoxs = $containter.find(".result-viewno :checked");
    
    var seriesViewNos = "";
    $.each(checkedBoxs, function(index) {
	    if(index == 0){
	        seriesViewNos = $(this).val();
	    } else {
	        seriesViewNos += ","+$(this).val();
	    }
	});
	
	if (seriesViewNos == "") {
	    showErrorMessage("请勾选需要替换的场次");
	    return false;
	}
	
	popupPromptBox("提示", "是否确定将所选场次的场景全部替换为新的内容？", function() {
	    $.ajax({
            url: "/viewManager/replaceViewBatch",
            type: "post",
            data: {seriesViewNoStr: seriesViewNos},
            dataType: "json",
            async: false,
            success: function(response) {
                if (response.success) {
                    $containter.find(":checked").parent("div").remove();
                    showSuccessMessage(response.message);
                    
                    $containter.find(".result-series-viewno").eq(0).find(".result-viewno").eq(0).find("span").click();
                    if ($containter.find(".result-series-viewno").find("div").length == 0) {
                    	 window.location.href="/viewManager/toviewListPage";
                    }
                } else {
                    showErrorMessage(response.message);
                }
            }
        });
	});
}

//处理上传结果时取消操作
function cancelOperate(seriesViewNoStr) {
    $.ajax({
        url: "/viewManager/cancelOperate",
        dataType: "json",
        async: true,
        data:{seriesViewNoStr: seriesViewNoStr},
        success: function(response) {
        	if (response.success) {
        		 showSuccessMessage(response.message);
			}else{
				showErrorMessage(response.message);
			}
        }, 
        failure: function() {
            showErrorMessage("网络故障");
        }
    });
    window.location.href="/viewManager/toviewListPage";
}

//判断是否全选中
function checkIsCheckedAll($this) {
    var totalLength = $this.parents("div .result-series-viewno").find(".result-viewno").length;
    var checkedLength = $this.parents("div .result-series-viewno").find(".result-viewno").find(":checked").length;
    if (totalLength == checkedLength) {
        $this.parents("div .result-series-viewno").find(".result-seriesno").find(":checkbox").prop("checked", true);
    } else {
        $this.parents("div .result-series-viewno").find(".result-seriesno").find(":checkbox").prop("checked", false);
    }
}

//初始化添加到通告单窗口
function initAddViewToNoticeWin(groupList){
	var groupSource = [];
    for(var key in groupList){
    	groupSource.push({text: groupList[key], value: key});
    }
    groupSource.push({text:'新增组',value:'99'});
	                    
   $('#customWindow').jqxWindow({
    theme: theme,
    width: 640,
    maxHeight: 1600,
    height: 420,
    resizable: false,
    autoOpen: false,
    isModal: true,
    title: '添加到通告单',
    cancelButton: $('.noticeCancelButton'),
    initContent: function () {
    	//初始化通告单中各种组件的样式
    	initNoticeCss();
    	
        /* 新建通告单 */
        $("#noticeTime").jqxInput({theme: theme});
        $("#noticeNameInput").jqxInput({theme: theme});
        //初始化分组下拉框的数据
        $("#group").jqxDropDownList({
            theme:theme,selectedIndex:0,source: groupSource, displayMember: "text", valueMember: "value", width: '300px', height: 30,placeHolder: ""
                ,dropDownHeight: getHeight(groupSource)
        });
        
        //验证通告单名称不能为空
        $('#noticeForm').jqxValidator({
            animationDuration: 1,
            rules: [
              { input: '#noticeNameInput', message: '通告单名称不可为空!', action: 'keyup,blur', rule: 'required' }
            ]
        });
        
        
        /* 已有通告单 */
        //加载已有通告单的数据列表
        loadHasNoticeGrid();
        
       //解决第一次加载窗口，因为控件都没有初始化完成导致赋值不成功问题
       $("#group").jqxDropDownList('selectIndex', 0);
       $("#noticeTime").val(new Date().Format('yyyy-MM-dd'));
       autoGetNoticeName();
      }
   });
}

//将场景信息添加到已有通告单中
function confirmAddToNotice(){
	 //获取选中的拍摄计划ID
    var noticeGridRowIndexes = $('#existNoticeGrid').jqxGrid('getselectedrowindexes');
    if(noticeGridRowIndexes.length == 0){
        showErrorMessage("请选择通告单");
        return;
    }
    if (noticeGridRowIndexes.length > 1) {
		showErrorMessage("只能选择一条通告单");
		return;
    }
    
    var noticeRows="";
    for(var i = 0; i < noticeGridRowIndexes.length; i++){
        var resultrow = noticeResultData.resultList[noticeGridRowIndexes[i]];
        noticeRows += resultrow.noticeId +",";
    }
    noticeRows = noticeRows.substring(0, noticeRows.length - 1);
    
    //获取选中的场景IDs
    if(grid.getSelectIds()==""){
        showErrorMessage("请选择场次");
        return;
    }
    var viewRows=grid.getSelectIds();
    
    
    //取出请假信息
    //判断当前场景中是否有演员请假
	$.ajax({
		url:"/notice/checkIsLeave",
		data:{viewIds:grid.getSelectIds(),noticeId:noticeRows},
		dataType:"json",
		type:'post',
		async:false,
		success:function(data){
				if(!data.success){
					showErrorMessage(data.message);
				}else{
					var leaveInfo = data.leaveInfo;
					if (leaveInfo != null && leaveInfo != '' && leaveInfo != 'undefined') {
						
						doubleCallBackFun("是否重新选择", leaveInfo,"重新选择", "添加到通告单", null, function () {
						    $.ajax({
						        url: "/notice/addNoticeView",
						        type: 'post',
						        data: {'noticeId': noticeRows, 'viewIds': viewRows},
						        dataType: 'json',
						        async: false,
						        success: function (response) {
						            if (response.success) {
						                showSuccessMessage(response.message);
						                $('#customWindow').jqxWindow('close');
						                grid.unSelectedAll();
						                $('#existNoticeGrid').jqxGrid('clearselection');
						            } else {
						                   showErrorMessage(response.message);
						            }
						        },
						        error: function () {
						            showErrorMessage("发送请求失败");
						        }
						    });
						});
						  
					}else {
						  
					    $.ajax({
					        url: "/notice/addNoticeView",
					        type: 'post',
					        data: {'noticeId': noticeRows, 'viewIds': viewRows},
					        dataType: 'json',
					        async: false,
					        success: function (response) {
					            if (response.success) {
					                showSuccessMessage(response.message);
					                $('#customWindow').jqxWindow('close');
					                grid.unSelectedAll();
					                $('#existNoticeGrid').jqxGrid('clearselection');
					            } else {
					                   showErrorMessage(response.message);
					            }
					        },
					        error: function () {
					            showErrorMessage("发送请求失败");
					        }
					    });
					}
				}
			}
	}); 
    
}

//加载已有通告单的数据列表
function loadHasNoticeGrid(){
     var noticeSource =
     {
        datatype: "json",
        root:'resultList',
        url:'/notice/loadNoticeList',
        data: {forSimple: true},
        datafields: [
             { name: 'noticeId',type: 'string' },
             { name: 'noticeName',type: 'string' },
             { name: 'noticeDate',type: 'date' },
             { name: 'shootLocation',type: 'string' },
             { name: 'groupName',type: 'string' },
             { name: 'mainrole',type: 'string' },
             { name: 'canceledStatus',type: 'string' }
        ],
        type:'post',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        beforeprocessing:function(data){
            //查询之后可执行的代码
            noticeSource.totalrecords=data.result.total;
            noticeResultData = data.result;
        }
     };
     //渲染通告单数据
     var canceledStatusRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
         if(rowdata.canceledStatus==0){
             return "<div class='rowStatusColor' style='padding-top:5px;'>未销场</div>";
         }else if(rowdata.canceledStatus==1){
             return "<div class='rowStatusColor' style='padding-top:5px;'>已销场</div>";
         }
     };
     
     var noticeDataAdapter = new $.jqx.dataAdapter(noticeSource);
     
     $("#existNoticeGrid").jqxGrid({
         theme:theme,
         width: '99%',
         source: noticeDataAdapter,
         pageable: false,
         autoheight: false,
         height: 262,
         columnsresize: true,
         showtoolbar: false,
         rendergridrows:rendergridrows,
         localization:localizationobj,//表格文字设置
         columns: [
           { text: '通告单名称', datafield: 'noticeName', width: 245},
           { text: '时间', datafield: 'noticeDate', cellsformat: 'yyyy-MM-dd', width: 190},
           { text: '分组', datafield: 'groupName', width: 70},
           { text: '状态', cellsrenderer:canceledStatusRenderer, width: 96}
         ]
     });
}

//保存通告单
function saveNoticeInfo(){
    if($('#noticeForm').jqxValidator("validate")){
        var noticeName=$("#noticeNameInput").val();
        var group=$("#group").val();
        var noticeTime=$("#noticeTime").val();
        
        if(grid.getSelectIds()==0){
            showErrorMessage("请选择场次");
            return;
        }
        
        //判断当前场景中是否有演员请假
    	$.ajax({
    		url:"/notice/checkIsLeave",
    		data:{viewIds:grid.getSelectIds(),noticeDateStr:noticeTime},
    		dataType:"json",
    		type:'post',
			async:false,
			success:function(data){
    				if(!data.success){
    					showErrorMessage(data.message);
    				}else{
    					var leaveInfo = data.leaveInfo;
    					if (leaveInfo != null && leaveInfo != '' && leaveInfo != 'undefined') {
    						
    						doubleCallBackFun("是否重新选择", leaveInfo,"重新选择", "添加到通告单", null, function () {
    							$.ajax({
    					            url:"/notice/noticeSave",
    					            type:"post",
    					            dataType:"json",
    					            data:{noticeName:noticeName,groupId:group,noticeDateStr:noticeTime,viewIds:grid.getSelectIds()},
    					            async:false,
    					            success:function(data){
    					                if(data.success){
    					                    showSuccessMessage("添加通告单成功！");
    					                    $('#customWindow').jqxWindow('close');
    					                    grid.unSelectedAll();
    					                    $('#existNoticeGrid').jqxGrid("updatebounddata");
    					                }else{
    					                	showErrorMessage(data.message);
    					                }
    					            }
    					        });
    							
    						});
    						  
    					}else {
    						$.ajax({
    				            url:"/notice/noticeSave",
    				            type:"post",
    				            dataType:"json",
    				            data:{noticeName:noticeName,groupId:group,noticeDateStr:noticeTime,viewIds:grid.getSelectIds()},
    				            async:false,
    				            success:function(data){
    				                if(data.success){
    				                    showSuccessMessage("添加通告单成功！");
    				                    
    				                    $('#customWindow').jqxWindow('close');
    				                    grid.unSelectedAll();
    				                    $('#existNoticeGrid').jqxGrid("updatebounddata");
    				                }else{
    				                	showErrorMessage(data.message);
    				                }
    				            }
    				        });
    					}
    				}
    			}
    	});
        
    }
}

//改变分组信息
function changeGroupInfo(event){
    var args = event.args;
    if (args) {
        var index = args.index;
        var item = args.item;
        
        //选择新增分组
        if(item.value=="99"){
            if(index>25){
                showErrorMessage("目前最多选择到Z组");
                $("#group").jqxDropDownList("selectIndex",index-1);
                return;
            }
            $.ajax({
                url:"/shootGroupManager/saveGroup",
                type:"post",
                dataType:"json",
                data:{groupName:groupArray[index-1].text},
                success:function(data){
                    if(!data.success){
                        showErrorMessage(data.message);
                        $("#group").jqxDropDownList("selectIndex",0);
                    }
                    $("#group").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                    $("#group").jqxDropDownList("selectIndex",index);
                    autoGetNoticeName();
                }
            });
        }
        autoGetNoticeName();
    }
}

//初始化通告单中的按钮/表单/div的样式
function initNoticeCss(){
	$("#saveNoticeButton").jqxButton({
        theme:theme, 
        width: 80, 
        height: 25
    });
    
    $(".noticeCancelButton").jqxButton({
           theme:theme, 
           width: 80, 
           height: 25
    });
       
    $("#addToNoticeButton").jqxButton({
        theme:theme, 
        width: 80, 
        height: 25
    });
    
    $("#noticeDivForm").jqxExpander({
        theme: theme,
        width: '100%',
        expanded: true,
    });
    
    $("#noticeDivList").jqxExpander({
        theme: theme,
        width: '100%',
        expanded: false
    });
    
    $("#noticeDivForm").on('expanding', function () {
        $("#noticeDivList").jqxExpander('collapse');
    });
    $("#noticeDivList").on('expanding', function () {
        $("#noticeDivForm").jqxExpander('collapse');
    });
}

//获取跳转到场景列表之后需要加载的列表数据
function loadListDta(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var groupList = response.groupList;
				var majorRoleList = response.majorRoleList;
				//当主演人数过多时，弹出窗口
				if (majorRoleList.length>39) {
					swal({
						title: "提示",
						text: '角色中"主要演员"人数过多，会导致页面加载缓慢，是否去角色表中设置角色类型？',
						type: "warning",
						showCancelButton: true,  
						confirmButtonColor: "rgba(255,103,2,1)",
						confirmButtonText: "去设置",   
						cancelButtonText: "继续查看",   
						closeOnConfirm: false,   
						closeOnCancel: true
					}, function (isConfirm) {
						if (isConfirm) {
							//确定跳转到角色列表
							window.location.href = "/viewRole/toViewRolePage";
						}else {
							//忽略，加载场景列表
							//生成场景表列表
							loadViewListTable(majorRoleList);
						}
					});
				}else {
					//生成场景表列表
					loadViewListTable(majorRoleList);
				}
				//初始化添加到通告单窗口
				initAddViewToNoticeWin(groupList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//初始化场景列表的布局
function initViewListSpilter(){
	$("#viewSpliter").jqxSplitter({
		theme:theme,
        width: '100%',
        height: '100%',
        showSplitBar: true,
        resizable: false,
        panels: [
           { size: "100%", collapsible: false },
           {min: 377 ,collapsed :true}]
    });
}

//获取剧组类型
function getCrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//初始化右键菜单文本框
function initRightContent(){
	$("#addColorBox").jqxMenu({ 
		theme:theme, 
		width: '120px', 
		height: '275px',
		autoOpenPopup: false, 
		mode: 'popup',
		popupZIndex: 30000
	});
}

//隐藏右侧弹出的多选列表框
function hiddenPopup(){
	Popup.hide();
}

//判端是否是左击事件
function isLeftClick(event) {
    var leftclick;
    if (!event) var event = window.event;
    if (event.which) leftclick = (event.which == 1);
    else if (event.button) leftclick = (event.button == 1);
    return leftclick;
}

//当鼠标弹起时,是否弹出右键菜单选项
function isOpenRigthMenu(event){
	var contextMenu = $("#addColorBox");
    var selecation = '';
    if(document.selection){
        selecation = document.selection.createRange().text.toString();//ie
        if(selecation!=""){
            selecationText=selecation;
        } else {
            return ;
        }
    } else {
        selecation =document.getSelection().toString();
        if(selecation!=""){
            selecationText=selecation;
        }else{
            return ;
        }
    }
    
    var leftClick = isLeftClick(event) || $.jqx.mobile.isTouchDevice();
    if (leftClick) {
        var scrollTop = $(window).scrollTop();
        var scrollLeft = $(window).scrollLeft();
        
        var windowHeight = $(this).height();
        var boxY = parseInt(event.clientY) + 5 + scrollTop;
        
        if (boxY + 180 > windowHeight) {
            boxY = windowHeight - 180;
        }
        
        contextMenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, boxY);
        return false;
    }
}

//分析右键菜单的单击事件
function analysisRightClick(own){
	 //分析菜单的单击行为
    var frame= $('#viewInfoFrame').contents();
    //将当前场标识为改变状态
    frame.find("#isChanged").val(1);
    //主要内容
    if($(own).is('#c_content')){
        if(selecationText.length>0){
            frame.find('input.scene_content').val(selecationText);
        }
        return false;
    }
    //主要演员
    if($(own).is('#c_majoractor')){
        if(selecationText.length>0){
            frame.find('div.performer_first ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //特约演员
    if($(own).is('#c_guestactor')){
        if(selecationText.length>0){
            frame.find('div.performer_special ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //群众演员
    if($(own).is('#c_messactor')){
        if(selecationText.length>0){
            frame.find('div.performer_common ul li.tagInput').before('<li>'+selecationText+'<a href="javascript:void(0)" class="closeTag"></a></li>');
        }
        return false;
    }
    //主场景
    if($(own).is('#c_majorscene')){
        if(selecationText.length>0){
            frame.find('input.scene_first').val(selecationText);
        }
        return false;
    }
    //次场景
    if($(own).is('#c_secscene')){
        if(selecationText.length>0){
            frame.find('input.scene_second').val(selecationText);
        }
        return false;
    }
    //三级场景
    if($(own).is('#c_thirdscene')){
        if(selecationText.length>0){
            frame.find('input.scene_third').val(selecationText);
        }
        return false;
    }
    
    //服装
    if ($(own).is('#c_clothes')) {
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分服装",
    				text: '检测到您选择的服装中含有特殊字符，是否将当前服装拆分为多个服装？',
    				type: "warning",
    				showCancelButton: true,  
    				confirmButtonColor: "rgba(255,103,2,1)",
    				confirmButtonText: "是",   
    				cancelButtonText: "否",   
    				closeOnConfirm: true,   
    				closeOnCancel: true
    			}, function (isConfirm) {
    				if (isConfirm) {
    					var nameArr = text.split(testReg);
    					for(var i = 0; i<nameArr.length; i++){
    						if ($.trim(nameArr[i]) != '') {
    							frame.find('div.clothes_info ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.clothes_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.clothes_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    
    //化妆
    if ($(own).is('#c_makeup')) {
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分化妆",
    				text: '检测到您选择的化妆中含有特殊字符，是否将当前化妆拆分为多个化妆？',
    				type: "warning",
    				showCancelButton: true,  
    				confirmButtonColor: "rgba(255,103,2,1)",
    				confirmButtonText: "是",   
    				cancelButtonText: "否",   
    				closeOnConfirm: true,   
    				closeOnCancel: true
    			}, function (isConfirm) {
    				if (isConfirm) {
    					var nameArr = text.split(testReg);
    					for(var i = 0; i<nameArr.length; i++){
    						if ($.trim(nameArr[i]) != '') {
    							frame.find('div.makeup_info ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.makeup_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.makeup_info ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    //道具
    if($(own).is('#c_commonprop')){
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分道具",
    				text: '检测到您选择的道具中含有特殊字符，是否将当前道具拆分为多个道具？',
    				type: "warning",
    				showCancelButton: true,  
    				confirmButtonColor: "rgba(255,103,2,1)",
    				confirmButtonText: "是",   
    				cancelButtonText: "否",   
    				closeOnConfirm: true,   
    				closeOnCancel: true
    			}, function (isConfirm) {
    				if (isConfirm) {
    					var nameArr = text.split(testReg);
    					for(var i = 0; i<nameArr.length; i++){
    						if ($.trim(nameArr[i]) != '') {
    							frame.find('div.tool_main ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.tool_main ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.tool_main ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
    //特殊道具
    if($(own).is('#c_specialprop')){
        if(selecationText.length>0){
        	//判断输入的内容是否包含特殊字符，如果包含则提示是否分割
            //定义正则
    		var testReg = new RegExp('\\,|,|，|、|/|；| |\\t|　| ');
            var text = selecationText;
            if (testReg.test(text)) {
    			//包含特殊字符
    			swal({
    				title: "是否拆分道具",
    				text: '检测到您选择的道具中含有特殊字符，是否将当前道具拆分为多个道具？',
    				type: "warning",
    				showCancelButton: true,  
    				confirmButtonColor: "rgba(255,103,2,1)",
    				confirmButtonText: "是",   
    				cancelButtonText: "否",   
    				closeOnConfirm: true,   
    				closeOnCancel: true
    			}, function (isConfirm) {
    				if (isConfirm) {
    					var nameArr = text.split(testReg);
    					for(var i = 0; i<nameArr.length; i++){
    						if ($.trim(nameArr[i]) != '') {
    							frame.find('div.tool_special ul li.tagInput').before('<li>'+nameArr[i]+'<a href="javascript:void(0)" class="closeTag"></a></li>');
							}
    					}
    				}else {
    					frame.find('div.tool_special ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    				}
    			});
    		}else{
    			frame.find('div.tool_special ul li.tagInput').before('<li>'+text+'<a href="javascript:void(0)" class="closeTag"></a></li>');
    		}
        }
        return false;
    }
}

//初始化剧本上传页面窗口,并跳转到剧本上传页面
function loadScenarioUpload() {
	$("#uploadScenarioWindow").jqxWindow({
		 theme:theme,  
         width: 730,
         height: 635, 
         autoOpen: false,
         maxWidth: 2000,
         maxHeight: 1500,
         resizable: true,
         isModal: true,
         showCloseButton: true,
         modalZIndex: 1000,
         initContent: function() {
        	 $("#uploadScenarioDiv").find("iframe").attr("src", "/scenarioManager/touploadScePage");
         }
    });
}

//查询上一场 
function searchPreScene() {
	
	var rowData = grid.getRowData(parseInt(viewContentRowNo) - 1);
	if(rowData){
		showViewContent(rowData.viewId);
	}else{
		// 判断是否最后一行
		showErrorMessage("已经是第一场了");
	   return;
	}
}

//查询下一场
function searchNextScene() {
	var rowData = grid.getRowData(parseInt(viewContentRowNo) + 1);
	if(rowData){
		showViewContent(rowData.viewId);
	}else{
		showErrorMessage("已经是最后一场了");
	   return;
	}
}
/**
 * 
 * 表格对象
 * @param tableId
 * @param url
 * @param pageSize
 * @param pageNo
 * @param columns
 * @param filter
 * @param root
 */
function SimpleGrid(tableId, url, pagesize, pagenum, columns, filter, root, rendertoolbar) {
	
	var $this = this;
	
	//结果记录
	this.tableId=tableId;
	this.source= {columns:columns};
	this.url=url;
	this.page={pagesize:pagesize,pagenum:pagenum};
	this.columns=columns;
	this.filter=filter;
	this.root=root;
	this.rendertoolbar=rendertoolbar;
	
	//是否加载统计
	this.summaryFlag=true;
	
	this.isRowClick=true;
	
	this.setFilter=function(filter){
		this.filter=filter;
	};
	
	//当第一次操作的请求还没有结束时, 第二次操作的请求发起了, 那么问题就来了. 全局标识是否应该继续发送请求
	this.continueAjaxRecords = false;
	
	//异步加载反馈 //需要一次握手之后才能继续
	this.handshake = true;
	
	//动态加载表格数据
	this.getAjaxRecords = function(callback, filter){
		
		filter.pagenum = filter.pagenum +1;
		
		$.ajax({
			url:$this.url,
			type:"post",
			dataType:"json",
			data: filter,
			success:function(data){
				$this.source.data = $this.source.data.concat(data.result[root]);
				
				$this.loadGridContent(data.result[root]);
				
				//显示第200条
				if(data.result.pageNo == 2){
					$("#tablebody tbody:last").removeClass("hidden-tbody");
				}
				
				//判断是否还存在异步数据, 没有的话就开启全选按钮
				if($this.continueAjaxRecords && data.result.pageNo != data.result.pageCount){
					callback(callback, filter);
				}else{
					$("#checkedAll").prop("disabled",false);
					$("#hideGridColumns").attr("disabled", false);//开启可隐藏列按钮
					$this.handshake = true;
				}
			}
		});
	};
	
	//获取记录数据
	this.getRecords = function(){
		
		this.filter.pagesize = this.page.pagesize;
		this.filter.pagenum = this.page.pagenum;
		
		$.ajax({
			url:this.url,
			type:"post",
			dataType:"json",
			data:this.filter,
			success:function(data){
				if(!root){
					throw Exception("root不可为空");
				}
				
				var total = data.result.total;
				var pageCount = data.result.pageCount;
				var records = data.result[root];
				
//				if (total == 0 && !filter.fromAdvance) {
//					popupPromptBox("提示", "当前还未上传剧本，是否现在上传？", function() {
//						$("#uploadScenarioWindow").jqxWindow("open");
//					});
//				}
				
				//为表格中page属性赋值
				$this.page.total = total;
				
				//设置分页数据
				if(($this.page.pagenum * $this.page.pagesize + parseInt($this.page.pagesize)) > total) {
					if($this.page.pagenum * $this.page.pagesize==0){
						$this.page.start = 1;
					} else {
						$this.page.start = $this.page.pagenum * $this.page.pagesize;
					}
					$this.page.end = total;
					
				} else if ($this.page.pagenum == 0) {
					$this.page.start = 1;
					$this.page.end = $this.page.pagesize;
				} else {
					$this.page.start = $this.page.pagenum * $this.page.pagesize;
					$this.page.end = $this.page.pagenum * $this.page.pagesize+parseInt($this.page.pagesize);
				}
				
				$this.page.pageCount = pageCount;
				$this.source.data = records;
				$this.loadGridContent(records);
				
				//绑定事件
				$this.initTableEvent();
				
				//显示第一个100条数据
				$("#tablebody tbody").removeClass("hidden-tbody");
				
				if($this.page.pageCount > 1){
					$this.continueAjaxRecords = true;
					$this.handshake = false;
					
					//在所有的数据没有加载完之前, 全选是禁用的
					$("#checkedAll").prop("disabled",true);
//					$("#hideGridColumns").attr("disabled", true);//隐藏列按钮不可用
					$this.getAjaxRecords($this.getAjaxRecords, $this.filter);
				}
			}
		});
	};
	
	//加载表格
	this.loadTable = function(){
		
		//此处创建的是表格的框架，还不带有任何数据
		this.createTable();
		
		//此处获取数据的方式为异步，获取数据后，调用填充数据方法把表格数据以及分页信息更新到页面上
		this.getRecords();
		
		//重置文本框的高度
		resizeViewGird();
		
		//重置选择统计
		$("#_already_selected").html($("#tablebody :checked").length);
	};
	
	//兼容行index 但是要保证顺序调用
	var _tbodyIndex = 0;
	
	//创建表格html
	this.createTable = function (){
		
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tbodyIndex = 0;
		
		_tableObj.append('<div class="t_i_h" id="hh"><div class="ee"><table cellpadding="0" cellspacing="0" border="0">'+
				'<thead><tr id="tableHead"></tr></thead></table></div></div>');
		
		var _head = _tableObj.find("#tableHead");
		
		//所有列
		var columns = this.source.columns;
		
		for(var i=0; i<columns.length; i++){
			if(columns[i].isCheckbox){
				_head.append('<td width="15px" cellid="'+ i +'" class="bold"><input type="checkbox" id="checkedAll" class="line-height"/></td>');
			
			}else{
				
				if(columns[i].style){
					if(jQuery.inArray(columns[i].text, hideTdText) == -1){
						_head.append('<td width="50px" cellid="'+ i +'" style="'+columns[i].style+'" class="bold"><p style="width:50px">'+columns[i].text+'</p></td>');
					}else{
						_head.append('<td width="50px" cellid="'+ i +'" style="display: none;'+columns[i].style+'" class="bold"><p style="width:50px">'+columns[i].text+'</p></td>');
						hideTdIndex.push(i);
					}
					
				}else{
					if(jQuery.inArray(columns[i].text, hideTdText) == -1){
						_head.append('<td width="'+columns[i].width+'" cellid="'+ i +'" class="bold"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
					}else{
						_head.append('<td width="'+columns[i].width+'" cellid="'+ i +'" class="bold" style="display:none;"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
						hideTdIndex.push(i);
					}
					
				}
			}
		}
		
		//滚动条预留列
		_head.append('<td><p style="width:6px;"></p></td>');	
		
		//表格主体部分
		_tableObj.append('<div class="cc" id="cc" onscroll="aa()"><div id="_table_doc"><table id="tablebody" cellpadding="0" cellspacing="0" border="0"></table></div></div>');
		
		if(this.rendertoolbar){
			this.rendertoolbar($("#rendertoolbar"));
		}
		
		//加载统计
		if(this.summaryFlag){
			this.loadSummary();
		}
		
		this.summaryFlag = true;
	};
	
	//加载表格主体部分
	this.loadGridContent = function(tableData) {
		
		var _this = this;
		var _tableObj = $("#tablebody");
		
		row_array = ['<tbody class="hidden-tbody" >'];
		
		for(var i=0, len = tableData.length;i<len;i++){
			
			row_array.push(this.createRow(null, tableData[i], (_tbodyIndex*100) + i));
		}
		
		row_array.push('</tbody>');
		
		_tableObj.append(row_array.join(''));
		
		_tbodyIndex++;
		
		var tbody = _tableObj.find("tbody:last");
		
		//加入选中Id
		tbody.on('click', ':checkbox', function(e){
			
			$("#_already_selected").html($("#tablebody :checked").length);
			
			//判断是否全选
			isCheckAll();
			e.stopPropagation();
			
		}).on('click', '>tr', function(){
			
			if(_this.isRowClick){
				//取消其他行的选中状态
				/*$("#tablebody  input[type='checkbox']:checked").each(function(){
					$(this).prop("checked", false);
				});*/
				$(this).find(":checkbox").trigger("click");
				//获取已经选中的行
				/*$("#tablebody tr[class='show-content-col']").each(function(){
					$(this).removeClass("show-content-col");
				});
				//设置当前行为选中行
				$(this).addClass("show-content-col");   */
			}else{
				_this.isRowClick=true;
			}
		});
		
	};
	
	//全不选
	this.unSelectedAll=function(){
		
		$("#"+this.tableId).find("tbody :checkbox").prop("checked",false);
		$("#_already_selected").html($("#tablebody :checked").length);
	};
	
	this.initTableEvent = function(){
		
		var _tableObj = $("#"+this.tableId);
		
		var pageCount = this.page.pageCount;
		var total = this.page.total;
		
		//checkbox全选
		$("#checkedAll").click(function(){
			
			if(this.checked){
				_tableObj.find("tbody :checkbox").prop("checked",true);
			}else{
				_tableObj.find("tbody :checkbox").prop("checked",false);
			}
			
			$("#_already_selected").html($("#tablebody :checked").length);
		});
		
		//一个body的高度
		var bodyheight = _tableObj.find("tbody:eq(0)").height();
		
		//设置文档的高度
		$("#_table_doc").css("height", bodyheight/100 * total);
		
		//拉动滚动条产生的重复事件
		var timeoutnum = 0;
		
		//记录那个body在窗口显示
		var preId = 0;
		
		$("#cc").scroll(function(evt){
			
			//清除重复的事件
			window.clearTimeout(timeoutnum);
			
			timeoutnum = setTimeout(function(){
			
				var target = $(evt.currentTarget);
				
				var scollTop = target.scrollTop();
				
				var id = (scollTop - scollTop % bodyheight)/bodyheight;
				
				//如果相同就不更新数据
				if(preId != id){
					
					preId = id;
					
					//解决滚动条长距离拉动的问题
					_tableObj.find("tbody").addClass("hidden-tbody");
					
					//如果在异步加载之前滚动, 判断异步加载是否完成
					if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
						
						(function pollingBody(){
							
							setTimeout(function(){
								
								if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
									
									pollingBody();
								}else{
									
									_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
									
									$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
								}
								
							},1000);
						}).call(this);
						
					}else{
						
						_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
						
						$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
					}
				}
				
			},0);
		});
	};
	
	//分页信息
	this.loadPage = function() {
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find(".pageturn").remove();
		
		//分页部分
		var pageHTML = '<div class="pageturn"><ul class="page">'
		+ '<li>总场数：'+this.page.total+'</li>'
		+ '<li>每页显示场数：<div style="float: right;margin-top: 4px;" id="pageSize"></div></li>'
		+ '<li>当前场数：'+this.page.start+'-'+this.page.end+'</li>'
		+ '<li>总页数：'+ this.page.pageCount +'</li>'
		+ '<li>当前页数：<input class="search_text" type="text" onkeyup="lmaNumber(this, ' + this.page.pageCount + ');" id="pagenum" value="'+(parseInt(this.page.pagenum)+1)+'" /><input id="oldPageNum" type="hidden"  value="'+(parseInt(this.page.pagenum)+1)+'"></li>'
		+ '<li><input class="previous_button"  title="上一场" type="button" id="previous_button"  />&nbsp;<input class="next_button" title="下一场" type="button" id="next_button" /></li>';
		+ '</ul></div>';
		
		_tableObj.append(pageHTML);
		var source = [
	                    {text:"100",value:100},
	                    {text:"500",value:500},
	                    {text:"全部",value:99999999}];
		
		$("#pageSize").jqxDropDownList({theme:theme, enableBrowserBoundsDetection: true, source: source,autoDropDownHeight: true, displayMember: "text", valueMember: "value", width: '50', height: '18'});
		
		var selectPageSizeIndex=0;
		if(this.page.pagesize==100){
			selectPageSizeIndex=0;
		}else if(this.page.pagesize==500){
			selectPageSizeIndex=1;
		}else if(this.page.pagesize==99999999){
			selectPageSizeIndex=2;
		}
		
		$("#pageSize").jqxDropDownList("selectIndex",selectPageSizeIndex);
		//页长改变事件绑定
		$("#pageSize").bind("change",{grid:this,pageNo:0, pageCount: this.page.pageCount},pageChanged);
		//上一页
		$("#previous_button").bind("click",{grid:this,pageNo:this.page.pagenum-1, pageCount: this.page.pageCount},pageChanged);
		//下一页
		$("#next_button").bind("click",{grid:this,pageNo:this.page.pagenum+1,  pageCount: this.page.pageCount},pageChanged);
		//指定页
		$("#pagenum").bind("change",{grid:this,pageNo:null,  pageCount: this.page.pageCount},pageChanged);
	};
	
	//统计信息
	this.loadSummary = function(){
		var _this=this;
		$.ajax({
			url:"/viewManager/loadSummary",
			data:_this.filter,
			dataType:"json",
			type:"post",
			async: true,
				success:function(data){
					
					var viewStatistics = data.viewStatistics;
					//总场数
					var statisticsViewCount = viewStatistics.statisticsViewCount;
					//总页数
					var statisticsPageCount = viewStatistics.statisticsPageCount;
					//状态分类统计
					var statisticsShootStatus = viewStatistics.statisticsShootStatus;
					//内外景统计
					var statisticsSite = viewStatistics.statisticsSite;
					//场景总数
					var statisticsHTML = "统计：共"+statisticsViewCount[0].funResult+"场";
					
					statisticsHTML+="/"+statisticsPageCount[0].funResult.toFixed(1)+"页";
					
					//状态统计
					var shootStatusKeys = shootStatusMap.keys();
					for(var i=0;i<shootStatusKeys.length;i++){
						var shootStatusKey = shootStatusKeys[i];
						for(var j = 0;j<statisticsShootStatus.length;j++){
							if(shootStatusKey==statisticsShootStatus[j].shootStatus){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+statisticsShootStatus[j].funResult+"场 ";
								break;
							}else if(j==statisticsShootStatus.length-1){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+"0场 ";
							}
						}
					}
					
					//内外景统计
					var siteKeys = siteMap.keys();// |  &nbsp;气氛2场 &nbsp;
					for(var i=0;i<siteKeys.length;i++){
						var siteKey = siteKeys[i];
						for(var j = 0;j<statisticsSite.length;j++){
							if(siteKey==statisticsSite[j].site){
								statisticsHTML+="|  "+siteMap.get(siteKey)+statisticsSite[j].funResult+"场 ";
								break;
							}else if(j==statisticsSite.length-1){
								statisticsHTML+="|  "+siteMap.get(siteKey)+"0场 ";
							}
						}
					}
					
					$("#statistics").text(statisticsHTML);
				}
		});
	};
	
	//获取选中行的Id
	this.getSelectIds = function(){
		
		var result = "";
		
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find("tbody :checkbox:checked").each(function(index){
			
			if(index == 0){
				
				result = $(this).attr("id");
			}else{
				result += ","+$(this).attr("id");
			}
		});
		
		return result;
	};
	
	//获取选中行的状态
	this.getSelectStatus = function(){
		
		var result = "";
		
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find("tbody :checkbox:checked").each(function(index){
			//取出每行的集场号
			var viewNos = $(this).parent().next().children().children().text();
			if(index == 0){
				
				result = $(this).attr("sval")+"&"+viewNos;
			}else{
				result += ","+$(this).attr("sval")+"&"+viewNos;
			}
		});
		
		return result;
	};
	
	//跳转到页面
	this.goToPage = function(pageNo){
		
		this.continueAjaxRecords = false;
		
		var inter = null;
		
		//判断上一次的操作的异步加载是否结束, 如果没有结束, 在下一次告诉他结束异步加载, 并且反馈回来.
		inter = setInterval(function(){
			
			if($this.handshake){
				
				clearInterval(inter);
				
				$this.summaryFlag = true;
				
				var pageSize = $("#pageSize").val();
				
				if(pageNo==null){
					
					pageNo=$("#pagenum").val()-1;
				}
				
				$this.page.pagenum=pageNo;
				
				if(pageSize){
					$this.page.pagesize=pageSize;
				}
				
				$this.loadTable();
			}
			
		},50);
	};
	
	this.refresh = function(){
		
		this.loadTable();
	};
	
	this.getRowData = function(index){
		
		return this.source.data[index];
	};
	
	this.getRowIndex = function(viewId){
		
		if(!viewId){
			return null;
		}
		
		return parseInt($("#"+viewId).attr("index"));
	};
	
	this.selectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	this.unSelectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	//获取选中行的行号
	this.getSelectedIndexs = function() {
		
		var resultArray = new Array();
		
		var _tBody = $(".cc");
		_tBody.find(":checkbox").each(function(event) {
			if ($(this).prop("checked")) {
				resultArray.push($(this).attr("index"));
			}
		});
		
		return resultArray;
	};
	
	//更新单行数据
	this.updaterowdata = function(rowIndex, rowData) {
		
		var _tBody = $(".cc");
		
		var _row = this.createRow(_tBody, rowData, rowIndex);
		var $row = $(_row);
		
		//然后替换表格中指定的行
		_tBody.find("tr[rowid="+ rowIndex +"]").replaceWith($row);
		
		$("#_already_selected").html($("#tablebody :checked").length);
		$("#checkedAll").prop("checked",false);
		//设置当前行被选中
		this.selectRow(rowIndex);
		_tBody.find("tr[rowid="+ rowIndex +"]").addClass("show-content-col");
	};

	//更新一行中的一列数据 createCell方法还未完成，cellIndex获取方法也未完成
	this.updatecell = function(rowIndex, cellIndex, cellData) {
		var _tBody = $(".cc");
		var _row = _tBody.find(" tr[rowid="+ rowIndex +"]");

		//生成表格的列
		var _cell = this.createCell();
		
		//替换掉表格中指定的列
		_row.find("td[cellid="+ cellIndex +"]").replaceWith(_cell);
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		
		var style = "";
		
		if(rowData.shootStatus != ""){
			
			style = " style='background-color:"+getColor(rowData.shootStatus)+";' ";
		}
		
		var _row =["<tr rowId='"+rowid+"'" + style + ">"];
		
		for(var j=0;j<columns.length;j++){
			
			if(columns[j].isCheckbox){
				_row.push('<td cellid="'+ j +'" class="bold"><input type="checkbox" id="'+rowData.viewId+'" index="'+rowid+'" class="line-height" sval="'+ rowData.shootStatus +'"/></td>');
			}else{
				if(columns[j].cellsrenderer){
					if(jQuery.inArray(j,hideTdIndex) == -1){//判断当前列是否是隐藏列
						_row.push('<td cellid="'+ j +'"><p style="height:14px;width:' + columns[j].width + ';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</p></td>');
					}else{
						_row.push('<td cellid="'+ j +'" style="display: none;"><p style="height:14px;width:' + columns[j].width + ';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</p></td>');
					}
					
				}else{
					if(jQuery.inArray(j,hideTdIndex) == -1){
						_row.push('<td cellid="'+ j +'"><p title="'+rowData[columns[j].filedName]+'" style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+rowData[columns[j].filedName]+'</p></td>');
					}else{
						_row.push('<td cellid="'+ j +'" style="display: none;"><p title="'+rowData[columns[j].filedName]+'" style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+rowData[columns[j].filedName]+'</p></td>');
					}
					
				}
			}
		}
		_row.push("</tr>");
		
		return _row.join('');
	};
}

//验证当前输入框中只能输入数字
function lmaNumber(obj, pageCount){
	//检查是否是非数字值  
    if (isNaN(obj.value)) {  
        obj.value = $("#oldPageNum").val();  
    }
    if(obj.value==0){
    	obj.value = $("#oldPageNum").val();
    }
    if (obj.value > pageCount) {
    	obj.value = $("#oldPageNum").val();
    }
    if (obj != null) {  
        //检查小数点后是否对于两位http://blog.csdn.net/shanzhizi  
        if (obj.value.toString().indexOf(".") != -1) {  
        	showErrorMessage("只能输入整数！");  
            obj.value = "1";  
        }  
    }  
}

//行checkbox事件只判断是否全选
function isCheckAll(){
	
	var _tableObj = $("#tablebody");
	var checkboxs = _tableObj.find(":checkbox");
	
	for(var i=0, len=checkboxs.length; i<len;i++){
		
		if(!checkboxs[i].checked)
			break;
	}
	
	if(i != len){
		$("#checkedAll").prop("checked",false);
	}else{
		$("#checkedAll").prop("checked",true);
	}
}

//查询分场数据
function queryFenView(own){
	if ($(own).hasClass("btn-default")) {
		filter.sortType=2;
		filter.fromAdvance = true;
     
		grid.setFilter(filter);
		grid.goToPage(0);
     
		$(own).removeClass("btn-default");
		$(own).addClass("btn-primary");
		$("#smoothView").removeClass("btn-primary");
		$("#smoothView").addClass("btn-default");
	}
}
//查询顺场数据
function queryShunView(own){
	if ($(own).hasClass("btn-default")) {
		filter.sortType=1;
		filter.fromAdvance = true;
     
		grid.setFilter(filter);
		grid.goToPage(0);
     
		$(own).removeClass("btn-default");
		$(own).addClass("btn-primary");
		$("#groupView").removeClass("btn-primary");
		$("#groupView").addClass("btn-default");
	}
}

//加载高级查询窗口
function superSearchViewInfo(){
	//如果是第一次打开高级查询窗口则需要初始化查询窗口,若不是,只需要清空拍摄地点并重新加载拍摄地点的查询条件即可
	if(typeof(popupLayer)=="undefined"){
    	
    	loadSearchDIV();
    	popupLayer.jqxWindow('open');
    }else{
    	loadSearchCondition();
    	popupLayer.jqxWindow('open');
    }
}

//初始化高级查询窗口
function loadSearchDIV(){
	var screenWidth = window.screen.width;
	var winHeight;//弹窗高度
	if(screenWidth >= 1366 && screenWidth <= 1399){
		winHeight = 550;
	}else{
		winHeight = 710;
	}
	popupLayer = $('#searchWindow').jqxWindow({
		theme:theme,  
	    width: 840,
        height: winHeight, 
        maxHeight: 800,
        autoOpen: false,
        cancelButton: $('#closeSearchSubmit'),
        isModal: true,
        resizable: false,
        initContent: function () {
	        //加载查询条件
	        loadSearchCondition();
	        
	        showDifferentSearchData();
	        
		    $("#searchSubmit").jqxButton({
	           width:80,
	           height: 25
	        });
		    
		    $("#closeSearchSubmit").jqxButton({
	           width:80,
	           height: 25
	        });
		    
		    $("#clearSearchButton").jqxButton({
		       width:80,
	           height: 25
		    });
		    
	        $("#seriesViewNos").jqxInput({
	            placeHolder: '输入范例：1-1,1-2,1-3'
	        });
	        
	        $("#viewNos").jqxInput({
                placeHolder: '输入范例：1,2,3'
            });
	        
	        $("#viewRemark, #mainContent").jqxInput({
	            theme: theme
	        });

		    $('.selectpicker').selectpicker({
                size: 10
            });
            
		    //jqxFormattedInput({ width: 250, height: 25, radix: "decimal", decimalNotation: "exponential", value: "330000" });
		    $("#startSeriesNo").jqxInput({theme:theme, placeHolder: "集", value: '' });
		    $("#startSeriesNo, #endSeriesNo").on("keyup", function() {
                if (isNaN($(this).val())) {
			        $(this).val("");
			    }
		    });
		    
            $("#startViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
            $("#endSeriesNo").jqxInput({theme:theme,placeHolder: "集", value: '' });
            $("#endViewNo").jqxInput({theme:theme,placeHolder: "场", minLength: 1 });
            
            //下拉控件中当选择空的时候自动取消勾选其他选项，当选择其他选项时，自动取消勾选空选项
            $('.selectpicker').on('change', function(event) {
                var value = event.target.value;
                var eventId = event.target.id;  //获取当前select控件id
                
                //var prevSelectedValue = $("#selectpickerPreValue").val();   //select控件之前选中的值
                var prevSelectedValue = $("#"+eventId).parent().find(".preValue").val();  
                if (prevSelectedValue == "blank") {
                    $("#"+ eventId).find('option').eq(0).prop('selected', false).removeAttr('selected');
                    $("#"+ eventId).selectpicker('render');
                    
                    //$("#selectpickerPreValue").val($("#"+ eventId).val());
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
                var id = $(this).siblings(".selectpicker").attr("id");
                if (id == "majorRoleSelect") {
                    //隐藏单选按钮
                    $("#anyOneAppear").hide();
                    $("#noOneAppear").hide();
                    $("#everyOneAppear").hide();
                    $("#notEvenyOneAppear").hide();
                }
                $(this).siblings(".selectpicker").selectpicker('deselectAll');
            });
    	}
    });
}

//清空高级查询文本框中的内容
function clearSaerchContent(){
    //集场重置
    if (crewType != Constants.CrewType.movie && crewType != 3) {
        $("#startSeriesNo").val("");
        $("#endSeriesNo").val("");
    }
    
    $("#startViewNo").val("");
    $("#endViewNo").val("");
    $("#seriesViewNos").val("");
    $("#viewNos").val("");
    
    //隐藏单选按钮
    $("#anyOneAppear").hide();
    $("#noOneAppear").hide();
    $("#everyOneAppear").hide();
    $("#notEvenyOneAppear").hide();
    
    //设置单选按钮默认选中状态
    $("input[name='searchMode'][value='1']").prop("checked",true);
    //$("input[name='sortType'][value='1']").prop("checked",true);
   /* $("input[name='sortFlag'][value='1']").prop("checked",true);*/
    
    //所有下拉值全部反选
    $('.selectpicker').selectpicker('deselectAll');
    
    $(".preValue").val("");
    
    $("#mainContent").val("");
    $("#viewRemark").val("");

}

//更改高级查询中的主演下拉框时触发的方法
function changeMajorRole(event){
	 //当选择主要演员显现"出现即可"和"不出现"单选按钮，只有当选择两个及两个以上才显现"同时出现"和"不同时出现"单选按钮
    var majorRoleVal = $("#majorRoleSelect").val();
    
    if (majorRoleVal != null) {
        var selectedLength = majorRoleVal.length;
        if (selectedLength > 1) {
            /* 选择多条数据的情况 */
            
            // 展现"同时出现"、"不同时出现"
            $("#everyOneAppear").show();
            $("#notEvenyOneAppear").show();
        } else {
            /* 由选择多条数据变为选择一条数据情况 */
            
            //如果此时同时出现或不同时出现被选中，则设置单选按钮“出现即可”选中
            var searchMode=$("input[name='searchMode']:checked").val();
            if (searchMode == 0 || searchMode == 2) {
                $("input[name='searchMode'][value='1']").prop("checked",true);
            }
            
            //展现"出现即可"、"不出现"，隐藏"同时出现"、"不同时出现"
            $("#anyOneAppear").show();
            $("#noOneAppear").show();
            $("#everyOneAppear").hide();
            $("#notEvenyOneAppear").hide();
        }
    } else {
        //一条数据都没选择的情况
        //隐藏"出现即可"、"不出现"、"同时出现"、"不同时出现"
        $("#anyOneAppear").hide();
        $("#noOneAppear").hide();
        $("#everyOneAppear").hide();
        $("#notEvenyOneAppear").hide();
    }

}

//点击高级查询页面的查询按钮,根据查询条件获取数据
function confirmSearchViewInfo(){
	var atmosphere =$("#atmosphereSelect").val();
	var site =$("#siteSelect").val();
	var major =$("#firstLocationSelect").val();
	var minor =$("#secondLocationSelect").val();
	var thirdLevelView = $("#thirdLocationSelect").val();
	var clothes =$("#clothSelect").val();
	var makeup =$("#makeupSelect").val();
	var roles = $("#majorRoleSelect").val(); 
	var props = $("#propSelect").val(); 
    var specialProps = $("#specialPropSelect").val();
	var guestRole = $("#guestRoleSelect").val(); 
	var massRole = $("#massRoleSelect").val(); 
	var shootStatus = $("#shootStatusSelect").val();
	var advert = $("#advertInfoSelect").val();
	//拍摄地点
	var shootLocation =$("#shootLocationSelect").val();
	var mainContent = $("#mainContent").val();
	var remark = $("#viewRemark").val();
    var seriesViewNos = $("#seriesViewNos").val();
    var viewNos = $("#viewNos").val();
    //特殊提醒
    var specialRemind = $("#specialRemindSelect").val();
    //对数据进行校验
	//气氛
	if(atmosphere!= null && atmosphere!=""){
		var atmosphereStr = "";
		
		for(var i=0;i<atmosphere.length;i++){
			atmosphereStr+=atmosphere[i]+",";
		}
		atmosphereStr=atmosphereStr.substring(0,atmosphereStr.length-1);
		filter.atmosphere=atmosphereStr;
	}else{
		filter.atmosphere="";
	}
	
	//特殊提醒
	if(specialRemind!= null && specialRemind!=""){
		var specialRemindStr = "";
		
		for(var i=0;i<specialRemind.length;i++){
			specialRemindStr+=specialRemind[i]+",";
		}
		specialRemindStr=specialRemindStr.substring(0,specialRemindStr.length-1);
		filter.specialRemind=specialRemindStr;
	}else{
		filter.specialRemind="";
	}
	
	//内外景
	if(site!= null && site!=""){
		var siteStr = "";
		
		for(var i=0;i<site.length;i++){
			siteStr+=site[i]+",";
		}
		siteStr=siteStr.substring(0,siteStr.length-1);
		filter.site=siteStr;
	}else{
		filter.site="";
	}
	//主场景信息
	if(major!= null && major!=""){
		var majorStr = "";
		
		for(var i=0;i<major.length;i++){
			majorStr+=major[i]+",";
		}
		majorStr=majorStr.substring(0,majorStr.length-1);
		filter.major=majorStr;
	}else{
		filter.major="";
	}
	//次场景信息
	if(minor!= null && minor!=""){
		var minorStr = "";
		
		for(var i=0;i<minor.length;i++){
			minorStr+=minor[i]+",";
		}
		minorStr=minorStr.substring(0,minorStr.length-1);
		filter.minor=minorStr;
	}else{
		filter.minor="";
	}
	
	//三级场景信息
	if(thirdLevelView!= null && thirdLevelView!=""){
		var thirdLevelViewStr = "";
		
		for(var i=0;i<thirdLevelView.length;i++){
			thirdLevelViewStr+=thirdLevelView[i]+",";
		}
		thirdLevelViewStr=thirdLevelViewStr.substring(0,thirdLevelViewStr.length-1);
		filter.thirdLevel=thirdLevelViewStr;
	}else{
		filter.thirdLevel="";
	}
	//道具
	if(clothes!= null && clothes!=""){
		var clothe = "";
		
		for(var i=0;i<clothes.length;i++){
			clothe+=clothes[i]+",";
		}
		clothe=clothe.substring(0,clothe.length-1);
		filter.clothes=clothe;
	}else{
		filter.clothes="";
	}
	//化妆
	if(makeup!= null && makeup!=""){
		var makeupStr = "";
		
		for(var i=0;i<makeup.length;i++){
			makeupStr+=makeup[i]+",";
		}
		makeupStr=makeupStr.substring(0,makeupStr.length-1);
		filter.makeup=makeupStr;
	}else{
		filter.makeup="";
	}
	//拍摄状态
	if(shootStatus!= null && shootStatus!=""){
		var shootStatusStr = "";
		
		for(var i=0;i<shootStatus.length;i++){
			shootStatusStr+=shootStatus[i]+",";
		}
		shootStatusStr=shootStatusStr.substring(0,shootStatusStr.length-1);
		filter.shootStatus=shootStatusStr;
	}else{
		filter.shootStatus="";
	}
	//角色信息
	if(roles!= null && roles!=""){
		var role = "";
		
		for(var i=0;i<roles.length;i++){
			role+=roles[i]+",";
		}
		role=role.substring(0,role.length-1);
		filter.roles=role;
	}else{
		filter.roles="";
	}
	//道具
	if(props!= null && props!=""){
		var prop = "";
		
		for(var i=0;i<props.length;i++){
			prop+=props[i]+",";
		}
		prop=prop.substring(0,prop.length-1);
		filter.props=prop;
	}else{
		filter.props="";
	}
	//特殊道具
    if (specialProps != null && specialProps != "") {
       var specialProp = "";
       for (var i = 0; i < specialProps.length; i++) {
           specialProp += specialProps[i] + ",";
       }
       specialProp = specialProp.substring(0, specialProp.length-1);
       filter.specialProps = specialProp;
    } else {
       filter.specialProps = ""; 
    }
    //特约演员
	if(guestRole!= null && guestRole!=""){
		var guest = "";
		
		for(var i=0;i<guestRole.length;i++){
			guest+=guestRole[i]+",";
		}
		guest=guest.substring(0,guest.length-1);
		filter.guest=guest;
	}else{
		filter.guest="";
	}
	//群众演员
	if(massRole!= null && massRole!=""){
		var mass = "";
		
		for(var i=0;i<massRole.length;i++){
			mass+=massRole[i]+",";
		}
		mass=mass.substring(0,mass.length-1);
		filter.mass=mass;
	}else{
		filter.mass="";
	}
	//拍摄地点
	if(shootLocation!= null && shootLocation!=""){
		var shootLocationStr = "";
		
		for(var i=0;i<shootLocation.length;i++){
			shootLocationStr+=shootLocation[i]+",";
		}
		shootLocationStr=shootLocationStr.substring(0,shootLocationStr.length-1);
		filter.shootLocation=shootLocationStr;
	}else{
		filter.shootLocation="";
	}
	//商植
	if(advert!= null && advert!=""){
		var advertStr = "";
		
		for(var i=0;i<advert.length;i++){
			advertStr+=advert[i]+",";
		}
		advertStr=advertStr.substring(0,advertStr.length-1);
		filter.advert=advertStr;
	}else{
		filter.advert="";
	}
	//开始集次号
	if (crewType == 0 || crewType == 3) {
		if($("#startSeriesNo").val()!=""){
			filter.startSeriesNo=$("#startSeriesNo").val();
		}else{
	        filter.startSeriesNo="1";
	    }
	}else {
		if($("#startSeriesNo").val()!=""){
			filter.startSeriesNo=$("#startSeriesNo").val();
		}else{
			filter.startSeriesNo="";
		}
	}
	//开始场次号
	if($("#startViewNo").val()!=""){
		filter.startViewNo=$("#startViewNo").val();
	}else{
		filter.startViewNo="";
	}
	
	//结束集次号
	if (crewType == 3 || crewType == 0) {
		if($("#endSeriesNo").val()!=""){
			filter.endSeriesNo=$("#endSeriesNo").val();
		}else{
			filter.endSeriesNo="1";
		}
	}else {
		if($("#endSeriesNo").val()!=""){
			filter.endSeriesNo=$("#endSeriesNo").val();
		}else{
			filter.endSeriesNo="";
		}
		
	}
	//结束场次号
	if($("#endViewNo").val()!=""){
		filter.endViewNo=$("#endViewNo").val();
	}else{
		filter.endViewNo="";
	}
	
	if (crewType != 0 && crewType != 3) { //电影或网大
		if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
			showErrorMessage("请填写起始集数");
			return;
		}
	}
	
	if (crewType != 0 && crewType != 3) {
		if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
			showErrorMessage("请填写集数");
			return;
		}
	}
	filter.seriesNo="";
	filter.viewNo="";
    filter.seriesViewNos = "";
    //集场编号（非电影剧本时使用）
    if (seriesViewNos != null && seriesViewNos != "") {
        var seriesViewNoArr = seriesViewNos.split(/，|；|,|;/);
        for (var i = 0; i < seriesViewNoArr.length; i++) {
            if (seriesViewNoArr[i] != null && seriesViewNoArr[i] != "" && !/^\d+( )*(-|－|——)( )*.+/.test(seriesViewNoArr[i])) {
                showErrorMessage("《" + seriesViewNoArr[i] + "》场集场编号不符合规范，请重新输入");
                return;
            }
            
            var singleSeriesViewNoArr = seriesViewNoArr[i].split(/-|－|——/);
            var seriesNo = singleSeriesViewNoArr[0];
            if (isNaN(seriesNo)) {
                showErrorMessage("《" + seriesViewNoArr[i] + "》场集号只能输入数字，请重新输入");
                return;
            }
        }
    
        filter.seriesViewNos = seriesViewNos;
    }
    
    //场次编号（电影剧本时使用）
    if (viewNos != null && viewNos != "") {
        var viewNoArr = viewNos.split(/，|；|,|;/);
        
        var dealedViewNos = "";
        for (var i = 0; i < viewNoArr.length; i++) {
            var seriesViewNo = "1-" + viewNoArr[i];
            if (i == 0) {
                dealedViewNos = seriesViewNo;
            } else {
                dealedViewNos += "," + seriesViewNo;
            }
        }
    
        filter.seriesViewNos = dealedViewNos;
    }
	
	//主要内容
	if (mainContent != "" && mainContent != null) {
	 filter.mainContent = mainContent;
	} else {
	 filter.mainContent = "";
	}
	
	//备注
	if (remark != "" && remark != null) {
     filter.remark = remark;
    } else {
     filter.remark = "";
    }
	//排序方式
	/*var sortFlag = $("input[name='sortFlag']:checked").val();
	filter.sortFlag=sortFlag;*/
	//查询条件的出现频率
	filter.searchMode=$("input[name='searchMode']:checked").val();
	filter.fromAdvance = true;
	grid.setFilter(filter);
	grid.goToPage(0);
	
	$('#searchWindow').jqxWindow('close');
	
	$("select.selectpicker").each(function(){
        var _this = $(this);
        var sid = _this.attr("id");
        var sval = $("#"+sid).val();
        if(sval != null){
            for(var i=0;i<sval.length;i++){
                _this.find("option[value="+sval[i]+"]").insertBefore(_this.find('option:eq(1)'));
            }
        }
        $("#"+sid).selectpicker('refresh');
    });
}

//同步加载高级查询条件
function loadSearchCondition() {
	//查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
    $.ajax({
        url:"/viewManager/loadAdvanceSerachData",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
            if(data.success) {
            	//只动态更新拍摄地
        		var viewFilterDto = data.viewFilterDto;
                var atmosphereList = viewFilterDto.atmosphereList;
                var shootStatusList = viewFilterDto.shootStatusList;
                var siteList = viewFilterDto.siteList;
                var firstLocationList = viewFilterDto.firstLocationList;
                var secondLocationList = viewFilterDto.secondLocationList;
                var thirdLocationList = viewFilterDto.thirdLocationList;
                var majorRoleList = viewFilterDto.majorRoleList;
                var guestRoleList = viewFilterDto.guestRoleList;
                var massesRoleList = viewFilterDto.massesRoleList;
                var commonPropList = viewFilterDto.commonPropList;
                var specialPropList  = viewFilterDto.specialPropList;
                var clotheList = viewFilterDto.clotheList;
                var makeupList = viewFilterDto.makeupList;
//                var shootLocationList = viewFilterDto.shootLocationList;
                var shootLocationList = viewFilterDto.shootLocationRegionList;
                var advertInfoList = viewFilterDto.advertInfoList;
                var specialRemindList = viewFilterDto.specialRemindList;
                

                $("#atmosphereSelect").empty();
                $("#atmosphereSelect").append("<option value='blank'>[空]</option>");
                for (var atm in atmosphereList) {
                    $("#atmosphereSelect").append("<option value="+ atm + ">" + atmosphereList[atm] + "</option>");
                }
                if (filter.atmosphere == "blank") {
                	$("#atmosphereSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#atmosphereSelect").selectpicker('render');
                } else if (filter.atmosphere) {
                	$("#atmosphereSelect").selectpicker("val", filter.atmosphere.split(","));
                }
                $("#atmosphereSelect").selectpicker('refresh');
                
                
                $("#siteSelect").empty();
                $("#siteSelect").append("<option value='blank'>[空]</option>");
                for (var site in siteList) {
                    $("#siteSelect").append("<option value="+ siteList[site] + ">" + siteList[site] + "</option>");
                }
                if (filter.site == "blank") {
                	$("#siteSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#siteSelect").selectpicker('render');
                } else if (filter.site) {
                	 $("#siteSelect").selectpicker('val', filter.site.split(','));
                }
                $("#siteSelect").selectpicker('refresh');
                
                
                $("#specialRemindSelect").empty();
                $("#specialRemindSelect").append("<option value='blank'>[空]</option>");
                for (var specialRemind in specialRemindList) {
                    $("#specialRemindSelect").append("<option value="+ specialRemindList[specialRemind] + ">" + specialRemindList[specialRemind] + "</option>");
                }
                if (filter.specialRemind == "blank") {
                	$("#specialRemindSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#specialRemindSelect").selectpicker('render');
                } else if (filter.specialRemind) {
                	$("#specialRemindSelect").selectpicker('val', filter.specialRemind.split(","));
                }
                $("#specialRemindSelect").selectpicker('refresh');
                

                $("#shootStatusSelect").empty();
                for (var shotstatus in shootStatusList) {
                    $("#shootStatusSelect").append("<option value="+ shotstatus + ">" + shootStatusList[shotstatus] + "</option>");
                }
                if (filter.shootStatus) {
                	$("#shootStatusSelect").selectpicker('val', filter.shootStatus.split(","));
                }
                $("#shootStatusSelect").selectpicker('refresh');
                

                $("#advertInfoSelect").empty();
                $("#advertInfoSelect").append("<option value='blank'>[空]</option>");
                for (var advert in advertInfoList) {
                    $("#advertInfoSelect").append("<option value="+ advert + ">" + advertInfoList[advert] + "</option>");
                }
                if (filter.advert == "blank") {
                	$("#advertInfoSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#advertInfoSelect").selectpicker('render');
                } else if (filter.advert) {
                	$("#advertInfoSelect").selectpicker('val', filter.advert.split(","))
                }
                $("#advertInfoSelect").selectpicker('refresh');
                

                $("#shootLocationSelect").empty();
                $("#shootLocationSelect").append("<option value='blank'>[空]</option>");
                for (var i= 0; i<shootLocationList.length; i++) {
                    $("#shootLocationSelect").append("<option value="+ shootLocationList[i].shootLocationId + ">" + shootLocationList[i].shootLocationRegion + "</option>");
                }
                if (filter.shootLocation == "blank") {
                	$("#shootLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#shootLocationSelect").selectpicker('render');
                } else if (filter.shootLocation) {
                	$("#shootLocationSelect").selectpicker('val', filter.shootLocation.split(","))
                }
                $("#shootLocationSelect").selectpicker('refresh');
                

                $("#firstLocationSelect").empty();
                $("#firstLocationSelect").append("<option value='blank'>[空]</option>");
                for (var fLocation in firstLocationList) {
                    $("#firstLocationSelect").append("<option value="+ firstLocationList[fLocation] + ">" + firstLocationList[fLocation] + "</option>");
                }
                if (filter.major == "blank") {
                	$("#firstLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#firstLocationSelect").selectpicker('render');
                } else if (filter.major) {
                	$("#firstLocationSelect").selectpicker('val', filter.major.split(','));
                }
                $("#firstLocationSelect").selectpicker('refresh');
                

                $("#secondLocationSelect").empty();
                $("#secondLocationSelect").append("<option value='blank'>[空]</option>");
                for (var sLocation in secondLocationList) {
                    $("#secondLocationSelect").append("<option value="+ secondLocationList[sLocation] + ">" + secondLocationList[sLocation] + "</option>");
                }
                if (filter.minor == "blank") {
                	$("#secondLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#secondLocationSelect").selectpicker('render');
                } else if (filter.minor) {
                	$("#secondLocationSelect").selectpicker('val', filter.minor.split(","));
                }
                $("#secondLocationSelect").selectpicker('refresh');
                
                $("#thirdLocationSelect").empty();
                $("#thirdLocationSelect").append("<option value='blank'>[空]</option>");
                for (var sLocation in thirdLocationList) {
                    $("#thirdLocationSelect").append("<option value="+ thirdLocationList[sLocation] + ">" + thirdLocationList[sLocation] + "</option>");
                }
                if (filter.thirdLevel == "blank") {
                	$("#thirdLocationSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#thirdLocationSelect").selectpicker('render');
                } else if (filter.thirdLevel) {
                	$("#thirdLocationSelect").selectpicker('val', filter.thirdLevel.split(","));
                }
                $("#thirdLocationSelect").selectpicker('refresh');
                

                $("#majorRoleSelect").empty();
                $("#majorRoleSelect").append("<option value='blank'>[空]</option>");
                for (var mrole in majorRoleList) {
                    $("#majorRoleSelect").append("<option value="+ mrole + ">" + majorRoleList[mrole] + "</option>");
                }
                if (filter.roles == "blank") {
                	$("#majorRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#majorRoleSelect").selectpicker('render');
                } else if (filter.roles) {
                	$("#majorRoleSelect").selectpicker('val', filter.roles.split(","));
                }
                $("#majorRoleSelect").selectpicker('refresh');
                

                $("#guestRoleSelect").empty();
                $("#guestRoleSelect").append("<option value='blank'>[空]</option>");
                for (var grole in guestRoleList) {
                    $("#guestRoleSelect").append("<option value="+ grole + ">" + guestRoleList[grole] + "</option>");
                }
                if (filter.guest == "blank") {
                	$("#guestRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#guestRoleSelect").selectpicker('render');
                } else if (filter.guest) {
                	$("#guestRoleSelect").selectpicker('val', filter.guest.split(","));
                }
                $("#guestRoleSelect").selectpicker('refresh');


                $("#massRoleSelect").empty();
                $("#massRoleSelect").append("<option value='blank'>[空]</option>");
                for (var mrole in massesRoleList) {
                    $("#massRoleSelect").append("<option value="+ mrole + ">" + massesRoleList[mrole] + "</option>");
                }
                if (filter.mass == "blank") {
                	$("#massRoleSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#massRoleSelect").selectpicker('render');
                } else if (filter.mass) {
                	$("#massRoleSelect").selectpicker('val', filter.mass.split(","));
                }
                $("#massRoleSelect").selectpicker('refresh');
                

                $("#clothSelect").empty();
                $("#clothSelect").append("<option value='blank'>[空]</option>");
                for (var cloth in clotheList) {
                    $("#clothSelect").append("<option value="+ cloth + ">" + clotheList[cloth] + "</option>");
                }
                if (filter.clothes == "blank") {
                	$("#clothSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#clothSelect").selectpicker('render');
                } else if (filter.clothes) {
                	$("#clothSelect").selectpicker('val', filter.clothes.split(","));
                }
                $("#clothSelect").selectpicker('refresh');

                
                $("#makeupSelect").empty();
                $("#makeupSelect").append("<option value='blank'>[空]</option>");
                for (var makeup in makeupList) {
                    $("#makeupSelect").append("<option value="+ makeup + ">" + makeupList[makeup] + "</option>");
                }
                if (filter.makeup == "blank") {
                	$("#makeupSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#makeupSelect").selectpicker('render');
                } else if (filter.makeup) {
                	$("#makeupSelect").selectpicker('val', filter.makeup.split(","));
                }
                $("#makeupSelect").selectpicker('refresh');

                
                $("#propSelect").empty();
                $("#propSelect").append("<option value='blank'>[空]</option>");
                for (var cprop in commonPropList) {
                    $("#propSelect").append("<option value="+ cprop + ">" + commonPropList[cprop] + "</option>");
                }
                if (filter.props == "blank") {
                	$("#propSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#propSelect").selectpicker('render');
                } else if (filter.props) {
                	$("#propSelect").selectpicker('val', filter.props.split(","));
                }
                $("#propSelect").selectpicker('refresh');

                
                $("#specialPropSelect").empty();
                $("#specialPropSelect").append("<option value='blank'>[空]</option>");
                for (var sprop in specialPropList) {
                    $("#specialPropSelect").append("<option value="+ sprop + ">" + specialPropList[sprop] + "</option>");
                }
                if (filter.specialProps == "blank") {
                	$("#specialPropSelect").find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
                    $("#specialPropSelect").selectpicker('render');
                } else if (filter.specialProps) {
                	$("#specialPropSelect").selectpicker('val', filter.specialProps.split(","));
                }
                $("#specialPropSelect").selectpicker('refresh');
        	}
        }
    });
}

//高级查询窗口初始化时根据当前剧组的不同类型显示不同的数据
function showDifferentSearchData(){
	//根据剧组类型显示不同的集场区间信息
	if (crewType == 0 || crewType == 3) { //电影
		$("#tvbSctionNo").empty();
	}else {
		$("#moviceSectionNo").empty();
	}
	
	//根据剧组类型显示不同的编号信息
	if (crewType == 0 || crewType == 3) {
		$("#tvbSeriesNoLi").css("display", "none");
	}else {
		$("#moviceViewNos").css("display", "none");
	}
}

//初始化场景列表表头的按钮样式
function initTopButtonCss(){
	$("#exportButton").attr("class","");
    $("#exportButton").addClass("daochu_button");
    
    $("#importButton").attr("class", "");
    $("#importButton").addClass("daoru_button");
    
    $("#noticeWindowButton").attr("class","");
    $("#noticeWindowButton").addClass("newapaper_button");
    
    $("#planWindowButton").attr("class","");
    $("#planWindowButton").addClass("camera_button");
    
    $("#addViewButton").attr("class","");
    $("#addViewButton").addClass("building_button");
    
    $("#uploadScenarioButton").attr("class","");
    $("#uploadScenarioButton").addClass("upload_button");
    
    $("#superSearchButton").attr("class","");
    $("#superSearchButton").addClass("search_button");
    
    $("#setAddress").attr("class","");
    $("#setAddress").addClass("install_button");
    
    $("#batchUpdateView").attr("class","");
    $("#batchUpdateView").addClass("batch_update_button");
    
    $("#coalitionViewLocation").attr("class","");
    $("#coalitionViewLocation").addClass("coalition_location_button");
    
    $("#hideGridColumns").attr("class", "");
    $("#hideGridColumns").addClass("hide-columns-btn");
    
    $("#printViewList").attr("class", "");
    $("#printViewList").addClass("print-view-btn");
}

//初始化设置拍摄地点窗口
function initSetAddressWindow(){
    $("#setAddressWindow").jqxWindow({
        theme:theme,  
        width: 640,
        height: 420, 
        autoOpen: false,
        isModal: true,
        resizable: false,
        cancelButton: $('#setAddressClose'),
        initContent: function() {
            //查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
            $.ajax({
	            url:"/sceneViewInfoController/queryShootLocationList",
	            dataType:"json",
	            type:"post",
	            async: true,
	            success:function(data){
                    var shootLocationList = data.shootLocationList;
                    $.each(shootLocationList, function() {
                        $("#addressList").append("<span class='addressOpt' value="+ this.vname +" onclick='addDressSpan(this)'>"+ this.vname +"</span>");
                    });
                }
	        });
            
	        //初始化按钮样式
            $("#setAddressClose, #setAddressButton").jqxButton({
            	theme: theme, 
            	height: 25, 
            	width: 80
            });
            
            $("#addressInput").jqxInput({theme: theme});
            
            $("#addressInput").on('keyup.textchange', function() {
                var _this = $(this);
                var addressList = $("#addressList").find("span");
                addressList.each(function(){
                    var addValue = $(this).text();
                    if(addValue.search($.trim(_this.val())) != -1){
                        $(this).show();
                    } else {
                        $(this).hide();
                    }
                });
            });
        }
     });
}

//将拍摄场地信息保存到数据库中
function setShootAddress(address, shootRegion, viewIds) {
	if(address != null && address != "") {
		var _frame=$('#viewInfoFrame').contents();
	    var option = _frame.find("#locationList").find("li[title="+ address +"]");
	    if (option.length == 0) {
	        
	    	_frame.find("#locationList").append("<li title="+ address +" onclick='addLocation(this)'>"+ address +"</span>");
	    	
//	        $("#locationList").find("span[value="+ address +"]").on('click', function(event) {
//	            $("#addressInput").val($(this).text());
//	            $(this).siblings("span").removeClass("mouse_click");
//	            $(this).addClass("mouse_click");
//	        });
	    }
    }
    
    $.ajax({
        url:"/viewManager/saveAddress",
        data:{viewIds:viewIds,addressStr:address, shootRegion: shootRegion},
        dataType:"json",
        type:"post",
        async:false,
        success:function(data){
            if(data.success){
            	showSuccessMessage(data.message);
            	
                if (filter.shootLocation != null && filter.shootLocation != undefined && filter.shootLocation != "") {
                       filter.shootLocation = address;
                }
//                $('#setAddressWindow').jqxWindow('close'); 
                //refreshViewGridRow({viewIds:viewIds,pagesize:viewIds.split(",").length});
                //refreshViewRowWithNoRequest(address, '', '', viewIds);
            } else {
            	showErrorMessage(data.message);
            }
        }
    });
}

//对表格数据进行不重新请求刷新
function refreshViewRowWithNoRequest(shootLocation,atmosp,site,viewIds) {
    var viewIdArr = viewIds.split(',');
    for(var i = 0;  i< viewIdArr.length; i++){
    	 var rowIndex = grid.getRowIndex(viewIdArr[i]);
    	 var rowData = grid.getRowData(rowIndex);
    	 if (shootLocation != '' && shootLocation != undefined) {
			rowData.shootLocation = shootLocation;
		}
    	if (atmosp != '' && atmosp != undefined) {
 			rowData.atmosphereName = atmosp;
 		}
    	if (site != '' && site != undefined) {
 			rowData.site = site;
 		}
    	 grid.updaterowdata(rowIndex, rowData);
    }
}

//对场景表表格进行行刷新
function refreshViewGridRow(queryCondition) {
	
	$.ajax({
        url:'/viewManager/loadViewList',
        data:queryCondition,
        type:"post",
        async: true,
        success:function(param){
        	var viewData = param.result.resultList;
            
        	for (var i = 0; i < viewData.length; i++) {
                var rowData = viewData[i];
                var rowIndex = grid.getRowIndex(rowData.viewId);
                grid.updaterowdata(rowIndex, rowData);
            }
        }
    });
}

//打开设置拍摄场地窗口
function setViewLoocation(){
	if(grid.getSelectIds()==""){
        showErrorMessage("请选择要设置的场次！");
        return;
	}
	$("#setAddressWindow").jqxWindow("open");
}

//将场景信息添加到拍摄计划中
function addViewToPlan(){
	if(grid.getSelectIds()==""){
        showErrorMessage("请选择场次");
        return;
    }
    $("#planWindow").jqxWindow("open");
}

//将场景信息添加到通告单中
function addViewToNotice(){
	if(grid.getSelectIds()==0){
        showErrorMessage("请选择场次");
        return;
    }
	$('#customWindow').jqxWindow('open');
	
	$("#group").jqxDropDownList('selectIndex', 0);
    $("#noticeTime").val(new Date().Format('yyyy-MM-dd'));
    autoGetNoticeName();
}

//初始化场景信息框
function initViewContent(){
	$('#right_main').jqxSplitter({
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
}

//初始化右侧场景信息窗口的格式及大小
function initAdvertContent(){
	$('#con_right_bottom').jqxSplitter({
        orientation: 'horizontal', 
        width: '100%', 
        height: "100%", 
        resizable: false,
        showSplitBar: false,
        panels: [
            {size:'100%', collapsible: false}, 
            {collapsible: false}
        ]
    });
}

//面板右侧场景框中确定/取消/删除 按钮样式初始化
function initViewButton(){
	$("#btnsure, #btncancle, #btndelete").jqxButton({
        theme: theme,
        width: '50px',
        height: '28px'
    });
}

//自动获取通告单名称
function autoGetNoticeName() {
    var selectGroupItem = $("#group").jqxDropDownList('getSelectedItem');
    if (selectGroupItem == undefined) {
        return false;
    }
    var groupName = selectGroupItem.label;
    var noticeDateStr = $("#noticeTime").val();
    
    var noticeDateArr = noticeDateStr.split("-");
    var noticeDate = noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日";
    
    $("#noticeNameInput").val("《" + crewName + "》" + noticeDate + groupName+"通告");
    
    $("#addNoticeError").html("");
}

//添加新的场景信息
function addNewView(){
	showViewContent("","");
}

//点击场次编号,查询场次的详细信息,当发现右侧场景信息框中有未保存信息时,提示
function showViewContent(viewId, isViewClick){
	//初始化右侧窗口
	var _frame=$('#viewInfoFrame').contents();
	
    //是否修改
    var isChanged = _frame.find('input#isChanged').val();
    
   
    if (isChanged == 1) {
        popupPromptBox("提示", "您有一些操作尚未保存，是否现在离开？", function() {
            showViewDetailInfo(viewId,isViewClick);
        });
        return;
    }
    
    showViewDetailInfo(viewId, isViewClick);
}

//查询场景的详细信息
function showViewDetailInfo(viewId, isViewClick) {
    var index = grid.getRowIndex(viewId);
    $("#right_main").show();
    
    $("#btndelete").show();
    //新增场景功能
    if (viewId == "") {
    	$("#viewInfoFrame").attr("src","/viewManager/toViewDetailInfo");
    	$("#viewSpliter").jqxSplitter('expand');
    	$('#viewContentWindow').jqxWindow("close", function(){
    		$(".show-content-col").removeClass("show-content-col");
    	});
    	
    	$("#btndelete").hide();
    	return false;
    }
    //剧本内容在那行
    $(".show-content-col").removeClass("show-content-col");
    $("#tablebody tr[rowid='"+index+"']").addClass("show-content-col");
    
    //查看场景功能
    if(index >= 0){
    	viewContentRowNo = index;
        
        //执行行点击事件
        if(isViewClick){
            grid.isRowClick=true;
        }
        
        $("#viewInfoFrame").attr("src","/viewManager/toViewDetailInfo?viewId="+viewId+"&flag=view");
        
        $("#viewSpliter").jqxSplitter('expand');
        var ishide = $("#showOrHideScenarioBtn").attr("ishide");
        if(ishide == "1"){
        	$.ajax({
                url:"/viewManager/queryViewContent",
                data:{viewId: viewId},
                dataType:"json",
                type:"post",
                success:function(data){
                	var seriesNoAndViewNo = data.seriesNoAndViewNo;
                    if (crewType == Constants.CrewType.movie || crewType == 3) {
                        $("#viewSpanTitle").html("<div style='margin-left:290px;'>剧本内容</div>");
                    } else {
                        $("#viewSpanTitle").html("<div style='margin-left:290px;'>" + seriesNoAndViewNo + "&nbsp;剧本内容</div>");
                    }
                	
                    $("#viewTitle #title").html(data.title.replace(/\n/g, "<br>"));
//                    if (data.noGetRoleNames != null && data.noGetRoleNames != "") {
//                        $('#viewTitle #noGetRoleNames').show();
//                        $('#viewTitle #noGetRoleNames').html("可能存在的角色：" + data.noGetRoleNames);
//                        
//                    } else {
//                        
//                        $('#viewTitle #noGetRoleNames').hide();
//                        $('#viewTitle #noGetRoleNames').html("");
//                    }
                    $("#viewContentDIV span").html(data.viewContent.replace(/\n/g, "<br>"));
                    var isOpen = $('#viewContentWindow').jqxWindow('isOpen');
                    
                    if (!isOpen) {
                  		$('#viewContentWindow').jqxWindow("open");
                    }
                    
                    //$('#viewContentWindow').jqxWindow('expand');
                }
            });
        }else{
        	$('#viewContentWindow').jqxWindow("close");
        }
        
        
        setTimeout(function() {
            var _frame=$('#viewInfoFrame').contents();
	        var isManualSave = _frame.find("input#isManualSave").val();
	        if (isManualSave == "true") {
	           $("#right_title_span").removeClass("nosave");
	           $("#right_title_span").addClass("saved");
	        } else {
	           $("#right_title_span").removeClass("saved");
	            $("#right_title_span").addClass("nosave");
	        }
        }, 1000);
    }
}

//删除场景信息
function confirmDelView(){
	if(isViewInfoReadonly) {
		showErrorMessage("对不起，您没有权限进行删除");
		return false;
	}
	popupPromptBox("提示", "删除后将不可恢复，请确认是否删除?", function() {
        var _frame=$('#viewInfoFrame').contents();
        var viewId = _frame.find("input[name=viewId]").val();
        $.ajax({
            url:'/viewManager/deleteViewInfo',
            data: {viewIds: viewId},
            type:"post",
            async:false,
            success:function(param){
                if (param.success) {
                   showSuccessMessage(param.message);
                   
                   $("#viewSpliter").jqxSplitter('collapse');
                   
                   $("#viewContentWindow").jqxWindow("close", function(){
                	   $(".show-content-col").removeClass("show-content-col");
                   });
                   
                   grid.refresh();
                } else {
                   showErrorMessage(param.message);
                }
            }
        });
    });
}

//点击场景详情信息列表中的取消按钮
function cancleUpdateView(){
	//是否修改
	var _frame=$('#viewInfoFrame').contents();
    var isChanged = _frame.find('input#isChanged').val();
    if(isChanged == 1){
    	swal({
    	    title: "提示",
            text: '您有一些操作尚未保存，是否要保存？',
            type: "warning",
            showCancelButton: true,  
            confirmButtonColor: "rgba(255,103,2,1)",
            confirmButtonText: "是",   
            cancelButtonText: "否",   
            closeOnConfirm: true,   
            closeOnCancel: true
    	},function (isConfirm){
    		if (isConfirm){
    			saveView();
//    			 _frame.find('input#isChanged').val(0);
    		}else{
    			$("#viewSpliter").jqxSplitter('collapse');
    			$("#viewContentWindow").jqxWindow("close", function(){
    				$(".show-content-col").removeClass("show-content-col");
    			});
    			 _frame.find('input#isChanged').val(0);
    		}
    	});
    }else{
    	$("#viewSpliter").jqxSplitter('collapse');
		$("#viewContentWindow").jqxWindow("close", function(){
			$(".show-content-col").removeClass("show-content-col");
		});
		 _frame.find('input#isChanged').val(0);
    }
   
	
}

//判断拍摄地和拍摄地域是否统一
function judgeShootLocationRegin() {
	var _frame=$('#viewInfoFrame').contents();
	var sourceFrom = _frame.find("input[id='sourceForm']").val();
	var shootLocation = "";
	var shootRegin = "";
    if (sourceFrom == 'batchUpdate'){
    	shootLocation = _frame.find("#shootLocationInput").val();
    	shootRegin = _frame.find("#shootReginInfo").text();
    	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
    }else{
    	shootLocation = _frame.find("#noFinishViewShootLocation").val();
    	shootRegin = _frame.find("#shootReginValue").text();
    	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
    }
    if(shootLocation=="") {
    	if(shootRegin) {
    		if (sourceFrom == 'batchUpdate'){
    			_frame.find("#shootReginInfo").text("");
    		} else {
    			_frame.find("#shootReginValue").text("");
    		}
    		shootRegin="";
    	}
    }	
	if(shootLocation == "" && shootRegin == ""){
		saveViewInfo();
		return;
	}
	$.ajax({
		url: '/viewManager/validateShootLocationRegion',
		type: 'post',
		async: false,
		data: {"shootLocation": shootLocation, "shootRegion": shootRegin},
		datatype: 'json',
		success: function(response){
			if(response.success){
				saveViewInfo();
			}else{
				popupPromptBox("提示","当前地域与原来的地域不一致, 是否要更改 ？", function (){
					saveViewInfo();
				});
			}
		}
	});
}
//保存场景
function saveView(){
	judgeShootLocationRegin();
}
//保存场景详情
function saveViewInfo(){
	var arr = [];
    var _frame=$('#viewInfoFrame').contents();
    var sourceFrom = _frame.find("input[id='sourceForm']").val();
    if (sourceFrom == 'batchUpdate') {
    	//取出选中的场景id字符串
    	var viewIds = grid.getSelectIds();
		//气氛单选框
    	var atmosphereCheck = _frame.find("input[id='atmoIsEnabled']").prop("checked");
    	var cgAtmosphereName = atmosphereCheck;
    	var atmosphereName = "";
    	if (atmosphereCheck) { //选择了气氛单选框
			//取出选择的气氛内容
    		var atmoInfo = _frame.find("input[id='atmosphereInfo']").val();
    		if (atmoInfo == '' || atmoInfo == null || atmoInfo == undefined) {
				showErrorMessage("您勾选了气氛为需要更新项，请选择需要更新的内容！");
				return;
			}
    		atmosphereName = atmoInfo;
		}
    	
    	//特殊提醒
    	 var specialRemarkCheck = _frame.find("input[id='specialRemarkIsEnabled']").prop("checked");
    	 var specialRemark = "";
    	 var cgSpecialRemark = specialRemarkCheck;
    	 if (specialRemarkCheck) {
    		//取出选择的内外内容
     		var specialRemarkeInfo = _frame.find("input[id='specialRemarkInfo']").val();
     		if (specialRemarkeInfo == '' || specialRemarkeInfo == null || specialRemarkeInfo == undefined) {
 				showErrorMessage("您勾选了特殊提醒为需要更新项，请填写需要更新的内容！");
 				return;
 			}
     		specialRemark = specialRemarkeInfo;
		}
    	
    	//内外单选框
    	 var siteCheck = _frame.find("input[id='siteInfoIsEnabled']").prop("checked");
    	 var site = "";
    	 var cgSite = siteCheck;
    	 if (siteCheck) {
    		//取出选择的内外内容
     		var siteInfo = _frame.find("input[id='siteInfo']").val();
     		if (siteInfo == '' || siteInfo == null || siteInfo == undefined) {
 				showErrorMessage("您勾选了内外为需要更新项，请选择需要更新的内容！");
 				return;
 			}
     		site = siteInfo;
		}
    	 
    	//场景状态
    	var statusCheck = _frame.find("input[id='statusEnabled']").prop("checked");
    	var cgShootStatus = statusCheck;
    	var shootStatus ="";
    	var statusStr = grid.getSelectStatus();
    	var viewNoStatusArr = statusStr.split(",");
    	if (statusCheck) {
    		//取出的季节内容
    		var statusInfo = _frame.find("#batchStatus option:selected").val();
    		if (statusInfo == '' || statusInfo == null || statusInfo == undefined) {
    			showErrorMessage("您勾选了场景状态为需要更新项，请选择需要更新的状态！");
 				return;
			}

    		var message = "";
    		//判断选择的状态
    		if (statusInfo == 0) {
				//设置状态为未完成状态，选择的场景只能是删戏状态
    			 //判断选中的场次是否有已经完成的
        	    for(var i=0; i<viewNoStatusArr.length; i++){
        	    	var viewNoStatusStr = viewNoStatusArr[i];
        	    	var statusArr = viewNoStatusStr.split("&");
        	    	var status = statusArr[0];
        	    	var viewNo = statusArr[1];
        	    	if (status != 3) {
        				if (message == "") {
        					message = viewNo + "不是删戏状态，只能是删戏状态才能改为未拍状态";
        				}else {
        					message = message + "，" +viewNo + "不是删戏状态，只有删戏状态才能改为未拍状态";
        				}
        			}
        	    }
			}else if (statusInfo == 3) {
				//设置状态为删戏状态，选择的场景只能是未完成的状态
				 //判断选中的场次是否有已经完成的
        	    for(var i=0; i<viewNoStatusArr.length; i++){
        	    	var viewNoStatusStr = viewNoStatusArr[i];
        	    	var statusArr = viewNoStatusStr.split("&");
        	    	var status = statusArr[0];
        	    	var viewNo = statusArr[1];
        	    	if (status != 0) {
        				if (message == "") {
        					message = viewNo + "不是未拍状态，只能是未拍状态才能改为删戏状态";
        				}else {
        					message = message + "，" +viewNo + "不是未拍状态，只有未拍状态才能改为删戏状态";
        				}
        			}
        	    }
			}
    		shootStatus = statusInfo;
    		
    		if (message != '') {
    			showErrorMessage(message);
    			return;
    		}
		}
    	
    	//设置拍摄地
    	var shootAddress = _frame.find("input[id=setShootLocation]").prop("checked");
    	var shootLocation = "";
    	var shootRegion="";
    	if(shootAddress){
    		shootLocation = _frame.find("input[id=shootLocationInput]").val();
    		shootRegion = _frame.find("#shootReginInfo").text();
    		shootRegion=shootRegion.replace(/\(/g,'').replace(/\)/g,'');
    	}
    	//统一场景信息
    	var unifiedEnabled = _frame.find("input[id=unifiedEnabled]").prop("checked");
    	
    	var saveMessage = '';
    	var finishView = [];
    	var deleteView = [];
    	for(var i=0; i<viewNoStatusArr.length; i++){
	    	var viewNoStatusStr = viewNoStatusArr[i];
	    	var statusArr = viewNoStatusStr.split("&");
	    	var status = statusArr[0];
	    	var viewNo = statusArr[1];
	    	if (status ==  2 || status == 5) {
	    		finishView.push(viewNo);
			}else if (status ==  3) {
				deleteView.push(viewNo);
			}
	    }
    	if (finishView != null && finishView.length>0) {
			for(var i= 0; i<finishView.length; i++){
				if (saveMessage == '') {
					saveMessage = finishView[i];
				}else {
					saveMessage = saveMessage + "," + finishView[i];
				}
			}
		}
    	if (saveMessage != '') {
    		saveMessage = saveMessage +" 已经完成；";
		}
    	
    	if (deleteView != null && deleteView.length>0) {
			for(var i= 0; i<deleteView.length; i++){
				if (saveMessage == '') {
					saveMessage = deleteView[i];
				}else if (saveMessage != '' && i== 0) {
					saveMessage = saveMessage + deleteView[i];
				}else{
					saveMessage = saveMessage + "," + deleteView[i];
				}
			}
			if (saveMessage != '') {
				saveMessage = saveMessage +" 已经删戏；";
			}
		}
    	if (saveMessage == '') {
    		if(shootAddress){
    			//保存拍摄地点信息
    			setShootAddress(shootLocation, shootRegion, viewIds);
    		}
    		//保存场景信息
    		if(unifiedEnabled){
        		saveBtchViewLocation();
        	}
    		//向后台更新数据
			$.ajax({
				url:'/viewManager/batchUpdateView',
				data:{viewIds: viewIds,cgAtmosphereName:cgAtmosphereName,atmosphereName:atmosphereName,
					cgSite:cgSite,site:site,cgShootStatus:cgShootStatus, shootStatus: shootStatus,cgSpecialRemark: cgSpecialRemark, specialRemark: specialRemark},
					type:"post",
					success:function(response){
						if (response.success) {
							showSuccessMessage(response.message);
							
							//刷新场景表
							if (viewIds != null && viewIds != undefined && viewIds != "") {
								var viewIdArr = viewIds.split(',');
								refreshViewGridRow({viewIds:viewIds,pagesize:viewIdArr.length});
							} else {
								grid.refresh();
							}
							
						} else {
							showErrorMessage(response.message);
						}
					},
					error: function() {
						showErrorMessage("发送请求失败");
					}
			});
		}else {
			popupPromptBox("提示", saveMessage + " 是否要继续保存？", function() {
				if(shootAddress){
	    			//保存拍摄地点信息
	    			setShootAddress(shootLocation, shootRegion, viewIds);
	    		}
				//保存场景信息
	    		if(unifiedEnabled){
	        		saveBtchViewLocation();
	        	}
				//向后台更新数据
				$.ajax({
					url:'/viewManager/batchUpdateView',
					data:{viewIds: viewIds,cgAtmosphereName:cgAtmosphereName,atmosphereName:atmosphereName,
						cgSite:cgSite,site:site,cgShootStatus:cgShootStatus, shootStatus: shootStatus,cgSpecialRemark: cgSpecialRemark, specialRemark: specialRemark},
						type:"post",
						success:function(response){
							if (response.success) {
								showSuccessMessage(response.message);
								//刷新场景表
								if (viewIds != null && viewIds != undefined && viewIds != "") {
									var viewIdArr = viewIds.split(',');
									refreshViewGridRow({viewIds:viewIds,pagesize:viewIdArr.length});
								} else {
									grid.refresh();
								}
								
							} else {
								showErrorMessage(response.message);
							}
						},
						error: function() {
							showErrorMessage("发送请求失败");
						}
				});
			});
		}
    	
	}else {
		var viewId = _frame.find("input[name=viewId]").val();
		//集.
		var seriesNo = _frame.find('input.scene_set').val();
		//场
		var viewNo = _frame.find('input.scene_field').val();
		if (crewType == 0|| crewType == 3) { //电影或网大
//			if (seriesNo == null || seriesNo == '' || seriesNo == undefined) {
//				_frame.find('input.scene_set').val(1);
//				seriesNo = 1;
//			}
			if (viewNo == null || viewNo == "" || viewNo == undefined) {
				showErrorMessage("请填写场次信息");
				return;
			}
		}else {
			if (seriesNo == null || seriesNo == ""  || seriesNo == undefined  
					|| viewNo == null || viewNo == "" || viewNo == undefined) {
				showErrorMessage("请填写集场信息");
				return;
			}
		}
		
		_frame.find('input#isChanged').val(0);
		
		//是否只读权限校验
	    if(isViewInfoReadonly) {
	    	showErrorMessage("对不起，您没有权限进行修改");
	    	$('#viewInfoFrame')[0].contentWindow.location.reload(true);
			return;
	    }
		
		//主要内容
		var mainContent = _frame.find("input.scene_content").val();
		if (mainContent.length >= 250) {
			showErrorMessage("亲，主要内容太长，不能超过250个字哦");
			return;
		}
		
		//特殊提醒
		var specialRemind = _frame.find("input.special_remind").val();
		if (specialRemind.length >=100) {
			showErrorMessage("特殊提醒不能超过100个字哦");
			return;
		}
		
		//主场景
		var firstLocation = _frame.find("input.scene_first").val();
		//次场景
		var secondLocation = _frame.find("input.scene_second").val();
		//三级场景
		var thirdLocation = _frame.find("input.scene_third").val();
		if (firstLocation == "" || firstLocation == null) {
			//判断二级场景是否为空
			if (secondLocation == '' || secondLocation == null) {
				//判断三级场景是否为空
				if (thirdLocation != '' && thirdLocation != null) {
					showErrorMessage("请先填写主场景和次场景，再填写三级场景");
					return false;
				}
			}else {
				showErrorMessage("请先填写主场景，再填写次级场景");
				return false;
			}
		}else {
			//判断二级场景是否为空
			if (secondLocation == '' || secondLocation == null) {
				//判断三级场景是否为空
				if (thirdLocation != '' && thirdLocation != null) {
					showErrorMessage("请先填写次场景，再填写三级场景");
					return false;
				}
			}
		}
		//拍摄地域:
		var shootRegin = _frame.find("#shootReginValue").text();
    	shootRegin=shootRegin.replace(/\(/g,'').replace(/\)/g,'');
		_frame.find('input.shoot_regin_info').val(shootRegin);
		
		//主要演员
		var majorName='';
		_frame.find('div.performer_first ul .tagInput').siblings('li').each(function(){
			var value = $(this).text();
			var subValue = value.substring(value.indexOf("(")+1,value.indexOf(")"));
			if( subValue != null && subValue.length>0){
				majorName= $.trim( $(this).text() ).substring(0,value.indexOf("("));
				majorName=majorName.replace(/\s*/g,'');
				arr.push( majorName+ '(' + subValue +')');
			}else{
				majorName=$.trim( $(this).text());
				majorName=majorName.replace(/\s*/g,'');
				arr.push(majorName);
			}
		});
		_frame.find('input.performer_first').val(arr);
		arr.length=0;
		
		//特约演员        
		_frame.find('div.performer_special ul .tagInput').siblings('li').each(function(){
			arr.push($(this).text());
		});
		_frame.find('input.performer_special').val(arr);
		arr.length=0;
		//群众演员
		var _cList=[];// 群众演员的名称
		var figurantName='';        
		_frame.find('div.performer_common ul .tagInput').siblings('li').each(function(){
			if( /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) ) != null){
				figurantName= $.trim( $(this).text() ).replace(/[\(（](\d*)[\)）]/g, '');
				figurantName=figurantName.replace(/\s*/g,'');
				arr.push( figurantName+ '_' + /[\(（](\d*)[\)）]/g.exec( $.trim( $(this).text() ) )[1] );
			}else{
				figurantName=$.trim( $(this).text());
				figurantName=figurantName.replace(/\s*/g,'');
				arr.push(figurantName);
			}
			_cList.push(figurantName);
		});
		_frame.find('input.performer_common').val(arr);
		arr.length=0;
		
		// 主要演员
		var _fList = _frame.find('input.performer_first').val().replace(/\s*/g,'').split(','),
		// 特约演员
		_sList = _frame.find('input.performer_special').val().replace(/\s*/g,'').split(',');
		
		// 是否有重复
		var isRepeat = false;
		for(var i = 0; i < _fList.length; i++){
			// 主要演员
			var f = _fList[i];
			// 主要 与 特约 是否有重复
			if($.inArray(f, _sList) != -1 && f != ''){
				isRepeat = true;
				showErrorMessage('保存失败！主要演员 与 特约演员 存在重复：' + f);    
				break;
			}
			
			// 主要 与 群众 是否有重复            
			if($.inArray(f, _cList) != -1 && f != ''){
				isRepeat = true;
				showErrorMessage('保存失败！主要演员 与 群众演员 存在重复：' + f);    
				break;
			}
		}
		
		// 有重复 直接跳出
		if(isRepeat){
			return false;
		}
		//普通道具
		arr.length=0;
		_frame.find('div.tool_main ul .tagInput').siblings('li').each(function(){
			arr.push($(this).text());
		});
		_frame.find('input.tool_main').val(arr);
		
		//特殊道具
		arr.length=0;
		_frame.find('div.tool_special ul .tagInput').siblings('li').each(function(){
			arr.push($(this).text());
		});
		_frame.find('input.tool_special').val(arr);
		
		//服装
		arr.length=0;
		_frame.find('div.clothes_info ul .tagInput').siblings('li').each(function(){
			arr.push($(this).text());
		});
		_frame.find('input.clothes_info').val(arr);
		
		//化妆
		arr.length=0;
		_frame.find('div.makeup_info ul .tagInput').siblings('li').each(function(){
			arr.push($(this).text());
		});
		_frame.find('input.makeups_info').val(arr);
		
		//禁用按钮
		$("#saveNoticeButton").prop("disabled","disabled");
		
		//拍摄状态
		var shootStatus = _frame.find('input#shootStatus').val();
		if (shootStatus ==  2 || shootStatus == 5) {
			popupPromptBox("提示", "当前场景已经完成，是否要继续保存？", function() {
				publicSaveViewInfo(_frame);
				$('#viewInfoFrame')[0].contentWindow.location.reload(true);
				return;
			});
		}else if (shootStatus == 3) {
			popupPromptBox("提示", "当前场景已删戏，是否要继续保存？", function() {
				publicSaveViewInfo(_frame);
				$('#viewInfoFrame')[0].contentWindow.location.reload(true);
				return;
			});
		}else {
			publicSaveViewInfo(_frame);
			$('#viewInfoFrame')[0].contentWindow.location.reload(true);
			return;
		}
		
	}
}

//保存场景的公用方法
function publicSaveViewInfo(_frame) {
	var viewId = _frame.find("input[name=viewId]").val();
	$.ajax({
		url:'/viewManager/saveViewInfo',
		data:_frame.contents().find('form').serialize(),
		type:"post",
		success:function(response){
			if (response.success) {
				
				$("#viewInfoFrame").attr("src","/viewManager/toViewDetailInfo?viewId="+viewId);
				//判断该场景信息是否保存过,若果保存过,则不再保存
				if ( $("#right_title_span").hasClass("nosave")) {
					$("#right_title_span").removeClass("nosave");
					$("#right_title_span").addClass("saved");
				}
				
				//刷新场景表
				if (viewId != null && viewId != undefined && viewId != "") {
					refreshViewGridRow({viewIds: viewId});
				} else {
					grid.refresh();
				}
				
				showSuccessMessage(response.message);
			} else {
				showErrorMessage(response.message);
			}
			//启用保存按钮
			$("#saveNoticeButton").prop("disabled",false);
		},
		error: function() {
			showErrorMessage("发送请求失败");
		}
	});
}

//初始化剧本导出窗口
function initExportWindow(){
	$("#exportScenarioWindow").jqxWindow({
		theme:theme,  
		width: 250,
		height: 130, 
		autoOpen: false,
		maxWidth: 2000,
		maxHeight: 1500,
		resizable: true,
		isModal: true,
		showCloseButton: false,
		resizable: true,
		cancelButton: $("#cancelExpBtn"),
   });
}

function confirmExport(){
	//隐藏导出窗口
	$("#exportScenarioWindow").jqxWindow("close");
	
	var form = $("<form></form>");
	
    var atmosphere =$("#atmosphereSelect").val();
    var site =$("#siteSelect").val();
    var major =$("#firstLocationSelect").val();
    var minor =$("#secondLocationSelect").val();
    var clothes =$("#clothSelect").val();
    var makeup =$("#makeupSelect").val();
    var roles = $("#majorRoleSelect").val(); 
    var props = $("#propSelect").val(); 
    var specialProps = $("#specialPropSelect").val();
    var guestRole = $("#guestRoleSelect").val(); 
    var massRole = $("#massRoleSelect").val(); 
    var shootStatus = $("#shootStatusSelect").val();
    var advert = $("#advertInfoSelect").val();
    var shootLocation =$("#shootLocationSelect").val();
    var mainContent = $("#mainContent").val();
    var remark = $("#viewRemark").val();
    var seriesViewNos = $("#seriesViewNos").val();
   /* //季节信息
	if(season!=null && season!=""){
		form.append("<input type='hidden' name='season'>");
		var seasonStr = "";
		for(var i=0;i<season.length;i++){
			seasonStr+=season[i]+",";
		}
		seasonStr=seasonStr.substring(0,seasonStr.length-1);
		form.find("input[name='season']").val(seasonStr);
	}*/
	//气氛
	if(atmosphere!=null && atmosphere!=""){
		form.append("<input type='hidden' name='atmosphere'>");
		var atmosphereStr = "";
		for(var i=0;i<atmosphere.length;i++){
			atmosphereStr+=atmosphere[i]+",";
		}
		atmosphereStr=atmosphereStr.substring(0,atmosphereStr.length-1);
		form.find("input[name='atmosphere']").val(atmosphereStr);
		filter.atmosphere=atmosphereStr;
		
	}
	//内外景
	if(site!=null && site!=""){
		form.append("<input type='hidden' name='site'>");
		var siteStr = "";
		for(var i=0;i<site.length;i++){
			siteStr+=site[i]+",";
		}
		siteStr=siteStr.substring(0,siteStr.length-1);
		form.find("input[name='site']").val(siteStr);
		//filter.site=siteStr;
	}
	//文武特效
	/*if(viewType!=null && viewType!=""){
		form.append("<input type='hidden' name='type'>");
		var viewTypeStr = "";
		for(var i=0;i<viewType.length;i++){
			viewTypeStr+=viewType[i]+",";
		}
		viewTypeStr=viewTypeStr.substring(0,viewTypeStr.length-1);
		form.find("input[name='viewType']").val(viewTypeStr);
		//filter.viewType= viewTypeStr;
	}*/
	//拍摄状态
	if(shootStatus!=null&& shootStatus!=""){
		form.append("<input type='hidden' name='shootStatus'>");
		var shootStatusStr = "";
		for(var i=0;i<shootStatus.length;i++){
			shootStatusStr+=shootStatus[i]+",";
		}
		shootStatusStr=shootStatusStr.substring(0,shootStatusStr.length-1);
		form.find("input[name='shootStatus']").val(shootStatusStr);
		//filter.shootStatus= shootStatusStr;
	}
	//角色信息
	if(roles!= null && roles!=""){
		var role = "";
		
		for(var i=0;i<roles.length;i++){
			role+=roles[i]+",";
		}
		role=role.substring(0,role.length-1);
		form.append("<input type='hidden' name='roles'>");
		form.find("input[name='roles']").val(role);
		//filter.roles=role;
	}
	//道具
	if(props!= null && props!=""){
		var prop = "";
		
		for(var i=0;i<props.length;i++){
			prop+=props[i]+",";
		}
		prop=prop.substring(0,prop.length-1);
		form.append("<input type='hidden' name='props'>");
		form.find("input[name='props']").val(prop);
		//filter.props=prop;
	}
	//特殊道具
	if (specialProps != null && specialProps != "") {
	   var specialProp = "";
	   for (var i = 0; i < specialProps.length; i++) {
	       specialProp += specialProps[i] + ",";
	   }
	   specialProp = specialProp.substring(0, specialProp.length-1);
	   form.append("<input type='hidden' name='specialProps'>");
       form.find("input[name='specialProps']").val(specialProp);
       //filter.specialProps = specialProp;
	}
	//特约演员
	if(guestRole!= null && guestRole!=""){
		var guest = "";
		
		for(var i=0;i<guestRole.length;i++){
			guest+=guestRole[i]+",";
		}
		guest=guest.substring(0,guest.length-1);
		form.append("<input type='hidden' name='guest'>");
		form.find("input[name='guest']").val(guest);
		//filter.guest=guest;
	}
	//群众演员
	if(massRole!= null && massRole!=""){
		var mass = "";
		
		for(var i=0;i<massRole.length;i++){
			mass+=massRole[i]+",";
		}
		mass=mass.substring(0,mass.length-1);
		form.append("<input type='hidden' name='mass'>");
		form.find("input[name='mass']").val(mass);
		//filter.mass=mass;
	}
	//商植
	if(advert!= null && advert!=""){
		var advertStr = "";
		for(var i=0;i<advert.length;i++){
			advertStr+=advert[i]+",";
		}
		advertStr=advertStr.substring(0,advertStr.length-1);
		form.append("<input type='hidden' name='advert'>");
		form.find("input[name='advert']").val(advertStr);
		//filter.advert=advertStr;
	}
	//服装
    if(clothes!= null && clothes!=""){
        var clothesStr = "";
        for(var i=0;i<clothes.length;i++){
            clothesStr+=clothes[i]+",";
        }
        clothesStr=clothesStr.substring(0,clothesStr.length-1);
        form.append("<input type='hidden' name='clothes'>");
        form.find("input[name='clothes']").val(clothesStr);
        //filter.advert=clothesStr;
    }
    //化妆
    if(makeup!= null && makeup!=""){
        var makeupStr = "";
        for(var i=0;i<makeup.length;i++){
            makeupStr+=makeup[i]+",";
        }
        makeupStr=makeupStr.substring(0,makeupStr.length-1);
        form.append("<input type='hidden' name='makeup'>");
        form.find("input[name='makeup']").val(makeupStr);
        //filter.advert=makeupStr;
    }
    //拍摄地点
    if(shootLocation!= null && shootLocation!=""){
        var shootLocationStr = "";
        for(var i=0;i<shootLocation.length;i++){
            shootLocationStr+=shootLocation[i]+",";
        }
        shootLocationStr=shootLocationStr.substring(0,shootLocationStr.length-1);
        form.append("<input type='hidden' name='shootLocation'>");
        form.find("input[name='shootLocation']").val(shootLocationStr);
        //filter.advert=shootLocationStr;
    }
    //主要场景
    if(major!= null && major!=""){
        var majorStr = "";
        for(var i=0;i<major.length;i++){
            majorStr+=major[i]+",";
        }
        majorStr=majorStr.substring(0,majorStr.length-1);
        form.append("<input type='hidden' name='major'>");
        form.find("input[name='major']").val(majorStr);
        //filter.advert=majorStr;
    }
    //次要场景
    if(minor!= null && minor!=""){
        var minorStr = "";
        for(var i=0;i<minor.length;i++){
            minorStr+=minor[i]+",";
        }
        minorStr=minorStr.substring(0,minorStr.length-1);
        form.append("<input type='hidden' name='minor'>");
        form.find("input[name='minor']").val(minorStr);
        //filter.advert=minorStr;
    }
	//最小集次号
    var startSeriesNo = $("#startSeriesNo").val();
    if (crewType == 0|| crewType == 3) {
    	if (startSeriesNo =="" || startSeriesNo == null) {
    		$("#startSeriesNo").val('1');
    		startSeriesNo = '1';
		}
    }
	if(startSeriesNo!="" && startSeriesNo != null){
		//filter.startSeriesNo=$("#startSeriesNo").val();
		form.append("<input type='hidden' name='startSeriesNo'>");
		form.find("input[name='startSeriesNo']").val($("#startSeriesNo").val());
	}
	//最小场次号
	var startViewNo = $("#startViewNo").val();
	if(startViewNo!="" && startViewNo != null){
		//filter.startViewNo=$("#startViewNo").val();
		form.append("<input type='hidden' name='startViewNo'>");
		form.find("input[name='startViewNo']").val($("#startViewNo").val());
	}
	
	//结束集次号
	 if (crewType == 0|| crewType == 3) {
	    	if ($("#endSeriesNo").val() =="" || $("#endSeriesNo").val() == null) {
	    		$("#endSeriesNo").val('1');
			}
	   }
	if($("#endSeriesNo").val()!=""){
		//filter.endSeriesNo=$("#endSeriesNo").val();
		form.append("<input type='hidden' name='endSeriesNo'>");
		form.find("input[name='endSeriesNo']").val($("#endSeriesNo").val());
	}
	//结束场次号
	if($("#endViewNo").val()!=""){
		//filter.endViewNo=$("#endViewNo").val();
		form.append("<input type='hidden' name='endViewNo'>");
		form.find("input[name='endViewNo']").val($("#endViewNo").val());
	}
	//对集次号集场次号进行数据验证
	if (crewType != 0 && crewType != 3) {
		if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
			showErrorMessage("请填写集数");
			return;
		}
		
	} 
	
	if (crewType != 0 && crewType != 3) {
		if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
			showErrorMessage("请填写集数");
			return;
		}
		
	}
	//集场次编号
	if (seriesViewNos != null && seriesViewNos != "") {
        //filter.seriesViewNos = seriesViewNos;
        form.append("<input type='hidden' name='seriesViewNos'>");
        form.find("input[name='seriesViewNos']").val(seriesViewNos);
    }
	//主要内容
    if (mainContent != "" && mainContent != null) {
     //filter.mainContent = mainContent;
     form.append("<input type='hidden' name='mainContent'>");
     form.find("input[name='mainContent']").val(mainContent);
    }
    //备注
    if (remark != "" && remark != null) {
     //filter.remark = remark;
     form.append("<input type='hidden' name='remark'>");
     form.find("input[name='remark']").val(remark);
    }
	//排序字段
	if (filter.sortType != null && filter.sortType != '') {
	   var sortType = filter.sortType;
	    form.append("<input type='hidden' name='sortType'>");
	    form.find("input[name='sortType']").val(sortType);
	}
	
	//是否导出剧本
	var exportViewContent = $("#exportViewContent").prop("checked");
	form.append("<input type='hidden' name='exportViewContent'>");
	form.find("input[name='exportViewContent']").val(exportViewContent);
	
	//是否导出场景
	var exportViewInfo = $("#exportViewInfo").prop("checked");
	form.append("<input type='hidden' name='exportViewInfo'>");
	form.find("input[name='exportViewInfo']").val(exportViewInfo);
	
	//排序方式
	/*form.append("<input type='hidden' name='sortFlag'>");
	form.find("input[name='sortFlag']").val($("input[name='sortFlag']:checked").val());*/
	
	if (exportViewInfo || exportViewContent) {
		/*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
		var _PageHeight = document.documentElement.clientHeight;
		//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
		var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
		_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
		//在页面未加载完毕之前显示的loading Html自定义内容
		var _LoadingHtml = $("#loadingDiv");
		$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
		//呈现loading效果
		_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
		_LoadingHtml.show();
	}
    
    var fileAddress ="";
    var fileName = "";
    var contentPath = "";
    
    //导出场景
    if (exportViewInfo) {
    	$.ajax({
    		url:"/viewManager/exportExcel",
    		data:form.serialize(),
    		dataType:"json",
    		type:"post",
    		success:function(response){
    			if (response.success) {
    				_LoadingHtml.hide();
    				$(".opacityAll").hide();
    				fileAddress = response.downloadPath;
    				fileName = response.fileName;
    				
    			}else{
    				showErrorMessage(response.message);
    				return;
    			}
    			//导出场景
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
	}
    
    //导出剧本
    if (exportViewContent) {
    	$.ajax({
    		url:"/viewManager/exportViewContent",
    		data:form.serialize(),
    		dataType:"json",
    		type:"post",
    		success:function(response){
    			if (response.success) {
    				_LoadingHtml.hide();
    				$(".opacityAll").hide();
    				contentPath = response.contentPath;
    				
    			}else{
    				showErrorMessage(response.message);
    				return;
    			}
    			//导出场景
				var form = $("<form></form>");
				form.attr("action","/fileManager/downloadFileByAddr");
				form.attr("method","post");
				form.append("<input type='hidden' name='address'>");
				form.find("input[name='address']").val(contentPath);
				$("body").append(form);
				form.submit();
				
				form.remove();
    		}
    	});
	
	}
}
//导出场景表
function downloadExcel(){
	$("#exportScenarioWindow").jqxWindow("open");
}

//初始化剧本信息框,并加载剧本框绑定的事件
function loadViewContentWindow(){

    var windowWidth = document.body.clientWidth;
    
	$('#viewContentWindow').jqxWindow({
        theme:theme,
        autoOpen:false,
        showCollapseButton: true, 
        maxHeight: 800, 
        maxWidth: 700, 
        minHeight: 400, 
        minWidth: 300, 
        draggable: true,
        height: 500, 
        width: 800,
        resizable: false,
        initContent: function () {
        }
    });
    
	//折叠
    $('#viewContentWindow').unbind("collapse");
    $('#viewContentWindow').on('collapse', function (event) {
        var contentWidth = $(this).width();
        $('#viewContentWindow').jqxWindow('move', windowWidth-contentWidth-375, 86);
    });
    
    //展开
    $('#viewContentWindow').unbind("expand");
    $('#viewContentWindow').on('expand', function (event) {
        
    });
    
    //关闭
//    $('#viewContentWindow').unbind("close");
//    $('#viewContentWindow').on('close', function (event) {
//    	$(".show-content-col").removeClass("show-content-col");
//    });
    
    $("#viewContentWindow").unbind("click");
    $("#viewContentWindow").on("click", function() {
        Popup.hide();
    });
}

//关闭剧本上传页面
function closeUploadWin() {
  $("#uploadScenarioWindow").jqxWindow("close");
}

//调用翻页
function pageChanged(event){
	//上下页时页数到达极限的处理
	if(event.data.pageNo < 0){
		return false;
	}
	if (event.data.pageNo >= event.data.pageCount) {
		return false;
	}
	
	//指定页时，页数到达极限的处理
	if ($("#pagenum").val()-1 >= event.data.pageCount) {
		$("#pagenum").val($("#oldPageNum").val());
		return false;
	}
	if ($("#pagenum").val()-1 < 0) {
		$("#pagenum").val($("#oldPageNum").val());
		return false;
	}
	
	event.data.grid.goToPage(event.data.pageNo);
};

/***************************屏幕遮幕*****************************/
function loadDiv(message) {
    var sub = "<div style='z-index: 9999999; margin-left: -66px; margin-top: -24px; position: relative; width: 100px; height: 45px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;'>"
            +"<div style='float: left; overflow: hidden; width: 32px; height: 32px;' class='jqx-grid-load'/>" 
            + "<span style='margin-top: 10px; float: left; display: block; margin-left: 5px;' >" + message + "</span>" 
            + "</div></div>";
    var div = "<div id='_layer_'> " 
            + "<div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 99999; position: absolute;"
            + "left: 0; top: 0; overflow: hidden; display: none'>" 
            + "</div>" 
            + "<div id='_wait_' style='z-index: 9999999; position: absolute; width:430px;height:218px; display: none'  >" 
            + "<center>" 
            + sub
            + "</center>" 
            + "</div>" 
            + "</div>";
    return div;
}

//设置拍摄地点时点击下拉框时触发
function addDressSpan(own){
	 $("#addressInput").val($(own).text());
     $(own).siblings("span").removeClass("mouse_click");
     $(own).addClass("mouse_click");
}

//上传剧本
function uploadScenario() {
    $("#uploadScenarioWindow").jqxWindow('open');
}

//展示选择下拉框
function showSelectPanel(data) {
	  Popup.show({
	      dataList: data.dataJson,
	      right: data.right,
	      arrowTop: data.arrowTop,
	      title: data.title,
	      multiselect: data.multiselect,
	      currentTarget: data.currentTarget,
	      top: 85
	  });
}

//设置消息显示的格式
function LayerShow(message) {
    $(window).resize(Position);
    Position();
    $("#_MaskLayer_").show();
    $("#_wait_").show();
}

//刷新场景表格数据
function reloadPage() {
    $("#jqxgrid").jqxGrid('updatebounddata', 'cells');
}

/*//重置上传剧本页面的高度和宽度
function resizeWindow() {
  var uploadWinWidth = $("#uploadScenarioWindow").width();
  if (uploadWinWidth == "985") {
      $("#uploadScenarioWindow").jqxWindow('resize', 440, 520);
  } else {
      $("#uploadScenarioWindow").jqxWindow('resize', 985, 520);
  }
}*/

//显示上传结果页面
function showResultWindow() {
	$("#uploadResultWindow").jqxWindow("open");
}

//根据起始集场号,刷新剧本分析页面
function refreshPage() {
  window.location.href="/viewManager/toviewListPage";
}


//获取拍摄状态的颜色
function getColor(shootStatus){
	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(shootStatus);
	return divColor;
}

//重置文本框的高度
function resizeViewGird() {
    var tableheadHeight = $("#tableHead").height();
    $(".cc").css("height", window.innerHeight - 150 - tableheadHeight);
}

//修改表格调用
function aa() {
	var b = document.getElementById("cc").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}

//对计划文本框中的时间进行格式化后进行赋值
function initPlanDate(){
	$("#planStartTime").val(currDate);
	$("#planEndTime").val(currDate);
}

//初始化定位信息
function Position() {
    $("#_MaskLayer_").width($(document).width());
    var deWidth = $(window).width();
    $("#_wait_").css({
        left : (deWidth - $("#_wait_").width()) / 2 + "px",
    });
}

//隐藏并移除特殊显示
function LayerHide() {
    $("#_MaskLayer_").hide();
    $("#_wait_").hide();
    del();
}

//移除特殊显示
function del() {
    var delDiv = document.getElementById("_layer_");
    delDiv.parentNode.removeChild(delDiv);
};

//批量修改场景信息
function batchUpdateViewInfo(){
	 //获取选中的场景IDs
    if(grid.getSelectIds() == ""){
        showErrorMessage("请选择场次");
        return;
    }
    var message = "";
    //判断选中的场次是否有已经完成的
    var statusStr = grid.getSelectStatus();
    var viewNoStatusArr = statusStr.split(",");
   /* for(var i=0; i<viewNoStatusArr.length; i++){
    	var viewNoStatusStr = viewNoStatusArr[i];
    	var statusArr = viewNoStatusStr.split("&");
    	var status = statusArr[0];
    	var viewNo = statusArr[1];
    	if (status == 2) {
			if (message == "") {
				message = viewNo + "已完成，不能修改";
			}else {
				message = message + "；" +viewNo + "已完成，不能修改";
			}
		}
    }*/
    
    if (message != '') {
		showErrorMessage(message);
		return;
	}
    
    $("#right_main").show();
	$("#viewInfoFrame").attr("src","/viewManager/toBatchUpdateViewPage");
	$("#viewSpliter").jqxSplitter('expand');
	$('#viewContentWindow').jqxWindow("close", function(){
		$(".show-content-col").removeClass("show-content-col");
	});
	
	$("#btndelete").hide();
}

//初始化合并主场景窗口
function initAutoViewWindown(){
	$('#autoSetViewWindown').jqxWindow({
        theme:theme,
        autoOpen:false,
        showCollapseButton: true, 
        maxHeight: 800, 
        maxWidth: 700, 
        height: 530, 
        width: 600,
        isModal: true,
        modalZIndex: 1000,
        resizable: false,
        initContent: function () {
        	
        }
    });
}

//场景原数据
var initeViewLocationData = null;
//展开自动设置场景窗口
function showAutoSetWindow(){
	 var _frame=$('#viewInfoFrame').contents();
	//取出选中的场景id
	 //获取选中的场景IDs
//    if(grid.getSelectIds() == ""){
//        showErrorMessage("请选择场次");
//        return;
//    }
//    
//    var message = "";
//    //判断选中的场次是否有已经完成的
//    var statusStr = grid.getSelectStatus();
//    var viewNoStatusArr = statusStr.split(",");
//    for(var i=0; i<viewNoStatusArr.length; i++){
//    	var viewNoStatusStr = viewNoStatusArr[i];
//    	var statusArr = viewNoStatusStr.split("&");
//    	var status = statusArr[0];
//    	var viewNo = statusArr[1];
//    	if (status == 2) {
//			if (message == "") {
//				message = viewNo + "已完成，不能修改";
//			}else {
//				message = message + "；" +viewNo + "已完成，不能修改";
//			}
//		}else if (status == 3) {
//			if (message == "") {
//				message = viewNo + "已删戏，不能修改";
//			}else {
//				message = message + "；" +viewNo + "已删戏，不能修改";
//			}
//		}
//    }
//    
//    if (message != '') {
//		showErrorMessage(message);
//		return;
//	}
    
    var viewRows=grid.getSelectIds();
    //清空数据
    _frame.find("#locationSceneTable").html('');
//    $('#autoSetViewWindown').jqxWindow("show");
    /*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDataDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	
	//填充数据
	$.ajax({
		url:"/viewManager/queryLocationListByVieId",
        data:{viewIds: viewRows},
        dataType:"json",
        type:"post",
        success:function(response){
        	if (response.success) {
        		var sameLocationList = response.sameLocationList;
        		initeViewLocationData = sameLocationList;
        		_LoadingHtml.hide();
				$(".opacityAll").hide();
        		//拼接数据
        		splicAutoWindowData(sameLocationList);
			}else {
				showErrorMessage(response.message);
			}
        }
	});
}


//显示隐藏列面板
function showColumnsPanel(own, event){
	var left = $(own).position().left;
	var top = $(own).position().top;
	var height = $(own).outerHeight();
	$("#columnsPanel").css({"left": left, "top": top+height+5}).toggle();
	event.stopPropagation();
}

//阻止冒泡事件
function objEvent(event){
	event.stopPropagation();
}

//显示或隐藏列
function showOrHideColumn(own){
	var index = $(own).attr("td-index");
	var text= $(own).parent("label").text();
	index = parseInt(index);
	if($(own).is(":checked")){
		$("td[cellid="+index+"]").show();
		hideTdIndex.splice(jQuery.inArray(index,hideTdIndex),1); //移除数组中的指定元素
		hideTdText.splice(jQuery.inArray(text, hideTdText),1);
	}else{
		$("td[cellid="+index+"]").hide();
		hideTdIndex.push(index);
		hideTdText.push(text);
	}
	$.ajax({
		url: '/cacheManager/saveCacheInfo',
		type: 'post',
		data: {"type": 1, content: hideTdText.join(",")},
		datatype: 'json',
		success: function(response){
			if(response.success){
				
			}else{
				showErrorMesage(response.message);
			}
		}
	});
	console.log(index);
	console.log(hideTdIndex);
}

//打印
function printViewList(){
	var random=Math.random();
    var tempForm = document.createElement("form");  
    tempForm.id="tempForm1";  
    tempForm.method="post";  
    tempForm.action='/viewManager/toViewPrintPreviewPage';  
    tempForm.target='_blank';
//    for(var key in filter){
//    	if(filter[key] != undefined || filter[key] != null){
//    		if((filter[key] != "pagesize") && filter[key] != "pagenum"){
//    			var hideInput = document.createElement("input");  
//                hideInput.type="hidden";  
//                hideInput.name= key;//季节
//                hideInput.value= filter[key];
//                tempForm.appendChild(hideInput);
//    		}
//    		
//        }
//    	
//    }     
    var hideInput = document.createElement("input");
    hideInput.type="hidden";  
    hideInput.name= "filter";
    var printFilter = {};
    for(var key in filter){
    	if((key != "pagesize") && (key != "pagenum"))
    	printFilter[key]= filter[key];
    }
    hideInput.value= JSON.stringify(printFilter);
    tempForm.appendChild(hideInput);
    var hideInput28 = document.createElement("input");  
    hideInput28.type="hidden";  
    hideInput28.name= "hideColumn";
    hideInput28.value= hideTdIndex.join(",");
    tempForm.appendChild(hideInput28);
    if(window.addEventListener) {
  	  tempForm.addEventListener("onsubmit",openWindow(random));
     } else if(window.attachEvent){
  	  tempForm.attachEvent("onsubmit",openWindow(random));
    }
    document.body.appendChild(tempForm);  
    tempForm.submit();
    document.body.removeChild(tempForm);
    
}
function openWindow(name){
	  // var newwindows =  window.open('about:blank',name);
	   //newwindows.focus();
}




//取消自动设置场景窗口
function cancelMergeView() {
	$('#autoSetViewWindown').jqxWindow("close");
}


//拼接批量删除窗口的数据
function splicAutoWindowData(locationList){
	 var _frame=$('#viewInfoFrame').contents();
	 _frame.find("#locationSceneTable").html('');
	//拼接表格
	var $locationSceneTable = _frame.find("#locationSceneTable");
	var tableArr = [];
	if (locationList != null && locationList.length != 0) {
		for(var i = 0; i<locationList.length; i++){
			var locationDataList = locationList[i];
			for(var a=0; a< locationDataList.length; a++){
				tableArr.push(" <tr>");
				var sameViewLocationDto = locationDataList[a];
				var mainLocation = '';
				//主场景
				if (a == 0) {
					mainLocation = sameViewLocationDto.mainLocation;
				}
				//次场景
				var secondLocationStr = sameViewLocationDto.secondLocation;
				//三级场景
				var thirdLocationStr = sameViewLocationDto.thirdLocation;
				
				//添加数据
				if (a == 0) {
					tableArr.push(" <td name='mainLocation' rowspan='"+ locationDataList.length +"' style='text-algin:center;'> <input type='text' class='location-content' value='"+ mainLocation +"' /></td>");
				}
				tableArr.push(" <td name='secondLocation'><input type='text' class='location-content' value='"+ secondLocationStr +"' /></td>");
				tableArr.push(" <td name='thirdLocation'><input type='text' class='location-content' value='"+ thirdLocationStr +"' /></td>");
				tableArr.push(" <input type='hidden' name='viewIds' value='"+ sameViewLocationDto.viewId +"' />");
				
				tableArr.push(" </tr>");
			}
		}
		
		$locationSceneTable.append(tableArr.join(""));
	}
}

//点击还原按钮
function backToInitData(){
	//拼接数据
	splicAutoWindowData(initeViewLocationData);
	$("#searchMainLocation").val('');
}

//批量保存场景信息
function saveBtchViewLocation(){
	var _frame=$('#viewInfoFrame').contents();
	//取出表中的每一行
	var trs = _frame.find("#locationSceneTable tr");
	//定义总的字符串
	var totalTrString = "";
	var tempMainLocation = "";
	var viewIds = "";
	for(var i=0; i<trs.length; i++){
		var tr = trs[i];
		//取出每一行的dtd
		var tds = $(tr).children();
		var totalTdString = "";
		var mainLocation = "";
		var secondLocation = "";
		var thirdLocation = "";
		var viewId = "";
		for(var a=0; a<tds.length; a++){
			if ($(tds[a]).attr("name") == "mainLocation") {
				var $text = $(tds[a]).children();
				tempMainLocation = $($text).val();
			}
			
			//取出主场景
			if ($(tds[a]).attr("name") == "mainLocation") {
				mainLocation = $(tds[a]).children().val();
			}
			
			//取出次场景
			if ($(tds[a]).attr("name") == "secondLocation") {
				secondLocation = $(tds[a]).children().val();
			}
			
			//取出三级场景
			if ($(tds[a]).attr("name") == "thirdLocation") {
				thirdLocation = $(tds[a]).children().val();
			}
			//取出场景id
			if ($(tds[a]).attr("name") == "viewIds") {
				viewId = $(tds[a]).val();
				if (viewId != "") {
					if (viewIds == '') {
						viewIds = viewId;
					}else {
						viewIds = viewIds + "," +viewId;
					}
				}
			}
			
			if (mainLocation == "") {
				mainLocation = tempMainLocation;
			}
		}
		
		//拼接一行的数据
		totalTdString = mainLocation + "-" + secondLocation + "-" +thirdLocation + "-" + viewId;
		
		//拼接总的字符串
		if (totalTrString == "") {
			totalTrString = totalTdString;
		}else {
			totalTrString = totalTrString + "&" + totalTdString;
		}
	}
	
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
	var _PageHeight = document.documentElement.clientHeight;
	//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	//在页面未加载完毕之前显示的loading Html自定义内容
	var _LoadingHtml = $("#loadingDataDiv");
	$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	//呈现loading效果
	_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	_LoadingHtml.show();
	//向后台发送请求
	$.ajax({
		url : '/viewManager/batchUpdateViewLocation',
		type : 'post',
		data : {viewStr: totalTrString},
		async : false,
		success : function(respone) {
			if (respone.success) {
				showAutoSetWindow();
				//关闭窗口
//				$('#autoSetViewWindown').jqxWindow("close");
//				showSuccessMessage(respone.message);
			} else {
				showErrorMessage(respone.message);
			}
			_LoadingHtml.hide();
			$(".opacityAll").hide();
		}
	});
	
	//重新选中行
	/*for(var a=0; a<selectedIndexsArr.length; a++){
		var index = selectedIndexsArr[a];
		var checkBox = $(":checkbox[index='"+index+"']");
		$(checkBox).prop("checked", true);
	}*/
}

//在输入框中按下回车键
function clicksearchMainLocation(e){
	if (e.keyCode == 13) {
		searchSameMainLocation();
	}
}

//收索主场景
function searchSameMainLocation(){
	 var _frame=$('#viewInfoFrame').contents();
	//取出输入的主场景字符串
	var mainLocationStr = _frame.find("#searchMainLocation").val();
	//取出表中的每一行
	var trs = _frame.find("#locationSceneTable tr");
	var tempMainLocation = "";
	var viewIds ="";
	for(var i=0; i<trs.length; i++){
		var tr = trs[i];
		//取出每一行的dtd
		var tds = $(tr).children();
		var mainLocation = "";
		var viewId = "";
		if(mainLocationStr == ""){
			backToInitData();
			return;
		}else{
			for(var a=0; a<tds.length; a++){
				if ($(tds[a]).attr("name") == "mainLocation") {
					var $text = $(tds[a]).children();
					tempMainLocation = $($text).val();
				}
				
				//取出主场景
				if ($(tds[a]).attr("name") == "mainLocation") {
					mainLocation = $(tds[a]).children().val();
				}
				
				//取出场景id
				if ($(tds[a]).attr("name") == "viewIds") {
					viewId = $(tds[a]).val();
				}
				
				if (mainLocation == "") {
					mainLocation = tempMainLocation;
				}
				
				if (mainLocation.indexOf(mainLocationStr) != -1) {
					if (viewId != '' && viewId != undefined) {
						if (viewIds == '') {
							viewIds = viewId;
						}else {
							viewIds = viewIds + "," +viewId;
						}
					}
				}
			}
		}
		
		
	}
	//如果没有收索到场景，给出提示信息，不再请求后台
	if (viewIds == '') {
		showErrorMessage("没有搜索到您填写的主场景！");
	}else {
		/*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
		var _PageHeight = document.documentElement.clientHeight;
		//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
		var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
		_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
		//在页面未加载完毕之前显示的loading Html自定义内容
		var _LoadingHtml = $("#loadingDataDiv");
		$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
		//呈现loading效果
		_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
		_LoadingHtml.show();
		//填充数据
		$.ajax({
			url:"/viewManager/queryLocationListByVieId",
			data:{viewIds: viewIds},
			dataType:"json",
			type:"post",
			success:function(response){
				if (response.success) {
					var sameLocationList = response.sameLocationList;
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					//拼接数据
					splicAutoWindowData(sameLocationList);
				}else {
					showErrorMessage(response.message);
				}
			}
		});
	}
	
}


//显示或隐藏剧本
function showOrHideScenario(own){
	var $this = $(own);
	//ishide:判断当前剧本状态是显示(1)或隐藏(0);
	var ishide = $this.attr("ishide");
	if(ishide == '0'){
		$this.removeClass("hide-scenario");
		$this.addClass("show-scenario");
		$this.val("看剧本");
		$this.attr("ishide", '1');
		var index = $("#tablebody tr.show-content-col").attr("rowid");
		if(index != undefined){
			var seriesNoAndViewNo = "";
			seriesNoAndViewNo = grid.getRowData(index).seriesNo + "-" + grid.getRowData(index).viewNo;
			$.ajax({
	            url:"/viewManager/queryViewContent",
	            data:{seriesViewNo: seriesNoAndViewNo},
	            dataType:"json",
	            type:"post",
	            success:function(data){
	                if (crewType == Constants.CrewType.movie || crewType == 3) {
	                    $("#viewSpanTitle").html("<div style='margin-left:290px;'>剧本内容</div>");
	                } else {
	                    $("#viewSpanTitle").html("<div style='margin-left:290px;'>" + seriesNoAndViewNo + "&nbsp;剧本内容</div>");
	                }
	            	
	                $("#viewTitle #title").html(data.title.replace(/\n/g, "<br>"));
	                if (data.noGetRoleNames != null && data.noGetRoleNames != "") {
	                    $('#viewTitle #noGetRoleNames').show();
	                    $('#viewTitle #noGetRoleNames').html("可能存在的角色：" + data.noGetRoleNames);
	                    
	                } else {
	                    
	                    $('#viewTitle #noGetRoleNames').hide();
	                    $('#viewTitle #noGetRoleNames').html("");
	                }
	                $("#viewContentDIV span").html(data.viewContent.replace(/\n/g, "<br>"));
	                var isOpen = $('#viewContentWindow').jqxWindow('isOpen');
	                
	                if (!isOpen) {
	              		$('#viewContentWindow').jqxWindow("open");
	                }
	                //设置剧本内容的颜色
	                window.f_scene_create.initContentColor();
	            }
	        });
		}
		
		
		
	}
	else{
		$this.removeClass("show-scenario");
		$this.addClass("hide-scenario");
		$this.attr("ishide", '0');
		$('#viewContentWindow').jqxWindow("close");
		return;
	}
}



