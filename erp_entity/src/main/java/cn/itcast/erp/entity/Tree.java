package cn.itcast.erp.entity;

import java.util.List;

/**
 * jeasyui中树的节点数据
 *
 */
public class Tree {

	private String id; // 菜单编号
	private String text; // 菜单的名称
	private boolean checked; // 是否选中
	private List<Tree> children; // 子节点

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<Tree> getChildren() {
		return children;
	}

	public void setChildren(List<Tree> children) {
		this.children = children;
	}
}
