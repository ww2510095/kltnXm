package com.bm.orders.receiver;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ReceiverService extends BaseService{

	@Override
	protected String getTabName() {
		return "Receiver";
	}

}
