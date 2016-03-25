package org.crucian.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.crucian.rpc.core.client.RpcProxy;
import com.crucian.rpc.service.api.HelloService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {
	@Autowired
	private RpcProxy rpcProxy;

	@Test
	public void helloTest() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello("World");
		Assert.assertEquals("Hello! World", result);
	}
}