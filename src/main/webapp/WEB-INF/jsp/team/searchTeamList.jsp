<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/kkpager/kkpager_blue.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/team/searchTeamList.css" />
	
    <script type="text/javascript" src="<%=basePath%>/js/kkpager/kkpager.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/team/searchTeamList.js"></script>

  </head>
  
  <body>
    <div class="my-container">
	    <div class="tab-body-wrap">
            <!-- tab键容器 -->
            <div class="btn_tab_wrap">
                <!-- tab键空白处 -->
                <div class="btn_wrap"></div>
                <!-- tab键 -->
                <div class="tab_wrap">
                    <ul>
                        <li id="searchteam" onclick="showTeamInfo()">组讯</li>
                        <li id="searchteam" class="tab_li_current" onclick="showSearchTeam()">寻组</li>
                    </ul>
                </div>                    
            </div>
        </div>
        <div class="community-public searchteam-modal">
			<div class="toolbar" id="toolbar">
				<input type='button' class='advance-search-btn'	id='advanceSearchBtn' onclick='openAdvanceSearch()' title="高级搜索">
				<input type='button' class='delete-btn' id='deleteBtn' onclick='deleteMulSearchTeam()' title="删除">
			</div>

			<!-- 标题头的table -->
         	<div class="title-table-div">
	          <table class='title-table' cellspacing = 0, cellpadding = 0>
	         	<tr>
	         		<td style='width:50px;'><input type='checkbox' id='checkAll' onclick="checkAll(this)"></td>
	         		<td style='width:calc((100% - 50px) / 5);'>姓名</td>
	         		<td style='width:calc((100% - 50px) / 5);'>工作意向</td>
	         		<td style='width:calc((100% - 50px) / 5);'>个人档期</td>
	         		<td style='width:calc((100% - 50px) / 5);'>联系电话</td>
	         		<td style='width:calc((100% - 50px) / 5);'>发布时间</td>
	         	</tr>
	         </table>
        	</div>
        	<div class="search-team-div" id="searchteamDiv">
	            <table class='search-team-list-table' id="searchTeamList" cellspacing = 0, cellpadding = 0>
	            
	            </table>
        	</div>
        	<!-- 表格分页 -->
			<div class="table-page-div">
			   	<div id="kkpager" style="float: right;margin-right: 60px;"></div>
			</div>
        </div>
        
        <div id="queryWindow" class="searchWindow" style="display: none;">
			<div>高级查询</div>
	    	<div class="Popups_box">
	    		<ul>
	    			<li>
	    				<label>意向：</label>
	    				<input type="text" id="searchLikePositionName">
	    			</li>
	    		</ul>
	    		<ul>
	    			<li>
	    				<label>年龄段：</label>
	    				<label><input name="searchAge" value="" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="searchAge" value="1" type="radio"/>4-14岁</label> &nbsp;&nbsp;
						<label><input name="searchAge" value="2" type="radio"/>15-24岁</label> &nbsp;&nbsp;
						<label><input name="searchAge" value="3" type="radio"/>25-34岁</label> &nbsp;&nbsp;
						<label><input name="searchAge" value="4" type="radio"/>35-44岁</label> &nbsp;&nbsp;
						<label><input name="searchAge" value="5" type="radio"/>45岁以上</label>
	    			</li>
	    		</ul>
	    		<ul>
	    			<li>
	    				<label>性别：</label>
	    				<label><input name="searchSex" value="3" type="radio" checked="checked"/>不限</label> &nbsp;&nbsp;
						<label><input name="searchSex" value="1" type="radio"/>男</label> &nbsp;&nbsp;
						<label><input name="searchSex" value="0" type="radio"/>女</label> &nbsp;&nbsp;
	    			</li>
	    		</ul>
	    		<ul class="oper_ul">
					<li>
						<input type='button' value='查询' id='searchSubmit' onclick="querySearchTeam()"/>&nbsp;&nbsp;
						<input type='button' value='关闭' id='closeSearchSubmit' />&nbsp;&nbsp;
						<input type='button' value='清空' id='clearSearchButton' onclick="clearSearchCon()"/>
					</li>
				</ul>
	    	</div>
	    </div>
    </div>
  </body>
</html>
