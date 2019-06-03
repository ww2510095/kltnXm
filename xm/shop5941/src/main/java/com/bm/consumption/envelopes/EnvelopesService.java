package com.bm.consumption.envelopes;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class EnvelopesService extends BaseService{

	@Override
	protected String getTabName() {
		return "Envelopes";
	}

}
