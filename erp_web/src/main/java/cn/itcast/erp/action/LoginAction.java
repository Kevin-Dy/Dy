package cn.itcast.erp.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.util.WebUtil;

/**
 * 登陆登出
 *
 */
public class LoginAction {
	
	private String username; // 登陆名
	private String pwd; // 密码
	
	/**
	 * 登陆
	 */
	public void login(){
		/*Emp emp;
		try {
			emp = empBiz.findByUsernameAndPwd(username, pwd);
			if(null != emp){
				// 放入session中
				ServletActionContext.getRequest().getSession().setAttribute("loginUser", emp);
				WebUtil.ajaxReturn(true, "登陆成功");
			}else{
				WebUtil.ajaxReturn(false, "用户名或密码不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "登陆失败");
		}*/
		// 当事人
		Subject subject = SecurityUtils.getSubject();
		// 令牌信息
		UsernamePasswordToken upt = new UsernamePasswordToken(username,pwd);
		
		try {
			subject.login(upt);
			WebUtil.ajaxReturn(true, "登陆成功");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "登陆失败");
		}
		
	}
	
	/**
	 * 显示登陆用户名
	 */
	public void showName(){
		// 取出登陆用户
		Emp emp = WebUtil.getLoginUser();
		if(null != emp){
			WebUtil.ajaxReturn(true, emp.getName());
		}else{
			WebUtil.ajaxReturn(false, "您还没有登陆");
		}
	}
	
	/**
	 * 退出登陆
	 */
	public void loginOut(){
		//ServletActionContext.getRequest().getSession().removeAttribute("loginUser");
		SecurityUtils.getSubject().logout();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public void unauthorized(){
		// 判断 是ajax 请求
		// header X-Requested-With: XMLHttpRequest
		HttpServletRequest req = ServletActionContext.getRequest();
		//判断 是ajax 请求
		if("XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))){
			
			WebUtil.ajaxReturn(false, "没有权限");
		}else{
			try {
				ServletActionContext.getResponse().sendRedirect("error.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
