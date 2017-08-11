<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>   
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">	
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/finance/finance.css" type="text/css"></link>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxdatatable.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxtreegrid.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxgrid.grouping.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxgrid.aggregates.js"></script>
<style type="text/css">
.groupDiv{width:100%;height:100%;background:#E8E8E8;margin-top: -12px;}
</style>	
<script type="text/javascript">
var ctx;
$(document).ready(function(){
	 topbarInnerText("进度表&&合同进度");
	ctx=$("input[name=contextPath]").val();
	
	//$("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%' });
	var source =
    {             
        datafields:
        [
            { name: 'contractId', type: 'string' },
            { name: 'contractNo', type: 'string' },
            { name: 'prosonName', type: 'string' },
            { name: 'totalMoney', type: 'string' },
            { name: 'payMoney', type: 'string' },
            { name: 'currencyId', type: 'string' },
            { name: 'currencyName', type: 'string' },
            { name: 'currencyCode', type: 'string' },
            { name: 'type', type: 'number' },
            { name: 'accountId', type: 'string' },
            { name: 'accountName', type: 'string' }
        ],
        datatype: "json",
        type:"post",
        url:'<%=request.getContextPath()%>/invoice/getContractList',
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        }
    };
    var dataAdapter = new $.jqx.dataAdapter(source);
    var toThemeProperty = function (className) {
        return className + " " + className + "-" + theme;
    }
    var groupsrenderer = function (text, group, expanded, data) {
        if (data.groupcolumn.datafield == 'type') {
       	 //console.log(data)
       		/* if (data.subItems.length > 0) {
              var aggregate = this.getcolumnaggregateddata('totalMoney', ['sum'], true, data.subItems);
              var aggregate2 = this.getcolumnaggregateddata('payMoney', ['sum'], true, data.subItems);
        	}
        	else {
             var rows = new Array();
             var getRows = function (group, rows) {
             if (group.subGroups.length > 0) {
                    for (var i = 0; i < group.subGroups.length; i++) {
                                    getRows(group.subGroups[i], rows);
                    }
              }
              else {
                   for (var i = 0; i < group.subItems.length; i++) {
                                    rows.push(group.subItems[i]);
                   }
              }
        	}
        	getRows(data, rows)
        	var aggregate = this.getcolumnaggregateddata('totalMoney', ['sum'], true, rows);
        	var aggregate2 = this.getcolumnaggregateddata('payMoney', ['sum'], true, rows);
        	} */
        	if(data.group == 1){
           	 	return '<div class="' + toThemeProperty('jqx-grid-groups-row') + ' groupDiv" style="position: absolute;"><span>&nbsp;职员合同</span></div>'; 
        	}else if(data.group == 2){
           	 	return '<div class="' + toThemeProperty('jqx-grid-groups-row') + ' groupDiv" style="position: absolute;"><span>&nbsp;演员合同</span></div>';
        	}else{
           		 return '<div class="' + toThemeProperty('jqx-grid-groups-row') + ' groupDiv" style="position: absolute;"><span>&nbsp;制作合同</span></div>';
        	}
        }
        else {
            return '<div class="' + toThemeProperty('jqx-grid-groups-row') + '" style="position: absolute;"><span>' + text + '</span>';
        }
    }
	$("#jqxgrid").jqxGrid(
            {
                width: '99.5%',
                theme:theme,
                height:'90%',
                source: dataAdapter,
                groupable: true,
                groupsrenderer: groupsrenderer,
                showgroupsheader: false,
                selectionmode: 'multiplerowsextended',
                groups: ['type'],
                columns: [
                  { text: '合同编号', groupable: false, datafield: 'contractNo', width: '20%' },
                  { text: '姓名', groupable: false, datafield: 'prosonName', width: '15%',
                	  cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
                		  return '<div class="font_v_m"><a href="javascript:void(0)" onclick="queryDetails(this);" sid="'+rowdata.contractId+'">'+value+'</a></div>';
                	  }
                  },
                  { text: '<div style="margin-right:20px;">合同金额</div>', groupable: false, datafield: 'totalMoney', width: '15%' ,align: "right",
               	   cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
              		 	return '<div class="font_v_m dig_sty pad_l_5">'+fmoney(value,2)+'<span style="color:#1C94C4;">('+rowdata.currencyCode+'<span>)'+'</div>';
              	  		}
                  },
                  { text: '<div style="margin-right:20px;">已付金额</div>', groupable: false, datafield: 'payMoney', width: '25%', cellsalign: 'right',align: "right",
               	   cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
                 		 	return '<div class="font_v_m dig_sty pad_l_5">'+fmoney(value,2)+'<span style="color:#1C94C4;">('+rowdata.currencyCode+'<span>)'+'</div>';
                 	  }
                  },
                  { text: '<div style="margin-right:20px;">已付比例</div>', groupable: false, width: '25%', cellsalign: 'right',align: "right",
               	   cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
                		  return '<div class="font_v_m dig_sty pad_l_5">'+fmoney(accDiv(rowdata.payMoney,rowdata.totalMoney)*100,2)+"%"+'</div>';
                	  }
                  },
                  { text:'',datafield: 'type',width:0,
               	   cellsrenderer:function(row, columnfield, value, defaulthtml, columnproperties, rowdata){
                 		  return '';
                 	  }
                  }
                ]
      		});
		
});
//查询付款明细
var queryDetails = function(obj){
	alert($(obj).attr("sid"))
}
//减法函数  
function Subtr(arg1, arg2) { 
	if(arg1 == null)arg1 = 0;
	if(arg2 == null)arg2 = 0;
    var r1, r2, m, n;  
    try {  
        r1 = arg1.toString().split(".")[1].length;  
    }  
    catch (e) {  
        r1 = 0;  
    }  
    try {  
        r2 = arg2.toString().split(".")[1].length;  
    }  
    catch (e) {  
        r2 = 0;  
    }  
    m = Math.pow(10, Math.max(r1, r2));  
     //last modify by deeka  
     //动态控制精度长度  
    n = (r1 >= r2) ? r1 : r2;  
    return ((arg1 * m - arg2 * m) / m).toFixed(n);  
}
//除法函数  
function accDiv(arg1, arg2) {  
	if(arg1 == null)arg1 = 0;
	if(arg2 == null)arg2 = 0;
    var t1 = 0, t2 = 0, r1, r2;  
    try {  
        t1 = arg1.toString().split(".")[1].length;  
    }  
    catch (e) {  
    }  
    try {  
        t2 = arg2.toString().split(".")[1].length;  
    }  
    catch (e) {  
    }  
    with (Math) {  
        r1 = Number(arg1.toString().replace(".", ""));  
        r2 = Number(arg2.toString().replace(".", ""));  
        return (r1 / r2) * pow(10, t2 - t1);  
    }  
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
</script>

  </head>
  
  <body>
  <!-- <div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget"> -->
       <!--   <ul>
             <li style="margin-left: 30px;">合同进度</li> 
         </ul> -->
         
         <!--职员合同 -->
        <div>
        	<div id="jqxgrid"></div>
        	
        </div>               
		<input type=hidden name=contextPath value='<%= request.getContextPath()%>' /> 	              
 <!--  </div> -->
  
  </body>
</html>
