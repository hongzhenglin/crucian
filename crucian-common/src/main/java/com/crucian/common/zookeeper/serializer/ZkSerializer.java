package com.crucian.common.zookeeper.serializer;

/**
 * @author linhz
 */
public interface ZkSerializer {

    public byte[] serialize(Object data) throws ZkMarshallingException;

    public Object deserialize(byte[] bytes) throws ZkMarshallingException;
}
