package com.bm.orders.orders;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ReturnOrdersService extends BaseService{

	@Override
	protected String getTabName() {
		return "ReturnOrders";
	}
	
	public int add(Orders or) throws Exception {
		or.setOrdernumber(or.getId().toString());
		or.setUpdatetime(System.currentTimeMillis());
//		or.setId(System.currentTimeMillis());
		or.setEndtime(System.currentTimeMillis());
		or.setAutosystem(0);
		or.setTrajectory("");
		return super.add(or);
	}

	
	public void updateBySelect(Orders or) throws Exception {
		or.setUpdatetime(System.currentTimeMillis());
		super.updateBySelect(or);
	}
}
