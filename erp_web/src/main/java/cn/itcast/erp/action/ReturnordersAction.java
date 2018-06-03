package cn.itcast.erp.action;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;

import com.alibaba.fastjson.JSON;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

/**
 * 退货订单Action 
 * @author Administrator
 *
 */
public class ReturnordersAction extends BaseAction<Returnorders> {

	private IReturnordersBiz returnordersBiz;
	
	private IWaybillWs waybillWs;
	private Long waybillsn;
	private String json;

	public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
		this.returnordersBiz = returnordersBiz;
		super.setBaseBiz(this.returnordersBiz);
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
			returnordersBiz.doCheck(getId(), loginUser.getUuid());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "没有权限");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "审核失败");
		}
	}
	
	
	//---------------------以下是get set-------------------//

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public IWaybillWs getWaybillWs() {
		return waybillWs;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public Long getWaybillsn() {
		return waybillsn;
	}

	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}
	

	@Override
	public void add() {
		//判断是否有用户登录
		Emp emp = WebUtil.getLoginUser();
		if(emp==null){
			WebUtil.ajaxReturn(false, "请先登录");
			return;
		}
		try {
			//[{"num":"2","money":"2.22","goodsuuid":"2","goodsname":"大鸭梨","price":"1.11"}]
			List<Returnorderdetail> returnorderdetail = JSON.parseArray(json, Returnorderdetail.class);
			Returnorders returnorders = getT();
			//封装订单明细
			returnorders.setReturnorderdetails(returnorderdetail);
			//封装下单员
			returnorders.setCreater(emp.getUuid());
			returnordersBiz.add(returnorders);
			WebUtil.ajaxReturn(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(true, "保存失败");
		}
	}
	
	
	/**
	 * 查询由当前用户发起的销售退货订单/采购退货的订单
	 */
	public void myListByPage(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		
		if(getT1()==null){
			setT1(new Returnorders());
		}
		//设置采购退货操作员工编号
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}
	
}
