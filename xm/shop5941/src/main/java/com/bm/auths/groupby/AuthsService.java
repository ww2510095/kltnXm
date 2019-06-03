package com.bm.auths.groupby;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class AuthsService extends BaseService{

	@Override
	protected String getTabName() {
		return "Auths";
	}

}
