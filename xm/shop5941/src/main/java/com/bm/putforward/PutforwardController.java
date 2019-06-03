package com.bm.putforward;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.MyDate;
import com.bm.putforward.zfb.Zfb;
import com.bm.putforward.zfb.ZfbService;
import com.bm.user.Member;
import com.bm.user.PhoneMessage;
import com.bm.user.PhoneMessageService;
import com.myjar.Stringutil;

@RestController
@Api(tags = "提现")
public class PutforwardController extends BaseController{
	@Autowired
	private PutforwardService mPutforwardService;
	@Autowired
	private ZfbService mZfbService;
	@Autowired
	private PhoneMessageService mPhoneMessageService;
	
	
	public static final BigDecimal PROCEDURES = new BigDecimal("2");
	public static final BigDecimal MONEY_a = new BigDecimal("0.0015");
	public static final BigDecimal MONEY_MAX = new BigDecimal("25");

	/**
	 * 申请绑定支付宝
	 * name:真实姓名
	 * zfb:支付宝账户
	 * message:短信验证码
	 * */
	@RequestMapping(value ="/zfb/add", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType add(Zfb mZfb,Integer message,String uname) throws Exception{
		if(message==null)return sendFalse("验证码不可为空");
		if(Stringutil.isBlank(mZfb.getName()))return sendFalse("真实姓名不可为空");
		if(Stringutil.isBlank(mZfb.getZfb()))return sendFalse("支付宝账户不可为空");
//		if(Stringutil.isBlank(mZfb.getZfbcode()))return sendFalse("密码不可为空");
		
		
		PhoneMessage pm = new PhoneMessage();
		pm.setPhone(uname);
		pm.setType(MyParameter.phone_message_type4);
		pm.setCode(message);
		
		@SuppressWarnings("unchecked")
		List<PhoneMessage> lm = (List<PhoneMessage>) mPhoneMessageService.getALL(pm);
		if (lm.size() == 0)
			return sendFalse("验证码错误！");
		else {
			if (lm.get(0).getStatus() == 1)
				return sendFalse("验证码已使用！");
			pm.setStatus(1);
			pm.setId(lm.get(0).getId());
			
		}
	
		mPhoneMessageService.updateBySelect(pm);
		
		mZfb.setPhone(uname);
		mZfb.setIstrue(1);
		mZfb.setZfbcode(message.toString());
		mZfb.setMemberid(getLogin(uname).getUserid());
		mZfbService.add(mZfb);
		
		Member mMember = new Member();
		mMember.setId(mZfb.getMemberid());
		mMember.setZfb(mZfb.getZfb());
		mMember.setMembername(mZfb.getName());
		mMemberService.updateBySelect(mMember);
		
		setRedisMember(mMemberService.getById(mMember,Member.class), false);
		
		return sendTrueMsg("绑定成功");
		
	}
	/**
	 * 审核支付宝
	 * */
//	@RequestMapping(value ="/zfb/auto", method = RequestMethod.POST) 
//	@Auth
//	@Transactional
//	public RequestType auto(Zfb mZfb,Integer message,String uname) throws Exception{
//		if(mZfb.getId()==null)
//			return sendFalse("审核编号不可为空");
//		if(getMember(uname).getSuperadmin()!=1)
//			if(message==null)
//				return sendFalse("验证码不可为空");
//		
//		Integer i = mZfb.getIstrue();
//		if(i==null)
//			return sendFalse("状态不可为空");
//		mZfb = mZfbService.getById(mZfb,Zfb.class);
//		
//		if(mZfb==null)
//			return sendFalse("审核编号不存在");
//		
//		if(mZfb.getIstrue()==1)
//			return sendFalse("该数据已审核");
//		
//		mZfb.setIstrue(i);
//		mZfbService.updateBySelect(mZfb);
//		
//		
//		Member mMember = new Member();
//		mMember.setId(mZfb.getMemberid());
//		mMember.setZfb(mZfb.getZfb());
//		mMember.setMembername(mZfb.getName());
//		mMemberService.updateBySelect(mMember);
//		
//		setRedisMember(mMemberService.getById(mMember,Member.class), false);
//		
//		return sendTrueMsg("审核成功");
//		
//	}
	/**
	 * 支付宝申请列表
	 * */
	@RequestMapping(value ="/zfb/list", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType list(Zfb mZfb,String uname,Integer page,Integer rows) throws Exception{
		return sendTrueData(mZfbService.getALL(mZfb, "istrue,id desc", 0, page, rows));
		
	}
	/**
	 * 申请提现
	 * */
	@RequestMapping(value ="/Putforward/add", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType Putforwardadd(Putforward mPutforward,String uname) throws Exception{
		if(mPutforward.getNum()==null)return sendFalse("提现金额不可为空");
		if(mPutforward.getNum().doubleValue()<5)return sendFalse("最少提5元");
		if(mPutforward.getNum().intValue()!=mPutforward.getNum().doubleValue())return sendFalse("提现金额必须是整数");
		
		Member mMember = mMemberService.getById(getLogin(uname).getUserid(),Member.class);
		if(Stringutil.isBlank(mMember.getZfb()))
			return sendFalse("请先绑定支付宝",-2);
		
		mPutforward.setIstrue(0);
		mPutforward.setMemberid(getLogin(uname).getUserid());
		mPutforward.setPhone(uname);
		mPutforward.setImagepath("审核中");
		mPutforward.setName(mMember.getMembername());
		mPutforward.setZfb(mMember.getZfb());
		
		mPutforward.setProcedures(mPutforward.getNum().multiply(MONEY_a));
		if(mPutforward.getProcedures().compareTo(PROCEDURES)==-1)
			mPutforward.setProcedures(PROCEDURES);
		if(mPutforward.getProcedures().compareTo(MONEY_MAX)==1)
			mPutforward.setProcedures(MONEY_MAX);
		
		mPutforward.setMoney(mPutforward.getNum().subtract(mPutforward.getProcedures()));
		
		mMember.setPlatformcurrency(mMember.getPlatformcurrency().subtract(mPutforward.getNum()));
		if(mMember.getPlatformcurrency().doubleValue()<0)
			return sendTrueMsg("余额不足");
		mMemberService.updateBySelect(mMember);
		
		setRedisMember(mMember, false);
		
		mPutforwardService.add(mPutforward);
		
		return sendTrueMsg("申请成功,等待审核");
		
	}
	/**
	 * 提现审核
	 * */
	@RequestMapping(value ="/Putforward/auto", method = RequestMethod.POST) 
	@Auth(admin=true)
	@Transactional
	public RequestType Putforwardauto(Putforward mPutforward,HttpServletRequest req) throws Exception{
		Integer i = mPutforward.getIstrue();
		String str =mPutforward.getImagepath();
		if(mPutforward.getId()==null)
			return sendFalse("审核编号不可为空");
		if(i==null)
			return sendFalse("状态不可为空");
		mPutforward=mPutforwardService.getById(mPutforward,Putforward.class);
		if(mPutforward==null)
			return sendFalse("编号不存在");
		if(mPutforward.getIstrue()==1)
			return sendFalse("该数据已审核");
		
		if(i==-1){
			mPutforward.setImagepath(str);
			if(Stringutil.isBlank(mPutforward.getImagepath()))
				return sendFalse("拒绝原因不可为空");
			
			Member mMember = mMemberService.getById(mPutforward.getMemberid(),Member.class);
			mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mPutforward.getNum()));
			mMemberService.updateBySelect(mMember);
			
			setRedisMember(mMember, false);
		}else if(i==1){
			mPutforward.setImagepath(MyDate.stampToDate(System.currentTimeMillis())+"提现成功了");
//			if (req instanceof StandardMultipartHttpServletRequest)
//				mPutforward.setImagepath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
//			else
//				return sendFalse("转账截图不可为空");
		}
		mPutforward.setIstrue(i);
		mPutforwardService.updateBySelect(mPutforward);
		
		return sendTrueMsg("审核成功");
		
	}
	/**
	 * 提现列表列表
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping(value ="/Putforward/list", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType Putforwardlist(Putforward mPutforward,String uname,Integer page,Integer rows) throws Exception{
		Map<String, Object> mapobj = new HashMap<String, Object>();
		if(getMember(uname).getSuperadmin()==1){
//			listPutforward= (List<Putforward>) mPutforwardService.getALL(mPutforward, "istrue,id desc ", 0, page, rows);
			
			return sendTrueData(mPutforwardService.getALL(mPutforward, "istrue,id desc ", 0, page, rows));
		}else{
			
			mapobj.put("suma", getMember(uname).getPlatformcurrency());
			Sql msql = new Sql();
			msql.setSql("select nvl(sum(num),0) num from Putforward where istrue=0 and memberid="+getLogin(uname).getUserid());
			mapobj.put("sumb", mPutforwardService.exeSelectSql(msql).get(0).get("NUM"));
			
			msql.setSql("select nvl(sum(num),0) num from Putforward where istrue=1 and memberid="+getLogin(uname).getUserid());
			mapobj.put("sumc", mPutforwardService.exeSelectSql(msql).get(0).get("NUM"));
			
			mPutforward.setMemberid(getLogin(uname).getUserid());
			List<Putforward> listPutforward =(List<Putforward>) mPutforwardService.getALL(mPutforward, "istrue,id desc ", 0, page, rows);
			for (Putforward putforward : listPutforward) {
				putforward.setTime(MyDate.stampToDate(putforward.getId()));
				if(putforward.getIstrue()==1)
					putforward.setType("已完成");
				else if(putforward.getIstrue()==-1)
					putforward.setType("已拒绝");
				else
					putforward.setType("审核中");
			}
			mapobj.put("list",listPutforward);
			return sendTrueData(mapobj);
			
		}
		
		
			
		
	}
	
}
