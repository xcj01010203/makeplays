package com.xiaotu.makeplays.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CommonController {
	

	@RequestMapping("/forward")
	public ModelAndView forward(HttpServletRequest request, ModelAndView mv,
			ModelMap modelMap)
	{
		String path = request.getParameter("path");
		Enumeration<String> nameEnumeration = request.getParameterNames();
		if (nameEnumeration != null)
		{
			while (nameEnumeration.hasMoreElements())
			{
				String key = nameEnumeration.nextElement();
				modelMap.addAttribute(key, request.getParameter(key));
			}
		}
		mv.setViewName(path);
		return mv;
	}
	
}
