package cn.itcast.erp.dao.impl;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ParameterMode;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.orm.hibernate5.HibernateCallback;

import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细数据访问类
 * @author Administrator
 *
 */
public class OrderdetailDao extends BaseDao<Orderdetail> implements IOrderdetailDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Orderdetail orderdetail1,Orderdetail orderdetail2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Orderdetail.class);
		if(orderdetail1!=null){
			if(null != orderdetail1.getGoodsname() && orderdetail1.getGoodsname().trim().length()>0){
				dc.add(Restrictions.like("goodsname", orderdetail1.getGoodsname(), MatchMode.ANYWHERE));
			}
			if(null != orderdetail1.getState() && orderdetail1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", orderdetail1.getState()));
			}
			// 按订单查询
			if(null != orderdetail1.getOrders() && null != orderdetail1.getOrders().getUuid()){
				dc.add(Restrictions.eq("orders", orderdetail1.getOrders()));
			}

		}
		return dc;
	}

	@Override
	public Map<String, Object> doOutStoreByProc(final Long uuid, final Long empuuid, final Long storeuuid) {
		// TODO Auto-generated method stub
		return this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Map<String, Object>>(){

			@Override
			public Map<String, Object> doInHibernate(Session session) throws HibernateException {
				Map<String, Object> result = new HashMap<String, Object>();
				ProcedureCall call = session.createStoredProcedureCall("PROC_DOOUTSTORE");
				// p1: 位置 1开始
				// p2: 数据的类型
				// p3: 参数的类型
				call.registerParameter(1, long.class, ParameterMode.IN).bindValue(uuid);
				call.registerParameter(2, long.class, ParameterMode.IN).bindValue(empuuid);
				call.registerParameter(3, long.class, ParameterMode.IN).bindValue(storeuuid);
				call.registerParameter(4, long.class, ParameterMode.OUT);
				call.registerParameter(5, String.class, ParameterMode.OUT);
				// outputs封装返回的信息
				ProcedureOutputs outputs = call.getOutputs();// 执行存储过程，只执行一次
				Object outFlag = outputs.getOutputParameterValue(4);
				Object outMessage = outputs.getOutputParameterValue(5);
				
				result.put("outFlag", outFlag);
				result.put("outMessage", outMessage);
				return result;
			}
			
		});
	}

}
