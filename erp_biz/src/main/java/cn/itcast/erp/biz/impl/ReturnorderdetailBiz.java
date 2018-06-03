package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.dao.IReturnorderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
/**
 * 退货订单明细业务逻辑类
 * @author Administrator
 *
 */
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {

	private IReturnorderdetailDao returnorderdetailDao;
    private IStoredetailDao storedetailDao;
    private IStoreoperDao storeoperDao;
    private ISupplierDao supplierDao;
    private IWaybillWs waybillWs;

	public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
		this.returnorderdetailDao = returnorderdetailDao;
		super.setBaseDao(this.returnorderdetailDao);
	}

	@Override
	public void doOutReturns(Long id, Long storeuuid, Long uuid) {
        //判断是否出库
        Returnorderdetail returnorderdetail = returnorderdetailDao.get(id);
        if (Returnorderdetail.STATE_OUT.equals(returnorderdetail.getState())) {
            throw new ErpException("商品已出库,请勿重新操作");
        }

        // ------------------ 设置订单明细表---------------------------//
        //设置出库时间
        returnorderdetail.setEndtime(new Date());
        //设置出库员
        returnorderdetail.setEnder(uuid);
        //设置仓库
        returnorderdetail.setStoreuuid(storeuuid);
        //设置状态为出库
        returnorderdetail.setState(Returnorderdetail.STATE_OUT);

        // ------------------- 设置仓库表-----------------------------//
        Storedetail storedetail = new Storedetail();
        storedetail.setGoodsuuid(returnorderdetail.getGoodsuuid());
        storedetail.setStoreuuid(storeuuid);
        List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
        //判断是否存在库存
        Long num = -1L;
        if (list != null && list.size() > 0) {
            num = list.get(0).getNum() - returnorderdetail.getNum();
        }
        if (num >= 0) {
            //将仓库的商品总数重新设值
            list.get(0).setNum(num);
        } else {
            throw new ErpException("库存不足,出库失败");
        }

        // ------------------- 设置日志表-----------------------------//
        Storeoper storeoper = new Storeoper();
        //设置操作时间
        storeoper.setOpertime(returnorderdetail.getEndtime());
        //操作商品
        storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
        //操作仓库
        storeoper.setStoreuuid(storeuuid);
        //操作人
        storeoper.setEmpuuid(uuid);
        //操作数量
        storeoper.setNum(returnorderdetail.getNum());
        //操作类型为出库
        storeoper.setType(Returnorders.STATE_OUT);
        storeoperDao.add(storeoper);
        // ------------------- 设置订单表-----------------------------//
        Returnorders returnorders = returnorderdetail.getReturnorders();
        Returnorderdetail rd = new Returnorderdetail();
        rd.setReturnorders(returnorders);
        //设置未出库
        rd.setState(Returnorderdetail.STATE_NOTOUT);
        //根据订单号,状态为未出库查询
        long count = returnorderdetailDao.getCount(rd, null, null);
        //判断是否还有未出库的明细
        if (count == 0) {
            //订单设为出库
            returnorders.setState(Returnorders.STATE_OUT);
            //出库员
            returnorders.setEnder(uuid);
            //出库时间
            returnorders.setEndtime(returnorderdetail.getEndtime());
            //根据订单获得客户信息:为了得到客户名字,发货地址,电话,
            Supplier supplier = supplierDao.get(returnorders.getSupplieruuid());
           /* //添加运单,获得运单号
            Long waybillsn = waybillWs.addWaybill(1L, supplier.getAddress(), supplier.getName(), supplier.getTele(), "--");
            //设置订单的运单号
            returnorders.setWaybillsn(waybillsn);*/
        }
    }

	@Override
	public void doInReturns(long id, Long storeuuid, Long uuid) {
		Returnorderdetail returnorderdetail = returnorderdetailDao.get(id);
        if (Returnorderdetail.STATE_IN.equals(returnorderdetail.getState())) {
            throw new ErpException("商品已入库,请勿重复操作");
        }
        // ----------------- 设置订单明细表--------------------------//
        //设置入库时间
        returnorderdetail.setEndtime(new Date());
        //设置入库员
        returnorderdetail.setEnder(uuid);
        //设置状态
        returnorderdetail.setState("2");
        //设置仓库
        returnorderdetail.setStoreuuid(storeuuid);

        // ----------------- 设置仓库表-----------------------------//
        Storedetail storedetail = new Storedetail();
        storedetail.setGoodsuuid(returnorderdetail.getGoodsuuid());
        storedetail.setStoreuuid(returnorderdetail.getStoreuuid());
        List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
        //是否存在库存信息
        if (list != null && list.size() > 0) {
            //存在库存信息,在原有基础上加
            list.get(0).setNum(list.get(0).getNum() + returnorderdetail.getNum());
        } else {
            storedetail.setNum(returnorderdetail.getNum());
            storedetailDao.add(storedetail);
        }
        // ----------------- 设置日志表-----------------------------//
        Storeoper storeoper = new Storeoper();
        //设置商品入库员
        storeoper.setEmpuuid(uuid);
        //设置入库商品
        storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
        //设置商品入库数量
        storeoper.setNum(returnorderdetail.getNum());
        //设置商品入库时间
        storeoper.setOpertime(returnorderdetail.getEndtime());
        //设置商品入库仓库
        storeoper.setStoreuuid(storeuuid);
        //设置操作类型为入库
        storeoper.setType(Storeoper.TYPE_IN);
        storeoperDao.add(storeoper);
        // ----------------- 设置订单表-----------------------------//
        Returnorders returnorders = returnorderdetail.getReturnorders();
        //获得订单明细
        Returnorderdetail rd = new Returnorderdetail();
        rd.setReturnorders(returnorders);
        rd.setState(Returnorderdetail.STATE_NOT_IN);
        long count = returnorderdetailDao.getCount(rd, null, null);
        if (count == 0) {
            //设置状态为入库
            returnorders.setState(Returnorders.STATE_END);
            //设置入库时间
            returnorders.setEndtime(returnorderdetail.getEndtime());
            //入库员
            returnorders.setEnder(uuid);
        }
	}
//----------------------------以下是get set-----------------------------//
	public IStoredetailDao getStoredetailDao() {
		return storedetailDao;
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public IStoreoperDao getStoreoperDao() {
		return storeoperDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	public ISupplierDao getSupplierDao() {
		return supplierDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public IWaybillWs getWaybillWs() {
		return waybillWs;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public IReturnorderdetailDao getReturnorderdetailDao() {
		return returnorderdetailDao;
	}
	
	
}
