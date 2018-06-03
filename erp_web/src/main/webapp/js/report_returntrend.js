$(function(){
	var date = new Date();
	var year = date.getFullYear();
	$('#year').combobox('setValue',year);
	//加载表格数据
	$('#grid').datagrid({
		url:'report_trendReportReturn.action',
		queryParams:{year:year},// 查询请求时，加上这个参数
		columns:[[
			{field:'name',title:'商品类型',width:60},
	  		{field:'m1',title:'一月',width:35},
			{field:'m2',title:'二月',width:35},
			{field:'m3',title:'三月',width:35},
			{field:'m4',title:'四月',width:35},
			{field:'m5',title:'五月',width:35},
			{field:'m6',title:'六月',width:35},
			{field:'m7',title:'七月',width:35},
			{field:'m8',title:'八月',width:35},
			{field:'m9',title:'九月',width:35},
			{field:'m10',title:'十月',width:35},
			{field:'m11',title:'十一月',width:40},
			{field:'m12',title:'十二月',width:40}
		]],
		singleSelect: true,
		onLoadSuccess:function(data){
			show(data.rows);
		}
	});

	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	
   
});

/**
 * 显示图
 * @param _data
 */

function show(_data) {
	var months = [];
	for(var i = 1; i <=12; i++){
		months.push(i + "月");
	}
	$('#chart').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: $('#year').combobox('getValue') + '年度销售退货趋势图'
        },
        subtitle: {
            text: 'Source: 黑马小组'
        },
        xAxis: {
            categories: months,
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: 'RMB (￥)'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>￥ {point.y:.1f} </b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: _data,
        credits: {
        	enabled: true,
        	href: 'http://www.itheima.com',
        	text: "黑马小组"
        }
    });
}