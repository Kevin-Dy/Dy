package cn.itcast.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.itcast.erp.dao.IReportDao;
@SuppressWarnings("unchecked")
public class ReportDao extends HibernateDaoSupport implements IReportDao {

	@Override
	//销售统计
	public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
		String hql ="select new Map(gt.name as name,sum(od.money) as y,gt.name as name2) from "
				+ "Goodstype gt, Orders o, Orderdetail od, Goods g "
				+ "where "
				+ "o = od.orders "
				+ "and gt=g.goodstype "
				+ "and g.uuid=od.goodsuuid "
				+ "and o.type='2' ";
		//保存参数
		List<Date> params = new ArrayList<Date>();
		if(null != startDate){
			hql += "and o.createtime>=? ";
			params.add(startDate);
		}
		if(null != endDate){
			hql += "and o.createtime<=? ";
			params.add(endDate);
		}
		hql += "group by gt.name";
		
		return (List<Map<String,Object>>)this.getHibernateTemplate().find(hql, params.toArray());
	}

	@Override
	//销售趋势
	public Map<String, Object> trendReport(int year, int month) {
		// month/year 都会调用oracle中的extract函数: 从日期中抽取部分的值
		String hql = "select new Map(month(o.createtime) as name,sum(od.money) as y) "
				+ "from Orders o, Orderdetail od "
				+ "where o=od.orders "
				+ "and o.type='2' and year(o.createtime)=? "
				+ "and month(o.createtime)=? "
				+ "group by month(o.createtime)";
		// 有，且只有一条记录
		List<Map<String, Object>> list = (List<Map<String, Object>>) getHibernateTemplate().find(hql, year,month);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	
	@Override
	//根据某个商品类型获取该类型商品销售退货情况(PIE 下钻)

	public List<Map<String, Object>> orderReport(String typeName, Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		List<Object> parmList =new ArrayList<>();
		Map<String, Object> resultMap=new HashMap<>();
		String hql="select new Map(g.name as name ,sum(rod.money) as y) from Returnorders ro,Returnorderdetail rod,Goods g ,Goodstype gt "
				+ " where ro=rod.returnorders and rod.goodsuuid=g.uuid and g.goodstype=gt and gt.uuid=(select gt.uuid  from Goodstype gt where gt.name= ?) "
				+ "and ro.type='2' ";

		
		parmList.add(typeName);
		
		if(startTime !=null){
			hql+=" and ro.createtime >= ?";
			parmList.add(startTime);
		}
		if(endTime !=null){
			hql+=" and ro.createtime <= ?";
			parmList.add(endTime);
		}
		
		hql+= "group by g.uuid,g.name";
		return (List<Map<String, Object>>) getHibernateTemplate().find(hql, parmList.toArray());
	}
	
	@Override
	//显示pie圆形
	public List<Map<String, Object>> reportReturn(Date startDate, Date enDate) {

		String hql = "select new Map(gt.name as name,sum(rt.money) as y) "
				+ " from Returnorders r , Returnorderdetail rt ,Goods g ,Goodstype gt"
				+ " where r=rt.returnorders"
				+ " and rt.goodsuuid = g.uuid"
				+ " and g.goodstype=gt"
				+ " and r.type = '2' ";
		List<Date> param = new ArrayList<Date>();
		if (null != startDate) {
			param.add(startDate);
			hql += " and r.createtime > ?";
		}
		if (null != enDate) {
			param.add(enDate);
			hql += "and r.createtime < ?";
		}
		hql += " group by gt.name";
		List<Map<String, Object>> list = (List<Map<String, Object>>) getHibernateTemplate().find(hql,param.toArray());
		System.out.println(list.size());
		return list;
	}
	
	@Override//退货柱形图
	public List<List<?>> trendReportReturn(int year) {
		//退货柱状图

		String hql = "select new List( gt.uuid as uuid , month(r.createtime)||'月' as months ,gt.name as name,sum(rod.money) as money) "
				+ " from Goods g,Goodstype gt,Returnorders r,Returnorderdetail rod"
				+ " where g.goodstype=gt"
				+ " and rod.goodsuuid=g.uuid"
				+ " and rod.returnorders=r"
				+ " and r.type=2"
				+ " and year(r.createtime)=?"
				+ " group by month(r.createtime),gt.uuid,gt.name"
				+ " order by gt.uuid";
		List<List<?>> list = (List<List<?>>) getHibernateTemplate().find(hql, year);
//		System.out.println(JSON.toJSONString(list));
		return list;
	}

}
