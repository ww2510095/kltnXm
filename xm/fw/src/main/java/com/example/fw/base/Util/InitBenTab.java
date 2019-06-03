package com.example.fw.base.Util;

import com.example.fw.base.Util.oracle.Init;

/**
 *可根据表的字段生成对于的javaBen
 */

public class InitBenTab {
    static String mainclasspath="com.example.fw";//外面的包名
    static String calsspath="D:\\xm\\idea\\fw\\src\\main\\java";//包名外面的物理路径
    static String tabname="gfakchd";//数据库表名
    static String tabnametl="高仿爱库存活动";//数据库表名的注释
    public static void main(String[] args)throws  Exception{
        Init mInit = new Init(mainclasspath,calsspath,tabname,tabnametl,true);
        mInit.setBm();
        mInit.SelectData();
    }


}
