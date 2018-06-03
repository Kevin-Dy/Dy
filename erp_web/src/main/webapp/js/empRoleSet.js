$(function(){
	// 用户列表
	$('#grid').datagrid({
		url : 'emp_list.action',
		singleSelect : true,
		columns : [ [
			{field:'uuid',title:'编号',width:100},
		    {field:'name',title:'名称',width:100}
		] ],
		onClickRow:function(rowIndex, rowData){
			// 用户编号
			var empuuid = rowData.uuid;
			$('#btnSave').show();
			$('#tree').tree({
				//data:[{id,text,children,checked}]
				checkbox:true,//定义是否在每一个借点之前都显示复选框
				animate:true,//定义节点在展开或折叠的时候是否显示动画效果。
				url:'emp_readEmpRoles.action?id=' + empuuid
			});
		}
	});
	
	
	
	//保存按钮
	$('#btnSave').bind('click',function(){
		// 获取用户的编号 , id
		var emp = $('#grid').datagrid('getSelected');
		if(null == emp){
			$.messager.alert('提示', '请选择用户', 'info');
			return;
		}
		var submitData = {};
		// 设置提交的数据，用户的编号
		submitData.id=emp.uuid;
		// 获取所有选中的节点，取出角色的编号
		var roles = $('#tree').tree('getChecked');
		var roleIds = [];// 所有选中的角色编号
		$.each(roles, function(i,role){
			roleIds.push(role.id);
		});
		submitData.ids = roleIds.toString();//字符串由逗号分隔，且连接起来
		
		// 提交数据
		$.ajax({
			url : 'emp_updateEmpRoles.action',
			data : submitData, // 查询的条件,json对象
			dataType : 'json', // 把服务器响应回来的内容转成json对象
			type : 'post',//请求的方式
			success : function(rtn) {
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
	});
});