package cn.itcast.erp.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.entity.Emp;

public class WebUtil {

	public static void write(String jsonString){
		HttpServletResponse res = ServletActionContext.getResponse();
		// 通知浏览读取内容使用的编码
		res.setContentType("text/html;charset=utf-8");
		try {
			res.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void write(Object obj){
		String jsonString = JSON.toJSONString(obj);
		WebUtil.write(jsonString);
	}
	
	/**
	 * ajax返回给前端
	 * @param success
	 * @param message
	 */
	public static void ajaxReturn(boolean success, String message){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", success);
		map.put("message", message);
		WebUtil.write(map);
	}
	
	/**
	 * 获取登陆用户
	 * @return
	 */
	public static Emp getLoginUser(){
		return (Emp)SecurityUtils.getSubject().getPrincipal();
	}
	
	
	
}
