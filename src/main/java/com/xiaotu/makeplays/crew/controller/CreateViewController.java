package com.xiaotu.makeplays.crew.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.ViewInfoService;
 
@Deprecated
@Controller
@RequestMapping("view")
public class CreateViewController extends BaseController{

	@Autowired
	private AtmosphereService atmosphereService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@RequestMapping("createView")
	public ModelAndView createView(HttpServletRequest request) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		ModelAndView view = new ModelAndView("view/createView");
		
		HttpSession session = request.getSession();
		String crewId = this.getCrewId(request);

		List<AtmosphereInfoModel> atmo = atmosphereService.queryAllByCrewId(crewId);
		
		view.addObject("atmosphere", atmo);
		
		return view;
	}
	
	@RequestMapping("createInputView")
	public ModelAndView createInputView(HttpServletRequest request) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		ModelAndView view = new ModelAndView("view/createInputView");
		
		HttpSession session = request.getSession();
		String crewId = this.getCrewId(request);

		List<AtmosphereInfoModel> atmo = atmosphereService.queryAllByCrewId(crewId);
		
		view.addObject("atmosphere", atmo);
		
		return view;
	}
	
	@RequestMapping("uploadView")
	public @ResponseBody Map uploadViewFile(MultipartFile file, HttpServletRequest request, ModelMap model) throws IOException{
		
		Map resultMap = new HashMap();
		
		String text="";
		
		if(file.getOriginalFilename().endsWith(".docx")){
			StringBuilder builder=new StringBuilder();
			XWPFDocument xwpfd=new XWPFDocument(file.getInputStream());
			List<XWPFParagraph> xwpfpList=xwpfd.getParagraphs();
			if(xwpfpList!=null && xwpfpList.size()>0){
				XWPFParagraph xwpfp=null;
				String line=null;
				for(int p=0;p<xwpfpList.size();p++){
					xwpfp=xwpfpList.get(p);
					line=xwpfp.getText();
					//line=line.replaceAll("", System.getProperty("line.separator"));//将软回车替换为回车符
					builder.append(line);
					builder.append(System.getProperty("line.separator"));
				}
			}
			text=builder.toString();
		}else if(file.getOriginalFilename().endsWith(".doc")){
			
			WordExtractor extractor = new WordExtractor(file.getInputStream());
			
			text = extractor.getText();
			
		}
		
		resultMap.put("status", "success");
		resultMap.put("content", text);
		return resultMap;
	}
	
	
	@RequestMapping("saveView")
	public ModelAndView saveView(HttpServletRequest request, ViewInfoModel viewModel,String viewRoles
			,String actor,String pViewRoles,String props,String fileContent,
			String majorView,String minorView,String thirdLevelView,String propsSpecial) throws Exception{
		
		ModelAndView view = new ModelAndView("view/sucsses");
		
		HttpSession session = request.getSession();
		
		UserInfoModel userInfo =(UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
		
		String crewId = this.getCrewId(request);
		viewModel.setCrewId(crewId);
		//view.setPlayId("123");
		viewInfoService.saveView(fileContent, viewModel, viewRoles, 
				actor, pViewRoles, props, userInfo.getUserId(), 
				userInfo.getUserName(),majorView,minorView,thirdLevelView,propsSpecial);
		
		return view;
	}
	
}
