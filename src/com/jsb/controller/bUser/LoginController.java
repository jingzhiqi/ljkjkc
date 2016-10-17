package com.jsb.controller.bUser;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jsb.Util;
import com.jsb.model.PageResult;
import com.jsb.model.basic.User;
import com.jsb.model.log.LogEntity;
import com.jsb.secure.LoginToken;
import com.jsb.service.bUser.UserService;
import com.jsb.service.log.LogService;


/**
 * 
 * 
 *
 *@author king 
 *
 *@since 2015年6月10日  上午11:04:30
 *
 */
@Controller
@RequestMapping("login")
public class LoginController{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	LogService logService;
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ModelAndView login(String userName,String passWord,String chkNumber,HttpServletRequest req,HttpServletResponse resp){
		LoginToken token=new LoginToken(userName, passWord.toCharArray(),chkNumber);
		token.setRememberMe(true);
		Subject sub=SecurityUtils.getSubject();
		try{
			ModelAndView mav = new ModelAndView("redirect:/view/index.jsp");
			sub.login(token);
			User user = userService.getUserByUName(userName);
			sub.getSession().setAttribute("uid", user.getId());
			sub.getSession().setAttribute("uname", user.getName());
			sub.getSession().setAttribute("tel", user.getTel());
			
			String roleName = "";
			if(user.getRole()!=null){
				if(user.getRole().equals("1")){
					roleName = "管理员";
				}else if(user.getRole().equals("2")){
					roleName = "库管";
				}else if(user.getRole().equals("3")){
					roleName = "业务";
				}
			}else{
				roleName = "未知";
			}
			sub.getSession().setAttribute("roleName", roleName);
			sub.getSession().setAttribute("role", user.getRole());
			sub.getSession().setAttribute("user", user);
			sub.getSession().setAttribute("logintime", Util.nowTime());
			sub.getSession().setAttribute("dname", user.getDepartment().getName());

			logService.saveLog("登陆成功");
			return mav;
		}catch(Exception e){
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("view/login");
			mav.addObject("error",e.getMessage());
			mav.addObject("userName",userName);
			mav.addObject("passWord",passWord);
			mav.addObject("chkNumber",chkNumber);
			logService.saveLog("登陆失败:"+e.getMessage());
			return mav;
		}
	}
	/**
	 * 点击顶部导航时，记录当前导航的
	 * 
	 *@param id
	 *@return
	 *
	 *@author king 
	 *
	 *@since 2015年6月10日  下午5:20:35
	 *
	 */
	@RequestMapping(value = "setupTM", method = RequestMethod.POST)
	@ResponseBody
	public boolean setupTM(String id){
		try{
			Subject sub=SecurityUtils.getSubject();
			sub.getSession().setAttribute("tmid", id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 点击右侧导航时，记录当前点击的导航内容
	 * 
	 *@param id
	 *@return
	 *
	 *@author king 
	 *
	 *@since 2015年6月10日  下午5:21:04
	 *
	 */
	@RequestMapping(value = "setupLM", method = RequestMethod.POST)
	@ResponseBody
	public boolean setupLM(String id){
		try{
			Subject sub=SecurityUtils.getSubject();
			sub.getSession().setAttribute("lmid", id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(String name){
		Subject sub=SecurityUtils.getSubject();
		sub.getSession().removeAttribute("uid");
		sub.getSession().removeAttribute("uname");
		sub.getSession().removeAttribute("jh");
		sub.getSession().removeAttribute("roleName");
		sub.getSession().removeAttribute("role");
		sub.getSession().removeAttribute("dname");
		sub.getSession().removeAttribute("logintime");
		return "redirect:/view/login.jsp";
	}
	
	@RequestMapping(value = "getLogNoPage", method = RequestMethod.GET)
	@ResponseBody
	public List<LogEntity> getLogNoPage(String name){
		return logService.getAllLog(name);
	}
	@RequestMapping(value = "getLog", method = RequestMethod.GET)
	@ResponseBody
	public PageResult getAllLog(String name,String page,String rows){
		if(name==null||name.toLowerCase().equals("null")){
			name = "";
		}else{
			name = name.trim();
		}
		int pageI = 1;
		try{
			pageI = Integer.parseInt(page);
		}catch(Exception e){
		}
		int rowsI = 20;
		try{
			rowsI = Integer.parseInt(rows);
		}catch(Exception e){
		}
		return logService.getAllLog(name,pageI,rowsI);
	}
	
	@RequestMapping(value = "deleteLog", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteLog(String ids){
		return logService.deleteLog(ids);
	}
	
	@RequestMapping(value = "deleteAllLog", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteAllLog(){
		return logService.deleteAllLog();
	}
}
