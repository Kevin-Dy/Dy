package cn.itcast.erp.action;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.WebUtil;

/**
 * 菜单Action 
 * @author Administrator
 *
 */
public class MenuAction extends BaseAction<Menu> {

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	
	/**
	 * 显示菜单
	 */
	public void getMenuTree(){
		// menu进入持久化状态
		//Menu menu = menuBiz.get("0");
		
		//JSON.toJSONString getMenus触发对象导航
		/*for (Menu m1 : menu.getMenus()) {
			// getMenus触发对象导航
			for (Menu m2 : m1.getMenus()) {
				m2.getMenus(); // 没有结果返回
			}
		}*/
		
		Emp emp = WebUtil.getLoginUser();
		if(null != emp){
			Menu menu = menuBiz.readEmpMenus(emp.getUuid());
			WebUtil.write(menu);
		}
		
		
	}

}
