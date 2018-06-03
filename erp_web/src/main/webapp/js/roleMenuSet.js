$(function(){
	// 角色列表
	$('#grid').datagrid({
		url : 'role_list.action',
		singleSelect : true,
		columns : [ [
			{field:'uuid',title:'编号',width:100},
		    {field:'name',title:'名称',width:100}
		] ],
		onClickRow:function(rowIndex, rowData){
			// 角色编号
			var roleuuid = rowData.uuid;
			$('#btnSave').show();
			$('#tree').tree({
				//data:[{id,text,children,checked}]
				checkbox:true,//定义是否在每一个借点之前都显示复选框
				animate:true,//定义节点在展开或折叠的时候是否显示动画效果。
				url:'role_readRoleMenus.action?id=' + roleuuid
			});
		}
	});
	
	
	
	//保存按钮
	$('#btnSave').bind('click',function(){
		// 获取角色的编号 , id
		var role = $('#grid').datagrid('getSelected');
		if(null == role){
			$.messager.alert('提示', '请选择角色', 'info');
			return;
		}
		var submitData = {};
		// 设置提交的数据，角色的编号
		submitData.id=role.uuid;
		// 获取所有选中的节点，取出菜单的编号
		var menus = $('#tree').tree('getChecked');
		var menuIds = [];// 所有选中的菜单编号
		$.each(menus, function(i,menu){
			menuIds.push(menu.id);
		});
		submitData.ids = menuIds.toString();//字符串由逗号分隔，且连接起来
		
		// 提交数据
		$.ajax({
			url : 'role_updateRoleMenus.action',
			data : submitData, // 查询的条件,json对象
			dataType : 'json', // 把服务器响应回来的内容转成json对象
			type : 'post',//请求的方式
			success : function(rtn) {
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
	});
	
	
	
	
	
});