package com.bm.orders.orderrelevance;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class OrderrelevanceService extends BaseService{

	@Override
	protected String getTabName() {
		return "Orderrelevance";
	}
	@Override
	public int add(Object obj) throws Exception {
//		deleteRedisKey("Commodity");
		return super.add(obj);
	}
	
//	@Override
//	public List<?> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows)
//			throws Exception {
//		Sql msql = new Sql();
//		msql.setSql("select Orderrelevance.*,Commodity.youcode from Orderrelevance left join Commodity on Commodity.id=Orderrelevance.itemid "+ getWhere(t));
//		msql.setRows(rows);
//		msql.setPage(page);
//		msql.setOrderbykey(orderbykey);
//		msql.setOrderbytype(orderbytype);
//		return IBeanUtil.ListMap2ListJavaBean(exeSelectSql(msql), t.getClass());
//	}
	

}
