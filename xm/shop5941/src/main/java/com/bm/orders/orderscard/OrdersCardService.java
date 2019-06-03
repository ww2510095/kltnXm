package com.bm.orders.orderscard;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class OrdersCardService extends BaseService{

	@Override
	protected String getTabName() {
		return "OrdersCard";
	}

}
