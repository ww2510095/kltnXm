package com.example.fw.main.b;

import java.util.List;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Zhuzhijiagou extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="quan_x",value = "权限")
	@ApiModelProperty(value="权限",example="权限")
	private String quan_x;	//权限	
	@ApiParam(name="ukey",value = "角色")
	@ApiModelProperty(value="角色",example="角色")
	private String ukey;	//角色

	@ApiParam(name="miao_s",value = "描述")
	@ApiModelProperty(value="描述",example="描述")
	private String miao_s;	//描述
	
	private List<String> lists;}
