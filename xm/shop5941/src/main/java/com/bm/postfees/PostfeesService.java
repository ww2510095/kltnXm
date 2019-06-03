package com.bm.postfees;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class PostfeesService extends BaseService{

	@Override
	protected String getTabName() {
		return "Postfees";
	}

}
