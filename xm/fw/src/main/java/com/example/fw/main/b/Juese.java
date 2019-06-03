package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Juese extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="uphone",value = "账号")
	@ApiModelProperty(value="账号",example="账号")
	private String uphone;	//账号	
	@ApiParam(name="zhuzhijiagou_name",value = "角色")
	@ApiModelProperty(value="角色",example="角色")
	private String zhuzhijiagou_name;	//角色
	@ApiParam(name="miao_s",value = "描述")
	@ApiModelProperty(value="描述",example="描述")
	private String miao_s;	//角色
	private String[] key;
}
