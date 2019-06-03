package com.bm.auths.groupby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.auths.auths.Auths;
import com.bm.auths.organization.Organization;
import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.GsonUtil;
import com.myjar.Stringutil;

@RestController
public class GroupbyController extends BaseController{
	@Autowired
	private GroupbyService mGroupbyService;
	@Autowired
	private AuthsService mAuthsService;
	@Autowired
	private OrganizationService mOrganizationService;
	
	/***
	 * 添加组
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/groupby/add", method = RequestMethod.POST)
	public RequestType add(String organization,String JsonArray) throws Exception {
		if(Stringutil.isBlank(organization))return sendFalse("组织名不可为空");
		Map<String, Object> mso = mGroupbyService.getByparameter("organization", organization);
		if(mso!=null)return sendFalse("组织名已存在");
		
		List<String> ls = GsonUtil.fromJsonList(JsonArray, String.class);
		if(ls.size()==0)return sendFalse("权限错误");
		
		List<Groupby> mGroupbys = new ArrayList<Groupby>();
		for (String string : ls) {
			Groupby mGroupby = new Groupby();
			mGroupby.setOrganization(organization);
			mGroupby.setAuthskey(string);
			mGroupbys.add(mGroupby);
		}
		mGroupbyService.addList(mGroupbys);
		return sendTrueMsg("添加成功");
	}

	/***
	 * 修改组
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/groupby/update", method = RequestMethod.POST)
	public RequestType update(String organization,String JsonArray) throws Exception {
		if(Stringutil.isBlank(organization))return sendFalse("组织名不可为空");
		Map<String, Object> mso = mGroupbyService.getByparameter("organization", organization);
		if(mso==null||mso.get("ID")==null)return sendFalse("组织名不存在");
		
		List<String> ls = GsonUtil.fromJsonList(JsonArray, String.class);
		if(ls.size()==0)return sendFalse("权限错误");
		
		List<Groupby> mGroupbys = new ArrayList<Groupby>();
		for (String string : ls) {
			Groupby mGroupby = new Groupby();
			mGroupby.setOrganization(organization);
			mGroupby.setAuthskey(string);
			mGroupbys.add(mGroupby);
		}
		Groupby mGroupby = new Groupby();
		mGroupby.setOrganization(organization);
		mGroupbyService.deleteBySelect(mGroupby);//删除原来的权限
		mGroupbyService.addList(mGroupbys);//添加新权限
		return sendTrueMsg("添加成功");
	}

	/***
	 * 删除组
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/groupby/delete", method = RequestMethod.POST)
	public RequestType delete(String organization) throws Exception {
		Groupby mGroupby = new Groupby();
		mGroupby.setOrganization(organization);
		mGroupbyService.deleteBySelect(mGroupby);
		return sendTrueMsg("删除成功");
	}

	/***
	 * 
	 * 查询所有组
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/groupby/selectall", method = RequestMethod.POST)
	public RequestType selectall(Integer page,Integer rows) throws Exception {
		Sql msql = new Sql();
		msql.setRows(rows);
		msql.setPage(page);
		msql.setSql("select distinct organization from  Groupby");
		return sendTrueData(mGroupbyService.exeSelectSql(msql));
	}
	/***
	 * 查询组权限
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/groupby/select", method = RequestMethod.POST)
	public RequestType select(String organization,Integer page,Integer rows) throws Exception {
		Sql msql = new Sql();
		msql.setRows(rows);
		msql.setPage(page);
		msql.setSql("select Groupby.*,notes from Groupby left join auths on auths.key=Groupby.authskey where organization='"+organization+"'");
		return sendTrueData(mGroupbyService.exeSelectSql(msql));
	}
	/***
	 * 查询系统所有权限
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/auths/select", method = RequestMethod.POST)
	public RequestType authsselect(Integer page,Integer rows) throws Exception {
		return sendTrueData(mAuthsService.getALL(new Auths(),page,rows));
	}
	/***
	 * 权限模糊解锁
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/auths/selectlike", method = RequestMethod.POST)
	public RequestType authsselectlike(String name) throws Exception {
		Sql msql =new Sql();
		msql.setSql("select * from Auths where notes like '%"+name+"%'");
		msql.setRows(1000);
		return sendTrueData(map2map(mAuthsService.exeSelectSql(msql)));
	}
	/***
	 * 组模糊解锁
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/Groupby/selectlike", method = RequestMethod.POST)
	public RequestType Groupbyselectlike(String name) throws Exception {
		Sql msql =new Sql();
		msql.setSql("select DISTINCT organization  from Groupby  where organization like '%"+name+"%'");
		msql.setRows(1000);
		return sendTrueData(mAuthsService.exeSelectSql(msql));
	}
	private static List<Map<String, Object>> map2map(List<Map<String, Object>> map){
		List<Map<String, Object>> listmap = new ArrayList<>();
		for (Map<String, Object> map2 : map) {
			Map<String, Object> map1 = new HashMap<>();
			Set<String> mSet = map2.keySet();
			for (String string : mSet) {
				map1.put(string.toLowerCase(),map2.get(string).toString());
			}
			listmap.add(map1);
		}
		return listmap;
	}
	/***
	 * 查询用户所有的角色
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/auths/selectbymember", method = RequestMethod.POST)
	public RequestType selectbymember(String  phone) throws Exception {
		Organization mOrganization = new Organization();
		mOrganization.setMemberid(getLogin(phone).getUserid());
		return sendTrueData(mOrganizationService.getALL(mOrganization,1,1000));
	}
	
	/**
	 * 用户授权
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/organization/authorization", method = RequestMethod.POST)
	public RequestType authorization(String mphone,String JsonArray) throws Exception {
		if(Stringutil.isBlank(mphone))return sendFalse("授权用户不可为空");
		List<String> mlist = GsonUtil.fromJsonList(mphone, String.class);
		for (String string : mlist) {
			Map<String, Object> map = mMemberService.getByparameter("uname", string);
			if(map==null||map.get("ID")==null)return sendFalse("用户不存在");
			
			Long memberid =Long.valueOf(map.get("ID").toString());
			
			Organization mOrganization1 = new Organization();
			mOrganization1.setMemberid(memberid);
			
			List<String> ls = GsonUtil.fromJsonList(JsonArray, String.class);
			if(ls.size()==0){
				mOrganizationService.deleteBySelect(mOrganization1);//删除原来的权限
				return sendTrueMsg("删除权限成功");
			}
			
			List<Organization> mmOrganizationms = new ArrayList<Organization>();
			for (String mString : ls) {
				Organization mOrganization = new Organization();
				mOrganization.setGroupbyname(mString);
				mOrganization.setMemberid(memberid);
				mmOrganizationms.add(mOrganization);
			}
			
			mOrganizationService.deleteBySelect(mOrganization1);//删除原来的权限
			mOrganizationService.addList(mmOrganizationms);//添加新权限
		}
		
		return sendTrueMsg("添加成功");
	}
}
