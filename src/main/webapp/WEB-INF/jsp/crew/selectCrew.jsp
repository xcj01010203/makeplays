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
	
	
    <link rel="stylesheet" href="<%=basePath%>/js/jquery.multiselect/css/multi-select.css" type="text/css" />
    <link rel="stylesheet" href="<%=basePath%>/css/joinCrew.css" type="text/css" />
    
    <script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript"  src="<%=basePath%>/js/jquery.multiselect/js/jquery.multi-select.js"></script>

    <script>
        $(document).ready(function () {
            
        });
    
        //返回链接
        function toJoinCrewPage () {
            window.location.href = "/crewManager/toJoinCrewPage";
        }
        
        //搜索文本框选中事件
        function focusSearch () {
            $(".crew-info").removeClass("selected");
            $("#crewNameInput").removeClass("has-error");
        }
        //搜索文本框中敲击键盘事件
        function keyupSearch(own) {
            var $this = $(own);
        
            var key = event.keyCode;
            if (key == 13) {
                sarchCrew();
            }
            
            if ($this.val() == "" && $("#crewList").html() == "") {
                $(".empty-div").show();
                $(".empty-div label.descrip").html("输入剧组名称关键字<br>点击搜索按钮，查询想要加入的剧组");
            }
        }
        
        //搜索剧组按钮点击事件
        function sarchCrew() {
            $(".crew-info").removeClass("selected");
            
            var crewName = $("#crewNameInput").val();
            if (crewName == "") {
                $("#crewNameInput").addClass("has-error");
                return false;
            }
            
            $.ajax({
                url: "/crewManager/queryByKeyword",
                type: "post",
                data: {keyword: crewName},
                async: true,
                success: function(response) {
                    if (response.success) {
                        $("#crewList").html("");
                    
                        var crewList = response.crewList;
                        $.each(crewList, function(index, item) {
                            var crewId = item.crewId;
                            var crewName = item.crewName;
                            var company = item.company;
                            if (company == null) {
                                company = "未知"
                            }
                            var shootStartDate = item.shootStartDate;
                            var shootEndDate = item.shootEndDate;
                            
                            var crewManagerInfo = item.crewManagerInfo;
                            
                            var crewInfoHtmlArray = [];
                            crewInfoHtmlArray.push("<div class='crew-info'>");
                            crewInfoHtmlArray.push("    <div class='crew-base-info'>");
                            crewInfoHtmlArray.push("        <ul>");
                            crewInfoHtmlArray.push("            <li class='crew-name'>《" + crewName + "》</li>");
                            crewInfoHtmlArray.push("            <li>制片公司：" + company + "</li>");
                            crewInfoHtmlArray.push("            <li>管理员：" + crewManagerInfo + "</li>");
                            if (shootStartDate == "" || shootEndDate == "") {
                                crewInfoHtmlArray.push("            <li>拍摄周期：未知</li>");
                            } else {
                                crewInfoHtmlArray.push("            <li>拍摄周期：" + shootStartDate + " 至 " + shootEndDate + "</li>");
                            }
                            crewInfoHtmlArray.push("        </ul>");
                            crewInfoHtmlArray.push("    </div>");
                            crewInfoHtmlArray.push("    <div class='crew-form-info'>");
                            crewInfoHtmlArray.push("        <ul>");
                            crewInfoHtmlArray.push("            <li>");
                            crewInfoHtmlArray.push("                <input class='crewId' type='hidden' value='" + crewId + "'>");
                            crewInfoHtmlArray.push("                <input class='enterPassword' type='text' placeHolder='入组密码'>");
                            crewInfoHtmlArray.push("                <label class='necessory'>*</label>");
                            crewInfoHtmlArray.push("                <label class='error-message'></label>");
                            crewInfoHtmlArray.push("                <label class='descrip'>入组密码需要从剧组管理员处索取</label>");
                            crewInfoHtmlArray.push("            </li>");
                            crewInfoHtmlArray.push("            <li>");
                            crewInfoHtmlArray.push("                <p class='descrip'><label class='necessory'>*</label>选择想要担任的职务（未选择 &lt;&gt;已选择）</p>");
                            crewInfoHtmlArray.push("                <label class='error-message'></label>");
                            crewInfoHtmlArray.push("                <select class='roleIds' multiple>");
                            crewInfoHtmlArray.push("                </select>");
                            crewInfoHtmlArray.push("            </li>");
                            crewInfoHtmlArray.push("            <li><input class='remark' type='text' placeHolder='备注'></li>");
                            crewInfoHtmlArray.push("            <li class='enter-crew-btn'><input type='button' onclick='applyEnterCrew(this)' value='申请加入'></li>");
                            crewInfoHtmlArray.push("            <li class='success-msg-li'>");
                            crewInfoHtmlArray.push("                <label class='success-message'></label>");
                            crewInfoHtmlArray.push("            </li>");
                            crewInfoHtmlArray.push("        </ul>");
                            crewInfoHtmlArray.push("    </div>");
                            crewInfoHtmlArray.push("</div>");
                            
                            $("#crewList").append(crewInfoHtmlArray.join(""));
                        });
                        
			            $(".crew-info").on("click", function() {
			                $(".crew-info").removeClass("selected");
			                if (!$(this).hasClass("selected")) {
			                    $(this).addClass("selected");
			                    
			                    $(this).find(".enterPassword").focus();
			                }
			            });
			            //阻止crew-form-info的点击事件上传，不然会自动触发crew-info的点击事件
			            $(".crew-form-info").on("click", function() {
			                event.stopPropagation();
			            });
			            
			            $(".enterPassword").on("blur", function() {
			                var value = $(this).val();
			                if (value == "") {
			                    $(this).siblings(".descrip").hide();
			                    $(this).siblings(".error-message").text("请填写入组密码");
			                    return false;
			                }
			            }).on("focus", function() {
			                $(this).siblings(".error-message").text("");
			                $(this).siblings(".descrip").show();
			            });
                        
                        //获取系统部门职务信息
           	            //data:{needManager:true},
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
                                    
                                    $(".roleIds").append(roleOptions.join(""));
                                    
                                    //初始化职务下拉框
                                    $(".roleIds").multiSelect();
                                } else {
                                    alert(response.message);
                                }
                            }
                        });
                        
                        if (crewList.length > 0) {
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
        function applyEnterCrew(own) {
            var $this = $(own);
            
            var $crewFormInfo = $this.parents(".crew-form-info");
            var crewId = $crewFormInfo.find(".crewId").val();
            var enterPassword = $crewFormInfo.find(".enterPassword").val();
            var roleIds = $crewFormInfo.find(".roleIds").val();
            var remark = $crewFormInfo.find(".remark").val();
            
            if (roleIds == null || roleIds == "") {
                 $crewFormInfo.find(".roleIds").siblings(".descrip").hide();
                 $crewFormInfo.find(".roleIds").siblings(".error-message").text("* 请选择期望担任的职务（未选择<>已选择）");
                 
                 return false;
            }
            
            if (enterPassword == "") {
                return false;
            }
            
            $this.attr("disabled", "disabled");
            $.ajax({
                url: "/crewManager/applyToJoinCrew",
                type: "post",
                data: {crewId: crewId, enterPassword: enterPassword, roleIds: roleIds + "", remark: remark},
                success: function(response) {
                    if (response.success) {
                        $this.hide();
                        $this.parent(".enter-crew-btn").siblings(".success-msg-li").find(".success-message").text("申请成功，请等待管理员审核");
                        
                        setTimeout(function(event) {
                            $this.parents(".crew-info").hide();
                        }, 2000);
                    } else {
                        $this.parents(".crew-form-info").find(".enterPassword").siblings(".descrip").hide();
                        $this.parents(".crew-form-info").find(".enterPassword").siblings(".error-message").text(response.message);
                        $this.removeAttr("disabled");
                    }
                }
            });
        }
    </script>
  </head>
  
  <body>
	  <div style="width: 100%; height: 100%; overflow: auto;">
	    <div class="select-crew">
	        <div class="search-div">
	            <a href="javascript:(0)" onclick="toJoinCrewPage()">&lt;&lt;返回</a>
	            <input id="crewNameInput" type="text" placeHolder="输入剧组名称" onfocus="focusSearch()" onkeyup="keyupSearch(this)" autofocus>
	            <input type="button" onclick="sarchCrew()" value="搜  索">
	        </div>
	        <div class="empty-div">
	            <label class="descrip">输入剧组名称关键字<br>点击搜索按钮，查询想要加入的剧组</label>
	        </div>
	        <div class="crew-list" id="crewList">
	            <!-- <div class="crew-info">
	                <div class="crew-base-info">
	                    <ul>
		                    <li class="crew-name">《成龙历险记》</li>
		                    <li>制片公司：神情艺术公司</li>
		                    <li>管理员：张大宝</li>
		                    <li>拍摄周期：2015-05-01 至 2015-09-01</li>
	                    </ul>
	                </div>
	                <div class="crew-form-info">
	                    <ul>
	                        <li>
	                            <input class="enterPassword" type="text" placeHolder="入组密码">
	                            <label class="necessory">*</label>
	                            <label class="error-message"></label>
	                            <label class="descrip">入组密码需要从剧组管理员处索取</label>
	                        </li>
	                        <li>
	                            <p class="descrip"><label class="necessory">*</label>选择想要担任的职务（未选择 &lt;&gt;已选择）</p>
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
	                        <li><input class="remark" type="text" placeHolder="备注"></li>
	                        <li class="enter-crew-btn"><input type="button" onclick="applyEnterCrew(this)" value="申请加入"></li>
	                        <li class="success-msg-li">
				                <label class="success-message" id="successMessage"></label>
				            </li>
	                    </ul>
	                </div>
	            </div> -->
	        </div>
	    </div>
	  </div>
  </body>
</html>
