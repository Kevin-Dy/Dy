package cn.itcast.erp.entity;
/**
 * 盘盈盘亏实体类
 * @author Administrator *
 */
public class Inventory {	
	private Long uuid;//编号
	private Long goodsuuid;//商品
	private Long storeuuid;//仓库
	private Long num;//数量
	private String type;//类型,1:盘盈，2：盘亏
	private java.util.Date createtime;//登记日期
	private java.util.Date checktime;//审核日期
	private Long creater;//登记人
	private Long checker;//审核人
	private String state;//状态,0:未审核，1：已审核
	private String remark;//备注
	
	private String goodsName;// 商品名称
	private String storeName;// 仓库名称
	private String checkerName;// 审核人名字
	private String createrName;// 登记人名字
	/**
	 * 登记状态：未审核
	 */
	public static final String STATE_CREATE = "0";

	/**
	 * 登记状态：已审核
	 */
	public static final String STATE_CHECK = "1";

	public Long getUuid() {		
		return uuid;
	}
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public Long getGoodsuuid() {		
		return goodsuuid;
	}
	public void setGoodsuuid(Long goodsuuid) {
		this.goodsuuid = goodsuuid;
	}
	public Long getStoreuuid() {		
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
	public Long getNum() {		
		return num;
	}
	public void setNum(Long num) {
		this.num = num;
	}
	public String getType() {		
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public java.util.Date getCreatetime() {		
		return createtime;
	}
	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}
	public java.util.Date getChecktime() {		
		return checktime;
	}
	public void setChecktime(java.util.Date checktime) {
		this.checktime = checktime;
	}
	public Long getCreater() {		
		return creater;
	}
	public void setCreater(Long creater) {
		this.creater = creater;
	}
	public Long getChecker() {		
		return checker;
	}
	public void setChecker(Long checker) {
		this.checker = checker;
	}
	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRemark() {		
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getCheckerName() {
		return checkerName;
	}
	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}
	public String getCreaterName() {
		return createrName;
	}
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

}
