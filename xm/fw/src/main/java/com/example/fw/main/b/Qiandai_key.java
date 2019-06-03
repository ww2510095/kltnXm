package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Qiandai_key extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="bei_z",value = "备注")
	@ApiModelProperty(value="备注",example="备注")
	private String bei_z;	//备注	
	@ApiParam(name="ming_c",value = "钱袋名称")
	@ApiModelProperty(value="钱袋名称",example="钱袋名称")
	private String ming_c;	//钱袋名称	
	@ApiParam(name="shi_ylx",value = "使用类型")
	@ApiModelProperty(value="使用类型",example="使用类型")
	private String shi_ylx;	//使用类型	
	@ApiParam(name="quan_blx",value = "券别类型")
	@ApiModelProperty(value="券别类型",example="券别类型")
	private String quan_blx;	//券别类型	
	@ApiParam(name="qian_dzt",value = "钱袋状态")
	@ApiModelProperty(value="钱袋状态",example="钱袋状态")
	private String qian_dzt;	//钱袋状态	
	@ApiParam(name="chuang_jr",value = "创建人")
	@ApiModelProperty(value="创建人",example="创建人")
	private String chuang_jr;	//创建人	
	@ApiParam(name="xiu_gsj",value = "修改时间")
	@ApiModelProperty(value="修改时间",example="修改时间")
	private String xiu_gsj;	//修改时间	
	@ApiParam(name="xiu_gr",value = "修改人")
	@ApiModelProperty(value="修改人",example="修改人")
	private String xiu_gr;	//修改人
}
