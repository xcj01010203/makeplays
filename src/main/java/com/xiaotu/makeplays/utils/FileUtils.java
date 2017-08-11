package com.xiaotu.makeplays.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 文件相关的工具
 * 包含上传、下载、删除、导入、导出、预览、
 * @author xuchangjian
 */
public class FileUtils {

	private final static SimpleDateFormat yyyyMMddFormate = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 上传文件
	 * @param request
	 * @return 文件的名称和存储路径信息，
	 * 当key="fileRealName"时value为上传的文件本来名称，
	 * 当key="fileStoreName"时value为上传的文件存储名称，
	 * 当key="storePath"时，value为文件存储在服务器上的路径
	 * 当key="size"时，value为文件大小
	 * @throws FileUploadException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static final Map<String, String> uploadFile(HttpServletRequest request, String storePath)
			throws FileUploadException, UnsupportedEncodingException,
			FileNotFoundException, IOException {
		String realStorePath = "";
		String fileRealName = "";
		String fileStoreName = "";
		String size = "";
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = multipartRequest.getFileMap();	// 获取上传的文件
		Iterator<String> fileNames = multipartRequest.getFileNames();	// 获取上传文件的名称
		
		// 文件上传目录
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		for(; fileNames.hasNext();) {
			String fileName = (String)fileNames.next();
			MultipartFile file = files.get(fileName);
			byte[] bytes = file.getBytes();
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			if(bytes.length != 0) {
				checkUploadArgument(file, properties);
				
				//文件真实名称
				fileRealName = file.getOriginalFilename();
				String suffix = fileRealName.substring(fileRealName.lastIndexOf("."));
				
				size = file.getSize() + "";
				
				//生成文件存储路径
				realStorePath = genStorepath(storePath);
				
				// 生成文件存储名称
				fileStoreName = UUIDUtils.getId() + suffix;
				
				//上传文件到服务器
				File folder = new File(realStorePath);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				
				File uploadedFile = new File(realStorePath + fileStoreName);
				FileCopyUtils.copy(bytes, uploadedFile);
			}
		}
		
		//保存剧本文件基本信息
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileRealName", fileRealName);
		map.put("fileStoreName", fileStoreName);
		map.put("storePath", realStorePath);
		map.put("size", size);
		
		return map;
	}
	
	/**
	 * 上传文件
	 * 该方法只针对单文件的上传，适配前台的异步上传文件请求
	 * @param request
	 * @param needCheck 是否需要进行文件大小和格式的校验
	 * @param storePath 文件存储路径，不带有文件名
	 * @return 文件的名称和存储路径信息，
	 * 当key="fileRealName"时value为上传的文件本来名称，
	 * 当key="fileStoreName"时value为上传的文件存储名称，
	 * 当key="storePath"时，value为文件存储在服务器上的路径
	 * 当key="size"时，value为文件大小
	 * @throws FileUploadException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static final Map<String, String> uploadFile(MultipartFile file, boolean needCheck, String storePath)
			throws FileUploadException, UnsupportedEncodingException,
			FileNotFoundException, IOException {
		String realStorePath = "";
		String fileRealName = "";
		String fileStoreName = "";
		String size = "";
		
		// 文件上传目录
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		byte[] bytes = file.getBytes();
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		if(bytes.length != 0) {
			if (needCheck) {
				checkUploadArgument(file, properties);
			}
			
			//文件真实名称
			fileRealName = file.getOriginalFilename();
			String suffix = "";
			if (fileRealName.lastIndexOf(".") != -1) {
				suffix = fileRealName.substring(fileRealName.lastIndexOf("."));
			}
			size = file.getSize() + "";
			
			//生成文件存储路径
			realStorePath = genStorepath(storePath);
			
			// 生成文件存储名称
			fileStoreName = UUIDUtils.getId() + suffix;
			
			//上传文件到服务器
			File folder = new File(realStorePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			File uploadedFile = new File(realStorePath + fileStoreName);
			FileCopyUtils.copy(bytes, uploadedFile);
		}
		
		//保存剧本文件基本信息
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileRealName", fileRealName);
		map.put("fileStoreName", fileStoreName);
		map.put("storePath", realStorePath);
		map.put("size", size);
		
		return map;
	}
	
	/**
	 * 对上传的文件格式，大小进行校验，
	 * 通过抛异常的方式表现校验的成功与否
	 * @param fileItem
	 * @param properties
	 */
	private static void checkUploadArgument(MultipartFile file, Properties properties) {
		String fileName = file.getOriginalFilename();
		String maxFileSizeStr = properties.getProperty("fileupload.maxSize");
		
		long maxFileSizeLong = Long.parseLong(maxFileSizeStr);
		long fileSize = file.getSize();
		
		// 对文件后缀进行判断
		String suffix = fileName.substring(fileName.lastIndexOf(".")); // 获取文件后缀名
		if (!".doc".equals(suffix) && !".docx".equals(suffix) && !".xls".equals(suffix) && !".xlsx".equals(suffix)) {
			throw new IllegalArgumentException(
					"请上传doc或docx格式的文档，其他格式的文档暂不支持。");
		}
		//对文件的大小进行判断
		if (fileSize > maxFileSizeLong) {
			throw new IllegalArgumentException(
					"最大只支持上传" + maxFileSizeLong / 1024 / 1024 + "M的文件");
		}
	}
	
	/**
	 * 生成文件预览地址
	 * @param path	文件相对路径
	 * @return
	 * @throws IOException 
	 */
	public static String genPreviewPath(String path) throws IOException {
		//读取配置文件，获得服务器地址
		Resource resource = new ClassPathResource("/config.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		String serverPath = (String) props.get("server.basepath");
		
		String previewPath = serverPath + "/fileManager/previewAttachment?address=" + path;
		
		return previewPath;
	}
	
	/**
	 * 生成文件下载地址
	 * @param path	文件相对路径
	 * @return
	 * @throws IOException 
	 */
	public static String genDownloadPath(String path, String fileName) throws IOException {
		//读取配置文件，获得服务器地址
		Resource resource = new ClassPathResource("/config.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		String serverPath = (String) props.get("server.basepath");
		
		String downloadPath = serverPath + "/fileManager/downloadFileByAddr?address=" + path + "&fileName=" + fileName;
		
		return downloadPath;
	}
	
	/**
	 * 根据配置文件生成文件在服务器上的存储路径
	 * 文件存储路径规则：配置的根路径 + 年月日（yyyyMMdd） + 文件名
	 * @param properties
	 * @return 文件存储的路径
	 */
	public static String genStorepath(String storePath) {
		String dateStr = yyyyMMddFormate.format(new Date());
		
		String absolutePath = storePath + "/"+dateStr + "/";
		return absolutePath;
	}
	
	/**
	 * 下载附件
	 * @param response
	 * @param storePath	附件在服务器上的存储路径
	 * @param fileName	附件名称
	 */
	public static void downloadFile(HttpServletResponse response, String storePath, String fileName) {

        BufferedOutputStream output = null;
        BufferedInputStream input = null;
		try {
        File file =new File(storePath);
        response.reset();
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=" + 
                new String(fileName.getBytes("gb2312"),"iso8859-1"));
        response.setContentLength((int) file.length());

        byte[] buffer = new byte[4096];

         // 写缓冲区：
        
            output = new BufferedOutputStream(response.getOutputStream());
            input = new BufferedInputStream(new FileInputStream(file));

            int n = (-1);
            while ((n = input.read(buffer, 0, 4096)) > -1) {
                output.write(buffer, 0, n);
            }
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("附件下载失败，请重新尝试.", e);
        } finally {
        	try {
        		if (input != null) input.close();
                if (output != null) output.close();
        	} catch (Exception e) {
        		
        	}
        } 
	}
	

	
	/**
	 * 图片附件预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static void viewFile(HttpServletResponse response, String storePath) throws Exception {
        File file =new File(storePath);
        if (file.exists()) {
            IOUtils.write(org.apache.commons.io.FileUtils.readFileToByteArray(file), response.getOutputStream());
        }
        response.flushBuffer();
	}
	
	/**
	 * 绘制缩小后的图片
	 * @param oldImage
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getNewImage(MultipartFile oldImage, File file, double width, double height) throws IOException{  
		/* srcURl 原图地址；deskURL 缩略图地址；comBase 压缩基数；scale 压缩限制(宽/高)比例 */
		InputStream input = null;
		if (oldImage != null) {
			input = new ByteArrayInputStream(oldImage.getBytes());
		}
		if (file != null) {
			input = new FileInputStream(file);
		}
		if (input == null) {
			throw new IllegalArgumentException("请提供需要压缩的图片");
		}
		// ByteArrayInputStream bais = new
		// ByteArrayInputStream(oldImage.getBytes());
		MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(
				input);
		Image src = ImageIO.read(mciis);
		if (src == null) {
			throw new IllegalArgumentException("不支持的文件格式");
		}
		double srcHeight = src.getHeight(null);
		double srcWidth = src.getWidth(null);
		double deskHeight = 0;// 缩略图高
		double deskWidth = 0;// 缩略图宽
		if (srcWidth > srcHeight) {
			if (srcWidth > width) {
				if (width / height > srcWidth / srcHeight) {
					deskHeight = height;
					deskWidth = srcWidth / (srcHeight / height);
				} else {
					deskHeight = width / (srcWidth / srcHeight);
					deskWidth = width;
				}
			} else {

				if (srcHeight > height) {
					deskHeight = height;
					deskWidth = srcWidth / (srcHeight / height);
				} else {
					deskHeight = srcHeight;
					deskWidth = srcWidth;
				}

			}

		} else if (srcHeight > srcWidth) {
			if (srcHeight > (height)) {
				if ((height) / width > srcHeight / srcWidth) {
					deskHeight = srcHeight / (srcWidth / width);
					deskWidth = width;
				} else {
					deskHeight = height;
					deskWidth = (height) / (srcHeight / srcWidth);
				}
			} else {
				if (srcWidth > width) {
					deskHeight = srcHeight / (srcWidth / width);
					deskWidth = width;
				} else {
					deskHeight = srcHeight;
					deskWidth = srcWidth;
				}

			}

		} else if (srcWidth == srcHeight) {

			if (width >= (height) && srcHeight > (height)) {
				deskWidth = (height);
				deskHeight = (height);
			} else if (width <= (height) && srcWidth > width) {
				deskWidth = width;
				deskHeight = width;
			} else if (width == (height) && srcWidth < width) {
				deskWidth = srcWidth;
				deskHeight = srcHeight;
			} else {
				deskHeight = srcHeight;
				deskWidth = srcWidth;
			}

		}
		BufferedImage tag = new BufferedImage((int) deskWidth, (int) deskHeight, BufferedImage.TYPE_3BYTE_BGR);
		tag.getGraphics().drawImage(src, 0, 0, (int) deskWidth, (int) deskHeight, null); // 绘制缩小后的图
		return tag;
    }
	
	/**
	 * 创建文件目录
	 * @param descFile
	 * @return
	 */
	public static boolean makeDir(File descFile) {
		if (!descFile.getParentFile().exists())
		{
			// 如果目标文件所在的目录不存在，则创建父目录
			if (!descFile.getParentFile().mkdirs())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取文件扩展名（包括"."）
	 * @param fileName
	 * @return
	 */
	public static String getExtendName(String fileName)
	{
		if (fileName == null || fileName.equals(""))
		{
			return fileName;
		}
		int dot = fileName.lastIndexOf('.');
		if ((dot > -1) && (dot < (fileName.length() - 1)))
		{
			return fileName.substring(dot);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 把base64编码的字符串转换为图片
	 * @param base64Str base64编码的字符串
	 * @param storePath 文件存储路径
	 * @return 返回存储信息
	 * @throws IOException 
	 */
	public static Map<String, String> saveBase64Img(String storePath, String base64Str) throws IOException {
		Map<String, String> fileMap = new HashMap<String, String>();
		
		byte[] decodedByte = FileUtils.decode(base64Str);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(decodedByte);
		
		String realStorePath = genStorepath(storePath);
		String realStoreName = UUIDUtils.getId() + ".jpg";
		
		File importFile = new File(realStorePath + realStoreName);
		if (!importFile.getParentFile().isDirectory()) {
		    importFile.getParentFile().mkdirs();
		}
		FileOutputStream os = new FileOutputStream(importFile);
		IOUtils.copy(bais, os);
		
		fileMap.put("storePath", realStorePath);
		fileMap.put("fileStoreName", realStoreName);
		
		return fileMap;
	}
	
	/**
	 * Base64解码
	 * @param str
	 * @return
	 */
	public static byte[] decode(String str) {
		byte[] bt = Base64.decodeBase64(str);
		return bt;
	}
	
	/**
	 * base64加密
	 * @param byteArr
	 * @return
	 */
	public static String ecode(byte[] byteArr) {
		String result = Base64.encodeBase64String(byteArr);
		return result;
	}
	
	/**
	 * 删除附件
	 * @param request
	 */
	public static void deleteFile(String storePath) {
		File file = new File(storePath);
		if (file.exists()) {
			file.delete();
		}
	}
	
	
	/**
	 * 是否是图片
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static boolean isPicture(String file){
		boolean flag = false;   
        
		try {
			BufferedImage is = ImageIO.read(new File(file));
			if(is !=null){  
	        	flag = true;  
	        }
		} catch (IOException e) {
		}  
        
        return flag;  
	}

	
	/**
	 * 获取文件大小,单位：字节（k）
	 * @param storePath	文件存储路径
	 * @return
	 */
	public static long getFileSize (String storePath) {
		File file= new File(storePath);
		return file.length() / 1024;
	}
	
	/**
	 * 根据文件流获取文件大小,单位：字节（k）
	 * @param storePath	文件存储路径
	 * @return
	 * @throws IOException 
	 */
	public static long getFileSizeByStream(String storePath) throws IOException {
		File f= new File(storePath);
        FileInputStream fis= new FileInputStream(f);  
        FileChannel fc= fis.getChannel();
        
        return fc.size() / 1024;
	}
	
	
	/**
	 * 上传文件
	 * @param request
	 * @param storePath  存储文件路径
	 * @param newName 存储文件名称
	 * @return
	 * @throws FileUploadException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static final Map<String, String> uploadFileForExcel(HttpServletRequest request, String storePath,String newName)
			throws FileUploadException, UnsupportedEncodingException,
			FileNotFoundException, IOException {
		String realStorePath = "";
		String fileRealName = "";
		String fileStoreName = "";
		String size = "";
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = multipartRequest.getFileMap();	// 获取上传的文件
		Iterator<String> fileNames = multipartRequest.getFileNames();	// 获取上传文件的名称
		
		// 文件上传目录
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		for(; fileNames.hasNext();) {
			String fileName = (String)fileNames.next();
			MultipartFile file = files.get(fileName);
			byte[] bytes = file.getBytes();
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			if(bytes.length != 0) {
				checkUploadArgument(file, properties);
				
				//文件真实名称
				fileRealName = file.getOriginalFilename();
				String suffix = fileRealName.substring(fileRealName.lastIndexOf("."));
				
				size = file.getSize() + "";
				
				//生成文件存储路径
				realStorePath = genStorepath(storePath);
				
				// 生成文件存储名称
				fileStoreName = newName + suffix;
				
				//上传文件到服务器
				File folder = new File(realStorePath);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				
				File uploadedFile = new File(realStorePath + fileStoreName);
				FileCopyUtils.copy(bytes, uploadedFile);
			}
		}
		
		//保存剧本文件基本信息
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileRealName", fileRealName);
		map.put("fileStoreName", fileStoreName);
		map.put("storePath", realStorePath);
		map.put("size", size);
		
		return map;
	}
	
}
