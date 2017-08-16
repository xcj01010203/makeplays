package com.xiaotu.makeplays.common.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xiaotu.makeplays.common.service.UserAuthFilterService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.Constants;

/**
 * @类名：UserAuthFilter.java
 * @作者：李晓平
 * @时间：2017年8月8日 下午4:18:03
 * @描述：用户权限过滤
 */
@Component
public class UserAuthFilter extends OncePerRequestFilter {

	@Autowired
	UserAuthFilterService userAuthFilterService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// 不过滤的url
		String[] notFilter = new String[] {"/toLoginPage", "/toIndexPage", "toRestrictedPage"};
		// 是否过滤
		boolean doFilter = true;
		// 请求的url，path兼容新版框架
		String url = request.getRequestURI();
		String path = request.getParameter("path");

		String validateUrl = url;
		if (path != null) {
			validateUrl = path;
		}
		if(validateUrl != null) {
			for (String one : notFilter) {
				if (url.indexOf(one) != -1) {
					// 如果url中包含不过滤的url，则不进行过滤
					doFilter = false;
					break;
				}
			}
		}
		if (doFilter) { // 如果不包含，执行过滤	
			HttpSession session = request.getSession();
			UserInfoModel userInfo = (UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
			CrewInfoModel crewInfo = (CrewInfoModel) session.getAttribute(Constants.SESSION_CREW_INFO);
			String ifCheck = (String) session.getAttribute(Constants.SESSION_IFCHECK);
			Integer loginUserType = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_TYPE);
			String roleId = (String) session.getAttribute(Constants.SESSION_LOGIN_SERVICE_TYPE);
			if(userInfo != null) {

				String userId = userInfo.getUserId();
				String crewId = "";
				if(crewInfo != null) {
					crewId = crewInfo.getCrewId();
				}
//				if(loginUserType == 1) {//系统管理员，没有默认剧组
//					crewId = "";
//				}
				
				if("OK".equals(ifCheck)) {
					try {
						userAuthFilterService.setFuncPermitList(userId, crewId, loginUserType, roleId);
						session.setAttribute(Constants.SESSION_IFCHECK, "NO");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				boolean isRegisted = UserAuthFilterService.checkAuthRegistered(url, path);
				
				if(isRegisted) { // 如果url已注册，执行过滤
					//用户是否有权限访问
					boolean hasAuth = UserAuthFilterService.checkAuthControl(crewId, userId, url, path);
					if(hasAuth) {
						// 如果有访问权，则继续
						filterChain.doFilter(request, response);
					} else {
						// 如果没有访问权，则转到失败页面
						request.setCharacterEncoding("UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setContentType("text/html;charset=UTF-8");
						PrintWriter out = response.getWriter();
						String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath();
						String loginPage = basePath + "/toRestrictedPage";
						StringBuilder builder = new StringBuilder();
						builder.append("<script charset=\"utf-8\" language=\"javascript\" type=\"text/javascript\">");
						builder.append("window.location='");
						builder.append(loginPage);
						builder.append("';");
						builder.append("</script>");
						out.print(builder.toString());
					}
				} else {
					// 如果没注册，则继续
					filterChain.doFilter(request, response);
				}
			} else {
				// 如果没注册，则继续
				filterChain.doFilter(request, response);
			}
		} else {
			// 如果不过滤 ，则继续
			filterChain.doFilter(request, response);
		}
	}
}
