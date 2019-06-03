package com.bm.consumption.envelopes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.consumption.erp.ERP_YHJ;
import com.bm.consumption.erp.ERP_send;

/**
 * 红包
 * */
@RestController
@Api(tags = "红包")
public class EnvelopesController extends BaseController{
	

 	 
	@Autowired
    private EnvelopesService mEnvelopesService;
	
	@Autowired
	private EnvelopeskeyService mEnvelopesKeyService;
    
    @RequestMapping(value="/Envelopes/add",method=RequestMethod.POST)
    public RequestType Envelopesadd(String wid) throws Exception{
//    	if(Stringutil.isBlank(wid))return sendFalse("微信id错误");
//    	Sql msql = new Sql();
//    	msql.setRows(2);
//    	msql.setOrderbykey("id");
//    	msql.setOrderbytype(1);
//    	msql.setSql("select * from EnvelopesKey");
//    	List<EnvelopesKey> listkey= IBeanUtil.ListMap2ListJavaBean(mEnvelopesService.exeSelectSql(msql), EnvelopesKey.class);
//    	msql.setRows(listkey.get(0).getMaxsize());
//    	msql.setSql("select id from Envelopes where wid='"+wid+"' and istrue=0 ");
//    	if(mEnvelopesService.exeSelectSql(msql).size()==listkey.get(0).getMaxsize())return sendFalse("你已经有"+listkey.get(0).getMaxsize()+"个红包了，请先使用了再来");
//    	
//    	
//    	msql.setSql("select nvl(count(*),0)A from Envelopes ");
//    	Envelopes mEnvelopes = new Envelopes();
//    	mEnvelopes.setWid(wid);
//    	mEnvelopes.setIstrue(0);
//    	if(Integer.parseInt(mEnvelopesService.exeSelectSql(msql).get(0).get("A").toString())==listkey.get(0).getMaxt()-1)
//    		mEnvelopes.setNum(listkey.get(0).getMaxnum());
//    	
//    	  msql.setSql("select * from Envelopes "); 
//    	  msql.setOrderbykey("addtime");
//   	   	  msql.setRows(3);
//   	   	  BigDecimal a = null;
//   	   	  BigDecimal b = null;
//   	   	  List<Envelopes> listEnvelopes = IBeanUtil.ListMap2ListJavaBean(mEnvelopesService.exeSelectSql(msql), Envelopes.class);
//   	   	if(listEnvelopes.size()!=3){
//   	       double min = listkey.get(0).getMinnum().doubleValue();
//	  	   double max = listkey.get(0).getNum_price().doubleValue();
//	  	   double boundedDouble = min + new Random().nextDouble() * (max - min);
//	  	   DecimalFormat df = new DecimalFormat("#.00");
//	  	   mEnvelopes.setNum(new BigDecimal(df.format(boundedDouble)));
//   	   	}else{
//   	   		for (int i = 0; i < 3; i++) {
//				if(listEnvelopes.get(i).getNum().compareTo(listkey.get(0).getMaxnum())==0)continue;
//				if(a==null){a=listEnvelopes.get(i).getNum();continue;}
//				if(b==null)b=listEnvelopes.get(i).getNum();
//			}
//   	   		if(a.add(b).compareTo(listkey.get(0).getNum_price())==0){
//	   	   	   double min = listkey.get(0).getMinnum().doubleValue();
//		  	   double max = listkey.get(0).getNum_price().doubleValue();
//		  	   double boundedDouble = min + new Random().nextDouble() * (max - min);
//		  	   DecimalFormat df = new DecimalFormat("#.00");
//		  	   mEnvelopes.setNum(new BigDecimal(df.format(boundedDouble)));
//		  	   }else{
//		  		mEnvelopes.setNum(listkey.get(0).getNum_price().subtract(a));
//		  	   }
//   	   	}
//    	 
//    	 
//   	Long ida =  mEnvelopesService.getMaxParameter("id");
//   	if(ida.toString().equals("1")){
//   		int code = new Random().nextInt(999999-100000+1)+100000;
//    	mEnvelopes.setId(Long.valueOf(code));
//   	}else{
//
//   		int code = new Random().nextInt(9)+1;
//   		ida=ida+code;
//   		synchronized (this) {
//   			while (mEnvelopesService.getById(ida)!=null) {
//   	    		code = new Random().nextInt(9)+1;
//   			}
//   	    	mEnvelopes.setId(ida);
//		}
//    	
//   	
//   	}
//   		mEnvelopes.setAddtime(System.currentTimeMillis());
//    	mEnvelopesService.add(mEnvelopes);
//    	return sendTrueData(mEnvelopes.getNum());
    	return sendFalse("...");

    }
    @RequestMapping(value="/Envelopes/newadd",method=RequestMethod.POST)
    public RequestType Envelopesnewadd(String wid) throws Exception{
    	Sql msql = new Sql();
    	msql.setSql("select id from Envelopes where istrue=0 and wid='"+wid+"'");
    	msql.setRows(1);
    	if(mEnvelopesService.exeSelectSql(msql).size()!=0)return sendFalse("你已经有红包了，请使用以后再来。");
    	ERP_YHJ erp = ERP_send.creater_yhj();
    	Envelopes mEnvelopes =new Envelopes();
    	mEnvelopes.setId((long)erp.getId());
    	mEnvelopes.setWid(wid);
    	mEnvelopes.setIstrue(0);
    	mEnvelopes.setErpsign(erp.getCoupon_sn());
    	mEnvelopes.setNum(new BigDecimal(erp.getCoupon_price()));
    	mEnvelopes.setAddtime(System.currentTimeMillis());
    	mEnvelopesService.add(mEnvelopes);
    	return sendTrueData(erp.getCoupon_price());
    	
    }
    /**
     * 查询我的红包
     * */
    @RequestMapping(value="/Envelopes/my_",method=RequestMethod.POST)
    public RequestType ridesshop(String wid,Integer rows,Integer page) throws Exception{
    	if(rows==null) rows=10;
    	Sql msql = new Sql();
    	msql.setSql("select * from Envelopes where wid='"+wid+"'");
    	msql.setRows(rows);
    	msql.setPage(page);
    	msql.setOrderbykey("addtime");
    	msql.setOrderbytype(1);
    	List<Envelopes> list = IBeanUtil.ListMap2ListJavaBean(mEnvelopesService.exeSelectSql(msql), Envelopes.class);
    	String[] str = new String[list.size()];
    	int a = list.size();
    	for (int i=0;i<a;i++) {
    		str[i]=list.get(i).getErpsign();
		}
    	List<Envelopes> list1 = new ArrayList<>();
    	List<ERP_YHJ> listsep = ERP_send.yhj_selectList(str);
    	for (ERP_YHJ erp_YHJ : listsep) {
			for (Envelopes mEnvelopes : list) {
				if(mEnvelopes.getErpsign().equals(erp_YHJ.getCoupon_sn())){
					if(erp_YHJ.getRec_status().equals("F")){
						if(mEnvelopes.getIstrue()==0){
							mEnvelopes.setIstrue(1);
							list1.add(mEnvelopes);
						}
					}
				}
			}
		}
    	for (Envelopes mEnvelopes : list1) {
    		mEnvelopesService.updateBySelect(mEnvelopes);
		}
    	return sendTrueData(list);
    	
    }
    /**
     * 管理员查询红包
     * */
    @Auth(admin=true)
    @RequestMapping(value="/Envelopes/all",method=RequestMethod.POST)
    public RequestType Envelopes(Integer rows,Integer page,Envelopes mEnvelopes) throws Exception{
    	return sendTrueData(mEnvelopesService.getALL(mEnvelopes,page,rows));
    }
    /**
     * 添加红包领取规则
     * */
    @Auth(admin=true)
    @RequestMapping(value="/Envelopeskey/add",method=RequestMethod.POST)
    public RequestType selectshop(EnvelopesKey mEnvelopesKey) throws Exception{
    	if(mEnvelopesKey.getMaxnum()==null)mEnvelopesKey.setMaxnum(new BigDecimal("99"));
    	if(mEnvelopesKey.getMinnum()==null)mEnvelopesKey.setMinnum(new BigDecimal("1"));
    	if(mEnvelopesKey.getNum_price()==null)mEnvelopesKey.setNum_price(new BigDecimal("16"));
    	if(mEnvelopesKey.getMaxsize()==null)mEnvelopesKey.setMaxsize(4);
    	if(mEnvelopesKey.getMaxt()==null)mEnvelopesKey.setMaxt(1000);;
    	mEnvelopesKeyService.add(mEnvelopesKey);
    	return sendTrueMsg("添加成功");
    }
    /**
     * 添加查询红包领取规则
     * */
    @Auth(admin=true)
    @RequestMapping(value="/Envelopeskey/all",method=RequestMethod.POST)
    public RequestType selectshop(Integer rows,Integer page,EnvelopesKey mEnvelopesKey) throws Exception{
    	return sendTrueData(mEnvelopesKeyService.getALL(mEnvelopesKey,page,rows));
    }
}
