package com.hxl.inventory.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 库存汇总导出行
 */
@Data
public class DrugInventorySummaryExcelVO {

    @ExcelProperty("药品id")
    private Long drugId;

    @ExcelProperty("药品编码")
    private String drugCode;

    @ExcelProperty("药品名称")
    private String drugName;

    @ExcelProperty("规格")
    private String specification;

    @ExcelProperty("生产厂家")
    private String manufacturerName;

    @ExcelProperty("入库数量")
    private Integer inboundQuantity;

    @ExcelProperty("入库退货数量")
    private Integer inboundReturnQuantity;

    @ExcelProperty("出库数量")
    private Integer outboundQuantity;

    @ExcelProperty("出库退货数量")
    private Integer outboundReturnQuantity;

    @ExcelProperty("当前库存")
    private Integer currentInventory;
}
