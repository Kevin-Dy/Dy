package cn.itcast.erp.action;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.ExcelBean;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.WebUtil;

/**
 * 员工Action 
 * @author Administrator
 *
 */
public class EmpAction extends BaseAction<Emp> {

	private IEmpBiz empBiz;
	private String oldPwd; // 原密码
	private String newPwd; // 新密码

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
		super.setBaseBiz(this.empBiz);
	}
	
	/**
	 * 修改密码
	 */
	public void updatePwd(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "请先登陆!");
			return;
		}
		try {
			empBiz.updatePwd(oldPwd, newPwd, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "修改密码成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "修改密码失败");
		}
	}
	
	/**
	 * 重置密码
	 */
	public void updatePwd_reset(){
		try {
			Long uuid = getId();// 前端传过来的员工编号
			empBiz.updatePwd_reset(newPwd, uuid);
			WebUtil.ajaxReturn(true, "重置密码成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "重置密码失败");
		}
	}
	
	/**
	 * 读取用户角色设置信息
	 */
	public void readEmpRoles(){
		List<Tree> list = empBiz.readEmpRoles(getId());
		WebUtil.write(list);
	}
	
	private String ids; // 角色的编号，多个用逗号分割
	
	/**
	 * 更新用户的角色
	 */
	public void updateEmpRoles(){
		try {
			empBiz.updateEmpRoles(getId(), ids);
			WebUtil.ajaxReturn(true, "保存成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "保存失败");
		}
	}
	
	@Override
	public ExcelBean getExcelBean() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String filename="员工表"+simpleDateFormat.format(new Date())+".xls";
		String sheetName="员工";
		String[] headerNames={"编号","登陆名","真实姓名","性别","邮件地址","联系电话","联系地址","出生年月日","部门"};
		String[] valueNamesExport={"uuid","username","name","gender","email","tele","address","birthday","depName"};
		String[] valueNamesImport={"uuid","username","name","gender","email","tele","address","birthday","depName"};
		int isUniqueIndex=1;
		ExcelBean excelBean = new ExcelBean();
		excelBean.setFilename(filename);
		excelBean.setSheetName(sheetName);
		excelBean.setHeaderNames(headerNames);
		excelBean.setValueNamesExport(valueNamesExport);
		excelBean.setValueNamesImport(valueNamesImport);
		excelBean.setIsUniqueIndex(isUniqueIndex);
		return excelBean;
	}
	

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
