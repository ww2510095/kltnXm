package com.example.fw.main.s;

import com.example.fw.base.BaseService;
import com.example.fw.main.b.Qiandai_list;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Qiandai_listService extends BaseService {
    @Override
    protected String getTabName() {
        return "Qiandai_list";
    }
    
    
    public int add(String zhuang_t,String xiu_grzh,String xiu_grmz,String tab_name) throws Exception {
    	 Qiandai_list mQiandai_list = new Qiandai_list();
 		mQiandai_list.setZhuang_t(zhuang_t);	//当前状态	
 		mQiandai_list.setXiu_grzh(xiu_grzh);	//修改人账号	
 		mQiandai_list.setXiu_grmz(xiu_grmz);	//修改人名字	
 		mQiandai_list.setTab_name(tab_name);	//数据表名	
    	return super.add(mQiandai_list);
    }
    
	public List<?> getTab_name_data(String key) throws Exception{
    	Qiandai_list mQiandai_list=  new Qiandai_list();
    	mQiandai_list.setTab_name(key);
    	return  getALL(mQiandai_list,1,100000,_where.like);
    }
   
}
