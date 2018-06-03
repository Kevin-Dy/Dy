package cn.itcast.erp.entity;

public class ExcelBean {
	private String filename;//Excel文件名
	private String sheetName;//工作表名
	private String[] headerNames;//表头列名
	private String[] valueNamesExport;//导出表数据对应的对象属性名
	private String[] valueNamesImport;//导入表数据对应的对象属性名
	private int isUniqueIndex;//校验记录是否唯一的属性名在valueNames对应索引
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String[] getHeaderNames() {
		return headerNames;
	}
	public void setHeaderNames(String[] headerNames) {
		this.headerNames = headerNames;
	}
	public int getIsUniqueIndex() {
		return isUniqueIndex;
	}
	public void setIsUniqueIndex(int isUniqueIndex) {
		this.isUniqueIndex = isUniqueIndex;
	}
	public String[] getValueNamesExport() {
		return valueNamesExport;
	}
	public void setValueNamesExport(String[] valueNamesExport) {
		this.valueNamesExport = valueNamesExport;
	}
	public String[] getValueNamesImport() {
		return valueNamesImport;
	}
	public void setValueNamesImport(String[] valueNamesImport) {
		this.valueNamesImport = valueNamesImport;
	}
	
	
}
