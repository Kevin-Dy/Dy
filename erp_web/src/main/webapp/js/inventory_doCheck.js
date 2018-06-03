$(function(){
	$('#grid').datagrid({
		url : 'inventory_listByPage.action',
		pagination : true,
		singleSelect : true,
		columns : [[
		  		    {field:'uuid',title:'编号',width:100},
		  		    {field:'storeName',title:'仓库',width:100},
		  		    {field:'goodsName',title:'商品',width:100},
		  		    {field:'num',title:'数量',width:100},
		  		    {field:'type',title:'类型',width:100,formatter:formatType},
		  		    {field:'createtime',title:'登记日期',width:100,formatter:formatDate},
		  		    {field:'checktime',title:'审核日期',width:100,formatter:formatDate},
		  		    {field:'createrName',title:'登记人',width:100},
		  		    {field:'checkerName',title:'审核人',width:100},
		  		    {field:'state',title:'状态',width:100,formatter:formatState},
		  		    {field:'remark',title:'备注',width:100}
					]],
		/*data:[
		      {'uuid':12,'storename':'水果','goodsname':'猕猴桃','num':'100','type':'1','createtime':'2018-5-12','checktime':'2018-5-12','creater':'Joey','checker':'admin','state':'0'}
		      ],*/
		onDblClickRow:showInventoryDetailDlg
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	$('#btnSearch').bind('click', function() {
		// 把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load', formData);
	});
	
	//调用初始化窗口方法
	initInventoryDetailDlg();
});

//显示盘盈盘亏详情窗口
function showInventoryDetailDlg(rowIndex,rowData){

	//判断状态是否为未审核
	if(rowData.state == '0'){
		//打开窗口并显示盘盈盘亏详情
		$('#inventoryDetailDlg').dialog('open');
		$('#uuid').html(rowData.uuid);
		$('#createtime').html(formatDate(rowData.createtime));
		$('#goodsname').html(rowData.goodsName);
		$('#storename').html(rowData.storeName);
		$('#num').html(rowData.num);
		$('#type').html(rowData.type);
		$('#remark').html(rowData.remark);
	}
}

//格式化状态
function formatState(value){
	switch(value*1){
		case 0: return '未审核';
		case 1: return '已审核';
		default: return null;
	}
}

//格式化类型
function formatType(value){
	switch(value*1){
		case 1: return '盘盈';
		case 2: return '盘亏';
		default: return null;
	}
}

//格式化日期
function formatDate(value){
	
	if(value){
		return new Date(value).Format("yyyy-MM-dd");
	}
	
}

//初始化盘盈盘亏详情窗口
function initInventoryDetailDlg(){
	$('#inventoryDetailDlg').dialog({
		title:'盘点详情',
		width:250,
		height:260,
		closed:true,
		modal:true,
		buttons:[{
			text:'审核',
			iconCls:'icon-save',
			handler:function(){
				$.ajax({
					url : 'inventory_doCheck.action',
					data : {id:$('#uuid').html()}, // 查询的条件,json对象
					dataType : 'json', // 把服务器响应回来的内容转成json对象
					type : 'post',//请求的方式
					success : function(rtn) {
						$.messager.alert('提示', rtn.message, 'info',function(){
							if(rtn.success){
								// 关闭订单详情窗口
								$('#inventoryDetailDlg').dialog('close');
								// 刷新订单列表
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		}]
	});
}

