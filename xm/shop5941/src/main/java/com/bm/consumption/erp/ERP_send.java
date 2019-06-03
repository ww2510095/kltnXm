package com.bm.consumption.erp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.bm.base.util.GsonUtil;
import com.bm.base.util.wsdl.SendSoap;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

public class ERP_send {
	/**
	 * 返回数据的节点
	 */
	public static final String namepage= "http://tempuri.org/IServiceMain/";
	public static final String jsoncsc = "jsoncsc";
	public static final String strMsg = "strMsg";// 错误节点
	public static final String SoapUrl = "http://apps.cdkltn.com:7090/ServiceMain.svc?wsdl";
	public static final String creater_star = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"> <soapenv:Header/>  <soapenv:Body>";
	public static final String creater_end = "  </soapenv:Body> </soapenv:Envelope>";
	public static final String type[] ={ "SPCT00000192","SPCT00000193","SPCT00000194"};

	public static ERP_YHJ creater_yhj() {
		String action = namepage+"CouponCreateEx";
		String keys = "<tem:CouponCreateEx><tem:strCouponConfigSn>";
		String keye = " </tem:strCouponConfigSn></tem:CouponCreateEx>";
		// SPCT00000186
		int code = new Random().nextInt(9999-1000+1)+1000;
		String soapstr = creater_star +keys+ type[code%3] +keye+ creater_end;
//		String soapstr = creater_star +keys+ "SPCT00000195" +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get(jsoncsc);
		if (Stringutil.isBlank(str))
			 throw new RunException(map.get(strMsg));
		return GsonUtil.fromJsonString(str, ERP_YHJ.class);

	}
	public static ERP_YHJ yhj_select(String coupon_sn) {
		String action = namepage+"CheckCouponEx";
		String keys = "<tem:CheckCouponEx> <tem:strCouponSn>";
		String keye = "</tem:strCouponSn> </tem:CheckCouponEx>";
		// SPCT00000186
		String soapstr = creater_star +keys+ coupon_sn +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get(jsoncsc);
		if (Stringutil.isBlank(str))
			throw new RunException(map.get(strMsg));
		return GsonUtil.fromJsonString(str, ERP_YHJ.class);

	}

	public static List<ERP_YHJ> yhj_selectList(String[] array) {
		if(array.length==0)return new ArrayList<ERP_YHJ>();
		String action = namepage+"CheckCouponBatchEx";
		String keys = "<tem:CheckCouponBatchEx> <tem:listCouponSn>";
		String keye = "</tem:listCouponSn> </tem:CheckCouponBatchEx>";
		// 5605968589685161,8127506193034576
		StringBuilder sb  = new StringBuilder();
		for (String string : array) {
			sb.append(string);
			sb.append(",");
		}
		String soapstr = creater_star +keys+ sb.substring(0,sb.length()-1) +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get(jsoncsc);
		if (Stringutil.isBlank(str))
			throw new RunException(map.get(strMsg));
		return GsonUtil.fromJsonList(str, ERP_YHJ.class);
		
	}
	
	public static ERP_User member_idnfo(String phone) {
		String action = namepage+"GetVipInfoForWechat";
		String keys = "<tem:GetVipInfoForWechat> <tem:strPhone>";
		String keye = "</tem:strPhone> </tem:GetVipInfoForWechat>";
		// SPCT00000186
		String soapstr = creater_star +keys+ phone +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get("strjson");
		if (Stringutil.isBlank(str))
			throw new RunException(map.get(strMsg));
		return GsonUtil.fromJsonList(str, ERP_User.class).get(0);

	}
	public static ERP_orders member_orders(String phone) {
		String action = namepage+"GetIntegralWaterForWechat";
		String keys = "<tem:GetIntegralWaterForWechat> <tem:strPhone>";
		String keye = "</tem:strPhone><tem:iPreviousId>0</tem:iPreviousId>  <tem:iMaxCount>10</tem:iMaxCount>"
				+ "</tem:GetIntegralWaterForWechat>";
		// SPCT00000186
		String soapstr = creater_star +keys+ phone +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get("strjson");
		if (Stringutil.isBlank(str))
			throw new RunException(map.get(strMsg));
		try {
			ERP_orders mERP_orders = GsonUtil.fromJsonList(str, ERP_orders.class).get(0);
			if(!Stringutil.isBlank(mERP_orders.getBill_val()))
				mERP_orders.setBill_val(new BigDecimal(mERP_orders.getBill_val()).intValue()+"");
			return mERP_orders;
		} catch (Exception e) {
			return new ERP_orders();
		}
		
		
	}
	public static void member_orders1() {
		String action = namepage+"GetIntegralWaterForWechat";
		String keys = "<tem:GetIntegralWaterForWechat> <tem:strPhone>";
		String keye = "</tem:strPhone><tem:iPreviousId>0</tem:iPreviousId>  <tem:iMaxCount>10</tem:iMaxCount>"
				+ "</tem:GetIntegralWaterForWechat>";
		// SPCT00000186
		String soapstr = creater_star +keys+ "14780160021" +keye+ creater_end;
		Map<String, String> map = SendSoap.send_A(SoapUrl, action, soapstr);
		String str = map.get("strjson");
		if (Stringutil.isBlank(str))
			throw new RunException(map.get(strMsg));
		try {
			List<ERP_orders> mERP_orders = GsonUtil.fromJsonList(str, ERP_orders.class);
			for (ERP_orders erp_orders : mERP_orders) {
				System.out.println(erp_orders);
				System.out.println();
			}
		} catch (Exception e) {
		}
		
		
	}
	public static void main(String[] args) {
		member_orders1();
	}
}
