var queryFilter={};
var hideColumn=new HashMap();
$(document).ready(function() {
	
	//生成主场景汇总列表
	majorRoleNumValid();
//	loadLocationStatisticTable();
	
	 $(window).resize(function (){
		 var scrollWidth = document.getElementById("table_body").offsetWidth - document.getElementById("mainTableBody").scrollWidth;
		 if(scrollWidth<=0){
			 scrollWidth=18;
		 }
	     $("td.td_scroll").width(scrollWidth);
	 });
	
	
	
	//table行选中事件
	$('#maintable_tbody').on('click', 'tr', function(){
		$(this).siblings().removeClass('td_focus');
		$(this).addClass('td_focus');
	});
	//加载查询条件
	loadSearchCondition();
	//初始化查询按钮
	$("#querybutton").jqxButton({theme:theme, width: 60, height: 25 });
	$("#cancelbutton").jqxButton({theme:theme, width: 60, height: 25 });
	$("#clearbutton").jqxButton({theme:theme, width: 60, height: 25 });
	//初始化搜索窗口
	$('#searchdiv').jqxWindow({
		theme:theme,  
	    width: 500,
        height: 370, 
        autoOpen: false,
        isModal: true,
        showCloseButton: true,
        cancelButton: $('#cancelbutton'),
        resizable: false,
        initContent: function () {
        }
    });
	//初始化下拉插件
	$('.selectpicker').selectpicker({
        size: 7
    });
	//下拉控件中当选择空的时候自动取消勾选其他选项，当选择其他选项时，自动取消勾选空选项
    $('.selectpicker').on('change', function(event) {
        var value = event.target.value;
        var eventId = event.target.id;  //获取当前select控件id  
        
        var prevSelectedValue = $("#"+eventId).parent().find(".preValue").val(); //select控件之前选中的值
        if (prevSelectedValue == "blank") {
            $("#"+ eventId).find('option').eq(0).prop('selected', false).removeAttr('selected');
            $("#"+ eventId).selectpicker('render');
            
            $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
            return false;
        }
        
        if (value == "blank") {
            $("#"+ eventId).selectpicker('deselectAll');    //首先取消所有选中
            
            //为[空]值执行选中事件 setSelected
            $("#"+ eventId).find('option[value=blank]').prop('selected', true).attr('selected', 'selected');
            $("#"+ eventId).selectpicker('render');
        }
        $("#"+eventId).parent().find(".preValue").val($("#"+ eventId).val());
    });
    
    //对选择框设置鼠标移动时的显示样式
    $('.searchUl').on('mouseover', function(event) {
        if ($(this).find("li").find("select").val() != null && $(this).find("li").find("select").val() != undefined) {
            $(this).find("li").find(".clearSelection").show();
        }
    });
    
    $('.searchUl').on('mouseout', function(event) {
        $(this).find("li").find(".clearSelection").hide();
    });
    
    $(".clearSelection").on('click', function() {
        var id = $(this).siblings(".selectpicker").attr("id");
        if (id == "majorRoleSelect") {
            //隐藏单选按钮
            $(".checkradiospan").hide();
        }
        $(this).siblings(".selectpicker").selectpicker('deselectAll');
    });
    //隐藏下拉菜单
	$(document).click(function(){
		$("#togglecolumn").hide();
	});
	$("#togglecolumn").on("click",function(ev){
		ev.stopPropagation();
	});
	
	//判断权限
	if(!hasExportViewTotalAuth) {
		$(".export_button").remove();
	}
	
	//场数范围blur事件，判断格式
    $("#minViewNum").on("blur", function() {
        if(!validateInteger($(this).val(),"minViewNum")){
        	return false;
        }
    });
    $("#maxViewNum").on("blur", function() {
        if(!validateInteger($(this).val(),"maxViewNum")){
        	return false;
        }
    });
    $(".figure-input").keyup(function(){
		 $(this).val($(this).val().replace(/[^\d]/g,""));  //清除“数字”和“.”以外的字符      
    }).bind("paste",function(){  //CTR+V事件处理    
 	 $(this).val($(this).val().replace(/[^0-9]/g,''));     
    }).css("ime-mode", "disabled"); //CSS设置输入法不可用
    
    //选择框事件
    $("input[name='completionChk']").click(function(){
    	if($(this).val()==''){//全选
   			$("input[name='completionChk']").prop('checked',$(this).prop('checked'));
    	} else {
    		var allchecked = true;
    		$("input[name='completionChk']").each(function(i,item){
    			if($(item).val()!='' && !$(item).prop('checked')) {
    				allchecked=false;
    			}
    		});
			$("input[name='completionChk']").eq(0).prop('checked',allchecked);
    	}
    });
});
//主要演员数量验证，超过40
function majorRoleNumValid() {
	$.ajax({
		url: '/viewStatisticManager/queryMajorActor',
		type: 'post',
		async: true,
		datatype: 'json',
		data:queryFilter,
		success: function(response){
			if(response.success){
				var majorRoleList = response.majorRoleList;
				//当主演人数过多时，弹出窗口
				if (majorRoleList.length>40) {
					swal({
						title: "提示",
						text: '角色中"主要演员"人数过多，会导致页面加载缓慢，是否去角色表中设置角色类型？',
						type: "warning",
						showCancelButton: true,  
						confirmButtonColor: "rgba(255,103,2,1)",
						confirmButtonText: "去设置",   
						cancelButtonText: "继续查看",   
						closeOnConfirm: false,   
						closeOnCancel: true
					}, function (isConfirm) {
						if (isConfirm) {
							//确定跳转到角色列表
							window.location.href = "/viewRole/toViewRolePage";
						}else {
							//继续查看，继续加载场景汇总
							loadLocationStatisticTable();
						}
					});
				}else {
					//生成场景表列表
					loadLocationStatisticTable();
				}
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//生成主场景汇总列表
function loadLocationStatisticTable(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingTableDiv");
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    
	$.ajax({
		url: '/viewStatisticManager/queryViewLocationStatistic',
		type: 'post',
		async: true,
		datatype: 'json',
		data:queryFilter,
		success: function(response){
			_LoadingHtml.hide();
   		 	$(".opacityAll").hide();
			if(response.success){
				var majorRoleList = response.majorRoleList;
				
				var locationStatisticList = response.locationStatisticList;
				
				var lineMap = response.lineMap;
				var headHtml=[];
				
				var toggleColumn=[];
				
				//计算表格宽度
				var len=0;
				if(majorRoleList) {
					len=majorRoleList.length;
				}
				if(hideColumn.size()!=0) {
					len=len-hideColumn.size();
				}
				var widthArr=calculateTableWidth(len);
				var width=widthArr[0];
				var roleWidth=widthArr[1];
				headHtml.push('<tr>');
				if(!queryFilter.sortField || queryFilter.sortField=='shootLocation'){
					headHtml.push('<td class="td_w120 head td_0" style="'+width+'">拍摄地点<input type="button" class="sort select-sort" onclick="sortFom(this,\'shootLocation\')"></td>');
				}else{
					headHtml.push('<td class="td_w120 head td_0" style="'+width+'">拍摄地点<input type="button" class="sort" onclick="sortFom(this,\'shootLocation\')"></td>');
				}
				headHtml.push('<td class="td_w120 head td_1" style="'+width+'">主场景</td>');
				if(queryFilter.sortField=='viewNum'){
					headHtml.push('<td class="td_w120 head td_2 text-align-center" style="'+width+'">完成/场数<input type="button" class="sort select-sort" onclick="sortFom(this,\'viewNum\')"></td>');
				}else{
					headHtml.push('<td class="td_w120 head td_2 text-align-center" style="'+width+'">完成/场数<input type="button" class="sort" onclick="sortFom(this,\'viewNum\')"></td>');
				}
				headHtml.push('<td class="td_w120 head td_3 text-align-center" style="'+width+'">完成/页数</td>');
				headHtml.push('<td class="td_w120 head td_4" style="'+width+'">气氛</td>');
				//toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" checked value="td_0">拍摄地点<label></li>');
				//toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" checked value="td_1">主场景<label></li>');
				//toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" checked value="td_2">场数/页数<label></li>');
				//toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" checked value="td_3">气氛<label></li>');
				if(majorRoleList && majorRoleList.length>0){
					for(var i=0;i<majorRoleList.length;i++){
						if(hideColumn.containsKey("td_"+(i+5))){
							headHtml.push('<td class="td_w30 head td_' + (i+5) + '" style="'+roleWidth+' display:none;">'+majorRoleList[i].viewRoleName+'</td>');
							toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" value="td_'+(i+5)+'">'+majorRoleList[i].viewRoleName+'<label></li>');
						}else{
							headHtml.push('<td class="td_w30 head td_' + (i+5) + '" style="'+roleWidth+'">'+majorRoleList[i].viewRoleName+'</td>');
							toggleColumn.push('<li><label><input type="checkbox" name="toggleColumnChk" checked value="td_'+(i+5)+'">'+majorRoleList[i].viewRoleName+'<label></li>');
						}
					}
				}
				headHtml.push('<td class="td_scroll"></td>');
				headHtml.push('</tr>');
				var tableHtml=[];
				if(locationStatisticList && locationStatisticList.length>0) {
					var shootLocationIdFlag='-1';
					for(var i=0;i<locationStatisticList.length;i++){
						var obj=locationStatisticList[i];
						tableHtml.push('<tr>');
						if(shootLocationIdFlag!=obj.shootLocationId) {
							shootLocationIdFlag=obj.shootLocationId;
							var shootLocation=obj.shootLocation;
							if(!shootLocation){
								shootLocation='';
							}
							var lineNum=obj.lineNum;
							tableHtml.push('<td class="td_w120 td_0" style="'+width+'" rowspan="'+lineNum+'"><p title="'+shootLocation+'">'+shootLocation+'</p></td>');
						}
						var location=obj.location;
						if(!location){
							location='';
						}
						tableHtml.push('<td class="td_w120 td_1 click" style="'+width+'" onclick="showViewList(\''+obj.shootLocationId+'\',\''+obj.locationId+'\',\''+location+'\')"><p title="'+location+'"><a style="cursor:pointer;">'+location+'</a></p></td>');
						
						var percent = divide(multiply(obj.finishedViewNum, 100), obj.viewNum);
						if (obj.finishedPageNum == null || obj.finishedPageNum == '' || obj.finishedPageNum == undefined) {
							obj.finishedPageNum = 0.0;
						}else {
							obj.finishedPageNum = parseFloat(obj.finishedPageNum).toFixed(1);
						}
						
						if (obj.pageNum == null || obj.pageNum == '' || obj.pageNum == undefined) {
							obj.pageNum = 0.0;
						}else {
							obj.pageNum = parseFloat(obj.pageNum).toFixed(1);
						}
						if(percent < 50){
							if(percent == 0){
								tableHtml.push('<td class="td_w120 td_2 text-align-center" style="'+width+' "><p title="'+obj.finishedViewNum+'/'+obj.viewNum+'">'+obj.finishedViewNum+'/'+obj.viewNum+'</p></td>');
								tableHtml.push('<td class="td_w120 td_3 text-align-center" style="'+width+' "><p title="'+obj.finishedPageNum+'/'+obj.pageNum+'">'+obj.finishedPageNum+'/'+obj.pageNum+'</p></td>');
							}else{
								tableHtml.push('<td class="td_w120 td_2 text-align-center" style="'+width+' background: #D0E5F5;"><p title="'+obj.finishedViewNum+'/'+obj.viewNum+'">'+obj.finishedViewNum+'/'+obj.viewNum+'</p></td>');
								tableHtml.push('<td class="td_w120 td_3 text-align-center" style="'+width+' background: #D0E5F5;"><p title="'+obj.finishedPageNum+'/'+obj.pageNum+'">'+obj.finishedPageNum+'/'+obj.pageNum+'</p></td>');
							}
							
						}else if(percent == 100){
							tableHtml.push('<td class="td_w120 td_2 text-align-center" style="'+width+' background: #ffbaba;"><p title="'+obj.finishedViewNum+'/'+obj.viewNum+'">'+obj.finishedViewNum+'/'+obj.viewNum+'</p></td>');
							tableHtml.push('<td class="td_w120 td_3 text-align-center" style="'+width+' background: #ffbaba;"><p title="'+obj.finishedPageNum+'/'+obj.pageNum+'">'+obj.finishedPageNum+'/'+obj.pageNum+'</p></td>');
						}else{
							tableHtml.push('<td class="td_w120 td_2 text-align-center" style="'+width+' background: #fee9fa;"><p title="'+obj.finishedViewNum+'/'+obj.viewNum+'">'+obj.finishedViewNum+'/'+obj.viewNum+'</p></td>');
							tableHtml.push('<td class="td_w120 td_3 text-align-center" style="'+width+' background: #fee9fa;"><p title="'+obj.finishedPageNum+'/'+obj.pageNum+'">'+obj.finishedPageNum+'/'+obj.pageNum+'</p></td>');
						}
						
						var atmosphere = '';
						if(obj.atmosphere) {
							atmosphere+=obj.atmosphere;
						}
				    	if(obj.site){
				    		if(obj.atmosphere) {
				    			atmosphere+=' ';
				    		}
				    		atmosphere+=obj.site;
				    	}
						tableHtml.push('<td class="td_w120 td_4" style="'+width+'"><p title="'+atmosphere+'">'+atmosphere+'</p></td>');
						//加载角色
						var roleList=obj.roleList;
						if(majorRoleList) {
							for(var m=0;m<majorRoleList.length;m++) {
								var id=m+5;
								var flag = false;
								if(roleList) {
									for(var n=0;n<roleList.length;n++) {
										if(roleList[n].viewRoleId==majorRoleList[m].viewRoleId) {
											flag = true;
											if(hideColumn.containsKey("td_"+id)){
												if(roleList[n].shortName && roleList[n].shortName.trim()) {
													tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+' display:none;">'+roleList[n].shortName+'</td>');
												} else {
													tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+' display:none;">√</td>');
												}
											}else{
												if(roleList[n].shortName && roleList[n].shortName.trim()) {
													tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+'">'+roleList[n].shortName+'</td>');
												} else {
													tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+'">√</td>');
												}
											}											
											break;
										}
									}
								}
								if(!flag) {
									if(hideColumn.containsKey("td_"+id)){
										tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+' display:none;"></td>');
									}else{
										tableHtml.push('<td class="td_w30 td_'+id+'" style="'+roleWidth+'"></td>');
									}
								}
							}
						}
						tableHtml.push('</tr>');
					}
				}
				$("#maintable_thead").empty();
				$("#maintable_tbody").empty();
				$("#maintable_thead").append(headHtml.join(''));
				$("#table_body").css('height','calc(100% - '+($("#table_head").height()+30+35)+'px)');
				$("#maintable_tbody").append(tableHtml.join(''));
				
				//汇总信息
				var totalHtml=[];
				var totalMap = response.totalMap;
				totalHtml.push('统计：拍摄地共' + totalMap.shootLocationNum+'个&nbsp;|&nbsp;');
				totalHtml.push('主场景共'+totalMap.majorLocationNum+'个，全部完成'+totalMap.finishedMajorLocationNum+'个，部分完成'+totalMap.partFinishedMajorLocationNum+'个，未开始'+totalMap.notStartedMajorLocationNum+'个&nbsp;|&nbsp;');
				totalHtml.push('共' + totalMap.viewNum+'场，已完成'+totalMap.finishedViewNum+'场&nbsp;|&nbsp;');
				if (totalMap.pageNum == null || totalMap.pageNum == '' || totalMap.pageNum == undefined) {
					totalMap.pageNum = 0.0;
				}
				if (totalMap.finishedPageNum == null || totalMap.finishedPageNum == '' || totalMap.finishedPageNum == undefined) {
					totalMap.finishedPageNum = 0.0;
				}
				totalHtml.push('共' + parseFloat(totalMap.pageNum).toFixed(1)+'页，已完成'+parseFloat(totalMap.finishedPageNum).toFixed(1)+'页');
				$("#total_info").html(totalHtml.join(''));
				
				//计算滚动条的宽度
				var scrollWidth = document.getElementById("table_body").offsetWidth - document.getElementById("mainTableBody").scrollWidth;
				if(scrollWidth<=0){
					scrollWidth=18;
				}
				$("td.td_scroll").width(scrollWidth);
				
				$("#togglecolumn").empty();
				$("#togglecolumn").append(toggleColumn.join(''));
				
				
				$('input[name="toggleColumnChk"]').on('click', function(ev){
					var classname=$(this).attr('value');
				 	if($(this).prop('checked')) {
				 		hideColumn.remove(classname);
				 		$("."+classname).show();
				 	} else {
				 		hideColumn.put(classname,classname);
				 		$("."+classname).hide();
				 	}
			 		reCalculateTableWidth(len);
					ev.stopPropagation();
				});
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
//排序
function sortFom(own, sortField){
	$(".select-sort").removeClass("select-sort");
	$(own).addClass("select-sort");
	queryFilter.sortField=sortField;
	//加载场景汇总信息
	loadLocationStatisticTable();
}
//显示隐藏--重新计算表格宽度
function reCalculateTableWidth(totallen) {
	var len = $('input[name="toggleColumnChk"]:checked').length;
	var widthArr=calculateTableWidth(len);
	var width=widthArr[0];
	if(width) {
		width=width.substring(width.indexOf(":")+1,width.indexOf(";"));
	}
	var roleWidth=widthArr[1];
	if(roleWidth){
		roleWidth=roleWidth.substring(roleWidth.indexOf(":")+1,roleWidth.indexOf(";"));
	}
	//更新表格宽度
	for(var i=0;i<totallen+5;i++){
		if(i<5){
			$(".td_"+i).eq(0).css('width',width);
			$(".td_"+i).eq(1).css('width',width);
		}else{
			$(".td_"+i).eq(0).css('width',roleWidth);
			$(".td_"+i).eq(1).css('width',roleWidth);
		}
	}
}
//计算表格宽度
function calculateTableWidth(len){
	var scrollWidth = document.getElementById("table_body").offsetWidth - document.getElementById("mainTableBody").scrollWidth;
	if(scrollWidth<=0){
		scrollWidth=18;
	}
	var width='';
	var roleWidth='';
	if(len==0) {
		width='width:calc((100% - '+scrollWidth+'px)/5);';
	}else{
		var realWidth=600 + len * 30 + scrollWidth;
		if(realWidth<$("#table_head").width()) {
			width='width:calc((100% - '+scrollWidth+'px) / '+(5+len)+');';
			if(($("#table_head").width()-scrollWidth)/(5+len)<120) {
				width="";
				roleWidth='width:calc((100% - '+(600+scrollWidth)+'px) / '+len+');';
			}else{
				roleWidth=width;
			}
		}
	}
	return [width,roleWidth];
}
//body底部滚动条滑动，头部跟着滑动
function bodyScroll(){
	var b = document.getElementById("table_body").scrollLeft;
	document.getElementById("table_head").scrollLeft = b;
}
//显示搜索窗口
function showSearchWin(){
	$("#searchdiv").jqxWindow("open");
}
//关闭搜索窗口
function closeSearchWin(){
	$("#searchdiv").jqxWindow("close");
}
//加载查询条件
function loadSearchCondition() {
    $.ajax({
        url:"/viewStatisticManager/loadSearchCondition",
        dataType:"json",
        type:"post",
        async: true,
        success:function(data){
            if(data.success) {
            	var viewFilterDto = data.viewFilterDto;
        		var shootLocationList = viewFilterDto.shootLocationList;//拍摄地
                var firstLocationList = viewFilterDto.firstLocationList;//主场景
                var majorRoleList = viewFilterDto.majorRoleList;//主要演员
        		
				for (var shotLocation in shootLocationList) {
                	
                    $("#shootLocationSelect").append("<option value="+ shotLocation + ">" + shootLocationList[shotLocation] + "</option>");
                }
                $("#shootLocationSelect").selectpicker('refresh');
                
                for (var fLocation in firstLocationList) {
                    $("#firstLocationSelect").append("<option value="+ fLocation + ">" + firstLocationList[fLocation] + "</option>");
                }
                $("#firstLocationSelect").selectpicker('refresh');
                
                for (var mrole in majorRoleList) {
                    $("#majorRoleSelect").append("<option value="+ mrole + ">" + majorRoleList[mrole] + "</option>");
                }
                $("#majorRoleSelect").selectpicker('refresh');
            }
        }
    });
}
//设置查询条件，并查询结果
function gotoquery(){
	//拍摄地点
	var shootLocation =$("#shootLocationSelect").val();
	var shootLocationStr = "";
	if(shootLocation){
		for(var i=0;i<shootLocation.length;i++){
			shootLocationStr+=shootLocation[i]+",";
		}
		shootLocationStr=shootLocationStr.substring(0,shootLocationStr.length-1);
	}
	queryFilter.shootLocation=shootLocationStr;
	//主场景
	var location =$("#firstLocationSelect").val();
	var locationStr = "";
	if(location){
		for(var i=0;i<location.length;i++){
			locationStr+=location[i]+",";
		}
		locationStr=locationStr.substring(0,locationStr.length-1);
	}
	queryFilter.major=locationStr;
	//主要演员
	var crewRole =$("#majorRoleSelect").val();
	var crewRoleStr = "";
	if(crewRole){
		for(var i=0;i<crewRole.length;i++){
			crewRoleStr+=crewRole[i]+",";
		}
		crewRoleStr=crewRoleStr.substring(0,crewRoleStr.length-1);
		queryFilter.searchMode=$("input[name='searchMode']:checked").val();
	}
	queryFilter.roles=crewRoleStr;
	//排序
//	var sortField=$("input[name='sortField']:checked").val();
//	if(sortField) {
//		queryFilter.sortField=sortField;
//	} else {
//		queryFilter.sortField='';
//	}
	//场数
	queryFilter.minViewNum=$("#minViewNum").val();
	queryFilter.maxViewNum=$("#maxViewNum").val();
	//完成度
	var completion = '';
	$("input[name=completionChk]:checked").each(function(i,n){
		if($(this).val()!='') {
			if(completion != ''){
				completion += ",";
			}
			completion += $(this).val();
		}		
	});
	queryFilter.completion=completion;

	//关闭查询窗口
	closeSearchWin();
	//加载场景汇总信息
	loadLocationStatisticTable();
}
//更改主演下拉框时触发的方法
function changeMajorRole(event){
    $("#everyOneAppear").hide();
    $("#notEvenyOneAppear").hide();
    var majorRoleVal = $("#majorRoleSelect").val();
    
    if (majorRoleVal != null) {
        var selectedLength = majorRoleVal.length;
        if (selectedLength > 1) {
            /* 选择多条数据的情况 */            
            // 展现"同时出现"、"不同时出现"
            $("#everyOneAppear").show();
            $("#notEvenyOneAppear").show();
        } else {
            /* 由选择多条数据变为选择一条数据情况 */
            
            //如果此时同时出现或不同时出现被选中，则设置单选按钮“出现即可”选中
            var searchMode=$("input[name='searchMode']:checked").val();
            if (searchMode == 2) {
                $("input[name='searchMode'][value='0']").prop("checked",true);
            }
            
            //展现"出现即可"、"不出现"，隐藏"同时出现"、"不同时出现"
            $("#anyOneAppear").show();
            $("#noOneAppear").show();
            $("#everyOneAppear").hide();
            $("#notEvenyOneAppear").hide();
        }
    	$(".checkradiospan").show();
    } else {
        //一条数据都没选择的情况
        //隐藏"出现即可"、"不出现"、"同时出现"、"不同时出现"
    	$(".checkradiospan").hide();
    }
}
//导出场景汇总信息
function exportExcel(){

	var form = $("<form></form>");
	
	//拍摄地点
	var shootLocation = queryFilter.shootLocation;
	if(shootLocation) {
		form.append("<input type='hidden' name='shootLocation'>");
		form.find("input[name='shootLocation']").val(shootLocation);
	}
	//主场景
	var location = queryFilter.major;
	if(location) {
		form.append("<input type='hidden' name='major'>");
		form.find("input[name='major']").val(location);
	}
	//主要演员
	var crewRole = queryFilter.roles;
	if(crewRole) {
		form.append("<input type='hidden' name='roles'>");
		form.find("input[name='roles']").val(crewRole);
		form.append("<input type='hidden' name='searchMode'>");
		form.find("input[name='searchMode']").val(queryFilter.searchMode);
	}
	//排序
	var sortField=queryFilter.sortField;
	if(sortField) {
		form.append("<input type='hidden' name='sortField'>");
		form.find("input[name='sortField']").val(sortField);
	}
	//场数
	var minViewNum=queryFilter.minViewNum;
	if(minViewNum){
		form.append("<input type='hidden' name='minViewNum'>");
		form.find("input[name='minViewNum']").val(minViewNum);
	}
	var maxViewNum=queryFilter.maxViewNum;
	if(maxViewNum){
		form.append("<input type='hidden' name='maxViewNum'>");
		form.find("input[name='maxViewNum']").val(maxViewNum);
	}
	var completion=queryFilter.completion;
	if(completion) {
		form.append("<input type='hidden' name='completion'>");
		form.find("input[name='completion']").val(completion);
	}
	
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    
    
    var fileAddress ="";
    var fileName = "";
	$.ajax({
		 url:"/viewStatisticManager/exportExcel",
         data:form.serialize(),
         dataType:"json",
         type:"post",
         success:function(response){
    		 _LoadingHtml.hide();
    		 $(".opacityAll").hide();
        	 if (response.success) {
        		fileAddress = response.downloadPath;
        		fileName = response.fileName;
			}else{
	        	showErrorMessage(response.message);
	            return;
	        }
        	 
        	var form = $("<form></form>");
            form.attr("action","/fileManager/downloadFileByAddr");
            form.attr("method","post");
            form.append("<input type='hidden' name='address'>");
            form.append("<input type='hidden' name='fileName'>");
            form.find("input[name='address']").val(fileAddress);
            form.find("input[name='fileName']").val(fileName);
			$("body").append(form);
			form.submit();
			form.remove();
         }
	});	
}
//显示/隐藏td选择框
function toggletd(ev){
	$("#togglecolumn").toggle();
	ev.stopPropagation();
}
//清空查询条件
function clearSearchCon(){
	$(".clearSelection").trigger('click');
	$(".search-text").val('');
	$("input[name='completionChk']").prop('checked',false);
}
//验证整数
function validateInteger(value){
	if(value==""){
		return true;
	}
	if(!/^[1-9]\d*$/.test(value)) {
		//$("#seriesNoErrorMsg").text("请输入整数");
		return false;
	}
	return true;
}
//显示主场景所在的场景信息
function showViewList(shootLocationId, locationId, locationName) {
	$("#rightPopUpWin").show().animate({"right": "0px"}, 500);
	//加载该科目账务详情
	loadViewList(shootLocationId, locationId, locationName);
}
//关闭右侧滑出框
function closeRightWin() {
	var right = $("#rightPopUpWin").width();
	$("#rightPopUpWin").animate({"right": 0-right}, 500);
	setTimeout(function(){
		$("#rightPopUpWin").hide();
	}, 500);
}
//加载主场景所在的场景
function loadViewList(shootLocationId, locationId, locationName) {
	$("#shootLocationId").val(shootLocationId);
	$("#locationId").val(locationId);
	$("#locationName").val(locationName);
	$("#viewListDiv").html("");
	var roles=queryFilter.roles;
	if(!roles) {
		roles="";
	}
	var searchMode=queryFilter.searchMode;
	if(!searchMode){
		searchMode="";
	}
	$.ajax({
		url: '/viewStatisticManager/queryViewListByMajorLocation',
		type: 'post',
		data:{shootLocationId:shootLocationId,locationId:locationId,roles:roles,searchMode:searchMode},
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var viewList=response.result;
				var gridHead_array = ["<div class='t_i_h' id='hh'><table class='scence-table' cellspacing=0 cellpadding=0><thead>"];
				gridHead_array.push("<tr>");
				
				gridHead_array.push("<td><p style='width:80px;'>拍摄状态</p></td>");
				if (crewType == 0 || crewType == 3) {
					gridHead_array.push("<td><p style='width:50px;'>场次</p></td>");
				} else {
					gridHead_array.push("<td><p style='width:50px;'>集-场</p></td>");
				}
				gridHead_array.push("<td><p style='width:70px;'>特殊提醒</p></td>");
				gridHead_array.push("<td><p style='width:65px;'>气氛/内外</p></td>");
				gridHead_array.push("<td><p style='width:50px;'>页数</p></td>");
				
				gridHead_array.push("<td><p style='width:100px;'>拍摄地点</p></td>");
				gridHead_array.push("<td><p style='width:150px;'>场景</p></td>");
				gridHead_array.push("<td><p style='width:150px;'>主要内容</p></td>");
				
				gridHead_array.push("<td><p style='width:150px;'>主要演员</p></td>");
				gridHead_array.push("<td><p style='width:130px;'>特约/群演</p></td>");
				
				gridHead_array.push("<td><p style='width:150px;'>服化道</p></td>");
				gridHead_array.push("<td><p style='width:150px;'>特殊道具</p></td>");
				
				gridHead_array.push("<td><p style='width:150px;'>商植</p></td>");
				gridHead_array.push("<td style='border-right:none !important;'><p style='width:150px;'>备注</p></td>");
				
				gridHead_array.push("<td><p style='width:7px;'></p></td>");
				
				gridHead_array.push("</tr>");
				gridHead_array.push("</thead></table></div>");
				
				var gridBody_array=["<div class='auto-height cc' id='ca' onscroll='viewGridScroll()'><table cellpadding='0' cellspacing='0' border='0' id='ViewGrid'><tbody id='ViewTbody'>"];
				if(viewList && viewList.length > 0) {
					for(var i=0;i<viewList.length;i++){
						var rowData=viewList[i];
						var statusClass=" style='background-color:"+getColor(rowData.shootStatus)+"' ";
						
						gridBody_array.push("<tr "+ statusClass +">");						
			
						var shootStatusText = "";
						if(rowData.shootStatus){
							shootStatusText=rowData.shootStatus;
						}
						gridBody_array.push("<td><p style='width:80px;'>"+shootStatusText+"</p></td>");//拍摄状态
						
						//电影类型的剧组不显示集次
						if (crewType == 0 || crewType == 3) {
							gridBody_array.push("<td><p style='width:50px;' title='"+ rowData.viewNo +"'>"+ rowData.viewNo+"</p></td>");//集场
						} else {
							gridBody_array.push("<td><p style='width:50px;' title='"+ rowData.seriesNo+"-"+rowData.viewNo +"'>"+rowData.seriesNo+"-"+rowData.viewNo+"</p></td>");//集场
						}
						
						//添加集场数组数据
						
						var specialRemindText= rowData.specialRemind;
						if(specialRemindText==null){
							specialRemindText="";
						}
						gridBody_array.push("<td><p style='width:70px;'>"+specialRemindText+"</p></td>");//季节
						
						//气氛.内外景
						var siteText="";
						var atmosphere = "";
						if(rowData.atmosphereName){
							atmosphere=rowData.atmosphereName;
							if(rowData.site){
								siteText=rowData.site;
								gridBody_array.push("<td><p style='width:65px;'>"+atmosphere + "/"+ siteText +"</p></td>");
							}else {
								gridBody_array.push("<td><p style='width:65px;'>"+atmosphere+"</p></td>");
							}
						}else {
							if(rowData.site){
								siteText=rowData.site;
								gridBody_array.push("<td><p style='width:65px;'>"+siteText+"</p></td>");
							}else {
								gridBody_array.push("<td><p style='width:65px;'></p></td>");
							}
						}
						
						//原页数
						gridBody_array.push("<td><p style='width:50px;'>"+ rowData.pageCount +"</p></td>");//页数
					   	
					   	//拍摄地点
					   	var shootLocationText="";
						if(rowData.shootLocation){
							shootLocationText=rowData.shootLocation;
						}
						gridBody_array.push("<td><p style='width:100px;' title='"+ shootLocationText +"'>"+shootLocationText+"</p></td>");
					    
					    //场景
					    var viewText=[];
					    if(rowData.majorView) {
					    	viewText.push(rowData.majorView);
					    }
					    if(rowData.minorView) {
					    	viewText.push(rowData.minorView);
					    }
					    if(rowData.thirdLevelView) {
					    	viewText.push(rowData.thirdLevelView);
					    }
					    if(viewText.length>0) {
					    	gridBody_array.push("<td><p style='width:150px;' title='"+ viewText.join(" | ") +"'>" + viewText.join(" | ") +"</p></td>");
					    } else {
					    	gridBody_array.push("<td><p style='width:150px;' title=''></p></td>");
					    }
					    
					    //文武戏
					    
						var mainContentText="";
					   	if(rowData.mainContent){
					   		mainContentText=rowData.mainContent;
					   	}
					   	gridBody_array.push("<td><p style='width:150px;' title='"+ mainContentText +"'>"+mainContentText+"</p></td>");//主要内容
						var roleListText="";
					   	if(rowData.roleList){
					   		roleListText=rowData.roleList;
					   	}
					   	gridBody_array.push("<td><p style='width:150px;' title='"+ roleListText +"'>"+roleListText+"</p></td>");//主要演员
					   	
					   	//特约演员、群众演员
						var guestRoleListText="";
						var massRoleListText="";
					   	if(rowData.guestRoleList){
					   		guestRoleListText=rowData.guestRoleList;
					   		if(rowData.massRoleList){
					   	   		massRoleListText=rowData.massRoleList;
					   	   		gridBody_array.push("<td><p style='width:130px;' title='"+ guestRoleListText+ " | " + massRoleListText +"'>"+guestRoleListText+ " | " + massRoleListText +"</p></td>");
					   	   	}else {
					   	   		gridBody_array.push("<td><p style='width:130px;' title='"+ guestRoleListText +"'>"+guestRoleListText+"</p></td>");//特约演员
							}
					   	}else {
					   		if(rowData.massRoleList){
					   	   		massRoleListText=rowData.massRoleList;
					   	   		gridBody_array.push("<td><p style='width:130px;' title='"+ massRoleListText +"'>"+massRoleListText+"</p></td>");//特约演员
					   		}else {
					   			gridBody_array.push("<td><p style='width:130px;' title=''></p></td>");//特约演员
							}
						}
					   	//服化道
					   	var clothesMakeupPropsText=[];
					   	if(rowData.makeupName){
					   		clothesMakeupPropsText.push(rowData.makeupName);
					   	}
					   	if(rowData.clothesName) {
					   		clothesMakeupPropsText.push(rowData.clothesName);
					   	}
					   	if(rowData.propsList) {
					   		clothesMakeupPropsText.push(rowData.propsList);
					   	}
					   	if(clothesMakeupPropsText.length>0) {
					   		gridBody_array.push("<td><p style='width:150px;' title='"+ clothesMakeupPropsText.join(" | ") +"'>" + clothesMakeupPropsText.join(" | ") + "</p></td>");
					   	} else {
							gridBody_array.push("<td><p style='width:150px;' title=''></p></td>");
						}
					   	//特殊道具
						var specialPropsListText="";
					   	if(rowData.specialPropsList){
					   		specialPropsListText=rowData.specialPropsList;
					   	}
					   	gridBody_array.push("<td><p style='width:150px;' title='"+ specialPropsListText +"'>"+specialPropsListText+"</p></td>");//特殊道具
						var advertNameText="";
					   	if(rowData.advertName){
					   		advertNameText=rowData.advertName;
					   	}
					   	gridBody_array.push("<td><p style='width:150px;' title='"+ advertNameText +"'>"+advertNameText+"</p></td>");//商植
						var remarkText="";
					   	if(rowData.viewRemark){
					   		remarkText=rowData.viewRemark;
					   	}
					   	gridBody_array.push("<td><p style='width:150px;' title='"+ remarkText +"'>"+remarkText+"</p></td>");//备注
					   	gridBody_array.push("</tr>");
					}
				}
				gridBody_array.push("<tbody></table></div>");
				$("#viewListDiv").html(gridHead_array.join("")+gridBody_array.join(""));
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}
function viewGridScroll() {
	var b = document.getElementById("ca").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}
function getColor(shootStatus){	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(noticeShootStatusMap.getKey(shootStatus));
	return divColor;
}
//导出主场景场景表
function exportViewList() {
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
	var formData = [];
	formData.push(" <form action='/viewStatisticManager/exportViewListByMajorLocation'>");
	formData.push(" <input type='text' name='shootLocationId' value='"+$("#shootLocationId").val()+"'/>");	
	formData.push(" <input type='text' name='locationId' value='"+$("#locationId").val()+"'/>");
	formData.push(" <input type='text' name='locationName' value='"+$("#locationName").val()+"'/>");
	var roles=queryFilter.roles;
	if(roles) {
		formData.push(" <input type='text' name='roles' value='" + roles + "'/>");
	}
	var searchMode=queryFilter.searchMode;
	if(!searchMode){
		formData.push(" <input type='text' name='searchMode' value='" + searchMode + "'/>");
	}
	formData.push(" </form>");
	
	var form = $(formData.join(""));
	
	$("body").append(form);
	form.submit();
	form.remove();
	_LoadingHtml.hide();
	 $(".opacityAll").hide();
}