package com.bm.task;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class TaskService extends BaseService{

	@Override
	protected String getTabName() {
		return "task";
	}

}
