package com.xiaotu.makeplays.finance.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 费用管理模块拦截器，用户返回数据加密
 * @author xuchangjian 2016-9-28上午11:05:56
 */
@Aspect
@Component
public class FinanceInterceptor {
	
	Logger logger = LoggerFactory.getLogger(FinanceInterceptor.class);
	
	private static final String TABLE_NAME_FIELD = "TABLE_NAME";
	
	@Autowired
	private  HttpServletRequest request;
	
	@Around("execution(* com.xiaotu.makeplays.finance.controller..*.*(..))")
	public Object aroundMethod(ProceedingJoinPoint joinPoint) {
		Object result = null;
		try {
			result = joinPoint.proceed();
			
			Boolean needFinancePwd = (Boolean) request.getSession().getAttribute(Constants.NEED_FINANCE_PWD);
			
			Boolean needValidUserIp = (Boolean) request.getSession().getAttribute(Constants.NEED_VALID_USERIP);
			
			String url = request.getRequestURI(); 
			if (url.indexOf("/nopassword") != -1) {	//请求的url中如果带有nopassword标明不需要进行加密操作
				needFinancePwd = false;
				needValidUserIp = false;
			}
			
			if ((needFinancePwd != null && needFinancePwd) || (needValidUserIp != null && needValidUserIp)) {
				if (result instanceof String) {
					try {
						JSONObject json = JSONObject.fromObject(result);
						result = JSONObject.fromObject(this.encryptJSON(json)).toString();
					} catch (JSONException e) {
						String val = UUIDUtils.getEntryStr();
						result = val.length() > 10 ? val.substring(0, 9) : val;
					}
				} else if (result instanceof ModelAndView) {
					result = encryptModelAndView((ModelAndView) result);
				} else if (result instanceof HashMap) {
					result = encryptMap((Map<String, Object>) result);
				} else if (result instanceof List) {
					result = encryptList((List<Object>) result);
				}
			}
		} catch (Throwable e) {
			logger.error("加密失败", e);
		}
		return result;
	}
	
	/**
	 * 加密ModelAndView
	 * @throws Exception
	 */
	private ModelAndView encryptModelAndView(ModelAndView mv) throws Exception {
		ModelAndView resmv = new ModelAndView();
		Map<String, Object> mMap = mv.getModel();
		Iterator<Map.Entry<String, Object>> it = mMap.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() instanceof String) {
				String val = UUIDUtils.getEntryStr();
				val = val.length() > 10 ? val.substring(0, 9) : val;
				mMap.put(entry.getKey(), val);
			} else if (entry.getValue() instanceof Map) {
				mMap.put(entry.getKey(), encryptMap((Map<String, Object>) entry.getValue()));
			} else if (entry.getValue() instanceof List) {
				mMap.put(entry.getKey(), encryptList((List<Object>) entry.getValue()));
			} else if (entry.getValue() instanceof Double) {
				mMap.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Integer) {
				mMap.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Float) {
				mMap.put(entry.getKey(), 0f);
			} else if (entry.getValue() instanceof Long) {
				mMap.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Date) {
				mMap.put(entry.getKey(), new Date());
			} else if (entry.getValue() instanceof Boolean) {
				mMap.put(entry.getKey(), false);
			}  else {
				mMap.put(entry.getKey(), parseObject(entry.getValue()));
			}
		}
		String name = mv.getViewName();
		resmv.addAllObjects(mMap);
		resmv.setViewName(name);
		return resmv;
	}
	
	/**
	 * 加密JSON
	 * @throws Exception 
	 */
	private Map<String, Object> encryptJSON(JSONObject json) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Object k : json.keySet()) {
			Object v = json.get(k);
			
			if (k.equals("datafields") || k.equals("columns")) {
				map.put(k.toString(), v);
				continue;
			}
			
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<JSONObject> it = ((JSONArray) v).iterator();
				while (it.hasNext()) {
					JSONObject json2 = it.next();
					list.add(encryptJSON(json2));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), encryptObject(v));
			}
		}
		return map;
	}
	
	/**
	 * 判断object类型进行加密
	 * @throws Exception 
	 */
	private Object encryptObject(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			obj = UUIDUtils.getEntryStr();
		} else if (obj instanceof Double) {
			obj = 0;
		} else if (obj instanceof Float) {
			obj = 0f;
		} else if (obj instanceof Long) {
			obj = UUIDUtils.getEntryStr();
		} else if (obj instanceof Date) {
			obj = new Date();
		} else if (obj instanceof Boolean) {
			obj = false;
		}  else if (obj instanceof String) {
			if (isNumber(obj + "")) {
				obj = "0";
			} else {
				obj = UUIDUtils.getEntryStr();
				obj = (obj + "").length() > 20 ? (obj + "").substring(0, 19) : obj;
			}

		} else if (obj instanceof List) {
			obj = encryptList((List<Object>) obj);
		} else if (obj instanceof Map) {
			obj = encryptMap((Map<String, Object>) obj);
		} else {
			obj = parseObject(obj);
		}
		return obj;
	}
	
	/**
	 * 加密List
	 * @throws Exception
	 */
	private List<Object> encryptList(List<Object> list) throws Exception {
		int i = 0;
		for (Object object : list) {
			if (object == null) {
				continue;
			}
			if (object instanceof String) {
				String val = UUIDUtils.getEntryStr();
				val = val.length() > 10 ? val.substring(0, 9) : val;
				list.set(i, val);
			} else if (object instanceof Map) {
				list.set(i, encryptMap((Map<String, Object>) object));
			} else if (object instanceof List) {
				list.set(i, encryptList((List<Object>) object));
			} else if (object instanceof Double) {
				list.set(i, UUIDUtils.getEntryStr());
			} else if (object instanceof Integer) {
				list.set(i, UUIDUtils.getEntryStr());
			} else if (object instanceof Float) {
				list.set(i, 0f);
			} else if (object instanceof Long) {
				list.set(i, UUIDUtils.getEntryStr());
			} else if (object instanceof Date) {
				list.set(i, new Date());
			} else if (object instanceof Boolean) {
				list.set(i, false);
			} else {
				list.set(i, parseObject(object));
			}
			i++;
		}
		return list;
	}
	
	/**
	 * Map 加密返回map
	 * 
	 * @throws Exception
	 */
	private Map<String, Object> encryptMap(Map<String, Object> map) throws Exception {
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() instanceof String) {
				String val = UUIDUtils.getEntryStr();
				val = val.length() > 10 ? val.substring(0, 9) : val;
				map.put(entry.getKey(), val);
			} else if (entry.getValue() instanceof Map) {
				map.put(entry.getKey(), encryptMap((Map<String, Object>) entry.getValue()));
			} else if (entry.getValue() instanceof List) {
				map.put(entry.getKey(), encryptList((List<Object>) entry.getValue()));
			} else if (entry.getValue() instanceof Double) {
				map.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Integer) {
				map.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Float) {
				map.put(entry.getKey(), 0f);
			} else if (entry.getValue() instanceof Long) {
				map.put(entry.getKey(), UUIDUtils.getEntryStr());
			} else if (entry.getValue() instanceof Date) {
				map.put(entry.getKey(), new Date());
			} else if (entry.getValue() instanceof Boolean) {
				map.put(entry.getKey(), false);
			} else {
				map.put(entry.getKey(), parseObject(entry.getValue()));
			}
		}
		return map;
	}
	
	/**
	 * 修改object类中的值进行加密
	 * 
	 * @throws Exception
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Object parseObject(Object obj) throws SecurityException, NoSuchMethodException, Exception {
		if (obj == null) {
			return null;
		}
		// 拿到该类
		Class<?> clz = obj.getClass();
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			if (this.TABLE_NAME_FIELD.equals(field.getName()) || field.getName().indexOf("noField") == 0) {
				continue;
			}
			field.setAccessible(true);
			Method m = obj.getClass().getMethod("get" + genMethodName(field.getName()));
			Object val = m.invoke(obj);
			if (val != null) {
				if (field.getGenericType().toString().equals("class java.lang.String")) {
					if (isNumber(val + "")) {
						field.set(obj, "0");
					} else {
						field.set(obj, encryptObject(val));
					}
				} else if (field.getGenericType().toString().equals("class java.sql.Timestamp")) {
					field.set(obj, new Timestamp((new Date()).getTime()));
				} else {
					field.set(obj, encryptObject(val));
				}
			}
		}
		return obj;
	}
	
	/**
	 * 生成方法名
	 * @param fildeName
	 * @return
	 * @throws Exception
	 */
	private String genMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}
	
	/**
	 * 判断字符串是否是整数
	 */
	private static boolean isInteger(String value) {
		boolean result = false;
		try {
			Integer.parseInt(value);
			result = true;
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	private static boolean isDouble(String value) {
		boolean result = false;
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			result = false;
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}

	/**
	 * 判断是否是数字
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}
}
