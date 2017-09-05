$(document).ready(function () {
	//加载用户权限信息
	loadCrewUserAuthInfo();
});

//加载用户权限信息
function loadCrewUserAuthInfo() {
    $.ajax({
        url: "/userManager/queryMultiUserAuthInfo",
        type: "post",
        dataType: "json",
        data: {userIds: userIds},
        async: true,
        success: function(response) {
            if (response.success) {
                var appAuthList = response.appAuthList;
                var pcAuthList = response.pcAuthList;
                
                /*
                 * 用户权限列表
                 */
                 //PC端权限
                $("#pcAuthList").html("");
                var pcAuthListHtml = [];    //所有的一级权限集合                     
                var isAllCheckedMap={};
                var isShowReadOnlyMap={};
                $.each(pcAuthList, function(findex, fitem) {
                   var firstlvlAuthHtml = [];   //单个第一级权限
                   
                   var fauthId = fitem.authId;
                   var fauthName = fitem.authName;
                   var fhasAuth = fitem.hasAuthStatus;
                   var fdifferInRAndW = fitem.differInRAndW;
                   var freadonly = fitem.readonly;
                   var secondAuthList = fitem.subAuthList;
                   
                   firstlvlAuthHtml.push("<div class='first-level-auth'>");
                   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='group-auth'><input type='checkbox' class='checkbox-group-auth' id='auth_"+fauthId+"' onclick='checkRootAuth(this, false)'>"+ fauthName +"</label>");
                   firstlvlAuthHtml.push("<label class='group-auth'><input type='checkbox' class='checkbox-group-readonly' id='readonly_"+fauthId+"' onclick='checkRootAuth(this, true)'>可编辑</label>");
                   
                   var isShowReadOnly=false;//是否包含可编辑权限
                   var isAllChecked=0;//0:不选中,1：全部选中，2：部分选中
                   var isAllReadOnly=null;//0:可编辑，1：只读，2：部分只读
                   if(fhasAuth!=0){  //是否拥有此权限
                	   isAllChecked=1;
                   }
                   //遍历二级权限
                   $.each(secondAuthList, function(sindex, sitem) {
                       var secondlvlAuthHtml = [];  //单个第二级权限
                   
                       var sauthId = sitem.authId;
                       var sauthName = sitem.authName;
                       var shasAuth = sitem.hasAuthStatus;
                       var sdifferInRAndW = sitem.differInRAndW;
                       var sreadonly = sitem.readonlyStatus;
                       var thirdAuthList = sitem.subAuthList;
                       
                       secondlvlAuthHtml.push("<div class='second-level-auth'>");
                       if (sdifferInRAndW) {//是否区分读写操作
                    	   isShowReadOnly=true;
                       
                          if (shasAuth!=0) {  //是否拥有此权限
                        	  if(shasAuth==1) {//全有
                            	  if(isAllChecked==0){
                            		  isAllChecked=1;
                            	  }
                                  secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                        	  }else{//部分有
                        		  isAllChecked=2;
                        		  secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                        	  }
                              if (sreadonly==1) {  //是否只读
                            	  if(isAllReadOnly==null){
                            		  isAllReadOnly=1;
                            	  }
                            	  if(isAllReadOnly==0) {
                                	  isAllReadOnly=2;
                            	  }
                            	  secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                              } else {
                                  if(sreadonly==0) {
                                	  if(isAllReadOnly==null){
                                		  isAllReadOnly=0;
                                	  }
                                      if(isAllReadOnly==1){
                                    	  isAllReadOnly=2;
                                      }
                                      secondlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                  }else{
                                	  isAllReadOnly=2;
                                      secondlvlAuthHtml.push("<label class='allow-modify-tag selected-part' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                  }
                              }
                          } else {
                        	  if(isAllChecked==1){
                        		  isAllChecked=2;
                        	  }
                              secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                              secondlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                          }
                       } else {
                           if (shasAuth!=0) {  //是否拥有此权限
                        	   if(shasAuth==1) {//全有
                            	   if(isAllChecked==0){
                              		   isAllChecked=1;
                              	   }
                        		   secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                        	   }else{//部分有
                        		   isAllChecked=2;
                        		   secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                        	   }
                           } else {
                         	   if(isAllChecked==1){
                        		   isAllChecked=2;
                        	   }
                               secondlvlAuthHtml.push("<label id='"+ sauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ sauthName +"</label>");
                           }
                       }
                       
                       
                       //遍历三级权限
                       var thirdlvlAuthHtml = [];   //单个三级权限
                       if (shasAuth) {
                           thirdlvlAuthHtml.push("<div class='third-level-auth'>");
                       } else {
                           thirdlvlAuthHtml.push("<div class='third-level-auth' style='display:none;'>");
                       }
                       
                       $.each(thirdAuthList, function(tindex, titem) {
                           
                           var tauthId = titem.authId;
                           var tauthName = titem.authName;
                           var thasAuth = titem.hasAuthStatus;
                           var tdifferInRAndW = titem.differInRAndW;
                           var treadonly = titem.readonlyStatus;
                           
                           
                           if (tdifferInRAndW) {
                        	   isShowReadOnly=true;
                               if (thasAuth!=0) {
                            	   if(thasAuth==1) {
                                	   if(isAllChecked==0){
                                  		   isAllChecked=1;
                                  	   }
                            		   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");   
                            	   }else{
                            		   isAllChecked=2;
                            		   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                            	   }                                   
                                   
                                   if (treadonly==1) {
                                	   if(isAllReadOnly==null){
                                 		  isAllReadOnly=1;
                                 	  }
                                 	  if(isAllReadOnly==0) {
                                     	  isAllReadOnly=2;
                                 	  }
                                       thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                   } else {
                                      if(treadonly==0){
                                   	   if(isAllReadOnly==null){
                                  		  isAllReadOnly=0;
                                  	  }
 	                                      if(isAllReadOnly==1){
                                     	  isAllReadOnly=2;
                                       }
                                    	  thirdlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                      }else{
                                    	  isAllReadOnly=2;
                                    	  thirdlvlAuthHtml.push("<label class='allow-modify-tag selected-part' onclick='changeUserAuth(this, true)'>可编辑</label>");
                                      }
                                       
                                   }
                                   
                               } else {
                            	   if(!isAllReadOnly){
                             		  isAllReadOnly=0;
                             	  }
                                   if(isAllReadOnly==1){
                                 	  isAllReadOnly=2;
                                   }
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                                   thirdlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                               }
                           } else {
                               if (thasAuth!=0) {
                            	   if(thasAuth==1) {
                                	   if(isAllChecked==0){
                                  		   isAllChecked=1;
                                  	   }
                            		   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");   
                            	   }else{
                            		   isAllChecked=2;
                            		   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                            	   }                                   
                               } else {
                             	   if(isAllChecked==1){
                            		   isAllChecked=2;
                            	   }
                                   thirdlvlAuthHtml.push("<label id='"+ tauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ tauthName +"</label>");
                               }
                           }
                           
                       });
                       thirdlvlAuthHtml.push("</div>");
                       
                       secondlvlAuthHtml.push(thirdlvlAuthHtml.join(""));
                       
                       secondlvlAuthHtml.push("</div>");
                       
                       firstlvlAuthHtml.push(secondlvlAuthHtml.join(""));
                   });
                   
                   firstlvlAuthHtml.push("</div>");
                   
                   pcAuthListHtml.push(firstlvlAuthHtml.join(""));
                   
                   isAllCheckedMap["auth_"+fauthId]=isAllChecked;
                   isAllCheckedMap["readonly_"+fauthId]=isAllReadOnly;
                   isShowReadOnlyMap["readonly_"+fauthId]=isShowReadOnly;
                });
                
                $("#pcAuthList").append(pcAuthListHtml.join(""));                        
                for(var id in isAllCheckedMap){
                	var value=isAllCheckedMap[id];
                	if(id.indexOf('auth')!=-1){
                    	if(value==0){
                    		$("#"+id).prop("checked", false);
                    	}else if(value==1){
                    		$("#"+id).prop("checked", true);
                    	}else if(value==2){
                    		$("#"+id).prop("checked", true);
                    		$("#"+id).prop("indeterminate", true);
                    	}
                	}else if(id.indexOf('readonly')!=-1){
                		if(value==0){
                    		$("#"+id).prop("checked", true);
                    	}else if(value==1){
                    		$("#"+id).prop("checked", false);
                    	}else if(value==2){
                    		$("#"+id).prop("checked", true);
                    		$("#"+id).prop("indeterminate", true);
                    	}
                	}
                }
                for(var id in isShowReadOnlyMap){
                	if(!isShowReadOnlyMap[id]){
                		$("#"+id).parent().remove();
                	}
                }
                
                //app端权限,app端只有一级权限
                $("#appAuthList").html("");
                var appAuthListHtml = [];    //所有的一级权限集合
                $.each(appAuthList, function(findex, fitem) {
                   var firstlvlAuthHtml = [];   //单个第一级权限
                   
                   var fauthId = fitem.authId;
                   var fauthName = fitem.authName;
                   var fhasAuth = fitem.hasAuthStatus;
                   var fdifferInRAndW = fitem.differInRAndW;
                   var freadonly = fitem.readonlyStatus;
                   
                   firstlvlAuthHtml.push("<div class='first-level-auth'>");
                   
                   if (fdifferInRAndW) {//是否区分读写操作
                       
                      if (fhasAuth!=0) {  //是否拥有此权限
                    	  if(fhasAuth==1){
                              firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");                    		  
                    	  }else{
                              firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                    	  }
                          
                          if (freadonly==1) {  //是否只读
                            firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                          } else {
                        	  if(freadonly==0){
                                  firstlvlAuthHtml.push("<label class='allow-modify-tag selected' onclick='changeUserAuth(this, true)'>可编辑</label>");
                        	  }else{
                        		  firstlvlAuthHtml.push("<label class='allow-modify-tag selected-part' onclick='changeUserAuth(this, true)'>可编辑</label>");
                        	  }
                          }
                      } else {
                          firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                          firstlvlAuthHtml.push("<label class='allow-modify-tag' onclick='changeUserAuth(this, true)'>可编辑</label>");
                      }
                   } else {
                       if (fhasAuth!=0) {  //是否拥有此权限
                    	   if(fhasAuth==1){
                    		   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");   
                    	   }else{
                    		   firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth selected-part' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                    	   }
                       } else {
                           firstlvlAuthHtml.push("<label id='"+ fauthId +"' class='single-auth' onclick='changeUserAuth(this, false)'>"+ fauthName +"</label>");
                       }
                   }
                   
                   firstlvlAuthHtml.push("</div>");
                   
                   appAuthListHtml.push(firstlvlAuthHtml.join(""));
                });
                
                $("#appAuthList").append(appAuthListHtml.join(""));
                
            } else {
                parent.showErrorMessage(response.message);
            }
        }
    });
}
//own:当前元素    isModify：是否是修改操作
function changeUserAuth(own, isModify) {
    var $this = $(own);
    
    var operateType = 1;    //operateType 操作类型 1：新增  2：修改  3：删除
    var authId = '';
    var readonly = false;
    
    //isModify为true表示是点击后面的“可编辑”文本
    if (isModify) {
        operateType = 2;
        authId = $this.prev(".single-auth").attr("id");
        
        if (!$this.prev(".single-auth").hasClass("selected") && !$this.prev(".single-auth").hasClass("selected-part")) {
            return false;
        }        
        if ($this.hasClass("selected") || $this.hasClass("selected-part")) {
            readonly = true;
        } else {
        	readonly = false;
        }
    } else {
        if ($this.hasClass("selected") || $this.hasClass("selected-part")) {
            operateType = 3;
        } else {
            operateType = 1;
        }
        authId = $this.attr("id");
    }
    
    $.ajax({
        url: "/userManager/saveMultiUserAuthInfo",
        type: "post",
        async: false,
        dataType: "json",
        data: {userIds:userIds, operateType:operateType, authId:authId, readonly:readonly},
        success: function(response) {
            if (!response.success) {
                parent.showErrorMessage(response.message);
                return false;
            }
            
            if ($this.hasClass("selected") || $this.hasClass("selected-part")) {//全有、部分有设为全没有
                $this.removeClass("selected").removeClass("selected-part");
                if (!isModify) {
                    $this.next(".allow-modify-tag").removeClass("selected").removeClass("selected-part");
                    
                    //三级权限隐藏
                    $this.siblings(".third-level-auth").hide();
                    $this.siblings(".third-level-auth").find(".single-auth").removeClass("selected").removeClass("selected-part");
                }
            } else {//全没有、只读设为全有、部分有（根据权限状态决定）
                if (!isModify) {
                	if($this.parent().hasClass("third-level-auth")) {
                		if($this.parent().parent().find(".single-auth").eq(0).hasClass("selected")) {
                			$this.addClass("selected");
                            $this.next(".allow-modify-tag").addClass("selected");
                		}else if($this.parent().parent().find(".single-auth").eq(0).hasClass("selected-part")){
                			$this.addClass("selected-part");
                            $this.next(".allow-modify-tag").addClass("selected-part");
                		}
                	}else{
                        $this.addClass("selected");
                        $this.next(".allow-modify-tag").addClass("selected");
                	}
                    
                    //显示三级权限
                    $this.siblings(".third-level-auth").show();
                }else{
                	if($this.prev(".single-auth").hasClass("selected")){
                    	$this.addClass("selected");
                	} else if($this.prev(".single-auth").hasClass("selected-part")){
                		$this.addClass("selected-part");
                	}
                }
            }
            //设置根权限checkbox
         	var root=null;
            if($this.parent().hasClass("second-level-auth")) {
            	root=$this.parent().parent();
            }
            if($this.parent().hasClass("third-level-auth")) {
            	root=$this.parent().parent().parent();
            }
            if(root) {
            	//获取所有子节点
            	var childs = root.find(".single-auth");
            	var isAllChecked=null;
            	for(var i=0;i<childs.length;i++){
            		if($(childs[i]).hasClass("selected")) {
            			if(isAllChecked==null){
            				isAllChecked=1;
            			}else if(isAllChecked==0){
            				isAllChecked=2;
            				break;            				
            			}
        			}if($(childs[i]).hasClass("selected-part")) {
        				isAllChecked=2;
        				break;
        			}else{
        				if(isAllChecked==null){
            				isAllChecked=0;
            			}else if(isAllChecked==1){
            				isAllChecked=2;
            				break;            				
            			}
        			}
            	}
        		root.find(".checkbox-group-auth").prop("indeterminate", false);
            	if(isAllChecked==0){
            		root.find(".checkbox-group-auth").prop("checked", false);
            	}else if(isAllChecked==1){
            		root.find(".checkbox-group-auth").prop("checked", true);
            	}else if(isAllChecked==2){
            		root.find(".checkbox-group-auth").prop("checked", true);
            		root.find(".checkbox-group-auth").prop("indeterminate", true);
            	}
            }
            //设置只读属性
        	//获取所有二级权限
        	var isAllReadOnly=null;
        	var root=$this.parent().parent();
        	var objs = root.find(".allow-modify-tag");
        	for(var i=0;i<objs.length;i++) {
        		if(($(objs[i]).prev().hasClass('selected') || $(objs[i]).prev().hasClass('selected-part'))) {
        			if($(objs[i]).hasClass('selected')) {
        				if(isAllReadOnly==null){
        					isAllReadOnly=0;
        				}else if(isAllReadOnly==1){
            				isAllReadOnly=2;
            				break;
        				}
        			}if($(objs[i]).hasClass("selected-part")) {
        				isAllReadOnly=2;
        				break;
        			}else{
        				if(isAllReadOnly==null){
        					isAllReadOnly=1;
        				}else if(isAllReadOnly==0){
            				isAllReadOnly=2;
            				break;
        				}
        			}
        		}
        	}
        	root.find(".checkbox-group-readonly").prop("indeterminate", false);
        	if(isAllReadOnly==0){
        		root.find(".checkbox-group-readonly").prop("checked", true);
        	}else if(isAllReadOnly==1){
        		root.find(".checkbox-group-readonly").prop("checked", false);
        	}else if(isAllReadOnly==2){
        		root.find(".checkbox-group-readonly").prop("checked", true);
        		root.find(".checkbox-group-readonly").prop("indeterminate", true);
        	}
        }
    });
}

//根权限全选/取消操作 own:当前元素
function checkRootAuth(own, isModify) {
	var $this = $(own);
	var operateType=1; //operateType 操作类型 1：新增  2：修改  3：删除
	var authId = "";
	if(isModify) {
		authId=$this.parent().prev().attr("id");
	} else {
		authId=$this.parent().attr("id");
	}        	
	var readonly = false; //只读
	//isModify为true表示是点击"可编辑"多选框
    if (isModify) {
    	//判断是否可以设置可编辑全选，如果第二级权限都未选中，则不可以设置可编辑
    	var childs=$this.parent().siblings(".second-level-auth").find(".allow-modify-tag");
    	var isHasReadonly=false;
		for(var i=0;i<childs.length;i++){
			if($(childs[i]).prev().hasClass('selected') || $(childs[i]).prev().hasClass('selected-part')){
				isHasReadonly=true;
				break;
			}
		}
		if(!isHasReadonly){
			$this.prop('checked',false);
			return;
		}
    	
        operateType = 4;
        
        if (!$this.prop('checked')) {
            readonly=true;
        }
        
    } else {//点击根权限
        if ($this.prop('checked')) {
            operateType = 1;
        } else {
            operateType = 3;
        }
    }
	$.ajax({
        url: "/userManager/saveMultiUserAuthInfo",
        type: "post",
        async: false,
        dataType: "json",
        data: {userIds: userIds, operateType: operateType, authId: authId, readonly: readonly},
        success: function(response) {
            if (!response.success) {
                parent.showErrorMessage(response.message);
                return false;
            }
            if(isModify) {//设置可编辑
            	if (!$this.prop('checked')) {
            		//所有子节点去掉可编辑属性
            		var childs=$this.parent().siblings(".second-level-auth").find(".allow-modify-tag");
            		childs.removeClass('selected').removeClass("selected-part");
            	} else {
            		//所有子节点可编辑设为选中
            		var childs=$this.parent().siblings(".second-level-auth").find(".allow-modify-tag");
            		for(var i=0;i<childs.length;i++){
            			if($(childs[i]).prev().hasClass('selected')){
            				$(childs[i]).removeClass("selected-part").addClass('selected');
            			}else if($(childs[i]).prev().hasClass('selected-part')){
            				$(childs[i]).addClass('selected-part');
            			}
            		}
            	}
            } else {
                if (!$this.prop('checked')) {
                	//所有子节点设为未选中
                	var childs=$this.parent().siblings(".second-level-auth").find(".single-auth");
                	childs.removeClass("selected").removeClass("selected-part");
                	//可编辑设为未选中
                	childs.next(".allow-modify-tag").removeClass("selected").removeClass("selected-part");
                    
                    //三级权限隐藏
                    childs.siblings(".third-level-auth").hide();
                    //childs.siblings(".third-level-auth").find(".single-auth").removeClass("selected");
                    
                } else {
                	var childs=$this.parent().siblings(".second-level-auth").find(".single-auth");
                	childs.removeClass("selected-part").addClass("selected");
                	//可编辑设为选中
                	childs.next(".allow-modify-tag").removeClass("selected-part").addClass("selected");
                    
                    //显示三级权限
                    childs.siblings(".third-level-auth").show();
                    childs.siblings(".third-level-auth").find(".single-auth").removeClass("selected-part").addClass("selected");
                }
                $this.parent().next().find('input[type=checkbox]').prop('checked', $this.prop('checked'));
                $this.parent().next().find('input[type=checkbox]').prop('indeterminate', $this.prop('indeterminate'));
            }
        }
    });
}
//切换权限的pc/app视图
function siwtchPlatform(type, own) {
    var $this = $(own);
    //type 1：pc端   2：app端
    if (!$this.hasClass("selected")) {
        $this.addClass("selected");
        $this.siblings("p").removeClass("selected");
        
        if (type == 1) {
            $("#pcAuthList").show();
            $("#appAuthList").hide();
        } else {
            $("#appAuthList").show();
            $("#pcAuthList").hide();
        }
    }
}