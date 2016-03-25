package com.crucian.common.zookeeper.serializer;

/**
 * @author linhz
 */
public class ZkMarshallingException extends RuntimeException {

	private static final long serialVersionUID = 5923681143373416737L;

	public ZkMarshallingException() {
		super();
	}

	public ZkMarshallingException(String message) {
		super(message);
	}

	public ZkMarshallingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZkMarshallingException(Throwable cause) {
		super(cause);
	}

}
