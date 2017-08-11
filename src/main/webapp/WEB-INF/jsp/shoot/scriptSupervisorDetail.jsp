<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/scriptSupervisorDetail.css" type="text/css"></link>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/shootLog/scriptSupervisorDetail.js"></script>
    
  </head>
  
  <body>
  <input type="hidden" id="noticeId" value="${noticeId}">
  <div style="" id="bodySpliter">
        <div style="" class="setting-left">
            <div class="setting-left-header">
                <input class="return-btn" type="button" title="返回日志列表" onclick="returnShootLogList()">
                <span>现场日志</span>
            </div>
            <ul>
                <!-- <li class="setting-li-current">场记单</li> -->
                <li class="setting-li-current">现场信息</li>
                <li>演员出勤</li>
                <li>特殊道具</li>
                <li>重要备注</li>
            </ul>
            
        </div> 
        <div class="setting-right clip-head-div">
            <span class="current-menu-name">现场信息</span>
            <!--场记单  -->
            <!-- <div class='financeSetting clip-info-div' id="clipInfo" style="display: none;">
                <table>
                    <thead>
                        <tr id="clipInfoTr">
                            <td rowspan="2" style="min-width:100px;">集-场</td>
                            <td rowspan="2" style="min-width:70px;">内外景</td>
                            <td rowspan="2" style="min-width:120px;">拍摄状态</td>
                            <td rowspan="2" style="min-width:50px;">镜号</td>
                            <td rowspan="2" style="min-width:50px;">镜次</td>
                        </tr>
                        <tr id="clipNameTr">
                        
                        </tr>
                    </thead>
                    <tbody id="clipInfoTbody">
                        
                    </tbody>
                </table>
            </div> -->
            
            <!--现场信息  -->
            <div class='financeSetting scene-head-div'>
                <!--基本信息  -->
                <div class="scene-info-div" >基本信息</div>
                <ul>
                    <li>
                        <dl>
                            <dt>拍摄带号：</dt>
                            <dd id="tapNoDD"></dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>拍摄地点：</dt>
                            <dd id="shootLocationDD" class='shoot-sence-dd'></dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>拍摄场景：</dt>
                            <dd id="shootSeneDD" class='shoot-sence-dd'></dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>出发时间：</dt>
                            <dd id="startTimeDD"><dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>到场时间：</dt>
                            <dd id="arriveTimeDD"></dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>开机时间：</dt>
                            <dd id="bootTimeDD"></dd>
                        </dl>
                    </li>
                    <li>
                        <dl>
                            <dt>收工时间：</dt>
                            <dd id="packupTimeDD"></dd>
                        </dl>
                    </li>
                </ul>
                
                <!--转场信息  -->
                <div class="convert-info-div">转场信息</div>
                <table>
                    <thead>
                        <tr>
                            <td>编号</td>
                            <td>转场时间</td>
                            <td>到场时间</td>
                            <td>开机时间</td>
                        </tr>
                    </thead>
                    <tbody id="convertInfoTbody">
                    
                    </tbody>
                </table>
            </div>
            
            <!--演员出勤  -->
            <div class='financeSetting majorrole-head-div'>
                <!--主要演员出勤信息  -->
                <div class="mejorrole-info-div">主要演员出勤信息</div>
                <table>
                    <thead>
                        <tr>
                            <td>演员</td>
                            <td>到场时间</td>
                            <td>收工时间</td>
                        </tr>
                    </thead>
                    <tbody id="majorRoleAttenInfoTbody">
                    
                    </tbody>
                </table>
                
                <!--特约、群众演员出勤信息  -->
                <div class="massrole-info-div" >特约、群众演员出勤信息</div>
                <table>
                    <thead>
                        <tr>
                            <td>演员类别</td>
                            <td>角色</td>
                            <td>人数</td>
                            <td>到场时间</td>
                            <td>收工时间</td>
                        </tr>
                    </thead>
                    <tbody id="massRoleAttenTbody">
                    
                    </tbody>
                </table>
            </div>
            
            <!--部门评分  -->
            <!-- <div class='financeSetting' style="width: 810px;height: 158px;border: 1px solid rgba(213,213,213,0.3);box-shadow: 1px 1px 2px rgba(136,136,136,0.4);padding:30px;display: none;">
                
            </div> -->
            
            <!--特殊道具  -->
            <div class='financeSetting specialprop-head-div'>
                <table>
                    <thead>
                        <tr>
                            <td>名称</td>
                            <td>数量</td>
                            <td>备注</td>
                        </tr>
                    </thead>
                    <tbody id="specialPropTbody">
                    </tbody>
                </table>
            </div>
            
            <!--重要备注  -->
            <div class='financeSetting comment-info-div'>
                <table>
                    <thead>
                        <tr>
                            <!-- <td>时间</td> -->
                            <td>内容</td>
                        </tr>
                    </thead>
                    <tbody id="importCommentTbody">
                    
                    </tbody>
                </table>
            </div>
        </div>
   </div>
   <div id="tipprop" class="sma-tips">
   <div class="arrow-up"></div>
   <div>
     <table>
       <thead><tr><td colspan="2">请选择要删除的机位</td></tr></thead>
       <tbody>
         <tr>
           <td><input type="checkbox"/></td>
           <td>A机</td>
         </tr>
         <tr>
           <td><input type="checkbox"/></td>
           <td>C机</td>
         </tr>
         <tr>
           <td><input type="checkbox"/></td>
           <td>D机</td>
         </tr>
       </tbody>
       <tfoot><tr><td colspan="2"><input type="button" value="删除"/></td></tr></tfoot>
     </table>
   </div>
   </div>
  </body>
</html>
