package com.example.fw.main.c;

import com.example.fw.base.BaseController;
import com.example.fw.base.BaseService._where;
import com.example.fw.base.RequestType;
import com.example.fw.base.Sql;
import com.example.fw.main.b.Wangdian;
import com.example.fw.main.s.WangdianService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Wangdian")
@Api(tags = "组织架构")
public class WangdianController extends BaseController {
    @Autowired
    protected WangdianService mWangdianService;

    @RequestMapping(value ="/save", method = RequestMethod.POST)
    public RequestType save(Wangdian mWangdian) throws Exception {
    	if(mWangdian.getDeng_j()>4||mWangdian.getDeng_j()<1)return sendFalse("等级只能是1.2.3.4");
    	if(mWangdian.getDeng_j()==3||mWangdian.getDeng_j()==4)wangdianxiaoyan(mWangdian.getDeng_j(),mWangdian.getShang_j());
        if(Stringutil.isBlank(mWangdian.getId())){
        	if(mWangdianService.getByparameter("wang_ddz", mWangdian.getWang_ddz())!=null)
        		return sendFalse("网点"+mWangdian.getWang_ddz()+"已存在");
        	if(mWangdian.getDeng_j()==2){
        		mWangdian.setA2(mWangdian.getWang_ddz());
        	}else
        	 if(mWangdian.getDeng_j()==3){
        		 mWangdian.setA2(mWangdian.getShang_j());
        	}else if(mWangdian.getDeng_j()==4){
        		Wangdian mWangdian1 = mWangdianService.getByparameter("wang_ddz", mWangdian.getShang_j(),Wangdian.class);
        		 mWangdian.setA3(mWangdian.getShang_j());
        		 mWangdian.setA2(mWangdian1.getShang_j());
        	}
        	
            mWangdianService.add(mWangdian);
            return sendTrueMsg("添加成功");
        }else{
        
        	if(mWangdianService.getByparameter("wang_ddz", mWangdian.getWang_ddz())!=null)
        		return sendFalse("网点"+mWangdian.getWang_ddz()+"已存在");
        	
        	Wangdian mWangdian1 = mWangdianService.getById(mWangdian,Wangdian.class);
        	Sql msql  =new Sql();
        	if(mWangdian1.getDeng_j()==2){
        		msql.setSql("update Wangdian set A2='"+mWangdian.getWang_ddz()+"' where A2='"+mWangdian1.getWang_ddz()+"'");
        		mWangdianService.execSQL(msql);
        	}else if(mWangdian1.getDeng_j()==3){
        		msql.setSql("update Wangdian set A3='"+mWangdian.getWang_ddz()+"' where A3='"+mWangdian1.getWang_ddz()+"'");
        		mWangdianService.execSQL(msql);
        	} 
            mWangdianService.updateBySelect(mWangdian);
            return sendTrueMsg("更新成功");
        }

    }
    private void wangdianxiaoyan(int dj,String name) throws Exception {
    	Wangdian mWangdian;
    	switch (dj) {
		case 3:
			mWangdian=mWangdianService.getByparameter("wang_ddz", name,Wangdian.class);
			if(mWangdian==null||mWangdian.getDeng_j()!=2)
			throw new RunException("等级错误，只能选择2级");
			return;
		case 4:
			mWangdian=mWangdianService.getByparameter("wang_ddz", name,Wangdian.class);
			if(mWangdian==null||mWangdian.getDeng_j()!=3)
			throw new RunException("等级错误，只能选择3级");
			return;
		default:
			break;
		}
	}
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Wangdian mWangdian,Integer page,Integer rows) throws Exception {
        return sendTrueData(mWangdianService.getALL(mWangdian,page,rows,_where.like));
    }

    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mWangdianService.getById(id));
    }

}
