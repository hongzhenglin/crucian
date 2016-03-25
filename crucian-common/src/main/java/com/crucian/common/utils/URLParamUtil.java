package com.crucian.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParamUtil {

	private final static Pattern paramsPat = Pattern.compile("([^=]*)=?(.*)");
	
	private final static String UTF_8 = "UTF-8";
	
	public static Map<String, String> parseQueryString(String query) {
		try {
			Map<String, String> params = new HashMap<String, String>();

			for (String p : query.split("&")) {
				Matcher m = paramsPat.matcher(p);
				m.matches();
				String key = URLDecoder.decode(m.group(1), UTF_8);
				String value = URLDecoder.decode(m.group(2), UTF_8);
				params.put(key, URLDecoder.decode(value, UTF_8));
			}
			return params;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encode(Map<String, String> params) {
		try {
			StringBuilder sb = new StringBuilder();
			boolean first = true;

			for (Map.Entry<String, String> p : params.entrySet()) {
				if (first)
					first = false;
				else
					sb.append("&");
				sb.append(URLEncoder.encode(p.getKey(), UTF_8));
				sb.append("=");
				sb.append(URLEncoder.encode(p.getValue(), UTF_8));
			}

			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
