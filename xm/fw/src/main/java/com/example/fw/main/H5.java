package com.example.fw.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class H5 {
	
	/**
	 * 通过扫码跳转H5注册页面
	 * @throws Exception 
	 * */
	@RequestMapping(value ="/gpy/open", method = {RequestMethod.POST,RequestMethod.GET}) 
	public String open() throws Exception {
		return "a";

	}

}
