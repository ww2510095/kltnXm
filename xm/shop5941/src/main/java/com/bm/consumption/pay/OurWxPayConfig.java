package com.bm.consumption.pay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.bm.base.MyParameter;
import com.github.wxpay.sdk.WXPayConfig;

public class OurWxPayConfig implements WXPayConfig{
	 /** 加载证书  这里证书需要到微信商户平台进行下载*/
//	   private byte [] certData;
    public OurWxPayConfig() throws  Exception{
//        InputStream certStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cert/wxpay/apiclient_cert.p12");
//        this.certData = IOUtils.toByteArray(certStream);
//        certStream.close();
    }




 //AppSecret: 7cd330f13a11d0e2e5dbfe5583b97dcd
// 	String AppID="wx1152a02fb11a23a9";
// 	String hd="";
// 	String key="1107f92b4b7a9594bed569047e8e3700";
   @Override
   public String getAppID() {
//       return "wx1152a02fb11a23a9";
       return "wxbf8a9c74b0da22f8";
   }

   @Override
   public String getMchID() {
//       return "1510307381";
       return "1521241551";
   }

   @Override
   public String getKey() {
//       return "1107f92b4b7a9594bed569047e8e3700";
       return "4fd5e145a8999ca2cfc76631ab43d4c9";
   }

   @Override
   public InputStream getCertStream() {
	   InputStream mInputStream=null;
	try {
		mInputStream = new FileInputStream(new File(MyParameter.WEI_PRCK));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
       return mInputStream;
   }

   @Override
   public int getHttpConnectTimeoutMs() {
       return 0;
   }

   @Override
   public int getHttpReadTimeoutMs() {
       return 0;
   }


}
