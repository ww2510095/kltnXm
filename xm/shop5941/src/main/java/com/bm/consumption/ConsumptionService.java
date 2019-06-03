package com.bm.consumption;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ConsumptionService extends BaseService{

	@Override
	protected String getTabName() {
		return "Consumption";
	}

}
