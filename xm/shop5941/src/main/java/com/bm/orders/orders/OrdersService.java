package com.bm.orders.orders;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bm.CommissionTask;
import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.clerk.commission.CommissionService;
import com.bm.user.goldcoin.GoldcoinVService;

@Service
public class OrdersService extends BaseService{

	@Autowired
	private OrdersDao mOrdersDao;
	@Autowired
	private CommissionService mCommissionService;
	@Autowired
	private GoldcoinVService mValue;
	
	@Override
	protected String getTabName() {
		return "Orders";
	}

	public List<Map<String, Object>> getNumberByRroupByOrders(String ids){
		return mOrdersDao.getNumberByRroupByOrders(ids);
	}
	public List<Map<String, Object>> getOrdersBystatus(){
		return mOrdersDao.getOrdersBystatus();
	}

	public int getNotTaskSize() {
		return mOrdersDao.getNotTaskSize();
	}

	public List<String> getsupplierbyids(String substring) {
		return mOrdersDao.getsupplierbyids(substring);
	}
	public void updateBySelect(Orders or,int i) throws Exception {
		or.setOrdernumber(null);
		if(or.getStatus()!=null){
			Orders or1 = getById(or,Orders.class);
			String str = or1.getTrajectory();
			Sql msql = new Sql();
			switch (or.getStatus()) {
			case 2:
				str=str+System.currentTimeMillis()+";买家已付款;";
				//订单支付时间
				or.setPaymenttime(System.currentTimeMillis());
				CommissionTask.start(mCommissionService, or.getId().toString(), CommissionTask.COMMISSION_1);
				
				msql.setSql("update GOLDCOINV set ISTRUE=1 where b_y='"+or1.getId()+"'");
				execSQL(msql);
				
				break;
			case 3:
				if(i==0)
					str=str+System.currentTimeMillis()+";订单已发货;";
				else
					str=str+System.currentTimeMillis()+";订单超时，系统自动发货;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 4:
				if(i==0)
					str=str+System.currentTimeMillis()+";订单已完成;";
				else
					str=str+System.currentTimeMillis()+";订单超时，系统自动签收;";
				//订单完成时间
				or.setEndtime(System.currentTimeMillis());
				CommissionTask.start(mCommissionService, or.getId().toString(), CommissionTask.COMMISSION_2);
				
				
				//积分
				mValue.updateByordersID(or.getId().toString());
				break;
			case 5:
				str=str+System.currentTimeMillis()+";订单已关闭;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 6:
				str=str+System.currentTimeMillis()+";订单已到门店;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 7:
				str=str+System.currentTimeMillis()+";订单已出仓;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 8:
				if(i==0)
					str=str+System.currentTimeMillis()+";商家已接单;";
				else
					str=str+System.currentTimeMillis()+";订单超时，系统自动接单;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 9:
				str=str+System.currentTimeMillis()+";商家已拒单;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 10:
				str=str+System.currentTimeMillis()+";用户申请取消订单;";
				or.setUpdatetime(System.currentTimeMillis());
				
				CommissionTask.start(mCommissionService, or.getId().toString(), CommissionTask.COMMISSION_RETURN);
				break;
			case 11:
				str=str+System.currentTimeMillis()+";订单已退款;";
				or.setUpdatetime(System.currentTimeMillis());
				
				msql.setSql("delete Commission where ordersid="+or.getId());
				execSQL(msql);
				break;
				
				
				
				
				
			case 20:
				str=str+System.currentTimeMillis()+";店铺拒绝退货,原因:"+or.getTrajectory()+";";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 21:
				str=str+System.currentTimeMillis()+";用户申请退货,原因:"+or.getBuyermesege()+";";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 22:
				str=str+System.currentTimeMillis()+";店铺同意退货;";
				or.setUpdatetime(System.currentTimeMillis());
				CommissionTask.start(mCommissionService, or.getId().toString(), CommissionTask.COMMISSION_1);
				break;
			case 23:
				str=str+System.currentTimeMillis()+";订单已退仓;";
				or.setUpdatetime(System.currentTimeMillis());
				break;
			case 24:
				str=str+System.currentTimeMillis()+";仓库已验收;";
				or.setUpdatetime(System.currentTimeMillis());
				break;

			default:
				break;
			}
			or.setTrajectory(str);	
		}
		
		if(or.getAutosystem()!=null){
			if(or.getAutosystem()==1){
				or.setTrajectory(or.getTrajectory()+System.currentTimeMillis()+";管理员已退款;");
			}
		}
		super.updateBySelect(or);
	
	}
	public void updateBySelect(Orders or) throws Exception {
		updateBySelect(or, 0);
	}
}
