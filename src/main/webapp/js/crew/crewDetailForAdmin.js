var crewNameValid = false;
var enterPasswordValid = false;

$(document).ready(function() {

    //获取系统题材数据
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
                //$("#subject").val('当代主旋律');
            } else {
                $("#errorMessage").text(response.message);
            }
        }
    });
    
    //剧组名称blur事件，判断非空
    $("#crewName").on("blur", function() {
        if(!validateCrewName($(this).val())){
        	return false;
        }
        crewNameValid = true;
    });
    $("#crewName").on("focus", function() {
        $("#crewNameErrorMsg").text("");
        $("#errorMessage").text("");
    });
    
    //入组密码blur事件，判断格式
    $("#enterPassword").on("blur", function() {
        var value = $(this).val();
        if(!validateEnterPassword(value)){
        	return false;
        }
        enterPasswordValid=true;
    });
    $("#enterPassword").on("focus", function() {
        $(this).siblings(".descrip").show();
        $("#enterPassMsg").text("");
        $("#errorMessage").text("");
    });
    if(crewId) {
        loadCrewInfo();
    }
});
function validateCrewName(value){
	if (value == "") {
        $("#crewNameErrorMsg").text("请填写剧组名称");
        crewNameValid = false;
        return false;
    }
	return true;
}
function validateEnterPassword(value){
    $("#enterPassMsg").siblings(".descrip").hide();
	if (value == "") {
        $("#enterPassMsg").text("请输入入组密码");
        enterPasswordValid = false;
        return false;
    }
    
    if (!/^[0-9]{6}$/.test(value)) {
        $("#enterPassMsg").text("仅支持6位数字");
        enterPasswordValid = false;
        return false;
    }
    return true;
}
function closeWindow() {
    parent.closeCrewDetailWindow();
}
function modifyCrew() {
    if (!validateCrewName($("#crewName").val()) || !validateEnterPassword($("#enterPassword").val())) {
        return false;
    }
    
    var crewName = $("#crewName").val();
    var crewType = $("#crewType").val();
    var projectType = $("#projectType").val();
    var allowExport = $("#exportchk").is(":checked");
    var subject = $("#subject").val();
    var recordNumber = $("#recordNumber").val();
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    var shootStartDate = $("#shootStartDate").val();
    var shootEndDate = $("#shootEndDate").val();
    var company = $("#company").val();
    var director = $("#director").val();
    var scriptWriter = $("#scriptWriter").val();
    var mainactor = $("#mainactor").val();
    var enterPassword = $("#enterPassword").val();
    
    var param = {};
    param.crewId = crewId;
    param.crewName = crewName;
    param.crewType = crewType;
    param.projectType = projectType;
    if(projectType == 1) {
    	param.allowExport = allowExport;
    } else {
    	param.allowExport = true;
    }
    param.subject = subject;
    param.recordNumber = recordNumber;
    param.startDate = startDate;
    param.endDate = endDate;
    param.shootStartDate = shootStartDate;
    param.shootEndDate = shootEndDate;
    param.company = company;
    param.director = director;
    param.scriptWriter = scriptWriter;
    param.mainactor = mainactor;
    param.enterPassword = enterPassword;
    
    $.ajax({
        url: "/crewManager/saveCrewByAdmin",
        type: "post",
        data: param,
        dataType: "json",
        success: function(response) {
            if (response.success) {
                $("#modifyBtn").hide();
                closeWindow();
            } else {
                $("#errorMessage").text(response.message);
            }
        }
    });
}

//加载剧组信息
function loadCrewInfo () {
    $.ajax({
        url: "/crewManager/queryCurrentCrewInfo",
        type: "post",
        async: true,
        dataType: "json",
        data: {},
        success: function(response) {
            if (!response.success) {
                alert(response.message);
                return false;
            }
            
            var crewInfo = response.crewInfo;
            
            var crewName = crewInfo.crewName;
            var crewType = crewInfo.crewType;
            var projectType = crewInfo.projectType;
            //var allowExport = crewInfo.allowExport;
            var company = crewInfo.company;
            var startDate = crewInfo.startDate;
            var endDate = crewInfo.endDate;
            var shootStartDate = crewInfo.shootStartDate;
            var shootEndDate = crewInfo.shootEndDate;
            var subject = crewInfo.subject;
            var recordNumber = crewInfo.recordNumber;
            var director = crewInfo.director;
            var scriptWriter = crewInfo.scriptWriter;
            var mainactor = crewInfo.mainactor;
            var enterPassword = crewInfo.enterPassword;
            
            $("#crewName").val(crewName);
            $("#company").val(company);
            $("#startDate").val(startDate);
            $("#endDate").val(endDate);
            $("#shootStartDate").val(shootStartDate);
            $("#shootEndDate").val(shootEndDate);
            $("#crewType").val(crewType);
            $("#projectType").val(projectType).trigger('change');
            //$("#exportchk").prop('checked', allowExport);
            
            $("#subject").val(subject);
            $("#recordNumber").val(recordNumber);
            $("#director").val(director);
            $("#scriptWriter").val(scriptWriter);
            $("#mainactor").val(mainactor);
            $("#enterPassword").val(enterPassword);
            
            crewNameValid=true;
            enterPasswordValid=true;
            
            //默认是试用项目，则可以修改项目类型，默认是普通项目，不能修改项目类型
            if(projectType != 1) {
            	$("#projectType").attr('disabled',true);
            }
            $("#isallowexportdiv").hide();
        }
    });
}
//项目类型切换，显示/隐藏可导出选项
function showAllowExport(obj){
	if($(obj).val()==1) {
		$("#isallowexportdiv").show();
	}else{
		$("#isallowexportdiv").hide();
	}
}