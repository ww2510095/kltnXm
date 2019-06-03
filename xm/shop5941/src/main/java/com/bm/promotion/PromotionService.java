package com.bm.promotion;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class PromotionService extends BaseService{

	@Override
	protected String getTabName() {
		return "Promotion";
	}
	


}
