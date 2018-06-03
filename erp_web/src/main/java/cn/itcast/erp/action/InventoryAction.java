package cn.itcast.erp.action;
import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

/**
 * 盘盈盘亏Action 
 * @author Administrator
 *
 */
public class InventoryAction extends BaseAction<Inventory> {

	private IInventoryBiz inventoryBiz;

	public void setInventoryBiz(IInventoryBiz inventoryBiz) {
		this.inventoryBiz = inventoryBiz;
		super.setBaseBiz(this.inventoryBiz);
	}
	
	
	/**
	 * 盘盈盘亏审核
	 */
	public void doCheck(){
		
		try {
			Emp emp = WebUtil.getLoginUser();
			inventoryBiz.doCheck(getId(),emp.getUuid());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "审核失败");
		}
		
	}

	/**
	 * 添加登记信息
	 */
	@Override
	public void add() {
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		try {
			//设置下单人
			Inventory inventory = getT();
			inventory.setCreater(loginUser.getUuid());
			inventoryBiz.add(inventory);
			WebUtil.ajaxReturn(true, "添加登记成功");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "添加登记失败");
		}
		//super.add();
	}
	
	/**
	 * 盘盈盘亏登记查询
	 */
	public void myListByPage(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "请登录后操作");
			return;
		}
		if(null == getT1()){
			//构建查询条件
			setT1(new Inventory());
		}
		//
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}
}
