package cn.itcast.erp.biz.impl;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.itcast.erp.biz.IBaseBiz;
import cn.itcast.erp.dao.IBaseDao;
import cn.itcast.erp.exception.ErpException;

/**
 * 通用业务逻辑实现类
 * @author Administrator
 *
 * @param <T>
 */
public class BaseBiz<T> implements IBaseBiz<T> {
	private Class<T> entityClass;
	public BaseBiz() {
		// 获取对象对应的父类的类型
		Type baseDaoClass = this.getClass().getGenericSuperclass();
		// 转成带参数，即泛型的类型
		ParameterizedType pType = (ParameterizedType) baseDaoClass;
		// 获取参数泛型类型数组
		Type[] types = pType.getActualTypeArguments();
		// 由于我们的BaseDao<T>的泛型参数里只有一个类型T，因此数组的第一个元素就是类型T的实际上的类型
		entityClass = (Class<T>) types[0];
	}
	/** 数据访问注入*/
	private IBaseDao<T> baseDao;

	public void setBaseDao(IBaseDao<T> baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	public List<T> getList(T t1,T t2,Object param){
		return baseDao.getList(t1,t2,param);
	}
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	public List<T> getListByPage(T t1,T t2,Object param,int firstResult, int maxResults){
		return baseDao.getListByPage(t1,t2,param,firstResult, maxResults);
	}

	@Override
	public long getCount(T t1,T t2,Object param) {
		return baseDao.getCount(t1,t2,param);
	}

	@Override
	public void add(T t) {
		baseDao.add(t);
	}

	/**
	 * 删除
	 */
	public void delete(Long uuid){
		baseDao.delete(uuid);
	}
	
	/**
	 * 通过编号查询对象
	 * @param uuid
	 * @return
	 */
	public T get(Long uuid){
		return baseDao.get(uuid);
	}
	
	/**
	 * 通过编号查询对象
	 * @param uuid
	 * @return
	 */
	public T get(String uuid){
		return baseDao.get(uuid);
	}
	
	/**
	 * 更新
	 */
	public void update(T t){
		baseDao.update(t);
	}
	
	@Override
	public void export(OutputStream os, T t1, String sheetName, String[] headerNames, String[] valueNames)
			throws Exception {
		// 创建工作簿
		Workbook wb = new HSSFWorkbook();// 97-03 excel
		// 创建工作表
		Sheet sheet = wb.createSheet(sheetName);
		// 创建表头
		Row row = sheet.createRow(0);// 索引从0开始
		Cell cell = null;
		if (headerNames != null && headerNames.length > 0) {
			for (int i = 0; i < headerNames.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(headerNames[i]);
				sheet.setColumnWidth(i, 3000);
			}
		}
		List<T> list = baseDao.getList(t1, null, null);
		if (list != null && list.size() > 0) {
			int columnNum = 1;
			PropertyDescriptor pd = null;
			Method method = null;
			Object propertyValue = null;
			// 设置文本样式
			CellStyle contextstyle = null;
			for (T t : list) {
				row = sheet.createRow(columnNum);
				for (int i = 0; i < valueNames.length; i++) {
					contextstyle = wb.createCellStyle();
					cell = row.createCell(i);
					// 根据属性名获取属性描述器
					pd = new PropertyDescriptor(valueNames[i], entityClass);
					// 获取get方法
					method = pd.getReadMethod();
					// 获取属性值
					propertyValue = method.invoke(t);
					// ===========判断数据类型，格式化单元格并赋值==============
					Boolean isNum = false;// propertyValue是否为数值型
					Boolean isInteger = false;// propertyValue是否为整数
					Boolean isPercent = false;// propertyValue是否为百分数
					Boolean isDate = false;// propertyValue是否为日期
					if (propertyValue != null || "".equals(propertyValue)) {
						// 判断propertyValue是否为数值型
						isNum = propertyValue.toString().matches("^(-?\\d+)(\\.\\d+)?$");
						// 判断propertyValue是否为整数（小数部分是否为0）
						isInteger = propertyValue.toString().matches("^[-\\+]?[\\d]*$");
						// 判断propertyValue是否为百分数（是否包含“%”）
						isPercent = propertyValue.toString().contains("%");
						// 判断propertyValue是否为日期类型
						isDate = propertyValue.toString().matches("^([1][7-9][0-9][0-9]|[2][0][0-9][0-9])(\\-)([0][1-9]|[1][0-2])(\\-)([0-2][1-9]|[3][0-1]){1}.*");
						
					}
					// 如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
					if (isNum && !isPercent &&!isDate) {
						// 此处设置数据格式
						if (isInteger) {
							contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0"));// 数据格式只显示整数
						} else {
							contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));// 保留两位小数点
						}
						// 设置单元格格式
						cell.setCellStyle(contextstyle);
						// 设置单元格内容为double类型
						cell.setCellValue(Double.parseDouble(propertyValue.toString()));
					} else if (isDate) {
						// 设置单元格数据格式
						cell.setCellStyle(contextstyle);
						// 设置单元格内容为字符型
						cell.setCellValue(propertyValue.toString().substring(0, 10));
					}else{
						cell.setCellStyle(contextstyle);
						// 设置单元格内容为字符型
						cell.setCellValue(propertyValue.toString());
					}
					// ============================================
				}
				columnNum++;
			}
		}
		// 保存到输出流中
		try {
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭工作簿
			try {
				wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void doImport(InputStream is, String sheetName, String[] valueNames, int isUniqueIndex) throws Exception {
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			// 判断工作表明是否正确
			if (sheetName == null || !sheetName.equals(sheet.getSheetName())) {
				throw new ErpException("工作表的名称不正确");
			}
			// 读取数据
			int lastRowNum = sheet.getLastRowNum();
			T t = null;
			PropertyDescriptor pd = null;
			Method method = null;
			Cell cell = null;
			for (int i = 1; i <= lastRowNum; i++) {
				// 判断该实体在数据库中是否存在
				t = entityClass.newInstance();
				// 根据属性名获取属性描述器
				pd = new PropertyDescriptor(valueNames[isUniqueIndex], entityClass);
				// 获取set方法
				method = pd.getWriteMethod();
				// 执行set方法
				method.invoke(t, sheet.getRow(i).getCell(isUniqueIndex).getStringCellValue());
				List<T> list = baseDao.getList(null, t, null);
				if (list != null && list.size() > 0) {
					// 如果存在就修改数据库中查询出来的那个对象
					t = list.get(0);
				}
				// ================更新数据=====================
				for (int j = 1; j < valueNames.length; j++) {
					pd = new PropertyDescriptor(valueNames[j], entityClass);
					method = pd.getWriteMethod();
					cell = sheet.getRow(i).getCell(j);
					cell.setCellType(Cell.CELL_TYPE_STRING);

					Class<?> propertyType = pd.getPropertyType();
					if (propertyType.equals(Date.class)) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						Date date = format.parse(cell.getStringCellValue());
						method.invoke(t, date);
					}else if(propertyType.equals(Double.class)) {
						method.invoke(t, Double.parseDouble(cell.getStringCellValue()));
					}else if(propertyType.equals(Long.class)) {
						method.invoke(t, Long.parseLong(cell.getStringCellValue()));
					}else if(propertyType.equals(String.class)) {
						method.invoke(t, cell.getStringCellValue());
					} else {
						excuteElse(method,t, cell.getStringCellValue());
					}

				}
				if (list == null || list.size() == 0) {
					// 不存在则将他保存到数据库中
					baseDao.add(t);
				}
			}
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	//子类实现
	public void excuteElse(Method method, T t, String stringCellValue) {
	}

}

