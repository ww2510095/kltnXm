package com.bm.base.interceptor;

import java.util.HashMap;

import com.bm.auths.MemberAuths;
import com.bm.base.BaseDao;
import com.bm.base.Sql;
import com.bm.base.util.IBeanUtil;
import com.bm.user.Member;
import com.myjar.desutil.RunException;

public class UMap extends HashMap<Long,MemberAuths>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseDao mBaseDao;
	public UMap(BaseDao mBaseDao) {
		this.mBaseDao=mBaseDao;
	}
	
	public MemberAuths get(Long id) {
		MemberAuths v =super.get(id);
		if(v==null){
			try {
				Sql msql = new Sql();
				msql.setSql("select * from member where id="+id);
				Member m = IBeanUtil.Map2JavaBean(mBaseDao.exeSelectSql(msql).get(0), Member.class);
				InterceptorConfig.lmap.get(m.getUname());
				v = super.get(id);
			} catch (Exception e) {
			}
			
		}
		if(v==null)throw new RunException("登录超时");
		return v;
	}

}
