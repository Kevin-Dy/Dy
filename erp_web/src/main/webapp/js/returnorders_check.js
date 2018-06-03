var oper = Request.oper;
var type = Request.type;
/**
 * 主函数
 */
$(function() {
	var title = "采购订单详情";
	var url = 'returnorders_listByPage.action?t1.type=1';
	// 判断是否是审核
	if (oper == "doCheck") {
		if (type == 2) {
			url = 'returnorders_listByPage.action?t1.type=2&t1.state=0';
			document.title = "销售订单退货审核";
		}
		if (type == 1) {
			url = 'returnorders_listByPage.action?t1.type=1&t1.state=0';
			document.title = "采购订单退货审核";
		}

	}
	$("#grid").datagrid({
		title : "采购退货审核",
		url : url,
		singleSelect : true,
		pagination : true,
		columns : getGridColumns(),
		// 在用户双击一行的时候触发，参数包括：
		// rowIndex：点击的行的索引值，该索引值从0开始。
		// rowData：对应于点击行的记录。
		onDblClickRow : function(rowIndex, rowData) {
			$("#ordersDlg").dialog("open");
			// 订单赋值
			if (rowData.type == 1) {
				$("#sname").html("供应商");
			}
			if (rowData.type == 2) {
				$("#sname").html("客户");
			}
			$("#uuid").html(rowData.uuid);
			$("#createtime").html(formatDate(rowData.createtime));
			$("#checktime").html(formatDate(rowData.checktime));
			$("#starttime").html(formatDate(rowData.starttime));
			$("#endtime").html(formatDate(rowData.endtime));
			$("#createrName").html(rowData.createrName);
			$("#checkerName").html(rowData.checkerName);
			$("#starterName").html(rowData.starterName);
			$("#supplierName").html(rowData.supplierName);
			$("#state").html(formatState(rowData.state));
			$("#enderName").html(rowData.enderName);
			if (rowData.waybillsn) {
				$("#waybillsn").html(rowData.waybillsn);
			} else {
				$("#waybillsn").html("");
			}
			// 订单明细表加载数据
			$("#itemgrid").datagrid("loadData", rowData.returnorderdetails);

		}

	});

	/*initOrderDlg();*/
	initOrdersDlg();

})

/**
 * 初始化订单详情窗口
 */
function initOrderDlg() {
	var OrderDlgCfgtoolbar = [];
	var OrderDlgCfg = {
		title : "订单详情",
		width : 700,
		height : 340,
		closed : true,
		modal : true,
	};
	// 是否是要审核的
	if (oper == "doCheck") {
		// 要审核的添加按钮
		OrderDlgCfgtoolbar.push({
			text : "审核",
			iconCls : 'icon-search',
			handler : doCheck
		});
	}
	;

	// 判断OrderDlgCfgtoolbar里是否有内容
	if (OrderDlgCfgtoolbar.length > 0) {
		// 将工具栏添加到窗口顶部
		OrderDlgCfg.toolbar = OrderDlgCfgtoolbar;
	}

	$("#ordersDlg").dialog(OrderDlgCfg);
}

/**
 * 订单审核
 */
function doCheck() {
	// 提示确认框
	$.messager.confirm("提示", "确认要审核吗?", function(yes) {
		if (yes) {
			// 提交请求
			$.ajax({
				type : "post",
				url : "returnorders_doCheck.action?id=" + $("#uuid").html(),// 将要审核的订单id提交过去
				dataType : "json",
				success : function(rtn) {
					$.messager.alert("提示", rtn.message, "info", function() {
						if (rtn.success) {
							// 关闭窗口
							$("#ordersDlg").dialog('close');
							// 重载行。等同于'load'方法，但是它将保持在当前页
							$("#grid").datagrid("reload");
						}
					})
				}
			});
		}
	})
}

function initOrdersDlg() {
	// 订单详情窗口的配置信息
	var ordersDlgCfg = {
		title : '订单详情',
		width : 700,
		height : 340,
		closed : true,
		modal : true
	};
	var ordersDlgToolbar = new Array();// [];

	if (oper == 'doCheck') {
		// 添加审核按钮
		ordersDlgToolbar.push({
			text : '审核',
			iconCls : 'icon-search',
			handler : doCheck
		});
	}

	

	if (ordersDlgToolbar.length > 0) {
		// 有按钮,动态添加顶部工具栏
		ordersDlgCfg.toolbar = ordersDlgToolbar;
	}

	$('#ordersDlg').dialog(ordersDlgCfg);

	// 订单明细列表的配置信息
	var itemGridCfg = {
		title : '商品列表',// 标题
		singleSelect : true, // 一定要加上
		columns : [ [ 
		{field : 'uuid',title : '编号',width : 100}, 
		{field : 'goodsuuid',title : '商品编号',width : 100},
		{field : 'goodsname',title : '商品名称',width : 100},
		{field : 'price',title : '价格',width : 100}, 
		{field : 'num',title : '数量',width : 100}, 
		{field : 'money',title : '金额',width : 80}, 
		{field : 'state',title : '状态',width : 60,formatter : formatDetailState} 
		] ]
	};

	if (oper == 'doInStore' || oper == "doOutStore") {
		// 入库业务，添加双击行事件，弹出入库的窗口
		itemGridCfg.onDblClickRow = function(rowIndex, rowData) {
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
 * 订单表格素双击事件
 * 
 * @param rowIndex
 * @param rowData
 */
function getDblClickRow(rowIndex, rowData) {
	$("#ordersDlg").dialog('open');
	// 订单赋值
	if (rowData.type == 1) {
		$("#sname").html("供应商");
	}
	if (rowData.type == 2) {
		$("#sname").html("客户");
	}

	$("#uuid").html(rowData.uuid);
	$("#createtime").html(formatDate(rowData.createtime));
	$("#checktime").html(formatDate(rowData.checktime));
	$("#starttime").html(formatDate(rowData.starttime));
	$("#endtime").html(formatDate(rowData.endtime));
	$("#createrName").html(rowData.createrName);
	$("#checkerName").html(rowData.checkerName);
	$("#starterName").html(rowData.starterName);
	$("#supplierName").html(rowData.supplierName);
	$("#state").html(formatState(rowData.state));
	if (rowData.waybillsn) {
		$("#waybillsn").html(rowData.waybillsn);
	} else {
		$("#waybillsn").html("");
	}
	// 订单明细表加载数据
	$("#itemgrid").datagrid("loadData", rowData.orderdetails);
}

/**
 * 获取订单表格列
 * 
 * @returns {Array}
 */
function getGridColumns() {
	if (type == 2) {
		return [ [ 
		    {field : 'uuid',title : '编号',width : 100},
			{field : 'createtime',title : '生成日期',width : 100,formatter : formatDate},
			{field : 'checktime',title : '审核日期',width : 100,formatter : formatDate},
			{field : 'endtime',title : '入库日期',width : 100,formatter : formatDate}, 
			{field : 'createrName',title : '下单员',width : 100}, 
			{field : 'checkerName',title : '审核员',width : 100}, 
			{field : 'enderName',title : '库管员',width : 100}, 
			{field : 'supplierName',title : '客户',width : 100},
			{field : 'totalmoney',title : '合计金额',width : 100}, 
			{field : 'state',title : '状态',width : 100,formatter : formatState},
			{field : 'waybillsn',title : '运单号',width : 100} 
			] ]
	}
	if (type == 1) {
		return [ [ 
		           {field : 'uuid',title : '编号',width : 100},
		           {field : 'createtime',title : '生成日期',width : 100,formatter : formatDate}, 
		           {field : 'checktime',title : '审核日期',width : 100,formatter : formatDate},
		           {field : 'endtime',title : '出库日期',width : 100,formatter : formatDate}, 
		           {field : 'createrName',title : '下单员',width : 100}, 
		           {field : 'checkerName',title : '审核员',width : 100}, 
		           {field : 'enderName',title : '库管员',width : 100}, 
		           {field : 'supplierName',title : '供应商',width : 100}, 
		           {field : 'totalmoney',title : '合计金额',width : 100}, 
		           {field : 'state',title : '状态',width : 100,formatter : formatState},
		           {field : 'waybillsn',title : '运单号',width : 100}

		] ]
	}

}
/**
 * 订单明细状态格式化器
 * @param value
 * @returns {String}
 */
function formatDetailState(value) {
    if (type == 2) {
        switch (value * 1) {
        	case 0:
        		return "未出库";
            case 1:
                return "未入库";
            case 2:
                return "已入库";
            default:
                return "";
        }
    }
    if (type == 1) {
        switch (value * 1) {
        	case 0:
        		return "未出库";
            case 1:
                return "未出库";
            case 2:
                return "已出库";
            default:
                return "";
        }
    }
}

/**
 * 日期格式化器
 * 
 * @param value
 * @returns
 */
function formatDate(value) {
	if (value) {
		return new Date(value).Format("yyyy-MM-dd");
	}
}

/**
 * 订单状态格式化器
 * 
 * @param value
 * @returns {String}
 */
function formatState(value) {
	// 采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；销售：0:未出库 1:已出库
	if (type == 1) {
		switch (value * 1) {
		case 0:
			return "未审核";
		case 1:
			return "已审核";
		case 2:
			return "已出库";
		default:
			return "";
		}
	}
	if (type == 2) {
		switch (value * 1) {
		case 0:
			return "未审核";
		case 1:
			return "已审核";
		case 2:
			return "已入库";
		default:
			return "";
		}
	}

}