package com.bingdeng.tool.excel.v2;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Fran
 * @Date: 2020/7/7
 * @Desc:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
//@ColumnWidth(40)
public class EasyExcelUser {
    @ColumnWidth(15)
    @ExcelProperty(value = "用户名")
    private String username;
    @ColumnWidth(25)
    @ExcelProperty(value = "出生日期")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    @ColumnWidth(10)
    @ExcelProperty("年龄")
    private Integer age;
    @ColumnWidth(40)
    @ExcelProperty(value = "地址")
    private String address;
    @ColumnWidth(15)
    @ExcelProperty(value = "财富")
    private Double money;
    @ColumnWidth(50)
    @ExcelProperty(value = "备注")
    private String remarks;
    @ColumnWidth(50)
    @ExcelProperty(value = "财富2")
    private BigDecimal money2;
}
