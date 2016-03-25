package com.crucian.common.zookeeper.curator;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crucian.common.utils.StringUtils;
import com.crucian.common.zookeeper.ChildListener;
import com.crucian.common.zookeeper.DataListener;
import com.crucian.common.zookeeper.StateListener;
import com.crucian.common.zookeeper.ZookeeperConfig;
import com.crucian.common.zookeeper.support.AbstractZookeeperClient;

/**
 * @author linhz
 */
public class CuratorZookeeperClient extends AbstractZookeeperClient {

	private static final String DEFAULT_DIGEST = "digest";

	private static final Logger logger = LoggerFactory
			.getLogger(CuratorZookeeperClient.class);

	private CuratorFramework client;
	private final ZookeeperConfig zkConfig;
	private TreeCache cache;

	/**
	 * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
	 */
	private static ExecutorService pool = Executors.newFixedThreadPool(2);

	public CuratorZookeeperClient(final ZookeeperConfig zookeeperConfig) {
		this.zkConfig = zookeeperConfig;
	}

	public void init() {

		Builder builder = CuratorFrameworkFactory
				.builder()
				.connectString(zkConfig.getServerLists())
				.retryPolicy(
						new ExponentialBackoffRetry(zkConfig
								.getBaseSleepTimeMilliseconds(), zkConfig
								.getMaxRetries(), zkConfig
								.getMaxSleepTimeMilliseconds()))
				.namespace(zkConfig.getNamespace());
		if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
			builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
		}
		if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
			builder.connectionTimeoutMs(zkConfig
					.getConnectionTimeoutMilliseconds());
		}
		digest(builder, zkConfig.getDigest());
		client = builder.build();

		addStateChangedListener();

		client.start();
		try {
			cacheData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void digest(Builder builder, final String digest) {
		if (!StringUtils.isNullOrEmpty(digest)) {
			builder.authorization(DEFAULT_DIGEST,
					digest.getBytes(Charset.forName("UTF-8"))).aclProvider(
					new ACLProvider() {

						public List<ACL> getDefaultAcl() {
							return ZooDefs.Ids.CREATOR_ALL_ACL;
						}

						public List<ACL> getAclForPath(final String path) {
							return ZooDefs.Ids.CREATOR_ALL_ACL;
						}
					});
		}
	}

	private void addStateChangedListener() {
		client.getConnectionStateListenable().addListener(
				new ConnectionStateListener() {
					public void stateChanged(CuratorFramework client,
							ConnectionState state) {
						if (state == ConnectionState.LOST) {
							CuratorZookeeperClient.this
									.stateChanged(StateListener.DISCONNECTED);
						} else if (state == ConnectionState.CONNECTED) {
							CuratorZookeeperClient.this
									.stateChanged(StateListener.CONNECTED);
						} else if (state == ConnectionState.RECONNECTED) {
							CuratorZookeeperClient.this
									.stateChanged(StateListener.RECONNECTED);
						} else if (state == ConnectionState.SUSPENDED) {
							CuratorZookeeperClient.this
									.stateChanged(StateListener.DISCONNECTED);
						}
					}
				});
	}

	/**
	 * 监听数据节点的变化情况
	 */
	public void watchNodeData(final String path) {

		final NodeCache nodeCache = new NodeCache(client, path, false);
		try {
			nodeCache.start(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		nodeCache.getListenable().addListener(new NodeCacheListener() {

			public void nodeChanged() throws Exception {
				System.out.println("Node data is changed, new data: "
						+ new String(nodeCache.getCurrentData().getData()));
			}
		}, pool);
	}

	private void cacheData() throws Exception {
		cache = new TreeCache(client, "/");
		cache.start();
	}

	@Override
	protected String createPersistent(String path, boolean sequential) {
		try {

			if (sequential) {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT_SEQUENTIAL)
						.forPath(path);
			} else {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT).forPath(path);
			}
		} catch (KeeperException.NodeExistsException e) {
			return path;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected String createPersistent(final String path, final String data,
			boolean sequential) {
		try {
			if (sequential) {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT_SEQUENTIAL)
						.forPath(path, data.getBytes(Charset.forName("UTF-8")));
			} else {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT)
						.forPath(path, data.getBytes(Charset.forName("UTF-8")));
			}
		} catch (KeeperException.NodeExistsException e) {
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected String createEphemeral(String path, boolean sequential) {
		try {

			if (sequential) {

				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.forPath(path);
			} else {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.EPHEMERAL).forPath(path);
			}
		} catch (KeeperException.NodeExistsException e) {
			return path;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private void update(final String key, final String value) {
		try {
			client.inTransaction().check().forPath(key).and().setData()
					.forPath(key, value.getBytes(Charset.forName("UTF-8")))
					.and().commit();

		} catch (final Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected String createEphemeral(final String path, final String data,
			boolean sequential) {
		try {
			if (sequential) {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.forPath(path, data.getBytes(Charset.forName("UTF-8")));
			} else {
				return client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.EPHEMERAL)
						.forPath(path, data.getBytes(Charset.forName("UTF-8")));
			}
		} catch (KeeperException.NodeExistsException e) {
			return path;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected NodeCacheListener addTargetNodeListener(String path,
			DataListener dataListener) {
		final NodeCache nodeCache = new NodeCache(client, path, false);
		final NodeCacheListener listener = new NodeCacheListenerImpl(path,
				nodeCache, dataListener);
		try {
			nodeCache.start(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		nodeCache.getListenable().addListener(listener, pool);
		return listener;
	}

	@Override
	protected void removeTargetNodeListener(String path,
			NodeCacheListener listener) {
		((NodeCacheListenerImpl) listener).unwatch();
	}

	@Override
	protected PathChildrenCacheListenerImpl createTargetChildListener(
			String path, ChildListener listener) {
		return new PathChildrenCacheListenerImpl(listener);
	}

	@Override
	protected void addTargetChildListener(String path,
			PathChildrenCacheListener listener) {
		try {
			final PathChildrenCache pathCache = new PathChildrenCache(client,
					path, false);
			pathCache.start();

			logger.info("监听开始" + path);
			// 注册监听
			pathCache.getListenable().addListener(listener, pool);
		} catch (KeeperException.NoNodeException e) {
			e.printStackTrace();

		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected void removeTargetChildListener(String path,
			PathChildrenCacheListener listener) {
		((PathChildrenCacheListenerImpl) listener).unwatch();
	}

	public boolean delete(String path) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
			return true;
		} catch (KeeperException.NoNodeException e) {
			return true;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public boolean exists(String path) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			exists(path.substring(0, i));
		}
		try {
			return client.checkExists().forPath(path) != null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public String getData(final String path) {
		if (null == cache) {
			return null;
		}
		ChildData resultIncache = cache.getCurrentData(path);
		if (null != resultIncache) {
			return null == resultIncache.getData() ? null : new String(
					resultIncache.getData(), Charset.forName("UTF-8"));
		}
		return getDirectly(path);
	}

	private String getDirectly(final String key) {
		try {
			return new String(client.getData().forPath(key),
					Charset.forName("UTF-8"));

		} catch (final Exception ex) {

			return null;
		}
	}

	public void setData(final String path, final String data) {

		try {
			if (exists(path)) {
				update(path, data);
			} else {
				client.setData().forPath(path,
						data.getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public List<String> getChildren(String path) {
		try {
			List<String> result = client.getChildren().forPath(path);
			Collections.sort(result, new Comparator<String>() {

				public int compare(final String o1, final String o2) {
					return o2.compareTo(o1);
				}
			});
			return result;
		} catch (KeeperException.NoNodeException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public boolean isConnected() {
		return client.getZookeeperClient().isConnected();
	}

	@Override
	protected void doClose() {
		if (null != cache) {
			cache.close();
		}
		waitForCacheClose();
		CloseableUtils.closeQuietly(client);
	}

	/*
	 * TODO 等待500ms, cache先关闭再关闭client, 否则会抛异常 因为异步处理,
	 * 可能会导致client先关闭而cache还未关闭结束. 等待Curator新版本解决这个bug.
	 * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
	 */
	private void waitForCacheClose() {
		try {
			Thread.sleep(500L);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private class PathChildrenCacheListenerImpl implements
			PathChildrenCacheListener {
		private volatile ChildListener listener;

		public PathChildrenCacheListenerImpl(ChildListener listener) {
			this.listener = listener;
		}

		public void unwatch() {
			this.listener = null;
		}

		public void childEvent(CuratorFramework client,
				PathChildrenCacheEvent event) throws Exception {
			listener.childChanged(event);
		}
	}

	private class NodeCacheListenerImpl implements NodeCacheListener {

		private volatile String path;
		private volatile NodeCache nodeCache;
		private volatile DataListener listener;

		public NodeCacheListenerImpl(final String path,
				final NodeCache nodeCache, DataListener listener) {
			this.path = path;
			this.nodeCache = nodeCache;
			this.listener = listener;
		}

		public void unwatch() {
			this.listener = null;
		}

		public void nodeChanged() throws Exception {
			listener.dataChanged(path, new String(nodeCache.getCurrentData()
					.getData()));
		}
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}
	
	

}
