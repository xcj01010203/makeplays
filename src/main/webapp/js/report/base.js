//各页面 功能
var page={};
var scrollBarWidth = 0;
if(navigator.userAgent.toLowerCase().search('windows') != -1){
	scrollBarWidth = 17;
}
//1、重置页面
function resetPage(){
    var _windowHeight=$(window).height()>=600?$(window).height():600
    ,_wrapHeight=_windowHeight-$('#header').height();
    //1-2、左边栏、内容区高度
    $('.wrap,#nav_first,.nav_second').height(_wrapHeight);
    //1-3、设置 html,body 的滚动条是否显示
    if($(window).height()>600){
        $('html').css('overflow-y','hidden');
    }else{
        $('html').css('overflow-y','scroll');
    }
    if($(window).width()>1280){
        $('html').css('overflow-x','hidden');
    }else{
        $('html').css('overflow-x','scroll');
    }
    //1-4、重置表格
    tb_reset();
    //1-5、右侧弹层高度
    if($('body.no_scroll').length>0){
        $('#con_right').height($('#con_box').outerHeight()-10);
    }else{
        $('#con_right').css({height:$('.tb_box').height()-21});
    }
}
//2、重置表格
function tb_reset(){
    //2-1、iframe里的.tb_box
    if( $('#frame_page').length>0){
        if($('.tb_body~.tb_foot').length>0){
            $('.tb_body').css({height:$(window).height()-$('.tb_body~.tb_foot').outerHeight()-14});
            $('.tb_body~.tb_foot').css({top:-30});
            $('.tb_foot').css({top:-33});
        }else{
            $('.tb_body').css({height:$(window).height()-14});
        }
        if($('#frame_page #page_top').length>0){
            $('.tb_body').css({height:$('.tb_body').height()-$('#page_top').outerHeight()})
        }
        $('.tb_body').css({width:$(parent.window.document).find('#scene_popup iframe').width()-14});
        $('.tb_head , .tb_foot').width($('.tb_body').width()-scrollBarWidth);
        return false;
    }
    //2-2、包含#con_box的页面
    if($('body.no_scroll').length>0){
        $('#con_box').css({height:$('#content').height()-$('#con_top').height()-14});
    }else{
        //2-2-1、判断页面是否存在tb_foot ：$('.tb_body~.tb_foot').length>0 代表存在
        if($('.tb_body~.tb_foot').length>0){
            $('.tb_body').css({height:$('#content').height()-$('#con_top').height()-14-$('.tb_body~.tb_foot').outerHeight()});           
            $('.tb_body~.tb_foot').css({top:-$('.tb_head').outerHeight()});
        }else{
            $('.tb_body').css({height:$('#content').height()-$('#con_top').height()});
        }
        //$('.tb_box table').css({width:$(this).width()});
        //2-2-2、设置tb_head、tb_foot的宽度（在windows下滚动条宽度：17px/14px）
        $('.tb_head , .tb_foot').width($('.tb_body').width()-scrollBarWidth);
        //重置右侧弹层高度
        if($('.tb_foot').length>0){
        	$('#con_right').css({height:$('.tb_box').height() - $('.tb_head').height() + 10});
        }else{
        	$('#con_right').css({height:$('.tb_box').height() - $('.tb_head').height()});
        }
        
    }
    //2-3、拍摄计划、通告单 展开的计划详情内容表格重置
    $('#con_box thead.open').each(function(){
        var tb_box=$(this).parent('table').find('.tb_box');
        if(tb_box.find('.tb_body~.tb_foot').length>0){
            tb_box.find('.tb_body').css({height:tb_box.height()-tb_box.find('.tb_body~.tb_foot').outerHeight()});
            tb_box.find('.tb_body~.tb_foot').css({top:-30});
        }else{
            tb_box.find('.tb_body').css({height:tb_box.height()});
        }
        tb_box.find('.tb_head , .tb_foot').width(tb_box.children('.tb_body').width()-scrollBarWidth);
    })
}
//3、右侧弹层 -- 显示
function con_right_show(obj,callback){
    $('#con_right').children(obj).show();
    $('#con_right').show().stop().animate({right:0},80,function(){
        if($('body.no_scroll').length>0){
            $('#con_box').css('margin-right',$('#con_right').outerWidth());
        }else{
            $('.tb_box').css('margin-right',$('#con_right').outerWidth());

        }
        tb_reset();
        if(callback){
            callback();
        }
    });
    setTimeout(function(){
    	$(window).scrollLeft($(window).scrollLeft()-1);	
    },100)
    
}
//4、右侧弹层 -- 隐藏
function con_right_hide(callback){
    $('#con_top_edit a.current').removeClass('current');
    $('#con_right').stop().animate({right:-$('#con_right').outerWidth()},80,function(){
        if($('body.no_scroll').length>0){
            $('#con_box').css('margin-right',0);
            tb_reset();
        }else{
            $('.tb_box').css('margin-right',0);
            tb_reset();
        }
        $('#con_right').hide();
        if(callback){
            callback();
        }
    });
}
//5、鼠标HOVER时显示或隐藏下拉列表、二级菜单  例：dropdown_hover($('#titles')【hover事件对象】,$('#titles').next('#titles_list')【下拉列表】);
function dropdown_hover(obj,dropList){
    var _timer=null;
    obj.hover(function(){
        clearInterval(_timer);
        dropList.show();
    },function(){
        clearInterval(_timer);
        _timer=setTimeout(function(){
            dropList.hide();
        },200);
    })
    dropList.hover(function(){
        clearInterval(_timer);
        $(this).show();
    },function(){
        clearInterval(_timer);
        _timer=setTimeout(function(){
            dropList.hide();
        },200);
    })
}
//6、功能列表
function feature(){
    //6-1、右上角功能区 -- 点击
    $('#con_top_edit').click(function(ev){
        $('#con_right>div').hide();
        //6-1-1、按钮选中样式
        if(ev.target.tagName.toLowerCase()=='a'){
            $(ev.target).addClass('current').siblings('a').removeClass('current');
        }
        //6-1-2、设置拍摄地点点击
        if($(ev.target).hasClass('scene_edit_local')){
            con_right_show('#con_right_local',function(){
                $('.scene_local_list').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()*2-$('.con_right_main .search_box').outerHeight()});
            });
            return  false;
        }
        //6-1-3、添加拍摄计划点击
        if($(ev.target).hasClass('scene_edit_plan')){
            con_right_show('#con_right_plan',function(){
                $('#con_right_plan .con_right_main').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()-38});
            });
            return  false;
        }

        //6-1-3A、添加到通告单 
        if($(ev.target).hasClass('scene_edit_notice')){
            con_right_show('#con_right_notice',function(){
                $('#con_right_plan .con_right_main').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()-38});
            });
            return  false;
        }
        //6-1-4、添加列设置点击
        if($(ev.target).hasClass('scene_edit_row')){
            con_right_show('#con_right_row',function(){
                $('#con_right_row .con_right_main>div').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()-38,'overflow':'scroll'});
            });
            return  false;
        }
        //6-1-5、导出场景表点击
        if($(ev.target).hasClass('scene_edit_export')){
            con_right_show('#con_right_export',function(){
                $('#con_right_export .con_right_main>div').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()-38,'overflow-y':'auto'});
            });
            return  false;
        }
        //6-1-6、 添加场景
        if($(ev.target).hasClass('scene_edit_add')){
            con_right_show('#con_right_add',function(){
                $('#con_right_add .con_right_main>div').css({height:$('#con_right').outerHeight()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()*2,'overflow-y':'auto'});
                 $('#con_right_add #f_scene_create').attr('src',$('#con_right_add #f_scene_create').attr('baseSrc')+'?now_time='+new Date().getTime());
            });
            return false;
        }
        //6-1-7、 删除
        if($(ev.target).hasClass('scene_edit_delete')){
            con_right_show('#con_right_delete');
            return false;
        }
        //6-1-8、添加计划
        if($(ev.target).hasClass('plan_edit_add')){
            con_right_show('#con_right_planAdd');
            
            Date.prototype.format = function(format) { 
				var o = {  
					"M+" :this.getMonth() + 1, // month  
					"d+" :this.getDate(), // day  
					"h+" :this.getHours(), // hour  
					"m+" :this.getMinutes(), // minute  
					"s+" :this.getSeconds(), // second  
					"q+" :Math.floor((this.getMonth() + 3) / 3), // quarter  
					"S" :this.getMilliseconds()    
				}  
				if (/(y+)/.test(format)) {  
					format = format.replace(RegExp.$1, (this.getFullYear() + "")  
							.substr(4 - RegExp.$1.length));  
				}
				for ( var k in o) {  
					if (new RegExp("(" + k + ")").test(format)) {  
						format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]  
								: ("00" + o[k]).substr(("" + o[k]).length));  
					}  
				}  
				return format;  
			}  
            
            var nowDate = new Date().format("yyyy/MM/dd");  
			//var nowDatetime = new Date().format("yyyy-MM-dd hh:mm:ss"); 
            
            $('#con_right_planAdd').find('input[type=text]').val('');
            $('#con_right_planAdd .con_right_title span').text('新建计划');
            $('#plan_start').val(nowDate);
            $('#plan_group').val('未分组').attr({'sid':1});
            return false;
        }
        //6-1-8、添加计划
        if($(ev.target).hasClass('plan_edit_addNotice')){
            con_right_show('#con_right_notice');
            $('#con_right_notice').find('.con_right_title span').text('新建通告单');
            $('#con_right_notice').find('input[type=text]').val('').eq(0).val($('#con_right_notice').find('input[type=text]:eq(0)').attr('defaultTime'));
           
            return false;
        }
        //6-1-9、添加角色
        if($(ev.target).hasClass('role_edit_add')){
            con_right_show('#con_right_roleAdd');
            $('#con_right_roleAdd').find('input[type=text]').val('');
            $('#con_right_roleAdd .con_right_title span').text('添加角色');
            return false;
        }
      //6-1-10、拍摄计划页面中添加拍摄计划点击
        if($(ev.target).hasClass('con_right_plan')){
            con_right_show('#con_right_plan',function(){
                $('#con_right_plan .con_right_main').css({height:$('#con_right').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()-38});
            });
            return  false;
        }
    })
    //6-2、主内容功能区 -- 点击
    $('.con_box_edit a').click(function(ev){
        var _this=$(this);
        var _table=_this.parents('.con_box_body');
        //恢复选中状态
        _this.addClass('current').siblings('a').removeClass('current');
        //关闭计划详情
        if(_this.hasClass('plan_close')){
            //关闭当前计划
            _this.parents('thead').removeClass('open').parent('table').find('tbody').hide();
            //显示其他 编辑栏
            _this.parent('div').hide().siblings('div').show();
            //恢复 编辑栏选中样式
            _this.removeClass('current').siblings('a').removeClass('current').eq(0).addClass('current');
            //恢复 计划详情显示
            _this.parents('table').find('tr.tb_statis').hide().prev('tr').show();
            ev.stopPropagation();
            return false;
        }
        //添加场景
        if(_this.hasClass('plan_add')){
            $('#scene_popup').show().stop().animate({bottom:0},210);
            $('#scene_popup iframe').attr('src',$('#scene_popup iframe').attr('baseSrc')+'?random='+new Date());
            return false;
        }
        //计划详情
        if(_this.hasClass('plan_details')){
            _table.find('tr.tb_statis').hide().prev('tr').show();
            tb_reset();
            return false;
        }
        //统计详情
        if(_this.hasClass('plan_statis')){
            _table.find('tr.tb_statis').show().prev('tr').hide();
            return false;
        }
        //********* 隐藏右侧不显示的div *********//
        $('#con_right>div').hide();
        //修改计划
        if(_this.hasClass('plan_edit')){
            con_right_show('#con_right_planAdd',function(){
                $('#con_right_planAdd').find('input[type=text]').val('');
                $('#con_right_planAdd').find('.con_right_title span').text('修改计划');
                $('#con_right_planAdd').find('input[type=text]').eq(0).val(_this.parents('thead').find('td:eq(0)').text());
            });
            return false;
        }
        //修改通告单
        if(_this.hasClass('notice_edit')){
            con_right_show('#con_right_notice',function(){
                $('#con_right_notice').find('input[type=text]').val('');
                $('#con_right_notice').find('.con_right_title span').text('修改通告单');
                var _noticeForm=$('#con_right_notice').find('#editNotice');
                $('#con_right_notice').find('input[type=text]').eq(0).val(_this.parents('thead').find('td:eq(0)').text().trim());
                $(_noticeForm).find('input[name="noticeId"]').val(_this.parents('thead').find('tr').attr('sid'));
                $('#con_right_notice').find('input[type=text]').eq(1).val(_this.parents('thead').find('td:eq(2)').text().trim());
                $(_noticeForm).find('input[name="groupId"]').val(_this.parents('thead').find('td:eq(2)').attr('sid'));
            });
            return false;
        }
        //删除计划
        if(_this.hasClass('plan_delete')){
            con_right_show('#con_right_planDel');
            return false;
        }
        //设置计划拍摄日期
        if(_this.hasClass('plan_time')){
            con_right_show('#con_right_planDate');
            return false;
        }
        //移出场景
        if(_this.hasClass('plan_remove')){
            if(_this.parents('.tb_box_top').next('.tb_box').find('.tb_body tbody :checked').length<1){
                alert('请至少选择一个场景');
                con_right_hide();
                return false;
            }
            con_right_show('#con_right_sceneDel');
            return false;
        }
        //设置拍摄地点
        if(_this.hasClass('plan_local')){
            var _hei=$('#con_box').height()-$('.con_right_title').outerHeight()-$('.con_right_bottom').outerHeight()*2-$('.con_right_main .search_box').outerHeight();
            con_right_show('#con_right_local',function(){
                $('.scene_local_list').height(_hei);
            });
            return  false;
        }
        ev.stopPropagation();
    })
}
//7、右侧弹层功能
function right_popup_feature(){
    //7-1、右侧弹层“取消按钮”点击 【关闭右侧弹层】
    $('.con_right_bottom .btn_cancle').click(function(){
        con_right_hide(function(){
            resetPage();
        });
    })
    //7-2、场景表页面 - 右侧弹层 - 设置拍摄地点 - 拍摄地点列表（点击）
    $('.scene_local_list').click(function(ev){
        if(ev.target.tagName.toLowerCase()=='a'){
            $(ev.target).parents('.scene_local_list').prev('.search_box').val($(ev.target).text())
            $(ev.target).parent('li').addClass('current').siblings('li').removeClass('current');
        }
    })
    //7-3、场景表页面 - 添加到拍摄计划 - 已有计划（点击）
    $('.exist_plan dd li').click(function(){
        $(this).addClass('current').siblings('li').removeClass('current');
    })
    //7-4、场景表页面 - 添加拍摄计划 - 二级标题（点击折叠）
    $('#con_right_plan .con_right_main dt').click(function(){
        //隐藏
        if($(this).hasClass('current')){
            $(this).removeClass('current');
            $(this).siblings('dd').hide();
        }else{
            $(this).addClass('current');
            $(this).siblings('dd').show();
            $(this).parent('dl').siblings('dl').each(function(){
                $(this).children('dt').removeClass('current').siblings('dd').hide();
            })
        }
    })
    //设置拍摄地点 搜索框
    $('#con_right_local .search_box').keyup(function(){
        var _this=$(this)
            ,_sList=_this.next('.scene_local_list').children('li');
        _sList.each(function(){
            if($(this).text().search($.trim(_this.val())) != -1){
                    $(this).show();
            }else{
                $(this).hide();
            }
        })
    })
}
//8、日期初始化
function data_init(){
    //给所有data_picker加事件
    $('.date_picker').click(function(){WdatePicker({skin:'twoer',dateFmt:'yyyy/MM/dd'/*,opposite:false,disabledDates:['2014-04-13','2014-04-19','2014-04-16','2014-04-29','2014-04-21','2014-04-20']*/})});
    //添加角色、新建计划 - 日期限制
    $('#join_group , #plan_start,#leave_group , #plan_end').focus(function(){
        var obj_id=null
            ,sizeDate='maxDate'
            ,data_json={
                isShowClear:false,
                readOnly:true,
                skin:'twoer'
                ,dateFmt:'yyyy/MM/dd'
            };
        switch($(this).attr('id')){
            case 'join_group':
                obj_id='leave_group';
                sizeDate='maxDate';
                break;
            case 'leave_group':
                obj_id='join_group';
                sizeDate='minDate';
                break;
            case 'plan_start':
                obj_id='plan_end';
                sizeDate='maxDate';
                break;
            case 'plan_end':
                obj_id='plan_start';
                sizeDate='minDate';
                break;
        }
        data_json[sizeDate]='#F{$dp.$D(\''+obj_id+'\')||\'2020-10-01\'}';
        WdatePicker(data_json);
    })
}
//9、页面初始化
function  base_init(){
    $(document).click(function(){
        $('.dropdown_list').hide();
    })
    $(window).off('resize');
    $(window).resize(function(){
        resetPage();
    });
    //******************* 拍摄计划、通告单 start **********************//
    //拍摄计划、通告单 表头展开表格
    $('.con_box_body thead').click(function(ev){
        if(!$(this).hasClass('open')){
            $(this).find('.td_div_handle .plan_details').click();
        }
    })
    //表头 - 计划详情 - 点击并展开
    $('.td_div_handle .plan_details').click(function(ev){
        var _this=$(this);
        //关闭已展开的计划
        $('.con_box_body .open').each(function(){
            $(this).find('.plan_close').click();
        });
        //展开当前计划
        _this.parents('thead').addClass('open').parent('table').find('tbody').show();
        //显示 编辑栏
        _this.parent('div').hide().prev('div').hide().siblings('.con_box_edit').show();
        tb_reset();
        ev.stopPropagation();
    })
    //表头 - 统计详情 - 点击并展开
    $('.td_div_handle .plan_statis').click(function(ev){
        var _this=$(this);
        //关闭已展开的计划
        $('.con_box_body .open').each(function(){
            $(this).find('.plan_close').click();
        });
        //展开当前计划
        _this.parents('thead').addClass('open').parent('table').find('tbody').show();
        //显示 编辑栏
        _this.parent('div').hide().prev('div').hide().siblings('.con_box_edit').show();
        //显示 统计详情
        _this.parents('table').find('tr.tb_statis').show().prev('tr').hide();
        //恢复 编辑栏选中样式
        _this.parents('thead').find('.con_box_edit .plan_statis').addClass('current').siblings('a').removeClass('current');
        ev.stopPropagation();
    })
    $('.con_box_body tr.tb_statis dt').off('click');
    //统计 - 内容折叠
    $('.con_box_body tr.tb_statis dt').click(function(){
        var _this=$(this);
        if(_this.hasClass('open')){
            _this.removeClass('open');
            _this.next('dd').hide();
        }else{
            _this.addClass('open');
            _this.next('dd').show();
        }
    })
    //******************* 拍摄计划、通告单 end **********************//

    //表格滚动
    $('.tb_body').off('scroll');
    $('.tb_body').scroll(function(){
        var _left=$(this).scrollLeft();
        if($('.tb_body~.tb_foot').length>0){
            $(this).siblings('.tb_head , .tb_foot').scrollLeft(_left).css({'overflow-x':'scroll'}).css({'overflow-x':'hidden'});
        }else{
            $(this).prev('.tb_head').scrollLeft(_left).css({'overflow-x':'scroll'}).css({'overflow-x':'hidden'});
        }
    })
    //选择所有表格【checkbox】
    $('.tb_head .checkAll').click(function(){
        if($(this).is(':checked')){
            $(this).parents('.tb_box').find('.tb_body input[type="checkbox"]:visible').prop('checked',true);
        }else{
            $(this).parents('.tb_box').find('.tb_body input[type="checkbox"]:visible').prop('checked',false);
        }
    })
    //点击 TR 变色
    $('.tb_body tr').click(function(ev){
        $(this).addClass('tr_cur').siblings('.tr_cur').removeClass('tr_cur');
        ev.stopPropagation();
    })
    //所有 .dropdown 下拉列表效果
    $('.dropdown').each(function(){
        dropdown_hover($(this),$(this).children('.dropdown_list'));
    })
    $('.drop_click').off('click');
    $('.drop_click').click(function(ev){
        var _this=$(this);
        $('.dropdown_box').hide();
        if(_this.attr('url')){
            $.ajax({
                type:'post'
                ,url:_this.attr('url')
                ,dataType:'json'
                ,success:function(param){
                    var message=param.message;
                    if(message==undefined || message=="0"){
                        var groupList=param.groupList;
                        if(groupList!=undefined && groupList.length>0){
                            $('.dropdown_box').find('.plan_add_group').siblings('li').remove();
                            for(var i=0;i<groupList.length;i++){
                                var _liCopy=_this.next('.dropdown_box').find('.plan_add_group').clone(true);                               
                                _liCopy.removeClass('plan_add_group');
                                $(_liCopy).attr('sid',groupList[i].id);
                                $(_liCopy).find('a').text(groupList[i].name);
                                $('.dropdown_box').find('.plan_add_group').before(_liCopy);
                            }
                            _this.next('.dropdown_box').css({left:_this.position().left,top:_this.position().top+_this.outerHeight()}).show();
                        }
                    }else{
                        alert(message);
                    }
                }
            });
        }else{
            _this.next('.dropdown_box').css({left:_this.position().left,top:_this.position().top+_this.outerHeight()}).show();
        }
        ev.stopPropagation();
    })
    $('.dropdown_box li').off('click');
    $('.dropdown_box li').click(function(ev){
        var _this=$(this)
            ,arr=['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','R','U','V','W','X','Y','Z'];
        //通告单- 新建分组
        if(_this.hasClass('plan_add_group')){
            if(_this.siblings('li').length>5){
                return false;
            }
            var _copy=_this.prev().clone(true);
            var groupName=arr[$.inArray(_copy.text().split('')[0],arr)+1]+'组';
            //添加分组
            $.ajax({
                type:'post'
                ,url:'/produce/group/addGroup'
                ,data:'groupName='+groupName
                ,dataType:"json"
                ,success:function(param){
                    var message=param.message;
                    if(message==undefined || message=="0"){
                        _copy.attr('sid',param.id);
                        _copy.children('a').text(groupName);
                        _this.before(_copy);
                    }else{
                        alert(message);
                    }
                }
            });
            return false;
        }
        //将选择的文字填入input
        $(this).parent('ul').prev('.drop_click').val($(this).text()).attr('sid',$(this).attr('sid'));
        $(this).parents("#editNotice").find("input[name='groupId']").val($(this).attr('sid'));
    })
    $(document).click(function(){
        $('.dropdown_box').hide();
    })

    //过滤td - 点击
    $('.td_filter').click(function(ev){
        var _this=$(this)
            ,td_class='td_role_name'/* 角色演员管理页面 - 筛选器列名称 */
            ,tr_arr=[] /* 角色演员管理页面 - 当前可见的TR */
            ,_tb_filter=$('<div id="tb_filter"><i class="tb_filter_arrows"></i><ul></ul></div>') /* 过滤器BOX */
            ,_shade=$('<div id="shade_bg"></div>')/* 过滤器遮罩层 */;
        	if(_this.find(':checkbox:not(:first)').length == _this.find(':checked:not(:first)').length){
       			_this.find(':checkbox:first').prop('checked',true);
       		}else{
       			_this.find(':checkbox:first').prop('checked',false);
       		}
        //在页面上生成遮罩  #shade_bg
        if($('#shade_bg').length<1){
            _shade.css({width:$(window).width(),height:$('html').height()});
            $('body').append(_shade);
        }else{
            $('#shade_bg').css({width:$(window).width(),height:$('html').height()}).show();
        }
    	// 右键 取消搜索
        $('#shade_bg')[0].oncontextmenu=function(){
        	$('#shade_bg, #tb_filter, #tb_filter_performer').hide();
        	return false;
        }
        $('#shade_bg').off('click');
        //遮罩添加绑定查询事件
        $('#shade_bg').click(function(ev){
            tr_arr.length=0;
            var role_filter=[],attendance_filter=[];
            if(_this.parents('body#page_role_manager').length>0){//角色演员管理 页面  过滤查询
                if($('#tb_filter').find('ul').text().indexOf('演员')!=-1){
                    $('.tb_head .td_role_name ul').replaceWith($('#tb_filter').find('ul').clone(true).hide());
                    td_class='td_role_name';
                }else{
                    $('.tb_head .td_attendance ul').replaceWith($('#tb_filter').find('ul').clone(true).hide());
                    td_class='td_attendance';
                }
                //角色类型 - 条件
                $('.tb_head .td_role_name ul input:not(".filter_check_all"):checked').each(function(){
                    role_filter.push($(this).val());
                })
                //考勤 - 条件
                $('.tb_head .td_attendance ul input:not(".filter_check_all"):checked').each(function(){
                    attendance_filter.push($(this).val());
                })
                $('.tb_body tbody tr').each(function(){
                    var _this=$(this)
                        ,attendance_flag=_this.find('td.td_attendance').attr('flag')=='0'?'0':'1';
                    if($.inArray($.trim(_this.find('td.td_role_name').attr('flag')),role_filter)!=-1 && $.inArray(attendance_flag,attendance_filter)!=-1){
                        //alert('d')
                        _this.show();
                    }else{
                        _this.hide();
                    }
                })
            }else{//场景表 页面
                $('#tb_filter').find('input:not(".filter_check_all"):checked').each(function(){
                    tr_arr.push($(this).val());
                })
                $.ajax({
                    url:'',
                    data:'字段名='+_this.attr('sid')+':'+tr_arr,
                    success:function(param){
                        //alert(_this.attr('sid')+':'+tr_arr)
                    }
                })
            }
            $('#tb_filter').hide();
            $('#shade_bg').hide();
        })
        //在页面上生成过滤器 #tb_filter
        if($('#tb_filter').length<1){
            $('body').append(_tb_filter);
            //过滤器 - 点击
            $('#tb_filter').click(function(ev){
                ev.stopPropagation();
            })
        }
        $('#tb_filter ul').html(_this.find('.td_div ul').children().clone(true));
        $('#tb_filter').css({left:_this.offset().left,top:_this.offset().top+_this.outerHeight()}).show();
        if(_this.outerWidth()>$('#tb_filter').outerWidth()){
            $('#tb_filter').width(_this.outerWidth());
        }
        ev.stopPropagation();
    })
    //过滤器 -- checkbox 选中/取消
    $('.filter_check_all').click(function(){
        if($(this).is(':checked')){
            $(this).parents('li').siblings('li:visible').find(':checkbox').prop('checked',true);
        }else{
            $(this).parents('li').siblings('li:visible').find(':checkbox').prop('checked',false);
        }
    })
    $('.filter_check_search').off('keyup');
    // 过滤器 -- 模仿搜索 .filter_check_search
    $('.filter_check_search').keyup(function(){
    	var _this = $(this),
    		_thisLi = _this.closest('li'),
    		// 全选
    		_checkAll = _thisLi.next('li'),
    		// 被搜索的li
    		_liAll = _checkAll.nextAll('li')
    		// 是否有搜索到匹配值
    		,isExist = false;
    	
    	// 去除当前输入内容的前后空格,并按内容里边的空格拆分成数组（多个空格算一个）
    	var _key = $.trim(_this.val()).replace(/\s+/g,' ').split(' ');
    	_liAll.each(function(){
    		var that = $(this);
    		var _val =that.find(':checkbox').val();
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
    	_checkAll.find(':checked').prop('checked', false);
    	_checkAll.nextAll('li:hidden').find(':checked').prop('checked', false);
    	
    });
    
    //header - titles - 下拉列表效果
    dropdown_hover($('#titles'),$('#titles').next('#titles_list'));
    //修改个人信息
    $('#user_info_popup .btn_close').click(function(){
    	$(this).parents('#user_info_popup').find('.btn_cancle').click();
    })
    $('#settings #personal').click(function(){
         $('#user_info_popup').show();
            $('#user_info_bg').css({'width':'100%','height':'100%'}).show();
            $('#user_info_popup h2 em:eq(0)').click();
    })
    $('#user_info_popup h2 em').click(function(){
        var _this=$(this);
        _this.addClass('current').siblings('em').removeClass('current');
        $('#user_info_popup table').removeClass('current').eq(_this.index()).addClass('current');
    })
    $('#user_info_popup .btn_sure').off('click');
    $('#user_info_popup .btn_cancle').off('click');
    $('#user_info_popup .btn_cancle').click(function(){
        $('#user_info_popup').hide();
        $('#user_info_bg').hide();
    })
    $('#user_info_popup .btn_sure').click(function(){
        var _this=$(this);
        
        if(_this.hasClass('pwd_btn_sure')){//修改密码
        	var oldPassword = _this.parents('table').find('input[name="oldPassword"]').val();
        	var newPassword = _this.parents('table').find('input[name="newPassword"]').val();
        	var newPassword1 = _this.parents('table').find('input[name="newPassword1"]').val();
        	
        	if(oldPassword==''){
        		alert('请输入当前密码！');
        		return false;
        	}
        	if(!(/^[\w~!@#$%^&*]{6,16}$/.test(newPassword))){
        		alert('新密码格式：6～16位半角字符（字母、数字、符号~!@#$%^&*），区分大小写！');
        		return false;
        	}else if(oldPassword==newPassword){
        		alert('新密码不能和当前密码相同！');
        		return false;
        	}
        	if(newPassword1!=newPassword){
        		alert('两次输入的新密码不一致！');
        		return false;
        	}
        	
        	$.ajax({
        		url:'/user/updatePassword'
                ,type: 'POST'  
        		,data:'oldPassword='+encodeURI(oldPassword)+'&newPassword='+encodeURI(newPassword)
        		,success:function(info){
        		if(info == '0'){
        			alert("修改用户密码成功！");
        		}else{
        			alert("修改用户密码失败！"+info);
        		}
        	}
        	})            	
        	
        }else{//修改用户信息
            var userName = _this.parents('table').find('input[name="userName"]').val();
            var realName = _this.parents('table').find('input[name="realName"]').val();
            
        	if(!(/^[a-zA-Z][a-zA-Z0-9_-]{5,19}$/.test(userName))){
        		alert('用户名格式：6~20个字母、数字、下划线、减号，需以字母开头。');
        		return false;
        	}
        	
            if(!(/^[a-zA-Z.\s\u4E00-\u9FA3]{1,20}$/.test(realName))){
            	alert('姓名必填，1～20个中英文字符！');
            	return false;
            }
            
        	$.ajax({
        		url:'/user/updateUser'
                ,type: 'POST'  
        		,data:'userName='+userName+'&realName='+encodeURI(realName)
        		,success:function(info){
        		if(info == '0'){
        			alert("修改用户信息成功！");
        		}else{
        			alert("修改用户信息失败！"+info);
        		}
        	}
        	})            	
            
        }
    })
}

function tdHoverLayer(obj){
	if(!obj){
		var obj=$('.tb_body tbody');//:td:not(:first)	
	}
	//鼠标移到td上显示弹层效果
    $('body').append('<div id="tdHoverLayer" style="opacity:0.9;filter:alpha(opacity=90);font-weight:bolx;display: none;border:1px solid #006699;font-size:14px;position: absolute;;background: #e1f0ff;padding:10px;width:180px;line-height: 25px;z-index: 999;"></div>');
    obj.on('mousemove','td',function(){
    	var $target=$(this);
    	 var _left=$target.offset().left;
	    	 if($.trim($target.text()).length<3){
	    		 return false;
	    	 }
	            var _top=$target.offset().top+$target.outerHeight();
	            _left=_left+$('#tdHoverLayer').outerWidth()>=$(window).width()?_left-(_left+$('#tdHoverLayer').outerWidth()-$(window).width())-20:_left;
		
	           if($.trim($target.text())!=''){
	                $('#tdHoverLayer').show().css({left:_left,top:_top}).html($.trim($target.children('.td_div').text()));
	            }else{
	                $('#tdHoverLayer').hide();
	            }
    });
    obj.mouseout(function(){
        $('#tdHoverLayer').hide();
    });
    $(document).click(function(){
        $('#tdHoverLayer').hide();
    })
}

function dwFile(url){
  var w=window.open(url,'_blank');
  w.location.href = url;  
}

//TR 编辑 、 修改 行定位
function tr_edit_orientation(){
	try{
		//预算表 页面
		if($('.tr_edit_orientation').attr('level')){
			level_open($('.tr_edit_orientation').attr('level'));
		}
		var _top = $('.tr_edit_orientation').position().top;
	}catch(e){
		return false;
	}
	$('.tb_body').scrollTop(_top-$('.tb_head').outerHeight());
	//level展开
	function level_open(level){
		if(level.split('-').length <= 1){
			return false;
		}
		level=level.substring(0,level.lastIndexOf('-'));
		$('tr[level="'+level+'"]').find('.subj_name').click();
		return level_open(level);
	}
}

/**
 * 生成 场-景 概览 弹层
 * @param tit 
 * 标题 （集-场）
 * @param content
 * 内容 （场景内容）
 */
var createScenePopup = {
		init: function(tit, content){
			var that = this ;
			if(typeof tit == 'undefined' || typeof content == 'undefined'){
				return false;
			}
			content = typeof content == 'object'?content.html():content;
			// 创建弹层
			return {popup: this.create(tit,content), csp: that};
		},
		create: function(tit, content){
			var $popup= $('#sceneContentPopup'),
				that = this;
			
			// 已存在弹层
			if( $popup.length == 0 ){
				$popup  = $('<div id="sceneContentPopup"> <h1 class="sceneHead"> <em></em> <span class="btn_close"></span> </h1> <div class="sceneContent"></div> </div>');
				
				// 关闭弹层 
				$popup.find('.btn_close').click(function(){
					that.close();
				});
				
				// 拖拽
				this.drag($popup.find('h1'));
				
				$('body').append($popup);
			}else{
				$popup.show();
			}
			$popup.find('.sceneHead em').text(tit);
			$popup.find('.sceneContent').html(content);
			return $popup;
		},
		close: function(obj){
			(obj?obj:$('#sceneContentPopup')).hide();
		},
		drag: function(obj){
			// 鼠标按下
			obj.mousedown(function(ev){
				var $this = $(this),
					$popup = $this.parent('#sceneContentPopup');
				var dx = ev.clientX - $popup.offset().left;
				var dy = ev.clientY - $popup.offset().top;
				// 移动
				$(document).mousemove(function(ev){
					var x = ev.clientX,
						y = ev.clientY;
					$popup.css({left: x - dx, top: y - dy});
				});
				ev.stopPropagation();
				ev.preventDefault();
			});
			// 鼠标弹起
			obj.mouseup(function(){
				$(document).off('mousemove');
			});
			$(document).mouseup(function(){
				$(document).off('mousemove');
			});
		}
		
};
/**
 * 修改、添加场景时
 * 对主、次、三级场可选项搜索
 */
function sceneListSearch(){
	$(':text.scene_first, :text.scene_second, :text.scene_third').keyup(function(){
		var $this = $(this),
			_val = $.trim($this.val());
			$list = null;
		if( $this.next('.dropdown_box').length>0 ){
			 $list  = $this.next('.dropdown_box').find('li');
			 $list.each(function(){
				 if( $(this).text().search(_val) != -1 ){
					 $(this).show(); 
				 }else{
					 $(this).hide();
				 }
			 });
		}
	});
}

$(function(){ 
	// 群众演员过滤弹层 查找
	$('.filter_performer_search>input').keyup(function(){
		var $this =$(this),
			_val = $.trim( $this.val() ),
			_valArr = _val.replace(/\s+/g,' ').split(' '),
			$list = $this.closest('dt').next('dd').find(':checkbox'),
			isExist = false;
			$list.prop('checked', false);
			$this.parent().prev('label').children(':checkbox').prop('checked', false);
			 $list.each(function(){
				 for(var i = 0; i < _valArr.length; i++){
					 var _v = _valArr[i];
					 if( $(this).val().search(_v) != -1 ){
						 $(this).parent().show();
						 break;
					 }else{
						 $(this).parent().hide();
					 }
				 }
			 });
			
	});
    //执行
    base_init();
    data_init();
    resetPage();
    feature();
    right_popup_feature();
    $('#shade_bg').css({width:$('body').width()})
   /*TEST 给table设置固定宽度
   $('.tb_box table').width($('.tb_box table').outerWidth());*/
    //TEST 模拟增加表格
 /*  for(var i=0;i<20;i++){
        var _tr=$('.tb_body tbody tr:eq(0)').clone();
       _tr.children('td:eq(0)').children('.td_div').html(i);
        $('.tb_body tbody').append(_tr);
    }*/
    sceneListSearch();
    
    base_init();

	$(window).scroll(function(){
		$('#con_right').css('right',-($(this).scrollLeft()));
	});
})

/**
 * 
 */
function getSceneContent(isChild){
	$('.td_scene_find').off('click');
	// 获取场景
	$('.td_scene_find').click(function(ev){
		var _this = $(this);
		$.ajax({
			type:'post',
			url:'/produce/playAnalysis/getSceneById',
			data: 'sceneId=' + _this.attr("sid"),
			dataType:'json',
			success:function(param){
				if(param==undefined || param==null){
					alert("无法获取剧本内容");
				}else{
					var message=param.message;
	        		if(message==undefined || message=="0"){
	        			var sceneInfo=param.sceneInfo;
	        			if(sceneInfo==undefined || sceneInfo==null){
	        				alert("无法获取剧本内容");
	        			}else{
	        				var playTitle=sceneInfo.title;
	        				if(playTitle==null || playTitle==undefined){
	        					playTitle="";
	        				}
	        				var playContent=sceneInfo.playContent;
	        				if(playContent==null || playContent==undefined){
	        					playContent="";
	        				}

	        				if(isChild){
	        					window.parent.createScenePopup.init($.trim(_this.text()), playTitle+"<br/>"+playContent);
	        				}else{
	        					createScenePopup.init($.trim(_this.text()), playTitle+"<br/>"+playContent);
	        				}
	        				
	        			}
	        		}else{
	        			alert(message);
	        		}
				}
			}

		});
		ev.stopPropagation();
	});
}

/**
 * 集-场 过滤条件 输入数据验证
 * @param set
 * @param scene
 * @param n
 * @returns {Boolean}
 */
function yzSceneFilter( set, scene, n){
	   if( !$.isNumeric(set) ){
		  alert('请输入正确的'+ n +'集数');
		  return false;
	   }
	   if( !$.isNumeric(scene) ){
		  alert('请输入正确的'+ n +'场次');
		  return false;
	   }
	   
	  if( set != '' && scene != ''){
		  if( !$.isNumeric(set) && $.isNumeric(set) < 1 ){
			  alert('请输入正确的'+ n +'集数');
			  return false;
		  }
		 if( !$.isNumeric(scene) && $.isNumeric(scene) < 1 ){
			  alert('请输入正确的'+ n +'场次');
			  return false;
		  }
	   }
	   if( set=='' &&  scene != '' ){
		   alert('请输入'+ n+'集数' );
		   return false;
	   }  
	   return true;
 }

$(document).ready(function(){
	
	getSceneContent();
		
});



