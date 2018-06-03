// oper是url中的一个参数, 是用来标识当前所做的业务，如：doCheck=审核业务, doStart确认
var oper = Request.oper
// 订单类型的标志, type=1代表采购， type=2代表销售订单
var type = Request.type * 1;

$(function(){
	//销售退货申请,销售type=2
	//http://localhost:8080/erp/returnorders.html?oper=myorders&type=2
	//select * from returnorders;
	//select * from returnorderdetail;
	
	var url = "returnorders_listByPage.action?t1.type=1"; // 订单列表只能列出采购订单
	
	
	if(oper == 'orders'){
		// 查询业务
		url = "returnorders_listByPage.action?t1.type=" + type;
	}
	

	var gridTitle = "订单列表";
	//var columns = getColumns();
	// 订单列表配置信息
	var gridCfg = {
			pagination:true,
			singleSelect:true,
			columns:getColumns(),// columns:属性，接收的类型是数组, 方法名() 执行方法, getColumns()返回数组
			onDblClickRow:gridDblClickRow// onDblClickRow:事件，接收类型 方法(fn),gridDblClickRow没有括弧，它是一方法变量
		};
	// 我的订单
	if(oper == 'myorders'){
		
		url = "returnorders_myListByPage.action?t1.type=" + type;
		gridTitle = "我的" + (type==1?"采购退货":"销售退货") + gridTitle;
		// 订单列表的顶部工具栏
		gridCfg.toolbar = [];
		gridCfg.toolbar.push(
			{
				text : type==1?'采购退货申请':'销售订单退货录入',
				iconCls : 'icon-add',
				handler : function() {
					// 弹出采购申请的窗口
					$('#addOrdersDlg').dialog('open');
				}
			}		
		);
	}
	// 订单列表的标题
	gridCfg.title=gridTitle;
	// 设置订单的url
	gridCfg.url = url;
	// 订单
	$('#grid').datagrid(gridCfg);
	
	//订单详情窗口
	initOrdersDlg();
	
	
	// 采购申请窗口
	$('#addOrdersDlg').dialog({
		title:type==1?'采购退货申请':'销售订单退货录入',
		width:700,
		height:400,
		closed:true,
		modal:true
	});
	
	
	
});

/** 
 * 订单详情窗口所有的内容与操作都在这个方法中完成
 */
function initOrdersDlg(){
	// 订单详情窗口的配置信息
	var ordersDlgCfg = {
			title:'订单详情',
			width:700,
			height:340,
			closed:true,
			modal:true
		};
	var ordersDlgToolbar = new Array();//[];

	
	
	if(ordersDlgToolbar.length > 0){
		// 有按钮,动态添加顶部工具栏
		ordersDlgCfg.toolbar = ordersDlgToolbar;
	}
	
	$('#ordersDlg').dialog(ordersDlgCfg);
	
	// 订单明细列表的配置信息
	var itemGridCfg = {
			title:'商品列表',// 标题
			singleSelect:true, // 一定要加上
			columns:[[
				{field:'uuid',title:'编号',width:100},
				{field:'goodsuuid',title:'商品编号',width:100},
				{field:'goodsname',title:'商品名称',width:100},
				{field:'price',title:'价格',width:100},
				{field:'num',title:'数量',width:100},
				{field:'money',title:'金额',width:80},
				{field:'state',title:'状态',width:60,formatter:formatDetailState}
			]]
		};
	
	
	
	// 订单明细
	$('#itemgrid').datagrid(itemGridCfg);
}

/**
 * 日期格式化器
 * @param value
 * @returns
 */
function formatDate(value){
	if(value){
		return new Date(value).Format("yyyy-MM-dd");
	}
}

/**
 * 订单状态格式化器
 * @param value
 * @returns {String}
 */
function formatState(value){
	//采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；销售：0:未出库 1:已出库
	if(type == 1){
		switch(value * 1){
			case 0: return '未审核';
			case 1: return '已出库';
			default: return '';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0: return '未审核';
			case 1: return '已审核';
			case 2: return '已入库';
			default: return '';
		}
	}
	
}

/**
 * 订单明细的状态格式化器
 * @param value
 */
function formatDetailState(value){
	//采购：0=未入库，1=已入库  销售：0=未出库，1=已出库
	if(type == 1){
		switch(value * 1){
			case 0: return '未入库';
			case 1: return '已入库';
			default: return '';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0: return '未出库';
			case 1: return '已出库';
			default: return '';
		}
	}	
}


/**
 * 获取列的定义
 */
function getColumns(){
	if(type == 1){
		// 采购订单的列
		return [[
					{field:'uuid',title:'编号',width:100},
					{field:'createtime',title:'录入日期',width:100,formatter:formatDate},
					{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
					{field:'endtime',title:'出库日期',width:100,formatter:formatDate},
					{field:'createrName',title:'下单员',width:100},
					{field:'checkerName',title:'审查员',width:100},
					{field:'enderName',title:'库管员',width:100},
					{field:'supplierName',title:'供应商',width:100},
					{field:'totalmoney',title:'总金额',width:100},
					{field:'state',title:'订单状态',width:100,formatter:formatState},
					{field:'waybillsn',title:'运单号',width:100}
				]];
	}
	if(type == 2){
		// 销售订单的列
		return [[
					{field:'uuid',title:'编号',width:100},
					{field:'createtime',title:'录入日期',width:100,formatter:formatDate},
					{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
					{field:'endtime',title:'入库日期',width:100,formatter:formatDate},
					{field:'createrName',title:'下单员',width:100},
					{field:'checkerName',title:'审查员',width:100},
					{field:'enderName',title:'库管员',width:100},
					{field:'supplierName',title:'客户',width:100},
					{field:'totalmoney',title:'总金额',width:100},
					{field:'state',title:'订单状态',width:100,formatter:formatState},
					{field:'waybillsn',title:'运单号',width:100}
				]];
	}
}

function gridDblClickRow(rowIndex, rowData){
	if(rowData.type==1){
		$('#sname').html("供应商");
	}
	if(rowData.type==2){
		$('#sname').html("客户");
	}
//		在用户双击一行的时候触发，参数包括：
//		rowIndex：点击的行的索引值，该索引值从0开始。
//		rowData：对应于点击行的记录。
		//alert(JSON.stringify(rowData));
	
	$('#uuid').html(rowData.uuid);
	$('#supplierName').html(rowData.supplierName);
	$('#state').html(formatState(rowData.state));
	$('#createrName').html(rowData.createrName);
	$('#checkerName').html(rowData.checkerName);
	$('#starterName').html(rowData.starterName);
	$('#enderName').html(rowData.enderName);
	$('#createtime').html(formatDate(rowData.createtime));
	$('#checktime').html(formatDate(rowData.checktime));
	$('#starttime').html(formatDate(rowData.starttime));
	$('#endtime').html(formatDate(rowData.endtime));
	$('#waybillsn').html(rowData.waybillsn);
	
	
	//加载商品 列表数据
	$('#itemgrid').datagrid('loadData',rowData.orderDetails);
}
