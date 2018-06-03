package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}
	
	@Override
	public void add(Orders orders) {
		// 生成日期
		orders.setCreatetime(new Date());
		// 订单的类型为采购
		//orders.setType(Orders.TYPE_IN); 由前端传过来
		// 订单的状态 未审核
		String odState = "";
		// 获取登陆用户的操作对象
		Subject subject = SecurityUtils.getSubject();
		
		if(Orders.TYPE_IN.equals(orders.getType())){
			if(!subject.isPermitted("我的采购订单")){
				throw new ErpException("没有权限");
			}
			// 采购
			orders.setState(Orders.STATE_CREATE);
			odState = Orderdetail.STATE_NOT_IN;
		}else if (Orders.TYPE_OUT.equals(orders.getType())){
			if(!subject.isPermitted("我的销售订单")){
				throw new ErpException("没有权限");
			}
			// 销售
			orders.setState(Orders.STATE_NOT_OUT);
			odState = Orderdetail.STATE_NOT_OUT;
		}else{
			throw new ErpException("订单类型不正确");
		}
		double totalMoney = 0;
		List<Orderdetail> list = orders.getOrderDetails();
		for (Orderdetail od : list) {
			// 累计订单的合计金额
			totalMoney += od.getMoney();
			// 明细的状态为未入库
			od.setState(odState);
			// 告诉明细对应的订单, 一方放弃外键维护了
			od.setOrders(orders);
		}
		orders.setTotalmoney(totalMoney);
		super.add(orders);
	}
	
	@Override
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
		List<Orders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for (Orders orders : list) {
			// 设置下单员
			orders.setCreaterName(empDao.get(orders.getCreater()).getName());
			orders.setCheckerName(getEmpName(orders.getChecker()));
			orders.setStarterName(getEmpName(orders.getStarter()));
			orders.setEnderName(getEmpName(orders.getEnder()));
			
			// 供应商
			orders.setSupplierName(supplierDao.get(orders.getSupplieruuid()).getName());
		}
		return list;
	}
	
	/**
	 * 获取员工名称
	 * @param uuid
	 * @return
	 */
	private String getEmpName(Long uuid){
		if(null == uuid){
			return null;
		}
		return empDao.get(uuid).getName();
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	@Override
	@RequiresPermissions("采购审核")
	public void doCheck(Long uuid, Long empuuid) {
		// 持久化状态的对象
		Orders orders = ordersDao.get(uuid);
		// 状态的判断
		if(!Orders.STATE_CREATE.equals(orders.getState())){
			throw new ErpException("该订单已经审核过了，不能重复审核");
		}
		
//		1. 审核日期		系统时间
		orders.setChecktime(new Date());
//		2. 审核员 		登陆用户的编号
		orders.setChecker(empuuid);
//		3. 状态			已审核 1
		orders.setState(Orders.STATE_CHECK);
	}

	@Override
	@RequiresPermissions("采购确认")
	public void doStart(Long uuid, Long empuuid) {
		// 持久化状态的对象
		Orders orders = ordersDao.get(uuid);
		// 状态的判断
		if(!Orders.STATE_CHECK.equals(orders.getState())){
			throw new ErpException("该订单已经确认过了，不能重复确认");
		}
		
//		1. 确认日期		系统时间
		orders.setStarttime(new Date());
//		2. 确认员 		登陆用户的编号
		orders.setStarter(empuuid);
//		3. 状态			已确认 2
		orders.setState(Orders.STATE_START);
	}

	@Override
	public void exportById(OutputStream os, Long uuid) throws Exception {
		Orders orders = ordersDao.get(uuid);
		List<Orderdetail> orderDetails = orders.getOrderDetails();
		// 创建工作簿
		Workbook wk = null;
		try {
			wk = new HSSFWorkbook();//2003版本的excel
			// 创建工作表
			Sheet sht = wk.createSheet("采购单");
			Row row = null;
			
			// 创建样式, 内容区域的样式
			CellStyle style_content = wk.createCellStyle();
			// 内容区域的字体
			Font font_content = wk.createFont();
			font_content.setFontHeightInPoints((short)11);
			font_content.setFontName("宋体");
			
			// 标题样式
			CellStyle style_title = wk.createCellStyle();
			Font font_title = wk.createFont();
			font_title.setFontHeightInPoints((short)18);
			font_title.setFontName("黑体");
			
			style_content.setAlignment(CellStyle.ALIGN_CENTER);// 水平居中对齐
			style_content.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中对齐
			
			// 复制内容区域的样式，类似格式刷
			style_title.cloneStyleFrom(style_content);
			
			// 设置标题的字体
			style_title.setFont(font_title);
			
			// 设置内容区域的字体
			style_content.setFont(font_content);
			
			style_content.setBorderTop(CellStyle.BORDER_THIN);// 上边框
			style_content.setBorderBottom(CellStyle.BORDER_THIN);// 下边框
			style_content.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
			style_content.setBorderRight(CellStyle.BORDER_THIN);// 右边框
			
			// 创建样式, 内容区域的日期样式
			CellStyle style_date = wk.createCellStyle();
			style_date.cloneStyleFrom(style_content);
			
			DataFormat date_format = wk.createDataFormat();
			style_date.setDataFormat(date_format.getFormat("yyyy-MM-dd HH:mm"));
			
			int rowCnt = 10 + orderDetails.size();
			// 创建行
			for(int i = 2; i < rowCnt; i++){
				row = sht.createRow(i);
				for(int col = 0; col < 4; col++){
					row.createCell(col).setCellStyle(style_content);;
				}
				// 内容区域的行高
				row.setHeight((short)500);
			}
			
			// 合并单元格
			sht.addMergedRegion(new CellRangeAddress(0,0,0,3)); // 标题
			sht.addMergedRegion(new CellRangeAddress(2,2,1,3)); // 供应商
			sht.addMergedRegion(new CellRangeAddress(7,7,0,3)); // 订单明细
			
			// 创建第一行
			Cell cell_title = sht.createRow(0).createCell(0);
			cell_title.setCellStyle(style_title); // 设置标题的样式
			cell_title.setCellValue("采 购 单");
			sht.getRow(2).getCell(0).setCellValue("供应商");
			sht.getRow(2).getCell(1).setCellValue(supplierDao.get(orders.getSupplieruuid()).getName());
			sht.getRow(3).getCell(0).setCellValue("下单日期");
			sht.getRow(4).getCell(0).setCellValue("审核日期");
			sht.getRow(5).getCell(0).setCellValue("采购日期");
			sht.getRow(6).getCell(0).setCellValue("入库日期");
			
			sht.getRow(3).getCell(1).setCellStyle(style_date);
			sht.getRow(4).getCell(1).setCellStyle(style_date);
			sht.getRow(5).getCell(1).setCellStyle(style_date);
			sht.getRow(6).getCell(1).setCellStyle(style_date);
			// 下单日期
			sht.getRow(3).getCell(1).setCellValue(orders.getCreatetime());
			setDateValue(sht.getRow(4).getCell(1),orders.getChecktime());// 审核日期
			setDateValue(sht.getRow(5).getCell(1),orders.getStarttime());// 确认日期
			setDateValue(sht.getRow(6).getCell(1),orders.getEndtime()); // 入库日期
			
			
			sht.getRow(3).getCell(2).setCellValue("经办人");
			sht.getRow(4).getCell(2).setCellValue("经办人");
			sht.getRow(5).getCell(2).setCellValue("经办人");
			sht.getRow(6).getCell(2).setCellValue("经办人");
			
			sht.getRow(3).getCell(3).setCellValue(getEmpName(orders.getCreater()));
			sht.getRow(4).getCell(3).setCellValue(getEmpName(orders.getChecker()));
			sht.getRow(5).getCell(3).setCellValue(getEmpName(orders.getStarter()));
			sht.getRow(6).getCell(3).setCellValue(getEmpName(orders.getEnder()));
			
			sht.getRow(7).getCell(0).setCellValue("订单明细");
			sht.getRow(8).getCell(0).setCellValue("商品名称");
			sht.getRow(8).getCell(1).setCellValue("数量");
			sht.getRow(8).getCell(2).setCellValue("价格");
			sht.getRow(8).getCell(3).setCellValue("金额");
			
			// 行高
			sht.getRow(0).setHeight((short)1000); // 标题的行高
			// 列宽
			for(int i = 0; i < 4; i++){
				sht.setColumnWidth(i, 5000);
			}
			
			// 设置明细的数据
			int i = 9;
			for(Orderdetail od : orderDetails){
				row = sht.getRow(i);
				row.getCell(0).setCellValue(od.getGoodsname());
				row.getCell(1).setCellValue(od.getNum());
				row.getCell(2).setCellValue(od.getPrice());
				row.getCell(3).setCellValue(od.getMoney());
				i++;
			}
			sht.getRow(i).getCell(0).setCellValue("合计");
			sht.getRow(i).getCell(3).setCellValue(orders.getTotalmoney());
			
			// 保存 存放到本地磁盘中
			wk.write(os);
		} finally{
			try {
				wk.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 设置日期格式单元格的值，防止日期为空
	 * @param cell
	 * @param date
	 */
	private void setDateValue(Cell cell, Date date){
		if(null != date){
			cell.setCellValue(date);
		}
	}
	
}















