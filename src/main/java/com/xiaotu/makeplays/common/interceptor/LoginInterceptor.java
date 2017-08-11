package com.xiaotu.makeplays.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.Constants;

public class LoginInterceptor extends HandlerInterceptorAdapter
{
	
	final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception
	{
		HttpSession session = request.getSession();
		UserInfoModel userInfo = (UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
		
		if (userInfo == null) {
			if (request.getHeader("x-requested-with") != null
					&& request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
				response.setHeader("sessionstatus", "timeout");
			} else {
				String basePath =request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath();
				response.sendRedirect(basePath + "/toLoginPage");
			}
			return false;
		}
		return true;
		
//		if(session.getAttribute(Constants.SESSION_USER_INFO) != null){
//			Map<String, Object> mapMenu=(Map<String, Object>) session.getAttribute(Constants.AUTH_NOTMENU);
//			Map<String, Object> finMenu=(Map<String, Object>) session.getAttribute(Constants.SESSION_FINFNCE_PWD);
//			
//			String url=request.getRequestURI();
//			String uri="";
//			if(url.indexOf("?")>0){
//				uri=url.substring(0, url.indexOf("?"));
//			}else{
//				uri=url;
//			}
//			uri = uri.replaceAll("///+", "/"); 
//			
//			if(mapMenu!=null){
//				if(mapMenu.containsKey(uri)){
//					response.sendRedirect("/toIndexPage");
//					return false;
//				}
//			}
//
//			Object fCode=session.getAttribute(Constants.SESSION_FINFNCE_IFENCODE);
//			if(finMenu!=null && fCode==null){
//				if(finMenu.containsKey(uri)){
//					response.sendRedirect("/toIndexPage");
//					return false;
//				}
//			}
//			
//			return true;
//		}else{
//			response.sendRedirect("/toLoginPage");
//			return false;
//		}
		
	}
	
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception
	{
		// logger.info("===========AuthorityInterceptor postHandle");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception
	{
		// logger.info("===========AuthorityInterceptor afterCompletion");
	}
	
}