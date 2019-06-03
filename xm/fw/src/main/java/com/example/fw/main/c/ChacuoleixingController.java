package com.example.fw.main.c;

import com.example.fw.base.BaseController;
import com.example.fw.base.RequestType;
import com.example.fw.main.b.Chacuoleixing;
import com.example.fw.main.s.ChacuoleixingService;
import com.myjar.Stringutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping("/Chacuoleixing")
@Api(tags = "差错类型")
public class ChacuoleixingController extends BaseController {
    @Autowired
    protected ChacuoleixingService mChacuoleixingService;

    @ApiOperation(value = "添加或修改差错类型，id不为空时修改，否则添加", response = RequestType.class)
    @RequestMapping(value ="/save", method = RequestMethod.POST)
    public RequestType save(Chacuoleixing mChacuoleixing) throws Exception {
        if(Stringutil.isBlank(mChacuoleixing.getId())){
            if(Stringutil.isBlank(mChacuoleixing.getLeix()))return sendFalse("类型不可为空");
            if(mChacuoleixingService.getByparameter("Leix",mChacuoleixing.getLeix())!=null)return sendFalse("类型已存在");
            mChacuoleixingService.add(mChacuoleixing);
            return sendTrueMsg("添加成功");
        }else{
            mChacuoleixingService.updateBySelect(mChacuoleixing);
            return sendTrueMsg("更新成功");
        }

    }
    @ApiOperation(value = "差错类型列表，支持所有参数联合解锁", response = Chacuoleixing.class)
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Chacuoleixing mChacuoleixing,Integer page,Integer rows) throws Exception {
        return sendTrueData(mChacuoleixingService.getALL(mChacuoleixing,page,rows));
    }

    @ApiOperation(value = "查询单个的差错类型id不可为空", response = Chacuoleixing.class)
    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mChacuoleixingService.getById(id));
    }

}
