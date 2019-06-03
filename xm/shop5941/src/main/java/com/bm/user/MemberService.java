package com.bm.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.myjar.desutil.RunException;
 
@Service
public class MemberService extends BaseService{
	
	@Autowired
	private MemberDao mMemberDao;
	
	@Override
	public String getTabName() {return "member";}

	public List<String> getAuths(Long memberid) {
		return mMemberDao.getAuths( memberid);
	}
	public List<Map<String, Object>> getAuthsAll(Long memberid) {
		return mMemberDao.getAuthsAll( memberid);
	}
	
	public int add(Member  mMember) throws Exception {
		if(getByparameter("uname", mMember.getUname())!=null)throw new RunException("账号"+mMember.getUname()+"已存在");
		return super.add(mMember);
	}
   
}
