package com.xiaotu.makeplays.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 文件管理控制器
 * 请求该类方法时不会被登录页面拦截
 * 因此该类主要用于管理不需要校验session的功能方法
 * @author xuchangjian
 */
@Controller
@RequestMapping("/fileManager")
public class FileController {
	
	Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private CrewInfoService crewInfoService;

	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 根据地址下载文件
	 * @throws IOException 
	 */
	@RequestMapping("/downloadFileByAddr")
	public void downLoadFileByAddr(HttpServletResponse response,String address, String fileName) throws IOException{
		File file = new File(address);
		if(!file.exists()){
			return;
		}
		String name = "";
		if (StringUtils.isBlank(fileName)) {
			name = java.net.URLEncoder.encode(file.getName(),"UTF-8");
		} else {
			name = java.net.URLEncoder.encode(fileName,"UTF-8");
		}
		response.setHeader("Content-Disposition", "attachment;fileName="+ name);
		response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        
        Long len = new File(address).length();
		response.setContentLength(len.intValue());
		
		InputStream inputStream=new FileInputStream(address);
        OutputStream os=response.getOutputStream();  
        byte[] b=new byte[1024];  
        int length;  
        while((length=inputStream.read(b))>0){
            os.write(b,0,length);  
        }
        inputStream.close();  
        os.close();
	}
	
	//预览文件
	@RequestMapping("/previewAttachment")
	public void previewAttachment(HttpServletResponse response,String address){
		try {
			if(!StringUtils.isEmpty(address)){
				FileUtils.viewFile(response, address);
			}
		} catch (Exception e) {
			logger.error("预览附件失败",e);
		}
	}
}
