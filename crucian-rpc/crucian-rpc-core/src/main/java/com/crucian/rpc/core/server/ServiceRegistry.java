package com.crucian.rpc.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crucian.common.zookeeper.ZookeeperConfig;
import com.crucian.common.zookeeper.curator.CuratorZookeeperClient;
import com.crucian.rpc.core.common.Constant;

public class ServiceRegistry {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceRegistry.class);

	private CuratorZookeeperClient client;
	private String registryAddress;

	public ServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
		this.client = new CuratorZookeeperClient(new ZookeeperConfig(
				registryAddress));
	}

	public void register(final String data) {
		if (data != null) {
			if (client != null) {
				client.create(Constant.ZK_DATA_PATH, data, true, true);
			}
		}
	}

}
