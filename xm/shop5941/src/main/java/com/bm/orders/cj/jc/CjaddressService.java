package com.bm.orders.cj.jc;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@Service
public class CjaddressService extends BaseService{

	@Override
	public String getTabName() {
		return "Cjaddress";
	}
	
	public int add(Cj_address mCj_address) throws Exception {
		if(Stringutil.isBlank(mCj_address.getMember_address()))throw new RunException("请填写完整信息");
		if(Stringutil.isBlank(mCj_address.getMember_phone()))throw new RunException("请填写完整信息");
		if(Stringutil.isBlank(mCj_address.getMember_name()))throw new RunException("请填写完整信息");
		super.add(mCj_address);
		return 1;
	}
	public void add_ZIP_code(Cj_address mCj_address) throws Exception {
		String zipcode=mCj_address.getZip_code();
		Long jc_id=mCj_address.getJc_id();
		if(jc_id==null)throw new RunException("奖品不可为空");
		if(Stringutil.isBlank(zipcode))throw new RunException("快递号不可为空");
		mCj_address=getById(mCj_address,Cj_address.class);
		if(mCj_address==null)throw new RunException("地址错误或不存在");
		mCj_address.setJc_id(jc_id);
		mCj_address.setZip_code(zipcode);
		mCj_address.setId(System.currentTimeMillis());
		super.add(mCj_address);
	}
	public List<Map<String, Object>> select_not_ZIP_code(Cj_address mCj_address,Integer page,Integer rows) throws Exception {
		mCj_address.setZip_code(null);
		mCj_address.setJc_id(null);
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select * from "+getTabName()+getWhere(mCj_address)+" and jc_id is null");
		return exeSelectSql(msql);
	}
	public List<Map<String, Object>> select_ZIP_code(Cj_address mCj_address,Integer page,Integer rows) throws Exception {
		String sql="select Cjaddress.*,jc.title jc_title from Cjaddress left join jc on jc.id=jc_id where Jc_id is not null ";
		if(!Stringutil.isBlank(mCj_address.getZip_code()))
			sql=sql+" and zip_code='"+mCj_address.getZip_code()+"'";
		if(mCj_address.getJc_id()!=null)
			sql=sql+" and Jc_id='"+mCj_address.getJc_id()+"'";
		if(mCj_address.getMember_id()!=null)
			sql=sql+" and Member_id='"+mCj_address.getMember_id()+"'";
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql(sql);
		return exeSelectSql(msql);
	}
	

}
