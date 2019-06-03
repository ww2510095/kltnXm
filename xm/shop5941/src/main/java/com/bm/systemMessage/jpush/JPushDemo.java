
//package com.bm.systemMessage.jpush;
//
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import cn.jpush.api.JPushClient;
//import cn.jpush.api.push.PushResult;
//import cn.jpush.api.push.model.Message;
//import cn.jpush.api.push.model.Platform;
//import cn.jpush.api.push.model.PushPayload;
//import cn.jpush.api.push.model.PushPayload.Builder;
//import cn.jpush.api.push.model.audience.Audience;
//import cn.jpush.api.push.model.audience.AudienceTarget;
//import cn.jpush.api.push.model.notification.Notification;  
//  
//public class JPushDemo {  
//    protected static final Logger LOG = LoggerFactory.getLogger(JPushDemo.class);  
//  
//    public static final String TITLE = "hello Jpush";  //通知标题  如果指定了，则通知里原来展示 App名称的地方，将展示成这个字段。
//    public static final String ALERT = "欢迎使用Jpush";  //通知内容  必须指定
//    public static final String MSG_CONTENT = "";  //自定义消息内容
//    public static final String REGISTRATION_ID = "0900e8d85ef";  //注册id
//    public static final String TAG = "";  // 推送目标
//      
//    public  static JPushClient jpushClient=null;  
//      
//    @Test
//    public void testJpush() {
//    	JPushDemo.testSendPush("44262636e2afd75d9b9f7932", "ae5c0ab5f093b2aba1f8ce25");
//    }
//    
//    
//    @SuppressWarnings("deprecation")
//	public static void testSendPush(String appKey ,String masterSecret) {  
//          
//        jpushClient = new JPushClient(masterSecret, appKey, 3);  
//      
//         //生成推送的内容，这里我们先测试全部推送  
//        PushPayload payload=sendMessage(3,"","");  
//           
//        try {  
//            Application.out(payload.toString());  
//            PushResult result = jpushClient.sendPush(payload);  
//            Application.out(result+"................................");  
//            Application.out(result.getResponseCode());
//              
//            LOG.info("Got result - " + result);  
//              
//        } catch (Exception e) {  
//            LOG.error("Connection error. Should retry later. ", e);  
//              
//        } 
//    }  
//      
//
//    /**
//     * @param t ,1:android,2:ios,3全部
//     * */
//    public static PushPayload sendMessage(int t,String message,String...strings ) {  
//    	Builder mPushPayload = PushPayload.newBuilder();
//    	mPushPayload.setNotification(Notification.android(ALERT, TITLE, null))  
//    				.setAudience(Audience.newBuilder()  
////              	.addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))  //设置推送目标 满足tag1，tag2 其一即可
//    				.addAudienceTarget(AudienceTarget.alias(strings))  //设置推送给多个别名
//    				.build())
//    				.setMessage(Message.newBuilder()  
//                    .setMsgContent(message)  //设置自定义消息内容
//                    .addExtra("from", "JPush")  //设置扩展字段。
//                    .build());
//    	switch (t) {
//		case 1:
//			mPushPayload.setPlatform(Platform.android());
//			break;
//		case 2:
//			mPushPayload.setPlatform(Platform.ios());
//			break;
//
//		default:
//			mPushPayload.setPlatform(Platform.all());
//			break;
//			
//		}
//    	    	return mPushPayload.build();  
//    }  
//}  