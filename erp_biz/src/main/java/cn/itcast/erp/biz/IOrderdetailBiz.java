package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail>{

	/**
	 * 入库
	 * @param uuid 明细的编号
	 * @param empuuid 员工编号
	 * @param storeuuid 仓库编号
	 */
	void doInStore(Long uuid, Long empuuid, Long storeuuid);
	
	/**
	 * 出库
	 * @param uuid 明细的编号
	 * @param empuuid 员工编号
	 * @param storeuuid 仓库编号
	 */
	void doOutStore(Long uuid, Long empuuid, Long storeuuid);
}

