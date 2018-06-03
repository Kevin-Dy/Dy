package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;
/**
 * 员工业务逻辑层接口
 * @author Administrator
 *
 */
public interface IEmpBiz extends IBaseBiz<Emp>{

	/**
	 * 登陆用户校验
	 * @param username
	 * @param pwd
	 * @return
	 */
	Emp findByUsernameAndPwd(String username, String pwd);
	
	/**
	 * 修改密码
	 * @param oldPwd
	 * @param newPwd
	 * @param uuid
	 */
	void updatePwd(String oldPwd, String newPwd, Long uuid);
	
	/**
	 * 重置密码
	 * @param newPwd
	 * @param uuid
	 */
	void updatePwd_reset(String newPwd, Long uuid);
	
	/**
	 * 读取用户角色设置信息
	 * @param uuid
	 * @return
	 */
	List<Tree> readEmpRoles(Long uuid);
	
	/**
	 * 更新用户的角色
	 * @param uuid 用户的编号
	 * @param ids 角色编号，多个以逗号分割
	 */
	void updateEmpRoles(Long uuid, String ids);
}

