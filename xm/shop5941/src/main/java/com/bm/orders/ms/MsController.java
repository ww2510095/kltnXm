package com.bm.orders.ms;

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
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "秒杀")
public class MsController extends BaseController {
	@Autowired 
	private MsService mMsService;
	
	@Auth(admin=true)
	@RequestMapping(value ="/ms/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType msadd(Ms mMs,HttpServletRequest req) throws Exception{
		if(mMs.getStar()==null)mMs.setStar(System.currentTimeMillis());
		if(mMs.getEnd()==null)return sendFalse("结束时间不可为空");
		if(mMs.getNum()==null)return sendFalse("数量不可为空");
		if(mMs.getPrice()==null)return sendFalse("价格不可为空");
		if(Stringutil.isBlank(mMs.getTitle()))return sendFalse("标题不可为空");
		try {
			if (req instanceof StandardMultipartHttpServletRequest)
				mMs.setImagepath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
			else
				return sendFalse("图片不可为空");
		} catch (Exception e) {
			return sendFalse("图片错误");
		}

		Sql msql = new Sql();
		List<Commodity> list;
		if(mMs.getCommoditykeyid()==null){
			if(Stringutil.isBlank(mMs.getCode())){
				return sendFalse("商品错误");
			}else{
				msql.setSql("select * from Commodity where youcode='"+mMs.getCode()+"'");
				list=IBeanUtil.ListMap2ListJavaBean(mMsService.exeSelectSql(msql), Commodity.class);
			}
		}else{
			msql.setSql("select * from Commodity where Commoditykeyid='"+mMs.getCommoditykeyid()+"'");
			list=IBeanUtil.ListMap2ListJavaBean(mMsService.exeSelectSql(msql), Commodity.class);
		}
		if(list==null||list.size()==0)
			return sendFalse("商品不存在");
		Long ida = System.currentTimeMillis();
		for (Commodity commodity : list) {
			msql.setSql("select id from ms where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from ys where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from pdd where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'");
			if(mMsService.exeSelectSql(msql).size()!=0)throw new RunException("错误，条码"+commodity.getYoucode()+"已经在活动中了(同一个商品，秒杀，预售，拼团只能选择一个)");
			mMs.setId(ida);
			mMs.setCode(commodity.getYoucode());
			mMs.setCommoditykeyid(Long.valueOf(commodity.getCommoditykeyid()));
			mMsService.add(mMs);
			ida=ida+1;
		}
		
		return sendTrueMsg("添加成功");
	}
	
	@Auth
	@RequestMapping(value ="/ms/select", method = RequestMethod.POST) 
	public RequestType select(Ms mMs,Integer page,Integer rows) throws Exception{
		return sendTrueData(mMsService.getALL(mMs,page,rows));
	}
	@RequestMapping(value ="/ms/applist", method = RequestMethod.POST) 
	public RequestType applist(Ms mMs,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select imagepath,commoditykeyid,title,star,end,min(price)price from ms group by commoditykeyid,imagepath,title,star,end");
		return sendTrueData(mMsService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/ms/bycode", method = RequestMethod.POST) 
	public RequestType bycode(String code,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from ms where code='"+code+"'  and end>"+System.currentTimeMillis()+" order by id desc ");
		return sendTrueData(mMsService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/ms/bykeyid", method = RequestMethod.POST) 
	public RequestType bykeyid(String keyid,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from ms where commoditykeyid='"+keyid+"' and end>"+System.currentTimeMillis()+" order by id desc");
		return sendTrueData(mMsService.exeSelectSql(msql));
	}
	@Auth(admin=true)
	@RequestMapping(value ="/ms/end", method = RequestMethod.POST) 
	public RequestType end(Long id) throws Exception{
		Ms mMs = new Ms();
		mMs.setId(id);
		mMs.setEnd(System.currentTimeMillis());
		return sendTrueMsg(mMsService.updateBySelect(mMs));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
