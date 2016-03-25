package com.crucian.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {
	public static String getHostAddress() {
		String localAddress = "127.0.0.1";
		try {
			localAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return localAddress;
	}
}
