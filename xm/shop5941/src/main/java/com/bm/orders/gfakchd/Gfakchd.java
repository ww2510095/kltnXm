package com.bm.orders.gfakchd;


import com.bm.base.BaseEN;
import lombok.Data;
@Data
public class Gfakchd extends BaseEN {
	
	private Long id;	//	
	private String ming_c;	//名称	
	private String jie_s;	//介绍	
	private String tu_p;	//图片	
	private String huo_dsp;	//商品	
	private Long kai_ssj;	//开始时间	
	private Long jie_ssj;	//结束时间
	
	
}
