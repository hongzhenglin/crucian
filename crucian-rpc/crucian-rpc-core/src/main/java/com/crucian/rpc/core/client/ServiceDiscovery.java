package com.crucian.rpc.core.client;

import io.netty.util.internal.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crucian.common.zookeeper.ChildListener;
import com.crucian.common.zookeeper.ZookeeperConfig;
import com.crucian.common.zookeeper.curator.CuratorZookeeperClient;
import com.crucian.rpc.core.common.Constant;

public class ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceDiscovery.class);

	private List<String> addressList = new ArrayList<String>();

	private String registryAddress;
	private CuratorZookeeperClient curatorZookeeperClient;

	public ServiceDiscovery(String registryAddress) {
		this.registryAddress = registryAddress;
		this.curatorZookeeperClient = new CuratorZookeeperClient(
				new ZookeeperConfig(registryAddress));
		if (curatorZookeeperClient != null) {
			watchNode(curatorZookeeperClient);
		}

	}

	private void watchNode(final CuratorZookeeperClient curatorZookeeperClient) {

		curatorZookeeperClient.addChildListener(Constant.ZK_REGISTRY_PATH,
				new ChildListener() {
					public void childChanged(PathChildrenCacheEvent event) {

						List<String> nodeList = curatorZookeeperClient
								.getChildren(Constant.ZK_REGISTRY_PATH);
						List<String> dataList = new ArrayList<>();
						for (String node : nodeList) {
							String address = curatorZookeeperClient
									.getData(Constant.ZK_REGISTRY_PATH + "/"
											+ node);
							dataList.add(address);
						}
						addressList = dataList;
						LOGGER.debug("node data: {}", dataList);
					}
				});
	}

	public String discover() {
		String data = null;
		int size = addressList.size();
		if (size > 0) {
			if (size == 1) {
				data = addressList.get(0);
				LOGGER.debug("using only data: {}", data);
			} else {
				data = addressList.get(ThreadLocalRandom.current()
						.nextInt(size));
				LOGGER.debug("using random data: {}", data);
			}
		}
		return data;
	}

}
