$(function(){
	$('#grid').datagrid({
		url:'emp_listByPage.action',
		columns:[[
  		    {field:'uuid',title:'编号',width:100},
  		    {field:'username',title:'登陆名',width:100},
  		    {field:'name',title:'真实姓名',width:100},
  		    {field:'gender',title:'性别',width:100,formatter:function(value){
  		    	// value * 1把字符串转成数值
  		    	if(value * 1 == 1){
  		    		return '男';
  		    	}
  		    	if(value * 1 == 0){
  		    		return '女';
  		    	}
  		    	return '';
  		    }},
  		    {field:'email',title:'邮件地址',width:100},
  		    {field:'tele',title:'联系电话',width:100},
  		    {field:'address',title:'联系地址',width:100},
  		    {field:'birthday',title:'出生年月日',width:100,formatter:formatDate},
  		    {field:'dep',title:'部门',width:100,formatter:function(value){
  		    	if(value){
  		    		//value: {name: "管理员组", tele: "000000", uuid: 1}
  		    		return value.name;
  		    	}
  		    }},

			{field:'-',title:'操作',width:200,formatter: function(value,row,index){
				var oper = "<a href=\"javascript:void(0)\" onclick=\"updatePwd_reset(" + row.uuid + ')">重置密码</a>';
				return oper;
			}}
		]],
		singleSelect: true,
		pagination: true
	});
	
	// 重置密码窗口
	$('#editDlg').dialog({
		title:'重置密码',
		height:120,
		width:260,
		modal:true,
		closed:true,
		buttons:[
		    {
		    	text:'保存',
		    	iconCls:'icon-save',
		    	handler:function(){
		    		var submitData = $('#editForm').serializeJSON();
		    		$.ajax({
						url : 'emp_updatePwd_reset.action',
						data : submitData, // 查询的条件,json对象
						dataType : 'json', // 把服务器响应回来的内容转成json对象
						type : 'post',//请求的方式
						success : function(rtn) {
							$.messager.alert('提示', rtn.message, 'info',function(){
								if(rtn.success){
									// 关闭重置窗口
									$('#editDlg').dialog('close');
								}
							});
						}
					});
		    	}
		    }         
		]
	});
});


/**
 * 弹出重置密码窗口
 */
function updatePwd_reset(uuid){
	$('#editDlg').dialog('open');
	// 设置员工编号，放入表单中
	$('#id').val(uuid);
}

function formatDate(value){
	if(value){ // value有值，不为null, 不为undefined
		return new Date(value).Format("yyyy-MM-dd");
	}
}