package com.bm.orders;

import com.Shop5941Application;
import com.bm.Aenum.Orders_myp;
import com.bm.Aenum.Orders_paymenttype;
import com.bm.CommissionTask;
import com.bm.Message;
import com.bm.auths.MemberAuths;
import com.bm.base.BaseController;
import com.bm.base.BaseService;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.redis.RedisUtils;
import com.bm.base.request.RequestType;
import com.bm.base.util.*;
import com.bm.clerk.commission.CommissionService;
import com.bm.commodity.Commodity;
import com.bm.commodity.CommodityService;
import com.bm.consumption.Consumption;
import com.bm.consumption.ConsumptionService;
import com.bm.consumption.Refundresponse;
import com.bm.consumption.RefundresponseService;
import com.bm.consumption.pay.Apay;
import com.bm.consumption.pay.WeiPay;
import com.bm.coupon.Coupon;
import com.bm.myaddress.Myaddress;
import com.bm.myaddress.MyaddressService;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.orders.orderrelevance.OrderrelevanceEN;
import com.bm.orders.orderrelevance.OrderrelevanceService;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.bm.orders.orders.ReturnOrdersService;
import com.bm.orders.orderscard.OrdersCard;
import com.bm.orders.pdd.Pdd.ptfaenum;
import com.bm.orders.receiver.Receiver;
import com.bm.orders.receiver.ReceiverService;
import com.bm.promotion.Promotion;
import com.bm.promotion.PromotionController;
import com.bm.putforward.Putforward;
import com.bm.putforward.PutforwardService;
import com.bm.shop.Shop;
import com.bm.shoppingcard.ShoppingCard;
import com.bm.shoppingcard.ShoppingCardService;
import com.bm.stock.StockController;
import com.bm.task.TSystem;
import com.bm.user.Member;
import com.bm.user.MemberService;
import com.bm.user.PhoneMessage;
import com.bm.user.PhoneMessageService;
import com.bm.user.goldcoin.*;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;
import com.myjar.desutil.RunException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.*;

@RestController
@Api(tags = "订单")
public class OrdersController extends BaseController {
	@Autowired // 订单
	private OrdersService mOrdersService;
	@Autowired // 订单
	private ReturnOrdersService mReturnOrdersService;
	@Autowired // 商品
	private OrderrelevanceService mOrderrelevanceService;
	 @Autowired//物流
	 private ReceiverService mReceiverService;
	 @Autowired//收货地址
	 private MyaddressService mMyaddressService;
	@Autowired // 消费
	private ConsumptionService mConsumptionService;
	@Autowired // 购物车
	private ShoppingCardService mShoppingCardService;
//	@Autowired // 邮费
//	private PostfeesService mPostfeesService;

	@Autowired
	private CommodityService commodityService;
	@Autowired
    private PhoneMessageService mPhoneMessageService;
	@Autowired
	private CommissionService mCommissionService;
	@Autowired
    private RefundresponseService mRefundresponseService;
	
	
	@Autowired
	private GoldcoinVService mValue;
	@Autowired
	private GoldcoinDefaultService mDefault;
	
	
	@Auth
	@Transactional
	@RequestMapping(value = "/orders/jiesuanweijiesuan", method = RequestMethod.POST)
	public RequestType ordersReturn() throws Exception {
		Sql msql = new Sql();
		
		
		if(MyParameter.Home_name.equals( InetAddress.getLocalHost().getHostName())){
			msql.setSql("select * from  orders where status=1 ");
			List<Map<String, Object>> listmap= mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				CommissionTask.start(mCommissionService, map.get("ID").toString(), CommissionTask.COMMISSION_1);
			}
//			msql.setSql("update orders set status=2 where status=1 ");
//			mMemberService.execSQL(msql, -1, "");
			
		}else{
			return sendFalse("此功能只有测试环境才能使用");
		}
		
		return sendTrueData("付款成功");
	}
	
	
	///////////////////////退单Star//////////////////////////////////////////////
		
	/**
	 * 退货 20，拒绝，21提交，22已同意，23已退仓，24已完成
	 * buyermesege:退货原因
	 */
	@Auth
	@Transactional
	@RequestMapping(value = "/orders/Return", method = RequestMethod.POST)
	public RequestType ordersReturn(Integer returntype,String postfeenumber,String id,String buyermesege,Integer type,String uname) throws Exception {
		if(type==null){
			
			Orders mOrders=mOrdersService.getById(id,Orders.class);
		
			if(mOrders==null)
				return sendFalse("订单不存在");
			
			if(mOrders.getStatus()!=4)
				return sendFalse("还未收货的订单不能退货");
			
			if(System.currentTimeMillis()-mOrders.getUpdatetime()>MyParameter.ReturnOrdersTime)
				return sendFalse("已超过退货时间，不能退货");
			
			Sql msql = new Sql();
			msql.setSql("select orderid from Returngoods left join orderrelevance on orderrelevance.id=orderrelevanceid where orderid="+id);
			if(mOrdersService.exeSelectSql(msql).size()!=0)
				return sendFalse("该订单已退了一部分商品，不能申请退单");
			
			mOrders.setTrajectory("");
			if(mReturnOrdersService.getById(id)!=null)
				return sendFalse("该订单已申请");
			mOrders.setUpdatetime(System.currentTimeMillis());
			mOrders.setStatus(21);
			mOrders.setBuyermesege(buyermesege);
			if(returntype==null)returntype=2;
			String str = returntype==1?"供应商":"门店";
			mOrders.setTrajectory(mOrders.getTrajectory()+System.currentTimeMillis()+";退货渠道:"+str+";");
			mOrdersService.updateBySelect(mOrders);
			mOrders.setOrdernumber(mOrders.getId()+"");
			mOrders.setTrajectory("");
			mOrders.setPostfeenumber(postfeenumber);
			mReturnOrdersService.add(mOrders);
		
			return sendTrueMsg("操作成功");
		}else{
			AutoShop(uname);
			
			Orders mOrders=mOrdersService.getById(id,Orders.class);
			if(mOrders==null)
				return sendFalse("订单不存在");
			
			if(type==20){
				if(Stringutil.isBlank(buyermesege))
					return sendFalse("拒绝原因不可为空");
				mOrders.setTrajectory(buyermesege);
				if(mOrders.getStatus()!=21)
					return sendFalse("该订单已处理");
			}else
			if(type==22){
				if(getMember(uname).getmShop()!=null&&getMember(uname).getmShop().getSuperid()==0)
					mCouponService.OutsendCouponTH(mOrders.getMemberid());
				else
					mCouponService.OutsendCouponTH(getLogin(uname).getUserid());
				if(mOrders.getStatus()!=21&&mOrders.getStatus()!=20)
					return sendFalse("该订单已同意");
			}else
				if(type==23){
					if(mOrders.getStatus()!=22)
						return sendFalse("该订单不是同意状态");
					if(Stringutil.isBlank(postfeenumber))
						return sendFalse("运单号不可为空");
			}else
				if(type==24){
					AutoShopOne(uname);
					if(mOrders.getStatus()!=23)
						return sendFalse("该订单不是退仓状态");
				}
			mOrders.setStatus(type);
//			Sql msql = new Sql();
			if(type==23)
				mOrders.setPostfeenumber(postfeenumber);
//				msql.setSql("update orders set status="+type+",postfeenumber='"+postfeenumber+"' where id='"+id+"'");
			else if(type==20)
				mOrders.setTrajectory(buyermesege);
//				msql.setSql("update orders set status="+type+",Trajectory='"+buyermesege+"' where id='"+id+"'");
			else if(type==22)
				mOrders.setAutosystem(0);
//				msql.setSql("update orders set autosystem=0,status="+type+" where id='"+id+"'");
//			else
//				msql.setSql("update orders set status="+type+" where id='"+id+"'");
			
			mOrdersService.updateBySelect(mOrders);
			
			if(type==23)
				mOrders.setPostfeenumber(postfeenumber);
			else if(type==20)
				mOrders.setTrajectory(buyermesege);
			else if(type==22)
				mOrders.setAutosystem(0);
			mReturnOrdersService.updateBySelect(mOrders);
			
			
			
		}
		
		
		return sendTrueMsg("操作成功");
	}
	
	/**
	 * 退货列表
	 * 退货 20，拒绝，21提交，22已同意，23已退仓，24已完成
	 * buyermesege:退货原因
	 */
	@Auth
	@RequestMapping(value = "/orders/ListReturn", method = RequestMethod.POST)
	public RequestType ordersListReturn(String uname,Integer page,Integer rows,Orders mOrders) throws Exception {
			Long s = AutoShop(uname);
			Sql msql = new Sql();
			msql.setPage(page);
			msql.setRows(rows);
			String sql ="select * from ReturnOrders "+mOrdersService.getWhere(mOrders);
			if(s!=null)
				mOrders.setShopid(s);
			if(getMember(uname).getSuperadmin()!=1){
				if(getMember(uname).getmShop()!=null&&getMember(uname).getmShop().getSuperid()==0)
					sql=sql+" and onephone='"+uname+"'";
				else
					sql=sql+" and shopid='"+getMember(uname).getmShop().getId()+"'";
			}
			
			msql.setSql(sql);
			msql.setOrderbytype(1);
			msql.setOrderbykey("id");
			List<Map<String, Object>> listmap = mOrdersService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				map.put("MEMBERID", getMember(Long.valueOf(map.get("MEMBERID").toString())).getUname());
			}
			return sendTrueData(listmap);
//		return sendTrueData(mReturnOrdersService.getALL(mOrders, page, rows));
	}
	/**
	 * 退货列表
	 * 退货 20，拒绝，21提交，22已同意，23已退仓，24已完成
	 * buyermesege:退货原因
	 */
	@Auth
	@RequestMapping(value = "/orders/ListReturnPhone", method = RequestMethod.POST)
	public RequestType ListReturnPhone(Integer status,String uname,Integer page,Integer rows) throws Exception {
		if(getMember(uname).getmShop()!=null)
			return MyListReturn(getMember(uname).getmShop().getId(), status, rows, page, uname, uname);
		else
			return MyListReturn(null, status, rows, page, uname, uname);
	}
	
	
	/**
	 * 我的退货列表
	 * 退货 20，拒绝，21提交，22已同意，23已退仓，24已完成
	 */
//	@Auth
//	@Transactional
//	@RequestMapping(value = "/orders/MyListReturn", method = RequestMethod.POST)
//	public RequestType ordersMyListReturn(String uname,Integer page,Integer rows) throws Exception {
//		Orders mOrders = new Orders();
//		mOrders.setMemberid(getLogin(uname).getUserid());
//		@SuppressWarnings("unchecked")
//		List<Orders> listorders = (List<Orders>) mReturnOrdersService.getALL(mOrders, page, rows);
//		
//		for (Orders os : listorders) {
//			if(os.getStatus()>22)
//				os.setStatus(22);
//			
//		}
//		return sendTrueData(listorders);
//	}
//	
	@Auth
	@RequestMapping(value = "/orders/MyListReturn", method = RequestMethod.POST)
	public RequestType MyListReturn(Long id,Integer status,  Integer rows, Integer page, 
			String uname,String phone) throws Exception {
		String sql = "select ReturnOrders.id id2, ReturnOrders.shopid,1 mka,ReturnOrders.memberid, ordernumber, paymenttype,couponid,nvl(postfeenumber,'暂无')postfeenumber,status statustype,shippingtype,trajectory,buyermesege,"
				+ MyDate.orcaleCDATE("ReturnOrders.endtime")
		
				+ ",ReturnOrders.id,decode(status,20,'已拒绝',21,'审核中',22,'已同意',23,'已退仓',24,'已收仓') status,status status1,payment+postfee payment from ReturnOrders  where  status>=20 "
				
				+ " union all(select Returngoods.id, orders.shopid,orderrelevanceid,orders.memberid, to_char(orders.id),refund,orders.paymenttype,nvl(logistics,'暂无'),decode(istrue,-1,20,0,21,1,22,2,23,3,24),orders.shippingtype,refuse,reason,"
				+ MyDate.orcaleCDATE("Returngoods.id")
				+ ",orders.id,decode(istrue,-1,'已拒绝',0,'审核中',1,'已同意',2,'已退仓',3,'已收仓'),decode(istrue,-1,20,0,21,1,22,2,23,3,24),Returngoods.price from Returngoods left join orderrelevance on orderrelevance.id=orderrelevanceid "
				+ "left join orders on orders.id=orderid )";
		sql="select * from ("+sql+") where 1=1 ";
		Sql msql = new Sql();
		msql.setOrderbykey("id");
		msql.setOrderbytype(1);
		msql.setPage(page == null ? 1 : page);
		msql.setRows(rows);
		if (status != null&&status!=0) {
			if (status == 22)
				sql = sql + " and status1 in(22,23,24)";
			else
				sql = sql + " and status1= "+status;
			
		}
		if(!Stringutil.isBlank(phone)){
			if(id==null)//店员
				sql=sql+ " and memberid in(select memberidb from Friends where memberida="+getLogin(phone).getUserid()+")";
			else//店主
				sql=sql+ " and shopid="+id;
			}else{
				sql=sql+" and memberid=" + getLogin(uname).getUserid();
			}
		msql.setSql(sql);
		
		List<Map<String, Object>> listmap = mOrdersService.exeSelectSql(msql);
		msql.setPage(null);
		msql.setRows(null);
		for (Map<String, Object> map : listmap) {
			if(map.get("MKA").toString().equals("1")){
				// 图片
				msql.setSql("select picpath from Orderrelevance where orderid=" + map.get("ID").toString());
				List<Map<String, Object>> list = mOrdersService.exeSelectSql(msql);
				if (list.size() > 4)
					map.put("sizenumber", 1);
				else
					map.put("sizenumber", 0);
				map.put("image", list);
				map.put("imagesize", list.size());
				
			}else{
				// 图片
				msql.setSql("select picpath from Orderrelevance where id=" + map.get("MKA").toString());
				List<Map<String, Object>> list = mOrdersService.exeSelectSql(msql);
				map.put("sizenumber", 0);
				map.put("image", list);
				map.put("imagesize", list.size());
				
			}
		
			
		}
		return sendTrueData(listmap);
	}

	
	
	///////////////////////退单end//////////////////////////////////////////////
	 /**
     * 到货提醒
     * */
    public  void phonemessageaAdd(String phone,String orderid){
    	try {
    		PhoneMessage mphonemessage = new PhoneMessage();
        	mphonemessage.setPhone(phone);
        	mphonemessage.setType(3);
        	Map<String, Object> map = new HashMap<String, Object>();
    		map.put("apikey",MyParameter.MESSAGE_APIKEY);
    		map.put("mobile", phone);
        	
        	phone ="【5941商城】您的订单尾号："+orderid+",已到门店，请您到门店自提。";
        	map.put("text", phone);
        	mphonemessage.setCode(1234);
    		mphonemessage.setTime(System.currentTimeMillis());
    		mphonemessage.setMsg(phone);
    		mPhoneMessageService.add(mphonemessage);
        
    		HttpRequest.sendPost(MyParameter.MESSAGE_URL, map);
		} catch (Exception e) {
		}
    	
    
    		
    	
    }
    
//    public static void main(String[] args) {
//    	Map<String, Object> map = new HashMap<String, Object>();
//		map.put("apikey",MyParameter.MESSAGE_APIKEY);
//		map.put("mobile", "15711586039");
//    	
////    	map.put("text", "您的验证码是1234。如非本人操作，请忽略本短信");
//    	map.put("text", "【八明科技】您的订单编号：1234,已到门店，请您到门店自提。");
//    	
//    
//		HttpRequest.sendPost(MyParameter.MESSAGE_URL, map);
//	}
    
    private void zf(Orders mOrders,Member mMember) throws Exception{
		Consumption cu = new Consumption();
		cu.setMemberid(mOrders.getMemberid());
		cu.setOriginalprice(mOrders.getPayment());
		cu.setTime(System.currentTimeMillis());
		cu.setIntroduce("支付订单:" + mOrders.getId());
		cu.setType(1);
		cu.setState(0);
		mConsumptionService.add(cu);
		
		
		
		Putforward mPutforward = new Putforward();
		mPutforward.setPhone(getMember(mOrders.getMemberid()).getUname());
		mPutforward.setMemberid(mOrders.getMemberid());
		mPutforward.setName("订单支付");
		mPutforward.setZfb(mOrders.getId().toString());
		mPutforward.setNum(mOrders.getPayment());
		mPutforward.setIstrue(1);
		mPutforward.setProcedures(new BigDecimal(0));
		mPutforward.setMoney(mOrders.getPayment());
		mPutforwardService.add(mPutforward);
		
		mOrders = new Orders();
		mOrders.setId(Long.valueOf(mPutforward.getZfb()));
		mOrders.setUpdatetime(System.currentTimeMillis());
		mOrders.setPaymenttime(System.currentTimeMillis());
		mOrders.setStatus(2);
		mOrders.setPaymenttype(5);
		mOrdersService.updateBySelect(mOrders);
		
		
    }
    
	/**
	 * 钱包支付
	 */
	@SuppressWarnings("all")
	@RequestMapping(value = "/pay/{sign}", method = RequestMethod.POST)
	@Transactional
	@Auth
	public RequestType systempay(@PathVariable String sign, String uname) throws Exception {
		if (!MyParameter.consumption_true)
			return sendFalse("支付错误，钱包不存在");
		
		
		BigDecimal ba = new BigDecimal("0");
		Orders mOrders;
		List<Orders> mlistOrders = new ArrayList<>();
		try {
			String id = DESUtils.decode2(sign, MyParameter.KEY_ORDERS);
			
			try {
				mOrders = IBeanUtil.Map2JavaBean(mOrdersService.getById(id), Orders.class);
				if (mOrders == null)
					return sendFalse("订单不存在");
				mlistOrders.add(mOrders);
			} catch (Exception e) {
				mOrders = new Orders();
				mOrders.setOrdernumber(id);
				mlistOrders=(List<Orders>) mOrdersService.getALL(mOrders);
				
			}
			 
			for (Orders orders : mlistOrders) {
				ba=ba.add(orders.getPayment());
			}

			Member mMember=mMemberService.getById(getLogin(uname).getUserid(),Member.class);
			// 余额验证
			BigDecimal b =mMember.getPlatformcurrency();
			if (b.subtract(ba).doubleValue() < 0)
				return sendFalse("抱歉，余额不足");
			mMember.setPlatformcurrency(b.subtract(ba));
			mMemberService.updateBySelect(mMember);
			setRedisMember(mMember, false);
			for (Orders orders : mlistOrders) {
				zf(orders, mMember);
			}
			
			return sendTrueMsg("支付成功");
		} catch (Exception e) {
			return sendFalse("签名错误");
		}

	}
	@Autowired
	private PutforwardService mPutforwardService;

	/**
	 * 订单签名
	 */
	@Auth
	@RequestMapping(value = "/orders/{id}", method = RequestMethod.POST)
	public RequestType ordersSign(@PathVariable String id) throws Exception {
		try {
			Long.valueOf(id);
			if (mOrdersService.getById(id) != null)
				return sendTrueData(DESUtils.encode2(id, MyParameter.KEY_ORDERS, 300));
		} catch (Exception e) {
			if (mOrdersService.getByparameter("ordernumber", id) != null)
				return sendTrueData(DESUtils.encode2(id, MyParameter.KEY_ORDERS, 300));
		}
		return sendFalse("订单不存在");
	}
	/**
	 * 退单
	 */
	@Auth
	@RequestMapping(value = "/orders1/{id}", method = RequestMethod.POST)
	public RequestType ordersSign1(@PathVariable String id,String uname) throws Exception {
		Orders mOrders=mOrdersService.getById(id,Orders.class);
		if(mOrders==null)
		return sendFalse("订单不存在");
		if(mOrders.getStatus()!=2)
			return sendFalse("商家已接单，订单不能取消");
		
		if(!getLogin(uname).getUserid().toString().equals(mOrders.getMemberid().toString()))
			return sendFalse("订单不属于你");
		 mOrders= new Orders();
		mOrders.setId(Long.valueOf(id));
		mOrders.setStatus(10);
		mOrdersService.updateBySelect(mOrders);
		
		return sendTrueMsg("取消订单成功，退款将会在24小时内原路返回");
	}
	public static synchronized void returnPrice(RefundresponseService mRefundresponseService,MemberService mMemberService,OrdersService mOrdersService,Orders mOrders,BigDecimal je) throws Exception{
		mOrders=mOrdersService.getById(mOrders,Orders.class);
		
		if(mOrders.getPaymenttype()==Orders_paymenttype.ZFB.getKey()){
			if(je!=null)return;
			Apay.aPAYorderReturn(mRefundresponseService,mOrdersService, mOrders.getId());
		}else if(mOrders.getPaymenttype()==Orders_paymenttype.WX.getKey()){
			WeiPay.wPAYorderReturn(mRefundresponseService,mOrdersService, mOrders.getId(),je);
		}else if(mOrders.getPaymenttype()==Orders_paymenttype.KLB.getKey()){
			Sql msql = new Sql();
			msql.setSql("select * from member where id="+mOrders.getMemberid());
			Member mMember =IBeanUtil.Map2JavaBean(mOrdersService.exeSelectSql(msql).get(0), Member.class);
			je=je==null?mOrders.getPayment():je;
			mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(je));
			mMemberService.updateBySelect(mMember);
			
			Refundresponse rp = new Refundresponse();
			rp.setOrdercode(mOrders.getOrdernumber());
			rp.setPrice(je);
			rp.setOrdernum(mOrders.getPayment());
			rp.setType(Refundresponse.Refundresponse_type.KLB.toString());
			rp.setIstrue(1);
			mRefundresponseService.add(rp);
			
			
			
		}else{
			throw new RunException("退款失败，未知的支付模式");
		}
	}
	public static void returnPrice(RefundresponseService mRefundresponseService,MemberService mMemberService,OrdersService mOrdersService,Orders mOrders) throws Exception{
		returnPrice(mRefundresponseService,mMemberService,mOrdersService, mOrders, null);
	}
	/**
	 * 退款
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/orders2/{id}", method = RequestMethod.POST)
	@Transactional
	public RequestType ordersSign2(@PathVariable String id) throws Exception {
		Orders mOrders=mOrdersService.getById(id,Orders.class);
			if(mOrders==null)
				return sendFalse("订单不存在");
			if(mOrders.getAutosystem()!=null&&mOrders.getAutosystem()==1){
				return sendFalse("订单已退款");
			}

			mOrders.setAutosystem(1);
			mOrdersService.updateBySelect(mOrders);
			mOrders= new Orders();
			mOrders.setId(Long.valueOf(id));
			mOrders.setAutosystem(1);
			mReturnOrdersService.updateBySelect(mOrders);
			returnPrice(mRefundresponseService,mMemberService, mOrdersService, mOrders);
			
			return sendTrueMsg("退单成功，退款将会在24小时内原路返回");
		
//			if(mOrders.getStatus()!=10&&mOrders.getStatus()!=9){
//				 mOrders=mReturnOrdersService.getByparameter("ordernumber", id,Orders.class);
//				 if(mOrders==null)
//						return sendFalse("未找到订单");
//				 if(mOrders.getStatus()>21){
//					 mOrders= new Orders();
//						mOrders.setId(Long.valueOf(id));
////						mOrders.setStatus(11);
//						mOrders.setAutosystem(1);
//						mReturnOrdersService.updateBySelect(mOrders);
//						mOrdersService.updateBySelect(mOrders);
////						returnPrice(mMemberService, mOrdersService, mOrders);
//						return sendTrueMsg("退单成功，单品退货请手动退款");
//				 }else{
//					 return sendTrueMsg("订单还为未同意");
//				 }
//					
//			}else{
////				mOrders= new Orders();
////				mOrders.setId(Long.valueOf(id));
////				mOrders.setStatus(11);
//				mOrders.setAutosystem(1);
//				mOrdersService.updateBySelect(mOrders);
//				
//				returnPrice(mRefundresponseService,mMemberService, mOrdersService, mOrders);
//				
//				mOrders= new Orders();
//				mOrders.setId(Long.valueOf(id));
////				mOrders.setStatus(11);
//				mOrders.setAutosystem(1);
//				mReturnOrdersService.updateBySelect(mOrders);
//				
//				return sendTrueMsg("退单成功，退款将会在24小时内原路返回");
//			}
		
		}
	/**
	 * 订单统计
	 */
	@Auth
	@RequestMapping(value = "/orders/sumbyshopid", method = RequestMethod.POST)
	public RequestType orderssumbyshopid(String uname,Integer rows,Integer page,Long star,Long end) throws Exception {
		star=star==null?0L:star;
		end=end==null?System.currentTimeMillis():end;
		String sql = "select count(*) sc,shopid,shopname from orders where id<"+end +" and id>"+star;
		Sql msql = new Sql();
		msql.setRows(rows);
		msql.setPage(page);
		if(getMember(getLogin(uname)).getSuperadmin()!=1)
			sql= sql+ " and shopid="+getMember(getLogin(uname)).getmShop().getId();
		sql=sql+" group by shopid,shopname";
			
		return sendTrueData(mOrdersService.exeSelectSql(msql));
	}

	/**
	 * 删除订单
	 */
	@Auth
	@RequestMapping(value = "/orders/delete", method = RequestMethod.POST)
	public RequestType delete(String id,String uname) throws Exception {
		Orders mOrders = mOrdersService.getById(id, Orders.class);
		if (mOrders == null)
			return sendFalse("订单不存在");
		if (mOrders.getStatus() != 1)
			return sendFalse("订单不能删除");
		
		if(getMember(uname).getSuperadmin()!=1&&!mOrders.getMemberid().toString().equals(getLogin(uname).getUserid().toString()))
			return sendFalse("该订单不属于你");
		
		
		Sql msql = new Sql();
		msql.setSql("select * from Orderrelevance where orderid="+mOrders.getId());
		List<Orderrelevance> listmap = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orderrelevance.class);
		for (Orderrelevance orderrelevance : listmap) {
			msql.setSql("update stock set num=num+"+orderrelevance.getNum() +" where code='"+orderrelevance.getYoucode()+"'");
			mOrdersService.execSQL(msql, 0, "");
			msql.setSql("delete Orderrelevance where id ="+orderrelevance.getId());
		}
		
		mOrdersService.deleteByid(id);
		
//		mOrders.setStatus(5);
//		mOrders.setClosetime(System.currentTimeMillis());
//		mOrdersService.updateBySelect(mOrders);
//		mOrdersService.deleteByid(id);
		return sendTrueMsg("删除成功");
	}
	/**
	 * 供应商运单号批量导入
	 */
	@Auth
	@Transactional
	@RequestMapping(value = "/orders/postfeenumberex", method = RequestMethod.POST)
	public RequestType postfeenumberex(String uname,HttpServletRequest req) throws Exception {
		try {
			if(getMember(uname).getSuperadmin()!=1&&getMember(uname).getmShop().getSuperid()!=0)return sendFalse("权限不足");
		} catch (Exception e) {
			return sendFalse("权限不足");
		}
		
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			int a=0;
			for (List<String> list : lls) {
				if(list.size()!=2)throw new RunException("错误，第"+a+"行，数据不足，请检查");
				Orders mOrders = mOrdersService.getById(list.get(0),Orders.class);
				if(mOrders==null)throw new RunException("错误，第"+a+"行，订单号："+list.get(0)+" 不存在，请检查");
				mOrders =new Orders();
				mOrders.setId(Long.valueOf(list.get(0)));
				mOrders.setPostfeenumber(list.get(1));
				mOrdersService.updateBySelect(mOrders);
				a=a+1;
			}
			

//			
			return sendTrueMsg("导入成功，此次一共导入" + a + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}

	
	private void xg(List<OrderrelevanceEN> cm,String uname) throws Exception {
		//限购数量
		for (OrderrelevanceEN orderrelevanceEN : cm) {
			if(orderrelevanceEN.getAsize()!=null&&orderrelevanceEN.getAsize()!=0){
				Sql msql = new Sql();
//				msql.setSql("select nvl(asize,0)asize,commoditykeyid from (select a.*,b.itemid from (select sum(num)asize,commoditykeyid from Orderrelevance left join commodity on commodity.id=itemid where memberid="+getLogin(uname).getUserid()+" group by commoditykeyid )a left join (select itemid,commoditykeyid from Orderrelevance left join commodity on commodity.id=itemid   )b on a.commoditykeyid=b.commoditykeyid"
//						+ ") where commoditykeyid =(select commoditykeyid from commodity where id="+orderrelevanceEN.getItemid()+")");
				msql.setSql("select sum(num) asize, commoditykeyid "
						+ "from Orderrelevance "
						+ "left join commodity "
						+ " on commodity.id = "
						+ getLogin(uname).getUserid()
						+ " where memberid = 1537933762036 "
						+ "and commoditykeyid = "
						+ " (select commoditykeyid from commodity where id = "
						+ orderrelevanceEN.getItemid()+")"
						+ "group by commoditykeyid");
				
				
				List<Map<String, Object>> ma = mOrdersService.exeSelectSql(msql);
				int a ;
				if(ma.size()==0)
					a=0;
				else
					a=Integer.parseInt(ma.get(0).get("ASIZE").toString());
				if(orderrelevanceEN.getAsize()-(a+orderrelevanceEN.getNum())<0)
					throw new RunException("错误，商品"+orderrelevanceEN.getTitle()+"限购"+orderrelevanceEN.getAsize()+"个\n你最多还可以购买"+(orderrelevanceEN.getAsize()-a)+"个");
			}
		}

	}
	/**
	 * 确认信息页面2.0
	 * */	
	@RequestMapping(value = "/Orderrelevance/selectbyCommodityid2", method = RequestMethod.POST)
	public RequestType selectbyCommodityid2(String JsonArray, Integer size,  String uname,String couponid) throws Exception {
		
			Map<String, Object> mMap1= new HashMap<String, Object>();
			List<ordersCommodity> list = new ArrayList<>();
			BigDecimal ba = new BigDecimal("0");
			
			
			Map<String, Object> mMap =getOrderrelevance(JsonArray, size, uname, couponid);
			@SuppressWarnings("unchecked")
			List<OrderrelevanceEN> cm = (List<OrderrelevanceEN>) mMap.get("data");//全部数据
			xg(cm, uname);
			//塞选供应商
			for (OrderrelevanceEN orderrelevanceEN : cm) {
				boolean b =true;
				for (ordersCommodity mordersCommodity : list) {
					if(mordersCommodity.getOnephone().equals(orderrelevanceEN.getOneA())){
						b=false;
						break;
					}
						
				}
				if(b){
					ordersCommodity a= new ordersCommodity();
					a.setOnename(orderrelevanceEN.getOneB());
					a.setOnephone(orderrelevanceEN.getOneA());
					a.setNumall(new BigDecimal("0"));
					a.setData(new ArrayList<>());
					list.add(a);
				}
			}
			//商品价格玉数据
			for (OrderrelevanceEN orderrelevanceEN : cm) {
				for (ordersCommodity mordersCommodity : list) {
					if(mordersCommodity.getOnephone().equals(orderrelevanceEN.getOneA())){
						mordersCommodity.getData().add(orderrelevanceEN);
						mordersCommodity.setNumall(mordersCommodity.getNumall().add(orderrelevanceEN.getTotalfee()));
						ba=ba.add(orderrelevanceEN.getTotalfee());
					}
				}
			}
			//邮费
			for (ordersCommodity mordersCommodity : list) {
				mordersCommodity.setPostfee(getpostfee(mordersCommodity.getNumall()));
				mordersCommodity.setNumall(mordersCommodity.getNumall().add(mordersCommodity.getPostfee()));
				ba=ba.add(mordersCommodity.getPostfee());
			}
			mMap1.put("numall", ba);
			mMap1.put("Array", list);
			mMap1.put("shopaddress", mMap.get("shopaddress"));
		return sendTrueData(mMap1);
		
	}
	
	
	
	public class ordersCommodity{
		private String onephone;//供应商账号
		private String onename;//供应商店铺名字
		private BigDecimal numall;//价格
		private BigDecimal postfee;//邮费
		List<OrderrelevanceEN> data ;//商品
		public String getOnephone() {
			return onephone;
		}
		public void setOnephone(String onephone) {
			this.onephone = onephone;
		}
		public String getOnename() {
			return onename;
		}
		public void setOnename(String onename) {
			this.onename = onename;
		}
		public BigDecimal getNumall() {
			return numall;
		}
		public void setNumall(BigDecimal numall) {
			this.numall = numall;
		}
		public BigDecimal getPostfee() {
			return postfee;
		}
		public void setPostfee(BigDecimal postfee) {
			this.postfee = postfee;
		}
		public List<OrderrelevanceEN> getData() {
			return data;
		}
		public void setData(List<OrderrelevanceEN> data) {
			this.data = data;
		}
		
		
	}
	
	private Map<String, Object> getOrderrelevance(String JsonArray, Integer size,  String uname,String couponid) throws Exception {

		if("undefined".equals(couponid))couponid=null;
		List<Long> listlong = GsonUtil.fromJsonList(JsonArray, Long.class);
		if (listlong.size() == 0)
			throw new RunException("未找到商品");
		Map<String, Object> mp = new HashMap<>();
		//绑定的店铺
			Myaddress mMyaddress = new Myaddress();
			Shop mShop=getShop(uname);
				mMyaddress.setProvince(mShop.getProvince());
				mMyaddress.setCity(mShop.getCity());
				mMyaddress.setArea(mShop.getArea());
				mMyaddress.setStreet(mShop.getStreet());
				mMyaddress.setDetailed(mShop.getDetailed());
				mMyaddress.setLongitude(mShop.getLongitude());
				mMyaddress.setLatitude(mShop.getLatitude());
				mMyaddress.setName(mShop.getShopname());
				mMyaddress.setPhone(mShop.getShopphone());
				
			mp.put("shopaddress",mMyaddress);
			
		if (listlong.size() == 1) {
			Commodity mCommodity = IBeanUtil.Map2JavaBean(commodityService.getById(listlong.get(0)), Commodity.class);
			if (mCommodity == null) {
				return getdata(listlong, mp,getLogin(uname).getUserid(),couponid);
			}
			OrderrelevanceEN rle = new OrderrelevanceEN();
			rle.setItemid(listlong.get(0));
			if (size == null)
				rle.setNum(1);
			else
				rle.setNum(size);
			rle.setTotalfee(mCommodity.getPrice().multiply(new BigDecimal(size)));
			rle.setTitle(mCommodity.getName());
			rle.setPrice(mCommodity.getPrice());
			rle.setPicpath(mCommodity.getMainimage().split(";")[0]);
			rle.setType1(mCommodity.getLargeclass());
			rle.setType2(mCommodity.getInclass());
			rle.setType3(mCommodity.getSmallclass());
			rle.setType4(mCommodity.getFineclass());
			rle.setColour(mCommodity.getColour());
			rle.setMysize(mCommodity.getMysize());
			rle.setOneA(mCommodity.getSupplier());
			rle.setOneB(mCommodity.getSuppliername());
			rle.setAsize(mCommodity.getAsize());
			List<OrderrelevanceEN> cm = new ArrayList<>();
			cm.add(rle);

			Promotion mPromotion;
			BigDecimal b1 = new BigDecimal("0");// 减免价格
			for (OrderrelevanceEN orderrelevanceEN : cm) {
				
				//检查库存是否充足
				StockController.autostock(mMemberService, orderrelevanceEN.getItemid(),orderrelevanceEN.getNum(),orderrelevanceEN.getTitle());
				// 活动
				mPromotion = PromotionController.getPromotion(mMemberService,mCommodity.getYoucode());
				orderrelevanceEN.setmPromotion(mPromotion);

				if (mPromotion != null) {
					if (mPromotion.getType() == 1)
						b1 = mCommodity.getPrice().subtract(mCommodity.getPrice().multiply(mPromotion.getDiscount().divide(new BigDecimal("10")))).multiply(new BigDecimal(orderrelevanceEN.getNum()));
					else if (mPromotion.getType() == 2)
						b1 = mPromotion.getReduce().multiply(new BigDecimal(orderrelevanceEN.getNum()));

					mPromotion.setReduce(b1);
				}

			}
			BigDecimal b = ( mCommodity.getPrice().multiply(new BigDecimal(rle.getNum()))).subtract(b1);
			b=b.add(getpostfee(b));
			mp.put("data", cm);//s数据
			
//			try {
//				Shop mshop  = getMember(getLogin(mCommodity.getSupplier())).getmShop();
//				Sql msql = new Sql();
//				msql.setSql("select * from Postfees where oneid="+mshop.getId()+" order by id desc");
//				List<Map<String, Object>> list = commodityService.exeSelectSql(msql);
//				if(list.size()!=0){
//					mp.put("postfee", list.get(0).get("POSTFEE"));
//				}else
//				mp.put("postfee", 0);
//			} catch (Exception e) {
//				mp.put("postfee", 0);
//			}
//			mp.put("Coupon",CouponController.istrue(new BigDecimal(mp.get("numall").toString()), null, getLogin(uname).getUserid(), mOrdersService));
				
			mp.put("postfee", getpostfee(b));
			
			
			
			if(!Stringutil.isBlank(couponid)){//优惠券
				Sql msql = new Sql();
				msql.setSql("select * from coupon where id='" +couponid + "'");
				b = b.subtract(new BigDecimal(mOrdersService.exeSelectSql(msql).get(0).get("NUMBERA").toString()));
			}
			mp.put("numall",b);//金额
			
			return mp;
		} else {
			return getdata(listlong, mp,getLogin(uname).getUserid(),couponid);
		}

	

	}
	/**
	 * 商品信息1.0
	 * 
	 */
	@RequestMapping(value = "/Orderrelevance/selectbyCommodityid", method = RequestMethod.POST)
	public RequestType selectbyCommodityid(String JsonArray, Integer size,  String uname,String couponid) throws Exception {
		Map<String, Object> mMap =getOrderrelevance(JsonArray, size, uname, couponid);
		@SuppressWarnings("unchecked")
		List<OrderrelevanceEN> cm = (List<OrderrelevanceEN>) mMap.get("data");//全部数据
		xg(cm, uname);
		return sendTrueData(mMap);
	}

	private Map<String, Object> getdata(List<Long> listlong, Map<String, Object> mp,Long memberid,String couponid) throws Exception {

		StringBuilder ids = new StringBuilder("");
		for (Long long1 : listlong) {
			ids.append(long1 + ",");
		}
		Sql msql = new Sql();
		msql.setSql("select * from ShoppingCard where id in(" + ids.substring(0, ids.length() - 1) + ")");
		List<OrderrelevanceEN> cm = GsonUtil.fromJsonList(
				GsonUtil.toJsonString(
						IBeanUtil.ListMap2ListJavaBean(mShoppingCardService.exeSelectSql(msql), ShoppingCard.class)),
				OrderrelevanceEN.class);
		if (cm.size() == 0) {
			msql.setSql("select * from Orderrelevance where id in(" + ids.substring(0, ids.length() - 1) + ")");
			cm = GsonUtil.fromJsonList(GsonUtil.toJsonString(
					IBeanUtil.ListMap2ListJavaBean(mShoppingCardService.exeSelectSql(msql), ShoppingCard.class)),
					OrderrelevanceEN.class);
		}

		BigDecimal b = new BigDecimal("0");// 价格
		Promotion mPromotion;
		BigDecimal b1 = new BigDecimal("0");// 减免价格
//		BigDecimal b2 = new BigDecimal("0");// 运费险
		Set<String> sa = new HashSet<>();
		for (OrderrelevanceEN orderrelevanceEN : cm) {
			//检查库存是否充足
			StockController.autostock(mMemberService, orderrelevanceEN.getItemid(),orderrelevanceEN.getNum(),orderrelevanceEN.getTitle());
			
			msql.setSql("select * from commodity where id =" + orderrelevanceEN.getItemid());
			List<Map<String, Object>> listmap = mShoppingCardService.exeSelectSql(msql);
			orderrelevanceEN.setColour(listmap.get(0).get("COLOUR") + "");
			orderrelevanceEN.setMysize(listmap.get(0).get("MYSIZE") + "");
			String SUPPLIER = listmap.get(0).get("SUPPLIER") + "";
			orderrelevanceEN.setOneA(SUPPLIER);
			orderrelevanceEN.setOneB(listmap.get(0).get("SUPPLIERNAME") + "");
			Object obj = listmap.get(0).get("ASIZE");
			if(obj!=null)
			orderrelevanceEN.setAsize(Integer.parseInt(obj.toString()));
			sa.add(SUPPLIER);
			// 活动
			mPromotion = PromotionController.getPromotion(mMemberService,listmap.get(0).get("YOUCODE").toString());
			orderrelevanceEN.setmPromotion(mPromotion);

			if (mPromotion != null) {
				if (mPromotion.getType() == 1){
					BigDecimal jj = orderrelevanceEN.getPrice().subtract(orderrelevanceEN.getPrice().multiply(mPromotion.getDiscount().divide(new BigDecimal("10")))).multiply(new BigDecimal(orderrelevanceEN.getNum()));
					b1 = b1.add(jj);
					mPromotion.setReduce(jj);
				}else if (mPromotion.getType() == 2)
					b1 = b1.add(mPromotion.getReduce().multiply(new BigDecimal(orderrelevanceEN.getNum())));
				
				

			}
			// 总价
			b = b.add(new BigDecimal(listmap.get(0).get("PRICE").toString())
					.multiply(new BigDecimal(orderrelevanceEN.getNum())));

		}
//		for (String string : sa) {
//			try {
//				msql.setSql("select * from Postfees where oneid="+getMember(getLogin(string)).getmShop().getId()+" order by id desc");
//				List<Map<String, Object>> list = commodityService.exeSelectSql(msql);
//				if(list.size()!=0)
//					b2=b2.add(new BigDecimal(list.get(0).get("POSTFEE")+""));
//				
//			} catch (Exception e) {
//			}
//			
//			
//		}
		b=b.subtract(b1);
		b=b.add(getpostfee(b));
//		mp.put("postfee", b2);
		mp.put("data", cm);//商品数据
		
//		mp.put("Coupon",CouponController.istrue(b, null, memberid, mOrdersService));
		mp.put("postfee",getpostfee(b));//运费
		
		if(!Stringutil.isBlank(couponid)){//优惠券
			msql.setSql("select * from coupon where id='" +couponid + "'");
			b = b.subtract(new BigDecimal(mOrdersService.exeSelectSql(msql).get(0).get("NUMBERA").toString()));
		}
			
		mp.put("numall", b);//价格
		return mp;

	}
	/**
	 * a:1直接返回邮费，
	 * 2：用户邮费
	 * 3：邮费取反
	 * */
	public static BigDecimal getpostfee(BigDecimal b,int a) {
		if(a==1)
			return new BigDecimal(7);
		if(a==2)
			return new BigDecimal(b.doubleValue()<0?7:0);
		return new BigDecimal(b.doubleValue()>0?7:0);
	}
	private BigDecimal getpostfee(BigDecimal b) {
		return getpostfee(b,2);

	}
	
	/**
	 * 批量下单
	 * 
	 * @param paymenttype
	 *            支付方式1在线支付2货到付款
	 * @param shippingtype
	 *            物流方式1自提2送货3无需物流（虚拟物品）
	 * @param buyermesege
	 *            留言
	 */
	@Transactional
	@RequestMapping(value = "/orders/orderrelevanceAdd", method = RequestMethod.POST)
	@Auth
	public RequestType orderrelevanceAdd(String JsonArray, Integer numsize,Integer paymenttype, Integer shippingtype,
			String buyermesege, String uname, Long myaddressid,String couponid) throws Exception {
		
		return sendFalse("抱歉，你的版本太低了，请升级");
//		
//		if (paymenttype == null || (paymenttype != 1 && paymenttype != 2))
//			return sendFalse("支付方式不合法");
//		if (shippingtype == null || (shippingtype != 1 && shippingtype != 2 && shippingtype != 3))
//			return sendFalse("物流方式不合法");
//		if (myaddressid == null && shippingtype == 2)
//			return sendFalse("收货地址错误");
//		List<Long> listLong = GsonUtil.fromJsonList(JsonArray, Long.class);
//		if (listLong.size() == 0)
//			return sendFalse("商品错误");
//		
//		if(shippingtype == 1||shippingtype == 3)
//			myaddressid=null;
//		
//		
//		// 订单号
//		String ordernumber = UUID.randomUUID().toString().replace("-", "");
//		while (mOrdersService.getByparameter("ordernumber", ordernumber) != null) {
//			// 订单号重复从新生成新的订单号
//			ordernumber = UUID.randomUUID().toString().replace("-", "");
//		}
//		Sql msql = new Sql();
//		List<Map<String, Object>> listmap;
//		Promotion mPromotion;
//		// 商品id
//		StringBuilder ids = new StringBuilder();
//		for (Long mlong : listLong) {
//			ids.append(mlong + ",");
//			// orderrelevance.setOrdernum(ordernumber);
//			// mOrderrelevanceService.add(orderrelevance);
//		}
//		// key:供应商，每个供应商生成一条订单
//		Map<String, List<MCommodity>> orderlist = new HashMap<String, List<MCommodity>>();
//		List<String> ls = mOrdersService.getsupplierbyids(ids.toString().substring(0, ids.length() - 1));
//		for (String string : ls) {
//			orderlist.put(string, new ArrayList<MCommodity>());
//		}
//		List<Orderrelevance> listOrderrelevance;
//		if (listLong.size() == 1) {
//			listOrderrelevance= addonedata(mShoppingCardService,numsize,listLong.get(0), 
//					uname, 1);
//
////			listOrderrelevance = GsonUtil.fromJsonList(GsonUtil.toJsonString(lOrderrelevance), Orderrelevance.class);
////			mOrderrelevanceService.addList(listOrderrelevance);
//		} else {
//			msql.setSql("select * from ShoppingCard where itemid in(" + ids.substring(0, ids.length() - 1) + ") and memberid="+getLogin(uname).getUserid());
//			listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mShoppingCardService.exeSelectSql(msql),
//					Orderrelevance.class);
//		}
//
//		// 对不同供应商商品归类
//		for (Orderrelevance orderrelevance : listOrderrelevance) {
//			Commodity Commodity = IBeanUtil.Map2JavaBean(commodityService.getById(orderrelevance.getItemid()),
//					Commodity.class);
//			MCommodity mcommodity = GsonUtil.fromJsonString(GsonUtil.toJsonString(Commodity), MCommodity.class);
//			mcommodity.setNumsize(orderrelevance.getNum());
//			orderlist.get(mcommodity.getSupplier()).add(mcommodity);
//		}
//		
//		BigDecimal numbermin = new BigDecimal("0");
//		
//		int size=0;
//		// 每个店铺分别生成一条不同的数据
//		for (String string : ls) {
//			// 订单id
//			long mOrdersid = System.currentTimeMillis();
//			Orders mOrders = new Orders();
//			mOrders.setOnephone(string);
//			mOrders.setOrdernumber(ordernumber);
//			mOrders.setId(mOrdersid);
//			BigDecimal b = new BigDecimal("0");
//			BigDecimal b1 = new BigDecimal("0");
//			int number=0;//购买了多少件商品
//			for (MCommodity mMCommodity : orderlist.get(string)) {
//				
//				if(mMCommodity.getPrice().doubleValue()<0)
//					throw new RunException("错误,商品"+mMCommodity.getYoucode()+"价格异常");
//				
//
//				for (Orderrelevance orderrelevance : listOrderrelevance) {
//					// 迭代商品，设置订单号
//					if ((orderrelevance.getItemid() + "").equals(mMCommodity.getId() + "")) {
//						
//						//删除购物车
//						msql.setSql("delete ShoppingCard where itemid="+mMCommodity.getId()+" and memberid="+getLogin(uname).getUserid());
//						
//						mMemberService.execSQL(msql, -1, "");
//						
//						
//						
//						orderrelevance.setId(null);
//						orderrelevance.setOrderid(mOrdersid);
//						orderrelevance.setOrdernum(ordernumber);
//						orderrelevance.setYoucode(mMCommodity.getYoucode());
//						
//						b1 = mMCommodity.getPrice() .multiply(new BigDecimal(orderrelevance.getNum()+""));
//						
//						number=number+orderrelevance.getNum();
//						
//						mPromotion = PromotionController.getPromotion(mMemberService,  mMCommodity.getYoucode());
//						if(mPromotion!=null){
//							if (mPromotion.getType() == 1){
//								orderrelevance.setReduction(mMCommodity.getPrice().subtract(mMCommodity.getPrice().multiply(mPromotion.getDiscount().divide(new BigDecimal("10")))).toString()
//										);
//							}
//								
//							else if (mPromotion.getType() == 2)
//								orderrelevance.setReduction(mPromotion.getReduce().toString());
//
////							msql.setSql("update Orderrelevance set promotionid=" + mPromotion.getId() + ", promotiontitle='"
////									+ mPromotion.getTitle() + "' where "  );
////							mOrdersService.execSQL(msql, 0, mPromotion.getId() + "");
//							
//							orderrelevance.setPromotionid(mPromotion.getId()+"");
//							orderrelevance.setPromotiontitle(mPromotion.getTitle());
//							orderrelevance.setPrice(mMCommodity.getPrice().subtract(new BigDecimal(orderrelevance.getReduction())) );
//							
//							b1 = mMCommodity.getPrice().subtract(new BigDecimal(orderrelevance.getReduction()))  .multiply(new BigDecimal(orderrelevance.getNum()+""));
//						}
//						orderrelevance.setTotalfee(b1);
//						orderrelevance.setPhone(getMember(getLogin(uname)).getPhone());
//						orderrelevance.setMemberid(getLogin(uname).getUserid());
//						orderrelevance.setNickname(getMember(getLogin(uname)).getNickname());
//						orderrelevance.setShippingtype(shippingtype+"");
//						try {
//							mOrderrelevanceService.add(orderrelevance);
//						} catch (Exception e) {
//							orderrelevance.setId(System.currentTimeMillis());
//							mOrderrelevanceService.add(orderrelevance);
//						}
//						
//
//						// 更新库存
//						synchronized (mMCommodity.getYoucode()) {
//							msql.setSql("select num from Stock where code='" + mMCommodity.getYoucode() + "'");
//							listmap = mOrdersService.exeSelectSql(msql);
//							if (listmap.size() == 0 || Integer.parseInt(listmap.get(0).get("NUM").toString())
//									- orderrelevance.getNum() < 0)
//								throw new RunException("抱歉，商品:" + mMCommodity.getName() + "库存不足！");
//
//							msql.setSql("update   Stock set num =num-" + orderrelevance.getNum() + " where code='"
//									+ mMCommodity.getYoucode() + "'");
//							mOrdersService.execSQL(msql, 0, mMCommodity.getYoucode());
//
//						}
//
//					}
//				}
//
//				// 查询活动相关
//				// msql.setSql("select * from (select Promotion.* from Promotion
//				// left join (select shop.memberid,commodity.id"
//				// + " commodityid from Commodity left join Stock on
//				// Commodity.Youcode=stock.code left join shop on "
//				// + "shop.code=stock.shopcode)a on
//				// a.memberid=Promotion.Memberid "
//				// + "where commodityname="+mMCommodity.getId()+" and end >
//				// "+System.currentTimeMillis()+" order by id desc) where
//				// rownum=1 ");
//				// listmap = mOrdersService.exeSelectSql(msql);
//				// if(listmap.size()==0){
//				// msql.setSql("select * from (select * from Promotion where
//				// commodityname='"+mMCommodity.getId()+"' and
//				// end>"+System.currentTimeMillis()+" order by id desc )where
//				// rownum=1");
//				// listmap = mOrdersService.exeSelectSql(msql);
//				// }
//				//
////				msql.setSql("select * from (select * from Promotion where commodityname='" + mMCommodity.getYoucode()
////						+ "' and end>" + System.currentTimeMillis() + " order by id desc )where rownum=1");
////				listmap = mOrdersService.exeSelectSql(msql);
//		
////				mPromotion = IBeanUtil.Map2JavaBean(listmap.get(0), Promotion.class);
//			
//
//			
//				b = b.add(b1);
//			}
//			mOrders.setPayment(b);
//			mOrders.setPaymenttype(paymenttype);
//			mOrders.setStatus(1);
//			mOrders.setAddtime(System.currentTimeMillis());
//			mOrders.setShippingtype(shippingtype);
//			mOrders.setMemberid(getLogin(uname).getUserid());
//			mOrders.setBuyermesege(buyermesege);
//			mOrders.setBuyernick(getMember(getLogin(uname).getUserid()).getNickname());
//			msql.setSql(
//					"select oneid,memberida,shopid,shopname from Friends  left join Clerk on Clerk.Memberid=memberida left join shop on shop.id=shopid where memberidb = "
//							+ getLogin(uname).getUserid());
//			listmap = mOrdersService.exeSelectSql(msql);
//			try {
//				mOrders.setShopname(listmap.get(0).get("SHOPNAME").toString());
//				mOrders.setMemberidsu(Long.valueOf(listmap.get(0).get("MEMBERIDA").toString()));
//				
//				
//				
//				msql.setSql("select id from Identity where type!=0 and memberid="+listmap.get(0).get("MEMBERIDA").toString()
//						);
//				int a =mMemberService.exeSelectSql(msql).size();
//				if(a>0)
//					throw new RunException("抱歉，你的账户不支持你选择的物流方式");
//			} catch (Exception e) {
//				throw new RunException("抱歉，你的账户没有推荐人，不能下单");
//			}
//			
//			mOrders.setShopid(Long.valueOf(listmap.get(0).get("SHOPID").toString()));
//			mOrders.setSystemtype(getMember(getLogin(uname).getUserid()).getUsersystem());
//			mOrders.setAutosystem(0);
//			mOrders.setBuyerrate("0");
//			mOrders.setMyaddressid(myaddressid);
//			
////			Postfees mPostfees = new Postfees();
////			mPostfees.setOrdersid(mOrdersid); //订单id
////			mPostfees.setOneid(Long.valueOf(listmap.get(0).get("ONEID").toString())); //供应商id
//////			mPostfees.setIndemnity(1); //赔付人，0：供应商，1：店铺和店员共同承担
////			mPostfees.setShopid(mOrders.getShopid()); //订单关联的店铺id
////			mPostfees.setClerk(Long.valueOf(listmap.get(0).get("MEMBERIDA").toString())); //购买人所绑定的店员
////			
////			//包邮
////			msql.setSql("select * from Freeshipping where oneid="+Long.valueOf(listmap.get(0).get("ONEID").toString()) +" and end>"+System.currentTimeMillis() );
////			msql.setOrderbykey("ID");
////			msql.setOrderbytype(1);
////			msql.setRows(1);
////			listmap = mOrdersService.exeSelectSql(msql);
////			mPostfees.setIstrue(0);
////			if(listmap.size()==0){
////				mOrders.setPostfee(new BigDecimal("7"));
////				mPostfees.setPostfee(new BigDecimal("7")); //邮费多少
////				mPostfees.setIndemnity(1);
////			}else{
////				mOrders.setPostfee(new BigDecimal(listmap.get(0).get("ORDERS").toString()));
////				mOrders.setPostfee(new BigDecimal(listmap.get(0).get("ORDERS").toString()));
////				try {
////					//多少件包邮
////					if(number>=Integer.valueOf(listmap.get(0).get("FREESHIPPINGSIZE").toString()))
////						mPostfees.setIndemnity(0);
////				} catch (Exception e) {
////					//未多少件包邮
////					mPostfees.setIndemnity(1);
////				}
////				try {
////					//多少钱包邮
////					if(mOrders.getPayment().doubleValue()>=Double.valueOf(listmap.get(0).get("FREESHIPPINGNUMBER").toString()))
////						mPostfees.setIndemnity(0);
////				} catch (Exception e) {
////					//未设置多少钱包邮
////					if(number>=88)
////						mPostfees.setIndemnity(0);
////					else
////						mPostfees.setIndemnity(1);
////				}
////				
////			
////				
////			}
//			if(shippingtype==3)
//				mOrders.setPostfee(new BigDecimal("0"));
//			else
//				mOrders.setPostfee(getpostfee(mOrders.getPayment()));
//			
//			if(mOrders.getPayment().doubleValue()>0){
//				
//				if(myaddressid!=null){
//					Map<String, Object> my = mMyaddressService.getById(myaddressid);
//					Receiver re = IBeanUtil.Map2JavaBean(my, Receiver.class);
//					Long idaa = System.currentTimeMillis();
//					re.setId(idaa);
//					re.setOrderid(mOrders.getId());
//					mReceiverService.add(re);
//					myaddressid=idaa;
//				}
////				mPostfeesService.add(mPostfees);
//				mOrders.setTrajectory(System.currentTimeMillis()+";购买人:"+uname+";");
//				mOrders.setMyaddressid(myaddressid);
//				mOrdersService.add(mOrders);
//				numbermin=numbermin.add(mOrders.getPayment());
//				size=size+1;
//			
//			}
//			
//			else
//				throw new RunException("您购买的商品太少了，再去购买一批吧");
//			
//
//		}
//		
//		if(size!=1)throw new RunException("您当前版本暂不支持购买多个货源的商品，请升级最新版本");
//		
//		if(!Stringutil.isBlank(couponid)){
//			List<Coupon> list = CouponController.istrue(numbermin, Long.valueOf(couponid), getLogin(uname).getUserid(), mOrdersService,null);
//			msql = new Sql();
//			//优惠券已使用
//			msql.setSql(" update Coupon set state=1 where id="+couponid);
//			mOrdersService.execSQL(msql, 0, couponid+"");
//			//订单添加优惠券
//			msql.setSql(" update orders set couponid="+couponid +",payment =payment-"+list.get(0).getNumbera()+" where ordernumber='"+ordernumber+"'");
//			mOrdersService.execSQL(msql, -1, null);
//		}
//		
//		
//		
//		return sendTrueData(ordernumber);
//		// return ordersSign(ordernumber);

	}
	/**
	 * 批量下单2.0版本
	 * 优惠卷分批使用
	 * 
	 */
	@Transactional
	@RequestMapping(value = "/orders/orderrelevanceAdd2", method = RequestMethod.POST)
	@Auth
	public RequestType orderrelevanceAdd2(String JsonArray, Integer numsize,Integer paymenttype, Integer shippingtype,
			String buyermesege, String uname, Long myaddressid,String couponArray,Long orderscardid) throws Exception {
	
		if(!Stringutil.isBlank(RedisUtils.get(stringRedisTemplate, "orderrelevanceAdd2"+uname))){
			Shop5941Application.out("5秒内只能下一单");
			return sendFalse("5秒内只能下一单");
		}
		MemberAuths mMyMember = getMember(uname);
		boolean YS =false;
		boolean PDD =false;
		boolean PDD_true =false;
		boolean MS =false;
		int spsl=0;
		List<Coupon> listCoupon=null;
		List<CouponSu> listCouponSu = new ArrayList<>();
		if(!Stringutil.isBlank(couponArray)){
			listCoupon=new ArrayList<>();
			Set<String> set = new HashSet<String>();
			listCouponSu = GsonUtil.fromJsonList(couponArray, CouponSu.class);
			StringBuilder s = new StringBuilder();
			for (CouponSu mCouponSu : listCouponSu) {
				set.add(mCouponSu.getCouponid());
				s.append(mCouponSu.getCouponid());
				s.append(",");
			}
			
			if(set.size()!=listCouponSu.size())
				return sendFalse("错误，有多个单子选择了同一张优惠卷");
			
			
			//优惠卷
//			List<Long> lstr = GsonUtil.fromJsonList(couponArray, Long.class); 
//			StringBuilder s = new StringBuilder();
//			for (Long long1 : lstr) {
//				
//			}
			Sql msql1 = new Sql();
			msql1.setSql("select * from Coupon where id in("+s.substring(0,s.length()-1)+")");
			listCoupon = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql1), Coupon.class);
			for (Coupon mCoupon : listCoupon) {
				msql1.setSql("select id from orders where couponid="+mCoupon.getId());
				if(mOrdersService.exeSelectSql(msql1).size()!=0)
					return sendFalse("错误，优惠卷"+mCoupon.getTitle()+"已使用");
			}
//			
//			
		}
		
		
		if (paymenttype == null || (paymenttype != 1 && paymenttype != 2))
			return sendFalse("支付方式不合法");
		if (shippingtype == null || (shippingtype != 1 && shippingtype != 2 && shippingtype != 3))
			return sendFalse("物流方式不合法");
		if (myaddressid == null && shippingtype == 2)
			return sendFalse("收货地址错误");
		List<Long> listLong = GsonUtil.fromJsonList(JsonArray, Long.class);
		if (listLong.size() == 0)
			return sendFalse("商品错误");
		
		if(shippingtype == 1||shippingtype == 3)
			myaddressid=null;
		
		
		// 订单号
		String ordernumber = UUID.randomUUID().toString().replace("-", "");
		while (mOrdersService.getByparameter("ordernumber", ordernumber) != null) {
			// 订单号重复从新生成新的订单号
			ordernumber = UUID.randomUUID().toString().replace("-", "");
		}
		Sql msql = new Sql();
		List<Map<String, Object>> listmap;
		Promotion mPromotion;
		// 商品id
		StringBuilder ids = new StringBuilder();
		for (Long mlong : listLong) {
			ids.append(mlong + ",");
			// orderrelevance.setOrdernum(ordernumber);
			// mOrderrelevanceService.add(orderrelevance);
		}
		// key:供应商，每个供应商生成一条订单
		Map<String, List<MCommodity>> orderlist = new HashMap<String, List<MCommodity>>();
		List<String> ls = mOrdersService.getsupplierbyids(ids.toString().substring(0, ids.length() - 1));
		for (String string : ls) {
			orderlist.put(string, new ArrayList<MCommodity>());
		}
		List<Orderrelevance> listOrderrelevance;
		if (listLong.size() == 1) {
			listOrderrelevance= addonedata(mShoppingCardService,numsize,listLong.get(0), 
					uname, 1);
			
//			listOrderrelevance = GsonUtil.fromJsonList(GsonUtil.toJsonString(lOrderrelevance), Orderrelevance.class);
//			mOrderrelevanceService.addList(listOrderrelevance);
		} else {
			msql.setSql("select * from ShoppingCard where itemid in(" + ids.substring(0, ids.length() - 1) + ") and memberid="+getLogin(uname).getUserid());
			listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mShoppingCardService.exeSelectSql(msql),
					Orderrelevance.class);
		}
		
		// 对不同供应商商品归类
		for (Orderrelevance orderrelevance : listOrderrelevance) {
			Commodity Commodity = IBeanUtil.Map2JavaBean(commodityService.getById(orderrelevance.getItemid()),
					Commodity.class);
			MCommodity mcommodity = GsonUtil.fromJsonString(GsonUtil.toJsonString(Commodity), MCommodity.class);
			mcommodity.setNumsize(orderrelevance.getNum());
			orderlist.get(mcommodity.getSupplier()).add(mcommodity);
		}
		
//		BigDecimal numbermin = new BigDecimal("0");
		
		@SuppressWarnings("unchecked")
		List<GoldcoinDefault> listGoldcoinDefault=(List<GoldcoinDefault>) mDefault.getALL(new GoldcoinDefault(),1,2);

		// 每个店铺分别生成一条不同的数据
		for (String string : ls) {
			// 订单id
			long mOrdersid = System.currentTimeMillis();
			Orders mOrders = new Orders();
			mOrders.setOnephone(string);
			mOrders.setOrdernumber(ordernumber);
			mOrders.setId(mOrdersid);
			BigDecimal b = new BigDecimal("0");
			BigDecimal b1 = new BigDecimal("0");
			int number=0;//购买了多少件商品
			for (MCommodity mMCommodity : orderlist.get(string)) {
				msql.setSql("select type from Commodity where id="+mMCommodity.getId());
				if(!mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString().equals("1"))
					throw new RunException("错误,商品"+mMCommodity.getName()+"已下架");
				
				if(mMCommodity.getPrice().doubleValue()<0)
					throw new RunException("错误,商品"+mMCommodity.getYoucode()+"价格异常");
				
				
				for (Orderrelevance orderrelevance : listOrderrelevance) {
					// 迭代商品，设置订单号
					if ((orderrelevance.getItemid() + "").equals(mMCommodity.getId() + "")) {
						
						//删除购物车
						msql.setSql("delete ShoppingCard where itemid="+mMCommodity.getId()+" and memberid="+getLogin(uname).getUserid());
						
						mMemberService.execSQL(msql, -1, "");
						
						
						
						orderrelevance.setId(null);
						orderrelevance.setOrderid(mOrdersid);
						orderrelevance.setOrdernum(ordernumber);
						orderrelevance.setYoucode(mMCommodity.getYoucode());
						
						b1 = mMCommodity.getPrice() .multiply(new BigDecimal(orderrelevance.getNum()+""));
						
						number=number+orderrelevance.getNum();
						
						mPromotion = PromotionController.getPromotion(mMemberService,  mMCommodity.getYoucode());
						if(mPromotion!=null){
							if (mPromotion.getType() == 1){
								orderrelevance.setReduction(mMCommodity.getPrice().subtract(mMCommodity.getPrice().multiply(mPromotion.getDiscount().divide(new BigDecimal("10")))).toString()
										);
							}
							
							else if (mPromotion.getType() == 2)
								orderrelevance.setReduction(mPromotion.getReduce().toString());
							
//							msql.setSql("update Orderrelevance set promotionid=" + mPromotion.getId() + ", promotiontitle='"
//									+ mPromotion.getTitle() + "' where "  );
//							mOrdersService.execSQL(msql, 0, mPromotion.getId() + "");
							
							orderrelevance.setPromotionid(mPromotion.getId()+"");
							orderrelevance.setPromotiontitle(mPromotion.getTitle());
							orderrelevance.setPrice(mMCommodity.getPrice().subtract(new BigDecimal(orderrelevance.getReduction())) );
							
							b1 = mMCommodity.getPrice().subtract(new BigDecimal(orderrelevance.getReduction()))  .multiply(new BigDecimal(orderrelevance.getNum()+""));
						}
						orderrelevance.setTotalfee(b1);
						orderrelevance.setPhone(mMyMember.getPhone());
						orderrelevance.setMemberid(mMyMember.getId());
						orderrelevance.setNickname(mMyMember.getNickname());
						orderrelevance.setShippingtype(shippingtype+"");
						if(mMCommodity.getYs()==1)YS=true;
						if(mMCommodity.getPdd()==1)PDD=true;
						if(mMCommodity.getMs()==1)MS=true;
						
						if(MS){
							if(mMCommodity.getMs()==0)
								throw new RunException("错误，秒杀商品不可与普通商品同时下单，请分2次购买");
							
							
							msql.setSql("update ms set num=num-"+orderrelevance.getNum() +" where code='"+mMCommodity.getYoucode()+"' and end>"+System.currentTimeMillis());
							mOrderrelevanceService.execSQL(msql);
							
							synchronized (this) {
								msql.setSql("select num from  ms  where code='"+mMCommodity.getYoucode()+"'");
								if(Integer.parseInt(mOrderrelevanceService.exeSelectSql(msql).get(0).get("NUM").toString())<0){
									throw new RunException("商品数量不足");
								}
							}
							
						}
						if(YS){
							if(mMCommodity.getYs()==0)
								throw new RunException("错误，预售商品不可与普通商品同时下单，请分2次购买");
							
							msql.setSql("update ys set num=num-"+orderrelevance.getNum() +" where code='"+mMCommodity.getYoucode()+"' and end>"+System.currentTimeMillis());
							mOrderrelevanceService.execSQL(msql);
							
							synchronized (this) {
								msql.setSql("select num from  ys  where code='"+mMCommodity.getYoucode()+"'");
								if(Integer.parseInt(mOrderrelevanceService.exeSelectSql(msql).get(0).get("NUM").toString())<0){
									throw new RunException("商品数量不足");
								}
							}
							
						}
						if(PDD){
							if(mMCommodity.getPdd()==0)
								throw new RunException("错误，团购商品不可与普通商品同时下单，请分2次购买");
							
//							Orderrelevance mOrderrelevance1 =new Orderrelevance();
							orderrelevance.setBh(mMCommodity.getBh());
							
							try {
								if(mMCommodity.getPtfa()==ptfaenum.DDL.getValue()){

									msql.setSql("select count(*) A from (select orderid,nvl(count(*),0)a from Orderrelevance where bh='"+mMCommodity.getBh()+"' group by orderid) ");
									if((mMCommodity.getPtsl()-1)<=Integer.valueOf(mOrderrelevanceService.exeSelectSql(msql).get(0).get("A").toString())){
										PDD_true=true;
									}
								
								}else if(mMCommodity.getPtfa()==ptfaenum.RS.getValue()){

									msql.setSql("select count(*) A from (select memberid,nvl(count(*),0)a from Orderrelevance where bh='"+mMCommodity.getBh()+"' group by memberid) ");
									if((mMCommodity.getPtsl()-1)<=Integer.valueOf(mOrderrelevanceService.exeSelectSql(msql).get(0).get("A").toString())){
										PDD_true=true;
									}
								
								}else if(mMCommodity.getPtfa()==ptfaenum.SPSL.getValue()){
									spsl=spsl+orderrelevance.getNum();
									msql.setSql("select nvl(sum(num),0)a from Orderrelevance where bh='"+mMCommodity.getBh()+"'  ");
									if(mMCommodity.getPtsl()<=spsl+(Integer.valueOf(mOrderrelevanceService.exeSelectSql(msql).get(0).get("A").toString()))){
										PDD_true=true;
									}
								
								}
								
								if(PDD_true){
									msql.setSql("update orders set b='' where id in(select orderid from Orderrelevance where bh='"+mMCommodity.getBh()+"')");
									mOrderrelevanceService.execSQL(msql);
								}
							} catch (Exception e) {
							}
						
							
						}
						/**添加积分star*/
						Integer page_j= msql.getPage();
						Integer rows_j= msql.getRows();
						 msql.setPage(1);
						 msql.setRows(2);
						msql.setSql("select * from Goldcoinkey where youcode='"+orderrelevance.getYoucode()+"' order by id desc");
						List<GoldcoinKey> listGoldcoinkey = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), GoldcoinKey.class);
						GoldcoinV mGoldcoinV =new GoldcoinV();
						mGoldcoinV.setMemberid(mMyMember.getId());
						mGoldcoinV.setTitle("购买商品:"+orderrelevance.getTitle()+"获得");
						if(listGoldcoinkey.size()==0){
							if(listGoldcoinDefault.size()!=0){
								BigDecimal ba = orderrelevance.getTotalfee().multiply(new BigDecimal(listGoldcoinDefault.get(0).getPercentage()));
								mGoldcoinV.setNum((int)(double)ba.divide(new BigDecimal(100)).doubleValue());
							}
						}else{
							mGoldcoinV.setNum(listGoldcoinkey.get(0).getNum()*orderrelevance.getNum());
							
						}
						mGoldcoinV.setB_y(mOrdersid+"");
						mGoldcoinV.setIstrue(0);
						mValue.add(mGoldcoinV);
						msql.setPage(page_j);
						msql.setRows(rows_j);
						
						/**添加积分end*/
						
						try {
							mOrderrelevanceService.add(orderrelevance);
						} catch (Exception e) {
							orderrelevance.setId(System.currentTimeMillis());
							mOrderrelevanceService.add(orderrelevance);
						}
						//袜子核销卷
						if(mMyMember.isClerk()){
							if(mMCommodity.getCommoditykeyid().toString().equals(MyParameter.clsid)){
								msql.setSql("update shop set csl=nvl(csl,0)+"+(orderrelevance.getNum()*MyParameter.clssize)+" where id="+getShop(uname).getId());
								mOrdersService.execSQL(msql);
							}
						}
						
						// 更新库存
						synchronized (mMCommodity.getYoucode()) {
							msql.setSql("select num from Stock where code='" + mMCommodity.getYoucode() + "'");
							listmap = mOrdersService.exeSelectSql(msql);
							if (listmap.size() == 0 || Integer.parseInt(listmap.get(0).get("NUM").toString())
									- orderrelevance.getNum() < 0)
								throw new RunException("抱歉，商品:" + mMCommodity.getName() + "库存不足！");
							
							msql.setSql("update   Stock set num =num-" + orderrelevance.getNum() + " where code='"
									+ mMCommodity.getYoucode() + "'");
							mOrdersService.execSQL(msql, 0, mMCommodity.getYoucode());
							
						}
						
					}
				}
				
				
				
				b = b.add(b1);
			}
			if(number==0)continue;
			mOrders.setPayment(b);
			mOrders.setPaymenttype(paymenttype);
			mOrders.setStatus(1);
			mOrders.setAddtime(System.currentTimeMillis());
			mOrders.setShippingtype(shippingtype);
			mOrders.setMemberid(mMyMember.getId());
			mOrders.setBuyermesege(buyermesege);
			mOrders.setBuyernick(mMyMember.getNickname());
			msql.setSql(
					"select oneid,memberida,shopid,shopname from Friends  left join Clerk on Clerk.Memberid=memberida left join shop on shop.id=shopid where memberidb = "
							+ getLogin(uname).getUserid());
			listmap = mOrdersService.exeSelectSql(msql);
			try {
				mOrders.setShopname(listmap.get(0).get("SHOPNAME").toString());
				mOrders.setMemberidsu(Long.valueOf(listmap.get(0).get("MEMBERIDA").toString()));
				
				
			} catch (Exception e) {
				throw new RunException("抱歉，你的账户没有推荐人，不能下单");
			}
			if(shippingtype==3){
				msql.setSql("select id from Identity where type!=0 and memberid="+listmap.get(0).get("MEMBERIDA").toString()
						);
				int a =mMemberService.exeSelectSql(msql).size();
				if(a>0)
					throw new RunException("抱歉，你的账户不支持你选择的物流方式");
			}
			
			
			mOrders.setShopid(Long.valueOf(listmap.get(0).get("SHOPID").toString()));
			mOrders.setSystemtype(mMyMember.getUsersystem());
			mOrders.setAutosystem(0);
			mOrders.setBuyerrate("0");
			mOrders.setMyaddressid(myaddressid);
			
			if(shippingtype==3)
				mOrders.setPostfee(new BigDecimal("0"));
			else
				mOrders.setPostfee(getpostfee(mOrders.getPayment()));
			
			if(mOrders.getPayment().doubleValue()>0){
				
				Long idaa1=null;
				
				if(myaddressid!=null){
					Map<String, Object> my = mMyaddressService.getById(myaddressid);
					Receiver re = IBeanUtil.Map2JavaBean(my, Receiver.class);
					Long idaa = System.currentTimeMillis();
					re.setId(idaa);
					re.setOrderid(mOrders.getId());
					mReceiverService.add(re);
					idaa1=idaa;
				}
//				mPostfeesService.add(mPostfees);
				mOrders.setTrajectory(System.currentTimeMillis()+";购买人:"+uname+";");
				mOrders.setMyaddressid(idaa1);
				
			
				
				//优惠券
				if(listCoupon!=null){
					boolean bkey=false;
					for (CouponSu mCouponSu : listCouponSu) {
						if(mCouponSu.getOnephone().equals(string)||mCouponSu.getOnephone().equals("13333333333")){
							for (Coupon coupon : listCoupon) {
								if(coupon.getNumbera().doubleValue()<1){
									bkey=true;
									if(listCoupon.size()!=1){
										throw new RunException("一个单子只能使用一张打折卷");
									}
									mOrders.setPayment(mOrders.getPayment().multiply(coupon.getNumbera()));
								}else{
									if(bkey){
										throw new RunException("打折卷与满减卷不能同时使用");
									}
									if(coupon.getId().toString().equals(mCouponSu.getCouponid())){
										mOrders.setPayment(mOrders.getPayment().subtract(coupon.getNumbera()));
										if(mOrders.getPayment().doubleValue()<=0)
											mOrders.setPayment(new BigDecimal("0.01"));
//										mOrders.setCouponid(mOrders.getCouponid()+";"+coupon.getId());
										mOrders.setCouponid(coupon.getId()+"");
									}
								}
								
							
							}
							
						
						}
					}
					
					
					if(!Stringutil.isBlank(mOrders.getCouponid())){
						mOrders.setCouponid(mOrders.getCouponid().substring(0,mOrders.getCouponid().length()-1));
					}
					
//					
//					for (Coupon coupon : listCoupon) {
//						if(coupon.getOnephone().equals(string)||coupon.getOnephone().equals("13333333333")){
//								if(coupon.getOnephone().equals(string)){
//									mOrders.setPayment(mOrders.getPayment().subtract(coupon.getNumbera()));
//									if(mOrders.getPayment().doubleValue()<=0)
//										mOrders.setPayment(new BigDecimal("0.01"));
//									
//									mOrders.setCouponid(coupon.getId()+"");
//								}
//							
//						
//						}
//					}	
				}
				
				//实物卡
				if(orderscardid!=null){
					msql=new Sql();
					msql.setRows(1);
					msql.setSql("select * from OrdersCard where id="+orderscardid);
					OrdersCard mOrdersCard=null;
					try {
						 mOrdersCard =IBeanUtil.Map2JavaBean(mOrdersService.exeSelectSql(msql).get(0), OrdersCard.class);
					} catch (Exception e) {
						throw new RunException("实物卡消费错误");
					}
					if(mOrdersCard==null)	throw new RunException("实物卡消费错误");
					if(!mMyMember.getId().toString().equals(mOrdersCard.getMemberid().toString()))throw new RunException("实物卡不属于你");
					
					if(mOrdersCard.getStar()>System.currentTimeMillis())throw new RunException("使用的实物卡还未到可以使用的时间");
					if(mOrdersCard.getEnd()<System.currentTimeMillis())throw new RunException("使用的实物卡已过期");
					
					msql.setSql("select code from OrdersCardRule order by id desc");
					List<Map<String, Object>> listOrdersCardRule =	mOrdersService.exeSelectSql(msql);
					if(listOrdersCardRule.size()!=0){
						if(!Stringutil.isBlank(listOrdersCardRule.get(0).get("CODE"))){
							msql.setSql("select num from OrdersCardRule where min<"+mOrders.getPayment()+" and code='"+listOrdersCardRule.get(0).get("CODE").toString()+"' order by min desc ");
							 listOrdersCardRule =	mOrdersService.exeSelectSql(msql);
							 if(listOrdersCardRule.size()!=0){
								 mOrders.setPayment( mOrders.getPayment().subtract(new BigDecimal(listOrdersCardRule.get(0).get("NUM").toString())));
								 mOrdersCard.setNum(mOrdersCard.getNum().subtract(new BigDecimal(listOrdersCardRule.get(0).get("NUM").toString())));
								 if(mOrdersCard.getNum().doubleValue()<0)throw new RunException("实物卡余额不足");
								 mOrders.setTrajectory(mOrders.getTrajectory()+";"+System.currentTimeMillis()+";实物卡优惠号:"+mOrdersCard.getCode()+";");
							 }
						}
					}
					
				
					
					
					
				}
				
				mOrders.setAutosystem(null);
				if(MS){
					mOrders.setB(Orders_myp.MS.toString());
					mOrders.setTrajectory(System.currentTimeMillis()+";秒杀:"+uname+";");
				}
				if(YS){
					mOrders.setB(Orders_myp.YS.toString());
					mOrders.setTrajectory(System.currentTimeMillis()+";预售:"+uname+";");
				}
					
				if(PDD){
					if(!PDD_true)
						mOrders.setB(Orders_myp.PDD.toString());
					mOrders.setTrajectory(System.currentTimeMillis()+";拼团:"+uname+";");
				}
				if(PDD_true){
					mOrders.setB("");
				}
				mOrdersService.add(mOrders);
				
				
				if(!Stringutil.isBlank(mOrders.getCouponid())){
					msql=new Sql();
					msql.setSql("update Coupon set state=1 where id="+mOrders.getCouponid());
				}
//				numbermin=numbermin.add(mOrders.getPayment());
				
				
			}
			
			else
				throw new RunException("您购买的商品太少了，再去购买一批吧");
			
			
			
			
		}
		RedisUtils.set(stringRedisTemplate, "orderrelevanceAdd2"+uname,uname,5L);
		
		return sendTrueData(ordernumber);
		// return ordersSign(ordernumber);
		
	}
	public static String getordertype(String column,String alias){
		return "decode("+column+",1,'未付款',2,'已付款',3,'已发货',4,'已完成',5,'已关闭',6,'已到货',7,'已出货',8,'已接单',9,'已拒单',10,'已退单',11,'已退款',20,'拒绝退货',21,'申请退货',22,'同意退货',23,'已退仓',24,'已验收')"+alias+" ";
	}



//	/**
//	 * 单个商品下单
//	 */
//	@Transactional
//	@RequestMapping(value = "/orders/addone", method = RequestMethod.POST)
//	@Auth
//	public RequestType addone(Long id, Integer paymenttype, Integer shippingtype, String buyermesege, String uname,
//			int index, Long myaddressid) throws Exception {
//		if (paymenttype == null || (paymenttype != 1 && paymenttype != 2))
//			return sendFalse("支付方式不合法");
//		if (shippingtype == null || (shippingtype != 1 && shippingtype != 2 && shippingtype != 3))
//			return sendFalse("物流方式不合法");
//
//		List<Orderrelevance> lOrderrelevance = addonedata(id, paymenttype, shippingtype, buyermesege, uname, index,
//				myaddressid);
//		if(lOrderrelevance.size()==0)
//			return sendFalse("商品错误");
//		List<Long> listlong = new ArrayList<Long>();
//		listlong.add(lOrderrelevance.get(0).getItemid());
//		return orderrelevanceAdd(GsonUtil.toJsonString(listlong), paymenttype, shippingtype, buyermesege, uname,
//				myaddressid);
//	}

	public static List<Orderrelevance> addonedata(BaseService mBaseService,Integer size,Long id,  
			String uname, int index) throws Exception {

		Sql msql = new Sql();
		msql.setSql("select * from commodity where id ='" + id + "'");
		Commodity mCommodity = IBeanUtil
				.ListMap2ListJavaBean(mBaseService.exeSelectSql(msql), Commodity.class).get(0);

		List<Orderrelevance> lOrderrelevance = new ArrayList<Orderrelevance>();
		Orderrelevance mOrderrelevance = new Orderrelevance();
		Long mOrderrelevanceid = System.currentTimeMillis();
		mOrderrelevance.setId(mOrderrelevanceid);
		mOrderrelevance.setItemid(id);
		mOrderrelevance.setNum((size==null||size<1)?1:size);
		mOrderrelevance.setTotalfee(mCommodity.getPrice());
		mOrderrelevance.setTitle(mCommodity.getName());
		mOrderrelevance.setPrice(mCommodity.getPrice());
		mOrderrelevance.setPicpath(mCommodity.getMainimage().split(";")[index - 1]);
		mOrderrelevance.setType1(mCommodity.getLargeclass());
		mOrderrelevance.setType2(mCommodity.getInclass());
		mOrderrelevance.setType3(mCommodity.getSmallclass());
		mOrderrelevance.setType4(mCommodity.getFineclass());
		lOrderrelevance.add(mOrderrelevance);
		return lOrderrelevance;
	}

//	/**
//	 * 购物车下单
//	 * 
//	 * @param paymenttype
//	 *            支付方式1在线支付2货到付款
//	 * @param shippingtype
//	 *            物流方式1自提2送货3无需物流（虚拟物品）
//	 * @param buyermesege
//	 *            留言
//	 */
//	@Transactional
//	@RequestMapping(value = "/orders/shoppingcardadd", method = RequestMethod.POST)
//	@Auth
//	public RequestType shoppingcardadd(Integer paymenttype, Integer shippingtype, String buyermesege, String uname,
//			Long myaddressid) throws Exception {
//		if (paymenttype == null || (paymenttype != 1 && paymenttype != 2))
//			return sendFalse("支付方式不合法");
//		if (shippingtype == null || (shippingtype != 1 && shippingtype != 2 && shippingtype != 3))
//			return sendFalse("物流方式不合法");
//		ShoppingCard mShoppingCard = new ShoppingCard();
//		mShoppingCard.setMemberid(getLogin(uname).getUserid());
//		@SuppressWarnings("unchecked")
//		List<ShoppingCard> ls = (List<ShoppingCard>) mShoppingCardService.getALL(mShoppingCard);
//		if (ls.size() == 0)
//			return sendFalse("购物车是空的");
//
//		return orderrelevanceAdd(GsonUtil.toJsonString(ls), paymenttype, shippingtype, buyermesege, uname, myaddressid);
//	}

	/**
	 * 修改订单物流方式
	 */
	@RequestMapping(value = "/orders/updateshippingtype", method = RequestMethod.POST)
	@Auth
	public RequestType updateshippingtype(Long id, Integer shippingtype, String uname) throws Exception {
		if (shippingtype == null || (shippingtype != 1 && shippingtype != 2 && shippingtype != 3))
			return sendFalse("物流方式不合法");

		Orders or = new Orders();
		or.setMemberid(getLogin(uname).getUserid());
		or.setId(id);
		@SuppressWarnings("unchecked")
		List<Orders> listmap = (List<Orders>) mOrdersService.getALL(or);
		if (listmap.size() == 0)
			return sendFalse("订单不存在");
		if (listmap.get(0).getStatus() != 2)
			return sendFalse("抱歉，当前订单不能修改物流");
		or.setShippingtype(shippingtype);
		mOrdersService.updateBySelect(or);

		return sendTrueMsg("更新成功");

	}

	/**
	 * 修改订单留言
	 */
	@RequestMapping(value = "/orders/updatebuyermesege", method = RequestMethod.POST)
	@Auth
	public RequestType updatebuyermesege(Long id, String buyermesege, String uname) throws Exception {
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
		if (or.getStatus() != 2)
			return sendFalse("抱歉，当前订单不能修改留言");
		or.setBuyermesege(buyermesege);
		mOrdersService.updateBySelect(or);

		return sendTrueMsg("更新成功");

	}

	/**
	 * 发货
	 */
	@RequestMapping(value = "/orders/Delivergoods", method = RequestMethod.POST)
	@Auth(orders=true)
	public RequestType Delivergoods(Long id, String uname,String postfeenumber) throws Exception {
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
	

		if (or.getStatus() != 2&&or.getStatus()!=6&&or.getStatus()!=8)
			return sendFalse("只有已付款未发货的订单才能做次操作");
		
		if(or.getStatus()==8){
			or.setPostfeenumber(postfeenumber);
			if(Stringutil.isBlank(postfeenumber))
				return sendFalse("运单号不可为空");
//			if(mOrdersService.getByparameter("postfeenumber", postfeenumber)!=null)
//				return sendFalse("运单号已存在，是不是不小心输错了");
		}

		or.setStatus(3);
		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";订单已发货;";
		or.setTrajectory(str);
		mOrdersService.updateBySelect(or);
		
		Message.start(mSystemMessageService, "物流助手", "到货通知", "您的订单号为:"+id+"的商品已发货啦", or.getMemberid());

		return sendTrueMsg("操作成功");

	}

	/**
	 * 收货
	 */
	@RequestMapping(value = "/orders/Collectgoods", method = RequestMethod.POST)
//	@Auth(orders=true)
	public RequestType Collectgoods(Long id, String uname) throws Exception {
		
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");

		if (or.getStatus() != 3&&or.getStatus() != 4)
			return sendFalse("只有已发货的订单才能做次操作");
		or.setStatus(4);
		or.setB1(uname);
		
		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";订单已签收;";
		or.setTrajectory(str);
		mOrdersService.updateBySelect(or);
		
		Message.start(mSystemMessageService, "物流助手", "到货通知", "您的订单号为:"+id+"的商品已经确认签收，欢迎再次购买", or.getMemberid());

		return sendTrueMsg("操作成功");

	}
	/**
	 * 出货
	 */
	@RequestMapping(value = "/orders/Delivergoods1", method = RequestMethod.POST)
	@Auth(orders=true)
	public RequestType Delivergoods1(Long id, String uname,String postfeenumber) throws Exception {
		if(Stringutil.isBlank(postfeenumber))
			return sendFalse("运单号不可为空");
//		if(mOrdersService.getByparameter("postfeenumber", postfeenumber)!=null)
//			return sendFalse("运单号已存在，是不是不小心输错了");
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
		
		if(or.getShippingtype()!=1)
			return sendFalse("错误，该订单不属于自提，请直接给用户发货");
		
		if (or.getStatus() != 8)
			return sendFalse("请先接单");
		
		if((or.getShopid()+"").equals(getMember(getLogin(uname)).getmShop().getId()+""))
			return sendFalse("错误，该用户直属于你，你不能给自己发货，请直接给用户发货");

		or.setStatus(7);
//		if(getMember(getLogin(uname)).getmShop()==null||getMember(getLogin(uname)).getmShop().getId()==null){
//			return sendFalse("只有店铺才能进行此操作");
//		}
		or.setB(getMember(getLogin(uname)).getmShop().getShopname());
		
		or.setPostfeenumber(postfeenumber);

		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";订单已出仓库;";
		or.setTrajectory(str);
		mOrdersService.updateBySelect(or);
		
		Message.start(mSystemMessageService, "物流助手", "发货通知", "您的订单号为:"+id+"的商品已经出货", or.getMemberid());

		return sendTrueMsg("操作成功");

	}
	/**
	 * 接单
	 */
	@RequestMapping(value = "/orders/Delivergoods2", method = RequestMethod.POST)
	@Auth(orders=true)
	public RequestType Delivergoods2(Long id, String uname) throws Exception {
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
		
		if (or.getStatus() != 2)
			return sendFalse("只有已付款未发货的订单才能做次操作");
		
		
		or.setStatus(8);
//		if(getMember(getLogin(uname)).getmShop()==null||getMember(getLogin(uname)).getmShop().getId()==null){
//			return sendFalse("只有店铺才能进行此操作");
//		}
		if(!Stringutil.isBlank(or.getB())){
			if(or.getB().equals(Orders_myp.PDD.toString())){
				throw new RunException("错误，此订单为拼团订单，还未满足拼团条件，不能接单");
			}
		}
		
		or.setB(uname);
		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";商家已接单;";
		or.setTrajectory(str);
		mOrdersService.updateBySelect(or);
		
		Message.start(mSystemMessageService, "物流助手", "接单通知", "您的订单号为:"+id+"的商品商家已经接单，正在分解订单", or.getMemberid());
		
		
		//心跳任务
		String[] sa = new String[4];
		sa[0]="0";//任务id
		sa[1]="-1";//延时时间，毫秒，空为默认值
		sa[2]=id+"";//数据id
		sa[3]=or.getMemberid()+"";//附加数据
		TSystem.start(sa, mTaskService);
		
		return sendTrueMsg("操作成功");
		
	}
	/**
	 * 拒单
	 */
	@RequestMapping(value = "/orders/Delivergoods3", method = RequestMethod.POST)
	@Auth(orders=true)
	public RequestType Delivergoods3(Long id, String uname) throws Exception {
		
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
		
		if (or.getStatus() != 2&&or.getStatus() != 8)
			return sendFalse("只有已付款未发货的订单才能做次操作");
		
		
		or.setStatus(9);
//		if(getMember(getLogin(uname)).getmShop()==null||getMember(getLogin(uname)).getmShop().getId()==null){
//			return sendFalse("只有店铺才能进行此操作");
//		}
		or.setB(getMember(getLogin(uname)).getmShop().getShopname());
		
		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";商家已拒单;";
		or.setTrajectory(str);
		mOrdersService.updateBySelect(or);
		
		Message.start(mSystemMessageService, "物流助手", "通知", "抱歉，您的订单号为:"+id+"的订单被商家拒绝了", or.getMemberid());
		
		return sendTrueMsg("操作成功");
		
	}
	/**
	 *到货
	 */
	@RequestMapping(value = "/orders/Collectgoods1", method = RequestMethod.POST)
	@Auth(orders=true)
	public RequestType Collectgoods1(Long id, String uname) throws Exception {
		Orders or = mOrdersService.getById(id,Orders.class);
		if (or==null)
			return sendFalse("订单不存在");
		
		if (or.getStatus() != 7)
			return sendFalse("只有已付款未发货的订单才能做次操作");
		
		or.setStatus(6);
		
//		if(getMember(getLogin(uname)).getmShop()==null||getMember(getLogin(uname)).getmShop().getId()==null){
//			return sendFalse("只有店铺才能进行此操作");
//		}
		try {
			getMember(getLogin(uname)).getmShop().getShopname();
			or.setB(uname);
		} catch (Exception e) {
			//店员
			Sql msql = new Sql();
			msql.setSql("select shopid from clerk where memberid="+getLogin(uname).getUserid());
			if(mOrdersService.exeSelectSql(msql).size()==0)
				throw new RunException("抱歉，权限不足");
			or.setB(uname);
		}
		
		String str = or.getTrajectory();
		if(str==null)str="";
		str=str+System.currentTimeMillis()+";订单已到自提点;";
		or.setTrajectory(str);
		
		mOrdersService.updateBySelect(or);
		Message.start(mSystemMessageService, "物流助手", "到货通知", "您的订单号为:"+id+"的商品已经到提货的店铺啦", or.getMemberid());
		
		
		
		phonemessageaAdd(getMember(or.getMemberid()).getPhone(), or.getId().toString().substring(9,13));
		
		return sendTrueMsg("操作成功");
		
	}

	/**
	 * 查询我的订单列表
	 */
	@RequestMapping(value = "/orders/selectbymemberid", method = RequestMethod.POST)
	@Auth
	public RequestType selectbymemberid(Integer status, String uname, Integer rows, Integer page,Integer type) throws Exception {
		
		return selectbytype(null,null,status, uname, rows, page, type==null?1:type, type==null?null:uname);         
	}
	/**
	 * 查询导购业绩
	 */
	@RequestMapping(value = "/orders/selectbyclerk", method = RequestMethod.POST)
	@Auth
	public RequestType selectbyclerk(String uname,String code,String clerkphone, Integer rows, Integer page,Integer type,Long star,Long end) throws Exception {
		Sql msql = new Sql();
		String sql;
		MemberAuths mMemberAuths= getMember(uname);
		Shop mshop =mMemberAuths.getmShop();
		sql = "select orders.*,shop.code,uname from orders left join Friends on memberid=memberidb left join member on member.id = memberida left join shop on orders.shopid=shop.id where 1=1 ";
		if(mMemberAuths.getSuperadmin()!=1){
			if(mshop==null)
				return sendFalse("权限不足");
			else
				code=mshop.getCode();
			
			if(!Stringutil.isBlank(clerkphone)){
				msql.setSql("select id from clerk where memberid="+getMember(clerkphone)+" and shopid="+mshop.getId());
				if(mMemberService.exeSelectSql(msql).size()==0)
					return sendFalse("权限不足");
			}
		
		}
			if(!Stringutil.isBlank(code))
				sql = sql + " and shop.code='"+code+"'";
			if(!Stringutil.isBlank(clerkphone))
				sql = sql + " and member.uname='"+clerkphone+"'";
			
			if(star!=null)
				sql=sql+" and orders.id>"+star;
			if(end!=null)
				sql=sql+" and orders.id<"+end;
			
			
			msql.setSql(sql);
			msql.setPage(page);
			msql.setRows(rows);
			
		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			map.put("MEMBERPHONE",getMember(Long.valueOf(map.get("MEMBERID").toString())).getUname());
		}
		return sendTrueData(listmap);         
	}

	// /**
	// * 店铺订单列表
	// * */
	// @RequestMapping(value ="/orders/selectbyshopid", method =
	// RequestMethod.POST)
	// @Auth
	// public RequestType selectbyshopid(Integer status,String uname,Integer
	// rows,Integer page) throws Exception{
	// return selectbytype(status, uname, rows, page, 2);
	// }
	/**
	 * type 0:全部，必须是超级管理员 1：用户， 2：商户
	 */
	@Auth
	@RequestMapping(value = "/orders/selectbytype", method = RequestMethod.POST)
	public RequestType selectbytype(String ordernumber,Long id,Integer status, String phone, Integer rows, Integer page, Integer type,
			String uname) throws Exception {
//		if(status!=null&&status==9)
//			status=null;
		String sql = "select autosystem,paymenttype,couponid,nvl(postfeenumber,'暂无')postfeenumber,status statustype,shippingtype,shopname,"
				+ "TO_CHAR(orders.id / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS CDATE,"
				+ "orders.id,ordernumber,"+getordertype("status","status")+" ,status status1,payment+postfee payment from orders  where 1=1 ";
		if (type == 1)
			sql = sql + "and memberid=" + getLogin(phone).getUserid();
		else if (type == 2) {
			try {
				if( getMember(getLogin(phone)).getmShop().getSuperid()==0){
//					sql = sql + "and shopid in ( select id from shop where oneid=" + getMember(getLogin(phone)).getmShop().getId()+") ";
					sql = sql + "and onephone="+phone;
				}else{
					sql = sql + "and shopid=" + getMember(getLogin(phone)).getmShop().getId();
				}
				
			} catch (Exception e) {
				try {
//					Sql msql1 = new Sql();
//					msql1.setSql("select memberidb from  Friends where memberida="+getLogin(phone).getUserid());
//					List<Map<String, Object>> listmap = mOrdersService.exeSelectSql(msql1);
//					if(listmap.size()==0)
//						throw new RunException("抱歉，权限不足");
					
					sql = sql + "and memberid in(select memberidb from  Friends where memberida="+getLogin(phone).getUserid()+")";
					
				} catch (Exception e2) {
					throw new RunException("抱歉，权限不足");
				}

				
			}
		} else {
			if (getMember(getLogin(uname)).getSuperadmin() != 1) {
				try {
					sql = sql + "and shopid=" + getMember(getLogin(uname)).getmShop().getId() ;
				} catch (Exception e) {
					throw new RunException("权限不足");
				}
			}

		}
		Sql msql = new Sql();
		msql.setOrderbykey("id");
		msql.setOrderbytype(1);
		msql.setPage(page == null ? 1 : page);
		msql.setRows(rows);
		if (status != null&&status!=0) {
			if (status == 2)
				sql = sql + " and (status=2 or status=6 or status =7 or status=8)";
			else
				sql = sql + " and status= "+status;
			
		}
		if(!Stringutil.isBlank(ordernumber))sql = sql+ " and ordernumber='"+ordernumber+"'";
		if(id!=null)sql = sql+ " and id="+id;
		msql.setSql(sql);
		List<Map<String, Object>> listmap = mOrdersService.exeSelectSql(msql);
		msql.setPage(null);
		msql.setRows(null);
		for (Map<String, Object> map : listmap) {
			// 图片
			msql.setSql("select picpath from Orderrelevance where orderid=" + map.get("ID").toString());
			List<Map<String, Object>> list = mOrdersService.exeSelectSql(msql);
			if (list.size() > 4)
				map.put("sizenumber", 1);
			else
				map.put("sizenumber", 0);
			map.put("image", list);
			map.put("imagesize", list.size());
			
//			Object  obj =map.get("COUPONID");
//			if(obj!=null){
//				msql.setSql("select * from Coupon where id=" + map.get("COUPONID").toString());
//				map.put("PAYMENT", new BigDecimal(map.get("PAYMENT").toString()).subtract(new BigDecimal(mOrdersService.exeSelectSql(msql).get(0).get("NUMBERA").toString())));
//			}
			
		}
		return sendTrueData(listmap);
	}

	/**
	 * 订单详情
	 */
	@RequestMapping(value = "/orders/selectbyordersid", method = RequestMethod.POST)
	@Auth
	public RequestType selectbyordersid(String uname, Long id) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		Sql msql = new Sql();
		msql.setSql("select postfee,memberid,shippingtype,TRAJECTORY,"
				+ "TO_CHAR(id / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS CDATE,"
				+ "id,"+getordertype("status","status")+" ,status status1,payment+postfee payment from orders where id="
				+ id);
		List<Map<String, Object>> listmap = mOrdersService.exeSelectSql(msql);
		if (listmap.size() == 0)
			return sendFalse("订单号不存在");
//		if (getMember(getLogin(uname)).getSuperadmin() != 1) {
//			if (!(getLogin(uname).getUserid() + "").equals(listmap.get(0).get("MEMBERID").toString())){//不是用户自己
//				//关联店铺
////				getMember(getLogin(uname)).getShopto().getId()
//			}
//				return sendFalse("该订单不属于你");
//		}
		
		for (Map<String, Object> map : listmap) {
			map.put("mphone",getMember(Long.valueOf(map.get("MEMBERID").toString())).getUname());
			
		}
		uname = getMember(Long.valueOf(listmap.get(0).get("MEMBERID").toString())).getUname();
		
		Object obj =  listmap.get(0).get("TRAJECTORY");
		Object obj1 =  listmap.get(0).get("STATUS");
				
		data.put("order", listmap);
		msql.setSql(
				"select Orderrelevance.*,company,mysize,specifications,colour from Orderrelevance left join Commodity on Commodity.id=Orderrelevance.itemid where orderid="
						+ id);
		msql.setRows(1000);
		listmap = mOrderrelevanceService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			map.put("AAAPromotion", PromotionController.getPromotion(mMemberService, map.get("ITEMID")));
		}
		data.put("Orderrelevance", listmap);
		msql.setSql(
				"select Receiver.* from orders left join  Receiver on Receiver.id=orders.myaddressid where  orders.id="
						+ id);
		Myaddress mMyaddress = null;
		listmap = mOrderrelevanceService.exeSelectSql(msql);
		
		if(listmap.size()!=0){
			try {
				mMyaddress= IBeanUtil.Map2JavaBean(listmap.get(0), Myaddress.class);	
			} catch (Exception e) {
			}
		}
			
		if(mMyaddress==null||mMyaddress.getId()==null){
			mMyaddress = new Myaddress();
			Shop mShop=getShop(uname);
			mMyaddress.setProvince(mShop.getProvince());
			mMyaddress.setCity(mShop.getCity());
			mMyaddress.setArea(mShop.getArea());
			mMyaddress.setStreet(mShop.getStreet());
			mMyaddress.setDetailed(mShop.getDetailed());
			mMyaddress.setLongitude(mShop.getLongitude());
			mMyaddress.setLatitude(mShop.getLatitude());
			mMyaddress.setName(mShop.getShopname());
			mMyaddress.setPhone(mShop.getShopphone());
			data.put("address",mMyaddress);
			
		}else {
			data.put("address", mMyaddress );
		}
		
		String[] trajectorys = null ;
		List<trajectory> mLtrajectory = new LinkedList<>();
		if(obj!=null)
			 trajectorys = obj.toString().split(";");
		
		String datas;
		if(trajectorys!=null&&trajectorys.length!=0){
			
			int a = trajectorys.length/2;
			for (int i = 0;i<a;i++) {
				trajectory mtrajectory= new trajectory();
				datas=MyDate.stampToDate(Long.valueOf(trajectorys[i*2])) ;
				if(!getOrdersTrajectoryUserNot(trajectorys[(i*2)+1])){
					mtrajectory.setTime(datas);
					mtrajectory.setType(trajectorys[(i*2)+1]);
					mLtrajectory.add(mtrajectory);
				}
				
			}
			
		}
		
		if(mLtrajectory.size()==0){
			if("已拒单".equals(obj1.toString())){
				trajectory mtrajectory= new trajectory();
				datas=MyDate.stampToDate(id+MyParameter.TASK_ORDERS9) ;
				mtrajectory.setTime(datas);
				mtrajectory.setType("商家已拒单");
				mLtrajectory.add(mtrajectory);
			}
			
		}
		Collections.reverse(mLtrajectory);
		data.put("trajectorys", mLtrajectory);
		return sendTrueData(data);

	}
	class trajectory{
		private String time;
		private String type;
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}
	

	/**
	 * 购买详情
	 */
	@RequestMapping(value = "/orders/selectbyOrderrelevanceid", method = RequestMethod.POST)
	@Auth
	public RequestType selectbyOrderrelevanceid(String uname, Long id) throws Exception {
		Sql msql = new Sql();
		msql.setSql(
				"select TO_CHAR(Orderrelevance.id / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS CDATE,Orderrelevance.*,colour,mysize,specifications from Orderrelevance left join Commodity on Commodity.id=itemid where Orderrelevance.id="
						+ id);
		List<Map<String, Object>> s = mOrderrelevanceService.exeSelectSql(msql);
//		for (Map<String, Object> map : s) {
//			if(map.get("PROMOTIONID")!=null){
//				Promotion p = PromotionController.getPromotion(mMemberService, map.get("PROMOTIONID"));
//				 
//				if(p!=null){
//					BigDecimal b1 = new BigDecimal("0");
//					if (p.getType() == 1)
//						b1 = new BigDecimal(map.get("PRICE").toString()).multiply(p.getDiscount().divide(new BigDecimal("10")));
//					else if (p.getType() == 2)
//						b1 = new BigDecimal(map.get("PRICE").toString()).subtract(p.getReduce());
//					
//					map.put("PRICE", b1);
//					
//				}
//			}
//			
//			
//			
//		}
//		
		return sendTrueData(s);

	}

}
