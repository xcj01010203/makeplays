$(document).ready(function(){
	//默认选中分场表
	defaultSelect();
	//初始化表格分页
	initTablePage();
	
	
	//隐藏下拉菜单
	$(document).click(function(){
		$("#collScenceDrop").slideUp("fast");
		$("#atmosphereDrop").slideUp("fast");
		$("#scenceDrop").slideUp("fast");
		$("#mainActorDrop").slideUp("fast");
		$("#specialActorDrop").slideUp("fast");
		$("#publicActorDrop").slideUp("fast");
		$("#clothPropDrop").slideUp("fast");
	});
});

//默认选中分场表
function defaultSelect(){
	$("#breakScenceBtn").addClass("on");
	$("#breakScenceBtn").on("click", function(){
		$(this).addClass("on");
		$("#planOneBtn").removeClass("on");
		$("#planTwoBtn").removeClass("on");
	});
	
	$("#planOneBtn").on("click", function(){
		$(this).addClass("on");
		$("#breakScenceBtn").removeClass("on");
		$("#planTwoBtn").removeClass("on");
	});
	
	$("#planTwoBtn").on("click", function(){
		$(this).addClass("on");
		$("#breakScenceBtn").removeClass("on");
		$("#planOneBtn").removeClass("on");
	});
	
	
}


//全选
function selectAll(own){
	
	own = $(own);
	
	if(own.is(":checked")){
		$("table.notice-scence-table input[type=checkbox]").prop("checked", true);
	}else{
		$("table.notice-scence-table input[type=checkbox]").prop("checked", false);
	}
}



//初始化表格分页
function initTablePage(){
	//ajax请求数据
	/*var pageCount = response.totalCount/20;*/
	var pageCount = 10;
	var options = {
		bootstrapMajorVersion: 2,//版本
		currentPage: 1,//当前页数
		countSize: 5,
		totalPages: pageCount,//总页数
		itemTexts: function(type, page, current){
			switch(type){
			case "first":
				return "首页";
			case "prev":
				return "上一页";
			case "next":
				return "下一页";
			case "last":
				return "末页";
			case "page":
				return page;
			}
		},
		//点击事件，用于通过ajax来刷新列表
		onPageClicked: function(event, originalEvent, type, page){
			debugger;
			$.ajax({
				url: '',
				type: 'post',
				data: {page: page, recordCount: 15},//recordCount每页显示条数
				datatype: 'json',
				success: function(response){
					
				}
			});
		}
	};
	$('#tablePage').bootstrapPaginator(options); 
}


//选择集场
function selectCollScence(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #collScenceDrop").css({'left': left-35, 'top': top+own.outerHeight()}).slideToggle("fast");
	ev.stopPropagation();
	$("#collScenceDrop").on("click", function(event){
		event.stopPropagation();
	});
}

//选择气氛
function selectAtmosphere(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #atmosphereDrop").css({'left': left-35, 'top': top+own.outerHeight()}).slideToggle("fast");
	ev.stopPropagation();
		
	
	$("#atmosphereDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
		
}

//选择场景
function selectScence(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #scenceDrop").css({'left': left-35, 'top': top+30}).slideToggle("fast");
	ev.stopPropagation();
	
	$("#scenceDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
}


//选择主要演员
function selectMainActor(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #mainActorDrop").css({'left': left-35, 'top': top+30}).slideToggle("fast");
	ev.stopPropagation();
	
	$("#mainActorDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
}


//选择特约演员
function selectSpecialActor(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #specialActorDrop").css({'left': left-35, 'top': top+30}).slideToggle("fast");
	ev.stopPropagation();
	
	$("#specialActorDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
}

//选择群众演员
function selectPublicActor(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #publicActorDrop").css({'left': left-35, 'top': top+30}).slideToggle("fast");
	ev.stopPropagation();
	
	$("#publicActorDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
}


//选择服化道
function selectClothProp(own, ev){
	own = $(own);
	var left = own.position().left;
	var top = own.position().top;
	$(".notice-scence-table #clothPropDrop").css({'left': left-35, 'top': top+30}).slideToggle("fast");
	ev.stopPropagation();
	
	$("#clothPropDrop").on("click", "a", function(event){
		if($(this).hasClass("on")){
			$(this).removeClass("on");
			
		}else{
			$(this).addClass("on");
		}
		event.stopPropagation();
	});
}