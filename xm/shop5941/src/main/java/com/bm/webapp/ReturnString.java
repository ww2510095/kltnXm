package com.bm.webapp;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;

@RestController
@Api(tags = "版本下载")
public class ReturnString extends BaseController{
	
	/**
	 * 下载
	 * @throws Exception 
	 * */
	@RequestMapping(value ="/download/downloadApp/url", method = {RequestMethod.POST,RequestMethod.GET}) 
	public String downloadApp(HttpServletRequest request) throws Exception {
		if(isIOSDevice(request))
			return "https://itunes.apple.com/cn/app/id1424059090?mt=8";
		else{
			Sql msql = new Sql();
			msql.setSql("select url from Version order by id desc ");
			String str=	mMemberService.exeSelectSql(msql).get(0).get("URL").toString();
			return str;
		}
		
		
	}

	   public static boolean isIOSDevice(HttpServletRequest request) {
	        boolean isMobile = false;
	        final String[] ios_sys = { "iPhone", "iPad", "iPod" };
	        String userAgent = request.getHeader("user-agent");
	        for (int i = 0; !isMobile && userAgent != null && !userAgent.trim().equals("") && i < ios_sys.length; i++) {
	            if (userAgent.contains(ios_sys[i])) {
	                isMobile = true;
	                break;
	            }
	        }
	        return isMobile;//
	    }

}
