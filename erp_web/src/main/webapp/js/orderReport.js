$(function(){
	
	$('#grid').datagrid({
		url : 'report_orderReport.action',
		singleSelect : true,
		columns : [ [
		     {field:'name',width:'100',title:'商品类型'},
		     {field:'y',width:'100',title:'销售额'}
		] ],
		onLoadSuccess:function(data){
			//alert(JSON.stringify(data));
			showChart(data.rows);
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		if(formData['endDate'] != ''){
			// 有结束日期
			formData['endDate']+=" 23:59:59.999";
		}
		$('#grid').datagrid('load',formData);
	});
	
	
	
});

function showChart(_data){
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
            text: '销售统计图'
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
                    format: '{point.name}: <b>{point.percentage:.1f}%</b>'
                },
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: '百分比',
            data: _data
        }]
    });
}