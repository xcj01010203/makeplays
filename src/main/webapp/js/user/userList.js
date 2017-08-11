var cellids;
var crewFilter={};
var filter = {};
var currentUserInfo=null;
$(document).ready(function () {
	//加载用户列表
	if(openType == 1){
    	$("#searchUser").val(openUserInfo);
    	searchUser("#searchUser");
    }else{
    	loadUserListGrid();
    }
	//初始化button
	$(':button').jqxButton({theme:theme, width: 100, height: 25 });	
	//用户搜索按钮
	$('.icon_cha1').on('click',function(){    	
    	searchUser('#searchUser');
    });
	//剧组搜索按钮
	$('.icon_cha').on('click',function(){
    	var val = $('#searchCrewInput').val();
    	crewFilter.crewName = val;
	    addCrewToUserGrid();
    });
	//创建用户窗口
	$("#adminWindow").jqxWindow({ 
		theme: theme,
		height:550,
		width:380, 
		resizable: false, 
		isModal: true, 
		autoOpen: false,
		initContent: function () {
			
		}  		  	
	});
	//给用户添加剧组
    $("#addCrewToUser").jqxWindow({ 
    	theme: theme,
    	height: 800,
    	width:800, 
    	resizable: false, 
    	isModal: true, 
    	autoOpen: false
    });
    //高级搜索
    $("#superQueryWindow").jqxWindow({
    	theme: theme,
    	height: 310,
    	width:500, 
    	resizable: false, 
    	isModal: true, 
    	autoOpen: false,
        cancelButton: $('#closeSearchSubmit'),
    });
    //初始化全选checkbox事件
    //allChkFunction($("input[name='status_search']"));
    allChkFunction($("input[name='userType_search']"));
    
	// 获取系统部门职务信息
	loadRoleList();
	
	//表单校验器
    $('#adminForm').jqxValidator({
    	hintType: 'label',
     	animationDuration: 0,
        rules: [
            { input: '#realNameInput', message: '用户姓名不能为空', action: 'keyup,blur', rule: 'required' },
            { input: '#ubCreateCrewNum', message: '可建组次数不能为空', action: 'keyup,blur', rule: 'required' },
            { input: '#passwordInput', message: '只支持数字、字母、特殊字符、长度需大于6', action: 'keyup,blur', rule: function(input, commit){
//          	  if($("#operateStatus").val()== '1'){
//               		return true;
//               		}
          	  var myreg =/^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~]{6,22}$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
                return true;
            } },
            { input: '#passwordConfirmInput', message: '密码不一致！', action: 'keyup,blur', rule: function(input, commit){
                if(input.val()!=$("#passwordInput").val()){ 
                	//$("#passwordConfirmInput").val("");
                    return false; 
                } 
                return true;
            } },
            { input: '#phone', message: '手机号不合法!', action: 'keyup,blur', rule: function(input, commit){
            	
    			if(input.val().length==0){ 
                    return true; 
                }
    			
            	if(input.val().length!=11){ 
                    return false; 
                } 
                 
                var myreg =   /^(1(([34578][0-9])|(76)))\d{8}$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
                
                
            	return true;
            } },
            
      		 { input: '#emailInput', message: '邮箱格式不正确!', action: 'keyup,blur', rule: function(input, commit){
      			if(input.val().length==0){ 
                    return true; 
                }
                var myreg =/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
            	return true;
            } },
        ]
    });
    //表单校验器
    $('#updateForm').jqxValidator({
    	hintType: 'label',
     	animationDuration: 0,
        rules: [
            { input: '#realNameUpdateInput', message: '用户姓名不能为空', action: 'keyup,blur', rule: 'required' },
            { input: '#ubCreateCrewNumUpdateInput', message: '可建组次数不能为空', action: 'keyup,blur', rule: 'required' },
            { input: '#passwordUpdateInput', message: '只支持数字、字母、特殊字符、长度需大于6', action: 'keyup,blur', rule: function(input, commit){
            	if(input.val().length==0){ 
                    return true; 
                }
          	  var myreg =/^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~]{6,22}$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
                return true;
            } },
            { input: '#passwordConfirmUpdateInput', message: '密码不一致！', action: 'keyup,blur', rule: function(input, commit){
                if(input.val()!=$("#passwordUpdateInput").val()){ 
                	//$("#passwordConfirmInput").val("");
                    return false; 
                } 
                return true;
            } },
            { input: '#phoneUpdate', message: '手机号不合法!', action: 'keyup,blur', rule: function(input, commit){
            	$("#phoneUpdateDiv").hide();
    			if(input.val().length==0){ 
                    return true; 
                }
    			
            	if(input.val().length!=11){ 
                    return false; 
                } 
                 
                var myreg =   /^(1(([34578][0-9])|(76)))\d{8}$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
                
                
            	return true;
            } },
            
      		 { input: '#emailUpdateInput', message: '邮箱格式不正确!', action: 'keyup,blur', rule: function(input, commit){
      			if(input.val().length==0){ 
                    return true; 
                }
                var myreg =/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                if(!myreg.test(input.val())){ 
                    return false; 
                } 
            	return true;
            } },
        ]
    });
    //剧组类型变化
    $("#searchCrewType li").on("click",function(){
    	var _this = $(this);
    	if(!_this.hasClass('font_color')){
    		_this.addClass('font_color').siblings().removeClass('font_color');
        	crewFilter.crewType = _this.attr('sid');
		    addCrewToUserGrid();
    	}
    });
    
    $('#searchCrewInput').on('keyup',function(){
    	var val = $(this).val();
    	crewFilter.crewName = val;
	    addCrewToUserGrid();
    });
    //用户类型选择事件
    $("input[name='userType']").click(function(){
    	if($(this).val()==2){
    		$("#serverTypeTr").show();
    	} else {
    		$("#serverTypeTr").hide();
    	}
    });
});
// 获取系统部门职务信息
//data:{needManager:true},
function loadRoleList(){
    $.ajax({
        url: "/sysrole/queryCrewDepartmentAndDuties",
        type: "post",
        success: function(response) {
            if (response.success) {
                var roleOptions = [];
            
                var roleList = response.roleList;
                $.each(roleList, function(index, item) {
                    var roleName = item.roleName;
                    var child = item.child;
                    
                    roleOptions.push("<optgroup label='"+ roleName +"'>");
                    
                    $.each(child, function(index, cItem) {
                        var cRoleId = cItem.roleId;
                        var cRoleName = cItem.roleName;
                        
                        roleOptions.push("<option value='" + cRoleId + "'>"+ cRoleName +"</option>");
                    });
                    roleOptions.push("</optgroup>");
                });
                
                $("#roleSelect").append(roleOptions.join(""));
                
                // 初始化职务下拉框
                $("#roleSelect").multiSelect();
            } else {
                alert(response.message);
            }
        }
    });
}
//加载用户列表
function loadUserListGrid(){
	var gridsource =
    {
        datatype: "json",
        datafields: [
			{ name: 'userId',type: 'string' },
			{ name: 'userName',type: 'string' },
	        { name: 'realName',type: 'string' },
	        { name: 'type',type: 'int' },
	        { name: 'sex',type: 'int' },
	        { name: 'status',type: 'int' },
	        { name: 'phone',type: 'string' },
	        { name: 'email',type: 'string' },
	        { name: 'token',type: 'string' },
	        { name: 'clientType',type: 'string' },
	        { name: 'appVersion',type: 'string' },
	        { name: 'isCrew',type: 'int' },
	        { name: 'isKefu',type: 'int' },
	        { name: 'ubCreateCrewNum', type: 'int'},
	        { name: 'createTime', type: 'date'},
	        { name: 'crewNum', type: 'int'},
	        { name: 'enabledCrewNum', type: 'int'},
	        { name: 'userCrewNum', type: 'int'},
	        { name: 'userEnabledCrewNum', type: 'int'},
	        { name: 'roleIds', type: 'string'}
        ],
        type:'post',
        beforeprocessing:function(data){
        	// 查询之后可执行的代码
        	gridsource.totalrecords=data.result.total;
        },
        root:'resultList',
        data:filter,
        processdata: function (data) {
            // 查询之前可执行的代码
        },
        url:'/userManager/queryUserList',
        //点击表头时排序(必须)  
        sort: function() {  
            $("#userListGrid").jqxGrid('updatebounddata','sort');  
        },  
        /*//默认排序属性(可不写)  
        sortcolumn: 'id',  
        sortdirection: 'asc'*/ 
    };
	
	var sexColumn = function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
		var sex = rowdata.sex;
		if(sex!=null){
			sex = sexMap.get(rowdata.sex);
		}else{
			sex='';
		}
    	html = "<div class='colstyle'>"+sex+"</div>";
    	return html;
       
    };
    
    var rendergridrows = function (params) {
    	// 调用json返回的列表数据
        return params.data;
    };
    $("#userListGrid").remove();
    $(".griddiv").append("<div id='userListGrid' class='userListGrid'></div>");
    var dataAdapter = new $.jqx.dataAdapter(gridsource);
    $("#userListGrid").jqxGrid(
    {
        width: '100%',
        height: '100%',
        source: dataAdapter,
        selectionmode:"none",
        columnsresize: true,
        sortable: true,
        altrows: true,
        rowsheight: 30,
        pagesize: 50,
        pageable: true,
        pagesizeoptions: ['50', '100', '全部'],
        pagerbuttonscount: 5,
        //这两个是后台分页所必需的属性，不设置无法跳下一页
        virtualmode :true,
        rendergridrows:rendergridrows,
        localization:localizationobj,// 表格文字设置
        cellhover: function (cellhtmlElement, x, y) {// 鼠标悬浮事件
        	var ids= $(cellhtmlElement).find("span[id^='link_span']").attr("id");
        	var sib = $(cellhtmlElement).siblings().find("span[id^='link_span']").attr("id");
	        if(typeof(sib)=="undefined"){
	        	$('.userdelete_span').hide();
	        }
	        $("#"+ids).show();
        },
        columns: [
          { text: '姓名', datafield: 'realName',cellsAlign: "center" ,align: 'center', width: '10%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		  var html ="<div class='colstyle' style='text-align:left;margin-left: 5px;'><a href='#' title='修改' onclick='updateUser(\""+rowdata.userId+"\")'>"+value+"</a>";
        		  if(rowdata.userId != "0") {
        			  html+='<span class="userdelete_span" id="link_span'+row+'" onclick="deleteUser(\''+rowdata.isCrew+'\',\''+rowdata.userId+'\',\''+rowdata.type+'\')" title="删除用户"></span>';
        		  }
        		  html+="</div>";
        		  return html;
        	  }	  
          },
          { text: '状态', datafield: 'status', align: 'center', width: '6%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		  var status=rowdata.status=='1'?'有效':'无效';
         		 return '<div class="colstyle">'+status+'</div>';
         	  } 
           },
           { text: '类型', datafield: 'type', align: 'center', width: '6%',
         	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
         		 var type='';
         		 if(rowdata.type==0) {
         			 type='普通用户';
         		 } else if(rowdata.type==1) {
         			 type='系统管理员';
         		 } else if(rowdata.type==2) {//客服
         			 if(rowdata.roleIds==majorCustomerService) {
         				 type='总客服';
         			 } else if(rowdata.roleIds==seniorCustomerService) {
         				 type='高级客服';
         			 } else if(rowdata.roleIds==middleCustomerService) {
         				 type='中级客服';
         			 } else if(rowdata.roleIds==juniorCustomerService) {
         				 type='初级客服';
         			 }
         		 }
         		 return '<div class="colstyle">'+type+'</div>';
          	  } 
            },
          { text: '可建组次数', datafield: 'ubCreateCrewNum', align: 'center', cellsAlign: "center" ,width: '7%' },
          { text: '加入剧组数', datafield: 'crewNum', align: 'center', width: '7%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		 return '<div class="colstyle">'+rowdata.crewNum+'('+rowdata.enabledCrewNum+')</div>';
        	  } 
          },
          { text: '在组数', datafield: 'userCrewNum', align: 'center', width: '7%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		 return '<div class="colstyle">'+rowdata.userCrewNum+'('+rowdata.userEnabledCrewNum+')</div>';
        	  } 
          },
          { text: '客户端类型', datafield: 'clientType', align: 'center', width: '7%',
        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
        		  var html='';
        		  if(rowdata.clientType) {
        			  if(rowdata.token) {
        				  html+='<div class="text_green">';
        			  } else {
        				  html+='<div class="text_gray">';
        			  }
        			  if(rowdata.clientType=='1'){
        				  html+='ios';
        			  }else if(rowdata.clientType=='2') {
        				  html+='and';
        			  }else if(rowdata.clientType=='3') {
        				  html+='pad';
        			  }
        			  html+='</div>';
        		  }
        		  return html;
        	  } 
          },
          { text: '版本号', datafield: 'appVersion', cellsAlign: "center" ,align: 'center', width: '7%' },
          { text: '性别', datafield: 'sex', cellsrenderer: sexColumn,cellsAlign: "center" ,align: 'center', width: '7%' },
          { text: '手机', datafield: 'phone',cellsAlign: "center" ,align: 'center', width: '12%' },
          { text: '邮箱', datafield: 'email',cellsAlign: "center" ,align: 'center', width: '12%' },
          { text: '创建时间', datafield: 'createTime',cellsAlign: "center" ,align: 'center', width: '12%',cellsformat: 'yyyy-MM-dd HH:mm:ss'}
        ],
        ready:function(){
        	$("#centerDIV").css({margin: 0,width: '590px'});
        	if(openType == 1){
        		updateUser(openUserId);
            }
        }
    });
    $("#userListGrid").bind("pagechanged", function (event) {
    	// 翻页时的事件绑定
    });
    $("#userListGrid").on('rowclick', function (event) {
  	   var args = event.args;
  	    // row's bound index
  	    var boundIndex = args.rowindex;
  	    var ids="row"+boundIndex+"userListGrid"; 
  	    if(cellids!=ids){
  	    	 $("#"+cellids).find("div").css('background-color','#ffffff');
  	    }
  	    $("#"+ids).find("div").css('background-color','#d1d1d1');
  	    cellids=ids;
 	});
}
//模糊查询用户
function searchUser(obj){	
	var valueObj=$(obj).val();
	
	filter = [];
	if(valueObj == ''){
		filter.phone='';
		filter.realName='';
	}else if(valueObj.length<2 ){
		filter.realName=valueObj;
		filter.phone='';
	}else{
		filter.phone=valueObj;
		filter.realName=valueObj;
	}
	loadUserListGrid();
}
//打开创建用户窗口
function createUser(){
//	$("#operateStatus").val("0");
	$("#userId").val("");
	$("#userName").val("");
	$("#passwordInput").val("");
	$("#realNameInput").val("");
	$("#passwordConfirmInput").val("");
	$("#emailInput").val("");
	$("#phone").val("");
	$("#sex").val("1");
	$("#statusUser").val("1");
	$("input[name='userType']").eq(0).prop('checked',true).trigger('click');
	$("input[name='serverType']").eq(0).prop('checked',true);
	
	$("#usernameDiv").hide();
	$("#phoneDiv").hide();
	$('#adminWindow').jqxWindow('setTitle', '创建用户');
	$("#adminWindow").jqxWindow('open');
}
//创建用户关闭事件
function closeAdmin(){
	$("#adminWindow").jqxWindow('close');
}
//创建用户提交表单
function addUser(){
	if (!$('#adminForm').jqxValidator('validate')) {
        return;
    }
	
	var userType = $("input[name='userType']:checked").val();
	var roleId='';
	if(userType == 2) {
		roleId = $("input[name='serverType']:checked").val();
	}
	$.ajax({
         url: '/userManager/saveUser',
         data: {
        	 crewId:$("#adminCrewId").val(),
        	 password:$("#passwordInput").val(),
        	 realName:$("#realNameInput").val(),
        	 sex:$("#sex").val(),
        	 email:$("#emailInput").val(),
        	 phone:$("#phone").val(),
        	 status:$("#statusUser").val(),
        	 ubCreateCrewNum: $("#ubCreateCrewNum").val(),
        	 type:userType,
        	 roleId:roleId
         },
         type: 'post',
         async: false,
         dataType: 'json',
         success: function(data) {        	 
        	 if (data.success) {
	        	showSuccessMessage(data.message);
        		$("#userListGrid").jqxGrid("updatebounddata",'cells');
        		$("#adminWindow").jqxWindow('close');
				}else{
					showErrorMessage(data.message);
				}
         }
    });
}
//删除用户
function deleteUser(isCrew,userId,userType){
	$("#rightMain").hide();
	if(isCrew>0) {
		showInfoMessage("当前用户已有剧组，请先从剧组中删除该用户。");
		return;
	}
	var tips="是否删除此用户？";
	if(userType==2){
		tips="该用户是客户服务，确定要删除吗？";
	}
	popupPromptBox("提示",tips,function(){
		$.ajax({
			url:"/userManager/deleteUser",
			data:{userId:userId},
			dataType:"json",
			type:"post",
			success:function(data){
				if(data.isSuccess == '1'){
					showSuccessMessage("删除成功！");
					$('#userListGrid').jqxGrid("updatebounddata",'cells');
				}else if(data.isSuccess == '2'){
					showErrorMessage("当前用户已有剧组，不能删除！");
				}else{
					showErrorMessage("删除失败！");
				}
			}
		});
	});	
}
//打开修改用户右侧div
function updateUser(userId){
//	$("#operateStatus").val('1');
	$("#usercrewTab").trigger('click');
	$.ajax({
		url:'/userManager/queryUserById',
	    type:'post',
	    data:{
	      	userId:userId,
	    },
	    async: false,
	    dataType: 'json',
	    success:function(data){
	    	if(data.success) {
	    		currentUserInfo=data.result;
	    		var userInfo=data.result;
	      		$("#updateForm").show();
	      		$("#userId").val(userInfo.userId);
	      		$("#userNameUpdate").val(userInfo.userName);
	      		$('#oldUserName').val(userInfo.userName);
	      		$("#passwordUpdateInput").val("");
	      		$("#passwordConfirmUpdateInput").val("");
	      		$("#realNameUpdateInput").val(userInfo.realName);
	      		$("#emailUpdateInput").val($.trim(userInfo.email));
	      		$("#phoneUpdate").val($.trim(userInfo.phone));
	      		$("#sexUpdate").val(userInfo.sex);
	      		$('#oldPhone').val(userInfo.phone);
	            $("#ubCreateCrewNumUpdateInput").val(userInfo.ubCreateCrewNum);
	      		$("#statusUserUpdate").val(userInfo.status);
	      		$("#usernameUpdateDiv").hide();
	      		$("#phoneUpdateDiv").hide();
	      		if(userInfo.type==0){
		      		$("#sendMsgBtn").show();
	      		} else {
		      		$("#sendMsgBtn").hide();
	      		}
	      		//显示用户类型
	      		var html='';
	      		if(userInfo.type==0){
	      			html='普通用户';
	      		}else if(userInfo.type==1){
	      			html='系统管理员';
	      		}else if(userInfo.type==2){
	      			currentUserInfo.roleId=data.roleId;
	      			if(data.roleId==majorCustomerService) {
		      			html='总客服';
	      			} else {
		      			html='<label><input type="radio" name="serverTypeUpdate" value="'+seniorCustomerService+'">高级</label> &nbsp;&nbsp;'
		                    +'<label><input type="radio" name="serverTypeUpdate" value="'+middleCustomerService+'">中级</label> &nbsp;&nbsp;' 
		                    +'<label><input type="radio" name="serverTypeUpdate" value="'+juniorCustomerService+'">初级</label>';
	      			}
	      		}
      			$("#userTypeTd").html(html);
      			if(userInfo.type==2){
      				$("input[name='serverTypeUpdate'][value='"+data.roleId+"']").prop('checked',true);
      			}
	      		//显示用户剧组
	      		showUserCrew(userId);
	      		$("#rightMain").show();
	    	} else {
	    		showErrorMessage(data.message);
	    	}
	    }
	});	
}
//关闭右侧div
function closeUpdate(){
	$("#rightMain").hide();
}
//显示用户剧组
function showUserCrew(userId) {
	$('#add').attr('userId',userId);
	if(userId != '0' && currentUserInfo.roleId != majorCustomerService){
  		//显示用户工作经历
  		showUserWorkHistory(userId);
		$.ajax({
			url:'/userManager/queryUserCrews',
			data:{userId:userId},
			dataType:'json',
			type:'post',
			success:function(data){
				if(data.success) {
					
					var html = [];
					
					var tb1 = " <div onclick='crewUserInfo(this)' userId='"+userId+"' crewId='";
					var tb1_1 = "' class='cards_box ";
					var tb1_2 = "'><div class='card_title'>《";
					var tb2 = "》</div><div class='card_job'>剧组职务:";
					var tb3 = "</div>";	
					var tb4 = "</div>";			
					
					$("#add").show();
					$.each(data.result, function(i,v){
						var cur = "";
						cur += tb1 + v.crewId + tb1_1 + tb1_2;
						if(v.tcrewId != '0'){
							cur += v.crewName+tb2;
							
							if(v.roleName != null)
								cur += v.roleName+tb3;
							else
								cur += "无"+tb3;
							
							//如果为客服，显示删除剧组按钮
							if(currentUserInfo.type==2) {
								cur+="<div class='card_oper' title='删除用户剧组' onclick='deleteCrewFromCrewUser(\""+userId+"\",\""+v.crewId+"\")'></div>";
							}
							cur+=tb4;
						}else{
							cur = "<table><tr><td>当前用户角色为客户服务</td></tr></table>";
							$("#add").hide();
						}
						
						html.push(cur);
					});
					$('.con_right_bot_tit').show();
					if(data.result.length==0){
						$("#con_right_bottom").hide();
						$("#con_right_bottom1").show();
						/*if(currentUserInfo.type==0) {
							$("#customservice").show();
						} else {
							$("#customservice").hide();
						}*/
					}else{
						$("#con_right_bottom").show();
						$("#con_right_bottom1").hide();
						/*$("#customservice").hide();*/
					}
					$("#con_right_bottom").html(html.join(''));
					//如果为客服，显示删除剧组按钮
					if(currentUserInfo.type==2) {
						//添加鼠标悬浮事件
						$(".cards_box").hover(function()
						{
							$(this).find(".card_oper").show();
						},
						function()
						{
							$(this).find(".card_oper").hide();
						});
					}
					$("#rightMain").show();
				}else{
					showErrorMessage(data.message);
				}
			},
			error:function(){
				
			}
		});
	}else{
		$('.con_right_bot_tit').hide();
		$("#user_crew").hide();
		$("#user_work_history").hide();
	}	
}
//显示用户工作经历
function showUserWorkHistory(userId) {
	$.ajax({
		url:'/userManager/queryWorkExperAndUserInfo',
		data:{userId:userId},
		dataType:'json',
		type:'post',
		success:function(response){
			if(response.success) {
				var html=[];
				var userInfo=response.userInfo;
				html.push('<div class="user_work_title"><div></div><label>个人简介</label></div>');
				html.push('<div class="user_introduction">');
				if(userInfo.profile) {
					html.push(userInfo.profile);
				}
				html.push('</div>');
				
				var workList=response.workList;
				html.push('<div class="user_work_title"><div></div><label>工作经历</label></div>');
				if(workList && workList.length>0) {
					for(var i=0;i<workList.length;i++){
						var obj=workList[i];
						html.push('<ul class="ul_user_work">');
						html.push('<li><label>剧组名称：</label>'+obj.crewName+'</li>');
						if(obj.positionName) {
							html.push('<li><label>职务：</label>'+obj.positionName+'</li>');
						}else{
							html.push('<li><label>职务：</label></li>');
						}
						html.push('<li><label>拍摄时间：</label>'+obj.joinCrewDate+' 至 '+obj.leaveCrewDate+'</li>');
						if(obj.workrequirement){
							html.push('<li><label>主要职责：</label>'+obj.workrequirement+'</li>');
						}else{
							html.push('<li><label>主要职责：</label></li>');
						}
						if(obj.experienceId) {
							html.push('<li><div class="del_div" id="'+obj.experienceId+'" userId="'+userId+'" onclick="deleteWorkExperience(this)" title="删除"></div></li>');
						}
						html.push('</ul>');
					}
				}else{
					
				}
				$("#user_work_history").html(html.join(''));
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//删除工作经历
function deleteWorkExperience(own) {
	popupPromptBox("提示","确定删除吗?",function(){
        $.ajax({
            url:"/userManager/deleteWorkExper",
            type:"post",
            dataType:"json",
            data:{
            	experienceId:$(own).attr('id'),
            	userId:$(own).attr('userId')
            },
            success:function(data){
                if(data.success){
                    showSuccessMessage("删除成功!");
                    showUserCrew($(own).attr('userId'));
                }else{
                    showErrorMessage(data.message);
                }
            }
        });
    });
}
//删除客服剧组关联关系
function deleteCrewFromCrewUser(userId, crewId){
	popupPromptBox("提示","确定删除吗?",function(){
        $.ajax({
            url:"/userManager/deleteCrewUserMap",
            type:"post",
            dataType:"json",
            data:{
                userId:userId,
                crewId:crewId
            },
            async:true,
            success:function(data){
                if(data.success){
                    showSuccessMessage(data.message);
                    $("#userListGrid").jqxGrid("updatebounddata",'cells');
                    showUserCrew(userId);
                }else{
                    showErrorMessage(data.message);
                }
            }
        });
    });
	stopPropagation();
}
function stopPropagation(e) {
    e = e || window.event;
    if(e.stopPropagation) { //W3C阻止冒泡方法
        e.stopPropagation();
    } else {
        e.cancelBubble = true; //IE阻止冒泡方法
    }
}

// 修改用户
function submintUpdate(){	
	if (!$('#updateForm').jqxValidator('validate')) {
        return;
    }
	var phones=$("#phoneUpdate").val();
	if(phones.length<1){
		phones=" ";
	}
	var emails= $("#emailUpdateInput").val();
	if(emails.length<1){
		emails=" ";
	}
	var type=currentUserInfo.type;
	var roleId='';
	if(type==2){
		roleId=$("input[name='serverTypeUpdate']:checked").val();
	}
	$.ajax({
         url: '/userManager/saveUser',
         data: {
        	 userId:$("#userId").val(),
        	 crewId:$("#adminCrewId").val(),
        	 password:$("#passwordUpdateInput").val(),
        	 realName:$("#realNameUpdateInput").val(),
        	 sex:$("#sexUpdate").val(),
        	 type:type,
        	 roleId:roleId,
        	 email:emails,
        	 phone:phones,
        	 status:$("#statusUserUpdate").val(),
             ubCreateCrewNum:$("#ubCreateCrewNumUpdateInput").val()
         },
         type: 'post',
         async: false,
         dataType: 'json',
         success: function(data) {
        	 if (data.success) {
         		$("#rightMain").hide();
        		loadUserListGrid();
        		showSuccessMessage(data.message);
			}else{
				showErrorMessage(data.message);
			}
        }
    });
}
//发送通知短信
function sendMsg() {
    var realName = $("#realNameUpdateInput").val();
    var phone = $("#phoneUpdate").val();
    
    $.ajax({
        url: "/userManager/sendNoticeMsg",
        data: {name: realName, phone: phone, password: phone},
        success: function(response) {
            if (response.success) {
                showSuccessMessage("发送成功");
            }
        }
    });    
}
//设为客户服务
/*function customservice(){
	$.ajax({
		url:'/userManager/setCustomerService',
		data:{userId:$('#add').attr('userId')},
		type:'post',
		dataType:'json',
		success:function(data){
			if(data.success){
				showSuccessMessage('设置成功！');
				$("#customservice").hide();
				$("#add").hide();
				$("#con_right_bottom").show();
				$("#con_right_bottom1").hide();
				cur = "<table><tr><td>当前用户角色为客户服务</td></tr></table>";
				$("#con_right_bottom").html(cur);
			}else{
				showErrorMessage(data.message);
			}
		},
		error:function(){}
	});
}*/
//为用户添加剧组
function addCrew(obj){
	var userId = $(obj).attr('userId');
	crewFilter = {};
	crewFilter.currentUserId = userId;
	
	addCrewToUserGrid();
	$("#roleSelect").multiSelect('deselect_all');
	$("#addCrewToUser").jqxWindow('open');
}
// 添加剧组表格
function addCrewToUserGrid(){
	goback();
	
	$.ajax({
		url: "/crewManager/searchAllCrew",
		type: "post",
		dataType: "json",
		data: crewFilter,
		success: function(response) {
			if (response.success) {
				if(response.result && response.result.length > 0) {
					var tvHtml=[];
					var movieHtml=[];
					var netTvHtml=[];
					var netMovieHtml=[];
					for(var i=0;i<response.result.length;i++) {
						var obj=response.result[i];
						var crewName = obj.crewName;
						if(obj.isValid==0) {//过期剧组
							crewName += '<font color="red">(已过期)</font>';
						}
						switch(obj.crewType){
							case 0:
								movieHtml.push('<dd title="'+obj.crewName+'" class="grayColor"><label><input type="checkbox" name="movieCrewChk" id="'+obj.crewId+'">'+crewName+'</label></dd>');
								break;
							case 1:
								tvHtml.push('<dd title="'+obj.crewName+'" class="grayColor"><label><input type="checkbox" name="tvCrewChk" id="'+obj.crewId+'">'+crewName+'</label></dd>');
								break;
							case 2:
								netTvHtml.push('<dd title="'+obj.crewName+'" class="grayColor"><label><input type="checkbox" name="netTvCrewChk" id="'+obj.crewId+'">'+crewName+'</label></dd>');
								break;
							case 3:
								netMovieHtml.push('<dd title="'+obj.crewName+'" class="grayColor"><label><input type="checkbox" name="netMovieCrewChk" id="'+obj.crewId+'">'+crewName+'</label></dd>');
								break;
						}
					}
					$("#addCrewToUserBody").empty();
					if(tvHtml.length != 0) {
						$("#addCrewToUserBody").append('<dl class="group-dl"><dt><span class="span-title">电视剧：</span>&nbsp;&nbsp;<label><input type="checkbox" id="tv" onclick="checkAll(this)">全选</label></dt>'+tvHtml.join('')+'</dl>');
					}
					if(movieHtml.length != 0) {
						$("#addCrewToUserBody").append('<dl class="group-dl"><dt><span class="span-title">电影：</span>&nbsp;&nbsp;<label><input type="checkbox" id="movie" onclick="checkAll(this)">全选</label></dt>'+movieHtml.join('')+'</dl>');
					}
					if(netTvHtml.length != 0) {
						$("#addCrewToUserBody").append('<dl class="group-dl"><dt><span class="span-title">网剧：</span>&nbsp;&nbsp;<label><input type="checkbox" id="netTv" onclick="checkAll(this)">全选</label></dt>'+netTvHtml.join('')+'</dl>');
					}
					if(netMovieHtml.length != 0) {
						$("#addCrewToUserBody").append('<dl class="group-dl"><dt><span class="span-title">网大：</span>&nbsp;&nbsp;<label><input type="checkbox" id="netMovie" onclick="checkAll(this)">全选</label></dt>'+netMovieHtml.join('')+'</dl>');
					}
				} else {
					$("#addCrewToUserBody").html('<center>暂无数据。</center>');
				}
				$("#addCrewToUserBody").find("input[type='checkbox']").click(function(){
					if($(this).is(':checked')){
						$(this).parent().parent().removeClass().addClass('blackColor');
					}else{
						$(this).parent().parent().removeClass().addClass('grayColor');
					}
				});
			} else {
				showErrorMessage(response.message);
			}
		}
	});
	
	/*var addcrewsource =
    {
        datatype: "json",
        data:crewFilter,
        datafields: [
			{ name: 'crewId',type: 'string' },
	        { name: 'crewName',type: 'string' },
	        { name: 'director',type: 'string' },
	        { name: 'status',type: 'int' },
	        { name: 'crewType',type: 'string' }
        ],
        type:'post',
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	addcrewsource.totalrecords=data.result.total;
        	//职务
        	var roleStr="<option value=''>--请选择--</option>";
        },
        root:'resultList',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        url:"/crewManager/searchAllCrew"
    };
	var userdataAdapter = new $.jqx.dataAdapter(addcrewsource);
	var rendergridrows = function (params) {
	    // 调用json返回的列表数据
	    return params.data;
	};  
    var crewTypeRenderer =function (row, columnfield, value, defaulthtml, columnproperties, rowdata) {
    	// 操作列html
    	var html = '';
    	if(rowdata.crewType!=null)
    	  html="<div style='margin-top: 4px;text-align:center;'>"+crewTypeMap.get(rowdata.crewType)+"</div>";
    	return html;
    };
    $("#addCrewToUserGrid").remove();
    $("#addCrewToUserBody").append("<div id='addCrewToUserGrid'></div>");
	$("#addCrewToUserGrid").jqxGrid(
    {
        width: '99%',
        height: '100%',
        source: userdataAdapter,
        selectionmode: 'checkbox',
        pageable: true,
        virtualmode :true,
        columnsresize: true,
        altrows: true,
        pagesize: 5,
        pagerbuttonscount: 5,
        pagesizeoptions: ['5', '50', '全部'],
        rendergridrows:rendergridrows,
        localization:localizationobj,// 表格文字设置
        rendertoolbar: function (toolbar) {            
        },
        cellhover:function(e){
        	   var _this = $(e);
        	   _this.siblings().addClass("jqx-fill-state-hover").parent()
        	   .siblings().children().removeClass("jqx-fill-state-hover");
           },
       ready:function(){
    	   $("div[id^='row']").mouseout(function(){
    		   $(this).children().removeClass("jqx-fill-state-hover");
    	    });    	   
       },
       columns: [
			{ text: '剧组名', datafield: 'crewName', width: '50%'},
			{ text: '剧组类型', cellsrenderer:crewTypeRenderer,cellsAlign: 'center',align:'center', width: '46%' }
       ]
    });*/
}

//全选操作
function checkAll(obj) {
	if($(obj).is(':checked')){
		$("input[name='"+$(obj).attr('id')+"CrewChk']").prop('checked',true).parent().parent().removeClass().addClass('blackColor');
	}else{
		$("input[name='"+$(obj).attr('id')+"CrewChk']").prop('checked',false).parent().parent().removeClass().addClass('grayColor');
	}
}

// 打开用户剧组信息
function crewUserInfo(obj){
	var _this = $(obj);
	var userId = _this.attr('userId');
	var crewId = _this.attr('crewId');
	window.location.href = ctx + "/crewManager/toCrewManagePage?userId="+userId+"&crewId=" + crewId+"&flag=user";
}


// 加入剧组按钮事件，显示角色选择
function addCrewButtonClick(){
	//剧组ID
	var arr=[];
	$("dd").find("input[type='checkbox']:checked").each(function(){
		arr.push($(this).attr('id'));
	});
	if(arr.length>0){
		if(currentUserInfo.type==2) {//客服
			addToCrew(currentUserInfo.roleId);
		} else {
			$("#roleSelect").multiSelect('deselect_all');
			$("#crewSelDiv").hide();
			$("#descrip").show();
			$("#errormessage").hide();
			$("#roleSelDiv").show();
		}
	}else{
		showInfoMessage("请选择剧组！");
	}
}
// 返回
function goback(){
	$("#roleSelDiv").hide();
	$("#crewSelDiv").show();
}
// 加入到剧组
function addToCrew(roleIdsStr){
	//var rowindexes = $('#addCrewToUserGrid').jqxGrid('getselectedrowindexes');
	if(!roleIdsStr){
		var roleIds=$("#roleSelect").val();
		if(!roleIds) {
			$("#descrip").hide();
			$("#errormessage").show();
			return;
		} else {
			$("#descrip").show();
			$("#errormessage").hide();
		}
		roleIdsStr='';
		for(var i=0;i<roleIds.length;i++){
			roleIdsStr+=roleIds[i]+",";
		}
		roleIdsStr=roleIdsStr.substring(0,roleIdsStr.length-1);
	}
	//剧组ID
	var arr=[];
	$("dd").find("input[type='checkbox']:checked").each(function(){
		arr.push($(this).attr('id'));
	});
	
	if(arr.length>0){
    	var param = {userId:$('#add').attr('userId'),crewIds:arr.join(','),roleIds:roleIdsStr};
    	$.ajax({
    		data:param,
    		url:'/crewManager/addUserToCrew',
    		type:'post',
    		dataType:'json',
    		success:function(data){
    			
    			showUserCrew($('#add').attr('userId'));
    			$("#con_right_bottom").show();
				$("#con_right_bottom1").hide();
    			
				addCrewToUserGrid();
    			showSuccessMessage("添加成功！");
    		},
    		error:function(){
    			showErrorMessage("添加失败！");
    		}
    	});
	}else{
		showInfoMessage("请选择剧组！");
	}
}

//显示高级搜索框
function showSearchWindow(){
	$("#superQueryWindow").jqxWindow('open');
}
//清空查询条件
function clearSearchCon(){
	$(".search-condition").find('input[type="text"]').val('');
	$(".search-condition").find('input[type="checkbox"]').prop('checked',false);
	$(".search-condition").find('input[type="radio"]').prop('checked',false);
}
//全选事件注册
function allChkFunction(obj){
	$(obj).click(function(){
    	if($(this).val()==''){//全选
    		$(obj).prop('checked',$(this).prop('checked'));
    	} else {
    		var allchecked = true;
    		$(obj).each(function(i,item){
    			if($(item).val()!='' && !$(item).prop('checked')) {
    				allchecked=false;
    			}
    		});
    		$(obj).eq(0).prop('checked',allchecked);
    	}
    });
}
//高级搜索
function queryUser(){
	var userName=$("#userName_search").val();
	if(userName == ''){
		filter.phone='';
		filter.realName='';
	}else if(userName.length<2 ){
		filter.realName=userName;
		filter.phone='';
	}else{
		filter.phone=userName;
		filter.realName=userName;
	}
	var status = $("input[name='status_search']:checked").val();
	/*$("input[name='status_search']:checked").each(function(){
		if($(this).val()!=''){
			status.push($(this).val());
		}
	});*/
	if(status){
		filter.status=status;
	}else{
		filter.status=null;
	}
	var userType = [];
	$("input[name='userType_search']:checked").each(function(){
		if($(this).val()!=''){
			userType.push($(this).val());
		}
	});
	if(userType.length>0){
		filter.userType=userType.join(",");
	}else{
		filter.userType='';
	}
	
	loadUserListGrid();
	$('#superQueryWindow').jqxWindow('close');
}
//tab切换
function tabChange(own,id) {
	if(!$(own).hasClass('current')) {
		$(own).siblings().removeClass('current');
		$(own).addClass('current');
		if(id==1) {
			$("#add").show();
			$("#user_work_history").hide();
			$("#user_crew").show();
		}else{
			$("#add").hide();
			$("#user_work_history").show();
			$("#user_crew").hide();
		}
	}
}