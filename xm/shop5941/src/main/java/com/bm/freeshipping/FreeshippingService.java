package com.bm.freeshipping;


import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class FreeshippingService extends BaseService{

	@Override
	protected String getTabName() {
		return "Freeshipping";
	}


}
