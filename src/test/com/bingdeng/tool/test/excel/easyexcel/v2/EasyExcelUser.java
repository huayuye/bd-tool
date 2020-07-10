package com.bingdeng.tool.test.excel.easyexcel.v2;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Fran
 * @Date: 2020/7/10
 * @Desc:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EasyExcelUser {
    @ExcelProperty(value = "用户名")
    private String username;
    @ExcelProperty(value = "密码")
    private String password;
    @ColumnWidth(25)
    @ExcelProperty(value = "出生日期")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    @ColumnWidth(15)
    @ExcelProperty(value = "财富")
    private BigDecimal money;
}
