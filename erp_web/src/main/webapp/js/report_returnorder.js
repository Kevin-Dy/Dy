var columns = [ [ {
	field : 'name',
	width : 100,
	title : '商品类型',
	sortable : true
}, {
	field : 'y',
	width : 100,
	title : '销售额',
	sortable : true
} ] ];

$(function() {
	// 加载表格数据
	$('#grid').datagrid({
/*		url : 'report_orderReport.action',*/
		url : 'report_reportReturn.action',
		columns : columns,
		singleSelect : true,
		onLoadSuccess : function(data) {
			// alert(JSON.stringify(data));
			showPie(data.rows);
		}

	});

	$('#searchBtn').click(function() {
		var comitData = $('#searchForm').serializeJSON();
		if (comitData.endTime != null) {
			var temp = comitData.endTime;
			comitData.endTime = temp + " 23:59:59:999";
		}
		$('#grid').datagrid('load', comitData);
	});

})
var drilldowns;
// 圆饼图形报表
function showPie(showData) {

	$('#container')
			.highcharts(
					{
						chart : {
							type : 'pie',
							
							//options3d: { enabled: true, alpha: 45, beta: 0 },
							 
							// 设置上下钻事件
							events : {
								drillup : function(e) {
									// 上钻回调事件
									//var chart = this;
									 //chart.redraw();
									//console.log(e.seriesOptions);
								},
								drilldown : function(e) {
									//alert(e.point.name);
									if (!e.seriesOptions) {
										var chart = this,
										// 请求参配置
										typeName = e.point.name;// 加载类型名
										comitData = $('#searchForm')
												.serializeJSON();// 获取查询条件
										if (comitData.endTime != null) {
											var temp = comitData.endTime;
											comitData.endTime = temp
													+ " 23:59:59:999";
										}
										comitData.typeName = typeName;
										$
												.ajax({
													type : "POST",
													url : "report_goodsTypeReport.action",
													data : comitData,
													dataType : "json",
													success : function(result) {	
													
														 chart.showLoading('加载中...');
														
														setTimeout(
																function() {
																	chart.hideLoading();
																	chart.addSeriesAsDrilldown(e.point,result);
																}, 500);
													}

												});
									}
								}
							}
						},
						title : {
							text : '商品类型销售退货报表'
						},
						tooltip : {
							pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
						},
						plotOptions : {
							pie : {
								allowPointSelect : true,
								cursor : 'pointer',
								depth : 35,
								dataLabels : {
									enabled : true,
									format : '{point.name}'
								},
								showInLegend : true
							}
						},
						series : [ {
							type : 'pie',
							name : '每类商品退货详情',
							data : showData
						} ],
						drilldown : {
							series : drilldowns
						}
					});
}







/*$(function(){
	//加载表格数据
	$('#grid').datagrid({
		url:'report_reportReturn.action',
		columns:[[
			{field:'name',title:'商品类型',width:100},
	  		{field:'y',title:'销售额',width:100}
		]],
		singleSelect: true,
		onLoadSuccess:function(data){
			//alert(JSON.stringify(data));
			showChart(data.rows);
		}
	});

	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	
   
});*/

/**
 * 显示图
 * @param _data
 */
/*function showChart(_data){
	 $('#chart').highcharts({
	        chart: {
	            type: 'pie',
	            options3d: {
	                enabled: true,
	                alpha: 45,
	                beta: 0
	            }
	        },
	        title: {
	            text: '销售退货分析图'
	        },
	        tooltip: {
	            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        plotOptions: {
	            pie: {
	                allowPointSelect: true,
	                cursor: 'pointer',
	                depth: 35,
	                dataLabels: {
	                    enabled: true,
	                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                },
	                showInLegend:true
	            }
	        },
	        series: [{
	            type: 'pie',
	            name: '百分比',
	            data: _data
	        }],
	        credits: {
	        	enabled: true,
	        	href: 'http://www.itheima.com',
	        	text: "黑马小组"
	        }
	    });
}*/