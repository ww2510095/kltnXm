package com.bm.zsh;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.CommissionTask;
import com.bm.auths.MemberAuths;
import com.bm.auths.groupby.OrganizationService;
import com.bm.auths.organization.Organization;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.MyDate;
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.clerk.commission.CommissionService;
import com.bm.clerk.identity.Identity;
import com.bm.clerk.identity.IdentityService;
import com.bm.friends.Friends;
import com.bm.friends.FriendsService;
import com.bm.shop.Shop;
import com.bm.shop.ShopService;
import com.bm.user.Member;
import com.bm.user.PhoneMessage;
import com.bm.user.PhoneMessageService;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;
import com.myjar.desutil.RunException;


@RestController
@Api(tags = "招商会")
public class ZshController extends BaseController {
	
	@Autowired
	private ZshService mZshService;
	@Autowired
	private Zsh2Service mZsh2Service;
	
	@Autowired
	private PhoneMessageService mPhoneMessageService;
	@Autowired
	private FriendsService mFriendsService;
	@Autowired
	private ShopService mShopService;
	@Autowired
	private ClerkService mClerkService;
	 @Autowired
	 private CommissionService mCommissionService;
	@Autowired
	private IdentityService mIdentityService;
	@Autowired
	private OrganizationService mOrganizationService;
	@Autowired
	private Zsh3Service mZsh3Service;
	
	private String tjjj_398="200";//团建基金
	private String tjjj_1980="1000";//团建基金
	
	/**
	 * 注册
	 * */
	@RequestMapping(value ="/zsh3/zshrcj", method = RequestMethod.POST) 
	public RequestType add(Zsh3 mzsh3) throws Exception{
		if(Stringutil.isBlank(mzsh3.getName()))return sendFalse("名字不可为空");
//		if(Stringutil.isBlank(mzsh3.getNum()))return sendFalse("人数不可为空");
		if(Stringutil.isBlank(mzsh3.getPhone()))return sendFalse("电话不可为空");
		mZsh3Service.add(mzsh3);
		return sendTrueMsg("添加成功");
		
	}
	
	
	/**
	 * 注册
	 * */
	@RequestMapping(value ="/zsh/zc", method = RequestMethod.POST) 
	public RequestType add(String uname,String password,Integer code) throws Exception{
		if (Stringutil.isBlank(uname))
			return sendFalse("用户名不可为空！");
		if (code==null)
			return sendFalse("验证码不可为空！");
		if (Stringutil.isBlank(password))
			return sendFalse("密码不可为空！");
		
		Member mMember= new Member();
		PhoneMessage pm = new PhoneMessage();
		pm.setPhone(mMember.getUname());
		pm.setType(MyParameter.phone_message_type0);
		pm.setCode(code);

		
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
	

		long uid = System.currentTimeMillis();
		Friends fi = new Friends();
		fi.setMemberidb(uid);

		Map<String, Object> map = mShopService.getByparameter("code", "8888");
		fi.setMemberida(Long.valueOf(map.get("MEMBERID").toString()));
		fi.setMemberidb(uid);
		fi.setType(2);
		mFriendsService.add(fi);
		mMember.setId(uid);
		mMember.setSource(1);
		memberRegister(mMember,false);
		
		mPhoneMessageService.updateBySelect(pm);
		

		return sendTrueMsg("恭喜你，注册成功");

	}
	
	/**
	 * 我要合作页面
	 * */
	@RequestMapping(value ="/zsh/xd", method = RequestMethod.POST) 
	@Auth
	public  RequestType xd(Integer type,String uname) throws Exception{
		if(type==null)sendFalse("类型不可为空");
		String id ="zsh"+ System.currentTimeMillis();
		Zsh mZsh = new Zsh();
		mZsh.setMemberid(getLogin(uname).getUserid());
		mZsh.setPhone(uname);
		mZsh.setMoney(type==1?new BigDecimal(1980):new BigDecimal(398));
		mZsh.setType(type);
		mZsh.setStatis(0);
		mZsh.setIstrue(0);
		mZsh.setId(id);
		mZshService.add(mZsh);
		return sendTrueData(DESUtils.encode2(id, MyParameter.KEY_ORDERS, 10));
	}
	/**
	 * 摸个时间段下单数量
	 * */
	@RequestMapping(value ="/zsh/xdsl", method = RequestMethod.POST) 
	@Auth
	public  RequestType xd(Long star,Long end,Integer type) throws Exception{
		if(type==null)type=1;
		if(star==null)star=0L;
		if(end==null)end=System.currentTimeMillis();	
		Sql msql  =new Sql();
		if(type==1)
			msql.setSql("select count(*) 数量 from orders where id>"+star+" and id<"+end);
		else
			msql.setSql("select count(*) 数量  from Zsh where to_number(SUBSTR(id,4,20))>"+star+" and to_number(SUBSTR(id,4,20))<"+end);
		return sendTrueData(mMemberService.exeSelectSql(msql));
	}
	/**
	 * 查询订单状态
	 * */
	@RequestMapping(value ="/zsh/type", method = RequestMethod.POST) 
	@Auth
	public  RequestType type(String id) throws Exception{
		Zsh mZsh = mZshService.getById(id,Zsh.class);
		if(mZsh==null)
			return sendFalse("订单不存在");
		if(mZsh.getStatis()==0){
			return sendFalse("未支付",-9);
		}else{
			return sendTrueMsg("");
		}
	}
	/**
	 * 添加虚拟数据
	 * */
	@RequestMapping(value ="/zsh/addxni", method = RequestMethod.POST) 
	@Auth(admin=true)
	public  RequestType addxuni() throws Exception{
		String id ="zsh"+ System.currentTimeMillis();
		Zsh mZsh = new Zsh();
		mZsh.setMemberid(System.currentTimeMillis());
		mZsh.setPhone(getTel());
		if(gettype()>1){
			mZsh.setMoney(new BigDecimal(398));
			mZsh.setType(2);
		}
		else{
			mZsh.setMoney(new BigDecimal(1980));
			mZsh.setType(1);
		}
			
		mZsh.setStatis(1);
		mZsh.setIstrue(0);
		mZsh.setId(id);
		int code = new Random().nextInt(20)+5;
		mZsh.setUpdatetime(System.currentTimeMillis()+(Long.valueOf(code)*1000L));
		mZsh2Service.add(mZsh);
		return sendTrueData(DESUtils.encode2(id, MyParameter.KEY_ORDERS, 10));
	}
	
	private int gettype() {
		return  new Random().nextInt(10);

	}
	
	 /**
     * 返回手机号码 
     */
    private static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }
    public static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }
	/**
	 * 查询权益购买情况
	 * */
	@RequestMapping(value ="/zsh/pm", method = RequestMethod.POST) 
	@Auth
	public  RequestType pm(Integer page,Integer rows,Long star,Long end) throws Exception{
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		rows = rows==null?20:rows;
		Sql msql = new Sql();
		msql.setSql("select id 编号,phone 购买人,"+MyDate.orcaleCDATE("to_number(SUBSTR(id,4,20))", "下单时间")
				+","+MyDate.orcaleCDATE("updatetime", "支付时间")+",decode(type,1,'经销商',2,'线上店主') 类型,money 付款金额 "
						+ " from zsh where statis=1 and id>"+star+" and id<"+end );
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey("to_number(SUBSTR(id,4,20))");
		msql.setOrderbytype(1);
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}
	/**
	 * 查询权益购买情况ex
	 * */
	@RequestMapping(value ="/zsh/pmex", method = RequestMethod.POST) 
	@Auth
	public  RequestType pmex(Integer page,Integer rows,Long star,Long end) throws Exception{
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		rows = rows==null?20:rows;
		Sql msql = new Sql();
		msql.setSql("select id 编号,replace(phone,SUBSTR(phone,4,4),'****') 购买人,"+MyDate.orcaleCDATE("to_number(SUBSTR(id,4,20))", "下单时间")
		+","+MyDate.orcaleCDATE("updatetime", "支付时间")+",decode(type,1,'经销商',2,'线上店主') 类型,money 付款金额 "
		+ " from zsh where statis=1 and to_number(SUBSTR(id,4,20))>"+star+" and to_number(SUBSTR(id,4,20))<"+end +" union all select id 编号,replace(phone,SUBSTR(phone,4,4),'****')  购买人,"+MyDate.orcaleCDATE("to_number(SUBSTR(id,4,20))", "下单时间")
		+","+MyDate.orcaleCDATE("updatetime", "支付时间")+",decode(type,1,'经销商',2,'线上店主') 类型,money 付款金额 "
		+ " from zshex where statis=1 and to_number(SUBSTR(id,4,20))>"+star+" and to_number(SUBSTR(id,4,20))<"+end );
		msql.setPage(page);
		msql.setRows(rows);
//		msql.setOrderbykey("to_number(SUBSTR(id,4,20))");
//		msql.setOrderbytype(1);
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}
	/**
	 * 查询订单排名
	 * */
	@RequestMapping(value ="/zsh/orders/pm", method = RequestMethod.POST) 
	@Auth(admin=true)
	public  RequestType ordersom(Integer page,Integer rows,Long star,Long end) throws Exception{
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		rows = rows==null?20:rows;
		Sql msql = new Sql();
		msql.setSql("select orders.id 编号,replace(uname,SUBSTR(uname,4,4),'****') 购买人,"+MyDate.orcaleCDATE("orders.id", "下单时间")
		+","+MyDate.orcaleCDATE("paymenttime", "支付时间")+",payment 付款金额 "
		+ " from orders left join member on memberid=member.id where paymenttime>"+star+" and paymenttime<"+end);
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey("paymenttime");
		msql.setOrderbytype(0);
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}
	/**
	 * 激活
	 * */
	@RequestMapping(value ="/zsh/jh", method = RequestMethod.POST) 
	@Auth
	public  RequestType jh(String id,String shopcode,String suphone,String uname) throws Exception{
		if(Stringutil.isBlank(id))return sendFalse("订单不可为空");
		if(Stringutil.isBlank(shopcode))return sendFalse("店铺编号不可为空");
		
		Zsh mZsh = mZshService.getById(id,Zsh.class);
		if(mZsh==null)
			return sendFalse("订单不存在");
		
		if(!mZsh.getPhone().equals(uname)){
			return sendFalse("该订单不属于你");
		}

		if(mZsh.getStatis()==0){
			return sendFalse("订单未支付");
		}else{
			if(mZsh.getIstrue()==1){
				return sendFalse("订单已激活");
			}
			if(shopcode.length()==13){
				String scode;
				Identity mIdentity = mIdentityService.getByparameter("memberid", shopcode,Identity.class);
				if(mIdentity==null){
					if(getMember(Long.valueOf(shopcode)).getmShop()==null){
						return sendFalse("错误");
					}
					scode = getMember(Long.valueOf(shopcode)).getmShop().getCode();
				}else{
					if(mIdentity.getType()==2){
						mIdentity = mIdentityService.getById(mIdentity.getSuid(),Identity.class);
					}
					
					scode = mShopService.getById(mIdentity.getShopid()).get("CODE").toString();
				}
				
				 if(mIdentity==null)
					 shopcode= getMember(Long.valueOf(shopcode)).getUname();
				 else
					 shopcode=getMember(mIdentity.getMemberid()).getUname();
				IdentityAdd(mZsh.getPhone(), scode, shopcode, mZsh.getType(), 365L, uname);
				mZsh.setIstrue(1);
				mZshService.updateBySelect(mZsh);
				if(mZsh.getType()==1)
					CommissionTask.start_je(mCommissionService, getLogin(uname).getUserid()+";"+getMember(shopcode).getId(), CommissionTask.COMMISSION_K2,tjjj_1980);
				else
					CommissionTask.start_je(mCommissionService, getLogin(uname).getUserid()+";"+getMember(shopcode).getId(), CommissionTask.COMMISSION_K2,tjjj_398);
				return sendTrueMsg("激活成功");
			}else{
				return sendFalse("参数错误");
//				IdentityAdd(mZsh.getPhone(), shopcode, suphone, mZsh.getType(), 365L, uname);
//				mZsh.setIstrue(1);
//				mZshService.updateBySelect(mZsh);
//				return sendTrueMsg("激活成功");
			}
			
			
		}
	}
	/**
	 * 虚拟付款
	 * */
	@RequestMapping(value ="/zsh/xnfk", method = RequestMethod.POST) 
	@Auth(admin=true)
	public  RequestType xnfk() throws Exception{
		Sql msql = new Sql();
		msql.setSql("update zsh set statis=1 ");
		return sendTrueMsg(mMemberService.execSQL(msql));
	}
	
	private RequestType IdentityAdd(String phone,String shopcode,String suphone,Integer type,Long date, String uname) throws Exception{
		MemberAuths mMember = getMember(phone);
		if(mMember.getmShop()!=null)
			throw new RunException("错误，账户"+phone+"已经是实体店主了，不能成为渠道");
		
		if(date==null){
				throw new RunException("错误，时限不可为空（天）");
		}
		
		
		
		Sql msql = new Sql();
		Shop mshop = mShopService.getByparameter("code", shopcode,Shop.class);
		if(mshop==null){
				throw new RunException("错误，店铺"+shopcode+"不存在");
		}
		
		Long ida = System.currentTimeMillis();
		
		Identity mIdentity = new Identity();
		mIdentity.setMemberid(mMember.getId());

		try {
			mIdentity= (Identity) mIdentityService.getALL(mIdentity).get(0);
		} catch (Exception e) {
			mIdentity=null;
		}
		
		if(mIdentity!=null){
			if(mIdentity.getEnd()>System.currentTimeMillis()){
					throw new RunException("错误，"+phone+"已经是经销售或线上店主");
			}
		}
	
		
		
		mIdentity = new Identity();
		mIdentity.setMemberid(mMember.getId());
		mIdentity.setType(type);
		mIdentity.setStar(System.currentTimeMillis());
		mIdentity.setEnd(mIdentity.getStar()+(86400000L*date));
		if(!Stringutil.isBlank(suphone)){
			if(getMember(suphone).getmShop()!=null){
				Identity mIdentity1;
				synchronized (this) {
					mIdentity1 = mIdentityService.getByparameter("memberid", getMember(suphone).getId()+"",Identity.class);
					if(mIdentity1==null){
						Long id = System.currentTimeMillis();
						mIdentity1 = new Identity();
						mIdentity1.setMemberid(getMember(suphone).getId());
						mIdentity1.setType(0);
						mIdentity1.setStar(id);
						mIdentity1.setEnd(MyDate.dateToStamp("9999-11-28 23:59:59"));
						mIdentity1.setSuid(0L);
						mIdentity1.setShopid(mshop.getId());
						mIdentity1.setId(id);
						mIdentity1.setSystem(1);
						mIdentityService.add(mIdentity1);
					}
					
				}
				 
			
				
				mIdentity.setSuid(mIdentity1.getId());
			}else{
				try {
					Map<String, Object> map= mIdentityService.getByparameter("memberid", getLogin(suphone).getUserid()+"");
					if(map.get("TYPE").toString().equals("2"))
						throw new RunException("");
					mIdentity.setSuid(Long.valueOf(map.get("ID").toString()));
				} catch (Exception e) {
						throw new RunException("错误，"+suphone+"不能成为渠道上级");
				}
			}
		}else{

			Identity mIdentity1 = mIdentityService.getByparameter("memberid", mshop.getMemberid()+"",Identity.class);
			if(mIdentity1==null){
				Long id = System.currentTimeMillis();
				mIdentity1 = new Identity();
				mIdentity1.setMemberid(mshop.getMemberid());
				mIdentity1.setType(0);
				mIdentity1.setStar(id);
				mIdentity1.setEnd(MyDate.dateToStamp("9999-11-28 23:59:59"));
				mIdentity1.setSuid(0L);
				mIdentity1.setShopid(mshop.getId());
				mIdentity1.setId(id);
				mIdentityService.add(mIdentity1);
			}
			
			
			mIdentity.setSuid(mIdentity1.getId());
		
		}
	
		mIdentity.setShopid(mshop.getId());
		
		msql.setSql("select id from clerk where memberid="+mMember.getId());
		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
		if(listmap.size()==0){
			Clerk mClerk = new Clerk();
			mClerk.setMemberid(mMember.getId());
			mClerk.setShopid(mshop.getId());
			mClerk.setState(1);
			mClerk.setId(ida);
			mClerkService.add(mClerk);
			
			//权限
			Organization mOrganization = new Organization();
			mOrganization.setGroupbyname("店员");
			mOrganization.setMemberid(mMember.getId());
			mOrganizationService.add(mOrganization);
			
			msql.setSql("update Friends set memberida="+mMember.getId()+" where  memberidb="+mMember.getId());
			mClerkService.execSQL(msql, -1, "");
			
			
		}
			if(getMember(uname).getSuperadmin()==1)
				mIdentity.setSystem(1);
			else
				mIdentity.setSystem(0);
			if(listmap.size()!=0)
			mIdentity.setOneshopid(Long.valueOf(listmap.get(0).get("ID").toString()));
			Identity mIdentity1 = mIdentityService.getByparameter("memberid", mMember.getId()+"",Identity.class);
			if(mIdentity1==null){
				mIdentity.setOnetype(2);
				mIdentityService.add(mIdentity);
				
				//优惠卷
				mCouponService.OutsendCouponZSH(mIdentity.getMemberid(), type);
			}else{
				if(listmap.size()==0)
					mIdentity.setOnetype(1);
				else
					mIdentity.setOnetype(mIdentity1.getType()==1?3:4);
				mIdentity.setId(mIdentity1.getId());
				mIdentityService.updateBySelect(mIdentity);
				
				//优惠卷
				mCouponService.OutsendCouponZSH(mIdentity.getMemberid(), type);
			}
			
		
		
		
		
		
		return sendTrueMsg("添加成功");

	}

}
