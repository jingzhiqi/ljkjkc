package com.jsb.controller.bUser;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jsb.Util;
import com.jsb.controller.BaseEntityController;
import com.jsb.model.PageResult;
import com.jsb.model.basic.Department;
import com.jsb.model.basic.User;
import com.jsb.service.BaseInfoService;
import com.jsb.service.bUser.DepartmentService;
import com.jsb.service.bUser.UserService;

@Controller
@RequestMapping("au/user")
public class UserController extends BaseEntityController<User>{
	
	public UserController(){
		super(User.class);
	}
	
	@Autowired
	private UserService userService;

	@Autowired
	BaseInfoService baseInfoService;
	
	@Autowired
	DepartmentService departmentService;
	
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@ResponseBody
	public List<User> findAllUsers() {
		return userService.findAllUsers();
	}
	
	@RequestMapping(value = "saveUser", method = RequestMethod.POST)
	@ResponseBody
	public User saveUser(User user) {
		return userService.saveUser(user);
	}
	
	@RequestMapping(value = "getUserByNameNoPage", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getUserByNameNoPage(String name) {
		if(name==null||name.toLowerCase().equals("null")){
			name = "";
		}else{
			name = name.trim();
		}
		return userService.getUserByName(name);
	}
	
	@RequestMapping(value = "getUserByName", method = RequestMethod.GET)
	@ResponseBody
	public PageResult getUserByName(String name,String page,String rows) {
		name = Util.convertString(name);
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
		return userService.getUserByName(name,pageI,rowsI);
	}
	
	@RequestMapping(value = "check", method = RequestMethod.GET)
	@ResponseBody
	public  boolean check(String cid,String id ,String account) {
		return !userService.isAccountExists( id,account);
	}
	
	
	@RequestMapping(value = "del", method = RequestMethod.POST)
	@ResponseBody
	public  String del(String id) {
		return userService.del(id);
	}
	
	@RequestMapping(value = "updatePasswd", method = RequestMethod.POST,produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public  boolean updatePasswd(String oldpw,String newpw,String conformpw) {
		try {
			Subject sub = SecurityUtils.getSubject();
			String uid = sub.getSession().getAttribute("uid").toString();
			User user = this.userService.findUserByID(uid);
			if(user!=null){
				if(user.getPassword().equals(oldpw)){
					this.userService.updateUserPassword(uid,newpw);
					return true;
				}
				else{
					return false;
				}
			}else{
				return false;
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	@RequestMapping(value = "updateOrAddDep", method = RequestMethod.GET)
	public ModelAndView updateOrAddDep(String id) {
		ModelAndView mv= new ModelAndView("view/userAdd");
		User user = null;
		if(id==null||id.toLowerCase().equals("null")||id.equals("")){
			mv.addObject("flag", "添加人员");
		}else{
			mv.addObject("flag", "修改人员");
			user = baseInfoService.findOne(User.class, id);
		}
		if(user!=null){
			mv.addObject("uid", user.getId());
			mv.addObject("uname", user.getName());
			mv.addObject("userial", user.getSerial());
			mv.addObject("utel", user.getTel());
			mv.addObject("upassword", user.getPassword());
			mv.addObject("urole", user.getRole());
			mv.addObject("udname", user.getDepartment().getName());
		}else{
			mv.addObject("uid", "");
			mv.addObject("uname", "");
			mv.addObject("userial", "");
			mv.addObject("utel", "");
			mv.addObject("upassword", "");
			mv.addObject("urole", "3");
			mv.addObject("udname", "");
		}
		List<Department> listDep = departmentService.getDepByName("");
		mv.addObject("deps", listDep);
		return mv;
	}
}
