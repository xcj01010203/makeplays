<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isRoleReadonly = false;//道具表是否只读

Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_PROPS)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_PROPS) == 1){
            isRoleReadonly = true;
        }
    }
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">
<script src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script> 
<link rel="stylesheet" href="<%=basePath%>/css/propInfoList/propInfoList.css" type="text/css">
<script type="text/javascript" src="<%=path%>/js/prop/propList.js"></script>
<script>
	var isReadonly = <%=isRoleReadonly %>;
</script>
</head>
<body>
	<input type="hidden" id='sortType' value='0'>
	<!--道具表工具栏-->
		<div id="" class="prop-head-wrap">
			<div class="prop-toolbar-wrap">
				<input type="button" name="" id="" title="搜索" class="prop-toolbar-search" onclick="searchPropPopupWin()"/>
				<input type="button" name="" id="" title="添加服化道信息" class="prop-toolbar-add" onclick="addPropInfoWin()"/>
				<input type="button" name="" id="" title="合并名称" class="prop-toolbar-combine" onclick="combinePropPopupWin()"/>
				<input type="button" name="" id="" title="设置类型" class="prop-toolbar-setType"  onclick="showPropsType()"/><!--onmouseout="hidePropsType()"-->
				<div class="select-prop-popup" id="selectProWrap">
					<div id="commonProp" onclick="setPropsType(0)">普通道具</div>
					<div id="specialProp" onclick="setPropsType(1)">特殊道具</div>
					<div id="makeupDiv" onclick="setPropsType(2)">化妆</div>
					<div id="clothesDiv" onclick="setPropsType(3)">服装</div>
				</div>
				<input type="button" name="" id="" title="删除服化道信息" class="prop-toolbar-delete" onclick="batchDelPropInfo()"/>
				<input type="button" name="" id="" title="导出列表" class="prop-toolbar-export" onclick="exportPropTab()"/>
			</div>
		</div>
		
		<input type="hidden" id="sortFlag" value=0>
		<!--道具表表头-->
		<ul class="clearfix prop-table-head">
			<li class="fl">
				<span><input type="checkbox" name="checkAll" id="checkAll" class="prop-th-allCheck" onclick="checkedAllProp()"/></span>
			</li>
			<li class="fl">名称<span class="sort-btn" sval='1' onclick="sortPropList(this)"></span></li>
			<li class="fl">类型<span class="sort-btn" sval='2' onclick="sortPropList(this)"></span></li>
			<li class="fl">首次出现<span class="sort-btn" sval='3' onclick="sortPropList(this)"></span></li>
			<li class="fl">场数<span class="sort-btn select" sval='0' onclick="sortPropList(this)"></span></li>
			<li class="fl">库存数量</li>
			<li class="fl">备注</li>
		</ul>
	<div class="prop-body-wrap">
		<!--道具表主要内容部分-->
		<div class="propMainInfo-wrap">
			<table border="" cellspacing="" cellpadding="" class="prop-main-cont" style="border-collapse: collapse;" id="propMainInfo"></table>
		</div>
		<!--道具表右侧弹窗-->
		<div class="">
			<!--<div class="prop-right-opaticy"></div>-->
			<div class="prop-popup-wrap" id="propPopupWin">
				<div class="prop-popup-head">
					<input type="button" id="prop-save-btn" class="prop-save-btn" value="保 存" onclick="savePropsInfo()"/>
					<input type="button" id="prop-delete-btn" class="prop-delete-btn" value="删 除" onclick="delProp()"/>
					<input type="button" id="prop-close-btn" class="prop-close-btn" value="关 闭" onclick="closeRightWin()"/>
				</div>
				<div class="prop-popup-cont">
					<ul class="clearfix name-type-num-wrap">
						<li class="fl">
							<span>服化道名称&nbsp;<span class="must-info">*</span>：</span>
							<span><input type="text" class="prop-name" placeholder="请输入服化道名称" id="propsName" value="" onblur="queryPropName()"/></span>
							<input type="text" name="" id="hidePropInput" value="" style="display: none;"/>
						</li>
						<li class="fl positionR">
							<span>类&nbsp;&nbsp;&nbsp;&nbsp;型&nbsp;<span class="must-info">*</span>：</span>
							<span><input type="text" class="prop-type" placeholder="请输入类型" id="propsType" value="" onclick="selectPropsType()" readonly="readonly"/></span>
							<div class="right-popup-select" id="rightPopupSelect">
								<div onclick="selectPopupType(this)">普通道具</div>
								<div onclick="selectPopupType(this)">特殊道具</div>
								<div onclick="selectPopupType(this)">服装</div>
								<div onclick="selectPopupType(this)">化妆</div>
							</div>
						</li>
						
						<li class="fl">
							<span>库存数量&nbsp;<span class="must-info">*</span>：</span>
							<span><input type="text" class="prop-num" placeholder="请输入库存数量" id="propStock" value="" onkeyup="onlyNumber(this)"/></span>
						</li>
						
					</ul>
					<div id="" class="prop-remark-wrap">
						<span>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</span>
						<span><input type="text" class="prop-remark" id="propRemark" value="" /></span>
					</div>
					<div id='useData'>
						<!--使用记录表-->
						<div class="fontweight use-record-table">
							使用记录表
						</div>
						<div class="show-prop-use-status">待服化道确定后，显示服化道使用情况</div>
						<div class="show-prop-use-status2">暂无数据</div>
					</div>
					<div class="">
						
					</div>
					<table border="" cellspacing="" cellpadding="" class="use-record-table-cont">
						<tr>
							<th>拍摄地</th>
							<th>场景</th>
							<th>场数</th>
						</tr>
					</table>
				</div>
			</div>
		</div>
		
	</div>
	<!--搜索弹框-->
	<div class="prop-search-popup-wrap" id="propSearchPopupWrap">
		<div class="prop-search-opaticy"></div>
		<div class="prop-search-popup">
			<div class="prop-search-popup-head">高级查询</div>
			<ul class="prop-search-popup-body">
				<li class="clearfix">
					<div class="fl lineH30">服化道名称 :</div>
					<div id="" class="fl">
						<input type="text" id="searchPropName" placeholder="请输入查询名称"  class="prop-search-popup-name"/>
					</div>
				</li>
				<li class="clearfix prop-search-type">
					<div class="fl lineH30">服化道类型 :</div>
					<div id="" class="fl">
						<input type="text" id="searchPropType" placeholder="请选择类型"  class="prop-search-popup-type" onclick="showSearchWrap()" readonly="readonly"/>
						<div class="search-prop-select-wrap" id="searchPropTypeSelect">
							<!--<div class="" onclick="searchPropType(this)">请选择道具</div>-->
							<div class="" onclick="searchPropType(this)">普通道具</div>
							<div class="" onclick="searchPropType(this)">特殊道具</div>
							<div class="" onclick="searchPropType(this)">服装</div>
							<div class="" onclick="searchPropType(this)">化妆</div>
						</div>
					</div>
				</li>
				<li class="clearfix">
					<div class="fl lineH30">场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数 :</div>
					<div id="" class="fl">
						<input type="text" id="searchPropStart" placeholder="请输入场数" class="prop-search-popup-Cnum1" onkeyup="onlyNumber(this)"/>
					</div>
					<div class="fl lineH30">到</div>
					<div id="" class="fl">
						<input type="text" id="searchPropEnd" placeholder="请输入场数" class="prop-search-popup-Cnum2" onkeyup="onlyNumber(this)"/>
					</div>
				</li>
			</ul>
			<div class="prop-search-button">
				<input type="button" id="propSubmitBtn" class="prop-submit-btn" value="确 定" onclick="searchPropSubmit()"/>
				<input type="button" id="propCancelBtn" class="prop-cancel-btn" value="取 消" onclick="searchPropCancel()"/>
				<input type="button" id="propClearBtn" class="prop-clear-btn" value="清 空" onclick="searchPropClear()"/>
			</div>
		</div>
	</div>
	<!--合并道具表-->
	<div class="prop-search-popup-wrap" id="combinePropWin">
		<div class="prop-search-opaticy"></div>
		<div class="prop-search-popup">
			<div class="prop-search-popup-head">服化道合并</div>
			<ul class="prop-search-popup-body">
				<li class="clearfix">
					<div class="fl lineH30">服化道名称 :</div>
					<div id="" class="fl">
						<input type="text" id="combinePropName" placeholder="请输入服化道名称"  class="prop-search-popup-name"/>
					</div>
				</li>
				<li class="clearfix prop-search-type">
					<div class="fl lineH30">类&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;型 :</div>
					<div id="" class="fl">
						<input type="text" id="combinePropType" placeholder="请选择类型" class="prop-search-popup-type" onclick="showCombineSelect()" readonly="readonly"/>
					</div>
					<div class="combine-prop-select-wrap fl" id="combinePropTypeSelect">
						<div class="" onclick="combinePropType(this)">普通道具</div>
						<div class="" onclick="combinePropType(this)">特殊道具</div>
						<div class="" onclick="combinePropType(this)">服装</div>
						<div class="" onclick="combinePropType(this)">化妆</div>
					</div>
				</li>
				<li class="clearfix">
					<div class="fl lineH30">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注 :</div>
					<div id="" class="fl">
						<input type="text" id="combinePropRemark" class="prop-search-popup-remark"/>
						<input type="text" name="" id="" value="" style="display: none;"/>
					</div>
				</li>
			</ul>
			<div class="prop-combine-button">
				<input type="button" id="propSubmitBtn" class="prop-submit-btn" value="确 定" onclick="combinePropInfo()"/>
				<input type="button" id="propCancelBtn" class="prop-cancel-btn" value="取 消" onclick="combinePropCancel()"/>
			</div>
		</div>
	</div>
</body>
</html>