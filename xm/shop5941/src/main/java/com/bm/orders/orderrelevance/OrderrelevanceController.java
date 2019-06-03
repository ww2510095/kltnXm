package com.bm.orders.orderrelevance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import com.bm.base.util.MyDate;
import com.bm.orders.OrdersController;
import com.bm.shop.Shop;
import com.bm.shop.ShopService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;



@RestController
@Api(tags = "销售商品")
public class OrderrelevanceController extends BaseController{
	@Autowired
	private OrderrelevanceService mOrderrelevanceService;
	@Autowired
	private ShopService mShopService;
	

	
	
	@RequestMapping(value = "/Orderrelevance/selectbyorderid", method = RequestMethod.POST)
	public RequestType selectbyorderid(Long orderid,HttpServletRequest req) throws Exception {
		if(orderid==null) return sendFalse("订单id不可为空");
		Orderrelevance mOrderrelevance = new Orderrelevance();
		mOrderrelevance.setOrderid(orderid);
		return sendTrueData(mOrderrelevanceService.getALL(mOrderrelevance, 1, 1000));
	}
	/**
	 * 销售查询
	 * @param star 开始时间，
	 * @param end 结束时间
	 * 
	 * */
	@Auth(Orderrelevance=true)
	@RequestMapping(value = "/Orderrelevance/select", method = RequestMethod.POST)
	public RequestType select(String code,String uname,Long star,Long end,Integer page,Integer rows,HttpServletResponse response) throws Exception {
//		public RequestType select(Orderrelevance mOrderrelevance,HttpServletRequest req,String uname,Long star,Long end,Integer page,Integer rows) throws Exception {
		return sendTrueData(getOrderrelevanceData(uname,code, star, end, page, rows));
	}
	@Auth(Orderrelevance=true)
	@RequestMapping(value = "/Orderrelevance/exportOrderrelevance", method = RequestMethod.GET)
	public void exportOrderrelevance(String code,String uname,Long star,Long end,Integer rows,HttpServletResponse response) throws Exception {
		List<Map<String, Object>> listmap = getOrderrelevanceData(uname,code, star, end, 1, rows);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
//		title.add("条码");   		key.add("youcode");
//		title.add("供应商");  	key.add("supplier");
//		title.add("商品名字");		key.add("name");
//		title.add("品牌");		key.add("brand");
//		title.add("销售数量");		key.add("num");
//		title.add("原价");		key.add("originalprice");
//		title.add("售价");		key.add("price");
//		title.add("颜色");		key.add("colour");
//		title.add("尺码");		key.add("mysize");
//		title.add("成本价");		key.add("costprice");
//		title.add("大类");		key.add("largeclass");
//		title.add("中类");		key.add("inclass");
//		title.add("小类");		key.add("smallclass");
//		title.add("细类");		key.add("fineclass");
//		title.add("促销编号");		key.add("promotionid");
//		title.add("促销标签");		key.add("promotiontitle");
		title.add("时间");   			key.add("time");
		title.add("订单编号");  		key.add("id");
		title.add("条码");			key.add("youcode");
		title.add("数量");			key.add("num");
		title.add("单价");			key.add("price");
		title.add("总价");			key.add("totalfee");
		title.add("收货地址");			key.add("detailed");
		title.add("联系人电话");		key.add("phone");
		title.add("联系人名字/门店名字");	key.add("name");
		title.add("订单状态");			key.add("ordertype");
		ExportExcel.Export("销售报表",title,key,listmap,response);
		
	}
	
	
	//日期，条码，数量，金额，地址，电话，联系人，订单编号
	
	private List<Map<String, Object>> getOrderrelevanceData(String uname,String code,Long star,Long end,Integer page,Integer rows) throws Exception {
		
		Sql msql = new Sql();
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		msql.setPage(page);
		msql.setRows(rows);
		
		String sql = "select "+MyDate.orcaleCDATE("orders.id","time")+","+OrdersController.getordertype("orders.status","ordertype")+",orders.id,youcode,num,Orderrelevance.price,totalfee,"
				+ "nvl(Receiver.detailed,shop.detailed)detailed,nvl(Receiver.phone,shopphone)phone,"
				+ "nvl(name,shop.shopname)name from Orderrelevance left join orders on orderid=orders.id left join Receiver on "
				+ "Receiver.orderid=Orderrelevance.Orderid left join shop on shop.id=orders.shopid where orders.id>"+star+" and orders.id<"+end;
		
		//用户资料
		MemberAuths mMember = getMember(getLogin(uname).getUserid());
		if(mMember.getSuperadmin()==1){
			//超级管理员
			if(!Stringutil.isBlank(code)){
				Shop mshop = mShopService.getByparameter("code", code,Shop.class);
				if(mshop==null) throw new RunException("店铺编号不存在");
				if(mshop.getSuperid()==0)
					sql = sql+" and orders.onephone='"+getMember(mshop.getMemberid()).getUname()+"'";
				else
					sql = sql+" and shop.code='"+mshop.getCode()+"'";
			}
				
			
		}else{
			Shop mshop =getShop(uname);
			if(mshop==null) throw new RunException("暂无数据");
			if(mshop.getSuperid()==0)
				sql = sql+" and orders.onephone='"+getMember(mshop.getMemberid()).getUname()+"'";
			else
				sql = sql+" and shop.code='"+mshop.getCode()+"'";
		}
		msql.setSql("select * from ("+sql+") order by id desc ");
		return mOrderrelevanceService.exeSelectSql(msql);

	}
	
//	private List<Map<String, Object>> selectData(Orderrelevance mOrderrelevance,HttpServletRequest req,String uname,Long star,Long end,Integer page,Integer rows) throws Exception {
//		Sql msql = new Sql();
//		star = star ==null?0:star;
//		end = end ==null?System.currentTimeMillis():end;
//		msql.setPage(page);
//		msql.setRows(rows);
//		//基础sql1
//		String sql1  = "select largeclass,inclass,smallclass,fineclass,name,supplier,brand,originalprice,Orderrelevance.price,costprice,colour,mysize, "
//				+ "Commodity.youcode, title, sum(num) num,promotionid,promotiontitle  from (select * from Orderrelevance "+
//				mOrderrelevanceService.getWhere(mOrderrelevance)+ ")Orderrelevance left join Commodity on Commodity.id="
//				+ "Orderrelevance.Itemid left join (select * from orders where id<"+end+" and id > "+star;
//				
//		//基础sql2
//		String sql2  =")orders on  orders.ordernumber=ordernum where orders.status=4 group by   Commodity.youcode, largeclass,inclass,smallclass,fineclass,"
//				+ "name,supplier,brand,originalprice,Orderrelevance.price,costprice,colour，mysize，title,promotionid,promotiontitle";
//		//用户资料
//		Member mMember = getMember(getLogin(uname).getUserid());
//		if(mMember.getSuperadmin()==1){
//			//超级管理员
//			msql.setSql(sql1+sql2);
//		}else{
//			Shop mshop = IBeanUtil.Map2JavaBean(mShopService.getByparameter("memberid", mMember.getId()+""), Shop.class);
//			if(mshop==null) throw new RunException("抱歉，权限不足");
//			if(mshop.getSuperid()==0){
//				//顶级节点
//				msql.setSql("select * from ("+sql1+sql2+") where supplier="+uname);
//			}else{
//				//其他节点
//				msql.setSql(sql1+" and shopid="+mshop.getId()+" "+sql2);
//			}
//			
//		}
//		return mOrderrelevanceService.exeSelectSql(msql);
//	}
	/**
	 * type:1:查用户，2查店铺
	 * phone,d店铺或用户账号
	 * */
	@Auth
	@RequestMapping(value = "/Orderrelevance/selectByshopORmember", method = RequestMethod.POST)
	public RequestType selectByshopORmember(String phone,Integer rows,Integer page,Long star,Long end,String uname,Integer type) throws Exception {
		if(Stringutil.isBlank(phone))
			return sendFalse("查询账号不可为空");
		type=type==null?1:type;
		MemberAuths ma = getMember(getLogin(uname));
		Sql msql = new Sql();
		if(ma.getSuperadmin()!=1){
			if(!phone.equals(uname)){
				//查询非自己的数据先检查权限
				if(ma.getmShop().getSuperid()==0){
					//供应商
					if(type==1){
						msql.setSql("select clerk.memberid from clerk where shopid in(select id from shop where oneid = "+ma.getmShop().getId()+") and clerk.memberid="+getLogin(phone).getUserid());
					}else{
						msql.setSql("select id from shop where oneid ="+ma.getmShop().getId()+" and id="+ getMember(getLogin(phone)).getmShop().getId());
					}
				}else{
					//普通店铺
					if(type==1){
						msql.setSql("select clerk.memberid from clerk where shopid  = "+ma.getmShop().getId()+" and clerk.memberid="+getLogin(phone).getUserid());
					}
					return sendFalse("抱歉，你不能查询店铺"+phone+"的销售订单");
				}
				if(mOrderrelevanceService.exeSelectSql(msql).size()==0)
					return sendFalse("抱歉，你不能查询"+(type==1?"用户":"店铺")+phone+"的销售订单");
			}
		}
		//查询销售的订单
		star = star ==null?0:star;
		end = end ==null?System.currentTimeMillis():end;
		msql.setPage(page);
		msql.setRows(rows);
		if(type==1){
			msql.setSql("select orders.* from orders left join Friends on memberidb=memberid where memberida ="+getLogin(phone).getUserid()+" and  orders.id<"+end+" and orders.id > "+star);
		}else{
			msql.setSql("select * from orders where shopid ="+getMember(getLogin(phone)).getmShop().getId()+" and  id<"+end+" and id > "+star);
		}
		
		return sendTrueData(mOrderrelevanceService.exeSelectSql(msql));
	}
	

}
