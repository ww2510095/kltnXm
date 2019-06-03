package com.bm.orders.ys;

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
@Api(tags = "预售")
public class YsController extends BaseController {
	@Autowired 
	private YsService mYsService;
	
	@Auth(admin=true)
	@RequestMapping(value ="/ys/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType msadd(Ys mYs,HttpServletRequest req) throws Exception{
		if(mYs.getStar()==null)mYs.setStar(System.currentTimeMillis());
		if(mYs.getEnd()==null)return sendFalse("结束时间不可为空");
		if(mYs.getNum()==null)return sendFalse("数量不可为空");
		if(mYs.getPrice()==null)return sendFalse("价格不可为空");
		if(Stringutil.isBlank(mYs.getTitle()))return sendFalse("标题不可为空");
		try {
			if (req instanceof StandardMultipartHttpServletRequest)
				mYs.setImagepath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
			else
				return sendFalse("图片不可为空");
		} catch (Exception e) {
			return sendFalse("图片错误");
		}

		Sql msql = new Sql();
		List<Commodity> list;
		if(mYs.getCommoditykeyid()==null){
			if(Stringutil.isBlank(mYs.getCode())){
				return sendFalse("商品错误");
			}else{
				msql.setSql("select * from Commodity where youcode='"+mYs.getCode()+"'");
				list=IBeanUtil.ListMap2ListJavaBean(mYsService.exeSelectSql(msql), Commodity.class);
			}
		}else{
			msql.setSql("select * from Commodity where Commoditykeyid='"+mYs.getCommoditykeyid()+"'");
			list=IBeanUtil.ListMap2ListJavaBean(mYsService.exeSelectSql(msql), Commodity.class);
		}
		if(list==null||list.size()==0)
			return sendFalse("商品不存在");
		
		Long ida = System.currentTimeMillis();
		for (Commodity commodity : list) {
			msql.setSql("select id from ms where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from ys where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'"
					+ " union all select id from pdd where end>'"+System.currentTimeMillis()+"' and code='"+commodity.getYoucode()+"'");
			if(mYsService.exeSelectSql(msql).size()!=0)throw new RunException("错误，条码"+commodity.getYoucode()+"已经在预售中了");
			mYs.setId(ida);
			mYs.setCode(commodity.getYoucode());
			mYs.setCommoditykeyid(Long.valueOf(commodity.getCommoditykeyid()));
			mYsService.add(mYs);
			ida=ida+1;
		}
		
		return sendTrueMsg("添加成功");
	}
	
	@Auth
	@RequestMapping(value ="/ys/select", method = RequestMethod.POST) 
	public RequestType select(Ys mYs,Integer page,Integer rows) throws Exception{
		return sendTrueData(mYsService.getALL(mYs,page,rows));
	}
	@RequestMapping(value ="/ys/applist", method = RequestMethod.POST) 
	public RequestType applist(Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select imagepath,commoditykeyid,title,star,end,min(price)price from ys group by commoditykeyid,imagepath,title,star,end ");
		return sendTrueData(mYsService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/ys/bycode", method = RequestMethod.POST) 
	public RequestType bycode(String code,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from ys where code='"+code+"'  and end>"+System.currentTimeMillis()+" order by id desc ");
		return sendTrueData(mYsService.exeSelectSql(msql));
	}
	@RequestMapping(value ="/ys/bykeyid", method = RequestMethod.POST) 
	public RequestType bykeyid(String keyid,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from ys where commoditykeyid='"+keyid+"' and end>"+System.currentTimeMillis()+" order by id desc");
		return sendTrueData(mYsService.exeSelectSql(msql));
	}
	@Auth(admin=true)
	@RequestMapping(value ="/ys/end", method = RequestMethod.POST) 
	public RequestType end(Long id) throws Exception{
		Ys mMs = new Ys();
		mMs.setId(id);
		mMs.setEnd(System.currentTimeMillis());
		return sendTrueMsg(mYsService.updateBySelect(mMs));
	}
	
	
	

	
}
