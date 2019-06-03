package com.example.fw.main.s;

import com.example.fw.base.BaseService;
import com.example.fw.main.b.Wangdian;
import org.springframework.stereotype.Service;

@Service
public class WangdianService extends BaseService {
    @Override
    protected String getTabName() {
        return "Wangdian";
    }

}
