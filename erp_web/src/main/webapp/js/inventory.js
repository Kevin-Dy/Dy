//提交的方法名称
var oper = Request.oper;
var btnText = "";// 按钮的文本
var icon = ""// 图标
var name = "inventory";
var method = "";
var inventoryGridDblClickRow = function() {
}
var columns = [ [ 
                 
		{field : 'uuid',title : '编号',width : 100},
		{field : 'goodsName',title : '商品',width : 100}, 
		{field : 'storeName',title : '仓库',width : 100},
		{field : 'num',title : '数量',width : 100},
		{field : 'type',title : '类型',width : 100,formatter : formatType}, 
		{field : 'createtime',title : '登记日期',width : 100,formatter : formatDate},
		{field : 'checktime',title : '审核日期',width : 100,formatter : formatDate}, 
		{field : 'createrName',title : '登记人',width : 100},
		{field : 'checkerName',title : '审核人',width : 100},
		{field : 'state',title : '状态',width : 100,formatter : formatState},
		{field : 'remark',title : '备注',width : 100},

] ];
$(function() {
	// 加载表格数据
	$('#grid').datagrid({
		url : getinventoryGridUrl(),// 采购编辑,
		columns : columns,
		singleSelect : true,
		pagination : true,
		onDblClickRow : inventoryGridDblClickRow
	});

	// 点击查询按钮
	$('#btnSearch').bind('click', function() {
		// 把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load', formData);
	});

});
/**
 * 盘点表格的url
 * 
 * @returns {String}
 */
function getinventoryGridUrl() {
	var url = 'inventory_listByPage.action';
	switch (oper) {
	case "inventory":
		url = 'inventory_listByPage.action';
		document.title = "盘盈盘亏查询";

		break;
	default:
		break;
	}
	return url;
}

/**
 * 日期格式化器
 * 
 * @param value
 * @returns
 */
function formatDate(value) {
	if (value) {
		return new Date(value).Format('yyyy-MM-dd');
	}
	return "";
}

/**
 * 类型格式化器
 * @param value
 * @returns
 */
function formatType(value){
	if(value*1==1){
		return "盘盈";
	}
	if(value*1==2){
		return "盘亏";
	}	
}

function formatState(value){
	switch(value*1){
		case 0: return '未审核';
		case 1: return '已审核';
		default: return null;
	}
}
