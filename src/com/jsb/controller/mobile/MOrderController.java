package com.jsb.controller.mobile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jsb.Util;
import com.jsb.exception.NotFountException;
import com.jsb.exception.SessionTimeoutException;
import com.jsb.model.JsonResult;
import com.jsb.model.PageResult;
import com.jsb.model.ReturnCode;
import com.jsb.model.order.OrderInfo;
import com.jsb.model.order.ReqOrderInfo;
import com.jsb.model.order.OrderProduct;
import com.jsb.service.BaseInfoService;
import com.jsb.service.log.LogService;
import com.jsb.service.order.OrderService;
import com.jsb.util.ResultDataUtil;
import com.mchange.v2.resourcepool.TimeoutException;

@Controller
@RequestMapping("mobile/order")
public class MOrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private LogService logService;
	@Autowired
	BaseInfoService baseInfoService;

	@RequestMapping(value = "getOrderInfoByNameForPage", method = RequestMethod.GET)
	@ResponseBody
	public ResultDataUtil getOrderInfoByNameForPage(String name, String page,
			String rows) {
		name = Util.convertString(name);
		int pageI = Util.convertPage(page);
		int rowsI = Util.convertRow(rows);
		return orderService.getOrderInfoForMobile(name, pageI, rowsI,"");
	}

	/**
	 * 产品页提交表单
	 * 
	 * @return
	 */
	@RequestMapping(value = "submitOrder", method = RequestMethod.POST)
	public String submitOrder() {
		return "view/mobile/order_confirm";
	}

	/**
	 * 保存订单
	 * 
	 * @param reqOrderInfos
	 *            前台传来json数组转换后的对象信息
	 * @return
	 */
	@RequestMapping(value = "saveOrder", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult<OrderInfo> saveOrder(String srq,String note) {
		JsonResult<OrderInfo> rs = new JsonResult<OrderInfo>();
		try {
			List<ReqOrderInfo> rois = orderService.getCartGoods();
			if (rois == null) {
				rs.setCode(ReturnCode.FAIL);
				rs.setMsg("您的购物车中没有产品信息...");
			} else {
				OrderInfo orderInfo = orderService.saveOrderInfo(rois,srq,note);
				if (orderInfo != null) {
					orderService.clearCart();// 清空购物车
					rs.setCode(ReturnCode.SUCCESS);
					rs.setMsg(orderInfo.getId());
				} else {
					rs.setCode(ReturnCode.FAIL);
					rs.setMsg("保存订单出错...");
				}
			}
		} catch (SessionTimeoutException | NotFountException e) {
			e.printStackTrace();
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg(e.getMessage());
			logService.saveLog("保存订单成败:" + e.getMessage());
		} catch (Exception e) {
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg("保存订单产品列表出错...");
			logService.saveLog("保存订单产品列表出错:" + e.getMessage());
		}
		return rs;
	}

	@RequestMapping(value = "delOrder", method = RequestMethod.POST)
	@ResponseBody
	public String del(String id) {
		return orderService.del(id);
	}

	@RequestMapping(value = "orderDetail", method = RequestMethod.GET)
	public ModelAndView orderDetail(String id) {
		ModelAndView mv = new ModelAndView("view/mobile/order_detail");
		OrderInfo order = null;
		List<OrderProduct> list = new ArrayList<OrderProduct>();
		if (id == null || id.toLowerCase().equals("null") || id.equals("")) {
			mv.addObject("flag", "0");
		} else {
			mv.addObject("flag", "1");
			order = baseInfoService.findOne(OrderInfo.class, id);
			String sql = "select p.id,p.oid,p.pname,p.pid,p.pnum,p.pprice,p.price,p.total,p.isout,p.msg,x.model,x.size,x.stockCount,x.slt "
					+ "from t_order_product p left join t_product x on p.pid=x.id where p.oid='"
					+ id + "' ";
			list = baseInfoService.getMapBySQL(sql, 0, 0);
			if (order != null) {
				mv.addObject("oid", order.getId());
				mv.addObject("orderSn", order.getOrderSn());
				mv.addObject("orderTime", order.getOrderTime());
				mv.addObject("cname", order.getCname());
				mv.addObject("sname", order.getSname());
				mv.addObject("cid", order.getCid());
				mv.addObject("rname", order.getRname());
				mv.addObject("raddress", order.getRaddress());
				mv.addObject("rtel", order.getRtel());
				mv.addObject("status", order.getStatus());
				mv.addObject("price", order.getPrice());
				mv.addObject("total", order.getTotal());
				mv.addObject("uid", order.getUid());
				mv.addObject("uname", order.getUname());
				mv.addObject("ps", list);
			}
		}
		return mv;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "addCart", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult<ReqOrderInfo> addCart(String pid, int num) {
		System.out.println(num);
		JsonResult<ReqOrderInfo> rs = new JsonResult<ReqOrderInfo>();
		try {
			if (num == 0) {// 删除购物车指定产品
				orderService.removeCartGood(pid);
			} else {
				orderService.addCart(pid, num);
			}
			rs.setCode(ReturnCode.SUCCESS);
			rs.setMsg(orderService.getCartProductNum(pid) + "");
			rs.setTotal(orderService.getCartGoodsNum());
		} catch (TimeoutException e) {
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg(e.getMessage());
			logService.saveLog("添加商品到购物车失败:" + e.getMessage());
		}
		return rs;
	}
	
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "updateCart", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult<ReqOrderInfo> updateCart(String pid, int num) {
		JsonResult<ReqOrderInfo> rs = new JsonResult<ReqOrderInfo>();
		try {
			orderService.updateCartGood(pid, num);
			rs.setCode(ReturnCode.SUCCESS);
			rs.setTotal(orderService.getCartGoodsNum());
		} catch (TimeoutException | NotFountException e) {
			// TODO Auto-generated catch block
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg(e.getMessage());
			logService.saveLog("更新购物车商品失败:" + e.getMessage());
		}
		return rs;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "cartNum", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult<ReqOrderInfo> cartNum() {
		JsonResult<ReqOrderInfo> rs = new JsonResult<ReqOrderInfo>();
		try {
			int num = orderService.getCartGoodsNum();
			rs.setCode(ReturnCode.SUCCESS);
			rs.setTotal(num);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg(e.getMessage());
			logService.saveLog("查询购物车产品数量失败:" + e.getMessage());
		}
		return rs;
	}

	@RequestMapping(value = "orderConfirm", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult<OrderInfo> orderConfirm() {
		JsonResult<OrderInfo> rs = new JsonResult<OrderInfo>();
		try {
			OrderInfo orderInfo = orderService.getPreparedOrderInfo();
			rs.setCode(ReturnCode.SUCCESS);
			rs.setResult(orderInfo);
		} catch (TimeoutException | NotFountException e) {
			// TODO Auto-generated catch block
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg(e.getMessage());
			logService.saveLog("查询购物车产品失败:" + e.getMessage());
		}
		return rs;
	}
	
	@RequestMapping(value = "cusReport", method = RequestMethod.GET)
	@ResponseBody
	public ResultDataUtil cusReport(String day,String name,String page,String pageSize,String fenlei,String sort,String sortName) {
		ResultDataUtil rd = new ResultDataUtil();
		rd.setCode("0");
		
		name = Util.convertString(name);
		day = Util.convertString(day);
		fenlei = Util.convertString(fenlei);
		if(day.equals("")){
			if(fenlei.equals("1")){
				day = Util.nowYear();
			}else if(fenlei.equals("2")){
				day = Util.nowMonth();
			}else{
				day = Util.nowDate();
			}
		}
		
		int pageI = Util.convertPage(page);
		int rows = Util.convertRow(pageSize);
		
		if(sort==null||sort.equals("")){
			sort = "desc";
		}
		
		if(sortName==null||sortName.equals("")){
			sortName = "price";
		}
		
		PageResult pr = orderService.getOrderCountOrderBy(name, day, pageI, rows,sort,sortName);
		if(pr.getTotal()>0&&pr.getRows()!=null&&pr.getRows().size()>0){
			rd.setCode("1");
			rd.setData(pr.getRows());
			rd.setDataNum(pr.getTotal());
		}
		return rd;
	}
	
	@RequestMapping(value = "cusReportById", method = RequestMethod.GET)
	@ResponseBody
	public ResultDataUtil cusReportById(String page,String pageSize,String id) {
		id = Util.convertString(id);
		
		int pageI = Util.convertPage(page);
		int rows = Util.convertRow(pageSize);
		
		return orderService.getOrderInfoForMobile("", pageI, rows, id);
	}
	
	
	@RequestMapping(value = "searchOrder", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult<OrderInfo> searchOrder(String orderStatus, String orderTime, String key, int page, int rows) {
		
		JsonResult<OrderInfo> rs = new JsonResult<OrderInfo>();
		if (page == 0) {
			page = 1;
		}
		
		if (rows == 0) {
			rows = 4;
		}
		
		try {
			List<OrderInfo> ois = orderService.searchOrder( orderStatus,  orderTime,  key,  page,  rows);
			if (ois == null) {
				rs.setCode(ReturnCode.FAIL);
				rs.setMsg("没有查找到订单信息...");
			} else {
				rs.setCode(ReturnCode.SUCCESS);
				rs.setResults(ois);
			}
		} catch (Exception e) {
			rs.setCode(ReturnCode.FAIL);
			rs.setMsg("查询订单信息出错...");
			logService.saveLog("查询订单信息出错-- 类：MOrderController 方法：searchOrder " + e.getMessage());
		}
		return rs;
	}
}
