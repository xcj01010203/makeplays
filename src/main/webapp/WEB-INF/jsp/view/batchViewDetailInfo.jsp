<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/view/batchViewDetaiInfo.css">

<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/view/batchViewDetaiInfo.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Constants.js"></script>
<title>Insert title here</title>
</head>
<body>
	<input id="sourceForm" type="hidden" value='batchUpdate' />
	<div class="main-comtent-div">
	<span class='tip-info'>勾选需要修改的元素后，再进行内容的修改</span>
	  <p style="position: relative;">
	     <input type="checkbox" id="setShootLocation">
	     <span>设置拍摄地:</span>
	     <input type="text" id="shootLocationInput" class="drop_down input-text-info shoot-location" name="shootLocationInput" disabled="disabled" onkeyup = "checkLocation(this)">
	     <input type="button" class="set-regin-btn" id="setShootReginInfo" value="地域" disabled="disabled">
	     <i id="shootReginInfo"></i>
	  </p>
	  
	  <p>
		  <input type="checkbox" id="specialRemarkIsEnabled">
			<span>特殊提醒：</span>
			<input type="text" id="specialRemarkInfo" class="drop_down input-text-info" name="specialRemark" disabled="disabled" value="">
		</p> 
		
		<p>
		  <input type="checkbox" id="atmoIsEnabled">
			<span>气&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;氛：</span>
			<input type="text" id="atmosphereInfo" class="drop_down input-text-info" name="atmosphere" disabled="disabled" value="">
		</p> 
		
		<p>
		  <input type="checkbox" id="siteInfoIsEnabled">
			<span>内&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;外：</span>
			<input type="text" id="siteInfo" class="drop_down input-text-info" name="site" disabled="disabled" value="" />
		</p>
		
		
		<p>
		  <input type="checkbox" id="statusEnabled">
			<span>拍摄&nbsp;状态：</span>
			<select id="batchStatus" name="status" class="new_input"  style='width: 235px;' disabled="disabled" sval="">
				<option value="" selected="selected">请选择</option>
				<option value="0">未拍</option>
				<option value="3">删戏</option>
			</select>
			<br>
			<span class='tip-info status-tip-info'>场景状态只能在“未拍”与”删戏“之间转换</span>
		</p>
		
		<div class="unify-div">
		    <input type="checkbox" id="unifiedEnabled" onclick="isRepeatViewEnabled(this)">
        <span>统一&nbsp;场景</span>
        <div class="main-content" id="unifiedView" style="display: none;">
          <div class="main-scene-div">
              <input class='search-content-input form-control' type="text" id='searchMainLocation' placeholder='过滤主场景' onkeyup="parent.clicksearchMainLocation(event)"><input type="text" style='display: none;'>
			        <div class='icon_cha1' onclick="parent.searchSameMainLocation()">
			          <img src="../images/find.png">
			        </div>
          </div>
          
          <div class="main-scene-content">
              <!-- 表头 -->
              <div class="main-scene-table-header">
                  <table class="more-scene-table-header" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>主场景</td>
                      <td>次场景</td>
                      <td>三级场景</td>
                      </tr>
                  </table>
              </div>
              <!-- 表体 -->
              <div class="main-scene-table-body">
                  <table class="more-scene-table-body" cellpadding="0" cellspacing="0" id='locationSceneTable'>
                      
                     
                  </table>
              </div>
          </div>
      </div>
		</div>
		<!-- <p>
		  <input type="checkbox" id="viewIsEnabled">
			<span>场&nbsp;&nbsp;&nbsp;&nbsp;景：</span>
			<input type="text" class='batch-location-input input-text-info' id='batchLocationInput' placeholder='请输入需要设置的主场景' disabled="disabled" />
			<input type="button" class='auto-set-button' value="智能并景" id='autoSetViewButton' onclick="showAutoWindow()"/> 
		</p> -->
	</div>
</body>
</html>