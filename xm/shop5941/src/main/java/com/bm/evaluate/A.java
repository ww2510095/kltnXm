package com.bm.evaluate;

import com.bm.base.MyParameter;
import com.myjar.desutil.RunException;

public class A {

	public A() {
		if(!MyParameter.order_true)throw new RunException("评价加载错误");
		MyParameter.evaluate_true=true;
	}
}
