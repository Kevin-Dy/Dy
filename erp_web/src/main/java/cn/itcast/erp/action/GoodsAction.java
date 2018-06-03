package cn.itcast.erp.action;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.itcast.erp.biz.IGoodsBiz;
import cn.itcast.erp.entity.ExcelBean;
import cn.itcast.erp.entity.Goods;

/**
 * 商品Action 
 * @author Administrator
 *
 */
public class GoodsAction extends BaseAction<Goods> {

	private IGoodsBiz goodsBiz;

	public void setGoodsBiz(IGoodsBiz goodsBiz) {
		this.goodsBiz = goodsBiz;
		super.setBaseBiz(this.goodsBiz);
	}
	
	@Override
	public ExcelBean getExcelBean() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String filename="商品表"+simpleDateFormat.format(new Date())+".xls";
		String sheetName="商品";
		String[] headerNames={"编号","名称","产地","厂家","计量单位","进货价格","销售价格","商品类型"};
		String[] valueNamesExport={"uuid","name","origin","producer","unit","inprice","outprice","goodstypeName"};
		String[] valueNamesImport={"uuid","name","origin","producer","unit","inprice","outprice","goodstypeName"};
		int isUniqueIndex=1;
		ExcelBean excelBean = new ExcelBean();
		excelBean.setFilename(filename);
		excelBean.setSheetName(sheetName);
		excelBean.setHeaderNames(headerNames);
		excelBean.setValueNamesExport(valueNamesExport);
		excelBean.setValueNamesImport(valueNamesImport);
		excelBean.setIsUniqueIndex(isUniqueIndex);
		return excelBean;
	}

}
