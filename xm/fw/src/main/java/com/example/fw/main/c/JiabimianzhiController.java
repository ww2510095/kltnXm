package com.example.fw.main.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseController;
import com.example.fw.base.RequestType;
import com.example.fw.main.b.Jiabimianzhi;
import com.example.fw.main.s.JiabimianzhiService;
import com.myjar.Stringutil;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/Jiabimianzhi")
@Api(tags = "假币面值")
public class JiabimianzhiController extends BaseController {
    @Autowired
    protected JiabimianzhiService mJiabimianzhiService;

    @RequestMapping(value ="/save", method = RequestMethod.POST)
    public RequestType save(Jiabimianzhi mJiabimianzhi) throws Exception {
        if(Stringutil.isBlank(mJiabimianzhi.getId())){
            if(Stringutil.isBlank(mJiabimianzhi.getMz()))return sendFalse("面值不可为空");
            if(mJiabimianzhiService.getByparameter("mz",mJiabimianzhi.getMz())!=null)return sendFalse("面值已存在");
            mJiabimianzhiService.add(mJiabimianzhi);
            return sendTrueMsg("添加成功");
        }else{
            mJiabimianzhiService.updateBySelect(mJiabimianzhi);
            return sendTrueMsg("更新成功");
        }

    }
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Jiabimianzhi mJiabimianzhi,Integer page,Integer rows) throws Exception {
        return sendTrueData(mJiabimianzhiService.getALL(mJiabimianzhi,page,rows));
    }

    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mJiabimianzhiService.getById(id));
    }

}
