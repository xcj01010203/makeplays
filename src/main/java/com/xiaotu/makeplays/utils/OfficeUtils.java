package com.xiaotu.makeplays.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;

public class OfficeUtils {
	
	private final static SimpleDateFormat yyyyMMddFormate = new SimpleDateFormat("yyyyMMdd");
	
	public static final String lineSeparator = "\r\n";

	/**
	 * 读取word文件内容
	 * 
	 * @param fileStorePath
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static String readWordFile(String fileStorePath) throws IOException,
			XmlException, OpenXML4JException {
		String text = null;
		String ext = fileStorePath.substring(fileStorePath.lastIndexOf("."));
		if (ext.equalsIgnoreCase(".doc")) {
			text = readDocFile(fileStorePath);
		} else if (ext.equalsIgnoreCase(".docx")) {
			text = readDocxFile(fileStorePath);
		} else {
			throw new IllegalArgumentException("不支持的文件格式");
		}
		return text;
	}

	/**
	 * 读取.docx格式的word文件
	 * 
	 * @param filePath
	 * @return
	 * @throws XmlException
	 * @throws OpenXML4JException
	 * @throws IOException
	 */
	public static String readDocxFile(String filePath) throws XmlException,
			OpenXML4JException, IOException {
		String text = "";
		
		try {
			text = readFromConvertedTxt(filePath);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder builder=new StringBuilder();
			FileInputStream in = new FileInputStream(new File(filePath));
			XWPFDocument xwpfd=new XWPFDocument(in);
			List<XWPFParagraph> xwpfpList=xwpfd.getParagraphs();
			if(xwpfpList!=null && xwpfpList.size()>0){
				XWPFParagraph xwpfp=null;
				String line=null;
				for(int p=0;p<xwpfpList.size();p++){
					xwpfp=xwpfpList.get(p);
					line=xwpfp.getText();
					line=line.replaceAll("", lineSeparator);//将软回车替换为回车符
					builder.append(line);
					builder.append(lineSeparator);
				}
			}
			in.close();
			text=builder.toString();
		}
		
		return text;
	}

	/**
	 * 读取.doc格式的word文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readDocFile(String filePath) throws IOException {
		String text = "";
		
		try {
			text = readFromConvertedTxt(filePath);
		} catch (Exception e) {
			e.printStackTrace();
			FileInputStream in = new FileInputStream(new File(filePath));
			WordExtractor extractor = new WordExtractor(in);

			text = extractor.getText();

//			text = text.replaceAll("[\\t\\n\\r]", System.getProperty("line.separator"));// 将软回车替换为回车符
			text = text.replaceAll("[\\n\\r]", lineSeparator);// 将软回车替换为回车符
			in.close();
		}
		
		return text;
	}
	
	/**
	 * 从转换的txt文件中读取文档内容
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFromConvertedTxt(String filePath) throws FileNotFoundException, IOException {
		String fileNameWithSuffix = filePath.substring(filePath
				.lastIndexOf("/") + 1);
		String fileName = fileNameWithSuffix.substring(0,
				fileNameWithSuffix.lastIndexOf("."));

		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		
		String baseStorePath = properties.getProperty("fileupload.path");
		String dateStr = yyyyMMddFormate.format(new Date());
		
		String convertedStorePath = baseStorePath + "converted/txt/" + dateStr + "/" + fileName + ".txt";
		
		String convertedFilePath = word2Format(filePath, convertedStorePath);
		
		String text = readTxt(convertedFilePath);
		
		return text;
	}

	/**
	 * 获取excel文件列值
	 * 
	 * @param xssfRow
	 * @return
	 */
	public static String getExcelCellValue(Cell cell) {
		CellStyle style = cell.getCellStyle();
		int formatIndex = style.getDataFormat();
		String formatString = style.getDataFormatString();
		DataFormatter formatter = new DataFormatter();
		
		if (formatString == null) {
			formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
		}
		
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			
			String cellValue = "";
			if (formatString != null) {
				cellValue = formatter.formatRawCellContents(cell.getNumericCellValue(), formatIndex, formatString);
			} else {
				cellValue = cell.getNumericCellValue() + "";
			}
			
			return cellValue;
			
		} else {
			String cellValue = cell.getStringCellValue().replaceAll("[\\t\\n\\r]", "");
			return String.valueOf(cellValue);
		}
	}

	/**
	 * 将Office文档转换为其他格式. 运行该函数需要用到OpenOffice
	 * 通过指定outputFilePath文件后缀，该方法亦可实现将Office文档转换为TXT、PDF等格式.
	 * 运行该函数需要用到OpenOffice,需要在服务器上安装OpenOffice 文件转换成功与否以异常的形式抛出
	 * 
	 * @param inputFilePath
	 *            源文件,绝对路径. 可以是Office2003-2007全部格式的文档, Office2010的没测试. 包括.doc,
	 *            .docx, .xls, .xlsx, .ppt, .pptx等.
	 * @param outputFilePath
	 *            转换后文件输出路径
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	/*public static String word2Format(String inputFilePath, String outputFilePath)
			throws FileNotFoundException, IOException {
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(2017);
		Process pro = null;
		try {
			File inputFile = new File(inputFilePath);
			if (!inputFile.exists()) {
				throw new IllegalArgumentException("找不到需要转换的文件");
			}
			
			File outputFile = new File(outputFilePath);
			if (!outputFile.getParentFile().exists()) { // 假如目标路径不存在, 则新建该路径
				outputFile.getParentFile().mkdirs();
			}
			
			//获取服务器上openOffice的安装路径
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String officeHome = properties.getProperty("openInstallPath");
			String command = "soffice --headless --accept=\"socket,port=2017;urp;\" --nofirststartwizard&";  
            pro = Runtime.getRuntime().exec(command);
			
			connection.connect();
			
			DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
			converter.convert(inputFile, outputFile);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (connection.isConnected()) {
				connection.disconnect();
				connection = null;
			}
			if (pro != null) {
				pro.destroy();
			}
		}

		return outputFilePath;
	}*/
	
	/**
	 * 将Office文档转换为其他格式. 运行该函数需要用到OpenOffice
	 * 通过指定outputFilePath文件后缀，该方法亦可实现将Office文档转换为TXT、PDF等格式.
	 * 运行该函数需要用到OpenOffice,需要在服务器上安装OpenOffice 文件转换成功与否以异常的形式抛出
	 * 
	 * @param inputFilePath
	 *            源文件,绝对路径. 可以是Office2003-2007全部格式的文档, Office2010的没测试. 包括.doc,
	 *            .docx, .xls, .xlsx, .ppt, .pptx等.
	 * @param outputFilePath
	 *            转换后文件输出路径
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String word2Format(String inputFilePath, String outputFilePath)
			throws FileNotFoundException, IOException {
		
//		DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();

		//获取服务器上openOffice的安装路径
//		Properties properties = PropertiesUitls
//				.fetchProperties("/config.properties");
//		String officeHome = properties.getProperty("openInstallPath");
//		
//		config.setOfficeHome(officeHome);
		
		OfficeManager officeManager = MyOfficeManager.getInstance();
//		if (!officeManager.isRunning()) {
//			officeManager.start();
//		}

		try {
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
			File inputFile = new File(inputFilePath);
			if (inputFile.exists()) {// 找不到源文件, 则返回
				File outputFile = new File(outputFilePath);
				if (!outputFile.getParentFile().exists()) { // 假如目标路径不存在, 则新建该路径
					outputFile.getParentFile().mkdirs();
				}
				converter.convert(inputFile, outputFile);
			} else {
				throw new IllegalArgumentException("找不到需要转换的文件");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return outputFilePath;
	}
	

	/**
	 * 读取txt文件内容
	 * 
	 * @throws IOException
	 */
	public static String readTxt(String filepath) throws IOException {
		File file = new File(filepath);
		InputStreamReader read =  new InputStreamReader(new FileInputStream(file), "UTF-8");
		
		BufferedReader reader = new BufferedReader(read);

		String content = "";
		while (reader.ready()) {
			content += reader.readLine() + lineSeparator;
		}
		reader.close();
		
		content = content.replaceAll("", lineSeparator);
		return content;
	}
	
	/**
     * 获取Excel文档单行数据
     * @param row
     * @return
     */
    public static Map<Integer, String> genRowData(Row row) {
    	int lastCellNum = row.getLastCellNum();
    	Map<Integer, String> rowData = new HashMap<Integer, String>();
    	
    	for (int i = 0; i < lastCellNum; i++) {
			Cell cell = row.getCell(i);
			if (cell != null) {
				String cellValue = OfficeUtils.getExcelCellValue(cell).trim();
    			if (!StringUtils.isBlank(cellValue)) {
    				rowData.put(i, cellValue);
    			}
			}
		}
    	
    	return rowData;
    }
	
	/**
	 * 读取PDF文档
	 * @param file 文档实例
	 * @return 读取结果
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	/*public static String readPDF(String filepath) throws IOException {
		File file = new File(filepath);
		FileInputStream fis = new FileInputStream(file);
		
		PDFParser p = new PDFParser(fis);
		p.parse();
		PDDocument pdd = p.getPDDocument();
		PDFTextStripper ts = new PDFTextStripper();
		
		String text = ts.getText(pdd);
		return text;
	}*/
    
   /* *//**
     * @param wordFilePath 读取word文件的路径
     * @param htmlFilePath 转换后html文件的路劲
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     *//*
    public static String word2Html(String wordFilePath,String htmlFilePath) throws FileNotFoundException, IOException{
    	DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
    	//获取服务器上openOffice的安装路径
    	Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String officeHome = properties.getProperty("openInstallPath");
		config.setOfficeHome(officeHome);
		
		OfficeManager officeManager = config.buildOfficeManager();
		if (!officeManager.isRunning()) {
			officeManager.start();
		}
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		converter.convert(new File(wordFilePath), new File(htmlFilePath));
		officeManager.stop();
    	return htmlFilePath;
    }*/
}
