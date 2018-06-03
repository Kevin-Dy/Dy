package cn.itcast.erp.biz;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 通用业务逻辑接口
 * @author Administrator
 *
 * @param <T>
 */
public interface IBaseBiz<T> {
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	List<T> getList(T t1,T t2,Object param);
	
	/**
	 * 分页查询
	 * @param t1
	 * @param t2
	 * @param param
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	List<T> getListByPage(T t1,T t2,Object param,int firstResult, int maxResults);
	
	/**
	 * 计算总记录数
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	public long getCount(T t1,T t2,Object param);
	
	/**
	 * 新增
	 * @param t
	 */
	void add(T t);
	
	/**
	 * 删除
	 */
	void delete(Long uuid);
	
	/**
	 * 通过编号查询对象
	 * @param uuid
	 * @return
	 */
	T get(Long uuid);
	
	/**
	 * 通过编号查询对象
	 * @param uuid
	 * @return
	 */
	T get(String uuid);
	
	/**
	 * 更新
	 */
	void update(T t);
	/**
	 * 导出
	 * @param os 输出流
	 * @param t 查询条件
	 * @param sheetName 工作表名
	 * @param headerNames 表头列名
	 * @param valueNames 表数据对应的对象属性名
	 * @throws Exception
	 */
	void export(OutputStream os,T t,String sheetName,String[] headerNames,String[] valueNames) throws Exception;

	/**
	 * 导入
	 * @param is 输入流
	 * @param sheetName 校验工作表名
	 * @param valueNames 表数据对应的对象属性名
	 * @param isUniqueIndex 校验记录是否唯一的属性名在valueNames对应索引
	 * @throws Exception
	 */
	void doImport(InputStream is, String sheetName, String[] valueNames, int isUniqueIndex) throws Exception;

}
