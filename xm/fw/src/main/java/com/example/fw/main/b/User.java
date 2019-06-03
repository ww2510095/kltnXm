package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@ApiModel
@Data
public class User extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//id	
	@ApiParam(name="zhuang_t",value = "用户状态")
	@ApiModelProperty(value="用户状态",example="用户状态")
	private Integer zhuang_t;	//用户状态	
	@ApiParam(name="uname",value = "账号")
	@ApiModelProperty(value="账号",example="账号")
	private String uname;	//账号	
	@ApiParam(name="pwd",value = "密码")
	@ApiModelProperty(value="密码",example="密码")
	private String pwd;	//密码	
	@ApiParam(name="nickname",value = "称谓")
	@ApiModelProperty(value="称谓",example="称谓")
	private String nickname;	//称谓	
	@ApiParam(name="suo_swd",value = "所属网点")
	@ApiModelProperty(value="所属网点",example="所属网点")
	private String suo_swd;	//所属网点	
	@ApiParam(name="shou_jh",value = "手机号")
	@ApiModelProperty(value="手机号",example="手机号")
	private String shou_jh;	//手机号
	@ApiModelProperty(value="有效期,单位天",example="365")
	private Long you_xq;	//有效期
	
	
	
	@ApiModelProperty(value="用户的权限",example="")
	   private String[] key; // 能看的按钮
	@ApiModelProperty(value="用户的等级",example="")
	    private Integer deng_j; // 网点等级


//	@ApiParam(name="ywd",value = "原网点")
//	@ApiModelProperty(value="原网点",example="原网点")
//	private String ywd;	//原网点
}
