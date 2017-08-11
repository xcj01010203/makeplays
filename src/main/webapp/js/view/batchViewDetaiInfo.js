
$(document).ready(function(){
	//跳转到批量修改页面时需要加载的数据
	loadUpdateData();
	//查询拍摄地信息
    queryLocationInfo();
	//初始化点击事件
	initRightContentClick();
	//初始化设置地域
	initSetShootRegin();
	//关闭弹层
	$(document).click(function(){
		parent.Popup.hide();
		$('.dropdown_box').hide();
    });
	
	isEnabled();
	parent.showAutoSetWindow();
});


//跳转到批量修改页面时需要加载的数据
function loadUpdateData(){
	$.ajax({
		url: '/viewManager/queryViewDetailInfo',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var	filterDto = response.filterDto;
				//将数据添加到dom元素中
				addDataToDom(filterDto);
			}
		}
	});
}

//将数据添加到dom元素中
function addDataToDom(dataList){
	//添加气氛数据
	var $atmosphereInfo = $("#atmosphereInfo");
	var atmosphereArr = [];
	var atmosphereList = dataList.atmosphereList;
	atmosphereArr.push("	<ul class='dropdown_box' style='width:235px;'>");
	for(var key in atmosphereList){
		atmosphereArr.push("	<li>");
		atmosphereArr.push("	<a href='javascript:void(0)' onclick='atmosphereSelect(this)'>"+atmosphereList[key]+"</a>");
		atmosphereArr.push("	</li>");
	}
	atmosphereArr.push("	<span class='arrows_up'></span>");
	atmosphereArr.push("	</ul>");
	$atmosphereInfo.after(atmosphereArr.join(""));
	
	//拼接内外景
	var $siteInfo = $("#siteInfo");
	var siteArr= [];
	var siteList = dataList.siteList;
	siteArr.push("	<ul class='dropdown_box' style='width:235px;'>");
	for(var i = 0; i < siteList.length; i++){
		siteArr.push("	<li>");
		var  noFinishSite = siteList[i];
		siteArr.push("	<a href='javascript:void(0)' onclick='siteSelect(this)'>"+noFinishSite+"</a>");
		siteArr.push("	</li>");
	}
	siteArr.push("	<span class='arrows_up'></span>");
	siteArr.push("	</ul>");
	$siteInfo.after(siteArr.join(""));
	
	//特殊提醒
	var $specialRemarkInfo = $("#specialRemarkInfo");
	var specialRemarkArr= [];
	var specialRemindList = dataList.specialRemindList;
	specialRemarkArr.push("	<ul class='dropdown_box' style='width:235px;'>");
	for(var i = 0; i < specialRemindList.length; i++){
		specialRemarkArr.push("	<li>");
		var  noFinishspecialRemind = specialRemindList[i];
		specialRemarkArr.push("	<a href='javascript:void(0)' onclick='specialRemindSelect(this)'>"+noFinishspecialRemind+"</a>");
		specialRemarkArr.push("	</li>");
	}
	specialRemarkArr.push("	<span class='arrows_up'></span>");
	specialRemarkArr.push("	</ul>");
	$specialRemarkInfo.after(specialRemarkArr.join(""));
}

//初始化设置地域
function initSetShootRegin(){
	$("#setShootReginInfo").unbind("click");
	$("#setShootReginInfo").click(function(ev){
		if($("#shootLocationInput").val() == ""){
			parent.showInfoMessage("请选择拍摄地");
			return;
		}
		var _this=$(this);
		$(".dropdown_box").hide();
		var hadValue = $("#shootReginInfo").text();
		hadValue=hadValue.replace(/\(/g,'').replace(/\)/g,'');
    	var dataJson = new Array();
        var dropdownLiList = $('.shoot-regin-ul').find("li a");
        $.each(dropdownLiList, function(index, item) {
	    	if (item.text == hadValue) {
	    		dataJson[index] = {name: item.text, selected: true, attr: $(this).attr("shoot_regin")};
	    	} else {
	    		dataJson[index] = {name: item.text, attr: $(this).attr("shoot_regin")};
	    	}
	    });
        var title = "拍摄地域";
        var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
        parent.showSelectPanel({dataJson: dataJson, right: window.innerWidth, arrowTop: _this.offset().top - scrollTop + 40, title: title, multiselect: false, currentTarget: _this.get(0)});
        parent.Popup.setCallback(this, function(option){
        	if (option.selected) {
        		if(option.value) {
            		$("#shootReginInfo").text("("+option.value+")");
        		} else {
        			$("#shootReginInfo").text("");
        		}
        	} else {
        		$("#shootReginInfo").text("");
        	}
      	});
	    ev.stopPropagation();
	});
}



//将点击内容添加到文本框中
function addDataToContet(){
	//把点击内容放入文本框
    $('.dropdown_box li').unbind("click");
    $('.dropdown_box li').click(function(ev){
    	$(this).parent('ul').prev('.drop_down').val($(this).text()).attr('sid',$(this).attr('sid'));
    });
}

//初始化快速收索功能
function initSearch(){
	//快速搜索功能
    $('.drop_down').on("keyup.textchange", function(ev) {
    	var _this=$(this);
    	var value = _this.val();
    	var dropdownList = _this.next('.dropdown_box').find("li");
    	
    	dropdownList.each(function(){
            var dropdownValue = $(this).text();
            if(dropdownValue.search($.trim(value)) != -1){
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });
}

//初始化drop_down的点击事件
function initRightContentClick(){
	$('.drop_down').unbind("click");
    $('.drop_down').click(function(ev){
        var _this=$(this);
        $('.dropdown_box').hide();
    	if (_this.context.readOnly) {
    		return false;
    	}
        _this.next('.dropdown_box').css({left:_this.position().left,top:_this.position().top+_this.outerHeight()}).show();
        ev.stopPropagation();
    });
 }

//气氛下拉点击事件
function atmosphereSelect(own){
	$("#atmosphereInfo").val('');
	var atmosphereText = $(own).text();
	$("#atmosphereInfo").val(atmosphereText);
}

//内外景下拉点击事件
function siteSelect(own){
	$("#siteInfo").val('');
	var siteText = $(own).text();
	$("#siteInfo").val(siteText);
}

//特殊提醒下拉点击事件
function specialRemindSelect(own){
	$("#specialRemarkInfo").val('');
	var siteText = $(own).text();
	$("#specialRemarkInfo").val(siteText);
}

//判断是否可用
function isEnabled(){
	//设置拍摄地
	$("#setShootLocation").on("click", function(){
		if($(this).is(":checked")){
			$("#shootLocationInput").attr("disabled", false);
			$("#setShootReginInfo").attr("disabled", false);
		}else{
			//清空内容
			$("#shootLocationInput").val('');
			$("#shootReginInfo").text("");
			$("#shootLocationInput").attr("disabled", true);
			$("#setShootReginInfo").attr("disabled", true);
		}
	});
	//气氛
	$("#atmoIsEnabled").on("click", function(){
		if($(this).is(":checked")){
			$("#atmosphereInfo").attr("disabled", false);
		}else{
			//清空内容
			$("#atmosphereInfo").val('');
			$("#atmosphereInfo").attr("disabled", true);
		}
	});
	//内外
	$("#siteInfoIsEnabled").on("click", function(){
		if($(this).is(":checked")){
			$("#siteInfo").attr("disabled", false);
		}else{
			//清空内容
			$("#siteInfo").val('');
			$("#siteInfo").attr("disabled", true);
		}
	});
	
	//场景状态
	$("#statusEnabled").on("click", function(){
		if($(this).is(":checked")){
			$("#batchStatus").attr("disabled", false);
		}else{
			//清空场景状态
			var options = $("#batchStatus option");
			for (var t = 0; t < options.length; t++) {
				var option = options[t];
				if ($(option).text() == '请选择') {
					$(option).prop('selected', true);
				}else {
					$(option).prop('selected', false);
				}
			}
			$("#batchStatus").attr("disabled", true);
		}
	});
	
	//特殊提醒
	$("#specialRemarkIsEnabled").on("click", function(){
		if($(this).is(":checked")){
			$("#specialRemarkInfo").attr("disabled", false);
		}else{
			$("#specialRemarkInfo").attr("disabled", true);
		}
	});
	
	/*//场景
	$("#viewIsEnabled").on("click", function(){
		if($(this).is(":checked")){
			$("#autoSetViewButton").show();
			$("#batchLocationInput").attr("disabled", false);
			window.parent.showInfoMessage("勾选后左侧场景表中所选项的主场景将会设置为所填写的内容！");
		}else{
			$("#autoSetViewButton").hide();
			//清空内容
			$("#batchLocationInput").val('');
			$("#batchLocationInput").attr("disabled", true);
		}
	});*/
}



//显示拍摄地信息
function showDropInfo(own){
	var left = $(own).position().left;
	var top = $(own).position().top;
	var height = $(own).outerHeight();
	$("#locationList").css({"left": left, "top": top+height}).show();
}

//查询拍摄地信息
function queryLocationInfo(){
	$.ajax({
        url:"/sceneViewInfoController/queryShootLocationList",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
        	//查询拍摄地域信息
        	queryShootRegin();
            var shootLocationList = data.shootLocationList;
            var html = [];
            html.push("<ul class='dropdown_box' style='width:235px;' id='locationList'>");
            $.each(shootLocationList, function() {
            	var vname="";
            	if(this.vname){
            		vname+=this.vname;
            	}
            	if(this.vcity){
            		vname+="("+this.vcity+")";
            	}
                html.push("<li title="+ this.vname +" onclick='addLocation(this)' shoot-regin='"+ this.vcity +"'>"+ vname +"</li>");
            });
            html.push("</ul>");
            $("#shootLocationInput").after(html.join(""));
        }
    });
}
//查询拍摄地域信息
function queryShootRegin(){
	$.ajax({
		url: '/sceneViewInfoController/queryProCityList',
		type: 'post',
		datatype: 'json',
		success: function(response){
			if(response){
				var shootRegionList = response.shootRegionList;
				if(shootRegionList != null){
					var html = [];
					html.push('<ul class="shoot-regin-ul" style="display: none;">');
					for(var i=0; i< shootRegionList.length; i++){
						html.push('<li><a shoot-regin="'+ shootRegionList[i] +'">'+ shootRegionList[i] +'</a></li>');
					}
					html.push("</ul>");
					$("#shootReginInfo").after(html.join(""));
				}
			}
		}
	});
}
//检索拍摄地信息
function checkLocation(own){
	var _this = $(own);
    var addressList = $("#locationList").find("li");
    addressList.each(function(){
        var addValue = $(this).text();
        if(addValue.search($.trim(_this.val())) != -1){
            $(this).show();
        } else {
            $(this).hide();
        }
    });
}

function addLocation(own){
	var address = $(own).attr("title");
	$("#shootLocationInput").val(address);
	var shootRegin = $(own).attr("shoot-regin");
	if(shootRegin && shootRegin != "null") {
		$("#shootReginInfo").text("("+shootRegin+")");
	}else{
		$("#shootReginInfo").text("");
	}
}

//统一场景是否可用
function isRepeatViewEnabled(own){
	if($(own).is(":checked")){
		$("#unifiedView").show();
	}else{
		$("#unifiedView").hide();
	}
}