package com.bm.returngoods;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.MyDate;
import com.bm.consumption.RefundresponseService;
import com.bm.orders.OrdersController;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "单品退货")
public class ReturngoodsController extends BaseController{
	@Autowired
	private ReturngoodsService mReturngoodsService;
	@Autowired
	private OrdersService mOrdersService;
	@Autowired
    private RefundresponseService mRefundresponseService;
//	

	/**
	 * 申请退货
	 * size:退货件数
	 * type:退回渠道，1：店铺，2：厂商
	 * */
	@RequestMapping(value ="/Returngoods/add", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType add(String postfeenumber,Long orderrelevanceid,String reason,BigDecimal refund,Integer size,Integer type,String uname) throws Exception{
		if(type==null)type=1;
		if(size==null)size=1;
		Returngoods mReturngoods = new Returngoods();
		if(refund==null)return sendFalse("退价不可为空");
		mReturngoods.setRefund(refund);
		Sql msql =new Sql();
		msql.setSql("select orderrelevance.*,orders.endtime,shopid from orderrelevance left join orders on orders.id=orderrelevance.orderid where orders.status=4 and orderrelevance.id ="+orderrelevanceid);
		List<Map<String,Object>> listmap = mReturngoodsService.exeSelectSql(msql);
		if(listmap.size()==0)return sendFalse("商品不存在");
		if(Long.valueOf(listmap.get(0).get("ENDTIME").toString())+MyParameter.ReturnOrdersTime<System.currentTimeMillis())
			return sendFalse("超过期限不能退货");
		
		 Map<String, Object> map = mReturngoodsService.getByparameter("orderrelevanceid", orderrelevanceid+"");
		 if(map!=null&&Integer.parseInt(map.get("ISTRUE").toString())>=0)
			return sendFalse("请不要重复申请");
		 
		if(size>Integer.parseInt(listmap.get(0).get("NUM").toString()))
			return sendFalse("退货件数大于购买件数");
		
		
		if(refund.subtract(new BigDecimal(listmap.get(0).get("TOTALFEE")+"") ).doubleValue()>0)
			return sendFalse("退价不能高于卖价");
		
		
		mReturngoods.setLogistics(postfeenumber);
		mReturngoods.setMemberid(getLogin(uname).getUserid());
		mReturngoods.setOrderrelevanceid(orderrelevanceid);
		mReturngoods.setReason(reason);
		mReturngoods.setIstrue(0);; //是否同意0:未处理，-1，不同意，1已同意
		mReturngoods.setPrice(new BigDecimal(listmap.get(0).get("PRICE").toString()).multiply(new BigDecimal(size)) );; //商品售价
		mReturngoods.setShopid(listmap.get(0).get("SHOPID").toString());
		mReturngoods.setA(size);
		
		if(refund.compareTo(mReturngoods.getPrice())==1)
			return sendFalse("退价不能高于卖价");
		Long tid;
		if(type==1){
			msql.setSql("select memberid from shop where id="+mReturngoods.getShopid());
			tid=Long.valueOf(mReturngoodsService.exeSelectSql(msql).get(0).get("MEMBERID").toString());
		}else{
			
			msql.setSql("select supplier from Commodity where id="+listmap.get(0).get("ITEMID").toString());
			tid=getLogin(mReturngoodsService.exeSelectSql(msql).get(0).get("SUPPLIER").toString()).getUserid();
		}
		mReturngoods.setSystemexpress(tid);
		mReturngoods.setReturnnum(0);
		mReturngoodsService.add(mReturngoods);
		
		msql.setSql("update Orderrelevance set title=CONCAT(title,'(已退"+size+"件)') where id="+listmap.get(0).get("ID").toString());
		mReturngoodsService.execSQL(msql);
		
		return sendTrueMsg("申请成功");
		
	}
	/**
	 * 退仓
	 * */
	@RequestMapping(value ="/Returngoods/retreat", method = RequestMethod.POST) 
	@Auth(Returngoods={Administration.UPDATE})
	public RequestType retreat(Long id,String uname,String logistics) throws Exception{
		if(Stringutil.isBlank(logistics))
			return sendTrueMsg("运单号不可为空");
		
		return retreataaaaaaaaaaaaa(id, 2,logistics);
//		if(getMember(getLogin(uname)).getSuperadmin()==1)
//			return retreataaaaaaaaaaaaa(id, 2,logistics);
//		
//		try {
//			if(getMember(getLogin(uname)).getmShop().getSuperid()!=0)
//				return retreataaaaaaaaaaaaa(id, 2,logistics);
//			else
//				return sendTrueMsg("权限不足");
//		} catch (Exception e) {
//			return sendTrueMsg("权限不足");
//		}
		
	}
	/**
	 * 查询物品物流地址
	 * */
	@RequestMapping(value ="/Returngoods/selectbyitemid", method = RequestMethod.POST) 
	@Auth
	public RequestType selectbyitemid(Long itemid,String uname) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select shop.* from Commodity left join stock on Commodity.youcode=stock.code left join shop on shop.code=stock.shopcode where Commodity.id="+itemid);
		return sendTrueData(mReturngoodsService.exeSelectSql(msql).get(0));
	}
	/**
	 * 赔付
	 * */
//	@RequestMapping(value ="/Returngoods/SystemExpress", method = RequestMethod.POST) 
//	@Auth(admin=true)
//	public RequestType SystemExpress(Long id,BigDecimal number) throws Exception{
//		if(number==null)
//			return sendFalse("赔付金额不可为空");
//		Returngoods mReturngoods=mReturngoodsService.getById(id,Returngoods.class);
//		if(mReturngoods==null)
//			return sendFalse("订单不存在");
//		if(mReturngoods.getSystemExpress()!=null&&mReturngoods.getSystemExpress()==1)
//			return sendFalse("订单已赔付");
//		if(Stringutil.isBlank(mReturngoods.getLogistics()))
//			return sendFalse("没有运单号，无法赔付");
//		
//		
//		
//		Sql msql = new Sql();
//		msql.setSql("select supplier from Returngoods left join orderrelevance on orderrelevance.id="
//				+ "orderrelevanceid left join commodity on commodity.id=orderrelevance.itemid where Returngoods.id="+id);
//		List<Map<String, Object>> listmap = mReturngoodsService.exeSelectSql(msql);
//		if(listmap.size()==0)
//			return sendFalse("错误，商品未找到供应商");
//		
//		MemberAuths ma = getMember(getLogin(listmap.get(0).get("SUPPLIER").toString()));
//		
//		
//		msql.setSql("select * from Express where oneid="+ma.getmShop().getId()+" and end>"+System.currentTimeMillis());
//		listmap = mReturngoodsService.exeSelectSql(msql);
//		if(listmap.size()==0)
//			return sendFalse("错误，该商品的供应商为购买运费险或已经失效");
//		
//		Express mExpress=IBeanUtil.Map2JavaBean(listmap.get(0), Express.class); 
//		
//		if(mExpress.getMinindemnitya().doubleValue()>number.doubleValue()||mExpress.getMaxindemnitya().doubleValue()<number.doubleValue())
//			return sendFalse("错误，赔付低于单笔最低赔付或高于单笔最高赔付");
//		
//		msql.setSql("select nvl(sum(payment),0) payment from SystemExpress where oneid="+ma.getmShop().getId()+" and id<"+mExpress.getEnd() +" and id >"+mExpress.getStr());
//		
//		
//		
//		if(new BigDecimal(mReturngoodsService.exeSelectSql(msql).get(0).get("PAYMENT").toString()).add(number).doubleValue()>mExpress.getMaxindemnity().doubleValue())
//			return sendFalse("错误，赔付金额超过了最大赔付金额");
//		SystemExpress mSystemExpress= new SystemExpress();
//		mSystemExpress.setId(mReturngoods.getLogistics());
//		mSystemExpress.setTime(System.currentTimeMillis());
//		mSystemExpress.setPayment(number);
//		mSystemExpress.setOneid(ma.getmShop().getId());
//		try {
////			mSystemExpressService.add(mSystemExpress);
////			mReturngoods.setSystemExpress(1);
////			mReturngoodsService.updateBySelect(mReturngoods);
//		} catch (Exception e) {
//			return sendFalse("错误，该运单号已经赔付");
//		}
//		
//		
//		return sendTrueMsg("操作成功");
//	}
	/**
	 * 仓库确认收货
	 * */
	@RequestMapping(value ="/Returngoods/retreat1", method = RequestMethod.POST) 
	@Auth
	public RequestType retreat1(Long id,String uname) throws Exception{
		if(getMember(getLogin(uname)).getSuperadmin()==1)
			return retreataaaaaaaaaaaaa(id, 3,"");
		
		try {
			if(getMember(getLogin(uname)).getmShop().getSuperid()==0)
				return retreataaaaaaaaaaaaa(id, 3,"");
			else
				return sendTrueMsg("权限不足");
		} catch (Exception e) {
			return sendTrueMsg("权限不足");
		}
		
	}
	/**
	 * 修改物流单号
	 * */
	@RequestMapping(value ="/Returngoods/updatelogistics", method = RequestMethod.POST) 
	@Auth
	public RequestType updatelogistics(Integer type,Long id,String logistics,String uname) throws Exception{
		if(type==null)type=1;
		Sql msql = new Sql();
		
		if(type==1){
			msql.setSql("update Returngoods set logistics='"+logistics+"' where id="+id);
		}else{
			msql.setSql("update Returnorders set postfeenumber='"+logistics+"' where id="+id);
		}
		
		mReturngoodsService.execSQL(msql);
		return sendTrueMsg("修改成功");
		
	}
	private RequestType retreataaaaaaaaaaaaa(Long id,int i,String logistics) throws Exception {
		Returngoods mReturngoods = mReturngoodsService.getById(id,Returngoods.class);
		if(mReturngoods==null)
			throw new RunException("订单不存在");
		if(i==3){
			if(mReturngoods.getIstrue()!=2)
				throw new RunException("错误，店铺还未退仓");
		}
		if(!Stringutil.isBlank(logistics)){
			if(mReturngoodsService.getByparameter("logistics", logistics)!=null)
				throw new RunException("抱歉，运单号已存在");
			mReturngoods.setLogistics(logistics);
		}
		mReturngoods.setId(id);
		mReturngoods.setIstrue(i);
		mReturngoodsService.updateBySelect(mReturngoods);
		return sendTrueMsg("退仓成功");
	

	}
	/**
	 * 取消申请退货
	 * */
	@RequestMapping(value ="/Returngoods/deletebyid", method = RequestMethod.POST) 
	@Auth
	public RequestType deletebyid(Long id) throws Exception{
		Map<String, Object> m =mReturngoodsService.getById(id);
		if(m==null)return sendFalse("未找到数据");
		if(Integer.parseInt(m.get("ISTRUE").toString())==1)
			return sendFalse("改商品已经退了，不可取消");
		mReturngoodsService.deleteByid(id);
		return sendTrueMsg("取消成功");
		
	}
	/**
	 * 退款
	 * */
	@RequestMapping(value ="/Returngoods/returnnum", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType returnnum(Long id ) throws Exception{
		Returngoods mReturngoods =mReturngoodsService.getById(id,Returngoods.class);
		
		if(mReturngoods==null)return sendFalse("未找到数据");
		if(mReturngoods.getReturnnum()!=null&&mReturngoods.getReturnnum()==1)
			return sendFalse("改商品已经退款");
		Long sid = mReturngoods.getOrderrelevanceid();
		BigDecimal sbd =  mReturngoods.getRefund();
		mReturngoods = new Returngoods();
		mReturngoods.setId(id);
		mReturngoods.setReturnnum(1);
		Sql msql = new Sql();
		msql.setSql("select orders.* from orders left join Orderrelevance on Orderrelevance.orderid=orders.id  where Orderrelevance.id="+sid);
		OrdersController.returnPrice(mRefundresponseService,mMemberService, mOrdersService, IBeanUtil.Map2JavaBean(mOrdersService.exeSelectSql(msql).get(0), Orders.class), sbd);
		return sendTrueMsg(mReturngoodsService.updateBySelect(mReturngoods));
		
	}
	/**
	 * 同意退货
	 * */
	@RequestMapping(value ="/Returngoods/Agree", method = RequestMethod.POST) 
	@Auth(Returngoods={Administration.UPDATE})
	public RequestType Agree(Long id,String uname) throws Exception{
		Returngoods mReturngoods;
		if(getMember(uname).getmShop()!=null&&getMember(uname).getmShop().getSuperid()!=0)
			mCouponService.OutsendCouponTH(getLogin(uname).getUserid());
		else{
			 mReturngoods =mReturngoodsService.getById(id,Returngoods.class);
			mCouponService.OutsendCouponTH(mReturngoods.getMemberid());
		}
		 mReturngoods = new Returngoods();
		mReturngoods.setId(id);
//		mReturngoods.setRefund(refund);
		mReturngoods.setIstrue(1);; //是否同意0:未处理，-1，不同意，1已同意,2:退仓，3：仓库收货
		mReturngoodsService.updateBySelect(mReturngoods);
		return sendTrueMsg("操作成功");
		
	}
	/**
	 * 拒绝退货
	 * */
	@RequestMapping(value ="/Returngoods/refuse", method = RequestMethod.POST) 
	@Auth(Returngoods={Administration.UPDATE})
	public RequestType refuse(Long id,String refuse) throws Exception{
		if(Stringutil.isBlank(refuse))return sendFalse("拒绝原因不可为空");
		Returngoods mReturngoods = mReturngoodsService.getById(id,Returngoods.class);
		mReturngoods.setId(id);
		mReturngoods.setRefuse(refuse);
		mReturngoods.setIstrue(-1);; //是否同意0:未处理，-1，不同意，1已同意
		mReturngoodsService.updateBySelect(mReturngoods);
		Sql msql = new Sql();
		msql.setSql("select * from Orderrelevance where id='"+mReturngoods.getOrderrelevanceid()+"'");
		Orderrelevance mOrderrelevance=IBeanUtil.Map2JavaBean(mReturngoodsService.exeSelectSql(msql).get(0), Orderrelevance.class);
		if(mOrderrelevance.getTitle().contains("(已退")){
			msql.setSql("update Orderrelevance set title="+
					mOrderrelevance.getTitle().substring(0,mOrderrelevance.getTitle().length()-6)+" where id="+mOrderrelevance.getId());
					mReturngoodsService.execSQL(msql);
		}
			
		
		return sendTrueMsg("操作成功");
		
	}
	/**
	 * 退货列表2
	 * */
	@RequestMapping(value ="/Returngoods/shoplist", method = RequestMethod.POST) 
	@Auth()
	public RequestType shoplist(String uname,Long id,String logistics,String phone,Integer istrue,Integer page,Integer rows) throws Exception{
		String sql = "select "+MyDate.orcaleCDATE("Returngoods.id")+"  ,"
		+ "Returngoods.*,orders.id ORDERSID,uname  MEMBERPHONE, Orderrelevance.title,Orderrelevance.num,Orderrelevance.totalfee,itemid,Orderrelevance.youcode,"
		+ "Orderrelevance.picpath from Returngoods left join Orderrelevance on "
		+ "Orderrelevance.id=Returngoods.orderrelevanceid"
		+ " left join orders on orders.ID=Orderrelevance.ORDERID left join member on orders.memberid=member.id where 1=1 ";
		if(istrue!=null)
			sql=sql+" and Returngoods.istrue='"+istrue+"'";//状态
		if(id!=null)
			sql=sql+" and orders.id="+id;//id
		if(!Stringutil.isBlank(logistics))
			sql=sql+" and Returngoods.logistics='"+logistics+"'";//物流
		if(getMember(uname).getSuperadmin()==1){
			if(!Stringutil.isBlank(phone)){//审核人
				sql=sql+" and Returngoods.SystemExpress='"+getLogin(phone).getUserid()+"'";
			}
		}else{
			if(getMember(uname).getmShop()!=null){
				if(getMember(uname).getmShop().getSuperid()==0){
					sql=sql+" and( Returngoods.SystemExpress='"+getLogin(uname).getUserid()+"' or (Returngoods.istrue=2 and shopid in(select id from shop where oneid='"+getMember(uname).getmShop().getId()+"')))";
				}else{
					sql=sql+" and Returngoods.SystemExpress='"+getLogin(uname).getUserid()+"'";
				}
				
			}
			else{
				sql=sql+" and orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")='";
			}
		}
		Sql  msql = new Sql();
		msql.setSql(sql);
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey("Returngoods.id");
		msql.setOrderbytype(1);
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}
	/**
	 * 退货列表3
	 * */
	@RequestMapping(value ="/Returngoods/mylist", method = RequestMethod.POST) 
	@Auth()
	public RequestType mylist(String uname,Long id,String logistics,Integer istrue,Integer page,Integer rows) throws Exception{
		String sql = "select "+MyDate.orcaleCDATE("Returngoods.id")+"  ,"
				+ "Returngoods.*,Orderrelevance.title,Orderrelevance.num,Orderrelevance.totalfee,itemid,Orderrelevance.youcode,"
				+ "Orderrelevance.picpath from Returngoods left join Orderrelevance on "
				+ "Orderrelevance.id=Returngoods.orderrelevanceid"
				+ " left join orders on orders.ID=Orderrelevance.ORDERID where orders.memberid= "+getLogin(uname).getUserid();
		if(istrue!=null)
			sql=sql+" Returngoods.istrue='"+istrue+"'";//状态
		if(id!=null)
			sql=sql+" and orders.id="+id;//id
		if(!Stringutil.isBlank(logistics))
			sql=sql+" Returngoods.logistics='"+logistics+"'";//物流
		
		Sql  msql = new Sql();
		msql.setSql(sql);
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}
	
	/**
	 * 店铺退货列表
	 * */
//	@RequestMapping(value ="/Returngoods/selectallbyshopid", method = RequestMethod.POST) 
//	@Auth
//	public RequestType selectallbyshopid(String logistics,String onephone,String uname,Integer istrue, Long shopid,String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
////		Returngoods mReturngoods = new Returngoods();
////		if(getMember(getLogin(uname).getUserid()).getSuperadmin()!=1)
////		mReturngoods.setShopid(shopid+"");
////		mReturngoods.setIstrue(istrue);
////		return sendTrueData(mReturngoodsService.getALL(mReturngoods, orderbykey, orderbytype, page, rows));
//		return selectallbyid(logistics,onephone,2, shopid, null, istrue, uname, orderbykey, orderbytype, page, rows);
//		
//	}
//	/**
//	 * 店铺退货列表(手机)
//	 * */
//	@RequestMapping(value ="/Returngoods/selectallbyshopidapp", method = RequestMethod.POST) 
//	@Auth
//	public RequestType selectallbyshopidapp(String logistics,String uname,Integer istrue, String shopid,String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
//		if("undefined".equals(shopid))
//			return selectallbyid(logistics,null,true,2, null, null, istrue, uname, orderbykey, orderbytype, page, rows);
//		return selectallbyid(logistics,null,true,2, Long.valueOf(shopid), null, istrue, uname, orderbykey, orderbytype, page, rows);
////		return selectallbyid(true,2, shopid, null, istrue, uname, orderbykey, orderbytype, page, rows);
//		
//	}
//	
//	
//	
//	/**
//	 * 我的退货列表
//	 * */
//	@RequestMapping(value ="/Returngoods/selectallbymemberid", method = RequestMethod.POST) 
//	@Auth
//	public RequestType selectallbymemberid(String logistics,String onephone,Long id,Integer istrue,String uname, String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
//		String sql = "select TO_CHAR(Returngoods.id / (1000 * 60 * 60 * 24)"
//				+ " + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),  'YYYY-MM-DD HH24:MI:SS') AS CDATE ,"
//				+ "Returngoods.*,Orderrelevance.title,Orderrelevance.num,Orderrelevance.totalfee,Orderrelevance.price,Orderrelevance.youcode"
//				+ "Orderrelevance.picpath from Returngoods left join Orderrelevance on "
//				+ "Orderrelevance.id=Returngoods.orderrelevanceid"
//				+ " left join orders on orders.ordernumber=Orderrelevance.ordernum where "
//				+ "orders.memberid="+getLogin(uname).getUserid();
//		if(id!=null)sql="select * from ("+sql+ " and Returngoods.id="+id +" order by Returngoods.id desc) where rownum=1";
//		Sql msql = new Sql();
//		msql.setSql(sql);
//		if(istrue!=null) msql.setSql(msql.getSql()+" and Returngoods.istrue="+istrue);
//		msql.setOrderbykey(orderbykey);
//		msql.setOrderbytype(orderbytype);
//		msql.setPage(page);
//		msql.setRows(rows);
//		return sendTrueData(mReturngoodsService.exeSelectSql(msql));
//		return selectallbyid(logistics,onephone,1, null, id, istrue, uname, orderbykey, orderbytype, page, rows);
//		
//	}
//	/**
//	 *客户退货列表
//	 * */
//	@RequestMapping(value ="/Returngoods/selectallbymemberid1", method = RequestMethod.POST) 
//	@Auth
//	public RequestType selectallbymemberid1(String logistics,String onephone,Long id,Integer istrue,String uname, String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
//		return selectallbyid(logistics,onephone,3, null, id, istrue, uname, orderbykey, orderbytype, page, rows);
//		
//	}
//	
//	public RequestType selectallbyid(String logistics,String onephone,int i,Long shopid,Long id,Integer istrue,String uname, String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
//		return selectallbyid(logistics,onephone,false, i, shopid, id, istrue, uname, orderbykey, orderbytype, page, rows);
//		
//	}
//	public RequestType selectallbyid(String logistics,String onephone,boolean b,int i,Long shopid,Long id,Integer istrue,String uname, String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception{
//		String sql = "select "+MyDate.orcaleCDATE("Returngoods.id")+"  ,"
//				+ "Returngoods.*,Orderrelevance.title,Orderrelevance.num,Orderrelevance.totalfee,itemid,Orderrelevance.youcode,"
//				+ "Orderrelevance.picpath from Returngoods left join Orderrelevance on "
//				+ "Orderrelevance.id=Returngoods.orderrelevanceid"
//				+ " left join orders on orders.ID=Orderrelevance.ORDERID where 1=1 ";
//		
//		if(i==3){
//			sql = "select "+MyDate.orcaleCDATE("Returngoods.id")+" , uname kuname,"
//					+ "Returngoods.*,Orderrelevance.title,Orderrelevance.num,Orderrelevance.totalfee,Orderrelevance.youcode,"
//					+ "Orderrelevance.picpath from Returngoods left join Orderrelevance on "
//					+ "Orderrelevance.id=Returngoods.orderrelevanceid"
//					+ " left join orders on orders.ID=Orderrelevance.ORDERID  left join Friends on memberidb = orders.memberid "
//					+ "    left join member on member.id=orders.memberid where 1=1 ";
//		}else
//			if(i==1)sql = sql
//			+ " and orders.memberid="+getLogin(uname).getUserid();
//			else{
//				if(getMember(getLogin(uname).getUserid()).getSuperadmin()!=1){
//					try {
//						if(getMember(getLogin(uname).getUserid()).getmShop().getSuperid()==0){
//							sql = sql + "and Returngoods.shopid in ( select id from shop where oneid=" + getMember(getLogin(uname)).getmShop().getId()+")";
//						}else{
//							sql = sql +" and Returngoods.shopid="+getMember(getLogin(uname)).getmShop().getId();
//						}
//						
//					} catch (Exception e) {
//						try {
//							Sql msql1 = new Sql();
//							msql1.setSql("select shopid from clerk where memberid="+getLogin(uname).getUserid());
//							List<Map<String, Object>> listmap = mReturngoodsService.exeSelectSql(msql1);
//							if(listmap.size()==0)
//								throw new RunException("抱歉，权限不足");
//							
//							sql = sql + "and Returngoods.shopid=" + listmap.get(0).get("SHOPID").toString();
//							
//						} catch (Exception e2) {
//							throw new RunException("抱歉，权限不足");
//						}
//					
//					}
//				}else{
//					if(!Stringutil.isBlank(onephone))
//						sql = sql + "and Returngoods.shopid in ( select id from shop where oneid=" + getMember(getLogin(onephone)).getmShop().getId()+")";
//					else
//					if(shopid!=null)
//						sql = sql+" and Returngoods.shopid="+shopid;
//				}
//				
//			}
//		
//		if(id!=null)sql="select * from ("+sql+ " and Returngoods.id="+id +" order by Returngoods.id desc) where rownum=1";
//		Sql msql = new Sql();
//		msql.setSql(sql);
//		String istruesql ;
//		if(istrue!=null) {
//			istruesql=" and Returngoods.istrue="+istrue;
//			if(b){
//				if(istrue==1)
//					istruesql=" and Returngoods.istrue in(1,2,3)";
////					msql.setSql(msql.getSql()+" and Returngoods.istrue in(1,2,3)");
//			}
//			msql.setSql(msql.getSql()+istruesql);
//		}
//		if(!Stringutil.isBlank(logistics))msql.setSql(msql.getSql()+" and logistics='"+logistics+"'");
//		msql.setOrderbykey(orderbykey);
//		msql.setOrderbytype(orderbytype);
//		msql.setPage(page);
//		msql.setRows(rows);
//		return sendTrueData(mReturngoodsService.exeSelectSql(msql));
//		
//	}
//	
	
	
	
}
