package com.bm.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.SelectProvider;

public interface MemberDao{
	
	@SelectProvider(type = MemberDaoImp.class,method = "getAuths")
    public List<String> getAuths(Long memberid); 
	
	@SelectProvider(type = MemberDaoImp.class,method = "getAuthsAll")
	public List<Map<String, Object>> getAuthsAll(Long memberid); 

}
