package com.xiaotu.makeplays.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

	// double 数字精度，小数点后位数
	static int scale = 2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		double a = 1234567890;
		double b = 1234567890;
		
		double c = add(a,b);
		System.out.println(c);
		
//		DecimalFormat df1 = new DecimalFormat("0.000000");
//		String s = df1.format(c);
//		System.out.println(s);
		
	}

	/**
	 * 加法
	 * @param a
	 * @param b
	 * @return
	 */
	public static double add(double a, double b) {

		BigDecimal bd1 = new BigDecimal(Double.toString(a));
		BigDecimal bd2 = new BigDecimal(Double.toString(b));
		
		BigDecimal result = bd1.add(bd2);
		
		return result.doubleValue();
	}

	/**
	 * 减法
	 * @param a
	 * @param b
	 * @return
	 */
	public static double subtract(double a, double b) {

		BigDecimal bd1 = new BigDecimal(Double.toString(a));
		BigDecimal bd2 = new BigDecimal(Double.toString(b));

		return bd1.subtract(bd2).doubleValue();
	}

	/**
	 * 乘法
	 * @param a
	 * @param b
	 * @return
	 */
	public static double multiply(double a, double b) {

		BigDecimal bd1 = new BigDecimal(Double.toString(a));
		BigDecimal bd2 = new BigDecimal(Double.toString(b));

		return bd1.multiply(bd2).doubleValue();
	}

	/**
	 * 除法（保留两位小数）
	 * @param a
	 * @param b
	 * @return
	 */
	public static double divide(double a, double b) {

		BigDecimal bd1 = new BigDecimal(Double.toString(a));
		BigDecimal bd2 = new BigDecimal(Double.toString(b));

		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 除法，可自定义保留小数位数
	 * @param a
	 * @param b
	 * @param scale
	 * @return
	 */
	public static double divide(double a, double b, int scale) {

		BigDecimal bd1 = new BigDecimal(Double.toString(a));
		BigDecimal bd2 = new BigDecimal(Double.toString(b));

		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
