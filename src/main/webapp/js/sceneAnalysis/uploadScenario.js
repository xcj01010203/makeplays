var $afterElem = null;
var crewType = null;
var $curElemForDrag = null; // 用于存放当前拖动的$dom
var $afterElem = null;
var toUploaderFilesNum = 0; // 待上传文件数量
var hasUploadSuccess = false; // 是否有上传成功的文件
var minSeriesNo = 10000; // 所有剧本中最小的集数
var $container = null;
var $wrap = null;
var isDefaultFormat = false;

$(document).ready(function() {

	$container = $('.J-container');
	$wrap = $('.J-wrap');
	
	//获取剧组类型
	getCrewType(); 
	
	// 初始化对分割符的操作
	initSepatorDom($container); 
	
	// 初始化对格式信息的操作
	initContainerDom($container);
	
	// 初始化拖拽操作
	initDragDom($wrap); 
	
	//加载分隔符/标签/自定义格式 信息
	loadSymbolInfo();
	
	//加载校验格式信息窗口
	loadCheckFormatWindow();
	/*************************************单击解析日志复制开始****************************************/
	var clip = new ZeroClipboard(document.getElementById("copyInfo"));
	clip.on("aftercopy", function(){
		parent.showSuccessMessage("复制成功！");
	});
    /*var clip = new ZeroClipboard(document.getElementById("analysisLog"), {
      moviePath: "<%=basePath%>/js/zeroClipboard/ZeroClipboard.swf"
    });
      clip.on("ready", function() {
        //alert("Flash movie loaded and ready.");
        this.on("aftercopy", function(event) {
          //alert("Copied text to clipboard: " + event.data["text/plain"]);
          $('#copyInfo').text('复制成功！');
          setTimeout(function(){
                    $('#copyInfo').text('单击复制！');
          }, 5000);
        });
      });

      clip.on("error", function(event) {
        alert('error[name="' + event.name + '"]: ' + event.message);
        ZeroClipboard.destroy();
      });
    
    var text = $('#analysisLog').val();
    if(!text){
      $('#copyInfo').hide();
    }*/
	
    /*************************************单击解析日志复制结束****************************************/
	
});

//获取剧组类型
function getCrewType(){
	$.ajax({
		url : "/viewManager/getCrewType",
		type : "post",
		dataType : "json",
		async : false,
		success : function(response) {
			if (response.success) {
				crewType = response.crewType;
				//加载示例场景标题
				loadFirstContent();
			}else {
				parent.showErrorMessage(response.message);
			}
		}
	});
}

//加载校验格式窗口
function loadCheckFormatWindow(){
	var $viewInfoTr = $("#viewInfoTr");
	var dataArr = [];
	if (crewType == 0 || crewType == 3) { //电影剧本
		dataArr.push(" <tr><td>场</td><td></td></tr>");
	}else {
		dataArr.push(" <tr><td>集</td><td></td></tr><tr><td>场</td><td></td></tr>");
	}
	$viewInfoTr.before(dataArr.join(""));
}

//跳转到第二步窗口
function nextSecondStep(){
	var content = $("#scenarioSample").val();
	/*if (content == '') {
		showErrorMessage("请填写场景标题信息！");
		return;
	}*/
	$("#secondScenarioSample").val(content);
	//隐藏第一步窗口，显示第二步窗口
	$("#firstWindow").hide();
	$("#secondWindow").show();
}

//点击上一步
function goToFirstWindow(){
	closeExample();
	//隐藏第第二步窗口，显示第一步窗口
	$("#excempleWindow").hide();
	$("#thirdWindow").hide();
	$("#firstWindow").show();
}

//根据剧组类型拼接第一步内容
function loadFirstContent(){
	//第一个div
	var $example1 = $("#example1");
	//第二个div
	var $example2 = $("#example2");
	//第三个div
	var $example3 = $("#example3");
	
	if (crewType == 0 || crewType == 3) { //电影剧本
		$("#firstInput").val("e2s0e3s0e4s0e5s1s2e7");
		$example1.append(" <p><span> 3   繁华的街道       日         外</span></p><p class='title-p-span'><span>人物: 张品, 李铎, 范阿姨</span></p>");
		$("#secondInput").val("e2s0e3s0e4s0e5");
		$example2.append(" <p><span> 15. 高级粤菜餐厅包间          日         内</span></p>");
		$("#thirdInput").val("e2s0e4s0e5s1s4e3s1s2e7");
		$example3.append(" <p><span> 3           日          外 </span></p><p class='title-p-span'><span> 场景: 繁华的街道 </span></p><p class='title-p-span'><span> 人物: 张品, 李铎, 范阿姨</span></p>");
	}else {
		$("#firstInput").val("e1s0e2s0e3s0e4s0e5s1s2e7");
		$example1.append(" <p><span> 1-3   繁华的街道        日         外</span></p><p class='title-p-span'><span>人物: 张品, 李铎, 范阿姨</span></p>");
		$("#secondInput").val("e1s0e2s0e3s0e4s0e5");
		$example2.append(" <p><span> 8-15. 高级粤菜餐厅包间         日          内</span></p>");
		$("#thirdInput").val("e1s0e2s0e4s0e5s1s4e3s1s2e7");
		$example3.append(" <p><span> 1-3          日           外</span></p><p class='title-p-span'><span> 场景: 繁华的街道 </span></p><p class='title-p-span'><span> 人物: 张品, 李铎, 范阿姨</span></p>");
	}
}

//跳转到第三步窗口
function nextThirdStep(){
	var content = $("#scenarioSample").val();
	/*if (content == '') {
		showErrorMessage("请填写场景标题信息！");
		return;
	}*/
	//重置窗口
	$("#thirdScenarioSample").val(content);
	//隐藏第一步窗口，显示第二步窗口
	$("#firstWindow").hide();
	$("#secondWindow").hide();
	$("#thirdWindow").show();
	//选中第二步选中的格式
	var $selectedTextarea = $("div[class*='selected-textarea']");
	//设置选中的剧本格式，判断当前是否是后台返回的默认的剧本格式，并且没有选择格式信息
	if (isDefaultFormat) {
		var selectedId = $selectedTextarea.attr("id");
		$("#formatTag").html("");
		if ($selectedTextarea != undefined && selectedId != undefined) {
			//取出选中的格式
			var formatStr = '';
			if (selectedId == 'example1') {
				formatStr = $("#firstInput").val();
			}else if (selectedId == 'example2') {
				formatStr = $("#secondInput").val();
			}else if (selectedId == 'example3') {
				formatStr = $("#thirdInput").val();
			}
			
			var $separatorListContainter = $("#separatorListContainter");
			var $scripteleListContainter = $("#scripleteListContainter");
			
			/** ================动态加载自定义格式信息列表========================= */
			$.ajax({
				url : "/scenarioManager/queryScenarioFormatInfo",
				type : "post",
				async : false,
				data: {formatStr: formatStr},
				success : function(param) {
					if (param.success) {
						//当前是默认剧本格式
						if (param.isDefaultFormat) {
							isDefaultFormat = param.isDefaultFormat;
						}
						var scenarioFormatList = param.scenarioFormatList;
						$.each(scenarioFormatList, function(index, scenarioFormatAtomic) {
							// 如果是元素，执行元素标签的点击事件
							$scripteleListContainter.find("span[value=" + scenarioFormatAtomic + "]").trigger("click");
							// 如果是分隔符，执行分隔符标签的点击事件
							$separatorListContainter.find("span[value=" + scenarioFormatAtomic + "]").trigger("click");
						});
						if (param.supportCNViewNo) {
							$("#supportCNViewNo").attr("checked", "checked");
						}
						
						$("#wordCount").val(param.wordCount);
						$("#lineCount").val(param.lineCount);
						if (param.pageIncludeTitle) {
							$("#pageIncludeTitle")[0].checked = true;
						} else {
							$("#pageIncludeTitle")[0].checked = false;
						}
					} else {
						showErrorMessage(param.message);
					}
				}
			});
			
		}else {
			var $formatTag = $("#formatTag");
			$formatTag.append("<label class='format-info-span' id='pointInfoSpan'>请从右边备选标签中匹配您的场景标题信息所对应的场景格式</label>");
		}
	}
	
}

//选中文本域时将当前文本域设置为蓝色
function selectFormat(own){
	/*//将文本域变为默认背景色
	$allTextarea = $("div[name='exampleTextarea']");
	$allTextarea.css('background-color', '#F2F2F2');
	$allTextarea.css('color', '#A7A7A7');
	$allTextarea.removeClass("selected-textarea");
	//给当前的文本域设置背景色
	$textarea = $(own);
	$textarea.css('background-color', 'D8EDFE');
	$textarea.css('color', '');
	$textarea.addClass("selected-textarea");*/
	
	if (!$(own).hasClass("selected")) {
		$(own).addClass("selected");
		$(own).addClass("selected-textarea");
		
		$(own).siblings(".text-div-info").removeClass("selected-textarea");
		$(own).siblings(".text-div-info").removeClass("selected");
	} else {
		$(own).removeClass("selected");
		$(own).removeClass("selected-textarea");
	}
}

//加载分隔符/标签/自定义格式 信息
function loadSymbolInfo(){
	var $separatorListContainter = $("#separatorListContainter");
	var $scripteleListContainter = $("#scripleteListContainter");
	
	/** ================动态加载分隔符信息列表========================= */
	$.ajax({
		url : "/scenarioManager/querySeparatorList",
		type : "post",
		async : false,
		success : function(respone) {
			if (respone.success) {
				var separatorInfoList = respone.separatorInfoList;
				$.each(
					separatorInfoList,
					function(index, separatorInfo) {
						if (separatorInfo.sepaName == "\\t") {
							$separatorListContainter.append("<span class='bg-f0f0f0' title='tab' sample=' ' value="	+ separatorInfo.sepaId
													+ " draggable='true'>Tab</span>");
						}else if (separatorInfo.sepaName == "/r/n") {
							$separatorListContainter
							.append("<span class='bg-f0f0f0 key key-enter' title='换行' sample='<br/>' value="
									+ separatorInfo.sepaId
									+ " draggable='true'></span>");
						}else if (separatorInfo.crewId != "0") {
							$separatorListContainter.append("<span class='bg-f0f0f0 label-new' sample="	+ separatorInfo.sepaName
													+ " value="	+ separatorInfo.sepaId + " draggable='true'><span class='J-contenteditable' contenteditable='false'>"
													+ separatorInfo.sepaName + "</span><i class='btn-del'>x</i></span>");
						} else {
							$separatorListContainter.append("<span class='bg-f0f0f0' title=" + separatorInfo.sepaDesc + " sample="
													+ separatorInfo.sepaName + " value=" + separatorInfo.sepaId	+ " draggable='true'>"
													+ separatorInfo.sepaName + "</span>");
						}
					});
				$separatorListContainter.append("<span class='btn-add bg-f0f0f0'>+</span>");
				// 如果不是剧组不是电视剧剧本，则显示是否支持“一二三”场次的选项
			}
		},
		failure : function(respone) {

		}
	});

	/** ================动态加载元素信息列表========================= */
	$.ajax({
		url : "/scenarioManager/queryScripteleList",
		type : "post",
		async : false,
		success : function(respone) {
			if (respone.success) {
				var scripteleList = respone.scripteleList;
				$.each(scripteleList, function(index, scriptele) {

					if (scriptele.eleName == "集") {
						if (crewType != 0 && crewType != 3) {
							$scripteleListContainter.append("<span class='bg-78a6cd' sample="+ scriptele.eleSample + " value="
													+ scriptele.eleId + " draggable='true'>" + scriptele.eleName + "</span>");
						}
					}else if (scriptele.eleName == "人物") {
						$scripteleListContainter.append("<span class='bg-78a6cd' sample=" + scriptele.eleSample + " value="
								+ scriptele.eleId + " draggable='true'>" + scriptele.eleName + "</span>");
					} else {
						$scripteleListContainter.append("<span class='bg-78a6cd' sample=" + scriptele.eleSample + " value="
													+ scriptele.eleId + " draggable='true'>" + scriptele.eleName + "</span>");
					}
				});
			}
		},
		failure : function(param) {

		}
	});

	/** ================动态加载自定义格式信息列表========================= */
	$.ajax({
		url : "/scenarioManager/queryScenarioFormatInfo",
		type : "post",
		async : false,
		success : function(param) {
			if (param.success) {
				//当前是默认剧本格式
				if (param.isDefaultFormat) {
					isDefaultFormat = param.isDefaultFormat;
				}
				var scenarioFormatList = param.scenarioFormatList;
				$.each(scenarioFormatList, function(index, scenarioFormatAtomic) {
					// 如果是元素，执行元素标签的点击事件
					$scripteleListContainter.find("span[value=" + scenarioFormatAtomic + "]").trigger("click");
					// 如果是分隔符，执行分隔符标签的点击事件
					$separatorListContainter.find("span[value=" + scenarioFormatAtomic + "]").trigger("click");
				});
				if (param.supportCNViewNo) {
					$("#supportCNViewNo").attr("checked", "checked");
				}
				$("#wordCount").val(param.wordCount);
				$("#lineCount").val(param.lineCount);
				if (param.pageIncludeTitle) {
					$("#pageIncludeTitle")[0].checked = true;
				} else {
					$("#pageIncludeTitle")[0].checked = false;
				}
			} else {
				showErrorMessage(param.message);
			}
		},
		failure : function() {

		}
	});

}

//初始化对分割符的操作
function initSepatorDom($container) {
		$('.J-dragged').on('click', '> span:not(.btn-add)',	function(ev) {
				var $self = $(this);
				$("#pointInfoSpan").remove();
				if ($self.find('.J-contenteditable').attr('contenteditable') !== "true") {
					$self.clone().removeClass('require').removeClass('label-new').appendTo($container);
				}
	
			}).on('dragstart', '>span', function(ev) {
				// 开始拖拽
				ev.originalEvent.dataTransfer.setData('Text', ''); // 用于兼容火狐
				ev.originalEvent.dataTransfer.effectAllowed = 'copy';
				$curElemForDrag = $(ev.target);
				$curElemForDrag.isNew = true;
				return true;
			}).on('dragen', '>span', function(ev) {
				// 开始结束
				$curElemForDrag = null;
				return false;
			}).on('click','.btn-add', function() {
					// 添加新的标签
					$('<span class="bg-f0f0f0 label-new" draggable="true"><span class="J-contenteditable" contenteditable="true"></span><i class="btn-del">x</i></span>')
							.insertBefore(this).find('.J-contenteditable')
							.focus();
				}).on('click','.btn-del',function() {
				var sepaId = $(this).parent().attr("value");
				// 删除
				deleteOperate(sepaId);
				// 根据标识，删除container里对应的标签
				$container.find("span[value=" + sepaId + "]").remove();
				$("#scenarioSample").find("span[tagId=" + sepaId + "]")	.remove();

				$(this).parent().remove();

				return false;
			}).on('click', '.btn-edit', function(ev) {

			var el = this.previousElementSibling;
			if (el.getAttribute('contenteditable') == "true") {
				el.focus();
				return false;
			}

			el.setAttribute('contenteditable', true);
			el.focus();

			return false;

			}).on('blur', '.J-contenteditable', function() {

				if (this.getAttribute('contenteditable') == "false")
					return false;

				// 验证
				var $span = $(this);

				var text = $span.text().trim();
				var value = $span.parent("span").attr("value");

				if (validateNewLabel(text)) {

					// ajax 请求 以下是success回调应做的事
					var newOperateId = saveNewOperate(text, value);
					if (newOperateId == "") {
						$span.next("i[class=btn-del]").click();
						return false;
					}

					if (text.length != $span.text().length) {
						$span.text(text);
					}
					this.setAttribute('contenteditable', false);
					$span.parent().attr("value", newOperateId);
					$span.parent().attr("sample", text);
				} else {
					$span.focus();
				}
			}).on('keypress', '.J-contenteditable', function(e) {

				// 验证
				if (e.keyCode === 13) {
					var $span = $(this);
					var text = $span.text().trim();
					var value = $span.parent("span").attr("value");

					if (validateNewLabel(text)) {

						// ajax 请求 以下是success回调应做的事
						var newOperateId = saveNewOperate(text, value);
						if (newOperateId == "") {
							$span.next("i[class=btn-del]").click();
							return false;
						}

						if (text.length != $span.text().length) {
							$span.text(text);
						}
						this.setAttribute('contenteditable', false);
						$span.parent().attr("value", newOperateId);
						$span.parent().attr("sample", text);
					} else {
						$span.focus();
					}
					return false;
				}
			}).on('dragstart', '.J-contenteditable', function(ev) {

				if (this.getAttribute('contenteditable')) {
					// 防止在编辑状态拖动内容项
					ev.preventDefault();
					return false;
				}
			});
}

//初始化对格式信息的操作
function initContainerDom($container) {
	$container.on('click', '> span', function() {
		this.remove();
	}).on('dragover', '>span', function(ev) {
		/* 拖拽元素在目标元素头上移动的时候 */
		ev.preventDefault();
		return true;
	}).on('dragenter', '>span', function() {
		/* 拖拽元素进入目标元素头上的时候 */
		if ($curElemForDrag) {
			this.style.marginLeft = $curElemForDrag.outerWidth() + 'px';
		}

		if ($afterElem)
			$afterElem.css('margin-left', '0');

		$afterElem = $(this);
		return true;
	}).on('drop', '>span', function(ev) {
		/* 拖拽元素进入目标元素头上，同时鼠标松开的时候 */
		drop({
			'top' : ev.originalEvent.pageY,
			'left' : ev.originalEvent.pageX
		});

		genScenarioSample();
		return false;
	}).on('dragover', function(ev) {
		/* 拖拽元素在目标元素头上移动的时候 */
		ev.preventDefault();
		return true;
	}).on('drop', function(ev) {
		drop({
			'top' : ev.originalEvent.pageY,
			'left' : ev.originalEvent.pageX
		});

		return false;
	}).on('dragstart', '>span', function(ev) {
		// 开始拖拽
		$curElemForDrag = $(ev.target);
		ev.originalEvent.dataTransfer.setData('Text', '');
		ev.originalEvent.dataTransfer.effectAllowed = 'move';
		this.className += ' dropping';
		return true;
	}).on('dragen', '>span', function(ev) {
		// 开始结束
		$curElemForDrag = null;
		return false;
	});
}

//初始化拖拽操作
function initDragDom($wrap) {
	$wrap.on('dragover', function(ev) {
		/* 拖拽元素在目标元素头上移动的时候 */
		ev.preventDefault();
		return true;
	}).on('drop', function(ev) {
		$afterElem && $afterElem.css('margin-left', '0');
		$afterElem = null;
		$curElemForDrag = null;
		return false;
	});
}

//保存新分隔符信息
function saveNewOperate(operate, id) {
	var newOperateId = "";
	var oldOperateId = "";

	if (id != undefined) {
		oldOperateId = id;
	}

	$.ajax({
		url : '/scenarioManager/saveOperateInfo',
		type : 'post',
		async : false,
		data : {
			operate : operate,
			operateId : oldOperateId
		},
		success : function(param) {
			if (!param.success) {
				showErrorMessage(param.message);
			} else {
				newOperateId = param.separatorInfoModel.sepaId;

				// 修改成功后，在自定义格式列表中查找对应的符号，如果有，则更改相应的符号
				if (oldOperateId != "") {
					$("#formatTag").find(">span[value=" + oldOperateId + "]").text(operate);
					$("#scenarioSample").find(">span[tagid=" + oldOperateId + "]").text(operate);
				}
			}
		},
		failure : function() {

		}
	});

	return newOperateId;
}

//删除节点信息
function drop(mouseOffset) {
	if (!$curElemForDrag){
		return;
	}
	if ($curElemForDrag.isNew){
		$curElemForDrag = $curElemForDrag.clone();
	}
	
	if ($afterElem) {
		var afterElemOffset = $afterElem.offset();

		if ((afterElemOffset.left + $afterElem.outerWidth()) < mouseOffset.left || afterElemOffset.top + $afterElem.outerHeight() < mouseOffset.top) {
			$container.append($curElemForDrag);
			
		} else {
			$afterElem.before($curElemForDrag);
		}
		$afterElem.css('margin-left', '0');
		$afterElem = null;
	} else {
		$container.append($curElemForDrag);
	}
	$curElemForDrag.removeClass('require').removeClass('label-new');
	$curElemForDrag = null;
}

// 删除分隔符信息
function deleteOperate(sepaId) {
	$.ajax({
		url : '/scenarioManager/deleteOperateInfo',
		type : 'post',
		data : {sepaId : sepaId},
		success : function(param) {
			if (!param.success) {
				showErrorMessage(param.message);
			}
		},
		failure : function() {

		}
	});
}

//引用父窗口的错误提示
function showErrorMessage(message) {
	if (typeof (parent.showErrorMessage) == 'function') {
		parent.showErrorMessage(message);
	}
}

//验证lable数据
function validateNewLabel(text) {
	return true;
}

//改变日志滚动条
function logChange() {
	document.getElementById("analysisLog").scrollTop = document.getElementById("analysisLog").scrollHeight;
	//获取上传文件后输出的日志信息
	//var $span = $("#analysisLog span");
	var $p = $("#analysisLog p");
	var logContentArr = [];
	/*$span.each(function(){
		var logcontent = $(this).text();
		logContentArr.push(logcontent+"\r");
	});*/
	
	$p.each(function(){
		var logcontent = $(this).text();
		if (logcontent.indexOf('上传') > -1 && logcontent.indexOf('上传') < 9) {
			if (logcontent.indexOf('上传结束') > -1) {
				logContentArr.push(logcontent+"\r\r");
			}else {
				logContentArr.push(logcontent+"\r");
			}
		}else {
			logContentArr.push("  " + logcontent+"\r");
		}
	});
	
	$("#logContent").val(logContentArr.join(""));
}

//如果不是剧组不是电视剧剧本，则显示是否支持“一二三”场次的选项
//function isShowOtherRule() {
//	if (crewType == Constants.CrewType.movie || crewType == 3) {
//		$("#otherRule").show();
//	} else {
//		$("#otherRule").hide();
//	}
//}

//校验是否是第一次上传剧本
function isFirstUploadScenari() {
	$.ajax({
		url : "/scenarioManager/checkUploadedScenaris",
		type : "post",
		dataType : "json",
		async : false,
		success : function(response) {
			if (!response.success) {
				showErrorMessage(response.message);
				return false;
			}
			if (!response.exist) {
				isFirstUpload = true;
				$(".help").css("-webkit-animation",
						"myfirst 1s linear 0s infinite alternate");
			}
		}
	});
}

// 点击关闭上传剧本界面按钮
function closeUpload() {
	parent.closeUploadWin();

	// 判断是否有需要跳过和替换的数据，如果有跳转到跳过替换的页面
	if (toUploaderFilesNum == 0) {
		return false;
	}

	$.ajax({
		url : '/scenarioManager/hasSkipOrReplaceData',
		type : 'post',
		data : {},
		async : false,
		success : function(respone) {
			if (respone.success) {
				if (respone.hasSkipOrReplaceData) {
					parent.showResultWindow();
				} else if (hasUploadSuccess) {
					if (typeof (parent.refreshPage) == 'function') {
						parent.refreshPage(minSeriesNo);
					}
				}
			} else {
				showErrorMessage(respone.message);
			}
		},
		failure : function() {

		}
	});
}

// 初始化上传日志
function initAnalysisLog() {
	var text = $('#analysisLog').val();
	if (!text) {
		$('#copyInfo').hide();
	}
}

//跳转到第二步
function goToSecondWindow(){
	//隐藏第一步窗口，显示第三步窗口
	$("#firstWindow").hide();
	$("#secondWindow").show();
	$("#thirdWindow").hide();
}

//创建两个超时器；
var timer1;
var timer2;
//展示示例
function showExample(){
	clearInterval(timer1);
	var viewInfoLeft = $("#viewInfoDiv").width();
	$("#viewInfoDiv").animate({"left": 0-viewInfoLeft}, 300);
	timer1 = setTimeout(function(){
		$("#viewInfoDiv").hide();
		
	},300);
	/*$("#viewInfoDiv").hide();*/
	$("#excempleWindow").animate({"right": 0}, 350).show();
}

//关闭示例显示
function closeExample(){
	clearInterval(timer2);
	
	$("#excempleWindow").animate({"right": -460}, 300);
	timer2 = setTimeout(function(){
		$("#excempleWindow").hide();
		
	}, 300);
	$("#viewInfoDiv").animate({"left": 0}, 350).show();
	
}

//跳转到第四步
function nextFourStep(){
	//隐藏第一步窗口，显示第二步窗口
	$("#firstWindow").hide();
	$("#excempleWindow").hide();
	$("#thirdWindow").hide();
	$("#fivethWindow").show();
}

//返回第三步窗口
function goTothirdWindow (){
	$("#firstWindow").hide();
	$("#thirdWindow").show();
	$("#fivethWindow").hide();
}

//跳转到第五步窗口
function nextFiveStep (){
	closeExample();
	$("#firstWindow").hide();
	$("#excempleWindow").hide();
	$("#thirdWindow").hide();
	$("#fivethWindow").show();
	
	//初始化多部件上传
	loadUploadScenario();
}

//返回第四步窗口
function goToFourthWindow(){
	$("#firstWindow").hide();
	$("#thirdWindow").show();
	$("#fivethWindow").hide();
}
//解析样例
function refreshSample() {
	// 待匹配样例
	var scenarioSample = $("#thirdScenarioSample").val();
	if (scenarioSample == "") {
		showErrorMessage("请输入待匹配的文本");
		return false;
	}

	// 自定义格式
	var tags = $("#formatTag").find(">span");
	var tagsValues = '';
	$.each(tags, function() {
		var value = $(this).attr("value");
		tagsValues += value;
	});

	if (tagsValues == "") {
		showErrorMessage("请定义剧本格式");
		return false;
	}

	var seriesNoTag = $("#formatTag").find("span[value=" + Constants.Scriptele.seriesNo + "]");
	var hasSeriesNoTag = true;
	if (seriesNoTag.length == 0) {
		hasSeriesNoTag = false;
	}
	var supportCNViewNo = false;
	if (!hasSeriesNoTag && $("#supportCNViewNo")[0].checked) {
		supportCNViewNo = true;
	}

	$.ajax({
		url : "/scenarioManager/analysisScenarioTitle",
		type : "post",
		async : true,
		data : {
			scenarioFormat : tagsValues,
			title : scenarioSample,
			supportCNViewNo : supportCNViewNo
		},
		success : function(param) {
			if (!param.success) {
				showErrorMessage(param.message);
				return false;
			}

			var tbody = $("#analyResult").find("tbody");
			tbody.children().remove();

			//电影剧本不显示集次
			if (crewType == 0 || crewType == 3) {
				if (param.e2 != null) {
					tbody.append("<tr><td>场</td><td>" + param.e2 + "</td></tr>");
				}
			}else {
				if (param.e1 != null) {
					tbody.append("<tr><td>集</td><td>" + param.e1 + "</td></tr>");
				}
				if (param.e2 != null) {
					tbody.append("<tr><td>场</td><td>" + param.e2 + "</td></tr>");
				}
			}
			
			if (param.e3 != null) {
				tbody.append("<tr><td>主场景/次场景/三级场景</td><td>" + param.e3
						+ "</td></tr>");
			}
			if (param.e4 != null) {
				tbody.append("<tr><td>气氛</td><td>" + param.e4 + "</td></tr>");
			}
			if (param.e5 != null) {
				tbody.append("<tr><td>内外景</td><td>" + param.e5 + "</td></tr>");
			}
			if (param.e6 != null) {
				tbody.append("<tr><td>季节</td><td>" + param.e6 + "</td></tr>");
			}
			if (param.e7 != null) {
				tbody.append("<tr><td>人物（/隔开）</td><td>" + param.e7 + "</td></tr>");
			}
		},
		failure : function(param) {

		}
	});
}

//生成剧本样式
function genScenarioSample() {
	var tags = $("#formatTag").find(">span");
	var totalSample = '';
	$.each(tags, function() {
		var sample = $(this).attr("sample");
		var tagId = $(this).attr("value");
		totalSample += "<span tagId=" + tagId + ">" + sample + "</span>";
	});

	$("#scenarioSample").html(totalSample);
}

//加载多剧本上传
function loadUploadScenario() {
	var uploader = WebUploader.create({
				// 不压缩image
				resize : false,

				// 文件接收服务端。
				server : 'scenarioManager/uploadScenario',

				// 选择文件的按钮。可选。
				// 内部根据当前运行是创建，可能是input元素，也可能是flash.
				pick : '#selectFileBtn',
				formData : {},
				threads : 1,
				timeout : 20 * 60 * 1000,
				// 只允许选择文件，可选。
				accept : {
					title : 'Office_Word',
					extensions : 'doc,docx',
					mimeTypes : 'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'
				}
			});

	// 当有文件添加进来的时候
	uploader.on('fileQueued', function(file) {
				var newTr = $("<tr id="	+ file.id + " class='item'><td class='info'><p>" + file.name 
							+ "</p></td><td class='state'>等待上传...</td><td class=''><span class='deleteScenario'>删除</span></td></tr>");
				newTr.find(".deleteScenario").on("click", function() {
					uploader.removeFile(file, true);

					newTr.remove();
				});
				$("#filelist tbody").append(newTr);
			});

	// 文件上传过程中创建进度条实时显示。
	uploader.on('uploadProgress',function(file, percentage) {
				var $state = $('#' + file.id).find(".state"), 
					$percent = $state.find('.progress-bar');

				// 避免重复创建
				if (!$percent.length) {
					$state.text("");

					$state.append("<div class='progress-bar spinner'><div class='rect1'></div><div class='rect2'></div><div class='rect3'></div><div class='rect4'></div><div class='rect5'></div></div>");
					$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").hide();
				}
			});

	// 发送请求成功后触发
	uploader.on('uploadSuccess', function(file, response) {
		if (response.success) {
			$('#' + file.id).find('.state').text('已上传');
			hasUploadSuccess = true;

			if (response.titleErrorMsgStr != "") {
				$("#analysisLog").append(response.titleErrorMsgStr);
				$('#copyInfo').show();
			}
			if (response.notFullMatchTitle != "") {
				$("#analysisLog").append(response.notFullMatchTitle);
			}
			//$("#analysisLog").append("<span>--《" + file.name + "》上传成功</span><br>");
			$("#analysisLog").append("<p>-上传结束</p>");
			logChange();
			// 处理最小集数
			if (minSeriesNo > response.minSeriesNo) {
				minSeriesNo = response.minSeriesNo;
			}

			$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").show();

			// 删除列表中的文件
			setTimeout(function() {
				$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").click();
			}, 5000);
		} else {
			$('#' + file.id).find('.state').text('解析失败');
			showErrorMessage("《" + file.name + "》" + response.message);

			// 中断上传当前正在上传的文件。
			uploader.stop(true);
			// 删除列表中的文件
			setTimeout(function() {
				$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").click();
			}, 1000);

			$("#analysisLog").append("<p>*《" + file.name + "》上传失败</p>");
			logChange();
		}

	});

	uploader.on('uploadError', function(file, reason) {
		$('#' + file.id).find('.state').text('网络故障');// 中断上传当前正在上传的文件。
		uploader.stop(true);
		setTimeout(function() {
			$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").click();
		}, 1000);

		$("#analysisLog").append("<p>*网络故障，错误码：" + reason + "</p>");
	});

	// 不管成功或者失败，文件上传完成时触发
	uploader.on('uploadComplete', function(file) {
		$("#seriesNo").val("");
	});

	// 当所有文件上传结束时触发
	uploader.on('uploadFinished', function(file) {
	});

	// 当开始上传流程启动时触发
	uploader.on('startUpload', function(file) {
		// 检验是否含有集次的标签
		var seriesNoTag = $("#formatTag").find("span[value=e1]");
		var seriesNo = $("#seriesNo").val();

		// 获取已经进入队列, 等待上传 的文件
		var toUploaderFiles = uploader.getFiles('queued');

		if (toUploaderFiles.length == 0) {
			toUploaderFilesNum = 0;
			return false;
		}

		toUploaderFilesNum = toUploaderFiles.length;

		if (seriesNoTag.length == 0 && crewType != 0 && crewType != 3) {
			var seriesNoFlag = $("input[name=seriesNoFlag]:checked").val();
			if (seriesNoFlag == 2 && toUploaderFiles.length > 1) {
				$.each(toUploaderFiles, function(index, file) {
					if (index != 0) {
						setTimeout(function() {
							$("#filelist tbody").find("tr[id=" + file.id + "]").find(".deleteScenario").click();
						}, 1000);
					}
				});
			}
			if (seriesNoFlag == 2 && seriesNo == "") {
				uploader.stop(true);
				showErrorMessage("请填写当前剧本的集次");
			}

			if ($("#seriesNoDiv").is(":hidden")) {
				$("#seriesNoDiv").show();
				uploader.stop(true);
				return;
			}
		}
	});

	uploader.on("uploadStart", function(file) {
		$("#analysisLog").append("<p>上传《" + file.name + "》</p>");
		logChange();
	});

	uploader.on('all', function(type) {
	});

	$("#uploadBtn").on("click",	function() {
				// 校验是否含有集次的标签
				var seriesNoTag = $("#formatTag").find("span[value=" + Constants.Scriptele.seriesNo + "]");
				var seriesNo = $("#seriesNo").val();
				var hasSeriesNoTag = true;
				if (seriesNoTag.length == 0) {
					hasSeriesNoTag = false;
				}
				if (hasSeriesNoTag) {
					$("#seriesNoDiv").hide();
				}
				
				var tags = $("#formatTag").find(">span");
				if (tags.length == 0) {
					showErrorMessage("请配置剧本格式");
					return ;
				}
				var tagsValues = '';
				$.each(tags, function() {
					var value = $(this).attr("value");
					tagsValues += value;
				});

				var seriesNoFlag = $("input[name=seriesNoFlag]:checked").val();
				var groupSeriesNoFlag = false;
				if (!hasSeriesNoTag && seriesNoFlag == 1) {
					groupSeriesNoFlag = true;
				}

				var supportCNViewNo = false;
				if (!hasSeriesNoTag && $("#supportCNViewNo")[0].checked) {
					supportCNViewNo = true;
				}
				
				var pageIncludeTitle = false;
				if ($("#pageIncludeTitle")[0].checked) {
					pageIncludeTitle = true;
				}

				uploader.option('formData', {
					lineCount : $("#lineCount").val(),
					wordCount : $("#wordCount").val(),
					scenarioFormat : tagsValues,
					hasSeriesNoTag : hasSeriesNoTag,
					extralSeriesNo : seriesNo,
					groupSeriesNoFlag : groupSeriesNoFlag,
					supportCNViewNo : supportCNViewNo,
					pageIncludeTitle: pageIncludeTitle
				});

				uploader.upload();
			});

	$("#seriesNo").on("keyup", function() {
		if (isNaN($(this).val())) {
			$(this).val("");
		}
	});
	$("#lineCount").on("keyup", function() {
		if (isNaN($(this).val())) {
			$(this).val("");
		}
	});
	$("#wordCount").on("keyup", function() {
		if (isNaN($(this).val())) {
			$(this).val("");
		}
	});
	
}

//复制日志
function copyLogContent(){
	//var path = serverPath;
	//ZeroClipboard.setMoviePath(path + 'js/zeroClipboard/ZeroClipboard.swf'); 
	//ZeroClipboard.config( { swfPath: path+'js/zeroClipboard/ZeroClipboard.swf' } );
	
	/*var clip = new ZeroClipboard.Client(); // 新建一个对象 
	clip.setHandCursor( true ); // 设置鼠标为手型 
	clip.setText(logContentArr.join("")); // 设置要复制的文本。 
	clip.glue("copyInfo");*/
}

//鼠标移动到集次号，改变集次的颜色
function seriesChange(){
	$("#seriesNoSpan").css("background-color","#FF6600");
	$("#seriesNOTitle").css("background-color","#FF6600");
}

function resizeSeries(){
	$("#seriesNoSpan").css("background-color","#78a6cd");
	$("#seriesNOTitle").css("background-color","#F2F2F2");
}

//鼠标移动到场次号，改变场次的颜色
function viewNoChange(){
	$("#viewNoSpan").css("background-color","#FF6600");
	$("#viewNoTitle").css("background-color","#FF6600");
}
function resizeViewNo(){
	$("#viewNoSpan").css("background-color","#78a6cd");
	$("#viewNoTitle").css("background-color","#F2F2F2");
}

//鼠标移动到气氛，改变气氛的颜色
function atmosChange(){
	$("#atmosphereSpan").css("background-color","#FF6600");
	$("#atmosphereTitle").css("background-color","#FF6600");
}
function resizeAtmos(){
	$("#atmosphereSpan").css("background-color","#78a6cd");
	$("#atmosphereTitle").css("background-color","#F2F2F2");
}

//鼠标移动到内外景，改变内外景的颜色
function siteChange(){
	$("#siteSpan").css("background-color","#FF6600");
	$("#siteTitle").css("background-color","#FF6600");
}
function resizeSite(){
	$("#siteSpan").css("background-color","#78a6cd");
	$("#siteTitle").css("background-color","#F2F2F2");
}

//鼠标移动到场景，改变场景的颜色
function viewChange(){
	$("#viewSpan").css("background-color","#FF6600");
	$("#viewTitle").css("background-color","#FF6600");
}
function resizeView(){
	$("#viewSpan").css("background-color","#E3E3E3");
	$("#viewTitle").css("background-color","#F2F2F2");
}

//鼠标移动到场景内容，改变场景内容的颜色
function viewContentChange(){
	$("#viewContentSpan").css("background-color","#FF6600");
	$("#viewContentTitle").css("background-color","#FF6600");
}
function resizeViewContent(){
	$("#viewContentSpan").css("background-color","#337AB7");
	$("#viewContentTitle").css("background-color","#F2F2F2");
}

//鼠标移动到人物，改变人物的颜色
function roleChange(){
	$("#roleSpan").css("background-color","#FF6600");
	$("#roleTitle").css("background-color","#FF6600");
}
function resizeRole(){
	$("#roleSpan").css("background-color","#E3E3E3");
	$("#roleTitle").css("background-color","#F2F2F2");
}

//鼠标移动到人物内容时，改变人物的颜色
function roleContentChange(){
	$("#roleContent").css("background-color","#FF6600");
	$("#roleContentTitle").css("background-color","#FF6600");
}

function resizeRoleContent(){
	$("#roleContent").css("background-color","#337AB7");
	$("#roleContentTitle").css("background-color","#F2F2F2");
}

//显示引导界面
function showGuide() {
	$("#showGuidePicture").show();
}

//关闭引导页面
function closeGuide(){
	$("#showGuidePicture").hide();
}

