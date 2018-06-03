package cn.itcast.erp.action;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.util.WebUtil;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}
	
	private String q;// 为'remote'模式的时候，用户输入将会发送到名为'q'的http请求参数， 做名称的模糊查询

	public void setQ(String q) {
		this.q = q;
	}
	
	public void list() {
		if(null == getT1()){
			// 构建查询条件
			setT1(new Supplier());
		}
		// 按名称模糊查询
		getT1().setName(q);
		super.list();

	}
	
	/**
	 * 导出数据
	 */
	public void export(){
		String filename = "";
		if(null == getT1()){
			filename = "没有传t1.type的参数.xls";
		}else if(Supplier.TYPE_SUPPLIER.equals(getT1().getType())){
			filename = "供应商.xls";
		}else if(Supplier.TYPE_CUSTOMER.equals(getT1().getType())){
			filename = "客户.xls";
		}else{
			filename="url中没有传type的值.xls";
		}
		try {
			// 先打散转成原始的字节数组，以ISO-8859-1编码重组成字符, 原因：浏览器读取响应头用户的编码是ISO-8859-1
			filename = new String(filename.getBytes(),"ISO-8859-1");
			HttpServletResponse response = ServletActionContext.getResponse();
			// 写响应头
			response.setHeader("Content-Disposition", "attachement;filename=" + filename);
			supplierBiz.export(response.getOutputStream(), getT1());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private File file; // form表里的name
	private String fileFileName; // 文件名
	private String fileContentType;// 文件类型
	
	/**
	 * 导入数据
	 */
	public void doImport(){
		// 文件类型校验
		if(!"application/vnd.ms-excel".equals(fileContentType)){
			if(!fileFileName.endsWith(".xls")){
				WebUtil.ajaxReturn(false, "文件格式不正确!");
				return;
			}
		}
		
		try {
			supplierBiz.doImport(new FileInputStream(file));
			WebUtil.ajaxReturn(true, "导入成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "导入失败");
		}
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

}
