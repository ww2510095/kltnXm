package com.bm.consumption;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.redis.RedisMemberType;
import com.bm.base.request.RequestType;
import com.google.gson.Gson;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;

/**
 * 消费功能
 * */
public class ConsumptionController extends BaseController{
	
	@Autowired
    private ConsumptionService mConsumptionService;
    /**
	 * 用户消费
	 * @param desu 传入Consumption的json加密信息，石板返回提示信息，成功返回当前时间戳加密信息
	 * */
    @RequestMapping(value="/consumption/member",method=RequestMethod.POST)
    public String ridesMember(String desu) throws Exception{
    	Gson gs =new Gson();
    	//解密
    	Consumption mConsumption  =  gs.fromJson(DESUtils.decode2(desu, MyParameter.KEY_CONSUMPTION), Consumption.class);
    	if(mConsumption==null||mConsumption.getMemberid()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("校验失败")),MyParameter.KEY_CONSUMPTION);//解密失败，密文不符合规则,或者不是需要的密文内容
    	if(mConsumption.getOriginalprice()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("金额错误")),MyParameter.KEY_CONSUMPTION);//消费金额';
    	if(mConsumption.getTime()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("时间错误")),MyParameter.KEY_CONSUMPTION);//'消费生成时间';支付时间
    	if(Stringutil.isBlank(mConsumption.getIntroduce()))
    		return DESUtils.encode2(gs.toJson(sendFalse("描述错误")),MyParameter.KEY_CONSUMPTION);//描述
    	if(System.currentTimeMillis()-mConsumption.getTime()<0||System.currentTimeMillis()-mConsumption.getTime()>1000*60*5)
    		return DESUtils.encode2(gs.toJson(sendFalse("超时")), MyParameter.KEY_CONSUMPTION);//有效时间已过//5分钟
    	//登录验证
    	RedisMemberType rmt = getLogin(getMember(mConsumption.getMemberid()).getUname());
    	if(rmt==null||rmt.getUname()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("请登录")), MyParameter.KEY_CONSUMPTION);
    	
    	//余额验证
    	BigDecimal b =(BigDecimal) mMemberService.getById(rmt.getUserid()).get("PLATFORMCURRENCY");
    	if(b.subtract(mConsumption.getOriginalprice()).doubleValue()<0)
    		return DESUtils.encode2(gs.toJson(sendFalse("余额不足")), MyParameter.KEY_CONSUMPTION);
    	
    	mConsumption.setType(1);
    	mConsumption.setState(0);
    	mConsumptionService.add(mConsumption);
    	return DESUtils.encode2(gs.toJson(sendTrueData(System.currentTimeMillis())), MyParameter.KEY_CONSUMPTION);

    }
    /**
     * 店铺消费
     * @param desu 传入Consumption的json加密信息，石板返回提示信息，成功返回当前时间戳加密信息
     * */
    @RequestMapping(value="/consumption/shop",method=RequestMethod.POST)
    public String ridesshop(String desu) throws Exception{
    	Gson gs =new Gson();
    	//解密
    	Consumption mConsumption  =  gs.fromJson(DESUtils.decode2(desu, MyParameter.KEY_CONSUMPTION), Consumption.class);
    	if(mConsumption==null||mConsumption.getMemberid()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("校验失败")),MyParameter.KEY_CONSUMPTION);//解密失败，密文不符合规则,或者不是需要的密文内容
    	if(mConsumption.getOriginalprice()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("金额错误")),MyParameter.KEY_CONSUMPTION);//消费金额';
    	if(mConsumption.getTime()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("时间错误")),MyParameter.KEY_CONSUMPTION);//'消费生成时间';支付时间
    	if(Stringutil.isBlank(mConsumption.getIntroduce()))
    		return DESUtils.encode2(gs.toJson(sendFalse("描述错误")),MyParameter.KEY_CONSUMPTION);//描述
    	if(System.currentTimeMillis()-mConsumption.getTime()<0||System.currentTimeMillis()-mConsumption.getTime()>1000*60*5)
    		return DESUtils.encode2(gs.toJson(sendFalse("超时")), MyParameter.KEY_CONSUMPTION);//有效时间已过//5分钟
    	//登录验证
    	RedisMemberType rmt = getLogin(getMember(mConsumption.getMemberid()).getUname());
    	if(rmt==null||rmt.getUname()==null)
    		return DESUtils.encode2(gs.toJson(sendFalse("请登录")), MyParameter.KEY_CONSUMPTION);
    	
    	//店铺金额只能增加（卖东西，充值）
    	if(mConsumption.getOriginalprice().doubleValue()>0)
    		return DESUtils.encode2(gs.toJson(sendFalse("金额错误")), MyParameter.KEY_CONSUMPTION);
    	
    	mConsumption.setType(2);
    	mConsumption.setState(0);
    	mConsumptionService.add(mConsumption);
    	return DESUtils.encode2(gs.toJson(sendTrueData(System.currentTimeMillis())), MyParameter.KEY_CONSUMPTION);
    	
    }
    /**
     * 查询个人账单
     * */
    @Auth
    @RequestMapping(value="/consumption/selectmember",method=RequestMethod.POST)
    public RequestType selectmember(Integer rows,Integer page,String uname) throws Exception{
    	Consumption mConsumption  = new Consumption();
    	mConsumption.setMemberid(getLogin(uname).getUserid());
    	mConsumption.setType(1);
    	mConsumption.setState(0);
//    	List<Consumption> listConsumption = IBeanUtil.ListMap2ListJavaBean(mConsumptionService.getALL(mConsumption,rows,page), Consumption.class);
    	return sendTrueData(mConsumptionService.getALL(mConsumption,rows,page));
    }
    /**
     * 查询店铺账单
     * */
    @Auth(consumptionShop={Administration.SELECT})
    @RequestMapping(value="/consumption/selectshop",method=RequestMethod.POST)
    public RequestType selectshop(Integer rows,Integer page,String uname) throws Exception{
    	Consumption mConsumption  = new Consumption();
    	mConsumption.setMemberid(getLogin(uname).getUserid());
    	mConsumption.setType(2);
    	mConsumption.setState(0);
//    	List<Consumption> listConsumption = IBeanUtil.ListMap2ListJavaBean(mConsumptionService.getALL(mConsumption,rows,page), Consumption.class);
    	return sendTrueData(mConsumptionService.getALL(mConsumption,rows,page));
    }
}
