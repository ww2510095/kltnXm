package com.bm.webapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.advertisement.Advertisement;
import com.bm.advertisement.AdvertisementService;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.MyDate;
import com.bm.commodity.Commodity;
import com.bm.commodity.CommodityService;
import com.bm.commodity.Mk;
import com.bm.ordersRule.OrdersRule;
import com.bm.shop.Shop;
import com.myjar.Stringutil;

@RestController
@Api(tags = "app抽象接口")
public class App extends BaseController{
	/**
	 * app需求整理
	 * */
	
	@Autowired
	private AdvertisementService mAdvertisementService;//广告
	@Autowired
	private CommodityService commodityService;//商品
	
	

	/**
	 * 通过对imageDtaa的坐标进行排序
	 */
	public static List<Xa> Sort(Set<Xa> list1) {
		List<Xa> list = new ArrayList<>();
		for (Xa xa : list1) {
			list.add(xa);
		}
		Xa temp;
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1 - i; j++) {
				if (list.get(j).getINDEXSBY() < list.get(j + 1).getINDEXSBY()) // 交换两数位置
				{
					temp = list.get(j);

					list.set(j, list.get(j + 1));
					list.set(j + 1, temp);
				}
			}
		}
		return list;
	}
	public class Xa{
		private String INDEXS;
		private Integer INDEXSBY;
		public String getINDEXS() {
			return INDEXS;
		}
		public void setINDEXS(String iNDEXS) {
			INDEXS = iNDEXS;
		}
		public Integer getINDEXSBY() {
			return INDEXSBY;
		}
		public void setINDEXSBY(Integer iNDEXSBY) {
			INDEXSBY = iNDEXSBY;
		}
		
		
		
		
	}
	
	/**
	 * app首页
	 * */
	@RequestMapping(value ="/index", method = RequestMethod.POST) 
	public RequestType index(String index,HttpServletRequest req,Commodity mCommodity,String mk) throws Exception{
		Map<String, Object> map =new HashMap<String, Object>();
		
		Advertisement mAdvertisement= new Advertisement();
		mAdvertisement.setKey(index==null?"1":index);
		
		map.put("TOP", mAdvertisementService.getALL(mAdvertisement));//轮播图
		mAdvertisement.setKey("0");//广告
		map.put("SYSTEMADVERTISEMENT", mAdvertisementService.getALL(mAdvertisement).get(0));//广告图
	
		mAdvertisement.setKey("4");//首页轮播图下面的广告图
		map.put("TOPBelow", mAdvertisementService.getALL(mAdvertisement,"orderby",1));
		
		mAdvertisement.setKey("2");//弹出的广告图
		
		try {
			map.put("Eject", mAdvertisementService.getALL(mAdvertisement).get(0));//弹出的广告图
		} catch (Exception e) {
			map.put("Eject", null);//弹出的广告图
		}
		
		Sql msql = new Sql();
		if("mk".equals(mk)){
			List<Mk> list = new ArrayList<Mk>();
			msql.setSql("select  ZDYHD INDEXS,nvl(max(to_number(indexsby)),-1) indexsby from COMMODITYkey where ZDYHD is not null group by ZDYHD");
			msql.setOrderbykey("indexsby");
			msql.setOrderbytype(1);
			boolean b=false;
			List<Xa> listmap =GsonUtil.fromJsonList(GsonUtil.toJsonString(commodityService.exeSelectSql(msql)), Xa.class) ;
			if(listmap.size()==0||Stringutil.isBlank(listmap.get(0).getINDEXS())){
				msql.setSql("select  indexs,nvl(max(to_number(indexsby)),-1) indexsby from COMMODITYkey where indexs is not null group by indexs");
				listmap =GsonUtil.fromJsonList(GsonUtil.toJsonString(commodityService.exeSelectSql(msql)), Xa.class) ;
				b=true;
			}

			msql.setOrderbykey("vip");
			msql.setOrderbytype(1);
			Set<Xa> indexs = new HashSet<Xa>();
			for (Xa map2 : listmap) {
				if(map2.getINDEXS()==null)continue;
				indexs.add(map2);
			}
			List<Xa> lx = Sort(indexs);
			msql.setRows(10);
			for (Xa str : lx) {
				if(b) {
					msql.setSql("select   max(ms)ms,max(ys)ys,max(pdd)pdd, commoditykeyid,NAME,INTRODUCTION,max(vip)vip,originalprice,min(PRICE)PRICE,MAINIMAGE from COMMODITY where type=1 and "
							+ "INDEXS='" + str.getINDEXS() + "' group by  commoditykeyid,NAME,INTRODUCTION,originalprice ,MAINIMAGE ");
				}else{
					msql.setSql("select   max(ms)ms,max(ys)ys,max(pdd)pdd, commoditykeyid,NAME,INTRODUCTION,max(vip)vip,originalprice,min(PRICE)PRICE,MAINIMAGE from COMMODITY where type=1 and "
							+ "ZDYHD='" + str.getINDEXS() + "' group by  commoditykeyid,NAME,INTRODUCTION,originalprice ,MAINIMAGE ");
				}
				List<Map<String, Object>> listmap1 = commodityService.exeSelectSql(msql);
				for (Map<String, Object> map3 : listmap1) {
					map3.put("MAINIMAGE", map3.get("MAINIMAGE").toString().split(";")[0]);
				}
				
				Mk mmk = new Mk();
				mmk.setMk(str.getINDEXS());
				mmk.setListmap(listmap1);
				list.add(mmk);
			}
			msql.setRows(null);
			map.put("COMMODITY", list);
			msql.setOrderbykey(null);
			msql.setOrderbytype(0);
			
		}else{
			msql.setSql("select   DISTINCT commoditykeyid,NAME,INTRODUCTION,vip,originalprice,PRICE,MAINIMAGE from COMMODITY where type=1 order by vip desc");
			List<Map<String, Object>> listmap = commodityService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				map2.put("MAINIMAGE", map2.get("MAINIMAGE").toString().split(";")[0]);
			}
			map.put("COMMODITY", listmap);
		}
		
		msql.setSql("select largeclass from (select max(id)a,COMMODITY.largeclass  from COMMODITY where type!=-1 group by largeclass order by a desc )");
		
		List<Map<String, Object>> list  = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 =new HashMap<String, Object>();
		map1.put("LARGECLASS", "推荐");
		list.add(map1);
		list.addAll(commodityService.exeSelectSql(msql));
		map.put("TYPE", list);
		return sendTrueData(map);
	}
	
	/**
	 * app分类
	 * */
	@RequestMapping(value ="/appIndexType", method = RequestMethod.POST) 
	public RequestType index(String largeclass) throws Exception{
		Map<String, Object> map =new HashMap<String, Object>();
		Sql msql = new Sql();
		if(Stringutil.isBlank(largeclass))
			msql.setSql("select largeclass from (select max(id)a,COMMODITY.largeclass  from COMMODITY where type!=-1 group by largeclass order by a desc )");
		else
			msql.setSql("select  DISTINCT largeclass   from COMMODITY where largeclass='"+largeclass+"'");
		//大类
		List<Commodity> list  = IBeanUtil.ListMap2ListJavaBean(commodityService.exeSelectSql(msql), Commodity.class);
		Advertisement mAdvertisement =new Advertisement();
		if(list.size()!=0){
			String[] strs = new String[list.size()];
			int size = strs.length;
			for (int i = 0; i < size; i++) {
				strs[i]=list.get(i).getLargeclass();
			}
			map.put("LEFT", strs);
			mAdvertisement.setDescribe(list.get(0).getLargeclass());
			mAdvertisement.setKey("-1");
			map.put("TOP", mAdvertisementService.getALL(mAdvertisement,1,10,0));
			mAdvertisement.setBys(list.get(0).getLargeclass());
			mAdvertisement.setDescribe(list.get(0).getFineclass());
			mAdvertisement.setKey("-3");
			map.put("BELOW", mAdvertisementService.getALL(mAdvertisement,1,1000,0));
			
		}
		return sendTrueData(map);
	}
	/**
	 * 分类递推
	 * */
	@RequestMapping(value ="/commoditytype", method = RequestMethod.POST) 
	public RequestType commoditytype(String largeclass,String inclass,String smallclass) throws Exception{
		Sql msql = new Sql();
		if(!Stringutil.isBlank(largeclass))
			msql.setSql("select DISTINCT inclass from COMMODITYkey");
		else if(!Stringutil.isBlank(inclass))
			msql.setSql("select DISTINCT smallclass from COMMODITYkey");
		else if(!Stringutil.isBlank(inclass))
			msql.setSql("select DISTINCT fineclass from COMMODITYkey");
		else
			msql.setSql("select DISTINCT largeclass from COMMODITYkey");
		return sendTrueData(commodityService.exeSelectSql(msql));
	}
	/**
	 * 细类模糊
	 * */
	@RequestMapping(value ="/commoditytype1", method = RequestMethod.POST) 
	public RequestType commoditytype1(String a) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select DISTINCT fineclass from COMMODITYkey where fineclass like '%"+a+"%'");
		return sendTrueData(commodityService.exeSelectSql(msql));
	}
	/**
	 * 我的推荐收入
	 * */
	@Auth
	@RequestMapping(value ="/Recommend", method = RequestMethod.POST) 
	public RequestType Recommend(String uname,Integer rows,Integer page,Long star,Long end) throws Exception{
		star=star==null?0L:star;
		end=end==null?System.currentTimeMillis():end;
		
		Map<String, Object> map =new HashMap<String, Object>();
//		String sql1 ="select ";
//		String sql2="from Orderrelevance a left join orders on orders.ordernumber=a.ordernum left join "
//				+ "Friends on orders.memberid=memberidb "
//				+ "left join member on member.id=orders.memberid "
//				+ "where memberida="+getLogin(uname).getUserid();
//		Sql msql = new Sql();
//		msql.setRows(rows==null?10:rows);
//		msql.setPage(page);
//		msql.setSql(sql1+ "member.uname,member.nickname,a.*,decode(orders.shippingtype,1,'自提',2,'送货上门',3,'无需物流') shippingtype "+sql2);
//		map.put("data",commodityService.exeSelectSql(msql));
//		msql.setRows(null);
//		msql.setPage(null);
//		msql.setSql("select nvl(sum(clerk),0) A from Sharingdetails left join  orders  on orders.id=ordersid "
//				+ "left join Friends on orders.memberid=memberidb where memberida="+getLogin(uname).getUserid());
//		map.put("sum", commodityService.exeSelectSql(msql).get(0).get("A"));
		
//		Sql msql = new Sql();
//		msql.setSql("select oneid from shop where id=(select shopid from clerk where memberid='"+getLogin(uname).getUserid()+"')");//先查绑定店铺的代理商
//		List<Map<String, Object>> listmap = commodityService.exeSelectSql(msql);
//		if(listmap.size()==0||listmap.get(0)==null||listmap.get(0).get("ONEID")==null)
//			return sendFalse("未找到代理商");
//		msql.setSql("select * from OrdersRule where shoponeid='"+listmap.get(0).get("ONEID").toString()+"' order by id desc ");//查询代理商的分成
//		listmap = commodityService.exeSelectSql(msql);
//		if(listmap.size()==0||listmap.get(0)==null)
//			return sendFalse("你的代理商还没有与系统协商分销规则");
//		
//		OrdersRule le = IBeanUtil.Map2JavaBean(listmap.get(0), OrdersRule.class);
//		
//		if(le==null||le.getClerk()==null)
//			return sendFalse("你的代理商还没有与系统协商分销规则");
//		
//		msql.setPage(page);
//		msql.setRows(rows);
//		msql.setSql("select a.*,uname,nickname,decode(orders.shippingtype,1,'自提',2,'送货上门',3,'无需物流') shippingtype from  Orderrelevance  a left join orders on orders.id= a.orderid "
//				+ "left join member on member.id=orders.memberid left join Friends on memberidb=orders.memberid "
//				+ " where orders.status=4 and orders.id>"+star+" and orders.id< "+end+" and memberida="+getLogin(uname).getUserid());
//		
//		map.put("data",commodityService.exeSelectSql(msql));//推荐人购买的商品，不计算活动的情况下
//		
//		msql.setSql("select nvl(sum(payment),0) sum from orders left join Friends on memberidb=orders.memberid where orders.status=4 and orders.id>"+star+" and orders.id< "+end+" and memberida="+getLogin(uname).getUserid());
//		msql.setPage(null);
//		msql.setRows(null);
//		listmap = commodityService.exeSelectSql(msql);//推荐人购买的商品总价
//		BigDecimal b = new BigDecimal("0");
//		try {
//			b=new BigDecimal(listmap.get(0).get("SUM").toString()).multiply(le.getClerk()).divide(new BigDecimal("100"));
//			map.put("sum", b);
//		} catch (Exception e) {
//			map.put("sum", 0);
//		}
//				
		
		Sql msql = new Sql();
		//总价
		msql.setSql("select nvl(sum(clerk),0) sum from Sharingdetails where memberid="+getLogin(uname).getUserid());
		map.put("sum", commodityService.exeSelectSql(msql).get(0).get("SUM"));
		//自己所在的店铺
		msql.setSql("select * from shop where id = (select shopid from Clerk where memberid="+getLogin(uname).getUserid()+")");
		
		OrdersRule or;
		try {
			//分成
			msql.setSql("select *  from OrdersRule where shoponeid="+mMemberService.exeSelectSql(msql).get(0).get("ONEID").toString());
			msql.setOrderbykey("id");
			msql.setOrderbytype(1);
			 or = IBeanUtil.Map2JavaBean(commodityService.exeSelectSql(msql).get(0),OrdersRule.class);
			 if(or==null)
				 return sendFalse("你的代理商还没有与系统协商分销规则");
		} catch (Exception e) {
			return sendFalse("你的代理商还没有与系统协商分销规则");
		}
		
		msql.setOrderbykey(null);
		msql.setOrderbytype(null);
		
		//单笔
		msql.setSql("select DISTINCT Orderrelevance.id,Orderrelevance.orderid,Orderrelevance.num,Orderrelevance.title,Orderrelevance.price,"
				+ "Orderrelevance.totalfee,Orderrelevance.promotionid,Orderrelevance.promotiontitle,Orderrelevance.youcode,"
				+ "Orderrelevance.reduction,Orderrelevance.memberid,Orderrelevance.shippingtype,Orderrelevance.nickname,"
				+ "Orderrelevance.phone from Sharingdetails  left join Orderrelevance on Sharingdetails.memberid1 = Orderrelevance.memberid "
				+ " left join orders on orders.id=Orderrelevance.orderid "
				+ "where orders.status=4 and Sharingdetails.memberid ="+getLogin(uname).getUserid()+" and Orderrelevance.id is not null union all "
				+ "select DISTINCT Orderrelevance.id,Orderrelevance.orderid,Orderrelevance.num,Orderrelevance.title,"
				+ "Orderrelevance.price,to_number('-' ||Orderrelevance.totalfee ),Orderrelevance.promotionid,Orderrelevance.promotiontitle,"
				+ "Orderrelevance.youcode,Orderrelevance.reduction,Orderrelevance.memberid,Orderrelevance.shippingtype,Orderrelevance.nickname,"
				+ "Orderrelevance.phone from Returngoods  left join Orderrelevance on Orderrelevance.id=orderrelevanceid left join Sharingdetails  "
				+ "on Sharingdetails.memberid1 = Orderrelevance.memberid where Sharingdetails.memberid ="+getLogin(uname).getUserid()+" and clerk<0");
//		//单笔
//		msql.setSql("select DISTINCT Orderrelevance.* from Sharingdetails left join Orderrelevance on Sharingdetails.memberid1=Orderrelevance.memberid where Sharingdetails.memberid="+getLogin(uname).getUserid()+" and Orderrelevance.id is not null"
//				+ "   union all "
//				+ "select DISTINCT Orderrelevance.* from Returngoods  left join Orderrelevance  on Orderrelevance.id=orderrelevanceid left join Sharingdetails"
//				+ " on Sharingdetails.memberid1 = Orderrelevance.memberid where Sharingdetails.memberid ="+getLogin(uname).getUserid()+" and clerk<0"
//				+ " ");
		msql.setSql("select * from ("+msql.getSql()+") order by id desc,totalfee");
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> lm = commodityService.exeSelectSql(msql);
		for (Map<String, Object> map2 : lm) {
			map2.put("CLERK",new BigDecimal(map2.get("TOTALFEE")+"").divide(new BigDecimal("100")).multiply(or.getClerk()).multiply(new BigDecimal(map2.get("NUM")+"")) );
		}
		map.put("data", lm);
		return sendTrueData(map);
		
	}
	
	
	
	/**
	 * 我的推荐收入2.0
	 * */
	@Auth
	@RequestMapping(value ="/Recommend2", method = RequestMethod.POST) 
	public RequestType Recommend2(String uname,Integer rows,Integer page) throws Exception{
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("suma", 0);
		map.put("sum1a", 0);
		map.put("sum2a", 0);
		
		map.put("sumb", 0);
		map.put("sum1b", 0);
		map.put("sum2b", 0);
		
		map.put("sumc", 0);
		map.put("sum1c", 0);
		map.put("sum2c", 0);
		
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
		
		map.put("type", a);
		List<Map<String, Object>> listmap;
		if(a==0){
			//店主门店贡献
			msql.setSql("select sum(num4) num, state from Commission where num2=0 and num3=0 and memberid4="+getLogin(uname).getUserid()+" group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sumb", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("suma", map2.get("NUM"));
				}else {
					map.put("sumc", map2.get("NUM"));
				}
			}
//			//导购
//			msql.setSql("select sum(num1) num, state from Commission where memberid1="+getLogin(uname).getUserid()+" group by state");
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				if(map2.get("STATE").equals("1")){
//					map.put("sumb", new BigDecimal(map.get("sumb").toString()).add(new BigDecimal(map2.get("NUM").toString())));
//				}else if(map2.get("STATE").equals("2")){
//					map.put("suma", new BigDecimal(map.get("suma").toString()).add(new BigDecimal(map2.get("NUM").toString())));
//				}else {
//					map.put("sumc", new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(map2.get("NUM").toString())));
//				}
//			}
			//线上店主
			msql.setSql("select sum(num4) num, state from Commission where memberid2 is not null and memberid3 is null and memberid4="+getLogin(uname).getUserid()+" group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sum2b", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("sum2a", map2.get("NUM"));
				}else {
					map.put("sum2c", map2.get("NUM"));
				}
			}
			//经销商
			msql.setSql("select sum(num4) num, state from Commission where memberid3 is not null and memberid4="+getLogin(uname).getUserid()+" group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sum1b", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("sum1a", map2.get("NUM"));
				}else {
					map.put("sum1c", map2.get("NUM"));
				}
			}
			
		}else{

			//经销商
			msql.setSql("select sum(my_to_app_je(3,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num, state from Commission  group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sum1b", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("sum1a", map2.get("NUM"));
				}else {
					map.put("sum1c", map2.get("NUM"));
				}
			}
			//线上店主
			msql.setSql("select sum(my_to_app_je(2,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num, state from Commission   group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sum2b", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("sum2a", map2.get("NUM"));
				}else {
					map.put("sum2c", map2.get("NUM"));
				}
			}

			//门店贡献
			msql.setSql("select sum(my_to_app_je(1,"+getLogin(uname).getUserid()+",memberid1,memberid2,memberid3,memberid4,num1,num2,num3,num4)) num, state from Commission  group by state");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map2 : listmap) {
				if(map2.get("STATE").equals("1")){
					map.put("sumb", map2.get("NUM"));
				}else if(map2.get("STATE").equals("2")){
					map.put("suma", map2.get("NUM"));
				}else {
					map.put("sumc", map2.get("NUM"));
				}
			}
		
		
		}
			
//			if(a==1){
//			//经销商
//			msql.setSql("select sum(numsu1) num, state from Commission where memberid3="+getLogin(uname).getUserid()+" and num2=0 group by state");
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				if(map2.get("STATE").equals("1")){
//					map.put("sum1b", map2.get("NUM"));
//				}else if(map2.get("STATE").equals("2")){
//					map.put("sum1a", map2.get("NUM"));
//				}else {
//					map.put("sum1c", map2.get("NUM"));
//				}
//			}
//			//线上店主
//			msql.setSql("select sum(numsu1) num, state from Commission where memberid3="+getLogin(uname).getUserid()+"  and num2!=0  group by state");
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				if(map2.get("STATE").equals("1")){
//					map.put("sum2b", map2.get("NUM"));
//				}else if(map2.get("STATE").equals("2")){
//					map.put("sum2a", map2.get("NUM"));
//				}else {
//					map.put("sum2c", map2.get("NUM"));
//				}
//			}
//		}else if(a==2){
//			//线上店主
//			msql.setSql("select sum(num2) num, state from Commission where memberid2="+getLogin(uname).getUserid()+" group by state");
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				if(map2.get("STATE").equals("1")){
//					map.put("sum2b", map2.get("NUM"));
//				}else if(map2.get("STATE").equals("2")){
//					map.put("sum2a", map2.get("NUM"));
//				}else {
//					map.put("sum2c", map2.get("NUM"));
//				}
//			}
//		}else{
//			//线上店主
//			msql.setSql("select sum(num1) num, state from Commission where memberid1="+getLogin(uname).getUserid()+" group by state");
//			listmap = mMemberService.exeSelectSql(msql);
//			for (Map<String, Object> map2 : listmap) {
//				if(map2.get("STATE").equals("1")){
//					map.put("sumb", map2.get("NUM"));
//				}else if(map2.get("STATE").equals("2")){
//					map.put("suma", map2.get("NUM"));
//				}else {
//					map.put("sumc", map2.get("NUM"));
//				}
//			}
//		}
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	


		
	try {
		msql.setSql("select type from Identity where memberid="+getLogin(uname).getUserid());
		map.put("type",mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString());
	} catch (Exception e) {
		if(getMember(uname).getmShop()==null)
			map.put("type",3);
		else
			map.put("type",0);
	}
		map.put("a",new BigDecimal(map.get("suma").toString()).add(new BigDecimal(map.get("sum1a").toString())).add(new BigDecimal(map.get("sum2a").toString())));
		map.put("b",new BigDecimal(map.get("sumb").toString()).add(new BigDecimal(map.get("sum1b").toString())).add(new BigDecimal(map.get("sum2b").toString())));
		map.put("c",new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(map.get("sum1c").toString())).add(new BigDecimal(map.get("sum2c").toString())));
		map.put("data", new ArrayList<>());
		return sendTrueData(map);
	}
	/**
	 * 我的推荐收入2.0
	 * */
//	@Auth
//	@RequestMapping(value ="/Recommend2", method = RequestMethod.POST) 
//	public RequestType Recommend2(String uname,Integer rows,Integer page) throws Exception{
//		Map<String, Object> map =new HashMap<String, Object>();
//		Sql msql = new Sql();
//		//结算规则
//		  List<OrdersRule> lOrdersRule;//结算规则
//		  msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=1 order by id desc)"
//		   				+ "where rownum=1"
//		   				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id =  (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=2 order by id desc)"
//		   				+ "where rownum=1");
//		   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(commodityService.exeSelectSql(msql),OrdersRule.class);
//		   if(lOrdersRule.size()!=2)return sendFalse("您的供应商还没有跟系统协商规则");
//			   OrdersRule or ;
//		
//		Long time=1538323200000L;//2018年10月1日0点0分0秒
//		//未入账金额	   
//		//百分之2的分成金额
//		msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and shippingtype=3 and "
//				+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time
//				+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//		BigDecimal Unaccounted =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//		or=lOrdersRule.get(1);
//		//实得金额
//		Unaccounted=Unaccounted.multiply(or.getClerk());
//		
//		//百分之10分成
//		msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and shippingtype!=3 and "
//				+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and id>"+time
//				+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//		BigDecimal Unaccounted1 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//		or=lOrdersRule.get(0);
//		
//		
//		//单品减9，
//		msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
//				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//				+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//		
////		msql.setSql(
////				 "select nvl(count(*)*9,0) count from ("
////				+ "select Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
////				+ " left join orders on orders.id=orderid"
////				+ " where status in("+getOrdersStatusTrue()+") and orders.id not in(select ordersid from Sharingdetails)  and "
////				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
////				+" group by Commoditykeyid,orderid)");
//		Unaccounted1=Unaccounted1.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//		//导购拼单+4
////		msql.setSql("select nvl((sum(num)-count(*))*4,0) count  from Orderrelevance left join orders on orders.id=orderid "
////				+ "where status in("+getOrdersStatusTrue()+") and orders.id not in(select ordersid from Sharingdetails)  and "
////				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype=3 and Orderrelevance.id>"+time);
//		
//		//导购拼单+4
//		msql.setSql("select nvl((sum(a)-count(*))*400,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//				+ "left join orders on orders.id=orderid "
//				+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  "
//				+ " and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")"
//				+ "and "
//				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//				+" group by Commoditykeyid,orderid)");
//		
//		//分成金额
//		Unaccounted1=Unaccounted1.multiply(or.getClerk());
//		//实得金额
//		Unaccounted1=Unaccounted1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			   
//		//已入账金额
////		//百分之2的分成金额
////		msql.setSql("select nvl(sum(clerk),0)"
////				+ " payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype=3 and Sharingdetails.memberid="+
////				getLogin(uname).getUserid()
////				+" and orders.id>"+time);
////		BigDecimal b2 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
////		//百分之10分成
////		msql.setSql("select nvl(sum(clerk),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and Sharingdetails.memberid="+
////		getLogin(uname).getUserid()+" and orders.id>"+time);
////		BigDecimal b3 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
////		
//		
//		
//		
//		
//		
//		//已入账金额
//				//百分之2的分成金额
//						msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and shippingtype=3 and "
//								+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time
//								+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//						BigDecimal HaveBeenAccountedFor =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						or=lOrdersRule.get(1);
//						//实得金额
//						HaveBeenAccountedFor=HaveBeenAccountedFor.multiply(or.getClerk());
//						
//						//百分之10分成
//						msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and shippingtype!=3 and "
//								+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and id>"+time
//								+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//						BigDecimal HaveBeenAccountedFor1 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//						or=lOrdersRule.get(0);
//						
//						
//						//单品减9，
//						msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//								+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//								+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//						
//						HaveBeenAccountedFor1=HaveBeenAccountedFor1.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*400,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								+ "left join orders on orders.id=orderid "
//								+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//								+ " and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")"
//								+ "and "
//								+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//								+" group by Commoditykeyid,orderid)");
//						
//						//分成金额
//						HaveBeenAccountedFor1=HaveBeenAccountedFor1.multiply(or.getClerk());
//						//实得金额
//						HaveBeenAccountedFor1=HaveBeenAccountedFor1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				
//				
//
//						
//						
//						
//						
//						
//						
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		if(getMember(uname).getmShop()!=null){
//			
//			//未入账金额	   
//			//百分之2的分成金额
//			msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") and  autosystem=0 and shippingtype=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal Unaccounted4 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(1);
//			Unaccounted4=Unaccounted4.multiply(or.getShop());
//			//百分之10分成
//			msql.setSql("select nvl(sum(payment) ,0)payment  from orders where status in("+getOrdersStatusTrue()+") and  autosystem=0 and shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal Unaccounted5 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(0);
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid "
//					+ " where status in("+getOrdersStatusTrue()+") and autosystem=0 and orders.shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			
//			Unaccounted5=Unaccounted5.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			Unaccounted5=Unaccounted5.multiply(or.getShop());
//			
////			//已入账金额
////			//百分之2的分成金额
//////			msql.setSql("select nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where "
//////					+ "shippingtype=3 and Sharingdetails.memberid in"
//////					+ "(select memberid from clerk where shopid="+getMember(uname).getmShop().getId()+")and Sharingdetails.id>"+time);
////			msql.setSql("select nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where "
////					+ "shippingtype=3  and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time);
////			BigDecimal b6 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
////			//百分之10分成
////			msql.setSql("select  nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and "
////					+ "  shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time);
//////			msql.setSql("select  nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and Sharingdetails.memberid in"
//////					+ "(select memberid from clerk where shopid="+getMember(uname).getmShop().getId()+")and Sharingdetails.id>"+time);
////			BigDecimal b7 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			
//			
//			
//			//已入账金额	   
//			//百分之2的分成金额
//			msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") and  autosystem=1 and shippingtype=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal HaveBeenAccountedFo4 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(1);
//			HaveBeenAccountedFo4=HaveBeenAccountedFo4.multiply(or.getShop());
//			//百分之10分成
//			msql.setSql("select nvl(sum(payment) ,0)payment  from orders where status in("+getOrdersStatusTrue()+") and  autosystem=1 and shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal HaveBeenAccountedFor5 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(0);
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid "
//					+ " where status in("+getOrdersStatusTrue()+") and autosystem=1 and orders.shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			
//			HaveBeenAccountedFor5=HaveBeenAccountedFor5.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			HaveBeenAccountedFor5=HaveBeenAccountedFor5.multiply(or.getShop());
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			
//			map.put("sumb", Unaccounted.add(Unaccounted1).add(Unaccounted4).add(Unaccounted5).divide(new BigDecimal(100)));
//			map.put("suma", HaveBeenAccountedFor.add(HaveBeenAccountedFor1).add(HaveBeenAccountedFo4).add(HaveBeenAccountedFor5).divide(new BigDecimal(100)));
//			//作为导购的情况下金额
////			msql.setSql("select (nvl(sum(clerk),0))/100 sum from "
////					+ "Sharingdetails where memberid="+
////					getLogin(uname).getUserid()+" and state=1 and Sharingdetails.id>"+time);
////			map.put("sumc", commodityService.exeSelectSql(msql).get(0).get("SUM"));
////			
////			//作为店铺的情况下金额
////			msql.setSql("select (nvl(sum(shop),0))/100 sum from "
////					+ "Sharingdetails left join orders on ordersid=orders.id where shopid="+
////					getMember(uname).getmShop().getId()+" and state=1 and Sharingdetails.id>"+time);
//		
//			
//			
//			
//			
//			
//			
//			
//			
//			//已结算金额	   
//			//百分之2的分成金额
//			msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and  autosystem=1 and shippingtype=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal AlreadySettled =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(1);
//			AlreadySettled=AlreadySettled.multiply(or.getShop());
//			msql.setSql("select nvl(sum(payment),0) payment from orders where shippingtype=3  and orders.status in("+getOrdersStatusTrue()+") and memberidsu="+getLogin(uname).getUserid()+" and memberid not in (select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			AlreadySettled=AlreadySettled.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString()).multiply(or.getClerk()));
//			
//			//百分之10分成
//			msql.setSql("select nvl(sum(payment) ,0)payment  from orders where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and  autosystem=1 and shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			BigDecimal AlreadySettled2 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			or=lOrdersRule.get(0);
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid "
//					+ " where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and autosystem=1 and orders.shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time
//					+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			
//			AlreadySettled2=AlreadySettled2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			AlreadySettled2=AlreadySettled2.multiply(or.getShop());
//			
//			msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and shippingtype!=3 and memberidsu="+getLogin(uname).getUserid()+" and memberid not in (select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			
//			BigDecimal ba = new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid "
//					+ " where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and autosystem=1 and orders.shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time
//					+" and orders.memberidsu="+getLogin(uname).getUserid()+" and orders.memberid not in (select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//			ba=ba.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//			AlreadySettled2=AlreadySettled2.add(ba.multiply(or.getClerk()));
//			
//			
//			//导购拼单+4
//			msql.setSql("select nvl((sum(a)-count(*))*400,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//					+ "left join orders on orders.id=orderid "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//					+ " and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")"
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and "
//					+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//					+" group by Commoditykeyid,orderid)");
//			
//			AlreadySettled2=AlreadySettled2.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));			
//			
//			
//			
//			
//			
//			
//			
//			
//			map.put("sumc", AlreadySettled.add(AlreadySettled2) .divide(new BigDecimal(100)));
//			
//		}else{
//			map.put("suma", HaveBeenAccountedFor.add(HaveBeenAccountedFor1).divide(new BigDecimal(100)));
//			map.put("sumb", Unaccounted.add(Unaccounted1).divide(new BigDecimal(100)));
//			//总价
////			msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk,1)),0)/100 sum from Sharingdetails where memberid="+
////			getLogin(uname).getUserid()+" and id <"+(System.currentTimeMillis()-MyParameter.ReturnOrdersTime));
////			map.put("suma", commodityService.exeSelectSql(msql).get(0).get("SUM"));
////			
////			msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk,1)) ,0)/100 sum from Sharingdetails where memberid="+
////					getLogin(uname).getUserid()+" and id>"+(System.currentTimeMillis()-MyParameter.ReturnOrdersTime));
////			map.put("sumb", commodityService.exeSelectSql(msql).get(0).get("SUM"));
//			
////			msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk,1)),0)/100 sum from Sharingdetails where memberid="+
////					getLogin(uname).getUserid()+" and state=1 and Sharingdetails.id>"+time);
////			map.put("sumc", commodityService.exeSelectSql(msql).get(0).get("SUM"));
////			
//			
//			
//			
//			
//			
//			
//			
//
//			
//			//已结算
//					//百分之2的分成金额
//							msql.setSql("select nvl(sum(payment),0) payment from orders where status in("+getOrdersStatusTrue()+") "
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ "and autosystem=1 and shippingtype=3 and "
//									+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time
//									+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//							BigDecimal AlreadySettled =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							or=lOrdersRule.get(1);
//							//实得金额
//							AlreadySettled=AlreadySettled.multiply(or.getClerk());
//							
//							//百分之10分成
//							msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") "
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ "and autosystem=1 and shippingtype!=3 and "
//									+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and id>"+time
//									+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//							BigDecimal AlreadySettled2 =new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//							or=lOrdersRule.get(0);
//							
//							
//							//单品减9，
//							msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//									+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")"
//									+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//									+ "union all "
//									+ "select ordersid from Sharingdetails2 where state=1 )"
//											+ "and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//									+" and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")");
//							
//							AlreadySettled2=AlreadySettled2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							//导购拼单+4
//							msql.setSql("select nvl((sum(a)-count(*))*400,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//									+ "left join orders on orders.id=orderid "
//									+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//									+ " and memberidsu not in(select memberid from Identity where shopid="+getShop(uname).getId()+" and memberid != "+getLogin(uname).getUserid()+")"
//											+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//											+ "union all "
//											+ "select ordersid from Sharingdetails2 where state=1 )"
//									+ "and "
//									+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
//									+" group by Commoditykeyid,orderid)");
//							
//							//分成金额
//							AlreadySettled2=AlreadySettled2.multiply(or.getClerk());
//							//实得金额
//							AlreadySettled2=AlreadySettled2.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//							
//							
//							map.put("sumc", AlreadySettled.add(AlreadySettled2).divide(new BigDecimal(100)));
//					
//			
//			
//		}
//		
//		msql.setPage(page);
//		msql.setRows(rows);
//		msql.setOrderbykey("orders.id");
//		msql.setOrderbytype(1);
////	List<Map<String, Object>> listmap =commodityService.exeSelectSql(msql);
//
//	//最终金额
////	for (Map<String, Object> map2 : listmap) {
////		map2.put("Final",new BigDecimal(map2.get("CLERK").toString()).
////				subtract(new BigDecimal(map2.get("POSTFEE").toString())));
////		map2.put("phone1",getMember(Long.valueOf(map2.get("MEMBERID1").toString())).getUname());
////		
////	}
//	
//	map.put("data",new ArrayList<>());
////	map.put("data",listmap);
//	msql = new Sql();
//	msql.setSql("select * from (select * from Programme order by id desc) where rownum=1");
//	//结算规则
//	Programme mProgramme = IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0), Programme.class) ;
//	
//	if(getMember(uname).getmShop()!=null){
//		//门店店主
//		
//		try {
//			//经销商贡献未入账
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal1=	mBigDecimal1
//					.multiply(mProgramme.getShop())
//					.divide(new BigDecimal(100));
//			
//			
//		
//			
//			
//			
//			
//			//线上店主贡献未入账，1：自己直接推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid !="+getLogin(uname).getUserid()+")"
//					);
//			
//			
//			
//			BigDecimal mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid !="+getLogin(uname).getUserid()+")"
//					);
//			
//			mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal2=	mBigDecimal2
//					.multiply(mProgramme.getShop().add(mProgramme.getSales()))
//					.divide(new BigDecimal(100));
//			
//			
//			
//			//线上店主贡献未入账，2：经销商推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders "
//					+ "where status in("+getOrdersStatusTrue()+") and  autosystem=0 and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid !="+getLogin(uname).getUserid()+")"
//							);
//			
//			BigDecimal mBigDecimal3 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and  autosystem=0 and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+") and memberid !="+getLogin(uname).getUserid()+")"
//							);
//			
//			mBigDecimal3=mBigDecimal3.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal3=	mBigDecimal3
//					.multiply(mProgramme.getSales())
//					.divide(new BigDecimal(100));
//			
//			
//			
//			map.put("sum1b",mBigDecimal1);
//			map.put("sum2b",mBigDecimal2.add(mBigDecimal3));
//		} catch (Exception e) {
//			map.put("sum1b",0);
//			map.put("sum2b",0);
//		}
////		//经销售已入账
////		msql.setSql("select (nvl(sum(sales),0)/100) sales from Sharingdetails2 where   salesmemberid="+getLogin(uname).getUserid()
////				+" and shopid="+getMember(uname).getmShop().getId());
////		map.put("sum1a",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////		//经销售已结算
////		msql.setSql("select (nvl(sum(sales),0)/100) sales from Sharingdetails2 where state=1 and salesmemberid="+getLogin(uname).getUserid()
////				+" and shopid="+getMember(uname).getmShop().getId());
////		map.put("sum1c",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////		//线上店主已入账
////		msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where   onlineshopkeepermemberid="+getLogin(uname).getUserid()
////				+" and shopid="+getMember(uname).getmShop().getId());
////		map.put("sum2a",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////		//线上店主已结算
////		msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where state=1 and onlineshopkeepermemberid="+getLogin(uname).getUserid()
////				+" and shopid="+getMember(uname).getmShop().getId());
////		map.put("sum2c",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
//		
//		
//		
//		
//
//
//		
//		//已入账
//		
//		try {
//			//经销商贡献未入账
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal1=	mBigDecimal1
//					.multiply(mProgramme.getShop())
//					.divide(new BigDecimal(100));
//			
//			
//		
//			
//			
//			
//			
//			//线上店主贡献未入账，1：自己直接推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//					);
//			
//			
//			
//			BigDecimal mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//					);
//			
//			mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal2=	mBigDecimal2
//					.multiply(mProgramme.getShop().add(mProgramme.getSales()))
//					.divide(new BigDecimal(100));
//			
//			
//			
//			//线上店主贡献未入账，2：经销商推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders "
//					+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1 and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//			
//			BigDecimal mBigDecimal3 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1 and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//			
//			mBigDecimal3=mBigDecimal3.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal3=	mBigDecimal3
//					.multiply(mProgramme.getSales())
//					.divide(new BigDecimal(100));
//			
//			map.put("sum1a",mBigDecimal1);
//			map.put("sum2a",mBigDecimal2.add(mBigDecimal3));
//		} catch (Exception e) {
//			map.put("sum1a",0);
//			map.put("sum2a",0);
//		}
//
//		
//		
//		
//		
//		
//		
//		
//		//已结算
//		
//		
//		try {
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and autosystem=1 and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			BigDecimal mBigDecimal1 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and autosystem=1  and "
//					+ " memberidsu in( select memberid from Identity where type=1 and shopid="+getMember(uname).getmShop().getId()+")");
//			
//			mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal1=	mBigDecimal1
//					.multiply(mProgramme.getShop())
//					.divide(new BigDecimal(100));
//			
//			
//		
//			
//			
//			
//			
//			//线上店主贡献未入账，1：自己直接推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and autosystem=1 and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//					);
//			
//			
//			
//			BigDecimal mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and "
//					+ " memberidsu in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid =(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//					);
//			
//			mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal2=	mBigDecimal2
//					.multiply(mProgramme.getShop().add(mProgramme.getSales()))
//					.divide(new BigDecimal(100));
//			
//			
//			
//			//线上店主贡献未入账，2：经销商推荐
//			msql.setSql("select nvl(sum(payment),0) payment  from orders "
//					+ "where status in("+getOrdersStatusTrue()+") and  autosystem=1 "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//			
//			BigDecimal mBigDecimal3 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//			
//			//单品减9，
//			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//					+ "where status in("+getOrdersStatusTrue()+") "
//					+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//					+ "union all "
//					+ "select ordersid from Sharingdetails2 where state=1 )"
//					+ "and  autosystem=1 and "
//					+ " memberidsu  in( select memberid from Identity where type=2 and shopid="+getMember(uname).getmShop().getId()
//					+" and suid !=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//			
//			mBigDecimal3=mBigDecimal3.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//			mBigDecimal3=	mBigDecimal3
//					.multiply(mProgramme.getSales())
//					.divide(new BigDecimal(100));
//			
//			map.put("sum1c",mBigDecimal1);
//			map.put("sum2c",mBigDecimal2.add(mBigDecimal3));
//		} catch (Exception e) {
//			map.put("sum1c",0);
//			map.put("sum2c",0);
//		}
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//	}else{
//		msql.setSql("select type from Identity where memberid ="+getLogin(uname).getUserid()+" group by type ");
//		List<Map<String, Object>> li = mMemberService.exeSelectSql(msql);
//		if(li.size()==0){
//			map.put("sum1b",0);
//			map.put("sum2b",0);
//		}
//		List<Identity> mIdentity =IBeanUtil.ListMap2ListJavaBean(li, Identity.class);
//		BigDecimal mBigDecimal1 = new BigDecimal(0);//经销商贡献
//		BigDecimal mBigDecimal2 = new BigDecimal(0);//线上店主贡献
//		for (Identity identity : mIdentity) {
//			if(identity.getType()==1){
//				
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  "
//							
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal1 =	mBigDecimal1
//					.multiply(mProgramme.getSales().add(mProgramme.getOnlineshopkeeper()))
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal1=mBigDecimal1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					
//					
//					//1：线上店主推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//							+ " memberidsu in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//					
//					
//					mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//					
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
//							+ " orders.memberidsu in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+")) and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					mBigDecimal2=mBigDecimal2
//							.multiply(mProgramme.getSales())
//							.divide(new BigDecimal(100));
//					
//					map.put("sum1b",mBigDecimal1);
//					map.put("sum2b",mBigDecimal2);
//				} catch (Exception e) {
//					map.put("sum1b",0);
//					map.put("sum2b",0);
//				}
//				
//				
//				
//				//经销售已入账
////				msql.setSql("select (nvl(sum(my_to_so(1,salesmemberid,onlineshopkeepermemberid,sales,onlineshopkeeper)),0)/100) sales from Sharingdetails2 where "
////						+ " salesmemberid ="+getMember(uname).getId());
////				map.put("sum1a",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////				//经销售已结算
////				msql.setSql("select (nvl(sum(my_to_so(1,salesmemberid,onlineshopkeepermemberid,sales,onlineshopkeeper)),0)/100) sales from Sharingdetails2 where state=1 "
////						+" and salesmemberid ="+getMember(uname).getId());
////				map.put("sum1c",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////				//线上店主已入账
////				msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where  "
////						+ " salesmemberid !="+getMember(uname).getId()+" and onlineshopkeepermemberid="+getMember(uname).getId());
////				map.put("sum2a",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////				//线上店主已结算
////				msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where state=1  "
////						+ " and salesmemberid !="+getMember(uname).getId()+" and onlineshopkeepermemberid="+getMember(uname).getId());
////				map.put("sum2c",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
//				
//				
//				
//				
//				//已入账
//				
//				
//				
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//							
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal1 =	mBigDecimal1
//					.multiply(mProgramme.getSales().add(mProgramme.getOnlineshopkeeper()))
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal1=mBigDecimal1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					
//					
//					//1：线上店主推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 and "
//							+ " memberidsu in(select memberid from Identity where suid=(select id from   Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//					
//
//					mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//					
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  and "
//							+ " orders.memberidsu in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+")) and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					mBigDecimal2=mBigDecimal2
//							.multiply(mProgramme.getSales())
//							.divide(new BigDecimal(100));
//					
//					
//					
//					map.put("sum1a",mBigDecimal1);
//					map.put("sum2a",mBigDecimal2);
//				} catch (Exception e) {
//					map.put("sum1a",0);
//					map.put("sum2a",0);
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				//已结算
//				
//				
//				
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal1 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and autosystem=1  and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal1=mBigDecimal1.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal1 =	mBigDecimal1
//					.multiply(mProgramme.getSales().add(mProgramme.getOnlineshopkeeper()))
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal1=mBigDecimal1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					
//					
//					//1：线上店主推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and autosystem=1 and "
//							+ " memberidsu in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+"))"
//							);
//					
//
//					mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//					
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu in(select memberid from Identity where suid=(select id from Identity where memberid="+getLogin(uname).getUserid()+")) and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					mBigDecimal2=mBigDecimal2
//							.multiply(mProgramme.getSales())
//							.divide(new BigDecimal(100));
//					
//					
////					mBigDecimal2 = new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString())
////							.multiply(mProgramme.getSales())
////							.divide(new BigDecimal(100));
//					
//					
//					map.put("sum1c",mBigDecimal1);
//					map.put("sum2c",mBigDecimal2);
//					
//				} catch (Exception e) {
//					map.put("sum1c",0);
//					map.put("sum2c",0);
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//			}else{
//				
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal2 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=0 and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=0  "
//							
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal2 =	mBigDecimal2
//					.multiply(mProgramme.getOnlineshopkeeper())
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal2=mBigDecimal2.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					map.put("sum1b",mBigDecimal1);
//					map.put("sum2b",mBigDecimal2);
//				} catch (Exception e) {
//					map.put("sum1b",0);
//					map.put("sum2b",0);
//				}
//				
//				
//				
//				
//				
////				map.put("sum1a",0);
////				map.put("sum2a",0);
//				
////				//线上店主已入账
////				msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where  "
////						+ " salesmemberid !="+getMember(uname).getId()+" and onlineshopkeepermemberid="+getMember(uname).getId());
////				map.put("sum2a",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
////				//线上店主已结算
////				msql.setSql("select (nvl(sum(onlineshopkeeper),0)/100) sales from Sharingdetails2 where state=1 and "
////						+ " salesmemberid !="+getMember(uname).getId()+" and onlineshopkeepermemberid="+getMember(uname).getId());
////				map.put("sum2c",mMemberService.exeSelectSql(msql).get(0).get("SALES"));
//				
//				
//				//已入账
//				
//				
//
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal2 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1 "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
//							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
//							+ "union all "
//							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal2 =	mBigDecimal2
//					.multiply(mProgramme.getOnlineshopkeeper())
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal2=mBigDecimal2.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					map.put("sum1c",mBigDecimal1);
//					map.put("sum2c",mBigDecimal2);
//				} catch (Exception e) {
//					map.put("sum1c",0);
//					map.put("sum2c",0);
//				}
//				
//				
//				
//				//已结算
//				
//				
//				
//				
//
//				try {
//					//1：自己直接推荐
//					msql.setSql("select nvl(sum(payment),0) payment  from orders where status in("+getOrdersStatusTrue()+") and autosystem=1 "
////							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
////							+ "union all "
////							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " memberidsu ="+getLogin(uname).getUserid()
//							);
//					
//					mBigDecimal2 =	new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
//
//					//单品减9，
//					msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1 "
////							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
////							+ "union all "
////							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+" and Orderrelevance.id>"+time
//							);
//					mBigDecimal2=mBigDecimal2.subtract(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					//导购拼单+4
//					msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//							+ "left join orders on orders.id=orderid "
//							+ "where status in("+getOrdersStatusTrue()+") and autosystem=1  "
////							+" and orders.id  in(select ordersid from Sharingdetails where state=1 "
////							+ "union all "
////							+ "select ordersid from Sharingdetails2 where state=1 )"
//							+ "and "
//							+ " orders.memberidsu ="+getLogin(uname).getUserid()+"  and Orderrelevance.id>"+time
//							+" group by Commoditykeyid,orderid)");
//					
//					
//					
//					mBigDecimal2 =	mBigDecimal2
//					.multiply(mProgramme.getOnlineshopkeeper())
//					.divide(new BigDecimal(100));
//					
//					//实得金额
//					mBigDecimal2=mBigDecimal2.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//					
//					map.put("sum1a",mBigDecimal1);
//					map.put("sum2a",mBigDecimal2);
//				} catch (Exception e) {
//					map.put("sum1a",0);
//					map.put("sum2a",0);
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//				
//			}
//		}
//	
//		
//		
//		
//	}
//	
//	
//	if(map.get("sum1a")==null)
//		map.put("sum1a",0);
//	if(map.get("sum1c")==null)
//		map.put("sum1c",0);
//	if(map.get("sum2a")==null)
//		map.put("sum2a",0);
//	if(map.get("sum2c")==null)
//		map.put("sum2c",0);
//	
//	try {
//		msql.setSql("select type from Identity where memberid="+getLogin(uname).getUserid());
//		map.put("type",mMemberService.exeSelectSql(msql).get(0).get("TYPE").toString());
//	} catch (Exception e) {
//		if(getMember(uname).getmShop()==null)
//			map.put("type",3);
//		else
//			map.put("type",0);
//	}
//
//	if(map.get("type").toString().equals("0")){
//		map.put("a",new BigDecimal(map.get("suma").toString()).add(new BigDecimal(map.get("sum1a").toString())).add(new BigDecimal(map.get("sum2a").toString())));
//		map.put("b",new BigDecimal(map.get("sumb").toString()).add(new BigDecimal(map.get("sum1b").toString())).add(new BigDecimal(map.get("sum2b").toString())));
//		map.put("c",new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(map.get("sum1c").toString())).add(new BigDecimal(map.get("sum2c").toString())));
//		
//	}else if(map.get("type").toString().equals("1")){
//		map.put("a",new BigDecimal(map.get("sum1a").toString()).add(new BigDecimal(map.get("sum2a").toString())));
//		map.put("b",new BigDecimal(map.get("sum1b").toString()).add(new BigDecimal(map.get("sum2b").toString())));
//		map.put("c",new BigDecimal(map.get("sum1c").toString()).add(new BigDecimal(map.get("sum2c").toString())));
//	}else if(map.get("type").toString().equals("2")){
//		map.put("a",map.get("sum2a").toString());
//		map.put("b",map.get("sum2b").toString());
//		map.put("c",map.get("sum2c").toString());
//	}else{
//		map.put("a",map.get("suma").toString());
//		map.put("b",map.get("sumb").toString());
//		map.put("c",map.get("sumc").toString());
//	}
//	
//	
//	
//	
//	
//	
//	
//	return sendTrueData(map);
//		
//		
//	}
	
	/**
	 * 未入账明细
	 * */
	@Auth
	@RequestMapping(value ="/Recommend2/list", method = RequestMethod.POST) 
	public RequestType Recommend2notlist(String uname,Integer type,Integer rows,Integer page,String phone,Long id) throws Exception{
		if(type==null)return sendFalse("类型不可为空");
		if(!Stringutil.isBlank(phone)){
			if(getMember(uname).getSuperadmin()==1)
				uname=phone;
		}
		Sql msql = new Sql();
		//结算规则
		  List<OrdersRule> lOrdersRule;//结算规则
		  msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=1 order by id desc)"
		   				+ "where rownum=1"
		   				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id =  (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=2 order by id desc)"
		   				+ "where rownum=1");
		   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(commodityService.exeSelectSql(msql),OrdersRule.class);
		   if(lOrdersRule.size()!=2)return sendFalse("您的供应商还没有跟系统协商规则");
		msql.setPage(page);
		rows=rows==null?10:rows;
		msql.setRows(rows);
		OrdersRule or ;
		Long time=1538323200000L;//2018年10月1日0点0分0秒
		Shop mshop=getMember(uname).getmShop();
		
		if(type==1){
		//未入账金额	   
		msql.setSql("select decode(shippingtype,1,'自提',2,'送货',3,'门店直销')shippingtype, payment,shopid,shopname,"
				+ MyDate.orcaleCDATE("id")+",id"
				+ " from orders where status in("+getOrdersStatusTrue()+") and autosystem=0  and "
				+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time);
		}else{
			//已入账金额	   
			msql.setSql("select decode(shippingtype,1,'自提',2,'送货',3,'门店直销')shippingtype, payment,shopid,shopname,"
					+ MyDate.orcaleCDATE("id")+",id"
					+ " from orders where id  in(select ordersid from Sharingdetails)  and "
					+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time);
		}
		if(id!=null)msql.setSql(msql.getSql()+" and id="+id);
		List<Map<String, Object>> listmap = commodityService.exeSelectSql(msql) ;
		
		

		BigDecimal b;
		BigDecimal b1;
		
		for (Map<String, Object> map : listmap) {
			b1=new BigDecimal(map.get("PAYMENT").toString());
			//单品减9，
			msql.setSql("select nvl(sum(num),0) count  from Orderrelevance where orderid="+map.get("ID"));
			//商品数量
			map.put("NUM", commodityService.exeSelectSql(msql).get(0).get("COUNT"));
			if(map.get("SHIPPINGTYPE").equals("门店直销")){
				or=lOrdersRule.get(1);
			}else{
				or=lOrdersRule.get(0);
				b1=b1.subtract (new BigDecimal(map.get("NUM").toString()).multiply(new BigDecimal(9)));
			}
		
			//分成
			if(mshop!=null&&mshop.getId().toString().equals(map.get("SHOPID").toString()))
				b=b1.multiply(or.getClerk().add(or.getShop()).divide(new BigDecimal(100)));//店铺导购双重身份
			else
				b=b1.multiply(or.getClerk().divide(new BigDecimal(100)));
			
			if(map.get("SHIPPINGTYPE").equals("门店直销")){
				map.put("MONEY",b);
			}else{
				//导购拼单+4
				msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
						+ "left join orders on orders.id=orderid "
						+ "where orders.id="+map.get("ID")+" "
								+ " group by Commoditykeyid,orderid"
								+ ")");
				
				
				
				
//				//分成金额
//				b1=b1.multiply(or.getClerk());
//				//实得金额
//				b1=b1.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				
//				
//				//导购拼单+4
//				msql.setSql("select nvl((sum(num)-count(*))*4,0) count  from Orderrelevance where orderid="+map.get("ID"));
				//实际获得
				map.put("MONEY",b.add(new BigDecimal(commodityService.exeSelectSql(msql).get(0).get("COUNT").toString())));
			}
			
		
		}
	
		
	
		
		
		return sendTrueData(listmap);
	}
	/**
	 * 我推荐的人
	 * */
	@Auth
	@RequestMapping(value ="/Friends", method = RequestMethod.POST) 
	public RequestType Friends(String uname,String iPhone,Integer rows,Integer page) throws Exception{
		if(!Stringutil.isBlank(iPhone))
			uname=iPhone;
		Sql msql = new Sql();
		msql.setSql("select "+MyDate.orcaleCDATE("a.id")+",a.* ,nvl(bz,uname) uname2,phone from (select Friends.*,uname uname1 from Friends left join member on memberida=member.id )a left join member on member.id = a.memberidb where uname1 = '"+
				uname
				+"'");
		msql.setOrderbykey("a.id");
		msql.setOrderbytype(1);
		msql.setRows(rows);
		msql.setPage(page);
		return sendTrueData(commodityService.exeSelectSql(msql));
		
	}
	/**
	 * 修改备注
	 * */
	@Auth
	@RequestMapping(value ="/FriendsBz", method = RequestMethod.POST) 
	public RequestType FriendsBz(Long id,String bz) throws Exception{
		if(id==null)
			return sendFalse("编号不可为空");
		if(Stringutil.isBlank(bz))
			return sendFalse("备注不可为空");
		
		Sql msql = new Sql();
		msql.setSql("update Friends set bz='"+bz+"' where id="+id);
		commodityService.execSQL(msql, 1, id+"");
		return sendTrueMsg("修改成功");
		
	}
	/**
	 * 我要合作页面
	 * */
	@RequestMapping(value ="/MyItCooperation", method = RequestMethod.POST) 
	@Auth
	public  RequestType MyItCooperation(String uname) throws Exception{
		Integer type1;//经销商1：未购买，2：已购买未激活，3：已激活
		Integer type2;//线上店主1：未购买，2：已购买未激活，3：已激活
		Object obj1 = null;
		Object obj2 = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Advertisement mAdvertisement= new Advertisement();
		mAdvertisement.setKey("11");//经销商
		map.put("image1", mAdvertisementService.getALL(mAdvertisement));//经销商图片
		map.put("Distributor", readFileByChars(MyParameter.Distributor));//经销商协议
		mAdvertisement.setKey("12");//线上店主
		map.put("image2", mAdvertisementService.getALL(mAdvertisement));//线上店主
		map.put("shopkeeper", readFileByChars(MyParameter.shopkeeper));//线上店主
		//经销商
		Sql msql = new Sql();
		msql.setSql("select * from Identity where type=1 and memberid="+getLogin(uname).getUserid()+" and end>"+System.currentTimeMillis());
		if(mMemberService.exeSelectSql(msql).size()>0)
			type1=3;
		else{
			msql.setSql("select * from zsh where statis=1 and istrue=0 and type=1 and memberid="+getLogin(uname).getUserid());
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			if(listmap.size()>0){
				type1=2;
			obj1=listmap.get(0).get("ID");
			}else
				type1=1;
		}
		//线上店主
		msql.setSql("select * from Identity where type=2 and memberid="+getLogin(uname).getUserid()+" and end>"+System.currentTimeMillis());
		if(mMemberService.exeSelectSql(msql).size()>0)
			type2=3;
		else{
			msql.setSql("select * from zsh where statis=1 and istrue=0 and type=2 and memberid="+getLogin(uname).getUserid());
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			if(listmap.size()>0){
				type2=2;
			obj2=listmap.get(0).get("ID");
			}else
				type2=1;
		}

		map.put("type1", type1);
		map.put("type2", type2);
		map.put("obj1", obj1);
		map.put("obj2", obj2);
		
		return sendTrueData(map);
	}
	
	/**
	 *查询协议
	 * */
	@RequestMapping(value ="/Agreement/select", method = RequestMethod.POST) 
	public RequestType Agreementselect(String key) throws Exception{
			if("1".equals(key))
		return sendTrueData(readFileByChars(MyParameter.System1));//注册协议
			return sendTrueData(readFileByChars(MyParameter.System2));//隐私协议
		
	}
	
	
	
	
	
	/** 

	    * 以字符为单位读取文件，常用于读文本，数字等类型的文件 

	    */  
	
	@SuppressWarnings("resource")
	public static String readFileByChars(String fileName) {
		int a =0;
		   try {
			   File filename = new File(fileName); // 要读取以上路径的input文件  
	           InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"GBK"); // 建立一个输入流对象reader  
	           BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
	          StringBuilder sb = new StringBuilder();
	          String s;
	           while (true) { 
	        	   s=br.readLine();
	        	   if(Stringutil.isBlank(s)){
	        		   a=a+1;
	        		   if(a==2)
	        			   break;
	        	   }else{
	        		   a=0;
	        	   }
	        		  
	        	   sb.append(s);
	        	   sb.append("\n");
	         	  }
	           return sb.toString();
		   } catch (Exception e) {
			   return null;
		}
		
	      
	   }  
}
