package key;

import key.pz.Ocx;
import key.util.HttpRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import key.util.ReadTxt;

public class javaEx {

    public static final String BaseUrl="101.37.244.165:8001";


    public void systemInit(String uname){
        String su="http://"+getServiceIP()+"/initUname";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("_uname", uname);
        try {
            su=  HttpRequest.sendPost(su,params);
        }catch (Exception e){

        }
        JOptionPane.showMessageDialog( null, "账号到期时间:"+su, "提示", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * 打开拍照功能
     * */
    public void openKey(){
        System.out.println("准备启动ocx拍照程序");
        Ocx a=new Ocx();
        System.out.println("ocx拍照程序初始化成功");
        try {
            a.start();
//            new Thread(){
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                        a.Startsb();
//                    }catch (Exception e){}
//
//                }
//            }.start();

        } catch (Exception e) {
            System.out.println("ocx拍照程序初始化异常="+e.getMessage());
            e.printStackTrace();
        }

    }
   /**
    * 获取配置的服务器ip地址和端口号
    * */
    public String getServiceIP(){
        return BaseUrl;
    }
   /**
    * 获取配置的服务器ip地址和端口号
    * */
    public void lodurl(String url){
        JxBrowser.getBrowser().loadURL(url);
    }

    /**
     * 上传拍完的图片
     * */
    public String sendImage(){
       return  UploadImage("c://0.jpg",getServiceIP());
    }
    /**
     * 打开浏览器下载报表，参数：用户账号
     * */
    public void uploadFile(String s){
        try {
            String su="http://"+getServiceIP()+"/upload/file/" + s+ ".xls";

            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+su);
        }catch (Exception e){
            JOptionPane.showMessageDialog(
                    null, "程序需打开浏览器，但未发现浏览器", "错误", JOptionPane.ERROR_MESSAGE);
        }

    }



    //以下是内置函数
    public  String UploadImage(String file,String ip){
        String url = "http://"+ip+"/UpLoadFile";
        String fileName = file;
        Map<String, String> textMap = new HashMap<String, String>();
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("upfile", fileName);
        String contentType = "";//image/png
        return formUpload(url, textMap, fileMap,contentType);
    }
    public  String formUpload(String urlStr, Map<String, String> textMap,
                                    Map<String, String> fileMap,String contentType) {
        String res = "";
        HttpURLConnection conn = null;

        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                    System.out.println(inputName+","+inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();


                    contentType = new MimetypesFileTypeMap().getContentType(file);

                    if(!"".equals(contentType)){
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        }else if (filename.endsWith(".ico")) {
                            contentType = "image/image/x-icon";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    System.out.println(inputName+","+filename);

                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("posterr" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }


}
