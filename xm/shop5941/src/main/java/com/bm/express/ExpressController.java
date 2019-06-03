package com.bm.express;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.auths.MemberAuths;
import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.excle.ExportExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.MyDate;
import com.bm.ordersRule.OrdersRule;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "赔付")
public class ExpressController extends BaseController{
	@Autowired
	private ExpressService mExpressService;
	
	
	/***
	 * 添加运费赔付
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Express/add", method = RequestMethod.POST)
	public RequestType add(Express mExpress) throws Exception {
		if(mExpress.getOneid()==null) return sendFalse("供应商账号不可为空"); //供应商id
		if(mExpress.getMaxindemnity()==null) return sendFalse("最高赔付不可为空"); //最高赔付
		if(mExpress.getMaxindemnitya()==null) return sendFalse("单笔最高不可为空"); //单笔最高赔付
		if(mExpress.getMinindemnitya()==null) return sendFalse("单笔最低不可为空"); //单笔最低赔付
		if(mExpress.getPayment()==null) return sendFalse("单笔价格不可为空"); //单笔价格
		if(mExpress.getStr()==null) return sendFalse("生效时间不可为空"); //生效时间
		if(mExpress.getEnd()==null) return sendFalse("结束时间不可为空"); //结束时间
		
		MemberAuths ma = getMember(getLogin(mExpress.getOneid()+""));
		if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
			return sendFalse(mExpress.getOneid()+"不是供应商"); 
		
		
		Sql msql = new Sql();
		msql.setSql("select id from Express where oneid="+ma.getmShop().getId()+" and end>"+System.currentTimeMillis());
		
		if(mExpressService.exeSelectSql(msql).size()!=0)
			 return sendFalse("供应商"+mExpress.getOneid()+"已经有运费险了，请先废弃");
		
		mExpress.setOneid(ma.getmShop().getId());
		
		mExpressService.add(mExpress);
		
		return sendTrueMsg("添加成功");
		
		
	}
	/***
	 * 查询赔付
	 * */
	@Auth(Express=true)
	@RequestMapping(value = "/Express/select", method = RequestMethod.POST)
	public RequestType select(Express mExpress,String uname,Integer page,Integer rows) throws Exception {
		if(getMember(getLogin(uname)).getSuperadmin()==1){
			if(mExpress.getOneid()!=null){
				MemberAuths ma = getMember(getLogin(mExpress.getOneid()+""));
				if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
					return sendFalse(mExpress.getOneid()+"不是供应商"); 
				mExpress.setOneid(ma.getmShop().getId());
			} 
		}else{

			MemberAuths ma = getMember(getLogin(uname));
			if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
				return sendFalse(uname+"不是供应商"); 
			mExpress.setOneid(ma.getmShop().getId());
		
		}
		Sql msql = new Sql();
		msql.setSql("select Express.*,uname phone from Express left join shop on shop.id=Express.oneid left join member on member.id=memberid ");
		if(mExpress.getOneid()!=null)
			msql.setSql(msql.getSql()+" where Express.oneid="+mExpress.getOneid());
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mExpressService.exeSelectSql(msql));
//		return sendTrueData(selectData(mExpress.getOneid(), star, end, page, rows));
		
	}
	/***
	 * 废弃赔付
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Express/delete", method = RequestMethod.POST)
	public RequestType delete(Long id) throws Exception {
		if(id==null)
			return sendFalse("编号不可为空");
		Express mExpress=mExpressService.getById(id,Express.class);
		if(mExpress==null)
			return sendFalse("赔付不存在");
		mExpress.setEnd(System.currentTimeMillis());
		mExpressService.updateBySelect(mExpress);
		return sendTrueMsg("废弃成功");
		
	}
	/**
	 * 赔付明细
	 * */
	@Auth(Express=true)
	@RequestMapping(value = "/SystemExpress/select", method = RequestMethod.POST)
	public RequestType select(String onephone,String uname,Long star,Long end,Integer page,Integer rows) throws Exception {
		return sendTrueData(selectData(onephone,   star, end, page==null?1:page, rows,uname));
	}
	/**
	 * 导出赔付明细
	 * */
	@Auth(Express=true)
	@RequestMapping(value = "/SystemExpress/exportselect", method = RequestMethod.GET)
	public void exportselect(String onephone,HttpServletResponse response,String uname,Long star,Long end,Integer page,Integer rows) throws Exception {
		List<Map<String, Object>> listmap = selectData(onephone,   star, end,page, rows==null?1000:rows,uname);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("赔付运单号");   	key.add("id");
		title.add("赔付时间");  	key.add("time");
		title.add("赔付金额");		key.add("payment");
		title.add("供应商账号");	key.add("uname");
		title.add("供应商名字");	key.add("shopname");
		ExportExcel.Export("赔付报表",title,key,listmap,response);
	
		
	}
	
	/**
	 * 赔付数据
	 * */
	private List<Map<String, Object>> selectData(String onephone,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		Long oneid = null;
		if(getMember(getLogin(uname)).getSuperadmin()==1){
			if(!Stringutil.isBlank(onephone)){
				MemberAuths ma = getMember(getLogin(onephone));
				if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
					throw new RunException(onephone+"不是供应商");
				oneid = ma.getmShop().getId();
			} 
		}else{
			MemberAuths ma = getMember(getLogin(uname));
			if(ma.getmShop()==null||ma.getmShop().getSuperid()==null||!(ma.getmShop().getSuperid()+"").equals("0"))
				throw new RunException(uname+"不是供应商");
			oneid = ma.getmShop().getId();
		
		}
		
		
		Sql msql = new Sql();
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		msql.setPage(page);
		msql.setRows(rows);
		//基础sql1
		String sql  = "select '合计' id, ''time,sum(payment)payment ,''uname ,'' shopname "
				+ "from SystemExpress  where SystemExpress.time<"+end+" and SystemExpress.time > "+star
				+" union all select to_char(SystemExpress.id),"+MyDate.orcaleCDATE("SystemExpress.time")+","
				+ "SystemExpress.Payment,uname,shopname from SystemExpress left join shop on shop.id= SystemExpress.oneid "
				+ "left join member on member.id=shop.memberid  where SystemExpress.time<"+end+" and SystemExpress.time > "+star;
				
			if(oneid!=null)
				sql = sql+" and SystemExpress.oneid ="+oneid;
			
			msql.setSql(sql);
			
		return mExpressService.exeSelectSql(msql);
	}
	/**
	 * 运费明细
	 * */
	@Auth
	@RequestMapping(value = "/SystemExpress/selectorders", method = RequestMethod.POST)
	public RequestType selectorders(Integer i,String phone,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		return sendTrueData(selectordersData(i, phone, star, end, page==null?1:page, rows, uname));
	}
	/**
	 * 导出运费明细
	 * */
	@Auth
	@RequestMapping(value = "/SystemExpress/exportselectorders", method = RequestMethod.GET)
	public void exportselectorders(HttpServletResponse response,Integer i,String phone,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		List<Map<String, Object>> listmap = selectordersData(i, phone, star, end, page, rows==null?1000:rows, uname);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("订单时间");   		key.add("id");
		title.add("订单编号");  		key.add("Ordersid");
		title.add("供应商账号");		key.add("oneuname");
		title.add("付费人");			key.add("Indemnity");
		title.add("订单绑定店铺账号");	key.add("shopuname");
		title.add("订单绑定店员账号");	key.add("clerkuname");
		title.add("金额");			key.add("Postfee");
		title.add("供应商承担费用");		key.add("onenumber");
		title.add("店铺承担费用");		key.add("tonumber");
		title.add("推荐人承担费用");		key.add("clerknumber");
		ExportExcel.Export("赔付报表",title,key,listmap,response);
	
		
	}
	/**
	 * 运费数据
	 * */
	private List<Map<String, Object>> selectordersData(Integer i,String phone,Long star,Long end,Integer page,Integer rows,String uname) throws Exception {
		Long shopid = null;
		Sql msql = new Sql();
		if(i!=null&&i==1){//商户
			if(getMember(getLogin(uname)).getSuperadmin()==1){
				if(!Stringutil.isBlank(phone)){
					MemberAuths ma = getMember(getLogin(phone));
					if(ma.getmShop()==null||ma.getmShop().getSuperid()==null)
						throw new RunException(phone+"不是店铺");
					shopid = ma.getmShop().getId();
				} 
			}else{
				MemberAuths ma = getMember(getLogin(uname));
				if(ma.getmShop()==null||ma.getmShop().getSuperid()==null)
					throw new RunException(uname+"不是店铺");
				shopid = ma.getmShop().getId();
				 
			}
		}else{//店员
			if(getMember(getLogin(uname)).getSuperadmin()==1){
				if(!Stringutil.isBlank(phone))
					shopid =getLogin(phone).getUserid();
				}else{
					shopid =getLogin(uname).getUserid();
				}
				
			
		}
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		msql.setPage(page);
		msql.setRows(rows);
		
		msql.setSql("select oneid from Postfees where Postfees.id<"+end+" and Postfees.id > "+star +" group by oneid ");
		List<Map<String, Object>> listmap =mExpressService.exeSelectSql(msql);
		ArrayList<String> als = new ArrayList<String>();
		for (Map<String, Object> map : listmap) {
			als.add(map.get("ONEID").toString());
		}
		
	
		//基础sql1
		String sql1  ="select '合计' id,                   "
						+"       '' Ordersid,                 "
						+"       '' oneuname,                 "
						+"       nvl(sum(Postfee), 0) Postfee,"
						+"       '' Indemnity,                "
						+"       '' shopuname,                "
						+"       '' clerkuname,				  "
						+" 		 ''oneid                	  "
						+"         from Postfees     where 1=1         ";
		String sql2=	" union all                           "
						+"select "+MyDate.orcaleCDATE("Postfees.id")+","
						+"       to_char(Postfees.Ordersid),  "
						+"       m1.uname oneuname,           "
						+"       Postfees.Postfee,            "
						+" decode(Postfees.Indemnity,0,'供应商',1,'店铺与店员'), "
						+"       m2.uname,                    "
						+"       m3.uname,"
						+" to_char(Postfees.Oneid)            "
						+"  from Postfees                     "
						+"  left join shop a1                 "
						+"    on a1.id = Postfees.oneid       "
						+"  left join member m1               "
						+"    on m1.id = a1.memberid          "
						+"  left join shop a2                 "
						+"    on a2.id = Postfees.shopid      "
						+"  left join member m2               "
						+"    on m2.id = a2.memberid          "
						+"  left join member m3               "
						+"    on m3.id = Postfees.clerk       "
						+ "where Postfees.id<"+end+" and Postfees.id > "+star;
		
		if(shopid!=null){
			if(i!=null&&i==1){
				sql1 = sql1+" and Postfees.oneid ="+shopid;
				sql2 = sql2+" and Postfees.oneid ="+shopid;
			}else{
				sql1 = sql1+" and Postfees.clerk ="+shopid;
				sql2 = sql2+" and Postfees.clerk ="+shopid;
			}
				
		}
			
		
		msql.setSql(sql1+sql2);
		listmap =mExpressService.exeSelectSql(msql);
		Set<OrdersRule> HashSet = new HashSet<OrdersRule>();
		for (String str : als) {
			msql.setSql("select * from OrdersRule where shoponeid="+str);
			HashSet.addAll(IBeanUtil.ListMap2ListJavaBean(mExpressService.exeSelectSql(msql), OrdersRule.class));
		}
		for (OrdersRule ordersRule : HashSet) {
			for (Map<String, Object> map : listmap) {
				if(map.get("ONEID")!=null){
					if((ordersRule.getShoponeid()+"").equals(map.get("ONEID")+"")){
						if("供应商".equals(map.get("INDEMNITY")+"")){
							map.put("ONENUMBER", map.get("POSTFEE"));
							map.put("TONUMBER", 0);
							map.put("CLERKNUMBER", 0);
						}else{
							map.put("ONENUMBER", 0);
							map.put("TONUMBER", ordersRule.getShopto().
									divide(ordersRule.getShopto().add(ordersRule.getClerk())).multiply(new BigDecimal(map.get("POSTFEE")+"")));
							map.put("CLERKNUMBER",ordersRule.getClerk().
									divide(ordersRule.getShopto().add(ordersRule.getClerk())).multiply(new BigDecimal(map.get("POSTFEE")+"")));
						}
						continue;
						
					}
				}
//				else{
//					map.put("ONENUMBER","供应商承担费用");
//					map.put("TONUMBER", "店铺承担费用");
//					map.put("CLERKNUMBER", "推荐人承担费用");
//				}
			}
		}
	
		
		return listmap;
	}

	
}
