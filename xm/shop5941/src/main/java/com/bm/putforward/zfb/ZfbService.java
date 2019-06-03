package com.bm.putforward.zfb;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class ZfbService extends BaseService{

	@Override
	protected String getTabName() {
		return "zfb";
	}

}
