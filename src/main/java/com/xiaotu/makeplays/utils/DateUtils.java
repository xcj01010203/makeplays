package com.xiaotu.makeplays.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	public static final String PATTERN_DEFAULT = "yyyy/MM";
	
	public static final String PATTERN_SYSTEM_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String PATTERN_SYSTEM_MIN = "yyyy/MM/dd HH:mm";
	
	public static final String PATTERN_BASE = "yyyy-MM-dd";
	
	public static final String PATTERN_MONTH_DAY = "MM/dd";
	//格式化时间分隔符   /   。  .   -   
	private static final String[] SEPARATOR = new String[]{"/","\\u002E","。","-"};
	
	/**
	 * 计算两个日期之间相差几个月份
	 * 
	 * @param date1
	 * 		日期：2014-05
	 * @param date2
	 * 		日期：2014-08
	 * @return
	 * 		3
	 * @throws ParseException
	 */
	@Deprecated
	public static int _getMonthSpace(String date1, String date2, String pattern) throws ParseException {
		int result = 0;

		// 如果pattern为空，采用默认格式
		SimpleDateFormat sdf = null;
		if(pattern == null || "".equals(pattern.trim())) {
			sdf = new SimpleDateFormat(PATTERN_DEFAULT);
		}
		else {
			sdf = new SimpleDateFormat(pattern);
		}
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		result = c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH);

		return result == 0 ? 1 : Math.abs(result);

	}
	
	/**
	 * 计算两个日期之间相差几个月份
	 * 
	 * @param date1
	 * 		日期：2014-05
	 * @param date2
	 * 		日期：2014-08
	 * @return
	 * 		3
	 * @throws ParseException
	 */
	public static int getMonthSpace(String start, String end, String pattern) throws ParseException {
		// 如果pattern为空，采用默认格式
		SimpleDateFormat sdf = null;
		if(pattern == null || "".equals(pattern.trim())) {
			sdf = new SimpleDateFormat(PATTERN_DEFAULT);
		}
		else {
			sdf = new SimpleDateFormat(pattern);
		}
		
		Date startDate = sdf.parse(start); 
		Date endDate = sdf.parse(end);
		
		if (startDate.after(endDate)) {
            Date t = startDate;
            startDate = endDate;
            endDate = t;
        }
        
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        Calendar temp = Calendar.getInstance();
        temp.setTime(endDate);
        temp.add(Calendar.DATE, 1);

        int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month + 1;
        } else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month;
        } else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
            return year * 12 + month;
        } else {
            return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
        }
    }
	
	
	 /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    } 
    
    /**  
     * 计算两个日期之间相差的分钟数
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int minusBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_minus=(time2-time1)/(1000*60);  
            
       return Integer.parseInt(String.valueOf(between_minus));           
    } 
	
	/**
	 * 根据此日期计算前几个月或者后几个月的日期
	 * 		
	 * @param dateStr
	 * 		日期：
	 * @param month
	 * 		如果为正数，获取当前日期的后几个月的日期
	 * 		如果为负数，获取当前日期的前几个月的日期
	 * @param pattern
	 * 		日期格式
	 * @return
	 * @throws ParseException
	 */
	public static String getBeforeOrAfterDate(String dateStr, int month, String pattern) throws ParseException {
		// 如果pattern为空，采用默认格式
		SimpleDateFormat sdf = null;
		if(pattern == null || "".equals(pattern.trim())) {
			sdf = new SimpleDateFormat(PATTERN_DEFAULT);
		}
		else {
			sdf = new SimpleDateFormat(pattern);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(dateStr));
		cal.add(Calendar.MONTH, month);
		
		String str = sdf.format(cal.getTime());
		return str;
	}
	
	/**
	 * 根据此日期计算前几天或者后几天的日期
	 * 		
	 * @param dateStr
	 * 		日期：
	 * @param month
	 * 		如果为正数，获取当前日期的后几天的日期
	 * 		如果为负数，获取当前日期的前几天的日期
	 * @param pattern
	 * 		日期格式
	 * @return
	 * @throws ParseException
	 */
	public static String getBeforeOrAfterDayDate(String dateStr, int day, String pattern) throws ParseException {
		// 如果pattern为空，采用默认格式
		SimpleDateFormat sdf = null;
		if(pattern == null || "".equals(pattern.trim())) {
			sdf = new SimpleDateFormat(PATTERN_BASE);
		}
		else {
			sdf = new SimpleDateFormat(pattern);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(dateStr));
		cal.add(Calendar.DAY_OF_MONTH, day);
		
		String str = sdf.format(cal.getTime());
		return str;
	}
	
	/**
	 * 日期格式转换
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 日期格式转换
	 * 
	 * @param dateStr
	 * 		2014-02-01
	 * @param sourcePattern
	 * 		"yyyy-MM-dd" 原有格式
	 * @param targetPattern
	 * 		"yyyy/MM" 目标格式：2014/02
	 * @return
	 * @throws ParseException
	 */
	public static String fromatDate(String dateStr, String sourcePattern, String targetPattern) throws ParseException {
		String str = null;
		
		// 如果pattern为空，采用默认格式
		SimpleDateFormat sdf_target = null;
		if(targetPattern == null || "".equals(targetPattern.trim())) {
			sdf_target = new SimpleDateFormat(PATTERN_DEFAULT);
		}
		else {
			sdf_target = new SimpleDateFormat(targetPattern);
		}
		
		SimpleDateFormat sdf_source = new SimpleDateFormat(sourcePattern);
		Date date = sdf_source.parse(dateStr);
		str = sdf_target.format(date);
		
		return str;
	}
	
	/**
	 * 比较两个日期的大小
	 * 
	 * @param dateStr1
	 * @param dateStr2
	 * @return
	 * @throws ParseException
	 */
	public static boolean compare(String dateStr1, String dateStr2, String pattern) throws ParseException {
		
		if(StringUtils.isEmpty(dateStr1) || StringUtils.isEmpty(dateStr2)) {
			return false;
		}
		
		SimpleDateFormat sdf = null;
		if(StringUtils.isEmpty(pattern)) {
			sdf = new SimpleDateFormat(PATTERN_DEFAULT);
		}
		else {
			sdf = new SimpleDateFormat(pattern);
		}
		
		Date date1 = sdf.parse(dateStr1);
		Date date2 = sdf.parse(dateStr2);
		
		if(date1.getTime() >= date2.getTime()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 将字符串转换为Date类型
	 * @param source
	 * @return
	 */
	public static Date parse2Date(String source) {
		if (source.length() == 0 || org.apache.commons.lang.StringUtils.isBlank(source)) {
			return null;
		}
		SimpleDateFormat sdf = null;
		try {
			sdf = new SimpleDateFormat(PATTERN_BASE);
			return sdf.parse(source);
		} catch (Exception e) {
			throw new RuntimeException(source + "类型转换失败");
		}
	}
	
	/**
	 * 将字符串转换为Date类型
	 * @param source
	 * @return
	 */
	public static String parse2String(Date source, String format) {
		if (source == null) {
			return null;
		}
		SimpleDateFormat sdf = null;
		try {
			sdf = new SimpleDateFormat(format);
			return sdf.format(source);
		} catch (Exception e) {
			throw new RuntimeException(source + "类型转换失败");
		}
	}

	/**
	 * @Description  将 2016.1.9
	 * 				   2016/1/9
	 * 				   2016-01-9
	 * 				   2016。01。9
	 * 				 类型数据转换成yyyy-MM-dd
	 * @param dateStr
	 */
	public static String formatToString(String dateStr, int columIndex) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(org.apache.commons.lang.StringUtils.isNotBlank(dateStr)){
				String[] dateArray = null;
				for(String separatorStr:SEPARATOR){
					dateArray = dateStr.split(separatorStr);
					if(dateArray!=null&&dateArray.length == 3){
						break;
					}
				}
				
				if(dateArray==null || dateArray.length !=3){
					if (columIndex != 0) {
						throw new IllegalArgumentException("第"+ columIndex +"行日期格式异常");
					}else {
						throw new IllegalArgumentException("日期格式异常");
					}
				}
				
				String year = dateArray[0];
				String month = dateArray[1];
				String day = dateArray[2];
				
				dateStr = year+"-"+month+"-"+day;
				
				dateStr =sdf.format(sdf.parse(dateStr));
			}
		} catch (ParseException e) {
			if (columIndex != 0) {
				throw new IllegalArgumentException("第"+ columIndex +"行日期格式异常");
			}else {
				throw new IllegalArgumentException("日期格式异常");
			}
		}
		return dateStr;
	}
	
	/**
     * 获取某年某周的起始时间和结束时间
     * 
     * @param year
     * @param weekindex
     * @return
     */
    public static String[] getDayOfWeek(int year, int weekindex) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setWeekDate(year, weekindex, 1);
 
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
        c.add(Calendar.DATE, -dayOfWeek); // 得到本周的第一天
        String begin = sdf.format(c.getTime());
        c.add(Calendar.DATE, 6); // 得到本周的最后一天
        String end = sdf.format(c.getTime());
        String[] range = new String[2];
        range[0] = begin;
        range[1] = end;
        return range;
    }
}
