package com.bm.consumption.pay;

public class ApayJson {
	public enum ApayJson_code{
		TRUE("10000");//成功
		private ApayJson_code(String i) {
			type=i;
		}
		private String type;
		public String getValue(){
			return type;
		}
	}
	
	private AJson alipay_trade_refund_response;//阿里响应值
	
	public AJson getAlipay_trade_refund_response() {
		return alipay_trade_refund_response;
	}

	public void setAlipay_trade_refund_response(AJson alipay_trade_refund_response) {
		this.alipay_trade_refund_response = alipay_trade_refund_response;
	}

	public static class AJson{
		private String code;//支付宝返回码，如果是40004代表失败10000代表成功

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		
	}
}
