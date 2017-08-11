<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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
    
    <link rel="stylesheet" href="<%=basePath%>/js/jquery.multiselect/css/multi-select.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/css/crewUserDetail.css" type="text/css" />

    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript"  src="<%=basePath%>/js/jquery.multiselect/js/jquery.multi-select.js"></script>
    <script src="<%=basePath%>/js/UI-Checkbox-master/checkbox.min.js"></script>
    <script>
        var aimUserId = "${aimUserId }";
        $(document).ready(function() {
        
            loadCrewUserBaseInfo(); //用户基本信息
            loadCrewUserRoleInfo(); //用户职务信息
         	//判断是否已设置剧组权限
        	$.ajax({
                url:"/crewManager/isCrewHasAuth",
                type:"post",
                dataType:"json",
                success:function(data){
                    if(data.success){
                        loadCrewUserAuthInfo(); //用户权限信息
                    }else{
                        parent.showErrorMessage(data.message);
                    }
                }
            });            
        });
        
        //加载用户基本信息
        function loadCrewUserBaseInfo() {
            $.ajax({
                url: "/userManager/queryCrewUserBaseInfo",
                type: "post",
                dataType: "json",
                data: {userId: aimUserId},
                async: true,
                success: function(response) {
                    if (response.success) {
                        var userInfo = response.userInfo;
                        var userName = userInfo.userName;
                        var phone = userInfo.phone;
                        var status = userInfo.status;
                        
                        $("#userName").text(userName);
                        $("#userPhone").text(phone);
                        
                        if (status == 1) {
                           $("#crewUserStatus").prop("checked", true);
                        } else {
                           $("#crewUserStatus").prop("checked", false);
                        }
                    } else {
                        parent.showErrorMessage(response.message);
                    }
                }
            });
        }
        
        //加载用户职务信息
        function loadCrewUserRoleInfo() {
            $.ajax({
                url: "/userManager/queryCrewUserRoleInfo",
                type: "post",
                dataType: "json",
                data: {userId: aimUserId},
                async: true,
                success: function(response) {
                    if (response.success) {
                        var roles = response.userRoleList;
                        var userSysRoleList = response.userSysRoleList;
                        var majorRoleList = response.majorRoleList;
                        var guestRoleList = response.guestRoleList;
                        
                        //用户基本信息展示
                        var roleNames = "";
                        $.each(roles, function(index, item) {
                           roleNames += item.roleName + " ";
                        });
                        $("#selectedRoles").text(roleNames);
                        
                        //用户职务列表
                        $("#roleList").html("");
                        var roleListHtml = [];
                        $.each(userSysRoleList, function (index, item) {
                           var singleGroupHtml = [];
                        
                           var proleId = item.roleId;
                           var proleName = item.roleName;
                           var child = item.child;
                           
                           singleGroupHtml.push("<div class='single-group'>");
                           singleGroupHtml.push("<div id='"+ proleId +"' class='group-name'>"+ proleName +"</div>");
                           singleGroupHtml.push("<div class='group-roles'>");
                           
                           var groupSelected = false;   //组下是否有职务被选中
                           $.each(child, function(cIndex, cItem) {
                               var croleId = cItem.roleId;
                               var croleName = cItem.roleName;
                               var hasRole = cItem.hasRole;
                               
                               if (hasRole) {
                                   singleGroupHtml.push("<label id='"+ croleId +"' class='group-role selected' onclick='changeUserRole(this)'>"+ croleName +"</label>");
                                   groupSelected = true;
                               } else {
                                   singleGroupHtml.push("<label id='"+ croleId +"' class='group-role' onclick='changeUserRole(this)'>"+ croleName +"</label>");
                               }
                               
                           });
                           
                           singleGroupHtml.push("</div>");
                           
                           //关联场景角色
                            if (proleName == "演员组") {
                                
                                singleGroupHtml.push("<div class='relate-roles'>");
                                singleGroupHtml.push("<p>关联角色（未关联&lt;&gt;已关联）</p>");
                                
                                if (groupSelected) {
                                    singleGroupHtml.push("<select id='relateRoles' multiple>");
                                } else {
                                    singleGroupHtml.push("<select id='relateRoles' disabled multiple>");
                                }
                                
                                singleGroupHtml.push("<optgroup label='主要演员'>");
                                $.each(majorRoleList, function(mrIndex, mrItem) {
                                    var viewRoleId = mrItem.viewRoleId;
                                    var viewRoleName = mrItem.viewRoleName;
                                    var hasRelated = mrItem.hasRelated;
                                    
                                    if (hasRelated) {
                                        singleGroupHtml.push("<option value='"+ viewRoleId +"' selected>"+ viewRoleName +"</option>");
                                    } else {
                                        singleGroupHtml.push("<option value='"+ viewRoleId +"'>"+ viewRoleName +"</option>");
                                    }
                                });
                                singleGroupHtml.push("</optgroup>");
                                singleGroupHtml.push("<optgroup label='特约演员'>");
                                 $.each(guestRoleList, function(grIndex, grItem) {
                                    var viewRoleId = grItem.viewRoleId;
                                    var viewRoleName = grItem.viewRoleName;
                                    var hasRelated = grItem.hasRelated;
                                    
                                    if (hasRelated) {
                                        singleGroupHtml.push("<option value='"+ viewRoleId +"' selected>"+ viewRoleName +"</option>");
                                    } else {
                                        singleGroupHtml.push("<option value='"+ viewRoleId +"'>"+ viewRoleName +"</option>");
                                    }
                                });
                                singleGroupHtml.push("</optgroup>");
                                singleGroupHtml.push("</select>");
                                singleGroupHtml.push("</div>");
                            }
                           
                           singleGroupHtml.push("</div>");
                           
                           
                           roleListHtml.push(singleGroupHtml.join(""));
                           
                        });
                        
                        $("#roleList").append(roleListHtml.join(""));
                        
                        $("#relateRoles").multiSelect({
                            afterSelect: function(values) {
                                changeRelateRole(values, 1);
                            },
                            afterDeselect: function(values) {
                                changeRelateRole(values, 2);
                            }
                        });
                        
                    } else {
                        parent.showErrorMessage(response.message);
                    }
                }
            });
        }
        
        
        //加载用户权限信息
        function loadCrewUserAuthInfo() {
            $.ajax({
                url: "/userManager/queryCrewUserAuthInfo",
                type: "post",
                dataType: "json",
                data: {userId: aimUserId},
                async: true,
                success: function(response) {
                    if (response.success) {
                        var appAuthList = response.appAuthList;
                        var pcAuthList = response.pcAuthList;
                        
                        /*
                         * 用户权限列表
                         */
                         //PC端权限
                        $("#pcAuthList").html("");
                        var pcAuthListHtml = [];    //所有的一级权限集合
                        $.each(pcAuthList, function(findex, fitem) {
                           var firstlvlAuthHtml = [];   //单个第一级权限
                           
                           var fauthId = fitem.authId;
                           var fauthName = fitem.authName;
                           var fhasAuth = fitem.hasAuth;
                           var fdifferInRAndW = fitem.differInRAndW;
                           var freadonly = fitem.readonly;
                           var secondAuthList = fitem.subAuthList;
                           
                           firstlvlAuthHtml.push("<div class='first-level-auth'>");
                           firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth group-auth'>"+ fauthName +"</label>");
                           
                           //遍历二级权限
                           $.each(secondAuthList, function(sindex, sitem) {
                               var secondlvlAuthHtml = [];  //单个第二级权限
                           
                               var sauthId = sitem.authId;
                               var sauthName = sitem.authName;
                               var shasAuth = sitem.hasAuth;
                               var sdifferInRAndW = sitem.differInRAndW;
                               var sreadonly = sitem.readonly;
                               var thirdAuthList = sitem.subAuthList;
                               
                               secondlvlAuthHtml.push("<div class='second-level-auth'>");
                               if (sdifferInRAndW) {//是否区分读写操作
                               
                                  if (shasAuth) {  //是否拥有此权限
                                      secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                                      
                                      if (sreadonly) {  //是否只读
                                        secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                      } else {
                                        secondlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                      }
                                  } else {
                                      secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                                      secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                  }
                               } else {
                                   if (shasAuth) {  //是否拥有此权限
                                       secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false, "+sreadonly+")'>"+ sauthName +"</label>");
                                   } else {
                                       secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeUserAuth(this, false, "+sreadonly+")'>"+ sauthName +"</label>");
                                   }
                               }
                               
                               
                               //遍历三级权限
                               var thirdlvlAuthHtml = [];   //单个三级权限
                               if (shasAuth) {
                                   thirdlvlAuthHtml.push("<div class='third-level-auth'>");
                               } else {
                                   thirdlvlAuthHtml.push("<div class='third-level-auth' style='display:none;'>");
                               }
                               
                               $.each(thirdAuthList, function(tindex, titem) {
                                   
                                   var tauthId = titem.authId;
                                   var tauthName = titem.authName;
                                   var thasAuth = titem.hasAuth;
                                   var tdifferInRAndW = titem.differInRAndW;
                                   var treadonly = titem.readonly;
                                   
                                   
                                   if (tdifferInRAndW) {
                                       if (thasAuth) {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                                           
                                           if (treadonly) {
                                               thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                           } else {
                                               thirdlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                           }
                                           
                                       } else {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                                           thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                       }
                                   } else {
                                       if (thasAuth) {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false, "+sreadonly+")'>"+ tauthName +"</label>");
                                       } else {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                                       }
                                   }
                                   
                               });
                               thirdlvlAuthHtml.push("</div>");
                               
                               secondlvlAuthHtml.push(thirdlvlAuthHtml.join(""));
                               
                               secondlvlAuthHtml.push("</div>");
                               
                               firstlvlAuthHtml.push(secondlvlAuthHtml.join(""));
                           });
                           
                           firstlvlAuthHtml.push("</div>");
                           
                           pcAuthListHtml.push(firstlvlAuthHtml.join(""));
                        });
                        
                        $("#pcAuthList").append(pcAuthListHtml.join(""));
                        
                        
                        
                        
                        
                        //app端权限,app端只有一级权限
                        $("#appAuthList").html("");
                        var appAuthListHtml = [];    //所有的一级权限集合
                        $.each(appAuthList, function(findex, fitem) {
                           var firstlvlAuthHtml = [];   //单个第一级权限
                           
                           var fauthId = fitem.authId;
                           var fauthName = fitem.authName;
                           var fhasAuth = fitem.hasAuth;
                           var fdifferInRAndW = fitem.differInRAndW;
                           var freadonly = fitem.readonly;
                           
                           firstlvlAuthHtml.push("<div class='first-level-auth'>");
                           
                           if (fdifferInRAndW) {//是否区分读写操作
                               
                              if (fhasAuth) {  //是否拥有此权限
                                  firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                                  
                                  if (freadonly) {  //是否只读
                                    firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                  } else {
                                    firstlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                  }
                              } else {
                                  firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                                  firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                              }
                           } else {
                               if (fhasAuth) {  //是否拥有此权限
                                   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false, "+freadonly+")'>"+ fauthName +"</label>");
                               } else {
                                   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeUserAuth(this, false, "+freadonly+")'>"+ fauthName +"</label>");
                               }
                           }
                           
                           firstlvlAuthHtml.push("</div>");
                           
                           appAuthListHtml.push(firstlvlAuthHtml.join(""));
                        });
                        
                        $("#appAuthList").append(appAuthListHtml.join(""));
                        
                    } else {
                        parent.showErrorMessage(response.message);
                    }
                }
            });
        }
        
        
        
        //修改用户在剧组中的状态
        function modifyUserStatus(own) {
            var operateType = 1;
            if (own.checked) {
                operateType = 2;
            }
            
            $.ajax({
                url: "/userManager/modifyUserStatus",
                type: "post",
                async: true,
                data: {aimUserId: aimUserId, operateType: operateType},
                dataType: "json",
                success: function(response) {
                    if (!response.success) {
                        parent.showErrorMessage(response.message);
                    }
                }
            });
        }
        
        //切换权限的pc/app视图
        function siwtchPlatform(type, own) {
            var $this = $(own);
            //type 1：pc端   2：app端
            if (!$this.hasClass("selected")) {
                $this.addClass("selected");
                $this.siblings("p").removeClass("selected");
                
                if (type == 1) {
                    $("#pcAuthList").show();
                    $("#appAuthList").hide();
                } else {
                    $("#appAuthList").show();
                    $("#pcAuthList").hide();
                }
            }
        }
        
        //修改用户职务信息
        function changeUserRole(own) {
            var $this = $(own);
            var roleId = $this.attr("id");
            var roleName = $this.text();
            
            var operateType = 1;    //操作类型：1新增    2删除
            if ($this.hasClass("selected")) {
                operateType = 2;
            }
            
            $.ajax({
                url: "/userManager/saveUserRoleInfo",
                type: "post",
                async: true,
                dataType: "json",
                data: {aimUserId: aimUserId, operateType: operateType, roleId: roleId},
                success: function(response) {
                    if (response.success) {
                        var selectedRoles = $("#selectedRoles").text();
                        
                        if ($this.hasClass("selected")) {
                            $this.removeClass("selected");
                            $("#selectedRoles").text(selectedRoles.replace(roleName + " ", ""));
                            
                            //演员职务特殊处理
	                        if (!$this.siblings(".group-role").hasClass("selected") && (roleId == "73" || roleId == "74")) {
	                            $this.parents(".single-group").find(".relate-roles #relateRoles").attr("disabled", "disabled");
	                            $("#relateRoles").multiSelect("deselect_all");
	                            $("#relateRoles").multiSelect("refresh");
	                        }
                        } else {
                            $this.addClass("selected");
                            $("#selectedRoles").text(selectedRoles + roleName + " ");
                            
                            //演员职务特殊处理
	                        if (roleId == "73" || roleId == "74") {
	                            $this.parents(".single-group").find(".relate-roles #relateRoles").removeAttr("disabled");
                                $("#relateRoles").multiSelect("refresh");
	                        }
                        }
                        
                        loadCrewUserAuthInfo();
                    } else {
                        parent.showErrorMessage(response.message);
                    }
                }
            });
            
        }
        
        //own:当前元素    isModify：是否是修改操作
        function changeUserAuth(own, isModify, isReadonly) {
            var $this = $(own);
            
            var operateType = 1;    //operateType 操作类型 1：新增  2：修改  3：删除
            var authId = '';
            var readonly = false;
            if(isReadonly != undefined) {
            	readonly = isReadonly;
            }
            
            //isModify为true表示是点击后面的“可编辑”文本
            if (isModify) {
                operateType = 2;
                authId = $this.prev(".single-auth").attr("id");
                
                if (!$this.prev(".single-auth").hasClass("selected")) {
                    return false;
                }
                
                if ($this.hasClass("selected")) {
                    readonly = true;
                } else {
                    readonly = false;
                }
                
            } else {
                if ($this.hasClass("selected")) {
                    operateType = 3;
                } else {
                    operateType = 1;
                }
                authId = $this.attr("id");
            }
            
            $.ajax({
                url: "/userManager/saveUserAuthInfo",
                type: "post",
                async: true,
                dataType: "json",
                data: {aimUserId: aimUserId, operateType: operateType, authId: authId, readonly: readonly},
                success: function(response) {
                    if (!response.success) {
                        parent.showErrorMessage(response.message);
                        return false;
                    }
                    
                    if ($this.hasClass("selected")) {
                        $this.removeClass("selected");
                        if (!isModify) {
                            $this.next(".allow-modify-tag").removeClass("selected");
                            
                            //三级权限隐藏
                            $this.siblings(".third-level-auth").hide();
                            $this.siblings(".third-level-auth").find(".single-auth").removeClass("selected");
                        }
                    } else {
                        $this.addClass("selected");
                        if (!isModify) {
                            $this.next(".allow-modify-tag").addClass("selected");
                            
                            //显示三级权限
                            $this.siblings(".third-level-auth").show();
                        }
                    }
                }
            });
        }
        
        //修改用户关联的场景角色
        //operateType操作类型  1:新增    2：删除
        function changeRelateRole(viewRoleId, operateType) {
            $.ajax({
                url: "/userManager/saveActorUserCrewRoleRelation",
                type: "post",
                async: true,
                dataType: "json",
                data: {aimUserId: aimUserId, operateType: operateType, viewRoleId: viewRoleId + ""},
                success: function(response) {
                    if (!response.success) {
                        parent.showErrorMessage(response.message);
                        return false;
                    }
                }
            });
        }
        
        function closeWindow() {
            parent.closeCrewUserDetailWindow();
        }
    </script>
  </head>
  
  <body>
    <div class="crew-user-detail">
	    <div class="main-div">
	        <div class="user-role-info">
	            <div class="user-info">
	                <div>
	                    <img src="/images/userName.png">
	                    <label id="userName"></label>
	                </div>
	                <div>
	                    <img src="/images/userPhone.png">
	                    <label id="userPhone"></label>
	                    <div class="ui toggle checkbox user-status">
							<input id="crewUserStatus" type="checkbox" onclick="modifyUserStatus(this)" checked>
							<label>激活</label>
	                    </div>
	                </div>
	                <div class="selected-roles-div">
		                <img src="/images/userRole.png">
		                <p class="selected-roles" id="selectedRoles"></p>
	                </div>
	            </div>
	            <div class="role-info">
	                <div class="title">职务</div>
	                <div class="content" id="roleList">
	                    <div class="single-group">
	                        <div class="group-name">制片组</div>
	                        <div class="group-roles">
			                    <label id="11" class="group-role selected">制片人</label>
			                    <label id="12" class="group-role selected">制片助理</label>
			                    <label id="13" class="group-role">艺术总监</label>
			                    <label id="14" class="group-role selected">执行制片</label>
		                        <label id="15" class="group-role">编剧</label>
		                        <label id="16" class="group-role">责编</label>
		                        <label id="17" class="group-role selected">制片主任</label>
		                        <label id="18" class="group-role">财务</label>
		                        <label id="19" class="group-role selected">统筹</label>
		                        <label id="10" class="group-role selected">生活制片</label>
		                        <label id="20" class="group-role">外联制片</label>
		                        <label id="21" class="group-role">车队长</label>
	                        </div>
	                        <div class="relate-roles">
								<p>关联角色（未关联&lt;&gt;已关联）</p>
								<select id="relateRoles" class="roleIds" multiple>
								    <optgroup label='主要演员'>
								        <option value='1' selected>制片人</option>
								        <option value='2'>制片助理</option>
								        <option value='3'>执行制片</option>
								        <option value='4'>艺术总监</option>
								      </optgroup>
								      <optgroup label='特约演员'>
								        <option value='5'>导演</option>
								        <option value='6'>副导演</option>
								        <option value='7'>执行导演</option>
								      </optgroup>
								</select>
	                        </div>
	                    </div>
                        
	                </div>
	            </div>
	        </div>
	        <div class="user-auth-info">
	            <div class="title-tab">
	               <p class="selected" onclick="siwtchPlatform(1, this)">PC端权限</p>
                   <p onclick="siwtchPlatform(2, this)">APP端权限</p>
	            </div>
                <div class="auth-info">
                    <!-- PC端权限 -->
                    <div id="pcAuthList" class="pc-auth-list">
                        <!-- <div class="first-level-auth">
                            <label class="single-auth selected">拍摄管理</label>
                            <div class="second-level-auth">
                                <label class="single-auth selected">剧本分析</label><label class="allow-modify-tag">可编辑</label>
                                <div class="third-level-auth">
                                    <label class="single-auth">批量删除场景</label>
                                </div>
                            </div>
                            <div class="second-level-auth">
                                <label class="single-auth selected">场景表</label><label class="allow-modify-tag selected">可编辑</label>
                            </div>
                            <div class="second-level-auth">
                                <label class="single-auth selected">角色表</label>
                            </div>
                        </div> -->
                    </div>
                    
                    <!-- APP端权限 -->
                    <div id="appAuthList" class="app-auth-list">
                        
                    </div>
                </div>
	        </div>
	    </div>
	    
	    <div class="btn-div">
	       <input type="button" onclick="closeWindow()" value="关闭">
	    </div>
    </div>
  </body>
</html>
