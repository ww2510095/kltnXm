package com.bm.stock;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
 
@Service
public class StockService extends BaseService{
	
	@Override
	public String getTabName() {return "Stock";}
   
}
