package cn.itcast.erp.action;
import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.util.WebUtil;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}
	
	/**
	 * 登陆用户的仓库
	 */
	public void myList(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null != loginUser){
			// 获取查询条件
			Store t1 = getT1();
			// 如果前端没有传查询条件,此时t1为空值
			if(null == t1){
				// 构建查询条件
				t1 = new Store();
				setT1(t1);
			}
			t1.setEmpuuid(loginUser.getUuid());
			super.list();
		}
		// 如果没有登陆，则不返回内容
	}

}
