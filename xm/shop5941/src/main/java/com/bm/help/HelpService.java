package com.bm.help;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class HelpService extends BaseService{

	@Override
	protected String getTabName() {
		return "apphelp";
	}
	

}
