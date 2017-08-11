package com.xiaotu.makeplays.utils;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

	 private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);    //日志记录
	 
	    /**
	     * httpPost
	     * @param url  路径
	     * @param jsonParam 参数
	     * @return
	     */
	    public static JSONObject httpPost(String url,JSONObject jsonParam){
	        return httpPost(url, jsonParam, false);
	    }
	 
	    /**
	     * post请求
	     * @param url         url地址
	     * @param jsonParam     参数
	     * @param noNeedResponse    不需要返回结果
	     * @return
	     */
	    public static JSONObject httpPost(String url,JSONObject jsonParam, boolean noNeedResponse){
	        //post请求返回结果
	        HttpClient httpClient = new DefaultHttpClient();
	        JSONObject jsonResult = null;
	        HttpPost method = new HttpPost(url);
	        try {
	            if (null != jsonParam) {
	                //解决中文乱码问题
	                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
	                entity.setContentEncoding("UTF-8");
	                entity.setContentType("application/json");
	                method.setEntity(entity);
	            }
	            HttpResponse result = httpClient.execute(method);
	            url = URLDecoder.decode(url, "UTF-8");
	            /**请求发送成功，并得到响应**/
	            if (result.getStatusLine().getStatusCode() == 200) {
	                String str = "";
	                try {
	                    /**读取服务器返回过来的json字符串数据**/
	                    str = EntityUtils.toString(result.getEntity());
	                    if (noNeedResponse) {
	                        return null;
	                    }
	                    /**把json字符串转换成json对象**/
	                    jsonResult = JSONObject.fromObject(str);
	                } catch (Exception e) {
	                    logger.error("post请求提交失败:" + url, e);
	                }
	            }
	        } catch (IOException e) {
	            logger.error("post请求提交失败:" + url, e);
	        }
	        return jsonResult;
	    }
	 
	 
	    /**
	     * 发送get请求
	     * @param url    路径
	     * @return
	     */
	    public static JSONObject httpGet(String url){
	        //get请求返回结果
	        JSONObject jsonResult = null;
	        try {
	            HttpClient client = new DefaultHttpClient();
	            //发送get请求
	            HttpGet request = new HttpGet(url);
	            HttpResponse response = client.execute(request);
	 
	            /**请求发送成功，并得到响应**/
	            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                /**读取服务器返回过来的json字符串数据**/
	                String strResult = EntityUtils.toString(response.getEntity());
	                /**把json字符串转换成json对象**/
	                jsonResult = JSONObject.fromObject(strResult);
	                //strResult = URLDecoder.decode(strResult, "UTF-8");
	            } else {
	                logger.error("get请求提交失败:" + url);
	            }
	        } catch (Exception e) {
	            logger.error("get请求提交失败:" + url, e);
	        }
	        return jsonResult;
	    }
	    
	    
	    public static String getRemoteHost(HttpServletRequest request){
	        String ip = request.getHeader("x-forwarded-for");
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	            ip = request.getHeader("Proxy-Client-IP");
	        }
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	            ip = request.getHeader("WL-Proxy-Client-IP");
	        }
	        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	            ip = request.getRemoteAddr();
	        }
	        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	    }
	    
	    /**
		 * 获取当前url的操作权限
		 * @return 0：只读，1：写, 2:修改已结算的付款单 或 不可以修改用户权限
		 */
		public static int getAuthorityByUrl(HttpServletRequest request) {
			HttpSession session = request.getSession();
			String url = request.getRequestURI();
			String uri="";
			if(url.indexOf("?")>0){
				uri=url.substring(0, url.indexOf("?"));
			}else{
				uri=url;
			}
			
			Object obj = session.getAttribute(Constants.SESSION_USER_AUTH_MAP);
			/*if(obj != null) {
				List<Map<String,Object>> m = (List<Map<String,Object>>)obj;
				
				for(Map<String, Object> map:m){
					if(uri.trim().equals(map.get("value").toString().trim())){
						return (Integer) map.get("authType");
					}
				}
			}*/
			
			return 1;
		}
		
		/**
		 * 获取特定url的操作权限
		 * @return 0：只读，1：写, 2:修改已结算的付款单 或 不可以修改用户权限,3.不可见
		 */
		public static int getUserAuthBySpecialUrl(HttpServletRequest request, String url) {
			HttpSession session = request.getSession();
			
			Object obj = session.getAttribute(Constants.SESSION_USER_AUTH_MAP);
			if(obj != null) {
				/*List<Map<String,Object>> m = (List<Map<String,Object>>)obj;
				
				for(Map<String, Object> map:m){
					if(url.trim().equals(map.get("value").toString().trim())){
						return (Integer) map.get("authType");
					}
				}*/
			}
			
			return 1;
		}
}
