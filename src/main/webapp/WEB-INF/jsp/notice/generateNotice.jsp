<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">

<% 
java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
java.util.Date currentTime = new java.util.Date();//得到当前系统时间
String nowDate = formatter.format(currentTime); //将日期时间格式化 

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isNoticeReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.NOTICE_INFO) == 1){
        	isNoticeReadonly = true;
        }
    }
}
%>
<!-- bootstrap -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.base.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/timePicker.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/notice/generateNotice.css">



 <script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/jquery-1.11.1.min.js"></script>

 <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxwindow.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/jqwidgets/jqxpanel.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/generateNotice.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/notice/loadNoticeViewList.js"></script>
  <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=kkz9NgUHHA0yVwnYfgBbqpiB"></script> 
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/TimePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/scripts/ajaxupload.js"></script>
<!-- sweetaltert -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/sweetalert/sweetalert.css">
<script src="<%=basePath%>/js/sweetalert/sweetalert.min.js"></script> 
<!-- 时间控件 -->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
	var isNoticeReadonly=<%=isNoticeReadonly%>;
</script>
</head>

<body>
<input type="hidden" name="noticeId" id="generateNoticeId" value="${noticeId}"/>
<input type="hidden" id="imgStorePath">
<input type="hidden" id="smallImgStorePath">
<div class='body-div'>

	<!-- 通告单标题div -->
	<div class="notice-title-div">
		<div class="title-center-div">
			<p id="noticeTitleContent">
			<input class="notice-date-input" type="hidden" id="noticeTime">
			<input class="notice-group-input" type="hidden" id="noticeGroupSelect">
			<!-- <select class="group-list-select" id="noticeGroupSelect" onchange="changeStaus()" disabled="disabled">
			 
			</select> -->
			</p>
			
			<p class="second-row-p">
				<input class='weather-button' id="getWeatherInfoButton" type="button" value="获取" onclick="getWeather()">
			 <!-- <span class="director-span">导演：<input class="director-name-input" id="directorNameInput" type="text" value="" onkeyup='changeStaus()'><input type='text' style='display: none;' /></span>  -->
			</p>
		</div>
	</div>

	<!-- 通告单拍摄地点时间div -->
	<div class="notice-location-div">
	  <p>
      <span class="shoot-location-span">拍摄地点：<input class="shoot-location-input" id="shootLocationInput" type="text" value="" onkeyup='changeStaus()'><input type='text' style='display: none;' /></span>
      <span class="director-span"><input class="director-name-input" id="directorNameInput" placeholder=' 可填写导演、美术、摄影等主创' type="text" value="" onkeyup='changeStaus()'><input type='text' style='display: none;' /></span>
    </p> 
	 <!-- <p id="notice_location_p">
		     <span class="shoot-location-span">拍摄地点:</span>
		     <span class="shoot-location-map" id="shoot_location_map">
		          
		     </span>
		</p>  -->
		
		<p class="breakfast-time-p">
			<span class="breakfast-time-span">早餐时间：<input class="breakfast-time" id="breakfastTime" type="text" value="" onclick="changeStaus()"/></span>
			<span class="move-start-time" id="moveStartSpan">出发时间：<input type="text" class="satrt-time-input" id='moveStartTime' value="" onkeyup='changeStaus()' onpaste='changeStaus()'><input type='text' style='display: none;' /></span>
		</p>
	</div>
	
	<!-- 通告单内容具体内容div -->
	<div style="text-align: center;">
	  <div>
		<table class="notice-content-table" cellspacing="0" cellpadding="0">
			<tbody>
			<!-- 第一行 -->
			<tr class="first-content-tr">
				<td class="first-content-td" rowspan="2" colspan="6">
					<span class='title-font contact-list-span'>联系表<span class="add-contact-span" onclick="addCrewContact()">添加</span></span>
					<textarea class="crew-contact-div" id="concatInfoList" onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>
					<input type="hidden" id="contactInput" value="">
				</td>
				<!-- 需遍历取值的td -->
				<td class="center-td" id="roleName">角色</td>
				
				<!-- <td class="center-td">李家瑜</td>
				<td class="center-td">林天明</td>
				<td class="center-td">大九</td>
				<td class="center-td">周心妍</td> -->
				
				<td class="border-td" colspan="4" id='advertTextArea'>
					<!-- <span class='title-font advert-title-span'>商植</span>
					<textarea class='advert-content-textarea' onkeyup='changeStaus()'></textarea> -->
				</td>
			</tr>
			
			<!-- 第二行 -->
			<tr>
				<!-- 需遍历取值的td -->
				<td class="center-td" id="actorName">演员</td>
				
				<!-- 群演特约演员信息的td -->
				<td rowspan="3" colspan="4" class="role-content-td">
					<div class="role-actor-div">
						<!-- 具体内容 -->
						<textarea class="concat-info-textarea" id="roleInfo" onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>
					</div>
				</td>
			</tr>
			
			<!-- 第三行 -->
			<tr>
				<td class="border-td" colspan="6">
					<span class="title-font point-info-span">提示</span>
					<textarea class="point-info-content" id="pointInfo" onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>
				</td>
				
				<!-- 需遍历取值的td -->
				<td class="center-td" id="makeupAddress">化妆地</td>
				
				<!-- <td class="center-td"><input class="makeup-info-input" id='makeup_" + roleMap['roleMap']' type="text" value="化妆间"/></td>
				<td class="center-td"><input class="makeup-info-input" type="text" value="化妆间"/></td>
				<td class="center-td"><input class="makeup-info-input" type="text" value="化妆间"/></td>
				<td class="center-td"><input class="makeup-info-input" type="text" value="化妆间"/></td> -->
				
			</tr>
			
			<!-- 第四行 -->
			<tr class="fourth-time-tr">
				<td colspan="6" class="border-td">
					 <span class="title-font remark-info-span">备注<div class='fill-remark-div'></div></span>
					 <textarea class="remark-info-content" id="remarkInfo" onkeyup='changeStaus()' onpaste='changeStaus()'></textarea>
				</td>
				
				<!-- 需遍历取值的td -->
				<td class="four-center-td" id="makeupTime">化妆 <input type="button" class="uniform-button" title="统一设置化妆时间" onclick="uniformArrivTime(event)"></td>
				
				<%-- <td  class="four-center-td"><input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" id="arrive_${role.viewRoleId}" type="text" name="role_arriveTime" class="timepicker" value="06:00"/></td>
				<td  class="four-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td>
				
				<td  class="four-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td>
				
				<td  class="four-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td> --%>
			</tr>
			
			<!-- 第五行 -->
			<tr id="fiveTr">
				<td class="five-special-td" style="width: 52px; min-width: 52px;">
					 <span class="title-font five-special-span">特别</span>
				</td>
				
				<td class="five-viewNo-td" id="scrianceViewNo" style="width: 60px; min-width: 60px;">
					<!--  <span class="title-font five-viewNo-span">集-场</span> -->
				</td>
				
				<td class="five-atmosp-td" style="width: 52px; min-width: 52px;">
					 <span class="title-font five-atmosp-span">气氛</span>
				</td>
				
				<td class="five-pageCount-td" style="width: 52px; min-width: 52px;">
					 <span class="title-font five-pageCount-span">页数</span>
				</td>
				
				<td class="five-view-td">
					 <span class="title-font five-view-span">&nbsp;场景</span>
				</td>
				
				<td class="five-content-td">
					 <span class="title-font five-content-span">内容提要</span>
				</td>
				
				<td class="five-center-td" id="giveMakeupTime">
					 <span class="title-font five-makeuptime-span">出发<input type="button" class="uniform-button"  title="统一设置出发时间" onclick="uniformMakeupValue(event)"></span>
				</td>
				<%-- <td class="five-center-td"><input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" id="givemakeup_${role.viewRoleId}" type="text" name="role_arriveTime" class="timepicker" value="06:00"/></td>
				
				<td  class="five-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td>
				
				<td  class="five-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td>
				
				<td  class="five-center-td">
					<input style="border:1px solid #90CAF9;height: 30px;border-radius:4px;" type="text" name="role_arriveTime" class="timepicker" value="06:00"/>
				</td> --%>
				
				 <td class="five-specialactor-td">
					 <span class="title-font five-specialactor-span">特约</span>
				</td>
				<td class="five-mass-td">
					 <span class="title-font five-mass-span">群演</span>
				</td>
				<td class="five-cloth-td">
					 <span class="title-font five-cloth-span">服化道</span>
				</td>
				<td class="five-backup-td">
					 <span class="title-font five-backup-span">备注</span> 
				</td>
			</tr>
			
			<!-- 第六行 -->
			<%-- <tr>
				<td colspan="4" class="border-center-td"><span class="transfer-info-span" id="${locationViewList.locationId}">拍摄地点：北京大剧院</span></td>
				
				<!-- 需要根据数据长度改变此数值 -->
				<td colspan="7+4" class="border-center-td">
					<span class="title-font transfer-info-span">提示：</span>
					<input class="transfer-point-input" type="text" value="">
				</td>
			</tr> --%>
			
			<!-- 第七行 -->
			<!-- <tr class="seven-tr">
				<td class="border-center-td"><span class='view-content-font'></span></td>
				<td class="border-center-td"><span class='view-content-font'>2-43</span></td>
				<td class="border-center-td"><span class='view-content-font'>日/内</span></td>
				<td class="border-center-td"><span class='view-content-font'>0.3</span></td>
				<td class="content-center-td"><span class='view-content-font'>郊区医院急诊，家瑜被送到病房后</span></td>
				<td class="content-center-td"><span class='view-content-font'>天明抱家瑜进急诊室，进急诊室后，天明去缴费</span></td>
				<td class="blank-time-td"></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>瑜</span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>明</span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="spacial-role-td"><span class='view-content-font'>护士站护士</span></td>
				<td class="mass-role-td"><span class='view-content-font'>护士、医生</span></td>
				<td class="makeup-td"><span class='view-content-font'>收据，家瑜伤妆/脏妆</span></td>
				<td class="backup-content-td"><span class='view-content-font'></span></td>
			</tr>
			
			第八行
			<tr class="seven-tr">
				<td class="border-center-td"></td>
				<td class="border-center-td"><span class='view-content-font'>2-43</span></td>
				<td class="border-center-td"><span class='view-content-font'>日/内</span></td>
				<td class="border-center-td"><span class='view-content-font'>0.3</span></td>
				<td class="content-center-td"><span class='view-content-font'>郊区医院急诊，家瑜被送到病房后</span></td>
				<td class="content-center-td"><span class='view-content-font'>天明抱家瑜进急诊室，进急诊室后，天明去缴费</span></td>
				<td class="blank-time-td"><span class='view-content-font'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>明</span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>心妍</span></td>
				<td class="spacial-role-td"><span class='view-content-font'>护士站护士</span></td>
				<td class="mass-role-td"><span class='view-content-font'>护士、医生</span></td>
				<td class="makeup-td"><span class='view-content-font'>收据，家瑜伤妆/脏妆</span></td>
				<td class="backup-content-td"><span class='view-content-font'></span></td>
			</tr>
			
			第九行
			<tr>
				<td colspan="4" class="border-center-td">
					<span class="transfer-info-span">拍摄地点：北京大剧院</span>
				</td>
				
				需要根据数据长度改变此数值
				<td colspan="11" class="border-center-td">
					<span class="title-font transfer-info-span">提示：</span>
					<input class="transfer-point-input" type="text" value="">
				</td>
			</tr>
			
			第十行
			<tr class="seven-tr">
				<td class="border-center-td"></td>
				<td class="border-center-td"><span class='view-content-font'>2-43</span></td>
				<td class="border-center-td"><span class='view-content-font'>日/内</span></td>
				<td class="border-center-td"><span class='view-content-font'>0.3</span></td>
				<td class="content-center-td"><span class='view-content-font'>郊区医院急诊，家瑜被送到病房后</span></td>
				<td class="content-center-td"><span class='view-content-font'>天明抱家瑜进急诊室，进急诊室后，天明去缴费</span></td>
				<td class="blank-time-td"><span class='view-content-font'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>瑜</span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'>大九</span></td>
				<td class="blank-time-td"><span class='view-content-font margin-content'></span></td>
				<td class="spacial-role-td"><span class='view-content-font'>护士站护士</span></td>
				<td class="mass-role-td"><span class='view-content-font'>护士、医生</span></td>
				<td class="makeup-td"><span class='view-content-font'>收据，家瑜伤妆/脏妆</span></td>
				<td class="backup-content-td"><span class='view-content-font'></span></td>
			</tr> -->
			
			<!-- 最后一行 -->
			<tr id="lastTr" style='text-align: left;'>
			</tr>
			</tbody>
		</table>
		</div>
		<!-- 底部按钮 -->
		<div class="bottom-button-div">
			<button class="save-button save-botton-left" type="button" onclick="closeGenNotice()">&lt;配置场景</button>
			<button class="save-button close-botton-left" type="button" onclick="saveGenerateNotice()">保存</button>
			<button class="save-button publish-button" type="button" id='publishButton' onclick="publishNotice()">发布</button>
		</div>
		
		<!-- 添加场景按钮 -->
		<!-- <div class="add-view-div" id="addNewView" onclick="addViewToNotice()">
			<img alt="" src="../../images/blankedit.png">
		</div> -->
	</div>
  <!-- 联系表弹窗 -->
	<div id="userList" style="display:none;"></div>
	<!-- 统一化妆时间弹窗 -->
	<div class="set-time-div" id="uniformArriveTimeDiv">
	   <!-- <div></div> -->
	   <div style="padding-left: 0px; padding-right: 0px;">
	       <p style="text-align: center;">
	           <input class="time-input" style="background:#fff;" id="arriveTimeInput" type="text" placeholder="例:06:00">
	           <input class="time-button" id="confirmArriveTime" type="button" value="OK" onclick="confirmArriveTime()">
	       </p>
	       
	   </div>
	</div>
	<!-- 统一出发时间弹窗 -->
	<div class="set-time-div" id="uniformMakeupTimeDiv">
	   <!-- <div></div> -->
     <div style="padding-left: 0px; padding-right: 0px;">
         <p style="text-align: center;">
             <input class="time-input" style="background:#fff;" id="makeupTimeInput" type="text" placeholder="例:06:30">
             <input class="time-button" id="confirmMakeupTime" type="button" value="OK" onclick="confirmMakeupTime()">
         </p>
     </div>
	</div>
	
	<!-- 获取天气弹窗 -->
	 <div id="weatherInfoWindow"  style="display:none;">
        <div style="font-size:14px; background-color: #045485;color:#FFFFFF;">获取天气</div>
        <div style="padding:5px;">
            <span>所在城市：</span><input class="cityInp" type="text" style="margin-top:10px;" id="cityInp"><input type='text' style='display: none;' />
            <input type="button" id="obtainWeather" class="obtainWeatherBtn" value="获取">
        </div>
    </div>
    
    <!-- <div id="aa"  style="display:none;">
        <div>详细位置</div>
        <div>
            <div class="map-con-div">
                <div class="search-address-div">
                    
                    <p><span style="color: #f00;">*</span>&nbsp;请用搜索框搜索地址并拖动红色标志进行精确定位</p>
                    <input type="button" value="保存并关闭" onclick="saveLocation()">
                </div>
               <div id="r-result" style='position: absolute; top: 43px; left: 30px; z-index: 999; width: 30%;'>
                  <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="搜索地点" style="width:100%;" />
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%;height: calc(100% - 30px);"></div>
              </div>
        </div>
    </div> -->
    
    
    <!-- 百度地图弹窗 -->
      <div id="baiduMapWindow" style="display: none;">
        <div>详细位置</div>
        <div>
            <div class="map-con-div">
                <div class="search-address-div">
                    
                    <p><span style="color: #f00;">*</span>&nbsp;请用搜索框搜索地址并拖动红色标志进行精确定位</p>
                    <input type="button" value="保存并关闭" onclick="saveLocation()">
                </div>
               <div id="r-result" style='position: absolute; top: 43px; left: 30px; z-index: 999; width: 30%;'>
                  <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="搜索地点" style="width:100%;" />
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%;height: calc(100% - 30px);"></div>
              </div>
        </div>
    </div>   
    <!-- 弹窗遮罩 -->
    <!-- <div class="win-modal-zindex" id="winModalZIndex"></div>
    
     <div class="baidu-map-win" id="baiduMapWindow" style="display: none;">
        <div class="map-win-title">
            <p class="map-win-tips">详细位置</p>
            <input class="map-win-close" type="button" onclick="closeMapWin()">
        </div>
        <div style="height: calc(100% - 25px);">
            <div class="map-con-div">
                <div class="search-address-div">
                    
                    <p><span style="color: #f00;">*</span>&nbsp;请用搜索框搜索地址并拖动红色标志进行精确定位</p>
                    <input type="button" value="保存并关闭" onclick="saveLocation()">
                </div>
                <div id="r-result" style='position: absolute; top: 43px; left: 30px; z-index: 999; width: 30%;'>
                  <input class="map-search-input" type="text" id="suggestId" size="100"  placeholder="搜索地点" style="width:100%;" />
                </div>
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
                <div id="allmap" style="width:100%;height: calc(100% - 30px);"></div>
              </div>
        </div>
    </div>  -->
    
    
    
   <div id="jqxNotification"></div>
</div>

</body>
</html>