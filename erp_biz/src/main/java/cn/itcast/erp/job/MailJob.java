package cn.itcast.erp.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.util.MailUtil;

/**
 * 预警邮件任务
 *
 */
public class MailJob {
	
	private IStoredetailDao storedetailDao;
	private MailUtil mailUtil;
	private String to;
	private String title;
	private String content;

	public void doJob(){
		// 查询预警商品列表, 扩展的需求：邮件发送失败，尝试3次，之后就不再发， 到了第二天尝试3次
		List<Storealert> list = storedetailDao.getStorealertList();
		if(null != list && list.size() > 0){

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
				mailUtil.sendMail(to, title.replace("[time]", sdf.format(new Date())), 
						content.replace("[count]", list.size() + ""));
				System.out.println("邮件发送成功");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
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

	public void setContent(String content) {
		this.content = content;
	}
}
