package cn.itcast.erp.biz.impl;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import cn.itcast.erp.biz.IGoodsBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IGoodstypeDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Goodstype;
import cn.itcast.erp.exception.ErpException;
/**
 * 商品业务逻辑类
 * @author Administrator
 *
 */
public class GoodsBiz extends BaseBiz<Goods> implements IGoodsBiz {

	private IGoodsDao goodsDao;
	private IGoodstypeDao goodstypeDao;
	
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
		super.setBaseDao(this.goodsDao);
	}
	
	@Override
	public void doImport(InputStream is, String sheetName, String[] valueNames, int isUniqueIndex) throws Exception {
		HSSFWorkbook workbook = null;
		Class<Goods> entityClass = Goods.class;
		try {
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			// 判断工作表明是否正确
			if (sheetName == null || !sheetName.equals(sheet.getSheetName())) {
				throw new ErpException("工作表的名称不正确");
			}
			// 读取数据
			int lastRowNum = sheet.getLastRowNum();
			Goods t = null;
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
				List<Goods> list = goodsDao.getList(null, t, null);
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
				if("商品".equals(sheetName)){
					Goodstype goodstype = new Goodstype();
					//此时的cell就是最后一个单元格
					goodstype.setName(cell.getStringCellValue());
					List<Goodstype> list2 = goodstypeDao.getList(goodstype, null, null);
					if(null == list2||list2.size() == 0){
						return;
					}
					Goods goods = (Goods)t;
					goods.setGoodstype(list2.get(0));
				}
				if (list == null || list.size() == 0) {
					// 不存在则将他保存到数据库中
					goodsDao.add(t);
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

	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
	}
	
}
