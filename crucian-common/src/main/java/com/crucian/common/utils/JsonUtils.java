package com.crucian.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class JsonUtils {
	private static SerializerFeature[] features = {
			SerializerFeature.WriteNullNumberAsZero,
			SerializerFeature.WriteNullStringAsEmpty };

	public static String beanToJson(Object object) {
		return JSON.toJSONString(object, features);
	}

	public static Object jsonToBean(String jsonStr, Class clazz) {
		return JSON.toJavaObject(JSON.parseObject(jsonStr), clazz);
	}

	public static String beanToJsonFilter(Object object, Class cls,
			String... attr) {
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(cls, attr);
		return JSON.toJSONString(object, filter, features);
	}
}
