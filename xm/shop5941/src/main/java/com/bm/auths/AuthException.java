package com.bm.auths;

public class AuthException extends RuntimeException{

	/**
	 * 权限异常
	 */
	private static final long serialVersionUID = 1L;
	
	public AuthException(String msg) {
		super(msg);
	}
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	@Override
	public String toString() {
		return "出现了一个异常："+getMessage();
	}

}
