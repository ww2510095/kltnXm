package com.bm.commodity;

import java.util.ArrayList;
import java.util.List;

import com.bm.base.util.GsonUtil;

import lombok.Data;

@Data
public class kc_ys_cm_tm {
	
	private String kc; //库存
	private String colour; //颜色数组
	private String mysize; //尺码数组
	private String youcode; //别人的条码
	

	public static void main(String[] args) {
		List<kc_ys_cm_tm> mkc_ys_cm_tmlist =new ArrayList<>();
		kc_ys_cm_tm mkc_ys_cm_tm= new kc_ys_cm_tm();
		mkc_ys_cm_tm.setKc("20");
		mkc_ys_cm_tm.setColour("红色");
		mkc_ys_cm_tm.setMysize("10码");
		mkc_ys_cm_tm.setYoucode("123");
		mkc_ys_cm_tmlist.add(mkc_ys_cm_tm);
		kc_ys_cm_tm mkc_ys_cm_tm1= new kc_ys_cm_tm();
		mkc_ys_cm_tm1.setKc("10");
		mkc_ys_cm_tm1.setColour("蓝色");
		mkc_ys_cm_tm1.setMysize("20码");
		mkc_ys_cm_tm1.setYoucode("234");
		mkc_ys_cm_tmlist.add(mkc_ys_cm_tm1);
		System.out.println(GsonUtil.toJsonString(mkc_ys_cm_tmlist));
	}
}
