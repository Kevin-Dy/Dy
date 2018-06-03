package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IGoodstypeDao;
import cn.itcast.erp.dao.IReportDao;
import cn.itcast.erp.entity.Goodstype;

public class ReportBiz implements IReportBiz {
	
	private IReportDao reportDao;
	private IGoodstypeDao goodstypeDao;
	
	

	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
	}

	@Override
	public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
		return reportDao.orderReport(startDate, endDate);
	}

	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	@Override
	public List<Map<String, Object>> trendReport(int year) {
		// 构建返回的数据
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// map<name,vlaue>
		Map<String, Object> monthData = null;
		// 循环12个进行查询
		for(int i = 1; i <= 12; i++){
			// 查询每个月销售额
			monthData = reportDao.trendReport(year, i);
			if(null == monthData){
				// 这个月没有销售额, 补0
				//[{name:"1",y:52.1},{name:"2",y:931.2}]
				monthData = new HashMap<String, Object>();
				monthData.put("name", i);
				monthData.put("y", 0);
			}
			result.add(monthData);
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> reportReturn(Date startDate, Date enDate) {
		List<Map<String,Object>> list = reportDao.reportReturn(startDate, enDate);
		for (Map<String, Object> map : list) {
			//drilldown: true
			map.put("drilldown", true);
		}
		return list;
	}
	
	
	@Override
	public Map<String, Object> goodsTypeReport(String typeName,Date startTime, Date endTime) {
//		public List<Map<String, Object>> GoodstypeReport(String typeName,Date startTime, Date endTime) {
		List<Map<String, Object>> orderReport = reportDao.orderReport(typeName,startTime, endTime);
		
		Map<String, Object> map=new HashMap<>();
		map.put("name", typeName);
		map.put("id", typeName);
		
		List<Object> list=new ArrayList<>();
		
		for (Map<String, Object> empMap : orderReport) {
			
			Object [] data = {empMap.get("name"),empMap.get("y")};
			list.add(data);
		}
		map.put("data", list);
		return map;
	}
	
	@Override
	public List<Map<String, Object>> trendReportReturn(int year) {
		//查询商品类型的总数
		long count = goodstypeDao.getCount(null, null, null);
		//查询所有的商品类型信息
		List<Goodstype> GoodstypesList = goodstypeDao.getList(null, null, null);
		//查询数据
		List<List<?>> list = reportDao.trendReportReturn(year);
		System.out.println(JSON.toJSONString(list));
		//创建一个list集合map存放返回的数据
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		//存放柱状图的数据data
		Map<String, Object> data = null;
		//data中存放每个月总价的数据
		List<Double> totalmoney = null;
		
		Map<String, Map<String, Object>> typeMap = new HashMap<String, Map<String, Object>>();//按类型分类存放的待查漏补缺的数据
		//遍历商品类型
		for (int i = 1; i <= count; i++) {
			//遍历获取的数据
			Map<String, Object> monthMap = new HashMap<String, Object>();//存放同类型下所有数据
			for (List<?> l1 : list) {
				//判断类型是否存在数据
				if (Long.parseLong(l1.get(0)+"") == i) {
					//将数据按照商品类型分开封装，查漏补缺
					monthMap.put((String) l1.get(1), l1.get(3));
					
				}
			}
			//获得按照类型分类的数据
			typeMap.put(i+"", monthMap);
		}
		System.out.println(typeMap);
		//对类型查漏补缺
		for (int i = 1; i <= count; i++) {
			String GoodstypeName = getGoodstypeName(GoodstypesList, i);
			//如果不存在商品类型的数据则添加空数据,左边表格显示
			if (typeMap.get(i+"")!=null && typeMap.get(i+"").size()==0) {
				continue;
				}else {
				Map<String, Object> map = typeMap.get(i+"");
				data = new HashMap<String, Object>();
				totalmoney = new ArrayList<Double>();
				for (int j = 1; j <= 12; j++) {
					//如果该类型下没有当前月份
					if (null == map.get(j+"月")) {
						totalmoney.add((double) 0);
						data.put("m"+j, (double) 0);
					}else {
						totalmoney.add((Double) map.get(j+"月"));
						data.put("m"+j, (Double) map.get(j+"月"));
					}
				}
				data.put("name", GoodstypeName);
				data.put("data", totalmoney);
				result.add(data);
			}
		}
		return result;
	}
	
	/**
	 * 查询指定id的名称
	 * @param GoodstypesList
	 * @param uuid
	 * @return
	 */
	public String getGoodstypeName(List<Goodstype> GoodstypesList,int uuid){
		String name = null;
		for (Goodstype Goodstype : GoodstypesList) {
			if (Goodstype.getUuid()==uuid) {
				name = Goodstype.getName();
			}
		}
		return name;
	}

}
