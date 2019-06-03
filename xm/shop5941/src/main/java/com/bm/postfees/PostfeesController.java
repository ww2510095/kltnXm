package com.bm.postfees;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;

@RestController
public class PostfeesController extends BaseController{
	
//	@Autowired
//	protected  PostfeesService mPostfeesService;
//	
//	
//	/**
//	 * 添加运费险功能
//	 * */
//	@RequestMapping(value ="/Postfees/add", method = RequestMethod.POST) 
//	@Auth(admin=true)
//	public RequestType add(Postfees mPostfees, HttpServletRequest req,String phone) throws Exception{
//		if(mPostfees.getPostfee()==null)return sendFalse("退费险不可为空");
//		if(mPostfees.getReturnpostfee()==null)return sendFalse("退费不可为空");
//		if(Stringutil.isBlank(phone))return sendFalse("供应商账号不可为空");
//		
//		Shop mshop = getMember(getLogin(phone)).getmShop();
//		if(mshop==null)
//			return sendFalse("用户"+phone+"不是供应商");
//		
//		mPostfees.setOneid(mshop.getId());
//		mPostfeesService.add(mPostfees);
//		return sendTrueMsg("添加成功");
//	}
//	
//		
//	/**
//	 *运费险列表
//	 * */
//	@RequestMapping(value ="/Postfees/select", method = RequestMethod.POST) 
//	@Auth
//	public RequestType select(String uname,Integer page,Integer rows,String phone) throws Exception{
//		Sql msql = new Sql();
//		Shop mshop;
//		if(getMember(getLogin(uname)).getSuperadmin()==1){
//			if(Stringutil.isBlank(phone))
//				msql.setSql("select * from Postfees");
//			else{
//				 mshop = getMember(getLogin(phone)).getmShop();
//				if(mshop==null)
//					return sendFalse("用户"+phone+"不是供应商");
//				msql.setSql("select * from Postfees where oneid="+mshop.getId());
//			}
//		}else{
//			 mshop = getMember(getLogin(uname)).getmShop();
//			if(mshop==null)
//				return sendFalse("抱歉，你不是供应商");
//			msql.setSql("select * from Postfees where oneid="+mshop.getId());
//		}
//				
//		msql.setPage(page);
//		msql.setRows(rows);
//		List<Map<String, Object>> lmap = mSystemMessageService.exeSelectSql(msql);
//		for (Map<String, Object> map : lmap) {
//			mshop  = getMember(Long.valueOf(map.get("ONEID").toString())).getmShop();
//			map.put("ONENAME", mshop.getShopname());
//		}
//		
//		return sendTrueData(lmap);
//	}
//	
	
	
	
	
}
