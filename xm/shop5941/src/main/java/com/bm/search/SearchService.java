package com.bm.search;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class SearchService extends BaseService{

	@Override
	protected String getTabName() {
		return "search";
	}

}
