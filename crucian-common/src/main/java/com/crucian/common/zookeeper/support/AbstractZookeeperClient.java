package com.crucian.common.zookeeper.support;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crucian.common.zookeeper.ChildListener;
import com.crucian.common.zookeeper.DataListener;
import com.crucian.common.zookeeper.StateListener;
import com.crucian.common.zookeeper.ZookeeperClient;

 

/**
 * @author linhz
 */
public abstract class AbstractZookeeperClient implements ZookeeperClient {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractZookeeperClient.class);

	private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

	private final ConcurrentMap<String, ConcurrentMap<ChildListener, PathChildrenCacheListener>> childListeners = new ConcurrentHashMap<String, ConcurrentMap<ChildListener, PathChildrenCacheListener>>();
	private final ConcurrentMap<String, ConcurrentMap<DataListener, NodeCacheListener>> dataListeners = new ConcurrentHashMap<String, ConcurrentMap<DataListener, NodeCacheListener>>();

	private volatile boolean closed = false;

	public String create(String path, boolean ephemeral, boolean sequential) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			create(path.substring(0, i), false, false);
		}
		if (ephemeral) {
			return createEphemeral(path, sequential);
		} else {
			return createPersistent(path, sequential);
		}
	}

	public String create(String path, String data, boolean ephemeral,
			boolean sequential) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			create(path.substring(0, i), data, false, false);
		}
		if (ephemeral) {
			return createEphemeral(path, data, sequential);
		} else {
			return createPersistent(path, data, sequential);
		}
	}

	public Set<StateListener> getSessionListeners() {
		return stateListeners;
	}

	public void addStateListener(StateListener listener) {
		stateListeners.add(listener);
	}

	public void removeStateListener(StateListener listener) {
		stateListeners.remove(listener);
	}

	public NodeCacheListener addNodeListener(final String path,
			final DataListener listener) {

		ConcurrentMap<DataListener, NodeCacheListener> listeners = dataListeners
				.get(path);
		if (listeners == null) {
			dataListeners.putIfAbsent(path,
					new ConcurrentHashMap<DataListener, NodeCacheListener>());
			listeners = dataListeners.get(path);
		}
		NodeCacheListener targetListener = listeners.get(listener);

		if (targetListener == null) {
			listeners.putIfAbsent(listener,
					addTargetNodeListener(path, listener));
			targetListener = listeners.get(listener);
		}
		return targetListener;
	}

	public void addChildListener(final String path, final ChildListener listener) {
		ConcurrentMap<ChildListener, PathChildrenCacheListener> listeners = childListeners
				.get(path);
		if (listeners == null) {
			childListeners
					.putIfAbsent(
							path,
							new ConcurrentHashMap<ChildListener, PathChildrenCacheListener>());
			listeners = childListeners.get(path);
		}
		PathChildrenCacheListener targetListener = listeners.get(listener);
		if (targetListener == null) {
			listeners.putIfAbsent(listener,
					createTargetChildListener(path, listener));
			targetListener = listeners.get(listener);
		}
		addTargetChildListener(path, targetListener);
	}

	public void removeChildListener(String path, ChildListener listener) {
		ConcurrentMap<ChildListener, PathChildrenCacheListener> listeners = childListeners
				.get(path);
		if (listeners != null) {
			PathChildrenCacheListener targetListener = listeners
					.remove(listener);
			if (targetListener != null) {
				removeTargetChildListener(path, targetListener);
			}
		}
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			doClose();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	protected void stateChanged(int state) {
		for (StateListener stateListener : getSessionListeners()) {
			stateListener.stateChanged(state);
		}
	}

	protected abstract void doClose();

	protected abstract String createPersistent(String path, boolean sequential);

	protected abstract String createPersistent(String path, String data,
			boolean sequential);

	protected abstract String createEphemeral(String path, boolean sequential);

	protected abstract String createEphemeral(String path, String data,
			boolean sequential);

	protected abstract PathChildrenCacheListener createTargetChildListener(
			String path, ChildListener listener);

	protected abstract void addTargetChildListener(String path,
			PathChildrenCacheListener listener);

	protected abstract void removeTargetChildListener(String path,
			PathChildrenCacheListener listener);

	protected abstract NodeCacheListener addTargetNodeListener(String path,
			DataListener listener);

	protected abstract void removeTargetNodeListener(String path,
			NodeCacheListener listener);
}
