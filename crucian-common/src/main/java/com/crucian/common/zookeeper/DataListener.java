package com.crucian.common.zookeeper;

public interface DataListener {
	void dataChanged(String path, String newData);
}
