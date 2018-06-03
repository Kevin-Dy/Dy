package cn.itcast.erp.realm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ErpRealm extends AuthorizingRealm {
	
	private IEmpBiz empBiz;
	
	private IMenuBiz menuBiz;
	
	private JedisPool jedisPool;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("授权...");
		// 存放的是登陆用户所有的权限信息
		SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
		// 登陆用户 SecurityUtils.getSubject().getPriciple()
		Emp loginUser = (Emp)principals.getPrimaryPrincipal();
		//System.out.println(loginUser.getDep());
		List<Menu> empMenus = null;
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			// 检查redis缓存是否存在
			String menuListString = jedis.get(Const.MENU_KEY + loginUser.getUuid());
			if(StringUtils.isEmpty(menuListString)){
				// 没有缓存, 查询数据
				// 获取登陆用户的权限集合
				empMenus = menuBiz.getEmpMenus(loginUser.getUuid());
				// 存入缓存
				String jsonString = JSON.toJSONString(empMenus);
				jedis.set(Const.MENU_KEY + loginUser.getUuid(), jsonString);
			}else{
				empMenus = JSON.parseArray(menuListString,Menu.class);
			}
		}catch(Exception e){
			e.printStackTrace();
			empMenus = menuBiz.getEmpMenus(loginUser.getUuid());
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
		
		for (Menu menu : empMenus) {
			sai.addStringPermission(menu.getMenuname());//bcd
		}
		//System.out.println(loginUser);
		
		//sai.addStringPermission("采购确认");
		
		return sai;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("认证...");
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		String username = upt.getUsername();
		String pwd = new String(upt.getPassword());
		// 查询数据库，用户是否存在
		Emp emp = empBiz.findByUsernameAndPwd(username, pwd);
		if(null != emp){
			// 构建认证信息
			// principal: 当事人或主角,登陆用户
			// credentials: 凭证, 密码。 shiro会把pwd跟token中的密码进行匹配，如果不一致则报错
			// realmName: realm的名称
			SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(emp,pwd,getName());
			return sai;
		}
		return null;
	}

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

}
