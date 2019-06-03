package com.bm.friends;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class FriendsService extends BaseService {

	@Override
	protected String getTabName() {
		return "Friends";
	}

}
