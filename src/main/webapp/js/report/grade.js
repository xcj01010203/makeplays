;(function(){
    $(function(){
        //鼠标移到星上
        var $ul = $('.grade-star');
        var $df = $('.grade-df');

        $ul.off().on('click', '.star-left', function(e){

            var $el = $(e.currentTarget).parent();

            setFullStar($ul, $el, $df, true);

        }).on('click', '.star-right', function(e){

            var $el = $(e.currentTarget).parent();

            setFullStar($ul, $el, $df, false);

        });
    });

    /*
     * 填充星星
     * @params index 填充到序号为止 0开始
     */
     function setFullStar($ul, $el, $df, isHalf, score){
         var index = $el && $el.length ? $el.index() : undefined;
         var df = score || (index === undefined ? 0 : (index + 1) * 20);

         $ul.find('>li').removeClass('full-star').removeClass('half-star').find('.star-info').hide();
         $ul.find('>li:lt('+ (isHalf || index === undefined ? index : index + 1) +')').addClass('full-star');

         if($el && $el.length){
             $el.find('.star-info').css({
                 'display': index !== undefined ? 'block' : 'none',
                 'right': isHalf ? '50%' : 'auto',
                 'left': isHalf ? 'auto' : '50%'
             });
         }
         if(isHalf){
             $el && $el.addClass('half-star');
             !score && (df -= 10);
         }
         $df.text(df);
     };

    /*
    *   根据分数获取星的位置
    *   @params  $ul
    *           score 分数
    *
    *   @return  $el
    *           isHalf
    */
    function getCurElem($ul, score){
        if(!score){
            return {
                '$el': undefined,
                'isHalf': false
            };
        }
        if(score > 100){
            score = 100;
        }
        var isHalf = !!(score % 20);
        var index = parseInt(score / 20);

        return {
            '$el': $ul.find('>li:eq('+ (isHalf ? index : index - 1) +')'),
            'isHalf': isHalf
        };

    };

    //对外接口命名空间
    var Grade = this.Grade || {};

    /*
    *   对外接口  设置分数，改变页面效果
    *   @params score
    */
    Grade.setScore = function(score){
        var $ul = $('.grade-star');
        var $df = $('.grade-df');

        var result = getCurElem($ul, score);

        setFullStar($ul, result.$el, $df, result.isHalf, score);
    };

    /*
    *   对外接口 设置选中印象项
    *   @params impression 一个object对象 里面有两个属性：best, bad
    *                       best, bad 都是array类型，存放选中的checkbox对应的value值
    */
    Grade.setChecked = function(impression){
        var best_arr = impression.best;
        var bad_arr = impression.bad;

        if(best_arr && best_arr.length){
            var $bestContainer = $('.yx > .best');

            //先重置所有best下的checkbox状态
            $bestContainer.find('input[type="checkbox"]').each(function(i, el){
                el.checked = false;
            });

            for(var i = 0, len = best_arr.length; i < len; i++){
                var el = $bestContainer.find('input[value=' + best_arr[i] + ']');
                if(el.length){
                    el[0].checked = true;
                }
            }
        }

        if(bad_arr && bad_arr.length){
            var $badContainer = $('.yx > .bad');

            //先重置所有best下的checkbox状态
            $badContainer.find('input[type="checkbox"]').each(function(i, el){
                el.checked = false;
            });

            for(var i = 0, len = bad_arr.length; i < len; i++){
                var el = $badContainer.find('input[value=' + bad_arr[i] + ']');
                if(el.length){
                    el[0].checked = true;
                }
            }
        }
    };

    this.Grade = Grade;

}).call(this);