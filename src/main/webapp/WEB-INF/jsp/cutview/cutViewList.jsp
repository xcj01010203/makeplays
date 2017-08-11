<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isCutViewReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CUTVIEW)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CUTVIEW) == 1){
        	isCutViewReadonly = true;
        }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
<link rel="stylesheet" href="<%=path%>/css/exportLoading.css" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=path%>/css/cutView/cutViewList.css">

<script src="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.js"></script>
<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=path%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=path%>/js/cutView/cutViewList.js"></script>
 <script type="text/javascript">
        var isCutViewReadonly = <%=isCutViewReadonly %>;
 </script>
</head>
<body>
    <div class="my-container">
        <div class="toolbar">
            <input type="button" class="search-btn" onclick="showAdvanceQuery()">
            <div class="hide-already">
                <span>隐藏已完成</span>
                <div class="ui toggle checkbox">
		                <input type='checkbox' id='showAllViews' checked onclick="isHideAll(this)">
		                <label></label>
		            </div>
            </div>
        </div>
        <div class="main-content">
            <div class="main-grid">
                <div class="grid-header">
                    <table class="table-header" cellspacing="0" cellpadding="0" id="tableHeader">
                        <tr>
                            <td style="width: 10%;">拍摄日期<span class="sort-desc" onclick="sortCutDate(this)"></span></td>
                            <td style="width: 8%;">组别</td>
                            <td style="width: 10%;" id='querySeriesViewTd'></td>
                            <td style="width: 8%;">拍摄页数</td>
                            <td style="width: 10%;" class="split-line">拍摄状态</td>
                            <td style="width: 10%;">剪辑日期</td>
                            <td style="width: 10%;">剪辑时长</td>
                            <td style="width: 24%;">备注</td>
                            <td style="width: 10%;">该场是否剪完</td>
                            <td style="width: 0px; border: 0px;"></td>
                        </tr>
                    </table>
                </div>
                <div class="grid-body" id="gridBody">
                    <table class="table-body" id="cutViewTable" cellspacing="0" cellpadding="0"></table>
                </div>
            </div>
        </div>
        <div class="grid-footer">
            <p> 拍摄完成&nbsp;&nbsp;&nbsp;&nbsp;<span id="viewCount">20</span>&nbsp;&nbsp;&nbsp;场&nbsp;&nbsp;&nbsp;&nbsp;<span id="pageCount">15</span>&nbsp;&nbsp;&nbsp;页</p>
            <p>剪辑完成&nbsp;&nbsp;&nbsp;&nbsp;<span id="cutViewCount">10</span>&nbsp;&nbsp;&nbsp;场&nbsp;&nbsp;&nbsp;&nbsp;<span id="cutPageCount">6</span>&nbsp;&nbsp;&nbsp;页&nbsp;&nbsp;&nbsp;&nbsp;<span id="minuteCount">80</span>&nbsp;&nbsp;&nbsp;分钟</p>
        </div>
        
        <!-- 高级查询 -->
        <div class="jqx-window" id="advanceQuery">
            <div>高级查询</div>
            <div class="jqx-content">
                <ul class="query-content">
                    <li>
                        <span>拍摄日期:</span>
                        <input type="text" id="shootStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">——<input type="text" id="shootEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
                    </li>
                    <li id='querySeriesViewLi'>
                    </li>
                    <li>
                        <span>拍摄页数:</span>
                        <input type="text" id="startPage" onclick="onlyNumberPointer(this)">——<input type="text" id="endPage" onclick="onlyNumberPointer(this)">
                    </li>
                    <li>
                        <span>剪辑时长:</span>
                        <input type="text" id="startLength" placeholder="填写分钟">——<input type="text" id="endLength" placeholder="填写分钟">
                    </li>
                    <li>
                        <span>剪辑日期</span>
                        <input type="text" id="cutStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">——<input type="text" id="cutEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})">
                    </li>
                </ul>
                <div class="win-btn-list">
                    <input type="button" value="查询" onclick="confirmQuery()">
                    <input type="button" value="清空" onclick="clearQueryContent()">
                    <input type="button" value="取消" onclick="closeAdvanceQuery()">
                </div>
            </div>
        </div>
         <!-- 正在加载 -->
			  <div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;">
			  </div>
			  <div id="loadingDataDiv" class="show-loading-container" style="display: none;">
			    <div class="show-loading-div"> 正在加载数据，请稍候... </div>
			  </div>
    </div>
</body>
</html>