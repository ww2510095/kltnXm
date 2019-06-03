package com.bm.stock;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.BaseService;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.commodity.Commodity;
import com.bm.commodity.CommodityService;
import com.bm.promotion.Promotion;
import com.bm.promotion.PromotionController;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;



@RestController
@Api(tags = "库存相关")
public class StockController extends BaseController{
	@Autowired
	private StockService mStockService;
	@Autowired
	private CommodityService mCommodityService;
	
	
	/**
	 * 检查库存是否充足
	 * @throws Exception 
	 * */
	public static void autostock(BaseService bs,Long itemid,int num,String name) throws  Exception{
		Sql msql = new Sql();
		msql.setSql("select nvl(num,0) num from stock where code=(select youcode from Commodity where id ="+itemid+")");
		if(Integer.parseInt(bs.exeSelectSql(msql).get(0).get("NUM").toString())<num)
			throw new RunException(name+"库存不足");
	}
	
	/**
	 * 导入最新库存
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/stock/readStockExcel", method = RequestMethod.POST)
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			Long ida = System.currentTimeMillis();
			int siza1=0;
			Sql msql = new Sql();
			for (List<String> list : lls) {
				siza1++;
				if(Stringutil.isBlank(list.get(0)) ) continue;
				Stock sk  =  new Stock();
				sk.setCode(list.get(0).trim());
				msql.setSql("select name from shop left join member on memberid=member.id left join Commodity on uname=supplier where name is not null and shop.code='"+list.get(2).trim()+"' and youcode='"+list.get(0).trim()+"'");
				if(mStockService.exeSelectSql(msql).size()==0)
					throw new RunException("第"+(siza1+3)+"行可能存在错误，你填写的店铺编码与商品所属供应商不一致，请检查");
				
				@SuppressWarnings("unchecked")
				List<Stock> lsk = (List<Stock>) mStockService.getALL(sk);
				sk.setShopcode(list.get(2).trim());
				if(lsk.size()==0){
					sk.setNum(Integer.parseInt(list.get(1)));
					sk.setType(1);
					sk.setId(ida);
					ida++;
					mStockService.add(sk);
				}else{
					if(!list.get(2).trim().equals(lsk.get(0).getShopcode()))
						throw new RunException("第"+(siza1+3)+"行可能存在错误，该商品已经有库存了，但是你导入的店铺编号却和原本的不一样");
					sk.setNum(Integer.parseInt(list.get(1)));
					sk.setId(lsk.get(0).getId());
					mStockService.updateBySelect(sk);
				}
			}
			
			return sendTrueMsg("导入成功，此次一共导入" + lls.size() + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	/**
	 * 修改单个库存
	 * */
	@Auth(stock={Administration.UPDATE})
	@RequestMapping(value = "/stock/update", method = RequestMethod.POST)
	public RequestType update(HttpServletRequest req, Stock mStock) throws Exception {
		mStockService.updateBySelect(mStock);
		return sendTrueMsg("更新成功");
	}
	/**
	 * 增加单个库存
	 * */
	@Auth(stock={Administration.ADD})
	@RequestMapping(value = "/stock/add", method = RequestMethod.POST)
	public RequestType add(HttpServletRequest req, Stock mStock) throws Exception {
		if(Stringutil.isBlank(mStock.getShopcode()))return sendFalse("店铺编号不可为空");
		if(Stringutil.isBlank(mStock.getCode()))return sendFalse("条码不可为空");
		if(mStock.getNum()==null)return sendFalse("库存数不可为空");
		mStock.setType(1);
		mStockService.add(mStock);
		return sendTrueMsg("操作成功");
	}
	/**
	 * 下架商品
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/stock/Lowerframe", method = RequestMethod.POST)
	public RequestType Lowerframe(HttpServletRequest req, String youcode,String uname) throws Exception {
		if(Stringutil.isBlank(youcode))
			return sendFalse("最少选择一条数据");
		List<String> li = GsonUtil.fromJsonList(youcode, String.class);
		if(li.size()==0)
			li.add(youcode);
		List<Stock> lc = new ArrayList<>();
		for (String string : li) {
			Stock mmStock= new Stock();
			mmStock.setCode(string);
			lc.add(mmStock);
		}
		
		
		updateStock1(lc, -1, uname);
		return sendTrueMsg("更新成功");
	}
	/**
	 * 冻结商品
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/stock/Frozen", method = RequestMethod.POST)
	public RequestType Frozen(HttpServletRequest req, Stock mStock,String uname) throws Exception {
		List<Stock> lc = new ArrayList<>();
		lc.add(mStock);
		updateStock1(lc, -2, uname);
		return sendTrueMsg("更新成功");
	}
	private  synchronized void updateStock1(List<Stock> sk,int type,String uname) throws Exception{
		if(sk.size()==0)return;
		for (Stock stock : sk) {
			if(stock.getId()==null)
				if(!Stringutil.isBlank(stock.getCode())){
					stock = mStockService.getByparameter("code", stock.getCode(), Stock.class);
					if(stock==null)
						throw new RunException("条码不存在");
				}
			stock.setType(type);
			if(getMember(getLogin(uname)).getSuperadmin()!=1){
				try {
					stock.setShopcode(getMember(getLogin(uname)).getmShop().getCode());
				} catch (Exception e) {
					throw new RunException("抱歉，权限不足");
				}
			}
			mStockService.updateBySelect(stock);
			
		}
	}
	/**
	 * 上架商品
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/stock/normal", method = RequestMethod.POST)
	public RequestType normal(HttpServletRequest req, String youcode,String uname) throws Exception {
		if(Stringutil.isBlank(youcode))
			return sendFalse("最少选择一条数据");
		List<String> li = GsonUtil.fromJsonList(youcode, String.class);
		if(li.size()==0)
			li.add(youcode);
		List<Stock> lc = new ArrayList<>();
		for (String string : li) {
			Stock mmStock= new Stock();
			mmStock.setCode(string);
			lc.add(mmStock);
		}
		updateStock1(lc, 1, uname);
		return sendTrueMsg("更新成功");
	}
	
	/**
	 * 根据商品特性冻结，恢复，下架商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/stock/updatebycommodity", method = RequestMethod.POST) 
	public RequestType updatebycommodity(Commodity mcommodity,int newtype) throws Exception{
		String where = mCommodityService.getWhere(mcommodity);
		Sql msql =new Sql();
		msql.setSql("update Stock set type="+newtype+" where code in( select youcode from commodity " +where +")");
		mCommodityService.execSQL(msql, -1,"");
		return sendTrueMsg("操作成功");
	}
	/**
	 * 修改单个店铺所有商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/stock/updatebyshopcode", method = RequestMethod.POST) 
	public RequestType updateStock(String shopcode,int type,String uname) throws Exception{
		Stock mStock =new Stock();
		mStock.setShopcode(shopcode);
		List<Stock> lc = new ArrayList<>();
		lc.add(mStock);
		updateStock1(lc, type, uname);
		return sendTrueMsg("操作成功");
	}

	/**
	 * 查询我的店铺的库存，如果是超级管理员，查询所有库存
	 * */
	@Auth
	@RequestMapping(value = "/stock/selectall", method = RequestMethod.POST)
	public RequestType selectall(Stock mStock,String uname,Integer page,Integer rows) throws Exception {
		Sql msql =new Sql();
		if(getMember(getLogin(uname).getUserid()).getSuperadmin()!=1)
			if(Stringutil.isBlank(mStock.getShopcode()))return sendFalse("错误，店铺编号不可为空");
		
			
		msql.setSql("select * from Stock " + mStockService.getWhere(mStock));
		msql.setRows(rows);
		msql.setPage(page);
		
		return sendTrueData(mStockService.exeSelectSql(msql));
	}
	/**
	 * 查询单个商品库存
	 * */
	@RequestMapping(value = "/stock/selectbycode", method = RequestMethod.POST)
	public RequestType selectbycode(String code) throws Exception {
		Sql msql =new Sql();
		msql.setSql("select num from Stock where code='"+code+"'");
		return sendTrueData(mStockService.exeSelectSql(msql));
	}
	/**
	 * 根据名字尺码颜色查库存
	 * */
	@RequestMapping(value = "/stock/selectbyparameter", method = RequestMethod.POST)
	public RequestType selectbyparameter(String keyid,String colour,String mysize) throws Exception {
		if(Stringutil.isBlank(keyid))return sendFalse("商品id不可为空");
		if(Stringutil.isBlank(colour))return sendFalse("颜色不可为空");
		if(Stringutil.isBlank(mysize))return sendFalse("尺码不可为空");
		Commodity mCommodity = new Commodity();
		mCommodity.setCommoditykeyid(keyid);
		mCommodity.setColour(colour);
		mCommodity.setMysize(mysize);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list  = (List<Map<String, Object>>) mCommodityService.getALL(mCommodity);
		if(list.size()!=1)
			return sendFalse("库存不足");
		
		Sql msql = new Sql();
		msql.setSql("select nvl(kc,0)num from Commodity where youcode='"+list.get(0).get("YOUCODE")+"'");
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("NUM", mCommodityService.exeSelectSql(msql).get(0).get("NUM"));
		map.put("price", list.get(0).get("PRICE"));
		map.put("id", list.get(0).get("ID"));
		map.put("code", list.get(0).get("YOUCODE"));
		map.put("num",map1);
		
		Promotion mPromotion = PromotionController.getPromotion(mMemberService,list.get(0).get("YOUCODE").toString());
		BigDecimal b1 = new BigDecimal("0");
		if(mPromotion!=null){
			
			if (mPromotion.getType() == 1)
				b1 = new BigDecimal(map.get("price").toString()).multiply(mPromotion.getDiscount().divide(new BigDecimal("10")));
			else if (mPromotion.getType() == 2)
				b1 = new BigDecimal(map.get("price").toString()).subtract(mPromotion.getReduce());
			

			map.put("price1", b1);
		}
		map.put("price2", new BigDecimal(list.get(0).get("PRICE").toString()).subtract(b1));
		map.put("mPromotion", mPromotion);
		return sendTrueData(map);
	}
	

}
