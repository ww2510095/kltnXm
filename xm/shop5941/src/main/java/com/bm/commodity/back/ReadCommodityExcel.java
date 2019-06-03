package com.bm.commodity.back;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bm.commodity.Commodity;
import com.bm.stock.Stock;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

public class ReadCommodityExcel {

	public List<Commodityback> readCommodity(List<List<String>> data,String uname) {
		List<Commodityback> commoditys = new ArrayList<Commodityback>();
		for (List<String> list : data) {
			Commodityback mmCommodity = new Commodityback();
			mmCommodity.setMycode(System.currentTimeMillis() + ""); // 自己的条码
			mmCommodity.setLargeclass(list.get(1)); // 大类
			mmCommodity.setInclass(list.get(2)); // 中类
			mmCommodity.setSmallclass(list.get(3)); // 小类
			mmCommodity.setFineclass(list.get(4)); // 细类
			mmCommodity.setName(list.get(5)); // 商品名字
			mmCommodity.setSupplier(list.get(6)); // 供应商
			mmCommodity.setBrand(list.get(7)); // 品牌
			mmCommodity.setIntroduction(list.get(8)); // 商品简介
			mmCommodity.setDetailed(list.get(9)); // 详细介绍
			mmCommodity.setYoucode(list.get(10)); // 别人的条码
			mmCommodity.setCompany(list.get(11)); // 计量单位
			mmCommodity.setPackingmethod(list.get(12)); // 包装方式
			mmCommodity.setOriginalprice(list.get(13)); // 原价
			mmCommodity.setPrice(list.get(14)); // 售价
			mmCommodity.setCostprice(list.get(15)); // 成本价
			mmCommodity.setColour(list.get(16)); // 颜色
			mmCommodity.setMysize(list.get(17)); // 尺码
			mmCommodity.setSpecifications(list.get(18).equals("尺码")?"0":"1"); //选择规格还是 尺码
			mmCommodity.setMainimage(list.get(19)); // 主图数组
			mmCommodity.setDetailsimage(list.get(20)); // 详情图数组
			mmCommodity.setAdminphone(uname);
			commoditys.add(mmCommodity);
		}

		return commoditys;
	}
	public List<Commodity> readCommodityadmin(List<List<String>> data) {
		List<Commodity> commoditys = new ArrayList<Commodity>();
		int i;
		for (List<String> list : data) {
			if(list.size()==2) continue;
			Commodity mmCommodity = new Commodity();
			 i=1;
			mmCommodity.setMycode(System.currentTimeMillis() + ""); // 自己的条码
			mmCommodity.setLargeclass(list.get(i));				i++; // 大类
			mmCommodity.setInclass(list.get(i)); 				i++;// 中类
			mmCommodity.setSmallclass(list.get(i));				i++; // 小类
			mmCommodity.setFineclass(list.get(i)); 				i++;// 细类
			mmCommodity.setName(list.get(i)); 					i++;// 商品名字
			mmCommodity.setSupplier(list.get(i)); 				i++;// 供应商
			mmCommodity.setSuppliername(list.get(i));			i++;//供应商名字
			mmCommodity.setBrand(list.get(i));					i++; // 品牌
			if("空".equals(list.get(i))) 
				mmCommodity.setIntroduction("");
			else
				mmCommodity.setIntroduction(list.get(i));			
			
			i++; // 商品简介
			mmCommodity.setDetailed(list.get(i)); 				i++;// 详细介绍
//			mmCommodity.setYoucode(list.get(i)); 				i++;// 别人的条码
			mmCommodity.setCompany(list.get(i)); 				i++;// 计量单位
			mmCommodity.setPackingmethod(list.get(i));			i++; // 包装方式
//			mmCommodity.setOriginalprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 原价
//			mmCommodity.setPrice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 售价
//			mmCommodity.setCostprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 成本价
//			mmCommodity.setColour(list.get(i)); 				i++;// 颜色
//			mmCommodity.setMysize(list.get(i)); 				i++;// 尺码
			mmCommodity.setSpecifications(list.get(i).equals("尺码")?"0":"1");i++; // 选择规格还是尺码
			mmCommodity.setCode(list.get(i));i++; // 助记码
			mmCommodity.setManufactor(list.get(i));i++; // 厂家
			try {
				mmCommodity.setMainimage(list.get(i)); 				i++;// 主图数组
			} catch (Exception e) {
				System.out.println(list);
			}
			
//			mmCommodity.setDetailsimage(list.get(i));			i++; // 详情图数组
			commoditys.add(mmCommodity);
		}
		
		return commoditys;
	}
	/**
	 * 商品
	 * */
	public List<Commodity> readCommodityadmin2(List<List<String>> data) {
		List<Commodity> commoditys = new ArrayList<Commodity>();
		int i;
		Long mycode=System.currentTimeMillis();
		for (List<String> list : data) {
			if(list.size()==2) continue;
			Commodity mmCommodity = new Commodity();
			mmCommodity.setMycode(mycode+"");
			i=0;
			for (Commodity mCommodity : commoditys) {
				if(mCommodity.getMainimage().equals(list.get(i)))
					continue;
			}
			mmCommodity.setMainimage(list.get(i));i++; // 自己的条码
			mmCommodity.setLargeclass(list.get(i));				i++; // 大类
			mmCommodity.setInclass(list.get(i)); 				i++;// 中类
			mmCommodity.setSmallclass(list.get(i));				i++; // 小类
			mmCommodity.setFineclass(list.get(i)); 				i++;// 细类
			mmCommodity.setName(list.get(i)); 					i++;// 商品名字
			mmCommodity.setSupplier(list.get(i)); 				i++;// 供应商
			mmCommodity.setSuppliername(list.get(i));			i++;//供应商名字
			mmCommodity.setBrand(list.get(i));					i++; // 品牌
			if("空".equals(list.get(i))) 
				mmCommodity.setIntroduction("");
			else
				mmCommodity.setIntroduction(list.get(i));			
			
			i++; // 商品简介
			mmCommodity.setDetailed(list.get(i)); 				i++;// 详细介绍
			mmCommodity.setCompany(list.get(i)); 				i++;// 计量单位
			mmCommodity.setPackingmethod(list.get(i));			i++; // 包装方式
			mmCommodity.setSpecifications(list.get(i).equals("尺码")?"0":"1");i++; // 选择规格还是尺码
			mmCommodity.setCode(list.get(i));i++; // 助记码
			mmCommodity.setManufactor(list.get(i));i++; // 厂家
			mmCommodity.setDetailsimage(i+"");
			commoditys.add(mmCommodity);
		}
		
		return commoditys;
	}
	public List<Commodity> readSpecifications(List<List<String>> data) {
		List<Commodity> commoditys = new ArrayList<Commodity>();
		List<String> youcode = new LinkedList<String>();
		int i;
		for (List<String> list : data) {
			if(list.size()==2) continue;
			Commodity mmCommodity = new Commodity();
			i=0;
			
			mmCommodity.setCommoditykeyid(list.get(i));			i++;//商品id
			youcode.add(list.get(i).trim());
			mmCommodity.setYoucode(list.get(i).trim()); 				i++;// 别人的条码
			mmCommodity.setOriginalprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 原价
			mmCommodity.setPrice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 售价
			mmCommodity.setCostprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 成本价
			mmCommodity.setColour(list.get(i)); 				i++;// 颜色
			mmCommodity.setMysize(list.get(i)); 				i++;// 尺码
			commoditys.add(mmCommodity);
		}
		if(youcode.size()!=commoditys.size())
			throw new RunException("错误，有重复条码");
		
		return commoditys;
	}
	/**2.0的导入功能，所有数据一起导入*/
	public List<CK> readSpecifications2(List<List<String>> data,List<Commodity> lista) {
		if(lista.size()==0)throw new RunException("错误，没有商品");
		int index=Integer.parseInt(lista.get(0).getDetailsimage());
		List<CK> listck = new ArrayList<>();
//		List<Commodity> commoditys = new ArrayList<Commodity>();
		List<String> youcode = new LinkedList<String>();
		int i;
		for (List<String> list : data) {
			if(list.size()==2) continue;
			CK mck=new CK();
			Commodity mmCommodity = new Commodity();
			Stock mstock = new Stock();
			for (Commodity mCommodity : lista) {
				if(list.get(0).equals(mCommodity.getMainimage())){
					mmCommodity.setCommoditykeyid(mCommodity.getId().toString());
					mmCommodity.setSupplier(mCommodity.getSupplier());
					break;
				}
			}
			i=index;
			
//			mmCommodity.setCommoditykeyid(list.get(i));			i++;//商品id
			youcode.add(list.get(i).trim());
			mmCommodity.setYoucode(list.get(i).trim()); 				i++;// 别人的条码
			mmCommodity.setOriginalprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 原价
			mmCommodity.setPrice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 售价
			mmCommodity.setCostprice(new BigDecimal(Stringutil.isBlank(list.get(i))?"0":list.get(i))); i++;// 成本价
			mmCommodity.setColour(list.get(i)); 				i++;// 颜色
			mmCommodity.setMysize(list.get(i)); 				i++;// 尺码
			
			mstock.setCode(mmCommodity.getYoucode());
			mstock.setNum(Integer.valueOf(list.get(i)));
			mstock.setType(1);
			
			mck.setmCommodity(mmCommodity);
			mck.setmStock(mstock);
			listck.add(mck);
//			commoditys.add(mmCommodity);
		}
		if(youcode.size()!=listck.size())
			throw new RunException("错误，有重复条码");
		
		return listck;
	}
}
