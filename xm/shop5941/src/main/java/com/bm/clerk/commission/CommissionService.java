package com.bm.clerk.commission;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class CommissionService extends BaseService {
	// 正常的金额
	@Override
	public String getTabName() {
		return "Commission";
	}

//	@Override
//	public synchronized int add(Object object) throws Exception {
//		Thread.sleep(1);
//		object.getClass().getMethod("setId", Long.class).invoke(object, System.currentTimeMillis());// 设置id
//		return 1;
//
//	}

}
