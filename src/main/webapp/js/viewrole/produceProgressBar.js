//公共数据说明
/**
 * 500: 进度条的宽度
 * progressLeftFinal: 标志宽度的一半(left值默认为-progressLeftFinalpx)**/

var progressBarColor = "#9fdabf";
var progressLeftFinal = 5.5;

//场数进度条
 /**
  * progressId:进度条的id
  * progressFlag: 进度条标志的id
  * totalViewCount: 总场数
  * finishedTotalViewCount：完成的场数
  * **/
function viewProgressBar(progressId, progressFlag, totalViewCount, finishedTotalViewCount){
	var viewCountValue;
	if(totalViewCount == 0){
		viewCountValue = 0;
		progressId.css({"background": "#ccc"});
	}else{
		viewCountValue = divide(multiply(500, finishedTotalViewCount), totalViewCount);
		progressId.css({"background": progressBarColor});
	}
	
	progressFlag.css({"left": viewCountValue - progressLeftFinal});
}


//生成两项对比进度条
/**
 * progressFirstId: 第一部分进度条的id
 * progressSecondId: 第二部分进度条的id
 * progressFirstFlag: 第一部分进度条标志的id
 * progressSecondFlag: 第二部分进度条标志的id
 * firstViewCount: 第一部分总数
 * secondViewCount: 第二部分总数
 * firstFinishedViewCount: 第一部分完成总数
 * secondFinishedViewCount: 第二部分完成总数**/

function twoContrastProgressBar (progressFirstId, progressSecondId, progressFirstFlag, progressSecondFlag, firstViewCount, secondViewCount, firstFinishedViewCount, secondFinishedViewCount){
	var width=$(".progress-div").eq(0).width()-2;
	var firstProgressWidth = multiply(width, divide(firstViewCount, add(firstViewCount, secondViewCount)));//第一部分所占宽度
	var secondProgressWidth = multiply(width, divide(secondViewCount, add(firstViewCount, secondViewCount)));//第二部分所占宽度
	if(firstViewCount == 0 && secondViewCount == 0){//第一部分，第二部分总数为0
		//隐藏外戏显示内戏
		progressSecondId.css({"width": '0px'});
		progressSecondFlag.hide();
		progressFirstId.css({"width": '100%', "background": "#ccc"});
		progressFirstFlag.css({"left": '-'+progressLeftFinal+'px'}).show();
		
	}
	else if(firstViewCount == 0){//第一部分总数为零,显示第二部分
		progressFirstId.css({"width": '0px'});
		progressSecondId.css({"width": '100%'});
		progressFirstFlag.hide();
		if(secondFinishedViewCount != 0){//外戏完成进度
			var waiValue = multiply(divide(secondProgressWidth, secondViewCount), secondFinishedViewCount);
			progressSecondFlag.css({"left": waiValue - progressLeftFinal}).show();
		}else{
			progressSecondFlag.css({"left": '-'+progressLeftFinal+'px'}).show();
		}
	}
	else if(secondViewCount == 0){//第二部分总数为零,显示第一部分
		progressSecondId.css({"width": '0px'});
		progressFirstId.css({"width": '100%', "background": progressBarColor});
		progressSecondFlag.hide();
		if(firstFinishedViewCount != 0){//内戏完成进度
			var neiValue = multiply(divide(firstProgressWidth, firstViewCount), firstFinishedViewCount);
			progressFirstFlag.css({"left": neiValue - progressLeftFinal}).show();
		}else{
			progressFirstFlag.css({"left": "-"+progressLeftFinal+"px"}).show();
		}
	}
	else{
		progressFirstId.css({"width": firstProgressWidth, "background": progressBarColor});
		progressSecondId.css({"width": secondProgressWidth});
		if(firstFinishedViewCount != 0){//内戏完成进度
			var neiValue = multiply(divide(firstProgressWidth, firstViewCount), firstFinishedViewCount);
			progressFirstFlag.css({"left": neiValue - progressLeftFinal}).show();
		}else{
			progressFirstFlag.css({"left": "-"+progressLeftFinal+"px"}).show();
		}
		if(secondFinishedViewCount != 0){//外戏完成进度
			var waiValue = multiply(divide(secondProgressWidth, secondViewCount), secondFinishedViewCount);
			progressSecondFlag.css({"left": waiValue - progressLeftFinal}).show();
		}else{
			progressSecondFlag.css({"left": '-'+progressLeftFinal+'px'}).show();
		}
	}
	
}

