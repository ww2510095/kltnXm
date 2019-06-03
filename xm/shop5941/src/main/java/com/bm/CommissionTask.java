package com.bm;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.bm.Aenum.Orderrelevance_gdf;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.util.IBeanUtil;
import com.bm.clerk.commission.Commission;
import com.bm.clerk.commission.CommissionService;
import com.bm.clerk.identity.Identity;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.orders.orders.Orders;
import com.bm.ordersRule.OrdersRule;
import com.bm.ordersRule.Programme;
import com.bm.ordersRule.gd.Gd;
import com.bm.ordersRule.gd.Gd_Commodity_key;
import com.myjar.desutil.RunException;

public class CommissionTask implements Runnable{
	
	public static final int COMMISSION_1=1;
	public static final int COMMISSION_2=2;
	public static final int COMMISSION_3=3;
	public static final int COMMISSION_K=4;
	public static final int COMMISSION_K2=6;
	public static final int COMMISSION_TASK=5;
	public static final int COMMISSION_RETURN=7;
	public static volatile boolean COMMISSIONTASK_TIME3=false;
	
	

	private CommissionService mCommissionService;
	private String ordersNumber;//订单编号或者订单id
	private String str_je;//订单编号或者订单id
	private Integer type;
	@Override
	@Transactional
	public void run() {
		try {
			if(type==COMMISSION_1)
				exec();
			else if(type==COMMISSION_2)
				exec2();
			else if(type==COMMISSION_3)
				exec3();
			else if(type==COMMISSION_K||type==COMMISSION_K2)
				execK();
			else if(type==COMMISSION_RETURN)
				execreturn();
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
			throw new RunException("");
		}
	}
	private void execreturn() throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from orders where ordernumber='"+ordersNumber+"'");
		List<Orders> listorders = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Orders.class);
		if(listorders.size()==0){
			msql.setSql("select * from orders where id='"+ordersNumber+"'");
			listorders = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Orders.class);
			if(listorders.size()==0)
				return;
		}
		Commission mCommission;
		for (Orders mOrders : listorders) {
			mCommission = new Commission();
			mCommission.setOrdersid(mOrders.getId());
			mCommissionService.deleteBySelect(mCommission);
		
	}
}
	public static void start(CommissionService mCommissionService,String ordersNumber,Integer type) {
		CommissionTask mCommissionTask = new CommissionTask();
		mCommissionTask.mCommissionService=mCommissionService;
		mCommissionTask.ordersNumber=ordersNumber;
		mCommissionTask.type=type;
		new Thread(mCommissionTask).start();
	}
	public static void start_je(CommissionService mCommissionService,String ordersNumber,Integer type,String je) {
		CommissionTask mCommissionTask = new CommissionTask();
		mCommissionTask.mCommissionService=mCommissionService;
		mCommissionTask.ordersNumber=ordersNumber;
		mCommissionTask.type=type;
		mCommissionTask.str_je=je;
		new Thread(mCommissionTask).start();
	}
	
	//线路，1代表门店模式，2代表线上店主经销商模式
	private int istype1(Commission mCommission) {
		if(mCommission.getType()==1||mCommission.getType()==4)
			return 1;
		if(mCommission.getType()==2||mCommission.getType()==3)
			return 2;
		if(mCommission.getTypesu()==1||mCommission.getTypesu()==4)
			return 1;
		else
			return 2;

	}
	private void exec2() throws Exception{
		Commission mCommission=mCommissionService.getByparameter("ordersid", ordersNumber,Commission.class);
		if(mCommission==null)return;
		if(mCommission.getTime1()!=null)return;
		mCommission.setState(2);
		mCommission.setTime1(System.currentTimeMillis());
		mCommissionService.updateBySelect(mCommission);
	}
	private synchronized void exec3() throws Exception{
		Commission mCommission=mCommissionService.getByparameter("ordersid", ordersNumber,Commission.class);
		if(mCommission==null)return;
		mCommission.setState(3);
		mCommission.setTime2(System.currentTimeMillis());
//		if(COMMISSIONTASK_TIME3)
//			mCommission.setTime3(System.currentTimeMillis());
		
//		COMMISSIONTASK_TIME3=false;
		mCommissionService.updateBySelect(mCommission);
	}
	//核销
	private void execK() throws  Exception {
		if(type==COMMISSION_K)
			str_je="3.5";
		
		BigDecimal je = new BigDecimal(str_je);
		String[] strs=ordersNumber.split(";");
		Commission mCommission = new Commission();
		mCommission.setOrdersid(Long.valueOf(strs[0]));
		Sql msql =new Sql();
		if(type==COMMISSION_K){
			msql.setSql("select memberid from Coupon where id="+strs[0]);
			mCommission.setMemberid(Long.valueOf(mCommissionService.exeSelectSql(msql).get(0).get("MEMBERID").toString()));
			mCommission.setMemberidsu(Long.valueOf(strs[1]));
		}else{
			mCommission.setMemberid(Long.valueOf(strs[0]));
			mCommission.setMemberidsu(Long.valueOf(strs[1]));
		}
		
		 //购买人身份
		List<Map<String, Object>> listIdentity;
		  msql.setSql("select * from clerk where memberid='"+mCommission.getMemberid()+"'");
		  listIdentity=mCommissionService.exeSelectSql(msql);
		  if(listIdentity.size()==0){
			  mCommission.setType(0);
		  }else{
			  msql.setSql("select * from shop where memberid='"+mCommission.getMemberid()+"'");
			  if(mCommissionService.exeSelectSql(msql).size()!=0){
				  mCommission.setType(4);
			  }else{
				  msql.setSql("select * from Identity where memberid='"+mCommission.getMemberid()+"'");
				  listIdentity=mCommissionService.exeSelectSql(msql);
				  if(listIdentity.size()==0){
					  mCommission.setType(1);
				  }else{
					  if(listIdentity.get(0).get("TYPE").toString().equals("1")){
						  mCommission.setType(3);
					  }else if(listIdentity.get(0).get("TYPE").toString().equals("2")){
						  mCommission.setType(2);
					  }else{
						  mCommission.setType(4);
					  }
					} 
			  }
			 
			  
		  }
		  

			msql.setSql("select * from shop where memberid="+mCommission.getMemberidsu());
			listIdentity=mCommissionService.exeSelectSql(msql);
			if(listIdentity.size()!=0){
				 mCommission.setTypesu(4);
			}else{
				  //上一级身份
				  msql.setSql("select * from clerk where memberid='"+mCommission.getMemberidsu()+"'");
				  listIdentity=mCommissionService.exeSelectSql(msql);
				  if(listIdentity.size()==0){
					  mCommission.setTypesu(0);
				  }else{
					  msql.setSql("select * from Identity where memberid='"+mCommission.getMemberidsu()+"'");
					  listIdentity=mCommissionService.exeSelectSql(msql);
					  if(listIdentity.size()==0){
						  mCommission.setTypesu(1);
					  }else{
						  if(listIdentity.get(0).get("TYPE").toString().equals("1")){
							  mCommission.setTypesu(3);
						  }else if(listIdentity.get(0).get("TYPE").toString().equals("2")){
							  mCommission.setTypesu(2);
						  }else{
							  mCommission.setTypesu(4);
						  }
						}
					  
				  }
				 
			}
		  
		  
		
		mCommission.setNum1(MyParameter.mBigDecimal_0);
		mCommission.setNum2(MyParameter.mBigDecimal_0);
		mCommission.setNum3(MyParameter.mBigDecimal_0);
		mCommission.setNum4(MyParameter.mBigDecimal_0);
		
		if(mCommission.getTypesu()==1){
			mCommission.setNum1(je);
			mCommission.setMemberid1(mCommission.getMemberidsu());
		}else if(mCommission.getTypesu()==2){
			mCommission.setNum2(je);
			mCommission.setMemberid2(mCommission.getMemberidsu());
		}else if(mCommission.getTypesu()==3){
			mCommission.setNum3(je);
			mCommission.setNumsu1(je);
			mCommission.setMemberid3(mCommission.getMemberidsu());
		}else{
			mCommission.setNum4(je);
			mCommission.setNumsu2(je);
			mCommission.setMemberid4(mCommission.getMemberidsu());
		}
		mCommission.setTypenum(1);
		mCommission.setOrdersnum(je);
		mCommission.setOnenum(MyParameter.mBigDecimal_0);
		mCommission.setMemberone("1");
		
		mCommission.setState(2);
		mCommission.setTime1(System.currentTimeMillis());
		mCommission.setCommoditynum(1);
		mCommission.setOrderstype(2);
		while (true) {
			long index =System.currentTimeMillis();

			try {
				mCommissionService.add(mCommission);
				break;
			} catch (Exception e) {
				index=index+1;
				mCommission.setId(index);
			}
			
			
		}
	}
	@Transactional
	private   void exec() throws Exception{

		Sql msql = new Sql();
		msql.setSql("select * from orders where ordernumber='"+ordersNumber+"'");
		List<Orders> listorders = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Orders.class);
		if(listorders.size()==0){
			msql.setSql("select * from orders where id='"+ordersNumber+"'");
			listorders = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Orders.class);
			if(listorders.size()==0)
				return;
		}
		Commission mCommission;
		BigDecimal b;
		BigDecimal b1 ;
		List<Map<String,Object>> listshop;
		List<Map<String,Object>> listshop1;
		List<Map<String,Object>> listIdentity;
		
		for (Orders mOrders : listorders) {
			if(mOrders.getStatus()>21||mOrders.getStatus()==10){
				mCommission = new Commission();
				mCommission.setOrdersid(mOrders.getId());
				mCommissionService.deleteBySelect(mCommission);
				return;
			}
			
		
			if(mCommissionService.getByparameter("ordersid", mOrders.getId().toString())!=null)
				return;
			//导购拼单+4
			msql.setSql("select nvl((sum(a)-count(*))*"+MyParameter.ORDERONEADD+",0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
					+ "left join orders on orders.id=orderid "
					+ "where orders.id="+mOrders.getId()
					+" group by Commoditykeyid ,orderid) ");
			
			b1=new BigDecimal(mCommissionService.exeSelectSql(msql).get(0).get("COUNT").toString());
			b=mOrders.getPayment();
			
			msql.setSql("select * from shop where memberid="+mOrders.getMemberid());
			listshop=mCommissionService.exeSelectSql(msql);
			mCommission = new Commission();
			mCommission.setOrderstype(1);
			 mCommission.setOrdersid(mOrders.getId());
			  mCommission.setMemberid(mOrders.getMemberid());
			  
			  mCommission.setNum1(MyParameter.mBigDecimal_0);
			  mCommission.setNum2(MyParameter.mBigDecimal_0);
			  mCommission.setNum3(MyParameter.mBigDecimal_0);
			  mCommission.setNum4(MyParameter.mBigDecimal_0);
			  msql.setSql("select id from member where uname='"+mOrders.getOnephone()+"'");
			  mCommission.setMemberone(mCommissionService.exeSelectSql(msql).get(0).get("ID").toString());
			 
			  //购买人身份
			  msql.setSql("select * from clerk where memberid='"+mOrders.getMemberid()+"'");
			  listIdentity=mCommissionService.exeSelectSql(msql);
			  if(listIdentity.size()==0){
				  mCommission.setType(0);
			  }else{
				  if(listshop.size()!=0){
					  mCommission.setType(4);
				  }else{
					  msql.setSql("select * from Identity where memberid='"+mOrders.getMemberid()+"'");
					  listIdentity=mCommissionService.exeSelectSql(msql);
					  if(listIdentity.size()==0){
						  mCommission.setType(1);
					  }else{
						  if(listIdentity.get(0).get("TYPE").toString().equals("1")){
							  mCommission.setType(3);
						  }else if(listIdentity.get(0).get("TYPE").toString().equals("2")){
							  mCommission.setType(2);
						  }else{
							  mCommission.setType(4);
						  }
						} 
				  }
				 
				  
			  }
			  if(mCommission.getType()==1||mCommission.getType()==3){
				  msql.setSql("select memberid from shop where id="+mOrders.getShopid());
				  mCommission.setMemberidsu(Long.valueOf(mCommissionService.exeSelectSql(msql).get(0).get("MEMBERID").toString()));
			  }else if(mCommission.getType()==2){
				  msql.setSql("select memberid from Identity where id=(select suid from Identity where memberid="+
						  mOrders.getMemberid()
						  +")");
				  mCommission.setMemberidsu(Long.valueOf(mCommissionService.exeSelectSql(msql).get(0).get("MEMBERID").toString()));
			  }else if(mCommission.getType()==4){
				  mCommission.setMemberidsu(mCommission.getMemberid());
			  }else{
				  mCommission.setMemberidsu(mOrders.getMemberidsu());
			  }
			  
			  
				msql.setSql("select * from shop where memberid="+mCommission.getMemberidsu());
				listshop1=mCommissionService.exeSelectSql(msql);
				if(listshop1.size()!=0){
					 mCommission.setTypesu(4);
				}else{
					  //上一级身份
					  msql.setSql("select * from clerk where memberid='"+mCommission.getMemberidsu()+"'");
					  listIdentity=mCommissionService.exeSelectSql(msql);
					  if(listIdentity.size()==0){
						  mCommission.setTypesu(0);
					  }else{
						  msql.setSql("select * from Identity where memberid='"+mCommission.getMemberidsu()+"'");
						  listIdentity=mCommissionService.exeSelectSql(msql);
						  if(listIdentity.size()==0){
							  mCommission.setTypesu(1);
						  }else{
							  if(listIdentity.get(0).get("TYPE").toString().equals("1")){
								  mCommission.setTypesu(3);
							  }else if(listIdentity.get(0).get("TYPE").toString().equals("2")){
								  mCommission.setTypesu(2);
							  }else{
								  mCommission.setTypesu(4);
							  }
							}
						  
					  }
					 
				}
			
			{
				//单品减9，
				msql.setSql("select nvl(sum(num),0) count  from Orderrelevance where orderid="+mOrders.getId());
				mCommission.setCommoditynum(Integer.parseInt(mCommissionService.exeSelectSql(msql).get(0).get("COUNT").toString()));
				b=mOrders.getPayment().subtract(new BigDecimal(mCommission.getCommoditynum()*MyParameter.ORDERONESUB));
				if(mOrders.getShippingtype()==3){
					mCommission.setMemberid1(mOrders.getMemberidsu());
					//结算规则
					OrdersRule or;//结算规则
					  msql.setSql("select * from  (select * from OrdersRule where shoponeid=(select oneid from shop where id =  "+mOrders.getShopid()+" ) and nvl(type,1)=2 order by id desc)"
					   				+ "where rownum=1");
					  or =  IBeanUtil.Map2JavaBean(mCommissionService.exeSelectSql(msql).get(0),OrdersRule.class);
					
					 
					  if(listshop.size()==0){
						  mCommission.setNum1(or.getClerk());
						  mCommission.setNum4(or.getShop());
					  }else{
						  mCommission.setNum4(or.getShop().add(or.getClerk()));
					  }
					  mCommission.setOnenum(or.getShopone());
					  mCommission.setFid(or.getId());
					  
					  mCommission.setNumsu2(mCommission.getNum4());
					  
					  mCommission.setTypenum(1);
					  mCommission.setOrdersnum(mOrders.getPayment());
				}else{
					//b = b.multiply(new BigDecimal(9));
					if(istype1(mCommission)==1){
						//结算规则
						OrdersRule or;//结算规则
						  msql.setSql("select * from  (select * from OrdersRule where shoponeid=(select oneid from shop where id =  "+mOrders.getShopid()+" ) and nvl(type,1)=1 order by id desc)"
						   				+ "where rownum=1");
						  or =  IBeanUtil.Map2JavaBean(mCommissionService.exeSelectSql(msql).get(0),OrdersRule.class);
						 if(mCommission.getType()==0){//购买人是普通用户
							 if(mCommission.getTypesu()==1){//上一级是导购
								  mCommission.setNum1(or.getClerk());
								  mCommission.setNum4(or.getShop());
							 }else{//上一级是店铺
								 mCommission.setNum4(or.getShop().add(or.getClerk()));
							 }
							 
						 }else if(mCommission.getType()==1){//购买人是导购
							 mCommission.setNum1(or.getClerk());
							 mCommission.setNum4(or.getShop());
							 
						 }else{//购买人是店铺
							 mCommission.setNum4(or.getShop().add(or.getClerk()));
						 }
						  
//						  if(listshop1.size()==0){
//							  mCommission.setNum1(or.getClerk());
//							  mCommission.setNum4(or.getShop());
//						  }else{
//							  mCommission.setNum4(or.getShop().add(or.getClerk()));
//						  }
						  mCommission.setOnenum(or.getShopone());
						  mCommission.setFid(or.getId());
						  
//						  mCommission.setNumsu2(mCommission.getNum4());
						  
						  mCommission.setTypenum(1);
						  mCommission.setOrdersnum(mOrders.getPayment());
					}else{
						
						
						msql.setSql("select * from Identity where memberid="+mCommission.getMemberidsu());
						Identity mIdentity = IBeanUtil.Map2JavaBean(mCommissionService.exeSelectSql(msql).get(0), Identity.class);
						
						msql.setSql("select * from (select * from Programme order by id desc) where rownum=1");
						//结算规则
						Programme mProgramme = IBeanUtil.Map2JavaBean(mCommissionService.exeSelectSql(msql).get(0), Programme.class);
						 mCommission.setFid(mProgramme.getId());
						mCommission.setOnenum(mProgramme.getShopone());
						  mCommission.setFid(mProgramme.getId());
						  mCommission.setTypenum(1);
						  mCommission.setOrdersnum(mOrders.getPayment());
						if(mCommission.getType()==0){//普通用户购买
							if(mCommission.getTypesu()==2){//上一级是线上店主
								if(mIdentity.getType()==1){//线上店主上一级是经销商
									mCommission.setNum2(mProgramme.getOnlineshopkeeper());
									mCommission.setNum3(mProgramme.getSales());
									mCommission.setNum4(mProgramme.getShop());
								}else{//线上店主上一级是门店
									mCommission.setNum2(mProgramme.getOnlineshopkeeper());
									mCommission.setNum4(mProgramme.getShop().add(mProgramme.getSales()));
								}
							}else{
								//上一级是经销商
								mCommission.setNum3(mProgramme.getOnlineshopkeeper().add(mProgramme.getSales()));
								mCommission.setNum4(mProgramme.getShop());
							}
							
							
						}else if(mCommission.getType()==2){
							mCommission.setNum2(mProgramme.getOnlineshopkeeper());//购买人是线上店主
							if(mCommission.getTypesu()==3){//上一级是经销商
								mCommission.setNum3(mProgramme.getSales());
								mCommission.setNum4(mProgramme.getShop());
							}else{
								//上一级是店主
								mCommission.setNum4(mProgramme.getShop().add(mProgramme.getSales()));
							}
						}else{
							//购买人是经销商
							mCommission.setNum3(mProgramme.getOnlineshopkeeper().add(mProgramme.getSales()));
							mCommission.setNum4(mProgramme.getShop());
							
						}
						
					}
					
				}
			}
			
			mCommission.setNum1(mCommission.getNum1().multiply(b).divide(new BigDecimal(100)));
			mCommission.setNum2(mCommission.getNum2().multiply(b).divide(new BigDecimal(100)));
			mCommission.setNum3(mCommission.getNum3().multiply(b).divide(new BigDecimal(100)));
			mCommission.setNum4(mCommission.getNum4().multiply(b).divide(new BigDecimal(100)));
			mCommission.setOnenum(mCommission.getOnenum().multiply(b).divide(new BigDecimal(100)));
			
			
			
			
			mCommission.setState(1);
			if(mCommission.getTypesu()==1){
				mCommission.setMemberidsusu(mCommission.getMemberid4());
			}else if(mCommission.getTypesu()==2){
				if(mCommission.getMemberid3()==null){
					mCommission.setMemberidsusu(mCommission.getMemberid4());
				}else{
					mCommission.setMemberidsusu(mCommission.getMemberid3());
				}
//			}else if(mCommission.getTypesu()==3){
//				mCommission.setMemberidsusu(mCommission.getMemberid4());
			}else{
				mCommission.setMemberidsusu(mCommission.getMemberid4());
			}
			
			
//			if(mCommission.getMemberid4().toString().equals(mCommission.getMemberid3().toString())){
//				mCommission.setMemberid3(null);
//				mCommission.setNum4(mCommission.getNum4().add(mCommission.getNum3()));
//				mCommission.setNum3(new BigDecimal(0));
//			}
			
			msql.setSql("select id,memberid from shop where id="+mOrders.getShopid());//订单所属店铺
			listshop1=mCommissionService.exeSelectSql(msql);
			mCommission.setMemberid4(Long.valueOf(listshop1.get(0).get("MEMBERID").toString()));
			if(mCommission.getType()==1){//购买人是导购
				mCommission.setMemberid1(mOrders.getMemberid());
				mCommission.setNum1(mCommission.getNum1().add(b1));
				
			}else
			if(mCommission.getType()==2){//购买人是线上店主
				mCommission.setMemberid2(mOrders.getMemberid());
				mCommission.setNum2(mCommission.getNum2().add(b1));
				if(mCommission.getTypesu()==3){
					msql.setSql("select id,memberid from Identity where id=(select suid from Identity where memberid="+mOrders.getMemberid()+")");//线上店主上面的经销商
					mCommission.setMemberid3(Long.valueOf(mCommissionService.exeSelectSql(msql).get(0).get("MEMBERID").toString()));
				}
			}else
			if(mCommission.getType()==3){//购买人是经销商
				mCommission.setMemberid3(mOrders.getMemberid());
				mCommission.setNum3(mCommission.getNum3().add(b1));
			}else
			if(mCommission.getType()==4){//购买人是店主
				mCommission.setMemberid3(mOrders.getMemberid());
				mCommission.setNum4(mCommission.getNum4().add(b1));
			}else{//购买人是普通用户
				if(mCommission.getTypesu()==1){//上一级是导购
					mCommission.setMemberid1(mOrders.getMemberidsu());
					mCommission.setNum1(mCommission.getNum1().add(b1));
				} else if(mCommission.getTypesu()==2){
					mCommission.setNum2(mCommission.getNum2().add(b1));
					mCommission.setMemberid2(mOrders.getMemberidsu());
					
					msql.setSql("select memberid from Identity where id=(select suid from Identity where memberid="+mOrders.getMemberidsu()+")");
					List<Map<String, Object>>  listmap = mCommissionService.exeSelectSql(msql);
					if(listmap.size()!=0){
						//上一级是经销商
						mCommission.setMemberid3(Long.valueOf(listmap.get(0).get("MEMBERID").toString()));
					}
					
				} else if(mCommission.getTypesu()==3){//上一级是经销商
					mCommission.setMemberid3(mOrders.getMemberidsu());
					mCommission.setNum3(mCommission.getNum3().add(b1));
				} else {
					mCommission.setNum4(mCommission.getNum4().add(b1));
				}
			}
				

		 
			
			mCommission.setJtype(0);
//			if(mCommission.getMemberid1()==null)
//				mCommission.setNum1(MyParameter.mBigDecimal_0);
//			if(mCommission.getMemberid2()==null)
//				mCommission.setNum2(MyParameter.mBigDecimal_0);
//			if(mCommission.getMemberid3()==null)
//				mCommission.setNum3(MyParameter.mBigDecimal_0);
			
			if(mCommission.getMemberid3()!=null){
				if(mCommission.getMemberid3().toString().equals(mCommission.getMemberid4().toString())){
					mCommission.setMemberid3(null);
				}
			}

			mCommission=exec_gd(mCommission);
			
			mCommission.setNumsu1(mCommission.getNum3());;//为经销商贡献了多少钱
			mCommission.setNumsu2(mCommission.getNum4());;//为门店店主贡献了了多少钱
			
			if(mCommission.getMemberid1()==null)mCommission.setNum1(MyParameter.mBigDecimal_0);
			if(mCommission.getMemberid2()==null)mCommission.setNum2(MyParameter.mBigDecimal_0);
			if(mCommission.getMemberid3()==null)mCommission.setNum3(MyParameter.mBigDecimal_0);
			if(mCommission.getMemberid4()==null)mCommission.setNum4(MyParameter.mBigDecimal_0);
			synchronized (this) {
				while (true) {
					long index =System.currentTimeMillis();

					try {
						mCommissionService.add(mCommission);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						index=index+1;
						mCommission.setId(index);
					}
					
				}
			}
		
		
		
//			mCommissionService.add(mCommission);
			if(MyParameter.Home_name.equals( InetAddress.getLocalHost().getHostName())){
			msql.setSql("update orders set status=2 where status=1 and id="+mCommission.getOrdersid());
			mCommissionService.execSQL(msql);
			}
		}
		
		
		

	}

	private Commission exec_gd(Commission mCommission) throws Exception {
		Sql msql = new Sql();
		msql.setRows(1);
		msql.setSql("select * from gd where shoponeid="+mCommission.getMemberone()+" order by id desc");
		List<Gd> listgd =IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Gd.class);
		if(listgd.size()!=0){
			  mCommission.setFid(null);
			  mCommission.setNum1(MyParameter.mBigDecimal_0);
			  mCommission.setNum2(MyParameter.mBigDecimal_0);
			  mCommission.setNum3(MyParameter.mBigDecimal_0);
			  mCommission.setNum4(MyParameter.mBigDecimal_0);
			  mCommission.setOnenum(MyParameter.mBigDecimal_0);
			msql.setSql("select * from Orderrelevance where orderid="+mCommission.getOrdersid());
			msql.setRows(null);
			List<Orderrelevance> listOrderrelevance = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Orderrelevance.class);
			msql.setRows(1);
			for (Orderrelevance orderrelevance : listOrderrelevance) {
				msql.setSql("select * from Gd_Commodity_key where youcode='"+orderrelevance.getYoucode()+"' order by id desc");
				List<Gd_Commodity_key> listGd_Commodity_key = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Gd_Commodity_key.class);
				if(listGd_Commodity_key.size()==0){
					mCommission.setNum1(mCommission.getNum1().add(listgd.get(0).getClerk()));
					mCommission.setNum2(mCommission.getNum2().add(listgd.get(0).getXian_shang_dian_zhu()));
					mCommission.setNum3(mCommission.getNum3().add(listgd.get(0).getJing_xiao_shang()));
					mCommission.setNum4(mCommission.getNum4().add(listgd.get(0).getShop()));
					mCommission.setOnenum(mCommission.getOnenum().add(listgd.get(0).getShopone()));
					msql.setSql("update orderrelevance set Gdf="+Orderrelevance_gdf.DEFAULT.getKey() +" where id="+orderrelevance.getId());
				}else{
					mCommission.setNum1(mCommission.getNum1().add(listGd_Commodity_key.get(0).getClerk()));
					mCommission.setNum2(mCommission.getNum2().add(listGd_Commodity_key.get(0).getXian_shang_dian_zhu()));
					mCommission.setNum3(mCommission.getNum3().add(listGd_Commodity_key.get(0).getJing_xiao_shang()));
					mCommission.setNum4(mCommission.getNum4().add(listGd_Commodity_key.get(0).getShop()));
					mCommission.setOnenum(mCommission.getOnenum().add(listGd_Commodity_key.get(0).getShopone()));
					msql.setSql("update orderrelevance set Gdf="+Orderrelevance_gdf.key.getKey() +" where id="+orderrelevance.getId());
				}
			}
		}
		return mCommission;
	}
	
}
