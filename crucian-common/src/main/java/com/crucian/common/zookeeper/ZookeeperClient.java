package com.crucian.common.zookeeper;

import java.util.List;

/**
 * 
 * @author linhz
 * @Date 2016-02-19
 *
 */
public interface ZookeeperClient {

	String create(String path, boolean ephemeral, boolean sequential);

	String create(String path, String data, boolean ephemeral,
			boolean sequential);

	boolean delete(String path);

	boolean exists(String path);

	<T> T getData(String path);

	void setData(String path, String data);

	List<String> getChildren(String path);

	void removeChildListener(String path, ChildListener listener);

	void addStateListener(StateListener listener);

	void removeStateListener(StateListener listener);

	boolean isConnected();

	void close();
}
