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
import com.jsb.model.cus.Customer;
import com.jsb.service.BaseInfoService;
import com.jsb.service.bUser.CustomerService;

@Controller
@RequestMapping("au/customer")
public class CustomerController extends BaseEntityController<Customer>{
	
	public CustomerController(){
		super(Customer.class);
	}
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	BaseInfoService baseInfoService;
	
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@ResponseBody
	public List<Customer> findAllCustomer() {
		return customerService.findAllCustomers();
	}
	
	@RequestMapping(value = "isExistsx", method = RequestMethod.GET)
	@ResponseBody
	public int isExistsx(String id,String tel) {
		id = Util.convertString(id);
		tel = Util.convertString(tel);
		return customerService.isCustomerExitsTel(tel, id);
	}
	
	@RequestMapping(value = "saveCustomer", method = RequestMethod.POST)
	@ResponseBody
	public Customer saveCustomer(Customer customer) {
		return customerService.saveCustomer(customer);
	}
	
	@RequestMapping(value = "getCustomerByName", method = RequestMethod.GET)
	@ResponseBody
	public PageResult getCustomerByName(String name,String page,String rows) {
		name = Util.convertString(name);
		int pageI = Util.convertPage(page);
		int rowsI = Util.convertRow(rows);
		return customerService.getCustomerByName(name,pageI,rowsI);
	}
	
	@RequestMapping(value = "check", method = RequestMethod.GET)
	@ResponseBody
	public  boolean check(String cid,String id ,String account) {
		return !customerService.isAccountExists( id,account);
	}
	
	
	@RequestMapping(value = "del", method = RequestMethod.POST)
	@ResponseBody
	public  String del(String id) {
		return customerService.del(id);
	}
	
	@RequestMapping(value = "updatePasswd", method = RequestMethod.POST,produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public  boolean updatePasswd(String oldpw,String newpw,String conformpw) {
		try {
			Subject sub = SecurityUtils.getSubject();
			String uid = sub.getSession().getAttribute("uid").toString();
			Customer customer = this.customerService.findCustomerByID(uid);
			if(customer!=null){
				if(customer.getPassword().equals(oldpw)){
					this.customerService.updateCustomerPassword(uid,newpw);
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
		ModelAndView mv= new ModelAndView("view/customerAdd");
		Customer customer = null;
		if(id==null||id.toLowerCase().equals("null")||id.equals("")){
			mv.addObject("flag", "添加客户");
		}else{
			mv.addObject("flag", "修改客户");
			customer = baseInfoService.findOne(Customer.class, id);
		}
		if(customer!=null){
			mv.addObject("uid", customer.getId());
			mv.addObject("uname", customer.getName());
			mv.addObject("utel", customer.getTel());
			mv.addObject("upassword", customer.getPassword());
			mv.addObject("uphone", customer.getPhone());
			mv.addObject("uqq", customer.getQq());
			mv.addObject("usname", customer.getSname());
			mv.addObject("usaddress", customer.getSaddress());
			mv.addObject("ms", customer.getMs());
		}else{
			mv.addObject("uid", "");
			mv.addObject("uname", "");
			mv.addObject("utel", "");
			mv.addObject("upassword", "");
			mv.addObject("uphone","");
			mv.addObject("uqq", "");
			mv.addObject("usname", "");
			mv.addObject("usaddress", "");
			mv.addObject("ms", "");
		}
		return mv;
	}
}
