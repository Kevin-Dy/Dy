//提交的方法名称
var method = "";
var height = 200;
var listParam = "";
var saveParam = "";
$(function(){
	//加载表格数据
	$('#grid').datagrid({
		url:name + '_listByPage.action' + listParam,
		columns:columns,
		singleSelect: true,
		pagination: true,
		toolbar: [{
			text: '新增',
			iconCls: 'icon-add',
			handler: function(){
				//设置保存按钮提交的方法为add
				method = "add";
				//关闭编辑窗口
				$('#editDlg').dialog('open');
			}
		},'-',{
			text: '导出',
			iconCls: 'icon-excel',
			handler: function(){
				var formData = $('#searchForm').serializeJSON();
				$.download(name + '_export.action'+listParam,formData);
			}
		},'-',{
			text: '导入',
			iconCls: 'icon-save',
			handler: function(){
				//弹出导入窗口
				$('#importDlg').dialog('open');
			}
		}]
	});

	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});

	//初始化编辑窗口
	$('#editDlg').dialog({
		title: '编辑',//窗口标题
		width: 300,//窗口宽度
		height: height,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				// 表单验证，确保所有的输入框都有效
				if(!$('#editForm').form('validate')){
					return;
				}
				
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: name + '_' + method + '.action' + saveParam,
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
								//刷新表格
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		},{
			text:'关闭',
			iconCls:'icon-cancel',
			handler:function(){
				//关闭弹出的窗口
				$('#editDlg').dialog('close');
			}
		}]
	});
	
	var importDlg = document.getElementById('importDlg');
	if(importDlg){
		// 有导入窗口
		$('#importDlg').dialog({
			title:'导入数据',
			width:350,
			height:106,
			modal:true,
			closed:true,
			buttons:[
				{
					text : '导入',
					iconCls : 'icon-save',
					handler : function() {
						$.ajax({
							url : name+'_doImport.action',
							data : new FormData($('#importForm')[0]),// 表单数据
							dataType : 'json',
							type : 'post',
							processData:false,//data选项的数据将被处理，并转换成一个查询字符串提交, jquery处理
							contentType:false,// 告诉服务端，我的内容是字节流，不要转成字符串处理
							success : function(rtn) {
								// 提示操作结果
								$.messager.alert('提示', rtn.message, 'info',function() {
									if (rtn.success) {
										// 关闭导入窗口
										$('#importDlg').dialog('close');
										// 刷新表格数据
										$('#grid').datagrid('reload');
									}
								});
							}
						});
					}
				}
			]
		});
	}

});


/**
 * 删除
 */
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name + '_delete.action',
				data:{id:uuid},
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}

/**
 * 修改
 */
function edit(uuid){
	//弹出窗口
	$('#editDlg').dialog('open');

	//清空表单内容
	$('#editForm').form('clear');

	//设置保存按钮提交的方法为update
	method = "update";
/*$.ajax({
	url : 'dep_get.action',
	data : {id:1}, // 查询的条件,json对象
	dataType : 'json', // 把服务器响应回来的内容转成json对象
	type : 'post',//请求的方式
	success : function(rtn) {
		$('#depname').val(rtn.name);
	}
});*/
	//加载数据
	$('#editForm').form('load',name + '_get.action?id=' + uuid);
	/*
	var data = {
			"t.address":"建材城西路中腾商务大厦","t.birthday":"1949-10-01",
			"t.dep.name":"管理员组","t.dep.tele":"000000","t.dep.uuid":1,
			"t.email":"admin@itcast.cn","t.gender":1,"t.name":"超级管理员","t.tele":"12345678","t.username":"admin","t.uuid":1
		};
	//
	$('#editForm').form('load',data);*/
}