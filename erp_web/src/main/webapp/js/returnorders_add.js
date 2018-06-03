//定义当前行下标
var endEditIndex=-1;
var submitData = '';
var flag=false;
$(function(){
	$("#addReturnordersgrid").datagrid({
		singleSelect:true,//单选一行
		showFooter:true,//显示行脚
	    columns:[[    
			{field:'ordersuuid',title:'订单编号',width:100,editor:{type:'numberbox',options:{
				disabled:true		//不可选中
			}}},   
	        //editor编辑器 type:字符串 options:对象
	        {field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{
	        	disabled:true		//不可选中
	        }}},    
	        {field:'goodsname',title:'商品名称',width:100,editor:{type:'textbox',options:{
	        	disabled:true		//不可选中
	        }}},    
	        {field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{
	        	precision:2,
	        	disabled:true
	        }}},   
	        {field:'num',title:'数量',width:100,editor:{type:'numberbox',options:{
	        	}}
	        },
	        {field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{
	        	precision:2,
	        	disabled:true
	        }}},    
	    	{field:'-',title:'操作',width:150,formatter: function(value,row,index){
	    		if(row.num=='合计'){
	    			return;
	    		}
	    		return  ' <a href="javascript:void(0)" onclick="del(' + index + ')">删除</a>';
				
			}}  
	    ]] ,
	    
	   toolbar: [{
			iconCls: 'icon-add',
			text:'增加',
			handler: function(){
				submitData = $('#returnordersForm').serializeJSON();
				if(submitData['t.supplieruuid']==''){
					$.messager.alert('提示',"请选择供应商"); 
					return;
				}
				var g = $('#supplier').combogrid('grid');//获取数据表格对象
				var r = g.datagrid('getSelected');		// 获取选择的行
				$("#ordersDlg").dialog('open');
				$('#ordersgrid').datagrid({    
				    url:'orders_listByPage.action?t1.state=3&t1.supplieruuid='+r.uuid,  
				    pagination:true,
				    singleSelect:true,
				    columns:[[    
				          {field:'uuid',title:'编号',width:60},
						  {field:'createtime',title:'生成日期',width:80,formatter:formatDate},
						  {field:'checktime',title:'审核日期',width:80,formatter:formatDate},
						  {field:'starttime',title:'确认日期',width:80,formatter:formatDate},
						  {field:'endtime',title:'入库日期',width:80,formatter:formatDate},
						  {field:'createrName',title:'下单员',width:80},
						  {field:'checkerName',title:'审核员',width:80},
						  {field:'starterName',title:'采购员',width:80},
						  {field:'enderName',title:'库管员',width:80},
						  {field:'supplierName',title:'供应商',width:80},
						  {field:'totalmoney',title:'合计金额',width:80},
						  {field:'state',title:'状态',width:90,formatter:formatState},   
				    ]] ,
				    onDblClickRow:gridDblClickRow
				});
			}
		},'-',{
			iconCls: 'icon-save',
			text:'提交',
			handler: function(){
				endEditor();		//关闭编辑行
				submitData = $('#returnordersForm').serializeJSON();
				if(submitData['t.supplieruuid']==''){
					$.messager.alert('提示',"请选择供应商"); 
					return;
				}
				var rows=$('#addReturnordersgrid').datagrid('getRows');
				var jsonString=JSON.stringify(rows);
				submitData.json=jsonString;
				$.ajax({
					url:'returnorders_add.action?t.type=1',
					data:submitData,//查询条件,提交数据
					dataType:'json',//返回格式
					type:'post',//提交类型
					success:function(rtn){
						$.messager.confirm('提示',rtn.message,function(){  
							if(rtn.message=='请先登录'){
								location.href='login.html';
							}
							if(rtn.success){
								// 清空供应商
								$('#supplier').combogrid('clear');
								//loadData total:0,rows:[]
								$('#addReturnordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});
								$("#addReturnordersgrid").datagrid('reload');
							}
						}); 
					}
				});
			}
		}],
		/**
		 * 单击行事件
		 * rowIndex:行索引 0开始
		 * rowData:行事件
		 */
		onClickRow:function(rowIndex, rowData){
			//关闭当前编辑行
			endEditor();
			//设置为当前行索引
			endEditIndex=rowIndex;
			//开启编辑行
			$('#addReturnordersgrid').datagrid('beginEdit',endEditIndex);
			bindGridEvent(rowData);
		}
	});
	//重载页脚行
	$('#addReturnordersgrid').datagrid('reloadFooter',[{num:'合计',money:0}]);
	$('#supplier').combogrid({    
	    panelWidth:650,    
	    idField:'uuid',    
	    textField:'name',    
	    url:"supplier_list.action?t1.type=1",  
	    columns:[[    
	          {field:'uuid',title:'编号',width:100},
			  {field:'name',title:'名称',width:100},
			  {field:'address',title:'联系地址',width:100},
			  {field:'contact',title:'联系人',width:100},
			  {field:'tele',title:'联系电话',width:100},
			  {field:'email',title:'邮件地址',width:100},   
	    ]],
		mode:'remote'
	});  
	/**
	 * 订单明细
	 */
	$("#ordersdetailgrid").datagrid({
		title:'订单明细',
		columns:[[
		      {field:'uuid',title:'编号',width:100},
			  {field:'goodsuuid',title:'商品编号',width:100},
			  {field:'goodsname',title:'商品名称',width:100},
			  {field:'price',title:'价格',width:100},
			  {field:'num',title:'数量',width:100},
			  {field:'money',title:'金额',width:100},
			  {field:'state',title:'状态',width:100,formatter:formatDetailState}
		]],
		singleSelect:true,
		onDblClickRow:function(rowIndex, rowData){
			//取出当前所有行
			var rows=$('#addReturnordersgrid').datagrid('getRows');
			$.each(rows,function(i,row){
				if(row.ordersuuid==rowData.uuid){
					$.messager.alert('提示','存在此订单');   
					flag=true;
					return;
				}
			});
			if(flag){
				flag=false;
				$("#ordersDlg").dialog("close");
				$("#ordersdetailDlg").dialog("close");
				return;
			}
			$("#ordersDlg").dialog("close");
			$("#ordersdetailDlg").dialog("close");
			//新建一行添加数据,在最后面,
			$('#addReturnordersgrid').datagrid('appendRow',{ordersuuid:rowData.uuid,goodsuuid:rowData.goodsuuid,
				price:rowData.price,goodsname:rowData.goodsname,num:rowData.num,money:rowData.money});
			//关闭编辑行
			endEditor();
			//获取当前行索引
			endEditIndex=$('#addReturnordersgrid').datagrid('getRows').length-1;
			//开启编辑行
			$('#addReturnordersgrid').datagrid('beginEdit',endEditIndex);
        	var numEditor = getEditor('num');			//获取数量编辑器
        	$(numEditor.target).select();				//选中指定编辑器
        	bindGridEvent(rowData);
		}
	})
	/**
	 * 订单明细
	 */
	
	/*采购订单窗口*/
	$("#ordersDlg").dialog({
		title:"采购过的订单",
		width:'1000',
		height:'300',
		closed:true,
		modal:true,
	});
	/*订单明细窗口*/
	$("#ordersdetailDlg").dialog({
		title:"订单详情",
		width:'750',
		height:'300',
		closed:true,
		modal:true,
	});
})	

	/**
	 * 获取编辑器
	 * index:下标
	 * field:列名
	 */
	function getEditor(field){
		return $('#addReturnordersgrid').datagrid('getEditor', {index:endEditIndex,field:field});
	}
	/**
	 * 关闭编辑行
	 */
	function endEditor(){
		if(endEditIndex > -1){
			$('#addReturnordersgrid').datagrid('endEdit',endEditIndex);
		}
	}
		
	/**
	 * 计算金额
	 */	
	function calc(){
		var numEditor = getEditor('num');			//获取数量编辑器
		var num = $(numEditor.target).val();		//获取数量
    	var priceEditor = getEditor('price');		//获取价格编辑器
    	var price = $(priceEditor.target).val();	//获取价格
    	var money = (num * price).toFixed(2);		//计算金额
    	var moneyEditor = getEditor('money');		//获取金额编辑器
    	$(moneyEditor.target).val(money);			//给金额赋值
    	var rows=$('#addReturnordersgrid').datagrid('getRows'); //获取所有行
    	rows[endEditIndex].money = money;			//当前编辑行价格赋值
	}
	/**
	 * 绑定输入数量时键盘弹起事件
	 */
	function bindGridEvent(rowData){
		var numEditor = getEditor('num');			//获取数量编辑器
		$(numEditor.target).bind('keyup',function(){
			if($(numEditor.target).val()>rowData.num){
        		$.messager.alert('提示','数量不能大于'+rowData.num);
        		$(numEditor.target).val(rowData.num);
        		endEditor();	//关闭编辑器才能赋值
        		$('#addReturnordersgrid').datagrid('beginEdit',endEditIndex);//开启编辑器
        		bindGridEvent(rowData);	//重写绑定
        	}
			if($(numEditor.target).val()<0){
        		$.messager.alert('提示','数量不能小于0');
        		$(numEditor.target).val(0);
        		endEditor();	//关闭编辑器才能赋值
        		$('#addReturnordersgrid').datagrid('beginEdit',endEditIndex);//开启编辑器
        		bindGridEvent(rowData);	//重写绑定
        	}
			calc();
			sum();
		});
	}
	/**
	 * 计算合计
	 */
	function sum(){
		var rows=$('#addReturnordersgrid').datagrid('getRows');	//取出当前所有行
		var totalMoney=0;
		$.each(rows,function(i,row){
			totalMoney += row.money*1;
		});
		$('#addReturnordersgrid').datagrid('reloadFooter',[{num:'合计',money:totalMoney.toFixed(2)}]);
	}
	/**
	 * 删除行
	 */
	function del(index){
		endEditor();			//关闭编辑行
		$('#addReturnordersgrid').datagrid('deleteRow',index);	//删除行
		var data=$('#addReturnordersgrid').datagrid('getData');
		$('#addReturnordersgrid').datagrid('loadData',data);
		endEditIndex=-1;
		sum();
	}
	/**
	 * 日期格式化器
	 * @param value
	 */
	function formatDate(value){
		if(value){
			return new Date(value).Format("yyyy-MM-dd");
		}
	}
	/**
	 * 订单状态
	 * 采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；销售：0:未出库 1:已出库
	 */
	function formatState(value){
		switch(value*1){
		case 0:return '未审核';
		case 1:return '已审核';
		case 2:return '已确认';
		case 3:return '已入库';
		default :return '';
		}
	
	}
	/**
	 * 明细的状态
	 * @param value
	 */
	function formatDetailState(value){
		switch(value*1){
		case 0:return '未入库';
		case 1:return '已入库';
		default :return '';
		}
	}
	/**
	 * 用户双击触发
	 */
	function gridDblClickRow(rowIndex, rowData){
		$("#ordersdetailDlg").dialog('open');
		$("#ordersdetailgrid").datagrid('loadData',rowData.orderDetails);
	}
	

