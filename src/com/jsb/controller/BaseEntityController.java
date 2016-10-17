package com.jsb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsb.service.BaseInfoService;
import com.jsb.service.util.UtilService;
import com.jsb.util.SortType;


/**
 * 基本实体外部接口
 * 
 * @author rambo
 * 
 * @since Dec 22, 2014
 */
public abstract class BaseEntityController<E> {
	
	private Class<E> clazz;
	@Autowired
	private UtilService utilService;
	public BaseEntityController(Class<E> clazz) {
		this.clazz = clazz;
	}

	public BaseEntityController() {
	}

	@Autowired
	private BaseInfoService baseService;

	public BaseInfoService getBaseService() {
		return baseService;
	}

	public void setBaseService(BaseInfoService baseService) {
		this.baseService = baseService;
	}

	@RequestMapping(value="save",  method = RequestMethod.POST)
	@ResponseBody
	public E save(E entity) {
		try {
			E result = baseService.save(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	@RequestMapping(value="delete",  method = RequestMethod.POST)
	@ResponseBody
	public String delete(String id) {
		try {
			baseService.deleteOne(clazz, id);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	@RequestMapping(value="deleteMore",  method = RequestMethod.POST)
	@ResponseBody
	public String deleteMore(String ids) {
		try {
			baseService.deleteMore(clazz, ids);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	@RequestMapping(value="update",  method = RequestMethod.POST)
	@ResponseBody
	public E update(E entity) {
		try {
			baseService.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	@RequestMapping(value="findByID",  method = RequestMethod.GET)
	@ResponseBody
	public E find(String id) {
		try {
			E result = baseService.findOne(clazz,id );
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@RequestMapping(value="findAll",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> find() {
		try {
			List<E> result = baseService.findAll(clazz);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="findWithACS/{fieldName}",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> findWithACS(@PathVariable("fieldName") String fieldName) {
		try {
			List<E> result = baseService.findWithSort(clazz, SortType.ASC, fieldName);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="findWithDESC/{fieldName}",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> findWithDESC(@PathVariable("fieldName") String fieldName) {
		try {
			List<E> result = baseService.findWithSort(clazz,SortType.DESC, fieldName);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value="find/{fieldName}/{fieldValue}",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> findByFieldNameAndValue(@PathVariable("fieldName") String fieldName,@PathVariable("fieldValue") String fieldValue) {
		try {
			List<E> result = baseService.findByFieldNameAndValue(clazz, fieldName, fieldValue);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 模糊查询并按指定字段降序排序
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @Function
	 */
	@RequestMapping(value="searchWithDESC",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> searchWithDESC(String fieldName,String fieldValue,String sortFiledName) {
		try {
			List<E> result = baseService.search(clazz, fieldName, fieldValue,sortFiledName,SortType.DESC);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 模糊查询，不指定排序
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @Function
	 */
	@RequestMapping(value="search",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> searchWithNormal(String fieldName,String fieldValue) {
		try {
			List<E> result = baseService.search(clazz, fieldName, fieldValue,null,SortType.NORMAL);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 模糊查询并按指定字段排序
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @Function
	 */
	@RequestMapping(value="searchWithASC",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> searchWithASC(String fieldName,String fieldValue,String sortFiledName) {
		try {
			List<E> result = baseService.search(clazz, fieldName, fieldValue,sortFiledName,SortType.ASC);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value="isExists/{fieldName}/{fieldValue}",  method = RequestMethod.GET)
	@ResponseBody
	public boolean isExists(@PathVariable("fieldName") String fieldName,@PathVariable("fieldValue") String fieldValue) {
		List<E> result = null;
		try {
			 result = baseService.findByFieldNameAndValue(clazz,fieldName,fieldValue);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		if (result != null && result.size() != 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 查找除了该id以外的满足条件的实体集合
	 * @param fieldName
	 * @param fieldValue
	 * @param id
	 * @return
	 */
	@RequestMapping(value="isExists/{fieldName}/{fieldValue}/{id}",  method = RequestMethod.GET)
	@ResponseBody
	public boolean isExists(@PathVariable("fieldName") String fieldName,@PathVariable("fieldValue") String fieldValue,@PathVariable("id")Object id) {
		List<E> result = null;
		if (id != null) {
			try {
				result = baseService.findByFieldNameAndValue(clazz,fieldName,fieldValue,id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		if (result != null && result.size() != 0) {
			return true;
		}
		return false;
	}

	@RequestMapping(value="range/{index}/{count}",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> findRange(@PathVariable("index") int index,
			@PathVariable("count") int count) {
		try {
			List<E> result = baseService.find(index, count, clazz);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	@RequestMapping(value="findByCid",  method = RequestMethod.GET)
	@ResponseBody
	public List<E> findByCid() {
		try {
			//String cid = utilService.getCurrentUser().company.getId();
			List<E> result = baseService.findByFieldNameAndValue(clazz, "company.id", "1");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
}
