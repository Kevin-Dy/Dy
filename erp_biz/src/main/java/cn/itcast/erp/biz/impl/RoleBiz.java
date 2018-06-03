package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	private IRoleDao roleDao;
	private IMenuDao menuDao;
	private JedisPool jedisPool;
	
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}

	@Override
	public List<Tree> readRoleMenus(Long uuid) {
		// 获取角色所拥有的权限, role进入持久化
		Role role = roleDao.get(uuid);
		// 角色所拥有的权限
		List<Menu> roleMenus = role.getMenus();
		
		List<Tree> result = new ArrayList<Tree>();
		// 获取根菜单, 持久化状态
		Menu root = menuDao.get("0");
		//取出所有的一级菜单
		List<Menu> l1Menus = root.getMenus();
		for (Menu l1menu : l1Menus) {
			Tree t1 = createTree(l1menu);
			// 一级菜单的二级菜单
			List<Menu> l2Menus = l1menu.getMenus();
			for (Menu l2menu : l2Menus) {
				Tree t2 = createTree(l2menu);
				// roleMenus集合是否包含这个l2menu
				if(roleMenus.contains(l2menu)){
					//这个角色拥有这个权限菜单时
					t2.setChecked(true);//让它选中
				}
				// 添加子节点
				t1.getChildren().add(t2);
			}
			result.add(t1);
		}
		return result;
	}
	
	/**
	 * 把菜单数据转成树的节点
	 * @param menu
	 * @return
	 */
	private Tree createTree(Menu menu){
		Tree tree = new Tree();
		tree.setId(menu.getMenuid());
		tree.setText(menu.getMenuname());
		// 解决添加子节点时的空异常
		tree.setChildren(new ArrayList<Tree>());
		return tree;
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}

	@Override
	public void updateRoleMenus(Long uuid, String ids) {
		// 获取角色对象，进入持久状
		Role role = roleDao.get(uuid);
		// 清除原有的关系
		// delete from role_menu where roleuuid=?
		role.setMenus(new ArrayList<Menu>());
		// 分割的菜单的编号
		String[] menuIds = ids.split(",");
		
		for (String menuId : menuIds) {
			// 让菜单进入持久态
			Menu menu = menuDao.get(menuId);
			// 重新设置角色下的权限
			role.getMenus().add(menu);
		}
		// 得到拥有这个角色的所有用户
		List<Emp> emps = role.getEmps();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			for (Emp emp : emps) {
				// 清除redis中用户权限的缓存
				String key = Const.MENU_KEY + emp.getUuid();
				jedis.del(key);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(null != jedis){
				try {
					jedis.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jedis = null;
			}
		}
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
}
