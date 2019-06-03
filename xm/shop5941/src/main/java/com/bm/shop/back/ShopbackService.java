package com.bm.shop.back;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ShopbackService extends BaseService{

	
	@Override
	protected String getTabName() {
		return "shopback";
	}
	
	public int add(Shopback mShopback) throws Exception {
		mShopback.setCode(System.currentTimeMillis()+"");
		return super.add(mShopback);
	}
}
