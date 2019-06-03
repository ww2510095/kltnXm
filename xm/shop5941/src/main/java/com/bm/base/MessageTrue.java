package com.bm.base;

public class MessageTrue extends RuntimeException{

	/**
	 * 提示正常，一般用户静态方法，直接返回
	 */
	private static final long serialVersionUID = 2L;
	
	public MessageTrue(String msg) {
		super(msg);
	}
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	@Override
	public String toString() {
		return getMessage();
	}

}
