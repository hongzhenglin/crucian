package com.crucian.common.zookeeper;

import java.util.List;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * @author linhz
 */
public interface ChildListener {

	void childChanged(PathChildrenCacheEvent event);

}