package cn.itcast.erp.util;

import org.aspectj.lang.ProceedingJoinPoint;

public class LoginCheck {

	public Object checkUser(ProceedingJoinPoint pjp){
		System.out.println(pjp.getTarget());
		System.out.println(pjp.getArgs());
		try {
			return pjp.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
