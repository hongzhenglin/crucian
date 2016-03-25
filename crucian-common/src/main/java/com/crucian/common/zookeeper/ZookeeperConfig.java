package com.crucian.common.zookeeper;

/**
 * 基于Zookeeper的注册中心配置.
 * 
 * @author linhz
 */

public class ZookeeperConfig {

 
	private static final String Default_Namespace = "ctg_schedule";

	private static final int Default_BaseSleepTimeMilliseconds = 1000;

	private static final int Default_MaxSleepTimeMilliseconds = 3000;

	private static final int Default_MaxRetries = 5;

	/**
	 * 连接Zookeeper服务器的列表. 包括IP地址和端口号. 多个地址用逗号分隔. 如: host1:2181,host2:2181
	 */
	private String serverLists;

	/**
	 * 命名空间.
	 */
	private String namespace;

	/**
	 * 等待重试的间隔时间的初始值. 单位毫秒.
	 */
	private int baseSleepTimeMilliseconds;

	/**
	 * 等待重试的间隔时间的最大值. 单位毫秒.
	 */
	private int maxSleepTimeMilliseconds;

	/**
	 * 最大重试次数.
	 */
	private int maxRetries;

	/**
	 * 会话超时时间. 单位毫秒.
	 */
	private int sessionTimeoutMilliseconds;

	/**
	 * 连接超时时间. 单位毫秒.
	 */
	private int connectionTimeoutMilliseconds;

	/**
	 * 连接Zookeeper的权限令牌. 缺省为不需要权限验证.
	 */
	private String digest;

	/**
	 * 是否允许本地值覆盖注册中心.
	 */
	private boolean overwrite;

	public ZookeeperConfig(final String serverLists) {
		this(serverLists, Default_Namespace, Default_BaseSleepTimeMilliseconds,
				Default_MaxSleepTimeMilliseconds, Default_MaxRetries);
	}

	/**
	 * 包含了必需属性的构造器.
	 * 
	 * @param serverLists
	 *            连接Zookeeper服务器的列表
	 * @param baseSleepTimeMilliseconds
	 *            等待重试的间隔时间的初始值
	 * @param maxSleepTimeMilliseconds
	 *            等待重试的间隔时间的最大值
	 * @param maxRetries
	 *            最大重试次数
	 */
	public ZookeeperConfig(final String serverLists, final String nameSpace,
			final int baseSleepTimeMilliseconds,
			final int maxSleepTimeMilliseconds, final int maxRetries) {
		this.serverLists = serverLists;
		this.namespace = nameSpace;
		this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
		this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
		this.maxRetries = maxRetries;
	}

	public String getServerLists() {
		return serverLists;
	}

	public void setServerLists(String serverLists) {
		this.serverLists = serverLists;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public int getBaseSleepTimeMilliseconds() {
		return baseSleepTimeMilliseconds;
	}

	public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
		this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
	}

	public int getMaxSleepTimeMilliseconds() {
		return maxSleepTimeMilliseconds;
	}

	public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
		this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getSessionTimeoutMilliseconds() {
		return sessionTimeoutMilliseconds;
	}

	public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
		this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
	}

	public int getConnectionTimeoutMilliseconds() {
		return connectionTimeoutMilliseconds;
	}

	public void setConnectionTimeoutMilliseconds(
			int connectionTimeoutMilliseconds) {
		this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
