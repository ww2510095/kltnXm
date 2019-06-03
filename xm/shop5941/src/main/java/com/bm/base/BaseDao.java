package com.bm.base;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.SelectProvider;

public interface BaseDao {
	
	@SelectProvider(type = BaseDaoImp.class,method = "getALL")
    public List<Map<String, Object>> getALL(Sql msql); 
	
	@SelectProvider(type = BaseDaoImp.class,method = "getMaxParameter")
	public Long getMaxParameter(String tabname, String parametername);
   
    @SelectProvider(type = BaseDaoImp.class,method = "getById")
    public Map<String, Object> getById(String tabname,String id);
    
    @SelectProvider(type = BaseDaoImp.class,method = "deleteByid")
    public void deleteByid(String tabname,String id);

    @SelectProvider(type = BaseDaoImp.class,method = "execSQL")
	public void execSQL(Sql msql);

    @SelectProvider(type = BaseDaoImp.class,method = "getByparameter")
	public Map<String, Object> getByparameter(String tabname,String parametername, String parametervalue);

    @SelectProvider(type = BaseDaoImp.class,method = "exeSelectSql")
	public List<Map<String, Object>> exeSelectSql(Sql msql);

}
