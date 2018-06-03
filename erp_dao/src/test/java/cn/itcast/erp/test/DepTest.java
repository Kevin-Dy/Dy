package cn.itcast.erp.test;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.dao.IOrderdetailDao;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:applicationContext*.xml"})
public class DepTest {
	
	@Autowired
	private IOrderdetailDao orderdetailDao;
	
	@Test
	public void testLogic(){
		Map<String, Object> result = orderdetailDao.doOutStoreByProc(20l, 1l, 1l);
		System.out.println(JSON.toJSONString(result));
	}
	

}
