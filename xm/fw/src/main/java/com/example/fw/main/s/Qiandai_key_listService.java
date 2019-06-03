package com.example.fw.main.s;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fw.base.BaseService;
import com.example.fw.main.b.Qiandai_key_list;

@Service
public class Qiandai_key_listService extends BaseService {
	public boolean yuan = false;
	
	@Autowired
    protected Qiandai_key_list_yuanService mQiandai_key_list_yuanService;
	
    @Override
    protected String getTabName() {
    	if(yuan){
    		yuan=false;
    		return "Qiandai_key_list_yuan";
    	}
    	
        return "Qiandai_key_list";
    }
    
    public List<?> getTab_name_data(String key) throws Exception{
    	yuan=true;
    	Qiandai_key_list mQiandai_list=  new Qiandai_key_list();
    	mQiandai_list.setTab_name(key);
    	return  getALL(mQiandai_list,1,100000,_where.like);
    }
    
    @Override
    public <T> T getByparameter(String parametername, String parametervalue, Class<T> clazz) throws Exception {
    	// TODO Auto-generated method stub
    	return super.getByparameter(parametername, parametervalue, clazz);
    }
    public String updateBySelect(Qiandai_key_list mQiandai_key_list) throws Exception {
    	Qiandai_key_list mQiandai_key_list1 = new Qiandai_key_list();
    	mQiandai_key_list1.setTab_name(mQiandai_key_list.getTab_name());
    	mQiandai_key_list1.setMing_c(mQiandai_key_list.getMing_c());
    	if(mQiandai_key_list_yuanService.getALL(mQiandai_key_list1).size()==0)
    		mQiandai_key_list_yuanService.add(mQiandai_key_list);
    	else
    		mQiandai_key_list_yuanService.updateBySelect(mQiandai_key_list);
    	return "操作成功";
    	}
}
