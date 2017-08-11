<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


<link rel="stylesheet"	href="<%=basePath%>/css/bootstrap/css/bootstrap.css" type="text/css">
<link rel="stylesheet"	href="<%=basePath%>/css/notice/optionViewList.css" type="text/css">
<script type="text/javascript" src="<%=basePath%>/js/bootstrap/bootstrap-paginator.js"></script>

<script type="text/javascript"	src="<%=basePath%>/js/notice/optionViewList.js"></script>

</head>
<body>
	<div class="my-container">
		<div class="notice-header">
			<div class="left-header public-header">
				<div class="left-header-top">
					<div class="border-left"></div>
					<span class="header-title">通告场景</span>
				</div>

				<div class="notice-count">
					共 <span>XX</span>场/<span>XX</span>页
				</div>

			</div>
			<div class="right-header public-header">
				<div class="right-header-top">
					<div class="border-left"></div>
					<span class="header-title">备选场景</span>
				</div>

				<div class="notice-count" id="noticeCount">
					<ul>
						<li><a id="breakScenceBtn" href="javascript:void(0);">分场表</a>
						</li>
						<li>|</li>
						<li><a id="planOneBtn" href="javascript:void(0);">计划一</a></li>
						<li>|</li>
						<li><a id="planTwoBtn" href="javascript:void(0);">计划二</a></li>
					</ul>
					<!-- 添加到通告场景 -->
					<input class="add-to-notice-scence" type="button"
						id="addToNoticeScence" value="添加到通告场景">
				</div>



			</div>
		</div>

		<div class="container-body">
			<!-- 通告场景表 -->
			<div class="notice-scene-left">
				<!-- <div id="noticeSceneList"></div> -->
				<table class="break-secent-table" cellspacing=0 cellpadding=0>
					<tr>
						<th>集场</th>
					</tr>
					<tr>
						<td></td>
					</tr>
					<tr>
						<td>3-5</td>
					</tr>
					<tr>
						<td>3-7</td>
					</tr>
					<tr>
						<td>3-8</td>
					</tr>
					<tr>
						<td>3-9</td>
					</tr>
				</table>
			</div>
			<!-- 分场表 -->
			<div class="break-secent-right">
				<!-- <div id="breakSecentList"></div> -->
				<table class="notice-scence-table" cellspacing=0 cellpadding=0>
					<tr>
						<th class="select-all">全选</th>
						<th class="collection-scence">集场</th>
						<th class="air-atmo">气氛</th>
						<th class="scence-th">场景</th>
						<th class="content-th">内容</th>
						<th class="main-actor">主要演员</th>
						<th class="special-actor">特约</th>
						<th class="public-actor">群演</th>
						<th class="cloth-dress-prop">服化道</th>
						<th class="remark-th">备注</th>

					</tr>
					<tr>
						<td><input type="checkbox" id="selectAll"
							onchange="selectAll(this);"></td>
						<td>
							<!-- 集场td --> <span class="select-coll-scence"
							onclick="selectCollScence(this, event)">全部</span>
							<ul class="dropdown_box" id="collScenceDrop">
								<li class="coll-first-li">
									<ul class="drop-content-ul">
										<li><span>集</span> <input type="text" id="collection">
										</li>
										<li><span>场</span> <input type="text" id="scence">
										</li>
									</ul>

								</li>
								<li class="coll-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>

							</ul>
						</td>
						<td>
							<!-- 气氛td --> <span class="select-air-atmo"
							id="selectedAtmosphere" onclick="selectAtmosphere(this, event)">全部</span>
							<ul class="atmo-dropdown-box" id="atmosphereDrop">
								<li class="air-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">日</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">内</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">外</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">黄昏</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">夜晚</a>
										</li>
									</ul>
								</li>
								<li class="air-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul>

						</td>
						<td><span class="select-scence" id="selectScence"
							onclick="selectScence(this, event)">全部</span>
							<ul class="scence-dropdown-box" id="scenceDrop">
								<li class="scence-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">古长城</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">上海大街</a>
										</li>
									</ul>
								</li>
								<li class="scence-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul></td>
						<td>---</td>
						<td><span class="select-main-actor" id="selectMainActor"
							onclick="selectMainActor(this, event)">全部</span>
							<ul class="main-actor-drop-box" id="mainActorDrop">
								<li class="main-actor-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
									</ul>
								</li>
								<li class="main-actor-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul></td>
						<td><span class="select-special-actor"
							id="selectSpecialActor" onclick="selectSpecialActor(this, event)">全部</span>
							<ul class="special-actor-drop-box" id="specialActorDrop">
								<li class="special-actor-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
									</ul>
								</li>
								<li class="special-actor-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul></td>
						<td><span class="select-public-actor" id="selectPublicActor"
							onclick="selectPublicActor(this, event)">全部</span>
							<ul class="public-actor-drop-box" id="publicActorDrop">
								<li class="public-actor-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">张三</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">李四</a>
										</li>
									</ul>
								</li>
								<li class="public-actor-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul></td>
						<td><span class="select-cloth-dress-prop"
							id="selectClothProp" onclick="selectClothProp(this, event)">全部</span>
							<ul class="cloth-prop-drop-box" id="clothPropDrop">
								<li class="cloth-prop-first-li">
									<ul>
										<li class="drop-down-li"><a href="javascript:void(0)">全部</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">ssssssssssssssssssssssssssssssssss</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">xxxxxxxxxxxxxxxxxxxxxxxxxxxxx</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">zzzzzzzzzzzzzzzzzzzzzzzzzzzzzz</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">sssssssssssssssssssssssssssssssss</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">aaaaaa</a>
										</li>
										<li class="drop-down-li"><a href="javascript:void(0)">wwwwwwwwwww</a>
										</li>
									</ul>
								</li>
								<li class="cloth-prop-last-li">
									<div class="select-btn-list">
										<input type="button" value="确定" onclick="">
									</div>
								</li>
							</ul></td>
						<td>---</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>

					</tr>

					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>

					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>

					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>11-11</td>
						<td>日/内</td>
						<td>古长城</td>
						<td class="content-column"><div class="jqx-column">
								1111111111111111111111111111111</div></td>
						<td class="main-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="special-actor"><div class="jqx-column">张王，李钊，林清，刘艺，王菲</div></td>
						<td class="public-actor"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXxXXXXX</div></td>
						<td class="cloth-dress-prop"><div class="jqx-column">XXXXXXXXXXXXXXXXXXXXXXXXXXXX</div></td>
						<td class="remark-column"><div class="jqx-column">SSSSSSSSSSSSSSSSSSSSSSSS</div></td>
					</tr>
				</table>

				<!-- 表格分页 -->
				<div class="table-page-div">
					<div class="span9">
						<div id="tablePage"></div>
					</div>
				</div>




			</div>
		</div>

		<div class="btn-list">
			<input type="button" value="完成">
		</div>

	</div>
</body>
</html>