package com.bm.orders.gfakchd;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@RequestMapping("/Gfakchd")
@Api(tags = "高仿爱库存活动")
public class GfakchdController extends BaseController {
    @Autowired
    protected GfakchdService mGfakchdService;

    @RequestMapping(value ="/add", method = RequestMethod.POST)
    public RequestType save(Gfakchd mGfakchd,HttpServletRequest req) throws Exception {
    	if(Stringutil.isBlank(mGfakchd.getMing_c()))return sendFalse("活动名称不可为空");
    	if(Stringutil.isBlank(mGfakchd.getJie_s()))return sendFalse("活动介绍不可为空");
    	if(Stringutil.isBlank(mGfakchd.getKai_ssj()))return sendFalse("开始时间不可为空");
    	if(Stringutil.isBlank(mGfakchd.getJie_ssj()))return sendFalse("结束时间不可为空");
		if(Stringutil.isBlank(mGfakchd.getTu_p())){
			if (req instanceof StandardMultipartHttpServletRequest)
				mGfakchd.setTu_p(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
		}
    	if(Stringutil.isBlank(mGfakchd.getTu_p()))return sendFalse("活动图片不可为空");
        mGfakchdService.add(mGfakchd);
        return sendTrueMsg("添加成功");
    }
    /**添加活动商品*/
    @RequestMapping(value ="/add_com", method = RequestMethod.POST)
    public RequestType addcom(String exa,Long id) throws Exception {
    	if(Stringutil.isBlank(exa))return sendFalse("添加内容不可为空");
    	Gfakchd mGfakchd=mGfakchdService.getById(id,Gfakchd.class);
    	if(mGfakchd==null)return sendFalse("活动编号不存在");
    	Sql msql = new Sql();
    	String s="";
    	msql.setSql("select commoditykeyid from commodity where commoditykeyid='"+exa+"' group by commoditykeyid");
    	List<Map<String, Object>> listmap = mGfakchdService.exeSelectSql(msql);
    	if(listmap.size()!=1){
    		msql.setSql("select commoditykeyid from commodity where youcode='"+exa+"'");
    		listmap = mGfakchdService.exeSelectSql(msql);
    		if(listmap.size()!=1){
    			msql.setSql("select commoditykeyid from commodity where name='"+exa+"'");
        		listmap = mGfakchdService.exeSelectSql(msql);
        		if(listmap.size()!=1){
        			msql.setSql("select commoditykeyid from commodity where fineclass='"+exa+"'");
        			listmap = mGfakchdService.exeSelectSql(msql);
        			if(listmap.size()==0){
        				msql.setSql("select commoditykeyid from commodity where commoditykeytype='"+exa+"'");
            			listmap = mGfakchdService.exeSelectSql(msql);
            			if(listmap.size()==0)
            				return sendFalse("输入的内容不存在或商品已下架，请检查(可以输入商品的商城编号，商品条码，商品名字，商品细类，商品自定义属性)");
        			}
        		}
    		}
    		
    	}

    	for (Map<String, Object> map : listmap) {
			s=s+map.get("COMMODITYKEYID").toString()+";";
		}
    	mGfakchd.setHuo_dsp(mGfakchd.getHuo_dsp()+s);
    	try {
    		mGfakchdService.updateBySelect(mGfakchd);
    		return sendTrueData("添加成功");
		} catch (Exception e) {
			throw new RunException("该活动商品已超过上限，一个活动最多添加285个商品");
		}
    	
    }
    /**删除指定活动的指定商品*/
    @RequestMapping(value ="/delete_com", method = RequestMethod.POST)
    public RequestType deletecom(String exa,Long id) throws Exception {
    	if(Stringutil.isBlank(exa))return sendFalse("商品编号不可为空");
    	Gfakchd mGfakchd=mGfakchdService.getById(id,Gfakchd.class);
    	if(mGfakchd==null)return sendFalse("活动编号不存在");
    	if(mGfakchd.getHuo_dsp().contains(exa+";")){
    		mGfakchd.getHuo_dsp().replace(exa+";", "");
    		mGfakchdService.updateBySelect(mGfakchd);
    		return sendTrueData("删除成功");
    	}
    	return sendFalse("该活动没有这个商品");
    	
    }
    
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Gfakchd mGfakchd,Integer page,Integer rows) throws Exception {
        return sendTrueData(mGfakchdService.getALL(mGfakchd,page,rows));
    }
    /**可用活动*/
    @RequestMapping(value ="/list_is", method = RequestMethod.POST)
    public RequestType list_is(Gfakchd mGfakchd,Integer page,Integer rows) throws Exception {
    	Sql msql = new Sql();
    	page=page==null?1:page;
    	msql.setPage(page);
    	msql.setRows(rows);
    	msql.setSql("select * from Gfakchd "+mGfakchdService.getWhere(mGfakchd)+" and jie_ssj<"+System.currentTimeMillis()+" and Kai_ssj>"+System.currentTimeMillis());
    	List<Map<String, Object>> listmap = mGfakchdService.exeSelectSql(msql);
    	for (Map<String, Object> map : listmap) {
    		map.remove("HUO_DSP");
		}
    	return sendTrueData(listmap);
    }
    /**
     * 查询活动的商品
     * */
    @RequestMapping(value ="/select_com_by_id", method = RequestMethod.POST)
    public RequestType select_com_by_id(Long id,Integer page,Integer rows) throws Exception {
    	Gfakchd mGfakchd=mGfakchdService.getById(id,Gfakchd.class);
		if(mGfakchd==null)return sendFalse("活动不存在");
//		if(System.currentTimeMillis()>mGfakchd.getJie_ssj())
//			return sendFalse("活动已过期");
//		if(System.currentTimeMillis()<mGfakchd.getKai_ssj())
//			return sendFalse("活动未开始");
		
		String sa="'";
		String[] ss = mGfakchd.getHuo_dsp().split(";");
		if(ss.length==0)return sendFalse("活动没有商品");
		for (String string : ss) {
			sa=sa+string+"',";
		}
		Sql msql = new Sql();
		msql.setOrderbykey("max(vip)");
		msql.setOrderbytype(1);
		msql.setSql("select min(nvl(ms.price,b.price)) price,max(my_null(ms.price))ms,max(my_null(pdd.price))pdd,max(my_null(ys.price))ys, originalprice,  LARGECLASS, INCLASS, commoditykeyid,"
				+ " SMALLCLASS,  FINECLASS, NAME, SUPPLIER, BRAND, INTRODUCTION, "
				+ " DETAILED, COMPANY,  PACKINGMETHOD,  MAINIMAGE, DETAILSIMAGE, "
				+ " SYSTEM, SPECIFICATIONS, SUPPLIERNAME from commoditykey a "
				+ " left join SPECIFICATIONS b on  b.commoditykeyid=a.id "
				+ "left join  Stock  on Stock.code=b.youcode "
				+ " left join (select num,code,price from ms where num>0 and end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))ms"
				+ " on ms.code=b.youcode "
				+ " left join (select num,code,price from ys where num>0 and end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))ys "
				+ " on Ys.code=b.youcode"
				+"  left join (select num,code,price,bh,type tfa from pdd where num>0 "
				+ " and star<(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + "
				+ "TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF'))  and "
				+ "end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))pdd"
				 +"  on pdd.code=b.youcode"
				+ " where commoditykeyid in()" 
				+ " group by  originalprice,  LARGECLASS, INCLASS, commoditykeyid, SMALLCLASS,  FINECLASS, NAME, SUPPLIER, BRAND, INTRODUCTION,  DETAILED, COMPANY,  PACKINGMETHOD,  MAINIMAGE, DETAILSIMAGE,  SYSTEM, SPECIFICATIONS, SUPPLIERNAME");
		
		  msql.setRows(rows);
		  msql.setPage(page);
		  return sendTrueData(mGfakchdService.exeSelectSql(msql));
    }
  
    

}
