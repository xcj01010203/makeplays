package com.xiaotu.makeplays.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * @author xuchangjian
 */
public class RegexUtils {

	/**
	 * 判断字符串和指定的正则表达式是否匹配
	 * @param reg 正则表达式
	 * @param str 带匹配的字符串
	 * @return
	 */
	public static boolean regexFind(String reg, String str) {
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}
	
	/**
	 * 通过正则表达式拆分字符串
	 * @param reg
	 * @param str
	 * @return
	 */
	public static String[] regexSplitStr(String reg, String str) {
		if (str == null) {
			str = "";
		}
		return str.split(reg);
	}
	
	/**
	 * 替换
	 */
	public static String replaceStr(String str, String rex, String replaceStr) {
		Matcher matcher = Pattern.compile(rex).matcher(str);
		return matcher.replaceAll(replaceStr);
	}
	
	/**
	 * 根据正则表达式去除字符串中的空格
	 * @param str
	 * @return
	 */
	public static String replaceSpace(String str) {
		return RegexUtils.replaceStr(str, Constants.REGEX_SPACE_CHAR, "");
	}
}
