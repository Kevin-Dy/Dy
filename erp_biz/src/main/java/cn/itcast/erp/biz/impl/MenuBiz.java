package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑类
 * @author Administrator
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;
	
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

	@Override
	public List<Menu> getEmpMenus(Long uuid) {
		return menuDao.getEmpMenus(uuid);
	}

	@Override
	public Menu readEmpMenus(Long uuid) {
		// 获取模板,根菜单里头包含所有的菜单且有层级结构
		Menu root = menuDao.get("0");
		// 获取用户下的菜单集合
		List<Menu> empMenus = getEmpMenus(uuid);
		
		// 复制一级菜单是空的, 返回值
		Menu _root = cloneMenu(root);
		// 得到模板中的所有一级菜单
		List<Menu> l1Menus = root.getMenus();
		// 遍历所有一级菜单
		for (Menu l1Menu : l1Menus) {
			// 复制一级菜单
			Menu _l1 = cloneMenu(l1Menu);
			// 遍历二级菜单
			List<Menu> l2Menus = l1Menu.getMenus();
			for (Menu l2Menu : l2Menus) {
				// 判断用户下是否拥有这个菜单
				if(empMenus.contains(l2Menu)){
					// 包含
					Menu _l2 = cloneMenu(l2Menu);
					// 复制的二级菜单加入到 复制的一级菜单中
					_l1.getMenus().add(_l2);
				}
			}
			if(_l1.getMenus().size() > 0){
				// 复制的一级菜单下有二级菜单, 加入到复制的结果里
				_root.getMenus().add(_l1);
			}
		}
		return _root;
	}
	
	private Menu cloneMenu(Menu src){
		Menu m = new Menu();
		m.setMenuid(src.getMenuid());
		m.setMenuname(src.getMenuname());
		m.setIcon(src.getIcon());
		m.setUrl(src.getUrl());
		m.setMenus(new ArrayList<Menu>());
		return m;
	}
	
}
