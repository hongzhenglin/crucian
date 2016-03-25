package com.crucian.rpc.service.impl;

import com.crucian.rpc.core.server.RpcService;
import com.crucian.rpc.service.api.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		return "Hello! " + name;

	}

}
