package cn.itcast.erp.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.util.WebUtil;

/**
 * 报表action
 *
 */
public class ReportAction {

	private IReportBiz reportBiz;

	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}
	
	private Date startDate; // 开始日期
	private Date endDate; // 结束日期
	private int year;// 年份
	private String typeName;//商品类型名
	
	

	/**
	 * 销售统计
	 */
	public void orderReport(){
		List<Map<String,Object>> list = reportBiz.orderReport(startDate, endDate);
		WebUtil.write(list);
	}
	
	/**
	 * 销售趋势
	 */
	public void trendReport(){
		List<Map<String, Object>> list = reportBiz.trendReport(year);
		WebUtil.write(list);
	}

	/**销售退货分析
	 * @param startDate
	 * @param enDate
	 */
	public void reportReturn(){
		List<Map<String,Object>> list = reportBiz.reportReturn(startDate, endDate);
		WebUtil.write(list);
	};
	/**
	 * 根据某个商品类型统计详细销售退货报表
	 */
	public void goodsTypeReport(){
		Map<String, Object> orderReport = reportBiz.goodsTypeReport(typeName,startDate,endDate);
		WebUtil.write(orderReport);
	}
	
	/**
	 * 销售退货趋势分析
	 */
	public void trendReportReturn(){
		List<Map<String, Object>> list = reportBiz.trendReportReturn(year);
		System.out.println(JSON.toJSONString(list,true));
		WebUtil.write(list);
	}
	
	
	
	
	
	
	
	

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setYear(int year) {
		this.year = year;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
