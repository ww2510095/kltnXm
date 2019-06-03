package com.bm.version;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.myjar.Stringutil;


@RestController
@Api(tags = "版本管理(web)")
public class VersionController extends BaseController {
	
	@Autowired
	private VersionService mVersionService;
	
	
	@RequestMapping(value ="/version/select", method = RequestMethod.POST) 
	public RequestType select(String uname,Integer page ,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setOrderbykey("ID");
		msql.setOrderbytype(1);
		msql.setSql("select * from Version");
	if(uname==null){
		msql.setPage(1);
		msql.setRows(1);
	}else{
		if(getMember(uname).getSuperadmin()!=1){
			msql.setPage(1);
			msql.setRows(1);
		}else{
			msql.setPage(page);
			msql.setRows(rows);
		}
			
	}
	
		return sendTrueData(mVersionService.exeSelectSql(msql));
	}
	
	@RequestMapping(value ="/version/update", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType update(Version mVersion) throws Exception{
		mVersionService.updateBySelect(mVersion);
		return sendTrueMsg("修改成功");
	}
	@RequestMapping(value ="/version/delete", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType delete(Version mVersion) throws Exception{
		mVersionService.deleteByid(mVersion);
		return sendTrueMsg("删除成功");
	}
	@RequestMapping(value ="/version/add", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType add(Version mVersion) throws Exception{
			if(Stringutil.isBlank(mVersion.getCode()))return sendFalse("版本号不可为空");
			if(Stringutil.isBlank(mVersion.getUrl()))return sendFalse("下载地址不可为空");
			if(Stringutil.isBlank(mVersion.getData()))return sendFalse("更新内容不可为空");
		
			mVersionService.add(mVersion);
		return sendTrueMsg("添加成功");
	}

}
