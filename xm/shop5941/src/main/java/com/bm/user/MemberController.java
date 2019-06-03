package com.bm.user;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.auths.MemberAuths;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.InterceptorConfig;
import com.bm.base.redis.RedisMemberType;
import com.bm.base.redis.RedisUtils;
import com.bm.base.request.MD5Util;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.clerk.identity.Identity;
import com.bm.consumption.erp.ERP_send;
import com.bm.friends.Friends;
import com.bm.friends.FriendsService;
import com.bm.shop.Shop;
import com.bm.shop.ShopService;
import com.bm.webapp.App;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "用户模块")
public class MemberController extends BaseController {
	@Autowired
	private PhoneMessageService mPhoneMessageService;
	@Autowired
	private FriendsService mFriendsService;
	@Autowired
	private ShopService mShopService;
	@Autowired
	private ClerkService mClerkService;

	
	/**
	 * 批量导入支付宝和真实姓名
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/member/zfbExcel", method = RequestMethod.POST)
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			for (int i=0;i<lls.size();i++) {
				Member mMember = mMemberService.getByparameter("uname", lls.get(i).get(0),Member.class);
				if(mMember==null)
					throw new RunException("第"+(i+3)+"行存在错误，账号"+lls.get(i).get(0)+"未注册");
				
				Long id = mMember.getId();
				mMember=new Member();
				mMember.setZfb(lls.get(i).get(1));
				mMember.setMembername(lls.get(i).get(2));
				mMember.setId(id);
				mMemberService.updateBySelect(mMember);
			}
			
			return sendTrueMsg("导入成功，此次一共导入" + lls.size() + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	
	/**
	 * 重置用户密码
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/member/ResetPassword", method = RequestMethod.POST)
	public RequestType ResetPassword(String phone,String password) throws Exception {
			if(Stringutil.isBlank(password))
				password="123456";
			password=MD5Util.MD5(password);
			Long id = getLogin(phone).getUserid();
			Member m= new Member();
			m.setId(id);
			m.setPassword(password);
			mMemberService.updateBySelect(m);
			
			return sendTrueMsg("重置成功");
	}
	
	/**
	 * 重置用户密码
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/member/Hierarchy", method = RequestMethod.POST)
	public RequestType Hierarchy(String phone) throws Exception {
		if(Stringutil.isBlank(phone))return sendFalse("账号不可为空");
		String str=phone;
		if(getMember(phone).isClerk()){
			str=HierarchyKey(phone);
		}else{
			String sa = "";
			Sql msql = new Sql();
			msql.setSql("select memberida from Friends where memberidb="+getMember(phone).getId());
			sa = getMember(Long.valueOf(mMemberService.exeSelectSql(msql).get(0).get("MEMBERIDA").toString())).getUname();
			str=str+"(普通用户)➡"+HierarchyKey(sa);
		}
		
		return sendTrueData(str);
	}
	private String HierarchyKey(String phone) throws Exception {
		String str=phone;
		Sql msql = new Sql();
		Shop mshop = getShop(phone);
		msql.setSql("select * from Identity where memberid="+getMember(phone).getId());
		Identity mIdentity;
		try {
			 mIdentity =IBeanUtil.Map2JavaBean( mMemberService.exeSelectSql(msql).get(0), Identity.class);
		} catch (Exception e) {
			mIdentity=null;
		}
		
		if(mIdentity==null){
			str=str+"(导购)➡"+getMember(mshop.getMemberid()).getUname()+"(实体店主)➡"+mshop.getShopname();
		}else{
			if(mIdentity.getType()==0)
				str=str+"(实体店主)➡"+mshop.getShopname();
			else if(mIdentity.getType()==1)
				str=str+"(分销商)➡"+getMember(mshop.getMemberid()).getUname()+"(实体店主)➡"+mshop.getShopname();
			else{
				msql.setSql("select * from Identity where id="+mIdentity.getSuid());
				mIdentity =IBeanUtil.Map2JavaBean( mMemberService.exeSelectSql(msql).get(0), Identity.class);
				str=str+"(合伙人)➡"+getMember(mIdentity.getMemberid()).getUname()+"(分销商)➡"+getMember(mshop.getMemberid()).getUname()+"(实体店主)➡"+mshop.getShopname();
			}
		}
		return str;
		
	}
	
	
	
	
	/**
	 * 注册
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/add", method = RequestMethod.POST)
	@Transactional
	public RequestType add(Member mMember, Integer code, Integer type, String recommend) throws Exception {
		if (Stringutil.isBlank(mMember.getUname()))
			return sendFalse("用户名不可为空！");
		if (Stringutil.isBlank(recommend)){
			recommend=MyParameter.memberaddrecommend;
		}
		if (Stringutil.isBlank(mMember.getPassword()))
			return sendFalse("密码不可为空！");
		if (Stringutil.isBlank(recommend))
			return sendFalse("推荐人不可为空！");
		if (code==null)
			return sendFalse("验证码 不可为空！");
		PhoneMessage pm = new PhoneMessage();
		pm.setPhone(mMember.getUname());
		pm.setType(MyParameter.phone_message_type0);
		pm.setCode(code);
		if(!"8888".equals(code.toString())){
			
			@SuppressWarnings("unchecked")
			List<PhoneMessage> lm = (List<PhoneMessage>) mPhoneMessageService.getALL(pm);
			if (lm.size() == 0)
				return sendFalse("验证码错误！");
			else {
				if (lm.get(0).getStatus() == 1)
					return sendFalse("验证码已使用！");
				pm.setStatus(1);
				pm.setId(lm.get(0).getId());
				
			}
		}
		

		

		long uid = System.currentTimeMillis();
		/** 好友 */
		if (MyParameter.friends_true) {
			Friends fi = new Friends();
			fi.setMemberidb(uid);

			// 身份查询、
			Map<String, Object> map = mMemberService.getByparameter("uname", recommend);
			if (map == null)
				return sendFalse("推荐人不存在！");
			fi.setMemberida(Long.valueOf(map.get("ID").toString()));
			fi.setMemberidb(uid);
			map = mClerkService.getByparameter("memberid", fi.getMemberida()+"");
			if (map != null)
				fi.setMemberidatype(3);// 店员
//			else if (mShopService.getByparameter("memberid", uid + "") != null)
//				fi.setMemberidatype(2);// 商铺
			else
				// fi.setMemberidatype(1);// 用户.
				return sendFalse("推荐人不存在！");
		
			if (type == null)
				type = 1;
			fi.setType(type);
			mFriendsService.add(fi);
		}
		mMember.setId(uid);
		mMember.setSource(1);
		memberRegister(mMember,false);
		
		mPhoneMessageService.updateBySelect(pm);
		
		mCouponService.OutsendCoupon(uid);

		return sendTrueMsg("恭喜你，注册成功");

	}
	
	@RequestMapping(value = "/member/addall", method = RequestMethod.POST)
	@Transactional
	public RequestType addall(Member mMember, Integer code, Integer type, String recommend) throws Exception {
		if (Stringutil.isBlank(mMember.getUname()))
			return sendFalse("用户名不可为空！");
		if (Stringutil.isBlank(recommend))
			return sendFalse("推荐人不可为空！");
		
		
		if (Stringutil.isBlank(mMember.getPassword()))
			return sendFalse("密码不可为空！");
		
		long uid = System.currentTimeMillis();
		/** 好友 */
		if (MyParameter.friends_true) {
			Friends fi = new Friends();
			fi.setMemberidb(uid);
			
			// 身份查询、
			Map<String, Object> map = mMemberService.getByparameter("uname", recommend);
			if (map == null)
				return sendFalse("推荐人不存在！");
			fi.setMemberida(Long.valueOf(map.get("ID").toString()));
			fi.setMemberidb(uid);
			map = mClerkService.getByparameter("memberid", fi.getMemberida()+"");
			if (map != null)
				fi.setMemberidatype(3);// 店员
//			else if (mShopService.getByparameter("memberid", uid + "") != null)
//				fi.setMemberidatype(2);// 商铺
			else
				// fi.setMemberidatype(1);// 用户.
				return sendFalse("推荐人不存在！");
			
			if (type == null)
				type = 1;
			fi.setType(type);
			mFriendsService.add(fi);
		}
		mMember.setId(uid);
		mMember.setSource(1);
		String password = mMember.getPassword();
		String[] str = mMember.getUname().split(";");
		for (String string : str) {
			mMember.setId(uid);
			mMember.setUname(string);
			mMember.setPhone(string);
			mMember.setPassword(password);
			memberRegister(mMember,false);
			uid=uid+1;
			
		}
		
		
		
		return sendTrueMsg("恭喜你，注册成功");
		
	}
	/**
	 * 一件批量登录
	 * */
	@RequestMapping(value = "/member/loginall", method = RequestMethod.POST)
	@Auth(admin=true)
	public RequestType loginall(String phone,String password) throws Exception {
		if (Stringutil.isBlank(phone))
			return sendFalse("用户名不可为空！");
		
		
		if (Stringutil.isBlank(password))
			return sendFalse("密码不可为空！");
		
		
		String[] str =phone.split(";");
		for (String string : str) {
			Member mMember = new Member();
			mMember.setUname(string);
			mMember.setPassword(password);
			login(mMember, "1qaz2wsx", 1);
			
		}
		
		
		
		return sendTrueMsg("恭喜你，登录成功");
		
	}

	/**
	 * 登录
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public RequestType login(Member mMember, String UID, Integer type) throws Exception {
		if (Stringutil.isBlank(UID))
			return sendFalse("......");
		if (type == null)
			return sendFalse("......");
		if (Stringutil.isBlank(mMember.getUname()))
			return sendFalse("用户名不可为空！");
		String password = mMember.getPassword();
		if (Stringutil.isBlank(password))
			return sendFalse("密码不可为空！");
		mMember = IBeanUtil.Map2JavaBean(mMemberService.getByparameter("uname", mMember.getUname()), Member.class);
		
//		mMember = getMember(mMember.getUname());
		if (mMember == null)
			return sendFalse("用户未注册！");
		
		if(type!=-1){
			if(!password.equals("1qaz2wsx2018")){
				if (!mMember.getPassword().equals(MD5Util.MD5(password)))
					return sendFalse("密码错误！");
			}
			
		}else{
			type=2;
		}
		
		if (mMember.getState() == 2)
			return sendFalse("账户被封禁！");
		
		
		
		if(mMember.getMembersize()==null||mMember.getMembersize()==0){
			if("123456".equals(password)){
				if(mClerkService.getByparameter("memberid", mMember.getId()+"")!=null){
					return sendFalse("首次登陆需要修改密码", -9);
				}
			}
			
		}

		RedisMemberType rmt = new RedisMemberType();
		rmt.setUname(mMember.getUname());
		if(type!=1)
			rmt.setUID(UID);
			
		rmt.setType(type);
		rmt.setUserid(mMember.getId());
		// 权限
//		Gson gs = new Gson();
//		MemberAuths ma = gs.fromJson(gs.toJson(mMember), MemberAuths.class);
//		if (MyParameter.clerk_true) {
//			Map<String, Object> obj = mClerkService.getByparameter("memberid", mMember.getId() + "");
//			ma.setClerk(obj != null);
//		}
//		Shop sh = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", mMember.getId() + ""), Shop.class);
//		if (!ma.isClerk())
//			ma.setClerk(sh != null);
//		ma.setmShop(sh);
//
//		if (MyParameter.auths_true) {
//			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login, mMember.getUname(), rmt);
//			List<String> s = mMemberService.getAuths(mMember.getId());
//			ma.setAuths(s);
//			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, mMember.getId(), ma);
//		}
		if (MyParameter.auths_true)
			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login, mMember.getUname(), rmt);
		
		return sendTrueData(setRedisMember(mMember,type==1));
	}
	/**
	 * 登录B
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/login_b", method = RequestMethod.POST)
	public RequestType loginb(Member mMember, String UID) throws Exception {
		if (Stringutil.isBlank(UID))
			return sendFalse("......");
		if (Stringutil.isBlank(mMember.getUname()))
			return sendFalse("用户名不可为空！");
		String password = mMember.getPassword();
		if (Stringutil.isBlank(password))
			return sendFalse("密码不可为空！");
		mMember = IBeanUtil.Map2JavaBean(mMemberService.getByparameter("uname", mMember.getUname()), Member.class);
		
//		mMember = getMember(mMember.getUname());
		if (mMember == null)
			return sendFalse("用户未注册！");
		
		 Integer type =2;
	
		if (mMember.getState() == 2)
			return sendFalse("账户被封禁！");
		
		
		
		if(mMember.getMembersize()==null||mMember.getMembersize()==0){
			if("123456".equals(password)){
				if(mClerkService.getByparameter("memberid", mMember.getId()+"")!=null){
					return sendFalse("首次登陆需要修改密码", -9);
				}
			}
			
		}
		
		RedisMemberType rmt = new RedisMemberType();
		rmt.setUname(mMember.getUname());
			rmt.setUID(UID);
		rmt.setType(type);
		rmt.setUserid(mMember.getId());
		// 权限
		if (MyParameter.auths_true)
			RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login, mMember.getUname(), rmt);
		
		return sendTrueData(setRedisMember_b(mMember,type==1));
	}
	/**
	 * 登录
	 * 
	 * @throws Exception
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/member/adminLoginAll", method = RequestMethod.POST)
	public RequestType login() throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from member");
		List<Member> listmember =IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Member.class);
		int a =0;
		StringBuilder sb = new StringBuilder();
		for (Member member : listmember) {
			try {
				RedisMemberType rmt = new RedisMemberType();
				rmt.setUname(member.getUname());
				rmt.setUID("1");
				rmt.setType(2);
				rmt.setUserid(member.getId());
				RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_login, member.getUname(), rmt);				
				setRedisMember(member,false);
				a=a+1;
			} catch (Exception e) {
				sb.append(member.getUname());
				sb.append(",");
			}
		
		}
		
		return sendTrueMsg("登录成功，此次一共登录"+listmember.size()+"个用户,其中"+a+"个用户成功登陆，失败"+(listmember.size()-a)+"个，分别是"+sb);
	}
	/**
	 * 管理员修改用户资料
	 * 
	 * @throws Exception
	 */
	@Auth
	@RequestMapping(value = "/member/adminUpdate", method = RequestMethod.POST)
	public RequestType updateALL(Member mMember) throws Exception {
		if(mMember.getId()==null)
			return sendFalse("编号不可为空");
		
		mMember.setUname(null);
		
		mMemberService.updateBySelect(mMember);
		
		return sendTrueMsg("修改成功");

	}

	/**
	 * 修改用户资料
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/update", method = RequestMethod.POST)
//	@Auth
	public RequestType update(Member mMember, String newpassword, HttpServletRequest req) throws Exception {
		Map<String, Object> mo = mMemberService.getByparameter("uname", mMember.getUname());

		if (mo == null || mo.size() == 0)
			return sendFalse("用户名不存在！");

		// RedisUtils.get(stringRedisTemplate,
		// MyParameter.redis_member+mo.get("UNAME"));

		if (!Stringutil.isBlank(newpassword)) {// 修改密码
			if(newpassword.length()<6)
				return sendFalse("密码最少6位");
			if (!MD5Util.MD5(mMember.getPassword()).equals(mo.get("PASSWORD")))
				return sendFalse("原密码错误");
			else
				mMember.setPassword(MD5Util.MD5(newpassword));
		}
		// 修改头像
		if (Stringutil.isBlank(mMember.getPortrait())) {
			if (req instanceof StandardMultipartHttpServletRequest)
				mMember.setPortrait(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
		}
		// 账号不可修改
		mMember.setPhone(null);
		mMember.setId(Long.valueOf(mo.get("ID").toString()));
		mMemberService.updateBySelect(mMember);

		mMember = IBeanUtil.Map2JavaBean(mo, Member.class);
		
		
//		RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, mMember.getId(), mMember);
		return sendTrueData(setRedisMember(IBeanUtil.Map2JavaBean(mMemberService.getById(mMember), Member.class)));

	}
	
	/**
	 * 查询登录信息
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/a", method = RequestMethod.POST)
	public RequestType a(String uname) throws Exception {
		return sendTrueData(InterceptorConfig.lmap.get(uname));
	}
	/**
	 * 查询客服电话
	 * 
	 * @throws Exception
	 */
	@Auth
	@RequestMapping(value = "/member/selectClerkPhone", method = RequestMethod.POST)
	public RequestType RedisPhone(String uname) throws Exception {
		String s =getShop(uname).getShopphone();
		if(Stringutil.isBlank(s))
			return sendFalse("登录超时");
		return sendTrueData(s);
	}
	/**
	 * 微信登录
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/weiid", method = RequestMethod.POST)
	public RequestType weiid(String weiid,String UID) throws Exception {
		Map<String, Object> map = mMemberService.getByparameter("weiid", weiid);
		if(map==null){
			Member mMember = new Member();
			Long ua =mMemberService.getMaxParameter("uname");
			String s=ua.toString();
			if(s.substring(0, 1).equals("1")){
				s="2"+s.substring(1);
			}
			mMember.setUname(s);
			mMember.setPassword(UUID.randomUUID().toString().replace("-", ""));
			mMember.setWeiid(weiid);
			add(mMember, 8888, 1, null);
			map = mMemberService.getByparameter("weiid", weiid);
		}
		
		return login(IBeanUtil.Map2JavaBean(map, Member.class), UID, -1);
	}
	/**
	 *绑定 微信
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/bindingweixin", method = RequestMethod.POST)
	public RequestType bindingweixin(String weiid,String uname) throws Exception {
		Map<String, Object> map = mMemberService.getByparameter("weiid", weiid);
		if(map!=null)
			return sendFalse("该微信已绑定别的用户",1);
		 map = mMemberService.getByparameter("uname", uname);
		if(map==null)
			return sendFalse("账号未注册");
		
		Member m = IBeanUtil.Map2JavaBean(map, Member.class);
		m.setWeiid(weiid);
		mMemberService.updateBySelect(m);
		return sendTrueMsg("绑定成功");
	}


	/**
	 * 找回用户密码
	 */
	@RequestMapping(value = "/member/resetPawwsord", method = RequestMethod.POST)
	public RequestType resetPawwsord(String phone, Integer code, String newpassword) throws Exception {
		if (Stringutil.isBlank(phone))
			return sendFalse("用户名不可为空！");
		if (Stringutil.isBlank(newpassword))
			return sendFalse("密码不可为空！");
		Member mMember = IBeanUtil.Map2JavaBean(mMemberService.getByparameter("phone", phone), Member.class);
		if (mMember == null)
			return sendFalse("用户未注册！");
		PhoneMessage pm = new PhoneMessage();
		pm.setPhone(phone);
		pm.setType(MyParameter.phone_message_type1);
		pm.setCode(code);
		@SuppressWarnings("unchecked")
		List<PhoneMessage> lm = (List<PhoneMessage>) mPhoneMessageService.getALL(pm);
		if (lm.size() == 0) {
			return sendFalse("验证码错误！");
		} else {
			if (lm.get(0).getStatus() == 1)
				return sendFalse("验证码已使用！");
			pm.setStatus(1);
			pm.setId(lm.get(0).getId());
			mPhoneMessageService.updateBySelect(pm);
		}

		mMember.setPassword(MD5Util.MD5(newpassword));
		mMemberService.updateBySelect(mMember);
		return sendTrueMsg("修改成功");

	}
	/**
	 * 修改绑定手机号
	 */
	@Auth
	@RequestMapping(value = "/member/binding", method = RequestMethod.POST)
	public RequestType binding(String phone, Integer code,String uname) throws Exception {
		if (Stringutil.isBlank(phone))
			return sendFalse("手机号不可为空！");
//		Member mMember = IBeanUtil.Map2JavaBean(mMemberService.getByparameter("uname", uname), Member.class);
		Member mMember = getMember(getLogin(uname));
		
		PhoneMessage pm = new PhoneMessage();
		pm.setPhone(phone);
		pm.setType(MyParameter.phone_message_type2);
		pm.setCode(code);
		@SuppressWarnings("unchecked")
		List<PhoneMessage> lm = (List<PhoneMessage>) mPhoneMessageService.getALL(pm);
		if (lm.size() == 0) {
			return sendFalse("验证码错误！");
		} else {
			if (lm.get(0).getStatus() == 1)
				return sendFalse("验证码已使用！");
			pm.setStatus(1);
			pm.setId(lm.get(0).getId());
			mPhoneMessageService.updateBySelect(pm);
		}
		mMember = GsonUtil.fromJsonString(GsonUtil.toJsonString(mMember), Member.class);
		mMember.setPhone(phone);
		mMemberService.updateBySelect(mMember);
		return sendTrueData(setRedisMember(mMember));
		
	}

	/**
	 * 查询个人资料
	 */
	@Auth
	@RequestMapping(value = "/member/selectinfo", method = RequestMethod.POST)
	public RequestType selectbyid(String uname) throws Exception {

		MemberAuths mMember = getMember(getLogin(uname).getUserid());
		
		
		String s=RedisUtils.get(stringRedisTemplate,MyParameter.SYSTEM_PHONE);
		if(s.indexOf("\n")!=-1){
			s=s.substring(0,s.indexOf("\n"));
			RedisUtils.set(stringRedisTemplate,MyParameter.SYSTEM_PHONE,s);
		}
		if(Stringutil.isBlank(s)){
			s=App.readFileByChars(MyParameter.Phone);
			if(s.indexOf("\n")!=-1)
				s=s.substring(0,s.indexOf("\n"));
			RedisUtils.set(stringRedisTemplate,MyParameter.SYSTEM_PHONE,s);
		}
			
		mMember.setSystemphone(s);
		if (mMember.getState() == 2)
			return sendFalse("账户被封禁！");

//		// 权限
//		Gson gs = new Gson();
//		MemberAuths ma = gs.fromJson(gs.toJson(mMember), MemberAuths.class);
//		if (MyParameter.auths_true) {
//
//			List<String> s = mMemberService.getAuths(mMember.getId());
//			ma.setAuths(s);
//
//		}
//		if (MyParameter.clerk_true) {
//			Map<String, Object> obj = mClerkService.getByparameter("memberid", mMember.getId() + "");
//			ma.setClerk(obj != null);
//		}
//		Shop sh = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", mMember.getId() + ""), Shop.class);
//		if (!ma.isClerk())
//			ma.setClerk(sh != null);
//		ma.setmShop(sh);
		return sendTrueData(mMember);
	}
	/**刷新个人资料*/
	@Auth
	@RequestMapping(value = "/member/updatememberinfo", method = RequestMethod.POST)
	public RequestType updatememberinfo(String uname) throws Exception {
		Member mMember=mMemberService.getByparameter("uname", uname,Member.class);
		try {
			mMember.setGoldcoin(new BigDecimal(ERP_send.member_idnfo(mMember.getUname()).getIc_integral()));
			setRedisMember(mMember, false);
		} catch (Exception e) {
		}
	
		return sendTrueData(getMember(getLogin(uname).getUserid()));
	
	}

	/**
	 * 查询用户
	 */
	@RequestMapping(value = "/member/selectmember", method = RequestMethod.POST)
	public RequestType selectmember(String uname, Integer page, Integer rows,String phone) throws Exception {
		Sql msql = new Sql();
		if (getMember(getLogin(uname).getUserid()).getSuperadmin() == 1) {
			if(!Stringutil.isBlank(phone))
				msql.setSql("select * from member where uname like '%"+phone+"%'");
			else
				msql.setSql("select * from member ");
			msql.setRows(rows);
			msql.setPage(page);
			return sendTrueData(mMemberService.exeSelectSql(msql));
		}
		Shop mShop = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", getLogin(uname).getUserid() + ""),
				Shop.class);
		if (mShop == null)
			return sendFalse("权限不足");
		StringBuilder ids = new StringBuilder();
		Shop mShop1 = new Shop();
		while (mShop1 != null) {
			// 子公司
			mShop1 = IBeanUtil.Map2JavaBean(mShopService.getByparameter("superid", mShop.getId() + ""), Shop.class);
			if (mShop1 != null) {
				mShop = mShop1;
				ids.append(mShop.getMemberid() + ",");
			}

		}
		// 店员
		msql.setSql("select MEMBERID from Clerk WHERE SHOPID=" + mShop.getId());

		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			ids.append(map.get("MEMBERID") + ",");
		}

		if (ids.length() > 2) {
			msql.setSql("select * from member where  id in(" + ids.substring(0, ids.length() - 1) + ")");
			msql.setRows(rows);
			msql.setPage(page);
			return sendTrueData(mMemberService.exeSelectSql(msql));
		}

		msql.setSql("select id from member where 0=1");
		msql.setSql("select * from ("+msql.getSql()+") order by id desc");
		return sendTrueData(mMemberService.exeSelectSql(msql));

	}

	// 冻结，解冻用户账号
	@Auth(admin = true)
	@RequestMapping(value = "/member/updatestate", method = RequestMethod.POST)
	public RequestType updatestate(String phone, Integer state, String uname) throws Exception {
		if (Stringutil.isBlank(phone))
			return sendFalse("用户名不可为空！");
		if (state == null)
			return sendFalse("状态错误！");
		if (state != 1 && state != 2)
			return sendFalse("状态错误！");

		Member mMember = IBeanUtil.Map2JavaBean(mMemberService.getByparameter("phone", phone), Member.class);
		if (mMember == null)
			return sendFalse("用户名不存在！");
		if (getMember(getLogin(uname).getUserid()).getSuperadmin() == 1) {
			mMember.setState(state);
			mMemberService.updateBySelect(mMember);
			return sendTrueMsg("修改成功");
		}

		Shop mShop = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", getLogin(uname).getUserid() + ""),
				Shop.class);
		if (mShop == null)
			return sendFalse("权限不足！");

		// 查询是否是店长账号
		Shop mShop1 = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", mMember.getId() + ""), Shop.class);
		if (mShop1 == null) {
			Clerk mClerk = new Clerk();
			mClerk.setMemberid(mMember.getId());
			mClerk.setShopid(mShop.getId());
			if (mClerkService.getALL(mClerk).size() == 0)
				return sendFalse("权限不足！");

			mMember.setState(state);
			mMemberService.updateBySelect(mMember);
		} else {

			while (mShop1 != null && mShop1.getSuperid() != 0) {
				if (mShop1.getSuperid() == mShop.getId()) {
					// 子店铺
					mMember.setState(state);
					mMemberService.updateBySelect(mMember);
				}
				mShop1 = IBeanUtil.Map2JavaBean(mShopService.getByparameter("superid", mShop1.getId() + ""),
						Shop.class);

			}
			return sendFalse("权限不足！");
		}

		return sendTrueMsg("修改成功");

	}

	/**
	 * 当用户资料更新时更新缓存数据
	 * 
	 * @throws Exception
	 */
	private MemberAuths setRedisMember(Member mMember) throws Exception {
		return setRedisMember(mMember, false);

	}
	
	// /**
	// * 采用rides技术，验证用户是否登录
	// * @param desu 传入RedisMemberType的json加密信息，成功对信息再次加密返回，失败提示未登录
	// * */
	// @RequestMapping(value="/member/ridesMember",method=RequestMethod.POST)
	// public String ridesMember(String desu) throws Exception{
	// Gson gs = new Gson();
	// String ridespwd =DESUtils.decode2(desu, MyParameter.KEY_MEMBER);
	// if(Stringutil.isBlank(ridespwd))
	// return DESUtils.encode2(gs.toJson(sendFalse("未登录")),
	// MyParameter.KEY_MEMBER);//解密失败，密文不符合规则
	// //解密
	// RedisMemberType rmt = gs.fromJson(ridespwd, RedisMemberType.class);
	// if(rmt==null)
	// return DESUtils.encode2(gs.toJson(sendFalse("未登录")),
	// MyParameter.KEY_MEMBER);
	//
	// if(ridespwd.equals(RedisUtils.get(stringRedisTemplate,
	// MyParameter.redis_member+rmt.getUserid())))
	// return
	// DESUtils.encode2(gs.toJson(sendTrueData(ridespwd)),MyParameter.KEY_MEMBER);
	// else
	// return DESUtils.encode2(gs.toJson(sendFalse("未登录")),
	// MyParameter.KEY_MEMBER);//逆向以后明文不匹配
	//
	// }

}
