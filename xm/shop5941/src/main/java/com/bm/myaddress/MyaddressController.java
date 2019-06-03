package com.bm.myaddress;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.myjar.Stringutil;

@RestController
@Api(tags = "收货地址")
public class MyaddressController extends BaseController {

	@Autowired
	private MyaddressService mMyaddressService;

	/**
	 * 添加收货地址
	 */
	@Auth
	@Transactional
	@RequestMapping(value = "/myaddress/add", method = RequestMethod.POST)
	public RequestType shopsave(Myaddress mMyaddress,String uname ) throws Exception {
		if(Stringutil.isBlank(mMyaddress.getProvince()))return sendFalse("省不可为空");
		if(Stringutil.isBlank(mMyaddress.getCity()))return sendFalse("市不可为空");
		if(Stringutil.isBlank(mMyaddress.getArea()))return sendFalse("区不可为空");
//		if(Stringutil.isBlank(mMyaddress.getStreet()))return sendFalse("街道不可为空");
		if(Stringutil.isBlank(mMyaddress.getDetailed()))return sendFalse("详细地址不可为空");
		if(Stringutil.isBlank(mMyaddress.getName()))return sendFalse("收货人姓名不可为空");
		if(Stringutil.isBlank(mMyaddress.getPhone()))return sendFalse("收货人电话不可为空");
		if(Stringutil.isBlank(mMyaddress.getCode()))return sendFalse("邮编不可为空");
//		if(mMyaddress.getLongitude()==null||mMyaddress.getLatitude()==null)return sendFalse("地理位置获取失败");
		
		Myaddress mMyaddress1 = new Myaddress();
		mMyaddress1.setMemberid(getLogin(uname).getUserid());
		List<?> lobj = mMyaddressService.getALL(mMyaddress1);
		if(lobj.size()==10)return sendFalse("收货地址最多10个");
		if(lobj.size()==0)
			mMyaddress.setIstrue(1);
		
		mMyaddress.setMemberid(getLogin(uname).getUserid());
		Long id =System.currentTimeMillis();
		mMyaddress.setId(id);
		mMyaddressService.add(mMyaddress);
		
		if(mMyaddress.getIstrue()!=null&&mMyaddress.getIstrue()==1)
			updatedefault(id+"", uname);
		
		return sendTrueMsg("添加成功");

	}
	/**
	 * 查询我的收货地址
	 * */
	@Auth
	@RequestMapping(value = "/myaddress/selectall", method = RequestMethod.POST)
	public RequestType selectall(String uname,Long id) throws Exception {
		Myaddress mMyaddress1 = new Myaddress();
		mMyaddress1.setMemberid(getLogin(uname).getUserid());
		mMyaddress1.setId(id);
		return sendTrueData(mMyaddressService.getALL(mMyaddress1));
		
	}
	/**
	 * 查询我的默认收货地址
	 * */
	@Auth
	@RequestMapping(value = "/myaddress/selectdefault", method = RequestMethod.POST)
	public RequestType selectdefault(String uname) throws Exception {
		Myaddress mMyaddress1 = new Myaddress();
		mMyaddress1.setMemberid(getLogin(uname).getUserid());
		mMyaddress1.setIstrue(1);
		return sendTrueData(mMyaddressService.getALL(mMyaddress1));
		
	}
	
	/**
	 * 修改收货地址
	 */
	@Auth
	@RequestMapping(value = "/myaddress/update", method = RequestMethod.POST)
	public RequestType addto(Myaddress mMyaddress,String uname) throws Exception {
		if(mMyaddress.getId()==null) return sendFalse("收货地址编号不可为空！");
		mMyaddressService.updateBySelect(mMyaddress);
		
		if(mMyaddress.getIstrue()!=null&&mMyaddress.getIstrue()==1)
			updatedefault(mMyaddress.getId()+"", uname);
		
		return sendTrueMsg("更新成功");

	}

	/**
	 * 删除收货地址
	 */
	@Auth
	@RequestMapping(value = "/myaddress/delete", method = RequestMethod.POST)
	public RequestType delete(String id) throws Exception {
		mMyaddressService.deleteByid(id);
		return sendTrueMsg("删除成功");
	}

	/**
	 * 更改地址为默认地址
	 */
	@Auth
	@Transactional
	@RequestMapping(value = "/myaddress/updatedefault", method = RequestMethod.POST)
	public RequestType updatedefault(String id,String uname) throws Exception {
		Sql msql =new Sql();
		msql.setSql("update myaddress set istrue=0 where memberid='"+getLogin(uname).getUserid()+"'");
		mMyaddressService.execSQL(msql,-1,"");
		msql.setSql("update myaddress set istrue=1 where memberid='"+getLogin(uname).getUserid()+"' and id='"+id+"'");
		mMyaddressService.execSQL(msql,0,id);
		return sendTrueMsg("设置成功");
	}

}
