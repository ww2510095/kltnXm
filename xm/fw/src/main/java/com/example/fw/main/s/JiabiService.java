package com.example.fw.main.s;

import com.example.fw.base.BaseService;
import com.example.fw.base.IBeanUtil;
import com.example.fw.base.Sql;
import com.example.fw.main.b.User;
import com.example.fw.main.b.Wangdian;
import com.example.fw.main.b.Zhuzhijiagou;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JiabiService extends BaseService {
    @Override
    protected String getTabName() {
        return "jiabiuser";
    }
	public static String sp_js(String juese) {
		String aa ="'";
		String[] a = juese.split(";");
		for (String string : a) {
			aa=aa+string+"',";
		}
		if(a.length==0)return "";
		return aa.substring(0,aa.length()-1);
		
	}
	
    
	private User getUserKey(User user) throws Exception{
		Sql msql = new Sql();
		try {
			msql.setSql("select ZHUZHIJIAGOU_NAME A from Juese where uphone='"+user.getUname()+"'");
			String aa = sp_js(exeSelectSql(msql).get(0).get("A").toString());
			//aa=aa.replace("''", "'");
			msql.setSql("select * from ZHUZHIJIAGOU where ukey in("+aa+")");
			Zhuzhijiagou mZhuzhijiagou =IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0), Zhuzhijiagou.class);
			user.setKey(mZhuzhijiagou.getQuan_x().split(";"));
		} catch (IndexOutOfBoundsException e) {
			user.setKey(new String[0]);
		}
		try {
			msql.setSql("select * from wangdian where wang_ddz ='"+user.getSuo_swd()+"'");
			Wangdian mWangdian=IBeanUtil.Map2JavaBean(exeSelectSql(msql).get(0),Wangdian.class);
			user.setDeng_j(mWangdian.getDeng_j());
			msql.setSql("select * from wangdian where wai_b ='"+user.getSuo_swd()+"'");
			List<Map<String, Object>> listmap = exeSelectSql(msql);
			if(listmap.size()!=0){
//				user.setYwd(listmap.get(0).get("WANG_DDZ").toString());
			}
		} catch (IndexOutOfBoundsException e) {
			user.setDeng_j(1);
		}
		return user;
	}

	public User getUser(String uname) throws Exception {
		if(Stringutil.isBlank(uname))throw new RunException("账号错误");
		User mUser1 = getByparameter("uname",uname,User.class);
		if(mUser1==null)
			throw new RunException("账号不存在");
		mUser1=getUserKey(mUser1);
		return mUser1;
	}
	public User getUser(Long id) throws Exception {
		if(Stringutil.isBlank(id))throw new RunException("编号错误");
		User mUser1 = getById(id,User.class);
		if(mUser1==null)
			throw new RunException("账号不存在");
		mUser1=getUserKey(mUser1);
		return mUser1;
	}
	
	public int add(User obj) throws Exception {
		obj.setDeng_j(null);
		return super.add(obj);
	}
	public String updateBySelect(User obj) throws Exception {
		obj.setDeng_j(null);
		return super.updateBySelect(obj);
	}


}
