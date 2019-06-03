package com.example.fw.main.b;

import com.example.fw.base.BaseEN;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
@ApiModel
@Data
public class Chacuoleixing extends BaseEN {
	
	@ApiParam(name="id",value = "唯一标识")
	@ApiModelProperty(value="唯一标识",example="123456")
	private Long id;	//	
	@ApiParam(name="leix",value = "差错类型")
	@ApiModelProperty(value="差错类型",example="差错类型")
	private String leix;	//差错类型
}
