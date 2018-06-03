
//区分业务的变量
var oper = Request.oper;
// 订单的类型 1: 采购订单， 2: 销售订单
var type = Request.type;

$(function(){
	var btnText = "退货登记";
	var columns = getGridColumns();
	var title = type==2?"销售退货列表":"采购退货列表";
	// 订单列表的配置信息
	var gridCfg = {
			title : title,
			singleSelect : true,
			pagination : true,
			columns : columns,
			onDblClickRow:function(rowIndex, rowData){
				//alert(JSON.stringify(rowData));
				// 打开详情的窗口
				$('#ordersDlg').dialog('open');
				
				//回显数据
				$('#uuid').html(rowData.uuid);
				$('#supplierName').html(rowData.supplierName);
				$('#state').html(formatReturnState(rowData.state));
				$('#createrName').html(rowData.createrName);
				$('#checkerName').html(rowData.checkerName);
				$('#starterName').html(rowData.starterName);
				$('#enderName').html(rowData.enderName);
				$('#createtime').html(formatDate(rowData.createtime));
				$('#checktime').html(formatDate(rowData.checktime));
				$('#starttime').html(formatDate(rowData.starttime));
				$('#endtime').html(formatDate(rowData.endtime));
				//运单号
				$('#waybillsn').html(rowData.waybillsn);
				
				$('#itemgrid').datagrid('loadData',rowData.returnOrderDetails);
				
			}
		};
	
	//???如何区分当前要做的业务是查询还是审核
	var url = '';
	if(oper == 'doCheck'){
		url = 'returnorders_listByPage.action?t1.state=0&t1.type='+type;
	}
	if(oper == 'doStart'){
		url = 'returnorders_listByPage.action?t1.state=1&t1.type='+type;
	}
	if(oper == 'doInStore'){
		url = 'returnorders_listByPage.action?type='+type;
	}
	if(oper == 'orders'){
		url = 'returnorders_listByPage.action?t1.type=' + type;
	}
	if(oper == 'doOutStore'){
		url = 'returnorders_listByPage.action?t1.state=0&t1.type='+type;
	}
	if(oper == 'myorders'){
		url = 'returnorders_myListByPage.action?t1.type=' + type;
		
		// 如何判断
		if(type == 2){
			btnText = "销售退货登记";
		}
		// 添加采购申请的按钮
		gridCfg.toolbar = [{
			text:btnText,
			iconCls:'icon-add',
			handler:function(){
				// 弹出采购申请的窗口
				//alert('弹出采购申请的窗口');
				$('#addOrdersDlg').dialog('open');
			}
		}];
	}
	// 设置订单列表查询的url
	gridCfg.url = url;
	
	$('#grid').datagrid(gridCfg);
	
	//订单详情的窗口
	initOrdersDlg();
	
	// 订单详情中的订单明细表格
	initDetailGrid();
	
	// 入出库窗口
	$('#itemDlg').dialog({
		title : type==2?'入库':'出库',
		width : 300,
		height : 200,
		closed : true,
		modal : true,
		buttons : [ {
			text : type==2?'入库':'出库',
			iconCls : 'icon-save',
			handler : doInOutStore
		} ]
	});
	
	
	// 采购申请窗口初始化
	$('#addOrdersDlg').dialog({
		title : btnText,
		width : 700,
		height : 400,
		closed : true,
		modal : true
	});
	
	$('#addSupplierName').html(type==1?"供应商":"客户");
	
	// 物流详情窗口
	$('#waybilldetailDlg').dialog({
		title : '物流详情',
		width : 500,
		height : 300,
		closed : true,
		modal : true
	});
});

/**
 * 订单详情的窗口的所有操作
 */
function initOrdersDlg(){
	//查询，不需要按钮
	//if(审核业务时){
		//窗口加按钮
	//}
	// 订单详情窗口配置信息
	var ordersDlgCfg = {
			title : '订单详情',
			width : 700,
			height : 400,
			closed : true,
			modal : true
		};
	
	//顶部工具栏  按钮数组
	var ordersDlgCfgToolbar = [];
	if(oper == 'doCheck'){
		// 审核, push给数组添加一个元素
		ordersDlgCfgToolbar.push({
			text:'审核',
			iconCls:'icon-search',
			handler:doCheck
		});
	}
	
	if(oper == 'doStart'){
		// 确认, push给数组添加一个元素
		ordersDlgCfgToolbar.push({
			text:'确认',
			iconCls:'icon-ok',
			handler:doStart
		});
	}
	
	// 添加导出按钮
	ordersDlgCfgToolbar.push({
		text:'导出',
		iconCls:'icon-excel',
		handler:function(){
			$.download('returnOrders_exportById.action',{id:$('#uuid').html()});
		}
	});
	ordersDlgCfgToolbar.push({
		text:'物流详情',
		iconCls:'icon-search',
		handler:function(){
			//  判断是否有运单号
			if(!$('#waybillsn').html()){
				$.messager.alert('提示', "没有运单号，没有物流信息", 'info');
				return;
			}
			
			// 打开物流详情窗口
			$('#waybilldetailDlg').dialog('open');
			$('#waybilldetailGrid').datagrid({
				title : '路径信息列表',
				url : "returnOrders_waybilldetailList.action?waybillsn=" + $('#waybillsn').html(),
				columns : [[
		            {field:'exedate',title:'执行日期',width:80},
		  		    {field:'exetime',title:'执行时间',width:80},
		  		    {field:'info',title:'执行信息',width:200}        
				]],
				singleSelect : true,
				pagination : true
			});
		}
	});
	
	// 设置顶部工具栏
	if(ordersDlgCfgToolbar.length > 0){
		ordersDlgCfg.toolbar = ordersDlgCfgToolbar;
	}
	$('#ordersDlg').dialog(ordersDlgCfg);
}

/**
 * 订单详情中的订单明细表格
 */
function initDetailGrid(){
	// 商品明细列表配置
	var itemGridCfg = {
			title : '商品列表',
			columns : [[
				{field:'uuid',title:'编号',width:60},
				{field:'goodsuuid',title:'商品编号',width:80},
				{field:'goodsname',title:'商品名称',width:100},
				{field:'price',title:'价格',width:60},
				{field:'num',title:'数量',width:60},
				{field:'money',title:'金额',width:100},
				{field:'state',title:'状态',width:100,formatter:formatDetailInState}
			]],
			singleSelect : true
		};
	// 入库操作时 需要双击事件
	if(oper == 'doInStore' || oper == 'doOutStore'){
		itemGridCfg.onDblClickRow = function(rowIndex, rowData){
			// 打开入库窗口
			$('#itemDlg').dialog('open');
			
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#goodsname').html(rowData.goodsname);
			$('#num').html(rowData.num);
			// 明细的编号
			$('#id').val(rowData.uuid);
			
		}
	}
	$('#itemgrid').datagrid(itemGridCfg);
}


/**
 * 订单审核
 */
function doCheck(){
	$.messager.confirm('确认','确认要审核吗？',function(yes){
		if(yes){
			$.ajax({
				url : 'returnorders_doCheck.action',
				data : {id:$('#uuid').html()},
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					// 提示操作结果
					$.messager.alert('提示', rtn.message, 'info',function() {
						if (rtn.success) {
							// 关闭窗口
							$('#ordersDlg').dialog('close');
							// 刷新未审核列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 订单确认
 */
function doStart(){
	$.messager.confirm('确认','确定要确认吗？',function(yes){
		if(yes){
			$.ajax({
				url : 'orders_doStart.action',
				data : {id:$('#uuid').html()},
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					// 提示操作结果
					$.messager.alert('提示', rtn.message, 'info',function() {
						if (rtn.success) {
							// 关闭窗口
							$('#ordersDlg').dialog('close');
							// 刷新未审核列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 入出库
 */
function doInOutStore(){
	var message = "确定要出库吗?";
	var submitUrl = 'returnorderdetail_doOutStore.action';
	if(type == 2){
		message = "确定要入库吗?";
		submitUrl = 'returnorderdetail_doInStore.action';
	}
	$.messager.confirm('确认',message,function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			$.ajax({
				url : submitUrl,
				data : submitData,
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					// 提示操作结果
					$.messager.alert('提示', rtn.message, 'info', function() {
						if (rtn.success) {
							// 关闭入库的窗口
							$('#itemDlg').dialog('close');
							// 更新明细的状态
							var row = $('#itemgrid').datagrid('getSelected');
							// 设置状态为已入库
							row.state = '1';
							
							// 刷新
							var data = $('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data);
							
							// 判断是否所有明细都完成入库
							var flag = true;
							for(var i = 0; i < data.rows.length; i++){
								row = data.rows[i];
								if(row.state * 1 == 0){
									//还有明细没有入库
									flag = false;
									break;
								}
							}
							
							// 所有明细都入库
							if(flag == true){
								// 关闭详情窗口
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
 * 获取订单列表的列
 * @returns {Array}
 */
function getGridColumns(){
	if(type == 1){
		//采购的列
		return [[
		    {field:'uuid',title:'编号',width:60},
		    {field:'createtime',title:'生成日期',width:100,formatter:formatDate},
		    {field:'checktime',title:'审核日期',width:100,formatter:formatDate},
		    {field:'starttime',title:'确认日期',width:100,formatter:formatDate},
		    {field:'endtime',title:'入库日期',width:100,formatter:formatDate},
		    {field:'createrName',title:'下单员',width:80},
		    {field:'checkerName',title:'审核员',width:80},
		    {field:'starterName',title:'采购员',width:80},
		    {field:'enderName',title:'库管员',width:80},
		    {field:'supplierName',title:'供应商',width:80},
		    {field:'totalmoney',title:'合计金额',width:80},
		    {field:'state',title:'状态',width:60,formatter:formatReturnState},
		    {field:'waybillsn',title:'运单号',width:100},
			]];
	}
	if(type == 2){
		//销售的列
		return [[
            {field:'uuid',title:'编号',width:60},
            {field:'createtime',title:'录入日期',width:100,formatter:formatDate},
            {field:'checktime',title:'审核日期',width:100,formatter:formatDate},
            {field:'endtime',title:'入库日期',width:100,formatter:formatDate},
            {field:'createrName',title:'下单员',width:80},
            {field:'checkerName',title:'审核员',width:80},
            {field:'enderName',title:'库管员',width:80},
            {field:'supplierName',title:'客户',width:80},
            {field:'totalmoney',title:'总金额',width:80},
            {field:'state',title:'订单状态',width:60,formatter:formatReturnState},
            {field:'waybillsn',title:'运单号',width:100},
			]];
	}
}


