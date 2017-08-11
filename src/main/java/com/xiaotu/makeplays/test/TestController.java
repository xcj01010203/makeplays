package com.xiaotu.makeplays.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author xuchangjian 2016-9-28下午2:54:23
 */
@Controller
@RequestMapping("/testManager")
public class TestController {

	@RequestMapping("/toTestPage")
	public ModelAndView toTestPage() {
		ModelAndView mv = new ModelAndView("/test/test");
		return mv;
	}
}
