package com.bm.base.request;

public class RequestType {
	private long timestamp;//当前时间
	private int status; //状态：200：成功
	private String message;//提示信息
	private Object data;//数据信息
	private long runtime;//运行时间
	
	
	
	
	public long getRuntime() {
		return runtime;
	}
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "RequestType [timestamp=" + timestamp + ", status=" + status + ", message=" + message + ", data=" + data
				+ ", runtime=" + runtime + "]";
	}
	
}
