package update;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;


public class HttpRequest {




	 public static String sendPost(String urlParam, Map<String, Object> params,Integer cs) throws  Exception{
		return sendPost(urlParam, params, "UTF-8",cs);  
	 }
	 public static String sendPost(String urlParam, Map<String, Object> params) throws  Exception{
		 return sendPost(urlParam, params, "UTF-8",null);  
	 }
	 public static String sendPost(String urlParam, Map<String, Object> params, String charset,Integer cs) throws  Exception {

	        StringBuffer resultBuffer = null;  
	        // 构建请求参数  
	        StringBuffer sbParams = new StringBuffer();  
	        if (params != null && params.size() > 0) {  
	            for (Entry<String, Object> e : params.entrySet()) {  
	                sbParams.append(e.getKey());  
	                sbParams.append("=");  
	                sbParams.append(e.getValue());  
	                sbParams.append("&");  
	            }  
	        }  
	        URLConnection con ;
	        OutputStreamWriter osw =null;
	        BufferedReader br =null;
	        try {  
	            URL realUrl = new URL(urlParam);  
	            // 打开和URL之间的连接  
	            con = realUrl.openConnection();  
	           
	            // 设置通用的请求属性  
	            con.setRequestProperty("accept", "*/*");  
	            con.setRequestProperty("connection", "Keep-Alive");  
//	            con.setRequestProperty("Content-Type", "application/json");  
	            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
	            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
	            	
//	            con.addRequestProperty("Authorization", "Basic ApiBoot:ApiBootSecret");
	            
	            // 发送POST请求必须设置如下两行  
	            con.setDoOutput(true);  
	            con.setDoInput(true);  
	            if(cs!=null)
	            con.setReadTimeout(cs);
	            // 获取URLConnection对象对应的输出流  
	            osw = new OutputStreamWriter(con.getOutputStream(), charset);  
	            if (sbParams != null && sbParams.length() > 0) {  
	                // 发送请求参数  
	                osw.write(sbParams.substring(0, sbParams.length() - 1));  
	                // flush输出流的缓冲  
	                osw.flush();  
	            }  
	            // 定义BufferedReader输入流来读取URL的响应  
	            resultBuffer = new StringBuffer();  
	            
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));  
                String temp;  
                while ((temp = br.readLine()) != null) {  
                    resultBuffer.append(temp);  
                }  
            
	        }  finally {
	        	try {
					osw.close();
					br.close();
					osw = null;
					br = null;
				}catch (Exception e){

				}

	        }
	        return resultBuffer.toString();  
	    }  
}
