/**
 * 社区管理js
 */
var filter={pageNo: 1, pagesize: 50};
$(function(){
	//初始化分页事件
	initPagation();
	//初始化查询窗口
    $('#queryWindow').jqxWindow({
		theme:theme,
		width: 560,
        height: 250,
		resizable: false, 
        autoOpen: false,
        isModal: true,
        cancelButton: $('#closeSearchSubmit'),
        initContent: function () {
        }
	});
    //button
    $("#searchSubmit").jqxButton({theme:theme, width: 65, height: 25});
    $("#closeSearchSubmit").jqxButton({theme:theme, width: 65, height: 25});
    $("#clearSearchButton").jqxButton({theme:theme, width: 65, height: 25});
});
//显示组讯
function showTeamInfo() {
	location.href="/teamInfoManager/toCommunityPage?teamType=1";
}
//显示寻组
function showSearchTeam() {
	location.href="/teamInfoManager/toCommunityPage?teamType=2";
}
//初始化分页事件
function initPagation(){
	$.ajax({
		url: '/searchTeamManager/querySearchTeamList',
		type: 'post',
		data: filter,
		datatype: 'json',
		success:  function(response){
			if(response.success){
				var total = response.total;
				if (total == 0 || total == null) {
					total = 1;
				}
				var pageCount = response.pageCount;
				var searchTeamList = response.searchTeamList;
				$('#tablePage').html("");
				loadSearchTeamList(searchTeamList);

				kkpager.generPageHtml({
					pno : filter.pageNo,
					//总页码
					total : pageCount,
					//总数据条数
					totalRecords : total,
					mode : 'click',//默认值是link，可选link或者click
					click : function(n){
						this.selectPage(n);
						filter.pageNo=n;
						$.ajax({
							url: '/searchTeamManager/querySearchTeamList',
							type: 'post',
							data: filter,
							datatype: 'json',
							success: function(response){
								var searchTeamList = response.searchTeamList;
								//加载表格数据
								loadSearchTeamList(searchTeamList);
							}
						});
					}
				}, true);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//加载寻组列表
function loadSearchTeamList(dataList){
	var tableHtml=[];
	if(dataList){
		for(var i=0;i<dataList.length;i++){
			var obj=dataList[i];
			tableHtml.push('<tr>');
			tableHtml.push("<td style='width:50px;'><input type='checkbox' name='searchTeamChk' id='"+obj.searchTeamId+"' onclick='checkOne(this)'></td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 5); text-align: left;' title='"+obj.realName+"'><a class='link' id='"+obj.userId+"' onclick='showUserDetail(this)'>"+obj.realName+"</a></td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 5); text-align: left;' title='"+nullToEmptyStr(obj.likePositionName)+"'>"+nullToEmptyStr(obj.likePositionName)+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 5);'>"+obj.currentStartDate+"至"+obj.currentEndDate+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 5);'>"+obj.phone+"</td>");
			tableHtml.push("<td style='width:calc((100% - 50px) / 5);'>"+obj.createTime+"</td>");
			tableHtml.push('</tr>');
		}
	}
	$("#searchTeamList").html(tableHtml.join(''));
}
//全选
function checkAll(own){
	$("input[name='searchTeamChk']").prop('checked',$(own).prop('checked'));
}
//选择框选中事件
function checkOne(own){
	if(!$(own).prop('checked')) {
		if($("#checkAll").prop('checked')){
			$("#checkAll").prop('checked',false);
		}
	}else{
		var isAllCheck=true;
		$("input[name='searchTeamChk']").each(function(){
			if(!$(this).prop('checked')){
				isAllCheck=false;
				$("#checkAll").prop('checked',false);
				return;
			}
		});
		if(isAllCheck){
			$("#checkAll").prop('checked',true);
		}
	}
}
//将空字符串处理成''
function nullToEmptyStr(obj){
	if(!obj){
		return '';
	}
	return obj;
}
//删除寻组信息
function deleteMulSearchTeam() {
	var searchTeamIdArr = [];
	$("input[name='searchTeamChk']:checked").each(function(){
		searchTeamIdArr.push($(this).attr('id'));
	});
	if(searchTeamIdArr.length==0){
		showInfoMessage('请选择要删除的信息!');
		return;
	}
	$.ajax({
		url: '/searchTeamManager/deleteSearchTeam',
		type: 'post',
		data: {searchTeamId:searchTeamIdArr.join(',')},
		datatype: 'json',
		success: function(response){
			if(response.success){
				showSuccessMessage("删除成功");
				initPagation();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//显示查询窗口
function openAdvanceSearch(){
	$('#queryWindow').jqxWindow('open');
}
//清空查询条件
function clearSearchCon(){
	$("#queryWindow").find('input[type="text"]').val('');
	$("#queryWindow").find('input[type="radio"]').prop('checked',false);
}
//查询寻组
function querySearchTeam() {
	var likePositionName=$("#searchLikePositionName").val();
	if(likePositionName) {
		filter.likePositionName=likePositionName;
	}else{
		filter.likePositionName="";
	}
	var searchAge = $("input[name='searchAge']:checked").val();
	if(searchAge) {
		switch(searchAge) {
			case "1":
				filter.minAge=4;
				filter.maxAge=14;
				break;
			case "2":
				filter.minAge=15;
				filter.maxAge=24;
				break;
			case "3":
				filter.minAge=25;
				filter.maxAge=34;
				break;
			case "4":
				filter.minAge=35;
				filter.maxAge=44;
				break;
			case "5":
				filter.minAge=45;
				break;
		}
	}else{
		filter.minAge="";
		filter.maxAge="";
	}
	var sex = $("input[name='searchSex']:checked").val();
	if(sex) {
		filter.sex=sex;
	}else{
		filter.sex="";
	}
	initPagation();
	$('#queryWindow').jqxWindow('close');
}
//跳转到用户管理页面
function showUserDetail(own) {
	window.location.href = "/userManager/toUserListPage?type=1&userId="+$(own).attr('id');
}