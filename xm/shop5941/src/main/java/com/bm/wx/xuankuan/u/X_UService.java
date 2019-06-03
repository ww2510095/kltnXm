package com.bm.wx.xuankuan.u;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
 
@Service
public class X_UService extends BaseService{
	
	@Override
	public String getTabName() {return "XU";}
	
	public String updateBySelect(X_u mxu) throws Exception {
		mxu.setX_z_time(null);
		return super.updateBySelect(mxu);
	}

	public int add(X_u mxu) throws Exception {
		if(Stringutil.isBlank(mxu.getX_b()))
			throw new RunException("经理账号不可为空");
//		if(getByparameter("x_name", mxu.getX_b())==null)
//			throw new RunException("经理账号"+ mxu.getX_b()+"不存在");
		return super.add(mxu);
	}
   
}
