package cn.itcast.erp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.itcast.erp.biz.IBaseBiz;
import cn.itcast.erp.entity.ExcelBean;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

/**
 * 通用Action类
 * @author Administrator
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class BaseAction<T> {

	private IBaseBiz<T> baseBiz;
	
	public void setBaseBiz(IBaseBiz<T> baseBiz) {
		this.baseBiz = baseBiz;
	}
	
	//属性驱动:条件查询
	private T t1;
	private T t2;
	private Object param;
	public T getT2() {
		return t2;
	}
	public void setT2(T t2) {
		this.t2 = t2;
	}
	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public T getT1() {
		return t1;
	}
	public void setT1(T t1) {
		this.t1 = t1;
	}
	
	private int page;//页码
	private int rows;//每页的记录数
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	/**
	 * 条件查询
	 */
	public void list(){
		List<T> list = baseBiz.getList(t1,t2,param);
		//把部门列表转JSON字符串
		String listString = JSON.toJSONString(list);
		write(listString);
	}
	
	public void listByPage(){
		System.out.println("页码：" + page + " 记录数:" + rows);
		int firstResult = (page -1) * rows;
		List<T> list = baseBiz.getListByPage(t1,t2,param,firstResult, rows);
		long total = baseBiz.getCount(t1,t2,param);
		//{total: total, rows:[]}
		Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put("total", total);
		mapData.put("rows", list);
		//把部门列表转JSON字符串, DisableCircularReferenceDetect: 禁止循环引用
		// WriteMapNullValue: 如果属性值为空, 则输出null
		String listString = JSON.toJSONString(mapData,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue );
		write(listString);
	}
	
	/**新增，修改*/
	private T t;
	public T getT() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	/**
	 * 新增
	 * @param jsonString
	 */
	public void add(){
		//{"success":true,"message":""}
		//返回前端的JSON数据
		try {
			baseBiz.add(t);
			WebUtil.ajaxReturn(true,"新增成功");
		} catch (ErpException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "新增失败");
		}
		//write(JSON.toJSONString(rtn));
		// {}{}
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * 删除
	 * @param jsonString
	 */
	public void delete(){
		
		try {
			baseBiz.delete(id);
			ajaxReturn(true, "删除成功");
		} catch (ErpException e) {
			e.printStackTrace();
			ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false, "删除失败");
		}
	}
	
	/**
	 * 通过编辑查询对象
	 */
	public void get(){
		T t = baseBiz.get(id);
		// WithDateFormat, 如果转换的对象有日期，把日期转成指定格式的字符串
		// WriteMapNullValue: 如果属性值为空, 则输出null
		String jsonString = JSON.toJSONStringWithDateFormat(t,"yyyy-MM-dd",SerializerFeature.WriteMapNullValue);
		System.out.println("转换前：" + jsonString);
		//{"name":"管理员组","tele":"000011","uuid":1}
		String jsonStringAfter = mapData(jsonString, "t");
		System.out.println("转换后：" + jsonStringAfter);
		write(jsonStringAfter);
	}
	
	/**
	 * 修改
	 */
	public void update(){
		try {
			baseBiz.update(t);
			ajaxReturn(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false, "修改失败");
		}
	}
	
	/**
	 * //{"name":"管理员组","tele":"000011","uuid":1} 
	 * @param jsonString JSON数据字符串
	 * @param prefix 要加上的前缀
	 * @return  {"t.name":"管理员组","t.tele":"000011","t.uuid":1} 
	 */
	@SuppressWarnings("unchecked")
	public String mapData(String jsonString, String prefix){
		Map<String, Object> map = JSON.parseObject(jsonString);
		
		//存储key加上前缀后的值
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//给每key值加上前缀
//		{
//			"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
//			"t.dep":{"name":"管理员组","tele":"000000","uuid":1},
//			"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1
//		}
		for(String key : map.keySet()){
			if(map.get(key) instanceof Map){
				//"t.dep":{"name":"管理员组","tele":"000000","uuid":1}
				
				Map<String,Object> innerMap = (Map<String,Object>)map.get(key);
				for(String innerKey : innerMap.keySet()){
					// prefix + "." + key => t.dep
					String newKey = prefix + "." + key + "." + innerKey;
					// "t.dep.name":"管理员组","t.dep.tele":"000000","t.dep.uuid":1
					dataMap.put(newKey, innerMap.get(innerKey));
				}
			}else{
				dataMap.put(prefix + "." + key, map.get(key));
			}
		}
		return JSON.toJSONString(dataMap);
	}
	
	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public void ajaxReturn(boolean success, String message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		write(JSON.toJSONString(rtn));
	}
	
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出表格
	 */
	public void export(){
		ExcelBean excelBean = getExcelBean();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition", "attachment;filename="+new String(excelBean.getFilename().getBytes(),"ISO-8859-1"));
			baseBiz.export(response.getOutputStream(), getT1(),excelBean.getSheetName(),excelBean.getHeaderNames(),excelBean.getValueNamesExport());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 由子类重写
	 * @return
	 */
	public ExcelBean getExcelBean(){
		return null;
	}
	
	private File file;
	private String fileFileName;
	private String fileContentType;
	public void doImport() {
		//文件格式校验
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			if(!fileFileName.endsWith(".xls")){
			WebUtil.ajaxReturn(false, "文件格式有误");
			return;
			}
		}
		ExcelBean excelBean = getExcelBean();
		try {
			baseBiz.doImport(new FileInputStream(file), excelBean.getSheetName(), excelBean.getValueNamesImport(), excelBean.getIsUniqueIndex());
			WebUtil.ajaxReturn(true, "导入成功");
		}catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		}catch (Exception e) {
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


