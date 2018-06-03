package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单数据访问接口
 * @author Administrator
 *
 */
public interface IMenuDao extends IBaseDao<Menu>{

	/**
	 * 获取用户的权限菜单
	 * @param uuid
	 * @return
	 */
	List<Menu> getEmpMenus(Long uuid);
}
