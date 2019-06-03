package com.example.fw.base;

public class MyParameter {
	

	
	/** Tomcat虚拟盘符 */
	public static final String TomcatSD = "D:/";
	/** Tomcat虚拟目录 */
	public static final String Tomcat = TomcatSD+"upload/";//正常文件路径
//	public static final String Tomcat = TomcatSD+"debug/";//debug文件路径
	/** 图片虚拟目录 */
	public static final String TomcatFileImage = Tomcat+"images/";
	/** 文件队列，非图片的文件放到另外一个文件夹 */
	public static final String TomcatFile = Tomcat+"file/";


}
