package com.bm.commodity.back;

import com.bm.commodity.Commodity;
import com.bm.stock.Stock;

public class CK {
	
	private Commodity mCommodity;
	private Stock mStock;
	public Commodity getmCommodity() {
		return mCommodity;
	}
	public void setmCommodity(Commodity mCommodity) {
		this.mCommodity = mCommodity;
	}
	public Stock getmStock() {
		return mStock;
	}
	public void setmStock(Stock mStock) {
		this.mStock = mStock;
	}
	@Override
	public String toString() {
		return "CKmCommodity" + mCommodity + "1mStock" + mStock;
	}
	
	

}
