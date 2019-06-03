package com.example.fw.base;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

//import com.myjar.desutil.AuthException;
//import com.myjar.desutil.LoginOutException;
import com.myjar.desutil.RunException;

@ControllerAdvice
public class ExceptionHandle{
	private final Logger mLogger= LoggerFactory.getLogger(getClass());
		/**
		 * 异常拦截
		 * */
	  @ExceptionHandler(value = Exception.class)
      @ResponseBody
      public RequestType exceptionGet(Exception ex){
			RequestType rt = new RequestType();
			rt.setData(System.currentTimeMillis());
//			rt.setRuntime(System.currentTimeMillis()-InterceptorConfig.time);//测试时间
			if(ex != null){
				if (ex instanceof RunException) {
					rt.setMessage(ex.getMessage());
					rt.setStatus(-2);//数据不合法
//				}else if (ex instanceof LoginOutException) {
//					rt.setMessage(ex.getMessage());
//					rt.setStatus(-9);//登录超时
//				}
//				else if (ex instanceof AuthException) {
//					rt.setMessage(ex.getMessage());
//					rt.setStatus(-3);//权限不足
				}else if (ex instanceof ClientAbortException) {
					rt.setMessage("服务器繁忙0x001");
					rt.setStatus(-4);
				}else if(ex instanceof BindException||ex instanceof MethodArgumentTypeMismatchException||ex instanceof DataIntegrityViolationException){
					rt.setMessage("服务器繁忙0x002");
					rt.setStatus(-2);//数据不合法
				}else if(ex instanceof BadSqlGrammarException||ex instanceof UncategorizedSQLException){
					rt.setMessage("服务器繁忙0x003");
					rt.setStatus(-2);//数据不合法
				}else if(ex instanceof IndexOutOfBoundsException){
					rt.setMessage("服务器繁忙0x004");
					rt.setStatus(200);
				}else if (ex instanceof HttpRequestMethodNotSupportedException||ex instanceof ClassCastException){
					rt.setMessage("服务器繁忙0x005");
					rt.setStatus(-5);
				}else if(ex instanceof MultipartException){
					rt.setMessage("服务器繁忙0x006");
					rt.setStatus(-6);
				}else if(ex instanceof NullPointerException){
					rt.setMessage("您要查找的数据不存在");
					rt.setStatus(500);
				}else{
					rt.setMessage("服务器繁忙0x007");
					rt.setStatus(500);
				}
				
				//日志功能
				mLogger.error(ex.getMessage());
			}
			ex.printStackTrace();
			return rt;
		
      }
  }  
  