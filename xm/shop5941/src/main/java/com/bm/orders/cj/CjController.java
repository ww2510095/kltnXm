package com.bm.orders.cj;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.consumption.erp.ERP_orders;
import com.bm.consumption.erp.ERP_send;
import com.bm.coupon.Coupon;
import com.bm.coupon.CouponService;
import com.bm.orders.cj.erpzg.Zg;
import com.bm.orders.cj.erpzg.ZgService;
import com.bm.orders.cj.jc.Cj_address;
import com.bm.orders.cj.jc.CjaddressService;
import com.bm.orders.cj.jc.Jc;
import com.bm.orders.cj.jc.JcService;
import com.bm.user.Member;
import com.myjar.Stringutil;
import com.myjar.desutil.DESUtils;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "抽奖")
public class CjController extends BaseController {
	@Autowired 
	private JcService mJcService;
	@Autowired 
	private CjService mCjService;
	@Autowired 
	private CouponService mCouponService;
	@Autowired 
	private CjaddressService mCjaddressService;
	@Autowired 
	private ZgService mZgService;
	
	@Auth(admin=true)
	@RequestMapping(value ="/jc/add", method = RequestMethod.POST) 
	public RequestType jcadd(Jc mjc,HttpServletRequest req) throws Exception{
		mJcService.add(mjc,req,mFileService);
		return sendTrueMsg("添加成功");
	}
	@Auth(admin=true)
	@RequestMapping(value ="/jc/add_image_all", method = RequestMethod.POST) 
	public RequestType add_image_all(Jc mjc,HttpServletRequest req) throws Exception{
		if(Stringutil.isBlank(mjc.getBh()))return sendFalse("奖池编号不可为空");
		Sql msql = new Sql();
		msql.setSql("update jc set Jiang_ctu='"+FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService)+"' where bh='"+mjc.getBh()+"'");
		mJcService.execSQL(msql);	
	
		return sendTrueMsg("修改成功");
	}
	/**
	 * 奖品查询
	 * */
	@RequestMapping(value ="/jc/select", method = RequestMethod.POST) 
	public RequestType jcselect(Jc mjc,String uname) throws Exception{
		if(!Stringutil.isBlank(uname)){
			if(getMember(uname).getSuperadmin()!=1){
				if(Stringutil.isBlank(mjc.getBh())){
					return sendFalse("奖池编号不可为空");
				}
			}
		}else{
			if(Stringutil.isBlank(mjc.getBh())){
				return sendFalse("奖池编号不可为空");
			}
		}
	
		return sendTrueData(mJcService.getALL(mjc,1,1000));
	}
	//指定时间段内奖池，end为空则无限靠后，star为空，则无限靠前
	@Auth(admin=true)
	@RequestMapping(value ="/jc/getkeyAll", method = RequestMethod.POST) 
	public RequestType jcgetkeyAll(Long star,Long end) throws Exception{
		return sendTrueData(mJcService.getkeyAll(star,end));
	}
	//当前可用奖池
	@RequestMapping(value ="/jc/getkey_system", method = RequestMethod.POST) 
	public RequestType jcgetkey_system() throws Exception{
		return sendTrueData(mJcService.getkey_system());
	}
	
	//erp添加抽奖次数
	@RequestMapping(value ="/cj/erp_number_add", method = RequestMethod.POST) 
	@Transactional
	public RequestType erp_number_add(String arg1) throws Exception{
		try {
			arg1=DESUtils.password(arg1);
			Zg mZg =GsonUtil.fromJsonString(arg1, Zg.class);
			 String s = mZg.getPhone().replaceAll("[^\\d]", "");
			 mZg.setPhone(s);
			if(mZg.getNum()==null)return sendFalse("金额不可为空");
			if(Stringutil.isBlank(mZg.getErp_number_code()))return sendFalse("订单号不可为空");
			if(Stringutil.isBlank(mZg.getPhone()))return sendFalse("账号不可为空");
			if(mZgService.getByparameter("Erp_number_code", mZg.getErp_number_code())!=null)return sendFalse("订单号以存在");
			mZg.setIstrue(0);
			mZgService.add(mZg);
			Cj mcj = new Cj();
			Sql msql = new Sql();
			msql.setRows(1);
			msql.setSql("select bh from jc order by id desc");
			mcj.setJc_bh(mZgService.exeSelectSql(msql).get(0).get("BH").toString());
			mcj.setType(3);
			mcj.setNum(1);
			cjadd(mcj, "13333333333", mZg.getPhone());
			return sendTrueData("添加成功");
		} catch (Exception e) {
			throw new RunException("签名错误");
		}
	}
	//erp添加抽奖次数
	@RequestMapping(value ="/cj/erp_number_add_test_sige", method = RequestMethod.POST) 
	@Transactional
	public RequestType erp_number_add_testsige(Zg mZg) throws Exception{
		if(mZg.getNum()==null)return sendFalse("金额不可为空");
		if(Stringutil.isBlank(mZg.getErp_number_code()))return sendFalse("订单号不可为空");
		if(Stringutil.isBlank(mZg.getPhone()))return sendFalse("账号不可为空");
		if(mZgService.getByparameter("Erp_number_code", mZg.getErp_number_code())!=null)return sendFalse("订单号以存在");
		String a = GsonUtil.toJsonString(mZg);
		a=DESUtils.password(a);
		return sendTrueData(a);
	}
	
	
	@Auth()
	@Transactional
	@RequestMapping(value ="/cj/add", method = RequestMethod.POST) 
	public RequestType cjadd(Cj mcj,String uname,String phone) throws Exception{
//		return sendFalse("活动暂未开启");
//		
		mcj.setT("未处理");
		if(Stringutil.isBlank(mcj.getJc_bh()))return sendFalse("奖池编号不可为空");
		if(mcj.getType()==null)mcj.setType(2);
		Member mMember =getMember(uname);
		Jc mjc = mJcService.getByparameter("bh", mcj.getJc_bh(),Jc.class);
		if(mjc==null)return sendFalse("奖池不存在");
		Long time=System.currentTimeMillis();
		if(time<mjc.getStar())return sendFalse("活动还未开始");
		if(time>mjc.getEnd())return sendFalse("活动已结束");
		Sql msql = new Sql();
		 List<Map<String, Object>> listjc;
		 ERP_orders meo = null;
		if(mcj.getType()==1){
			if(mMember.getGoldcoin().intValue()-mjc.getM_num()<0){
				return sendFalse("积分不足");
			}else{
				
				Member mMember1 =new Member();
				mMember1.setId(mMember.getId());
				mMember1.setGoldcoin(mMember.getGoldcoin().subtract(new BigDecimal(mjc.getM_num())));
				mMemberService.updateBySelect(mMember1);
				
				mMember.setGoldcoin(mMember1.getGoldcoin());
				setRedisMember(mMember, false);
			}
		}else if(mcj.getType()==2){
			//抽奖次数
//			Cj mcj_xn = getcj_xn(mMember.getId(), mcj.getJc_bh());
//			if(mcj_xn==null||mcj_xn.getNum()==0){
//				mcj.setType(1);
//				return cjadd(mcj, uname, phone);
//			}
//			mcj.setNum(mcj_xn.getNum()-1);
			
			 meo = ERP_send.member_orders(uname);
			msql.setSql("select * from zg where ERP_NUMBER_CODE='"+meo.getId()+"'");
			if(mCjService.exeSelectSql(msql).size()!=0){
				if(Integer.parseInt(meo.getBill_val())<50){
//					throw new RunException("次数不足");
//					mcj.setType(1);
//					return cjadd(mcj, uname, phone);
				}
			}
			
		}else{
			if(mMember.getSuperadmin()!=1){
				return sendFalse("权限不足");
			}else{
				 mMember =getMember(phone);
				 if(mcj.getNum()==null)return sendFalse("添加次数不可为空");
				 msql.setRows(1);
				 msql.setSql("select num num from CJ where member_id="+mMember.getId()+" order by id desc ");
					listjc = mCjService.exeSelectSql(msql);
					if(listjc.size()!=0){
						if(listjc.get(0)!=null){
							if(listjc.get(0).get("NUM")!=null){
								mcj.setNum(mcj.getNum()+Integer.valueOf(listjc.get(0).get("NUM").toString()));
							}
						}
					
						 
					}
					mcj.setMember_id(mMember.getId());
					mCjService.add(mcj);
				 return sendTrueMsg("添加成功");
			}
		}
		
		int code = new Random().nextInt(9999-1000+1)+1000;
		msql.setSql("select * from (select jc.*,my_cj_cs(id,num,(select count(1) from cj)) cishuaid from jc where bh= '"+mcj.getJc_bh()+"') where cishuaid is not null");
		listjc = mCjService.exeSelectSql(msql);
		if(listjc.size()==0){
			msql.setRows(1);
			msql.setSql("select jc.*,my_cj_cs(id,dqyz,(select count(1) from cj)) cishuaid from jc where bh= '"+mcj.getJc_bh()+"' and dqyz>"+code);
			 listjc = mCjService.exeSelectSql(msql);
		}
		
		if(listjc.size()==0){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("TITLE", "谢谢参与");
			mcj.setT("已处理");
			listjc.add(map);
			mcj.setJc_id(-1L);
		}else{
			
			
			//优惠券
			if(listjc.get(0).get("XN_ID")!=null){
				Coupon mCoupon=mCouponService.getById(listjc.get(0).get("XN_ID"),Coupon.class);
				mCoupon.setId(System.currentTimeMillis());
				mCoupon.setMemberid(mMember.getId());
				mCouponService.add(mCoupon);
				mcj.setT("已处理");
				mcj.setJc_id(mCoupon.getId());
			}else{
				mcj.setJc_id(Long.valueOf(listjc.get(0).get("ID").toString()));
			}
			
			if(listjc.get(0).get("TITLE").toString().equals("免单")){
//				 msql.setSql("select * from zg where id=(select min(id) from zg where istrue=0 and phone='"+uname+"')");
//				Zg mzg = IBeanUtil.Map2JavaBean(mCjService.exeSelectSql(msql).get(0), Zg.class);
//				
				mCouponService.md(new BigDecimal(meo.getBill_val()),Long.valueOf(meo.getId()), getLogin(uname).getUserid());
//				 msql.setSql("update zg set istrue=1 where id='"+ mzg.getId()+"'");
//				 mCjService.execSQL(msql);
			}else{
//				 msql.setSql("update zg set istrue=1 where id=(select min(id) from zg where istrue=0 and phone='"+uname+"')");
//				 mCjService.execSQL(msql);
			}
			
		}
		if(meo!=null&&meo.getId()!=null){
			Zg mZg = new Zg();
			mZg.setNum(new BigDecimal(meo.getBill_val()));
			mZg.setIstrue(1);
			mZg.setErp_number_code(meo.getId());
			mZg.setPhone(uname);
			mZgService.add(mZg);
		}
		
		
		mcj.setMember_id(mMember.getId());
		Long sa = (System.currentTimeMillis()/1000);
		while (mCjService.getByparameter("code", sa.toString().substring(1,sa.toString().length()))!=null) {
			sa=sa+1;
			
		}
		mcj.setCode(sa.toString().substring(1,sa.toString().length()));
		if("已处理".equals(mcj.getT()))
			mcj.setSu_name("system");
		mCjService.add(mcj);
		return sendTrueData(listjc.get(0));
	}
//	private boolean getcj_xn(String uname,String bh) throws Exception{
//		
////		Sql msql = new Sql();
////		msql.setRows(1);
////		msql.setSql("select * from cj where member_id="+memberid+" and jc_bh='"+bh+"'  order by id desc");
////		List<Map<String, Object>> listcj = mCjService.exeSelectSql(msql);
////		if(listcj.size()==0)return null;
////		return IBeanUtil.Map2JavaBean(listcj.get(0), Cj.class);
//		ERP_orders mERP_orders=ERP_send.member_orders(uname);
//		return mZgService.getByparameter("erp_number_code", mERP_orders.getId())==null;
//	}
	//抽奖次数查询
	@Auth()
	@RequestMapping(value ="/cj/select_max", method = RequestMethod.POST) 
	public RequestType cjselect_max(String bh,String uname) throws Exception{
//		Cj mcj=getcj_xn(getMember(uname).getId(), bh);
//		if(mcj==null)
//			return sendTrueData(0);
		ERP_orders mERP_orders=ERP_send.member_orders(uname);
		if(Integer.parseInt(mERP_orders.getBill_val())>=50)
			return sendTrueData(mZgService.getByparameter("erp_number_code", mERP_orders.getId())==null?1:0);
		return sendTrueData(0);
		
	}
	/**
	 * 抽奖记录
	 * */
	@Auth()
	@RequestMapping(value ="/cj/select", method = RequestMethod.POST) 
	public RequestType cjselect(Cj mcj,String uname,String phone,Integer page,Integer rows) throws Exception{
		if(getMember(uname).getSuperadmin()!=1)phone=uname;
		if(!Stringutil.isBlank(phone)){
			Member mMember=getMember(phone);
			mcj.setMember_id(mMember.getId());
		}
		
		@SuppressWarnings("unchecked")
		List<Cj> listcj = (List<Cj>) mCjService.getALL(mcj,"cj.id",1,page,rows);
			for (Cj cj : listcj) {
				cj.setMember_id(Long.valueOf(getMember(cj.getMember_id()).getUname()));
//				if(cj.getT()==null)
//					cj.setT("实物未处理或虚拟物品");
//				else
//					cj.setT("已处理");
				if(Stringutil.isBlank(cj.getJc_name()))
					cj.setJc_name("谢谢参与");
				
		}
		return sendTrueData(listcj);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 添加或修改收货地址
	 * */
	@Auth()
	@RequestMapping(value ="/Cj_address/add_for_update", method = RequestMethod.POST) 
	public RequestType Cj_addressadd_for_update(Cj_address mCj_address,String uname) throws Exception{
		mCj_address.setMember_id(getMember(uname).getId());
		Sql msql = new Sql();
		msql.setRows(1);
		msql.setSql("select id from "+mCjaddressService.getTabName()+" where member_id="+getMember(uname).getId()+" and jc_id is null");
		try {
			Cj_address mCj_address1=IBeanUtil.Map2JavaBean(mCjaddressService.exeSelectSql(msql).get(0), Cj_address.class);
			mCj_address.setId(mCj_address1.getId());
			mCjaddressService.updateBySelect(mCj_address);
			return sendTrueData("修改成功");
		} catch (Exception e) {
			mCjaddressService.add(mCj_address);
			return sendTrueData("添加成功");
		}
		
	}
	
	
	/**
	 * 查询收货地址
	 * */
	@Auth()
	@RequestMapping(value ="/Cj_address/select", method = RequestMethod.POST) 
	public RequestType Cj_addressselect(Cj_address mCj_address,String uname,Integer page,Integer rows,String phone) throws Exception{
		if(getMember(uname).getSuperadmin()!=1)
			mCj_address.setMember_id(getMember(uname).getId());
		else if(!Stringutil.isBlank(phone))
			mCj_address.setMember_id(getMember(phone).getId());
		return sendTrueData(mCjaddressService.select_not_ZIP_code(mCj_address, page, rows));
	}
	
	/**
	 * 查询奖品发放情况
	 * */
	@Auth()
	@RequestMapping(value ="/Cj_address/select_jp", method = RequestMethod.POST) 
	public RequestType Cj_addressselect_jp(Cj_address mCj_address,String uname,Integer page,Integer rows,String phone) throws Exception{
		if(getMember(uname).getSuperadmin()!=1)
			mCj_address.setMember_id(getMember(uname).getId());
		else if(!Stringutil.isBlank(phone))
			mCj_address.setMember_id(getMember(phone).getId());
		return sendTrueData(mCjaddressService.select_ZIP_code(mCj_address, page, rows));
	}
	
	
	
	
	
	
	/**
	 * 发放实物奖品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Cj_address/update_jp", method = RequestMethod.POST) 
	public RequestType update_jp(Cj mcj,String uname,String zip_code) throws Exception{
		mcj=mCjService.getById(mcj,Cj.class);
		if(mcj==null)return sendFalse("奖品不存在");
		if("已处理".equals(mcj.getT()))return sendFalse("奖品已处理");
//		if(mcj.getJc_id()!=null)return sendFalse("奖品已处理");
//		mcj.setJc_id(-1L);
		if(mCjaddressService.getByparameter("jc_id", mcj.getJc_id().toString())!=null)return sendFalse("奖品已处理");
		
		Sql msql = new Sql();
		msql.setRows(1);
		msql.setSql("select * from "+mCjaddressService.getTabName()+" where member_id="+mcj.getMember_id()+" and jc_id is null");
		Cj_address mCj_address=null;
		try {
			 mCj_address=IBeanUtil.Map2JavaBean(mCjaddressService.exeSelectSql(msql).get(0), Cj_address.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			return sendTrueData("发放失败，该用户未填写收货地址");
		}
		mCj_address.setZip_code(zip_code);
		mCj_address.setJc_id(mcj.getJc_id());
		mCjaddressService.add_ZIP_code(mCj_address);
		//mCjService.updateBySelect(mcj);

		return sendTrueData("发放成功");
		
	}
	
}
