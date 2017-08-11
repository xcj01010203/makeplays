<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Object isContactReadonly = false;
Object hasImportContactAuth = false;
Object hasExportContactAuth = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.CREW_CONTACT_WEB)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.CREW_CONTACT_WEB) == 1){
        	isContactReadonly = true;
        }
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.IMPORT_CREW_CONTACT) != null){
    	hasImportContactAuth = true;
    }
    if(authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.EXPORT_CREW_CONTACT) != null){
    	hasExportContactAuth = true;
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript">
        var basePath = "<%=basePath %>";
    </script>
  
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxwindow.js"></script>
	<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/js/jqwidgets/jqxcore.js"></script> --%>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/easy-ui/datagrid-dnd.js"></script>
	
	<!-- bootstrap JS -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>

	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.css" type="text/css" />
	<script src="<%=request.getContextPath()%>/js/UI-Checkbox-master/checkbox.min.js"></script>
	
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/easy-ui/easyui.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/easy-ui/icon.css" type="text/css"></link>
	
    <!-- bootstrap CSS -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css">
    
	<!-- 加载jsp页面自己的css和js代码 -->
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/crewContact/crewContactList.css" type="text/css"></link>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/numberToCapital.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/crewContact/crewContactList.js"></script>
	<script>
	    var isContactReadonly = <%=isContactReadonly%>;
	    var hasImportContactAuth = <%=hasImportContactAuth%>;
	    var hasExportContactAuth = <%=hasExportContactAuth%>;
	</script>
</head>
<body>
 <div id="main-table">
 	<div class='contact-header'>
 		<div class="contact-header-div">
			<a class="add-contact add-contact-a" title='添加剧组成员'  href='javascript:addPeson();'></a>
			<a class="import-contact add-contact-a" title='导入'  href='javascript:showImportWin();'></a>
            <a class="export-contact add-contact-a" title='导出'  href='javascript:exportContact()'></a>
			
			<div class="search-contact-div">
				<div class="search_box search-box-div">
					<input type="text" class='search_kuang' onkeyup="searchName(this);"   placeholder='请输入要查询的姓名'/>
					<div class='icon_cha1'>
						<img src="../images/find.png">
					</div>
				</div>
				<div class="super-search-div" onclick="searchContact();">
				高级查询
				</div>
			</div>
			
		</div>
 	</div>
 	<div class='contact-body'>
 		<table id="contact"></table>
 	</div>
 </div>
 
  <!-- 搜索窗口 -->
 <div class="hidden-tag" id="searchWindow">
 	<div>按条件搜索剧组联系表成员</div>
 	<div>
 		<table class="_table" style="position: absolute;">
 			<tr>
 				<th>姓名：</th>
 				<td><input type="text" name="contactName-search" id="contactName-search" class="text-input" value=""/></td>
 				<th>性别：</th>
 				<td><input type="radio" name="sex-search" class="" value="" checked/>&nbsp;全部
 				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 				<input type="radio" name="sex-search" class="" value="1"/>&nbsp;男
 				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 				<input type="radio" name="sex-search" id="sex-search" class="" value="0"/>&nbsp;女</td>
 			</tr>
 			<tr>
 				<th>部门：</th>
 				<td>
 				<select id="department-search" class="selectpicker show-tick" multiple data-live-search="true" onchange="changeSearchDepartment()">
											
				</select>
				<input type="hidden" class="preValue" />
				<a class="clearSelection clear-style">清空</a>
 				</td>
 				<th>职务：</th>
 				<td>
 				<select id="duties-search" class="selectpicker show-tick" multiple data-live-search="true">
											
				</select>
				<input type="hidden" class="preValue" />
				<a class="clearSelection clear-style">清空</a>
 				</td>
 			</tr>
 			<tr>
 				<th>入组日期：</th>
 				<td><input type="text" name="enterDate-search" id="enterDate-search" class="text-input" value="" onFocus="WdatePicker({readOnly:true})"/></td>
 				<th>离组日期：</th>
 				<td><input type="text" name="leaveDate-search" id="leaveDate-search" class="text-input" value="" onFocus="WdatePicker({readOnly:true})"/></td>
 			</tr>
 			<tr>
 				<th>餐别：</th>
 				<td>
 					<input type="radio" name="mealType-search" value='' checked/>
	 				&nbsp;全部
	 				&nbsp;&nbsp;
	 				<input type="radio" name="mealType-search" value="1"/>
	 				&nbsp;常规
	 				&nbsp;&nbsp;
	 				<input type="radio" name="mealType-search" id="mealType-search" value="2"/>
	 				&nbsp;清真
	 				&nbsp;&nbsp;
	 				<input type="radio" name="mealType-search" id="" value="3"/>
	 				&nbsp;素餐&nbsp;&nbsp;
	 				<input type="radio" name="mealType-search" id="" value="4"/>
	 				&nbsp;特餐
	 				</td>
 				<th>宾馆：</th>
 				<td>
 					<input type="text" name="hotel-search" id="hotel-search" class="text-input"/>
 				</td>
 			</tr>
 		</table>
 		<table class="_table table-button">
 			<tr>
 				<td style="text-align: center;">
	 				<input type="button" value="确定" id='contact-btn-search-sure' onclick="searchContactButton()">
	 				<input class="close-search-button" type="button" value="取消" id='contact-btn-search-close'/>
 				</td>
 			</tr>
 			
 		</table>
 	</div>
 </div>
    <!--导入窗口  -->
	<div id="importContactWin" class="jqx-window">
		<div>导入</div>
		<div>
		    <iframe id="importIframe" width="100%" height="100%"></iframe>
		</div>
	</div>
	
	<!-- 导出剧组联系表  -->
	<div id='exportContactDiv' style="display: none;">
	
	</div>
	
	<!-- 滑动窗口 -->
  <div class="right-popup-win" id="rightPopUpWin">
       <input type="hidden" id="needContactId">
       <div class="popup-win-btn">
          <input type="button" value="确定" id='contact-btn-sure' onclick="confirmSaveContact()">
          <input class="table-button-style" type="button" value="删除" id='contact-btn-delete' onclick="confirmDelContatc()">
          <input class="table-button-style" type="button" value="关闭" id='contact-btn-close' onclick="closeContactDetailInfo()"/>
       </div>
	     <div class="right-content-div">
           <!-- 联系人信息 -->
           <div class="public-contact contact-info-div">
               <!-- <iframe  id="contactIframe" style="width: 100%; height: 100%;" scrolling="auto"></iframe> -->
               <form id="form">
					    <input type="hidden" name="contactId" id="contactId" class="text-input" value=""/>
					    <table class="_table">
					      <tr>
					        <td style="font-size:14px; font-weight: bold;">基本信息：</td>
					      </tr>
					      <tr>
					        <th><em class="must">*</em>姓名：</th>
					        <td><input type="text" name="contactName" id="contactName" class="text-input" value=""/></td>
					        <th>性别：</th>
					        <td><input type="radio" name="sex" class="" value="1" checked/>&nbsp;男
					        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					        <input type="radio" name="sex" id="sex" class="" value="0"/>&nbsp;女</td>
					      </tr>
					      <tr>
					        <th><em class="must">*</em>手机号：</th>
					        <td><input type="text" name="phone" id="phone" class="text-input" value=""/></td>
					        <th><em class="must">*</em>证件类型：</th>
					                <td>
					                    <select class="text-input" name="identityCardType" id="identityCardType">
					                        <option value="1">身份证</option>
					                        <option value="2">护照</option>
					                        <option value="3">台胞证</option>
					                        <option value="4">军官证</option>
					                        <option value="5">其他</option>
					                    </select>
					                </td>
					      </tr>
					      <tr>
					        <th><em class="must">*</em>部门：</th>
					        <td>
					        <select id="department" class="selectpicker show-tick" multiple data-live-search="true" onchange="changeDepartment()">
					                      
					        </select>
					        <input type="hidden" class="preValue" />
					        </td>
					        
					        <th><em class="must">*</em>证件号码：</th>
					                <td><input type="text" name="identityCardNumber" id="idNumber" class="text-input" value=""/></td>
					      </tr>
					      <tr>
					          <th><em class="must">*</em>职务：</th>
					                <td>
					                <select id="duties" class="selectpicker show-tick" multiple data-live-search="true" ></select>
					                </td>
					        <th><em class="must">*</em>入组日期：</th>
					        <td>
					          <input type="text" name="enterDate" id="enterDate" class="text-input" value="" onFocus="WdatePicker({isShowClear:false,readOnly:true})"/>
					        </td>
					      </tr>
					      <tr>
					        <th>备注：</th>
					        <td><input type="text" name="remark" id="remark" class="text-input" value=""/></td>
					        <th><em class="must">*</em>离组日期：</th>
					                <td>
					                    <input type="text" name="leaveDate" id="leaveDate" class="text-input" value="" onFocus="WdatePicker({isShowClear:false,readOnly:true})"/>
					                </td>
					      </tr>
					      <tr>
					        
					        <th>餐别：</th>
					        <td>
					          <input type="radio" name="mealType" value="1" checked/>
					          &nbsp;常规
					          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					          <input type="radio" name="mealType" id="" value="2"/>
					          &nbsp;清真
					          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					          <input type="radio" name="mealType" id="" value="3"/>
					          &nbsp;素餐&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					          <input type="radio" name="mealType" id="" value="4"/>
					          &nbsp;特餐
					          </td>
					          <th class="open-contact-th">公开到组：</th>
					        <td>
					          <input type="radio" name="ifOpen" id="" class="" value="1" checked/>
					          &nbsp;是
					          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					          <input type="radio" name="ifOpen" id="ifOpen" class="" value="0"/>
					          &nbsp;否
					        </td>
					      </tr>
					    </table>
					</form>
           </div>
                
	     </div>
	</div> 
	
	
	<!-- 提示信息 -->
       <div id="eventAll" style="display: none;">
             <div>
                       提示
             </div>
             <div>
                <div style="margin-top: 25px;font-size: 16px;margin-left: 10px;" id="eventContent">
                              是否确定此操作？
                </div>
                <div>
                  <div style=" margin: 30px 0px 0px 60px;">
                     <input type="button" id="sure" value="确定" style="margin-right: 10px;" />
                     <input type="button" id="closeBtn" value="取消" />
                  </div>
                </div>
              </div>
       </div>
	
</body>

</html>