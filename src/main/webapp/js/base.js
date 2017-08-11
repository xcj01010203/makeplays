var localizationobj = {};
var pageSize=20;
$(function(){
    //植入分析
    
    
    
    //grid中文定义
    localizationobj.pagertotalpagestring = "总页数：";
    localizationobj.pagergotopagestring = "当前页数：";
    localizationobj.pagershowrowsstring = "每页显示条数：";
    localizationobj.pagerrangestring = " 总条数：";
    localizationobj.pagercurrnumstring = " 当前条数：";
    localizationobj.sortascendingstring = "升序排列";
    localizationobj.sortdescendingstring = "降序排列";
    localizationobj.sortremovestring = "移除排序";
    localizationobj.emptydatastring="暂无数据";
    
    localizationobj.pagernextbuttonstring = "下一场";
    localizationobj.pagerpreviousbuttonstring = "上一场";

    localizationobj.firstDay = 1;
    localizationobj.percentsymbol = "%";
    localizationobj.currencysymbol = "€";
    localizationobj.currencysymbolposition = "before";
    localizationobj.decimalseparator = ".";
    localizationobj.thousandsseparator = ",";

    var days = {
        // full day names
        names: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
        // abbreviated day names
        namesAbbr: ["Sonn", "Mon", "Dien", "Mitt", "Donn", "Fre", "Sams"],
        // shortest day names
        namesShort: ["日", "一", "二", "三", "四", "五", "六"]
    };

    localizationobj.days = days;

    var months = {
        // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
        names: ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember", ""],
        // abbreviated month names
        namesAbbr: ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dez", ""]
    };

    localizationobj.months = months;
    
    
	localizationobj.browseButton='选择文件';
	localizationobj.uploadButton='上传';
	localizationobj.cancelButton='取消';
	localizationobj.uploadFileTooltip='Datei hochladen';
	localizationobj.cancelFileTooltip='aufheben';
});



function sendMessage(message){
	$("#jqxNotification > div").text(message);
	$("#jqxNotification").jqxNotification("open");
}

/**
 * 删除字符串两端空格
 * @param str
 * @returns
 */
function trim(str){ 
    return str.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 下拉框高度获取
 * array 下拉框数据数组
 */
function getHeight(array){
	
	if(!array.length){
		return 25;
	}
	
	if(array.length==0){
		return 25;
	}
	
	if(array.length>10){
		return 250;
	}else{
		return (array.length+1)*25;
	}
}


function showSuccessMessage(message){
	$("#jqxNotification").find(".jqx-notification-content ").text("");
	$("#jqxNotification").find(".jqx-notification-content ").text(message);
	$("#jqxNotification").jqxNotification({template: "success"});
	$("#jqxNotification").jqxNotification({autoCloseDelay: 1000});
	$("#jqxNotification").jqxNotification("open");
}
function showErrorMessage(message){
	$("#jqxNotification").find(".jqx-notification-content ").text("");
	$("#jqxNotification").find(".jqx-notification-content ").text(message);
	$("#jqxNotification").jqxNotification({template: "error"});
	$("#jqxNotification").jqxNotification({autoCloseDelay: 5000});
	$("#jqxNotification").jqxNotification("open");
}
function showInfoMessage(message){
	$("#jqxNotification").find(".jqx-notification-content ").text("");
	$("#jqxNotification").find(".jqx-notification-content ").text(message);
	$("#jqxNotification").jqxNotification({template: "info"});
	$("#jqxNotification").jqxNotification({autoCloseDelay: 2000});
	$("#jqxNotification").jqxNotification("open");
}




/**
 * 获取导出数据的递归函数
 */
function addRecord(record,datafield){
	
	for(var i=0;i<record.length;i++){
		
		var obj = {};
		for(var n=0;n<datafield.length;n++){
			obj[datafield[n].dataField] = record[i][datafield[n].dataField] ;
		}
		obj["level"] = record[i]["level"] ;
		exportData.push(obj);
		if(record[i].records){
			addRecord(record[i].records,datafield);
		}
	}
}

/**
 * 去除html标签
 * @param str
 * @returns
 */
function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}
/**
 * 头部导航条添加文字
 * 父级，子级之间&&隔开
 */
function topbarInnerText(str){
	if(str.indexOf("&&") != -1) {
		var strs = str.split("&&");
		var text = "";
		var len = strs.length;
		for(var i=0;i<len;i++){
			text += strs[i];
			if(i < (len - 1)){
				text += "<span style='font-size:14px;'>&nbsp;&nbsp;>&nbsp;&nbsp;</span>";
			}
		}
		$("#topbar").html(text);
	}else{
		$("#topbar").html(str);
	}
	
}
/**
 * 弹出提示框
 * @param title 标题
 * @param content 内容
 */
function popupPromptBox(title,content,obj){
	$('#eventWindowAll').jqxWindow('open');
	if(title!=undefined || title!=null)
		$('#eventWindowAll').jqxWindow('setTitle', title);
	if(content!=undefined || content!=null)
		$('#eventWindowContent').html(content);
	if(content.length > 15)
		$('#eventWindowContent').css("margin-top","13px");
	$('#eventWindowAll').unbind("close");
	$('#eventWindowAll').on('close', function (event) {
		if (event.args.dialogResult.OK) {
			$(obj);
        }
    });
}
