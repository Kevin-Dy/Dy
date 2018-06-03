package cn.itcast.erp.biz.impl;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.shiro.crypto.hash.Md5Hash;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IDepDao;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Dep;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * 员工业务逻辑类
 * @author Administrator
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	private IEmpDao empDao;
	private IRoleDao roleDao;
	private JedisPool jedisPool;
	private IDepDao depDao;
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		pwd = encrypt(pwd, username);
		System.out.println("pwd:" + pwd);
		return empDao.findByUsernameAndPwd(username, pwd);
	}
	
	@Override
	public void add(Emp t) {
		// 参数1： 要加密的 内容
		// 参数2： 盐          扰乱码
		// 参数3：散列次数    再多次md5
		Md5Hash md5 = new Md5Hash(t.getUsername(),t.getUsername(),3);
		// 加密
		String newPwd = md5.toString();
		// 设置成加密后的密码
		t.setPwd(newPwd);
		super.add(t);
	}
	
	private String encrypt(String src, String salt){
		Md5Hash md5 = new Md5Hash(src,salt,3);
		return md5.toString();
	}

	@Override
	public void updatePwd(String oldPwd, String newPwd, Long uuid) {
		Emp emp = empDao.get(uuid);
		// 加密旧密码
		oldPwd = encrypt(oldPwd, emp.getUsername());
		if(!emp.getPwd().equals(oldPwd)){
			throw new ErpException("原密码不正确");
		}
		// 加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		empDao.updatePwd(uuid, newPwd);
	}

	@Override
	public void updatePwd_reset(String newPwd, Long uuid) {
		Emp emp = empDao.get(uuid);
		// 加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		empDao.updatePwd(uuid, newPwd);
	}

	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		Emp emp = empDao.get(uuid);
		List<Role> empRoles = emp.getRoles();
		
		List<Tree> result = new ArrayList<Tree>();
		// 所有的角色信息
		List<Role> roles = roleDao.getList(null, null, null);
		// 把角色转成树的节点
		for (Role role : roles) {
			// 把角色转成树的节点
			Tree t = createTree(role);
			if(empRoles.contains(role)){
				// 用户的角色集合中包含这个角色，让它选中
				t.setChecked(true);
			}
			
			// 把节点添加到树中
			result.add(t);
		}
		
		return result;
	}

	@Override
	public void updateEmpRoles(Long uuid, String ids) {
		// 获取用户对象，进入持久状
		Emp emp = empDao.get(uuid);
		// 清除原有的关系
		// delete from emp_role where empuuid=?
		emp.setRoles(new ArrayList<Role>());
		// 分割的角色的编号
		String[] roleIds = ids.split(",");
		
		for (String roleId : roleIds) {
			// 让角色进入持久态
			Role role = roleDao.get(Long.valueOf(roleId));
			// 重新设置用户下的角色
			emp.getRoles().add(role);
		}
		
		// 清除redis用户权限的缓存
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = Const.MENU_KEY + uuid;
			jedis.del(key);
		} catch (Exception e) {
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

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
	/**
	 * 把角色数据转成树的节点
	 * @param role
	 * @return
	 */
	private Tree createTree(Role role){
		Tree tree = new Tree();
		tree.setId(role.getUuid() + "");
		tree.setText(role.getName());
		// 解决添加子节点时的空异常
		tree.setChildren(new ArrayList<Tree>());
		return tree;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	@Override
	public void doImport(InputStream is, String sheetName, String[] valueNames, int isUniqueIndex) throws Exception {
		HSSFWorkbook workbook = null;
		Class<Emp> entityClass = Emp.class;
		try {
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			// 判断工作表明是否正确
			if (sheetName == null || !sheetName.equals(sheet.getSheetName())) {
				throw new ErpException("工作表的名称不正确");
			}
			// 读取数据
			int lastRowNum = sheet.getLastRowNum();
			Emp t = null;
			PropertyDescriptor pd = null;
			Method method = null;
			Cell cell = null;
			for (int i = 1; i <= lastRowNum; i++) {
				// 判断该实体在数据库中是否存在
				t = entityClass.newInstance();
				// 根据属性名获取属性描述器
				pd = new PropertyDescriptor(valueNames[isUniqueIndex], entityClass);
				// 获取set方法
				method = pd.getWriteMethod();
				// 执行set方法
				method.invoke(t, sheet.getRow(i).getCell(isUniqueIndex).getStringCellValue());
				List<Emp> list = empDao.getList(null, t, null);
				if (list != null && list.size() > 0) {
					// 如果存在就修改数据库中查询出来的那个对象
					t = list.get(0);
				}
				// ================更新数据=====================
				for (int j = 1; j < valueNames.length; j++) {
					pd = new PropertyDescriptor(valueNames[j], entityClass);
					method = pd.getWriteMethod();
					cell = sheet.getRow(i).getCell(j);
					cell.setCellType(Cell.CELL_TYPE_STRING);

					Class<?> propertyType = pd.getPropertyType();
					if (propertyType.equals(Date.class)) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						Date date = format.parse(cell.getStringCellValue());
						method.invoke(t, date);
					}else if(propertyType.equals(Double.class)) {
						method.invoke(t, Double.parseDouble(cell.getStringCellValue()));
					}else if(propertyType.equals(Long.class)) {
						method.invoke(t, Long.parseLong(cell.getStringCellValue()));
					}else if(propertyType.equals(String.class)) {
						
						method.invoke(t, cell.getStringCellValue());
					} else {
						excuteElse(method,t, cell.getStringCellValue());
					}

				}
				if("员工".equals(sheetName)){
					Dep dep = new Dep();
					//此时的cell就是最后一个单元格
					dep.setName(cell.getStringCellValue());
					List<Dep> list2 = depDao.getList(dep, null, null);
					if(null == list2||list2.size() == 0){
						return;
					}
					Emp emp = (Emp)t;
					emp.setDep(list2.get(0));
				}
				if (list == null || list.size() == 0) {
					// 不存在则将他保存到数据库中
					empDao.add(t);
				}
			}
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setDepDao(IDepDao depDao) {
		this.depDao = depDao;
	}
	
}
