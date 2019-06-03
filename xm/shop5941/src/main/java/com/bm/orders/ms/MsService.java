package com.bm.orders.ms;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class MsService extends BaseService{

	@Override
	protected String getTabName() {
		return "ms";
	}

}
