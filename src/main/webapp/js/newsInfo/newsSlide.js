function newsSlide(jqx){
    var slide = [];
    var $slide = $("#slide");
    var $aNum = $("#active-num");
    var conH = $slide.height();
    var conW = $slide.width();
    var maxScale = 3;
    var doubleTapScale = 2;

    jqx.find("img").each(function(i, el){
        slide.push('<div class="swiper-slide"><img src="'+ this.src +'"></div>');

        var timer;
        $(el).on("click", function(e){
            clearTimeout(timer);
            timer = setTimeout(function(){
                $slide.css({
                    "z-index": 9999,
                    "opacity": 1
                });
            }, 100);
            my.slideTo(i);
            $("#active-num").text(i + 1);
            my.onResize();
        })
    });

    var my = new Swiper('.swiper-container', {
        onSlideChangeStart: function(swiper){
            $aNum.text(swiper.activeIndex+1);

            // 1. �жϵ�ǰ��ͼƬ�Ƿ�Ŵ�״̬
            var el = null;
            switch (swiper.swipeDirection){
                case "next":
                    el = swiper.slides[swiper.activeIndex - 1];
                    break;
                case "prev":
                    el = swiper.slides[swiper.activeIndex + 1];
                    break;
            }
            if(el){
                $(el).find(">img").css({
                    "-webkit-transform": "scale(1) translateZ(0)",
                    "transform": "scale(1) translateZ(0)"
                }).get(0).isScale = false;
            }
        }
    });

    $(".swiper-wrapper").html(slide.join(""));
    $("#all-num").text(slide.length);

    function hideSlide(){
        $slide.css({
            "z-index": -9999,
            "opacity": 0
        });
        var el = my.slides[my.activeIndex];
        $(el).find(">img").css({
            "-webkit-transform": "scale(1) translateZ(0)",
            "transform": "scale(1) translateZ(0)"
        }).get(0).isScale = false;
    }

    $slide.on("click", function(){
        hideSlide();
    }).find("img").each(function(i, el){
        var hammer = new Hammer(el);
        hammer.get('pinch').set({ enable: true });
        hammer.get('pan').set({ direction: Hammer.DIRECTION_ALL });
        var $el = $(el);
        var timer;
        var height = 0;
        var width = 0;

        hammer.on("doubletap", function(ev){
            clearTimeout(timer);
            if(!el.isScale){
                el.isScale = true;
                $el.css({"transform" : "scale("+doubleTapScale+") translateZ(0)", "-webkit-transform" : "scale("+doubleTapScale+") translateZ(0)"});
            }else{
                el.isScale = false;
                $el.css({"transform" : "scale(1) translateZ(0)", "-webkit-transform" : "scale(1) translateZ(0)"});
                my.unlockSwipes();
            }

        }).on("panstart", function(ev){
            // 1. �ж��Ƿ�Ϊ�Ŵ�״̬
            if(el.isScale){
                my.lockSwipes();

                // 2. ��ȡ�ϴ��϶���ƫ����
                var transform = $el.css("transform");
                var matrix = transform.split(/[,\(\)]/g);
                var deltaX = Number(matrix[5]);
                var scale = Number(matrix[1]);

                height || (height = $el.height());
                width || (width = $el.width());

                var maxDeltaX = (width * scale - conW) / 2;
                maxDeltaX = el.maxDeltaX = maxDeltaX < 0 ? 0 : maxDeltaX;

                var maxDeltaY = (height * scale - conH) / 2;
                el.maxDeltaY = maxDeltaY < 0 ? 0 : maxDeltaY;

                if(maxDeltaX && Math.abs(deltaX) == maxDeltaX){
                    my.unlockSwipes();
                }

                el.deltaX = deltaX;
                el.deltaY = Number(matrix[6]);
                el.scale = scale;
            }
        }).on("panend", function(ev){

            // 1. �Ƿ�Ŵ�״̬
            if(el.isScale){
                var deltaY = ev.deltaY + el.deltaY;

                var maxDeltaY = el.maxDeltaY;
                if((maxDeltaY - Math.abs(deltaY)) < 0) {
                    // ������Ļ֮��
                    var scale = el.scale;
                    var deltaX = Number($el.css("transform").split(/[,\(\)]/g)[5]);
                    var matrix = "";
                    if(deltaY < 0){
                        // ��������
                        matrix = "matrix("+ scale +", 0, 0, " + scale + ", " + deltaX + ", " + -maxDeltaY + ") translateZ(0)";
                        $el.css({"transform" : matrix, "-webkit-transform" : matrix});
                    }else if(deltaY > 0){
                        // ��������
                        matrix = "matrix("+ scale +", 0, 0, " + scale + ", " + deltaX + ", " + maxDeltaY + ") translateZ(0)";
                        $el.css({"transform" : matrix, "-webkit-transform" : matrix});
                    }
                }
            }
        }).on("panmove", function(ev){
            if(!el.isScale)
                return;

            var deltaX = el.deltaX + ev.deltaX;
            var deltaY = ev.deltaY + el.deltaY;
            var scale = el.scale;
            var maxDeltaX = el.maxDeltaX;

            if((maxDeltaX - Math.abs(deltaX)) > 0) {
                // ���û���
                my.lockSwipes();

                // �ƶ�ͼƬ
                var matrix = "matrix("+ scale +", 0, 0, " + scale + ", " + deltaX + ", " + deltaY + ") translateZ(0)";
                $el.css({"transform" : matrix, "-webkit-transform" : matrix});
            }else{
                deltaY = Number($el.css("transform").split(/[,\(\)]/g)[6]);
                if(deltaX < 0){
                    // ��������
                    matrix = "matrix("+ scale +", 0, 0, " + scale + ", " + -maxDeltaX + ", " + deltaY + ") translateZ(0)";
                    $el.css({"transform" : matrix, "-webkit-transform" : matrix});
                }else if(deltaX > 0){
                    // ��������
                    matrix = "matrix("+ scale +", 0, 0, " + scale + ", " + maxDeltaX + ", " + deltaY + ") translateZ(0)";
                    $el.css({"transform" : matrix, "-webkit-transform" : matrix});
                }
            }
        }).on("pinchstart", function(){
            // 当前缩放倍数
            var transform = $el.css("transform");
            var matrix = transform.split(/[,\(\)]/g);
            el.pinchStartScale = Number(matrix[1]);

        }).on("pinchmove", function(ev){
            var scale = el.pinchStartScale + ev.scale - 1;
            $el.css({"transform" : "scale("+ scale +") translateZ(0)", "-webkit-transform" : "scale("+ scale +") translateZ(0)"});
        }).on("pinchend", function(ev){
            var scale = el.pinchStartScale + ev.scale - 1;

            if(scale > maxScale){
                scale = maxScale;
                el.isScale = true;
            }else if(scale > 1){
                el.isScale = true;
            }else{
                scale = 1;
                el.isScale = false;
                my.unlockSwipes();
            }
            $el.css({"transform" : "scale("+ scale +") translateZ(0)", "-webkit-transform" : "scale("+ scale +") translateZ(0)"});
        }).on("tap", function(){
            clearTimeout(timer);
            timer = setTimeout(function(){
                hideSlide();
            }, 200);

        });
    });
};