package com.bm;

import com.Shop5941Application;
import com.bm.Aenum.Orders_myp;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.Emil;
import com.bm.base.util.IBeanUtil;
import com.bm.clerk.commission.Commission;
import com.bm.clerk.commission.CommissionService;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.orders.orders.Orders;
import com.bm.orders.orders.OrdersService;
import com.bm.ordersRule.sharingdetails.SharingdetailsService2;
import com.bm.task.TSystem;
import com.bm.user.Member;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 任务系统
 * */
@Component
@RestController
@Api(tags = "任务")
public class Task extends BaseController{
	
	 @Autowired
	 private OrdersService mOrdersService;
	 @Autowired
	 private SharingdetailsService2 mSharingdetailsService2;
	 @Autowired
	 private CommissionService mCommissionService;
	 
		/**
		 * 更新排序
		 * */
		@RequestMapping(value = "/Task/updatepx", method = RequestMethod.GET)
		@Transactional
		public RequestType updatepx() throws Exception {
			Sql msql = new Sql();
			msql.setSql("select id,rownum a from SPECIFICATIONS order by id desc ");
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				msql.setSql("update SPECIFICATIONS set vip="+map.get("A").toString()+" where id="+map.get("ID").toString());
				mCommissionService.execSQL(msql);
			}
			msql.setSql("select Commoditykeyid from SPECIFICATIONS where Commoditykeyid is not null group by Commoditykeyid ");
			listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				msql.setSql("update SPECIFICATIONS set vip=(select max(vip) from SPECIFICATIONS where Commoditykeyid="+map.get("COMMODITYKEYID").toString()+") where Commoditykeyid="+map.get("COMMODITYKEYID").toString());
				mCommissionService.execSQL(msql);
			}
			return sendTrueMsg("架构完成");
		}
		/**
		 * 将10月1日以后的结算改为新逻辑
		 * */
		@RequestMapping(value = "/Task/mCommissionServiceA", method = RequestMethod.GET)
		@Transactional
		public RequestType mCommissionServiceA() throws Exception {
			Sql msql = new Sql();
			msql.setSql("select id from orders  where id>1538323200000 and status in("+getOrdersStatusTrue_a()+") and id not in(select ordersid from Commission)");
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				CommissionTask.start(mCommissionService, map.get("ID").toString(), CommissionTask.COMMISSION_1);
			}
			
			return sendTrueMsg("架构完成");
		}
		/**
		 * 将10月1日以后的结算改为新逻辑
		 * */
		@RequestMapping(value = "/Task/mCommissionServiceB", method = RequestMethod.GET)
		@Transactional
		public RequestType mCommissionServiceB() throws Exception {
			Sql msql = new Sql();
			msql.setSql("select ordersid from (select ordersid from Sharingdetails  "
					+ "where ordersid not in (select ordersid from Sharingdetails where shop<0 ) and ordersid>1538323200000"
					+ "  union all select ordersid from Sharingdetails2  "
					+ "where ordersid not in (select ordersid from Sharingdetails2 where shop<0)) "
					+ " where ordersid not in(select ordernumber from returnorders)");
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				CommissionTask.start(mCommissionService, map.get("ORDERSID").toString(), CommissionTask.COMMISSION_2);
			}
			
			return sendTrueMsg("架构完成");
		}
		/**
		 * 将10月1日以后的结算改为新逻辑
		 * */
		@RequestMapping(value = "/Task/mCommissionServiceC", method = RequestMethod.GET)
		@Transactional
		public RequestType mCommissionServiceC() throws Exception {
			Sql msql = new Sql();
			msql.setSql("select ordersid from (select ordersid from Sharingdetails  "
					+ "where ordersid not in (select ordersid from Sharingdetails where shop<0 ) and ordersid>1538323200000 "
					+ " and state=1 "
					+ "  union all select ordersid from Sharingdetails2  "
					+ "where ordersid not in (select ordersid from Sharingdetails2 where shop<0)) "
					+ " where ordersid not in(select ordernumber from returnorders)");
			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
//				CommissionTask.COMMISSIONTASK_TIME3=true;
				CommissionTask.start(mCommissionService, map.get("ORDERSID").toString(), CommissionTask.COMMISSION_3);
			}
			return sendTrueMsg("架构完成");
		}
//		/**
//		 * 将10月1日以后的结算改为新逻辑
//		 * */
//		@RequestMapping(value = "/Task/mCommissionServiceD", method = RequestMethod.GET)
//		@Transactional
//		public RequestType mCommissionServiceD() throws Exception {
//			Sql msql = new Sql();
//			msql.setSql("select * from member where platformcurrency>0");
//			List<Member> listmap = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Member.class);
//			msql.setSql("select * from Putforward");
//			List<Putforward> listmap1 = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Putforward.class);
//			for (Member mMember : listmap) {
//				for (Putforward putforward : listmap1) {
//					if(putforward.getMemberid().toString().equals(mMember.getId().toString())){
//						mMember.setPlatformcurrency(mMember.getPlatformcurrency().subtract(putforward.getNum()));
//					}
//				}
//			}
//			for (Member mMember : listmap) {
//				mMemberService.updateBySelect(mMember);
//				setRedisMember(mMember, false);
//			}
//			return sendTrueMsg("架构完成");
//		}
		/**
		 * 将10月1日以后的结算改为新逻辑
		 * */
		@RequestMapping(value = "/Task/mCommissionServiceD", method = RequestMethod.GET)
		@Transactional
		public RequestType mCommissionServiceE() throws Exception {
			Sql msql = new Sql();
			msql.setSql("select * from orders where status=11");
			List<Orders> listmap = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Orders.class);
			for (Orders mOrders : listmap) {
				mOrders.setAutosystem(1);
				mOrdersService.updateBySelect(mOrders);
			}
			return sendTrueMsg("架构完成");
		}
		/**
		 * 管理员手动结账
		 * */
		@RequestMapping(value = "/Task/adminSharingdetails", method = RequestMethod.POST)
		@Auth(admin=true)
		@Transactional
		public RequestType adminSharingdetails() throws Exception {
			Auto();
			return sendTrueMsg("结账完成");
		}
		/**
		 * 管理员手动结账
		 * */
		@RequestMapping(value = "/Task/adminAutoMoney", method = RequestMethod.POST)
		@Auth(admin=true)
		@Transactional
		public RequestType adminAutoMoney() throws Exception {
			
			
//			AutoMoney();//门店贡献打款
//			AutoMoney2();//线上店主与经销商贡献打款
//			
//			Sql msql = new Sql();
//			
//			msql.setSql("update Commission set time3="+System.currentTimeMillis()+" where  time2 is not null");
//			mSharingdetailsService2.execSQL(msql);
			AutoMoneyTo();
			return sendTrueMsg("打款完成");
		}
		
	public void AutoMoneyTo() throws Exception{
		MyParameter.SERVICE_TURE=false;
		Thread.sleep(1000);
		Sql msql = new Sql();
		msql.setSql("select * from member where id in(select DISTINCT memberid1 from ("
				+ "	select to_char(memberid1)memberid1 from Commission where  time2 is not null and time3 is null "
				+ " union all "
				+ " select to_char(memberid2) from Commission where  time2 is not null and time3 is null "
				+ " union all "
				+ " select to_char(memberid3) from Commission where  time2 is not null and time3 is null "
				+ " union all "
				+" select to_char(memberid4) from Commission where  time2 is not null and time3 is null "
				+" union all "
			    +"   select to_char(memberone) from Commission where  time2 is not null and time3 is null "
				+ ")) ");
		List<Member> listMember = IBeanUtil.ListMap2ListJavaBean( mMemberService.exeSelectSql(msql), Member.class);
		msql.setSql("select * from  Commission  where  time2 is not null and time3 is null ");
		List<Commission> listCommission = IBeanUtil.ListMap2ListJavaBean( mMemberService.exeSelectSql(msql), Commission.class);
		for (Commission mCommission : listCommission) {
			for (Member mMember : listMember) {
				
				if(mMember.getId().toString().equals(mCommission.getMemberid1()+"")){
					mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mCommission.getNum1()));
				}
				if(mMember.getId().toString().equals(mCommission.getMemberid2()+"")){
					mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mCommission.getNum2()));
				}
				if(mMember.getId().toString().equals(mCommission.getMemberid3()+"")){
					mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mCommission.getNum3()));
				}
				if(mMember.getId().toString().equals(mCommission.getMemberid4()+"")){
					mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mCommission.getNum4()));
				}
				if(mMember.getId().toString().equals(mCommission.getMemberone()+"")){
					mMember.setPlatformcurrency(mMember.getPlatformcurrency().add(mCommission.getOnenum()));
				}
			}
		}
		msql.setSql("update Commission set time3="+System.currentTimeMillis()+"  where state=3  and time2 is not null  and time3 is null");
		mSharingdetailsService2.execSQL(msql);
		
		for (Member mMember : listMember) {
			mMemberService.updateBySelect(mMember);
			setRedisMember(mMember, false);
		}
		MyParameter.SERVICE_TURE=true;
	}
		
		
	   @Scheduled(cron ="0 15 2 ? * *")//每天凌晨2.15执行
//	   @Scheduled(cron ="0/2 * *  * * ? ")//5秒执行一次，用于测试
	   @Transactional
	   public void Auto() throws Exception{
		   AutoMoneyTo();
//		   	Sql msql = new Sql();
//			msql.setSql("update Commission set time1="+System.currentTimeMillis()+" where state=2 and time1 is null");
//			mSharingdetailsService2.execSQL(msql);
//		   List<Orders> listorders;
//		   Sql msql = new Sql();
//		   
//		   //----------------------------------规则1-------------------------------------------------------------
//		   //正向
//		   msql.setSql("select * from orders where status=4 and shippingtype!=3 and autosystem = '0' and memberidsu not in (select Identity.memberid from Identity left join shop on shop.id=shopid where Identity.memberid!=shop.memberid)");
//		   listorders =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Orders.class);
//		   for (Orders orders : listorders) {
//			   AutoSettlementA(orders);
//			}
//		   //----------------------------------规则2-------------------------------------------------------------
//		   //正向
//		   msql.setSql("select * from orders where status=4 and shippingtype=3 and autosystem = '0' and memberidsu not in (select Identity.memberid from Identity left join shop on shop.id=shopid where Identity.memberid!=shop.memberid)");
//		   listorders =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Orders.class);
//		   for (Orders orders : listorders) {
//			   AutoSettlementB(orders);
//			}
//		   
//		   
//		   //逆向业务
////		   msql.setSql("select * from RETURNORDERS where status>21  and autosystem = '0' and memberidsu not in (select Identity.memberid from Identity left join shop on shop.id=shopid where Identity.memberid!=shop.memberid)");
////		   listorders =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Orders.class);
////		   for (Orders orders : listorders) {
////			   returnorders(orders.getOrdernumber());
////			   msql.setSql("update RETURNORDERS set Autosystem=1 where id="+orders.getId());
////			   mOrdersService.execSQL(msql, -1, "");
////			}
//		   
//		   //经销商与线上店主线路正向结算
//		   msql.setSql("select * from orders where status=4  and autosystem = '0' and memberidsu  in (select Identity.memberid from Identity left join shop on shop.id=shopid where Identity.memberid!=shop.memberid)");
//		   listorders =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Orders.class);
//		   for (Orders orders : listorders) {
//			   AutoSettlementA2(orders);
//			}
////		   //经销商与线上店主线路逆向结算
////		   msql.setSql("select * from RETURNORDERS where status>21  and autosystem = '0' and memberidsu  in (select Identity.memberid from Identity left join shop on shop.id=shopid where Identity.memberid!=shop.memberid)");
////		   listorders =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Orders.class);
////		   for (Orders orders : listorders) {
////			   returnorders2(orders.getOrdernumber());
////			   msql.setSql("update RETURNORDERS set Autosystem=1 where id="+orders.getId());
////			   mOrdersService.execSQL(msql);
////			}
////		   
//		   
	   }

	   /**
	    * 分成规则1：
	    * 单品价格-9，订单物流方式1，2
	    * i:1售货，执行正向业务，其他执行逆向业务
	    * 
	    * */
//	   private void AutoSettlementA2(Orders orders) throws Exception{
//		   if(orders==null)return;
//		   Sql msql = new Sql();
//		   msql.setSql("select * from (select * from Programme order by id desc )where rownum=1");
//		   
//		   Programme or = null;
//		   try {
//			   or =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),Programme.class).get(0);
//		   		} catch (Exception e) {
//		   			return;
//		
//		   };
//		   Sharingdetails2 mSharingdetails =new Sharingdetails2();
//		   
//			
//			 mSharingdetails.setOrdersid(orders.getId());
//		   mSharingdetails.setShopone((orders.getPayment().multiply(or.getShopone())).add(new BigDecimal("900")));
//		   mSharingdetails.setShopto(orders.getPayment().multiply(or.getShopto()));
//		   mSharingdetails.setShop(orders.getPayment().multiply(or.getShop()));
//		   mSharingdetails.setSales(orders.getPayment().multiply(or.getSales()));
//		   mSharingdetails.setOnlineshopkeeper(orders.getPayment().multiply(or.getOnlineshopkeeper()));
//		   mSharingdetails.setSystemone(orders.getPayment().multiply(or.getSystemone()));
//		   mSharingdetails.setMemberid(orders.getMemberidsu());
//		   mSharingdetails.setMemberid1(orders.getMemberid());
//		   mSharingdetails.setState(0);
//		   mSharingdetails.setIstrue(0);
//		   mSharingdetails.setFid(or.getId());
//		   mSharingdetails.setShopid(orders.getShopid());
//		   
//		   msql.setSql("select * from (select * from Identity where memberid ="+orders.getMemberidsu()+" order by id desc ) where rownum=1");
//		   List<Map<String, Object>> listmap= mSharingdetailsService2.exeSelectSql(msql);
//		   if(listmap.size()!=1)return;
//		   Identity mIdentity = IBeanUtil.Map2JavaBean(listmap.get(0), Identity.class);
//		   if(mIdentity.getType()==1){
//			   mSharingdetails.setSalesmemberid(mIdentity.getMemberid());
//			   mSharingdetails.setOnlineshopkeepermemberid(mIdentity.getMemberid());
//		   }
//		   else{
//			   mSharingdetails.setOnlineshopkeepermemberid(mIdentity.getMemberid());
//			   msql.setSql("select * from (select * from Identity where id ="+mIdentity.getSuid()+" order by id desc ) where rownum=1");
//			   listmap= mSharingdetailsService2.exeSelectSql(msql);
//			   mIdentity = IBeanUtil.Map2JavaBean(listmap.get(0), Identity.class);
//			   if(listmap.size()!=0){
//				   mSharingdetails.setSalesmemberid(mIdentity.getMemberid());
//			   }
//			   
//			   
//		   }
//			  
//		   
////		   if(mIdentity.getShopid()!=0){
////			   msql.setSql("select * from (select * from Identity where suid ="+mIdentity.getId()+" order by id desc ) where rownum=1");
////			   mIdentity = IBeanUtil.Map2JavaBean(mSharingdetailsService2.exeSelectSql(msql).get(0), Identity.class);
////			   if(mIdentity.getType()==1)
////				   mSharingdetails.setSalesmemberid(mIdentity.getMemberid());
////			   else
////				   mSharingdetails.setOnlineshopkeepermemberid(mIdentity.getMemberid());
////		   }
//		   
//		   mSharingdetailsService2.add(mSharingdetails);
//		   msql.setSql("update orders set Autosystem=1 where id="+orders.getId());
//		   mOrdersService.execSQL(msql, -1, "");
//	   
//	   }
	   /**
	    * 分成规则1：
	    * 单品价格-9，订单物流方式1，2
	    * i:1售货，执行正向业务，其他执行逆向业务
	    * 
	    * */
//	   private void AutoSettlementA(Orders orders) throws Exception{
//		   if(orders==null)return;
//		   Sql msql = new Sql();
//		   msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//				   + "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+orders.getShopid()+" ) and nvl(type,1)=1 order by id desc)"
//				   + "where rownum=1");
//		   
//		   OrdersRule or = null;
//		   try {
//			   or =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),OrdersRule.class).get(0);
//		   } catch (Exception e) {
//			   return;
//			   
//		   };
//		   int osize=0;
//		   Sharingdetails mSharingdetails =new Sharingdetails();
//		   mSharingdetails.setClerk(new BigDecimal("0"));
//		   msql.setSql("select sum(num) num from Orderrelevance  left join Commodity on itemid= Commodity.id  "
//				   + "where orderid="+orders.getId()+
//				   "group by Commoditykeyid");
//		   List<Orderrelevance> listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orderrelevance.class);
//		   for (Orderrelevance orderrelevance : listOrderrelevance) {
//			   osize=osize+orderrelevance.getNum();
//			   if(orderrelevance.getNum()>1){
//				   mSharingdetails.setClerk(mSharingdetails.getClerk().add(new BigDecimal((orderrelevance.getNum()-1)*400)));
//			   }
//		   }
//		   
//		   orders.setPayment(orders.getPayment().subtract(new BigDecimal(osize*9)));
//		   
//		   mSharingdetails.setOrdersid(orders.getId());
//		   mSharingdetails.setShopone((orders.getPayment().multiply(or.getShopone())).add(new BigDecimal("900")));
//		   mSharingdetails.setShopto(orders.getPayment().multiply(or.getShopto()));
//		   mSharingdetails.setShop(orders.getPayment().multiply(or.getShop()));
//		   mSharingdetails.setClerk(mSharingdetails.getClerk().add( orders.getPayment().multiply(or.getClerk())));
//		   mSharingdetails.setSharingdetailsshopid(orders.getShopid());
//		   //已分金额
//		   BigDecimal ba =mSharingdetails.getShopone().add(mSharingdetails.getShopto()).add(mSharingdetails.getShop()).add(mSharingdetails.getClerk());
//		   mSharingdetails.setSystemone(orders.getPayment().multiply(new BigDecimal(100)).subtract(ba).add(new BigDecimal(osize*900)));
//		   
////		   msql.setSql("select memberida,memberidb from orders left join Friends on memberidb=memberid where orders.id="+orders.getId());
//		   
////		   List<Map<String, Object>> lm = mSharingdetailsService.exeSelectSql(msql);
//		   mSharingdetails.setMemberid(orders.getMemberidsu());
//		   mSharingdetails.setMemberid1(orders.getMemberid());
//		   mSharingdetails.setState(0);
//		   
//		   
//		   mSharingdetailsService.add(mSharingdetails);
//		   msql.setSql("update orders set Autosystem=1 where id="+orders.getId());
//		   mOrdersService.execSQL(msql, -1, "");
//		   
//	   }
	   /**
	    * 分成规则2：
	    * 不参与单品价格-9，订单物流方式3
	    * i:1售货，执行正向业务，其他执行逆向业务
	    * 
	    * */
//	   private void AutoSettlementB(Orders orders) throws Exception{
//		   if(orders==null)return;
//		   Sql msql = new Sql();
//		   msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//				   + "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+orders.getShopid()+" ) and nvl(type,1)=2 order by id desc)"
//				   + "where rownum=1");
//		   
//		   OrdersRule or = null;
//		   try {
//			   or =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),OrdersRule.class).get(0);
//		   } catch (Exception e) {
//			   return;
//			   
//		   };
//			   Sharingdetails mSharingdetails =new Sharingdetails();
//			   
//			   mSharingdetails.setOrdersid(orders.getId());
//			   mSharingdetails.setShopone((orders.getPayment().multiply(or.getShopone())));
//			   mSharingdetails.setShopto(orders.getPayment().multiply(or.getShopto()));
//			   mSharingdetails.setShop(orders.getPayment().multiply(or.getShop()));
//			   mSharingdetails.setClerk(orders.getPayment().multiply(or.getClerk()));
//			   //已分金额
//			   mSharingdetails.setSystemone(orders.getPayment().multiply(or.getSystemone()));
//			   
////			   msql.setSql("select memberida,memberidb from orders left join Friends on memberidb=memberid where orders.id="+orders.getId());
////			   List<Map<String, Object>> lm = mSharingdetailsService.exeSelectSql(msql);
////			   mSharingdetails.setMemberid(Long.valueOf(lm.get(0).get("MEMBERIDA")+""));
////			   mSharingdetails.setMemberid1(Long.valueOf(lm.get(0).get("MEMBERIDB")+""));
//			   mSharingdetails.setMemberid(orders.getMemberidsu());
//			   mSharingdetails.setMemberid1(orders.getMemberid());
//			   mSharingdetails.setState(0);
//			   mSharingdetails.setIstrue(0);
//		   
//		   
//		   mSharingdetailsService.add(mSharingdetails);
//		   msql.setSql("update orders set Autosystem=1 where id="+orders.getId());
//		   mOrdersService.execSQL(msql, -1, "");
//		   
//	   }
	   
//	  private void AutoSettlement(int i) throws Exception{
//		  Sql msql = new Sql();
//		  List<Orders> Lmap;
//		  if(i==1)
//			  Lmap = IBeanUtil.ListMap2ListJavaBean(mOrdersService.getOrdersBystatus(), Orders.class);
//		  else{
//			  msql.setSql("select * from ReturnORDERS where autosystem=0 and ordernumber  not in(select ordersid from Sharingdetails where shop<0)");
//			  Lmap = IBeanUtil.ListMap2ListJavaBean( mOrdersService.exeSelectSql(msql),Orders.class);
//		  }
//		  List<Map<String, Object>> lm;
//		   List<OrdersRule> lOrdersRule;//结算规则
//		  
//		   for (Orders orders : Lmap) {
//			   msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//			   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+orders.getShopid()+" ) and nvl(type,1)=1 order by id desc)"
//			   				+ "where rownum=1"
//			   				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
//			   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = "+orders.getShopid()+" ) and nvl(type,1)=2 order by id desc)"
//			   				+ "where rownum=1");
//			   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(mSharingdetailsService.exeSelectSql(msql),OrdersRule.class);
//			   if(lOrdersRule.size()==0)continue;
//			   OrdersRule or ;
//			   if(orders.getShippingtype()==3){
//				   //无需物流
//				   if(lOrdersRule.size()==1){
//					   if(lOrdersRule.get(0).getType()==2)
//						   or=lOrdersRule.get(0);
//					   else
//						   continue;
//				   }else{
//					   if(lOrdersRule.get(0).getType()==2)
//						   or=lOrdersRule.get(0);
//					   else
//						   or=lOrdersRule.get(1);
//				   }
//			   }else{
//				   //有物流
//				   if(lOrdersRule.size()==1){
//					   if(lOrdersRule.get(0).getType()==1)
//						   or=lOrdersRule.get(0);
//					   else
//						   continue;
//				   }else{
//					   if(lOrdersRule.get(0).getType()==1)
//						   or=lOrdersRule.get(0);
//					   else
//						   or=lOrdersRule.get(1);
//				   }
//			   }
//			   if(or==null) continue;//如果未添加结算规则直接跳过去
//			   if(i==1){
//				   Sharingdetails mSharingdetails =new Sharingdetails();
//				   mSharingdetails.setOrdersid(orders.getId());
//				   mSharingdetails.setShopone(orders.getPayment().multiply(or.getShopone()));
//				   mSharingdetails.setShopto(orders.getPayment().multiply(or.getShopto()));
//				   mSharingdetails.setShop(orders.getPayment().multiply(or.getShop()));
//				   mSharingdetails.setSystemone(orders.getPayment().multiply(or.getSystemone()));
//				   mSharingdetails.setClerk(orders.getPayment().multiply(or.getClerk()));
//				   msql.setSql("select memberida,memberidb from orders left join Friends on memberidb=memberid where orders.id="+orders.getId());
//				   lm = mSharingdetailsService.exeSelectSql(msql);
//				   mSharingdetails.setMemberid(Long.valueOf(lm.get(0).get("MEMBERIDA")+""));
//				   mSharingdetails.setMemberid1(Long.valueOf(lm.get(0).get("MEMBERIDB")+""));
//				   mSharingdetails.setState(0);
//
//				   msql.setSql("select * from Orderrelevance  where orderid="+orders.getId());
//				   List<Orderrelevance> listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orderrelevance.class);
//					  for (Orderrelevance orderrelevance : listOrderrelevance) {
//						if(orderrelevance.getNum()>1){
//							 mSharingdetails.setClerk(mSharingdetails.getClerk().add(new BigDecimal((orderrelevance.getNum()-1)*400)));
//						}
//					}
//				   
//				   
//				   mSharingdetailsService.add(mSharingdetails);
//				   msql.setSql("update orders set Autosystem=1 where id="+orders.getId());
//				   mOrdersService.execSQL(msql, -1, "");
//			   }else{
//				   Sharingdetails mSharingdetails =new Sharingdetails();
//				   mSharingdetails.setOrdersid(Long.valueOf(orders.getOrdernumber()));
//				   mSharingdetails.setShopone(new BigDecimal("-"+orders.getPayment().multiply(or.getShopone())));
//				   mSharingdetails.setShopto(new BigDecimal("-"+orders.getPayment().multiply(or.getShopto())));
//				   mSharingdetails.setShop(new BigDecimal("-"+orders.getPayment().multiply(or.getShop())));
//				   mSharingdetails.setSystemone(new BigDecimal("-"+orders.getPayment().multiply(or.getSystemone())));
//				   mSharingdetails.setClerk(new BigDecimal("-"+orders.getPayment().multiply(or.getClerk())));
//				   msql.setSql("select memberida,memberidb from RETURNORDERS left join Friends on memberidb=memberid where RETURNORDERS.id="+orders.getId());
//				   lm = mSharingdetailsService.exeSelectSql(msql);
//				   mSharingdetails.setMemberid(Long.valueOf(lm.get(0).get("MEMBERIDA")+""));
//				   mSharingdetails.setMemberid1(Long.valueOf(lm.get(0).get("MEMBERIDB")+""));
//				   mSharingdetails.setState(0);
//				   
//				   msql.setSql("select * from Orderrelevance  where orderid="+orders.getOrdernumber());
//				   List<Orderrelevance> listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orderrelevance.class);
//					  for (Orderrelevance orderrelevance : listOrderrelevance) {
//						if(orderrelevance.getNum()>1){
//							 mSharingdetails.setClerk(mSharingdetails.getClerk().subtract(new BigDecimal((orderrelevance.getNum()-1)*400)));
//						}
//					}
//				   
//				   
////				   Integer isize = Integer.parseInt(mOrdersService.exeSelectSql(msql).get(0).get("A").toString());
////				   if(isize>1){
////					   mSharingdetails.setClerk(mSharingdetails.getClerk().add(new BigDecimal((isize-1)*4)));
////				   }
//					  
//				msql.setSql("select id from Sharingdetails  where ordersid="+orders.getId());
//				if(mOrdersService.exeSelectSql(msql).size()==0){
//				   mSharingdetailsService.add(mSharingdetails);
//				   msql.setSql("update ReturnORDERS set Autosystem=1 where id="+orders.getId());
//				   mOrdersService.execSQL(msql, -1, "");
//				}
//				   
//			
//			   }
//			  
//			   
//			 
//			   
//			   
//			   
//			   
//			   
//			   
//		   }
//		   
//	   }
	   
	   /**
	    * 清除未支付订单
	    * @throws Exception 
	    * */
//	   @Scheduled(cron ="0/5 * *  * * ? ")//5秒执行一次，用于测试
	   @Scheduled(cron ="0 15 3 ? * *")//每天凌晨3.15执行
	   @Transactional
	   public  void deleteorders() throws Exception {
		   Shop5941Application.out("========================开始清除未支付订单===================================");
		   Sql msql = new Sql();
		   //查出未付款的订单id
		   msql.setSql("select id from orders where status ='1' or status='5'");
		   List<Orders> Lmap = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orders.class);
		   StringBuilder sb = new StringBuilder();
		   if(Lmap.size()!=0){
			   for (Orders orders : Lmap) {
					sb.append(orders.getId());
					sb.append(",");
				  }   
			   //查出未付款的商品
			   msql.setSql("select * from orderrelevance where orderid in("+sb.substring(0,sb.length()-1)+")");
			   List<Orderrelevance> listmap = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orderrelevance.class);
			   for (Orderrelevance orderrelevance : listmap) {
				   //归还库存
				   msql.setSql("update stock set num = num+"+orderrelevance.getNum()+" where code='"+orderrelevance.getYoucode()+"'");
				   mOrdersService.execSQL(msql, -1, "");
				   //删除购买的商品
				   msql.setSql("delete orderrelevance where id= "+orderrelevance.getId());
				   mOrdersService.execSQL(msql, -1, "");
			   }
			   //删除运费
			   msql.setSql("delete Postfees where ordersid in("+sb.substring(0,sb.length()-1)+")");
			   mOrdersService.execSQL(msql, -1,"");
			   //删除订单
			   msql.setSql("delete orders where id in("+sb.substring(0,sb.length()-1)+")");
			   mOrdersService.execSQL(msql, -1,"");
			  
		   }


		   Shop5941Application.out("========================清除完成===================================");
	   }
	   /**
	    * 自动收货
	    * @throws Exception 
	    * */
	   @Scheduled(cron ="0/60 * *  * * ? ")//5秒执行一次，用于测试
	   @Transactional
	   public  void autoOrders() throws Exception {
		   Sql msql = new Sql();
		   Long time = System.currentTimeMillis()-(1000L*60L*60L*24L*10L);
//		   Long time = System.currentTimeMillis()-(1000L*60L*5);
		   msql.setSql("select id from orders where status ='3' and Updatetime>"+(time-(1000L*60L*10))+" and Updatetime <"+time);
		   List<Orders> Lmap = IBeanUtil.ListMap2ListJavaBean(mOrdersService.exeSelectSql(msql), Orders.class);
		   for (Orders orders : Lmap) {
			   Long id = orders.getId();
			   orders = new Orders();
			   orders.setId(id);
			   orders.setStatus(4);
//			   msql.setSql("update orders set Status=4 where id="+orders.getId());
			   mOrdersService.updateBySelect(orders, 1);
		}
	   
	   }
	   
	   /**
	    * 门店贡献打款
	    * */
//	   @Scheduled(cron ="0 30 2 ? * *")//每天凌晨2.30执行
//	   @Transactional
//	   public void AutoMoney() throws Exception{
//		   /**服务器暂时暂停提供服务*/
//		MyParameter.SERVICE_TURE=false;
//		Thread.sleep(3000);//先等待3秒，等待服务器处理其他数据
//		  Sql msql = new Sql();
//		  msql.setSql("select orders.id oid,orders.payment,A.memberid onememberid,A.id oneid,memberidsu,shop.memberid shopmemberid ,orders.shippingtype,SHOP.oneid from Sharingdetails left join  "
//		  		+ "orders on orders.id=ordersid left join  shop on shop.id=shopid LEFT JOIN SHOP A ON SHOP.ONEID=A.ID "
//		  		+ "where istrue=0 and memberidsu is not null and Sharingdetails.state=1 and orders.id>1538323200000 and ordersid not in(select ordernumber from ReturnOrders)");	
//		  List<Map<String, Object>> listmap = mSharingdetailsService.exeSelectSql(msql);
//		  StringBuilder ida;//拼接的id
//		  List<Member> listmember;//分金额的用户
//		  List<String> ls;//订单id
//		  Set<Long> set ;//用户id
//		  String ONEMEMBERID;//供应商id
//		  String MEMBERIDSU;//推荐人id
//		  String SHOPMEMBERID;//店铺用户id
//		  Integer type;//订单类型
//		  BigDecimal b1;//金额
//		  OrdersRule or;//规则
//		  String oneid;//供应商店铺id
//		  String oid;//订单id
//		  if(listmap.size()!=0){
//			   set = new HashSet<Long>();
//			  ls = new ArrayList<String>();
//			  for (Map<String, Object> map : listmap) {
//			  set.add(Long.valueOf(map.get("MEMBERIDSU").toString()));
//			  set.add(Long.valueOf(map.get("SHOPMEMBERID").toString()));
//			  set.add(Long.valueOf(map.get("ONEMEMBERID").toString()));
//			  ls.add(map.get("OID").toString());
//		}
//			  ida = new StringBuilder();
//			  for (Long long1 : set) {
//				  ida.append(long1);
//				  ida.append(",");
//			 }
//			  msql.setSql("select * from member where id in("+ida.substring(0,ida.length()-1)+")");
//		 listmember =  IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Member.class);
//			 
//		  for (Map<String, Object> map : listmap) {
//			  oid=map.get("OID").toString();
//			  oneid=map.get("ONEID").toString();
//			  type=map.get("SHIPPINGTYPE").toString().equals("3")?2:1;
//			   b1 = new BigDecimal(map.get("PAYMENT").toString());
//			//单品减9，
//				msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+oid );
//				
//				 b1 = b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				
//			  ONEMEMBERID=map.get("ONEMEMBERID").toString();
//			  MEMBERIDSU= map.get("MEMBERIDSU").toString();
//			  SHOPMEMBERID= map.get("SHOPMEMBERID").toString();
//			 
//			  
//				try {
//					//分成
//					 msql.setSql("select *  from OrdersRule where shoponeid="+oneid+" and nvl(type,1)="+type);
//					msql.setOrderbykey("id");
//					msql.setOrderbytype(1);
//					 or = IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0),OrdersRule.class);
//					 if(or==null)
//						 continue;
//					 
//						msql.setOrderbykey(null);
//						msql.setOrderbytype(null);
//				} catch (Exception e) {
//					continue;
//				}
//				
//				for (Member member : listmember) {
//					if(member.getId().toString().equals(ONEMEMBERID)){
//						member.setPlatformcurrency(member.getPlatformcurrency()
//								.add(b1
//										.divide(new BigDecimal(100))
//										.multiply(or.getShopone())));
//					}
//					if(member.getId().toString().equals(MEMBERIDSU)){
//						member.setPlatformcurrency(member.getPlatformcurrency()
//								.add( b1
//										.divide(new BigDecimal(100))
//										.multiply(or.getClerk())));
//						
//						//导购拼单+4
//						msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								+ "left join orders on orders.id=orderid "
//								+ "where orders.id ="+oid+" group by Commoditykeyid,orderid)");
//						member.setPlatformcurrency(member.getPlatformcurrency().add(
//								new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString())));
//					}
//					if(member.getId().toString().equals(SHOPMEMBERID)){
//						member.setPlatformcurrency(member.getPlatformcurrency()
//								.add( b1
//										.divide(new BigDecimal(100))
//										.multiply(or.getShop())));
//					}
//				}
//			 
//		  }
//		  for (Member member : listmember) {
//			  mMemberService.updateBySelect(member);
//			  setRedisMember(member, false);
//			}
//		  ida = new StringBuilder();
//		  for (String str : ls) {
//			  ida.append(str);
//			  ida.append(",");
//		 }
//		  msql.setSql("update Sharingdetails set istrue=1 where ordersid in("+ida.substring(0,ida.length()-1)+")");
//		  mMemberService.execSQL(msql);
//		  }
//		
//	
//		/**继续提供服务*/
//		 MyParameter.SERVICE_TURE=true;
//		 Application.out("==============");
//	}
	   /**
	    * 线上店主与经销商打款
	    * */
//	   @Scheduled(cron ="0 40 2 ? * *")//每天凌晨2.40执行
//	   @Transactional
//	   public void AutoMoney2() throws Exception{
//		   /**服务器暂时暂停提供服务*/
//		   MyParameter.SERVICE_TURE=false;
//		   Thread.sleep(3000);//先等待3秒，等待服务器处理其他数据
//		   Sql msql = new Sql();
//		   /*msql.setSql("select orders.id oid,orders.payment,A.memberid onememberid,A.id oneid,memberidsu,shop.memberid shopmemberid ,orders.shippingtype,SHOP.oneid from Sharingdetails2 left join  "
//				   + "orders on orders.id=ordersid left join  shop on shop.id=shopid LEFT JOIN SHOP A ON SHOP.ONEID=A.ID "
//				   + "where istrue=0 and memberidsu is not null and Sharingdetails.state=1 and orders.id>1538323200000");	
//		   List<Map<String, Object>> listmap = mSharingdetailsService.exeSelectSql(msql);*/
//		   List<Map<String, Object>> listmap;
//		   StringBuilder ida;//拼接的id
//		   List<Member> listmember;//分金额的用户
//		   List<String> ls;//订单id
//		   Set<Long> set ;//用户id
//		   String ONEMEMBERID;//供应商id
//		   String MEMBERIDSU;//推荐人id
//		   String SHOPMEMBERID;//店铺用户id
//		   BigDecimal b1;//金额
//		   String oid;//订单id
//		   
//		   //线上店主与经销商打款
//		   msql.setSql("select SALESMEMBERID,ONLINESHOPKEEPERMEMBERID,orders.id oid,orders.payment,A.memberid onememberid,A.id oneid,memberidsu,shop.memberid shopmemberid ,orders.shippingtype,SHOP.oneid from Sharingdetails2 left join  "
//				   + "orders on orders.id=ordersid left join  shop on shop.id=orders.shopid LEFT JOIN SHOP A ON SHOP.ONEID=A.ID "
//				   + "where istrue=0 and memberidsu is not null and Sharingdetails2.state=1 and ordersid not in(select ordernumber from ReturnOrders)");	
//		   listmap = mSharingdetailsService.exeSelectSql(msql);
//		   if(listmap.size()!=0){
//			   set = new HashSet<Long>();
//			   ls=new ArrayList<String>();
//			   msql.setSql("select * from (select * from Programme order by id desc) where rownum=1");
//			   //结算规则
//			   Programme mProgramme = IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0), Programme.class) ;
//			   for (Map<String, Object> map : listmap) {
//				   set.add(Long.valueOf(map.get("SALESMEMBERID").toString()));
//				   set.add(Long.valueOf(map.get("ONLINESHOPKEEPERMEMBERID").toString()));
//				   set.add(Long.valueOf(map.get("SHOPMEMBERID").toString()));
//				   set.add(Long.valueOf(map.get("ONEID").toString()));
//				   ls.add(map.get("OID").toString());
//			   }
//			   
//			   ida = new StringBuilder();
//			   for (Long long1 : set) {
//				   ida.append(long1);
//				   ida.append(",");
//			   }
//			   msql.setSql("select * from member where id in("+ida.substring(0,ida.length()-1)+")");
//			   listmember =  IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Member.class);
//			   String SALESMEMBERID;
//			   String ONLINESHOPKEEPERMEMBERID;
//			   for (Map<String, Object> map : listmap) {
//				   oid=map.get("OID").toString();
//				   b1 = new BigDecimal(map.get("PAYMENT").toString());
//				   //单品减9，
//				   msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance where orderid="+oid );
//				   
//				   b1 = b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
//				   
//				   MEMBERIDSU= map.get("MEMBERIDSU").toString();
//				   ONEMEMBERID=map.get("ONEID").toString();
//				   SALESMEMBERID= map.get("SALESMEMBERID").toString();
//				   ONLINESHOPKEEPERMEMBERID= map.get("ONLINESHOPKEEPERMEMBERID").toString();
//				   SHOPMEMBERID=	 map.get("SHOPMEMBERID").toString();
//				   for (Member member : listmember) {
//					   if(ONEMEMBERID.toString().equals(member.getId().toString())){
//						   member.setPlatformcurrency(member.getPlatformcurrency()
//								   .add(mProgramme.getShopone()
//										   .multiply(b1
//												   .divide(new BigDecimal(100)))));
//					   }
//					   if(SALESMEMBERID.toString().equals(member.getId().toString())){
//						   member.setPlatformcurrency(member.getPlatformcurrency()
//								   .add(mProgramme.getSales()
//										   .multiply(b1
//												   .divide(new BigDecimal(100)))));
//					   }
//					   if(ONLINESHOPKEEPERMEMBERID.toString().equals(member.getId().toString())){
//						   member.setPlatformcurrency(member.getPlatformcurrency()
//								   .add(mProgramme.getOnlineshopkeeper()
//										   .multiply(b1
//												   .divide(new BigDecimal(100)))));
//					   }
//					   if(SHOPMEMBERID.toString().equals(member.getId().toString())){
//						   member.setPlatformcurrency(member.getPlatformcurrency()
//								   .add(mProgramme.getShop()
//										   .multiply(b1
//												   .divide(new BigDecimal(100)))));
//					   }
//					   if(MEMBERIDSU.equals(member.getId().toString())){
//						   //导购拼单+4
//						   msql.setSql("select nvl((sum(a)-count(*))*4,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
//								   + "left join orders on orders.id=orderid "
//								   + "where orders.id ="+oid+" group by Commoditykeyid,orderid)");
//						   member.setPlatformcurrency(member.getPlatformcurrency().add(
//								   new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString())));
//					   }
//				   }
//			   }
//			   for (Member member : listmember) {
//				   mMemberService.updateBySelect(member);
//				   setRedisMember(member, false);
//			   }
//			   ida = new StringBuilder();
//			   for (String str : ls) {
//				   ida.append(str);
//				   ida.append(",");
//			   }
//			   msql.setSql("update Sharingdetails2 set istrue=1 where ordersid in("+ida.substring(0,ida.length()-1)+")");
//			   mMemberService.execSQL(msql);
//		   }
//		   
//		   /**继续提供服务*/
//		   MyParameter.SERVICE_TURE=true;
//	   }
	   
	   /**
	    *自动收发货
	    * */
	   
//	   @Scheduled(cron ="0/5 * *  * * ? ")//5秒执行一次，用于测试
	   @Scheduled(cron = "0 0 */1 * * ?")
	   @Transactional
	   public void AutoOrders() throws Exception{
		   //门店自动发货
		   Sql msql = new Sql();
		   msql.setSql("select * from orders where status=7 and "+System.currentTimeMillis()+"-updatetime>"+ MyParameter.ReturnOrdersTime);	
//		   msql.setSql("select * from orders where status=7 and "+System.currentTimeMillis()+"-updatetime>"+ 6000);	
		   List<Orders> listorders =IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),Orders.class);
		   for (Orders or : listorders) {
			   or.setStatus(3);
				String str = or.getTrajectory();
				if(str==null)str="";
				str=str+System.currentTimeMillis()+";订单已发货(系统自动);";
				or.setTrajectory(str);
				mOrdersService.updateBySelect(or);
				Message.start(mSystemMessageService, "物流助手", "到货通知", "您的订单号为:"+or.getId()+"的商品已发货啦", or.getMemberid());
		 }
		   //用户自动收货
		   msql.setSql("select * from orders where status=3 and "+System.currentTimeMillis()+"-updatetime>"+ MyParameter.OrdersTrueTime);	
//		   msql.setSql("select * from orders where status=3 and "+System.currentTimeMillis()+"-updatetime>"+6000);	
		  listorders =IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),Orders.class);
		   for (Orders or : listorders) {
			   	or.setStatus(4);
				or.setB1("系统自动收货");
				String str = or.getTrajectory();
				if(str==null)str="";
				str=str+System.currentTimeMillis()+";超过时限，系统自动签收;";
				or.setTrajectory(str);
				mOrdersService.updateBySelect(or);
				
				Message.start(mSystemMessageService, "物流助手", "到货通知", "您的订单号为:"+or.getId()+"的商品已经确认签收，欢迎再次购买", or.getMemberid());
		   }
		   
			

		 
	   }
	   /**
	    *自动接单
	    * */
	   
	   @Scheduled(cron ="0/30 * *  * * ? ")//5秒执行一次，用于测试
	   @Transactional
	   public void jd() throws Exception{
		   //门店自动发货
		   Sql msql = new Sql();
		   msql.setSql("select * from orders where status=2 and ("+System.currentTimeMillis()+"-updatetime)>"+ MyParameter.OrdersjdTime);	
//		   msql.setSql("select * from orders where status=2 and "+System.currentTimeMillis()+"-updatetime>"+ 6000);	
		   List<Orders> listorders =IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),Orders.class);
		   for (Orders or : listorders) {
 				for (Orders_myp e : Orders_myp.values()) {
 					if(e!=Orders_myp.MS){
 						if(e.toString().equals(or.getB()))
 							 continue;
 					}
					
				}
			   or.setStatus(8);
				or.setB("系统自动接单");
				String str = or.getTrajectory();
				if(str==null)str="";
				str=str+System.currentTimeMillis()+";商家已接单;";
				or.setTrajectory(str);
				mOrdersService.updateBySelect(or);
				
				Message.start(mSystemMessageService, "物流助手", "接单通知", "您的订单号为:"+or.getId()+"的商品商家已经接单，正在分解订单", or.getMemberid());
				
				
				//心跳任务
				String[] sa = new String[4];
				sa[0]="0";//任务id
				sa[1]="-1";//延时时间，毫秒，空为默认值
				sa[2]=or.getId()+"";//数据id
				sa[3]=or.getMemberid()+"";//附加数据
				TSystem.start(sa, mTaskService);
		   }
		   
		   
	   }
	   @Scheduled(cron ="0 15 1 ? * *")//每天凌晨1.15执行
	   @Transactional
	   public void  zzmd() throws Exception{
		   Emil.send("24994604@qq.com");
		   Emil.send("1651603612@qq.com");
	   }
}
