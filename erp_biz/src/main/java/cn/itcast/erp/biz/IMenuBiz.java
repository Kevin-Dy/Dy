package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IMenuBiz extends IBaseBiz<Menu>{

	/**
	 * 获取用户的权限菜单
	 * @param uuid
	 * @return
	 */
	List<Menu> getEmpMenus(Long uuid);
	
	/**
	 * 获取用户的权限菜单
	 * @param uuid
	 * @return
	 */
	Menu readEmpMenus(Long uuid);
}

