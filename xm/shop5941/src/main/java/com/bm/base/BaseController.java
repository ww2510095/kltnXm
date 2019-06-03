
package com.bm.base;

import com.Shop5941Application;
import com.bm.auths.AuthException;
import com.bm.auths.MemberAuths;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.redis.RedisMemberType;
import com.bm.base.redis.RedisUtils;
import com.bm.base.request.MD5Util;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.coupon.CouponService;
import com.bm.file.FileService;
import com.bm.shop.Shop;
import com.bm.systemMessage.SystemMessageService;
import com.bm.task.TaskService;
import com.bm.user.Member;
import com.bm.user.MemberService;
import com.bm.wx.xuankuan.u.X_u;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class BaseController {
	
	
	
	@Autowired
	protected  MemberService mMemberService;
	@Autowired
	protected StringRedisTemplate stringRedisTemplate;
	@Autowired
	protected FileService mFileService;
	@Autowired
	protected SystemMessageService mSystemMessageService;
	@Autowired
	protected TaskService mTaskService;
	@Autowired
	protected  CouponService mCouponService;
	
	
	public X_u getX_u(String UID) {
		return RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_user, UID,X_u.class);

	}
	
	protected void AutoShopOne(String uname) throws Exception{
		if(getMember(uname).getSuperadmin()==1)
			return ;
		if(getMember(uname).getmShop()==null)
			throw new RunException("权限不足，你不是供应商");
		if(getMember(uname).getmShop().getSuperid()==0)
			return ;
		throw new RunException("权限不足，你不是供应商");
	}
	protected Long AutoShop(String uname) throws Exception{
		if(getMember(uname).getSuperadmin()==1)
			return null;
		if(getMember(uname).getmShop()!=null)
			return getMember(uname).getmShop().getId();
		Sql msql = new Sql();
		msql.setSql("select shopid from clerk where memberid="+getLogin(uname).getUserid());
		 List<Map<String, Object>> listmap =mMemberService.exeSelectSql(msql);
		
		if(listmap.size()==0)
			throw new RunException("权限不足，你不是店铺管理员");
		
			return Long.valueOf(listmap.get(0).get("SHOPID").toString());
	}
	
	public Shop getShop(String uname) throws Exception {
		return getMember(uname).getShopto(mMemberService, stringRedisTemplate);

	}
	/**
	 * 订单有效状态（可分成）
	 * */
	public String getOrdersStatusTrue() {
		return "2,3,4,6,7,8,20) and orders.id not in (select ordersid from Sharingdetails where shop<0)"
				+ " and (orders.id>1538323200000";
		//return "2,3,4,6,7,8,20) and (orders.id>1538323200000 ";
		
	}
	public String getOrdersStatusTrue_a() {
		return "2,3,4,6,7,8,20";
		//return "2,3,4,6,7,8,20) and (orders.id>1538323200000 ";
		
	}
	/**
	 * 对用户屏蔽的订单轨迹
	 * */
	public boolean getOrdersTrajectoryUserNot(String s) {
		return s.equals("订单已退仓")||s.equals("仓库已验收");
		
	}
	/**
	 * 成功，只返回提示信息
	 * */
	protected RequestType sendTrueMsg(String msg){
		return getRequestType(null, msg, null);
	}
	/**
	 * 成功，只返回数据
	 * */
	protected RequestType sendTrueData(Object obj){
		return getRequestType(null, null, obj);
	}
	/**
	 * 失败，返回提示信息
	 * */
	protected RequestType sendFalse(String msg){
		return getRequestType(-1, msg, null);
	}
	/**
	 * 失败，返回提示信息，带着stat
	 * */
	protected RequestType sendFalse(String msg,int status){
		return getRequestType(status, msg, null);
	}

	private RequestType getRequestType(Integer code,String msg,Object data){
		RequestType reqt = new RequestType();
		//状态码
		if(code!=null)reqt.setStatus(code);
		else reqt.setStatus(200);
		//提示信息
		if(msg!=null)reqt.setMessage(msg);
		//数据
		reqt.setData(data);
		//时间
		reqt.setTimestamp(System.currentTimeMillis());

		//运行时间，测试使用
//		reqt.setRuntime(System.currentTimeMillis()-InterceptorConfig.time);
		Shop5941Application.out(reqt);
		return reqt;		
	}
	  /**
     * 获取当前用户的缓存信息，如果是空的则未登录
     * */
    protected MemberAuths getMember(String uname) throws Exception{
    	return getMember(getLogin(uname));
    }

	/**
	 * 获取当前用户的缓存信息，如果是空的则未登录
	 * */
    protected MemberAuths getMember(Long id) throws Exception{
    	MemberAuths mMemberAuths= RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_user, id,MemberAuths.class);
    	if(mMemberAuths==null) {
    		Map<String, Object> membera = mMemberService.getById(id);
    		if(membera==null)
    			 throw new RunException("用户不存在");
    		mMemberAuths= setRedisMember(IBeanUtil.Map2JavaBean(membera, Member.class), false);
    	}
    	return mMemberAuths;
    }
    /**
     * 获取当前用户的缓存信息，如果是空的则未登录
     * */
    protected MemberAuths getMember(RedisMemberType mRedisMemberType) throws Exception{
    	return getMember(mRedisMemberType.getUserid());
    }
  
	/**
	 * 获取当前用户的缓存信息，如果是空的则未登录
	 * */
    protected RedisMemberType getLogin(String uname) throws Exception{
    	RedisMemberType mRedisMemberType= RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_login, uname,RedisMemberType.class);
    	if(mRedisMemberType==null){
    		Member membera = mMemberService.getByparameter("uname", uname,Member.class);
    		if(membera==null)
    			throw new RunException("用户："+uname+" 不存在");
    		
    		MemberAuths mMemberAuths = setRedisMember(membera, false);
        	mRedisMemberType = new RedisMemberType();
     		mRedisMemberType.setUname(mMemberAuths.getUname());
     		mRedisMemberType.setUID("1qaz2wsx");
     		mRedisMemberType.setType(2);
     		mRedisMemberType.setUserid(mMemberAuths.getId());
     		RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login, mMemberAuths.getUname(), mRedisMemberType);
     		
     		
   		setRedisMember(membera, false);
   		
     		return mRedisMemberType;
    	}
    	 
    	
    	return mRedisMemberType;
    }
    /**
     * 注册用户,用户名不可为空
     * */
    protected void memberRegister(Member mMember,boolean b,String s1) throws Exception{
    	Long id = System.currentTimeMillis();
    	if(Stringutil.isBlank(mMember.getUname()))throw new RunException("用户名不可为空！");
    	if(mMemberService.getByparameter("uname", mMember.getUname())!=null){
    		throw new RunException(mMember.getUname()+"用户已注册");
    	}
    	if(Stringutil.isBlank(mMember.getPassword()))mMember.setPassword("123456");//如果密码为空默认123456
    	if(mMember.getSource()==null)mMember.setSource(2);
    	mMember.setState(1);
    	mMember.setAddtime(System.currentTimeMillis());
    	mMember.setPlatformcurrency(new BigDecimal("0"));
    	mMember.setPassword(MD5Util.MD5(mMember.getPassword()));
    	mMember.setPhone(mMember.getUname());
    	mMember.setSuperadmin(0);
    	mMember.setNickname("用户"+mMember.getUname().substring(0,3)+"****"+mMember.getUname().substring(7,mMember.getUname().length()));
    	mMember.setGoldcoin(new BigDecimal("0"));
    	if(mMember.getUsersystem()==null)mMember.setUsersystem("0");
    	if(mMember.getId()==null)
    	mMember.setId(id);
    	String tx =  MyParameter.TomcatFileImage+"touxiang.png";
    	mMember.setPortrait(tx.substring(2, tx.length()));
    	mMember.setSex(0);
    	mMember.setMembersize(1);
    	mMemberService.add(mMember);
    	
    	if(b){
    		Sql msql = new Sql();
    		msql.setSql("INSERT INTO Friends (id,memberida,memberidb,memberidatype) VALUES('"+System.currentTimeMillis()+"',"+s1+",'"+id+"','0')");
    		mMemberService.execSQL(msql, -1, id+"");
    	}
    	
    }
    
    protected MemberAuths setRedisMember(Member mMember,boolean b) throws Exception {
    	Sql msql = new Sql();
    	msql.setSql("select * from shop where memberid="+mMember.getId());
    	List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
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
			listmap = mMemberService.exeSelectSql(msql);
			ma.setClerk(listmap.size()!=0);
		}
		
		if (!ma.isClerk())
			ma.setClerk(sh != null);
		ma.setmShop(sh);
		//绑定的店铺
		msql.setSql("select * from shop where id = (select shopid from Clerk where memberid=(select memberida"
				+ " from Friends where memberidb="+mMember.getId()+"))");
		 listmap = mMemberService.exeSelectSql(msql);
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
				 listmap = mMemberService.exeSelectSql(msql);
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
		
//		try {
//			//推荐人电话
//			msql.setSql("select phone from member where id = (select memberida"
//					+ " from Friends where memberidb="+mMember.getId()+")");
//			ma.setAphone(mMemberService.exeSelectSql(msql).get(0).get("PHONE").toString());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		ma.setAphone(sh.getShopphone());
		if (MyParameter.auths_true) {
			List<String> s = mMemberService.getAuths(mMember.getId());
			ma.setAuths(s);
			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, mMember.getId(), ma);
		}
		mMember =new Member();
		mMember.setId(ma.getId());
		if(ma.getMembersize()==null)
			mMember.setMembersize(1);
		else
			mMember.setMembersize(ma.getMembersize()+1);
		
		mMemberService.updateBySelect(mMember);
		
//		if(getLogin(mMember.getUname()).getType()==1){
//			if(getMember(mMember.getId()).getSuperadmin()!=1){
//				if(getMember(mMember.getId()).getmShop()==null)
//					throw new RunException("权限不足，你不能登录管理系统");
//			}
//		}
		ma.setClerk(false);
		return ma;
	}
    protected MemberAuths setRedisMember_b(Member mMember,boolean b) throws Exception {
    	Sql msql = new Sql();
    	msql.setSql("select * from shop where memberid="+mMember.getId());
    	List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
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
    		listmap = mMemberService.exeSelectSql(msql);
    		ma.setClerk(listmap.size()!=0);
    	}
    	
    	if (!ma.isClerk())
    		ma.setClerk(sh != null);
    	ma.setmShop(sh);
    	//绑定的店铺
    	msql.setSql("select * from shop where id = (select shopid from Clerk where memberid=(select memberida"
    			+ " from Friends where memberidb="+mMember.getId()+"))");
    	listmap = mMemberService.exeSelectSql(msql);
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
    			listmap = mMemberService.exeSelectSql(msql);
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
    	
//		try {
//			//推荐人电话
//			msql.setSql("select phone from member where id = (select memberida"
//					+ " from Friends where memberidb="+mMember.getId()+")");
//			ma.setAphone(mMemberService.exeSelectSql(msql).get(0).get("PHONE").toString());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		ma.setAphone(sh.getShopphone());
    	if (MyParameter.auths_true) {
    		List<String> s = mMemberService.getAuths(mMember.getId());
    		ma.setAuths(s);
    		RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, mMember.getId(), ma);
    	}
    	mMember =new Member();
    	mMember.setId(ma.getId());
    	if(ma.getMembersize()==null)
    		mMember.setMembersize(1);
    	else
    		mMember.setMembersize(ma.getMembersize()+1);
    	
    	mMemberService.updateBySelect(mMember);
    	
//		if(getLogin(mMember.getUname()).getType()==1){
//			if(getMember(mMember.getId()).getSuperadmin()!=1){
//				if(getMember(mMember.getId()).getmShop()==null)
//					throw new RunException("权限不足，你不能登录管理系统");
//			}
//		}
    	
    	if(!ma.isClerk())throw new RunException("权限不足");
    	
    	return ma;
    }

    /**
     * 注册用户,用户名不可为空
     * */
    protected void memberRegister(Member mMember,boolean b) throws Exception{
    	memberRegister(mMember, b, "1");
    }
    /**
     * 文件查重，如果有重复返回文件路径，没有返回null
     * @throws Exception 
     * */
    protected String filerepeat(String md5) throws Exception{
    	Map<String, Object> obj =mFileService.getByparameter("md5", md5);
    	if(obj!=null&&Stringutil.isBlank(obj.get("PATH").toString()))return obj.get("PATH").toString();
    	return null;
    	
    	
    }
    
    public void sendImage( HttpServletResponse response,BufferedImage image) throws Exception {
        ServletOutputStream out = response.getOutputStream();  
        ImageIO.write(image, "jpg", out);
	      out.flush();  
	      out.close();
        
    }
  
}
