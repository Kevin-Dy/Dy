package cn.itcast.erp.biz.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.MailUtil;
/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IStoreDao storeDao;
	private IGoodsDao goodsDao;
	private MailUtil mailUtil;
	private String to;  // 收件人
	private String title;  // 邮件的标题
	private String text; // 邮件的内容
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}
	
	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult,
			int maxResults) {
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for (Storedetail sd : list) {
			// 商品名称
			sd.setGoodsName(goodsDao.get(sd.getGoodsuuid()).getName());
			// 仓库名称
			sd.setStoreName(storeDao.get(sd.getStoreuuid()).getName());
		}
		return list;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	@Override
	public List<Storealert> getStorealertList() {
		return storedetailDao.getStorealertList();
	}

	@Override
	public void sendStorealertMail() throws Exception{
		List<Storealert> list = storedetailDao.getStorealertList();
		if(null == list || list.size() == 0){
			throw new ErpException("没有需要预警的商品");
		}
		
		//有库存预警的商品，需要发邮件
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		String _title = title.replace("[time]", dateStr);
		String _content = text.replace("[count]", list.size() + "");
		mailUtil.sendMail(to, _title, _content);
		
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
