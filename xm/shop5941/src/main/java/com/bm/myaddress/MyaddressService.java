package com.bm.myaddress;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class MyaddressService extends BaseService{

	@Override
	protected String getTabName() {
		return "Myaddress";
	}
	

}
