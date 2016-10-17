package com.jsb.controller.baseinfo;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author jing
 *
 * @since 2014年12月26日
 */
@Controller
public class JSBCoreController {

	@RequestMapping(value = "core/{regex1:[a-z]+\\[a-z]+}", method = RequestMethod.GET)
	String unsafe(@PathVariable String regex1){
		System.out.println(regex1);
		return regex1;
	}
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	String index(){
		return "index.html";
	}
	
	@RequestMapping(value = "workspace", method = RequestMethod.GET)
	String report(){
		return "workspace";
	}
	
	@RequestMapping(value = "login", method = RequestMethod.GET)
	String main(HttpServletRequest req){
		StringBuffer url=req.getRequestURL();
		System.out.println("------->>>>>>>>>"+url);
		return "login/login";
	}
	
}
