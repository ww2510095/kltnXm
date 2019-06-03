package com.bm.orders.gfakchd;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;

@Service
public class GfakchdService extends BaseService {
    @Override
    protected String getTabName() {
        return "Gfakchd";
    }
}
