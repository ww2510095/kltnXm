package com.bm.coupon;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.CommissionTask;
import com.bm.base.BaseController;
import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.MyDate;
import com.bm.clerk.commission.CommissionService;
import com.bm.consumption.envelopes.Envelopes;
import com.bm.consumption.envelopes.EnvelopesService;
import com.bm.orders.OrdersController;
import com.bm.orders.cj.Cj;
import com.bm.orders.cj.CjService;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.shop.Shop;
import com.bm.user.goldcoincoupon.GoldcoincouponService;
import com.bm.user.Member;
import com.bm.user.goldcoin.GoldcoinV;
import com.bm.user.goldcoin.GoldcoinVService;
import com.bm.user.goldcoincoupon.Goldcoincoupon;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "优惠券")
public class CouponController extends BaseController {
	
	@Autowired
	protected  CommissionService mCommissionService;
	@Autowired
	protected  EnvelopesService mEnvelopesService;
	@Autowired
	private GoldcoincouponService mGoldcoincouponService;
	@Autowired
	private GoldcoinVService mValue;
	@Autowired 
	private CjService mCjService;
	
	public static final String Goldcoincouponkey="Goldcoincouponkey";
	/**
	 *积分兑换优惠券规则,具体参见{@link Couponadd}}
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/Goldcoincoupon/add", method = RequestMethod.POST)
	public RequestType Goldcoincoupon(Goldcoincoupon mCoupon,String uname) throws Exception {
		if(mCoupon.getGoldcoinnum()==null)return sendFalse("所需积分不可为空");
		mCoupon.setIstrue(1);
		mCoupon.setStar(System.currentTimeMillis());
		mCoupon.setEnd(System.currentTimeMillis());
		return Couponadd(mCoupon, 1, 0, uname);
		}
	/**
	 * 发放优惠券
	 * size:一次发放数量
	 * outtype：0，注册发放,1:直接发放
	 */
	@Auth(coupon={Administration.ADD})
	@RequestMapping(value = "/Coupon/add", method = RequestMethod.POST)
	public RequestType Couponadd(Goldcoincoupon mCoupon,Integer size,Integer outtype,String uname) throws Exception {
		if(Stringutil.isBlank(mCoupon.getTitle()))return sendFalse("说明不可为空"); //说明
		if(mCoupon.getNumbera()==null)return sendFalse("面值不可为空"); //面值
		if(Stringutil.isBlank(mCoupon.getOnephone()))mCoupon.setOnephone("13333333333");//通用
		if(mCoupon.getNumbermin()==null)mCoupon.setNumbermin(mCoupon.getNumbera());; //满多少使用
		if(mCoupon.getStar()==null)mCoupon.setStar(System.currentTimeMillis());; //开始时间
		if(mCoupon.getEnd()==null)return sendFalse("结束时间不可为空"); //结束时间
		if(mCoupon.getType()==null)mCoupon.setType(1);; //使用类型，1：通用卷，2：互斥卷
		if(mCoupon.getCardtype()==null)return sendFalse("卷类型不可为空"); //卷类型：6,所有商品，1：单品，2：细类，3：小类，4：中类，5：大类
		if(mCoupon.getCardtype()==1&&mCoupon.getKey()==null)return sendFalse("单品类型不可为空"); //单品类型，1：一个商品，2：一款商品
		if(mCoupon.getCardtype()==1&&mCoupon.getKey()!=1&&mCoupon.getKey()!=2)return sendFalse("单品类型错误");
		if(mCoupon.getCardtype()!=6&&Stringutil.isBlank(mCoupon.getData()))return sendFalse("非条码或类名不可为空"); //条码或类名
		if(size==null)size=1;
		//if(size<1)return sendFalse("最少发一张");
		if(outtype!=null&&outtype==0)
			mCoupon.setMemberid(Long.valueOf(size+1));
		else if(mCoupon.getGoldcoinnum()==null){
			if(mCoupon.getMemberid()==null)return sendFalse("发放人不可为空"); //所属人
		}
		
		if("13333333333".equals(mCoupon.getMemberid()+""))
			return sendFalse("不可发放给这个账号"); //所属人
		
		
		
		if(size>9999)
			return sendFalse("单次最多9999张"); //所属人
		
		if(mCoupon.getType()==2)return sendFalse("暂不支持互斥卷");
		mCoupon.setState(0);; //状态，0：未使用，1：已使用
		mCoupon.setMphone(uname);
		
		int key = mCoupon.getKey()==null?0:mCoupon.getKey();
		switch (mCoupon.getCardtype()) {
		case 6:
			break;
		case 1:
			if(key==2)
				mCoupon.setData(auth(mCouponService,key, "youcode", mCoupon.getData()).get(0).get("COMMODITYKEYID").toString());
			else
				mCoupon.setData(auth(mCouponService,key, "youcode", mCoupon.getData()).get(0).get("ID").toString());
			break;
		case 2:
			auth(mCouponService,key, "fineclass", mCoupon.getData());
			break;
		case 3:
			auth(mCouponService,key, "smallclass", mCoupon.getData());
			break;
		case 4:
			auth(mCouponService,key, "inclass", mCoupon.getData());
			break;
		case 5:
			auth(mCouponService,key, "largeclass", mCoupon.getData());
			break;

		default:
			return sendFalse("卷类型错误");
		}
		if(outtype==null||outtype!=0){
			if(mCoupon.getMemberid().toString().length()>10)
				mCoupon.setMemberid(getLogin(mCoupon.getMemberid()+"").getUserid());
		}
			
		
//		if(size!=null){
//			if((mCoupon.getMemberid()+"").length()>5){
//				for (int i =0; i<size ; i++) {
//					mCouponService.add(mCoupon);
//				}
//			}else
		if(mCoupon.getGoldcoinnum()!=null){
			mGoldcoincouponService.add(mCoupon);
		}else{
			mCouponService.add(mCoupon);
		}
				
				
			
//		}
		return sendTrueMsg("发放成功！");
	}
	
	
//	public static  void OutsendCoupon(Long memberid,CouponService mCouponService) throws Exception{
//		Sql msql = new Sql();
//		msql.setSql("select * from Coupon where length(memberid)<5 ");
//		List<Coupon> listCoupon = IBeanUtil.ListMap2ListJavaBean( mCouponService.exeSelectSql(msql), Coupon.class);
//		for (Coupon coupon : listCoupon) {
//			int size =Integer.valueOf(coupon.getMemberid()+"")-1;
//			for (int i=0;i<size;i++) {
//				coupon.setMemberid(memberid);
//				mCouponService.add(coupon);
//			}
//		}
//	}
	/**
	 * 查询优惠券
	 * id:优惠券id
	 * mphone:发放人
	 * phone：s所属人
	 */
	@Auth
	@RequestMapping(value = "/Coupon/select", method = RequestMethod.POST)
	public RequestType Couponselect(String mGoldcoincouponKey, String id,String uname ,String mphone,String phone,Integer state,Integer carddate,Integer rows,Integer page,String web) throws Exception {
		String sa ;
		if(Goldcoincouponkey.equals(mGoldcoincouponKey)){
			sa="Goldcoincoupon";
		}else{
			sa="Coupon";
		}
		Sql msql = new Sql();
		msql.setSql("select "+sa+".*, nvl(uname,my_to_CouponName(memberid))uname,"+MyDate.orcaleCDATE2("star","star1")+","+MyDate.orcaleCDATE2("end","end1")+" from "+sa+" left join member on memberid=member.id where 1=1  ");
		if(!"1".equals(web))phone=uname;
		else
		if(getMember(getLogin(uname)).getSuperadmin()!=1)
			mphone=uname;
		
		if(Goldcoincouponkey.equals(mGoldcoincouponKey)){
			if(getMember(uname).getSuperadmin()!=1){
				msql.setSql(msql.getSql()+" and istrue =1 ");
			}
		}else{
			if(state!=null&&state==-1)state=null;
			if(carddate!=null&&carddate==-1)carddate=null;
			
			if(state==null&&carddate==null)
				msql.setSql(msql.getSql()+ " and end>"+System.currentTimeMillis()+"  and "+sa+" .state=0 ");
			else{
				if(state!=null){
					if(state==0)
						msql.setSql(msql.getSql()+ "  and "+sa+" .state = 0 ");
					else
						msql.setSql(msql.getSql()+ "  and "+sa+" .state = 1 ");
				}
					
				if(carddate!=null){
					if(carddate==0)
						msql.setSql(msql.getSql()+ " and "+sa+" .end>"+System.currentTimeMillis());
					else
						msql.setSql(msql.getSql()+ " and "+sa+" .end<"+System.currentTimeMillis());
				}
			}
			
			if(!Stringutil.isBlank(phone)){
				if(phone.length()>5)
					msql.setSql(msql.getSql()+" and memberid="+getLogin(phone).getUserid());
				else
					msql.setSql(msql.getSql()+" and memberid="+phone);
			}
				
			
		}
		
		
			
			
		if("1".equals(web)){
			if(!Stringutil.isBlank(mphone))
				msql.setSql(msql.getSql()+" and mphone="+mphone);
		}
		if(!Stringutil.isBlank(id))
			msql.setSql(msql.getSql()+" and "+sa+" .id = "+id);
		
		msql.setSql(msql.getSql()+" order by numbera desc , end ");
		
//		msql.setOrderbykey("Coupon.numbera");
//		msql.setOrderbytype(1);
		msql.setRows(rows);
		msql.setPage(page);
		return sendTrueData(mCouponService.exeSelectSql(msql));
	}
	/**
	 * 查询积分兑换优惠券
	 */
	@Auth
	@RequestMapping(value = "/Goldcoincoupon/select", method = RequestMethod.POST)
	public RequestType Goldcoincouponselect( String id,String uname ,String mphone,String phone,Integer state,Integer carddate,Integer rows,Integer page,String web) throws Exception {
		return Couponselect(Goldcoincouponkey, id, uname, mphone, phone, state, carddate, rows, page, getMember(uname).getSuperadmin()==1?"1":"0");
	}
	/**
	 * 上下架积分兑换优惠券
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/Goldcoincoupon/ut", method = RequestMethod.POST)
	public RequestType Goldcoincouponut(Long  id,Integer istrue) throws Exception {
		if(istrue==null)istrue=0;
		if(istrue!=0&&istrue!=1)return sendFalse("错误,未知的状态");
		Goldcoincoupon mGoldcoincoupon = new Goldcoincoupon();
		mGoldcoincoupon.setId(id);
		mGoldcoincoupon.setIstrue(istrue);
		mGoldcoincouponService.updateBySelect(mGoldcoincoupon);
		return sendTrueMsg((istrue==0?"下架":"上架")+"成功");
	}
	/**
	 * 兑换优惠券
	 */
	@Auth()
	@RequestMapping(value = "/coupon/mc", method = RequestMethod.POST)
	public RequestType couponmC(Long  id,String uname) throws Exception {
		Goldcoincoupon mGoldcoincoupon = mGoldcoincouponService.getById(id,Goldcoincoupon.class);
		if(mGoldcoincoupon==null)return sendFalse("错误,未找到指定优惠券");
		if(mGoldcoincoupon.getIstrue()==0)return sendFalse("该商品已下架");
		Member mMember= getMember(uname);
		if(mMember.getGoldcoin().intValue()<mGoldcoincoupon.getGoldcoinnum())return sendFalse("积分不足");
		Coupon mcoupon=GsonUtil.fromJsonString(GsonUtil.toJsonString(mGoldcoincoupon), Coupon.class);
		mcoupon.setStar(System.currentTimeMillis());
		mcoupon.setEnd(System.currentTimeMillis()+(1000L*60L*24L*365L));
		mcoupon.setState(0);
		mcoupon.setId(System.currentTimeMillis());
		mcoupon.setMemberid(mMember.getId());
		mCouponService.add(mcoupon);
		
		GoldcoinV mGoldcoinV = new GoldcoinV();
		mGoldcoinV.setMemberid(mMember.getId());
		mGoldcoinV.setNum(mGoldcoincoupon.getGoldcoinnum()-(mGoldcoincoupon.getGoldcoinnum()*2));
		mGoldcoinV.setTitle("兑换优惠券:"+mcoupon.getId());
		mValue.add(mGoldcoinV);
		
		mMember.setGoldcoin(mMember.getGoldcoin().subtract(new BigDecimal(mGoldcoincoupon.getGoldcoinnum())));
		setRedisMember(mMember, false);
		return sendTrueMsg("兑换成功");
	}

	/**
	 * 优惠券核销
	 */
	@Auth(coupon={Administration.UPDATE})
	@RequestMapping(value = "/Coupon/Write", method = RequestMethod.POST)
	public RequestType CouponWrite(String uname ,Long id) throws Exception {
		if(id==null)return sendFalse("优惠券编号不可为空");
		if(id.toString().length()==13){
			Coupon mCoupon=mCouponService.getById(id,Coupon.class);
			if(mCoupon==null)
				return sendFalse("优惠券编号不存在");
			if(mCoupon.getNumbera().doubleValue()>0)
				return sendFalse("该优惠券不是核销类型的优惠券");
			
			if(mCoupon.getState()!=0)
				return sendFalse("该优惠券已使用");
			Long time = System.currentTimeMillis();
			if(mCoupon.getStar()>time||mCoupon.getEnd()<time)
				return sendFalse("该优惠券不在使用期");
			
			if(getMember(uname).isClerk()){
				synchronized (this) {
					Sql msql = new Sql();
					msql.setSql("select id,nvl(csl,0)csl from shop where id="+getShop(uname).getId());
					Shop mshop= IBeanUtil.Map2JavaBean(mCouponService.exeSelectSql(msql).get(0), Shop.class);
					if(mshop.getCsl()==0)
						throw new RunException("剩余可核销袜子数量不足");
				}
				
			}else{
				return sendFalse("权限不足，如果你是新添加的店员或店主请点击(我的➡设置➡退出登陆)重新登陆");
			}
			
			mCoupon.setState(1);
			mCoupon.setData(uname);
			mCouponService.updateBySelect(mCoupon);
			CommissionTask.start(mCommissionService, id+";"+getLogin(uname).getUserid(), CommissionTask.COMMISSION_K);
		}else if( id.toString().length()==9){
			Cj mcj = mCjService.getByparameter("code", id.toString(),Cj.class);
			if(mcj==null)return sendFalse("兑换码不存在");
			mcj.setSu_name(uname);
			mcj.setT("已处理");
			mCjService.updateBySelect(mcj);
			
			
		}else{
			if(getMember(uname).getmShop()==null)return sendFalse("权限不足,你不能核销");
			Envelopes mEnvelopes = mEnvelopesService.getById(id,Envelopes.class);
			if(mEnvelopes==null)
				return sendFalse("红包不存在");
			if(mEnvelopes.getIstrue()==1)
				return sendFalse("红包已使用");
			
			mEnvelopes.setShopid(getMember(uname).getmShop().getId());
			mEnvelopes.setShopname(getMember(uname).getmShop().getShopname());
			mEnvelopes.setIstrue(1);
			mEnvelopes.setPhone(uname);
			mEnvelopesService.updateBySelect(mEnvelopes);
		
		}
		
//		if(mCoupon.getMemberid().equals((getLogin(uname).getUserid()+"")))
//			return sendFalse("该优惠券不属于你");
		return sendTrueMsg("核销成功");
	}
	/**
	 * 优惠券删除
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/Coupon/delete", method = RequestMethod.POST)
	public RequestType delete(Long id) throws Exception {
		if(id==null)return sendFalse("优惠券编号不可为空");
		Coupon mCoupon=mCouponService.getById(id,Coupon.class);
		if(mCoupon==null)
			return sendFalse("优惠券编号不存在");
		
		mCouponService.deleteByid(id);
		
		return sendTrueMsg("删除成功");
	}
	
	/**
	 * 验证商品是否合法,返回商品编号
	 * @throws Exception 
	 * */
	private static List<Map<String, Object>> auth(BaseService mBaseService,Integer i,String key,String value) throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from Commodity where "+key+"='"+value+"'");
		List<Map<String, Object>> listmap=mBaseService.exeSelectSql(msql);
		if(listmap.size()==0)
			throw new RunException("错误"+value+"不存在");
		if(i==1){
			if(listmap.get(0).get("TYPE")==null||!listmap.get(0).get("TYPE").toString().equals("1"))
				throw new RunException("错误"+value+"未上架");
			
			return listmap;
		}
		return null;

	}
	
	@Auth
	@RequestMapping(value = "/Coupon/istrue", method = RequestMethod.POST)
	public  RequestType Couponistrue(String onephone,BigDecimal number,String uname,String JsonArray) throws Exception{
		Sql msql = new Sql();
		
		List<Long> listLong = GsonUtil.fromJsonList(JsonArray, Long.class);
		List<Orderrelevance> listOrderrelevance;
		if (listLong.size() == 1) {
			
			listOrderrelevance = OrdersController.addonedata(mCouponService,null,listLong.get(0), uname, 1);

		} else {
			StringBuilder ids = new StringBuilder();
			for (Long mlong : listLong) {
				ids.append(mlong + ",");
			}
			msql.setSql("select * from ShoppingCard where itemid in(" + ids.substring(0, ids.length() - 1) + ")");
			listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mCouponService.exeSelectSql(msql),
					Orderrelevance.class);
		}
		List<Coupon> list =istrue(onephone,number, null, getLogin(uname).getUserid(), mCouponService,listOrderrelevance);
		return sendTrueData(list);
//		if(Stringutil.isBlank(CouponArray))
//			return sendTrueData(list);
//		else{
//			List<String> list1 = GsonUtil.fromJsonList(CouponArray, String.class); 
//			
//			Set<Coupon> listA =new HashSet<Coupon>();
//			
//			for (Coupon coupon : list) {
//				for (String string : list1) {
//					if(!string.equals(coupon.getId()+"")){
//						listA.add(coupon);
//					}
//				}
//			}
//			 
//			 
//			return sendTrueData(listA);
//		}
//		
		
	}
	/**
	 * 查询可用优惠券(暂不支持互斥卷)
	 * b:订单金额
	 * couponid：优惠券id，如果该id不可用抛出运行异常,否则返回改优惠券
	 * memberid:所属人
	 * @throws Exception 
	 * */
	public static List<Coupon> istrue(BigDecimal b,Long couponid,Long memberid,BaseService mBaseService,List<Orderrelevance> listOrderrelevance) throws Exception{
		return istrue("", b, couponid, memberid, mBaseService, listOrderrelevance);
		
	}
	public static List<Coupon> istrue(String onephone,BigDecimal b,Long couponid,Long memberid,BaseService mBaseService,List<Orderrelevance> listOrderrelevance) throws Exception{

//		if(listOrderrelevance!=null){
//			for (Orderrelevance orderrelevance : listOrderrelevance) {
//				//东岸梧桐
//				if(orderrelevance.getTitle().contains("东岸梧桐"))
//					return new ArrayList<Coupon>();
//			}
//		}
	
	List<Coupon> lc1=null;
	List<Coupon> listCoupon=null;
	Sql msql = new Sql();
	List<Map<String, Object>> listmap=null;
	List<Coupon> lc=null;
	if(couponid!=null){
		msql.setSql("select * from coupon where id="+couponid+" and memberid="+memberid +" and state=0 and end>"+System.currentTimeMillis()+"and star<"+System.currentTimeMillis()+" and numbermin<="+b.doubleValue());
		listmap = mBaseService.exeSelectSql(msql);
		if(listmap.size()==0)
			throw new RunException("购买的商品价格必须大于优惠券金额才可以使用优惠券");
	}else{
		msql.setSql("select "+MyDate.orcaleCDATE2("star", "star1")+","+MyDate.orcaleCDATE2("end", "end1")+",coupon.* from coupon where  memberid="+memberid +" and state=0 and end>"+System.currentTimeMillis()+"and star<"+System.currentTimeMillis()+" and numbermin<="+b.doubleValue()+" and numbera>0 order by numbera desc");
		listCoupon = IBeanUtil.ListMap2ListJavaBean(mBaseService.exeSelectSql(msql), Coupon.class);
		
		 lc = new ArrayList<>();
		
		for (Coupon mCoupon : listCoupon) {
			switch (mCoupon.getCardtype()) {
			case 1:
				if(mCoupon.getKey()==1){//单个商品
					for (Orderrelevance mOrderrelevance : listOrderrelevance) {
						if((mOrderrelevance.getItemid()+"").equals(mCoupon.getData()))
						{
							lc.add(mCoupon);
							break;
						}
					}
				}else{//一款商品
					for (Orderrelevance mOrderrelevance : listOrderrelevance) {
						if(auth(mBaseService, 1, "COMMODITYKEYID", mCoupon.getData()).get(0).get("ID").toString().equals(mOrderrelevance.getItemid()+""))
						{
							lc.add(mCoupon);
							break;
						}
					}
				}
				
				break;
			case 2:
				for (Orderrelevance mOrderrelevance : listOrderrelevance) {
					if((mOrderrelevance.getType4()).equals(mCoupon.getData()))
						{	
						lc.add(mCoupon);
						break;
						}
				}
				break;
			case 3:
				for (Orderrelevance mOrderrelevance : listOrderrelevance) {
					if((mOrderrelevance.getType3()).equals(mCoupon.getData()))
					{
						lc.add(mCoupon);
						break;
					}
				}
				break;
			case 4:
				for (Orderrelevance mOrderrelevance : listOrderrelevance) {
					if((mOrderrelevance.getType2()).equals(mCoupon.getData())){
						lc.add(mCoupon);
						break;
					}
					
				}
				break;
			case 5:
				for (Orderrelevance mOrderrelevance : listOrderrelevance) {
					if((mOrderrelevance.getType1()).equals(mCoupon.getData())){
						lc.add(mCoupon);
						break;
					}
						
				}
				break;

			default:
				lc.add(mCoupon);
				break;
			}
		}
		 lc1 = new ArrayList<>();
		 if(Stringutil.isBlank(onephone))
			 onephone="13333333333";
		 

			for (Coupon coupon : lc) {
				if(coupon.getOnephone().equals(onephone)||coupon.getOnephone().equals("13333333333")){
					lc1.add(coupon);
				}
			}
		
		
	}
	
	return lc==null?IBeanUtil.ListMap2ListJavaBean(listmap, Coupon.class):lc1;

	}

}
