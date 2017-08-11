package com.xiaotu.makeplays.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date> {

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Date convert(String source) {
		if (source.length() == 0) {
			return null;
		}
		try {
			return format.parse(source);
		} catch (Exception e) {
			throw new RuntimeException(source + "类型转换失败");
		}
	}

}
