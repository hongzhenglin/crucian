package com.crucian.common.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * 
 * 功能说明:properties帮助类
 *
 * @author Y
 * 
 * @Date 2015年9月18日 下午4:20:17
 *
 *
 *       版本号 | 作者 | 修改时间 | 修改内容
 *
 */
public class PropertiesHelper {

	/*
	 * 从文件名读到Properties对象,文件路径需要加入到classpath
	 * "schedule.properties"
	 */
	public static Properties getProperties(String fileName) throws IOException {
		Properties properties = new Properties();
		
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			if (classLoader == null) {
				classLoader = PropertiesHelper.class.getClassLoader();
			}
			properties.load(classLoader.getResourceAsStream(fileName));
		
		return properties;
	}

	
}
