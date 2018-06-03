$(function(){
	$('#grid').datagrid({
		url : 'storedetail_storealertList.action',
		pagination : true,
		singleSelect : true,
		columns : [ [
		    {field:'uuid',title:'商品编号',width:100},
		    {field:'name',title:'商品名称',width:100},
		    {field:'storenum',title:'库存数量',width:100},
		    {field:'outnum',title:'待发货数量',width:100}
		] ],
		toolbar:[
		    {
				text : '发送预警邮件',
				iconCls : 'icon-alert',
				handler : function() {
					$.ajax({
						url : 'storedetail_sendStorealertMail.action',
						dataType : 'json', // 把服务器响应回来的内容转成json对象
						type : 'post',//请求的方式
						success : function(rtn) {
							$.messager.alert('提示', rtn.message, 'info');
						}
					});
				}
			}
		]
	});
});