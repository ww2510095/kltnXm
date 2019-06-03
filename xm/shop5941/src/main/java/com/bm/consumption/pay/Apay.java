package com.bm.consumption.pay;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.HttpRequest;
import com.bm.base.util.IBeanUtil;
import com.bm.consumption.Refundresponse;
import com.bm.consumption.RefundresponseService;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.bm.task.TSystem;
import com.bm.zsh.Zsh;
import com.bm.zsh.ZshService;
import com.myjar.desutil.DESUtils;
import com.myjar.desutil.RunException;

/**
 * 支付宝相关
 */
@RestController
@Api(tags = "支付宝相关")
public class Apay extends BaseController {
	// appkey
//	public static final String appid = "2018060860318941";
	public static final String appid = "2018121362514610";
	// 支付宝公钥
	// public static final String
	// ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArcwJjRsKBqrCLe625Q3nBcCH8H3mtmmcVPBQ+UjTaGIIGAibEWORriny5ZA/2qF2AX2tC7BF/BXOTgn8FPn8as7LLazptypImIpoz5jhJFQFhWqFLBPz2CFf3kqzuG4wfAjlWNgd+UwzpEkezFah9M1TFR+F3WciIqTNluFTi/zV2OVSyOYgCL/WyxFoB09n9piUs1y7waZOzOpypPVCuYPNW5QDMhnd6roiKl6qHkA1AcNZY5PLr0A9LwR+rkNOsrbqoLRyrZFiuazdbZ0f9PA1tV35ls6sBnceqBBqz4QqtMhTTP7XbbRG7rne6NZD5vOrGkUwmk0LVbo8MUe3PQIDAQAB";
	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3UCH/+K4Tn31gn0+X7Q2ewlTCJUltLNJizyDcyHjsiTfSbGPwQRBoSuHen/mgXPlcJet9koVu9A9v3AVPbjOJux3Jba38uJdDtVnkemRSYIyUVt8AwkaY23hy8Ku2L/5f9BQeXpxS58CRXBanwnlIqkq28LAEilxiWXBbfanihUwdCkRbyiOWOYUErc3LwwbDg6VY/pJIiRZmbPcgSxvyMv/43sI1BFYX4dRos5pGTyF/SS7mGBo8BHzYwG05lf5j5JmW6GOATGNg+xeOpGSkXjfU5JF/A2TG+S6M9IGlyOmQXYRZVUHebWi6JDO+jAXbMqemgztJs78tcekHf0P3QIDAQAB";
//	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArcwJjRsKBqrCLe625Q3nBcCH8H3mtmmcVPBQ+UjTaGIIGAibEWORriny5ZA/2qF2AX2tC7BF/BXOTgn8FPn8as7LLazptypImIpoz5jhJFQFhWqFLBPz2CFf3kqzuG4wfAjlWNgd+UwzpEkezFah9M1TFR+F3WciIqTNluFTi/zV2OVSyOYgCL/WyxFoB09n9piUs1y7waZOzOpypPVCuYPNW5QDMhnd6roiKl6qHkA1AcNZY5PLr0A9LwR+rkNOsrbqoLRyrZFiuazdbZ0f9PA1tV35ls6sBnceqBBqz4QqtMhTTP7XbbRG7rne6NZD5vOrGkUwmk0LVbo8MUe3PQIDAQAB";
	// 应用私钥
	public static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDdQIf/4rhOffWCfT5ftDZ7CVMIlSW0s0mLPINzIeOyJN9JsY/BBEGhK4d6f+aBc+Vwl632ShW70D2/cBU9uM4m7Hcltrfy4l0O1WeR6ZFJgjJRW3wDCRpjbeHLwq7Yv/l/0FB5enFLnwJFcFqfCeUiqSrbwsASKXGJZcFt9qeKFTB0KRFvKI5Y5hQStzcvDBsODpVj+kkiJFmZs9yBLG/Iy//jewjUEVhfh1GizmkZPIX9JLuYYGjwEfNjAbTmV/mPkmZboY4BMY2D7F46kZKReN9TkkX8DZMb5Loz0gaXI6ZBdhFlVQd5taLokM76MBdsyp6aDO0mzvy1x6Qd/Q/dAgMBAAECggEAJ9VRZuFTm1YUGG0iCKryUA5qs5XelvzcVcQMSnMkglMfHC5qmi6A/lpO5uAWCOJrwWyddkT6SrkAFPJB8L3P57QU82ZFEeZtKJmFLwDlFl3pXzpgRk+iP5pt13hycDWH3toLUwOXWC0FhKjot89ZQHdCbxRN7jcl6L24KGsK2NIIAYozsRL8tVP6XlFiDoT1qPWwaz2Db23eNBabWYqRFxGKygq+8gWxrjM0zT06jtK1ugOI3WALtt1pPxQxOY4QxQqxlwIvcdnFAeuYHO3JHCPEmwFMYAcn1Ip7fbyNzL9p73wA3ACtv2j8q/XgGBXWMCykyqJtiLi1HefmyCh1IQKBgQD5CJoUxXzuUVQwL3ijqbQxruU3YfvKRrr341yMVy5VqKkDhr07c1xyS+OCumTfiRDestjSE/bbScpqjn8Pg+D0sHn03eeagIe8GlHa4NntTb75qsyqioWZRXh9btUv7Zj3q0Eov4aH5I6WdP0w4BfLbuESktrv4XlermKFxXBiBQKBgQDjcPpsnK5XiKwgzkThwvCssAodnQTWiO+KapSCmxPv3kBo4HNnhDtnQvD2PAJaHHpFRTEvNYvrReDGuGdNMVvejXvjNdQFVieebo8fHy6Fb+G3V3RdxjO7Aqfb/0ktggFvaHMVj1Eu7xHT0vcjsJA7BlWTa7Cbodga5u+uR+cl+QKBgQCcaEulqhd58niGvvntCKPU5HywGQk0FNJO6Gxy9Dp0sDZesZxeH2k7m/ArZff+8S44QGmvH7zWI3VPEWo6C2EZLVGbeleBESKvAHTSnJAi28aLNjvPDKs2BYzUcuzQalC9lMCa4BeVd/YaByYSMVygd2ZC7Rs2uA0raykTzVp1JQKBgEMw4Dpv+y1O6hLy/PR5I6LQitx+w6whXirVG7SN3dDyJrMVA+Lj9Kyn4QnNZqB6Y0DNlR8uQXyq8yGh3pq2Zz6TK7NO9qjw79llLhDypEq1Z8hDJNSDNkscGLzsaRDxpDzmbYFECqbD589dNrerdKCLWYKowM7pfC9OZCZnhYj5AoGAZZS3XXRbVInefTIyhgH8iAyTUOCu0//K4hh+TpmHmRN37FRSJyth7khG6zl4jAta5mp/1EO0cOkuuaVoS/8298cnMsYDQcZarCsXUtB5344CKTKoDUUFAM/WLvebm0Z7JAGDOuh20UUylVvRuNQhvuRQRX1DvrJuxo60GLtEd3g=";
//	public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDRLzBnR7tPYSaUq7tHlEGizmrPp0/ybUDgN5SEto6CYPBHVfwQ3YM9OYipf4Sj92GJQqzWZTJzBxNb95l1N43HhvpfvKcyXBNm+DzB8qCMUmAyfWl5MZIr7y7Cf/1ozF3u/T63lrbSFrFISicLGMSNr0WDVNDUArXKdsk64Yrkr0HabwTFDaN/mja5LRdkwXkPlmk9svyQ+5dzTAvRnwSHkE8zCtflwcikEr3AyoclPVcr60b2za4Jk4ISuuA+1CVTH0STmRpcfpw09L6sO0gOnigjo2kqU/+D641U6DzMuAn+N28NFsJ4GQlhSAMcYpid3wWrqxzLYRApvY5ogqx7AgMBAAECggEBAL60GZmeoJoOhKC3KyZnd6sYKfIjo1Ps3F9M7F5NHACqpYMzVXh+qZ3QbNGxyle9OLNtEKroWSTax4BpawKc4emMGYfcMkVMAfg2yRU7v7gNhVuiT9zIH21kQanaC0lWPzCYuWufwA4p1/eCerO4IcDVkDbZxQPzcI4i2vOOGRS/NzLjnYvPb/znnSBGz/nwVzWfWBaJquVBu6dIlaaBTcLFmKccKhxoiHHaoSm8CjS9qKHPjE2woL1JPQMYS8d/QEBvmhX3Tsxq/FtCt01NeyEeS37aeygMd1ed95i45Zg46FTKP9H3XkXfMDsFxYRsN1QjxfdHcsQXl8ZDsD2AvFECgYEA9lqM0kzTgGnyoindjmIZ1M7OMXg4M2IgzYXo+Z50171IqxR0b1tf8p1g1AX8GFP9dgIivuaKjn8Wqz3G4IH00V1GBsEr9bfSRjXep4Ue7/eRhANLYLErkHeySaf8CZD6MQ2L5EKfwkvxHE1nIODRnHy/cn/hvAMl6Vlu2fFbAfkCgYEA2WANl1A1L0yEnvwrPPYxiEPsurRuhWc0a+hFHv97g+Tph4UoAcgXqeRIsVY2erVddJlRhtzfE7294muuH8NE9FFfA0++mwKc1JeVfYTvOVgxUfHLbNuoMtabhETom7C9BtOd+nVsx9Kq1TTcwVVq03ud0eC99YQL6tLhbeC/fxMCgYAWHyl4FPgFFv53u2SuzXZeWre/T3lUe+JQMk8qYyVA3jlxS2PtP/pMsf8o6uSpVplzCFwQYmlcxGLHu8NpE6JlTofkQWJuLTSPo3S7/EM77zI89YLGSwKLdYQoAxKp1T/yObIvLCBmdSVQkk7JcAbCk50pbfxaQrobyzEeFwNCGQKBgAy/ICzmTCgY4ZH9EYACo8bszL3qBK0X7zAAr8TwkEqcqsECouTv2cthko2rk4hAiFllAC3bV81ti+vyCoTcS3fUKbElBPVMAuxIc0CoAesn68R1XEtm2gcZJIduDwHKLFtxH0tnhVh2VwQnlGZV3uzFy+xZtGnoM1ayxPtPeYs3AoGBAJX2byQysKD8UxIeIkxW9WhEbg8GZ5btnE5uhRZbl1TJ28nLMG85s/EvlizElCBeyXp1mIQJYZmtugNa5X6We+iuk/8HAIS4bnS4E70Wmmh0RYzHQIgWZ2iA+ql0xnc7OaVzX4KlXZ0hV3i+fpEsY7utki1NKwrl7S/Oq3Fyv2+z";
	// 订单支付回调
	 public static final String order_hd="http://123.207.147.134:8091/apay/order";
//	public static final String order_hd = "http://1y74625t01.iok.la:19023/apay/order";
	 
	 
	 public static final String apayReturn="https://openapi.alipay.com/gateway.do";
	 
	 

	@Autowired // 订单
	private OrdersService mOrdersService;
	
	@Autowired//招商会
	private ZshService mZshService;
	@Autowired
    private RefundresponseService mRefundresponseService;
   
	
	/**
	 * 支付宝订单回调
	 */
	@RequestMapping(value = "/apay/orderReturn")
	public RequestType orderReturn(Long id) throws Exception {
		aPAYorderReturn(mRefundresponseService,mOrdersService, id);
		return sendTrueMsg("成功");
	}
	public static void aPAYorderReturn(RefundresponseService mRefundresponseService,OrdersService mOrdersService,Long id) throws Exception{
		Orders mOrders=mOrdersService.getById(id,Orders.class);
		if(mOrders==null)
			throw new RunException("订单不存在");
		Refundresponse rp = new Refundresponse();
		Long ida = System.currentTimeMillis();
		rp.setOrdercode(mOrders.getOrdernumber());
		rp.setPrice(mOrders.getPayment());
		rp.setOrdernum(mOrders.getPayment());
		rp.setType(Refundresponse.Refundresponse_type.ZFB.toString());
		
		ApayJson aj = GsonUtil.fromJsonString(tk(mOrders.getOrdernumber(), mOrders.getPayment()), ApayJson.class);
		rp.setResponsecode(aj.getAlipay_trade_refund_response().getCode());
		if(aj.getAlipay_trade_refund_response().getCode().equals(ApayJson.ApayJson_code.TRUE.getValue())){
			rp.setIstrue(1);
			rp.setId(ida);
			ida=ida+1;
			mRefundresponseService.add(rp);
			return;
		}
		rp.setIstrue(0);
		rp.setId(ida);
		ida=ida+1;
		mRefundresponseService.add(rp);
		
		rp.setOrdercode(id+"");
		 aj = GsonUtil.fromJsonString(tk(id+"", mOrders.getPayment()), ApayJson.class);
		 if(aj.getAlipay_trade_refund_response().getCode().equals(ApayJson.ApayJson_code.TRUE.getValue())){
				rp.setIstrue(1);
				rp.setId(ida);
				ida=ida+1;
				mRefundresponseService.add(rp);
				return;
			}
			rp.setIstrue(0);
			rp.setId(ida);
			ida=ida+1;
			mRefundresponseService.add(rp);
		
		
//		Map<String, String> params;
//		
//		if(je==null)
//			params = buildorderReturnParamMap(mOrders.getPayment().toString(),  id+"");
//		else
//			params = buildorderReturnParamMap(je.toString(),  id+"");
////		String orderParam = buildOrderParam(params);
//		String apaysign = getSign(params, APP_PRIVATE_KEY);
////		final String orderInfo = orderParam + "&" + apaysign;
//		
//		Map<String, Object> mmap = new HashMap<String, Object>();
//		  
//        for (Entry<String, String> e : params.entrySet()) { 
//        	mmap.put(e.getKey(), e.getValue());
//        }  
//        mmap.put("sign",apaysign.substring(5,apaysign.length()));
//		HttpRequest.sendPost(apayReturn,mmap);
		
	}
	public static void main(String[] args) {
		tk("123", new BigDecimal("1"));
	}
	private static String tk(String Str,BigDecimal je) {
		Map<String, String> params = buildorderReturnParamMap(je.toString(),  Str);
		String apaysign = getSign(params, APP_PRIVATE_KEY);
		Map<String, Object> mmap = new HashMap<String, Object>();
        for (Entry<String, String> e : params.entrySet()) { 
        	mmap.put(e.getKey(), e.getValue());
        }  
        mmap.put("sign",apaysign.substring(5,apaysign.length()));
		return HttpRequest.sendPost(apayReturn,mmap);
	}
	/**
	 * 退款参数
	 * */
	public static Map<String, String> buildorderReturnParamMap(String jinge, String orderid) {
		Map<String, String> keyValues = new HashMap<String, String>();

		keyValues.put("app_id", appid);

		keyValues.put("biz_content",
				"{\"out_trade_no\":\"" + orderid+ "\",\"refund_amount\":\"" + jinge + "\"}");

		keyValues.put("charset", "UTF-8");

		keyValues.put("method", "alipay.trade.refund");

		keyValues.put("sign_type", "RSA2");

		keyValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		keyValues.put("version", "1.0");


		return keyValues;
	}

	
	/**
	 * 支付宝订单回调
	 */
	@RequestMapping(value = "/apay/order")
	public RequestType apayorder(HttpServletRequest request) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		// getParameterMap()一般多用于接收前台表单多参数传输的数据
		// 支付宝的回调都是把信息放到request里面
		Map<String, String[]> requestParams = request.getParameterMap();
		// keyset()是获取所有的key值，iterator()是迭代遍历
		Iterator<String> iter = requestParams.keySet().iterator();
		while (iter.hasNext()) {

			String name = (String) iter.next();
			// 这里把key放到数组里面
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			// 这个for循环的尊用就是把上面那个String中的值都遍历一遍
			for (int i = 0; i < values.length; i++) {
				// 这个是三元运算符
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 把数据全部加进map集合中 name就是key valueStr就是value
			params.put(name, valueStr);

		}
		// for(iter.hasNext()){
		// String name = (String)iter.next();
		// //这里把key放到数组里面
		// String[] values = (String[]) requestParams.get(name);
		// String valueStr = "";
		// //这个for循环的尊用就是把上面那个String中的值都遍历一遍
		// for(int i = 0 ; i <values.length;i++){
		// //这个是三元运算符
		// valueStr = (i == values.length -1)?valueStr + values[i]:valueStr +
		// values[i]+",";
		// }
		// //把数据全部加进map集合中 name就是key valueStr就是value
		// params.put(name,valueStr);
		// }
		// sign就是签名 trade_status是交易的状态

		// 非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
		// 这个非常重要，但是不知道这个是什么意思
		// 这个是签名类型 sign参数已经帮我们删除掉了，在这里我们要把签名类型删除掉
		params.remove("sign_type");
		try {
			// 这里是RSA验证签名
			// Configs.getAlipayPublicKey() 其实就是支付宝的应用公钥（记住不是支付宝的公钥，是支付宝应用公钥）
			// Configs.getSignType()也就是支付宝文本类型里面的签名类型：RSA2
			// boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params,
			// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0S8wZ0e7T2EmlKu7R5RBos5qz6dP8m1A4DeUhLaOgmDwR1X8EN2DPTmIqX+Eo/dhiUKs1mUycwcTW/eZdTeNx4b6X7ynMlwTZvg8wfKgjFJgMn1peTGSK+8uwn/9aMxd7v0+t5a20haxSEonCxjEja9Fg1TQ1AK1ynbJOuGK5K9B2m8ExQ2jf5o2uS0XZMF5D5ZpPbL8kPuXc0wL0Z8Eh5BPMwrX5cHIpBK9wMqHJT1XK+tG9s2uCZOCErrgPtQlUx9Ek5kaXH6cNPS+rDtIDp4oI6NpKlP/g+uNVOg8zLgJ/jdvDRbCeBkJYUgDHGKYnd8Fq6scy2EQKb2OaIKsewIDAQAB","utf-8","RSA2");
			// 如果验证上面的boolean为true的话，我们就应该更改下订单的状态，减少下库存这些操作

			if(params.get("trade_status").equals("TRADE_CLOSED"))return  sendFalse("支付失败");
			
			String s = params.get("out_trade_no");// 支付宝订单号
			Orders mOrders = null;
			Zsh mzsh = null ;
			try {
				mOrders = IBeanUtil.Map2JavaBean(mOrdersService.getById(s), Orders.class);
				if(mOrders==null)
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

					//订单
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
							orders.setPaymenttype(4);
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
						mOrders.setPaymenttype(4);
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
		} catch (AlipayApiException e) {
		}
		return sendFalse("错误");
	}

	/**
	 * 支付宝支付
	 */
	@RequestMapping(value = "/apay/{sign}", method = RequestMethod.POST)
	public RequestType systempay(@PathVariable String sign) throws Exception {
		Sql mSql = new Sql();
		try {
			String id = DESUtils.decode2(sign, MyParameter.KEY_ORDERS);
			List<Map<String, Object>> ListMap = new ArrayList<>();

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

			// Orders mOrders=
			// IBeanUtil.Map2JavaBean(mOrdersService.getById(id), Orders.class);
			//
			// if(mOrders==null)return sendFalse("订单不存在");
			// if(mOrders.getPaymenttype()!=1)return sendFalse("该订单为货到付款");
			// return order(mOrders.getId()+"", "支付", "购买商品",
			// mOrders.getPayment().toString(), order_hd);
			BigDecimal b = new BigDecimal(0);
			for (Map<String, Object> map : ListMap) {
				
				b = b.add(new BigDecimal(map.get("PAYMENT").toString()));
				b = b.add(new BigDecimal(map.get("POSTFEE").toString()));
				
//				Object obj =map.get("COUPONID") ;
//				if(obj!=null)
//				if(!Stringutil.isBlank(obj.toString())){
//					mSql.setSql("select * from coupon where id='" +map.get("COUPONID") + "'");
//					ListMap = mOrdersService.exeSelectSql(mSql);
//					if(ListMap.size()!=1)
//						throw new RunException("订单错误，未找到对应优惠券或优惠券异常");
//					
//					b = b.subtract(new BigDecimal(ListMap.get(0).get("NUMBERA").toString()));
//				}
					
			}
			
			Map<String, String> params = buildOrderParamMap(b.toString(), "商城支付", id);
			String orderParam = buildOrderParam(params);
			String apaysign = getSign(params, APP_PRIVATE_KEY);
			final String orderInfo = orderParam + "&" + apaysign;

			return sendTrueData(orderInfo);

		} catch (Exception e) {
			return sendFalse("签名错误");
		}

	}
	/**
	 * 支付宝支付
	 */
	@RequestMapping(value = "/apay/zsh/{sign}", method = RequestMethod.POST)
	public RequestType systempayzsh(@PathVariable String sign) throws Exception {
		try {
			String id = DESUtils.decode2(sign, MyParameter.KEY_ORDERS);
			Zsh mZsh = mZshService.getById(id,Zsh.class);
			
			if (mZsh==null)
				return sendFalse("订单不存在");
			
			BigDecimal b =mZsh.getMoney();
			
			Map<String, String> params = buildOrderParamMap(b.toString(), "商城支付", id);
			String orderParam = buildOrderParam(params);
			String apaysign = getSign(params, APP_PRIVATE_KEY);
			final String orderInfo = orderParam + "&" + apaysign;
			
			return sendTrueData(orderInfo);
			
		} catch (Exception e) {
			return sendFalse("签名错误");
		}
		
	}

	/**
	 * 构造支付订单参数信息
	 * 
	 * @param map
	 *            支付订单参数
	 * @return
	 */
	public static String buildOrderParam(Map<String, String> map) {
		List<String> keys = new ArrayList<String>(map.keySet());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			sb.append(buildKeyValue(key, value, true));
			sb.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		sb.append(buildKeyValue(tailKey, tailValue, true));

		return sb.toString();
	}

	/**
	 * 拼接键值对
	 * 
	 * @param key
	 * @param value
	 * @param isEncode
	 * @return
	 */
	private static String buildKeyValue(String key, String value, boolean isEncode) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("=");
		if (isEncode) {
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				sb.append(value);
			}
		} else {
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * 构造支付订单参数列表
	 * 
	 * @param pid
	 * @param app_id
	 * @param target_id
	 * @return
	 */
	public static Map<String, String> buildOrderParamMap(String jinge, String title, String orderid) {
		Map<String, String> keyValues = new HashMap<String, String>();

		keyValues.put("app_id", appid);

		keyValues.put("biz_content",
				"{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + jinge
						+ "\",\"subject\":\"" + title + "\",\"body\":\"成都八明科技有限公司\",\"out_trade_no\":\"" + orderid
						+ "\",\"notify_url\":\"" + order_hd + "\"}");

		keyValues.put("charset", "UTF-8");

		keyValues.put("method", "alipay.trade.app.pay");

		keyValues.put("sign_type", "RSA2");

		keyValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		keyValues.put("version", "1.0");

		keyValues.put("notify_url", order_hd);

		return keyValues;
	}

	/**
	 * 对支付参数信息进行签名
	 * 
	 * @param map
	 *            待签名授权信息
	 * 
	 * @return
	 */
	public static String getSign(Map<String, String> map, String rsaKey) {
		List<String> keys = new ArrayList<String>(map.keySet());
		// key排序
		Collections.sort(keys);

		StringBuilder authInfo = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			authInfo.append(buildKeyValue(key, value, false));
			authInfo.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		authInfo.append(buildKeyValue(tailKey, tailValue, false));

		String oriSign = sign(authInfo.toString(), rsaKey);
		String encodedSign = "";

		try {
			encodedSign = URLEncoder.encode(oriSign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "sign=" + encodedSign;
	}

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static String getAlgorithms(boolean rsa2) {
		return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
	}

	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(getAlgorithms(true));

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	

}
