package cn.itcast.erp.action;
import org.apache.shiro.authz.UnauthorizedException;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

/**
 * 退货订单明细Action 
 * @author Administrator
 *
 */
public class ReturnorderdetailAction extends BaseAction<Returnorderdetail> {

	private IReturnorderdetailBiz returnorderdetailBiz;
    private Long storeuuid;
	
	public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
		this.returnorderdetailBiz = returnorderdetailBiz;
		super.setBaseBiz(this.returnorderdetailBiz);
	}
	/**
     * 采购订单退货出库
     */
    public void doOutReturns() {
        //判断用户是否登录
        Emp emp = WebUtil.getLoginUser();
        if (emp == null) {
            ajaxReturn(false, "未登录,请重新登录");
            return;
        }
        try {
            returnorderdetailBiz.doOutReturns(getId(), storeuuid, emp.getUuid());
            ajaxReturn(true, "出库成功");
        } catch (UnauthorizedException e) {
            ajaxReturn(false, "没有权限");
        } catch (ErpException e) {
            ajaxReturn(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ajaxReturn(false, "出库失败");
        }
    }

    /**
     * 销售订单退货入库
     */
    public void doInReturns() {
        //判断用户是否登录
        Emp emp = WebUtil.getLoginUser();
        if (emp == null) {
            ajaxReturn(false, "未登录,请重新登录");
            return;
        }
        try {
            returnorderdetailBiz.doInReturns(getId(), storeuuid, emp.getUuid());
            ajaxReturn(true, "入库成功");
        }catch (UnauthorizedException e) {
            ajaxReturn(false, "没有权限");
        } catch (ErpException e) {
            e.printStackTrace();
            ajaxReturn(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ajaxReturn(false, "入库失败");
        }
    }
    //---------------------------以下是get set------------------//
	public Long getStoreuuid() {
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
    
}
