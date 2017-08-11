var teamId = "";

$(document).ready(function() {
	//初始化题材
	$.ajax({
        url: "/crewManager/queryAllSubject",
        type: "post",
        dataType: "json",
        async: false,
        success: function(response) {
            if (response.success) {
                var subjectList = response.subjectList;
                for (var i = 0; i < subjectList.length; i++) {
                    var subject = subjectList[i];
                    $("#subject").append("<option value='" + subject.subjectName + "'>" + subject.subjectName + "</option>");
                }
            }
        }
    });
	
	
	//初始化职务
	//获取系统部门职务信息
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
                
                $("#needPosition").append(roleOptions.join(""));
            }
        }
    });
	
});

//保存组训信息
function saveTeamInfo() {
	var data = $("#teamInfoForm").serialize();
	$.ajax({
		url: "/teamInfoManager/saveTeamInfo",
		dataType: "json",
		data: data,
		type: "post",
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return;
			}
			teamId = response.teamId;
			$("#teamId").val(teamId);
			showSuccessMessage("保存成功");
			$(".team-detail-info").hide();
			$(".position-detail-info").show();
			$(".position-info-remark").show();
			$("#positionInfoRemark").text("为《" + $("#crewName").val() + "》组训添加职位");
		}
	});
}

//保存组训下的职位信息
function savePositionInfo() {
	var data = $("#positionInfoForm").serialize();
	$.ajax({
		url: "/teamInfoManager/savePositionInfo",
		dataType: "json",
		data: data,
		type: "post",
		success: function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return;
			}
			showSuccessMessage("保存成功");
			document.getElementById("positionInfoForm").reset();
			$("#teamId").val(teamId);
		}
	});
}

//新建组训
function createNewTeamInfo() {
	document.getElementById("teamInfoForm").reset();
	document.getElementById("positionInfoForm").reset();
	

	$(".team-detail-info").show();
	$(".position-info-remark").hide();
	$(".position-detail-info").hide();
}

//选择一个职位
function selectPosition(own) {
	var positonName = $(own).find("option:selected").text();
	$("#positonName").val(positonName);
}

//校验是否是数字
function checkNum(own) {
	if (isNaN(own.value)) {
		own.value = "";  
    }  
}