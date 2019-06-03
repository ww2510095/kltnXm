package com.bm.putforward;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class PutforwardService extends BaseService{

	@Override
	protected String getTabName() {
		return "Putforward";
	}

}
