var unSelectedNum = 0;
    
$(document).ready(function() {
	loadAccountInfo("");
	loadBudgetSubj("");
	initDetailWindow();
	
	//只读权限，不能进行添加
	if(isBudgetReadonly) {
		$(".btn-add").remove();
	}
 });
        
 //初始化会计科目详细信息窗口控件
function initDetailWindow() {
	$("#accountDetailWin").jqxWindow({
	    theme: "ui-lightness",
	    width: 550,
	    height: 230,
	    maxHeight: 2000,
	    maxWidth: 2000,
	    modalZIndex: 1000,
	    resizable: false,
	    autoOpen: false,
	    cancelButton: $("#cancel"),
	    isModal: true,
	    initContent: function() {
	        $("#name").on("focus", function () {
	        $(this).removeClass("has-error");
	        $(".error-message").text("");
	        });
	        $("#name").on("blur", function () {
	        if ($(this).val() == "") {
	        	$(this).addClass("has-error");
	           }
	        });
	        $("#code").on("focus", function () {
	            $(this).removeClass("has-error");
	            $(".error-message").text("");
	        });
	        $("#code").on("blur", function () {
	            if ($(this).val() == "") {
	            	$(this).addClass("has-error");
	            }
	        });
	    }
    });
}
        
        //加载会计科目表格
        //accountId: 加载后需要默认选中的行的数据
        function loadAccountInfo(accountId) {
            //每次加载时，把选中的会计科目ID值置为空
            $("#selectedAccId").val("");
            $("#selectedAccName").val("");
            $("#selectedAccCode").val("");

            $.ajax({
                url: "/accountSubject/queryAccSubjList",
                type: "post",
                dataType: "json",
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return ;
                    }
                    
                    $("#accountTableBody").empty();
                    var accSubjList = response.accSubjList;
                    
                    if (accSubjList.length > 0) {
                        $(".empty-div").hide();
                    } else {
                        $(".empty-div").show();
                    }
                    
                    var trArray = [];
                    
                    $.each(accSubjList, function(index, item) {
                        var id = item.id;
                        var name = item.name;
                        var code = item.code;
                        var financeSubjNames = item.financeSubjNames == null ? "" : item.financeSubjNames;
                        
                        
                        trArray.push("<tr index='"+ (++index) +"' accId='"+ id +"' title='单击管理财务科目' onclick='showMySubj(this, \""+ id +"\", \""+ name +"\", \""+ code +"\")' onmouseover='showOperBtn(this)' onmouseout='hideOperBtn(this)'>");
                        trArray.push("  <td class='acc-num-td'><p>"+ code);
                        //只读权限，不能进行删除、修改
                        if(!isBudgetReadonly) {
                            trArray.push("      <span class='col-operate-btn delete' accId='"+ id +"' accName='"+ name +"' accCode='"+ code +"' onclick='deleteAcc(this)'></span>");
                            trArray.push("      <span class='col-operate-btn modify' accId='"+ id +"' accName='"+ name +"' accCode='"+ code +"' onclick='modifyAcc(this)'></span>");
                        }
                        trArray.push("  </p></td> ");
                        trArray.push("  <td class='acc-name-td'><p>" + name + "</p>");
                        trArray.push("  </td>");
                        trArray.push("  <td class='budget-names-td'><p>"+ financeSubjNames +"</p></td>");
                        trArray.push("</tr>");
                        
                    });
                    
                    $("#accountTableBody").append(trArray.join(""));
                    
                    if (accountId != "" && $("#accountTableBody").find("tr[accId='"+ accountId +"']").length > 0) {
                        $("#accountTableBody").find("tr[accId='"+ accountId +"']").click();
                    } else if ($("#accountTableBody").find("tr").length > 0) {
                        $("#accountTableBody").find("tr").eq(0).click();
                    } else {
                        loadBudgetSubj("");
                        $(".direction").hide();
                    }
                    
                    var fixHelper = function(e, ui) {
			            ui.children().each(function() {  
			                //$(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
			                $(this).height($(this).height());
			            });  
			            return ui;
			        };
				    $("#accountTableBody tbody").sortable({   //这里是talbe tbody，绑定 了sortable   
		                helper: fixHelper,    //调用fixHelper   
		                axis:"y",
		                start:function(e, ui){
		                    return ui;  
		                },
		                stop:function(e, ui){
		                    return ui;
		                }
		            });
		            
				    $("#accountTableBody tbody").on("sortstop", function(event, ui) {
				        var trs = $("#accountTableBody").find("tr");
				        
				        var ids = "";
				        $.each(trs, function(index, item) {
				            var id = $(item).attr("accId");
				            ids += id + ",";
				        });
				        ids = ids.substring(0, ids.length);
				        $.ajax({
				            url: "/accountSubject/modifyAccountSubjSequence",
				            type: "post",
				            dataType: "json",
				            data: {ids: ids},
				            success: function(response) {
				                if (!response.success) {
				                    alert(response.message);
				                    return ;
				                }
				            }
				        });
				    });
                }                
            });
        }
        
        //展示会计科目下的财务科目
        function showMySubj(own, accountId, accSubjName, accSubjCode) {
            $this = $(own);
            $this.siblings("tr").removeClass("selected");
            $this.addClass("selected");
            
            $("#selectedAccId").val(accountId);
            $("#selectedAccName").val(accSubjName);
            $("#selectedAccCode").val(accSubjCode);
            
            //箭头定位
            var index = $this.attr("index");
            var scrollTop = 34 * (index) + 40;
            $(".direction").show();
            $(".direction").animate({"margin-top": scrollTop});
            
            loadBudgetSubj(accountId);
        }
        
        //加载财务科目表格
        function loadBudgetSubj(accountId) {
            $("#budgetSubject").treegrid({
                width: "100%",
                height: "100%",
                loadMsg: "正在加载财务科目..",
                animate: true,
                fitColumns: true,
                url: "/accountSubject/queryAllFinaSubjByAccountId?accountSubjId=" + accountId,
                method: 'get',
                idField: 'financeSubjId',
                treeField: 'financeSubjName',
                singleSelect: false,
                columns: [[
                    {title:'财务科目<span class="static-info" id="staticInfo"></span>', field:'financeSubjName', width:90},
                    {title:'会计科目代码', field:'accSubjCode', width:45},
                    {title:'会计科目', field:'accSubjName', width:45}
                ]],
                rowStyler: function(row){
                    if (!row.canUse) {
                        return "background-color:#F5F5F5; color: #ACA899;";
                    }
                    if(isBudgetReadonly && !row.accSubjCode) {
                    	return "background-color:#fff; color: #000;";
                    }
                    if(row.accSubjCode) {
                    	return "background-color:#a0c8ff; color: #000;";
                    }
                },
                onLoadSuccess: function(row) {
                    loadOwnFincSubj(accountId);
                    loadStatics();
                },
                onClickRow: function(row) {
                	//只读权限，不触发点击事件
                	if(isBudgetReadonly) {
                		return;
                	}
                	
                    //如果当前结点是不可用状态，则不执行任何操作
                    if (!row.canUse) {
                        $("#budgetSubject").treegrid("unselect", row.id);
                        return ;
                    }
                    
                    var accountSubjId = $("#selectedAccId").val();  //当前财务科目ID
                    var financeSubjId = row.financeSubjId;  //当前点击的行的财务科目ID
                    var financeSubjName = row.financeSubjName;
                    if (checkHasSelected(financeSubjId)) {//选中操作
                        addFinaSubjToAccSubj(accountSubjId, financeSubjId, financeSubjName);
                        var children = $("#budgetSubject").treegrid("getChildren", financeSubjId);
	                    $.each(children, function(index, item) {
                            if (!checkHasSelected(item.financeSubjId) && item.canUse) {
	                            $("#budgetSubject").treegrid("select", item.financeSubjId);
	                            addFinaSubjToAccSubj(accountSubjId, item.financeSubjId, item.financeSubjName);
                            }
	                    });
	                    
	                    //判断父节点下的所有子节点是否都被选中，如果都被选中了，则把父节点也选中
	                    checkParent(accountSubjId, financeSubjId);
	                    
                    } else {//取消选中操作
                        removeFinaSubjFromAccSubj(accountSubjId, financeSubjId, financeSubjName);
                        var children = $("#budgetSubject").treegrid('getChildren', financeSubjId);
                        $.each(children, function(index, item) {
                            if (checkHasSelected(item.financeSubjId) && item.canUse) {
	                            $("#budgetSubject").treegrid("unselect", item.financeSubjId);
	                            removeFinaSubjFromAccSubj(accountSubjId, item.financeSubjId, item.financeSubjName);
                            }
                        });
                        
                        //判断父节点是否被选中，如果被选中，则取消选中
                        unCheckParent(accountSubjId, financeSubjId);
                    }
                }
            });
        }
        
        //取消选中父节点
        function unCheckParent(accountSubjId, financeSubjId) {
            var parent = $("#budgetSubject").treegrid("getParent", financeSubjId);
            if (parent) {
                if (checkHasSelected(parent.financeSubjId)) {
                    $("#budgetSubject").treegrid("unselect", parent.financeSubjId);
                    removeFinaSubjFromAccSubj(accountSubjId, parent.financeSubjId, parent.financeSubjName);
                }
                
                unCheckParent(accountSubjId, parent.financeSubjId);
            }
        }
        
        //选中父节点
        function checkParent(accountSubjId, financeSubjId) {
            //判断父节点下的所有子节点是否都被选中，如果都被选中了，则把父节点也选中
            var parent = $("#budgetSubject").treegrid("getParent", financeSubjId);
            if (parent) {
               var parentChildren = $("#budgetSubject").treegrid("getChildren", parent.financeSubjId);
               
               var selectParent = true;
               $.each(parentChildren, function(index, item) {
                   if (!checkHasSelected(item.financeSubjId)) {
                       selectParent = false;
                       return false;
                   }
               });
               
               if (selectParent) {
                   $("#budgetSubject").treegrid("select", parent.financeSubjId);
                   addFinaSubjToAccSubj(accountSubjId, parent.financeSubjId, parent.financeSubjName);
               }
               
               checkParent(accountSubjId, parent.financeSubjId);
            }
        }
        
        //检查树节点是否被选中
        function checkHasSelected(id) {
            var selectOpt = false;
            
            var selectedArray = $("#budgetSubject").treegrid("getSelections");
            $.each(selectedArray, function(index, item) {
                if (id == item.financeSubjId) {
                    selectOpt = true;
                    return false;
                }
            });
            
            return selectOpt;
        }
        
        //向会计科目下添加财务科目
        function addFinaSubjToAccSubj(accountSubjId, financeSubjId, financeSubjName) {
            $.ajax({
                url: "/accountSubject/modifyAccountBudgetMap",
                data: {operateType: 1, accountSubjId: accountSubjId, financeSubjId: financeSubjId},
                dataType: "json",
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return ;
                    }
                    //为对应的会计科目“财务科目”列赋值
                    var $selectedSubjContainter = $("#accountTableBody").find("tr[accId="+ accountSubjId +"]").find(".budget-names-td p");
                    var originalText = $selectedSubjContainter.text();
                    if (originalText != null && originalText != "") {
                        originalText = originalText + ",";
                    }
                    $selectedSubjContainter.text(originalText + financeSubjName);
                    
                    //统计信息
                    var children = $("#budgetSubject").treegrid("getChildren", financeSubjId);
                    if (children.length == 0) {
                        //统计信息
                        $("#staticInfo").text("剩余" + (--unSelectedNum) + "个未选");
                    }
                    
                    //刷新树表行数据
                    $("#budgetSubject").treegrid("update", {
                        id: financeSubjId,
                        row: {
                            accSubjName: $("#selectedAccName").val(),
                            accSubjCode: $("#selectedAccCode").val()
                        }
                    });
                }
            });
        }
        
        //把财务科目从会计科目下移出
        function removeFinaSubjFromAccSubj(accountSubjId, financeSubjId, financeSubjName) {
	        $.ajax({
	            url: "/accountSubject/modifyAccountBudgetMap",
	            data: {operateType: 2, accountSubjId: accountSubjId, financeSubjId: financeSubjId},
	            dataType: "json",
	            success: function(response) {
	                if (!response.success) {
	                    alert(response.message);
	                    return ;
	                }
	                
	                //为对应的会计科目“财务科目”列赋值
                    var selectedArray = $("#budgetSubject").treegrid("getSelections");
                    
                    var selectedNames = "";
		            $.each(selectedArray, function(index, item) {
		                selectedNames += item.financeSubjName + ",";
		            });
		            if (selectedNames != "") {
		              selectedNames = selectedNames.substring(0, selectedNames.length-1);
		            }
		            $("#accountTableBody").find("tr[accId="+ accountSubjId +"]").find(".budget-names-td p").text(selectedNames);
                    
                    //统计信息
                    var children = $("#budgetSubject").treegrid("getChildren", financeSubjId);
                    if (children.length == 0) {
                        //统计信息
                        $("#staticInfo").text("剩余" + (++unSelectedNum) + "个未选");
                    }
                    
                    //刷新树表行数据
                    $("#budgetSubject").treegrid("update", {
                        id: financeSubjId,
                        row: {
                            accSubjName: "",
                            accSubjCode: ""
                        }
                    });
	            }
	        });
        }
        
        //加载尚未被选择的财务科目统计信息
        function loadStatics() {
            $.ajax({
                url: "/accountSubject/countUnSelectedBudgSubj",
                type: "post",
                data: {},
                dataType: "json",
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return ;
                    }
                    
                    unSelectedNum = response.count;
                    $("#staticInfo").text("剩余" + unSelectedNum + "个未选");
                }
            });
        }
        
        //加载属于会计科目自己的财务科目
        function loadOwnFincSubj(accountId) {
            $.ajax({
                url: "/accountSubject/queryOwnFinaSubjByAccountId",
                type: "post",
                data: {accountSubjId: accountId},
                dataType: "json",
                success: function(response) {
                    if (!response.success) {
                        alert(response.message);
                        return ;
                    }
                    
                    $("#budgetSubject").treegrid("unselectAll");
                    
                    var financeSubjList = response.financeSubjList;
                    $.each(financeSubjList, function(index, item) {
                        var financeSubjId = item.id;
                        $("#budgetSubject").treegrid('select', financeSubjId);
                    });
                }
            });
        }
        
        //显示修改/删除按钮
        function showOperBtn(own) {
            var $this = $(own);
            $this.find(".col-operate-btn").show();
        }
        
        //隐藏修改/删除按钮
        function hideOperBtn(own) {
            var $this = $(own);
            $this.find(".col-operate-btn").hide();
        }
        
        //删除会计科目
        function deleteAcc(own) {
            var $this = $(own);
            var id = $this.attr("accId");
            popupPromptBox("提示", "是否确认删除？", function() {
            	
                $.ajax({
                    url: "/accountSubject/deleteAccountSubjectInfo",
                    data: {id: id},
                    dataType: "json",
                    type: "post",
                    success: function(response) {
                        if (!response.success) {
                            alert(response.message);
                            return ;
                        }
                        
                        loadAccountInfo($("#selectedAccId").val());
                    }
                });
            });
            
           event.stopPropagation();
        }
        
        //修改会计科目
        function modifyAcc(own) {
            var $this = $(own);
            
            var id = $this.attr("accId");
            var name = $this.attr("accName");
            var code = $this.attr("accCode");
            
            $("#accountId").val(id);
            $("#name").val(name);
            $("#code").val(code);
            
            showDetailWin();
            event.stopPropagation();
        }
        
        //添加会计科目
        function addAcc() {
            $("#accountId").val("");
            $("#name").val("");
            $("#code").val("");
            
            showDetailWin();
        }
        
        //打开会计科目详情窗口
        function showDetailWin() {
            $("#accountDetailWin").jqxWindow("open");
            $("#code").focus();
        }
        
        //关闭会计科目详情窗口
        function closeDetailWin() {
            $("#accountDetailWin").jqxWindow("close");
        }
        
        //保存会计科目详情信息
        function saveAccountInfo() {
            var id = $("#accountId").val();
            var name = $("#name").val();
            var code = $("#code").val();
            
            if (name == "" || code == "") {
                return ;
            }
            
            $.ajax({
                url: "/accountSubject/saveAccountSubjectInfo",
                data: {id: id, name: name, code: code},
                dataType: "json",
                type: "post",
                success: function(response) {
                    if (!response.success) {
                        $(".error-message").text(response.message);
                        return ;
                    }
                    
                    closeDetailWin();
                    loadAccountInfo(response.id);
                }
            });
        }
        
        //返回费用预算
        function back() {
            window.location.href = "/financeSubject/toFinanceBudgetPage";
        }
        
        //在会计科目面板中点击回车键，自动保存
        function detailWinClick() {
            var key = event.keyCode;
            if (key == 13) {
                saveAccountInfo();
            }
        }