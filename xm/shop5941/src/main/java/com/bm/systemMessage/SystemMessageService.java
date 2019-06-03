package com.bm.systemMessage;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class SystemMessageService extends BaseService{

	@Override
	protected String getTabName() {
		return "SystemMessage";
	}

}
