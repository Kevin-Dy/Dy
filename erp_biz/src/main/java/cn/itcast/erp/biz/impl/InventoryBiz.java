package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IInventoryDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.Const;
/**
 * 盘盈盘亏业务逻辑类
 * @author Administrator
 *
 */
public class InventoryBiz extends BaseBiz<Inventory> implements IInventoryBiz {

	private IInventoryDao inventoryDao;
	private IStoredetailDao storedetailDao;
	private IEmpDao empDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	private IStoreoperDao storeoperDao;
	@Override
	/*
	 * 盘盈盘亏审核
	 * (non-Javadoc)
	 * @see cn.itcast.erp.biz.IInventoryBiz#doCheck(java.lang.Long, java.lang.Long)
	 */
	public void doCheck(Long inventoryuuid, Long empuuid) {
		
		//更新盘盈盘亏记录
		Inventory inventory = inventoryDao.get(inventoryuuid);
		String state = inventory.getState();
		if(Const.INVENTORY_CHECKED.equals(state)){
			throw new ErpException("该单已审核,请勿重复审核");
		}
		inventory.setChecker(empuuid);
		inventory.setChecktime(new Date());
		inventory.setState("1");
		
		//更新库存数量
		Long storeuuid = inventory.getStoreuuid();
		Long goodsuuid = inventory.getGoodsuuid();
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(goodsuuid);
		storedetail.setStoreuuid(storeuuid);
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
		Storedetail storedetail2 = list.get(0);
		if(Const.INVENTORY_OVER.equals(inventory.getType())){
			storedetail2.setNum(storedetail2.getNum()+inventory.getNum());
		}
		if(Const.INVENTORY_LOSS.equals(inventory.getType())){
			storedetail2.setNum(storedetail2.getNum()-inventory.getNum());
		}
		
	}

	/**
	 * 新增
	 */
	@Override
	public void add(Inventory inventory) {
		//登记日期
		inventory.setCreatetime(new Date());
		//状态
		inventory.setState(Inventory.STATE_CREATE);
		super.add(inventory);
	}
    
	/**
	 * 盘盈盘亏登记查询
	 */
	@Override
	public List<Inventory> getListByPage(Inventory t1, Inventory t2, Object param, int firstResult, int maxResults) {
		List<Inventory> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		
		for (Inventory inventory : list) {
			if (null != inventory.getCreater()) {
				inventory.setCreaterName(empDao.get(inventory.getCreater()).getName());
			}
			if (null != inventory.getChecker()) {
				inventory.setCheckerName(empDao.get(inventory.getChecker()).getName());
			}
			if (null != inventory.getStoreuuid()) {
				inventory.setStoreName(storeDao.get(inventory.getStoreuuid()).getName());
			}
			if (null != inventory.getGoodsuuid()) {
				inventory.setGoodsName(goodsDao.get(inventory.getGoodsuuid()).getName());
			}
		}
		
		return list;
	}
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setInventoryDao(IInventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
		super.setBaseDao(this.inventoryDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}
	
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}
	
	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public IStoreoperDao getStoreoperDao() {
		return storeoperDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}
	
}
