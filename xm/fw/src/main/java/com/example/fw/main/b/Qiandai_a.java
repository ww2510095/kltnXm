package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Qiandai_a extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="1")
	private Long id;	//	
	@ApiParam(name="bei_z",value = "备注")
	@ApiModelProperty(value="备注",example="备注")
	private String bei_z;	//备注	
	@ApiParam(name="zhi_dr",value = "制单人")
	@ApiModelProperty(value="制单人",example="制单人")
	private String zhi_dr;	//制单人	
	@ApiParam(name="dan_jzt",value = "单据状态")
	@ApiModelProperty(value="单据状态",example="单据状态")
	private String dan_jzt;	//单据状态	
	@ApiParam(name="tab_name",value = "表数据")
	@ApiModelProperty(value="表数据",example="表数据")
	private String tab_name;	//表数据	
	@ApiParam(name="fa_hwd",value = "发货网点")
	@ApiModelProperty(value="发货网点",example="发货网点")
	private String fa_hwd;	//发货网点	
	@ApiParam(name="deng_j",value = "数据等级")
	@ApiModelProperty(value="数据等级",example="1")
	private Integer deng_j;	//数据等级	
	@ApiParam(name="shou_hwd",value = "收货网点")
	@ApiModelProperty(value="收货网点",example="收货网点")
	private String shou_hwd;	//收货网点
	
	public enum Qiandai_a_tab_name{
		qdcg,//钱袋采购
		yjd,//移交单
		lyd,//领用单
		jyd,//借用单
		ghd,//归还单

	}
}
