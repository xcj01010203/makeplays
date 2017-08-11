"use strict";
(function($) {
    $.fn.timePicker = function(options) {
        var DEFAULT_HEIGHT = 200,
            DEFAULT_INTERVAL = 10,
            ITEM_HEIGHT = 26,
            KEY_ENTER = 13,
            KEY_UP = 38,
            KEY_DOWN = 40;
        options = options || {};

        var interval =  options.interval || DEFAULT_INTERVAL,
            listHeight = options.listHeight || DEFAULT_HEIGHT,
            height = options.height,
            width = options.width;

        return this.each(function(){

            var $self = $(this);
            var val = $self.val();
            var array = getArray();

            createHTML($self, array, getCurTimeIndex(val), val);
        });

        function createHTML($inp, array, curTimeIndex, val){
            var cHeight = height || $inp.outerHeight(true);
            var $list = $('<div class="time-picker-list"/>').css('height', listHeight);
            var $container = $inp.wrap('<div class="time-picker-container"  tabindex="-1"/>').parent()
                .css({
                    'width': width || $inp.outerWidth(true),
                    'height': cHeight,
                    'line-height': (cHeight-2) + 'px'
                }).append('<small class="timer-picker-error-msg">时间格式不正确</small>')
                .append($list).end().addClass('time-picker-title').parent();

            var $itemCon = $list.append('<div class="time-picker-list-container1"><span class="time-picker-list-item">' + array[0] + '</span></div>')
                .append('<div class="time-picker-list-container"/>').find('.time-picker-list-container')
                .css('height', listHeight-26);
            for(var i = 1, len = array.length; i < len; i++)
                $itemCon.append('<span class="time-picker-list-item">' + formatTime(array[i]) + '</span>');

            if(!!val)
                $list.find('.time-picker-list-item:eq('+ (curTimeIndex + 1) +')').addClass('active');

            bindEvents($container, $itemCon);
        }

        function bindEvents($container, $list){
            var timer;
            $container.on('click', '.time-picker-title', function(){
                toggleOpen($container, $list);
            }).on('keydown', function(e){
                if(e.keyCode == KEY_ENTER)
                    toggleOpen($container, $list);
                else if(e.keyCode == KEY_DOWN)
                    listDown($container, $list);
                else if(e.keyCode == KEY_UP)
                    listUp($container, $list);
            }).on('blur', function(){
                $container.removeClass('open');
                $list.find('.hover').removeClass('hover');
            }).on('click', '.time-picker-list-item', function(){
                clearTimeout(timer);
                $list.find('.active').removeClass('active');
                $container.find('.time-picker-title').val($(this).addClass('active').text()).end().removeClass('open');
            }).on('change','.time-picker-title', function(){
                var $tar = $(this);
                var val = $tar.val();

                //if(!val.match(/\d{2}:\d{2}/)){
                //    var $msg = $container.find('.timer-picker-error-msg').fadeIn();
                //    setTimeout(function(){
                //        $tar.val('');
                //        $msg.fadeOut();
                //    }, 500)
                //}

                $list.find('.active').removeClass('active');
                $list.find('.hover').removeClass('hover');
            }).on('blur', '.time-picker-title', function(){
                timer = setTimeout(function(){
                    $container.blur();
                }, 200);
            });
        }

        function toggleOpen($container, $list){
            $container.toggleClass('open');
            if($container.hasClass('open')){
                var $cur = $list.find('.active');
                var $title = $container.find('.time-picker-title');
                var curTimeIndex = $cur.length ? $cur.index() : getCurTimeIndex($title.val());

                /*$list.animate({
                    'scrollTop': ITEM_HEIGHT * (curTimeIndex + 1) - listHeight/2
                });*/
                $list.prop('scrollTop', ITEM_HEIGHT * (curTimeIndex + 1) - listHeight/2);
            }else{
                var $cur = $list.find('.hover').removeClass('hover');
                $cur.length && $list.find('.active').removeClass('active') && $container.find('.time-picker-title').val($cur.addClass('active').text());
            }
        }

        function listDown($container, $list){
            if($container.hasClass('open')){
                var $cur = $list.find('.hover').removeClass('hover');
                if(!$cur.length)
                    $cur = $list.find('.active');
                if(!$cur.length){
                    var curTimeIndex = getCurTimeIndex();
                    var $next = $list.find('.time-picker-list-item:eq('+ (curTimeIndex + 1) +')').addClass('hover');
                }else{
                    var curTimeIndex = $cur.index();
                    var $next = $cur.next();
                }

                if($next.length){
                    $list.scrollTop(ITEM_HEIGHT * ++curTimeIndex - listHeight/2);
                    $next.addClass('hover');
                }else{
                    $list.scrollTop(0);
                    $list.find('.time-picker-list-item:first-child').addClass('hover');
                }
            } else{
                var $title = $container.find('.time-picker-title');
                var $cur = $list.find('.active').removeClass('active');
                if(!$cur.length){
                    $cur =  $list.find('.time-picker-list-item:eq('+ getCurTimeIndex() +')');
                }
                var $next = $cur.next();
                if($next.length){
                    $title.val($next.addClass('active').text());
                }else{
                    $title.val($list.find('.time-picker-list-item:first-child').addClass('active').text());
                }
            }
        }

        function listUp($container, $list){
            if($container.hasClass('open')){
                var $cur = $list.find('.hover').removeClass('hover');
                if(!$cur.length)
                    $cur = $list.find('.active');
                if(!$cur.length){
                    var curTimeIndex = getCurTimeIndex();
                    var $prev = $list.find('.time-picker-list-item:eq('+ (curTimeIndex - 1) +')').addClass('hover');
                }else{
                    var curTimeIndex = $cur.index();
                    var $prev = $cur.prev();
                }

                if($prev.length){
                    $list.scrollTop(ITEM_HEIGHT * --curTimeIndex - listHeight/2);
                    $prev.addClass('hover');
                }else{
                    $list.scrollTop(ITEM_HEIGHT * $list.children().length);
                    $list.find('.time-picker-list-item:last-child').addClass('hover');
                }
            } else{
                var $title = $container.find('.time-picker-title');
                var $cur = $list.find('.active').removeClass('active');
                if(!$cur.length){
                    $cur =  $list.find('.time-picker-list-item:eq('+ getCurTimeIndex() +')');
                }
                var $prev = $cur.prev();
                if($prev.length){
                    $title.val($prev.addClass('active').text());
                }else{
                    $title.val($list.find('.time-picker-list-item:last-child').addClass('active').text());
                }
            }
        }

        function getArray(){
            var totalM = 24 * 60,
                result = ['待定'];
            for(var i = interval; i <= totalM; i += interval)
                result.push(i);

            return result;
        }

        function getCurTimeIndex(val){
            var curDate = val ? new Date('0000:' + val) : new Date();
            var curTime = curDate.getHours() * 60 + curDate.getMinutes();

            return Math.ceil(curTime/interval - 1);
        }

        function formatTime(minute){
            var hour = 0;

            if(minute > 60) {
                hour = parseInt(minute/60);
                minute = parseInt(minute%60);
            }
            return [zero(hour),zero(minute)].join(":");
        }

        function zero(v){
            return (v>>0)<10?"0"+v:v;
        }
    };
})(jQuery);