package com.example.fw.main.c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseController;
import com.example.fw.base.BaseService._where;
import com.example.fw.base.RequestType;
import com.example.fw.base.Util.GsonUtil;
import com.example.fw.main.b.Juese;
import com.example.fw.main.b.User;
import com.example.fw.main.s.JuseService;
import com.example.fw.main.s.ZhuzhijiagouService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController extends BaseController {
	
	@Autowired
	protected JuseService mJuseService;
	  @Autowired
	    protected ZhuzhijiagouService mZhuzhijiagouService;

    @RequestMapping(value ="/save", method = RequestMethod.POST)
    @ApiOperation(value = "添加或修改用户，id不为空时修改，否则添加", response = RequestType.class)
    @Transactional
    @ApiImplicitParams({
      	 @ApiImplicitParam(name = "array", value = "角色组成的josnarray</br>示例:<font color=\"#FF0000\">[\"角色1\",\"角色2\"]\n</font>\n", dataType = "String", required = true),
      })
    public RequestType save(User mUser,String array) throws Exception {
        if(mUser.getId()==null){
            if(Stringutil.isBlank(mUser.getUname()))
                return  sendFalse("账号不可为空");
            if(Stringutil.isBlank(mUser.getPwd()))
                return  sendFalse("密码不可为空");
            if(Stringutil.isBlank(mUser.getNickname()))
                mUser.setNickname(mUser.getUname());

            if(mJiabiService.getByparameter("uname",mUser.getUname())!=null)
                return  sendFalse("账号已存在");
            if(mUser.getZhuang_t()==null)
            mUser.setZhuang_t(1);
            if(!Stringutil.isBlank(mUser.getYou_xq()))
            	mUser.setYou_xq(System.currentTimeMillis()+(mUser.getYou_xq()*86400000L));
            mJiabiService.add(mUser);
            updatejue_se(array,mUser.getUname() );
            return  sendTrueMsg("添加成功");
        }else{
        	  if(!Stringutil.isBlank(mUser.getYou_xq())){
        		  if(mUser.getYou_xq().toString().length()!=13)
        				mUser.setYou_xq(System.currentTimeMillis()+(mUser.getYou_xq()*86400000L));
        	  }
              
            mJiabiService.updateBySelect(mUser);
            updatejue_se(array,mUser.getUname() );
            return  sendTrueMsg("更新成功");
        }

    }
    private void updatejue_se(String array,String uname) throws Exception {

    	List<String> key =GsonUtil.fromJsonList(array, String.class);
    	if(key.size()==0)throw new RunException("权限个数不可为0");
    	String akey = "";
    	for (String string : key) {
    		akey=akey+string+";";	
		}
    	if(Stringutil.isBlank(uname))throw new RunException("账号字不可为空");
    	Juese mJuese = new Juese();
    	mJuese.setUphone(uname);
    	mJuseService.deleteBySelect(mJuese);
    	mJuese.setUphone(uname);
    	mJuese.setZhuzhijiagou_name(akey);
    	mJuseService.add(mJuese);
    	
    

    

	}
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    @ApiOperation(value = "用户列表，支持所有参数模糊解锁", response = User.class)
    public RequestType list(User User,Integer page,Integer rows) throws Exception {
        List<User> listuser = (List<com.example.fw.main.b.User>) mJiabiService.getALL(User,page,rows,_where.like);
        for (User muser: listuser) {
            if(muser.getYou_xq()==null)muser.setYou_xq(99999L);
            else
             muser.setYou_xq((muser.getYou_xq()-System.currentTimeMillis())/86400000);
        }
        return sendTrueData(listuser);
    }


}
