package com.bm.orders.orderscard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.excle.ExportExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.MyDate;
import com.myjar.Stringutil;

@RestController
@Api(tags = "抽奖规则")
public class OrdersCardController extends BaseController {
	
	@Autowired 
	private OrdersCardService mOrdersCardService;
	@Autowired 
	private OrdersCardRuleService mOrdersCardRuleService;
	
	
	@Auth(admin=true)
	@RequestMapping(value ="/OrdersCardRule/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType OrdersCardRuleadd(OrdersCardRule mOrdersCardRule) throws Exception{
		if(Stringutil.isBlank(mOrdersCardRule.getCode()))return sendFalse("编号不可为空");
		if(Stringutil.isBlank(mOrdersCardRule.getMax()))return sendFalse("最大阈值不可为空");
		if(Stringutil.isBlank(mOrdersCardRule.getMin()))return sendFalse("最小阈值不可为空");
		if(Stringutil.isBlank(mOrdersCardRule.getNum()))return sendFalse("金额不可为空");
		if(mOrdersCardRule.getMax().doubleValue()<=mOrdersCardRule.getMin().doubleValue())return sendFalse("最大阈值必须大于最小阈值");
		Sql msql = new Sql();
		msql.setSql("select id from OrdersCardRule where Max>="+mOrdersCardRule.getMin()+" and code='"+mOrdersCardRule.getCode()+"'");
		if(mOrdersCardRuleService.exeSelectSql(msql).size()!=0)return sendFalse("该阈值已经存在，请检查，此功能不支持修改，如果填错了请从新添加编码，系统将自动执行最新的规则");
		mOrdersCardRuleService.add(mOrdersCardRule);
		return sendTrueMsg("添加成功");
	}

	@Auth(admin=true)
	@RequestMapping(value ="/OrdersCardRule/list", method = RequestMethod.POST) 
	public RequestType OrdersCardRulelist(OrdersCardRule mOrdersCardRule,String uname,Integer page,Integer rows) throws Exception{
		return sendTrueData(mOrdersCardRuleService.getALL(mOrdersCardRule,page,rows));
	}
	
	
	
	
	
	/**
	 * 实物卡导出
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/OrdersCard/Excel", method = RequestMethod.GET)
	public RequestType OrdersCardExcel( String code,HttpServletResponse response)
			throws Exception {
		Sql msql = new Sql();
		if(Stringutil.isBlank(code))return sendFalse("批量编号不可为空");
		msql.setSql("select * from OrdersCard where bcode='"+code+"'");
		List<Map<String, Object>> listmap =  mOrdersCardRuleService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			map.put("STAR", MyDate.stampToDate(Long.valueOf(map.get("STAR").toString())));
			map.put("END", MyDate.stampToDate(Long.valueOf(map.get("END").toString())));
			Object obj = map.get("MEMBERID");
			if(obj==null)
				map.put("MEMBERID", "暂无");
			else
				map.put("MEMBERID", getMember(Long.valueOf(map.get("MEMBERID").toString())).getUname());
			
		}
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("卡号");			key.add("code");
		title.add("密码");			key.add("pwd");
		title.add("说明");   			key.add("title");
		title.add("剩余额度");  		key.add("num");
		title.add("初始额度");			key.add("nummax");
		title.add("有效期开始时间");		key.add("star");
		title.add("有效期结束时间");		key.add("end");
		title.add("批量编号");			key.add("bcode");
		title.add("绑定用户");			key.add("memberid");
		ExportExcel.Export("实物卡报表",title,key,listmap,response);
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	@Auth(admin=true)
	@RequestMapping(value ="/OrdersCard/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType OrdersCardadd(OrdersCard mOrdersCard,Integer size) throws Exception{
		if(Stringutil.isBlank(mOrdersCard.getTitle()))return sendFalse("说明不可为空");
		if(Stringutil.isBlank(mOrdersCard.getNummax()))return sendFalse("金额不可为空");
		
		mOrdersCard.setBcode(UUID.randomUUID().toString());
		mOrdersCard.setNum(mOrdersCard.getNummax());
		if(Stringutil.isBlank(mOrdersCard.getStar()))
			mOrdersCard.setStar(System.currentTimeMillis());
		if(Stringutil.isBlank(mOrdersCard.getEnd()))
			mOrdersCard.setEnd(mOrdersCard.getStar()+(1000L*60L*60L*24L*365L*10L));
		
		
		
		Long id = System.currentTimeMillis();
		Integer code;
		for (int i = 0; i < size; i++) {
			mOrdersCard.setId(id);
			id=id+1;
			mOrdersCard.setCode(UUID.randomUUID().toString());
			 code=0; 
			while (code<100000) {
				code= new Random().nextInt(999999-100000+1)+100000;
			}
			mOrdersCard.setPwd(code.toString());
			mOrdersCardService.add(mOrdersCard);
		}
		
		return sendTrueMsg("添加成功");
	}
	@Auth()
	@RequestMapping(value ="/OrdersCard/binding", method = RequestMethod.POST) 
	public RequestType OrdersCardbinding(String code,String pwd,String uname) throws Exception{
		if(Stringutil.isBlank(code))return sendFalse("卡号不可为空");
		if(Stringutil.isBlank(pwd))return sendFalse("密码不可为空");
		OrdersCard mOrdersCard=mOrdersCardService.getByparameter("code", code,OrdersCard.class);
		if(mOrdersCard==null)return sendFalse("卡号不存在");
		if(!mOrdersCard.getPwd().equals(pwd))return sendFalse("密码错误");
		if(!Stringutil.isBlank(mOrdersCard.getMemberid()))return sendFalse("该卡已绑定别的用户");
		mOrdersCard.setMemberid(getMember(uname).getId());
		mOrdersCardService.updateBySelect(mOrdersCard);
		return sendTrueMsg("绑定成功");
	}
	@Auth()
	@RequestMapping(value ="/OrdersCard/list", method = RequestMethod.POST) 
	public RequestType OrdersCardlist(OrdersCard mOrdersCard,String uname,String phone,Integer page,Integer rows) throws Exception{
		if(getMember(uname).getSuperadmin()==1){
			if(!Stringutil.isBlank(phone))
				mOrdersCard.setMemberid(getMember(phone).getId());
		}else{
			mOrdersCard.setMemberid(getMember(uname).getId());
		}
		@SuppressWarnings("unchecked")
		List<OrdersCard> listcard = (List<OrdersCard>) mOrdersCardService.getALL(mOrdersCard,page,rows);
		for (OrdersCard ordersCard : listcard) {
			Long mid=ordersCard.getMemberid();
			if(mid!=null){
			ordersCard.setMemberid(Long.valueOf(getMember(mid).getUname()));
			}
		}
		return sendTrueData(listcard);
	}
	
	@Auth()
	@RequestMapping(value ="/OrdersCard/selectbynum", method = RequestMethod.POST) 
	public RequestType OrdersCardselectbynum(BigDecimal num) throws Exception{
		if(num==null)return sendTrueData(0);
		
		Sql msql = new Sql();
		msql.setSql("select code from OrdersCardRule order by id desc");
		List<Map<String, Object>> listOrdersCardRule =	mOrdersCardService.exeSelectSql(msql);
		if(listOrdersCardRule.size()!=0){
			if(!Stringutil.isBlank(listOrdersCardRule.get(0).get("CODE"))){
				msql.setSql("select num from OrdersCardRule where min<"+num+" and code='"+listOrdersCardRule.get(0).get("CODE").toString()+"' order by min desc ");
				 listOrdersCardRule =	mOrdersCardService.exeSelectSql(msql);
				 if(listOrdersCardRule.size()!=0){
					 return sendTrueData(listOrdersCardRule.get(0).get("NUM"));
				 }
			}
		}
		
		return sendTrueData(0);
	}
	
	
	
	
	
	
}
