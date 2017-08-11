filter.pagenum = 0;
filter.pagesize = 100;
var crewType;
var gridColumns = [];
var hideTdIndex = [];
$(function(){
	$("#printBtn").attr("disabled", true);//打印按钮不可用
	var hideColumnString = $("#hideColumn").val();//隐藏列
	var hideColumn = [];
	if(hideColumnString != ""){
		hideColumn = hideColumnString.split(",");
		for(var i=0; i< hideColumn.length; i++){
			hideTdIndex.push(parseInt(hideColumn[i]));
		}
	}
	//获取剧组类型
	getCrewType();
	//获取角色列表
	getRoleList();
});

//获取剧组类型
function getCrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
	            crewName = response.crewName; //剧组名称
	            $("#viewListTitle").html("《" + crewName + "》场景表");
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}
//获取角色列表
function getRoleList(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: false,
		datatype: 'json',
		success: function(response){
			var roleList = [];
			if(response.success){
				roleList = response.majorRoleList;
				initGrid(roleList);
			}else{
				initGrid(roleList);
				showErrorMessage(response.message);
			}
		}
	});
}


//初始化表格
function initGrid(roleList){
	//加载集次编号信息
    viewColumn = function(columnfield, value, columnproperties, rowdata){
		var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
//    	return "<span style='cursor:pointer;color:#52b0cc;'  class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' onclick='showViewContent(\""+rowdata.viewId+"\",\""+rowdata.viewId+"\")'>" + seriesNoAndViewNo + "</span>";
		return seriesNoAndViewNo;
    };
    
    //加载场次编号
    viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
        var viewNo =rowdata.viewNo;
//        return "<span style='cursor:pointer;color:#52b0cc;' class='bold' name='seriesViewNo' sval='"+ rowdata.shootStatus +"' onclick='showViewContent(\""+rowdata.viewId+"\",\""+rowdata.viewId+"\")'>" + viewNo + "</span>";
        return viewNo;
    };
  //加载角色列信息
    roleColumn = function (columnfield, value, columnproperties, rowdata) {
    	//操作列html
    	var thisRowData = rowdata;
    	
    	var roleArray = thisRowData.roleList;
    	for(var i=0;i<roleArray.length;i++){
    		if(columnproperties.text==roleArray[i].viewRoleName){
    			var roleNum = roleArray[i].roleNum;
    			if (roleNum == 0) {
    				return "OS";
				}else {
					if(roleArray[i].shortName==null || roleArray[i].shortName.trim() == ""){
						return "√";
					}else{
						return roleArray[i].shortName;
					}
				}
    		}
    	}
    	return "";
    };
	//电影类型的剧组不显示集次
    if (crewType == 3) {
      gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn, width: '65px' ,pinned: true});
    } else {
      gridColumns.push({ text: '集-场', cellsrenderer: viewColumn, width: '65px' ,pinned: true});
    }
    rendergridrows = function (params) {
    	//调用json返回的列表数据
        return params.data;
    };
    
    //加载气氛信息
    atmosphere=function(columnfield, value, columnproperties, rowdata){
    	var atmosphere= rowdata.atmosphereName;
    	if(atmosphere==null){
    		atmosphere="";
    	}
    	return atmosphere;
    };
    
    //加载季节信息
    seasonColumn=function(columnfield, value, columnproperties, rowdata){
    	var seasonText= '';
    	if(rowdata.specialRemind !=null && rowdata.specialRemind != undefined){
    		seasonText=rowdata.specialRemind;
    	}
    	return "<span title='"+ seasonText +"'>" + seasonText + "<span>";
    };
    
    //加载内外景信息
    siteColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.site){
    		text=rowdata.site;
    	}
    	return text;
    };
    
    //加载主场景信息
    majorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.majorView){
    		text=rowdata.majorView;
    	}
    	return text;
    };
    
    //加载次场景信息
    minorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.minorView){
    		text=rowdata.minorView;
    	}
    	return text;
    };
    
    //加载三级场景信息
    thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.thirdLevelView){
    		text=rowdata.thirdLevelView;
    	}
    	return text;
    };
    
    //加载主要内容信息
    mainContentColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.mainContent){
    		text=rowdata.mainContent;
    	}
    	return text;
    };
    
    //加载页数信息
    pageCountColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.pageCount){
    		text=rowdata.pageCount;
    	}
    	return text;
    };
    
    //加载特约演员信息
    guestRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.guestRoleList){
    		text=rowdata.guestRoleList;
    	}
    	return text;
    };
    
    //加载主要演员信息
    massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.massRoleList){
    		text=rowdata.massRoleList;
    	}
    	return text;
    };
    
    //加载道具信息
    propsListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.propsList){
    		text=rowdata.propsList;
    	}
    	return text;
    };
    
    //加载服装信息
    clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.clothesName){
    		text=rowdata.clothesName;
    	}
    	return text;
    };
    
    //加载化妆信息
    makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.makeupName){
    		text=rowdata.makeupName;
    	}
    	return text;
    };
    
    //加载拍摄日期信息
    shootDateColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootDate){
    		text=rowdata.shootDate;
    	}
    	return text;
    };
    
    //加载备注信息
    remarkColumn= function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.remark){
    		text=rowdata.remark;
    	}
    	return text;
    };
    
    //加载拍摄地点信息
    shootLocationColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.shootLocation){
    		text=rowdata.shootLocation;
    	}
    	return text;
    };
    
    
    //加载拍摄状态信息
    shootStatusColumn=function(columnfield, value, columnproperties, rowdata){
    	var shootStatusText = rowdata.shootStatus;
    	if(shootStatusText==null){
    		shootStatusText="";
    	}else {
    		if(shootStatusText == 0){
    			shootStatusText = "未完成";
    		}else if(shootStatusText == 1){
    			shootStatusText = "部分完成";
    		}else if(shootStatusText == 2){
    			shootStatusText = "完成";
    		}else if(shootStatusText == 3){
    			shootStatusText = "删戏";
    		}else if(shootStatusText == 4){
    			shootStatusText = "加戏";
    		}else if(shootStatusText == 5){
    			shootStatusText == "加戏完成";
    		}else {
    			shootStatusText == "完成";
    		}
    	}
    	return shootStatusText;
    };
    
    //加载广告信息
    advertNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.advertName){
    		text=rowdata.advertName;
    	}
    	return text;
    };
    
    //加载数据列,拼接场景列表的表头信息
    gridColumns.push({ width: '40px',isCheckbox:true });
    gridColumns.push(
    		{ text: '特殊提醒', cellsrenderer: seasonColumn, width: '60px' },
            { text: '气氛',cellsrenderer: atmosphere, width: '40px'},
            { text: '内外景',cellsrenderer:siteColumn, width: '40px'},
            { text: '拍摄地点', cellsrenderer: shootLocationColumn, width: '120px' },
            { text: '主场景',cellsrenderer:majorViewColumn, width: '120px' },
            { text: '次场景',cellsrenderer:minorViewColumn , width: '120px' },
            { text: '三级场景',cellsrenderer: thirdLevelViewColumn, width: '120px' },
            { text: '主要内容',cellsrenderer: mainContentColumn, width: '120px' },
            { text: '页数',cellsrenderer: pageCountColumn, width: '40px'}
    );
    if(roleList.length != 0){
    	for(var i = 0; i<roleList.length; i++){
            gridColumns.push({ text: roleList[i].viewRoleName, cellsrenderer: roleColumn, width: '20px',isRoleColumn:true });
        }
    }
    gridColumns.push(
    		   { text: '特约演员',cellsrenderer: guestRoleListColumn, width: '90px' },
    	       { text: '群众演员',cellsrenderer: massRoleListColumn, width: '90px' },
    	       { text: '服装',cellsrenderer: clothesNameColumn,width: '90px' },
    	       { text: '化妆',cellsrenderer: makeupNameColumn, width: '90px' },
    	       { text: '道具',cellsrenderer: propsListColumn, width: '90px' },
    	       { text: '特殊道具',filedName: "specialPropsList", width: '90px' },
    	       { text: '备注',cellsrenderer: remarkColumn, width: '90px' },
    	       { text: '商植', cellsrenderer: advertNameColumn, width: '120px' },
    	       { text: '拍摄时间',cellsrenderer: shootDateColumn, width: '90px' },
    	       { text: '拍摄状态', cellsrenderer:  shootStatusColumn, width: '90px' }
    );
    var grid = new produceTable('/viewManager/loadViewList', gridColumns);
    grid.tableFrame();
}





//生成表格方法
function produceTable(url, gridColumns){
	$this = this;
	this.url = url;
	this.gridColumns = gridColumns;
	var columns = this.gridColumns;
	//生成表格表头和表体
	this.tableFrame = function(){
		var _head = [];
		_head.push('<tr>');
		for(var i=0; i<columns.length; i++){
			if(!(columns[i].isCheckbox)){
				if(columns[i].style){
					if(jQuery.inArray(i,hideTdIndex) == -1){//判断该列是否隐藏
						_head.push('<td width="50px" cellid="'+ i +'" style="'+columns[i].style+'" class="bold"><p style="width:50px">'+columns[i].text+'</p></td>');
					}else{
//						_head.append('<td width="50px" cellid="'+ i +'" style="display: none;'+columns[i].style+'" class="bold"><p style="width:50px">'+columns[i].text+'</p></td>');
					}
					
				}else{
					if(jQuery.inArray(i,hideTdIndex) == -1){
						_head.push('<td width="'+columns[i].width+'" cellid="'+ i +'" class="bold"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
					}else{
//						_head.append('<td width="'+columns[i].width+'" cellid="'+ i +'" class="bold" style="display:none;"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
					}
					
				}
			}
				
		}
		_head.push('</tr>');
		$("#viewGrid").append(_head.join(""));
		//显示加载中
		$this.loading();
		//查询表格数据
		$this.queryViewData();
	};
	this.loading = function(){
		//显示loading效果
		/*显示加载中*/
		var clientWidth=window.screen.availWidth;
		//获取浏览器页面可见高度和宽度
		var _PageHeight = document.documentElement.clientHeight;
		//计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
		var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 230) / 2 : 0,
		_LoadingLeft = clientWidth > 240 ? (clientWidth - 240) / 2 : 0;
		//在页面未加载完毕之前显示的loading Html自定义内容
		var _LoadingHtml = $("#loadingDataDiv");
		$(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
		//呈现loading效果
		_LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
		_LoadingHtml.show();
	};
	
	this.queryViewData = function(){
		$.ajax({
			url: $this.url,
			type: 'post',
			data: filter,
			datatype: 'json',
			success: function(response){
				if(response.success){
					var pageCount = response.result.pageCount;
					var result = response.result.resultList;
					//生成打印场景表格
					$this.produceGrid(result, pageCount);
				}else{
					showErrorMessage(response.message);
				}
			}
		});
	};
	
	this.produceGrid = function(result, pageCount){
		filter.pagenum += 1;
		var _rowArray = [];
		for(var i=0; i< result.length; i++){
			_rowArray.push($this.creatRow(result[i]));
		}
		
		$("#viewGrid").append(_rowArray);
		//分页获取表格数据
		if(filter.pagenum > pageCount){
			//取消loading效果
			$("#loadingDataDiv").hide();
			$(".opacityAll").hide();
			$("#printBtn").attr("disabled", false);
			var width = $("#viewGrid").width();
			return;
		}else{
			return $this.queryViewData();
		}
	};
	
	this.creatRow = function(rowData){
		var _row = [];
		_row.push('<tr>');
		for(var j=0;j<columns.length;j++){
			if(!(columns[j].isCheckbox)){
				if(columns[j].cellsrenderer){
					if(jQuery.inArray(j,hideTdIndex) == -1){//判断当前列是否是隐藏列
						_row.push('<td cellid="'+ j +'" style="height:14px;width:' + columns[j].width + ';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</td>');
					}else{
//						_row.push('<td cellid="'+ j +'" style="display: none;"><p style="height:14px;width:' + columns[j].width + ';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</p></td>');
					}
					
				}else{
					if(jQuery.inArray(j,hideTdIndex) == -1){
						_row.push('<td cellid="'+ j +'" style="height:14px;width:' + columns[j].width + ';">'+rowData[columns[j].filedName]+'</td>');
					}else{
//						_row.push('<td cellid="'+ j +'" style="display: none;"><p title="'+rowData[columns[j].filedName]+'" style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+rowData[columns[j].filedName]+'</p></td>');
					}
					
				}
			}
				
		}
		_row.push("</tr>");
		return _row.join("");
	};
}

//打印
function printViewList(){
	$("#printBtn").hide();
	window.print();
	$("#printBtn").show();
}


