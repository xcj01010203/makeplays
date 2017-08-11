<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
selecationText = "";
$(document).ready(function () {
	//页面tab初始化
    $("#tabswidget").jqxTabs({theme:theme,  height: '100%', width: '100%' });
	//表单
	$("#register").jqxExpander({theme:theme, toggleMode: 'none', width: '1000px', showArrow: false });
	$('#sendButton').jqxButton({theme:theme, width: 60, height: 25 });
	$(':button').jqxButton({theme:theme, width: 60, height: 25 });
	$('.text-input').jqxInput({theme:theme, width: 300 });

	//$('#editor').jqxEditor({theme:theme,height: "490px",width: '490px',tools:"",pasteMode:"text"});
	
	$('#form').jqxValidator({
	    hintType: 'label',
	    animationDuration: 1,
	 rules: [
	        { input: '#seriesNo', message: '集次不可为空!', action: 'keyup,blur', rule: 'required' },
	        { input: '#viewNo', message: '场次不可为空！', action: 'keyup,blur', rule: 'required' }
	        ]
	});
	
	$('#sendButton').on("click",function(){
		if($('#form').jqxValidator('validate')){
			$('#form').submit();
		}
	});
	
	//异步上传文件-----------------------------------------------------start
	// 创建一个上传参数
	var uploadOption =
	{
	    
	    action: "/view/uploadView",    // 提交目标
	    name: "file",            // 服务端接收的名称
	    autoSubmit: true,        // 是否自动提交
	    
	    // 选择文件之后…
	    onChange: function (file, extension) 
	    {
	    	var fileType = file.split(".")[1];
	    	
	    	if(fileType!="doc"&&fileType!="docx"){
	    		
	    		alert("目前只支持上传word文档！");
	    		
	    		return false;
	    	}
	    	return true;
	        // 选择文件之后，比如校验文件后缀…
	    },
	    
	    // 开始上传文件
	    onSubmit: function (file, extension)
	    {
	    },
	    
	    // 上传完成之后
	    onComplete: function (file, response) 
	    {
	    	var result=response.substring(response.indexOf("{"),response.indexOf("}")+1);
	    	result=JSON.parse(result);
	    	
	    	$('#fileContent').val(result.content);
	    	$('#editorDIV').html("");
	    	$('#editorDIV').append(result.content.replace(/\r\n/g,"<br>")+"");
	    	
	    }
	}

	// 初始化图片上传框
	var au = new AjaxUpload("viewFile", uploadOption);

	// 如果 autoSubmit 为 false，应该要在适当的地方调用提交文件
	au.submit();
	//异步上传文件-----------------------------------------------------end
	
	
	//创建右键菜单-----------------------------------------------------start
	var contextMenu = $("#rightMenu").jqxMenu({ theme:theme,width: '120px', height: '230px', autoOpenPopup: false, mode: 'popup'});
    // open the context menu when the user presses the mouse right button.
    $("#editorDIV").on('mouseup', function (event) {
    	var selecation = '';
    	          if(document.selection){
    	                selecation = document.selection.createRange().text.toString();//ie
    					if(selecation!=""){
    						selecationText=selecation;
    					}else{
							return ;
						}
    	         }else{
    	                selecation =document.getSelection().toString();
				    	if(selecation!=""){
							selecationText=selecation;
						}else{
							return ;
						}
    	         }
    	
        var leftClick = isLeftClick(event) || $.jqx.mobile.isTouchDevice();
        if (leftClick) {
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
            return false;
        }
    });
    // disable the default browser's context menu.
    $(document).on('contextmenu', function (e) {
        return false;
    });
    function isRightClick(event) {
        var rightclick;
        if (!event) var event = window.event;
        if (event.which) rightclick = (event.which == 3);
        else if (event.button) rightclick = (event.button == 2);
        return rightclick;
    }
    function isLeftClick(event) {
        var leftclick;
        if (!event) var event = window.event;
        if (event.which) leftclick = (event.which == 1);
        else if (event.button) leftclick = (event.button == 1);
        return leftclick;
    }
  //创建右键菜单-----------------------------------------------------end
  
  
  //下拉多选框创建----------------------------------------------------start
    var countries = new Array();
    // Create a jqxComboBox
    $("#viewRolesDIV").jqxComboBox({source: countries, multiSelect: true, width: 300, height: 25});           
    
    $("#viewRolesDIV").on('change', function (event) {
    	$("#viewRolesDIV").val("");
        
    });
    
    
	$("#actorDIV").jqxComboBox({ multiSelect: true, width: 300, height: 25});           
    
    $("#actorDIV").on('change', function (event) {
    	$("#actorDIV").val("");
    });
    
    
	$("#pViewRolesDIV").jqxComboBox({ multiSelect: true, width: 300, height: 25});           
    
    $("#pViewRolesDIV").on('change', function (event) {
    	$("#pViewRolesDIV").val("");
        
    });
    
	$("#propsDIV").jqxComboBox({ multiSelect: true, width: 300, height: 25});           
    
    $("#propsDIV").on('change', function (event) {
    	$("#propsDIV").val("");
    });
	$("#propsSpecialDIV").jqxComboBox({ multiSelect: true, width: 300, height: 25});           
    
    $("#propsSpecialDIV").on('change', function (event) {
    	$("#propsSpecialDIV").val("");
    });
  //下拉多选框创建----------------------------------------------------end
  
  
    
  
  
  
});

function setMainContent(){
	$("#mainContent").val( selecationText);	
}

function setMajorView(){
	$("#majorView").val( selecationText);	
}
function setMinorView(){
	$("#minorView").val( selecationText);	
}
function setThirdLevelView(){
	$("#thirdLevelView").val( selecationText);	
}

function setViewRoles(){
	var roles = selecationText.replace(/，/g,",").split(",");
	for(var i=0;i<roles.length;i++){
		if(roles[i]==""){
			continue;
		}
		var item = $("#viewRolesDIV").jqxComboBox('getItemByValue', roles[i]);
		if(typeof(item) == "undefined"){
        	
			$("#viewRolesDIV").jqxComboBox('addItem', roles[i]);	
			$("#viewRolesDIV").jqxComboBox('selectItem', roles[i] );
        }else{
        	$("#viewRolesDIV").jqxComboBox('selectItem', item );
        }
	}
	
	var items = $("#viewRolesDIV").jqxComboBox('getSelectedItems');
	$("#viewRoles").val("");
	var rolesStr = "";
	for(var i=0;i<items.length;i++){
		rolesStr=rolesStr+items[i].value+",";
	}
	
	$("#viewRoles").val(rolesStr.substring(0,rolesStr.length-1));
	
}

function setActor(){
	//debugger;
	var actors = selecationText.replace(/，/g,",").split(",");
	
	for(var i=0;i<actors.length;i++){
		if(actors[i]==""){
			continue;
		}
		var item = $("#actorDIV").jqxComboBox('getItemByValue', actors[i]);
		if(typeof(item) == "undefined"){
        	
			$("#actorDIV").jqxComboBox('addItem', actors[i]);	
			$("#actorDIV").jqxComboBox('selectItem', actors[i] );
        }else{
        	$("#actorDIV").jqxComboBox('selectItem', item );
        }
	}
	
	var items = $("#actorDIV").jqxComboBox('getSelectedItems');
	$("#actor").val("");
	var actorsStr = "";
	for(var i=0;i<items.length;i++){
		actorsStr=actorsStr+items[i].value+",";
	}
	
	$("#actor").val(actorsStr.substring(0,actorsStr.length-1));
}

function setPViewRoles(){
	var pViewRoles = selecationText.replace(/，/g,",").split(",");
	
	
	for(var i=0;i<pViewRoles.length;i++){
		if(pViewRoles[i]==""){
			continue;
		}
		var item = $("#pViewRolesDIV").jqxComboBox('getItemByValue', pViewRoles[i]);
		if(typeof(item) == "undefined"){
        	
			$("#pViewRolesDIV").jqxComboBox('addItem', pViewRoles[i]);	
			$("#pViewRolesDIV").jqxComboBox('selectItem', pViewRoles[i] );
        }else{
        	$("#pViewRolesDIV").jqxComboBox('selectItem', item );
        }
	}
	
	var items = $("#pViewRolesDIV").jqxComboBox('getSelectedItems');
	$("#pViewRoles").val("");
	var pViewRolesStr = "";
	for(var i=0;i<items.length;i++){
		pViewRolesStr=pViewRolesStr+items[i].value+",";
	}
	
	$("#pViewRoles").val(pViewRolesStr.substring(0,pViewRolesStr.length-1));
}

function setprops(){
	var propsArray = selecationText.replace(/，/g,",").split(",");

	for(var i=0;i<propsArray.length;i++){
		if(propsArray[i]==""){
			continue;
		}
		var item = $("#propsDIV").jqxComboBox('getItemByValue', propsArray[i]);
		if(typeof(item) == "undefined"){
        	
			$("#propsDIV").jqxComboBox('addItem', propsArray[i]);	
			$("#propsDIV").jqxComboBox('selectItem', propsArray[i] );
        }else{
        	$("#propsDIV").jqxComboBox('selectItem', item );
        }
	}
	
	var items = $("#propsDIV").jqxComboBox('getSelectedItems');
	$("#props").val("");
	var propsStr = "";
	for(var i=0;i<items.length;i++){
		propsStr=propsStr+items[i].value+",";
	}
	
	$("#props").val(propsStr.substring(0,propsStr.length-1));
}

function setpropsSpecialDIV(){
	var propsSpecialArray = selecationText.replace(/，/g,",").split(",");

	for(var i=0;i<propsSpecialArray.length;i++){
		if(propsSpecialArray[i]==""){
			continue;
		}
		var item = $("#propsSpecialDIV").jqxComboBox('getItemByValue', propsSpecialArray[i]);
		if(typeof(item) == "undefined"){
        	
			$("#propsSpecialDIV").jqxComboBox('addItem', propsSpecialArray[i]);	
			$("#propsSpecialDIV").jqxComboBox('selectItem', propsSpecialArray[i] );
        }else{
        	$("#propsSpecialDIV").jqxComboBox('selectItem', item );
        }
	}
	
	var items = $("#propsSpecialDIV").jqxComboBox('getSelectedItems');
	$("#propsSpecial").val("");
	var propsSpecialStr = "";
	for(var i=0;i<items.length;i++){
		propsSpecialStr=propsSpecialStr+items[i].value+",";
	}
	
	$("#propsSpecial").val(propsSpecialStr.substring(0,propsSpecialStr.length-1));
}

</script>
<style type="text/css">
#editor{ background:#8ac024; }
#rightMenu .jqx-menu-item-top-hover{background-color: #EB6100;color: black;width: 95px;height: 15px;}
</style>
</head>
<body>
<div class="jqx-hideborder jqx-hidescrollbars" id="tabswidget">
	<ul>
	    <li style="margin-left: 50px;" onclick="window.location.href='/view/createView'">上传录入</li>
	    <li style="margin-left: 50px;" onclick="window.location.href='/view/createInputView'">手工录入</li>
	</ul>
	<div id="uploadDIV">
	<div id="register" style=" margin-left: 200px;margin-top:20px">
        <div><h3>创建剧本</h3></div>
        <div>
            <form id="form" action="/view/saveView" method="post">
            <input type="hidden" value="1" name="isManualSave"/>
            <input type="hidden" value="1" name="createWay"/>
                <table class="register-table">
                    <tr>
                        <td align="right">集次:</td>
                        <td><input type="text" name="seriesNo" id="seriesNo" class="text-input" /></td>
                        
                        <td width="60%"> 上传剧本
                        <input type="file" name="viewFile" id="viewFile" />
									</td>
                    </tr>
                    <tr>
                        <td align="right">场次:</td>
                        <td><input type="text" name="viewNo" id="viewNo" class="text-input" /></td>
                        <td align="center">剧本内容</td>
                    </tr>
                    <tr>
                        <td align="right">季节:</td>
                        <td>
                        	<select name="season" id="season">
                        		<option value="">请选择</option>
                        		<option value="1">春</option>
                        		<option value="2">夏</option>
                        		<option value="3">秋</option>
                        		<option value="4">冬</option>
                        	</select>
                        </td>
                        <td rowspan="15">
	                        <div id='content'>
	                        	<div id='editorDIV' style='vertical-align: middle; text-align: left;  line-height:26px; font-size:14px; overflow:auto; background: #d8ffd5; border:1px solid #ccc;height: 600px; width: 600px;'>
						            <div id='rightMenu'>
						                <ul>
						                	<li onclick="setMajorView();">主场景</li>
						                	<li onclick="setMinorView();">次场景</li>
						                	<li onclick="setThirdLevelView();">三级场景</li>
						                    <li onclick="setMainContent();">主要内容</li>
						                    <li onclick="setViewRoles();">主要演员</li>
						                    <li onclick="setActor();">特约演员</li>
						                    <li onclick="setPViewRoles();">群众演员</li>
						                    <li onclick="setprops();">道具
						                        <!-- <ul>
						                            <li><a href="#">Enquiry Form</a></li>
						                            <li><a href="#">Map &amp; Driving Directions</a></li>
						                            <li><a href="#">Your Feedback</a></li>
						                        </ul>
						                         -->
						                    </li>
						                    <li onclick="setpropsSpecialDIV();">个人道具</li>
						                </ul>
						            </div>
						            <div id="editor" style='font-size: 14px; position: relative; top: 180px; font-family: Verdana Arial; '>
						                 </div>
						             
						            <!-- <textarea id="editor" rows="" cols="" onmouseup="alert(window.getSelection());"></textarea> -->
						        </div>
						    </div>
                        	
                        	
                        	<input type="hidden" name="fileContent" id="fileContent"/> 
                        </td>
                    </tr>
                    <tr>
                        <td align="right">气氛:</td>
                        <td><!-- tab_atmosphere_info 气氛表 -->
                        	<select name="atmosphereId" id="atmosphereId">
                        		<option value="">请选择</option>
                        		<c:forEach var="atmo" items="${atmosphere }">
                        			<option value="${atmo.atmosphereId }">${atmo.atmosphereName }</option>
                        		</c:forEach>
                        	</select>
                        </td>
                    </tr>
                    
                    <tr style=" width: 150px; margin-top: 10px; margin-left: 50px;">
                        <td align="right">内外景:</td>
                        <td >
                        	<select name="site" id="site">
                        		<option value="">请选择</option>
                        		<option value="内">内景</option>
                        		<option value="外">外景</option>
                        		<option value="内外">内外景</option>
                        	</select>
                        </td>
                    </tr>
                    <tr style=" width: 150px; margin-top: 10px; margin-left: 50px;">
                        <td align="right">文武戏:</td>
                        <td style=" width: 100px;">
                        <select name="viewType" >
                        	<option value="">请选择</option>
	                        <option value="1">文戏</option>
	                        <option value="2">武戏</option>
	                        <option value="3">文武戏</option>
                        </select></td>
                    </tr>
                    <tr>
                        <td align="right">主场景:</td>
                        <td><input type="text" id="majorView" name="majorView" class="text-input" /></td>
                    </tr>
                    <tr>
                        <td align="right">次场景:</td>
                        <td><input type="text" id="minorView" name="minorView" class="text-input" /></td>
                    </tr>
                    <tr>
                        <td align="right">三级场景:</td>
                        <td><input type="text" id="thirdLevelView" name="thirdLevelView" class="text-input" /></td>
                    </tr>
                    <tr>
                        <td align="right">页数:</td>
                        <td><input type="text" id="pageCount" name="pageCount" class="text-input" /></td>
                    </tr>
                    <tr>
                        <td align="right">主要内容:</td>
                        <td><input type="text" id="mainContent" name="mainContent" class="text-input" /></td>
                    </tr>
                    <tr>
                        <td align="right">主要演员:</td>
                        <td>
                        <div class="" id="viewRolesDIV" ></div>
                        <input type="hidden" id="viewRoles" name="viewRoles" /></td>
                    </tr>
                    <tr>
                        <td align="right">特约演员:</td>
                        <td>
                        <div class="" id="actorDIV" ></div>
                        <input type="hidden" id="actor" name="actor" /></td>
                    </tr>
                    <tr>
                        <td align="right">群众演员:</td>
                        <td>
                        <div id="pViewRolesDIV"></div>
                        <input type="hidden" id="pViewRoles" name="pViewRoles" /></td>
                    </tr>
                    <tr>
                        <td align="right">道具:</td>
                        <td>
                        <div id="propsDIV"></div>
                        <input type="hidden" id="props" name="props" /></td>
                    </tr>
                    <tr>
                        <td align="right">个人道具:</td>
                        <td>
                        <div id="propsSpecialDIV"></div>
                        <input type="hidden" id="propsSpecial" name="propsSpecial" /></td>
                    </tr>
                    <tr>
                        <td align="right">备注:</td>
                        <td>
                        <input type="text" id="remark" name="remark" class="text-input"/></td>
                    </tr>
                    <tr>
                        <td colspan="3" style="text-align: center;">
                        <input type="button" value="保存" id="sendButton" />
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
  </div>
  <div id="inputDIV">
  
  </div>
</div>
                        
</body>
