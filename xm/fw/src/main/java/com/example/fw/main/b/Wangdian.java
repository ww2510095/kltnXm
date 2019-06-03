package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Wangdian extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="wang_ddz",value = "网点地址")
	@ApiModelProperty(value="网点地址",example="网点地址")
	private String wang_ddz;	//网点地址	
	@ApiParam(name="wai_b",value = "外包账号")
	@ApiModelProperty(value="外包账号",example="外包账号")
	private String wai_b;	//外包账号	
	@ApiParam(name="shang_j",value = "上级网点")
	@ApiModelProperty(value="上级网点",example="上级网点")
	private String shang_j;	//上级网点	
	@ApiParam(name="deng_j",value = "等级,1,2,3,4")
	@ApiModelProperty(value="等级,1,2,3,4",example="1")
	private Integer deng_j;	//等级,1,2,3,4	
	@ApiParam(name="suo_sqy",value = "所属区域")
	@ApiModelProperty(value="所属区域",example="所属区域")
	private String suo_sqy;	//所属区域	
	@ApiParam(name="a1",value = "1级")
	@ApiModelProperty(value="1级",example="1级")
	private String a1;	//1级	
	@ApiParam(name="a2",value = "2级")
	@ApiModelProperty(value="2级",example="2级")
	private String a2;	//2级	
	@ApiParam(name="a3",value = "3级")
	@ApiModelProperty(value="3级",example="3级")
	private String a3;	//3级

	@ApiParam(name="jrjg",value = "金融机构")
	@ApiModelProperty(value="金融机构",example="金融机构")
	private String jrjg;	//金融机构

	@ApiParam(name="sfwwbf",value = "是否为外包方")
	@ApiModelProperty(value="是否为外包方",example="是否为外包方")
	private String sfwwbf;	//金融机构



}
