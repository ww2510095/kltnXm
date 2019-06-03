package com.example.fw.base;

import lombok.Data;


@Data
public class RequestType {
	private long timestamp;//当前时间
	private int status; //状态：200：成功
	private String message;//提示信息
	private Object data;//数据信息
	private long runtime;//运行时间
	private boolean listtrue=false;
	

	
}
