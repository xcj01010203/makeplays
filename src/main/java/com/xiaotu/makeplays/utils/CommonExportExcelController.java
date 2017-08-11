package com.xiaotu.makeplays.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;

@Controller
@RequestMapping("/commonExportExcel")
public class CommonExportExcelController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(CommonExportExcelController.class);

	/**
	 * 导出jqxGrid表中的数据
	 * @param request
	 * @param response
	 * @param rows 行数据
	 * @param columns	列数据
	 * @param fileName	文件名
	 */
	@ResponseBody
	@RequestMapping("/exportExcel")
	public Map<String, Object> exportExcel(HttpServletRequest request, HttpServletResponse response, String rows, String columns, String fileName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();

			// 将json格式的字符串转换为json数组对象
			JSONArray rowsArray = JSONArray.fromObject(rows);
			JSONArray columnsArray = JSONArray.fromObject(columns);

			Map<String, Object> data = new HashMap<String, Object>();

			List<String> columnList = new ArrayList<String>();
			List<String> fieldIdlist = new ArrayList<String>();
			
			// 组织表头
			for (int i = 0; i < columnsArray.size(); i++) {
				JSONObject obj = columnsArray.getJSONObject(i);

				if (null == obj.get("text")) {
					continue;
				}
				columnList.add((String) obj.get("text"));
				fieldIdlist.add((String) obj.get("dataField"));
			}

			// 组织数据行和列
			List<Object> rowList = new ArrayList<Object>();
			int maxLevel = 0;
			for (int i = 0; i < rowsArray.size(); i++) {
				JSONObject obj = rowsArray.getJSONObject(i);
				List<Object> valueList = new ArrayList<Object>();
				if (null != obj.get("level")) {
					if (maxLevel < (Integer) obj.get("level")) {
						maxLevel = (Integer) obj.get("level");
					}
					valueList.add(obj.get("level"));
				}
				for (int n = 0; n < fieldIdlist.size(); n++) {
					/*if (obj.get(fieldIdlist.get(n).toString()) != null) {
						String value = obj.get(fieldIdlist.get(n).toString()).toString();
						if (StringUtils.isBlank(value) || value.equals("null")||"0.00".equals(value)) {
							value = "";
						}
						
						valueList.add(value);
					}*/
					Object value = obj.get(fieldIdlist.get(n).toString());
					String valueStr = "";
					if(value!=null){
						valueStr = value.toString();
						if (StringUtils.isBlank(valueStr) || valueStr.equals("null")||"0.00".equals(valueStr)) {
							valueStr = "";
						}
					}
					valueList.add(valueStr);
				}
				rowList.add(valueList);
			}

			
			if (maxLevel > 0) {
				// 形成属性结构
				for (int i = 0; i < rowList.size(); i++) {
					List<Object> valueList = (List<Object>) rowList.get(i);
					int level = (Integer) valueList.get(0);
					valueList.remove(0);
					for (int n = 0; n < maxLevel + 1; n++) {
						if (level == n) {
							continue;
						}
						if (n <= (maxLevel)) {
							valueList.add(n, "");
						}
					}
				}
			}

			
			// 组织表头
			for (int n = 1; n < maxLevel + 1; n++) {
				columnList.add(n, "");
			}

			data.put("columnList", columnList);
			data.put("rowList", rowList);
			data.put("maxLevel", maxLevel);
			data.put("title", "《"+crewName+"》"+fileName);
			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = property.getProperty("financeTemplate");
			String downloadPath = property.getProperty("downloadPath") + "《" + crewName + "》" + fileName + "_"
					+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if (!pathFile.isDirectory()) {
				pathFile.mkdirs();
			}

			ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			
			resultMap.put("downloadPath", downloadPath);
			
			this.sysLogService.saveSysLog(request, "导出" + fileName, Constants.TERMINAL_PC, null, null, 5);
		} catch(Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "导出" + fileName + "失败：" + e.getMessage(), Constants.TERMINAL_PC, null, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

}
