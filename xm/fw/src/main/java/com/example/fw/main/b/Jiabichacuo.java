package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Jiabichacuo extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="123456")
	private Long id;	//id	
	@ApiParam(name="wang_d",value = "单位")
	@ApiModelProperty(value="单位",example="单位")
	private String wang_d;	//单位	
	@ApiParam(name="juan_blx",value = "卷别列席")
	@ApiModelProperty(value="卷别列席",example="卷别列席")
	private String juan_blx;	//卷别列席	
	@ApiParam(name="mian_z",value = "面值")
	@ApiModelProperty(value="面值",example="面值")
	private String mian_z;	//面值	
	@ApiParam(name="cha_clx",value = "差错类型")
	@ApiModelProperty(value="差错类型",example="差错类型")
	private String cha_clx;	//差错类型	
	@ApiParam(name="guan_zhxx",value = "冠字号信息")
	@ApiModelProperty(value="冠字号信息",example="冠字号信息")
	private String guan_zhxx;	//冠字号信息	
	@ApiParam(name="image",value = "图片")
	@ApiModelProperty(value="图片",example="图片")
	private String image;	//图片	
	@ApiParam(name="shen_hr",value = "审核人")
	@ApiModelProperty(value="审核人",example="审核人")
	private String shen_hr;	//审核人	
	@ApiParam(name="shi_fty",value = "是否同意")
	@ApiModelProperty(value="是否同意",example="是否同意")
	private String shi_fty;	//是否同意	
	@ApiParam(name="shen_hyj",value = "审核意见")
	@ApiModelProperty(value="审核意见",example="审核意见")
	private String shen_hyj;	//审核意见	
	@ApiParam(name="shen_htp",value = "审核图片")
	@ApiModelProperty(value="审核图片",example="审核图片")
	private String shen_htp;	//审核图片	
	@ApiParam(name="ti_jr",value = "提交人")
	@ApiModelProperty(value="提交人",example="提交人")
	private String ti_jr;	//提交人	
	@ApiParam(name="shi_fck",value = "是否查看")
	@ApiModelProperty(value="是否查看",example="1")
	private Integer shi_fck;	//是否查看	
	@ApiParam(name="zhi_ddw",value = "制单单位")
	@ApiModelProperty(value="制单单位",example="制单单位")
	private String zhi_ddw;	//制单单位

	@ApiParam(name="fxrq",value = "发现日期")
	@ApiModelProperty(value="发现日期",example="发现日期")
	private Long fxrq;	//发现日期
	@ApiParam(name="qfrq",value = "封签日期")
	@ApiModelProperty(value="封签日期",example="封签日期")
	private Long qfrq;	//封签日期
	@ApiParam(name="jblx",value = "假币类型\n")
	@ApiModelProperty(value="假币类型\n",example="假币类型\n")
	private String jblx;	//假币类型
	@ApiParam(name="bzblx",value = "变造币类型\n")
	@ApiModelProperty(value="变造币类型1\n",example="变造币类型1\n")
	private String bzblx;	//变造币类型

}
