package com.bm.orders.pdd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.commodity.Commodity;
import com.bm.orders.pdd.Pdd.ptfaenum;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "拼团")
public class PddController extends BaseController {
	@Autowired 
	private PddService mPddService;
	
	@Auth(admin=true)
	@RequestMapping(value ="/pdd/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType msadd(Pdd mPdd,HttpServletRequest req) throws Exception{
		if(mPdd.getStar()==null)mPdd.setStar(System.currentTimeMillis());
		if(mPdd.getEnd()==null)return sendFalse("结束时间不可为空");
		if(mPdd.getNum()==null)return sendFalse("数量不可为空");
		if(mPdd.getPrice()==null)return sendFalse("价格不可为空");
		if(mPdd.getType()==null)return sendFalse("类型不可为空");
		if(Stringutil.isBlank(mPdd.getBh()))return sendFalse("编号不可为空");
		if(Stringutil.isBlank(mPdd.getTitle()))return sendFalse("标题不可为空");
		try {
			if (req instanceof StandardMultipartHttpServletRequest)
				mPdd.setImagepath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
			else
				return sendFalse("图片不可为空");
		} catch (Exception e) {
			return sendFalse("图片错误");
		}
		boolean ba=false;
		for (ptfaenum iterable_element : ptfaenum.values()) {
			if(mPdd.getType()==iterable_element.getValue())
				ba=true;
		}
		if(!ba)
			throw new RunException("编号错误");
		
		Sql msql = new Sql();
		List<Commodity> list;
		if(mPdd.getCommoditykeyid()==null){
			if(Stringutil.isBlank(mPdd.getCode())){
				return sendFalse("商品错误");
			}else{
				msql.setSql("select * from Commodity where youcode='"+mPdd.getCode()+"'");
				list=IBeanUtil.ListMap2ListJavaBean(mPddService.exeSelectSql(msql), Commodity.class);
			}
		}else{
			msql.setSql("select * from Commodity where Commoditykeyid='"+mPdd.getCommoditykeyid()+"'");
			list=IBeanUtil.ListMap2ListJavaBean(mPddService.exeSelectSql(msql), Commodity.class);
		}
		if(list==null||list.size()==0)
			return sendFalse("商品不存在");
		Long ida = System.currentTimeMillis();
		for (Commodity commodity : list) {
			msql.setSql("select id from ms where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from ys where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from pdd where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'");
			if(mPddService.exeSelectSql(msql).size()!=0)throw new RunException("错误，条码"+commodity.getYoucode()+"已经在秒杀中了");
			mPdd.setId(ida);
			mPdd.setCode(commodity.getYoucode());
			mPdd.setCommoditykeyid(Long.valueOf(commodity.getCommoditykeyid()));
			Pdd mPdda = mPddService.getByparameter("bh", mPdd.getBh(),Pdd.class);
			if(mPdda!=null){
				if(mPdda.getType()!=mPdd.getType()){
					throw new RunException("错误，该编号已经指定了其他类型");
				}
				if(mPdda.getNum()!=mPdd.getNum()){
					throw new RunException("错误，该编号已经指定了其他数量");
				}
				
			}
		
			mPddService.add(mPdd);
			ida=ida+1;
		}
		
		return sendTrueMsg("添加成功");
	}
	
	@Auth
	@RequestMapping(value ="/pdd/select", method = RequestMethod.POST) 
	public RequestType select(Pdd mPdd,Integer page,Integer rows) throws Exception{
		return sendTrueData(mPddService.getALL(mPdd,page,rows));
	}
	@RequestMapping(value ="/pdd/applist", method = RequestMethod.POST) 
	public RequestType applist(Pdd mPdd,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select imagepath,commoditykeyid,title,star,end,min(price)price from ms group by commoditykeyid,imagepath,title,star,end");
		return sendTrueData(mPddService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/pdd/bycode", method = RequestMethod.POST) 
	public RequestType bycode(String code,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from pdd where code='"+code+"'  and end>"+System.currentTimeMillis()+" order by id desc ");
		return sendTrueData(mPddService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/pdd/bykeyid", method = RequestMethod.POST) 
	public RequestType bykeyid(String keyid,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from pdd where commoditykeyid='"+keyid+"' and end>"+System.currentTimeMillis()+" order by id desc");
		return sendTrueData(mPddService.exeSelectSql(msql));
	}

	
	
	
	
}
