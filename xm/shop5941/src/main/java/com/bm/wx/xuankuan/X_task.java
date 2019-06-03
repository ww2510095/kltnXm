package com.bm.wx.xuankuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.GsonUtil;
import com.bm.wx.xuankuan.u.X_u;

@Component
public class X_task extends BaseController{
	  
	   /**
	    * 清除过期的key值
	    * */
	   @Scheduled(cron ="0/5 * *  * * ? ")//5秒执行一次，用于测试
	   public  void deletekey() throws Exception {
			Set<String> sa =stringRedisTemplate.keys("user_*");
			List<String> la = new ArrayList<String>();
			for (String string : sa) {
				if(string.length()>32){
					X_u mxu = GsonUtil.fromJsonString(RedisUtils.get(stringRedisTemplate, string), X_u.class);
					if(System.currentTimeMillis()-mxu.getX_z_time()>MyParameter.w_out_time)
						la.add(string);
				}
					
			}
			stringRedisTemplate.delete(la);
		   
	   }
	   
	   @Scheduled(cron ="0 01 01 ? * *")//每天凌晨2.15执行
	   public void updatespfb() throws Exception{
		Sql msql = new Sql();
		msql.setSql("update xsp set fa_b=0 where jie_ssj<"+System.currentTimeMillis());
		mMemberService.execSQL(msql);
	   }
}
