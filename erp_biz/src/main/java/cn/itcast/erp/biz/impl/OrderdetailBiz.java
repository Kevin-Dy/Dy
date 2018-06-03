package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	private IWaybillWs waybillWs;
	private ISupplierDao supplierDao;
	
	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}

	@Override
	@RequiresPermissions("采购入库")
	public void doInStore(Long uuid, Long empuuid, Long storeuuid) {
//		明细表(orderdetail)：明细的编号 进入持久化状态
		Orderdetail od = orderdetailDao.get(uuid);
		// 判断的状态
		if(!Orderdetail.STATE_NOT_IN.equals(od.getState())){
			throw new ErpException("该明细已经入库了，不能重复入库");
		}
//		1. 结束日期		系统时间
		od.setEndtime(new Date());
//		2. 库管员		登陆用户的编号
		od.setEnder(empuuid);
//		3. 仓库编号		从前端传过来
		od.setStoreuuid(storeuuid);
//		4. 状态         已入库 1
		od.setState(Orderdetail.STATE_IN);
//		
//		库存表(storedetail), 引入storedetailDao
//		1. 判断库存信息是否存在(根据仓库编号和商品编号查询库存表), storedetailDao.getList(t1);
		Storedetail storedetail = new Storedetail();
//			仓库编号： 由前端传过来
		storedetail.setStoreuuid(storeuuid);
//			商品编号： 明细里有
		storedetail.setGoodsuuid(od.getGoodsuuid());
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
//		2. 如果存在： 更新数量累计
		if(list.size() > 0){
			// 库存信息对象 进入持久化状态
			storedetail = list.get(0);
			// 数量累加
			storedetail.setNum(storedetail.getNum() + od.getNum());
		}else{
//		3. 不存在： 插入新的库存信息
			// 设置库存的数量
			storedetail.setNum(od.getNum());
			// 插入库存信息
			storedetailDao.add(storedetail);
		}
		
//		操作记录(STOREOPER)
		Storeoper log = new Storeoper();
//		插入操作:
//			操作员工编号    登陆用户的编号
		log.setEmpuuid(empuuid);
//			操作日期		系统时间
		log.setOpertime(od.getEndtime());
//			仓库编号： 由前端传过来
		log.setStoreuuid(storeuuid);
//			商品编号： 明细里有
		log.setGoodsuuid(od.getGoodsuuid());
//			数量：	   明细里有
		log.setNum(od.getNum());
//			操作类型    1 入库
		log.setType(Storeoper.TYPE_IN);
		// 插入操作
		storeoperDao.add(log);
//		
//		订单表
//		1. 判断是否所有的明细都已经完成入库 orderdetailDao.getCount(t1);
		// 构建查询条件
		Orderdetail queryParam = new Orderdetail();
//			查询该订单下的明细(状态为0)的个数, getCount
//				条件： 订单(明细里有 orderdetail.getOrders())，明细的状态未入库 0
		queryParam.setOrders(od.getOrders());
		queryParam.setState(Orderdetail.STATE_NOT_IN);
		long count = orderdetailDao.getCount(queryParam, null, null);
//		2. 个数>0，还有明细没有入库,不需要做其它操作
//		3. 个数=0, 所有的明细都入库
		if(count == 0){
//		进入持久化状态
			Orders orders = od.getOrders();
//		orders.set
//		入库日期   系统时间
			orders.setEndtime(od.getEndtime());
//		库管员     登陆用户的编号
			orders.setEnder(empuuid);
//		状态       已入库 3
			orders.setState(Orders.STATE_END);
		}
		
	}
	
	@Override
	@RequiresPermissions("销售订单出库")
	public void doOutStore(Long uuid, Long empuuid, Long storeuuid) {
//		明细表(orderdetail)：明细的编号 进出持久化状态
		Orderdetail od = orderdetailDao.get(uuid);
		// 判断的状态
		if(!Orderdetail.STATE_NOT_OUT.equals(od.getState())){
			throw new ErpException("该明细已经出库了，不能重复出库");
		}
//		1. 结束日期		系统时间
		od.setEndtime(new Date());
//		2. 库管员		登陆用户的编号
		od.setEnder(empuuid);
//		3. 仓库编号		从前端传过来
		od.setStoreuuid(storeuuid);
//		4. 状态         已出库 1
		od.setState(Orderdetail.STATE_OUT);
//		
//		库存表(storedetail), 引出storedetailDao
//		1. 判断库存信息是否存在(根据仓库编号和商品编号查询库存表), storedetailDao.getList(t1);
		Storedetail storedetail = new Storedetail();
//			仓库编号： 由前端传过来
		storedetail.setStoreuuid(storeuuid);
//			商品编号： 明细里有
		storedetail.setGoodsuuid(od.getGoodsuuid());
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
//		2. 如果存在： 更新数量累计
		if(list.size() > 0){
			// 库存信息对象 进出持久化状态
			storedetail = list.get(0);
			// 判断数量是否足够
			long num = storedetail.getNum() - od.getNum();
			if(num >= 0){
				storedetail.setNum(num);
			}else{
				throw new ErpException("库存不足");
			}
			
		}else{
//		3. 不存在：
			throw new ErpException("库存不足");
		}
		
//		操作记录(STOREOPER)
		Storeoper log = new Storeoper();
//		插出操作:
//			操作员工编号    登陆用户的编号
		log.setEmpuuid(empuuid);
//			操作日期		系统时间
		log.setOpertime(od.getEndtime());
//			仓库编号： 由前端传过来
		log.setStoreuuid(storeuuid);
//			商品编号： 明细里有
		log.setGoodsuuid(od.getGoodsuuid());
//			数量：	   明细里有
		log.setNum(od.getNum());
//			操作类型   2 出库
		log.setType(Storeoper.TYPE_OUT);
		// 插出操作
		storeoperDao.add(log);
//		
//		订单表
//		1. 判断是否所有的明细都已经完成出库 orderdetailDao.getCount(t1);
		// 构建查询条件
		Orderdetail queryParam = new Orderdetail();
//			查询该订单下的明细(状态为0)的个数, getCount
//				条件： 订单(明细里有 orderdetail.getOrders())，明细的状态未出库 0
		queryParam.setOrders(od.getOrders());
		queryParam.setState(Orderdetail.STATE_NOT_OUT);
		long count = orderdetailDao.getCount(queryParam, null, null);
//		2. 个数>0，还有明细没有出库,不需要做其它操作
//		3. 个数=0, 所有的明细都出库
		if(count == 0){
//		进出持久化状态
			Orders orders = od.getOrders();
//		orders.set
//		出库日期   系统时间
			orders.setEndtime(od.getEndtime());
//		库管员     登陆用户的编号
			orders.setEnder(empuuid);
//		状态       已出库 1
			orders.setState(Orders.STATE_OUT);
		//Long userId, String toaddress, String addresse, String tele, String info
			// 获取客户信息
			Supplier customer = supplierDao.get(orders.getSupplieruuid());
			// 调用bos系统在线预约下单, 获得运单号
			Long waybillsn = waybillWs.addWaybill(1l, customer.getAddress(), customer.getContact(), customer.getTele(), "--");
			// 更新订单的运单号
			orders.setWaybillsn(waybillsn);
		}
		
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	
}
