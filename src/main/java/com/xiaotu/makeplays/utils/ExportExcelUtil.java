package com.xiaotu.makeplays.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * 下载excel公共类
 *
 */
public class ExportExcelUtil {

	/**
	 * 下载excel
	 * @param response
	 * @param srcfilePath 模板文件位置
	 * @param destFilePath 生成文件路径文职
	 * @param data 数据
	 * @throws ParsePropertyException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static void downloadExcel( HttpServletResponse response,String srcfilePath,String destFilePath,Map data) throws ParsePropertyException, InvalidFormatException, IOException{
		exportViewToExcelTemplate(srcfilePath, data, destFilePath);
		
		String fileName = destFilePath.substring(destFilePath.lastIndexOf("/")+1);
		
		response.setHeader("Content-Disposition", "attachment;fileName="+ java.net.URLEncoder.encode(fileName,"UTF-8"));
		response.setContentType("application/x-excel");
        response.setCharacterEncoding("UTF-8");
		
        InputStream inputStream=new FileInputStream(destFilePath);
        OutputStream os=response.getOutputStream();  
        byte[] b=new byte[1024];  
        int length;  
        while((length=inputStream.read(b))>0){
            os.write(b,0,length);  
        }
        inputStream.close();  
        os.close();
	}
	
	/**
	 * 生成excel
	 * @param srcFilePath
	 * @param data
	 * @param destFilePath
	 * @throws ParsePropertyException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static void exportViewToExcelTemplate(String srcFilePath,Map data,String destFilePath) throws ParsePropertyException, InvalidFormatException, IOException{
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformXLS(srcFilePath, data, destFilePath);
	}
}
