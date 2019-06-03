package com.bm.webapp.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.excle.ExportExcel;
import com.bm.base.excle.ExportExcel.Exa;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.MyDate;
import com.bm.orders.OrdersController;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

/**
 * 数据分析与统计
 * */
@RestController
@Api(tags = "数据处理")
public class Data extends BaseController{

	
	/**
	 *导购的分成
	 * @throws Exception 
	 * */
	private void sumclerk(Long star,Long end,List<SumRecommend2> listSumRecommend2,Integer rows, Integer page,String phone) throws Exception{
		boolean b=listSumRecommend2.size()==0;
		Sql msql =new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> listmap;
		if(b){
			if(Stringutil.isBlank(phone))
				msql.setSql("select memberid,uname from clerk left join member on member.id=memberid");
			else
				msql.setSql("select memberid,uname from clerk left join member on memberid=member.id where uname='"+phone+"'");
			listmap = mMemberService.exeSelectSql(msql);
			if(!Stringutil.isBlank(phone)&&listmap.size()==0)
				throw new RunException("用户"+phone+"不是导购");
			}else{
				StringBuilder sb = new StringBuilder("'");
				for (SumRecommend2 mSumRecommend2 : listSumRecommend2) {
					sb.append(mSumRecommend2.getPhone());
					sb.append("','");
				}
				msql.setSql("select memberid,uname from clerk left join member on memberid=member.id where uname in("+sb.substring(0,sb.length()-2)+")");
				listmap = mMemberService.exeSelectSql(msql);
			}
		
		
		
		
		for (Map<String, Object> map : listmap) {
			try {
				SumRecommend2 mSumRecommend2=null;
				if(b)
					mSumRecommend2= new SumRecommend2();
				else{
					for (SumRecommend2 mSumRecommend21: listSumRecommend2) {
						if(mSumRecommend21.getPhone().equals(map.get("UNAME").toString())){
							mSumRecommend2=mSumRecommend21;
							break;
						}
					}
				}
				msql.setSql("select nvl(sum(clerk),0)/100 clerk from Sharingdetails where memberid ="+map.get("MEMBERID").toString()+" and state=1"
						+ " and id>"+star+" and id<"+end);
				
				mSumRecommend2.setPhone(getMember(Long.valueOf(map.get("MEMBERID").toString())).getUname());
				mSumRecommend2.setClerka(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//已结算金额
				
				msql.setSql("select nvl(sum(clerk),0)/100 clerk from Sharingdetails where memberid ="+map.get("MEMBERID").toString()+" and state=0"
						+ " and id>"+star+" and id<"+end);
				mSumRecommend2.setClerkb(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//未结算金额
				if(b)
				listSumRecommend2.add(mSumRecommend2);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		
	
	}
	/**
	 *店铺的分成
	 * @throws Exception 
	 * */
	private void sumShop(Long star,Long end,List<SumRecommend2> listSumRecommend2,Integer rows, Integer page,String phone) throws Exception{
		boolean b =listSumRecommend2.size()==0;
		Sql msql =new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> listmap;
		if(b){
			if(Stringutil.isBlank(phone))
				msql.setSql("select uname,shop.id from shop left join member on memberid=member.id");
			else
				msql.setSql("select uname,shop.id from shop left join member on memberid=member.id where uname='"+phone+"'");
			listmap = mMemberService.exeSelectSql(msql);
			
			if(!Stringutil.isBlank(phone)&&listmap.size()==0)
				throw new RunException("用户"+phone+"不是店铺");
		}else{
			StringBuilder sb = new StringBuilder("'");
			for (SumRecommend2 mSumRecommend2 : listSumRecommend2) {
				sb.append(mSumRecommend2.getPhone());
				sb.append("','");
			}
			msql.setSql("select uname,shop.id from shop left join member on memberid=member.id where uname in("+sb.substring(0,sb.length()-2)+")");
			listmap = mMemberService.exeSelectSql(msql);
		}
		for (Map<String, Object> map : listmap) {
			SumRecommend2 mSumRecommend2=null;
			if(b)
				mSumRecommend2= new SumRecommend2();
			else{
				for (SumRecommend2 mSumRecommend21: listSumRecommend2) {
					if(mSumRecommend21.getPhone().equals(map.get("UNAME").toString())){
						mSumRecommend2=mSumRecommend21;
						break;
					}
				}
			}
			msql.setSql("select nvl(sum(shop),0)/100 clerk from Sharingdetails where memberid in(select memberid from clerk where shopid="+
					map.get("ID").toString()
					+") and state=1"
					+ " and id>"+star+" and id<"+end);
			mSumRecommend2.setPhone(map.get("UNAME").toString());
			mSumRecommend2.setShopa(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//已结算金额
			
			msql.setSql("select nvl(sum(shop),0)/100 clerk from Sharingdetails where memberid in(select memberid from clerk where shopid="+
					map.get("ID").toString()
					+") and state=0"
					+ " and id>"+star+" and id<"+end);
			mSumRecommend2.setShopb(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//未结算金额
			if(b)
				listSumRecommend2.add(mSumRecommend2);
		}
		
		
		
	}
	/**
	 *供应商的分成
	 * @throws Exception 
	 * */
	private void sumShopone(Long star,Long end,List<SumRecommend2> listSumRecommend2,Integer rows, Integer page,String phone) throws Exception{
		Sql msql =new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> listmap;
		if(Stringutil.isBlank(phone))
			msql.setSql("select uname,shop.id from shop left join member on memberid=member.id where superid =0");
		else
			msql.setSql("select uname,shop.id from shop left join member on memberid=member.id where superid =0 and uname="+phone);
			
		listmap = mMemberService.exeSelectSql(msql);
		
		if(!Stringutil.isBlank(phone)&&listmap.size()==0)
			throw new RunException("用户"+phone+"不是供应商");
		
		for (Map<String, Object> map : listmap) {
			msql.setSql("select nvl(sum(shopone),0)/100 clerk from Sharingdetails where memberid in(select memberid from clerk where shopid="+
					map.get("ID").toString()
					+") and state=1 "
					+ " and id>"+star+" and id<"+end);
			SumRecommend2 mSumRecommend2 = new SumRecommend2();
			mSumRecommend2.setPhone(map.get("UNAME").toString());
			mSumRecommend2.setShoponea(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//已结算金额
			
			msql.setSql("select nvl(sum(shopone),0)/100 clerk from Sharingdetails where memberid in(select memberid "
					+ "from clerk where shopid in(select id from shop where oneid="+
					map.get("ID").toString()
					+")) and state=0"
					+ " and id>"+star+" and id<"+end);
			mSumRecommend2.setShoponeb(mMemberService.exeSelectSql(msql).get(0).get("CLERK").toString());//未结算金额
			listSumRecommend2.add(mSumRecommend2);
		}
		
		
		
	}
	
	
	/**
	 * 收入统计2.0 ，合计版
	 * type，身份，1，店员，2店长，3，供应商
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/data/SumRecommend2", method = RequestMethod.POST)
	public RequestType SumRecommend2M(Long star,Long end,Integer rows, Integer page, Integer type, String phone)
			throws Exception {
				return sendTrueData(getListSumRecommend2(star,end,rows, page, type, phone));
		
	}
	/**
	 * 收入统计2.0 ，合计版
	 * type，身份，1，店员，2店长，3，供应商
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/data/SumRecommend2Excel", method = RequestMethod.GET)
	public void SumRecommend2Excel(Long star,Long end, Integer rows, Integer page, Integer type, String phone,HttpServletResponse response)
			throws Exception {
		if(type==null)type=3;
		List<SumRecommend2> list =getListSumRecommend2(star,end, rows, page, type, phone);
		List<Map<String, Object>> listmap = new ArrayList<Map<String,Object>>();
		for (SumRecommend2 sumRecommend2 : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("PHONE",sumRecommend2.getPhone());
			map.put("SUMA",sumRecommend2.getSuma());
			map.put("SUMB",sumRecommend2.getSumb());
			map.put("CLERKA",sumRecommend2.getClerka());
			map.put("CLERKB",sumRecommend2.getClerkb());
			if(type==2||type==3){
			map.put("SHOPA",sumRecommend2.getShopa());
			map.put("SHOPB",sumRecommend2.getShopb());
			}
			if(type==3){
			map.put("SHOPONEA",sumRecommend2.getShoponea());
			map.put("SHOPONEB",sumRecommend2.getShoponeb());
			}
			listmap.add(map);
		}
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("账号");   			key.add("phone");
		title.add("已结算合计");  		key.add("suma");
		title.add("未结算合计");		key.add("sumb");
		title.add("作为导购已结算金额");	key.add("clerka");
		title.add("作为导购未结算金额");	key.add("clerkb");
		if(type==2||type==3){
			title.add("作为店铺已结算金额");	key.add("shopa");
			title.add("作为店铺未结算金额");	key.add("shopb");
		}
		if(type==3){
			title.add("作为供应商未结算金额");	key.add("shoponea");
			title.add("作为供应商未结算金额");	key.add("shoponeb");
		}
		
		ExportExcel.Export("佣金合计报表",title,key,listmap,response);
		
	}
	
	private List<Map<String, Object>> getAccount(Long star,Long end) throws Exception{
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		Sql msql = new Sql();
		msql.setSql("select TO_CHAR(orders.id / (1000 * 60 * 60 * 24) + "
				+ " TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), "
				+ " 'YYYY-MM-DD HH24:MI:SS') AS 时间, "
				+ "  uname 导购账户,  orders.id 订单编号, "
				+ " Orderrelevance.title 商品名字,  youcode 条码, "
				+ " ordernumber 商户订单号, shop.shopname 门店名字,  Orderrelevance.num 数量, "
				+ "  Orderrelevance.price 单价,  Orderrelevance.totalfee 总价, "
				+OrdersController.getordertype("orders.status", "订单状态")
				+"  ,   decode(orders.shippingtype, 1, '自提', 2, '送货', 3, '门店直销') 送货方式, "
				+ " numbera 优惠价,  postfee 邮费, "
				+ " nvl(concat(concat(concat(concat(Receiver.province, Receiver.city), "
				+ " Receiver.area), "
				+ " Receiver.street), "
				+ " Receiver.detailed), "
				+ " shop.detailed) 地址,  nvl(Receiver.phone, shopphone) 联系电话 "
				+ "from Orderrelevance "
				+ "left join orders "
				+ " on orderid = orders.id "
				+ "left join member "
				+ " on memberidsu = member.id "
				+ "left join Receiver "
				+ " on Receiver.orderid = orders.id "
				+ " left join shop "
				+ "on shop.id = shopid "
				+ "left join Coupon "
				+ " on Couponid = Coupon.id "
				+ " where orders.id> "+star
				+ " and orders.id<"+end);
		return mMemberService.exeSelectSql(msql);
	}
	/**
	 *对账报表
	 */
	@Auth()
	@RequestMapping(value = "/data/Account", method = RequestMethod.GET)
	public void Account (Long star,Long end,HttpServletResponse response)
			throws Exception {
		
		List<Map<String, Object>> listmap =getAccount(star, end);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("时间");   			key.add("时间");
		title.add("导购账号");   		key.add("导购账户");
		title.add("订单编号");   		key.add("订单编号");
		title.add("商品名称");   		key.add("商品名字");
		title.add("条码");   			key.add("条码");
		title.add("商户订单号");   		key.add("商户订单号");
		title.add("门店名字");   		key.add("门店名字");
		title.add("数量");   			key.add("数量");
		title.add("单价");   			key.add("单价");
		title.add("总价");   			key.add("总价");
		title.add("订单状态");   		key.add("订单状态");
		title.add("送货方式");   		key.add("送货方式");
		title.add("优惠价");   		key.add("优惠价");
		title.add("邮费");   			key.add("邮费");
		title.add("收货地址");   		key.add("地址");
		title.add("联系人电话");   		key.add("联系电话");



		ExportExcel.Export("财务报表",title,key,listmap,response);
		
	}
	/**
	 *查询对账报表
	 * @return 
	 */
	@Auth()
	@RequestMapping(value = "/data/AccountSelect", method = RequestMethod.POST)
	public RequestType AccountSelect (Long star,Long end,HttpServletResponse response)
			throws Exception {
		return sendTrueData(getAccount(star, end));

		
	}
	private List<SumRecommend2> getListSumRecommend2(Long star,Long end, Integer rows, Integer page, Integer type, String phone)throws Exception{
		if(type==null)type=3;
		
		if(star==null)star=0L;
		if(end==null)end=System.currentTimeMillis();
		
		List<SumRecommend2> listSumRecommend2 = new ArrayList<>();
		if(type==1){
			sumclerk(star,end,listSumRecommend2,rows,page,phone);
			for (SumRecommend2 sumRecommend2 : listSumRecommend2) {
				sumRecommend2.setSuma(sumRecommend2.getClerka());
				sumRecommend2.setSumb(sumRecommend2.getClerkb());
			}
		}else if(type==2){
			sumShop(star,end,listSumRecommend2, rows, page,phone);
			sumclerk(star,end,listSumRecommend2,rows,page,phone);
			for (SumRecommend2 sumRecommend2 : listSumRecommend2) {
				try {
					sumRecommend2.setSuma(new BigDecimal(sumRecommend2.getClerka()).add(new BigDecimal(sumRecommend2.getShopa())).toString());
					sumRecommend2.setSumb(new BigDecimal(sumRecommend2.getClerkb()).add(new BigDecimal(sumRecommend2.getShopb())).toString());
				} catch (Exception e) {
//				e.printStackTrace();
				}
				
			}
		}else{
			sumShopone(star,end,listSumRecommend2, rows, page,phone);
			sumShop(star,end,listSumRecommend2, rows, page,phone);
			sumclerk(star,end,listSumRecommend2,rows,page,phone);
			for (SumRecommend2 sumRecommend2 : listSumRecommend2) {
				try {
					sumRecommend2.setSuma(new BigDecimal(sumRecommend2.getClerka()).add(new BigDecimal(sumRecommend2.getShopa())).
							add(new BigDecimal(sumRecommend2.getShoponea())).toString());
					
					sumRecommend2.setSumb(new BigDecimal(sumRecommend2.getClerkb()).add(new BigDecimal(sumRecommend2.getShopb())).
							add(new BigDecimal(sumRecommend2.getShoponeb())).toString());
				} catch (Exception e) {
				}
				
			}
		}
		
				return listSumRecommend2;
	}


	


//	// 总价

//
//	msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//			+ ")) ,0)/100 sum from Sharingdetails " + "left join orders on ordersid=orders.id where onephone="
//			+ uname + " and Sharingdetails.id>" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//	map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//	msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//			+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where onephone="
//			+ uname + " and state=1");
//	map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
	
	
	
	@Auth
	@RequestMapping(value = "/web/selectPurchase/select", method = RequestMethod.POST)
	public RequestType selectPurchaseA(Long star,Long end,Integer type,Integer page,Integer rows,String uname) throws Exception {
		return sendTrueData(getPurchase(type,star, end,page,rows,uname));
		
	}
	@Auth
	@RequestMapping(value = "/web/selectPurchase/newselect", method = RequestMethod.POST)
	public RequestType selectPurchaseNewselect(Long star,Long end,Integer type,Integer page,Integer rows,String uname) throws Exception {
		return sendTrueData(getNewPurchase(type,star, end,page,rows,uname));
		
	}
//	@Auth(admin=true)
	@RequestMapping(value = "/web/selectPurchase/newexpor", method = RequestMethod.GET)
	public void selectPurchasenewexpor(Long star,Long end,Integer type,Integer page,Integer rows,String uname,HttpServletResponse response) throws Exception {
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		List<Map<String, Object>> listmap =getNewPurchase(type,star, end,page,rows,uname);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("订单号");			key.add("id");
		title.add("商品条码");			key.add("youcode");
		title.add("助记码");   		key.add("code");
		title.add("商品名");  		key.add("title");
		title.add("颜色");			key.add("colour");
		title.add("尺码");			key.add("mysize");
		title.add("数量");			key.add("num");
		title.add("名字");			key.add("name");
		title.add("地址");			key.add("detailed");
		title.add("电话");			key.add("phone");
		title.add("厂家");			key.add("manufactor");
		title.add("品牌");			key.add("brand");
		title.add("订单状态");			key.add("status");
		title.add("下单时间");			key.add("cdate");
		title.add("快递单号");			key.add("a");
		
		
		ExportExcel.Export(MyDate.stampToDate(star)+"   至   "+MyDate.stampToDate(end)+" 采购统计",title,key,listmap,response);
		
	}
	
	private List<Map<String, Object>> getPurchase(Integer type,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select nvl(concat(Receiver.province,concat(Receiver.city,concat(Receiver.area,concat(Receiver.street,Receiver.detailed)))),shop.detailed)detailed, Orderrelevance.youcode,"
				+ "nvl(Receiver.name,shop.shopname)name, "
				+ "nvl(Receiver.phone,member.phone)phone,Commodity.brand,decode(orders.status,2,'已付款',8,'已接单') status,"
				+ "orders.id,Commodity.code,Commodity.manufactor,"
				+ "Commodity.colour,Commodity.mysize,Orderrelevance.num,"
				+ "Commodity.name title  from orders left join Orderrelevance on "
				+ "Orderrelevance.orderid=orders.id left join Commodity on "
				+ "itemid=Commodity.id left join Receiver on Receiver.orderid=orders.id "
				+ "left join shop on shop.id=orders.shopid left join Friends on "
				+ "Friends.memberidb=orders.memberid left join member on member.id=memberida  where orders.status in(2,8) and orders.id>"+star+" and orders.id<"+end);

		if(type!=null)
			msql.setSql(msql.getSql()+" and orders.status="+type);
		
		AutoShopOne(uname);
		
		if(getMember(uname).getSuperadmin()!=1)
			msql.setSql(msql.getSql()+" and orders.onephone="+uname);
		
		msql.setSql(msql.getSql()+" order by orders.id");
		return mMemberService.exeSelectSql(msql);
	
	}
	private List<Map<String, Object>> getNewPurchase(Integer type,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select nvl(concat(Receiver.province,concat(Receiver.city,concat(Receiver.area,concat(Receiver.street,Receiver.detailed)))),shop.detailed)detailed, "
				+ "TO_CHAR(orders.id / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS CDATE "
				+ ",orders.id,Orderrelevance.youcode,num,Orderrelevance.Price,postfee,nvl(Receiver.phone,shop.shopphone)phone,nvl(Receiver.name,shop.shopname) name,"
				+ "decode(orders.status,1,'未付款',2,'已付款',3,'已发货',4,'已完成',5,'已关闭',6,'已到货',7,'已出货',8,'已接单',9,'已拒单',10,'已退单',11,'已退款')status,"
				+ "decode(orders.shippingtype,1,'自提',2,'送货',3,'门店直销')shippingtype,commodity.code,colour,mysize,manufactor,Orderrelevance.title"
				+ ",brand,''a from Orderrelevance left join orders on orderid=orders.id "
				+ " left join Receiver on Receiver.Orderid=Orderrelevance.orderid left join shop on shopid=shop.id "
				+ "left join commodity on Orderrelevance.youcode=commodity.YOUCODE where orders.status in(2,8) and orders.id>"+star+" and orders.id<"+end);
		
		if(type!=null)
			msql.setSql(msql.getSql()+" and orders.status="+type);
		
		AutoShopOne(uname);
		
		if(getMember(uname).getSuperadmin()!=1)
			msql.setSql(msql.getSql()+" and orders.onephone="+uname);
		
		msql.setSql(msql.getSql()+" order by orders.id");
		return mMemberService.exeSelectSql(msql);
		
	}
	@Auth
	@RequestMapping(value = "/web/selectPurchase/expor", method = RequestMethod.GET)
	public void selectPurchase(Integer type,HttpServletResponse response,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		
//		Set<String> sa = new HashSet<>();
//		
//		Sql msql = new Sql();
//		msql.setSql("SELECT ID FROM ORDERS WHERE ID>"+star+" AND ID<"+end +" and ");
//		List<Map<String, Object>> list = mMemberService.exeSelectSql(msql);
//		for (Map<String, Object> map : list) {
//			sa.add(map.get("ID").toString());
//		}
//		for (String str : sa) {
//			msql.setSql("select nvl(Receiver.name,shop.detailed)detailed, "
//					+ "nvl(Receiver.detailed,shop.shopname)name, "
//					+ "nvl(Receiver.phone,member.phone)phone,"
//					+ "orders.id,Commodity.code,Commodity.manufactor,"
//					+ "Commodity.colour,Commodity.mysize,Orderrelevance.num,"
//					+ "Commodity.name  from orders left join Orderrelevance on "
//					+ "Orderrelevance.orderid=orders.id left join Commodity on "
//					+ "itemid=Commodity.id left join Receiver on Receiver.orderid=orders.id "
//					+ "left join shop on shop.id=orders.shopid left join Friends on "
//					+ "Friends.memberidb=orders.memberid left join member on member.id=memberida where orders.id="+end+" and shop.shopname!='SYSTEM'");
//		}
//		
//		
//		
//		list = mMemberService.exeSelectSql(msql);
//		List<Exa> listExa = new ArrayList<>(list.size());
//		for (Map<String, Object> map : list) {
//			Exa mExa= new Exa();
//			mExa.setAddress(map.get("DETAILED").toString());
//			mExa.setCode(code);
//		}
//		Sql msql = new Sql();
//		msql.setSql("select nvl(Receiver.name,shop.detailed)detailed, "
//				+ "nvl(Receiver.detailed,shop.shopname)name, "
//				+ "nvl(Receiver.phone,member.phone)phone,Commodity.brand,"
//				+ "orders.id,Commodity.code,Commodity.manufactor,"
//				+ "Commodity.colour,Commodity.mysize,Orderrelevance.num,"
//				+ "Commodity.name title  from orders left join Orderrelevance on "
//				+ "Orderrelevance.orderid=orders.id left join Commodity on "
//				+ "itemid=Commodity.id left join Receiver on Receiver.orderid=orders.id "
//				+ "left join shop on shop.id=orders.shopid left join Friends on "
//				+ "Friends.memberidb=orders.memberid left join member on member.id=memberida  where orders.status=2 and orders.id>"+star+" and orders.id<"+end+" order by orders.id");
		
		List<Map<String, Object>> listmap = getPurchase(type,star, end,page,rows,uname);
		Set<String> sa = new HashSet<String>();
		List<Exa> listExa = new ArrayList<>();
		for (Map<String, Object> map : listmap) {
			sa.add(map.get("ID").toString());
		}
		for (String str : sa) {
			Exa mExa = new Exa();
			mExa.setCode(str);
			mExa.setmSaleDara(new ArrayList<SaleDara>());
			listExa.add(mExa);
		}
		for (Map<String, Object> map : listmap) {
			for (Exa exa : listExa) {
				if(exa.getCode().equals(map.get("ID").toString())){
					//地址
					Object obj = map.get("DETAILED");
					if(obj!=null)
						exa.setAddress(obj.toString());
					else
						exa.setAddress("");
					//姓名
					 obj = map.get("NAME");
					if(obj!=null)
						exa.setName(obj.toString());
					else
						exa.setName("");
					//电话
					 obj = map.get("PHONE");
					if(obj!=null)
						exa.setPhone(obj.toString());
					else
						exa.setPhone("");
					
					SaleDara mSaleDara = new SaleDara();
					 obj = map.get("CODE");
						if(obj!=null)
							mSaleDara.setCode(obj.toString());
						else
							mSaleDara.setCode("");
						
						obj = map.get("YOUCODE");
						if(obj!=null)
							mSaleDara.setBarcode(obj.toString());
						else
							mSaleDara.setBarcode("");
						
						obj = map.get("COLOUR");
						if(obj!=null)
							mSaleDara.setColour(obj.toString());
						else
							mSaleDara.setColour("");
						
						obj = map.get("MANUFACTOR");
						if(obj!=null)
							mSaleDara.setManufactor(obj.toString());
						else
							mSaleDara.setManufactor("");
						
						obj = map.get("TITLE");
						if(obj!=null)
							mSaleDara.setName(obj.toString());
						else
							mSaleDara.setName("");
						
						obj = map.get("NUM");
						if(obj!=null)
							mSaleDara.setNumber(obj.toString());
						else
							mSaleDara.setNumber("");
						
						obj = map.get("BRAND");
						if(obj!=null)
							mSaleDara.setBrand(obj.toString());
						else
							mSaleDara.setBrand("");
						
						obj = map.get("STATUS");
						if(obj!=null)
							mSaleDara.setType(obj.toString());
						else
							mSaleDara.setType("");
						
						obj = map.get("MYSIZE");
						if(obj!=null)
							mSaleDara.setSize(obj.toString());
						else
							mSaleDara.setSize("");
						
					exa.getmSaleDara().add(mSaleDara);
				}
			}
		}
		
		
		
		ExportExcel.ExportA(listExa, response);
		

	}
	
	
	
	/**
	 * 销售查询
	 * @param star 开始时间，
	 * @param end 结束时间
	 * 
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/data/shopMember", method = RequestMethod.POST)
	public RequestType select(String code,String uname,Long star,Long end,Integer page,Integer rows,HttpServletResponse response) throws Exception {
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		page=page==null?1:page;
		return sendTrueData(getshopMember(code,uname, star, end,page,rows));
	}
	@Auth(admin=true)
	@RequestMapping(value = "/data/exportShopMember", method = RequestMethod.GET)
	public void exportOrderrelevance(String code,String uname,Long star,Long end,Integer rows,HttpServletResponse response) throws Exception {
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		List<ShopMember> listShopMember = getshopMember(code,uname,star, end,1,rows);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("店铺编号");			key.add("code");
		title.add("店铺名字");			key.add("name");
		title.add("会员总数");   		key.add("summember");
		title.add("新增会员");  		key.add("newsummember");
		title.add("新增导购");			key.add("newsumclerk");
		title.add("活跃用户");			key.add("active");
		title.add("成交金额");			key.add("money");
		
		List<Map<String, Object>> listmap = new ArrayList<>();
		for (ShopMember mShopMember: listShopMember) {
			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("SUMMEMBER",mShopMember.getSummember());
			mMap.put("NEWSUMMEMBER",mShopMember.getNewsummember());
			mMap.put("NEWSUMCLERK",mShopMember.getNewsumclerk());
			mMap.put("ACTIVE",mShopMember.getActive());
			mMap.put("MONEY",mShopMember.getMoney());
			mMap.put("CODE",mShopMember.getCode());
			mMap.put("NAME",mShopMember.getName());
			listmap.add(mMap);
			}
		
		ExportExcel.Export(MyDate.stampToDate(star)+"   至   "+MyDate.stampToDate(end)+" 统计",title,key,listmap,response);
		
	}
	
	
	//日期，条码，数量，金额，地址，电话，联系人，订单编号
	
	private List<ShopMember> getshopMember(String code,String uname,Long star,Long end,Integer page,Integer rows) throws Exception {
		
		
		Sql msql = new Sql();
		msql.setSql("select id,code,shopname from shop where memberid !=1");
		msql.setPage(page);
		msql.setRows(rows);
		if(!Stringutil.isBlank(code)){
			msql.setSql(msql.getSql()+" and code='"+code+"'");
		}
		
		List<Map<String, Object>> ls =  mMemberService.exeSelectSql(msql);
		msql.setPage(null);
		msql.setRows(null);
		Map<String, Object> map;
		
		int size = ls.size();
		List<ShopMember> lm = new ArrayList<ShopMember>(size);
		
		for (int i=0;i<size;i++) {
			ShopMember mShopMember = new ShopMember();
			mShopMember.setCode(ls.get(i).get("CODE").toString());
			mShopMember.setName(ls.get(i).get("SHOPNAME").toString());
			//会员数
			msql.setSql(""
					+ "select "
					+ " (select nvl(count(*),0) from Friends where memberida in(select memberid from clerk where shopid="+ls.get(i).get("ID").toString()+"))summember,"
					+ " (select nvl(province,'暂无') from shop where id="+ls.get(i).get("ID").toString()+")province,"
					+ " (select nvl(count(*),0) from Friends where memberida in(select memberid from clerk where shopid="+ls.get(i).get("ID").toString()+") and id<"+end+" and id>"+star+")newsummember,"
					+ " (select nvl(count(*),0) from clerk where shopid="+ls.get(i).get("ID").toString()+" and id<"+end+" and id>"+star+")newsumclerk,"
					+ " (select count(*) from (select nvl(count(*),0),memberid from orders where memberid in(select memberidb from Friends where memberida "
					+ "in(select memberid from clerk where shopid="+ls.get(i).get("ID").toString()+")) and status!=1 and status!=10 and status!=11 and  id<"+end+" and id>"+star+" group by memberid))active,"
					+ " (select nvl(sum(payment),0) from orders where shopid="+ls.get(i).get("ID").toString()+" and status!=1  and status!=5 and status!=10 and status!=11  and id<"+end+" and id>"+star+") money "
					+ " from xxx");
			map = mMemberService.exeSelectSql(msql).get(0);
			mShopMember.setSummember(map.get("SUMMEMBER").toString());
			mShopMember.setNewsummember(map.get("NEWSUMMEMBER").toString());
			mShopMember.setNewsumclerk(map.get("NEWSUMCLERK").toString());
			mShopMember.setActive(map.get("ACTIVE").toString());
			mShopMember.setMoney(map.get("MONEY").toString());
			mShopMember.setProvince(map.get("PROVINCE").toString());
			lm.add(mShopMember);
		}
		//合计
		ShopMember mShopMember = new ShopMember();
		mShopMember.setCode("合计");
		mShopMember.setName("合计");
		mShopMember.setSummember("0");
		mShopMember.setNewsummember("0");
		mShopMember.setNewsumclerk("0");
		mShopMember.setActive("0");
		mShopMember.setMoney("0");
			for (ShopMember shopMember : lm) {
				mShopMember.setSummember(new BigDecimal(mShopMember.getSummember()).add(new BigDecimal(shopMember.getSummember())).toString());
				mShopMember.setNewsummember(new BigDecimal(mShopMember.getNewsummember()).add(new BigDecimal(shopMember.getNewsummember())).toString());
				mShopMember.setNewsumclerk(new BigDecimal(mShopMember.getNewsumclerk()).add(new BigDecimal(shopMember.getNewsumclerk())).toString());
				mShopMember.setActive(new BigDecimal(mShopMember.getActive()).add(new BigDecimal(shopMember.getActive())).toString());
				mShopMember.setMoney(new BigDecimal(mShopMember.getMoney()).add(new BigDecimal(shopMember.getMoney())).toString());
			}
		
		lm.add(mShopMember);
		
		Collections.reverse(lm);
		
		return lm;

	}
	//门店编号	区域	店名	线上销售额	会员总数	新增会员数	合伙人总数	新增合伙人数	分销商总数	新增分销商数	客户群总人数	客户群今日新增

	
	private List<Map<String, Object>> getselectshopdata(String stara,String enda) throws Exception {
		if(Stringutil.isBlank(stara))
			throw new RunException("开始时间不可为空");
		if(Stringutil.isBlank(enda))
			throw new RunException("结束时间不可为空");
		
		Long star = MyDate.dateToStamp(stara);
		Long end = MyDate.dateToStamp(enda);
		
		String[] BHs={"KLTN003","S0125","S0206","S0011","S0016","S0085","S0079","S0022","S0014","S0037"};
		String[] QYs={"直营托管区","成都区域","德绵区域","德绵区域","西南区域","西南区域","西南区域","东北区域","东北区域","东北区域"};
		String[] MZs={"双流中和店","仁寿清水店","什邡文化路店","广汉南兴店","雅安名山店","南溪文化路店","汉源富林大道店","安居拦江店","营山磨子街店","大竹竹阳店"};
		Sql msql = new Sql();
		List<Map<String, Object>> listmap = new ArrayList<>();
		int a =MZs.length;
		for (int i=0;i<a;i++) {
			Map<String, Object> map= new HashMap<String, Object>();
			map.put("BH", BHs[i]);
			map.put("QY", QYs[i]);
			map.put("MZ", MZs[i]);
			//销售
			msql.setSql("select nvl(sum(payment),0) payment from orders where shopid =(select id from shop where code='"
			+BHs[i]+"') and id>"+star+" and id<"+end);
			map.put("XX", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			//会员总数
			msql.setSql("select nvl(count(*),0) payment from Friends where memberida in"
					+ "(select memberid from clerk where shopid=(select id from shop where code='"
					+BHs[i]+"')) ");
			map.put("HYZS", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			//增加会员
			msql.setSql("select nvl(count(*),0) payment from Friends where memberida in"
					+ "(select memberid from clerk where shopid=(select id from shop where code='"
					+BHs[i]+"')) and id>"+star+" and id<"+end);
			map.put("XZHY", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			//合伙人总数
			msql.setSql("select nvl(count(*),0) payment from Identity where "
					+ " shopid=(select id from shop where code='"
					+BHs[i]+"') and type =2 ");
			map.put("HHRZS", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			
			//新增合伙人总数
			msql.setSql("select nvl(count(*),0) payment from Identity where "
					+ " shopid=(select id from shop where code='"
					+BHs[i]+"') and type =2 and id>"+star+" and id<"+end);
			map.put("XZHHR", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			
			//分销商总数
			msql.setSql("select nvl(count(*),0) payment from Identity where "
					+ " shopid=(select id from shop where code='"
					+BHs[i]+"') and type =1 ");
			map.put("FXSZS", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			
			//新增分销商
			msql.setSql("select nvl(count(*),0) payment from Identity where "
					+ " shopid=(select id from shop where code='"
					+BHs[i]+"') and type =1 and id>"+star+" and id<"+end);
			map.put("XZFXS", mMemberService.exeSelectSql(msql).get(0).get("PAYMENT"));
			
			listmap.add(map);
		}
		
		
		return listmap;
		
	}
	
	/**
	 * 销售查询
	 * @param star 开始时间，
	 * @param end 结束时间
	 * 
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/data/getselectshopdata", method = RequestMethod.POST)
	public RequestType getselectshopdata(String star,String end,Integer page,Integer rows,HttpServletResponse response) throws Exception {
		return sendTrueData(getselectshopdata(star, end));
	}
	@Auth(admin=true)
	@RequestMapping(value = "/data/exportgetselectshopdata", method = RequestMethod.GET)
	public void exportgetselectshopdata(String star,String end,Integer rows,HttpServletResponse response) throws Exception {
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		
		List<Map<String, Object>> listmap = getselectshopdata(star, end);
		title.add("店铺编号");			key.add("bh");
		title.add("区域");			key.add("QY");
		title.add("名字");   			key.add("MZ");
		title.add("销售");  			key.add("XX");
		title.add("会员总数");			key.add("HYZS");
		title.add("新增会员");			key.add("XZHY");
		title.add("合伙人总数");		key.add("HHRZS");
		title.add("新增合伙人");		key.add("XZHHR");
		title.add("分销商总数");		key.add("FXSZS");
		title.add("新增分销商");		key.add("XZFXS");
		
		ExportExcel.Export(star+"   至   "+end+" 统计",title,key,listmap,response);
		
	}
	@Auth(admin=true)
	@RequestMapping(value = "/data/exporone1", method = RequestMethod.GET)
	public void exporone1(String star,String end,Integer rows,HttpServletResponse response) {
		try {
			Long s1 = MyDate.dateToStamp(star);
			Long s2 = MyDate.dateToStamp(end);
			Sql msql = new Sql();
			msql.setSql("select orderrelevance.*,orders.shopname,supplier from orderrelevance left join orders on orderid=orders.id "
					+ "left join Commodity on itemid=Commodity.id "
					+ "where orderrelevance.id<"+s2+" and orderrelevance.id>"+s1+"  and orders.id is not null");
			List<String> title = new ArrayList<String>();
			List<String> key = new ArrayList<String>();
			
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			title.add("商品名字");			key.add("title");
			title.add("单价");			key.add("PRICE");
			title.add("合计");   			key.add("TOTALFEE");
			title.add("大类");  			key.add("TYPE1");
			title.add("中类");			key.add("TYPE2");
			title.add("小类");			key.add("TYPE3");
			title.add("细类");			key.add("TYPE4");
			title.add("数量");			key.add("NUM");
			title.add("条码");		key.add("YOUCODE");
			title.add("店铺名字");		key.add("SHOPNAME");
			title.add("供应商");		key.add("SUPPLIER");
			
			ExportExcel.Export(star+"   至   "+end+" 统计",title,key,listmap,response);
		} catch (Exception e) {
			throw new RunException("时间是不是输错了，yyyy-MM-dd HH:mm:ss，如2019-04-01 15:30:30");
		}
		
		
	}
}
