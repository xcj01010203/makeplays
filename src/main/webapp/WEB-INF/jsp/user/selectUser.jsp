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
    <meta http-equiv="description" content="This is select a crew page">
    
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript"  src="<%=basePath%>/js/jquery.multiselect/js/jquery.multi-select.js"></script>
    
    <link rel="stylesheet" href="<%=basePath%>/js/jquery.multiselect/css/multi-select.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/css/selectUser.css" type="text/css" />

    <script>
        $(document).ready(function () {
            $(".user-info").on("click", function() {
                $(".user-info").removeClass("selected");
                if (!$(this).hasClass("selected")) {
                    $(this).addClass("selected");
                }
            });
        
        
            //获取系统部门职务信息
             $.ajax({
                 url: "/sysrole/queryCrewDepartmentAndDuties",
                 type: "post",
	             data:{needManager:true},
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
                         
                         $(".roleIds").append(roleOptions.join(""));
                         
                         //初始化职务下拉框
                         $(".roleIds").multiSelect();
                     } else {
                         alert(response.message);
                     }
                 }
             });
        });
    
        //搜索文本框选中事件
        function focusSearch () {
            $(".user-info").removeClass("selected");
            $("#phoneInput").removeClass("has-error");
        }
        //搜索文本框中敲击键盘事件
        function keyupSearch(own) {
            var $this = $(own);
        
            var key = event.keyCode;
            if (key == 13) {
                sarchUser();
            }
            
            if ($this.val() == "" && $("#userList").html() == "") {
                $(".empty-div").show();
                $(".empty-div label.descrip").html("输入电话号码<br>点击回车键查询用户");
            }
        }
        
        //搜索剧组按钮点击事件
        function sarchUser() {
            $(".user-info").removeClass("selected");
            
            var phone = $("#phoneInput").val();
            if (phone == "") {
                $("#phoneInput").addClass("has-error");
                return false;
            }
            
            $.ajax({
                url: "/userManager/queryUserByPhone",
                type: "post",
                data: {phone: phone},
                async: true,
                success: function(response) {
                    if (response.success) {
                        $("#userList").html("");
                    
                        var userList = response.userList;
                        $.each(userList, function(index, item) {
                            var userId = item.userId;
                            var userName = item.realName;
                            var phone = item.phone;
                            
                            var userInfoHtmlArray = [];
                            userInfoHtmlArray.push("<div class='user-info'>");
                            userInfoHtmlArray.push("    <div class='user-base-info'>");
                            userInfoHtmlArray.push("        <ul>");
                            userInfoHtmlArray.push("            <li>" + userName + " （"+ phone +"）</li>");
                            userInfoHtmlArray.push("        </ul>");
                            userInfoHtmlArray.push("    </div>");
                            userInfoHtmlArray.push("    <div class='user-form-info'>");
                            userInfoHtmlArray.push("        <ul>");
                            userInfoHtmlArray.push("            <li>");
                            userInfoHtmlArray.push("                <p class='descrip'><label class='necessory'>*</label>选择将要担任的职务（未选择 &lt;&gt;已选择）</p>");
                            userInfoHtmlArray.push("                <label class='error-message'></label>");
                            userInfoHtmlArray.push("                <select class='roleIds' multiple>");
                            userInfoHtmlArray.push("                </select>");
                            userInfoHtmlArray.push("            </li>");
                            userInfoHtmlArray.push("            <li class='enter-user-btn'><input userId='"+ userId +"' type='button' onclick='addUserToCrew(this)' value='加入到剧组'></li>");
                            userInfoHtmlArray.push("            <li class='success-msg-li'>");
                            userInfoHtmlArray.push("                <label class='success-message'>操作成功</label>");
                            userInfoHtmlArray.push("                <a onclick='goonAdd(this)'>继续添加</a>");
                            userInfoHtmlArray.push("                <a userId='"+ userId +"' onclick='gotoSetAuthInfo(this)'>前往设置权限&gt;&gt;</a>");
                            userInfoHtmlArray.push("            </li>");
                            userInfoHtmlArray.push("        </ul>");
                            userInfoHtmlArray.push("    </div>");
                            userInfoHtmlArray.push("</div>");
                            
                            $("#userList").append(userInfoHtmlArray.join(""));
                        });
                        
                        $(".user-info").on("click", function() {
                            $(".user-info").removeClass("selected");
                            if (!$(this).hasClass("selected")) {
                                $(this).addClass("selected");
                            }
                        });
                        
                        //获取系统部门职务信息
			            $.ajax({
			                url: "/sysrole/queryCrewDepartmentAndDuties",
			                type: "post",
			                //data:{needManager:true},
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
			                        
			                        $(".roleIds").append(roleOptions.join(""));
			                        
			                        //初始化职务下拉框
			                        $(".roleIds").multiSelect();
			                    } else {
			                        alert(response.message);
			                    }
			                }
			            });
                        
                        if (userList.length > 0) {
                            $(".empty-div").hide();
                        } else {
                            $(".empty-div").show();
                            $(".empty-div label.descrip").text("查询无结果");
                        }
                    } else {
                        alert(response.message);
                    }
                }
            });
        }
        
        //申请加入剧组点击事件
        function addUserToCrew(own) {
            var $this = $(own);
            var $userFormInfo = $this.parents(".user-form-info");
        
            var userId = $(own).attr("userId");
            var roleIds = $userFormInfo.find(".roleIds").val();
            
            if (roleIds == null || roleIds == "") {
                 $userFormInfo.find(".roleIds").siblings(".descrip").hide();
                 $userFormInfo.find(".roleIds").siblings(".error-message").text("* 请选择期望担任的职务（未选择<>已选择）");
                 
                 return false;
            }
            $this.attr("disabled", "disabled");
            $.ajax({
                url: "/userManager/addUserToCrew",
                type: "post",
                data: {aimUserId: userId, roleIds: roleIds + ""},
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return false;
                    }
                    
                    $this.hide();
                    $this.parents(".user-info").find(".success-msg-li").show();
                }
            });
        }
        
        //前往设置权限和职务
        function gotoSetAuthInfo(own) {
            var userId = $(own).attr("userId");
            
            parent.hideAddUserWin();
            //打开父页面的人员职务和权限窗口
            parent.showDetailWithUserId(userId);
        }
        
        //继续添加  
        function goonAdd(own) {
            $(own).parents(".user-info").hide();
            $("#phoneInput").val("");
            $("#phoneInput").focus();
        }
    </script>
  </head>
  
  <body>
      <div style="width: 100%; height: 100%; overflow: auto;">
        <div class="select-user">
            <div class="search-div">
                <input id="phoneInput" type="text" placeHolder="输入电话号码" onfocus="focusSearch()" onkeyup="keyupSearch(this)" autofocus>
                <input type="button" onclick="sarchUser()" value="搜  索">
            </div>
            <div class="empty-div">
                <label class="descrip">输入电话号码<br>点击回车键查询用户</label>
            </div>
            <div class="user-list" id="userList">
                <!-- <div class="user-info selected">
                    <div class="user-base-info">
                        <ul>
                            <li>葛优（15265425875）</li>
                        </ul>
                    </div>
                    <div class="user-form-info">
                        <ul>
                            <li>
                                <p class="descrip"><label class="necessory">*</label>选择即将担任的职务（未选择 &lt;&gt;已选择）</p>
                                <label class="error-message"></label>
                                <select class="roleIds" multiple>
                                    <optgroup label='制片组'>
                                        <option value='1'>制片人</option>
                                        <option value='2'>制片助理</option>
                                        <option value='3'>执行制片</option>
                                        <option value='4'>艺术总监</option>
                                      </optgroup>
                                      <optgroup label='导演组'>
                                        <option value='5'>导演</option>
                                        <option value='6'>副导演</option>
                                        <option value='7'>执行导演</option>
                                      </optgroup>
                                </select>
                            </li>
                            <li class="enter-crew-btn" style="display: none;"><input type="button" onclick="applyEnterCrew(this)" value="加入到剧组"></li>
                            <li class="success-msg-li" style="display: block;">
                                <label class="success-message" id="successMessage">操作成功</label>
                                <a onclick="gotoSetAuthInfo()">前往设置权限</a>
                                <a onclick="goonAdd()">继续添加</a>
                            </li>
                        </ul>
                    </div>
                </div> -->
            </div>
        </div>
      </div>
  </body>
</html>
