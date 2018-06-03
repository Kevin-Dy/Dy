package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
/**
 * 供应商业务逻辑类
 * @author Administrator
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		super.setBaseDao(this.supplierDao);
	}
	
	@Override
	public void add(Supplier t) {
		Subject subject = SecurityUtils.getSubject();
		if(Supplier.TYPE_CUSTOMER.equals(t.getType())){
			if(!subject.isPermitted("客户")){
				throw new ErpException("没有权限");
			}
		}
		if(Supplier.TYPE_SUPPLIER.equals(t.getType())){
			if(!subject.isPermitted("供应商")){
				throw new ErpException("没有权限");
			}
		}
		super.add(t);
	}

	@Override
	public void export(OutputStream os, Supplier t1) throws Exception {
		// 根据条件获取查询的结果
		List<Supplier> list = supplierDao.getList(t1, null, null);
		// 创建工作簿
		Workbook wk = null;
		try {
			wk = new HSSFWorkbook();//2003版本的excel
			// 创建工作表
			String shtName = "供应商";
			if(Supplier.TYPE_CUSTOMER.equals(t1.getType())){
				shtName = "客户";
			}
			Sheet sht = wk.createSheet(shtName);
			// 设置列宽
			sht.setColumnWidth(0, 5000);
			// 创建行, 第一行，写标题
			Row row = sht.createRow(0);// 下标是从0开始
			String[] headers = {"名称","地址","联系人","电话","Email"};
			int[] withds = {4000,8000,2000,3000,8000};
			int i = 0;
			for (; i < headers.length; i++) {
				row.createCell(i).setCellValue(headers[i]);
				// 设置列的宽度
				sht.setColumnWidth(i, withds[i]);
			}
			i = 1; // 内容是从第二行开始，对应的下标为1
			for (Supplier supplier : list) {
				// 每行写入到excel中
				row = sht.createRow(i);
				row.createCell(0).setCellValue(supplier.getName()); // 供应商名称
				row.createCell(1).setCellValue(supplier.getAddress()); // 地址
				row.createCell(2).setCellValue(supplier.getContact()); // 联系人
				row.createCell(3).setCellValue(supplier.getTele()); // 电话
				row.createCell(4).setCellValue(supplier.getEmail()); // Email
				i++;
			}
			wk.write(os);
		} finally{
			if(null != wk)
				try {
					wk.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public void doImport(InputStream is) throws Exception{
		Workbook wk = null;
		try {
			// 读取文件
			wk = new HSSFWorkbook(is);
			Sheet sht = wk.getSheetAt(0);// 获取第一个工作表,下标是从0开始
			String type = "";
			if("供应商".equals(sht.getSheetName())){
				type = Supplier.TYPE_SUPPLIER;
			}
			if("客户".equals(sht.getSheetName())){
				type = Supplier.TYPE_CUSTOMER;
			}
			int lastRowNum = sht.getLastRowNum();// 从0开始
			Supplier supplier = null;
			String name = null;
			Row row = null;
			for(int i = 1; i <= lastRowNum; i++){
				// 第二行开始
				row = sht.getRow(i);
				supplier = new Supplier();
				name = row.getCell(0).getStringCellValue(); // 供应商的名称
				supplier.setName(name);
				// 进行查询判断 通过名称来判断 
				List<Supplier> list = supplierDao.getList(null, supplier, null);
				if(list.size() > 0){
					// 名称已经存在, 进行更新
					supplier = list.get(0);// 进入持久化状态
				}
				supplier.setAddress(row.getCell(1).getStringCellValue()); // 地址
				supplier.setContact(row.getCell(2).getStringCellValue()); // 联系人
				supplier.setTele(row.getCell(3).getStringCellValue());// 电话
				supplier.setEmail(row.getCell(4).getStringCellValue()); // 邮件
				if(list.size() == 0){
					// 名称不存在
					supplier.setType(type);
					supplierDao.add(supplier);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(null != wk){
				try {
					wk.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wk = null;
			}
		}
		
	}
	
}
