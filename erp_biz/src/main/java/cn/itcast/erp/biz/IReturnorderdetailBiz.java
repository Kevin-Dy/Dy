package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Returnorderdetail;
/**
 * 退货订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail>{
	/**
     * 采购订单退货出库
     * @param id 订单号
     * @param storeuuid 仓库编号
     * @param uuid 库管员编号
     */
   void doOutReturns (Long id ,Long storeuuid, Long uuid);

    /**
     * 销售订单退货入库
     * @param id 订单号
     * @param storeuuid 仓库编号
     * @param uuid 库管员编号
     */
    void doInReturns(long id, Long storeuuid, Long uuid);
}

