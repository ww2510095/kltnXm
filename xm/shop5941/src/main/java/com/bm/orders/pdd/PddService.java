package com.bm.orders.pdd;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class PddService extends BaseService{

	@Override
	protected String getTabName() {
		return "Pdd";
	}

}
