package com.bm.commodity;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bm.ordersRule.gd.GdService;
import com.bm.ordersRule.gd.Gd_Commodity_key;
import com.bm.ordersRule.gd.Gd_Commodity_keyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.auths.MemberAuths;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ZIP;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.FileUtil.FileBen;
import com.bm.collection.Collection;
import com.bm.file.FileService;
import com.bm.stock.Stock;
import com.bm.stock.StockService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import lombok.Data;



@RestController
@Api(tags = "商品")
public class CommodityController extends BaseController{
	@Autowired
	private CommodityService commodityService;
	@Autowired
	private SpecificationsService mSpecificationsService;
	@Autowired
	private StockService mStockService;
	@Autowired
	private Gd_Commodity_keyService mGd_Commodity_keyService;


	
//	private final String leftjoinend=") A left join (SELECT SUM(NUM) NUM, CODE FROM Stock where type=1  GROUP BY CODE )  B on A.youcode=B.CODE";
	
	/**
	 * 查询所有商品
	 * */
	@RequestMapping(value ="/commodity/selectAll", method = RequestMethod.POST) 
	public RequestType selectAll(String orderbykey,Integer orderbytype,Integer page ,Integer rows) throws Exception{
		Sql msql =new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey(orderbykey);
		msql.setOrderbytype(orderbytype);
//		msql.setSql("select nvl(Num,0)Num,A.* from ("+Sql.page_rows("select rownum a1,commodity.* from commodity ", page, rows, orderbykey, orderbytype)+leftjoinend);
		msql.setSql("select * from commoditykey ");
	
//		msql.setPage(null);
//		msql.setRows(null);
//		msql.setOrderbykey(null);
//		msql.setOrderbytype(0);
		return sendTrueData(commodityService.exeSelectSql(msql));
	}
	/**
	 * 删除商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/delete", method = RequestMethod.POST) 
	public RequestType delete(String commoditykeyid) throws Exception{
		try {
			commodityService.deleteByid(commoditykeyid);
		} catch (Exception e) {
			return sendTrueMsg("删除错误，已找到子记录，需要先删除库存，再删除规格，最后删除商品，且商品必须是未卖出过的商品");
		}
		
		return sendTrueMsg("删除成功");
	}
	/**
	 * 商品搜索，根据名字模糊解锁
	 * */
	@RequestMapping(value ="/commodity/selectByName", method = RequestMethod.POST) 
	public RequestType selectByName(String name,String orderbykey,Integer orderbytype,Integer page ,Integer rows) throws Exception{
		Sql msql =new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		if(orderbytype!=null&&orderbytype==2){
			//活动商品
			msql.setSql("select distinct mainimage,commoditykeyid,largeclass,inclass,smallclass,fineclass,name,supplier,company,packingmethod,originalprice,price from commodity where  NVL(TYPE,0)=1 AND name like '%"+name+"%' and youcode in (select commodityname from Promotion where id >"+System.currentTimeMillis()+")");
		}else{
			msql.setOrderbykey(orderbykey);
			msql.setOrderbytype(orderbytype);
//			msql.setSql("select nvl(Num,0)Num,A.* from ("+Sql.page_rows("select rownum a1,commodity.* from commodity where name like '%"+name+"%'", page, rows, orderbykey, orderbytype)+leftjoinend);
			msql.setSql("select distinct mainimage,commoditykeyid,largeclass,inclass,smallclass,fineclass,name,supplier,company,packingmethod,originalprice,price from commodity where name like '%"+name+"%' AND NVL(TYPE,0)=1");
		}
		List<Map<String, Object>> listmap = commodityService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			map.put("MAINIMAGE", map.get("MAINIMAGE").toString().split(";")[0]);
		}
		return sendTrueData(listmap);
	}
	/**
	 * 商品搜索，根据条件解锁
	 * */
	@RequestMapping(value ="/commodity/selectBySelect", method = RequestMethod.POST) 
	public RequestType selectBySelect(String uname,Commodity mcommodity,String orderbykey,Integer orderbytype,Integer page ,Integer rows,String mk) throws Exception{
		List<Map<String, Object>> list =  commodityService.getALL(mcommodity, orderbykey, orderbytype, page, rows);
		if(list.size()==0&&!Stringutil.isBlank(mcommodity.getIndexs())){
			mcommodity.setLargeclass(mcommodity.getIndexs());
			mcommodity.setIndexs(null);
			list =  commodityService.getALL(mcommodity, orderbykey, orderbytype, page, rows);
		}

		for (Map<String, Object> map : list) {
			map.put("MAINIMAGE", map.get("MAINIMAGE").toString().split(";")[0]);
		}
		if("mk".equals(mk)){
			List<Mk> listkm = new ArrayList<Mk>();
			Mk mmk = new Mk();
			mmk.setMk(mcommodity.getLargeclass());
			mmk.setListmap(list);
			listkm.add(mmk);
			return sendTrueData(listkm);

		}
		return sendTrueData(list);
	}
	/**
	 * 商品搜索，根据条件解锁
	 * */
	@Auth()
	@ApiOperation(value = "后台的商品搜索，可以所有条件联合解锁", response = RequestType.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "UID", value = "设备号", dataType = "String", required = true ,paramType="query"),
			@ApiImplicitParam(name = "uname", value = "账号", dataType = "String", required = true ,paramType="query"),
	})
	@RequestMapping(value ="/commodity/selectBySelect2", method = RequestMethod.POST) 
	public RequestType selectBySelect2(String uname,Commodity mcommodity,String orderbykey,Integer orderbytype,Integer page ,Integer rows,String mk) throws Exception{
		rows=rows==null?10:rows;
		if(getMember(getLogin(uname)).getSuperadmin()!=1){
				mcommodity.setSupplier(uname);
		}
		RequestType mRequestType = selectBySelect(uname, mcommodity, orderbykey, orderbytype, page, rows, mk);
		List<Object> listobj = (List<Object>) mRequestType.getData();
		if(listobj.size()==0)return  mRequestType;

		List<Map<String, Object>> listmap;
		if("mk".equals(mk)){
			Mk mmk = (Mk) listobj.get(0);
			listmap=mmk.getListmap();
		}else{
			listmap= (List<Map<String, Object>>) mRequestType.getData();
		}
		for (Map<String, Object> mmap:listmap) {
			Gd_Commodity_key mGd_Commodity_key =mGd_Commodity_keyService.getByparameter("youcode",mmap.get("YOUCODE").toString(),Gd_Commodity_key.class);
			if(mGd_Commodity_key==null)mGd_Commodity_key=new Gd_Commodity_key();
			mmap.put("MYMONEY",mGd_Commodity_key);
		}
		if("mk".equals(mk)){
			List<Mk> listkm = new ArrayList<Mk>();
			Mk mmk = new Mk();
			mmk.setMk(mcommodity.getLargeclass());
			mmk.setListmap(listmap);
			listkm.add(mmk);
			return sendTrueData(listkm);
		}

		return sendTrueData(listmap);

	}

	/**
	 * 商品详情
	 * */
	@RequestMapping(value ="/commodity/selectInfoByname", method = RequestMethod.POST) 
	public RequestType selectInfoByname(String  name,String uname,Integer type) throws Exception{
		Sql msql =new Sql();
		List<Map<String, Object>> listmap;
		if(type!=null&&type==1){
			msql.setSql("select commoditykeyid from commodity where id="+name );
			listmap = commodityService.exeSelectSql(msql);
			if(listmap.size()==0)
				if(listmap.size()==0)return sendFalse("商品不存在");
			
			name = listmap.get(0).get("COMMODITYKEYID")+"";
		}
		Map<String, Object> map = new HashMap<>();
		
		//商品信息
		msql.setSql("select * from commoditykey where id="+name);
		listmap= commodityService.exeSelectSql(msql);
		if(listmap.size()==0){
			msql.setSql("select * from commodity where id="+name);
			listmap = commodityService.exeSelectSql(msql);
			if(listmap.size()==0)return sendFalse("商品不存在");
			
		}
		map.put("commodity", listmap);
		//客服
//		msql.setSql("select shopphone from commodity left join stock on stock.code=commodity.YOUCODE left join shop on shop.code=stock.shopcode "
//				+ "where commoditykeyid ='"+name+"' and shopphone is not null and rownum=1" );
//		map.put("shopphone", commodityService.exeSelectSql(msql).get(0).get("SHOPPHONE"));
		if(!(Stringutil.isBlank(uname)||"undefined".equals(uname)||"null".equals(uname))){
			msql.setSql("select phone from member where id = (select memberida from Friends where memberidb="+getLogin(uname).getUserid()+")" );
			map.put("shopphone", commodityService.exeSelectSql(msql).get(0).get("PHONE"));
			
		}
		
		//库存
		msql.setSql("select nvl(sum(num),0) num ,nvl(min(price),0)  minprice,nvl(max(price),0)  maxprice,nvl(max(originalprice),0)  maxoriginalprice from Stock left join commodity on commodity.youcode=stock.code where commoditykeyid="+name);
		map.put("num", commodityService.exeSelectSql(msql).get(0));
		//颜色
		msql.setSql("select distinct colour from commodity where commoditykeyid ='"+name+"' and nvl(kc,-1) >-1 order by colour");
		listmap = commodityService.exeSelectSql(msql);
		for (Map<String, Object> map1 : listmap) {
			map1.put("colour",map1.get("COLOUR"));
//			map1.remove("COLOUR");
		}
		map.put("colour", listmap);
		//尺码
		msql.setSql("select distinct  mysize from commodity where commoditykeyid ='"+name+"'  and nvl(kc,-1) >-1 order by my_to_number(mysize) ");
		listmap = commodityService.exeSelectSql(msql);
		for (Map<String, Object> map1 : listmap) {
			map1.put("mysize",map1.get("MYSIZE"));
//			map1.remove("MYSIZE");
		}
		map.put("mysize", listmap);
//		map.put("mysize", commodityService.exeSelectSql(msql));
		if(Stringutil.isBlank(uname)||"undefined".equals(uname)||"null".equals(uname)){
			map.put("Collection", new Collection[0]);
			map.put("ShoppingCard",0);
			
		}else{
			//是否收藏
			msql.setSql("select id from Collection where commodityid ='"+name+"' and memberid="+getLogin(uname).getUserid());
			map.put("Collection", commodityService.exeSelectSql(msql));
			msql.setSql("select nvl(count(*),0) a from ShoppingCard where memberid ="+getLogin(uname).getUserid());
			String s = commodityService.exeSelectSql(msql).get(0).get("A").toString();
			if(s.length()>1)s="9+";
			map.put("ShoppingCard",s);
		}
		
		//活动
		msql.setSql("select introduce from Promotion where commodityname ='"+listmap.get(0).get("YOUCODE")+"' order by id desc");
		listmap = commodityService.exeSelectSql(msql);
		if(listmap.size()!=0)
			map.put("Promotion",listmap.get(0));
		else
			map.put("Promotion","");
		
		
		
		//图片
		msql.setSql("select mainimage , detailsimage from commodity where commoditykeyid ='"+name+"'");
		List<Map<String, Object>> list = commodityService.exeSelectSql(msql);
		map.put("mainimage", list.get(0).get("MAINIMAGE").toString().split(";"));//主图
		map.put("detailsimage", list.get(0).get("DETAILSIMAGE").toString().split(";"));//详情图
		return sendTrueData(map);
	}
	/**
	 * 查询我的店铺的商品
	 * */
	@Auth
	@RequestMapping(value ="/commodity/selectbyshone", method = RequestMethod.POST) 
	public RequestType addone(String uname) throws Exception{
		MemberAuths  ma = getMember(getLogin(uname).getUserid());
		if(ma.getmShop()==null)return sendFalse("您还没有店铺");
		Sql msql =new Sql();
		//商品信息
		msql.setSql("select dcommodity.* "
				+ "from commodity  left join Stock on Stock.code=commodity.youcode where shopcode='"+ma.getmShop().getCode()+"'");
	
		
		return sendTrueData(commodityService.exeSelectSql(msql));
	}
	/**
	 * 颜色尺码塞选
	 * type:，1尺码，2颜色
	 * */
	@RequestMapping(value ="/commodity/coloursize", method = RequestMethod.POST) 
	public RequestType coloursize(String keyid,String str,Integer type) throws Exception{
		if(type==null)return sendFalse("类型错误");
		if(Stringutil.isBlank(str))return sendFalse("内容错误");
		if(Stringutil.isBlank(keyid))return sendFalse("商品错误");
		Sql msql = new Sql();
		List<Coloursize> listmColoursize;
		List<Map<String, Object>> listmap;
		if(type==1){
			msql.setSql("select distinct mysize , 0 type from Commodity where Commoditykeyid='"+keyid+"' order by my_to_number(mysize)");
			listmColoursize= IBeanUtil.ListMap2ListJavaBean(commodityService.exeSelectSql(msql), Coloursize.class);
			msql.setSql("select distinct mysize  from Commodity where Commoditykeyid='"+keyid+"' and colour='"+str+"' and nvl(kc,0)>0 and type=1 ");
			listmap = commodityService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				for (Coloursize mColoursize : listmColoursize) {
					if(mColoursize.getMysize().equals(map.get("MYSIZE").toString())){
						mColoursize.setType(1);
					}
				}
			}
		}else{
			msql.setSql("select distinct  colour , 0 type from Commodity where Commoditykeyid='"+keyid+"' order by colour");
			listmColoursize= IBeanUtil.ListMap2ListJavaBean(commodityService.exeSelectSql(msql), Coloursize.class);
			msql.setSql("select distinct  colour  from Commodity where Commoditykeyid='"+keyid+"' and mysize='"+str+"' and nvl(kc,0)>0 and type=1");
			listmap = commodityService.exeSelectSql(msql);
			for (Map<String, Object> map : listmap) {
				for (Coloursize mColoursize : listmColoursize) {
					if(mColoursize.getColour().equals(map.get("COLOUR").toString())){
						mColoursize.setType(1);
					}
				}
			}
		}
		
		return sendTrueData(listmColoursize);
	}
	/**
	 * 修改商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/updatecommodity", method = RequestMethod.POST) 
	public RequestType updatecommodity(Commodity mcommodity) throws Exception{
		if(mcommodity.getCommoditykeyid()==null)return sendFalse("错误,商品编号");
		
		if(!Stringutil.isBlank(mcommodity.getIndexs())){
			if(!mcommodity.getIndexs().equals("删除")){
				
				Sql msql = new Sql();
				msql.setSql("select  indexs,nvl(max(vip),0)vip from COMMODITY where indexs is not null   group by indexs");
				List<Map<String, Object>> listmap = commodityService.exeSelectSql(msql);
				boolean ba =false;
				for (Map<String, Object> map : listmap) {
					if(map.get("INDEXS").toString().equals(mcommodity.getIndexs())){
						mcommodity.setVip(Integer.valueOf(map.get("VIP").toString())+2);
						ba=true;
//						msql.setSql("update SPECIFICATIONS set vip="+(Integer.valueOf(map.get("VIP").toString())+1) +" where id="+mcommodity.getCommoditykeyid());
//						commodityService.execSQL(msql);
					}
				}
				if(!ba){
					if(listmap.size()>4)
						return sendFalse("错误,最多5个模块");
				}
				
				
			
			}
		}
		
		mcommodity.setId(Long.valueOf(mcommodity.getCommoditykeyid()));
		mcommodity.setCommoditykeyid(null);
		commodityService.updateBySelect(mcommodity);
		
		return sendTrueMsg("修改成功");
	}
	/**
	 * 按照怕品牌修改商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/updateCommodityBybrand", method = RequestMethod.POST)
	@ApiOperation(value = "根据商品品牌修改商品属性，可支持所有参数，indexs,商品首页的互动名称</br>只修改不为空得字段，如果要置空可以传入中文\"删除\"", response = RequestType.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "brand", value = "品牌", dataType = "String", required = true,paramType="path"),
			@ApiImplicitParam(name = "uname", value = "当前登录账户", dataType = "String", required = true,paramType="path"),
			@ApiImplicitParam(name = "UID", value = "设备号", dataType = "String", required = true,paramType="path"),
	})
	public RequestType updateCommodityBybrand(Commodity mcommodity) throws Exception{
		if(Stringutil.isBlank(mcommodity.getBrand()))return sendFalse("品牌不可为空");
		if(commodityService.getByparameter("brand",mcommodity.getBrand()).size()==0)return sendFalse("品牌"+mcommodity.getBrand()+"不存在");

		mcommodity.setId(Long.valueOf(mcommodity.getCommoditykeyid()));
		mcommodity.setCommoditykeyid(null);
		commodityService.updateBySelect(mcommodity,"brand",mcommodity.getBrand());

		return sendTrueMsg("修改成功");
	}

	/**
	 * 修改商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/updateoriginalprice", method = RequestMethod.POST) 
	public RequestType updateoriginalprice(Commodity mcommodity) throws Exception{
		if(mcommodity.getCommoditykeyid()==null)return sendFalse("错误,商品编号");
		
		Sql msql = new Sql();
		msql.setSql("update SPECIFICATIONS set originalprice="+mcommodity.getOriginalprice()+" where commoditykeyid="+mcommodity.getCommoditykeyid());
		
		commodityService.execSQL(msql,-1,"");
		
		return sendTrueMsg("修改成功");
	}
	/**
	 * 为已有的模块增加商品
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/updateindex", method = RequestMethod.POST) 
	public RequestType updateindex(String indexs,String keyids) throws Exception{
		if(Stringutil.isBlank(indexs)||Stringutil.isBlank(keyids))return sendFalse("参数错误");
		
		List<Long> ids1 = GsonUtil.fromJsonList(keyids, Long.class);
			if(ids1.size()==0)return sendFalse("编号错误");
		
		Sql msql = new Sql();
		msql.setSql("select DISTINCT indexs from COMMODITYkey where indexs is not null");
		List<Map<String, Object>> lmp = commodityService.exeSelectSql(msql);
		if(lmp.size()>4){
			boolean b=false;
			for (Map<String, Object> map : lmp) {
				if(map.get("INDEXS").toString().equals(indexs)){
					b=true;
					break;
				}
			}
			if(!b)
				return sendFalse("错误,模块"+indexs+"不存在");
		}
			
		StringBuilder sb = new StringBuilder();
		for (Long long1 : ids1) {
			sb.append(long1);
			sb.append(",");
		}
		
		msql.setSql("update  COMMODITYkey set indexs ='"+indexs+"' where id in("+sb.substring(0,sb.length()-1)+")");
		commodityService.execSQL(msql, 0, "");
		return sendTrueMsg("修改成功");
	}

	
	/**
	 * 修改商品图片
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/commodity/updateimage", method = RequestMethod.POST) 
	public RequestType updateimage(Long  keyid,HttpServletRequest req,String password) throws Exception{
		if(keyid==null)return sendFalse("错误,商品编号");
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			
			File[] li = ZIP.unzip(MyParameter.TomcatSD + s, MyParameter.TomcatFileImage, password);
			String mainimage="";
			String detailsimage="";
			for (File file : li) {
				if(file.toString().contains("\\main\\"))
					mainimage = mainimage+file.toString().substring(2, file.toString().length())+";";
				else
					detailsimage = detailsimage+file.toString().substring(2, file.toString().length())+";";
					
			}
			Sql msql = new Sql();
			msql.setSql("update commoditykey set   ");
			if(!Stringutil.isBlank(mainimage))
				msql.setSql(msql.getSql()+" mainimage='"+mainimage+"',");
			if(!Stringutil.isBlank(detailsimage))
				msql.setSql(msql.getSql()+" detailsimage='"+detailsimage+"',");
			
				msql.setSql(msql.getSql()+" id=id where id="+keyid);
			commodityService.execSQL(msql, 0, keyid+"");
			
			return sendTrueMsg("修改成功");
		}

		return sendFalse("未发现文件");
		
		
		
		
	}
	/**
	 * 添加商品
	 * */
	@Auth()
	@Transactional
	@RequestMapping(value ="/commodity/add", method = RequestMethod.POST) 
	public RequestType add(Commodity mcommodity,String jsonArray,String uname,HttpServletRequest mHttpServletRequest) throws Exception{
		MemberAuths mMemberAuths= getMember(uname);
		if(mMemberAuths.getmShop()==null||mMemberAuths.getmShop().getSuperid()!=0)
			return sendFalse("权限不足");
		
		if(Stringutil.isBlank(mcommodity.getLargeclass()))return sendFalse("大类不可为空");
		if(Stringutil.isBlank(mcommodity.getInclass()))return sendFalse("中类不可为空");
		if(Stringutil.isBlank(mcommodity.getSmallclass()))return sendFalse("小类不可为空");
		if(Stringutil.isBlank(mcommodity.getFineclass()))return sendFalse("细类不可为空");
		if(Stringutil.isBlank(mcommodity.getName()))return sendFalse("商品名字不可为空");
		if(Stringutil.isBlank(mcommodity.getBrand()))return sendFalse("品牌不可为空");
		if(Stringutil.isBlank(mcommodity.getCompany()))return sendFalse("计量单位不可为空");
		if(Stringutil.isBlank(mcommodity.getPackingmethod()))return sendFalse("包装方式不可为空");
		if(Stringutil.isBlank(mcommodity.getSpecifications()))return sendFalse("0：尺码，1：规格不可为空");
		if(Stringutil.isBlank(mcommodity.getCode()))return sendFalse("助记码不可为空");
		if(Stringutil.isBlank(mcommodity.getManufactor()))return sendFalse("厂家不可为空");
		if (!(mHttpServletRequest instanceof StandardMultipartHttpServletRequest))
			return sendFalse("图片不可为空");
			
		
		
		List<kc_ys_cm_tm> listkc_ys_cm_tm =GsonUtil.fromJsonList(jsonArray, kc_ys_cm_tm.class);
		if(listkc_ys_cm_tm.size()==0)return sendFalse("颜色尺码条码库存不可为空");
		for (kc_ys_cm_tm kc_ys_cm_tm : listkc_ys_cm_tm) {
			if(Stringutil.isBlank(kc_ys_cm_tm.getColour()))return sendFalse("颜色尺码条码库存数据不完整，请检查");
			if(Stringutil.isBlank(kc_ys_cm_tm.getMysize()))return sendFalse("颜色尺码条码库存数据不完整，请检查");
			if(Stringutil.isBlank(kc_ys_cm_tm.getYoucode()))return sendFalse("颜色尺码条码库存数据不完整，请检查");
			if(Stringutil.isBlank(kc_ys_cm_tm.getKc()))return sendFalse("颜色尺码条码库存数据不完整，请检查");
			if(mSpecificationsService.getByparameter("youcode", kc_ys_cm_tm.getYoucode())!=null)return sendFalse("条码已存在");
		}
		
		md mma = doFileUpload((StandardMultipartHttpServletRequest)mHttpServletRequest);
		if(mma.getM().split(";").length>6||mma.getM().split(";").length<1)
			return sendFalse("主图必须是1-6张");
		if(mma.getD().split(";").length<1)
			return sendFalse("详情图不可为空");
		
		mcommodity.setMainimage(mma.getM());
		mcommodity.setDetailsimage(mma.getD());
		
		mcommodity.setSupplier(uname);
		mcommodity.setSuppliername(mMemberAuths.getmShop().getShopname());
		
		
		BigDecimal riginalprice =mcommodity.getOriginalprice();//商品原价
		BigDecimal Price =mcommodity.getPrice();//商品售价
		mcommodity.setOriginalprice(null);
		mcommodity.setPrice(null);
		mcommodity.setId(System.currentTimeMillis());
		mcommodity.setMycode(mcommodity.getId().toString());
		commodityService.add(mcommodity);
		
		try {
			for (kc_ys_cm_tm kc_ys_cm_tm : listkc_ys_cm_tm) {
				Commodity mcommodity1 = new Commodity();
				mcommodity1.setYoucode(kc_ys_cm_tm.getYoucode());
				mcommodity1.setColour(kc_ys_cm_tm.getColour());
				mcommodity1.setMysize(kc_ys_cm_tm.getMysize());
				mcommodity1.setOriginalprice(riginalprice);
				mcommodity1.setPrice(Price);
				mcommodity1.setCostprice(riginalprice);
				mcommodity1.setCommoditykeyid(mcommodity.getId().toString());
				mSpecificationsService.addsu(mcommodity1);
				
				Stock mStock=new Stock();
				mStock.setShopcode(mMemberAuths.getmShop().getCode());
				mStock.setNum(Integer.parseInt(kc_ys_cm_tm.getKc()));; //库存量
				mStock.setCode(kc_ys_cm_tm.getYoucode());; //条码
				mStock.setType(-1);; //状态：1正常，-1，已下架，-2已冻结
				mStockService.add(mStock);
			}
		} catch (Exception e) {
			throw new RunException("参数异常,请检查");
		}
		
		return sendTrueData("添加成功");
	}
	public  md doFileUpload(StandardMultipartHttpServletRequest request)
			throws Exception {
		md aa = new CommodityController.md();
		aa.setM("");
		aa.setD("");
		// 获取上传的文件集合
		Iterator<String> iterator = request.getFileNames();
		while (iterator.hasNext()) {
			String sa = iterator.next();
			String path = FileUtil.saveMultipartFile(request.getFile(sa),mFileService);
			if(sa.contains("a"))
				aa.setM(aa.getM()+path+";");
			else
				aa.setD(aa.getD()+path+";");
		}
		return aa;
	}
	@Data
	public class md{
		private String m;
		private String d;
	}
//	/**
//	 * 批量上传商品图片
//	 * */
//	@Auth(admin=true)
//	@RequestMapping(value ="/commodity/RxcelImage", method = RequestMethod.POST) 
//	public RequestType RxcelImage(Long  keyid,HttpServletRequest req,String password) throws Exception{
//		if(keyid==null)return sendFalse("错误,商品编号");
//		if (req instanceof StandardMultipartHttpServletRequest) {
//			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
//			
//			File[] li = ZIP.unzip(MyParameter.TomcatSD + s, MyParameter.TomcatFileImage, password);
//			String mainimage="";
//			String detailsimage="";
//			String str = MyParameter.TomcatFileImage;
//			for (File file : li) {
//				if(file.toString().contains("\\main\\"))
//					mainimage = mainimage+file.toString().substring(2, file.toString().length())+";";
//				else
//					detailsimage = detailsimage+file.toString().substring(2, file.toString().length())+";";
//				
//			}
//			Sql msql = new Sql();
//			msql.setSql("update commoditykey set   ");
//			if(!Stringutil.isBlank(mainimage))
//				msql.setSql(msql.getSql()+" mainimage='"+mainimage+"',");
//			if(!Stringutil.isBlank(detailsimage))
//				msql.setSql(msql.getSql()+" detailsimage='"+detailsimage+"',");
//			
//			msql.setSql(msql.getSql()+" id=id where id="+keyid);
//			commodityService.execSQL(msql, 0, keyid+"");
//			
//			return sendTrueMsg("修改成功");
//		}
//		
//		return sendFalse("未发现文件");
//		
//		
//		
//		
//	}
	

}

