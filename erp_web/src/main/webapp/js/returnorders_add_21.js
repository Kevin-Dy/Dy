// 保存当前编辑行的下标
var existEditIndex = -1;

$(function(){
	$('#ordersgrid').datagrid({
		columns:[[
			{field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{disabled:true}}},
			{field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
				url:'goods_list.action',valueField:'name',textField:'name',
				onSelect:function(goods){// 在用户选择列表项的时候触发
					//alert(JSON.stringify(goods));
					// 商品编号
					var goodsuuid = goods.uuid;
					// 进货价
					var price = goods.inprice;
					if(type == 2){
						// 销售价格
						price = goods.outprice;
					}
					// 获取编辑器
					var goodsuuidEditor = $('#ordersgrid').datagrid('getEditor',{index:existEditIndex, field:'goodsuuid'});
					// goodsuuidEditor.target => 输入框 input 标签
					//alert($(goodsuuidEditor.target));
					$(goodsuuidEditor.target).val(goodsuuid);
					// 价格编辑器
					var priceEditor = getEditor('price');
					// 设置价格
					$(priceEditor.target).val(price);
					
					var numEditor = getEditor('num');
					$(numEditor.target).select();
					// 再次计算金额
					calc();
					// 重新计算合计金额
					sum();
				}
			}}},
			{field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{disabled:true}}},
			{field:'num',title:'数量',width:100,editor:{type:'numberbox',options:{min:0}}},
			{field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{disabled:true,precision:2}}},
			{field:'-',title:'操作',width:100,formatter: function(value,row,index){
				if(row.num == '合计'){
					return;
				} 
				var oper = '<a href="javascript:void(0)" onclick="deleteRow(' + index + ')">删除</a>';
				return oper;
			}}
		]],
		singleSelect:true,
		showFooter:true, // 显示行脚
		toolbar: [{
			text:'新增',
			iconCls: 'icon-add',
			handler: function(){
				// 存在正在的编辑行
				if(existEditIndex > -1){
					// 关闭当前正在编辑的行
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				
				// 往最后添加一行
				$('#ordersgrid').datagrid('appendRow',{
					num: 0,
					money: 0
				});
				
				//获取所有的行
				var rows = $('#ordersgrid').datagrid('getRows');
				// 新增这一行的下标
				var index = rows.length - 1;
				// 开启编辑行
				$('#ordersgrid').datagrid('beginEdit',index);
				
				// 设置当前编辑行的下标
				existEditIndex = index;
				
				// 绑定按键的弹起事件
				bindGridEvent();
			}
		},'-',{
			text:'提交',
			iconCls: 'icon-save',
			handler: function(){
				// 存在正在的编辑行
				if(existEditIndex > -1){
					// 关闭当前正在编辑的行
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}				
				
				// 获取表数据
				var submitData = $('#orderForm').serializeJSON();
				if(submitData['t.supplieruuid'] == ''){
					$.messager.alert('提示', "请选择供应商", 'info');
					return;
				}
				// 获取所有行[]
				var rows = $('#ordersgrid').datagrid('getRows');
				// 把数组转成json格式字符串
				var jsonString = JSON.stringify(rows);
				// 提交的是字符串
				submitData.json = jsonString;
				// type，静态url中的值,代表订单的类型
				submitData['t.type'] = type;
				// type = 1,1, 2,2
				// 提交
				$.ajax({
					url : 'returnorders_add.action',
					data : submitData, // 查询的条件,json对象
					dataType : 'json', // 把服务器响应回来的内容转成json对象
					type : 'post',//请求的方式
					success : function(rtn) {
						$.messager.alert('提示', rtn.message, 'info',function(){
							if(rtn.success){
								// 清空供应商
								$('#supplier').combogrid('clear');
								
								//loadData total:0,rows:[]
								$('#ordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});
								
								// 关闭申请窗口
								$('#addOrdersDlg').dialog('close');
								// 刷新订单列表
								$('#grid').datagrid('reload');
							}
						});
					}
				});
				
			}
		}],
		onClickRow:function(rowIndex, rowData){
			//在用户点击一行的时候触发，参数包括：
			//rowIndex：点击的行的索引值，该索引值从0开始。
			//rowData：对应于点击行的记录。
			//alert(JSON.stringify(rowData));
			
			// 存在正在的编辑行
			if(existEditIndex > -1){
				// 关闭当前正在编辑的行
				$('#ordersgrid').datagrid('endEdit',existEditIndex);
			}
			// 当前编辑的行变成点击的这一行的下标
			existEditIndex = rowIndex;
			// 开启编辑行
			$('#ordersgrid').datagrid('beginEdit',existEditIndex);
			
			// 绑定按键的弹起事件
			bindGridEvent();
		}
	});
	
	
	
	// 给行脚赋值，合计
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:0}]);
	
	// 动态显示 供应商或客户
	var supplierName = "";
	supplierName = type == 1? "供应商":"客户";
	$('#addOrdersSupplier').html(supplierName);
	
	// 供应商下拉表格
	$('#supplier').combogrid({    
	    panelWidth:750,
	    mode:'remote',
	    idField:'uuid',// valueField, 提交的内容
	    textField:'name',  // 显示的文本  
	    url:'supplier_list.action?t1.type=' + type,    
	    columns:[[    
			{field:'uuid',title:'编号',width:100},
			{field:'name',title:'名称',width:100},
			{field:'address',title:'联系地址',width:100},
			{field:'contact',title:'联系人',width:100},
			{field:'tele',title:'联系电话',width:100},
			{field:'email',title:'邮件地址',width:100}
	    ]]    
	}); 

});

/**
 * 获取编辑器
 * @param _field
 * @returns
 */
function getEditor(_field){
	return $('#ordersgrid').datagrid('getEditor',{index:existEditIndex, field:_field});
}

/**
 * 计算金额
 */
function calc(){
	// 数量编辑器
	var numEditor = getEditor('num');
	// 数量
	var num = $(numEditor.target).val();
	
	// 价格编辑器
	var priceEditor = getEditor('price');
	// 价格
	var price = $(priceEditor.target).val();
	
	var money = num * price;
	// toFixed 保留小数后几位
	money = money.toFixed(2);
	
	// 金额编辑器
	var moneyEditor = getEditor('money');
	
	$(moneyEditor.target).val(money);
	
	// 让金额进入到datagrid的数据源中
	// 获取所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	rows[existEditIndex].money = money;
}

/**
 * 绑定事件
 */
function bindGridEvent(){
	// 数量编辑器
	var numEditor = getEditor('num');
	$(numEditor.target).bind('keyup',function(){
		calc();
		sum();
	});
}

/**
 * 合计金额
 */
function sum(){
	// 获取所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	var totalMoney = 0;
	// 循环每一行，累计金额
	$.each(rows,function(i,row){
		totalMoney += row.money * 1;
	});
	// 展示出来
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:totalMoney.toFixed(2)}]);
}

/**
 * 删除行
 * @param index
 */
function deleteRow(index){
	
	// 存在正在的编辑行
	if(existEditIndex > -1){
		// 关闭当前正在编辑的行
		$('#ordersgrid').datagrid('endEdit',existEditIndex);
	}
	
	$('#ordersgrid').datagrid('deleteRow',index);
	
	var data = $('#ordersgrid').datagrid('getData');
	$('#ordersgrid').datagrid('loadData',data);
	
	// 刷新后，不存在编辑的行
	existEditIndex = -1;
	
	// 重新计算合计金额
	sum();
}



