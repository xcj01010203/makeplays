$(document).ready(function () {
	console.log($("#isViewList").val());
	if($("#isViewList").val() == "true"){
		$("#newOldCompareDiv").hide();
	}
	loadCompareScenarioiContent();
});

//加载新老剧本对比界面的内容
function loadCompareScenarioiContent(){
	var seriesViewNo = $("#seriesViewNoInput").val();
	$.ajax({
		url: '/viewManager/queryScenarioCompareInfo',
		type: 'post',
		data:{seriesViewNo:seriesViewNo},
		datatype: 'json',
		success: function(response){
			if(response.success){
				var oldViewData = response.oldViewData;
				var newViewData = response.newViewData;
				var crewType = response.crewType;
				//添加新老剧本数据
				var oldtitle = oldViewData.title;
				var newtitle = newViewData.title;
				var oldcontent = oldViewData.content;
				var newcontent = newViewData.content;
				if(oldtitle&&oldcontent&&oldtitle!=null&&oldcontent!=null){
					$(".sce_old").append("<p>"+oldViewData.title+"<br>"+oldViewData.content+"</p>");
				}
				if(newtitle&&newcontent&&newtitle!=null&&newcontent!=null){
					$(".sce_new").append("<p>"+newViewData.title+"<br>"+newViewData.content+"</p>");
				}
				
				var crewTypeTd =  $("#crewTypeTd");
				//对比集场次信息
				if (crewType == 0 || crewType == 3) { //是电影剧本
					crewTypeTd.append("	<div class='td_div'>场次</div>");
				}else {
					crewTypeTd.append("	<div class='td_div'>集-场</div>");
				}
				
				//显示新的集场次数据
				var td_set_scene = $("#newViewNoTd");
				if (newViewData != '' && newViewData != 'undefined' && newViewData != null && crewType != 0 && crewType != 3) {
					td_set_scene.append("<div class='td_div'><p>"+newViewData.seriesNo+"-"+newViewData.viewNo+"</p></div>");
				}else if (newViewData != '' && newViewData != 'undefined' && newViewData != null && (crewType == 0 || crewType == 3)) {
					td_set_scene.append("<div class='td_div'><p>"+newViewData.viewNo+"</p></div>");
				}
				//显示旧的集场次数据
				var oldViewNoTd = $("#oldViewNoTd");
				if (oldViewData != '' && oldViewData != 'undefined' && oldViewData != null && crewType != 0 && crewType != 3) {
					oldViewNoTd.append("<div class='td_div'><p>"+oldViewData.seriesNo+"-"+oldViewData.viewNo+"</p></div>");
				}else if (oldViewData != '' && oldViewData != 'undefined' && oldViewData != null && (crewType == 0 || crewType == 3)) {
					oldViewNoTd.append("<div class='td_div'><p>"+oldViewData.viewNo+"</p></div>");
				}
				
				//添加新场景的气氛信息
				if ((newViewData.atmosphere != oldViewData.atmosphereName) || (newViewData.site != oldViewData.site)) {
					$("#newViewAtmosphereTr").addClass("different");
				}
				
				var newViewAtmosphereTd = $("#newViewAtmosphereTd");
				if (newViewData.atmosphere != '' && newViewData.atmosphere != 'undefined' && newViewData.atmosphere != null) {
					newViewAtmosphereTd.append("<div class='td_div'><p>"+newViewData.atmosphere+"/"+newViewData.site+"</p></div>");
				}
				//添加旧的场景气氛信息
				var oldViewAtmosphereTd = $("#oldViewAtmosphereTd");
				var strArr = [];
				strArr.push("<div class='td_div'><p>");
				if (oldViewData.atmosphereName != '' && oldViewData.atmosphereName != 'undefined' && oldViewData.atmosphereName != null) {
					strArr.push(oldViewData.atmosphereName+"/");
				}
				if (oldViewData.site != '' && oldViewData.site != 'undefined' && oldViewData.site != null) {
					strArr.push(oldViewData.site);
				}
				strArr.push("</p></div>");
				oldViewAtmosphereTd.append(strArr.join(""));
				
				/*//添加季节信息对比
				if (newViewData.season != oldViewData.seasonValue) {
					$("#comprViewSeaonTr").addClass("different");
				}*/
				
				/*//添加新场景的季节信息
				var newViewSeason = $("#newViewSeason");
                if (newViewData.season == 1) {
                	newViewSeason.append("<p>春</p>");
				}else if (newViewData.season == 2) {
					newViewSeason.append("<p>夏</p>");
				}else if (newViewData.season == 3) {
					newViewSeason.append("<p>秋</p>");
				}else if (newViewData.season == 4) {
					newViewSeason.append("<p>冬</p>");
				}
                //添加旧场景的季节信息
                if (oldViewData.seasonName != '' && oldViewData.seasonName != 'undefined' && oldViewData.seasonName != null) {
                	$("#oldViewSeason").append("<p>"+oldViewData.seasonName+"</p>");
				}*/
                
                //拍摄地点对比
                if (newViewData.shootLocation != oldViewData.shootLocation) {
					$("#comprViewShootLocation").addClass("different");
				}
                if (newViewData.shootLocation != '' && newViewData.shootLocation != 'undefined' && newViewData.shootLocation != null) {
                	//添加新场景的拍摄地点的数据
                	$("#newViewShootLocation").append("<p title='"+newViewData.shootLocation+"'>"+newViewData.shootLocation+"</p>");
				}
                if (oldViewData.shootLocation != '' && oldViewData.shootLocation != 'undefined' && oldViewData.shootLocation != null) {
                	//添加旧场景的拍摄地点的数据
                	$("#oldViewShootLocation").append("<p title='"+oldViewData.shootLocation+"'>"+oldViewData.shootLocation+"</p>");
				}
                
                //主场景信息对比
                if (newViewData.firstLocation != oldViewData.firstLocation) {
					$("#comprViewFirstLocation").addClass("different");
				}
                if (newViewData.firstLocation != '' && newViewData.firstLocation != 'undefined' && newViewData.firstLocation != null) {
                	//新场景 的主场景信息
                	$("#newViewFirstLocation").append("<p title='"+newViewData.firstLocation+"'>"+newViewData.firstLocation+"</p>");
				}
                if (oldViewData.firstLocation != '' && oldViewData.firstLocation != 'undefined' && oldViewData.firstLocation != null) {
                	//旧场景的主场景信息
                	$("#oldViewFirstLocation").append("<p title='"+oldViewData.firstLocation+"'>"+oldViewData.firstLocation+"</p>");
				}
                
                //次场景信息对比
                console.log(newViewData.secondLocation+'-------'+oldViewData.secondLocation);
                var ns = newViewData.secondLocation ==null ?'':newViewData.secondLocation;
                var os = oldViewData.secondLocation ==null ?'':oldViewData.secondLocation;
                if (ns != os) {
					$("#comprViewSecondLocation").addClass("different");
				}
                if (newViewData.secondLocation != '' && newViewData.secondLocation != 'undefined' && newViewData.secondLocation != null) {
                	//新场景 的次场景信息
                	$("#newViewSecondLocation").append("<p title='"+newViewData.secondLocation+"'>"+newViewData.secondLocation+"</p>");
				}
                if (oldViewData.secondLocation != '' && oldViewData.secondLocation != 'undefined' && oldViewData.secondLocation != null) {
                	//旧场景的次场景信息
                	$("#oldViewSecondLocation").append("<p title='"+oldViewData.secondLocation+"'>"+oldViewData.secondLocation+"</p>");
				}
                
                //三级场景信息对比
                var nt = newViewData.thirdLocation ==null ?'':newViewData.thirdLocation;
                var ot = oldViewData.thirdLocation ==null ?'':oldViewData.thirdLocation;
                console.log(ot+'-------'+nt);
                if (nt != ot) {
					$("#comprViewThirdLocation").addClass("different");
				}
                if (newViewData.thirdLocation != '' && newViewData.thirdLocation != 'undefined' && newViewData.thirdLocation != null) {
                	//新场景 的三级场景信息
                	$("#newViewThirdLocation").append("<p title='"+newViewData.thirdLocation+"'>"+newViewData.thirdLocation+"</p>");
				}
                if (oldViewData.thirdLocation != '' && oldViewData.thirdLocation != 'undefined' && oldViewData.thirdLocation != null) {
                	//旧场景的三级景信息
                	$("#oldViewThirdLocation").append("<p title='"+oldViewData.thirdLocation+"'>"+oldViewData.thirdLocation+"</p>");
				}
                
                //主要内容信息对比
                var ncontent = newViewData.mainContent ==null ?'':newViewData.mainContent;
                var ocontent = oldViewData.mainContent ==null ?'':oldViewData.mainContent;
                if (ncontent != ocontent) {
					$("#comprViewMainContent").addClass("different");
				}
                if (newViewData.mainContent != '' && newViewData.mainContent != 'undefined' && newViewData.mainContent != null) {
                	//新场景 的主要内容信息
                	$("#newViewMainContent").append("<p title='"+newViewData.mainContent+"'>"+newViewData.mainContent+"</p>");
				}
                if (oldViewData.mainContent != '' && oldViewData.mainContent != 'undefined' && oldViewData.mainContent != null) {
                	//旧场景的主要内容信息
                	$("#oldViewMainContent").append("<p title='"+oldViewData.mainContent+"'>"+oldViewData.mainContent+"</p>");
				}
				
                //主要演员信息对比
                var nrs = newViewData.roleNames!=null?newViewData.roleNames.split(',').sort().join(''):'';
                var ors = oldViewData.majorActor!=null?oldViewData.majorActor.split(',').sort().join(''):'';
                console.log(nrs+'---主要演员信息对比----'+ors);
                if (nrs != ors) {
					$("#comprViewRoleNames").addClass("different");
				}
                if (newViewData.roleNames != '' && newViewData.roleNames != 'undefined' && newViewData.roleNames != null) {
                	//新场景 的主演信息
                	$("#newViewRoleNames").append("<p title='"+newViewData.roleNames+"'>"+newViewData.roleNames+"</p>");
				}
                if (oldViewData.majorActor != '' && oldViewData.majorActor != 'undefined' && oldViewData.majorActor != null) {
                	//旧场景的主演信息
                	$("#oldViewRoleNames").append("<p title='"+oldViewData.majorActor+"'>"+oldViewData.majorActor+"</p>");
				}
                
                //特约演员信息对比
                var ngn = newViewData.guestNames!=null?newViewData.guestNames.split(',').sort().join(''):'';
                var ogn = oldViewData.guestActor!=null?oldViewData.guestActor.split(',').sort().join(''):'';
                console.log(ngn+'----特约演员信息对比---'+ogn);
                if (ngn != ogn) {
                	$("#comprViewguestNames").addClass("different");
                }
                if (newViewData.guestNames != '' && newViewData.guestNames != 'undefined' && newViewData.guestNames != null) {
                	//新场景 的特约演员信息
                	$("#newViewGuestNames").append("<p title='"+newViewData.guestNames+"'>"+newViewData.guestNames+"</p>");
				}
                if (oldViewData.guestActor != '' && oldViewData.guestActor != 'undefined' && oldViewData.guestActor != null) {
                	//旧场景的特约演员信息
                	$("#oldViewGuestNames").append("<p title='"+oldViewData.guestActor+"'>"+oldViewData.guestActor+"</p>");
				}
                
                //群众演员信息对比
                var nmas = newViewData.massNames!=null?newViewData.massNames.split(',').sort().join(''):'';
                var omas = oldViewData.massesActor!=null?oldViewData.massesActor.split(',').sort().join(''):'';
                console.log(nmas+'----群众演员信息对比---'+omas);
                if (nmas != omas ) {
					$("#comprViewMassNames").addClass("different");
				}
                if (newViewData.massNames != '' && newViewData.massNames != 'undefined' && newViewData.massNames != null) {
                	//新场景 的群众演员信息
                	$("#newViewMassNames").append("<p title='"+newViewData.massNames+"'>"+newViewData.massNames+"</p>");
				}
                if (oldViewData.massesActor != '' && oldViewData.massesActor != 'undefined' && oldViewData.massesActor != null) {
                	//旧场景的群众演员信息
                	$("#oldViewMassNames").append("<p title='"+oldViewData.massesActor+"'>"+oldViewData.massesActor+"</p>");
				}
                
                //服装信息对比
                var ncn = newViewData.clothesNames!=null?newViewData.clothesNames.split(',').sort().join(''):'';
                var ocn = oldViewData.clothes!=null?oldViewData.clothes.split(',').sort().join(''):'';
                console.log(ncn+'---服装信息对比----'+ocn);
                if (ncn != ocn) {
					$("#comprViewClothes").addClass("different");
				}
                if (newViewData.clothesNames != '' && newViewData.clothesNames != 'undefined' && newViewData.clothesNames != null) {
                	//新场景 的服装信息
                	$("#newViewClothes").append("<p title='"+newViewData.clothesNames +"'>"+newViewData.clothesNames +"</p>");
				}
                if (oldViewData.clothes != '' && oldViewData.clothes != 'undefined' && oldViewData.clothes != null) {
                	//旧场景的服装信息
                	$("#oldViewClothes").append("<p title='"+oldViewData.clothes+"'>"+oldViewData.clothes+"</p>");
				}
                
                //化妆信息对比
                var nmake = newViewData.makeupNames!=null?newViewData.makeupNames.split(',').sort().join(''):'';
                var omake = oldViewData.makeups!=null?oldViewData.makeups.split(',').sort().join(''):'';
                console.log(nmake+'---化妆信息对比----'+omake);
                if (nmake != omake) {
					$("#comprViewMakeups").addClass("different");
				}
                if (newViewData.makeupNames != '' && newViewData.makeupNames != 'undefined' && newViewData.makeupNames != null) {
                	//新场景 的化妆信息
                	$("#newViewMakeups").append("<p title='"+newViewData.makeupNames +"'>"+newViewData.makeupNames +"</p>");
				}
                if (oldViewData.makeups != '' && oldViewData.makeups != 'undefined' && oldViewData.makeups != null) {
                	//旧场景的化妆信息
                	$("#oldViewMakeups").append("<p title='"+oldViewData.makeups+"'>"+oldViewData.makeups+"</p>");
				}
                
                //普通道具信息对比
                var nprops = newViewData.propsNames!=null?newViewData.propsNames.split(',').sort().join(''):'';
                var oprops = oldViewData.commonProps!=null?oldViewData.commonProps.split(',').sort().join(''):'';
                console.log(nprops+'---普通道具信息对比----'+oprops);
                if (nprops != oprops) {
					$("#comprViewCommonProps").addClass("different");
				}
                if (newViewData.propsNames != '' && newViewData.propsNames != 'undefined' && newViewData.propsNames != null) {
                	//新场景 的普通道具信息
                	$("#newViewCommonProps").append("<p title='"+newViewData.propsNames +"'>"+newViewData.propsNames +"</p>");
				}
                if (oldViewData.commonProps != '' && oldViewData.commonProps != 'undefined' && oldViewData.commonProps != null) {
                	//旧场景的普通道具信息
                	$("#oldViewCommonProps").append("<p title='"+oldViewData.commonProps+"'>"+oldViewData.commonProps+"</p>");
				}
                
                //备注信息对比
                if (newViewData.remark != oldViewData.remark ) {
					$("#comprViewRemark").addClass("different");
				}
                if (newViewData.remark != '' && newViewData.remark != 'undefined' && newViewData.remark != null) {
                	//新场景 的备注信息
                	$("#newViewRemark").append("<p title='"+newViewData.remark +"'>"+newViewData.remark +"</p>");
				}
                if (oldViewData.remark != '' && oldViewData.remark != 'undefined' && oldViewData.remark != null) {
                	//旧场景的备注信息
                	$("#oldViewRemark").append("<p title='"+oldViewData.remark+"'>"+oldViewData.remark+"</p>");
				}
                
               /* //文武戏信息对比
                if (newViewData.viewType != oldViewData.typeValue ) {
					$("#comprViewViewType").addClass("different");
				}
                var newViewType = $("#newViewType");
                //添加新场景的文武戏的信息
                if (newViewData.viewType == 1) {
                	newViewType.append("<p>文戏</p>");
				}else if (newViewData.viewType == 2) {
					newViewType.append("<p>武戏</p>");
				}else if (newViewData.viewType == 3) {
					newViewType.append("<p>文武戏</p>");
				}
                //添加旧场景的文武戏的信息
                if (oldViewData.type != '' && oldViewData.type != 'undefined' && oldViewData.type != null) {
                	$("#oldViewType").append("<p>"+oldViewData.type+"</p>");
				}*/
             
			}else{
				parent.showErrorMessage(response.message);
			}
		}
	});
}