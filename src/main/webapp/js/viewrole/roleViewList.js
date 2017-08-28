var crewType = null;
var filter = {};

$(document).ready(function(){
	
	//获取剧组类型
	getCrewType();
	
	//拼接列表数据
	loadMajorList();
});

////获取剧组类型
function getCrewType(){
	$.ajax({
		url: '/viewManager/getCrewType',
		type: 'post',
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
	            crewType = response.crewType; //剧组的类型
			}else{
					showErrorMessage(response.message);
			}
		}
	});
}

//加载主演数据
function loadMajorList(){
	$.ajax({
		url: '/viewManager/queryViewList',
		type: 'post',
		async: true,
		datatype: 'json',
		success: function(response){
			if(response.success){
				var majorRoleList = response.majorRoleList;
				//生成场景表列表
				loadViewListTable(majorRoleList);
			}else{
				showErrorMessage(response.message);
			}
		}
	});
}


//生成场景表列表
function loadViewListTable(majorRoleList){
	var viewRoleName = $("#roleName").val();
	var viewRoleId = $("#roleId").val();
	filter = {};
	//拼接查询条件
	filter.roles = viewRoleId;
	filter.searchMode = 1;
	
	//初始化roleArray数据
	roleArray= [];
	for(var i= 0; i < majorRoleList.length; i++){
		var role = majorRoleList[i];
		roleArray.push({text: role.viewRoleName, value: role.viewRoleId});
	}
	
	//加载source
    source =
    {
        datatype: "json",
        datafields: [
			{ name: 'viewId,',type: 'string' },//场景id
			{ name: 'seriesNo,',type: 'int' },//集次编号
			{ name: 'viewNo,',type: 'string' },//场次编号
	        { name: 'specialRemind',type: 'string' },//特殊提醒
	        { name: 'atmosphereId',type: 'int' },//气氛
	        { name: 'site',type: 'string' },//内外景
	        { name: 'shootLocation',type: 'string' },//拍摄地点
	        { name: 'majorView',type: 'string' },//主场景
	        { name: 'minorView',type: 'string' },//次场景
	        { name: 'thirdLevelView',type: 'string' },//三级场景
	        { name: 'mainContent',type: 'string' },//主要内容
	        { name: 'pageCount',type: 'string' },//页数
	        { name: 'guestRoleList',type: 'string' },//特约演员
	        { name: 'massRoleList',type: 'string' },//群众演员
	        { name: 'propsList',type: 'string' },//普通道具
	        { name: 'specialPropsList',type: 'string' },//特殊道具
	        { name: 'clothesName',type: 'string' },//服装
	        { name: 'makeupName',type: 'string' },//化妆
	        { name: 'shootDate',type: 'string' },//拍摄时间
	        { name: 'remark',type: 'string' },//备注
	        { name: 'shootStatus',type: 'string' },//拍摄状态
	        { name: 'advertName',type: 'string' }//商植
                ],
        type:'post',
        beforeprocessing:function(data){
        	//查询之后可执行的代码
        	//全局变量赋值
        	source.totalrecords=data.result.total;
        },
        root:'resultList',
        processdata: function (data) {
            //查询之前可执行的代码
        },
        url:'/viewManager/loadViewList'
    };
	
    //加载角色列信息
    roleColumn = function (columnfield, value, columnproperties, rowdata) {
    	//操作列html
    	var thisRowData = rowdata;
    	
    	var roleArray = thisRowData.roleList;
    	for(var i=0;i<roleArray.length;i++){
    		if(columnproperties.text==roleArray[i].viewRoleName){
    			//修改不能显示为OS信息
    			if(roleArray[i].roleNum==0){
    				return "OS";
    			}else{
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
    
    //加载集次编号信息
    viewColumn = function(columnfield, value, columnproperties, rowdata){
		var seriesNoAndViewNo =rowdata.seriesNo+"-"+rowdata.viewNo;
    	return "<span style='color:#52b0cc;' class='bold'>" + seriesNoAndViewNo + "</span>";
    };
    
    //加载场次编号
    viewNoColumn = function(columnfield, value, columnproperties, rowdata) {
        var viewNo =rowdata.viewNo;
        return "<span style='color:#52b0cc;' class='bold'>" + viewNo + "</span>";
    };
    
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
    	var specialRemindText= rowdata.specialRemind;
    	if(specialRemindText==null){
    		specialRemindText="";
    	}
    	return "<span title='"+ specialRemindText +"'>" + specialRemindText + "<span>";
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
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载次场景信息
    minorViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.minorView){
    		text=rowdata.minorView;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载三级场景信息
    thirdLevelViewColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.thirdLevelView){
    		text=rowdata.thirdLevelView;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载主要内容信息
    mainContentColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.mainContent){
    		text=rowdata.mainContent;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
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
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载主要演员信息
    massRoleListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.massRoleList){
    		text=rowdata.massRoleList;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载道具信息
    propsListColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.propsList){
    		text=rowdata.propsList;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载服装信息
    clothesNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.clothesName){
    		text=rowdata.clothesName;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //加载化妆信息
    makeupNameColumn = function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.makeupName){
    		text=rowdata.makeupName;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
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
    	return "<span title='"+ text +"'>" + text + "<span>";
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
    	var shootStatusText = shootStatusMap.get(rowdata.shootStatus);
    	if(shootStatusText==null){
    		shootStatusText="";
    	}
    	return shootStatusText;
    };
    
    //加载广告信息
    advertNameColumn=function(columnfield, value, columnproperties, rowdata){
    	var text="";
    	if(rowdata.advertName){
    		text=rowdata.advertName;
    	}
    	return "<span title='"+ text +"'>" + text + "<span>";
    };
    
    //设置每行数据的格式
    gridColumns=[{ width: '40px',isCheckbox:true }];
    
    //电影类型的剧组不显示集次
    if (crewType == Constants.CrewType.movie || crewType == 3) {
      gridColumns.push({ text: '场次', cellsrenderer: viewNoColumn, width: '65px' ,pinned: true});
    } else {
      gridColumns.push({ text: '集-场', cellsrenderer: viewColumn, width: '65px' ,pinned: true});
    }
    //加载数据列,拼接场景列表的表头信息
    gridColumns.push(
			{ text: '特殊提醒', cellsrenderer: seasonColumn, width: '60px'},
            { text: '气氛',cellsrenderer: atmosphere, width: '40px', style:'padding-left: 0px;text-align:center;'},
            { text: '内外景',cellsrenderer:siteColumn, width: '40px', style:'padding-left: 0px;text-align:center;'},
            { text: '拍摄地点', cellsrenderer: shootLocationColumn, width: '120px' },
            { text: '主场景',cellsrenderer:majorViewColumn, width: '120px' },
            { text: '次场景',cellsrenderer:minorViewColumn , width: '120px' },
            { text: '三级场景',cellsrenderer: thirdLevelViewColumn, width: '120px' },
            { text: '主要内容',cellsrenderer: mainContentColumn, width: '120px' },
            { text: '页数',cellsrenderer: pageCountColumn, width: '40px'}
          );
    //暂时显示所有的主演信息列表
   	if(false){
   		//此处的木得是只显示查询的主演角色列表信息,但是目前要求是显示所有的主演数据
   		//此处做处理动态列
       var showRoleArray = filter.roles.split(",");
       var showRoleMap=new HashMap();
       showRoleMap.clear();
       //要显示的角色
       for(var i=0;i<showRoleArray.length;i++){
          showRoleMap.put(showRoleArray[i],showRoleArray[i]);
       }
       //循环所有角色，值保留要显示的角色
       for(var i =0;i<roleArray.length;i++){
       	
           if(showRoleMap.get(roleArray[i].value)!=null&&(typeof(filter.searchMode)=="undefined"||filter.searchMode!="2")){
           	//查询方式为必须同时出现或者出现即可
           	gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '50px',isRoleColumn:true });
           }else if(showRoleMap.get(roleArray[i].value)==null&&typeof(filter.searchMode)!="undefined"&&filter.searchMode=="2"){
           	//查询方式为必须不可同时出现
           	gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '50px',isRoleColumn:true });
           }
       }
   }else{
   	//没有过滤条件时，显示所有列
   	for(var i = 0; i<roleArray.length; i++){
           gridColumns.push({ text: roleArray[i].text,datafield: roleArray[i].value, cellsrenderer: roleColumn, width: '20px',isRoleColumn:true });
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
	
	//生成表格头部查询条件
	rendertoolbar = function (toolbar) {
    	if($("#searchDIV")[0]){
    		return;
    	}
        var container = $("<div style='margin: 5px;' id='searchDIV'></div>");
        //生成头部查询条件
        var html="";
        html += "<h6 style= 'font-size:14px;'> " + viewRoleName + " > 角色场景表   ";
		/*if(hasExportRoleAuth){
			html+="<a class='exportImg' style='line-height:31px;text-decoration:none; height:31px; ' title='导出' href='javascript:downloadExcel()' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>";
		}*/
        html+="<a class='rolecene'></a></h6>";
        
        container.append(html);
        toolbar.append(container);
        
	};
	//开始加载表格数据
	grid = new SimpleGrid("jqxgrid","/viewManager/loadViewList",100,0,gridColumns,filter,"resultList",rendertoolbar);
	grid.loadTable();
}

/**
 * 
 * 表格对象
 * @param tableId
 * @param url
 * @param pageSize
 * @param pageNo
 * @param columns
 * @param filter
 * @param root
 */
function SimpleGrid(tableId,url,pagesize,pagenum,columns,filter,root,rendertoolbar){
	
	var $this = this;
	
	//结果记录
	this.tableId=tableId;
	this.source= {columns:columns};
	this.url=url;
	this.page={pagesize:pagesize,pagenum:pagenum};
	this.columns=columns;
	this.filter=filter;
	this.root=root;
	this.rendertoolbar=rendertoolbar;
	
	//是否加载统计
	this.summaryFlag=true;
	
	this.isRowClick=true;
	
	this.setFilter=function(filter){
		this.filter=filter;
	};
	
	//当第一次操作的请求还没有结束时, 第二次操作的请求发起了, 那么问题就来了. 全局标识是否应该继续发送请求
	this.continueAjaxRecords = false;
	
	//异步加载反馈 //需要一次握手之后才能继续
	this.handshake = true;
	
	//动态加载表格数据
	this.getAjaxRecords = function(callback, filter){
		
		filter.pagenum = filter.pagenum +1;
		$.ajax({
			url:$this.url,
			type:"post",
			dataType:"json",
			data: filter,
			success:function(data){
				
				$this.source.data = $this.source.data.concat(data.result[root]);
				
				$this.loadGridContent(data.result[root]);
				
				//显示第200条
				if(data.result.pageNo == 2){
					
					$("#tablebody tbody:last").removeClass("hidden-tbody");
				}
				
				//$("#checkedAll").prop("disabled",true);
				//判断是否还存在异步数据, 没有的话就开启全选按钮
				if($this.continueAjaxRecords && data.result.pageNo != data.result.pageCount){
					
					callback(callback, filter);
					
				}else{
					
					//$("#checkedAll").prop("disabled",false);
					$this.handshake = true;
				}
			}
		});
	};
	
	//获取记录数据
	this.getRecords = function(){
		
		this.filter.pagesize = this.page.pagesize;
		this.filter.pagenum = this.page.pagenum;
		
		$.ajax({
			url:this.url,
			type:"post",
			dataType:"json",
			data:this.filter,
			success:function(data){
				
				if(!root){
					throw Exception("root不可为空");
				}
				
				var total = data.result.total;
				var pageCount = data.result.pageCount;
				var records = data.result[root];
				
				if (total == 0 && !filter.fromAdvance) {
					popupPromptBox("提示", "当前还未上传剧本，是否现在上传？", function() {
						$("#uploadScenarioWindow").jqxWindow("open");
					});
				}
				
				//为表格中page属性赋值
				$this.page.total = total;
				
				//设置分页数据
				if(($this.page.pagenum*$this.page.pagesize+parseInt($this.page.pagesize))>total){
					
					if($this.page.pagenum*$this.page.pagesize==0){
						$this.page.start=1;
					}else{
						$this.page.start=$this.page.pagenum*$this.page.pagesize;
					}
					$this.page.end=total;
					
				}else if($this.page.pagenum==0){
					
					$this.page.start=1;
					$this.page.end=$this.page.pagesize;
				}else{
					
					$this.page.start=$this.page.pagenum*$this.page.pagesize;
					$this.page.end=$this.page.pagenum*$this.page.pagesize+parseInt($this.page.pagesize);
				}
				
				$this.page.pageCount = pageCount;
				$this.source.data = records;
				$this.loadGridContent(records);
				
				//绑定事件
				$this.initTableEvent();
				
				//显示第一个100条数据
				$("#tablebody tbody").removeClass("hidden-tbody");
				
				if($this.page.pageCount > 1){
					$this.continueAjaxRecords = true;
					$this.handshake = false;
					
					//在所有的数据没有加载完之前, 全选是禁用的
					//$("#checkedAll").prop("disabled",true);
					$this.getAjaxRecords($this.getAjaxRecords, $this.filter);
				}
			}
		});
	};
	
	//加载表格
	this.loadTable = function(){
		
		//此处创建的是表格的框架，还不带有任何数据
		this.createTable();
		
		//此处获取数据的方式为异步，获取数据后，调用填充数据方法把表格数据以及分页信息更新到页面上
		this.getRecords();
		
		//重置文本框的高度
		resizeViewGird();
		
	};
	
	//兼容行index 但是要保证顺序调用
	var _tbodyIndex = 0;
	
	//创建表格html
	this.createTable = function (){
		
		//加载统计
		if(this.summaryFlag){
			this.loadSummary();
		}
		
		this.summaryFlag = true;
		
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tbodyIndex = 0;
		
		_tableObj.append('<div class="t_i_h" id="hh"><div class="ee"><table cellpadding="0" cellspacing="0" border="0">'+
				'<thead><tr id="tableHead"></tr></thead></table></div></div>');
		
		var _head = _tableObj.find("#tableHead");
		
		//所有列
		var columns = this.source.columns;
		
		for(var i=0; i<columns.length; i++){
			if(columns[i].isCheckbox){
				//_head.append('<td width="15px" class="bold"></td>');
			
			}else{
				
				if(columns[i].style){
					
					_head.append('<td width="50px" style="'+columns[i].style+'" class="bold"><p style="width:50px">'+columns[i].text+'</p></td>');
				}else{
					
					_head.append('<td width="'+columns[i].width+'" class="bold"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
				}
			}
		}
		
		//滚动条预留列
		_head.append('<td><p style="width:6px;"></p></td>');	
		
		//表格主体部分
		_tableObj.append('<div class="cc" id="cc" onscroll="aa()"><div id="_table_doc"><table id="tablebody" cellpadding="0" cellspacing="0" border="0"></table></div></div>');
		
		if(this.rendertoolbar){
			this.rendertoolbar($("#rendertoolbar"));
		}
		
		
	};
	
	//加载表格主体部分
	this.loadGridContent = function(tableData) {
		
		var _this = this;
		var _tableObj = $("#tablebody");
		
		row_array = ['<tbody class="hidden-tbody" >'];
		
		for(var i=0, len = tableData.length;i<len;i++){
			
			row_array.push(this.createRow(null, tableData[i], (_tbodyIndex*100) + i));
		}
		
		row_array.push('</tbody>');
		
		_tableObj.append(row_array.join(''));
		
		_tbodyIndex++;
		
		var tbody = _tableObj.find("tbody:last");
		
		//加入选中Id
		tbody.on('click', ':checkbox', function(e){
			
			//$("#_already_selected").html($("#tablebody :checked").length);
			
			//判断是否全选
			isCheckAll();
			e.stopPropagation();
			
		}).on('click', '>tr', function(){
			
			if(_this.isRowClick){
				$(this).find(":checkbox").trigger("click");
			}else{
				_this.isRowClick=true;
			}
		});
		
	};
	
	//全不选
	this.unSelectedAll=function(){
		
		$("#"+this.tableId).find("tbody :checkbox").prop("checked",false);
		//$("#_already_selected").html($("#tablebody :checked").length);
	};
	
	this.initTableEvent = function(){
		
		var _tableObj = $("#"+this.tableId);
		
		var pageCount = this.page.pageCount;
		var total = this.page.total;
		
		//checkbox全选
		$("#checkedAll").click(function(){
			
			if(this.checked){
				_tableObj.find("tbody :checkbox").prop("checked",true);
			}else{
				_tableObj.find("tbody :checkbox").prop("checked",false);
			}
			
			//$("#_already_selected").html($("#tablebody :checked").length);
		});
		
		//一个body的高度
		var bodyheight = _tableObj.find("tbody:eq(0)").height();
		
		//设置文档的高度
		//$("#_table_doc").css("height", bodyheight/100 * total);
		
		//拉动滚动条产生的重复事件
		var timeoutnum = 0;
		
		//记录那个body在窗口显示
		var preId = 0;
		
		$("#cc").scroll(function(evt){
			
			//清除重复的事件
			window.clearTimeout(timeoutnum);
			
			timeoutnum = setTimeout(function(){
			
				var target = $(evt.currentTarget);
				
				var scollTop = target.scrollTop();
				
				var id = (scollTop - scollTop % bodyheight)/bodyheight;
				
				//如果相同就不更新数据
				if(preId != id){
					
					preId = id;
					
					//解决滚动条长距离拉动的问题
					_tableObj.find("tbody").addClass("hidden-tbody");
					
					//如果在异步加载之前滚动, 判断异步加载是否完成
					if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
						
						(function pollingBody(){
							
							setTimeout(function(){
								
								if(_tableObj.find("tbody:eq("+(pageCount == id+1 ? id : id+1 )+")").length == 0){
									
									pollingBody();
								}else{
									
									_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
									_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
									
									$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
								}
								
							},1000);
						}).call(this);
						
					}else{
						
						_tableObj.find("tbody:eq("+(id)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id - 1)+")").removeClass("hidden-tbody");
						_tableObj.find("tbody:eq("+(id + 1)+")").removeClass("hidden-tbody");
						
						$("#_table_doc").css("padding-top", id == 0 ? "" : bodyheight * (id - 1));
					}
				}
				
			},0);
		});
	};
	
	//分页信息
	this.loadPage = function() {
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find(".pageturn").remove();
		
		//分页部分
		var pageHTML = '<div class="pageturn"><ul class="page">'
		+ '<li>总场数：'+this.page.total+'</li>'
		+ '<li>每页显示场数：<div style="float: right;margin-top: 4px;" id="pageSize"></div></li>'
		+ '<li>当前场数：'+this.page.start+'-'+this.page.end+'</li>'
		+ '<li>总页数：'+ this.page.pageCount +'</li>'
		+ '<li>当前页数：<input class="search_text" type="text" onkeyup="lmaNumber(this, ' + this.page.pageCount + ');" id="pagenum" value="'+(parseInt(this.page.pagenum)+1)+'" /><input id="oldPageNum" type="hidden"  value="'+(parseInt(this.page.pagenum)+1)+'"></li>'
		+ '<li><input class="previous_button"  title="上一场" type="button" id="previous_button"  />&nbsp;<input class="next_button" title="下一场" type="button" id="next_button" /></li>';
		+ '</ul></div>';
		
		_tableObj.append(pageHTML);
		var source = [
	                    {text:"100",value:100},
	                    {text:"500",value:500},
	                    {text:"全部",value:99999999}];
		
		$("#pageSize").jqxDropDownList({theme:theme, enableBrowserBoundsDetection: true, source: source,autoDropDownHeight: true, displayMember: "text", valueMember: "value", width: '50', height: '18'});
		
		var selectPageSizeIndex=0;
		if(this.page.pagesize==100){
			selectPageSizeIndex=0;
		}else if(this.page.pagesize==500){
			selectPageSizeIndex=1;
		}else if(this.page.pagesize==99999999){
			selectPageSizeIndex=2;
		}
		
		$("#pageSize").jqxDropDownList("selectIndex",selectPageSizeIndex);
		//页长改变事件绑定
		$("#pageSize").bind("change",{grid:this,pageNo:0, pageCount: this.page.pageCount},pageChanged);
		//上一页
		$("#previous_button").bind("click",{grid:this,pageNo:this.page.pagenum-1, pageCount: this.page.pageCount},pageChanged);
		//下一页
		$("#next_button").bind("click",{grid:this,pageNo:this.page.pagenum+1,  pageCount: this.page.pageCount},pageChanged);
		//指定页
		$("#pagenum").bind("change",{grid:this,pageNo:null,  pageCount: this.page.pageCount},pageChanged);
	};
	
	//统计信息
	this.loadSummary = function(){
		var _this=this;
		$.ajax({
			url:"/viewManager/loadSummary",
			data:_this.filter,
			dataType:"json",
			type:"post",
			async: true,
				success:function(data){
					
					var viewStatistics = data.viewStatistics;
					//总场数
					var statisticsViewCount = viewStatistics.statisticsViewCount;
					//总页数
					var statisticsPageCount = viewStatistics.statisticsPageCount;
					//状态分类统计
					var statisticsShootStatus = viewStatistics.statisticsShootStatus;
					//内外景统计
					var statisticsSite = viewStatistics.statisticsSite;
					//场景总数
					var statisticsHTML = "统计：共"+statisticsViewCount[0].funResult+"场";
					
					statisticsHTML+="/"+statisticsPageCount[0].funResult.toFixed(1)+"页";
					
					//状态统计
					var shootStatusKeys = shootStatusMap.keys();
					for(var i=0;i<shootStatusKeys.length;i++){
						var shootStatusKey = shootStatusKeys[i];
						for(var j = 0;j<statisticsShootStatus.length;j++){
							if(shootStatusKey==statisticsShootStatus[j].shootStatus){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+statisticsShootStatus[j].funResult+"场 ";
								break;
							}else if(j==statisticsShootStatus.length-1){
								statisticsHTML+="|  "+shootStatusMap.get(shootStatusKey)+"0场 ";
							}
						}
					}
					
					//内外景统计
					var siteKeys = siteMap.keys();// |  &nbsp;气氛2场 &nbsp;
					for(var i=0;i<siteKeys.length;i++){
						var siteKey = siteKeys[i];
						for(var j = 0;j<statisticsSite.length;j++){
							if(siteKey==statisticsSite[j].site){
								statisticsHTML+="|  "+siteMap.get(siteKey)+statisticsSite[j].funResult+"场 ";
								break;
							}else if(j==statisticsSite.length-1){
								statisticsHTML+="|  "+siteMap.get(siteKey)+"0场 ";
							}
						}
					}
					
					$("#statistics").text(statisticsHTML);
				}
		});
	};
	
	//获取选中行的Id
	this.getSelectIds = function(){
		
		var result = "";
		
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.find("tbody :checkbox:checked").each(function(index){
			
			if(index == 0){
				
				result = $(this).attr("id");
			}else{
				result += ","+$(this).attr("id");
			}
		});
		
		return result;
	};
	
	//跳转到页面
	this.goToPage = function(pageNo){
		
		this.continueAjaxRecords = false;
		
		var inter = null;
		
		//判断上一次的操作的异步加载是否结束, 如果没有结束, 在下一次告诉他结束异步加载, 并且反馈回来.
		inter = setInterval(function(){
			
			if($this.handshake){
				
				clearInterval(inter);
				
				$this.summaryFlag = true;
				
				var pageSize = $("#pageSize").val();
				
				if(pageNo==null){
					
					pageNo=$("#pagenum").val()-1;
				}
				
				$this.page.pagenum=pageNo;
				
				if(pageSize){
					$this.page.pagesize=pageSize;
				}
				
				$this.loadTable();
			}
			
		},50);
	};
	
	this.refresh = function(){
		
		this.loadTable();
	};
	
	this.getRowData = function(index){
		
		return this.source.data[index];
	};
	
	this.getRowIndex = function(viewId){
		
		if(!viewId){
			return null;
		}
		
		return parseInt($("#"+viewId).attr("index"));
	};
	
	this.selectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	this.unSelectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("click");
	};
	
	//获取选中行的行号
	this.getSelectedIndexs = function() {
		
		var resultArray = new Array();
		
		var _tBody = $(".cc");
		_tBody.find(":checkbox").each(function(event) {
			if ($(this).prop("checked")) {
				resultArray.push($(this).attr("index"));
			}
		});
		
		return resultArray;
	};
	
	//更新单行数据
	this.updaterowdata = function(rowIndex, rowData) {
		
		var _tBody = $(".cc");
		
		var _row = this.createRow(_tBody, rowData, rowIndex);
		var $row = $(_row);
		
		//然后替换表格中指定的行
		_tBody.find("tr[rowid="+ rowIndex +"]").replaceWith($row);
		
		//$("#_already_selected").html($("#tablebody :checked").length);
		$("#checkedAll").prop("checked",false);
	};

	//更新一行中的一列数据 createCell方法还未完成，cellIndex获取方法也未完成
	this.updatecell = function(rowIndex, cellIndex, cellData) {
		var _tBody = $(".cc");
		var _row = _tBody.find(" tr[rowid="+ rowIndex +"]");

		//生成表格的列
		var _cell = this.createCell();
		
		//替换掉表格中指定的列
		_row.find("td[cellid="+ cellIndex +"]").replaceWith(_cell);
	};
	
	//生成表格的一行数据
	this.createRow = function(_tBody, rowData, rowid) {
		
		var style = "";
		
		if(rowData.shootStatus != ""){
			
			style = " style='background-color:"+getColor(rowData.shootStatus)+";' ";
		}
		
		var _row =["<tr rowId='"+rowid+"'" + style + " >"];
		
		for(var j=1;j<columns.length;j++){
			
			if(columns[j].isCheckbox){
				_row.push('<td cellid="'+ j +'" class="bold"></td>');
			}else{
				if(columns[j].cellsrenderer){
					_row.push('<td cellid="'+ j +'"><p style="height:14px;width:' + columns[j].width + ';overflow:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</p></td>');
				}else{
					_row.push('<td cellid="'+ j +'"><p title="'+rowData[columns[j].filedName]+'" style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+rowData[columns[j].filedName]+'</p></td>');
				}
			}
		}
		_row.push("</tr>");
		
		return _row.join('');
	};
}

//导出场景表
function downloadExcel(){
	/*显示加载中*/
	var clientWidth=window.screen.availWidth;
	//获取浏览器页面可见高度和宽度
    var _PageHeight = document.documentElement.clientHeight;
    //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
    var _LoadingTop = _PageHeight > 230 ? (_PageHeight - 330) / 2 : 0,
        _LoadingLeft = clientWidth > 240 ? (clientWidth - 640) / 2 : 0;
    //在页面未加载完毕之前显示的loading Html自定义内容
    var _LoadingHtml = $("#loadingDiv");
    $(".opacityAll").css({opacity:0,width:clientWidth,height:_PageHeight}).show();
    //呈现loading效果
    _LoadingHtml.css({top:_LoadingTop,left:_LoadingLeft});
    _LoadingHtml.show();
    
    var fileAddress ="";
    var fileName = "";
	$.ajax({
		 url:"/viewManager/exportExcel",
         data:{roles:filter.roles},
         dataType:"json",
         type:"post",
         success:function(response){
        	 if (response.success) {
        		 _LoadingHtml.hide();
        		 $(".opacityAll").hide();
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
             form.submit();
         }
	});
	
	
}

//获取拍摄状态的颜色
function getColor(shootStatus){
	
	if(shootStatus==""){
		return "#FFFFFF";
	}
	var divColor=viewStatusColor.get(shootStatus);
	return divColor;
}

//重置文本框的高度
function resizeViewGird() {
    var tableheadHeight = $("#tableHead").height();
    $(".cc").css("height", window.innerHeight - 80 - tableheadHeight);
}

//修改表格调用
function aa() {
	var b = document.getElementById("cc").scrollLeft;
	document.getElementById("hh").scrollLeft = b;
}