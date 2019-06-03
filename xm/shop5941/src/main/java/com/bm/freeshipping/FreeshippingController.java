package com.bm.freeshipping;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.auths.MemberAuths;
import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;

@RestController
@Api(tags = "邮费")
public class FreeshippingController extends BaseController{
	@Autowired
	private FreeshippingService mFreeshippingService;
	
	
	/***
	 * 添加运费赔付
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Freeshipping/add", method = RequestMethod.POST)
	public RequestType add(Freeshipping mFreeshipping) throws Exception {
		if(mFreeshipping.getOneid()==null)return sendFalse("供应商账号不可为空"); //供应商id
		if(mFreeshipping.getFreeshippingsize()==null&&mFreeshipping.getFreeshippingnumber()==null)
			return sendFalse("多少件包邮与多少钱包邮最少输入一个"); //多少件包邮
		if(mFreeshipping.getOrders()==null)return sendFalse("邮费不可为空"); //邮费多少	
		if(mFreeshipping.getStr()==null) return sendFalse("生效时间不可为空"); //生效时间
		if(mFreeshipping.getEnd()==null) return sendFalse("结束时间不可为空"); //结束时间
		
		MemberAuths ma = getMember(getLogin(mFreeshipping.getOneid()+""));
		if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
			return sendFalse(mFreeshipping.getOneid()+"不是供应商"); 
		
		Sql msql = new Sql();
		msql.setSql("select id from Freeshipping where oneid="+ma.getmShop().getId()+" and end>"+System.currentTimeMillis());
		
		if(mFreeshippingService.exeSelectSql(msql).size()!=0)
			 return sendFalse("供应商"+mFreeshipping.getOneid()+"已经有包邮规则了，请先废弃");
		
		mFreeshipping.setOneid(ma.getmShop().getId());
		
		mFreeshippingService.add(mFreeshipping);
		
		return sendTrueMsg("添加成功");
		
		
	}
	/***
	 * 查询包邮规则
	 * */
	@Auth
	@RequestMapping(value = "/Freeshipping/select", method = RequestMethod.POST)
	public RequestType select(Freeshipping mFreeshipping,String uname,Integer page,Integer rows) throws Exception {
		if(getMember(getLogin(uname)).getSuperadmin()==1){
			if(mFreeshipping.getOneid()!=null){
				MemberAuths ma = getMember(getLogin(mFreeshipping.getOneid()+""));
				if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
					return sendFalse(mFreeshipping.getOneid()+"不是供应商"); 
				mFreeshipping.setOneid(ma.getmShop().getId());
			} 
		}else{

			MemberAuths ma = getMember(getLogin(uname));
			if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
				return sendFalse(uname+"不是供应商"); 
			mFreeshipping.setOneid(ma.getmShop().getId());
		
		}
		Sql msql = new Sql();
		msql.setSql("select Freeshipping.*,uname phone from Freeshipping left join shop on shop.id=Freeshipping.oneid left join member on member.id=memberid ");
		if(mFreeshipping.getOneid()!=null)
			msql.setSql(msql.getSql()+" where Freeshipping.oneid="+mFreeshipping.getOneid());
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mFreeshippingService.exeSelectSql(msql));
		
	}
	/***
	 * 废弃包邮规则
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Freeshipping/delete", method = RequestMethod.POST)
	public RequestType delete(Long id) throws Exception {
		if(id==null)
			return sendFalse("编号不可为空");
		Freeshipping mExpress=mFreeshippingService.getById(id,Freeshipping.class);
		if(mExpress==null)
			return sendFalse("规则不存在");
		mExpress.setEnd(System.currentTimeMillis());
		mFreeshippingService.updateBySelect(mExpress);
		return sendTrueMsg("废弃成功");
		
	}

	
}
