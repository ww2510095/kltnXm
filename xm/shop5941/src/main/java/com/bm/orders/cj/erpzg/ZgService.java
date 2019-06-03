package com.bm.orders.cj.erpzg;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ZgService extends BaseService{

	@Override
	protected String getTabName() {
		return "zg";
	}
	
}
