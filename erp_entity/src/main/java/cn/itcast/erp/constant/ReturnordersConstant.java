package cn.itcast.erp.constant;

public  class ReturnordersConstant {
	/**
	 * 订单类型：采购
	 */
	public static final String TYPE_IN = "1";

	/**
	 * 订单类型：销售
	 */
	public static final String TYPE_OUT = "2";

	/**
	 * 采购订单状态：未审核
	 */
	public static final String STATE_CREATE = "0";

	/**
	 * 采购订单状态：已审核
	 */
	public static final String STATE_CHECK = "1";

	/**
	 * 采购订单状态：已确认
	 */
	public static final String STATE_START = "2";

	/**
	 * 采购订单状态：已入库
	 */
	public static final String STATE_END = "3";

	/**
	 * 销售订单状态：未出库
	 */
	public static final String STATE_NOT_OUT = "0";

	/**
	 * 销售订单状态：已出库
	 */
	public static final String STATE_OUT = "1";
}
