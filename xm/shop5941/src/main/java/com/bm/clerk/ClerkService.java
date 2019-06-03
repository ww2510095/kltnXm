package com.bm.clerk;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
 
@Service
public class ClerkService extends BaseService{
	
	@Override
	public String getTabName() {return "Clerk";}
   
}
