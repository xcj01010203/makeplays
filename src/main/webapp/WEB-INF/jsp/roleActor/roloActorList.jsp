<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page isELIgnored="false"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Date currentTime = new java.util.Date();//得到当前系统时间
	String str_date1 = formatter.format(currentTime); //将日期时间格式化 
	
	Object isRoleReadonly = false;     //角色表是否只读
	Object obj = session.getAttribute("userAuthMap");
	
	if(obj!=null){
		java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
		if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.ROLE_VIEW)) {
			if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.ROLE_VIEW) == 1){
				isRoleReadonly = true;
			}
		}
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=request.getContextPath()%>/js/report/base.css" rel="stylesheet" />
<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script> --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/viewList.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/report/role_viewlist.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/report/grade.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/echarts/echarts.js"></script>
<style type="text/css">
	.font_v_m{height:26px;line-height:26px;overflow:hidden;}
	.ft_m{text-align: center;}
		#jqxgridAddress td {
	border: 1px solid #ccc;
  	width: 265px;
  	border-top:0;
	border-left: 0;
	height: 30px;
	line-height: 30px;
	font-size: 13px;
  }
  .lmaCount{ border:1px solid #ccc;/* background-color: #fde5bf; */height: 30px;line-height: 30px;padding-left: 13px; border-top: 0;  width: 99.3%;}
  thead{background:#ebebeb}
  /* 角色场景表 */
*{ padding:0; margin:0; list-style:none; outline:none;}
.Popups_box h4{ width:80px; float:left; text-align:right;}
.Popups_box{ width:740px; height:600px; font-size: 14px;  line-height: 25px; }
.Popups_box ul{ width:350px; height:25px; padding:10px; float:left; margin:0 auto; border-bottom:1px dashed #CCC;}
.Popups_box li{ float:left; padding-left:10px; }
.Popups_box li input{ margin-right:5px;}
.Popups_box .happy li input{}
.Popups_box .xiala select{ width:100px; height:30px; border:1px solid #C60;}
.Popups_box .shuru input{ width:200px; height:30px; border:1px solid #C60;}
.Popups_box textarea{ width:480px; height:300px; border:1px solid #C60;}
.jqx-fill-state-hover-ui-lightness{ background: #fdf5ce; border-color:#fbcb09; color: #c77405; }
.clear{clear:both;white-space:nowrap;}
.tianjia ul{width:350px; height:25px; padding:10px; float:left; margin:0 auto; border-bottom:1px dashed #CCC; line-height:25px;}
.rowStatusColor{ font-size:12px; line-height:20px;height:100%;width:100%;overflow: hidden; 
text-overflow: ellipsis;  text-align: left; padding-left: 4px; padding-top: 4px;}  
#lmaTwoButton input{
width: 26px;
  height: 18px;
  margin-top: 4px;
  color: #f60;
  border: 1px solid #ccc;
  background-color: #fff;
  border-radius: 3px;
  line-height: 18px;
  background-position: -29px -16px;
  background-image: url(../images/ui-icons_ef8c08_256x240.png);
}
#lmabutton input{width: 26px;height: 18px;margin-top: 4px;color: #f60;border: 1px solid #ccc;background-color: #fff;border-radius: 3px;line-height: 18px;
  background-position: -92px -16px;background-image: url(../images/ui-icons_ef8c08_256x240.png);}
  #gogo{width:20px; height:20px;background: url("../images/roleform/search.png") no-repeat;}
  #gogo:hover{background:url(../images/roleform/search1.png) no-repeat;}
  #addBtn{width:20px; height:20px;background: url("../images/tianjiachengyuan.png") no-repeat;}
  #addBtn:hover{background:url(../images/tianjiachengyuan1.png) no-repeat;}
  #mergeBtn{width:20px; height:20px;background: url("../images/roleform/tongyi_jsm.png") no-repeat;}
  #mergeBtn:hover{background:url(../images/roleform/tongyi_jsm1.png) no-repeat;}
  #setRoleTypeDiv{width:20px; height:20px;background: url("../images/roleform/js_lx.png") no-repeat;position: absolute;}
  #setRoleTypeDiv:hover{background:url(../images/roleform/js_lx1.png) no-repeat;}
  #rolecjb{width:20px; height:20px;background: url("../images/export.png") no-repeat;}/* roleform/js_cjb  */
  #rolecjb:hover{background:url(../images/export_hover.png) no-repeat;}
  #batchDelete{width:20px; height:20px;background: url("../images/delete_ioc.png") no-repeat;}/* roleform/js_cjb  */
  #batchDelete:hover{background:url(../images/delete1_ioc.png) no-repeat;}
  /* #resetMain{width:20px; height:20px;background: url("../images/return.png") no-repeat;}
  #resetMain:hover{background:url(../images/return1.png) no-repeat;} */
 /*  #imagetie{width:157px; height:28px;background: url("../images/tuli.png") no-repeat;position: absolute;} */
  
  .deltelumns{width:16px; height:16px;background: url("../images/delete_ioc.png") no-repeat;position: absolute;right:100px;background-size: 15px 15px;}
  .deltelumns:hover{background:url(../images/delete1_ioc.png) no-repeat;background-size: 16px 16px;}
  .qincolumns{width:16px; height:16px;background: url("../images/roleform/shezhi.png") no-repeat;position: absolute;right:70px;background-size: 15px 15px;}
  .qincolumns:hover{background:url(../images/roleform/shezhi1.png) no-repeat;background-size: 16px 16px;}
  .xicolumn{width:16px; height:16px;background: url("../images/roleform/xitong.png") no-repeat;position: absolute;right:40px;background-size: 15px 15px;}
  .xicolumn:hover{background:url(../images/roleform/xitong1.png) no-repeat;background-size: 16px 16px;}
  .rolecolumns{width:16px; height:16px;background: url("../images/roleform/js_cjb.png") no-repeat;position: absolute;right:10px;background-size: 15px 15px;}
  .rolecolumns:hover{background:url(../images/roleform/js_cjb1.png) no-repeat;background-size: 16px 16px;}
  .rolecene{width:306px;margin-top: -2px; margin-right: -11px; height:31px;background: url("../images/colorExample3.png") no-repeat;float: right;}
  .exportImg{width:31px; border:0px; height:16px;background: url(../images/export.png) no-repeat;float: none;}
  .exportImg:hover{background:url(../images/export_hover.png) no-repeat;}
  
  
  /*演员评价  */
  #actorEvaluate{width: 760px;height: 460px;}
  #actorEvaluate ul{list-style: none;padding-left: 0;margin: 0;}
  #actorEvaluate ul li{display: inline-block;}
  #actorEvaluate .content{padding: 20px;margin: 0;background-color: #f1f1f1;}
  #actorEvaluate .content label{width: 70px;display: inline-block;overflow: hidden;clear: left;text-align: right;text-overflow: ellipsis;white-space: nowrap;}
  #actorEvaluate .content>li{display: block;}
  #actorEvaluate .content>li + li{margin-top: 30px;}
  #actorEvaluate .content .grade-star{display: inline-block;width: 450px;height: 45px;}
  .content label{font-size: 14px;letter-spacing: 10px;}
.content .grade-star>li{height: 45px;width: 45px;background: url(../../../images/xing0.png) no-repeat center;position: relative;transition: background .2s ease-in;}
.content .grade-star>li.full-star{background-image: url(../../../images/xing1.png);}
.content .grade-star>li.half-star{background-image: url(../../../images/xing2.png);}
.content .grade-star>li .star-left, .content .grade-star>li .star-right{display: inline-block;width: 50%;height: 100%;cursor: pointer;}
.content .grade-star>li .star-info{position: absolute;padding: 2px 5px;border: 1px solid #ddd;background-color: #f7f3ea;white-space: nowrap;top: 100%;margin-top: 5px;display: none;}
.content .grade-star>li + li{margin-left: 30px;}
.content .grade-df{font-family: Grade;color: #f6af39;font-size: 70px;line-height: 1;}
.content>.py, .content>.yx{position: relative;padding-left: 70px;}
.content>.py>label, .content>.yx>label{position: absolute;left: 0;}
.content>.yx>label{line-height: 60px;}
.content .grade-impression li + li{position: relative;margin-left: 15px;}
.content .grade-impression li>span{display: inline-block;padding: 5px;background-color: #ede7e9;color: #6D6D6D;letter-spacing: 1px;transition: background-color ease-in .2s;}
.content .grade-impression li>input{position: absolute;cursor: pointer;z-index: 1;opacity: 0;filter:alpha(opacity=0)margin: 0;padding: 0;height: 100%;width: 100%;}
.content .grade-impression li:hover > span{background-color: #dedadb;}
.content .grade-impression li>input:checked + span{color: #fff;}
.content .grade-impression.best li>input:checked + span{background-color: #cd3333;}
.content .grade-impression.bad li>input:checked + span{background-color: #5e5e5e;}
.content .grade-impression li:first-child{height: 16px;width: 16px;margin-right: -10px;}
.content .grade-impression.best li:first-child{background-image: url(../../../images/hong.png);background-position: center;background-repeat: no-repeat;background-size: auto 16px;}
.content .grade-impression.bad li:first-child{
    background: url(../../../images/hei.png);
    background-position: center;
    background-repeat: no-repeat;
    background-size: auto 16px;
}
.content .hr{
    width: 615;
    background-color: #d8d8d8;
    background-origin:content-box;
    border:none;
    height: 1px;
    margin: 5px;
    padding: 0 20px;
}
.content .grade-py{
    height: 100px;
    resize: none;
    width: 98%;
    padding: 6px;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
    -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}
.content .grade-py:focus {
    border-color: #66afe9;
    outline: 0;
    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);
}
.text-input:HOVER {border:1px solid #409DFE;box-shadow:0 0 3px #409DFE;}
select {border-bottom:1px solid #cccccc;}
   
</style>
<!-- 场景角色表  begin -->
<script type="text/javascript">
Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
//配置echar路径
require.config({
    paths: {
        echarts: ctx+'/js/echarts'
    }
});
var isRoleReadonly=<%=isRoleReadonly %>;
var scrollHei;
//var scrollCheck=[];
var allRoleId=[];
var cellids;
//新表格对象
var grid;

var resultData;

//是否刷新统计
var summaryFlag=true;

var sourceView;
var gridColumns=new Array();
var viewContentRowNo;   //在场景内容面板上显示的场景在场景表格中的行号

//所有角色列表
var roleArray;
var showRoleArray;
var showRoleMap=new HashMap();
var roleMap = new HashMap();
//特约演员
var guestArray;
//群众演员
var massArray;
//主场景
var majorViewList;
//次场景
var minorViewList;
//lma 下载
var downLoadViewRoleId;
var filter={};

var columnData;//表格数据

var statisType = 1;//角色按集统计方式：1.按场，2.按页
var statisticRoleName;

var crewType = "${crewType }";

//$(document).ready(function () {
function tabReady(inde){
	//页面tab初始化
   	roleArray= [
				<c:forEach items="${roleSignList}" var="role">
  	            {
  	            	text: '${role.viewRoleName}',
  	            	value: '${role.viewRoleId}'
  	            },
  	        </c:forEach>
  	            ];
  	
   	sourceView =
          {
		        datatype: "json",
		        datafields: [
					{ name: 'viewId,',type: 'string' },
					{ name: 'seriesNo,',type: 'int' },
					{ name: 'viewNo,',type: 'string' },
			        { name: 'season',type: 'int' },//季节 seasonMap
			        { name: 'atmosphereId',type: 'int' },//气氛
			        { name: 'site',type: 'string' },//内外景
			        { name: 'viewType',type: 'int' },//文戏武戏
			        { name: 'shootLocation',type: 'string' },//拍摄地点
			        { name: 'majorView',type: 'string' },//主场景
			        { name: 'minorView',type: 'string' },//次场景
			        { name: 'thirdLevelView',type: 'string' },//三级场景
			        { name: 'mainContent',type: 'string' },//主要内容
			        { name: 'pageCount',type: 'string' },//页数
			        <c:forEach items="${roleSignList}" var="role">
			        { name: '${role.viewRoleId}',type: 'string' },//
			        </c:forEach>
			        { name: 'guestRoleList',type: 'string' },//特约演员
			        { name: 'massRoleList',type: 'string' },//群众演员
			        { name: 'propsList',type: 'string' },//普通道具
			        { name: 'rolePropsList',type: 'string' },//个人道具
			        { name: 'clothesName',type: 'string' },//服装
			        { name: 'makeupName',type: 'string' },//化妆
			        { name: 'shootDate',type: 'string' },//拍摄时间
			        { name: 'remark',type: 'string' },//备注
			        { name: 'shootStatus',type: 'string' },//拍摄状态
			        { name: 'advertName',type: 'string' }//商植
		                ],
		        type:'post',
		        //data:{seriesNo:1,viewNo:1},
		        beforeprocessing:function(data){
		        	//查询之后可执行的代码
		        	//全局变量赋值
		        	sourceView.totalrecords=data.result.total;
		        	resultData=data.result ;
		        },
		        root:'resultList',
		        processdata: function (data) {
							        	
		            //查询之前可执行的代码
		        },
	            url:'<%=request.getContextPath()%>/viewManager/loadViewList'
          };
          roleColumn = function (columnfield, value, columnproperties, rowdata) {
          	//操作列html
          	//var resultList = resultData.resultList;
          	var thisRowData = rowdata;
          	
          	var roleArray = thisRowData.roleList;
          	for(var i=0;i<roleArray.length;i++){
          		if(columnproperties.text==roleArray[i].viewRoleName){
          			if(roleArray[i].shortName==null  || roleArray[i].shortName.trim() == ""){
          				return "√";
          			}else{
          				return roleArray[i].shortName;
          			}
          		}
          	}
          	return "";
             
          }
          
          viewColumn = function(columnfield, value, columnproperties, rowdata){
				//var resultList = resultData.resultList;
				var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
          	return "<span style='cursor:pointer;color:#52b0cc;'>" + seriesNoAndViewNo + "</span>";
          }
          viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
                var viewNo =rowdata.viewNo;
                return "<span style='cursor:pointer;color:#52b0cc;' class='bold' onclick='showViewContent(\""+rowdata.viewId+"\",\""+rowdata.viewId+"\")'>" + viewNo + "</span>";
            }
          
          rendergridrows = function (params) {
          	//调用json返回的列表数据
              return params.data;
          }
          
          atmosphere=function(columnfield, value, columnproperties, rowdata){
          	//var resultList = resultData.resultList;
          	var atmosphere= rowdata.atmosphereName;
          	if(atmosphere==null){
          		atmosphere="";
          	}
          	//return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+atmosphere+"</div>";
          	return atmosphere;
          }
          
          seasonColumn=function(columnfield, value, columnproperties, rowdata){
          	//var resultList = resultData.resultList;
          	var seasonText= seasonMap.get(rowdata.season);
          	if(seasonText==null){
          		seasonText="";
          	}
          	return seasonText;
          	//return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+seasonText+"</div>";
          }
          
          siteColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.site){
          		text=rowdata.site;
          	}
          	return text;
          }
          majorViewColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.majorView){
          		text=rowdata.majorView;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          minorViewColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.minorView){
          		text=rowdata.minorView;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.thirdLevelView){
          		text=rowdata.thirdLevelView;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          mainContentColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.mainContent){
          		text=rowdata.mainContent;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          pageCountColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.pageCount){
          		text=rowdata.pageCount;
          	}
          	return text;
          }
          guestRoleListColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.guestRoleList){
          		text=rowdata.guestRoleList;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.massRoleList){
          		text=rowdata.massRoleList;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          propsListColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.propsList){
          		text=rowdata.propsList;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.clothesName){
          		text=rowdata.clothesName;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.makeupName){
          		text=rowdata.makeupName;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          shootDateColumn = function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.shootDate){
          		text=rowdata.shootDate;
          	}
          	return text;
          }
          remarkColumn= function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.remark){
          		text=rowdata.remark;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          }
          shootLocationColumn=function(columnfield, value, columnproperties, rowdata){
          	var text="";
          	if(rowdata.shootLocation){
          		text=rowdata.shootLocation;
          	}
          	return text;
          }
          
			typeColumn=function(columnfield, value, columnproperties, rowdata){
          	//var resultList = resultData.resultList;
          	var typeText = typeMap.get(rowdata.viewType);
          	if(typeText==null){
          		typeText="";
          	}
          	return typeText;
          	//return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+typeText+"</div>";
          }
          
          shootStatusColumn=function(columnfield, value, columnproperties, rowdata){
          	var shootStatusText = shootStatusMap.get(rowdata.shootStatus);
          	if(shootStatusText==null){
          		shootStatusText="";
          	}
          	return shootStatusText;
          	//return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+shootStatusText+"</div>";
          }
          advertNameColumn=function(columnfield, value, columnproperties, rowdata){
          	//var resultList = resultData.resultList;
          	var text="";
          	if(rowdata.advertName){
          		text=rowdata.advertName;
          	}
          	return "<span title='"+ text +"'>" + text + "<span>";
          	//return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+shootStatusText+"</div>";
          }
          
          gridColumns=[];
                         
            //电影类型的剧组不显示集次
            if (crewType == Constants.CrewType.movie) {
              gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn, width: '65px' ,pinned: true});
            } else {
              gridColumns.push({ text: '集-场', cellsrenderer: viewColumn, width: '65px' ,pinned: true});
            }
          
          gridColumns.push({ text: '季节', cellsrenderer: seasonColumn, width: '40px', style:'padding-left: 0px;text-align:center;' },
                { text: '气氛',cellsrenderer: atmosphere, width: '40px', style:'padding-left: 0px;text-align:center;'},
                { text: '内外景',cellsrenderer:siteColumn, width: '40px', style:'padding-left: 0px;text-align:center;'},
                { text: '文武戏', cellsrenderer: typeColumn , width: '40px', style:'padding-left: 0px;text-align:center;'},
                
                { text: '拍摄地点', cellsrenderer: shootLocationColumn, width: '120px' },
                { text: '主场景',cellsrenderer:majorViewColumn, width: '120px' },
                { text: '次场景',cellsrenderer:minorViewColumn , width: '120px' },
                { text: '三级场景',cellsrenderer: thirdLevelViewColumn, width: '120px' },
                { text: '主要内容',cellsrenderer: mainContentColumn, width: '120px' },
                { text: '页数',cellsrenderer: pageCountColumn, width: '40px'});
                 	//判断是否有角色过滤条件
                 	
                 	//暂时显示所有列
                 	if(false){
                 	//if(typeof(filter.roles)!="undefined" && filter.roles!=""){
                 		//此处做处理动态列
                         showRoleArray = filter.roles.split(",");
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
                     	for(var i =0;i<roleArray.length;i++){
                             gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '20px',isRoleColumn:true });
                         }
                     }
                 		gridColumns.push(
                    		{ text: '特约演员',cellsrenderer: guestRoleListColumn, width: '90px' },
                       	{ text: '群众演员',cellsrenderer: massRoleListColumn, width: '90px' },
                       	{ text: '道具',cellsrenderer: propsListColumn, width: '90px' },
                       	{ text: '服装',cellsrenderer: clothesNameColumn,width: '90px' },
                       	{ text: '化妆',cellsrenderer: makeupNameColumn, width: '90px' },
                         { text: '拍摄时间',cellsrenderer: shootDateColumn, width: '90px' },
                         { text: '备注',cellsrenderer: remarkColumn, width: '90px' },
                         { text: '商植', cellsrenderer: advertNameColumn, width: '120px' },
                         { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: '90px' }
                     );
          
	rendertoolbar=function (toolbar) {//生成表格头部查询条件

      $(":button").jqxButton({theme:theme});
	}          		
          /*------------------------------------------------新版表格加载开始-----------------------------------------------------*/
         <%-- grid = new SimpleGrid("jqxgrid","<%=request.getContextPath()%>/viewManager/loadViewList",
          		100,0,gridColumns,filter,"resultList",rendertoolbar);
          grid.loadTable(); --%>
          
          /*------------------------------------------------新版表格加载结束-----------------------------------------------------*/
//});
}
function downloadExcel(viewRoleId,idInde){
	//if(roleArray.length>50){
	showInfoMessage("文件生成中，请耐心等待！");
	//}
	/*  $.ajax({
         url: '/viewManager/exportExcel',
         data: {roles:viewRoleId},
         type: 'post',
         async: false,
         dataType: 'json',
         success: function(data) {
         	
         }
      }); */
	window.open('/viewManager/exportExcel?roles='+viewRoleId);
}
function downloadList(viewRoleId){
	window.open('/viewManager/exportExcel?roles='+viewRoleId);
	/* for(var i=0;i<urls.length;i++){
		 window.open(urls[i]);
	} */
}
function resizeViewGird() {
  var windowHeight = document.body.clientHeight;
  $(".cc").css("height", windowHeight -270);
}

function getColor(shootStatus){
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(shootStatus);
	return divColor;
}
//时娜修改表格调用
function aa(obje) {
	var b = document.getElementById("cc"+obje).scrollLeft;
	document.getElementById("hh"+obje).scrollLeft = b;
}
</script>
<!--  场景角色表  end  -->
<!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
 <script type="text/javascript">
var source;
var dataAdapter;
var mainFilter = {};
var clikcCount=0;//请假设置（是否添加或删除）
var viewNames=[];//角色名是否存在的条件
var mergeRoleIds="";
var actorIds="";
var actorName_pf;//评价
var viewRoleName_pf;
var evaluateId_pf;
var types_pf;
var viewRoleData;

 $(document).ready(function () {
	 topbarInnerText("拍摄管理&&角色表");
 	$(".text-input").blur(function(){
		$(this).removeClass("input_cur_1");
	});
	
	$(".text-input").focus(function(){
		$(this).addClass("input_cur_1");
	});
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
		});
		
		//初始化评价
		$('#actroEvaluateWindow').jqxWindow({theme:theme,  width: 780,
	        height: 430, autoOpen: false,isModal:true,
	        cancelButton: $('#actorEvaluateCencle'),
	        initContent: function () {
	        	$("#actorEvaluateSubmit").jqxButton({theme:theme,width:50});
	        	$("#actorEvaluateCencle").jqxButton({theme:theme,width:50});
	        }
		});
		$("#actorEvaluateSubmit").click(function(){
    		//添加或修改评论
    		  var spCodesTemp = "";
		      $('.content input[type=checkbox]:checked').each(function(i){
		    	  if(0==i){
			        spCodesTemp = $(this).attr("id");
			      }else{
			        spCodesTemp += (","+$(this).attr("id"));
			      }
		      });
		      var score=$(".grade-df").text();//.substring(0,$(".grade-df").text().length-1);
		      $.ajax({
		  		url:ctx+'/evaluateManager/addOrUpdateEvaluate',
		  	   	type:'post',
		  	   	data:{
		  	   		fromUserName:'${user.userName}',
		  	   		toUserName:actorName_pf,
		  	   		roleName:viewRoleName_pf,
		  	   		comment:$('.grade-py').val(),
		  	   		score:score,
		  	   		tagIds:spCodesTemp,
		  	   		evaluateId:evaluateId_pf,
		  	   		types:types_pf
		  	   	}, 
		  	   	async: false,
		  	   	dataType: 'json',
		  	   	success:function(data){
		  	  	  $('#actroEvaluateWindow').jqxWindow("close");
			  	   	 if(data){
		        		showSuccessMessage("评价成功！");
			  	   	 }else{
			  	   	   showErrorMessage("评价失败，请联系管理员！"); 
			  	   	 }
		  	    } 
		  	 });
    	});
    
    
    source =
    {
        datatype: "json",
        datafields: [
			{ name: 'viewRoleId',type: 'string' },
	        { name: 'viewRoleName',type: 'string' },
	        { name: 'shortName',type: 'string' },
	        { name: 'actorName',type: 'string' },
	        { name: 'crewAmountByview',type: 'number' },
	        { name: 'crewAmountByPage',type: 'number' },
	        { name: 'enterDate',type: 'string' },
	        { name: 'leaveDate',type: 'string' },
	        { name: 'viewRoleType',type: 'string' },
	        { name: 'crewId',type: 'string' },
	        { name: 'actorId',type: 'string' },
	        { name: 'leaveDays',type: 'string' },
	        { name: 'daysCount',type: 'string' },
	        { name: 'count',type: 'number' },
	        { name: 'nocount',type: 'nubmer' }
	      
                ],
        type:'post',
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	viewRoleData = data.result;
        	source.totalrecords=data.result.total;
        },
        root:'resultList',
        processdata: function (data) {
            //查询之前可执行的代码
        },
     
        url:'<%=request.getContextPath()%>/roleActorManager/getRoloActorList'
    };
    
    
  	 
    dataAdapter = new $.jqx.dataAdapter(source,{
    	//autoBind: true,
        downloadComplete: function (data) {
        	allRoleId = [];
        	columnData = data.result.resultList;
            if(columnData != undefined){
            	for(var i=0;i<columnData.length;i++){
            		allRoleId.push(columnData[i].viewRoleId)
                }
            }
        }
    });
    
    loadgrid(dataAdapter);
    
         $("#popupWindow").jqxWindow({
             width: 350,   resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), //modalOpacity: 0.01           
         });
        
         $("#Cancel").jqxButton({ theme: theme });
         $("#clearSearch").jqxButton({ theme: theme });
         $("#Save").jqxButton({ theme: theme });
         $("#goReset").jqxButton({ theme: theme });
         $("#goSave").jqxButton({ theme: theme });
         
         //表单校验器
         $('#popupWindow').jqxValidator({
        	 hintType: 'label',
          	 animationDuration: 0,
             rules: [
                 { input: '#viewRoleName', message: '角色名称不能为空', action: 'blur', rule:function(input, commit){
                   	$("#rolenameDiv").hide();
                   	if(input.val().length==0){ 
                        return false; 
                    } 
                    return true;
                 } },
                 { input: '#viewRoleType', message: '演员类型不能为空', action: 'keyup,blur', rule: function(input, commit){
                  	if(input.val().length==0){ 
                          return false; 
                      } 
                      return true;
                  } },
             ]
         });
         // update the edited row when the user clicks the 'Save' button.
         $("#Save").on('click', function() {
        		    delay_till_last('woshiid', function() {//注意 id 是唯一的
        		        //响应事件
        		    }, 300);
        		
         });
         //条件查询
         $("#checkGogo").jqxWindow({ theme: theme,
        	 height:280,width:380, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("goReset"), /* position: { x: '8%', y:  '12%' } */           
         });
         //请假设置窗体关闭执行事件
         $('#attendanceWindow').on('close', function (event) {
        	 if(clikcCount!=0){
        		 //$('#jqxgridlma').jqxGrid('clearselection');
      			 //$("#jqxgridlma").jqxGrid("updatebounddata", 'cells');
      			 //source.data = mainFilter;
	             loadgrid(dataAdapter);
            	 <%-- window.location='<%=request.getContextPath()%>/roleActorManager/roloActorList'; --%>
        	 }
          });
         
         
         
         $("input[name=statisticsType]").on("click",function(){
        	 statisType = $(this).val();
        	 diversityStatistics(statisticRoleName);
         });

});
 
 function loadgrid(dataAdapter){
	 $("#jqxgridlma").remove();
	 $("#jqxgrid-mian-position").before("<div id='jqxgridlma'></div>");
	 
	 var rendergridrows = function (params) {
	    	//调用json返回的列表数据
	        return params.data;
	    }
	    //lma 初始化日期控件
	/*     $("#enterDate").jqxDateTimeInput({formatString: "yyyy-MM-dd", width: '200px', height: '25px',culture: 'ch-CN'});
	    $("#leaveDate").jqxDateTimeInput({formatString: "yyyy-MM-dd", width: '200px', height: '25px',culture: 'ch-CN'}); */
	    var roleTypeColumn = function(row, columnfield, value, defaulthtml, columnproperties, rowdata) {
	        var html = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" + viewRoleTypeMap.get(rowdata.viewRoleType) + "</div>"
	        return html;
	    };
 
	 $("#jqxgridlma").jqxGrid(
			    {   
			        //altrows: true,
			        width: '100%',
			        height: '99%',
			        source: dataAdapter,
			        showtoolbar: true,
			        selectionmode: 'checkbox',
			        //sortable: true,
			        
			        //enablehover: true,
			        //editable: true,
			        localization:localizationobj,//表格文字设置
			        rendertoolbar: function (toolbar) {
			          //href=\"roleActorManager/roloGogo\"
			            var container = $("<div style='margin: 7px;'></div>");
			            var gogo = $("<button style='margin-left: 15px;border: 0px;'   id='gogo' ></button>");//.jqxButton({theme:theme, width: '29', height: '29'	 });
			            debugger;
			           if(!isRoleReadonly){
			        	   var setRoleType = "<div id='setRoleTypeDiv'   style='border: 0px;display:block; float:left; top:7px; margin-left: 155px;'>";
				  			 setRoleType += "<div style='border: none;' id='jqxTreeDrop'><ul>";
				  			 setRoleType += " <li item-selected='true' value='1'>主要演员</li>";
				  			 setRoleType += " <li item-expanded='true' value='2'>特约演员</li>";
				  			 setRoleType += " <li item-expanded='true' value='3'>群众演员</li>";
				  			 setRoleType += " <li item-expanded='true' value='4'>待定角色</li>";
				  			 setRoleType += "</ul></div>";
			               	setRoleType += "</div>";
			              var button = $("<button  style='border: 0px;margin-left: 15px;' id='addBtn'></button>");
			              var merge = $("<button style='border: 0px;margin-left: 15px;' id='mergeBtn' ></button>");
			              var actroCene = $("<button id='rolecjb' class='rolecjb' style='border: 0px;margin-left: 15px; '></button>");
			             // var resetMain=$("<button  id='resetMain' style='border: 0px;margin-left: 185px; '></button>");
			             
			             <c:if test="${crewUser.roleId == '2' }">
			             /* 批量删除 */
			             var batchDelete = $("<button id='batchDelete' class='batchDelete' style='border: 0px;margin-left: 50px; '></button>");
			             </c:if>
			             
			             container.append(gogo);
			              container.append(button);
			              container.append(merge);
			              container.append(setRoleType);
			             // container.append(resetMain);
			             container.append(actroCene);
			             
			             <c:if test="${crewUser.roleId == '2' }">
			             /* 批量删除 */
			             container.append(batchDelete);
			             </c:if>
			           
			           }else{
			              container.append(gogo);
			           }
			            toolbar.append(container);
			           	$("#toolbarjqxgridlma").css("background-color","#ffffff");
			            
			            if(!isRoleReadonly){
			            	 $("#setRoleTypeDiv").jqxDropDownButton({theme:theme,height: 20, width: 20});
			            	 $("#jqxTreeDrop").jqxTree({theme:theme, width: 100});
				            
			            	 $('#jqxTreeDrop').click('select', function (event) {
			                     var item = event.target.innerHTML;
			                     if(item=='主要演员'){
			                     	item=1;
			                     }else if(item=='特约演员'){
			                     	item=2;
			                     }else if(item=='群众演员'){
			                     	item=3;
			                     }else if(item='待定角色'){
			                      	item=4;
			                     }
			                     
			                     var selectedRowIndexs =  $("#jqxgridlma").jqxGrid("getselectedrowindexes");
			                     var viewRoleIds = "";
			                     for (var i = 0; i < selectedRowIndexs.length; i++) {
			                         var viewRoleId = viewRoleData.resultList[selectedRowIndexs[i]].viewRoleId;
			                         viewRoleIds += viewRoleId + ",";
			                     }
			                     viewRoleIds = viewRoleIds.substring(0, viewRoleIds.length -1);
			                     
			                   if(viewRoleIds.length >0){
			                 	  $.ajax({
			                           url: '/roleActorManager/specifiedRoleType',
			                           data: {viewRoleIds: viewRoleIds, viewRoleType: item},
			                           type: 'post',
			                           async: false,
			                           dataType: 'json',
			                           success: function(data) {
			                            if (data.success) {
			                            	showSuccessMessage(data.message);
			                            	scrollCheck=[];
			                                //$("#jqxgridlma").jqxGrid("clearselection");
			                                //$("#jqxgridlma").jqxGrid("updatebounddata", "cells");
			                                //source.data = mainFilter;
	             							loadgrid(dataAdapter);
			                                //$("#setRoleTypeDiv").jqxDropDownButton("close");
			                            } else {
			                            	showErrorMessage(data.message);
			                            }
			                           }
			                        }); 
			                   }else{
			                 	  showInfoMessage("请选择演员角色！");
			                   }
			                 });
			            	 
			            	 actroCene.on("click",function(){
				            	 //window.open(ctx+'/roleManager/exportExcel?roleIds='+scrollCheck.toString());
				            	 var selectedRowIndexs =  $("#jqxgridlma").jqxGrid("getselectedrowindexes");
			                     var viewRoleIds = "";
			                     for (var i = 0; i < selectedRowIndexs.length; i++) {
			                         var viewRoleId = viewRoleData.resultList[selectedRowIndexs[i]].viewRoleId;
			                         viewRoleIds += viewRoleId + ",";
			                     }
			                     viewRoleIds = viewRoleIds.substring(0, viewRoleIds.length -1);
				            	 if(viewRoleIds.length < 1){
				            		 showInfoMessage("请选择角色！");
				            		return; 
				            	 }
				            	 $("#postForm").html('');//防止元素重复
					             $("#postForm").append('<input type="hidden" name="roleIds" value="'+viewRoleIds+'"/>');
					             $("#postForm").attr("target","newWin");
					             $("#postForm").attr("action",ctx+"/roleManager/exportExcel");
					             window.open("下载","newWin","");//newWin 是上面form的target
					             $("#postForm").submit();
				            });
			            	 merge.on("click",function(){
			            		 var selectedRowIndexs =  $("#jqxgridlma").jqxGrid("getselectedrowindexes");
			                     var viewRoleIds = "";
			                     viewNames=[]; 
			                 	 mergeRoleIds ='';
			                 	 actorIds ='';
			                 	 var actorIdArr=[];
			                     for (var i = 0; i < selectedRowIndexs.length; i++) {
			                         var viewRoleId = viewRoleData.resultList[selectedRowIndexs[i]].viewRoleId;
			                         viewRoleIds += viewRoleId+",";
			                         viewNames.push(viewRoleData.resultList[selectedRowIndexs[i]].viewRoleName);
			                         actorIdArr.push(viewRoleData.resultList[selectedRowIndexs[i]].actorId+",");
			                         
			                     }
			                     viewRoleIds = viewRoleIds.substring(0, viewRoleIds.length -1);
			                     mergeRoleIds=viewRoleIds;
			                     actorIds=actorIdArr.join("");
			       	       		if(actorIdArr.length<2){
			       	       			showInfoMessage("请选择要合并的多个角色");
			       	       			return;
			       	       		}
			     	   	       
			     	   	       $("#actorName").removeClass('jqx-validator-error-element').next('label').remove();
			     	   			    actorIds=actorIds.substring(0,actorIds.length-1);
			     	   			    //mergeRoleIds=mergeRoleIds.substring(0,mergeRoleIds.length-1);
			                 		$("#rolenameDiv").hide();
			                    	var offset = $("#jqxgridlma").offset();
			                         $("#popupWindow").jqxWindow({ theme: theme,height:400,width:380,isModal: true,});
			                         $(".tite").html("统一角色名称");
			     	               	 $("#viewRoleName").val("");
			                         $("#shortName").val("");
			                         $("#actorName").val("");
			                         $("#enterDate").val("");
			                         $("#leaveDate").val("");
			                         $("#viewRoleType").val("");
			                         $("#viewRoleId").val("");
			                         $("#crewId").val("");
			                         $("#actorId").val("");
			                         $("#mergeFlag").val("1");
			                         $("#popupWindow").jqxWindow('open');
			                 });
			                 
			                 button.click(function (event) {
			                 	mergeRoleIds='';//清空
			                 	actorIds='';
			                 	viewNames=[];
			                 	$("#rolenameDiv").hide();
			                 	 var offset = $("#jqxgridlma").offset();
			                 	 $("#popupWindow").jqxWindow({ theme: theme,height:400,width:380,isModal: true,});
			                 	 $(".tite").html("创建角色");
			                 	 $("#viewRoleName").val("");
			                      $("#shortName").val("");
			                      $("#viewRoleType").val("");
			                      $("#actorName").val("");
			                      $("#actorName").removeClass('jqx-validator-error-element').next('label').remove();
			                      viewNames[0]='0';
			                      
			                       		$("#enterDate").val("");
			                             $("#leaveDate").val("");
			                             
			                      $("#viewRoleId").val("");
			                      $("#crewId").val("");
			                      $("#actorId").val("");
			                      $("#mergeFlag").val("0");
			                      $("#popupWindow").jqxWindow('open');
			                 });
			            	 
				            $("#addBtn").jqxTooltip({ content: '创建角色', position: 'bottom', });
				            $("#mergeBtn").jqxTooltip({ content: '统一角色名称', position: 'bottom', });
				            $("#rolecjb").jqxTooltip({ content: '导出角色场景表', position: 'bottom', });
				            $("#setRoleTypeDiv").jqxTooltip({ content: '设置演员类型', position: 'bottom',});
			            }
				        $("#gogo").jqxTooltip({ content: '检索', position: 'bottom', });
				        
				        <c:if test="${crewUser.roleId == '2' }">
				        /* 批量删除 */
				        $("#batchDelete").jqxTooltip({ content: '批量删除', position: 'bottom', });
				        $("#batchDelete").click(function(){
				        	 var selectedRowIndexs =  $("#jqxgridlma").jqxGrid("getselectedrowindexes");
				        	 var viewRoleIds = "";
				        	 var roleActorIds = "";
		                    
		                     for (var i = 0; i < selectedRowIndexs.length; i++) {
		                         var viewRole = viewRoleData.resultList[selectedRowIndexs[i]];
		                         if(viewRole.crewAmountByview == 0 || viewRole.viewRoleType == 4){
		                        	 viewRoleIds += viewRole.viewRoleId + ",";
		                        	 if(viewRole.actorId!=null)
		                        	 	roleActorIds += viewRole.actorId;
		                         }else{
		                        	 showErrorMessage("请选择戏量为0或待定角色！");
		                        	 return;
		                         }
		                         //console.log(viewRole );
		                         
		                      }
		                     popupPromptBox("提示","确定删除吗?",function(){
		                    	 $.ajax({
				                    	url:ctx+"/roleActorManager/deltecolumns",
				                        dataType:'text',
				                        data:{viewRoleId:viewRoleIds,actorId:roleActorIds},
				                        type:'post',
				                        success:function(){
				                        	source.data = mainFilter;
						   	                loadgrid(dataAdapter);
				                        }
				                  });
		                     });
		                     
				        });
				        </c:if>
				        
			           // $("#resetMain").jqxTooltip({ content: '返回', position: 'bottom', /* autoHide: false, name: 'movieTooltip' */});
			           // $(".rolecolumns").jqxTooltip({ content: '角色场景表', position: 'bottom', });
			           // $(".xicolumn").jqxTooltip({ content: '戏量统计', position: 'bottom', });
			           // $(".qincolumns").jqxTooltip({ content: '请假设置', position: 'bottom', });
			           // $(".rolenames").jqxTooltip({ content: '修改角色', position: 'bottom', });
			               $("#jqxgridlma").on('rowclick', function (event) {
			            	   var args = event.args;
			            	    // row's bound index
			            	    var boundIndex = args.rowindex;
			            	   var ids=args.row.bounddata.viewRoleId+"check";
			            	   // if(cellids!=ids){
			            	    $("#"+cellids).find("div").css('background-color','#ffffff');
			            	    //}
			            	   // $("#"+ids).find("div").css('background-color','#d1d1d1');#e8e8e8
			            	    $("#"+ids).parent().parent().parent().find("div").css('background-color','#d1d1d1');
			            	   // cellids=ids;
			            	    cellids=$("#"+ids).parent().parent().parent().attr("id");
			      		   });
			           
			            gogo.click(function (event) {
			            	 $("#checkGogo").jqxWindow('open');
			          });
			            
			            /* $(window).resize(function(){
			            	for(var j=0;j <scrollCheck.length;j++){
			        			$("#"+scrollCheck[j]+"check").prop("checked",'true')
			        		}
			         	}); */
			         	/* sortStyle 0:无序；1：升序；2：降序 */
			         	$(".name-sort").click(function(){
			         		var _this = $(this).children("span");
			         		if($.trim(_this.attr("ifsort")) == "0"){
			         			_this.attr("ifsort","1");
			         			mainFilter.sortStyle = 1;
			         			source.data = mainFilter;
			         			$("#jqxgridlma").jqxGrid("updatebounddata", "cells");
			         		} else if(_this.hasClass("jqx-icon-arrow-up")){  
			         			_this.removeClass().addClass("jqx-icon-arrow-down").attr("ifsort","1");//降序排序
			         			mainFilter.sortStyle = 2;
			         			source.data = mainFilter;
			   	                //loadgrid(dataAdapter);
			         			$("#jqxgridlma").jqxGrid("updatebounddata", "cells");
			         		}else if(_this.hasClass("jqx-icon-arrow-down")){
			         			_this.removeClass().attr("ifsort","0");//默认排序
			         			mainFilter.sortStyle = 0;
			         			source.data = mainFilter;
			         			$("#jqxgridlma").jqxGrid("updatebounddata", "cells");
			         		}else{
			         			_this.removeClass().addClass("jqx-icon-arrow-up").attr("ifsort","1");//升序排序
			         			mainFilter.sortStyle = 1;
			         			source.data = mainFilter;
			         			$("#jqxgridlma").jqxGrid("updatebounddata", "cells");
			         		}
			         	}).hover(function(){
			         		if($.trim($(this).children("span").attr("ifsort")) == "0")
			         			$(this).children("span").removeClass().addClass("jqx-icon-arrow-up");
			         	},function(){
			         		if($.trim($(this).children("span").attr("ifsort")) == "0")
			         			$(this).children("span").removeClass();
			         	});
			            
			        },
			        cellhover: function (e) {//鼠标悬浮事件
			        	var _this = $(e);
			        	var ids;  //戏量统计
			        	if(_this.find("span[id^='link_span']").attr("id")){
			        		ids= _this.find("span[id^='link_span']").attr("id");
			        	}else{
			        		ids= _this.siblings().find("span[id^='link_span']").attr("id");
			        	}
			            
			            var actorid  ;
			            if(_this.find("div[id^='evaluate']").attr("id")){
			            	actorid = _this.find("div[id^='evaluate']").attr("id");
			            }else{
			            	actorid = _this.siblings().find("div[id^='evaluate']").attr("id");	
			            }
			            /*$("#contenttablejqxgridlma").find("div").css('background-color','#ffffff');
			            var inputId= $(cellhtmlElement).find("input").attr("id");//戏量统计
			            $("#"+inputId).parent().parent().nextAll().css('background-color','#e8e8e8'); */
			           if(ids!=undefined){
			          	  $('.lmaclass').hide();
			          	 }
			            $("#"+ids).show();
			            $("#"+actorid).show();
			            
			        	
			     	   _this.siblings().addClass("jqx-fill-state-hover").parent()
			     	   .siblings().children().removeClass("jqx-fill-state-hover");
			            
			       },
			        columns: [//<div style="padding-top:6px;margin-left: 3px;z-index:300;position:relative"><input id="roleNamecheckeds"  onclick="checkedNames(this);"  type="checkbox"/></div>
			                 /*  { text: '<div style="padding-top:3px;margin-left: 2px;z-index:300;position:relative"><input id="roleNamecheckeds"  onclick="checkedNames(this);"  type="checkbox"/></div>', datafield: 'loanId',  width: '2%',
								  cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
									  allRoleId.push(rowdata.viewRoleId);
									  var html = "<div   style='padding-top:6px;margin-left: 6px'>";
									  html += "<input type='checkbox' class='boxSelect' name='roleNamechecked' onclick='boxSelect(this)'  checkedId='"+rowdata.viewRoleId+"' id='"+rowdata.viewRoleId+"check' checkedActorId='"+rowdata.actorId+"' checkedName='"+rowdata.viewRoleName+"'/></div>";
									  $("#roleNamecheckeds").attr("indexs",0); 
									  return html;
								  }  
							  }, */
							//{ text: '', datafield: '',threestatecheckbox: false,  columntype: 'checkbox',  width: '2%'},
							{ text: '<div class="name-sort" style="width:100%;height:100%;">角色名称<span class="" ifsort="0" style="width:17px;display: inline;margin-left: 5px;">&nbsp;&nbsp;</span></div>', datafield: 'viewRoleName', width: '16%' ,align:'center',sortable: true,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
									   var html ="<div class='font_v_ms' style='margin-top:5px;padding-left: 5px;'><a href='#' class='rolenames' title='修改角色'  onclick='acrNameHref(\""+row+"\")'>"+rowdata.viewRoleName+"</a>";/* font-size:14px; */ 
				 						html+="<span class='lmaclass' id=\"link_span"+row+"\" style=\"float: right;display:none;\">";
				 						html+="<a class='rolecolumns' title='角色场景表' style='color: #1d93c1; ' href=\"javascript:roleView('"+rowdata.viewRoleName+"','"+rowdata.viewRoleId+"')\"></a><a class='xicolumn'title='戏量统计' style='color: #1d93c1; font-size:14px;' href=\"javascript:fenAddress('"+rowdata.viewRoleId+"','"+ rowdata.viewRoleName +"','"+rowdata.actorName+"')\"></a>";
				 						if(!isRoleReadonly){
					 						html+="<a class='qincolumns' style=' ' title='请假设置' href='javascript:askForLeave(\""+rowdata.actorId+"\",\""+ rowdata.actorName +"\")'></a>";
					 						if(rowdata.crewAmountByview==0 || rowdata.viewRoleType == 4){
						 						html+="<a class='deltelumns' style='color: #1d93c1; font-size:14px;' title='删除' href=\"javascript:deltecolumns('"+rowdata.viewRoleId+"','"+rowdata.actorId+"')\"></a>";
					 						}
				 						}
				 						html+="</span></div>";
				 						 return html;
				                 }	
							},
							{ text: '简称', datafield: 'shortName', width: '6%',cellsAlign: 'center',align:'center',sortable: false, },
			                { text: '演员类型', cellsrenderer: roleTypeColumn , width: '8%',align:'center',sortable: false,},
							{ text: '演员姓名', datafield: 'actorName', width: '13%',align:'center',sortable: false,
			                	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		
			                		var html = "<div class='font_v_ms' style='margin-top:5px;padding-left: 5px;'>"+value;
			                		html += "<div style='float:right;display:none;' class='actorevaluatef lmaclass' id='evaluate"+row+"'>";
			                		if(!isRoleReadonly && rowdata.actorName!=null){
			                			html+="<span style='padding-right: 10px;'><a href='javascript:actorEvaluate(\""+rowdata.viewRoleName+"\",\""+rowdata.actorName+"\");' actorName='"+value+"'>评</a></span>";                		}
			                		html+="</div>";
			                		html += "</div>";
			                		return html;
			                	}
							},
							{ text: '场', datafield: 'crewAmountByview', width: '6%',cellsAlign: 'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}
							},
							{ text: '页', datafield: 'crewAmountByPage', width: '6%',cellsAlign: 'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}					
							},
							{ text: '已完成场数', datafield: 'count', width: '5%',cellsAlign: 'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}					
							},
							{ text: '未完成场数', datafield: 'nocount', width: '5%',cellsAlign: 'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}					
							},
							{ text: '入组时间', datafield: 'enterDate', width: '10%',cellsAlign: 'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}					
							},
							{ text: '离组时间', datafield: 'leaveDate', width: '10%',cellsAlign:'right',align:'center',
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
			                		var html = "<div class='' style='margin-top:5px;float:right;padding-right: 6px;'>"+value+"</div>";
			                		return html;
			                	}					
							},
							{ text: '请假记录', datafield: 'attendance',align:'center',width:'13%', sortable: false,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
								   var html = " "    
								   if(rowdata.leaveDays!=null && rowdata.leaveDays!=''){
									  	html+="<div style='text-align: center;line-height: 25px;cursor: pointer;' onclick='askForLeave(\""+rowdata.actorId+"\",\""+ rowdata.actorName +"\")'><a type='button' style=' color:navy;text-decoration:none;'  id='"+rowdata.viewRoleId+"'";
									  	html+="aria-disabled='false' ";
										html+=" >请假："+rowdata.daysCount+"次/共："+rowdata.leaveDays+"天</a></div>";
								   }/* else{
										//html+=" display:none;' id='"+rowdata.viewRoleId+"' value='请假设置' ";
									} */
								   if(rowdata.viewRoleType!=1 && rowdata.viewRoleType!=2){
										return " ";
			                    	}
				                    return html;
			                 }
			             }
			        ],
			        ready:function(){
			        	//滚动绑定checkedbox 事件
			        	 scrollHei = $("#jqxScrollAreaDownverticalScrollBarjqxgridlma").css("height");
			        	 //dsd();
			        	 
			        	 $("div[id^='row']").mouseout(function(){
			      		   $(this).children().removeClass("jqx-fill-state-hover");
			      		 	$('.lmaclass').hide();
			      	    });
			        }
			    });
			         $("#jqxgridlma").bind("pagechanged", function (event) {
			         	//翻页时的事件绑定
			         });
 }
 
 //防止表单重复提交
 var _timer = {};
 function delay_till_last(id, fn, wait) {
     if (_timer[id]) {
         window.clearTimeout(_timer[id]);
         delete _timer[id];
     }
  
     return _timer[id] = window.setTimeout(function() {
    	 saveRoleFun();
         delete _timer[id];
     }, wait);
 }

function saveRoleFun(){
	$("#rolenameDiv").hide();
    //校验必填数据
    if (!$('#popupWindow').jqxValidator('validate')) {
         return;
    }
    //查询角色名是否存在
    var roname=$.trim($("#viewRoleName").val()); 
    var flag=true;
    viewNames;
    for(var i=0;i<viewNames.length;i++){
 	   if(roname==$.trim(viewNames[i])){
 		   flag=false;
 		   break;
 	   }
    }
    var leaveDate=  $("#leaveDate").val();
    var enterDate= $("#enterDate").val();
    if(enterDate > leaveDate){
 	   showErrorMessage("入组时间小于离组时间！")
 	   return;
    }
    
    	var actorname = $.trim($("#actorName").val());
    	if((leaveDate != ''|| enterDate != '') && (actorname == '') ){ //.clearit
    		
    		$("#actorName").removeClass('jqx-validator-error-element').next('label').remove();
    		$("#actorName").addClass('jqx-validator-error-element').after("<label class='jqx-validator-error-label' style='position: relative; left: 0px; width: 240px; top: 2px; display: block;'>已填写入组或离组时间，演员姓名不能为空</label>");
    		$("#actorName").unbind("keyup");
    		$("#actorName").on("keyup",function(){
    			$("#actorName").removeClass('jqx-validator-error-element').next('label').remove();
    		});
    		return;
    	}
    if(flag){
 	   $.ajax({
         	url:'<%=request.getContextPath()%>/roleActorManager/getcrewRoleName',
         	type:'post',
         	data:{
					name:$("#viewRoleName").val()                    		
         	},
         	dataType: 'json',
         	 success:function(data){
         		if(data.count>0){
         			$("#rolenameDiv").show();
         		}else{
             		  var actorName = $("#actorName").val();
       	               /* if ($.trim(actorName).length==0) {
       	               	 $("#actorId").val("");
       	               } */
       	        	   var leaveDate=  $("#leaveDate").val();
       	        	   var enterDate= $("#enterDate").val();
       	        	   var mergeFlag = $("#mergeFlag").val();
       	                 $.ajax({
       	                 	url:'<%=request.getContextPath()%>/roleActorManager/saveRole',
       	                 	type:'post',
       	                 	data:{
       	                 		viewRoleId:$("#viewRoleId").val(),
       	                 		actorId:$("#actorId").val(),
       	                 		enterDate:enterDate,
       	                 		leaveDate:leaveDate,
       	                 		viewRoleName:$("#viewRoleName").val(),
       	                 		shortName:$("#shortName").val(),
       	                 		viewRoleType: $("#viewRoleType").val(),
       	                 		actorName:$("#actorName").val(),
       	                 		mergeRoleIds:mergeRoleIds,
       	                 		actorIds:actorIds
       	                 	},
       	                 	dataType: 'json',
       	                 	 success:function(data){
       	                 		if(data.maseger=="true"){
       	                 			//scrollCheck=[];
       	                 			showSuccessMessage("操作成功");
       	                 			//$('#jqxgridlma').jqxGrid('clearselection');
       	                 			//$("#jqxgridlma").jqxGrid("updatebounddata", 'cells');
       	                 			//source.data = mainFilter;
			         				loadgrid(dataAdapter);
                       				$("#popupWindow").jqxWindow('close');
       	                 		}
       	        	        }
       	             });
         		}
	        	}
      });
    }else{
        var actorName = $("#actorName").val();
        /* if ($.trim(actorName).length==0) {
        	 $("#actorId").val("");
        } */
 	   var leaveDate=  $("#leaveDate").val();
 	   var enterDate= $("#enterDate").val();
 	   
 	   var mergeFlag = $("#mergeFlag").val();
          $.ajax({
          	url:'<%=request.getContextPath()%>/roleActorManager/saveRole',
          	type:'post',
          	data:{
          		viewRoleId:$("#viewRoleId").val(),
          		actorId:$("#actorId").val(),
          		enterDate:enterDate,
          		leaveDate:leaveDate,
          		viewRoleName:$("#viewRoleName").val(),
          		shortName:$("#shortName").val(),
          		viewRoleType: $("#viewRoleType").val(),
          		actorName:$("#actorName").val(),
          		mergeRoleIds:mergeRoleIds,
          		actorIds:actorIds
          	},
          	dataType: 'json',
          	 success:function(data){
          		if(data.maseger=="true"){
          			//scrollCheck=[];
          			showSuccessMessage("操作成功");	                 			
          			//source.data = mainFilter;
         			loadgrid(dataAdapter);
     				$("#popupWindow").jqxWindow('close');
          		}
 	        }
      });
    } 
}
 
 //删除戏量为空的角色
 function deltecolumns(roleId,actorId){
	 $.ajax({//查询角色是否已被引用
      	url:'<%=request.getContextPath()%>/roleActorManager/queryCrewRoleId',
      	type:'post',
      	data:{
      		viewRoleId:roleId,
      	},
      	async: false,
      	dataType: 'json',
      	success:function(data){
      		if(data){
      			popupPromptBox("提示","确定删除吗?",function(){
      				$.ajax({
      		         	url:'<%=request.getContextPath()%>/roleActorManager/deltecolumns',
      		         	type:'post',
      		         	data:{
      		         		viewRoleId:roleId,
      		         		actorId:actorId
      		         	},
      		         	async: false,
      		         	dataType: 'json',
      		         	 success:function(data){
      		         		if(data){
      		         			//$('#jqxgridlma').jqxGrid('clearselection');
      		         			//$("#jqxgridlma").jqxGrid("updatebounddata", 'cells');
      		         			//source.data = mainFilter;
			         			loadgrid(dataAdapter);
      		         			showSuccessMessage("操作成功");
      		         		}else{
      		         			showErrorMessage("操作失败，请联系管理");
      		         		}
      			        }
      		     	});
      		   })
      		}else {
      			showErrorMessage("角色已被引用，不能删除！");
      		}
	     }
  	});	 
 
 }
function roleView(viewRoleName,viewRoleId){
	var clientWidth=window.screen.availWidth;
	var clientHeight=window.screen.availHeight;
	 $('#actroCeneWindow').jqxWindow({ 
		 theme:theme,
         resizable: true,
        width: '98%', height:900,maxWidth:clientWidth,maxHeight:'100%', resizable: false,  isModal: true, autoOpen: false//,modalOpacity: 0.01
     });
			//表  
        	 var divstr="<div style=\"border:1px solid #ccc; overflow:hidden; width:100%;\">";
        	 divstr+="<div class=\"title back_1\" id=\"rendertoolbar"+0+"\">";
        	 divstr+="<h6 style=\"font-size:14px;\"> "+viewRoleName+" > 角色场景表   ";
		      if(!isRoleReadonly){
		    	 divstr+="<a class='exportImg' style='line-height:31px;text-decoration:none; height:31px; ' title='导出' href='javascript:downloadExcel(\""+viewRoleId+"\",\""+i+"\");'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>";
		      }
 			 divstr+="<a class='rolecene' ></a></h6></div>";
 			 divstr+="<div id=\"jqxgrid"+0+"\" class=\"t_i\">";
 			 divstr+="</div>";
 			 divstr+="</div><div class='lmaCount' back id='statistics"+0+"'></div><br/>";
 			$("#doubelQ").children().remove(); 
 			$("#doubelQ").append(divstr); 
         //李明 加载场景表 bigin
        	 tabReady(0);//加载表单数据
        	 
             grid = new SimpleGrid("jqxgrid"+0,"<%=request.getContextPath()%>/viewManager/loadViewList",
       				50,0,gridColumns,filter,"resultList",rendertoolbar,viewRoleId,0);
       		grid.loadTable();
         // 场景表end	
    	 $("#actroCeneWindow").jqxWindow('open'); 
}
var leaveActorId;
var actorNames;
//添加或修改
function kqsave(attendanceId){
	var endDateS= $("#endDate").val();
	var startDateS=$("#starDate").val();
	if(endDateS >= startDateS){
		clikcCount=1;
		 $.ajax({
			 	url:'<%=request.getContextPath()%>/roleActorManager/attendanceSave',
	         	type:'post',
	         	data:{
	         		actorId:leaveActorId,
	         		leaveStartDate:startDateS,
	         		leaveEndDate:endDateS,
	         		attendanceId:attendanceId
	         	},
	         	async: false,
	         	dataType: 'json',
	         	 success:function(data){
	         		askForLeave(leaveActorId, actorNames);
		        }
		 });
	}else{
		showErrorMessage("结束日期不能小于开始日期！");
	}
}
//删除考勤
function kqdelet(attendanceId){
	popupPromptBox("提示","您确定要删除此条请假记录么?",function(){
		clikcCount=1;
		 $.ajax({
			 	url:'<%=request.getContextPath()%>/roleActorManager/attendanceDelet',
	      	type:'post',
	      	data:{
	      		attendanceId:attendanceId
	      	},
	      	async: false,
	      	dataType: 'json',
	      	 success:function(data){
					 //$("#popupWindow").jqxWindow('hide');
	      		askForLeave(leaveActorId, actorNames);
		        }
		 });
	})
}
//搜索
function goSaves(){   
	
	 $("#checkGogo").jqxWindow('hide');
	 
	 var roleType=$("#roleType").val();
	 var byview=$("#byview").val();
	 var byPage=$("#byPage").val();
	 var leave=$("#leave").val();
	 var viewRoleName=$("#searchViewRoleName").val();
	 var viewAccountStart=$("#viewAccountStart").val();
	 var viewAccountEnd=$("#viewAccountEnd").val();
	 
	 if(roleType!= null && roleType!=""){
		 mainFilter.roleType=roleType;
 	 }else{
 		mainFilter.roleType="";
 	 }
	 
	 if(byview!= null && byview!=""){
		 mainFilter.byview=byview;
 	 }else{
 		mainFilter.byview="";
 	 }
	 
	 if(byPage!= null && byPage!=""){
		 mainFilter.byPage=byPage;
 	 }else{
 		mainFilter.byPage="";
 	 }
	 
	 if(leave!= null && leave!=""){
		 mainFilter.leave=leave;
 	 }else{
 		mainFilter.leave="";
 	 }
	 
	 if(viewRoleName!= null && viewRoleName!=""){
		 mainFilter.viewRoleName=viewRoleName;
 	 }else{
 		mainFilter.viewRoleName="";
 	 }
	 
	 if(viewAccountStart!= null && viewAccountStart!=""){
		 mainFilter.viewAccountStart=viewAccountStart;
 	 }else{
 		mainFilter.viewAccountStart="";
 	 }
	 
	 if(viewAccountEnd!= null && viewAccountEnd!=""){
		 mainFilter.viewAccountEnd=viewAccountEnd;
 	 }else{
 		mainFilter.viewAccountEnd="";
 	 }
	 
	 
	 source.data = mainFilter;
	 loadgrid(dataAdapter);
}
function goResets(){
	 $("#checkGogo").jqxWindow('hide');
}
//修改点击事件
function acrNameHref(editrow){
	if(isRoleReadonly){
		return null;	
	}
	mergeRoleIds='';//清空
	$("#actorName").removeClass('jqx-validator-error-element').next('label').remove();
	actorIds='';
	viewNames=[];
	$("#rolenameDiv").hide();
  var offset = $("#jqxgridlma").offset();
  $("#popupWindow").jqxWindow({ theme: theme,height:400,width:380,isModal: true,});
  var dataRecord = $("#jqxgridlma").jqxGrid('getrowdata', editrow);
  $("#viewRoleName").val(dataRecord.viewRoleName);
  $("#shortName").val(dataRecord.shortName);
  $("#viewRoleType").val(dataRecord.viewRoleType);
  viewNames[0]=dataRecord.viewRoleName;
  $("#actorName").val(dataRecord.actorName);
 	var enterDate=dataRecord.enterDate;
    var leaveDate=dataRecord.leaveDate;
 if(enterDate!=null && enterDate!=""){
	  $("#enterDate").val(enterDate);
	  $("#leaveDate").val(leaveDate);
 }else{
	  $("#enterDate").val("");
	  $("#leaveDate").val("");
 }
  $("#viewRoleId").val(dataRecord.viewRoleId);
  $("#crewId").val(dataRecord.crewId);
  $("#actorId").val(dataRecord.actorId);
  // show the popup window.
  $(".tite").html("修改角色");
  $("#mergeFlag").val("0");
  $("#popupWindow").jqxWindow('open');
}
//请假
	function askForLeave(actorId, actorName){
		if(isRoleReadonly){
			return null;	
		}
       leaveActorId=actorId;
       actorNames=actorName;
       if(actorName!="" && actorName!=null && actorName != "null"){
            var source1 =
            {
                datatype: "json",
                datafields: [
					{ name: 'attendanceId',type: 'string' },
					{ name: 'leaveStartDate',type: 'string' },
					{ name: 'leaveEndDate',type: 'string' },
					{ name: 'leaveDays',type: 'string' }
                ],
                type:'post',
                beforeprocessing:function(data){
                	//查询之后可执行的代码
                	viewRoleData = data.result;
                	source.totalrecords=data.result.total;
                },
                root:'resultList',
                processdata: function (data) {
                    //查询之前可执行的代码
                },
                url:'<%=request.getContextPath()%>/roleActorManager/leaveList?actorId='+actorId,
            };
            var rendergridrows = function (params) {
            	//调用json返回的列表数据
                return params.data;
            };
            var dataAdapter1 = new $.jqx.dataAdapter(source1);
            $("#jqxgridDate").jqxGrid(
            {
            	width: 590,
            	//theme: theme,
                source: dataAdapter1,
                height:300,
                localization:localizationobj,
                sortable: true,
                filterable: true,
                altrows: true,
                columns: [
                  { text: '开始时间',  datafield: 'leaveStartDate', width: '25%',cellsAlign: 'center',align:'center' },
                  { text: '结束时间', datafield: 'leaveEndDate', width: '25%',cellsAlign: 'center',align:'center' },
                  { text: '请假天数', datafield: 'leaveDays', width: '25%',cellsAlign: 'center',align:'center' },
                  { text: '操作', datafield: 'edindt',width: '25%',align:'center',
                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
   					   var html = "<div class='font_v_m ft_m'><a href='javascript:void(0);' style='width: 50%; height: 99%;text-decoration:none;' "
   					   + " aria-disabled='false' class='' "
   					   + " onclick='kqdelet(\""+rowdata.attendanceId+"\")' >删除</a></div>";
   	                    return html;
                    }                	  
                  }
                ]
            });
              //请假设置
              $("#attendanceWindow").jqxWindow({
             	 theme: theme, width: 640, height:420,  isModal: true,// cancelButton: $("#reset"), //modalOpacity: 0.01           
              });
             // $("#starDate").jqxDateTimeInput({formatString: "yyyy-MM-dd", width: '150px', height: '25px',culture: 'ch-CN'});
              //$("#endDate").jqxDateTimeInput({formatString: "yyyy-MM-dd", width: '150px', height: '25px',culture: 'ch-CN'});
              $("#saves").jqxButton({ theme: theme,width: '60px'});
              //$("#reset").jqxButton({ theme: theme,width: '60px' });
              
              $("#attendanceWindow").jqxWindow('open');
           }else{
        	   showInfoMessage("请先为角色选择演员!");
           }
}
//查询当前角色戏量
function fenAddress(viewRoleId,viewRoleName,actorName){
  if(actorName == 'null'|| actorName==''||actorName==null){
	actorName='待定'; 
  }
  statisticRoleName = viewRoleName;
	//$("#tabAddress").jqxTabs({theme:theme,  height: '100%', width: '99%' });
	 $("#actorCrewAddress").jqxWindow({theme:theme,
		 width: 840, height:710,maxWidth:830, resizable: false,  isModal: true, autoOpen: false//,modalOpacity: 0.01           
     });
	//按拍摄地点主场景统计
	$.ajax({
        url:  '<%=request.getContextPath()%>/lmaroleCrewReportManager/getByAddress',  
        type: 'POST',
        data: {
        	roleName:viewRoleId
         },
        async: false,
        dataType: 'JSON',
        success:function(data){
		if(data!="" && data!=null)
		{
			var strtite="<div style='font-size: 13px;line-height:25px;'>&nbsp;&nbsp;"+viewRoleName+"&nbsp;|&nbsp;演员："+actorName+"&nbsp;|&nbsp;共"+data.totaView+"&nbsp;|&nbsp;场景数："+data.mainCount+"&nbsp;|&nbsp;";//其中：
			if(data.wuPage!=undefined){
				strtite+="&nbsp;"+data.wuPage+"&nbsp;|&nbsp;"+data.wenPage+"&nbsp;|&nbsp;";
				if(data.wenWuPage!=undefined){
					strtite+=data.wenWuPage+"&nbsp;|&nbsp;";
				}
			}
			strtite+="<br/>&nbsp;&nbsp;";
			if(data.byScene != undefined){
				strtite+="日："+data.byScene+"&nbsp;|&nbsp;"; 
			}
			if(data.nineScene !=undefined){
				strtite+="夜："+data.nineScene+"&nbsp;|&nbsp;";
			}
			if(data.nineByScene !=undefined){
				strtite+="日夜："+data.nineByScene+"&nbsp;|&nbsp;";
			}
			if(data.waiScene !=undefined){
				strtite+="外："+data.waiScene+"&nbsp;|&nbsp;";
			}
			if(data.neiScene !=undefined){
				strtite+="内："+data.neiScene+"&nbsp;|&nbsp;";
			}
			if(data.neiWaiScene !=undefined){
				strtite+="内外："+data.neiWaiScene+"&nbsp;|&nbsp;";
			}
			strtite+="</div>";
			$(".mainTite").children().remove(); 
			$(".mainTite").append(strtite); 
			 //消除拍摄地主场景内容
			 $("#jqxgridAddress").children().remove();;
			//角色戏量
		  	   //主场景
		  	   $("#jqxgridAddress").append(data.resulDatatList);
						}
			        }
			  	});
	 $("#actorCrewAddress").jqxWindow('open');
	 
	 //分集//pageTite
		diversityStatistics(viewRoleName);
}
function diversityStatistics(viewRoleName){
	$.ajax({
        url:  "<%=request.getContextPath()%>/lmaroleCrewReportManager/getByContSet",  
        type: 'POST',
        data: {
        	roleName:viewRoleName,
        	statisType:statisType
         },
        async: false,
        dataType: 'JSON',
        success:function(data){
			if(data!="" && data!=null)
			{
				
			  	   //console.log(data.categories);
			  	   var seriesDataArray = [];
			  	   
			  	 seriesDataArray[0]=data.series;
			  	var columnWidth=15;//每个柱子自身的宽度
			  	
		 	   	var xmin = 100;
			  	var scrollbar = false;
			  	if(data.categories.length>30){
			  		xmin = parseInt(30/data.categories.length*100);
			  		scrollbar = true;
			  	}
			  	var xtext = '按场统计';
			  	var tips = "场";
			  	if(statisType == 2){
			  		xtext = '按页统计';tips = "页";
			  	}
			  	
			  	 require(
			             [
			                 'echarts',
			                 'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
			             ],
			             function(ec){
			            	 //var ecConfig = require('echarts/config'); //点击事件
			             
			            	 var myChart = ec.init(document.getElementById('jqxgridPage')); 
			            	 
			            	 var option = {
			                         tooltip: {
			                             show: true
			                         },
			                         legend: {
			                             data:[tips],
			                             //y:'bottom' 
			                             x: 740,
			                             y: 20
			                         },
			                         grid:{
			                        	 x2:20,
			                        	 x:30,
			                        	 y2:65,
			                        	 y:40
			                         },
			                         title: {
			                        	 text: xtext,
			                        	 x: 'center',
			                        	 itemGap: 0,
			                        	 padding: 0,
			                        	 textStyle: {
			                        		 fontSize: 16,
			                        		 fontWeight: 'normal',
			                        		 color: '#333'
			                        	 }
			                         },
			                         tooltip: {
			                        	 formatter: function(params,ticket,callback){
			                        		 //console.log(params)
			                        		 var res = params.name+"集</br>"+params.value+params.seriesName;
			                        		 /* for(var i = 0, l = params.length; i < l; i++) {
			                        			 
			                        		 } */
			                        		 //callback(ticket, res);
			                        		 return res;
			                        	 },
			                         },
			                         dataZoom: {
			                        	 show: scrollbar,
			                        	 start: 0,
			                        	 end: xmin,
			                         },
			                         xAxis : [
			                             {
			                                 type : 'category',
			                                 splitLine: {show:false},
			                                 data : data.categories
			                             }
			                         ],
			                         yAxis : [
			                             {
			                                 type : 'value'
			                             }
			                         ],
			                         series : seriesDataArray
			                     };
			            	// console.log(option)
			            	 myChart.setOption(option); 
			            	 /* myChart.on(ecConfig.EVENT.CLICK, function (param) { //点击事件
			            	        console.log(param)
			            	    }) */
			             }
			             );
			  	
			  	   //演员参演集数统计
			  	   
			  	 $(".actorNumber").html(data.partSetNo+"/"+data.totalSetNo);
			}
        }
  	});
}
/**
 * 格式化金额
 * @param s
 * @param n
 * @returns {String}
 */
function fmoney(s, n)   
{   
	var ss = $.trim(s);
	if(s == null || ss == "" || s==undefined)return "";
   n = n > 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(),   
   r = s.split(".")[1];   
   t = "";   
   for(var i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }   
   return t.split("").reverse().join("") + "." + r;   
}
//全选事件
/* function checkedNames(obj) {
	if($(obj).is(":checked")){
		$("[name='roleNamechecked']").prop("checked",true);
		scrollCheck = allRoleId;
	}else{
		$("[name='roleNamechecked']").prop("checked",false);
		scrollCheck = [];
	}
	
    event.stopPropagation();
}; */
/* 演员评价 */
function actorEvaluate(viewRoleName,actorName){
    types_pf=0;//0为添加1为修改
	actorName_pf=actorName;
    viewRoleName_pf=viewRoleName;
    evaluateId_pf='';
    
    $(".content input[type=checkbox]").prop("checked",false);
	Grade.setScore(0);
	$('.grade-py').val("");
     //查询演员评价详情
	/* $.ajax({
		url:ctx+'/evaluateManager/evaluateDesc',
	   	type:'post',
	   	data:{
	   		fromUserName:'${user.userName}',
	   		toUserName:actorName,
	   		roleName:viewRoleName
	   	},
	   	async: false,
	   	dataType: 'json',
	   	success:function(data){
	        $(".content input[type=checkbox]").prop("checked",false);
			Grade.setScore(0);
			$('.grade-py').val("");
			 //debugger;
			if(data.length>0){
				if(data[0].score!=null && data[0].score>0){
					Grade.setScore(data[0].score);// * 10
				}
				if(data[0].fromUserName == '${user.userName}'){
					types_pf=1;
				}
				evaluateId_pf=data[0].evaluateId;
	            $('.grade-py').val(data[0].comment);
	            for(var i=0;i<data.length;i++){
		           $("#"+data[i].tagId).prop("checked",true);
	            }
			}
	    } 
	 }); */
	$('#actroEvaluateWindow').jqxWindow("open");
}

function clearSearch(){
	 $("#roleType").val('0'), 
	 //$("#byview").val(), 
	 //$("#byPage").val(), 
	 //$("#leave").val(), 
	 $("#searchViewRoleName").val(''),
	 $("#viewAccountStart").val(''),
	 $("#viewAccountEnd").val('')
}

</script>
 <style type="text/css">
        .register-table
        {
            margin-top: 10px;
            margin-bottom: 10px;
        }
        .register-table td, 
        .register-table tr
        {
            margin: 0px;
            padding: 2px;
            border-spacing: 0px;
            border-collapse: collapse;
            font-family: Verdana;
            font-size: 12px;
        }
        h3 
        {
            display: inline-block;
            margin: 0px;
        }
        .bd_wrap{margin-top:10px;}
    </style>
    
</head>
<body>
           <!-- <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget"> -->
                  <!-- <ul>
                      <li style="margin-left: 30px;">演员角色管理</li>
                  </ul> -->
                <!--  <div> -->
		<div id="jqxgrid-mian-position">
		</div>
       <div id="popupWindow" style="display: none;">
            <div ><span class='tite'>修改角色</span></div>
            <div style="overflow: hidden;">
              <form id='form' action="<%=request.getContextPath()%>/roleActorManager/saveRole" method="post">
              	<input type="hidden" id='mergeFlag' name="mergeFlag"/>
              	<input type="hidden" id='viewRoleId' name="viewRoleId"/>
              	<input type="hidden" id='crewId' name="crewId"/>
              	<input type="hidden" id='actorId' name="actorId"/>
                <table style="margin: auto;"><br/>
                    <tr  style="height: 49px">
                        <td align="right"><span style="color: red;margin-left: 9px;">*</span> 角色名称：</td>
                        <td align="left"><input type="text" id="viewRoleName" class="text-input" value=''/>
                        	<div style="color: #dd4b39;display: none;" id='rolenameDiv'>角色名已存在</div>
                        </td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right">角色简称：</td>
                        <td align="left"><input type="text" id="shortName"  class="text-input"/></td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right" ><span style="color: red;margin-left: 9px;">*</span> 演员类型：</td>
                        <td align="left">
                        	<select id="viewRoleType" class="text-input">
                        		<option value="" selected="selected">--请选择--</option>
                        		<option value="1">主要演员</option>
                        		<option value="2">特约演员</option>
                        		<option value="3">群众演员</option>
                        	</select>
                        </td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right">演员姓名：</td>
                        <td align="left"><input type="text" class="text-input" id='actorName'></td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right">入组时间：</td>
                        <td align="left"><input type="text" class="text-input" style="width: 242px;height: 32px;" id="enterDate" readonly="readonly" onfocus="WdatePicker({readOnly:true,startDate:'${crewDate.shootStartDate}'})" /></td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right">离组时间：</td>
                        <td align="left"><input type="text" class="text-input" style="width: 242px;height: 32px;" id="leaveDate" readonly="readonly" onfocus="WdatePicker({readOnly:true,startDate:'${crewDate.shootEndDate}'})" /></td>
                    </tr>
                    <tr>
                        <!-- <td align="right"></td> -->
                        <td align="center"  style="padding-top: 10px;"  colspan="2">
	                        <input style="margin-right: 70px; width: 50px;" type="button" id="Save" value="确定" />
	                        <input id="Cancel" style='width: 50px;/* margin-right: 55px; */' type="button" value="取消" />
                        </td>
                    </tr>
                </table>
                </form>
            </div>
       </div>
       <!-- 考勤设置 -->
       <div style="display: none" id="attendanceWindow">
            <div id=''>请假设置</div>
            <div style="overflow: hidden;">
              	<input type="hidden" id='' name="viewRoleId"/>
              	<input type="hidden" id='' name="crewId"/>
              	<input type="hidden" id='' name="actorId"/>
                <table id="attendTable" style="margin-top: 20px;margin-left: 25px;">
	                <tr ><td ><h4 >添加请假记录：</h4></td>
	                <td align='left'>
	                	<input type="text" style="width: 180px;height: 32px;" class="text-input" id="starDate" value="<%=str_date1 %>" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" />
	                <!-- <div  id='starDate' style="float: left; margin-top: 3px;margin-right: 5px;" ></div> --><!-- <div style='/* float: left; margin-top: 10px;margin-right: 5px; */'> <h4>--><span style="margin-left: 20px;">到</span><!--</h4> </div> --></td>
					<td align='left'><!-- <div  id='endDate' style='float: left; margin-top: 3px;margin-right: 15px;'></div> -->
						<input type="text" class="text-input" style="margin-left:20px; width: 180px;height: 32px;" value="<%=str_date1 %>" id="endDate" readonly="readonly" onfocus="WdatePicker({isShowClear:false,readOnly:true})" />
					</td>
					<td align='left'><input style='margin-right: 10px;margin-top: 3px' type='button' onclick='kqsave()' id='saves' value='添加'/></td>
					</tr><tr height='10px'></tr>
                </table>
                 <div id="jqxgridDate" style="margin-left: 20px;">
        		</div>
            </div>
       </div>
           <!-- 搜索条件 -->
       <div style="display: none" id="checkGogo">
            <div id=''>条件查询</div>
            <div style="overflow: hidden;">
                <table id="" style="margin-left: 30px;">
                    <tr style="height: 49px">
                        <td align="right">角色名称：</td>
                        <td align="left"><input type="text" id="searchViewRoleName" class="text-input" value=''/></td>
                    </tr>
                	<!-- <tr height="20px;"></tr> -->
                	<tr style="height: 49px">
                        <td align="right">演员类型：</td>
                        <td align="left">
							<select id="roleType" class="text-input">
								<option value=0>全部演员</option>
								<option value=1>主要演员</option>
								<option value=2>特约演员</option>
								<option value=3>群众演员</option>
								<option value=4>待定角色</option>
							</select>
						</td>
                    </tr>
                    <tr style="height: 49px">
                        <td align="right">场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数：</td>
                        <td align="left">&nbsp;<input type="text" id="viewAccountStart" style="width: 100px;" onkeyup="(this.v=function(){this.value=this.value.replace(/[^0-9-]+/,'');}).call(this)"   class="text-input" value=''/>&nbsp;&nbsp;&nbsp;到&nbsp;&nbsp;&nbsp;
                        <input type="text" style="width: 100px;" onkeyup="(this.v=function(){this.value=this.value.replace(/[^0-9-]+/,'');}).call(this)"  id="viewAccountEnd" class="text-input" value=''/></td>
                    </tr>
                     <tr>
                        <!-- <td align="right"></td> -->
                        <td align="center" style="padding-top: 50px;" colspan="2">
	                        <input style="margin-right: 25px;width: 50px;" type="button" onclick="goSaves();" id="goSave" value="确定" />
	                        <input id="goReset" onclick="goResets();" style="width: 50px;margin-right: 25px;" type="button" value="取消" />
	                        <input id="clearSearch" onclick="clearSearch();" style='width: 50px;/* margin-right: 55px; */' type="button" value="清空" />
                        </td>
                    </tr>
                </table>
            </div>
       </div>
   		<!-- 角色戏量统计 -->
       
  <div style="display: none" id="actorCrewAddress">
      <div id='' >戏量统计</div>
     <!--  <dd  style='margin-top: 1px;' id='mainTite'></dd> -->
     <div class="bd_wrap">
	<div class="btn_tab_wrap">
    	<div class="btn_wrap"></div>
        <div class="tab_wrap">
        	<ul>
            	<li id="tab_1" class="tab_li_current" style="width: 45%">分拍摄地主场景</li>
                <li id="tab_2" style="width: 45%">分集</li>
            </ul>
        </div>
    </div>
    <div class="danju danju_1">
   			<table id='tieaddress'   style="border:1px solid #ccc;width: 775px;margin-top: 20px;margin-left: 20px;vertical-align: middle;">
   				<thead>
                      <tr id="">
                          <td style="width: 34%"><div class="td_div">拍摄地点/戏量</div></td>
                          <td style="width: 34%"><div class="td_div">主场景</div></td>
                          <td style="width: 34%"><div class="td_div">戏量</div></td>
                      </tr>
               		 </thead>
             </table>
               		 <div style="overflow-y:auto;height: 400px; margin-right: 20px" > 
               		 	<div  id="jqxgridAddress" style="margin-left: 20px; " >
               		 	
               		 	</div>
               		 </div>
               		 	 <dd  style='margin-top: 10px;margin-left: 20px;' class='mainTite'></dd>
    </div>
    
     <div class="danju danju_2" style="display: none;"> 
     	<div style="font-size: 18px;line-height: 33px;margin-left: 20px;">
     		参演集数：<span class='actorNumber'></span>
     		<div style="display: inline-block;float: right;font-size: 12px;margin-right: 10px;">
     			<input type="radio" style="cursor:pointer;" name="statisticsType"  checked value="1">场&nbsp;&nbsp;&nbsp;
     			<input type="radio" style="cursor:pointer;" name="statisticsType"   value="2">页
     		</div>
     	</div>
     	<div style="width: width: 800px;height: 422px;overflow-x: auto;overflow-y: hidden;">
        	<div  style='margin-top: 0px;margin-left: 20px;margin-right: 20px;width: 800px;height: 422px;' id="jqxgridPage"></div>
        </div>
        <dd  style='margin-top: 10px;margin-left: 20px;' class='mainTite'></dd>
		
    </div>
</div>
</div>
       <!-- 角色场景表          /////////////////////////////////////////////////////////////////////////////////   -->
<div style="display: none" id="actroCeneWindow">
    <div id=''>角色场景表</div>
        	<!-- 表格加载div -->
		<div id="doubelQ">
				
		</div>
			<!-- 表格加载div结束 -->
</div>
<!--演员评价  -->
<div style="display: none" id="actroEvaluateWindow">
    <div id=''>演员评价</div>
        	<!-- 表格加载div -->
		<div id="actorEvaluate">
		<div class="wrap">
			<ul class="content">
        <li>
            <label class="lh45">评价:</label>
            <ul class="grade-star">
                <li>
                    <span class="star-left"></span><span class="star-right"></span>
                    <span class="star-info">很差</span>
                </li>
                <li>
                    <span class="star-left"></span><span class="star-right"></span>
                    <span class="star-info">差</span>
                </li>
                <li>
                    <span class="star-left"></span><span class="star-right"></span>
                    <span class="star-info">一般</span>
                </li>
                <li>
                    <span class="star-left"></span><span class="star-right"></span>
                    <span class="star-info">好</span>
                </li>
                <li>
                    <span class="star-left"></span><span class="star-right"></span>
                    <span class="star-info">非常好</span>
                </li>
            </ul>
            <label class="lh45">得分:</label><span class="grade-df">0</span>
        </li>
        <li class="yx">
            <label>印象:</label>
            <ul class="grade-impression best">
                <li></li>
               <!--  <li><input type="checkbox" name="best" value=""><span>工作认真</span></li>
                <li><input type="checkbox" name="best"><span>精益求精</span></li>
                <li><input type="checkbox" name="best"><span>谦恭有礼</span></li>
                <li><input type="checkbox" name="best"><span>生活严谨</span></li>
                <li><input type="checkbox" name="best"><span>爱护道具</span></li>
                <li><input type="checkbox" name="best"><span>遵纪守时</span></li>
                <li><input type="checkbox" name="best"><span>不为钱动</span></li>
                <li><input type="checkbox" name="best"><span>重信守约</span></li> -->
                <c:forEach items="${evtagList }" var='evtag'>
                  <c:if test="${evtag.tagType eq 1}">
                	<li><input type="checkbox" id='${evtag.tagId}' name="best" value="id='${evtag.tagId}'"><span>${evtag.tagName}</span></li>
                </c:if>
                </c:forEach>
            </ul>
            <div class="hr"></div>
            <ul class="grade-impression bad">
                <li></li>
                <!-- <li><input type="checkbox" name="bad"><span>好耍大牌</span></li>
                <li><input type="checkbox" name="bad"><span>肆意毁约</span></li>
                <li><input type="checkbox" name="bad"><span>迟到早退</span></li>
                <li><input type="checkbox" name="bad"><span>拉帮结派</span></li>
                <li><input type="checkbox" name="bad"><span>毁坏物品</span></li>
                <li><input type="checkbox" name="bad"><span>不服管理</span></li>
                <li><input type="checkbox" name="bad"><span>酗酒赌博</span></li>
                <li><input type="checkbox" name="bad"><span>涉黄涉毒</span></li> -->
                <c:forEach items="${evtagList }" var='evtag'>
                  <c:if test="${evtag.tagType eq 2}">
                	<li><input id='${evtag.tagId}' type="checkbox" name="best" value="${evtag.tagId}"><span>${evtag.tagName}</span></li>
                </c:if>
                </c:forEach>
            </ul>
        </li>
        <li class="py">
            <label>评语:</label>
            <textarea class="grade-py"></textarea>
        </li>
    </ul>
    <div style="width: 100%;text-align: center;">
        <button id="actorEvaluateSubmit">提交</button>
        <button id="actorEvaluateCencle" style="margin-left: 100px;">取消</button>
    </div>
		</div>		
		</div>
			<!-- 表格加载div结束 -->
</div>   				
   				
   				<!-- </div> -->
              <!-- </div> -->
   
<input type="button" style="display: none;" class="exportImg"  >   
<form action="" id="postForm" method="post"></form>  
</body>
</html>