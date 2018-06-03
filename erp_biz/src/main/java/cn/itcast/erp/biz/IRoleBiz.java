package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
/**
 * 角色业务逻辑层接口
 * @author Administrator
 *
 */
public interface IRoleBiz extends IBaseBiz<Role>{

	/**
	 * 获取角色的权限菜单
	 * @param uuid
	 * @return
	 */
	List<Tree> readRoleMenus(Long uuid);
	
	/**
	 * 更新角色权限
	 * @param uuid 角色的编号
	 * @param ids 菜单的编号，多个以逗号分割
	 */
	void updateRoleMenus(Long uuid, String ids);
}

