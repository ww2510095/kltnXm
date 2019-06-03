package com.example.fw.main.s;

import com.example.fw.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class JuseService extends BaseService {
    @Override
    protected String getTabName() {
        return "juese";
    }
}
