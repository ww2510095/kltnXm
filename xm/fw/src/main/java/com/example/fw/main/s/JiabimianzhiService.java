package com.example.fw.main.s;

import com.example.fw.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class JiabimianzhiService extends BaseService {
    @Override
    protected String getTabName() {
        return "Jiabimianzhi";
    }
}
