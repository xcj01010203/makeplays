var operateDot = '';  //当前操作的节点
var clickId = [];  //当前点击节点
$(document).ready(function () {
	//页面splitter初始化
	$("#authSpliter").jqxSplitter({
		theme:theme,
        width: '99%',
        height: '99%',
        resizable: false,
        splitBarSize: 1,
        //showSplitBar: false,
        panels: [
           { size: 373, min: 373, collapsible: false },
           {size: '100%',min: 800 ,collapsible: false}]
    });
	
	$('#right_main').jqxSplitter({
        orientation: 'horizontal', 
        width: '100%', 
        height: "100%", 
        resizable: false,
        showSplitBar: false,
        panels: [
            {size: 270, collapsible: false}, 
            {size:'100%', collapsible: false}
        ]
    });
	loadAuthorityTree('pcTree',2);//pc
	loadAuthorityTree('mobileTree',3);//app
	//修改页面不可填写
	$('.con_right_title input[type=text]').attr("disabled","disabled").removeClass("cur_1");
	$('.con_right_title select').attr("disabled","disabled").removeClass("cur_1");
	
	//添加权限弹窗
    $('#addAuthWindow').jqxWindow({
    	theme:theme,
    	width: 400,
	    height: 500, 
	    autoOpen: false,
		resizable: false, 
	    isModal: true,
	    cancelButton: $('#cancelAuth'),
	    initContent: function () {
	    	
	    }
	});
    //初始化button
    $(':button').jqxButton({theme:theme, width: 60, height: 25 });
    //添加权限验证
    $('#addAuthForm').jqxValidator({
	    hintType: 'label',
	    animationDuration: 1,
	    rules: [
	        { input: '#authName', message: '权限名不可为空!', action: 'keyup, blur', rule: 'required' }
	    ]
	});
    $('#updateAuthForm').jqxValidator({
	    hintType: 'label',
	    animationDuration: 1,
	    rules: [
	        { input: '#authNameRight', message: '权限名不可为空!', action: 'keyup, blur', rule: 'required' },
	    ]
	});
    //tab切换事件,该权限的角色分布、该权限的剧组分布
    $(".tab_wrap li").click(function(){
    	if(!$(this).hasClass('tab_li_current')) {
    		$(this).siblings().removeClass('tab_li_current');
    		$(this).addClass('tab_li_current');
    		if($(this).index()==0) {
    			$("#authRoleTable").show();
    			$("#authCrewTable").hide();
    		} else {
    			$("#authRoleTable").hide();
    			$("#authCrewTable").show();
    		}
    	}
    });
});
//加载权限树
function loadAuthorityTree(divId,type) {
	$('#'+divId).treegrid({
	    height:'100%',
	    animate:true,
	    width:340,
	    url:'/authorityManager/queryAuthorityList',
	    idField:'id',
	    treeField:'name',
	    method: 'post',
	    autoRowHeight:true,
		loadMsg:'数据正在加载，请等待',
		dnd:true,
		queryParams: {
			type: type,
		},
	    columns:[[
				
	        {title:'权限名称',field:'name',width:220,
	            formatter:function(value,row) {
	            	var html = '';
	            	if(!(row.id == '0' || row.id == '1'))
	            		html="<span><a href='javascript:gotoUpdateAuth(\""+row.id+"\");' title='编辑'>"+value+"</a><span class='subjectClass' style='display:none;'>";
	            	else
	            		html="<span><a href='javascript:void(0);'>"+value+"</a><span class='subjectClass' style='display:none;'>";
   	              	
   	              	html += "</span></span>";
   	              	
   	              	return html;
	            }
    	    },
    	    {title:'权限url',field:'authUrl',width:100,
    	    	formatter:function(value,row) {
	            	var html = "<span class='subjectClass' style='display:none;'>";
	            		
   	              	html += "<span class='celladdclass' onclick='addAuth(\""+row.id+"\",\""+row.authPlantform+"\")' title='添加'><img style='width: 18px; height:18px;' src='"+ctx+"/css/finance/image/plus.png'/></span>";
   	              	if(!(row.id == '0' || row.id == '1')){
       	              	html += "<span class='celldeleclass' onclick='deleteAuth(\""+row.id+"\")' title='删除'><img style='width: 18px; height:18px;' src='"+ctx+"/css/finance/image/icon-delete.png'/></span>";
   	              	}
   	              	
   	              	html += "</span>";
   	              	
   	              	return html;
	            }
    	    },
	    ]],
	    onLoadSuccess: function(row){
            $(this).treegrid('enableDnd', row?row.id:null);
           //定位
           if(operateDot != ''){
        	   $(this).treegrid('select',operateDot);
           }
        },
        onDragOver:function(targetRow, sourceRow){
      	   if(targetRow._parentId!=sourceRow._parentId){
          		return false;
          	}
       },
        onBeforeDrag:function(Row){
        	
        },
        onBeforeDrop: function(targetRow, sourceRow, point){
        	if(targetRow.fid!=sourceRow.fid){
        		return false;
        	}
        	if(point == 'append'){
        		return false;
        	}        	
        },
        onStopDrag: function(row){        	
        	
        },
        onClickRow:function(row){
        	
        },	   
        onDrop:function(targetRow, sourceRow, point){
        	if(targetRow._parentId!=sourceRow._parentId){
          		return false;
          	}
          	if(point == 'append'){
          		showInfoMessage("拖动只能在同级之间进行！");
          		return false;
          	}
    	    var parent = $('tr[node-id='+sourceRow.id+']').parent();
          	var child = [];
          	parent.children('tr').each(function(i,n){
          		var nodeid = $(this).attr('node-id');
          		if(nodeid != undefined){
          			child.push(nodeid);
          		}
          	});
           	
           	$.ajax({
           		url: '/authorityManager/updateAuthoritySequence',
           		data: {ids:child.join(',')},
           		dataType: 'json',
           		type: 'post',
           		success: function(response){
           			if(response.success){
           				showSuccessMessage("更新权限顺序成功！");
           			}else{
           				showErrorMessage(response.message);
           			}
           		}
           	});
        }
	});
}
//pc、mobile切换
function tabChange(id) {
	if(id==0) {//pc
		var _this=$("#pcImg");
		if(_this.hasClass('pcImg1')){
			_this.removeClass('pcImg1').addClass('pcImg');
			$("#mobileImg").removeClass().addClass('mobileImg1');
			$("#operateType").val('0');
			$('#authPlantform').val(2);
			$("#mobiletreediv").hide();
			$("#pctreediv").show();
		}
	}else if(id==1) {//mobile
		var _this=$("#mobileImg");
		if(_this.hasClass('mobileImg1')){
			_this.removeClass('mobileImg1').addClass('mobileImg');
			$("#pcImg").removeClass().addClass('pcImg1');
			$("#operateType").val('1');
			$('#authPlantform').val(3);
			$("#mobiletreediv").show();
			$("#pctreediv").hide();
		}
	}
}
//添加权限
 function addAuth(id){
	$("#authCode").removeClass('jqx-validator-error-element').next('label').remove();
	//清空信息
	$('#authId').val('');
	if(id) {
		$('#parentId').val(id);	
	}else{
		$('#parentId').val($("#operateType").val());
	}
	$('#sequence').val('');
	$('#authName').val('');
	$('#operType').val('0');
	$('#operDesc').val('');
	$('#authUrl').val('');
	$('#status').val('0');
	$('#ifMenu').val('1');
	$('#differInRAndW').val('0').trigger('change');
	$('#defaultRorW').val('2');
	$('#authCode').val('');
	$('#isForAllCrew').val('0');
	//打开窗口
	$('#addAuthWindow').jqxWindow('open');
}
//保存权限信息
function saveAuth(){
	if(!$('#addAuthForm').jqxValidator('validate')){//验证不通过
		return;
	}
	var authcode = $("#authCode").val();//操作编码
	var authId = $('#authId').val();
	if(authcode){//验证操作编码是否已存在
		$.ajax({
    		url:'/authorityManager/validateAuthCode',
    		data:{authCode:authcode,authId:authId},
    		dataType:'json',
    		type:'post',
    		success:function(data){
    			if(data.success) {
    				submitAuthInfo();
    			} else {
    				if(data.flag && data.flag==1) {//操作编码已存在
    					$("#authCode").addClass('jqx-validator-error-element').after("<label class='jqx-validator-error-label' style='position: relative; left: 0px; width: 240px; top: 2px; display: block;'>编码已存在！</label>");
    		       		$("#authCode").unbind("keyup");
    		       		$("#authCode").on("keyup",function(){
    		       			$("#authCode").removeClass('jqx-validator-error-element').next('label').remove();
    		       		});
    				} else {
    					showErrorMessage(data.message);
    				}
    			}
    		}
    	});
	}else{
		submitAuthInfo();
	}
}
//新增权限信息-保存
function submitAuthInfo() {
	$.ajax({
		url:'/authorityManager/saveAuthority',
		data:$("#addAuthForm").serialize(),
		dataType:'json',
		type:'post',
		success:function(data){
			if(data.success) {
				showSuccessMessage(data.message);
				
				$('#addAuthWindow').jqxWindow('close');
				
				if($("#operateType").val() == 0){//pc
					$('#pcTree').treegrid('reload');
				}else{//app
					$('#mobileTree').treegrid('reload');
				}
			} else {
				showErrorMessage(data.message);
			}
		}
	});
}
//显示权限信息
function gotoUpdateAuth(authId){
	$.ajax({
		url:'/authorityManager/queryOneAuthority',
		data:{authId:authId},
		dataType:'json',
		type:'post',
		success:function(data){
			if(data.success) {
				var node=data.result;
				operateDot = node.authId;
				$('#authId').val(node.authId);
				$('#parentId').val(node.parentId);
				//var sib = $('tr[node-id='+node.authId+']').prevAll();
				$('#sequence').val(node.sequence);
				$('#authNameRight').val(node.authName);
				$('#operDescRight').val(node.operDesc);
				$('#authUrlRight').val(node.authUrl);
				$('#statusRight').val(node.status);
				$('#ifMenuRight').val(node.ifMenu);
				$('#authCodeRight').val(node.authCode);
				if (node.differInRAndW) {
				   $("#differInRAndWRight").val(1);
				   $("#defaultRorWRight").parents("tr").show();
				} else {
				   $("#differInRAndWRight").val(0);
				   $("#defaultRorWRight").parents("tr").hide();
				}
				$("#defaultRorWRight").val(node.defaultRorW);
				$("#cssNameRight").val(node.cssName);
				
				$('.con_right_title input[type=text]').attr("disabled","disabled").removeClass("cur_1");
				$('.con_right_title select').attr("disabled","disabled").removeClass("cur_1");
				$('#modifyauthroity').attr('title','修改');
				
				$("#modifyauthroity").show();
				
				clickId = [];
				clickId.push(node.authId);
				clickId.push(node.parentId);
				showAuthRole(node.authId, node.parentId);
				showAuthCrew(node.authId, node.parentId);
				
				if($("#modifyauthroity").hasClass('authSave')){
					$("#modifyauthroity").removeClass().addClass('authModify');
				}
			} else {
				showErrorMessage(data.message);
			}
		}
	});
}
//显示权限的角色分布
function showAuthRole(authId,parentId){
	$('#btnsure').attr('authId',authId);
	$.ajax({
		url:'/authorityManager/queryAllRoleByAuthId',
		data:{authId:authId,parentId:parentId},
		dataType:'json',
		type:'post',
		success:function(response){
			if(response.success) {
				var data = response.result;

				var dt1 = "<dt><input type='checkbox' class='authGroup' id='";
				var dt1_1 = "<dt><input type='checkbox' class='' id='";
				var dt2 = "'/>";
				var dt3 = "</dt>";
				var dd1 = "<dd class='grayColor'><input type='checkbox' class='authcheck authChild' initial='0' id='";
				var dd1_1 = "<dd class='blackColor'><input type='checkbox' class='authcheck authChild' initial='1' checked id='";
				var dd4 = "' pid='";
				var dd2 = "'/>";
				var dd3 = "</dd>";
				var html = [];
				html.push(dt1_1 + 'checkAllAuth' + dt2 + '全选' + dt3);
				$.each(data,function(i,v){
					if(!(v.childList.length == 0 && v.parentId == '00')){
						html.push(dt1 + v.roleId + dt2 + v.roleName+dt3);
						if(v.childList.length == 0){
							if(v.status == 0){
								html.push(dd1 + v.roleId + dd4 + v.roleId + dd2 + v.roleName+dd3);
							}else{
								html.push(dd1_1 + v.roleId + dd4 + v.roleId + dd2 + v.roleName+dd3);
							}
							
						}else{
							$.each(v.childList,function(i1,v1){
								if(v1.status == 0){
									html.push(dd1 + v1.roleId + dd4 + v1.parentId + dd2 + v1.roleName+dd3);
								}else{
									html.push(dd1_1 + v1.roleId + dd4 + v1.parentId + dd2 + v1.roleName+dd3);
								}
								
							});
						}
					}
					
					
				});
				$("#authRoleTable").html(html.join(''));
				
				$('#checkAllAuth').click(function(){
					if($(this).is(':checked')){
						$("#authRoleTable").find('input[type=checkbox]').prop('checked',true)
						.parent().removeClass().addClass('blackColor');
					}else{
						$("#authRoleTable").find('input[type=checkbox]').prop('checked',false)
						.parent().removeClass().addClass('grayColor');
					}
				});
				$("#authRoleTable").find('dt:eq(0)').nextAll().each(function(){
					$(this).find('input[type=checkbox]').on('change',function(){
						var _this = $(this);
						if(_this.is(':checked')){
							_this.parent().removeClass().addClass('blackColor');
							if(_this.hasClass('authcheck')){
								var b = false;
								var pid = _this.attr('pid');
								_this.parent().siblings('dd').each(function(){
									if($(this).find('input').not(':checked').attr('pid') == pid){
										b = true;
										return false;
									}
								});
								if(!b){
									$('input[id='+_this.attr('pid')+']').prop('checked',true);
								}
							}
						}else{
							_this.parent().removeClass().addClass('grayColor');
							if(_this.hasClass('authcheck')){
								$('input[id='+_this.attr('pid')+']').prop('checked',false);
							}
						}
					});
				});
				
				$('.authGroup').on('click',function(){
					var tid = $(this).attr('id');
					if($(this).is(':checked')){
						$(this).parent().nextAll('dd').find('input[type=checkbox]').each(function(){
							if($(this).attr('pid') == tid){
								$(this).prop('checked',true).parent().removeClass().addClass('blackColor');
							}
						});
						
					}else{
						$(this).parent().nextAll('dd').find('input[type=checkbox]').each(function(){
							if($(this).attr('pid') == tid){
								$(this).prop('checked',false).parent().removeClass().addClass('grayColor');
							}
						});
					}
				});
			} else {
				showErrorMessage(response.message);
			}
		},
		error:function(){
			
		}
	});	
}
//显示权限的剧组分布
function showAuthCrew(authId,parentId){
	$.ajax({
		url:'/authorityManager/queryAllCrewByAuthId',
		data:{authId:authId,parentId:parentId},
		dataType:'json',
		type:'post',
		success:function(response){
			if(response.success) {
				var data = response.result;
				var isAuthUsedByCommonRole=response.isAuthUsedByCommonRole;
				var validhtml=[];
				var expiredhtml=[];
				validhtml.push('<dt><span class="span-title">有效剧组：</span>&nbsp;&nbsp;<input type="checkbox" class="" id="checkAllValidCrew">全选</dt>');
				expiredhtml.push('<dt><span class="span-title">过期剧组：</span>&nbsp;&nbsp;<input type="checkbox" class="" id="checkAllExpiredCrew">全选</dt>');
				for(var i=0;i<data.length;i++){
					var html=[]
					var obj=data[i];
					if(obj.status==1) {
						html.push('<dd class="blackColor" title="'+obj.crewName+'">');
					}else{
						html.push('<dd class="grayColor" title="'+obj.crewName+'">');
					}
					html.push('<input type="checkbox" name="crewChk" id="'+obj.crewId+'"');
					if(obj.status==1) {
						html.push(' checked initial="1" ');
					} else {
						html.push(' initial="0" ');
					}
					if(!isAuthUsedByCommonRole) {
						html.push(' disabled ');
					}
					html.push('>'+obj.crewName);
					html.push('</dd>');
					if(obj.outofdate==1) {
						validhtml.push(html.join(''));
					} else {
						expiredhtml.push(html.join(''));
					}
				}
				$("#validCrewDiv").html(validhtml.join(''));
				$("#expiredCrewDiv").html(expiredhtml.join(''));
				
				$('#checkAllValidCrew').click(function(){
					if($(this).is(':checked')){
						$("#validCrewDiv").find('input[type=checkbox]').prop('checked',true)
						.parent().removeClass().addClass('blackColor');
					}else{
						$("#validCrewDiv").find('input[type=checkbox]').prop('checked',false)
						.parent().removeClass().addClass('grayColor');
					}
				});
				$('#checkAllExpiredCrew').click(function(){
					if($(this).is(':checked')){
						$("#expiredCrewDiv").find('input[type=checkbox]').prop('checked',true)
						.parent().removeClass().addClass('blackColor');
					}else{
						$("#expiredCrewDiv").find('input[type=checkbox]').prop('checked',false)
						.parent().removeClass().addClass('grayColor');
					}
				});
				$("input[name='crewChk']").click(function(){
					if($(this).is(':checked')){
						$(this).parent().removeClass().addClass('blackColor');
					}else{
						$(this).parent().removeClass().addClass('grayColor');
					}
				});
			} else {
				showErrorMessage(response.message);
			}
		},
		error:function(){
			
		}
	});	
}
//是否区分读写操作值改变事件
function differInRAndWRightChange(own) {
    var value = $(own).val();
    if (value == 1) {
        $("#defaultRorWRight").parents("tr").show();
    } else {
        $("#defaultRorWRight").parents("tr").hide();
    }
}
function differInRAndWChange(own) {
    var value = $(own).val();
    if (value == 1) {
        $("#defaultRorW").parents("tr").show();
    } else {
        $("#defaultRorW").parents("tr").hide();
    }
}
//修改权限信息
function updateAuth(obj){
	var _this = $(obj);
	if(_this.hasClass('authModify')){
		_this.removeClass().addClass('authSave');
		$('.con_right_title input[type=text]').removeAttr("disabled").addClass("cur_1");
		$('.con_right_title select').removeAttr("disabled").addClass("cur_1");
		$('#modifyauthroity').attr('title','保存');
	}else{		
		if(!$('#updateAuthForm').jqxValidator('validate')){
			return;
		}
		var authcode = $('#authCodeRight').val();
		var authId = $('#authId').val();		
		if(authcode){
			$.ajax({
        		url:'/authorityManager/validateAuthCode',
        		data:{authCode:authcode,authId:authId},
        		dataType:'json',
        		type:'post',
        		success:function(data){
        			if(data.success) {
        				submitAuthInfoForUpdate();
        			} else {
        				if(data.flag && data.flag==1) {//操作编码已存在
        					$("#authCodeRight").addClass('jqx-validator-error-element').after("<label class='jqx-validator-error-label' style='position: relative; left: 0px; width: 240px; top: 2px; display: block;'>编码已存在！</label>");
        		       		$("#authCodeRight").unbind("keyup");
        		       		$("#authCodeRight").on("keyup",function(){
        		       			$("#authCodeRight").removeClass('jqx-validator-error-element').next('label').remove();
        		       		});
        				} else {
        					showErrorMessage(data.message);
        				}
        			}
        		}
        	});
		}else{
			submitAuthInfoForUpdate();
		}
	}
}
//修改权限-保存
function submitAuthInfoForUpdate() {
	var authcode = $('#authCodeRight').val();
	var authId = $('#authId').val();
    var differInRAndW = false;
    if ($("#differInRAndWRight").val() == 1) {
        differInRAndW = true;
    }
	var params = {
			authId:authId,
			parentId:$('input[name=parentId]').val(),
			sequence:$('input[name=sequence]').val(),
			authPlantform:$('input[name=authPlantform]').val(),
			operType:$('input[name=operType]').val(),
			authName:$('input[name=authNameRight]').val(),
			operDesc:$('input[name=operDescRight]').val(),
			authUrl:$('input[name=authUrlRight]').val(),
			status:$('select[name=statusRight]').val(),
			ifMenu:$('select[name=ifMenuRight]').val(),
			authCode:authcode,
			differInRAndW: differInRAndW,
			defaultRorW: $("select[name=defaultRorWRight]").val(),
			cssName:$('input[name=cssNameRight]').val()
	};
	$.ajax({
		url:'/authorityManager/saveAuthority',
		data:params,
		dataType:'json',
		type:'post',
		success:function(data){
			if(data.success) {
				showSuccessMessage(data.message);
				$("#modifyauthroity").removeClass().addClass('authModify');
				$('.con_right_title input[type=text]').attr("disabled","disabled").removeClass("cur_1");
				$('.con_right_title select').attr("disabled","disabled").removeClass("cur_1");
				$('#modifyauthroity').attr('title','修改');
				
				if($("#operateType").val() == 0){//pc
					$('#pcTree').treegrid('reload');
				}else{//app
					$('#mobileTree').treegrid('reload');
				}
			} else {
				showErrorMessage(data.message);
			}
		},
		error:function(){}
	});
}
//删除权限
function deleteAuth(id){
	popupPromptBox("提示","是否确定删除？",function(){
		$.ajax({
			url:'/authorityManager/deleteAuthority',
			data:{authId:id},
			dataType:'json',
			type:'post',
			success:function(data){
				if(data.success){
					showSuccessMessage("删除成功！");
					
					$("#authRoleTable").empty();
					$("#validCrewDiv").empty();
					$("#expiredCrewDiv").empty();
					$("#modifyauthroity").hide();
					//清空修改
					$('#authId').val('');
					$('#parentId').val('');
					$('#sequence').val('');
					$('#authNameRight').val('');
					$('#operDescRight').val('');
					$('#authUrlRight').val('');
					$('#statusRight').val('');
					$('#ifMenuRight').val('');
					$('#authCodeRight').val('');
					
					if($("#operateType").val() == 0){//pc
						$('#pcTree').treegrid('reload');
					}else{//app
						$('#mobileTree').treegrid('reload');
					}
				}else{
					showInfoMessage(data.message);
				}
			},
			error:function(){
				
			}
		});
	});
}
//保存权限分布
function saveAuthDis(){
	if($(".tab_wrap li").eq(0).hasClass('tab_li_current')) {
		saveAuthRole();
	} else {
		saveAuthCrew();
	}
}
//保存权限对应的角色
function saveAuthRole() {
	var authId = $('#btnsure').attr('authId');
	var result = [];
	$('.authcheck').each(function(){
		var s = [];
		var initial = $(this).attr('initial');
		
		if($(this).is(':checked')){
			if(initial == '0'){
				s.push($(this).attr('id'));
				s.push(1);
				result.push(s.join('-'));
			}
		}else{
			if(initial == '1'){
				s.push($(this).attr('id'));
				s.push(0);
				result.push(s.join('-'));
			}
		}
		
	});
	
	if(result.length>0){
		swal({
		    title: "提示",
	        text: '是否修改相关的用户权限信息？',
	        type: "warning",
	        showCancelButton: true,  
	        confirmButtonColor: "rgba(255,103,2,1)",
	        confirmButtonText: "是",   
	        cancelButtonText: "否",   
	        closeOnConfirm: false,   
	        closeOnCancel: true
		},function (isConfirm){
			var clientWidth=window.screen.availWidth;
			//获取浏览器页面可见高度和宽度
	        var _PageHeight = document.documentElement.clientHeight,
	            _PageWidth = document.documentElement.clientWidth;
			//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	        var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	            _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	        //在页面未加载完毕之前显示的loading Html自定义内容
	        var _LoadingHtml = $("#loadingDiv");
	        //呈现loading效果
	        _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	        _LoadingHtml.show();
	        $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
	        $.ajax({
				url:'/authorityManager/saveAuthRoleMap',
				data:{authId:authId,roles:result.join(','),isDelete:isConfirm},
				dataType:'json',
				type:'post',
				success:function(data){
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					if(data.success){						
						showSuccessMessage("修改成功！");
					}else{
						showErrorMessage(data.message);
					}
					showAuthRole(clickId[0],clickId[1]);
					showAuthCrew(clickId[0],clickId[1]);
				},
				error:function(){
					
				}
			});
		});		
	}
}

//保存权限对应的剧组
function saveAuthCrew() {
	var authId = $('#btnsure').attr('authId');
	var result = [];
	$('input[name="crewChk"]').each(function(){
		var s = [];
		var initial = $(this).attr('initial');
		
		if($(this).is(':checked')){
			if(initial == '0'){
				s.push($(this).attr('id'));
				s.push(1);
				result.push(s.join('-'));
			}
		}else{
			if(initial == '1'){
				s.push($(this).attr('id'));
				s.push(0);
				result.push(s.join('-'));
			}
		}		
	});
	
	if(result.length>0){
		popupPromptBox("提示","是否确定保存？",function(){
			var clientWidth=window.screen.availWidth;
			//获取浏览器页面可见高度和宽度
	        var _PageHeight = document.documentElement.clientHeight,
	            _PageWidth = document.documentElement.clientWidth;
			//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
	        var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
	            _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
	        //在页面未加载完毕之前显示的loading Html自定义内容
	        var _LoadingHtml = $("#loadingDiv");
	        //呈现loading效果
	        _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
	        _LoadingHtml.show();
	        $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
			$.ajax({
				url:'/authorityManager/saveAuthCrewMap',
				data:{authId:authId,crews:result.join(',')},
				dataType:'json',
				type:'post',
				success:function(data){
					_LoadingHtml.hide();
					$(".opacityAll").hide();
					if(data.success){						
						showSuccessMessage("修改成功！");
					}else{
						showErrorMessage(data.message);
					}
					showAuthCrew(clickId[0],clickId[1]);
				},
				error:function(){
					
				}
			});
		});
	}
}