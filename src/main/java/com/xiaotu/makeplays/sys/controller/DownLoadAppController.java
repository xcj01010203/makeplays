package com.xiaotu.makeplays.sys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 下载APP的controller
 * @author wanrenyi 2016年11月4日上午10:48:51
 */
@Controller
@RequestMapping("/downLoadAppMananger")
public class DownLoadAppController {

	/**
	 * 跳转到App下载页面
	 * @return
	 */
	@RequestMapping("/appIndex/toDownAppPage")
	public ModelAndView toDownAppPage() {
		ModelAndView view = new ModelAndView();
		view.setViewName("/downLoadPage/downLoadPage");
		return view;
	}
}
