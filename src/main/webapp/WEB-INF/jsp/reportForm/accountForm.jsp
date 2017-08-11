<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <link href="<%=request.getContextPath()%>/js/report/base.css" rel="stylesheet" />
     <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/report.js"></script>
 	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/echarts/echarts.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/costSchedule.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/report/jqmeter.min.js"></script>
<style type="text/css">
	.spanTitle:hover{color:#ff6600;}
	.new_input:HOVER {border:1px solid #409DFE;box-shadow:0 0 3px #409DFE;}
	
	#jqmeter-container{height: 50px;margin:0 auto;margin-left: 86px;}
	.jqx-widget-content-ui-lightness{background-color: #f2f4f6; }
</style>
    <script type="text/javascript">
  //配置echar路径
    require.config({
        paths: {
            echarts: ctx+'/js/echarts'
        }
    });
    var barchar = '${statsType}';
    var goals;//预算
    var raiseds;//支出
    //var authTypeOronly='${authTypeOronly}';//用户权限(读写/只读)
    var zidiyiChild =1;
        $(function(){
        	getStatsCount(1);  //总费用进度
        	var clientWidth=window.screen.availWidth*0.78;
        	/* var clientHeight=window.innerHeight - 290; */
        	$("#statis_charts_day").css("width",clientWidth);  //日支出或日累计支出宽度
        	//$(".bd_wrap").css("height",clientHeight);
        	/* if(authTypeOronly!=1){
        		$(".add_other_subj").remove();
        	} */
        	var statsTypes="${statsType}";
            if(statsTypes==''||statsTypes==undefined|| statsTypes==null){
	           	 statsTypes=1;
	           	 barchar=1;
	        	 $(".subj_custom_title").prop("checked",true);
	        	 getStatsData(1);//初始化
            }else{
            	if(statsTypes==0){
            		$(".subj_custom").prop("checked",true);
	           	 getStatsData(0);//初始化
            	}else{
	            	$(".subj_custom_title").prop("checked",true);
	           	 getStatsData(1);//初始化
            	}
            }
        	 topbarInnerText("进度表&&费用进度");
        	 $(".new_input").blur(function(){
        			$(this).removeClass("input_cur_1");
        		});
        		$(".new_input").focus(function(){
        			$(this).addClass("input_cur_1");
        		});
        		
        		$("#tab_1").click(function(){
    				$(this).addClass("tab_li_current");
    				$(this).siblings().removeClass("tab_li_current");
    				$(".danju").hide();
    				$(".danju_1").show();
    			});
    				
    			$("#tab_2").click(function(){
    				$(this).addClass("tab_li_current");
    				$(this).siblings().removeClass("tab_li_current");
    				$(".danju").hide();
    				$(".danju_2").show();
    				dayTypeline(1);//调用日进度
    			});
            //阻止冒泡
            $('.s_subj_control').click(function(ev){
                ev.stopPropagation();
            })
            //编辑自定义分组
           /*  $('.subj_custom_list a').click(function(){
                showRight($(this).html(),$(this).attr("sid"));
            }) */
            var htmltite="<table style='font-size:12px;line-height:16px;'><tr><td>总费用</td><td></td></tr> <tr><td style='color:#5485bb;'>预算：</td><td style='font-weight:bold;'>"+goals+"</td></tr><tr><td style='color:rgba(139,189,104,1);'>支出：</td><td style='font-weight:bold;'>"+raiseds+"</td></tr></table>"
            $("#jqmeter-container").jqxTooltip({ content: htmltite, position: 'bottom',autoHideDelay: 90000  });
            //自定义分组总费用
            $(".subj_custom").click(function(){
				zidiyiChild=0;                
            	$(this).parent('label').nextAll('label').show();
                barchar=0;
                getStatsData(0);
            });
          //预算科目
            /* $(".subj_custom_title").click(function(){
            	 barchar=type;
            	 $('.subj_custom').parent('label').nextAll('label').hide();
            	 $("span[name="+accountId+"]").nextAll('span').remove();
            	 getStatsData(type,accountId);
            }); */
             if(statsTypes==0){
            	 $(".subj_custom").parent('label').nextAll('label').show();
                 barchar=0;
                 getStatsData(0);//初始化
             }
           
            //lma 自定义科目事件绑定
            $('#jqxTree').jqxTree({theme: theme,height: '200px', hasThreeStates: true, checkboxes: true, width: '350px'});
            $('#jqxTree').css('visibility', 'visible');
           /*  $('#jqxTree').jqxTree('expandAll');
       	 $('#jqxTree').jqxTree('collapseAll'); */
            var sidarr=[];
            $("#popupWindow").jqxWindow({theme:theme,
       		 width: 400, height:380,maxWidth:480,maxHeight:800, resizable: false,  isModal: true, autoOpen: false,//modalOpacity: 0.01           
            });
            //选中或取消事件
            $('#jqxTree').on('checkChange', function (event) {
            		    sidarr = [];
            		    var items = $('#jqxTree').jqxTree('getCheckedItems');
            			for(var i=0;i<items.length;i++){
            				var item=items[i];
            				if(item.hasItems == false){
            					sidarr.push(item.id);
            				}
            			}
            });     
            //确定保存
             	$('#addBtn').click(function(){
                    var groupId = $('.new_input').attr('sid');
                    var groupName = $('.f_l .new_input').val();
                    if(groupName==null || $.trim(groupName)=='') {
                    	showInfoMessage("名称不能为空");
                    	return;
                    }
                    if(groupId == "" && sidarr.length>0) {
                    	$.ajax({
    		                url:'<%=request.getContextPath()%>/financeAccountManager/accountGroupSave'
    				    	,type:'post'
    				    	,dataType:'json'
    		                ,data:'groupName=' + groupName + '&subjectIdStr=' + sidarr
    		                ,async:true
    		                ,success:function(param){
    		            		 if(param.isSuccess == '1') {
    		            			 showSuccessMessage('添加成功!');
    				    	   		//parent.location.reload();
     		            		     getStatsData(0);
     		            		    $("#popupWindow").jqxWindow('close');
    		            		 } else {
    		            			 showInfoMessage('科目名已存在!');
    		            		 }
    		                }
    		            });
                    }else if(groupId == "" && sidarr.length==0){
                    	 showInfoMessage('请选择要定义的科目');
                    }else if(groupId != "" && sidarr.length>0) {
                    	$.ajax({
    		                url:'<%=request.getContextPath()%>/financeAccountManager/accountGroupUpdate'
    				    	,type:'post'
    				    	,dataType:'json'
    		                ,data:'groupId=' +groupId+ '&groupName=' + groupName + '&subjectIdStr=' + sidarr
    		                ,async:true
    		                ,success:function(param){
    		            		 if(param.isSuccess == '1') {
    		            			 showSuccessMessage('修改成功!');
    				    	   		 parent.location.reload();
    		            			getStatsData(0);
    		            			$("#popupWindow").jqxWindow('close');
    		                     }
    		            		 else {
    		            			 showErrorMessage('修改失败!请与管理员联系');
    		            		 }
    		                }
    		            });
                    }else{
                    	showInfoMessage('请选择要定义的科目');
                    }
                });
                 //删除
                $("#delBtn").click(function(){
                    var groupId = $('.f_l .new_input').attr('sid');
                   $("#sid").v
                    //var name = $('iframe').contents().find('.f_l .new_input').val();
                    if(groupId == "") {
                    	 showInfoMessage('此科目组不存在，不能删除');
                    	return;
                    }else{
                    	popupPromptBox("提示","您确定要删除此自定义科目组么?",function(){
	    	                $.ajax({
	    		                url:'<%=request.getContextPath()%>/financeAccountManager/accountGroupDel'
	    				    	,type:'post'
	    				    	,dataType:'json'
	    		                ,data:'groupId=' + groupId
	    		                ,async:true
	    		                ,success:function(param){
	    		            		 if(param.isSuccess == '1') {
	    		            			showSuccessMessage('删除成功!');
	    		            			getStatsData(0);
	    		            			$("#popupWindow").jqxWindow('close');
	    		                     }
	    		            		 else {
	    		            			 showErrorMessage('删除失败!请与管理员联系');
	    		            		 }
	    		                }
	    		            });
                    	});
                    }
                }); 
                 //关闭
                $("#clsBtn").click(function(){
	    		    $("#popupWindow").jqxWindow('close'); 
                }); 
     });
        function getStatsData(statsType,parentId,title) {
        	$(".subj_custom_list").children().remove();
        	$.ajax({
			    url:'<%=request.getContextPath()%>/financeAccountManager/statsList'
			    ,type:'post'
			    ,dataType:'json'
			    ,data:{parentId: parentId,statsType:statsType,zidiyiChild:zidiyiChild}
			    ,async:false
			    ,success:function(param){
			    	var xAxisTitleList = ["预算","支出"];//"支出",,"超支"
			    	var xAxisList = [];
			    	var dataList = [];
			    	var dateList = [];
			    	var day_dataList = [];
			    	var accountList=[];
			    	if(param != null) {
			    		/* var budsum=param.budgetSum;
			    		var playSum=param.palySum; */
			    		if(param.nameList!=null){xAxisList = param.nameList;}
			    		if(param.dataList!=null){dataList = param.dataList;}
			    		if(param.dateList!=null){dateList = param.dateList;}
			    		if(param.day_dataList!=null){day_dataList = param.day_dataList;}
			    		if(param.accountList!=null){accountList = param.accountList;}
			    		//if(barchar!=0 && barchar!=199  && budsum!=null && playSum!=null){
			    		/* if(barchar!=0 && budsum!=null){
			    			getbarchar(budsum,playSum);
			    			getLineChart(dateList, day_dataList, null, '元', 'statis_charts_day');
			    		} */
			    	}
             		/* if(param.financeList!=null && authTypeOronly!=0){ */
             			for(var i=0;i<param.financeList.length;i++){
             				$(".subj_custom_list").append("<a class='spanTitle' style='text-decoration:none;padding-left:10px;vertical-align:baseline;'  href=\"javascript:showRight('"+param.financeList[i].groupName+"','"+param.financeList[i].groupId+"');\" id=\"zidinyi"+i+"\" sid=\""+param.financeList[i].groupId+"\"  style=\"padding-left:3px;\">"+param.financeList[i].groupName+"</a>")
             			}
             		/* } */
             		var tit = "";
             		if(title != undefined){
             			tit = title + '-';
             		}
			    	getBarChart(xAxisList,dataList,/* dataOneList, */  xAxisTitleList, '元', 'statis_charts_fs',accountList,param.childCount,tit+"预算支出概况");
			    	if(zidiyiChild == 1){
			    		$("#finance-daily-expenses").show().css("width",$("#statis_charts_fs").width());
			    		$("#finance-daily-accumulate").show().css("width",$("#statis_charts_fs").width());
			    	
				    	dailyExpenses(1,parentId,'finance-daily-expenses',tit+'日支出');
				    	dailyExpenses(2,parentId,'finance-daily-accumulate',tit+'日累计支出');
			    	}else{
			    		$("#finance-daily-expenses").hide();
			    		$("#finance-daily-accumulate").hide();
			    	}
			    }
			}); 
        }
        function getStatsCount(statsType) {
        	$.ajax({
			    url:'<%=request.getContextPath()%>/financeAccountManager/statsList'
			    ,type:'post'
			    ,dataType:'json'
			    ,data:{statsType:statsType,zidiyiChild:1}
			    ,async:false
			    ,success:function(param){
			    	if(param != null) {
			    		var budsum=param.budgetSum;
			    		var playSum=param.palySum;
			    		//getbarchar(budsum,playSum);
			    		 goals=fmoney(budsum, 2);//预算
			    		 raiseds=fmoney(playSum, 2);//支出
			    	     $('#jqmeter-container').jQMeter({
			 				goal:goals,
			     			raised:raiseds,
			     			width:'75%;',
			     			height:'50px',
			 				bgColor:'#5485bb',
			 				barColor:'rgba(139,189,104,0.5)',
			 			});
			    	}
			    }
			}); 
        }
      //日进度支出、累计支出
        function dayTypeline(typeline) {
      		if(typeline==2){
      			$("#typeline").prop("checked",false);
      			$("#typelines").prop("checked",true);
      		}else{
      			$("#typelines").prop("checked",false);
      			$("#typeline").prop("checked",true);
      		}
              	$.ajax({
      			    url:'<%=request.getContextPath()%>/financeAccountManager/getTypeline'
      			    ,type:'post'
      			    ,dataType:'json'
      			    ,data:{typeline: typeline}
      			    ,async:false
      			    ,success:function(param){
      			    	var dateList = [];
      			    	var day_dataList = [];
      			    	if(param != null) {
      			    		if(param.dateList!=null){dateList = param.dateList;}
      			    		if(param.day_dataList!=null){day_dataList = param.day_dataList;}
      			    	}
      			    	getLineChart(dateList, day_dataList, null, '元', 'statis_charts_day','');
      			    }
      			}); 
          }
      
      //日进度支出、累计支出(按财务科目统计)
        function dailyExpenses(typeline,accountId,id,title) {
      		/* if(typeline==2){
      			$("#typeline").prop("checked",false);
      			$("#typelines").prop("checked",true);
      		}else{
      			$("#typelines").prop("checked",false);
      			$("#typeline").prop("checked",true);
      		} */
              	$.ajax({
      			    url:'<%=request.getContextPath()%>/financeAccountManager/getTypeline'
      			    ,type:'post'
      			    ,dataType:'json'
      			    ,data:{typeline: typeline,accountId:accountId,zidiyiChild:zidiyiChild}
      			    ,async:false
      			    ,success:function(param){
      			    	var dateList = [];
      			    	var day_dataList = [];
      			    	if(param != null) {
      			    		if(param.dateList!=null){dateList = param.dateList;}
      			    		if(param.day_dataList!=null){day_dataList = param.day_dataList;}
      			    	}
      			    	getLineChart(dateList, day_dataList, null, '元', id,title);
      			    }
      			}); 
          }
      
        /**
         * 格式化金额
         * @param s
         * @param n
         * @returns {String}
         */
        function fmoney(s, n)   
        { 
        	var ss = $.trim(s);
        	if(s == null || ss == "" || s==undefined)return "";
           n = n > 0 && n <= 20 ? n : 2;   
           s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
           var l = s.split(".")[0].split("").reverse(),   
           r = s.split(".")[1];   
           t = "";   
           for(var i = 0; i < l.length; i ++ )   
           {   
              t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
           }   
           return t.split("").reverse().join("") + "." + r;   
        }
  //自定义lma
function  custom (){
    $('.f_l .new_input').attr('sid','');
	$('.f_l .new_input').val('');
	$('#jqxTree').jqxTree('uncheckAll');  
    $("#addBtn").jqxButton({theme:theme, width: '70', height: '30'});
    $("#delBtn").jqxButton({theme:theme, width: '70', height: '30'});
    $("#clsBtn").jqxButton({theme:theme, width: '70', height: '30'});
	$("#popupWindow").jqxWindow('open');
}
  //tite头部导行
 function titeSpan(type,accountId,title){
	 zidiyiChild=1; 	  
	 barchar=type;
	 $(".subj_custom_title").prop("checked",true);
	 $('.subj_custom').parent('label').nextAll('label').hide();
	 $("span[name="+accountId+"]").nextAll('span').remove();
	 getStatsData(type,accountId,title);
 }
 //显示右边编辑层
 function showRight(editer,sids){
		custom ();
		$('.f_l .new_input').attr('sid',sids);
		$('.f_l .new_input').val(editer);
 	$.ajax({
         url:'<%=request.getContextPath()%>/financeAccountManager/getInfo'
	    	,type:'post'
	    	,dataType:'json'
         ,data:'groupName='+editer
         ,async:false
         ,success:function(param){
         	//console.log(param.fbmList)
         	for(var i=0;i<param.fbmList.length;i++){
         		var fbmlist=param.fbmList[i];
         		$("#jqxTree").jqxTree('checkItem', $("#"+fbmlist)[0], true); 
         	}
         }
     });
 }

</script>
</head>
<body id="statis_page" class="no_scroll">
    <div id="content" style="margin-left:0px;overflow: auto;">
        <!-- 内容区 begin -->
        <div id="con_box" style="float:none;">
      
     <div class="bd_wrap" style="margin-top: 0px;">
	<!-- <div class="btn_tab_wrap">
    	<div class="btn_wrap"></div>
        <div class="tab_wrap">
        	<ul>
            	<li id="tab_1" class="tab_li_current">分财务科目</li>
                <li id="tab_2">日进度</li>
            </ul>
        </div>
    </div> -->
    
    <!-- <div class=""> -->   <!-- danju danju_1 -->
         <dl  class="fold_body" style="height: 70px;margin-top: 14px;" >
                    <!-- <div class="statis_charts" style="overflow: scroll;overflow: auto; height: 80px;" id="container">
                    </div> -->
					<span style='font-size:14px;float: left; margin-top: 17px; margin-top: 17px; color: #5485bb; font-family: "微软雅黑";'> 总费用进度：</span><div id="jqmeter-container" style=''></div>
                	</dl>
            <dl class="statis_fold" style="top: 1px;border: 1px solid #D7DAE4;">
                <dt class="open fold_head" style="background-color:#ebebeb;" >
                    <!-- <span class="f_l">按财务科目统计</span> -->
                    <div class="s_subj_control f_l">
                        <div class="ml40" style="display: inline;">
                            <input  type="radio" name="s_pace_subj" style='cursor:pointer;' onclick="titeSpan(1,'defultOne');" <c:if test="${statsType != 0}">checked</c:if> class='subj_custom_title'  value="1"/>
                            <span name='defultOne' class='spanTitle' style='cursor:pointer;'  onclick="titeSpan(1,'defultOne');">预算科目</span>
                           
                        </div>
                       <!--  <label>
                            <input type="radio" name="s_pace_subj" value="2"/>
                            <span>二级科目</span>
                        </label> -->
                        <label>
                            <input type="radio" name="s_pace_subj" style='cursor:pointer;vertical-align:baseline;"' <c:if test="${statsType eq 0}">checked</c:if> class="subj_custom" value="0"/>
                            <span class='spanTitle' style="cursor:pointer; vertical-align:baseline;"">自定义科目组</span>
                        </label>
                        <label class="dis_none subj_custom_list">
                           <a></a>
                        		<%-- <c:forEach items="${financeList }" var= "finan" varStatus="index_s">
									<a class='spanTitle' style='text-decoration:none;' href="javascript:void(0)" id="zidinyi${index_s.index }" sid="${finan.groupId}"  style="padding-left:3px;">${finan.groupName}</a>                        			
                        		</c:forEach> --%>
                        </label>
                         <label class="dis_none">
                            <span class="add_other_subj spanTitle" title='添加自定义科目组' style='font-size: 20px;cursor:pointer;vertical-align:baseline;"' onclick="custom();">✚</span>
                        </label>
                    </div>
                </dt>
               		<div style=" overflow: auto;height: 100%;" id="statistic-finance-char">
	                    <div class="statis_charts" style="height: 420px;margin-top: 5px;padding-top: 10px;" id="statis_charts_fs">
	                    </div>
	                    
	                    <div style="height: 420px;margin-top: 20px;padding-top: 10px;padding-bottom: 10px;background: url(../images/tzfw.png) no-repeat;background-position: 10px 400px;" id="finance-daily-expenses">
                		</div>
                		<div style="height: 420px;margin-top: 20px;padding-top: 10px;padding-bottom: 10px;background: url(../images/tzfw.png) no-repeat;background-position: 10px 400px;" id="finance-daily-accumulate">
                		</div>
                	</div>
                	
            </dl>
    <!-- </div> -->
    
     <%-- <div class="danju danju_2" style="display: none;">
                <div style="margin-top: 14px;">  
        	  <form id='form_byDay' method="post" action="">
        		 <label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        			 <input type="hidden"  name="statisticsType"   value="${statisticsType}">
	                	<input type="radio" style='cursor:pointer;' id='typeline' onclick="dayTypeline(1);" checked="checked" />
	                    <span style="color: #1c94c4;cursor:pointer;">日支出</span>
	                </label>
	                <label>&nbsp;&nbsp;
	                		<input type="radio" style='cursor:pointer;' id='typelines' onclick="dayTypeline('2');" />
	                    <span style="color: #1c94c4;cursor:pointer;">日累计支出</span>
	                </label>
	            </form>    
        </div><br/>
                   <div  style="overflow: auto;width: 100%;" >
	                   <div class="statis_charts" style="overflow: auto;" id="statis_charts_day">
	                    </div>
                    </div>
                    
            
           
    </div> --%>
</div> 
     
    </div>
</div>

 <div id="popupWindow" style="display: none;">
	        <div ><span class='tite'>添加/修改自定义科目组</span></div>
	        <div id='' >
	        	<div class="right_add_tb" style="position: relative" >
	   <ul class="clearfix" style="line-height: 35px;">
	       <li class="f_l">
	            <i class="must">*</i>名称：
	       </li>
	       <li class="f_l" style="width: 300px;">
					<input type="text" name="subjName" class="new_input"  value="" sid=""/>
	       </li>
	   </ul>
	</div> 
	        	<div id='jqxTree' style='visibility: hidden; float: left; margin-left: 20px;'>
	          <ul ><br/>
	           <c:forEach items="${fbmList }" var="obj">
	                 <li style='margin-top: 7px;'  id="${obj.accountId}">${obj.accountName}
	                     <ul>
	                    <c:forEach items="${obj.childList }" var="secondObj">
	                         <li id="${secondObj.accountId}">${secondObj.accountName}
	                             <ul>
	                        		<c:forEach items="${secondObj.childList }" var="thirdObj">
	                                 <li id="${thirdObj.accountId}">${thirdObj.accountName}
	                                    <ul>
	                                  		<c:forEach items="${thirdObj.childList }" var='fourObj'>
	                                  			   <li id="${fourObj.accountId}">${fourObj.accountName}</li>
	                                  		</c:forEach>
	                                    </ul>
	                                 </li>
	                             </c:forEach>
	                             </ul>
	                         </li>
	                     </c:forEach>
	                     </ul>
	                 </li>
	             </c:forEach>
	             </ul>
	         </div>
	     		<input type="button" style="margin-top: 20px;margin-left: 60px;" class="" id="addBtn" value="保&nbsp;&nbsp;存">
	          <input type="button" style="margin-left: 30px;" class="" id="delBtn" value="删&nbsp;&nbsp;除">
	          <input type="button" style="margin-left: 30px;" class="" id="clsBtn" value="关&nbsp;&nbsp;闭">
	        </div>
	    </div> 

</body>
</html>