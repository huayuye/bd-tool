package com.bingdeng.tool.excel.v2;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: Fran
 * @Date: 2020/7/10
 * @Desc:
 **/
@Data
public class EasyExcelArticle {
    @ExcelProperty(value = "文章标题")
    private String title;
    @ExcelProperty(value = "文章内容")
    private String content;
    @ExcelProperty(value = "作者")
    private String author;
}
