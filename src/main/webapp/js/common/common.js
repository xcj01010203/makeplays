//消息框
function showInfoMessage(message){
	swal({
        title: "",   
        text: "<p style='padding:5px;'>"+ message +"</p>", 
        html: true,
        showConfirmButton: true,
        });
}
//成功提示框
function showSuccessMessage(message){
	swal({
		title: '',
        text: "<p style='padding:5px;'>"+ message +"</p>",
        timer: 800,   
        showConfirmButton: false,
        type:"success",
        html: true
      });
}
//错误提示框
function showErrorMessage(message){
	swal({
        title: "",   
        text: "<p style='padding:5px;'>"+ message +"</p>", 
        html: true,
        type: "error",
        showConfirmButton: true,
        });
}
//提示框
//            function popupPromptBox(title,content,obj){
//            	swal({
//                    title:'',
//                    text: content,   
//                    type: "warning",   
//                    showCancelButton: true,   
//                    confirmButtonColor: "rgba(255,103,2,1)",   
//                    confirmButtonText: "确定",  
//                    cancelButtonText: "取消",
//            		closeOnConfirm: false,
//            		closeOnCancel: true
//                }, function(isConfirm) {
//                	if(isConfirm){
//                		$(obj);
//            		}
//                });  
//            }

//取消、确定同时需要回调
function doubleCallBackFun(title,content,confirmButtonText, cancelButtonText, confirm, cancel){
	if (confirmButtonText == null) {
		confirmButtonText = "确定";
	}
	
	if (cancelButtonText == null) {
		cancelButtonText = "取消";
	}
	
	if (confirm == null) {
		confirm = swal.close;
	}
	
	if (cancel == null) {
		cancel = swal.close;
	}
	
	swal({
		title: title,
		text: content,
		type: "warning",
		showCancelButton: true,
		confirmButtonColor: "rgba(255,103,2,1)",
		confirmButtonText: confirmButtonText,
		cancelButtonText: cancelButtonText,
		closeOnConfirm: false,
		closeOnCancel: false
	},
	function(isConfirm){
		if(isConfirm){
			$(confirm);
		}else{
			$(cancel);
		}
	});
}



/**
 * 设置未来(全局)的AJAX请求默认选项
 * 主要设置了AJAX请求遇到Session过期的情况
 */
$.ajaxSetup({
    type: 'POST',
    complete: function(xhr,status) {
        var sessionStatus = xhr.getResponseHeader('sessionstatus');
        if(sessionStatus == 'timeout') {
        	top.location.href = '/toLoginPage';
        }
    }
});

/**
 * 在页面中任何嵌套层次的窗口中获取顶层窗口
 * @return 当前页面的顶层窗口对象
 */
function getTopWinow(){
    var p = window;
    while(p != p.parent){
        p = p.parent;
    }
    return p;
}
