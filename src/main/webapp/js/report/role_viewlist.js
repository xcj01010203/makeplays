//lma

/**
 * 表格对象
 * @param tableId
 * @param url
 * @param pageSize
 * @param pageNo
 * @param columns
 * @param filter
 * @param root
 */
function SimpleGrid(tableId,url,pagesize,pagenum,columns,filter,root,rendertoolbar,viewRoleIds,idIndex){
	//结果记录
	this.tableId=tableId;
	this.source={columns:columns};
	this.url=url;
	this.page={pagesize:pagesize,pagenum:pagenum};
	this.columns=columns;
	this.filter=filter;
	this.root=root;
	this.rendertoolbar=rendertoolbar;
	//是否加载统计
	this.summaryFlag=true;
	this.selectedIds="";
	this.isRowClick=true;
	
	this.setFilter=function(filter){
		this.filter=filter;
	};
	
	//获取记录数据
	this.getRecords=function(){
		var total=0;
		var pageCount=0;
		var records=null;
		this.filter.pagesize=this.page.pagesize;
		this.filter.pagenum=this.page.pagenum;
		/*var addressData="";*/
		$.ajax({
			url:this.url,
			type:"post",
			dataType:"json",
			data:{pagesize:this.filter.pagesize,pagenum:this.filter.pagenum,viewRoleIds:viewRoleIds},//this.filter,
			async:false,
			success:function(data){
				if(!root){
					throw Exception("root不可为空");
				}
				total=data.result.total;
				pageCount=data.result.pageCount;
				records=data.result[root];
			}
		});
		this.page.total=total;
		if((this.page.pagenum*this.page.pagesize+parseInt(this.page.pagesize))>total){
			if(this.page.pagenum*this.page.pagesize==0){
				this.page.start=1;
			}else{
				this.page.start=this.page.pagenum*this.page.pagesize;
			}
			this.page.end=total;
		}else if(this.page.pagenum==0){
			this.page.start=1;
			this.page.end=this.page.pagesize;
		}else{
			this.page.start=this.page.pagenum*this.page.pagesize;
			this.page.end=this.page.pagenum*this.page.pagesize+parseInt(this.page.pagesize);
		}
		/*this.addressData=addressData;*/
		this.page.pageCount=pageCount;
		this.source.data=records;
		return this.source.data;
		
	};
	
	//加载表格
	this.loadTable=function(){
		showSuccessMessage("正在加载...");
		this.getRecords();
		this.createTable();
		resizeViewGird();
	};
	
	//创建表格html
	this.createTable = function (){
		var _this=this;
		//表格对象
		var _tableObj = $("#"+this.tableId);
		
		_tableObj.children().remove();
		
		_tableObj.append('<div class="t_i_h" id="hh'+idIndex+'"><div class="ee"><table cellpadding="0" cellspacing="0" border="0">'+
				'<thead><tr id="tableHead'+idIndex+'"></tr></thead></table></div></div>');
		//表格头对象
		var _head=_tableObj.find("#tableHead"+idIndex);
		//所有列
		var columns = this.source.columns;
		for(var i=0;i<columns.length;i++){
			if(columns[i].isCheckbox){
				//_head.append('<td width="15px" class="bold"><input type="checkbox" id="checkedAll" class="line-height"/></td>');
			}else{
				_head.append('<td width="'+columns[i].width+'" class="bold"><p style="width:'+ columns[i].width +'">'+columns[i].text+'</p></td>');
			}
		}
		_head.append('<td><p style="width:6px;"></p></td>');	//滚动条预留列
		
		_tableObj.append('<div class="cc" id="cc'+idIndex+'" onscroll="aa('+idIndex+')" ><table cellpadding="0"   cellspacing="0" border="0"><tbody id="tableBody'+idIndex+'"></tbody></table></div>');
		//表格主体   
		var _tBody=_tableObj.find("#tableBody"+idIndex);
		
		//所有数据
		var tableData = this.source.data;
		for(var i=0;i<tableData.length;i++){
			var rowData=tableData[i];
			//行
			var _row = $("<tr rowId='"+i+idIndex+"' style='background-color:"+getColor(rowData.shootStatus)+";' ></tr>");
			//行点击事件
			_row.click(function(){
				//$(this).toggleClass("mouse_click");
				if(_this.isRowClick){
					if($(this).attr("class")&&$(this).attr("class").indexOf("mouse_click")>-1){
						//$(this).find(":checkbox").prop("checked",false);
						$(this).find(":checkbox").trigger("unChecked");
					}else{
						//$(this).find(":checkbox").prop("checked",true);
						$(this).find(":checkbox").trigger("checked");
					}
				}else{
					_this.isRowClick=true;
				}
			});
			
			for(var j=0;j<columns.length;j++){
				//单元格
				if(columns[j].isCheckbox){
					_row.append('<td width="15px" class="bold"><input type="checkbox" id="'+rowData.viewId+idIndex+'" index="'+i+'" class="line-height"/></td>');
				}else{
					if(columns[j].cellsrenderer){
						_row.append('<td width="'+columns[j].width+'" class="bold"><p style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+columns[j].cellsrenderer(columns[j].filedName,rowData[columns[j].filedName],columns[j],rowData)+'</p></td>');
					}else{
						_row.appaddNoticeViewIdsend('<td width="'+columns[j].width+'" class="bold"><p style="height:14px;width:' + columns[j].width + ';overflow-y:hidden;">'+rowData[columns[j].filedName]+'</p></td>');
					}
				}
			}
			_tBody.append(_row);
		}
		
		//分页部分
		var pageHTML = '<div class="pageturn"><ul class="page"><li>页数：<input class="search_text" type="text" onkeyup="lmaNumber(this, ' + this.page.pageCount +','+idIndex+');" id="pagenum'+idIndex+'" value="'+(parseInt(this.page.pagenum)+1)+'" /><input id="oldPageNum'+idIndex+'" type="hidden"  value="'+(parseInt(this.page.pagenum)+1)+'"></li>'+
		'<li style="">每页显示条数：<div style="float: right;margin-top: -2px;" id="pageSize'+idIndex+'"></div></li><li>'+
		this.page.start+'-'+this.page.end+' 总条数：'+this.page.total+'</li>'+
		'<li id="lmabutton"><input class="previous_button" type="button" id="previous_button'+idIndex+'"  /></li>&nbsp;<li id="lmaTwoButton"><input class="next_button"  type="button" id="next_button'+idIndex+'" /></li></ul></div>';
		
		_tableObj.append(pageHTML);
		var source = [
	                    {text:"50",value:50},
	                    {text:"100",value:100},
	                    {text:"全部",value:99999999}];
		$("#pageSize"+idIndex).jqxDropDownList({theme:theme, source: source,autoDropDownHeight: true, displayMember: "text", valueMember: "value", selectedIndex: 0, width: '50', height: '25'});
		var selectPageSizeIndex=0;
		if(this.page.pagesize==50){
			selectPageSizeIndex=0;
		}else if(this.page.pagesize==100){
			selectPageSizeIndex=1;
		}else if(this.page.pagesize==99999999){
			selectPageSizeIndex=2;
		}
		$("#pageSize"+idIndex).jqxDropDownList("selectIndex",selectPageSizeIndex);
		//页长改变事件绑定
		$("#pageSize"+idIndex).bind("change",{grid:this,pageNo:0,pageCount: this.page.pageCount},pageChanged);
		//上一页
		$("#previous_button"+idIndex).bind("click",{grid:this,pageNo:this.page.pagenum-1,pageCount: this.page.pageCount},pageChanged);
		//下一页
		$("#next_button"+idIndex).bind("click",{grid:this,pageNo:this.page.pagenum+1,pageCount: this.page.pageCount},pageChanged);
		//指定页
		$("#pagenum"+idIndex).bind("change",{grid:this,pageNo:null,pageCount: this.page.pageCount},pageChanged);
		
		
		if(this.rendertoolbar){
			this.rendertoolbar($("#rendertoolbar"+idIndex));
		}
		//加载统计
		if(this.summaryFlag){
			this.loadSummary();
		}
		this.summaryFlag=true;
	};
	
	this.loadSummary=function(){
		$.ajax({
	        url:  "/lmaroleCrewReportManager/getByAddress",  
	        type: 'POST',
	        data: {
	        	roleName:viewRoleIds
	         },
	        async: false,
	        dataType: 'JSON',
	        success:function(data){
				if(data!="" && data!=null)
				{
					var strtite="统计：共"+data.totaView+" | 场景数："+data.mainCount;//其中：
					if(data.wuPage!=undefined){
						strtite+=" | "+data.wuPage+" | "+data.wenPage;
						if(data.wenWuPage!=undefined){
							strtite+=" | "+data.wenWuPage;
						}
					}
					//strtite+="&nbsp;&nbsp;";
					if(data.byScene != undefined){
						strtite+=" | 日："+data.byScene; 
					}
					if(data.nineScene !=undefined){
						strtite+=" | 夜："+data.nineScene;
					}
					if(data.nineByScene !=undefined){
						strtite+=" | 日夜："+data.nineByScene;
					}
					if(data.waiScene !=undefined){
						strtite+=" | 外："+data.waiScene;
					}
					if(data.neiScene !=undefined){
						strtite+=" | 内："+data.neiScene;
					}
					if(data.neiWaiScene !=undefined){
						strtite+=" | 内外："+data.neiWaiScene;
					}
					$("#statistics"+idIndex).text(strtite);
				}
	        }
	     });
	};
	
	//获取选中行的Id
	this.getSelectIds=function(){
		
		if(this.selectedIds){
			return this.selectedIds;
		}else{
			return "";
		}
	};
	
	//全选
	this.selectedAll=function(){
		$("#checkedAll").trigger("checked");
	};
	//全不选
	this.unSelectedAll=function(){
		$("#checkedAll").trigger("unChecked");
	};
	
	//跳转到页面
	this.goToPage=function(pageNo){
		this.summaryFlag=false;
		var pageSize = $("#pageSize"+idIndex).val();
		if(pageNo==null){
			pageNo=$("#pagenum"+idIndex).val()-1;
		}
		
		this.page.pagenum=pageNo;
		if(pageSize){
			this.page.pagesize=pageSize;
		}
		this.loadTable();
	};
	
	this.refresh=function(){
		this.loadTable();
	};
	
	this.getRowData=function(index){
		return this.source.data[index];
	};
	
	this.getRowIndex=function(viewId){
		
		if(!viewId){
			return null;
		}
		
		return parseInt($("#"+viewId).attr("index"));
	};
	
	this.selectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("checked");
		//$("tr[rowId='"+index+"']").trigger("click");
		//this.isRowClick=true;
	};
	
	this.unSelectRow=function(index){
		//不执行行点击事件
		$(":checkbox[index='"+index+"']").trigger("unChecked");
		//$("tr[rowId='"+index+"']").trigger("click");
		//this.isRowClick=true;
	};
	
}
//lma
function lmaNumber(obj,pageCount,inId){
	//检查是否是非数字值  
    if (isNaN(obj.value)) {  
    	showErrorMessage("只能输入整数！");
    	obj.value = $("#oldPageNum"+inId).val();  
    }
    if(obj.value==0){
    	showErrorMessage("整数必须大于0！");
    	obj.value = $("#oldPageNum"+inId).val(); 
    }
    if (obj.value > pageCount) {
    	showErrorMessage("已超过总页数");
    	obj.value = $("#oldPageNum"+inId).val(); 
    }
    if (obj != null) {  
        //检查小数点后是否对于两位http://blog.csdn.net/shanzhizi  
        if (obj.value.toString().indexOf(".") != -1) {  
        	showErrorMessage("只能输入整数！");  
        	obj.value = $("#oldPageNum"+inId).val(); 
        }  
    }  
}

//调用翻页
function pageChanged(event){
	if(event.data.pageNo<0){
		return false;
	}
	if (event.data.pageNo >= event.data.pageCount) {
		return false;
	}
	event.data.grid.goToPage(event.data.pageNo);
};



/***************************屏幕遮幕*****************************/
function loadDiv(message) {
    var sub = "<div style='z-index: 9999999; margin-left: -66px; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;'>"
            +"<div style='float: left; overflow: hidden; width: 32px; height: 32px;' class='jqx-grid-load'/>" 
            + "<span style='margin-top: 10px; float: left; display: block; margin-left: 5px;' >" + message + "</span>" 
            + "</div></div>";
    var div = "<div id='_layer_'> " 
            + "<div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 99999; position: absolute;"
            + "left: 0; top: 0; overflow: hidden; display: none'>" 
            + "</div>" 
            + "<div id='_wait_' style='z-index: 9999999; position: absolute; width:430px;height:218px; display: none'  >" 
            + "<center>" 
            + sub
            + "</center>" 
            + "</div>" 
            + "</div>";
    return div;
}
function LayerShow(message) {
    var addDiv = loadDiv(message);
    
    var element = $(addDiv).appendTo(document.body);
    $(window).resize(Position);
    var deHeight = $(document).height();
    var deWidth = $(document).width();
    Position();
    $("#_MaskLayer_").show();
    $("#_wait_").show();
}
function Position() {
    $("#_MaskLayer_").width($(document).width());
    var deHeight = $(window).height();
    var deWidth = $(window).width();
    $("#_wait_").css({
        left : (deWidth - $("#_wait_").width()) / 2 + "px",
        top : (deHeight - $("#_wait_").height()) / 2 + "px"
    });
}
function LayerHide() {
    $("#_MaskLayer_").hide();
    $("#_wait_").hide();
    del();
}
function del() {
    var delDiv = document.getElementById("_layer_");
    delDiv.parentNode.removeChild(delDiv)
};
/***************************屏幕遮幕*****************************/
