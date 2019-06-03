package com.bm.feedback;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class FeedbackService extends BaseService{

	@Override
	protected String getTabName() {
		return "Feedback";
	}

}
