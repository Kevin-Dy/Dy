package cn.itcast.erp.dao.impl;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
/**
 * 仓库库存数据访问类
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class StoredetailDao extends BaseDao<Storedetail> implements IStoredetailDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Storedetail storedetail1,Storedetail storedetail2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Storedetail.class);
		if(storedetail1!=null){
			if(null != storedetail1.getStoreuuid()){
				dc.add(Restrictions.eq("storeuuid", storedetail1.getStoreuuid()));
			}
			if(null != storedetail1.getGoodsuuid()){
				dc.add(Restrictions.eq("goodsuuid", storedetail1.getGoodsuuid()));
			}
		}
		return dc;
	}
	
	@Override
	public List<Storealert> getStorealertList() {
		String hql = "from Storealert where storenum < outnum";
		return (List<Storealert>) this.getHibernateTemplate().find(hql);
	}

}
