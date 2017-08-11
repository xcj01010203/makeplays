package com.xiaotu.makeplays.utils;

import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * <core>字符串的工具类</core>
 * 
 * @author Administrator
 * 
 * @version
 *
 */
public class StringUtils {
	
	// 1M=1024*1024b
	public static final long M = 1048576;
	
	// 1G=1024*1024*1024b
	public static final long G = 1073741824;
	
	/**
	 * 判断字符串是否是空串
	 * 
	 * @param str
	 * 		待判断的字符串
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		}

		return false;
	}
	
	/**
	 * 判断字符串是否是空串
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if(isEmpty(str) || "null".equals(str)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断字符串是否非空
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * 判断字符串是否是数字
	 * 
	 * @param str
	 * 		待判断的字符串
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 计算两个浮点数的和
	 * @param a
	 * @param b
	 * @return
	 */
	public static double add(double a, double b) {
		BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.add(b2).doubleValue();
	}
	
	/** 
     * 提供精确的减法运算。 
     *  
     * @param v1 
     *            被减数 
     * @param v2 
     *            减数 
     * @return 两个参数的差 
     */  
  
    public static double sub(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.subtract(b2).doubleValue();  
    } 
    
    /** 
     * 提供精确的乘法运算。 
     *  
     * @param v1 
     *            被乘数 
     * @param v2 
     *            乘数 
     * @return 两个参数的积 
     */  
  
    public static double mul(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.multiply(b2).doubleValue();  
    }  
    
    /** 
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
     *  
     * @param v1 
     *            被除数 
     * @param v2 
     *            除数 
     * @param scale 
     *            表示表示需要精确到小数点以后几位。 
     * @return 两个参数的商 
     */  
  
    public static double div(double v1, double v2, int scale) {  
        if(scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
    
    /** 
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
     *  
     * @param v1 
     *            被除数 
     * @param v2 
     *            除数 
     * @param scale 
     *            表示表示需要精确到小数点以后几位。 
     * @return 两个参数的商 
     */  
  
    public static double div(int v1, int v2, int scale) {  
        if(scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal b1 = new BigDecimal(Integer.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
    
    /** 
     * 将字节转换为M，G
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
     *  
     * @param v1 
     *            被除数 
     * @param scale 
     *            表示表示需要精确到小数点以后几位。 
     * @return 两个参数的商 
     */  
    public static String div(long v1, int scale) {
    	 if(scale < 0) {  
             throw new IllegalArgumentException("The scale must be a positive integer or zero");  
         }  
         BigDecimal b1 = new BigDecimal(v1);
         if(v1 > G) {
        	 BigDecimal b2 = new BigDecimal(G);  
        	 return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue() + "G";  	
	     } else {
	    	 BigDecimal b2 = new BigDecimal(M);  
	    	 return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue() + "M";  		
	     }
         
    }
    
    /**
     * 获取随机数(范围小于10000)
     * 
     * @return
     */
    public static String getRandom() {
    	Random rdm = new Random();
    	int num = rdm.nextInt(1000000);
    	while(num > 10000) {
    		num = rdm.nextInt(1000000);
    	}
    	
    	return String.valueOf(num);
    }
    
    /**
     * 四舍五入
     * @param data
     * @param scale
     *            表示表示需要精确到小数点以后几位。 
     * @return
     */
	public static double round(double data,int scale){
		if(scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        } 
		BigDecimal bigDecimal=new BigDecimal(data);
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 针对[1, 5, 4b, 4a, 7, 5c, 5a, 1]类型的字符串排序成[1, 1, 4a, 4b, 5, 5a, 5c, 7]的算法
	 * @return
	 */
	public static Comparator<String> sort() {
		Comparator<String> com = new Comparator<String>() {
            public int compare(String o1, String o2) {
                // 前面3个IF主要是判空的
                if (o1 == o2) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                // 这里没有做太多的判断, index 代表第几个开始是数字
                int index = 0;
                for (;index <= o1.length() -1 && 
                		index >= 0 
                		&& (o1.charAt(index) >= '0' && o1.charAt(index) <= '9'); index++) {
                	
                }
                String str1 = o1.substring(index);
                int num1 = 0;
                if (index != 0) {
                	num1 = Integer.parseInt(o1.substring(0, index));
                }
                
                index = 0;
                for (;index <= o2.length() -1 && index >= 0 && (o2.charAt(index) >= '0' && o2.charAt(index) <= '9'); index++) {
                	
                }
                String str2 = o2.substring(index);
                int num2 = 0;
                if (index != 0) {
                	num2 = Integer.parseInt(o2.substring(0, index));
                }
                if (num1 == num2) {
                	return str1.compareTo(str2);
                }
                return num1 - num2;
            }
        };
        return com;
	}
	
	/**
	 * 针对汉字进行排序的算法，结果为按照汉字的首字母升序排列
	 * @return
	 */
	public static Comparator<String> chineseSort() {
		Comparator<String> com = new Comparator<String>() {
            public int compare(String o1, String o2) {
            	// 把字符串转换为一系列比特，它们可以以比特形式与 CollationKeys 相比较
        		CollationKey key1 = Collator.getInstance().getCollationKey(o1.toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.toString().toLowerCase());
        		return key1.compareTo(key2);
            }
        };
        return com;
	}
	
	/**
	 * 比较两个字符串是否相同
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equalStr(String a, String b) {
		if (org.apache.commons.lang.StringUtils.isBlank(a) && org.apache.commons.lang.StringUtils.isBlank(b)) {
			return true;
		}
		if (a.equals(b)) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 把英文符号转为中文符号
	 * @param separator
	 * @return
	 */
	public static String EnToCHSeparator(String separator) {
		String changedStr = separator
				.replaceAll(":", "：")
				.replaceAll(",", "，")
				.replaceAll(";", "；")
				.replaceAll("\\)", "）")
				.replaceAll("\\(", "（");
		return changedStr;
	}
	
	/**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
             char c[] = input.toCharArray();
             for (int i = 0; i < c.length; i++) {
               if (c[i] == ' ') {
                 c[i] = '\u3000';
               } else if (c[i] < '\177') {
                 c[i] = (char) (c[i] + 65248);
               }
             }
             return new String(c);
    }

	/**
	 * 全角转半角
	 * 
	 * @param input
	 *            String.
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}

	/**
	 * 取汉字拼音首字母
	 * 
	 * @return 大写首字母
	 */
	public static String String2Alpha(String c) {
		
		for (int i = 0; i < 27; ++i) {
			table[i] = gbValue(String.valueOf(chartable[i]));
		}
		
		char result = '0';

		char ch = c.charAt(0);

		if (ch >= 'a' && ch <= 'z')

			result = (char) (ch - 'a' + 'A');

		if (ch >= 'A' && ch <= 'Z')

			result = ch;

		int gb = gbValue(c);

		if (gb < table[0])

			result = '0';

		int i;

		for (i = 0; i < 26; ++i) {

			if (match(i, gb))
				break;
		}

		if (i >= 26)

			result = '0';
		else
			result = alphatable[i];

		return String.valueOf(result);
	}
	
	/**
	 * 大写数字转换为小写
	 * @param str
	 * @return
	 */
	public static int toNumberLower(String str) {
		String[] numbers = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
		String[] units = {"十", "百", "千"};
		
		String[] array = str.trim().split("");
		
		int result = 0;
		
		int preNumber = 1;
		for (int i = 0; i < array.length; i++) {
			String a = array[i];
			if (org.apache.commons.lang.StringUtils.isBlank(a)) {
				continue;
			}
			
			for (int j = 0; j < numbers.length; j++) {
				if (a.equals(numbers[j])) {
					if (i == array.length - 1) {
						result += j + 1;
					} else {
						preNumber = j + 1;
					}
					break;
				}
			}
			for (int k = 0; k < units.length; k++) {
				if (a.equals(units[k])) {
					result += (preNumber *= Math.pow(10, (k+1)));
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 获取字符串表示的中文场次信息，示例：
	 * “三十”：场次为“30”
	 * “三十扉页”：场次为“30扉页”
	 * “三十ins”： 场次为“30ins”
	 * “三十2ins”：不识别为场次
	 * “三2十”：不识别为场次
	 * “这三十”：不识别为场次
	 * @param str
	 * @return 如果返回为空，表示不识别的场次
	 */
	public static String genCNViewNo(String str) {
		boolean upperViewNo = RegexUtils.regexFind("^(零|一|二|三|四|五|六|七|八|九|十)+.*", str);
		boolean lowerViewNo = RegexUtils.regexFind("^\\d+.*", str);
		
		String result = "";
		if (lowerViewNo) {
			result = str;
		}
		if (upperViewNo) {
			String [] strArray = str.split("");
			List<String> numberCNList = Arrays.asList("零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百", "千");
			
			String numberCn = "";
			String restStr = "";
			for (int i = 1; i < strArray.length; i++) {
				if (numberCNList.contains(strArray[i])) {
					if (i == strArray.length - 1) {
						numberCn = str;
						restStr = "";
					}
					continue;
				} else {
					numberCn = str.substring(0, i-1);
					restStr = str.substring(i-1, str.length());
					break;
				}
			}
			
			//如果碰到“三十2”这种类型的场次，标识为不识别的场次
			if (!StringUtils.isBlank(restStr) && RegexUtils.regexFind("^\\d+.*", restStr)) {
				restStr = "";
				numberCn = "";
			}
			
			if (!StringUtils.isBlank(numberCn)) {
				result = com.xiaotu.makeplays.utils.StringUtils.toNumberLower(numberCn) + restStr;
			}
		}
		
		return result;
	}
	
	private static char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期',
			'然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座' };
	private static char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	
	private static int[] table = new int[27];

	private static boolean match(int i, int gb) {

		if (gb < table[i])
			return false;
		int j = i + 1;

		while (j < 26 && (table[j] == table[i]))
			++j;
		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];
	}

	private static int gbValue(String c) {

		try {
			byte[] bytes = c.getBytes("GB2312");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 将List<String>转化成string字符串
	 * @param list
	 * @return
	 */
	public static String listToString(List<String> stringList, String separator) {
		if (stringList == null) {
            return null;
        }
		if (separator == null) {
			separator = ",";
		}
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : stringList) {
            if (flag) {
                result.append(separator);
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
	}
}
