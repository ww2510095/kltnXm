package com.bm.clerk;

import com.bm.base.MyParameter;
import com.myjar.desutil.RunException;

public class A {

	public A() {
		if(!MyParameter.shop_true)throw new RunException("错误！店员必须依赖店铺存在");
		MyParameter.clerk_true=true;
	}
}
