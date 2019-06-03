package com.bm.commodity.back;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class CommodityBackService extends BaseService{

	@Override
	protected String getTabName() {
		return "Commodityback";
	}

}
