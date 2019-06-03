package com.bm.shoppingcard;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ShoppingCardService extends BaseService{

	@Override
	protected String getTabName() {
		return "ShoppingCard";
	}

}
