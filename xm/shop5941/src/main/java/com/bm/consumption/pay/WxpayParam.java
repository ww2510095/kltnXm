package com.bm.consumption.pay;

import java.math.BigDecimal;

public class WxpayParam {

    /** 微信支付的金额是String类型 并且是以分为单位
     * 下面举个例子单位是元是怎么转为分的
     * */
    BigDecimal totalPrice  ; //此时的单位是元

    private String body = "5941商城平台商户";
//    private String totalFee = totalPrice.multiply(new BigDecimal(100)).toBigInteger().toString();
    /** 随机数字字符串*/
    private String outTradeNo=System.currentTimeMillis()+"";
    
    

    public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTotalFee() {
        return totalPrice.multiply(new BigDecimal(100)).toBigInteger().toString();
    }

//    public void setTotalFee(String totalFee) {
//        this.totalFee = totalFee;
//    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
