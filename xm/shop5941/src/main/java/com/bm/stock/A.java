package com.bm.stock;

import com.bm.base.MyParameter;
import com.myjar.desutil.RunException;

public class A {
	
	/**
	 * 库存系统
	 * 此系统必须依赖商品系统
	 * */
	public A() {
		if(!MyParameter.commodity_true)throw new RunException("库存错误，未检测到商品系统");
		MyParameter.stock_true=true;
	}

}
