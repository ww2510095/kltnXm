package com.bm.returngoods;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bm.Aenum.Orderrelevance_gdf;
import com.bm.base.BaseService;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.util.IBeanUtil;
import com.bm.clerk.commission.Commission;
import com.bm.clerk.commission.CommissionService;
import com.bm.orders.orderrelevance.Orderrelevance;
import com.bm.ordersRule.OrdersRule;
import com.bm.ordersRule.Programme;
import com.bm.ordersRule.gd.Gd;
import com.bm.ordersRule.gd.Gd_Commodity_key;
import com.myjar.desutil.RunException;

@Service
public class ReturngoodsService extends BaseService{

	@Autowired
	private CommissionService mCommissionService;
	
	@Override
	protected String getTabName() {
		return "Returngoods";
	}

	
	public String updateBySelect(Returngoods obj) throws Exception {
		Long od=obj.getId();
		
		if(obj.getIstrue()!=null&&obj.getIstrue()==1){
			obj=getById(obj,Returngoods.class);
			if(obj.getIstrue()==1)
			throw new RunException("操作错误");
			Sql msql = new Sql();
			msql.setSql("select * from Commission where ordersid=(select orderid from Orderrelevance where id="+obj.getOrderrelevanceid()+")");
			Commission mCommission;
			try {
				 mCommission = IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0), Commission.class);
			} catch (Exception e) {
				msql.setSql("select * from Commission where ordersid=(select orderid from orderrelevance where id=(select orderrelevanceid from Returngoods where id="+obj.getId()+"))");
				 mCommission = IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0), Commission.class);
			}
			
			if(obj.getA()==mCommission.getCommoditynum()){
				msql.setSql("delete Commission where id="+mCommission.getId());
				execSQL(msql);
				obj.setIstrue(1);
				return super.updateBySelect(obj);
			}
			if(mCommission.getFid()==null){
				msql.setSql("select * from Orderrelevance where id="+obj.getOrderrelevanceid());
				Orderrelevance mOrderrelevance = IBeanUtil.Map2JavaBean(mCommissionService.exeSelectSql(msql).get(0), Orderrelevance.class);
			if(mOrderrelevance.getGdf()==Orderrelevance_gdf.DEFAULT.getKey()){
				msql.setSql("select * from gd where shoponeid="+mCommission.getMemberone()+" and id<"+mCommission.getOrdersid()+" order by id desc");
				List<Gd> listgd =IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Gd.class);
				mCommission.setNum1(mCommission.getNum1().subtract(listgd.get(0).getClerk()));
				mCommission.setNum2(mCommission.getNum2().subtract(listgd.get(0).getXian_shang_dian_zhu()));
				mCommission.setNum3(mCommission.getNum3().subtract(listgd.get(0).getJing_xiao_shang()));
				mCommission.setNum4(mCommission.getNum4().subtract(listgd.get(0).getShop()));
				mCommission.setOnenum(mCommission.getOnenum().subtract(listgd.get(0).getShopone()));
			}else{
				msql.setSql("select * from Gd_Commodity_key where youcode="+mOrderrelevance.getYoucode()+" and id<"+mCommission.getOrdersid()+"  order by id desc");
				List<Gd_Commodity_key> listGd_Commodity_key = IBeanUtil.ListMap2ListJavaBean(mCommissionService.exeSelectSql(msql), Gd_Commodity_key.class);
				mCommission.setNum1(mCommission.getNum1().subtract(listGd_Commodity_key.get(0).getClerk()));
				mCommission.setNum2(mCommission.getNum2().subtract(listGd_Commodity_key.get(0).getXian_shang_dian_zhu()));
				mCommission.setNum3(mCommission.getNum3().subtract(listGd_Commodity_key.get(0).getJing_xiao_shang()));
				mCommission.setNum4(mCommission.getNum4().subtract(listGd_Commodity_key.get(0).getShop()));
				mCommission.setOnenum(mCommission.getOnenum().subtract(listGd_Commodity_key.get(0).getShopone()));
			}
				
			}else{
				boolean b=false;
				BigDecimal b1;
				if(mCommission.getNum2().compareTo(new BigDecimal(0))==0&&mCommission.getNum3().compareTo(new BigDecimal(0))==0){
					msql.setSql("select * from OrdersRule where id="+mCommission.getFid());
					OrdersRule or = IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0), OrdersRule.class);
					if(or.getType()!=null&&or.getType()==2){
						b=true;
						b1=obj.getPrice();
					}else{
						b1=obj.getRefund().subtract(new BigDecimal(obj.getA()*MyParameter.ORDERONESUB));
					}
					if(mCommission.getNum1().doubleValue()>0){
						mCommission.setNum1(mCommission.getNum1().subtract(or.getClerk().multiply(b1).divide(new BigDecimal(100))));
						mCommission.setNum4(mCommission.getNum4().subtract(or.getShop().multiply(b1).divide(new BigDecimal(100))));
					}else{
						mCommission.setNum4(mCommission.getNum4().subtract(or.getShop().add(or.getClerk()).multiply(b1).divide(new BigDecimal(100))));
					}
					
					mCommission.setOnenum(mCommission.getOnenum().subtract(or.getShopone().multiply(b1).divide(new BigDecimal(100))));
					
				}else{
					b1=obj.getRefund();
					b1=b1.subtract(new BigDecimal(obj.getA()*MyParameter.ORDERONESUB));
					
					msql.setSql("select * from Programme where id="+mCommission.getFid());
					Programme mProgramme = IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0), Programme.class);
					if(mCommission.getNum2().doubleValue()>0){
						mCommission.setNum2(mCommission.getNum2().subtract(mProgramme.getOnlineshopkeeper().multiply(b1).divide(new BigDecimal(100))));
						if(mCommission.getNum3().doubleValue()>0){
							mCommission.setNum3(mCommission.getNum3().subtract(mProgramme.getSales().multiply(b1).divide(new BigDecimal(100))));
							mCommission.setNum4(mCommission.getNum4().subtract(mProgramme.getShop().multiply(b1).divide(new BigDecimal(100))));
						}else{
							mCommission.setNum4(mCommission.getNum4().subtract(mProgramme.getSales().add(mProgramme.getShop()).multiply(b1).divide(new BigDecimal(100))));
						}
						
					}else if(mCommission.getNum3().doubleValue()>0){
						mCommission.setNum3(mCommission.getNum3().subtract(mProgramme.getSales().multiply(b1).divide(new BigDecimal(100))));
						mCommission.setNum4(mCommission.getNum4().subtract(mProgramme.getShop().add(mProgramme.getOnlineshopkeeper()).multiply(b1).divide(new BigDecimal(100))));
					}
					
					
					mCommission.setOnenum(mCommission.getOnenum().subtract(mProgramme.getShopone().multiply(b1).divide(new BigDecimal(100))));
				}
				if(!b){
					b1 = new BigDecimal(obj.getA()*MyParameter.ORDERONEADD);
					msql.setSql("select num from Orderrelevance where id='"+obj.getOrderrelevanceid()+"'");
					if(exeSelectSql(msql).get(0).get("NUM").toString().equals(obj.getA().toString()))
						b1=b1.subtract(new BigDecimal(MyParameter.ORDERONEADD));
					
					if(mCommission.getType()==1){//购买人是导购
						mCommission.setNum1(mCommission.getNum1().subtract(b1));
						
					}else
					if(mCommission.getType()==2){//购买人是线上店主
						mCommission.setNum2(mCommission.getNum2().subtract(b1));
						
					}else
					if(mCommission.getType()==3){//购买人是经销商
						mCommission.setNum3(mCommission.getNum3().subtract(b1));
					}else
					if(mCommission.getType()==4){//购买人是店主
						mCommission.setNum4(mCommission.getNum4().subtract(b1));
					}else{//购买人是普通用户
						if(mCommission.getTypesu()==1){//上一级是导购
							mCommission.setNum1(mCommission.getNum1().subtract(b1));
						} else if(mCommission.getTypesu()==2){
							mCommission.setNum2(mCommission.getNum2().subtract(b1));
							
						} else if(mCommission.getTypesu()==3){
							mCommission.setNum3(mCommission.getNum3().subtract(b1));
							
						} else{
							mCommission.setNum4(mCommission.getNum4().subtract(b1));
							
						} 
					}
				}
			}
			
			mCommissionService.updateBySelect(mCommission);
			obj = new Returngoods();
			obj.setId(od);
			obj.setIstrue(1);; //是否同意0:未处理，-1，不同意，1已同意,2:退仓，3：仓库收货
		}
		return super.updateBySelect(obj);
	}
}
