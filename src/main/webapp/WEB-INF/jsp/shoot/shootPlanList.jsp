<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.xiaotu.makeplays.utils.Constants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String currDate = sdf.format(new Date());
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
	
	<!-- bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
	
    <link rel="stylesheet" type="text/css" href="/css/shootPlan.css">
	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
	
	<!-- bootstrap JS -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
	
	<script>
	</script>
	<script type="text/javascript">
	var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];
    var planGridColumn, planGridDataFields;
    var rendergridrows;
    var crewName;
    var notHaveViewSources = new Array();//添加场景表格的数据源，放到外面用于高级查询
    var notHaveViewList = new Array(); //定义成数组的形式用户当用户展开多个列时，之前列的表格不会被覆盖掉
    var advancePlanId;  //用于高级查询计划下的场景信息的计划ID查询条件
    var parentPlanId; //在添加到子计划的功能中用于查询子计划的父计划ID
    var nestedGrids = new Array();
    var hadPlanViewIds = new Array();
    var groupSourceForSearch =
    [
         {text:"所有分组",value:""},
          <c:forEach items="${groupList}" var="group">
         {text:"${group.groupName}",value:"${group.groupId}"},
         </c:forEach>
     ];
     var groupSourceForAdd =
     [
         <c:forEach items="${groupList}" var="group">
         {text:"${group.groupName}",value:"${group.groupId}"},
         </c:forEach>
         {text:"新增组",value:"99"}
     ];
     
     /*弹出窗口对应的变量*/
     var planWindow;    //拍摄计划窗口
     var noticeWindow;  //通告单窗口
     var subPlanWindow; //子计划窗口
     var setShootPlanInfoWindow;    //设置场景拍摄时间、地点窗口
     
    //子计划数据源
    var s_planSource;
    
    var subPlanListDiv = new Array();
    var planViewListDiv = new Array();
    var subPlanListHead = new Array();
    var subPlanListGrid = new Array();
    
    var planViewListDiv = new Array();
    var planViewListHead = new Array();
    var planViewListGrid = new Array();
     
	$(document).ready(function () {
	//获取剧组名称
	loadCrewName();
    /***********************拍摄计划表格列渲染start*********************************/
    //计划时间列
    var planTimeCellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        var startDate = rowdata.startDate.Format('yyyy/MM/dd');
        var endDate = rowdata.endDate.Format('yyyy/MM/dd');
        var html = "<div style='overflow: hidden;line-height:30px;'>"+startDate + "-" + endDate+"</div>";
        return html;
    };
    
    planGridColumn = [
        { text: '计划名称', datafield: 'planName'},
        { text: '计划时间', cellsrenderer: planTimeCellsrenderer, width: 180},
        { text: '拍摄地点', datafield: 'shootLocations'},
        { text: '分组', datafield: 'groupName', width: 80},
        { text: '场数', datafield: 'viewNumTotal', width: 80},
        { text: '页数', datafield: 'pageCountNumTotal', cellsformat:'f2', width: 80},
        { text: '已完成', datafield: 'finishRate', cellsformat: 'p', width: 80 },
        { text: '最后修改时间', datafield:'updateTime', cellsformat: 'yyyy-MM-dd HH:mm:ss', width: 150 }
    ];
    
    planGridDataFields = [
        { name: 'planId',type: 'string' },
        { name: 'planName',type: 'string'},
        { name: 'startDate',type: 'date' },
        { name: 'endDate',type: 'date' },
        { name: 'groupId',type: 'string' },
        { name: 'groupName',type: 'string' },
        { name: 'updateTime',type: 'date' },
        { name: 'crewId',type: 'string' },
        { name: 'playType',type: 'int' },
        { name: 'parentPlan',type: 'string' },
        { name: 'sequence',type: 'string' },
        { name: 'shootLocations',type: 'string' },
        { name: 'viewNumTotal',type: 'int' },
        { name: 'pageCountNumTotal',type: 'string' },
        { name: 'finishRate',type: 'string' }
    ];
    /***********************拍摄计划表格列渲染end*********************************/
    
    
	
	/*****************************表格上每行的详细信息************************************/
	var initrowdetails = function (index, parentElement, gridElement, record) {
		var planId = record.planId;
		
		subPlanListDiv[index] = $($(parentElement).children()[0]);
        subPlanListHead[index] = subPlanListDiv[index].find("#subPlanListHead");
        subPlanListGrid[index] = subPlanListDiv[index].find("#subPlanListGird");
        planViewListDiv[index] = $($(parentElement).children()[1]);
        planViewListHead[index] = planViewListDiv[index].find("#planViewListHead");
        planViewListGrid[index] = planViewListDiv[index].find("#planViewListGrid");
        
        nestedGrids[index] = planViewListGrid[index];
        
        subPlanListGrid[index].hide();
        subPlanListHead[index].addClass('close');
        planViewListHead[index].addClass('open');
        
        loadSubPlanGrid(planId, subPlanListGrid[index], index);
		loadPlanViewGrid(planViewListGrid[index], $("#shootPlanGrid"), index, planId);
		
		//为子计划表格绑定展开列详细信息事件
		subPlanListGrid[index].on('rowexpand', function (event){
             var rows = subPlanListGrid[index].jqxGrid('getdisplayrows');
             var args = event.args;
             for (var i = 0; i < rows.length; i++) {
                  if (i != args.rowindex) {
                      subPlanListGrid[index].jqxGrid('hiderowdetails', i);
                  }
             }
             event.stopPropagation();
        });
		
		
		//表格导航点击事件
        subPlanListHead[index].unbind("click");
		subPlanListHead[index].on('click', function() {
		 if($(this).hasClass('open')){
            $(this).removeClass('open');
            $(this).addClass('close');
            subPlanListGrid[index].hide();
            subPlanListGrid[index].hide();
            
            /* if(planViewListHead.hasClass('close')){
                planViewListHead.click();
            } */
         }else{
            $(this).removeClass('close');
            $(this).addClass('open');
            subPlanListGrid[index].show();
            subPlanListGrid[index].show();
            if(planViewListHead[index].hasClass('open')){
	            planViewListHead[index].click();
	        }
         }
		});
		
		
        planViewListHead[index].unbind("click");
		planViewListHead[index].on('click', function() {
		  if($(this).hasClass('open')){
            $(this).removeClass('open');
            $(this).addClass('close');
            planViewListGrid[index].hide();
            planViewListGrid[index].hide();
            
            /* if(subPlanListHead.hasClass('close')){
                subPlanListHead.click();
            } */
         }else{
            $(this).removeClass('close');
            $(this).addClass('open');
            planViewListGrid[index].show();
            planViewListGrid[index].show();

            if(subPlanListHead[index].hasClass('open')){
                subPlanListHead[index].click();
            }
         }
		});
    };
	
	    //加载导航栏数据
	    topbarInnerText("拍摄管理&&计划");
	
	    var source =
	       {
				datatype: "json",
				root:'resultList',
				url:'<%=basePath%>/shootPlanManager/shootPlanlistJson',
	            datafields: [
					{ name: 'planId',type: 'string' },
					{ name: 'planName',type: 'string'},
					{ name: 'startDate',type: 'date' },
					{ name: 'endDate',type: 'date' },
					{ name: 'groupId',type: 'string' },
					{ name: 'groupName',type: 'string' },
					{ name: 'updateTime',type: 'date' },
					{ name: 'crewId',type: 'string' },
					{ name: 'playType',type: 'int' },
					{ name: 'parentPlan',type: 'string' },
					{ name: 'sequence',type: 'string' },
					{ name: 'shootLocations',type: 'string' },
					{ name: 'viewNumTotal',type: 'int' },
					{ name: 'pageCountNumTotal',type: 'string' },
					{ name: 'finishRate',type: 'string' }
	            ],
	            type:'post',
	            processdata: function (data) {
	                //查询之前可执行的代码
	            },
	            beforeprocessing:function(data){
	                //查询之后可执行的代码
	                source.totalrecords=data.result.total;
	            }/* ,
			    updaterow: function (rowid, rowdata, commit) {
			        
			        commit(true);
			    } */
	         };
	          
        rendergridrows = function (params) {
            //调用json返回的列表数据
            return params.data;
        };
        
        var dataAdapter = new $.jqx.dataAdapter(source);
		$("#shootPlanGrid").jqxGrid({
		    theme:theme,
			width: '100%',
			height: '95%',
			source: dataAdapter,
			//altrows: true,
			pageable: true,
			virtualmode :true,
			//autoheight: true,
			columnsresize: true,
			rowdetails: true,
			rowsheight: 30,
			//表格数据的详细信息配置
			initrowdetails: initrowdetails,
			rowdetailstemplate: {rowdetails: "<div id='subPlanListDiv' style='margin: 10px;'><div id='subPlanListHead'>子计划列表</div><div id='subPlanListGird'></div></div><div id='planViewListDiv' style='margin: 10px;'><div id='planViewListHead'>场景列表</div><div id='planViewListGrid'></div></div>", rowdetailsheight: 500, rowdetailshidden: true},
			ready: function () {
                
			},
			showtoolbar: true,
			pagesize: 100,
			pagesizeoptions: ['50', '100'],
			pagerbuttonscount: 5,
			rendergridrows: rendergridrows,
			localization:localizationobj,//表格文字设置
			rendertoolbar: function (toolbar) {
			
			     /*******************表格头的分组查询按钮***********style='position:absolute; left:120px; top:12px;display:block; '*************/
			          var container = $("<div style='margin: 5px;'></div>");
			          var html="<div id='groupDIV' style='float:right;margin-right:15px;'></div>";
			          html += "<input type='button' style='float:left; margin-left:25px;' id='addShootPlanBtn' class='add'>";
			          toolbar.append(container);
			          container.append(html);
			          
			          ///////////////////////分组查询条件/////////////////
			          $("#groupDIV").jqxDropDownList({
			              theme:theme,selectedIndex:0,
			              source: groupSourceForSearch, 
			              displayMember: "text", 
			              valueMember: "value", 
			              width: 100, 
			              height: 25,
			              placeHolder: "",
			              dropDownHeight: getHeight(groupSourceForSearch)
			          });
			          
			          $("#groupDIV").on('change',function(event) {
			              var args = event.args;
			              var value = $("#groupDIV").val();
			              source.data={groupId:value};
			              $("#shootPlanGrid").jqxGrid('updatebounddata', 'cells');
			          });
			          
			          
			          ////////////////////////新建计划///////////////////////
			          $("#addShootPlanBtn").jqxTooltip({content: '新建拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
			          
			          
                      $("#addShootPlanBtn").unbind("click");
			          $("#addShootPlanBtn").on('click', function() {
			                showPlanWindow($("#shootPlanGrid"));
			                $("#pplanWindow").jqxWindow("setTitle", "新建拍摄计划");
			                
			                $("#pplanNameInput").val("");
							$("#pplanStartTime").val(new Date().Format("yyyy-MM-dd"));
							$("#pplanEndTime").val(new Date().Format("yyyy-MM-dd"));
							$("#pplanGroup").jqxDropDownList('selectIndex', 0);
							$("#pgroupIdValue").val("");
							$("#p_pplanId").val("");
							$("#pplanId").val("");
			          });
			},
			columns: [
			  { text: '计划名称', datafield: 'planName'},
			  { text: '计划时间', cellsrenderer: planTimeCellsrenderer, width: 180},
			  { text: '拍摄地点', datafield: 'shootLocations'},
			  { text: '分组', datafield: 'groupName', width: 80},
			  { text: '场数', datafield: 'viewNumTotal', width: 80},
			  { text: '页数', datafield: 'pageCountNumTotal', cellsformat:'f2', width: 80},
			  { text: '已完成', datafield: 'finishRate', cellsformat: 'p', width: 80 },
			  { text: '最后修改时间', datafield:'updateTime', cellsformat: 'yyyy-MM-dd HH:mm:ss', width: 150 }
			]
		});
		$("#shootPlanGrid").bind("pagechanged", function (event) {
		    //翻页时的事件绑定
		});
		
		var rowBoundIndex;
		$('#shootPlanGrid').on('rowexpand', function (event){
		     var rows = $('#shootPlanGrid').jqxGrid('getdisplayrows');
		     var args = event.args;
		     rowBoundIndex = args.rowindex;
		     for (var i = 0; i < rows.length; i++) {
			      if (i != args.rowindex) {
			          $('#shootPlanGrid').jqxGrid('hiderowdetails', i);
			      } else {
			          $('#shootPlanGrid').jqxGrid('selectrow', i);
			      }
		     }
		});
		
		/* $('#shootPlanGrid').on('cellclick', function (event) {
            var args = event.args;
            var boundIndex = args.rowindex;
            var columnindex = args.columnindex;
            if(columnindex == 0){
                return;
            }else{
                if(rowBoundIndex == boundIndex){
                    rowBoundIndex = 9999999;
                    $('#shootPlanGrid').jqxGrid('hiderowdetails', boundIndex);
                }else{
                    $('#shootPlanGrid').jqxGrid('showrowdetails', boundIndex);
                    $('#shootPlanGrid').jqxGrid('ensurerowvisible', rowBoundIndex+1);
                }
            }
        }); */
		/********************拍摄计划表格初始化end**************************/
		
		/***********************加载统计数据start*******************************/
		var viewSum = "${viewSum }";
        var pageSum = "${pageSum }";
        var inPlanViewSum = "${inPlanViewSum }";
        var inPlanPageSum = "${inPlanPageSum }";
		var text = "";
        text += "统计：未添加的戏量：" + (viewSum - inPlanViewSum) + "场/" + Math.round((pageSum - inPlanPageSum) * 100)/100 + "页 | ";
        text += "已添加的戏量：" + inPlanViewSum + "场/" + Math.round(inPlanPageSum * 100)/100 + "页, ";
        
        if ('${groupPlanViewList }' != null && '${groupPlanViewList }' != '' && '${fn:length(groupPlanViewList) }' > 0) {
          text += "其中--";
        }
        
        text += "<c:forEach items='${groupPlanViewList }' var='groupMap'>";
        text += "<c:forEach items='${groupMap }' var='group'>";
        
        text += "<c:if test='${group.key == "groupName" }'>";
        text += " ${group.value }：";
        text += "</c:if>";
        
        text += "<c:if test='${group.key == "viewCount" }'>";
        text += "${group.value }场/";
        text += "</c:if>";
        
        text += "<c:if test='${group.key == "pageCount" }'>";
        text += Math.round('${group.value }' * 100)/100;
        text += " 页；";
        text += "</c:if>";
        
        text += "</c:forEach>";
        text += "</c:forEach>";
        
        $("#statistics").text(text);
        /***********************加载统计数据end*******************************/
		
		
		/*********************添加场景高级查询窗口初始化start******************************/
		$('#searchWindow').jqxWindow({
	        theme:theme,  
	        width: 800,
	        height: 650, 
	        maxHeight: 800,
	        modalZIndex: 30000,
	        autoOpen: false,
	        cancelButton: $('#closeSearchSubmit'),
	        isModal: true,
	        resizable: false,
	        initContent: function () {
                //加载高级查询条件
                loadSearchCondition();
	        
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
		        $("#viewRemark, #mainContent").jqxInput({
		            theme: theme
		        });
		    
		        
                $("#searchSubmit").unbind("click");
		        $("#searchSubmit").on("click",function(){
	                var filter={};
	                var season =$("#seasonSelect").val();
	                var atmosphere =$("#atmosphereSelect").val();
	                var site =$("#siteSelect").val();
	                var major =$("#firstLocationSelect").val();
	                var minor =$("#secondLocationSelect").val();
	                //var thirdLevel =$("#thirdLevelViewListDIV").jqxDropDownList("getSelectedItem");
	                var clothes =$("#clothSelect").val();
	                var makeup =$("#makeupSelect").val();
	                var roles = $("#majorRoleSelect").val(); 
	                var props = $("#propSelect").val(); 
                    var specialProps = $("#specialPropSelect").val();
	                var guestRole = $("#guestRoleSelect").val(); 
	                var massRole = $("#massRoleSelect").val(); 
	                var shootStatus = $("#shootStatusSelect").val();
	                var advert = $("#advertInfoSelect").val();
	                var viewType=$("#cultureTypeSelect").val();
	                var shootLocation =$("#shootLocationSelect").val();
	                var mainContent = $("#mainContent").val();
	                var remark = $("#viewRemark").val();
                    var seriesViewNos = $("#seriesViewNos").val();
	                
	                if(season!= null && season!=""){
	                    var seasonStr = "";
	                    
	                    for(var i=0;i<season.length;i++){
	                        seasonStr+=season[i]+",";
	                    }
	                    seasonStr=seasonStr.substring(0,seasonStr.length-1);
	                    filter.season=seasonStr;
	                }else{
	                    filter.season="";
	                }
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
	                if(viewType!= null && viewType!=""){
	                    var viewTypeStr = "";
	                    
	                    for(var i=0;i<viewType.length;i++){
	                        viewTypeStr+=viewType[i]+",";
	                    }
	                    viewTypeStr=viewTypeStr.substring(0,viewTypeStr.length-1);
	                    filter.viewType=viewTypeStr;
	                }else{
	                    filter.viewType="";
	                }
	                
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
	                if(specialProps!= null && specialProps!=""){
                        var prop = "";
                        
                        for(var i=0;i<specialProps.length;i++){
                            prop+=specialProps[i]+",";
                        }
                        prop=prop.substring(0,prop.length-1);
                        filter.specialProps=prop;
                    }else{
                        filter.specialProps="";
                    }
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
	                
	                if($("#startSeriesNo").val()!=""){
	                    filter.startSeriesNo=$("#startSeriesNo").val();
	                }else{
	                    filter.startSeriesNo="";
	                }
	                if($("#startViewNo").val()!=""){
	                    filter.startViewNo=$("#startViewNo").val();
	                }else{
	                    filter.startViewNo="";
	                }
	                if($("#endSeriesNo").val()!=""){
	                    filter.endSeriesNo=$("#endSeriesNo").val();
	                }else{
	                    filter.endSeriesNo="";
	                }
	                if($("#endViewNo").val()!=""){
	                    filter.endViewNo=$("#endViewNo").val();
	                }else{
	                    filter.endViewNo="";
	                }
	                
	                if($("#startViewNo").val()!=""&&$("#startSeriesNo").val()==""){
	                    showErrorMessage("请填写集数");
	                    return;
	                }
	                if($("#endViewNo").val()!=""&&$("#endSeriesNo").val()==""){
	                    showErrorMessage("请填写集数");
	                    return;
	                }
	                
	                filter.seriesNo="";
	                filter.viewNo="";
	                //$("#seriesNo").val("");
	                //$("#viewNo").val("");
	                
	                /* if(/.*[\u4e00-\u9fa5]+.*$/.test(seriesViewNos)){
	                    showErrorMessage("集场编号中不能含有汉字");
	                    return;
	                } */
	                
	                var seriesViewNoArr = seriesViewNos.split(/，|；|,|;/);
	                for (var i = 0; i < seriesViewNoArr.length; i++) {
	                    if (seriesViewNoArr[i] != null && seriesViewNoArr[i] != "" && !/^\d+( )*(-|－|——)( )*.+/.test(seriesViewNoArr[i])) {
	                        showErrorMessage(seriesViewNoArr[i] + "场集场编号不符合规范，请重新输入");
	                        return;
	                    }
	                    
                    
	                    var singleSeriesViewNoArr = seriesViewNoArr[i].split(/-|－|——/);
	                    var seriesNo = singleSeriesViewNoArr[0];
	                    if (isNaN(seriesNo)) {
	                        showErrorMessage("《" + seriesViewNoArr[i] + "》场集号只能输入数字，请重新输入");
	                        return;
	                    }
	                }
	                
	                if (seriesViewNos != null && seriesViewNos != "") {
	                    filter.seriesViewNos = seriesViewNos;
	                } else {
	                    filter.seriesViewNos = "";
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
	                
	                /* var sortType = $("input[name='sortType']:checked").val();
                    var sortFlag = $("input[name='sortFlag']:checked").val();
                    filter.sortType=sortType;
                    filter.sortFlag=sortFlag; */
                    
                    filter.searchMode=$("input[name='searchMode']:checked").val();
                    filter.pageFlag=true;
                    filter.fromAdvance = true;
                    
                    filter.planId = advancePlanId;
                    filter.inPlan = false;
                    
                    
                    var viewIndex=$("#refreshPlanIndex").val();
                    //alert(viewIndex);
                    notHaveViewSources[viewIndex].data = filter;
                    $('#searchWindow').jqxWindow('close');
                    notHaveViewList[viewIndex].jqxGrid('gotopage', 0);
                    notHaveViewList[viewIndex].jqxGrid('updatebounddata');
                    
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
	            });
		            
		            
	            $('.selectpicker').selectpicker({
	                size: 10
	            });
	            
	            //jqxFormattedInput({ width: 250, height: 25, radix: "decimal", decimalNotation: "exponential", value: "330000" });
	            $("#startSeriesNo").jqxFormattedInput({theme:theme, placeHolder: "集", radix: "decimal", height: 20, width: 30, min: 1, value: '' });
	            $("#startViewNo").jqxInput({theme:theme,placeHolder: "场", height: 20, width: 30, minLength: 1 });
	            $("#endSeriesNo").jqxFormattedInput({theme:theme,placeHolder: "集", radix: "decimal", height: 20, width: 30, min: 1, value: '' });
	            $("#endViewNo").jqxInput({theme:theme,placeHolder: "场", height: 20, width: 30, minLength: 1 });
	            $("#startSeriesNo, #endSeriesNo").on("keyup", function() {
                if (isNaN($(this).val())) {
                    $(this).val("");
                }
            });
	            
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
	            
	            
	            //当选择主要演员显现"出现即可"和"不出现"单选按钮，只有当选择两个及两个以上才显现"同时出现"和"不同时出现"单选按钮
	            $("#majorRoleSelect").on('change', function (event){
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
	            });
            
	            $('.searchUl').on('mouseover', function(event) {
	                if ($(this).find("li").find("select").val() != null && $(this).find("li").find("select").val() != undefined) {
	                    $(this).find("li").find(".clearSelection").show();
	                }
	            });
	            
	            $('.searchUl').on('mouseout', function(event) {
	                $(this).find("li").find(".clearSelection").hide();
	            });
	            
	            
                $(".clearSelection").unbind("click");
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
	            
	            
                $("#clearSearchButton").unbind("click");
	            $("#clearSearchButton").on("click",function(){
	                //集场重置
	                $("#startSeriesNo").val("");
	                $("#startViewNo").val("");
	                $("#endSeriesNo").val("");
	                $("#endViewNo").val("");
                    $("#seriesViewNos").val("");
	                
	                //隐藏单选按钮
	                $("#anyOneAppear").hide();
	                $("#noOneAppear").hide();
	                $("#everyOneAppear").hide();
	                $("#notEvenyOneAppear").hide();
	                
	                //设置单选按钮默认选中状态
	                $("input[name='searchMode'][value='1']").prop("checked",true);
	                /* $("input[name='sortType'][value='1']").prop("checked",true);
	                $("input[name='sortFlag'][value='1']").prop("checked",true); */
	                
	                //所有下拉值全部反选
	                $('.selectpicker').selectpicker('deselectAll');
	                
	                //$("#selectpickerPreValue").val("");
	                $(".preValue").val("");
	                
	                $("#mainContent").val("");
                    $("#viewRemark").val("");
	            });
	            
	            $('#searchWindow').on('close', function (event) {
                    var viewIndex=$("#refreshPlanIndex").val();
                });
		     }
	    });
		/*********************添加场景高级查询窗口初始化end******************************/
		
		
		
        /*******************场景内容窗口start*********************/
        $('#viewContentWindow').jqxWindow({theme:theme,autoOpen:false,zIndex:20000,
            showCollapseButton: true, maxHeight: 400, maxWidth: 700, minHeight: 200, minWidth: 200, height: 300, width: 500,
            initContent: function () {
                
            }
        });
        /*******************场景内容窗口end*********************/
        
        $("#bottomRight").css("visibility", 'hidden');
	});
	
	//跳转到更新拍摄日志信息页面
	function updateShootLog (shootLogId) {
	   window.location.href = "<%=basePath%>/shootLogManager/shootLogDetail?shootLogId=" + shootLogId;
	};
	
	function exportPlan(planId) {
	  //window.location.href = "/shootPlanManager/exportPlanInfo?planId=" + planId;
	  window.location.href = "/shootPlanManager/exportPlanInfoByTemplate?planId=" + planId;
	  
	};
	
	function modifyPlan(planGrid, planId) {
		$.ajax({
            url:"/shootPlanManager/queryOnePlanJson",
            data:{planId: planId},
            dataType:"json",
            type:"post",
            async:false,
            success:function(param){
                if(param.success) {
                    var planInfo = param.shootPlanInfo;
                    showPlanWindow(planGrid);
                    $("#pplanWindow").jqxWindow("setTitle", "修改拍摄计划");
                    
                    $("#pplanId").val(planInfo.planId);
	                $("#pplanNameInput").val(planInfo.planName);
	                $("#pplanStartTime").val(new Date(planInfo.startDate).Format("yyyy-MM-dd"));
	                $("#pplanEndTime").val(new Date(planInfo.endDate).Format("yyyy-MM-dd"));
	                $("#pplanGroup").jqxDropDownList('selectItem', valueMember=planInfo.groupId);
	                $("#pgroupIdValue").val("");
                } else {
                    showErrorMessage(param.message);
                }
            }
		});
	};
	
	//删除拍摄计划 
	function deletePlan(planGrid, planId) {
	    popupPromptBox("提示", "是否删除该拍摄计划？", function() {
	       $.ajax({
                url:"/shootPlanManager/delteShootPlan",
                data:{planId: planId},
                dataType:"json",
                type:"post",
                async:false,
                success:function(param){
                    if(param.success){
                        showSuccessMessage(param.message);
                        planGrid.jqxGrid('updatebounddata');
                        var rowindex = planGrid.jqxGrid('getselectedrowindex');
                        if(rowindex != -1) {
                            planGrid.jqxGrid('unselectrow', rowindex);
                        }
                    } else {
                        showErrorMessage(param.message);
                    }
                }
            });
	    });
	};
	
    function getIndex(grid, index){
        var datainformation = grid.jqxGrid('getdatainformation');
        var paginginformation = datainformation.paginginformation;
        var pagenum = paginginformation.pagenum;
        var pagesize = paginginformation.pagesize;
        var pagescount = paginginformation.pagescount;
        
        if ((index + 1) >= (pagenum*pagesize)) {
           index=index-pagenum*pagesize;
        }
        return index;
    }
    function getColor(shootStatus){
        
        if(shootStatus==""){
            return null;
        }
        var divColor=viewStatusColor.get(shootStatus);
        return divColor;
    }
    function showViewContent(viewId,title){
        $.ajax({
            url:"/notice/getViewContent",
            data:{viewId:viewId},
            dataType:"json",
            type:"post",
            success:function(data){
                $("#viewTitle").text(title);
                $("#viewContent").text("");
                $("#viewContent").append(data.content.title+"<br>");
                $("#viewContent").append(data.content.content);
                
                $('#viewContentWindow').jqxWindow("open");
            }
        });
        
    }
    
     /**
      * 加载子计划列表表格,planId:父计划ID，subPlanListGrid:装载表格的DIV控件
      */
    function loadSubPlanGrid(parentPlanId, subPlanListGrid, parentIndex) {
        var subPlanRowDetails = function (index, parentElement, gridElement, record) {
	        var _thisIndex = parentIndex + "" + index;
	        var planId = record.planId;
	        
	        var subPlanViewGrid = $($(parentElement).children()[0]);
	        
	        nestedGrids[_thisIndex] = subPlanViewGrid;
	        
	        loadPlanViewGrid(subPlanViewGrid, subPlanListGrid, _thisIndex, planId, parentPlanId);
	    };
	    
	    var sp_planTimeCellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
	        var startDate = rowdata.startDate.Format('yyyy/MM/dd');
	        var endDate = rowdata.endDate.Format('yyyy/MM/dd');
	        var html = "<div style='overflow: hidden; line-height:25px;'>"+startDate + "-" + endDate+"</div>";
	        return html;
	    };
	    var sp_planGridColumn = [
	        { text: '计划名称', datafield: 'planName'},
	        { text: '计划时间', cellsrenderer: sp_planTimeCellsrenderer, width: 180},
	        { text: '拍摄地点', datafield: 'shootLocations'},
	        { text: '分组', datafield: 'groupName', width: 80},
	        { text: '场数', datafield: 'viewNumTotal', width: 80},
	        { text: '页数', datafield: 'pageCountNumTotal', cellsformat:'f2', width: 80},
	        { text: '已完成', datafield: 'finishRate', cellsformat: 'p', width: 80 },
	        { text: '最后修改时间', datafield:'updateTime', cellsformat: 'yyyy-MM-dd HH:mm:ss', width: 150 }
	    ];
    
        var source = {
	        datatype: "json",
	        root:'resultList',
	        url:'<%=basePath%>/shootPlanManager/shootPlanlistJson',
	        data: {parentPlanId: parentPlanId},
	        datafields: planGridDataFields,
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
        subPlanListGrid.jqxGrid({
            theme:theme,
            width: '95%',
            source: dataAdapter,
            //pageable: true,
            autoheight: false,
            columnsresize: true,
            localization:localizationobj,//表格文字设置
            columns: sp_planGridColumn,
            showtoolbar: true,
            rendertoolbar: '',
            rowdetails: true,
            //表格数据的详细信息配置
            initrowdetails: subPlanRowDetails,
            rowdetailstemplate: {
                rowdetails: "<div id='subPlanViewGrid"+ parentIndex +"' style='margin: 10px;'></div>", 
                rowdetailsheight: 500, 
                rowdetailshidden: true 
            },
            rendertoolbar: function (toolbar) {
                 /*******************表格头的分组查询按钮***********style='position:absolute; left:120px; top:12px;display:block; '*************/
                 var container = $("<div style='margin: 5px;'></div>");
                 var html="<div id='subPlanGroupDIV"+ parentIndex +"' class='' style='float:right;margin-right:15px;'></div>";
                 html += "<input type='button' style='float:left;' class='refresh' id='refresh"+ parentIndex +"'>";
                 html += "<input type='button' style='float:left; margin-left:5px;' id='addSubPlanBtn"+ parentIndex +"' class='add-sub-plan'>";
                 
                 toolbar.append(container);
                 container.append(html);
                 
                 $("#addSubPlanBtn"+parentIndex).jqxTooltip({content: '新建拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                 $("#refresh"+parentIndex).jqxTooltip({content: '刷新', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                 
                 ///////////////////////分组查询条件/////////////////
                 $("#subPlanGroupDIV" + parentIndex).jqxDropDownList({
                     theme:theme,selectedIndex:0,
                     source: groupSourceForSearch, 
                     displayMember: "text", 
                     valueMember: "value", 
                     width: 100, 
                     height: 25,
                     placeHolder: "",
                     autoDropDownHeight: true
                 });
                 
                 $("#subPlanGroupDIV" + parentIndex).on('change',function(event) {
                     var args = event.args;
                     var value = $("#subPlanGroupDIV" + parentIndex).val();
                     source.data={groupId:value, parentPlanId: parentPlanId};
                     subPlanListGrid.jqxGrid('updatebounddata');
                 });
                 
                 //刷新表格
                 
                 $("#refresh").unbind("click");
                 $("#refresh" + parentIndex).on('click', function() {
                   subPlanListGrid.jqxGrid('updatebounddata');
                 });
                 
                 ////////////////////////新建计划///////////////////////
                 $("#addSubPlanBtn" + parentIndex).unbind("click");
                 $("#addSubPlanBtn" + parentIndex).on('click', function() {
                    $("#pplanNameInput").val("");
                    
                    $("#pplanGroup").jqxDropDownList('selectIndex', 0);
                    $("#pgroupIdValue").val("");
                    $("#p_pplanId").val(parentPlanId);
                    $("#pplanId").val("");
                    
                    /* $("#s_minPlanTime").val();
                    $("#s_maxPlanTime").val(); */
                    
                    $.ajax({
			            url:"/shootPlanManager/queryOnePlanJson",
			            data:{planId: parentPlanId},
			            dataType:"json",
			            type:"post",
			            async:false,
			            success:function(param){
			                if(param.success) {
			                    var startDate = param.staretDate;
			                    var endDate = param.endDate;
			                    
			                    $("#s_minPlanTime").val(startDate);
                                $("#s_maxPlanTime").val(endDate);
                                
                                $("#pplanStartTime").val(startDate);
                                $("#pplanEndTime").val(startDate);
			                }
			            }
                    })
                    
                    
                    
                    showPlanWindow(subPlanListGrid);
                    $("#pplanWindow").jqxWindow("setTitle", "新建子计划");
                 });
            }
        });
        
        var rowBoundIndex;
        subPlanListGrid.on('rowexpand', function (event){
             var rows = subPlanListGrid.jqxGrid('getdisplayrows');
             var args = event.args;
             rowBoundIndex = args.rowindex;
             for (var i = 0; i < rows.length; i++) {
                  if (i != args.rowindex) {
                      subPlanListGrid.jqxGrid('hiderowdetails', i);
                  } else {
                      subPlanListGrid.jqxGrid('selectrow', i);
                  }
             }
        });
        
        /* subPlanListGrid.on('cellclick', function (event) {
            var args = event.args;
            var boundIndex = args.rowindex;
            var columnindex = args.columnindex;
            if(columnindex == 0){
                return;
            }else{
                if(rowBoundIndex == boundIndex){
                    rowBoundIndex = 9999999;
                    subPlanListGrid.jqxGrid('hiderowdetails', boundIndex);
                }else{
                    subPlanListGrid.jqxGrid('showrowdetails', boundIndex);
                    subPlanListGrid.jqxGrid('ensurerowvisible', rowBoundIndex);
                }
            }
        }); */
    }
    
    //加载计划下场景信息表格,grid:装载表格的容器,index:表格序号，planId:所属计划ID
    function loadPlanViewGrid(grid, planListGrid, index, planId, ownParentPlanId) {
        var hadViewResultData = new Array();
        var notHaveViewIds = new Array();
        var otherPlanIds = new Array();
        var hadViewSources = { 
            datafields: [
                { name: 'viewId',type: 'string' },//场景ID
                { name: 'seriesNo',type: 'int' },//集次
                { name: 'viewNo',type: 'string' },//场次
                { name: 'atmosphereId',type: 'string' },    //气氛ID
                { name: 'atmosphereName',type: 'string' },    //气氛名称
                { name: 'site',type: 'string' },//内外景
                { name: 'viewType',type: 'int' },//文戏武戏
                { name: 'shootLocation',type: 'string' },//拍摄地点
                { name: 'firstLocation',type: 'string' },//主场景
                { name: 'secondLocation',type: 'string' },//次场景
                { name: 'thirdLocation',type: 'string' },//三级场景
                { name: 'mainContent',type: 'string' },//主要内容
                { name: 'pageCount',type: 'string' },//页数
                { name: 'majorRole',type: 'string' },//主要演员
                { name: 'guestRole',type: 'string' },//特约演员
                { name: 'massRole',type: 'string' },//群众演员
                { name: 'clothesName',type: 'string' },//服装
                { name: 'makeupName',type: 'string' },//化妆
                { name: 'propsName',type: 'string' },//道具 
                { name: 'specialPropsName',type: 'string' },//特殊道具 
                { name: 'advertsInfo',type: 'string' },//广告
                { name: 'shootDate',type: 'string' },//拍摄时间
                { name: 'remark',type: 'string' },//备注
                { name: 'shootStatus',type: 'string' }//拍摄状态
            ],
            datatype: "json",
            type:'post',
            data:{planId: planId, inPlan: true},
            beforeprocessing:function(data){
                //查询之后可执行的代码
                hadViewSources.totalrecords = data.result.total;
                hadViewResultData[index] = data.result;
            },
            root:'resultList',
            processdata: function (data) {
                //查询之前可执行的代码
                
            },
            url:'<%=request.getContextPath()%>/shootPlanManager/planViewListJson'
         };
         var serialViewNoColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var viewTitle = rowdata.seriesNo + "-" + rowdata.viewNo;
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus)+";cursor:pointer;' onclick='showViewContent(\""+rowdata.viewId+"\",\"" + viewTitle +"\")'>" + viewTitle+"</div>";
         };
         var serialViewNoColumnWithoutClick = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var viewTitle = rowdata.seriesNo + "-" + rowdata.viewNo;
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus)+";'>" + viewTitle+"</div>";
         };
         var shootDateColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var shootDateStr = "";
            if (rowdata.shootDate != null) {
                shootDateStr = rowdata.shootDate;
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + shootDateStr +"</div>";
         };
         var shootLocationColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var shootLocationStr = "";
            if (rowdata.shootLocation != null) {
                shootLocationStr = rowdata.shootLocation;
            }
            return "<div class='rowStatusColor' title='" + shootLocationStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + shootLocationStr +"</div>";
         };
         var atmosphereNameColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var atmosphereNameStr = rowdata.atmosphereName;
            if(atmosphereNameStr==null){
                atmosphereNameStr="";
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + atmosphereNameStr +"</div>";
         };
         var siteColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var siteStr = "";
            if (rowdata.site != null) {
                siteStr = rowdata.site;
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + siteStr +"</div>";
         };
         var firstLocationColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var firstLocationStr = "";
            if (rowdata.firstLocation != null) {
                firstLocationStr = rowdata.firstLocation;
            }
            return "<div class='rowStatusColor' title='" + firstLocationStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + firstLocationStr +"</div>";
         };
         var secondLocationColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var secondLocationStr = "";
            if (rowdata.secondLocation != null) {
                secondLocationStr = rowdata.secondLocation;
            }
            return "<div class='rowStatusColor' title='" + secondLocationStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + secondLocationStr +"</div>";
         };
         var thirdLocationColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var thirdLocationStr = "";
            if (rowdata.secondLocation != null) {
                thirdLocationStr = rowdata.thirdLocation;
            }
         
            return "<div class='rowStatusColor' title='" + thirdLocationStr + "'  style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + thirdLocationStr +"</div>";
         };
         var mainContentColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var mainContentStr = "";
            if (rowdata.mainContent != null) {
                mainContentStr = rowdata.mainContent;
            }
            return "<div class='rowStatusColor' title='" + mainContentStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + mainContentStr +"</div>";
         };
         var pageCountColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var pageCountStr = "";
            if (rowdata.pageCount != null) {
                pageCountStr = rowdata.pageCount;
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + pageCountStr +"</div>";
         };
         var majorRoleColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var majorRoleStr = "";
            if (rowdata.majorRole != null) {
                majorRoleStr = rowdata.majorRole;
            }
            return "<div class='rowStatusColor' title='" + majorRoleStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + majorRoleStr +"</div>";
         };
         var guestRoleColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var guestRoleStr = "";
            if (rowdata.guestRole != null) {
                guestRoleStr = rowdata.guestRole;
            }
            return "<div class='rowStatusColor' title='" + guestRoleStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + guestRoleStr +"</div>";
         };
         var massRoleColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var massRoleStr = "";
            if (rowdata.massRole != null) {
                massRoleStr = rowdata.massRole;
            }
            return "<div class='rowStatusColor' title='" + massRoleStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + massRoleStr +"</div>";
         };
         var clothesNameColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var clothesNameStr = "";
            if (rowdata.clothesName != null) {
                clothesNameStr = rowdata.clothesName;
            }
            return "<div class='rowStatusColor' title='" + clothesNameStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + clothesNameStr +"</div>";
         };
         var makeupNameColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var makeupNameStr = "";
            if (rowdata.makeupName != null) {
                makeupNameStr = rowdata.makeupName;
            }
            return "<div class='rowStatusColor' title='" + makeupNameStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + makeupNameStr +"</div>";
         };
         var propsNameColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var propsNameStr = "";
            if (rowdata.propsName != null) {
                propsNameStr = rowdata.propsName;
            }
            return "<div class='rowStatusColor' title='" + propsNameStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + propsNameStr +"</div>";
         };
         var specialPropsNameColumn = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var specialPropsNameStr = "";
            if (rowdata.specialPropsName != null) {
                specialPropsNameStr = rowdata.specialPropsName;
            }
            return "<div class='rowStatusColor' title='" + specialPropsNameStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + specialPropsNameStr +"</div>"; 
         }
         var advertNameColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var advertNameStr = "";
            if (rowdata.advertsInfo != null) {
                advertNameStr = rowdata.advertsInfo;
            }
            return "<div class='rowStatusColor' title='" + advertNameStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + advertNameStr +"</div>";
         };
         var remarkColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var remarkStr = "";
            if (rowdata.remark != null) {
                remarkStr = rowdata.remark;
            }
            return "<div class='rowStatusColor' title='" + remarkStr + "' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + remarkStr +"</div>";
         };
         var viewTypeColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var typeText = typeMap.get(rowdata.viewType);
            if(typeText==null){
                typeText = "";
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + typeText+"</div>";
         };
         var shootStatusColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var shootStatusText = shootStatusMap.get(rowdata.shootStatus);
            if(shootStatusText==null){
                shootStatusText="";
            }
            return "<div class='rowStatusColor' style='background-color:"+ getColor(rowdata.shootStatus) +";'>" + shootStatusText+"</div>";
         };
         
         var hadViewGridAdapter = new $.jqx.dataAdapter(hadViewSources);
         if (grid != null) {
             grid.jqxGrid({
                 theme:theme,
                 selectionmode: 'checkbox',
                 altrows: true,
                 source: hadViewGridAdapter, 
                 width: '95%', 
                 height: 400,
                 //pageable: true,
                 //pagesize: pageSize,
                 //pagesizeoptions: [ '20', '100','全部'],
                 //virtualmode :true,
                 showtoolbar: true,
                 localization:localizationobj,//表格文字设置
                 //columnsresize: true,
                 rendertoolbar: function (toolbar) {
                    
    				var container = $("<div style='margin: 5px;'></div>");
        			//var selected="<span class='badge' id='_already_selected_"+index+"' style='vertical-align: bottom'>0</span>";
                	 
                    var html = "<div class='planViewToolbar'  id='addPlanView"+index+"'>"//添加场景
                        + "<div id='viewList"+index+"'></div>"       
                        + "</div>"
                        + "<input type='button' class='planViewToolbar addToNotice'  id='addToNotice"+index+"'/>"   //添加到通告单
                        
                        + "<input type='button' class='planViewToolbar setShootInfo'  id='setShootInfo"+index+"'/>"
                        + "<input type='button' class='planViewToolbar removeView' id='deletePlanView"+index+"'/>"
                        + "<input type='button' class='planViewToolbar addToSubPlan'  id='addToSubPlan"+index+"'/>"
                        + "<input type='button' class='planViewToolbar export' onclick='exportPlan(\""+planId+"\")' id='exportPlanView"+index+"'/>"
                        + "<input type='button' class='planViewToolbar modify' id='modifyPlan"+index+"'/>"
                        + "<input type='button' class='planViewToolbar delete' id='deletePlan"+index+"'/>"
                        + "<input type='button' style='float:right'  value='' id='viewColorExample' class='colorExample3'>";
                        
                    toolbar.append(container);
                    //container.append(selected);
                    container.append(html);
                    
                    
                    $("#addToNotice" + index).jqxTooltip({ content: '添加到通告单', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#setShootInfo" + index).jqxTooltip({ content: '设置拍摄时间/地点', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#deletePlanView" + index).jqxTooltip({ content: '移除场景', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#addToSubPlan" + index).jqxTooltip({ content: '添加到子计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#exportPlanView" + index).jqxTooltip({ content: '导出', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#modifyPlan" + index).jqxTooltip({ content: '修改拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    $("#deletePlan" + index).jqxTooltip({ content: '删除拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                    
                    
                    $("#modifyPlan" + index).unbind("click");
                    $("#modifyPlan" + index).on("click", function() {
                        modifyPlan(planListGrid, planId);
                    });
                    
                    $("#deletePlan" + index).unbind("click");
                    $("#deletePlan" + index).on("click", function() {
                        deletePlan(planListGrid, planId);
                    });
                    
                    //绑定表格选中和取消选中事件，用于获取选中的计划中场景的ID编号，如此做可以保证在选中不同页的数据时获取准确数据
                    grid.on('rowselect', function(event) {
                        if (hadPlanViewIds[index] == undefined || hadPlanViewIds[index] == null || hadPlanViewIds[index] == 'undefined') {
                            hadPlanViewIds[index] = "";
                        }
                        var rowBoundIndex = JSON.stringify(args.rowindex);
                        if (rowBoundIndex.indexOf("[") != -1) {
                            rowBoundIndex = rowBoundIndex.substring(1, rowBoundIndex.length-1);
                        }
                        if (rowBoundIndex.indexOf(",") != -1) {
                            var rowIndexArr = rowBoundIndex.split(",");
                            for (var i = 0; i < rowIndexArr.length; i++) {
                                 hadPlanViewIds[index] += hadViewResultData[index].resultList[getIndex(grid,rowIndexArr[i])].viewId+",";
                            }
                        } else {
                            if (rowBoundIndex == "") {
                                rowBoundIndex = -1;
                            }
                            if (getIndex(grid,rowBoundIndex) != -1) {
                                hadPlanViewIds[index] += hadViewResultData[index].resultList[getIndex(grid,rowBoundIndex)].viewId+",";
                            } else {
                                hadPlanViewIds[index] = "";
                            }
                            //hadPlanViewIds[index] += hadViewResultData[index].resultList[getIndex(grid,rowBoundIndex)].viewId+",";
                        }
                    });
                    grid.on('rowunselect', function() {
                        if (hadPlanViewIds[index] == undefined || hadPlanViewIds[index] == null) {
                            hadPlanViewIds[index] = "";
                            return;
                        }
                        
                        var rowBoundIndex = args.rowindex;
                        hadPlanViewIds[index] = hadPlanViewIds[index].replace(hadViewResultData[index].resultList[getIndex(grid,rowBoundIndex)].viewId+",", "");
                    });
                    
                   ////////////////////////////////添加场景////////////////////////////////////
                   $("#addPlanView" + index).jqxDropDownButton({theme:theme,height: 20, width: 120});
                   $("#addPlanView" + index).jqxDropDownButton('setContent', '添加场景');
                   
                   $("#addPlanView" + index).on('open', function(event) {
                        //notHaveViewSources[index].data = {planId: planId, inPlan: false};
                        //$("#viewList" + index).jqxGrid('updatebounddata');
                        
                        $("#addPlanView"+index).jqxDropDownButton("setCloseLock",true);
                        LayerShow();
                        if (notHaveViewResultData == null || notHaveViewResultData == undefined || notHaveViewResultData == "" || notHaveViewResultData == "null" || notHaveViewResultData.total == 0) {
                            notHaveViewList[index] = $("#viewList" + index);
                            
                            advancePlanId = planId;
                            $("#refreshPlanIndex").val(index);
                            $('#searchWindow').jqxWindow('open');
                        }
                   }); 
                   var notHaveViewResultData;
                   notHaveViewSources[index] = {
                       datatype: "json",
                       root:'resultList',
                       url:'<%=request.getContextPath()%>/shootPlanManager/planViewListJson',
                       type:'post',
                       data:{planId: planId, inPlan: false},
                       datafields: [
                           { name: 'viewId',type: 'string' },//场景ID
                           { name: 'seriesNo',type: 'int' },//集次
                           { name: 'viewNo',type: 'string' },//场次
                           { name: 'atmosphereId',type: 'string' }, //气氛ID
                           { name: 'atmosphereName',type: 'string' },   //气氛名称
                           { name: 'site',type: 'string' },//内外景
                           { name: 'viewType',type: 'int' },//文戏武戏
                           { name: 'shootLocation',type: 'string' },//拍摄地点
                           { name: 'firstLocation',type: 'string' },//主场景
                           { name: 'secondLocation',type: 'string' },//次场景
                           { name: 'thirdLocation',type: 'string' },//三级场景
                           { name: 'mainContent',type: 'string' },//主要内容
                           { name: 'pageCount',type: 'string' },//页数
                           { name: 'majorRole',type: 'string' },//主要演员
                           { name: 'guestRole',type: 'string' },//特约演员
                           { name: 'massRole',type: 'string' },//群众演员
                           { name: 'clothesName',type: 'string' },//服装
                           { name: 'makeupName',type: 'string' },//化妆
                           { name: 'propsName',type: 'string' },//道具
                           { name: 'specialPropsName', type: 'string'}, //特殊道具
                           { name: 'advertsInfo',type: 'string' },//广告
                           { name: 'shootDate',type: 'string' },//拍摄时间
                           { name: 'remark',type: 'string' },//备注
                           { name: 'shootStatus',type: 'string' }//拍摄状态
                       ],
                       beforeprocessing:function(data){
                           //查询之后可执行的代码
                           notHaveViewSources[index].totalrecords = data.result.total;
                           notHaveViewResultData = data.result;
                       },
                       processdata: function (data) {
                           //notHaveViewOldSources.data = {planId: planId, inPlan: false};
                           //查询之前可执行的代码
                       },
                    };  
                 var notHaveViewGridAdapter = new $.jqx.dataAdapter(notHaveViewSources[index]);
                 $("#viewList" + index).jqxGrid({
                    theme:theme,
                    width: 800,
                    height: 500,
                    source: notHaveViewGridAdapter,
                    selectionmode: 'checkbox',
                    pageable: true,
                    //autoheight: true,
                    columnsresize: true,
                    showtoolbar: true,
                    pagesize: pageSize,
                    pagerbuttonscount: 5,
                    virtualmode: true,
                    //pagermode: 'simple',
                    altrows: true,
                    rendergridrows:rendergridrows,
                    localization:localizationobj,//表格文字设置
                    rendertoolbar: function (toolbar) {
                        var container = $("<div style='margin: 5px;'></div>");
                        
                        var selected="<span class='badge' id='_already_selected_"+index+"' style='vertical-align: bottom;margin-bottom: 1px;margin-right: 10px;'>0</span>";
                        
                        var html = "<input type='button' style='margin-left:3px;' class='advanceSearch' id='viewAdvanceSearch" + index +"'/>";
                         html+="<input type='button' class='addToPlan' id='addToPlanBtn" + index +"'/>";
                         html+="<input type='button' class='close'  id='closeNotHaveViewBtn" + index +"'/>";
                         
                        toolbar.append(container);
                        container.append(selected);
                        container.append(html);
                        
                        $("#viewAdvanceSearch" + index).jqxTooltip({content: '高级查询', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                        $("#addToPlanBtn" + index).jqxTooltip({content: '添加到拍摄计划', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                        $("#closeNotHaveViewBtn" + index).jqxTooltip({content: '关闭', position: 'bottom', autoHide: true, name: 'movieTooltip'});
                        
                        //绑定表格选中和取消选中事件，用于获取选中的计划中没有的场景的ID编号，如此做可以保证在选中不同页的数据时获取准确数据
                        $("#viewList" + index).on('rowselect', function() {
                        	
                        	//显示选中的数量
                        	$("#_already_selected_"+index).html($("#viewList"+index).jqxGrid('getselectedrowindexes').length);
                        	
                            if (notHaveViewIds[index] == undefined || notHaveViewIds[index] == null || notHaveViewIds[index] == 'undefined') {
                                notHaveViewIds[index] = "";
                            }
                            var rowBoundIndex = JSON.stringify(args.rowindex);
                            if (rowBoundIndex.indexOf("[") != -1) {
                                rowBoundIndex = rowBoundIndex.substring(1, rowBoundIndex.length-1);
                            }
                            if (rowBoundIndex.indexOf(",") != -1) {
                                var rowIndexArr = rowBoundIndex.split(",");
                                for (var i = 0; i < rowIndexArr.length; i++) {
                                     notHaveViewIds[index] += notHaveViewResultData.resultList[getIndex($("#viewList" + index),rowIndexArr[i])].viewId+",";
                                }
                            } else {
                                //当反选表格时，该表格不会执行rowunselect事件，而是执行rowselect事件，并且rowBoundIndex值为""
                                if (rowBoundIndex == "") {
                                    rowBoundIndex = -1;
                                }
                                if (getIndex($("#viewList" + index),rowBoundIndex) != -1) {
                                    notHaveViewIds[index] += notHaveViewResultData.resultList[getIndex($("#viewList" + index),rowBoundIndex)].viewId+",";
                                } else {
                                    notHaveViewIds[index] = "";
                                }
                            }
                        });
                        $("#viewList" + index).on('rowunselect', function() {
                            
                        	//显示选中的数量
                        	$("#_already_selected_"+index).html($("#viewList"+index).jqxGrid('getselectedrowindexes').length);
                        	
                        	if (notHaveViewIds[index] == undefined || notHaveViewIds[index] == null) {
                                notHaveViewIds[index] = "";
                                return;
                              }
                              var rowBoundIndex = args.rowindex;
                              notHaveViewIds[index] = notHaveViewIds[index].replace(notHaveViewResultData.resultList[getIndex($("#viewList" + index),rowBoundIndex)].viewId+",", "");
                        });
                        
                        
                        $("#viewAdvanceSearch" + index).unbind("click");
                        $("#viewAdvanceSearch" + index).on('click', function() {
                           
                           notHaveViewList[index] = $("#viewList" + index);
                           
                           advancePlanId = planId;
                           $("#refreshPlanIndex").val(index);
                           $('#searchWindow').jqxWindow('open');
                        });
                        
                        
                        $("#addToPlanBtn" + index).unbind("click");
                        $("#addToPlanBtn" + index).on('click', function() {
                            //获取选中的场景IDs
                            var viewRowIndexes = $('#viewList' + index).jqxGrid('getselectedrowindexes');
                            if(viewRowIndexes.length==0){
                                showErrorMessage("请选择场次");
                                return;
                            }
                            viewRows = notHaveViewIds[index].substring(0, notHaveViewIds[index].length - 1);
                            
                            $.ajax({
                                url: "/shootPlanManager/addViewToPlan",
                                type: 'post',
                                data: {'planIds': planId, 'viewIds': viewRows},
                                dataType: 'json',
                                async: false,
                                success: function (param) {
                                    if (param.success) {
                                        showSuccessMessage(param.message);
                                        notHaveViewIds[index] = "";
                                        
                                        grid.jqxGrid('updatebounddata');
                                        refreshSinglePlanRowData(planId, planListGrid);
                                        
                                        $("#viewList" + index).jqxGrid('updatebounddata');
                                        $("#viewList" + index).jqxGrid('clearselection');
                                    } else {
                                        showErrorMessage(param.message);
                                    }
                                },
                                error: function () {
                                    showErrorMessage("发送请求失败");
                                }
                            });
                        });
                        
                        
                        $("#closeNotHaveViewBtn" + index).unbind("click");
                        $("#closeNotHaveViewBtn" + index).on('click', function() {
                            $("#addPlanView"+index).jqxDropDownButton("setCloseLock",false);//设置锁死弹出层
                            $("#addPlanView"+index).jqxDropDownButton('close');
                            LayerHide();
                        });
                         
                     },
                     columns: [
                        { text: '集-场', cellsrenderer: serialViewNoColumnWithoutClick, width: 65, pinned: true},
                        { text: '文武戏', cellsrenderer: viewTypeColumn , width: 60 }, 
                        { text: '计划拍摄日期',cellsrenderer: shootDateColumn, width: 100},
                        { text: '拍摄地点',cellsrenderer: shootLocationColumn, width: 120 },
                        { text: '气氛',cellsrenderer: atmosphereNameColumn, width: 40 },
                        { text: '内外景',cellsrenderer: siteColumn, datafield: 'site' ,width: 50 },
                        { text: '主场景',cellsrenderer: firstLocationColumn, width: 120 },
                        { text: '次场景',cellsrenderer: secondLocationColumn, width: 120 },
                        { text: '三级场景',cellsrenderer: thirdLocationColumn, width: 120 },
                        { text: '主要内容',cellsrenderer: mainContentColumn, width: 120 },
                        { text: '页数',cellsrenderer: pageCountColumn, width: 40 },
                        { text: '主要演员',cellsrenderer: majorRoleColumn, width: 150 },
                        { text: '特约演员',cellsrenderer: guestRoleColumn, width: 100 },
                        { text: '群众演员',cellsrenderer: massRoleColumn, width: 100 },
                        { text: '服装',cellsrenderer: clothesNameColumn, width: 100},
                        { text: '化妆',cellsrenderer: makeupNameColumn, width: 100},
                        { text: '道具',cellsrenderer: propsNameColumn, width: 100 },
                        { text: '特殊道具',cellsrenderer: specialPropsNameColumn, width: 100 },
                        { text: '商植',cellsrenderer: advertNameColumn, width: 100},                      
                        { text: '备注',cellsrenderer: remarkColumn, width: 90 },
                        { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: 90 }
                     ],
                    rendergridrows: rendergridrows
                });
                $("#viewList" + index).on("rowclick", function(event) {
                    jqxGridRowClick($("#viewList" + index), event);
                });
                
                ////////////////////////////////////添加场景到通告单//////////////////////////////
                $("#addToNotice" + index).unbind("click");
                $("#addToNotice" + index).on("click", function() {
                    showNoticeWindow(grid,index);
                    
		            $("#group").jqxDropDownList('selectIndex', 0);
		            $("#noticeTime").val(new Date().Format("yyyy-MM-dd"));
		            autoGetNoticeName();
                });
                 
                 /////////////////////设置拍摄地点/时间///////////////////////////////////////////
                 $("#setShootInfo" + index).unbind("click");
                 $("#setShootInfo" + index).on('click', function() {
                    if (hadPlanViewIds[index] == null || hadPlanViewIds[index] == '' || hadPlanViewIds[index] == undefined) {
                        showErrorMessage("请选择需要设置的场景");
                        return;
                    }
                    advancePlanId = planId;
                    showSetShootInfoWindow(planListGrid, index, planId);
                 })
                 
                 ///////////////////////////////////删除场景//////////////////////////////////////
                 $("#deletePlanView" + index).unbind("click");
                 $("#deletePlanView" + index).on('click', function () {
                     //获取选中的场景信息
                     var rowindexes = grid.jqxGrid('getselectedrowindexes');
                     if(rowindexes==""){
                         showErrorMessage("请选择要设置的场次！");
                         $('#setShootDate'+index).jqxDropDownButton('close'); 
                         return;
                     }
                     
                     var viewIds = "";
                     for(var i=0;i<rowindexes.length;i++){
                         viewIds += hadViewResultData[index].resultList[getIndex(grid, rowindexes[i])].viewId+",";
                     }
                     viewIds = viewIds.substring(0,viewIds.length-1);
                     
                     popupPromptBox("提示", "确定要移除所选场景吗？", function() {
	                     $.ajax({
	                      url:"/shootPlanManager/deleteViewFromPlan",
	                      data:{planId: planId, viewIds:viewIds},
	                      dataType:"json",
	                      type:"post",
	                      async:false,
	                      success:function(param){
	                          if(param.success){
                                  showSuccessMessage(param.message);
	                              grid.jqxGrid('updatebounddata');
	                              grid.jqxGrid('clearselection');
	                              
                                  refreshSinglePlanRowData(planId, planListGrid);
	                          } else {
	                              showErrorMessage(param.message);
	                          }
	                      }
	                     });
                     });
                     
                 });
                 
                 ///////////////////////////////添加场景到子计划//////////////////////////////////////
                 //只有当指定计划没有父计划的时候，才能添加子计划
                 if (ownParentPlanId != null && ownParentPlanId != undefined && ownParentPlanId != '') {
                    $("#addToSubPlan" + index).hide();
                 }
                 
                 
                 $("#addToSubPlan" + index).unbind("click");
                 $("#addToSubPlan" + index).on('click', function() {
                    if (hadPlanViewIds[index] == null || hadPlanViewIds[index] == '' || hadPlanViewIds[index] == undefined) {
                        showErrorMessage("请选择需要设置的场景");
                        return;
                    }
                    
                    showSubPlanWindow(index, planId);
                 });
                 
              },
              columns: [
                { text: '集-场', cellsrenderer: serialViewNoColumn, width: 65, pinned: true},
                { text: '文武戏', cellsrenderer: viewTypeColumn , width: 60 },      
                { text: '计划拍摄日期',cellsrenderer: shootDateColumn, width: 100},
                { text: '拍摄地点',cellsrenderer: shootLocationColumn, width: 120 },
                { text: '气氛',cellsrenderer: atmosphereNameColumn, width: 40 },
                { text: '内外景',cellsrenderer: siteColumn, datafield: 'site' ,width: 50 },
                { text: '主场景',cellsrenderer: firstLocationColumn, width: 120 },
                { text: '次场景',cellsrenderer: secondLocationColumn, width: 120 },
                { text: '三级场景',cellsrenderer: thirdLocationColumn, width: 120 },
                { text: '主要内容',cellsrenderer: mainContentColumn, width: 120 },
                { text: '页数',cellsrenderer: pageCountColumn, width: 40 },
                { text: '主要演员',cellsrenderer: majorRoleColumn, width: 150 },
                { text: '特约演员',cellsrenderer: guestRoleColumn, width: 100 },
                { text: '群众演员',cellsrenderer: massRoleColumn, width: 100 },
                { text: '服装',cellsrenderer: clothesNameColumn, width: 100},
                { text: '化妆',cellsrenderer: makeupNameColumn, width: 100},
                { text: '道具',cellsrenderer: propsNameColumn, width: 100 },
                { text: '特殊道具',cellsrenderer: specialPropsNameColumn, width: 100 },
                { text: '商植',cellsrenderer: advertNameColumn, width: 100},                 
                { text: '备注',cellsrenderer: remarkColumn, width: 90 },
                { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: 90 }
             ],
             rendergridrows: rendergridrows
          });
          
          
          grid.on("rowclick", function(event) {
              jqxGridRowClick(grid, event);
          });
       }
    }
    
    //设置UI框架中表格点击行中任意列选中和取消选中的方法
    function jqxGridRowClick(grid, event) {
        var args = event.args;
        var boundIndex = args.rowindex;
        
        var rowindexs = grid.jqxGrid('getselectedrowindexes');
        var containsFlag = false;
        
        for (var i = 0; i < rowindexs.length; i ++) {
            if (rowindexs[i] == boundIndex) {
               containsFlag = true;
            }
        }
        
        if(containsFlag) {
            grid.jqxGrid('unselectrow', boundIndex);
        } else {
            grid.jqxGrid('selectrow', boundIndex);
        }
    }
    //显现添加/修改拍摄计划面板
    function showPlanWindow(planGrid) {
        if (planWindow != null && planWindow != undefined) {
            $('#pplanWindow').jqxWindow("open");
            
            //如果面板已经初始化好，此时仍然需要重新绑定点击事件，因为planGrid值为变量，不重新绑定的话planGrid值不会实时更新
	        $("#savepPlanButton").unbind("click");
	        $("#savepPlanButton").on("click", function () {
	            $("#pgroupIdValue").val($("#pplanGroup").val());
	            if ($('#pplanForm').jqxValidator('validate')) {
	                $.ajax({
	                    url: "/shootPlanManager/saveShootPlan",
	                    type: 'post',
	                    data: $('#pplanForm').serialize(),
	                    dataType: 'json',
	                    async: false,
	                    success: function (param) {
	                        if (param.success) {
	                            showSuccessMessage(param.message);
	                            //$('#pplanForm')[0].reset();
	                            $("#pplanWindow").jqxWindow('close');
	                            planGrid.jqxGrid("updatebounddata");
	                            
	                            var rowindex = planGrid.jqxGrid('getselectedrowindex');
	                            if(rowindex != -1) {
	                                planGrid.jqxGrid('unselectrow', rowindex);
	                            }
	                        } else {
	                            showErrorMessage(param.message);
	                        }
	                    }
	                });
	            }
	        });
            return;
        }
       
        planWindow = $("#pplanWindow").jqxWindow({
            theme: theme,
            width: 400,
            maxHeight: 1600,
            height: 380,
            resizable: false,
            autoOpen: false,
            isModal: true,
            cancelButton: $(".pPlanCancelButton"),
            title: "添加到拍摄计划",
            initContent: function () {
               
            }
        });
        
        $("#pplanNameInput").jqxInput({theme: theme});
                
        $(".pPlanCancelButton").jqxButton({
            theme:theme, 
            width: 80, 
            height: 25 
        });
        
        $("#savepPlanButton").jqxButton({
            theme:theme, 
            width: 80, 
            height: 25
        });
        
        /*新建计划*/
        $("#pplanStartTime").jqxInput({
            theme: theme,
            width: '235px', 
            height: '28px'
        });
        
        $("#pplanEndTime").jqxInput({
            theme: theme,
            width: '235px', 
            height: '30px'
        });
        
        $("#pplanGroup").jqxDropDownList({
            theme:theme,
            selectedIndex:0,
            source: groupSourceForAdd, 
            displayMember: "text", 
            valueMember: "value", 
            width: '235px', 
            height: 30,
            dropDownHeight: getHeight(groupSourceForAdd)
        });
        
        $("#pplanGroup").on('change',function(event){
            var args = event.args;
            if (args) {
                var index = args.index;
                var item = args.item;
                
                if(item.value=="99"){
                    
                    if(index>25) {
                        showErrorMessage("目前最多选择到Z组");
                        $("#planGroup").jqxDropDownList("selectIndex",index-1);
                        return;
                    }
                    $.ajax({
                        url:"/shootGroupManager/saveGroup",
                        type:"post",
                        dataType:"json",
                        data:{groupName:groupArray[index-1].text},
                        success:function(data){
                            if(data.success){
                                $("#pplanGroup").jqxDropDownList("selectIndex",0);
                            }
                            $("#pplanGroup").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                            $("#pplanGroup").jqxDropDownList("selectIndex",index);
                        }
                    });
                }
            }
        });
        
        $('#pplanForm').jqxValidator({
            animationDuration: 1,
            rules: [{input: '#pplanNameInput', message: '计划名称不可为空!', action: 'keyup,blur', rule: 'required' }]
        });
        
        $("#savepPlanButton").unbind("click");
        $("#savepPlanButton").on("click", function () {
            $("#pgroupIdValue").val($("#pplanGroup").val());
            if ($('#pplanForm').jqxValidator('validate')) {
                $.ajax({
                    url: "/shootPlanManager/saveShootPlan",
                    type: 'post',
                    data: $('#pplanForm').serialize(),
                    dataType: 'json',
                    async: false,
                    success: function (param) {
                        if (param.success) {
                            showSuccessMessage(param.message);
                            //$('#pplanForm')[0].reset();
                            $("#pplanWindow").jqxWindow('close');
                            planGrid.jqxGrid("updatebounddata");
                            
                            var rowindex = planGrid.jqxGrid('getselectedrowindex');
                            if(rowindex != -1) {
                                planGrid.jqxGrid('unselectrow', rowindex);
                            }
                        } else {
                            showErrorMessage(param.message);
                        }
                    }
                });
            }
        });
        
        $("#pplanWindow").jqxWindow("show");
    }
    
    function showNoticeWindow(grid, index) {
        var viewIds = "";
        var rowIndexes = grid.jqxGrid('getselectedrowindexes');
        if (rowIndexes.length == 0) {
            showErrorMessage("请选择需要添加的场景");
            return;
        }
        for (var i = 0; i < rowIndexes.length; i++) {
            var data = grid.jqxGrid('getrowdata', rowIndexes[i])
            var viewId = data.viewId;
            viewIds += viewId + ",";
        }
        viewIds = viewIds.substring(0, viewIds.length - 1);
        
        /* if (noticeWindow != undefined) {
            $('#noticeWindow').jqxWindow("open");
            return;
        } */
    
        noticeWindow = $('#noticeWindow').jqxWindow({ 
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
               
            }
        });
        
        $("#saveNoticeButton, .noticeCancelButton, #addToNoticeButton").jqxButton({
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
        
        /* 新建通告单 */
        $("#noticeNameInput").jqxInput({theme: theme});
        $("#noticeTime").jqxInput({
            theme: theme,
            width: '300px', 
            height: '30px'
        });
        
        $("#group").jqxDropDownList({
            theme:theme,
            selectedIndex:0,
            source: groupSourceForAdd, 
            displayMember: "text", 
            valueMember: "value", 
            width: '300px', 
            height: 30,placeHolder: "",
            dropDownHeight: getHeight(groupSourceForAdd)
        });
        
        $("#group").on('change',function(event){
            
            var args = event.args;
            if (args) {
	            // index represents the item's index.                      
	            var index = args.index;
	            var item = args.item;
	            
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
	                        if(data.status!="0"){
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
        });
                    
        /* $("#noticeTime").on("change", function() {
            autoGetNoticeName();
        }); */
        
        $('#noticeForm').jqxValidator({
            animationDuration: 1,
            rules: [
              { input: '#noticeNameInput', message: '通告单名称不可为空!', action: 'keyup,blur', rule: 'required' }
            ]
        });
        
        
        $("#saveNoticeButton").unbind("click");
        $("#saveNoticeButton").on('click',function(){
            
            if($('#noticeForm').jqxValidator("validate")){
                var noticeName=$("#noticeNameInput").val();
                var group=$("#group").val();
                var noticeTime=$("#noticeTime").val();
                
                $.ajax({
                    url:"/notice/noticeSave",
                    type:"post",
                    dataType:"json",
                    data:{noticeName:noticeName,groupId:group,noticeDateStr:noticeTime,viewIds:viewIds},
                    async:false,
                    success:function(data){
                        if(!data.success){
                            $("#addNoticeError").html("<div>错误提示：</div><div>" + data.message + "</div>");
                        }else{
                            showSuccessMessage("添加通告单成功！");
                            
                            $("#noticeNameInput").val("");
                            $("#group").jqxDropDownList("selectIndex",0);
                            $("#noticeTime").jqxDateTimeInput('setDate', getNewDay(new Date(),1));
                            
                            $('#existNoticeGrid').jqxGrid("updatebounddata");
                            $('#noticeWindow').jqxWindow('close');
                            
                            nestedGrids[index].jqxGrid("clearselection");
                        }
                    }
                });
            }
        });
        
        /* 已有通告单 */
        var noticeResultData;
        var noticeSource =
        {
           datatype: "json",
           root:'resultList',
           url:'/notice/loadNotice',
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
        var canceledStatusRenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            if(rowdata.canceledStatus==0){
                return "<div class='rowStatusColor' style='padding-top:5px;'>未销场</div>";
            }else if(rowdata.canceledStatus==1){
                return "<div class='rowStatusColor' style='padding-top:5px;'>已销场</div>";
            }
        }
        var noticeDataAdapter = new $.jqx.dataAdapter(noticeSource);
        $("#existNoticeGrid").jqxGrid({
            theme:theme,
            width: '99%',
            source: noticeDataAdapter,
            selectionmode: 'checkbox',
            //altrows: true,
            pageable: false,
            //virtualmode :true,
            autoheight: false,
            height: 258,
            columnsresize: true,
            showtoolbar: false,
            //pagermode: "simple",
            //pagesize: pageSize,
            //pagerbuttonscount: 10,
            rendergridrows:rendergridrows,
            localization:localizationobj,//表格文字设置
            columns: [
              { text: '通告单名称', datafield: 'noticeName', width: 220},
              { text: '时间', datafield: 'noticeDate', cellsformat: 'yyyy-MM-dd', width: 190},
              { text: '分组', datafield: 'groupName', width: 60},
              { text: '状态', cellsrenderer:canceledStatusRenderer, width: 96}
            ]
        });
        
        
        
        $("#addToNoticeButton").unbind("click");
        $("#addToNoticeButton").on('click', function () {
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
                noticeRows += resultrow.noticeId+",";
            }
            noticeRows = noticeRows.substring(0, noticeRows.length - 1);
            
            $.ajax({
                url: "/notice/addNoticeView",
                type: 'post',
                data: {'noticeId': noticeRows, 'viewIds': viewIds},
                dataType: 'json',
                async: false,
                success: function (param) {
                    if (param.status == "0") {
                        showSuccessMessage("添加成功");
                        $('#existNoticeGrid').jqxGrid('clearselection');
                        $('#noticeWindow').jqxWindow('close');
                        
                        nestedGrids[index].jqxGrid("clearselection");
                    } else {
                        showErrorMessage(param.message);
                    }
                },
                error: function () {
                    showErrorMessage("发送请求失败");
                }
            });
       });
       
       $('#noticeWindow').jqxWindow("open");
    }
    
    function showSubPlanWindow(index, parentPlanId) {
        //hadPlanViewIds[index]值是根据表格的rowselect和rowunselect事件产生变动的，而当表格全选后再取消全选，此时表格没有触发rowunselect事件
        //这样会导致根据hadPlanViewIds[index]获取选中的场景ID不准确
        //再者由于此处场景表没有分页，所以没有翻页后获取选中数据出错的情况，所以此处引入两种判断是否选中场景表数据的方式
        if (hadPlanViewIds[index] == null || hadPlanViewIds[index] == '' || hadPlanViewIds[index] == undefined) {
            showErrorMessage("请选择需要添加的场景");
            return;
        }
        
        var viewIds = "";
        var rowIndexes = nestedGrids[index].jqxGrid('getselectedrowindexes');
        if (rowIndexes.length == 0) {
            showErrorMessage("请选择需要添加的场景");
            return;
        }
        for (var i = 0; i < rowIndexes.length; i++) {
            var data = nestedGrids[index].jqxGrid('getrowdata', rowIndexes[i])
            var viewId = data.viewId;
            viewIds += viewId + ",";
        }
        viewIds = viewIds.substring(0, viewIds.length - 1);
        
        
        if (subPlanWindow != undefined) {
            $("#subPlanWindow").jqxWindow("open");
            
            s_planSource.url = '/shootPlanManager/subShootPlanJson?parentPlanId=' + parentPlanId;
            $("#s_existPlanGrid").jqxGrid('updatebounddata');
            
            //如果第二次调用该方法，parentPlanId改变后，如果不重新绑定一下事件，会导致parentPlanId值不是实时的
            $("#s_savePlanButton").unbind("click");
	        $("#s_savePlanButton").on("click", function () {
	            $("#s_groupIdValue").val($("#s_planGroup").val());
	            
	            $("#parentPlanIdInput").val(parentPlanId);
	            
	            $("#s_viewIdsInput").val(viewIds);
	            if ($('#s_planForm').jqxValidator('validate')) {
	                $.ajax({
	                    url: "/shootPlanManager/saveShootPlan",
	                    type: 'post',
	                    data: $('#s_planForm').serialize(),
	                    dataType: 'json',
	                    async: false,
	                    success: function (param) {
	                        if (param.success) {
	                            showSuccessMessage(param.message);
	                            $('#s_planForm')[0].reset();
	                            
	                            $("#subPlanWindow").jqxWindow('close');
	                            
	                            nestedGrids[index].jqxGrid("clearselection");
	                            nestedGrids[index].jqxGrid("updatebounddata");
	                            hadPlanViewIds[index] = "";
	                            
	                            $('#s_existPlanGrid').jqxGrid("updatebounddata");
	                        } else {
	                            showErrorMessage(param.message);
	                        }
	                    }
	                });
	            }
	        });
            return;
        }
    
        subPlanWindow = $("#subPlanWindow").jqxWindow({
	        theme: theme,
	        width: 640,
	        maxHeight: 1600,
	        height: 420,
	        resizable: false,
	        autoOpen: false,
	        isModal: true,
	        cancelButton: $(".s_planCancelButton"),
	        title: "添加到子计划",
	        initContent: function () {
	        
	        }
	    });
	    
	    $("#s_planNameInput").jqxInput({theme: theme});
        $(".s_planCancelButton").jqxButton({
            theme:theme, 
            width: 80, 
            height: 25 
        });
        
        $("#s_savePlanButton").jqxButton({
            theme:theme, 
            width: 80, 
            height: 25
        });
        
        $("#s_addToPlanButton").jqxButton({
            theme:theme, 
            width: 80, 
            height: 25
        });
        
        $("#s_planDivForm").jqxExpander({
            theme: theme,
            width: '100%',
            expanded: true,
        });
        
        $("#s_planDivList").jqxExpander({
            theme: theme,
            width: '100%',
            expanded: false
        });
        
        $("#s_planDivForm").on('expanding', function () {
            $("#s_planDivList").jqxExpander('collapse');
        });
        $("#s_planDivList").on('expanding', function () {
            $("#s_planDivForm").jqxExpander('collapse');
        });
        
        
        /*新建计划*/
        $("#s_planStartTime, #s_planEndTime").jqxInput({
            theme: theme,
            width: '300px', 
            height: '28px'
        });
        
        $("#s_planGroup").jqxDropDownList({
            theme:theme,
            selectedIndex:0,
            source: groupSourceForAdd, 
            displayMember: "text", 
            valueMember: "value", 
            width: '300px', 
            height: 28,
            dropDownHeight: getHeight(groupSourceForAdd)
        });
        
        $("#s_planGroup").on('change',function(event){
            var args = event.args;
            if (args) {
                var index = args.index;
                var item = args.item;
                
                if(item.value=="99"){
                    
                    if(index>25){
                        showErrorMessage("目前最多选择到Z组");
                        $("#planGroup").jqxDropDownList("selectIndex",index-1);
                        return;
                    }
                    $.ajax({
                        url:"/shootGroupManager/saveGroup",
                        type:"post",
                        dataType:"json",
                        data:{groupName:groupArray[index-1].text},
                        success:function(data){
                            if(data.status!="0"){
                                $("#s_planGroup").jqxDropDownList("selectIndex",0);
                            }
                            $("#s_planGroup").jqxDropDownList('insertAt', {text:data.group.groupName,value:data.group.groupId}, index);
                            $("#s_planGroup").jqxDropDownList("selectIndex",index);
                        }
                    });
                }
            }
        });
        
        $('#s_planForm').jqxValidator({
            animationDuration: 1,
            rules: [{input: '#s_planNameInput', message: '计划名称不可为空!', action: 'keyup,blur', rule: 'required' }]
        });
        
        
        $("#s_savePlanButton").unbind("click");
        $("#s_savePlanButton").on("click", function () {
            $("#s_groupIdValue").val($("#s_planGroup").val());
            
            $("#parentPlanIdInput").val(parentPlanId);
            
            $("#s_viewIdsInput").val(viewIds);
            if ($('#s_planForm').jqxValidator('validate')) {
                $.ajax({
                    url: "/shootPlanManager/saveShootPlan",
                    type: 'post',
                    data: $('#s_planForm').serialize(),
                    dataType: 'json',
                    async: false,
                    success: function (param) {
                        if (param.success) {
                            showSuccessMessage(param.message);
                            $('#s_planForm')[0].reset();
                            
                            $("#subPlanWindow").jqxWindow('close');
                            
                            nestedGrids[index].jqxGrid("clearselection");
                            nestedGrids[index].jqxGrid("updatebounddata");
                            hadPlanViewIds[index] = "";
                            
                            $('#s_existPlanGrid').jqxGrid("updatebounddata");
                        } else {
                            showErrorMessage(param.message);
                        }
                    }
                });
            }
        });
        
        /*已有计划*/
        var planResultData;
        s_planSource =
        {
           datatype: "json",
           root:'resultList',
           url:'/shootPlanManager/subShootPlanJson?parentPlanId=' + parentPlanId,
           datafields: [
               { name: 'planId', type: 'string' },
               { name: 'planName', type: 'string' },
               { name: 'startDate', type: 'date'},
               { name: 'endDate', type: 'date'},
               { name: 'finishRate', type: 'string' },
               { name: 'groupName', type: 'string'}
           ],
           type:'post',
           processdata: function (data) {
               //查询之前可执行的代码
           },
           beforeprocessing:function(data){
               //查询之后可执行的代码
               s_planSource.totalrecords=data.result.total;
               planResultData = data.result;
           }
        };
        
        //计划时间列
        var planTimeCellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
            var startDate = rowdata.startDate.Format('yyyy/MM/dd');
            var endDate = rowdata.endDate.Format('yyyy/MM/dd');
            
            var html = "<div style='line-height:26px;'>" + startDate + " — " + endDate + "</div>";
            return html;
        };
        var planDataAdapter = new $.jqx.dataAdapter(s_planSource);
        $("#s_existPlanGrid").jqxGrid({
            theme:theme,
            width: '99%',
            source: planDataAdapter,
            selectionmode: 'checkbox',
            //altrows: true,
            //pageable: true,
            //virtualmode :true,
            autoheight: false,
            height: 258,
            columnsresize: true,
            showtoolbar: false,
            //pagermode: "simple",
            //pagesize: pageSize,
            //pagerbuttonscount: 5,
            rendergridrows: rendergridrows,
            localization:localizationobj,//表格文字设置
            columns: [
              { text: '计划名称', datafield: 'planName', width: 220},
              { text: '计划起始时间', cellsrenderer: planTimeCellsrenderer, width: 200},
              { text: '分组', datafield: 'groupName', width: 60},
              { text: '已完成', datafield: 'finishRate', cellsformat: 'p', width: 94}
            ]
        });
        
        
        $("#s_addToPlanButton").unbind("click");
        $("#s_addToPlanButton").on('click', function () {
            //获取选中的拍摄计划ID
            var planGridRowIndexes = $('#s_existPlanGrid').jqxGrid('getselectedrowindexes');
            if(planGridRowIndexes.length == 0){
                showErrorMessage("请选择拍摄计划");
                return;
            }
            var planRows="";
            for(var i = 0; i < planGridRowIndexes.length; i++){
                var resultrow = planResultData.resultList[planGridRowIndexes[i]];
                planRows += resultrow.planId+",";
            }
            planRows = planRows.substring(0, planRows.length - 1);
            
            $.ajax({
                url: "/shootPlanManager/addViewToPlan",
                type: 'post',
                data: {'planIds': planRows, 'viewIds': viewIds},
                dataType: 'json',
                async: false,
                success: function (param) {
                    if (param.success) {
                        showSuccessMessage(param.message);
                        $("#subPlanWindow").jqxWindow('close');
                        
                        nestedGrids[index].jqxGrid("clearselection");
                        nestedGrids[index].jqxGrid("updatebounddata");
                        hadPlanViewIds[index] = "";
                        
                        $('#s_existPlanGrid').jqxGrid('clearselection');
                    } else {
                        showErrorMessage(param.message);
                    }
                },
                error: function () {
                    showErrorMessage("发送请求失败");
                }
            });
       });
	    
	    $("#subPlanWindow").jqxWindow("open");
    }
    
    /**
      * 刷新拍摄计划表的单行数据
      * @param planId 需要刷新的行中计划ID
      * @param planListGrid 计划表格对象
      */
    function refreshSinglePlanRowData(planId, planListGrid) {
        $.ajax({
            url:"/shootPlanManager/queryOnePlanJson",
            data:{planId: planId},
            dataType:"json",
            type:"post",
            async:false,
            success:function(param){
                if(param.success) {
                    var planInfo = param.shootPlanInfo;
                    var selectedrowindex = planListGrid.jqxGrid('getselectedrowindex');
                    var rowscount = planListGrid.jqxGrid('getdatainformation').rowscount;
                    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                        var id = planListGrid.jqxGrid('getrowid', selectedrowindex);
                        //var commit = planListGrid.jqxGrid('updaterow', id, planInfo);
                        
                        planListGrid.jqxGrid('setcellvaluebyid', id, "shootLocations", planInfo.shootLocations);
                        planListGrid.jqxGrid('setcellvaluebyid', id, "viewNumTotal", planInfo.viewNumTotal);
                        planListGrid.jqxGrid('setcellvaluebyid', id, "pageCountNumTotal", planInfo.pageCountNumTotal);
                        planListGrid.jqxGrid('setcellvaluebyid', id, "finishRate", planInfo.finishRate);
                        
                        planListGrid.jqxGrid('ensurerowvisible', selectedrowindex);
                        
                    }
                } else {
                    showErrorMessage(param.message);
                }
            }
        });
    }
    
    function showSetShootInfoWindow(planGrid, index, planId) {
        if (setShootPlanInfoWindow == undefined) {
            setShootPlanInfoWindow = $('#setShootInfoWindow').jqxWindow({
	            theme:theme,
	            autoOpen:false,
	            zIndex:20000,
	            maxHeight: 400, 
	            maxWidth: 700, 
	            minHeight: 200, 
	            minWidth: 200, 
	            height: 230, 
	            width: 380,
	            isModal : true,
	            resizable: false,
	            title: '设置拍摄时间/地址',
	            //showCloseButton: false,
	            cancelButton: '#cancelSetShootInfoBtn',
	            initContent: function () {
	                $("#shootDate").jqxInput({
	                     theme: theme,
	                     width: '200px', 
	                     height: '25px'
	                });
	                var shootLocationSource =
	                {
	                    datatype: "json",
	                    url:'<%=basePath%>/sceneViewInfoController/queryShootLocationList',
	                    datafields: [
	                        { name: 'id',type: 'string' },
	                        { name: 'vname',type: 'string' },
	                    ],
	                    beforeprocessing:function(data){
	                        //查询之后可执行的代码
	                        addressData=data;
	                    },
	                };
	                var shootLocationDataAdapter = new $.jqx.dataAdapter(shootLocationSource);
	                $("#shootLocation").jqxComboBox({
	                    theme: theme, 
	                    width: '200px',
	                    source: shootLocationDataAdapter,
	                    searchMode: 'contains',
	                    placeHolder: "请输入或选择拍摄地！",  
	                    displayMember: "vname",
	                    valueMember: "vname"
	                });
	                
	                $("#setShootInfoBtn, #cancelSetShootInfoBtn").jqxButton({
	                    theme:theme, 
	                    width: 80, 
	                    height: 25
	                });
	            }
	        });
        }
        
        var viewIds = "";
        var rowIndexes = nestedGrids[index].jqxGrid('getselectedrowindexes');
        if (rowIndexes.length == 0) {
            showErrorMessage("请选择需要添加的场景");
            return;
        }
        for (var i = 0; i < rowIndexes.length; i++) {
            var data = nestedGrids[index].jqxGrid('getrowdata', rowIndexes[i])
            var viewId = data.viewId;
            viewIds += viewId + ",";
        }
        viewIds = viewIds.substring(0, viewIds.length - 1);
        
        
        
        $("#setShootInfoBtn").unbind("click");
        $("#setShootInfoBtn").on('click', function() {
            var hadViewGrid = nestedGrids[index];
            
            //获取拍摄地点时间数据
            var shootLocation = $("#shootLocation").val();
            var shootDate = $("#shootDate").val();
            
            ///数据校验
            if ((shootLocation == null || shootLocation == '' || shootLocation == undefined)
                && (shootDate == null || shootDate == '' || shootDate == undefined)) {
                showErrorMessage("请填写拍摄地点或拍摄时间");
                return;
            }
            
            var successFlag = true;
            //设置拍摄地点
            if (shootLocation != null && shootLocation != '' && shootLocation != undefined) {
                $.ajax({
                    url:"/viewManager/saveAddress",
                    data:{viewIds:viewIds, addressStr: shootLocation},
                    dataType:"json",
                    type:"post",
                    async:false,
                    success:function(data){
                        if(data.status==1){
                            showErrorMessage(data.message);
                            successFlag = false;
                        } else {
                            var item = $("#shootLocation").jqxComboBox('getItemByValue', shootLocation);
                            if (item == undefined) {
                                var newItem = {label:shootLocation, value:shootLocation};
                                $("#shootLocation").jqxComboBox("addItem", newItem);
                                $("#shootLocation").jqxComboBox('selectItem', newItem);
	                            $("#shootLocationSelect").append("<option value='"+shootLocation+"'>"+shootLocation+"</option>");
	                            $("#shootLocationSelect").selectpicker('refresh');
                            }
                        }
                    }
                });
            }
            
            //设置拍摄时间
            if (shootDate != null && shootDate != '' && shootDate != undefined) {
                $.ajax({
                    url:"/shootPlanManager/saveShootDate",
                    data:{planId: advancePlanId, viewIds:viewIds, shootDate: shootDate},
                    dataType:"json",
                    type:"post",
                    async:false,
                    success:function(param){
                        if(!param.success){
                            showErrorMessage(param.message);
                            successFlag = false;
                        }
                    }
                });
            }
            
            if (successFlag) {
                showSuccessMessage("操作成功");
                $('#setShootInfoWindow').jqxWindow('close');
                hadPlanViewIds[index] = "";
                
                refreshPlanGridRow(hadViewGrid, {inPlan: true, planId: advancePlanId, viewIds:viewIds});
                
                hadViewGrid.jqxGrid('clearselection');
                //hadViewGrid.jqxGrid('updatebounddata', 'cells');
                
                refreshSinglePlanRowData(planId, planGrid);
            }
        });
        
        $('#setShootInfoWindow').jqxWindow("open");
    }
	
    //获取剧组名称
    function loadCrewName(){
		$.ajax({
			url: '/scenarioManager/getCrewType',
			type: 'post',
			async: false,
			datatype: 'json',
			success: function(response){
				if(response.success){
		            crewName = response.crewName; //剧组名称
				}else{
						showErrorMessage(response.message);
				}
			}
		});
	}
    
	//刷新计划下场景表的单行数据
	function refreshPlanGridRow(viewGrid, queryCondition) {
	   $.ajax({
	       url:"/shootPlanManager/planViewListJson",
	       data: queryCondition,
	       dataType:"json",
	       type:"post",
	       async:false,
	       success:function(param){
	           var viewList = param.result.resultList;
	           var selectedrowindexs = viewGrid.jqxGrid('getselectedrowindexes');
               var rowIDs = new Array();
	           for (var i = 0; i < selectedrowindexs.length; i++) {
	               var id = viewGrid.jqxGrid('getrowid', selectedrowindexs[i]);
	               rowIDs.push(id);
	               
                   viewGrid.jqxGrid('updaterow', id, viewList[i]);
	           }
	       }
       })
	}
	
	//异步加载高级查询条件
	function loadSearchCondition() {
	    //查询拍摄地信息，采用同步查询，只有这样查询过后为所有元素绑定的事件才会生效
	    $.ajax({
	        url:"/viewManager/loadAdvanceSerachData",
	        dataType:"json",
	        type:"post",
	        async: true,
	        success:function(data){
	            if(data.success) {
	                var viewFilterDto = data.viewFilterDto;
	                var atmosphereList = viewFilterDto.atmosphereList;
	                var seasonList = viewFilterDto.seasonList;
	                var shootStatusList = viewFilterDto.shootStatusList;
	                var siteList = viewFilterDto.siteList;
	                var viewLocationList = viewFilterDto.viewLocationList;
	                var firstLocationList = viewFilterDto.firstLocationList;
	                var secondLocationList = viewFilterDto.secondLocationList;
	                var thirdLocationList = viewFilterDto.thirdLocationList;
	                var majorRoleList = viewFilterDto.majorRoleList;
	                var guestRoleList = viewFilterDto.guestRoleList;
	                var massesRoleList = viewFilterDto.massesRoleList;
	                var commonPropList = viewFilterDto.commonPropList;
	                var specialPropList  = viewFilterDto.specialPropList;
	                var cultureTypeList = viewFilterDto.cultureTypeList;
	                var clotheList = viewFilterDto.clotheList;
	                var makeupList = viewFilterDto.makeupList;
	                var shootLocationList = viewFilterDto.shootLocationList;
	                var advertInfoList = viewFilterDto.advertInfoList;
	                
	                for (var season in seasonList) {
	                    $("#seasonSelect").append("<option value="+ season + ">" + seasonList[season] + "</option>");
	                }
	                $("#seasonSelect").selectpicker('refresh');
	                
	                for (var atm in atmosphereList) {
	                    $("#atmosphereSelect").append("<option value="+ atm + ">" + atmosphereList[atm] + "</option>");
	                }
	                $("#atmosphereSelect").selectpicker('refresh');
	                
	                for (var site in siteList) {
	                    $("#siteSelect").append("<option value="+ siteList[site] + ">" + siteList[site] + "</option>");
	                }
	                $("#siteSelect").selectpicker('refresh');
	                
	                for (var shotstatus in shootStatusList) {
	                    $("#shootStatusSelect").append("<option value="+ shotstatus + ">" + shootStatusList[shotstatus] + "</option>");
	                }
	                $("#shootStatusSelect").selectpicker('refresh');
	                
	                for (var cultureType in cultureTypeList) {
	                    $("#cultureTypeSelect").append("<option value="+ cultureType + ">" + cultureTypeList[cultureType] + "</option>");
	                }
	                $("#cultureTypeSelect").selectpicker('refresh');
	                
	                for (var advert in advertInfoList) {
	                    $("#advertInfoSelect").append("<option value="+ advert + ">" + advertInfoList[advert] + "</option>");
	                }
	                $("#advertInfoSelect").selectpicker('refresh');
	                
	                
	                for (var shotLocation in shootLocationList) {
	                    $("#shootLocationSelect").append("<option value="+ shotLocation + ">" + shootLocationList[shotLocation] + "</option>");
	                }
	                $("#shootLocationSelect").selectpicker('refresh');
	                
	                for (var fLocation in firstLocationList) {
	                    $("#firstLocationSelect").append("<option value="+ fLocation + ">" + firstLocationList[fLocation] + "</option>");
	                }
	                $("#firstLocationSelect").selectpicker('refresh');
	                
	                for (var sLocation in secondLocationList) {
	                    $("#secondLocationSelect").append("<option value="+ sLocation + ">" + secondLocationList[sLocation] + "</option>");
	                }
	                $("#secondLocationSelect").selectpicker('refresh'); 
	                
	                
	                
	                for (var mrole in majorRoleList) {
	                    $("#majorRoleSelect").append("<option value="+ mrole + ">" + majorRoleList[mrole] + "</option>");
	                }
	                $("#majorRoleSelect").selectpicker('refresh');
	                
	                for (var grole in guestRoleList) {
	                    $("#guestRoleSelect").append("<option value="+ grole + ">" + guestRoleList[grole] + "</option>");
	                }
	                $("#guestRoleSelect").selectpicker('refresh');
	                
	                for (var mrole in massesRoleList) {
	                    $("#massRoleSelect").append("<option value="+ mrole + ">" + massesRoleList[mrole] + "</option>");
	                }
	                $("#massRoleSelect").selectpicker('refresh');
	                
	                
	                
	                for (var cloth in clotheList) {
	                    $("#clothSelect").append("<option value="+ cloth + ">" + clotheList[cloth] + "</option>");
	                }
	                $("#clothSelect").selectpicker('refresh');
	                
	                for (var makeup in makeupList) {
	                    $("#makeupSelect").append("<option value="+ makeup + ">" + makeupList[makeup] + "</option>");
	                }
	                $("#makeupSelect").selectpicker('refresh');
	                
	                for (var cprop in commonPropList) {
	                    $("#propSelect").append("<option value="+ cprop + ">" + commonPropList[cprop] + "</option>");
	                }
	                $("#propSelect").selectpicker('refresh');
	                
	                for (var sprop in specialPropList) {
	                    $("#specialPropSelect").append("<option value="+ sprop + ">" + specialPropList[sprop] + "</option>");
	                }
	                $("#specialPropSelect").selectpicker('refresh');
	            }
	        }
	    })
	}
	
	function loadDiv() {
	    var sub = "<div style='z-index: 99999; margin-left: -66px; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;'>"
	            +"<div style='float: left; overflow: hidden; width: 32px; height: 32px;' class='jqx-grid-load'/>" 
	            + "<span style='margin-top: 10px; float: left; display: block; margin-left: 5px;' >" +"上传中..." + "</span>" 
	            + "</div>";
	    var div = "<div id='_layer_'> " 
	            + "<div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 9999; position: absolute;"
	            + "left: 0; top: 0; overflow: hidden; display: none'>" 
	            + "</div>" 
	            + "<div id='_wait_' style='z-index: 1005; position: absolute; width:430px;height:218px; display: none'  >" 
	            + "<center>" 
	            + "</center>" 
	            + "</div>" 
	            + "</div>";
	    return div;
	}
	function LayerShow() {
	    var addDiv = loadDiv();
	    
	    var element = $(addDiv).appendTo(document.body);
	    $(window).resize(Position);
	    var deHeight = $(document).height();
	    var deWidth = $(document).width();
	    Position();
	    $("#_MaskLayer_").show();
	    $("#_wait_").show();
	}
	function Position() {
	    $("#_MaskLayer_").width($(document).width());
	    var deHeight = $(window).height();
	    var deWidth = $(window).width();
	    $("#_wait_").css({
	        left : (deWidth - $("#_wait_").width()) / 2 + "px",
	        top : (deHeight - $("#_wait_").height()) / 2 + "px"
	    });
	}
	function LayerHide() {
	    $("#_MaskLayer_").hide();
	    $("#_wait_").hide();
	    del();
	}
	function del() {
	    var delDiv = document.getElementById("_layer_");
	    delDiv.parentNode.removeChild(delDiv)
	};
	
	
	//自动生成通告单名称
	function autoGetNoticeName() {
	    var selectGroupItem = $("#group").jqxDropDownList('getSelectedItem');
	    if (selectGroupItem == undefined) {
	       return false;
	    }
	    var groupName = selectGroupItem.label;
	    var noticeDateStr = $("#noticeTime").val();
	    
	    var noticeDateArr = noticeDateStr.split("-");
	    var noticeDate = noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日";
	    
	    $("#noticeNameInput").val(crewName + noticeDate + groupName+"通告");
	    
	    $("#addNoticeError").html("");
	}
	</script>
  </head>
  
  <body>
    <div id="shootPlanGrid"></div>
    <div id='statistics' class='tgji' style='letter-spacing: 1px;line-height: 20px;display:block;padding-left: 5px;padding-right: 5px;margin-top:5px;'></div>
    <input type="hidden" value="" id="refreshPlanIndex">
    <!-- 添加场景中的高级搜索面板 -->
    <div id="searchWindow" style="display:none;">
        <div id="customWindowHeader">
             <span id="captureContainer" style="float: left">高级查询 </span>
        </div>
        <div id="dropDownDIV" class='Popups_box'>
            <div class="classify">基本信息：</div>
            <ul class="searchUl">
                <li>
                    <label>集场区间：</label>
                    <input type='text' name='startSeriesNo' id='startSeriesNo' />-<input type='text' name='startViewNo' id='startViewNo' />到
                    <input type='text' name='endSeriesNo' id='endSeriesNo' />-<input type='text' name='endViewNo' id='endViewNo' />
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>集场编号：</label>
                   <input type="text" id="seriesViewNos">
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                    <label>季&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;节：</label>
                    <select id="seasonSelect" class="selectpicker show-tick" multiple>
                        <option value="blank">[空]</option>
                    </select>
                    <input type="hidden" class="preValue" />
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                    <label>气&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;氛：</label>
                    <select id="atmosphereSelect" class="selectpicker show-tick" multiple>
                        <option value="blank">[空]</option>
                    </select>
                    <input type="hidden" class="preValue" />
                </li>
            </ul>
            
			<ul class='searchUl'>
			    <li>
			       <label>内&nbsp;外&nbsp;&nbsp;景：</label>
			       <select id="siteSelect" class="selectpicker show-tick" multiple>
			           <option value="blank">[空]</option>
			       </select>
			       <input type="hidden" class="preValue" />
			    </li>
			</ul>
            
            <ul class='searchUl'>
                <li>
                   <label>文/武&nbsp;&nbsp;戏：</label>
                   <select id="cultureTypeSelect" class="selectpicker show-tick" multiple>
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                </li>
            </ul>
            
            <ul class='searchUl'>
               <li>
                   <label>拍摄状态：</label>
                   <select id="shootStatusSelect" class="selectpicker show-tick" multiple>
                   </select>
                   <input type="hidden" class="preValue" />
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植：</label>
                   <select id="advertInfoSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>主要内容：</label>
                   <input type="text" id="mainContent">
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</label>
                   <input type="text" id="viewRemark">
                </li>
            </ul>
            
            <div class="classify">场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景：</div>
            <ul class='searchUl'>
                <li>
                    <label>拍摄地点：</label>
                    <select id="shootLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
                        <option value="blank">[空]</option>
                    </select>
                    <input type="hidden" class="preValue" />
                    <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'></ul>
            
            <ul class='searchUl'>
                <li>
                    <label>主&nbsp;&nbsp;场&nbsp;景：</label>
                    <select id="firstLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
                        <option value="blank">[空]</option>
                    </select>
                    <input type="hidden" class="preValue" />
                    <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                    <label>次&nbsp;&nbsp;场&nbsp;景：</label>
                    <select id="secondLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
                        <option value="blank">[空]</option>
                    </select>
                    <input type="hidden" class="preValue" />
                    <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <div class="classify">演&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;员：</div>
            <ul class='searchUl'>
                <li>
                   <label>主要演员：</label>
                   <select id="majorRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li id="searchModeLI" style="">
                    <dd style="display: none;float:left;" id="anyOneAppear"><input name="searchMode" value="1" type="radio" checked="checked"/>出现即可</dd>
                    <dd style="display: none;float:left;" id="noOneAppear" ><input name="searchMode" value="3" type="radio"/>不出现</dd>
                    <dd style="display: none;float:left;" id="everyOneAppear" ><input name="searchMode" value="0" type="radio"/>同时出现</dd>
                    <dd style="display: none;float:left;" id="notEvenyOneAppear"><input name="searchMode"  value="2" type="radio"/>不同时出现</dd>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>特约演员：</label>
                   <select id="guestRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>群众演员：</label>
                   <select id="massRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <div class="classify">服&nbsp;&nbsp;化&nbsp;道：</div>
            <ul class='searchUl'>
                <li>
                   <label>服&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;装：</label>
                   <select id="clothSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>化&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;妆：</label>
                   <select id="makeupSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>道&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;具：</label>
                   <select id="propSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            
            <ul class='searchUl'>
                <li>
                   <label>特殊道具：</label>
                   <select id="specialPropSelect" class="selectpicker show-tick" multiple data-live-search="true">
                       <option value="blank">[空]</option>
                   </select>
                   <input type="hidden" class="preValue" />
                   <a style="display:none; cursor:pointer; font-size:13px; font-family:'微软雅黑';" class="clearSelection">清空</a>
                </li>
            </ul>
            <!-- <ul class='xiala' style=' height:25px; font-size:14px; line-height:25px;'>
            <h4>三级场景：</h4>
            <li>
            <div id='thirdLevelViewListDIV'></div></li>
            </ul> -->
            <ul style="width:100%;text-align:center;padding:0px 0px;">
                <li style="text-align:center;padding:0px 0px;">
                    <input type='button'  value='查询' id='searchSubmit' style="margin-top:10px;"/>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <input type='button'  value='关闭' id='closeSearchSubmit'/>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <input type='button'  value='清空' id='clearSearchButton'/>
                </li>
            </ul>
        </div>
    </div>
    <div id="viewContentWindow">
         <div id="windowHeader">
             <span id="viewTitle">
                 
             </span>
         </div>
         <div style="overflow:auto; line-height:22px;letter-spacing:1px; font-size:12px;" id="viewContent">
             
         </div>
    </div>
    <div id="setShootInfoWindow" style="display: none;">
         <div style="overflow:auto; line-height:22px;letter-spacing:1px; font-size:12px;" id="shootInfoContent" class="Popups_box">
            <table class="register-table">
                <tr>
                    <td class="nameLabel">拍摄地点：</td>
                    <td><div id="shootLocation"></div></td>
                </tr>
                <tr>
                    <td class="nameLabel">拍摄时间：</td>
                    <td><input type="text" id="shootDate"  onFocus="WdatePicker({isShowClear:true,readOnly:true})"></td>
                </tr>
            </table>
            <div style="text-align:center;margin-top: 20px;">
                <input type="button" id="setShootInfoBtn" style="margin:10px" value="确定">
                <input type="button" id="cancelSetShootInfoBtn" style="margin:10px" value="取消">
            </div>
         </div>
    </div>
    
    
    <div id="subPlanWindow" style="display: none;">
        <div style="overflow: hidden">
            <div id="s_planDivForm" style="height:380px;">
                <div>新建计划</div>
                <div>
                    <form id="s_planForm" action="/shootPlanManager/saveShootPlan">
                        <input type="hidden" id="s_viewIdsInput" name="viewIds">
                        <input type="hidden" id="parentPlanIdInput" name="parentPlanId">
                        <input id="s_minPlanTime" type="hidden">
                        <input id="s_maxPlanTime" type="hidden">
                        <table class="register-table">
                            <tr>
                                <td class="nameLabel">计划名称：</td>
                                <td><input type="text" name="planName" id="s_planNameInput" class="text-input" style="width:300px;height:30px;" /></td>
                            </tr>
                            <tr>
                                <td class="nameLabel">计划开始时间：</td>
                                <td>
                                    <input type="text" id='s_planStartTime' 
                                    name='planStartTime' value="<%=currDate %>" 
                                    style='float: left; margin-top: 3px;' 
                                    onFocus="WdatePicker({isShowClear:false,readOnly:true,minDate:'#F{$dp.$D(\'s_minPlanTime\')}',maxDate:'#F{$dp.$D(\'s_maxPlanTime\')}'})">
                                </td>
                            </tr>
                            <tr>
                                <td class="nameLabel">计划结束时间：</td>
                                <td>
                                    <input type="text" id='s_planEndTime' 
                                    name="planEndTime" value="<%=currDate %>" 
                                    style='float: left; margin-top: 3px;'
                                    onFocus="WdatePicker({isShowClear:false,readOnly:true,minDate:'#F{$dp.$D(\'s_minPlanTime\')}',maxDate:'#F{$dp.$D(\'s_maxPlanTime\')}'})">
                                </td>
                            </tr>
                            <tr>
                                <td class="nameLabel">分组：</td>
                                <td>
                                    <div id="s_planGroup" /></div> 
                                    <input type="hidden" name="groupId" id="s_groupIdValue">
                                </td>
                            </tr>
                        </table>
                    </form>
                    <div style="margin-bottom:10px;text-align:center;">
                        <input type="button" value="确定" id="s_savePlanButton" /> 
                        <input type="button" value="关闭" class="s_planCancelButton" />
                    </div>
                </div>
            </div>
            <div id="s_planDivList" style="margin-top:10px;">
                <div>选择已有计划</div>
                <div>
                    <div id="s_existPlanGrid"></div>
                    <div style="margin-bottom:10px;text-align:center;">
                        <input type="button" value="确定" id="s_addToPlanButton" /> 
                        <input type="button" value="关闭" class="s_planCancelButton" />
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div id="pplanWindow" style="display: none;">
	    <div>新建计划</div>
	    <div>
	        <form id="pplanForm" action="/shootPlanManager/saveShootPlan">
                <input type="hidden" name="planId" id="pplanId" />
                <input type="hidden" name="parentPlanId" id="p_pplanId" />
                <input id="s_minPlanTime" type="hidden">
                <input id="s_maxPlanTime" type="hidden">
	            <table>
	                <tr>
	                    <td class="nameLabel"><p>计划名称：</p></td>
	                    <td><input type="text" name="planName" id="pplanNameInput" class="text-input" style="width:235px;height:30px;" /></td>
	                </tr>
	                <tr>
	                    <td class="nameLabel"><p>计划开始时间：</p></td>
	                    <td>
	                        <input type="text" id='pplanStartTime' 
	                        name='planStartTime' value="<%=currDate %>" 
	                        style='float: left; margin-top: 3px;' 
	                        onFocus="WdatePicker({isShowClear:false,readOnly:true,minDate:'#F{$dp.$D(\'s_minPlanTime\')}',maxDate:'#F{$dp.$D(\'s_maxPlanTime\')}'})">
	                    </td>
	                </tr>
	                <tr>
	                    <td class="nameLabel"><p>计划结束时间：</p></td>
	                    <td>
	                        <input type="text" id='pplanEndTime' 
	                        name='planEndTime' value="<%=currDate %>" 
	                        style='float: left; margin-top: 3px;' 
	                        onFocus="WdatePicker({isShowClear:false,readOnly:true,minDate:'#F{$dp.$D(\'s_minPlanTime\')}',maxDate:'#F{$dp.$D(\'s_maxPlanTime\')}'})">
	                    </td>
	                </tr>
	                <tr>
	                    <td class="nameLabel"><p>分组：</p></td>
	                    <td>
	                        <div id="pplanGroup" /></div> 
	                        <input type="hidden" name="groupId" id="pgroupIdValue">
	                    </td>
	                </tr>
	            </table>
	        </form>
	        
	        <div style="text-align:center;margin-top: 41px;">
	            <input type="button" value="确定" id="savepPlanButton" style='margin-right:38px;' /> 
	            
	            <input type="button" value="关闭" class="pPlanCancelButton" />
	        </div>
	    </div>
    </div>
    
    <div id="noticeWindow" style="display: none;">
        <div>
            <div id="noticeDivForm" style="overflow: hidden">
                <div>新建通告单</div>
                <div>
                    <form id="noticeForm" action="/notice/noticeSave">
                         <table>
                             <tr>
                                 <td class="nameLabel">名称：</td>
                                 <td><input type="text" style="width:300px;height:30px;" name="noticeName" id="noticeNameInput" /></td>
                             </tr>
                             <tr>
                                 <td class="nameLabel">时间：</td>
                                 <td><input type="text" id="noticeTime" onFocus="WdatePicker({isShowClear:false,readOnly:true, onpicked:autoGetNoticeName})"></td>
                             </tr>
                             <tr>
                                 <td class="nameLabel">分组：</td>
                                 <td><div id="group"></div></td>
                             </tr>
                             <tr>
                                 <td colspan="2"><div id="addNoticeError" style="text-align:left;padding-left:97px;width:435px;color:red;color:red; line-height:17px;"></div></td>
                             </tr>
                         </table>
                         <div style="margin-bottom:10px;text-align:center;margin-top:40px;">
                              <input type="button" value="确定" style="margin-bottom: 5px;" id="saveNoticeButton" />
                              <input type="button" value="关闭" class="noticeCancelButton" />
                         </div>
                     </form>
                 </div>
            </div>
            <div id="noticeDivList" style="margin-top:10px;">
               <div>选择已有通告单</div>
               <div>
                   <div id="existNoticeGrid"></div>
                   <div style="margin-bottom:10px;text-align:center;">
                       <input type="button" value="确定" id="addToNoticeButton" /> 
                       <input type="button" value="关闭" class="noticeCancelButton" />
                   </div>
               </div>
            </div>
        </div>
    </div>
  </body>
</html>
