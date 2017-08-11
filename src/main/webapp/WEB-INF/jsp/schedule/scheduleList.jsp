<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isScheduleReadonly = false;
Object hasImportScheduleAuth = false;
Object hasExportScheduleAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_SCHEDULE)) {
	    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_SCHEDULE) == 1){
	    	isScheduleReadonly = true;
	    }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_SCHEDULE) != null){
    	hasImportScheduleAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_SCHEDULE) != null){
    	hasExportScheduleAuth = true;
    }
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html onselectstart="return false">
  <head>
    <base href="<%=basePath%>">    
    <title></title>
    <!-- bootstrap CSS -->
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
		
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/exportLoading.css">
	  <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/schedule/scheduleList.css" />
	   
	   <!-- bootstrap JS -->
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
		
    <script type="text/javascript" src="<%=basePath%>/js/schedule/scheduleList.js"></script>
    <script>
	    var isScheduleReadonly = <%=isScheduleReadonly%>;
	    var hasImportScheduleAuth = <%=hasImportScheduleAuth%>;
	    var hasExportScheduleAuth = <%=hasExportScheduleAuth%>;
	</script>
  </head>
  
  <body onselectstart="return false">
      
  <!-- 计划编辑 -->
	    <div class="my-container" id="myContainer">
	    
			    <!-- tab键 -->
		      <div class="tab-wrap">
		          <ul class="tab-btn">
		              <li onclick="viewPlan()">查看计划</li>
		              <li onclick="planDetail()">计划详情</li>
		              <li id="editPlanTab" class="tab_li_current" onclick="editedPlan()">编辑计划</li>
		          </ul>
		      </div>
	    
	    
		      <div class="left-panel collapse-left-panel" id="leftPanel">
			        <div class="panel-title">
			             <span class="border-left"></span>
			             <span>场景分组</span>
			        </div>
			        <div class="left-panel-content">
				          <div class="extend-btn">
				              <input type="button" class="click-all" id="queryAllView" value="全部分组" onclick="queryAllView(this)">
				              <input class="extend-coll-btn collapse" type="button" onclick="extendOrCollapse(this)">
				          </div>
				          <div class="group-container">
				              <iframe id="groupIframe" name="groupIframe"></iframe>
				          </div>
			        </div>
		      </div>
		      <div class="right-panel collapse-right-panel" id="rightPanel">
			        <div class="panel-title">
			           <span class="border-left"></span>
		                <span id="rightPanelTitle">全部分组</span>
		                <p id="viewSummary">场景统计:<span id="statisticsViewCount">30</span>场&nbsp;&nbsp;&nbsp;&nbsp;<span id="statisticsPageCount">20</span>页&nbsp;&nbsp;内<span id="inSite">15</span>场&nbsp;&nbsp;&nbsp;&nbsp;外<span id="outSite">15</span>场&nbsp;&nbsp;&nbsp;&nbsp;
		                                                内外<span id="statisticsSite">0</span>场&nbsp;&nbsp;&nbsp;&nbsp;完成<span>0</span>场&nbsp;&nbsp;&nbsp;&nbsp;部分完成<span>0</span>场&nbsp;&nbsp;&nbsp;&nbsp;删戏<span>0</span>场</p>
			        </div>
		          <div class="right-panel-content">
				          <div class="toolbar">
					            <span class="count-flag">0</span>
					            <!-- <label><input type="checkbox" id="selectAll" title="全选" onclick="selectAll(this)">全选</label> -->
					            <button class="select-btn" id="selectAll" title="全选" onclick="selectAll(this)" disabled="disabled">全选</button>
					            <button class="select-btn" id="reverseSelect" title="反选" onclick="reverseSelect(this)" disabled="disabled">反选</button>
					            <input class="filter-btn" type="button" title="筛选" onclick="superSearchViewInfo()">
					            <input class="lock-btn" type="button" title="锁定" onclick="lockSelectRow()">
					            <input class="repeat-modify" type="button" title="批量修改" onclick="showRepeatWin()">
					            <input class="move-to" type="button" title="移动到" onclick="showMoveToWin()">
					            <input class="add-notice" type="button" title="添加到通告单" onclick="addViewToNotice()">
					            <input class="hide-columns" type="button" title="隐藏列" onclick="showColumnsPanel(this, event)">
					            
					            <div class="status-btn-list">
					               <span class="loading-flag" id="loadingFlag"></span>
					               
					               <input type="button" class="click-finish checked" id="queryFinishView" value="完成" onclick="queryView(this,2)">
					               <input type="button" class="half-finish checked" id="queryHalfView" value="部分完成" onclick="queryView(this,1)"> 
					               <input type="button" class="delete-view checked" id="querydeleteView" value="删戏" onclick="queryView(this,3)">
					            </div>
				          </div>
				          <!--计划列表-->
				          <div class="view-list" id="viewList"></div>
		          </div>
		     </div>
		     
		     <!-- 智能整理窗口 -->
		     <div class="jqx-window" id="intelligentWin">
		        <div>智能整理</div>
                 <div class="jqx-content">
                     <p>请选择智能排期的条件依据</p>
                     <ul class="finish-condition">
                         <li>
                             <table>
                                 <tr>
                                     <td>首要条件:</td>
                                     <td><p class="tips">条件为主要演员时可用</p></td>
                                 </tr>
                                 <tr>
                                     <td>
                                        <select id="conditionFirst" onchange="setConditionFirst(this)">
                                            <option value="">未选择</option>
                                           <!--  <option value="shootLocation">拍摄地</option> -->
                                            <option value="shootLocation">实景地域</option>
                                            <option value="viewRole">主要演员</option>
                                        </select>
                                     </td>
                                     <td>
                                        <div class="role-select" id="mainRoleListOne"  onchange="mainRoleOne(event)">
                                            
                                        </div>
                                     </td>
                                 </tr>
                             </table>
                         </li>
                         <li>
                             <table>
                                 <tr>
                                     <td>次要条件:</td>
                                     <td></td>
                                 </tr>
                                 <tr>
                                     <td>
                                        <select id="conditionSecond" onchange="setConditionSecond(this)">
                                            <option value="">未选择</option>
                                            <!-- <option value="shoot_location">拍摄地</option> -->
                                            <option value="shootLocation">实景地域</option>
                                            <option value="viewRole">主要演员</option>
                                        </select>
                                     </td>
                                     <td>
                                        <div class="role-select" id="mainRoleListTwo"  onchange="mainRoleTwo(event)">
                                            
                                        </div>
                                     </td>
                                 </tr>
                             </table>
                         </li>
                     </ul>
                     <div class="win-btn-list">
                         <input type="button" value="确定" onclick="confirmIntelligent()">
                         <input type="button" value="清空" onclick="clearIntellWin()">
                         <input type="button" value="取消" onclick="cancelBtn()">
                     </div>
                 </div>
		     </div>
		     
		     <!-- 批量修改 -->
		     <div class="jqx-window" id="repeatModify">
		        <div>批量修改</div>
		        <div class="jqx-content">
		            <ul class="set-condition">
		                <li>
		                    <p><label><input type="checkbox" id="planDateEnable" onclick="isplanDateEnable(this)">设置计划拍摄日期(所选日期:<span id="dateRange"></span>)</label></p>
		                    <p class="condition-content">
		                        <label><input type="radio" value="0" name="setPlanDate" disabled="disabled" onclick="isShootDate(this)">计划日期:</label>
		                        <input type="text" id="shootDate" disabled="disabled" onfocus="WdatePicker({isShowClear:true,readOnly:true, onpicked:autoGetNoticeName})">
		                        <input type="text" style="display: none">
		                        
		                    </p>
		                    <p class="condition-content">
		                        <label><input type="radio" value="1" name="setPlanDate" disabled="disabled" onclick="isAdjustDate(this)">调整日期:</label>
		                        <button class="date-icon" onclick="substractDays(this)" disabled="disabled">-</button>
		                        <input type="text" class="only-num" id="adjustDate" disabled="disabled" onkeyup="onlyNumber(this)">
		                        <button class="date-icon2" onclick="addDays(this)" disabled="disabled">+</button>
		                    </p>
		                    <p class="tips"><i>正数为“延后”天数，负数为“提前”天数</i></p>
		                </li>
		                <li>
		                    <p><label><input type="checkbox" id="planGroupEnable" onclick="isPlanGroupEnable(this)">设置计划拍摄组别(所选组别:<span class="show-group" id="selectplanGroup">单组</span>)</label></p>
		                    <div class="condition-group">
		                        <label>&nbsp;&nbsp;&nbsp;组&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别:</label>
		                        <div class="shoot-group">
		                            <div id="setShootGroup" onchange="changeNoticeGroup(event)"></div>
		                        </div>
		                    </div>
		                    
		                </li>
		            </ul>
		            <div class="shoot-group-list" id="shootGroupList"></div>
		            <div class="win-btn-list">
		                <input type="button" value="确定" onclick="confirmRepeatSet()">
		                <input type="button" value="取消" onclick="cancelRepeatSet()">
		            </div>
		        </div>
		     </div>
		     
		     <!-- 移动到 -->
		     <div class="jqx-window" id="moveToWin">
		        <div>移动到</div>
		        <div class="jqx-content">
		            <ul class="remove-ul">
		                <li>
		                    <p>分组名称:</p>
		                    <div class="plan-group-list" id="planGroupList"></div>
		                </li>
		            </ul>
		            <div class="win-btn-list">
		                <input type="button" value="确定" onclick="moveToGroup()">
		                <input type="button" value="取消" onclick="closeMoveToWin()">
		            </div>
		        </div>
		     </div>
		     
		     
		     <!-- 筛选 -->
		     <div id="searchWindow" class="jqx-window">
					    <div id="customWindowHeader">
					        <span class="capture-search-span" id="captureContainer">高级查询 </span>
					    </div>
					    <div id="dropDownDIV" class='Popups_box container'>
					        <div class="classify">基本信息：</div>
					           <ul class="searchUl">
						            <li id="moviceSectionNo">
							             <label>场次区间：</label>
							             <input class="hidden-tag" type='text' name='startSeriesNo' id='startSeriesNo'>
							             <input class="view-sction-input" type='text' name='startViewNo' id='startViewNo'>到
							             <input class="hidden-tag" type='text' name='endSeriesNo' id='endSeriesNo'>
							             <input class="view-sction-input" type='text' name='endViewNo' id='endViewNo'>
						            </li>
					            
						            <li id="tvbSctionNo">
								             <label>集场区间：</label>
								             <input class="series-view-section" type='text' name='startSeriesNo' id='startSeriesNo'>
								             -
								             <input class="series-view-section" type='text' name='startViewNo' id='startViewNo'>
								                              到
								             <input class="series-view-section" type='text' name='endSeriesNo' id='endSeriesNo'>
								             -
								             <input class="series-view-section" type='text' name='endViewNo' id='endViewNo'>
						            </li>
					             
					           </ul>
					              
					              <ul class='searchUl'>
					                  <li id="tvbSeriesNoLi">
					                     <label>集场编号：</label>
					                     <input type="text" id="seriesViewNos">
					                  </li>
					                  
					                  <li id="moviceViewNos">
					                       <label>场次编号：</label>
					                       <input type="text" id="viewNos">
					                  </li>
					              </ul>
					           
					              <ul class='searchUl'>
					                  <li>
					                     <label>特殊提醒：</label>
					                     <select id="specialRemindSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
					                   </li>
					              </ul>
					  
					            <ul class='searchUl'>
					                <li>
					                    <label>气&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;氛：</label>
					                    <select id="atmosphereSelect" class="selectpicker show-tick" multiple>
					                    <option value="blank">[空]</option>
					                    </select>
					                <input type="hidden" class="preValue">
					                </li>
					            </ul>
					  
					           <ul class='searchUl'>
					              <li>
					                 <label>内&nbsp;外&nbsp;&nbsp;景：</label>
					                    <select id="siteSelect" class="selectpicker show-tick" multiple>
					                           <option value="blank">[空]</option>
					                    </select>
					                    <input type="hidden" class="preValue">
					              </li>
					           </ul>
					           
					           <ul class='searchUl'>
					              <li>
			                     <label>拍摄状态：</label>
			                     <select id="shootStatusSelect" class="selectpicker show-tick" multiple>
			                     
			                     </select>
			                     <input type="hidden" class="preValue">
					               </li>
					           </ul>
					              
					              <ul class='searchUl'>
					                  <li>
					                     <label>商&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;植：</label>
					                     <select id="advertInfoSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
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
					                     <input type="text" id="viewRemark" autocomplete="off" />
					                     <input style="display:none"><!-- for disable autocomplete on chrome -->
					                 </li>
					             </ul>
					             
					             <div class="classify" style="width: 235px;">场&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;景：</div>
					             <ul class='searchUl'>
					                 <li>
					                     <label>拍摄地点：</label>
					                     <select id="shootLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
					                 </li>
					             </ul>
					             
					             <ul class='searchUl'></ul>
					             
					             <ul class='searchUl'>
					                 <li>
					                     <label>主&nbsp;&nbsp;场&nbsp;景：</label>
					                     <select id="firstLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
					                 </li>
					             </ul>
					             
					             <ul class='searchUl'>
					                 <li>
					                     <label>次&nbsp;&nbsp;场&nbsp;景：</label>
					                     <select id="secondLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
					                 </li>
					             </ul>
					             
					             <ul class='searchUl'>
					                 <li>
					                     <label>三级场景：</label>
					                     <select id="thirdLocationSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                         <option value="blank">[空]</option>
					                     </select>
					                     <input type="hidden" class="preValue">
					                     <a class="clearSelection clear-a-selection">清空</a>
					                 </li>
					             </ul>
					          <ul class='searchUl'></ul>
					          
					             <div class="classify">演&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;员：</div>
						          <ul class='searchUl'>
						             <li>
						                <label>主要演员：</label>
						                   <select id="majorRoleSelect" class="selectpicker show-tick" multiple data-live-search="true" onchange="changeMajorRole(event)">
						                          <option value="blank">[空]</option>
						                   </select>
						                      <input type="hidden" class="preValue">
						                      <a class="clearSelection clear-a-selection">清空</a>
						             </li>
						          </ul>
					          
						          <ul class='searchUl'>
						             <li id="searchModeLI">
						                  <dd class="anyone-appear-dd" id="anyOneAppear">
						                    <input name="searchMode" value="1" type="radio" checked="checked"> 出现即可
						                  </dd>
						              
						                  <dd class="other-appear-dd" id="noOneAppear" >
						                    <input name="searchMode" value="3" type="radio"> 不出现
						                  </dd>
						                
						                  <dd class="other-appear-dd" id="everyOneAppear">
						                    <input name="searchMode" value="0" type="radio">  同时出现
						                  </dd>
						              
						                  <dd class="other-appear-dd" id="notEvenyOneAppear">
						                    <input name="searchMode"  value="2" type="radio"> 不同时出现
						                  </dd>
						              
						             </li>
						          </ul>
					             
				             <ul class='searchUl'>
				                 <li>
				                    <label>特约演员：</label>
				                    <select id="guestRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
				                        <option value="blank">[空]</option>
				                    </select>
				                    <input type="hidden" class="preValue">
				                    <a class="clearSelection clear-a-selection">清空</a>
				                 </li>
				             </ul>
					          
						          <ul class='searchUl'>
						             <li>
						                <label>群众演员：</label>
						                   <select id="massRoleSelect" class="selectpicker show-tick" multiple data-live-search="true">
						                          <option value="blank">[空]</option>
						                   </select>
						                      <input type="hidden" class="preValue">
						                      <a class="clearSelection clear-a-selection">清空</a>
						             </li>
						          </ul>
					             
					             
				             <div class="classify">服&nbsp;&nbsp;化&nbsp;道：</div>
				             <ul class='searchUl'>
				                 <li>
				                    <label>服&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;装：</label>
				                    <select id="clothSelect" class="selectpicker show-tick" multiple data-live-search="true">
				                        <option value="blank">[空]</option>
				                    </select>
				                    <input type="hidden" class="preValue">
				                    <a class="clearSelection clear-a-selection">清空</a>
				                 </li>
				             </ul>
					             
				             <ul class='searchUl'>
				                 <li>
				                    <label>化&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;妆：</label>
				                    <select id="makeupSelect" class="selectpicker show-tick" multiple data-live-search="true">
				                        <option value="blank">[空]</option>
				                    </select>
				                    <input type="hidden" class="preValue">
				                    <a class="clearSelection clear-a-selection">清空</a>
				                 </li>
				             </ul>
					          
					          <ul class='searchUl'>
					           <li>
					              <label>道&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;具：</label>
					                 <select id="propSelect" class="selectpicker show-tick" multiple data-live-search="true">
					                        <option value="blank">[空]</option>
					                 </select>
					                    <input type="hidden" class="preValue">
					                    <a class="clearSelection clear-a-selection">清空</a>
					           </li>
					          </ul>
					             
				             <ul class='searchUl'>
				                 <li>
				                    <label>特殊道具：</label>
				                    <select id="specialPropSelect" class="selectpicker show-tick" multiple data-live-search="true">
				                        <option value="blank">[空]</option>
				                    </select>
				                    <input type="hidden" class="preValue">
				                    <a class="clearSelection clear-a-selection">清空</a>
				                 </li>
				             </ul>
					          <!-- <ul class='searchUl'>
					          
					           <li>
					            <label>排序方式：</label>
					            <input type='radio' name='sortFlag' id='sortFlag' value='1' checked/>正序&nbsp;
					            <input type='radio' name='sortFlag' id='sortFlag' value='2'>倒序
					           </li>
					          </ul> -->
					          <ul style="width:100%;text-align:center;padding:0px 0px;">
						           <li class="search-button-li">
							            <input type='button'  value='查询' id='searchSubmit' style="margin-top:10px;" onclick="confirmSearchViewInfo()">
							            &nbsp;&nbsp;&nbsp;&nbsp;
							            <input type='button'  value='关闭' id='closeSearchSubmit'>
							            &nbsp;&nbsp;&nbsp;&nbsp;
							            <input type='button'  value='清空' id='clearSearchButton' onclick="clearSaerchContent()">
						            </li>
					          </ul>
					    </div>
					  </div>
		        
		        <!-- 添加到通告单窗口 -->
		        <div class="hidden-tag" id="customWindow">
		            <div>
			              <div class="over-flow-hidden" id="noticeDivForm">
			                  <div>新建通告单</div>
			                  <div>
					                  <form id="noticeForm" action="/notice/noticeSave">
					                       <table>
					                           <tr>
					                               <td class="nameLabel">名称：</td>
					                                     <td><input class="notice-name-input" type="text" name="noticeName" id="noticeNameInput" ></td>
					                           </tr>
			                               <tr>
			                                   <td class="nameLabel">时间：</td>
			                                   <td><input class="notice-time-input" type="text" id="noticeTime" onFocus="WdatePicker({isShowClear:false,readOnly:true, onpicked:autoGetNoticeName})"></td>
			                               </tr>
			                               <tr>
			                                   <td class="nameLabel">分组：</td>
			                                   <td><div id="group" onchange="changeGroupInfo(event)"></div></td>
			                               </tr>
			                               <tr>
				                               <td colspan="2"><div class="add-notice-error-div" id="addNoticeError"></div></td>
				                             </tr>
					                        </table>
					                             <div class="save-notice-div">
					                                  <input class="save-notice-button" type="button" value="确定" id="saveNoticeButton" onclick="saveNoticeInfo()">
					                                  <input type="button" value="关闭" class="noticeCancelButton">
					                             </div>
					                    </form>
			                    </div>
			                </div>  
				              <div class="notice-list-div" id="noticeDivList">
				                 <div>选择已有通告单</div>
				                 <div>
				                   <div id="existNoticeGrid"></div>
					                     <div class="exist-notice-div">
					                         <input type="button" value="确定" style='margin-top: 10px;' id="addToNoticeButton" onclick="confirmAddToNotice()"> 
					                         <input type="button" value="关闭" class="noticeCancelButton">
					                     </div>
				                 </div>
				              </div>
		              </div>
		          </div>
		        
		        
		        <!--快捷菜单-->
			      <div class="short-menu" id="shortCutMenu">
			        <ul>
			          <li onclick="cutTrData()">剪切</li>
			          <li onclick="pasteTrData()">粘贴</li>
			        </ul>
			      </div>
			      
			      <!-- 隐藏列面板 -->
			      <div class="columns-panel" id="columnsPanel" onclick="objEvent(event)">
			         <ul id="columnsPanelItem">
			         </ul>
			      </div>
		        
		        
		     
		        <!-- 正在加载 -->
			      <div class="opacityAll" style="opacity: 0.45; display: none; position: absolute; top: 0px; left: 0px; z-index: 18000;cursor: wait;"></div>
			      <div id="loadingDataDiv" class="show-loading-container" style="display: none;">
			          <div class="show-loading-div"> 正在加载数据，请稍候... </div>
			      </div>
		        <div id="loadingDiv" class="show-loading-container">
		            <div class="show-loading-div"> 正在生成下载文件，请稍候... </div>
		        </div>
	    </div>
	    
	    <!-- 导入窗口 -->
	    <div id="importPlanWin" class="jqx-window">
          <div>导入</div>
          <div>
              <iframe id="importIframe" width="100%" height="100%"></iframe>
           </div>
      </div>
	    
      
      <!-- 计划查看\计划详情 -->
      <div class="hidden-wrap" id="viewPlanInfo">
          <iframe name="viewPlanDetail" id="viewPlanDetail"></iframe>
      </div>
      
      
  </body>
</html>
