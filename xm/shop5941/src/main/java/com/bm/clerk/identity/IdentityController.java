package com.bm.clerk.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.auths.MemberAuths;
import com.bm.auths.groupby.OrganizationService;
import com.bm.auths.organization.Organization;
import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.MyDate;
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.orders.OrdersController;
import com.bm.shop.Shop;
import com.bm.shop.ShopService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "线上店主与经销商")
public class IdentityController extends BaseController{
	
	@Autowired
	private IdentityService mIdentityService;
	@Autowired
	private ShopService mShopService;
	@Autowired
	private ClerkService mClerkService;
	@Autowired
	private OrganizationService mOrganizationService;
	

	/**
	 *添加经销售或者线上店主
	 *phone,要添加的账户
	 *shopcode，店铺编码
	 *suphone 上级
	 *type 身份，1：经销商，2：线上店主
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Identity/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType IdentityAdd(String phone,String shopcode,String suphone,Integer type,Long date, Integer row,String uname) throws Exception{
		MemberAuths mMember = getMember(phone);
		if(mMember.getmShop()!=null)
			throw new RunException("错误，账户"+phone+"已经是实体店主了，不能成为渠道");
		
		if(date==null){
			if(row!=null)
				throw new RunException("错误，第"+row+"行，时限不可为空（天）");
			else
				throw new RunException("错误，时限不可为空（天）");
		}
//		if(type==1){
//			if(Stringutil.isBlank(suphone)){
//				if(row!=null)
//					throw new RunException("错误，第"+row+"行，上级为空");
//				else
//					throw new RunException("错误，上级不可为空");
//			}
//		}
		
		
		
		Sql msql = new Sql();
		Shop mshop = mShopService.getByparameter("code", shopcode,Shop.class);
		if(mshop==null){
			if(row!=null)
				throw new RunException("错误，第"+row+"行，店铺"+shopcode+"不存在");
			else
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
				if(row!=null)
					throw new RunException("错误，第"+row+"行，"+phone+"已经是经销售或线上店主");
				else
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
//				Identity mIdentity1 = mIdentityService.getByparameter("memberid", getMember(suphone).getId()+"",Identity.class);
//				if(mIdentity1==null){
//					Long id = System.currentTimeMillis();
//					mIdentity1 = new Identity();
//					mIdentity1.setMemberid(getMember(suphone).getId());
//					mIdentity1.setType(0);
//					mIdentity1.setStar(id);
//					mIdentity1.setEnd(MyDate.dateToStamp("9999-11-28 23:59:59"));
//					mIdentity1.setSuid(0L);
//					mIdentity1.setShopid(mshop.getId());
//					mIdentity1.setId(id);
//					mIdentity1.setSystem(1);
//					mIdentityService.add(mIdentity1);
//				}
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
					mIdentity.setSuid(mIdentity1.getId());
					
				}
				 
				
			
			}else{
				try {
					Map<String, Object> map= mIdentityService.getByparameter("memberid", getLogin(suphone).getUserid()+"");
					if(map.get("TYPE").toString().equals("2"))
						throw new RunException("");
					mIdentity.setSuid(Long.valueOf(map.get("ID").toString()));
				} catch (Exception e) {
					if(row!=null)
						throw new RunException("错误，第"+row+"行，"+suphone+"不能成为渠道上级");
					else
						throw new RunException("错误，"+suphone+"不能成为渠道上级");
				}
			}
		}else{

			Identity mIdentity1 = mIdentityService.getByparameter("memberid", mshop.getMemberid()+"",Identity.class);
			synchronized (this) {
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
			
			mIdentity.setOnetype(1);
			
			//mIdentityService.add(mIdentity);
			
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
	/**
	 *查询经销售或者线上店主
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Identity/select", method = RequestMethod.POST) 
	public RequestType IdentityAdd(Identity mIdentity,String phone,Integer page,Integer rows,String orderbykey,Integer orderbytype) throws Exception{
		if(!Stringutil.isBlank(phone))
			mIdentity.setMemberid(getLogin(phone).getUserid());
		
		
		@SuppressWarnings("unchecked")
		List<Identity> listIdentity = (List<Identity>) mIdentityService.getALL(mIdentity, orderbykey, orderbytype, page, rows);
		for (Identity identity : listIdentity) {
			identity.setMemberid(Long.valueOf(getMember(identity.getMemberid()).getUname()));
			identity.setShopname(mShopService.getById(identity.getShopid()).get("SHOPNAME").toString()); ;
		}
		return sendTrueData(listIdentity);
		
	}
//	/**
//	 *角色切换
//	 *phone,要添加的账户
//	 *shopcode，店铺编码
//	 *suphone 上级
//	 *type 身份，1：经销商，2：线上店主，3普通导购
//	 * */
//	@Auth(admin=true)
//	@RequestMapping(value ="/Identity/updatejs", method = RequestMethod.POST) 
//	public RequestType updatejs(String phone,String shopcode,String suphone,Integer type,Long date, Integer row,String uname) throws Exception{
//		if()
//		if(!Stringutil.isBlank(phone))
//			mIdentity.setMemberid(getLogin(phone).getUserid());
//		return sendTrueData(mIdentityService.getALL(mIdentity, orderbykey, orderbytype, page, rows));
//		
//	}
	/**
	 *用户列表上面那个
	 * */
	@Auth
	@RequestMapping(value ="/Identity/FriendsType", method = RequestMethod.POST) 
	public RequestType FriendsType(String uname) throws Exception{
		List<FriendsType> sa = new ArrayList<FriendsType>();
		FriendsType ma = new FriendsType();
		ma.setStatus(1);
		ma.setStr("店铺分销商");
		FriendsType ma2 = new FriendsType();
		ma2.setStatus(2);
		ma2.setStr("店铺合伙人");
		FriendsType ma3 = new FriendsType();
		ma3.setStatus(3);
		ma3.setStr("导购");
		FriendsType ma4 = new FriendsType();
		ma4.setStatus(4);
		ma4.setStr("普通用户");
		
		
		sa.add(ma);
		sa.add(ma2);
		sa.add(ma3);
		sa.add(ma4);
		return sendTrueData(sa);
		
	}
	public class FriendsType{
		private Integer status;
		private String str;
		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public String getStr() {
			return str;
		}
		public void setStr(String str) {
			this.str = str;
		}
		
	}
	
	/**
	 *查询指定类型用户
	 *type:1:经销商，2，线上店主，3：普通导购，4自己的客户
	 * */
	@Auth
	@RequestMapping(value ="/Identity/FriendsBytype", method = RequestMethod.POST) 
	public RequestType IdentityFriendsBytype(String uname,Integer page,Integer rows,Integer type) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id,uname ,nvl(bz,nickname) bz from Friends"
				+ " left join Identity on Friends.memberidb=Identity.memberid left join member on member.id=Friends.memberidb "
				+ "where 1=1");
		if(type==1){//经销商
			msql.setSql(msql.getSql()+" and Identity.type=1 ");
			
			if(getMember(uname).getSuperadmin()!=1){
				msql.setSql(msql.getSql()+" and suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+" )");
			}
		}else if(type==2){//线上店主
			msql.setSql(msql.getSql()+" and Identity.type=2 ");
			
			if(getMember(uname).getSuperadmin()!=1){
				msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
				
				if(getMember(uname).getmShop()==null){
					msql.setSql(msql.getSql()+" and suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+" )");
				}
			}
		}else if(type==3){//普通导购
			msql.setSql(msql.getSql()+" and Friends.memberidb not in(select memberid from Identity) and Friends.memberidb in  (select memberid from clerk where shopid="+getMember(uname).getmShop().getId()+")");
			
//			if(getMember(uname).getSuperadmin()!=1){
//				msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
//			}
//		
		}else if(type==4){
			//普通用户
			msql.setSql(msql.getSql()+" and Friends.memberida != memberidb ");
			
			if(getMember(uname).getSuperadmin()!=1){
				msql.setSql(msql.getSql()+" and memberida="+getLogin(uname).getUserid());
			}
		
		
		}
		msql.setSql("select * from ("+msql.getSql()+") order by id desc");
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mIdentityService.exeSelectSql(msql));
		
	}
	/**
	 *查询某个经销商或者线上店主下面的人（手机端返回自己的数据）
	 *type:1:经销商，2，线上店主，3：普通导购，4自己的客户
	 * */
	@Auth
	@RequestMapping(value ="/Identity/Friends", method = RequestMethod.POST) 
	public RequestType IdentityFriends(String phone,String uname,Integer page,Integer rows,Integer type) throws Exception{
		Sql msql = new Sql();
		if(type==1){//经销商
			
			msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberid,uname ,nvl(bz,nickname) bz from Identity"
					+ " left join Friends on Friends.memberidb=Identity.memberid left join member on member.id=Identity.memberid "
					+ "where Identity.type=1");
			if(getMember(uname).getSuperadmin()==1){
				if(!Stringutil.isBlank(phone)){
					uname=phone;
					msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
				}
				
			}else{
				if(getMember(uname).getmShop()!=null){
					msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
				}else{
					msql.setSql(msql.getSql()+" and Identity.memberid="+getLogin(uname).getUserid());
				}
			}
		}else if(type==2){//线上店主

			msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberid,uname ,nvl(bz,nickname) bz from Identity"
					+ " left join Friends on Friends.memberidb=Identity.memberid left join member on member.id=Identity.memberid "
					+ "where Identity.type=2");
			if(getMember(uname).getSuperadmin()==1){
				if(!Stringutil.isBlank(phone)){
					uname=phone;
					msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
				}
				
			}else{
				if(getMember(uname).getmShop()!=null)
					msql.setSql(msql.getSql()+" and shopid="+getShop(uname).getId());
				else
					msql.setSql(msql.getSql()+" and suid in(select id from Identity where memberid="+getLogin(uname).getUserid()+")");
			}
		
		}else if(type==3){//普通导购
			if(getMember(uname).getSuperadmin()==1){
				if(!Stringutil.isBlank(phone)){
					msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberid,uname ,nvl(bz,nickname) bz from clerk"
							+ " left join Friends on Friends.memberidb=clerk.memberid left join member on member.id=clerk.memberid "
							+ "where clerk.memberid not in(select memberid from Identity where memberid !="+getLogin(uname).getUserid()+""
									+ " and shopid="+getShop(uname).getId()+") and shopid="+getShop(uname).getId());
				}else{
					msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberid,uname ,nvl(bz,nickname) bz from clerk"
							+ " left join Friends on Friends.memberidb=clerk.memberid left join member on member.id=clerk.memberid "
							+ "where clerk.memberid not in(select memberid from Identity ) ");
				}
				
			}else{
				if(getMember(uname).getmShop()==null){
					msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberidb memberid,uname ,nvl(bz,nickname) bz from Friends"
							+ " left join member on memberidb=member.id where memberida="+getLogin(uname).getUserid());
				}else{
					msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberidb memberid,uname ,nvl(bz,nickname) bz from Friends"
							+ " left join member on memberidb=member.id left join clerk on clerk.memberid=Friends.memberidb "
							+ "where clerk.shopid="+getMember(uname).getmShop().getId()
							+" and clerk.memberid not in(select memberid from Identity where shopid="+getMember(uname).getmShop().getId()+")"
									+ " or memberida="+getLogin(uname).getUserid());
				}
				
			}
		
		}else{
			
			msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberidb memberid,uname ,nvl(bz,nickname) bz from Friends"
					+ " left join member on memberidb=member.id left join clerk on memberidb=clerk.memberid"
					+ " where memberida="+getLogin(uname).getUserid());
			if(getMember(uname).getmShop()!=null){
				
				msql.setSql(msql.getSql()+" or clerk.shopid= "+getMember(uname).getmShop().getId());
				
			}
		
			
//			if(getMember(uname).getSuperadmin()==1){
//				if(!Stringutil.isBlank(phone)){
//					uname=phone;
//					msql.setSql(msql.getSql()+" and clerk.shopid="+getShop(uname).getId());
//				}
//				
//			}else{
//				msql.setSql(msql.getSql()+" and clerk.shopid="+getShop(uname).getId());
//			}
			
		}
//		msql.setSql("select "+MyDate.orcaleCDATE("Friends.id") +", Friends.id, memberid,uname ,nvl(bz,nickname) bz from Friends left join "
//				+ "member on memberidb=member.id left join clerk on clerk.memberid=memberidb where 1=1 ");
//		if(getMember(uname).getSuperadmin()==1){
//			if(!Stringutil.isBlank(phone)){
//				uname=phone;
//				msql.setSql(msql.getSql()+" and shopid="+getMember(uname).getmShop().getId());
//			}
//		}else{
////			msql.setSql(msql.getSql()+" and shopid="+getMember(uname).getmShop().getId());
//		}
//		if(type!=null){
//			if(type==3){
//				if(getMember(uname).getmShop()!=null)
//				msql.setSql(msql.getSql()+" and Friends.memberidb not in(select memberid from Identity) and shopid="+
//						getMember(uname).getmShop().getId() +" and memberidb in (select memberid from clerk)");
//				else
//					msql.setSql(msql.getSql()+" and Friends.memberida="+getLogin(uname).getUserid());
//			}else if(type==4){
//				msql.setSql(msql.getSql()+" and Friends.memberidb not in(select memberid from clerk) and shopid="+
//						getMember(uname).getmShop().getId() );
//			}else{
//				if(type==1)
//					msql.setSql(msql.getSql()+" and Friends.memberida="+getLogin(uname).getUserid());
//				else
//					msql.setSql(msql.getSql()+" and (Friends.memberida="+getLogin(uname).getUserid()+
//							" or memberida in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+" and end>"+System.currentTimeMillis()+")))");
//				
//				
//				try {
//					msql.setSql(msql.getSql()+" and Friends.memberidb  in(select memberid from Identity where shopid="+
//							getMember(uname).getmShop().getId()+" ) ");
//				} catch (Exception e) {
//					msql.setSql(msql.getSql()+" and Friends.memberidb  in(select memberid from Identity where suid=(select id from )"+
//							getMember(uname).getmShop().getId()+" ) ");
//				}
//				
//			}
//		}
		
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mIdentityService.exeSelectSql(msql));
		
	}
	

	/**
	 *用户列表，包括佣金
	 *type:1:经销商，2，线上店主，3：普通导购，4自己的客户
	 * @throws Exception 
	 * */
//	private List<Map<String, Object>> getFriendslist(int type,String uname,Integer page,Integer rows) throws Exception{
//		if(rows==null) rows=10;
//		Sql msql = new Sql();
//		
//		if(getMember(uname).getmShop()==null){
//			if(type==1){
//				msql.setSql("select member.id memberid,uname bz,uname from Friends left join member on member.id=memberidb where memberida="+getLogin(uname).getUserid());
//			}else if(type==2){
//				msql.setSql("select type from Identity where memberid="+getLogin(uname).getUserid());
//				if(mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString().equals("1")){
//					//经销商
//					msql.setSql(
//							 " select memberid,uname bz,uname from Identity left join member on memberid=member.id "
//							+ " where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+") ");
//				}else{
//					msql.setSql("select member.id memberid,uname bz,uname from Friends left join member "
//							+ " on member.id = memberidb where memberida="+getLogin(uname).getUserid());
//				}
//				
//			}else {
//				msql.setSql("select member.id memberid,uname bz,uname from Friends left join member on member.id=memberidb where memberida="+getLogin(uname).getUserid());
//				
//			}
//		}else{
//			if(type==1){
//				msql.setSql(
//						 " select memberid,uname bz,uname from Identity left join member on memberid=member.id "
//						+ " where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+") and Identity.type=1");
//			}else if(type==2){
//				msql.setSql(
//						 " select memberid,uname bz,uname from Identity left join member on memberid=member.id "
//						+ " where shopid=(select shopid from Identity where memberid="+getLogin(uname).getUserid()+") and Identity.type=2");
//				
//			}else{
//				msql.setSql("select member.id memberid,uname bz,uname from Friends left join member on member.id=memberidb where memberida="
//						+getLogin(uname).getUserid()+" union all "
//							+ " select member.id memberid, uname bz,uname from clerk left join member on member.id=memberid "
//							+ "where shopid="+getMember(uname).getmShop().getId()+" and memberid !="+getLogin(uname).getUserid()
//							+" and member.id not in(select memberid from Identity where shopid="+getMember(uname).getmShop().getId()+")");
//			}
//		
//		}
//	
//		msql.setPage(page);
//		msql.setRows(rows);
//		return mMemberService.exeSelectSql(msql);
//	} 
	
//	private String getFriendslistSql(String sum,String where){
//		return "select sum("+sum+") money ,uname bz,member.id MEMBERID,uname "
//				+ "from Commission  "
//				+ " left join member on member.id=memberidsu "+where+" group by member.id,uname";
//	} 
	
	/**
	 *用户列表，包括佣金
	 *type:1:经销商，2，线上店主，3：普通导购，4实体店主
	 *key:，1：未入账，2已入账，3：已结算
	 * */
	@Auth
	@RequestMapping(value ="/Identity/Friendslist", method = RequestMethod.POST) 
	public RequestType Friendslist(String uname,Integer page,Integer rows,Integer type,Integer key) throws Exception{
		if(type==null)return sendFalse("错误");
		if(key==null)return sendFalse("错误");
		
//		Long time=1538323200000L;//2018年10月1日0点0分0秒
		
		Sql msql = new Sql();
		Integer a;
		try {
			msql.setSql("select type from Identity where memberid="+getLogin(uname).getUserid());
			a=Integer.parseInt(mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString());
//			map.put("type",mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString());
		} catch (Exception e) {
			if(getMember(uname).getmShop()==null)
				a=3;
			else
				a=0;
		}
		List<Map<String, Object>> listmap;
		msql.setPage(page);
		msql.setRows(rows);
		if(a==0){
			if(type==0){
				//店主门店贡献
				msql.setSql("select a.*,uname,member.id memberid from ("
						+ "select sum(numsu2) num,memberidsu  from Commission where state="+key+" and num2=0 and num3=0 and memberid4="+getLogin(uname).getUserid()+" group by memberidsu)"
								+ " a left join member on memberidsu=member.id where uname is not null");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
			}else if(type==1){
				//经销商
				msql.setSql("select a.*,uname,member.id memberid from ("
						+ "select sum(numsu2) num,memberid3  from Commission where state="+key+" and  num3!=0 and memberid4="+getLogin(uname).getUserid()+" group by memberid3)"
								+ " a left join member on memberid3=member.id where uname is not null");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
			}else{
				//线上店主
				msql.setSql("select a.*,uname,member.id memberid from ("
						+ "select sum(num4) num,memberid2  from Commission where state="+key+" and num1=0 and num3=0 and memberid4="+getLogin(uname).getUserid()+" group by memberid2)"
								+ " a left join member on memberid2=member.id where uname is not null");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
				
			}
		

			
			
		
		}else {

			if(type==2){
				//线上店主
				msql.setSql("select a.*,uname,member.id memberid from ("
						+ "select sum(my_to_app_je(2,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num,memberid2  from Commission where state="+key+" group by memberid2)"
								+ " a left join member on memberid2=member.id where uname is not null and num>0");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
				
			
			}else if(type==1){
				//经销商
				msql.setSql("select a.*,uname,member.id memberid from ("
						+ "select sum(my_to_app_je(3,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num,memberid3  from Commission where state="+key+"  group by memberid3)"
								+ " a left join member on memberid3=member.id where uname is not null and num>0");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
			}else{
				//门店贡献
				msql.setSql("select a.*,uname from ("
						+ "select sum(my_to_app_je(1,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num,memberid  from Commission where state="+key+"  group by memberid)"
								+ " a left join member on a.memberid=member.id where uname is not null and num>0");
				listmap = mMemberService.exeSelectSql(msql);
				for (Map<String, Object> map2 : listmap) {
					map2.put("money", map2.get("NUM"));
					map2.put("BZ", map2.get("UNAME"));
				}
			}
		

			
			
		
		
		}
			
			
			
//			if(a==1){
//			if(type==1){
//				//经销商
//				msql.setSql("select a.*,uname,member.id memberid from ("
//						+ "select sum(num3) num ,memberid3 from Commission where state="+key+" and num2=0 and   memberid3="+getLogin(uname).getUserid()+""
//								+ " group by memberid3)"
//								+ " a left join member on memberid3=member.id");
//				listmap = mMemberService.exeSelectSql(msql);
//				for (Map<String, Object> map2 : listmap) {
//					map2.put("money", map2.get("NUM"));
//					map2.put("BZ", map2.get("UNAME"));
//				}	
//			}else{
//				//线上店主
//				msql.setSql("select a.*,uname,member.id memberid from ("
//						+ "select sum(num3) num , memberid2 from Commission where state="+key+" and num2!=0 and  memberid3="+getLogin(uname).getUserid()+"  group by memberid2)"
//						+ " a left join member on memberid2=member.id");
//				listmap = mMemberService.exeSelectSql(msql);
//				for (Map<String, Object> map2 : listmap) {
//					map2.put("money", map2.get("NUM"));
//					map2.put("BZ", map2.get("UNAME"));
//				}	
//			}
//			
//			
//		}else {
//			msql.setSql("select id from Identity where memberid="+getLogin(uname).getUserid());
//			if(mMemberService.exeSelectSql(msql).size()==0){
//				msql.setSql("select a.*,uname,member.id memberid from ("
//						+ "select sum(num1) num  ,memberid1 from Commission where state="+key+" and   memberid1="+getLogin(uname).getUserid()+" group by memberid1)"
//								+ " a left join member on memberid1=member.id");
//			}else{
//				msql.setSql("select a.*,uname,member.id memberid from ("
//						+ "select sum(num2) num  ,memberid2 from Commission where state="+key+" and   memberid2="+getLogin(uname).getUserid()+" group by memberid2)"
//								+ " a left join member on memberid2=member.id");
//			}
//		
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				map2.put("money", map2.get("NUM"));
//				map2.put("BZ", map2.get("UNAME"));
//			}
//		}
//		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		
//		
//		String sql;
//		switch (type) {
//		case 1:
//			if(key==1){
//				sql=getFriendslistSql("numsu1", "where Commission.state=1 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else if(key==2){
//				sql=getFriendslistSql("numsu1", "where Commission.state=2 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else{
//				sql=getFriendslistSql("numsu1", "where Commission.state=3 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}
//			break;
//		case 2:
//			if(key==1){
//				sql=getFriendslistSql("num2", "where Commission.state=1  and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else if(key==2){
//				sql=getFriendslistSql("num2", "where Commission.state=2 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else{
//				sql=getFriendslistSql("num2", "where Commission.state=3 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}
//			break;
//		case 3:
//			if(key==1){
//				sql=getFriendslistSql("num1", "where Commission.state=1 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else if(key==2){
//				sql=getFriendslistSql("num1", "where Commission.state=2 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else{
//				sql=getFriendslistSql("num1", "where Commission.state=3 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}
//			break;
//		case 0:
//			if(key==1){
//				sql=getFriendslistSql("numsu2", "where Commission.state=1 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else if(key==2){
//				sql=getFriendslistSql("numsu2", "where Commission.state=2 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}else{
//				sql=getFriendslistSql("numsu2", "where Commission.state=3 and Commission.memberidsu="+getLogin(uname).getUserid());
//			}
//			break;
//			
//		
//
//		default:
//			return sendFalse("错误");
//		}
//		msql.setPage(page);
//		msql.setRows(rows);
//		msql.setSql(sql);
//		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
//		for (Map<String, Object> map : listmap) {
//			map.put("money", map.get("MONEY"));
//		}
//		
////		RequestType rqt = IdentityFriends(uname, uname, page, rows, type);
//		msql.setSql("select * from (select * from Programme order by id desc )where rownum=1");
//		   
//		 Programme or = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),Programme.class).get(0);
//		   msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//			   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+getShop(uname).getId()
//			   		+" ) and nvl(type,1)=1 order by id desc)"
//			   				+ "where rownum=1"
//			   				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//			   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+getShop(uname).getId()
//			   		+" ) and nvl(type,1)=2 order by id desc)"
//			   				+ "where rownum=1");
//					 List<OrdersRule>   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),OrdersRule.class);
//		 
//		 
//		 msql.setPage(page);
//		msql.setRows(rows);
//		
//		List<Map<String, Object>> listmap =getFriendslist(type, uname, page, rows);
//		List<Map<String, Object>> listmap1 ;
//		for (Map<String, Object> map : listmap) {
//			switch (type) {
//			case 1:
//				if(key==1){
//					if(getMember(uname).getmShop()!=null){
//						//门店店主
//						//经销商贡献未入账
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//								+ " memberidsu ="+map.get("MEMBERID").toString()
//										);
//						
//						BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//								
//						
//						
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=0  and "
//								+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//								);
//						
//						mBigDecimal1=mBigDecimal1.subtract( new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						mBigDecimal1=mBigDecimal1.multiply(or.getShop())
//								.divide(new BigDecimal(100));
//						map.put("money",mBigDecimal1);
//						
//						
//					}else{
//						BigDecimal mBigDecimal1 = new BigDecimal(0);//经销商贡献
//						
//						
//						//1：自己直接推荐
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//								+ " memberidsu ="+getLogin(uname).getUserid()+" and memberid="+map.get("MEMBERID").toString()
//								);
//						
//						mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=0  and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								);
//						mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								+ "left join orders on orders.id=orderid "
//								+ "where status in("+getOrdersStatusTrue()+") and  autosystem=0 and orders.Shippingtype !=3 "
//								+ " and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								+" group by Commoditykeyid,orderid)");
//						
//						mBigDecimal1 =	mBigDecimal1
//						.multiply(or.getSales().add(or.getOnlineshopkeeper()))
//						.divide(new BigDecimal(100));
//						//实得金额
//						mBigDecimal1=mBigDecimal1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						map.put("money",mBigDecimal1 );
//						
//						
//					}
//					
//					
//					
//				}else if(key==2){
////					msql.setSql("select nvl(sum(shop),0)payment from Sharingdetails2 where salesmemberid="+map.get("MEMBERID") );
////					listmap1=mMemberService.exeSelectSql(msql);
////					map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString()).divide(new BigDecimal(100)));
//					
//
//					if(getMember(uname).getmShop()!=null){
//						//门店店主
//						//经销商贡献未入账
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberidsu ="+map.get("MEMBERID").toString()
//										);
//						
//						BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//								
//						
//						
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+")  and orders.Shippingtype !=3 and autosystem=1  and "
//								+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//								);
//						
//						mBigDecimal1=mBigDecimal1.subtract( new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						mBigDecimal1=mBigDecimal1.multiply(or.getShop())
//								.divide(new BigDecimal(100));
//						map.put("money",mBigDecimal1);
//						
//						
//					}else{
//						BigDecimal mBigDecimal1 = new BigDecimal(0);//经销商贡献
//						
//						
//						//1：自己直接推荐
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberidsu ="+getLogin(uname).getUserid()+" and memberid="+map.get("MEMBERID").toString()
//								);
//						
//						mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+")  and orders.Shippingtype !=3 and autosystem=1  and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								);
//						mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								+ "left join orders on orders.id=orderid "
//								+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1 and orders.Shippingtype !=3 "
//								+ " and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								+" group by Commoditykeyid,orderid)");
//						
//						mBigDecimal1 =	mBigDecimal1
//						.multiply(or.getSales().add(or.getOnlineshopkeeper()))
//						.divide(new BigDecimal(100));
//						//实得金额
//						mBigDecimal1=mBigDecimal1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						map.put("money",mBigDecimal1 );
//						
//						
//					}
//					
//					
//					
//				
//				}else{
////					msql.setSql("select nvl(sum(shop),0)payment from Sharingdetails2 where salesmemberid="+map.get("MEMBERID") +" and state=1");
////					listmap1=mMemberService.exeSelectSql(msql);
////					map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString()).divide(new BigDecimal(100)));
//
//
//					if(getMember(uname).getmShop()!=null){
//						//门店店主
//						//经销商贡献未入账
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberidsu ="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//										);
//						
//						BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//								
//						
//						
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+")  and orders.Shippingtype !=3 and autosystem=1  and "
//								+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								);
//						
//						mBigDecimal1=mBigDecimal1.subtract( new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						mBigDecimal1=mBigDecimal1.multiply(or.getShop())
//								.divide(new BigDecimal(100));
//						map.put("money",mBigDecimal1);
//						
//						
//					}else{
//						BigDecimal mBigDecimal1 = new BigDecimal(0);//经销商贡献
//						
//						
//						//1：自己直接推荐
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberidsu ="+getLogin(uname).getUserid()+" and memberid="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								);
//						
//						mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+")  and orders.Shippingtype !=3 and autosystem=1  and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								);
//						mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								+ "left join orders on orders.id=orderid "
//								+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1  and orders.Shippingtype !=3 "
//								+ " and "
//								+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								+" group by Commoditykeyid,orderid)");
//						
//						mBigDecimal1 =	mBigDecimal1
//						.multiply(or.getSales().add(or.getOnlineshopkeeper()))
//						.divide(new BigDecimal(100));
//						//实得金额
//						mBigDecimal1=mBigDecimal1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						map.put("money",mBigDecimal1 );
//						
//						
//					}
//					
//					
//					
//				
//				
//					
//				}
//				break;
//			case 2:
//				if(key==1){
//					msql.setSql("select id from Identity where type=1 and memberid="+getLogin(uname).getUserid()
//							);
//					int a =mMemberService.exeSelectSql(msql).size();
//					//单品减9，
//					if(a>0||getMember(uname).getmShop()!=null){
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=0  and "
//							+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//							);
//					}else{
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=0  and "
//								+ " orders.memberid ="+map.get("MEMBERID").toString()
//								);
//					}
//				BigDecimal	m1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString());
//				
//					BigDecimal mBigDecimal2 ;
//					if(a>0||getMember(uname).getmShop()!=null){
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//								+ " memberidsu ="+map.get("MEMBERID").toString() 
//								);
//					}else{
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//								+ " memberid ="+map.get("MEMBERID").toString() 
//								);
//					}
//					
//					if(getMember(uname).getmShop()==null){
//						
//						mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						
//						if(a>0){
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getSales())
//									.divide(new BigDecimal(100));
//						}else{
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getOnlineshopkeeper())
//									.divide(new BigDecimal(100));
//						}
//						
//						if(a==0){
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=0 and orders.Shippingtype !=3 "
//									+ " and "
//									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//							
//							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}
//						
//						
//						
//					}else{
//						msql.setSql("select id from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid="+map.get("MEMBERID").toString());
//						if(mMemberService.exeSelectSql(msql).size()!=0){
//							msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//									+ " memberidsu ="+map.get("MEMBERID").toString() 
//									);
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//										.multiply(or.getShop().add(or.getSales()))
//										.divide(new BigDecimal(100));
//							//导购拼单+4
////							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
////									+ "left join orders on orders.id=orderid "
////									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=0 and orders.Shippingtype !=3 "
////									+ " and "
////									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
////									+" group by Commoditykeyid,orderid)");
////							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}else{
//							msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
//									+ " memberidsu ="+map.get("MEMBERID").toString() 
//									);
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getShop())
//									.divide(new BigDecimal(100));
//						}
//					
//					}
//					map.put("money", mBigDecimal2);
////					//线上店主贡献未入账，1：自己直接推荐
////					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
////							+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getShop(uname).getId()
////							+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
////							);
////					
////					BigDecimal mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString())
////							.multiply(or.getShop().add(or.getSales()))
////							.divide(new BigDecimal(100));
////					//线上店主贡献未入账，2：经销商推荐
////					msql.setSql("select nvl(sum(payment),0) payment  from orders "
////							+ "where status in("+getOrdersStatusTrue()+")  and autosystem=0  and "
////							+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getShop(uname).getId()
////							+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
////									);
////					BigDecimal mBigDecimal3 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString())
////							.multiply(or.getSales())
////							.divide(new BigDecimal(100));
////					
////					map.put("money", mBigDecimal2.add(mBigDecimal3));
//					
////					msql.setSql("select nvl(sum(payment),0)payment from orders where memberidsu="+map.get("MEMBERID")
////							+ " and shopid="+getShop(uname).getId());
////					
////					listmap1=mMemberService.exeSelectSql(msql);
////					map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString()).multiply(or.getShop()).divide(new BigDecimal(100)));
//				}else if(key==2){
//					msql.setSql("select id from Identity where type=1 and memberid="+getLogin(uname).getUserid()
//							);
//					int a =mMemberService.exeSelectSql(msql).size();
//					//单品减9，
//					if(a>0||getMember(uname).getmShop()!=null){
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=1  and "
//							+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//							);
//					}else{
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and autosystem=1  and "
//								+ " orders.memberid ="+map.get("MEMBERID").toString()
//								);
//					}
//				BigDecimal	m1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString());
//				
//					BigDecimal mBigDecimal2 ;
//					if(a>0||getMember(uname).getmShop()!=null){
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberidsu ="+map.get("MEMBERID").toString() 
//								);
//					}else{
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//								+ " memberid ="+map.get("MEMBERID").toString() 
//								);
//					}
//					
//					if(getMember(uname).getmShop()==null){
//						
//						mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						
//						if(a>0){
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getSales())
//									.divide(new BigDecimal(100));
//						}else{
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getOnlineshopkeeper())
//									.divide(new BigDecimal(100));
//						}
//						
//						if(a==0){
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1  "
//									+ " and "
//									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//							
//							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}
//						
//						
//						
//					}else{
//						msql.setSql("select id from Identity where suid=(select id from Identity where memberid="+map.get("MEMBERID").toString()+")");
//						if(mMemberService.exeSelectSql(msql).size()!=0){
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//										.multiply(or.getShop().add(or.getSales()))
//										.divide(new BigDecimal(100));
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1  "
//									+ " and "
//									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}else{
//							msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  and "
//									+ " memberidsu ="+map.get("MEMBERID").toString() 
//									);
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							
//							msql.setSql("select id from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid="+map.get("MEMBERID").toString());
//							if(mMemberService.exeSelectSql(msql).size()==0){
//								mBigDecimal2 = mBigDecimal2.subtract(m1)
//										.multiply(or.getShop())
//										.divide(new BigDecimal(100));
//							}else{
//								mBigDecimal2 = mBigDecimal2.subtract(m1)
//										.multiply(or.getShop().add(or.getSales()))
//										.divide(new BigDecimal(100));
//							}
//							
//						}
//					
//					}
//					map.put("money", mBigDecimal2);
//				}else{
//					msql.setSql("select id from Identity where type=1 and memberid="+getLogin(uname).getUserid()
//							);
//					int a =mMemberService.exeSelectSql(msql).size();
//					//单品减9，
//					if(a>0||getMember(uname).getmShop()!=null){
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ " and autosystem=1  and "
//							+ " orders.memberidsu ="+map.get("MEMBERID").toString()
//							);
//					}else{
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//								+ "where status in("+getOrdersStatusTrue()+") "
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								+ " and autosystem=1  and "
//								+ " orders.memberid ="+map.get("MEMBERID").toString()
//								);
//					}
//				BigDecimal	m1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString());
//				
//					BigDecimal mBigDecimal2 ;
//					if(a>0||getMember(uname).getmShop()!=null){
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  "
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								+ "and "
//								+ " memberidsu ="+map.get("MEMBERID").toString() 
//								);
//					}else{
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  "
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								+ "and "
//								+ " memberid ="+map.get("MEMBERID").toString() 
//								);
//					}
//					
//					if(getMember(uname).getmShop()==null){
//						
//						mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						
//						if(a>0){
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getSales())
//									.divide(new BigDecimal(100));
//						}else{
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getOnlineshopkeeper())
//									.divide(new BigDecimal(100));
//						}
//						
//						if(a==0){
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1  "
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ " and "
//									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//							
//							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}
//						
//						
//						
//					}else{
//						msql.setSql("select id from Identity where suid=(select id from Identity where memberid="+map.get("MEMBERID").toString()+")");
//						if(mMemberService.exeSelectSql(msql).size()!=0){
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//										.multiply(or.getShop().add(or.getSales()))
//										.divide(new BigDecimal(100));
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1  "
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ " and "
//									+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and orders.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//							mBigDecimal2=mBigDecimal2.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}else{
//							msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+")  and autosystem=1  "
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ "and "
//									+ " memberidsu ="+map.get("MEMBERID").toString() 
//									);
//							mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							mBigDecimal2 = mBigDecimal2.subtract(m1)
//									.multiply(or.getShop())
//									.divide(new BigDecimal(100));
//						}
//					
//					}
//					map.put("money", mBigDecimal2);
//				}
//				break;
//			case 3:
//			case 0:
//				if(key==1){
//					if(getMember(uname).getmShop()==null){
//						 
//						 //百分之2
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where status in("+getOrdersStatusTrue()+") and memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype=3 and autosystem=0 and ORDERS.memberid="+map.get("MEMBERID").toString());
//						 listmap1=mMemberService.exeSelectSql(msql);
//						 
//						map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//							.multiply(lOrdersRule.get(1).getClerk()).divide(new BigDecimal(100)));
//						
//						
//						 //百分之10
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where status in("+getOrdersStatusTrue()+") and  memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype !=3 and autosystem=0 and ORDERS.memberid="+map.get("MEMBERID").toString());
//						 BigDecimal b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						 
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and  autosystem=0  and "
//								+ " memberidsu  ="+getLogin(uname).getUserid()+" and ORDERS.memberid="+map.get("MEMBERID").toString());
//						
//						 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//						 
//						 b=b.multiply(lOrdersRule.get(0).getClerk().divide(new BigDecimal(100)));
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+")  and autosystem=0 and orders.Shippingtype !=3 "
//									+ " and memberidsu = "+getLogin(uname).getUserid()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//									+" and ORDERS.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//						
//						b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						 
//						 
//						map.put("money", new BigDecimal(map.get("money").toString())
//								.add(b));
//						 
//					}else{
//						
//						msql.setSql("select id from clerk where memberid="+map.get("MEMBERID").toString());
//						if(!uname.equals(map.get("UNAME").toString())&& mMemberService.exeSelectSql(msql).size()>0){
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where status in("+getOrdersStatusTrue()+") and  shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and autosystem=0 and ORDERS.memberidsu="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop())
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where status in("+getOrdersStatusTrue()+") and  shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and autosystem=0 and ORDERS.memberidsu ="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and orders.Shippingtype !=3 and  autosystem=0 and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +"  and autosystem=0 and ORDERS.memberidsu="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().divide(new BigDecimal(100)));
//							
//
//							 
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}else{
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and autosystem=0 and status in("+getOrdersStatusTrue()+") and ORDERS.memberid="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop().add(lOrdersRule.get(1).getClerk()))
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where  status in("+getOrdersStatusTrue()+") and shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and autosystem=0 and ORDERS.memberid ="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+")   and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +" and orders.Shippingtype !=3 and autosystem=0 and ORDERS.memberid="+map.get("MEMBERID").toString()+" and autosystem=0 ");
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().add(lOrdersRule.get(0).getClerk()).divide(new BigDecimal(100)));
//							 
//							 
//								//店铺直推荐人拼单+4
//								msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//											+ "left join orders on orders.id=orderid "
//											+ "where status in("+getOrdersStatusTrue()+")  and autosystem=0 and orders.Shippingtype !=3  "
//											+ " and orders.memberid = "+map.get("MEMBERID").toString()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//											
//											+" group by Commoditykeyid,orderid)");
//								
//								 b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}
//						
//						 
//						 
//					 
//					}
//					  
//					
//					 
//					 
//					 
//					 
//					
//					   
//				}else if(key==2){
////					msql.setSql("select nvl(sum(shop),0)payment from Sharingdetails where memberid="+map.get("MEMBERID") );
////					listmap1=mMemberService.exeSelectSql(msql);
////					map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString()).divide(new BigDecimal(100)));
//
//					if(getMember(uname).getmShop()==null){
//						 
//						 //百分之2
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype=3 and autosystem=1 and status in("+getOrdersStatusTrue()+") and ORDERS.memberid="+map.get("MEMBERID").toString());
//						 listmap1=mMemberService.exeSelectSql(msql);
//						 
//						map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//							.multiply(lOrdersRule.get(1).getClerk()).divide(new BigDecimal(100)));
//						
//						
//						 //百分之10
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype !=3 and autosystem=1 and status in("+getOrdersStatusTrue()+") and ORDERS.memberid="+map.get("MEMBERID").toString());
//						 BigDecimal b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						 
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//								+ " memberidsu  ="+getLogin(uname).getUserid()+" and orders.Shippingtype !=3 and ORDERS.memberid="+map.get("MEMBERID").toString());
//						
//						 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//						 
//						 b=b.multiply(lOrdersRule.get(0).getClerk().divide(new BigDecimal(100)));
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+")  and autosystem=1  and orders.Shippingtype !=3 "
//									+ " and memberidsu = "+getLogin(uname).getUserid()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//									+" and ORDERS.memberid="+map.get("MEMBERID").toString()
//									+" group by Commoditykeyid,orderid)");
//						
//						b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						 
//						 
//						map.put("money", new BigDecimal(map.get("money").toString())
//								.add(b));
//						 
//					}else{
//						
//						msql.setSql("select id from clerk where memberid="+map.get("MEMBERID").toString());
//						if(!uname.equals(map.get("UNAME").toString())&& mMemberService.exeSelectSql(msql).size()>0){
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and autosystem=1 and status in("+getOrdersStatusTrue()+") and ORDERS.memberidsu="+map.get("MEMBERID").toString());
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 msql.setPage(null);
//							 msql.setRows(null);
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop())
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberidsu ="+map.get("MEMBERID").toString());
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +"  and orders.Shippingtype !=3 and autosystem=1 and ORDERS.memberidsu="+map.get("MEMBERID").toString());
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().divide(new BigDecimal(100)));
//							
//
//							 
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}else{
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString());
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop().add(lOrdersRule.get(1).getClerk()))
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberid ="+map.get("MEMBERID").toString());
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+")   and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +" and orders.Shippingtype !=3 and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString() );
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().add(lOrdersRule.get(0).getClerk()).divide(new BigDecimal(100)));
//							 
//							 
//								//店铺直推荐人拼单+4
//								msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//											+ "left join orders on orders.id=orderid "
//											+ "where status in("+getOrdersStatusTrue()+")  and autosystem=1 and orders.Shippingtype !=3  "
//											+ " and orders.memberid = "+map.get("MEMBERID").toString()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//											
//											+" group by Commoditykeyid,orderid)");
//								
//								 b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}
//						
//						 
//						 
//					 
//					}
//					  
//					
//					   
//				
//				}else{
////					msql.setSql("select nvl(sum(shop),0)payment from Sharingdetails where memberid="+map.get("MEMBERID") +" and state=1");
////					listmap1=mMemberService.exeSelectSql(msql);
////					map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString()).divide(new BigDecimal(100)));
////					
//
//
//					if(getMember(uname).getmShop()==null){
//						 
//						 //百分之2
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype=3 and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString()
//							    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//							    );
//						 listmap1=mMemberService.exeSelectSql(msql);
//						 
//						map.put("money", new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//							.multiply(lOrdersRule.get(1).getClerk()).divide(new BigDecimal(100)));
//						
//						
//						 //百分之10
//						 msql.setSql("select nvl(sum(payment),0)payment from orders where memberidsu ="+getLogin(uname).getUserid() 
//							    + " and Shippingtype !=3 and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString()
//							    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//							    );
//						 BigDecimal b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						 
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//								+ " memberidsu  ="+getLogin(uname).getUserid()+" and orders.Shippingtype !=3 and ORDERS.memberid="+map.get("MEMBERID").toString()
//								+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//								+ "union all "
//								+ "select ordersid from Sharingdetails2 where state=1 )"
//								);
//						
//						 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//						 
//						 b=b.multiply(lOrdersRule.get(0).getClerk().divide(new BigDecimal(100)));
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+")  and autosystem=1   "
//									+ " and memberidsu = "+getLogin(uname).getUserid()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//									+" and ORDERS.memberid="+map.get("MEMBERID").toString()
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+" group by Commoditykeyid,orderid)");
//						
//						b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						 
//						 
//						map.put("money", new BigDecimal(map.get("money").toString())
//								.add(b));
//						 
//					}else{
//						
//						msql.setSql("select id from clerk where memberid="+map.get("MEMBERID").toString());
//						if(!uname.equals(map.get("UNAME").toString())&& mMemberService.exeSelectSql(msql).size()>0){
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberidsu="+map.get("MEMBERID").toString()
//								    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )");
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop())
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberidsu ="+map.get("MEMBERID").toString()
//								    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									 );
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+")   and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +" and orders.Shippingtype !=3 and autosystem=1 and ORDERS.memberidsu="+map.get("MEMBERID").toString()
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									);
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().divide(new BigDecimal(100)));
//							
//
//							 
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}else{
//							 //百分之2
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype=3  and status in("+getOrdersStatusTrue()+") and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString()
//								    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									 );
//							 listmap1=mMemberService.exeSelectSql(msql);
//							 
//							 BigDecimal b ;
//							 
//							map.put("money", 
//									new BigDecimal(listmap1.get(0).get("PAYMENT").toString())
//									.multiply(lOrdersRule.get(1).getShop().add(lOrdersRule.get(1).getClerk()))
//									.divide(new BigDecimal(100)));
//							
//							
//							 //百分之10
//							 msql.setSql("select nvl(sum(payment),0)payment from orders where shopid ="+getMember(uname).getmShop().getId() 
//								    + " and Shippingtype !=3  and autosystem=1 and status in("+getOrdersStatusTrue()+") and ORDERS.memberid ="+map.get("MEMBERID").toString()
//								    +" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									 );
//							  b = new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							 
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) PAYMENT  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+")   and "
//									+ " orders.shopid  ="+getMember(uname).getmShop().getId() +" and orders.Shippingtype !=3 and autosystem=1 and ORDERS.memberid="+map.get("MEMBERID").toString() 
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )");
//							
//							 b=b.subtract(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
//							 b=b.multiply(lOrdersRule.get(0).getShop().add(lOrdersRule.get(0).getClerk()).divide(new BigDecimal(100)));
//							 
//							 
//								//店铺直推荐人拼单+4
//								msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//											+ "left join orders on orders.id=orderid "
//											+ "where status in("+getOrdersStatusTrue()+")  and autosystem=1 and orders.Shippingtype !=3  "
//											+ " and orders.memberid = "+map.get("MEMBERID").toString()+" and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//											+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//											+ "union all "
//											+ "select ordersid from Sharingdetails2 where state=1 )"
//											+" group by Commoditykeyid,orderid)");
//								
//								 b=b.add(new BigDecimal( mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							
//							map.put("money", new BigDecimal(map.get("money").toString())
//									.add(b));
//							 
//						}
//						
//						 
//						 
//					 
//					}
//					  
//					
//					   
//				
//				
//				
//				}
//				
//				break;
//			
//
//			default:
//				return sendFalse("错误");
//			}
//			
//		}
//		
//	
		return sendTrueData(listmap);
		
	}
	/**
	 *查询某个经销商或者线上店主归属店铺（手机端返回自己的数据）
	 * */
	@Auth
	@RequestMapping(value ="/Identity/selectsu", method = RequestMethod.POST) 
	public RequestType Identityselectsu(String phone,String uname) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select "+MyDate.orcaleCDATE("Identity.id") +",shopname from Identity left join "
				+ "shop on shopid=shop.id ");
		if(getMember(uname).getSuperadmin()==1){
			if(!Stringutil.isBlank(phone)){
				msql.setSql(msql.getSql()+" where Identity.memberid="+getLogin(phone).getUserid());
			}
		}else{
			msql.setSql(msql.getSql()+" where Identity.memberid="+getLogin(uname).getUserid());
		}
		
		return sendTrueData(mIdentityService.exeSelectSql(msql));
		
	}
	/**
	 *经销商或线上店主停用
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Identity/Stop", method = RequestMethod.POST) 
	public RequestType IdentityStop(Long id,Integer type) throws Exception{
		if(id==null)
			return sendFalse("编号不可为空");
		Identity mIdentity = new Identity();
		if(type==null)type=1;
		if(type!=1&&type!=2&&type!=-1)return sendFalse("状态错误");
		mIdentity.setId(id);
		mIdentity.setSystem(type);
		
		return sendTrueMsg(mIdentityService.updateBySelect(mIdentity));
		
	}
	/**
	 *结束经销商或线上店主
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Identity/end", method = RequestMethod.POST) 
	public RequestType Identityend(Long id) throws Exception{
		if(id==null)
			return sendFalse("编号不可为空");
		Identity mIdentity = new Identity();
		mIdentity.setId(id);
		mIdentity.setEnd(System.currentTimeMillis());
		
		return sendTrueMsg(mIdentityService.updateBySelect(mIdentity));
		
	}
	/**
	 *取消经销商或线上店主身份
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value ="/Identity/Close", method = RequestMethod.POST) 
	public RequestType IdentityClose(Long id,String phone) throws Exception{
		if(id==null)
			return sendFalse("编号不可为空");
		
		Identity mIdentity = mIdentityService.getById(id,Identity.class);
		if(mIdentity==null)
			return sendFalse("编号不存在");
		Sql msql = new Sql();
		msql.setSql("select id from Friends where memberida="+mIdentity.getMemberid());
		if(mIdentityService.exeSelectSql(msql).size()!=0)
			return sendFalse("错误，该用户下面的人员没有全部转移");
		
		msql.setSql("delete Identity where memberid="+mIdentity.getMemberid());
		mIdentityService.execSQL(msql);
		
//		if(Stringutil.isBlank(phone))
//			msql.setSql("update Friends set memberida=1539759995877 where memberidb="+mIdentity.getMemberid());
//		else
//			msql.setSql("update Friends set memberida="+getLogin(phone).getUserid()+" where memberidb="+mIdentity.getMemberid());
		
//		mIdentityService.execSQL(msql);
		
		msql.setSql("delete clerk where memberid="+mIdentity.getMemberid());
		
		
		return sendTrueMsg(mIdentityService.execSQL(msql));
		
	}
	
	
//	private String getselectordersSql(int key,String s){
//		Long time=1538323200000L;//2018年10月1日0点0分0秒
//		String sa;
//		if(key==1)
//			sa="select payment,memberidsu,orders.id,sum(num) num,uname,"+OrdersController.getordertype("orders.status", "status")+",orders.Shippingtype from orders "
//			+ "left join Orderrelevance on orders.id=orderid left join member on orders.memberid=member.id"
//			+ " where "+s+" and autosystem=0 and orders.id>"+time+" and orders.status in("+getOrdersStatusTrue()+") group by  "
//					+ " payment,memberidsu,orders.id,uname,status,orders.Shippingtype";
//		else if(key==2)
//			sa=
//			"select payment,memberidsu,orders.id,sum(num) num,uname,"+OrdersController.getordertype("orders.status", "status")+
//			",orders.Shippingtype from orders "
//			+ "left join Orderrelevance on orders.id=orderid left join member on orders.memberid=member.id"
//			+ " where "+s+" and autosystem=1 and  orders.id>"+time+" and orders.status in("+getOrdersStatusTrue()+") group by  "
//					+ " payment,memberidsu,orders.id,uname,status,orders.Shippingtype";
//		else
//			sa="select payment,memberidsu,orders.id,sum(num) num,uname,"+OrdersController.getordertype("orders.status", "status")+",orders.Shippingtype from orders "
//					+ "left join Orderrelevance on orders.id=orderid left join member on orders.memberid=member.id"
//					+ " where "+s
//					+" and autosystem=1 and orders.status in("+getOrdersStatusTrue()+") and orders.id>"+time+" and orders.id  in (select ordersid from Sharingdetails where state=1"
//					+ " union all select ordersid from Sharingdetails2 where state=1 ) group by   payment,memberidsu,orders.id,uname,status,orders.Shippingtype";
//		
//		return sa;
//	}
	private String getselectordersSql(int key,int type,Long memberid,Long memberidsu,Long ordersid) throws Exception {
//		type=0;
//		if(getMember(memberidsu).getmShop()!=null){
//			type=4;
//		}else{
//			try {
//				Sql msql = new Sql();
//				msql.setSql("select type from Identity where memberid="+memberidsu);
//				type=Integer.parseInt(mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString());
//				if(type==0)
//					type=4;
//				else if(type==1)
//					type=3;
//			} catch (Exception e) {
//				type=1;
//			}
//			
//				
//			
//		}
//		
		String sa;
		String sql;
		if(getMember(memberidsu).getmShop()!=null){
			if(type==1){
				sa=" and num2=0 and num3=0 ";
			}else if(type==2){
				sa="  and num3!=0";
			}else{
				sa=" and num2!=0 and num3=0";
			}
			 sql = "select * from (select ordersnum PAYMENT,my_orders_effective("+memberid+",memberid1,memberid2,memberid3,memberid4) typea,"
						+ "UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
						+" num4 money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
								+ "left join member on member.id=Commission.memberid where   Commission.state="+key+sa+") where money>0 and typea >0";	
			
		}else{
			 sql = "select * from (select ordersnum PAYMENT,my_orders_effective("+memberidsu+",memberid1,memberid2,memberid3,memberid4) typea,"
						+ "UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
						+" my_to_app_je(6,"+memberidsu+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4) money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
								+ "left join member on member.id=Commission.memberid where   Commission.state="+key+") where money>0 and typea >0";	
		}
		
	
		
		
//		int a =key;
////		key=type;
////		type=a;
//		String str;
//				
//				String sql = null;		
//		Sql msql = new Sql();
//		msql.setSql("select type from Identity where  memberid="+memberidsu);
//		 List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
//		 if(listmap.size()==0){
//			 sql = "select ordersnum PAYMENT,UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
//						+"num1 money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
//								+ "left join member on member.id=Commission.memberid where Commission.memberid1="+memberidsu+" and Commission.state="+key;	
//			 
//		 }else{
//			  a = Integer.parseInt(listmap.get(0).get("TYPE").toString());
//			  if(a==2){
//				  
//				  
//					sql = "select ordersnum PAYMENT,UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
//							+" num2 money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
//									+ "left join member on member.id=Commission.memberid where Commission.memberid2="+memberidsu+" and Commission.state="+key;	
//			 
//			  
//			  }else if(a==1){
//				  
////				  if(type==1){
////					  str=memberidsu+" and num2=0 ";
////				  }else{
////					
////					 str=memberidsu+" and num2!=0 ";
////				  }
//				  str=memberidsu+" ";
//					sql = "select ordersnum PAYMENT,UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
//							+" num3 money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
//									+ "left join member on member.id=Commission.memberid where Commission.memberid3="+str+"   and Commission.state="+key;	
//				
//			  }
//		 }
//		 if(getMember(memberidsu).getmShop()!=null){
//			 if(type==1){
//				 str=memberidsu+" and num3!=0 and ";
//			 }else if(type==2){
//				 str=memberidsu+" and num3=0 and ";
//			 }else{
//				 str=memberidsu+" and num3=0 and num2=0 and  ";
//			 }
//			 
//				sql = "select ordersnum PAYMENT,UNAME,"+OrdersController.getordertype("orders.status", "STATUS")+","
//						+" num4 money,Commission.MEMBERIDSU,SHIPPINGTYPE,orders.id,commoditynum NUM from Commission left join orders on orders.id=ordersid "
//								+ "left join member on member.id=Commission.memberid where Commission.memberid4="+str+"  Commission.state="+key;	
//			
//		 }
				
				
		 if(ordersid!=null)
			 sql=sql+" and id="+ordersid;
		
		return sql;
	}

	/**
	 *查询某个用户的订单，返回所有节点的分成信息，手机端自动判断身份信息
	 *1:经销商，2：线上店主，3:导购，0：店主
	 * */
	@Auth
	@Transactional
	@RequestMapping(value ="/Identity/selectorders", method = RequestMethod.POST) 
	public RequestType Identityselectorders(Long ordersid,Integer type,Long id,String phone,String uname,Integer page,Integer rows,Integer key) throws Exception{
		
		if(id==null&&Stringutil.isBlank(phone))
			return sendFalse("编号和账号最少选一个");
		
//		Long time=1538323200000L;//2018年10月1日0点0分0秒
		MemberAuths mMember;
		if(id!=null)
			mMember=getMember(id);
		else
			mMember=getMember(phone);
		
		
		Sql msql = new Sql();
		msql.setSql(getselectordersSql(key, type, mMember.getId(),getLogin(uname).getUserid(),ordersid));
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> listmap =	mMemberService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			map.put("money", map.get("MONEY"));
			msql.setSql("select id from orders where id="+map.get("ID"));
			if(mMemberService.exeSelectSql(msql).size()==0){
				msql.setSql("select id from Coupon where id="+map.get("ID"));
				if(mMemberService.exeSelectSql(msql).size()==0){
					map.put("ID","核销反佣金");
				}else{
					map.put("ID", "团建基金");
				}
			}
		}
//
//		msql.setSql("select * from (select * from Programme order by id desc )where rownum=1");
//		Programme or = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),Programme.class).get(0);
//		
//		
//		
//		msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//				+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+getShop(uname).getId()
//				+" ) and nvl(type,1)=1 order by id desc)"
//				+ "where rownum=1"
//				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//				+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+getShop(uname).getId()
//				+" ) and nvl(type,1)=2 order by id desc)"
//				+ "where rownum=1");
//		List<OrdersRule>   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),OrdersRule.class);
//		
//		
//		List<Map<String, Object>> listmap;
//		if(type==1){
//			
//			msql.setSql("select * from Friends where memberidb="+mMember.getId());
//			Friends mFriends = IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0), Friends.class);
//			if((!mMember.getId().equals(getLogin(uname).getUserid()))&& mFriends.getMemberida().equals(mFriends.getMemberidb())){//线上店主
//				
//				String s = " memberidsu="+mFriends.getMemberidb();
//				
//				msql.setSql(getselectordersSql(key, s));
//				
//				if(ordersid!=null)
//					msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//				
//				msql.setPage(page);
//				msql.setRows(rows);
//				listmap = mMemberService.exeSelectSql(msql);
//				msql.setPage(null);
//				msql.setRows(null);
//				
//				for (Map<String, Object> mmap : listmap) {
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//					
//					BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//					b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					
//					mmap.put("money", b1.multiply(or.getSales()).divide(new BigDecimal(100)));
//				
//				}
//	
//		}else{
//			//经销商直接推荐的用户
//
//			String s = "orders. memberid="+mFriends.getMemberidb();
//			
//			msql.setSql(getselectordersSql(key, s));
//			
//			if(ordersid!=null)
//				msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//			
//			msql.setPage(page);
//			msql.setRows(rows);
//			listmap = mMemberService.exeSelectSql(msql);
//			msql.setPage(null);
//			msql.setRows(null);
//			
//			for (Map<String, Object> mmap : listmap) {
//				//单品减9，
//				msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//				
//				BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//				b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				
//
//				b1=b1.multiply(or.getSales().add(or.getOnlineshopkeeper())).divide(new BigDecimal(100));
//				//导购拼单+4
//				msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//						+ "left join orders on orders.id=orderid "
//						+ "where orderid="+mmap.get("ID").toString() +" and orders.Shippingtype !=3 group by Commoditykeyid,orderid)"
//								);
//				
//				b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			
//				mmap.put("money",b1 );
//			
//				}
//			}
//				
//		}else if(type==2){
//			//经销商直接推荐的用户
//			
//				msql.setSql("select id from Identity where type=1 and memberid="+getLogin(uname).getUserid());
//				int a = mMemberService.exeSelectSql(msql).size();
//				
//				if(getMember(uname).getmShop()!=null||a>0){
//					String s = " memberidsu="+mMember.getId();
//					
//					msql.setSql(getselectordersSql(key, s));
//					
//					if(ordersid!=null)
//						msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//					
//					msql.setPage(page);
//					msql.setRows(rows);
//					listmap = mMemberService.exeSelectSql(msql);
//					msql.setPage(null);
//					msql.setRows(null);
//					
//					for (Map<String, Object> mmap : listmap) {
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//						
//						BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//						b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						
//						if(getMember(uname).getmShop()!=null){
//							msql.setSql("select id from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+" )and memberid="+mMember.getId());
//							if(mMemberService.exeSelectSql(msql).size()>0)
//								b1=b1.multiply(or.getShop().add(or.getSales())).divide(new BigDecimal(100));
//							else
//								b1=b1.multiply(or.getShop()).divide(new BigDecimal(100));
//						}else{
//							b1=b1.multiply(or.getSales()).divide(new BigDecimal(100));
//						}
//						msql.setSql("select id from orders where id="+mmap.get("ID").toString()+" and memberidsu="+getLogin(uname).getUserid());
//						if(mMemberService.exeSelectSql(msql).size()>0){
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where orderid="+mmap.get("ID").toString() +" and orders.Shippingtype !=3 group by Commoditykeyid,orderid)"
//											);
//							
//							b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						}
//					
//						mmap.put("money",b1 );
//					
//						}
//			}else{
//
//				String s = " orders.memberid="+mMember.getId();
//				
//				msql.setSql(getselectordersSql(key, s));
//				
//				if(ordersid!=null)
//					msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//				
//				msql.setPage(page);
//				msql.setRows(rows);
//				listmap = mMemberService.exeSelectSql(msql);
//				msql.setPage(null);
//				msql.setRows(null);
//				
//				for (Map<String, Object> mmap : listmap) {
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//					
//					BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//					b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//
//					b1=b1.multiply(or.getOnlineshopkeeper()).divide(new BigDecimal(100));
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where orderid="+mmap.get("ID").toString() +" and orders.Shippingtype !=3 group by Commoditykeyid,orderid)"
//									);
//					
//					b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				
//					
//				
//					mmap.put("money",b1 );
//				
//					}	
//			}
//			
//			
//			
//		}else if(type==3){
//			//导购身份
//			String s  = " orders.memberid="+mMember.getId();
////			msql.setSql("select id from clerk where memberid="+mMember.getId());
////			if(mMemberService.exeSelectSql(msql).size()!=0){
////				 s = " memberidsu="+mMember.getId()+"";
////			}else{
////				 s = " orders.memberid="+mMember.getId();
////			}
//			
//			
//			
//			
//			msql.setSql(getselectordersSql(key, s));
//			
//			if(ordersid!=null)
//				msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//			msql.setPage(page);
//			msql.setRows(rows);
//			listmap = mMemberService.exeSelectSql(msql);
//			msql.setPage(null);
//			msql.setRows(null);
//			for (Map<String, Object> mmap : listmap) {
//				BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//				if(mmap.get("SHIPPINGTYPE").equals("3")){
//					b1=b1.multiply(lOrdersRule.get(1).getClerk().divide(new BigDecimal(100)));
//					mmap.put("money",b1 );
//				}else{
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//					b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//
//					b1=b1.multiply(lOrdersRule.get(0).getClerk().divide(new BigDecimal(100)));
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where orderid="+mmap.get("ID").toString() +" and orders.Shippingtype !=3 group by Commoditykeyid,orderid)"
//									);
//					
//					b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					mmap.put("money",b1 );
//				
//				}
//			}
//		}else{
//			
//			//店主
//			msql.setSql("select type from Identity where memberid="+mMember.getId()+" and memberid!="+getLogin(uname).getUserid() );
//			listmap = mMemberService.exeSelectSql(msql);
//			if(listmap.size()==0){
//				msql.setSql("select * from Friends where memberidb="+mMember.getId());
//				Friends mFriends = IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0), Friends.class);
//				if(mFriends.getMemberida().equals(mFriends.getMemberidb())&&!mFriends.getMemberida().equals(getLogin(uname).getUserid())){//导购
//					String s = " memberidsu="+mMember.getId();
//					msql.setSql(getselectordersSql(key, s));
//					
//					msql.setPage(page);
//					msql.setRows(rows);
//					listmap = mMemberService.exeSelectSql(msql);
//					msql.setPage(null);
//					msql.setRows(null);
//					
//					for (Map<String, Object> mmap : listmap) {
//						BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//						if(mmap.get("SHIPPINGTYPE").equals("3")){
//							b1=b1.multiply(lOrdersRule.get(1).getShop().divide(new BigDecimal(100)));
//							mmap.put("money",b1 );
//						}else{
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//							b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//
//							b1=b1.multiply(lOrdersRule.get(0).getShop().divide(new BigDecimal(100)));
//							mmap.put("money",b1 );
//						
//						}
//					}
//					
//				}else{
//					//店铺直接推荐的用户
//					String s = " orders.memberid="+mMember.getId();
//					msql.setSql(getselectordersSql(key, s));
//					
//					msql.setPage(page);
//					msql.setRows(rows);
//					listmap = mMemberService.exeSelectSql(msql);
//					msql.setPage(null);
//					msql.setRows(null);
//					
//					for (Map<String, Object> mmap : listmap) {
//						BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//						if(mmap.get("SHIPPINGTYPE").equals("3")){
//							b1=b1.multiply(lOrdersRule.get(1).getShop()
//									.add(lOrdersRule.get(0).getClerk())
//									.divide(new BigDecimal(100)));
//							mmap.put("money",b1 );
//						}else{
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//							b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//
//							b1=b1.multiply(lOrdersRule.get(0).getShop()
//									.add(lOrdersRule.get(0).getClerk())
//									.divide(new BigDecimal(100)));
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where orderid="+mmap.get("ID").toString() +" and orders.Shippingtype !=3 group by Commoditykeyid,orderid)"
//											);
//							
//							b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							mmap.put("money",b1 );
//						
//						}
//					}
//					
//				
//				}
//			}else{
//				int a =Integer.parseInt(listmap.get(0).get("TYPE").toString());
//				if(a==1){
//					//经销商
//					String s = " memberidsu="+mMember.getId();
//					msql.setSql(getselectordersSql(key, s));
//					
//					if(ordersid!=null)
//						msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//					
//					msql.setPage(page);
//					msql.setRows(rows);
//					listmap = mMemberService.exeSelectSql(msql);
//					msql.setPage(null);
//					msql.setRows(null);
//					
//					for (Map<String, Object> mmap : listmap) {
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//						
//						BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//						b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//
//						b1=b1.multiply(or.getShop()).divide(new BigDecimal(100));
//						mmap.put("money",b1 );
//					
//						}
//					
//				}else{
//					//线上店主
//					String s = " memberidsu="+mMember.getId();
//					msql.setSql(getselectordersSql(key, s));
//					
//					if(ordersid!=null)
//						msql.setSql("select * from ("+msql.getSql()+") where id="+ordersid);
//					
//					msql.setPage(page);
//					msql.setRows(rows);
//					listmap = mMemberService.exeSelectSql(msql);
//					msql.setPage(null);
//					msql.setRows(null);
//					
//					for (Map<String, Object> mmap : listmap) {
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+mmap.get("ID").toString() );
//						
//						BigDecimal b1 = new BigDecimal(mmap.get("PAYMENT").toString());
//						b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						b1=b1.multiply(or.getShop().add(or.getSales())).divide(new BigDecimal(100));
//						mmap.put("money",b1 );
//					
//						}
//					
//				
//				}
//			}
//			
//		}
//	
//		
		return sendTrueData(listmap);
		
	}
	
	
	
		

}
