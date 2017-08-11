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
    
    <link rel="stylesheet" href="<%=basePath%>/css/roleAuthDetail.css" type="text/css" />

    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script>
        var aimRoleId = "${aimRoleId }";
        $(document).ready(function() {
            loadRoleBaseInfo();
            loadRoleAuthInfo();
        
            //loadCrewUserBaseInfo(); //用户基本信息
            //loadCrewUserRoleInfo(); //用户职务信息
            //loadCrewUserAuthInfo(); //用户权限信息
            
        });
        
        //加载角色基本信息
        function loadRoleBaseInfo() {
            $.ajax({
                url: "/sysrole/queryRoleById",
                type: "post",
                dataType: "json",
                data: {roleId: aimRoleId},
                async: true,
                success: function(response) {
                    if (response.success) {
                        var roleInfo = response.roleInfo;
                        
                        var roleId = roleInfo.roleId;
                        var roleName = roleInfo.roleName;
                        var roleDesc = roleInfo.roleDesc;
                        //debugger;
                        var canBeEvaluate = roleInfo.canBeEvaluate;
                        
                        $("#roleId").val(roleId);
                        $("#roleName").val(roleName);
                        $("#roleDesc").val(roleDesc);
                        if (canBeEvaluate) {
                            $("#canBeEvaluate").val(1);
                        } else {
                            $("#canBeEvaluate").val(0);
                        }
                        
                        
                    } else {
                        alert(response.message);
                    }
                }
            });
        }
        
        //加载角色权限信息
        function loadRoleAuthInfo() {
            $.ajax({
                url: "/sysrole/queryRoleAuthList",
                type: "post",
                dataType: "json",
                data: {roleId: aimRoleId},
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
                                      secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ sauthName +"</label>");
                                      
                                      if (sreadonly) {  //是否只读
                                        secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                      } else {
                                        secondlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                      }
                                  } else {
                                      secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ sauthName +"</label>");
                                      secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                  }
                               } else {
                                   if (shasAuth) {  //是否拥有此权限
                                       secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ sauthName +"</label>");
                                   } else {
                                       secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ sauthName +"</label>");
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
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ tauthName +"</label>");
                                           
                                           if (treadonly) {
                                               thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                           } else {
                                               thirdlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                           }
                                           
                                       } else {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ tauthName +"</label>");
                                           thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                       }
                                   } else {
                                       if (thasAuth) {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ tauthName +"</label>");
                                       } else {
                                           thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ tauthName +"</label>");
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
                                  firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ fauthName +"</label>");
                                  
                                  if (freadonly) {  //是否只读
                                    firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                  } else {
                                    firstlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                                  }
                              } else {
                                  firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ fauthName +"</label>");
                                  firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeRoleAuth(this, true)'>可编辑</label>");
                              }
                           } else {
                               if (fhasAuth) {  //是否拥有此权限
                                   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeRoleAuth(this, false)'>"+ fauthName +"</label>");
                               } else {
                                   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeRoleAuth(this, false)'>"+ fauthName +"</label>");
                               }
                           }
                           
                           firstlvlAuthHtml.push("</div>");
                           
                           appAuthListHtml.push(firstlvlAuthHtml.join(""));
                        });
                        
                        $("#appAuthList").append(appAuthListHtml.join(""));
                        
                    } else {
                        alert(response.message);
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
        
        //own:当前元素    isModify：是否是修改操作
        function changeRoleAuth(own, isModify) {
            var $this = $(own);
            
            var operateType = 1;    //operateType 操作类型 1：新增  2：修改  3：删除
            var authId = '';
            var readonly = false;
            
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
                url: "/sysrole/saveRoleAuthInfo",
                type: "post",
                async: true,
                dataType: "json",
                data: {aimRoleId: aimRoleId, operateType: operateType, authId: authId, readonly: readonly},
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
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
        
        function closeWindow() {
            parent.closeRoleDetailWindow();
        }
        
        function saveRoleInfo() {
            var roleId = $("#roleId").val();
            var roleName = $("#roleName").val();
            var roleDesc = $("#roleDesc").val();
            
            if (roleName == "") {
                alert("请输入角色名称");
                return false;
            }
        
            $.ajax({
                url: "/sysrole/saveRoleInfo",
                type: "post",
                async: true,
                dataType: "json",
                data: {roleId: roleId, roleName: roleName, roleDesc: roleDesc},
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return false;
                    }
                    
                    parent.closeRoleDetailWindowAndRefresh();
                }
            });
        }
    </script>
  </head>
  
  <body>
    <div class="role-auth-detail">
        <div class="main-div">
            <div class="role-info">
                <div>
                    <label>角色名称：</label>
                    <input id="roleId" type="hidden"/>
                    <input id="roleName" type="text"/>
                </div>
                <div>
                    <label>角色描述：</label>
                    <textarea id="roleDesc"></textarea>
                </div>
                <!-- <div>
                    <label>是否可被评价：</label>
                    <select id="canBeEvaluate">
                        <option value=0>不可以</option>
                        <option value=1>可以</option>
                    </select>
                </div> -->
            </div>
            <div class="role-auth-info">
                <div class="title-tab">
                   <p class="selected" onclick="siwtchPlatform(1, this)">PC端权限</p>
                   <p onclick="siwtchPlatform(2, this)">APP端权限</p>
                </div>
                <div class="auth-info">
                    <!-- PC端权限 -->
                    <div id="pcAuthList" class="pc-auth-list">
                        <div class="first-level-auth">
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
                        </div>
                    </div>
                    
                    <!-- APP端权限 -->
                    <div id="appAuthList" class="app-auth-list">
                        
                    </div>
                </div>
            </div>
        </div>
        
        <div class="btn-div">
           <input type="button" onclick="saveRoleInfo()" value="保存">
           <input type="button" onclick="closeWindow()" value="关闭">
        </div>
    </div>
    
  </body>
</html>