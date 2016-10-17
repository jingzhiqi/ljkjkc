package com.jsb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "core")
public class CoreControl {
	@RequestMapping(value = "{url}", method = RequestMethod.GET)
	public String redirect(@PathVariable String url) {
		//String url = "404";
		url=url.replaceAll("&", "/");
		System.out.println(url);
		return "view/" + url;
	}
}
