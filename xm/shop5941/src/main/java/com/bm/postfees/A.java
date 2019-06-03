package com.bm.postfees;

import com.bm.base.MyParameter;
import com.myjar.desutil.RunException;

public class A {
	
	public A() {
		if(!MyParameter.order_true)throw new RunException("未找到订单模块");
		MyParameter.postfees=true;
	}

}
