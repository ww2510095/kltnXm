package com.bm.promotion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.stock.Stock;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "商城活动")
public class PromotionController extends BaseController {

	@Autowired
	private PromotionService mPromotionService;


	/**
	 * 添加活动
	 * @param code 商品条码
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/Promotion/add", method = RequestMethod.POST)
	@Transactional
	public RequestType Promotionadd(Promotion mPromotion, HttpServletRequest req,String code1) throws Exception {
		if(mPromotion.getEnd()==null)return sendFalse("结束时间不可为空");
		if(Stringutil.isBlank(code1))return sendFalse("商品条码不可为空");
		if(mPromotion.getStar()==null)mPromotion.setStar(System.currentTimeMillis());
		if(Stringutil.isBlank(mPromotion.getTitle()))return sendFalse("标题不可为空");
		if(Stringutil.isBlank(mPromotion.getIntroduce()))return sendFalse("活动介绍不可为空");
		if(mPromotion.getType()==null)return sendFalse("类型不可为空");
		if(mPromotion.getType()==1){
			if(mPromotion.getDiscount()==null)
				return sendFalse("错误，你选择的是打折，但打折数是空的");
			
			if(mPromotion.getDiscount().doubleValue()>9.99||mPromotion.getDiscount().doubleValue()<0.01)
				return sendFalse("错误，折扣应该在0.01-9.99之间");
		}else if(mPromotion.getType()==2){
			if(mPromotion.getReduce()==null)
				return sendFalse("错误，你选择的是降价，但降价多少是空的");
		}else if(mPromotion.getType()==3){
			if(mPromotion.getSubtraction1()==null)
				return sendFalse("错误，你选择的是满减，但满多少是空的");
			if(mPromotion.getSubtraction2()==null)
				return sendFalse("错误，你选择的是满减，但减多少是空的");
		}else return sendFalse("目前只支持1：打折，2：降价，3：满减");
		
		
		
		
		Sql msql = new Sql();
		List<String> lists = GsonUtil.fromJsonList(code1, String.class);
		if(lists.size()==0)
			lists.add(code1);
		Long id = System.currentTimeMillis();
		for (String iterable_element : lists) {
			msql.setSql("select * from Stock where code='"+iterable_element+"'");
			List<Map<String,Object>> listmap = mPromotionService.exeSelectSql(msql);
			if(listmap.size()==0)throw new RunException("错误，商品未上架");
			
//			mPromotion.setCommodityname(code);
			
			Stock mStock = IBeanUtil.Map2JavaBean(listmap.get(0), Stock.class);
			if(mStock==null)throw new RunException("错误，商品未上架");
			
			mPromotion.setShopcode(mStock.getShopcode());
			msql.setSql("select memberid from Stock left join shop on shop.code = Stock.shopcode where Stock.id="+mStock.getId());
			
			listmap = mPromotionService.exeSelectSql(msql);
			if(listmap.size()==0)throw new RunException("错误，商品未上架");
			
			mPromotion.setMemberid(Long.valueOf(listmap.get(0).get("MEMBERID").toString()));
			
			msql.setSql("select * from Promotion where commodityname = '"+iterable_element+"' and end > " +mPromotion.getStar());
			listmap = mPromotionService.exeSelectSql(msql);
			if(listmap.size()!=0)throw new RunException("错误，该商品已经有活动了");
			
//			msql.setSql("select youcode from SPECIFICATIONS where COMMODITYKEYID=(select COMMODITYKEYID from SPECIFICATIONS where youcode='"+iterable_element+"')");
//			listmap = mPromotionService.exeSelectSql(msql);
			if(mPromotion.getType()==2){
				msql.setSql("select * from Commodity where youcode='"+iterable_element+"'");
				listmap = mPromotionService.exeSelectSql(msql);
				if(mPromotion.getReduce().subtract(new BigDecimal(listmap.get(0).get("PRICE").toString())).doubleValue()>=0 )
					throw new RunException("错误，降价不能大于原价");
			}
			
			
			mPromotion.setCommodityname(iterable_element);
			mPromotion.setId(id);
			id=id+1L;
			mPromotionService.add(mPromotion);
		}
	
		
		
		return sendTrueMsg("添加成功");
	}
	/**
	 *查询活动信息
	 */
	@Auth(Promotion={ Administration.SELECT})
	@RequestMapping(value = "/Promotion/select", method = RequestMethod.POST)
	public RequestType Promotionselect(String uname) throws Exception {
		Sql msql = new Sql();
		
		if(getMember(getLogin(uname).getUserid()).getSuperadmin()==1)
			msql.setSql("select * from Promotion ");
		else 
			msql.setSql("select * from Promotion where memberid ="+getLogin(uname).getUserid());
		
		return sendTrueData(mPromotionService.exeSelectSql(msql));
	}
	
	/**
	 *查询活动信息
	 */
	@RequestMapping(value = "/Promotion/selectByid", method = RequestMethod.POST)
	public RequestType Promotionselect(Long id) throws Exception {
		return sendTrueData(mPromotionService.getById(id));
	}
	/**
	 *废弃单条活动
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/Promotion/DiscardedByid", method = RequestMethod.POST)
	public RequestType DiscardedByid(Long id) throws Exception {
		Promotion mPromotion = new Promotion();
		mPromotion.setId(id);
		mPromotion.setEnd(System.currentTimeMillis());
		mPromotionService.updateBySelect(mPromotion);
		return sendTrueMsg("废弃成功");
	}
	
	public static Promotion getPromotion(BaseService mbase,Object codeORid) throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from Promotion where id='"+codeORid+"'");
		List<Map<String, Object>> listmap = mbase.exeSelectSql(msql);
		if(listmap.size()==0){
			msql.setSql("select * from ( select * from Promotion where commodityname='" + codeORid
					+ "' and end >"+System.currentTimeMillis()+" order by id desc ) where rownum=1");
			 listmap = mbase.exeSelectSql(msql);

		}
		if(listmap.size()==0){
			msql.setSql("select * from ( select * from Promotion where commodityname=(select youcode from Commodity where id='"+codeORid+"') and end >"+System.currentTimeMillis()+" order by id desc ) where rownum=1");
			listmap = mbase.exeSelectSql(msql);
			
		}
		if (listmap.size() != 0)
			return IBeanUtil.Map2JavaBean(listmap.get(0), Promotion.class);

		return null;

	}


}
