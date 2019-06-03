package com.bm.coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.util.IBeanUtil;

@Service
public class CouponService extends BaseService{

	
	@Override
	protected String getTabName() {
		return "Coupon";
	}
	
	@Override
	public int add(Object obj) throws Exception {
		Long sid =System.currentTimeMillis();
		obj.getClass().getMethod("setId", Long.class).invoke(obj,sid);// 设置id
		while (true) {
			try {
				return super.add(obj);
			} catch (Exception e) {
				sid=sid+1;
				obj.getClass().getMethod("setId", Long.class).invoke(obj,sid);// 设置id
			}
			
		}
		
		
	}
	
	
	public   void OutsendCoupon(Long memberid) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select * from Coupon where length(memberid)<5 and memberid>0 and memberid<100");
		List<Coupon> listCoupon = IBeanUtil.ListMap2ListJavaBean(exeSelectSql(msql), Coupon.class);
		List<Coupon> listCoupon1 = new ArrayList<>();
		for (Coupon coupon : listCoupon) {
			int size =Integer.valueOf(coupon.getMemberid()+"")-1;
			for (int i=0;i<size;i++) {
				coupon.setMemberid(memberid);
				listCoupon1.add(coupon);
			}
			
		}
		addList(listCoupon1);
	}
	public   void OutsendCouponZSH(Long memberid,Integer type) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select * from Coupon where memberid=-"+type);
		List<Coupon> listCoupon = IBeanUtil.ListMap2ListJavaBean(exeSelectSql(msql), Coupon.class);
		List<Coupon> listCoupon1 = new ArrayList<>();
		for (Coupon coupon : listCoupon) {
			coupon.setEnd(System.currentTimeMillis()+(1000L*60L*60L*24L*365));
			coupon.setMemberid(memberid);
			listCoupon1.add(coupon);
			
		}
		addList(listCoupon1);
	}
	public   void OutsendCouponTH(Long memberid) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select * from Coupon where memberid=123");
		List<Coupon> listCoupon = IBeanUtil.ListMap2ListJavaBean(exeSelectSql(msql), Coupon.class);
		List<Coupon> listCoupon1 = new ArrayList<>();
		for (Coupon coupon : listCoupon) {
			coupon.setEnd(System.currentTimeMillis()+(1000L*60L*60L*24L*365*10L));
			coupon.setMemberid(memberid);
			listCoupon1.add(coupon);
			
		}
		addList(listCoupon1);
	}
	public   void md(BigDecimal je,Long zgid,Long memberid) throws Exception{
		Coupon mCoupon = new Coupon();
		mCoupon.setId(System.currentTimeMillis());
		mCoupon.setTitle(je+"元无门槛使用卷("+zgid+")");
		mCoupon.setNumbera(je);
		mCoupon.setNumbermin(MyParameter.mBigDecimal_0);
		mCoupon.setStar(System.currentTimeMillis());
		mCoupon.setEnd(System.currentTimeMillis()+(86400L*365L));
		mCoupon.setType(1);
		mCoupon.setState(0);
		mCoupon.setCardtype(6);
		mCoupon.setMemberid(memberid);
		mCoupon.setMphone("13333333333");
		mCoupon.setOnephone("13333333333");
		add(mCoupon);
	}

	
}
