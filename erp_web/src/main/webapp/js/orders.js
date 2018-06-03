// oper是url中的一个参数, 是用来标识当前所做的业务，如：doCheck=审核业务, doStart确认
var oper = Request.oper
// 订单类型的标志, type=1代表采购， type=2代表销售订单
var type = Request.type * 1;

$(function(){
	
	var url = "orders_listByPage.action?t1.type=1"; // 订单列表只能列出采购订单
	// 审核业务
	if(oper == 'doCheck'){
		url = "orders_listByPage.action?t1.state=0&t1.type=1";
	}
	// 确认业务
	if(oper == 'doStart'){
		url = "orders_listByPage.action?t1.state=1&t1.type=1";
	}
	
	// 入库业务
	if(oper == 'doInStore'){
		url = "orders_listByPage.action?t1.state=2&t1.type=1";
	}
	
	if(oper == 'orders'){
		// 查询业务
		url = "orders_listByPage.action?t1.type=" + type;
	}
	// 销售出库
	if(oper == 'doOutStore'){
		// 列出未出库的销售订单
		url = "orders_listByPage.action?t1.state=0&t1.type=" + type;
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
		url = "orders_myListByPage.action?t1.type=" + type;
		gridTitle = "我的" + (type==1?"采购":"销售") + gridTitle;
		// 订单列表的顶部工具栏
		gridCfg.toolbar = [];
		gridCfg.toolbar.push(
			{
				text : type==1?'采购申请':'销售订单录入',
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
	
	// 入库的窗口
	$('#itemDlg').dialog({
		title : type==1?'入库':'出库',
		width : 300,
		height : 200,
		closed : true,
		modal : true,
		buttons:[
	         {
	        	 text:type==1?'入库':'出库',
	        	 iconCls:'icon-save',
	        	 handler:doInOutStore
	         }
		]
	});
	
	// 采购申请窗口
	$('#addOrdersDlg').dialog({
		title:type==1?'采购申请':'销售订单录入',
		width:700,
		height:400,
		closed:true,
		modal:true
	});
	
	// 物流详情窗口
	$('#waybillDlg').dialog({
		title:"物流详情",
		width:500,
		height:300,
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

	if(oper == 'doCheck'){
		// 添加审核按钮
		ordersDlgToolbar.push({
			text:'审核',
			iconCls:'icon-search',
			handler:doCheck
		});
	}
	if(oper == 'doStart'){
		// 添加确认按钮
		ordersDlgToolbar.push({
			text:'确认',
			iconCls:'icon-search',
			handler:doStart
		});
	}
	
	ordersDlgToolbar.push({
		text: '导出',
		iconCls: 'icon-excel',
		handler: function(){
			var formData = {id:$('#uuid').html()};
			$.download("orders_exportById.action",formData);
		}
	});
	ordersDlgToolbar.push({
		text: '物流详情',
		iconCls: 'icon-search',
		handler: function(){
			var waybillsn = $('#waybillsn').html();
			if(!waybillsn){
				$.messager.alert('提示', "当前没有物流信息", 'info');
			}else{
				$('#waybillDlg').dialog('open');
				$('#waybillGrid').datagrid({
					url : 'orders_waybilldetailList.action?waybillsn=' + $('#waybillsn').html(),
					pagination : true,
					singleSelect : true,
					columns : [ [
						{field:'exedate',title:'执行日期',width:100},
						{field:'exetime',title:'执行时间',width:100},
						{field:'info',title:'执行信息',width:100}
					] ]
				});
			}
		}
	});
	
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
	
	if(oper == 'doInStore' || oper == "doOutStore"){
		// 入库业务，添加双击行事件，弹出入库的窗口
		itemGridCfg.onDblClickRow = function(rowIndex, rowData){
			// 明细的编号
			$('#itemuuid').val(rowData.uuid);
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#goodsname').html(rowData.goodsname);
			$('#num').html(rowData.num);
			// 弹出入库的窗口
			$('#itemDlg').dialog('open');
		}
	}
	
	
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
			case 1: return '已审核';
			case 2: return '已确认';
			case 3: return '已入库';
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
 * 审核
 */
function doCheck(){
	$.messager.confirm('确认','确认要审核吗?',function(yes){
		if(yes){
			// 点了确定按钮
			$.ajax({
				url : 'orders_doCheck.action',
				data : {id:$('#uuid').html()}, // 查询的条件,json对象
				dataType : 'json', // 把服务器响应回来的内容转成json对象
				type : 'post',//请求的方式
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							// 关闭订单详情窗口
							$('#ordersDlg').dialog('close');
							// 刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 确认
 */
function doStart(){
	$.messager.confirm('确认','确定要确认吗?',function(yes){
		if(yes){
			// 点了确定按钮
			$.ajax({
				url : 'orders_doStart.action',
				data : {id:$('#uuid').html()}, // 查询的条件,json对象
				dataType : 'json', // 把服务器响应回来的内容转成json对象
				type : 'post',//请求的方式
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							// 关闭订单详情窗口
							$('#ordersDlg').dialog('close');
							// 刷新订单列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 出入库
 */
function doInOutStore(){
	var msg = type==1?'确认要入库吗？':'确认要出库吗？';
	$.messager.confirm('确认',msg,function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			var url = "";
			if(type == 1){
				// 采购入库
				url = 'orderdetail_doInStore.action';
			}
			if(type == 2){
				// 销售出库
				url = 'orderdetail_doOutStore.action';
			}
			$.ajax({
				url : url,
				data : submitData, // 查询的条件,json对象
				dataType : 'json', // 把服务器响应回来的内容转成json对象
				type : 'post',//请求的方式
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info',function(){
						if(rtn.success){
							// 关闭入库的窗口
							$('#itemDlg').dialog('close');
							// 更新明细的状态, 返回第一个被选中的行或如果没有选中的行则返回null
							var row = $('#itemgrid').datagrid('getSelected');
							row.state='1';
							// 使用状态生效，手式刷新
							var data = $('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data);
							// 判断是否所有的明细完成入库, 循环明细的列表的所有行，查看是否存在状态为0的明细
							var flag = true; // 假设 所有的明细都 入库
							$.each(data.rows,function(i,r){
								if(r.state * 1 == 0){
									// 标识存在 未入库
									flag = false;
									return false;// for break;
								}
							});
							if(flag){
								// 关闭详情的窗口
								$('#ordersDlg').dialog('close');
								// 刷新订单列表
								$('#grid').datagrid('reload');
							}
						}
					});
				}
			});
		}
	});
}

/**
 * 获取列的定义
 */
function getColumns(){
	if(type == 1){
		// 采购订单的列
		return [[
					{field:'uuid',title:'编号',width:100},
					{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
					{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
					{field:'starttime',title:'确认日期',width:100,formatter:formatDate},
					{field:'endtime',title:'入库日期',width:100,formatter:formatDate},
					{field:'createrName',title:'下单员',width:100},
					{field:'checkerName',title:'审核员',width:100},
					{field:'starterName',title:'采购员',width:100},
					{field:'enderName',title:'库管员',width:100},
					{field:'supplierName',title:'供应商',width:100},
					{field:'totalmoney',title:'合计金额',width:100},
					{field:'state',title:'状态',width:100,formatter:formatState},
					{field:'waybillsn',title:'运单号',width:100}
				]];
	}
	if(type == 2){
		// 销售订单的列
		return [[
					{field:'uuid',title:'编号',width:100},
					{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
					{field:'endtime',title:'出库日期',width:100,formatter:formatDate},
					{field:'createrName',title:'下单员',width:100},
					{field:'enderName',title:'库管员',width:100},
					{field:'supplierName',title:'客户',width:100},
					{field:'totalmoney',title:'合计金额',width:100},
					{field:'state',title:'状态',width:100,formatter:formatState},
					{field:'waybillsn',title:'运单号',width:100}
				]];
	}
}

function gridDblClickRow(rowIndex, rowData){
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
	// 运单号
	$('#waybillsn').html(rowData.waybillsn);
	
	//弹出窗口
	$('#ordersDlg').dialog('open');
	
	//加载商品 列表数据
	$('#itemgrid').datagrid('loadData',rowData.orderDetails);
}
