var crewName = null;
var crewType = null;
var saved = false;
var noticePublished = false;
var isHaveFacdbcak = false;
var groupArray = [{text:"A组",value:"0"},{text:"B组",value:"1"},{text:"C组",value:"2"},{text:"D组",value:"3"},{text:"E组",value:"4"},
                  {text:"F组",value:"5"},{text:"G组",value:"6"},{text:"H组",value:"7"},{text:"I组",value:"8"},{text:"J组",value:"9"},
                  {text:"K组",value:"10"},{text:"L组",value:"11"},{text:"M组",value:"12"},{text:"N组",value:"13"},{text:"O组",value:"14"},
                  {text:"P组",value:"15"},{text:"Q组",value:"16"},{text:"R组",value:"17"},{text:"S组",value:"18"},{text:"T组",value:"19"},
                  {text:"U组",value:"20"},{text:"V组",value:"21"},{text:"W组",value:"22"},{text:"X组",value:"23"},{text:"Y组",value:"24"},
                  {text:"Z组",value:"25"}];


$(document).ready(function(){
	getCrewType();
	initNoticeData();
	confirmShowWindow();
	loadGroupListDta();
	createNewGroup();
	
	loadNoticeData();
	
	//只读权限，不可编辑
	if(isNoticeReadonly){
		$("input[type='text']").attr('disabled',true);
		$("select").attr('disabled',true);
	}
});

//跳转到通告单详情页面需要加载的shuju
function loadNoticeData(){
	//取出通告单id
	var noticeId = $("#noticeId").val();
		$.ajax({
			url: '/notice/queryGenerateNoticeData',
			type: 'post',
			data:{noticeId:noticeId},
			async: false,
			datatype: 'json',
			success: function(response){
				if(response.success){
					//获取反馈列表
					var facdbackList = response.facdbackList;
					if (facdbackList != null && facdbackList.length != 0) {
						isHaveFacdbcak = true;
					}
					var dataMap = response.data;
					noticePublished = dataMap['noticePublished'];
				}
			}
		});
}


//获取当前剧组类型
function getCrewType(){
	$.ajax({
		url: '/scenarioManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}


//跳转过来时，确定要显示第几步页面
function confirmShowWindow(){
	var stepPage = $("#stepPage").val();
	var noticeId = $("#noticeId").val();
	if(stepPage == '2') {//销场
		$("#createNotice").hide();
		$(".btn-list").hide();
	 		//要load的页面
//	 	$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);
		$("#loadNoticeContent").hide();
		$("#noticeContentIframe").show();
		$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+noticeId);
	 	$("li.click").removeClass("click");
	 	$(".nav-ul li:nth-child(3)").addClass("click");
	}else if (stepPage == '3') {//查看
		$("#createNotice").hide();
		$(".btn-list").hide();
 		//要load的页面
//	 	$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+noticeId);
	 	$("#loadNoticeContent").hide();
	 	$("#noticeContentIframe").show();
	 	$("#noticeContentIframe").attr("src","/notice/toGenerateNotice?noticeId="+noticeId);
	 	$("li.click").removeClass("click");
	 	$(".nav-ul li:nth-child(4)").addClass("click");
	}
}

function reLocation(noticeId){
	$("#loadNoticeContent").hide();
 	$("#noticeContentIframe").show();
 	$("#noticeContentIframe").attr("src","/notice/toGenerateNotice?noticeId="+noticeId);
}

//当跳转到第一步时，判断是否需要初始化页面数据
function initNoticeData(){
	var noticeId = $("#noticeId").val();
	if (noticeId != '' && noticeId != null && noticeId != undefined) {
		//根据noticeId查询出通告单的信息
		$.ajax({
			url: '/notice/getNoticeDate',
			type: 'post',
			async: false,
			datatype: 'json',
			data:{noticeId: noticeId},
			success: function(response){
				if(response.success){
					var noticeInfo = response.noticeInfo;
					var noticeDate = new Date(noticeInfo.noticeDate);
					var noticeName = noticeInfo.noticeName;
					
					$("#noticeNameInput").val(noticeName);
					$("#noticeDateDiv").val(noticeDate.getFullYear()+"-"+(noticeDate.getMonth()+1)+"-"+noticeDate.getDate());
					//加载分组选中数据
					var option = $("#groupSelect option");
					for(var i = 0; i<option.length; i++){
						if ($(option[i]).val() == noticeInfo.groupId) {
							$(option[i]).prop('selected', true);
							break;
						}
					}
					
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	}else {
		$("#noticeDateDiv").val(new Date().Format('yyyy-MM-dd'));
		autoGetNoticeName();
	}
}

//获取跳转到通告单详情页面之后需要加载的分组列表数据
function loadGroupListDta(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var groupList = response.groupList;
				//初始化添加到通告单窗口
				initAddViewToNoticeWin(groupList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}

//初始化添加到通告单窗口
function initAddViewToNoticeWin(groupList){
	var $noticeGroup = $("#groupSelect");
	var groupSource = [];
	var i = 1;
    for(var key in groupList){
    	if( i== 1){
    		groupSource.push("<option value='" + key + "' selected='selected'>" + groupList[key] + "</option>");
    		i++;
    		continue;
    	}
    	groupSource.push("<option value='" + key + "'>" + groupList[key] + "</option>");
    }
    groupSource.push("<option value='99'>" + "新增分组" + "</option>");
    
    $noticeGroup.append(groupSource.join(""));
}

//新建分组
function createNewGroup(){
	$("#groupSelect").on('change', function(){
		if ($("select option:selected").val() == '99') {
			//取出当前下拉框的长度
			var selectLength = document.getElementById("groupSelect").length-2;

            if(selectLength > 25){
            	showErrorMessage("目前最多选择到Z组");
                return;
            }
            
            $.ajax({
                url:"/shootGroupManager/saveGroup",
                type:"post",
                dataType:"json",
                data:{groupName:groupArray[selectLength].text},
                async: false,
                success:function(data){
                    if(!data.success){
                    	showErrorMessage(data.message);
                    }else {
                    	var $noticeGroup = $("#groupSelect");
                    	var group = data.group;
                    	$noticeGroup.append(" <option value='" + group.groupId + "' selected='selected'>" + group.groupName + "</option>");
					}
                }
            });
		}
		autoGetNoticeName();
	});
}


//返回
function goNoticePage(own){
	//取出要返回列表时展示的页面
	var showWindow = $("#showWindow").val();
	$("li.click").removeClass("center-li-click last-li-click click");
	window.location.href= "/notice/toNoticeList?window="+showWindow;
	//返回通告单
}
//通告单命名
function noticeName(own){
	var $this = $(own);
	
	//load页面
	var stepPage = $("#stepPage").val();
	if (stepPage == '3') {
		window.noticeContentIframe.saveNoticeWithOutMessage($this);
	}else{
	    $("#stepPage").val('1');
	    
	    $("#noticeContentIframe").hide();
	    $("#loadNoticeContent").hide();
		$this.siblings("li").removeClass("click");
		$(".win-btn-list").hide();
		$(".btn-list").show();
		$("#createNotice").show();
		$this.addClass("click");
	}
	initNoticeData();
}

//改变保存状态
function changeStatus(){
	saved = true;
}

//保存新建通告单
function saveNewNotice(){
	//取出通告单id
	var noticeId = $("#noticeId").val();
	//取出通告日期
	var noticeDateStr = $("#noticeDateDiv").val();
	//分组id
	var groupId = $("#groupSelect option:selected").val();
	//通告单名称
	var noticeName = $("#noticeNameInput").val();
	if (noticeName == '' || noticeName == undefined) {
		showErrorMessage("通告单名称不能为空！");
		return false;
	}
	//错误信息
	var errorMessage = "";
	//先校验通告单是否能够保存
	$.ajax({
		url: "/notice/checkNoticeSaveInfo",
		type: 'post',
		data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr},
		dataType: 'json',
		async: false,
		success: function (param) {
			if (!param.success) {
				errorMessage = param.message;
			}
		}
	});

	//如果有错误信息，则不能保存，也不提示撤销通告单
	if (errorMessage == "") {
		if (saved) {
			//判断当前通告单是否已经发布
			if (noticePublished || isHaveFacdbcak) {
				//有修改，跳出确认框，提示是否修改
				swal({
					title: "是否撤销通告单",
					text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
					type: "warning",
					showCancelButton: true,  
					confirmButtonColor: "rgba(255,103,2,1)",
					confirmButtonText: "是",   
					cancelButtonText: "否",   
					closeOnConfirm: true,   
					closeOnCancel: true
				}, function (isConfirm) {
					//选择撤销通告单会将当前通告单信息保存；如果选择否则不保存当前修改的信息，保持通告单信息不变
					if (isConfirm) {
						totalSaveNoticeInfo('');
					}else{
						totalSaveNoticeInfo('cancleChanged');
					}
				});
				
			}else {
				totalSaveNoticeInfo('');
			}
			
		}else {
			$("#createNotice").hide();
			$(".btn-list").hide();
			/*//要load的页面
			$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);*/
			$("#loadNoticeContent").hide();
			$("#noticeContentIframe").show();
			$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+noticeId);
			$("li.click").removeClass("click").next("li").addClass("click");
			$("#stepPage").val('2');
		}
	}else {
		showErrorMessage(errorMessage);
	}
}

function totalSaveNoticeInfo(cancleChanged){
	//取出通告单id
	var noticeId = $("#noticeId").val();
	//取出通告日期
	var noticeDateStr = $("#noticeDateDiv").val();
	//分组id
	var groupId = $("#groupSelect option:selected").val();
	//通告单名称
	var noticeName = $("#noticeNameInput").val();
	if (noticeName == '' || noticeName == undefined) {
		showErrorMessage("通告单名称不能为空！");
		return false;
	}
	$.ajax({
		url: "/notice/noticeSaveWinoutView",
		type: 'post',
		data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr, cancleChanged:cancleChanged},
		dataType: 'json',
		async: false,
		success: function (param) {
			if (param.success) {
				//showSuccessMessage(param.message);
				var newNoticeId = param.noticeId;
				$("#noticeId").val(newNoticeId);
				$("#createNotice").hide();
				$(".btn-list").hide();
				//要load的页面
//				$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+newNoticeId);
				$("#loadNoticeContent").hide();
				$("#noticeContentIframe").show();
				$("#noticeContentIframe").attr("src", "/notice/toNoticeViewListPage?noticeId="+newNoticeId);
				$("li.click").removeClass("click").next("li").addClass("click");
				//改变导航条
				saved = false;
				//window.location.href="/notice/toNoticeList?noticeId="+noticeId+"&source=2";
				$("#stepPage").val('2');
			} else {
				setTimeout(function(){
					showErrorMessage(param.message);
				},400);
			}
		}
	});
}

//自动生成通告单名称
function autoGetNoticeName() {
	$("#noticeNameInput").val("");
	//取出通告单id
	var noticeId = $("#noticeId").val();
	//分组id
    var groupName =  $("#groupSelect option:selected").text();
    //取出通告日期
	var noticeDateStr = $("#noticeDateDiv").val();
    
    //修改通告单时，设置分组信息会触发到该方法，从而产生通告单名称不对的问题
    //解决方案：关闭新增/修改通告单窗口时，把通告单日期设置为空。修改通告单时，先为分组赋值，此时通告单日期为空，修改通告单名称文本框值的逻辑将不再执行
    if (noticeDateStr == undefined || noticeDateStr == "") {
        return false;
    }
    var noticeDateArr = noticeDateStr.split("-");
    var noticeDate = noticeDateArr[0] + "年"+ noticeDateArr[1] + "月" + noticeDateArr[2] + "日";
    
    $("#noticeNameInput").val("《" + crewName + "》" + noticeDate + groupName+"通告");
    
    $("#addNoticeError").html("");
    
    saved = true;
}

//点击场景配置
function showNoticeViwq(own){
	var stepPage = $("#stepPage").val();
	if (stepPage == '1') {
		saveNewNotice();
		/* $("#createNotice").hide();
 		//要load的页面
		 $("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+newNoticeId);
		$("li.click").removeClass("click").next("li").addClass("click");
		$("li.click").prev("li").removeClass("center-li-click");
		$("li.click").addClass("center-li-click");*/
	}else if (stepPage == '3') {
//		closeGenNotice();
		//父页面调用子页面的关闭方法
		window.noticeContentIframe.closeGenNotice();
		/*$("#createNotice").hide();
		//要load的页面
		$("#loadNoticeContent").load("/notice/toNoticeViewListPage?noticeId="+noticeId);
		$("li.click").removeClass("last-li-click").removeClass("click").prev("li").addClass("click");
		$("li.click").addClass("center-li-click");*/
	}
}

//跳转到附加信息页面时判断需要保存的内容
function showGenerateNotice(own){
	var $this = $(own);
	var stepPage = $("#stepPage").val();
	//取出通告单id
	var noticeId = $("#noticeId").val();
	if (stepPage == '1') {
		//取出通告日期
		var noticeDateStr = $("#noticeDateDiv").val();
		//分组id
		var groupId = $("#groupSelect option:selected").val();
		//通告单名称
		var noticeName = $("#noticeNameInput").val();
		if (noticeName == '' || noticeName == undefined) {
			showErrorMessage("通告单名称不能为空！");
			return false;
		}
		//错误信息
		var errorMessage = "";
		//先校验通告单是否能够保存
		$.ajax({
			url: "/notice/checkNoticeSaveInfo",
			type: 'post',
			data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr},
			dataType: 'json',
			async: false,
			success: function (param) {
				if (!param.success) {
					errorMessage = param.message;
				}
			}
		});
		
		if (saved) {
			if (errorMessage == "") {
				//判断当前通告单是否已经发布
				if (noticePublished || isHaveFacdbcak) {
					//有修改，跳出确认框，提示是否修改
					swal({
						title: "是否撤销通告单",
						text: '您对通告单做了修改，是否从手机端撤销上一版本的通告？',
						type: "warning",
						showCancelButton: true,  
						confirmButtonColor: "rgba(255,103,2,1)",
						confirmButtonText: "是",   
						cancelButtonText: "否",   
						closeOnConfirm: false,   
						closeOnCancel: true
					}, function (isConfirm) {
						if (isConfirm) {
							$.ajax({
								url: "/notice/noticeSaveWinoutView",
								type: 'post',
								data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr, cancleChanged: ''},
								dataType: 'json',
								async: false,
								success: function (param) {
									if (param.success) {
										saved = false;
										var newNoticeId = param.noticeId;
										$("#noticeId").val(newNoticeId);
										
										$("#createNotice").hide();
										$(".btn-list").hide();
										//要load的页面
//										$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+newNoticeId);
										$("#loadNoticeContent").hide();
										$("#noticeContentIframe").show();
										$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+newNoticeId);
										//改变导航条
										$("li.click").removeClass("click");
										$this.addClass("click");
										
										$("#stepPage").val('3');
									} else {
										showErrorMessage(param.message);
									}
								}
							});
						}else {
							$.ajax({
								url: "/notice/noticeSaveWinoutView",
								type: 'post',
								data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr, cancleChanged: 'cancleChanged'},
								dataType: 'json',
								async: false,
								success: function (param) {
									if (param.success) {
										saved = false;
										var newNoticeId = param.noticeId;
										$("#noticeId").val(newNoticeId);
										
										$("#createNotice").hide();
										$(".btn-list").hide();
										//要load的页面
//										$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+newNoticeId);
										$("#loadNoticeContent").hide();
										$("#noticeContentIframe").show();
										$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+newNoticeId);
										//改变导航条
										$("li.click").removeClass("click");
										$this.addClass("click");
										
										$("#stepPage").val('3');
									} else {
										showErrorMessage(param.message);
									}
								}
							});
						}
					});
					
				}else {
					$.ajax({
						url: "/notice/noticeSaveWinoutView",
						type: 'post',
						data: {noticeId: noticeId,noticeName: noticeName, groupId: groupId, noticeDateStr: noticeDateStr, cancleChanged: ''},
						dataType: 'json',
						async: false,
						success: function (param) {
							if (param.success) {
								saved = false;
								var newNoticeId = param.noticeId;
								$("#noticeId").val(newNoticeId);
								
								$("#createNotice").hide();
								$(".btn-list").hide();
								//要load的页面
//							$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+newNoticeId);
								$("#loadNoticeContent").hide();
								$("#noticeContentIframe").show();
								$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+newNoticeId);
								//改变导航条
								$("li.click").removeClass("click");
								$this.addClass("click");
								
								$("#stepPage").val('3');
							} else {
								showErrorMessage(param.message);
							}
						}
					});
				}
			}else {
				showErrorMessage(errorMessage);
			}
			
		}else {
			$("#createNotice").hide();
			$(".btn-list").hide();
			//要load的页面
//			$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+noticeId);
			$("#loadNoticeContent").hide();
			$("#noticeContentIframe").show();
			$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+noticeId);
			//改变导航条
			$("li.click").removeClass("click");
			$this.addClass("click");
			$("#stepPage").val('3');
		}
		
	}else if (stepPage == '2') {
		$("#createNotice").hide();
		$(".btn-list").hide();
		//要load的页面
//		$("#loadNoticeContent").load("/notice/toGenerateNotice?noticeId="+noticeId);
		$("#loadNoticeContent").hide();
		$("#noticeContentIframe").show();
		$("#noticeContentIframe").attr("src", "/notice/toGenerateNotice?noticeId="+noticeId);
		
		$("li.click").removeClass("click");
    	$this.addClass("click");
    	
    	$("#stepPage").val('3');
	}
	
}
