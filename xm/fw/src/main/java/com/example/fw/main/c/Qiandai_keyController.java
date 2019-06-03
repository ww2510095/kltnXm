package com.example.fw.main.c;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.fw.base.Sql;
import com.example.fw.main.s.Qiandai_key_list_yuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseController;
import com.example.fw.base.RequestType;
import com.example.fw.base.Util.GsonUtil;
import com.example.fw.main.b.Qiandai_a;
import com.example.fw.main.b.Qiandai_key;
import com.example.fw.main.b.Qiandai_key_list;
import com.example.fw.main.s.Qiandai_aService;
import com.example.fw.main.s.Qiandai_keyService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/Qiandai_key")
@Api(tags = "钱袋管理")
public class Qiandai_keyController extends BaseController {
    @Autowired
    protected Qiandai_keyService mQiandai_keyService;
    @Autowired
    protected Qiandai_aService mQiandai_aService;

    @ApiOperation(value = "钱袋信息添加或修改，id不为空时修改，否则添加", response = RequestType.class)
    @RequestMapping(value ="/save", method = RequestMethod.POST)
    public RequestType save(Qiandai_key mQiandai_key, @ApiIgnore() HttpSession httpSession) throws Exception {
        if(Stringutil.isBlank(mQiandai_key.getId())){
        	 if(mQiandai_keyService.getByparameter("ming_c", mQiandai_key.getMing_c())!=null)throw new RunException("钱袋:"+mQiandai_key.getMing_c()+"已存在");
            mQiandai_key.setXiu_gr(getUser(httpSession).getUname());
            mQiandai_key.setXiu_gsj(System.currentTimeMillis()+"");
            mQiandai_key.setChuang_jr(mQiandai_key.getXiu_gr());
            mQiandai_key.setQian_dzt("正常");
           
            try {
            	mQiandai_keyService.add(mQiandai_key);
			} catch (Exception e) {
				throw new RunException("钱袋名称已存在");
			}
            
            return sendTrueMsg("添加成功");
        }else{
            mQiandai_key.setXiu_gr(getUser(httpSession).getUname());
            mQiandai_key.setXiu_gsj(System.currentTimeMillis()+"");
            try {
            	mQiandai_keyService.updateBySelect(mQiandai_key);
			} catch (Exception e) {
				throw new RunException("钱袋名称已存在");
			}
            
            return sendTrueMsg("更新成功");
        }

    }
    @ApiOperation(value = "钱袋信息列表", response = RequestType.class)
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Qiandai_key mQiandai_key,Integer rows,Integer page) throws Exception {
    return sendTrueData(mQiandai_keyService.getALL(mQiandai_key,page,rows));	
    }
    @ApiOperation(value = "钱袋采购", response = RequestType.class)
    @RequestMapping(value ="/list_save", method = RequestMethod.POST)
    @ApiImplicitParams({
    	 @ApiImplicitParam(name = "array", value = "单据明细的id组成的josnarray</br>示例:<font color=\"#FF0000\">[{\"id\":1,\"ying_rsl\":20},{\"id\":2,\"ying_rsl\":10}]</font>\n", dataType = "String", required = true),
    })
    @Transactional
    public RequestType list_save(Qiandai_a mQiandai_a,String array, @ApiIgnore() HttpSession httpSession) throws Exception {
    	String system = "system";
    	String str = Qiandai_a.Qiandai_a_tab_name.qdcg.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+system;
    	if(Stringutil.isBlank(mQiandai_a.getId())){
    		  mQiandai_a.setZhi_dr(getUser(httpSession).getUname());	//制单人
    	        if(Stringutil.isBlank(mQiandai_a.getDan_jzt()))return sendFalse("单据不可为空");
    	        mQiandai_a.setFa_hwd(system);	//发货网点
    	        mQiandai_a.setDeng_j(getUser(httpSession).getDeng_j());
    	        mQiandai_a.setShou_hwd(getUser(httpSession).getSuo_swd());	//收货网点
    	        mQiandai_a.setTab_name(str);
    	        List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    	        if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
    	        mQiandai_aService.add(mQiandai_a);
    	        for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    	        	int sl = qiandai_key_list.getYing_rsl();
    	        	qiandai_key_list=mQiandai_keyService.getById(qiandai_key_list,Qiandai_key_list.class);
    	        	qiandai_key_list.setTab_name(str+mQiandai_a.getId());
    	        	qiandai_key_list.setYing_rsl(sl);
    	        	qiandai_key_list.setShi_rsl(sl);
    	        	qiandai_key_list.setSun_hls(0);
    	        	qiandai_key_list.setKeyid(qiandai_key_list.getId());
    	        	qiandai_key_list.setId(null);
    	        	mQiandai_key_listService.add(qiandai_key_list);
    			}
    	        mQiandai_listService.add(mQiandai_a.getDan_jzt(), mQiandai_a.getZhi_dr(), getUser(httpSession).getNickname(), str+mQiandai_a.getId());
    	        return sendTrueMsg("采购成功");

    	}else{
    		mQiandai_aService.updateBySelect(mQiandai_a);
//    		Qiandai_key_list mQiandai_key_list =new Qiandai_key_list();
//    		mQiandai_key_list.setTab_name(str+mQiandai_a.getId());
//    		mQiandai_key_list_yuanService.deleteBySelect(mQiandai_key_list);
    	        List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    	       // if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
    	      
    	        for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    	        	int sl = qiandai_key_list.getYing_rsl();

    	        	qiandai_key_list=mQiandai_keyService.getById(qiandai_key_list,Qiandai_key_list.class);
    	        	qiandai_key_list.setTab_name(str+mQiandai_a.getId());
    	        	qiandai_key_list.setYing_rsl(sl);
    	        	qiandai_key_list.setShi_rsl(sl);
    	        	qiandai_key_list.setSun_hls(0);
    	        	qiandai_key_list.setKeyid(qiandai_key_list.getId());
//    	        	qiandai_key_list.setId(null);
    	        	mQiandai_key_listService.updateBySelect(qiandai_key_list);
    			}
    	        mQiandai_listService.add(mQiandai_a.getDan_jzt(), mQiandai_a.getZhi_dr(), getUser(httpSession).getNickname(), str+mQiandai_a.getId());
    	        return sendTrueMsg("修改成功");

    	}
    	
      
    }
    
   
    @ApiOperation(value = "查询单个的钱袋管理id不可为空", response = Qiandai_key.class)
    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mQiandai_keyService.getById(id));
    }

}
