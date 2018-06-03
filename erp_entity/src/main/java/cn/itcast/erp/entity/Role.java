package cn.itcast.erp.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 角色实体类
 * @author Administrator *
 */
public class Role {	
	private Long uuid;//编号
	private String name;//名称
	/*
	@ManyToMany
	@JoinTable(name="emp_role", joinColumns={
			@JoinColumn(referencedColumnName="uuid",name="roleuuid")
	},inverseJoinColumns={
			@JoinColumn(referencedColumnName="uuid",name="empuuid")
	})*/
	@JSONField(serialize=false)
	private List<Emp> emps;// 拥有这个角色的所有用户
	@JSONField(serialize=false)
	private List<Menu> menus;// 这个角色所拥有权限合集

	public Long getUuid() {		
		return uuid;
	}
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public String getName() {		
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Emp> getEmps() {
		return emps;
	}
	public void setEmps(List<Emp> emps) {
		this.emps = emps;
	}
	public List<Menu> getMenus() {
		return menus;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

}
