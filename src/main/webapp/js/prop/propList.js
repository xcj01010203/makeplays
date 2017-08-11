$(function(){
	if(isReadonly){
		$(".prop-toolbar-add").remove();
		$(".prop-toolbar-combine").remove();
		$(".prop-toolbar-setType").remove();
		$(".prop-toolbar-delete").remove();
		$('#prop-save-btn').remove();
		$('#prop-delete-btn').remove();
		
		$('.prop-name').attr('disabled',true);
		$('.prop-type').attr('disabled',true);
		$('.prop-num').attr('disabled',true);
		$('.prop-remark').attr('disabled',true);
	}
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest(".prop-toolbar-setType").length == 0){ 
			$("#selectProWrap").hide(); 
		} 
	});
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest("#combinePropType").length == 0){ 
			$("#combinePropTypeSelect").hide(); 
		} 
	});
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest("#propsType").length == 0){ 
			$("#rightPopupSelect").hide(); 
		} 
	});
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest("#searchPropType").length == 0){ 
			$("#searchPropTypeSelect").hide(); 
		} 
	});
	$(document).bind("click",function(e){ 
		var target = $(e.target); 
		if(target.closest(".prop-toolbar-setType").length == 0){ 
			$("#selectProWrap").hide(); 
		} 
	});
	queryPropsInfoList();
});

//搜索道具弹窗
function searchPropPopupWin(){
	closeRightWin();
	var searchHeight = $(document).height();
	$('.prop-search-opaticy').css('height',searchHeight);
	$('#propSearchPopupWrap').show();
}
//搜索道具确定按钮
function searchPropSubmit(){
	$('#propSearchPopupWrap').hide();
}
//取消道具搜索
function searchPropCancel(){
	$('#propSearchPopupWrap').hide();
}
//清空道具搜索弹框
function searchPropClear(){
	$('#propSearchPopupWrap').find('input[type=text]').val('');
	$('#searchPropType').attr('propTypeVal','');
}
//添加道具信息弹窗
function addPropInfoWin(){
	var rightHeight = $(document).height();
	$('.prop-right-opaticy').css('height',rightHeight);
	$('.prop-right-opaticy').animate({"right":"0px"},600);

	$('#hidePropInput').val('');

	$('#propPopupWin').animate({"right": "0px"}, 600);
	//清空所有内容
	$('#propsName').val('');
	$('#propsType').val('');
	$('#propStock').val('');
	$('#propRemark').val('');
	$('.use-record-table-cont').hide();
	$('#prop-delete-btn').hide();
	$('#useData').hide();
}
//关闭道具信息弹窗
function closeRightWin(){
	var right = $("#propPopupWin").width();
	var rightWidth = $(document).width();
	$("#propPopupWin").animate({"right": 0-right}, 600);
	$(".prop-right-opaticy").animate({"right": 0-rightWidth}, 600);
}
//全选，全不选
function checkedAllProp(){
    if($('#checkAll').is(':checked')){//判断全选按钮是否是选中状态
	    $("input[name=checkprop").each(function(){//全选  
	        $(this).prop("checked", true); 
//	        $('#checkAll').prop("checked", true); 
	        $(this).parent().parent().parent().addClass('bg');
	    }); 
	}else{
		$("input[name=checkprop").each(function(){//全不选  
	        $(this).prop("checked", false); 
	        $(this).parent().parent().parent().removeClass('bg');
	    }); 
	}
}
//取消全选
function cancelCheckAll(_this){
	if(!$(_this).is(':checked')){
		$(_this).parent().parent().parent().removeClass('bg');
		$('#checkAll').prop('checked',false);
	}else{
		$(_this).parent().parent().parent().addClass('bg');
	}
        
}
//全部选中之后，全选按钮选中
function isCheckAll(){
	var allLength=$("input[name=checkprop]").length; //所有的checkbox的长度
    $("input[name=checkprop]").each(function(){
        $(this).bind('click',function(){
            var selectedLength=$("input[name=checkprop]:checked").length;//所有的选中的checkbox的长度
            if(selectedLength==allLength){
                $('#checkAll').prop("checked",true);//全选按钮
                
            }else{
                $('#checkAll').prop("checked",false);
            }
        });
    });
	
}
//查询道具信息  /propManager/queryPropsInfoList
function queryPropsInfoList(){
	var propName = $('#searchPropName').val();
	var type = $('#searchPropType').attr('propTypeVal');
	var start = $('#searchPropStart').val();
	var end = $('#searchPropEnd').val();
	var sortType = $("#sortType").val();
	var subData = {'propName':propName,'type':type,'end':end,'start':start,'sortType':sortType};
	$.ajax({
		url: '/propManager/queryPropsInfoList',
		type: 'post',
		dataType: 'json',
		data:subData,
		success: function(response){
			if(response.success){
				if(!response || !response.propInfoList){
					return;
				}
				var data = response.propInfoList;
				loadPropInfo(data);
			 }else{
				 showErrorMessage(response.message);
			 }
		}
	});
}
//加载表格
function loadPropInfo(tableData){
	var len =  tableData.length;
	$('#checkAll').attr("disabled",false);
	if(len == 0){
		$('#propMainInfo').html('');
		$('#propMainInfo').append("<div style='text-align:center;margin-top:50px;'>暂无数据</div>");
		$('#checkAll').attr("disabled",true);
		return;
	}
	var html = '';
	var propsId,propsName,propsType,firstUse,allViewNo,stock,remark;
	for(var i= 0;i < len; i++){
		propsId = tableData[i].propsId;
		propsName = tableData[i].propsName;
		propsType = tableData[i].propsType;
		firstUse = tableData[i].firstUse;
		allViewNo = tableData[i].allViewNo;
		stock = tableData[i].stock;
		remark = tableData[i].remark;
		
		if(!propsName){
			propsName = '';
		}
		if(propsType === 0){
			propsType = '普通道具';
		}else if(propsType == 1){
			propsType = '特殊道具';
		}else if(propsType == 2){
			propsType = '化妆';
		}else if(propsType == 3){
			propsType = '服装';
		}else{
			propsType = "";
		}
		
		if(!firstUse){
			firstUse = '';
		}
		if(!allViewNo){
			allViewNo = '';
		}
		if(!stock){
			stock = '';
		}
		if(!remark){
			remark = '';
		}

		html +='<tr>';
		html +='<td class="width6"><span><input type="checkbox" name="checkprop" id="checkAllProp'+ i +'" value="'+ propsId +'" class="prop-th-allCheck" onclick="cancelCheckAll(this)" onblur="isCheckAll(this)"/></span></td>';
    	html +='<td class="width-name"><a href="javascript:void(0);" class="white-nowrap" onclick="modifyPropInfo(\''+ propsId +'\',this);">'+ propsName +'</a></td>';
    	html +='<td class="width14">'+ propsType +'</td>';
    	html +='<td class="width14">'+ firstUse +'</td>';
    	html +='<td class="width14">'+ allViewNo +'</td>';
    	html +='<td class="width14">'+ stock +'</td>';
    	html +='<td class="width24"><div class="remark-breaks" title="'+remark+'">'+ remark +'</div></td>';
    	html +='</tr>';
	}
	
	$('#propMainInfo').html('');
	$('#propMainInfo').append(html);
	if(isReadonly){
		$('#checkAll').attr("disabled","disabled");
		$("input[name=checkprop").attr("disabled","disabled");
//		$('.width-name a').css('color','#000');
//		$('.width-name a').css('text-decoration','none');
//		$('.width-name a').css('text-decoration','none');
	}
}
//道具名称失去焦点，验证是否重复  propsName
function queryPropName(){
	var propsId = $('#hidePropInput').val();
	var propName = $('#propsName').val();
	var subData = {};
	if(!propName || propName.replace(/(^\s*)|(\s*$)/g, "") ==""){
		
		return ;
	}
	subData.propsName = propName;
	$.ajax({
		url: '/propManager/queryPropsInfoById',
		type: 'post',
		data: subData,
		dataType: 'json',
		success: function(response){
			if(response.success){
				var data = response.propInfoList;
				if(data.length == 0){
				}else if(data.length == 1){
					if(propsId){
						var arr = data[0];
						var pid = data[0].id;
						if(pid != propsId){
							$('#propsName').val('');
							showErrorMessage("名称有重复，请修改");
						}
					}else{
						$('#propsName').val('');
						showErrorMessage("名称有重复，请修改");
					}
				}else{
					$('#propsName').val('');
					showErrorMessage("名称有重复，请修改");
				}
			 }else{
				 showErrorMessage(response.message);
			 }
		}
	});
}
//查询一条道具信息/判断道具名称是否重复  /propManager/queryPropsInfoById propsName
function modifyPropInfo(propsId,_this){
	$(_this).parents('tr').addClass('bg').siblings().removeClass('bg');
	$.ajax({
		url: '/propManager/queryPropsInfoById',
		type: 'post',
		data: {propsId:propsId},
		dataType: 'json',
		success: function(response){
			if(response.success){
				if(!response || !response.propInfoList){
					return;
				}
				addPropInfoWin();
				$('#prop-delete-btn').show();
				$('#useData').show();
				var propsId = response.propInfoList[0].id;//ID
				var propsName = response.propInfoList[0].goodsName;//名称
				var propsType = response.propInfoList[0].goodsType;//类型
				var stock = response.propInfoList[0].stock;//库存数量
				var remark = response.propInfoList[0].remark;//备注
				queryPropsUseInfo(propsId);
				if(!propsName){
					propsName = '';
				}
				if(propsType === 0){
					propsType = '普通道具';
				}else if(propsType == 1){
					propsType = '特殊道具';
				}else if(propsType == 2){
					propsType = '化妆';
				}else if(propsType == 3){
					propsType = '服装';
				}else{
					propsType = '';
				}
				if(!stock){
					stock = '';
				}
				if(!remark){
					remark = '';
				}
				$('#hidePropInput').val(propsId);
				$('#propsName').val(propsName);
				$('#propsType').val(propsType);
				$('#propStock').val(stock);
				$('#propRemark').val(remark);
			 }else{
				 showErrorMessage(response.message);
			 }
		}
	});
}
//删除一条道具信息  /propManager/delPropsInfo
function delProp(){
	var propid =  $('#hidePropInput').val();
	popupPromptBox("提示","是否要删除该条信息？",function(){
		$.ajax({
			url:'/propManager/delPropsInfo',
			type:'post',
			data:{propsIdArray: propid},
			dataType: 'json',
			success: function(data){
				if(data.success){
					showSuccessMessage("删除成功");
					closeRightWin();
					$('#propMainInfo').find('tr.bg').remove();
				}else{
					showErrorMessage(data.message);
				}
			}
		});
	});
}
//获取选中的附件信息id
function getCheckedPropsIds(){
	var propsIdArray = [];
	var checkedInput = $('#propMainInfo').find('input[type=checkbox]:checked');
	for(var i =0,le = checkedInput.length;i<le;i++){
		propsIdArray.push($(checkedInput[i]).val());
	}
	return propsIdArray;
}
//批量删除道具信息
function batchDelPropInfo(){
	var propsIdArray = getCheckedPropsIds();
	if(propsIdArray.length == 0){
		showErrorMessage('请选择需要删除的信息');
		return;
	}
	var propsIds = propsIdArray.join(",");
    popupPromptBox("提示","是否要删除该条信息？",function(){
		$.ajax({
			url:'/propManager/delPropsInfo',
			type:'post',
			data:{propsIdArray: propsIds},
			dataType: 'json',
			success: function(data){
				if(data.success){
					showSuccessMessage("删除成功");
					$('#propMainInfo').find('tr.bg').remove();
				}else{
					showErrorMessage(data.message);
				}
			}
		});
	});
}
//保存道具信息 /propManager/savePropsInfo
//参数 ：propName（道具名称）type（道具类型） remark（备注） stock（库存量）
function savePropsInfo(){
//	$('#prop-save-btn').prop('disabled','disabled');
	var propsId = $('#hidePropInput').val();
	var propName = $('#propsName').val();
	var propValue10 = $('#propsType').val();
	var stock = $('#propStock').val();
	var remark = $('#propRemark').val();
	var subData = {};
	if(propsId){
		subData.propsId =propsId;
	}else{
		subData.propsId =propsId;
	}
	if(propName){
		subData.propName =propName;
	}else{
		showErrorMessage("名称不为空");
		
		return false;
	}
	if(propValue10){
		if(propValue10 == '普通道具'){
			$('#propsType').attr('propTypeVal',0);;
		}else if(propValue10 == '特殊道具'){
			$('#propsType').attr('propTypeVal',1);;
		}else if(propValue10 == '服装'){
			$('#propsType').attr('propTypeVal',3);;
		}else if(propValue10 == '化妆'){
			$('#propsType').attr('propTypeVal',2);;
		}
		var type = $('#propsType').attr('propTypeVal');
		subData.type =type;
	}else{
		showErrorMessage("请选择类型");
		return false;
	}
	
	if(stock){
		subData.stock =stock;
	}else{
		showErrorMessage("库存量不为空");
		return false;
	}
	if(!remark || remark ==''){
		remark = ' ';
	}
	subData.remark =remark;
	$.ajax({
		url:'/propManager/savePropsInfo',
		type:"post",
		dataType:"json",
		data:subData,
		success:function(response){
			if(response.success){
				showSuccessMessage("保存成功");
				closeRightWin();
				//location.reload();
				queryPropsInfoList();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
function showPropsType(){
	$('#selectProWrap').toggle();
}
function hidePropsType(){
	$('#selectProWrap').hide();
}
//批量修改道具类型：/propManager/updatePropsType 参数：propsIdArray（道具id）type（道具类型）
function setPropsType(type){
	var propsIdArray = getCheckedPropsIds();
    if(propsIdArray.length == 0){
    	showErrorMessage('请选择服化道信息');
    	return;
    }
    var propsId = propsIdArray.join(",");
    $('#selectProWrap').show();
    $.ajax({
		url:'/propManager/updatePropsType',
		type:"post",
		dataType:"json",
		data:{propsIdArray:propsId,type:type},
		success:function(response){
			if(response.success){
				showSuccessMessage("修改成功");
				//location.reload();
				queryPropsInfoList();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//显示合并道具窗口
function combinePropPopupWin(){
	var propsIdArray = getCheckedPropsIds();
	if(propsIdArray.length < 2 ){
		showErrorMessage('请选择需要合并的服化道信息');
		return;
	}
	closeRightWin();
	var searchHeight = $(document).height();
	$('.prop-search-opaticy').css('height',searchHeight);
	$('#combinePropWin').show();
}
//导出道具表
function exportPropTab(){
	var dataLength = $("#propMainInfo tr:visible").length;
	var sortType = $("#sortType").val();
	if(dataLength && dataLength > 0){
		window.location.href = '/propManager/exportPropsInfo?sortType='+sortType;
	}else{
		showErrorMessage("没有可导出的数据");
	}
	
	
}
//搜索确定按钮,参数：propName（道具名称） type（道具类型） start（开始场数） end（结束场数）
function searchPropSubmit(){
	$('#propSearchPopupWrap').hide();
	queryPropsInfoList();
}
//合并道具信息：/propManager/updateCombinePropsInfo 
//参数：idArray（道具id） propName(道具名称) type(道具类型) remark(备注)
function combinePropInfo(){
	var propName = $('#combinePropName').val();
	var type = $('#combinePropType').attr('propTypeVal');
	var remark = $('#combinePropRemark').val();
	var idArray =  getCheckedPropsIds();
	var propsId = idArray.join(",");
	
	if(!propName || propName.replace(/(^\s*)|(\s*$)/g, "") == ''){
		showErrorMessage("请填写名称");
		return false;
	}
	if(!type){
		showErrorMessage("请选择类型");
		return false;
	}
	
	var subData = {'idArray':propsId,'propName':propName,'type':type,'remark':remark};
	$.ajax({
		url:'/propManager/updateCombinePropsInfo',
		type:"post",
		dataType:"json",
		data:subData,
		success:function(response){
			if(response.success){
				showSuccessMessage('合并成功');
				combinePropCancel();
				//location.reload();
				queryPropsInfoList();
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//隐藏合并道具窗口
function combinePropCancel(){
	$('#combinePropWin').hide();
}
//显示合并道具类型下拉框
function showCombineSelect(){
	$('#combinePropTypeSelect').show();
}
//查询具体使用情况：/propManager/queryPropsUseInfo 参数：propId（道具id）
function queryPropsUseInfo(propsId){
	$.ajax({
		url:'/propManager/queryPropsUseInfo',
		type:"post",
		dataType:"json",
		data:{propId:propsId},
		success:function(response){
			if(response.success){
				if(!response.propsUseInfoList){
					return;
				}
				
				var html = '';
				var len = response.propsUseInfoList.length;
				var useData = response.propsUseInfoList;
				
				if(len === 0){
					$('.use-record-table-cont').hide();
					$('.show-prop-use-status').hide();
					$('.show-prop-use-status2').show();
					return;
				}
				$('.use-record-table-cont').show();
				$('.show-prop-use-status').hide();
				$('.show-prop-use-status2').hide();
				//建立在数据排序好的前提下
				var temp = "";
				var rowspan = 1;
				var merges = [];
				var mer = {};
				for(var i = 0,le = len;i<le;i++){
					//合计
					var shootId = useData[i].shootId;
					var shootName = useData[i].shootName;
					if(temp == ''){
						mer = {};
						mer.index = i;
						temp = shootId;
					}else if(temp !=shootId){
						temp = shootId;
						mer.rowspan = rowspan;
						
						merges.push(mer);
						mer = {};
						rowspan = 1;
						mer.index = i;
					}else{
						rowspan ++;
					}
					if(i == le -1){
						mer.rowspan = rowspan;
						merges.push(mer);
					}
				}
				for(var i = 0,le = merges.length;i<le;i++){
					var index = merges[i].index;
					var rnum = merges[i].rowspan;
					for(var m = 0,lem = useData.length;m<lem;m++){
						var shootN = useData[m].shootName?useData[m].shootName:"暂无拍摄地";
						var proplocation = useData[m].location;
						if(!proplocation ){
							proplocation = '';
						}
						if(index == m){
							if (useData[m].number != '0') {
								html += '<tr class="right-prop-cont">';
								html += '<td rowspan = '+rnum+' style="text-align:center;">'+ shootN +'</td>';
								html += '<td>'+ proplocation +'</td>';
								html += '<td>'+ useData[m].number +'</td>';
								html += '</tr>';
							}
						}else if(m < index +rnum && m > index){
							if (useData[m].number != '0') {
								html += '<tr class="right-prop-cont">';
								html += '<td>'+ proplocation +'</td>';
								html += '<td>'+ useData[m].number +'</td>';
								html += '</tr>';
							}
						}else{
						}
					}
				}
				$('.right-prop-cont').remove();
				$('.use-record-table-cont').append(html);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//选择道具类型
function selectPropsType(){
	$('#rightPopupSelect').show();
}
//选择道具类型
function selectPopupType(own){
	var $this = $(own);
	$('#propsType').val($this.text());
	var propValue10 = $('#propsType').val();
	if(propValue10 == '普通道具'){
		$('#propsType').attr('opotion-id',0);;
	}else if(propValue10 == '特殊道具'){
		$('#propsType').attr('opotion-id',1);;
	}else if(propValue10 == '服装'){
		$('#propsType').attr('opotion-id',3);;
	}else if(propValue10 == '化妆'){
		$('#propsType').attr('opotion-id',2);;
	}
	$('#rightPopupSelect').hide();
}
//搜索弹框选择道具类型
function searchPropType(own){
	var $this = $(own);
	$('#searchPropType').val($this.text());
	var propValue10 = $('#searchPropType').val();
	if(propValue10 == '普通道具'){
		$('#searchPropType').attr('propTypeVal',0);
	}else if(propValue10 == '特殊道具'){
		$('#searchPropType').attr('propTypeVal',1);
	}else if(propValue10 == '服装'){
		$('#searchPropType').attr('propTypeVal',3);
	}else if(propValue10 == '化妆'){
		$('#searchPropType').attr('propTypeVal',2);
	}else{
		$('#searchPropType').val('');
		$('#searchPropType').attr('propTypeVal','');
	}
	$('#searchPropTypeSelect').hide();
}
function showSearchWrap(){
	$('#searchPropTypeSelect').show();
}
//道具合并择选择道具类型
function combinePropType(own){
	var $this = $(own);
	$('#combinePropType').val($this.text());
	var propValue10 = $('#combinePropType').val();
	if(propValue10 == '普通道具'){
		$('#combinePropType').attr('propTypeVal',0);
	}else if(propValue10 == '特殊道具'){
		$('#combinePropType').attr('propTypeVal',1);;
	}else if(propValue10 == '服装'){
		$('#combinePropType').attr('propTypeVal',3);
	}else if(propValue10 == '化妆'){
		$('#combinePropType').attr('propTypeVal',2);
	}
	$('#searchPropTypeSelect').hide();
}

//只允许输入非零的正整数
function onlyNumber(own){
	var $this = $(own);
	$this.val($this.val().replace(/[^\d]/g,""));
	$this.val($this.val().replace(/\D/g,'', ""));
}

//按照指定的排序规则对道具表进行排序 
function sortPropList(own) {
	//取消其它按钮的样式
	var sortBtnArr = $("span[class*='sort-btn']");
	for(var i=0; i<sortBtnArr.length; i++){
		$(sortBtnArr[i]).removeClass("select");
	}
	//添加样式
	$(own).addClass("select");
	
	//设置排序规则
	var sortType = $(own).attr("sval");
	$("#sortType").val(sortType);
	
	queryPropsInfoList();
}