<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/kkpager/kkpager_blue.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/team/teamInfoList.css" />
	
	<link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-loader/loader.min.css" type="text/css" />
	<link rel="stylesheet" href="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.css" type="text/css" />
	<script type="text/javascript" src="<%=basePath%>/js/semantic/semantic-ui-dimmer/dimmer.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/kkpager/kkpager.js"></script>
	<script type="text/javascript" src="<%=path%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/dateUtils.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/team/teamInfoList.js"></script>

  </head>
  
  <body>
    <div class="my-container">
	    <div class="tab-body-wrap">
            <!-- tab键容器 -->
            <div class="btn_tab_wrap">
                <!-- tab键空白处 -->
                <div class="btn_wrap"></div>
                <!-- tab键 -->
                <div class="tab_wrap">
                    <ul>
                        <li id="teaminfo" class="tab_li_current" onclick="showTeamInfo()">组讯</li>
                        <li id="searchteam" onclick="showSearchTeam()">寻组</li>
                    </ul>
                </div>                    
            </div>
        </div>
        <div class="community-public teaminfo-modal">
			<div class="toolbar" id="toolbar">
				<input type='button' class='advance-search-btn'	id='advanceSearchBtn' onclick='openAdvanceSearch()' title="高级搜索">
				<input type='button' class='add-btn' id='addBtn' onclick='addTeamInfo()' title="添加">
				<input type='button' class='delete-btn' id='delBtn' onclick='deleteMulTeamInfo()' title="删除">
			</div>

			<!-- 标题头的table -->
         	<div class="title-table-div">
	          <table class='title-table' cellspacing = 0, cellpadding = 0>
	         	<tr>
	         		<td style='width:50px;'><input type='checkbox' id='checkAll' onclick="checkAll(this)"></td>
	         		<td style='width:calc((100% - 50px) / 11); text-align: left;'>剧组名称</td>
	         		<td style='width:calc((100% - 50px) / 11);'>状态</td>
	         		<td style='width:calc((100% - 50px) / 11); text-align: left;'>招募职位</td>
	         		<td style='width:calc((100% - 50px) / 11);'>开机时间</td>
	         		<td style='width:calc((100% - 50px) / 11);'>距今(天)</td>
	         		<td style='width:calc((100% - 50px) / 11); text-align: left;'>联系电话</td>
	         		<td style='width:calc((100% - 50px) / 11); text-align: left;'>联系地址</td>
	         		<td style='width:calc((100% - 50px) / 11);'>发布时间</td>
	         		<td style='width:calc((100% - 50px) / 11);'>投递简历数</td>
	         		<td style='width:calc((100% - 50px) / 11);'>收藏人数</td>
	         		<td style='width:calc((100% - 50px) / 11);'>创建人</td>
	         	</tr>
	         </table>
        	</div>
        	<div class="team-info-div" id="teamInfoDiv">
	            <table class='team-info-list-table' id="teamInfoList" cellspacing = 0, cellpadding = 0>
	            
	            </table>
        	</div>
        	<!-- 表格分页 -->
			<div class="table-page-div">
			   	<div id="kkpager" style="float: right;margin-right: 60px;"></div>
			</div>
        	<!-- 添加/修改窗口 -->
	       	<div class="right-popup-win" id="rightPopUpWin">
	           	<div class="right-popup-body">
	               	<div class="header-btn-list">
				        <input type="button" id="deleteBtn" value="删除" onclick="deleteTeamInfo()">
				        <input type="button" value="关闭" onclick="closeRightWin()">
				    </div>
				    <div class="tab-body-wrap">
			            <!-- tab键容器 -->
			            <div class="btn_tab_wrap">
			                <!-- tab键空白处 -->
			                <div class="btn_wrap"></div>
			                <!-- tab键 -->
			                <div class="tab_wrap">
			                    <ul>
			                        <li id="basicinfo" class="tab_li_current" onclick="changeTeamInfoTab(this,1)">基本信息</li>
			                        <li id="positioninfo" onclick="changeTeamInfoTab(this,2)">招募职位</li>
			                    </ul>
			                </div>                    
			            </div>
			        </div>
			        <div class="main-content basicinfo-modal" id="basicInfoDiv">
			        	<div class="form-list">
			        	<form id="teamInfoForm" name="teamInfoForm">
			        	<input type="hidden" id="teamId">
			        	 <p class="form-title">基本信息</p>
			             <ul>
			                 <li>
			                     <p>剧组名称&nbsp;:</p>
			                     <input type="text" id="crewName" name="crewName">
			                     <label class="necessory">*</label>
			                 </li>
			                 <li>
			                     <p>剧组类型&nbsp;:</p>
			                     <select id="crewType" name="crewType">
			                     	<option value="1" selected>电视剧</option>
			                        <option value="0">电影</option>
			                        <option value="2">网剧</option>
			                        <option value="3">网大</option>
			                     </select>
			                     <label class="necessory">*</label>
			                 </li>
			                 <li>
			                     <p>拍摄题材&nbsp;:</p>
			                     <select id="subject" name="subject"></select>
			                     <label class="necessory">*</label>
			                 </li>
			             </ul>
			             <ul>
			                 <li>
			                     <p>制片公司&nbsp;:</p>
			                     <input type="text" id="company" name="company">
			                 </li>
			                 <li>
		                       	 <p>导&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演&nbsp;:</p>
		                         <input type="text" id="director">
		                     </li>
			                 <li>
			                     <p>编&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;剧&nbsp;:</p>
			                     <input type="text" id="scriptWriter" name="scriptWriter">
			                 </li>			                 
			             </ul>	
			             <ul>
			                 <li>
			                     <p>开机时间&nbsp;:</p>
			                     <input type="text" id="shootStartDate" name="shootStartDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})" readonly="readonly">
			                 </li>
			                 <li>
		                       	 <p>杀青时间&nbsp;:</p>
			                     <input type="text" id="shootEndDate" name="shootEndDate" onfocus="WdatePicker({isShowClear:true,readOnly:true})" readonly="readonly">
		                     </li>
			                 <li>
			                     <p>拍摄地点&nbsp;:</p>
			                     <input type="text" id="shootLocation" name="shootLocation">
			                 </li>			                 
			             </ul>	
			             <ul>
			                 <li>
			                     <p>联&nbsp;&nbsp;系&nbsp;人&nbsp;:</p>
			                     <input type="text" id="contactName" name="contactName">
			                 </li>
			                 <li>
		                       	 <p>联系电话&nbsp;:</p>
			                     <input type="text" id="phoneNum" name="phoneNum">
		                     </li>
			                 <li>
			                     <p>联系邮箱&nbsp;:</p>
			                     <input type="text" id="email">
			                 </li>			                 
			             </ul>	
			             <ul>
			                 <li class="cross">
			                     <p>筹备地址&nbsp;:</p>
			                     <input type="text" id="contactAddress" name="contactAddress" >
			                 </li>
			             </ul>
			             <ul>
			                 <li class="cross">
			                     <p>剧组简介&nbsp;:</p>
			                     <textarea rows="3" cols="10" id="crewComment" name="crewComment"></textarea>
			                 </li>
			             </ul>
			             <ul>
			                 <li class="cross">
			                 <p>宣传图片&nbsp;:</p>
					             <div class="upload-container">
		                            <div class="select-file-btn" id="uploadFileBtn">添加</div>
                            		<ul class="upload-file-list" id="uploadFileList"></ul>
		                        </div>
		                     </li>
		                 </ul>
			             <ul style="text-align: center;">
			             	<input type="button" class="save-btn" value="保存" onclick="saveTeamInfo()">
			             </ul>
			             </form>
			         </div>
			         <div class="store-list" id="storeListDiv">
			        	 <p>收藏记录</p>
			        	 <div class="store-div">
							<table class="store-table" id="storeTable" cellspacing = 0, cellpadding = 0>
								<tr>
									<th style="width: 34%;">姓名</th>
									<th style="width: 33%;">联系方式</th>
									<th style="width: 33%;">收藏时间</th>
								<tr>
							</table>
						</div>
					 </div>
			        </div>
			        <div class="main-content positioninfo-modal" id="positionInfoDiv" style="display: none;">
			        	<div class="toolbar" style="background: none;padding-left: 53px;">
							<input type='button' class='add-btn' id='addPositionBtn' onclick='addPosition()' title="添加职位">
						</div>
						<div class="position-body">
	                        <div class="position-header-div">
	                            <table class="position-header-table" cellspacing = 0, cellpadding = 0>
	                                <tr>
	                                    <td style="width: 20%; min-width: 20%; max-width: 20%; box-sizing: border-box; padding-left: 5px;">职位名称</td>
	                                    <td style="width: 20%; min-width: 20%; max-width: 20%; box-sizing: border-box; padding-left: 5px;">招募人数</td>
	                                    <td style="width: 30%; min-width: 30%; max-width: 30%; box-sizing: border-box; padding-left: 5px;">职务要求</td>
	                                    <td style="width: 30%; min-width: 30%; max-width: 30%; box-sizing: border-box; padding-left: 5px;">职位意向</td>
	                                </tr>
	                            </table>
	                        </div>
	                        <div class="position-body-div">
	                            <table class="position-body-table" id="positionBodyTable" cellspacing = 0, cellpadding = 0>
	                                <tr class="blank-tr">
	                                	<td colspan="4" style="text-align: center; vertical-align: middle;">暂无数据</td>
	                                </tr>
	                            </table>
	                        </div>
	                        <div class="btn-list">
			                    <input type="button" class="save-btn" value="保存" onclick="savePosition()">
			                </div>
	                    </div>
			        </div>
	           	</div>
	       	</div>
        </div>        
        <div class="ui dimmer body" id="myLoader">
	        <div class="ui large text loader">正在上传，请稍后...</div>
	    </div>
	    
	    <div id="queryWindow" class="searchWindow" style="display: none;">
			<div>高级查询</div>
	    	<div class="Popups_box">
	    		<ul>
	    			<li>
	    				<label>类型：</label>
	    				<label><input name="searchCrewType" value="" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="searchCrewType" value="1" type="radio"/>电视剧</label> &nbsp;&nbsp;
						<label><input name="searchCrewType" value="0" type="radio"/>电影</label> &nbsp;&nbsp;
						<label><input name="searchCrewType" value="2" type="radio"/>网剧</label> &nbsp;&nbsp;
						<label><input name="searchCrewType" value="3" type="radio"/>网大</label>
	    			</li>
	    		</ul>
	    		<ul>
	    			<li>
	    				<label>状态：</label>
	    				<label><input name="searchStatus" value="" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="searchStatus" value="1" type="radio"/>可用</label> &nbsp;&nbsp;
						<label><input name="searchStatus" value="2" type="radio"/>不可用</label>
	    			</li>
	    		</ul>
	    		<ul>
	    			<li>
	    				<label>开机时间：</label>
	    				<label><input name="shootStartType" value="" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="shootStartType" value="1" type="radio"/>最近一个月</label> &nbsp;&nbsp;
						<label><input name="shootStartType" value="2" type="radio"/>最近三个月</label> &nbsp;&nbsp;
						<label><input name="shootStartType" value="3" type="radio"/>最近半年</label>
	    			</li>
	    		</ul>
	    		<ul>
	    			<li>
	    				<label>发布时间：</label>
	    				<label><input name="createTimeType" value="" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="createTimeType" value="1" type="radio"/>一个星期内</label> &nbsp;&nbsp;
						<label><input name="createTimeType" value="2" type="radio"/>最近一个月</label> &nbsp;&nbsp;
						<label><input name="createTimeType" value="3" type="radio"/>最近三个月</label>
	    			</li>
	    		</ul>
	    		<ul>
	    			<li class="search-subject">
	    				<label>题材：</label>
	    				<div id="search-subject-li"></div> 				
	    			</li>
	    		</ul>
	    		<ul class="oper_ul">
					<li>
						<input type='button' value='查询' id='searchSubmit' onclick="queryTeamInfo()"/>&nbsp;&nbsp;
						<input type='button' value='关闭' id='closeSearchSubmit' />&nbsp;&nbsp;
						<input type='button' value='清空' id='clearSearchButton' onclick="clearSearchCon()"/>
					</li>
				</ul>
	    	</div>
	    </div>
    </div>
  </body>
</html>
