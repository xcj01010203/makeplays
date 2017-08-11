<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object isCewPictureReadonly = false;
Object obj = session.getAttribute("userAuthMap");

if(obj!=null){
    java.util.Map<String, Object> authCodeMap = (java.util.Map<String, Object>)obj;
    if(authCodeMap.containsKey(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CREWPICTURE)) {
        if((Integer) authCodeMap.get(com.xiaotu.makeplays.utils.AuthorityConstants.PC_CREWPICTURE) == 1){
        	isCewPictureReadonly = true;
        }
    }
}

Object isKeFu = false;

if((Integer) request.getSession().getAttribute("loginUserType") == 4 || (Integer) request.getSession().getAttribute("loginUserType") == 2){
	isKeFu =true;
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=basePath%>/js/jqwidgets/styles/jqx.ui-lightness.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/webuploader.css">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/crewPicture/crewPictureList.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/js-viewer/viewer.css">
<link rel="stylesheet" href="<%=basePath%>/css/exportLoading.css" type="text/css" />
<script type="text/javascript" src="<%=basePath%>/js/webuploader/webuploader.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/js-viewer/viewer.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/crewPicture/crewPictureList.js"></script>
<script>
	    var isKeFu = <%=isKeFu%>;
	    var isCewPictureReadonly = <%=isCewPictureReadonly%>;
	</script>
<title></title>
</head>
<body>
    <div class="my-container">
        <div class="show-container" id="showAlbumList">
            <div class="btn-list">
		            <input type="button" value="上传" onclick="showUploadPicture()">
		            <input  type="button" value="创建分组" onclick="createAlbum()">
		        </div>
		        <div class="upload-img-list">
		            <ul class="group-list" id="fileList">
		            </ul>
		        </div>
        </div>
        
        <!-- 相册详细信息div -->
        <div class="hidden-div" id="hiddenAlbumDetail">
            <input type="hidden" id="nowCrewPictureId"><!-- 当前的剧照相册id -->
            <input type="hidden" id="nowAppackId">
            <div class="album-detail-header">
                <div class="header-btn">
                    <input class="return-btn" type="button" title="返回" onclick="returnMainList()">
                </div>
                <div class="album-nav-div">
                    <div class="cover-picture-div">
                        <img class="cover-picture" id="coverImg" src="" alt='找不到图片'>
                        <p class="set-cover-p" style="display: none;" onclick="setCoverImg()">设置封面</p>
                    </div>
                    
                    <div class="album-operation-div">
                        <div class="album-operation-con">
                            <h1 class="album-name-con" id="albumRealName">我的分组</h1>
		                        <div class="album-operation-btn">
		                            <input type="button" value="上传" onclick="showUploadWin()">
		                            <input type="button" value="删除" onclick="deletePicture()">
		                            <input type="button" value="移动到" onclick="moveTo()"> 
		                            <input type="button" value="下载" onclick="downloadImg()">
		                            <input type="button" value="修改密码" id='updatePicturePassword' onclick="updatePicturePasswordBtn()">
		                            <input type="text" style="display: none">
		                        </div>
                        </div>
                        
                    </div>
                </div>
                <p class="select-all-p"><label><input type="checkbox" id="selectAll" onclick="selectAll(this,event)">全选</label></p>
            </div>
            
            <div class="album-picture-detail">
                <ul class="group-list" id="detailImage">
                    
                </ul>
            </div>
        </div>
        
        <!-- 上传图片窗口 -->
        <div class="jqx-window" id="uploadPictureWin">
            <div>上传照片</div>
            <div class="upload-img-container">
                <!-- <input type="hidden" id="groupId">
                <input type="hidden" id="attpackId"> -->
                <div class="win-header-div">
                    <p class="upload-capition">上传到分组:</p>
                    <input class="drop-down-input" type="text" id="dropDownInput" placeholder = "请选择或输入名称">
                    <div class="select-file-btn" id="uploadFileBtn">选择图片</div>
                </div>
                <!-- 下拉列表 -->
                <div class="drop-list-div" id="dropListDiv">
                    <ul class="drop-list-ul" id="dropListUl">
                        <li onclick="setButtonContent(this)">分组一</li>
                        <li onclick="setButtonContent(this)">分组二</li>
                        <li onclick="setButtonContent(this)">分组三</li>
                    </ul>
                </div>
                <!-- 图片容器 -->
                <div class="win-content-div">
                    <ul class="crew-picture-list" id="crewPictureList"></ul>
                </div>
                <div class="win-btn-list">
                    <input type="button" value="开始上传" onclick="uploadPicture()">
                    <input type="button" value="关闭" onclick="closeUploadWin()">
                </div>
            </div>
        </div>
        
        <!-- 创建剧照命名弹窗 -->
        <div class="jqx-window" id="createAlbumWin">
            <div>新建分组</div>
            <div>
                <p class="album-capition">*请设置分组信息</p>
                <p class="album-content">
                    <span class="album-name-span">分组名称:</span>
                    <input class="album-name-input" type="text" id="albumNameInput">
                </p>
                <p class="album-content">
                    <span class="album-name-span">分组密码:</span>
                    <input class="album-name-input" type="password" id="albumPasswordInput" placeholder="为当前分组设置密码"><input type="text" style="display: none;">
                </p>
                <p class="album-content">
                    <span class="album-name-span">确认密码:</span>
                    <input class="album-name-input" type="password" id="repeatAlbumPasswordInput" placeholder="请再次输入分组密码"><input type="text" style="display: none;">
                </p>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick = "confirmCreateAlbum()">
                    <input type="button" value="取消" onclick = "cancelCreateAlbum()">
                </div>
            </div>
        </div>
        
        <!-- 选择封面弹窗 -->
        <div class="jqx-window" id="selectCoverWin">
            <div>选择封面</div>
            <div class="cover-img-list" id="coverImgList">
                <ul class="cover-img-ul" id="coverImgUl">
                    
                </ul>
            </div>
        </div>
        
        <!-- 移动图片弹窗 -->
        <div class="jqx-window" id="moveToWin">
            <div>移动到</div>
            <div class="move-img-div" id="moveToContent">
                <ul class="move-img-ul" id="moveToUl">
                    
                </ul>
                <div class="win-btn-list">
                    <input type="button" value="移动" onclick="moveToImg()">
                    <input type="button" value="关闭" onclick="closeMoveWin()">
                </div>
            </div>
        </div>
        
         <!-- 输入密码弹窗 -->
        <div class="jqx-window" id="validatePasswordWin">
            <div>安全验证</div>
            <div>
                <p class="album-content">
                    <input class="album-name-input" type="password" id="validatePasswordInput" placeholder="输入当前分组的密码" />
                	<input type="text" style="display: none;">
                </p>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick = "confirmValidatePassword()">
                    <input type="button" value="取消" onclick = "cancelValidatePassword()">
                </div>
            </div>
        </div>
        
        <input type="hidden" id='crewPictureId'>
        <input type="hidden" id='picturePassword'>
        <input type="hidden" id='sourceFrom'>
        <input type="hidden" id='packetId'>
        <input type="hidden" id='packetName'>
        
         <!-- 修改密码弹窗 -->
        <div class="jqx-window" id="updatePasswordWin">
            <div>修改密码</div>
            <div>
            	<p class="album-content">
                    <input class="album-name-input" type="password" id="oldUpdatePasswordInput" placeholder="输入原密码"><input type="text" style="display: none;">
                </p>
                <p class="album-content">
                    <input class="album-name-input" type="password" id="updatePasswordInput" placeholder="输入新的分组密码"><input type="text" style="display: none;">
                </p>
                 <p class="album-content">
                    <input class="album-name-input" type="password" id="repeatUpdatePasswordInput" placeholder="请再次输入新的分组密码"><input type="text" style="display: none;">
                </p>
                <div class="win-btn-list">
                    <input type="button" value="确定" onclick = "confirmUpdatePassword()">
                    <input type="button" value="取消" onclick = "cancelUpdatePassword()">
                </div>
            </div>
        </div>
    </div>
</body>
</html>