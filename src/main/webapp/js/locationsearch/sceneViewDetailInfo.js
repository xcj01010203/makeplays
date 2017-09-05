var subData={};
var alreadySceneViewId;
var sceneViewId = {};
var shootRegionList;//所有地域信息
$(document).ready(function(){
	//初始化文件上传
	if($("#where").val() == "prepare"){//是否是筹备管理的堪景
		if(!isReadonly){
			initFileUpload();
		}
	}else{//拍摄管理的堪景
		if(!isSceneViewReadonly) {
			initFileUpload();
		}
	}

	//获取所有地域信息
	queryAllRegionInfo();
	//加载未配置场景列表主要演员
	loadNoneConfigMainActorList();
	
	//改景和道具陈设的默认值
	$("#isModifyView").val(1);
	$("#modifyViewCost").attr("disabled", true);
	$("#modifyViewTime").attr("disabled", true);
	$("#hasProp").val(1);
	$("#propCost").attr("disabled", true);
	$("#propTime").attr("disabled", true);
	
	$("#modifyScenceFinance").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	$("#price").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	
	//加载单个场景信息
	if($("#sceneViewId").val() != ""){
		loadSceneInfo();
	}else{
		$(".delete-btn").hide();
	}
	$(window).resize(function(){
		bodyHeight = $(this).height();
		bodyWidth = $(document.body).width();
		$("#mapModalZIndex").css({"width": bodyWidth, "height": bodyHeight});
		
	});
	//格式化价格金额以及后续处理
	dealValueFormat();
	//初始化进度控件
	$("#myLoader").dimmer({
		closable: false
	});
	//只读
	if($("#where").val() == "prepare"){//筹备管理的堪景
		if(isReadonly){
			$(".save-btn").remove();
			$(".delete-btn").remove();
			$("input[type='text']").attr("disabled", true);
			$("select").attr("disabled", true);
			$("textarea").attr("disabled", true);
		}
	}else{//拍摄管理的堪景
		if(isSceneViewReadonly) {
			$(".save-btn").remove();
			$(".delete-btn").remove();
			$("input[type='text']").attr("disabled", true);
			$("select").attr("disabled", true);
			$("textarea").attr("disabled", true);
		}
	}
	
});
var map;
var point;

var bodyHeight;
var bodyWidth;
function showMapWindow(){
	if($("#where").val() == "prepare"){//筹备管理的堪景
		if(isReadonly){
			return false;
		}
	}else{//拍摄管理的堪景
		if(isSceneViewReadonly) {
			return false;
		}
	}
	
	
	bodyHeight = $(window).height();
	
	bodyWidth = $(document.body).width();
	$("#mapModalZIndex").css({"width": bodyWidth, "height": bodyHeight}).show();
	$("#setDetailAddressWin").show();
	initBaiDuMap();
}

//获取所有地域信息
function queryAllRegionInfo(){
	$.ajax({
		url: '/sceneViewInfoController/queryProCityList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response){
				shootRegionList = response.shootRegionList;
			}
		}
	});
}

var hasDrag = false;//是否拖动
function saveLocation(){
	var addComp;
	var province;
	var city;
	var district;
	$("#vLongitude").val(point.lng);
	$("#vLatitude").val(point.lat);
	
	$("#setDetailAddressWin").hide();
	$("#mapModalZIndex").hide();
	  var geoc = new BMap.Geocoder();    
	    geoc.getLocation(point, function(rs){
		addComp = rs.addressComponents;
		province = addComp.province;
		city = addComp.city;
	    district = addComp.district;
	    var detailAddress;
	    var newAddress = "";
	    if(province == city){
	    	
	    		if(province=="宁夏回族自治区"){
	    			province ="宁夏";
		    	}else if(province == "新疆维吾尔自治区"){
		    		province = "新疆";
		    	}else if(province == "内蒙古自治区"){
		    		province = "内蒙古";
		    	}else if(province == "广西壮族自治区"){
		    		province = "广西";
		    	}else if(province == "西藏自治区"){
		    		province = "西藏";
		    	}else  {//去掉"市"
		    		if(province.indexOf("市") != -1){
		    			province = province.replace(/市/g, "");
		    		}
		    	}
	    		var nowAddress = province+district;
	    		for(var i=0; i< shootRegionList.length; i++){
	    			if(nowAddress.indexOf(shootRegionList[i]) != -1){
	    				newAddress = shootRegionList[i];
	    				break;
	    			}
	    		}
//	    		$('#cityName').val(newAddress);
	    	
	    	detailAddress = addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}else{
				if(province=="宁夏回族自治区"){
		    		province ="宁夏";
		    	}else if(province == "新疆维吾尔自治区"){
		    		province = "新疆";
		    	}else if(province == "内蒙古自治区"){
		    		province = "内蒙古";
		    	}else if(province == "广西壮族自治区"){
		    		province = "广西";
		    	}else if(province == "西藏自治区"){
		    		province = "西藏";
		    	}else  {//去掉"省"
		    		province = province.replace(/省/g, "");
		    	}
				var nowAddress = province+city;
				for(var i=0; i< shootRegionList.length; i++){
	    			if(nowAddress.indexOf(shootRegionList[i]) != -1){
	    				newAddress = shootRegionList[i];
	    				break;
	    			}
	    		}
//				$('#cityName').val(newAddress);
			
			detailAddress = addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;
		}
	    if($('#address').val() !=''){
	    	if( hasDrag){
	    		parent.popupPromptBox('提示','是否用"'+ detailAddress +'"覆盖当前地址？',function(){
					
					setValue(addComp, province, city, district, newAddress);
				});
				hasDrag = false;
	    	}
			
		}else{
			setValue(addComp, province, city, district, newAddress);
			hasDrag = false;
		}
	   }); 
		
}


//获取地图信息，并赋值
function setValue(addComp, province, city, district, newAddress){
		if(province == city){
				$('#address').val(addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber);
				$("#cityName").val(newAddress);
		}else{
				$('#address').val(addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber);
				$("#cityName").val(newAddress);
		}
	
}

var num = 0;
//初始化地图
function initBaiDuMap(){
	map = new BMap.Map("allmap");
	if(num ==0){
		initAutoComplete(map);
		num ++;
	}
	if (point&&point.lng!=null&&point.lat!=null) {
		
		map.clearOverlays();    //清除地图上所有覆盖物
		var marker = new BMap.Marker(point); // 创建标注
		marker.enableDragging(); // 可拖拽
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
		map.centerAndZoom(point, 16);
		map.addOverlay(marker); // 将标注添加到地图中
//		marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	} else {
		map.clearOverlays();    //清除地图上所有覆盖物
		
		var p = new BMap.Point(116.405285, 39.904989);
		map.centerAndZoom(p,11);
		point = p;
	
		var marker = new BMap.Marker(p); // 创建标注
		marker.enableDragging(); // 可拖拽
		map.addOverlay(marker); // 将标注添加到地图中
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
		
		locateCurrentCity();
	}

	map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
}

//获取当前城市信息
function locateCurrentCity() {
	$.getJSON('https://route.showapi.com/632-1?showapi_appid=12437&showapi_sign=6c2a2ad77d644fee81c6d71a5ee9310d', function(data){
		var currentLat = data.showapi_res_body.lat; 
		var currentLon = data.showapi_res_body.lnt; 
		var p = new BMap.Point(currentLon, currentLat);
		map.setCenter(p);
		point = p;
		
		
		map.clearOverlays();    //清除地图上所有覆盖物
		
		var marker = new BMap.Marker(p); // 创建标注
		marker.enableDragging(); // 可拖拽
		map.addOverlay(marker); // 将标注添加到地图中
		marker.addEventListener("dragend", function(e){
			point.lng = e.point.lng;
			point.lat = e.point.lat;
			hasDrag = true;
		});
	});
}

function initAutoComplete(){
	var ac = new BMap.Autocomplete(//建立一个自动完成的对象
			{"input" : "suggestId"
			,"location" : map
		});

		ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
			var str = "";
			var _value = e.fromitem.value;
			var value = "";
			if (e.fromitem.index > -1) {
				value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
			}    
			str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
			
			value = "";
			if (e.toitem.index > -1) {
				_value = e.toitem.value;
				value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
			}    
			str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
			G("searchResultPanel").innerHTML = str;
		});

		var myValue;
		ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
			var _value = e.item.value;
			myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
			G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
			
			setPlace();
		});

		function setPlace(){
			map.clearOverlays();    //清除地图上所有覆盖物
			function myFun(result){
//				var post;
//			    for(var i= 0; i< result.getCurrentNumPois(); i++){
//			    	 post = result.getPoi(i).postcode;
//			    }
				var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
				map.centerAndZoom(pp, 18);
				point = pp;
				hasDrag = true;
				var marker = new BMap.Marker(pp); //创建标注
				marker.enableDragging(); // 可拖拽
				map.addOverlay(marker);    //添加标注
				marker.addEventListener("dragend", function(e){
					point.lng = e.point.lng;
					point.lat = e.point.lat;
					hasDrag = true;
				});
			}
			var local = new BMap.LocalSearch(map, { //智能搜索
			  onSearchComplete: myFun
			});
			local.search(myValue);
		}
}

//百度地图API功能
function G(id) {
	return document.getElementById(id);
}






//定义上传文件变量
var uploader;

//初始化文件上传
function initFileUpload(){
	uploader = WebUploader.create({
		// 不压缩image
		resize : false,
		// 文件接收服务端。
		server : '/contractManager/uploadAttachment',
		timeout: 30*60*1000,//超时
		pick : '#uploadFileBtn',
		// 只允许选择图片文件。
	    accept: {
	        title: 'Images',
	        extensions: 'jpg,jpeg,png',
	        mimeTypes: 'image/jpg,image/jpeg,image/png'
	    }
	});
	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
		if(file.size > 104857600){
    		parent.showInfoMessage("文件大小超出了100M");
    		uploader.removeFile( file, true );
    		return;
    	}
		var fileUl = $("#showSmallUploadFile");
		var $li = $("<li class='upload-file-list-li'></li>");
		
		uploader.makeThumb( file, function( error, ret ) {
	        if ( error ) {
	            $li.html("预览错误");
	            $("#showSmallUploadFile").append($li);
	        } else {
	        	$li.append("<img alt='' src='" + ret + "' /><a class='closeTag' onclick='deleteReadyUploadFile(this,\""+ file.id +"\")'></a>");
	        	$li.append("<p class='file-list-tips' title='"+ file.name +"'>" + file.name + "</p>");
	            $("#showSmallUploadFile").append($li);
	        }
	    });
	});
	
	//当文件开始上传时
	uploader.on("startUpload", function() {
		$('#myLoader').dimmer("show");
	});
	//当文件上传结束时
	uploader.on('uploadFinished', function(file) {
		    $(".loader").text("上传成功");
		    $('#myLoader').dimmer("hide");
		    closeRightPopupWin();
			parent.showSuccessMessage("保存成功");
			
		
	});
	
	uploader.on('uploadComplete', function(file) {
		
	});
}

//删除未上传的文件
function deleteReadyUploadFile(own, fileId){
	own= $(own);
	uploader.removeFile(fileId, true);
	own.parent("li").remove();
}

//删除上传成功的文件附件
//function deleteUploadedFile(own,attId){
//	own= $(own);
//	parent.popupPromptBox("提示","是否删除该附件？", function () {
//		$.ajax({
//	    	url:'',
//	    	type:'post',
//	    	dataType:'json',
//	    	data:{attachmentId: attId},
//	    	success:function(data){
//	    		if(data.success){
//	    			own.parent("li").remove();
//	    			parent.showSuccessMessage("删除成功！");
//	    		}else{
//	    			parent.showErrorMessage('删除附件失败');
//	    		}
//	    		
//	    	}   	
//		});
//    });
//	
//}



//改变状态
function changeViewStatus(own){//改景
	var $this = $(own);
	if($this.val() == 0){
		$("#modifyViewCost").attr("disabled", false);
		$("#modifyViewTime").attr("disabled", false);
	}else{
		$("#modifyViewCost").val("");
		$("#modifyViewTime").val("");
		$("#modifyViewCost").next("input[type=hidden]").val("");
		$("#modifyViewCost").attr("disabled", true);
		$("#modifyViewTime").attr("disabled", true);
	}
}

function changePropStatus(own){
	var $this = $(own);
	if($this.val() == 0){
		$("#propCost").attr("disabled", false);
		$("#propTime").attr("disabled", false);
	}else{
		$("#propCost").val("");
		$("#propTime").val("");
		$("#propCost").next("input[type=hidden]").val("");
		$("#propCost").attr("disabled", true);
		$("#propTime").attr("disabled", true);
	}
}


//删除实景信息
function delScenceInfo(){
	var id =  $('#sceneViewId').val();
	parent.popupPromptBox("提示","是否要删除该条场景信息？", function (){
		$.ajax({
			url: '/sceneViewInfoController/delSceneViewInfo',
			type: 'post',
			data: {id:id},
			dataType: 'json',
			success: function(response){
				parent.showSuccessMessage("操作成功");
				closeRightPopupWin();
				parent.reloadMainScenceGrid();
			}
		});
	});
	
}

//保存场景
function saveScenceInfo(){
	
	var subData = {};
	if($("#vName").val() == ""){
		parent.showInfoMessage("名称不能为空");
		return;
	}
	if($("#address").val() == ""){
		parent.showInfoMessage("详细地址不能为空");
		return;
	}
	if($("#vLongitude").val() == "" && $("#vLatitude").val() == ""){
		parent.showInfoMessage("缺少经纬度，您可尝试通过地图定位获取经纬度");
		return;
	}
	if($("#isModifyView").val() == 0){
		if($("#modifyViewTime").val() == ""){
			parent.showInfoMessage("改景时间不能为空");
			return;
		}
		if($("#modifyViewCost").val() == ""){
			parent.showInfoMessage("改景费用不能为空");
			return;
		}
		
	}
	if($("#hasProp").val() == 0){
		if($("#propTime").val() == ""){
			parent.showInfoMessage("陈设时间不能为空");
			return;
		}
		if($("#propCost").val() == ""){
			parent.showInfoMessage("陈设费用不能为空");
			return;
		}
		
	}
	
	subData.vName = $("#vName").val();
	subData.distanceToHotel = $("#distanceToHotel").val();
	subData.vCity = $("#cityName").val();
	subData.holePeoples = $("#holePeoples").val();
	subData.vAddress = $("#address").val();
	subData.vLongitude = $("#vLongitude").val();
	subData.vLatitude = $("#vLatitude").val();
	subData.deviceSpace = $("#equipmentSpace").val();
	subData.isModifyView = $("#isModifyView").val()-0;
//	subData.modifyViewCost = $("#modifyViewCost").next("input[type=hidden]").val();
	subData.modifyViewCost = $("#modifyViewCost").val();
	subData.modifyViewTime = $("#modifyViewTime").val();
	subData.hasProp = $("#hasProp").val()-0;
//	subData.propCost = $("#propCost").next("input[type=hidden]").val();
	subData.propCost = $("#propCost").val();
	subData.propTime = $("#propTime").val();
	subData.viewUseTime = $("#viewUseTime").val();
//	subData.viewPrice = $("#viewPrice").next("input[type=hidden]").val();
	subData.viewPrice = $("#viewPrice").val();
	//空闲档期
	subData.freeStartDate = $("#freeStartDate").val();
	subData.freeEndDate = $("#freeEndDate").val();
	subData.enterViewDate = $("#enterViewDate").val();
	subData.leaveViewDate = $("#leaveViewDate").val();
	subData.contactName = $("#contactName").val();
	subData.contactRole = $("#contactRole").val();
	subData.contactNo = $("#contactNo").val();
	subData.remark = $("#remark").val();
	subData.id = $('#sceneViewId').val();
	
	$.ajax({
		url: '/sceneViewInfoController/saveOrUpdateSceneViewInfo',
		type: 'post',
		data: subData,
		dataType: 'json',
		success: function(response){
			if(response.success){
				//对场景id进行赋值
				$("#sceneViewId").val(response.sceneViewInfoId);
				/*parent.showSuccessMessage("保存成功");
				closeRightPopupWin();*/
				parent.reloadMainScenceGrid();
                var sceneViewInfoId = response.sceneViewInfoId;
	    		if (uploader.getFiles().length == 0) {
	    			parent.showSuccessMessage("保存成功");
	    			//closeRightPopupWin();
	    		} else {
	    			uploader.option('formData', {
		    			attpackId: sceneViewInfoId
		    		});
		    		uploader.upload();
	    		}
				
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//关闭弹窗
function closeRightPopupWin(){
	parent.closeRightPopupWin();
}




//加载单个场景内容
function loadSceneInfo(){
	$.ajax({
		url: '/sceneViewInfoController/querySceneViewInfoById',
		type: 'post',
		data: {sceneViewId: $("#sceneViewId").val()},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var sceneViewList = response.sceneViewInfoModel;
				var attachmentList = response.attachmentList;
				$("#vName").val(sceneViewList.vname);
				$("#distanceToHotel").val(sceneViewList.distanceToHotel);
				$("#cityName").val(sceneViewList.vcity);
//				if(sceneViewList.holePeoples == 0){
//					$("#holePeoples").val("");
//				}else{
//					$("#holePeoples").val(sceneViewList.holePeoples);
//				}
				$("#holePeoples").val(sceneViewList.holePeoples);
				$("#address").val(sceneViewList.vaddress);
				$("#vLongitude").val(sceneViewList.vlongitude);
				$("#vLatitude").val(sceneViewList.vlatitude);
				$("#equipmentSpace").val(sceneViewList.deviceSpace);
				$("#isModifyView").val(sceneViewList.isModifyView);
				if(sceneViewList.isModifyView == 1){
					$("#modifyViewCost").attr("disabled", true);
					$("#modifyViewTime").attr("disabled", true);
				}else{
					$("#modifyViewCost").attr("disabled", false);
					$("#modifyViewTime").attr("disabled", false);
				}
				if(sceneViewList.modifyViewCost == null){
					/*$("#modifyViewCost").val(sceneViewList.modifyViewCost);
					$("#modifyViewCost").next("input[type=hidden]").val("");*/
				}else{
					$("#modifyViewCost").next("input[type=hidden]").val(sceneViewList.modifyViewCost);
//					$("#modifyViewCost").val(fmoney(sceneViewList.modifyViewCost));
					$("#modifyViewCost").val(sceneViewList.modifyViewCost);
				}
				
				$("#modifyViewTime").val(sceneViewList.modifyViewTime);
				$("#hasProp").val(sceneViewList.hasProp);
				if(sceneViewList.hasProp == 1){
					$("#propCost").attr("disabled", true);
					$("#propTime").attr("disabled", true);
				}else{
					$("#propCost").attr("disabled", false);
					$("#propTime").attr("disabled", false);
				}
				
				if( sceneViewList.propCost == null){
					/*$("#propCost").val(sceneViewList.propCost);
					$("#propCost").next("input[type=hidden]").val("");*/
				}else{
					$("#propCost").next("input[type=hidden]").val(sceneViewList.propCost);
//					$("#propCost").val(fmoney(sceneViewList.propCost));
					$("#propCost").val(sceneViewList.propCost);
				}
				$("#propTime").val(sceneViewList.propTime);
			    $("#viewUseTime").val(sceneViewList.viewUseTime);
//			    if(sceneViewList.viewPrice == null){
//			    	$("#viewPrice").val("");
//			    	$("#viewPrice").next("input[type=hidden]").val("");
//			    }else{
//			    	$("#viewPrice").val(fmoney(sceneViewList.viewPrice));
//			    	$("#viewPrice").next("input[type=hidden]").val(sceneViewList.viewPrice);
//			    }
			    $("#viewPrice").val(sceneViewList.viewPrice);
		    	$("#viewPrice").next("input[type=hidden]").val(sceneViewList.viewPrice);
				
				
				//空闲档期
				$("#freeStartDate").val(sceneViewList.freeStartDate);
				$("#freeEndDate").val(sceneViewList.freeEndDate);
				
				$("#enterViewDate").val(sceneViewList.enterViewDate);
				$("#leaveViewDate").val(sceneViewList.leaveViewDate);
				$("#contactName").val(sceneViewList.contactName);
				$("#contactRole").val(sceneViewList.contactRole);
				$("#contactNo").val(sceneViewList.contactNo);
				$("#remark").val(sceneViewList.remark);
				
				//初始化地图
				point = new BMap.Point(sceneViewList.vlongitude,sceneViewList.vlatitude);
				//上传合同附件信息
				if(attachmentList !=null || attachmentList.length != 0){
					for(var i= 0; i< attachmentList.length; i++){
						var attachment = attachmentList[i];
						
						var html = [];
						html.push("<li class='upload-file-list-li' onclick='previewAtts(\""+ attachment.attpackId +"\", \""+ attachment.type +"\")'><img src='/fileManager/previewAttachment?address="+attachment.hdStorePath+"'><a class='closeTag' onclick='deleteUploadedFile(event,this,\""+ attachment.id +"\")'></a><p class='file-list-tips' title='"+ attachment.name +"'>" + attachment.name + "</p></li>");
						html.join("");
						$("#showSmallUploadFile").append(html);
					}
				}
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}
//预览附件
function previewAtts(attpackId, type) {
	window.open("/attachmentManager/toPreviewPage?attpackId='" + attpackId + "'&type=" + type);
}
//删除上传成功的文件附件
function deleteUploadedFile(ev,own,attId){
	if($("#where").val() == "prepare"){//筹备管理的堪景
		if(isReadonly){
			ev.stopPropagation();
			return false;
		}
	}else{//拍摄管理的堪景
		if(isSceneViewReadonly) {
			ev.stopPropagation();
			return false;
		}
	}
	
	own= $(own);
	parent.popupPromptBox("提示","是否删除该附件？", function () {
		$.ajax({
	    	url:'/attachmentManager/deleteAttachment',
	    	type:'post',
	    	dataType:'json',
	    	data:{attachmentId: attId},
	    	success:function(data){
	    		if(data.success){
	    			own.parent("li").remove();
	    			parent.showSuccessMessage("删除成功！");
	    		}else{
	    			parent.showErrorMessage('删除附件失败');
	    		}
	    		
	    	}   	
		});
    });
	ev.stopPropagation();
}


//格式化价格金额以及后续处理
function dealValueFormat(){
	return;
	$("#viewPrice").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	$("#viewPrice").on("focus", function(){
		var value = $("#viewPrice").val();
		if(value != ""){
			$("#viewPrice").val(value.replace(/,/g, ""));	
		}
	}).on("blur", function(){
		var value = $("#viewPrice").val();
		if(value != ""){
			$("#viewPrice").next("input[type=hidden]").val(value);
			$("#viewPrice").val(fmoney(value));
			
		}
	});
	
	//格式化改景费用
	$("#modifyViewCost").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	$("#modifyViewCost").on("focus", function(){
		var value = $("#modifyViewCost").val();
		if(value != ""){
			$("#modifyViewCost").val(value.replace(/,/g, ""));
		}
	}).on("blur", function(){
		var value = $("#modifyViewCost").val();
		if(value != ""){
			$("#modifyViewCost").next("input[type=hidden]").val(value);
			$("#modifyViewCost").val(fmoney(value));
			
		}
	});
	
	//格式化陈设费用
	$("#propCost").keyup(function(){    
		$(this).val($(this).val().replace(/[^\d.]/g,""));  //清除“数字”和“.”以外的字符
		$(this).val($(this).val().replace(/^\./g,""));  //验证第一个字符是数字而不是.
		$(this).val($(this).val().replace(/\.{2,}/g,".")); //只保留第一个. 清除多余的.
		$(this).val($(this).val().replace(".","$#$").replace(/\./g,"").replace("$#$","."));
	});
	$("#propCost").on("focus", function(){
		var value = $("#propCost").val();
		if(value != ""){
			$("#propCost").val(value.replace(/,/g, ""));
		}
	}).on("blur", function(){
		var value = $("#propCost").val();
		if(value != ""){
			$("#propCost").next("input[type=hidden]").val(value);
			$("#propCost").val(fmoney(value));
			
		}
	});
	
}


//获得日期相差的天数
function getDays(){
	if($("#enterViewDate").val() != "" && $("#leaveViewDate").val() != ""){
		var strDateStart = $("#enterViewDate").val();
		var strDateEnd = $("#leaveViewDate").val();
		   var strSeparator = "-"; //日期分隔符
		   var oDate1;
		   var oDate2;
		   var iDays;
		   oDate1= strDateStart.split(strSeparator);
		   oDate2= strDateEnd.split(strSeparator);
		   var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]);
		   var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]);
		   iDays = parseInt(Math.abs(strDateS - strDateE ) / 1000 / 60 / 60 /24);//把相差的毫秒数转换为天数 
		   $("#viewUseTime").val(iDays);
	}
}

//展示设置场景信息
function showSteSceneView(own){
	//取出场景id
	var id = $("#sceneViewId").val();
	var _this = $(own);
	_this.addClass("tab_li_current");
	$("#tab_0").removeClass("tab_li_current");
	//影藏基本信息
	$("#baseInfoDiv").hide();
	$("#setScenceWin").show();
	$("#btnListDiv").hide();
	
	/*//清空未配置场景列表
	$("#noneConfigured").empty();
	//收起未配置场景列表
	$("#relationMoreView").show();
	$("#noneConfigured").hide();*/
	
	alreadySceneViewId = id;
	//获取已配置场景列表
	initAlreadyScence(id);
	//获取未配置场景列表
	getNoneConfiguredScenceList();
	
}

//展示基本信息
function showBaseInfo(own){
	var _this = $(own);
	_this.addClass("tab_li_current");
	$("#tab_1").removeClass("tab_li_current");
	
	//影藏基本信息
	$("#baseInfoDiv").show();
	$("#setScenceWin").hide();
	$("#btnListDiv").show();
}

var noneConfigActor;//未配置列表主要演员
//初始化已配置列表
function initAlreadyScence(id){
	var source =
    {
        dataType: "json",
        data: {id: id},
        dataFields: [ 
		          {name: 'id', type: "string" },
		          {name: 'parentId', type: "string" },
		          {name: "location", type: "string"},
	              {name: "locationid", type: "string"},
	              {name: "pageCount", type: "string"},
	              {name: "siteNum", type: "string"},
	              {name: "DAY", type: "string"},
	              {name: "night", type: "string"},
	              {name: "sitePage", type: "string"},
	              {name: "d_n", type: "string"}
      
        ],
        hierarchy:
            {
	               keyDataField: {name:'id'},
	               parentDataField: {name:'parentId'}
            },
        url: '/sceneViewInfoController/queryHasCheckViewInfoForSceneView',
        id: "id"
    };
	
	
    var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function () {
        }
    });
    
    
    var columnInformation = [];
    columnInformation.push({text: "主场景", datafield: 'location',width: '50%', cellsAlign: 'left', align: 'center', sortable: false});
    
    columnInformation.push({text: "场/页数", datafield: 'sitePage',width: '25%', cellsAlign: 'left', align: 'center', sortable: false});
    
    columnInformation.push({text: "日夜比例", datafield: 'd_n',width: '25%', cellsAlign: 'left', align: 'center', sortable: false});
      
    
    
    var rendertoolbar = function(toolbar) {
      var container = [];
      container.push("<div class='toolbar'>");
      container.push("<div class='left-border-top'>");
      container.push("  <div class='border-left'></div>");
      container.push("  <span class='header-title'>已配置场景</span>");
      container.push("</div>");
      if($("#sceneViewId").val() != ""){
    	  if(!isSceneViewReadonly) {
              container.push("<input type='button' class='delete-scence-btn' id='deleteScenceBtn' onclick='deleteScenceBtn()' value='移除→'>");
         } 
      }
      
      container.push("</div>");
      toolbar.append($(container.join("")));
    };
    
    $("#alreadyConfigured").jqxTreeGrid(
            {
            	source: dataAdapter,
            	width: '100%',
                height: '100%',
                columnsHeight: 35, 
                /* rowsHeight: noneConfigRowsHeight, */   
                sortable: true,
                hierarchicalCheckboxes: true,
                toolbarHeight: 40,
                showToolbar: true,
                checkboxes: true,
                rendertoolbar: rendertoolbar,
                ready: function () {
                },
                columns: columnInformation
            });

}


////显示未配置场景列表
//function showNoneConfiguredScence(own){
//	if(isSceneViewReadonly) {
//		showErrorMessage("对不起，您没有权限进行该操作");
//		return false;
//	}
//	var $this = $(own);
//	var screenWidth = window.screen.width;
//	if(screenWidth >= 1366 && screenWidth <= 1399){
//		$this.parent("div#noneConfiguredCon").css({"height":"290px"});
//		$("#alreadyConfiguredCon").css({"height":"290px"});
//	}else{
//		$this.parent("div#noneConfiguredCon").css({"height":"300px"});
//		$("#alreadyConfiguredCon").css({"height":"350px"});
//	}
//	
//	$("#noneConfigured").show();
//	$this.hide();
//	//重新刷新一下表格
//	sceneViewId.id = alreadySceneViewId;
//	$("#alreadyConfigured").jqxTreeGrid('updateBoundData');
//	$this.next("div#noneConfigured").slideDown("fast");
//	//获取未配置场景列表
//	//getNoneConfiguredScenceList();
//}
//获取未配置场景列表
function getNoneConfiguredScenceList(){
	var source =
    {
        dataType: "json",
        data: subData,
        dataFields: [
		          {name: 'id', type: "string" },
		          {name: 'parentId', type: "string" },
		          {name: "location", type: "string"},
	              {name: "locationid", type: "string"},
	              {name: "pageCount", type: "string"},
	              {name: "siteNum", type: "string"},
	              {name: "DAY", type: "string"},
	              {name: "night", type: "string"},
	              {name: "sitePage", type: "string"},
	              {name: "d_n", type: "string"},
	              {name: "vname", type: "string"}
      
        ],
        hierarchy:
            {
	               keyDataField: {name:'id'},
	               parentDataField: {name:'parentId'}
            },
        url: '/sceneViewInfoController/queryAlternativeViewInfo',
        id: "id"
    };
	
	
    var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function () {
        }
    });
    
    var columnInformation = [];
    columnInformation.push({text: "主场景", datafield: 'location',width: '50%', cellsAlign: 'left', align: 'center', sortable: false});
    columnInformation.push({text: "已配置拍摄地",datafield: 'vname',hidden:false,width: '15%'});
    
    columnInformation.push({text: "场/页数", datafield: 'sitePage',width: '15%', cellsAlign: 'left', align: 'center', sortable: false});
    
    columnInformation.push({text: "日夜比例", datafield: 'd_n',width: '20%', cellsAlign: 'left', align: 'center', sortable: false});
      
    
    
      var rendertoolbar = function(toolbar) {
      var container = [];
      container.push("<div class='toolbar'>");
      container.push("<div class='left-border-top'>");
      container.push(" <div class='border-left'></div>");
      container.push("  <span class='header-title'>备选场景</span>");
      container.push("</div>");
      if($("#sceneViewId").val() != "" && !isSceneViewReadonly){
    	  container.push("<input type='button' class='add-scence-to-btn' id='addScenceToBtn' onclick='addScenceToBtn()' value='←添加'>");
      }
      if (!isSceneViewReadonly) {
    	  container.push("<input class='search-input' type='text' id='searchScencName' placeholder='主场景名称' onkeyDown='quickSearch(event)'><div class='search-btn-div'  onclick='searchLocationView()'><img src='../images/roleform/search.png' style='width:100%;'></div>");
	}
      container.push("<ul class='main-scene-list-ul' id='mainSceneList'></ul>");
      container.push("</div>");
      toolbar.append($(container.join("")));
    };
    
    
    
    $("#noneConfigured").jqxTreeGrid(
            {
            	source: dataAdapter,
            	width: '100%',
                height: '100%',
                columnsHeight: 35, 
                /* rowsHeight: noneConfigRowsHeight, */   
                sortable: true,
                hierarchicalCheckboxes: true,
                toolbarHeight: 40,
                showToolbar: true,
                checkboxes: true,
                rendertoolbar: rendertoolbar,
                ready: function () {
                },
                columns: columnInformation
            });
    
}




/*添加到配置场景表*/
function addScenceToBtn(){
	var locationIds = [];

	var readySceneViewId = alreadySceneViewId;
	var rows = $("#noneConfigured").jqxTreeGrid('getRows');
	var vname = '';
    var traverseTree = function(rows)
    {
        for(var i = 0; i < rows.length; i++){
        	    if(rows[i].checked){
        	    	vname = rows[i].vname;
        	    	var lid = rows[i].locationid;
	        		if(lid == null){
	        			lid = 'blank';
	        		}
        	    	locationIds.push(lid);
        	    }
            	var records = rows[i].records;
            	if (records != undefined){
                    traverseTree(rows[i].records);
                }
            
        }
    };
    traverseTree(rows);
    var locationId = locationIds.join("##");
    if(locationId == ""){
    	parent.showInfoMessage("请选择未配置的场景");
    	return;
    }else{
		$.ajax({
			url: '/sceneViewInfoController/saveSceneViewViewInfoMap',
			type: 'post',
			data: {sceneviewId: readySceneViewId, locationId: locationId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("操作成功");
					$('#noneConfigured').jqxTreeGrid('clearSelection');
					$("#noneConfigured").jqxTreeGrid('updateBoundData');
					
					//重新加载已配置场景表格
					$('#alreadyConfigured').jqxTreeGrid('clearSelection');
					$("#alreadyConfigured").jqxTreeGrid('updateBoundData');
					parent.reloadMainScenceGrid();
				}
			}
		});
	}
}

//加载未配置场景列表主要演员
function loadNoneConfigMainActorList(){
	$.ajax({
		url: '/sceneViewInfoController/queryAlternativeViewInfo',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response.success){
				noneConfigActor = response.titleMap;
			}else{
				parent.showErrorMessage(response.message);
			}
		}
		
	});
}

//移除选中主场景
function deleteScenceBtn(){
	var readysceneViewId = alreadySceneViewId;
	var locationIds = [];
	var rows = $("#alreadyConfigured").jqxTreeGrid('getRows');
    var traverseTree = function(rows)
    {
        for(var i = 0; i < rows.length; i++){
	        	if(rows[i].checked){
	        		var lid = rows[i].locationid;
	        		if(lid == null){
	        			lid = 'blank';
	        		}
	        		locationIds.push(lid);
	            }
            	
            	var records = rows[i].records;
            	if (records != undefined){
                    traverseTree(rows[i].records);
                }
        }
    };
    traverseTree(rows);
    var locationId = locationIds.join("##");
    if(locationId == ""){
    		parent.showInfoMessage("请选择要移除的场景");
    		return;
    }else{
		$.ajax({
			url: '/sceneViewInfoController/delSceneViewMapInfo',
			type: 'post',
			data: {sceneViewInfoId: readysceneViewId, locationId: locationId},
			datatype: 'json',
			success: function(response){
				if(response.success){
					parent.showSuccessMessage("操作成功");
					$('#noneConfigured').jqxTreeGrid('clearSelection');
					$("#alreadyConfigured").jqxTreeGrid('updateBoundData');
					
					$("#noneConfigured").jqxTreeGrid('updateBoundData');
					parent.reloadMainScenceGrid();
					
				}else{
					parent.showErrorMessage(response.message);
				}
			}
		});
    }

}


//快速搜索
function quickSearch(event){
	
	if(event.keyCode == 13){
		searchLocationView();
	}
}


//搜索按钮
function searchLocationView(){
	if($("#searchScencName").val() != ""){
		subData.location = $("#searchScencName").val();
		$("#noneConfigured").jqxTreeGrid('updateBoundData');
		$('#noneConfigured').jqxTreeGrid('clearSelection');
	}else{
		subData.location = "";
		$("#noneConfigured").jqxTreeGrid('updateBoundData');
		$('#noneConfigured').jqxTreeGrid('clearSelection');
	}
}

//显示loading
function showLoading(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
     _LoadingHtml = $("#loadingTable");
    
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
}
