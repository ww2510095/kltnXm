package com.example.fw.main.c;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseController;
import com.example.fw.base.BaseService._where;
import com.example.fw.base.RequestType;
import com.example.fw.base.Util.GsonUtil;
import com.example.fw.main.b.Juese;
import com.example.fw.main.b.Zhuzhijiagou;
import com.example.fw.main.s.JuseService;
import com.example.fw.main.s.ZhuzhijiagouService;
import com.myjar.Stringutil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/Zhuzhijiagou")
@Api(tags = "权限")
public class ZhuzhijiagouController extends BaseController {
    @Autowired
    protected ZhuzhijiagouService mZhuzhijiagouService;
    @Autowired
    protected JuseService mJuseService;
    //所有可用菜单
    public static List<String> caidan = null;
    //所有可用按钮
    public static List<String> anniu = null;

    @RequestMapping(value ="/save_j", method = RequestMethod.POST)
	@ApiOperation(value = "更新或添加角色，每个用户只能存在一条数据，如果用户有数据了则忽略id，直接为更新", response = RequestType.class)
    @ApiImplicitParams({
   	 @ApiImplicitParam(name = "array", value = "权限组成的josnarray</br>示例:<font color=\"#FF0000\">[\"权限1\",\"权限2\"]\n</font>\n", dataType = "String", required = true),
   })
    public RequestType save_j(Zhuzhijiagou mZhuzhijiagou,String array) throws Exception {
    	List<String> key =GsonUtil.fromJsonList(array, String.class);
    	if(key.size()==0)return sendFalse("权限个数不可为0");
    	String akey = "";
    	for (String string : key) {
    		akey=akey+string+";";	
		}
    	mZhuzhijiagou.setQuan_x(akey);
        if(Stringutil.isBlank(mZhuzhijiagou.getId())){
        	if(Stringutil.isBlank(mZhuzhijiagou.getUkey()))return sendFalse("角色名字不可为空");
        	Zhuzhijiagou mZhuzhijiagou1 = mZhuzhijiagouService.getByparameter("ukey", mZhuzhijiagou.getUkey(),Zhuzhijiagou.class);
        	if(mZhuzhijiagou1!=null){
        		mZhuzhijiagou.setId(mZhuzhijiagou1.getId());
        		mZhuzhijiagouService.updateBySelect(mZhuzhijiagou);
        		 return sendTrueMsg("更新成功");
        	}else{
        		  mZhuzhijiagouService.add(mZhuzhijiagou);
        		  return sendTrueMsg("添加成功");
        	}
           
        }else{
            mZhuzhijiagouService.updateBySelect(mZhuzhijiagou);
            return sendTrueMsg("更新成功");
        }

    }
    @RequestMapping(value ="/list_j", method = RequestMethod.POST)
    @ApiOperation(value = "查询角色列表，所有字段均可模糊解锁", response = RequestType.class)
    public RequestType list_j(Zhuzhijiagou mZhuzhijiagou,Integer page,Integer rows) throws Exception {
    	return sendTrueData(mZhuzhijiagouService.getALL(mZhuzhijiagou,page,rows,_where.like));
    	
    }
    @RequestMapping(value ="/getByid_j", method = RequestMethod.POST)
   	@ApiOperation(value = "根据id查询角色权限", response = RequestType.class)
       public RequestType getByid(String id) throws Exception {
           if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
           Zhuzhijiagou mZhuzhijiagou= mZhuzhijiagouService.getById(id,Zhuzhijiagou.class);
           mZhuzhijiagou.setLists(Arrays.asList(mZhuzhijiagou.getQuan_x().split(";")));
           return sendTrueData(mZhuzhijiagou);
       }
    
    @RequestMapping(value ="/save", method = RequestMethod.POST)
	@ApiOperation(value = "添加角色或修改用户角色", response = RequestType.class)
    @ApiImplicitParams({
      	 @ApiImplicitParam(name = "array", value = "角色组成的josnarray</br>示例:<font color=\"#FF0000\">[\"角色1\",\"角色2\"]\n</font>\n", dataType = "String", required = true),
      })
    public RequestType save(Juese mJuese,String array) throws Exception {
    	List<String> key =GsonUtil.fromJsonList(array, String.class);
    	if(key.size()==0)return sendFalse("权限个数不可为0");
    	String akey = "";
    	for (String string : key) {
    		akey=akey+string+";";	
		}
    	mJuese.setZhuzhijiagou_name(akey);
        if(Stringutil.isBlank(mJuese.getId())){
        	if(Stringutil.isBlank(mJuese.getUphone()))return sendFalse("账号字不可为空");
        	Zhuzhijiagou mZhuzhijiagou1 = mZhuzhijiagouService.getByparameter("Uphone", mJuese.getUphone(),Zhuzhijiagou.class);
        	if(mZhuzhijiagou1!=null){
        		mJuese.setId(mZhuzhijiagou1.getId());
        		mJuseService.updateBySelect(mJuese);
        		 return sendTrueMsg("更新成功");
        	}else{
        		mJuseService.add(mJuese);
        		  return sendTrueMsg("添加成功");
        	}
           
        }else{
        	mJuseService.updateBySelect(mJuese);
            return sendTrueMsg("更新成功");
        }

    }
  
    @RequestMapping(value ="/getbyid", method = RequestMethod.POST)
    @ApiOperation(value = "查询某个用户的角色", response = RequestType.class)
    @ApiImplicitParams({
     	 @ApiImplicitParam(name = "uphone", value = "账号", dataType = "String", required = true),
     })
    public RequestType getbyid(String uphone) throws Exception {
    	Juese mjuese = mJuseService.getByparameter("uphone", uphone,Juese.class);
    	mjuese.setKey(mjuese.getZhuzhijiagou_name().split(";"));
    	return sendTrueData(mjuese);
    }
    

   
    //查询可用菜单
    @RequestMapping(value ="/listall_caidan", method = RequestMethod.POST)
	@ApiOperation(value = "查询系统所有可用菜单", response = RequestType.class)
    public RequestType listall_caidan() throws Exception {
        return sendTrueData(caidan);
    }
    //查询可用菜单
    @RequestMapping(value ="/listall_anniu", method = RequestMethod.POST)
	@ApiOperation(value = "查询系统所有可用按钮", response = RequestType.class)
    public RequestType listall_anniu() throws Exception {
    	return sendTrueData(anniu);
    }
    

}
