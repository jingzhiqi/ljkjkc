package com.jsb.controller.bUser;

import java.util.List;

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
import com.jsb.service.BaseInfoService;
import com.jsb.service.bUser.DepartmentService;

@Controller
@RequestMapping("au/dep")
public class DepartmentControl extends BaseEntityController<Department>{
	@Autowired
	private DepartmentService departmentService;
	

	@Autowired
	BaseInfoService baseInfoService;
	
	
	public DepartmentControl(){
		super(Department.class);
	}
	
	@RequestMapping(value = "getDepByNameNoPage", method = RequestMethod.GET)
	@ResponseBody
	public List<Department> getDepByNameNoPage(String name) {
		if(name==null||name.toLowerCase().equals("null")){
			name = "";
		}else{
			name = name.trim();
		}
		return departmentService.getDepByName(name);
	}
	@RequestMapping(value = "getDepByName", method = RequestMethod.GET)
	@ResponseBody
	public PageResult getDepByName(String name,String page,String rows) {
		name = Util.convertString(name);
		int pageI = Util.convertPage(page);
		
		int rowsI = Util.convertRow(rows);
		return departmentService.getDepByName(name,pageI,rowsI);
	}
	
	@RequestMapping(value = "delDep", method = RequestMethod.POST,produces="application/text;charset=UTF-8")
	@ResponseBody
	public  String del(String id) {
		return departmentService.del(id);
	}
	
	@RequestMapping(value = "updateOrAddDep", method = RequestMethod.GET)
	public ModelAndView updateOrAddDep(String id) {
		ModelAndView mv= new ModelAndView("view/depAdd");
		if(id==null||id.toLowerCase().equals("null")||id.equals("")){
			mv.addObject("flag", "添加部门");
		}else{
			mv.addObject("flag", "修改部门");
			Department dep = baseInfoService.findOne(Department.class, id);
			if(dep!=null){
				mv.addObject("id", dep.getId());
				mv.addObject("name", dep.getName());
				mv.addObject("serial", dep.getSerial());
			}else{
				mv.addObject("id", "");
				mv.addObject("name", "");
				mv.addObject("serial", "");
			}
		}
		return mv;
	}
}
