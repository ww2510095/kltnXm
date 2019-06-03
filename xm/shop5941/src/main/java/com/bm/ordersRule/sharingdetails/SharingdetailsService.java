package com.bm.ordersRule.sharingdetails;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.myjar.desutil.RunException;
 
@Service
public class SharingdetailsService extends BaseService{
	
	@Override
	public String getTabName() {return "Sharingdetails";}
	
	public int add(Sharingdetails mSharingdetails) throws Exception {
		mSharingdetails.setIstrue(0);
		Long ida = System.currentTimeMillis();
		int a = 0;
		while (true) {
			try {
				mSharingdetails.setId(ida);
				return super.add(mSharingdetails);
			} catch (Exception e) {
				ida=ida+1;
				a=a+1;
				if(a==100)throw new RunException("结账异常");
			}
			
			
		}
	}
   
}
