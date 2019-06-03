package com.bm.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.Shop5941Application;
import com.myjar.Stringutil;

public class MyDate {
	
	   /* 
     * 将时间转换为时间戳
     */    
    public static Long dateToStamp(String s) throws Exception{
//        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
//        res = String.valueOf(ts);
        return ts;
    }
    public static void main(String[] args) throws Exception {
        Shop5941Application.out(dateToStamp("2018-11-01  00:00:00"));//1536595200000
        Shop5941Application.out(dateToStamp("2018-11-30  00:00:00"));//1536595200000
        Shop5941Application.out(stampToDate(1544095689227L));//1536595200000
	}
    
    /* 
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
    /* 
     * 今日凌晨时间戳
     */
    public static Long Dtae_star() throws Exception{
    	String strA=stampToDate(System.currentTimeMillis());//2019-01-02 15:16:10
    	strA=strA.substring(0,11);
    	return MyDate.dateToStamp(strA+"00:00:00");
    }
    /* 
     * 今日结束时间戳
     */
    public static Long Dtae_end() throws Exception{
    	return Dtae_star()+(86400000L-1L);
    }
    public static String orcaleCDATE(String orgs1,String orgs2) {
		if(Stringutil.isBlank(orgs2))
			return " TO_CHAR("+orgs1+" / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS CDATE ";
		else
			return " TO_CHAR("+orgs1+" / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS  "+orgs2+" ";
	}
    public static String orcaleCDATE2(String orgs1,String orgs2) {
    	if(Stringutil.isBlank(orgs2))
    		return " TO_CHAR("+orgs1+" / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01', 'YYYY-MM-DD'), 'YYYY-MM-DD') AS CDATE ";
    	else
    		return " TO_CHAR("+orgs1+" / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01', 'YYYY-MM-DD'), 'YYYY-MM-DD') AS  "+orgs2+" ";
    }
    public static String orcaleCDATE(String orgs1) {
		return orcaleCDATE(orgs1, null);
    }


}
