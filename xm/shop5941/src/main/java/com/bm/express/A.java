package com.bm.express;

import com.bm.base.MyParameter;
import com.myjar.desutil.RunException;

public class A {
	public A() {
		if(!MyParameter.shop_true) throw new RunException("没有店铺怎么加载邮费？");
		MyParameter.express=true;
	}

}
