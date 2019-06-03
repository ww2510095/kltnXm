package com.bm.base.util.wsdl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

 
/** 
* 发送soap格式的xml请求 
*  
*/ 
public class SendSoap {  
	

    public static Map<String, String> send_A(String url1, String SOAPAction, String soap) {  
        Document reqDoc = null;  
        Map<String, String> map = new HashMap<String, String>();
        try {  
            URL url = new URL(url1);  
            URLConnection conn = url.openConnection();  
            conn.setUseCaches(false);  
            conn.setDoInput(true);  
            conn.setDoOutput(true);  
            // conn.setRequestProperty("Content-Length",  
            // Integer.toString(soap.length()));  
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");  
            conn.setRequestProperty("SOAPAction", SOAPAction);  
            OutputStream os = conn.getOutputStream();  
            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");  
            osw.write(soap);  
            osw.flush();  
            osw.close();  
            InputStream is = conn.getInputStream();  
            reqDoc = openXmlDocument(is);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return map;  
              
        }  
        getNodes(reqDoc.getRootElement(), map);
        return map;  
    }  
//    public static void main(String[] args) {
//    	Document req=	send_A("http://apps.cdkltn.com:7090/ServiceMain.svc?wsdl", "http://tempuri.org/IServiceMain/CouponCreateEx", sb);
//    	Map<String, String> map = new HashMap<String, String>();
//    	getNodes(req.getRootElement(),map);
//    	System.out.println(map);
//    }
    public static  void getNodes(Element node,Map<String, String> map){
    	if(map==null)
    		map = new HashMap<String, String>();
    	map.put(node.getName(), node.getTextTrim());
		List<Element> listElement=node.elements();//所有一级子节点的list
		for(Element e:listElement){//遍历所有一级子节点
			getNodes(e,map);//递归
		}
    }
   
      
    /** 
     * 从InputStream中读取Document对象 
     *  
     * @param in 
     * @return 
     */ 
    public static Document openXmlDocument(InputStream in) {  
        Document resDoc = null;  
          
        SAXReader reader = new SAXReader();  
        try {  
            return reader.read(in);  
              
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
        return resDoc;  
    }  
} 