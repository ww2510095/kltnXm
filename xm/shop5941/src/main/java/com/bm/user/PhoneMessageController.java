package com.bm.user;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.request.RequestType;
import com.bm.base.util.HttpRequest;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "短信模块")
public class PhoneMessageController extends BaseController {
	
	@Autowired
    private PhoneMessageService mPhoneMessageService;
	
	/**
	 * 发送验证码
	 * */
    @RequestMapping(value="/phonemessage/select" ,method=RequestMethod.GET)
    public RequestType select() throws Exception{
    	return sendTrueData(mPhoneMessageService.getALL(new PhoneMessage(), "id",1));
    	
    }
	
	/**
	 * 发送验证码
	 * */
    @RequestMapping(value="/phonemessage/send" ,method=RequestMethod.POST)
    public RequestType add(String phone,Integer type) throws Exception{
    	
    	if(Stringutil.isBlank(phone)||phone.length()<11)return sendFalse("请填写正确的手机号！");
    	if(type==null)return sendFalse("类型可为空！");
    	
    	PhoneMessage mphonemessage = new PhoneMessage();
    	mphonemessage.setPhone(phone);
    	mphonemessage.setType(type);
    	int code = new Random().nextInt(9999-1000+1)+1000;
    	
    	
    	
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put("apikey",MyParameter.MESSAGE_APIKEY);
		map.put("mobile", phone);
		
    	switch (type) {
		case 0://注册
			if(mMemberService.getByparameter("uname", phone)!=null) 
				throw new RunException("用户已注册");
			break;
		case 1://找回密码
		case 2://修改绑定手机号
		case 4://申请绑定支付宝
			if(mMemberService.getByparameter("uname", phone)==null) 
				throw new RunException("用户未注册");
			
			break;
		default:
			return sendFalse("错误！！");
		}
    	
    	/*************************************发送短信*************************************************/
		phone ="【5941商城】"+"您的验证码是"+code+"。如非本人操作，请忽略本短信";
		map.put("text", phone);
		
		if(!MyParameter.Home_name.equals( InetAddress.getLocalHost().getHostName())){
		
    	@SuppressWarnings("unchecked")
		List<PhoneMessage> lmap =(List<PhoneMessage>) mPhoneMessageService.getALL(mphonemessage);
    	if(lmap.size()!=0){
    		if(System.currentTimeMillis()-lmap.get(0).getTime()<3*60*1000)
    			return sendFalse("您的操作太频繁了，请稍后再试！");
    	
    	if(type==MyParameter.phone_message_type4){
    		if(System.currentTimeMillis()-lmap.get(0).getTime()<(60L*60L*1000L*24L))
    			return sendFalse("该短信24小时之内只能发送一条,请稍后再试");
    		
    		Sql msql = new Sql();
    		msql.setSql("select id from zfb where phone='"+phone+"' and istrue=0");
    		if(mPhoneMessageService.exeSelectSql(msql).size()!=0)
    			return sendFalse("你已经申请过了，后台正在审核");
    		}
    			
    	}
	}
    	mphonemessage.setStatus(0);
    	String s =HttpRequest.sendPost(MyParameter.MESSAGE_URL, map);
    	s=s.substring(s.indexOf("code")+6);
    	s=s.substring(0,s.indexOf(","));
    	if("0".equals(s)){
    		mphonemessage.setCode(code);
        	mphonemessage.setTime(System.currentTimeMillis());
        	mphonemessage.setMsg(phone);
    		mPhoneMessageService.add(mphonemessage);
    		return sendTrueMsg("发送成功");
    	} else{
    		if(MyParameter.Home_name.equals( InetAddress.getLocalHost().getHostName())){
    			mphonemessage.setCode(code);
            	mphonemessage.setTime(System.currentTimeMillis());
            	mphonemessage.setMsg(phone);
        		mPhoneMessageService.add(mphonemessage);
        		return sendTrueMsg("测试短信发送成功");
    		}
    	}
    		
		return sendFalse("验证码发送失败");
    	
    
    }
   
}
