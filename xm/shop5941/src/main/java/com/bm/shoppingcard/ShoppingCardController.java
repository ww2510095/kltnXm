package com.bm.shoppingcard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.commodity.Commodity;
import com.bm.commodity.CommodityService;
import com.bm.promotion.Promotion;
import com.bm.promotion.PromotionController;
import com.bm.stock.StockController;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "购物车相关")
public class ShoppingCardController extends BaseController{
	@Autowired
	private ShoppingCardService mShoppingCardService;
	@Autowired
	private CommodityService mCommodityService;
   
	

	/**
	 * 添加购物车
	 * */
	@RequestMapping(value ="/card/add", method = RequestMethod.POST) 
	@Auth
	public RequestType add(Long id,String uname,int index,Integer num) throws Exception{
		Commodity mCommodity =IBeanUtil.Map2JavaBean(mCommodityService.getById(id), Commodity.class);
		if(mCommodity==null) return sendFalse("商品不存在");
		//检查库存是否充足
		StockController.autostock(mMemberService, id,1,mCommodity.getName());
		
		 ShoppingCard mShoppingCard = new ShoppingCard();
//		 if(num!=null)mShoppingCard.setNum(num);
		 mShoppingCard.setItemid(id);
		 mShoppingCard.setMemberid(getLogin(uname).getUserid());
		 @SuppressWarnings("unchecked")
		List<ShoppingCard> listmap  =  (List<ShoppingCard>) mShoppingCardService.getALL(mShoppingCard);
		 if(listmap.size()!=0){
			 mShoppingCard.setId(listmap.get(0).getId());
			 if(num!=null)
				 mShoppingCard.setNum(listmap.get(0).getNum()+num);
			 else
				 mShoppingCard.setNum(listmap.get(0).getNum()+1);
			 mShoppingCard.setTotalfee(mCommodity.getPrice().multiply(new BigDecimal(mShoppingCard.getNum()+"")));
			 mShoppingCardService.updateBySelect(mShoppingCard);
			 return sendTrueMsg("更新成功");
		 }
		 
		 mShoppingCard.setNum(num==null?1:num);
		 mShoppingCard.setTotalfee(mCommodity.getPrice().multiply(new BigDecimal(num==null?1:num)));
		 mShoppingCard.setKid(Long.valueOf(mCommodity.getCommoditykeyid()));
		 mShoppingCard.setTitle(mCommodity.getName());
		 mShoppingCard.setPrice(mCommodity.getPrice());
		 mShoppingCard.setPicpath(mCommodity.getMainimage().split(";")[index-1]);
		 mShoppingCard.setType1(mCommodity.getLargeclass());
		 mShoppingCard.setType2(mCommodity.getInclass());
		 mShoppingCard.setType3(mCommodity.getSmallclass());
		 mShoppingCard.setType4(mCommodity.getFineclass());
		 mShoppingCard.setOnephone(mCommodity.getSupplier());
		 mShoppingCard.setOneshopname(mCommodity.getSuppliername());
		 mShoppingCardService.add(mShoppingCard);
		
		return sendTrueMsg("添加成功");
		
	}
	
	private RequestType itemupdate(Long id,String uname,int type) throws Exception{
		ShoppingCard sc =IBeanUtil.Map2JavaBean(mShoppingCardService.getById(id), ShoppingCard.class);
		if(sc!=null){
			if(!sc.getMemberid().equals(getLogin(uname).getUserid()))throw new RunException("错误，该物品不属于你");
			if(type==1){
				//加法运算
				sc.setNum(sc.getNum()+1);
				sc.setTotalfee(sc.getTotalfee().add(sc.getPrice()));
			}else{
				//减法运算
				if(sc.getNum()-1!=0){
					sc.setNum(sc.getNum()-1);
					sc.setTotalfee(sc.getTotalfee().subtract(sc.getPrice()));
				}
			}
		
			mShoppingCardService.updateBySelect(sc);
			return sendTrueMsg("更新成功");
		}else{
			return sendFalse("商品不存在");
		}
		
	}
	/**
	 * 购物车商品数量+1
	 * */
	@RequestMapping(value ="/card/itemadd", method = RequestMethod.POST) 
	@Auth
	public RequestType itemadd(Long itemid,String uname) throws Exception{
		return itemupdate(itemid, uname, 1);
	}
	/**
	 * 购物车商品数量-1
	 * */
	@RequestMapping(value ="/card/itemasubtract", method = RequestMethod.POST) 
	@Auth
	public RequestType itemsubtract(Long itemid,String uname) throws Exception{
		return itemupdate(itemid, uname, 0);
	}
	/**
	 * 删除购物车中的一个物品
	 * */
	@RequestMapping(value ="/card/delete", method = RequestMethod.POST) 
	@Auth
	public RequestType delete(Long id) throws Exception{
		mShoppingCardService.deleteByid(id);
		return sendTrueMsg("删除成功");
	}
	/**
	 * 删除购物车中的一些物品
	 * */
	@RequestMapping(value ="/card/deletejson", method = RequestMethod.POST) 
	@Auth
	public RequestType delete(String JsonArray) throws Exception{
		List<Long> l = GsonUtil.fromJsonList(JsonArray, Long.class);
		for (Long long1 : l) {
			mShoppingCardService.deleteByid(long1);
		}
		
		return sendTrueMsg("删除成功,总共删除"+l.size()+"个");
	}
	/**
	 * 清空购物车
	 * */
	@RequestMapping(value ="/card/deleteall", method = RequestMethod.POST) 
	@Auth
	public RequestType deleteall(String uname) throws Exception{
		ShoppingCard sc =new ShoppingCard();
		sc.setMemberid(getLogin(uname).getUserid());
		mShoppingCardService.deleteBySelect(sc);
		return sendTrueMsg("清空成功");
	}
	/**
	 * ---------------------------------------------查询购物车star-------------------------------
	 * */
	@RequestMapping(value ="/card/select", method = RequestMethod.POST) 
	@Auth
	public RequestType select(String uname,Integer page,Integer rows) throws Exception{
		return sendTrueData(getMap(page, rows, uname));
	}
	/**
	 * 查询购物车（2.0版本）
	 * */
	@RequestMapping(value ="/card/select2", method = RequestMethod.POST) 
	@Auth
	public RequestType select2(String uname,Integer page,Integer rows) throws Exception{
		Map<String, Object> mMap = getMap(page, rows, uname);
		
		List<Card2> listCard2 = new ArrayList<ShoppingCardController.Card2>();
		Set<String> set = new HashSet<String>();
		
		//塞选供应商
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listmap = (List<Map<String, Object>>) mMap.get("data");
		for (Map<String, Object> map : listmap) {
			Object obj = map.get("ONEPHONE");
			if(obj==null||obj.toString().length()==0) {
				//有1.0版本的数据
				deleteall(uname);
				mMap.put("data", new ArrayList<>());
				return sendTrueData(mMap);
			}
			set.add(obj.toString());
		}
		
		for (String str : set) {
			Card2 ca = new Card2();
			ca.setOnephone(str);
			ca.setListmap(new ArrayList<Map<String,Object>>());
			listCard2.add(ca);
		}
		for (Map<String, Object> map : listmap) {
				String obj = map.get("ONEPHONE").toString();
				for (Card2 mCard2 : listCard2) {
					if(obj.equals(mCard2.getOnephone())){
						mCard2.setOnename(map.get("ONESHOPNAME").toString());
						mCard2.getListmap().add(map);
					}
				}
		}
		//2.0版本的数据
		mMap.put("data", listCard2);
		return sendTrueData(mMap);
		
	}
	
	public class Card2{
		private String onephone;
		private String onename;
		private boolean checked=false;
		
		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}
		List<Map<String, Object>> listmap;
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
		public List<Map<String, Object>> getListmap() {
			return listmap;
		}
		public void setListmap(List<Map<String, Object>> listmap) {
			this.listmap = listmap;
		}
		
		
	}
	
	//得到购物车的数据
	private Map<String, Object> getMap(Integer page,Integer rows,String uname) throws Exception {
		Sql msql =new Sql();
		Map<String, Object> map = new HashMap<>();
		msql.setSql("select * from ShoppingCard where memberid=" +getLogin(uname).getUserid());
		msql.setRows(rows);
		msql.setPage(page);
		List<Map<String, Object>> listmap =mShoppingCardService.exeSelectSql(msql);
		for (Map<String, Object> map2 : listmap) {
			Promotion mPromotion = PromotionController.getPromotion(mMemberService, map2.get("ITEMID"));

			ShoppingCard	mShoppingCard = IBeanUtil.Map2JavaBean(map2, ShoppingCard.class);
			
			if (mPromotion != null) {
				BigDecimal b1 = null;
				if (mPromotion.getType() == 1)
					b1=mShoppingCard.getPrice().multiply(mPromotion.getDiscount().divide(new BigDecimal("10")));
					
				else if (mPromotion.getType() == 2)
					b1 = mShoppingCard.getPrice().subtract( mPromotion.getReduce());
				
				map2.put("PRICE", b1);

			}

			map2.put("checked", false);
		}
		map.put("data", listmap);
		if(listmap.size()!=0){
			msql.setSql("select nvl(sum(totalfee),0) A from ShoppingCard where memberid='"+getLogin(uname).getUserid()+"'");
			map.put("A", mShoppingCardService.exeSelectSql(msql).get(0));
			for (Map<String, Object> object : listmap) {
				try {
					Map<String, Object> map1 = mCommodityService.getById(object.get("ITEMID"));
					object.put("colour",map1.get("COLOUR"));
					object.put("mysize",map1.get("MYSIZE"));
					object.put("specifications",map1.get("SPECIFICATIONS"));
				} catch (Exception e) {
				}
			
			}
		}else{
			map.put("A",0);
		}
		return map;

	}
	
	/**
	 * ---------------------------------------------查询购物车end-------------------------------
	 * */
	
	/**
	 * 刷新购物车
	 * */
	@RequestMapping(value ="/card/auto", method = RequestMethod.POST) 
	@Auth
	public RequestType auto(String uname) throws Exception{
		Sql msql =new Sql();
		msql.setSql("select * from ShoppingCard where memberid='"+getLogin(uname).getUserid()+"'");
		List<ShoppingCard> listsc = IBeanUtil.ListMap2ListJavaBean(mShoppingCardService.exeSelectSql(msql), ShoppingCard.class);
		if(listsc.size()!=0){
			for (ShoppingCard mShoppingCard : listsc) {
				Commodity mCommodity =IBeanUtil.Map2JavaBean(mCommodityService.getById(mShoppingCard.getItemid()), Commodity.class);
				mShoppingCard.setTotalfee(mCommodity.getPrice().multiply(new BigDecimal(mShoppingCard.getNum()+"")));
				 mShoppingCard.setTitle(mCommodity.getName());
				 mShoppingCard.setPrice(mCommodity.getPrice());
				 mShoppingCard.setType1(mCommodity.getLargeclass());
				 mShoppingCard.setType2(mCommodity.getInclass());
				 mShoppingCard.setType3(mCommodity.getSmallclass());
				 mShoppingCard.setType4(mCommodity.getFineclass());
				 mShoppingCardService.updateBySelect(mShoppingCard);
			}
		}
		return sendTrueData("刷新成功");
		
	}
	
	
	
	
}
