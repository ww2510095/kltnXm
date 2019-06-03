package com.bm.evaluate;

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
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.myjar.Stringutil;



@RestController
@Api(tags = "评价")
public class EvaluateController extends BaseController{
	@Autowired
	private EvaluateService mEvaluateService;
	@Autowired//订单
	private OrdersService mOrdersService;
	/**
	 * 添加评价
	 * */
	@Auth
	@Transactional
	@RequestMapping(value = "/evaluate/add", method = RequestMethod.POST)
	public RequestType readShopExcel(Evaluate mEvaluate,HttpServletRequest req,String uname) throws Exception {
		if(mEvaluate.getNum()==null)return sendFalse("评价星级不可为空");
		Map<String,Object> map = mOrdersService.getById(mEvaluate.getOrdersid());
		Long userid =(Long)map.get("MEMBERID");
		if(userid!=getLogin(uname).getUserid())return sendFalse("订单不属于你");
		String buyerrate =map.get("BUYERRATE").toString();
		if("1".equals(buyerrate))return sendFalse("订单已评价");
		
		if(!"4".equals(map.get("STATUS").toString()))return sendFalse("订单暂时不可评价");
		
		mEvaluateService.add(mEvaluate);
		Orders od = new Orders();
		od.setId(mEvaluate.getOrdersid());
		od.setBuyerrate("1");
		mOrdersService.updateBySelect(od);
		return sendTrueMsg("评价成功");
	}
	/**
	 * 追加评价语
	 * */
	@Auth
	@RequestMapping(value = "/evaluate/update", method = RequestMethod.POST)
	public RequestType update(Evaluate mEvaluate,HttpServletRequest req,String uname) throws Exception {
		String s =mEvaluate.getContent();
		if(Stringutil.isBlank(s))return sendFalse("评价语不可为空");
		
		mEvaluate=IBeanUtil.Map2JavaBean(mEvaluateService.getById(mEvaluate), Evaluate.class);
		if(mEvaluate==null) return sendFalse("评价不存在");
		
		Map<String,Object> map = mOrdersService.getById(mEvaluate.getOrdersid());
		Long userid =(Long)map.get("MEMBERID");
		if(userid!=getLogin(uname).getUserid())return sendFalse("订单不属于你");
		mEvaluateService.updateBySelect(mEvaluate);
		return sendTrueMsg("评价成功");
	}
	/**
	 * 查询评价
	 * */
	@Auth
	@RequestMapping(value = "/evaluate/select", method = RequestMethod.POST)
	public RequestType select(Long orderid,HttpServletRequest req,String uname) throws Exception {
		Sql mSql = new Sql();
		mSql.setSql("select evaluate.*, member.phone, member.portrait, member.nickname,shop.memberid adminshopid "
				+ " from evaluate "
				+ " left join orders "
				+ "on orders.id = evaluate.ordersid "
				+ " left join member "
				+ " on member.id = orders.memberid "
				+ "  left join shop "
				+ " on shop.id = orders.shopid "
				+ " where ordersid = "+orderid);
		
		List<Map<String,Object>> listmap = mEvaluateService.exeSelectSql(mSql);
//		List<EvaluateSelect> listmap = GsonUtil.fromJsonList(mEvaluateService.exeSelectSql(mSql), EvaluateSelect.class);
		
		if(listmap.size()==0)return sendFalse("订单不存在或未评价");

		EvaluateSelect mEvaluateSelect = IBeanUtil.Map2JavaBean(listmap.get(0), EvaluateSelect.class);
		if(getMember(getLogin(uname).getUserid()).getSuperadmin()!=1)
			if(mEvaluateSelect.getMemberid()!=getLogin(uname).getUserid()&&mEvaluateSelect.getAdminshopid()!=getLogin(uname).getUserid())
				return sendFalse("抱歉，权限不足");
		
//		if(getMember(getLogin(uname).getUserid()).getSuperadmin()!=1)
//			if(listmap.get(0).getMemberid()!=getLogin(uname).getUserid()&&listmap.get(0).getAdminshopid()!=getLogin(uname).getUserid())
//				return sendFalse("抱歉，权限不足");
//		
		
		return sendTrueData(listmap.get(0));
	}
	

}
