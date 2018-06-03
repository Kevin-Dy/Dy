var oper = Request.oper;
var type = Request.type;

/**
 * 主函数
 */
$(function () {
    var url = 'returnorders_listByPage.action?t1.type=1';

    /**
     * 采购退货出库
     */
    if (oper == "doOutReturns") {
        url = "returnorders_listByPage.action?t1.type=1&t1.state=1";
        document.title = "采购退货出库";
        title = "采购退货订单详情";
    }
    if (oper == "doInReturns") {
        url = "returnorders_listByPage.action?t1.type=2&t1.state=1";
        document.title = "销售退货入库";
        title = "销售退货订单详情";
    }
    $("#grid").datagrid({
        title: "采购退货出库",
        url: url,
        singleSelect: true,
        pagination: true,
        columns: getGridColumns(),
        //在用户双击一行的时候触发，参数包括：
        //rowIndex：点击的行的索引值，该索引值从0开始。
        //rowData：对应于点击行的记录。
        onDblClickRow: function (rowIndex, rowData) {
            $("#ordersDlg").dialog("open");
            //订单赋值
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
            //订单明细表加载数据
            $("#itemgrid").datagrid("loadData", rowData.returnorderdetails);

        }

    });
    //订单弹窗初始化
    initeditDlg();
    //订单详情表初始化
    initItemgrid();
    //出入库窗口初始化
    initInStoreDlg();

})

/**
 * 订单弹窗初始化
 */
function initeditDlg() {
    $('#ordersDlg').dialog({
        title: '采购退货出库',
        width: 700,
        height: 300,
        closed: true,
        cache: false,
        modal: true
    });

}

/**
 * 订单详情表初始化
 */
function initItemgrid() {
    var ItemgridCfg = {
        columns: [[
            {field: 'uuid', title: '编号', width: 90},
            {field: 'goodsuuid', title: '商品编号', width: 100},
            {field: 'goodsname', title: '商品名称', width: 100},
            {field: 'price', title: '价格', width: 100},
            {field: 'num', title: '数量', width: 80},
            {field: 'money', title: '金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: formatDetailState},
        ]],
        singleSelect: true,

    };
    if (oper == "doInReturns" || oper == "doOutReturns") {
        ItemgridCfg.onDblClickRow = function (rowIndex, rowData) {
            $("#itemDlg").dialog("open");
            $("#itemuuid").val(rowData.uuid);
            $("#goodsuuid").html(rowData.goodsuuid);
            $("#goodsname").html(rowData.goodsname);
            $("#goodsnum").html(rowData.num);
        }
    }
    $("#itemgrid").datagrid(ItemgridCfg);

}

/**
 * 出入库窗口初始化
 */
function initInStoreDlg() {
    var InStoreDlgCfgbuttons = [];
    var InStoreDlgCfg = {
        width: 300,
        height: 200,
        closed: true,
        modal: true,
    };
    if (oper == "doInReturns") {
        InStoreDlgCfg.title = "销售退货入库";
        InStoreDlgCfgbuttons.push({
            text: "入库",
            iconCls: "icon-save",
            handler: inOrOutReturns
        })
    }
    if (oper == "doOutReturns") {
        InStoreDlgCfg.title = "采购退货出库";
        InStoreDlgCfgbuttons.push({
            text: "出库",
            iconCls: "icon-save",
            handler: inOrOutReturns
        })
    }
    if (InStoreDlgCfgbuttons.length > 0) {
        InStoreDlgCfg.buttons = InStoreDlgCfgbuttons
    }
    $("#itemDlg").dialog(InStoreDlgCfg);
}

/**
 * 出入库
 */
function inOrOutReturns() {
//获得入库窗口表单数据
    var fromData = $("#itemFrom").serializeJSON();
    var url = "";
    //判断是否选择了仓库
    if(fromData.storeuuid == ''){
        $.messager.alert("提示","请选择仓库", "info");
        return;
    }
    if(type == 2){
        message = "确认入库吗?";
        url = "returnorderdetail_doInReturns.action";
    }
    if(type == 1){
        message = "确认出库吗?";
        url = "returnorderdetail_doOutReturns.action";
    }
    $.messager.confirm("提示",message, function(yes){
        if(yes){
            $.ajax({
                type: "POST",
                url: url,
                data:fromData,
                dataType:"json",
                success: function(msg){
                    $.messager.alert("提示", msg.message, "info",function(){
                        if(msg.success){
                            //关闭窗口
                            $("#itemDlg").dialog("close");
                            //修改入库明细的状态
                            $("#itemgrid").datagrid("getSelected").state=2;
                            //刷新数据页面
                            var data=$("#itemgrid").datagrid("getData");
                            $("#itemgrid").datagrid("loadData",data);
                            var flag = true;
                            $.each(data.rows,function(i,row){
                                if(row.state * 1 == 0){
                                    flag = false ;
                                    return false;
                                }
                            })
                            if(flag){
                                //关闭明细窗口
                                $("#ordersDlg").dialog("close");
                                //刷新数据
                                $("#grid").datagrid("reload");
                            }
                        }
                    })
                }
            });
        }
    })
}

/**
 * 获取订单表格列
 * @returns {Array}
 */
function getGridColumns() {
    if (type == 2) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'checktime', title: '审核日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '入库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'checkerName', title: '审核员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '客户', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: formatState},
            {field: 'waybillsn', title: '运单号', width: 100}
        ]]
    }
    if (type == 1) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'checktime', title: '审核日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '出库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'checkerName', title: '审核员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '供应商', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: formatState},
            {field: 'waybillsn', title: '运单号', width: 100}


        ]]
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
        		return "未入库";
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
 * @param value
 * @returns {String}
 */
function formatState(value) {
//	采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；销售：0:未出库 1:已出库
    if (type == 1) {
        switch (value * 1) {
            case 0:
                return "未审核";
            case 1:
                return "未出库";
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
                return "未入库";
            case 2:
                return "已入库";
            default:
                return "";
        }
    }

}