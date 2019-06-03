package com.bm.orders.orders;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.SelectProvider;

public interface OrdersDao {
	
	@SelectProvider(type = OrdersDaoImp.class,method = "getNumberByRroupByOrders")
	public List<Map<String, Object>> getNumberByRroupByOrders(String ids);
	@SelectProvider(type = OrdersDaoImp.class,method = "getOrdersBystatus")
	public List<Map<String, Object>> getOrdersBystatus();
	@SelectProvider(type = OrdersDaoImp.class,method = "getNotTaskSize")
	public int getNotTaskSize();
	@SelectProvider(type = OrdersDaoImp.class,method = "getsupplierbyids")
	public List<String> getsupplierbyids(String substring);

}
