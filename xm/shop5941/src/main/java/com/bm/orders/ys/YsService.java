package com.bm.orders.ys;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class YsService extends BaseService{

	@Override
	protected String getTabName() {
		return "ys";
	}

	
}
