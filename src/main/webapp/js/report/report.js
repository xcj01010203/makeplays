// 折叠统计项
/*page.statis_shoot=function(){	
   /* $('.fold_head').click(function(){
        var _this=$(this);
        if(_this.hasClass('open')){
            _this.removeClass('open').next('.fold_body').hide();
        }else{
        	$('.open').click();
            _this.addClass('open').next('.fold_body').show();
        }
    })
}*/

//根据选择的项对打开的报表进行页面定位
page.statis_location=function(){
	//查询input聚焦展示列表
	$('.search_roleName .role_check_search').click(function(){
		$(this).val('');
		$('.search_roleName ul li').show();
		$('.search_roleName ul').fadeIn();
		
		//modify 
		var $container = $('#con_top');
		if(!$container.find('.J-model-search_roleName').length){
			var model = $('<div class="J-model-search_roleName"></div>');
			model.css({
				'height': window.innerHeight,
				'width':window.innerWidth,
				'position': 'fixed',
				'top': 0,
				'left': 0,
				'z-index': 99999
			}).click(function(){
				$('.search_roleName ul').hide();
				model.hide();
			});
			$('.search_roleName').css('z-index', 999999);
			$container.append(model);
		}else{
			$container.find('.J-model-search_roleName').show();
		}
		
	});
	//查询input右键取消
	if($('.search_roleName .role_check_search')[0]!= undefined){
		$('.search_roleName .role_check_search')[0].oncontextmenu=function(){
			$('.search_roleName .role_check_search').val('');
			$('.search_roleName ul').hide();
			return false;
		}
	}
	
	//对下拉列表进行模糊查询
	$('.role_check_search').off('keyup');
    $('.role_check_search').keyup(function(){
    	var _this = $(this),
    		_ul=$('.search_roleName ul');
    		if(_ul.is(':hidden')){
    			$('.search_roleName ul').fadeIn();
    		}
    		_liAll = _ul.find('li')
    		// 是否有搜索到匹配值
    		,isExist = false;
    	
    	// 去除当前输入内容的前后空格,并按内容里边的空格拆分成数组（多个空格算一个）
    	var _key = $.trim(_this.val()).replace(/\s+/g,' ').split(' ');
    	_liAll.each(function(){
    		var that = $(this);
    		var _val =$.trim(that.text());
    		// 循环_key数组
    		for(var i=0; i<_key.length; i++){
    			if( _val.search( _key[i] ) != -1 ){
    				isExist = true;//已搜索到
        			break;
        		}else{
        			isExist = false;
        		}
    		}
    		if(isExist){
    			that.show();
    		}else{
    			that.hide();
    		}
    		
    	});    	
    });
    
	//页面定位
	$('.search_roleName ul li').click(function(){
		$('.tb_body').scrollTop(0);
		$('.tb_body tr').removeClass('tr_cur');
		$('.role_check_search').val($(this).find('span').html());
		var searchInput=$('.role_check_search').val();
		var _openDt=$('.open');
		var _openTbody=_openDt.next().find('.tb_body');
		_openTbody.find('tbody tr').each(function(){
			var tdVal=$(this).find('td:first div:first');
			if($.trim(tdVal.text())==searchInput){
				var tdy=tdVal.position().top-tdVal.height()-tdVal.height();
				_openTbody.scrollTop(tdy);
				tdVal.parent().parent().addClass('tr_cur');
				$('.search_roleName ul').hide();
			}
		});
	});
}


$(function(){
   // page.statis_shoot();
    page.statis_location();
})