package com.bm.consumption.pay;

import com.Shop5941Application;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.consumption.Refundresponse;
import com.bm.consumption.RefundresponseService;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.bm.task.TSystem;
import com.bm.zsh.Zsh;
import com.bm.zsh.ZshService;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;
import com.myjar.desutil.RunException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信相关
 * */
@RestController
@Api(tags = "微信相关")
public class WeiPay extends BaseController{
	
	@Autowired // 订单
	private OrdersService mOrdersService;
	public static void wPAYorderReturn(RefundresponseService mRefundresponseService,OrdersService mOrdersService,Long id,BigDecimal je) throws Exception{
		Orders mOrders=mOrdersService.getById(id,Orders.class);
		if(mOrders==null)
			throw new RunException("订单不存在");
		je=je==null?mOrders.getPayment():je;
		Long ida = System.currentTimeMillis();
		Refundresponse rp = new Refundresponse();
		rp.setOrdercode(mOrders.getOrdernumber());
		rp.setPrice(mOrders.getPayment());
		rp.setOrdernum(mOrders.getPayment());
		rp.setType(Refundresponse.Refundresponse_type.WX.toString());
		Map<String, String> respData = tk(mOrders.getOrdernumber(), je,ida+"");
		String str=  respData.get("return_code");
		if(str.equals("SUCCESS")){
			if(respData.get("result_code").equals("SUCCESS")){
				rp.setIstrue(1);
				rp.setId(ida);
				ida=ida+1;
				rp.setResponsecode("SUCCESS");
				mRefundresponseService.add(rp);
				return;
			}else{
				respData = tk(id+"", je,ida+"");
				if(str.equals("SUCCESS")){
					if(respData.get("result_code").equals("SUCCESS")){
						rp.setIstrue(1);
						rp.setId(ida);
						ida=ida+1;
						rp.setResponsecode("SUCCESS");
						mRefundresponseService.add(rp);
						return;
					}
				}
			
			}
			
		}else{
			respData = tk(id+"", je,ida+"");
			 str=  respData.get("return_code");
			if(str.equals("SUCCESS")){
				if(respData.get("result_code").equals("SUCCESS")){
					rp.setIstrue(1);
					rp.setId(ida);
					ida=ida+1;
					rp.setResponsecode("SUCCESS");
					mRefundresponseService.add(rp);
					return;
				}
			}
		}
		rp.setResponsecode("return_code="+str+";result_code="+respData.get("result_code"));
		rp.setIstrue(0);
		rp.setId(ida);
		ida=ida+1;
		mRefundresponseService.add(rp);
		
		
	
		
	}
//	Refundresponse rp = new Refundresponse();
//	rp.setOrdercode(mOrders.getOrdernumber());
//	rp.setPrice(mOrders.getPayment());
//	rp.setOrdernum(mOrders.getPayment());
//	rp.setType(Refundresponse.Refundresponse_type.ZFB.toString());
//	
//	ApayJson aj = GsonUtil.fromJsonString(tk(mOrders.getOrdernumber(), mOrders.getPayment()), ApayJson.class);
//	rp.setResponsecode(aj.getAlipay_trade_refund_response().getCode());
//	if(aj.getAlipay_trade_refund_response().getCode().equals(ApayJson.ApayJson_code.TRUE.getValue())){
//		rp.setIstrue(1);
//		mRefundresponseService.add(rp);
//		return;
//	}
	
	public static void main(String[] args) throws Exception {
		tk("15789", new BigDecimal("1"), "15");
	}
	
	private static Map<String, String> tk(String ordernumber, BigDecimal payment,String returnkey) throws Exception {
		 WxpayParam wxpayParam = new WxpayParam();
	        wxpayParam.setTotalPrice(payment);

	        OurWxPayConfig ourWxPayConfig = new OurWxPayConfig();
	        WXPay wxPay = new WXPay(ourWxPayConfig);
	        
	        String notstr="abewrxs"+wxpayParam.getOutTradeNo();

	        //根据微信支付api来设置
	        Map<String,String> data = new HashMap<>();
	        data.put("appid",ourWxPayConfig.getAppID());
	        data.put("mch_id",ourWxPayConfig.getMchID());         //商户号
	        data.put("nonce_str",notstr);   					 // 随机字符串小于32位
	        data.put("out_trade_no",ordernumber);   //交易号
	        data.put("out_refund_no",returnkey);   //退单号
	        data.put("total_fee",payment.multiply(new BigDecimal(100)).toString());   //金额
	        data.put("refund_fee",payment.multiply(new BigDecimal(100)).toString());   //金额
	        
	        String s = WXPayUtil.generateSignature(data, ourWxPayConfig.getKey());  //签名
	        data.put("sign",s);
	        /** wxPay.refund 微信退款接口 */
	        Map<String, String> respData = wxPay.refund(data); 
	         System.out.println(respData);
//	        return respData.get("return_code");
	        return respData;
		
	}

	@RequestMapping(value = "/wpay/{sign}", method = RequestMethod.POST)
	public  RequestType weixin(@PathVariable String sign,HttpServletRequest request) throws Exception{
		 String notifyUrl = "http://123.207.147.134:8091/wpay1/order";  //我这里的回调地址是随便写的，到时候需要换成处理业务的回调接口
//		 String notifyUrl = "http://1y74625t01.iok.la:19023/wpay1/order";  //我这里的回调地址是随便写的，到时候需要换成处理业务的回调接口
		
		String id = DESUtils.decode2(sign, MyParameter.KEY_ORDERS);
		List<Map<String, Object>> ListMap = new ArrayList<>();
		Sql mSql = new Sql();
		try {
			Long.valueOf(id);
			Map<String, Object> map = mOrdersService.getById(id);
			if (map != null)
				ListMap.add(map);
		} catch (Exception e) {
			mSql.setRows(1000);
			mSql.setSql("select * from Orders where ordernumber='" + id + "'");
			ListMap = mOrdersService.exeSelectSql(mSql);
		}

		if (ListMap.size() == 0)
			return sendFalse("订单不存在");
		
		BigDecimal b = new BigDecimal("0");
		for (Map<String, Object> map : ListMap) {
			
			b = b.add(new BigDecimal(map.get("PAYMENT").toString()));
			b = b.add(new BigDecimal(map.get("POSTFEE").toString()));
			
//			Object obj =map.get("COUPONID") ;
//			if(obj!=null)
//			if(!Stringutil.isBlank(obj.toString())){
//				mSql.setSql("select * from coupon where id='" +map.get("COUPONID") + "'");
//				ListMap = mOrdersService.exeSelectSql(mSql);
//				if(ListMap.size()!=1)
//					throw new RunException("订单错误，未找到对应优惠券或优惠券异常");
//				
//				b = b.subtract(new BigDecimal(ListMap.get(0).get("NUMBERA").toString()));
//			}
				
		}

        WxpayParam wxpayParam = new WxpayParam();
        wxpayParam.setTotalPrice(b);
       

        OurWxPayConfig ourWxPayConfig = new OurWxPayConfig();
        WXPay wxPay = new WXPay(ourWxPayConfig);
        
        String notstr="abewrxs"+wxpayParam.getOutTradeNo();

        //根据微信支付api来设置
        Map<String,String> data = new HashMap<>();
        data.put("appid",ourWxPayConfig.getAppID());
        data.put("mch_id",ourWxPayConfig.getMchID());         //商户号
        data.put("trade_type","APP");                         //支付场景 APP 微信app支付 JSAPI 公众号支付  NATIVE 扫码支付
        data.put("notify_url",notifyUrl);                     //回调地址
//        data.put("spbill_create_ip",InterceptorConfig.getIpAddress(request));             //终端ip
        data.put("spbill_create_ip","123.207.147.134");             //终端ip
        data.put("total_fee",wxpayParam.getTotalFee());       //订单总金额
        data.put("fee_type","CNY");                           //默认人民币
        data.put("out_trade_no",id);   //交易号
        data.put("body",wxpayParam.getBody());
        data.put("nonce_str",notstr);   // 随机字符串小于32位
        String s = WXPayUtil.generateSignature(data, ourWxPayConfig.getKey());  //签名
        data.put("sign",s);
        
        /** wxPay.unifiedOrder 这个方法中调用微信统一下单接口 */
        Map<String, String> respData = wxPay.unifiedOrder(data); 
        if (respData.get("return_code").equals("SUCCESS")){

            //返回给APP端的参数，APP端再调起支付接口
            Map<String,String> repData = new HashMap<>();
            repData.put("appid",ourWxPayConfig.getAppID());
            repData.put("partnerid",ourWxPayConfig.getMchID());
            repData.put("prepayid",respData.get("prepay_id"));
            repData.put("noncestr",notstr);
            repData.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
            repData.put("package","Sign=WXPay");
            repData.put("sign",WXPayUtil.generateSignature(repData,ourWxPayConfig.getKey()));//签名
//            respData.put("timestamp",repData.get("timestamp"));
//            respData.put("package","Sign=WXPay");
			Shop5941Application.out(repData);
            return sendTrueData(repData);
        }
        throw new Exception(respData.get("return_msg"));	
	}
	@Autowired
	private ZshService mZshService;
	
	/**
	 * 微信支付招商会专用
	 */
	@RequestMapping(value = "/wpay/zsh/{sign}", method = RequestMethod.POST)
	public RequestType systempayzsh(@PathVariable String sign) throws Exception {

		 String notifyUrl = "http://123.207.147.134:8091/wpay1/order";  //我这里的回调地址是随便写的，到时候需要换成处理业务的回调接口
//		 String notifyUrl = "http://1y74625t01.iok.la:19023/wpay1/order";  //我这里的回调地址是随便写的，到时候需要换成处理业务的回调接口
		
		String id = DESUtils.decode2(sign, MyParameter.KEY_ORDERS);
		Zsh mZsh = mZshService.getById(id,Zsh.class);
		
		
		
		

		if (mZsh==null)
			return sendFalse("订单不存在");
		
		BigDecimal b = mZsh.getMoney();

       WxpayParam wxpayParam = new WxpayParam();
       wxpayParam.setTotalPrice(b);
      

       OurWxPayConfig ourWxPayConfig = new OurWxPayConfig();
       WXPay wxPay = new WXPay(ourWxPayConfig);
       
       String notstr="abewrxs"+wxpayParam.getOutTradeNo();

       //根据微信支付api来设置
       Map<String,String> data = new HashMap<>();
       data.put("appid",ourWxPayConfig.getAppID());
       data.put("mch_id",ourWxPayConfig.getMchID());         //商户号
       data.put("trade_type","APP");                         //支付场景 APP 微信app支付 JSAPI 公众号支付  NATIVE 扫码支付
       data.put("notify_url",notifyUrl);                     //回调地址
//       data.put("spbill_create_ip",InterceptorConfig.getIpAddress(request));             //终端ip
       data.put("spbill_create_ip","123.207.147.134");             //终端ip
       data.put("total_fee",wxpayParam.getTotalFee());       //订单总金额
       data.put("fee_type","CNY");                           //默认人民币
       data.put("out_trade_no",id);   //交易号
       data.put("body",wxpayParam.getBody());
       data.put("nonce_str",notstr);   // 随机字符串小于32位
       String s = WXPayUtil.generateSignature(data, ourWxPayConfig.getKey());  //签名
       data.put("sign",s);
       
       /** wxPay.unifiedOrder 这个方法中调用微信统一下单接口 */
       Map<String, String> respData = wxPay.unifiedOrder(data); 
       if (respData.get("return_code").equals("SUCCESS")){

           //返回给APP端的参数，APP端再调起支付接口
           Map<String,String> repData = new HashMap<>();
           repData.put("appid",ourWxPayConfig.getAppID());
           repData.put("partnerid",ourWxPayConfig.getMchID());
           repData.put("prepayid",respData.get("prepay_id"));
           repData.put("noncestr",notstr);
           repData.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
           repData.put("package","Sign=WXPay");
           repData.put("sign",WXPayUtil.generateSignature(repData,ourWxPayConfig.getKey()));//签名
//           respData.put("timestamp",repData.get("timestamp"));
//           respData.put("package","Sign=WXPay");
          
           return sendTrueData(repData);
       }
       throw new Exception(respData.get("return_msg"));	
	
		
		
	}
	/**
    * 微信支付通知地址
    *
    * @param request
    * @return
	 * @throws Exception 
	 * @throws NumberFormatException 
    */
	@RequestMapping(value = "/wpay1/order")
   public void weixin_pay_notify(HttpServletRequest request) throws Exception {
         BufferedReader reader = null;
           reader = request.getReader();
           String line = "";
           String xmlString = null;
           StringBuffer inputString = new StringBuffer();
           while ((line = reader.readLine()) != null) {
               inputString.append(line);
           }
           xmlString = inputString.toString();
           request.getReader().close();
           if(!Stringutil.isBlank(xmlString)) {
               Map<String, String> return_map = WXPayUtil.xmlToMap(xmlString);
               

               //TODO 账变，修改状态，到账提醒
//               Double amount1 = Double.parseDouble(return_map.get("total_fee"));
//               String passbackParams1 = return_map.get("total_fee");
//               String order_no1 = return_map.get("out_trade_no");
//              
//         Application.out(amount1);
//         Application.out(passbackParams1);
//         Application.out(order_no1);
               OurWxPayConfig ourWxPayConfig = new OurWxPayConfig();
               //验签
               if(WXPayUtil.isSignatureValid(xmlString, ourWxPayConfig.getKey())) {
//                     if(return_map.get("return_map").equals("SUCCESS")) {
                           //TODO 账变，修改状态，到账提醒
//                           Double amount = Double.parseDouble(return_map.get("total_fee"));
//                           String passbackParams = return_map.get("total_fee");
                           String order_no = return_map.get("out_trade_no");
                          
                           updateorders(order_no);
//                     }
               }
           }
   }
   
   private RequestType updateorders(String s) throws Exception {
		Orders mOrders = null;
		Zsh mzsh = null ;
		try {
			mOrders = IBeanUtil.Map2JavaBean(mOrdersService.getById(s), Orders.class);
			mzsh = mZshService.getById(s,Zsh.class);
		} catch (Exception e) {
			mzsh = mZshService.getById(s,Zsh.class);
		}

		if (mOrders == null) {
			if(mzsh!=null){
				//招商会订单
				if(mzsh.getStatis()==0){
					mzsh.setStatis(1);
					mzsh.setUpdatetime(System.currentTimeMillis());
					mZshService.updateBySelect(mzsh);
				}
			
			}else{
				
				mOrders = new Orders();
				mOrders.setOrdernumber(s);
				@SuppressWarnings("unchecked")
				List<Orders> listOrders = (List<Orders>) mOrdersService.getALL(mOrders, 1, 1000);
				if (listOrders.size() == 0)
					return sendFalse("订单错误");
				for (Orders orders : listOrders) {
					if(orders.getStatus()==1){
						orders.setUpdatetime(System.currentTimeMillis());
						orders.setPaymenttime(System.currentTimeMillis());
						if(orders.getShippingtype()==3)
							orders.setStatus(3);
						else
							orders.setStatus(2);
						orders.setPaymenttype(3);
						mOrdersService.updateBySelect(orders);
						
						
						//心跳任务
						String[] sa = new String[4];
						sa[0]="0";//任务id
						sa[1]="";//延时时间，毫秒，空为默认值
						sa[2]=mOrders.getId()+"";//数据id
						sa[3]=mOrders.getMemberid()+"";//附加数据
						TSystem.start(sa, mTaskService);
					}
					
				}

				return sendTrueData("支付完成");
			
			}
			
		}else{
			if(mzsh!=null){
				//招商会订单
				if(mzsh.getStatis()==0){
					mzsh.setStatis(1);
					mzsh.setUpdatetime(System.currentTimeMillis());
					mZshService.updateBySelect(mzsh);
				}
			}else{
				if(mOrders.getStatus()==1){
					mOrders.setUpdatetime(System.currentTimeMillis());
					mOrders.setPaymenttime(System.currentTimeMillis());
					mOrders.setStatus(2);
					mOrders.setPaymenttype(3);
				mOrdersService.updateBySelect(mOrders);
				
				//心跳任务
				String[] sa = new String[4];
				sa[0]="0";//任务id
				sa[1]="";//延时时间，毫秒，空为默认值
				sa[2]=mOrders.getId()+"";//数据id
				sa[3]=mOrders.getMemberid()+"";//附加数据
				TSystem.start(sa, mTaskService);
				
				}
			}
			
		}
		return sendFalse("错误");
   }
}
