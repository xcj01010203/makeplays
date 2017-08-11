package com.xiaotu.makeplays.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.user.model.constants.IdentityCardType;
import com.xiaotu.makeplays.user.model.constants.MealType;
import com.xiaotu.makeplays.user.model.constants.Sex;

/**
 * 
 *利用poi导出  导入 读取excel文件
 * @author Administrator
 *
 */
public class ExcelUtils {
	private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	private static DecimalFormat    df   = new DecimalFormat("######0.00");   
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";
    private static final String financeSubjectTitle = "财务科目";
    public static void main(String[] args) throws IOException {
		Map<String, Object> maps = readExcel("C://Users/Administrator/Desktop/excel日期测试.xlsx",true);
		//Map<String, Object> maps = financeSubject("C://Users/Administrator/Desktop/excel日期测试.xls");
		Set<String> keys = maps.keySet();
		Iterator<String> it = keys.iterator();
		/*while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> list = (List<Map<String, Object>>) maps.get(key);
			for (Map<String, Object> map : list) {
				System.out.println(map.get("id") + "---" + map.get("title") + "----" + map.get("parentId"));
			}
		}*/
    	
    	System.out.println((short)HSSFDataFormat.getBuiltinFormat("yyyy/MM/dd"));
    	System.out.println(HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd"));
    	System.out.println(HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd HH:mm:dd"));
    	System.out.println(HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd hh:mm:dd"));
    	System.out.println(HSSFDataFormat.getBuiltinFormat("yyyy年MM月dd日"));
    	
	}
	
	
	
	/**
	 * 解析财务预算excel表格
	 * 解析读取的excel文件信息 放入到一个list<map>中 最终返回一个map《key:sheet页名称，value:sheet页中的数据list<map>
	 * @param back 读取模板excel信息
	 * @return
	 * @throws FileFormatException 
	 * @throws FileNotFoundException 
	 */
	public static Map<String, Object> financeSubject(String filePath) throws FileNotFoundException, FileFormatException{
		//读取指定路径下的excel文件
		Map<String, Object> back =readExcel(filePath,false);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//如果excel文件内容为空直接返回
		if(back==null ){
			return null;
		}
		Set<String> keys = back.keySet();
		Iterator<String> it = keys.iterator();
		//解析多个sheet页中的数据
		while(it.hasNext()){
			String key = it.next();
			Object obj = back.get(key);
			if(obj==null){
				continue;
			}
			ArrayList<List<String>> list = (ArrayList<List<String>>)obj;
			if(list!=null&&list.size()>2){//如果只有标题栏不解析
				List<String> coloumnNameList = list.get(1);
				List<String> titleList = list.get(0);//标题集合
				//获取财务科目层级数目
				int num = getSubjectLevel(coloumnNameList,financeSubjectTitle);
				list.remove(0);
				list.remove(0);
//校验  标题  和列名
				boolean hasTitle = true;
				for(String title:titleList){
					if(title.equals(financeSubjectTitle)){
						hasTitle = false;
						break;
					}
				}
				
				if(!hasTitle){
					throw new IllegalArgumentException("请添加标题");
				}
				
	//校验  标题  和列名
				//整理数据将数据放入到list<map>中
				List<Map<String, Object>> data = arrangeData(list, coloumnNameList, num);
				resultMap.put(key, data);
			}
		}
		return resultMap;
	}



	/**
	 * 整理数据，判断是否有重复的财务科目
	 * 
	 * @param list  数据集合
	 * @param titleList   标题集合
	 * @param num   财务科目级别层级数
	 * @return
	 */
	private static List<Map<String, Object>> arrangeData(ArrayList<List<String>> list, List<String> titleList, int num) {
		//将数据放入到一个list<map>中
		//层级目录名称
		String[] subject = new String[num];
		//整理好的数据
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String,Object>>();
		//每条数据对应的id，key：财务科目层级路径，value：id
		Map<String,String> noRepeatSubject = new HashMap<String,String>();
		Map<String, Object> data = null;
		StringBuilder title = null;
		int level = 1;//目录级别
		for(int i=0;i<list.size();i++){
			title = new StringBuilder();
			data = new HashMap<String, Object>();
			List<String> dataList = list.get(i);
			if(dataList==null||dataList.size()<num-1){
				continue;
			}
			boolean fl = true;//是否为科目前的空格
			for(int m=0;m<num;m++){
				String titleData = dataList.get(m);
				if(StringUtils.isNotBlank(titleData)){
						title.append(titleData+"|");
						subject[m] = titleData;
						fl = false;
				}else{
					if(fl){
						titleData = subject[m];
						if(StringUtils.isNotBlank(titleData)){
							title.append(titleData+"|");
						}
					}
					
				}
				
			}
			String subjectNamePath = title.toString();
			//如果数量、单位、单价都为空  并且有子节点 则为父节点
			boolean isFather = false; //是否为父节点
			String nums = dataList.get(num);
			String unit = dataList.get(num+1);
			String price = dataList.get(num+2);
			
			//判断是否有预算数据
			boolean hasValue = false;//没有预算数据
			for(int k = num +3;k<dataList.size()-1;k++){
				String vString = dataList.get(k);
				if(StringUtils.isNotBlank(vString)){
					hasValue = true;
					break;
				}
			}
			
			
			if((nums==null||StringUtils.isBlank(nums))&&(unit==null||StringUtils.isBlank(unit))&&(price==null||StringUtils.isBlank(price))){
				isFather = true;
			}
			
			/*if(hasValue&&isFather){
				throw new IllegalArgumentException("第"+(i+3)+"行数据：没有填写数量、单位、单价，但是有预算值");
			}*/
			
			
			//将各列的数据放入map中
			for(int n =num;n<titleList.size();n++){
				data.put(titleList.get(n), dataList.get(n));
			}
			data.put("isFather", isFather);
			String id = UUIDUtils.getId();
			data.put("id", id);
			int beforeSize = noRepeatSubject.size();
			
			noRepeatSubject.put(subjectNamePath, id);//财务科目id
			
			int afterSize = noRepeatSubject.size();
			if(beforeSize==afterSize){
				throw new IllegalArgumentException("第"+(i+3)+"行：重复的财务科目："+subjectNamePath);
			}
			
			//获取父id
			String[] titles = subjectNamePath.split("\\|");
			StringBuilder afterTitle = new StringBuilder();
			for(int j =0;j<titles.length-1;j++){
				afterTitle.append(titles[j]+"|");
			}
			
			data.put("title", titles[titles.length-1]);
			
			//父节点id
			String parentId = noRepeatSubject.get(afterTitle.toString());
			if(StringUtils.isNotBlank(parentId)){
				data.put("parentId", parentId);
			}else{
				data.put("parentId", "0");
			}
			//获取菜单级别
			String levelParent = noRepeatSubject.get(afterTitle.toString()+"level");
			if(StringUtils.isNotBlank(levelParent)){
				level = Integer.valueOf(levelParent)+1;
			}else{
				level = 1;
			}
			data.put("level",level );
			noRepeatSubject.put(subjectNamePath+"level", String.valueOf(level));//财务科目菜单级别
			
			data.put("sequence", i);
			dataMapList.add(data);
		}
		return dataMapList;
	}
	/**
	 * 获取层级数目
	 * 
	 * @param list 标题栏数据
	 * @param title 科目层级共同的标题
	 * @return
	 */
	private static int getSubjectLevel(List<String> titleList,String title) {
		int num = 0;//财务科目层级数
		for(String str :titleList){
			if(title.equals(str)||(num!=0&&"".equals(str))){
				num ++;
			}else{
				return num;
			}
		}
		return num;
	}
	 /***
	  * 根据不同的excel文件后缀名来判断是否用哪个对象来解析excel文件
     * <pre>
     * 取得Workbook对象(xls和xlsx对象不同,不过都是Workbook的实现类)
     *   xls:HSSFWorkbook
     *   xlsx：XSSFWorkbook
     * @param filePath
     * @return
     * @throws IOException
     * </pre>
     */
    private static Workbook getWorkbook(InputStream is,String filePath) throws IOException {
        Workbook workbook = null;
        if (filePath.endsWith(EXTENSION_XLS)) {
        	workbook = new HSSFWorkbook(is);
        } else if (filePath.endsWith(EXTENSION_XLSX)) {
        	workbook = new XSSFWorkbook(is);
        }
        return workbook;
    }

    /**
     * 文件检查  判断文件是否存在
     * @param filePath
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    private static void preReadCheck(String filePath) throws FileNotFoundException, FileFormatException {
        // 常规检查
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("传入的文件不存在：" + filePath);
        }

        if (!(filePath.endsWith(EXTENSION_XLS) || filePath.endsWith(EXTENSION_XLSX))) {
            throw new FileFormatException("传入的文件不是excel");
        }
    }

    /**
     * 读取excel文件信息
     * @param filePath excel路径
     * @flag 是否读取当前打开工作表  true:是       false: 否（默认读取第一个sheet页）
     * @return  Map <string,list<list<>>> 外层list为一个sheet页中的数据，内层list为每一行数据
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readExcel(String filePath,boolean flag) throws FileNotFoundException, FileFormatException{
        Map<String, Object> back = new LinkedHashMap<String, Object>();
		
		// 检查文件是否有效
        preReadCheck(filePath);
        // 获取workbook对象
        InputStream is = new FileInputStream(filePath);
        Workbook workbook;
		try {
			workbook = getWorkbook(is,filePath);
			if(workbook==null){
	        	return null;
	        }
			// 读文件 一个sheet一个sheet地读取
	        if(flag){
	        	int sheetNums = workbook.getNumberOfSheets();//获取一个excel表格拥有的sheet页的个数
	 	        for (int numSheet = 0; numSheet < sheetNums; numSheet++) {
	 	            Sheet sheet = workbook.getSheetAt(numSheet);
	 	           
	 	            List<ArrayList<String>> li = getSheetValue(sheet);
	 	            back.put(sheet.getSheetName(), li);
	 	        }
	        }else{
	        	 int readSheetIndex = 0;
	        	 Sheet sheet = workbook.getSheetAt(readSheetIndex);//默认读取第一个sheet页
	        	 List<ArrayList<String>> li = getSheetValue(sheet);
 	             back.put(sheet.getSheetName(), li);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return back;
        
    }
    /**
     * 获取sheet中的数据 
     * 	以第一行的数据列数为准 
     * 		如果第一行有10列  那么所有的行都默认为只有10列数据
     * @param sheet
     * @return
     */
    private static List<ArrayList<String>> getSheetValue(Sheet sheet){
    	List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		if(sheet==null){
    		return null;
    	}
    	int firstRowIndex = sheet.getFirstRowNum();//首行index
        int lastRowIndex = sheet.getLastRowNum();//最后一行index
        ArrayList<String> innerList =null;
        int firstColumnIndex = 0;//首列index
        int lastColumnIndex = 20;//最后一列index
        for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; rowIndex++) {
        	//获取一行
            Row currentRow = sheet.getRow(rowIndex);// 当前行
            if(currentRow==null){
            	continue;
            }
            innerList= new ArrayList<String>();
            if(rowIndex==1){//以行首
            	firstColumnIndex = currentRow.getFirstCellNum(); // 首列
            	lastColumnIndex = currentRow.getLastCellNum() - 1;// 最后一列
            }
            boolean flagBlonk = true;//判断是否为空行，读取每个单元格的数据只要有一个单元格有数据就定位为非空行
            //获取当前行的每一个单元格信息
            for (int columnIndex = firstColumnIndex; columnIndex <= lastColumnIndex; columnIndex++) {
            	Cell currentCell = currentRow.getCell(columnIndex);// 当前单元格
                String currentCellValue = "";
                if(currentCell!=null){
                	boolean flag = isMergedRegion(sheet,currentCell);
                    if(flag){
                    	currentCellValue = getMergedRegionValue(sheet, currentCell);
                    }else{
                    	currentCellValue = getCellValue(currentCell, true);// 当前单元格的值
                    }
                    //去掉空行
                    if(StringUtils.isNotBlank(currentCellValue)){
                    	flagBlonk = false;
                    }
                }
                innerList.add(currentCellValue);
            }
            if(!flagBlonk){//只要一行有一列不为空则不为空行
            	list.add(innerList);
            }
        }
        return list;
    }
    
    
    /**
     * 判断该单元格是否为合并的单元格
     * @param sheet
     * @param cell
     * @return
     *
	 *   a-----
	 *   ------
	 *   -----b
	 *  判断是否为合并单元格：
	 *  	后去sheet也中的所有合并的单元格的左上角的坐标（行、列 index）和右下角的单元格坐标（行、列 index）
	 *  	拿某个单元格的行列index比较  
	 * 
	 * 
	 */
    private static boolean isMergedRegion(Sheet sheet,Cell cell){
    	if(sheet==null||cell==null){
    		return false;
    	}
    	boolean flag = false;//默认不是合并的单元格
    	int sheetMergeCount = sheet.getNumMergedRegions();//获取合并的单元格个数
        for(int i = 0;i<sheetMergeCount;i++){
        	//获取合并单元格
        	CellRangeAddress range = sheet.getMergedRegion(i);
        	int frow = range.getFirstRow();
        	int fcol = range.getFirstColumn();
        	int lrow = range.getLastRow();
        	int lcol = range.getLastColumn();
        	
        	int rowIndex = cell.getRowIndex();
        	int colIndex = cell.getColumnIndex();
        	if(rowIndex<=lrow&&rowIndex>=frow){
        		if(colIndex<=lcol&&colIndex>=fcol){
        			flag = true;
        			break;
        		}
        	}
        }
    	return flag;
    }
    
    
    /**
     * 获取单元格的信息
     * 		将单元格中的数据扩散到合并单元格中的每个小单元格中
     * 
     * 处理前：
     * -----
     * --a--
     * -----
     * 处理后：
     * aaaaa
     * aaaaa
     * aaaaa
     * 
     * @param sheet
     * @param cell
     * @return
     */
    private static String getMergedRegionValue(Sheet sheet,Cell cell){
    	if(sheet==null||cell==null){
    		return "";
    	}
    	String cellValue = "";//单元格的值
    	int sheetMergeCount = sheet.getNumMergedRegions();//获取合并的单元格个数
        for(int i = 0;i<sheetMergeCount;i++){
        	CellRangeAddress range = sheet.getMergedRegion(i);
        	//合并单元格的开始行列编号和结束行列编号
        	int frow = range.getFirstRow();
        	int fcol = range.getFirstColumn();
        	int lrow = range.getLastRow();
        	int lcol = range.getLastColumn();
        	//当前单元格的行列编号
        	int rowIndex = cell.getRowIndex();
        	int colIndex = cell.getColumnIndex();
        	//判断是否在合并单元格中
        	if(rowIndex<=lrow&&rowIndex>=frow){
        		//取合并单元格第一个单元格中的内容
        		if(colIndex<=lcol&&colIndex>=fcol){
        			Cell cl = sheet.getRow(frow).getCell(fcol);
        			cellValue =getCellValue(cl,false);
        			break;
        		}
        	}
        }
    	return cellValue;
    }
    
    
    
    /**
     * 取单元格的值
     * @param cell 单元格对象
     * @param treatAsStr 为true时，当做文本来取值 (取到的是文本，不会把“1”取成“1.0”)
     * @return
     */
    private static String getCellValue(Cell cell, boolean treatAsStr) {
        String cellValue = "";
    	if (cell == null) {
            return cellValue;
        }
    	if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
        	cellValue= String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
	       //poi的日期判断仅适用于欧美日期格式，对中文日期不支持，另外增加两个方法判断中文格式日期  
	       if (DateUtil.isCellDateFormatted(cell)||isReserved(cell.getCellStyle().getDataFormat())||isDateFormat(cell.getCellStyle().getDataFormatString())){  
	    	   cellValue= sdf.format(cell.getDateCellValue());  
	       }else{
	    	   cell.setCellType(Cell.CELL_TYPE_STRING);
	    	   cellValue= String.valueOf(cell.getStringCellValue());
    /*	       Double d=cell.getNumericCellValue();  
    	       if(cell.getCellStyle().getDataFormat()==0){  
    	          DecimalFormat dfs = new DecimalFormat("0");
    	          String
    	          cellValue= dfs.format(d);  
    	       }else{
    	    	   cellValue= String.valueOf(d);
    	       } */ 
	       } 
        }else{
        	cell.setCellType(Cell.CELL_TYPE_STRING);
        	cellValue= String.valueOf(cell.getStringCellValue());
        }
        
        return cellValue.trim();
    }
    
    private static boolean isReserved(short reserv){  
        if(reserv>=27&&reserv<=31){  
            return true;  
        }  
        return false;  
    } 
    private static boolean isDateFormat(String isNotDate){  
        if(isNotDate.contains("年")||isNotDate.contains("月")||isNotDate.contains("日")){  
            return true;  
        }else if(isNotDate.contains("aaa;")||isNotDate.contains("AM")||isNotDate.contains("PM")){  
            return true;  
        }  
        return false;  
    }
    /**
     * 导出剧组联系人列表
     * 
     * @param list
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportCrewContactForExcel(List<Map<String, Object>> list,HttpServletResponse response,Map<String, String> exportColoum,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	String title = "《"+crewName+"》剧组联系表";
    	HSSFWorkbook wb =exportExcel(list,exportColoum,title);
        String fileName = title+sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出剧组角色表
     * 
     * @param list
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportPropsInfoForExcel(List<Map<String, Object>> list,HttpServletResponse response,Map<String, String> exportColoum,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	String title = "《"+crewName+"》服化道列表";
    	HSSFWorkbook wb =exportExcel(list,exportColoum,title);
    	String fileName = title+sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    
    
    /**
     * 导出账务详情
     * 
     * @param list
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportFinanceInfoForExcel(List<Map<String, Object>> list,HttpServletResponse response,Map<String, String> exportColoum,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	String title = "《"+crewName+"》账务详情";
    	HSSFWorkbook wb =exportExcel(list,exportColoum,title);
    	String fileName = title+sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出剧组角色表
     * 
     * @param list
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportRoleInfoForExcel(List<Map<String, Object>> list,HttpServletResponse response,Map<String, String> exportColoum,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	String title = "《"+crewName+"》角色表";
    	HSSFWorkbook wb =exportExcel(list,exportColoum,title);
    	String fileName = title+sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出车辆信息（包括加油记录）
     * @param CarInfolist 车辆信息列表
     * @param CarDetaillist 车辆加油信息列表
     * @param response
     * @param CarInfoColoum 车辆信息对应的字段
     * @param CarDetailColoum 车辆加油记录对应的字段
     * @param crewName
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportCarInfoForExcel(List<Map<String, Object>> carInfolist,List<List<Map<String, Object>>> carDetaillist,
    		HttpServletResponse response,Map<String, String> carInfoColoum,Map<String, String> carDetailColoum,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	HSSFWorkbook wb =exportCarExcel(carInfolist, carDetaillist, response, carInfoColoum, carDetailColoum, crewName);
    	String fileName = "《" + crewName+ "》 车辆信息" +sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出项目对比信息
     * @param projectList 项目基本信息列表
     * @param projectDetailList 项目情况汇总信息列表
     * @param response
     * @param projectInfoColumn 项目基本信息对应的字段
     * @param budgetPayedColumn 预算支出结算对应的字段
     */
    public static void exportProjectInfoForExcel(List<Map<String, Object>> projectList, 
    		List<Map<String, Object>> projectDetailList, HttpServletResponse response, 
    		Map<String, String> projectInfoColumn, Map<String, String> budgetPayedColumn,
    		String[] proScheTitle, Map<String, String[]> proScheKeyMap) throws IOException, IllegalArgumentException, IllegalAccessException{
    	HSSFWorkbook wb = exportProjectInfoExcel(projectList, projectDetailList, 
    			response, projectInfoColumn, budgetPayedColumn, proScheTitle, proScheKeyMap);
    	String fileName = "项目情况汇总表" + sdf1.format(new Date()) +".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出计划
     * @param response
     * @param scheduleGroupList 计划分组列表
     * @param viewList 场景列表
     * @param columnKeyMap 列名及key
     * @throws Exception
     */
	public static void exportScheduleForExcel(HttpServletResponse response, CrewInfoModel crewInfo,
			List<Map<String, Object>> scheduleGroupList,
			List<Map<String, Object>> viewList, Map<String, String> columnKeyMap)
			throws Exception {
		HSSFWorkbook wb = exportScheduleExcel(scheduleGroupList, viewList, columnKeyMap);
    	String fileName = "《" + crewInfo.getCrewName() + "》计划表_" + sdf1.format(new Date()) +".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
	}
	
	/**
     * 导出计划详情
     * @param response
     * @param dateList 计划详情列表
     * @param columnKeyMap 列名及key
     * @throws Exception
     */
	public static void exportScheduleDetailForExcel(HttpServletResponse response, CrewInfoModel crewInfo,
			List<Map<String, Object>> dateList, Map<String, String> columnKeyMap)
			throws Exception {
		HSSFWorkbook wb = exportScheduleDetailExcel(dateList, columnKeyMap);
    	String fileName = "《" + crewInfo.getCrewName() + "》计划详情表_" + sdf1.format(new Date()) +".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
	}
    
    /**
     * 
     * @param response
     * @param dataList
     * @param crewName
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void exportCarOilMoneyInfoForExcel(HttpServletResponse response, Map<String, Object> dataMap, Map<String, String> columnMap, String crewName) throws IllegalArgumentException, IllegalAccessException, IOException {
    	HSSFWorkbook wb = exportCarOilMoneyInfoExcel(dataMap, columnMap);
    	String fileName = "《" + crewName+ "》 车辆油费日报表" + sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * 导出主场景所在的场景信息列表
     * @param response
     * @param list
     * @param exportColoum
     * @param crewName
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
	public static void exportViewListByMajorLocationForExcel(HttpServletResponse response,
			List<Map<String, Object>> list, Map<String, String> exportColoum,
			String crewName, String locationName) throws IOException, 
			IllegalArgumentException, IllegalAccessException {
		String title = "《" + crewName + "》" + locationName + "场景表";
		HSSFWorkbook wb = exportExcel(list, exportColoum, title);
		String fileName = title + sdf.format(new Date()) + ".xls";
		response.reset();
		response.setContentType("application/msexcel;charset=utf-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ new String(fileName.getBytes("gb2312"), "iso8859-1"));
		OutputStream out = response.getOutputStream();
		wb.write(out);
	}
    
    /**
     * 拼装excel
     * 
     * @param list 需要导出的数据集合
     * @param map 标题列   key:标题名称，value:实体对应的属性名称
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HSSFWorkbook exportExcel(List<Map<String, Object>> list,Map<String, String> map,String titleData) throws IllegalArgumentException, IllegalAccessException {
    	int rowsNum = 0;
    	int cellNum = 0;
    	//创建HSSFWorkbook对象
		HSSFWorkbook wb = new HSSFWorkbook();
		//创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("sheet1");
		Cell cell = null;
		if(StringUtils.isNotBlank(titleData)){
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, map.size()-1));
			HSSFRow rowTitle = sheet.createRow(rowsNum);
			HSSFCellStyle style = wb.createCellStyle(); // 样式对象      
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
			cell = rowTitle.createCell(0);
			cell.setCellValue(titleData);
			
			
			HSSFFont font=wb.createFont();
            font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
            font.setFontHeightInPoints((short)12);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
            //把字体应用到当前的样式
            style.setFont(font);
		
            cell.setCellStyle(style);
			
			rowsNum ++;
		}		
		
		
		
		Set<String> keySet = map.keySet();//要导出的列名
		Iterator<String> it = keySet.iterator();
//创建标题栏
		//创建HSSFRow对象
		HSSFRow row = sheet.createRow(rowsNum);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		cellStyle.setLocked(true);
		
		while(it.hasNext()){
			sheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell=row.createCell(cellNum);
			String title = it.next();
			//设置单元格的值
			cell.setCellValue(title);
			cell.setCellStyle(cellStyle);
			cellNum ++;
		}
		
		for(Map<String, Object> backMap:list){
			cellNum = 0;
			rowsNum ++;
			row = sheet.createRow(rowsNum);
			it = keySet.iterator();
			while(it.hasNext()){
				String value = it.next();//属性名称
				String attr = map.get(value);
					
				Set<String> backSet = backMap.keySet();
				Iterator<String> backIt = backSet.iterator();
				while(backIt.hasNext()){
					String backKey = backIt.next();
					if(attr.equals(backKey)){
						Object backValue = backMap.get(backKey);
						cell=row.createCell(cellNum);
						if(backValue==null){
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}else{
							backValue =getValue(backKey,backValue);
							cell.setCellType(Cell.CELL_TYPE_STRING);	
							cell.setCellValue(backValue.toString());
							int width = backValue.toString().length()*512;//设置单元格宽度
							if(width<3766){
								width = 3766;
							}
//							sheet.setColumnWidth(cellNum, width);//设置列宽
						}
						cellNum++;
						
						break;
					}
				}
			}
		}
		
		//输出Excel文件
		return wb;
	}
    
    /**
     * 导出车辆信息
     * @param CarInfolist
     * @param CarDetaillist
     * @param response
     * @param CarInfoColoum
     * @param CarDetailColoum
     * @param crewName
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HSSFWorkbook exportCarExcel(List<Map<String, Object>> CarInfolist,List<List<Map<String, Object>>> CarDetaillist,
    		HttpServletResponse response,Map<String, String> CarInfoColoum,Map<String, String> CarDetailColoum,String crewName) throws IllegalArgumentException, IllegalAccessException {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	//先导出车辆信息列表
    	int carRowsNum = 0;
    	int carCellNum = 0;
		//创建HSSFSheet对象
		HSSFSheet carSheet = wb.createSheet("目录");
		Cell carCell = null;
		
		String carTitleData = "《" + crewName + "》 车辆信息列表";
		carSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, CarInfoColoum.size()-1));
		HSSFRow carRowTitle = carSheet.createRow(carRowsNum);
		HSSFCellStyle carStyle = wb.createCellStyle(); // 样式对象      
		carStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
		carStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		carCell = carRowTitle.createCell(0);
		carCell.setCellValue(carTitleData);
		
		
		HSSFFont carFont=wb.createFont();
		carFont.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		carFont.setFontHeightInPoints((short)12);
		carFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
        carStyle.setFont(carFont);
	
        carCell.setCellStyle(carStyle);
		
        carRowsNum ++;
		
		
		
		Set<String> carKeySet = CarInfoColoum.keySet();//要导出的列名
		Iterator<String> carIt = carKeySet.iterator();
//创建标题栏
		//创建HSSFRow对象
		HSSFRow carRow = carSheet.createRow(carRowsNum);
		HSSFCellStyle carCellStyle = wb.createCellStyle();
		carCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		carCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		carCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		carCellStyle.setLocked(true);
		
		while(carIt.hasNext()){
			carSheet.setColumnWidth(carCellNum, 3766);//设置列宽
			//创建HSSFCell对象
			carCell=carRow.createCell(carCellNum);
			String title = carIt.next();
			//设置单元格的值
			carCell.setCellValue(title);
			carCell.setCellStyle(carCellStyle);
			carCellNum ++;
		}
		
		for(Map<String, Object> backMap:CarInfolist){
			carCellNum = 0;
			carRowsNum ++;
			carRow = carSheet.createRow(carRowsNum);
			carIt = carKeySet.iterator();
			while(carIt.hasNext()){
				String value = carIt.next();//属性名称
				String attr = CarInfoColoum.get(value);
					
				Set<String> backSet = backMap.keySet();
				Iterator<String> backIt = backSet.iterator();
				while(backIt.hasNext()){
					String backKey = backIt.next();
					if(attr.equals(backKey)){
						Object backValue = backMap.get(backKey);
						carCell=carRow.createCell(carCellNum);
						if(backValue==null){
							carCell.setCellType(Cell.CELL_TYPE_STRING);
						}else{
							backValue =getValue(backKey,backValue);
							carCell.setCellType(Cell.CELL_TYPE_STRING);	
							carCell.setCellValue(backValue.toString());
							int width = backValue.toString().length()*512;//设置单元格宽度
							if(width<3766){
								width = 3766;
							}
							carSheet.setColumnWidth(carCellNum, width);//设置列宽
						}
						carCellNum++;
						
						break;
					}
				}
			}
		}
		
		//在导出车辆加油信息列表
		for (List<Map<String, Object>> carDetail: CarDetaillist) {
			//初始化表格数据
			int rowsNum = 0;
	    	int cellNum = 0;
			//创建HSSFSheet对象
	    	String carNumber = "";
	    	for (Map<String, Object> carOilMap : carDetail) {
	    		carNumber = (String) carOilMap.get("carNumber");
	    		break;
			}
	    	String detailTitleData = "《" + crewName + "》  "+ carNumber +" 加油信息列表";
			HSSFSheet detailSheet = wb.createSheet(carNumber);
			Cell cell = null;
			if(StringUtils.isNotBlank(crewName)){
				detailSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, CarDetailColoum.size()-1));
				HSSFRow detailRowTitle = detailSheet.createRow(rowsNum);
				HSSFCellStyle detailStyle = wb.createCellStyle(); // 样式对象      
				detailStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
				detailStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
				cell = detailRowTitle.createCell(0);
				cell.setCellValue(detailTitleData);
				
				
				HSSFFont detailFont=wb.createFont();
				detailFont.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
				detailFont.setFontHeightInPoints((short)12);
				detailFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  //字体增粗
	            //把字体应用到当前的样式
	            detailStyle.setFont(detailFont);
			
	            cell.setCellStyle(detailStyle);
				
				rowsNum ++;
			}		
			
			
			
		Set<String> detailKeySet = CarDetailColoum.keySet();//要导出的列名
		Iterator<String> detailIt = detailKeySet.iterator();
        //创建标题栏
		//创建HSSFRow对象
		HSSFRow detailRow = detailSheet.createRow(rowsNum);
		HSSFCellStyle detailCellStyle = wb.createCellStyle();
		detailCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		detailCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		detailCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		detailCellStyle.setLocked(true);
		
		while(detailIt.hasNext()){
			detailSheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell=detailRow.createCell(cellNum);
			String title = detailIt.next();
			//设置单元格的值
			cell.setCellValue(title);
			cell.setCellStyle(detailCellStyle);
			cellNum ++;
		}
		
		for(Map<String, Object> backMap:carDetail){
			cellNum = 0;
			rowsNum ++;
			detailRow = detailSheet.createRow(rowsNum);
			detailIt = detailKeySet.iterator();
			while(detailIt.hasNext()){
				String value = detailIt.next();//属性名称
				String attr = CarDetailColoum.get(value);
					
				Set<String> backSet = backMap.keySet();
				Iterator<String> backIt = backSet.iterator();
				while(backIt.hasNext()){
					String backKey = backIt.next();
					if(attr.equals(backKey)){
						Object backValue = backMap.get(backKey);
						cell=detailRow.createCell(cellNum);
						if(backValue==null){
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}else{
							backValue =getValue(backKey,backValue);
							cell.setCellType(Cell.CELL_TYPE_STRING);	
							cell.setCellValue(backValue.toString());
							int width = backValue.toString().length()*512;//设置单元格宽度
							if(width<3766){
								width = 3766;
							}
							detailSheet.setColumnWidth(cellNum, width);//设置列宽
						}
						cellNum++;
						
						break;
					}
				}
			}
		}
	}
		
		
		//输出Excel文件
		return wb;
	}
    
    /**
     * 导出项目对比信息
     * @param projectList
     * @param projectDetailList
     * @param response
     * @param projectInfoColumn
     * @param budgetPayedColumn
     * @return
     */
    private static HSSFWorkbook exportProjectInfoExcel(List<Map<String, Object>> projectList, 
    		List<Map<String, Object>> projectDetailList, HttpServletResponse response, 
    		Map<String, String> projectInfoColumn, Map<String, String> budgetPayedColumn, 
    		String[] proScheTitle, Map<String, String[]> proScheKeyMap) {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	//先导出项目基本信息列表
    	int projectRowsNum = 0;
    	int projectCellNum = 0;
		//创建HSSFSheet对象
		HSSFSheet projectSheet = wb.createSheet("汇总表");
		Cell projectCell = null;
		
		String projectTitle = "项目情况汇总表";
		projectSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, projectInfoColumn.size()-1));
		HSSFRow projectTitleRow = projectSheet.createRow(projectRowsNum);
		HSSFCellStyle projectStyle = wb.createCellStyle(); // 样式对象      
		projectStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
		projectStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
		projectCell = projectTitleRow.createCell(0);
		projectCell.setCellValue(projectTitle);
				
		HSSFFont projectFont=wb.createFont();
		projectFont.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		projectFont.setFontHeightInPoints((short)12);
		projectFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
		projectStyle.setFont(projectFont);
	
		projectCell.setCellStyle(projectStyle);
		
		projectRowsNum++;
				
		Set<String> projectKeySet = projectInfoColumn.keySet();//要导出的列名
		Iterator<String> projectIt = projectKeySet.iterator();
		//创建标题栏
		//创建HSSFRow对象
		HSSFRow projectRow = projectSheet.createRow(projectRowsNum);
		HSSFCellStyle projectCellStyle = wb.createCellStyle();
		projectCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		projectCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		projectCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居左
		projectCellStyle.setLocked(true);
		
		while(projectIt.hasNext()){
			projectSheet.setColumnWidth(projectCellNum, 3766);//设置列宽
			//创建HSSFCell对象
			projectCell = projectRow.createCell(projectCellNum);
			String title = projectIt.next();
			//设置单元格的值
			projectCell.setCellValue(title);
			projectCell.setCellStyle(projectCellStyle);
			projectCellNum++;
		}
		
		for(Map<String, Object> backMap : projectList){
			projectCellNum = 0;
			projectRowsNum++;
			projectRow = projectSheet.createRow(projectRowsNum);
			projectIt = projectKeySet.iterator();
			while(projectIt.hasNext()){
				String value = projectIt.next();//属性名称
				String attr = projectInfoColumn.get(value);
					
				Set<String> backSet = backMap.keySet();
				Iterator<String> backIt = backSet.iterator();
				while(backIt.hasNext()){
					String backKey = backIt.next();
					if(attr.equals(backKey)){
						Object backValue = backMap.get(backKey);
						projectCell = projectRow.createCell(projectCellNum);
						if(backValue==null){
							projectCell.setCellType(Cell.CELL_TYPE_STRING);
						} else {
							projectCell.setCellType(Cell.CELL_TYPE_STRING);	
							projectCell.setCellValue(backValue.toString());
							int width = backValue.toString().length()*512;//设置单元格宽度
							if(width<3766){
								width = 3766;
							}
							projectSheet.setColumnWidth(projectCellNum, width);//设置列宽
						}
						projectCellNum++;
						
						break;
					}
				}
			}
		}
		
		//导出项目情况
		for (Map<String, Object> projectDetail : projectDetailList) {
			//初始化表格数据
			int rowsNum = 0;
	    	int cellNum = 0;
			//创建HSSFSheet对象
	    	String crewName = projectDetail.get("crewName") + "";
	    	String detailTitleData = "《" + crewName + "》 预算执行总表";
			HSSFSheet detailSheet = wb.createSheet("《" + crewName + "》");
			Cell cell = null;
			//标题
			detailSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, budgetPayedColumn.size()-1));
			HSSFRow detailRowTitle = detailSheet.createRow(rowsNum);
			HSSFCellStyle detailStyle = wb.createCellStyle(); // 样式对象      
			detailStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			detailStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			cell = detailRowTitle.createCell(0);
			cell.setCellValue(detailTitleData);			
			
			HSSFFont detailFont=wb.createFont();
			detailFont.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
			detailFont.setFontHeightInPoints((short)12);
			detailFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  //字体增粗
            //把字体应用到当前的样式
            detailStyle.setFont(detailFont);
		
            cell.setCellStyle(detailStyle);
			
			rowsNum++;
			
			//小标题
			HSSFRow detailLittleRowTitle = detailSheet.createRow(rowsNum);
			cell = detailLittleRowTitle.createCell(0);
			cell.setCellValue("一、预算支出");
			rowsNum++;
			
			Set<String> detailKeySet = budgetPayedColumn.keySet();//要导出的列名
			Iterator<String> detailIt = detailKeySet.iterator();
	        //创建标题栏
			//创建HSSFRow对象
			HSSFRow detailRow = detailSheet.createRow(rowsNum);
			HSSFCellStyle detailCellStyle = wb.createCellStyle();
			detailCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			detailCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			detailCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居中
			detailCellStyle.setLocked(true);
			
			while(detailIt.hasNext()){
				detailSheet.setColumnWidth(cellNum, 3766);//设置列宽
				//创建HSSFCell对象
				cell=detailRow.createCell(cellNum);
				String title = detailIt.next();
				//设置单元格的值
				cell.setCellValue(title);
				cell.setCellStyle(detailCellStyle);
				cellNum++;
			}

			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			if(projectDetail.get("budgetPayed") != null) {
				for(Map<String, Object> backMap : (List<Map<String, Object>>) projectDetail.get("budgetPayed")){
					cellNum = 0;
					rowsNum++;
					detailRow = detailSheet.createRow(rowsNum);
					detailIt = detailKeySet.iterator();
					while(detailIt.hasNext()){
						String value = detailIt.next();//属性名称
						String attr = budgetPayedColumn.get(value);
							
						Set<String> backSet = backMap.keySet();
						Iterator<String> backIt = backSet.iterator();
						while(backIt.hasNext()){
							String backKey = backIt.next();
							if(attr.equals(backKey)){
								Object backValue = backMap.get(backKey);
								cell = detailRow.createCell(cellNum);
								if(backValue==null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
								}else{
									backValue =getValue(backKey,backValue);
									cell.setCellType(Cell.CELL_TYPE_STRING);
									cell.setCellValue(backValue.toString());
									if(cellNum > 0) {
										cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
									} else {
										cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
									}									
									cell.setCellStyle(cellStyle);
									int width = backValue.toString().length()*512;//设置单元格宽度
									if(width<3766){
										width = 3766;
									}
									detailSheet.setColumnWidth(cellNum, width);//设置列宽
								}
								cellNum++;
								
								break;
							}
						}
					}
				}
			}
			
			//合计
			rowsNum++;
			cellNum = 0;
			Map<String, Object> totalInfo = (Map<String, Object>) projectDetail.get("totalInfo");
			detailRow = detailSheet.createRow(rowsNum);
			cell = detailRow.createCell(cellNum);
			cell.setCellValue("合计");
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cell.setCellStyle(cellStyle);
			cellNum++;
			cell = detailRow.createCell(cellNum);
			cell.setCellValue(totalInfo.get("totalBudgetMoney") + "");
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cell.setCellStyle(cellStyle);
			cellNum++;
			cell = detailRow.createCell(cellNum);
			cell.setCellValue(totalInfo.get("totalPayedMoney") + "");
			cell.setCellStyle(cellStyle);
			cellNum++;
			cell = detailRow.createCell(cellNum);
			cell.setCellValue(totalInfo.get("totalLeftMoney") + "");
			cell.setCellStyle(cellStyle);
			cellNum++;
			cell = detailRow.createCell(cellNum);
			cell.setCellValue(totalInfo.get("totalPayedRate") + "");
			cell.setCellStyle(cellStyle);
			
			//制作进度
			rowsNum = 1;
			cellNum = budgetPayedColumn.size() + 1;
			Map<String, Object> productionSchedule = (Map<String, Object>) projectDetail.get("productionSchedule");
			detailRow = detailSheet.getRow(rowsNum);
			cell = detailRow.createCell(cellNum);
			cell.setCellValue("二、制作进度");
			rowsNum++;
			detailRow = detailSheet.getRow(rowsNum);
			for(int i = 0; i < proScheTitle.length; i++) {
				detailSheet.setColumnWidth(cellNum, 3766);//设置列宽
				cell = detailRow.createCell(cellNum);
				cell.setCellValue(proScheTitle[i]);
				cell.setCellStyle(detailCellStyle);
				cellNum++;
			}		
			
			Set<String> proScheKey = proScheKeyMap.keySet();
			Iterator<String> proScheIt = proScheKey.iterator();
			while(proScheIt.hasNext()) {				
				rowsNum++;
				cellNum = budgetPayedColumn.size() + 1;
				if(detailSheet.getLastRowNum() >= rowsNum) {
					detailRow = detailSheet.getRow(rowsNum);
				}else{
					detailRow = detailSheet.createRow(rowsNum);
				}
				cell = detailRow.createCell(cellNum);
				String title = proScheIt.next();
				cell.setCellValue(title);
				String[] keyArr = proScheKeyMap.get(title);
				for(int i = 0; i < keyArr.length; i++) {
					cellNum++;
					cell = detailRow.createCell(cellNum);
					cell.setCellValue(productionSchedule.get(keyArr[i]) + "");
					cell.setCellStyle(cellStyle);
				}
			}			
		}
		
		//输出Excel文件
		return wb;
    }
    
    private static Object getValue(String key,Object value){
    	if("identityCardType".equals(key)){
    		value = IdentityCardType.valueOf(Integer.valueOf(value.toString())).getName();
    	}else if("sex".equals(key)){
    		value = Sex.valueOf(Integer.valueOf(value.toString())).getName();
    	}else if("mealType".equals(key)){
    		value = MealType.valueOf(Integer.valueOf(value.toString())).getName();
    	}else if("sysRoleNames".equals(key)){
    		value = value.toString().replace("-", "/");
    	}
    	
    	return value;
    }
    
    
    /**
     * 读取剧组联系人excel信息
     * 
     * @param filePath 文件路径
     * @return
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readContactInfo(String filePath) throws FileNotFoundException, FileFormatException{
    	Map<String, Object> excelInfo = readExcel(filePath,false);
    	return excelInfo;
    }
    
    /**
     * 读取剧组场景表excel信息
     * 
     * @param filePath 文件路径
     * @return
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readViewInfo(String filePath) throws FileNotFoundException, FileFormatException{
    	Map<String, Object> excelInfo = readExcel(filePath,false);
    	return excelInfo;
    }
    
    /**
     * 导出通告单列表数据
     * @param response
     * @param columnKeyMap 列名及key
     * @throws Exception
     */
	public static void exportNoticeListForExcel(HttpServletResponse response, CrewInfoModel crewInfo,
			List<Map<String, Object>> dataList, Map<String, String> columnKeyMap)
			throws Exception {
		HSSFWorkbook wb = exportNoticeListExcel(dataList, columnKeyMap);
    	String fileName = "《" + crewInfo.getCrewName() + "》通告单列表_" + sdf1.format(new Date()) +".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
	}
    
    
    /**
     * 读取剧组收、付、借款单
     * 
     * @param filePath 文件路径
     * @return
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readGetCostInfo(String filePath) throws FileNotFoundException, FileFormatException{
    	Map<String, Object> excelInfo = readExcel(filePath,false);
    	return excelInfo;
    }
    
    /**
     * 读取车辆加油记录(此方法支持只读取当前打开的sheet)
     * 
     * @param filePath 文件路径
     * @return
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readCarWorkInfo(String filePath) throws FileNotFoundException, FileFormatException{
    	Map<String, Object> excelInfo = readExcel(filePath,true);
    	return excelInfo;
    }
    
    /**
     * 读取计划
     * 
     * @param filePath 文件路径
     * @return
     * @throws FileNotFoundException
     * @throws FileFormatException
     */
    public static Map<String, Object> readScheduleInfo(String filePath) throws FileNotFoundException, FileFormatException{
    	Map<String, Object> excelInfo = readExcel(filePath,false);
    	return excelInfo;
    }
    /**
     * 导出住宿费用信息
     * 
     */
    public static void exportInHotelCostInfoForExcel(Map<String, Object> mainInfo,List<Map<String, Object>> detailInfo,HttpServletResponse response,String crewName) throws IOException, IllegalArgumentException, IllegalAccessException{
    	String title = "《"+crewName+"》住宿费用";
    	HSSFWorkbook wb =exportInHotelCostInfo(mainInfo,detailInfo,response);
    	String fileName = title+sdf.format(new Date())+".xls";
        response.reset();
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("gb2312"), "iso8859-1")); 
        OutputStream out = response.getOutputStream();
        wb.write(out);
    }
    
    /**
     * @Description 导出住宿费用列表和明细  明细以天为单位  每天一个sheet页
     * @param mainInfo
     * @param detailInfo
     * @param response
     * @return
     */
    public static HSSFWorkbook exportInHotelCostInfo(Map<String, Object> mainInfo,List<Map<String, Object>> detailInfo,HttpServletResponse response){
    	List<Map<String, Object>> list = (List<Map<String, Object>>) mainInfo.get("inHotelCostInfoList");
    	String sumCost  = mainInfo.get("sumCost")!=null ?mainInfo.get("sumCost").toString():"0.00";
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	HSSFCellStyle linkStyle = wb.createCellStyle();//链接地址样式
    	HSSFFont cellFont= wb.createFont();  
        cellFont.setUnderline((byte) 1);  
        cellFont.setColor(HSSFColor.BLUE.index);  
        linkStyle.setFont(cellFont);  
        linkStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中  
        linkStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
    	HSSFCellStyle boxStyle = wb.createCellStyle();//合并单元格样式
    	boxStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中  
    	boxStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
    	
    	HSSFSheet sheet  = null;
    	int rownum = 0;
    	//住宿费用一览信息
    	if(list!=null&&list.size()>0){
    		//创建HSSFSheet对象
    		sheet= wb.createSheet("住宿费用信息");
    		sheet.setDefaultColumnWidth(15);//设置列宽
    		int fcol = 0,lcol = 0;//要合并的开始列和最后一列
    		int frow = 1;//要合并的开始行和最后一行
    		String tempShowDate = "";
    		int cellNum = 0;
    		HSSFRow titleRow = sheet.createRow(rownum++);
			titleRow.createCell(cellNum++).setCellValue("日期");
			titleRow.createCell(cellNum++).setCellValue("宾馆名称");
			titleRow.createCell(cellNum++).setCellValue("人数");
			titleRow.createCell(cellNum++).setCellValue("房间数");
			titleRow.createCell(cellNum++).setCellValue("平均房价");
			titleRow.createCell(cellNum++).setCellValue("总费用");
			titleRow.createCell(cellNum++).setCellValue("操作");
    		for(int i = 0,le = list.size();i<le;i++){
    			Map<String, Object> temp = list.get(i);
    			cellNum = 0;
				HSSFRow row = sheet.createRow(rownum);
    			
    			String showDate = temp.get("checkInDate").toString();
    			String hotelname = temp.get("hotelname").toString();
    			String rnum = temp.get("rnum").toString();
    			String sprice = temp.get("sprice").toString();
    			String aprice = temp.get("aprice").toString();
    			String pnum = temp.get("pnum").toString();
    			//日期
    			HSSFCell showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(showDate);
    			//宾馆名称
    			showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(hotelname);
    			//人数
    			showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(pnum);
    			//房间数
    			showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(rnum);
    			//平均房价
    			showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(df.format(Double.valueOf(aprice)));
    			//总金额
    			showDateCell = row.createCell(cellNum++);
    			showDateCell.setCellValue(df.format(Double.valueOf(sprice)));
    			
    			HSSFCell likeCell = row.createCell(cellNum++);
    			Hyperlink hyperlink = new HSSFHyperlink(Hyperlink.LINK_DOCUMENT);  
                // "#"表示本文档    "明细页面"表示sheet页名称  "A10"表示第几列第几行  
	            hyperlink.setAddress("#'"+showDate+"'!A1");  
	            likeCell.setHyperlink(hyperlink);
	            likeCell.setCellValue("详情");
	            
	            
	            /* 设置为超链接的样式*/  
	            
	            likeCell.setCellStyle(linkStyle);
	            
	            
	            if(!showDate.equals(tempShowDate)){
	            	if(rownum>frow+1){
	            		CellRangeAddress caddress = new CellRangeAddress(frow, rownum-1, fcol, lcol);
	            		sheet.addMergedRegion(caddress);
	            	}
	            	if(i == le -1 ){
	            		sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
	            		frow = rownum;
	            	}
	            	sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
	            	frow = rownum;
	            	
	            }else{
	            	if(i == le -1){
	            		if(rownum>frow){
		            		CellRangeAddress caddress = new CellRangeAddress(frow, rownum, fcol, lcol);
		            		sheet.addMergedRegion(caddress);
		            	}
	            		sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
	            	}
	            }
	            
	            tempShowDate  = showDate;
	            
	            rownum++;
    		}
    		
    		HSSFRow r = sheet.createRow(rownum++);
			r.createCell(4).setCellValue("合计：");
			r.createCell(5).setCellValue(df.format(Double.valueOf(sumCost)));
    	}
    	//住宿费用详细信息
		if(detailInfo!=null&&detailInfo.size()>0){
			
			String showdate = "";
			String hName = "";
			int frow = 1;
			int fcol = 0,lcol = 0;
    		for(int n = 0,le = detailInfo.size();n<le;n++){
    			Map<String, Object> temp = detailInfo.get(n);
    			String showDate = temp.get("showdate")!=null?temp.get("showdate").toString():"";
    			String hotelname = temp.get("hotelname")!=null?temp.get("hotelname").toString():"";
    			String roomnumber = temp.get("roomnumber")!=null?temp.get("roomnumber").toString():"";
    			String concactname = temp.get("concactname")!=null?temp.get("concactname").toString():"";
    			String price = temp.get("price")!=null?temp.get("price").toString():"";
    			HSSFRow row = null;
    			int cellNum = 0;
    			if(!showdate.equals(showDate)){
    				if(StringUtils.isBlank(showdate)){
        				sheet = wb.createSheet(showDate);
        				
        			}else{
        				if(frow <rownum -1 ){
        					sheet.addMergedRegion(new CellRangeAddress(frow, rownum-1, fcol, lcol));
        				}
        				sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
        				sheet = wb.createSheet(showDate);
        			}
    				sheet.setDefaultColumnWidth(15);//设置列宽
    				frow = 1;
    				rownum = 0;
    				showdate = showDate;
    				row = sheet.createRow(rownum++);
    				row.createCell(cellNum++).setCellValue("宾馆名称");
        			row.createCell(cellNum++).setCellValue("房间号");
        			row.createCell(cellNum++).setCellValue("入住人员");
        			row.createCell(cellNum++).setCellValue("房价");
        			cellNum = 0;
        			hName = "";
				}
    			
    			row = sheet.createRow(rownum);
    			row.createCell(cellNum++).setCellValue(hotelname);
    			row.createCell(cellNum++).setCellValue(roomnumber);
    			row.createCell(cellNum++).setCellValue(concactname);
    			if (StringUtils.isNotBlank(price)) {
    				row.createCell(cellNum++).setCellValue(df.format(Double.valueOf(price)));
				}else {
					row.createCell(cellNum++).setCellValue(0.0);
				}
    			if(!hotelname.equals(hName)){
    				if(rownum>frow+1){
    					CellRangeAddress caddress = new CellRangeAddress(frow, rownum-1, fcol, lcol);
    					sheet.addMergedRegion(caddress);
    					sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
    					frow = rownum;
    				}
    				hName = hotelname;
    			}
				if(n == le -1){
            		if(rownum>frow){
	            		CellRangeAddress caddress = new CellRangeAddress(frow, rownum, fcol, lcol);
	            		sheet.addMergedRegion(caddress);
	            	}
            		sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
            	}
    			rownum++;
    		}
    	}
    	return wb;
    }
    
    /**
     * 拼装excel
     * 
     * @param list 需要导出的数据集合
     * @param map 标题列   key:标题名称，value:实体对应的属性名称
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HSSFWorkbook exportCarOilMoneyInfoExcel(Map<String, Object> dataMap, Map<String, String> columnMap) throws IllegalArgumentException, IllegalAccessException {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	//先导出项目基本信息列表
    	int rowsNum = 0;
    	int cellNum = 0;
		//创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("加油费用日统计");
		Cell cell = null;
		
		String title = "车辆加油费用日统计报表";
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnMap.size() - 1));
		HSSFRow titleRow = sheet.createRow(rowsNum);
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象      
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
		cell = titleRow.createCell(0);
		cell.setCellValue(title);
				
		HSSFFont font=wb.createFont();
		font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
		style.setFont(font);
	
		cell.setCellStyle(style);
		
		rowsNum++;
				
		Set<String> columnKeySet = columnMap.keySet();//要导出的列名
		Iterator<String> columnIt = columnKeySet.iterator();
		//创建标题栏
		//创建HSSFRow对象
		HSSFRow row = sheet.createRow(rowsNum);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居左
		cellStyle.setLocked(true);
		
		while(columnIt.hasNext()){
			sheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell = row.createCell(cellNum);
			String columnTitle = columnIt.next();
			//设置单元格的值
			cell.setCellValue(columnTitle);
			cell.setCellStyle(cellStyle);
			cellNum++;
		}
		rowsNum++;
		
		if(dataMap != null && !dataMap.isEmpty()) {
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataMap.get("resultList");			
			HSSFCellStyle boxStyle = wb.createCellStyle();//合并单元格样式
	    	boxStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居中  
	    	boxStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
			int frow = 2;
	    	int fcol = 0,lcol = 0;
			for(Map<String, Object> oneMap : dataList) {
    			cellNum = 0;
				row = sheet.createRow(rowsNum);
    			
    			//日期
    			cell = row.createCell(cellNum++);
    			cell.setCellValue(oneMap.get("workDate") + "");
    			
    			List<Map<String, Object>> carList = (List<Map<String, Object>>) oneMap.get("carList");
    			
    			if(carList != null && carList.size() > 0) {
    				for(int i = 0; i < carList.size(); i++) {
    					Map<String, Object> oneCar = carList.get(i);
    					if(i != 0) {
    						row = sheet.createRow(rowsNum);
    						cellNum = 1;
    					}
    	    			//车牌号
    					cell = row.createCell(cellNum++);
    					cell.setCellValue(oneCar.get("carNumber") + "");
    	    			//加油升数
    					cell = row.createCell(cellNum++);
    					cell.setCellValue(oneCar.get("totalLiters") + "");
    	    			//加油金额
    					cell = row.createCell(cellNum++);
    					cell.setCellValue(oneCar.get("totalMoney") + "");
    	    			
    	    			rowsNum++;
    				}
    			}
    			
    			CellRangeAddress caddress = new CellRangeAddress(frow, rowsNum-1, fcol, lcol);
        		sheet.addMergedRegion(caddress);
        		sheet.getRow(frow).getCell(fcol).setCellStyle(boxStyle);
            	frow = rowsNum;
			}
			HSSFRow r = sheet.createRow(rowsNum);
			r.createCell(2).setCellValue("合计：");
			r.createCell(3).setCellValue(dataMap.get("totalMoney") + "");
		}
		
		//输出Excel文件
		return wb;
	}
    
    /**
     * 导出计划excel
     * @param scheduleGroupList
     * @param viewList
     * @param columnKeyMap
     * @return
     */
    private static HSSFWorkbook exportScheduleExcel(List<Map<String, Object>> scheduleGroupList, List<Map<String, Object>> viewList, Map<String, String> columnKeyMap) {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	int rowsNum = 0;
    	int cellNum = 0;
		//创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("计划表");
		Cell cell = null;
		
		String title = "计划表";
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnKeyMap.size() - 1));
		HSSFRow titleRow = sheet.createRow(rowsNum);
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
		cell = titleRow.createCell(0);
		cell.setCellValue(title);
				
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
		style.setFont(font);
	
		cell.setCellStyle(style);
		
		rowsNum++;
				
		Set<String> columnKeySet = columnKeyMap.keySet();//要导出的列名
		Iterator<String> columnIt = columnKeySet.iterator();
		//创建标题栏
		//创建HSSFRow对象
		HSSFRow row = sheet.createRow(rowsNum);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居左
		cellStyle.setLocked(true);
		
		while(columnIt.hasNext()){
			sheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell = row.createCell(cellNum);
			String columnTitle = columnIt.next();
			//设置单元格的值
			cell.setCellValue(columnTitle);
			cell.setCellStyle(cellStyle);
			cellNum++;
		}
		rowsNum++;
		
		Map<String, Map<String, Object>> scheduleGroupMap = new HashMap<String, Map<String,Object>>();
		if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
			for(Map<String, Object> map : scheduleGroupList) {
				scheduleGroupMap.put(map.get("groupId") + "", map);
			}
		}
		if(viewList != null && viewList.size() > 0) {
			HSSFCellStyle contentStyle = wb.createCellStyle(); // 样式对象      
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			HSSFCellStyle groupStyle = wb.createCellStyle(); // 样式对象      
			groupStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
			groupStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			groupStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			groupStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			String planGroupIdFlag = "-1";
			for(Map<String, Object> viewMap : viewList) {
				String planGroupId = (String) viewMap.get("groupId");
				if(StringUtil.isBlank(planGroupId)) {
					planGroupId = "";
				}
				if(!planGroupIdFlag.equals(planGroupId)) {
					String value = "未分组";
					if(scheduleGroupMap.containsKey(planGroupId)) {
						value = scheduleGroupMap.get(planGroupId).get("groupName") + "";
					}
					sheet.addMergedRegion(new CellRangeAddress(rowsNum, rowsNum, 0, columnKeyMap.size() - 1));
					HSSFRow groupRow = sheet.createRow(rowsNum);
					cell = groupRow.createCell(0);
					cell.setCellValue(value);
					cell.setCellStyle(groupStyle);
					rowsNum++;
					planGroupIdFlag = planGroupId;
				}
				
				HSSFRow viewRow = sheet.createRow(rowsNum);
				cellNum = 0;
				columnIt = columnKeySet.iterator();
				while(columnIt.hasNext()) {
					cell = viewRow.createCell(cellNum);
					//设置单元格的值
					String key = columnIt.next();
					if(viewMap.get(columnKeyMap.get(key)) != null) {
						cell.setCellValue(viewMap.get(columnKeyMap.get(key)) + "");
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(contentStyle);
					cellNum++;
				}
				rowsNum++;
			}
		}
		//冻结前两行
		sheet.createFreezePane( 0, 2, 0, 2 );
		//输出Excel文件
		return wb;
    }
    

    
    /**
     * 导出计划excel
     * @param dateList
     * @param columnKeyMap
     * @return
     */
    private static HSSFWorkbook exportScheduleDetailExcel(List<Map<String, Object>> dateList, Map<String, String> columnKeyMap) {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	int rowsNum = 0;
		//创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("计划详情表");
		Cell cell = null;
		
		String title = "计划详情表";
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnKeyMap.size()));
		HSSFRow titleRow = sheet.createRow(rowsNum);
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
		cell = titleRow.createCell(0);
		cell.setCellValue(title);
				
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
		style.setFont(font);
	
		cell.setCellStyle(style);
		
		rowsNum++;
				
		Set<String> columnKeySet = columnKeyMap.keySet();//要导出的列名
		Iterator<String> columnIt = columnKeySet.iterator();
		//创建标题栏
		//创建HSSFRow对象
		HSSFRow row = sheet.createRow(rowsNum);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居左
		cellStyle.setLocked(true);

    	int cellNum = 1;
		while(columnIt.hasNext()){
			sheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell = row.createCell(cellNum);
			String columnTitle = columnIt.next();
			//设置单元格的值
			cell.setCellValue(columnTitle);
			cell.setCellStyle(cellStyle);
			cellNum++;
		}
		rowsNum++;
		
		if(dateList != null && dateList.size() > 0) {
			HSSFCellStyle contentStyle = wb.createCellStyle(); // 样式对象      
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			HSSFCellStyle dateStyle = wb.createCellStyle(); // 样式对象      
			dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			dateStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			dateStyle.setWrapText(true);
			HSSFCellStyle totalStyle = wb.createCellStyle(); // 样式对象      
			totalStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
			totalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			totalStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			totalStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			for(Map<String, Object> dateMap : dateList) {
				String dateValue = "";
				if(StringUtils.isNotBlank((String) dateMap.get("planShootDate"))) {
					dateValue += "第" + (Long) dateMap.get("dayNum") + "天\r\n" + (String) dateMap.get("planShootDate");
					if(StringUtils.isNotBlank((String) dateMap.get("planShootGroup"))) {
						dateValue += "  " + (String) dateMap.get("planShootGroup");
					}
				} else {
					dateValue += "待定";
					if(StringUtils.isNotBlank((String) dateMap.get("planShootGroup"))) {
						dateValue += "\r\n" + (String) dateMap.get("planShootGroup");
					}
				}
				
				HSSFRow groupRow = sheet.createRow(rowsNum);
				int rowsNumFlag = rowsNum;
				
				for(Map<String, Object> map : (List<Map<String, Object>>)dateMap.get("viewInfoList")) {
					cellNum = 1;
					HSSFRow viewRow = sheet.createRow(rowsNum);
					columnIt = columnKeySet.iterator();
					while(columnIt.hasNext()) {
						cell = viewRow.createCell(cellNum);
						//设置单元格的值
						String key = columnIt.next();
						if(map.get(columnKeyMap.get(key)) != null) {
							cell.setCellValue(map.get(columnKeyMap.get(key)) + "");
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(contentStyle);
						cellNum++;
					}
					rowsNum++;
				}
				String actor = "";
				if(StringUtils.isNotBlank((String)dateMap.get("majorRole"))) {
					actor += dateMap.get("majorRole");
				}
				if(StringUtils.isNotBlank((String)dateMap.get("guestRole"))) {
					if(StringUtils.isNotBlank(actor)) {
						actor += " | " + (String)dateMap.get("guestRole");
					} else {
						actor += (String)dateMap.get("guestRole");
					}
				}
				String dateDetailValue = "共" + dateMap.get("viewNum") + "场  完成" + dateMap.get("finishedViewNum") 
						+ "场  共" + (dateMap.get("pageCount")==null ? 0:dateMap.get("pageCount")) + "页  完成" 
						+ (dateMap.get("finishedPageCount")==null ? 0:dateMap.get("finishedPageCount"))
						+ "页  拍摄地点：" + (dateMap.get("shootLocation")==null ? "":dateMap.get("shootLocation")) 
						+ "  主演/特约：" + actor;
				sheet.addMergedRegion(new CellRangeAddress(rowsNum, rowsNum, 1, columnKeyMap.size()));
				HSSFRow dateDetailRow = sheet.createRow(rowsNum);
				cell = dateDetailRow.createCell(1);
				cell.setCellValue(dateDetailValue);
				cell.setCellStyle(totalStyle);
				rowsNum++;
				sheet.addMergedRegion(new CellRangeAddress(rowsNumFlag, rowsNumFlag + Integer.parseInt(dateMap.get("viewNum") + ""), 0, 0));
				cell = groupRow.createCell(0);
				cell.setCellValue(dateValue);
				cell.setCellStyle(dateStyle);
			}
		}
		//冻结前两行、第一列
		sheet.createFreezePane( 1, 2, 1, 2 );
		//输出Excel文件
		return wb;
    }
    
    /**
     * 导出通告单列表数据excel
     * @param dateList
     * @param columnKeyMap
     * @return
     */
    private static HSSFWorkbook exportNoticeListExcel(List<Map<String, Object>> dateList, Map<String, String> columnKeyMap) {
    	//创建HSSFWorkbook对象
    	HSSFWorkbook wb = new HSSFWorkbook();
    	
    	int rowsNum = 0;
		//创建HSSFSheet对象
		HSSFSheet sheet = wb.createSheet("通告单列表");
		Cell cell = null;
		
		String title = "通告单列表";
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnKeyMap.size()));
		HSSFRow titleRow = sheet.createRow(rowsNum);
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
		cell = titleRow.createCell(0);
		cell.setCellValue(title);
				
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
		style.setFont(font);
	
		cell.setCellStyle(style);
		
		rowsNum++;
				
		Set<String> columnKeySet = columnKeyMap.keySet();//要导出的列名
		Iterator<String> columnIt = columnKeySet.iterator();
		//创建标题栏
		//创建HSSFRow对象
		HSSFRow row = sheet.createRow(rowsNum);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居左
		cellStyle.setLocked(true);

    	int cellNum = 1;
		while(columnIt.hasNext()){
			sheet.setColumnWidth(cellNum, 3766);//设置列宽
			//创建HSSFCell对象
			cell = row.createCell(cellNum);
			String columnTitle = columnIt.next();
			//设置单元格的值
			cell.setCellValue(columnTitle);
			cell.setCellStyle(cellStyle);
			cellNum++;
		}
		rowsNum++;
		
		if(dateList != null && dateList.size() > 0) {
			HSSFCellStyle contentStyle = wb.createCellStyle(); // 样式对象      
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			HSSFCellStyle dateStyle = wb.createCellStyle(); // 样式对象      
			dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			dateStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			dateStyle.setWrapText(true);
			HSSFCellStyle totalStyle = wb.createCellStyle(); // 样式对象      
			totalStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
			totalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			totalStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
			totalStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
			for(Map<String, Object> dateMap : dateList) {
				String dateValue = (String) dateMap.get("noticeMessage");
				
				HSSFRow groupRow = sheet.createRow(rowsNum);
				int rowsNumFlag = rowsNum;
				
				for(Map<String, Object> map : (List<Map<String, Object>>)dateMap.get("viewList")) {
					cellNum = 1;
					HSSFRow viewRow = sheet.createRow(rowsNum);
					columnIt = columnKeySet.iterator();
					while(columnIt.hasNext()) {
						cell = viewRow.createCell(cellNum);
						//设置单元格的值
						String key = columnIt.next();
						if(map.get(columnKeyMap.get(key)) != null) {
							cell.setCellValue(map.get(columnKeyMap.get(key)) + "");
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(contentStyle);
						cellNum++;
					}
					rowsNum++;
				}
				
				String dateDetailValue = "共" + (dateMap.get("viewCount")==null?0:dateMap.get("viewCount"))
						+ "场  完成" + (dateMap.get("finishCount")==null?0: dateMap.get("finishCount"))
						+ "场  共" + (dateMap.get("sumPage")==null ? 0:dateMap.get("sumPage")) + "页  完成" 
						+ (dateMap.get("finishPage")==null ? 0:dateMap.get("finishPage"))
						+ "页  拍摄地点：" + (dateMap.get("shootLocation")==null ? "":dateMap.get("shootLocation")) 
						+ "  主演/特约：" + (dateMap.get("noticeMainGuestRoleStr")==null ? "":dateMap.get("noticeMainGuestRoleStr"));
				sheet.addMergedRegion(new CellRangeAddress(rowsNum, rowsNum, 1, columnKeyMap.size()));
				HSSFRow dateDetailRow = sheet.createRow(rowsNum);
				cell = dateDetailRow.createCell(1);
				cell.setCellValue(dateDetailValue);
				cell.setCellStyle(totalStyle);
				rowsNum++;
				sheet.addMergedRegion(new CellRangeAddress(rowsNumFlag, rowsNumFlag + Integer.parseInt(dateMap.get("viewCount")==null?"0":dateMap.get("viewCount")+""), 0, 0));
				cell = groupRow.createCell(0);
				cell.setCellValue(dateValue);
				cell.setCellStyle(dateStyle);
			}
		}
		//冻结前两行、第一列
		sheet.createFreezePane( 1, 2, 1, 2 );
		//输出Excel文件
		return wb;
    }
}
