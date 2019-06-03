package com.bm.webapp;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.user.Member;

@Controller
@Api(tags = "版本相关")
public class H5 extends BaseController{
	
	/**
	 * 通过扫码跳转H5注册页面
	 * @throws Exception 
	 * */
	@RequestMapping(value ="/register/{id}", method = {RequestMethod.POST,RequestMethod.GET}) 
	public String register(@PathVariable Long id,HttpServletRequest request) throws Exception {
		Member mMember=null;
		try {
			mMember  =	getMember(id);
		} catch (Exception e) {
		}
		
		if(mMember==null){
			mMember=getMember(MyParameter.memberaddrecommend);
		}
		id=mMember.getId();
		Sql msql = new Sql();
		msql.setSql("select id from clerk where memberid="+id);
		if(mMemberService.exeSelectSql(msql).size()==0){
			msql.setSql("select memberida from Friends where memberidb="+id);
			mMember=getMember(Long.valueOf(mMemberService.exeSelectSql(msql).get(0).get("MEMBERIDA").toString()));
		}
	
		request.setAttribute("mphne", mMember.getUname());
		request.setAttribute("appurl", "http://www.bming.net/5941.apk");
		return "register";

	}
	/**
	 * 通过扫码跳转H5注册页面
	 * @throws Exception 
	 * */
	@RequestMapping(value ="/download/downloadApp", method = {RequestMethod.POST,RequestMethod.GET}) 
	public String register(HttpServletRequest request) throws Exception {
		return "downloadApp";
		
	}
	
}
