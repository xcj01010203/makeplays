<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	Object isScenarioReadonly = false;
	Object isViewInfoReadonly = false;
	Object obj = session.getAttribute("userAuthMap");

	if(obj!=null){
	    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.SCENARIO_ANALYSE)) {
		    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.SCENARIO_ANALYSE) == 1){
		    	isScenarioReadonly = true;
		    }
	    }
	    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.VIEW_INFO)) {
		    if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.VIEW_INFO) == 1){
		    	isViewInfoReadonly = true;
		    }
	    }
	}
	String flag = request.getParameter("flag");
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

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/scenarioAnalysis.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/viewDetailInfo.css?version=20170726">
<!-- sweetaltert -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">
<script src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script> 

<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/view/viewDetaiInfo.js?version=20170726"></script>
<script type="text/javascript" src="<%=path%>/js/Constants.js"></script>
<script type="text/javascript" src="<%=path%>/js/HashMap.js"></script>
<script type="text/javascript">
var isScenarioReadonly = <%=isScenarioReadonly%>;
var isViewInfoReadonly = <%=isViewInfoReadonly%>;
var parentFromFlag = '<%=flag%>';
</script>

</head>
<body id="viewDetailBody">
	<input type="hidden" value="${viewId}" id="viewDetailId" />
<form id="viewDetailForm" action="">
	<input type="hidden" value="" name="saveType" id="saveType" />
	<input type="hidden" value="" name="shootStatus" id="shootStatus"> 
	<input type="hidden" value="" name="isManualSave" id="isManualSave">
	<input type="hidden" value="0" id="isChanged">

	<div class="right_add_tb" id="rightAddDiv">
		<table id="shootStatusTable">
			<tbody>

				<table id="otherShootTables">
					<tr>
						<td><span class="groupTag">基本信息：</span></td>
					</tr>

					<!-- 如果为电影类型剧组，则不展示集号 -->
					<tr id="noFinishFilmTr">
						<td class="film-view-no-td">
							<em class="must">*</em>场次：
						</td>
						<td align="left">
							<input type="hidden" id="NoFinishFilmViewid" name="viewId" value="" /> 
							<input type="text" class="scene_set hidden-element" id='noFinishFilmSeriesNo' name="seriesNo" value="1" />
							<input type="text" class="scene_field movie_scene" id="noFinishFilmViewNo" name="viewNo" value="" /> 
							<span>页数：</span>
							<input type="text" id="noFinishFilmPageCount" class="scene_page" name="pageCount" value="" />
					    </td>
					</tr>
					<tr id="noFinishTvbTr">
						<td class="film-view-no-td">
							<em class="must">*</em>集-场：
						</td>
						<td align="left">
							<input type="hidden" id="noFinishTvbViewId"	name="viewId" value="" /> 
							<input type="text"	id="noFinishTvbSeriesNo" class="scene_set" name="seriesNo"	value="" />
							 - 
							<input type="text" id="noFinishTvbViewNo"	class="scene_field" name="viewNo" value="" /> 
							<span>页数：</span> 
							<input type="text" id="noFinishTvbPageCount" class="scene_page"	name="pageCount" value="" />
						</td>
					</tr>
					<tr>
						<td>气氛/内外：</td>
						<td align="left">
							<input type="text" id="noFinishViewAtmosphere" class="scene_aura drop_down"	name="view" value="" />
						 	/ 
						 	<input type="text" id="noFinishViewSite" class="scene_location drop_down" name="site" value="" />
						 </td>
					</tr>
					<tr>
						<td class="scene_vertical_top">特殊提醒：</td>
						<td>
							<input type="text" id="noFinishSpecialRemind" class="special_remind drop_click special-remind" name="specialRemind" value="" placeholder="请输入文武特效、季节等特殊信息"/>
							<!-- <p class="special-modole">请输入文武特效、季节等特殊信息</p> -->
						</td>
					</tr>
					<!-- <tr>
						<td class="scene_vertical_top">季节：</td>
						<td>
							<select name="season" id="noFinishViewSeason" sval="">
								<option value="">请选择</option>
								<option value="春">春</option>
								<option value="夏">夏</option>
								<option value="秋">秋</option>
								<option value="冬">冬</option>
							</select>
						</td>
					</tr> -->
					<tr>
						<td>主要内容：</td>
						<td>
							<input type="text" id="noFinishMainContent"	class="scene_content" name="mainContent" value="" />
						</td>
					</tr>
					<tr>
						<td><span class="groupTag">场 景：</span></td>
					</tr>
					<tr>
						<td>主场景：</td>
						<td>
							<input type="text" id="noFinishFirstLocation" class="scene_first drop_click" name="firstLocation" value="" />
						</td>
					</tr>
					<tr>
						<td>次场景：</td>
						<td>
							<input type="text" id="noFinishSecondLocation"	class="scene_second drop_click" name="secondLocation" value="" />
						</td>
					</tr>
					<tr>
						<td>三级场景：</td>
						<td>
							<input type="text" id="noFinishThirdLocation" class="scene_third drop_click" name="thirdLocation" value="" />
						</td>
					</tr>
					<tr>
						<td>拍摄地：</td>
						<td style="text-align: left;">
							<input type="text" id="noFinishViewShootLocation" class="shoot_location" name="shootLocation" value="" />
						</td>
					</tr>
					<tr>
					   <td>
					     <input type="button" class="set-shootRegin" value="地域">
					   </td>
					   <td>
					       <p class="shoot-regin-info" id="shootReginValue"></p>
					   </td>
					</tr>
					<tr>
						<td><span class="groupTag">人 物：</span></td>
					</tr>
					<tr>
						<td class="scene_vertical_top">主要演员：</td>
						<td>
							<div class="tagWrap performer_first">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewMajorActor" sv="" /> 
										<!-- <span class="hidden-span"></span> -->
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishMajorActorUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td class="scene_vertical_top">特约演员：</td>
						<td>
							<div class="tagWrap performer_special">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewGuestActor" sv="" /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishGuestActorUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td class="scene_vertical_top">群众演员：</td>
						<td>
							<div class="tagWrap performer_common">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewMassActor" sv="" /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishMassActorUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td><span class="groupTag">服、化、道：</span></td>
					</tr>
					<tr>
						<td class="scene_vertical_top">服装：</td>
						<td>
							<div class="tagWrap clothes_info">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewClothes" sv="" /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishClothesUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td class="scene_vertical_top">化妆：</td>
						<td>
							<div class="tagWrap makeup_info">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewMakeups" sv="" /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishMakeupsUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td class="scene_vertical_top">道具：</td>
						<td>
							<div class="tagWrap tool_main">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewCommonProps" sv="" /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishCommonPropsUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td class="scene_vertical_top">特殊道具：</td>
						<td>
							<div class="tagWrap tool_special">
								<ul class="f_l">
									<li class="tagInput">
										<input class="make-width-104" type="text" id="noFinishViewSpecialProps" sv="" style='color:grey' /> 
										<span class="hidden-span"></span>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="clear"></div>
							</div>
							<div class="tagWrap_popup">
								<div class="trigonUp"></div>
								<a class="closePopup" href="javascript:void(0)"></a>
								<ul id="noFinishSpecialPropsUl">
								</ul>
							</div>
						</td>
					</tr>
					<tr>
						<td><span class="groupTag">其 他：</span></td>
					</tr>
					<tr>
						<td class="scene_vertical_top">拍摄状态：</td>
						<td>
							<input class="mark-grey" type="text" id="noFinishViewShootStatus" value="" readonly />
						</td>
					</tr>
					<tr>
						<td>备注：</td>
						<td>
							<input type="text" id="noFinishViewRemark" class="scene_remark" name="remark" value="" />
							 <!--主要演员--> 
							 <input type="hidden" name="majorActor" class="performer_first" /> 
							 <!--特约演员-->
							<input type="hidden" name="guestActor" class="performer_special" />
							<!--群众演员--> 
							<input type="hidden" name="massesActor" class="performer_common" /> 
							<!--普通道具-->
							 <input type="hidden" name="commonProps" class="tool_main" /> 
							 <!--特殊道具--> 
							 <input type="hidden" name="specialProps" class="tool_special" /> 
							 <!-- 服装 -->
							<input type="hidden" name="clothes" class="clothes_info" />
							 <!-- 化妆 -->
							<input type="hidden" name="makeups" class="makeups_info">
							<input type="hidden" name="shootRegion" class="shoot_regin_info">
						</td>
					</tr>
				</table>
			</tbody>
		</table>
		<div class="scene_vertical_top" id="insertAdvert">
			<span class="groupTag">广告植入：</span>
			<table id="advertInfoTable">
			</table>
			<br>
			<div id="addOneAdvert">
				<dl id="addAdvertTitle" onclick="addAdvertTitle()">
					添加一条广告
				</dl>
				<label class="advert-button-lable" id="cancelAddBtn" onclick="cancleAddAdvert(event)">取消</label>
				<label class="advert-button-lable" id="advertAddBtn" onclick="confirmAddAdvert(event)">添加&nbsp;&nbsp;&nbsp;</label>
				<div id="advertForm">
					<table class="advert-table">
						<tr>
							<td class="advert-name-td">
								<em class="must">*</em>广告名称：
							</td>
							<td>
								<input id="advertName" type='text' class="drop_click">
							</td>
						</tr>
						<tr class="advert-type-tr">
							<td>广告类型：</td>
							<td>
								<div id="advertType">
									<input type="radio" name="advertTypeRedio" value="1" checked />
									道具
									<input type="radio" name="advertTypeRedio" value="2" />
									台词 
									<input type="radio" name="advertTypeRedio" value="3" />
									场景
									<input type="radio" name="advertTypeRedio" value="99" />
									其他
								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</form>
</body>
</html>
