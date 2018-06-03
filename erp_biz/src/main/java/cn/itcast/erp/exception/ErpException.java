package cn.itcast.erp.exception;

/**
 * 自定义异常: 终止 (对 已知不符合业务逻辑的操作) 代码的继续执行
 *
 */
public class ErpException extends RuntimeException {
	private static final long serialVersionUID = 8914765131035633695L;

	public ErpException(String message){
		super(message);
	}
	
}
