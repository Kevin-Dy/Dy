package cn.itcast.erp.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

public class MyAuthorizationFilter extends PermissionsAuthorizationFilter {

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
		// 获取主角
        Subject subject = getSubject(request, response);
        HttpServletRequest req = (HttpServletRequest)request;
        String url = req.getRequestURI() + "?" +  req.getQueryString(); 
        System.out.println(url);
        // orders.html?oper=doCheck&type=1
        // 当前url标定的权限
        String[] perms = (String[]) mappedValue;

        if(null == perms || perms.length == 0){
        	return true;
        }
        // 遍历 标题权限
        for (String perm : perms) {
        	// 判断用户的授权的权限集里，是否存在这个权限
			if(subject.isPermitted(perm)){
				// 存在则放行
				return true;
			}
		}
        // 一个权限也没有，阻止
        return false;
    }
}
