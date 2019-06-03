package com.example.fw.main.s;

import com.example.fw.Application;
import com.example.fw.base.RequestType;
import org.springframework.stereotype.Service;


@Service
public class RequestTypeService {

    public RequestType sendFalse(String msg){
        return getRequestType(-1, msg, null);
    }
    public RequestType sendTrue(Object data){
        return getRequestType(null, null, data);
    }

    private RequestType getRequestType(Integer code,String msg,Object data){
        RequestType reqt = new RequestType();
        //状态码
        if(code!=null)reqt.setStatus(code);
        else reqt.setStatus(200);
        //提示信息
        if(msg!=null)reqt.setMessage(msg);
        //数据
        reqt.setData(data);
        //时间
        reqt.setTimestamp(System.currentTimeMillis());
        //运行时间，测试使用
//		reqt.setRuntime(System.currentTimeMillis()-InterceptorConfig.time);
        Application.out(reqt);
        return reqt;
    }
}
