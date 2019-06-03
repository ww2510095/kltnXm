package com.bm.user.goldcoin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.Sql;
 
@Service
public class GoldcoinVService extends BaseService{
	
	
	@Override
	public String getTabName() {return "Goldcoinv";}
	
	public int add(GoldcoinV obj) throws Exception {
		if(obj.getNum()==null||obj.getNum()==0)return 1;
		if(obj.getIstrue()==null)obj.setIstrue(1);
		if(obj.getIstrue()==1){
			Sql msql = new Sql();
			msql.setSql("update member set goldcoin = goldcoin + "+obj.getNum()+" where id="+obj.getMemberid());
			execSQL(msql);
		}
		return super.add(obj);
	}

	public String updateBySelect(GoldcoinV obj) throws Exception {
		if(obj.getIstrue()!=null){
			if(obj.getIstrue()==1){
				Sql msql = new Sql();
				msql.setSql("update member set goldcoin = goldcoin + (select sum(num) from GoldcoinV where b_y='"+obj.getB_y()+"') where id="+obj.getMemberid());
				execSQL(msql);
			}
		}
	
		return super.updateBySelect(obj);
	}
	
	public	void updateByordersID(String ordersid) throws Exception{
		GoldcoinV mGoldcoinV= new GoldcoinV();
		mGoldcoinV.setB_y(ordersid);
		@SuppressWarnings("unchecked")
		List<GoldcoinV> listGoldcoinV = (List<GoldcoinV>) getALL(mGoldcoinV);
		for (GoldcoinV goldcoinV : listGoldcoinV) {
			goldcoinV.setIstrue(1);
			updateBySelect(goldcoinV);
		}
	}
}
