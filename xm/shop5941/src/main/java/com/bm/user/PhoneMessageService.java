package com.bm.user;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
 
@Service
public class PhoneMessageService extends BaseService{
	
	@Override
	public String getTabName() {return "phonemessage";}
   
}
