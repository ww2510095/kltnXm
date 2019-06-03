package com.example.fw.main.c;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.fw.base.*;
import com.example.fw.main.s.Qiandai_key_list_yuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseService._where;
import com.example.fw.base.Util.GsonUtil;
import com.example.fw.main.b.Qiandai_a;
import com.example.fw.main.b.Qiandai_a.Qiandai_a_tab_name;
import com.example.fw.main.b.Qiandai_key_list;
import com.example.fw.main.s.Qiandai_aService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
@RestController
@RequestMapping("/Qiandai_a")
@Api(tags = "物流管理")
public class Qiandai_aController extends BaseController {
    @Autowired
    protected Qiandai_aService mQiandai_aService;
    @Autowired
    protected Qiandai_key_list_yuanService mQiandai_key_list_yuanService;

    @ApiOperation(value = "移交单", response = RequestType.class)
    @RequestMapping(value ="/yjd_save", method = RequestMethod.POST)
    @ApiImplicitParams({
    	 @ApiImplicitParam(name = "array", value = "单据明细的的josnarray</br>示例:<font color=\"#FF0000\">[{\"id\":1,\"ying_rsl\":20},{\"id\":2,\"ying_rsl\":10}]</font>\n", dataType = "String", required = true),
    })
    @Transactional
    public RequestType yjd_save(Qiandai_a mQiandai_a,String array, @ApiIgnore() HttpSession httpSession) throws Exception {
    	String yjd=Qiandai_a.Qiandai_a_tab_name.yjd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a.getShou_hwd();
    	if(Stringutil.isBlank(mQiandai_a.getId())){
    		mQiandai_a.setDeng_j(getUser(httpSession).getDeng_j());
    	      mQiandai_a.setZhi_dr(getUser(httpSession).getUname());	//制单人
    	        if(Stringutil.isBlank(mQiandai_a.getDan_jzt()))return sendFalse("单据不可为空");
    	        mQiandai_a.setFa_hwd(getUser(httpSession).getSuo_swd());	//发货网点
    	        if(Stringutil.isBlank(mQiandai_a.getShou_hwd())) return sendFalse("收货网点不可为空");	//收货网点
    	        mQiandai_a.setTab_name(yjd);
    	        List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    	        if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
    	        mQiandai_aService.add(mQiandai_a);
    	        for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {

    	        	update_add_Qiandai_key_list(qiandai_key_list,yjd+mQiandai_a.getId(),true);
					qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    			}
    	        mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), yjd+mQiandai_a.getId());
    	        return sendTrueMsg("移交成功");
    	}else{
    		if(Stringutil.isBlank(mQiandai_a.getFa_hwd())){
    			Qiandai_a mQiandai_a_yuan = mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
        		yjd=Qiandai_a.Qiandai_a_tab_name.yjd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a_yuan.getFa_hwd();
    		}
    			mQiandai_aService.updateBySelect(mQiandai_a);
    			if(!"管理员".equals(getUser(httpSession).getSuo_swd())){
    			Qiandai_a mQiandai_a1=mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
    			if("正常".equals(mQiandai_a1.getDan_jzt())){
    				if(!mQiandai_a1.getShou_hwd().equals(getUser(httpSession).getSuo_swd()))
    					throw new RunException("该单据只有"+mQiandai_a1.getShou_hwd()+"网点的人员才可以审核");
    			}
    			}
//    			Qiandai_key_list mqiandai_key_list = new Qiandai_key_list();
//    			mqiandai_key_list.setTab_name(yjd+mQiandai_a.getId());
//    			mQiandai_key_listService.deleteBySelect(mqiandai_key_list);

    	        List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    	        for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {

    	        	Integer srsl = qiandai_key_list.getShi_rsl();
    	        	qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,yjd+mQiandai_a.getId(),false);
    	        	qiandai_key_list.setShi_rsl(srsl);
    	        	if(qiandai_key_list.getYing_rsl()==null)qiandai_key_list.setYing_rsl(0);
        			if(qiandai_key_list.getShi_rsl()==null)qiandai_key_list.setShi_rsl(0);
    	        	qiandai_key_list.setSun_hls(qiandai_key_list.getYing_rsl()-qiandai_key_list.getShi_rsl());
					qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    	        	mQiandai_key_listService.updateBySelect(qiandai_key_list);

        			}
    	        mQiandai_listService.add(mQiandai_a.getDan_jzt(),getUser(httpSession).getUname(), getUser(httpSession).getNickname(), yjd+mQiandai_a.getId());
    	        return sendTrueMsg("修改成功");
    	}


    }
    private Qiandai_key_list update_add_Qiandai_key_list(Qiandai_key_list qiandai_key_list,String tab_name,boolean add) throws Exception {
    	Long keyid=qiandai_key_list.getId();
    	Integer sl = qiandai_key_list.getYing_rsl();
    	if(add)
    		qiandai_key_list=mQiandai_keyService.getById(qiandai_key_list,Qiandai_key_list.class);
    	else{
    		Sql msql = new Sql();
    		msql.setSql("select * from Qiandai_key_list_yuan where tab_name='"+tab_name+"' and keyid='"+qiandai_key_list.getId()+"'");
//    		mQiandai_key_listService.yuan=true;
//    		qiandai_key_list=mQiandai_key_listService.getByparameter("tab_name",tab_name,Qiandai_key_list.class);
            try {
                qiandai_key_list=IBeanUtil.Map2JavaBean(mQiandai_key_listService.exeSelectSql(msql).get(0), Qiandai_key_list.class);

            }catch (Exception e){

            }
    		if(qiandai_key_list==null){
                msql.setSql("select * from Qiandai_key_list_yuan where id='"+qiandai_key_list.getId()+"'");
                qiandai_key_list=IBeanUtil.Map2JavaBean(mQiandai_key_listService.exeSelectSql(msql).get(0), Qiandai_key_list.class);
            }
    	}
    	
    	qiandai_key_list.setTab_name(tab_name);
    	qiandai_key_list.setKeyid(keyid);
    	if(add){
    		qiandai_key_list.setId(null);
    		qiandai_key_list.setYing_rsl(sl);
			qiandai_key_list.setType(qiandai_key_list.getType());
    		mQiandai_key_listService.add(qiandai_key_list);
    		
    	}
    	return qiandai_key_list;
	}
    
    
    @ApiOperation(value = "领用单", response = RequestType.class)
    @RequestMapping(value ="lyd_save", method = RequestMethod.POST)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "array", value = "单据明细的josnarray</br>示例:<font color=\"#FF0000\">[{\"id\":1,\"ying_rsl\":20},{\"id\":2,\"ying_rsl\":10}]</font>\n", dataType = "String", required = true),
    })
    @Transactional
    public RequestType lyd_save(Qiandai_a mQiandai_a,String array, @ApiIgnore() HttpSession httpSession) throws Exception {
    	Sql msql = new Sql();
    	String lyd=Qiandai_a.Qiandai_a_tab_name.lyd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a.getFa_hwd();
    	String yjd=Qiandai_a.Qiandai_a_tab_name.yjd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a.getFa_hwd();
    	if(Stringutil.isBlank(mQiandai_a.getId())){
    		mQiandai_a.setDeng_j(getUser(httpSession).getDeng_j());
    		mQiandai_a.setZhi_dr(getUser(httpSession).getUname());	//制单人
    		if(Stringutil.isBlank(mQiandai_a.getDan_jzt()))return sendFalse("单据不可为空");
    		mQiandai_a.setShou_hwd(getUser(httpSession).getSuo_swd());	//发货网点
    		if(Stringutil.isBlank(mQiandai_a.getFa_hwd())) return sendFalse("发货网点不可为空");	//收货网点
    		mQiandai_a.setTab_name(lyd);
    		List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    		if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
    		mQiandai_aService.add(mQiandai_a);
    		for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    			Long keyid =qiandai_key_list.getId();
    			qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,lyd+mQiandai_a.getId(),true);
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    			
    			int sl = qiandai_key_list.getYing_rsl();
    			msql.setSql("select ((select nvl(sum(shi_rsl),0)  from Qiandai_key_list where tab_name like '%"+yjd
    					+"%' and keyid='"+keyid+"')-(select nvl(sum(shi_rsl),0)  from Qiandai_key_list where tab_name like '%"
    					+lyd+"%' and keyid='"+keyid+"')) sl from dual");
    			int a =Integer.parseInt(mQiandai_key_listService.exeSelectSql(msql).get(0).get("SL").toString());
    			a=a-sl;
    			if(a<0)
    				throw new RunException(qiandai_key_list.getMing_c()+"可领用数量不足");
    			
    			
    			
    		}
    		mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), lyd+mQiandai_a.getId());
    		return sendTrueMsg("申请成功");
    	}else{
    		if(Stringutil.isBlank(mQiandai_a.getFa_hwd())){
    			Qiandai_a mQiandai_a_yuan = mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
        		lyd=Qiandai_a.Qiandai_a_tab_name.lyd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a_yuan.getFa_hwd();
    		}
    		mQiandai_aService.updateBySelect(mQiandai_a);
    		if(!"管理员".equals(getUser(httpSession).getSuo_swd())){
    			Qiandai_a mQiandai_a1=mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
    			if("正常".equals(mQiandai_a1.getDan_jzt())){
    				if(!mQiandai_a1.getFa_hwd().equals(getUser(httpSession).getSuo_swd()))
    					throw new RunException("该单据只有"+mQiandai_a1.getShou_hwd()+"网点的人员才可以审核");
    			}	
    		}
    		
//    		Qiandai_key_list mqiandai_key_list = new Qiandai_key_list();
//    		mqiandai_key_list.setTab_name(yjd+mQiandai_a.getId());
//    		mQiandai_key_listService.deleteBySelect(mqiandai_key_list);
    		
    		List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    		for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
	        	Integer srsl = qiandai_key_list.getShi_rsl();
	        	qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,lyd+mQiandai_a.getId(),false);
	        	qiandai_key_list.setShi_rsl(srsl);
	        	if(qiandai_key_list.getYing_rsl()==null)qiandai_key_list.setYing_rsl(0);
    			if(qiandai_key_list.getShi_rsl()==null)qiandai_key_list.setShi_rsl(0);
	        	qiandai_key_list.setSun_hls(qiandai_key_list.getYing_rsl()-qiandai_key_list.getShi_rsl());
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
	        	mQiandai_key_listService.updateBySelect(qiandai_key_list);
	        	
    			}
    		mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), lyd+mQiandai_a.getId());
    		return sendTrueMsg("修改成功");
    	}
    	
    	
    }
    
    @ApiOperation(value = "借用单", response = RequestType.class)
    @RequestMapping(value ="jyd_save", method = RequestMethod.POST)
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "array", value = "单据明细的josnarray</br>示例:<font color=\"#FF0000\">[{\"id\":1,\"ying_rsl\":20},{\"id\":2,\"ying_rsl\":10}]</font>\n", dataType = "String", required = true),
    })
    @Transactional
    public RequestType jyd_save(Qiandai_a mQiandai_a,String array, @ApiIgnore() HttpSession httpSession) throws Exception {
    	//借用单，上级填写单子借用给下级，从上往下
    	String jyd=Qiandai_a.Qiandai_a_tab_name.jyd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a.getFa_hwd();
    	if(Stringutil.isBlank(mQiandai_a.getId())){
    		mQiandai_a.setDeng_j(getUser(httpSession).getDeng_j());
  	      mQiandai_a.setZhi_dr(getUser(httpSession).getUname());	//制单人
	        if(Stringutil.isBlank(mQiandai_a.getDan_jzt()))return sendFalse("单据不可为空");
	        mQiandai_a.setShou_hwd(getUser(httpSession).getSuo_swd());	//发货网点
	        if(Stringutil.isBlank(mQiandai_a.getFa_hwd())) return sendFalse("发货网点不可为空");	//收货网点
	        mQiandai_a.setTab_name(jyd);
	        List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
	        if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
	        if(!"草稿".equals(mQiandai_a.getDan_jzt()))mQiandai_a.setDan_jzt("正常");
	        mQiandai_aService.add(mQiandai_a);
	        for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
	        	qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,jyd+mQiandai_a.getId(),true);
	        	qiandai_key_list.setShi_rsl(qiandai_key_list.getYing_rsl());
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
	        	
	        	mQiandai_key_listService.updateBySelect(qiandai_key_list);
			}
	        
	        mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), jyd+mQiandai_a.getId());
	        return sendTrueMsg("提交成功");
    	}else{
    		if(Stringutil.isBlank(mQiandai_a.getFa_hwd())){
    			Qiandai_a mQiandai_a_yuan = mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
        		jyd=Qiandai_a.Qiandai_a_tab_name.jyd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a_yuan.getFa_hwd();
    		}
    		
    		mQiandai_aService.updateBySelect(mQiandai_a);
    		if(!"管理员".equals(getUser(httpSession).getSuo_swd())){
    			Qiandai_a mQiandai_a1=mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
    			if("正常".equals(mQiandai_a1.getDan_jzt())){
    					throw new RunException("该单据只有管理员才可以审核");
    			}
    		}
    		List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    		for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    			Integer srsl = qiandai_key_list.getShi_rsl();
    			qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,jyd+mQiandai_a.getId(),false);
    			qiandai_key_list.setShi_rsl(srsl);
    			if(qiandai_key_list.getYing_rsl()==null)qiandai_key_list.setYing_rsl(0);
    			if(qiandai_key_list.getShi_rsl()==null)qiandai_key_list.setShi_rsl(0);
    			qiandai_key_list.setSun_hls(qiandai_key_list.getYing_rsl()-qiandai_key_list.getShi_rsl());
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    			mQiandai_key_listService.updateBySelect(qiandai_key_list);
    			
    		}
    		mQiandai_listService.add(mQiandai_a.getDan_jzt(),getUser(httpSession).getUname(), getUser(httpSession).getNickname(), jyd+mQiandai_a.getId());
    		return sendTrueMsg("修改成功");
    	}
    	
    	
    }
    @ApiOperation(value = "归还单", response = RequestType.class)
    @RequestMapping(value ="ghd_save", method = RequestMethod.POST)
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "array", value = "单据明细的josnarray</br>示例:<font color=\"#FF0000\">[{\"id\":1,\"ying_rsl\":20},{\"id\":2,\"ying_rsl\":10}]</font>\n", dataType = "String", required = true),
    })
    @Transactional
    public RequestType ghd_save(Qiandai_a mQiandai_a,String array, @ApiIgnore() HttpSession httpSession) throws Exception {
    	//借用单，上级填写单子借用给下级，从上往下
    	String ghd=Qiandai_a.Qiandai_a_tab_name.ghd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a.getFa_hwd();
    	if(Stringutil.isBlank(mQiandai_a.getId())){
    		mQiandai_a.setDeng_j(getUser(httpSession).getDeng_j());
    		mQiandai_a.setZhi_dr(getUser(httpSession).getUname());	//制单人
    		if(Stringutil.isBlank(mQiandai_a.getDan_jzt()))return sendFalse("单据不可为空");
    		mQiandai_a.setFa_hwd(getUser(httpSession).getSuo_swd());	//发货网点
    		if(Stringutil.isBlank(mQiandai_a.getShou_hwd())) return sendFalse("收货网点不可为空");	//收货网点
    		mQiandai_a.setTab_name(ghd);
    		List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    		if(mQiandai_key_listlist.size()==0)return sendFalse("单据错误");
    		mQiandai_aService.add(mQiandai_a);
    		for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    			qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,ghd+mQiandai_a.getId(),true);
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    		}
    		mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), ghd+mQiandai_a.getId());
    		return sendTrueMsg("提交成功");
    	}else{
    		if(Stringutil.isBlank(mQiandai_a.getFa_hwd())){
    			Qiandai_a mQiandai_a_yuan = mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
        		ghd=Qiandai_a.Qiandai_a_tab_name.ghd.toString()+"_"+getUser(httpSession).getSuo_swd()+"_"+mQiandai_a_yuan.getFa_hwd();
    		}
    		mQiandai_aService.updateBySelect(mQiandai_a);
    		if(!"管理员".equals(getUser(httpSession).getSuo_swd())){
    			Qiandai_a mQiandai_a1=mQiandai_aService.getById(mQiandai_a,Qiandai_a.class);
    			if("正常".equals(mQiandai_a1.getDan_jzt())){
    				if(!mQiandai_a1.getShou_hwd().equals(getUser(httpSession).getSuo_swd()))
    					throw new RunException("该单据只有"+mQiandai_a1.getShou_hwd()+"网点的人员才可以审核");
    			}
    		}
    		List<Qiandai_key_list> mQiandai_key_listlist = GsonUtil.fromJsonList(array,Qiandai_key_list.class);
    		for (Qiandai_key_list qiandai_key_list : mQiandai_key_listlist) {
    			Integer srsl = qiandai_key_list.getShi_rsl();
    			qiandai_key_list=update_add_Qiandai_key_list(qiandai_key_list,ghd+mQiandai_a.getId(),false);
    			qiandai_key_list.setShi_rsl(srsl);
    			if(qiandai_key_list.getYing_rsl()==null)qiandai_key_list.setYing_rsl(0);
    			if(qiandai_key_list.getShi_rsl()==null)qiandai_key_list.setShi_rsl(0);
    			qiandai_key_list.setSun_hls(qiandai_key_list.getYing_rsl()-qiandai_key_list.getShi_rsl());
				qiandai_key_list.setType(mQiandai_a.getDan_jzt());
    			mQiandai_key_listService.updateBySelect(qiandai_key_list);
    			
    		}
    		mQiandai_listService.add(mQiandai_a.getDan_jzt(), getUser(httpSession).getUname(), getUser(httpSession).getNickname(), ghd+mQiandai_a.getId());
    		return sendTrueMsg("修改成功");
    	}
    	
    	
    }
    
    
    
    @ApiOperation(value = "钱袋管理列表，支持所有参数联合解锁，tab_name", response = Qiandai_a.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "tab_name", value = "数据表</br>"
        		+ "qdcg:钱袋采购</br>"
        		+ "lyd:领用单</br>"
        		+ "jyd:借用单</br>"
        		+ "ghd:归还单</br>"
        		+ "yjd:移交单", dataType = "String", required = true),
        @ApiImplicitParam(name = "star", value = "开始时间(默认无限靠前)", dataType = "String", required = false),
        @ApiImplicitParam(name = "end", value = "结束时间(默认当前时间)", dataType = "String", required = false),
    })
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Qiandai_a mQiandai_a,Integer page,Integer rows, Long star,Long end,@ApiIgnore() HttpSession httpSession) throws Exception {
    	if(star==null)star=0L;
    	if(end==null)end=System.currentTimeMillis();
//		mQiandai_a.setTab_name(getUser(httpSession).getYwd()==null?getUser(httpSession).getSuo_swd():getUser(httpSession).getYwd());
    	for (Qiandai_a_tab_name e : Qiandai_a_tab_name.values()) {
				if(e.toString().equals(mQiandai_a.getTab_name())){
					Sql msql = new Sql();
					msql.setPage(page);
					msql.setRows(rows);
					msql.setOrderbykey("id");
					msql.setOrderbytype(1);
					switch (getUser(httpSession).getDeng_j()) {
					case 1:
						msql.setSql("select * from (select * from Qiandai_a "+mQiandai_aService.getWhere(mQiandai_a, _where.like,BaseService._or_and.or)+") where id>"+
								star+" and id<"+end);
						break;
					case 2:
						msql.setSql("select * from (select * from Qiandai_a "+mQiandai_aService.getWhere(mQiandai_a, _where.like,BaseService._or_and.or)+") where id>"+
								star+" and id<"+end+" and fa_hwd in(select wang_ddz from wangdian where a2='"+getUser(httpSession).getSuo_swd()+"')");
						break;
					case 3:
						msql.setSql("select * from (select * from Qiandai_a "+mQiandai_aService.getWhere(mQiandai_a, _where.like,BaseService._or_and.or)+") where id>"+
								star+" and id<"+end+" and fa_hwd in(select wang_ddz from wangdian where a3='"+getUser(httpSession).getSuo_swd()+"')");
						break;
					case 4:
						msql.setSql("select * from (select * from Qiandai_a "+mQiandai_aService.getWhere(mQiandai_a, _where.like,BaseService._or_and.or)+") where id>"+
								star+" and id<"+end+" and fa_hwd ='"+getUser(httpSession).getSuo_swd()+"'");
						break;
					}
					
					 return sendTrueData(mQiandai_aService.exeSelectSql(msql));
				}
			
		}
        return sendFalse("参数是不是选错了");
    }

    @ApiOperation(value = "查询单个的钱袋管理id不可为空", response = Qiandai_a.class)
    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mQiandai_aService.getById(id));
    }

}
