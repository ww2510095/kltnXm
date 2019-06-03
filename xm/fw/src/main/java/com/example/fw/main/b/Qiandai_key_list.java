package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Qiandai_key_list extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="ming_c",value = "名称")
	@ApiModelProperty(value="名称",example="名称")
	private String ming_c;	//名称	
	@ApiParam(name="shi_ylx",value = "使用类型")
	@ApiModelProperty(value="使用类型",example="使用类型")
	private String shi_ylx;	//使用类型	
	@ApiParam(name="quan_blx",value = "类型")
	@ApiModelProperty(value="类型",example="类型")
	private String quan_blx;	//类型	
	@ApiParam(name="tab_name",value = "数据表")
	@ApiModelProperty(value="数据表",example="数据表")
	private String tab_name;	//数据表	
	@ApiParam(name="ying_rsl",value = "应入数量")
	@ApiModelProperty(value="应入数量",example="1")
	private Integer ying_rsl;	//应入数量	
	@ApiParam(name="shi_rsl",value = "实入数量")
	@ApiModelProperty(value="实入数量",example="1")
	private Integer shi_rsl;	//实入数量	
	@ApiParam(name="sun_hls",value = "损坏数量")
	@ApiModelProperty(value="损坏数量",example="1")
	private Integer sun_hls;	//损坏数量	
	@ApiParam(name="keyid",value = "keyid")
	@ApiModelProperty(value="keyid",example="1")
	private Long keyid;	//keyid
	private String type;	//keyid
}
