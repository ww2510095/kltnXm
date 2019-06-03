package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Qiandai_list extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="zhuang_t",value = "当前状态")
	@ApiModelProperty(value="当前状态",example="当前状态")
	private String zhuang_t;	//当前状态	
	@ApiParam(name="xiu_grzh",value = "修改人账号")
	@ApiModelProperty(value="修改人账号",example="修改人账号")
	private String xiu_grzh;	//修改人账号	
	@ApiParam(name="xiu_grmz",value = "修改人名字")
	@ApiModelProperty(value="修改人名字",example="修改人名字")
	private String xiu_grmz;	//修改人名字	
	@ApiParam(name="tab_name",value = "数据表名")
	@ApiModelProperty(value="数据表名",example="数据表名")
	private String tab_name;	//数据表名
}
