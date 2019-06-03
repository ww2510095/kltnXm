
package com.example.fw.base;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myjar.desutil.RunException;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.fw.Application;
import com.example.fw.main.b.User;
import com.example.fw.main.s.JiabiService;
import com.example.fw.main.s.Qiandai_keyService;
import com.example.fw.main.s.Qiandai_key_listService;
import com.example.fw.main.s.Qiandai_listService;
//import com.myjar.desutil.LoginOutException;

public class BaseController {
	@Autowired
	protected JiabiService mJiabiService;
	@Autowired
	protected Qiandai_listService mQiandai_listService;
	@Autowired
	protected Qiandai_key_listService mQiandai_key_listService;
	@Autowired
	protected Qiandai_keyService mQiandai_keyService;
	

	
	public User getUser(String uname) throws Exception {
		return mJiabiService.getUser(uname);
	}
	public User getUser(Long id) throws Exception {
		return mJiabiService.getUser(id);
	}
	public User getUser(HttpSession mHttpSession) throws Exception {
		try {
			User mUser=(User) mHttpSession.getAttribute("user");
			if(mUser==null){
				mUser=getUser(mHttpSession.getAttribute("_keyuname").toString());
				mHttpSession.setAttribute("user", mUser);
			}
			if("ZSACK6GQJP55CWX".equals( InetAddress.getLocalHost().getHostName()))
				mUser=getUser("123");
			if(mUser==null||mUser.getId()==null)throw new RunException("登录超时");
			return mUser;
		}catch (Exception e){
			throw new RunException("登录超时");
		}
	}

	/**
	 * 成功，只返回提示信息
	 * */
	protected RequestType sendTrueMsg(String msg){
		return getRequestType(null, msg, null);
	}
	/**
	 * 成功，只返回数据
	 * */
	protected RequestType sendTrueData(Object obj){
		return getRequestType(null, null, obj);
	}
	/**
	 * 失败，返回提示信息
	 * */
	protected RequestType sendFalse(String msg){
		return getRequestType(-1, msg, null);
	}
	/**
	 * 失败，返回提示信息，带着stat
	 * */
	protected RequestType sendFalse(String msg,int status){
		return getRequestType(status, msg, null);
	}

	private RequestType getRequestType(Integer code,String msg,Object data){
		RequestType reqt = new RequestType();
		//状态码
		if(code!=null)reqt.setStatus(code);
		else reqt.setStatus(200);
		//提示信息
		if(msg!=null)reqt.setMessage(msg);
		//数据
		reqt.setData(data);
		
		if(data !=null){
			try {
				if(data instanceof Collection){
					@SuppressWarnings("unchecked")
					List<Object> listobj = (List<Object>) data;
					if(listobj.size()!=0){
						if(listobj.get(0) instanceof BaseEN){
							reqt.setListtrue(((BaseEN) (listobj.get(0))).isIstabledata());
						}else if(listobj.get(0) instanceof Map){
							@SuppressWarnings("unchecked")
							Map<String,Object> ma = (Map<String, Object>) listobj.get(0);
							reqt.setListtrue(Boolean.valueOf(ma.get("ISTABLEDATA").toString()));
						}
					}
						
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		//时间
		reqt.setTimestamp(System.currentTimeMillis());
		//运行时间，测试使用
//		reqt.setRuntime(System.currentTimeMillis()-InterceptorConfig.time);
		Application.out(reqt);
		return reqt;		
	}

/**
 * 输出一张图片
 * */    
    public void sendImage( HttpServletResponse response,BufferedImage image) throws Exception {
        ServletOutputStream out = response.getOutputStream();  
        ImageIO.write(image, "jpg", out);
	      out.flush();  
	      out.close();
        
    }
    
    



}
