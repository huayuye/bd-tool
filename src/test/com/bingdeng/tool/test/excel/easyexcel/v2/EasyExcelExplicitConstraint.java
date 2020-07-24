package com.bingdeng.tool.test.excel.easyexcel.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Fran
 * @Date: 2020/7/23
 * @Desc:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EasyExcelExplicitConstraint {
    /**
     * 下拉内容放于第几列
     * @return
     */
    int column() default 0;

    /**
     * 多级联动,默认1 无联动
     * @return
     */
    int levelTandem() default 1;

    /**
     * 下拉内容始于第几行有效
     * @return
     */
    int startRow() default 1;

    /**
     * 下拉内容最大有效行数
     * @return
     */
    int maxRows() default 10000;

    /**
     * 下拉内容
     * @return
     */
    String[] contents() default{};

    /**
     * 支持动态查询
     * @return
     */
    Class[] contentClass() default{};
}
