package com.bm.orders.orders;

public class OrdersDaoImp {
	
	public String getNumberByRroupByOrders(String ids){
		return "select sum(oldprice*(discount/10)) a ,shoptype from CommodityMessage where id in("
				+ids+") group by shoptype";
	}
	public String getsupplierbyids(String ids){
		return "select DISTINCT supplier from Commodity where id in ("+ids+")";
	}
	public String getOrdersBystatus(){
		return "select * "
				+ "from orders "
				+ "left join shop "
				+ " on shopid = shop.id "
				+ "where autosystem = '0' "
				+ "and status = 4";
	}
	public String getNotTaskSize(){
		return "select count(*) from orders where autosystem='0' status = 4";
	}

}
