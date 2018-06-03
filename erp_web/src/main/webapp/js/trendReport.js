$(function(){
	// 获取当前日期
	var date = new Date();
	var year = date.getFullYear();
	$('#year').combobox('setValue',year);
	
	$('#grid').datagrid({
		url : 'report_trendReport.action',
		singleSelect : true,
		queryParams:{year:year},//在请求远程数据的时候发送额外的参数。
		columns : [ [
		     {field:'name',width:'100',title:'月份'},
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
		$('#grid').datagrid('load',formData);
	});
	
	
	
});

function showChart(_data){
	var months = [];
	for(var i = 1; i <= 12; i++){
		months.push(i+"月");
	}
	$('#chart').highcharts({
        chart: {
            type: 'line'
        },
        title: {
            text: $('#year').combobox('getValue') + '年度销售趋势图'
        },
        subtitle: {
            text: 'Source: itheima.com'
        },
        xAxis: {
            categories: months
        },
        yAxis: {
            title: {
                text: 'RMB (￥)'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true
                },
                enableMouseTracking: false
            }
        },
        series: [{
            name: '销售额',
            data: _data
        }],
        credits: {
        	enabled: true,
        	href: "http://www.itheima.com",
        	text: 'itheima.com'
         }
    });
}