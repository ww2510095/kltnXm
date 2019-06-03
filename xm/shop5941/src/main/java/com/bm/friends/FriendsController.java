package com.bm.friends;

import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.myjar.Stringutil;

@RestController
@Api(tags = "好友")
public class FriendsController extends BaseController {
	/**
	 * 推荐人移交
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Friends/update", method = RequestMethod.POST)
	public RequestType selectall(String phone1,String phone2) throws Exception {
		if(Stringutil.isBlank(phone1))return sendFalse("移交人不可为空");
		if(Stringutil.isBlank(phone2))return sendFalse("被移交人不可为空");
		Map<String, Object> map =mMemberService.getByparameter("uname", phone1);
		if(map==null) return sendFalse("移交人不存在");
		phone1=map.get("ID").toString();
		map =mMemberService.getByparameter("uname", phone2);
		if(map==null) return sendFalse("被移交人不存在");
		phone2=map.get("ID").toString();
		
		Sql msql = new Sql();
		msql.setSql("update Friends set memberida="+phone2+" where memberida="+phone1);
		mMemberService.execSQL(msql, 0, "");
		return sendTrueMsg("移交成功");
		
	}

}
