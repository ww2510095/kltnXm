package com.bm.Aenum;

public enum Orders_paymenttype implements Enum_i{
	ZFB(4,"支付宝"),//支付宝
	WX(3,"微信"),//微信
	KLB(5,"快乐币"),//快乐币
	KP(6,"卡片");//卡片
	private Orders_paymenttype(int i,String v) {
		_k=i;
		_v=v;
	}
	private int _k;
	private String _v;
	
	@Override
	public String getValue(){
		return _v;
	}
	@Override
	public int getKey(){
		return _k;
	}

}
