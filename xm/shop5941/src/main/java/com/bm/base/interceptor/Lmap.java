package com.bm.base.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bm.auths.AuthException;
import com.bm.auths.MemberAuths;
import com.bm.base.BaseDao;
import com.bm.base.MyParameter;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.Sql;
import com.bm.base.redis.RedisMemberType;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.shop.Shop;
import com.bm.user.Member;
import com.myjar.desutil.RunException;

public class Lmap extends HashMap<String,RedisMemberType>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseDao mBaseDao;
	private StringRedisTemplate stringRedisTemplate;
	public Lmap(BaseDao mBaseDao,StringRedisTemplate stringRedisTemplate) {
		this.mBaseDao=mBaseDao;
		this.stringRedisTemplate=stringRedisTemplate;
	}
	
	public RedisMemberType get(String uname) {
		RedisMemberType v =super.get(uname);
		
		if(v==null){
			try {
				Member m = null;
				Sql msql = new Sql();
				msql.setSql("select * from member where uname='"+uname+"'");
				m=IBeanUtil.Map2JavaBean(mBaseDao.exeSelectSql(msql).get(0), Member.class);
				
				

				v = new RedisMemberType();
				v.setUname(uname);
				v.setUID("1");
				v.setType(2);
				v.setUserid(m.getId());
				RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login,uname, v);				
				InterceptorConfig.umap.put(m.getId(),setRedisMember(m));
				put(uname, v);
			
			} catch (Exception e) {
			}
			
		}
		if(v==null)throw new RunException("登录超时");
		return v;
	}

	   protected MemberAuths setRedisMember(Member mMember) throws Exception {
		   boolean b=false;
	    	Sql msql = new Sql();
	    	msql.setSql("select * from shop where memberid="+mMember.getId());
	    	List<Map<String, Object>> listmap = mBaseDao.exeSelectSql(msql);
	    	Shop sh=null;
	    	if(listmap.size()!=0){
	    		//自己的店铺
	    		 sh = IBeanUtil.Map2JavaBean(listmap.get(0), Shop.class);
	    		if(b)
	    			if(mMember.getSuperadmin()!=1)
	    				if(sh==null)
	    					throw new AuthException("权限不足，您不能登录管理系统");
	    	}
			
			
			// 权限
			MemberAuths ma = GsonUtil.fromJsonString(GsonUtil.toJsonString(mMember), MemberAuths.class);
			if (MyParameter.clerk_true) {
				msql.setSql("select * from clerk where memberid="+mMember.getId());
				listmap = mBaseDao.exeSelectSql(msql);
				ma.setClerk(listmap.size()!=0);
			}
			
			if (!ma.isClerk())
				ma.setClerk(sh != null);
			ma.setmShop(sh);
			//绑定的店铺
			msql.setSql("select * from shop where id = (select shopid from Clerk where memberid=(select memberida"
					+ " from Friends where memberidb="+mMember.getId()+"))");
			 listmap = mBaseDao.exeSelectSql(msql);
			if(listmap.size()!=0)
				sh = IBeanUtil.Map2JavaBean(listmap.get(0), Shop.class);
			ma.setShopto(sh);
			if(ma.getmShop()!=null)
				ma.setM_type(4);
			else{
				if(!ma.isClerk())
					ma.setM_type(0);
				else{
					msql.setSql("select type from Identity where memberid="+ma.getId());
					 listmap = mBaseDao.exeSelectSql(msql);
					 if(listmap.size()==0||listmap.get(0).get("TYPE")==null)
						 ma.setM_type(1);
					 else{
						 if(listmap.get(0).get("TYPE").toString().equals("2"))
							 ma.setM_type(2);
						 else
							 ma.setM_type(3);
					 }
						 
				}
			}
			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, mMember.getId(), ma);
			
			mMember =new Member();
			mMember.setId(ma.getId());
			if(ma.getMembersize()==null)
				mMember.setMembersize(1);
			else
				mMember.setMembersize(ma.getMembersize()+1);
			
			
			return ma;
		}

}
