package cn.itcast.erp.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;
	private String json;
	private IWaybillWs waybillWs;
	private Long waybillsn;

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}
	
	@Override
	public void add() {
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		
		try {
			//System.out.println(json);
			//[{goodsuuid:,goodsname:,...}]
			List<Orderdetail> orderDetails = JSON.parseArray(json, Orderdetail.class);
			Orders orders = getT();
			
			orders.setOrderDetails(orderDetails);
			orders.setCreater(loginUser.getUuid());
			//System.out.println("供应商编号:" + getT().getSupplieruuid());
			//super.add();
			ordersBiz.add(orders);
			WebUtil.ajaxReturn(true, "新增订单成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(true, "新增订单失败");
		}
	}
	
	/**
	 * 审核
	 */
	public void doCheck(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		
		try {
			ordersBiz.doCheck(getId(), loginUser.getUuid());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "没有权限");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "审核失败");
		}
	}
	
	/**
	 * 确认
	 */
	public void doStart(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		
		try {
			ordersBiz.doStart(getId(), loginUser.getUuid());
			WebUtil.ajaxReturn(true, "确认成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "没有权限");
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "确认失败");
		}
	}
	
	/**
	 * 由我发起的订单列表
	 */
	public void myListByPage(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			//{success:false, message:''}
			//{total:11,rows:[{}]}
			//WebUtil.ajaxReturn(false, "您还没有登陆");
			Map<String,Object> rtn = new HashMap<String,Object>();
			rtn.put("total", 0);
			rtn.put("rows", new ArrayList<Object>());
			WebUtil.write(rtn);
			return;
		}
		if(null == getT1()){
			// 如果查询条件为空，则构建查询条件
			setT1(new Orders());
		}
		// 设置下单员编号
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}
	
	/**
	 * 导出订单
	 */
	public void exportById(){
		String filename = String.format("orders_%d.xls", getId());
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			// 写响应头
			response.setHeader("Content-Disposition", "attachement;filename=" + filename);
			ordersBiz.exportById(response.getOutputStream(), getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询物流路径信息
	 */
	public void waybilldetailList(){
		List<Waybilldetail> list = waybillWs.waybilldetailList(waybillsn);
		WebUtil.write(list);
	}
	

	public void setJson(String json) {
		this.json = json;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}

}
