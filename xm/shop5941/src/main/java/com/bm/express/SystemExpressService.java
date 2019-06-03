package com.bm.express;


import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class SystemExpressService extends BaseService{

	@Override
	protected String getTabName() {
		return "SystemExpress";
	}

}
