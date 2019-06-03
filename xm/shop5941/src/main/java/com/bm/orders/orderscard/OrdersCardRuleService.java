package com.bm.orders.orderscard;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class OrdersCardRuleService extends BaseService{

	@Override
	protected String getTabName() {
		return " OrdersCardRule";
	}

}
